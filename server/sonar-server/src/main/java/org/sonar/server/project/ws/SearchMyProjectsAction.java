/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.project.ws;

import java.util.List;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.server.ws.Change;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentLinkDto;
import org.sonar.db.component.ComponentQuery;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.measure.MeasureDto;
import org.sonar.db.measure.MeasureQuery;
import org.sonar.db.metric.MetricDto;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.Projects.SearchMyProjectsWsResponse;
import org.sonarqube.ws.Projects.SearchMyProjectsWsResponse.Link;
import org.sonarqube.ws.Projects.SearchMyProjectsWsResponse.Project;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static org.sonar.api.utils.Paging.offset;
import static org.sonar.core.util.Protobuf.setNullable;
import static org.sonar.server.project.ws.SearchMyProjectsData.builder;
import static org.sonar.server.ws.WsUtils.writeProtobuf;

public class SearchMyProjectsAction implements ProjectsWsAction {
  private static final int MAX_SIZE = 500;

  private final DbClient dbClient;
  private final UserSession userSession;

  public SearchMyProjectsAction(DbClient dbClient, UserSession userSession) {
    this.dbClient = dbClient;
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("search_my_projects")
      .setDescription("Return list of projects for which the current user has 'Administer' permission.")
      .setResponseExample(getClass().getResource("search_my_projects-example.json"))
      .addPagingParams(100, MAX_SIZE)
      .setSince("6.0")
      .setInternal(true)
      .setHandler(this);

    action.setChangelog(new Change("6.4", "The 'id' field is deprecated in the response"));
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    SearchMyProjectsWsResponse searchMyProjectsWsResponse = doHandle(toRequest(request));
    writeProtobuf(searchMyProjectsWsResponse, request, response);
  }

  private SearchMyProjectsWsResponse doHandle(SearchMyProjectsRequest request) {
    checkAuthenticated();

    try (DbSession dbSession = dbClient.openSession(false)) {
      SearchMyProjectsData data = load(dbSession, request);
      return buildResponse(request, data);
    }
  }

  private static SearchMyProjectsWsResponse buildResponse(SearchMyProjectsRequest request, SearchMyProjectsData data) {
    SearchMyProjectsWsResponse.Builder response = SearchMyProjectsWsResponse.newBuilder();

    ProjectDtoToWs projectDtoToWs = new ProjectDtoToWs(data);
    data.projects().stream()
      .map(projectDtoToWs)
      .forEach(response::addProjects);

    response.getPagingBuilder()
      .setPageIndex(request.getPage())
      .setPageSize(request.getPageSize())
      .setTotal(data.totalNbOfProjects())
      .build();

    return response.build();
  }

  private void checkAuthenticated() {
    userSession.checkLoggedIn();
  }

  private static SearchMyProjectsRequest toRequest(Request request) {
    return SearchMyProjectsRequest.builder()
      .setPage(request.mandatoryParamAsInt(Param.PAGE))
      .setPageSize(request.mandatoryParamAsInt(Param.PAGE_SIZE))
      .build();
  }

  private static class ProjectDtoToWs implements Function<ComponentDto, Project> {
    private final SearchMyProjectsData data;

    private ProjectDtoToWs(SearchMyProjectsData data) {
      this.data = data;
    }

    @Override
    public Project apply(ComponentDto dto) {
      Project.Builder project = Project.newBuilder();
      project
        .setId(dto.uuid())
        .setKey(dto.getDbKey())
        .setName(dto.name());
      data.lastAnalysisDateFor(dto.uuid()).ifPresent(project::setLastAnalysisDate);
      data.qualityGateStatusFor(dto.uuid()).ifPresent(project::setQualityGate);
      setNullable(emptyToNull(dto.description()), project::setDescription);

      data.projectLinksFor(dto.uuid()).stream()
        .map(ProjectLinkDtoToWs.INSTANCE)
        .forEach(project::addLinks);

      return project.build();
    }
  }

  private enum ProjectLinkDtoToWs implements Function<ComponentLinkDto, Link> {
    INSTANCE;

    @Override
    public Link apply(ComponentLinkDto dto) {
      Link.Builder link = Link.newBuilder();
      link.setHref(dto.getHref());

      if (!isNullOrEmpty(dto.getName())) {
        link.setName(dto.getName());
      }
      if (!isNullOrEmpty(dto.getType())) {
        link.setType(dto.getType());
      }

      return link.build();
    }
  }

  SearchMyProjectsData load(DbSession dbSession, SearchMyProjectsRequest request) {
    SearchMyProjectsData.Builder data = builder();
    ProjectsResult searchResult = searchProjects(dbSession, request);
    List<ComponentDto> projects = searchResult.projects;
    List<String> projectUuids = Lists.transform(projects, ComponentDto::projectUuid);
    List<ComponentLinkDto> projectLinks = dbClient.componentLinkDao().selectByComponentUuids(dbSession, projectUuids);
    List<SnapshotDto> snapshots = dbClient.snapshotDao().selectLastAnalysesByRootComponentUuids(dbSession, projectUuids);
    MetricDto gateStatusMetric = dbClient.metricDao().selectOrFailByKey(dbSession, CoreMetrics.ALERT_STATUS_KEY);
    MeasureQuery measureQuery = MeasureQuery.builder()
            .setProjectUuids(projectUuids)
            .setMetricId(gateStatusMetric.getId())
            .build();
    List<MeasureDto> qualityGates = dbClient.measureDao().selectByQuery(dbSession, measureQuery);

    data.setProjects(projects)
            .setProjectLinks(projectLinks)
            .setSnapshots(snapshots)
            .setQualityGates(qualityGates)
            .setTotalNbOfProjects(searchResult.total);

    return data.build();
  }

  @VisibleForTesting
  ProjectsResult searchProjects(DbSession dbSession, SearchMyProjectsRequest request) {
    int userId = requireNonNull(userSession.getUserId(), "Current user must be authenticated");

    List<Long> componentIds = dbClient.roleDao().selectComponentIdsByPermissionAndUserId(dbSession, UserRole.ADMIN, userId);
    ComponentQuery dbQuery = ComponentQuery.builder()
            .setQualifiers(Qualifiers.PROJECT)
            .setComponentIds(ImmutableSet.copyOf(componentIds))
            .build();

    return new ProjectsResult(
            dbClient.componentDao().selectByQuery(dbSession, dbQuery, offset(request.getPage(), request.getPageSize()), request.getPageSize()),
            dbClient.componentDao().countByQuery(dbSession, dbQuery));
  }

  private static class ProjectsResult {
    private final List<ComponentDto> projects;
    private final int total;

    private ProjectsResult(List<ComponentDto> projects, int total) {
      this.projects = projects;
      this.total = total;
    }
  }

  private static class SearchMyProjectsRequest {
    private final Integer page;
    private final Integer pageSize;

    private SearchMyProjectsRequest(Builder builder) {
      this.page = builder.page;
      this.pageSize = builder.pageSize;
    }

    @CheckForNull
    public Integer getPage() {
      return page;
    }

    @CheckForNull
    public Integer getPageSize() {
      return pageSize;
    }

    public static Builder builder() {
      return new Builder();
    }
  }

  private static class Builder {
    private Integer page;
    private Integer pageSize;

    private Builder() {
      // enforce method constructor
    }

    public Builder setPage(@Nullable Integer page) {
      this.page = page;
      return this;
    }

    public Builder setPageSize(@Nullable Integer pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public SearchMyProjectsRequest build() {
      return new SearchMyProjectsRequest(this);
    }
  }
}

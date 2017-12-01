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
package org.sonarqube.ws.client.component;

import java.util.List;
import org.sonarqube.ws.Components.SearchProjectsWsResponse;
import org.sonarqube.ws.Components.ShowWsResponse;
import org.sonarqube.ws.client.BaseService;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.WsConnector;

import static org.sonar.api.server.ws.WebService.Param;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.ACTION_SEARCH_PROJECTS;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.ACTION_SHOW;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.CONTROLLER_COMPONENTS;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_BRANCH;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_COMPONENT;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_COMPONENT_ID;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_FILTER;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_ORGANIZATION;

/**
 * @deprecated since 7.0, use {@link org.sonarqube.ws.client.components.ComponentsService} instead
 */
@Deprecated
public class ComponentsService extends BaseService {

  public ComponentsService(WsConnector wsConnector) {
    super(wsConnector, CONTROLLER_COMPONENTS);
  }

  public ShowWsResponse show(ShowRequest request) {
    GetRequest get = new GetRequest(path(ACTION_SHOW))
      .setParam(PARAM_COMPONENT_ID, request.getId())
      .setParam(PARAM_COMPONENT, request.getKey())
      .setParam(PARAM_BRANCH, request.getBranch());
    return call(get, ShowWsResponse.parser());
  }

  public SearchProjectsWsResponse searchProjects(SearchProjectsRequest request) {
    List<String> additionalFields = request.getAdditionalFields();
    List<String> facets = request.getFacets();
    GetRequest get = new GetRequest(path(ACTION_SEARCH_PROJECTS))
      .setParam(PARAM_ORGANIZATION, request.getOrganization())
      .setParam(PARAM_FILTER, request.getFilter())
      .setParam(Param.FACETS, !facets.isEmpty() ? inlineMultipleParamValue(facets) : null)
      .setParam(Param.SORT, request.getSort())
      .setParam(Param.ASCENDING, request.getAsc())
      .setParam(Param.PAGE, request.getPage())
      .setParam(Param.PAGE_SIZE, request.getPageSize())
      .setParam(Param.FIELDS, !additionalFields.isEmpty() ? inlineMultipleParamValue(additionalFields) : null);
    return call(get, SearchProjectsWsResponse.parser());
  }
}

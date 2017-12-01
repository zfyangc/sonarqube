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

import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.ws.Components;
import org.sonarqube.ws.client.ServiceTester;
import org.sonarqube.ws.client.WsConnector;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.sonar.api.server.ws.WebService.Param.ASCENDING;
import static org.sonar.api.server.ws.WebService.Param.FACETS;
import static org.sonar.api.server.ws.WebService.Param.FIELDS;
import static org.sonar.api.server.ws.WebService.Param.PAGE;
import static org.sonar.api.server.ws.WebService.Param.PAGE_SIZE;
import static org.sonar.api.server.ws.WebService.Param.SORT;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_FILTER;

public class ComponentsServiceTest {

  @Rule
  public ServiceTester<ComponentsService> serviceTester = new ServiceTester<>(new ComponentsService(mock(WsConnector.class)));

  private ComponentsService underTest = serviceTester.getInstanceUnderTest();

  @Test
  public void search_projects() {
    underTest.searchProjects(SearchProjectsRequest.builder()
      .setFilter("ncloc > 10")
      .setFacets(asList("ncloc", "duplicated_lines_density"))
      .setSort("coverage")
      .setAsc(true)
      .setPage(3)
      .setPageSize(10)
      .setAdditionalFields(singletonList("analysisDate"))
      .build());

    assertThat(serviceTester.getGetParser()).isSameAs(Components.SearchProjectsWsResponse.parser());
    serviceTester.assertThat(serviceTester.getGetRequest())
      .hasPath("search_projects")
      .hasParam(PARAM_FILTER, "ncloc > 10")
      .hasParam(FACETS, "ncloc,duplicated_lines_density")
      .hasParam(SORT, "coverage")
      .hasParam(ASCENDING, true)
      .hasParam(PAGE, 3)
      .hasParam(PAGE_SIZE, 10)
      .hasParam(FIELDS, "analysisDate")
      .andNoOtherParam();
  }

  @Test
  public void search_projects_without_sort() {
    underTest.searchProjects(SearchProjectsRequest.builder()
      .setFilter("ncloc > 10")
      .setFacets(singletonList("ncloc"))
      .setPage(3)
      .setPageSize(10)
      .build());

    assertThat(serviceTester.getGetParser()).isSameAs(Components.SearchProjectsWsResponse.parser());
    serviceTester.assertThat(serviceTester.getGetRequest())
      .hasPath("search_projects")
      .hasParam(PARAM_FILTER, "ncloc > 10")
      .hasParam(FACETS, singletonList("ncloc"))
      .hasParam(PAGE, 3)
      .hasParam(PAGE_SIZE, 10)
      .andNoOtherParam();
  }

  @Test
  public void show() {
    String key = randomAlphanumeric(20);
    String id = randomAlphanumeric(20);
    underTest.show(new ShowRequest()
      .setKey(key)
      .setId(id)
      .setBranch("my_branch"));

    assertThat(serviceTester.getGetParser()).isSameAs(Components.ShowWsResponse.parser());
    serviceTester.assertThat(serviceTester.getGetRequest())
      .hasPath("show")
      .hasParam("component", key)
      .hasParam("componentId", id)
      .hasParam("branch", "my_branch")
      .andNoOtherParam();
  }

}

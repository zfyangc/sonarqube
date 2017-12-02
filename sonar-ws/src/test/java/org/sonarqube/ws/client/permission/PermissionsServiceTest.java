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
package org.sonarqube.ws.client.permission;

import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.ws.Permissions;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.PostRequest;
import org.sonarqube.ws.client.ServiceTester;
import org.sonarqube.ws.client.WsConnector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_DESCRIPTION;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_GROUP_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_GROUP_NAME;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_NAME;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_ORGANIZATION;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_PERMISSION;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_PROJECT_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_PROJECT_KEY;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_PROJECT_KEY_PATTERN;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_QUALIFIER;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_NAME;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_USER_LOGIN;
import static org.sonarqube.ws.client.project.ProjectsWsParameters.PARAM_ANALYZED_BEFORE;
import static org.sonarqube.ws.client.project.ProjectsWsParameters.PARAM_ON_PROVISIONED_ONLY;
import static org.sonarqube.ws.client.project.ProjectsWsParameters.PARAM_PROJECTS;
import static org.sonarqube.ws.client.project.ProjectsWsParameters.PARAM_QUALIFIERS;
import static org.sonarqube.ws.client.project.ProjectsWsParameters.PARAM_VISIBILITY;

public class PermissionsServiceTest {
  private static final String ORGANIZATION_VALUE = "organization value";
  private static final String PERMISSION_VALUE = "permission value";
  private static final String PROJECT_ID_VALUE = "project id value";
  private static final String PROJECT_KEY_VALUE = "project key value";
  private static final String QUERY_VALUE = "query value";
  private static final int PAGE_VALUE = 66;
  private static final int PAGE_SIZE_VALUE = 99;
  private static final String GROUP_ID_VALUE = "group id value";
  private static final String GROUP_NAME_VALUE = "group name value";
  private static final String TEMPLATE_ID_VALUE = "template id value";
  private static final String TEMPLATE_NAME_VALUE = "template name value";
  private static final String LOGIN_VALUE = "login value";
  private static final String NAME_VALUE = "name value";
  private static final String DESCRIPTION_VALUE = "description value";
  private static final String PROJECT_KEY_PATTERN_VALUE = "project key pattern value";
  private static final String QUALIFIER_VALUE = "qualifier value";
  private static final String PARAM_Q = "q";
  private static final String PARAM_PS = "ps";
  private static final String PARAM_P = "p";

  @Rule
  public ServiceTester<PermissionsService> serviceTester = new ServiceTester<>(new PermissionsService(mock(WsConnector.class)));

  private PermissionsService underTest = serviceTester.getInstanceUnderTest();

  @Test
  public void users() {
    underTest.users(new UsersRequest()
      .setOrganization("org")
      .setProjectKey("project")
      .setProjectId("ABCD")
      .setPermission("user")
      .setQuery("query")
      .setPage(10)
      .setPageSize(50)
    );

    assertThat(serviceTester.getGetParser()).isSameAs(Permissions.UsersWsResponse.parser());
    GetRequest getRequest = serviceTester.getGetRequest();

    serviceTester.assertThat(getRequest)
      .hasPath("users")
      .hasParam("organization", "org")
      .hasParam("projectKey", "project")
      .hasParam("projectId", "ABCD")
      .hasParam("permission", "user")
      .hasParam("q", "query")
      .hasParam("p", "10")
      .hasParam("ps", "50")
      .andNoOtherParam();
  }
}

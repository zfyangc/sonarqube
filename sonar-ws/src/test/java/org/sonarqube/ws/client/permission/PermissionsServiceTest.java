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
  public void applyTemplate_does_POST_on_Ws_apply_template() {
    underTest.applyTemplate(new ApplyTemplateRequest()
      .setOrganization(ORGANIZATION_VALUE)
      .setProjectId(PROJECT_ID_VALUE)
      .setProjectKey(PROJECT_KEY_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("apply_template")
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .hasParam(PARAM_PROJECT_ID, PROJECT_ID_VALUE)
      .hasParam(PARAM_PROJECT_KEY, PROJECT_KEY_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void bulk_apply_template() {
    underTest.bulkApplyTemplate(new BulkApplyTemplateRequest()
      .setOrganization(ORGANIZATION_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setQualifiers(Arrays.asList("TRK", "VW"))
      .setQuery(QUERY_VALUE)
      .setVisibility("private")
      .setAnalyzedBefore("2017-04-01")
      .setOnProvisionedOnly(true)
      .setProjects(Arrays.asList("P1", "P2")));

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("bulk_apply_template")
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam("q", QUERY_VALUE)
      .hasParam(PARAM_QUALIFIERS, "TRK,VW")
      .hasParam(PARAM_VISIBILITY, "private")
      .hasParam(PARAM_ANALYZED_BEFORE, "2017-04-01")
      .hasParam(PARAM_ON_PROVISIONED_ONLY, "true")
      .hasParam(PARAM_PROJECTS, "P1,P2")
      .andNoOtherParam();
  }

  @Test
  public void createTemplate_does_POST_on_Ws_create_template() {
    underTest.createTemplate(new CreateTemplateRequest()
      .setOrganization(ORGANIZATION_VALUE)
      .setName(NAME_VALUE)
      .setDescription(DESCRIPTION_VALUE)
      .setProjectKeyPattern(PROJECT_KEY_PATTERN_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isSameAs(Permissions.CreateTemplateWsResponse.parser());
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("create_template")
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .hasParam(PARAM_NAME, NAME_VALUE)
      .hasParam(PARAM_DESCRIPTION, DESCRIPTION_VALUE)
      .hasParam(PARAM_PROJECT_KEY_PATTERN, PROJECT_KEY_PATTERN_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void deleteTemplate_does_POST_on_Ws_delete_template() {
    underTest.deleteTemplate(new DeleteTemplateRequest()
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("delete_template")
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void removeGroup_does_POST_on_Ws_remove_group() {
    underTest.removeGroup(new RemoveGroupRequest()
      .setPermission(PERMISSION_VALUE)
      .setGroupId(GROUP_ID_VALUE)
      .setGroupName(GROUP_NAME_VALUE)
      .setProjectId(PROJECT_ID_VALUE)
      .setProjectKey(PROJECT_KEY_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("remove_group")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_GROUP_ID, GROUP_ID_VALUE)
      .hasParam(PARAM_GROUP_NAME, GROUP_NAME_VALUE)
      .hasParam(PARAM_PROJECT_ID, PROJECT_ID_VALUE)
      .hasParam(PARAM_PROJECT_KEY, PROJECT_KEY_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void removeGroupFromTemplate_does_POST_on_Ws_remove_group_from_template() {
    underTest.removeGroupFromTemplate(new RemoveGroupFromTemplateRequest()
      .setPermission(PERMISSION_VALUE)
      .setGroupId(GROUP_ID_VALUE)
      .setGroupName(GROUP_NAME_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("remove_group_from_template")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_GROUP_ID, GROUP_ID_VALUE)
      .hasParam(PARAM_GROUP_NAME, GROUP_NAME_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void removeUser_does_POST_on_Ws_remove_user() {
    underTest.removeUser(new RemoveUserRequest()
      .setPermission(PERMISSION_VALUE)
      .setLogin(LOGIN_VALUE)
      .setProjectId(PROJECT_ID_VALUE)
      .setProjectKey(PROJECT_KEY_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("remove_user")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_USER_LOGIN, LOGIN_VALUE)
      .hasParam(PARAM_PROJECT_ID, PROJECT_ID_VALUE)
      .hasParam(PARAM_PROJECT_KEY, PROJECT_KEY_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void removeUserFromTemplate_does_POST_on_Ws_remove_user_from_template() {
    underTest.removeUserFromTemplate(new RemoveUserFromTemplateRequest()
      .setPermission(PERMISSION_VALUE)
      .setLogin(LOGIN_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("remove_user_from_template")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_USER_LOGIN, LOGIN_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void searchGlobalPermissions_does_GET_on_Ws_search_global_permissions() {
    underTest.searchGlobalPermissions();

    assertThat(serviceTester.getGetParser()).isSameAs(Permissions.WsSearchGlobalPermissionsResponse.parser());
    GetRequest getRequest = serviceTester.getGetRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("search_global_permissions")
      .andNoOtherParam();
  }

  @Test
  public void searchProjectPermissions_does_GET_on_Ws_search_project_permissions() {
    underTest.searchProjectPermissions(new SearchProjectPermissionsRequest()
      .setProjectId(PROJECT_ID_VALUE)
      .setProjectKey(PROJECT_KEY_VALUE)
      .setQualifier(QUALIFIER_VALUE)
      .setPage(PAGE_VALUE)
      .setPageSize(PAGE_SIZE_VALUE)
      .setQuery(QUERY_VALUE)
    );

    assertThat(serviceTester.getGetParser()).isSameAs(Permissions.SearchProjectPermissionsWsResponse.parser());
    GetRequest getRequest = serviceTester.getGetRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("search_project_permissions")
      .hasParam(PARAM_PROJECT_ID, PROJECT_ID_VALUE)
      .hasParam(PARAM_PROJECT_KEY, PROJECT_KEY_VALUE)
      .hasParam(PARAM_QUALIFIER, QUALIFIER_VALUE)
      .hasParam(PARAM_P, PAGE_VALUE)
      .hasParam(PARAM_PS, PAGE_SIZE_VALUE)
      .hasParam(PARAM_Q, QUERY_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void searchTemplates_does_GET_on_Ws_search_templates() {
    underTest.searchTemplates(new SearchTemplatesRequest()
      .setQuery(QUERY_VALUE)
    );

    assertThat(serviceTester.getGetParser()).isSameAs(Permissions.SearchTemplatesWsResponse.parser());
    GetRequest getRequest = serviceTester.getGetRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("search_templates")
      .hasParam(PARAM_Q, QUERY_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void setDefaultTemplate_does_POST_on_Ws_set_default_template() {
    underTest.setDefaultTemplate(new SetDefaultTemplateRequest()
      .setQualifier(QUALIFIER_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("set_default_template")
      .hasParam(PARAM_QUALIFIER, QUALIFIER_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void updateTemplate_does_POST_on_Ws_update_template() {
    underTest.updateTemplate(new UpdateTemplateRequest()
      .setDescription(DESCRIPTION_VALUE)
      .setId(TEMPLATE_ID_VALUE)
      .setName(TEMPLATE_NAME_VALUE)
      .setProjectKeyPattern(PROJECT_KEY_PATTERN_VALUE)
    );

    assertThat(serviceTester.getPostParser()).isSameAs(Permissions.UpdateTemplateWsResponse.parser());
    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("update_template")
      .hasParam(PARAM_DESCRIPTION, DESCRIPTION_VALUE)
      .hasParam(PARAM_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_PROJECT_KEY_PATTERN, PROJECT_KEY_PATTERN_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void add_project_creator_to_template() {
    underTest.addProjectCreatorToTemplate(AddProjectCreatorToTemplateRequest.builder()
      .setPermission(PERMISSION_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
      .build());

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest getRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("add_project_creator_to_template")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

  @Test
  public void remove_project_creator_from_template() {
    underTest.removeProjectCreatorFromTemplate(RemoveProjectCreatorFromTemplateRequest.builder()
      .setPermission(PERMISSION_VALUE)
      .setTemplateId(TEMPLATE_ID_VALUE)
      .setTemplateName(TEMPLATE_NAME_VALUE)
      .setOrganization(ORGANIZATION_VALUE)
      .build());

    assertThat(serviceTester.getPostParser()).isNull();
    PostRequest getRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("remove_project_creator_from_template")
      .hasParam(PARAM_PERMISSION, PERMISSION_VALUE)
      .hasParam(PARAM_TEMPLATE_ID, TEMPLATE_ID_VALUE)
      .hasParam(PARAM_TEMPLATE_NAME, TEMPLATE_NAME_VALUE)
      .hasParam(PARAM_ORGANIZATION, ORGANIZATION_VALUE)
      .andNoOtherParam();
  }

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

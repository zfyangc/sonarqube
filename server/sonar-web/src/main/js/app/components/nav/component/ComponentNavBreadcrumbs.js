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
import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import QualifierIcon from '../../../../components/shared/QualifierIcon';
import { getOrganizationByKey, areThereCustomOrganizations } from '../../../../store/rootReducer';
import OrganizationAvatar from '../../../../components/common/OrganizationAvatar';
import OrganizationHelmet from '../../../../components/common/OrganizationHelmet';
import OrganizationLink from '../../../../components/ui/OrganizationLink';
import PrivateBadge from '../../../../components/common/PrivateBadge';
import { collapsePath, limitComponentName } from '../../../../helpers/path';
import { getProjectUrl } from '../../../../helpers/urls';

class ComponentNavBreadcrumbs extends React.PureComponent {
  static propTypes = {
    breadcrumbs: PropTypes.array,
    component: PropTypes.shape({
      visibility: PropTypes.string
    }).isRequired
  };

  render() {
    const { breadcrumbs, component, organization, shouldOrganizationBeDisplayed } = this.props;

    if (!breadcrumbs) {
      return null;
    }

    const displayOrganization = organization != null && shouldOrganizationBeDisplayed;

    const lastItem = breadcrumbs[breadcrumbs.length - 1];

    const items = breadcrumbs.map((item, index) => {
      const isPath = item.qualifier === 'DIR';
      const itemName = isPath ? collapsePath(item.name, 15) : limitComponentName(item.name);
      return (
        <span key={item.key}>
          {index === 0 && (
            <span className="navbar-context-title-qualifier spacer-right">
              <QualifierIcon qualifier={lastItem.qualifier} />
            </span>
          )}
          <Link
            className="link-base-color link-no-underline"
            title={item.name}
            to={getProjectUrl(item.key)}>
            {itemName}
          </Link>
          {index < breadcrumbs.length - 1 && <span className="slash-separator" />}
        </span>
      );
    });

    return (
      <h1 className="navbar-context-header">
        <OrganizationHelmet
          title={component.name}
          organization={displayOrganization ? organization : null}
        />
        {displayOrganization && (
          <span>
            <OrganizationAvatar organization={organization} />
            <OrganizationLink
              organization={organization}
              className="link-base-color link-no-underline">
              {organization.name}
            </OrganizationLink>
            <span className="slash-separator" />
          </span>
        )}
        {items}
        {component.visibility === 'private' && (
          <PrivateBadge className="spacer-left" qualifier={component.qualifier} />
        )}
      </h1>
    );
  }
}

const mapStateToProps = (state, ownProps) => ({
  organization:
    ownProps.component.organization && getOrganizationByKey(state, ownProps.component.organization),
  shouldOrganizationBeDisplayed: areThereCustomOrganizations(state)
});

export default connect(mapStateToProps)(ComponentNavBreadcrumbs);

export const Unconnected = ComponentNavBreadcrumbs;

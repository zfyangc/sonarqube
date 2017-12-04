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
import * as React from 'react';
import CreateQualityGateForm from '../components/CreateQualityGateForm';
import { QualityGate } from '../../../api/quality-gates';
import { translate } from '../../../helpers/l10n';
import { Organization } from '../../../app/types';

interface Props {
  canCreate: boolean;
  onAdd: (qualityGate: QualityGate) => void;
  organization?: Organization;
}

interface State {
  createQualityGateOpen: boolean;
}

export default class ListHeader extends React.PureComponent<Props, State> {
  state = { createQualityGateOpen: false };

  openCreateQualityGateForm = () => this.setState({ createQualityGateOpen: true });
  closeCreateQualityGateForm = () => this.setState({ createQualityGateOpen: false });

  render() {
    const { organization } = this.props;

    return (
      <header className="page-header">
        <h1 className="page-title">{translate('quality_gates.page')}</h1>
        {this.props.canCreate && (
          <div className="page-actions">
            <button id="quality-gate-add" onClick={this.openCreateQualityGateForm}>
              {translate('create')}
            </button>
          </div>
        )}
        {this.state.createQualityGateOpen && (
          <CreateQualityGateForm
            onClose={this.closeCreateQualityGateForm}
            onCreate={this.props.onAdd}
            organization={organization && organization.key}
          />
        )}
      </header>
    );
  }
}

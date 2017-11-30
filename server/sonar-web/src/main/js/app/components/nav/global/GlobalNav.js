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
// @flow
import React from 'react';
import { connect } from 'react-redux';
import GlobalNavBranding from './GlobalNavBranding';
import GlobalNavMenu from './GlobalNavMenu';
import GlobalNavExplore from './GlobalNavExplore';
import GlobalNavUserContainer from './GlobalNavUserContainer';
import Search from '../../search/Search';
import GlobalHelp from '../../help/GlobalHelp';
import * as theme from '../../../theme';
import NavBar from '../../../../components/nav/NavBar';
import Tooltip from '../../../../components/controls/Tooltip';
import HelpIcon from '../../../../components/icons-components/HelpIcon';
import OnboardingModal from '../../../../apps/tutorials/onboarding/OnboardingModal';
import { getCurrentUser, getAppState, getGlobalSettingValue } from '../../../../store/rootReducer';
import { skipOnboarding } from '../../../../store/users/actions';
import { translate } from '../../../../helpers/l10n';
import './GlobalNav.css';

/*::
type Props = {
  appState: { organizationsEnabled: boolean },
  currentUser: { isLoggedIn: boolean, showOnboardingTutorial: boolean },
  location: { pathname: string },
  skipOnboarding: () => void,
  onSonarCloud: boolean
};
*/

/*::
type State = {
  helpOpen: boolean,
  onboardingTutorialOpen: boolean,
  onboardingTutorialTooltip: boolean
};
*/

class GlobalNav extends React.PureComponent {
  /*:: interval: ?number; */
  /*:: props: Props; */
  state /*: State */ = {
    helpOpen: false,
    onboardingTutorialOpen: false,
    onboardingTutorialTooltip: false
  };

  componentDidMount() {
    window.addEventListener('keypress', this.onKeyPress);
    if (this.props.currentUser.showOnboardingTutorial) {
      this.openOnboardingTutorial();
    }
  }

  componentWillUnmount() {
    if (this.interval) {
      clearInterval(this.interval);
    }
    window.removeEventListener('keypress', this.onKeyPress);
  }

  onKeyPress = e => {
    const tagName = e.target.tagName;
    const code = e.keyCode || e.which;
    const isInput = tagName === 'INPUT' || tagName === 'SELECT' || tagName === 'TEXTAREA';
    const isTriggerKey = code === 63;
    if (!isInput && isTriggerKey) {
      this.openHelp();
    }
  };

  handleHelpClick = event => {
    event.preventDefault();
    this.openHelp();
  };

  openHelp = () => this.setState({ helpOpen: true });

  closeHelp = () => this.setState({ helpOpen: false });

  openOnboardingTutorial = () => this.setState({ helpOpen: false, onboardingTutorialOpen: true });

  closeOnboardingTutorial = () => {
    this.setState({ onboardingTutorialOpen: false, onboardingTutorialTooltip: true });
    this.props.skipOnboarding();
    this.interval = setInterval(() => {
      this.setState({ onboardingTutorialTooltip: false });
    }, 3000);
  };

  render() {
    return (
      <NavBar className="navbar-global" id="global-navigation" height={theme.globalNavHeightRaw}>
        <GlobalNavBranding />

        <GlobalNavMenu {...this.props} />

        <ul className="global-navbar-menu pull-right">
          <GlobalNavExplore location={this.props.location} onSonarCloud={this.props.onSonarCloud} />
          <li>
            <a className="navbar-help" onClick={this.handleHelpClick} href="#">
              {this.state.onboardingTutorialTooltip ? (
                <Tooltip
                  defaultVisible={true}
                  overlay={translate('tutorials.follow_later')}
                  trigger="manual">
                  <HelpIcon />
                </Tooltip>
              ) : (
                <HelpIcon />
              )}
            </a>
          </li>
          <Search appState={this.props.appState} currentUser={this.props.currentUser} />
          <GlobalNavUserContainer {...this.props} />
        </ul>

        {this.state.helpOpen && (
          <GlobalHelp
            currentUser={this.props.currentUser}
            onClose={this.closeHelp}
            onTutorialSelect={this.openOnboardingTutorial}
            onSonarCloud={this.props.onSonarCloud}
          />
        )}

        {this.state.onboardingTutorialOpen && (
          <OnboardingModal onFinish={this.closeOnboardingTutorial} />
        )}
      </NavBar>
    );
  }
}

const mapStateToProps = state => {
  const sonarCloudSetting = getGlobalSettingValue(state, 'sonar.sonarcloud.enabled');

  return {
    currentUser: getCurrentUser(state),
    appState: getAppState(state),
    onSonarCloud: Boolean(sonarCloudSetting && sonarCloudSetting.value === 'true')
  };
};

const mapDispatchToProps = { skipOnboarding };

export default connect(mapStateToProps, mapDispatchToProps)(GlobalNav);

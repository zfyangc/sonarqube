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
import PageHeader from './PageHeader';
import Evolution from './Evolution';
import ProfilesList from './ProfilesList';
import { Profile } from '../types';
import { Actions } from '../../../api/quality-profiles';

interface Props {
  actions: Actions;
  languages: Array<{ key: string; name: string }>;
  location: { query: { [p: string]: string } };
  onRequestFail: (reason: any) => void;
  organization: string | null;
  profiles: Array<Profile>;
  updateProfiles: () => Promise<void>;
}

export default function HomeContainer(props: Props) {
  return (
    <div>
      <PageHeader {...props} />

      <div className="page-with-sidebar">
        <div className="page-main">
          <ProfilesList {...props} />
        </div>
        <div className="page-sidebar">
          <Evolution {...props} />
        </div>
      </div>
    </div>
  );
}

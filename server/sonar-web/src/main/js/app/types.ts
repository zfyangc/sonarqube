/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
export enum BranchType {
  LONG = 'LONG',
  SHORT = 'SHORT'
}

export interface MainBranch {
  analysisDate?: string;
  isMain: true;
  name: string;
  status?: {
    qualityGateStatus: string;
  };
}

export interface LongLivingBranch {
  analysisDate?: string;
  isMain: false;
  name: string;
  status?: {
    qualityGateStatus: string;
  };
  type: BranchType.LONG;
}

export interface ShortLivingBranch {
  analysisDate?: string;
  isMain: false;
  isOrphan?: true;
  mergeBranch: string;
  name: string;
  status?: {
    bugs: number;
    codeSmells: number;
    vulnerabilities: number;
  };
  type: BranchType.SHORT;
}

export type Branch = MainBranch | LongLivingBranch | ShortLivingBranch;

export interface Extension {
  key: string;
  name: string;
}

export interface Component {
  analysisDate?: string;
  breadcrumbs: Array<{
    key: string;
    name: string;
    qualifier: string;
  }>;
  configuration?: ComponentConfiguration;
  description?: string;
  extensions?: Extension[];
  isFavorite?: boolean;
  key: string;
  name: string;
  organization: string;
  path?: string;
  qualifier: string;
  refKey?: string;
  version?: string;
}

interface ComponentConfiguration {
  extensions?: Extension[];
  showBackgroundTasks?: boolean;
  showLinks?: boolean;
  showManualMeasures?: boolean;
  showQualityGates?: boolean;
  showQualityProfiles?: boolean;
  showPermissions?: boolean;
  showSettings?: boolean;
  showUpdateKey?: boolean;
}

export interface Metric {
  custom?: boolean;
  decimalScale?: number;
  description?: string;
  direction?: number;
  domain?: string;
  hidden?: boolean;
  key: string;
  name: string;
  qualitative?: boolean;
  type: string;
}

export interface Organization {
  adminPages?: { key: string; name: string }[];
  avatar?: string;
  canAdmin?: boolean;
  canDelete?: boolean;
  canProvisionProjects?: boolean;
  canUpdateProjectsVisibilityToPrivate?: boolean;
  description?: string;
  isDefault?: boolean;
  key: string;
  name: string;
  pages?: { key: string; name: string }[];
  projectVisibility: Visibility;
  url?: string;
}

export interface Paging {
  pageIndex: number;
  pageSize: number;
  total: number;
}

export enum Visibility {
  Public = 'public',
  Private = 'private'
}

export interface CurrentUser {
  isLoggedIn: boolean;
  showOnboardingTutorial?: boolean;
}

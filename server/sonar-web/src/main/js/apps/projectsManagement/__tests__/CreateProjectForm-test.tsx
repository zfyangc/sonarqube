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
/* eslint-disable import/first, import/order */
jest.mock('../../../api/components', () => ({
  createProject: jest.fn(({ name }: { name: string }) =>
    Promise.resolve({ project: { key: name, name } })
  )
}));

import * as React from 'react';
import { shallow } from 'enzyme';
import CreateProjectForm from '../CreateProjectForm';
import { change, submit, click } from '../../../helpers/testUtils';
import { Visibility } from '../../../app/types';

const createProject = require('../../../api/components').createProject as jest.Mock<any>;

const organization = { key: 'org', name: 'org', projectVisibility: Visibility.Public };

it('creates project', async () => {
  const wrapper = shallow(
    <CreateProjectForm
      onClose={jest.fn()}
      onProjectCreated={jest.fn()}
      organization={organization}
    />
  );
  (wrapper.instance() as CreateProjectForm).mounted = true;
  expect(wrapper).toMatchSnapshot();

  change(wrapper.find('input[name="name"]'), 'name', {
    currentTarget: { name: 'name', value: 'name' }
  });
  change(wrapper.find('input[name="key"]'), 'key', {
    currentTarget: { name: 'key', value: 'key' }
  });
  wrapper.find('VisibilitySelector').prop<Function>('onChange')('private');
  wrapper.update();
  expect(wrapper).toMatchSnapshot();

  submit(wrapper.find('form'));
  expect(createProject).toBeCalledWith({
    branch: '',
    name: 'name',
    organization: 'org',
    project: 'key',
    visibility: 'private'
  });
  expect(wrapper).toMatchSnapshot();

  await new Promise(setImmediate);
  wrapper.update();
  expect(wrapper).toMatchSnapshot();
});

it('shows more', () => {
  const wrapper = shallow(
    <CreateProjectForm
      onClose={jest.fn()}
      onProjectCreated={jest.fn()}
      organization={organization}
    />
  );
  expect(wrapper).toMatchSnapshot();

  click(wrapper.find('.js-more'));
  expect(wrapper).toMatchSnapshot();
});

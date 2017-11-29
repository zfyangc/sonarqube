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
package org.sonar.server.platform.db.migration.version.v70;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DataChange;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePersonMeasuresTest {
  private static final AtomicInteger GENERATOR = new AtomicInteger();
  @Rule
  public CoreDbTester db = CoreDbTester.createForSchema(DeletePersonMeasuresTest.class, "initial.sql");

  private DataChange underTest = new DeletePersonMeasures(db.database());

  @Test
  public void delete_measures_with_nonnull_person_id() throws Exception {
    insert(false);
    insert(true);
    insert(true);

    underTest.execute();

    assertThat(db.countSql("select count(id) from project_measures where person_id is null")).isEqualTo(1);
    assertThat(db.countSql("select count(id) from project_measures where person_id is not null")).isEqualTo(0);
  }

  @Test
  public void migration_is_reentrant() throws Exception {
    insert(false);
    insert(true);
    insert(true);

    underTest.execute();
    underTest.execute();

    assertThat(db.countSql("select count(id) from project_measures where person_id is null")).isEqualTo(1);
    assertThat(db.countSql("select count(id) from project_measures where person_id is not null")).isEqualTo(0);

  }

  private void insert(boolean hasPersonId) {
    Map<String, Object> measure = new HashMap<>();
    measure.put("COMPONENT_UUID", "" + GENERATOR.incrementAndGet());
    measure.put("ANALYSIS_UUID", "" + GENERATOR.incrementAndGet());
    measure.put("METRIC_ID", "123");
    measure.put("VALUE", "234");
    measure.put("PERSON_ID", hasPersonId ? "" + GENERATOR.incrementAndGet() : null);
    db.executeInsert("PROJECT_MEASURES", measure);
  }
}

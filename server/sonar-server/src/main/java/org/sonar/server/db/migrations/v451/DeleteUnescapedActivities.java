/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.db.migrations.v451;

import org.apache.commons.lang.StringUtils;
import org.sonar.core.persistence.Database;
import org.sonar.server.db.migrations.BaseDataChange;
import org.sonar.server.db.migrations.MassUpdate;
import org.sonar.server.db.migrations.Select;
import org.sonar.server.db.migrations.SqlStatement;

import java.sql.SQLException;

/**
 * See http://jira.codehaus.org/browse/SONAR-5758
 *
 * @since 4.5.1
 */
public class DeleteUnescapedActivities extends BaseDataChange {

  public DeleteUnescapedActivities(Database db) {
    super(db);
  }

  @Override
  public void execute(Context context) throws SQLException {
    MassUpdate massUpdate = context.prepareMassUpdate();
    massUpdate.select("select id,data_field from activities where log_type='QPROFILE'");
    massUpdate.update("delete activities where id=?");
    massUpdate.execute(new MassUpdate.Handler() {
      @Override
      public boolean handle(Select.Row row, SqlStatement update) throws SQLException {
        String csv = row.getString(2);
        if (isUnescaped(csv)) {
          update.setLong(1, row.getLong(1));
          return true;
        }
        return false;
      }
    });
  }

  static boolean isUnescaped(String csv) {
    String[] splits = csv.split(";");
    for (String split : splits) {
      if (StringUtils.countMatches(split, "=") != 1) {
        return true;
      }
    }
    return false;
  }
}

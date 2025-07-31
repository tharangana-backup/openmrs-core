/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package liquibase.ext.sqlgenerator.core;

import java.util.ArrayList;
import java.util.List;
import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.ChangeWithColumns;
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChange;
import liquibase.change.DatabaseChangeProperty;
import liquibase.database.Database;

	
	@Override
	public void setColumns(List<ColumnConfig> columns) {
		this.columns = columns;
	}
	
	public void addColumn(ColumnConfig column) {
		columns.add(column);
	}
	
	public void removeColumn(ColumnConfig column) {
		columns.remove(column);
	}
	
	public SqlStatement[] generateStatements(Database database) {
		ModifyColumnStatement statement = new ModifyColumnStatement(getSchemaName(), getTableName(),
			getColumns().toArray(new ColumnConfig[0]));

		log.debug( "modify column statement is '{}'", statement);

		return new SqlStatement[] { statement };
	}
	
	public String getConfirmationMessage() {
		List<String> names = new ArrayList<>(columns.size());
		for (ColumnConfig col : columns) {
			names.add(col.getName() + "(" + col.getType() + ")");
		}
		
		return "Columns " + StringUtil.join(names, ",") + " of " + getTableName() + " modified";
	}
}

/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.dateandtime.shared.dbunit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2Connection;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

public class DatabaseCapable {

	protected void verifyDatabaseTable(EntityManager manager, final String tableName) throws RuntimeDatabaseUnitException {

		Work work = new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {

				H2Connection dbunitConnection;
				IDataSet databaseDataSet;
				try {
					dbunitConnection = new H2Connection(
							connection, null);
					databaseDataSet = dbunitConnection.createDataSet();
				} catch (DatabaseUnitException ex) {
					throw new RuntimeException(ex);
				}

				ITable actualTable;
				try {
					actualTable = databaseDataSet.getTable(tableName);

					File placeholder = new File(DatabaseCapable.class
							.getResource("/expected/.dbunit-comparison-files")
							.getFile());
					File comparisonFile = new File(placeholder.getParentFile()
							.getPath()
							+ System.getProperty("file.separator")
							+ tableName + ".xml");

					//writeExpectedFile(dbunitConnection, comparisonFile, tableName);

					IDataSet expectedDataSet = new FlatXmlDataSetBuilder()
							.build(comparisonFile);
					ITable expectedTable = expectedDataSet.getTable(tableName);

					Assertion.assertEquals(expectedTable, actualTable);

				} catch (DatabaseUnitException ex) {
					throw new RuntimeDatabaseUnitException(ex);
				} catch (IOException ex) {
					throw new RuntimeDatabaseUnitException(ex);
				}
			}
		};
		
		((Session) (manager.getDelegate())).doWork(work);

	}

	protected void writeExpectedFile(IDatabaseConnection dbunitConnection,
			File outputFile, String tableName) throws IOException,
			DataSetException {
		QueryDataSet partialDataSet = new QueryDataSet(dbunitConnection);
		partialDataSet.addTable(tableName);
		// IDataSet export = work.getDatabaseDataSet(); // Full dataset

		FlatXmlDataSet.write(partialDataSet, new FileOutputStream(outputFile));
	}
}

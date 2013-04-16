/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: DbSchemaUtils
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2011, CRIXP Corp., Switzerland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.kernel.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.opencrx.kernel.tools.FastResultSet;
import org.openmdx.base.exception.ServiceException;

/**
 * DbSchemaUtils
 */
public class DbSchemaUtils {

	public static String getJdbcDriverName(
		String connectionUrl
	) {
		if(connectionUrl.startsWith("jdbc:postgresql:")) {
			return "org.postgresql.Driver";						
		} else if(connectionUrl.startsWith("jdbc:mysql:")) {
			return "com.mysql.jdbc.Driver";						
		} else if(connectionUrl.startsWith("jdbc:hsqldb:")) {
			return "org.hsqldb.jdbc.JDBCDriver";					
		} else if(connectionUrl.startsWith("jdbc:db2:")) {
			return "com.ibm.db2.jcc.DB2Driver";
		} else if(connectionUrl.startsWith("jdbc:as400:")) {
			return "com.ibm.as400.access.AS400JDBCDriver";
		} else if(connectionUrl.startsWith("jdbc:oracle:")) {
			return "oracle.jdbc.driver.OracleDriver";			
		} else if(connectionUrl.startsWith("jdbc:sqlserver:")) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else {
			return null;
		}
	}

	/**
	 * MigrationDefinition
	 *
	 */
	static class MigrationDefinition {

		public MigrationDefinition(
			String name,
			String testForReleaseStatement,
			List<String> migrateStatements
		) {
			this.name = name;
			this.testForVersionStatement = testForReleaseStatement;
			this.migrationStatements = migrateStatements;
		}
		
		public String getName() {
			return name;
		}
		
		public String getTestForVersionStatement() {
			return testForVersionStatement;
		}
		
		public List<String> getMigrationStatements() {
			return migrationStatements;
		}

		private final String name;
		private final String testForVersionStatement;
		private final List<String> migrationStatements;
	}

	/**
	 * Get connection to embedded database holding the reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	protected static Connection getSchemaConnection(
	) throws ServiceException {
		try {
			Properties properties = new Properties();
			// specifying the database in the URL does not work properly with HSQLDB 2.2.5
			properties.put("database", "org/opencrx/kernel/tools/resource/crx");
			properties.put("user", "SA");
			properties.put("password", "manager99");
			return DriverManager.getConnection("jdbc:hsqldb:res:", properties);	
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Get reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	protected static List<String> getSchema(
	) throws ServiceException {
		Connection connS = null;
		List<String> schema = new ArrayList<String>();
		try {
			connS = getSchemaConnection();
			PreparedStatement ps = connS.prepareStatement("script");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				schema.add(
					rs.getString("Command")
				);				
			}
			rs.close();
			ps.close();
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
		finally {
			if(connS != null) {
				try {
					connS.close();
				} catch(Exception e0) {}
			}
		}
		return schema;
	}
	
	/**
	 * Get definition for given db object from reference schema.
	 * 
	 * @param type
	 * @param object
	 * @param schema
	 * @param targetDatabaseName
	 * @param replaceObject
	 * @return
	 */
	protected static String getObjectDefinition(
		String type,
		String object,
		List<String> schema,
		String targetDatabaseName,
		boolean replaceObject
	) {
		for(String command: schema) {
			if(
				command.indexOf(type) >= 0 && 
				(command.indexOf(object + "(") > 0 || command.indexOf(object + " ") > 0)
			) {
				command = command.replace(CREATE_TABLE_PREFIX, "CREATE TABLE ");
				command = command.replace(CREATE_VIEW_PREFIX, "CREATE VIEW ");
				command = command.replace(" PUBLIC.", " ");
				command = command.replace(",PUBLIC.", ", ");
				// PostgreSQL
				if(targetDatabaseName.indexOf("PostgreSQL") >=0) {
					if(type.equals(CREATE_SEQUENCE_PREFIX)) {
						command = command.replace("AS INTEGER", "");
					} else {
						if(replaceObject) {
							command = command.replace("CREATE VIEW ", "CREATE OR REPLACE VIEW ");
						}
						command = mapColumnDefinition(targetDatabaseName, command);
					}
				}
				// HSQLDB
				else if(targetDatabaseName.indexOf("HSQL") >=0) {
					if(replaceObject) {
						command = command.replace("CREATE VIEW ", "ALTER VIEW ");
					}
				}
				// MySQL
				else if(targetDatabaseName.indexOf("MySQL") >=0) {
					if(replaceObject) {
						command = command.replace("CREATE VIEW ", "CREATE OR REPLACE VIEW ");
					}
					command = mapColumnDefinition(targetDatabaseName, command);
				}
				// DB2
				else if(targetDatabaseName.indexOf("DB2") >=0) {
					if(replaceObject) {
						// REPLACE not supported for UDB versions
						if(targetDatabaseName.indexOf("UDB") < 0) {
							command = command.replace("CREATE VIEW ", "CREATE OR REPLACE VIEW ");
						}
					}
					command = mapColumnDefinition(targetDatabaseName, command);
				}
				// Oracle
				else if(targetDatabaseName.indexOf("Oracle") >=0) {
					if(type.equals(CREATE_SEQUENCE_PREFIX)) {
						command = command.replace("AS INTEGER", "");
					} else {					
						if(replaceObject) {
							command = command.replace("CREATE VIEW ", "CREATE OR REPLACE VIEW ");
						}
						command = mapColumnDefinition(targetDatabaseName, command);
					}
				}
				// Microsoft
				else if(targetDatabaseName.indexOf("Microsoft") >=0) {
					if(replaceObject) {
						command = command.replace("CREATE VIEW ", "ALTER VIEW ");
					}
					command = mapColumnDefinition(targetDatabaseName, command);
				}
				return command;
			}
		}
		return null;
	}

	/**
	 * Map HSQLDB column definition to target database specific column definition.
	 * 
	 * @param targetDatabaseName
	 * @param command
	 * @return
	 */
	public static String mapColumnDefinition(
		String targetDatabaseName,
		String command
	) {
		if(targetDatabaseName.indexOf("HSQL") < 0) {
			command = command.replace("CLOB(10000000)", "CLOB");
			command = command.replace("VARBINARY(10000000)", "VARBINARY");
			command = command.replace("\"P$$PARENT\"", "P$$PARENT");
			command = command.replace("\"NAME\"", "NAME");
			command = command.replace("\"TYPE\"", "TYPE");
			command = command.replace("\"SCOPE\"", "SCOPE");
			command = command.replace("\"LANGUAGE\"", "LANGUAGE");
			command = command.replace("\"POSITION\"", "POSITION");
			command = command.replace("\"STATE\"", "STATE");
			command = command.replace("\"EXCEPTION\"", "EXCEPTION");
			command = command.replace("\"DOMAIN\"", "DOMAIN");
			command = command.replace("\"NUMBER\"", "NUMBER");
			command = command.replace("\"ACTION\"", "ACTION");
			command = command.replace("\"TEXT\"", "TEXT");
		}
		// PostgreSQL
		if(targetDatabaseName.indexOf("PostgreSQL") >=0) {
			command = command.replace(" TIMESTAMP", " TIMESTAMP WITH TIME ZONE");
			command = command.replace(" CLOB,", " TEXT,");
			command = command.replace(" VARBINARY,", " BYTEA,");
			command = command.replace(" CLOB)", " TEXT)");
			command = command.replace(" VARBINARY)", " BYTEA)");
			command = command.replace("'\\'", "E'\\\\'");
			command = command.replace("'AS DTYPE", "'::text AS DTYPE");
			command = command.replace(" CHAR(", " CHR(");
		}
		// HSQLDB
		else if(targetDatabaseName.indexOf("HSQL") >=0) {
		}		
		// MySQL
		else if(targetDatabaseName.indexOf("MySQL") >=0) {
			command = command.replace(" TIMESTAMP", " DATETIME");					
			command = command.replace(" BOOLEAN,", " BIT,");
			command = command.replace(" CLOB,", " LONGTEXT,");
			command = command.replace(" VARBINARY,", " BLOB,");
			command = command.replace(" BOOLEAN)", " BIT)");
			command = command.replace(" CLOB)", " LONGTEXT)");
			command = command.replace(" VARBINARY)", " BLOB)");
			command = command.replace("'\\'", "'\\\\'");
		}
		// DB2		
		else if(targetDatabaseName.indexOf("DB2") >=0) {
			command = command.replace(" BOOLEAN,", " SMALLINT,");
			command = command.replace(" VARBINARY,", " BLOB,");
			command = command.replace(" BOOLEAN)", " SMALLINT)");
			command = command.replace(" VARBINARY)", " BLOB)");
			command = command.replace("SUBSTRING(", "SUBSTR(");
			command = command.replace(" CHAR(", "CHR(");					
			command = command.replace(" WITH RECURSIVE", " WITH");
		}
		// Oracle
		else if(targetDatabaseName.indexOf("Oracle") >=0) {
			command = command.replace(" VARCHAR(", " VARCHAR2(");
			command = command.replace(" SMALLINT,", " NUMBER,");
			command = command.replace(" BOOLEAN,", " NUMBER,");					
			command = command.replace(" VARBINARY,", " BLOB,");
			command = command.replace(" BIGINT,", " INTEGER,");
			command = command.replace(" SMALLINT)", " NUMBER)");
			command = command.replace(" BOOLEAN)", " NUMBER)");					
			command = command.replace(" VARBINARY)", " BLOB)");
			command = command.replace(" BIGINT)", " INTEGER)");
			command = command.replace(",COMMENT ", ",\"comment\" ");
			command = command.replace(",NUMBER ", ",\"number\" ");
			command = command.replace("RESOURCE,", "\"resource\",");
			command = command.replace("RESOURCE)", "\"resource\")");
			command = command.replace(" RESOURCE ", " \"resource\" ");
			command = command.replace("SUBSTRING(", "SUBSTR(");
			command = command.replace(" CHAR(", "CHR(");						
			command = command.replace(" WITH RECURSIVE", " WITH");
		}		
		// Microsoft
		else if(targetDatabaseName.indexOf("Microsoft") >=0) {
			command = command.replace("||", "+");
			command = command.replace(" DATE,", " DATETIME,");					
			command = command.replace(" TIMESTAMP", " DATETIME");					
			command = command.replace(" BOOLEAN,", " BIT,");
			command = command.replace(" CLOB,", " NTEXT,");
			command = command.replace(" VARBINARY,", " IMAGE,");					
			command = command.replace(" DATE)", " DATETIME)");					
			command = command.replace(" BOOLEAN)", " BIT)");
			command = command.replace(" CLOB)", " NTEXT)");
			command = command.replace(" VARBINARY)", " IMAGE)");
			command = command.replace(" WITH RECURSIVE", " WITH");
			command = command.replace("(ASS0.OBJECT_ID)+'*+1'", " CAST(ASS0.OBJECT_ID+'*+1' AS VARCHAR)");
			command = command.replace("(ASS1.OBJECT_ID)+'*+'+(TEMP.DISTANCE+1)", " CAST(ASS1.OBJECT_ID+'*+'+(TEMP.DISTANCE+1) AS VARCHAR)");
		}
		return command;
	}

	/**
	 * Get all table names from reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> getTableNames(
	) throws ServiceException {
		Set<String> tableNames = new TreeSet<String>();
		List<String> schema = getSchema();
		for(String command: schema) {
			if(command.startsWith(CREATE_TABLE_PREFIX)) {
				tableNames.add(
					command.substring(
						CREATE_TABLE_PREFIX.length(),
						command.indexOf("(", CREATE_TABLE_PREFIX.length())
					).trim()
				);
			}
		}
		return new ArrayList<String>(tableNames);
	}
	
	/**
	 * Get all view names from reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> getViewNames(
	) throws ServiceException {
		Set<String> viewNames = new TreeSet<String>();
		List<String> schema = getSchema();
		for(String command: schema) {
			if(command.startsWith(CREATE_VIEW_PREFIX)) {
				viewNames.add(
					command.substring(
						CREATE_VIEW_PREFIX.length(),
						command.indexOf("(", CREATE_VIEW_PREFIX.length())
					).trim()
				);
			}
		}
		return new ArrayList<String>(viewNames);
	}
	
	/**
	 * Get all index names from reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> getIndexNames(
	) throws ServiceException {
		Set<String> indexNames = new TreeSet<String>();
		List<String> schema = getSchema();
		for(String command: schema) {
			if(command.startsWith(CREATE_INDEX_PREFIX)) {
				indexNames.add(
					command.substring(
						CREATE_INDEX_PREFIX.length(),
						command.indexOf(" ", CREATE_INDEX_PREFIX.length() + 1)
					).trim()
				);
			}
		}
		return new ArrayList<String>(indexNames);
	}
	
	/**
	 * Get all sequence names from reference schema.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> getSequenceNames(
	) throws ServiceException {
		Set<String> sequenceNames = new TreeSet<String>();
		List<String> schema = getSchema();
		for(String command: schema) {
			if(command.startsWith(CREATE_SEQUENCE_PREFIX)) {
				sequenceNames.add(
					command.substring(
						CREATE_SEQUENCE_PREFIX.length(),
						command.indexOf(" ", CREATE_SEQUENCE_PREFIX.length() + 1)
					).trim()
				);
			}
		}
		return new ArrayList<String>(sequenceNames);
	}
	
	/**
	 * Compare tables of given database with reference schema.
	 * 
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> validateTables(
		Connection connT,
		boolean fix
	) throws ServiceException {
		Connection connS = null;
		List<String> report = new ArrayList<String>();
		try {			
			connS = getSchemaConnection();
			List<String> schema = getSchema();
			List<String> tableNames = getTableNames();
			for(String tableName: tableNames) {
				String statement = "SELECT * FROM " + tableName + " WHERE 1=0";
				PreparedStatement psT = null;
				FastResultSet rsT = null;
				boolean exists = false;
				try {
					psT = connT.prepareStatement(statement);
					rsT = new FastResultSet(
						psT.executeQuery()
					);
					exists = true;
				} catch(Exception e) {
					if(!fix) {
						report.add("ERROR: Missing table=" + tableName + " (message=" + e + ")");
					}
				} finally {
					try {
						rsT.close();
					} catch(Exception e) {}
					try {
						psT.close();
					} catch(Exception e) {}
				}
				if(!exists) {
					statement = getObjectDefinition(CREATE_TABLE_PREFIX, tableName, schema, connT.getMetaData().getDatabaseProductName(), false); 
					if(fix) {
						PreparedStatement psFix = null;
						try {
							report.add("SQL: " + statement);
							psFix = connT.prepareStatement(statement);
							psFix.executeUpdate();
						} catch(Exception e0) {
							report.add("ERROR: Creation of table " + tableName + " failed (message=" + e0 + ")");
						} finally {
							try {
								psFix.close();
							} catch(Exception e0) {}
						}
					} else {
						report.add("FIX: " + statement);						
					}
				}
			}
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
		finally {
			try {
				connS.close();
			} catch(Exception e0) {}
		}
		return report;
	}
	
	/**
	 * Compare columns of all tables with reference schema.
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> validateTableColumns(
		Connection connT,
		boolean fix
	) throws ServiceException {
		Connection connS = null;
		List<String> report = new ArrayList<String>();
		try {
			connS = getSchemaConnection();
			List<String> schema = getSchema(); 
			List<String> tableNames = getTableNames();
			for(String tableName: tableNames) {
				String statement = "SELECT * FROM " + tableName + " WHERE 1=0";
				PreparedStatement psT = null;
				FastResultSet rsT = null;
				try {
					psT = connT.prepareStatement(statement);
					rsT = new FastResultSet(
						psT.executeQuery()
					);
				} catch(Exception e) {					
				}
				finally {
					try {
						rsT.close();
					} catch(Exception e) {}
					try {
						psT.close();
					} catch(Exception e) {}					
				}
				PreparedStatement psS = connS.prepareStatement(statement);		
				FastResultSet rsS = null;
				try {
					rsS = new FastResultSet(
						psS.executeQuery()
					);					
				} catch(Exception e) {					
				}
				finally {
					try {
						rsS.close();
					} catch(Exception e) {}
					try {
						psS.close();
					} catch(Exception e) {}					
				}
				if(rsT != null && rsS != null) {
					if(!rsT.getColumnNames().containsAll(rsS.getColumnNames())) {
						List<String> missingColumns = new ArrayList<String>(rsS.getColumnNames());
						missingColumns.removeAll(rsT.getColumnNames());
						statement = getObjectDefinition(CREATE_TABLE_PREFIX, tableName, schema, connT.getMetaData().getDatabaseProductName(), false); 							
						for(String columnName: missingColumns) {
							if(!fix) {
								report.add("ERROR: missing column in table=" + tableName + ", column=" + columnName);								
							}
							// pos1: beginning of column definition
							int pos1 = statement.indexOf("(" + columnName.toUpperCase() + " ");
							if(pos1 < 0) {
								pos1 = statement.indexOf("," + columnName.toUpperCase() + " ");
							}
							if(pos1 < 0) {
								pos1 = statement.indexOf("(\"" + columnName.toUpperCase() + "\" ");
							}
							if(pos1 < 0) {
								pos1 = statement.indexOf(",\"" + columnName.toUpperCase() + "\" ");
							}
							if(pos1 > 0) {
								// pos2: end of column definition
								int pos2 = statement.indexOf(",", pos1+1);
								if(pos2 < 0) {
									pos2 = statement.length() - 1;
								} else {
									// handle case DECIMAL(?,?)
									if(Character.isDigit(statement.charAt(pos2+1))) {
										pos2 = statement.indexOf(",", pos2+1);
										if(pos2 < 0) {
											pos2 = statement.length();
										}
									}
								}
								if(pos2 > 0) {
									PreparedStatement psFix = null;
									String addColumnStatement = "ALTER TABLE " + tableName + " ADD " + statement.substring(pos1+1, pos2);
									if(fix) {
										try {
											report.add("SQL: " + addColumnStatement);
											psFix = connT.prepareStatement(addColumnStatement);
											psFix.executeUpdate();
										} catch(Exception e0) {
											report.add("ERROR: Adding column failed (message=" + e0 + ")");
										} finally {
											if(psFix != null) {
												try {
													psFix.close();
												} catch(Exception e0) {}
											}
										}
									} else {
										report.add("FIX: " + addColumnStatement);
									}
								}
							}
						}
					} else {
						report.add("OK: Table=" + tableName);
					}
					if(!rsS.getColumnNames().containsAll(rsT.getColumnNames())) {
						List<String> extraColumns = new ArrayList<String>(rsT.getColumnNames());
						extraColumns.removeAll(rsS.getColumnNames());
						report.add("WARN: Extra or custom columns in table=" + tableName + ", columns=" + extraColumns);						
					}
				}
			}
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
		finally {
			try {
				connS.close();
			} catch(Exception e0) {}
		}
		return report;
	}

	/**
	 * Validate existence of views with reference schema.
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> validateViews(
		Connection connT,
		boolean fix
	) throws ServiceException {
		Connection connS = null;
		List<String> report = new ArrayList<String>();
		try {
			connS = getSchemaConnection();
			List<String> schema = getSchema();			
			List<String> viewNames = getViewNames();
			for(String viewName: viewNames) {
				if(!viewName.endsWith("_ALT")) {
					String statement = "SELECT * FROM " + viewName + " WHERE 1=0";
					PreparedStatement psT = null;
					FastResultSet rsT = null;
					boolean exists = false;
					try {
						psT = connT.prepareStatement(statement);
						rsT = new FastResultSet(
							psT.executeQuery()
						);
						exists = true;
					} catch(Exception ignore) {					
					} finally {
						try {
							rsT.close();
						} catch(Exception e) {}
						try {
							psT.close();
						} catch(Exception e) {}					
					}
					statement = getObjectDefinition(CREATE_VIEW_PREFIX, viewName, schema, connT.getMetaData().getDatabaseProductName(), exists); 
					if(fix) {
						PreparedStatement psFix = null;
						try {
							report.add("SQL: " + statement);
							psFix = connT.prepareStatement(statement);
							psFix.executeUpdate();
						} catch(Exception e) {
							// Fallback to alternative view (suffix _ALT) if available
							String viewNameAlt = viewName + "_ALT"; 
							if(viewNames.contains(viewNameAlt)) {
								statement = getObjectDefinition(CREATE_VIEW_PREFIX, viewNameAlt, schema, connT.getMetaData().getDatabaseProductName(), exists);
								statement = statement.replace("_ALT", "");
								try {
									report.add("SQL: " + statement);
									psFix = connT.prepareStatement(statement);
									psFix.executeUpdate();									
								} catch(Exception e0) {
									report.add("ERROR: Create/Replace of view " + viewName + " failed (message=" + e + ")");
								}
							} else {
								if(!OPTIONAL_DBOBJECTS.contains(viewName)) {
									report.add("ERROR: Create/Replace of view " + viewName + " failed (message=" + e + ")");
								}
							}
						} finally {
							try {
								psFix.close();
							} catch(Exception e0) {}
						}		
					} else {
						if(exists) {
							report.add("OK: View " + viewName);
						} else {
							if(!OPTIONAL_DBOBJECTS.contains(viewName)) {
								report.add("FIX: " + statement);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			throw new ServiceException(e);
		} finally {
			try {
				connS.close();
			} catch(Exception ignore) {}
		}
		return report;
	}

	/**
	 * Migrate data from an older schema version to latest version.
	 * 
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> migrateData(
		Connection connT,
		boolean fix
	) throws ServiceException {
		MigrationDefinition[] migrationDefinitions = {
			new MigrationDefinition(
				"2.2 -> 2.3",
				"SELECT member_role FROM OOCKE1_ACCOUNTASSIGNMENT_ WHERE 1=0",
				Arrays.asList(
					"UPDATE OOCKE1_ACCOUNTASSIGNMENT SET MEMBER_ROLE_0 = (SELECT MEMBER_ROLE FROM OOCKE1_ACCOUNTASSIGNMENT_ ass_ WHERE ass_.OBJECT_ID = OOCKE1_ACCOUNTASSIGNMENT.OBJECT_ID AND ass_.IDX = 0) WHERE MEMBER_ROLE_0 IS NULL",
					"UPDATE OOCKE1_ACCOUNTASSIGNMENT SET MEMBER_ROLE_1 = (SELECT MEMBER_ROLE FROM OOCKE1_ACCOUNTASSIGNMENT_ ass_ WHERE ass_.OBJECT_ID = OOCKE1_ACCOUNTASSIGNMENT.OBJECT_ID AND ass_.IDX = 1) WHERE MEMBER_ROLE_1 IS NULL",
					"UPDATE OOCKE1_ACCOUNTASSIGNMENT SET MEMBER_ROLE_2 = (SELECT MEMBER_ROLE FROM OOCKE1_ACCOUNTASSIGNMENT_ ass_ WHERE ass_.OBJECT_ID = OOCKE1_ACCOUNTASSIGNMENT.OBJECT_ID AND ass_.IDX = 2) WHERE MEMBER_ROLE_2 IS NULL",
					"UPDATE OOCKE1_ACCOUNTASSIGNMENT SET MEMBER_ROLE_3 = (SELECT MEMBER_ROLE FROM OOCKE1_ACCOUNTASSIGNMENT_ ass_ WHERE ass_.OBJECT_ID = OOCKE1_ACCOUNTASSIGNMENT.OBJECT_ID AND ass_.IDX = 3) WHERE MEMBER_ROLE_3 IS NULL",
					"UPDATE OOCKE1_ACCOUNTASSIGNMENT SET MEMBER_ROLE_4 = (SELECT MEMBER_ROLE FROM OOCKE1_ACCOUNTASSIGNMENT_ ass_ WHERE ass_.OBJECT_ID = OOCKE1_ACCOUNTASSIGNMENT.OBJECT_ID AND ass_.IDX = 4) WHERE MEMBER_ROLE_4 IS NULL"
				)
			),
			new MigrationDefinition(
				"2.3 -> 2.4",
				null,
				Collections.<String>emptyList()
			),
			new MigrationDefinition(
				"2.4 -> 2.5",
				"SELECT rate_type FROM OOCKE1_WORKRECORD WHERE 1=0",
				Arrays.asList(
					"UPDATE OOCKE1_WORKRECORD SET PAYMENT_TYPE = 0 WHERE PAYMENT_TYPE IS NULL",
					"UPDATE OOCKE1_WORKRECORD SET RECORD_TYPE = RATE_TYPE WHERE RECORD_TYPE IS NULL",
					"UPDATE OOCKE1_WORKRECORD SET QUANTITY = (1.0 * DURATION_HOURS) + (DURATION_MINUTES / 60.0) WHERE QUANTITY IS NULL",
					"UPDATE OOCKE1_WORKRECORD SET QUANTITY_UOM = N'uom/CRX/Root/hour' WHERE QUANTITY_UOM IS NULL"
				)				
			),
			new MigrationDefinition(
				"2.5 -> 2.6",
				null,
				Arrays.asList(
					"DELETE FROM OOCKE1_CALENDARFEED_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_CALENDARFEED WHERE DTYPE = 'org:opencrx:kernel:home1:IcalFeed')",
					"DELETE FROM OOCKE1_CALENDARFEED WHERE DTYPE = 'org:opencrx:kernel:home1:IcalFeed'"
				)								
			),
			new MigrationDefinition(
				"2.6 -> 2.7",
				null,
				Collections.<String>emptyList()
			),
			new MigrationDefinition(
				"2.7 -> 2.8",
				"SELECT e_mail_address FROM OOCKE1_EMAILACCOUNT WHERE 1=0",
				Arrays.asList(
					"UPDATE OOCKE1_ACTIVITYPARTY SET PARTY_STATUS = 0 WHERE PARTY_STATUS IS NULL",
					"UPDATE OOCKE1_DOCUMENTFOLDERASS SET ASSIGNMENT_ROLE = 0 WHERE ASSIGNMENT_ROLE IS NULL",
					"UPDATE OOCKE1_SEGMENT SET ACCESS_LEVEL_UPDATE = 3 WHERE ACCESS_LEVEL_UPDATE IS NULL",
					"UPDATE OOCKE1_EMAILACCOUNT SET NAME = E_MAIL_ADDRESS, IS_ACTIVE = true WHERE NAME IS NULL"
				)				
			),
			new MigrationDefinition(
				"2.8 -> 2.9",
				"SELECT reference_filter FROM OOCKE1_EXPORTPROFILE WHERE 1=0",
				Arrays.asList(
					"UPDATE OOMSE2_PRIVILEGE SET DTYPE = 'org:openmdx:security:realm1:Privilege' WHERE DTYPE LIKE 'org:openmdx:security:authorization1:%'", 
					"UPDATE OOMSE2_PRIVILEGE_ SET DTYPE = 'org:openmdx:security:realm1:Privilege' WHERE DTYPE LIKE 'org:openmdx:security:authorization1:%'",
					"UPDATE OOCKE1_EXPORTPROFILE SET EXPORT_PARAMS = REFERENCE_FILTER",
					"UPDATE OOCKE1_EXPORTPROFILE SET LOCALE = 0 WHERE LOCALE IS NULL",
					"UPDATE OOCKE1_ACTIVITYCREATOR SET ICAL_TYPE = 1 WHERE ICAL_TYPE = 0 AND EXISTS (SELECT 0 FROM OOCKE1_ACTIVITYTYPE T WHERE OOCKE1_ACTIVITYCREATOR.ACTIVITY_TYPE = T.OBJECT_ID AND ACTIVITY_CLASS <> 8)",
					"UPDATE OOCKE1_ACTIVITYCREATOR SET ICAL_TYPE = 2 WHERE ICAL_TYPE = 0 AND EXISTS (SELECT 0 FROM OOCKE1_ACTIVITYTYPE T WHERE OOCKE1_ACTIVITYCREATOR.ACTIVITY_TYPE = T.OBJECT_ID AND ACTIVITY_CLASS = 8)",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_TYPE = 1 WHERE ICAL_TYPE = 0 AND DTYPE <> 'org:opencrx:kernel:activity1:Task'",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_TYPE = 2 WHERE ICAL_TYPE = 0 AND DTYPE = 'org:opencrx:kernel:activity1:Task'"		
				)								 
			),
			new MigrationDefinition(
				"2.9 -> 2.10",
				"SELECT CLOSING_CODE FROM OOCKE1_ACCOUNT WHERE 1=0",
				Arrays.asList(
					"UPDATE OOCKE1_ACCOUNT SET CLOSING_CODE = 0 WHERE CLOSING_CODE IS NULL",
					"DELETE FROM OOCKE1_ALERT WHERE REFERENCE LIKE 'reminder/%'",
					"DELETE FROM OOCKE1_ALERT_ WHERE NOT EXISTS (SELECT 0 FROM OOCKE1_ALERT a WHERE a.OBJECT_ID = OOCKE1_ALERT_.OBJECT_ID)"			
				)								 				
			),
			new MigrationDefinition(
				"2.10 -> 2.11",
				"SELECT ICAL_CLASS FROM OOCKE1_ACTIVITYCREATOR WHERE 1=0",
				Arrays.asList(
					"UPDATE OOCKE1_ACTIVITYCREATOR SET ICAL_CLASS = 3 WHERE ICAL_CLASS IS NULL",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_CLASS = 1 WHERE ICAL_CLASS IS NULL AND ICAL LIKE '%CLASS:PRIVATE%'",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_CLASS = 2 WHERE ICAL_CLASS IS NULL AND ICAL LIKE '%CLASS:CONFIDENTIAL%'",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_CLASS = 3 WHERE ICAL_CLASS IS NULL AND ICAL LIKE '%CLASS:PUBLIC%'",
					"UPDATE OOCKE1_ACTIVITY SET ICAL_CLASS = 2 WHERE ICAL_CLASS IS NULL",
					"ALTER TABLE OOCKE1_WFPROCESS ALTER COLUMN description TYPE VARCHAR(2000)",
					"ALTER TABLE OOCKE1_CODEVALUEENTRY_ ALTER COLUMN long_text TYPE VARCHAR(512)"
				)
			),
		};
		String targetDatabaseName = "";
		try {
			targetDatabaseName = connT.getMetaData().getDatabaseProductName();
		} catch(Exception e) {}
		List<String> report = new ArrayList<String>();
		PreparedStatement psT = null;
		for(MigrationDefinition migrationDefinition: migrationDefinitions) {
			try {
				if(migrationDefinition.getTestForVersionStatement() != null) {
					psT = connT.prepareStatement(migrationDefinition.getTestForVersionStatement());
					psT.executeQuery();
					ResultSet rsT = psT.executeQuery();
					rsT.next();
					rsT.close();			
					psT.close();
				}
				for(String statement: migrationDefinition.getMigrationStatements()) {
					if(targetDatabaseName.indexOf("DB2") >=0 || targetDatabaseName.indexOf("Oracle") >=0) {
						statement = statement.replace("true", "1");
						statement = statement.replace("false", "0");
					}
					if(statement.startsWith(ALTER_TABLE_PREFIX)) {
						statement = mapColumnDefinition(targetDatabaseName, statement);
						if(targetDatabaseName.indexOf("PostgreSQL") >=0) {
							// nothing to do
						} else if(targetDatabaseName.indexOf("HSQL") >=0) {
							statement = statement.replace("TYPE ", "");							
						} else if(targetDatabaseName.indexOf("Oracle") >=0) {
							statement = statement.replace("ALTER COLUMN", "MODIFY");
							statement = statement.replace("TYPE ", "");
						} else if(targetDatabaseName.indexOf("MySQL") >=0) {
							statement = statement.replace("ALTER COLUMN", "MODIFY");						
							statement = statement.replace("TYPE ", "");
						} else if(targetDatabaseName.indexOf("DB2") >=0) {
							statement = statement.replace("TYPE ", "SET DATA TYPE ");
						} else if(targetDatabaseName.indexOf("Microsoft") >=0) {
							statement = statement.replace("TYPE ", "");
						}
					}
					if(fix) {
						try {					
							report.add("SQL (" + migrationDefinition.getName() + "): " + statement);
							psT = connT.prepareStatement(statement);
							psT.executeUpdate();
						} catch(Exception e) {
							report.add("ERROR: Migration failed (message=" + e + ")");									
						} finally {
							try {
								psT.close();
							} catch(Exception e0) {}
						}
					} else {								
						report.add("FIX (" + migrationDefinition.getName() + "): " + statement);								
					}
				}
			} catch(Exception e) {
				try {
					psT.close();
				} catch(Exception e0) {}
				// Schema is not valid for migration
			}
		}
		return report;
	}

	/**
	 * Validate existence of indexes with reference schema.
	 * 
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> validateIndexes(
		Connection connT,
		boolean fix
	) throws ServiceException {
		List<String> report = new ArrayList<String>();
		try {
			List<String> schema = getSchema();
			List<String> indexNames = getIndexNames();
			String databaseProductName = connT.getMetaData().getDatabaseProductName();
			for(String indexName: indexNames) {
				String statement = getObjectDefinition(CREATE_INDEX_PREFIX, indexName, schema, connT.getMetaData().getDatabaseProductName(), false);
				PreparedStatement psT = null;
				try {
					if(databaseProductName.indexOf("Oracle") >= 0) {
						if(indexName.length() > 30) {
							statement = statement.replace(indexName, indexName.substring(0, 30));
						}
					}
					psT = connT.prepareStatement(statement);
					psT.executeUpdate();
					if(fix) {
						report.add("SQL: " + statement);
					} else {
						report.add("OK: Index " + indexName);						
					}
				} catch(SQLException e0) {
					String message = e0.getMessage();
					int errorCode = e0.getErrorCode();
					boolean alreadyExists = 
						message.indexOf("already exists") >= 0 || // PostgreSQL, HSQLDB, DB2
						message.indexOf("Duplicate key name") >= 0 || // MySQL
						message.indexOf("ORA-01408") >= 0 || // Oracle
						message.indexOf("ORA-00955") >= 0 || // Oracle
						errorCode == 1913; // SQL Server
					if(alreadyExists) {
						if(!fix) {
							report.add("OK: Index " + indexName);
						}
					} else {
						report.add("ERROR: " + statement + " (message=" + e0.getMessage() + "; errorCode=" + errorCode + ")");
					}
				} finally {
					try {
						psT.close();
					} catch(Exception e0) {}
				}
			}
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
		return report;		
	}

	/**
	 * Validate existence of sequences with reference schema.
	 * 
	 * @param connT
	 * @param fix
	 * @return
	 * @throws ServiceException
	 */
	public static List<String> validateSequences(
		Connection connT,
		boolean fix
	) throws ServiceException {
		List<String> report = new ArrayList<String>();
		try {
			List<String> schema = getSchema();
			List<String> sequenceNames = getSequenceNames();
			String databaseProductName = connT.getMetaData().getDatabaseProductName();
			for(String sequenceName: sequenceNames) {
				String statement = getObjectDefinition(CREATE_SEQUENCE_PREFIX, sequenceName, schema, databaseProductName, false);
				PreparedStatement psT = null;
				if(
					databaseProductName.indexOf("Microsoft") >= 0 ||
					databaseProductName.indexOf("MySQL") >= 0
				) {
					statement = "SELECT * FROM " + sequenceName + " WHERE 1=0";
					try {
						psT = connT.prepareStatement(statement);
						psT.executeQuery();
						psT.close();
						report.add("OK: Sequence " + sequenceName);
					} catch(Exception e) {
						psT.close();
						statement = "CREATE TABLE " + sequenceName + "(nextval int)";
						report.add("SQL: " + statement);
						psT = connT.prepareStatement(statement);
						psT.executeUpdate();
						psT.close();
						statement = "INSERT INTO " + sequenceName + " (nextval) VALUES (1000000)";
						report.add("SQL: " + statement);
						psT = connT.prepareStatement(statement);
						psT.executeUpdate();
						psT.close();
					} finally {
						try {
							psT.close();
						} catch(Exception e0) {}
					}
				} else {
					try {
						if(databaseProductName.indexOf("Oracle") >= 0) {
							statement = statement + " NOCYCLE CACHE 100 NOORDER";
						}
						psT = connT.prepareStatement(statement);
						psT.executeUpdate();
						if(fix) {
							report.add("SQL: " + statement);
						} else {
							report.add("OK: Sequence " + sequenceName);						
						}
					}
					catch(SQLException e0) {
						String message = e0.getMessage();
						int errorCode = e0.getErrorCode();
						boolean alreadyExists = 
							message.indexOf("already exists") >= 0 ||
							message.indexOf("Duplicate key name") >= 0 ||
							message.indexOf("ORA-01408") >= 0 ||
							message.indexOf("ORA-00955") >= 0 ||
							message.indexOf("SQLSTATE=42710") >= 0 ||
							errorCode == 99999;
						if(alreadyExists) {
							if(!fix) {
								report.add("OK: Sequence " + sequenceName);
							}
						} else {
							report.add("ERROR: " + statement + " (message=" + e0.getMessage() + "; errorCode=" + errorCode + ")");
						}
					} finally {
						try {
							psT.close();
						} catch(Exception e0) {}
					}
				}
			}
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
		return report;		
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	public static final String CREATE_TABLE_PREFIX = "CREATE MEMORY TABLE PUBLIC.";
	public static final String CREATE_VIEW_PREFIX = "CREATE VIEW PUBLIC.";
	public static final String CREATE_SEQUENCE_PREFIX = "CREATE SEQUENCE PUBLIC.";
	public static final String CREATE_INDEX_PREFIX = "CREATE INDEX";
	public static final String ALTER_TABLE_PREFIX = "ALTER TABLE";
	public static final Set<String> OPTIONAL_DBOBJECTS = new HashSet<String>(
		Arrays.asList("OOCKE1_TOBJ_ACCTMEMBERSHIP_FR", "OOCKE1_TOBJ_ACCTMEMBERSHIP_TO")
	);
}

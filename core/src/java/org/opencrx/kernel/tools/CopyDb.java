/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CopyDb tool
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2011, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.opencrx.kernel.utils.DbSchemaUtils;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.LayerConfigurationEntries;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.exception.BasicException;

public class CopyDb {

	/**
	 * Map CLOB to String.
	 * 
	 * @param clob
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private static String getStringFromClob(
		java.sql.Clob clob
	) throws IOException, SQLException {
		Reader reader = clob.getCharacterStream();
		StringBuilder s = new StringBuilder();
		int c;
		while ((c = reader.read()) != -1) {
			s.append((char) c);
		}
		return s.toString();
	}

	/**
	 * Map BLOB to byte[].
	 * 
	 * @param blob
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private static byte[] getBytesFromBlob(
		java.sql.Blob blob
	) throws IOException, SQLException {
		InputStream is = blob.getBinaryStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int b;
		while ((b = is.read()) != -1) {
			os.write(b);
		}
		os.close();
		return os.toByteArray();
	}

	/**
	 * Db-specific column name mapping.
	 * 
	 * @param conn
	 * @param dbObject
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	private static String mapColumnName(
		Connection conn, 
		String dbObject, 
		String columnName
	) throws SQLException {
		String databaseProductName = conn.getMetaData().getDatabaseProductName();
		if("HSQL Database Engine".equals(databaseProductName)) {
			String mappedColumnName = columnName.toUpperCase();
			if("POSITION".equals(mappedColumnName) || mappedColumnName.indexOf("$") > 0) {
				return "\"" + mappedColumnName + "\"";
			}
			else {
				return mappedColumnName;
			}
		}
		else {
			return columnName.toUpperCase();
		}
	}

	/**
	 * DB-specific column value mapping.
	 * 
	 * @param conn
	 * @param dbObject
	 * @param columnName
	 * @param columnValue
	 * @param providerNameSource
	 * @param providerNameTarget
	 * @return
	 * @throws ServiceException
	 * @throws SQLException
	 */
	private static Object mapColumnValue(
		Connection conn, 
		String dbObject, 
		String columnName, 
		Object columnValue, 
		String providerNameSource, 
		String providerNameTarget
	) throws ServiceException, SQLException {
		String databaseProductName = conn.getMetaData().getDatabaseProductName();
		if(BOOLEAN_COLUMNS.contains(columnName.toUpperCase())) {
			if("PostgreSQL".equals(databaseProductName)) {
				return columnValue;
			}
			else if("MySQL".equals(databaseProductName)) {
				return columnValue;
			}
			else if("Microsoft SQL Server".equals(databaseProductName)) {
				return columnValue;
			}
			else if(databaseProductName.startsWith("DB2/")) {
				return Boolean.valueOf("Y".equals(columnValue));
			}
			else if("HSQL Database Engine".equals(databaseProductName)) {
				return columnValue;
			}
			else if("Oracle".equals(databaseProductName)) {
				return Boolean.valueOf(((Number) columnValue).intValue() == 1);
			}
			else {
				throw new ServiceException(BasicException.Code.DEFAULT_DOMAIN, BasicException.Code.NOT_SUPPORTED, "Database not supported", new BasicException.Parameter("database product name",
				    databaseProductName));
			}
		}
		else {
			if(columnValue instanceof String) {
				Object targetValue = columnValue;
				if(providerNameSource != null && providerNameSource.length() > 0 && providerNameTarget != null && providerNameTarget.length() > 0) {
					String sourceValue = (String) columnValue;
					int pos = sourceValue.indexOf("/" + providerNameSource + "/");
					if(pos > 0) {
						targetValue = sourceValue.substring(0, pos) + "/" + providerNameTarget + "/" + sourceValue.substring(pos + providerNameSource.length() + 2);
					}
				}
				return targetValue;
			}
			else {
				return columnValue;
			}
		}
	}

	/**
	 * Copy dbObject from source to target database.
	 * 
	 * @param dbObject
	 * @param useSuffix
	 * @param connSource
	 * @param connTarget
	 * @param providerNameSource
	 * @param providerNameTarget
	 * @param out
	 * @throws SQLException
	 */
	private static void copyDbObject(
		String dbObject, 
		boolean useSuffix, 
		Connection connSource, 
		Connection connTarget, 
		String providerNameSource, 
		String providerNameTarget, 
		PrintStream out
	)
	    throws SQLException {

		String currentStatement = null;

		Database_1 db = new Database_1();
		try {
			Configuration configuration = new Configuration();
			configuration.values(LayerConfigurationEntries.BOOLEAN_TYPE).put(0, LayerConfigurationEntries.BOOLEAN_TYPE_STANDARD);
			configuration.values(LayerConfigurationEntries.DATABASE_CONNECTION_FACTORY);
			db.activate((short) 0, configuration, null);
		}
		catch (Exception e) {
			out.println("Can not activate database plugin: " + e.getMessage());
		}
		try {
			// Delete all rows from target
			PreparedStatement s = connTarget.prepareStatement(currentStatement = "DELETE FROM " + dbObject + (useSuffix ? "_" : ""));
			s.executeUpdate();
			s.close();

			// Read all rows from source
			s = connSource.prepareStatement(currentStatement = "SELECT * FROM " + dbObject + (useSuffix ? "_" : ""));
			ResultSet rs = s.executeQuery();
			if(rs != null) {
				ResultSetMetaData rsm = rs.getMetaData();
				FastResultSet frs = new FastResultSet(rs);
				int nRows = 0;

				while (frs.next()) {
					// Read row from source and prepare INSERT statement
					String statement = "INSERT INTO " + dbObject + (useSuffix ? "_" : "") + " ";
					List<Object> statementParameters = new ArrayList<Object>();
					List<String> processTargetColumnNames = new ArrayList<String>();
					for (int j = 0; j < rsm.getColumnCount(); j++) {
						String columnName = rsm.getColumnName(j + 1);
						if(frs.getObject(columnName) != null) {
							String mappedColumnName = CopyDb.mapColumnName(connTarget, dbObject, columnName);
							if(mappedColumnName != null) {
								statement += (statementParameters.size() == 0 ? " (" : ", ") + mappedColumnName;
								processTargetColumnNames.add(mappedColumnName);
								if(frs.getObject(columnName) instanceof java.sql.Clob) {
									try {
										statementParameters.add(CopyDb.getStringFromClob((java.sql.Clob) frs.getObject(columnName)));
									}
									catch (Exception e) {
										out.println("Reading Clob failed. Reason: " + e.getMessage());
										out.println("statement=" + statement);
										out.println("parameters=" + statementParameters);
									}
								}
								else if(frs.getObject(columnName) instanceof java.sql.Blob) {
									try {
										statementParameters.add(CopyDb.getBytesFromBlob((java.sql.Blob) frs.getObject(columnName)));
									}
									catch (Exception e) {
										out.println("Reading Blob failed. Reason: " + e.getMessage());
										out.println("statement=" + statement);
										out.println("parameters=" + statementParameters);
									}
								}
								else {
									statementParameters.add(CopyDb.mapColumnValue(connSource, dbObject, columnName, frs.getObject(columnName), providerNameSource, providerNameTarget));
								}
							}
						}
					}
					statement += ") VALUES (";
					for (int j = 0; j < statementParameters.size(); j++) {
						statement += j == 0 ? "?" : ", ?";
					}
					statement += ")";

					// Add row to target
					try {
						PreparedStatement t = connTarget.prepareStatement(currentStatement = statement);
						for (int j = 0; j < statementParameters.size(); j++) {
							Object parameter = statementParameters.get(j);
							if("oracle.sql.TIMESTAMP".equals(parameter.getClass().getName())) {
								Method timestampValueMethod = parameter.getClass().getMethod("timestampValue", new Class[] {});
								parameter = timestampValueMethod.invoke(parameter, new Object[] {});
							}
							if(parameter instanceof java.sql.Timestamp) {
								t.setTimestamp(j + 1, (java.sql.Timestamp) parameter);
							}
							else if(parameter instanceof java.sql.Date) {
								t.setDate(j + 1, (java.sql.Date) parameter);
							}
							else {
								db.setPreparedStatementValue(connTarget, t, j + 1, parameter);
							}
						}
						t.executeUpdate();
						t.close();
					}
					catch (Exception e) {
						new ServiceException(e).log();
						out.println("Insert failed. Reason: " + e.getMessage());
						out.println("statement=" + statement);
						out.println("parameters=" + statementParameters);
					}
					nRows++;
					if(nRows % 1000 == 0) {
						out.println(nRows + " rows copied");
					}
				}
				rs.close();
			}
			else {
				out.println("Did not copy table (result set is null). Statement: " + currentStatement);
			}
			s.close();
		}
		catch (Exception e) {
			new ServiceException(e).log();
			out.println("Can not copy table (see log for more info). Statement: " + currentStatement);
		}
	}

	/**
	 * Copy all tables from source to target database.
	 * 
	 * @param connSource
	 * @param connTarget
	 * @param dbObjects
	 * @param startFromDbObject
	 * @param endWithDbObject
	 * @param providerNameSource
	 * @param providerNameTarget
	 * @param out
	 */
	private static void copyNamespace(
	    Connection connSource,
	    Connection connTarget,
	    List<String> dbObjects,
	    int startFromDbObject,
	    int endWithDbObject,
	    String providerNameSource,
	    String providerNameTarget,
	    PrintStream out
	) {
		String currentStatement = null;
		try {
			out.println("Processing tables:");
			int ii = 0;
			for(String dbObject: dbObjects) {
				out.println(ii + ": " + dbObject);
				ii++;
			}
			Set<String> processedDbObjects = new HashSet<String>();
			for (int i = 0; (i < dbObjects.size()) && (i <= endWithDbObject); i++) {
				String dbObject = dbObjects.get(i);
				if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
					if(i >= startFromDbObject) {
						out.println("Copying table " + i + ": " + dbObject);
						CopyDb.copyDbObject(
							dbObject, 
							false, 
							connSource, 
							connTarget, 
							providerNameSource, 
							providerNameTarget, 
							out
						);
						out.println("Copying table " + i + ": " + dbObject + "_");
						CopyDb.copyDbObject(
							dbObject, 
							true, 
							connSource, 
							connTarget, 
							providerNameSource, 
							providerNameTarget, 
							out
						);
					}
					else {
						out.println("Skipping table " + i + ": " + dbObject);
					}
					processedDbObjects.add(dbObject);
				}
			}
		}
		catch (SQLException e) {
			ServiceException e0 = new ServiceException(e);
			e0.log();
			out.println("statement: " + currentStatement + " (message=" + e0.getMessage());
		}
	}

	/**
	 * CopyDb utility.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Properties env = System.getProperties();
			copyDb(
				env.getProperty("jdbcDriverSource"), 
				env.getProperty("usernameSource"), 
				env.getProperty("passwordSource"), 
				env.getProperty("jdbcUrlSource"), 
				env.getProperty("jdbcDriverTarget"),
			    env.getProperty("usernameTarget"), 
			    env.getProperty("passwordTarget"), 
			    env.getProperty("jdbcUrlTarget"), 
			    env.getProperty("kernel.startFromDbObject"),
			    env.getProperty("kernel.endWithDbObject"), 
			    env.getProperty("security.startFromDbObject"), 
			    env.getProperty("security.endWithDbObject"), 
			    env.getProperty("providerNameSource"),
			    env.getProperty("providerNameTarget"), 
			    System.out
			);
		}
		catch (Exception e) {
			new ServiceException(e).log();
		}
	}

	/**
	 * CopyDb utility. 
	 * 
	 * @param jdbcDriverSource
	 * @param usernameSource
	 * @param passwordSource
	 * @param jdbcUrlSource
	 * @param jdbcDriverTarget
	 * @param usernameTarget
	 * @param passwordTarget
	 * @param jdbcUrlTarget
	 * @param kernelStartFromDbObject
	 * @param kernelEndWithDbObject
	 * @param securityStartFromDbObject
	 * @param securityEndWithDbObject
	 * @param providerNameSource
	 * @param providerNameTarget
	 * @param out
	 * @throws ServiceException
	 */
	public static void copyDb(
	    String jdbcDriverSource,
	    String usernameSource,
	    String passwordSource,
	    String jdbcUrlSource,
	    String jdbcDriverTarget,
	    String usernameTarget,
	    String passwordTarget,
	    String jdbcUrlTarget,
	    String kernelStartFromDbObject,
	    String kernelEndWithDbObject,
	    String securityStartFromDbObject,
	    String securityEndWithDbObject,
	    String providerNameSource,
	    String providerNameTarget,
	    PrintStream out
	) throws ServiceException {
		{
			DBOBJECTS_KERNEL.clear();
			DBOBJECTS_SECURITY.clear();
			List<String> tableNames = new ArrayList<String>();
			try {
				tableNames = DbSchemaUtils.getTableNames();
			}
			catch (Exception e) {
				new ServiceException(e).log();
			}
			for (String tableName : tableNames) {
				if(tableName.indexOf("_TOBJ_") < 0 && tableName.indexOf("_JOIN_") < 0 && !tableName.endsWith("_")) {
					if(tableName.startsWith("OOCKE1_")) {
						DBOBJECTS_KERNEL.add(tableName);
					}
					else if(tableName.startsWith("OOMSE2_")) {
						DBOBJECTS_SECURITY.add(tableName);
					}
				}
			}			
		}
		try {
			// Source connection
			Class.forName(jdbcDriverSource);
			Properties props = new Properties();
			props.put("user", usernameSource);
			props.put("password", passwordSource);
			Connection connSource = DriverManager.getConnection(jdbcUrlSource, props);
			connSource.setAutoCommit(true);
			// Target connection
			Class.forName(jdbcDriverTarget);
			props = new Properties();
			props.put("user", usernameTarget);
			props.put("password", passwordTarget);
			Connection connTarget = DriverManager.getConnection(jdbcUrlTarget, props);
			connTarget.setAutoCommit(true);
			// Namespace kernel
			String endWithDbObject = kernelEndWithDbObject;
			CopyDb.copyNamespace(
				connSource, 
				connTarget, 
				DBOBJECTS_KERNEL, 
				kernelStartFromDbObject == null || kernelStartFromDbObject.isEmpty() ? 0 : Integer.valueOf(kernelStartFromDbObject).intValue(),
			    endWithDbObject == null || endWithDbObject.isEmpty() ? Integer.MAX_VALUE : Integer.valueOf(endWithDbObject).intValue(), 
			    providerNameSource, 
			    providerNameTarget, 
			    out
			);
			// Namespace security
			endWithDbObject = securityEndWithDbObject;
			CopyDb.copyNamespace(
				connSource, 
				connTarget, 
				DBOBJECTS_SECURITY, 
				securityStartFromDbObject == null || securityStartFromDbObject.isEmpty() ? 0 : Integer.valueOf(securityStartFromDbObject).intValue(),
			    endWithDbObject == null || endWithDbObject.isEmpty() ? Integer.MAX_VALUE : Integer.valueOf(endWithDbObject).intValue(), 
			   	providerNameSource, 
			   	providerNameTarget, 
			   	out
			);
		}
		catch (Exception e) {
			throw new ServiceException(e);
		}
		out.println();
		out.println("!!! DONE !!!");
	}

	// -----------------------------------------------------------------------
	// Members
	// -----------------------------------------------------------------------
	static final List<String> DBOBJECTS_KERNEL = new ArrayList<String>();

	static final List<String> DBOBJECTS_SECURITY = new ArrayList<String>();

	static final Set<String> BOOLEAN_COLUMNS = new HashSet<String>(Arrays.asList(
	    "DISABLED", "USER_BOOLEAN0", "USER_BOOLEAN1", "USER_BOOLEAN2", "USER_BOOLEAN3", "USER_BOOLEAN4", "DO_NOT_BULK_POSTAL_MAIL", "DO_NOT_E_MAIL", "DO_NOT_FAX", "DO_NOT_PHONE",
	    "DO_NOT_POSTAL_MAIL", "EXT_BOOLEAN0", "EXT_BOOLEAN1", "EXT_BOOLEAN2", "EXT_BOOLEAN3", "EXT_BOOLEAN4", "EXT_BOOLEAN5", "EXT_BOOLEAN6", "EXT_BOOLEAN7", "EXT_BOOLEAN8", "EXT_BOOLEAN9",
	    "DISABLED", "DISCOUNT_IS_PERCENTAGE", "USER_BOOLEAN4", "IS_ALL_DAY_EVENT", "DELIVERY_RECEIPT_REQUESTED", "READ_RECEIPT_REQUESTED", "IS_MAIN", "RESET_TO_NULL", "IS_MAIN", "AUTOMATIC_PARSING",
	    "IS_CLOSED", "IS_FINAL", "CREDIT_FIRST", "IS_DEFAULT", "IS_WORKING_DAY", "IS_LOCKED", "IS_GIFT", "IS_TEMPLATE", "DISCOUNT_IS_PERCENTAGE", "IS_GIFT", "SALES_COMMISSION_IS_PERCENTAGE",
	    "IS_CREDIT_ON_HOLD", "ALLOW_POSITION_AUTO_CREATE", "IS_DEFAULT", "IS_LOCKED", "IS_TEMPLATE", "IS_LOCKED", "HOLDER_QUALIFIES_POSITION", "IS_DRAFT", "ALLOW_CREDIT_BOOKINGS",
	    "ALLOW_DEBIT_BOOKINGS", "IS_DEFAULT", "IS_ACTIVE", "BOOLEAN_PARAM", "IS_CHANGEABLE", "IS_QUERY", "IS_DERIVED", "IS_ABSTRACT", "IS_SINGLETON", "IS_CLUSTERED", "IS_NAVIGABLE",
	    "WEIGHT_IS_PERCENTAGE", "IS_FINAL", "DISCOUNT_IS_PERCENTAGE", "IS_DEFAULT", "ALLOW_MODIFICATION", "ALLOW_REMOVAL", "DISCOUNT_IS_PERCENTAGE", "OVERRIDE_PRICE", "IS_STOCK_ITEM",
	    "DISCOUNT_IS_PERCENTAGE", "IS_DEFAULT", "BOOLEAN_VALUE", "IS_ACTIVE", "NEW_BOOLEAN", "OLD_BOOLEAN", "SELECTOR", "IS_SCHEDULE_BASE_UOM", "STORE_SETTINGS_ON_LOGOFF", "IS_SYNCHRONOUS", "FAILED",
	    "IS_BILLABLE", "IS_REIMBURSABLE", "LOCKED", "ALLOW_ADD_DELETE", "ALLOW_CHANGE"
	));

}

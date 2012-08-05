/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: CheckDb.java,v 1.22 2007/08/18 21:46:16 wfro Exp $
 * Description: Convert database RID columns from numeric to string format
 * Revision:    $Revision: 1.22 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/08/18 21:46:16 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2005, CRIXP Corp., Switzerland
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

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.compatibility.base.exception.StackedException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;

public class CheckDb {

    //---------------------------------------------------------------------------
    static Path getReference(
        Connection conn,  
        long referenceId,
        String refTableName
    ) throws ServiceException  {
        PreparedStatement ps = null;
        String statement = null;
        ResultSet rs = null;
        Path reference = null;
        try {
            List statementParameters = new ArrayList();
            ps = conn.prepareStatement(
                statement = "SELECT * FROM " + refTableName + " WHERE (object_rid = ?)"
            );
            ps.setLong(
                1, 
                referenceId
            );
            statementParameters.add(new Long(referenceId));
            rs = ps.executeQuery();
            if(rs.next()) {
                int n = rs.getInt("n");
                reference = new Path("");
                for(int i = 0; i < n; i++) {
                    reference = reference.add(
                        rs.getString("c$" + i)
                    );
                }
            }
            else {
                return null;
            }
        }
        catch(SQLException ex) {
            throw new ServiceException(
              ex, 
              StackedException.DEFAULT_DOMAIN,
              StackedException.MEDIA_ACCESS_FAILURE, 
              new BasicException.Parameter[]{
                new BasicException.Parameter("reference", reference),
                new BasicException.Parameter("statement", statement)
              },
              null
            );
        }
        catch(Exception ex) {
            throw new ServiceException(
              ex, 
              StackedException.DEFAULT_DOMAIN,
              StackedException.GENERIC, 
              null, 
              ex.toString()
            );
        }
        finally {
            try {
              if(rs != null) rs.close();
            } catch(Throwable ex) {}
            try {
              if(ps != null) ps.close();
            } catch(Throwable ex) {}
        }          
        return reference;    
    }

    //---------------------------------------------------------------------------
    /**
     * Convert s from UUID to UID. Return s if it is not an UUID.
     */
    private static String convertPathComponent(
        String s
    ) {
        if((s.length() == 36) && ('-' == s.charAt(8)) && ('-' == s.charAt(13)) && ('-' == s.charAt(18)) && ('-' == s.charAt(23))) {
            return UUIDConversion.toUID(
                UUIDConversion.fromString(s)
            );
        }
        //  (deprecated) openMDX ID format
        else if(s.length() == 46) {
            try {
                return Base64.encode(new BigInteger(s).toByteArray()).substring(0, 26).replace('/', '-');
            }
            catch(Exception e) {
                return s;
            }
        }
        else {            
            return s;
        }
    }
    
    //---------------------------------------------------------------------------
    private static String convertRid(
        Connection conn,
        String dbObject,
        String columnName,
        String rid,
        Path[] types,
        String[] typeNames,
        Map referencesInError
    ) throws ServiceException {
        String namespace = dbObject.substring(0, dbObject.indexOf("_"));
        String refTableName = namespace + "_REF";
        Path reference = getReference(
            conn, 
            Long.parseLong(rid), 
            refTableName
        );
        if(reference != null) {
            String convertedRid = null;
            for(
                int k = 0;
                k < types.length;
                k++
            ) {
                Path type = types[k];
                if((type.size() >= 2) && reference.isLike(type.getParent())) {
                    convertedRid = typeNames[k];
                    if(convertedRid == null) {
                        System.out.println("ERROR: No type name defined for type " + k);
                        break;
                    }
                    for(
                        int l = 0;
                        l < reference.size();
                        l++
                    ) {
                        if(!reference.get(l).equals(type.get(l))) {
                            convertedRid += "/" + convertPathComponent(reference.get(l));
                        }
                    }
                    break;
                }
            }
            if(convertedRid != null) {
                return convertedRid;
            }
            else {
                System.out.println("ERROR: No path pattern found for reference " + reference + " at column " + columnName);
                if(!"object_rid".equals(columnName) && !"p$$object_parent__rid".equals(columnName)) {
                    String referenceName = columnName.endsWith("_parent__rid")
                        ? columnName.substring(3, columnName.indexOf("_parent__rid"))
                        : columnName.substring(3, columnName.indexOf("__rid"));
                    if(referencesInError.get(dbObject) == null) {
                        referencesInError.put(
                            dbObject,
                            new HashSet()
                        );                                                    
                    }
                    ((Set)referencesInError.get(dbObject)).add(referenceName);
                }
            }
        }
        else {
            System.out.println("ERROR: No reference found for rid " + rid + " at column " + columnName);
            if(!"object_rid".equals(columnName) && !"p$$object_parent__rid".equals(columnName)) {
                String referenceName = columnName.endsWith("_parent__rid")
                    ? columnName.substring(3, columnName.indexOf("_parent__rid"))
                    : columnName.substring(3, columnName.indexOf("__rid"));
                if(referencesInError.get(dbObject) == null) {
                    referencesInError.put(
                        dbObject,
                        new HashSet()
                    );                                                    
                }
                ((Set)referencesInError.get(dbObject)).add(referenceName);
            }
        }
        return null;
    }
              
    //---------------------------------------------------------------------------
    private static void convertFrom1_8_1To1_9_1(
        Connection conn,
        String namespaceId,
        boolean fixErrors,
        Path[] types,
        String[] dbObjects,
        String[] typeNames,
        Number startWithTable,
        Number endWithTable,
        Number startWithPhase
    ) {
        String currentStatement = null;           
        try {
            String databaseProductName = conn.getMetaData().getDatabaseProductName();

            System.out.println();
            System.out.println("Converting namespace " + namespaceId + "(database=" + databaseProductName + ")");            
            
            // Phase 0: Change column type of all RID columns to VARCHAR
            if((startWithPhase == null) || (startWithPhase.intValue() <= 0)) {
                System.out.println();
                System.out.println("Phase 0: Checking tables");
                boolean hasErrors = false;
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase 0: Processing table " + i + " (" + dbObject + ")");
                        PreparedStatement s = conn.prepareStatement(
                            currentStatement = "SELECT * FROM " + dbObject + " WHERE (1=0)"
                        );
                        s.executeQuery();
                        ResultSet rs = s.getResultSet();
                        ResultSetMetaData rsm = rs.getMetaData();
                        for(
                            int j =  0;
                            j < rsm.getColumnCount();
                            j++
                        ) {
                            String columnName = rsm.getColumnName(j+1).toLowerCase();
                            // Convert RID columns from integer to varchar
                            if(
                                "object_rid".equals(columnName) || 
                                "object_oid".equals(columnName) ||
                                (columnName.startsWith("p$$") && columnName.endsWith("__rid")) ||
                                (columnName.startsWith("p$$") && columnName.endsWith("__oid"))
                            ) {
                                String nullableClause = "object_rid".equals(columnName) || "object_oid".equals(columnName)
                                    ? "NOT NULL"
                                    : "";
                                if("PostgreSQL".equals(databaseProductName)) {
                                    try {
                                        if(!columnName.endsWith("__oid")) {
                                            PreparedStatement s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " ALTER " + columnName + " TYPE VARCHAR(200) " + nullableClause
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                        }
                                    }
                                    catch(Exception e) {
                                        System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
                                        hasErrors = true;
                                    }
                                }
                                else if("MySQL".equals(databaseProductName)) {
                                    try {
                                        if(!columnName.endsWith("__oid")) {
                                            PreparedStatement s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR(200) " + nullableClause
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                        }
                                    }
                                    catch(Exception e) {
                                        System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
                                        hasErrors = true;
                                    }
                                }
                                else if("Microsoft SQL Server".equals(databaseProductName)) {
                                    try {
                                        if("object_rid".equals(columnName) || "object_oid".equals(columnName)) {
                                            try {
                                                PreparedStatement s0 = conn.prepareStatement(
                                                    currentStatement = "ALTER TABLE " + dbObject + " DROP CONSTRAINT PK_" + dbObject
                                                );
                                                s0.executeUpdate();
                                                s0.close();
                                            }
                                            catch(Exception e) {
                                                System.out.println("INFO: Can not drop PK constraint for table " + dbObject);
                                            }
                                        }
                                        PreparedStatement s0 = conn.prepareStatement(
                                            currentStatement = "ALTER TABLE " + dbObject + " ALTER COLUMN " + columnName + " NVARCHAR(200) COLLATE SQL_Latin1_General_CP1_CS_AS " + nullableClause
                                        );
                                        System.out.println("  " + currentStatement);
                                        s0.executeUpdate();
                                        s0.close();
                                        if("object_rid".equals(columnName) || "object_oid".equals(columnName)) {
                                            s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " ADD CONSTRAINT PK_" + dbObject + " PRIMARY KEY (object_rid,object_oid,object_idx)"
                                            );
                                            s0.executeUpdate();
                                            s0.close();
                                        }
                                    }
                                    catch(Exception e) {
                                        System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
                                        hasErrors = true;
                                    }
                                }
                                else if(databaseProductName.startsWith("DB2/")) {
                                    try {
                                        if(!columnName.endsWith("__oid")) {
                                            PreparedStatement s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR(200) " + nullableClause
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                        }
                                    }
                                    catch(Exception e) {
                                        System.out.println("ERROR: Can not alter column " + columnName);
                                        hasErrors = true;
                                    }
                                }
                                else if("Oracle".equals(databaseProductName)) {
                                    try {
                                        if(!columnName.endsWith("__oid")) {
                                            // Update in one transaction
                                            conn.setAutoCommit(false);
                                            // DROP PRIMARY KEY
                                            if("object_rid".equals(columnName) || "object_oid".equals(columnName)) {
                                                try {
                                                    PreparedStatement s0 = conn.prepareStatement(
                                                        currentStatement = "ALTER TABLE " + dbObject + " DROP PRIMARY KEY"
                                                    );
                                                    System.out.println("  " + currentStatement);
                                                    s0.executeUpdate();
                                                    s0.close();
                                                }
                                                catch(Exception e) {
                                                    System.out.println("INFO: Can not drop PK constraint for table " + dbObject);
                                                }
                                            }
                                            // RENAME column to be converted to COL_TMP
                                            PreparedStatement s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " RENAME COLUMN " + columnName + " TO COL_TMP"
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                            // ADD column columnName
                                            s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " ADD (" + columnName + " VARCHAR2(200) )"
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                            // Transfer and convert data from COL_TMP to new column
                                            s0 = conn.prepareStatement(
                                                currentStatement = "UPDATE " + dbObject + " SET " + columnName + " = TO_CHAR(COL_TMP)"
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();                                        
                                            // Modify to NULL/NOT NULL
                                            s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " MODIFY (" + columnName + " " + nullableClause + ")"
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                            // Drop column COL_TMP
                                            s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " DROP (COL_TMP)"
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                            // Add primary key constraint
                                            if("object_rid".equals(columnName) || "object_oid".equals(columnName)) {
                                                s0 = conn.prepareStatement(
                                                    currentStatement = "ALTER TABLE " + dbObject + " ADD CONSTRAINT PK_" + (dbObject.length() > 27 ? dbObject.substring(0,27) : dbObject) + " PRIMARY KEY (object_rid,object_oid,object_idx)"
                                                );
                                                System.out.println("  " + currentStatement);
                                                s0.executeUpdate();
                                                s0.close();
                                            }
                                            // Commit
                                            conn.commit();
                                        }
                                    }
                                    catch(Exception e) {
                                        try {
                                            conn.rollback();
                                        } catch(Exception e0) {}
                                        System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
                                        hasErrors = true;
                                    }
                                    finally {
                                        try {
                                            conn.setAutoCommit(true);
                                        } catch(Exception e) {}
                                    }
                                }
                                else if("SAP DB".equals(databaseProductName)) {
                                    try {
                                        if(!columnName.endsWith("__oid")) {
                                            PreparedStatement s0 = conn.prepareStatement(
                                                currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR(200) " + nullableClause
                                            );
                                            System.out.println("  " + currentStatement);
                                            s0.executeUpdate();
                                            s0.close();
                                        }
                                    }
                                    catch(Exception e) {
                                        System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
                                        hasErrors = true;
                                    }
                                }
                                else {
                                    throw new ServiceException(
                                        StackedException.DEFAULT_DOMAIN,
                                        StackedException.NOT_SUPPORTED, 
                                        new BasicException.Parameter[]{
                                          new BasicException.Parameter("database product name", databaseProductName)
                                        },
                                        "Database not supported"
                                      );
                                }
                            }
                        }
                        processedDbObjects.add(dbObject);
                        rs.close();
                        s.close();
                    }
                }            
                if(hasErrors) {
                    System.out.println("Phase 0: Can not convert RIDs. Fix above errors first and restart again.");
                    return;
                }
            }
            
            // Phase I: set values of RID columns
            Map referencesInError = new HashMap();
            if((startWithPhase == null) || (startWithPhase.intValue() <= 1)) {            
                System.out.println();
                System.out.println("Phase I: Checking references");
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase I: Processing table " + i + " (" + dbObject + ")");
                        long nRows = 0;
                        // Iterate all rows
                        PreparedStatement ps = conn.prepareStatement(
                            currentStatement = "SELECT * FROM " + dbObject
                        );
                        ps.executeQuery();
                        FastResultSet frs = new FastResultSet(ps.getResultSet());
                        while(frs.next()) {
                            String objectRid = (String)frs.getObject("object_rid");
                            String objectOid = (String)frs.getObject("object_oid");
                            int objectIdx = ((Number)frs.getObject("object_idx")).intValue();
                            String statement = "UPDATE " + dbObject + " SET ";
                            List statementParameters = new ArrayList();
                            boolean hasClause = false;
                            for(
                                int j =  0;
                                j < frs.getColumnNames().size();
                                j++
                            ) {
                                String columnName = (String)frs.getColumnNames().get(j);
                                
                                // Update columns object_rid, object_oid
                                if("object_rid".equals(columnName)) {
                                    if((objectRid != null) && (objectRid.indexOf("/") < 0)) {
                                        String newRid = convertRid(conn, dbObject, columnName, objectRid, types, typeNames, referencesInError);
                                        if(newRid != null) {
                                            statement += hasClause ? ", " : "";
                                            statement += "object_rid = ?, object_oid = ?";
                                            statementParameters.add(
                                                newRid
                                            );
                                            statementParameters.add(
                                                convertPathComponent(objectOid)
                                            );
                                            hasClause = true;
                                        }
                                    }
                                }
                                // Update columns p$$<reference name>_parent__rid, p$$<reference name>_parent__oid
                                else if((columnName.startsWith("p$$") && columnName.endsWith("_parent__rid"))) {
                                    String columnNameReference = columnName.substring(3, columnName.indexOf("_parent__rid"));
                                    String columnNameRid = "p$$" + columnNameReference + "_parent__rid";
                                    String columnNameOid = "p$$" + columnNameReference + "_parent__oid";
                                    String rid = (String)frs.getObject(columnNameRid);
                                    String oid = (String)frs.getObject(columnNameOid);
                                    if((rid != null) && (rid.indexOf("/") < 0)) {
                                        String newRid = convertRid(conn, dbObject, columnName, rid, types, typeNames, referencesInError);
                                        if(newRid != null) {
                                            statement += hasClause ? ", " : "";
                                            statement += columnNameRid + " = ?, " + columnNameOid + " = ?";
                                            statementParameters.add(
                                                newRid
                                            );
                                            statementParameters.add(
                                                convertPathComponent(oid)
                                            );
                                            hasClause = true;
                                        }
                                    }
                                }
                                // Update columns p$$<reference name>_parent__rid, p$$<reference name>_parent__oid
                                else if((columnName.startsWith("p$$") && columnName.endsWith("__rid"))) {
                                    String columnNameReference = columnName.substring(3, columnName.indexOf("__rid"));
                                    
                                    // Update
                                    String columnNameRid = "p$$" + columnNameReference + "__rid";
                                    String columnNameOid = "p$$" + columnNameReference + "__oid";
                                    String rid = (String)frs.getObject(columnNameRid);
                                    String oid = (String)frs.getObject(columnNameOid);
                                    if((rid != null) && (rid.indexOf("/") < 0)) {
                                        if(frs.getObject(columnNameReference) == null) {
                                            if(referencesInError.get(dbObject) == null) {
                                                referencesInError.put(
                                                    dbObject,
                                                    new HashSet()
                                                );                                                    
                                            }
                                            ((Set)referencesInError.get(dbObject)).add(columnNameReference);
                                        }
                                        else {
                                            String newRid = convertRid(conn, dbObject, columnName, rid, types, typeNames, referencesInError);
                                            if(newRid != null) {
                                                statement += hasClause ? ", " : "";
                                                statement += columnNameReference + " = ?, " + columnNameRid + " = ?, " + columnNameOid + " = ?";
                                                statementParameters.add(
                                                    newRid + "/" + convertPathComponent(oid) 
                                                );
                                                statementParameters.add(
                                                    newRid
                                                );
                                                statementParameters.add(
                                                    convertPathComponent(oid)
                                                );
                                                hasClause = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if(hasClause) {
                                statement += " WHERE (object_rid = ?) AND (object_oid = ?) AND (object_idx = ?)";
                                PreparedStatement ps1 = conn.prepareStatement(
                                    currentStatement = statement
                                );
                                int j = 0;
                                while(j < statementParameters.size()) {
                                    if(statementParameters.get(j) instanceof Path) {
                                        ps1.setString(
                                            j+1, 
                                            ((Path)statementParameters.get(j)).toUri()
                                        );                                    
                                    }
                                    else {
                                        ps1.setString(
                                            j+1, 
                                            (String)statementParameters.get(j)
                                        );
                                    }
                                    j++;
                                }
                                ps1.setString(j+1, objectRid);
                                ps1.setString(j+2, objectOid);
                                ps1.setInt(j+3, objectIdx);
                                try {
                                    ps1.executeUpdate();
                                }
                                catch(SQLException e) {
                                    System.out.println("Update failed. Reason: " + e.getMessage());
                                    System.out.println("Row.rid=" + objectRid + ", oid=" + objectOid + ", idx=" + objectIdx);
                                    System.out.println("statement=" + statement);
                                    System.out.println("parameters=" + statementParameters);
                                }
                                ps1.close();
                            }
                            nRows++;
                            if(nRows % 50 == 0) {
                                System.out.println("INFO: Processed " + nRows + " rows");
                            }
                        }
                        ps.close();
                        processedDbObjects.add(dbObject);
                    }
                }
            }
            
            // Phase II: Fix errors
            if(
               fixErrors &&
               ((startWithPhase == null) || (startWithPhase.intValue() <= 2))
            ) {
                System.out.println();
                System.out.println("Phase II: Fixing tables");
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase II: Processing table " + i + " (" + dbObject + ")");
                        try {
                            // Remove objects with non-convertable object_rid
                            PreparedStatement ps = conn.prepareStatement(
                                currentStatement = "DELETE FROM " + dbObject + " WHERE object_rid NOT LIKE '%/%'"
                            );
                            ps.executeUpdate();
                            // Remove objects with non-convertable p$$parent_object__rid
                            ps = conn.prepareStatement(
                                currentStatement = "DELETE FROM " + dbObject + " WHERE object_oid IN (SELECT object_oid FROM " + dbObject + " WHERE p$$object_parent__rid NOT LIKE '%/%')"
                            );
                            ps.executeUpdate();
                            // Set invalid references to NULL
                            if(referencesInError.get(dbObject) != null) {
                                for(
                                    Iterator j = ((Set)referencesInError.get(dbObject)).iterator();
                                    j.hasNext();
                                ) {
                                    String referenceName = (String)j.next();
                                    System.out.println("  Fixing reference " + referenceName);
                                    ps = conn.prepareStatement(
                                        currentStatement = "UPDATE " + dbObject + " SET " + 
                                        referenceName + " = NULL," + 
                                        "p$$" + referenceName + "__rid = NULL, " +
                                        "p$$" + referenceName + "__oid = NULL, " +
                                        "p$$" + referenceName + "_parent__rid = NULL," + 
                                        "p$$" + referenceName + "_parent__oid = NULL" +
                                        " WHERE (p$$" + referenceName + "__rid NOT LIKE '%/%') OR (p$$" + referenceName + "_parent__rid NOT LIKE '%/%')"
                                    );
                                    ps.executeUpdate();
                                }
                            }
                        }
                        catch(Exception e) {
                            System.out.println("INFO: Can not fix table " + dbObject + ". Reason is " + e.getMessage());
                        }     
                        processedDbObjects.add(dbObject);
                    }
                }
            }
            
            System.out.println("INFO: Done");
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("statement: " + currentStatement);
        }
        catch(ServiceException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    private static void reorgTable(
        Connection conn,
        String dbObject
    ) {
        PreparedStatement s0 = null;
        String currentStatement = null;
        try {
            s0 = conn.prepareStatement(
                currentStatement = "CALL SYSPROC.ADMIN_CMD('REORG TABLE " + dbObject + "')"
            );
            s0.executeUpdate();
            System.out.println("  " + currentStatement);
            s0.close();
        }
        catch(Exception e) {}
        finally {
            try {
                s0.close();
            } catch(Exception e0) {}
        }                       
    }
    
    //-----------------------------------------------------------------------
    private static void addColumnVARCHAR(
        Connection conn,
        String dbObject,
        String columnName,
        int size,
        boolean isNullable
    ) throws SQLException, ServiceException {
        String currentStatement = null;           
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        String nullableClause = isNullable
            ? ""
            : "NOT NULL";
        PreparedStatement s0 = null;
        if("PostgreSQL".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD " + columnName + " VARCHAR(" + size + ") " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("MySQL".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD " + columnName + " VARCHAR(" + size + ") character set utf8 collate utf8_bin " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD " + columnName + " nvarchar(" + size + ") COLLATE SQL_Latin1_General_CP1_CS_AS " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if(databaseProductName.startsWith("DB2/")) {
            try {
                // ALTER
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD " + columnName + " varchar(" + size + ") " + nullableClause + (isNullable ? "" : " DEFAULT '#'")
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
                CheckDb.reorgTable(conn, dbObject);
            }
            catch(Exception e) {
                try {
                    s0.close();
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    s0.close();
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("Oracle".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD (" + columnName + " varchar2(" + size + ") " + nullableClause + ")"
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("SAP DB".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD " + columnName + " VARCHAR(" + size + ") " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not add column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
              );
        }        
    }
    
    //-----------------------------------------------------------------------
    private static void alterColumnVARCHAR(
        Connection conn,
        String dbObject,
        String columnName,
        int size,
        boolean isNullable
    ) throws SQLException, ServiceException {
        String currentStatement = null;           
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        String nullableClause = isNullable
            ? ""
            : "NOT NULL";
        PreparedStatement s0 = null;
        if("PostgreSQL".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ALTER " + columnName + " TYPE VARCHAR(" + size + ")" 
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
                if(isNullable) {
                    s0 = conn.prepareStatement(
                        currentStatement = "ALTER TABLE " + dbObject + " ALTER " + columnName + " DROP NOT NULL" 
                    );
                    System.out.println("  " + currentStatement);
                    s0.executeUpdate();
                    s0.close();
                }
                else {
                    s0 = conn.prepareStatement(
                        currentStatement = "ALTER TABLE " + dbObject + " ALTER " + columnName + " SET NOT NULL" 
                    );
                    System.out.println("  " + currentStatement);
                    s0.executeUpdate();
                    s0.close();                    
                }
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("MySQL".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR(" + size + ") character set utf8 collate utf8_bin " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ALTER COLUMN " + columnName + " NVARCHAR(" + size + ") COLLATE SQL_Latin1_General_CP1_CS_AS " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if(databaseProductName.startsWith("DB2/")) {
            try {
                // ALTER
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ALTER COLUMN " + columnName + " SET DATA TYPE VARCHAR(" + size + ") " + (isNullable ? "" : " ALTER COLUMN " + columnName + " SET NOT NULL")
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
                CheckDb.reorgTable(conn, dbObject);
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                    s0.close();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                    s0.close();
                } catch(Exception e) {}
            }
        }
        else if("Oracle".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR2(" + size + ") " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                    s0.close();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                    s0.close();
                } catch(Exception e) {}
            }
        }
        else if("SAP DB".equals(databaseProductName)) {
            try {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " MODIFY " + columnName + " VARCHAR(" + size + ") " + nullableClause
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                    s0.close();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not alter column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                    s0.close();
                } catch(Exception e) {}
            }
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
              );
        }        
    }
    
    //-----------------------------------------------------------------------
    private static void dropColumn(
        Connection conn,
        String dbObject,
        String columnName
    ) throws SQLException, ServiceException {
        String currentStatement = null;           
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            try {
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP " + columnName
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("MySQL".equals(databaseProductName)) {
            try {
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP " + columnName
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            try {
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP COLUMN " + columnName
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if(databaseProductName.startsWith("DB2/")) {
            try {
                // ALTER
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP " + columnName
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
                CheckDb.reorgTable(conn, dbObject);
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("Oracle".equals(databaseProductName)) {
            try {
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP (" + columnName + ")"
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else if("SAP DB".equals(databaseProductName)) {
            try {
                PreparedStatement s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " DROP " + columnName
                );
                System.out.println("  " + currentStatement);
                s0.executeUpdate();
                s0.close();
            }
            catch(Exception e) {
                try {
                    conn.rollback();
                } catch(Exception e0) {}
                System.out.println("ERROR: Can not drop column " + columnName + " (Reason: " + e.getMessage() + ")");
            }
            finally {
                try {
                    conn.setAutoCommit(true);
                } catch(Exception e) {}
            }
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String STRCAT_PREFIX(
        Connection conn
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            return "";
        }
        else if("MySQL".equals(databaseProductName)) {
            return "CONCAT(";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            return "";
        }
        else if(databaseProductName.startsWith("DB2/")) {
            return "";
        }
        else if("Oracle".equals(databaseProductName)) {
            return "";
        }
        else if("SAP DB".equals(databaseProductName)) {
            return "";
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String STRCAT_INFIX(
        Connection conn
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            return "||";
        }
        else if("MySQL".equals(databaseProductName)) {
            return ",";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            return "+";
        }
        else if(databaseProductName.startsWith("DB2/")) {
            return "||";
        }
        else if("Oracle".equals(databaseProductName)) {
            return "||";
        }
        else if("SAP DB".equals(databaseProductName)) {
            return "&";
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String STRCAT_SUFFIX(
        Connection conn
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            return "";
        }
        else if("MySQL".equals(databaseProductName)) {
            return ")";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            return "";
        }
        else if(databaseProductName.startsWith("DB2/")) {
            return "";
        }
        else if("Oracle".equals(databaseProductName)) {
            return "";
        }
        else if("SAP DB".equals(databaseProductName)) {
            return "";
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String STRPOS(
        Connection conn,
        String str,
        String pattern
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            return "POSITION(" + pattern + " IN " + str + ")";
        }
        else if("MySQL".equals(databaseProductName)) {
            return "POSITION(" + pattern + " IN " + str + ")";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            return "CHARINDEX(" + pattern  + ", " + str + ")";
        }
        else if(databaseProductName.startsWith("DB2/")) {
            return "LOCATE(" + pattern + ", " + str + ")";
        }
        else if("Oracle".equals(databaseProductName)) {
            return "INSTR(" + str + ", " + pattern + ")";
        }
        else if("SAP DB".equals(databaseProductName)) {
            return "INDEX(" + str + ", " + pattern + ")";
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String SUBSTR(
        Connection conn
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        if("PostgreSQL".equals(databaseProductName)) {
            return "SUBSTRING";
        }
        else if("MySQL".equals(databaseProductName)) {
            return "SUBSTRING";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            return "SUBSTRING";
        }
        else if(databaseProductName.startsWith("DB2/")) {
            return "SUBSTR";
        }
        else if("Oracle".equals(databaseProductName)) {
            return "SUBSTR";
        }
        else if("SAP DB".equals(databaseProductName)) {
            return "SUBSTRING";
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
    }
    
    //---------------------------------------------------------------------------
    private static String dropPrimaryKeyConstraint(
        Connection conn,
        String dbObject,
        Map renamedDbObjects
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        String currentStatement = null;
        String pkName = null;
        String dropClause = null;
        PreparedStatement s0 = null;
        if("PostgreSQL".equals(databaseProductName)) {
            if((renamedDbObjects != null) && renamedDbObjects.containsKey(dbObject)) {
                pkName = renamedDbObjects.get(dbObject) + "_pkey";
            }
            else {
                pkName = dbObject + "_pkey";                
            }
            dropClause = "CONSTRAINT " + pkName;
        }
        else if("MySQL".equals(databaseProductName)) {
            pkName = "PK_" + dbObject;
            dropClause = "PRIMARY KEY";
        }
        else if("Microsoft SQL Server".equals(databaseProductName)) {
            pkName = "PK_" + dbObject;
            dropClause = "CONSTRAINT " + pkName;
        }
        else if(databaseProductName.startsWith("DB2/")) {
            pkName = "PK_" + dbObject;
            dropClause = "PRIMARY KEY";
        }
        else if("Oracle".equals(databaseProductName)) {
            if((renamedDbObjects != null) && renamedDbObjects.containsKey(dbObject)) {
                pkName = "PK_" + renamedDbObjects.get(dbObject);
                pkName = pkName.substring(0, Math.min(30, pkName.length()));
                if(pkName.equalsIgnoreCase("PK_KERNEL_ACTIVITYEFFORTESTIMA")) {
                    pkName = "PK_KERNEL_ACTIVITYEFFORTEST";
                }
                else if(pkName.equalsIgnoreCase("PK_KERNEL_ADDITIONALEXTERNALLI")) {
                    pkName = "PK_KERNEL_ADDITIONALEXTLINK";
                }    
                else if(pkName.equalsIgnoreCase("PK_KERNEL_ACTIVITYGROUPASSIGNM")) {
                    pkName = "PK_KERNEL_ACTIVITYGROUPASS";
                }                   
                else if(pkName.equalsIgnoreCase("PK_KERNEL_ACTIVITYPROCESSACTIO")) {
                    pkName = "PK_KERNEL_ACTIVITYPROCESSACT";
                }                                   
            }
            else {
                pkName = "PK_" + dbObject;
                pkName = pkName.substring(0, Math.min(30, pkName.length()));
                if(pkName.equalsIgnoreCase("PK_KERNEL_DEPOTENTITY")) {
                    pkName = "PK_KERNEL_DEPOTENTRY";
                }
                else if(pkName.equalsIgnoreCase("PK_KERNEL_DEPOTHOLDER")) {
                    pkName = "PK_KERNEL_DEPOTCONTRACT";
                }
                else if(pkName.equalsIgnoreCase("PK_KERNEL_ACTIVITYPARTY")) {
                    pkName = "PK_KERNEL_ACTIVITYMEMBER";
                }                
            }
            dropClause = "CONSTRAINT " + pkName;
        }
        else if("SAP DB".equals(databaseProductName)) {
            pkName = "PK_" + dbObject;
            dropClause = "CONSTRAINT " + pkName;
        }
        else {
            throw new ServiceException(
                StackedException.DEFAULT_DOMAIN,
                StackedException.NOT_SUPPORTED, 
                new BasicException.Parameter[]{
                  new BasicException.Parameter("database product name", databaseProductName)
                },
                "Database not supported"
            );
        }        
        // Drop primary key constraint
        try {
            s0 = conn.prepareStatement(
                currentStatement = "ALTER TABLE " + dbObject + " DROP " + dropClause
            );
            System.out.println("  " + currentStatement);
            s0.executeUpdate();
            s0.close();
            if(databaseProductName.startsWith("DB2/")) {
                CheckDb.reorgTable(conn, dbObject);
            }
        }
        catch(Exception e) {
            System.out.println("INFO: Can not drop PK constraint for table " + dbObject);
        }
        finally {
            if(s0 != null) {
                try { s0.close(); } catch(Exception e) {}
            }
        }
        return pkName;
    }
    
    //---------------------------------------------------------------------------
    private static void addPrimaryKeyConstraint(
        Connection conn,
        String dbObject,
        String pkName,
        String pkColumns
    ) throws SQLException, ServiceException {
        String databaseProductName = conn.getMetaData().getDatabaseProductName();
        String currentStatement = null;
        PreparedStatement s0 = null;
        
        try {
            // Add primary key constraint
            if(databaseProductName.startsWith("DB2/")) {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD PRIMARY KEY (" + pkColumns + ")"
                );
            }
            else {
                s0 = conn.prepareStatement(
                    currentStatement = "ALTER TABLE " + dbObject + " ADD CONSTRAINT " + pkName + " PRIMARY KEY (" + pkColumns + ")"
                );                                            
            }
            System.out.println("  " + currentStatement);
            s0.executeUpdate();
            s0.close();
        }
        catch(Exception e) {
            System.out.println("INFO: Can not drop PK constraint for table " + dbObject);
        }
        finally {
            if(s0 != null) {
                try { s0.close(); } catch(Exception e) {}
            }
        }
    }            
    
    //---------------------------------------------------------------------------
    private static void convertFrom1_9_1To1_10_0(
        Connection conn,
        String namespaceId,
        String[] dbObjects,
        String[] dbObjectsN,
        List sharedDbObjects,
        Number startWithTable,
        Number endWithTable,
        Number startWithPhase
    ) {
        String currentStatement = null;
        
        Map renamedDbObjects = new HashMap();
        renamedDbObjects.put("kernel_ActivityGroupAss", "kernel_ActivityGroupAssignment");
        renamedDbObjects.put("kernel_ActivityProcState", "kernel_ActivityProcessState");
        renamedDbObjects.put("kernel_ActivityProcTrans", "kernel_ActivityProcessTrans");
        renamedDbObjects.put("kernel_ActivityProcAction", "kernel_ActivityProcessAction");
        renamedDbObjects.put("kernel_ActivityEffortEsti", "kernel_ActivityEffortEstimate");
        renamedDbObjects.put("kernel_ProductConfTypeSet", "kernel_ProductConfigTypeSet");
        renamedDbObjects.put("kernel_ProductConfig", "kernel_ProductConfiguration");
        renamedDbObjects.put("kernel_RASClassificatiElt", "kernel_RASClassificationElt");
        renamedDbObjects.put("kernel_RASVarPoint", "kernel_RASVariabilityPoint");
        renamedDbObjects.put("kernel_TemplateRepl", "kernel_TemplateReplacement");
        renamedDbObjects.put("kernel_ContactRel", "kernel_ContactRelationship");
        renamedDbObjects.put("kernel_OrgUnitRelShip", "kernel_OrgUnitRelationship");
        renamedDbObjects.put("kernel_DeliveryInfo", "kernel_DeliveryInformation");
        renamedDbObjects.put("kernel_AdditionalExtLink", "kernel_AdditionalExternalLink");
        try {
            String databaseProductName = conn.getMetaData().getDatabaseProductName();

            System.out.println();
            System.out.println("Converting namespace " + namespaceId + "(database=" + databaseProductName + ")");            
            
            // Phase 0: Change column type of all RID columns to VARCHAR
            if((startWithPhase == null) || (startWithPhase.intValue() <= 0)) {
                System.out.println();
                System.out.println("Phase 0: Converting object identities from (rid,oid) to (id) format");
                boolean hasErrors = false;
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase 0: Processing table " + i + " (" + dbObject + ")");
                        PreparedStatement s = conn.prepareStatement(
                            currentStatement = "SELECT * FROM " + dbObject + " WHERE (1=0)"
                        );
                        s.executeQuery();
                        ResultSet rs = s.getResultSet();
                        FastResultSet frs = new FastResultSet(s.getResultSet());
                        
                        // Convert (object_rid, object_oid) to object_id
                        if(frs.getColumnNames().contains("object_oid")) {
                            CheckDb.addColumnVARCHAR(conn, dbObject, "object_id", 250, true);
                            try {
                                PreparedStatement s0 = conn.prepareStatement(
                                    currentStatement = "UPDATE " + dbObject + " SET object_id = " + STRCAT_PREFIX(conn) + " object_rid " + STRCAT_INFIX(conn) + " '/' " + STRCAT_INFIX(conn) + " object_oid " + STRCAT_SUFFIX(conn)
                                );
                                System.out.println("  " + currentStatement);
                                s0.executeUpdate();
                                s0.close();
                            }
                            catch(Exception e) {
                                System.out.println("ERROR: Can not update table " + dbObject + " (Reason: " + e.getMessage() + ")");
                                hasErrors = true;
                            }
                            String pkName = CheckDb.dropPrimaryKeyConstraint(conn, dbObject, renamedDbObjects);
                            CheckDb.alterColumnVARCHAR(conn, dbObject, "object_id", 250, false);
                            // Add primary key constraint
                            if(databaseProductName.startsWith("DB2/")) {
                                PreparedStatement s0 = conn.prepareStatement(
                                    currentStatement = "ALTER TABLE " + dbObject + " ADD PRIMARY KEY (object_id,object_idx)"
                                );
                                System.out.println("  " + currentStatement);
                                s0.executeUpdate();
                                s0.close();
                                CheckDb.reorgTable(conn, dbObject);
                            }
                            else {
                                PreparedStatement s0 = conn.prepareStatement(
                                    currentStatement = "ALTER TABLE " + dbObject + " ADD CONSTRAINT " + pkName + " PRIMARY KEY (object_id,object_idx)"
                                );
                                System.out.println("  " + currentStatement);
                                s0.executeUpdate();
                                s0.close();
                            }
                            // Drop columns
                            CheckDb.dropColumn(conn, dbObject, "object_oid");
                            CheckDb.dropColumn(conn, dbObject, "object_rid");
                        }
                        // Add p$$type_name as discriminator in case table is shared
                        if(
                            sharedDbObjects.contains(dbObject) &&
                            !frs.getColumnNames().contains("p$$type_name")
                        ) {
                            CheckDb.addColumnVARCHAR(conn, dbObject, "p$$type_name", 50, true);
                            try {
                                PreparedStatement s0 = conn.prepareStatement(
                                    currentStatement = "UPDATE " + dbObject + " SET p$$type_name = " + SUBSTR(conn) + "(object_id, 1, " + STRPOS(conn, "object_id", "'/'") + " - 1)"
                                );
                                System.out.println("  " + currentStatement);
                                s0.executeUpdate();
                                s0.close();
                            }
                            catch(Exception e) {
                                System.out.println("ERROR: Can not update table " + dbObject + " (Reason: " + e.getMessage() + ")");
                                hasErrors = true;
                            }                            
                        }
                        // Convert (p$$object_parent__rid, p$$object_parent__oid) to object_id
                        if(frs.getColumnNames().contains("p$$object_parent__oid")) {
                            addColumnVARCHAR(conn, dbObject, "p$$parent", 250, true);
                            try {
                                PreparedStatement s0 = conn.prepareStatement(
                                    databaseProductName.equals("MySQL")                                    
                                        ? (currentStatement = "UPDATE " + dbObject + " SET p$$parent = (SELECT * FROM (SELECT " + STRCAT_PREFIX(conn) + " t.p$$object_parent__rid " + STRCAT_INFIX(conn) + " '/' " + STRCAT_INFIX(conn) + " t.p$$object_parent__oid " + STRCAT_SUFFIX(conn) + " FROM " + dbObject + " t WHERE t.object_id = " + "object_id AND t.object_idx = 0) x )")
                                        : (currentStatement = "UPDATE " + dbObject + " SET p$$parent = (SELECT " + STRCAT_PREFIX(conn) + " t.p$$object_parent__rid " + STRCAT_INFIX(conn) + " '/' " + STRCAT_INFIX(conn) + " t.p$$object_parent__oid " + STRCAT_SUFFIX(conn) + " FROM " + dbObject + " t WHERE t.object_id = " + dbObject + ".object_id AND t.object_idx = 0)")
                                );
                                System.out.println("  " + currentStatement);
                                s0.executeUpdate();
                                s0.close();
                                CheckDb.dropColumn(conn, dbObject, "p$$object_parent__rid");
                                CheckDb.dropColumn(conn, dbObject, "p$$object_parent__oid");
                            }
                            catch(Exception e) {
                                System.out.println("ERROR: Can not update table " + dbObject + " (Reason: " + e.getMessage() + ")");
                                hasErrors = true;
                            }
                        }
                        
                        for(
                            Iterator j = frs.getColumnNames().iterator();
                            j.hasNext();
                        ) {
                            String columnName = (String)j.next();
                            // Process columns containing object references
                            if(
                                frs.getColumnNames().contains(columnName) &&
                                frs.getColumnNames().contains("p$$" + columnName + "__rid") &&
                                frs.getColumnNames().contains("p$$" + columnName + "__oid") &&
                                frs.getColumnNames().contains("p$$" + columnName + "_parent__rid") &&
                                frs.getColumnNames().contains("p$$" + columnName + "_parent__oid")
                            ) {
                                CheckDb.addColumnVARCHAR(conn, dbObject, "p$$" + columnName + "_parent", 250, true);
                                try {
                                    PreparedStatement s0 = conn.prepareStatement(
                                        currentStatement = "UPDATE " + dbObject + " SET p$$" + columnName + "_parent = " + STRCAT_PREFIX(conn) + " p$$" + columnName + "_parent__rid " + STRCAT_INFIX(conn) + " '/' " + STRCAT_INFIX(conn) + " p$$" + columnName + "_parent__oid " + STRCAT_SUFFIX(conn) + " WHERE p$$" + columnName + "_parent__rid IS NOT NULL"
                                    );
                                    System.out.println("  " + currentStatement);
                                    s0.executeUpdate();
                                    s0.close();
                                }
                                catch(Exception e) {
                                    System.out.println("ERROR: Can not update table " + dbObject + " (Reason: " + e.getMessage() + ")");
                                    hasErrors = true;
                                }
                                CheckDb.alterColumnVARCHAR(conn, dbObject, columnName, 250, true);
                                CheckDb.dropColumn(conn, dbObject, "p$$" + columnName + "__rid");
                                CheckDb.dropColumn(conn, dbObject, "p$$" + columnName + "__oid");
                                CheckDb.dropColumn(conn, dbObject, "p$$" + columnName + "_parent__rid");
                                CheckDb.dropColumn(conn, dbObject, "p$$" + columnName + "_parent__oid");
                            }
                        }
                        processedDbObjects.add(dbObject);
                        try {
                            rs.close();
                        } catch(Exception e) {}
                        try {
                            s.close();
                        } catch(Exception e) {}
                    }
                }            
                if(hasErrors) {
                    System.out.println("Phase 0: Can not convert object identities from format (rid,oid) to (id).");
                    return;
                }
            }
            // Phase 1: normalize tables
            if((startWithPhase == null) || (startWithPhase.intValue() <= 1)) {            
                System.out.println();
                System.out.println("Phase I: Normalizing tables");
                boolean hasErrors = false;
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    String dbObjectN = dbObjectsN[i];
                    if(
                        (dbObject != null) && (dbObject.length() > 0) &&
                        (dbObjectN != null) && (dbObjectN.length() > 0) &&
                        !processedDbObjects.contains(dbObject)
                    ) {
                        System.out.println("Phase I: Processing table " + i + " (" + dbObject + ")");                           
                        try {
                            // Get column names of dbObject
                            PreparedStatement s0 = conn.prepareStatement(
                                currentStatement = "SELECT * FROM " + dbObject + " WHERE 1=0"
                            );
                            System.out.println("  " + currentStatement);
                            ResultSet rs = s0.executeQuery();
                            FastResultSet frs = new FastResultSet(rs);
                            List columnNamesDbObject = new ArrayList(frs.getColumnNames());
                            rs.close();
                            s0.close();
                            // dbObject is already normalized
                            if(columnNamesDbObject.contains("object_idx")) {
                                // Get column names of dbObjectN
                                s0 = conn.prepareStatement(
                                    currentStatement = "SELECT * FROM " + dbObjectN + " WHERE 1=0"
                                );
                                System.out.println("  " + currentStatement);
                                rs = s0.executeQuery();
                                frs = new FastResultSet(rs);
                                List columnNamesDbObjectN = new ArrayList(frs.getColumnNames());
                                rs.close();
                                s0.close();      
                                // Normalize only if dbObject contains all columns of dbObjectSecondary
                                if(columnNamesDbObject.containsAll(columnNamesDbObjectN)) {
                                    // Move rows to dbObjectN
                                    String columnNames = "";
                                    for(Iterator j = columnNamesDbObjectN.iterator(); j.hasNext(); ) {
                                        String column = (String)j.next();
                                        if(columnNames.length() > 0) columnNames += ", ";
                                        columnNames += column;
                                    }
                                    s0 = conn.prepareStatement(
                                        currentStatement = "INSERT INTO " + dbObjectN + " SELECT " + columnNames + " FROM " + dbObject
                                    );                                    
                                    System.out.println("  " + currentStatement);
                                    s0.executeUpdate();
                                    s0.close();
                                    // Drop columnNamesDbObjectN from dbObject
                                    for(Iterator j = columnNamesDbObjectN.iterator(); j.hasNext(); ) {
                                        String columnName = (String)j.next();
                                        if(!"object_id".equals(columnName) && !"object_idx".equals(columnName) && !"p$$parent".equals(columnName)) {
                                            CheckDb.dropColumn(conn, dbObject, columnName);
                                        }
                                    }
                                    // Remove all rows in dbObject with object_idx > 0
                                    s0 = conn.prepareStatement(
                                        currentStatement = "DELETE FROM " + dbObject + " WHERE object_idx > 0"
                                    );
                                    System.out.println("  " + currentStatement);
                                    s0.executeUpdate();
                                    s0.close();
                                    // Drop object_idx
                                    String pkName = CheckDb.dropPrimaryKeyConstraint(conn, dbObject, renamedDbObjects);
                                    CheckDb.dropColumn(conn, dbObject, "object_idx");
                                    if(pkName != null) {
                                        CheckDb.addPrimaryKeyConstraint(conn, dbObject, pkName, "object_id");
                                    }                                    
                                }
                                else {                                    
                                    columnNamesDbObjectN.removeAll(columnNamesDbObject);
                                    System.out.println("ERROR: Can not normalize table " + dbObject + ". Missing columns " + columnNamesDbObjectN);
                                }
                            }                                    
                        }
                        catch(Exception e) {
                            System.out.println("ERROR: Can not update table " + dbObject + " (Reason: " + e.getMessage() + ")");
                            hasErrors = true;
                        }                            
                    }                        
                }
            }            
            System.out.println("INFO: Done");
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("statement: " + currentStatement);
        }
        catch (ServiceException e) {
            e.printStackTrace();
            System.out.println("statement: " + currentStatement);
        }
    }

   //-----------------------------------------------------------------------
    private static void updateSizeColumns1_11_0(
        Connection conn,
        String namespaceId,
        String[] dbObjects,
        Number startWithTable,
        Number endWithTable,
        Number startWithPhase
    ) {
        String currentStatement = null;
        
        try {
            String databaseProductName = conn.getMetaData().getDatabaseProductName();

            System.out.println();
            System.out.println("Processing namespace " + namespaceId + "(database=" + databaseProductName + ")");            
            
            // Phase 0: Update size and class columns required for version 1_11 schema
            if((startWithPhase == null) || (startWithPhase.intValue() <= 0)) {
                System.out.println();
                System.out.println("Phase 0: Updating size columns");
                boolean hasErrors = false;
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase 0: Processing table " + i + " (" + dbObject + ")");
                        try {
                            // Get column names of dbObject
                            PreparedStatement s0 = conn.prepareStatement(
                                currentStatement = "SELECT * FROM " + dbObject + " WHERE 1=0"
                            );
                            System.out.println("  " + currentStatement);
                            ResultSet rs = s0.executeQuery();
                            FastResultSet frs = new FastResultSet(rs);
                            List columnNamesDbObject = new ArrayList(frs.getColumnNames());
                            rs.close();
                            s0.close();
                            // Update size columns (columns ending with _)
                            for(Iterator j = columnNamesDbObject.iterator(); j.hasNext(); ) {
                                String columnName = (String)j.next();
                                if(columnName.endsWith("_")) {
                                    s0 = conn.prepareStatement(
                                        currentStatement = "UPDATE " + dbObject + " SET " + columnName + " = (SELECT CASE WHEN MAX(IDX) IS NULL THEN 0 ELSE MAX(IDX)+1 END AS N FROM " + dbObject + "_ WHERE (OBJECT_ID = " + dbObject + ".OBJECT_ID) AND (" + columnName.substring(0, columnName.length() - 1) + " IS NOT NULL))"
                                    );                        
                                    System.out.println("  " + currentStatement);
                                    s0.executeUpdate();
                                    s0.close();
                                }
                            }
                        }
                        catch(Exception e) {
                            System.out.println("ERROR: Can not process table " + dbObject + " (Reason: " + e.getMessage() + ")");
                            hasErrors = true;
                        }
                        processedDbObjects.add(dbObject);                        
                    }                        
                }
            }
            // Phase 0: Update dtype columns required for version 1_11 schema
            if((startWithPhase == null) || (startWithPhase.intValue() <= 1)) {
                System.out.println();
                System.out.println("Phase 1: Updating dtype columns");
                boolean hasErrors = false;
                Set processedDbObjects = new HashSet();
                for(
                    int i = (startWithTable == null ? 0 : startWithTable.intValue());
                    i < (endWithTable == null ? dbObjects.length : java.lang.Math.min(dbObjects.length, endWithTable.intValue() + 1));
                    i++
                ) {
                    String dbObject = dbObjects[i];
                    if((dbObject != null) && (dbObject.length() > 0) && !processedDbObjects.contains(dbObject)) {
                        System.out.println("Phase 1: Processing table " + i + " (" + dbObject + ")");
                        try {
                            // Update DTYPE column
                            PreparedStatement s0 = conn.prepareStatement(
                                currentStatement = "UPDATE " + dbObject + "_ SET DTYPE = (SELECT DTYPE FROM " + dbObject + " WHERE (OBJECT_ID = " + dbObject + "_.OBJECT_ID)) WHERE DTYPE = '#undef' OR DTYPE IS NULL"
                            );                        
                            System.out.println("  " + currentStatement);
                            s0.executeUpdate();
                            s0.close();
                        }
                        catch(Exception e) {
                            System.out.println("ERROR: Can not process table " + dbObject + " (Reason: " + e.getMessage() + ")");
                            hasErrors = true;
                        }
                        processedDbObjects.add(dbObject);                        
                    }                        
                }
            }                        
            System.out.println("INFO: Done");
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("statement: " + currentStatement);
        }
    }
    
   //-----------------------------------------------------------------------
    public static void main(
        String[] args
    ) {
        try {
            Context componentEnvironment = (Context)new InitialContext().lookup("java:comp/env");

            Class.forName((String)componentEnvironment.lookup("jdbcDriver"));
            Connection conn = DriverManager.getConnection(
                (String)componentEnvironment.lookup("jdbcUrl"),
                (String)componentEnvironment.lookup("username"),
                (String)componentEnvironment.lookup("password")
            );    
            conn.setAutoCommit(true);

            String command = (String)componentEnvironment.lookup("command");
            
            // convert-from-181-to-191
            if("convert-from-1_8_1-to-1_9_1".equals(command)) {
                convertFrom1_8_1To1_9_1(
                    conn, 
                    "kernel", 
                    ((Boolean)componentEnvironment.lookup("fixErrors")).booleanValue(),
                    KERNEL_TYPES_181,
                    KERNEL_DBOBJECTS_1_8_1,
                    KERNEL_TYPENAMES_181,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
                convertFrom1_8_1To1_9_1(
                    conn, 
                    "security", 
                    ((Boolean)componentEnvironment.lookup("fixErrors")).booleanValue(),
                    SECURITY_TYPES_181,
                    SECURITY_DBOBJECTS_1_8_1,
                    SECURITY_TYPENAMES_181,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
            }
            // convert-from-1_9_1-to-1_10_0
            else if("convert-from-1_9_1-to-1_10_0".equals(command)) {
                convertFrom1_9_1To1_10_0(
                    conn, 
                    "kernel", 
                    KERNEL_DBOBJECTS_1_9_1,
                    KERNEL_SECONDARY_DBOBJECTS_1_9_1,
                    KERNEL_SHARED_DBOBJECTS_1_9_1,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
                convertFrom1_9_1To1_10_0(
                    conn, 
                    "security", 
                    SECURITY_DBOBJECTS_1_9_1,
                    SECURITY_SECONDARY_DBOBJECTS_1_9_1,
                    SECURITY_SHARED_DBOBJECTS_1_9_1,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
            }
            // update-size-columns-1_11_0
            else if("update-size-columns-1_11_0".equals(command)) {
                updateSizeColumns1_11_0(
                    conn, 
                    "kernel", 
                    KERNEL_DBOBJECTS_1_11_0,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
                updateSizeColumns1_11_0(
                    conn, 
                    "security", 
                    SECURITY_DBOBJECTS_1_11_0,
                    (Number)componentEnvironment.lookup("startWithTable"),
                    (Number)componentEnvironment.lookup("endWithTable"),
                    (Number)componentEnvironment.lookup("startWithPhase")
                );
            }
            else {
                System.out.println("Unknown command " + command);
                System.out.println("Valid commands are:");
                System.out.println("* convert-from-1_8_1-to-1_9_1");
                System.out.println("* convert-from-1_9_1-to-1_10_0");
                System.out.println("* update-size-columns-1_11_0");
            }
        }
        catch(NamingException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    
    // Namespace kernel
    private static final Path[] KERNEL_TYPES_181 = new Path[]{
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uomSchedule/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/wfProcess/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.region1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.reservation1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/revenueReport/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/subscription/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/topic/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/emailRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/faxRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*/entry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/incidentParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/mailingRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/mmsRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/eMailAccount/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/contactRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/phoneCallRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/smsRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/taskParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/salesTaxType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/forecast/:*/forecastPeriod/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*/member/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/meetingParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/ouRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/forecast/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/incidentTransition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*/actionLog/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/budgetMilestone/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/trackerCategory/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/slide/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/trackerMilestone/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/productApplication/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/workRecord/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/vote/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/:*/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/:*/:*/:*/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/contactMembership/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/contactMembership/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/deliveryInformation/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/deliveryRequest/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/deliveryRequest/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/creditLimit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/creditLimit/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/member/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/quickAccess/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/accessHistory/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/trackerActivityLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/chart/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/productReference/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/audit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/activityLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/incidentTransition/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/replacement/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/replacement/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingText/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/definingProfile/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/relatedProduct/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*/descriptor/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/dependency/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/artifactContext/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/vp/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entityRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/depotType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingPeriod/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemBooking/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/revision/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productOffering/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productOffering/:*/productBundle/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productBundleType/:*/bundledProduct/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/basePrice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/:*/:*/basePrice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productBundleType/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/depotReference/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/depotReference/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*/configurationType/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*")
    };
    
    private static final String[] KERNEL_DBOBJECTS_1_8_1 = new String[]{
        "kernel_Organization",
        "kernel_UomSchedule",
        "kernel_Uom",
        "kernel_Contract",
        "kernel_Contract",
        "kernel_Contract",
        "kernel_Contract",
        "kernel_Contract",
        "kernel_ContractPosition",
        "kernel_ContractPosition",
        "kernel_ContractPosition",
        "kernel_ContractPosition",
        "kernel_ContractPosition",
        "kernel_WfProcess",
        "kernel_Product",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_Segment",
        "kernel_RevenueReport",
        "kernel_Subscription",
        "kernel_Activity",
        "kernel_Topic",
        "kernel_ActivityParty",
        "kernel_ActivityParty",
        "kernel_CodeValueEntry",
        "kernel_CodeValueContainer",
        "kernel_ActivityParty",
        "kernel_ActivityParty",
        "kernel_ActivityParty",
        "kernel_Document",
        "kernel_EMailAccount",
        "kernel_ContactRelationship",
        "kernel_ActivityParty",
        "kernel_ActivityParty",
        "kernel_Building",
        "kernel_ActivityParty",
        "kernel_SalesTaxType",
        "kernel_PriceLevel",
        "kernel_Competitor",
        "kernel_BuildingUnit",
        "kernel_ForecastPeriod",
        "kernel_AddressGroupMember",
        "kernel_ActivityParty",
        "kernel_ActivityTracker",
        "kernel_OrgUnitRelationship",
        "kernel_Budget",
        "kernel_Forecast",
        "kernel_IncidentTransition",
        "kernel_AddressGroup",
        "kernel_WfActionLogEntry",
        "kernel_BuildingComplex",
        "kernel_BudgetMilestone",
        "kernel_TrackerCategory",
        "kernel_Address",
        "kernel_Address",
        "kernel_Address",
        "kernel_MmsSlide",
        "kernel_Account",
        "kernel_WfProcessInstance",
        "kernel_OrganizationalUnit",
        "kernel_TrackerMilestone",
        "kernel_ProductApplication",
        "kernel_WorkRecord",
        "kernel_Vote",
        "kernel_AdditionalExternalLink",
        "kernel_AdditionalExternalLink",
        "kernel_AdditionalExternalLink",
        "kernel_AdditionalExternalLink",
        "kernel_Facility",
        "kernel_Facility",
        "kernel_ContactMembership",
        "kernel_ContactMembership",
        "kernel_DeliveryInformation",
        "kernel_DocumentAttachment",
        "kernel_DocumentAttachment",
        "kernel_DocumentAttachment",
        "kernel_DocumentAttachment",
        "kernel_Rating",
        "kernel_Rating",
        "kernel_Rating",
        "kernel_Rating",
        "kernel_Note",
        "kernel_Note",
        "kernel_Note",
        "kernel_Note",
        "kernel_DeliveryRequest",
        "kernel_DeliveryRequest",
        "kernel_CreditLimit",
        "kernel_CreditLimit",
        "kernel_Description",
        "kernel_Description",
        "kernel_Description",
        "kernel_Member",
        "kernel_Media",
        "kernel_Media",
        "kernel_Media",
        "kernel_Media",
        "kernel_UserHome",
        "kernel_Alert",
        "kernel_QuickAccess",
        "kernel_AccessHistory",
        "kernel_TrackerActivityLink",
        "kernel_Chart",
        "kernel_ProductReference",
        "kernel_Property",
        "kernel_Property",
        "kernel_Property",
        "kernel_AuditEntry",
        "kernel_ActivityLink",
        "kernel_IncidentTransition",
        "kernel_TemplateReplacement",
        "kernel_TemplateReplacement",
        "kernel_ModelElement",
        "kernel_BookingText",
        "kernel_RASProfile",
        "kernel_RelatedProduct",
        "kernel_RASClassificationElt",
        "kernel_RASDescriptor",
        "kernel_RASSolutionPart",
        "kernel_RASArtifactDep",
        "kernel_RASArtifactContext",
        "kernel_RASVariabilityPoint",
        "kernel_DepotEntity",
        "kernel_DepotEntityRel",
        "kernel_DepotType",
        "kernel_DepotGroup",
        "kernel_DepotHolder",
        "kernel_CompoundBooking",
        "kernel_BookingPeriod",
        "kernel_Booking",
        "kernel_Depot",
        "kernel_DepotPosition",
        "kernel_ProductConfiguration",
        "kernel_ProductConfiguration",
        "kernel_ProductConfiguration",
        "kernel_DepotReport",
        "kernel_DepotReportItem",
        "kernel_DepotReportItem",
        "kernel_DocumentFolder",
        "kernel_Media",
        "kernel_ProductOffering",
        "kernel_ProductBundle",
        "kernel_BundledProduct",
        "kernel_ProductBasePrice",
        "kernel_ProductBasePrice",
        "kernel_ProductBundleType",
        "kernel_DepotReference",
        "kernel_DepotReference",
        "kernel_ProductConfigTypeSet",
        "kernel_ProductConfiguration",
        null,
        null
    };
    
    private static final String[] KERNEL_DBOBJECTS_1_9_1 = new String[]{
        "dummy",        
        "kernel_Segment",
        "kernel_ProductClass",
        "kernel_ProductClassRel",
        "kernel_ComponentConfig",
        "kernel_CalculationRule",
        "kernel_ContractPosMod",
        "kernel_PricingRule",
        "kernel_PriceModifier",
        "kernel_ActivityGroupAss",
        "kernel_ActivityType",
        "kernel_ActivityProcess",
        "kernel_ActivityProcState",
        "kernel_ActivityProcTrans",
        "kernel_ActivityProcAction",
        "kernel_Calendar",
        "kernel_CalendarDay",
        "kernel_Resource",
        "kernel_ResourceAssignment",
        "kernel_ActivityCreator",
        "kernel_ActivityFilter",
        "kernel_FilterProperty",
        "kernel_ActivityGroup",
        "kernel_ActivityEffortEsti",
        "kernel_DepotReference",
        "kernel_ProductReference",
        "kernel_DepotEntity",
        "kernel_DepotEntityRel",
        "kernel_DepotType",
        "kernel_DepotGroup",
        "kernel_DepotHolder",
        "kernel_CompoundBooking",
        "kernel_BookingPeriod",
        "kernel_BookingText",
        "kernel_Booking",
        "kernel_Depot",
        "kernel_DepotPosition",
        "kernel_ProductConfTypeSet",
        "kernel_ProductConfig",
        "kernel_DepotReport",
        "kernel_DepotReportItem",
        "kernel_RASProfile",
        "kernel_RASClassificatiElt",
        "kernel_RASDescriptor",
        "kernel_RASSolutionPart",
        "kernel_RASArtifactDep",
        "kernel_RASArtifactContext",
        "kernel_RASVarPoint",
        "kernel_ModelElement",
        "kernel_TemplateRepl",
        "kernel_Activity",
        "kernel_ActivityParty",
        "kernel_WorkRecord",
        "kernel_Vote",
        "kernel_ActivityLink",
        "kernel_ActivityFollowUp",
        "kernel_AuditEntry",
        "kernel_EMailAccount",
        "kernel_Organization",
        "kernel_Subscription",
        "kernel_Topic",
        "kernel_Uom",
        "kernel_UomSchedule",
        "kernel_WfProcess",
        "kernel_Product",
        "kernel_RelatedProduct",
        "kernel_RevenueReport",
        "kernel_CodeValueEntry",
        "kernel_ProductBasePrice",
        "kernel_CodeValueContainer",
        "kernel_Document",
        "kernel_DocumentFolder",
        "kernel_ContactRel",
        "kernel_Building",
        "kernel_SalesTaxType",
        "kernel_Competitor",
        "kernel_BuildingUnit",
        "kernel_ForecastPeriod",
        "kernel_AddressGroupMember",
        "kernel_OrgUnitRelShip",
        "kernel_Budget",
        "kernel_Forecast",
        "kernel_Facility",
        "kernel_AddressGroup",
        "kernel_ContactMembership",
        "kernel_DeliveryInfo",
        "kernel_Contract",
        "kernel_ContractPosition",
        "kernel_DocumentAttachment",
        "kernel_PriceLevel",
        "kernel_WfActionLogEntry",
        "kernel_BuildingComplex",
        "kernel_DeliveryRequest",
        "kernel_BudgetMilestone",
        "kernel_Address",
        "kernel_MmsSlide",
        "kernel_Account",
        "kernel_WfProcessInstance",
        "kernel_OrganizationalUnit",
        "kernel_CreditLimit",
        "kernel_ProductApplication",
        "kernel_Property",
        "kernel_PropertySet",
        "kernel_AdditionalExtLink",
        "kernel_Rating",
        "kernel_Note",
        "kernel_Description",
        "kernel_AccountAssignment",
        "kernel_Media",
        "kernel_UserHome",
        "kernel_Alert",
        "kernel_QuickAccess",
        "kernel_AccessHistory",
        "kernel_Chart",
        "kernel_IndexEntry"
    };
    
    private static final String[] KERNEL_SECONDARY_DBOBJECTS_1_9_1 = new String[]{
        "dummy_N",        
        "kernel_Segment_N",
        "kernel_ProductClass_N",
        "kernel_ProductClassRel_N",
        "kernel_ComponentConfig_N",
        "kernel_CalculationRule_N",
        "kernel_ContractPosMod_N",
        "kernel_PricingRule_N",
        "kernel_PriceModifier_N",
        "kernel_ActivityGroupAss_N",
        "kernel_ActivityType_N",
        "kernel_ActivityProcess_N",
        "kernel_ActivityProcState_N",
        "kernel_ActivityProcTrans_N",
        "kernel_ActivityProcAction_N",
        "kernel_Calendar_N",
        "kernel_CalendarDay_N",
        "kernel_Resource_N",
        "kernel_ResourceAssignment_N",
        "kernel_ActivityCreator_N",
        "kernel_ActivityFilter_N",
        "kernel_FilterProperty_N",
        "kernel_ActivityGroup_N",
        "kernel_ActivityEffortEsti_N",
        "kernel_DepotReference_N",
        "kernel_ProductReference_N",
        "kernel_DepotEntity_N",
        "kernel_DepotEntityRel_N",
        "kernel_DepotType_N",
        "kernel_DepotGroup_N",
        "kernel_DepotHolder_N",
        "kernel_CompoundBooking_N",
        "kernel_BookingPeriod_N",
        "kernel_BookingText_N",
        "kernel_Booking_N",
        "kernel_Depot_N",
        "kernel_DepotPosition_N",
        "kernel_ProductConfTypeSet_N",
        "kernel_ProductConfig_N",
        "kernel_DepotReport_N",
        "kernel_DepotReportItem_N",
        "kernel_RASProfile_N",
        "kernel_RASClassificatiElt_N",
        "kernel_RASDescriptor_N",
        "kernel_RASSolutionPart_N",
        "kernel_RASArtifactDep_N",
        "kernel_RASArtifactContext_N",
        "kernel_RASVarPoint_N",
        "kernel_ModelElement_N",
        "kernel_TemplateRepl_N",
        "kernel_Activity_N",
        "kernel_ActivityParty_N",
        "kernel_WorkRecord_N",
        "kernel_Vote_N",
        "kernel_ActivityLink_N",
        "kernel_ActivityFollowUp_N",
        "kernel_AuditEntry_N",
        "kernel_EMailAccount_N",
        "kernel_Organization_N",
        "kernel_Subscription_N",
        "kernel_Topic_N",
        "kernel_Uom_N",
        "kernel_UomSchedule_N",
        "kernel_WfProcess_N",
        "kernel_Product_N",
        "kernel_RelatedProduct_N",
        "kernel_RevenueReport_N",
        "kernel_CodeValueEntry_N",
        "kernel_ProductBasePrice_N",
        "kernel_CodeValueContainer_N",
        "kernel_Document_N",
        "kernel_DocumentFolder_N",
        "kernel_ContactRel_N",
        "kernel_Building_N",
        "kernel_SalesTaxType_N",
        "kernel_Competitor_N",
        "kernel_BuildingUnit_N",
        "kernel_ForecastPeriod_N",
        "kernel_AddressGroupMember_N",
        "kernel_OrgUnitRelShip_N",
        "kernel_Budget_N",
        "kernel_Forecast_N",
        "kernel_Facility_N",
        "kernel_AddressGroup_N",
        "kernel_ContactMembership_N",
        "kernel_DeliveryInfo_N",
        "kernel_Contract_N",
        "kernel_ContractPosition_N",
        "kernel_DocumentAttachment_N",
        "kernel_PriceLevel_N",
        "kernel_WfActionLogEntry_N",
        "kernel_BuildingComplex_N",
        "kernel_DeliveryRequest_N",
        "kernel_BudgetMilestone_N",
        "kernel_Address_N",
        "kernel_MmsSlide_N",
        "kernel_Account_N",
        "kernel_WfProcessInstance_N",
        "kernel_OrganizationalUnit_N",
        "kernel_CreditLimit_N",
        "kernel_ProductApplication_N",
        "kernel_Property_N",
        "kernel_PropertySet_N",
        "kernel_AdditionalExtLink_N",
        "kernel_Rating_N",
        "kernel_Note_N",
        "kernel_Description_N",
        "kernel_AccountAssignment_N",
        "kernel_Media_N",
        "kernel_UserHome_N",
        "kernel_Alert_N",
        "kernel_QuickAccess_N",
        "kernel_AccessHistory_N",
        "kernel_Chart_N",
        "kernel_IndexEntry_N"
    };
    
    private static final List KERNEL_SHARED_DBOBJECTS_1_9_1 = Arrays.asList(
        new String[]{
            "kernel_ContractPosition",
            "kernel_Contract",
            "kernel_Product",
            "kernel_Segment",
            "kernel_ActivityParty",
            "kernel_ProductBasePrice",
            "kernel_AccountAssignment",
            "kernel_ProductApplication",
            "kernel_AdditionalExtLink",
            "kernel_Facility",
            "kernel_ContactMembership",
            "kernel_Address",
            "kernel_DeliveryInfo",
            "kernel_ActivityFilter",
            "kernel_FilterProperty",
            "kernel_DocumentAttachment",
            "kernel_Rating",
            "kernel_Note",
            "kernel_DeliveryRequest",
            "kernel_ActivityGroup",
            "kernel_CreditLimit",
            "kernel_Description",
            "kernel_Media",
            "kernel_Chart",
            "kernel_ProductReference",
            "kernel_TemplateRepl",
            "kernel_CalendarDay",
            "kernel_ProductConfig",
            "kernel_Property",
            "kernel_DepotReportItem",
            "kernel_DepotReference",
            "kernel_PropertySet",
            "kernel_ContractPosMod"
        }
    );
    
    private static final String[] KERNEL_TYPENAMES_181 = new String[]{
        "organization",
        "uomSchedule",
        "uom",
        "lead",
        "opportunity",
        "quote",
        "salesOrder",
        "invoice",
        "leadPos",
        "opportunityPos",
        "quotePos",
        "salesOrderPos",
        "invoicePos",
        "wfProcess",
        "product",
        "authentications",
        "docs",
        "buildings",
        "codes",
        "products",
        "admins",
        "regions",
        "reservations",
        "accounts",
        "models",
        "depots",
        "forecasts",
        "workflows",
        "uoms",
        "activities",
        "homes",
        "contracts",
        "realms",
        "identities",
        "authorizations",
        "revenueReport",
        "subscription",
        "activity",
        "topic",
        "emailRecipient",
        "faxRecipient",
        "valueContainerEntry",
        "valueContainer",
        "incidentParty",
        "mailingRecipient",
        "mmsRecipient",
        "doc",
        "eMailAccount",
        "contactRelationship",
        "phoneCallRecipient",
        "smsRecipient",
        "building",
        "taskParty",
        "salesTaxType",
        "priceLevel",
        "competitor",
        "buildingUnit",
        "forecastPeriod",
        "addressGroupMember",
        "meetingParty",
        "activityTracker",
        "ouRelationship",
        "budget",
        "forecast",
        "incidentTransition",
        "addressGroup",
        "actionLog",
        "buildingComplex",
        "budgetMilestone",
        "activityCategory",
        "address1",
        "address2",
        "address3",
        "activitySlide",
        "account",
        "wfProcessInstance",
        "organizationalUnit",
        "activityMilestone",
        "productApplication",
        "workRecord0",
        "vote",
        "externalLink1",
        "externalLink2",
        "externalLink3",
        "externalLink4",
        "facility1",
        "facility2",
        "oContactMembership",
        "ouContactMembership",
        "deliveryInfo",
        "doc1",
        "doc2",
        "doc3",
        "doc4",
        "rating1",
        "rating2",
        "rating3",
        "rating4",
        "note1",
        "note2",
        "note3",
        "note4",
        "deliveryRequest1",
        "deliveryRequest2",
        "organizationalUnitCreditLimit",
        "organizationCreditLimit",
        "descr1",
        "descr2",
        "descr3",
        "accountMember",
        "media1",
        "media2",
        "media3",
        "media4",
        "userHome",
        "alert",
        "quickAccess",
        "accessHistory",
        "trackerActivityLink",
        "chart",
        "productRef1",
        "p1",
        "p2",
        "p3",
        "audit",
        "activityLinkTo",
        "incidentTransition",
        "replacement1",
        "replacement2",
        "modelElement",
        "bookingText",
        "productDefiningProfile",
        "relatedProduct",
        "productCassificationElement",
        "classificationElementDescriptor",
        "productSolutionPart",
        "solutionPartDependency",
        "solutionPartArtifactContext",
        "solutionPartVp",
        "depotEntity",
        "depotEntityRelationship",
        "depotType",
        "depotGroup",
        "depotHolder",
        "cb",
        "bookingPeriod",
        "booking",
        "depot",
        "depotPos",
        "config1",
        "config2",
        "config3",
        "depotReport",
        "depotReportItemPosition",
        "depotReportItemBooking",
        "docFolder",
        "docRevision",
        "productOffering",
        "productBundle",
        "bundledProduct",
        "price1",
        "price2",
        "productBundleType",
        "depotRef1",
        "depotRef2",
        "configTypeSet",
        "configType",
        "realm",
        "principal"
    };

    // Namespace security
    private static final Path[] SECURITY_TYPES_181 = new Path[]{
        new Path("xri:@openmdx:org:opencrx:security:identity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*/permission/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/privilege/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*/credential/:*"),
        new Path("xri:@openmdx:org:openmdx.security.authentication1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org:openmdx.security.realm1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*")        
    };
    
    private static final String[] SECURITY_DBOBJECTS_1_8_1 = new String[]{
        "security_Segment",
        "security_Realm",
        "security_Principal",
        "security_Policy",
        "security_Role",
        "security_Permission",
        "security_Privilege",
        "security_Subject",
        "security_Credential",
        "security_Segment",
        "security_Segment",
        "security_Segment"        
    };
    
    private static final String[] SECURITY_DBOBJECTS_1_9_1 = new String[]{
        "security_Segment",
        "security_Realm",
        "security_Principal",
        "security_Policy",
        "security_Role",
        "security_Permission",
        "security_Privilege",
        "security_Subject",
        "security_Credential"
    };
    
    private static final String[] SECURITY_SECONDARY_DBOBJECTS_1_9_1 = new String[]{
        null,
        "security_Realm_N",
        "security_Principal_N",
        "security_Policy_N",
        "security_Role_N",
        "security_Permission_N",
        "security_Privilege_N",
        "security_Subject_N",
        "security_Credential_N"
    };
    
    private static final List SECURITY_SHARED_DBOBJECTS_1_9_1 = Arrays.asList(
        new String[]{
            "security_Segment"
        }
    );
    
    private static final String[] SECURITY_TYPENAMES_181 = new String[]{
        "identities",
        "realm",
        "principal",
        "policy",
        "role",
        "permission",
        "privilege",
        "subject",
        "credential",
        "authentications",
        "realms",
        "authorizations"        
    };

    private static final String[] KERNEL_DBOBJECTS_1_11_0 = new String[]{
        "OOCKE1_ACCESSHISTORY",
        "OOCKE1_ACCOUNT",
        "OOCKE1_ACCOUNTASSIGNMENT",
        "OOCKE1_ACTIVITY",
        "OOCKE1_ACTIVITYCREATOR",
        "OOCKE1_ACTIVITYEFFORTESTI",
        "OOCKE1_ACTIVITYFOLLOWUP",
        "OOCKE1_ACTIVITYGROUP",
        "OOCKE1_ACTIVITYGROUPASS",
        "OOCKE1_ACTIVITYLINK",
        "OOCKE1_ACTIVITYPARTY",
        "OOCKE1_ACTIVITYPROCACTION",
        "OOCKE1_ACTIVITYPROCESS",
        "OOCKE1_ACTIVITYPROCSTATE",
        "OOCKE1_ACTIVITYPROCTRANS",
        "OOCKE1_ACTIVITYTYPE",
        "OOCKE1_ADDITIONALEXTLINK",
        "OOCKE1_ADDRESS",
        "OOCKE1_ADDRESSGROUP",
        "OOCKE1_ADDRESSGROUPMEMBER",
        "OOCKE1_ALERT",
        "OOCKE1_AUDITENTRY",
        "OOCKE1_BOOKING",
        "OOCKE1_BOOKINGPERIOD",
        "OOCKE1_BOOKINGTEXT",
        "OOCKE1_BUDGET",
        "OOCKE1_BUDGETMILESTONE",
        "OOCKE1_BUILDINGUNIT",
        "OOCKE1_CALCULATIONRULE",
        "OOCKE1_CALENDAR",
        "OOCKE1_CALENDARDAY",
        "OOCKE1_CHART",
        "OOCKE1_CODEVALUECONTAINER",
        "OOCKE1_CODEVALUEENTRY",
        "OOCKE1_COMPETITOR",
        "OOCKE1_COMPONENTCONFIG",
        "OOCKE1_COMPOUNDBOOKING",
        "OOCKE1_CONTACTMEMBERSHIP",
        "OOCKE1_CONTACTREL",
        "OOCKE1_CONTACTROLE",
        "OOCKE1_CONTRACT",
        "OOCKE1_CONTRACTPOSITION",
        "OOCKE1_CONTRACTPOSMOD",
        "OOCKE1_CREDITLIMIT",
        "OOCKE1_DELIVERYINFO",
        "OOCKE1_DELIVERYREQUEST",
        "OOCKE1_DEPOT",
        "OOCKE1_DEPOTENTITY",
        "OOCKE1_DEPOTENTITYREL",
        "OOCKE1_DEPOTGROUP",
        "OOCKE1_DEPOTHOLDER",
        "OOCKE1_DEPOTPOSITION",
        "OOCKE1_DEPOTREFERENCE",
        "OOCKE1_DEPOTREPORT",
        "OOCKE1_DEPOTREPORTITEM",
        "OOCKE1_DEPOTTYPE",
        "OOCKE1_DESCRIPTION",
        "OOCKE1_DOCUMENT",
        "OOCKE1_DOCUMENTATTACHMENT",
        "OOCKE1_DOCUMENTFOLDER",
        "OOCKE1_EMAILACCOUNT",
        "OOCKE1_EVENT",
        "OOCKE1_EVENTPART",
        "OOCKE1_EVENTSLOT",
        "OOCKE1_FACILITY",
        "OOCKE1_FILTER",
        "OOCKE1_FILTERPROPERTY",
        "OOCKE1_FORECAST",
        "OOCKE1_FORECASTPERIOD",
        "OOCKE1_INDEXENTRY",
        "OOCKE1_INVENTORYITEM",
        "OOCKE1_LINKABLEITEMLINK",
        "OOCKE1_MEDIA",
        "OOCKE1_MMSSLIDE",
        "OOCKE1_MODELELEMENT",
        "OOCKE1_NOTE",
        "OOCKE1_ORGANIZATION",
        "OOCKE1_ORGANIZATIONALUNIT",
        "OOCKE1_ORGUNITRELSHIP",
        "OOCKE1_PRICELEVEL",
        "OOCKE1_PRICEMODIFIER",
        "OOCKE1_PRICINGRULE",
        "OOCKE1_PRODUCT",
        "OOCKE1_PRODUCTAPPLICATION",
        "OOCKE1_PRODUCTBASEPRICE",
        "OOCKE1_PRODUCTCLASS",
        "OOCKE1_PRODUCTCLASSREL",
        "OOCKE1_PRODUCTCONFIG",
        "OOCKE1_PRODUCTCONFTYPESET",
        "OOCKE1_PRODUCTREFERENCE",
        "OOCKE1_PROPERTY",
        "OOCKE1_PROPERTYSET",
        "OOCKE1_QUICKACCESS",
        "OOCKE1_RASARTIFACTCONTEXT",
        "OOCKE1_RASARTIFACTDEP",
        "OOCKE1_RASCLASSIFICATIELT",
        "OOCKE1_RASDESCRIPTOR",
        "OOCKE1_RASPROFILE",
        "OOCKE1_RASSOLUTIONPART",
        "OOCKE1_RASVARPOINT",
        "OOCKE1_RATING",
        "OOCKE1_RELATEDPRODUCT",
        "OOCKE1_RESOURCE",
        "OOCKE1_RESOURCEASSIGNMENT",
        "OOCKE1_REVENUEREPORT",
        "OOCKE1_SALESTAXTYPE",
        "OOCKE1_SEGMENT",
        "OOCKE1_SUBSCRIPTION",
        "OOCKE1_TEMPLATEREPL",
        "OOCKE1_TOPIC",
        "OOCKE1_UOM",
        "OOCKE1_UOMSCHEDULE",
        "OOCKE1_USERHOME",
        "OOCKE1_VOTE",
        "OOCKE1_WFACTIONLOGENTRY",
        "OOCKE1_WFPROCESS",
        "OOCKE1_WFPROCESSINSTANCE",
        "OOCKE1_WORKRECORD"
    };
    
    private static final String[] SECURITY_DBOBJECTS_1_11_0 = new String[]{
        "OOCSE1_AUTHENTICATIONCONTEXT",
        "OOCSE1_CREDENTIAL",
        "OOCSE1_PERMISSION",
        "OOCSE1_POLICY",
        "OOCSE1_PRINCIPAL",
        "OOCSE1_PRIVILEGE",
        "OOCSE1_REALM",
        "OOCSE1_ROLE",
        "OOCSE1_SEGMENT",
        "OOCSE1_SUBJECT"
    };
    
}

//---------------------------------------------------------------------------

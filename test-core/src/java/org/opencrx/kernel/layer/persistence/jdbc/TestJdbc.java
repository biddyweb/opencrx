/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestJdbc.java,v 1.4 2008/06/30 14:17:47 wfro Exp $
 * Description: TestJdo
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/06/30 14:17:47 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.layer.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestJdbc
    extends TestCase {

    //-----------------------------------------------------------------------
    /**
     * Constructs a test case with the given name.
     */
    public TestJdbc(
        String name
    ) throws SQLException, ClassNotFoundException {
        super(name);
        // Postgres
        Class.forName("org.postgresql.Driver");        
        String url = "jdbc:postgresql://localhost/CRX_2_1?user=postgres&password=manager99";
//        // SQL Server
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");        
//        String url = "jdbc:sqlserver://localhost:1433;databaseName=CRX_2_1;user=system;password=manager;selectMethod=cursor";
//        // DB2
//        Class.forName("com.ibm.db2.jcc.DB2Driver");        
//        String url = "jdbc:db2://10.1.1.199:50000/OB2_CRX";
        this.conn = DriverManager.getConnection(url);        
    }
    
    //-----------------------------------------------------------------------
    /**
     * The batch TestRunner can be given a class to run directly.
     * To start the batch runner from your main you can write: 
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }
    
    //-----------------------------------------------------------------------
    /**
     * A test runner either expects a static method suite as the
     * entry point to get a test to run or it will extract the 
     * suite automatically. 
     */
    public static Test suite() {
        return new TestSuite(TestJdbc.class);
    }

    //-----------------------------------------------------------------------
    public void testPerformance(
    ) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // No placeholders
            ps = conn.prepareStatement(
                "SELECT  v.*, v.dtype AS p$$dtype, v.name AS p$$name FROM OOCKE1_PROPERTY v WHERE (1=1) AND (v.object_id LIKE 'p2/org:opencrx:kernel:product1/CRX/Standard/configurationTypeSet/pjK.0ODdEdy6L6-o7K4cog/configurationType/sexNQODdEdy6L6-o7K4cog/%') AND " + 
                "(((v.dtype IN ('org:opencrx:kernel:base:ReferenceProperty', 'org:opencrx:kernel:base:DateTimeProperty, org:opencrx:kernel:base:BooleanProperty', 'org:opencrx:kernel:base:DateProperty', 'org:opencrx:kernel:base:IntegerProperty', 'org:opencrx:kernel:base:UriProperty', 'org:opencrx:kernel:base:StringProperty', 'org:opencrx:kernel:base:Property', 'org:opencrx:kernel:base:DecimalProperty')))) " + 
                "AND (v.object_id  IN (SELECT object_id FROM OOCKE1_PROPERTY_ v  WHERE ((((v.owner IN ('Standard:Administrators', 'Standard:Users', 'Standard:Unassigned', 'Standard:Unspecified', 'Standard:drupal-Standard.Group', 'Standard:drupal-Standard', 'Standard:drupal-Standard.User'))))))) ORDER BY v.name",                                        
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE                
            );
            ps.setFetchSize(500);
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);            
            long startAt = System.currentTimeMillis();
            rs = ps.executeQuery();
            long duration = System.currentTimeMillis() - startAt;
            System.out.println("No placeholders execution time (ms) " + duration);
            rs.close();
            ps.close();
            // Placeholders 1
            ps = conn.prepareStatement(
                "SELECT  v.*, v.dtype AS p$$dtype, v.name AS p$$name FROM OOCKE1_PROPERTY v WHERE (1=1) AND (v.object_id LIKE ?) AND " + 
                "(((v.dtype IN (?, ?, ?, ?, ?, ?, ?, ?, ?)))) " + 
                "AND (v.object_id  IN (SELECT object_id FROM OOCKE1_PROPERTY_ v  WHERE ((((v.owner IN (?, ?, ?, ?, ?, ?, ?))))))) ORDER BY v.name",
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE                
            );         
            ps.setFetchSize(500);
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);            
            ps.setString(1, "p2/org:opencrx:kernel:product1/CRX/Standard/configurationTypeSet/pjK.0ODdEdy6L6-o7K4cog/configurationType/sexNQODdEdy6L6-o7K4cog/%");
            ps.setString(2, "org:opencrx:kernel:base:ReferenceProperty");
            ps.setString(3, "org:opencrx:kernel:base:DateTimePropery");
            ps.setString(4, "org:opencrx:kernel:base:BooleanProperty");
            ps.setString(5, "org:opencrx:kernel:base:DateProperty");
            ps.setString(6, "org:opencrx:kernel:base:IntegerProperty");
            ps.setString(7, "org:opencrx:kernel:base:UriProperty");
            ps.setString(8, "org:opencrx:kernel:base:StringProperty");
            ps.setString(9, "org:opencrx:kernel:base:Property");
            ps.setString(10, "org:opencrx:kernel:base:DecimalProperty");
            ps.setString(11, "Standard:Administrators");
            ps.setString(12, "Standard:Users");
            ps.setString(13, "Standard:Unassigned");
            ps.setString(14, "Standard:Unspecified");
            ps.setString(15, "Standard:drupal-Standard.Group");
            ps.setString(16, "Standard:drupal-Standard");
            ps.setString(17, "Standard:drupal-Standard.User");            
            startAt = System.currentTimeMillis();
            rs = ps.executeQuery();
            duration = System.currentTimeMillis() - startAt;
            System.out.println("Placeholders execution time (ms) " + duration);
            rs.close();
            // Placeholders 2
            ps.clearParameters();
            ps.setFetchSize(500);
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);            
            ps.setString(1, "p2/org:opencrx:kernel:product1/CRX/Standard/configurationTypeSet/pjK.0ODdEdy6L6-o7K4cog/configurationType/sexNQODdEdy6L6-o7K4cog/%");
            ps.setString(2, "org:opencrx:kernel:base:ReferenceProperty");
            ps.setString(3, "org:opencrx:kernel:base:DateTimePropery");
            ps.setString(4, "org:opencrx:kernel:base:BooleanProperty");
            ps.setString(5, "org:opencrx:kernel:base:DateProperty");
            ps.setString(6, "org:opencrx:kernel:base:IntegerProperty");
            ps.setString(7, "org:opencrx:kernel:base:UriProperty");
            ps.setString(8, "org:opencrx:kernel:base:StringProperty");
            ps.setString(9, "org:opencrx:kernel:base:Property");
            ps.setString(10, "org:opencrx:kernel:base:DecimalProperty");
            ps.setString(11, "Standard:Administrators");
            ps.setString(12, "Standard:Users");
            ps.setString(13, "Standard:Unassigned");
            ps.setString(14, "Standard:Unspecified");
            ps.setString(15, "Standard:drupal-Standard.Group");
            ps.setString(16, "Standard:drupal-Standard");
            ps.setString(17, "Standard:drupal-Standard.User");            
            startAt = System.currentTimeMillis();
            rs = ps.executeQuery();
            duration = System.currentTimeMillis() - startAt;
            System.out.println("Placeholders execution time (ms) " + duration);        
            rs.close();
            ps.close();
        }
        finally {
            if(ps != null) {
                try { ps.close(); } catch(Exception e) {}
            }
            if(rs != null) {
                try { rs.close(); } catch(Exception e) {}
            }
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static Connection conn = null;
}

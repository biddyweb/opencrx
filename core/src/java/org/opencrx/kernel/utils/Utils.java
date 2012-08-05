/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Utils.java,v 1.28 2009/03/08 17:04:48 wfro Exp $
 * Description: Utils
 * Revision:    $Revision: 1.28 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/08 17:04:48 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jmi.reflect.RefObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.admin1.jmi1.Admin1Package;
import org.opencrx.kernel.building1.jmi1.Building1Package;
import org.opencrx.kernel.contract1.jmi1.Contract1Package;
import org.opencrx.kernel.depot1.jmi1.Depot1Package;
import org.opencrx.kernel.forecast1.jmi1.Forecast1Package;
import org.opencrx.kernel.generic.jmi1.GenericPackage;
import org.opencrx.kernel.home1.jmi1.Home1Package;
import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.opencrx.kernel.uom1.jmi1.Uom1Package;
import org.opencrx.security.realm1.jmi1.Realm1Package;
import org.openmdx.application.dataprovider.transport.cci.Dataprovider_1ConnectionFactory;
import org.openmdx.application.dataprovider.transport.ejb.cci.Dataprovider_1ConnectionFactoryImpl;
import org.openmdx.application.dataprovider.transport.ejb.cci.Jmi1AccessorFactory_1;
import org.openmdx.application.dataprovider.transport.ejb.cci.Jmi1AccessorFactory_2;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.Authority;
import org.openmdx.base.mof.cci.Model_1_3;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.kernel.persistence.cci.ConfigurableProperty;

public class Utils {

    //-----------------------------------------------------------------------
    public static Model_1_3 getModel(
    ) {
        Model_1_3 model = null;
        try {
            model = Model_1Factory.getModel();
        }
        catch(Exception e) {
            System.out.println("Can not initialize model repository " + e.getMessage());
            System.out.println(new ServiceException(e).getCause());
        }
        return model;
    }
        
    //-----------------------------------------------------------------------
    public static PersistenceManagerFactory getPersistenceManagerFactory(
    ) throws NamingException, ServiceException {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put(
            ConfigurableProperty.ConnectionFactoryName.qualifiedName(), 
            "java:comp/env/ejb/EntityManagerFactory"
        );
        properties.put(
            ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(), 
            Jmi1AccessorFactory_2.class.getName()
        );
        return JDOHelper.getPersistenceManagerFactory(properties);
    }
    
    //-----------------------------------------------------------------------
    private static javax.jmi.reflect.RefPackage getOutermostPackage(
        PersistenceManager pm
    ) {
        return ((RefObject)pm.newInstance(org.opencrx.kernel.account1.jmi1.Segment.class)).refOutermostPackage();            
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.workflow1.jmi1.Workflow1Package getWorkflowPackage(
        PersistenceManager pm
    ) {
        org.opencrx.kernel.workflow1.jmi1.Workflow1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.workflow1.jmi1.Workflow1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:workflow1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.opencrx.kernel.workflow1.jmi1.Workflow1Package)pm.getObjectById(
                Authority.class,
                org.opencrx.kernel.workflow1.jmi1.Workflow1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.code1.jmi1.Code1Package getCodePackage(
        PersistenceManager pm
    ) {
        org.opencrx.kernel.code1.jmi1.Code1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.code1.jmi1.Code1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:code1");
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.opencrx.kernel.code1.jmi1.Code1Package)pm.getObjectById(
                Authority.class,
                org.opencrx.kernel.code1.jmi1.Code1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.document1.jmi1.Document1Package getDocumentPackage(
        PersistenceManager pm
    ) {
        org.opencrx.kernel.document1.jmi1.Document1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.document1.jmi1.Document1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:document1");
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.opencrx.kernel.document1.jmi1.Document1Package)pm.getObjectById(
                Authority.class,
                org.opencrx.kernel.document1.jmi1.Document1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;            
    }

    //-----------------------------------------------------------------------
    public static Admin1Package getAdminPackage(
        PersistenceManager pm
    ) {
        Admin1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.admin1.jmi1.Admin1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:admin1"); 
        }
        catch(UnsupportedOperationException e) {
            pkg = (Admin1Package)pm.getObjectById(
                Authority.class,
                Admin1Package.AUTHORITY_XRI
            ).refImmediatePackage();            
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static org.openmdx.base.jmi1.BasePackage getOpenMdxBasePackage(
        PersistenceManager pm
    ) {
        org.openmdx.base.jmi1.BasePackage pkg = null;
        try {
            pkg = (org.openmdx.base.jmi1.BasePackage)Utils.getOutermostPackage(pm).refPackage("org:openmdx:base"); 
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.openmdx.base.jmi1.BasePackage )pm.getObjectById(
                Authority.class,
                org.openmdx.base.jmi1.BasePackage .AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Home1Package getHomePackage(
        PersistenceManager pm
    ) {
        Home1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.home1.jmi1.Home1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:home1"); 
        }
        catch(UnsupportedOperationException e) {
            pkg = (Home1Package)pm.getObjectById(
                Authority.class,
                Home1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;            
    }

    //-----------------------------------------------------------------------
    public static Contract1Package getContractPackage(
        PersistenceManager pm
    ) {
        Contract1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.contract1.jmi1.Contract1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:contract1"); 
        }
        catch(UnsupportedOperationException e) {
            pkg = (Contract1Package)pm.getObjectById(
                Authority.class,
                Contract1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;            
    }

    //-----------------------------------------------------------------------
    public static Depot1Package getDepotPackage(
        PersistenceManager pm
    ) {
        Depot1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.depot1.jmi1.Depot1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:depot1"); 
        }
        catch(UnsupportedOperationException e) {
            pkg = (Depot1Package)pm.getObjectById(
                Authority.class,
                Depot1Package.AUTHORITY_XRI
            ).refImmediatePackage();            
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static org.openmdx.compatibility.datastore1.jmi1.Datastore1Package getDatastorePackage(
        PersistenceManager pm
    ) {
        org.openmdx.compatibility.datastore1.jmi1.Datastore1Package pkg = null;
        try {
            pkg = (org.openmdx.compatibility.datastore1.jmi1.Datastore1Package)Utils.getOutermostPackage(pm).refPackage("org:openmdx:compatibility:datastore1");
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.openmdx.compatibility.datastore1.jmi1.Datastore1Package)pm.getObjectById(
                Authority.class,
                org.openmdx.compatibility.datastore1.jmi1.Datastore1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;                    
    }
    
    //-----------------------------------------------------------------------
    public static Building1Package getBuildingPackage(
        PersistenceManager pm
    ) {
        Building1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.building1.jmi1.Building1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:building1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (Building1Package)pm.getObjectById(
                Authority.class,
                Building1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Product1Package getProductPackage(
        PersistenceManager pm
    ) {
        Product1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.product1.jmi1.Product1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:product1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (Product1Package)pm.getObjectById(
                Authority.class,
                Product1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Uom1Package getUomPackage(
        PersistenceManager pm
    ) {
        Uom1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.uom1.jmi1.Uom1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:uom1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (Uom1Package)pm.getObjectById(
                Authority.class,
                Uom1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Realm1Package getRealmPackage(
        PersistenceManager pm
    ) {
        Realm1Package pkg = null;
        try {
            pkg = (org.opencrx.security.realm1.jmi1.Realm1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:security:realm1");           
        }
        catch(UnsupportedOperationException e) {
            pkg =(Realm1Package)pm.getObjectById(
                Authority.class,
                Realm1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.base.jmi1.BasePackage getBasePackage(
        PersistenceManager pm
    ) {
        org.opencrx.kernel.base.jmi1.BasePackage pkg = null;
        try {
            pkg = (org.opencrx.kernel.base.jmi1.BasePackage)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:base");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (org.opencrx.kernel.base.jmi1.BasePackage)pm.getObjectById(
                Authority.class,
                org.opencrx.kernel.base.jmi1.BasePackage.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Activity1Package getActivityPackage(
        PersistenceManager pm
    ) {
        Activity1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.activity1.jmi1.Activity1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:activity1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (Activity1Package)pm.getObjectById(
                Authority.class,
                Activity1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Account1Package getAccountPackage(
        PersistenceManager pm
    ) {
        Account1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.account1.jmi1.Account1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:account1");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (Account1Package)pm.getObjectById(
                Authority.class,
                Account1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static Forecast1Package getForecastPackage(
        PersistenceManager pm
    ) {
        Forecast1Package pkg = null;
        try {
            pkg = (org.opencrx.kernel.forecast1.jmi1.Forecast1Package)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:forecast1");            
        }
        catch(Exception e) {
            pkg = (Forecast1Package)pm.getObjectById(
                Authority.class,
                Forecast1Package.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-----------------------------------------------------------------------
    public static GenericPackage getGenericPackage(
        PersistenceManager pm
    ) {
        GenericPackage pkg = null;
        try {
            pkg = (org.opencrx.kernel.generic.jmi1.GenericPackage)Utils.getOutermostPackage(pm).refPackage("org:opencrx:kernel:generic");            
        }
        catch(UnsupportedOperationException e) {
            pkg = (GenericPackage)pm.getObjectById(
                Authority.class,
                GenericPackage.AUTHORITY_XRI
            ).refImmediatePackage();
        }
        return pkg;
    }

    //-------------------------------------------------------------------------
    public static String toFilename(
        String s
    ) {
        s = s.replace(' ', '-');
        s = s.replace(',', '-');
        s = s.replace('/', '-');
        s = s.replace('\\', '-');
        s = s.replace('=', '-');
        s = s.replace('%', '-');
        s = s.replace(':', '-');
        s = s.replace('*', '-');
        s = s.replace('?', '-');
        s = s.replace('+', '-');
        s = s.replace('(', '-');
        s = s.replace(')', '-');
        s = s.replace('<', '-');
        s = s.replace('>', '-');
        s = s.replace('|', '-');
        s = s.replace('"', '-');
        s = s.replace('\'', '-');
        s = s.replace('&', '-');
        s = s.replace('.', '-');
        s = s.replace('#', '-');
        s = s.replace("-", "");
        if(s.length() > 50) {
            s = s.substring(0, 44) + s.substring(s.length()-5);
        }
        return s;
    }
        
    //-----------------------------------------------------------------------
    public static String getPasswordDigest(
        String password,
        String algorithm
    ) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(password.getBytes("UTF-8"));
            return "{" + algorithm + "}" + org.openmdx.base.text.conversion.Base64.encode(md.digest());
        }
        catch(NoSuchAlgorithmException e) {
        }
        catch(UnsupportedEncodingException e) {
        }
        return null;
    }
    
    //-----------------------------------------------------------------------
    public static PersistenceManagerFactory getRemotePersistenceManagerFactory(
    ) throws NamingException, ServiceException {
        Context initialContext = new InitialContext();
        Map<String, Object> configuration = new HashMap<String, Object>();
        configuration.put(
            Dataprovider_1ConnectionFactory.class.getName(),
            new Dataprovider_1ConnectionFactoryImpl(
                initialContext,
                "data",
                new String[]{"java:comp/env/ejb"}
            )
        );
        configuration.put(
            ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            Jmi1AccessorFactory_1.class.getName()
        );
        configuration.put(
            ConfigurableProperty.Optimistic.qualifiedName(),
            Boolean.TRUE.toString()
        );
        configuration.put(
            ConfigurableProperty.BindingPackageSuffix.qualifiedName(),
            "jmi1"
        );
        return JDOHelper.getPersistenceManagerFactory(configuration);
    }

    //-------------------------------------------------------------------------
    public static boolean areEqual(
        Object v1,
        Object v2
    ) {
        if(v1 == null) return v2 == null;
        if(v2 == null) return v1 == null;
        if(
            (v1 instanceof Comparable) && 
            (v2 instanceof Comparable) &&
            (v1.getClass().equals(v2.getClass()))
        ) {
            return ((Comparable)v1).compareTo(v2) == 0;
        }
        return v1.equals(v2);
    }

}

//--- End of File -----------------------------------------------------------

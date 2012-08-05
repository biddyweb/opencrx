/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Utils.java,v 1.8 2007/12/18 16:35:08 wfro Exp $
 * Description: Utils
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/18 16:35:08 $
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
package org.opencrx.kernel.workflow.servlet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.admin1.jmi1.Admin1Package;
import org.opencrx.kernel.generic.jmi1.GenericPackage;
import org.opencrx.kernel.home1.jmi1.Home1Package;
import org.openmdx.base.accessor.jmi.spi.PersistenceManagerFactory_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.Authority;
import org.openmdx.base.object.jdo.ConfigurableProperties_2_0;
import org.openmdx.compatibility.application.dataprovider.transport.ejb.cci.Dataprovider_1ConnectionFactoryImpl;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Dataprovider_1ConnectionFactory;
import org.openmdx.compatibility.datastore1.jmi1.Datastore1Package;
import org.openmdx.model1.accessor.basic.cci.Model_1_3;
import org.openmdx.model1.accessor.basic.spi.Model_1;
import org.openmdx.model1.mapping.Names;


public class Utils {

    //-----------------------------------------------------------------------
    public static Model_1_3 createModel(
    ) {
        Model_1_3 model = null;
        try {
            model = new Model_1();
            model.addModels(MODEL_PACKAGES);
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
            ConfigurableProperties_2_0.FACTORY_CLASS,
            PersistenceManagerFactory_1.class.getName()
        );
        configuration.put(
            ConfigurableProperties_2_0.OPTIMISTIC,
            Boolean.TRUE.toString()
        );
        configuration.put(
            ConfigurableProperties_2_0.BINDING_PACKAGE_SUFFIX,
            Names.JMI1_PACKAGE_SUFFIX
        );        
        return JDOHelper.getPersistenceManagerFactory(configuration);
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.workflow1.jmi1.Workflow1Package getWorkflowPackage(
        PersistenceManager pm
    ) {
        return (org.opencrx.kernel.workflow1.jmi1.Workflow1Package)((Authority)pm.getObjectById(
            Authority.class,
            org.opencrx.kernel.workflow1.jmi1.Workflow1Package.AUTHORITY_XRI
        )).refImmediatePackage();
    }

    //-----------------------------------------------------------------------
    public static Admin1Package getAdminPackage(
        PersistenceManager pm
    ) {
        return (Admin1Package)((Authority)pm.getObjectById(
            Authority.class,
            Admin1Package.AUTHORITY_XRI
        )).refImmediatePackage();
    }

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.base.jmi1.BasePackage getOpenCrxBasePackage(
        PersistenceManager pm
    ) {
        return (org.opencrx.kernel.base.jmi1.BasePackage)((Authority)pm.getObjectById(
            Authority.class,
            org.opencrx.kernel.base.jmi1.BasePackage.AUTHORITY_XRI
        )).refImmediatePackage();
    }

    //-----------------------------------------------------------------------
    public static org.openmdx.base.jmi1.BasePackage getOpenMdxBasePackage(
        PersistenceManager pm
    ) {
        return (org.openmdx.base.jmi1.BasePackage)((Authority)pm.getObjectById(
            Authority.class,
            org.openmdx.base.jmi1.BasePackage.AUTHORITY_XRI
        )).refImmediatePackage();
    }

    //-----------------------------------------------------------------------
    public static Datastore1Package getDatastoreBasePackage(
        PersistenceManager pm
    ) {
        return (Datastore1Package)((Authority)pm.getObjectById(
            Authority.class,
            Datastore1Package.AUTHORITY_XRI
        )).refImmediatePackage();     
    }

    //-----------------------------------------------------------------------
    public static Home1Package getHomePackage(
        PersistenceManager pm
    ) {
        return (Home1Package)((Authority)pm.getObjectById(
            Authority.class,
            Home1Package.AUTHORITY_XRI
        )).refImmediatePackage();     
    }

    //-----------------------------------------------------------------------
    public static Activity1Package getActivityPackage(
        PersistenceManager pm
    ) {
        return (Activity1Package)((Authority)pm.getObjectById(
            Authority.class,
            Activity1Package.AUTHORITY_XRI
        )).refImmediatePackage();     
    }

    //-----------------------------------------------------------------------
    public static Account1Package getAccountPackage(
        PersistenceManager pm
    ) {
        return (Account1Package)((Authority)pm.getObjectById(
            Authority.class,
            Account1Package.AUTHORITY_XRI
        )).refImmediatePackage();     
    }

    //-----------------------------------------------------------------------
    public static GenericPackage getGenericPackage(
        PersistenceManager pm
    ) {
        return (GenericPackage)((Authority)pm.getObjectById(
            Authority.class,
            GenericPackage.AUTHORITY_XRI
        )).refImmediatePackage();     
    }

    //-----------------------------------------------------------------------
    public static String xriAsIdentityPattern(
        String xri
    ) {
        xri = xri.replace(".", "\\.");
        xri = xri.replace("**", ".*");            
        return xri;
    }

    //-----------------------------------------------------------------------
    private static List<String> MODEL_PACKAGES = Arrays.asList(
        new String[]{
            "org:w3c",
            "org:openmdx:base",
            "org:openmdx:datastore1",
            "org:openmdx:filter1",
            "org:opencrx",
            "org:opencrx:kernel:base",
            "org:opencrx:kernel:generic",
            "org:opencrx:kernel:document1",
            "org:opencrx:kernel:workflow1",
            "org:opencrx:kernel:building1",
            "org:opencrx:kernel:address1",
            "org:opencrx:kernel:account1",
            "org:opencrx:kernel:product1",
            "org:opencrx:kernel:contract1",
            "org:opencrx:kernel:activity1",
            "org:opencrx:kernel:forecast1",
            "org:opencrx:kernel:code1",
            "org:opencrx:kernel:uom1",
            "org:oasis_open",
            "org:openmdx:generic1",
            "org:openmdx:compatibility:view1",
            "org:opencrx:kernel:home1",
            "org:openmdx:security:realm1",
            "org:openmdx:security:authorization1",
            "org:openmdx:security:authentication1",
            "org:opencrx:security:identity1",
            "org:opencrx:security:realm1",
            "org:opencrx:kernel:reservation1",
            "org:opencrx:kernel:admin1",
            "org:openmdx:compatibility:document1",
            "org:opencrx:kernel:model1",
            "org:opencrx:kernel:ras1",
            "org:opencrx:kernel:depot1",
            "org:openmdx:compatibility:state1",
            "org:opencrx:kernel",
            "org:opencrx:security"
        }
    );
    
}

//--- End of File -----------------------------------------------------------

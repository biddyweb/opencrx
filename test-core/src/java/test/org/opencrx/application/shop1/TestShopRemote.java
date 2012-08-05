/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestShopRemote.java,v 1.2 2010/08/10 13:29:19 wfro Exp $
 * Description: TestShop
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/08/10 13:29:19 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
package test.org.opencrx.application.shop1;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttributeType;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.resource.cci.ConnectionFactory;
import javax.servlet.ServletException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openmdx.application.rest.http.SimplePort;
import org.openmdx.application.rest.spi.EntityManagerProxyFactory_2;
import org.openmdx.base.accessor.jmi.spi.EntityManagerFactory_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.persistence.cci.ConfigurableProperty;
import org.openmdx.base.rest.spi.ConnectionFactoryAdapter;
import org.openmdx.kernel.lightweight.naming.NonManagedInitialContextFactoryBuilder;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestShopRemote.TestAll.class
    }
)

/**
 * TestShopRemote
 */
public class TestShopRemote extends TestShop {

    //-----------------------------------------------------------------------
    @BeforeClass
    public static void initialize(
    ) throws NamingException, ServiceException, ServletException {
        if(!NamingManager.hasInitialContextFactoryBuilder()) {
            NonManagedInitialContextFactoryBuilder.install(null);
        }
    	SimplePort port = new SimplePort();
	    //port.setMimeType("text/xml");
    	port.setMimeType("application/vnd.openmdx.wbxml");
    	port.setUserName("admin-Standard");
    	port.setPassword("admin-Standard");
    	port.setUri("http://127.0.0.1:8080/opencrx-rest-CRX/");
        ConnectionFactory connectionFactory = new ConnectionFactoryAdapter(
        	port,
            true, // supportsLocalTransactionDemarcation
            TransactionAttributeType.NEVER
        );
        Map<String,Object> dataManagerProxyConfiguration = new HashMap<String,Object>();
        dataManagerProxyConfiguration.put(
            ConfigurableProperty.ConnectionFactory.qualifiedName(),
            connectionFactory
        );
        dataManagerProxyConfiguration.put(
            ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            EntityManagerProxyFactory_2.class.getName()
        );    
        PersistenceManagerFactory outboundConnectionFactory = JDOHelper.getPersistenceManagerFactory(
            dataManagerProxyConfiguration
        );

        Map<String,Object> entityManagerConfiguration = new HashMap<String,Object>();
        entityManagerConfiguration.put(
            ConfigurableProperty.ConnectionFactory.qualifiedName(),
            outboundConnectionFactory
        );
        entityManagerConfiguration.put(
            ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            EntityManagerFactory_1.class.getName()
        );    
        entityManagerFactory = JDOHelper.getPersistenceManagerFactory(
            entityManagerConfiguration
        );
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
		    
}

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestShop.java,v 1.11 2010/12/22 08:53:31 wfro Exp $
 * Description: TestShop
 * Revision:    $Revision: 1.11 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/22 08:53:31 $
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

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.resource.cci.ConnectionFactory;
import javax.servlet.ServletException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.opencrx.application.shop1.service.ShopServiceImpl;
import org.opencrx.application.shop1.test.TestShopService;
import org.openmdx.application.rest.spi.EntityManagerProxyFactory_2;
import org.openmdx.base.accessor.jmi.spi.EntityManagerFactory_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.persistence.cci.ConfigurableProperty;
import org.openmdx.base.resource.spi.Port;
import org.openmdx.base.rest.spi.ConnectionFactoryAdapter;
import org.openmdx.base.transaction.TransactionAttributeType;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.lightweight.naming.NonManagedInitialContextFactoryBuilder;

import test.org.opencrx.generic.AbstractTest;
import test.org.opencrx.generic.ServletPort;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestShop.TestAll.class
    }
)

/**
 * TestShop
 */
public class TestShop {

    //-----------------------------------------------------------------------
    @BeforeClass
    public static void initialize(
    ) throws NamingException, ServiceException, ServletException {
        if(!NamingManager.hasInitialContextFactoryBuilder()) {
            NonManagedInitialContextFactoryBuilder.install(null);
        }
        if(USE_PROXY) {
	        // Configure a proxy entity manager factory. The proxy acts as a
	        // REST/Http client which delegates to the in-process servlet (ServletPort) 
	        // which itself delegates to the entity manager specified by entity-manager-factory-name.
	        Port port =  new ServletPort(
	            Collections.singletonMap(
	                    "entity-manager-factory-name",
	                    "EntityManagerFactory"
	                )
	            );
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
        else {
        	entityManagerFactory = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactory();
        }        
    }
    
    //-----------------------------------------------------------------------
    public static class TestAll extends AbstractTest {
    	
		public TestAll(
		) {
			super(TestShop.entityManagerFactory);
		}
	
        @Test
        public void run(
        ) throws ServiceException, IOException, ParseException{
        	// Initialize Configuration
        	new org.opencrx.kernel.aop2.Configuration();        	
            org.opencrx.application.shop1.cci2.ShopService shopService =
		        new ShopServiceImpl(
		    		pm,
		    		providerName,
		    		segmentName,
		    		"TestShop",
		    		false,
		    		false,
		    		new org.opencrx.application.shop1.datatypes.DatatypeMappers()
		        );            
            TestShopService shopServiceTester = new TestShopService(shopService);
            org.opencrx.application.shop1.cci2.ReturnStatusT returnStatus = null;
    		returnStatus = shopServiceTester.testProducts();
    		assertEquals("testProducts", 0, returnStatus.getReturnCode());
    		returnStatus = shopServiceTester.testCustomers();
    		assertTrue("testCustomers", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.DUPLICATE || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testDocuments();
    		assertEquals("testDocuments", 0, returnStatus.getReturnCode());
    		returnStatus = shopServiceTester.testLegalEntities();
    		assertEquals("testLegalEntities", 0, returnStatus.getReturnCode());
    		returnStatus = shopServiceTester.testSalesOrders();
    		assertTrue("testSalesOrders", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testInvoices();
    		assertTrue("testInvoices", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testVouchers();
    		assertTrue("testVouchers", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testCodeValues();
    		assertTrue("testCodeValues", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testActivities();
    		assertTrue("testActivities", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.NONE);
    		returnStatus = shopServiceTester.testRegisterCustomer();            
    		assertTrue("testRegisterCustomer", returnStatus.getReturnCode() == BasicException.Code.NOT_FOUND || returnStatus.getReturnCode() == BasicException.Code.DUPLICATE || returnStatus.getReturnCode() == BasicException.Code.NONE);
        }
		
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static final boolean USE_PROXY = true;
    
    protected static PersistenceManagerFactory entityManagerFactory = null;
	protected static String providerName = "CRX";
	protected static String segmentName = "Standard";
		    
}

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestQuery.java,v 1.6 2010/01/07 17:08:04 wfro Exp $
 * Description: TestQuery
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/01/07 17:08:04 $
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
package test.org.opencrx.kernel.api;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.compatibility.datastore1.jmi1.QueryFilter;
import org.openmdx.kernel.lightweight.naming.NonManagedInitialContextFactoryBuilder;

import test.org.opencrx.generic.AbstractTest;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestQuery.TestAll.class
    }
)

/**
 * TestQuery
 */
public class TestQuery {

    //-----------------------------------------------------------------------
    @BeforeClass
    public static void initialize(
    ) throws NamingException, ServiceException {
        if(!NamingManager.hasInitialContextFactoryBuilder()) {
            NonManagedInitialContextFactoryBuilder.install(null);
        }
        entityManagerFactory = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactory();
    }
    
    //-----------------------------------------------------------------------
    public static class TestAll extends AbstractTest {
    	
		public TestAll(
		) {
			super(TestQuery.entityManagerFactory);
		}
	
        @Test
        public void run(
        ) throws ServiceException, IOException, ParseException{
            this.testSimpleCciQueries();
            //this.testQueryFilters();
            this.testNestedCciQueries();
        }
		
	    protected void testSimpleCciQueries(
	    ) throws ServiceException{
	        try {
	        	org.opencrx.kernel.account1.jmi1.Segment accountSegment =
	        		(org.opencrx.kernel.account1.jmi1.Segment)this.entityManager.getObjectById(
		        		new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
		        	);
	        	System.out.println("account segment=" + accountSegment);
	        } finally {
	        }
	    }

	    protected void testQueryFilters(
	    ) throws ServiceException{
	        try {
	        	org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
	        		(org.opencrx.kernel.contract1.jmi1.Segment)this.entityManager.getObjectById(
		        		new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/" + providerName + "/segment/" + segmentName)
		        	);
	        	System.out.println("contract segment=" + contractSegment);
	        	org.opencrx.kernel.contract1.cci2.SalesOrderQuery salesOrderQuery = 
	        		(org.opencrx.kernel.contract1.cci2.SalesOrderQuery)entityManager.newQuery(
	        			org.opencrx.kernel.contract1.jmi1.SalesOrder.class
	        		);
            	QueryFilter queryFilter = entityManager.newInstance(QueryFilter.class);
            	queryFilter.setClause(
            		"EXISTS (" + 
            		"  SELECT 0 FROM " + 
            		"    OOCKE1_CONTRACTPOSITION cp " + 
            		"  INNER JOIN " + 
            		"    OOCKE1_PRODUCTCONFIG pc " + 
            		"  ON " + 
            		"    pc.p$$parent = cp.object_id " + 
            		"  INNER JOIN " + 
            		"    OOCKE1_PROPERTY p " + 
            		"  ON " + 
            		"    p.p$$parent = pc.object_id" + 
            		"  WHERE " + 
            		"    p.name = 'FieldUri' AND " + 
            		"    pc.name = 'CropScan.Default' AND " + 
            		"    cp.p$$parent = v.object_id" + 
            		")"
            	);
            	salesOrderQuery.thereExistsContext().equalTo(
                	queryFilter
                );
	        	List<org.opencrx.kernel.contract1.jmi1.SalesOrder> salesOrders = contractSegment.getSalesOrder(salesOrderQuery);
	        	assertTrue(salesOrderQuery.toString(), !salesOrders.isEmpty());	        	
	        } finally {
	        }
	    }
	    
	    protected void testNestedCciQueries(
	    ) throws ServiceException{
	        try {
	        	org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
	        		(org.opencrx.kernel.contract1.jmi1.Segment)this.entityManager.getObjectById(
		        		new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/" + providerName + "/segment/" + segmentName)
		        	);
	        	System.out.println("contract segment=" + contractSegment);
	        	org.opencrx.kernel.product1.jmi1.Segment productSegment =
	        		(org.opencrx.kernel.product1.jmi1.Segment)this.entityManager.getObjectById(
		        		new Path("xri://@openmdx*org.opencrx.kernel.product1/provider/" + providerName + "/segment/" + segmentName)
		        	);
	        	System.out.println("product segment=" + productSegment);
	        	// Test 1
	        	org.opencrx.kernel.contract1.cci2.SalesOrderQuery salesOrderQuery = 
	        		(org.opencrx.kernel.contract1.cci2.SalesOrderQuery)entityManager.newQuery(
	        			org.opencrx.kernel.contract1.jmi1.SalesOrder.class
	        		);
	        	salesOrderQuery.thereExistsPosition().thereExistsConfiguration().name().like("CropScan.*");
	        	salesOrderQuery.thereExistsPosition().thereExistsConfiguration().thereExistsProperty().name().equalTo("FieldUri");
	        	List<org.opencrx.kernel.contract1.jmi1.SalesOrder> salesOrders = contractSegment.getSalesOrder(salesOrderQuery);
	        	assertTrue(salesOrderQuery.toString(), !salesOrders.isEmpty());
	        	// Test 2
	        	salesOrderQuery = 
	        		(org.opencrx.kernel.contract1.cci2.SalesOrderQuery)entityManager.newQuery(
	        			org.opencrx.kernel.contract1.jmi1.SalesOrder.class
	        		);
	        	salesOrderQuery.thereExistsSalesRep().thereExistsFullName().like("F.*");
	        	salesOrders = contractSegment.getSalesOrder(salesOrderQuery);
	        	assertTrue(salesOrderQuery.toString(), !salesOrders.isEmpty());
	        	// Test 3
	        	org.opencrx.kernel.home1.jmi1.UserHome guest = (org.opencrx.kernel.home1.jmi1.UserHome)this.entityManager.getObjectById(
	        		new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/guest")
	        	);
	        	assertNotNull("UserHome guest", guest);
	        	org.opencrx.kernel.product1.cci2.ProductQuery productQuery = (org.opencrx.kernel.product1.cci2.ProductQuery)this.entityManager.newQuery(
	        		org.opencrx.kernel.product1.jmi1.Product.class
	        	);
	        	productQuery.thereExistsAssignedAccount().thereExistsAccount().equalTo(guest.getContact());
	        	List<org.opencrx.kernel.product1.jmi1.Product> products = productSegment.getProduct(productQuery);
	        	assertTrue(productQuery.toString(), !products.isEmpty());	        	
	        } finally {
	        }
	    }

    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static PersistenceManagerFactory entityManagerFactory = null;
	protected static String providerName = "CRX";
	protected static String segmentName = "Standard";
		    
}

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestUtils.java,v 1.3 2010/10/15 14:46:45 wfro Exp $
 * Description: TestQuery
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/10/15 14:46:45 $
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
import java.util.Collection;

import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.lightweight.naming.NonManagedInitialContextFactoryBuilder;

import test.org.opencrx.generic.AbstractTest;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestUtils.TestAll.class
    }
)

/**
 * TestUtils
 */
public class TestUtils {

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
			super(TestUtils.entityManagerFactory);
		}
	
        @Test
        public void run(
        ) throws ServiceException, IOException, ParseException{
            this.testTraverseObjectTree();
            this.testSecureObject();
        }
		
	    protected void testTraverseObjectTree(
	    ) throws ServiceException{
	        try {
	        	org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
	        		(org.opencrx.kernel.contract1.jmi1.Segment)this.pm.getObjectById(
		        		new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/" + providerName + "/segment/" + segmentName)
		        	);
	        	Collection<SalesOrder> salesOrders = contractSegment.getSalesOrder();
	        	int count = 0;
	        	for(SalesOrder salesOrder: salesOrders) {
		        	Utils.traverseObjectTree(
		        		salesOrder, 
		        		null, // referenceFilter 
		        		new Utils.TraverseObjectTreeCallback() {
							
							@Override
							public Object visit(
								RefObject_1_0 object, 
								Object context
							) throws ServiceException {
								System.out.println("Visit=" + object.refGetPath());
								return null;
							}
						},
		        		null
		        	);
		        	count++;
		        	if(count > 100) break;
	        	} 
	        } finally {
	        }
	    }

	    protected void testSecureObject(
	    ) throws ServiceException {
        	org.opencrx.kernel.account1.jmi1.Segment accountSegment =
        		(org.opencrx.kernel.account1.jmi1.Segment)this.pm.getObjectById(
	        		new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
	        	);
        	Collection<Account> accounts = accountSegment.getAccount();
        	int count = 0;
        	for(Account account: accounts) {
        		org.opencrx.security.realm1.jmi1.User user = account.getOwningUser();
        		String principalNameUser = user.refGetPath().getBase();
        		String principalName = principalNameUser.endsWith("." + SecurityKeys.USER_SUFFIX) ?
        			principalNameUser.substring(0, principalNameUser.indexOf(".")) :
        				principalNameUser;
        		UserHome userHome = UserHomes.getInstance().getUserHome(
        			principalName, 
        			accountSegment.refGetPath(), 
        			this.pm
        		);
        		Contact contact = userHome.getContact();
        		System.out.println("contact=" + contact);
        		count++;
        		if(count > 10) break;
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

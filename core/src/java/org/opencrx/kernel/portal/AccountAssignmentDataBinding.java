/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: AccountAssignmentDataBinding
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal;

import java.util.Map;
import java.util.TreeMap;

import javax.jmi.reflect.RefObject;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.building1.jmi1.AccountAssignmentInventoryItem;
import org.opencrx.kernel.building1.jmi1.InventoryItem;
import org.opencrx.kernel.contract1.jmi1.AbstractContract;
import org.opencrx.kernel.contract1.jmi1.AccountAssignmentContract;
import org.opencrx.kernel.product1.jmi1.AbstractProduct;
import org.opencrx.kernel.product1.jmi1.AccountAssignmentProduct;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.DataBinding;

public class AccountAssignmentDataBinding extends DataBinding {

	/**
	 * @param parameterString
	 */
	public AccountAssignmentDataBinding(
		String parameterString
	) {
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.DataBinding#getValue(javax.jmi.reflect.RefObject, java.lang.String, org.openmdx.portal.servlet.ApplicationContext)
	 */
	@Override
    public Object getValue(
        RefObject object, 
        String qualifiedFeatureName,
        ApplicationContext app
    ) {
    	Map<String,Account> assignedAccounts = new TreeMap<String,Account>();
    	try {
	        if(object instanceof AbstractContract) {
	        	for(AccountAssignmentContract assignment: ((AbstractContract)object).<AccountAssignmentContract>getAssignedAccount()) {
	        		Account account = assignment.getAccount();
	        		assignedAccounts.put(
	        			account.getFullName() + "*" + account.refGetPath(),
	        			account
	    			);        		
	        	}
	        }
	        else if(object instanceof AbstractProduct) {
	        	for(AccountAssignmentProduct assignment: ((AbstractProduct)object).<AccountAssignmentProduct>getAssignedAccount()) {
	        		Account account = assignment.getAccount();
	        		assignedAccounts.put(
	        			account.getFullName() + "*" + account.refGetPath(),
	        			account
	    			);        		
	        	}	        	
	        }
	        else if(object instanceof InventoryItem) {
	        	for(AccountAssignmentInventoryItem assignment: ((InventoryItem)object).<AccountAssignmentInventoryItem>getAssignedAccount()) {
	        		Account account = assignment.getAccount();
	        		assignedAccounts.put(
	        			account.getFullName() + "*" + account.refGetPath(),
	        			account
	    			);        		
	        	}
	        }
	    	return assignedAccounts.values();
    	} catch(Exception e) {}
    	return null;
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DataBinding#setValue(javax.jmi.reflect.RefObject, java.lang.String, java.lang.Object, org.openmdx.portal.servlet.ApplicationContext)
     */
    public void setValue(
        RefObject object, 
        String qualifiedFeatureName, 
        Object newValue,
        ApplicationContext app
    ) {
    }

}

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AccountImpl.java,v 1.7 2009/04/20 17:56:46 wfro Exp $
 * Description: AccountImpl
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/20 17:56:46 $
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
package org.opencrx.kernel.account1.aop2;

import javax.jdo.JDOUserException;
import javax.jdo.listener.StoreCallback;

import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class AccountImpl
	<S extends org.opencrx.kernel.account1.jmi1.Account,N extends org.opencrx.kernel.account1.cci2.Account,C extends Void>
	extends AbstractObject<S,N,C>
	implements StoreCallback {

    //-----------------------------------------------------------------------
    public AccountImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateLeadResult createLead(
        org.opencrx.kernel.account1.jmi1.CreateLeadParams params
    ) {
    	try {
	        Lead lead = Accounts.getInstance().createLead(
	            this.sameObject(),
	            params.getName(),
	            params.getDescription(),
	            params.getNextStep(),
	            // TODO pm for params workaround
	            (Lead)this.sameManager().getObjectById(params.getBasedOn().refGetPath())
	        );
	        return Utils.getAccountPackage(this.sameManager()).createCreateLeadResult(
	            lead                    
	        );                                        
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateOpportunityResult createOpportunity(
        org.opencrx.kernel.account1.jmi1.CreateOpportunityParams params
    ) {
    	try {
	        Opportunity opportunity = Accounts.getInstance().createOpportunity(
	            this.sameObject(),
	            params.getName(),
	            params.getDescription(),
	            null,
	            // TODO pm for params workaround
	            (Opportunity)this.sameManager().getObjectById(params.getBasedOn().refGetPath())
	        );
	        return Utils.getAccountPackage(this.sameManager()).createCreateOpportunityResult(
	            opportunity                    
	        );
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateQuoteResult createQuote(
        org.opencrx.kernel.account1.jmi1.CreateQuoteParams params
    ) {
    	try {
	        Quote quote = Accounts.getInstance().createQuote(
	            this.sameObject(),
	            params.getName(),
	            params.getDescription(),
	            // TODO pm for params workaround
	            (Quote)this.sameManager().getObjectById(params.getBasedOn().refGetPath())
	        );
	        return Utils.getAccountPackage(this.sameManager()).createCreateQuoteResult(
	            quote                    
	        );                                        
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateSalesOrderResult createSalesOrder(
        org.opencrx.kernel.account1.jmi1.CreateSalesOrderParams params
    ) {
    	try {
	        SalesOrder salesOrder = Accounts.getInstance().createSalesOrder(
	            this.sameObject(),
	            params.getName(),
	            params.getDescription(),
	            // TODO pm for params workaround
	            (SalesOrder)this.sameManager().getObjectById(params.getBasedOn().refGetPath())
	        );
	        return Utils.getAccountPackage(this.sameManager()).createCreateSalesOrderResult(
	            salesOrder                    
	        );                                        
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateInvoiceResult createInvoice(
        org.opencrx.kernel.account1.jmi1.CreateInvoiceParams params
    ) {
    	try {
	        Invoice invoice = Accounts.getInstance().createInvoice(
	            this.sameObject(),
	            params.getName(),
	            params.getDescription(),
	            // TODO pm for params workaround	            
	            (Invoice)this.sameManager().getObjectById(params.getBasedOn().refGetPath())
	        );
	        return Utils.getAccountPackage(this.sameManager()).createCreateInvoiceResult(
	            invoice
	        );                                        
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void updateVcard(
    ) {
        try {
            Accounts.getInstance().updateVcard(
                this.sameObject()
            );
            return this.newVoid();            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                                                    
    }

    //-----------------------------------------------------------------------
	@Override
    public void jdoPreStore(
    ) {
    	try {
    		Accounts.getInstance().updateAccount(
    			this.sameObject() 
    		);
    		super.jdoPreStore();
    	}
    	catch(ServiceException e) {
    		throw new JDOUserException(
    			"jdoPreStore failed",
    			e,
    			this.sameObject()
    		);
    	}
    }
    
}

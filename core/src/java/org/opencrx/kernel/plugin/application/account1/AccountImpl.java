/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AccountImpl.java,v 1.5 2009/01/09 18:11:25 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/01/09 18:11:25 $
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
package org.opencrx.kernel.plugin.application.account1;

import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class AccountImpl {

    //-----------------------------------------------------------------------
    public AccountImpl(
        org.opencrx.kernel.account1.jmi1.Account current,
        org.opencrx.kernel.account1.cci2.Account next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_0)this.current.refOutermostPackage()).refUserContext();
    }
            
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateLeadResult createLead(
        org.opencrx.kernel.account1.jmi1.CreateLeadParams params
    ) {
        Lead lead = this.getBackend().getAccounts().createLead(
            this.current.refGetPath(),
            params.getName(),
            params.getDescription(),
            params.getNextStep(),
            params.getBasedOn() == null ? null : params.getBasedOn().refGetPath()
        );
        return ((Account1Package)this.current.refOutermostPackage().refPackage(Account1Package.class.getName())).createCreateLeadResult(
            lead                    
        );                                        
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateOpportunityResult createOpportunity(
        org.opencrx.kernel.account1.jmi1.CreateOpportunityParams params
    ) {
        Opportunity opportunity = this.getBackend().getAccounts().createOpportunity(
            this.current.refGetPath(),
            params.getName(),
            params.getDescription(),
            null,
            params.getBasedOn() == null ? null : params.getBasedOn().refGetPath()
        );
        return ((Account1Package)this.current.refOutermostPackage().refPackage(Account1Package.class.getName())).createCreateOpportunityResult(
            opportunity                    
        );                                        
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateQuoteResult createQuote(
        org.opencrx.kernel.account1.jmi1.CreateQuoteParams params
    ) {
        Quote quote = this.getBackend().getAccounts().createQuote(
            this.current.refGetPath(),
            params.getName(),
            params.getDescription(),
            null,
            params.getBasedOn() == null ? null : params.getBasedOn().refGetPath()
        );
        return ((Account1Package)this.current.refOutermostPackage().refPackage(Account1Package.class.getName())).createCreateQuoteResult(
            quote                    
        );                                        
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateSalesOrderResult createSalesOrder(
        org.opencrx.kernel.account1.jmi1.CreateSalesOrderParams params
    ) {
        SalesOrder salesOrder = this.getBackend().getAccounts().createSalesOrder(
            this.current.refGetPath(),
            params.getName(),
            params.getDescription(),
            null,
            params.getBasedOn() == null ? null : params.getBasedOn().refGetPath()
        );
        return ((Account1Package)this.current.refOutermostPackage().refPackage(Account1Package.class.getName())).createCreateSalesOrderResult(
            salesOrder                    
        );                                        
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.CreateInvoiceResult createInvoice(
        org.opencrx.kernel.account1.jmi1.CreateInvoiceParams params
    ) {
        Invoice invoice = this.getBackend().getAccounts().createInvoice(
            this.current.refGetPath(),
            params.getName(),
            params.getDescription(),
            null,
            params.getBasedOn() == null ? null : params.getBasedOn().refGetPath()
        );
        return ((Account1Package)this.current.refOutermostPackage().refPackage(Account1Package.class.getName())).createCreateInvoiceResult(
            invoice
        );                                        
    }
    
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void updateVcard(
    ) {
        try {
            this.getBackend().getAccounts().updateVcard(
                this.current.refGetPath()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                                                    
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.account1.jmi1.Account current;
    protected final org.opencrx.kernel.account1.cci2.Account next;
    
}

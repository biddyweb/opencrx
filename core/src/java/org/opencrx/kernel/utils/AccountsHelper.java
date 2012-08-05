/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AccountsHelper.java,v 1.5 2009/07/13 16:39:11 wfro Exp $
 * Description: AccountsHelper
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/07/13 16:39:11 $
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
package org.opencrx.kernel.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.AccountFilterGlobalQuery;
import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.jmi1.AbstractFilterAccount;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.account1.jmi1.AccountFilterGlobal;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.format.DateFormat;

public class AccountsHelper {

    //-----------------------------------------------------------------------
    public AccountsHelper(
       PersistenceManager pm
    ) {
        this.pm = pm;
    }
    
    //-----------------------------------------------------------------------
    public int parseFilteredAccountsUri(
       String uri
    ) throws IllegalArgumentException  {
        List<String> l = AccountsHelper.splitUri(uri);
        if(l.size() >= 3) {
            // URL pattern is
            // ./provider.name/segment.name/filter/filter.name
            String providerName = l.get(0);
            String segmentName = l.get(1);
            this.filterName = null;
            if("filter".equals(l.get(l.size()-2))) {
                this.filterName = l.get(l.size()-1);
            }
            this.accountPackage = Utils.getAccountPackage(this.pm); 
            this.accountSegment = 
                (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
                );
            this.accountFilter = null;
            if(this.filterName != null) {
                AccountFilterGlobalQuery query = this.accountPackage.createAccountFilterGlobalQuery();
                query.name().equalTo(this.filterName);
                List<org.opencrx.kernel.account1.jmi1.AccountFilterGlobal> accountFilters = this.accountSegment.getAccountFilter(query);
                if(!accountFilters.isEmpty()) {
                    this.accountFilter = accountFilters.iterator().next();
                }
            }
        }
        return l.size();
    }
        
    //-----------------------------------------------------------------------
    public AbstractFilterAccount getAccountFilter(
    ) {
        return this.accountFilter;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
    ) {
        return this.accountSegment;
    }
    
    //-----------------------------------------------------------------------
    public String getFilterName(
    ) {
        return this.filterName;
    }

    //-----------------------------------------------------------------------
    public Collection<Account> getFilteredAccounts(
        AccountQuery accountQuery            
    ) {
        if(this.accountFilter != null) {
            return this.accountFilter.getFilteredAccount(accountQuery);
        }
        else {
            return Collections.emptyList();
        }
    }
    
    //-----------------------------------------------------------------------
    public static String formatDate(
        Date date
    ) {
        return DateFormat.getInstance().format(date).substring(0, 15) + "Z";
    }
    
    //-----------------------------------------------------------------------
    public static List<String> splitUri(
        String uri 
    ) throws IllegalArgumentException  {
        try {
            String[] ss = uri.split("/");
            int pathLength = ss.length - 1;
            if (pathLength < 2) {
                throw new IllegalArgumentException ("Bad uri: " + uri);
            }
            List<String> l = Arrays.asList(ss);
            return l.subList(1, l.size());
        } 
        catch (Exception e) {
            throw new IllegalArgumentException ("Bad uri: " + uri);
        }
    }
            
    //-----------------------------------------------------------------------
    public PersistenceManager getPersistenceManager(
    ) {
        return this.pm;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final PersistenceManager pm;
    protected AccountFilterGlobal accountFilter = null;
    protected Account1Package accountPackage = null;    
    protected org.opencrx.kernel.account1.jmi1.Segment accountSegment = null;
    protected String filterName = null;
    
}

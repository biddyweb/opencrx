/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: VCardServlet.java,v 1.8 2008/10/30 15:20:04 wfro Exp $
 * Description: VCardServlet
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/30 15:20:04 $
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
package org.opencrx.application.vcard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.backend.VCard;
import org.opencrx.kernel.base.jmi1.ImportParams;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.AccountsHelper;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.WebKeys;

public class VCardServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        if(this.persistenceManagerFactory == null) {                    
            try {
                Utils.createModel();
                this.persistenceManagerFactory = Utils.getPersistenceManagerFactory();
                this.rootPm = this.persistenceManagerFactory.getPersistenceManager(
                    SecurityKeys.ROOT_PRINCIPAL,
                    UUIDs.getGenerator().next().toString()
                );            
            }
            catch (NamingException e) {
                throw new ServletException( 
                    "Can not get the initial context", 
                    e
                );                
            }
            catch(ServiceException e) {
                throw new ServletException( 
                    "Can not get persistence manager", 
                    e
                );                
            }        
        }            
    }
    
    //-----------------------------------------------------------------------
    protected PersistenceManager getPersistenceManager(
        HttpServletRequest req
    ) {
        return req.getUserPrincipal() == null ?
            null :
            this.persistenceManagerFactory.getPersistenceManager(
                req.getUserPrincipal().getName(),
                UUIDs.getGenerator().next().toString()
            );
    }

    //-----------------------------------------------------------------------
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration getComponentConfiguration(
        String providerName
    ) {
        if(this.componentConfiguration == null) {
            this.componentConfiguration = ComponentConfigHelper.getComponentConfiguration(
                CONFIGURATION_ID,
                providerName,
                this.rootPm,
                false,
                null
            );
        }
        return this.componentConfiguration;
    }
    
    //-----------------------------------------------------------------------
    protected AccountsHelper getAccountsHelper(
        PersistenceManager pm,
        String requestedAccountFilter
    ) {
        AccountsHelper accountsHelper = new AccountsHelper(pm);
        if(requestedAccountFilter != null) {
            try {
                accountsHelper.parseFilteredAccountsUri(                        
                    (requestedAccountFilter.startsWith("/") ? "" : "/") + requestedAccountFilter
                );
            }
            catch(Exception  e) {}
        }        
        return accountsHelper;
    }
    
    //-----------------------------------------------------------------------
    protected Account findAccount(
        PersistenceManager pm,
        AccountsHelper accountsHelper,
        String uid
    ) {
        Account1Package accountPkg = Utils.getAccountPackage(pm);
        AccountQuery query = accountPkg.createAccountQuery();
        query.thereExistsExternalLink().equalTo(VCard.VCARD_SCHEMA + uid);
        List<Account> accounts = accountsHelper.getAccountSegment().getAccount(query);
        if(accounts.isEmpty()) {
            query = accountPkg.createAccountQuery();
            query.thereExistsExternalLink().equalTo(VCard.VCARD_SCHEMA + uid.replace('.', '+'));
            accounts = accountsHelper.getAccountSegment().getAccount(query);
            if(accounts.isEmpty()) {
                return null;
            }
            else {
                return accounts.iterator().next();
            }
        }
        else {
            return accounts.iterator().next();
        }
    }
        
    //-----------------------------------------------------------------------
    protected String getAccountUrl(        
        HttpServletRequest req,
        Account account,
        boolean htmlEncoded
    ) {
        Action selectAccountAction = 
            new Action(
                Action.EVENT_SELECT_OBJECT, 
                new Action.Parameter[]{
                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, account.refMofId())
                },
                "",
                true
            );        
        return htmlEncoded ? 
            req.getContextPath().replace("-vcard-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&amp;parameter=" + selectAccountAction.getParameterEncoded() : 
            req.getContextPath().replace("-vcard-", "-core-") +  "/" + WebKeys.SERVLET_NAME + "?event=" + Action.EVENT_SELECT_OBJECT + "&parameter=" + selectAccountAction.getParameter();
    }
    
    //-----------------------------------------------------------------------
    @Override
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        PersistenceManager pm = this.getPersistenceManager(req);
        if(pm == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        
        String requestedAccountFilter = req.getParameter("id");
        AccountsHelper accountsHelper = this.getAccountsHelper(pm, requestedAccountFilter);
        org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = 
            this.getComponentConfiguration(
                 accountsHelper.getAccountSegment().refGetPath().get(2)
            );
        String maxAccountsValue = componentConfiguration == null ? 
            null : 
            ComponentConfigHelper.getComponentConfigProperty("maxAccounts", componentConfiguration).getStringValue();
        int maxAccounts = Integer.valueOf(
            maxAccountsValue == null ? 
                "500" : 
                maxAccountsValue
        ).intValue();
        // Return all accounts in VCF format
        if(
            RESOURCE_NAME_ACCOUNTS_VCF.equals(req.getParameter(PARAMETER_NAME_RESOURCE)) ||
            RESOURCE_TYPE_VCF.equals(req.getParameter(PARAMETER_NAME_TYPE))
        ) {
            try {
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                AccountQuery accountQuery = Utils.getAccountPackage(pm).createAccountQuery();
                accountQuery.forAllDisabled().isFalse();
                accountQuery.vcard().isNonNull();
                PrintWriter p = resp.getWriter();
                int n = 0;
                for(Account account: accountsHelper.getFilteredAccounts(accountQuery)) {
                    String vcard = account.getVcard();
                    if((vcard != null) && (vcard.indexOf("BEGIN:VCARD") >= 0)) {
                        int start = vcard.indexOf("BEGIN:VCARD");
                        int end = vcard.indexOf("END:VCARD");
                        p.write(vcard.substring(start, end));
                        if(vcard.indexOf("URL:") < 0) {
                            p.write("URL:" + req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + this.getAccountUrl(req, account, false) + "\n");
                        }                        
                        p.write("END:VCARD\n");
                    }
                    n++;
                    if(n % 50 == 0) pm.evictAll();                
                    if(n > maxAccounts) break;
                }
                p.flush();
            }
            catch(Exception e) {
                new ServiceException(e).log();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        else {
            super.doGet(req, resp);
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    protected void doPut(
        HttpServletRequest req, 
        HttpServletResponse resp
    ) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        PersistenceManager pm = this.getPersistenceManager(req);
        if(pm == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }        
        String filterId = req.getParameter("id");
        AccountsHelper accountsHelper = this.getAccountsHelper(pm, filterId);
        if(
            RESOURCE_NAME_ACCOUNTS_VCF.equals(req.getParameter(PARAMETER_NAME_RESOURCE)) ||
            RESOURCE_TYPE_VCF.equals(req.getParameter(PARAMETER_NAME_TYPE))
        ) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setCharacterEncoding("UTF-8");
            BufferedReader reader = new BufferedReader(req.getReader());
            String l = null;
            while((l = reader.readLine()) != null) {
                if(l.toUpperCase().startsWith("BEGIN:VCARD")) {
                    StringBuilder vcard = new StringBuilder();
                    vcard.append("BEGIN:VCARD\n");
                    String uid = null;
                    String rev = null;
                    while((l = reader.readLine()) != null) {
                        vcard.append(l).append("\n");
                        if(l.startsWith("UID:")) {
                            uid = l.substring(4);
                        }
                        else if(l.startsWith("REV:")) {
                            rev = l.substring(14);
                        }
                        else if(l.startsWith("END:VCARD")) {
                            break;
                        }
                    }
                    AppLog.trace("VCARD", vcard);
                    if((uid != null) && (rev != null)) {
                        AppLog.detail("Lookup account", uid);
                        Account account = this.findAccount(
                            pm,
                            accountsHelper, 
                            uid
                        );
                        if(
                            (account != null) &&
                            ((account.getModifiedAt() == null) || 
                            // only compare date, hours and minutes (a sample date is 20070922T005655Z)
                            (rev.substring(0, 13).compareTo(ActivitiesHelper.formatDate(account.getModifiedAt()).substring(0, 13)) >= 0))
                        ) {
                            try {
                                pm.currentTransaction().begin();
                                ImportParams importItemParams = Utils.getBasePackage(pm).createImportParams(
                                    vcard.toString().getBytes("UTF-8"), 
                                    VCard.MIME_TYPE, 
                                    "import.vcf", 
                                    (short)0
                                );
                                account.importItem(importItemParams);
                                pm.currentTransaction().commit();
                            }
                            catch(Exception e) {
                                try {
                                    pm.currentTransaction().rollback();
                                } catch(Exception e0) {}                                    
                            }
                        }
                        else {
                            AppLog.detail(
                                "Skipping ", 
                                new String[]{
                                    "UID: " + uid, 
                                    "REV: " + rev, 
                                    "Account.number: " + (account == null ? null : account.refMofId()),
                                    "Account.modifiedAt:" + (account == null ? null : account.getModifiedAt())
                                }
                            );
                        }
                    }
                    else {
                        AppLog.detail("Skipping", vcard); 
                    }
                }                    
            }
        }            
        else {
            super.doPut(req, resp);
        }
        try {
            pm.close();            
        } catch(Exception e) {}        
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -7557742069034207175L;
    
    protected final static String CONFIGURATION_ID = "VCardServlet";    
    protected final static String RESOURCE_NAME_ACCOUNTS_VCF = "accounts.vcf";
    protected final static String RESOURCE_TYPE_VCF = "vcf";
    protected final static String PARAMETER_NAME_TYPE = "type";
    protected final static String PARAMETER_NAME_RESOURCE = "resource";
    
    protected static final int MAX_ACCOUNTS = 500;
    
    protected PersistenceManagerFactory persistenceManagerFactory = null;
    protected PersistenceManager rootPm = null;
    protected org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;    
        
}

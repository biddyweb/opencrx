/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSession.java,v 1.19 2008/05/02 16:09:35 wfro Exp $
 * Description: XWiki OpenCrxSession
 * Revision:    $Revision: 1.19 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/05/02 16:09:35 $
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
package org.opencrx.groupware.xwiki.spi;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.opencrx.groupware.generic.Util;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.opencrx.kernel.document1.jmi1.Document1Package;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.Authority;
import org.openmdx.compatibility.datastore1.jmi1.Datastore1Package;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.model1.accessor.basic.cci.Model_1_3;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

public class OpenCrxSession {

    //-----------------------------------------------------------------------
    public OpenCrxSession(
        XWikiContext context
    ) throws XWikiException {
        HttpSession session = context.getRequest() == null
            ? null
            : context.getRequest().getHttpServletRequest().getSession(true);
        // Persistence manager factory at session level 
        PersistenceManagerFactory persistenceManagerFactory = session == null
            ? null
            : (PersistenceManagerFactory)session.getAttribute(
                PersistenceManagerFactory.class.getName()
            );
        if(persistenceManagerFactory == null) {                    
            try {
                persistenceManagerFactory = Util.getPersistenceManagerFactory();
                if(session != null) {
                    session.setAttribute(
                        PersistenceManagerFactory.class.getName(),
                        persistenceManagerFactory
                    );
                }
            }
            catch (NamingException e) {
                throw new XWikiException( 
                    XWikiException.MODULE_XWIKI_STORE, 
                    XWikiException.ERROR_XWIKI_STORE_HIBERNATE_CREATE_DATABASE,
                    "Can not get the initial context", 
                    e
                );                
            }
            catch(ServiceException e) {
                throw new XWikiException( 
                    XWikiException.MODULE_XWIKI_STORE, 
                    XWikiException.ERROR_XWIKI_STORE_HIBERNATE_CREATE_DATABASE,
                    "Can not get connection to data provider", 
                    e
                );                
            }        
        }        
        String providerName = context.getWiki().Param(PARAM_PREFIX + "provider", DEFAULT_PROVIDER_NAME); 
        String segmentName = "xwiki".equals(context.getDatabase()) || (context.getDatabase() == null)
            ? DEFAULT_SEGMENT_NAME
            : context.getDatabase();
        if(segmentName.indexOf("-") > 0) {
            segmentName = segmentName.substring(0, segmentName.indexOf("-"));
        }
        segmentName = segmentName.length() <= 5
             ? segmentName.toUpperCase()
             : Character.toUpperCase(segmentName.charAt(0)) + segmentName.substring(1);
        String user = context.getUser().startsWith("XWiki.")
            ? context.getUser().substring(6)
            : context.getUser();
        this.pm = persistenceManagerFactory.getPersistenceManager(
            PRINCIPAL_XWIKI_GUEST.equals(user)
                ? "guest"
                : PRINCIPAL_XWIKI_ADMIN.equals(user) || PRINCIPAL_XWIKI_SUPERADMIN.equals(user)
                    ? SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName
                    : user,
            UUIDs.getGenerator().next().toString()
        );
        this.documentPackage = 
            (Document1Package)((Authority)this.pm.getObjectById(
                Authority.class,
                Document1Package.AUTHORITY_XRI
            )).refImmediatePackage(); 
        this.basePackage = 
            (BasePackage)((Authority)this.pm.getObjectById(
                Authority.class,
                BasePackage.AUTHORITY_XRI
            )).refImmediatePackage();
        this.datastorePackage = 
            (Datastore1Package)((Authority)this.pm.getObjectById(
                Authority.class,
                Datastore1Package.AUTHORITY_XRI
            )).refImmediatePackage();         
        this.documentSegment = 
            (org.opencrx.kernel.document1.jmi1.Segment)this.pm.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.document1/provider/" + providerName + "/segment/" + segmentName
            );
    }
    
    //-----------------------------------------------------------------------
    public Document1Package getDocumentPackage(
    ) {
        return this.documentPackage;
    }
    
    //-----------------------------------------------------------------------
    public BasePackage getBasePackage(
    ) {
        return this.basePackage;
    }
    
    //-----------------------------------------------------------------------
    public Datastore1Package getDatastorePackage(
    ) {
        return this.datastorePackage;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.document1.jmi1.Segment getDocumentSegment(
    ) {
        return this.documentSegment;
    }
    
    //-----------------------------------------------------------------------
    public boolean beginTransaction(          
    ) {
        this.pm.currentTransaction().begin();
        return true;
    }
    
    //-----------------------------------------------------------------------
    public void commitTransaction(
    ) {
        this.pm.currentTransaction().commit();
    }
    
    //-----------------------------------------------------------------------
    public void rollbackTransaction(
    ) {
        this.pm.currentTransaction().rollback();
    }
    
    //-----------------------------------------------------------------------
    public void flush(
    ) {
        this.pm.flush();
    }
    
    //-----------------------------------------------------------------------
    public Object getObjectById(
        String xri
    ) {
        return this.pm.getObjectById(xri);
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final String PARAM_PREFIX = "xwiki.store.opencrx.";
    private static final String DEFAULT_PROVIDER_NAME = "CRX";
    private static final String DEFAULT_SEGMENT_NAME = "Standard";
    private static final String PRINCIPAL_XWIKI_GUEST = "XWikiGuest";
    private static final String PRINCIPAL_XWIKI_ADMIN = "Admin";
    private static final String PRINCIPAL_XWIKI_SUPERADMIN = "superadmin";
    
    private static Model_1_3 model = Util.createModel(); 
    private final PersistenceManager pm;   
    private final Document1Package documentPackage;
    private final BasePackage basePackage;
    private final Datastore1Package datastorePackage;
    private final org.opencrx.kernel.document1.jmi1.Segment documentSegment;
      
}

/*
 * ====================================================================
 * Project:     opencCRX/Store, http://www.opencrx.org/
 * Name:        $Id: OpenCrxContextFactory.java,v 1.10 2008/11/07 23:03:51 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/07 23:03:51 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004, CRIXP Corp., Switzerland
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
package org.opencrx.store.common.util;

import java.util.Locale;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpSession;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;

/**
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public class OpenCrxContextFactory {
    
    //-----------------------------------------------------------------------
    public static OpenCrxContext createContext(
        HttpSession session
    ) throws Exception {

        // Get model
        if(OpenCrxContextFactory.model == null) {
            OpenCrxContextFactory.model = Utils.createModel();
        }        
        // Get persistence manager factory
        if(OpenCrxContextFactory.persistenceManagerFactory == null) {
            OpenCrxContextFactory.persistenceManagerFactory = Utils.getRemotePersistenceManagerFactory();
        }
        String localeAsString = session.getServletContext().getInitParameter("locale");
        Locale locale = new Locale(localeAsString.substring(0,2), localeAsString.substring(3,5));
        Converter.setLocale(locale);
        String root = session.getServletContext().getInitParameter("root");
        String providerName = root.substring(root.indexOf("provider/") + 9, root.indexOf("/segment"));
        String segmentName = root.substring(root.indexOf("segment/") + 8);
        return new OpenCrxContext(
            OpenCrxContextFactory.persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName, 
                session.getId()
            ),
            providerName,
            segmentName,
            new Short(session.getServletContext().getInitParameter("currencyCode")).shortValue(),
            locale,
            session.getServletContext().getInitParameter("salesTaxTypeName")
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static PersistenceManagerFactory persistenceManagerFactory = null;
    private static Model_1_0 model = null;
    
}

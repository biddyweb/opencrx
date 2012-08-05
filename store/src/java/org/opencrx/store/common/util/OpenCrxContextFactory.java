/*
 * ====================================================================
 * Project:     opencCRX/Store, http://www.opencrx.org/
 * Name:        $Id: OpenCrxContextFactory.java,v 1.7 2007/12/12 14:34:10 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/12 14:34:10 $
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.base.accessor.jmi.spi.PersistenceManagerFactory_1;
import org.openmdx.base.object.jdo.ConfigurableProperties_2_0;
import org.openmdx.compatibility.application.dataprovider.transport.ejb.cci.Dataprovider_1ConnectionFactoryImpl;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Dataprovider_1ConnectionFactory;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.openmdx.model1.accessor.basic.spi.Model_1;

/**
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public class OpenCrxContextFactory {
    
    //-----------------------------------------------------------------------
    public static OpenCrxContext createContext(
        HttpSession session
    ) throws Exception {

        // Load models
        if(OpenCrxContextFactory.model == null) {
            Model_1_0 model = new Model_1();
            model.addModels(OPENCRX_MODEL_PACKAGES);
        }
        
        // Get persistence manager factory
        if(OpenCrxContextFactory.persistenceManagerFactory == null) {
            Map configuration = new HashMap();
            Context initialContext = new InitialContext();
            configuration.put(
                Dataprovider_1ConnectionFactory.class.getName(),
                new Dataprovider_1ConnectionFactoryImpl(
                    initialContext,
                    "data",
                    new String[]{"java:comp/env/ejb"}
                )
            );
            configuration.put(
                ConfigurableProperties_2_0.FACTORY_CLASS,
                PersistenceManagerFactory_1.class.getName()
            );
            configuration.put(
                ConfigurableProperties_2_0.OPTIMISTIC,
                Boolean.TRUE.toString()
            );
            configuration.put(
                ConfigurableProperties_2_0.BINDING_PACKAGE_SUFFIX,
                "jmi1"
            );            
            OpenCrxContextFactory.persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(
                configuration
            );            
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
    private static final List OPENCRX_MODEL_PACKAGES = Arrays.asList(
        new String[]{
            "org:w3c",
            "org:openmdx:base",
            "org:openmdx:datastore1",
            "org:openmdx:filter1",
            "org:opencrx",
            "org:opencrx:kernel",
            "org:opencrx:kernel:base",
            "org:opencrx:kernel:generic",
            "org:opencrx:kernel:document1",
            "org:opencrx:kernel:workflow1",
            "org:opencrx:kernel:building1",
            "org:opencrx:kernel:address1",
            "org:opencrx:kernel:account1",
            "org:opencrx:kernel:product1",
            "org:opencrx:kernel:contract1",
            "org:opencrx:kernel:activity1",
            "org:opencrx:kernel:forecast1",
            "org:opencrx:kernel:code1",
            "org:opencrx:kernel:uom1",
            "org:oasis_open",
            "org:openmdx:generic1",
            "org:openmdx:compatibility:view1",
            "org:opencrx:kernel:home1",
            "org:opencrx:security",
            "org:openmdx:security:realm1",
            "org:openmdx:security:authorization1",
            "org:openmdx:security:authentication1",
            "org:opencrx:security:identity1",
            "org:opencrx:security:realm1",
            "org:opencrx:kernel:reservation1",
            "org:opencrx:kernel:admin1",
            "org:openmdx:compatibility:document1",
            "org:opencrx:kernel:model1",
            "org:opencrx:kernel:ras1",
            "org:opencrx:kernel:depot1",
            "org:openmdx:compatibility:state1",               
        }
    );
    
    private static PersistenceManagerFactory persistenceManagerFactory = null;
    private static Model_1_0 model = null;
    
}

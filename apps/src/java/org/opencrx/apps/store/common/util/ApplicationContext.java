/*
 * ====================================================================
 * Project:     openCRX/Apps, http://www.opencrx.org/
 * Name:        $Id: ApplicationContext.java,v 1.1 2009/11/27 18:23:05 wfro Exp $
 * Description: Store application context
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/11/27 18:23:05 $
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
package org.opencrx.apps.store.common.util;

import java.util.Locale;

import javax.jdo.PersistenceManager;

import org.openmdx.base.naming.Path;

public class ApplicationContext {

    //-----------------------------------------------------------------------
    public ApplicationContext(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        Short configuredCurrencyCode,
        Locale configuredLocale,
        String configuredSalesTaxTypeName         
    ) {
        this.pm = pm;
        this.providerName = providerName;
        this.segmentName = segmentName;
        this.configuredCurrencyCode = configuredCurrencyCode;
        this.configuredLocale = configuredLocale;
        this.configuredSalesTaxTypeName = configuredSalesTaxTypeName;
        this.accountSegment = 
            (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
                new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + this.providerName + "/segment/" + this.segmentName)
            );
        this.contractSegment = 
            (org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/" + this.providerName + "/segment/" + this.segmentName)
            );
        this.productSegment = 
            (org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
                new Path("xri://@openmdx*org.opencrx.kernel.product1/provider/" + this.providerName + "/segment/" + this.segmentName)
            );        
    }
    
    //-----------------------------------------------------------------------
    public PersistenceManager getPersistenceManager(
    ) {
        return this.pm;
    }
    
    //-----------------------------------------------------------------------
    public String getProviderName(
    ) {
        return this.providerName;
    }
    
    //-----------------------------------------------------------------------
    public String getSegmentName(
    ) {
        return this.segmentName;
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
    ) {
        return this.accountSegment;
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.contract1.jmi1.Segment getContractSegment(
    ) {
        return this.contractSegment;
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.Segment getProductSegment(
    ) {
        return this.productSegment;
    }
    
    //-----------------------------------------------------------------------
    public short getConfiguredCurrencyCode(
    ) {
        return this.configuredCurrencyCode;
    }

    //-----------------------------------------------------------------------
    public Locale getConfiguredLocale(
    ) {
        return this.configuredLocale;
    }

    //-----------------------------------------------------------------------
    public String getConfiguredSalesTaxTypeName(
    ) {
        return this.configuredSalesTaxTypeName;
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final String ID = ApplicationContext.class.getName();

    private final Short configuredCurrencyCode;
    private final Locale configuredLocale;
    private final String configuredSalesTaxTypeName; 
    
    private final PersistenceManager pm;
    private final String providerName;
    private final String segmentName;
    private final org.opencrx.kernel.account1.jmi1.Segment accountSegment;
    private final org.opencrx.kernel.contract1.jmi1.Segment contractSegment;
    private final org.opencrx.kernel.product1.jmi1.Segment productSegment;
    
}

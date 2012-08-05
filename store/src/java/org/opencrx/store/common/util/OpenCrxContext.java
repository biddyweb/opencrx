/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: OpenCrxContext.java,v 1.10 2007/12/18 17:39:01 wfro Exp $
 * Description: openCRX context
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/18 17:39:01 $
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

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account1Package;
import org.opencrx.kernel.contract1.jmi1.Contract1Package;
import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.openmdx.base.jmi1.Authority;

public class OpenCrxContext {

    public OpenCrxContext(
        PersistenceManager persistenceManager,
        String providerName,
        String segmentName,
        short storeCurrencyCode, // = 840; // USD
        Locale storeLocale, // = new Locale("en", "US");
        String storeSalesTaxTypeName // = "MWSt_CH_7.6%";         
    ) {
        this.persistenceManager = persistenceManager;
        this.providerName = providerName;
        this.segmentName = segmentName;
        this.currencyCode = storeCurrencyCode;
        this.storeLocale = storeLocale;
        this.salesTaxTypeName = storeSalesTaxTypeName;
        this.basePackage = (org.opencrx.kernel.base.jmi1.BasePackage)((Authority)persistenceManager.getObjectById(
            Authority.class,
            org.opencrx.kernel.base.jmi1.BasePackage.AUTHORITY_XRI
        )).refImmediatePackage();
        this.genericPackage = (org.opencrx.kernel.generic.jmi1.GenericPackage)((Authority)persistenceManager.getObjectById(
            Authority.class,
            org.opencrx.kernel.generic.jmi1.GenericPackage.AUTHORITY_XRI
        )).refImmediatePackage();
        this.accountPackage = (Account1Package)((Authority)persistenceManager.getObjectById(
            Authority.class,
            Account1Package.AUTHORITY_XRI
        )).refImmediatePackage();
        this.accountSegment = 
            (org.opencrx.kernel.account1.jmi1.Segment)persistenceManager.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.account1/provider/" + this.providerName + "/segment/" + this.segmentName
            );
        this.contractPackage = (Contract1Package)((Authority)persistenceManager.getObjectById(
            Authority.class,
            Contract1Package.AUTHORITY_XRI
        )).refImmediatePackage();
        this.contractSegment = 
            (org.opencrx.kernel.contract1.jmi1.Segment)persistenceManager.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.contract1/provider/" + this.providerName + "/segment/" + this.segmentName
            );
        this.productPackage = (Product1Package)((Authority)persistenceManager.getObjectById(
            Authority.class,
            Product1Package.AUTHORITY_XRI
        )).refImmediatePackage();
        this.productSegment = 
            (org.opencrx.kernel.product1.jmi1.Segment)persistenceManager.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.product1/provider/" + this.providerName + "/segment/" + this.segmentName
            );        
    }
    
    public PersistenceManager getPersistenceManager(
    ) {
        return this.persistenceManager;
    }
    
    public String getProviderName(
    ) {
        return this.providerName;
    }
    
    public org.opencrx.kernel.base.jmi1.BasePackage getBasePackage(
    ) {
        return this.basePackage;
    }
    
    public org.opencrx.kernel.generic.jmi1.GenericPackage getGenericPackage(
    ) {
        return this.genericPackage;
    }
    
    public Account1Package getAccountPackage(
    ) {
        return this.accountPackage;
    }
    
    public org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
    ) {
        return this.accountSegment;
    }

    public org.opencrx.kernel.contract1.jmi1.Segment getContractSegment(
    ) {
        return this.contractSegment;
    }

    public Contract1Package getContractPackage(
    ) {
        return this.contractPackage;
    }
    
    public org.opencrx.kernel.product1.jmi1.Segment getProductSegment(
    ) {
        return this.productSegment;
    }
    
    public Product1Package getProductPackage(
    ) {
        return this.productPackage;
    }
    
    //-----------------------------------------------------------------------
    public short getStoreCurrencyCode(
    ) {
        return this.currencyCode;
    }

    //-----------------------------------------------------------------------
    public Locale getStoreLocale(
    ) {
        return this.storeLocale;
    }

    //-----------------------------------------------------------------------
    public String getStoreSalesTaxTypeName(
    ) {
        return this.salesTaxTypeName;
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------    
    public static final String STORE_SCHEMA = "opencrx-store:";    
    public static final short PRICE_USAGE_CONSUMER = 100;
    
    private final short currencyCode;
    private final Locale storeLocale;
    private final String salesTaxTypeName; 
    
    private final PersistenceManager persistenceManager;
    private final String providerName;
    private final String segmentName;
    private final org.opencrx.kernel.base.jmi1.BasePackage basePackage; 
    private final org.opencrx.kernel.generic.jmi1.GenericPackage genericPackage; 
    private final org.opencrx.kernel.account1.jmi1.Segment accountSegment;
    private final Account1Package accountPackage; 
    private final org.opencrx.kernel.contract1.jmi1.Segment contractSegment;
    private final Contract1Package contractPackage;
    private final org.opencrx.kernel.product1.jmi1.Segment productSegment;
    private final Product1Package productPackage;
    
}

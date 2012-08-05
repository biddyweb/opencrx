/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Products.java,v 1.27 2008/02/24 12:12:41 wfro Exp $
 * Description: Products
 * Revision:    $Revision: 1.27 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/24 12:12:41 $
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
package org.opencrx.kernel.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.product1.jmi1.Product;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.compatibility.base.marshalling.Marshaller;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;

public class Products {

    //-----------------------------------------------------------------------
    public Products(
        Backend backend
    ) {
        this.backend = backend;
    }

    //-------------------------------------------------------------------------
    public static class PriceLevelMarshaller
        implements Marshaller {

        public PriceLevelMarshaller(
            String nameReplacementRegex,
            String nameReplacementValue,
            Date validFrom,
            Date validTo
        ) {
           this.nameReplacementRegex = nameReplacementRegex;
           this.nameReplacementValue = nameReplacementValue;
           this.validFrom = validFrom;
           this.validTo = validTo;           
        }
        
        public Object marshal(Object s) throws ServiceException {
            DataproviderObject priceLevel = new DataproviderObject(
                (DataproviderObject_1_0)s
            );
            if(
                (priceLevel.values("name").get(0) != null) && 
                (this.nameReplacementRegex != null) && 
                (this.nameReplacementValue != null)
            ) {
                String name = (String)priceLevel.values("name").get(0);
                if(name != null) {
                    priceLevel.clearValues("name").add(
                        name.replaceAll(
                            this.nameReplacementRegex, 
                            this.nameReplacementValue
                        )
                    );
                }
            }
            if(this.validFrom != null) {
                priceLevel.clearValues("validFrom").add(
                    DateFormat.getInstance().format(this.validFrom)
                );
            }
            priceLevel.clearValues("validTo");
            if(this.validTo != null) {
                priceLevel.values("validTo").add(
                     DateFormat.getInstance().format(this.validTo)
                );
            }
            priceLevel.clearValues("isFinal").add(
                Boolean.FALSE
            );
            return priceLevel;                       
        }
        
        public Object unmarshal(Object s) {
            throw new UnsupportedOperationException();
        }
                
        private final String nameReplacementRegex;
        private final String nameReplacementValue;
        private final Date validFrom;
        private final Date validTo;
        
    }
    
    //-------------------------------------------------------------------------
    public static class ProductPhasePriceLevelMarshaller
        implements Marshaller {

        public ProductPhasePriceLevelMarshaller(
            String nameReplacementRegex,
            String nameReplacementValue,
            String productPhaseKey
        ) {
           this.nameReplacementRegex = nameReplacementRegex;
           this.nameReplacementValue = nameReplacementValue;
           this.productPhaseKey = productPhaseKey;
        }
        
        public Object marshal(Object s) throws ServiceException {
            DataproviderObject priceLevel = new DataproviderObject(
                (DataproviderObject_1_0)s
            );
            if(
                (priceLevel.values("name").get(0) != null) && 
                (this.nameReplacementRegex != null) && 
                (this.nameReplacementValue != null)
            ) {
                String name = (String)priceLevel.values("name").get(0);
                if(name != null) {
                    priceLevel.clearValues("name").add(
                        name.replaceAll(
                            this.nameReplacementRegex, 
                            this.nameReplacementValue
                        )
                    );
                }
            }
            if(this.productPhaseKey != null) {
                priceLevel.clearValues("productPhase").add(
                    this.productPhaseKey
                );
            }
            priceLevel.clearValues("isFinal").add(
                Boolean.FALSE
            );
            return priceLevel;                       
        }
        
        public Object unmarshal(Object s) {
            throw new UnsupportedOperationException();
        }
                
        private final String nameReplacementRegex;
        private final String nameReplacementValue;
        private final String productPhaseKey;
        
    }
    
    //-------------------------------------------------------------------------
    /**
     * Clone ProductConfigurations stored under fromIdentity to toIdentity.
     * 
     * @return number of cloned configurations
     */
    public int cloneProductConfigurationSet(
        Path fromIdentity,
        Path toIdentity,
        boolean cloneDefaultOnly,
        boolean updateCurrentConfig
    ) throws ServiceException {
        List configurations = this.backend.getDelegatingRequests().addFindRequest(
            fromIdentity.getChild("configuration"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        for(
            Iterator i = configurations.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 configuration = (DataproviderObject_1_0)i.next();
            if(
                !cloneDefaultOnly ||                    
                ((configuration.values("isDefault").size() > 0) && ((Boolean)configuration.values("isDefault").get(0)).booleanValue())
            ) {
                this.backend.getCloneable().cloneAndUpdateReferences(
                    configuration,
                    toIdentity.getChild("configuration"),
                    null,
                    "property",
                    true
                );
            }
        }
        // configType
        DataproviderObject_1_0 from = this.backend.retrieveObject(fromIdentity);
        DataproviderObject to = this.backend.retrieveObjectForModification(
            toIdentity
        );
        to.clearValues("configType").addAll(
            from.values("configType")
        );
        
        // currentConfig
        if(updateCurrentConfig) {
            // Set current configuration to default config
            configurations = this.backend.getDelegatingRequests().addFindRequest(
                toIdentity.getChild("configuration"),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            Path defaultConfigurationIdentity = null;   
            for(
                Iterator i = configurations.iterator();
                i.hasNext();
            ) {
                DataproviderObject_1_0 configuration = (DataproviderObject_1_0)i.next();
                if((configuration.values("isDefault").size() > 0) && ((Boolean)configuration.values("isDefault").get(0)).booleanValue()) {
                    defaultConfigurationIdentity = configuration.path();
                    break;
                }
            }
            if(defaultConfigurationIdentity != null) {
                to.clearValues("currentConfig").add(defaultConfigurationIdentity);
            }
        }
        return configurations.size();
    }
    
    //-------------------------------------------------------------------------
    public void setConfigurationType(
        Path productIdentity,
        Path configurationTypeSetIdentity
    ) throws ServiceException {
        this.backend.removeAll(
            productIdentity.getChild("configuration"),
            null,
            0,
            Integer.MAX_VALUE,
            new HashSet()
        );
        List configurationTypes = this.backend.getDelegatingRequests().addFindRequest(
            configurationTypeSetIdentity.getChild("configurationType"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        Map objectMarshallers = new HashMap();
        objectMarshallers.put(
            "org:opencrx:kernel:product1:ProductConfigurationType",
            new Marshaller() {
                public Object marshal(Object s) throws ServiceException {
                    DataproviderObject configuration = new DataproviderObject(
                        (DataproviderObject_1_0)s
                    );
                    configuration.clearValues(
                        SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:product1:ProductConfiguration"
                    );
                    return configuration;                       
                }
                public Object unmarshal(Object s) {
                    throw new UnsupportedOperationException();
                }
            }
        );
        for(
            Iterator i = configurationTypes.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 configurationType = (DataproviderObject_1_0)i.next();
            DataproviderObject configuration =
                this.backend.getCloneable().cloneAndUpdateReferences(
                    configurationType,
                    productIdentity.getChild("configuration"),
                    objectMarshallers,
                    "property",
                    true
                );
            configuration = this.backend.retrieveObjectForModification(
                configuration.path()
            );
            configuration.clearValues("configType").add(
                configurationType.path()
            );
        }
        DataproviderObject modifiedProduct = this.backend.retrieveObjectForModification(
            productIdentity
        );
        modifiedProduct.clearValues("configType").add(
             configurationTypeSetIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public void unsetConfigurationType(
        Path bundledProdcutIdentity
    ) throws ServiceException {
        DataproviderObject modifiedProduct = this.backend.retrieveObjectForModification(
            bundledProdcutIdentity
        );
        modifiedProduct.clearValues("configType");
        modifiedProduct.clearValues("currentConfig");
        // Remove existing configuration
        this.backend.removeAll(
            bundledProdcutIdentity.getChild("configuration"),
            null,
            0,
            Integer.MAX_VALUE,
            new HashSet()
        );        
    }
    
    //-------------------------------------------------------------------------
    public void assignProduct(
        Path bundledProductIdentity,
        Product product
    ) throws ServiceException {
        DataproviderObject modifiedBundledProduct = this.backend.retrieveObjectForModification(
            bundledProductIdentity
        );
        modifiedBundledProduct.clearValues("product").add(
            product.refGetPath()
        );
        if(product.getConfigType() != null) {
            modifiedBundledProduct.clearValues("configType").add(
                product.getConfigType().refGetPath()
            );
        }
        // Remove existing configuration
        this.backend.removeAll(
            bundledProductIdentity.getChild("configuration"),
            null,
            0,
            Integer.MAX_VALUE,
            new HashSet()
        );
        this.cloneProductConfigurationSet(
            product.refGetPath(),
            bundledProductIdentity,
            false,
            true
        );
        // Remove existing depotReference
        this.backend.removeAll(
            bundledProductIdentity.getChild("depotReference"),
            null,
            0,
            Integer.MAX_VALUE,            
            new HashSet()
        );
        // Copy depot references from product
        List depotReferences = this.backend.getDelegatingRequests().addFindRequest(
            product.refGetPath().getChild("depotReference"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );        
        for(
            Iterator i = depotReferences.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 depotReference = (DataproviderObject_1_0)i.next();
            this.backend.getCloneable().cloneAndUpdateReferences(
                depotReference,
                bundledProductIdentity.getChild("depotReference"),
                null,
                "",
                true
            );
        }
    }
    
    //-------------------------------------------------------------------------
    public FilterProperty[] getProductFilterProperties(
        Path productFilterIdentity,
        boolean forCounting
    ) throws ServiceException {
        List filterProperties = this.backend.getDelegatingRequests().addFindRequest(
            productFilterIdentity.getChild("productFilterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List filter = new ArrayList();
        boolean hasQueryFilterClause = false;        
        for(
            Iterator i = filterProperties.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 filterProperty = (DataproviderObject_1_0)i.next();
            String filterPropertyClass = (String)filterProperty.values(SystemAttributes.OBJECT_CLASS).get(0);

            Boolean isActive = (Boolean)filterProperty.values("isActive").get(0);
            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:product1:ProductQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            new Object[]{
                                (forCounting ? Database_1_Attributes.HINT_COUNT : "") +
                                filterProperty.values("clause").get(0)
                            }
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                        )
                    );
                    // stringParam
                    List values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new String[values.size()])
                        )
                    );
                    // integerParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new Integer[values.size()])
                        )
                    );
                    // decimalParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new BigDecimal[values.size()])
                        )
                    );
                    // booleanParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new Boolean[values.size()])
                        )
                    );
                    // dateParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATE_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new XMLGregorianCalendar[values.size()])
                        )
                    );
                    // dateTimeParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new Date[values.size()])
                        )
                    );
                    hasQueryFilterClause = true;                    
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterProperty.values("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterProperty.values("filterOperator").get(0)).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterProperty.values("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterProperty.values("filterQuantor").get(0)).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:product1:ProductClassificationFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "classification",
                                filterOperator,
                                filterProperty.values("classification").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:DefaultSalesTaxTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "salesTaxType",
                                filterOperator,
                                filterProperty.values("salesTaxType").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:CategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterProperty.values("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:PriceUomFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "priceUom",
                                filterOperator,
                                filterProperty.values("priceUom").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterProperty.values("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{
                        Database_1_Attributes.HINT_COUNT + "(1=1)"
                    }
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                )
            );            
        }        
        return (FilterProperty[])filter.toArray(new FilterProperty[filter.size()]);
    }

    //-------------------------------------------------------------------------    
    public List getPriceListEntries(
        DataproviderObject_1_0 priceLevel,
        boolean applyPriceFilter
    ) throws ServiceException {
        List priceFilter = new ArrayList();
        priceFilter.add(
            new FilterProperty(
                Quantors.THERE_EXISTS,
                "priceLevel",
                FilterOperators.IS_IN,
                new Object[]{priceLevel.path()}                    
            )
        );
        if(applyPriceFilter) {
            if(!priceLevel.values("priceCurrency").isEmpty()) {
                priceFilter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "priceCurrency",
                        FilterOperators.IS_IN,
                        priceLevel.values("priceCurrency").toArray()                    
                    )
                );                
            }
            if(!priceLevel.values("priceUsage").isEmpty()) {
                priceFilter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "usage",
                        FilterOperators.IS_IN,
                        priceLevel.values("priceUsage").toArray()                  
                    )
                );                
            }
        }
        return this.backend.getDelegatingRequests().addFindRequest(
            priceLevel.path().getPrefix(5).getChild("priceListEntry"),
            (FilterProperty[])priceFilter.toArray(new FilterProperty[priceFilter.size()]),
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            BATCHING_MODE_SIZE,
            Directions.ASCENDING            
        );        
    }
    
    //-------------------------------------------------------------------------    
    public List findPrices(
        DataproviderObject_1_0 priceLevel,
        boolean useBasedOnPriceLevel,
        Path productIdentity
    ) throws ServiceException {
        List priceFilter = new ArrayList();
        // Get basedOn price level if useBasedOnPriceLevel==true
        priceLevel = useBasedOnPriceLevel
            ? priceLevel.values("basedOn").isEmpty()
                ? null
                : this.backend.retrieveObjectFromDelegation((Path)priceLevel.values("basedOn").get(0))
            : priceLevel;
                    
        // filter on priceCurrency if defined
        if((priceLevel != null) && !priceLevel.values("priceCurrency").isEmpty()) {
            priceFilter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceCurrency",
                    FilterOperators.IS_IN,
                    priceLevel.values("priceCurrency").toArray()                    
                )
            );
        }
        // filter on priceUsage if defined
        if((priceLevel != null) && !priceLevel.values("priceUsage").isEmpty()) {
            priceFilter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "usage",
                    FilterOperators.IS_IN,
                    priceLevel.values("priceUsage").toArray()                    
                )
            );
        }
        // filter on priceLevel if defined
        if(priceLevel != null) {
            priceFilter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceLevel",
                    FilterOperators.IS_IN,
                    new Object[]{priceLevel.path()}                    
                )
            );            
        }
        return this.backend.getDelegatingRequests().addFindRequest(
            productIdentity.getChild("basePrice"),
            (FilterProperty[])priceFilter.toArray(new FilterProperty[priceFilter.size()]),
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            BATCHING_MODE_SIZE,
            Directions.ASCENDING            
        );        
    }

    //-------------------------------------------------------------------------
    public int clonePriceLevel(
        Path priceLevelIdentity,
        Short processingMode,
        Map<String,Marshaller> priceLevelMarshallers
    ) throws ServiceException {
        DataproviderObject_1_0 priceLevel = this.backend.retrieveObject(
            priceLevelIdentity
        );
        List allPriceLevels = this.backend.getDelegatingRequests().addFindRequest(
            priceLevelIdentity.getParent(), 
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0, 
            BATCHING_MODE_SIZE,
            Directions.ASCENDING
        );
        Set dependentPriceLevels = this.getDependentPriceLevels(
            priceLevelIdentity, 
            allPriceLevels
        );
        if(
            (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_NO_PRICES) ||
            (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_INCLUDE_PRICES)
        ) {
            // Mapping <source pricel level identity, cloned price level identity>
            Map clonedIdentities = new HashMap();
            // Clone price level
            DataproviderObject_1_0 clonedPriceLevel = this.backend.getCloneable().cloneAndUpdateReferences(
                priceLevel, 
                priceLevelIdentity.getParent(), 
                priceLevelMarshallers, 
                "priceModifier, accountFilterProperty, assignedAccount, productFilterProperty", 
                false
            );
            clonedIdentities.put(
                priceLevelIdentity, 
                clonedPriceLevel.path()
            );            
            // Clone dependent price levels
            for(Iterator i = dependentPriceLevels.iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 dependentPriceLevel = (DataproviderObject_1_0)i.next();
                DataproviderObject_1_0 clonedDependentPriceLevel = this.backend.getCloneable().cloneAndUpdateReferences(
                    dependentPriceLevel, 
                    priceLevelIdentity.getParent(), 
                    priceLevelMarshallers, 
                    "priceModifier, accountFilterProperty, assignedAccount, productFilterProperty", 
                    false
                );     
                clonedIdentities.put(
                    dependentPriceLevel.path(), 
                    clonedDependentPriceLevel.path()
                );
            }
            // Update priceLevel.basedOn references
            for(Iterator i = clonedIdentities.values().iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 level = this.backend.retrieveObjectFromDelegation(
                    (Path)i.next()
                );
                if(
                    !level.values("basedOn").isEmpty() &&
                    clonedIdentities.keySet().contains(level.values("basedOn").get(0))
                ) {
                    DataproviderObject modifiedLevel = this.backend.retrieveObjectForModification(
                        level.path()
                    );
                    Path cloneIdentity = (Path)clonedIdentities.get(level.values("basedOn").get(0));
                    modifiedLevel.clearValues("basedOn").add(
                        cloneIdentity
                    );
                }                
            }
            // Clone prices if price level is basic price level
            if(
                (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_INCLUDE_PRICES) &&
                priceLevel.values("baseOn").isEmpty()
            ) {
                List priceListEntries = this.getPriceListEntries(
                    priceLevel,
                    true
                );
                for(
                    Iterator i = priceListEntries.iterator(); 
                    i.hasNext(); 
                ) {
                    DataproviderObject_1_0 priceListEntry = (DataproviderObject_1_0)i.next();
                    DataproviderObject_1_0 price = this.backend.retrieveObjectFromDelegation(
                        (Path)priceListEntry.values("basePrice").get(0)
                    );
                    DataproviderObject clonedPrice = new DataproviderObject(
                        price.path().getParent().getChild(this.backend.getUidAsString())
                    );
                    clonedPrice.addClones(
                        price,
                        true
                    );
                    clonedPrice.clearValues("priceLevel").add(
                        clonedPriceLevel.path()
                    );
                    this.backend.getDelegatingRequests().addCreateRequest(
                        clonedPrice
                    );
                }
            }
        }
        return dependentPriceLevels.size() + 1;        
    }

    //-------------------------------------------------------------------------
    public int clonePriceLevel(
        Path priceLevelIdentity,
        Short processingMode,
        String nameReplacementRegex,
        String nameReplacementValue,
        Date validFrom,
        Date validTo
    ) throws ServiceException {
        Map<String,Marshaller> priceLevelMarshallers = new HashMap<String,Marshaller>();
        priceLevelMarshallers.put(
            "org:opencrx:kernel:product1:PriceLevel",
            new PriceLevelMarshaller(
                nameReplacementRegex,
                nameReplacementValue,
                validFrom,
                validTo                    
            )
        );
        return this.clonePriceLevel(
            priceLevelIdentity, 
            processingMode, 
            priceLevelMarshallers
        );
    }
    
    //-------------------------------------------------------------------------
    public int cloneProductPhasePriceLevel(
        Path priceLevelIdentity,
        Short processingMode,
        String nameReplacementRegex,
        String nameReplacementValue,
        String productPhaseKey
    ) throws ServiceException {
        Map<String,Marshaller> priceLevelMarshallers = new HashMap<String,Marshaller>();
        priceLevelMarshallers.put(
            "org:opencrx:kernel:product1:ProductPhasePriceLevel",
            new ProductPhasePriceLevelMarshaller(
                nameReplacementRegex,
                nameReplacementValue,
                productPhaseKey
            )
        );
        return this.clonePriceLevel(
            priceLevelIdentity, 
            processingMode, 
            priceLevelMarshallers
        );
    }
    
    //-------------------------------------------------------------------------
    public Set getDependentPriceLevels(
        Path priceLevelIdentity,
        List allPriceLevels
    ) throws ServiceException {
        Set dependentPriceLevels = new HashSet();
        for(Iterator i = allPriceLevels.iterator(); i.hasNext(); ) {
            DataproviderObject_1_0 currentLevel = (DataproviderObject_1_0)i.next();
            if(
                !currentLevel.values("basedOn").isEmpty() &&
                currentLevel.values("basedOn").get(0).equals(priceLevelIdentity)
            ) {
                dependentPriceLevels.add(currentLevel);
                dependentPriceLevels.addAll(
                    this.getDependentPriceLevels(
                        currentLevel.path(),
                        allPriceLevels
                    )
                );
            }
        }
        return dependentPriceLevels;
    }
    
    //-------------------------------------------------------------------------
    public int calculatePrices(
        Path priceLevelIdentity,
        Short processingMode,
        Date includeProductsModifiedSince
    ) throws ServiceException {
        DataproviderObject_1_0 priceLevel = this.backend.retrieveObject(
            priceLevelIdentity
        );
        if(processingMode == PROCESSING_MODE_TEST) {
            return this.calculatePrices(
                priceLevel, 
                true,
                includeProductsModifiedSince
            );
        }
        else if(processingMode == PROCESSING_MODE_PROCESS) {
            return this.calculatePrices(
                priceLevel, 
                false,
                includeProductsModifiedSince
            );            
        }
        else if(processingMode == PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT) {
            List allPriceLevels = this.backend.getDelegatingRequests().addFindRequest(
                priceLevel.path().getParent(), 
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0, 
                BATCHING_MODE_SIZE,
                Directions.ASCENDING
            );
            Set dependentPriceLevels = this.getDependentPriceLevels(
                priceLevel.path(),
                allPriceLevels
            );
            dependentPriceLevels.add(priceLevel);
            int numberProcessed = 0;
            for(Iterator i = dependentPriceLevels.iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 dependentPriceLevel = (DataproviderObject_1_0)i.next();
                numberProcessed += this.calculatePrices(
                    dependentPriceLevel, 
                    false,
                    includeProductsModifiedSince
                );                            
            }
            return numberProcessed;
        }
        return 0;
    }
    
    //-------------------------------------------------------------------------
    private boolean isEqualAttributeValue(
        DataproviderObject_1_0 o1,
        DataproviderObject_1_0 o2,
        String attributeName
    ) {
        if(
            (o1.getValues(attributeName) == null) ||
            (o2.getValues(attributeName) == null)
        ) {
            return o1.getValues(attributeName) == o2.getValues(attributeName);
        }
        if(
            (o1.values(attributeName).get(0) == null) ||
            (o2.values(attributeName).get(0) == null)
        ) {
            return o1.values(attributeName).get(0) == o2.values(attributeName).get(0);
        }
        if(o1.values(attributeName) instanceof Comparable) {
            return ((Comparable)o1.values(attributeName).get(0)).compareTo(o2.values(attributeName).get(0)) == 0;
        }
        else {
            return o1.values(attributeName).equals(o2.values(attributeName));            
        }        
    }
    
    //-------------------------------------------------------------------------
    private void applyPriceModifiers(
        DataproviderObject modifiedPrice,
        List priceModifiers
    ) {
        BigDecimal quantityFromPrice = (BigDecimal)modifiedPrice.values("quantityFrom").get(0);
        BigDecimal quantityToPrice = (BigDecimal)modifiedPrice.values("quantityTo").get(0);
        for(
            Iterator k = priceModifiers.iterator();
            k.hasNext();
        ) {
            DataproviderObject_1_0 priceModifier = (DataproviderObject_1_0)k.next();
            BigDecimal quantityFromModifier = (BigDecimal)priceModifier.values("quantityFrom").get(0);
            quantityFromModifier = quantityFromModifier == null ? quantityFromPrice : quantityFromModifier;
            BigDecimal quantityToModifier = (BigDecimal)priceModifier.values("quantityTo").get(0);
            quantityToModifier = quantityToModifier == null ? quantityToPrice : quantityToModifier;
            // Apply modifier if quantity range is larger than the price quantity range
            if(
                ((quantityFromModifier == null) || (quantityFromModifier.compareTo(quantityFromPrice) <= 0)) &&
                ((quantityToModifier == null) || (quantityToModifier.compareTo(quantityToPrice) >= 0))
            ) {
                String modifierType = (String)priceModifier.values(SystemAttributes.OBJECT_CLASS).get(0);
                if("org:opencrx:kernel:product1:DiscountPriceModifier".equals(modifierType)) {
                    boolean discountIsPercentageModifier =
                        !priceModifier.values("discountIsPercentage").isEmpty() && 
                        ((Boolean)priceModifier.values("discountIsPercentage").get(0)).booleanValue();
                    boolean discountIsPercentagePrice =
                        !modifiedPrice.values("discountIsPercentage").isEmpty() && 
                        ((Boolean)modifiedPrice.values("discountIsPercentage").get(0)).booleanValue();
                    if(discountIsPercentageModifier == discountIsPercentagePrice) {
                        modifiedPrice.clearValues("discount").addAll(
                            priceModifier.values("discount")
                        );
                    }
                }
                else if("org:opencrx:kernel:product1:LinearPriceModifier".equals(modifierType)) {
                    BigDecimal priceMultiplier = (BigDecimal)priceModifier.values("priceMultiplier").get(0);
                    priceMultiplier = priceMultiplier == null ? new BigDecimal(0) : priceMultiplier;
                    BigDecimal priceOffset = (BigDecimal)priceModifier.values("priceOffset").get(0);
                    priceOffset = priceOffset == null ? new BigDecimal(0) : priceOffset;
                    BigDecimal roundingFactor = (BigDecimal)priceModifier.values("roundingFactor").get(0);
                    roundingFactor = roundingFactor == null ? new BigDecimal(1) : roundingFactor;
                    BigDecimal price = (BigDecimal)modifiedPrice.values("price").get(0);
                    price = price == null ? new BigDecimal(0) : price;
                    price = 
                        new BigDecimal(
                            price.multiply(priceMultiplier).multiply(roundingFactor).add(
                                new BigDecimal(0.5)
                            ).toBigInteger()
                        ).divide(
                            roundingFactor, price.scale(), BigDecimal.ROUND_FLOOR 
                        ).add(
                            priceOffset
                        );
                    modifiedPrice.clearValues("price").add(price);
                }
            }
        }        
    }
    
    //-------------------------------------------------------------------------
    public int calculatePrices(
        DataproviderObject_1_0 priceLevel,
        boolean testOnly,
        Date includeProductsModifiedSince
    ) throws ServiceException {
        if(
            ((!priceLevel.values("isFinal").isEmpty()) && 
            ((Boolean)priceLevel.values("isFinal").get(0)).booleanValue()) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevel.path())
                },
                "Operation is not allowed for final price level."
            );                                                                
        }
        // No calculation required for base price levels
        boolean isBasePriceLevel = 
            !priceLevel.values("basedOn").isEmpty();
        List priceModifiers = this.backend.getDelegatingRequests().addFindRequest(
            priceLevel.path().getChild("priceModifier"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            BATCHING_MODE_SIZE,
            Directions.ASCENDING
        );            
        // Get all products matching the product filter
        List productFilter = new ArrayList();
        if(includeProductsModifiedSince != null) {
            productFilter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    SystemAttributes.MODIFIED_AT,
                    FilterOperators.IS_GREATER_OR_EQUAL,
                    new String[]{
                        DateFormat.getInstance().format(includeProductsModifiedSince)
                    }
                )
            );
        }        
        productFilter.addAll(
            Arrays.asList(
                this.getProductFilterProperties(
                    priceLevel.path(),
                    false
                )
            )
        );
        List filteredProducts = this.backend.getDelegatingRequests().addFindRequest(
            priceLevel.path().getPrefix(5).getChild("product"),
            (FilterProperty[])productFilter.toArray(new FilterProperty[productFilter.size()]),
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            BATCHING_MODE_SIZE,
            Directions.ASCENDING
        );
        int numberProcessed = 0;
        // Iterate all matching products and calculate prices
        for(
            Iterator i = filteredProducts.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 product = (DataproviderObject_1_0)i.next();
            Path productIdentity = new Path((String)product.values(SystemAttributes.OBJECT_IDENTITY).get(0));            
            List basedOnPrices = this.findPrices(
                priceLevel,
                !isBasePriceLevel, // useBasedOnPriceLevel
                productIdentity
            );
            List existingPrices = this.findPrices(
                priceLevel, 
                false, 
                productIdentity
            );
            for(
                Iterator j = basedOnPrices.iterator(); 
                j.hasNext(); 
            ) {
                DataproviderObject_1_0 basePrice = (DataproviderObject_1_0)j.next();
                if(!testOnly) {
                    // If no price modifiers are defined do not create new price objects. 
                    // Only add the price level to the existing price. A copy can be enforced
                    // by adding a 1:1 price modifier, e.g. a LinearPriceModifier with priceMultiplier=1.0 and
                    // priceOffset=0.0.
                    if(priceModifiers.isEmpty()) {
                        DataproviderObject modifiedBasePrice = this.backend.retrieveObjectForModification(
                            basePrice.path()
                        );
                        List priceLevels = modifiedBasePrice.values("priceLevel");
                        if(!priceLevels.contains(priceLevel.path())) {
                            priceLevels.add(priceLevel.path());
                        }
                    }
                    // Create new price and apply price modifiers
                    else {
                        DataproviderObject newPrice = new DataproviderObject(
                            basePrice.path().getParent().getChild(this.backend.getUidAsString())
                        );
                        newPrice.addClones(
                            basePrice,
                            true
                        );
                        this.applyPriceModifiers(
                            newPrice, 
                            priceModifiers
                        );
                        newPrice.clearValues("priceLevel").add(
                            priceLevel.path()
                        );
                        newPrice.clearValues("priceCurrency").addAll(
                            priceLevel.values("priceCurrency")
                        );
                        
                        // Create new price if no price for price level exists
                        if(existingPrices.isEmpty()) {
                            this.backend.getDelegatingRequests().addCreateRequest(
                                newPrice
                            );
                        }
                        // Update existing prices
                        else {
                            for(Iterator k = existingPrices.iterator(); k.hasNext(); ) {
                                DataproviderObject_1_0 existingPrice = (DataproviderObject_1_0)k.next();
                                // Compare price, discount, discountIsPercentage, quantityFrom, quantityTo
                                boolean priceIsEqual = true;
                                priceIsEqual &= this.isEqualAttributeValue(existingPrice, newPrice, "price");
                                priceIsEqual &= this.isEqualAttributeValue(existingPrice, newPrice, "discount");
                                priceIsEqual &= this.isEqualAttributeValue(existingPrice, newPrice, "quantityFrom");
                                priceIsEqual &= this.isEqualAttributeValue(existingPrice, newPrice, "quantityTo");
                                priceIsEqual &= this.isEqualAttributeValue(existingPrice, newPrice, "discountIsPercentage");
                                if(!priceIsEqual) {
                                    DataproviderObject modifiedBasePrice = this.backend.retrieveObjectForModification(
                                        existingPrice.path()
                                    );
                                    modifiedBasePrice.clearValues("price").addAll(newPrice.values("price"));
                                    modifiedBasePrice.clearValues("discount").addAll(newPrice.values("discount"));
                                    modifiedBasePrice.clearValues("quantityFrom").addAll(newPrice.values("quantityFrom"));
                                    modifiedBasePrice.clearValues("quantityTo").addAll(newPrice.values("quantityTo"));
                                    modifiedBasePrice.clearValues("discountIsPercentage").addAll(newPrice.values("discountIsPercentage"));
                                }
                            }
                        }
                    }
                }
            }
            numberProcessed++;
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePrices(
        Path priceLevelIdentity,
        boolean removePrices
    ) throws ServiceException {
        DataproviderObject_1_0 priceLevel = this.backend.retrieveObject(
            priceLevelIdentity
        );
        if(
            ((priceLevel.values("isFinal").size() > 0) && 
            ((Boolean)priceLevel.values("isFinal").get(0)).booleanValue())
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevelIdentity)
                },
                "Operation is not allowed for final price level."
            );                                                                
        }
        List priceListEntries = this.getPriceListEntries(
            priceLevel,
            false
        );
        // Collect identities of prices 
        // * to be removed or 
        // * unlinked (remove reference to price level) from price level
        List priceIdentitiesToBeRemoved = new ArrayList();
        List priceIdentitiesToBeUnlinked = new ArrayList();
        int numberProcessed = 0;
        for(
            Iterator i = priceListEntries.iterator(); 
            i.hasNext(); 
        ) {
            DataproviderObject_1_0 priceListEntry = (DataproviderObject_1_0)i.next();
            if(
                removePrices && 
                (priceListEntry.values("basePrice").size() > 0)
            ) {
                if(priceListEntry.values("priceLevel").size() > 1) {
                    priceIdentitiesToBeUnlinked.add(
                        priceListEntry.values("basePrice").get(0)
                    );
                }
                else {
                    priceIdentitiesToBeRemoved.add(
                        priceListEntry.values("basePrice").get(0)
                    );                    
                }
            }    
            numberProcessed++;
        }
        // Remove prices
        for(
            Iterator i = priceIdentitiesToBeRemoved.iterator();
            i.hasNext();
        ) {
            this.backend.removeObject((Path)i.next());
        }
        // Unlink prices
        for(
            Iterator i = priceIdentitiesToBeUnlinked.iterator();
            i.hasNext();
        ) {
            DataproviderObject price = this.backend.retrieveObjectForModification(
                (Path)i.next()
            );
            price.values("priceLevel").remove(
                priceLevel.path()
            );
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePrices(
        Path priceLevelIdentity,
        Short processingMode        
    ) throws ServiceException {
        int numberProcessed = 0;
        if(processingMode == PROCESSING_MODE_PROCESS) {
            numberProcessed = this.removePrices(
                priceLevelIdentity, 
                true
            );
        }
        else if(processingMode == PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT) {
            this.removePrices(
                priceLevelIdentity, 
                true
            );
            List allPriceLevels = this.backend.getDelegatingRequests().addFindRequest(
                priceLevelIdentity.getParent(), 
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0, 
                BATCHING_MODE_SIZE,
                Directions.ASCENDING
            );
            Set dependentPriceLevels = this.getDependentPriceLevels(
                priceLevelIdentity, 
                allPriceLevels
            );
            for(Iterator i = dependentPriceLevels.iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 dependentPriceLevel = (DataproviderObject_1_0)i.next();
                this.removePrices(
                    dependentPriceLevel.path(), 
                    true
                );
            }
            numberProcessed = 1 + dependentPriceLevels.size();
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePriceLevels(
        Path priceLevelIdentity,
        Short processingMode
    ) throws ServiceException {
        int numberProcessed = 0;
        if(processingMode == PROCESSING_MODE_PROCESS) {
            // Remove prices of price level (no dependent)
            this.removePrices(
                priceLevelIdentity, 
                processingMode
            );
            this.removePriceLevel(
                priceLevelIdentity,
                true
            );
            numberProcessed = 1;
        }
        else if(processingMode == PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT) {
            // Remove prices of price level and dependent
            this.removePrices(
                priceLevelIdentity, 
                processingMode
            );
            this.removePriceLevel(
                priceLevelIdentity,
                false
            );
            List allPriceLevels = this.backend.getDelegatingRequests().addFindRequest(
                priceLevelIdentity.getParent(), 
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0, 
                BATCHING_MODE_SIZE,
                Directions.ASCENDING
            );
            Set dependentPriceLevels = this.getDependentPriceLevels(
                priceLevelIdentity, 
                allPriceLevels
            );
            for(Iterator i = dependentPriceLevels.iterator(); i.hasNext(); ) {
                DataproviderObject_1_0 dependentPriceLevel = (DataproviderObject_1_0)i.next();
                this.removePriceLevel(
                    dependentPriceLevel.path(),
                    false
                );                
            }
            numberProcessed = 1 + dependentPriceLevels.size();
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int createInitialPrices(
        Path priceLevelIdentity,
        Short processingMode,
        Path priceUomIdentity,
        Date includeProductsCreatedSince
    ) throws ServiceException {
        DataproviderObject_1_0 priceLevel = this.backend.retrieveObject(
            priceLevelIdentity
        );
        // Price level must not be final
        if(
            !priceLevel.values("isFinal").isEmpty() && 
            ((Boolean)priceLevel.values("isFinal").get(0)).booleanValue()                
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevel.path())
                },
                "Operation is not allowed for final price level."
            );                                                                
        }
        // Price level must have price currency
        if(priceLevel.values("priceCurrency").isEmpty()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_PRICE_LEVEL_MUST_HAVE_CURRENCY,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevel.path())
                },
                "Price level must have price uom and price currency."
            );                                                                
        }
        // Get price modifiers
        List priceModifiers = this.backend.getDelegatingRequests().addFindRequest(
            priceLevel.path().getChild("priceModifier"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            BATCHING_MODE_SIZE,
            Directions.ASCENDING
        );                        
        // Get all products matching the product filter
        List productFilter = new ArrayList();
        if(includeProductsCreatedSince != null) {
            productFilter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    SystemAttributes.MODIFIED_AT,
                    FilterOperators.IS_GREATER_OR_EQUAL,
                    new String[]{
                        DateFormat.getInstance().format(includeProductsCreatedSince)
                    }
                )
            );
        }
        productFilter.addAll(
            Arrays.asList(
                this.getProductFilterProperties(
                    priceLevel.path(),
                    false
                )
            )
        );
        List products = this.backend.getDelegatingRequests().addFindRequest(
            priceLevel.path().getPrefix(5).getChild("product"),
            (FilterProperty[])productFilter.toArray(new FilterProperty[productFilter.size()]),
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        int numberProcessed = 0;
        for(
            Iterator i = products.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 product = (DataproviderObject_1_0)i.next();
            Path productIdentity = new Path((String)product.values(SystemAttributes.OBJECT_IDENTITY).get(0));
            List basePrices = this.findPrices(
                priceLevel,
                false,
                productIdentity
            );
            if(basePrices.isEmpty()) {
                DataproviderObject basePrice = new DataproviderObject(
                    productIdentity.getDescendant(new String[]{"basePrice", this.backend.getUidAsString()})
                );
                basePrice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:product1:ProductBasePrice");
                basePrice.values("priceLevel").add(
                    priceLevel.path()
                );
                basePrice.values("usage").addAll(
                    priceLevel.values("priceUsage")
                );
                basePrice.values("price").add(
                    new BigDecimal(0)
                );
                basePrice.values("priceCurrency").addAll(
                    priceLevel.values("priceCurrency")
                );
                basePrice.values("discount").add(
                    new BigDecimal(0)
                );
                basePrice.values("discountIsPercentage").add(
                    Boolean.FALSE
                );
                if(priceUomIdentity != null) {
                    basePrice.values("uom").add(priceUomIdentity);
                }
                this.applyPriceModifiers(
                    basePrice, 
                    priceModifiers
                );
                if(processingMode == PROCESSING_MODE_PROCESS) {
                    this.backend.getDelegatingRequests().addCreateRequest(
                        basePrice
                    );
                }
                numberProcessed++;
            }
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.GetPriceLevelResult getPriceLevel(
        org.opencrx.kernel.product1.jmi1.PricingRule pricingRule,
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract,
        org.opencrx.kernel.product1.jmi1.AbstractProduct product,
        org.opencrx.kernel.uom1.jmi1.Uom priceUom,
        BigDecimal quantity,
        Date pricingDate
    ) throws ServiceException {
        String script = (pricingRule.getGetPriceLevelScript() == null) || (pricingRule.getGetPriceLevelScript().length() == 0)
            ? DEFAULT_GET_PRICE_LEVEL_SCRIPT
            : pricingRule.getGetPriceLevelScript();
        org.opencrx.kernel.product1.jmi1.Product1Package productPkg = 
            (org.opencrx.kernel.product1.jmi1.Product1Package)this.backend.getDelegatingPkg().refPackage(
                org.opencrx.kernel.product1.jmi1.Product1Package.class.getName()
            );
        try {
            Class c = new ClassBodyEvaluator(script).evaluate();
            Method m = c.getMethod(
                "getPriceLevel", 
                new Class[] {
                    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                    org.opencrx.kernel.product1.jmi1.PricingRule.class, 
                    org.opencrx.kernel.contract1.jmi1.AbstractContract.class,
                    org.opencrx.kernel.product1.jmi1.AbstractProduct.class,
                    org.opencrx.kernel.uom1.jmi1.Uom.class,
                    java.math.BigDecimal.class,
                    java.util.Date.class
                }
            );
            org.opencrx.kernel.product1.jmi1.GetPriceLevelResult result = 
                (org.opencrx.kernel.product1.jmi1.GetPriceLevelResult)m.invoke(
                    null, 
                    new Object[] {
                        this.backend.getLocalPkg(),
                        pricingRule,
                        contract,
                        product,
                        priceUom,
                        quantity,
                        pricingDate
                    }
                );
            return result;
        }
        catch(CompileException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "Can not compile getPriceLevelScript:\n" +
                e.getMessage()
            );
        }
        catch(Parser.ParseException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "Can not parse getPriceLevelScript:\n" +
                e.getMessage()
            );
        }
        catch(Scanner.ScanException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "Can not scan getPriceLevelScript:\n" +
                e.getMessage()
            );
        }
        catch(NoSuchMethodException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "getPriceLevelScript does not define a method with the following signature:\n" +
                DEFAULT_GET_PRICE_LEVEL_SCRIPT
            );
        }
        catch(InvocationTargetException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "Can not invoke getPriceLevel():\n" +
                e.getTargetException().getMessage()
            );
        }
        catch(IllegalAccessException e) {
            return productPkg.createGetPriceLevelResult(
                null, null, null, null,
                STATUS_CODE_ERROR,
                "Illegal access when invoking getPriceLevel():\n" +
                e.getMessage()
            );
        }
    }

    //-------------------------------------------------------------------------
    public boolean hasDependentPriceLevels(
        Path priceLevelIdentity
    ) throws ServiceException {
        List dependentPriceLevels = this.backend.getDelegatingRequests().addFindRequest(
            priceLevelIdentity.getParent(),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "basedOn",
                    FilterOperators.IS_IN,
                    new Object[]{priceLevelIdentity}
                )
            }
        );
        return !dependentPriceLevels.isEmpty();
    }
    
    //-------------------------------------------------------------------------
    public void removePriceLevel(
        Path priceLevelIdentity,
        boolean checkDependencies
    ) throws ServiceException {
        if(checkDependencies && this.hasDependentPriceLevels(priceLevelIdentity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_BASEDON_PRICE_LEVEL,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevelIdentity)
                },
                "Can not delete price level. Other price levels are based on this price level."
            );                                                                            
        }
        // Check for prices which are assigned to price level
        List priceListEntries = this.backend.getDelegatingRequests().addFindRequest(
            priceLevelIdentity.getPrefix(5).getChild("priceListEntry"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceLevel",
                    FilterOperators.IS_IN,
                    new Object[]{priceLevelIdentity}
                    
                )
            }
        );
        if(!priceListEntries.isEmpty()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_PRICE_LEVEL_HAVING_PRICES,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevelIdentity)
                },
                "Can not delete price level. Price level has assigned prices."
            );                                                                                        
        }
        DataproviderObject_1_0 priceLevel = this.backend.retrieveObjectFromDelegation(priceLevelIdentity);
        if(
            !priceLevel.values("isFinal").isEmpty() && 
            ((Boolean)priceLevel.values("isFinal").get(0)).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                new BasicException.Parameter[]{
                    new BasicException.Parameter("param0", priceLevel.path())
                },
                "Can not delete final price level."
            );                                                                                                    
        }
        this.backend.removeObject(
            priceLevel.path()
        );
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredProduct(
        Path productFilterIdentity
    ) throws ServiceException {
        List products = this.backend.getDelegatingRequests().addFindRequest(
            productFilterIdentity.getPrefix(5).getChild("product"),
            this.getProductFilterProperties(
                productFilterIdentity,
                true
            ),
            AttributeSelectors.NO_ATTRIBUTES,
            null,
            0, 
            1,
            Directions.ASCENDING
        );
        return products.size();
    }
        
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public final static int BATCHING_MODE_SIZE = 1000;
    
    public static final short PROCESSING_MODE_NA = 0;
    public static final short PROCESSING_MODE_TEST = 1;
    public static final short PROCESSING_MODE_PROCESS = 2;
    public static final short PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT = 3;
    public static final short PROCESSING_MODE_CLONE_PRICELEVEL_INCLUDE_PRICES = 4;
    public static final short PROCESSING_MODE_CLONE_PRICELEVEL_NO_PRICES = 5;
    
    public final static short STATUS_CODE_OK = 0;
    public final static short STATUS_CODE_ERROR = 1;
    
    private static final String DEFAULT_GET_PRICE_LEVEL_SCRIPT = 
        "public static org.opencrx.kernel.product1.jmi1.GetPriceLevelResult getPriceLevel(\n" + 
            "org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" + 
            "org.opencrx.kernel.product1.jmi1.PricingRule pricingRule,\n" + 
            "org.opencrx.kernel.contract1.jmi1.AbstractContract contract,\n" +         
            "org.opencrx.kernel.product1.jmi1.AbstractProduct product,\n" +         
            "org.opencrx.kernel.uom1.jmi1.Uom priceUom,\n" +         
            "java.math.BigDecimal quantity,\n" +         
            "java.util.Date pricingDate\n" +         
        ") {\n" +
            "org.opencrx.kernel.product1.jmi1.Product1Package productPkg =\n" + 
                "(org.opencrx.kernel.product1.jmi1.Product1Package)rootPkg.refPackage(\n" +
                    "org.opencrx.kernel.product1.jmi1.Product1Package.class.getName()\n" +
                ");\n" +
            "org.opencrx.kernel.product1.jmi1.GetPriceLevelResult result = productPkg.createGetPriceLevelResult(\n" +
                "null,\n" +
                "null,\n" +
                "null,\n" +
                "(short)0,\n" +
                "null\n" +                
            ");\n" +
            "return result;\n" +        
        "}";
    
    protected final Backend backend;
    
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Products.java,v 1.74 2009/06/08 09:21:19 wfro Exp $
 * Description: Products
 * Revision:    $Revision: 1.74 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/08 09:21:19 $
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
package org.opencrx.kernel.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.opencrx.kernel.base.jmi1.AttributeFilterProperty;
import org.opencrx.kernel.contract1.jmi1.ContractPosition;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.product1.cci2.AbstractPriceLevelQuery;
import org.opencrx.kernel.product1.cci2.PriceListEntryQuery;
import org.opencrx.kernel.product1.cci2.ProductBasePriceQuery;
import org.opencrx.kernel.product1.cci2.ProductQuery;
import org.opencrx.kernel.product1.jmi1.AbstractFilterProduct;
import org.opencrx.kernel.product1.jmi1.AbstractPriceLevel;
import org.opencrx.kernel.product1.jmi1.CategoryFilterProperty;
import org.opencrx.kernel.product1.jmi1.ConfiguredProduct;
import org.opencrx.kernel.product1.jmi1.DefaultSalesTaxTypeFilterProperty;
import org.opencrx.kernel.product1.jmi1.DisabledFilterProperty;
import org.opencrx.kernel.product1.jmi1.DiscountPriceModifier;
import org.opencrx.kernel.product1.jmi1.LinearPriceModifier;
import org.opencrx.kernel.product1.jmi1.PriceListEntry;
import org.opencrx.kernel.product1.jmi1.PriceModifier;
import org.opencrx.kernel.product1.jmi1.PriceUomFilterProperty;
import org.opencrx.kernel.product1.jmi1.PricingRule;
import org.opencrx.kernel.product1.jmi1.Product;
import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.opencrx.kernel.product1.jmi1.ProductBasePrice;
import org.opencrx.kernel.product1.jmi1.ProductClassificationFilterProperty;
import org.opencrx.kernel.product1.jmi1.ProductConfiguration;
import org.opencrx.kernel.product1.jmi1.ProductConfigurationType;
import org.opencrx.kernel.product1.jmi1.ProductFilterProperty;
import org.opencrx.kernel.product1.jmi1.ProductPhasePriceLevel;
import org.opencrx.kernel.product1.jmi1.ProductQueryFilterProperty;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.marshalling.Marshaller;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.compatibility.datastore1.jmi1.QueryFilter;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

public class Products extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Products());
	}
	
    //-------------------------------------------------------------------------
	public static Products getInstance(
	) throws ServiceException {
		return getInstance(Products.class);
	}

	//-------------------------------------------------------------------------
	protected Products(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public class PriceLevelMarshaller
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
        
        public Object marshal(
        	Object s
        ) throws ServiceException {
            AbstractPriceLevel priceLevel = PersistenceHelper.clone((AbstractPriceLevel)s);
            if(
                (priceLevel.getName() != null) && 
                (this.nameReplacementRegex != null) && 
                (this.nameReplacementValue != null)
            ) {
                String name = priceLevel.getName();
                if(name != null) {
                    priceLevel.setName(
                        name.replaceAll(
                            this.nameReplacementRegex, 
                            this.nameReplacementValue
                        )
                    );
                }
            }
            if(this.validFrom != null) {
                priceLevel.setValidFrom(
                    this.validFrom
                );
            }
            priceLevel.setValidTo(null);
            if(this.validTo != null) {
                priceLevel.setValidTo(
                     this.validTo
                );
            }
            priceLevel.setFinal(
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
    public class ProductPhasePriceLevelMarshaller
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
        
        public Object marshal(
        	Object s
        ) throws ServiceException {
            ProductPhasePriceLevel priceLevel = PersistenceHelper.clone((ProductPhasePriceLevel)s);
            if(
                (priceLevel.getName() != null) && 
                (this.nameReplacementRegex != null) && 
                (this.nameReplacementValue != null)
            ) {
                String name = priceLevel.getName();
                if(name != null) {
                    priceLevel.setName(
                        name.replaceAll(
                            this.nameReplacementRegex, 
                            this.nameReplacementValue
                        )
                    );
                }
            }
            if(this.productPhaseKey != null) {
                priceLevel.setProductPhaseKey(
                    this.productPhaseKey
                );
            }
            priceLevel.setFinal(
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
    public org.opencrx.kernel.product1.jmi1.PricingRule findPricingRule(
        String name,
        org.opencrx.kernel.product1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.product1.jmi1.Product1Package productPkg = org.opencrx.kernel.utils.Utils.getProductPackage(pm);
        org.opencrx.kernel.product1.cci2.PricingRuleQuery pricingRuleQuery = productPkg.createPricingRuleQuery();
        pricingRuleQuery.name().equalTo(name);
        List<org.opencrx.kernel.product1.jmi1.PricingRule> pricingRules = segment.getPricingRule(pricingRuleQuery);
        return pricingRules.isEmpty()
            ? null
            : pricingRules.iterator().next();
    }

    //-----------------------------------------------------------------------
    /**
     * @return Returns the product segment.
     */
    public org.opencrx.kernel.product1.jmi1.Segment getProductSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
            "xri:@openmdx:org.opencrx.kernel.product1/provider/"
            + providerName + "/segment/" + segmentName
        );
    }

    //-----------------------------------------------------------------------
    public PricingRule initPricingRule(
        String pricingRuleName,
        String description,
        String getPriceLevelScript,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        Product1Package productPkg = Utils.getProductPackage(pm);
        org.opencrx.kernel.product1.jmi1.Segment productSegment = this.getProductSegment(
            pm, 
            providerName, 
            segmentName
        );
        PricingRule pricingRule = null;
        if((pricingRule = this.findPricingRule(pricingRuleName, productSegment, pm)) != null) {
            return pricingRule;            
        }                
        pm.currentTransaction().begin();
        pricingRule = productPkg.getPricingRule().createPricingRule();
        pricingRule.setName(pricingRuleName);
        pricingRule.setDescription(description);
        pricingRule.setGetPriceLevelScript(getPriceLevelScript);
        pricingRule.setDefault(true);
        pricingRule.getOwningGroup().addAll(
            productSegment.getOwningGroup()
        );
        productSegment.addPricingRule(
            false,
            UUIDConversion.toUID(uuids.next()),
            pricingRule
        );                        
        pm.currentTransaction().commit();        
        return pricingRule;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Clone ProductConfigurations stored under fromIdentity to toIdentity.
     * 
     * @return number of cloned configurations
     */
    public int cloneProductConfigurationSet(
    	org.opencrx.kernel.product1.jmi1.ProductConfigurationSet from,
        ContractPosition to,
        boolean cloneDefaultOnly,
        boolean updateCurrentConfig
    ) throws ServiceException {
    	Collection<ProductConfiguration> configurations = from.getConfiguration();
    	for(ProductConfiguration configuration: configurations) {
            if(            	
                !cloneDefaultOnly ||                    
                ((configuration.isDefault() != null) && configuration.isDefault().booleanValue())
            ) {
            	Cloneable.getInstance().cloneObject(
            		configuration, 
            		to, 
            		"configuration", 
            		null, 
            		null
            	);
            }
        }
        // configType
    	if(to instanceof ConfiguredProduct) {
	        ((ConfiguredProduct)to).setConfigType(
	            from.getConfigType()
	        );
	        // currentConfig
	        if(updateCurrentConfig) {
	        	configurations = ((ConfiguredProduct)to).getConfiguration();
	            ProductConfiguration defaultConfiguration = null;   
	            for(ProductConfiguration configuration: configurations) {
	                if((configuration.isDefault() != null) && configuration.isDefault().booleanValue()) {
	                    defaultConfiguration = configuration;
	                    break;
	                }
	            }
	            if(defaultConfiguration != null) {
	                ((ConfiguredProduct)to).setCurrentConfig(
	                	defaultConfiguration
	                );
	            }
	        }
        }
        return configurations.size();
    }
    
    //-------------------------------------------------------------------------
    public void setConfigurationType(
        org.opencrx.kernel.product1.jmi1.Product product,
        org.opencrx.kernel.product1.jmi1.ProductConfigurationTypeSet configurationTypeSet
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(product);
    	Collection<ProductConfiguration> productConfigurations = product.getConfiguration();
    	for(ProductConfiguration configuration: productConfigurations) {
    		configuration.refDelete();
    	}
    	Collection<ProductConfigurationType> configurationTypes = configurationTypeSet.getConfigurationType();
    	for(ProductConfigurationType configurationType: configurationTypes) {
    		ProductConfiguration configuration = pm.newInstance(ProductConfiguration.class);
    		configuration.refInitialize(false, false);
    		configuration.setDefault(configurationType.isDefault());
    		configuration.setName(configurationType.getName());
    		configuration.setDescription(configurationType.getDescription());
    		configuration.setValidFrom(configurationType.getValidFrom());
    		configuration.setValidTo(configurationType.getValidTo());
    		configuration.setConfigType(configurationType);
    		product.addConfiguration(
    			false,
    			this.getUidAsString(),
    			configuration
    		);
    		Collection<org.opencrx.kernel.base.jmi1.Property> properties = configurationType.getProperty();
    		for(org.opencrx.kernel.base.jmi1.Property property: properties) {
    			Cloneable.getInstance().cloneObject(
    				property, 
    				configuration, 
    				"property", 
    				null, 
    				""
    			);
    		}    		
    	}
        product.setConfigType(
             configurationTypeSet
        );
    }
    
    //-------------------------------------------------------------------------
    public void unsetConfigurationType(
        org.opencrx.kernel.product1.jmi1.ProductConfigurationSet productConfigurationSet
    ) throws ServiceException {
    	productConfigurationSet.setConfigType(null);
    	if(productConfigurationSet instanceof ConfiguredProduct) {
    		((ConfiguredProduct)productConfigurationSet).setCurrentConfig(null);
    	}
    	Collection<ProductConfiguration> configurations = productConfigurationSet.getConfiguration();
    	for(ProductConfiguration configuration: configurations) {
    		configuration.refDelete();
    	}
    }
    
    //-------------------------------------------------------------------------
    public void unsetConfigurationType(
        org.opencrx.kernel.product1.jmi1.ProductConfiguration productConfiguration
    ) throws ServiceException {
    	productConfiguration.setConfigType(null);
    	Collection<org.opencrx.kernel.base.jmi1.Property> properties = productConfiguration.getProperty();
    	for(org.opencrx.kernel.base.jmi1.Property property: properties) {
    		property.refDelete();
    	}
    }
    
    //-------------------------------------------------------------------------
    public ProductQuery getFilteredProductQuery(
        AbstractFilterProduct productFilter,
        ProductQuery query,
        boolean forCounting
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(productFilter);
        Collection<ProductFilterProperty> filterProperties = productFilter.getProductFilterProperty();
        boolean hasQueryFilterClause = false;
        for(ProductFilterProperty filterProperty: filterProperties) {
            Boolean isActive = filterProperty.isActive();            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if(filterProperty instanceof ProductQueryFilterProperty) {
                	ProductQueryFilterProperty p = (ProductQueryFilterProperty)filterProperty;
                	QueryFilter queryFilter = pm.newInstance(QueryFilter.class);
                	queryFilter.setClause(
                		(forCounting ? Database_1_Attributes.HINT_COUNT : "") + p.getClause()
                	);
                    queryFilter.setStringParam(
                    	p.getStringParam()
                    );
                    queryFilter.setIntegerParam(
                    	p.getIntegerParam()
                    );
                    queryFilter.setDecimalParam(
                    	p.getDecimalParam()
                    );
                    queryFilter.setBooleanParam(
                    	p.getBooleanParam().isEmpty() ? Boolean.FALSE : p.getBooleanParam().iterator().next()
                    );
                    queryFilter.setDateParam(
                    	p.getDateParam()
                    );
                    queryFilter.setDateTimeParam(
                    	p.getDateTimeParam()
                    );
                    query.thereExistsContext().equalTo(
                    	queryFilter
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else if(filterProperty instanceof AttributeFilterProperty) {
                	AttributeFilterProperty attributeFilterProperty = (AttributeFilterProperty)filterProperty;
                    // Get filterOperator, filterQuantor
                    short operator = attributeFilterProperty.getFilterOperator();
                    operator = operator == 0 ? 
                    	FilterOperators.IS_IN : 
                    	operator;
                    short quantor = attributeFilterProperty.getFilterQuantor();
                    quantor = quantor == 0 ? 
                    	Quantors.THERE_EXISTS : 
                    	quantor;   
                    
                    if(filterProperty instanceof PriceUomFilterProperty) {
                    	PriceUomFilterProperty p = (PriceUomFilterProperty)filterProperty;
                    	switch(quantor) {
	                		case Quantors.FOR_ALL:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.forAllPriceUom().elementOf(p.getPriceUom()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.forAllPriceUom().notAnElementOf(p.getPriceUom()); 
	                					break;
	                				default:
	                					query.forAllPriceUom().elementOf(p.getPriceUom()); 
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.thereExistsPriceUom().elementOf(p.getPriceUom()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.thereExistsPriceUom().notAnElementOf(p.getPriceUom()); 
	                					break;
	                				default:
	                					query.thereExistsPriceUom().elementOf(p.getPriceUom()); 
	                					break;
	                			}
	                			break;
                    	}
                	}
                    else if(filterProperty instanceof CategoryFilterProperty) {
                    	CategoryFilterProperty p = (CategoryFilterProperty)filterProperty;
                    	switch(quantor) {
	                		case Quantors.FOR_ALL:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.forAllCategory().elementOf(p.getCategory()); 
	                					break;
	                				case FilterOperators.IS_LIKE:
	                					query.forAllCategory().like(p.getCategory()); 
	                					break;
	                				case FilterOperators.IS_GREATER:
	                					query.forAllCategory().greaterThan(p.getCategory().get(0)); 
	                					break;
	                				case FilterOperators.IS_GREATER_OR_EQUAL:
	                					query.forAllCategory().greaterThanOrEqualTo(p.getCategory().get(0)); 
	                					break;
	                				case FilterOperators.IS_LESS:
	                					query.forAllCategory().lessThan(p.getCategory().get(0)); 
	                					break;
	                				case FilterOperators.IS_LESS_OR_EQUAL:
	                					query.forAllCategory().lessThanOrEqualTo(p.getCategory().get(0)); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.forAllCategory().notAnElementOf(p.getCategory()); 
	                					break;
	                				case FilterOperators.IS_UNLIKE:
	                					query.forAllCategory().unlike(p.getCategory()); 
	                					break;
	                				default:
	                					query.forAllCategory().elementOf(p.getCategory()); 
	                					break;
	                			}
	                			break;
                    		default:
                    			switch(operator) {
                    				case FilterOperators.IS_IN: 
                    					query.thereExistsCategory().elementOf(p.getCategory()); 
                    					break;
                    				case FilterOperators.IS_LIKE: 
                    					query.thereExistsCategory().like(p.getCategory()); 
                    					break;
                    				case FilterOperators.IS_GREATER:
                    					query.thereExistsCategory().greaterThan(p.getCategory().get(0)); 
                    					break;
                    				case FilterOperators.IS_GREATER_OR_EQUAL:
                    					query.thereExistsCategory().greaterThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case FilterOperators.IS_LESS:
                    					query.thereExistsCategory().lessThan(p.getCategory().get(0)); 
                    					break;
                    				case FilterOperators.IS_LESS_OR_EQUAL:
                    					query.thereExistsCategory().lessThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case FilterOperators.IS_NOT_IN:
                    					query.thereExistsCategory().notAnElementOf(p.getCategory()); 
                    					break;
                    				case FilterOperators.IS_UNLIKE: 
                    					query.thereExistsCategory().unlike(p.getCategory()); 
                    					break;
                    				default:
                    					query.thereExistsCategory().elementOf(p.getCategory()); 
                    					break;
                    			}
                    			break;
                    	}
                    }
                    else if(filterProperty instanceof ProductClassificationFilterProperty) {
                    	ProductClassificationFilterProperty p = (ProductClassificationFilterProperty)filterProperty;
                    	switch(quantor) {
	                		case Quantors.FOR_ALL:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.forAllClassification().elementOf(p.getClassification()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.forAllClassification().notAnElementOf(p.getClassification()); 
	                					break;
	                				default:
	                					query.forAllClassification().elementOf(p.getClassification()); 
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.thereExistsClassification().elementOf(p.getClassification()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.thereExistsClassification().notAnElementOf(p.getClassification()); 
	                					break;
	                				default:
	                					query.thereExistsClassification().elementOf(p.getClassification()); 
	                					break;
	                			}
	                			break;
                    	}
                    }
                    else if(filterProperty instanceof DefaultSalesTaxTypeFilterProperty) {
                    	DefaultSalesTaxTypeFilterProperty p = (DefaultSalesTaxTypeFilterProperty)filterProperty;
                    	switch(quantor) {
	                		case Quantors.FOR_ALL:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.forAllSalesTaxType().elementOf(p.getSalesTaxType()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.forAllSalesTaxType().notAnElementOf(p.getSalesTaxType()); 
	                					break;
	                				default:
	                					query.forAllSalesTaxType().elementOf(p.getSalesTaxType()); 
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.thereExistsSalesTaxType().elementOf(p.getSalesTaxType()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN:
	                					query.thereExistsSalesTaxType().notAnElementOf(p.getSalesTaxType()); 
	                					break;
	                				default:
	                					query.thereExistsSalesTaxType().elementOf(p.getSalesTaxType()); 
	                					break;
	                			}
	                			break;
                    	}                    	
                    }
                    else if(filterProperty instanceof DisabledFilterProperty) {
                    	DisabledFilterProperty p = (DisabledFilterProperty)filterProperty;                    	
                    	switch(quantor) {
	                		case Quantors.FOR_ALL:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.forAllDisabled().equalTo(p.isDisabled()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN: 
	                					query.forAllDisabled().equalTo(!p.isDisabled());	                						
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(operator) {
	                				case FilterOperators.IS_IN: 
	                					query.thereExistsDisabled().equalTo(p.isDisabled()); 
	                					break;
	                				case FilterOperators.IS_NOT_IN: 
	                					query.thereExistsDisabled().equalTo(!p.isDisabled()); 
	                					break;
	                			}
	                			break;
                    	}
                	}
                }
            }
        }
        if(!hasQueryFilterClause && forCounting) {
        	QueryFilter queryFilter = pm.newInstance(QueryFilter.class);
        	queryFilter.setClause(
        		Database_1_Attributes.HINT_COUNT + "(1=1)"
        	);
        	query.thereExistsContext().equalTo(
        		queryFilter
        	);
        }
        return query;
    }
    
    //-------------------------------------------------------------------------    
    public List<PriceListEntry> getPriceListEntries(
    	AbstractPriceLevel priceLevel,
        boolean applyPriceFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
    	PriceListEntryQuery priceListEntryQuery = (PriceListEntryQuery)pm.newQuery(PriceListEntry.class);
    	priceListEntryQuery.thereExistsPriceLevel().equalTo(priceLevel);
        if(applyPriceFilter) {
        	priceListEntryQuery.priceCurrency().equalTo(priceLevel.getPriceCurrency());        	
            if(!priceLevel.getPriceUsage().isEmpty()) {
                priceListEntryQuery.thereExistsUsage().elementOf(
                	priceLevel.getPriceUsage()
                );
            }
        }
        org.opencrx.kernel.product1.jmi1.Segment productSegment =
        	(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
        		priceLevel.refGetPath().getPrefix(5)
        	);
        return productSegment.getPriceListEntry(priceListEntryQuery);
    }
    
    //-------------------------------------------------------------------------    
    public List<ProductBasePrice> findPrices(
        Product product,
        AbstractPriceLevel priceLevel,
        boolean useBasedOnPriceLevel
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(product);
    	ProductBasePriceQuery priceQuery = (ProductBasePriceQuery)pm.newQuery(ProductBasePrice.class);
        // Get basedOn price level if useBasedOnPriceLevel==true
        priceLevel = useBasedOnPriceLevel && (priceLevel.getBasedOn() != null) ? 
        	priceLevel.getBasedOn() : 
        	priceLevel;                    
        // filter on priceCurrency if defined
        priceQuery.priceCurrency().equalTo(
        	priceLevel.getPriceCurrency()
        );
        // filter on priceUsage if defined
        if((priceLevel != null) && !priceLevel.getPriceUsage().isEmpty()) {
        	priceQuery.thereExistsUsage().elementOf(
        		priceLevel.getPriceUsage()
        	);
        }
        if(priceLevel != null) {
        	priceQuery.thereExistsPriceLevel().equalTo(
        		priceLevel
        	);
        }
        return product.getBasePrice(priceQuery);
    }

    //-------------------------------------------------------------------------
    public int clonePriceLevel(
        AbstractPriceLevel priceLevel,
        Short processingMode,
        Map<String,Marshaller> priceLevelMarshallers
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
    	org.opencrx.kernel.product1.jmi1.Segment productSegment =
    		(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
    			priceLevel.refGetPath().getParent().getParent()
    		);
        List<AbstractPriceLevel> dependentPriceLevels = this.getDependentPriceLevels(
            priceLevel, 
            true
        );
        if(
            (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_NO_PRICES) ||
            (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_INCLUDE_PRICES)
        ) {
            // Mapping <source price level identity, cloned price level identity>
            Map<Path,AbstractPriceLevel> clonedLevels = new HashMap<Path,AbstractPriceLevel>();
            // Clone price level
            AbstractPriceLevel clonedPriceLevel = (AbstractPriceLevel)Cloneable.getInstance().cloneObject(
            	priceLevel, 
            	productSegment, 
            	"priceLevel", 
            	priceLevelMarshallers, 
            	"priceModifier, accountFilterProperty, assignedAccount, productFilterProperty"
            );
            clonedLevels.put(
                priceLevel.refGetPath(), 
                clonedPriceLevel
            );            
            // Clone dependent price levels
            for(AbstractPriceLevel dependentPriceLevel: dependentPriceLevels) {
            	AbstractPriceLevel clonedDependentPriceLevel = (AbstractPriceLevel)Cloneable.getInstance().cloneObject(
                	dependentPriceLevel, 
                	productSegment, 
                	"priceLevel", 
                	priceLevelMarshallers, 
                	"priceModifier, accountFilterProperty, assignedAccount, productFilterProperty"
                );
                clonedLevels.put(
                    dependentPriceLevel.refGetPath(), 
                    clonedDependentPriceLevel
                );
            }
            // Update priceLevel.basedOn references
            for(AbstractPriceLevel level: clonedLevels.values()) {
                if(
                    (level.getBasedOn() != null) &&
                    clonedLevels.keySet().contains(level.getBasedOn().refGetPath())
                ) {
                	AbstractPriceLevel clonedLevel = clonedLevels.get(level.getBasedOn().refGetPath());
                    level.setBasedOn(clonedLevel);
                }                
            }
            // Clone prices if price level is basic price level
            if(
                (processingMode == PROCESSING_MODE_CLONE_PRICELEVEL_INCLUDE_PRICES) &&
                (priceLevel.getBasedOn() == null) 
            ) {
                List<PriceListEntry> priceListEntries = this.getPriceListEntries(
                    priceLevel,
                    true
                );
                for(PriceListEntry priceListEntry: priceListEntries) {
                	org.opencrx.kernel.product1.jmi1.ProductBasePrice price = priceListEntry.getBasePrice();
                	// Do not clone disabled prices
                	if((price.isDisabled() == null) || !price.isDisabled().booleanValue()) {
	                	org.opencrx.kernel.product1.jmi1.Product product = 
	                		(org.opencrx.kernel.product1.jmi1.Product)pm.getObjectById(
	                			price.refGetPath().getParent().getParent()
	                		);
	                	// Do not clone prices for disabled products 
	                	if((product.isDisabled() == null) || !product.isDisabled().booleanValue()) {
		                	org.opencrx.kernel.product1.jmi1.ProductBasePrice clonedPrice = PersistenceHelper.clone(price);
		                	clonedPrice.getPriceLevel().clear();
		                	clonedPrice.getPriceLevel().add(
		                		clonedPriceLevel
		                	);
		                	product.addBasePrice(
		                		false,
		                		this.getUidAsString(),
		                		clonedPrice
		                	);
	                	}
                	}
                }
            }
        }
        return dependentPriceLevels.size() + 1;        
    }

    //-------------------------------------------------------------------------
    public int clonePriceLevel(
        org.opencrx.kernel.product1.jmi1.PriceLevel priceLevel,
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
            priceLevel, 
            processingMode, 
            priceLevelMarshallers
        );
    }
    
    //-------------------------------------------------------------------------
    public int cloneProductPhasePriceLevel(
        org.opencrx.kernel.product1.jmi1.ProductPhasePriceLevel priceLevel,
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
            priceLevel, 
            processingMode, 
            priceLevelMarshallers
        );
    }
    
    //-------------------------------------------------------------------------
    public List<AbstractPriceLevel> getDependentPriceLevels(
        AbstractPriceLevel priceLevel,
        boolean recursive
    ) throws ServiceException {
    	Set<AbstractPriceLevel> dependentPriceLevels = new HashSet<AbstractPriceLevel>();
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
        org.opencrx.kernel.product1.jmi1.Segment productSegment =
        	(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
        		priceLevel.refGetPath().getPrefix(5)
        	);
        AbstractPriceLevelQuery priceLevelQuery = (AbstractPriceLevelQuery)pm.newQuery(AbstractPriceLevel.class);
        priceLevelQuery.thereExistsBasedOn().equalTo(priceLevel);        
        List<AbstractPriceLevel> levels = productSegment.getPriceLevel(priceLevelQuery);
        dependentPriceLevels.addAll(levels);
        if(recursive) {
	        for(AbstractPriceLevel level: levels) {
	        	dependentPriceLevels.addAll(
	        		this.getDependentPriceLevels(
	        			level,
	        			true
	        		)
	        	);
	        }
        }
        return new ArrayList<AbstractPriceLevel> (dependentPriceLevels);
    }
    
    //-------------------------------------------------------------------------
    public int calculatePrices(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel,
        Short processingMode,
        Date includeProductsModifiedSince
    ) throws ServiceException {
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
            int numberProcessed = this.calculatePrices(
                priceLevel, 
                false,
                includeProductsModifiedSince
            );                                        	            
        	List<AbstractPriceLevel> dependentPriceLevels = this.getDependentPriceLevels(
                priceLevel,
                false
            );        	
            for(AbstractPriceLevel dependentPriceLevel: dependentPriceLevels) {
                numberProcessed += this.calculatePrices(
                    dependentPriceLevel, 
                    PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT,
                    includeProductsModifiedSince
                );                                        	
            }
            return numberProcessed;
        }
        return 0;
    }
    
    //-------------------------------------------------------------------------
    private void applyPriceModifiers(
        ProductBasePrice modifiedPrice,
        Collection<PriceModifier> priceModifiers
    ) {
        BigDecimal quantityFromPrice = modifiedPrice.getQuantityFrom();
        BigDecimal quantityToPrice = modifiedPrice.getQuantityTo();
        for(PriceModifier priceModifier: priceModifiers) {
            BigDecimal quantityFromModifier = priceModifier.getQuantityFrom();
            quantityFromModifier = quantityFromModifier == null ? quantityFromPrice : quantityFromModifier;
            BigDecimal quantityToModifier = priceModifier.getQuantityTo();
            quantityToModifier = quantityToModifier == null ? quantityToPrice : quantityToModifier;
            // Apply modifier if quantity range is larger than the price quantity range
            if(
                ((quantityFromModifier == null) || (quantityFromPrice == null) || (quantityFromModifier.compareTo(quantityFromPrice) <= 0)) &&
                ((quantityToModifier == null) || (quantityToPrice == null) || (quantityToModifier.compareTo(quantityToPrice) >= 0))
            ) {
                if(priceModifier instanceof DiscountPriceModifier) {
                	DiscountPriceModifier discountPriceModifier = (DiscountPriceModifier)priceModifier;
                    boolean discountIsPercentageModifier = 
                    	(discountPriceModifier.isDiscountIsPercentage() != null) && 
                    	discountPriceModifier.isDiscountIsPercentage().booleanValue();
                    boolean discountIsPercentagePrice =
                        (modifiedPrice.isDiscountIsPercentage() != null) && 
                        modifiedPrice.isDiscountIsPercentage().booleanValue();
                    if(discountIsPercentageModifier == discountIsPercentagePrice) {
                        modifiedPrice.setDiscount(
                            discountPriceModifier.getDiscount()
                        );
                    }
                }
                else if(priceModifier instanceof LinearPriceModifier) {
                	LinearPriceModifier linearPriceModifier = (LinearPriceModifier)priceModifier;
                    BigDecimal priceMultiplier = linearPriceModifier.getPriceMultiplier();
                    priceMultiplier = priceMultiplier == null ? BigDecimal.ZERO : priceMultiplier;
                    BigDecimal priceOffset = linearPriceModifier.getPriceOffset();
                    priceOffset = priceOffset == null ? BigDecimal.ZERO : priceOffset;
                    BigDecimal roundingFactor = linearPriceModifier.getRoundingFactor();
                    roundingFactor = roundingFactor == null ? BigDecimal.ONE : roundingFactor;
                    BigDecimal price = modifiedPrice.getPrice();
                    price = price == null ? BigDecimal.ZERO : price;
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
                    modifiedPrice.setPrice(price);
                }
            }
        }        
    }
    
    //-------------------------------------------------------------------------
    public int calculatePrices(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel,
        boolean testOnly,
        Date includeProductsModifiedSince
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
        if(priceLevel.isFinal()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                "Operation is not allowed for final price level.",
                new BasicException.Parameter("param0", priceLevel)
            );                                                                
        }
        // No calculation required for base price levels
        boolean isBasePriceLevel = priceLevel.getBasedOn() == null;
        Collection<PriceModifier> priceModifiers = priceLevel.getPriceModifier();
        // Get all products matching the product filter
        ProductQuery productQuery = (ProductQuery)pm.newQuery(Product.class);
        if(includeProductsModifiedSince != null) {
        	productQuery.modifiedAt().greaterThanOrEqualTo(includeProductsModifiedSince);
        }
        productQuery = this.getFilteredProductQuery(
        	priceLevel,
        	productQuery,
        	false	
        );
        org.opencrx.kernel.product1.jmi1.Segment productSegment =
        	(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
        		priceLevel.refGetPath().getParent().getParent()
        	);        		
        List<Product> filteredProducts = productSegment.getProduct(productQuery);
        int numberProcessed = 0;
        // Iterate all matching products and calculate prices
        for(Product product: filteredProducts) {
            List<ProductBasePrice> basedOnPrices = this.findPrices(
                product,
                priceLevel,
                !isBasePriceLevel // useBasedOnPriceLevel
            );
            List<ProductBasePrice> existingPrices = this.findPrices(
                product,
                priceLevel, 
                false
            );
            // Collect new prices and add after iteration to avoid ConcurrentModificationException on iterator
            List<ProductBasePrice> newPrices = new ArrayList<ProductBasePrice>();
            for(ProductBasePrice basePrice: basedOnPrices) {
                if(!testOnly) {
                    // If no price modifiers are defined do not create new price objects. 
                    // Only add the price level to the existing price. A copy can be enforced
                    // by adding a 1:1 price modifier, e.g. a LinearPriceModifier with priceMultiplier=1.0 and
                    // priceOffset=0.0.
                    if(priceModifiers.isEmpty()) {
                        List<AbstractPriceLevel> priceLevels = basePrice.getPriceLevel();
                        if(!priceLevels.contains(priceLevel)) {
                            priceLevels.add(priceLevel);
                        }
                    }
                    // Create new price and apply price modifiers
                    else {
                        ProductBasePrice newPrice = PersistenceHelper.clone(basePrice);
                        this.applyPriceModifiers(
                            newPrice, 
                            priceModifiers
                        );
                        newPrice.getPriceLevel().clear();
                        newPrice.getPriceLevel().add(priceLevel);
                        newPrice.setPriceCurrency(priceLevel.getPriceCurrency());
                        // Create new price if no price for price level exists
                        if(existingPrices.isEmpty()) {
                        	newPrices.add(
                        		newPrice
                        	);
                        }
                        // Update existing prices
                        else {
                            for(ProductBasePrice existingPrice: existingPrices) {
                                // Compare price, discount, discountIsPercentage, quantityFrom, quantityTo
                                boolean priceIsEqual = true;
                                priceIsEqual &= Utils.areEqual(existingPrice.getPrice(), newPrice.getPrice());
                                priceIsEqual &= Utils.areEqual(existingPrice.getDiscount(), newPrice.getDiscount());
                                priceIsEqual &= Utils.areEqual(existingPrice.getQuantityFrom(), newPrice.getQuantityFrom());
                                priceIsEqual &= Utils.areEqual(existingPrice.getQuantityTo(), newPrice.getQuantityTo());
                                priceIsEqual &= Utils.areEqual(existingPrice.isDiscountIsPercentage(), newPrice.isDiscountIsPercentage());
                                if(!priceIsEqual) {
                                	// Update price if it is not shared with another price level
                                	if(existingPrice.getPriceLevel().size() == 1) {
	                                	existingPrice.setPrice(newPrice.getPrice());
	                                	existingPrice.setDiscount(newPrice.getDiscount());
	                                	existingPrice.setQuantityFrom(newPrice.getQuantityFrom());
	                                	existingPrice.setQuantityTo(newPrice.getQuantityTo());
	                                	existingPrice.setDiscountIsPercentage(newPrice.isDiscountIsPercentage());
                                	}
                                	// Remove price level from existing price and create new price
                                	else {
                                		existingPrice.getPriceLevel().remove(priceLevel);
                                		newPrices.add(
                                			newPrice
                                		);
                                	}
                                }
                            }
                        }
                    }
                }
            }
            for(ProductBasePrice newPrice: newPrices) {
            	product.addBasePrice(
            		false,
            		this.getUidAsString(),
            		newPrice
            	);                                		
            }
            numberProcessed++;
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePrices(
        AbstractPriceLevel priceLevel
    ) throws ServiceException {
        if(priceLevel.isFinal()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                "Operation is not allowed for final price level.",
                new BasicException.Parameter("param0", priceLevel)
            );                                                                
        }
        List<PriceListEntry> priceListEntries = this.getPriceListEntries(
            priceLevel,
            false
        );
        int numberProcessed = 0;
        for(PriceListEntry priceListEntry: priceListEntries) {
        	if(priceListEntry.getBasePrice() != null) {
        		ProductBasePrice basePrice = priceListEntry.getBasePrice();
        		basePrice.getPriceLevel().remove(priceLevel);
        		if(basePrice.getPriceLevel().isEmpty()) {
        			basePrice.refDelete();
        		}
        		numberProcessed++;
        	}
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePrices(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel,
        Short processingMode        
    ) throws ServiceException {
        int numberProcessed = 0;
        if(processingMode == PROCESSING_MODE_PROCESS) {
            numberProcessed = this.removePrices(
                priceLevel 
            );
        }
        else if(processingMode == PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT) {
        	this.removePrices(
                priceLevel 
            );
            List<AbstractPriceLevel> dependentPriceLevels = this.getDependentPriceLevels(
                priceLevel, 
                true
            );
            for(AbstractPriceLevel dependentPriceLevel: dependentPriceLevels) {
            	this.removePrices(
                    dependentPriceLevel 
                );
            }
            numberProcessed = 1 + dependentPriceLevels.size();
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int removePriceLevels(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel,
        Short processingMode,
        boolean preDelete
    ) throws ServiceException {
        int numberProcessed = 0;
        if(processingMode == PROCESSING_MODE_PROCESS) {
            // Remove prices of price level (no dependent)
        	this.removePrices(
                priceLevel, 
                processingMode
            );
        	this.removePriceLevel(
                priceLevel,
                preDelete
            );
            numberProcessed = 1;
        }
        else if(processingMode == PROCESSING_MODE_PROCESS_INCLUDE_DEPENDENT) {
            List<AbstractPriceLevel> dependentPriceLevels = this.getDependentPriceLevels(
                priceLevel, 
                false
            );
            for(AbstractPriceLevel dependentPriceLevel: dependentPriceLevels) {
            	this.removePriceLevels(
                    dependentPriceLevel,
                    processingMode,
                    false
                );                
            }
        	this.removePrices(
                priceLevel, 
                PROCESSING_MODE_PROCESS
            );
        	this.removePriceLevel(
                priceLevel,
                preDelete
            );
            numberProcessed = 1 + dependentPriceLevels.size();
        }
        return numberProcessed;
    }
    
    //-------------------------------------------------------------------------
    public int createInitialPrices(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel,
        Short processingMode,
        org.opencrx.kernel.uom1.jmi1.Uom priceUom,
        Date includeProductsCreatedSince
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);    	
        // Price level must not be final
        if(priceLevel.isFinal()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_FINAL_PRICE_LEVEL,
                "Operation is not allowed for final price level.",
                new BasicException.Parameter("param0", priceLevel)
            );                                                                
        }
        // Get price modifiers        
        Collection<PriceModifier> priceModifiers = priceLevel.getPriceModifier();
        // Get all products matching the product filter
        ProductQuery productQuery = (ProductQuery)pm.newQuery(Product.class);
        if(includeProductsCreatedSince != null) {
        	productQuery.modifiedAt().greaterThanOrEqualTo(
        		includeProductsCreatedSince
        	);
        }
        productQuery = this.getFilteredProductQuery(
        	priceLevel,
        	productQuery,
        	false
        );
        org.opencrx.kernel.product1.jmi1.Segment productSegment =
        	(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
        		priceLevel.refGetPath().getPrefix(5)
        	);
        List<Product> products = productSegment.getProduct(productQuery);
        int numberProcessed = 0;
        for(Product product: products) {
            List<ProductBasePrice> basePrices = this.findPrices(
                product,
                priceLevel,
                false
            );
            if(basePrices.isEmpty()) {
                ProductBasePrice basePrice = pm.newInstance(ProductBasePrice.class);
                basePrice.refInitialize(false, false);
                basePrice.getPriceLevel().add(
                    priceLevel
                );
                basePrice.getUsage().addAll(
                    priceLevel.getPriceUsage()
                );
                basePrice.setPrice(BigDecimal.ZERO);                
                basePrice.setPriceCurrency(
                    priceLevel.getPriceCurrency()
                );
                basePrice.setDiscount(BigDecimal.ZERO);
                basePrice.setDiscountIsPercentage(Boolean.FALSE);
                basePrice.setUom(priceUom);
                this.applyPriceModifiers(
                    basePrice, 
                    priceModifiers
                );
                if(processingMode == PROCESSING_MODE_PROCESS) {
                	product.addBasePrice(
                		false,
                		this.getUidAsString(),
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
    	PersistenceManager pm = JDOHelper.getPersistenceManager(pricingRule);
        String script = (pricingRule.getGetPriceLevelScript() == null) || (pricingRule.getGetPriceLevelScript().length() == 0) ? 
        	PRICING_RULE_GET_PRICE_LEVEL_SCRIPT_LOWEST_PRICE : 
        	pricingRule.getGetPriceLevelScript().intern();
        org.opencrx.kernel.product1.jmi1.Product1Package productPkg = Utils.getProductPackage(pm); 
        try {
            Map<String,Class<?>> pricingRules = threadLocalPricingRules.get();
            Class<?> c = pricingRules.get(script);
            if(c == null) {
                pricingRules.put(
                   script, 
                   c = new ClassBodyEvaluator(script).evaluate()
                );
            }
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
                        Utils.getProductPackage(pm).refOutermostPackage(),
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
                PRICING_RULE_GET_PRICE_LEVEL_SCRIPT_LOWEST_PRICE
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
        AbstractPriceLevel priceLevel
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
    	org.opencrx.kernel.product1.jmi1.Segment productSegment =
    		(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
    			priceLevel.refGetPath().getPrefix(5)
    		);
    	AbstractPriceLevelQuery priceLevelQuery = (AbstractPriceLevelQuery)pm.newQuery(AbstractPriceLevel.class);
    	priceLevelQuery.thereExistsBasedOn().equalTo(
    		priceLevel
    	);
    	List<AbstractPriceLevel> levels = productSegment.getPriceLevel(priceLevelQuery);
    	for(AbstractPriceLevel level: levels) {
    		if(!JDOHelper.isDeleted(level)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    //-------------------------------------------------------------------------
    public void removePriceLevel(
        AbstractPriceLevel priceLevel,
        boolean preDelete
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(priceLevel);
    	org.opencrx.kernel.product1.jmi1.Segment productSegment =
    		(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
    			priceLevel.refGetPath().getPrefix(5)
    		);    	
        if(this.hasDependentPriceLevels(priceLevel)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_BASEDON_PRICE_LEVEL,
                "Can not delete price level. Other price levels are based on this price level.",
                new BasicException.Parameter("param0", priceLevel.getName())
            );                                                                            
        }
        // Check for prices which are assigned to price level
        PriceListEntryQuery priceListEntryQuery = (PriceListEntryQuery)pm.newQuery(PriceListEntry.class);
        priceListEntryQuery.thereExistsPriceLevel().equalTo(priceLevel);
        List<PriceListEntry> priceListEntries = productSegment.getPriceListEntry(priceListEntryQuery);
        if(!priceListEntries.isEmpty()) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.PRODUCT_OPERATION_NOT_ALLOWED_FOR_PRICE_LEVEL_HAVING_PRICES,
                "Can not delete price level. Price level has assigned prices.",
                new BasicException.Parameter("param0", priceLevel.getName())
            );                                                                                        
        }
        if(!preDelete) {
        	priceLevel.refDelete();
        }
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredProduct(
        org.opencrx.kernel.product1.jmi1.AbstractFilterProduct productFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(productFilter);
    	org.opencrx.kernel.product1.jmi1.Segment productSegment =
    		(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
    			productFilter.refGetPath().getPrefix(5)
    		);    	    	
    	ProductQuery productQuery = (ProductQuery)pm.newQuery(Product.class);
    	productQuery = this.getFilteredProductQuery(
    		productFilter,
    		productQuery,
    		true
    	);
        List<Product> products = productSegment.getProduct(productQuery);
        return products.size();
    }
        
    //-------------------------------------------------------------------------
    public static org.opencrx.kernel.product1.jmi1.GetPriceLevelResult getLowestPricePriceLevel(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        org.opencrx.kernel.product1.jmi1.PricingRule pricingRule,
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract,
        org.opencrx.kernel.product1.jmi1.AbstractProduct product,
        org.opencrx.kernel.uom1.jmi1.Uom priceUom,
        java.math.BigDecimal quantity,
        java.util.Date pricingDate
    ) {
        boolean logLevelDetail = false;
        boolean loggingActivated = true;
        if (loggingActivated && logLevelDetail) {
          System.out.println("pricing rule LowestPriceRule invoked: get lowest price");
        }
        org.opencrx.kernel.product1.jmi1.Product1Package productPkg =
            (org.opencrx.kernel.product1.jmi1.Product1Package)rootPkg.refPackage(
            org.opencrx.kernel.product1.jmi1.Product1Package.class.getName()
            );
        org.opencrx.kernel.account1.jmi1.Account1Package accountPkg =
            (org.opencrx.kernel.account1.jmi1.Account1Package)rootPkg.refPackage(
            org.opencrx.kernel.account1.jmi1.Account1Package.class.getName()
            );
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel priceLevel = null;    
        short statusCode = (short)0;
        String statusMessage = null;
        java.math.BigDecimal lowestPrice = null;
        java.math.BigDecimal lowestPriceCustomerDiscount = null;
        java.lang.Boolean lowestPriceCustomerDiscountIsPercentage = null;
        org.opencrx.kernel.account1.jmi1.Account lowestPriceCustomer = null;
        java.math.BigDecimal lowestPriceAfterDiscount = null;
        if((contract != null) && (pricingRule != null) && (priceUom != null)) {
            try {
              org.opencrx.kernel.product1.jmi1.Segment productSegment = 
                  (org.opencrx.kernel.product1.jmi1.Segment)rootPkg.refObject(
                      new org.openmdx.base.naming.Path(pricingRule.refMofId()).getParent().getParent().toXri()
                  );
              org.opencrx.kernel.product1.cci2.PriceLevelQuery priceLevelFilter = productPkg.createPriceLevelQuery();
              priceLevelFilter.forAllDisabled().isFalse();           
              List<org.opencrx.kernel.product1.jmi1.AbstractPriceLevel> priceLevels = productSegment.getPriceLevel(priceLevelFilter);
              for(org.opencrx.kernel.product1.jmi1.AbstractPriceLevel candidate: priceLevels) {
                  boolean candidateMatches = false;
                  if(candidate instanceof org.opencrx.kernel.product1.jmi1.PriceLevel) {
                      org.opencrx.kernel.product1.jmi1.PriceLevel l = (org.opencrx.kernel.product1.jmi1.PriceLevel)candidate;
                      boolean validFromMatches = (pricingDate == null) || (l.getValidFrom() == null) || l.getValidFrom().compareTo(pricingDate) <= 0;
                      boolean validToMatches = (pricingDate == null) || (l.getValidTo() == null) || l.getValidTo().compareTo(pricingDate) >= 0;
                      if (loggingActivated && logLevelDetail) {
                        System.out.println("testing candidate price level=" + candidate.getName() + "; validFromMatches=" + validFromMatches + "; validToMatches=" + validToMatches);
                      }
                      candidateMatches = validFromMatches && validToMatches;
                  }
                  else {
                      if (loggingActivated && logLevelDetail) {
                        System.out.println("testing candidate price level=" + candidate.getName() + "; is not instance of PriceLevel. Ignoring");
                      }
                      candidateMatches = false;
                  }
                  if(
                      (contract.getContractCurrency() == candidate.getPriceCurrency()) &&
                      candidateMatches
                  ) {
                      if (loggingActivated && logLevelDetail) {
                        System.out.println("candidate for price level found=" + candidate.getName());
                      }
                      candidateMatches = false;
                      // Test whether candidate price level defines lower price
                      Boolean discountIsPercentage = null;
                      java.math.BigDecimal discount = null;
                      org.opencrx.kernel.account1.jmi1.Account discountCustomer = null;
                      if(product != null) {
                          boolean customerFiltered = false;
                          boolean customerAssigned = false;
                          org.opencrx.kernel.account1.jmi1.Account customer = contract.getCustomer();
                          if (customer != null) {
                              org.opencrx.kernel.account1.cci2.AccountQuery accountFilter = accountPkg.createAccountQuery();
                              accountFilter.identity().equalTo(
                                  new String(customer.refMofId())
                              );
                              customerFiltered = (candidate.getFilteredAccount(accountFilter).size() > 0);
                              if (customer != null) {
                                  // check whether there exists an accountAssignment
                            	  Collection<org.opencrx.kernel.product1.jmi1.AccountAssignment> accountAssignments = candidate.getAssignedAccount();
                                  for(org.opencrx.kernel.product1.jmi1.AccountAssignment accountAssignment: accountAssignments) {
                                      try {
                                        boolean assignmentValidFromMatches = (accountAssignment.getValidFrom() == null) || (accountAssignment.getValidFrom().compareTo(pricingDate) <= 0);
                                        boolean assignmentValidToMatches = (accountAssignment.getValidTo() == null) || (accountAssignment.getValidTo().compareTo(pricingDate) >= 0);
                                        if (
                                          (accountAssignment.getAccount() == customer) &&
                                          assignmentValidFromMatches &&
                                          assignmentValidToMatches
                                        ) {
                                          customerAssigned = true;
                                          discountIsPercentage = accountAssignment.isDiscountIsPercentage();
                                          discount = accountAssignment.getDiscount();
                                          discountCustomer = customer;
                                          break;
                                        }
                                      } 
                                      catch (Exception e) {}
                                  }
                              }
                          }
                          if ((customer == null) || customerFiltered || customerAssigned) {
                              // if customer is set it must either be assigned or filtered
                              if (loggingActivated && logLevelDetail) {
                                System.out.println("customer is " + (customer == null ? "undefined" : "") + (customerFiltered ? "filtered " : "") + (customerAssigned ? "assigned" : ""));
                              }
                              // test whether candidate price level defines lower price
                              Collection<org.opencrx.kernel.product1.jmi1.ProductBasePrice> basePrices = product.getBasePrice();
                              for(org.opencrx.kernel.product1.jmi1.ProductBasePrice basePrice: basePrices) {
                                  boolean quantityFromMatches = (quantity == null) || (basePrice.getQuantityFrom() == null) || basePrice.getQuantityFrom().compareTo(quantity) <= 0;
                                  boolean quantityToMatches = (quantity == null) || (basePrice.getQuantityTo() == null) || basePrice.getQuantityTo().compareTo(quantity) >= 0;
                                  boolean priceUomMatches = (basePrice.getUom() == null)  || priceUom.equals(basePrice.getUom());
                                  if (loggingActivated && logLevelDetail) {
                                      System.out.println("quantityFromMatches=" + quantityFromMatches + "; quantityToMatches=" + quantityToMatches + "; priceUomMatches=" + priceUomMatches + "; basePrice.getPriceLevel().contains()=" + basePrice.getPriceLevel().contains(candidate));
                                  }
                                  if (basePrice.getPrice() == null) {
                                    break;
                                  }
                                  java.math.BigDecimal priceAfterDiscount = basePrice.getPrice();
                                  if (discount != null) {
                                    if ((discountIsPercentage != null) && (discountIsPercentage.booleanValue())) {
                                      priceAfterDiscount = (priceAfterDiscount.multiply((new java.math.BigDecimal(100.00)).subtract(discount))).divide(new java.math.BigDecimal(100.00), java.math.BigDecimal.ROUND_FLOOR);
                                    }
                                    else {
                                      priceAfterDiscount = priceAfterDiscount.subtract(discount);
                                    }
                                  }
                                  if(
                                      basePrice.getPriceLevel().contains(candidate) &&
                                      ((lowestPriceAfterDiscount == null) || (priceAfterDiscount.compareTo(lowestPriceAfterDiscount) < 0)) &&
                                      quantityFromMatches && quantityToMatches && priceUomMatches
                                  ) {
                                      lowestPrice = basePrice.getPrice();
                                      lowestPriceCustomerDiscount = discount;
                                      lowestPriceCustomerDiscountIsPercentage = discountIsPercentage;
                                      lowestPriceCustomer = discountCustomer;
                                      lowestPriceAfterDiscount = priceAfterDiscount;                                  
                                      candidateMatches = true;
                                      if (loggingActivated ) {
                                        System.out.println("new lowest price is " + lowestPrice);
                                      }
                                      break;
                                  }
                              }
                          }
                          else {
                              if (loggingActivated && logLevelDetail) {
                                System.out.println("customer is neither filtered nor assigned");
                              }
                          }
                      }
                      else {
                          if (loggingActivated && logLevelDetail) {
                            System.out.println("product is null");
                          }
                          break;
                      }
                      if(candidateMatches) {
                          priceLevel = candidate;
                      }
                  }
              }
          }
          catch(Exception e) {
              statusCode = 1;
              org.openmdx.base.exception.ServiceException e0 = new org.openmdx.base.exception.ServiceException(e);
              statusMessage = e0.getMessage();
              e0.log();
          }
        }
        org.opencrx.kernel.product1.jmi1.GetPriceLevelResult result = productPkg.createGetPriceLevelResult(
            lowestPriceCustomer,
            lowestPriceCustomerDiscount,
            lowestPriceCustomerDiscountIsPercentage,
            priceLevel,
            statusCode,
            statusMessage
        );
        return result;
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
    
    public static final short STATUS_CODE_OK = 0;
    public static final short STATUS_CODE_ERROR = 1;
    
    public static final String PRICING_RULE_NAME_LOWEST_PRICE = "Lowest Price";
    public static final String PRICING_RULE_DESCRIPTION_LOWEST_PRICE = "Get price level which returns the lowest price of the given product, contract currency, pricing date and quantity. If the product is not defined return the price level which matches the contract currency, price uom and pricing date.";
    public static final String PRICING_RULE_GET_PRICE_LEVEL_SCRIPT_LOWEST_PRICE = 
    "//<pre>\n" + 
    "    public static org.opencrx.kernel.product1.jmi1.GetPriceLevelResult getPriceLevel(\n" + 
    "    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" + 
    "    org.opencrx.kernel.product1.jmi1.PricingRule pricingRule,\n" + 
    "    org.opencrx.kernel.contract1.jmi1.AbstractContract contract,\n" +         
    "    org.opencrx.kernel.product1.jmi1.AbstractProduct product,\n" +         
    "    org.opencrx.kernel.uom1.jmi1.Uom priceUom,\n" +         
    "    java.math.BigDecimal quantity,\n" +         
    "    java.util.Date pricingDate\n" +         
    ") {\n" +
    "    return org.opencrx.kernel.backend.Products.getLowestPricePriceLevel(\n" + 
    "        rootPkg,\n" +
    "        pricingRule,\n" +
    "        contract,\n" +
    "        product,\n" +
    "        priceUom,\n" +
    "        quantity,\n" +
    "        pricingDate\n" +
    "    );\n" +
    "}//</pre>";        
    
    protected static final ThreadLocal<Map<String,Class<?>>> threadLocalPricingRules = new ThreadLocal<Map<String,Class<?>>>() {
        protected synchronized Map<String,Class<?>> initialValue() {
            return new java.util.IdentityHashMap<String,Class<?>>();
        }         
    };
    
}

//--- End of File -----------------------------------------------------------

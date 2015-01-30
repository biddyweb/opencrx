/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: Contracts
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
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

package org.opencrx.kernel.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.contract1.cci2.AbstractContractQuery;
import org.opencrx.kernel.contract1.cci2.AbstractInvoicePositionQuery;
import org.opencrx.kernel.contract1.cci2.AbstractOpportunityPositionQuery;
import org.opencrx.kernel.contract1.cci2.AbstractQuotePositionQuery;
import org.opencrx.kernel.contract1.cci2.AbstractSalesOrderPositionQuery;
import org.opencrx.kernel.contract1.cci2.CalculationRuleQuery;
import org.opencrx.kernel.contract1.cci2.ContractCreatorQuery;
import org.opencrx.kernel.contract1.cci2.ContractTypeQuery;
import org.opencrx.kernel.contract1.cci2.PositionModificationQuery;
import org.opencrx.kernel.contract1.jmi1.AbstractContract;
import org.opencrx.kernel.contract1.jmi1.AbstractFilterContract;
import org.opencrx.kernel.contract1.jmi1.AbstractInvoicePosition;
import org.opencrx.kernel.contract1.jmi1.AbstractOpportunityPosition;
import org.opencrx.kernel.contract1.jmi1.AbstractQuotePosition;
import org.opencrx.kernel.contract1.jmi1.AbstractRemovedPosition;
import org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition;
import org.opencrx.kernel.contract1.jmi1.AccountAssignmentContract;
import org.opencrx.kernel.contract1.jmi1.CalculationRule;
import org.opencrx.kernel.contract1.jmi1.Contract1Package;
import org.opencrx.kernel.contract1.jmi1.ContractCreator;
import org.opencrx.kernel.contract1.jmi1.ContractGroup;
import org.opencrx.kernel.contract1.jmi1.ContractGroupAssignment;
import org.opencrx.kernel.contract1.jmi1.ContractType;
import org.opencrx.kernel.contract1.jmi1.DeliveryInformation;
import org.opencrx.kernel.contract1.jmi1.GenericContract;
import org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult;
import org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.InvoicePosition;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.OpportunityPosition;
import org.opencrx.kernel.contract1.jmi1.PhoneNumber;
import org.opencrx.kernel.contract1.jmi1.PositionCreation;
import org.opencrx.kernel.contract1.jmi1.PositionModification;
import org.opencrx.kernel.contract1.jmi1.PositionRemoval;
import org.opencrx.kernel.contract1.jmi1.QuantityModification;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.QuotePosition;
import org.opencrx.kernel.contract1.jmi1.RemovedPosition;
import org.opencrx.kernel.contract1.jmi1.SalesContract;
import org.opencrx.kernel.contract1.jmi1.SalesContractCreator;
import org.opencrx.kernel.contract1.jmi1.SalesContractPosition;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.contract1.jmi1.SalesOrderPosition;
import org.opencrx.kernel.contract1.jmi1.SalesVolumeContract;
import org.opencrx.kernel.depot1.jmi1.BookingOrigin;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReference;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.SecurityKeys.Action;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.generic.jmi1.Description;
import org.opencrx.kernel.generic.jmi1.DescriptionContainer;
import org.opencrx.kernel.generic.jmi1.PropertySet;
import org.opencrx.kernel.product1.cci2.PricingRuleQuery;
import org.opencrx.kernel.product1.cci2.ProductBasePriceQuery;
import org.opencrx.kernel.product1.jmi1.AbstractPriceLevel;
import org.opencrx.kernel.product1.jmi1.ConfiguredProduct;
import org.opencrx.kernel.product1.jmi1.PricingRule;
import org.opencrx.kernel.product1.jmi1.Product;
import org.opencrx.kernel.product1.jmi1.ProductBasePrice;
import org.opencrx.kernel.uom1.jmi1.Uom;
import org.opencrx.kernel.utils.ScriptUtils;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.dataprovider.layer.persistence.jdbc.spi.Database_1_Attributes;
import org.openmdx.base.exception.RuntimeServiceException;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.Authority;
import org.openmdx.base.marshalling.Marshaller;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.rest.cci.QueryExtensionRecord;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * Default Contracts backend.
 *
 */
public class Contracts extends AbstractImpl {

	/**
	 * Register Contracts backend.
	 */
	public static void register(
	) {
		registerImpl(new Contracts());
	}
	
	/**
	 * Get registered Contracts backend.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static Contracts getInstance(
	) throws ServiceException {
		return getInstance(Contracts.class);
	}

	/**
	 * Constructor
	 */
	protected Contracts(
	) {
		
	}
	
    /**
     * Find calculation rule with given name.
     * 
     * @param name
     * @param segment
     * @param pm
     * @return
     */
    public CalculationRule findCalculationRule(
        String name,
        org.opencrx.kernel.contract1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
        CalculationRuleQuery calculationRuleQuery = (CalculationRuleQuery)pm.newQuery(CalculationRule.class);
        calculationRuleQuery.name().equalTo(name);
        List<CalculationRule> calculationRules = segment.getCalculationRule(calculationRuleQuery);
        return calculationRules.isEmpty() ? 
            null : 
            calculationRules.iterator().next();
    }
    
    /**
     * Find contract creator with given name.
     * 
     * @param name
     * @param segment
     * @param pm
     * @return
     */
    public ContractCreator findContractCreator(
        String name,
        org.opencrx.kernel.contract1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
        ContractCreatorQuery contractCreatorQuery = (ContractCreatorQuery)pm.newQuery(ContractCreator.class);
        contractCreatorQuery.name().equalTo(name);
        List<ContractCreator> contractCreators = segment.getContractCreator(contractCreatorQuery);
        return contractCreators.isEmpty() ? null : 
            contractCreators.iterator().next();
    }

    /**
     * Get contracts segment.
     * 
     * @param pm
     * @param providerName
     * @param segmentName
     * @return
     */
    public org.opencrx.kernel.contract1.jmi1.Segment getContractSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + providerName + "/segment/" + segmentName)
        );
    }

    /**
     * Create / update calculation rule.
     * 
     * @param calculationRuleName
     * @param description
     * @param getPositionAmountsScript
     * @param getContractAmountsScript
     * @param pm
     * @param providerName
     * @param segmentName
     * @return
     */
    public CalculationRule initCalculationRule(
        String calculationRuleName,
        String description,
        String getPositionAmountsScript,
        String getContractAmountsScript,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        CalculationRule calculationRule = null;
        if((calculationRule = this.findCalculationRule(calculationRuleName, contractSegment)) != null) {
            return calculationRule;            
        }                
        pm.currentTransaction().begin();
        calculationRule = pm.newInstance(CalculationRule.class);
        calculationRule.setName(calculationRuleName);
        calculationRule.setDescription(description);
        calculationRule.setGetPositionAmountsScript(getPositionAmountsScript);
        calculationRule.setGetContractAmountsScript(getContractAmountsScript);
        calculationRule.setDefault(true);
        calculationRule.getOwningGroup().addAll(
            contractSegment.getOwningGroup()
        );
        contractSegment.addCalculationRule(
            UUIDConversion.toUID(UUIDs.newUUID()),
            calculationRule
        );                        
        pm.currentTransaction().commit();        
        return calculationRule;
    }
        
    /**
     * Copy contract specific attributes from source to target.
     * 
     * @param source
     * @param target
     */
    protected void copyCrxObject(
    	CrxObject source,
    	CrxObject target
    ) {
        // Index 0
        target.setUserBoolean0(source.isUserBoolean0());
        target.setUserNumber0(source.getUserNumber0());
        target.setUserString0(source.getUserString0());
        target.setUserDateTime0(source.getUserDateTime0());
        target.setUserDate0(source.getUserDate0());
        target.setUserCode0(source.getUserCode0());            
        // Index 1
        target.setUserBoolean1(source.isUserBoolean1());
        target.setUserNumber1(source.getUserNumber1());
        target.setUserString1(source.getUserString1());
        target.setUserDateTime1(source.getUserDateTime1());
        target.setUserDate1(source.getUserDate1());
        target.setUserCode1(source.getUserCode1());            
        // Index 2
        target.setUserBoolean2(source.isUserBoolean2());
        target.setUserNumber2(source.getUserNumber2());
        target.setUserString2(source.getUserString2());
        target.setUserDateTime2(source.getUserDateTime2());
        target.setUserDate2(source.getUserDate2());
        target.setUserCode2(source.getUserCode2());            
        // Index 3
        target.setUserBoolean3(source.isUserBoolean3());
        target.setUserNumber3(source.getUserNumber3());
        target.setUserString3(source.getUserString3());
        target.setUserDateTime3(source.getUserDateTime3());
        target.setUserDate3(source.getUserDate3());
        target.setUserCode3(source.getUserCode3());            
        // Index 4
        target.setUserBoolean4(source.getUserBoolean4());
        target.setUserNumber4(source.getUserNumber4());
        target.setUserString4(source.getUserString4());
        target.setUserDateTime4(source.getUserDateTime4());
        target.setUserDate4(source.getUserDate4());
        target.setUserCode4(source.getUserCode4());                	
    }
    
    /**
     * Copy sales contract specific attributes from source to target.
     * 
     * @param source
     * @param target
     * @throws ServiceException
     */
    protected void copySalesContract(
        SalesContract source,
        SalesContract target
    ) throws ServiceException {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPriority(source.getPriority());
        target.setActiveOn(source.getActiveOn());
        target.setExpiresOn(source.getExpiresOn());
        target.setCancelOn(source.getCancelOn());
        target.setClosedOn(source.getClosedOn());
        target.setContractNumber(source.getContractNumber());
        target.setContractCurrency(source.getContractCurrency());
        target.setPaymentTerms(source.getPaymentTerms());
        target.setContractLanguage(source.getContractLanguage());
        target.setContractState(source.getContractState());
        target.setPricingDate(source.getPricingDate());
        target.getCompetitor().clear();
        target.getCompetitor().addAll(source.getCompetitor());
        target.getContact().clear();
        target.getContact().addAll(source.getContact());
        target.setBroker(source.getBroker());
        target.setCustomer(source.getCustomer());
        target.setSalesRep(source.getSalesRep());
        target.setSupplier(source.getSupplier());
        target.getActivity().clear();
        target.getActivity().addAll(source.getActivity());
        target.setOrigin(source.getOrigin());
        target.getInventoryCb().clear();
        target.getInventoryCb().addAll(source.getInventoryCb());
        target.setPricingRule(source.getPricingRule());
        target.setCalcRule(source.getCalcRule());
        target.setShippingMethod(source.getShippingMethod());
        target.setShippingTrackingNumber(source.getShippingTrackingNumber());
        target.setShippingInstructions(source.getShippingInstructions());
        target.setGift(source.isGift());
        target.setGiftMessage(source.getGiftMessage());
        target.getOwningGroup().clear();
        target.getOwningGroup().addAll(source.getOwningGroup());
        target.setOwningUser(source.getOwningUser());
        target.setDisabled(source.isDisabled());
        target.setDisabledReason(source.getDisabledReason());
        target.getExternalLink().clear();
        target.getExternalLink().addAll(source.getExternalLink());
        target.getCategory().clear();
        target.getCategory().addAll(source.getCategory());
    	copyCrxObject(
    		source,
    		target
    	);
    }

    /**
     * Copy sales contract position specific attributes from source to target.
     * 
     * @param source
     * @param target
     * @throws ServiceException
     */
    protected void copySalesContractPosition(
        SalesContractPosition source,
        SalesContractPosition target
    ) throws ServiceException {
    	target.setLineItemNumber(source.getLineItemNumber());
    	target.setName(source.getName());
    	target.setDescription(source.getDescription());
    	target.setPositionNumber(source.getPositionNumber());
    	target.setContractPositionState(source.getContractPositionState());
    	target.setQuantity(source.getQuantity());
    	target.setMinQuantity(source.getMinQuantity());
    	target.setMaxQuantity(source.getMaxQuantity());
    	target.setOffsetQuantity(source.getOffsetQuantity());    	
    	try { target.setMinMaxQuantityHandling(source.getMinMaxQuantityHandling()); } catch(Exception e) {}
    	target.setPricePerUnit(source.getPricePerUnit());
    	try { target.setPricingState(source.getPricingState()); } catch(Exception e) {}
    	target.setDiscount(source.getDiscount());
    	target.setDiscountDescription(source.getDiscountDescription());
    	target.setDiscountIsPercentage(source.isDiscountIsPercentage());
    	target.setSalesCommission(source.getSalesCommission());
    	target.setSalesCommissionIsPercentage(source.isSalesCommissionIsPercentage());
    	target.getContact().clear();
    	target.getContact().addAll(source.getContact());
    	target.setPriceUom(source.getPriceUom());
    	target.setUom(source.getUom());
    	target.setSalesTaxType(source.getSalesTaxType());    	
    	try { target.setListPrice(source.getListPrice()); } catch(Exception e) {}
    	target.setPricingRule(source.getPricingRule());
    	target.setPriceLevel(source.getPriceLevel());
    	target.setCalcRule(source.getCalcRule());
    	try { target.setShippingMethod(source.getShippingMethod()); } catch(Exception e) {}
    	target.setShippingTrackingNumber(source.getShippingTrackingNumber());
    	target.setShippingInstructions(source.getShippingInstructions());
    	try { target.setGift(source.isGift()); } catch(Exception e) {}
    	target.setGiftMessage(source.getGiftMessage());
    	target.setCarrier(source.getCarrier());
    	if(source instanceof ConfiguredProduct && target instanceof ConfiguredProduct) {
    		((ConfiguredProduct)target).setProduct(((ConfiguredProduct)source).getProduct());
    		((ConfiguredProduct)target).setProductSerialNumber(((ConfiguredProduct)source).getProductSerialNumber());
    		((ConfiguredProduct)target).setConfigType(((ConfiguredProduct)source).getConfigType());
    	}
    	if(source instanceof CrxObject && target instanceof CrxObject) {
	    	copyCrxObject(
	    		(CrxObject)source,
	    		(CrxObject)target
	    	);
    	}
    }

    /**
     * Get additional description matching the given language.
     * 
     * @param container
     * @param language
     * @return
     * @throws ServiceException
     */
    protected Description getAdditionalDescription(
        DescriptionContainer container,
        short language
    ) throws ServiceException {
        Collection<Description> descriptions = container.getAdditionalDescription();
        for(Description description: descriptions) {
        	if(description.getLanguage() == language) {
        		return description;
        	}
        }
        return null;
    }
    
    /**
     * Get default calculation rule.
     * 
     * @param contractSegment
     * @return
     * @throws ServiceException
     */
    protected CalculationRule getDefaultCalculationRule(
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractSegment);
    	CalculationRuleQuery query = (CalculationRuleQuery)pm.newQuery(CalculationRule.class);
    	query.thereExistsIsDefault().isTrue();
    	List<CalculationRule> calculationRules = contractSegment.getCalculationRule(query);
    	return calculationRules.isEmpty() ?
    		null :
    		calculationRules.iterator().next();
    }
    
    /**
     * Mark contract as closed.
     * 
     * @param contract
     * @param newContractState
     * @throws ServiceException
     */
    public void markAsClosed(
        AbstractContract contract,
        short newContractState
    ) throws ServiceException {
        contract.setClosedOn(
            new Date()
        );        
        contract.setContractState(
            newContractState
        );
    }
        
    //-------------------------------------------------------------------------
    protected BigDecimal getUomScaleFactor(
        SalesContractPosition position
    ) {
        return (position.getUom() != null) && (position.getPriceUom() != null) ? 
        	Utils.getUomScaleFactor(position.getUom(), position.getPriceUom()) : 
        	BigDecimal.ONE;
    }

    //-------------------------------------------------------------------------
    protected BigDecimal getSalesTaxRate(
        SalesContractPosition position
    ) {
        BigDecimal salesTaxRate = BigDecimal.ZERO;
        try {
            if(position.getSalesTaxType() != null) {
                if(position.getSalesTaxType().getRate() != null) {
                  salesTaxRate = position.getSalesTaxType().getRate();
                }
            }
        }
        catch(Exception e) {}
        return salesTaxRate;        
    }    

    //-----------------------------------------------------------------------
    protected javax.jmi.reflect.RefPackage getJmiPackage(
        PersistenceManager pm,
        String authorityXri
    ) {
    	Authority obj = pm.getObjectById(
            Authority.class,
            authorityXri
        );  
    	return obj.refOutermostPackage().refPackage(obj.refGetPath().getBase());
    }

    //-----------------------------------------------------------------------
    protected Contract1Package getContractPackage(
        PersistenceManager pm
    ) {
    	return (Contract1Package)getJmiPackage(
    		pm,
    		Contract1Package.AUTHORITY_XRI
    	);            
    }
    
    //-------------------------------------------------------------------------
    protected BigDecimal getMinMaxAdjustedQuantity(
        SalesContractPosition position
    ) {
        // quantity
        BigDecimal quantity = position.getQuantity() != null ? 
        	position.getQuantity() : 
        		BigDecimal.ZERO;
        BigDecimal minMaxAdjustedQuantity = quantity;
        BigDecimal minQuantity = position.getMinQuantity() != null ? 
        	position.getMinQuantity() : 
        		BigDecimal.ZERO;
        BigDecimal maxQuantity = position.getMaxQuantity() != null ? 
        	position.getMaxQuantity() : 
        		new BigDecimal(Double.MAX_VALUE);
        BigDecimal offsetQuantity = position.getOffsetQuantity() != null ?  
        	position.getOffsetQuantity() : 
        		BigDecimal.ZERO;        		
        short minMaxQuantityHandling = MIN_MAX_QUANTITY_HANDLING_NA;
        try {
            minMaxQuantityHandling = position.getMinMaxQuantityHandling(); 
        } 
        catch(Exception e) {}
        if(minMaxQuantityHandling == MIN_MAX_QUANTITY_HANDLING_LIMIT) {
            // Adjust min/max handling when quantity is negative
            if(quantity.compareTo(BigDecimal.ZERO) < 0) {
                minMaxAdjustedQuantity = minMaxAdjustedQuantity.add(offsetQuantity).max(maxQuantity.negate()).min(minQuantity.negate());              
            }
            else {
                minMaxAdjustedQuantity = minMaxAdjustedQuantity.subtract(offsetQuantity).max(minQuantity).min(maxQuantity);
            }
        }   
        return minMaxAdjustedQuantity;
    }
    
    //-------------------------------------------------------------------------
    public static GetPositionAmountsResult getPositionAmounts(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        CalculationRule calculationRule,
        SalesContractPosition position,
        java.math.BigDecimal minMaxAdjustedQuantity,
        java.math.BigDecimal uomScaleFactor,
        java.math.BigDecimal salesTaxRate
    ) {
    	try {
	    	return Contracts.getInstance().getPositionAmounts(
	    		calculationRule, 
	    		position, 
	    		minMaxAdjustedQuantity, 
	    		uomScaleFactor, 
	    		salesTaxRate
	    	);
    	} catch(ServiceException e) {
    		throw new RuntimeServiceException(e);
    	}
    }
    
    //-------------------------------------------------------------------------
    public GetPositionAmountsResult getPositionAmounts(
        CalculationRule calculationRule,
        SalesContractPosition position,
        BigDecimal minMaxAdjustedQuantity,
        BigDecimal uomScaleFactor,
        BigDecimal salesTaxRate
    ) {
        BigDecimal pricePerUnit = position.getPricePerUnit() == null ?
            BigDecimal.ZERO :
            	position.getPricePerUnit();
        BigDecimal baseAmount = minMaxAdjustedQuantity.multiply(pricePerUnit.multiply(uomScaleFactor));
        // discount
        Boolean discountIsPercentage = position.isDiscountIsPercentage() != null ? 
            position.isDiscountIsPercentage() : 
            Boolean.FALSE;
        BigDecimal discount = position.getDiscount() != null ? 
            position.getDiscount() : 
            	BigDecimal.ZERO;
        // Discount is per piece in case of !discountIsPercentage
        BigDecimal discountAmount = discountIsPercentage.booleanValue() ? 
            baseAmount.multiply(discount.divide(HUNDRED, BigDecimal.ROUND_UP)) : 
            minMaxAdjustedQuantity.multiply(discount.multiply(uomScaleFactor));
        // taxAmount
        BigDecimal taxAmount = baseAmount.subtract(discountAmount).multiply(
            salesTaxRate.divide(HUNDRED, BigDecimal.ROUND_UP)
        );    
        // amount
        BigDecimal amount = baseAmount.subtract(discountAmount).add(taxAmount);      
        GetPositionAmountsResult result = Structures.create(
        	GetPositionAmountsResult.class, 
        	Datatypes.member(GetPositionAmountsResult.Member.amount, amount),
        	Datatypes.member(GetPositionAmountsResult.Member.baseAmount, baseAmount),
        	Datatypes.member(GetPositionAmountsResult.Member.discountAmount, discountAmount),
        	Datatypes.member(GetPositionAmountsResult.Member.statusCode, (short)0),
        	Datatypes.member(GetPositionAmountsResult.Member.statusMessage, null),
        	Datatypes.member(GetPositionAmountsResult.Member.taxAmount, taxAmount)        	
        );
        return result;
    }

    //-------------------------------------------------------------------------
    public GetPositionAmountsResult getPositionAmounts(
        CalculationRule calculationRule,
        SalesContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(calculationRule);
        String script = (calculationRule.getGetPositionAmountsScript() == null) || (calculationRule.getGetPositionAmountsScript().length() == 0) ? 
            DEFAULT_GET_POSITION_AMOUNTS_SCRIPT : 
            calculationRule.getGetPositionAmountsScript();
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = this.getContractPackage(pm); 
        try {
        	Class<?> clazz = ScriptUtils.getClass(script);
            Method getPositionAmountMethod = clazz.getMethod(
                "getPositionAmounts", 
                new Class[] {
                    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                    CalculationRule.class, 
                    SalesContractPosition.class,
                    BigDecimal.class, // minMaxAdjustedQuantity
                    BigDecimal.class, // uomScaleFactor
                    BigDecimal.class // salesTaxRate
                }
            );
            org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult result = 
                (org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult)getPositionAmountMethod.invoke(
                    null, 
                    new Object[] {
                        contractPkg.refOutermostPackage(),
                        calculationRule,
                        position,
                        this.getMinMaxAdjustedQuantity(position),
                        this.getUomScaleFactor(position),
                        this.getSalesTaxRate(position)
                    }
                );
            return result;
        }
        catch(NoSuchMethodException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "getPositionAmounts does not define a method with the following signature:\n" +
                DEFAULT_GET_POSITION_AMOUNTS_SCRIPT,
                null
            );
        }
        catch(InvocationTargetException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Can not invoke getPositionAmounts:\n" + e.getTargetException().getMessage(),
                null
            );
        }
        catch(IllegalAccessException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Illegal access when invoking getPositionAmounts:\n" + e.getMessage(),
                null
            );
        }
        catch(Exception e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Error parsing getPositionAmountsScript:\n" + e.getMessage(),
                null
            );
        }
    }

    //-------------------------------------------------------------------------
    public GetContractAmountsResult getContractAmounts(
        CalculationRule calculationRule,
        SalesContract contract,
        List<?> lineItemNumbers,
        List<?> positionBaseAmounts,
        List<?> positionDiscountAmounts,
        List<?> positionTaxAmounts,
        List<?> positionAmounts,
        List<?> salesCommissions,
        List<?> salesCommissionIsPercentages
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(calculationRule);
        String script = (calculationRule.getGetContractAmountsScript() == null) || (calculationRule.getGetContractAmountsScript().length() == 0) ? 
            DEFAULT_GET_CONTRACT_AMOUNTS_SCRIPT : 
            calculationRule.getGetContractAmountsScript();
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = this.getContractPackage(pm); 
        try {
        	Class<?> clazz = ScriptUtils.getClass(script);
            Method getContractAmountMethod = clazz.getMethod(
                "getContractAmounts", 
                new Class[] {
                    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                    CalculationRule.class, 
                    SalesContract.class,
                    Integer[].class,
                    BigDecimal[].class,
                    BigDecimal[].class,
                    BigDecimal[].class,
                    BigDecimal[].class,
                    BigDecimal[].class,
                    Boolean[].class
                }
            );
            org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult result = 
                (org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult)getContractAmountMethod.invoke(
                    null, 
                    new Object[] {
                        contractPkg.refOutermostPackage(),
                        calculationRule,
                        contract,
                        lineItemNumbers.toArray(new Integer[lineItemNumbers.size()]),
                        positionBaseAmounts.toArray(new BigDecimal[positionBaseAmounts.size()]),
                        positionDiscountAmounts.toArray(new BigDecimal[positionDiscountAmounts.size()]),
                        positionTaxAmounts.toArray(new BigDecimal[positionTaxAmounts.size()]),
                        positionAmounts.toArray(new BigDecimal[positionAmounts.size()]),
                        salesCommissions.toArray(new BigDecimal[salesCommissions.size()]),
                        salesCommissionIsPercentages.toArray(new Boolean[salesCommissionIsPercentages.size()])
                    }
                );
            return result;
        }
        catch(NoSuchMethodException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "getContractAmounts does not define a method with the following signature:\n" +
                DEFAULT_GET_CONTRACT_AMOUNTS_SCRIPT,
                null, null, null, null, null, null
            );
        }
        catch(InvocationTargetException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Can not invoke getContractAmounts:\n" + e.getTargetException().getMessage(),
                null, null, null, null, null, null
            );
        }
        catch(IllegalAccessException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Illegal access when invoking getContractAmounts:\n" + e.getMessage(),
                null, null, null, null, null, null
            );
        }
        catch(Exception e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Error parsing getContractAmountsScript:\n" + e.getMessage(),
                null, null, null, null, null, null
            );
        }
    }

    //-------------------------------------------------------------------------
    public BigDecimal[] calculateAmounts(
    	SalesContractPosition position
    ) throws ServiceException {      
        // baseAmount, discountAmount         
        BigDecimal baseAmount = null;
        BigDecimal discountAmount = null;
        BigDecimal amount = null;
        BigDecimal taxAmount = null;
        BigDecimal pricePerUnit = position.getPricePerUnit() == null ? 
            BigDecimal.ZERO :
            position.getPricePerUnit();
        PersistenceManager pm = JDOHelper.getPersistenceManager(position);   
        String providerName = position.refGetPath().get(2);
        String segmentName = position.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
        SalesContract contract = (SalesContract)pm.getObjectById(
    		position.refGetPath().getParent().getParent()
    	);
        // Get all positions in one roundtrip. Read up to the last element
        BigDecimal minMaxAdjustedQuantity = this.getMinMaxAdjustedQuantity(position);
        // Calculation rule with first prio from position, then contract, then default
        CalculationRule calculationRule = position.getCalcRule() == null ?
        	contract.getCalcRule() :
        	position.getCalcRule();
        if(calculationRule == null) {
        	calculationRule = this.getDefaultCalculationRule(contractSegment);
        }
        if(calculationRule != null) {
            GetPositionAmountsResult result = this.getPositionAmounts(
                calculationRule,
                position
            );
            if(result.getStatusCode() != STATUS_CODE_OK) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.PROCESSING_FAILURE,
                    "Unable to calculate position amounts",
                    new BasicException.Parameter("result", result)
                );
            }          
            baseAmount = result.getBaseAmount();
            discountAmount = result.getDiscountAmount();   
            taxAmount = result.getTaxAmount();
            amount = result.getAmount();   
        }
        // No calculation rule. Fall back to default amount calculation
        else {
            BigDecimal uomScaleFactor = this.getUomScaleFactor(position);
            BigDecimal salesTaxRate = this.getSalesTaxRate(position);              
            baseAmount = minMaxAdjustedQuantity.multiply(pricePerUnit.multiply(uomScaleFactor));
            // discount
            Boolean discountIsPercentage = position.isDiscountIsPercentage() == null ? 
                Boolean.FALSE : 
                position.isDiscountIsPercentage();
            BigDecimal discount = position.getDiscount() == null ? 
                BigDecimal.ZERO : 
               position.getDiscount();
            // Discount is per piece in case of !discountIsPercentage
            discountAmount = discountIsPercentage.booleanValue() ? 
                baseAmount.multiply(discount.divide(HUNDRED, BigDecimal.ROUND_UP)) : 
                minMaxAdjustedQuantity.multiply(discount.multiply(uomScaleFactor));
            // taxAmount
            taxAmount = baseAmount.subtract(discountAmount).multiply(
                salesTaxRate.divide(HUNDRED, BigDecimal.ROUND_UP)
            );    
            // amount
            amount = baseAmount.subtract(discountAmount).add(taxAmount);      
        }        
        return new BigDecimal[]{
        	baseAmount, 
        	discountAmount, 
        	amount, 
        	taxAmount
        };
    }
    
    //-------------------------------------------------------------------------    
    public BigDecimal[] calculateQuantities(
    	SalesContractPosition position
    ) {
        BigDecimal minMaxAdjustedQuantity = this.getMinMaxAdjustedQuantity(position);
        Collection<DeliveryInformation> deliveryInformations = position.getDeliveryInformation();
        BigDecimal quantityShipped = BigDecimal.ZERO;
        for(DeliveryInformation deliveryInformation: deliveryInformations) {
            quantityShipped = quantityShipped.add(
                deliveryInformation.getQuantityShipped()
            );
        }
        return new BigDecimal[]{
        	quantityShipped, 
        	minMaxAdjustedQuantity.subtract(quantityShipped)
        };        
    }
    
    //-------------------------------------------------------------------------    
    public String[] calculateUomDescriptions(
    	SalesContractPosition position
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
       AbstractContract contract = (AbstractContract)pm.getObjectById(
    		position.refGetPath().getParent().getParent()
    	);    	
    	short contractLanguage = contract.getContractLanguage();
    	String description = "N/A";
    	String detailedDescription = "N/A";
        if(position.getUom() != null) {
            try {
                description = position.getUom().getDescription();
                detailedDescription = position.getUom().getDetailedDescription();
                Description additionalDescription = this.getAdditionalDescription(
                    position.getUom(),
                    contractLanguage
                );
                if(additionalDescription != null) {
                    description = additionalDescription.getDescription();
                    detailedDescription = additionalDescription.getDetailedDescription();
                }
            }
            catch(Exception e) {
                new ServiceException(e).log();
                description = "#ERR";
                detailedDescription = "#ERR";         
            }
        }
        return new String[]{
        	description,
        	detailedDescription
        };
    }

    //-------------------------------------------------------------------------    
    public String[] calculatePriceUomDescriptions(
    	SalesContractPosition position
    ) {    	
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
        AbstractContract contract = (AbstractContract)pm.getObjectById(
    		position.refGetPath().getParent().getParent()
    	);    	
    	short contractLanguage = contract.getContractLanguage();
    	String description = "N/A";
    	String detailedDescription = "N/A";
        if(position.getPriceUom() != null) {
            try {
                description = position.getPriceUom().getDescription();
                detailedDescription = position.getPriceUom().getDetailedDescription();
                Description additionalDescription = this.getAdditionalDescription(
                    position.getPriceUom(),
                    contractLanguage
                );
                if(additionalDescription != null) {
                    description = additionalDescription.getDescription();
                    detailedDescription = additionalDescription.getDetailedDescription();
                }
            }
            catch(Exception e) {
                new ServiceException(e).log();
                description = "#ERR";
                detailedDescription = "#ERR";         
            }
        }
        return new String[]{
        	description,
        	detailedDescription
        };
    }

    //-------------------------------------------------------------------------
    public String[] calculateProductDescriptions(
    	SalesContractPosition position
    ) {    	
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
       AbstractContract contract = (AbstractContract)pm.getObjectById(
    		position.refGetPath().getParent().getParent()
    	);    	
    	short contractLanguage = contract.getContractLanguage();
    	String description = "N/A";
    	String detailedDescription = "N/A";
        if(
        	(position instanceof ConfiguredProduct) &&
        	(((ConfiguredProduct)position).getProduct() != null)
        ) {
            try {
            	Product product = ((ConfiguredProduct)position).getProduct();
                description = product.getDescription();
                detailedDescription = product.getDetailedDescription();
                Description additionalDescription = this.getAdditionalDescription(
                	product,
                    contractLanguage
                );
                if(additionalDescription != null) {
                    description = additionalDescription.getDescription();
                    detailedDescription = additionalDescription.getDetailedDescription();
                }
            }
            catch(Exception e) {
                new ServiceException(e).log();
                description = "#ERR";
                detailedDescription = "#ERR";         
            }
        }
        return new String[]{
        	description,
        	detailedDescription
        };
    }
    
    //-------------------------------------------------------------------------
    public String[] calculateSalesTaxTypeDescriptions(
    	SalesContractPosition position
    ) {    	
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
        AbstractContract contract = (AbstractContract)pm.getObjectById(
    		position.refGetPath().getParent().getParent()
    	);    	
    	short contractLanguage = contract.getContractLanguage();
    	String description = "N/A";
    	String detailedDescription = "N/A";
        if(position.getSalesTaxType() != null) {
            try {
                description = position.getSalesTaxType().getDescription();
                detailedDescription = position.getSalesTaxType().getDetailedDescription();
                Description additionalDescription = this.getAdditionalDescription(
                    position.getSalesTaxType(),
                    contractLanguage
                );
                if(additionalDescription != null) {
                    description = additionalDescription.getDescription();
                    detailedDescription = additionalDescription.getDetailedDescription();
                }
            }
            catch(Exception e) {
                new ServiceException(e).log();
                description = "#ERR";
                detailedDescription = "#ERR";         
            }
        }
        return new String[]{
        	description,
        	detailedDescription
        };
    }
    
    //-------------------------------------------------------------------------
    public void markContractAsDirty(
        AbstractContract contract
    ) throws ServiceException {
        contract.setName(contract.getName());
    }
    
    //-------------------------------------------------------------------------
    public List<SalesContractPosition> getSalesContractPositions(
    	SalesContract contract
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
        List<SalesContractPosition> positions = new ArrayList<SalesContractPosition>();
        if(contract instanceof Opportunity) {
        	AbstractOpportunityPositionQuery query = (AbstractOpportunityPositionQuery)pm.newQuery(AbstractOpportunityPosition.class);
        	query.orderByLineItemNumber().ascending();
        	Collection<AbstractOpportunityPosition> p = ((Opportunity)contract).getPosition(query);
        	positions.addAll(p);
        }
        else if(contract instanceof Quote) {
        	AbstractQuotePositionQuery query = (AbstractQuotePositionQuery)pm.newQuery(AbstractQuotePosition.class);
        	query.orderByLineItemNumber().ascending();
        	Collection<AbstractQuotePosition> p = ((Quote)contract).getPosition(query);
        	positions.addAll(p);
        }
        else if(contract instanceof SalesOrder) {
        	AbstractSalesOrderPositionQuery query = (AbstractSalesOrderPositionQuery)pm.newQuery(AbstractSalesOrderPosition.class);
        	query.orderByLineItemNumber().ascending();
        	Collection<AbstractSalesOrderPosition> p = ((SalesOrder)contract).getPosition(query);
        	positions.addAll(p);
        }
        else if(contract instanceof Invoice) {
        	AbstractInvoicePositionQuery query = (AbstractInvoicePositionQuery)pm.newQuery(AbstractInvoicePosition.class);
        	query.orderByLineItemNumber().ascending();
        	Collection<AbstractInvoicePosition> p = ((Invoice)contract).getPosition(query);
        	positions.addAll(p);
        }
        return positions;
    }
    
    //-------------------------------------------------------------------------
    public BigDecimal[] calculateAmounts(
        SalesContract contract
    ) throws ServiceException {        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
    	String providerName = contract.refGetPath().get(2);
    	String segmentName = contract.refGetPath().get(4);    	
    	org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
    		pm, 
    		providerName, 
    		segmentName
    	);
        BigDecimal totalBaseAmount = null;
        BigDecimal totalDiscountAmount = null;
        BigDecimal totalTaxAmount = null;                
        BigDecimal totalAmount = null;
        BigDecimal totalAmountIncludingTax = null;
        BigDecimal totalSalesCommission = null;
        if(contract instanceof Lead) {
        	Lead lead = (Lead)contract;
        	totalBaseAmount = lead.getEstimatedValue();
        	totalBaseAmount = totalBaseAmount == null ? BigDecimal.ZERO : totalBaseAmount;
        	totalDiscountAmount = BigDecimal.ZERO;
        	totalTaxAmount = BigDecimal.ZERO;
        	totalAmount = totalBaseAmount;        	
        	totalAmountIncludingTax = totalBaseAmount;
        	totalSalesCommission = BigDecimal.ZERO;
        }
        else {
        	List<SalesContractPosition> positions = this.getSalesContractPositions(contract);
	        List<Integer> lineItemNumbers = new ArrayList<Integer>();
	        List<BigDecimal> positionBaseAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionDiscountAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionTaxAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> salesCommissions = new ArrayList<BigDecimal>();
	        List<Boolean> salesCommissionIsPercentages = new ArrayList<Boolean>();
	        for(SalesContractPosition position: positions) {
	            BigDecimal[] amounts = this.calculateAmounts(
	                position
	            );
	            lineItemNumbers.add(
	                (int)position.getLineItemNumber()
	            );
	            positionBaseAmounts.add(
	            	amounts[0]
	            );
	            positionDiscountAmounts.add(
	            	amounts[1]
	            );
	            positionAmounts.add(
	            	amounts[2]
	            );
	            positionTaxAmounts.add(
	            	amounts[3]
	            );
	            salesCommissions.add(
	                position.getSalesCommission() == null ? 
	                    BigDecimal.ZERO : 
	                    position.getSalesCommission()
	            );
	            salesCommissionIsPercentages.add(
	                position.isDiscountIsPercentage() == null ?
	                    Boolean.FALSE : 
	                    position.isSalesCommissionIsPercentage()
	            );
	        }
	        // To amount calculation using calculation rule if defined        
	        CalculationRule calculationRule = contract.getCalcRule() == null ?
	        	this.getDefaultCalculationRule(contractSegment) :
	        	contract.getCalcRule();
	        if(calculationRule != null) {
	            GetContractAmountsResult result = this.getContractAmounts(
	                calculationRule,
	                contract,
	                lineItemNumbers,
	                positionBaseAmounts,
	                positionDiscountAmounts,
	                positionTaxAmounts,
	                positionAmounts,
	                salesCommissions,
	                salesCommissionIsPercentages
	            );
	            if(result.getStatusCode() != STATUS_CODE_OK) {
	                throw new ServiceException(
	                    BasicException.Code.DEFAULT_DOMAIN,
	                    BasicException.Code.PROCESSING_FAILURE,
	                    "Unable to calculate contract amounts",
	                    new BasicException.Parameter("result", result)
	                );
	            }
	            totalBaseAmount = result.getTotalBaseAmount();
	            totalDiscountAmount = result.getTotalDiscountAmount();   
	            totalTaxAmount = result.getTotalTaxAmount();
	            totalAmount = result.getTotalAmount();   
	            totalAmountIncludingTax = result.getTotalAmountIncludingTax();
	            totalSalesCommission = result.getTotalSalesCommission();   
	        }
	        // To default amount calculation if no calculation rule is defined
	        else {
	            totalBaseAmount = BigDecimal.ZERO;
	            totalDiscountAmount = BigDecimal.ZERO;
	            totalTaxAmount = BigDecimal.ZERO;
	            totalSalesCommission = BigDecimal.ZERO;
	            for(int i = 0; i < positionBaseAmounts.size(); i++) {
	                totalBaseAmount = totalBaseAmount.add(
	                  positionBaseAmounts.get(i)
	                );
	                BigDecimal discountAmount = positionDiscountAmounts.get(i);
	                totalDiscountAmount = totalDiscountAmount.add(discountAmount);
	                totalTaxAmount = totalTaxAmount.add(
	                  positionTaxAmounts.get(i)
	                );
	                BigDecimal salesCommission = salesCommissions.get(i) != null ? 
	                    (BigDecimal) salesCommissions.get(i) : 
	                    BigDecimal.ZERO;
	                BigDecimal baseAmount = positionBaseAmounts.get(i);
	                totalSalesCommission = totalSalesCommission.add(
	                  (salesCommissionIsPercentages.get(i) != null) &&
	                  ((salesCommissionIsPercentages.get(i)).booleanValue()) ? 
	                	  baseAmount.subtract(discountAmount).multiply(salesCommission.divide(HUNDRED, BigDecimal.ROUND_UP)) : 
	                	  salesCommission
	                );
	            }
	            totalAmount = totalBaseAmount.subtract(totalDiscountAmount);
	            totalAmountIncludingTax = totalAmount.add(totalTaxAmount);
	        }
        }
        return new BigDecimal[]{
        	totalBaseAmount,
        	totalDiscountAmount,
        	totalAmount,
        	totalTaxAmount,
        	totalAmountIncludingTax,
        	totalSalesCommission
        };
    }
    
    /**
     * Update contract callback. Override for custom-specific behaviour.
     * 
     * @param contract
     * @throws ServiceException
     */
    protected void updateContract(
    	AbstractContract contract
    ) throws ServiceException {
    	if(JDOHelper.isNew(contract)) {
    		Base.getInstance().assignToMe(
    			contract, 
    			false, // overwrite
    			false // useRunAsPrincipal
    		);
    	}
    }

    /**
     * Update sales contract callback. Override for custom-specific behaviour.
     * 
     * @param contract
     * @throws ServiceException
     */
    protected void updateSalesContract(
    	SalesContract contract
    ) throws ServiceException {
    	if(JDOHelper.isNew(contract)) {
    		contract.setPricingState(PRICING_STATE_NA);
    	}
		BigDecimal[] amounts = Contracts.getInstance().calculateAmounts(
			contract
		);
		contract.setTotalBaseAmount(amounts[0]);
		contract.setTotalDiscountAmount(amounts[1]);
		contract.setTotalAmount(amounts[2]);
		contract.setTotalTaxAmount(amounts[3]);
		contract.setTotalAmountIncludingTax(amounts[4]);
		contract.setTotalSalesCommission(amounts[5]);    		    	
    }
    
    //-----------------------------------------------------------------------
    public Invoice createInvoice(
        SalesOrder salesOrder
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(salesOrder);
    	String providerName = salesOrder.refGetPath().get(2);
    	String segmentName = salesOrder.refGetPath().get(4);
    	Map<String,Marshaller> objectMarshallers = new HashMap<String,Marshaller>();
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:SalesOrder",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				SalesOrder salesOrder = (SalesOrder)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(salesOrder);
    				Invoice invoice = pm.newInstance(Invoice.class);
    				Contracts.getInstance().copySalesContract(
    					salesOrder,
    					invoice
    				);
    				return invoice;                       
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:SalesOrderPosition",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				SalesOrderPosition salesOrderPosition = (SalesOrderPosition)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(salesOrderPosition);
    				InvoicePosition invoicePosition = pm.newInstance(InvoicePosition.class);
    				Contracts.getInstance().copySalesContractPosition(
    					salesOrderPosition,
    					invoicePosition
    				);
    				return invoicePosition;
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        Invoice invoice = (Invoice)Cloneable.getInstance().cloneObject(
        	salesOrder, 
        	contractSegment, 
        	"invoice", 
        	objectMarshallers, 
        	null, // reference filter
        	null, // owning user
        	null // owning group
        );
        invoice.setOrigin(salesOrder);
        if(invoice.getSalesRep() == null) {
            Base.getInstance().assignToMe(
                invoice,
                true, // overwrite
                false // useRunAsPrincipal
            );
        }
        return invoice;
    }
    
    //-----------------------------------------------------------------------
    public SalesOrder createSalesOrder(
        Quote quote
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(quote);
    	String providerName = quote.refGetPath().get(2);
    	String segmentName = quote.refGetPath().get(4);
    	Map<String,Marshaller> objectMarshallers = new HashMap<String,Marshaller>();
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:Quote",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				Quote quote = (Quote)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(quote);
    				SalesOrder salesOrder = pm.newInstance(SalesOrder.class);
    				Contracts.getInstance().copySalesContract(
    					quote,
    					salesOrder
    				);
    				return salesOrder;                       
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:QuotePosition",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				QuotePosition quotePosition = (QuotePosition)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(quotePosition);
    				SalesOrderPosition salesOrderPosition = pm.newInstance(SalesOrderPosition.class);
    				Contracts.getInstance().copySalesContractPosition(
    					quotePosition,
    					salesOrderPosition
    				);
    				return salesOrderPosition;
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        SalesOrder salesOrder = (SalesOrder)Cloneable.getInstance().cloneObject(
        	quote, 
        	contractSegment, 
        	"salesOrder", 
        	objectMarshallers, 
        	null, // reference filter
        	null, // owning user
        	null // owning group
        );
        salesOrder.setOrigin(quote);
        if(salesOrder.getSalesRep() == null) {
            Base.getInstance().assignToMe(
                salesOrder,
                true, // overwrite
                false // useRunAsPrincipal
            );
        }
        return salesOrder;
    }

    //-----------------------------------------------------------------------
    public Quote createQuote(
        Opportunity opportunity
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(opportunity);
    	String providerName = opportunity.refGetPath().get(2);
    	String segmentName = opportunity.refGetPath().get(4);
    	Map<String,Marshaller> objectMarshallers = new HashMap<String,Marshaller>();
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:Opportunity",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				Opportunity opportunity = (Opportunity)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(opportunity);
    				Quote quote = pm.newInstance(Quote.class);
    				Contracts.getInstance().copySalesContract(
    					opportunity,
    					quote
    				);
    				return quote;                       
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:OpportunityPosition",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				OpportunityPosition opportunityPosition = (OpportunityPosition)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(opportunityPosition);
    				QuotePosition quotePosition = pm.newInstance(QuotePosition.class);
    				Contracts.getInstance().copySalesContractPosition(
    					opportunityPosition,
    					quotePosition
    				);
    				return quotePosition;
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        Quote quote = (Quote)Cloneable.getInstance().cloneObject(
        	opportunity, 
        	contractSegment, 
        	"quote", 
        	objectMarshallers, 
        	null, // reference filter
        	null, // owning user
        	null // owning group
        );
        quote.setOrigin(opportunity);
        if(quote.getSalesRep() == null) {
            Base.getInstance().assignToMe(
            	quote,
                true, // overwrite
                false // useRunAsPrincipal
            );
        }
        return quote;
    }
    
    //-----------------------------------------------------------------------
    public Opportunity createOpportunity(
        Lead lead
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(lead);
    	String providerName = lead.refGetPath().get(2);
    	String segmentName = lead.refGetPath().get(4);
    	Map<String,Marshaller> objectMarshallers = new HashMap<String,Marshaller>();
    	objectMarshallers.put(
    		"org:opencrx:kernel:contract1:Lead",
    		new Marshaller() {
    			public Object marshal(
    				Object s
    			) throws ServiceException {
    				Lead lead = (Lead)s;
    				PersistenceManager pm = JDOHelper.getPersistenceManager(lead);
    				Opportunity opportunity = pm.newInstance(Opportunity.class);
    				Contracts.getInstance().copySalesContract(
    					lead,
    					opportunity
    				);
    				return opportunity;                       
    			}
    			public Object unmarshal(Object s) {
    				throw new UnsupportedOperationException();
    			}
    		}
    	);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        Opportunity opportunity = (Opportunity)Cloneable.getInstance().cloneObject(
        	lead, 
        	contractSegment, 
        	"opportunity", 
        	objectMarshallers, 
        	null, // reference filter
        	null, // owning user
        	null // owning group
        );
        opportunity.setOrigin(lead);
        if(opportunity.getSalesRep() == null) {
            Base.getInstance().assignToMe(
            	opportunity,
                true, // overwrite
                false // useRunAsPrincipal
            );
        }
        return opportunity;
    }

    /**
     * Update list price on given position.
     * 
     * @param position
     * @param contract
     * @param product
     * @param overrideExistingPrice
     * @throws ServiceException
     */
    public void updateListPrice(
    	SalesContractPosition position,
        SalesContract contract,
        Product product,
        boolean overrideExistingPrice
    ) throws ServiceException {
        if(contract == null) return;
        PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
        if(position == null) return;
        if(product == null) return;        
        PricingRule pricingRule = position.getPricingRule();
        if(pricingRule == null) return;       
        BigDecimal quantity = position.getQuantity();
        if(quantity == null) return;
        Date pricingDate = position.getPricingDate() != null 
        	? position.getPricingDate() 
        	: contract.getPricingDate() != null 
        		? contract.getPricingDate() 
        		: contract.getActiveOn() != null 
        			? contract.getActiveOn() 
        			: new Date();
        org.opencrx.kernel.uom1.jmi1.Uom priceUom = position.getPriceUom() != null 
        	? position.getPriceUom() 
        	: position.getUom() != null 
        		? position.getUom() 
        		: null;
        AbstractPriceLevel priceLevel = null;
        BigDecimal customerDiscount = null;
        Boolean customerDiscountIsPercentage = null;        
        if(position.getPriceLevel() != null) {
            priceLevel = position.getPriceLevel();
        } else {
            org.opencrx.kernel.product1.jmi1.GetPriceLevelResult result = 
                Products.getInstance().getPriceLevel(
                    pricingRule,       
                    contract,
                    product,
                    priceUom,
                    quantity, 
                    pricingDate
                );
            if(result.getStatusCode() != STATUS_CODE_OK) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.PROCESSING_FAILURE,
                    "Unable to get price level",
                    new BasicException.Parameter("code", result.getStatusCode()),
                    new BasicException.Parameter("message", result.getStatusMessage())
                );
            }
            priceLevel = result.getPriceLevel() == null 
            	? null
            	: (AbstractPriceLevel)pm.getObjectById(result.getPriceLevel().refGetPath());
            customerDiscount = result.getCustomerDiscount();
            customerDiscountIsPercentage = result.isCustomerDiscountIsPercentage();
        }
        position.setPriceLevel(priceLevel);
        // Find price matching price list and quantity
        List<ProductBasePrice> prices = new ArrayList<ProductBasePrice>();
        ProductBasePrice listPrice = null;
        SysLog.trace("Price level", priceLevel);
        if(priceLevel != null) {
            // Find product prices
            // priceLevel and priceUom must match
        	ProductBasePriceQuery basePriceQuery = (ProductBasePriceQuery)pm.newQuery(ProductBasePrice.class);
        	basePriceQuery.thereExistsPriceLevel().equalTo(priceLevel);
        	basePriceQuery.uom().equalTo(priceUom);
        	SysLog.trace("Lookup of prices with filter", Arrays.asList(basePriceQuery));
            prices = product.getBasePrice(basePriceQuery);
            SysLog.trace("Matching prices found", prices);
            // Check quantity
            for(ProductBasePrice current: prices) {
            	SysLog.trace("Testing price", current);
                boolean quantityFromMatches =
                    (current.getQuantityFrom() == null) || 
                    (quantity == null) || 
                    (current.getQuantityFrom().compareTo(quantity) <= 0);
                SysLog.trace("Quantity from matches", quantityFromMatches);
                boolean quantityToMatches = 
                    (current.getQuantityTo() == null) || 
                    (quantity == null) || 
                    (current.getQuantityTo().compareTo(quantity) >= 0);
                SysLog.trace("Quantity to matches", quantityToMatches);
                if(quantityFromMatches && quantityToMatches) {
                    listPrice = current;
                    break;
                }
            }
        }
        // List price found?
        SysLog.trace("List price found", "" + (listPrice != null));
        position.setListPrice(listPrice);
        position.setPricingState(PRICING_STATE_NA);
        if(listPrice != null) {
            BigDecimal listPriceDiscount = listPrice.getDiscount();
            Boolean listPriceDiscountIsPercentage = listPrice.isDiscountIsPercentage();
            BigDecimal discount = position.getDiscount();
            Boolean discountIsPercentage = position.isDiscountIsPercentage();
            // Recalc discount if not already set on position
            if(discount == null) {
                // Accumulate list price discount and customer discount
                if(customerDiscount == null) {
                    discount = listPriceDiscount;
                    discountIsPercentage = listPriceDiscountIsPercentage;
                } else {
                    BigDecimal price = listPrice.getPrice();
                    price = price == null ? 
                    	BigDecimal.ZERO : 
                    	price;
                    listPriceDiscount = listPriceDiscount == null ? 
                    	BigDecimal.ZERO : 
                    	listPriceDiscount;
                    customerDiscount = customerDiscount == null ? 
                    	BigDecimal.ZERO : 
                    	customerDiscount;                    
                    if(
                        (listPriceDiscount.compareTo(BigDecimal.ZERO) == 0) ||
                        ((listPriceDiscountIsPercentage != null) &&
                        listPriceDiscountIsPercentage.booleanValue())
                    ) {
                        // listPriceDiscountIsPercentage=true, customerDiscountIsPercentage=true
                        // totalDiscount = 1 - (1-listPriceDiscount) * (1-customerDiscount)
                        if(
                            (customerDiscount.compareTo(BigDecimal.ZERO) == 0) ||
                            ((customerDiscountIsPercentage != null) &&
                            customerDiscountIsPercentage.booleanValue())
                        ) {
                            discountIsPercentage = Boolean.TRUE;
                            discount =
                                BigDecimal.ONE.subtract(
                                    BigDecimal.ONE.subtract(listPriceDiscount.divide(HUNDRED, BigDecimal.ROUND_FLOOR)).multiply(
                                        BigDecimal.ONE.subtract(customerDiscount.divide(HUNDRED, BigDecimal.ROUND_FLOOR))
                                    )
                                ).multiply(HUNDRED);
                        } else {
                            // listPriceDiscountIsPercentage=true, customerDiscountIsPercentage=false
                            // totalDiscount = price*listPriceDiscount + customerDiscount
                            discountIsPercentage = Boolean.FALSE;
                            discount = price.multiply(
                                listPriceDiscount.divide(HUNDRED, BigDecimal.ROUND_DOWN)
                            ).add(customerDiscount);
                        }
                    } else {
                        // listPriceDiscountIsPercentage=false, customerDiscountIsPercentage=true
                        // totalDiscount = listPriceDiscount + (price - listPriceDiscount)*customerDiscount
                        if(
                            (customerDiscount.compareTo(BigDecimal.ZERO) == 0) ||
                            (customerDiscountIsPercentage != null) &&
                            customerDiscountIsPercentage.booleanValue()
                        ) {
                            discountIsPercentage = Boolean.FALSE;
                            discount = listPriceDiscount.add(
                                price.subtract(listPriceDiscount).multiply(customerDiscount.divide(HUNDRED, BigDecimal.ROUND_FLOOR))
                            );
                        } else {
                            // listPriceDiscountIsPercentage=false, customerDiscountIsPercentage=false
                            // totalDiscount = listPriceDiscount + customerDiscount
                            discountIsPercentage = Boolean.FALSE;
                            discount = listPriceDiscount.add(customerDiscount);
                        }
                    }
                }
            }
            if(overrideExistingPrice) {
                position.setPricePerUnit(
                    listPrice.getPrice()
                );
                position.setDiscount(discount);
                position.setDiscountIsPercentage(
                	discountIsPercentage
                );
                position.setPriceUom(
                	listPrice.getUom()
                );
            }
            position.setPricingState(PRICING_STATE_OK);            
        }
    }

    /**
     * Update sales contract position.
     * 
     * @param contract
     * @param position
     * @param product
     * @param reprice
     */
    public void updateSalesContractPosition(
        SalesContract contract,
        SalesContractPosition position,
        Product product,
        boolean reprice
    ) {
        try {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
            // Create
            if(JDOHelper.isNew(position)) {
            	if(position.getUom() == null) {
            		position.setUom(product.getDefaultUom());
            	}
            	if(position.getSalesTaxType() == null) {
            		position.setSalesTaxType(product.getSalesTaxType());
            	}
            	PricingRule pricingRule = position.getPricingRule() == null ?
            		contract.getPricingRule() :
            		position.getPricingRule();
            	if(pricingRule == null) {
                	org.opencrx.kernel.product1.jmi1.Segment productSegment = Products.getInstance().getProductSegment(pm, position.refGetPath().get(2), position.refGetPath().get(4));
            		PricingRuleQuery pricingRuleQuery = (PricingRuleQuery)pm.newQuery(PricingRule.class);
            		pricingRuleQuery.thereExistsIsDefault().isTrue();
            		List<PricingRule> pricingRules = productSegment.getPricingRule(pricingRuleQuery);
            		if(!pricingRules.isEmpty()) {
            			pricingRule = pricingRules.iterator().next();
            		}
            	}
            	position.setPricingRule(pricingRule);
            	// PositionCreation
            	PositionCreation positionCreation = pm.newInstance(PositionCreation.class);
                positionCreation.setInvolved(position);
                contract.addPositionModification(
                	this.getUidAsString(),
                	positionCreation
                );
            } else {
                // Update
            	PersistenceManager pmOld = pm.getPersistenceManagerFactory().getPersistenceManager(
            		SecurityKeys.ROOT_PRINCIPAL,
            		null
            	);
                BigDecimal quantityOld = ((SalesContractPosition)pmOld.getObjectById(
                	position.refGetPath())
                ).getQuantity();
                BigDecimal quantityNew = position.getQuantity();
                if(quantityOld.compareTo(quantityNew) != 0) {
                	QuantityModification quantityModification = pm.newInstance(QuantityModification.class);
                	quantityModification.setInvolved(position);
                	quantityModification.setQuantity(quantityOld);
                    contract.addPositionModification(
                    	this.getUidAsString(),
                    	quantityModification
                    );            	
                }                            	
            }
            if(reprice) {
            	this.updateListPrice(
                    position, 
                    contract,
                    product,
                    true
                );
            }
        } catch(ServiceException e) {
        	SysLog.info(e.getMessage(), e.getCause());
        }
    }
    
    /**
     * Create sales contract position.
     * 
     * @param contract
     * @param isIgnoreProductConfiguration
     * @param name
     * @param quantity
     * @param pricingDate
     * @param product
     * @param uom
     * @param priceUom
     * @param pricingRule
     * @return
     */
    public SalesContractPosition createSalesContractPosition(
        SalesContract contract,
        Boolean isIgnoreProductConfiguration,
        String name,
        BigDecimal quantity,
        Date pricingDate,
        Product product,
        Uom uom,
        Uom priceUom,
        PricingRule pricingRule
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
    	SalesContractPosition position = null;
        try {
        	long maxLineItemNumber = 0;
        	if(contract instanceof Opportunity) {
        		AbstractOpportunityPositionQuery positionQuery = (AbstractOpportunityPositionQuery)pm.newQuery(AbstractOpportunityPosition.class);
        		positionQuery.orderByLineItemNumber().descending();
        		List<AbstractOpportunityPosition> positions = ((Opportunity)contract).getPosition(positionQuery);
        		if(!positions.isEmpty()) {
        			maxLineItemNumber = positions.iterator().next().getLineItemNumber();
        		}
        		position = pm.newInstance(OpportunityPosition.class);
        		((Opportunity)contract).addPosition(
        			this.getUidAsString(),
        			(OpportunityPosition)position
        		);
        	} else if(contract instanceof Quote) {
        		AbstractQuotePositionQuery positionQuery = (AbstractQuotePositionQuery)pm.newQuery(AbstractQuotePosition.class);
        		positionQuery.orderByLineItemNumber().descending();
        		List<AbstractQuotePosition> positions = ((Quote)contract).getPosition(positionQuery);
        		if(!positions.isEmpty()) {
        			maxLineItemNumber = positions.iterator().next().getLineItemNumber();
        		}
        		position = pm.newInstance(QuotePosition.class);
        		((Quote)contract).addPosition(
        			this.getUidAsString(),
        			(QuotePosition)position
        		);
        	} else if(contract instanceof SalesOrder) {
        		AbstractSalesOrderPositionQuery positionQuery = (AbstractSalesOrderPositionQuery)pm.newQuery(AbstractSalesOrderPosition.class);
        		positionQuery.orderByLineItemNumber().descending();
        		List<AbstractSalesOrderPosition> positions = ((SalesOrder)contract).getPosition(positionQuery);
        		if(!positions.isEmpty()) {
        			maxLineItemNumber = positions.iterator().next().getLineItemNumber();
        		}
        		position = pm.newInstance(SalesOrderPosition.class);
        		((SalesOrder)contract).addPosition(
        			this.getUidAsString(),
        			(SalesOrderPosition)position
        		);
        	} else if(contract instanceof Invoice) {
        		AbstractInvoicePositionQuery positionQuery = (AbstractInvoicePositionQuery)pm.newQuery(AbstractInvoicePosition.class);
        		positionQuery.orderByLineItemNumber().descending();
        		List<AbstractInvoicePosition> positions = ((Invoice)contract).getPosition(positionQuery);
        		if(!positions.isEmpty()) {
        			maxLineItemNumber = positions.iterator().next().getLineItemNumber();
        		}
        		position = pm.newInstance(InvoicePosition.class);
        		((Invoice)contract).addPosition(
        			this.getUidAsString(),
        			(InvoicePosition)position
        		);
        	} else {
                return null;
            }
            // lineItemNumber
        	Long positionNumber = new Long(100000 * (maxLineItemNumber / 100000 + 1));
            position.setLineItemNumber(
            	positionNumber
            );
            position.setPositionNumber(
            	positionNumber.toString()
            );
            // name
            if(name != null) {
                position.setName(name);
            } else {
                position.setName("Position " + position.getLineItemNumber());
            }
            // quantity
            if(quantity != null) {
                position.setQuantity(quantity);
            } else {
                position.setQuantity(BigDecimal.ONE);
            }
            // pricingDate
            if(pricingDate != null) {
                position.setPricingDate(pricingDate);
            } else {
                position.setPricingDate(
                    contract.getPricingDate()
                );
            }
            // uom (only touch if specified as param
            position.setUom(uom);
            // priceUom (only touch if specified as param
            if(priceUom != null) {
                position.setPriceUom(priceUom);
            }
            // pricingRule
            if(pricingRule != null) {
                position.setPricingRule(pricingRule);
            }
            // calcRule
            position.setCalcRule(
                contract.getCalcRule()
            );            
            // Update position
            if(product != null) {
	            this.updateSalesContractPosition(
	                contract,
	                position,
	                product,
	                true
	            );
	            if(position instanceof ConfiguredProduct) {
	            	((ConfiguredProduct)position).setProduct(product);
	            }
	            // Clone configurations
	            if((isIgnoreProductConfiguration == null) || !isIgnoreProductConfiguration.booleanValue()) {
	                Products.getInstance().cloneProductConfigurationSet(
	                    product,
	                    position,
	                    false,
	                    true,
	                    JDOHelper.isNew(contract) ? null : contract.getOwningUser(),
	                    JDOHelper.isNew(contract) ? null : contract.getOwningGroup()
	                );
	            }
            }
            // Touch contract --> jdoPreStore will be invoked
            this.markContractAsDirty(
            	contract
            );
        } catch(Exception e) {
	        new ServiceException(e).log();
	    }
	    return position;
    }
    
    /**
     * Remove sales contract position callback. Override for custom-specific behavour.
     * 
     * @param position
     * @param checkForMinPositions
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeSalesContractPosition(
    	SalesContractPosition position,
        boolean checkForMinPositions,
        boolean preDelete
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
    	SalesContract contract = (SalesContract)pm.getObjectById(
			position.refGetPath().getParent().getParent()
		);
        // Make a copy of the removed position
        Marshaller positionMarshaller = new Marshaller() {
            public Object marshal(
            	Object s
            ) throws ServiceException {
            	if(s instanceof SalesContractPosition) {
            		SalesContractPosition position = (SalesContractPosition)s;
            		PersistenceManager pm = JDOHelper.getPersistenceManager(position);
            		RemovedPosition removedPosition = pm.newInstance(RemovedPosition.class);
            		Contracts.getInstance().copySalesContractPosition(
            			position, 
            			removedPosition
            		);
            		return removedPosition;
            	}
            	else {
            		return s;
            	}
            }
            public Object unmarshal(Object s) {
                throw new UnsupportedOperationException();
            }
        };
        Map<String,Marshaller> objectMarshallers = new HashMap<String,Marshaller>();
        objectMarshallers.put("org:opencrx:kernel:contract1:OpportunityPosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:QuotePosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:SalesOrderPosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:InvoicePosition", positionMarshaller);
        RemovedPosition removedPosition = (RemovedPosition)Cloneable.getInstance().cloneObject(
        	position, 
        	contract, 
        	"removedPosition", 
        	objectMarshallers, 
        	null,
        	contract.getOwningUser(),
        	contract.getOwningGroup()
        );
        // Update position modifications
        // Replace involved with removed position
        Collection<PositionModification> positionModifications = contract.getPositionModification();
        for(PositionModification positionModification: positionModifications) {
        	try {
	        	if(positionModification.getInvolved().equals(position)) {
	        		positionModification.setInvolved(removedPosition);
	        	}
        	}
        	catch(Exception e) {}
        }
        PositionRemoval positionRemoval = pm.newInstance(PositionRemoval.class);
        positionRemoval.setInvolved(removedPosition);
        contract.addPositionModification(
        	false,
        	this.getUidAsString(),
        	positionRemoval
        );
        if(!preDelete) {
        	position.refDelete();        	
        }
        this.markContractAsDirty(
        	contract
        );
    }

    /**
     * Remove contract callback. Override for custom-specific behaviour.
     * 
     * @param contract
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeContract(
        AbstractContract contract,
        boolean preDelete
    ) throws ServiceException {
    }
    
    /**
     * Remove all pending inventory bookings of contract. Return last
     * final booking or null if no inventory booking is set on the contract.
     * 
     * @param contract
     * @return
     * @throws ServiceException
     */
    public CompoundBooking removePendingInventoryBookings(
        SalesContract contract
    ) throws ServiceException {
    	// List of bookings is ordered by booking date
    	List<CompoundBooking> inventoryCbs = contract.getInventoryCb();    	
    	List<CompoundBooking> removedInventoryCbs = new ArrayList<CompoundBooking>();
    	CompoundBooking lastFinalInventoryCb = null;
    	for(CompoundBooking cb: inventoryCbs) {
            short bookingStatus = cb.getBookingStatus();
            if(bookingStatus == Depots.BOOKING_STATUS_PENDING) {
                Depots.getInstance().removeCompoundBooking(
                    cb,
                    false
                );
                removedInventoryCbs.add(cb);
            }
            else {
                lastFinalInventoryCb = cb;
            }
        }
    	contract.getInventoryCb().removeAll(removedInventoryCbs);
        return lastFinalInventoryCb;
    }
    
    //-------------------------------------------------------------------------
    public CompoundBooking updateInventory(
        SalesContract contract
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
        CompoundBooking lastFinalInventoryCb = 
        	this.removePendingInventoryBookings(
                contract
            );
        // Get all position modifications since the last inventory update marked as final
        Date positionModificationsSince = lastFinalInventoryCb != null ? 
        	lastFinalInventoryCb.getBookingDate() :
        	null;                
        Depot deliveryDepot = null;        
        // Test depots of contract. It must have at least
        // a goods delivery depot.
        Collection<DepotReference> depotReferences = contract.getDepotReference();
        boolean hasDepotGoodsDelivery = false;
        for(DepotReference depotReference: depotReferences) {
            short depotUsage = depotReference.getDepotUsage();
            if((depotReference.getDepot() != null) && (depotUsage == Depots.DEPOT_USAGE_GOODS_DELIVERY)) {
                hasDepotGoodsDelivery = true;
                deliveryDepot = depotReference.getDepot();
            }
        }
        if(!hasDepotGoodsDelivery) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_DELIVERY,
                "Missing goods delivery depot.",
                new BasicException.Parameter("param0", contract.refGetPath())
            );                                                                            
        }
        // Test depots of all positions. Each position must have
        // a depot with usage=DEPOT_USAGE_GOODS_ISSUE and 
        // usage=DEPOT_USAGE_GOODS_RETURN
        List<SalesContractPosition> allPositions = new ArrayList<SalesContractPosition>();
        if(contract instanceof Opportunity) {
        	Collection<AbstractOpportunityPosition> p = ((Opportunity)contract).getPosition();
        	allPositions.addAll(p);
        }
        else if(contract instanceof Quote) {
        	Collection<AbstractQuotePosition> p = ((Quote)contract).getPosition();
        	allPositions.addAll(p);
        }
        else if(contract instanceof SalesOrder) {
        	Collection<AbstractSalesOrderPosition> p = ((SalesOrder)contract).getPosition();
        	allPositions.addAll(p);
        }
        else if(contract instanceof Invoice) {
        	Collection<AbstractInvoicePosition> p = ((Invoice)contract).getPosition();
        	allPositions.addAll(p);
        }
        Collection<RemovedPosition> removedPositions = contract.getRemovedPosition();
        for(RemovedPosition removedPosition: removedPositions) {
        	allPositions.add(removedPosition);
        }
        // Create positions
        List<DepotPosition> issueDepotPositions = new ArrayList<DepotPosition>();
        List<DepotPosition> returnDepotPositions = new ArrayList<DepotPosition>();
        List<DepotPosition> deliveryDepotPositions = new ArrayList<DepotPosition>();
        int ii = 0;
        for(Iterator<SalesContractPosition> i = allPositions.iterator(); i.hasNext(); ii++) {
        	SalesContractPosition position = i.next();
            Depot issueDepot = null;
            Depot returnDepot =  null;
            boolean holderQualifiesPosition = false;
            // Depot references at contract-level
        	depotReferences = contract.getDepotReference();
            for(DepotReference depotReference: depotReferences) {
                short depotUsage = depotReference.getDepotUsage();
                if(depotReference.getDepot() != null) {
	                if(depotUsage == Depots.DEPOT_USAGE_GOODS_ISSUE) {
	                    issueDepot = depotReference.getDepot();
	                    // The issue depot determines the useDepotPositionQualifier 
	                    holderQualifiesPosition = depotReference.isHolderQualifiesPosition();
	                }
	                if(depotUsage == Depots.DEPOT_USAGE_GOODS_RETURN) {
	                    returnDepot = depotReference.getDepot();
	                }
                }
            }
            // Depot references at position-level override depot references at contract-level
        	depotReferences = position.getDepotReference();
            for(DepotReference depotReference: depotReferences) {
                short depotUsage = depotReference.getDepotUsage();
                if(depotReference.getDepot() != null) {
	                if(depotUsage == Depots.DEPOT_USAGE_GOODS_ISSUE) {
	                    issueDepot = depotReference.getDepot();
	                    // The issue depot determines the useDepotPositionQualifier 
	                    holderQualifiesPosition = depotReference.isHolderQualifiesPosition();
	                }
	                if(depotUsage == Depots.DEPOT_USAGE_GOODS_RETURN) {
	                    returnDepot = depotReference.getDepot();
	                }
                }
            }
            if(issueDepot == null) {
                // Removed positions with no valid depot references can not have inventory bookings.
                // The position can be ignored.
                if(position instanceof AbstractRemovedPosition) {
                    i.remove();
                    continue;
                }
                else {
                    throw new ServiceException(
                        OpenCrxException.DOMAIN,
                        OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_ISSUE,
                        "Missing goods issue depot.",
                        new BasicException.Parameter("param0", position.getLineItemNumber())  
                    );
                }
            }
            if(returnDepot == null) {
                // Removed positions with no valid depot references can not have inventory bookings.
                // The position can be ignored.
                if(position instanceof AbstractRemovedPosition) {
                    i.remove();
                    continue;
                }
                else {
                    throw new ServiceException(
                        OpenCrxException.DOMAIN,
                        OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_RETURN,
                        "Missing goods return depot.",
                        new BasicException.Parameter("param0", position.getLineItemNumber())   
                    );
                }
            }
            Product product = position instanceof ConfiguredProduct ?
            	((ConfiguredProduct)position).getProduct() :
            		null;
            if(product == null) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.DEPOT_MISSING_PRODUCT,
                    "Missing product.",
                    new BasicException.Parameter("param0", position.getLineItemNumber())   
                );            	
            }
            // Delivery depot position
            deliveryDepotPositions.add( 
                Depots.getInstance().openDepotPosition(
                    deliveryDepot,
                    null, // positionName
                    null, // positionDescription
                    new Date(),
                    holderQualifiesPosition ? // positionQualifier
                    	position.getPositionNumber() : 
                    	null,
                    product,
                    Boolean.FALSE
                )
            );
            // Issue depot position
            issueDepotPositions.add( 
                Depots.getInstance().openDepotPosition(
                    issueDepot,
                    null, // positionName
                    null, // positionDescription
                    new Date(),
                    holderQualifiesPosition ?  // positionQualifier
                    	position.getPositionNumber() : 
                    	null,
                    product,
                    Boolean.FALSE
                )
            );
            // Return depot position
            returnDepotPositions.add(
                Depots.getInstance().openDepotPosition(
                    returnDepot,
                    null, // positionName
                    null, // positionDescription
                    new Date(),
                    holderQualifiesPosition ? // positionQualifier
                    	position.getPositionNumber() : 
                    	null,
                    product,
                    Boolean.FALSE
                )
            );
        }
        // Create booking        
        List<DepotPosition> creditPositions = new ArrayList<DepotPosition>();
        List<DepotPosition> debitPositions = new ArrayList<DepotPosition>();
        List<BigDecimal> quantities = new ArrayList<BigDecimal>();
        List<String> bookingTextNames = new ArrayList<String>();
        List<SalesContractPosition> origins = new ArrayList<SalesContractPosition>();        
        ii = 0;
        for(Iterator<SalesContractPosition> i = allPositions.iterator(); i.hasNext(); ii++) {
        	SalesContractPosition position = i.next();            
            // Get all modifications of this position since positionModificationsSince ordered by creation date
            PositionModificationQuery positionModificationQuery = (PositionModificationQuery)pm.newQuery(PositionModification.class);
            positionModificationQuery.thereExistsInvolved().equalTo(position);
            if(positionModificationsSince != null) {
            	positionModificationQuery.createdAt().greaterThan(
            		positionModificationsSince
            	);
            }
            positionModificationQuery.orderByCreatedAt().ascending();            
            List<PositionModification> positionModifications = contract.getPositionModification(positionModificationQuery);
            BigDecimal quantityBeforeFirstModification = BigDecimal.ZERO;
            for(PositionModification positionModification: positionModifications) {
                if(positionModification instanceof QuantityModification) {
                	QuantityModification quantityModification = (QuantityModification)positionModification;
                    quantityBeforeFirstModification = position.getQuantity().subtract(
                        quantityModification.getQuantity()
                    );
                    break;
                }
                else if(positionModification instanceof PositionCreation) {
                    quantityBeforeFirstModification = position.getQuantity();
                    break;
                }
                else if(positionModification instanceof PositionRemoval) {
                    quantityBeforeFirstModification = position.getQuantity().negate();
                    break;
                }
            }
            // No inventory booking if quantity == 0
            if(quantityBeforeFirstModification.signum() != 0) {
                if(quantityBeforeFirstModification.signum() < 0) {
                    creditPositions.add(
                        returnDepotPositions.get(ii)
                    );
                    debitPositions.add(
                        deliveryDepotPositions.get(ii)
                    );
                    bookingTextNames.add(
                        BOOKING_TEXT_NAME_RETURN_GOODS
                    );
                }
                else {
                    creditPositions.add(
                        deliveryDepotPositions.get(ii)
                    );
                    debitPositions.add(
                        issueDepotPositions.get(ii)
                    );
                    bookingTextNames.add(
                        BOOKING_TEXT_NAME_DELIVER_GOODS
                    );
                }
                origins.add(
                    position
                );
                quantities.add(
                    quantityBeforeFirstModification.abs()
                );
            }
        }
        if(!quantities.isEmpty()) {
        	DepotEntity depotEntity =
        		(DepotEntity)pm.getObjectById(
        			deliveryDepot.refGetPath().getPrefix(7)
        		);
            CompoundBooking inventoryCb = 
                Depots.getInstance().createCompoundBooking(
                    depotEntity,
                    new Date(),
                    Depots.BOOKING_TYPE_STANDARD,
                    quantities.toArray(new BigDecimal[quantities.size()]),
                    bookingTextNames.toArray(new String[bookingTextNames.size()]),
                    null, // bookingTexts
                    creditPositions.toArray(new DepotPosition[creditPositions.size()]),
                    debitPositions.toArray(new DepotPosition[debitPositions.size()]),
                    origins.toArray(new BookingOrigin[origins.size()]),
                    null, // reversalOf
                    null // errors
                );
            contract.getInventoryCb().add(
                inventoryCb
            );
            return inventoryCb;
        }
        else {
            return null;
        }
    }

    //-------------------------------------------------------------------------
    public void updatePricingState(
    	SalesContractPosition position,
    	short pricingState
    ) {
    	if(JDOHelper.isPersistent(position)) {
	    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
	        SalesContract contract = 
	        	(SalesContract)pm.getObjectById(
	            position.refGetPath().getParent().getParent()
	        );
	        this.updatePricingState(
	        	contract, 
	        	pricingState
	        );
    	}
        position.setPricingState(
            PRICING_STATE_DIRTY
        );
    }
    
    //-------------------------------------------------------------------------
    public void updatePricingState(
    	SalesContract contract,
    	short pricingState
    ) {
        contract.setPricingState(
            PRICING_STATE_DIRTY
        );    	
    }
    
    //-------------------------------------------------------------------------
    public void updateContractState(
    	AbstractContract contract,
    	short contractState
    ) {
    }
    
    /**
     * Update sales contract position callback. Override for custom-specific behaviour.
     * 
     * @param position
     * @throws ServiceException
     */
    protected void updateSalesContractPosition(
    	SalesContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
    	SalesContract contract = (SalesContract)pm.getObjectById(
            position.refGetPath().getParent().getParent()
        );
        if(position instanceof ConfiguredProduct) {
        	Product product = ((ConfiguredProduct)position).getProduct();
	        if(product != null) {
	            this.updateSalesContractPosition(
	                contract,
	            	position,
	                product,
	                false
	            );
	        }
        }
        this.markContractAsDirty(
            contract
        );
    }

    //-------------------------------------------------------------------------
    public short repriceSalesContractPosition(
        SalesContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
    	SalesContract contract = (SalesContract)pm.getObjectById(
			position.refGetPath().getParent().getParent()
		);
    	short pricingState = 0;
        if(position instanceof ConfiguredProduct) {
        	Product product = ((ConfiguredProduct)position).getProduct();
        	if(product != null) {
	            position.setPriceLevel(null);
	            this.updateSalesContractPosition(
	                contract,
	                position,
	                product,
	                true
	            );
        	}
        	pricingState = position.getPricingState();
        }
        return pricingState;
    }
    
    /**
     * Re-price the given sales contract.
     * 
     * @param contract
     * @throws ServiceException
     */
    public void repriceSalesContract(
    	SalesContract contract
    ) throws ServiceException {
        List<SalesContractPosition> positions = new ArrayList<SalesContractPosition>();
        if(contract instanceof Opportunity) {
        	Collection<AbstractOpportunityPosition> p = ((Opportunity)contract).getPosition();
        	positions.addAll(p);
        }
        else if(contract instanceof Quote) {
        	Collection<AbstractQuotePosition> p = ((Quote)contract).getPosition();
        	positions.addAll(p);
        }
        else if(contract instanceof SalesOrder) {
        	Collection<AbstractSalesOrderPosition> p = ((SalesOrder)contract).getPosition();
        	positions.addAll(p);
        }
        else if(contract instanceof Invoice) {
        	Collection<AbstractInvoicePosition> p = ((Invoice)contract).getPosition();
        	positions.addAll(p);
        }
        short pricingState = PRICING_STATE_OK;
        for(SalesContractPosition position: positions) {
            short pricingStatePosition = this.repriceSalesContractPosition(
                position
            );
            if(pricingStatePosition == PRICING_STATE_DIRTY) {
                pricingState = PRICING_STATE_DIRTY;
            }
        }
        contract.setPricingState(
            pricingState
        );
    }

    /**
     * Count contracts matching the given contract filter.
     * 
     * @param contractFilter
     * @return
     * @throws ServiceException
     */
    public int countFilteredContract(
        AbstractFilterContract contractFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractFilter);
        AbstractContractQuery query = (AbstractContractQuery)pm.newQuery(AbstractContract.class);
        QueryExtensionRecord queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);    	
        List<AbstractContract> contracts = contractFilter.getFilteredContract(query);
        return contracts.size();
    }

    /**
     * Get contract amounts for sales contract permissions.
     * 
     * @param rootPkg
     * @param calculationRule
     * @param position
     * @param minMaxAdjustedQuantity
     * @param uomScaleFactor
     * @param salesTaxRate
     * @return
     */
    public static GetPositionAmountsResult getSalesContractPositionAmounts(
        RefPackage_1_0 rootPkg,
        CalculationRule calculationRule,
        SalesContractPosition position,
        java.math.BigDecimal minMaxAdjustedQuantity,
        java.math.BigDecimal uomScaleFactor,
        java.math.BigDecimal salesTaxRate
    ) {
    	try {
	    	return Contracts.getInstance().getSalesContractPositionAmounts(
	    		calculationRule, 
	    		position, 
	    		minMaxAdjustedQuantity, 
	    		uomScaleFactor, 
	    		salesTaxRate
	    	);
    	} catch(ServiceException e) {
    		throw new RuntimeServiceException(e);
    	}
    }
    
    /**
     * Get amounts for contract positions.
     * 
     * @param calculationRule
     * @param position
     * @param minMaxAdjustedQuantity
     * @param uomScaleFactor
     * @param salesTaxRate
     * @return
     */
    public GetPositionAmountsResult getSalesContractPositionAmounts(
        CalculationRule calculationRule,
        SalesContractPosition position,
        BigDecimal minMaxAdjustedQuantity,
        BigDecimal uomScaleFactor,
        BigDecimal salesTaxRate
    ) {
        BigDecimal pricePerUnit = position.getPricePerUnit() == null ?
            BigDecimal.ZERO :
            	position.getPricePerUnit();
        BigDecimal baseAmount = minMaxAdjustedQuantity.multiply(pricePerUnit.multiply(uomScaleFactor));
        // discount
        Boolean discountIsPercentage = position.isDiscountIsPercentage() != null ? 
            position.isDiscountIsPercentage() : 
            Boolean.FALSE;
        BigDecimal discount = position.getDiscount() != null ? 
            position.getDiscount() : 
            	BigDecimal.ZERO;
        // Discount is per piece in case of !discountIsPercentage
        BigDecimal discountAmount = discountIsPercentage.booleanValue() ? 
            baseAmount.multiply(discount.divide(HUNDRED, BigDecimal.ROUND_UP)) : 
            minMaxAdjustedQuantity.multiply(discount.multiply(uomScaleFactor));
        // taxAmount
        BigDecimal taxAmount = baseAmount.subtract(discountAmount).multiply(
            salesTaxRate.divide(HUNDRED, BigDecimal.ROUND_UP)
        );    
        // amount
        BigDecimal amount = baseAmount.subtract(discountAmount).add(taxAmount);      
        GetPositionAmountsResult result = Structures.create(
        	GetPositionAmountsResult.class, 
        	Datatypes.member(GetPositionAmountsResult.Member.amount, amount),
        	Datatypes.member(GetPositionAmountsResult.Member.baseAmount, baseAmount),
        	Datatypes.member(GetPositionAmountsResult.Member.discountAmount, discountAmount),
        	Datatypes.member(GetPositionAmountsResult.Member.statusCode, (short)0),
        	Datatypes.member(GetPositionAmountsResult.Member.statusMessage, null),
        	Datatypes.member(GetPositionAmountsResult.Member.taxAmount, taxAmount)        	
        );
        return result;
    }

    /**
     * Get contract amounts.
     * 
     * @param rootPkg
     * @param calculationRule
     * @param contract
     * @param lineItemNumbers
     * @param positionBaseAmounts
     * @param positionDiscountAmounts
     * @param positionTaxAmounts
     * @param positionAmounts
     * @param salesCommissions
     * @param salesCommissionIsPercentages
     * @return
     */
    public static GetContractAmountsResult getContractAmounts(
        RefPackage_1_0 rootPkg,
        CalculationRule calculationRule,
        SalesContract contract,
        java.lang.Integer[] lineItemNumbers,
        java.math.BigDecimal[] positionBaseAmounts,
        java.math.BigDecimal[] positionDiscountAmounts,
        java.math.BigDecimal[] positionTaxAmounts,
        java.math.BigDecimal[] positionAmounts,
        java.math.BigDecimal[] salesCommissions,
        java.lang.Boolean[] salesCommissionIsPercentages
    ) {
        java.math.BigDecimal totalBaseAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalDiscountAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalTaxAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalSalesCommission = java.math.BigDecimal.ZERO;
        for(int i = 0; i < positionBaseAmounts.length; i++) {
            java.math.BigDecimal baseAmount = positionBaseAmounts[i] != null ? 
            	positionBaseAmounts[i] : 
            		java.math.BigDecimal.ZERO; 
            totalBaseAmount = totalBaseAmount.add(baseAmount);
            java.math.BigDecimal discountAmount = positionDiscountAmounts[i] != null ? 
            	positionDiscountAmounts[i] : 
            		 java.math.BigDecimal.ZERO;
            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            java.math.BigDecimal taxAmount = positionTaxAmounts[i] != null ? 
            	positionTaxAmounts[i] : 
            		 java.math.BigDecimal.ZERO;
            totalTaxAmount = totalTaxAmount.add(taxAmount);
            java.math.BigDecimal salesCommission = salesCommissions[i] != null ? 
            	salesCommissions[i] : 
            		java.math.BigDecimal.ZERO;
            totalSalesCommission = totalSalesCommission.add(
              (salesCommissionIsPercentages[i] != null) && (salesCommissionIsPercentages[i].booleanValue()) ? 
            	  baseAmount.subtract(discountAmount).multiply(salesCommission.divide(new java.math.BigDecimal(100), java.math.BigDecimal.ROUND_UP)) : 
            		  salesCommission
            );
        }
        BigDecimal totalAmount = totalBaseAmount.subtract(totalDiscountAmount);
        BigDecimal totalAmountIncludingTax = totalAmount.add(totalTaxAmount);
        GetContractAmountsResult result = Structures.create(
        	GetContractAmountsResult.class, 
        	Datatypes.member(GetContractAmountsResult.Member.statusCode, (short)0),
        	Datatypes.member(GetContractAmountsResult.Member.statusMessage, null),
        	Datatypes.member(GetContractAmountsResult.Member.totalAmount, totalAmount),
        	Datatypes.member(GetContractAmountsResult.Member.totalAmountIncludingTax, totalAmountIncludingTax),
        	Datatypes.member(GetContractAmountsResult.Member.totalBaseAmount, totalBaseAmount),
        	Datatypes.member(GetContractAmountsResult.Member.totalDiscountAmount, totalDiscountAmount),
        	Datatypes.member(GetContractAmountsResult.Member.totalSalesCommission, totalSalesCommission),
        	Datatypes.member(GetContractAmountsResult.Member.totalTaxAmount, totalTaxAmount)        	
        );
        return result;
    }
    
    /**
     * Creates a contract for the given contractType. Override this method to 
     * support custom-specific contract types.
     * 
     * @param contractSegment
     * @param contractType
     * @param basedOn
     * @return
     * @throws ServiceException
     */
    public AbstractContract createContract(
    	org.opencrx.kernel.contract1.jmi1.Segment contractSegment,
    	short contractType,
    	AbstractContract basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractSegment);
    	AbstractContract contract = null;
    	ContractTypeQuery contractTypeQuery = (ContractTypeQuery)pm.newQuery(ContractType.class);
    	contractTypeQuery.contractType().equalTo(contractType);
    	List<ContractType> contractTypes = contractSegment.getContractType(contractTypeQuery);
    	ContractType defaultContractType = contractTypes.isEmpty() ? null :
    		contractTypes.iterator().next();
    	// Lead
    	if(contractType == CONTRACT_TYPE_LEAD) {
    		contract = pm.newInstance(Lead.class);
	        contractSegment.addLead(
	        	this.getUidAsString(),
	        	(Lead)contract
	        );  	     
    	} else if(contractType == CONTRACT_TYPE_OPPORTUNITY) {
        	// Opportunity
    		if(basedOn instanceof Lead) {
    			contract = this.createOpportunity((Lead)basedOn);
    		} else if(basedOn instanceof Opportunity) {
	    		contract = (Opportunity)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"opportunity", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(Opportunity.class);
    	        contractSegment.addOpportunity(
    	        	this.getUidAsString(),
    	        	(Opportunity)contract
    	        );    			
    		}
    	} else if(contractType == CONTRACT_TYPE_QUOTE) {
        	// Quote
    		if(basedOn instanceof Opportunity) {
    			contract = this.createQuote((Opportunity)basedOn);
    		} else if(basedOn instanceof Quote) {
	    		contract = (Quote)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"quote", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(Quote.class);
    	        contractSegment.addQuote(
    	        	this.getUidAsString(),
    	        	(Quote)contract
    	        );    			
    		}
    	} else if(contractType == CONTRACT_TYPE_SALES_ORDER) {
        	// SalesOrder
    		if(basedOn instanceof Quote) {
    			contract = this.createSalesOrder((Quote)basedOn);
    		} else if(basedOn instanceof SalesOrder) {
	    		contract = (SalesOrder)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"salesOrder", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(SalesOrder.class);
    	        contractSegment.addSalesOrder(
    	        	this.getUidAsString(),
    	        	(SalesOrder)contract
    	        );
    		}
    	} else if(contractType == CONTRACT_TYPE_INVOICE) {
        	// Invoice
    		if(basedOn instanceof SalesOrder) {
    			contract = this.createInvoice((SalesOrder)basedOn);
    		} else if(basedOn instanceof Invoice) {
	    		contract = (Invoice)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"invoice", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(Invoice.class);
    	        contractSegment.addInvoice(
    	        	this.getUidAsString(),
    	        	(Invoice)contract
    	        );
    		}
    	} else if(contractType == CONTRACT_TYPE_SALESVOLUME_CONTRACT) {
        	// SalesVolumeContract
    		if(basedOn instanceof SalesVolumeContract) {
	    		contract = (SalesVolumeContract)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"contract", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(SalesVolumeContract.class);
    	        contractSegment.addContract(
    	        	this.getUidAsString(),
    	        	(SalesVolumeContract)contract
    	        );
    		}
    	} else if(contractType == CONTRACT_TYPE_GENERIC_CONTRACT) {
        	// GenericContract
    		if(basedOn instanceof GenericContract) {
	    		contract = (GenericContract)Cloneable.getInstance().cloneObject(
	    			basedOn, 
	    			contractSegment, 
	    			"contract", 
	    			null, // object marshallers
	    			null, // reference filter
	    			null, // owning user
	    			null // owningGroup
	    		);
    		} else {
    	        contract = pm.newInstance(GenericContract.class);
    	        contractSegment.addContract(
    	        	this.getUidAsString(),
    	        	(GenericContract)contract
    	        );
    		}
    	}
    	if(contract != null) {
    		contract.setContractType(defaultContractType);
    	}
    	return contract;
    }
    
    /**
     * Create contract based on given contract creator.
     * 
     * @param contractCreator
     * @param name
     * @param description
     * @param contractType
     * @param activeOn
     * @param priority
     * @param basedOn
     * @return
     * @throws ServiceException
     */
    public AbstractContract createContract(
        ContractCreator contractCreator,
        String name,
        String description,
        ContractType contractType,
        Date activeOn,
        Short priority,
        AbstractContract basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractCreator);
    	String providerName = contractCreator.refGetPath().get(2);
    	String segmentName = contractCreator.refGetPath().get(4);
    	boolean useRunAsPrincipal = Utils.hasObjectRunAsPermission(
    		contractCreator.refGetPath(), 
    		Utils.getPermissions(
    			Utils.getRequestingPrincipal(pm, providerName, segmentName), 
    			Action.RUN_AS.getName()
    		)
    	);
    	org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
    		pm, 
    		providerName, 
    		segmentName
    	);
    	AbstractContract contract = null;
    	if(contractType == null) {
    		contractType = contractCreator.getDefaultType();
    	}
    	if(contractType != null) {
    		contract = this.createContract(
    			contractSegment,
    			contractType.getContractType(),
    			basedOn
    		);
    		if(contract != null) {
    			contract.setName(
    				name == null ? "@" + new Date() : name
    			);
    			if(description != null) {
    				contract.setDescription(description);
    			}
    			activeOn = activeOn == null ? contractCreator.getActiveOn() : activeOn;
    			if(activeOn != null) {
    				contract.setActiveOn(activeOn);
    			}
    			priority = priority == null ? contractCreator.getPriority() : priority;
    			if(priority != null) {
    				contract.setPriority(priority);
    			}
    			contract.setContractType(contractType);
    			Base.getInstance().assignToMe(
    				contract, 
    				true, // overwrite 
    				useRunAsPrincipal
    			);
    			this.reapplyContractCreator(
    				contract, 
    				contractCreator
    			);
    		}
    	}
    	return contract;
    }

    /**
     * Create sales contract based on given contract creator.
     * 
     * @param contractCreator
     * @param name
     * @param description
     * @param contractType
     * @param activeOn
     * @param priority
     * @param pricingDate
     * @param contractCurrency
     * @param customer
     * @param salesRep
     * @param broker
     * @param supplier
     * @param basedOn
     * @return
     * @throws ServiceException
     */
    public AbstractContract createSalesContract(
    	SalesContractCreator contractCreator,
        String name,
        String description,
        ContractType contractType,
        Date activeOn,
        Short priority,
        Date pricingDate,
        Short contractCurrency,
        Account customer,
        Account salesRep,
        Account broker,
        Account supplier,
        AbstractContract basedOn
    ) throws ServiceException {
		PersistenceManager pm = JDOHelper.getPersistenceManager(contractCreator);
		String providerName = contractCreator.refGetPath().get(2);
		String segmentName = contractCreator.refGetPath().get(4);
    	AbstractContract contract = this.createContract(
    		contractCreator, 
    		name, 
    		description, 
    		contractType, 
    		activeOn, 
    		priority,
    		basedOn
    	);
    	if(contract instanceof SalesContract) {
            org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
                pm, 
                providerName, 
                segmentName
            );
            org.opencrx.kernel.product1.jmi1.Segment productSegment = Products.getInstance().getProductSegment(
            	pm, 
            	providerName, 
            	segmentName
            );
    		SalesContract salesContract = (SalesContract)contract;
    		pricingDate = pricingDate == null ? contractCreator.getPricingDate() : pricingDate;
    		if(pricingDate != null) {
    			salesContract.setPricingDate(pricingDate);
    		}
    		salesContract.setPricingRule(
    			contractCreator.getPricingRule() == null ?
        			((SalesContract)contract).getPricingRule() == null ?
        				Products.getInstance().getDefaultPricingRule(productSegment) :
        					((SalesContract)contract).getPricingRule() :
        						contractCreator.getPricingRule()    			
    		);
    		salesContract.setCalcRule(    			
    			contractCreator.getCalcRule() == null ? 
    				((SalesContract)contract).getCalcRule() == null ?
    					this.getDefaultCalculationRule(contractSegment) : 
    						((SalesContract) contract).getCalcRule() :
    							contractCreator.getCalcRule()
    		);
    		contractCurrency = contractCurrency == null ? contractCreator.getContractCurrency() : contractCurrency;
    		if(contractCurrency != null) {
    			salesContract.setContractCurrency(contractCurrency);
    		}
    		customer = customer == null ? contractCreator.getCustomer() : customer;
    		if(customer != null) {
	    		salesContract.setCustomer(customer);
    		}
    		salesRep = salesRep == null ? contractCreator.getSalesRep() : salesRep;
    		if(salesRep != null) {
    			salesContract.setSalesRep(salesRep);
    		}
    		broker = broker == null ? contractCreator.getBroker() : broker;
    		if(broker != null) {
    			salesContract.setBroker(broker);
    		}
    		supplier = supplier == null ? contractCreator.getSupplier() : supplier;
    		if(supplier != null) {
    			salesContract.setSupplier(supplier);
    		}
    	}
    	return contract;
    }

    /**
     * Re-apply contract creator on given contract.
     * 
     * @param contract
     * @param contractCreator
     * @throws ServiceException
     */
    public void reapplyContractCreator(
    	AbstractContract contract,
    	ContractCreator contractCreator
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
    	if(contractCreator == null) {
    		contractCreator = contract.getLastAppliedCreator();
    	}
    	if(contractCreator != null) {
			List<ContractGroup> contractGroups = contractCreator.getContractGroup();
			// Owning Groups
	        Set<PrincipalGroup> owningGroups = new HashSet<PrincipalGroup>();
	        for(ContractGroup contractGroup: contractGroups) {
	            List<PrincipalGroup> groups = contractGroup.getOwningGroup();
	            for(PrincipalGroup group: groups) {
	                if(contractGroup.getName().endsWith(PRIVATE_GROUP_SUFFIX)) {
	                	String contractGroupName = contractGroup.getName().substring(0, contractGroup.getName().indexOf("~"));
	                	String principalGroupName = group.getName().substring(0, group.getName().indexOf("."));
	                	if(contractGroupName.equals(principalGroupName)) {
	                		owningGroups.add(group);
	                	}
	                }
	                else {                         	
	                	owningGroups.add(group);
	                }
	            }
	        }
	        contract.getOwningGroup().clear();
	        contract.getOwningGroup().addAll(owningGroups);    	
			// Assign contract to groups
	        Collection<ContractGroupAssignment> existingGroupAssignments = contract.getGroupAssignment();
	        List<ContractGroup> excludeGroups = new ArrayList<ContractGroup>();
	        for(ContractGroupAssignment existingGroupAssignment: existingGroupAssignments) {
	            if(existingGroupAssignment.getContractGroup() != null) {
	                excludeGroups.add(
	                    existingGroupAssignment.getContractGroup()
	                );
	            }
	        }
	        // Add new group assignments
	        for(ContractGroup contractGroup: contractGroups) {
	            if(!excludeGroups.contains(contractGroup)) {
	    			ContractGroupAssignment assignment = pm.newInstance(ContractGroupAssignment.class);
	    			assignment.setName(contractGroup.getName());
	    			assignment.setContractGroup(contractGroup);
	    			assignment.getOwningGroup().addAll(owningGroups);
	    			contract.addGroupAssignment(
	    				this.getUidAsString(),
	    				assignment
	    			);
	            }
	        }
	        // Update PropertySet
	        Collection<PropertySet> existingPropertySets = contract.getPropertySet();
	        List<String> excludePropertySets = new ArrayList<String>();
	        for(PropertySet existingPropertySet: existingPropertySets) {
	            excludePropertySets.add(
	                existingPropertySet.getName()
	            );
	        }                    
	        Collection<PropertySet> propertySets = contractCreator.getPropertySet();
	        for(PropertySet propertySet: propertySets) {
	            if(!excludePropertySets.contains(propertySet.getName())) {
	                Cloneable.getInstance().cloneObject(
	                    propertySet,
	                    contract,
	                    "propertySet",
	                    null,
	                    "property",
	                    contract.getOwningUser(),
	                    contract.getOwningGroup()
	                );
	            }
	        }
	        contract.setLastAppliedCreator(contractCreator);
    	}
    }
   
    /**
     * Store callback for account assignments.
     * 
     * @param contract
     * @throws ServiceException
     */
    protected void updateAccountAssignmentContract(
    	AccountAssignmentContract accountAssignment
    ) throws ServiceException {
    }

    /**
     * Delete callback for account assignments.
     * 
     * @param contract
     * @throws ServiceException
     */
    protected void removeAccountAssignmentContract(
    	AccountAssignmentContract accountAssignment,
    	boolean preDelete
    ) throws ServiceException {
        if(!preDelete) {
        	accountAssignment.refDelete();
        }    	
    }

	/* (non-Javadoc)
	 * @see org.opencrx.kernel.backend.AbstractImpl#preDelete(org.opencrx.kernel.generic.jmi1.CrxObject, boolean)
	 */
	@Override
	public void preDelete(
		RefObject_1_0 object, 
		boolean preDelete
	) throws ServiceException {
		super.preDelete(object, preDelete);
		if(object instanceof AbstractContract) {
			this.removeContract((AbstractContract)object, preDelete);
		} else if(object instanceof AccountAssignmentContract) {
			this.removeAccountAssignmentContract((AccountAssignmentContract)object, preDelete);
		} else if(object instanceof SalesContractPosition) {
			this.removeSalesContractPosition((SalesContractPosition)object, true, preDelete);
		}
	}

	/* (non-Javadoc)
	 * @see org.opencrx.kernel.backend.AbstractImpl#preStore(org.opencrx.kernel.generic.jmi1.CrxObject)
	 */
	@Override
	public void preStore(
		RefObject_1_0 object
	) throws ServiceException {
		super.preStore(object);
		if(object instanceof SalesContract) {
			this.updateContract((AbstractContract)object);
			this.updateSalesContract((SalesContract)object);
		} else if(object instanceof AbstractContract) {
			this.updateContract((AbstractContract)object);
		} else if(object instanceof SalesContractPosition) {
			this.updateSalesContractPosition((SalesContractPosition)object);
		} else if(object instanceof AccountAssignmentContract) {
			this.updateAccountAssignmentContract((AccountAssignmentContract)object);
		} else if(object instanceof PhoneNumber) {
			Addresses.getInstance().updatePhoneNumber((PhoneNumber)object);
		}
	}

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public final static short STATUS_CODE_OK = 0;
    public final static short STATUS_CODE_ERROR = 1;
    
    // Min/Max Quantity Handling
    public static final int MIN_MAX_QUANTITY_HANDLING_NA = 0;
    public static final int MIN_MAX_QUANTITY_HANDLING_LIMIT = 3;
    
    // Pricing Status
    public static final short PRICING_STATE_NA = 0;
    public static final short PRICING_STATE_DIRTY = 10;
    public static final short PRICING_STATE_OK = 20;
    
    // Booking texts
    public static final String BOOKING_TEXT_NAME_RETURN_GOODS = "return goods";
    public static final String BOOKING_TEXT_NAME_DELIVER_GOODS = "deliver goods";
            
    public static final String CALCULATION_RULE_NAME_DEFAULT = "Default";
        
    // Standard contract types
    public static final short CONTRACT_TYPE_LEAD = 1;
    public static final short CONTRACT_TYPE_OPPORTUNITY = 2;
    public static final short CONTRACT_TYPE_QUOTE = 3;
    public static final short CONTRACT_TYPE_SALES_ORDER = 4;
    public static final short CONTRACT_TYPE_INVOICE = 5;
    public static final short CONTRACT_TYPE_SALESVOLUME_CONTRACT = 6;
    public static final short CONTRACT_TYPE_GENERIC_CONTRACT = 7;

    public static final BigDecimal HUNDRED = new BigDecimal("100.0");
    
	public static final String PRIVATE_GROUP_SUFFIX = "~Private";
    
    public static final String DEFAULT_GET_POSITION_AMOUNTS_SCRIPT = 
        "//<pre>\n" + 
        "public static org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult getPositionAmounts(\n" + 
        "    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" +
        "    org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,\n" +  
        "    org.opencrx.kernel.contract1.jmi1.SalesContractPosition position,\n" +
        "    java.math.BigDecimal minMaxAdjustedQuantity,\n" +
        "    java.math.BigDecimal uomScaleFactor,\n" +
        "    java.math.BigDecimal salesTaxRate\n" +
        ") {\n" +
        "    return org.opencrx.kernel.backend.Contracts.getPositionAmounts(\n" + 
        "        rootPkg,\n" +
        "        calculationRule,\n" +
        "        position,\n" +
        "        minMaxAdjustedQuantity,\n" +
        "        uomScaleFactor,\n" +
        "        salesTaxRate\n" +
        "   );\n" +
        "}//</pre>";
        
    public static final String DEFAULT_GET_CONTRACT_AMOUNTS_SCRIPT = 
        "//<pre>\n" + 
        "public static org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult getContractAmounts(\n" + 
        "    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" +
        "    org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,\n" +  
        "    org.opencrx.kernel.contract1.jmi1.SalesContract contract,\n" +
        "    Integer[] lineItemNumbers,\n" +
        "    java.math.BigDecimal[] positionBaseAmounts,\n" +
        "    java.math.BigDecimal[] positionDiscountAmounts,\n" +
        "    java.math.BigDecimal[] positionTaxAmounts,\n" +
        "    java.math.BigDecimal[] positionAmounts,\n" +
        "    java.math.BigDecimal[] salesCommissions,\n" +
        "    Boolean[] salesCommissionIsPercentages\n" +
        ") {\n" +
        "    return org.opencrx.kernel.backend.Contracts.getContractAmounts(\n" +
        "        rootPkg,\n" +
        "        calculationRule,\n" +
        "        contract,\n" +
        "        lineItemNumbers,\n" +
        "        positionBaseAmounts,\n" +
        "        positionDiscountAmounts,\n" +
        "        positionTaxAmounts,\n" +
        "        positionAmounts,\n" +
        "        salesCommissions,\n" +
        "        salesCommissionIsPercentages\n" +
        "    );\n" +
        "}//</pre>";
        
}

//--- End of File -----------------------------------------------------------

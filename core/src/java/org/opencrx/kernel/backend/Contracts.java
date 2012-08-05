/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Contracts.java,v 1.118 2010/12/09 12:45:56 wfro Exp $
 * Description: Contracts
 * Revision:    $Revision: 1.118 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/09 12:45:56 $
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

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.opencrx.kernel.contract1.cci2.AbstractContractQuery;
import org.opencrx.kernel.contract1.cci2.CalculationRuleQuery;
import org.opencrx.kernel.contract1.cci2.PositionModificationQuery;
import org.opencrx.kernel.contract1.jmi1.AbstractContract;
import org.opencrx.kernel.contract1.jmi1.AbstractContractPosition;
import org.opencrx.kernel.contract1.jmi1.AbstractInvoicePosition;
import org.opencrx.kernel.contract1.jmi1.AbstractOpportunityPosition;
import org.opencrx.kernel.contract1.jmi1.AbstractQuotePosition;
import org.opencrx.kernel.contract1.jmi1.AbstractRemovedPosition;
import org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition;
import org.opencrx.kernel.contract1.jmi1.CalculationRule;
import org.opencrx.kernel.contract1.jmi1.DeliveryInformation;
import org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult;
import org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.InvoicePosition;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.OpportunityPosition;
import org.opencrx.kernel.contract1.jmi1.PositionCreation;
import org.opencrx.kernel.contract1.jmi1.PositionModification;
import org.opencrx.kernel.contract1.jmi1.PositionRemoval;
import org.opencrx.kernel.contract1.jmi1.QuantityModification;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.QuotePosition;
import org.opencrx.kernel.contract1.jmi1.RemovedPosition;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.contract1.jmi1.SalesOrderPosition;
import org.opencrx.kernel.depot1.jmi1.BookingOrigin;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReference;
import org.opencrx.kernel.forecast1.cci2.BrokerAssignmentQuery;
import org.opencrx.kernel.forecast1.cci2.CustomerAssignmentQuery;
import org.opencrx.kernel.forecast1.cci2.SalesRepAssignmentQuery;
import org.opencrx.kernel.forecast1.cci2.SalesVolumeBudgetQuery;
import org.opencrx.kernel.forecast1.jmi1.BrokerAssignment;
import org.opencrx.kernel.forecast1.jmi1.CustomerAssignment;
import org.opencrx.kernel.forecast1.jmi1.SalesRepAssignment;
import org.opencrx.kernel.forecast1.jmi1.SalesVolumeBudget;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.generic.jmi1.Description;
import org.opencrx.kernel.generic.jmi1.DescriptionContainer;
import org.opencrx.kernel.product1.cci2.PricingRuleQuery;
import org.opencrx.kernel.product1.cci2.ProductBasePriceQuery;
import org.opencrx.kernel.product1.jmi1.AbstractPriceLevel;
import org.opencrx.kernel.product1.jmi1.ConfiguredProduct;
import org.opencrx.kernel.product1.jmi1.PricingRule;
import org.opencrx.kernel.product1.jmi1.Product;
import org.opencrx.kernel.product1.jmi1.ProductBasePrice;
import org.opencrx.kernel.uom1.jmi1.Uom;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.exception.RuntimeServiceException;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.marshalling.Marshaller;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.kernel.log.SysLog;

public class Contracts extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Contracts());
	}
	
    //-------------------------------------------------------------------------
	public static Contracts getInstance(
	) throws ServiceException {
		return getInstance(Contracts.class);
	}

	//-------------------------------------------------------------------------
	protected Contracts(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public org.opencrx.kernel.contract1.jmi1.CalculationRule findCalculationRule(
        String name,
        org.opencrx.kernel.contract1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        CalculationRuleQuery calculationRuleQuery = (CalculationRuleQuery)pm.newQuery(CalculationRule.class);
        calculationRuleQuery.name().equalTo(name);
        List<CalculationRule> calculationRules = segment.getCalculationRule(calculationRuleQuery);
        return calculationRules.isEmpty() ? 
            null : 
            calculationRules.iterator().next();
    }
    
    //-----------------------------------------------------------------------
    /**
     * @return Returns the contract segment.
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

    //-----------------------------------------------------------------------
    public CalculationRule initCalculationRule(
        String calculationRuleName,
        String description,
        String getPositionAmountsScript,
        String getContractAmountsScript,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = this.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        CalculationRule calculationRule = null;
        if((calculationRule = this.findCalculationRule(calculationRuleName, contractSegment, pm)) != null) {
            return calculationRule;            
        }                
        pm.currentTransaction().begin();
        calculationRule = pm.newInstance(CalculationRule.class);
        calculationRule.refInitialize(false, false);
        calculationRule.setName(calculationRuleName);
        calculationRule.setDescription(description);
        calculationRule.setGetPositionAmountsScript(getPositionAmountsScript);
        calculationRule.setGetContractAmountsScript(getContractAmountsScript);
        calculationRule.setDefault(true);
        calculationRule.getOwningGroup().addAll(
            contractSegment.getOwningGroup()
        );
        contractSegment.addCalculationRule(
            false,
            UUIDConversion.toUID(uuids.next()),
            calculationRule
        );                        
        pm.currentTransaction().commit();        
        return calculationRule;
    }
        
    //-------------------------------------------------------------------------
    void copyCrxObject(
    	CrxObject from,
    	CrxObject to
    ) {
        // Index 0
        to.setUserBoolean0(from.isUserBoolean0());
        to.setUserNumber0(from.getUserNumber0());
        to.setUserString0(from.getUserString0());
        to.setUserDateTime0(from.getUserDateTime0());
        to.setUserDate0(from.getUserDate0());
        to.setUserCode0(from.getUserCode0());            
        // Index 1
        to.setUserBoolean1(from.isUserBoolean1());
        to.setUserNumber1(from.getUserNumber1());
        to.setUserString1(from.getUserString1());
        to.setUserDateTime1(from.getUserDateTime1());
        to.setUserDate1(from.getUserDate1());
        to.setUserCode1(from.getUserCode1());            
        // Index 2
        to.setUserBoolean2(from.isUserBoolean2());
        to.setUserNumber2(from.getUserNumber2());
        to.setUserString2(from.getUserString2());
        to.setUserDateTime2(from.getUserDateTime2());
        to.setUserDate2(from.getUserDate2());
        to.setUserCode2(from.getUserCode2());            
        // Index 3
        to.setUserBoolean3(from.isUserBoolean3());
        to.setUserNumber3(from.getUserNumber3());
        to.setUserString3(from.getUserString3());
        to.setUserDateTime3(from.getUserDateTime3());
        to.setUserDate3(from.getUserDate3());
        to.setUserCode3(from.getUserCode3());            
        // Index 4
        to.setUserBoolean4(from.getUserBoolean4());
        to.setUserNumber4(from.getUserNumber4());
        to.setUserString4(from.getUserString4());
        to.setUserDateTime4(from.getUserDateTime4());
        to.setUserDate4(from.getUserDate4());
        to.setUserCode4(from.getUserCode4());                	
    }
    
    //-------------------------------------------------------------------------
    void copyAbstractContract(
        AbstractContract from,
        AbstractContract to
    ) throws ServiceException {
        to.setName(from.getName());
        to.setDescription(from.getDescription());
        to.setPriority(from.getPriority());
        to.setActiveOn(from.getActiveOn());
        to.setExpiresOn(from.getExpiresOn());
        to.setCancelOn(from.getCancelOn());
        to.setClosedOn(from.getClosedOn());
        to.setContractNumber(from.getContractNumber());
        to.setContractCurrency(from.getContractCurrency());
        to.setPaymentTerms(from.getPaymentTerms());
        to.setContractLanguage(from.getContractLanguage());
        to.setContractState(from.getContractState());
        to.setPricingDate(from.getPricingDate());
        to.getCompetitor().clear();
        to.getCompetitor().addAll(from.getCompetitor());
        to.getContact().clear();
        to.getContact().addAll(from.getContact());
        to.setBroker(from.getBroker());
        to.setCustomer(from.getCustomer());
        to.setSalesRep(from.getSalesRep());
        to.setSupplier(from.getSupplier());
        to.getActivity().clear();
        to.getActivity().addAll(from.getActivity());
        to.setOrigin(from.getOrigin());
        to.getInventoryCb().clear();
        to.getInventoryCb().addAll(from.getInventoryCb());
        to.setPricingRule(from.getPricingRule());
        to.setCalcRule(from.getCalcRule());
        to.setShippingMethod(from.getShippingMethod());
        to.setShippingTrackingNumber(from.getShippingTrackingNumber());
        to.setShippingInstructions(from.getShippingInstructions());
        to.setGift(from.isGift());
        to.setGiftMessage(from.getGiftMessage());
        to.getOwningGroup().clear();
        to.getOwningGroup().addAll(from.getOwningGroup());
        to.setOwningUser(from.getOwningUser());
        to.setDisabled(from.isDisabled());
        to.setDisabledReason(from.getDisabledReason());
        to.getExternalLink().clear();
        to.getExternalLink().addAll(from.getExternalLink());
        to.getCategory().clear();
        to.getCategory().addAll(from.getCategory());
    	copyCrxObject(
    		from,
    		to
    	);
    }

    //-------------------------------------------------------------------------
    void copyContractPosition(
        AbstractContractPosition from,
        AbstractContractPosition to
    ) throws ServiceException {
    	to.setLineItemNumber(from.getLineItemNumber());
    	to.setName(from.getName());
    	to.setDescription(from.getDescription());
    	to.setPositionNumber(from.getPositionNumber());
    	to.setContractPositionState(from.getContractPositionState());
    	to.setQuantity(from.getQuantity());
    	to.setMinQuantity(from.getMinQuantity());
    	to.setMaxQuantity(from.getMaxQuantity());
    	to.setOffsetQuantity(from.getOffsetQuantity());    	
    	try { to.setMinMaxQuantityHandling(from.getMinMaxQuantityHandling()); } catch(Exception e) {}
    	to.setPricePerUnit(from.getPricePerUnit());
    	try { to.setPricingState(from.getPricingState()); } catch(Exception e) {}
    	to.setDiscount(from.getDiscount());
    	to.setDiscountDescription(from.getDiscountDescription());
    	to.setDiscountIsPercentage(from.isDiscountIsPercentage());
    	to.setSalesCommission(from.getSalesCommission());
    	to.setSalesCommissionIsPercentage(from.isSalesCommissionIsPercentage());
    	to.getContact().clear();
    	to.getContact().addAll(from.getContact());
    	to.setPriceUom(from.getPriceUom());
    	to.setUom(from.getUom());
    	to.setSalesTaxType(from.getSalesTaxType());    	
    	try { to.setListPrice(from.getListPrice()); } catch(Exception e) {}
    	to.setPricingRule(from.getPricingRule());
    	to.setPriceLevel(from.getPriceLevel());
    	to.setCalcRule(from.getCalcRule());
    	try { to.setShippingMethod(from.getShippingMethod()); } catch(Exception e) {}
    	to.setShippingTrackingNumber(from.getShippingTrackingNumber());
    	to.setShippingInstructions(from.getShippingInstructions());
    	try { to.setGift(from.isGift()); } catch(Exception e) {}
    	to.setGiftMessage(from.getGiftMessage());
    	to.setCarrier(from.getCarrier());
    	if(from instanceof ConfiguredProduct && to instanceof ConfiguredProduct) {
    		((ConfiguredProduct)to).setProduct(((ConfiguredProduct)from).getProduct());
    		((ConfiguredProduct)to).setProductSerialNumber(((ConfiguredProduct)from).getProductSerialNumber());
    		((ConfiguredProduct)to).setConfigType(((ConfiguredProduct)from).getConfigType());
    	}
    	if(from instanceof CrxObject && to instanceof CrxObject) {
	    	copyCrxObject(
	    		(CrxObject)from,
	    		(CrxObject)to
	    	);
    	}
    }

    //-------------------------------------------------------------------------
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
    
    //-------------------------------------------------------------------------
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
    
    //-------------------------------------------------------------------------
    public void markAsClosed(
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract,
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
        org.opencrx.kernel.contract1.jmi1.AbstractContractPosition position
    ) {
        return (position.getUom() != null) && (position.getPriceUom() != null) ? 
        	Utils.getUomScaleFactor(position.getUom(), position.getPriceUom()) : 
        	BigDecimal.ONE;
    }

    //-------------------------------------------------------------------------
    protected BigDecimal getSalesTaxRate(
        org.opencrx.kernel.contract1.jmi1.AbstractContractPosition position
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

    //-------------------------------------------------------------------------
    protected BigDecimal getMinMaxAdjustedQuantity(
        AbstractContractPosition position
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
    public GetPositionAmountsResult getPositionAmounts(
        CalculationRule calculationRule,
        AbstractContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(calculationRule);
        String script = (calculationRule.getGetPositionAmountsScript() == null) || (calculationRule.getGetPositionAmountsScript().length() == 0) ? 
            DEFAULT_GET_POSITION_AMOUNTS_SCRIPT : 
            calculationRule.getGetPositionAmountsScript();
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = Utils.getContractPackage(pm); 
        try {
            Method getPositionAmountMethod = getPositionAmountMethods.get(script);
            if(getPositionAmountMethod == null) {
                Class<?> c = new ClassBodyEvaluator(script).getClazz();
                getPositionAmountMethod = c.getMethod(
                    "getPositionAmounts", 
                    new Class[] {
                        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                        org.opencrx.kernel.contract1.jmi1.CalculationRule.class, 
                        org.opencrx.kernel.contract1.jmi1.AbstractContractPosition.class,
                        BigDecimal.class, // minMaxAdjustedQuantity
                        BigDecimal.class, // uomScaleFactor
                        BigDecimal.class // salesTaxRate
                    }
                );
            }
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
        catch(CompileException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Can not compile getPositionAmountsScript:\n" +
                e.getMessage(),
                null
            );
        }
        catch(Parser.ParseException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Can not parse getPositionAmounts:\n" +
                e.getMessage(),
                null
            );
        }
        catch(Scanner.ScanException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Can not scan getPositionAmounts:\n" +
                e.getMessage(),
                null
            );
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
                "Can not invoke getPositionAmounts:\n" +
                e.getTargetException().getMessage(),
                null
            );
        }
        catch(IllegalAccessException e) {
            return contractPkg.createGetPositionAmountsResult(
                null, null, null,
                STATUS_CODE_ERROR,
                "Illegal access when invoking getPositionAmounts:\n" +
                e.getMessage(),
                null
            );
        }
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult getContractAmounts(
        CalculationRule calculationRule,
        AbstractContract contract,
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
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = Utils.getContractPackage(pm); 
        try {
            Method getContractAmountMethod = getContractAmountMethods.get(script);
            if(getContractAmountMethod == null) {
                Class<?> c = new ClassBodyEvaluator(script).getClazz();
                getContractAmountMethod = c.getMethod(
                    "getContractAmounts", 
                    new Class[] {
                        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                        org.opencrx.kernel.contract1.jmi1.CalculationRule.class, 
                        org.opencrx.kernel.contract1.jmi1.AbstractContract.class,
                        Integer[].class,
                        BigDecimal[].class,
                        BigDecimal[].class,
                        BigDecimal[].class,
                        BigDecimal[].class,
                        BigDecimal[].class,
                        Boolean[].class
                    }
                );
                getContractAmountMethods.put(
                    script,
                    getContractAmountMethod                        
                );
            }
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
        catch(CompileException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Can not compile getContractAmountsScript:\n" +
                e.getMessage(),
                null, null, null, null, null, null
            );
        }
        catch(Parser.ParseException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Can not parse getContractAmounts:\n" +
                e.getMessage(),
                null, null, null, null, null, null
            );
        }
        catch(Scanner.ScanException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Can not scan getContractAmounts:\n" +
                e.getMessage(),
                null, null, null, null, null, null
            );
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
                "Can not invoke getContractAmounts:\n" +
                e.getTargetException().getMessage(),
                null, null, null, null, null, null
            );
        }
        catch(IllegalAccessException e) {
            return contractPkg.createGetContractAmountsResult(
                STATUS_CODE_ERROR,
                "Illegal access when invoking getContractAmounts:\n" +
                e.getMessage(),
                null, null, null, null, null, null
            );
        }
    }

    //-------------------------------------------------------------------------
    public BigDecimal[] calculateAmounts(
    	AbstractContractPosition position
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
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		position.refGetPath().getPrefix(5)
        	);
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract =
        	(org.opencrx.kernel.contract1.jmi1.AbstractContract)pm.getObjectById(
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
    	AbstractContractPosition position
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
    	AbstractContractPosition position
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
    	AbstractContractPosition position
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
    	AbstractContractPosition position
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
    	AbstractContractPosition position
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
    public List<AbstractContractPosition> getPositions(
    	AbstractContract contract
    ) {
        List<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
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
        return positions;    	
    }
    
    //-------------------------------------------------------------------------
    public BigDecimal[] calculateAmounts(
        AbstractContract contract
    ) throws ServiceException {        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
    	org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
    		(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
    			contract.refGetPath().getPrefix(5)
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
        	totalDiscountAmount = BigDecimal.ZERO;
        	totalTaxAmount = BigDecimal.ZERO;
        	totalAmount = totalBaseAmount;        	
        	totalAmountIncludingTax = totalBaseAmount;
        	totalSalesCommission = BigDecimal.ZERO;
        }
        else {
        	List<AbstractContractPosition> positions = this.getPositions(contract);
	        List<Integer> lineItemNumbers = new ArrayList<Integer>();
	        List<BigDecimal> positionBaseAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionDiscountAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionTaxAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> positionAmounts = new ArrayList<BigDecimal>();
	        List<BigDecimal> salesCommissions = new ArrayList<BigDecimal>();
	        List<Boolean> salesCommissionIsPercentages = new ArrayList<Boolean>();
	        for(AbstractContractPosition position: positions) {
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
    
    //-----------------------------------------------------------------------
    public void updateContract(
    	AbstractContract contract
    ) throws ServiceException {
    	if(JDOHelper.isNew(contract)) {
    		Base.getInstance().assignToMe(
    			contract, 
    			false, 
    			null
    		);
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
        org.opencrx.kernel.contract1.jmi1.SalesOrder salesOrder
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(salesOrder);
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
    				invoice.refInitialize(false, false);
    				Contracts.getInstance().copyAbstractContract(
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
    				invoicePosition.refInitialize(false, false);
    				Contracts.getInstance().copyContractPosition(
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
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		salesOrder.refGetPath().getPrefix(5)
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
                true,
                null
            );
        }        
        return invoice;
    }
    
    //-----------------------------------------------------------------------
    public SalesOrder createSalesOrder(
        org.opencrx.kernel.contract1.jmi1.Quote quote
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(quote);
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
    				salesOrder.refInitialize(false, false);
    				Contracts.getInstance().copyAbstractContract(
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
    				salesOrderPosition.refInitialize(false, false);
    				Contracts.getInstance().copyContractPosition(
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
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		quote.refGetPath().getPrefix(5)
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
                true,
                null
            );
        }
        return salesOrder;
    }
    
    //-----------------------------------------------------------------------
    public Quote createQuote(
        org.opencrx.kernel.contract1.jmi1.Opportunity opportunity
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(opportunity);
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
    				quote.refInitialize(false, false);
    				Contracts.getInstance().copyAbstractContract(
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
    				quotePosition.refInitialize(false, false);
    				Contracts.getInstance().copyContractPosition(
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
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		opportunity.refGetPath().getPrefix(5)
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
                true,
                null
            );
        }
        return quote;
    }
    
    //-----------------------------------------------------------------------
    public Opportunity createOpportunity(
        org.opencrx.kernel.contract1.jmi1.Lead lead        
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(lead);
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
    				opportunity.refInitialize(false, false);
    				Contracts.getInstance().copyAbstractContract(
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
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment =
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		lead.refGetPath().getPrefix(5)
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
                true,
                null
            );
        }
        return opportunity;
    }

    //-------------------------------------------------------------------------
    public void updateListPrice(
    	AbstractContractPosition position,
        AbstractContract contract,
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
        Date pricingDate = position.getPricingDate() != null ?
        	position.getPricingDate() :
        		contract.getPricingDate() != null ?
        			contract.getPricingDate() :
        				contract.getActiveOn() != null ?
        					contract.getActiveOn() :
        						new Date();
        org.opencrx.kernel.uom1.jmi1.Uom priceUom = position.getPriceUom() != null ?
            position.getPriceUom() :
            	position.getUom() != null ?
            		position.getUom() :
            			null;
        AbstractPriceLevel priceLevel = null;
        BigDecimal customerDiscount = null;
        Boolean customerDiscountIsPercentage = null;        
        if(position.getPriceLevel() != null) {
            priceLevel = position.getPriceLevel();
        }
        else {
            org.opencrx.kernel.product1.jmi1.GetPriceLevelResult res = 
                Products.getInstance().getPriceLevel(
                    pricingRule,       
                    contract,
                    product,
                    priceUom,
                    quantity, 
                    pricingDate
                  );            
            priceLevel = res.getPriceLevel() == null ?
            	null :
            	(AbstractPriceLevel)pm.getObjectById(res.getPriceLevel().refGetPath());
            customerDiscount = res.getCustomerDiscount();
            customerDiscountIsPercentage = res.isCustomerDiscountIsPercentage();
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
                }
                else {
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
                        }
                        // listPriceDiscountIsPercentage=true, customerDiscountIsPercentage=false
                        // totalDiscount = price*listPriceDiscount + customerDiscount
                        else {
                            discountIsPercentage = Boolean.FALSE;
                            discount = price.multiply(
                                listPriceDiscount.divide(HUNDRED, BigDecimal.ROUND_DOWN)
                            ).add(customerDiscount);
                        }
                    }
                    else {
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
                        }
                        // listPriceDiscountIsPercentage=false, customerDiscountIsPercentage=false
                        // totalDiscount = listPriceDiscount + customerDiscount
                        else {
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
    
    //-------------------------------------------------------------------------
    public long getMaxLineItemNumber(
        Collection<AbstractContractPosition> positions
    ) throws ServiceException {
        long maxLineItemNumber = 0L;
        for(AbstractContractPosition position: positions) {
            if(position.getLineItemNumber() > maxLineItemNumber) {
                maxLineItemNumber = position.getLineItemNumber();
            }
        }
        return maxLineItemNumber;
    }

    //-------------------------------------------------------------------------
    public void updateContractPosition(
        AbstractContract contract,
        AbstractContractPosition position,
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
                	org.opencrx.kernel.product1.jmi1.Segment productSegment =
                		(org.opencrx.kernel.product1.jmi1.Segment)pm.getObjectById(
                			new Path("xri://@openmdx*org.opencrx.kernel.product1/provider/" + position.refGetPath().get(2) + "/segment/" + position.refGetPath().get(4))
                		);
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
                positionCreation.refInitialize(false, false);
                positionCreation.setInvolved(position);
                contract.addPositionModification(
                	false,
                	this.getUidAsString(),
                	positionCreation
                );            	
            }
            // Update
            else {
            	PersistenceManager pmOld = pm.getPersistenceManagerFactory().getPersistenceManager(
            		SecurityKeys.ROOT_PRINCIPAL,
            		null
            	);
                BigDecimal quantityOld = ((AbstractContractPosition)pmOld.getObjectById(
                	position.refGetPath())
                ).getQuantity();
                BigDecimal quantityNew = position.getQuantity();
                if(quantityOld.compareTo(quantityNew) != 0) {
                	QuantityModification quantityModification = pm.newInstance(QuantityModification.class);
                	quantityModification.refInitialize(false, false);
                	quantityModification.setInvolved(position);
                	quantityModification.setQuantity(quantityOld);
                    contract.addPositionModification(
                    	false,
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
        }
        catch(ServiceException e) {
        	SysLog.info(e.getMessage(), e.getCause());
        }
    }
    
    //-------------------------------------------------------------------------
    public AbstractContractPosition createContractPosition(
        AbstractContract contract,
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
    	AbstractContractPosition position = null;
        try {
        	long maxLineItemNumber = 0;
        	if(contract instanceof Opportunity) {
        		Collection<AbstractOpportunityPosition> c = ((Opportunity)contract).getPosition();
        		Collection<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
        		positions.addAll(c);
        		maxLineItemNumber = this.getMaxLineItemNumber(positions);
        		position = pm.newInstance(OpportunityPosition.class);
        		position.refInitialize(false, false);
        		((Opportunity)contract).addPosition(
        			false,
        			this.getUidAsString(),
        			(OpportunityPosition)position
        		);
        	}
        	else if(contract instanceof Quote) {
        		Collection<AbstractQuotePosition> c = ((Quote)contract).getPosition();
        		Collection<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
        		positions.addAll(c);
        		maxLineItemNumber = this.getMaxLineItemNumber(positions);
        		position = pm.newInstance(QuotePosition.class);
        		position.refInitialize(false, false);
        		((Quote)contract).addPosition(
        			false,
        			this.getUidAsString(),
        			(QuotePosition)position
        		);
        	}
        	else if(contract instanceof SalesOrder) {
        		Collection<AbstractSalesOrderPosition> c = ((SalesOrder)contract).getPosition();
        		Collection<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
        		positions.addAll(c);
        		maxLineItemNumber = this.getMaxLineItemNumber(positions);
        		position = pm.newInstance(SalesOrderPosition.class);
        		position.refInitialize(false, false);
        		((SalesOrder)contract).addPosition(
        			false,
        			this.getUidAsString(),
        			(SalesOrderPosition)position
        		);
        	}
        	else if(contract instanceof Invoice) {
        		Collection<AbstractInvoicePosition> c = ((Invoice)contract).getPosition();
        		Collection<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
        		positions.addAll(c);
        		maxLineItemNumber = this.getMaxLineItemNumber(positions);
        		position = pm.newInstance(InvoicePosition.class);
        		position.refInitialize(false, false);
        		((Invoice)contract).addPosition(
        			false,
        			this.getUidAsString(),
        			(InvoicePosition)position
        		);
        	}
            else {
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
            }
            else {
                position.setName("Position " + position.getLineItemNumber());
            }
            // quantity
            if(quantity != null) {
                position.setQuantity(quantity);
            }
            else {
                position.setQuantity(BigDecimal.ONE);
            }
            // pricingDate
            if(pricingDate != null) {
                position.setPricingDate(pricingDate);
            }
            else {
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
	            this.updateContractPosition(
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
        }
	    catch(Exception e) {
	        new ServiceException(e).log();
	    }
	    return position;
    }
    
    //-------------------------------------------------------------------------
    public void removeContractPosition(
    	AbstractContractPosition position,
        boolean checkForMinPositions,
        boolean preDelete
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
    	AbstractContract contract = (AbstractContract)pm.getObjectById(
			position.refGetPath().getParent().getParent()
		);
        // Make a copy of the removed position
        Marshaller positionMarshaller = new Marshaller() {
            public Object marshal(
            	Object s
            ) throws ServiceException {
            	if(s instanceof AbstractContractPosition) {
            		AbstractContractPosition position = (AbstractContractPosition)s;
            		PersistenceManager pm = JDOHelper.getPersistenceManager(position);
            		RemovedPosition removedPosition = pm.newInstance(RemovedPosition.class);
            		removedPosition.refInitialize(false, false);
            		Contracts.getInstance().copyContractPosition(
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
        positionRemoval.refInitialize(false, false);
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

    //-------------------------------------------------------------------------
    public void removeContract(
        AbstractContract contract,
        boolean preDelete
    ) throws ServiceException {
    }
    
    //-------------------------------------------------------------------------
    /**
     * Remove all pending inventory bookings of contract. Return last
     * final booking or null if no inventory booking is set on the contract
     */
    public CompoundBooking removePendingInventoryBookings(
        AbstractContract contract
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
        AbstractContract contract
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
        List<AbstractContractPosition> allPositions = new ArrayList<AbstractContractPosition>();
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
        for(Iterator<AbstractContractPosition> i = allPositions.iterator(); i.hasNext(); ii++) {
        	AbstractContractPosition position = i.next();
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
        List<AbstractContractPosition> origins = new ArrayList<AbstractContractPosition>();        
        ii = 0;
        for(Iterator<AbstractContractPosition> i = allPositions.iterator(); i.hasNext(); ii++) {
        	AbstractContractPosition position = i.next();            
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
    	AbstractContractPosition position,
    	short pricingState
    ) {
    	if(JDOHelper.isPersistent(position)) {
	    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
	        AbstractContract contract = 
	        	(AbstractContract)pm.getObjectById(
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
    	AbstractContract contract,
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
    
    //-------------------------------------------------------------------------
    public void updateContractPosition(
    	AbstractContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
        AbstractContract contract = (AbstractContract)pm.getObjectById(
            position.refGetPath().getParent().getParent()
        );
        if(position instanceof ConfiguredProduct) {
        	Product product = ((ConfiguredProduct)position).getProduct();
	        if(product != null) {
	            this.updateContractPosition(
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
    public short repriceContractPosition(
        org.opencrx.kernel.contract1.jmi1.AbstractContractPosition position
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(position);
    	AbstractContract contract = (AbstractContract)pm.getObjectById(
			position.refGetPath().getParent().getParent()
		);
    	short pricingState = 0;
        if(position instanceof ConfiguredProduct) {
        	Product product = ((ConfiguredProduct)position).getProduct();
        	if(product != null) {
	            position.setPriceLevel(null);
	            this.updateContractPosition(
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
    
    //-------------------------------------------------------------------------
    public void repriceContract(
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract
    ) throws ServiceException {
        List<AbstractContractPosition> positions = new ArrayList<AbstractContractPosition>();
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
        for(AbstractContractPosition position: positions) {
            short pricingStatePosition = this.repriceContractPosition(
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

    //-------------------------------------------------------------------------
    public int countFilteredContract(
        org.opencrx.kernel.contract1.jmi1.AbstractFilterContract contractFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractFilter);
        AbstractContractQuery query = (AbstractContractQuery)pm.newQuery(AbstractContract.class);
    	org.openmdx.base.query.Extension queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);    	
        List<AbstractContract> contracts = contractFilter.getFilteredContract(query);
        return contracts.size();
    }

    //-------------------------------------------------------------------------
    public static GetPositionAmountsResult getPositionAmounts(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        CalculationRule calculationRule,
        AbstractContractPosition position,
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
        AbstractContractPosition position,
        BigDecimal minMaxAdjustedQuantity,
        BigDecimal uomScaleFactor,
        BigDecimal salesTaxRate
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(calculationRule);
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
        GetPositionAmountsResult result = Utils.getContractPackage(pm).createGetPositionAmountsResult(
            amount, 
            baseAmount, 
            discountAmount,
            (short)0, // statusCode
            null, // statusMessage
            taxAmount
        );
        return result;
    }

    //-------------------------------------------------------------------------
    public static GetContractAmountsResult getContractAmounts(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        CalculationRule calculationRule,
        AbstractContract contract,
        java.lang.Integer[] lineItemNumbers,
        java.math.BigDecimal[] positionBaseAmounts,
        java.math.BigDecimal[] positionDiscountAmounts,
        java.math.BigDecimal[] positionTaxAmounts,
        java.math.BigDecimal[] positionAmounts,
        java.math.BigDecimal[] salesCommissions,
        java.lang.Boolean[] salesCommissionIsPercentages
    ) {
    	javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(contract);
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
        GetContractAmountsResult result = org.opencrx.kernel.utils.Utils.getContractPackage(pm).createGetContractAmountsResult(
            (short)0,
            null,
            totalAmount, 
            totalAmountIncludingTax, 
            totalBaseAmount, 
            totalDiscountAmount, 
            totalSalesCommission, 
            totalTaxAmount
        );
        return result;
    }
    
    //-------------------------------------------------------------------------
    public void assignSalesVolumeBudgets(
    	AbstractContract contract
    ) throws ServiceException {
    	Collection<AbstractContractPosition> positions = this.getPositions(contract);
    	List<SalesVolumeBudget> budgets = this.findSalesVolumeBudgets(contract);
    	for(AbstractContractPosition position: positions) {
    		for(SalesVolumeBudget budget: budgets) {
    			if(!position.getBudget().contains(budget)) {
    				position.getBudget().add(budget);
    			}
    		}
    	}
    }
    
    //-------------------------------------------------------------------------
    public void assignSalesVolumeBudgets(
    	AbstractContractPosition contractPosition
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(contractPosition);
    	AbstractContract contract = (AbstractContract)pm.getObjectById(contractPosition.refGetPath().getParent().getParent());
    	List<SalesVolumeBudget> budgets = this.findSalesVolumeBudgets(contract);    	
		for(SalesVolumeBudget budget: budgets) {
			if(!contractPosition.getBudget().contains(budget)) {
				contractPosition.getBudget().add(budget);
			}
		}
    }

    //-----------------------------------------------------------------------
    protected SalesVolumeBudgetQuery getFindSalesVolumeBudgetsQuery(
    	AbstractContract contract
    ) {		
		PersistenceManager pm = JDOHelper.getPersistenceManager(contract);    	
		SalesVolumeBudgetQuery salesVolumeBudgetQuery = (SalesVolumeBudgetQuery)pm.newQuery(SalesVolumeBudget.class);
		salesVolumeBudgetQuery.forAllDisabled().isFalse();
		salesVolumeBudgetQuery.contractType().equalTo(
			contract instanceof Opportunity ?
				CONTRACT_TYPE_OPPORTUNITY :
					contract instanceof Quote ?
						CONTRACT_TYPE_QUOTE :
							contract instanceof SalesOrder ?
								CONTRACT_TYPE_SALES_ORDER :
									contract instanceof Invoice ?
										CONTRACT_TYPE_INVOICE :
											(short)0
		);
		// Only include budgets which are valid during [activeOn, Min(expiresOn, cancelOn, closedOn))
		Date startingFrom = contract.getActiveOn();
		if(startingFrom != null) {
			salesVolumeBudgetQuery.thereExistsEndingAt().greaterThanOrEqualTo(startingFrom);
		}
		Date endingAt = contract.getExpiresOn();
		if(endingAt == null || (contract.getCancelOn() != null && contract.getCancelOn().compareTo(endingAt) < 0)) {
			endingAt = contract.getCancelOn();
		}
		if(endingAt == null || (contract.getClosedOn() != null && contract.getClosedOn().compareTo(endingAt) < 0)) {
			endingAt = contract.getClosedOn();
		}
		if(endingAt != null) {
			salesVolumeBudgetQuery.thereExistsStartingFrom().lessThanOrEqualTo(endingAt);			
		}
		return salesVolumeBudgetQuery;
    }
    
    //-----------------------------------------------------------------------
	public List<SalesVolumeBudget> findSalesVolumeBudgets(
		AbstractContract contract
	) throws ServiceException  {
		PersistenceManager pm = JDOHelper.getPersistenceManager(contract);
		String providerName = contract.refGetPath().get(2);
		String segmentName = contract.refGetPath().get(4);
		org.opencrx.kernel.forecast1.jmi1.Segment forecastSegment = 
			(org.opencrx.kernel.forecast1.jmi1.Segment)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.forecast1").getDescendant("provider", providerName, "segment", segmentName)
			);
		Set<SalesVolumeBudget> budgets = new HashSet<SalesVolumeBudget>();
		// 1. Query by Customer
		if(contract.getCustomer() != null) {
			SalesVolumeBudgetQuery salesVolumeBudgetQuery = this.getFindSalesVolumeBudgetsQuery(contract);
			salesVolumeBudgetQuery.thereExistsCustomer().thereExistsAccount().equalTo(contract.getCustomer());
			List<SalesVolumeBudget> candidateBudgets = forecastSegment.getBudget(salesVolumeBudgetQuery);
			for(SalesVolumeBudget candidateBudget: candidateBudgets) {
				boolean matches = true;
				// Match SalesRep
				if(!candidateBudget.getSalesRep().isEmpty()) {
					if(contract.getSalesRep() != null) {
						SalesRepAssignmentQuery query = (SalesRepAssignmentQuery)pm.newQuery(SalesRepAssignment.class);
						query.thereExistsAccount().equalTo(contract.getSalesRep());
						matches = !candidateBudget.getSalesRep(query).isEmpty();
					}
				}
				// Match Broker
				if(matches) {
					if(!candidateBudget.getBroker().isEmpty()) {
						if(contract.getBroker() != null) {
							BrokerAssignmentQuery query = (BrokerAssignmentQuery)pm.newQuery(BrokerAssignment.class);
							query.thereExistsAccount().equalTo(contract.getBroker());
							matches = !candidateBudget.getBroker(query).isEmpty();
						}
					}
				}
				if(matches) {
					budgets.add(candidateBudget);
				}
			}
		}
		// 2. Query by SalesRep
		if(contract.getSalesRep() != null) {
			SalesVolumeBudgetQuery salesVolumeBudgetQuery = this.getFindSalesVolumeBudgetsQuery(contract);
			salesVolumeBudgetQuery.thereExistsSalesRep().thereExistsAccount().equalTo(contract.getSalesRep());
			List<SalesVolumeBudget> candidateBudgets = forecastSegment.getBudget(salesVolumeBudgetQuery);
			for(SalesVolumeBudget candidateBudget: candidateBudgets) {
				boolean matches = true;
				// Match Customer
				if(!candidateBudget.getCustomer().isEmpty()) {
					if(contract.getCustomer() != null) {
						CustomerAssignmentQuery query = (CustomerAssignmentQuery)pm.newQuery(CustomerAssignment.class);
						query.thereExistsAccount().equalTo(contract.getCustomer());
						matches = !candidateBudget.getCustomer(query).isEmpty();
					}
				}
				// Match Broker
				if(matches) {
					if(!candidateBudget.getBroker().isEmpty()) {
						if(contract.getBroker() != null) {
							BrokerAssignmentQuery query = (BrokerAssignmentQuery)pm.newQuery(BrokerAssignment.class);
							query.thereExistsAccount().equalTo(contract.getBroker());
							matches = !candidateBudget.getBroker(query).isEmpty();
						}
					}
				}
				if(matches) {
					budgets.add(candidateBudget);
				}
			}
		}
		// 3. Query by Broker
		if(contract.getBroker() != null) {
			SalesVolumeBudgetQuery salesVolumeBudgetQuery = this.getFindSalesVolumeBudgetsQuery(contract);
			salesVolumeBudgetQuery.thereExistsBroker().thereExistsAccount().equalTo(contract.getBroker());
			List<SalesVolumeBudget> candidateBudgets = forecastSegment.getBudget(salesVolumeBudgetQuery);
			for(SalesVolumeBudget candidateBudget: candidateBudgets) {
				boolean matches = true;
				// Match Customer
				if(!candidateBudget.getCustomer().isEmpty()) {
					if(contract.getCustomer() != null) {
						CustomerAssignmentQuery query = (CustomerAssignmentQuery)pm.newQuery(CustomerAssignment.class);
						query.thereExistsAccount().equalTo(contract.getCustomer());
						matches = !candidateBudget.getCustomer(query).isEmpty();
					}
				}
				// Match SalesRep
				if(!candidateBudget.getSalesRep().isEmpty()) {
					if(contract.getSalesRep() != null) {
						SalesRepAssignmentQuery query = (SalesRepAssignmentQuery)pm.newQuery(SalesRepAssignment.class);
						query.thereExistsAccount().equalTo(contract.getSalesRep());
						matches = !candidateBudget.getSalesRep(query).isEmpty();
					}
				}
				if(matches) {
					budgets.add(candidateBudget);
				}
			}
		}
		return new ArrayList<SalesVolumeBudget>(budgets);
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
        
    // Contract types
    public static final short CONTRACT_TYPE_OPPORTUNITY = 1;
    public static final short CONTRACT_TYPE_QUOTE = 2;
    public static final short CONTRACT_TYPE_SALES_ORDER = 3;
    public static final short CONTRACT_TYPE_INVOICE = 4;
    
    public static final BigDecimal HUNDRED = new BigDecimal("100.0");
    
    public static final String DEFAULT_GET_POSITION_AMOUNTS_SCRIPT = 
        "//<pre>\n" + 
        "public static org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult getPositionAmounts(\n" + 
        "    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" +
        "    org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,\n" +  
        "    org.opencrx.kernel.contract1.jmi1.AbstractContractPosition position,\n" +
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
        "    org.opencrx.kernel.contract1.jmi1.AbstractContract contract,\n" +
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
    protected static final Map<String,Method> getContractAmountMethods = new HashMap<String,Method>();
    protected static final Map<String,Method> getPositionAmountMethods = new HashMap<String,Method>();
        
}

//--- End of File -----------------------------------------------------------

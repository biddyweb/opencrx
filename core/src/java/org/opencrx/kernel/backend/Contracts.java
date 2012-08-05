/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Contracts.java,v 1.37 2008/10/14 08:07:28 wfro Exp $
 * Description: Contracts
 * Revision:    $Revision: 1.37 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/14 08:07:28 $
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.opencrx.kernel.contract1.jmi1.CalculationRule;
import org.opencrx.kernel.contract1.jmi1.Contract1Package;
import org.opencrx.kernel.contract1.jmi1.ContractPosition;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.Orders;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.compatibility.base.marshalling.Marshaller;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.w3c.cci2.Datatypes;

public class Contracts {

    //-----------------------------------------------------------------------
    public Contracts(
        Backend backend
    ) {
        this.backend = backend;
        this.cloning = new Cloneable(           
            backend
        );
    }

    //-------------------------------------------------------------------------
    public static org.opencrx.kernel.contract1.jmi1.CalculationRule findCalculationRule(
        String name,
        org.opencrx.kernel.contract1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = org.opencrx.kernel.utils.Utils.getContractPackage(pm);
        org.opencrx.kernel.contract1.cci2.CalculationRuleQuery calculationRuleQuery = contractPkg.createCalculationRuleQuery();
        calculationRuleQuery.name().equalTo(name);
        List<org.opencrx.kernel.contract1.jmi1.CalculationRule> calculationRules = segment.getCalculationRule(calculationRuleQuery);
        return calculationRules.isEmpty()
            ? null
            : calculationRules.iterator().next();
    }
    
    //-----------------------------------------------------------------------
    /**
     * @return Returns the contract segment.
     */
    public static org.opencrx.kernel.contract1.jmi1.Segment getContractSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
            "xri:@openmdx:org.opencrx.kernel.contract1/provider/"
            + providerName + "/segment/" + segmentName
        );
    }

    //-----------------------------------------------------------------------
    public static CalculationRule initCalculationRule(
        String calculationRuleName,
        String description,
        String getPositionAmountsScript,
        String getContractAmountsScript,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        Contract1Package contractPkg = Utils.getContractPackage(pm);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getContractSegment(
            pm, 
            providerName, 
            segmentName
        );
        CalculationRule calculationRule = null;
        if((calculationRule = findCalculationRule(calculationRuleName, contractSegment, pm)) != null) {
            return calculationRule;            
        }                
        pm.currentTransaction().begin();
        calculationRule = contractPkg.getCalculationRule().createCalculationRule();
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
    static void copyAbstractContract(
        DataproviderObject_1_0 from,
        DataproviderObject to
    ) throws ServiceException {
        // Contract
        to.clearValues("name").addAll(from.values("name"));
        to.clearValues("description").addAll(from.values("description"));
        to.clearValues("priority").addAll(from.values("priority"));
        to.clearValues("activeOn").addAll(from.values("activeOn"));
        to.clearValues("expiresOn").addAll(from.values("expiresOn"));
        to.clearValues("cancelOn").addAll(from.values("cancelOn"));
        to.clearValues("closedOn").addAll(from.values("closedOn"));
        to.clearValues("contractNumber").addAll(from.values("contractNumber"));
        to.clearValues("contractCurrency").addAll(from.values("contractCurrency"));
        to.clearValues("paymentTerms").addAll(from.values("paymentTerms"));
        to.clearValues("contractLanguage").addAll(from.values("contractLanguage"));
        to.clearValues("contractState").addAll(from.values("contractState"));
        to.clearValues("pricingDate").addAll(from.values("pricingDate"));
        to.clearValues("competitor").addAll(from.values("competitor"));
        to.clearValues("contact").addAll(from.values("contact"));
        to.clearValues("broker").addAll(from.values("broker"));
        to.clearValues("customer").addAll(from.values("customer"));
        to.clearValues("salesRep").addAll(from.values("salesRep"));
        to.clearValues("supplier").addAll(from.values("supplier"));
        to.clearValues("activity").addAll(from.values("activity"));
        to.clearValues("origin").addAll(from.values("origin"));
        to.clearValues("inventoryCb").addAll(from.values("inventoryCb"));
        to.clearValues("pricingRule").addAll(from.values("pricingRule"));
        to.clearValues("calcRule").addAll(from.values("calcRule"));
        // ShippingDetail
        to.clearValues("shippingMethod").addAll(from.values("shippingMethod"));
        to.clearValues("shippingTrackingNumber").addAll(from.values("shippingTrackingNumber"));
        to.clearValues("shippingInstructions").addAll(from.values("shippingInstructions"));
        to.clearValues("isGift").addAll(from.values("isGift"));
        to.clearValues("giftMessage").addAll(from.values("giftMessage"));
        // SecureObject
        to.clearValues("owningGroup").addAll(from.values("owningGroup"));
        to.clearValues("owningUser").addAll(from.values("owningUser"));
        // CrxObject
        to.clearValues("disabled").addAll(from.values("disabled"));
        to.clearValues("disabledReason").addAll(from.values("disabledReason"));
        to.clearValues("externalLink").addAll(from.values("externalLink"));
        to.clearValues("category").addAll(from.values("category"));
        for(int i = 0; i < 5; i++) {
            to.clearValues("userBoolean" + i).addAll(from.values("userBoolean" + i));
            to.clearValues("userNumber" + i).addAll(from.values("userNumber" + i));
            to.clearValues("userString" + i).addAll(from.values("userString" + i));
            to.clearValues("userDateTime" + i).addAll(from.values("userDateTime" + i));
            to.clearValues("userDate" + i).addAll(from.values("userDate" + i));
            to.clearValues("userCode" + i).addAll(from.values("userCode" + i));            
        }        
    }

    //-------------------------------------------------------------------------
    static void copyAbstractContractPosition(
        DataproviderObject_1_0 from,
        DataproviderObject to
    ) throws ServiceException {
        to.addClones(from, true);
    }

    //-------------------------------------------------------------------------
    private DataproviderObject_1_0 getCachedDescription(
        Path path,
        int contractLanguage
    ) throws ServiceException {
        String key = path + ":" + contractLanguage;
        DataproviderObject_1_0 description = (DataproviderObject_1_0)this.cachedDescriptions.get(key);
        if((description == null) && !this.cachedDescriptions.containsKey(key)) {
            description = this.backend.getBase().getAdditionalDescription(
                path,
                contractLanguage
            );
            this.cachedDescriptions.put(
                key,
                description
            );
        }
        return description;          
    }
    
    //-------------------------------------------------------------------------
    private DataproviderObject_1_0 getCachedObject(
        Path path
    ) throws ServiceException {
        DataproviderObject_1_0 object = (DataproviderObject_1_0)this.cachedObjects.get(path);
        if((object == null) && !this.cachedObjects.containsKey(path)) {
            object = this.backend.retrieveObject(
                path
            );
            this.cachedObjects.put(
                object.path(),
                object
            );
        }
        return object;
    }
    
    //-------------------------------------------------------------------------
    public void markAsClosed(
        Path contractIdentity,
        short newContractState
    ) throws ServiceException {
        DataproviderObject contract = this.backend.retrieveObjectForModification(
            contractIdentity
        );
        String contractType = (String)contract.values(SystemAttributes.OBJECT_CLASS).get(0);
        contract.clearValues("closedOn").add(
            org.openmdx.base.text.format.DateFormat.getInstance().format(new Date())
        );        
        contract.clearValues("contractState").add(
            new Short(newContractState)
        );
    }
        
    //-------------------------------------------------------------------------
    private BigDecimal getUomScaleFactor(
        org.opencrx.kernel.uom1.jmi1.Uom from,
        org.opencrx.kernel.uom1.jmi1.Uom to
    ) {
        if(from == null || to == null) {
            return new BigDecimal(0.0);
        }
        else if(from.refMofId().equals(to.refMofId())) {
            return new BigDecimal(1.0);
        }
        else if(
            (from.getBaseUom() != null) && 
            from.getBaseUom().refMofId().equals(to.refMofId())
        ) {
            return from.getQuantity() != null 
                ? from.getQuantity() 
                : new BigDecimal(0.0);
        }
        else if(
            (to.getBaseUom() != null) && 
            to.getBaseUom().refMofId().equals(from.refMofId())
        ) {
            return (to.getQuantity() != null) && (to.getQuantity().signum() != 0) 
                ? new BigDecimal((double)1.0 / to.getQuantity().doubleValue()) 
                : new BigDecimal(0.0);
        }
        else if(
            (from.getBaseUom() != null) && 
            (to.getBaseUom() != null) && 
            from.getBaseUom().refMofId().equals(to.getBaseUom().refMofId())
        ) {
            return (from.getQuantity() != null) && (to.getQuantity() != null) && (to.getQuantity().signum() != 0) 
                ? new BigDecimal(from.getQuantity().doubleValue() / to.getQuantity().doubleValue()) 
                : new BigDecimal(0.0);
        }
        else {
            return new BigDecimal(0.0);
        }
    }
  
    //-------------------------------------------------------------------------
    private BigDecimal getUomScaleFactor(
        org.opencrx.kernel.contract1.jmi1.ContractPosition position
    ) {
        return (position.getUom() != null) && (position.getPriceUom() != null)
            ? this.getUomScaleFactor(position.getUom(), position.getPriceUom())
            : new BigDecimal(1.0);
    }

    //-------------------------------------------------------------------------
    private BigDecimal getSalesTaxRate(
        org.opencrx.kernel.contract1.jmi1.ContractPosition position
    ) {
        BigDecimal salesTaxRate = new BigDecimal(0);
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
    private BigDecimal getMinMaxAdjustedQuantity(
        org.opencrx.kernel.contract1.jmi1.ContractPosition position
    ) {
        // quantity
        BigDecimal quantity = position.getQuantity() != null 
            ? position.getQuantity()
            : new BigDecimal(0);
        BigDecimal minMaxAdjustedQuantity = quantity;
        BigDecimal minQuantity = position.getMinQuantity() != null 
            ? position.getMinQuantity()
            : new BigDecimal(0);
        BigDecimal maxQuantity = position.getMaxQuantity() != null 
            ? position.getMaxQuantity()
            : new BigDecimal(Double.MAX_VALUE);
        BigDecimal offsetQuantity = position.getOffsetQuantity() != null 
            ? position.getOffsetQuantity()
            : new BigDecimal(0);
        short minMaxQuantityHandling = MIN_MAX_QUANTITY_HANDLING_NA;
        try {
            minMaxQuantityHandling = position.getMinMaxQuantityHandling(); 
        } catch(Exception e) {}
        if(minMaxQuantityHandling == MIN_MAX_QUANTITY_HANDLING_LIMIT) {
            // Adjust min/max handling when quantity is negative
            if(quantity.compareTo(new BigDecimal(0.0)) < 0) {
                minMaxAdjustedQuantity = minMaxAdjustedQuantity.add(offsetQuantity).max(maxQuantity.negate()).min(minQuantity.negate());              
            }
            else {
                minMaxAdjustedQuantity = minMaxAdjustedQuantity.subtract(offsetQuantity).max(minQuantity).min(maxQuantity);
            }
        }   
        return minMaxAdjustedQuantity;
    }
    
    //-------------------------------------------------------------------------
    public void getPositionAmounts(
        DataproviderObject_1_0 calcRule,
        DataproviderObject_1_0 params,
        DataproviderObject getPositionAmountsResult        
    ) throws ServiceException {
        try {
            org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult res = 
                this.getPositionAmounts(
                    (org.opencrx.kernel.contract1.jmi1.CalculationRule)this.backend.getDelegatingPkg().refObject(
                        calcRule.path().toXri()
                    ),
                    params.values("position").isEmpty()
                        ? null
                        : (org.opencrx.kernel.contract1.jmi1.ContractPosition)this.backend.getDelegatingPkg().refObject(((Path)params.values("position").get(0)).toXri())                    
                );
            getPositionAmountsResult.values("statusCode").add(
                new Short(STATUS_CODE_OK)
            );
            if(res != null) {                                
                if(res.getBaseAmount() != null) {
                    getPositionAmountsResult.values("baseAmount").add(
                        res.getBaseAmount()
                    );
                }
                if(res.getDiscountAmount() != null) {
                    getPositionAmountsResult.values("discountAmount").add(
                        res.getDiscountAmount()
                    );
                }
                if(res.getTaxAmount() != null) {
                    getPositionAmountsResult.values("taxAmount").add(
                        res.getTaxAmount()
                    );
                }
                if(res.getAmount() != null) {
                    getPositionAmountsResult.values("amount").add(
                        res.getAmount()
                    );
                }
                getPositionAmountsResult.clearValues("statusCode").add(
                    new Short(res.getStatusCode())
                );
                if(res.getStatusMessage() != null) {
                    getPositionAmountsResult.values("statusMessage").add(
                        res.getStatusMessage()
                    );
                }
            }
        }
        finally {
        }
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult getPositionAmounts(
        org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,
        org.opencrx.kernel.contract1.jmi1.ContractPosition position
    ) throws ServiceException {
        String script = (calculationRule.getGetPositionAmountsScript() == null) || (calculationRule.getGetPositionAmountsScript().length() == 0)
            ? DEFAULT_GET_POSITION_AMOUNTS_SCRIPT
            : calculationRule.getGetPositionAmountsScript();
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = 
            (org.opencrx.kernel.contract1.jmi1.Contract1Package)this.backend.getDelegatingPkg().refPackage(
                org.opencrx.kernel.contract1.jmi1.Contract1Package.class.getName()
            );
        try {
            Class c = new ClassBodyEvaluator(script).evaluate();
            Method m = c.getMethod(
                "getPositionAmounts", 
                new Class[] {
                    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0.class,
                    org.opencrx.kernel.contract1.jmi1.CalculationRule.class, 
                    org.opencrx.kernel.contract1.jmi1.ContractPosition.class,
                    BigDecimal.class, // minMaxAdjustedQuantity
                    BigDecimal.class, // uomScaleFactor
                    BigDecimal.class // salesTaxRate
                }
            );
            org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult result = 
                (org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult)m.invoke(
                    null, 
                    new Object[] {
                        this.backend.getDelegatingPkg(),
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
    public void getContractAmounts(
        DataproviderObject_1_0 calcRule,
        DataproviderObject_1_0 params,
        DataproviderObject getContractAmountsResult        
    ) throws ServiceException {
        try {
            org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult res = 
                this.getContractAmounts(
                    (org.opencrx.kernel.contract1.jmi1.CalculationRule)this.backend.getDelegatingPkg().refObject(
                        calcRule.path().toXri()
                    ),
                    params.values("contract").isEmpty()
                        ? null
                        : (org.opencrx.kernel.contract1.jmi1.AbstractContract)this.backend.getDelegatingPkg().refObject(((Path)params.values("contract").get(0)).toXri()),
                    params.values("lineItemNumber"),
                    params.values("positionBaseAmount"),
                    params.values("positionDiscountAmount"),
                    params.values("positionTaxAmount"),
                    params.values("positionAmount"),
                    params.values("salesCommission"),
                    params.values("salesCommissionIsPercentage")                        
                );
            getContractAmountsResult.values("statusCode").add(
                new Short(STATUS_CODE_OK)
            );
            if(res != null) {                                
                if(res.getTotalBaseAmount() != null) {
                    getContractAmountsResult.values("totalBaseAmount").add(
                        res.getTotalBaseAmount()
                    );
                }
                if(res.getTotalDiscountAmount() != null) {
                    getContractAmountsResult.values("totalDiscountAmount").add(
                        res.getTotalDiscountAmount()
                    );
                }
                if(res.getTotalTaxAmount() != null) {
                    getContractAmountsResult.values("totalTaxAmount").add(
                        res.getTotalTaxAmount()
                    );
                }
                if(res.getTotalAmount() != null) {
                    getContractAmountsResult.values("totalAmount").add(
                        res.getTotalAmount()
                    );
                }
                if(res.getTotalAmountIncludingTax() != null) {
                    getContractAmountsResult.values("totalAmountIncludingTax").add(
                        res.getTotalAmountIncludingTax()
                    );
                }
                if(res.getTotalSalesCommission() != null) {
                    getContractAmountsResult.values("totalSalesCommission").add(
                        res.getTotalSalesCommission()
                    );
                }
                getContractAmountsResult.clearValues("statusCode").add(
                    new Short(res.getStatusCode())
                );
                if(res.getStatusMessage() != null) {
                    getContractAmountsResult.values("statusMessage").add(
                        res.getStatusMessage()
                    );
                }
            }
        }
        finally {
        }
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult getContractAmounts(
        org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract,
        List lineItemNumbers,
        List positionBaseAmounts,
        List positionDiscountAmounts,
        List positionTaxAmounts,
        List positionAmounts,
        List salesCommissions,
        List salesCommissionIsPercentages
    ) throws ServiceException {
        String script = (calculationRule.getGetContractAmountsScript() == null) || (calculationRule.getGetContractAmountsScript().length() == 0)
            ? DEFAULT_GET_CONTRACT_AMOUNTS_SCRIPT
            : calculationRule.getGetContractAmountsScript();
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg = 
            (org.opencrx.kernel.contract1.jmi1.Contract1Package)this.backend.getDelegatingPkg().refPackage(
                org.opencrx.kernel.contract1.jmi1.Contract1Package.class.getName()
            );
        try {
            Class c = new ClassBodyEvaluator(script).evaluate();
            Method m = c.getMethod(
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
            org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult result = 
                (org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult)m.invoke(
                    null, 
                    new Object[] {
                        this.backend.getDelegatingPkg(),
                        calculationRule,
                        contract,
                        (Integer[])lineItemNumbers.toArray(new Integer[lineItemNumbers.size()]),
                        (BigDecimal[])positionBaseAmounts.toArray(new BigDecimal[positionBaseAmounts.size()]),
                        (BigDecimal[])positionDiscountAmounts.toArray(new BigDecimal[positionDiscountAmounts.size()]),
                        (BigDecimal[])positionTaxAmounts.toArray(new BigDecimal[positionTaxAmounts.size()]),
                        (BigDecimal[])positionAmounts.toArray(new BigDecimal[positionAmounts.size()]),
                        (BigDecimal[])salesCommissions.toArray(new BigDecimal[salesCommissions.size()]),
                        (Boolean[])salesCommissionIsPercentages.toArray(new Boolean[salesCommissionIsPercentages.size()])
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
    public void calculateContractPosition(
        DataproviderObject_1_0 position,
        Set fetchSet
    ) throws ServiceException {
      
      // baseAmount, discountAmount         
      BigDecimal baseAmount = null;
      BigDecimal discountAmount = null;
      BigDecimal taxAmount = null;
      BigDecimal amount = null;
      BigDecimal pricePerUnit = position.values("pricePerUnit").isEmpty() 
          ? new BigDecimal(0)
          : (BigDecimal)position.values("pricePerUnit").get(0);
          
      // Get all positions in one roundtrip. Read up to the last element
      org.opencrx.kernel.contract1.jmi1.ContractPosition p = 
          (org.opencrx.kernel.contract1.jmi1.ContractPosition)this.backend.getDelegatingPkg().refObject(
              position.path().toXri()
          );

      BigDecimal minMaxAdjustedQuantity = this.getMinMaxAdjustedQuantity(p);
      // Do amount calculation using amount calculation rule
      if(!position.values("calcRule").isEmpty()) {
          DataproviderObject_1_0 params = new DataproviderObject(new Path(""));
          params.values("position").add(position.path());
          DataproviderObject result = new DataproviderObject(new Path(""));
          this.getPositionAmounts(
              this.backend.retrieveObjectFromDelegation(
                  (Path)position.values("calcRule").get(0)
              ),
              params,
              result
          );
          baseAmount = (BigDecimal)result.values("baseAmount").get(0);
          discountAmount = (BigDecimal)result.values("discountAmount").get(0);   
          taxAmount = (BigDecimal)result.values("taxAmount").get(0);
          amount = (BigDecimal)result.values("amount").get(0);   
      }
      // No calculation rule. Fall back to default amount calculation
      else {
          BigDecimal uomScaleFactor = this.getUomScaleFactor(p);
          BigDecimal salesTaxRate = this.getSalesTaxRate(p);
              
          baseAmount = minMaxAdjustedQuantity.multiply(pricePerUnit.multiply(uomScaleFactor));
          // discount
          Boolean discountIsPercentage = position.values("discountIsPercentage").isEmpty() 
              ? Boolean.FALSE
              : (Boolean)position.values("discountIsPercentage").get(0);
          BigDecimal discount = position.values("discount").isEmpty() 
              ? new BigDecimal(0)
              : (BigDecimal)position.values("discount").get(0);
          // Discount is per piece in case of !discountIsPercentage
          discountAmount = discountIsPercentage.booleanValue()
              ? baseAmount.multiply(discount.divide(new BigDecimal(100.0), BigDecimal.ROUND_UP))
              : minMaxAdjustedQuantity.multiply(discount.multiply(uomScaleFactor));
          // taxAmount
          taxAmount = baseAmount.subtract(discountAmount).multiply(
              salesTaxRate.divide(new BigDecimal(100), BigDecimal.ROUND_UP)
          );    
          // amount
          amount = baseAmount.subtract(discountAmount).add(taxAmount);      
      }        
      position.clearValues("baseAmount").add(baseAmount);
      position.clearValues("discountAmount").add(discountAmount);
      position.clearValues("taxAmount").add(taxAmount);
      position.clearValues("amount").add(amount);
      // quantityShipped, quantityBackOrdered
      if(
          (fetchSet == null)  ||
          fetchSet.contains("quantityShipped") ||
          fetchSet.contains("quantityBackOrdered")
      ) {
          // Retrieve DeliveryInformation and calculate quantity shipped
          List deliveryInformations = this.backend.getDelegatingRequests().addFindRequest(
              position.path().getChild("deliveryInformation"),
              null,
              AttributeSelectors.ALL_ATTRIBUTES,
              0,
              Integer.MAX_VALUE,
              Directions.ASCENDING
          );
          BigDecimal quantityShipped = new BigDecimal(0);
          for(
              Iterator i = deliveryInformations.iterator();
              i.hasNext();
          ) {
            DataproviderObject deliveryInformation = (DataproviderObject)i.next();
            quantityShipped = quantityShipped.add(
                (BigDecimal)deliveryInformation.values("quantityShipped").get(0)
            );
          }
          position.clearValues("quantityShipped").add(quantityShipped);
          position.clearValues("quantityBackOrdered").add(
              minMaxAdjustedQuantity.subtract(quantityShipped)
          );
      }
      
      // get contract language and calculate descriptions
      Set derivedAttributes = new HashSet(
          Arrays.asList(new String[]{
              "productDescription", "productDetailedDescription", 
              "uomDescription", "uomDetailedDescription", 
              "priceUomDescription", "priceUomDetailedDescription",
              "salesTaxTypeDescription", "salesTaxTypeDetailedDescription"
          })    
      );
      if((fetchSet == null) || derivedAttributes.removeAll(fetchSet)) {
          DataproviderObject_1_0 contract = this.backend.retrieveObject(
              position.path().getPrefix(position.path().size()-2)
          );
          int contractLanguage = contract.values("contractLanguage").size() < 1
            ? 0
            : ((Number)contract.values("contractLanguage").get(0)).intValue();      
          // get product description
          if(!position.values("product").isEmpty()) {
            try {
              DataproviderObject_1_0 product = this.backend.getDelegatingRequests().addGetRequest(
                (Path)position.values("product").get(0),
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}            
              );
              position.clearValues("productDescription").add(
                product.values("description").get(0)
              );
              position.clearValues("productDetailedDescription").add(
                product.values("detailedDescription").get(0)
              );
              DataproviderObject_1_0 additionalDescription = this.getCachedDescription(
                  product.path(),
                  contractLanguage
              );
              if(additionalDescription != null) {
                position.clearValues("productDescription").add(
                  additionalDescription.values("description").get(0)
                );
                position.clearValues("productDetailedDescription").add(
                  additionalDescription.values("detailedDescription").get(0)
                );            
              }
            }
            catch(Exception e) {
              new ServiceException(e).log();
              position.clearValues("productDescription").add("#ERR");
              position.clearValues("productDetailedDescription").add("#ERR");  
            }
          }
          else {
            position.clearValues("productDescription").add("N/A");
            position.clearValues("productDetailedDescription").add("N/A");
          }
          
          // Complete uom derived attributes
          if(!position.values("uom").isEmpty()) {
            try {
              DataproviderObject_1_0 uom = this.getCachedObject(
                  (Path)position.values("uom").get(0)
              );
              position.clearValues("uomDescription").add(
                uom.values("description").get(0)
              );
              position.clearValues("uomDetailedDescription").add(
                uom.values("detailedDescription").get(0)
              );
              DataproviderObject_1_0 additionalDescription = this.getCachedDescription(
                  uom.path(),
                  contractLanguage
              );
              if(additionalDescription != null) {
                position.clearValues("uomDescription").add(
                  additionalDescription.values("description").get(0)
                );
                position.clearValues("uomDetailedDescription").add(
                  additionalDescription.values("detailedDescription").get(0)
                );            
              }
            }
            catch(Exception e) {
              new ServiceException(e).log();
              position.clearValues("uomDescription").add("#ERR");
              position.clearValues("uomDetailedDescription").add("#ERR");         
            }
          }
          else {
            position.clearValues("uomDescription").add("N/A");
            position.clearValues("uomDetailedDescription").add("N/A");
          }
      
          // Complete priceUom derived attributes
          if(!position.values("priceUom").isEmpty()) {
              try {
                DataproviderObject_1_0 priceUom = this.getCachedObject(
                    (Path)position.values("priceUom").get(0)
                );
                position.clearValues("priceUomDescription").add(
                    priceUom.values("description").get(0)
                );
                position.clearValues("priceUomDetailedDescription").add(
                    priceUom.values("detailedDescription").get(0)
                );
                DataproviderObject_1_0 additionalDescription = this.getCachedDescription(
                    priceUom.path(),
                    contractLanguage
                );
                if(additionalDescription != null) {
                  position.clearValues("priceUomDescription").add(
                      additionalDescription.values("description").get(0)
                  );
                  position.clearValues("priceUomDetailedDescription").add(
                      additionalDescription.values("detailedDescription").get(0)
                  );            
                }
              }
              catch(Exception e) {
                  new ServiceException(e).log();
                  position.clearValues("priceUomDescription").add("#ERR");
                  position.clearValues("priceUomDetailedDescription").add("#ERR");         
              }
          }
          else {
              position.clearValues("priceUomDescription").add("N/A");
              position.clearValues("priceUomDetailedDescription").add("N/A");
          }
      
          // Complete salesTaxType derived attributes
          if(!position.values("salesTaxType").isEmpty()) {
            try {
              DataproviderObject_1_0 salesTaxType = this.getCachedObject(
                  (Path)position.values("salesTaxType").get(0)
              );
              position.clearValues("salesTaxTypeDescription").add(
                salesTaxType.values("description").get(0)
              );
              position.clearValues("salesTaxTypeDetailedDescription").add(
                salesTaxType.values("detailedDescription").get(0)
              );
              DataproviderObject_1_0 additionalDescription = this.getCachedDescription(
                  salesTaxType.path(),
                  contractLanguage
              );
              if(additionalDescription != null) {
                position.clearValues("salesTaxTypeDescription").add(
                  additionalDescription.values("description").get(0)
                );
                position.clearValues("salesTaxTypeDetailedDescription").add(
                  additionalDescription.values("detailedDescription").get(0)
                );            
              }
            }
            catch(Exception e) {
              new ServiceException(e).log();
              position.clearValues("salesTaxTypeDescription").add("#ERR");
              position.clearValues("salesTaxTypeDetailedDescription").add("#ERR");         
            }
          }
          else {
            position.clearValues("salesTaxTypeDescription").add("N/A");
            position.clearValues("salesTaxTypeDetailedDescription").add("N/A");
          }
      }
    }

    //-------------------------------------------------------------------------
    public void markContractAsDirty(
        Path contractIdentity
    ) throws ServiceException {
        DataproviderObject contract = this.backend.retrieveObjectForModification(
            contractIdentity
        );
        contract.clearValues("totalBaseAmount");
        contract.clearValues("totalDiscountAmount");
        contract.clearValues("totalAmount");
        contract.clearValues("totalTaxAmount");
        contract.clearValues("totalAmountIncludingTax");
        contract.clearValues("totalSalesCommission");        
    }
    
    //-------------------------------------------------------------------------
    public void completeContract(
        DataproviderObject_1_0 contract,
        Set fetchSet
    ) throws ServiceException {        
        if(!this.backend.isLead(contract)) {
            // Recalc if totalBaseAmount is not set. Assert that is set and matches the 
            // access path --> contract exists and is accessed by its composite path
            if(
                !contract.values(SystemAttributes.OBJECT_IDENTITY).isEmpty() &&                        
                contract.path().equals(new Path((String)contract.values(SystemAttributes.OBJECT_IDENTITY).get(0))) &&
                ((contract.getValues("totalBaseAmount") == null) ||
                contract.values("totalBaseAmount").isEmpty())
            ) {
                List positions = this.backend.getDelegatingRequests().addFindRequest(
                    contract.path().getChild("position"),
                    null,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    0,
                    Integer.MAX_VALUE,
                    Directions.ASCENDING
                );
                List lineItemNumbers = new ArrayList();
                List positionBaseAmounts = new ArrayList();
                List positionDiscountAmounts = new ArrayList();
                List positionTaxAmounts = new ArrayList();
                List positionAmounts = new ArrayList();
                List salesCommissions = new ArrayList();
                List salesCommissionIsPercentages = new ArrayList();
                for(
                    Iterator i = positions.iterator();
                    i.hasNext();
                ) {
                    DataproviderObject_1_0 position = (DataproviderObject_1_0)i.next();
                    this.calculateContractPosition(
                        position,
                        fetchSet
                    );
                    lineItemNumbers.add(
                        position.values("lineItemNumber").isEmpty()
                            ? new Integer(0)
                            : new Integer(((Number)position.values("lineItemNumber").get(0)).intValue())
                    );
                    positionBaseAmounts.add(
                        position.values("baseAmount").isEmpty()
                            ? new BigDecimal(0.0)
                            : position.values("baseAmount").get(0)
                    );
                    positionDiscountAmounts.add(
                        position.values("discountAmount").isEmpty()
                            ? new BigDecimal(0.0)
                            : position.values("discountAmount").get(0)
                    );
                    positionTaxAmounts.add(
                        position.values("taxAmount").isEmpty()
                            ? new BigDecimal(0.0)
                            : position.values("taxAmount").get(0)
                    );
                    positionAmounts.add(
                        position.values("amount").isEmpty()
                            ? new BigDecimal(0.0)
                            : position.values("amount").get(0)
                    );
                    salesCommissions.add(
                        position.values("salesCommission").isEmpty()
                            ? new BigDecimal(0)
                            : position.values("salesCommission").get(0)
                    );
                    salesCommissionIsPercentages.add(
                        position.values("salesCommissionIsPercentage").isEmpty()
                            ? Boolean.FALSE
                            : position.values("salesCommissionIsPercentage").get(0)
                    );
                }
                BigDecimal totalBaseAmount = null;
                BigDecimal totalDiscountAmount = null;
                BigDecimal totalTaxAmount = null;                
                BigDecimal totalAmount = null;
                BigDecimal totalAmountIncludingTax = null;
                BigDecimal totalSalesCommission = null;
                // To amount calculation using calculation rule if defined
                if(!contract.values("calcRule").isEmpty()) {
                    DataproviderObject_1_0 params = new DataproviderObject(new Path(""));
                    params.values("contract").add(contract.path());
                    params.values("lineItemNumber").addAll(lineItemNumbers);
                    params.values("positionBaseAmount").addAll(positionBaseAmounts);
                    params.values("positionDiscountAmount").addAll(positionDiscountAmounts);
                    params.values("positionTaxAmount").addAll(positionTaxAmounts);
                    params.values("positionAmount").addAll(positionAmounts);
                    params.values("salesCommission").addAll(salesCommissions);
                    params.values("salesCommissionIsPercentage").addAll(salesCommissionIsPercentages);                    
                    DataproviderObject result = new DataproviderObject(new Path(""));
                    this.getContractAmounts(
                        this.backend.retrieveObjectFromDelegation(
                            (Path)contract.values("calcRule").get(0)
                        ),
                        params,
                        result
                    );
                    totalBaseAmount = (BigDecimal)result.values("totalBaseAmount").get(0);
                    totalDiscountAmount = (BigDecimal)result.values("totalDiscountAmount").get(0);   
                    totalTaxAmount = (BigDecimal)result.values("totalTaxAmount").get(0);
                    totalAmount = (BigDecimal)result.values("totalAmount").get(0);   
                    totalAmountIncludingTax = (BigDecimal)result.values("totalAmountIncludingTax").get(0);
                    totalSalesCommission = (BigDecimal)result.values("totalSalesCommission").get(0);   
                }
                // To default amount calculation if no calculation rule is defined
                else {
                    totalBaseAmount = new BigDecimal(0);
                    totalDiscountAmount = new BigDecimal(0);
                    totalTaxAmount = new BigDecimal(0);
                    totalSalesCommission = new BigDecimal(0);
                    for(int i = 0; i < positionBaseAmounts.size(); i++) {
                        totalBaseAmount = totalBaseAmount.add(
                          (BigDecimal)positionBaseAmounts.get(i)
                        );
                        BigDecimal discountAmount = (BigDecimal)positionDiscountAmounts.get(i);
                        totalDiscountAmount = totalDiscountAmount.add(discountAmount);
                        totalTaxAmount = totalTaxAmount.add(
                          (BigDecimal)positionTaxAmounts.get(i)
                        );
                        BigDecimal salesCommission = salesCommissions.get(i) != null
                          ? (BigDecimal) salesCommissions.get(i)
                          : new BigDecimal(0);
                        BigDecimal baseAmount = (BigDecimal)positionBaseAmounts.get(i);
                        totalSalesCommission = totalSalesCommission.add(
                          (salesCommissionIsPercentages.get(i) != null) &&
                          (((Boolean)salesCommissionIsPercentages.get(i)).booleanValue())
                          ? baseAmount.subtract(discountAmount).multiply(salesCommission.divide(new BigDecimal(100), BigDecimal.ROUND_UP))
                          : salesCommission
                        );
                    }
                    totalAmount = totalBaseAmount.subtract(totalDiscountAmount);
                    totalAmountIncludingTax = totalAmount.add(totalTaxAmount);
                }
                contract.clearValues("totalBaseAmount").add(totalBaseAmount);
                contract.clearValues("totalDiscountAmount").add(totalDiscountAmount);
                contract.clearValues("totalAmount").add(totalAmount);
                contract.clearValues("totalTaxAmount").add(totalTaxAmount);
                contract.clearValues("totalAmountIncludingTax").add(totalAmountIncludingTax);
                contract.clearValues("totalSalesCommission").add(totalSalesCommission);
            }
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * calculate forward references, i.e.
     * Lead --> Opportunity
     * Opportunity --> Quote
     * Quote --> SalesOrder
     * SalesOrder --> Invoice
     */
    public void calculateForwardReferences(
        DataproviderObject_1_0 contract,
        Set fetchSet
    ) throws ServiceException {
      String contractType = (String)contract.values(SystemAttributes.OBJECT_CLASS).get(0);
      String referenceName = null;
      if("org:opencrx:kernel:contract1:Lead".equals(contractType)) {
          referenceName = "opportunity";
      }
      else if("org:opencrx:kernel:contract1:Opportunity".equals(contractType)) {
          referenceName = "quote";
      }
      else if("org:opencrx:kernel:contract1:Quote".equals(contractType)) {
          referenceName = "salesOrder";   
      }
      else if("org:opencrx:kernel:contract1:SalesOrder".equals(contractType)) {
          referenceName = "invoice";
      }
      if(
          (referenceName != null) &&          
          ((fetchSet == null) || fetchSet.contains(referenceName))
      ) {
          Path contractIdentity = new Path((String)contract.values(SystemAttributes.OBJECT_IDENTITY).get(0));
          List referencedContracts = 
              this.backend.getDelegatingRequests().addFindRequest(
	              contractIdentity.getParent().getParent().getChild(referenceName),
	              new FilterProperty[]{
	                  new FilterProperty(
			              Quantors.THERE_EXISTS,
			              "origin",
			              FilterOperators.IS_IN,
			              contractIdentity
	                  )
	              }
              );
          contract.clearValues(referenceName);
          for(Iterator i = referencedContracts.iterator(); i.hasNext(); ) {
              contract.values(referenceName).add(
                  ((DataproviderObject_1_0)i.next()).path()
              );
          }
      }
    }

    //-----------------------------------------------------------------------
    public Invoice createInvoice(
        Path salesOrderIdentity
    ) throws ServiceException {
        Map objectMarshallers = new HashMap();
        objectMarshallers.put(
          "org:opencrx:kernel:contract1:SalesOrder",
          new Marshaller() {
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 salesOrder = (DataproviderObject_1_0)s;
              DataproviderObject invoice = new DataproviderObject(new Path(""));
              copyAbstractContract(
                  salesOrder,
                  invoice
              );
              invoice.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Invoice");
              invoice.clearValues("contractState").add(new Short((short)0));              
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
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 salesOrderPosition = (DataproviderObject_1_0)s;
              DataproviderObject invoicePosition = new DataproviderObject(new Path(""));
              copyAbstractContractPosition(
                  salesOrderPosition,
                  invoicePosition
              );
              invoicePosition.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:InvoicePosition");
              invoicePosition.clearValues("contractPositionState").add(new Short((short)0));
              return invoicePosition;
            }
            public Object unmarshal(Object s) {
                throw new UnsupportedOperationException();
            }
          }
        );
        DataproviderObject invoice =
            this.backend.retrieveObjectForModification(
                this.cloning.cloneAndUpdateReferences(
                    this.backend.retrieveObject(salesOrderIdentity),
                    salesOrderIdentity.getParent().getParent().getChild("invoice"),
                    objectMarshallers,
                    DEFAULT_REFERENCE_FILTER,
                    false,
                    AttributeSelectors.ALL_ATTRIBUTES
                ).path()
            );
        invoice.clearValues("origin").add(salesOrderIdentity);
        if(invoice.values("salesRep").isEmpty()) {
            this.backend.getBase().assignToMe(
                invoice,
                null,
                true,
                null
            );
        }
        return invoice == null
            ? null
            : (Invoice)this.backend.getDelegatingPkg().refObject(invoice.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public SalesOrder createSalesOrder(
        Path quoteIdentity
    ) throws ServiceException {
        Map objectMarshallers = new HashMap();
        objectMarshallers.put(
          "org:opencrx:kernel:contract1:Quote",
          new Marshaller() {
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 quote = (DataproviderObject_1_0)s;
              DataproviderObject salesOrder = new DataproviderObject(new Path(""));
              copyAbstractContract(
                  quote,
                  salesOrder
              );
              salesOrder.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:SalesOrder");
              salesOrder.clearValues("contractState").add(new Short((short)0));
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
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 quotePosition = (DataproviderObject_1_0)s;
              DataproviderObject salesOrderPosition = new DataproviderObject(new Path(""));
              copyAbstractContractPosition(
                quotePosition,
                salesOrderPosition
              );
              salesOrderPosition.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:SalesOrderPosition");
              salesOrderPosition.clearValues("contractPositionState").add(new Short((short)0));
              salesOrderPosition.clearValues("estimatedCloseDate");
              salesOrderPosition.clearValues("closeProbability");
              return salesOrderPosition;
            }
            public Object unmarshal(Object s) {
              throw new UnsupportedOperationException();
            }
          }
        );
        DataproviderObject salesOrder =
            this.backend.retrieveObjectForModification(
                this.cloning.cloneAndUpdateReferences(
                    this.backend.retrieveObject(quoteIdentity),
                    quoteIdentity.getParent().getParent().getChild("salesOrder"),
                    objectMarshallers,
                    DEFAULT_REFERENCE_FILTER,
                    false,
                    AttributeSelectors.ALL_ATTRIBUTES
                ).path()
           );
        salesOrder.clearValues("origin").add(quoteIdentity);
        if(salesOrder.values("salesRep").isEmpty()) {
            this.backend.getBase().assignToMe(
                salesOrder,
                null,
                true,
                null
            );
        }
        return salesOrder == null
            ? null
            : (SalesOrder)this.backend.getDelegatingPkg().refObject(salesOrder.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public Quote createQuote(
        Path opportunityIdentity
    ) throws ServiceException {
        Map objectMarshallers = new HashMap();
        objectMarshallers.put(
          "org:opencrx:kernel:contract1:Opportunity",
          new Marshaller() {
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 opportunity = (DataproviderObject_1_0)s;
              DataproviderObject quote = new DataproviderObject(new Path(""));
              copyAbstractContract(
                opportunity,
                quote
              );
              quote.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Quote");
              quote.clearValues("contractState").add(new Short((short)0));
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
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 opportunityPosition = (DataproviderObject_1_0)s;
              DataproviderObject quotePosition = new DataproviderObject(new Path(""));
              copyAbstractContractPosition(
                opportunityPosition,
                quotePosition
              );
              quotePosition.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:QuotePosition");
              quotePosition.clearValues("contractPositionState").add(new Short((short)0));
              return quotePosition;
            }
            public Object unmarshal(Object s) {
              throw new UnsupportedOperationException();
            }
          }
        );
        DataproviderObject quote =
            this.backend.retrieveObjectForModification(
                this.cloning.cloneAndUpdateReferences(
                    this.backend.retrieveObject(opportunityIdentity),
                    opportunityIdentity.getParent().getParent().getChild("quote"),
                    objectMarshallers,
                    DEFAULT_REFERENCE_FILTER,
                    false,
                    AttributeSelectors.ALL_ATTRIBUTES
            ).path()
        );
        quote.clearValues("origin").add(opportunityIdentity);              
        if(quote.values("salesRep").isEmpty()) {
            this.backend.getBase().assignToMe(
                quote,
                null,
                true,
                null
            );
        }
        return quote == null
            ? null
            : (Quote)this.backend.getDelegatingPkg().refObject(quote.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public Opportunity createOpportunity(
        Path leadIdentity
    ) throws ServiceException {
        Map objectMarshallers = new HashMap();
        objectMarshallers.put(
          "org:opencrx:kernel:contract1:Lead",
          new Marshaller() {
            public Object marshal(Object s) throws ServiceException {
              DataproviderObject_1_0 lead = (DataproviderObject_1_0)s;
              DataproviderObject opportunity = new DataproviderObject(new Path(""));
              copyAbstractContract(
                lead,
                opportunity
              );
              opportunity.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Opportunity");
              opportunity.clearValues("contractState").add(new Short((short)0));
              return opportunity;     
            }
            public Object unmarshal(Object s) {
              throw new UnsupportedOperationException();
            }
          }
        );
        DataproviderObject opportunity =
            this.backend.retrieveObjectForModification(
                this.cloning.cloneAndUpdateReferences(
                    this.backend.retrieveObject(
                        leadIdentity
                    ),
                    leadIdentity.getParent().getParent().getChild("opportunity"),
                    objectMarshallers,
                    DEFAULT_REFERENCE_FILTER,
                    false,
                    AttributeSelectors.ALL_ATTRIBUTES
                ).path()
            );
        opportunity.clearValues("origin").add(leadIdentity);
        if(opportunity.values("salesRep").isEmpty()) {
            this.backend.getBase().assignToMe(
                opportunity,
                null,
                true,
                null
            );
        }
        return opportunity == null
            ? null
            : (Opportunity)this.backend.getDelegatingPkg().refObject(opportunity.path().toXri());
    }

    //-------------------------------------------------------------------------
    private void resetPrice(
        DataproviderObject position
    ) {
        position.clearValues("pricePerUnit");
    }

    //-------------------------------------------------------------------------
    private boolean productOverridesPrice(
        DataproviderObject_1_0 product
    ) throws ServiceException {
        Boolean overridePrice = product == null
            ? Boolean.FALSE
            : (Boolean)product.values("overridePrice").get(0);
        if((overridePrice != null) && overridePrice.booleanValue()) {
            return true;
        }         
        // Does type override price?
        DataproviderObject_1_0 productType = (product != null) && !product.values("type").isEmpty()
            ? this.backend.retrieveObjectFromDelegation((Path)product.values("type").get(0))
            : null;
        overridePrice = productType == null
            ? Boolean.FALSE
            : (Boolean)productType.values("overridePrice").get(0);
        if((overridePrice != null) && overridePrice.booleanValue()) {
            return true;
        }         
        return false;
    }
    
    //-------------------------------------------------------------------------
    private boolean parentPositionOverridesPrice(
        DataproviderObject_1_0 position
    ) throws ServiceException {
        while(!position.values("parentPosition").isEmpty()) {
            DataproviderObject_1_0 parentPosition = this.backend.retrieveObjectFromDelegation(
                (Path)position.values("parentPosition").get(0)
            );
            DataproviderObject_1_0 product = (parentPosition != null) && !parentPosition.values("basedOn").isEmpty()
                ? this.backend.retrieveObjectFromDelegation((Path)parentPosition.values("basedOn").get(0))
                : null;
            if(this.productOverridesPrice(product)) {
                return true;
            }
            position = parentPosition;
        }
        return false;
    }
    
    //-------------------------------------------------------------------------
    public void updateContractPositionPrice(
        DataproviderObject_1_0 contract,
        DataproviderObject position,
        DataproviderObject_1_0 oldValues,
        DataproviderObject_1_0 product,
        boolean priceEnabledOverridesPosition
    ) throws ServiceException {

        if(contract == null) return;
        if(position == null) return;
        if(product == null) return;
        Path productIdentity = product.path();
        
        // pricingRule
        Path pricingRuleIdentity = null;
        if(oldValues == null) {
            pricingRuleIdentity = (Path)position.values("pricingRule").get(0);
        }
        else {
            pricingRuleIdentity = (position.getValues("pricingRule") != null) && !position.values("pricingRule").isEmpty()
                ? (Path)position.values("pricingRule").get(0)
                : (Path)oldValues.values("pricingRule").get(0);            
        }
        if(pricingRuleIdentity == null) return;       
        // quantity
        BigDecimal quantity = null;
        if(oldValues == null) {
            quantity = (BigDecimal)position.values("quantity").get(0);
        }
        else {
            quantity = (position.getValues("quantity") != null) && !position.values("quantity").isEmpty()
                ? (BigDecimal)position.values("quantity").get(0)
                : (BigDecimal)oldValues.values("quantity").get(0);
        }
        if(quantity == null) return;
        // pricingDate
        String pricingDate = null;
        if(oldValues == null) {
            pricingDate = (String)position.values("pricingDate").get(0);
        }
        else {
            pricingDate = (position.getValues("pricingDate") != null) && !position.values("pricingDate").isEmpty()
                ? (String)position.values("pricingDate").get(0)
                : (String)oldValues.values("pricingDate").get(0);
        }
        // priceUom
        Path priceUomIdentity = (position.getValues("priceUom") != null) && !position.values("priceUom").isEmpty()
            ? (Path)position.values("priceUom").get(0)
            : !position.values("uom").isEmpty()
                ? (Path)position.values("uom").get(0)
                : oldValues == null
                    ? null
                    : !oldValues.values("priceUom").isEmpty()
                        ? (Path)oldValues.values("priceUom").get(0)
                        : (Path)oldValues.values("uom").get(0);
        // priceLevel
        Path priceLevelIdentity = null;
        BigDecimal customerDiscount = null;
        Boolean customerDiscountIsPercentage = null;
        if(position.getValues("priceLevel") != null) {
            priceLevelIdentity = (Path)position.values("priceLevel").get(0);
        }
        else {
            try {
                org.opencrx.kernel.product1.jmi1.GetPriceLevelResult res = 
                    this.backend.getProducts().getPriceLevel(
                        pricingRuleIdentity == null
                            ? null
                            : (org.opencrx.kernel.product1.jmi1.PricingRule)this.backend.getLocalPkg().refObject(pricingRuleIdentity.toXri()),                
                        (org.opencrx.kernel.contract1.jmi1.AbstractContract)this.backend.getLocalPkg().refObject(contract.path().toXri()),                
                        productIdentity == null
                            ? null
                            : (org.opencrx.kernel.product1.jmi1.AbstractProduct)this.backend.getLocalPkg().refObject(productIdentity.toXri()),                
                        priceUomIdentity == null
                            ? null
                            : (org.opencrx.kernel.uom1.jmi1.Uom)this.backend.getLocalPkg().refObject(priceUomIdentity.toXri()),                
                        quantity, 
                        pricingDate == null
                            ? new Date()
                            : org.openmdx.base.text.format.DateFormat.getInstance().parse(pricingDate)
                      );
                priceLevelIdentity = (Path)res.refDelegate().objGetValue("priceLevel");
                customerDiscount = res.getCustomerDiscount();
                customerDiscountIsPercentage = res.isCustomerDiscountIsPercentage();
            }
            catch(ParseException e) {
                throw new ServiceException(e);
            }
            finally {
            }
        }
        position.clearValues("priceLevel");
        if(priceLevelIdentity != null) {
            position.values("priceLevel").add(priceLevelIdentity);
        }        
        // Find price matching price list and quantity
        List prices = new ArrayList();
        DataproviderObject_1_0 listPrice = null;
        AppLog.trace("Price level found", "" + (priceLevelIdentity != null));
        if(priceLevelIdentity != null) {
            // Find product prices
            // priceLevel and priceUom must match
            FilterProperty[] priceFilter = new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceLevel",
                    FilterOperators.IS_IN,
                    priceLevelIdentity                        
                ),
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "uom",
                    FilterOperators.IS_IN,
                    priceUomIdentity                        
                )
            };
            AppLog.trace("Lookup of prices with filter", Arrays.asList(priceFilter));
            prices = this.backend.getDelegatingRequests().addFindRequest(
                productIdentity.getChild("basePrice"),
                priceFilter,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            AppLog.trace("Matching prices found", prices);
            // Check quantity
            for(
                Iterator i = prices.iterator(); 
                i.hasNext(); 
            ) {
                DataproviderObject_1_0 current = (DataproviderObject_1_0)i.next();
                AppLog.trace("Testing price", current);
                boolean quantityFromMatches =
                    (current.values("quantityFrom").isEmpty()) || 
                    (quantity == null) || 
                    ((BigDecimal)current.values("quantityFrom").get(0)).compareTo(quantity) >= 0;
                AppLog.trace("Quantity from matches", new Boolean(quantityFromMatches));
                boolean quantityToMatches = 
                    (current.values("quantityTo").isEmpty()) || 
                    (quantity == null) || 
                    ((BigDecimal)current.values("quantityTo").get(0)).compareTo(quantity) < 0;
                AppLog.trace("Quantity to matches", new Boolean(quantityToMatches));
                if(quantityFromMatches && quantityToMatches) {
                    listPrice = current;
                    break;
                }
            }
        }
        // List price found?
        AppLog.trace("List price found", "" + (listPrice != null));
        position.clearValues("pricingState").add(
            new Short(PRICING_STATE_NA)
        );
        if(listPrice != null) {
            position.clearValues("listPrice").add(
                listPrice.path()
            );
            position.clearValues("pricingState").add(
                new Short(PRICING_STATE_OK)
            );
            BigDecimal listPriceDiscount = (BigDecimal)listPrice.values("discount").get(0);
            Boolean listPriceDiscountIsPercentage = (Boolean)listPrice.values("discountIsPercentage").get(0);
            // Get existing discount
            BigDecimal discount = null;
            if(oldValues == null) {
                discount = (BigDecimal)position.values("discount").get(0);
            }
            else {
                discount = (position.getValues("discount") != null) && !position.values("discount").isEmpty()
                    ? (BigDecimal)position.values("discount").get(0)
                    : (BigDecimal)oldValues.values("discount").get(0);
            }            
            // Get existing discountIsPercentage
            Boolean discountIsPercentage = null;
            if(oldValues == null) {
                discountIsPercentage = (Boolean)position.values("discountIsPercentage").get(0);
            }
            else {
                discountIsPercentage = (position.getValues("discountIsPercentage") != null) && !position.values("discountIsPercentage").isEmpty()
                    ? (Boolean)position.values("discountIsPercentage").get(0)
                    : (Boolean)oldValues.values("discountIsPercentage").get(0);
            }            
            // Recalc discount if not already set on position
            if(discount == null) {
                // Accumulate list price discount and customer discount
                if(customerDiscount == null) {
                    discount = listPriceDiscount;
                    discountIsPercentage = listPriceDiscountIsPercentage;
                }
                else {
                    BigDecimal price = (BigDecimal)listPrice.values("price").get(0);
                    price = price == null
                        ? new BigDecimal(0.0)
                        : price;
                    listPriceDiscount = listPriceDiscount == null
                        ? new BigDecimal(0.0)
                        : listPriceDiscount;
                    customerDiscount = customerDiscount == null
                        ? new BigDecimal(0.0)
                        : customerDiscount;                    
                    if(
                        (listPriceDiscount.compareTo(new BigDecimal(0.0)) == 0) ||
                        ((listPriceDiscountIsPercentage != null) &&
                        listPriceDiscountIsPercentage.booleanValue())
                    ) {
                        // listPriceDiscountIsPercentage=true, customerDiscountIsPercentage=true
                        // totalDiscount = 1 - (1-listPriceDiscount) * (1-customerDiscount)
                        if(
                            (customerDiscount.compareTo(new BigDecimal(0.0)) == 0) ||
                            ((customerDiscountIsPercentage != null) &&
                            customerDiscountIsPercentage.booleanValue())
                        ) {
                            discountIsPercentage = Boolean.TRUE;
                            discount =
                                new BigDecimal(1.0).subtract(
                                    new BigDecimal(1.0).subtract(listPriceDiscount.divide(new BigDecimal(100.0), BigDecimal.ROUND_FLOOR)).multiply(
                                        new BigDecimal(1.0).subtract(customerDiscount.divide(new BigDecimal(100.0), BigDecimal.ROUND_FLOOR))
                                    )
                                ).multiply(new BigDecimal(100.0));
                        }
                        // listPriceDiscountIsPercentage=true, customerDiscountIsPercentage=false
                        // totalDiscount = price*listPriceDiscount + customerDiscount
                        else {
                            discountIsPercentage = Boolean.FALSE;
                            discount = price.multiply(
                                listPriceDiscount.divide(new BigDecimal(100.0), BigDecimal.ROUND_DOWN)
                            ).add(customerDiscount);
                        }
                    }
                    else {
                        // listPriceDiscountIsPercentage=false, customerDiscountIsPercentage=true
                        // totalDiscount = listPriceDiscount + (price - listPriceDiscount)*customerDiscount
                        if(
                            (customerDiscount.compareTo(new BigDecimal(0.0)) == 0) ||
                            (customerDiscountIsPercentage != null) &&
                            customerDiscountIsPercentage.booleanValue()
                        ) {
                            discountIsPercentage = Boolean.FALSE;
                            discount = listPriceDiscount.add(
                                price.subtract(listPriceDiscount).multiply(customerDiscount.divide(new BigDecimal(100.0), BigDecimal.ROUND_FLOOR))
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
            // Position creation
            if(oldValues == null) {
                AppLog.trace("New position");
                if(position.getValues("pricePerUnit") == null) {
                    position.values("pricePerUnit").addAll(
                        listPrice.values("price")
                    );
                    position.clearValues("discount");
                    if(discount != null) {
                        position.clearValues("discount").add(
                            discount
                        );
                    }
                    position.clearValues("discountIsPercentage");
                    if(discountIsPercentage != null) {
                        position.clearValues("discountIsPercentage").add(
                            discountIsPercentage
                        );
                    }
                }
                if(position.getValues("priceUom") == null) {
                    position.values("priceUom").addAll(listPrice.values("uom"));
                }
            }
            // Position update
            else {
                AppLog.trace("Replace position");
                boolean pricePerUnitModified = (position.getValues("pricePerUnit") != null) && !position.getValues("pricePerUnit").isEmpty() && !oldValues.getValues("pricePerUnit").isEmpty() 
                    ? ((BigDecimal)position.getValues("pricePerUnit").get(0)).compareTo((BigDecimal)oldValues.values("pricePerUnit").get(0)) != 0
                    : !position.getValues("pricePerUnit").isEmpty();
                AppLog.trace("pricePerUnitModified", new Boolean(pricePerUnitModified));
                boolean priceUomModified = (position.getValues("priceUom") != null) && !position.getValues("priceUom").isEmpty() && !oldValues.getValues("priceUom").isEmpty() 
                    ? position.getValues("priceUom").get(0).equals(oldValues.values("priceUom").get(0))
                    : !position.getValues("priceUom").isEmpty();                    
                AppLog.trace("priceUomModified", new Boolean(priceUomModified));
                AppLog.trace("priceEnabledOverridesPosition", new Boolean(priceEnabledOverridesPosition));
                if(priceEnabledOverridesPosition && !pricePerUnitModified) {
                    position.clearValues("pricePerUnit").addAll(
                        listPrice.values("price")
                    );
                    position.clearValues("discount");
                    if(discount != null) {
                        position.clearValues("discount").add(
                            discount
                        );
                    }
                    position.clearValues("discountIsPercentage");
                    if(discountIsPercentage != null) {
                        position.clearValues("discountIsPercentage").add(
                            discountIsPercentage
                        );
                    }
                }
                if(priceEnabledOverridesPosition && !priceUomModified) {
                    position.clearValues("priceUom").addAll(listPrice.values("uom"));
                }
                AppLog.trace("Updated position", position);
            }
        }
        if(this.productOverridesPrice(product)) {
            this.resetPrice(position);                
        }
    }
    
    //-------------------------------------------------------------------------
    public long getMaxLineItemNumber(
        DataproviderObject_1_0 contract,
        Path parentPositionIdentity
    ) throws ServiceException {
        long maxLineItemNumber = 0L;
        List positions = this.backend.getDelegatingRequests().addFindRequest(
            contract.path().getChild("position"),
            parentPositionIdentity == null
                ? null
                : new FilterProperty[]{
                      new FilterProperty(
                          Quantors.THERE_EXISTS, 
                          "parentPosition", 
                          FilterOperators.IS_IN, 
                          parentPositionIdentity
                      )
                  },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        for(
            Iterator i = positions.iterator(); 
            i.hasNext(); 
        ) {
            DataproviderObject_1_0 pos = (DataproviderObject_1_0)i.next();
            if(
                (pos.values("lineItemNumber").size() > 0) && 
                (((Number)pos.values("lineItemNumber").get(0)).longValue() > maxLineItemNumber)
            ) {
                maxLineItemNumber = ((Number)pos.values("lineItemNumber").get(0)).longValue();
            }
        }
        return maxLineItemNumber;
    }

    //-------------------------------------------------------------------------
    public void updateContractPosition(
        DataproviderObject_1_0 contract,
        DataproviderObject position,
        DataproviderObject_1_0 oldValues,
        DataproviderObject_1_0 product,
        boolean reprice
    ) {
        try {
            
            String productClass = (String)product.values(SystemAttributes.OBJECT_CLASS).get(0);
            
            // Update uoms
            Path positionUomIdentity = null;
            Path priceUomIdentity = null;
            // Create
            if(oldValues == null) {
                // Get from position or take from defaultUom specified on product, its
                // product type or its underyling product
                if((position.getValues("uom") != null) && !position.values("uom").isEmpty()) { 
                    positionUomIdentity = (Path)position.values("uom").get(0);
                }
                else if((product != null) && !product.values("defaultUom").isEmpty()) {
                    positionUomIdentity = (Path)product.values("defaultUom").get(0);
                }
                priceUomIdentity = (position.getValues("priceUom") != null) && !position.values("priceUom").isEmpty()
                    ? (Path)position.values("priceUom").get(0)
                    : null;
            }
            // Replace
            else {
                // Get uom from position if supplied, else leave unchanged
                positionUomIdentity = position.getValues("uom") != null
                    ? (Path)position.values("uom").get(0)
                    : (Path)oldValues.values("uom").get(0);
                priceUomIdentity = position.getValues("priceUom") != null
                    ? (Path)position.values("priceUom").get(0)
                    : (Path)oldValues.values("priceUom").get(0);
            }
            // Update uom
            if(position.getValues("uom") != null) {
                position.clearValues("uom");
            }
            if(positionUomIdentity != null) {
                position.values("uom").add(positionUomIdentity);
            }
            // Set priceUom
            if(position.getValues("priceUom") != null) {
                position.clearValues("priceUom");
            }
            if(priceUomIdentity != null) {
                position.values("priceUom").add(priceUomIdentity);
            }
            
            // Update sales tax type
            Path salesTaxTypeIdentity = null;
            // Create
            if(oldValues == null) {
                if((position.getValues("salesTaxType") != null) && (position.values("salesTaxType").size() > 0)) {
                    salesTaxTypeIdentity = (Path)position.values("salesTaxType").get(0);
                }
                else if((product != null) && (product.values("salesTaxType").size() > 0)) {
                    salesTaxTypeIdentity = (Path)product.values("salesTaxType").get(0);
                }
            }
            // Replace
            else {
                salesTaxTypeIdentity = position.getValues("salesTaxType") != null
                    ? (Path)position.values("salesTaxType").get(0)
                    : (Path)oldValues.values("salesTaxType").get(0);
            }
            position.clearValues("salesTaxType");
            if(salesTaxTypeIdentity != null) {
                position.values("salesTaxType").add(salesTaxTypeIdentity);
            }

            // Update pricing rule
            Path pricingRuleIdentity = null;
            // Create
            if(oldValues == null) {
                pricingRuleIdentity = (position.getValues("pricingRule") != null) && (position.values("pricingRule").size() > 0)
                    ? (Path)position.values("pricingRule").get(0)
                    : (Path)contract.values("pricingRule").get(0);
                if(pricingRuleIdentity == null) {
                    // Get default pricing rule
                    List pricingRules = this.backend.getDelegatingRequests().addFindRequest(
                        product.path().getPrefix(5).getChild("pricingRule"),
                        new FilterProperty[]{
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "isDefault",
                                FilterOperators.IS_IN,
                                Boolean.TRUE
                            )
                        }
                    );
                    if(pricingRules.size() > 0) {
                        pricingRuleIdentity = ((DataproviderObject_1_0)pricingRules.iterator().next()).path();
                    }
                }
            }
            // Replace
            else {
                pricingRuleIdentity = position.getValues("pricingRule") != null
                    ? (Path)position.values("pricingRule").get(0)
                    : (Path)oldValues.values("pricingRule").get(0);                
            }
            position.clearValues("pricingRule");
            if(pricingRuleIdentity != null) {
                position.values("pricingRule").add(pricingRuleIdentity);
            }
            // Reprice            
            if(reprice) {
                DataproviderObject_1_0 pricingProduct = product;
                this.updateContractPositionPrice(
                    contract,
                    position, 
                    oldValues,
                    pricingProduct,
                    true
                );
            }
            // PositionCreation 
            if(oldValues == null) {
                DataproviderObject positionCreation = new DataproviderObject(
                    position.path().getPrefix(position.path().size() - 2).getDescendant(
                        new String[]{"positionModification", this.backend.getUidAsString()}                          
                    )
                );
                positionCreation.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:contract1:PositionCreation"
                );
                positionCreation.values("involved").add(
                    position.path()
                );
                this.backend.createObject(
                    positionCreation
                );
            }
            // QuantityModification 
            else {
                Comparable quantityOld = (Comparable)oldValues.values("quantity").get(0);
                Comparable quantityNew = (Comparable)position.values("quantity").get(0);
                if(quantityOld.compareTo(quantityNew) != 0) {
                    DataproviderObject quantityModification = new DataproviderObject(
                        position.path().getPrefix(position.path().size() - 2).getDescendant(
                            new String[]{"positionModification", this.backend.getUidAsString()}                          
                        )
                    );
                    quantityModification.values(SystemAttributes.OBJECT_CLASS).add(
                        "org:opencrx:kernel:contract1:QuantityModification"
                    );
                    quantityModification.values("involved").add(
                        position.path()
                    );
                    quantityModification.values("quantity").add(
                        quantityOld
                    );
                    this.backend.createObject(
                        quantityModification
                    );
                }                
            }
        }
        catch(ServiceException e) {
            AppLog.info(e.getMessage(), e.getCause());
        }
    }
    
    //-------------------------------------------------------------------------
    public void updateContractPositionConfigurationProperty(
        DataproviderObject_1_0 contract,
        DataproviderObject property,
        DataproviderObject_1_0 oldValues
    ) {
        try {
            // ConfigurationModification 
            if(oldValues != null) {
                boolean isModified = false;
                String propertyValue = null;
                String propertyType = (String)property.values(SystemAttributes.OBJECT_CLASS).get(0);
                if("org:opencrx:kernel:base:StringProperty".equals(propertyType)) {                    
                    isModified = !property.values("stringValue").get(0).equals(oldValues.values("stringValue").get(0));
                    propertyValue = oldValues.values("stringValue").get(0).toString();
                }
                else if("org:opencrx:kernel:base:IntegerProperty".equals(propertyType)) {
                    isModified = ((Number)oldValues.values("integerValue").get(0)).intValue() != ((Number)property.values("integerValue").get(0)).intValue();
                    propertyValue = oldValues.values("integerValue").get(0).toString();
                }
                else if("org:opencrx:kernel:base:BooleanProperty".equals(propertyType)) {
                    isModified = ((Comparable)oldValues.values("booleanValue").get(0)).compareTo(property.values("booleanValue").get(0)) != 0;                                        
                    propertyValue = oldValues.values("booleanValue").get(0).toString();
                }
                else if("org:opencrx:kernel:base:UriProperty".equals(propertyType)) {
                    isModified = !property.values("uriValue").get(0).equals(oldValues.values("uriValue").get(0));                    
                    propertyValue = oldValues.values("uriValue").get(0).toString();
                }
                else if("org:opencrx:kernel:base:DecimalProperty".equals(propertyType)) {
                    isModified = ((Comparable)oldValues.values("decimalValue").get(0)).compareTo(property.values("decimalValue").get(0)) != 0;                    
                    propertyValue = oldValues.values("decimalValue").get(0).toString();
                }
                else if("org:opencrx:kernel:base:ReferenceProperty".equals(propertyType)) {
                    isModified = !property.values("referenceValue").get(0).equals(oldValues.values("referenceValue").get(0));                    
                    propertyValue = ((Path)oldValues.values("referenceValue").get(0)).toXri();
                }
                if(isModified) {
                    // ConfigurationModification
                    DataproviderObject configurationModification = new DataproviderObject(
                        property.path().getPrefix(7).getDescendant(
                            new String[]{"positionModification", this.backend.getUidAsString()}                          
                        )
                    );
                    configurationModification.values(SystemAttributes.OBJECT_CLASS).add(
                        "org:opencrx:kernel:contract1:ConfigurationModification"
                    );
                    configurationModification.values("propertyValue").add(
                        propertyValue
                    );
                    configurationModification.values("involvedPty").add(
                        property.path()
                    );
                    configurationModification.values("involved").add(
                         property.path().getPrefix(9)
                    );
                    this.backend.createObject(
                        configurationModification
                    );
                }                
            }            
        }
        catch(ServiceException e) {
            AppLog.info(e.getMessage(), e.getCause());
        }
    }
    
    //-------------------------------------------------------------------------
    public ContractPosition createContractPosition(
        Path contractIdentity,
        String name,
        BigDecimal quantity,
        Date pricingDate,
        Path productIdentity,
        Path uomIdentity,
        Path priceUomIdentity,
        Path pricingRuleIdentity,
        boolean cloneProductConfiguration
    ) {
        DataproviderObject position = null;
        try {
            DataproviderObject_1_0 contract = this.backend.retrieveObject(contractIdentity);
            position = new DataproviderObject(
                contractIdentity.getDescendant(new String[]{"position", this.backend.getUidAsString()})
            );
            String contractType = (String)contract.values(SystemAttributes.OBJECT_CLASS).get(0);
            if(this.backend.getModel().isSubtypeOf(contractType, "org:opencrx:kernel:contract1:Opportunity")) {
                position.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:OpportunityPosition");
            }
            else if(this.backend.getModel().isSubtypeOf(contractType, "org:opencrx:kernel:contract1:Quote")) {
                position.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:QuotePosition");
            }
            else if(this.backend.getModel().isSubtypeOf(contractType, "org:opencrx:kernel:contract1:SalesOrder")) {
                position.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:SalesOrderPosition");
            }
            else if(this.backend.getModel().isSubtypeOf(contractType, "org:opencrx:kernel:contract1:Invoice")) {
                position.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:InvoicePosition");
            }
            else {
                return null;
            }
            
            // lineItemNumber
            position.values("lineItemNumber").add(
                new Long(
                    100000 * (this.getMaxLineItemNumber(contract, null) / 100000 + 1)
                )
            );
            // name
            if(name != null) {
                position.values("name").add(name);
            }
            else {
                position.values("name").add("Position " + position.values("lineItemNumber").get(0));
            }
            // quantity
            if(quantity != null) {
                position.values("quantity").add(quantity);
            }
            else {
                position.values("quantity").add(new BigDecimal(1));
            }
            // pricingDate
            if(pricingDate != null) {
                position.values("pricingDate").add(
                    org.openmdx.base.text.format.DateFormat.getInstance().format(pricingDate)
                );
            }
            else {
                position.values("pricingDate").add(
                    !contract.values("pricingDate").isEmpty()
                        ? contract.values("pricingDate").get(0)
                        : org.openmdx.base.text.format.DateFormat.getInstance().format(new Date())
                );
            }
            // product                    
            DataproviderObject_1_0 product = productIdentity == null
                ? null
                : this.backend.retrieveObjectFromDelegation(productIdentity);
            // uom (only touch if specified as param
            if(uomIdentity != null) {
                position.values("uom").add(uomIdentity);
            }
            // priceUom (only touch if specified as param
            if(priceUomIdentity != null) {
                position.values("priceUom").add(priceUomIdentity);
            }
            // pricingRule
            if(pricingRuleIdentity != null) {
                position.values("pricingRule").add(pricingRuleIdentity);
            }
            // calcRule
            position.values("calcRule").addAll(
                contract.values("calcRule")
            );            
            // Update position
            this.updateContractPosition(
                contract,
                position,
                null,
                product,
                true
            );
            position.clearValues("product");
            if(productIdentity != null) {
                position.values("product").add(productIdentity);            
            }
            
            // Create position
            this.backend.getDelegatingRequests().addCreateRequest(
                position
            );
            // Clone configurations
            if(cloneProductConfiguration) {
                this.backend.getProducts().cloneProductConfigurationSet(
                    productIdentity,
                    position.path(),
                    false,
                    true
                );
            }
            this.markContractAsDirty(
                contract.path()
            );
        }
	    catch(Exception e) {
	        new ServiceException(e).log();
	    }
	    return position == null
	        ? null
	        : (ContractPosition)this.backend.getDelegatingPkg().refObject(position.path().toXri());	               
    }
    
    //-------------------------------------------------------------------------
    private int calculateTimeDistributionOpenContracts(
        Path reference,
        String objectClass,
        int[] timeDistribution,
        String distributionOnAttribute,
        int closedThreshold
    ) {
      try {
        List objects = this.backend.getDelegatingRequests().addFindRequest(
          reference,
          new FilterProperty[]{
              new FilterProperty(
                  Quantors.THERE_EXISTS,
                  SystemAttributes.OBJECT_CLASS,
                  FilterOperators.IS_IN,
                  objectClass
              ),
              new FilterProperty(
                  Quantors.THERE_EXISTS,
                  "contractState",
                  FilterOperators.IS_LESS,
                  new Short((short)closedThreshold)
              )
          },
          AttributeSelectors.ALL_ATTRIBUTES,
          0,
          Contracts.BATCHING_MODE_SIZE,
          Directions.ASCENDING          
        );
        int count = 0;
        for(Iterator i = objects.iterator(); i.hasNext(); ) {
          DataproviderObject_1_0 object = (DataproviderObject_1_0)i.next();
          Date dt = new Date();
          try {
            dt = org.openmdx.base.text.format.DateFormat.getInstance().parse(
              (String)object.values(distributionOnAttribute).get(0)
            );
          } catch(Exception e) {}

          long delayInDays = (System.currentTimeMillis() - dt.getTime()) / 86400000;
          if(delayInDays < 0) timeDistribution[0]++;
          else if(delayInDays < 1) timeDistribution[1]++;
          else if(delayInDays < 2) timeDistribution[2]++;
          else if(delayInDays < 3) timeDistribution[3]++;
          else if(delayInDays < 4) timeDistribution[4]++;
          else if(delayInDays < 5) timeDistribution[5]++;
          else if(delayInDays < 6) timeDistribution[6]++;
          else if(delayInDays < 7) timeDistribution[7]++;
          else if(delayInDays < 8) timeDistribution[8]++;
          else if(delayInDays < 15) timeDistribution[9]++;
          else if(delayInDays < 31) timeDistribution[10]++;
          else if(delayInDays < 91) timeDistribution[11]++;
          else if(delayInDays < 181) timeDistribution[12]++;
          else if(delayInDays < 361) timeDistribution[13]++;
          else timeDistribution[14]++;
          
          count++;
        }
        return count;
      }
      catch(Exception e) {
          ServiceException e0 = new ServiceException(e);
          AppLog.warning("Error when iterating objects on reference", Arrays.asList(reference, e.getMessage()));
          return 0;
      }
    }

    //-------------------------------------------------------------------------
    DataproviderObject[] calculateUserHomeCharts(
        Path userHome,
        Path chartReference
    ) throws ServiceException {

      java.text.DateFormat dateFormat = 
          java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, new Locale("en_US")); 
      java.text.DateFormat timeFormat = 
          java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, new Locale("en_US"));
      String createdAt = dateFormat.format(new Date()) + " " + timeFormat.format(new Date());
        
      // try to get full name of contact
      String fullName = "";
      try {
          fullName = (String)this.backend.retrieveObject(
              (Path)this.backend.retrieveObject(userHome).values("contact").get(0)
          ).values("fullName").get(0);
      } catch(Exception e) {}
      
      DataproviderObject[] charts = new DataproviderObject[2];
      String chartTitle = null;
      
      /**
       * Contracts Overview
       */
      chartTitle = (fullName.length() == 0 ? "" : fullName + ": ") + "Assigned Open Contracts Overview (" + createdAt + ")";
      
      charts[0] = new DataproviderObject(
        chartReference.getChild("0")
      );
      charts[0].values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:home1:Media");
      charts[0].values("description").add(chartTitle);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);

      pw.println("BEGIN:VND.OPENDMDX-CHART");
      pw.println("VERSION:1.0");
      pw.println("COUNT:1");

      // Sales Process Overview
      pw.println("CHART[0].TYPE:HORIZBAR");
      pw.println("CHART[0].LABEL:" + chartTitle);
      pw.println("CHART[0].SCALEXTITLE:#Contracts");
      pw.println("CHART[0].SCALEYTITLE:Contract type");
      pw.println("CHART[0].COUNT:5");
      
      pw.println("CHART[0].LABEL[0]:Invoices");
      pw.println("CHART[0].LABEL[1]:Sales Orders");
      pw.println("CHART[0].LABEL[2]:Quotes");
      pw.println("CHART[0].LABEL[3]:Opportunities");
      pw.println("CHART[0].LABEL[4]:Leads");
      
      int[] counts = new int[]{0, 0, 0, 0, 0};
      int[] timeDistribution = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      Path assignedContractReference = userHome.getChild("assignedContract");
      counts[0] = this.calculateTimeDistributionOpenContracts(
          assignedContractReference, 
          "org:opencrx:kernel:contract1:Invoice", 
          timeDistribution, 
          SystemAttributes.MODIFIED_AT, 
          CLOSED_THRESHOLD_INVOICE
      );
      counts[1] = this.calculateTimeDistributionOpenContracts(
          assignedContractReference, 
          "org:opencrx:kernel:contract1:SalesOrder", 
          timeDistribution,
          SystemAttributes.MODIFIED_AT, 
          CLOSED_THRESHOLD_SALES_ORDER
      );
      counts[2] = this.calculateTimeDistributionOpenContracts(
          assignedContractReference, 
          "org:opencrx:kernel:contract1:Quote", 
          timeDistribution, 
          SystemAttributes.MODIFIED_AT, 
          CLOSED_THRESHOLD_QUOTE
      );
      counts[3] = this.calculateTimeDistributionOpenContracts(
          assignedContractReference, 
          "org:opencrx:kernel:contract1:Opportunity", 
          timeDistribution, 
          SystemAttributes.MODIFIED_AT, 
          CLOSED_THRESHOLD_OPPORTUNITY
      );
      counts[4] = this.calculateTimeDistributionOpenContracts(
          assignedContractReference, 
          "org:opencrx:kernel:contract1:Lead", 
          timeDistribution, 
          SystemAttributes.MODIFIED_AT, 
          CLOSED_THRESHOLD_LEAD
      );
          
      int maxValue = 0;
      for(int i = 0; i < counts.length; i++) {
        pw.println("CHART[0].VAL[" + i + "]:" + counts[i]);
        pw.println("CHART[0].BORDER[" + i + "]:#000066");
        pw.println("CHART[0].FILL[" + i + "]:#F6D66D");
        maxValue = Math.max(maxValue, counts[i]);
      }

      pw.println("CHART[0].MINVALUE:0");
      pw.println("CHART[0].MAXVALUE:" + maxValue);

      pw.println("END:VND.OPENDMDX-CHART");

      try {
        pw.flush();
        os.close();
      } catch(Exception e) {}
      charts[0].values("content").add(
        os.toByteArray()
      );
      charts[0].values("contentMimeType").add("application/vnd.openmdx-chart");
      charts[0].values("contentName").add(
        Utils.toFilename(chartTitle) + ".txt"
      );
      
      /**
       * Contracts Age Distribution
       */
      chartTitle = (fullName.length() == 0 ? "" : fullName + ": ") + "Assigned Open Contracts Age Distribution (" + createdAt + ")";
      
      charts[1] = new DataproviderObject(
        chartReference.getChild("1")
      );
      charts[1].values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:home1:Media");
      charts[1].values("description").add(chartTitle);
      os = new ByteArrayOutputStream();
      pw = new PrintWriter(os);

      pw.println("BEGIN:VND.OPENDMDX-CHART");
      pw.println("VERSION:1.0");
      pw.println("COUNT:1");

      pw.println("CHART[0].TYPE:VERTBAR");
      pw.println("CHART[0].LABEL:" + chartTitle);
      pw.println("CHART[0].SCALEXTITLE:#Days");
      pw.println("CHART[0].SCALEYTITLE:#Contracts");
      pw.println("CHART[0].COUNT:14");
      
      pw.println("CHART[0].LABEL[0]:new");
      pw.println("CHART[0].LABEL[1]:1");
      pw.println("CHART[0].LABEL[2]:2");
      pw.println("CHART[0].LABEL[3]:3");
      pw.println("CHART[0].LABEL[4]:4");
      pw.println("CHART[0].LABEL[5]:5");
      pw.println("CHART[0].LABEL[6]:6");
      pw.println("CHART[0].LABEL[7]:7");
      pw.println("CHART[0].LABEL[8]:..14");
      pw.println("CHART[0].LABEL[9]:..30");
      pw.println("CHART[0].LABEL[10]:..90");
      pw.println("CHART[0].LABEL[11]:..180");
      pw.println("CHART[0].LABEL[12]:..360");
      pw.println("CHART[0].LABEL[13]:>360 days");
      
      maxValue = 0;
      // skip value @ 0: modifiedAt is always < now --> contracts modified
      // in the future should not occur
      for(int i = 0; i < 14; i++) {
        pw.println("CHART[0].VAL[" + i + "]:" + timeDistribution[i+1]);
        pw.println("CHART[0].BORDER[" + i + "]:#000066");
        pw.println("CHART[0].FILL[" + i + "]:#F6D66D");
        maxValue = Math.max(maxValue, timeDistribution[i+1]);
      }

      pw.println("CHART[0].MINVALUE:0");
      pw.println("CHART[0].MAXVALUE:" + maxValue);

      pw.println("END:VND.OPENDMDX-CHART");
      
      try {
        pw.flush();
        os.close();
      } catch(Exception e) {}
      charts[1].values("content").add(
        os.toByteArray()
      );
      charts[1].values("contentMimeType").add("application/vnd.openmdx-chart");
      charts[1].values("contentName").add(
        Utils.toFilename(chartTitle) + ".txt"
      );

      return charts;
    }

    //-------------------------------------------------------------------------
    public boolean allowPositionRemoval(
        Path contractIdentity,
        DataproviderObject_1_0 productRole,
        Path parentPositionIdentity
    ) throws ServiceException {
        List positions = this.backend.getDelegatingRequests().addFindRequest(
            contractIdentity.getChild("position"),
            parentPositionIdentity == null
                ? null
                : new FilterProperty[]{
                      new FilterProperty(
                          Quantors.THERE_EXISTS, 
                          "parentPosition", 
                          FilterOperators.IS_IN, 
                          parentPositionIdentity
                      )
                  },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        int nPositions = 0;
        for(
            Iterator i = positions.iterator(); 
            i.hasNext(); 
        ) {
            DataproviderObject_1_0 pos = (DataproviderObject_1_0)i.next();
            if(productRole.path().equals(pos.values("basedOn").get(0))) {
                nPositions++;
            }
        }
        // If minPositions is not defined for productRole fall back to product type
        // if defined
        if(productRole.values("minPositions").isEmpty()) {
            if(productRole.values("type").size() > 0) {
                DataproviderObject_1_0 productType = this.backend.retrieveObjectFromDelegation(
                    (Path)productRole.values("type").get(0)
                );
                return productType.values("minPositions").size() > 0
                    ? nPositions > ((Number)productType.values("minPositions").get(0)).intValue()
                    : true;
            }
            else {
                return true;
            }
        }
        else {
            return nPositions > ((Number)productRole.values("minPositions").get(0)).intValue();
        }
    }

    //-------------------------------------------------------------------------
    public int getNumberPositionsBasedOnProduct(
        Path contractIdentity,
        DataproviderObject_1_0 product,
        Path parentPositionIdentity,
        List positions
    ) throws ServiceException {
        if(positions == null) {
            positions = this.backend.getDelegatingRequests().addFindRequest(
                contractIdentity.getChild("position"),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
        }
        int nPositions = 0;
        for(
            Iterator i = positions.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 position = (DataproviderObject_1_0)i.next();
            boolean matches = true;
            if(position.values("parentPosition").isEmpty()) {
                matches &= parentPositionIdentity == null;
            }
            else {
                matches &= position.values("parentPosition").get(0).equals(parentPositionIdentity);
            }
            matches &= 
                product.path().equals(position.values("basedOn").get(0));
            if(matches) nPositions++;
        }
        return nPositions; 
    }

    //-------------------------------------------------------------------------
    public boolean allowPositionCreation(
        Path contractIdentity,
        DataproviderObject_1_0 product,
        Path parentPositionIdentity,
        List positions
    ) throws ServiceException {
        int nPositions = this.getNumberPositionsBasedOnProduct(
            contractIdentity,
            product,
            parentPositionIdentity,
            positions
        );
        // If maxPositions is not defined for productBundle fall back to product type
        // if defined
        if(product.values("maxPositions").isEmpty()) {
            if(!product.values("type").isEmpty()) {
                DataproviderObject_1_0 type = this.backend.retrieveObjectFromDelegation(
                    (Path)product.values("type").get(0)
                );
                return type.values("maxPositions").size() > 0
                    ? nPositions < ((Number)type.values("maxPositions").get(0)).intValue()
                    : true;
            }
            else {
                return true;
            }
        }
        else {
            return nPositions < ((Number)product.values("maxPositions").get(0)).intValue();
        }        
    }
    
    //-------------------------------------------------------------------------
    public void removeContractPosition(
        ServiceHeader header,
        DataproviderObject_1_0 position,
        boolean checkForMinPositions
    ) throws ServiceException {
        Path contractIdentity = position.path().getPrefix(position.path().size() - 2);
        Path productRoleIdentity = (Path)position.values("basedOn").get(0);
        if(productRoleIdentity != null) {
            DataproviderObject_1_0 productRole = this.backend.retrieveObjectFromDelegation(productRoleIdentity);
            Path parentPositionIdentity = (Path)position.values("parentPosition").get(0);
            if(checkForMinPositions && !this.allowPositionRemoval(contractIdentity, productRole, parentPositionIdentity)) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.CONTRACT_MIN_POSITIONS_REACHED,
                    new BasicException.Parameter[]{
                         new BasicException.Parameter("param0", productRole.values("minPositions").get(0)),   
                         new BasicException.Parameter("param1", position.values("name").get(0))   
                    },
                    "Removal not allowed. Min positions reached."
                );                                                                            
            }
            // Remove all contained positions
            // NOTE: find request also returns positions with empty parent position
            List positions = this.backend.getDelegatingRequests().addFindRequest(
                contractIdentity.getChild("position"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS, 
                        "parentPosition", 
                        FilterOperators.IS_IN, 
                        position.path()
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            for(
                Iterator i = positions.iterator();
                i.hasNext();
            ) {
                DataproviderObject_1_0 memberPosition = (DataproviderObject_1_0)i.next();
                if(!memberPosition.values("parentPosition").isEmpty()) {
                    this.removeContractPosition(
                        header,
                        memberPosition,
                        false
                    );
                }
            }
        }
        // Make a copy of the removed position
        // position marshallers
        Marshaller positionMarshaller = new Marshaller() {
            public Object marshal(Object s) throws ServiceException {
                DataproviderObject_1_0 position = new DataproviderObject((DataproviderObject_1_0)s);
                position.clearValues(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:RemovedPosition");
                position.attributeNames().remove("estimatedCloseDate");
                position.attributeNames().remove("closeProbability");
                position.attributeNames().remove("parentPosition");
                return position;     
            }
            public Object unmarshal(Object s) {
                throw new UnsupportedOperationException();
            }
        };
        Map objectMarshallers = new HashMap();
        // position marshallers
        objectMarshallers.put("org:opencrx:kernel:contract1:OpportunityPosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:QuotePosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:SalesOrderPosition", positionMarshaller);
        objectMarshallers.put("org:opencrx:kernel:contract1:InvoicePosition", positionMarshaller);
        // clone
        DataproviderObject removedPosition = this.backend.getCloneable().cloneAndUpdateReferences(
            position,
            position.path().getPrefix(position.path().size() - 2).getChild("removedPosition"),
            objectMarshallers,
            DEFAULT_REFERENCE_FILTER,
            true,
            AttributeSelectors.ALL_ATTRIBUTES
        );
        // Update position modifications
        // Replace involved with removed position
        List positionModifications = this.backend.getDelegatingRequests().addFindRequest(
            position.path().getPrefix(position.path().size() - 2).getChild("positionModification"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "involved",
                    FilterOperators.IS_IN,
                    position.path()
                )
            }            
        );
        for(
            Iterator i = positionModifications.iterator(); 
            i.hasNext(); 
        ) {
            DataproviderObject_1_0 positionModification = (DataproviderObject_1_0)i.next();
            DataproviderObject updatedPositionModification = this.backend.retrieveObjectForModification(
                positionModification.path()
            );
            updatedPositionModification.clearValues("involved").add(
                removedPosition.path()
            );
        }
        // Create position modification
        DataproviderObject positionRemoval = new DataproviderObject(
            position.path().getPrefix(position.path().size() - 2).getDescendant(
                new String[]{"positionModification", this.backend.getUidAsString()}                          
            )
        );
        positionRemoval.values(SystemAttributes.OBJECT_CLASS).add(
            "org:opencrx:kernel:contract1:PositionRemoval"
        );
        positionRemoval.values("involved").add(
            removedPosition.path()
        );
        this.backend.createObject(
            positionRemoval
        );        
        // Remove position
        this.backend.removeObject(
            position.path()
        );
        this.markContractAsDirty(
            contractIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * Copy depot references from product role to contract position.
     */
    public Set createDepotReferences(
        Path depotHolderIdentity,
        Path positionIdentity,
        Set excludeDepotUsage
    ) throws ServiceException {
        Set depotUsages = new HashSet();
        List depotReferences = this.backend.getDelegatingRequests().addFindRequest(
            depotHolderIdentity.getChild("depotReference"),
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
            Integer depotUsage = new Integer(((Number)depotReference.values("depotUsage").get(0)).intValue());
            if(!excludeDepotUsage.contains(depotUsage)) {
                this.backend.getCloneable().cloneAndUpdateReferences(
                    depotReference,
                    positionIdentity.getChild("depotReference"),
                    null,
                    "",
                    true,
                    AttributeSelectors.ALL_ATTRIBUTES                    
                );
            }
            depotUsages.add(depotUsage);
        }
        return depotUsages;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Add itemNumber to lineItemNumber. Assert that itemNumber is adjusted
     * to lineItemNumber.
     */
    public long getAdjustedLineItemNumber(
        long itemNumber,
        long lineItemNumber
    ) {
        long d = 1;
        while((lineItemNumber / d) % 100 == 0) {
            d *= 100;
        }
        itemNumber %= d;
        itemNumber = Math.max(1, itemNumber);
        d /= 100;
        d = Math.max(1L, d);
        while(itemNumber / d == 0) {
            itemNumber *= 100;
        }        
        return lineItemNumber + itemNumber;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Remove all pending inventory bookings of contract. Return last
     * final booking or null if no inventory booking is set on the contract
     */
    public CompoundBooking removePendingInventoryBookings(
        Path contractIdentity
    ) throws ServiceException {

        DataproviderObject updatedContract = this.backend.retrieveObjectForModification(
            contractIdentity
        );
        DataproviderObject_1_0 lastFinalInventoryCb = null;
        while(updatedContract.values("inventoryCb").size() > 0) {
            DataproviderObject_1_0 cb = this.backend.retrieveObjectFromDelegation(
                (Path)updatedContract.values("inventoryCb").get(updatedContract.values("inventoryCb").size()-1)
            );
            Number bookingStatus = (Number)cb.values("bookingStatus").get(0);
            if((bookingStatus == null) || (bookingStatus.shortValue() != Depots.BOOKING_STATUS_PROCESSED)) {
                this.backend.getDepots().removeCompoundBooking(
                    cb.path()
                );
                updatedContract.values("inventoryCb").remove(
                    updatedContract.values("inventoryCb").size()-1
                );
            }
            else {
                lastFinalInventoryCb = cb;
                break;
            }
        }
        return lastFinalInventoryCb == null
            ? null
            : (CompoundBooking)this.backend.getDelegatingPkg().refObject(lastFinalInventoryCb.path().toXri());
    }
    
    //-------------------------------------------------------------------------
    public CompoundBooking updateInventory(
        Path contractIdentity
    ) throws ServiceException {

        CompoundBooking lastFinalInventoryCb = 
            this.removePendingInventoryBookings(
                contractIdentity
            );
        // Get all position modifications since the last inventory update marked as final
        Date positionModificationsSince = lastFinalInventoryCb != null
            ? lastFinalInventoryCb.getBookingDate()
            : null;
                
        DataproviderObject_1_0 deliveryDepot = null;
        
        // Test depots of contract. It must have at least
        // a goods delivery depot.
        List depotReferences = this.backend.getDelegatingRequests().addFindRequest(
            contractIdentity.getChild("depotReference"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        boolean hasDepotGoodsDelivery = false;
        for(
            Iterator i = depotReferences.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 depotReference = (DataproviderObject_1_0)i.next();
            Number depotUsage = (Number)depotReference.values("depotUsage").get(0);
            if(!depotReference.values("depot").isEmpty() && (depotUsage != null) && (depotUsage.shortValue() == Depots.DEPOT_USAGE_GOODS_DELIVERY)) {
                hasDepotGoodsDelivery = true;
                deliveryDepot = this.backend.retrieveObject(
                    (Path)depotReference.values("depot").get(0)
                );
            }
        }
        if(!hasDepotGoodsDelivery) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_DELIVERY,
                new BasicException.Parameter[]{
                     new BasicException.Parameter("param0", contractIdentity),   
                },
                "Missing goods delivery depot."
            );                                                                            
        }

        // Test depots of all positions. Each position must have
        // a depot with usage=DEPOT_USAGE_GOODS_ISSUE and 
        // usage=DEPOT_USAGE_GOODS_RETURN
        List allPositions = new ArrayList();
        List positions = this.backend.getDelegatingRequests().addFindRequest(
            contractIdentity.getChild("position"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        for(Iterator i = positions.iterator(); i.hasNext(); ) {
            allPositions.add(i.next());
        }
        positions = this.backend.getDelegatingRequests().addFindRequest(
            contractIdentity.getChild("removedPosition"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        for(Iterator i = positions.iterator(); i.hasNext(); ) {
            allPositions.add(i.next());
        }

        // Create positions
        List issueDepotPositions = new ArrayList();
        List returnDepotPositions = new ArrayList();
        List deliveryDepotPositions = new ArrayList();
        int ii = 0;
        for(
            Iterator i = allPositions.iterator();
            i.hasNext();
            ii++
        ) {
            DataproviderObject_1_0 position = (DataproviderObject_1_0)i.next();  
            String positionType = (String)position.values(SystemAttributes.OBJECT_CLASS).get(0);
            depotReferences = this.backend.getDelegatingRequests().addFindRequest(
                position.path().getChild("depotReference"),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            DataproviderObject_1_0 issueDepot = null;
            DataproviderObject_1_0 returnDepot =  null;
            boolean holderQualifiesPosition = false;
            for(
                Iterator j = depotReferences.iterator();
                j.hasNext();
            ) {
                DataproviderObject_1_0 depotReference = (DataproviderObject_1_0)j.next();
                Number depotUsage = (Number)depotReference.values("depotUsage").get(0);
                if((depotReference.values("depot").size() > 0) && (depotUsage != null) && (depotUsage.shortValue() == Depots.DEPOT_USAGE_GOODS_ISSUE)) {
                    issueDepot = this.backend.retrieveObject(
                        (Path)depotReference.values("depot").get(0)
                    );
                    // The issue depot determines the useDepotPositionQualifier 
                    holderQualifiesPosition = depotReference.values("holderQualifiesPosition").size() > 0
                        ? ((Boolean)depotReference.values("holderQualifiesPosition").get(0)).booleanValue()
                        : false;
                }
                if((depotReference.values("depot").size() > 0) && (depotUsage != null) && (depotUsage.shortValue() == Depots.DEPOT_USAGE_GOODS_RETURN)) {
                    returnDepot = this.backend.retrieveObject(
                        (Path)depotReference.values("depot").get(0)
                    );                    
                }
            }
            if(issueDepot == null) {
                // Removed positions with no valid depot references can not have inventory bookings.
                // The position can be ignored.
                if(this.backend.getModel().isSubtypeOf(positionType, "org:opencrx:kernel:contract1:AbstractRemovedPosition")) {
                    i.remove();
                    continue;
                }
                else {
                    throw new ServiceException(
                        OpenCrxException.DOMAIN,
                        OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_ISSUE,
                        new BasicException.Parameter[]{
                             new BasicException.Parameter("param0", position.values("lineItemNumber").get(0)),   
                        },
                        "Missing goods issue depot."
                    );
                }
            }
            if(returnDepot == null) {
                // Removed positions with no valid depot references can not have inventory bookings.
                // The position can be ignored.
                if(this.backend.getModel().isSubtypeOf(positionType, "org:opencrx:kernel:contract1:AbstractRemovedPosition")) {
                    i.remove();
                    continue;
                }
                else {
                    throw new ServiceException(
                        OpenCrxException.DOMAIN,
                        OpenCrxException.CONTRACT_MISSING_DEPOT_GOODS_RETURN,
                        new BasicException.Parameter[]{
                             new BasicException.Parameter("param0", position.values("lineItemNumber").get(0)),   
                        },
                        "Missing goods return depot."
                    );
                }
            }

            // delivery depot position
            deliveryDepotPositions.add( 
                this.backend.getDepots().openDepotPosition(
                    deliveryDepot,
                    null,
                    null,
                    new Date(),
                    holderQualifiesPosition ? (String)position.values("positionNumber").get(0) : null,
                    (Path)position.values("basedOn").get(0),
                    null,
                    Boolean.FALSE
                )
            );

            // issue depot position
            issueDepotPositions.add( 
                this.backend.getDepots().openDepotPosition(
                    issueDepot,
                    null,
                    null,
                    new Date(),
                    holderQualifiesPosition ? (String)position.values("positionNumber").get(0) : null,
                    (Path)position.values("basedOn").get(0),
                    null,
                    Boolean.FALSE
                )
            );

            // return depot position
            returnDepotPositions.add(
                this.backend.getDepots().openDepotPosition(
                    returnDepot,
                    null,
                    null,
                    new Date(),
                    holderQualifiesPosition ? (String)position.values("positionNumber").get(0) : null,
                    (Path)position.values("basedOn").get(0),
                    null,
                    Boolean.FALSE
                )
            );
        }
        
        // Create booking        
        List creditPositions = new ArrayList();
        List debitPositions = new ArrayList();
        List quantities = new ArrayList();
        List bookingTextNames = new ArrayList();
        List productConfigurationSets = new ArrayList();
        List originIdentities = new ArrayList();
        
        ii = 0;
        for(
            Iterator i = allPositions.iterator();
            i.hasNext();
            ii++
        ) {
            DataproviderObject_1_0 position = (DataproviderObject_1_0)i.next();
            
            // Get all modifications of this position since positionModificationsSince ordered by creation date
            List positionModifications = this.backend.getDelegatingRequests().addFindRequest(
                contractIdentity.getChild("positionModification"),
                positionModificationsSince == null
                    ? new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "involved",
                            FilterOperators.IS_IN,
                            position.path()
                        )
                      }
                    : new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "involved",
                            FilterOperators.IS_IN,
                            position.path()
                        ),
                        new FilterProperty(
                            Quantors.THERE_EXISTS,                        
                            SystemAttributes.CREATED_AT,
                            FilterOperators.IS_GREATER,
                            org.openmdx.base.text.format.DateFormat.getInstance().format(positionModificationsSince)
                        )
                    },
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{
                    new AttributeSpecifier(
                        SystemAttributes.CREATED_AT,
                        0,
                        Orders.ASCENDING
                    )
                },
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );

            BigDecimal quantityBeforeFirstModification = new BigDecimal(0.0);
            for(
                Iterator j = positionModifications.iterator(); 
                j.hasNext(); 
            ) {
                DataproviderObject_1_0 positionModification = (DataproviderObject_1_0)j.next();
                String positionModificationType = (String)positionModification.values(SystemAttributes.OBJECT_CLASS).get(0);
                if("org:opencrx:kernel:contract1:QuantityModification".equals(positionModificationType)) {
                    quantityBeforeFirstModification = ((BigDecimal)position.values("quantity").get(0)).subtract(
                        (BigDecimal)positionModification.values("quantity").get(0)
                    );
                    break;
                }
                else if("org:opencrx:kernel:contract1:PositionCreation".equals(positionModificationType)) {
                    quantityBeforeFirstModification = (BigDecimal)position.values("quantity").get(0);
                    break;
                }
                else if("org:opencrx:kernel:contract1:PositionRemoval".equals(positionModificationType)) {
                    quantityBeforeFirstModification = ((BigDecimal)position.values("quantity").get(0)).negate();
                    break;
                }
            }
            // No inventory booking if quantity == 0
            if(quantityBeforeFirstModification.signum() != 0) {
                if(quantityBeforeFirstModification.signum() < 0) {
                    creditPositions.add(
                        ((DataproviderObject_1_0)returnDepotPositions.get(ii)).path()
                    );
                    debitPositions.add(
                        ((DataproviderObject_1_0)deliveryDepotPositions.get(ii)).path()
                    );
                    bookingTextNames.add(
                        BOOKING_TEXT_NAME_RETURN_GOODS
                    );
                }
                else {
                    creditPositions.add(
                        ((DataproviderObject_1_0)deliveryDepotPositions.get(ii)).path()
                    );
                    debitPositions.add(
                        ((DataproviderObject_1_0)issueDepotPositions.get(ii)).path()
                    );
                    bookingTextNames.add(
                        BOOKING_TEXT_NAME_DELIVER_GOODS
                    );
                }
                String contractPositionType = (String)position.values(SystemAttributes.OBJECT_CLASS).get(0);                    
                productConfigurationSets.add(
                    this.backend.getModel().isSubtypeOf(contractPositionType, "org:opencrx:kernel:product1:ProductDescriptor")
                        ? position.path()
                        : null
                );
                originIdentities.add(
                    position.path()
                );
                quantities.add(
                    quantityBeforeFirstModification.abs()
                );
            }
        }
        if(!quantities.isEmpty()) {
            DataproviderObject_1_0 inventoryCb = 
                this.backend.getDepots().createCompoundBooking(
                    deliveryDepot.path().getPrefix(7),
                    new Date(),
                    Depots.BOOKING_TYPE_STANDARD,
                    (BigDecimal[])quantities.toArray(new BigDecimal[quantities.size()]),
                    (String[])bookingTextNames.toArray(new String[bookingTextNames.size()]),
                    null,
                    (Path[])creditPositions.toArray(new Path[creditPositions.size()]),
                    (Path[])debitPositions.toArray(new Path[debitPositions.size()]),
                    (Path[])productConfigurationSets.toArray(new Path[productConfigurationSets.size()]),
                    (Path[])originIdentities.toArray(new Path[originIdentities.size()]),
                    null
                );        
            DataproviderObject updatedContract = this.backend.retrieveObjectForModification(
                contractIdentity
            );
            updatedContract.values("inventoryCb").add(
                inventoryCb.path()
            );
            return inventoryCb == null
                ? null
                : (CompoundBooking)this.backend.getDelegatingPkg().refObject(inventoryCb.path().toXri());
        }
        else {
            return null;
        }
    }
    
    //-------------------------------------------------------------------------
    public void updateContractPosition(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        // Contract position
        if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:contract1:ContractPosition")) {
            Path productIdentity = null;
            if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:product1:ConfiguredProduct")) {
                productIdentity = (object.getValues("product") != null) && (object.values("product").size() > 0)
                    ? (Path)object.values("product").get(0)
                    : (Path)oldValues.values("product").get(0);                
            }
            else {
                productIdentity = (object.getValues("basedOn") != null) && (object.values("basedOn").size() > 0)
                    ? (Path)object.values("basedOn").get(0)
                    : (Path)oldValues.values("basedOn").get(0);                                
            }
            DataproviderObject contract = this.backend.retrieveObjectForModification(
                object.path().getPrefix(7)
            );
            if(productIdentity != null) {
                DataproviderObject_1_0 product = this.backend.retrieveObjectFromDelegation(productIdentity);                
                this.updateContractPosition(
                    contract,
                    object,
                    oldValues,
                    product,
                    false
                );
            }
            this.markContractAsDirty(
                contract.path()
            );
        }
        // Configuration property of configured product on contract position
        else if(object.path().isLike(CONTRACT_POSITION_CONFIGURATION_PROPERTY)) {
            DataproviderObject_1_0 contract = this.backend.retrieveObject(
                object.path().getPrefix(7)
            );
            this.updateContractPositionConfigurationProperty(
                contract,
                object,
                oldValues
            );
        }
        
    }

    //-------------------------------------------------------------------------
    public void updateContract(
        ServiceHeader header,
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:contract1:AbstractContract")) {
            this.completeContract(
                object,
                null
            );
        }
    }

    //-------------------------------------------------------------------------
    /**
     * @return new pricingState of contract position
     */
    public short repriceContractPosition(
        Path contractPositionIdentity
    ) throws ServiceException {
        DataproviderObject_1_0 contractPosition = this.backend.retrieveObject(
            contractPositionIdentity
        );
        String objectClass = (String)contractPosition.values(SystemAttributes.OBJECT_CLASS).get(0);
        short pricingState = PRICING_STATE_NA;
        if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:contract1:ContractPosition")) {
            Path productIdentity = null;
            if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:product1:ProductDescriptor")) {
                productIdentity = (Path)contractPosition.values("product").get(0);
            }
            if(productIdentity != null) {
                DataproviderObject_1_0 product = this.backend.retrieveObjectFromDelegation(productIdentity);                
                DataproviderObject_1_0 contract = this.backend.retrieveObject(
                    contractPosition.path().getParent().getParent()
                );
                DataproviderObject position = this.backend.retrieveObjectForModification(
                    contractPosition.path()
                );
                // Force recalc of priceLevel
                position.attributeNames().remove("priceLevel");
                this.updateContractPosition(
                    contract,
                    position,
                    contractPosition,
                    product,
                    true
                );
                if(position.values("pricingState").size() > 0) {
                    pricingState = ((Number)position.values("pricingState").get(0)).shortValue();
                }
            }
        }
        return pricingState;
    }
    
    //-------------------------------------------------------------------------
    public void repriceContract(
        Path contractIdentity
    ) throws ServiceException {
        List positions = this.backend.getDelegatingRequests().addFindRequest(
            contractIdentity.getChild("position"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        short pricingState = PRICING_STATE_OK;
        for(Iterator i = positions.iterator(); i.hasNext(); ) {
            DataproviderObject_1_0 position = (DataproviderObject_1_0)i.next();
            short pricingStatePosition = this.repriceContractPosition(
                position.path()
            );
            if(pricingStatePosition == PRICING_STATE_DIRTY) {
                pricingState = PRICING_STATE_DIRTY;
            }
        }
        DataproviderObject contract = this.backend.retrieveObjectForModification(
            contractIdentity
        );
        contract.clearValues("pricingState").add(
            new Short(pricingState)
        );
    }

    //-------------------------------------------------------------------------
    public void replaceContractPosition(
        DataproviderRequest request
    ) throws ServiceException {
        // Set pricingState to dirty in case pricePerUnit is modified
        if(request.object().getValues("pricePerUnit") != null) {
            DataproviderObject newValues = request.object();
            DataproviderObject position = this.backend.retrieveObjectForModification(
                request.path()
            );
            boolean priceIsModified = (newValues.getValues("pricePerUnit") == null) || (newValues.values("pricePerUnit").get(0) == null) || (position.getValues("pricePerUnit") == null) || (position.values("pricePerUnit").get(0) == null)
                ? newValues.values("pricePerUnit").get(0) != position.values("pricePerUnit").get(0)
                : ((BigDecimal)newValues.values("pricePerUnit").get(0)).compareTo((BigDecimal)position.values("pricePerUnit").get(0)) != 0;
            if(priceIsModified) {
                DataproviderObject contract = this.backend.retrieveObjectForModification(
                    position.path().getPrefix(position.path().size()-2)
                );
                position.clearValues("pricingState").add(
                    new Short(PRICING_STATE_DIRTY)
                );
                contract.clearValues("pricingState").add(
                    new Short(PRICING_STATE_DIRTY)
                );
            }
        }
    }

    //-------------------------------------------------------------------------
    public FilterProperty[] getContractFilterProperties(
        Path contractFilterIdentity,
        boolean forCounting
    ) throws ServiceException {
        List filterProperties = this.backend.getDelegatingRequests().addFindRequest(
            contractFilterIdentity.getChild("filterProperty"),
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
            Iterator<DataproviderObject_1_0> i = filterProperties.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 filterProperty = i.next();
            String filterPropertyClass = (String)filterProperty.values(SystemAttributes.OBJECT_CLASS).get(0);

            Boolean isActive = (Boolean)filterProperty.values("isActive").get(0);            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:contract1:ContractQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterProperty.values("clause").get(0)
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList();
                    for(
                        Iterator<String> j = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList();
                    for(
                        Iterator<String> j = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
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
                    
                    if("org:opencrx:kernel:contract1:ContractTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                SystemAttributes.OBJECT_CLASS,
                                filterOperator,
                                filterProperty.values("contractType").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:ContractStateFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "contractState",
                                filterOperator,
                                filterProperty.values("contractState").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:ContractPriorityFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "priority",
                                filterOperator,
                                filterProperty.values("priority").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:TotalAmountFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "totalAmount",
                                filterOperator,
                                filterProperty.values("totalAmount").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:CustomerFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "customer",
                                filterOperator,
                                filterProperty.values("customer").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:SupplierFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "supplier",
                                filterOperator,
                                filterProperty.values("supplier").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:SalesRepFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "salesRep",
                                filterOperator,
                                filterProperty.values("salesRep").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:DisabledFilterProperty".equals(filterPropertyClass)) {
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
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }
        return (FilterProperty[])filter.toArray(new FilterProperty[filter.size()]);
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredContract(
        Path contractFilterIdentity
    ) throws ServiceException {
        List contracts = this.backend.getDelegatingRequests().addFindRequest(
            contractFilterIdentity.getChild("filteredContract"),
            this.getContractFilterProperties(
                contractFilterIdentity, 
                true
            ),
            AttributeSelectors.NO_ATTRIBUTES,
            null,
            0, 
            1,
            Directions.ASCENDING
        );
        return contracts.size();
    }
            
    //-------------------------------------------------------------------------
    public static org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult getPositionAmounts(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,
        org.opencrx.kernel.contract1.jmi1.ContractPosition position,
        java.math.BigDecimal minMaxAdjustedQuantity,
        java.math.BigDecimal uomScaleFactor,
        java.math.BigDecimal salesTaxRate
    ) {
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg =
            (org.opencrx.kernel.contract1.jmi1.Contract1Package)rootPkg.refPackage(
                org.opencrx.kernel.contract1.jmi1.Contract1Package.class.getName()
            );
        java.math.BigDecimal pricePerUnit = position.getPricePerUnit();
        java.math.BigDecimal baseAmount = minMaxAdjustedQuantity.multiply(pricePerUnit.multiply(uomScaleFactor));
        // discount
        Boolean discountIsPercentage = position.isDiscountIsPercentage() != null 
            ? position.isDiscountIsPercentage()
            : Boolean.FALSE;
        java.math.BigDecimal discount = position.getDiscount() != null
            ? position.getDiscount()
            : new java.math.BigDecimal(0);
        // Discount is per piece in case of !discountIsPercentage
        java.math.BigDecimal discountAmount = discountIsPercentage.booleanValue()
            ? baseAmount.multiply(discount.divide(new java.math.BigDecimal(100.0), java.math.BigDecimal.ROUND_UP))
            : minMaxAdjustedQuantity.multiply(discount.multiply(uomScaleFactor));
        // taxAmount
        java.math.BigDecimal taxAmount = baseAmount.subtract(discountAmount).multiply(
            salesTaxRate.divide(new java.math.BigDecimal(100), java.math.BigDecimal.ROUND_UP)
        );    
        // amount
        java.math.BigDecimal amount = baseAmount.subtract(discountAmount).add(taxAmount);      
        org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult result = contractPkg.createGetPositionAmountsResult(
            amount, baseAmount, discountAmount,
            (short)0,
            null,
            taxAmount
        );
        return result;
    }

    //-------------------------------------------------------------------------
    public static org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult getContractAmounts(
        org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,
        org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,
        org.opencrx.kernel.contract1.jmi1.AbstractContract contract,
        java.lang.Integer[] lineItemNumbers,
        java.math.BigDecimal[] positionBaseAmounts,
        java.math.BigDecimal[] positionDiscountAmounts,
        java.math.BigDecimal[] positionTaxAmounts,
        java.math.BigDecimal[] positionAmounts,
        java.math.BigDecimal[] salesCommissions,
        java.lang.Boolean[] salesCommissionIsPercentages
    ) {
        org.opencrx.kernel.contract1.jmi1.Contract1Package contractPkg =
            (org.opencrx.kernel.contract1.jmi1.Contract1Package)rootPkg.refPackage(
                org.opencrx.kernel.contract1.jmi1.Contract1Package.class.getName()
            );
        java.math.BigDecimal totalBaseAmount = new java.math.BigDecimal(0);
        java.math.BigDecimal totalDiscountAmount = new java.math.BigDecimal(0);
        java.math.BigDecimal totalTaxAmount = new java.math.BigDecimal(0);
        java.math.BigDecimal totalSalesCommission = new java.math.BigDecimal(0);
        for(int i = 0; i < positionBaseAmounts.length; i++) {
            java.math.BigDecimal baseAmount = positionBaseAmounts[i] != null
              ? positionBaseAmounts[i]
               : new java.math.BigDecimal(0); 
            totalBaseAmount = totalBaseAmount.add(baseAmount);
            java.math.BigDecimal discountAmount = positionDiscountAmounts[i] != null
              ? positionDiscountAmounts[i]
               : new java.math.BigDecimal(0);
            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            java.math.BigDecimal taxAmount = positionTaxAmounts[i] != null
              ? positionTaxAmounts[i]
              : new java.math.BigDecimal(0);
            totalTaxAmount = totalTaxAmount.add(taxAmount);
            java.math.BigDecimal salesCommission = salesCommissions[i] != null
              ? salesCommissions[i]
              : new java.math.BigDecimal(0);
            totalSalesCommission = totalSalesCommission.add(
              (salesCommissionIsPercentages[i] != null) &&
              (((Boolean)salesCommissionIsPercentages[i]).booleanValue())
              ? baseAmount.subtract(discountAmount).multiply(salesCommission.divide(new java.math.BigDecimal(100), java.math.BigDecimal.ROUND_UP))
              : salesCommission
            );
        }
        java.math.BigDecimal totalAmount = totalBaseAmount.subtract(totalDiscountAmount);
        java.math.BigDecimal totalAmountIncludingTax = totalAmount.add(totalTaxAmount);
        org.opencrx.kernel.contract1.jmi1.GetContractAmountsResult result = contractPkg.createGetContractAmountsResult(
            (short)0,
            null,
            totalAmount, totalAmountIncludingTax, totalBaseAmount, totalDiscountAmount, totalSalesCommission, totalTaxAmount
        );
        return result;
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    private static final String DEFAULT_REFERENCE_FILTER = null;
    
    private static final Path CONTRACT_POSITION_CONFIGURATION_PROPERTY = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/configuration/:*/property/:*");
    
    private final static int BATCHING_MODE_SIZE = 1000;
    
    private final static short STATUS_CODE_OK = 0;
    private final static short STATUS_CODE_ERROR = 1;
    
    // Closed Thresholds
    private static final short CLOSED_THRESHOLD_LEAD = 1110;
    private static final short CLOSED_THRESHOLD_OPPORTUNITY = 1210;
    private static final short CLOSED_THRESHOLD_QUOTE = 1310;
    private static final short CLOSED_THRESHOLD_SALES_ORDER = 1410;
    private static final short CLOSED_THRESHOLD_INVOICE = 1510;

    // Min/Max Quantity Handling
    private static final int MIN_MAX_QUANTITY_HANDLING_NA = 0;
    private static final int MIN_MAX_QUANTITY_HANDLING_INFORMATIONAL = 1;
    private static final int MIN_MAX_QUANTITY_HANDLING_STRICT = 2;
    private static final int MIN_MAX_QUANTITY_HANDLING_LIMIT = 3;
    
    // Pricing Status
    private static final short PRICING_STATE_NA = 0;
    private static final short PRICING_STATE_DIRTY = 10;
    private static final short PRICING_STATE_OK = 20;
    
    // Booking texts
    private static final String BOOKING_TEXT_NAME_RETURN_GOODS = "return goods";
    private static final String BOOKING_TEXT_NAME_DELIVER_GOODS = "deliver goods";
            
    public static final String CALCULATION_RULE_NAME_DEFAULT = "Default";
        
    public static final String DEFAULT_GET_POSITION_AMOUNTS_SCRIPT = 
        "//<pre>\n" + 
        "public static org.opencrx.kernel.contract1.jmi1.GetPositionAmountsResult getPositionAmounts(\n" + 
        "    org.openmdx.base.accessor.jmi.cci.RefPackage_1_0 rootPkg,\n" +
        "    org.opencrx.kernel.contract1.jmi1.CalculationRule calculationRule,\n" +  
        "    org.opencrx.kernel.contract1.jmi1.ContractPosition position,\n" +
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

    private final Backend backend;
    protected final Cloneable cloning;
    protected final Map cachedObjects = new HashMap();
    protected final Map cachedDescriptions = new HashMap();
        
}

//--- End of File -----------------------------------------------------------

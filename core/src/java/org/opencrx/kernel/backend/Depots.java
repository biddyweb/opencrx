/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Depots.java,v 1.22 2009/02/20 21:44:45 wfro Exp $
 * Description: Depots
 * Revision:    $Revision: 1.22 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/20 21:44:45 $
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.product1.jmi1.Product;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.Directions;
import org.openmdx.application.dataprovider.cci.Orders;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;

public class Depots {

    //-----------------------------------------------------------------------
    public Depots(
        Backend backend
    ) {
        this.backend = backend;
    }

    //-----------------------------------------------------------------------
    private String stripWhitespaces(
        String s
    ) {
        String stripped = "";
        for(int i = 0; i < s.length(); i++) {
            if(!Character.isWhitespace(s.charAt(i))) {
                stripped += s.charAt(i);
            }
        }
        return stripped;
    }
    
    //-----------------------------------------------------------------------
    public void testForOpenPosition(
        Date valueDate,
        short bookingType,
        Path positionIdentity
    ) throws ServiceException {
        
        List bookingPeriods = this.backend.getDelegatingRequests().addFindRequest(
            positionIdentity.getPrefix(7).getChild("bookingPeriod"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );                
        DataproviderObject_1_0 depot = this.backend.retrieveObject(positionIdentity.getParent().getParent());  
        String depotNumber = (String)depot.values("depotNumber").get(0);
        DataproviderObject_1_0 depotPosition = this.backend.retrieveObject(positionIdentity);  
        String positionName = (String)depotPosition.values("name").get(0);
        
        // Check for depot position isLocked
        if(
            (depotPosition.values("isLocked").size() > 0) && 
            ((Boolean)depotPosition.values("isLocked").get(0)).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_IS_LOCKED,
                "Depot position is locked",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", positionName)
            );
        }
        // Check for closing date of depot position
        if(
            !depotPosition.values("closingDate").isEmpty() && 
            (valueDate.compareTo(this.backend.parseDate((String)depotPosition.values("closingDate").get(0))) >= 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_IS_CLOSED,
                "Depot position is closed",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", positionName),
                new BasicException.Parameter("param2", depotPosition.values("closingDate").get(0))
            );
        }
        // Check for opening date of depot position
        if(
            !depotPosition.values("openingDate").isEmpty() && 
            (valueDate.compareTo(this.backend.parseDate((String)depotPosition.values("openingDate").get(0))) < 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_IS_NOT_OPEN,
                "Depot position is not open",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", positionName),
                new BasicException.Parameter("param2", depotPosition.values("openingDate").get(0))
            );
        }
        
        // Check for depot isLocked
        if(
            !depot.values("isLocked").isEmpty() && 
            ((Boolean)depot.values("isLocked").get(0)).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_LOCKED,
                "Depot is locked",
                new BasicException.Parameter("param0", depotNumber)
            );
        }
        // Check for closing date of depot
        if(
            !depot.values("closingDate").isEmpty() && 
            (valueDate.compareTo(this.backend.parseDate((String)depot.values("closingDate").get(0))) >= 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_CLOSED_CAN_NOT_BOOK,
                "Depot is closed",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", depot.values("closingDate").get(0))
            );
        }
        // Check for opening date of depot
        if(
            !depot.values("openingDate").isEmpty() && 
            (valueDate.compareTo(this.backend.parseDate((String)depot.values("openingDate").get(0))) < 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_NOT_OPEN,
                "Depot is not open",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", depot.values("openingDate").get(0))                
            );
        }
        // Find booking period matching value date
        DataproviderObject_1_0 bookingPeriod = null;
        for(
            Iterator i = bookingPeriods.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 period = (DataproviderObject_1_0)i.next();
            Date periodStartsAt = this.backend.parseDate((String)period.values("periodStartsAt").get(0));
            Date periodEndsAtExclusive = this.backend.parseDate((String)period.values("periodEndsAtExclusive").get(0));            
            if(
                ((periodStartsAt == null) || (valueDate.compareTo(periodStartsAt) >= 0)) &&
                ((periodEndsAtExclusive == null) || (valueDate.compareTo(periodEndsAtExclusive) < 0))
            ) {
                bookingPeriod = period;
                break;
            }
        }
        if(bookingPeriod == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_BOOKING_PERIOD_NOT_FOUND,
                "No booking period found for value date",
                new BasicException.Parameter("param0", valueDate)
            );
        }
        String bookingPeriodName = (String)bookingPeriod.values("name").get(0);        
        // Check for non-final booking period
        if(
            (bookingPeriod.values("isFinal").size() > 0) && 
            ((Boolean)bookingPeriod.values("isFinal").get(0)).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_BOOKING_PERIOD_IS_FINAL,
                "Booking period is final",
                new BasicException.Parameter("param0", bookingPeriodName)
            );
        }               
        // Check for non-closed booking period
        if(
            (bookingPeriod.values("isClosed").size() > 0) && 
            ((Boolean)bookingPeriod.values("isClosed").get(0)).booleanValue() &&
            (bookingPeriod.values("closingBookingTypeThreshold").size() > 0) &&
            bookingType <= ((Number)bookingPeriod.values("closingBookingTypeThreshold").get(0)).intValue() 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_BOOKING_PERIOD_IS_CLOSED,
                "Booking period is closed",
                new BasicException.Parameter("param0", bookingPeriodName),
                new BasicException.Parameter("param1", bookingPeriod.values("closingBookingTypeThreshold").get(0))
            );
        }
    }
    
    //-----------------------------------------------------------------------
    public void testForBalance(
        Path compoundBookingIdentity,
        BigDecimal balance
    ) throws ServiceException {
        List creditBookings = this.backend.getDelegatingRequests().addFindRequest(
            compoundBookingIdentity.getPrefix(compoundBookingIdentity.size()-2).getChild("booking"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    SystemAttributes.OBJECT_CLASS,
                    FilterOperators.IS_IN,
                    "org:opencrx:kernel:depot1:CreditBooking"
                ),
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "cb",
                    FilterOperators.IS_IN,
                    compoundBookingIdentity
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        BigDecimal compoundBalance = new BigDecimal(0);
        for(
            Iterator i = creditBookings.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 booking = (DataproviderObject_1_0)i.next();
            compoundBalance = compoundBalance.add(
                (BigDecimal)booking.values("quantityCredit").get(0)
            );
        }
        if(compoundBalance.compareTo(balance) != 0) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_REVERSAL_BALANCE_MISMATCH,
                "Balance mismatch",
                new BasicException.Parameter("param0", compoundBalance),
                new BasicException.Parameter("param1", balance)
            );            
        }
    }
    
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 createCreditDebitBooking(
        Path depotEntityIdentity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        Path bookingTextIdentity,
        Path positionCreditIdentity,
        Path positionDebitIdentity,
        Path productConfigurationSetIdentity,
        Path originIdentity,
        Path reversalOf
    ) throws ServiceException {        
        return this.createCompoundBooking(
            depotEntityIdentity,
            valueDate,
            bookingType,
            new BigDecimal[]{quantity},
            new String[]{bookingTextName},
            new Path[]{bookingTextIdentity},
            new Path[]{positionCreditIdentity},
            new Path[]{positionDebitIdentity},
            new Path[]{productConfigurationSetIdentity},
            new Path[]{originIdentity},
            reversalOf
        );
    }
    
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 createCompoundBooking(
        Path depotEntityIdentity,
        Date valueDate,
        short bookingType,
        BigDecimal[] quantities,
        String[] bookingTextNames,
        Path[] bookingTextIdentities,
        Path[] creditPositionIdentities,
        Path[] debitPositionIdentities,
        Path[] productConfigurationSetIdentities,
        Path[] originIdentities,
        Path reversalOf
    ) throws ServiceException {

        // Set default valueDate to current date
        if(valueDate == null) {
            valueDate = new Date();
        }
        
        // Assert open positions
        for(int i = 0; i < creditPositionIdentities.length; i++) {
            this.testForOpenPosition(
                valueDate,
                bookingType,
                creditPositionIdentities[i] 
            );
            this.testForOpenPosition(
                valueDate,
                bookingType,
                debitPositionIdentities[i] 
            );
        }
        
        // Test for matching balance
        BigDecimal balance = new BigDecimal(0);
        for(int i = 0; i < quantities.length; i++) {
            if(quantities[i] == null) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.DEPOT_MISSING_QUANTITY,
                    "Missing quantity"
                );
            }
            balance = balance.add(quantities[i]);
        }
        if(reversalOf != null) {
            this.testForBalance(
                reversalOf,
                balance
            );
        }
        
        // bookingText
        DataproviderObject_1_0[] bookingTexts = new DataproviderObject_1_0[bookingTextNames.length];     
        for(int i = 0; i < bookingTextNames.length; i++) {             
            if((bookingTextIdentities != null) && (bookingTextIdentities[i] != null)) {
                bookingTexts[i] = this.backend.retrieveObject(bookingTextIdentities[i]);
            }
            else {
                if(bookingTextNames[i] != null) {
                    List texts = this.backend.getDelegatingRequests().addFindRequest(
                        depotEntityIdentity.getChild("bookingText"),
                        new FilterProperty[]{
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "name",
                                FilterOperators.IS_IN,
                                bookingTextNames[i]                            
                            )
                        },
                        AttributeSelectors.ALL_ATTRIBUTES,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING                    
                    );
                    if(texts.size() > 0) {
                        bookingTexts[i] = (DataproviderObject_1_0)texts.get(0);
                    }
                }            
            }
            if(bookingTexts[i] == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_BOOKING_TEXT,
                   "Missing booking text"
               );
            }
        }
        // creditPositions
        DataproviderObject_1_0[] creditPositions = new DataproviderObject_1_0[creditPositionIdentities.length];
        for(int i = 0; i < creditPositionIdentities.length; i++) {
            if(creditPositionIdentities[i] != null) {
                creditPositions[i] = this.backend.retrieveObject(
                    creditPositionIdentities[i]
                );
            }
            if(creditPositions[i] == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_POSITION_CREDIT,
                   "Missing credit position"
               );            
            }
        }
        // debitPositions
        DataproviderObject_1_0[] debitPositions = new DataproviderObject_1_0[debitPositionIdentities.length];
        for(int i = 0; i < debitPositionIdentities.length; i++) {
            if(debitPositionIdentities[i] != null) {
                debitPositions[i] = this.backend.retrieveObject(
                    debitPositionIdentities[i]
                );
            }
            if(debitPositions[i] == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_POSITION_DEBIT,
                   "Missing debit position"
               );            
            }
        }        
        // Position name
        for(int i = 0; i < creditPositions.length; i++) {
            if(
                (creditPositions[i].values("name").get(0) == null) ||
                (debitPositions[i].values("name").get(0) == null) ||
                !creditPositions[i].values("name").get(0).equals(debitPositions[i].values("name").get(0))
            ) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_POSITION_NAME_MISMATCH,
                   "position names debit/credit do not match",
                   new BasicException.Parameter("param0", creditPositions[i].values("name").get(0)),
                   new BasicException.Parameter("param1", debitPositions[i].values("name").get(0))
               );                            
            }
        }
        // Value date
        if(valueDate == null) {
            valueDate = new Date();
        }
        Date bookingDate = new Date();

        // Create compound booking
        DataproviderObject compoundBooking = new DataproviderObject(
            depotEntityIdentity.getPrefix(
                depotEntityIdentity.size()-2
            ).getDescendant(
                new String[]{"cb", this.backend.getUidAsString()}
            )
        );
        compoundBooking.values(SystemAttributes.OBJECT_CLASS).add(
            "org:opencrx:kernel:depot1:CompoundBooking"
        );
        // name
        String positionName0 = (String)creditPositions[0].values("name").get(0);            
        
        // depotCredit
        DataproviderObject_1_0 depotCredit0 = this.backend.retrieveObject(
            creditPositions[0].path().getPrefix(creditPositions[0].path().size()-2)
        );
        String depotNumberCredit0 = (String)depotCredit0.values("depotNumber").get(0);

        // depotDebit
        DataproviderObject_1_0 depotDebit0 = this.backend.retrieveObject(
            debitPositions[0].path().getPrefix(debitPositions[0].path().size()-2)
        );
        String depotNumberDebit0 = (String)depotDebit0.values("depotNumber").get(0);        
        if(
            (bookingTexts[0].values("creditFirst").get(0) == null) ||
            !((Boolean)bookingTexts[0].values("creditFirst").get(0)).booleanValue()
        ) {
            compoundBooking.values("name").add(
                depotNumberCredit0 + " " + bookingTexts[0].values("cbNameInfix1").get(0) + " " + positionName0 + " " + bookingTexts[0].values("cbNameInfix2").get(0) + " " + depotNumberDebit0  
            );
        }
        else {
            compoundBooking.values("name").add(
                depotNumberDebit0 + " " + bookingTexts[0].values("cbNameInfix1").get(0) + " " + positionName0 + " " + bookingTexts[0].values("cbNameInfix2").get(0) + " " + depotNumberCredit0 
            );            
        }
        compoundBooking.values("bookingType").add(new Short(bookingType));
        compoundBooking.values("bookingStatus").add(new Short(BOOKING_STATUS_PENDING));
        compoundBooking.values("bookingDate").add(
            DateFormat.getInstance().format(bookingDate)
        );
        if(reversalOf != null) {            
            compoundBooking.values("reversalOf").add(reversalOf);
        }
        // Create compound booking
        this.backend.getDelegatingRequests().addCreateRequest(
            compoundBooking
        );
            
        // Create bookings
        for(
            int i = 0;
            i < creditPositions.length;
            i++
        ) {
            String positionName = (String)creditPositions[i].values("name").get(0);            
            
            // depotCredit
            DataproviderObject_1_0 depotCredit = this.backend.retrieveObject(
                creditPositions[i].path().getPrefix(creditPositions[i].path().size()-2)
            );
            String depotNumberCredit = (String)depotCredit.values("depotNumber").get(0);
    
            // depotDebit
            DataproviderObject_1_0 depotDebit = this.backend.retrieveObject(
                debitPositions[i].path().getPrefix(debitPositions[i].path().size()-2)
            );
            String depotNumberDebit = (String)depotDebit.values("depotNumber").get(0);
    
            // Create credit booking
            DataproviderObject bookingCredit = new DataproviderObject(
                depotEntityIdentity.getPrefix(
                    depotEntityIdentity.size()-2
                ).getDescendant(
                    new String[]{"booking", this.backend.getUidAsString()}
                )
            );
            bookingCredit.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:CreditBooking"
            );
            bookingCredit.values("name").add(
                depotNumberCredit + " " + bookingTexts[i].values("creditBookingNameInfix").get(0) + " " + positionName
            );
            bookingCredit.values("quantityCredit").add(quantities[i]);
            bookingCredit.values("valueDate").add(
                DateFormat.getInstance().format(valueDate)
            );
            bookingCredit.values("bookingType").add(new Short(bookingType));
            bookingCredit.values("bookingStatus").add(new Short(BOOKING_STATUS_PENDING));
            bookingCredit.values("bookingDate").add(
                DateFormat.getInstance().format(bookingDate)
            );
            bookingCredit.values("cb").add(compoundBooking.path());
            bookingCredit.values("position").add(creditPositions[i].path());
            if(originIdentities[i] != null) {
                bookingCredit.values("origin").add(originIdentities[i]);
            }
            this.backend.getDelegatingRequests().addCreateRequest(bookingCredit);
            
            // Clone product configurations
            if(productConfigurationSetIdentities[i] != null) {
                this.backend.getProducts().cloneProductConfigurationSet(
                    productConfigurationSetIdentities[i],
                    bookingCredit.path(),
                    false,
                    false                    
                );
            }
            
            // Create debit booking
            DataproviderObject bookingDebit = new DataproviderObject(
                depotEntityIdentity.getPrefix(
                    depotEntityIdentity.size()-2
                ).getDescendant(
                    new String[]{"booking", this.backend.getUidAsString()}
                )
            );
            bookingDebit.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:DebitBooking"
            );
            bookingDebit.values("name").add(
                depotNumberDebit + " " + bookingTexts[i].values("debitBookingNameInfix").get(0) + " " + positionName
            );
            bookingDebit.values("quantityDebit").add(quantities[i]);
            bookingDebit.values("valueDate").add(
                DateFormat.getInstance().format(valueDate)
            );
            bookingDebit.values("bookingType").add(new Short(bookingType));
            bookingDebit.values("bookingStatus").add(new Short(BOOKING_STATUS_PENDING));
            bookingDebit.values("bookingDate").add(
                DateFormat.getInstance().format(bookingDate)
            );
            bookingDebit.values("cb").add(compoundBooking.path());
            bookingDebit.values("position").add(debitPositions[i].path());
            if(originIdentities[i] != null) {
                bookingDebit.values("origin").add(originIdentities[i]);
            }            
            this.backend.getDelegatingRequests().addCreateRequest(bookingDebit);
        }
        
        return compoundBooking;
    }
    
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 getAndCreateDepotPosition(
        Path depotEntityIdentity,
        String depotNumber,
        Path depotIdentity,
        String positionName,
        Path productIdentity,
        Date openingDate
    ) throws ServiceException {
        DataproviderObject_1_0 depot = null;
        if(depotIdentity != null) {
            depot = this.backend.retrieveObject(depotIdentity);
        }
        else {
            if(depotNumber == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_DEPOT_NUMBER,
                   "Missing depot number"
               );                                        
            }
            List depots = this.backend.getDelegatingRequests().addFindRequest(
                depotEntityIdentity.getPrefix(5).getChild("extent"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_IDENTITY,
                        FilterOperators.IS_LIKE,
                        depotEntityIdentity.getDescendant(new String[]{"depotHolder", ":*", "depot", ":*"})
                    ),                        
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "depotNumber",
                        FilterOperators.IS_IN,
                        depotNumber                            
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING                    
            );
            if(!depots.isEmpty()) {
                depot = (DataproviderObject_1_0)depots.iterator().next();
            }
            else {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_DEPOT_NOT_FOUND,
                   "Depot not found",
                   new BasicException.Parameter("param0", depotNumber)
               );                                        
            }
        }
        List depotPositions = this.backend.getDelegatingRequests().addFindRequest(
            depot.path().getChild("position"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "name",
                    FilterOperators.IS_IN,
                    positionName                            
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        if(depotPositions.size() > 0) {
            return (DataproviderObject_1_0)depotPositions.iterator().next();
        }
        // On-demand creation of position
        else {
            if(
                (depot.values("allowPositionAutoCreate").size() > 0) &&
                ((Boolean)depot.values("allowPositionAutoCreate").get(0)).booleanValue()
            ) {
                DataproviderObject params = new DataproviderObject(new Path("xri:@openmdx:*"));
                params.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:OpenDepotPositionParams"
                );
                params.values("name").add(positionName);
                if(productIdentity != null) {
                    params.values("product").add(productIdentity);
                }
                if(openingDate != null) {
                    params.values("openingDate").add(DateFormat.getInstance().format(openingDate));
                }
                params.values("isLocked").add(Boolean.FALSE);
                return this.openDepotPosition(
                    depot,
                    params
                );
            }
            else {
                return null;
            }
        }
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByPosition(
        Path depotEntityIdentity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        Path bookingTextIdentity,
        Path positionCreditIdentity,
        Path positionDebitIdentity,
        Path originIdentity,
        Path reversalOfIdentity,
        List<String> errors
    ) throws ServiceException {
        DataproviderObject_1_0 compoundBooking = this.createCreditDebitBooking(                
            depotEntityIdentity,
            valueDate,
            bookingType,
            quantity,
            bookingTextName,
            bookingTextIdentity,
            positionCreditIdentity,
            positionDebitIdentity,
            null,
            reversalOfIdentity,
            reversalOfIdentity
        );
        return compoundBooking == null
            ? null
            : (CompoundBooking)this.backend.getDelegatingPkg().refObject(compoundBooking.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByProduct(
        Path depotEntityIdentity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        Path bookingTextIdentity,
        Product product,
        String depotNumberCredit,
        Path depotCreditIdentity,
        String depotNumberDebit,
        Path depotDebitIdentity,
        Path originIdentity,
        Path reversalOfIdentity,
        List<String> errors
    ) throws ServiceException {
        if(product == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_MISSING_PRODUCT,
               "Missing product"
           );                                    
        }
        String positionName = product.getProductNumber() != null
            ? product.getProductNumber()
            : product.getName();            
        DataproviderObject_1_0 positionCredit = this.getAndCreateDepotPosition(
            depotEntityIdentity,
            depotNumberCredit,
            depotCreditIdentity,
            positionName,
            product.refGetPath(),
            valueDate
        );
        if(positionCredit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_CREDIT,
               "Can not get/create credit depot position"
           );                                                
        }
        DataproviderObject_1_0 positionDebit = this.getAndCreateDepotPosition(
            depotEntityIdentity,
            depotNumberDebit,
            depotDebitIdentity,
            positionName,
            product.refGetPath(),
            valueDate
        );
        if(positionDebit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_DEBIT,
               "Can not get/create debit depot position"
           );                                                
        }
        DataproviderObject_1_0 compoundBooking = this.createCreditDebitBooking(
            depotEntityIdentity,
            valueDate,
            bookingType,
            quantity,            
            bookingTextName,
            bookingTextIdentity,
            positionCredit.path(),
            positionDebit.path(),
            null,
            originIdentity,
            reversalOfIdentity
        );
        return compoundBooking == null
            ? null
            : (CompoundBooking)this.backend.getDelegatingPkg().refObject(compoundBooking.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByPositionName(
        Path depotEntityIdentity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        Path bookingTextIdentity,
        String positionName,
        String depotNumberCredit,
        Path depotCreditIdentity,
        String depotNumberDebit,
        Path depotDebitIdentity,
        Path originIdentity,
        Path reversalOfIdentity,
        List<String> errors
    ) throws ServiceException {
        DataproviderObject_1_0 positionCredit = this.getAndCreateDepotPosition(
            depotEntityIdentity,
            depotNumberCredit,
            depotCreditIdentity,
            positionName,
            null,
            valueDate
        );
        if(positionCredit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_CREDIT,
               "Can not get/create credit depot position"
           );                                                
        }
        DataproviderObject_1_0 positionDebit = this.getAndCreateDepotPosition(
            depotEntityIdentity,
            depotNumberDebit,
            depotDebitIdentity,
            positionName,
            null,
            valueDate
        );
        if(positionDebit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_DEBIT,
               "Can not get/create debit depot position"
           );                                                
        }
        DataproviderObject_1_0 compoundBooking = this.createCreditDebitBooking(
            depotEntityIdentity,
            valueDate,
            bookingType,
            quantity,            
            bookingTextName,
            bookingTextIdentity,
            positionCredit.path(),
            positionDebit.path(),
            null,
            originIdentity,
            reversalOfIdentity
        );
        return compoundBooking == null
            ? null
            : (CompoundBooking)this.backend.getDelegatingPkg().refObject(compoundBooking.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public void refreshReport(
        Path depotIdentity,
        DataproviderObject_1_0 report,
        DataproviderObject_1_0 reportPreviousPeriod
    ) throws ServiceException {
        // Refresh only only draft reports
        if(
            (report.values("isDraft").size() > 0) &&
            ((Boolean)report.values("isDraft").get(0)).booleanValue()
        ) {
            this.backend.removeAll(
                report.path().getChild("itemPosition"),
                null,
                0,
                Integer.MAX_VALUE,                
                null
            );
            DataproviderObject_1_0 bookingPeriod = this.backend.retrieveObject(
                (Path)report.values("bookingPeriod").get(0)
            );
            String periodStartsAt = (String)bookingPeriod.values("periodStartsAt").get(0);
            // Create a position item for all depot positions
            List<DataproviderObject_1_0> positions =  this.backend.getDelegatingRequests().addFindRequest(
                depotIdentity.getChild("position"),
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            for(DataproviderObject_1_0 position: positions) {
                // Set beginning of report balances to end of period balances of previous report 
                BigDecimal balanceBop = null;
                BigDecimal balanceDebitBop = null;
                BigDecimal balanceCreditBop = null;
                BigDecimal balanceCredit = null;
                BigDecimal balanceDebit = null;
                BigDecimal balanceSimple = null;
                BigDecimal balanceSimpleBop = null;
                if(reportPreviousPeriod != null) {
                    List<DataproviderObject_1_0> items = this.backend.getDelegatingRequests().addFindRequest(
                        reportPreviousPeriod.path().getChild("itemPosition"),
                        new FilterProperty[]{
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "position",
                                FilterOperators.IS_IN,
                                position.path()
                            )
                        },
                        AttributeSelectors.ALL_ATTRIBUTES,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING
                    );
                    if(!items.isEmpty()) {
                        DataproviderObject_1_0 itemPosition = items.iterator().next();
                        balanceBop = (BigDecimal)itemPosition.values("balance").get(0);                        
                        balanceCreditBop = (BigDecimal)itemPosition.values("balanceCredit").get(0);
                        balanceDebitBop = (BigDecimal)itemPosition.values("balanceDebit").get(0);
                        balanceCredit = (BigDecimal)itemPosition.values("balanceCredit").get(0);
                        balanceDebit = (BigDecimal)itemPosition.values("balanceDebit").get(0);
                        balanceSimpleBop = (BigDecimal)itemPosition.values("balanceSimple").get(0);                                                
                        balanceSimple = (BigDecimal)itemPosition.values("balanceSimple").get(0);
                    }
                }
                balanceBop = balanceBop == null ? new BigDecimal(0) : balanceBop;
                balanceDebitBop = balanceDebitBop == null ? new BigDecimal(0) : balanceDebitBop;
                balanceCreditBop = balanceCreditBop == null ? new BigDecimal(0) : balanceCreditBop;
                balanceCredit = balanceCredit == null ? new BigDecimal(0) : balanceCredit;
                balanceDebit = balanceDebit == null ? new BigDecimal(0) : balanceDebit;                
                balanceSimpleBop = balanceSimpleBop == null ? new BigDecimal(0) : balanceSimpleBop;
                balanceSimple = balanceSimple == null ? new BigDecimal(0) : balanceSimple;
                // Create depot report item for position
                DataproviderObject itemPosition = new DataproviderObject(
                    report.path().getDescendant(new String[]{"itemPosition", this.backend.getUidAsString()})
                );
                itemPosition.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:DepotReportItemPosition"
                );
                itemPosition.values("positionName").addAll(
                    position.values("name")
                );
                itemPosition.values("valueDate").add(periodStartsAt);
                itemPosition.values("position").add(position.path());
                itemPosition.values("balanceBop").add(balanceBop);
                itemPosition.values("balanceCreditBop").add(balanceCreditBop);
                itemPosition.values("balanceDebitBop").add(balanceDebitBop);
                itemPosition.values("balanceSimpleBop").add(balanceSimpleBop);
                // Create it
                this.backend.getDelegatingRequests().addCreateRequest(itemPosition);

                // Get single bookings for this position item
                List<DataproviderObject_1_0> singleBookings = this.backend.getDelegatingRequests().addFindRequest(
                    itemPosition.path().getChild("singleBooking"),
                    null,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null,
                    0,
                    Integer.MAX_VALUE,
                    Directions.ASCENDING
                );                                    
                // Get simple bookings for this position item
                List<DataproviderObject_1_0> simpleBookings = this.backend.getDelegatingRequests().addFindRequest(
                    itemPosition.path().getChild("simpleBooking"),
                    null,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null,
                    0,
                    Integer.MAX_VALUE,
                    Directions.ASCENDING
                );                    
                // Sum up single bookings
                for(DataproviderObject_1_0 singleBooking: singleBookings) {
                    // Credit booking
                    if("org:opencrx:kernel:depot1:CreditBooking".equals(singleBooking.values(SystemAttributes.OBJECT_CLASS).get(0))) {
                        BigDecimal quantityCredit = (BigDecimal)singleBooking.values("quantityCredit").get(0);
                        balanceCredit = balanceCredit.add(quantityCredit);
                    }
                    // Debit booking
                    else {
                        BigDecimal quantityDebit = (BigDecimal)singleBooking.values("quantityDebit").get(0);
                        balanceDebit = balanceDebit.add(quantityDebit);
                    }
                }
                // Sum up simple bookings
                for(DataproviderObject_1_0 simpleBooking: simpleBookings) {
                    BigDecimal quantity = (BigDecimal)simpleBooking.values("quantity").get(0);
                    balanceSimple = balanceSimple.add(quantity);
                }
                // Update balances
                itemPosition = this.backend.retrieveObjectForModification(
                    itemPosition.path()
                );
                itemPosition.values("balance").add(balanceCredit.subtract(balanceDebit));
                itemPosition.values("balanceCredit").add(balanceCredit);
                itemPosition.values("balanceDebit").add(balanceDebit);
                itemPosition.values("balanceSimple").add(balanceSimple);
            }            
        }
    }
    
    //-----------------------------------------------------------------------
    public void assertReports(
        Path depotIdentity,
        short bookingStatusThreshold
    ) throws ServiceException {
        
        // Get booking periods
        List bookingPeriods = this.backend.getDelegatingRequests().addFindRequest(
                depotIdentity.getPrefix(7).getChild("bookingPeriod"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{
                new AttributeSpecifier("periodStartsAt", 0, Orders.ASCENDING)
            },
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        DataproviderObject_1_0 reportPreviousPeriod = null;
        DataproviderObject_1_0 latestReport = null;
        
        // Assert report for each booking period
        for(
            Iterator i = bookingPeriods.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 bookingPeriod = (DataproviderObject_1_0)i.next();
            List reports = this.backend.getDelegatingRequests().addFindRequest(
                depotIdentity.getChild("report"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "bookingPeriod",
                        FilterOperators.IS_IN,
                        bookingPeriod.path()
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Directions.ASCENDING
            ); 
            DataproviderObject_1_0 report = null;
            if(!reports.isEmpty()) {
                report = (DataproviderObject_1_0)reports.iterator().next();
            }
            else {
                DataproviderObject newReport = new DataproviderObject(
                    depotIdentity.getDescendant(new String[]{"report", this.backend.getUidAsString()})
                );
                newReport.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:DepotReport"
                );
                newReport.values("name").addAll(
                    bookingPeriod.values("name")
                );
                newReport.values("description").addAll(
                    bookingPeriod.values("description")
                );
                newReport.values("isDraft").add(
                    Boolean.TRUE
                );
                newReport.values("bookingStatusThreshold").add(
                    new Short(bookingStatusThreshold)
                );
                newReport.values("bookingPeriod").add(
                    bookingPeriod.path()
                );                
                this.backend.getDelegatingRequests().addCreateRequest(
                    newReport
                );
                report = newReport;
            }
            // Latest report
            String currentDate = DateFormat.getInstance().format(new Date());
            if(
               (currentDate.compareTo(((String)bookingPeriod.values("periodStartsAt").get(0))) >= 0) &&
               ((bookingPeriod.values("periodEndsAtExclusive").size() == 0) || (currentDate.compareTo(((String)bookingPeriod.values("periodEndsAtExclusive").get(0))) < 0))
            ) {
                latestReport = report;
            }
            // Refresh
            this.refreshReport(
                depotIdentity,
                report,
                reportPreviousPeriod
            );            
            reportPreviousPeriod = report;
        }
        if(latestReport != null) {
            DataproviderObject modifiableDepot = this.backend.retrieveObjectForModification(
                depotIdentity
            );
            modifiableDepot.clearValues("latestReport").add(latestReport.path());
        }
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking cancelCompoundBooking(
        CompoundBooking compoundBooking,
        List<String> errors
    ) throws ServiceException {
        boolean isProcessed = compoundBooking.getBookingStatus() == BOOKING_STATUS_PROCESSED;
        if(!isProcessed) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PROCESSED,
                "Booking status must be processed. Cancel is not allowed."
            );                                                
        }
        short bookingType = compoundBooking.getBookingType() > 0
            ? compoundBooking.getBookingType()
            : BOOKING_TYPE_STANDARD;
        // Can not cancel reversal bookings
        if(bookingType == BOOKING_TYPE_REVERSAL) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_CAN_NOT_CANCEL_REVERSAL_BOOKING,
                "Can not cancel reversal booking"
            );                        
        }
        // Check whether compound booking already has a reversal booking
        List compoundBookings = this.backend.getDelegatingRequests().addFindRequest(
            compoundBooking.refGetPath().getParent(),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "reversalOf",
                    FilterOperators.IS_IN,
                    compoundBooking.refGetPath()
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        if(!compoundBookings.isEmpty()) {
            DataproviderObject_1_0 reversal = (DataproviderObject_1_0)compoundBookings.iterator().next();
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_ALREADY_HAS_REVERSAL_BOOKING,
                "Compound booking already cancelled",
                new BasicException.Parameter("param0", reversal.values("name").get(0) + " / " + reversal.values("bookingDate").get(0))
            );                                    
        }

        // Create cancel compound booking
        DataproviderObject cancelCompoundBooking = new DataproviderObject(
            compoundBooking.refGetPath().getParent().getChild(this.backend.getUidAsString())
        );
        cancelCompoundBooking.values(SystemAttributes.OBJECT_CLASS).add(
            "org:opencrx:kernel:depot1:CompoundBooking"
        );        
        if(compoundBooking.getName() != null) {
            cancelCompoundBooking.values("name").add(compoundBooking.getName());
        }
        if(compoundBooking.getDescription() != null) {
            cancelCompoundBooking.values("description").add(compoundBooking.getDescription());
        }
        cancelCompoundBooking.values("bookingDate").add(DateFormat.getInstance().format(new Date()));
        cancelCompoundBooking.values("bookingType").add(new Short(BOOKING_TYPE_REVERSAL));
        cancelCompoundBooking.values("bookingStatus").add(new Short(BOOKING_STATUS_PENDING));
        cancelCompoundBooking.values("reversalOf").add(compoundBooking.refGetPath());
        this.backend.getDelegatingRequests().addCreateRequest(
            cancelCompoundBooking
        );

        // Create cancel bookings
        List bookings = this.backend.getDelegatingRequests().addFindRequest(
            compoundBooking.refGetPath().getPrefix(compoundBooking.refGetPath().size()-2).getChild("booking"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "cb",
                    FilterOperators.IS_IN,
                    compoundBooking.refGetPath()
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        for(
            Iterator i = bookings.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 booking = (DataproviderObject_1_0)i.next();
            DataproviderObject cancelBooking = new DataproviderObject(
                booking.path().getParent().getChild(this.backend.getUidAsString())
            );
            if("org:opencrx:kernel:depot1:CreditBooking".equals(booking.values(SystemAttributes.OBJECT_CLASS).get(0))) {
                cancelBooking.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:DebitBooking"
                );
                cancelBooking.values("quantityDebit").addAll(booking.values("quantityCredit"));
            }
            else if("org:opencrx:kernel:depot1:DebitBooking".equals(booking.values(SystemAttributes.OBJECT_CLASS).get(0))) {
                cancelBooking.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:CreditBooking"
                );
                cancelBooking.values("quantityCredit").addAll(booking.values("quantityDebit"));                
            }
            cancelBooking.values("name").addAll(booking.values("name"));
            cancelBooking.values("description").addAll(booking.values("description"));
            cancelBooking.values("valueDate").addAll(booking.values("valueDate"));
            cancelBooking.values("bookingDate").add(DateFormat.getInstance().format(new Date()));
            cancelBooking.values("position").addAll(booking.values("position"));
            this.testForOpenPosition(
                this.backend.parseDate((String)cancelBooking.values("valueDate").get(0)),
                BOOKING_TYPE_REVERSAL,
                (Path)cancelBooking.values("position").get(0)
            );
            cancelBooking.values("bookingType").add(new Short(BOOKING_TYPE_REVERSAL));
            cancelBooking.values("bookingStatus").add(new Short(BOOKING_STATUS_PENDING));
            cancelBooking.values("cb").add(cancelCompoundBooking.path());
            this.backend.getDelegatingRequests().addCreateRequest(
                cancelBooking
            );
        }
        return cancelCompoundBooking == null
            ? null
            : (CompoundBooking)this.backend.getDelegatingPkg().refObject(cancelCompoundBooking.path().toXri());
    }

    //-----------------------------------------------------------------------
    public void acceptCompoundBooking(
        CompoundBooking compoundBooking
    ) throws ServiceException {
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status must be pending. Accept is not allowed."
            );                                                
        }
        String acceptedBy = (String)this.backend.getServiceHeader().getPrincipalChain().get(0) + " @ " + DateFormat.getInstance().format(new Date());
        DataproviderObject modifiedCompoundBooking = this.backend.retrieveObjectForModification(
            compoundBooking.refGetPath()
        );
        modifiedCompoundBooking.values("acceptedBy").add(acceptedBy);
    }
    
    //-----------------------------------------------------------------------
    public void finalizeCompoundBooking(
        CompoundBooking compoundBooking
    ) throws ServiceException {
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status must be pending. Finalize is not allowed."
            );                                                
        }
        // Process bookings
        List bookings = this.backend.getDelegatingRequests().addFindRequest(
            compoundBooking.refGetPath().getPrefix(compoundBooking.refGetPath().size()-2).getChild("booking"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "cb",
                    FilterOperators.IS_IN,
                    compoundBooking.refGetPath()
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        for(
            Iterator i = bookings.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 booking = (DataproviderObject_1_0)i.next();
            this.testForOpenPosition(
                this.backend.parseDate((String)booking.values("valueDate").get(0)),
                ((Number)booking.values("bookingType").get(0)).shortValue(),
                (Path)booking.values("position").get(0)
            );
            DataproviderObject finalizedBooking = this.backend.retrieveObjectForModification(
                booking.path()
            );
            finalizedBooking.clearValues("bookingStatus").add(new Short(BOOKING_STATUS_PROCESSED));
        }
        // Process compound booking
        DataproviderObject finalizedCompoundBooking = this.backend.retrieveObjectForModification(
            compoundBooking.refGetPath()
        );
        finalizedCompoundBooking.clearValues("bookingStatus").add(new Short(BOOKING_STATUS_PROCESSED));        
    }

    //-----------------------------------------------------------------------
    public void removeCompoundBooking(
        Path compoundBookingIdentity
    ) throws ServiceException {
        DataproviderObject_1_0 compoundBooking = this.backend.retrieveObject(compoundBookingIdentity);
        // isPending
        boolean isPending = compoundBooking.values("bookingStatus").size() > 0
            ? ((Number)compoundBooking.values("bookingStatus").get(0)).shortValue() == BOOKING_STATUS_PENDING
            : false;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Delete is not allowed."
            );                                                
        }
        // isLocked
        boolean isLocked = compoundBooking.values("isLocked").size() > 0
            ? ((Boolean)compoundBooking.values("isLocked").get(0)).booleanValue()
            : false;
        if(isLocked) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_IS_LOCKED_CAN_NOT_DELETE,
                "Compound booking is locked. Delete is not allowed."
            );                                                
        }
        // Delete bookings
        List bookings = this.backend.getDelegatingRequests().addFindRequest(
            compoundBookingIdentity.getPrefix(compoundBookingIdentity.size()-2).getChild("booking"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "cb",
                    FilterOperators.IS_IN,
                    compoundBookingIdentity
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        List bookingIdentities = new ArrayList(); 
        for(
            Iterator i = bookings.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 booking = (DataproviderObject_1_0)i.next();
            bookingIdentities.add(booking.path());
        }
        for(
            Iterator i = bookingIdentities.iterator();
            i.hasNext();
        ) {
            Path bookingIdentity = (Path)i.next();
            this.backend.removeObject(bookingIdentity);
        }
        
        // Delete compound booking
        this.backend.removeObject(compoundBookingIdentity);       
    }

    //-----------------------------------------------------------------------
    public void removeSimpleBooking(
        Path simpleBookingIdentity
    ) throws ServiceException {
        DataproviderObject_1_0 simpleBooking = this.backend.retrieveObject(simpleBookingIdentity);
        // isPending
        boolean isPending = simpleBooking.values("bookingStatus").size() > 0
            ? ((Number)simpleBooking.values("bookingStatus").get(0)).shortValue() == BOOKING_STATUS_PENDING
            : false;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Delete is not allowed."
            );                                                
        }
        // Delete simple booking
        this.backend.removeObject(simpleBookingIdentity);       
    }

    //-----------------------------------------------------------------------
    public void removeSingleBooking(
        Path bookingIdentity
    ) throws ServiceException {
        throw new ServiceException(
            OpenCrxException.DOMAIN,
            OpenCrxException.DEPOT_CAN_NOT_REMOVE_BOOKING,
            "Can not delete bookings"
        );                                                
    }

    //-----------------------------------------------------------------------
    public boolean hasBookings(
        Path bookingElementIdentity
    ) throws ServiceException {
        Path segmentIdentity = bookingElementIdentity.getPrefix(5);
        Path positionPattern = new Path(
            "xri:@openmdx:org.opencrx.kernel.depot1" + 
            "/provider/" + bookingElementIdentity.get(2) + 
            "/segment/" + bookingElementIdentity.get(4) +
            "/entity/" + bookingElementIdentity.get(6)
        );        
        positionPattern.add("depotHolder");
        if(bookingElementIdentity.size() > 8) {
            positionPattern.add(bookingElementIdentity.get(8));
            positionPattern.add("depot");
            if(bookingElementIdentity.size() > 10) {               
                positionPattern.add(bookingElementIdentity.get(10));
                positionPattern.add("position");
                if(bookingElementIdentity.size() > 12) {               
                    positionPattern.add(bookingElementIdentity.get(12));
                }
                else {
                    positionPattern.add("%");                    
                }
            }
            else {
                positionPattern.add("%");
            }
        }
        else {
            positionPattern.add("%");
        }
        List bookings = this.backend.getDelegatingRequests().addFindRequest(
            segmentIdentity.getPrefix(5).getChild("extent"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "identity",
                    FilterOperators.IS_LIKE,
                    segmentIdentity.getDescendant(
                        new String[]{"booking", ":*"}
                    )
                ),
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "position",
                    FilterOperators.IS_LIKE,
                    positionPattern
                )
            },
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING                    
        );
        return bookings.size() > 0;
    }
    
    //-----------------------------------------------------------------------
    public void removeDepotEntity(
        Path depotEntityIdentity
    ) throws ServiceException {
        if(this.hasBookings(depotEntityIdentity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_ENTITY_HAS_BOOKINGS,
                "Depot entity has bookings."
            );                                                                        
        }
        this.backend.removeObject(
            depotEntityIdentity
        );
    }
    
    //-----------------------------------------------------------------------
    public void removeDepotContract(
        Path depotContractIdentity
    ) throws ServiceException {
        if(this.hasBookings(depotContractIdentity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_CONTRACT_HAS_BOOKINGS,
                "Depot entity has bookings."
            );                                                                        
        }
        this.backend.removeObject(
            depotContractIdentity
        );
    }
    
    //-----------------------------------------------------------------------
    public void removeDepot(
        Path depotIdentity
    ) throws ServiceException {
        if(this.hasBookings(depotIdentity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_HAS_BOOKINGS,
                "Depot entity has bookings."
            );                                                                        
        }
        this.backend.removeObject(
            depotIdentity
        );
    }
    
    //-----------------------------------------------------------------------
    public void removeDepotPosition(
        Path depotPositionIdentity
    ) throws ServiceException {
        if(this.hasBookings(depotPositionIdentity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_HAS_BOOKINGS,
                "Depot entity has bookings."
            );                                                                        
        }
        this.backend.removeObject(
            depotPositionIdentity
        );
    }
    
    //-----------------------------------------------------------------------
    public Depot openDepot(
        Path depotHolderIdentity,
        String name,
        String description,
        String depotNumber,
        Date openingDate,
        Path depotTypeIdentity,
        Path depotGroupIdentity,
        List errors
    ) throws ServiceException {
        if(depotNumber == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_NUMBER_REQUIRED,
                "Depot number is required."
            );                                                            
        }
        String depotNumberWithoutWhitespaces = this.stripWhitespaces(depotNumber);
        DataproviderObject depot = new DataproviderObject(
            depotHolderIdentity.getDescendant(new String[]{"depot", depotNumberWithoutWhitespaces.replace('/', ':')})            
        );
        depot.values(SystemAttributes.OBJECT_CLASS).add(
            "org:opencrx:kernel:depot1:Depot"
        );
        if(name != null) {
            depot.values("name").add(name);
        }
        if(description != null) {
            depot.values("description").add(description);
        }
        depot.values("depotNumber").add(depotNumber);        
        depot.values("openingDate").add(
            openingDate == null
                ? DateFormat.getInstance().format(new Date())
                : DateFormat.getInstance().format(openingDate)
        );
        if(depotTypeIdentity != null) {
            depot.values("depotType").add(depotTypeIdentity);
        }
        if(depotGroupIdentity != null) {
            depot.values("depotGroup").add(depotGroupIdentity);
        }
        depot.values("isDefault").add(Boolean.FALSE);
        depot.values("isLocked").add(Boolean.FALSE);
        depot.values("allowPositionAutoCreate").add(Boolean.FALSE);
        this.backend.createObject(
            depot
        );
        return depot == null
            ? null
            : (Depot)this.backend.getDelegatingPkg().refObject(depot.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public void closeDepot(
        Depot depot,
        Date closingDate,
        List errors
    ) throws ServiceException {
        if(depot.getClosingDate() != null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_IS_CLOSED_CAN_NOT_CLOSE,
                "Depot is closed. Can not close."
            );                                                                        
        }
        DataproviderObject closedDepot = this.backend.retrieveObjectForModification(
            depot.refGetPath()
        );
        closedDepot.clearValues("isLocked").add(Boolean.TRUE);
        closedDepot.clearValues("closingDate").add(
            closingDate == null
                ? DateFormat.getInstance().format(new Date())
                : DateFormat.getInstance().format(closingDate)
        );        
    }
    
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 openDepotPosition(
        DataproviderObject_1_0 depot,
        DataproviderObject_1_0 params
    ) throws ServiceException {
        String depotPositionQualifier = params.values("qualifier").size() > 0
            ? (String)params.values("qualifier").get(0)
            : null;
        Path productRoleIdentity = params.values("productRole").size() > 0
            ? (Path)params.values("productRole").get(0)
            : null;        
        Path productIdentity = params.values("product").size() > 0
            ? (Path)params.values("product").get(0)
            : null;
        String positionName = params.values("name").size() > 0
            ? (String)params.values("name").get(0)
            : null;
        String positionDescription = params.values("description").size() > 0
            ? (String)params.values("description").get(0)
            : null;
        Date openingDate = params.values("openingDate").size() == 0
            ? new Date()
            : this.backend.parseDate((String)params.values("openingDate").get(0));            
        Boolean isLocked = params.values("isLocked").size() == 0
            ? Boolean.FALSE
            : (Boolean)params.values("isLocked").get(0);            
        return this.openDepotPosition(
            depot,
            positionName,
            positionDescription,
            openingDate,
            depotPositionQualifier,
            productRoleIdentity,
            productIdentity,
            isLocked
        );
    }
    
    //-----------------------------------------------------------------------
    public DepotPosition openDepotPosition(
        Path depotIdentity,
        String positionName,
        String positionDescription,
        Date openingDate,
        String depotPositionQualifier,
        Path basedOnIdentity,
        Path productIdentity,
        Boolean isLocked
    ) throws ServiceException {
        DataproviderObject_1_0 depotPosition = this.openDepotPosition(
            this.backend.retrieveObject(
                depotIdentity
            ),
            positionName,
            positionDescription, 
            openingDate, 
            depotPositionQualifier, 
            basedOnIdentity, 
            productIdentity, 
            isLocked
        );
        return depotPosition == null
            ? null
            : (DepotPosition)this.backend.getDelegatingPkg().refObject(depotPosition.path().toXri());
    }
    
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 openDepotPosition(
        DataproviderObject_1_0 depot,
        String positionName,
        String positionDescription,
        Date openingDate,
        String depotPositionQualifier,
        Path basedOnProductIdentity,
        Path productIdentity,
        Boolean isLocked
    ) throws ServiceException {
        DataproviderObject depotPosition = new DataproviderObject(
            depot.path().getDescendant(new String[]{"position", this.backend.getUidAsString()})            
        );
        DataproviderObject_1_0 basedOnProduct = null; 
        DataproviderObject_1_0 product = null; 
        if(basedOnProductIdentity != null) {
            // Check whether position with given productRole already
            // exists. If yes, do not create new position.
            List positions = null;
            if(depotPositionQualifier == null) {
                positions = this.backend.getDelegatingRequests().addFindRequest(
                    depot.path().getChild("position"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "basedOn",
                            FilterOperators.IS_IN,
                            basedOnProductIdentity
                        )
                    },
                    AttributeSelectors.ALL_ATTRIBUTES,
                    0,
                    Integer.MAX_VALUE,
                    Directions.ASCENDING                    
                );
            }
            // qualifier and productRole must match
            else {
                positions = this.backend.getDelegatingRequests().addFindRequest(
                    depot.path().getChild("position"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "basedOn",
                            FilterOperators.IS_IN,
                            basedOnProductIdentity
                        ),
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "qualifier",
                            FilterOperators.IS_IN,
                            depotPositionQualifier
                        )
                    },
                    AttributeSelectors.ALL_ATTRIBUTES,
                    0,
                    Integer.MAX_VALUE,
                    Directions.ASCENDING                    
                );                
            }
            if(positions.size() > 0) {
                return (DataproviderObject_1_0)positions.iterator().next();
            }
            basedOnProduct = this.backend.retrieveObject(
                basedOnProductIdentity
            );
        }
        // Set class depending on supplied product and productRole
        if(basedOnProduct == null) {
            if(productIdentity != null) { 
                // Check whether position with given product already
                // exists. If yes, do not create new position.
                List positions = null;
                if(depotPositionQualifier == null) {
                    positions = this.backend.getDelegatingRequests().addFindRequest(
                        depot.path().getChild("position"),
                        new FilterProperty[]{
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "product",
                                FilterOperators.IS_IN,
                                productIdentity
                            )
                        },
                        AttributeSelectors.ALL_ATTRIBUTES,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING                    
                    );
                }
                else {
                    positions = this.backend.getDelegatingRequests().addFindRequest(
                        depot.path().getChild("position"),
                        new FilterProperty[]{
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "product",
                                FilterOperators.IS_IN,
                                productIdentity
                            ),
                            new FilterProperty(
                                Quantors.THERE_EXISTS,
                                "qualifier",
                                FilterOperators.IS_IN,
                                depotPositionQualifier
                            )
                        },
                        AttributeSelectors.ALL_ATTRIBUTES,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING                    
                    );                    
                }
                if(positions.size() > 0) {
                    return (DataproviderObject_1_0)positions.iterator().next();
                }
                product = this.backend.retrieveObject(
                    productIdentity
                );
                depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:ProductDepotPosition"
                );
                depotPosition.values("product").add(product.path());
            }
            else {
                depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:depot1:DepotPosition"
                );                
            }
        }
        else if("org:opencrx:kernel:product1:ProductOffering".equals(basedOnProduct.values(SystemAttributes.OBJECT_CLASS).get(0))) {
            depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:ProductOfferingDepotPosition"
            );
        }
        else if("org:opencrx:kernel:product1:ProductBundle".equals(basedOnProduct.values(SystemAttributes.OBJECT_CLASS).get(0))) {
            depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:ProductBundleDepotPosition"
            );
        }
        else if("org:opencrx:kernel:product1:ComplexProductBundle".equals(basedOnProduct.values(SystemAttributes.OBJECT_CLASS).get(0))) {
            depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:ComplexProductBundleDepotPosition"
            );
        }
        else if("org:opencrx:kernel:product1:BundledProduct".equals(basedOnProduct.values(SystemAttributes.OBJECT_CLASS).get(0))) {
            depotPosition.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:depot1:BundledProductDepotPosition"
            );
        }
        
        // In case a depot position qualifier is specified, set name to productNumber + " #" + depotPositionQualifier.
        String name = 
            positionName != null
                ? positionName
                : product != null
                    ? (String)product.values("productNumber").get(0)
                    : basedOnProduct != null
                        ? (String)basedOnProduct.values("productNumber").get(0)
                        : "";
        if(depotPositionQualifier != null) {
            depotPosition.values("qualifier").add(depotPositionQualifier);
            name += " #" + depotPositionQualifier;
        }            
        depotPosition.values("name").add(name);
        // Set path qualifier to name (remove blanks and replace / by :
        String nameWithoutWhitspaces = this.stripWhitespaces(name);
        if(nameWithoutWhitspaces.length() > 0) {
            depotPosition.path().setTo(
                depotPosition.path().getParent().getChild(nameWithoutWhitspaces.replace('/', ':'))
            );
        }
        // description
        depotPosition.values("description").add(
            positionDescription != null
                ? positionDescription
                : product != null
                    ? product.values("description").get(0)
                    : basedOnProduct != null
                        ? basedOnProduct.values("description").get(0)
                        : ""
        );
        depotPosition.values("basedOn").add(basedOnProductIdentity);
        if(openingDate != null) {
            depotPosition.values("openingDate").add(
                DateFormat.getInstance().format(openingDate)
            );
        }
        depotPosition.values("isLocked").add(isLocked);
        try {
            return this.backend.retrieveObjectFromDelegation(depotPosition.path());
        }
        catch(ServiceException e) {
            this.backend.createObject(
                depotPosition
            );
            return depotPosition;
        }
    }
    
    //-----------------------------------------------------------------------
    public void closeDepotPosition(       
        DepotPosition depotPosition,
        Date closingDate,
        List errors
    ) throws ServiceException {
        if(depotPosition.getClosingDate() != null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_POSITION_IS_CLOSED_CAN_NOT_CLOSE,
                "Depot position is closed. Can not close."
            );                                                                        
        }
        DataproviderObject closedDepotPosition = this.backend.retrieveObjectForModification(
            depotPosition.refGetPath()
        );
        closedDepotPosition.clearValues("isLocked").add(Boolean.TRUE);
        closedDepotPosition.clearValues("closingDate").add(
            closingDate == null
                ? DateFormat.getInstance().format(new Date())
                : DateFormat.getInstance().format(closingDate)
        );        
    }
    
    //-----------------------------------------------------------------------
    public void lockCompoundBooking(
        CompoundBooking compoundBooking,
        short lockingReason
    ) throws ServiceException {
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Locking is not allowed."
            );                                                
        }
        DataproviderObject modifiedCompoundBooking = this.backend.retrieveObjectForModification(
            compoundBooking.refGetPath()
        );
        modifiedCompoundBooking.clearValues("isLocked").add(Boolean.TRUE);
        modifiedCompoundBooking.clearValues("lockingReason").add(
            new Short(lockingReason)
        );
        modifiedCompoundBooking.clearValues("lockModifiedAt").add(
            DateFormat.getInstance().format(new Date())
        );
    }
    
    //-----------------------------------------------------------------------
    public void unlockCompoundBooking(
        CompoundBooking compoundBooking
    ) throws ServiceException {
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Unlocking is not allowed."
            );                                                
        }
        DataproviderObject modifiedCompoundBooking = this.backend.retrieveObjectForModification(
            compoundBooking.refGetPath()
        );
        modifiedCompoundBooking.clearValues("isLocked").add(Boolean.FALSE);
        modifiedCompoundBooking.clearValues("lockingReason").add(
            new Short((short)0)
        );
        modifiedCompoundBooking.clearValues("lockModifiedAt").add(
            DateFormat.getInstance().format(new Date())
        );
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final short BOOKING_TYPE_STANDARD = 10;
    public static final short BOOKING_TYPE_CLOSING = 20;
    public static final short BOOKING_TYPE_REVERSAL = 30;
    
    public static final short BOOKING_STATUS_PENDING = 1;
    public static final short BOOKING_STATUS_PROCESSED = 2;
        
    // Goods are issued to delivery.
    // Goods are returned from delivery
    public static final short DEPOT_USAGE_GOODS_ISSUE = 1;
    public static final short DEPOT_USAGE_GOODS_RETURN = 2;
    public static final short DEPOT_USAGE_GOODS_DELIVERY = 3;
    // Work effort
    public static final short DEPOT_USAGE_WORK_EFFORT = 10;
    
    protected final Backend backend;
        
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: Depots
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.depot1.cci2.BookingPeriodQuery;
import org.opencrx.kernel.depot1.cci2.BookingTextQuery;
import org.opencrx.kernel.depot1.cci2.CompoundBookingQuery;
import org.opencrx.kernel.depot1.cci2.CreditBookingQuery;
import org.opencrx.kernel.depot1.cci2.DepotPositionQuery;
import org.opencrx.kernel.depot1.cci2.DepotQuery;
import org.opencrx.kernel.depot1.cci2.DepotReportItemPositionQuery;
import org.opencrx.kernel.depot1.cci2.DepotReportQuery;
import org.opencrx.kernel.depot1.cci2.ProductDepotPositionQuery;
import org.opencrx.kernel.depot1.cci2.SimpleBookingQuery;
import org.opencrx.kernel.depot1.cci2.SingleBookingQuery;
import org.opencrx.kernel.depot1.jmi1.BookingOrigin;
import org.opencrx.kernel.depot1.jmi1.BookingPeriod;
import org.opencrx.kernel.depot1.jmi1.BookingText;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.CreditBooking;
import org.opencrx.kernel.depot1.jmi1.DebitBooking;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotGroup;
import org.opencrx.kernel.depot1.jmi1.DepotHolder;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReport;
import org.opencrx.kernel.depot1.jmi1.DepotReportItemPosition;
import org.opencrx.kernel.depot1.jmi1.DepotType;
import org.opencrx.kernel.depot1.jmi1.PhoneNumber;
import org.opencrx.kernel.depot1.jmi1.ProductDepotPosition;
import org.opencrx.kernel.depot1.jmi1.SimpleBooking;
import org.opencrx.kernel.depot1.jmi1.SingleBooking;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.product1.jmi1.Product;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.kernel.exception.BasicException;
import org.w3c.format.DateTimeFormat;

public class Depots extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Depots());
	}
	
    //-------------------------------------------------------------------------
	public static Depots getInstance(
	) throws ServiceException {
		return getInstance(Depots.class);
	}

	//-------------------------------------------------------------------------
	protected Depots(
	) {
		
	}
	
    //-----------------------------------------------------------------------
    /**
     * @return Returns the depot segment.
     */
    public org.opencrx.kernel.depot1.jmi1.Segment getDepotSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.depot1.jmi1.Segment) pm.getObjectById(
            new Path("xri://@openmdx*org.opencrx.kernel.depot1").getDescendant("provider", providerName, "segment", segmentName)
        );
    }

    //-----------------------------------------------------------------------
    public void testForOpenPosition(
        Date valueDate,
        short bookingType,
        DepotPosition depotPosition
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depotPosition);
        DepotEntity depotEntity = (DepotEntity)pm.getObjectById(depotPosition.refGetPath().getPrefix(7));
        Collection<BookingPeriod> bookingPeriods = depotEntity.getBookingPeriod();
        Depot depot = (Depot)pm.getObjectById(depotPosition.refGetPath().getParent().getParent());  
        String depotNumber = depot.getDepotNumber();
        String positionName = depotPosition.getName();        
        // Check for depot position isLocked
        if(depotPosition.isLocked()) { 
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
            (depotPosition.getClosingDate() != null) && 
            (valueDate.compareTo(depotPosition.getClosingDate()) >= 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_IS_CLOSED,
                "Depot position is closed",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", positionName),
                new BasicException.Parameter("param2", depotPosition.getClosingDate())
            );
        }
        // Check for opening date of depot position
        if(
            (depotPosition.getOpeningDate() != null) && 
            (valueDate.compareTo(depotPosition.getOpeningDate()) < 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_IS_NOT_OPEN,
                "Depot position is not open",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", positionName),
                new BasicException.Parameter("param2", depotPosition.getOpeningDate())
            );
        }        
        // Check for depot isLocked
        if(depot.isLocked()) { 
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_LOCKED,
                "Depot is locked",
                new BasicException.Parameter("param0", depotNumber)
            );
        }
        // Check for closing date of depot
        if(
            (depot.getClosingDate() != null) && 
            (valueDate.compareTo(depot.getClosingDate()) >= 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_CLOSED_CAN_NOT_BOOK,
                "Depot is closed",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", depot.getClosingDate())
            );
        }
        // Check for opening date of depot
        if(
            (depot.getOpeningDate() != null) && 
            (valueDate.compareTo(depot.getOpeningDate()) < 0) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_IS_NOT_OPEN,
                "Depot is not open",
                new BasicException.Parameter("param0", depotNumber),
                new BasicException.Parameter("param1", depot.getOpeningDate())                
            );
        }
        // Find booking period matching value date
        BookingPeriod bookingPeriod = null;
        for(BookingPeriod period: bookingPeriods) {
            Date periodStartsAt = period.getPeriodStartsAt();
            Date periodEndsAtExclusive = period.getPeriodEndsAtExclusive();            
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
        String bookingPeriodName = bookingPeriod.getName();        
        // Check for non-final booking period
        if(
            (bookingPeriod.isFinal() != null) &&  
            bookingPeriod.isFinal().booleanValue()
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
            (bookingPeriod.isClosed() != null) && 
            bookingPeriod.isClosed().booleanValue() &&
            (bookingType <= bookingPeriod.getClosingBookingTypeThreshold()) 
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_BOOKING_PERIOD_IS_CLOSED,
                "Booking period is closed",
                new BasicException.Parameter("param0", bookingPeriodName),
                new BasicException.Parameter("param1", bookingPeriod.getClosingBookingTypeThreshold())
            );
        }
    }
    
    //-----------------------------------------------------------------------
    public void testForBalance(
        CompoundBooking cb,
        BigDecimal balance,
        PersistenceManager pm
    ) throws ServiceException {
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
        	(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
        		cb.refGetPath().getParent().getParent()
        	);
    	CreditBookingQuery creditBookingQuery = (CreditBookingQuery)pm.newQuery(CreditBooking.class);
    	creditBookingQuery.thereExistsCb().equalTo(cb);
    	List<CreditBooking> creditBookings = depotSegment.getBooking(creditBookingQuery);
        BigDecimal compoundBalance = BigDecimal.ZERO;
    	for(CreditBooking booking: creditBookings) {
            compoundBalance = compoundBalance.add(
                booking.getQuantityCredit()
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
    public CompoundBooking createCreditDebitBooking(
        DepotEntity depotEntity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        BookingText bookingText,
        DepotPosition positionCredit,
        DepotPosition positionDebit,
        BookingOrigin originIdentity,
        CompoundBooking reversalOf,
        List<String> errors
    ) throws ServiceException {        
        return this.createCompoundBooking(
            depotEntity,
            valueDate,
            bookingType,
            new BigDecimal[]{quantity},
            new String[]{bookingTextName},
            new BookingText[]{bookingText},
            new DepotPosition[]{positionCredit},
            new DepotPosition[]{positionDebit},
            new BookingOrigin[]{originIdentity},
            reversalOf,
            errors
        );
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createCompoundBooking(
        DepotEntity depotEntity,
        Date valueDate,
        short bookingType,
        BigDecimal[] quantities,
        String[] bookingTextNames,
        BookingText[] bookingTexts,
        DepotPosition[] creditPositions,
        DepotPosition[] debitPositions,
        BookingOrigin[] origins,
        CompoundBooking reversalOf,
        List<String> errors
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depotEntity);    	
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
        	(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
        		depotEntity.refGetPath().getParent().getParent()
        	);    	
        // Set default valueDate to current date
        if(valueDate == null) {
            valueDate = new Date();
        }
        if(bookingTexts == null && bookingTextNames != null) {
        	bookingTexts = new BookingText[bookingTextNames.length];
        }
        // Assert open positions
        for(int i = 0; i < creditPositions.length; i++) {
        	this.testForOpenPosition(
                valueDate,
                bookingType,
                creditPositions[i]
            );
        	this.testForOpenPosition(
                valueDate,
                bookingType,
                debitPositions[i]
            );
        }        
        // Test for matching balance
        BigDecimal balance = BigDecimal.ZERO;
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
                balance,
                pm
            );
        }        
        // bookingText
        for(int i = 0; i < bookingTextNames.length; i++) {             
            if(bookingTexts[i] == null) {
                if(bookingTextNames[i] != null) {
                	BookingTextQuery bookingTextQuery = (BookingTextQuery)pm.newQuery(BookingText.class);
                	bookingTextQuery.name().equalTo(bookingTextNames[i]);
                	List<BookingText> texts = depotEntity.getBookingText(bookingTextQuery);
                    if(!texts.isEmpty()) {
                        bookingTexts[i] = texts.iterator().next();
                    }
                }            
            }
            if(bookingTexts[i] == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_BOOKING_TEXT,
                   "Missing booking text",
                   new BasicException.Parameter("param0", bookingTextNames[i])                   
               );
            }
        }
        // creditPositions
        for(int i = 0; i < creditPositions.length; i++) {
            if(creditPositions[i] == null) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_MISSING_POSITION_CREDIT,
                   "Missing credit position"
               );            
            }
        }
        // debitPositions
        for(int i = 0; i < debitPositions.length; i++) {
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
                (creditPositions[i].getName() == null) ||
                (debitPositions[i].getName() == null) ||
                !creditPositions[i].getName().equals(debitPositions[i].getName())
            ) {
               throw new ServiceException(
                   OpenCrxException.DOMAIN,
                   OpenCrxException.DEPOT_POSITION_NAME_MISMATCH,
                   "position names debit/credit do not match",
                   new BasicException.Parameter("param0", creditPositions[i].getName()),
                   new BasicException.Parameter("param1", debitPositions[i].getName())
               );                            
            }
        }
        Date bookingDate = new Date();
        // Create compound booking        
        CompoundBooking compoundBooking = pm.newInstance(CompoundBooking.class);
        String positionName0 = creditPositions[0].getName();                    
        // depotCredit
        Depot depotCredit0 = (Depot)pm.getObjectById(
            creditPositions[0].refGetPath().getParent().getParent()
        );
        String depotNumberCredit0 = depotCredit0.getDepotNumber();
        // depotDebit
        Depot depotDebit0 = (Depot)pm.getObjectById(
            debitPositions[0].refGetPath().getParent().getParent()
        );
        String depotNumberDebit0 = depotDebit0.getDepotNumber();        
        if(!bookingTexts[0].isCreditFirst()) {
            compoundBooking.setName(
                depotNumberCredit0 + " " + bookingTexts[0].getCbNameInfix1() + " " + positionName0 + " " + bookingTexts[0].getCbNameInfix2() + " " + depotNumberDebit0  
            );
        }
        else {
            compoundBooking.setName(
                depotNumberDebit0 + " " + bookingTexts[0].getCbNameInfix1() + " " + positionName0 + " " + bookingTexts[0].getCbNameInfix2() + " " + depotNumberCredit0 
            );            
        }
        compoundBooking.setBookingType(new Short(bookingType));
        compoundBooking.setBookingStatus(new Short(BOOKING_STATUS_PENDING));
        compoundBooking.setBookingDate(bookingDate);
        if(reversalOf != null) {            
            compoundBooking.setReversalOf(reversalOf);
        }
        // Create compound booking
        depotSegment.addCb(
        	true,
        	this.getUidAsString(),
        	compoundBooking
        );            
        // Create bookings
        for(
            int i = 0;
            i < creditPositions.length;
            i++
        ) {
            String positionName = creditPositions[i].getName();                        
            // depotCredit
            Depot depotCredit = (Depot)pm.getObjectById(
                creditPositions[i].refGetPath().getParent().getParent()
            );
            String depotNumberCredit = depotCredit.getDepotNumber();    
            // depotDebit
            Depot depotDebit = (Depot)pm.getObjectById(
                debitPositions[i].refGetPath().getParent().getParent()
            );
            String depotNumberDebit = depotDebit.getDepotNumber();    
            // Create credit booking
            CreditBooking bookingCredit = pm.newInstance(CreditBooking.class);
            bookingCredit.setName(
                depotNumberCredit + " " + bookingTexts[i].getCreditBookingNameInfix() + " " + positionName
            );
            bookingCredit.setQuantityCredit(quantities[i]);
            bookingCredit.setValueDate(valueDate);
            bookingCredit.setBookingType(new Short(bookingType));
            bookingCredit.setBookingStatus(new Short(BOOKING_STATUS_PENDING));
            bookingCredit.setBookingDate(bookingDate);
            bookingCredit.setCb(compoundBooking);
            bookingCredit.setPosition(creditPositions[i]);
            if(origins[i] != null) {
                bookingCredit.setOrigin(origins[i]);
            }
            depotSegment.addBooking(
            	false,
            	this.getUidAsString(),
            	bookingCredit
            );
            // Create debit booking
            DebitBooking bookingDebit = pm.newInstance(DebitBooking.class);
            bookingDebit.setName(
                depotNumberDebit + " " + bookingTexts[i].getDebitBookingNameInfix() + " " + positionName
            );
            bookingDebit.setQuantityDebit(quantities[i]);
            bookingDebit.setValueDate(valueDate);
            bookingDebit.setBookingType(new Short(bookingType));
            bookingDebit.setBookingStatus(new Short(BOOKING_STATUS_PENDING));
            bookingDebit.setBookingDate(bookingDate);
            bookingDebit.setCb(compoundBooking);
            bookingDebit.setPosition(debitPositions[i]);
            if(origins != null) {
                bookingDebit.setOrigin(origins[i]);
            }
            depotSegment.addBooking(
            	false,
            	this.getUidAsString(),
            	bookingDebit
            );
        }        
        return compoundBooking;
    }
    
    //-----------------------------------------------------------------------
    public DepotPosition getAndCreateDepotPosition(
        DepotEntity depotEntity,
        String depotNumber,
        Depot depot,
        String positionName,
        Product product,
        Date openingDate
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depotEntity);    	
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
        	(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
        		depotEntity.refGetPath().getParent().getParent()
        	);    	
    	if(depot == null) {
	        if(depotNumber == null) {
	           throw new ServiceException(
	               OpenCrxException.DOMAIN,
	               OpenCrxException.DEPOT_MISSING_DEPOT_NUMBER,
	               "Missing depot number"
	           );                                        
	        }
	        DepotQuery depotQuery = (DepotQuery)pm.newQuery(Depot.class);
	        depotQuery.identity().like(
	        	depotEntity.refGetPath().getDescendant(new String[]{"depotHolder", ":*", "depot", ":*"}).toXRI()        	
	        );
	        depotQuery.depotNumber().equalTo(depotNumber);
	        List<Depot> depots = depotSegment.getExtent(depotQuery);
	        if(!depots.isEmpty()) {
	            depot = depots.iterator().next();
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
    	DepotPositionQuery depotPositionQuery = (DepotPositionQuery)pm.newQuery(DepotPosition.class);
    	depotPositionQuery.name().equalTo(positionName);
    	List<DepotPosition> depotPositions = depot.getPosition(depotPositionQuery);
	    if(!depotPositions.isEmpty()) {
	        return depotPositions.iterator().next();
	    }
	    // On-demand creation of position
	    else {
	        if(depot.isAllowPositionAutoCreate()) {
	        	return this.openDepotPosition(
	        		depot, 
	        		positionName, 
	        		null, 
	        		openingDate, 
	        		null, 
	        		product, 
	        		Boolean.FALSE 
	        	);
	        }
	        else {
	            return null;
	        }
	    }
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByPosition(
        DepotEntity depotEntity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        BookingText bookingText,
        DepotPosition positionCredit,
        DepotPosition positionDebit,
        BookingOrigin origin,
        CompoundBooking reversalOf,
        List<String> errors
    ) throws ServiceException {
        return this.createCreditDebitBooking(                
            depotEntity,
            valueDate,
            bookingType,
            quantity,
            bookingTextName,
            bookingText,
            positionCredit,
            positionDebit,
            origin,
            reversalOf,
            errors
        );
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByProduct(
        DepotEntity depotEntity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        BookingText bookingText,
        Product product,
        String depotNumberCredit,
        Depot depotCredit,
        String depotNumberDebit,
        Depot depotDebit,
        BookingOrigin origin,
        CompoundBooking reversalOf,
        List<String> errors
    ) throws ServiceException {
        if(product == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_MISSING_PRODUCT,
               "Missing product"
           );                                    
        }
        String positionName = product.getProductNumber() != null ? 
        	product.getProductNumber() : 
        	product.getName();            
        DepotPosition positionCredit = this.getAndCreateDepotPosition(
            depotEntity,
            depotNumberCredit,
            depotCredit,
            positionName,
            product,
            valueDate
        );
        if(positionCredit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_CREDIT,
               "Can not get/create credit depot position"
           );                                                
        }
        DepotPosition positionDebit = this.getAndCreateDepotPosition(
            depotEntity,
            depotNumberDebit,
            depotDebit,
            positionName,
            product,
            valueDate
        );
        if(positionDebit == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.DEPOT_INVALID_POSITION_DEBIT,
               "Can not get/create debit depot position"
           );                                                
        }
        return this.createCreditDebitBooking(
            depotEntity,
            valueDate,
            bookingType,
            quantity,            
            bookingTextName,
            bookingText,
            positionCredit,
            positionDebit,
            origin,
            reversalOf,
            errors
        );
    }
    
    //-----------------------------------------------------------------------
    public CompoundBooking createBookingByPositionName(
        DepotEntity depotEntity,
        Date valueDate,
        short bookingType,
        BigDecimal quantity,
        String bookingTextName,
        BookingText bookingText,
        String positionName,
        String depotNumberCredit,
        Depot depotCredit,
        String depotNumberDebit,
        Depot depotDebit,
        BookingOrigin origin,
        CompoundBooking reversalOf,
        List<String> errors
    ) throws ServiceException {
        DepotPosition positionCredit = this.getAndCreateDepotPosition(
            depotEntity,
            depotNumberCredit,
            depotCredit,
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
        DepotPosition positionDebit = this.getAndCreateDepotPosition(
            depotEntity,
            depotNumberDebit,
            depotDebit,
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
        return this.createCreditDebitBooking(
            depotEntity,
            valueDate,
            bookingType,
            quantity,            
            bookingTextName,
            bookingText,
            positionCredit,
            positionDebit,
            origin,
            reversalOf,
            errors
        );
    }
    
    /**
     * Re-calculate depot reports.
     * 
     * @param depot
     * @param report
     * @param reportPreviousPeriod
     * @throws ServiceException
     */
    public void refreshReport(
        Depot depot,
        DepotReport report,
        DepotReport reportPreviousPeriod
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depot);
    	org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
    		(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
    			depot.refGetPath().getPrefix(5)
    		);
        // Refresh only only draft reports
        if(report.isDraft()) {
        	Collection<DepotReportItemPosition> itemPositions = report.getItemPosition();
        	for(DepotReportItemPosition itemPosition: itemPositions) {
        		itemPosition.refDelete();        		
        	}        	
            BookingPeriod bookingPeriod = report.getBookingPeriod();
            Date periodStartsAt = bookingPeriod.getPeriodStartsAt();
            // Create a position item for all depot positions
            Collection<DepotPosition> positions =  depot.getPosition();
            for(DepotPosition position: positions) {
                // Set beginning of report balances to end of period balances of previous report 
                BigDecimal balanceBop = null;
                BigDecimal balanceDebitBop = null;
                BigDecimal balanceCreditBop = null;
                BigDecimal balanceCredit = null;
                BigDecimal balanceDebit = null;
                BigDecimal balanceSimple = null;
                BigDecimal balanceSimpleBop = null;
                if(reportPreviousPeriod != null) {
                	DepotReportItemPositionQuery itemPositionQuery = (DepotReportItemPositionQuery)pm.newQuery(DepotReportItemPosition.class);
                	itemPositionQuery.thereExistsPosition().equalTo(position);
                	List<DepotReportItemPosition> items = reportPreviousPeriod.getItemPosition(itemPositionQuery);
                    if(!items.isEmpty()) {
                        DepotReportItemPosition itemPosition = items.iterator().next();
                        balanceBop = itemPosition.getBalance();                        
                        balanceCreditBop = itemPosition.getBalanceCredit();
                        balanceDebitBop = itemPosition.getBalanceDebit();
                        balanceCredit = itemPosition.getBalanceCredit();
                        balanceDebit = itemPosition.getBalanceDebit();
                        balanceSimpleBop = itemPosition.getBalanceSimple();                                                
                        balanceSimple = itemPosition.getBalanceSimple();
                    }
                }
                balanceBop = balanceBop == null ? BigDecimal.ZERO : balanceBop;
                balanceDebitBop = balanceDebitBop == null ? BigDecimal.ZERO : balanceDebitBop;
                balanceCreditBop = balanceCreditBop == null ? BigDecimal.ZERO : balanceCreditBop;
                balanceCredit = balanceCredit == null ? BigDecimal.ZERO : balanceCredit;
                balanceDebit = balanceDebit == null ? BigDecimal.ZERO : balanceDebit;                
                balanceSimpleBop = balanceSimpleBop == null ? BigDecimal.ZERO : balanceSimpleBop;
                balanceSimple = balanceSimple == null ? BigDecimal.ZERO : balanceSimple;
                // Create depot report item for position
                DepotReportItemPosition itemPosition = pm.newInstance(DepotReportItemPosition.class);
                itemPosition.setPositionName(position.getName());
                itemPosition.setValueDate(periodStartsAt);
                itemPosition.setPosition(position);
                itemPosition.setBalanceBop(balanceBop);
                itemPosition.setBalanceCreditBop(balanceCreditBop);
                itemPosition.setBalanceDebitBop(balanceDebitBop);
                itemPosition.setBalanceSimpleBop(balanceSimpleBop);
                report.addItemPosition(
                	false,
                	this.getUidAsString(),
                	itemPosition
                );
                // Get single bookings for this position item
                SingleBookingQuery singleBookingQuery = (SingleBookingQuery)pm.newQuery(SingleBooking.class);
                singleBookingQuery.thereExistsPosition().equalTo(position);                
                List<SingleBooking> singleBookings = depotSegment.getBooking(singleBookingQuery);
                // Sum up single bookings
                for(SingleBooking singleBooking: singleBookings) {
                    // Credit booking
                    if(singleBooking instanceof CreditBooking) {
                        BigDecimal quantityCredit = ((CreditBooking)singleBooking).getQuantityCredit();
                        balanceCredit = balanceCredit.add(quantityCredit);
                    }
                    // Debit booking
                    else if(singleBooking instanceof DebitBooking) {
                        BigDecimal quantityDebit = ((DebitBooking)singleBooking).getQuantityDebit();
                        balanceDebit = balanceDebit.add(quantityDebit);
                    }
                }
                // Get simple bookings for this position item
                SimpleBookingQuery simpleBookingQuery = (SimpleBookingQuery)pm.newQuery(SimpleBooking.class);
                simpleBookingQuery.thereExistsPosition().equalTo(position);
                List<SimpleBooking> simpleBookings = depotSegment.getSimpleBooking(simpleBookingQuery);
                // Sum up simple bookings
                for(SimpleBooking simpleBooking: simpleBookings) {
                    BigDecimal quantity = simpleBooking.getQuantity();
                    balanceSimple = balanceSimple.add(quantity);
                }
                // Update balances
                itemPosition.setBalance(balanceCredit.subtract(balanceDebit));
                itemPosition.setBalanceCredit(balanceCredit);
                itemPosition.setBalanceDebit(balanceDebit);
                itemPosition.setBalanceSimple(balanceSimple);
            }            
        }
    }
    
    /**
     * Re-calculate depot reports.
     * 
     * @param depot
     * @param bookingStatusThreshold
     * @throws ServiceException
     */
    public void assertReports(
        Depot depot,
        short bookingStatusThreshold
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depot);    	
        DepotEntity depotEntity = (DepotEntity)pm.getObjectById(
        	depot.refGetPath().getPrefix(7)
        );
        BookingPeriodQuery bookingPeriodQuery = (BookingPeriodQuery)pm.newQuery(BookingPeriod.class);
        bookingPeriodQuery.orderByPeriodStartsAt().ascending();
        List<BookingPeriod> bookingPeriods = depotEntity.getBookingPeriod(bookingPeriodQuery);
        DepotReport reportPreviousPeriod = null;
        DepotReport latestReport = null;        
        // Assert report for each booking period
        for(BookingPeriod bookingPeriod: bookingPeriods) {
        	DepotReportQuery depotReportQuery = (DepotReportQuery)pm.newQuery(DepotReport.class);
        	depotReportQuery.thereExistsBookingPeriod().equalTo(bookingPeriod);
        	List<DepotReport> reports = depot.getReport(depotReportQuery);
            DepotReport report = null;
            if(!reports.isEmpty()) {
                report = reports.iterator().next();
            }
            else {
                DepotReport newReport = pm.newInstance(DepotReport.class);
                newReport.setName(bookingPeriod.getName());
                newReport.setDescription(bookingPeriod.getDescription());
                newReport.setDraft(Boolean.TRUE);
                newReport.setBookingStatusThreshold(new Short(bookingStatusThreshold));
                newReport.setBookingPeriod(bookingPeriod);
                depot.addReport(
                	false,
                	this.getUidAsString(),
                	newReport
                );
                report = newReport;
            }
            // Latest report
            Date currentDate = new Date();
            if(
               (currentDate.compareTo(bookingPeriod.getPeriodStartsAt()) >= 0) &&
               ((bookingPeriod.getPeriodEndsAtExclusive() == null) || (currentDate.compareTo(bookingPeriod.getPeriodEndsAtExclusive()) < 0))
            ) {
                latestReport = report;
            }
            // Refresh
            this.refreshReport(
                depot,
                report,
                reportPreviousPeriod
            );            
            reportPreviousPeriod = report;
        }
        if(latestReport != null) {
            depot.setLatestReport(latestReport);
        }
    }
    
    /**
     * Cancel compound booking.
     * 
     * @param cb
     * @param errors
     * @return
     * @throws ServiceException
     */
    public CompoundBooking cancelCompoundBooking(
        CompoundBooking cb,
        List<String> errors
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(cb);    	
        boolean isFinal = cb.getBookingStatus() == BOOKING_STATUS_FINAL;
        if(!isFinal) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PROCESSED,
                "Booking status must be processed. Cancel is not allowed."
            );                                                
        }
        short bookingType = cb.getBookingType() > 0 ? 
        	cb.getBookingType() : 
        	BOOKING_TYPE_STANDARD;
        // Can not cancel reversal bookings
        if(bookingType == BOOKING_TYPE_REVERSAL) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_CAN_NOT_CANCEL_REVERSAL_BOOKING,
                "Can not cancel reversal booking"
            );                        
        }
        // Check whether compound booking already has a reversal booking
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
        	(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
        		cb.refGetPath().getParent().getParent()
        	);
        CompoundBookingQuery cbQuery = (CompoundBookingQuery)pm.newQuery(CompoundBooking.class);
        cbQuery.thereExistsReversalOf().equalTo(cb);
        List<CompoundBooking> compoundBookings = depotSegment.getCb(cbQuery);
        if(!compoundBookings.isEmpty()) {
        	CompoundBooking reversal = compoundBookings.iterator().next();
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_ALREADY_HAS_REVERSAL_BOOKING,
                "Compound booking already cancelled",
                new BasicException.Parameter("param0", reversal.getName() + " / " + reversal.getBookingDate())
            );                                    
        }
        // Create cancel compound booking
        CompoundBooking cancelCb = pm.newInstance(CompoundBooking.class);
        if(cb.getName() != null) {
            cancelCb.setName(cb.getName());
        }
        if(cb.getDescription() != null) {
            cancelCb.setDescription(cb.getDescription());
        }
        cancelCb.setBookingDate(new Date());
        cancelCb.setBookingType(new Short(BOOKING_TYPE_REVERSAL));
        cancelCb.setBookingStatus(new Short(BOOKING_STATUS_PENDING));
        cancelCb.setReversalOf(cb);
        depotSegment.addCb(
        	false,
        	this.getUidAsString(),
        	cancelCb
        );
        // Create cancel bookings
        SingleBookingQuery singleBookingQuery = (SingleBookingQuery)pm.newInstance(SingleBooking.class);
        singleBookingQuery.thereExistsCb().equalTo(cb);
        List<SingleBooking> bookings = depotSegment.getBooking(singleBookingQuery);
        for(SingleBooking booking: bookings) {
        	SingleBooking cancelBooking = null;
        	if(booking instanceof CreditBooking) {
        		cancelBooking = pm.newInstance(DebitBooking.class);
        		((DebitBooking)cancelBooking).setQuantityDebit(((CreditBooking)booking).getQuantityCredit());
        	}
        	else if(booking instanceof DebitBooking){
        		cancelBooking = pm.newInstance(CreditBooking.class);
        		((CreditBooking)cancelBooking).setQuantityCredit(((DebitBooking)booking).getQuantityDebit());
        	}
        	cancelBooking.setName(booking.getName());
        	cancelBooking.setDescription(booking.getDescription());
        	cancelBooking.setValueDate(booking.getValueDate());
        	cancelBooking.setBookingDate(new Date());
        	cancelBooking.setPosition(booking.getPosition());
        	this.testForOpenPosition(
                cancelBooking.getValueDate(),
                BOOKING_TYPE_REVERSAL,
                cancelBooking.getPosition()
            );
            cancelBooking.setBookingType(new Short(BOOKING_TYPE_REVERSAL));
            cancelBooking.setBookingStatus(new Short(BOOKING_STATUS_PENDING));
            cancelBooking.setCb(cancelCb);
            depotSegment.addBooking(
            	false,
            	this.getUidAsString(),
            	cancelBooking
            );
        }
        return cancelCb;
    }

    /**
     * Accept compound booking.
     * 
     * @param compoundBooking
     * @throws ServiceException
     */
    public void acceptCompoundBooking(
        CompoundBooking compoundBooking
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(compoundBooking);    	
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status must be pending. Accept is not allowed."
            );                                                
        }
        List<String> principals = UserObjects.getPrincipalChain(pm);
        String acceptedBy = principals.isEmpty() ? "NA" : principals.get(0) + " @ " + DateTimeFormat.BASIC_UTC_FORMAT.format(new Date());
        compoundBooking.getAcceptedBy().add(acceptedBy);
    }
    
    /**
     * Finalize compound booking.
     * 
     * @param cb
     * @throws ServiceException
     */
    public void finalizeCompoundBooking(
        CompoundBooking cb
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(cb);    	
        boolean isPending = cb.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status must be pending. Finalize is not allowed."
            );                                                
        }
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
        	(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
        		cb.refGetPath().getParent().getParent()
        	);        
        // Process bookings
        SingleBookingQuery singleBookingQuery = (SingleBookingQuery)pm.newQuery(SingleBooking.class);
        singleBookingQuery.thereExistsCb().equalTo(cb);
        List<SingleBooking> bookings = depotSegment.getBooking(singleBookingQuery);
        for(SingleBooking booking: bookings) {
        	this.testForOpenPosition(
                booking.getValueDate(),
                booking.getBookingType(),
                booking.getPosition()
            );
            booking.setBookingStatus(new Short(BOOKING_STATUS_FINAL));
        }
        cb.setBookingStatus(new Short(BOOKING_STATUS_FINAL));        
    }

    /**
     * Remove compound booking.
     * 
     * @param compoundBooking
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeCompoundBooking(
        CompoundBooking compoundBooking,
        boolean preDelete
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(compoundBooking);    	
    	org.opencrx.kernel.depot1.jmi1.Segment depotSegment = 
    		(org.opencrx.kernel.depot1.jmi1.Segment)pm.getObjectById(
    			compoundBooking.refGetPath().getPrefix(5)
    		);
        // isPending
        boolean isPending = compoundBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Delete is not allowed."
            );                                                
        }
        // isLocked
        boolean isLocked = compoundBooking.isLocked();
        if(isLocked) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_IS_LOCKED_CAN_NOT_DELETE,
                "Compound booking is locked. Delete is not allowed."
            );                                                
        }
        // Delete bookings
        SingleBookingQuery bookingQuery = (SingleBookingQuery)pm.newQuery(SingleBooking.class);
        bookingQuery.thereExistsCb().equalTo(compoundBooking);
        List<SingleBooking> bookings = depotSegment.getBooking(bookingQuery);
        for(SingleBooking booking: bookings) {
        	booking.refDelete();
        }
        if(!preDelete) {
        	compoundBooking.refDelete();
        }
    }

    /**
     * Remove simple booking.
     * 
     * @param simpleBooking
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeSimpleBooking(
        SimpleBooking simpleBooking,
        boolean preDelete
    ) throws ServiceException {
        boolean isPending = simpleBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Delete is not allowed."
            );                                                
        }
        if(!preDelete) {
        	simpleBooking.refDelete();
        }
    }

    /**
     * Remove single booking.
     * 
     * @param singleBooking
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeSingleBooking(
        SingleBooking singleBooking,
        boolean preDelete
    ) throws ServiceException {
        boolean isPending = singleBooking.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Delete is not allowed."
            );                                                
        }
        if(!preDelete) {
        	singleBooking.refDelete();
        }
    }

    /**
     * Test for bookings having a depot position which is composite of
     * the given booking target. The booking target can be a depot entity, 
     * a depot holder, a depot or a depot position.
     * 
     * @param bookingTarget
     * @return
     * @throws ServiceException
     */
    public boolean hasBookings(
        CrxObject bookingTarget
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(bookingTarget);
    	String providerName = bookingTarget.refGetPath().get(2);
    	String segmentName = bookingTarget.refGetPath().get(4);
        org.opencrx.kernel.depot1.jmi1.Segment depotSegment = this.getDepotSegment(pm, providerName, segmentName);
        SingleBookingQuery bookingQuery = (SingleBookingQuery)pm.newQuery(SingleBooking.class);
        bookingQuery.thereExistsPosition().elementOf(
        	PersistenceHelper.getCandidates(
        		pm.getExtent(DepotPosition.class, true),
        		bookingTarget.refMofId() + (bookingTarget.refGetPath().size() < 13 ? "/($...)" : "")
        	)
        );
        List<SingleBooking> bookings = depotSegment.getBooking(bookingQuery);
        return !bookings.isEmpty();
    }

    /**
     * Remove depot entity. Test for existing bookings.
     * 
     * @param depotEntity
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeDepotEntity(
        DepotEntity depotEntity,
        boolean preDelete
    ) throws ServiceException {
        if(this.hasBookings(depotEntity)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_ENTITY_HAS_BOOKINGS,
                "Depot entity has bookings."
            );                                                                        
        }
        if(!preDelete) {
        	depotEntity.refDelete();
        }
    }
    
    /**
     * Remove depot holder. Test for existing bookings.
     * 
     * @param depotHolder
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeDepotHolder(
        DepotHolder depotHolder,
        boolean preDelete
    ) throws ServiceException {
        if(this.hasBookings(depotHolder)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_CONTRACT_HAS_BOOKINGS,
                "Depot holder has bookings."
            );                                                                        
        }
        if(!preDelete) {
        	depotHolder.refDelete();
        }
    }
    
    /**
     * Remove depot. Test for existing bookings.
     * 
     * @param depot
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeDepot(
        Depot depot,
        boolean preDelete
    ) throws ServiceException {
        if(this.hasBookings(depot)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_DEPOT_HAS_BOOKINGS,
                "Depot has bookings."
            );                                                                        
        }
        if(!preDelete) {
        	depot.refDelete();
        }
    }
    
    /**
     * Remove depot position. Test for existing bookings.
     * 
     * @param depotPosition
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeDepotPosition(
        DepotPosition depotPosition,
        boolean preDelete
    ) throws ServiceException {
        if(this.hasBookings(depotPosition)) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DEPOT_POSITION_HAS_BOOKINGS,
                "Depot position has bookings."
            );                                                       
        }
        if(!preDelete) {
        	depotPosition.refDelete();
        }
    }
    
    /**
     * Remove depot group.
     * 
     * @param depotGroup
     * @param preDelete
     * @throws ServiceException
     */
    protected void removeDepotGroup(
        DepotGroup depotGroup,
        boolean preDelete
    ) throws ServiceException {
        if(!preDelete) {
        	depotGroup.refDelete();
        }
    }

    //-----------------------------------------------------------------------
    public Depot openDepot(
        DepotHolder depotHolder,
        String name,
        String description,
        String depotNumber,
        Date openingDate,
        DepotType depotType,
        DepotGroup depotGroup,
        List<String> errors
    ) throws ServiceException {
        if(depotNumber == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_NUMBER_REQUIRED,
                "Depot number is required."
            );                                                            
        }
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depotHolder);        
        Depot depot = pm.newInstance(Depot.class);
        if(name != null) {
            depot.setName(name);
        }
        if(description != null) {
            depot.setDescription(description);
        }
        depot.setDepotNumber(depotNumber);        
        depot.setOpeningDate(
            openingDate == null ? 
            	new Date() : 
            	openingDate
        );
        if(depotType != null) {
            depot.getDepotType().add(depotType);
        }
        if(depotGroup != null) {
            depot.setDepotGroup(depotGroup);
        }
        depot.setDefault(Boolean.FALSE);
        depot.setLocked(Boolean.FALSE);
        depot.setAllowPositionAutoCreate(Boolean.FALSE);
        depotHolder.addDepot(
        	false,
        	this.getUidAsString(),
        	depot
        );
        return depot;
    }
    
    //-----------------------------------------------------------------------
    public void closeDepot(
        Depot depot,
        Date closingDate,
        List<String> errors
    ) throws ServiceException {
        if(depot.getClosingDate() != null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_IS_CLOSED_CAN_NOT_CLOSE,
                "Depot is closed. Can not close."
            );                                                                        
        }
        depot.setLocked(Boolean.TRUE);
        depot.setClosingDate(
            closingDate == null ? 
            	new Date() : 
            	closingDate
        );        
    }
    
    //-----------------------------------------------------------------------
    public DepotPosition openDepotPosition(
        Depot depot,
        String positionName,
        String positionDescription,
        Date openingDate,
        String depotPositionQualifier,
        Product product,
        Boolean isLocked
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(depot);    	
    	DepotPosition depotPosition = null;
        if(product != null) {
            // Check whether position with given productRole already
            // exists. If yes, do not create new position.
            List<ProductDepotPosition> positions = null;
            if(depotPositionQualifier == null) {
            	ProductDepotPositionQuery depotPositionQuery = (ProductDepotPositionQuery)pm.newQuery(ProductDepotPosition.class);
            	depotPositionQuery.thereExistsProduct().equalTo(product);
            	positions = depot.getPosition(depotPositionQuery);
            }
            // qualifier and productRole must match
            else {
            	ProductDepotPositionQuery depotPositionQuery = (ProductDepotPositionQuery)pm.newQuery(ProductDepotPosition.class);
            	depotPositionQuery.thereExistsProduct().equalTo(product);
            	depotPositionQuery.thereExistsQualifier().equalTo(depotPositionQualifier);
            	positions = depot.getPosition(depotPositionQuery);
            }
            if(!positions.isEmpty()) {
                return positions.iterator().next();
            }
            depotPosition = pm.newInstance(ProductDepotPosition.class);
            ((ProductDepotPosition)depotPosition).setProduct(product);
        }
        else {
        	depotPosition = pm.newInstance(DepotPosition.class);
        }        
        // In case a depot position qualifier is specified, set name to productNumber + " #" + depotPositionQualifier.
        String name = 
            positionName != null ? 
            	positionName : 
            	product != null ?
        			product.getProductNumber() != null ?
        				product.getProductNumber() :
        					product.getName() :
        						"N/A";
        if(depotPositionQualifier != null) {
            depotPosition.setQualifier(depotPositionQualifier);
            name += " #" + depotPositionQualifier;
        }
        depotPosition.setName(name);
        // description
        depotPosition.setDescription(
            positionDescription != null ? 
            	positionDescription : 
            		product != null ? 
            			product.getDescription() : 
                        ""
        );
        if(openingDate != null) {
            depotPosition.setOpeningDate(openingDate);
        }
        depotPosition.setLocked(isLocked);
        depot.addPosition(
        	false,
        	this.getUidAsString(),
        	depotPosition
        );
        return depotPosition;
    }
    
    //-----------------------------------------------------------------------
    public void closeDepotPosition(       
        DepotPosition depotPosition,
        Date closingDate,
        List<String> errors
    ) throws ServiceException {
        if(depotPosition.getClosingDate() != null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_DEPOT_POSITION_IS_CLOSED_CAN_NOT_CLOSE,
                "Depot position is closed. Can not close."
            );                                                                        
        }
        depotPosition.setLocked(Boolean.TRUE);
        depotPosition.setClosingDate(
            closingDate == null ? 
            	new Date() : 
            	closingDate
        );        
    }
    
    //-----------------------------------------------------------------------
    public void lockCompoundBooking(
        CompoundBooking cb,
        short lockingReason
    ) throws ServiceException {
        boolean isPending = cb.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Locking is not allowed."
            );                                                
        }
        cb.setLocked(Boolean.TRUE);
        cb.setLockingReason(new Short(lockingReason));
        cb.setLockModifiedAt(new Date());
    }
    
    //-----------------------------------------------------------------------
    public void unlockCompoundBooking(
        CompoundBooking cb
    ) throws ServiceException {
        boolean isPending = cb.getBookingStatus() == BOOKING_STATUS_PENDING;
        if(!isPending) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.BOOKING_STATUS_MUST_BE_PENDING,
                "Booking status is not pending. Unlocking is not allowed."
            );                                                
        }
        cb.setLocked(Boolean.FALSE);
        cb.setLockingReason(new Short((short)0));
        cb.setLockModifiedAt(new Date());
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
		if(object instanceof CompoundBooking) {
			this.removeCompoundBooking((CompoundBooking)object, preDelete);
		} else if(object instanceof DepotEntity) {
			this.removeDepotEntity((DepotEntity)object, preDelete);
		} else if(object instanceof DepotGroup) {
			this.removeDepotGroup((DepotGroup)object, preDelete);
		} else if(object instanceof DepotHolder) {
			this.removeDepotHolder((DepotHolder)object, preDelete);
		} else if(object instanceof Depot) {
			this.removeDepot((Depot)object, preDelete);
		} else if(object instanceof DepotPosition) {
			this.removeDepotPosition((DepotPosition)object, preDelete);
		} else if(object instanceof SimpleBooking) {
			this.removeSimpleBooking((SimpleBooking)object, preDelete);
		} else if(object instanceof SingleBooking) {
			this.removeSingleBooking((SingleBooking)object, preDelete);
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
		if(object instanceof PhoneNumber) {
			Addresses.getInstance().updatePhoneNumber((PhoneNumber)object);
		}
	}

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final short BOOKING_TYPE_STANDARD = 10;
    public static final short BOOKING_TYPE_CLOSING = 20;
    public static final short BOOKING_TYPE_REVERSAL = 30;
    
    public static final short BOOKING_STATUS_PENDING = 1;
    public static final short BOOKING_STATUS_FINAL = 2;
        
    // Goods are issued to delivery.
    // Goods are returned from delivery
    public static final short DEPOT_USAGE_GOODS_ISSUE = 1;
    public static final short DEPOT_USAGE_GOODS_RETURN = 2;
    public static final short DEPOT_USAGE_GOODS_DELIVERY = 3;
    // Work effort
    public static final short DEPOT_USAGE_WORK_EFFORT = 10;
    
}

//--- End of File -----------------------------------------------------------

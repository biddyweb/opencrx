package org.opencrx.kernel.plugin.application.depot1;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot1Package;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;

public class DepotEntityImpl {
    
    //-----------------------------------------------------------------------
    public DepotEntityImpl(
        org.opencrx.kernel.depot1.jmi1.DepotEntity current,
        org.opencrx.kernel.depot1.cci2.DepotEntity next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_0)this.current.refOutermostPackage()).refUserContext();
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.CreateBookingResult createBookingByPosition(
        org.opencrx.kernel.depot1.jmi1.CreateBookingByPositionParams params
    ) {
        try {
            List<String> errors = new ArrayList<String>();
            CompoundBooking compoundBooking = 
                this.getBackend().getDepots().createBookingByPosition(
                    this.current.refGetPath(),
                    params.getValueDate(),
                    params.getBookingType(),
                    params.getQuantity(),
                    params.getBookingTextName(),
                    params.getBookingText() == null ? null : params.getBookingText().refGetPath(),
                    params.getPositionCredit() == null ? null : params.getPositionCredit().refGetPath(),
                    params.getPositionDebit() == null ? null : params.getPositionDebit().refGetPath(),
                    params.getOrigin() == null ? null : params.getOrigin().refGetPath(),
                    params.getReversalOf() == null ? null : params.getReversalOf().refGetPath(),
                    errors
                );
            if(compoundBooking == null) {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    null,
                    (short)1, 
                    errors.toString()
                );
            }
            else {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    compoundBooking,
                    (short)0, 
                    null
                );
            }
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.CreateBookingResult createBookingByPositionName(
        org.opencrx.kernel.depot1.jmi1.CreateBookingByPositionNameParams params
    ) {
        try {
            List<String> errors = new ArrayList<String>();
            CompoundBooking compoundBooking = 
                this.getBackend().getDepots().createBookingByPositionName(
                    this.current.refGetPath(), 
                    params.getValueDate(),
                    params.getBookingType(),
                    params.getQuantity(),
                    params.getBookingTextName(),
                    params.getBookingText() == null ? null : params.getBookingText().refGetPath(),
                    params.getPositionName(),
                    params.getDepotNumberCredit(),
                    params.getDepotCredit() == null ? null : params.getDepotCredit().refGetPath(),
                    params.getDepotNumberDebit(),
                    params.getDepotDebit() == null ? null : params.getDepotDebit().refGetPath(),
                    params.getOrigin() == null ? null : params.getOrigin().refGetPath(),                            
                    params.getReversalOf() == null ? null : params.getReversalOf().refGetPath(),
                    errors
                );
            if(compoundBooking == null) {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    null,
                    (short)1, 
                    errors.toString()
                );
            }
            else {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    compoundBooking,
                    (short)0, 
                    null
                );
            }
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.CreateBookingResult createBookingByProduct(
        org.opencrx.kernel.depot1.jmi1.CreateBookingByProductParams params
    ) {
        try {
            List<String> errors = new ArrayList<String>();
            CompoundBooking compoundBooking = 
                this.getBackend().getDepots().createBookingByProduct(
                    this.current.refGetPath(), 
                    params.getValueDate(),
                    params.getBookingType() ,
                    params.getQuantity(),
                    params.getBookingTextName(),
                    params.getBookingText() == null ? null : params.getBookingText().refGetPath(),
                    params.getProduct(),
                    params.getDepotNumberCredit(),
                    params.getDepotCredit() == null ? null : params.getDepotCredit().refGetPath(), 
                    params.getDepotNumberDebit(),
                    params.getDepotDebit() == null ? null : params.getDepotDebit().refGetPath(),
                    params.getOrigin() == null ? null : params.getOrigin().refGetPath(),                            
                    params.getReversalOf() == null ? null : params.getReversalOf().refGetPath(),
                    errors
                );
            if(compoundBooking == null) {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    null,
                    (short)1, 
                    errors.toString()
                );
            }
            else {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCreateBookingResult(
                    compoundBooking,
                    (short)0, 
                    null
                );
            }
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }        
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.depot1.jmi1.DepotEntity current;
    protected final org.opencrx.kernel.depot1.cci2.DepotEntity next;    
    
}

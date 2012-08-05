package org.opencrx.kernel.plugin.application.depot1;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot1Package;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class CompoundBookingImpl {

    //-----------------------------------------------------------------------
    public CompoundBookingImpl(
        org.opencrx.kernel.depot1.jmi1.CompoundBooking current,
        org.opencrx.kernel.depot1.cci2.CompoundBooking next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_3)this.current.refOutermostPackage()).refUserContext();
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.CancelCompoundBookingResult cancelCb(
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            List<String> errors = new ArrayList<String>();
            CompoundBooking compoundBooking = this.getBackend().getDepots().cancelCompoundBooking(
                this.current, 
                errors
            );
            if(compoundBooking == null) {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCancelCompoundBookingResult(
                    null,
                    (short)1, 
                    errors.toString()
                );
            }
            else {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCancelCompoundBookingResult(
                    compoundBooking,
                    (short)0, 
                    null
                );
            }
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
        
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void acceptCb(
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getDepots().acceptCompoundBooking(
                this.current
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
        
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void finalizeCb(
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getDepots().finalizeCompoundBooking(
                this.current
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
        
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void lockCb(
        org.opencrx.kernel.depot1.jmi1.LockCompoundBookingParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getDepots().lockCompoundBooking(
                this.current,
                params.getLockingReason()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
        
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void unlockCb(
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getDepots().unlockCompoundBooking(
                this.current
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.depot1.jmi1.CompoundBooking current;
    protected final org.opencrx.kernel.depot1.cci2.CompoundBooking next;    
    
}

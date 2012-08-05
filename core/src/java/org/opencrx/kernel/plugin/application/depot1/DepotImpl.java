package org.opencrx.kernel.plugin.application.depot1;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.depot1.jmi1.Depot1Package;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class DepotImpl {
    
    //-----------------------------------------------------------------------
    public DepotImpl(
        org.opencrx.kernel.depot1.jmi1.Depot current,
        org.opencrx.kernel.depot1.cci2.Depot next
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
    public org.opencrx.kernel.depot1.jmi1.CloseDepotResult closeDepot(
        org.opencrx.kernel.depot1.jmi1.CloseDepotParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            List<String> errors = new ArrayList<String>();
            this.getBackend().getDepots().closeDepot(
                this.current,
                params.getClosingDate(),
                errors
            );
            return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCloseDepotResult(
                (short)0, 
                null
            );
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.OpenDepotPositionResult openDepotPosition(
        org.opencrx.kernel.depot1.jmi1.OpenDepotPositionParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            List<String> errors = new ArrayList<String>();
            DepotPosition depotPosition = this.getBackend().getDepots().openDepotPosition(
                this.current.refGetPath(),
                params.getName(),
                params.getDescription(),
                params.getOpeningDate(),
                params.getQualifier(),
                params.getProductRole() == null ? null :  params.getProductRole().refGetPath(),
                params.getProduct() == null ? null : params.getProduct().refGetPath(),
                Boolean.FALSE
            );
            if(depotPosition == null) {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createOpenDepotPositionResult(
                    null,
                    (short)1, 
                    errors.toString()
                );
            }
            else {
                return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createOpenDepotPositionResult(
                    depotPosition,
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
    public org.openmdx.base.jmi1.Void assertReports(
        org.opencrx.kernel.depot1.jmi1.AssertReportsParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getDepots().assertReports(
                this.current.refGetPath(),
                params.getBookingStatusThreshold()
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
    protected final org.opencrx.kernel.depot1.jmi1.Depot current;
    protected final org.opencrx.kernel.depot1.cci2.Depot next;    
    
}

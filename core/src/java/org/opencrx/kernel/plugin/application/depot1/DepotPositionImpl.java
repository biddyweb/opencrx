package org.opencrx.kernel.plugin.application.depot1;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.depot1.jmi1.Depot1Package;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;

public class DepotPositionImpl {
    
    //-----------------------------------------------------------------------
    public DepotPositionImpl(
        org.opencrx.kernel.depot1.jmi1.DepotPosition current,
        org.opencrx.kernel.depot1.cci2.DepotPosition next
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
    public org.opencrx.kernel.depot1.jmi1.CloseDepotPositionResult closeDepotPosition(
        org.opencrx.kernel.depot1.jmi1.CloseDepotPositionParams params
    ) {
        try {
            List<String> errors = new ArrayList<String>();
            this.getBackend().getDepots().closeDepotPosition(
                this.current,
                params.getClosingDate(),
                errors
            );
            return ((Depot1Package)this.current.refOutermostPackage().refPackage(Depot1Package.class.getName())).createCloseDepotPositionResult(
                (short)0, 
                null
            );
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }        
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.depot1.jmi1.DepotPosition current;
    protected final org.opencrx.kernel.depot1.cci2.DepotPosition next;    
    
}

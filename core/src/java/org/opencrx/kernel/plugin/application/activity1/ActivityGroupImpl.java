package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;

public class ActivityGroupImpl {

    //-----------------------------------------------------------------------
    public ActivityGroupImpl(
        org.opencrx.kernel.activity1.jmi1.ActivityGroup current,
        org.opencrx.kernel.activity1.cci2.ActivityGroup next
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
    public org.opencrx.kernel.activity1.jmi1.CalcActualEffortResult calcActualEffort(
    ) {
        try {
            int[] actualEffort = this.getBackend().getActivities().calcActualEffort(
                this.current
            );
            return ((Activity1Package)this.current.refOutermostPackage().refPackage(Activity1Package.class.getName())).createCalcActualEffortResult(
                actualEffort[0],
                actualEffort[1]
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.activity1.jmi1.ActivityGroup current;
    protected final org.opencrx.kernel.activity1.cci2.ActivityGroup next;    
    
}

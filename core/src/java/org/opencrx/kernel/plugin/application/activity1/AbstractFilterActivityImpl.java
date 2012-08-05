package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;

public class AbstractFilterActivityImpl {

    //-----------------------------------------------------------------------
    public AbstractFilterActivityImpl(
        org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity current,
        org.opencrx.kernel.activity1.cci2.AbstractFilterActivity next
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
    public org.opencrx.kernel.base.jmi1.CountFilteredObjectsResult countFilteredActivity(
    ) {
        try {
            int count = this.getBackend().getActivities().countFilteredActivity(
                this.current.refGetPath(),
                this.current instanceof org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createCountFilteredObjectsResult(
                count
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
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
    protected final org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity current;
    protected final org.opencrx.kernel.activity1.cci2.AbstractFilterActivity next;    
    
}

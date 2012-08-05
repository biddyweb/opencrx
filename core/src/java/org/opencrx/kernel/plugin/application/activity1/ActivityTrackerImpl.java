package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class ActivityTrackerImpl {

    //-----------------------------------------------------------------------
    public ActivityTrackerImpl(
        org.opencrx.kernel.activity1.jmi1.ActivityTracker current,
        org.opencrx.kernel.activity1.cci2.ActivityTracker next
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
    public org.openmdx.base.jmi1.Void refreshItems(
    ) {
        try {
            this.getBackend().getActivities().refreshItems(
                this.current.refGetPath()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.activity1.jmi1.ActivityTracker current;
    protected final org.opencrx.kernel.activity1.cci2.ActivityTracker next;    
    
}

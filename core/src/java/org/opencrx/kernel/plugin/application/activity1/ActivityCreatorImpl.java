package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;

public class ActivityCreatorImpl {

    //-----------------------------------------------------------------------
    public ActivityCreatorImpl(
        org.opencrx.kernel.activity1.jmi1.ActivityCreator current,
        org.opencrx.kernel.activity1.cci2.ActivityCreator next
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
    public org.opencrx.kernel.activity1.jmi1.NewActivityResult newActivity(
        org.opencrx.kernel.activity1.jmi1.NewActivityParams params
    ) {
        try {        
            Activity activity = this.getBackend().getActivities().newActivity(
                this.current,
                params.getName(),
                params.getDescription(),
                params.getDetailedDescription(),
                params.getScheduledStart(),
                params.getScheduledEnd(),
                params.getDueBy(),
                params.getPriority(),
                params.getIcalType(),
                params.getReportingContact() == null ? null : params.getReportingContact().refGetPath() 
            );
            return ((Activity1Package)this.current.refOutermostPackage().refPackage(Activity1Package.class.getName())).createNewActivityResult(
                activity
            ); 
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }        
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.activity1.jmi1.ActivityCreator current;
    protected final org.opencrx.kernel.activity1.cci2.ActivityCreator next;    
    
}

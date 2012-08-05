package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityFollowUp;
import org.opencrx.kernel.activity1.jmi1.ActivityWorkRecord;
import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class ActivityImpl {

    //-----------------------------------------------------------------------
    public ActivityImpl(
        org.opencrx.kernel.activity1.jmi1.Activity current,
        org.opencrx.kernel.activity1.cci2.Activity next
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
    public org.openmdx.base.jmi1.Void voteForActivity(
        org.opencrx.kernel.activity1.jmi1.ActivityVoteForActivityParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getActivities().voteForActivity(
                this.current.refGetPath(),
                params.getName(),
                params.getDescription()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void assignTo(
        org.opencrx.kernel.activity1.jmi1.ActivityAssignToParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getActivities().assignTo(
                this.current.refGetPath(), 
                params.getResource() == null ? null : params.getResource().refGetPath()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }            
    }
        
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void reapplyActivityCreator(
        org.opencrx.kernel.activity1.jmi1.ReapplyActivityCreatorParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getActivities().reapplyActivityCreator(
                this.current.refGetPath(),
                params.getActivityCreator()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }                    
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpResult doFollowUp(
        org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            ActivityFollowUp followUp = this.getBackend().getActivities().doFollowUp(
                this.current.refGetPath(), 
                params.getFollowUpTitle(),
                params.getFollowUpText(),
                params.getTransition() == null ? null : params.getTransition().refGetPath(),
                params.getAssignTo() == null ? null : params.getAssignTo().refGetPath()
            );
            return ((Activity1Package)this.current.refOutermostPackage().refPackage(Activity1Package.class.getName())).createActivityDoFollowUpResult(
                followUp
            );            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }                            
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.AddWorkRecordResult addWorkRecordByDuration(
        org.opencrx.kernel.activity1.jmi1.ActivityAddWorkRecordByDurationParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            ActivityWorkRecord workRecord = this.getBackend().getActivities().activityAddWorkRecordByDuration(
                this.current.refGetPath(),
                params.getName(),
                params.getDescription(),
                params.getStartAt(),
                params.getEndAt(),
                params.getDurationHours(),
                params.getDurationMinutes(),
                params.getRateType(),
                params.getDepotSelector(),
                params.getResource() == null ? null : params.getResource().refGetPath()
            );
            return ((Activity1Package)this.current.refOutermostPackage().refPackage(Activity1Package.class.getName())).createAddWorkRecordResult(
                workRecord
            );            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }                                    
    }
    
    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void updateIcal(
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            this.getBackend().getActivities().updateIcal(
                this.current.refGetPath()
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
    protected final org.opencrx.kernel.activity1.jmi1.Activity current;
    protected final org.opencrx.kernel.activity1.cci2.Activity next;    
    
}

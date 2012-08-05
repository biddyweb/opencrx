package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
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
        org.openmdx.base.jmi1.Void params
    ) throws javax.jmi.reflect.RefException {
        try {
            int count = this.getBackend().getActivities().countFilteredActivity(
                this.current.refGetPath()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createCountFilteredObjectsResult(
                count
            );            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity current;
    protected final org.opencrx.kernel.activity1.cci2.AbstractFilterActivity next;    
    
}

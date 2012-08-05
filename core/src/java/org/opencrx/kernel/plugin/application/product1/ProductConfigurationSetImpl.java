package org.opencrx.kernel.plugin.application.product1;

import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class ProductConfigurationSetImpl {
    
    //-----------------------------------------------------------------------
    public ProductConfigurationSetImpl(
        org.opencrx.kernel.product1.jmi1.ProductConfigurationSet current,
        org.opencrx.kernel.product1.cci2.ProductConfigurationSet next
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
    public org.openmdx.base.jmi1.Void unsetConfigurationType(
    ) {
        try {        
            this.getBackend().getProducts().unsetConfigurationType(
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
    protected final org.opencrx.kernel.product1.jmi1.ProductConfigurationSet current;
    protected final org.opencrx.kernel.product1.cci2.ProductConfigurationSet next;    

}

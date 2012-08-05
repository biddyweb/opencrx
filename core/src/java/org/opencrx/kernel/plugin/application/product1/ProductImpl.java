package org.opencrx.kernel.plugin.application.product1;

import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class ProductImpl {
    
    //-----------------------------------------------------------------------
    public ProductImpl(
        org.opencrx.kernel.product1.jmi1.Product current,
        org.opencrx.kernel.product1.cci2.Product next
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
    public org.openmdx.base.jmi1.Void setConfigurationType(
        org.opencrx.kernel.product1.jmi1.SetConfigurationTypeParams params
    ) throws javax.jmi.reflect.RefException {
        try {        
            this.getBackend().getProducts().setConfigurationType(
                this.current.refGetPath(),
                params.getConfigurationType() == null ? null :  params.getConfigurationType().refGetPath()
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
    protected final org.opencrx.kernel.product1.jmi1.Product current;
    protected final org.opencrx.kernel.product1.cci2.Product next;    

}

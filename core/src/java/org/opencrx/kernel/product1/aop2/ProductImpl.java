package org.opencrx.kernel.product1.aop2;

import org.opencrx.kernel.backend.Products;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class ProductImpl
	<S extends org.opencrx.kernel.product1.jmi1.Product,N extends org.opencrx.kernel.product1.cci2.Product,C extends Void>
	extends AbstractObject<S,N,C> {
    
    //-----------------------------------------------------------------------
    public ProductImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void setConfigurationType(
        org.opencrx.kernel.product1.jmi1.SetConfigurationTypeParams params
    ) {
        try {        
            Products.getInstance().setConfigurationType(
                this.sameObject(),
                params.getConfigurationType()
            );
            return super.newVoid();
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
}

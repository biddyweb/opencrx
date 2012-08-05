package org.opencrx.kernel.product1.aop2;

import org.opencrx.kernel.backend.Products;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class ProductConfigurationImpl
	<S extends org.opencrx.kernel.product1.jmi1.ProductConfiguration,N extends org.opencrx.kernel.product1.cci2.ProductConfiguration,C extends Void>
	extends AbstractObject<S,N,C> {
    
    //-----------------------------------------------------------------------
    public ProductConfigurationImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void unsetConfigurationType(
    ) {
        try {        
            Products.getInstance().unsetConfigurationType(
                this.sameObject()
            );
            return super.newVoid();
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
}

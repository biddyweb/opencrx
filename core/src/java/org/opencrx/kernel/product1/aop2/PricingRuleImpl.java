package org.opencrx.kernel.product1.aop2;

import org.opencrx.kernel.backend.Products;
import org.opencrx.kernel.product1.jmi1.GetPriceLevelResult;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class PricingRuleImpl
	<S extends org.opencrx.kernel.product1.jmi1.PricingRule,N extends org.opencrx.kernel.product1.cci2.PricingRule,C extends Void>
	extends AbstractObject<S,N,C> {
    
    //-----------------------------------------------------------------------
    public PricingRuleImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.GetPriceLevelResult getPriceLevel(
        org.opencrx.kernel.product1.jmi1.GetPriceLevelParams params
    ) {
        try {        
            GetPriceLevelResult result = Products.getInstance().getPriceLevel(
                this.sameObject(),
                params.getContract(),
                params.getProduct(),
                params.getPriceUom(),
                params.getQuantity(),
                params.getPricingDate()
            );
            return result;
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
}

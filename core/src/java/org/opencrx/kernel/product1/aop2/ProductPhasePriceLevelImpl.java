package org.opencrx.kernel.product1.aop2;

import javax.jdo.listener.DeleteCallback;

import org.opencrx.kernel.backend.Products;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.exception.ServiceException;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

public class ProductPhasePriceLevelImpl
	<S extends org.opencrx.kernel.product1.jmi1.ProductPhasePriceLevel,N extends org.opencrx.kernel.product1.cci2.ProductPhasePriceLevel,C extends Void>
	extends AbstractPriceLevelImpl<S,N,C>
	implements DeleteCallback {
    
    //-----------------------------------------------------------------------
    public ProductPhasePriceLevelImpl(
        S same,
        N next
    ) {
        super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult cloneProductPhasePriceLevel(
        org.opencrx.kernel.product1.jmi1.CloneProductPhasePriceLevelParams params
    ) {
        try {        
            Integer numberProcessed = Products.getInstance().cloneProductPhasePriceLevel(
                this.sameObject(),
                params.getProcessingMode(),
                params.getNameReplacementRegex(),
                params.getNameReplacementValue(),
                params.getProductPhaseKey()
            );
            return Structures.create(
            	org.opencrx.kernel.product1.jmi1.ProcessPricesResult.class, 
            	Datatypes.member(org.opencrx.kernel.product1.jmi1.ProcessPricesResult.Member.numberProcessed, numberProcessed)
            );            
        } catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
    //-----------------------------------------------------------------------
    @Override
	public void jdoPreDelete(
	) {
		super.jdoPreDelete();
    }    
	            
}

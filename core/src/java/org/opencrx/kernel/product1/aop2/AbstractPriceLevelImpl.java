package org.opencrx.kernel.product1.aop2;

import javax.jdo.JDOUserException;
import javax.jdo.listener.DeleteCallback;

import org.opencrx.kernel.backend.Products;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

public class AbstractPriceLevelImpl
	<S extends org.opencrx.kernel.product1.jmi1.AbstractPriceLevel,N extends org.opencrx.kernel.product1.cci2.AbstractPriceLevel,C extends Void>
	extends AbstractObject<S,N,C>
	implements DeleteCallback {
    
    //-----------------------------------------------------------------------
    public AbstractPriceLevelImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult calculatePrices(
        org.opencrx.kernel.product1.jmi1.CalculatePricesParams params
    ) {
        try {        
            Integer numberProcessed = Products.getInstance().calculatePrices(
                this.sameObject(),
                params.getProcessingMode(),
                params.getIncludeProductsModifiedSince()
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
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult removePrices(
        org.opencrx.kernel.product1.jmi1.RemovePricesParams params
    ) {
        try {        
            Integer numberProcessed = Products.getInstance().removePrices(
                this.sameObject(),
                params.getProcessingMode()
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
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult removePriceLevels(
        org.opencrx.kernel.product1.jmi1.RemovePriceLevelsParams params
    ) {
        try {        
            Integer numberProcessed = Products.getInstance().removePriceLevels(
                this.sameObject(),
                params.getProcessingMode(),
                false
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
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult createInitialPrices(
        org.opencrx.kernel.product1.jmi1.CreateInitialPricesParams params
    ) {
        try {        
            Integer numberProcessed = Products.getInstance().createInitialPrices(
                this.sameObject(),
                params.getProcessingMode(),
                params.getPriceUom(),
                params.getIncludeProductsModifiedSince()
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
        try {
            Products.getInstance().removePriceLevel(
            	this.sameObject(),
            	true
            );
            super.jdoPreDelete();
        }
        catch(ServiceException e) {
            throw new JDOUserException(
            	"Unable to preDelete()",
            	e,
            	this.sameObject()
            );
        }		
    }
        
}

package org.opencrx.kernel.plugin.application.product1;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;

public class AbstractPriceLevelImpl {
    
    //-----------------------------------------------------------------------
    public AbstractPriceLevelImpl(
        org.opencrx.kernel.product1.jmi1.AbstractPriceLevel current,
        org.opencrx.kernel.product1.cci2.AbstractPriceLevel next
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
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult calculatePrices(
        org.opencrx.kernel.product1.jmi1.CalculatePricesParams params
    ) {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().calculatePrices(
                this.current.refGetPath(),
                params.getProcessingMode(),
                params.getIncludeProductsModifiedSince()
            );
            return ((Product1Package)this.current.refOutermostPackage().refPackage(Product1Package.class.getName())).createProcessPricesResult(
                numberProcessed                    
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult removePrices(
        org.opencrx.kernel.product1.jmi1.RemovePricesParams params
    ) {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().removePrices(
                this.current.refGetPath(),
                params.getProcessingMode()
            );
            return ((Product1Package)this.current.refOutermostPackage().refPackage(Product1Package.class.getName())).createProcessPricesResult(
                numberProcessed                    
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }             
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult removePriceLevels(
        org.opencrx.kernel.product1.jmi1.RemovePriceLevelsParams params
    ) {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().removePriceLevels(
                this.current.refGetPath(),
                params.getProcessingMode()
            );
            return ((Product1Package)this.current.refOutermostPackage().refPackage(Product1Package.class.getName())).createProcessPricesResult(
                numberProcessed                    
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                     
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult createInitialPrices(
        org.opencrx.kernel.product1.jmi1.CreateInitialPricesParams params
    ) {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().createInitialPrices(
                this.current.refGetPath(),
                params.getProcessingMode(),
                params.getPriceUom() == null ? null : params.getPriceUom().refGetPath(),
                params.getIncludeProductsModifiedSince()
            );
            return ((Product1Package)this.current.refOutermostPackage().refPackage(Product1Package.class.getName())).createProcessPricesResult(
                numberProcessed                    
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                     
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final org.opencrx.kernel.product1.jmi1.AbstractPriceLevel current;
    private final org.opencrx.kernel.product1.cci2.AbstractPriceLevel next;    

}

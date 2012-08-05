package org.opencrx.kernel.plugin.application.product1;

import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.exception.ServiceException;

public class ProductPhasePriceLevelImpl extends AbstractPriceLevelImpl {
    
    //-----------------------------------------------------------------------
    public ProductPhasePriceLevelImpl(
        org.opencrx.kernel.product1.jmi1.ProductPhasePriceLevel current,
        org.opencrx.kernel.product1.cci2.ProductPhasePriceLevel next
    ) {
        super(
             current,
             next
        );
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.product1.jmi1.ProcessPricesResult clonePriceLevel(
        org.opencrx.kernel.product1.jmi1.CloneProductPhasePriceLevelParams params
    ) {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().cloneProductPhasePriceLevel(
                this.current.refGetPath(),
                params.getProcessingMode(),
                params.getNameReplacementRegex(),
                params.getNameReplacementValue(),
                params.getProductPhaseKey()
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
    private final org.opencrx.kernel.product1.jmi1.ProductPhasePriceLevel current;
    private final org.opencrx.kernel.product1.cci2.ProductPhasePriceLevel next;    

}

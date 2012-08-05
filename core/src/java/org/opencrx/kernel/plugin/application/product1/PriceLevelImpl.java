package org.opencrx.kernel.plugin.application.product1;

import org.opencrx.kernel.product1.jmi1.Product1Package;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;

public class PriceLevelImpl extends AbstractPriceLevelImpl {
    
    //-----------------------------------------------------------------------
    public PriceLevelImpl(
        org.opencrx.kernel.product1.jmi1.PriceLevel current,
        org.opencrx.kernel.product1.cci2.PriceLevel next
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
        org.opencrx.kernel.product1.jmi1.ClonePriceLevelParams params
    ) throws javax.jmi.reflect.RefException  {
        try {        
            Integer numberProcessed = this.getBackend().getProducts().clonePriceLevel(
                this.current.refGetPath(),
                params.getProcessingMode(),
                params.getNameReplacementRegex(),
                params.getNameReplacementValue(),
                params.getValidFrom(),
                params.getValidTo()
            );
            return ((Product1Package)this.current.refOutermostPackage().refPackage(Product1Package.class.getName())).createProcessPricesResult(
                numberProcessed                    
            );            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }             
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final org.opencrx.kernel.product1.jmi1.PriceLevel current;
    private final org.opencrx.kernel.product1.cci2.PriceLevel next;    

}

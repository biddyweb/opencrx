/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: ProductManager.java,v 1.7 2009/11/27 18:23:05 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/11/27 18:23:05 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2007, CRIXP Corp., Switzerland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.apps.store.manager;

import java.util.List;

import javax.jdo.Transaction;

import org.opencrx.apps.store.common.ObjectCollection;
import org.opencrx.apps.store.common.PrimaryKey;
import org.opencrx.apps.store.common.util.ApplicationContext;
import org.opencrx.apps.store.objects.Product;
import org.opencrx.kernel.product1.cci2.ProductQuery;
import org.opencrx.kernel.product1.jmi1.ProductClassification;
import org.openmdx.base.exception.ServiceException;

/**
 * Product Manager manages product subsystem
 * 
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class ProductManager
{
    public ProductManager(
        ApplicationContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final boolean create(
        final Product newValue
    ) {
    	Transaction tx = null;
        try {
            tx = this.context.getPersistenceManager().currentTransaction();       
            tx.begin();
            org.opencrx.kernel.product1.jmi1.Product product = this.context.getPersistenceManager().newInstance(org.opencrx.kernel.product1.jmi1.Product.class);
            product.refInitialize(false, false);
            newValue.update(
                product, 
                this.context
            );
            this.context.getProductSegment().addProduct(
                false,
                newValue.getKey().getUuid(),
                product
            );
            tx.commit();
            return true;
        }
        catch(Exception e) {
        	if(tx != null) {
        		try {
        			tx.rollback();
        		}
        		catch(Exception e0) {}
        	}
            new ServiceException(e).log();
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final void delete(
        final PrimaryKey key
    ) {
    	// Products must be deleted with standard GUI
//    	Transaction tx = null;
//    	try {
//	        tx = this.context.getPersistenceManager().currentTransaction();       
//	        tx.begin();
//	        org.opencrx.kernel.product1.jmi1.Product product = 
//	            this.context.getProductSegment().getProduct(key.getUuid());
//	        product.refDelete();
//	        tx.commit();
//    	}
//    	catch(Exception e) {
//        	if(tx != null) {
//        		try {
//        			tx.rollback();
//        		}
//        		catch(Exception e0) {}
//        	}
//            new ServiceException(e).log();    		
//    	}
    }

    //-----------------------------------------------------------------------
    public final Product get(
        final PrimaryKey key
    ) {
        if(key.toString().length() > 0) {
            org.opencrx.kernel.product1.jmi1.Product product = 
                this.context.getProductSegment().getProduct(key.getUuid());
            return new Product(
                product, 
                this.context
            );
        }
        else {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    public final ObjectCollection getInCategory(
        final PrimaryKey categoryID
    ) {
        ObjectCollection productsInCategory = new ObjectCollection();
        if(categoryID.toString().length() > 0) {
            ProductClassification classification = this.context.getProductSegment().getProductClassification(categoryID.getUuid());
            if(classification != null) {
                ProductQuery query =  (ProductQuery)this.context.getPersistenceManager().newQuery(org.opencrx.kernel.product1.jmi1.Product.class);
                query.thereExistsClassification().equalTo(classification);
                List<org.opencrx.kernel.product1.jmi1.Product> products = this.context.getProductSegment().getProduct(query);
                for(org.opencrx.kernel.product1.jmi1.Product product: products) {
                    Product prod = new Product(product, this.context);
                    productsInCategory.put(
                        prod.getKey().toString(),
                        prod
                    );
                }
            }
        }
        return productsInCategory;
    }

    //-----------------------------------------------------------------------
    public final Product update(
        final Product newValue
    ) {
    	Transaction tx = null;
    	try {
	        tx = this.context.getPersistenceManager().currentTransaction();       
	        tx.begin();
	        org.opencrx.kernel.product1.jmi1.Product product = this.context.getProductSegment().getProduct(newValue.getKey().getUuid());
	        newValue.update(
	            product,
	            this.context
	        );
	        tx.commit();
	        return this.get(newValue.getKey());
    	}
    	catch(Exception e) {
        	if(tx != null) {
        		try {
        			tx.rollback();
        		}
        		catch(Exception e0) {}
        	}
            new ServiceException(e).log();
            return null;
    	}
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final ApplicationContext context;
    
}

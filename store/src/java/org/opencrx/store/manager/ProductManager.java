/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: ProductManager.java,v 1.16 2008/11/08 00:21:54 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.16 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/08 00:21:54 $
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
package org.opencrx.store.manager;

import java.util.Iterator;

import javax.jdo.Transaction;

import org.opencrx.store.common.ObjectCollection;
import org.opencrx.store.common.PrimaryKey;
import org.opencrx.store.common.util.OpenCrxContext;
import org.opencrx.store.objects.Product;
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
        OpenCrxContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final boolean create(
        final Product newValue
    ) {
        try {
            Transaction tx = this.context.getPersistenceManager().currentTransaction();       
            tx.begin();
            org.opencrx.kernel.product1.jmi1.Product product = 
                this.context.getProductPackage().getProduct().createProduct();
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
            new ServiceException(e).log();
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final void delete(
        final PrimaryKey key
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.product1.jmi1.Product product = 
            this.context.getProductSegment().getProduct(key.getUuid());
        product.refDelete();
        tx.commit();
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
        ObjectCollection products = new ObjectCollection();
        if(categoryID.toString().length() > 0) {
            org.opencrx.kernel.product1.jmi1.ProductClassification classification = 
                this.context.getProductSegment().getProductClassification(categoryID.getUuid());
            if(classification != null) {
                org.opencrx.kernel.product1.cci2.ProductQuery query = 
                    this.context.getProductPackage().createProductQuery();
                query.thereExistsClassification().equalTo(classification);
                for(
                    Iterator i = this.context.getProductSegment().getProduct(query).iterator(); 
                    i.hasNext(); 
                ) {
                    org.opencrx.kernel.product1.jmi1.Product product = 
                        (org.opencrx.kernel.product1.jmi1.Product)i.next();
                    Product prod = new Product(product, this.context);
                    products.put(
                        prod.getKey().toString(),
                        prod
                    );
                }
            }
        }
        return products;
    }

    //-----------------------------------------------------------------------
    public final Product update(
        final Product newValue
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.product1.jmi1.Product product = 
            this.context.getProductSegment().getProduct(newValue.getKey().getUuid());
        newValue.update(
            product,
            this.context
        );
        tx.commit();
        return this.get(newValue.getKey());
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final OpenCrxContext context;
    
}

/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: CategoryManager.java,v 1.15 2008/02/12 19:57:20 wfro Exp $
 * Description: CategoryManager
 * Revision:    $Revision: 1.15 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/12 19:57:20 $
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

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Transaction;

import org.opencrx.kernel.product1.cci2.ProductClassificationQuery;
import org.opencrx.kernel.product1.cci2.ProductClassificationRelationshipQuery;
import org.opencrx.kernel.product1.jmi1.ProductClassification;
import org.opencrx.kernel.product1.jmi1.ProductClassificationRelationship;
import org.opencrx.store.common.ObjectCollection;
import org.opencrx.store.common.PrimaryKey;
import org.opencrx.store.common.util.OpenCrxContext;
import org.opencrx.store.objects.Category;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openxri.XRI;

/**
 * Manager for Category subsystem
 * 
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class CategoryManager
{
    public CategoryManager(
        OpenCrxContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final boolean create(
        final Category newValue
    ) {
        try {
            UUIDGenerator uuids = UUIDs.getGenerator();
            Transaction tx = this.context.getPersistenceManager().currentTransaction();       
            tx.begin();
            org.opencrx.kernel.product1.jmi1.ProductClassification classification = 
                this.context.getProductPackage().getProductClassification().createProductClassification();
            classification.refInitialize(false, true);
            newValue.update(
                classification, 
                this.context
            );
            classification.setName(
                OpenCrxContext.STORE_SCHEMA + newValue.getTitle()
            );
            this.context.getProductSegment().addProductClassification(
                false,
                newValue.getKey().getUuid(),
                classification
            );
            tx.commit();
            // Get parent classification
            ProductClassification parent = null;            
            if(
                (newValue.getParentID() != null) &&
                (newValue.getParentID().getUuid().length() > 0)
            ) {
                parent = 
                    context.getProductSegment().getProductClassification(newValue.getParentID().getUuid());
            }
            // OpenCrxContext.SCHEMA_STORE + CATEGORY_NAME_PRODUCTS as default root classification 
            else {
                ProductClassificationQuery query = 
                    this.context.getProductPackage().createProductClassificationQuery();                
                query.name().equalTo(OpenCrxContext.STORE_SCHEMA + Category.CATEGORY_NAME_PRODUCTS);
                Collection classifications = this.context.getProductSegment().getProductClassification(query);
                if(!classifications.isEmpty()) {
                    parent = (ProductClassification)classifications.iterator().next();
                }
                // Create root classification on demand
                else {
                    tx.begin();
                    parent = 
                        this.context.getProductPackage().getProductClassification().createProductClassification();
                    parent.refInitialize(false, true);
                    parent.setName(OpenCrxContext.STORE_SCHEMA + Category.CATEGORY_NAME_PRODUCTS);
                    parent.setDescription(Category.CATEGORY_NAME_PRODUCTS);
                    this.context.getProductSegment().addProductClassification(
                        false,
                        uuids.next().toString(),
                        parent
                    );
                    tx.commit();
                }
            }
            // Add relationship to parent
            tx.begin();
            ProductClassificationRelationship relationship = 
                context.getProductPackage().getProductClassificationRelationship().createProductClassificationRelationship();
            relationship.refInitialize(false, true);
            relationship.setName(parent.getName());
            relationship.setRelationshipType((short)0);
            relationship.setRelationshipTo(parent);
            classification.addRelationship(
                false,
                uuids.next().toString(),
                relationship
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
        org.opencrx.kernel.product1.jmi1.ProductClassification classification = 
            this.context.getProductSegment().getProductClassification(key.getUuid());
        classification.refDelete();
        tx.commit();
    }

    //-----------------------------------------------------------------------
    public final Category get(
        final PrimaryKey key
    ) {
        if(key.toString().length() > 0) {
            org.opencrx.kernel.product1.jmi1.ProductClassification classification = 
                this.context.getProductSegment().getProductClassification(key.getUuid());
            return new Category(classification);
        }
        else {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    public final ObjectCollection getChildren(
        final PrimaryKey categoryID
    ) {
        ObjectCollection children = new ObjectCollection();
        org.opencrx.kernel.product1.jmi1.ProductClassification parent = null;
        if(categoryID.toString().length() == 0) {
            ProductClassificationQuery query = 
                this.context.getProductPackage().createProductClassificationQuery();
            query.name().equalTo(OpenCrxContext.STORE_SCHEMA + Category.CATEGORY_NAME_PRODUCTS);
            Collection classifications = this.context.getProductSegment().getProductClassification(query);
            if(!classifications.isEmpty()) {
                parent = (ProductClassification)classifications.iterator().next();
            }
        }
        else {
            parent = this.context.getProductSegment().getProductClassification(categoryID.getUuid());
        }
        if(parent != null) {
            ProductClassificationRelationshipQuery query = 
                this.context.getProductPackage().createProductClassificationRelationshipQuery();
            query.thereExistsRelationshipTo().equalTo(parent);
            query.identity().like(
                this.context.getProductSegment().refMofId().replaceAll("\\.", "\\\\.") + "/productClassification/:*/relationship/:*"
            );
            for(
                Iterator i = this.context.getProductSegment().getExtent(query).iterator();
                i.hasNext();
            ) {
                ProductClassificationRelationship relationship = (ProductClassificationRelationship)i.next();
                ProductClassification classification = relationship.getClassification();
                Category category = new Category(classification);
                children.put(
                    category.getKey().toString(), 
                    category
                );
            }
        }
        return children;
    }

    //-----------------------------------------------------------------------
    public final Category update(
        final Category newValue
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.product1.jmi1.ProductClassification classification = 
            this.context.getProductSegment().getProductClassification(newValue.getKey().getUuid());
        newValue.update(
            classification,
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

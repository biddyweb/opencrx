/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: OrderItemManager.java,v 1.2 2009/02/15 18:06:14 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/15 18:06:14 $
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Transaction;

import org.opencrx.apps.store.common.ObjectCollection;
import org.opencrx.apps.store.common.PrimaryKey;
import org.opencrx.apps.store.objects.OrderItem;
import org.opencrx.apps.utils.ApplicationContext;
import org.opencrx.kernel.contract1.cci2.SalesOrderPositionQuery;
import org.opencrx.kernel.contract1.jmi1.ContractPosition;
import org.opencrx.kernel.contract1.jmi1.SalesOrderPosition;
import org.openmdx.base.exception.ServiceException;

/**
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class OrderItemManager
{
    //-----------------------------------------------------------------------
    public OrderItemManager(
        ApplicationContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final OrderItem get(
        final PrimaryKey OrderItemID
    ) {
        if(OrderItemID.toString().length() > 0) {
            SalesOrderPositionQuery query = this.context.getContractPackage().createSalesOrderPositionQuery();
            query.positionNumber().equalTo(OrderItemID.getUuid());
            query.identity().like(
                this.context.getContractSegment().refMofId().replaceAll("\\.", "\\\\.") + "/salesOrder/:*/position/:*"
            );            
            Collection positions = this.context.getContractSegment().getExtent(query);
            if(!positions.isEmpty()) {
                return new OrderItem(
                     (org.opencrx.kernel.contract1.jmi1.SalesOrderPosition)positions.iterator().next()
                );
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    public final void update(
        final OrderItem newValue
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        SalesOrderPositionQuery query = this.context.getContractPackage().createSalesOrderPositionQuery();
        query.positionNumber().equalTo(newValue.getKey().getUuid());
        query.identity().like(
            this.context.getContractSegment().refMofId().replaceAll("\\.", "\\\\.") + "/salesOrder/:*/position/:*"
        );            
        Collection positions = this.context.getContractSegment().getExtent(query);
        if(!positions.isEmpty()) {
            ContractPosition position = (ContractPosition)positions.iterator().next();
            if(position instanceof org.opencrx.kernel.contract1.jmi1.SalesOrderPosition) {
                tx.begin();
                newValue.update(
                    (org.opencrx.kernel.contract1.jmi1.SalesOrderPosition)position,
                    this.context
                );
                tx.commit();
            }
        }
    }

    //-----------------------------------------------------------------------
    public final OrderItem getOrderedProduct(
        final PrimaryKey orderID, 
        final PrimaryKey productID
    ) {
        org.opencrx.kernel.product1.jmi1.Product product = 
            this.context.getProductSegment().getProduct(productID.getUuid());
        org.opencrx.kernel.contract1.jmi1.SalesOrder salesOrder = 
            this.context.getContractSegment().getSalesOrder(orderID.getUuid());
        for(
            Iterator i = salesOrder.getPosition().iterator();
            i.hasNext();
        ) {
            ContractPosition position = (ContractPosition)i.next();
            if(position instanceof SalesOrderPosition) {
                if(((SalesOrderPosition)position).getProduct().equals(product)) {
                    return new OrderItem(
                        (org.opencrx.kernel.contract1.jmi1.SalesOrderPosition)position
                    );                    
                }
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    private org.opencrx.kernel.product1.jmi1.SalesTaxType getSalesTaxType(
    ) {
        org.opencrx.kernel.product1.cci2.SalesTaxTypeQuery query = this.context.getProductPackage().createSalesTaxTypeQuery();
        query.name().equalTo(this.context.getConfiguredSalesTaxTypeName());
        Collection salesTaxTypes = this.context.getProductSegment().getSalesTaxType(query);
        return salesTaxTypes.isEmpty()
            ? null
            : (org.opencrx.kernel.product1.jmi1.SalesTaxType)salesTaxTypes.iterator().next();        
    }
    
    //-----------------------------------------------------------------------
    public final OrderItem newOrder(
        final PrimaryKey orderID, 
        final PrimaryKey productID, 
        final float unitPrice
    ) {
        final OrderItem existingItem = this.getOrderedProduct(
            orderID, 
            productID
        );
        if(null != existingItem) {
            existingItem.setQuantity( existingItem.getQuantity() + 1 );
            this.update( existingItem );
            return existingItem;
        }
        else {
            try {
                org.opencrx.kernel.contract1.jmi1.SalesOrder salesOrder = 
                    this.context.getContractSegment().getSalesOrder(orderID.getUuid());
                org.opencrx.kernel.product1.jmi1.Product product = 
                this.context.getProductSegment().getProduct(productID.getUuid());            
                Transaction tx = this.context.getPersistenceManager().currentTransaction();       
                tx.begin();
                org.opencrx.kernel.contract1.jmi1.CreatePositionParams params = this.context.getContractPackage().createCreatePositionParams(
                    null,
                    product.getName(), 
                    null, 
                    null, // pricing date 
                    null, // pricing rule 
                    product, 
                    new BigDecimal(1),  // quantity 
                    null // uom
                ); 
                org.opencrx.kernel.contract1.jmi1.CreatePositionResult result = salesOrder.createPosition(
                    params
                );
                tx.commit();
                org.opencrx.kernel.contract1.jmi1.ContractPosition position = 
                    (org.opencrx.kernel.contract1.jmi1.ContractPosition)this.context.getPersistenceManager().getObjectById(
                        result.getPosition().refMofId()
                    ); 
                org.opencrx.kernel.product1.jmi1.SalesTaxType salesTaxType = this.getSalesTaxType();                
                if(salesTaxType != null) {
                    tx.begin();
                    position.setSalesTaxType(salesTaxType);                    
                    tx.commit();
                }
                return this.get(
                    new PrimaryKey(position.getPositionNumber(), false)
                );
            }
            catch(Exception e) {
                new ServiceException(e).log();
                return null;
            }            
        }
    }

    //-----------------------------------------------------------------------
    public final ObjectCollection getItemsInOrder(
        final PrimaryKey orderID
    ) {
        org.opencrx.kernel.contract1.jmi1.SalesOrder salesOrder = 
            this.context.getContractSegment().getSalesOrder(orderID.getUuid());
        ObjectCollection orderItems = new ObjectCollection();
        for(
            Iterator i = salesOrder.getPosition().iterator();
            i.hasNext();
        ) {
            org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition position =
                (org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition)i.next();
            if(position instanceof org.opencrx.kernel.contract1.jmi1.SalesOrderPosition) {
                OrderItem orderItem = new OrderItem(
                    (org.opencrx.kernel.contract1.jmi1.SalesOrderPosition)position
                );
                orderItems.put(
                    orderItem.getKey().toString(), 
                    orderItem
                );
            }
        }
        return orderItems;
    }

    //-----------------------------------------------------------------------
    public final void clearItems(
        final PrimaryKey orderID
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.contract1.jmi1.SalesOrder salesOrder = 
            this.context.getContractSegment().getSalesOrder(orderID.getUuid());
        for(
            Iterator i = salesOrder.getPosition().iterator();
            i.hasNext();
        ) {
            org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition position =
                (org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition)i.next();
            position.refDelete();
        }
        tx.commit();
    }

    //-----------------------------------------------------------------------
    public final void delete(
        final PrimaryKey key
    ) {
        SalesOrderPositionQuery query = this.context.getContractPackage().createSalesOrderPositionQuery();
        query.positionNumber().equalTo(key.getUuid());
        query.identity().like(
            this.context.getContractSegment().refMofId().replaceAll("\\.", "\\\\.") + "/salesOrder/:*/position/:*"
        );                    
        Collection positions = this.context.getContractSegment().getExtent(query);
        if(!positions.isEmpty()) {
            org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition position =
                (org.opencrx.kernel.contract1.jmi1.AbstractSalesOrderPosition)positions.iterator().next();
            Transaction tx = this.context.getPersistenceManager().currentTransaction();
            tx.begin();
            position.refDelete();
            tx.commit();            
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final ApplicationContext context;
    
}

/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: Product.java,v 1.19 2008/11/08 00:21:54 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.19 $
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
package org.opencrx.store.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;

import org.opencrx.kernel.product1.cci2.PriceLevelQuery;
import org.opencrx.kernel.product1.jmi1.ProductBasePrice;
import org.opencrx.store.common.IStandardObject;
import org.opencrx.store.common.PrimaryKey;
import org.opencrx.store.common.util.OpenCrxContext;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

/**
 * Product object
 * 
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class Product implements IStandardObject
{
    //-----------------------------------------------------------------------
    public Product(final PrimaryKey key)
    {
        Key = key;
    }

    //-----------------------------------------------------------------------
    public Product()
    {
    }
    
    //-----------------------------------------------------------------------
    public Product(
        org.opencrx.kernel.product1.jmi1.Product product,
        OpenCrxContext context
    ) {
        this.Key = new PrimaryKey(product.refGetPath().getBase(), false);
        this.CategoryID = null;
        if(!product.getClassification().isEmpty()) {
            org.opencrx.kernel.product1.jmi1.ProductClassification productClassification = 
                (org.opencrx.kernel.product1.jmi1.ProductClassification)product.getClassification().get(0);
            this.CategoryID = new PrimaryKey(productClassification.refGetPath().getBase(), false);
        }
        this.Title = product.getName();
        this.Details = product.getDetailedDescription();        
        this.PictureFile = product.getDescription();
        // UnitPrice
        org.opencrx.kernel.product1.jmi1.PriceLevel priceLevel = this.findPriceLevel(context);
        if(priceLevel != null) {
            org.opencrx.kernel.product1.jmi1.ProductBasePrice basePrice = this.findBasePrice(
                product, 
                priceLevel, 
                context
            );
            if(basePrice != null) {
                this.UnitPrice = basePrice.getPrice().floatValue();
            }
        }
    }

    //-----------------------------------------------------------------------
    private org.opencrx.kernel.product1.jmi1.PriceLevel findPriceLevel(
        OpenCrxContext context
    ) {
        PriceLevelQuery priceLevelQuery = context.getProductPackage().createPriceLevelQuery();
        priceLevelQuery.name().equalTo(OpenCrxContext.STORE_SCHEMA + Product.PRICE_LEVEL_NAME + " [" + DecimalFormat.getCurrencyInstance(context.getStoreLocale()).getCurrency().getSymbol() + "]");
        Collection priceLevels = context.getProductSegment().getPriceLevel(priceLevelQuery);
        return priceLevels.isEmpty()
            ? null
            : (org.opencrx.kernel.product1.jmi1.PriceLevel)priceLevels.iterator().next();
    }
    
    //-----------------------------------------------------------------------
    private org.opencrx.kernel.product1.jmi1.ProductBasePrice findBasePrice(
        org.opencrx.kernel.product1.jmi1.Product product,
        org.opencrx.kernel.product1.jmi1.PriceLevel priceLevel,
        OpenCrxContext context
    ) {            
        for(Iterator i = product.getBasePrice().iterator(); i.hasNext(); ) {
            org.opencrx.kernel.product1.jmi1.ProductBasePrice price = (ProductBasePrice)i.next();
            if(price.getPriceCurrency() == context.getStoreCurrencyCode()) {
                if(price.getPriceLevel().contains(priceLevel)) {
                    return price;
                }
            }
        }
        return null;
    }
    
    //-----------------------------------------------------------------------
    public void update(
        org.opencrx.kernel.product1.jmi1.Product product,
        OpenCrxContext context
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        
        // classification
        product.getClassification().clear();
        product.getClassification().add(
            context.getProductSegment().getProductClassification(this.getCategoryID().getUuid())
        );
        // name
        product.setName(
            this.getTitle()
        );
        // description
        product.setDetailedDescription(
            this.getDetails()
        );
        // picture file
        product.setDescription(
            this.getPictureFile()
        );
        // Uom
        product.setDefaultUom(
            (org.opencrx.kernel.uom1.jmi1.Uom)context.getPersistenceManager().getObjectById("xri:@openmdx:org.opencrx.kernel.uom1/provider/" + context.getProviderName() + "/segment/Root/uom/Unit")
        );
        
        // Update / Create price
        org.opencrx.kernel.product1.jmi1.PriceLevel priceLevel = this.findPriceLevel(context);
        if(priceLevel == null) {
            String name =
                OpenCrxContext.STORE_SCHEMA + Product.PRICE_LEVEL_NAME + " [" + DecimalFormat.getCurrencyInstance(context.getStoreLocale()).getCurrency().getSymbol() + "]";
            priceLevel = context.getProductPackage().getPriceLevel().createPriceLevel();
            priceLevel.refInitialize(false, false);
            priceLevel.setName(name);
            priceLevel.setDescription(name);
            priceLevel.setPriceCurrency(context.getStoreCurrencyCode());
            priceLevel.getPriceUsage().add(new Short(OpenCrxContext.PRICE_USAGE_CONSUMER));
            context.getProductSegment().addPriceLevel(
                false,
                uuids.next().toString(),
                priceLevel
            );
        }
        org.opencrx.kernel.product1.jmi1.ProductBasePrice basePrice = this.findBasePrice(
            product, 
            priceLevel, 
            context
        );
        if(basePrice == null) {
            basePrice =
                context.getProductPackage().getProductBasePrice().createProductBasePrice();
            basePrice.refInitialize(false, false);
            basePrice.setPriceCurrency(context.getStoreCurrencyCode());
            basePrice.getUsage().add(new Short(OpenCrxContext.PRICE_USAGE_CONSUMER));
            basePrice.getPriceLevel().add(priceLevel);
            basePrice.setUom(
                (org.opencrx.kernel.uom1.jmi1.Uom)context.getPersistenceManager().getObjectById("xri:@openmdx:org.opencrx.kernel.uom1/provider/" + context.getProviderName() + "/segment/Root/uom/Unit")
            );
            product.addBasePrice(
                false,
                uuids.next().toString(),
                basePrice
            );
        }
        basePrice.setPrice(new BigDecimal(this.getUnitPrice()));
    }
        
    //-----------------------------------------------------------------------
    public final PrimaryKey getKey()
    {
        return Key;
    }

    //-----------------------------------------------------------------------
    public final void setKey(final PrimaryKey key)
    {
        this.Key = key;
    }

    //-----------------------------------------------------------------------
    public final PrimaryKey getCategoryID()
    {
        return CategoryID;
    }

    //-----------------------------------------------------------------------
    public final void setCategoryID(final PrimaryKey categoryID)
    {
        CategoryID = categoryID;
    }

    //-----------------------------------------------------------------------
    public final String getTitle()
    {
        return Title;
    }

    //-----------------------------------------------------------------------
    public final void setTitle(final String title)
    {
        Title = title;
    }

    //-----------------------------------------------------------------------
    public final float getUnitPrice()
    {
        return UnitPrice;
    }

    //-----------------------------------------------------------------------
    public final void setUnitPrice(final float unitPrice)
    {
        UnitPrice = unitPrice;
    }

    //-----------------------------------------------------------------------
    public final boolean isAvailable()
    {
        return IsAvailable;
    }

    //-----------------------------------------------------------------------
    public final void setAvailable(final boolean isAvailable)
    {
        IsAvailable = isAvailable;
    }

    //-----------------------------------------------------------------------
    public final String getDetails()
    {
        return Details;
    }

    //-----------------------------------------------------------------------
    public final void setDetails(final String details)
    {
        Details = details;
    }

    //-----------------------------------------------------------------------
    public final String getPictureFile()
    {
        return PictureFile;
    }

    //-----------------------------------------------------------------------
    public final void setPictureFile(final String pictureFile)
    {
        PictureFile = pictureFile;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns true if all properties are valid.
     */
    public final boolean isValid()
    {
        // Blank checks
        if (this.getTitle().equals(""))
            return false;
//        if( this.getAddress().equals( "" ) )
//            return false;

        // All OK
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the name of the objects
     */
    public final int compareTo(final Object o)
    {
        return compareTo((Product) o);
    }

    //-----------------------------------------------------------------------
    public final int compareTo(final Product o)
    {
        if (null == this.Title) return 1;
        return (this.Title.compareTo(o.getTitle()));
    }

    //-----------------------------------------------------------------------
    /**
     * If both objects has the same name then they are equal
     */
    public final boolean equals(final Object obj)
    {
        if (obj instanceof Product)
        {
            final Product o = (Product) obj;

            if (o.getTitle().equals(o.getTitle()))
                return true;
            else
                return false;
        } else
            return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Save the state of this object to a stream (i.e., serialize it).
     *
     * @serialData Stores all the properties one by one to the stream
     */
    private void writeObject(final ObjectOutputStream s)
            throws IOException
    {
        s.writeObject(this.getKey());
        s.writeUTF(this.getTitle());
        s.writeObject(this.getCategoryID());
        s.writeUTF(this.getDetails());
        s.writeUTF(this.getPictureFile());
        s.writeFloat(this.getUnitPrice());

    }

    //-----------------------------------------------------------------------
    /**
     * Reconstitute this object from a stream (i.e., deserialize it).
     */
    private void readObject(final ObjectInputStream s)
            throws IOException, ClassNotFoundException
    {
        this.Key = (PrimaryKey) s.readObject();
        this.Title = s.readUTF();
        this.CategoryID = (PrimaryKey) s.readObject();
        this.Details = s.readUTF();
        this.PictureFile = s.readUTF();
        this.UnitPrice = s.readFloat();
    }
    
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -611517302607057737L;
    public static final String PRICE_LEVEL_NAME = "Prices";
    public static final String PROP_CATEGORY_ID = "CategoryID";
    public static final String PROP_TITLE = "Title";
    public static final String PROP_UNIT_PRICE = "UnitPrice";
    public static final String PROP_IS_AVAILABLE = "IsAvailable";
    public static final String PROP_DETAILS = "Details";
    public static final String PROP_PICTURE_FILE = "PictureFile";

    private PrimaryKey Key = new PrimaryKey();
    private PrimaryKey CategoryID = new PrimaryKey();
    private String Title = "";
    private float UnitPrice = 0f;
    private boolean IsAvailable = true;
    private String Details = "";
    private String PictureFile = "";
    
}

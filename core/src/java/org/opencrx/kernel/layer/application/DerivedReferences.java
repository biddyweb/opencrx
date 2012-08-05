/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: DerivedReferences.java,v 1.57 2009/06/13 18:47:42 wfro Exp $
 * Description: DerivedReferences
 * Revision:    $Revision: 1.57 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/13 18:47:42 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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

package org.opencrx.kernel.layer.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Addresses;
import org.opencrx.kernel.backend.Contracts;
import org.opencrx.kernel.backend.Products;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.Directions;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.rest.spi.ObjectHolder_2Facade;
import org.openmdx.base.text.format.DateFormat;
import org.w3c.spi2.Datatypes;

public class DerivedReferences {

    //-----------------------------------------------------------------------
    public DerivedReferences(
        RequestHelper backend
    ) {
        this.requestHelper = backend;
    }
        
    //-------------------------------------------------------------------------
    private DataproviderRequest remapFindRequest(
        DataproviderRequest request,        
        Path reference,
        FilterProperty[] additionalFilter        
    ) throws ServiceException {
        for(int i = 0; i < additionalFilter.length; i++) {            
            request.addAttributeFilterProperty(
                additionalFilter[i]
            );
        }
        try {
	        return new DataproviderRequest(
	            request,            
	            ObjectHolder_2Facade.newInstance(reference).getDelegate(),
	            request.operation(),
	            request.attributeFilter(),
	            request.position(),
	            request.size(),
	            request.direction(),
	            request.attributeSelector(),
	            request.attributeSpecifier()
	        );
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
    }
    
    //-------------------------------------------------------------------------
    public DataproviderReply getReply(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        
        DataproviderReply reply = null;
            
        // GlobalFilterIncludesActivity
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_ACTIVITY)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("activity"),
                    DerivedReferences.getActivityFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
        }
        // GlobalFilterIncludesAccount
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_ACCOUNT)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("account"),
                    DerivedReferences.getAccountFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
        }
        // GlobalFilterIncludesAddress
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_ADDRESS)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("address"),
                    DerivedReferences.getAddressFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
        }
        // ActivityGroupFilterIncludesActivity
        else if(
            request.path().isLike(ACTIVITY_GROUP_FILTER_INCLUDES_ACTIVITY)
        ) {
            List<FilterProperty> filterProperties = new ArrayList<FilterProperty>();
            filterProperties.addAll(
                Arrays.asList(
                    DerivedReferences.getActivityFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
            // Query for activity group name
            MappedRecord activityGroup = this.requestHelper.retrieveObject(request.path().getPrefix(7));
            ObjectHolder_2Facade activityGroupFacade;
            try {
	            activityGroupFacade = ObjectHolder_2Facade.newInstance(activityGroup);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            String queryContext = SystemAttributes.CONTEXT_PREFIX + this.requestHelper.getUidAsString() + ":";
            filterProperties.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                    
                )
            );
            filterProperties.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{"EXISTS (SELECT 0 FROM OOCKE1_ACTIVITYGROUPASS ga INNER JOIN OOCKE1_ACTIVITYGROUP g ON ga.activity_group = g.object_id WHERE (v.object_id = ga.p$$parent) AND (g.name = ?s0))"}
                    
                )
            );
            filterProperties.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{activityGroupFacade.attributeValues("name").isEmpty() ? "NA" : activityGroupFacade.attributeValue("name")}                    
                )
            );
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("activity"),
                    filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // GlobalFilterIncludesContract
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_CONTRACT)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path(),
                    DerivedReferences.getContractFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
        } 
        // GlobalFilterIncludesProduct
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_PRODUCT)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("product"),
                    DerivedReferences.getProductFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );
        }
        // PriceLevelHasFilteredAccount
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_FILTERED_ACCOUNT)
        ) {
            List<FilterProperty> filterProperties = new ArrayList<FilterProperty>();
            filterProperties.addAll(
                Arrays.asList(
                    DerivedReferences.getAccountFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );            
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.account1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "account"}
                    ),
                    filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // PriceLevelHasFilteredProduct
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_FILTERED_PRODUCT)
        ) {
            List<FilterProperty> filterProperties = new ArrayList<FilterProperty>();
            filterProperties.addAll(
                Arrays.asList(
                    DerivedReferences.getProductFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false,
                        this.requestHelper.getDelegatingRequests()
                    )
                )
            );      
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.product1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "product"}
                    ),
                    filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // PriceLevelHasAssignedPriceListEntry
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_ASSIGNED_PRICE_LIST_ENTRY)
        ) {
            List<FilterProperty> filterProperties = new ArrayList<FilterProperty>();
            filterProperties.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceLevel",
                    FilterOperators.IS_IN,
                    new Object[]{request.path().getPrefix(7)}
                    
                )
            );
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.product1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "priceListEntry"}
                    ),
                    filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // CompoundBookingHasBooking
        else if(request.path().isLike(COMPOUND_BOOKING_HAS_BOOKINGS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("booking"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "cb",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(7)}
                        )                        
                    }
                )
            );
        }
        // DepotPositionHasBooking
        else if(request.path().isLike(DEPOT_POSITION_HAS_BOOKINGS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("booking"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "position",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(13)}
                        )                        
                    }
                )
            );
        }
        // DepotPositionHasSimpleBooking
        else if(request.path().isLike(DEPOT_POSITION_HAS_SIMPLE_BOOKINGS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("simpleBooking"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "position",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(13)}
                        )                        
                    }
                )
            );
        }
        // ClassifierClassifiesTypedElement
        else if(request.path().isLike(CLASSIFIER_CLASSIFIES_TYPED_ELEMENT)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("element"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "type",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(7)}
                        )                        
                    }
                )
            );
        }
        // DepotReportItemHasBookingItem
        else if(request.path().isLike(DEPOT_REPORT_ITEM_HAS_BOOKING_ITEMS)) {
        	MappedRecord itemPosition = this.requestHelper.retrieveObject(request.path().getParent());
            try {
	            reply = this.requestHelper.getDelegatingLayer().find(
	                header,
	                this.remapFindRequest(
	                    request,
	                    request.path().getPrefix(request.path().size()-3).getChild("itemBooking"),
	                    new FilterProperty[]{
	                        new FilterProperty(
	                            Quantors.THERE_EXISTS,
	                            "position",
	                            FilterOperators.IS_IN,
	                            new Object[]{ObjectHolder_2Facade.newInstance(itemPosition).attributeValue("position")}
	                        )                        
	                    }
	                )
	            );
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
        }
        // DepotContainsDepot
        else if(request.path().isLike(DEPOT_GROUP_CONTAINS_DEPOTS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("extent"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "depotGroup",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getParent()}
                        ),                        
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            SystemAttributes.OBJECT_IDENTITY,
                            FilterOperators.IS_LIKE,
                            new Object[]{request.path().getPrefix(7).getDescendant(new String[]{"depotHolder", ":*", "depot", ":*"})}
                        )                        
                    }
                )
            );
        }
        // DepotContainsDepotGroup
        else if(request.path().isLike(DEPOT_GROUP_CONTAINS_DEPOT_GROUPS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(request.path().size()-2),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "parent",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getParent()}
                        )                        
                    }
                )
            );
        }
        // DepotEntityContainsDepot
        else if(request.path().isLike(DEPOT_ENTITY_CONTAINS_DEPOTS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("extent"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            SystemAttributes.OBJECT_IDENTITY,
                            FilterOperators.IS_LIKE,
                            new Object[]{request.path().getParent().getDescendant(new String[]{"depotHolder", ":*", "depot", ":*"})}
                        )                        
                    }
                )
            );
        }
        // FolderContainsFolder
        else if(request.path().isLike(FOLDER_CONTAINS_FOLDERS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("folder"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "parent",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getParent()}
                        )                        
                    }
                )
            );
        }
        // ObjectFinderSelectsIndexEntryActivity
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACTIVITY)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:activity1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryAccount
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACCOUNT)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:account1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryContract
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_CONTRACT)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:contract1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryProduct
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_PRODUCT)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:product1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryDocument
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_DOCUMENT)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:document1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryBuilding
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_BUILDING)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:building1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // ObjectFinderSelectsIndexEntryDepot
        else if(request.path().isLike(OBJECT_FINDER_SELECTS_INDEX_ENTRY_DEPOT)) {
            Path segmentIdentity = new Path(
                new String[]{"org:opencrx:kernel:depot1", "provider", request.path().get(2), "segment", request.path().get(4)}
            );
            MappedRecord objectFinder = this.requestHelper.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = DerivedReferences.mapObjectFinderToFilter(objectFinder);
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    segmentIdentity.getChild("indexEntry"),
                    filter
                )
            );
        }
        // NamespaceContainsElement
        else if(request.path().isLike(MODEL_NAMESPACE_CONTAINS_ELEMENTS)) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("element"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "container",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(7)}
                        )                        
                    }
                )
            );
        }
        // ContractPositionHasModification
        else if(
            request.path().isLike(CONTRACT_POSITION_HAS_MODIFICATION) ||
            request.path().isLike(REMOVED_CONTRACT_POSITION_HAS_MODIFICATION)
        ) {
            reply = this.requestHelper.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(7).getChild("positionModification"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "involved",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(9)}
                        )                        
                    }
                )
            );
        }            
        return reply;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */    
    public static FilterProperty[] mapObjectFinderToFilter(
    	MappedRecord objectFinder
    ) throws ServiceException {
    	ObjectHolder_2Facade objectFinderFacade;
        try {
	        objectFinderFacade = ObjectHolder_2Facade.newInstance(objectFinder);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        String allWords = (String)objectFinderFacade.attributeValue("allWords");
        if((allWords != null) && (allWords.length() > 0)) {
            String words[] = allWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                filter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "keywords",
                        FilterOperators.IS_LIKE,
                        ".*" + words[i] + ".*"
                    )
                );
            }
        }
        String withoutWords = (String)objectFinderFacade.attributeValue("withoutWords");
        if((withoutWords != null) && (withoutWords.length() > 0)) {
            String words[] = withoutWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                filter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "keywords",
                        FilterOperators.IS_UNLIKE,
                        ".*" + words[i] + ".*"
                    )
                );
            }
        }
        String atLeastOneOfTheWords = (String)objectFinderFacade.attributeValue("atLeastOneOfTheWords");
        if((atLeastOneOfTheWords != null) && (atLeastOneOfTheWords.length() > 0)) {
            String words[] = atLeastOneOfTheWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                words[i] = ".*" + words[i] + ".*";
            }
            filter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "keywords",
                    FilterOperators.IS_LIKE,
                    (Object[])words
                )
            );
        }
        return filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static FilterProperty[] getProductFilterProperties(
        Path productFilterIdentity,
        boolean forCounting,
        RequestCollection channel
    ) throws ServiceException {
        List<MappedRecord> filterProperties = channel.addFindRequest(
            productFilterIdentity.getChild("productFilterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        boolean hasQueryFilterClause = false;        
        for(
            Iterator<MappedRecord> i = filterProperties.iterator();
            i.hasNext();
        ) {
        	MappedRecord filterProperty = i.next();
        	ObjectHolder_2Facade filterPropertyFacade;
            try {
	            filterPropertyFacade = ObjectHolder_2Facade.newInstance(filterProperty);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            String filterPropertyClass = filterPropertyFacade.getObjectClass();    
            Boolean isActive = (Boolean)filterPropertyFacade.attributeValue("isActive");
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:product1:ProductQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Products.getInstance().getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterPropertyFacade.attributeValue("clause")
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                        )
                    );
                    // stringParam
                    List<Object> values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterPropertyFacade.attributeValues("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterPropertyFacade.attributeValue("filterOperator")).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterPropertyFacade.attributeValues("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterPropertyFacade.attributeValue("filterQuantor")).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:product1:ProductClassificationFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "classification",
                                filterOperator,
                                filterPropertyFacade.attributeValues("classification").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:DefaultSalesTaxTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "salesTaxType",
                                filterOperator,
                                filterPropertyFacade.attributeValues("salesTaxType").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:CategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterPropertyFacade.attributeValues("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:PriceUomFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "priceUom",
                                filterOperator,
                                filterPropertyFacade.attributeValues("priceUom").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:product1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterPropertyFacade.attributeValues("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Products.getInstance().getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{
                        Database_1_Attributes.HINT_COUNT + "(1=1)"
                    }
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                )
            );            
        }        
        return filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static FilterProperty[] getContractFilterProperties(
        Path contractFilterIdentity,
        boolean forCounting,
        RequestCollection channel
    ) throws ServiceException {
        List filterProperties = channel.addFindRequest(
            contractFilterIdentity.getChild("filterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List filter = new ArrayList();
        boolean hasQueryFilterClause = false;
        for(
            Iterator<MappedRecord> i = filterProperties.iterator();
            i.hasNext();
        ) {
        	MappedRecord filterProperty = i.next();
        	ObjectHolder_2Facade filterPropertyFacade;
            try {
	            filterPropertyFacade = ObjectHolder_2Facade.newInstance(filterProperty);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	
            String filterPropertyClass = filterPropertyFacade.getObjectClass();    
            Boolean isActive = (Boolean)filterPropertyFacade.attributeValue("isActive");            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:contract1:ContractQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Contracts.getInstance().getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterPropertyFacade.attributeValue("clause")
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterPropertyFacade.attributeValues("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterPropertyFacade.attributeValue("filterOperator")).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterPropertyFacade.attributeValues("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterPropertyFacade.attributeValue("filterQuantor")).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:contract1:ContractTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                SystemAttributes.OBJECT_CLASS,
                                filterOperator,
                                filterPropertyFacade.attributeValues("contractType").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:ContractStateFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "contractState",
                                filterOperator,
                                filterPropertyFacade.attributeValues("contractState").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:ContractPriorityFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "priority",
                                filterOperator,
                                filterPropertyFacade.attributeValues("priority").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:TotalAmountFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "totalAmount",
                                filterOperator,
                                filterPropertyFacade.attributeValues("totalAmount").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:CustomerFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "customer",
                                filterOperator,
                                filterPropertyFacade.attributeValues("customer").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:SupplierFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "supplier",
                                filterOperator,
                                filterPropertyFacade.attributeValues("supplier").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:SalesRepFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "salesRep",
                                filterOperator,
                                filterPropertyFacade.attributeValues("salesRep").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:contract1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterPropertyFacade.attributeValues("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Contracts.getInstance().getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }
        return (FilterProperty[])filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static FilterProperty[] getActivityFilterProperties(
        Path activityFilterIdentity,
        boolean forCounting,
        RequestCollection channel
    ) throws ServiceException {
        List<MappedRecord> filterProperties = channel.addFindRequest(
            activityFilterIdentity.getChild("filterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        boolean hasQueryFilterClause = false;
        for(
            Iterator<MappedRecord> i = filterProperties.iterator();
            i.hasNext();
        ) {
        	MappedRecord filterProperty = i.next();
        	ObjectHolder_2Facade filterPropertyFacade;
            try {
	            filterPropertyFacade = ObjectHolder_2Facade.newInstance(filterProperty);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	        	
            String filterPropertyClass = filterPropertyFacade.getObjectClass();
            Boolean isActive = (Boolean)filterPropertyFacade.attributeValue("isActive");
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:activity1:ActivityQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Activities.getInstance().getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterPropertyFacade.attributeValue("clause")
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List<Object> values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterPropertyFacade.attributeValues("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterPropertyFacade.attributeValue("filterOperator")).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterPropertyFacade.attributeValues("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterPropertyFacade.attributeValue("filterQuantor")).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:activity1:ActivityStateFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "activityState",
                                filterOperator,
                                filterPropertyFacade.attributeValues("activityState").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:ScheduledStartFilterProperty".equals(filterPropertyClass)) {
                        if(filterPropertyFacade.attributeValues("scheduledStart").isEmpty()) {
                        	filterPropertyFacade.attributeValues("scheduledStart").add(
                                DateFormat.getInstance().format(new Date())
                            );
                        }
                        if(!filterPropertyFacade.attributeValues("offsetInHours").isEmpty()) {
                            int offsetInHours = ((Number)filterPropertyFacade.attributeValue("offsetInHours")).intValue();
                            for(int j = 0; j < filterPropertyFacade.attributeValues("scheduledStart").size(); j++) {
                                try {
                                    GregorianCalendar date = new GregorianCalendar();
                                    date.setTime(
                                        DateFormat.getInstance().parse((String)filterPropertyFacade.attributeValues("scheduledStart").get(j))
                                    );
                                    date.add(GregorianCalendar.HOUR_OF_DAY, offsetInHours);
                                    filterPropertyFacade.attributeValues("scheduledStart").set(
                                        j, 
                                        DateFormat.getInstance().format(date.getTime())
                                    );
                                } 
                                catch(Exception e) {}
                            }
                        }
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "scheduledStart",
                                filterOperator,
                                filterPropertyFacade.attributeValues("scheduledStart").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:ScheduledEndFilterProperty".equals(filterPropertyClass)) {
                        if(filterPropertyFacade.attributeValues("scheduledEnd").isEmpty()) {
                        	filterPropertyFacade.attributeValues("scheduledEnd").add(
                                DateFormat.getInstance().format(new Date())
                            );
                        }
                        if(!filterPropertyFacade.attributeValues("offsetInHours").isEmpty()) {
                            int offsetInHours = ((Number)filterPropertyFacade.attributeValue("offsetInHours")).intValue();
                            for(int j = 0; j < filterPropertyFacade.attributeValues("scheduledEnd").size(); j++) {
                                try {
                                    GregorianCalendar date = new GregorianCalendar();
                                    date.setTime(
                                        DateFormat.getInstance().parse((String)filterPropertyFacade.attributeValues("scheduledEnd").get(j))
                                    );
                                    date.add(GregorianCalendar.HOUR_OF_DAY, offsetInHours);
                                    filterPropertyFacade.attributeValues("scheduledEnd").set(
                                        j, 
                                        DateFormat.getInstance().format(date.getTime())
                                    );
                                } 
                                catch(Exception e) {}
                            }
                        }
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "scheduledEnd",
                                filterOperator,
                                filterPropertyFacade.attributeValues("scheduledEnd").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:ActivityProcessStateFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "processState",
                                filterOperator,
                                filterPropertyFacade.attributeValues("processState").toArray()
                            )                    
                        );
                    }
                    else if("org:opencrx:kernel:activity1:ActivityTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "activityType",
                                filterOperator,
                                filterPropertyFacade.attributeValues("activityType").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:AssignedToFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "assignedTo",
                                filterOperator,
                                filterPropertyFacade.attributeValues("contact").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:ActivityNumberFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "activityNumber",
                                filterOperator,
                                filterPropertyFacade.attributeValues("activityNumber").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:activity1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterPropertyFacade.attributeValues("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Activities.getInstance().getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }
        return filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static FilterProperty[] getAddressFilterProperties(
        Path activityFilterIdentity,
        boolean forCounting,
        RequestCollection channel
    ) throws ServiceException {
        List<MappedRecord> filterProperties = channel.addFindRequest(
            activityFilterIdentity.getChild("addressFilterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        boolean hasQueryFilterClause = false;
        for(
            Iterator<MappedRecord> i = filterProperties.iterator();
            i.hasNext();
        ) {
        	MappedRecord filterProperty = i.next();
        	ObjectHolder_2Facade filterPropertyFacade;
            try {
	            filterPropertyFacade = ObjectHolder_2Facade.newInstance(filterProperty);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	        	        	
            String filterPropertyClass = filterPropertyFacade.getObjectClass();
            Boolean isActive = (Boolean)filterPropertyFacade.attributeValue("isActive");            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:account1:AddressQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Accounts.getInstance().getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterPropertyFacade.attributeValue("clause")
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List<Object> values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterPropertyFacade.attributeValues("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterPropertyFacade.attributeValue("filterOperator")).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterPropertyFacade.attributeValues("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterPropertyFacade.attributeValues("filterQuantor").get(0)).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:account1:AddressCategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterPropertyFacade.attributeValues("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressTypeFilterProperty".equals(filterPropertyClass)) {
                        List<String> addressTypes = new ArrayList<String>();
                        for(Iterator<Object> j = filterPropertyFacade.attributeValues("addressType").iterator(); j.hasNext(); ) {
                            int addressType = ((Number)j.next()).intValue();
                            addressTypes.add(
                                Addresses.ADDRESS_TYPES[addressType]
                            );
                        }
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                SystemAttributes.OBJECT_INSTANCE_OF,
                                filterOperator,
                                addressTypes.toArray()
                            )
                        );
                    }                    
                    else if("org:opencrx:kernel:account1:AddressMainFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "isMain",
                                filterOperator,
                                filterPropertyFacade.attributeValues("isMain").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressUsageFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "usage",
                                filterOperator,
                                filterPropertyFacade.attributeValues("usage").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressDisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterPropertyFacade.attributeValues("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Accounts.getInstance().getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }        
        return filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static FilterProperty[] getAccountFilterProperties(
        Path accountFilterIdentity,
        boolean forCounting,
        RequestCollection channel        
    ) throws ServiceException {
        List<MappedRecord> filterProperties = channel.addFindRequest(
            accountFilterIdentity.getChild("accountFilterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        boolean hasQueryFilterClause = false;
        for(
            Iterator<MappedRecord> i = filterProperties.iterator();
            i.hasNext();
        ) {
        	MappedRecord filterProperty = i.next();
        	ObjectHolder_2Facade filterPropertyFacade;
            try {
	            filterPropertyFacade = ObjectHolder_2Facade.newInstance(filterProperty);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	        	        	        	
            String filterPropertyClass = filterPropertyFacade.getObjectClass();    
            Boolean isActive = (Boolean)filterPropertyFacade.attributeValue("isActive");            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:account1:AccountQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Accounts.getInstance().getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterPropertyFacade.attributeValue("clause")
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List<Object> values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<Object> j = filterPropertyFacade.attributeValues(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterPropertyFacade.attributeValues("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterPropertyFacade.attributeValue("filterOperator")).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterPropertyFacade.attributeValues("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterPropertyFacade.attributeValue("filterQuantor")).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:account1:AccountTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "accountType",
                                filterOperator,
                                filterPropertyFacade.attributeValues("accountType").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AccountCategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "accountCategory",
                                filterOperator,
                                filterPropertyFacade.attributeValues("accountCategory").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:CategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterPropertyFacade.attributeValues("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterPropertyFacade.attributeValues("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + Accounts.getInstance().getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }
        return filter.toArray(new FilterProperty[filter.size()]);
    }

	//-------------------------------------------------------------------------
    // Patterns for derived find requests
    private static final Path COMPOUND_BOOKING_HAS_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*/booking");
    private static final Path DEPOT_POSITION_HAS_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*/booking");
    private static final Path DEPOT_POSITION_HAS_SIMPLE_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*/simpleBooking");
    private static final Path CLASSIFIER_CLASSIFIES_TYPED_ELEMENT = new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*/typedElement");
    private static final Path DEPOT_REPORT_ITEM_HAS_BOOKING_ITEMS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*/itemBooking");
    private static final Path DEPOT_GROUP_CONTAINS_DEPOTS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*/depot");
    private static final Path DEPOT_GROUP_CONTAINS_DEPOT_GROUPS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*/depotGroup");
    private static final Path DEPOT_ENTITY_CONTAINS_DEPOTS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depot");
    private static final Path FOLDER_CONTAINS_FOLDERS = new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*/folder");
    private static final Path MODEL_NAMESPACE_CONTAINS_ELEMENTS = new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*/content");
    private static final Path GLOBAL_FILTER_INCLUDES_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*/filteredActivity");
    private static final Path GLOBAL_FILTER_INCLUDES_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*/filteredAccount");
    private static final Path GLOBAL_FILTER_INCLUDES_ADDRESS = new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*/filteredAddress");
    private static final Path GLOBAL_FILTER_INCLUDES_CONTRACT = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/contractFilter/:*/filteredContract");
    private static final Path GLOBAL_FILTER_INCLUDES_PRODUCT = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productFilter/:*/filteredProduct");
    private static final Path ACTIVITY_GROUP_FILTER_INCLUDES_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*/filteredActivity");
    private static final Path PRODUCT_PRICE_LEVEL_HAS_FILTERED_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/filteredAccount");
    private static final Path PRODUCT_PRICE_LEVEL_HAS_FILTERED_PRODUCT = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/filteredProduct");
    private static final Path PRODUCT_PRICE_LEVEL_HAS_ASSIGNED_PRICE_LIST_ENTRY = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/priceListEntry");
    private static final Path CONTRACT_POSITION_HAS_MODIFICATION = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/positionModification");
    private static final Path REMOVED_CONTRACT_POSITION_HAS_MODIFICATION = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/positionModification");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryActivity");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryAccount");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_CONTRACT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryContract");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_PRODUCT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryProduct");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_DOCUMENT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryDocument");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_BUILDING = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryBuilding");
    private static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_DEPOT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryDepot");
    
    private final RequestHelper requestHelper;

}

//--- End of File -----------------------------------------------------------

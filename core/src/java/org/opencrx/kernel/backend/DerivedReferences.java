/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: DerivedReferences.java,v 1.8 2008/01/21 00:29:27 wfro Exp $
 * Description: DerivedReferences
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/01/21 00:29:27 $
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

package org.opencrx.kernel.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderReply;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;

public class DerivedReferences {

    //-----------------------------------------------------------------------
    public DerivedReferences(
        Backend backend
    ) {
        this.backend = backend;
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
        return new DataproviderRequest(
            request,            
            new DataproviderObject(
                reference
            ),
            request.operation(),
            request.attributeFilter(),
            request.position(),
            request.size(),
            request.direction(),
            request.attributeSelector(),
            request.attributeSpecifier()
        );
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
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("activity"),
                    this.backend.getActivities().getActivityFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );
        }
        // GlobalFilterIncludesAccount
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_ACCOUNT)
        ) {
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("account"),
                    this.backend.getAccounts().getAccountFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );
        }
        // GlobalFilterIncludesAddress
        if(
            request.path().isLike(GLOBAL_FILTER_INCLUDES_ADDRESS)
        ) {
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("address"),
                    this.backend.getAddresses().getAddressFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );
        }
        // ActivityGroupFilterIncludesActivity
        else if(
            request.path().isLike(ACTIVITY_GROUP_FILTER_INCLUDES_ACTIVITY)
        ) {
            List filterProperties = new ArrayList();
            filterProperties.addAll(
                Arrays.asList(
                    this.backend.getActivities().getActivityFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );
            // Query for activity group name
            DataproviderObject_1_0 activityGroup = this.backend.retrieveObject(request.path().getPrefix(7));
            String queryContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
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
                    new Object[]{activityGroup.values("name").isEmpty() ? "NA" : activityGroup.values("name").get(0)}                    
                )
            );
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("activity"),
                    (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // PriceLevelHasFilteredAccount
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_FILTERED_ACCOUNT)
        ) {
            List filterProperties = new ArrayList();
            filterProperties.addAll(
                Arrays.asList(
                    this.backend.getAccounts().getAccountFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );            
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.account1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "account"}
                    ),
                    (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // PriceLevelHasFilteredProduct
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_FILTERED_PRODUCT)
        ) {
            List filterProperties = new ArrayList();
            filterProperties.addAll(
                Arrays.asList(
                    this.backend.getProducts().getProductFilterProperties(
                        request.path().getPrefix(request.path().size() - 1),
                        false
                    )
                )
            );      
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.product1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "product"}
                    ),
                    (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // PriceLevelHasAssignedPriceListEntry
        else if(
            request.path().isLike(PRODUCT_PRICE_LEVEL_HAS_ASSIGNED_PRICE_LIST_ENTRY)
        ) {
            List filterProperties = new ArrayList();
            filterProperties.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "priceLevel",
                    FilterOperators.IS_IN,
                    new Object[]{request.path().getPrefix(7)}
                    
                )
            );
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    new Path("xri:@openmdx:org.opencrx.kernel.product1/provider").getDescendant(
                       new String[]{request.path().get(2), "segment", request.path().get(4), "priceListEntry"}
                    ),
                    (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()])
                )
            );
        }
        // ResourceContainsWorkReportEntry
        else if(
            request.path().isLike(RESOURCE_CONTAINS_WORKREPORT_ENTRY)
        ) {
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("workReportEntry"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "resource",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(7)}
                            
                        )
                    }
                )
            );
        }
        // ActivityContainsWorkReportEntry
        else if(
            request.path().isLike(ACTIVITY_CONTAINS_WORKREPORT_ENTRY)
        ) {
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("workReportEntry"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "activity",
                            FilterOperators.IS_IN,
                            new Object[]{request.path().getPrefix(7)}
                            
                        )
                    }
                )
            );
        }
        // CompoundBookingHasBooking
        else if(request.path().isLike(COMPOUND_BOOKING_HAS_BOOKINGS)) {
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 itemPosition = this.backend.retrieveObject(request.path().getParent());
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(request.path().size()-3).getChild("itemBooking"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "position",
                            FilterOperators.IS_IN,
                            new Object[]{itemPosition.values("position").get(0)}
                        )                        
                    }
                )
            );
        }
        // DepotContainsDepot
        else if(request.path().isLike(DEPOT_GROUP_CONTAINS_DEPOTS)) {
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
        // FolderContainsDocument
        else if(request.path().isLike(FOLDER_CONTAINS_DOCUMENTS)) {
            reply = this.backend.getDelegatingLayer().find(
                header,
                this.remapFindRequest(
                    request,
                    request.path().getPrefix(5).getChild("document"),
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,
                            "folder",
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            DataproviderObject_1_0 objectFinder = this.backend.retrieveObject(
                 request.path().getParent()
            );
            FilterProperty[] filter = this.backend.getUserHomes().mapObjectFinderToFilter(objectFinder);
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
            reply = this.backend.getDelegatingLayer().find(
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
    // Patterns for derived find requests
    public static final Path COMPOUND_BOOKING_HAS_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*/booking");
    public static final Path DEPOT_POSITION_HAS_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*/booking");
    public static final Path DEPOT_POSITION_HAS_SIMPLE_BOOKINGS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*/simpleBooking");
    public static final Path CLASSIFIER_CLASSIFIES_TYPED_ELEMENT = new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*/typedElement");
    public static final Path DEPOT_REPORT_ITEM_HAS_BOOKING_ITEMS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*/itemBooking");
    public static final Path DEPOT_GROUP_CONTAINS_DEPOTS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*/depot");
    public static final Path DEPOT_GROUP_CONTAINS_DEPOT_GROUPS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*/depotGroup");
    public static final Path DEPOT_ENTITY_CONTAINS_DEPOTS = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depot");
    public static final Path FOLDER_CONTAINS_FOLDERS = new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*/folder");
    public static final Path FOLDER_CONTAINS_DOCUMENTS = new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*/document");
    public static final Path MODEL_NAMESPACE_CONTAINS_ELEMENTS = new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*/content");
    public static final Path GLOBAL_FILTER_INCLUDES_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*/filteredActivity");
    public static final Path GLOBAL_FILTER_INCLUDES_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*/filteredAccount");
    public static final Path GLOBAL_FILTER_INCLUDES_ADDRESS = new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*/filteredAddress");
    public static final Path ACTIVITY_GROUP_FILTER_INCLUDES_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*/filteredActivity");
    public static final Path RESOURCE_CONTAINS_WORKREPORT_ENTRY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*/workReportEntry");
    public static final Path ACTIVITY_CONTAINS_WORKREPORT_ENTRY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/workReportEntry");
    public static final Path PRODUCT_PRICE_LEVEL_HAS_FILTERED_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/filteredAccount");
    public static final Path PRODUCT_PRICE_LEVEL_HAS_FILTERED_PRODUCT = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/filteredProduct");
    public static final Path PRODUCT_PRICE_LEVEL_HAS_ASSIGNED_PRICE_LIST_ENTRY = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/priceListEntry");
    public static final Path CONTRACT_POSITION_HAS_MODIFICATION = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/positionModification");
    public static final Path REMOVED_CONTRACT_POSITION_HAS_MODIFICATION = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/positionModification");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryActivity");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_ACCOUNT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryAccount");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_CONTRACT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryContract");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_PRODUCT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryProduct");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_DOCUMENT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryDocument");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_BUILDING = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryBuilding");
    public static final Path OBJECT_FINDER_SELECTS_INDEX_ENTRY_DEPOT = new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*/indexEntryDepot");
    
    private final Backend backend;

}

//--- End of File -----------------------------------------------------------

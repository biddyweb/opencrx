/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: openCRX audit plugin
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.layer.persistence;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.FilterProperty;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.ConditionType;
import org.openmdx.base.query.Quantifier;
import org.openmdx.base.query.SortOrder;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.spi.Facades;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.w3c.format.DateTimeFormat;

/**
 * This plugin creates audit entries for modified objects.
 */
public class Audit_1 extends Indexed_1 {

    // --------------------------------------------------------------------------
    public Interaction getInteraction(
        javax.resource.cci.Connection connection
    ) throws ResourceException {
        return new LayerInteraction(connection);
    }
        	
    //-------------------------------------------------------------------------
    @Override
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1 delegation
    ) throws ServiceException {
        super.activate(
            id, 
            configuration, 
            delegation
        );
        for(Object value: configuration.values("visitorId").values()) {
	        this.visitorIds.add((String)value);	        	
        }
    }
    
    //-------------------------------------------------------------------------
    protected boolean isAuditee(
      MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:base:Auditee"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isInstanceOfBasicObject(
    	MappedRecord object
    ) {
        return Object_2Facade.getPath(object).size() > 5;
    }

    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path accessPath,
        String principalName
    ) {
        return accessPath.get(4) + ":" + principalName;
    }
    
    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private boolean areEqual(
        Object v1,
        Object v2
    ) {
        if(v1 == null) return v2 == null;
        if(v2 == null) return v1 == null;
        if(
        	(v1 instanceof Number) && 
        	(v2 instanceof Number)
        ) {
        	if(v1 instanceof Short || v2 instanceof Short) {
        		return ((Number)v1).shortValue() == ((Number)v2).shortValue();
        	} else if(v1 instanceof Integer || v2 instanceof Integer) {
        		return ((Number)v1).intValue() == ((Number)v2).intValue();        		
        	} else if(v1 instanceof Long || v2 instanceof Long) {
        		return ((Number)v1).longValue() == ((Number)v2).longValue();        		
        	} else {
                return ((Comparable)v1).compareTo(v2) == 0;        		
        	}
        }
        else if(
            (v1 instanceof Comparable) && 
            (v2 instanceof Comparable) &&
            (v1.getClass().equals(v2.getClass()))
        ) {
            return ((Comparable)v1).compareTo(v2) == 0;
        }
        else {
        	return v1.equals(v2);
        }
    }

    //-------------------------------------------------------------------------
    protected void setSecurityAttributes(
    	MappedRecord auditEntry
    ) throws ServiceException {
    	Object_2Facade auditEntryFacade;
        try {
	        auditEntryFacade = Object_2Facade.newInstance(auditEntry);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        // Owner of audit entries is segment administrator
        String segmentName = auditEntryFacade.getPath().get(4);
        auditEntryFacade.attributeValuesAsList("owner").clear();
        auditEntryFacade.attributeValuesAsList("owner").add(
            this.getQualifiedPrincipalName(auditEntryFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName + "." + SecurityKeys.USER_SUFFIX)
        );
        auditEntryFacade.attributeValuesAsList("owner").add(
            this.getQualifiedPrincipalName(auditEntryFacade.getPath(), SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS)
        );
        auditEntryFacade.attributeValuesAsList("accessLevelBrowse").clear();
        auditEntryFacade.attributeValuesAsList("accessLevelBrowse").add(
            new Short(SecurityKeys.ACCESS_LEVEL_BASIC)
        );
        auditEntryFacade.attributeValuesAsList("accessLevelUpdate").clear();
        auditEntryFacade.attributeValuesAsList("accessLevelUpdate").add(
            new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE)
        );
        auditEntryFacade.attributeValuesAsList("accessLevelDelete").clear();
        auditEntryFacade.attributeValuesAsList("accessLevelDelete").add(
            new Short(SecurityKeys.ACCESS_LEVEL_NA)
        );        
    }
    
    //-------------------------------------------------------------------------
    protected void setSystemAttributes(
        ServiceHeader header,
        MappedRecord object,
        short operation
    ) throws ServiceException {
    	Object_2Facade facade;
        try {
	        facade = Object_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        Date at = null;
        try {
        	at = header.getRequestedAt() == null ?
            	new Date() :
            	    DateTimeFormat.BASIC_UTC_FORMAT.parse(header.getRequestedAt());
        }
        catch(ParseException e) {
            at = new Date();
        }
        List<String> by = header.getPrincipalChain();
        switch(operation) {
            case DataproviderOperations.OBJECT_CREATION:
                // exclude Authority, Provider, Segment
                if(this.isInstanceOfBasicObject(object)) {
                  facade.attributeValuesAsList(SystemAttributes.CREATED_BY).clear();
                  facade.attributeValuesAsList(SystemAttributes.CREATED_BY).addAll(by);
                  facade.attributeValuesAsList(SystemAttributes.CREATED_AT).clear();
                  facade.attributeValuesAsList(SystemAttributes.CREATED_AT).add(at);
                }
                // no break here!         
            case DataproviderOperations.OBJECT_REPLACEMENT:
                // exclude Authority, Provider, Segment
                if(this.isInstanceOfBasicObject(object)) {
                  facade.attributeValuesAsList(SystemAttributes.MODIFIED_BY).clear();
                  facade.attributeValuesAsList(SystemAttributes.MODIFIED_BY).addAll(by);
                  facade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).clear();
                  facade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).add(at);
                }
                break;
        }        
    }
    
    // --------------------------------------------------------------------------
    public class LayerInteraction extends Indexed_1.LayerInteraction {
        
        public LayerInteraction(
            Connection connection
        ) throws ResourceException {
            super(connection);
        }

	    //-------------------------------------------------------------------------
        private String getVisitedBy(
        	String visitorId
        ) throws ServiceException {
        	// In case of bulk load mark audit entry as visited. 
        	return visitorId + ":" + (this.isBulkLoad() ?
        		DateTimeFormat.BASIC_UTC_FORMAT.format(new Date()) :
        			NOT_VISITED_SUFFIX);
        }

	    //-------------------------------------------------------------------------
	    private boolean isAuditSegment(
	        ServiceHeader header,
	        Path path
	    ) throws ServiceException {
	        Path p = path.getPrefix(5);
	        Boolean isAuditSegment = Audit_1.this.auditSegments.get(p);
	        if(isAuditSegment == null) {
	        	MappedRecord segment;
	            try {
	            	DataproviderRequest getRequest = new DataproviderRequest(
	                    Query_2Facade.newInstance(p).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
	            	DataproviderReply getReply = super.newDataproviderReply();
		            super.get(	            	
		                getRequest.getInteractionSpec(),
		                Query_2Facade.newInstance(getRequest.object()),
		                getReply.getResult()
		            );
		            segment = getReply.getObject();
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            Audit_1.this.auditSegments.put(
	                p,
	                isAuditSegment = new Boolean(Audit_1.this.isAuditee(segment))
	            );
	        }
	        return isAuditSegment.booleanValue();
	    }
	    
	    //-----------------------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	try {
		        Path reference = request.path().getParent();
		        if("audit".equals(reference.getBase())) {
		        	DataproviderRequest getRequest = new DataproviderRequest(
	                    Query_2Facade.newInstance(
	                        request.path().getPrefix(5).getDescendant(new String[]{"audit", request.path().getBase()})
	                    ).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
		        	DataproviderReply getReply = super.newDataproviderReply();
		        	super.get(
		        		getRequest.getInteractionSpec(),
		        		Query_2Facade.newInstance(getRequest.object()),
		        		getReply.getResult()
		        	);
		        	MappedRecord auditEntry = getReply.getObject(); 
	            	MappedRecord mappedAuditEntry = Object_2Facade.cloneObject(auditEntry);
	            	Object_2Facade.newInstance(mappedAuditEntry).setPath(
	                    request.path()
	                );		        	
		            if(reply.getResult() != null) {
		            	reply.getResult().add(
		            		mappedAuditEntry
		            	);
		            }
		            return true;
		        }
		        else {
		            return super.get(
		                ispec,
		                input,
		                output
		            );
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	try {
		        // If auditee is segment do not rewrite find request. Otherwise filter
		        // audit entries with auditee = requested reference
		        if(
		            (request.path().size() > 6) &&
		            "audit".equals(request.path().getBase())
		        ) {            
		            // Find audit entries assigned to requesting object,
		            // request.path().getParent() IS_IN auditee of audit entry
		            List<FilterProperty> filterProperties = request.attributeFilter() == null ? 
		            	new ArrayList<FilterProperty>() : 
		            	new ArrayList<FilterProperty>(Arrays.asList(request.attributeFilter()));
		            filterProperties.add(
		                new FilterProperty(
		                    Quantifier.THERE_EXISTS.code(),
		                    "auditee",
		                    ConditionType.IS_IN.code(),
		                    request.path().getParent().toXri()
		                )
		            );
		            DataproviderRequest findRequest = new DataproviderRequest(
	                    Query_2Facade.newInstance(
	                        request.path().getPrefix(5).getChild("audit")
	                    ).getDelegate(),
	                    DataproviderOperations.ITERATION_START,
	                    filterProperties.toArray(new FilterProperty[filterProperties.size()]),
	                    0, 
	                    500, // get max 500 audit entries
	                    SortOrder.ASCENDING.code(),
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    request.attributeSpecifier()
	                );
		            DataproviderReply findReply = super.newDataproviderReply();
		            super.find(
		            	findRequest.getInteractionSpec(),
		            	Query_2Facade.newInstance(findRequest.object()),
		            	findReply.getResult()
		            );
		            MappedRecord[] auditEntries = findReply.getObjects();	            
		            // Remap audit entries so that the parent of the mapped
		            // audit entries is the requesting object, i.e. the auditee.
		            List<MappedRecord> mappedAuditEntries = new ArrayList<MappedRecord>();
		            for(
		                int i = 0; 
		                i < auditEntries.length; 
		                i++
		            ) {
		            	MappedRecord mappedAuditEntry = Object_2Facade.cloneObject(auditEntries[i]);
		            	Object_2Facade.newInstance(mappedAuditEntry).setPath(
		                    request.path().getChild(Object_2Facade.getPath(auditEntries[i]).getBase())
		                );
		                mappedAuditEntries.add(
		                    mappedAuditEntry
		                );
		            }	            
		            // reply
		            if(reply.getResult() != null) {
		            	reply.getResult().addAll(
		            		mappedAuditEntries
		            	);
		            }
		            reply.setHasMore(
		            	Boolean.FALSE
		            );
		            reply.setTotal(
		                new Integer(mappedAuditEntries.size())
		            );
		            return true;
		        }
		        else {
		            return super.find(
		                ispec,
		                input,
		                output
		            );
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    @Override
	    public boolean put(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	try {
		        // Ignore replace requests for top-level objects such as segments 
		        // (if not user is segment admin) providers, authorities
		        String principalName = header.getPrincipalChain().size() == 0 ? 
		            null : 
		            (String)header.getPrincipalChain().get(0);        
		        if(
		            (request.path().size() > 5) ||
		            ((request.path().size() == 5) && principalName.startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR)) 
		        ) {
		            if(this.isAuditSegment(header, request.path())) {    
		                // Create audit entry
		            	DataproviderRequest getRequest = new DataproviderRequest(
	                        Query_2Facade.newInstance(request.path()).getDelegate(),
	                        DataproviderOperations.OBJECT_RETRIEVAL,
	                        AttributeSelectors.ALL_ATTRIBUTES,
	                        null
	                    );
		            	DataproviderReply getReply = super.newDataproviderReply();
		            	super.get(
		            		getRequest.getInteractionSpec(), 
		            		Query_2Facade.newInstance(getRequest.object()), 
		            		getReply.getResult()
		            	);
		            	MappedRecord existing = getReply.getObject();
		                // Create ObjectModificationAuditEntry and add it to segment
		                if(Audit_1.this.isAuditee(existing)) {
		                	MappedRecord auditEntry = Object_2Facade.newInstance(
		                        request.path().getPrefix(5).getDescendant(
		                        	new String[]{
		                        		"audit", 
		                        		UUIDConversion.toUID(UUIDs.newUUID())
		                        	}
		                        ),
		                        "org:opencrx:kernel:base:ObjectModificationAuditEntry"
		                    ).getDelegate();
		                	Object_2Facade auditEntryFacade = Object_2Facade.newInstance(auditEntry);
		                	auditEntryFacade.attributeValuesAsList("auditee").add(
		                        request.path().toXri()
		                    );
		                    for(Iterator<String> i = Audit_1.this.visitorIds.iterator(); i.hasNext(); ) {
		                        String visitorId = i.next();
		                        auditEntryFacade.attributeValuesAsList("visitedBy").add(
		                            this.getVisitedBy(visitorId)
		                        );
		                    }
		                    // Remove all attribute names from existing object (before
		                    // image) which are not modified. This produces a before image
		                    // which contains the attribute values before modification of
		                    // modified object attributes.
		                    MappedRecord beforeImage = Object_2Facade.cloneObject(existing);
		                    Object_2Facade.getValue(beforeImage).keySet().retainAll(
		                        Object_2Facade.getValue(request.object()).keySet()
		                    );
		                    Set<String> modifiedFeatures = Audit_1.this.getChangedAttributes(
		                        beforeImage,
		                        request.object()
		                    );
		                    // --> trivial update
		                    if(modifiedFeatures.isEmpty()) {
			                    // do not create audit entry if modifiedAt is only modified attribute	                    	
		                    }
		                    else if(
		                        ((modifiedFeatures.size() > 1) ||
		                        !modifiedFeatures.contains(SystemAttributes.MODIFIED_AT))
		                    ) {
		                        Object_2Facade.getValue(beforeImage).keySet().retainAll(
		                            modifiedFeatures
		                        );
		                        auditEntryFacade.attributeValuesAsList("beforeImage").add(
		                        	Audit_1.this.getBeforeImageAsString(beforeImage)
		                        );
		                        String modifiedFeaturesAsString = modifiedFeatures.toString();
		                        auditEntryFacade.attributeValuesAsList("modifiedFeatures").add(
		                            modifiedFeaturesAsString.length() > 300 
		                                ? modifiedFeaturesAsString.substring(0, 280) + "..." 
		                                : modifiedFeaturesAsString
		                        );
		                        Audit_1.this.setSystemAttributes(
		                            header, 
		                            auditEntry, 
		                            DataproviderOperations.OBJECT_CREATION
		                        );
		                        Audit_1.this.setSecurityAttributes(
		                            auditEntry
		                        );
		                
		                        // create entry
		                        try {
		                        	this.createAuditEntry(
		                                header,
		                                auditEntry
		                            );
		                        }
		                        catch(ServiceException e) {
		                            e.log();
		                        }
		                    }
		                }
		            }
		            
		            // Replace object
		            super.put(		            	
		                ispec,
		                input,
		                output
		            );
		            return true;
		        }
		        else {
		        	if(reply.getResult() != null) {
		        		reply.getResult().add(
		        			request.object()
		        		);
		        	}
		        	return true;
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }

	    //-----------------------------------------------------------------------
	    @SuppressWarnings("deprecation")
	    @Override
	    public boolean create(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	    	try {
		        // Create object
		        super.create(
		            ispec,
		            input,
		            output
		        );	       
		        // Create audit log entries for non-root-level objects only
		        // and for objects which are contained in an auditable segment
		        if(
		            (request.path().size() > 5) &&
		             this.isAuditSegment(header, request.path()) &&
		             Audit_1.this.isAuditee(request.object())
		        ) {	
		            // Create audit entry
		        	MappedRecord auditEntry = Object_2Facade.newInstance(
		                request.path().getPrefix(5).getDescendant(
		                	new String[]{
		                		"audit", 
		                		UUIDConversion.toUID(UUIDs.newUUID())
		                	}
		                ),
		                "org:opencrx:kernel:base:ObjectCreationAuditEntry"
		            ).getDelegate();
		        	Object_2Facade auditEntryFacade = Object_2Facade.newInstance(auditEntry);
		        	auditEntryFacade.attributeValuesAsList("auditee").add(
		                input.getPath().toXri()
		            );
		            for(Iterator<String> i = Audit_1.this.visitorIds.iterator(); i.hasNext(); ) {
		                String visitorId = i.next();
		                auditEntryFacade.attributeValuesAsList("visitedBy").add(
		                    this.getVisitedBy(visitorId)
		                );
		            }
		            Audit_1.this.setSystemAttributes(
		                header, 
		                auditEntry, 
		                DataproviderOperations.OBJECT_CREATION
		            );
		            Audit_1.this.setSecurityAttributes(
		                auditEntry
		            );            
		            // create entry
		            try {
		            	this.createAuditEntry(
		                    header, 
		                    auditEntry
		                );
		            }
		            catch(ServiceException e) {
		                e.log();
		            }
		        }           
		        return true;
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	    	try {
		        // Create audit log entries for non-root-level objects only
		        // and for objects which are contained in an auditable segment
		        if(
		            (request.path().size() > 5) &&
		             this.isAuditSegment(header, request.path())
		        ) {
		            // Create audit entry
		        	DataproviderRequest getRequest = new DataproviderRequest(
	                    Query_2Facade.newInstance(request.path()).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
		        	DataproviderReply getReply = super.newDataproviderReply();
		        	super.get(
		        		getRequest.getInteractionSpec(), 
		        		Query_2Facade.newInstance(getRequest.object()), 
		        		getReply.getResult()
		        	);
		        	MappedRecord existing = getReply.getObject();
		
		            // Create ObjectRemovalAuditEntry and add it to segment
		            if(Audit_1.this.isAuditee(existing)) {
		            	MappedRecord auditEntry = Object_2Facade.newInstance(
		                    request.path().getPrefix(5).getDescendant(
		                    	new String[]{
		                    		"audit", 
		                    		UUIDConversion.toUID(UUIDs.newUUID())
		                    	}
		                    ),
		                    "org:opencrx:kernel:base:ObjectRemovalAuditEntry"
		                ).getDelegate();
		            	Object_2Facade auditEntryFacade = Object_2Facade.newInstance(auditEntry);
		            	auditEntryFacade.attributeValuesAsList("auditee").add(
		                    request.path().toXri()
		                );
		                for(Iterator<String> i = Audit_1.this.visitorIds.iterator(); i.hasNext(); ) {
		                    String visitorId = i.next();
		                    auditEntryFacade.attributeValuesAsList("visitedBy").add(
		                        this.getVisitedBy(visitorId)
		                    );
		                }
		                auditEntryFacade.attributeValuesAsList("beforeImage").add(
		                	Audit_1.this.getBeforeImageAsString(existing)
		                );
		                Audit_1.this.setSystemAttributes(
		                    header, 
		                    auditEntry, 
		                    DataproviderOperations.OBJECT_CREATION
		                );
		                Audit_1.this.setSecurityAttributes(
		                    auditEntry
		                );
		                
		                // create entry
		                try {
		                	this.createAuditEntry(
		                        header, 
		                        auditEntry
		                    );
		                }
		                catch(ServiceException e) {
		                    e.log();
		                }
		            }
		        }        
		        // Remove object
		        super.delete(
		            ispec,
		            input,
		            output
		        );
		        return true;
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @Override
	    protected MappedRecord otherOperation(
	        ServiceHeader header,
	        DataproviderRequest request,
	        String operation, 
	        Path replyPath
	    ) throws ServiceException {
	    	try {
		        if("testAndSetVisitedBy".equals(operation)) {
		            Path auditEntryIdentity = request.path().getPrefix(request.path().size() - 2);
		            DataproviderRequest getRequest = new DataproviderRequest(
	                    Query_2Facade.newInstance(auditEntryIdentity).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
		            DataproviderReply getReply = super.newDataproviderReply();
		            super.get(
		            	getRequest.getInteractionSpec(), 
		            	Query_2Facade.newInstance(getRequest.object()), 
		            	getReply.getResult()
		            );
		            MappedRecord auditEntry = getReply.getObject();
		            Object_2Facade auditEntryFacade = Object_2Facade.newInstance(auditEntry);
		            String visitorId = (String)Object_2Facade.newInstance(request.object()).attributeValue("visitorId");
		            MappedRecord reply = Audit_1.this.createResult(
		                request,
		                "org:opencrx:kernel:base:TestAndSetVisitedByResult"
		            );
		            int pos = 0;
		            if(
		                (visitorId == null) ||
		                !Audit_1.this.visitorIds.contains(visitorId)
		            ) {
		                Object_2Facade.newInstance(reply).attributeValuesAsList("visitStatus").add(
		                    new Short((short)2)
		                );                
		            }
		            // Not yet visited by visitorId
		            else if((pos = auditEntryFacade.attributeValuesAsList("visitedBy").indexOf(visitorId + ":" + NOT_VISITED_SUFFIX)) >= 0) {
		            	auditEntryFacade.attributeValuesAsList("visitedBy").set(
		                    pos,
		                    visitorId + ":" + DateTimeFormat.BASIC_UTC_FORMAT.format(new Date())
		                );
		            	DataproviderRequest putRequest = new DataproviderRequest(
	                        auditEntry,
	                        DataproviderOperations.OBJECT_REPLACEMENT,
	                        AttributeSelectors.NO_ATTRIBUTES,
	                        null
	                    );
		            	DataproviderReply putReply = super.newDataproviderReply();
		            	super.put(
		            		putRequest.getInteractionSpec(), 
		            		Object_2Facade.newInstance(putRequest.object()), 
		            		putReply.getResult()
		            	);
		                Object_2Facade.newInstance(reply).attributeValuesAsList("visitStatus").add(
		                    new Short((short)0)
		                );
		            }            
		            else {
		            	 Object_2Facade.newInstance(reply).attributeValuesAsList("visitStatus").add(
		                    new Short((short)1)
		                );                
		            }
		            return reply;
		        }
		        // Delegate
		        else {
		            return super.otherOperation(
		                header,
		                request,
		                operation,
		                replyPath
		            );
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }

	    //-----------------------------------------------------------------------
	    protected void createAuditEntry(
	       ServiceHeader header,
	       MappedRecord auditEntry
	    ) throws ServiceException {
	    	DataproviderRequest createRequest = new DataproviderRequest(
	            auditEntry,
	            DataproviderOperations.OBJECT_CREATION,
	            AttributeSelectors.NO_ATTRIBUTES,
	            null
	        );
	    	DataproviderReply createReply = super.newDataproviderReply();
	    	try {
		        super.create(
		            createRequest.getInteractionSpec(),
		            Object_2Facade.newInstance(createRequest.object()),
		            createReply.getResult()
		        );
	    	} catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    	    
    }
    
    //-------------------------------------------------------------------------
    protected MappedRecord createResult(
      DataproviderRequest request,
      String structName
    ) throws ServiceException {
    	MappedRecord result = null;
        try {
	        result = Object_2Facade.newInstance(
	            request.path().getDescendant(
	              new String[]{
	            	  "reply", 
	            	  UUIDConversion.toUID(UUIDs.newUUID())
	              }
	            ),
	            structName
	        ).getDelegate();
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        catch (ServiceException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    	return result;
    }

    //-------------------------------------------------------------------------
    /**
     * Return the set of attributes which's values changed in o2 relative to o1.
     */
    @SuppressWarnings("unchecked")
    protected Set<String> getChangedAttributes(
    	MappedRecord o1,
    	MappedRecord o2
    ) throws ServiceException {
        if(o2 == null) {
          return new HashSet<String>();
        }
        Object_2Facade o1Facade = Facades.asObject(o1);
        Object_2Facade o2Facade = Facades.asObject(o2);
        // touch all o1 attributes in o2
        for(
          Iterator<String> i = o1Facade.getValue().keySet().iterator();
          i.hasNext();
        ) {
        	o2Facade.attributeValuesAsList(i.next());
        }        
        // diff
        Set<String> changedAttributes = new HashSet<String>();
        for(
            Iterator<String> i = o2Facade.getValue().keySet().iterator();
            i.hasNext();
        ) {
            String attributeName = i.next();
            List<Object> v1 = o1Facade.attributeValuesAsList(attributeName);
            List<Object> v2 = o2Facade.attributeValuesAsList(attributeName);
            if(
                !SystemAttributes.OBJECT_INSTANCE_OF.equals(attributeName) &&
                !"identity".equals(attributeName) && 
                !attributeName.startsWith(SystemAttributes.CONTEXT_PREFIX)
            ) { 
                boolean isEqual = v1.size() == v2.size();
                if(isEqual) {
                    for(int j = 0; j < v1.size(); j++) {
                    	isEqual = this.areEqual(v1.get(j), v2.get(j));
                        if(!isEqual) break;
                    }
                }
                if(!isEqual) {
                    changedAttributes.add(attributeName);
                }
            }
        }
        return changedAttributes;
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public String getBeforeImageAsString(
    	MappedRecord beforeImage
    ) throws ServiceException {
        String beforeImageAsString = "";
        Object_2Facade beforeImageFacade = Facades.asObject(beforeImage);
        for(
            Iterator<String> i = beforeImageFacade.getValue().keySet().iterator();
            i.hasNext();
        ) {
            String attributeName = i.next();
            beforeImageAsString += attributeName + ":\n";
            int jj = 0;
            for(
                Iterator<Object> j = beforeImageFacade.attributeValuesAsList(attributeName).iterator();
                j.hasNext();
                jj++
            ) {
                beforeImageAsString += "" + jj + ": " + j.next() + "\n";
            }
        }
        return beforeImageAsString;
    }
    
    //-----------------------------------------------------------------------
    protected static final String NOT_VISITED_SUFFIX = "-";

    protected final Map<Path,Boolean> auditSegments = new HashMap<Path,Boolean>();
    protected final List<String> visitorIds = new ArrayList<String>();
    
}

//--- End of File -----------------------------------------------------------

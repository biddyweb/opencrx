/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Audit_1.java,v 1.46 2009/06/13 18:47:42 wfro Exp $
 * Description: openCRX audit plugin
 * Revision:    $Revision: 1.46 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/13 18:47:42 $
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

import java.math.BigDecimal;
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
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderReplyContexts;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.DataproviderRequestContexts;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.layer.model.LayerConfigurationEntries;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.collection.SparseList;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.Directions;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.rest.spi.ObjectHolder_2Facade;
import org.openmdx.base.text.format.DatatypeFormat;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

/**
 * This plugin creates audit entries for modified objects.
 */
public class Audit_1 
    extends Indexed_1 {

    //-------------------------------------------------------------------------
    @SuppressWarnings({
        "unchecked"
    })
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws ServiceException {
        super.activate(
            id, 
            configuration, 
            delegation
        );
        this.datatypeFormat = configuration.isOn(
            LayerConfigurationEntries.XML_DATATYPES
        ) ? DatatypeFormat.newInstance(true) : null;        
        this.visitorIds.addAll(
            (SparseList)configuration.values("visitorId")
        );
    }
    
    //-------------------------------------------------------------------------
    public void prolog(
        ServiceHeader header,
        DataproviderRequest[] requests
    ) throws ServiceException {
        super.prolog(
          header,
          requests
        );
        String unitOfWorkId = requests.length == 0
          ? null
          : requests[0].context(DataproviderRequestContexts.UNIT_OF_WORK_ID).size() == 0
            ? null
            : (String)requests[0].context(DataproviderRequestContexts.UNIT_OF_WORK_ID).get(0);
        // guarantee that this.unitOfWorkId has always a valid value
        this.unitOfWorkId = unitOfWorkId != null
          ? unitOfWorkId
          : this.unitOfWorkId != null
            ? this.unitOfWorkId
            : super.uidAsString();    
    }

    // --------------------------------------------------------------------------
    /**
     * Tells whether XML datatype formatting is required
     * @return
     */
    protected boolean useDatatypes(){
        return this.datatypeFormat != null;
    }
        
    //-------------------------------------------------------------------------
    private boolean isAuditee(
      MappedRecord object
    ) throws ServiceException {
        String objectClass = ObjectHolder_2Facade.getObjectClass(object);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:base:Auditee"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isInstanceOfBasicObject(
    	MappedRecord object
    ) {
        return ObjectHolder_2Facade.getPath(object).size() > 5;
    }

    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path accessPath,
        String principalName
    ) {
        return accessPath.get(4) + ":" + principalName;
    }
    
    //-------------------------------------------------------------------------
    protected void setSecurityAttributes(
    	MappedRecord auditEntry
    ) throws ServiceException {
    	ObjectHolder_2Facade auditEntryFacade;
        try {
	        auditEntryFacade = ObjectHolder_2Facade.newInstance(auditEntry);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        // Owner of audit entries is segment administrator
        String segmentName = auditEntryFacade.getPath().get(4);
        auditEntryFacade.clearAttributeValues("owner");
        auditEntryFacade.attributeValues("owner").add(
            this.getQualifiedPrincipalName(auditEntryFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName + "." + SecurityKeys.USER_SUFFIX)
        );
        auditEntryFacade.attributeValues("owner").add(
            this.getQualifiedPrincipalName(auditEntryFacade.getPath(), SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS)
        );
        auditEntryFacade.clearAttributeValues("accessLevelBrowse").add(
            new Short(SecurityKeys.ACCESS_LEVEL_BASIC)
        );
        auditEntryFacade.clearAttributeValues("accessLevelUpdate").add(
            new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE)
        );
        auditEntryFacade.clearAttributeValues("accessLevelDelete").add(
            new Short(SecurityKeys.ACCESS_LEVEL_NA)
        );        
    }
    
    //-------------------------------------------------------------------------
    protected void setSystemAttributes(
        ServiceHeader header,
        MappedRecord object,
        short operation
    ) throws ServiceException {
    	ObjectHolder_2Facade facade;
        try {
	        facade = ObjectHolder_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        String at = header.getRequestedAt();
        if(at == null) at = DateFormat.getInstance().format(new Date());        
        List<String> by = header.getPrincipalChain();
        switch(operation) {
            case DataproviderOperations.OBJECT_CREATION:
                // exclude Authority, Provider, Segment
                if(this.isInstanceOfBasicObject(object)) {
                  facade.clearAttributeValues(
                      SystemAttributes.CREATED_BY
                  ).addAll(
                      by
                  );
                  facade.clearAttributeValues(
                      SystemAttributes.CREATED_AT
                  ).add(
                      this.useDatatypes() ? this.datatypeFormat.marshal(at) : at
                  );
                }
                // no break here!         
            case DataproviderOperations.OBJECT_MODIFICATION:
            case DataproviderOperations.OBJECT_REPLACEMENT:
            case DataproviderOperations.OBJECT_SETTING: 
                // exclude Authority, Provider, Segment
                if(this.isInstanceOfBasicObject(object)) {
                  facade.clearAttributeValues(
                      SystemAttributes.MODIFIED_BY
                  ).addAll(
                      by
                  );
                  facade.clearAttributeValues(
                      SystemAttributes.MODIFIED_AT
                  ).add(
                      this.useDatatypes() ? this.datatypeFormat.marshal(at) : at
                  );
                }
                break;
        }        
    }
    
    //-------------------------------------------------------------------------
    private boolean isAuditSegment(
        ServiceHeader header,
        Path path
    ) throws ServiceException {
        Path p = path.getPrefix(5);
        Boolean isAuditSegment = this.auditSegments.get(p);
        if(isAuditSegment == null) {
        	MappedRecord segment;
            try {
	            segment = super.get(
	                header,
	                new DataproviderRequest(
	                    ObjectHolder_2Facade.newInstance(p).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                )
	            ).getObject();
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            this.auditSegments.put(
                p,
                isAuditSegment = new Boolean(this.isAuditee(segment))
            );
        }
        return isAuditSegment.booleanValue();
    }
    
    //-------------------------------------------------------------------------
    private MappedRecord createResult(
      DataproviderRequest request,
      String structName
    ) throws ServiceException {
    	MappedRecord result = null;
        try {
	        result = ObjectHolder_2Facade.newInstance(
	            request.path().getDescendant(
	              new String[]{ "reply", super.uidAsString()}
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
    private Set<String> getChangedAttributes(
    	MappedRecord o1,
    	MappedRecord o2
    ) throws ServiceException {
        if(o2 == null) {
          return new HashSet<String>();
        }
        ObjectHolder_2Facade o1Facade;
        try {
	        o1Facade = ObjectHolder_2Facade.newInstance(o1);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        ObjectHolder_2Facade o2Facade;
        try {
	        o2Facade = ObjectHolder_2Facade.newInstance(o2);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }        
        // touch all o1 attributes in o2
        for(
          Iterator<String> i = o1Facade.getValue().keySet().iterator();
          i.hasNext();
        ) {
        	o2Facade.attributeValues(i.next());
        }        
        // diff
        Set<String> changedAttributes = new HashSet<String>();
        for(
            Iterator<String> i = o2Facade.getValue().keySet().iterator();
            i.hasNext();
        ) {
            String attributeName = i.next();
            SparseList<Object> v1 = o1Facade.attributeValues(attributeName);
            SparseList<Object> v2 = o2Facade.attributeValues(attributeName);
            if(
                !SystemAttributes.OBJECT_INSTANCE_OF.equals(attributeName) &&
                !"identity".equals(attributeName) && 
                !attributeName.startsWith(SystemAttributes.CONTEXT_PREFIX)
            ) { 
                boolean isEqual = v1.size() == v2.size();
                if(isEqual) {
                    for(int j = 0; j < v1.size(); j++) {
                        if(v1.get(j) instanceof BigDecimal) {
                            if(v2.get(j) instanceof BigDecimal) {
                                isEqual = ((BigDecimal)v1.get(j)).compareTo(((BigDecimal)v2.get(j))) == 0;
                            }
                            else {
                                isEqual = false;
                            }
                        }
                        else if(v1.get(j) == null) {
                            isEqual = v2.get(j) == null;
                        }
                        else {
                            isEqual = v1.get(j).equals(v2.get(j));
                        }
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
    public String getBeforeImageAsString(
    	MappedRecord beforeImage
    ) throws ServiceException {
        String beforeImageAsString = "";
        ObjectHolder_2Facade beforeImageFacade;
        try {
	        beforeImageFacade = ObjectHolder_2Facade.newInstance(beforeImage);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        for(
            Iterator<String> i = beforeImageFacade.getValue().keySet().iterator();
            i.hasNext();
        ) {
            String attributeName = i.next();
            beforeImageAsString += attributeName + ":\n";
            int jj = 0;
            for(
                Iterator<Object> j = beforeImageFacade.attributeValues(attributeName).iterator();
                j.hasNext();
                jj++
            ) {
                beforeImageAsString += "" + jj + ": " + j.next() + "\n";
            }
        }
        return beforeImageAsString;
    }
    
    //-----------------------------------------------------------------------
    protected void createAuditEntry(
       ServiceHeader header,
       MappedRecord auditEntry
    ) throws ServiceException {
        super.create(
            header,
            new DataproviderRequest(
                auditEntry,
                DataproviderOperations.OBJECT_CREATION,
                AttributeSelectors.NO_ATTRIBUTES,
                null
            )
        );        
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply get(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
    	try {
	        Path reference = request.path().getParent();
	        if("audit".equals(reference.getBase())) {            
	        	MappedRecord auditEntry = super.get(
	                header,
	                new DataproviderRequest(
	                    ObjectHolder_2Facade.newInstance(
	                        request.path().getPrefix(5).getDescendant(new String[]{"audit", request.path().getBase()})
	                    ).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                )
	            ).getObject();            
	        	MappedRecord mappedAuditEntry = ObjectHolder_2Facade.newInstance(
	                request.path()
	            ).getDelegate();
	            ObjectHolder_2Facade.getValue(mappedAuditEntry).putAll(
	                ObjectHolder_2Facade.getValue(auditEntry)
	            );
	            return new DataproviderReply(
	                mappedAuditEntry
	            );
	        }
	        else {
	            return super.get(
	                header, 
	                request
	            );
	        }
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
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
	                    Quantors.THERE_EXISTS,
	                    "auditee",
	                    FilterOperators.IS_IN,
	                    request.path().getParent().toXri()
	                )
	            );
	            MappedRecord[] auditEntries = super.find(
	                header,
	                new DataproviderRequest(
	                    ObjectHolder_2Facade.newInstance(
	                        request.path().getPrefix(5).getChild("audit")
	                    ).getDelegate(),
	                    DataproviderOperations.ITERATION_START,
	                    filterProperties.toArray(new FilterProperty[filterProperties.size()]),
	                    0, 
	                    500, // get max 500 audit entries
	                    Directions.ASCENDING,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    request.attributeSpecifier()
	                )
	            ).getObjects();	            
	            // Remap audit entries so that the parent of the mapped
	            // audit entries is the requesting object, i.e. the auditee.
	            List<MappedRecord> mappedAuditEntries = new ArrayList<MappedRecord>();
	            for(
	                int i = 0; 
	                i < auditEntries.length; 
	                i++
	            ) {
	            	MappedRecord mappedAuditEntry = ObjectHolder_2Facade.cloneObject(auditEntries[i]);
	            	ObjectHolder_2Facade.newInstance(mappedAuditEntry).setPath(
	                    request.path().getChild(ObjectHolder_2Facade.getPath(auditEntries[i]).getBase())
	                );
	                mappedAuditEntries.add(
	                    mappedAuditEntry
	                );
	            }	            
	            // reply
	            DataproviderReply reply = new DataproviderReply(
	                mappedAuditEntries
	            );
	            reply.context(
	                DataproviderReplyContexts.HAS_MORE
	            ).set(0, Boolean.FALSE);
	            reply.context(
	                DataproviderReplyContexts.TOTAL
	            ).set(
	                0, 
	                new Integer(mappedAuditEntries.size())
	            );
	            reply.context(DataproviderReplyContexts.ATTRIBUTE_SELECTOR).set(
	                0,
	                new Short(request.attributeSelector())
	            );
	            return reply;            
	        }
	        else {
	            return super.find(
	                header, 
	                request
	            );
	        }
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply replace(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        
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
	            	MappedRecord existing = super.get(
	                    header,
	                    new DataproviderRequest(
	                        request.object(),
	                        DataproviderOperations.OBJECT_RETRIEVAL,
	                        AttributeSelectors.ALL_ATTRIBUTES,
	                        null
	                    )
	                ).getObject();                
	                // Create ObjectModificationAuditEntry and add it to segment
	                if(this.isAuditee(existing)) {
	                	MappedRecord auditEntry = ObjectHolder_2Facade.newInstance(
	                        request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()}),
	                        "org:opencrx:kernel:base:ObjectModificationAuditEntry"
	                    ).getDelegate();
	                	ObjectHolder_2Facade auditEntryFacade = ObjectHolder_2Facade.newInstance(auditEntry);
	                	auditEntryFacade.attributeValues("auditee").add(
	                        request.path().toXri()
	                    );
	                    if(this.unitOfWorkId != null) {
	                    	auditEntryFacade.attributeValues("unitOfWork").add(
	                            this.unitOfWorkId
	                        );
	                    }
	                    for(Iterator<String> i = this.visitorIds.iterator(); i.hasNext(); ) {
	                        String visitorId = i.next();
	                        auditEntryFacade.attributeValues("visitedBy").add(
	                            visitorId + ":" + NOT_VISITED_SUFFIX
	                        );
	                    }
	                    // Remove all attribute names from existing object (before
	                    // image) which are not modified. This produces a before image
	                    // which contains the attribute values before modification of
	                    // modified object attributes.
	                    MappedRecord beforeImage;
                        try {
	                        beforeImage = ObjectHolder_2Facade.cloneObject(existing);
                        }
                        catch (ResourceException e) {
                        	throw new ServiceException(e);
                        }            
	                    ObjectHolder_2Facade.getValue(beforeImage).keySet().retainAll(
	                        ObjectHolder_2Facade.getValue(request.object()).keySet()
	                    );
	                    Set<String> modifiedFeatures = this.getChangedAttributes(
	                        beforeImage,
	                        request.object()
	                    );
	                    // do not create audit entry if modifiedAt is only modified attribute
	                    // --> trivial update
	                    if(modifiedFeatures.isEmpty()) {
	                        ServiceException e = new ServiceException(
	                            BasicException.Code.DEFAULT_DOMAIN,
	                            BasicException.Code.INVALID_CONFIGURATION, 
	                            "Replace request leading to empty modified feature set. No audit entry will be created.",
	                            new BasicException.Parameter("request", request),
	                            new BasicException.Parameter("existing", existing)
	                        );
	                        SysLog.info(e.getMessage(), e.getCause());
	                    }
	                    else if(
	                        ((modifiedFeatures.size() > 1) ||
	                        !modifiedFeatures.contains(SystemAttributes.MODIFIED_AT))
	                    ) {
	                        ObjectHolder_2Facade.getValue(beforeImage).keySet().retainAll(
	                            modifiedFeatures
	                        );
	                        auditEntryFacade.attributeValues("beforeImage").add(
	                            this.getBeforeImageAsString(beforeImage)
	                        );
	                        String modifiedFeaturesAsString = modifiedFeatures.toString();
	                        auditEntryFacade.attributeValues("modifiedFeatures").add(
	                            modifiedFeaturesAsString.length() > 300 
	                                ? modifiedFeaturesAsString.substring(0, 280) + "..." 
	                                : modifiedFeaturesAsString
	                        );
	                        this.setSystemAttributes(
	                            header, 
	                            auditEntry, 
	                            DataproviderOperations.OBJECT_CREATION
	                        );
	                        this.setSecurityAttributes(
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
	            return super.replace(
	                header, 
	                request
	            );
	        }
	        else {
	            return new DataproviderReply(
	                request.object()
	            );
	        }
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }

    //-----------------------------------------------------------------------
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
    	try {
	        // Create object
	        DataproviderReply reply = super.create(
	            header, 
	            request
	        );	       
	        // Create audit log entries for non-root-level objects only
	        // and for objects which are contained in an auditable segment
	        if(
	            (request.path().size() > 5) &&
	             this.isAuditSegment(header, request.path()) &&
	             this.isAuditee(request.object())
	        ) {	
	            // Create audit entry
	        	MappedRecord auditEntry = ObjectHolder_2Facade.newInstance(
	                request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()}),
	                "org:opencrx:kernel:base:ObjectCreationAuditEntry"
	            ).getDelegate();
	        	ObjectHolder_2Facade auditEntryFacade = ObjectHolder_2Facade.newInstance(auditEntry);
	        	auditEntryFacade.attributeValues("auditee").add(
	                ObjectHolder_2Facade.getPath(reply.getObject()).toXri()
	            );
	            if(this.unitOfWorkId != null) {
	            	auditEntryFacade.attributeValues("unitOfWork").add(
	                    this.unitOfWorkId
	                );
	            }
	            for(Iterator<String> i = this.visitorIds.iterator(); i.hasNext(); ) {
	                String visitorId = i.next();
	                auditEntryFacade.attributeValues("visitedBy").add(
	                    visitorId + ":" + NOT_VISITED_SUFFIX
	                );
	            }            
	            this.setSystemAttributes(
	                header, 
	                auditEntry, 
	                DataproviderOperations.OBJECT_CREATION
	            );
	            this.setSecurityAttributes(
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
	        return reply;
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {    
    	try {
	        // Create audit log entries for non-root-level objects only
	        // and for objects which are contained in an auditable segment
	        if(
	            (request.path().size() > 5) &&
	             this.isAuditSegment(header, request.path())
	        ) {
	            // Create audit entry
	        	MappedRecord existing = super.get(
	                header,
	                new DataproviderRequest(
	                    request.object(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                )
	            ).getObject();
	
	            // Create ObjectRemovalAuditEntry and add it to segment
	            if(this.isAuditee(existing)) {
	            	MappedRecord auditEntry = ObjectHolder_2Facade.newInstance(
	                    request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()}),
	                    "org:opencrx:kernel:base:ObjectRemovalAuditEntry"
	                ).getDelegate();
	            	ObjectHolder_2Facade auditEntryFacade = ObjectHolder_2Facade.newInstance(auditEntry);
	            	auditEntryFacade.attributeValues("auditee").add(
	                    request.path().toXri()
	                );
	                if(this.unitOfWorkId != null) {
	                	auditEntryFacade.attributeValues("unitOfWork").add(
	                        this.unitOfWorkId
	                    );
	                }
	                for(Iterator<String> i = this.visitorIds.iterator(); i.hasNext(); ) {
	                    String visitorId = i.next();
	                    auditEntryFacade.attributeValues("visitedBy").add(
	                        visitorId + ":" + NOT_VISITED_SUFFIX
	                    );
	                }                
	                auditEntryFacade.attributeValues("beforeImage").add(
	                    this.getBeforeImageAsString(existing)
	                );
	                this.setSystemAttributes(
	                    header, 
	                    auditEntry, 
	                    DataproviderOperations.OBJECT_CREATION
	                );
	                this.setSecurityAttributes(
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
	        return super.remove(
	            header, 
	            request
	        );
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
	            MappedRecord auditEntry = super.get(
	                header,
	                new DataproviderRequest(
	                    ObjectHolder_2Facade.newInstance(auditEntryIdentity).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                )             
	            ).getObject();
	            ObjectHolder_2Facade auditEntryFacade = ObjectHolder_2Facade.newInstance(auditEntry);
	            String visitorId = (String)ObjectHolder_2Facade.newInstance(request.object()).attributeValue("visitorId");
	            MappedRecord reply = this.createResult(
	                request,
	                "org:opencrx:kernel:base:TestAndSetVisitedByResult"
	            );
	            int pos = 0;
	            if(
	                (visitorId == null) ||
	                !this.visitorIds.contains(visitorId)
	            ) {
	                ObjectHolder_2Facade.newInstance(reply).attributeValues("visitStatus").add(
	                    new Short((short)2)
	                );                
	            }
	            // Not yet visited by visitorId
	            else if((pos = auditEntryFacade.attributeValues("visitedBy").indexOf(visitorId + ":" + NOT_VISITED_SUFFIX)) >= 0) {
	            	auditEntryFacade.attributeValues("visitedBy").set(
	                    pos,
	                    visitorId + ":" + org.openmdx.base.text.format.DateFormat.getInstance().format(new Date())
	                );
	                super.replace(
	                    header,
	                    new DataproviderRequest(
	                        auditEntry,
	                        DataproviderOperations.OBJECT_REPLACEMENT,
	                        AttributeSelectors.NO_ATTRIBUTES,
	                        null
	                    )
	                );
	                ObjectHolder_2Facade.newInstance(reply).attributeValues("visitStatus").add(
	                    new Short((short)0)
	                );
	            }            
	            else {
	            	 ObjectHolder_2Facade.newInstance(reply).attributeValues("visitStatus").add(
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
    private static final String NOT_VISITED_SUFFIX = "-";

    private String unitOfWorkId = null;
    private final Map<Path,Boolean> auditSegments = new HashMap<Path,Boolean>();
    private final List<String> visitorIds = new ArrayList<String>();
    private DatatypeFormat datatypeFormat = null;
    
}

//--- End of File -----------------------------------------------------------

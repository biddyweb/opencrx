/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Audit_1.java,v 1.30 2008/09/30 14:10:09 wfro Exp $
 * Description: openCRX audit plugin
 * Revision:    $Revision: 1.30 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/30 14:10:09 $
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

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.format.DatatypeFormat;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.application.configuration.Configuration;
import org.openmdx.compatibility.base.collection.SparseList;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderReply;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderReplyContexts;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequestContexts;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.model.LayerConfigurationEntries;
import org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

/**
 * This plugin creates audit entries for modified objects.
 */
public class Audit_1 
    extends Indexed_1 {

    //-------------------------------------------------------------------------
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws Exception, ServiceException {
        super.activate(
            id, 
            configuration, 
            delegation
        );
        this.datatypeFormat = configuration.isOn(
            LayerConfigurationEntries.XML_DATATYPES
        ) ? DatatypeFormat.newInstance(true) : null;        
        this.visitorIds.addAll(
            configuration.values("visitorId")
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
      DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.model.isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:base:Auditee"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isInstanceOfBasicObject(
        DataproviderObject_1_0 object
    ) {
        return object.path().size() > 5;
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
        DataproviderObject auditEntry
    ) {
        // Owner of audit entries is segment administrator
        String segmentName = auditEntry.path().get(4);
        auditEntry.clearValues("owner");
        auditEntry.values("owner").add(
            this.getQualifiedPrincipalName(auditEntry.path(), SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName + "." + SecurityKeys.USER_SUFFIX)
        );
        auditEntry.values("owner").add(
            this.getQualifiedPrincipalName(auditEntry.path(), SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS)
        );
        auditEntry.clearValues("accessLevelBrowse").add(
            new Short(SecurityKeys.ACCESS_LEVEL_BASIC)
        );
        auditEntry.clearValues("accessLevelUpdate").add(
            new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE)
        );
        auditEntry.clearValues("accessLevelDelete").add(
            new Short(SecurityKeys.ACCESS_LEVEL_NA)
        );        
    }
    
    //-------------------------------------------------------------------------
    protected void setSystemAttributes(
        ServiceHeader header,
        DataproviderObject object,
        short operation
    ) throws ServiceException {
        String at = header.getRequestedAt();
        if(at == null) at = DateFormat.getInstance().format(new Date());        
        List by = header.getPrincipalChain();
        switch(operation) {
            case DataproviderOperations.OBJECT_CREATION:
                // exclude Authority, Provider, Segment
                if(this.isInstanceOfBasicObject(object)) {
                  object.clearValues(
                      SystemAttributes.CREATED_BY
                  ).addAll(
                      by
                  );
                  object.clearValues(
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
                  object.clearValues(
                      SystemAttributes.MODIFIED_BY
                  ).addAll(
                      by
                  );
                  object.clearValues(
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
        Boolean isAuditSegment = (Boolean)this.auditSegments.get(p);
        if(isAuditSegment == null) {
            DataproviderObject segment = super.get(
                header,
                new DataproviderRequest(
                    new DataproviderObject(p),
                    DataproviderOperations.OBJECT_RETRIEVAL,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null
                )
            ).getObject();
            this.auditSegments.put(
                p,
                isAuditSegment = new Boolean(this.isAuditee(segment))
            );
        }
        return isAuditSegment.booleanValue();
    }
    
    //-------------------------------------------------------------------------
    private DataproviderObject createResult(
      DataproviderRequest request,
      String structName
    ) {
      DataproviderObject result = new DataproviderObject(
        request.path().getDescendant(
          new String[]{ "reply", super.uidAsString()}
        )
      );
      result.clearValues(SystemAttributes.OBJECT_CLASS).add(
        structName
      );
      return result;
    }

    //-------------------------------------------------------------------------
    /**
     * Return the set of attributes which's values changed in o2 relative to o1.
     */
    private Set getChangedAttributes(
        DataproviderObject_1_0 o1,
        DataproviderObject_1_0 o2
    ) {
        if(o2 == null) {
          return new HashSet();
        }
        
        // touch all o1 attributes in o2
        for(
          Iterator i = o1.attributeNames().iterator();
          i.hasNext();
        ) {
          o2.values((String)i.next());
        }
        
        // diff
        Set changedAttributes = new HashSet();
        for(
            Iterator i = o2.attributeNames().iterator();
            i.hasNext();
        ) {
            String attributeName = (String)i.next();
            SparseList v1 = o1.values(attributeName);
            SparseList v2 = o2.values(attributeName);
            if(
                !SystemAttributes.OBJECT_INSTANCE_OF.equals(attributeName) &&
                !"identity".equals(attributeName)
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
        DataproviderObject_1_0 beforeImage
    ) {
        String beforeImageAsString = "";
        for(
            Iterator i = beforeImage.attributeNames().iterator();
            i.hasNext();
        ) {
            String attributeName = (String)i.next();
            beforeImageAsString += attributeName + ":\n";
            int jj = 0;
            for(
                Iterator j = beforeImage.values(attributeName).iterator();
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
       DataproviderObject auditEntry
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
        Path reference = request.path().getParent();
        if("audit".equals(reference.getBase())) {            
            DataproviderObject auditEntry = super.get(
                header,
                new DataproviderRequest(
                    new DataproviderObject(
                        request.path().getPrefix(5).getDescendant(new String[]{"audit", request.path().getBase()})
                    ),
                    DataproviderOperations.OBJECT_RETRIEVAL,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null
                )
            ).getObject();            
            DataproviderObject mappedAuditEntry = new DataproviderObject(
                request.path()
            );
            mappedAuditEntry.addClones(
                auditEntry,
                true
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
    
    //-----------------------------------------------------------------------
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        // If auditee is segment do not rewrite find request. Otherwise filter
        // audit entries with auditee = requested reference
        if(
            (request.path().size() > 6) &&
            "audit".equals(request.path().getBase())
        ) {            
            // Find audit entries assigned to requesting object,
            // request.path().getParent() IS_IN auditee of audit entry
            List filterProperties = request.attributeFilter() == null
                ? new ArrayList()
                : new ArrayList(Arrays.asList(request.attributeFilter()));
            filterProperties.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "auditee",
                    FilterOperators.IS_IN,
                    request.path().getParent().toXri()
                )
            );
            DataproviderObject[] auditEntries = super.find(
                header,
                new DataproviderRequest(
                    new DataproviderObject(
                        request.path().getPrefix(5).getChild("audit")
                    ),
                    DataproviderOperations.ITERATION_START,
                    (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()]),
                    0, 
                    500, // get max 500 audit entries
                    Directions.ASCENDING,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    request.attributeSpecifier()
                )
            ).getObjects();
            
            // Remap audit entries so that the parent of the mapped
            // audit entries is the requesting object, i.e. the auditee.
            List mappedAuditEntries = new ArrayList();
            for(
                int i = 0; 
                i < auditEntries.length; 
                i++
            ) {
                DataproviderObject mappedAuditEntry = new DataproviderObject(
                    request.path().getChild(auditEntries[i].path().getBase())
                );
                mappedAuditEntry.addClones(
                    auditEntries[i],
                    true
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
    
    //-----------------------------------------------------------------------
    public DataproviderReply replace(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        
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
                DataproviderObject_1_0 existing = super.get(
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
                    DataproviderObject auditEntry = new DataproviderObject(
                        request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()})
                    );
                    auditEntry.values(SystemAttributes.OBJECT_CLASS).add(
                        "org:opencrx:kernel:base:ObjectModificationAuditEntry"
                    );
                    auditEntry.values("auditee").add(
                        request.path().toXri()
                    );
                    if(this.unitOfWorkId != null) {
                        auditEntry.values("unitOfWork").add(
                            this.unitOfWorkId
                        );
                    }
                    for(Iterator i = this.visitorIds.iterator(); i.hasNext(); ) {
                        String visitorId = (String)i.next();
                        auditEntry.values("visitedBy").add(
                            visitorId + ":" + NOT_VISITED_SUFFIX
                        );
                    }
                    // Remove all attribute names from existing object (before
                    // image) which are not modified. This produces a before image
                    // which contains the attribute values before modification of
                    // modified object attributes.
                    DataproviderObject beforeImage = new DataproviderObject(existing);            
                    beforeImage.attributeNames().retainAll(
                        request.object().attributeNames()
                    );
                    Set modifiedFeatures = this.getChangedAttributes(
                        beforeImage,
                        request.object()
                    );
                    // do not create audit entry if modifiedAt is only modified attribute
                    // --> trivial update
                    if(modifiedFeatures.isEmpty()) {
                        ServiceException e = new ServiceException(
                            BasicException.Code.DEFAULT_DOMAIN,
                            BasicException.Code.INVALID_CONFIGURATION, 
                            new BasicException.Parameter[]{
                                new BasicException.Parameter("request", request),
                                new BasicException.Parameter("existing", existing)
                            },
                            "Replace request leading to empty modified feature set. No audit entry will be created."
                        );
                        SysLog.info(e.getMessage(), e.getCause());
                    }
                    else if(
                        ((modifiedFeatures.size() > 1) ||
                        !modifiedFeatures.contains(SystemAttributes.MODIFIED_AT))
                    ) {
                        beforeImage.attributeNames().retainAll(
                            modifiedFeatures
                        );
                        auditEntry.values("beforeImage").add(
                            this.getBeforeImageAsString(beforeImage)
                        );
                        String modifiedFeaturesAsString = modifiedFeatures.toString();
                        auditEntry.values("modifiedFeatures").add(
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

    //-----------------------------------------------------------------------
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {

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
            DataproviderObject auditEntry = new DataproviderObject(
                request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()})
            );
            auditEntry.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:base:ObjectCreationAuditEntry"
            );
            auditEntry.values("auditee").add(
                reply.getObject().path().toXri()
            );
            if(this.unitOfWorkId != null) {
                auditEntry.values("unitOfWork").add(
                    this.unitOfWorkId
                );
            }
            for(Iterator i = this.visitorIds.iterator(); i.hasNext(); ) {
                String visitorId = (String)i.next();
                auditEntry.values("visitedBy").add(
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
    
    //-----------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        
        // Create audit log entries for non-root-level objects only
        // and for objects which are contained in an auditable segment
        if(
            (request.path().size() > 5) &&
             this.isAuditSegment(header, request.path())
        ) {

            // Create audit entry
            DataproviderObject_1_0 existing = super.get(
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
                DataproviderObject auditEntry = new DataproviderObject(
                    request.path().getPrefix(5).getDescendant(new String[]{"audit", super.uidAsString()})
                );
                auditEntry.values(SystemAttributes.OBJECT_CLASS).add(
                    "org:opencrx:kernel:base:ObjectRemovalAuditEntry"
                );
                auditEntry.values("auditee").add(
                    request.path().toXri()
                );
                if(this.unitOfWorkId != null) {
                    auditEntry.values("unitOfWork").add(
                        this.unitOfWorkId
                    );
                }
                for(Iterator i = this.visitorIds.iterator(); i.hasNext(); ) {
                    String visitorId = (String)i.next();
                    auditEntry.values("visitedBy").add(
                        visitorId + ":" + NOT_VISITED_SUFFIX
                    );
                }                
                auditEntry.values("beforeImage").add(
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
    
    //-----------------------------------------------------------------------
    protected DataproviderObject otherOperation(
        ServiceHeader header,
        DataproviderRequest request,
        String operation, 
        Path replyPath
    ) throws ServiceException {
        if("testAndSetVisitedBy".equals(operation)) {
            Path auditEntryIdentity = request.path().getPrefix(request.path().size() - 2);
            DataproviderObject auditEntry = super.get(
                header,
                new DataproviderRequest(
                    new DataproviderObject(auditEntryIdentity),
                    DataproviderOperations.OBJECT_RETRIEVAL,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null
                )             
            ).getObject();
            String visitorId = (String)request.object().values("visitorId").get(0);
            DataproviderObject reply = this.createResult(
                request,
                "org:opencrx:kernel:base:TestAndSetVisitedByResult"
            );
            int pos = 0;
            if(
                (visitorId == null) ||
                !this.visitorIds.contains(visitorId)
            ) {
                reply.values("visitStatus").add(
                    new Short((short)2)
                );                
            }
            // Not yet visited by visitorId
            else if((pos = auditEntry.values("visitedBy").indexOf(visitorId + ":" + NOT_VISITED_SUFFIX)) >= 0) {
                auditEntry.values("visitedBy").set(
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
                reply.values("visitStatus").add(
                    new Short((short)0)
                );
            }            
            else {
                reply.values("visitStatus").add(
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
    
    //-----------------------------------------------------------------------
    private static final String NOT_VISITED_SUFFIX = "-";
    
    private String unitOfWorkId = null;
    private final Map auditSegments = new HashMap();
    private final List visitorIds = new ArrayList();
    private DatatypeFormat datatypeFormat = null;
    
}

//--- End of File -----------------------------------------------------------

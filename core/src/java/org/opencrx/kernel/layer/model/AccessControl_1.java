/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: AccessControl_1.java,v 1.146 2010/12/10 11:42:41 wfro Exp $
 * Description: openCRX access control plugin
 * Revision:    $Revision: 1.146 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/10 11:42:41 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2005, CRIXP Corp., Switzerland
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

package org.opencrx.kernel.layer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.layer.model.SecurityContext.CachedPrincipal;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.FilterProperty;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.application.dataprovider.layer.model.Standard_1;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.application.dataprovider.spi.ResourceHelper;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.spi.EntityManagerFactory_1;
import org.openmdx.base.accessor.rest.DataManagerFactory_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.ConfigurableProperty;
import org.openmdx.base.query.ConditionType;
import org.openmdx.base.query.Quantifier;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.cci.MessageRecord;
import org.openmdx.base.rest.spi.ConnectionFactoryAdapter;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

/**
 * openCRX access control plugin. Implements the openCRX access control logic.
 */
public class AccessControl_1 extends Standard_1 {
	
    //-------------------------------------------------------------------------
	public AccessControl_1(
	) {
	}

    // --------------------------------------------------------------------------
    public Interaction getInteraction(
        Connection connection
    ) throws ResourceException {
        return new LayerInteraction(connection);
    }

    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	CachedPrincipal principal
    ) {
    	return this.getUserIdentity(
    		principal.getIdentity().get(6), 
    		principal.getIdentity().getBase()
    	);
    }
    	
    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	String qualifiedPrincipalName
    ) {
        int pos = qualifiedPrincipalName.indexOf(":");
        String realmName = null;
        String principalName = null;
        if(pos < 0) {
            SysLog.error("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName);
            realmName = "Root";
            principalName = qualifiedPrincipalName;
        }
        else {
            realmName = qualifiedPrincipalName.substring(0, pos);
            principalName = qualifiedPrincipalName.substring(pos+1);
        }
        return this.getUserIdentity(
        	realmName, 
        	principalName
        );
    }
    
    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	String realmName,
    	String principalName
    ) {
        // <= 1.7.1 compatibility. Principals with name 'admin'|'loader' must be
        // mapped to 'admin-<segment>'
        if(SecurityKeys.ADMIN_PRINCIPAL.equals(principalName) || SecurityKeys.LOADER_PRINCIPAL.equals(principalName)) {
            principalName = principalName + SecurityKeys.ID_SEPARATOR + realmName;
        }
        // <= 1.7.1 compatibility. Qualified principal name must have the
        // format of a user principal
        if(!principalName.endsWith(SecurityKeys.USER_SUFFIX)) {
            principalName += "." + SecurityKeys.USER_SUFFIX;
        }
        Path userIdentity = this.realmIdentity.getParent().getDescendant(
            new String[]{realmName, "principal", principalName}             
        );
        return userIdentity;
    }

    //-------------------------------------------------------------------------
    protected Path getUser(
    	SecurityContext.CachedPrincipal principal
    ) throws ServiceException {
    	return this.getUserIdentity(principal);
    }
        
    //-------------------------------------------------------------------------
    protected boolean hasReadAccess(
    	Object_2Facade objectFacade,
    	Object_2Facade parentFacade,
    	SecurityContext securityContext,
    	SecurityContext.CachedPrincipal requestingPrincipal,
        Path requestingUser
    ) throws ServiceException {
        Set<String> memberships = new HashSet<String>();
        if(parentFacade.attributeValuesAsList("accessLevelBrowse").isEmpty()) {
        	SysLog.error("missing attribute value for accessLevelBrowse", parentFacade);
        }
        else {
            memberships = securityContext.getMemberships(
                requestingPrincipal,
                requestingUser,
                ((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
                AccessControl_1.this.useExtendedAccessLevelBasic
            );
        }
        if(memberships != null) {
            memberships.retainAll(objectFacade.attributeValuesAsList("owner"));
            return !memberships.isEmpty();
        }
        else {
        	return true;
        }
    }
    
    //-------------------------------------------------------------------------
    protected void applyBrowseFilter(
    	Object_2Facade parentFacade,
    	DataproviderRequest request,
    	SecurityContext securityContext,
    	SecurityContext.CachedPrincipal requestingPrincipal,
        Path requestingUser    	
    ) throws ServiceException {
    	Set<String> memberships = new HashSet<String>();    	
        if(parentFacade.attributeValuesAsList("accessLevelBrowse").isEmpty()) {
        	SysLog.error("Missing attribute value for accessLevelBrowse", parentFacade);
        }
        else {
            memberships = securityContext.getMemberships(
                requestingPrincipal,
                requestingUser,
                ((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
                AccessControl_1.this.useExtendedAccessLevelBasic
            );
        }	            
        // allowedPrincipals == null --> global access. Do not restrict to allowed subjects
        if(memberships != null) {
        	// Optimize query
        	FilterProperty ownerFilterProperty = null;
        	for(FilterProperty p: request.attributeFilter()) {
        		if("owner".equals(p.name())) {
        			ownerFilterProperty = p;
        			break;
        		}
        	}
        	if(ownerFilterProperty != null) {
        		memberships.removeAll(
        			ownerFilterProperty.values()
        		);
        	}
    		// Add condition for owner
        	if(!memberships.isEmpty()) {
	            request.addAttributeFilterProperty(
	                new FilterProperty(
	                    Quantifier.THERE_EXISTS.code(),
	                    "owner",
	                    ConditionType.IS_IN.code(),
	                    memberships.toArray()
	                )
	            );
        	}
        }
    }
    
    //-------------------------------------------------------------------------
    protected Path getGroupIdentity(
        Path accessPath,
        String qualifiedPrincipalName
    ) {
        int pos = qualifiedPrincipalName.indexOf(":");
        String segmentName = null;
        String principalName = null;
        if(pos < 0) {
            System.err.println("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName + "; path=" + accessPath.toXRI());
            segmentName = "Root";
            principalName = qualifiedPrincipalName;
        }
        else {
            segmentName = qualifiedPrincipalName.substring(0, pos);
            principalName = qualifiedPrincipalName.substring(pos+1);
        }
        Path principalIdentity = this.realmIdentity.getParent().getDescendant(
            new String[]{segmentName, "principal", principalName}             
        );
        return principalIdentity;
    }

    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path accessPath,
        String principalName
    ) {
        return accessPath.get(4) + ":" + principalName;
    }
    
    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path principalIdentity
    ) {
        return principalIdentity.get(6) + ":" + principalIdentity.getBase();
    }
    
    // --------------------------------------------------------------------------
    public class LayerInteraction extends Standard_1.LayerInteraction {
        
        public LayerInteraction(
            Connection connection
        ) throws ResourceException {
            super(connection);
        }
            
	    //-------------------------------------------------------------------------
	    protected MappedRecord retrieveObject(
	        Path identity
	    ) throws ServiceException {
	        try {
	        	DataproviderRequest getRequest = new DataproviderRequest(
	                Object_2Facade.newInstance(identity).getDelegate(),
	                DataproviderOperations.OBJECT_RETRIEVAL,
	                AttributeSelectors.ALL_ATTRIBUTES,
	                new AttributeSpecifier[]{}
	            );
	        	DataproviderReply getReply = this.newDataproviderReply();
		        super.get(
		            getRequest.getInteractionSpec(),
		            Query_2Facade.newInstance(getRequest.path()),
		            getReply.getResult()
		        );
		        return getReply.getObject();
	        }
	        catch (Exception e) {
	        	throw new ServiceException(e);
	        }
	    }
    
	    //-------------------------------------------------------------------------
	    /**
	     * Get the direct composite parent of the object with the given access path. 
	     * If path matches a configured inheritFromParentTypes path then the composite
	     * parent of the parent is returned, recursively. 
	     */
	    private MappedRecord getCachedObject(
	        ServiceHeader header,
	        Path path
	    ) throws ServiceException {    	
	    	ConcurrentMap<Path,Object[]> objectCache = AccessControl_1.getObjectCache();
	        // Remove cached parents if expired
	        for(Iterator<Map.Entry<Path,Object[]>> i = objectCache.entrySet().iterator(); i.hasNext(); ) {
	            Map.Entry<Path,Object[]> entry = i.next();
	            try {
	                if(entry != null) {
	                    if(entry.getValue() == null) {
	                        i.remove();
	                    }
	                    else {
	                        Long expiresAt = (Long)entry.getValue()[1];
	                        if((expiresAt == null) || (expiresAt < System.currentTimeMillis())) {
	                            i.remove();
	                        }
	                    }
	                }
	            } 
	            catch(Exception e) {}
	        }
	        // Get cached parent
	        Path reference = path;
	        if(reference.size() % 2 == 1) {
	            reference = path.getParent();
	        }
	        // Determine the parent according to the access level inheritance configuration
	        while(true) {
	            boolean matched = false;
	            for(
	                Iterator<Path> i = AccessControl_1.this.inheritFromParentTypes.iterator();
	                i.hasNext();
	            ) {
	                Path referencePattern = i.next();
	                if(reference.isLike(referencePattern)) {
	                    matched = true;
	                    break;
	                }
	            }
	            if(!matched) {
	                break;
	            }
	            reference = reference.getPrefix(reference.size() - 2);
	        }
	        // Get parent from cache or retrieve
	        Path parentPath = reference.getParent();
	        Object[] entry = objectCache.get(parentPath);
	        MappedRecord parent = null;
	        if(entry == null) {
	            parent = this.retrieveObject(
	                parentPath
	            );
	            this.addToObjectCache(parent);
	        }
	        else {
	            parent = (MappedRecord)entry[0];
	        }
	        return parent;
	    }
	    
	    //-------------------------------------------------------------------------
	    private void addToObjectCache(
	    	MappedRecord object
	    ) throws ServiceException {
	    	Object_2Facade facade;
	        try {
		        facade = Object_2Facade.newInstance(object);
	        }
	        catch (ResourceException e) {
	        	throw new ServiceException(e);
	        }
	        if(facade.getObjectClass() != null) {
	            try {
	            	AccessControl_1.getObjectCache().put(
		                facade.getPath(),
		                new Object[]{
		                    Object_2Facade.cloneObject(object),
		                    new Long(System.currentTimeMillis() + TTL_CACHED_PARENTS)
		                }
		            );
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	        }
	        else {
	        	SysLog.error("Missing object class. Object not added to cache", facade.getPath());
	        }
	    }
	    	    
	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#create(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean create(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        SecurityContext.CachedPrincipal requestingPrincipal = securityContext.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path requestingUser = AccessControl_1.this.getUser(requestingPrincipal);
	        // Check permission. Must have update permission for parent
	        MappedRecord parent = null;
	        Object_2Facade parentFacade = null;
	        if(request.path().size() >= 7) {
		        parent = this.getCachedObject(
	                header, 
	                request.path()
	            );
		        parentFacade = ResourceHelper.getObjectFacade(parent);
		        if(AccessControl_1.this.isSecureObject(parent)) {
		            Set<String> memberships = new HashSet<String>();
	                if(parentFacade.attributeValuesAsList("accessLevelUpdate").size() < 1) {
	                	SysLog.error("missing attribute value for accessLevelUpdate", parent);
	                }
	                else {
	                    memberships = securityContext.getMemberships(
	                        requestingPrincipal,
	                        requestingUser,
	                        ((Number)parentFacade.attributeValue("accessLevelUpdate")).shortValue(),
	                        AccessControl_1.this.useExtendedAccessLevelBasic
	                    );
	                }
		            if(memberships != null) {
	                    memberships.retainAll(parentFacade.attributeValuesAsList("owner"));
			            if(memberships.isEmpty()) {
			                throw new ServiceException(
			                    OpenCrxException.DOMAIN,
			                    OpenCrxException.AUTHORIZATION_FAILURE_CREATE, 
			                    "No permission to create object.",
	                            new BasicException.Parameter("object", request.path()),
	                            new BasicException.Parameter("param0", request.path()),
	                            new BasicException.Parameter("param1", requestingPrincipal.getIdentity())                                
			                );
			            }
		            }
		        }
	        }
	        // Create object
	        MappedRecord newObject = request.object();
	        // Set owner in case of secure objects
	        if(AccessControl_1.this.isSecureObject(newObject)) {
	            Object_2Facade newObjectFacade;
	            try {
		            newObjectFacade = Object_2Facade.newInstance(newObject);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }            
	            // Owning user
		        String owningUser = null;
	            // If new object is composite to user home set owning user to owning user of user home
	            if(
	                (newObjectFacade.getPath().size() > USER_HOME_PATH_PATTERN.size()) &&
	                newObjectFacade.getPath().getPrefix(USER_HOME_PATH_PATTERN.size()).isLike(USER_HOME_PATH_PATTERN) &&
	                !parentFacade.attributeValuesAsList("owner").isEmpty()
	            ) {
	               owningUser = (String)parentFacade.attributeValue("owner");
	            }   
	            // Owning user set on new object
	            else if(!newObjectFacade.attributeValuesAsList("owningUser").isEmpty()) {
		            owningUser = AccessControl_1.this.getQualifiedPrincipalName(
	                    (Path)newObjectFacade.attributeValue("owningUser")
	                );
		        }
	            // Set requesting principal as default
		        else {
		            // If no user found set owner to segment administrator
		            owningUser = newObjectFacade.attributeValuesAsList("owner").isEmpty() ? 
		            	requestingUser == null ? 
		            		AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
		            			AccessControl_1.this.getQualifiedPrincipalName(requestingUser) : 
		            	(String)newObjectFacade.attributeValue("owner");
		        }
	            newObjectFacade.attributeValuesAsList("owner").clear();
	            newObjectFacade.attributeValuesAsList("owner").add(owningUser);	
	            // Owning group
		        Set<Object> owningGroup = new HashSet<Object>();
		        if(
		        	(newObjectFacade.getAttributeValues("owningGroup") != null) && 
		        	!newObjectFacade.attributeValuesAsList("owningGroup").isEmpty()
		        ) {
		            for(Iterator<Object> i = newObjectFacade.attributeValuesAsList("owningGroup").iterator(); i.hasNext(); ) {
		                owningGroup.add(
		                	AccessControl_1.this.getQualifiedPrincipalName((Path)i.next())
		                );
		            }
		        }
		        else {
		        	Path userGroup = requestingPrincipal == null ? 
		        		null : 
		        		securityContext.getPrimaryGroup(requestingPrincipal);
	                owningGroup = new HashSet<Object>();
	                if(parent != null) {
	                    List<Object> ownersParent = new ArrayList<Object>(parentFacade.attributeValuesAsList("owner"));
	                    // Do not inherit group Users at segment-level
	                    if(parentFacade.getPath().size() == 5) {
		                    ownersParent.remove(
		                    	AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.USER_GROUP_USERS)
		                    );
	                    }
	                    if(!ownersParent.isEmpty()) {
	                        owningGroup.addAll(
	                            ownersParent.subList(1, ownersParent.size())
	                        );
	                    }
	                }
		            owningGroup.add(
	                    userGroup == null ? 
	                    	AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), "Unassigned") : 
	                    		AccessControl_1.this.getQualifiedPrincipalName(userGroup)
		            );
		        }
		        newObjectFacade.attributeValuesAsList("owner").addAll(owningGroup);            
		        newObjectFacade.getValue().keySet().remove("owningUser");
		        newObjectFacade.getValue().keySet().remove("owningGroup");
	            // Access levels
	            for(
	                Iterator<String> i = Arrays.asList("accessLevelBrowse", "accessLevelUpdate", "accessLevelDelete").iterator();
	                i.hasNext();
	            ) {
	                String mode = i.next();
	                if(
	                    (newObjectFacade.attributeValuesAsList(mode).size() != 1) ||
	                    (((Number)newObjectFacade.attributeValue(mode)).shortValue() == SecurityKeys.ACCESS_LEVEL_NA)
	                ) {
	                	newObjectFacade.attributeValuesAsList(mode).clear();
	                	newObjectFacade.attributeValuesAsList(mode).add(
	                        new Short(
	                            "accessLevelBrowse".equals(mode) ? 
	                            	SecurityKeys.ACCESS_LEVEL_DEEP : 
	                            	SecurityKeys.ACCESS_LEVEL_BASIC
	                        )
	                    );
	                }
	            }
	        }
	        if(super.create(ispec, input, output)) {
	        	this.addToObjectCache(input.getDelegate());
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }
	    
	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#find(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        SecurityContext.CachedPrincipal requestingPrincipal = securityContext.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path requestingUser = AccessControl_1.this.getUser(requestingPrincipal);
	        MappedRecord parent = this.getCachedObject(
	            header,
	            request.path()
	        );
	        Object_2Facade parentFacade;
	        try {
		        parentFacade = Object_2Facade.newInstance(parent);
	        }
	        catch (ResourceException e) {
	        	throw new ServiceException(e);
	        }
	        ModelElement_1_0 referencedType = AccessControl_1.this.getReferencedType(
	            request.path(),
	            request.attributeFilter()
	        );
	        if(
	        	AccessControl_1.this.isSecureObject(referencedType) && 
	        	AccessControl_1.this.isSecureObject(parent)
	        ) {
	        	boolean containsSharedAssociation = AccessControl_1.this.model.containsSharedAssociation(
	        		request.path()
	        	);
	        	if(containsSharedAssociation) {
	        		Number originalSize = input.getSize();
	        		Number originalPosition = input.getPosition();
	        		// In case of shared association read first object which is used 
	        		// to determine the composite parent of the result set. Apply the
	        		// browse filter of the parent to the query
	        		input.setSize(1);
	        		input.setPosition(0);
			        super.find(
			        	ispec, 
			        	input, 
			        	output
			        );
	        		if(!output.isEmpty()) {
			        	Object_2Facade objectParentFacade;
			        	try {
			        		objectParentFacade = Object_2Facade.newInstance(
				        		this.getCachedObject(
				        			header, 
				        			Object_2Facade.getPath(reply.getObject()).getParent()
				        		)
				        	);
			        	} catch(ResourceException e) {
			        		throw new ServiceException(e);
			        	}
			        	AccessControl_1.this.applyBrowseFilter(
			        		objectParentFacade, 
			        		request, 
			        		securityContext, 
			        		requestingPrincipal, 
			        		requestingUser
			        	);
	        		}
	        		input.setPosition(originalPosition);
	        		input.setSize(originalSize);
	        		output.clear();
	        	}
	        	// Apply the browse filter of the actual parent
	        	AccessControl_1.this.applyBrowseFilter(
	        		parentFacade, 
	        		request, 
	        		securityContext, 
	        		requestingPrincipal, 
	        		requestingUser
	        	);
		        super.find(
		        	ispec, 
		        	input, 
		        	output
		        );
	        }
	        else {
		        super.find(
		        	ispec, 
		        	input, 
		        	output
		        );
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#get(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        CachedPrincipal requestingPrincipal = securityContext.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path requestingUser = AccessControl_1.this.getUser(requestingPrincipal);
	        super.get(
	            ispec,
	            input,
	            output
	        );
	        MappedRecord parent = null;
	        if(request.path().size() >= 7) {
		        parent = this.getCachedObject(
	                header,
	                request.path()
	            );
		        Object_2Facade parentFacade;
	            try {
		            parentFacade = Object_2Facade.newInstance(parent);
	            } catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
		        ModelElement_1_0 referencedType = this.getModel().getTypes(request.path())[2];
		        if(AccessControl_1.this.isSecureObject(referencedType) && AccessControl_1.this.isSecureObject(parent)) {
		        	Object_2Facade objectFacade;
		        	try {
		        		objectFacade = Object_2Facade.newInstance(reply.getObject());
		        	} catch(ResourceException e) {
		        		throw new ServiceException(e);
		        	}
		        	boolean hasReadAccess = AccessControl_1.this.hasReadAccess(
		        		objectFacade, 
		        		parentFacade, 
		        		securityContext, 
		        		requestingPrincipal, 
		        		requestingUser
		        	);
		        	if(hasReadAccess) {
		            	AccessControl_1.this.completeReply(
		                    header,
		                    reply,
                            parent
		                );
		                return true;
		            }
		            else {
                        throw new ServiceException(
                            BasicException.Code.DEFAULT_DOMAIN,
                            BasicException.Code.AUTHORIZATION_FAILURE, 
                            "no permission to access requested object.",
                            new BasicException.Parameter("path", request.path()),
                            new BasicException.Parameter("param0", request.path().toXRI()),                                
                            new BasicException.Parameter("param1", requestingPrincipal.getIdentity().toXRI()), 
                            new BasicException.Parameter("param2", requestingUser.toXRI()) 
                        );
		            }
		        }
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#remove(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        CachedPrincipal requestingPrincipal = securityContext.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path requestingUser = AccessControl_1.this.getUser(requestingPrincipal);
	        MappedRecord existingObject = this.retrieveObject(            
	            request.path()
	        );
	        MappedRecord parent = null;
	        if(AccessControl_1.this.isSecureObject(existingObject)) {            
	            parent = this.getCachedObject(
	                header,
	                request.path()
	            );
	            Object_2Facade existingObjectFacade;
	            try {
		            existingObjectFacade = Object_2Facade.newInstance(existingObject);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            // assert that requesting user is allowed to update object
	            Set<String> memberships = new HashSet<String>();
	            if(existingObjectFacade.attributeValuesAsList("accessLevelDelete").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelDelete", existingObject);
	            }
	            else {
	                memberships = securityContext.getMemberships(
		                requestingPrincipal,
	                    requestingUser,
		                ((Number)existingObjectFacade.attributeValue("accessLevelDelete")).shortValue(),
		                AccessControl_1.this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	                memberships.retainAll(existingObjectFacade.attributeValuesAsList("owner"));
	                // no permission
		            if(memberships.isEmpty()) {
		                throw new ServiceException(
		                    OpenCrxException.DOMAIN,
		                    OpenCrxException.AUTHORIZATION_FAILURE_DELETE, 
		                    "No permission to delete requested object.",
	                        new BasicException.Parameter("object", request.path()),
	                        new BasicException.Parameter("param0", request.path().toXRI()),
	                        new BasicException.Parameter("param1", requestingPrincipal.getIdentity().toXRI()),                            
	                        new BasicException.Parameter("param2", requestingUser.toXRI())                            
		                );
		            }
	            }
	        }
	        objectCache.remove(
	            request.path()
	        );
	        super.delete(
	        	ispec, 
	        	input, 
	        	output
	        );
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#replace(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean put(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        CachedPrincipal requestingPrincipal = securityContext.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path requestingUser = AccessControl_1.this.getUser(requestingPrincipal);
	        MappedRecord existingObject = this.retrieveObject(
	            request.path()
	        );
	        if(AccessControl_1.this.isSecureObject(existingObject)) {
	            Object_2Facade existingObjectFacade;
	            try {
		            existingObjectFacade = Object_2Facade.newInstance(existingObject);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }        	
	            // assert that requesting user is allowed to update object
	            Set<String> memberships = new HashSet<String>();
	            if(existingObjectFacade.attributeValuesAsList("accessLevelUpdate").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelUpdate", existingObject);
	            }
	            else {
	                memberships = securityContext.getMemberships(
		                requestingPrincipal,
	                    requestingUser,
		                ((Number)existingObjectFacade.attributeValue("accessLevelUpdate")).shortValue(),
		                AccessControl_1.this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	                memberships.retainAll(existingObjectFacade.attributeValuesAsList("owner"));
	                // no permission
		            if(memberships.isEmpty()) {
		                throw new ServiceException(
		                    OpenCrxException.DOMAIN,
		                    OpenCrxException.AUTHORIZATION_FAILURE_UPDATE, 
		                    "No permission to update requested object.",
	                        new BasicException.Parameter("object", request.path()),
	                        new BasicException.Parameter("param0", request.path()),
	                        new BasicException.Parameter("param1", requestingPrincipal.getIdentity().toXRI()), 
	                        new BasicException.Parameter("param2", requestingUser.toXRI()) 
		                );
		            }
	            }
	            // derive attribute owner from owningUser and owningGroup
	            MappedRecord replacement = request.object();
	            Object_2Facade replacementFacade;
	            try {
		            replacementFacade = Object_2Facade.newInstance(replacement);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
		        // Derive newOwner from owningUser and owningGroup
		        List<Object> newOwner = new ArrayList<Object>();	        
		        // owning user
		        String owningUser = null;
		        if(!replacementFacade.attributeValuesAsList("owningUser").isEmpty()) {
		            owningUser = AccessControl_1.this.getQualifiedPrincipalName((Path)replacementFacade.attributeValue("owningUser"));
		        }
		        else {
		            // if no user found set owner to segment administrator
		            owningUser = existingObjectFacade.attributeValuesAsList("owner").isEmpty() ? 
		            	requestingUser == null ? 
		            		AccessControl_1.this.getQualifiedPrincipalName(replacementFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
		            			AccessControl_1.this.getQualifiedPrincipalName(requestingUser) : 
		            	(String)existingObjectFacade.attributeValue("owner");
		        }
		        newOwner.add(owningUser);
		        // owning group
		        Set<Object> owningGroup = new HashSet<Object>();
		        if(replacementFacade.getAttributeValues("owningGroup") != null) {
	                // replace owning group
		            for(Iterator<Object> i = replacementFacade.attributeValuesAsList("owningGroup").iterator(); i.hasNext(); ) {
	                    Path group = (Path)i.next();
	                    if(group != null) {
	    	                owningGroup.add(
	    	                	AccessControl_1.this.getQualifiedPrincipalName(group)
	    	                );
	                    }
		            }
		        }
		        else {
	                // keep existing owning group
	                if(existingObjectFacade.attributeValuesAsList("owner").size() > 1) {
	                    owningGroup.addAll(
	                    	existingObjectFacade.attributeValuesAsList("owner").subList(1, existingObjectFacade.attributeValuesAsList("owner").size())
	                    );
	                }
		        }
		        newOwner.addAll(owningGroup);
		        // Only replace owner if modified
		        if(!existingObjectFacade.attributeValuesAsList("owner").containsAll(newOwner) || !newOwner.containsAll(existingObjectFacade.attributeValuesAsList("owner"))) {
		        	replacementFacade.attributeValuesAsList("owner").clear();
		        	replacementFacade.attributeValuesAsList("owner").addAll(newOwner);
		        }
		        replacementFacade.getValue().keySet().remove("owningUser");
		        replacementFacade.getValue().keySet().remove("owningGroup");
	        }
	        objectCache.remove(request.path());
	        super.put(
	        	ispec, 
	        	input, 
	        	output
	        );
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            null
	        );
	        return true;
	    }
	    
	    //-----------------------------------------------------------------------
	    @Override
	    public boolean invoke(
	        RestInteractionSpec ispec, 
	        MessageRecord input, 
	        MessageRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	        SecurityContext securityContext = AccessControl_1.this.getSecurityContext(
	            header,
	            request
	        );
	        String operationName = request.path().get(
	            request.path().size() - 2
	        );
	        if("checkPermissions".equals(operationName)) {
	        	AccessControl_1.this.getSecurityContext(
	                header,
	                request
	            );
	            String principalName;
	            try {
		            principalName = (String)Object_2Facade.newInstance(request.object()).attributeValue("principalName");
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            CachedPrincipal principal = securityContext.getPrincipal(principalName);    
	            if(principal == null) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
	                    "Requested principal not found.",
	                    new BasicException.Parameter("principal", principalName),
	                    new BasicException.Parameter("param0", principalName),
	                    new BasicException.Parameter("param1", AccessControl_1.this.realmIdentity)
	                );            
	            }
	            Path objectIdentity = request.path().getParent().getParent();
	            Path user = AccessControl_1.this.getUser(principal);
	            MappedRecord parent = objectIdentity.size() <= 5 ?
	            	null:
	        		this.getCachedObject(
		                header,
		                objectIdentity
		            );            
	            Object_2Facade parentFacade;
	            try {
		            parentFacade = parent == null ? null : Object_2Facade.newInstance(parent);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            MappedRecord object = this.retrieveObject(            
	                objectIdentity
	            );
	            Object_2Facade facade;
	            try {
		            facade = Object_2Facade.newInstance(object);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            MappedRecord result = AccessControl_1.this.createResult(
	                request,
	                "org:opencrx:kernel:base:CheckPermissionsResult"
	            );
	            Object_2Facade replyFacade;
	            try {
		            replyFacade = Object_2Facade.newInstance(result);
	            }
	            catch (ResourceException e) {
	            	throw new ServiceException(e);
	            }
	            // Check read permission
	            Set<String> memberships = new HashSet<String>();
	            if((parentFacade != null) && parentFacade.attributeValuesAsList("accessLevelBrowse").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelBrowse", parent);
	            }
	            else {
	                memberships = securityContext.getMemberships(
	                    principal,
	                    user,
	                    parentFacade == null ? 
	                    	SecurityKeys.ACCESS_LEVEL_GLOBAL : 
	                    	((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
	                    AccessControl_1.this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	                replyFacade.attributeValuesAsList("membershipForRead").addAll(memberships);
	                memberships.retainAll(facade.attributeValuesAsList("owner"));
	            }
	            else {
	            	replyFacade.attributeValuesAsList("membershipForRead").add("*");                
	            }
	            replyFacade.attributeValuesAsList("hasReadPermission").add(
	                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
	            );
	            // Check delete permission
	            memberships = new HashSet<String>();
	            if(facade.attributeValuesAsList("accessLevelDelete").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelDelete", object);
	            }
	            else {
	                memberships = securityContext.getMemberships(
	                    principal,
	                    user,
	                    ((Number)facade.attributeValue("accessLevelDelete")).shortValue(),
	                    AccessControl_1.this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	            	replyFacade.attributeValuesAsList("membershipForDelete").addAll(memberships);
	                memberships.retainAll(facade.attributeValuesAsList("owner"));
	            }
	            else {
	            	replyFacade.attributeValuesAsList("membershipForDelete").add("*");
	            }
	            replyFacade.attributeValuesAsList("hasDeletePermission").add(
	                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
	            );
	            // Check update permission
	            memberships = new HashSet<String>();
	            if(facade.attributeValuesAsList("accessLevelUpdate").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelUpdate", object);
	            }
	            else {
	                memberships = securityContext.getMemberships(
	                    principal,
	                    user,
	                    ((Number)facade.attributeValue("accessLevelUpdate")).shortValue(),
	                    AccessControl_1.this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	            	replyFacade.attributeValuesAsList("membershipForUpdate").addAll(memberships);
	                memberships.retainAll(facade.attributeValuesAsList("owner"));
	            }
	            else {
	            	replyFacade.attributeValuesAsList("membershipForUpdate").add("*");                
	            }
	            replyFacade.attributeValuesAsList("hasUpdatePermission").add(
	                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
	            );
	            // Return result
	            output.setPath(replyFacade.getPath());
	            output.setBody(replyFacade.getValue());
	            return true;
	        }
	        super.invoke(
	            ispec,
	            input,
	            output
	        );
	        return true;
	    }
	    
    }

    //-------------------------------------------------------------------------
    protected void completeOwningUserAndGroup(
      ServiceHeader header,
      MappedRecord object
    ) throws ServiceException {
    	Object_2Facade facade;
        try {
	        facade = Object_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
    	facade.getValue().keySet().remove("owningUser");
    	facade.getValue().keySet().remove("owningGroup");
        if(!facade.attributeValuesAsList("owner").isEmpty()) {
            if((String)facade.attributeValue("owner") == null) {
            	SysLog.error("Values of attribute owner are corrupt. Element at index 0 (owning user) is missing. Fix the database", object);
            }
            else {
            	facade.attributeValuesAsList("owningUser").add(
	                this.getUserIdentity((String)facade.attributeValue("owner"))
	            );
            }
        }
        for(int i = 1; i < facade.attributeValuesAsList("owner").size(); i++) {
        	facade.attributeValuesAsList("owningGroup").add(
        		this.getGroupIdentity(facade.getPath(), (String)facade.attributeValuesAsList("owner").get(i))
            );            
        }       
    }

    //-------------------------------------------------------------------------
    protected void completeAccessGrantedByParent(
      ServiceHeader header,
      MappedRecord object,
      MappedRecord accessGrantedByParent
    ) throws ServiceException {
        if(accessGrantedByParent != null) {
            try {
            	Object_2Facade facade = Object_2Facade.newInstance(object);
	            facade.attributeValuesAsList("accessGrantedByParent").clear();
	            facade.attributeValuesAsList("accessGrantedByParent").add(
	                Object_2Facade.getPath(accessGrantedByParent)
	            );
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
        }        
    }
    
    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      MappedRecord object,
      MappedRecord accessGrantedByParent
    ) throws ServiceException {
        this.completeOwningUserAndGroup(
            header,
            object
        );
        this.completeAccessGrantedByParent(
            header,
            object,
            accessGrantedByParent
        );
    }
    
    //-------------------------------------------------------------------------
    protected DataproviderReply completeReply(
      ServiceHeader header,
      DataproviderReply reply,
      MappedRecord accessGrantedByParent
    ) throws ServiceException {
    	if(reply.getResult() != null) {
	      for(int i = 0; i < reply.getObjects().length; i++) {
	    	  MappedRecord object = reply.getObjects()[i];
	          this.completeObject(
	              header,
	              object,
	              accessGrantedByParent
	          );
	      }
    	}
    	return reply;
    }
        
    //-------------------------------------------------------------------------
    protected boolean isPrincipalGroup(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        return this.model.isSubtypeOf(
            objectClass,
            "org:opencrx:security:realm1:PrincipalGroup"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        if(objectClass == null) {
        	SysLog.error("Undefined object class", Object_2Facade.getPath(object));
            return true;
        }
        else {
            return this.model.isSubtypeOf(
                objectClass,
                "org:opencrx:kernel:base:SecureObject"
            );
        }
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
      ModelElement_1_0 type
    ) throws ServiceException {
        return this.model.isSubtypeOf(
            type,
            "org:opencrx:kernel:base:SecureObject"
        );
    }

    //-------------------------------------------------------------------------
    public PersistenceManager newDelegatingPersistenceManager(
    ) {
        if(!this.connectionFactories.isEmpty()) {
        	ConnectionFactoryAdapter connectionFactoryAdapter = (ConnectionFactoryAdapter)this.connectionFactories.get(0);
        	Map<String,Object> entityManagerConfiguration = new HashMap<String,Object>();
        	entityManagerConfiguration.put(
            	ConfigurableProperty.ConnectionFactory.qualifiedName(),
            	DataManagerFactory_1.getPersistenceManagerFactory(
        			Collections.singletonMap(
        				ConfigurableProperty.ConnectionFactory.qualifiedName(),
        				connectionFactoryAdapter
        			)            		
            	)
            );
            entityManagerConfiguration.put(
            	ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            	EntityManagerFactory_1.class.getName()
            );	            	
        	return JDOHelper.getPersistenceManagerFactory(
        		entityManagerConfiguration
        	).getPersistenceManager(
        		SecurityKeys.ROOT_PRINCIPAL,
        		null
        	);
        }    
        return null;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Returns a list of types as path patterns of object references which inherit
     * the security settings from the parent object. This option should be used
     * only for performance improvements and applied only to business objects
     * which define a self-contained security entity (e.g. contract, its positions, 
     * depot references and product configurations). Additional paths can be added 
     * by overriding <code>getInheritFromParentTypes</code>. The API exposes the 
     * granting parent by the reference <code>SecureObject.accessGrantedByParent</code> 
     * and is set by completeObject.
     */
    //-------------------------------------------------------------------------
    public List<Path> getInheritFromParentTypes(
    ) {
        return Arrays.asList(
            new Path[]{
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/configuration"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/depotReference")
            }
        );
    }
    
    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#activate(short, org.openmdx.compatibility.base.application.configuration.Configuration, org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0)
     */
    @SuppressWarnings("unchecked")
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
        this.model = Model_1Factory.getModel();
        // realmIdentity
        if(!configuration.values(ConfigurationKeys.REALM_IDENTITY).isEmpty()) {
            this.realmIdentity = new Path((String)configuration.values(ConfigurationKeys.REALM_IDENTITY).get(0));
        }
        else {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.INVALID_CONFIGURATION, 
                "A realm identity must be configured with option 'realmIdentity'"
            );
        }         
        // Get dataprovider connections
        this.connectionFactories = new ArrayList(
        	configuration.values(
        		SharedConfigurationEntries.DATAPROVIDER_CONNECTION_FACTORY
        	).values()
        );
        // useExtendedAccessLevelBasic
        this.useExtendedAccessLevelBasic = configuration.values("useExtendedAccessLevelBasic").isEmpty() ?
            false :
            ((Boolean)configuration.values("useExtendedAccessLevelBasic").get(0)).booleanValue();        
        // Init inheritFromParentTypes
        this.inheritFromParentTypes = this.getInheritFromParentTypes();
    }
    
    //-------------------------------------------------------------------------
    protected String getPrincipalName(
    	ServiceHeader header
    ) {
    	return header.getPrincipalChain().isEmpty() ? 
        	null : 
        	(String)header.getPrincipalChain().get(0);
    }
    
    //-------------------------------------------------------------------------
    /**
     * Set the current security context to the requesting principal, i.e.
     * this.requestingPrincipal, this.currentSecurityContext, this.requestingUser.
     */
    protected SecurityContext getSecurityContext(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        String principalName = this.getPrincipalName(header); 
        String realmName = SecurityKeys.ROOT_PRINCIPAL.equals(principalName) ? 
        	"Root" : 
        	request.path().get(4);
        if(this.securityContexts.get(realmName) == null) {
            this.securityContexts.put(
                realmName,
                new SecurityContext(
                    this, 
                    this.realmIdentity.getParent().getChild(realmName)
                )
            );
        }        
        SecurityContext securityContext = this.securityContexts.get(realmName);
        CachedPrincipal requestingPrincipal = securityContext.getPrincipal(principalName);        
        if(requestingPrincipal == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
                "Requested principal not found.",
                new BasicException.Parameter("principal", header.getPrincipalChain()),
                new BasicException.Parameter("param0", header.getPrincipalChain()),
                new BasicException.Parameter("param1", this.realmIdentity)
            );
        }
        SysLog.detail("requesting principal", requestingPrincipal);
        return securityContext;
    }
    
    //-------------------------------------------------------------------------
    protected ModelElement_1_0 getReferencedType(
        Path accessPath,
        FilterProperty[] filter
    ) throws ServiceException {
        // Extent access requires special treatment
        // get the set of referenceIds specified by filter property 'identity'
        boolean isExtent = false;        
        if(filter != null && accessPath.isLike(EXTENT_PATTERN)) {
            for(
                int i = 0;
                i < filter.length;
                i++
            ) {
                FilterProperty p = filter[i];
                if(SystemAttributes.OBJECT_IDENTITY.equals(p.name())) {
                    if(p.values().size() > 1) {
                        throw new ServiceException(
                            BasicException.Code.DEFAULT_DOMAIN,
                            BasicException.Code.NOT_SUPPORTED, 
                            "at most one value allowed for filter property 'identity'",
                            new BasicException.Parameter("filter", (Object[])filter)
                        );                        
                    }
                    isExtent = true;
                    accessPath = new Path(p.values().iterator().next().toString());
                }
            }
            if(!isExtent) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_SUPPORTED, 
                    "extent lookups require at least a filter value for property 'identity'",
                    new BasicException.Parameter("filter", (Object[])filter)
                );            
            }
        }
        return this.model.getTypes(accessPath)[2];        
    }
    
    //-------------------------------------------------------------------------
    protected MappedRecord createResult(
        DataproviderRequest request,
        String structName
    ) throws ServiceException {
    	try {
	    	MappedRecord result = Object_2Facade.newInstance(
	            request.path().getDescendant(
	                new String[]{ "reply", super.uidAsString()}
	            ),
	            structName
	        ).getDelegate();
	        return result;
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }

    //-----------------------------------------------------------------------
    protected static ConcurrentMap<Path,Object[]> getObjectCache(
    ) {
    	return objectCache;
    }
    
    //-----------------------------------------------------------------------
    protected final static Path EXTENT_PATTERN = 
        new Path("xri:@openmdx:**/provider/**/segment/**/extent");
    
    protected static final Path USER_HOME_PATH_PATTERN =
        new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*");

    protected List<Object> connectionFactories = null;    
    protected List<Path> inheritFromParentTypes;
    protected Path realmIdentity = null;
    protected Model_1_0 model = null;
    protected boolean useExtendedAccessLevelBasic = false;
    
    // Subject cache as map with identity as key and subject as value.
    private Map<String,SecurityContext> securityContexts = 
    	new ConcurrentHashMap<String,SecurityContext>();
    
    // Cached parents
    private static final long TTL_CACHED_PARENTS = 20000L;
    // Entry contains DataproviderObject and expiration date
    protected static final ConcurrentMap<Path,Object[]> objectCache = 
        new ConcurrentHashMap<Path,Object[]>();
    
}

//--- End of File -----------------------------------------------------------

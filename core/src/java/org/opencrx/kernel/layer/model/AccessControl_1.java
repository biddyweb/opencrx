/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: AccessControl_1.java,v 1.111 2009/10/15 17:46:35 wfro Exp $
 * Description: openCRX access control plugin
 * Revision:    $Revision: 1.111 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/10/15 17:46:35 $
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.security.realm1.jmi1.User;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.application.dataprovider.layer.model.Standard_1;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.application.rest.ejb.DataManager_2ProxyFactory;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.spi.EntityManagerFactory_1;
import org.openmdx.base.collection.SparseList;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.AttributeSpecifier;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.rest.spi.ObjectHolder_2Facade;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.kernel.persistence.cci.ConfigurableProperty;

/**
 * openCRX access control plugin. Implements the openCRX access control logic.
 */
public class AccessControl_1 
    extends Standard_1 {

    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
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
        // <= 1.7.1 compatibility. Principals with name 'admin'|'loader' must be
        // mapped to 'admin-<segment>'
        if(SecurityKeys.ADMIN_PRINCIPAL.equals(principalName) || SecurityKeys.LOADER_PRINCIPAL.equals(principalName)) {
            principalName = principalName + SecurityKeys.ID_SEPARATOR + segmentName;
        }
        // <= 1.7.1 compatibility. Qualified principal name must have the
        // format of a user principal
        if(!principalName.endsWith(SecurityKeys.USER_SUFFIX)) {
            principalName += "." + SecurityKeys.USER_SUFFIX;
        }
        Path principalIdentity = this.realmIdentity.getParent().getDescendant(
            new String[]{segmentName, "principal", principalName}             
        );
        return principalIdentity;
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
    
    //-------------------------------------------------------------------------
    protected MappedRecord retrieveObjectFromLocal(
        ServiceHeader header,
        Path identity
    ) throws ServiceException {
        try {
	        return super.get(
	            header,
	            new DataproviderRequest(
	                ObjectHolder_2Facade.newInstance(identity).getDelegate(),
	                DataproviderOperations.OBJECT_RETRIEVAL,
	                AttributeSelectors.ALL_ATTRIBUTES,
	                new AttributeSpecifier[]{}
	            )
	        ).getObject();
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
    }
    
    //-------------------------------------------------------------------------
    protected void completeOwningUserAndGroup(
      ServiceHeader header,
      MappedRecord object
    ) throws ServiceException {
    	ObjectHolder_2Facade facade;
        try {
	        facade = ObjectHolder_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
    	facade.getValue().keySet().remove("owningUser");
    	facade.getValue().keySet().remove("owningGroup");
        if(!facade.attributeValues("owner").isEmpty()) {
            if((String)facade.attributeValue("owner") == null) {
            	SysLog.error("Values of attribute owner are corrupt. Element at index 0 (owning user) is missing. Fix the database", object);
            }
            else {
            	facade.attributeValues("owningUser").add(
	                this.getUserIdentity(facade.getPath(), (String)facade.attributeValue("owner"))
	            );
            }
        }
        for(int i = 1; i < facade.attributeValues("owner").size(); i++) {
        	facade.attributeValues("owningGroup").add(
                this.getGroupIdentity(facade.getPath(), (String)facade.attributeValues("owner").get(i))
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
	            ObjectHolder_2Facade.newInstance(object).clearAttributeValues("accessGrantedByParent").add(
	                ObjectHolder_2Facade.getPath(accessGrantedByParent)
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
    private void addToParentsCache(
    	MappedRecord object
    ) throws ServiceException {
    	ObjectHolder_2Facade facade;
        try {
	        facade = ObjectHolder_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        if(facade.getObjectClass() != null) {
            try {
	            cachedParents.put(
	                facade.getPath(),
	                new Object[]{
	                    ObjectHolder_2Facade.cloneObject(object),
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
    protected DataproviderReply completeReply(
      ServiceHeader header,
      DataproviderReply reply,
      MappedRecord accessGrantedByParent
    ) throws ServiceException {
      for(int i = 0; i < reply.getObjects().length; i++) {
    	  MappedRecord object = reply.getObjects()[i];
          this.completeObject(
              header,
              object,
              accessGrantedByParent
          );
      }
      return reply;
    }
        
    //-------------------------------------------------------------------------
    protected boolean isPrincipalGroup(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = ObjectHolder_2Facade.getObjectClass(object);
        return this.model.isSubtypeOf(
            objectClass,
            "org:opencrx:security:realm1:PrincipalGroup"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = ObjectHolder_2Facade.getObjectClass(object);
        if(objectClass == null) {
        	SysLog.error("Undefined object class", ObjectHolder_2Facade.getPath(object));
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
    public PersistenceManager getPmAsRoot(
    ) throws ServiceException {
    	if(this.pmAsRoot == null) {
            if(!this.dataproviderConnectionFactories.isEmpty()) {
            	ConnectionFactory securityProviderConnectionFactory = (ConnectionFactory)this.dataproviderConnectionFactories.get(0);
            	Map<String,Object> dataManagerConfiguration = new HashMap<String,Object>();
            	dataManagerConfiguration.put(
            		ConfigurableProperty.ConnectionFactory.qualifiedName(),
            		securityProviderConnectionFactory
            	);
            	dataManagerConfiguration.put(
            		ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            		DataManager_2ProxyFactory.class.getName()
            	);
            	PersistenceManagerFactory dataManagerFactory = JDOHelper.getPersistenceManagerFactory(
            		dataManagerConfiguration
            	);
            	Map<String,Object> entityManagerConfiguration = new HashMap<String,Object>();
            	entityManagerConfiguration.put(
                	ConfigurableProperty.ConnectionFactory.qualifiedName(),
                	dataManagerFactory
                );
                entityManagerConfiguration.put(
                	ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
                	EntityManagerFactory_1.class.getName()
                );	            	
            	this.pmAsRoot = JDOHelper.getPersistenceManagerFactory(
            		entityManagerConfiguration
            	).getPersistenceManager(
            		SecurityKeys.ROOT_PRINCIPAL,
            		null
            	);
            }
    	}
    	return this.pmAsRoot;
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
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/configuration"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/depotReference")
            }
        );
    }
    
    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#activate(short, org.openmdx.compatibility.base.application.configuration.Configuration, org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0)
     */
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws ServiceException {
        super.activate(id, configuration, delegation);
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
        this.dataproviderConnectionFactories = configuration.values(
        	SharedConfigurationEntries.DATAPROVIDER_CONNECTION_FACTORY
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
    private SecurityContext assertSecurityContext(
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
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(principalName);        
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
    /**
     * Get the direct composite parent of the object with the given access path. 
     * If path matches a configured inheritFromParentTypes path then the composite
     * parent of the parent is returned, recursively. 
     */
    private MappedRecord getCachedParent(
        ServiceHeader header,
        Path path,
        boolean forceRefresh
    ) throws ServiceException {
        Path reference = path;
        if(reference.size() % 2 == 1) {
            reference = path.getParent();
        }
        // Determine the parent according to the access level inheritance configuration
        while(true) {
            boolean matched = false;
            for(
                Iterator<Path> i = this.inheritFromParentTypes.iterator();
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
        Object[] entry = cachedParents.get(parentPath);
        MappedRecord parent = null;
        if(forceRefresh || (entry == null)) {        
            parent = this.retrieveObjectFromLocal(
                header, 
                parentPath
            );
            this.addToParentsCache(parent);
        }
        else {
            parent = (MappedRecord)entry[0];
        }
        return parent;
    }
    
    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#prolog(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest[])
     */
    public void prolog(
        ServiceHeader header, 
        DataproviderRequest[] requests
    ) throws ServiceException {
        super.prolog(header, requests);
        // Remove cached parents if expired
        for(Iterator<Map.Entry<Path,Object[]>> i = cachedParents.entrySet().iterator(); i.hasNext(); ) {
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
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#prolog(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest[])
     */
    public void epilog(
        ServiceHeader header, 
        DataproviderRequest[] requests,
        DataproviderReply[] replies
    ) throws ServiceException {
        super.epilog(
            header, 
            requests, 
            replies
        );
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#create(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(this.getPrincipalName(header));
        User requestingUser = securityContext.getUser(requestingPrincipal);
        // Check permission. Must have update permission for parent
        MappedRecord parent = null;
        if(request.path().size() >= 7) {
	        parent = this.getCachedParent(
                header, 
                request.path(),
                true
            );
	        if(this.isSecureObject(parent)) {
	            Set<String> memberships = new HashSet<String>();
	            try {
	                if(ObjectHolder_2Facade.newInstance(parent).attributeValues("accessLevelUpdate").size() < 1) {
	                	SysLog.error("missing attribute value for accessLevelUpdate", parent);
	                }
	                else {
	                    memberships = securityContext.getMemberships(
	                        requestingPrincipal,
	                        requestingUser,
	                        ((Number)ObjectHolder_2Facade.newInstance(parent).attributeValue("accessLevelUpdate")).shortValue(),
	                        this.useExtendedAccessLevelBasic
	                    );
	                }
                }
                catch (ResourceException e) {
                	throw new ServiceException(e);
                }
	            if(memberships != null) {
	                try {
	                    memberships.retainAll(ObjectHolder_2Facade.newInstance(parent).attributeValues("owner"));
                    }
                    catch (ResourceException e) {
                    	throw new ServiceException(e);
                    }
		            if(memberships.isEmpty()) {
		                throw new ServiceException(
		                    OpenCrxException.DOMAIN,
		                    OpenCrxException.AUTHORIZATION_FAILURE_CREATE, 
		                    "No permission to create object.",
                            new BasicException.Parameter("object", request.path()),
                            new BasicException.Parameter("param0", request.path()),
                            new BasicException.Parameter("param1", requestingPrincipal)                                
		                );
		            }
	            }
	        }
        }
        // Create object
        MappedRecord newObject = request.object();
        // Set owner in case of secure objects
        if(this.isSecureObject(newObject)) {
	        ObjectHolder_2Facade parentFacade;
            try {
	            parentFacade = ObjectHolder_2Facade.newInstance(parent);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	
            ObjectHolder_2Facade newObjectFacade;
            try {
	            newObjectFacade = ObjectHolder_2Facade.newInstance(newObject);
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
                (parentFacade.attributeValues("owner").size() > 0)
            ) {
               owningUser = (String)parentFacade.attributeValue("owner");
            }   
            // owning user set on new object
            else if(newObjectFacade.attributeValues("owningUser").size() > 0) {
	            owningUser = this.getQualifiedPrincipalName(
                    (Path)newObjectFacade.attributeValue("owningUser")
                );
	        }
            // set requesting principal as default
	        else {
	            // if no user found set owner to segment administrator
	            owningUser = newObjectFacade.attributeValues("owner").isEmpty() ? 
	            	requestingUser == null ? 
	            		this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
	            		this.getQualifiedPrincipalName(requestingUser.refGetPath()) : 
	            	(String)newObjectFacade.attributeValue("owner");
	        }
            newObjectFacade.clearAttributeValues("owner").add(owningUser);

            // Owning group
	        Set<Object> owningGroup = new HashSet<Object>();
	        if(
	        	(newObjectFacade.getAttributeValues("owningGroup") != null) && 
	        	!newObjectFacade.getAttributeValues("owningGroup").isEmpty()
	        ) {
	            for(Iterator<Object> i = newObjectFacade.attributeValues("owningGroup").iterator(); i.hasNext(); ) {
	                owningGroup.add(
	                    this.getQualifiedPrincipalName((Path)i.next())
	                );
	            }
	        }
	        else {
	        	org.openmdx.security.realm1.jmi1.Principal userGroup = requestingPrincipal == null ? 
	        		null : 
	        		securityContext.getPrimaryGroup(requestingPrincipal);
                owningGroup = new HashSet<Object>();
                if(parent != null) {
                    List<Object> ownersParent = new ArrayList<Object>(parentFacade.attributeValues("owner"));
                    // Do not inherit group Users at segment-level
                    if(parentFacade.getPath().size() == 5) {
	                    ownersParent.remove(
	                        this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.USER_GROUP_USERS)
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
                    	this.getQualifiedPrincipalName(newObjectFacade.getPath(), "Unassigned") : 
                    	this.getQualifiedPrincipalName(userGroup.refGetPath())
	            );
	        }
	        newObjectFacade.attributeValues("owner").addAll(owningGroup);            
	        newObjectFacade.getValue().keySet().remove("owningUser");
	        newObjectFacade.getValue().keySet().remove("owningGroup");
            // accessLevels
            for(
                Iterator<String> i = Arrays.asList(new String[]{"accessLevelBrowse", "accessLevelUpdate", "accessLevelDelete"}).iterator();
                i.hasNext();
            ) {
                String mode = i.next();
                if(
                    (newObjectFacade.attributeValues(mode).size() != 1) ||
                    (((Number)newObjectFacade.attributeValue(mode)).shortValue() == SecurityKeys.ACCESS_LEVEL_NA)
                ) {
                	newObjectFacade.clearAttributeValues(mode).add(
                        new Short(
                            "accessLevelBrowse".equals(mode) ? 
                            	SecurityKeys.ACCESS_LEVEL_DEEP : 
                            	SecurityKeys.ACCESS_LEVEL_BASIC
                        )
                    );
                }
            }
        }
        return this.completeReply(
            header,
            super.create(header, request),
            parent
        );
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
    private MappedRecord createResult(
        DataproviderRequest request,
        String structName
    ) throws ServiceException {
    	try {
	    	MappedRecord result = ObjectHolder_2Facade.newInstance(
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

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#find(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(this.getPrincipalName(header));
        User requestingUser = securityContext.getUser(requestingPrincipal);
        MappedRecord parent = this.getCachedParent(
            header,
            request.path(),
            false
        );
        ObjectHolder_2Facade parentFacade;
        try {
	        parentFacade = ObjectHolder_2Facade.newInstance(parent);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        if(request.operation() != DataproviderOperations.ITERATION_CONTINUATION) {
            ModelElement_1_0 referencedType = this.getReferencedType(
                request.path(),
                request.attributeFilter()
            );
            if(this.isSecureObject(referencedType) && this.isSecureObject(parent)) {
                Set<String> memberships = new HashSet<String>();
                if(parentFacade.attributeValues("accessLevelBrowse").isEmpty()) {
                	SysLog.error("missing attribute value for accessLevelBrowse", parent);
                }
                else {
                    memberships = securityContext.getMemberships(
    	                requestingPrincipal,
                        requestingUser,
    	                ((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
    	                this.useExtendedAccessLevelBasic
                    );
                }
                // allowedPrincipals == null --> global access. Do not restrict to allowed subjects
                if(memberships != null) {
    	            request.addAttributeFilterProperty(
    	                new FilterProperty(
    	                    Quantors.THERE_EXISTS,
    	                    "owner",
    	                    FilterOperators.IS_IN,
    	                    memberships.toArray()
    	                )
    	            );
                }
            }
        }
        return this.completeReply(
            header,
            super.find(header, request),
            parent
        );
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#get(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply get(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(this.getPrincipalName(header));
        User requestingUser = securityContext.getUser(requestingPrincipal);
        DataproviderReply reply = super.get(
            header, 
            request
        );
        MappedRecord parent = null;
        if(request.path().size() >= 7) {
	        parent = this.getCachedParent(
                header,
                request.path(),
                false
            );
	        ObjectHolder_2Facade parentFacade;
            try {
	            parentFacade = ObjectHolder_2Facade.newInstance(parent);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
	        ModelElement_1_0 referencedType = this.model.getTypes(request.path())[2];
	        if(this.isSecureObject(referencedType) && this.isSecureObject(parent)) {
	            Set<String> memberships = new HashSet<String>();
	            if(parentFacade.attributeValues("accessLevelBrowse").isEmpty()) {
	            	SysLog.error("missing attribute value for accessLevelBrowse", parent);
	            }
	            else {
	                memberships = securityContext.getMemberships(
		                requestingPrincipal,
                        requestingUser,
		                ((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
		                this.useExtendedAccessLevelBasic
	                );
	            }
	            if(memberships != null) {
	                try {
	                    memberships.retainAll(ObjectHolder_2Facade.newInstance(reply.getObject()).attributeValues("owner"));
                    }
                    catch (ResourceException e) {
                    	throw new ServiceException(e);
                    }
		            if(!memberships.isEmpty()) {
		                return this.completeReply(
		                    header,
		                    reply,
                            parent
		                );
		            }
		            else  {
		                try {
	                        throw new ServiceException(
	                            BasicException.Code.DEFAULT_DOMAIN,
	                            BasicException.Code.AUTHORIZATION_FAILURE, 
	                            "no permission to access requested object.",
	                            new BasicException.Parameter("path", request.path()),
	                            new BasicException.Parameter("param0", request.path()),                                
	                            new BasicException.Parameter("param1", requestingPrincipal.refGetPath()), 
	                            new BasicException.Parameter("param2", requestingUser.refGetPath()) 
	                        );
                        }
                        catch (Exception e) {
                        	throw new ServiceException(e);
                        }              
		            }
	            }
	        }
        }
        return this.completeReply(
            header,
            reply,
            parent
        );
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#remove(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(this.getPrincipalName(header));
        User requestingUser = securityContext.getUser(requestingPrincipal);
        MappedRecord existingObject = this.retrieveObjectFromLocal(            
            header,
            request.path()
        );
        MappedRecord parent = null;
        if(this.isSecureObject(existingObject)) {            
            parent = this.getCachedParent(
                header,
                request.path(),
                true
            );
            ObjectHolder_2Facade existingObjectFacade;
            try {
	            existingObjectFacade = ObjectHolder_2Facade.newInstance(existingObject);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            // assert that requesting user is allowed to update object
            Set<String> memberships = new HashSet<String>();
            if(existingObjectFacade.attributeValues("accessLevelDelete").isEmpty()) {
            	SysLog.error("missing attribute value for accessLevelDelete", existingObject);
            }
            else {
                memberships = securityContext.getMemberships(
	                requestingPrincipal,
                    requestingUser,
	                ((Number)existingObjectFacade.attributeValue("accessLevelDelete")).shortValue(),
	                this.useExtendedAccessLevelBasic
                );
            }
            if(memberships != null) {
                memberships.retainAll(existingObjectFacade.attributeValues("owner"));
                // no permission
	            if(memberships.isEmpty()) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_DELETE, 
	                    "No permission to delete requested object.",
                        new BasicException.Parameter("object", request.path()),
                        new BasicException.Parameter("param0", request.path()),
                        new BasicException.Parameter("param1", requestingPrincipal.refGetPath()),                            
                        new BasicException.Parameter("param2", requestingUser.refGetPath())                            
	                );
	            }
            }
        }
        cachedParents.remove(
            request.path()
        );
        return this.completeReply(
            header,
            super.remove(header, request),
            parent
        );
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#replace(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply replace(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        org.openmdx.security.realm1.jmi1.Principal requestingPrincipal = securityContext.getPrincipal(this.getPrincipalName(header));
        User requestingUser = securityContext.getUser(requestingPrincipal);
        MappedRecord existingObject = this.retrieveObjectFromLocal(
            header,
            request.path()
        );
        if(this.isSecureObject(existingObject)) {
            ObjectHolder_2Facade existingObjectFacade;
            try {
	            existingObjectFacade = ObjectHolder_2Facade.newInstance(existingObject);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }        	
            // assert that requesting user is allowed to update object
            Set<String> memberships = new HashSet<String>();
            if(existingObjectFacade.attributeValues("accessLevelUpdate").isEmpty()) {
            	SysLog.error("missing attribute value for accessLevelUpdate", existingObject);
            }
            else {
                memberships = securityContext.getMemberships(
	                requestingPrincipal,
                    requestingUser,
	                ((Number)existingObjectFacade.attributeValue("accessLevelUpdate")).shortValue(),
	                this.useExtendedAccessLevelBasic
                );
            }
            if(memberships != null) {
                memberships.retainAll(existingObjectFacade.attributeValues("owner"));
                // no permission
	            if(memberships.isEmpty()) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_UPDATE, 
	                    "No permission to update requested object.",
                        new BasicException.Parameter("object", request.path()),
                        new BasicException.Parameter("param0", request.path()),
                        new BasicException.Parameter("param1", requestingPrincipal.refGetPath()), 
                        new BasicException.Parameter("param2", requestingUser.refGetPath()) 
	                );
	            }
            }
            // derive attribute owner from owningUser and owningGroup
            MappedRecord replacement = request.object();
            ObjectHolder_2Facade replacementFacade;
            try {
	            replacementFacade = ObjectHolder_2Facade.newInstance(replacement);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
	        // Derive newOwner from owningUser and owningGroup
	        List<Object> newOwner = new ArrayList<Object>();	        
	        // owning user
	        String owningUser = null;
	        if(!replacementFacade.attributeValues("owningUser").isEmpty()) {
	            owningUser = this.getQualifiedPrincipalName((Path)replacementFacade.attributeValue("owningUser"));
	        }
	        else {
	            // if no user found set owner to segment administrator
	            owningUser = existingObjectFacade.attributeValues("owner").isEmpty() ? 
	            	requestingUser == null ? 
	            		this.getQualifiedPrincipalName(replacementFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
	            		this.getQualifiedPrincipalName(requestingUser.refGetPath()) : 
	            	(String)existingObjectFacade.attributeValue("owner");
	        }
	        newOwner.add(owningUser);
	        // owning group
	        Set<Object> owningGroup = new HashSet<Object>();
	        if(replacementFacade.getAttributeValues("owningGroup") != null) {
                // replace owning group
	            for(Iterator<Object> i = replacementFacade.attributeValues("owningGroup").iterator(); i.hasNext(); ) {
                    Path group = (Path)i.next();
                    if(group != null) {
    	                owningGroup.add(
    	                    this.getQualifiedPrincipalName(group)
    	                );
                    }
	            }
	        }
	        else {
                // keep existing owning group
                if(existingObjectFacade.attributeValues("owner").size() > 1) {
                    owningGroup.addAll(
                    	existingObjectFacade.attributeValues("owner").subList(1, existingObjectFacade.attributeValues("owner").size())
                    );
                }
	        }
	        newOwner.addAll(owningGroup);
	        // Only replace owner if modified
	        if(!existingObjectFacade.attributeValues("owner").containsAll(newOwner) || !newOwner.containsAll(existingObjectFacade.attributeValues("owner"))) {
	        	replacementFacade.clearAttributeValues("owner").addAll(newOwner);
	        }
	        replacementFacade.getValue().keySet().remove("owningUser");
	        replacementFacade.getValue().keySet().remove("owningGroup");
        }
        cachedParents.remove(request.path());
        return this.completeReply(
            header,
            super.replace(header, request),
            null
        );
    }
    
    //-----------------------------------------------------------------------
    @Override
    public DataproviderReply operation(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        SecurityContext securityContext = this.assertSecurityContext(
            header,
            request
        );
        String operationName = request.path().get(
            request.path().size() - 2
        );
        if("checkPermissions".equals(operationName)) {
            this.assertSecurityContext(
                header,
                request
            );    
            String principalName;
            try {
	            principalName = (String)ObjectHolder_2Facade.newInstance(request.object()).attributeValue("principalName");
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            org.openmdx.security.realm1.jmi1.Principal principal = null;
            if(principalName != null) {
                try {
                    principal = securityContext.getPrincipal(principalName);    
                } catch(Exception e) {}
            }
            if(principal == null) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
                    "Requested principal not found.",
                    new BasicException.Parameter("principal", principalName),
                    new BasicException.Parameter("param0", principalName),
                    new BasicException.Parameter("param1", this.realmIdentity)
                );            
            }
            Path objectIdentity = request.path().getParent().getParent();
            User user = securityContext.getUser(principal);
            MappedRecord parent = objectIdentity.size() <= 5 ?
            	null:
        		this.getCachedParent(
	                header,
	                objectIdentity,
	                true
	            );            
            ObjectHolder_2Facade parentFacade;
            try {
	            parentFacade = parent == null ? null : ObjectHolder_2Facade.newInstance(parent);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            MappedRecord object = this.retrieveObjectFromLocal(            
                header,
                objectIdentity
            );
            ObjectHolder_2Facade facade;
            try {
	            facade = ObjectHolder_2Facade.newInstance(object);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            MappedRecord reply = this.createResult(
                request,
                "org:opencrx:kernel:base:CheckPermissionsResult"
            );
            ObjectHolder_2Facade replyFacade;
            try {
	            replyFacade = ObjectHolder_2Facade.newInstance(reply);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
            // Check read permission
            Set<String> memberships = new HashSet<String>();
            if((parentFacade != null) && parentFacade.attributeValues("accessLevelBrowse").isEmpty()) {
            	SysLog.error("missing attribute value for accessLevelBrowse", parent);
            }
            else {
                memberships = securityContext.getMemberships(
                    principal,
                    user,
                    parentFacade == null ? 
                    	SecurityKeys.ACCESS_LEVEL_GLOBAL : 
                    	((Number)parentFacade.attributeValue("accessLevelBrowse")).shortValue(),
                    this.useExtendedAccessLevelBasic
                );
            }
            if(memberships != null) {
                replyFacade.attributeValues("membershipForRead").addAll(memberships);
                memberships.retainAll(facade.attributeValues("owner"));
            }
            else {
            	replyFacade.attributeValues("membershipForRead").add("*");                
            }
            replyFacade.attributeValues("hasReadPermission").add(
                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
            );
            // Check delete permission
            memberships = new HashSet<String>();
            if(facade.attributeValues("accessLevelDelete").isEmpty()) {
            	SysLog.error("missing attribute value for accessLevelDelete", object);
            }
            else {
                memberships = securityContext.getMemberships(
                    principal,
                    user,
                    ((Number)facade.attributeValue("accessLevelDelete")).shortValue(),
                    this.useExtendedAccessLevelBasic
                );
            }
            if(memberships != null) {
            	replyFacade.attributeValues("membershipForDelete").addAll(memberships);
                memberships.retainAll(facade.attributeValues("owner"));
            }
            else {
            	replyFacade.attributeValues("membershipForDelete").add("*");
            }
            replyFacade.attributeValues("hasDeletePermission").add(
                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
            );
            // Check update permission
            memberships = new HashSet<String>();
            if(facade.attributeValues("accessLevelUpdate").isEmpty()) {
            	SysLog.error("missing attribute value for accessLevelUpdate", object);
            }
            else {
                memberships = securityContext.getMemberships(
                    principal,
                    user,
                    ((Number)facade.attributeValue("accessLevelUpdate")).shortValue(),
                    this.useExtendedAccessLevelBasic
                );
            }
            if(memberships != null) {
            	replyFacade.attributeValues("membershipForUpdate").addAll(memberships);
                memberships.retainAll(facade.attributeValues("owner"));
            }
            else {
            	replyFacade.attributeValues("membershipForUpdate").add("*");                
            }
            replyFacade.attributeValues("hasUpdatePermission").add(
                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
            );
            // Return result
            return new DataproviderReply(reply);
        }
        return super.operation(
            header, 
            request
        );
    }

    //-----------------------------------------------------------------------
    protected final static Path EXTENT_PATTERN = 
        new Path("xri:@openmdx:**/provider/**/segment/**/extent");
    
    private static final Path USER_HOME_PATH_PATTERN =
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*");

    private SparseList<Object> dataproviderConnectionFactories = null;    
    private List<Path> inheritFromParentTypes;
    private Path realmIdentity = null;
    private PersistenceManager pmAsRoot = null;
    private Model_1_0 model = null;
    private boolean useExtendedAccessLevelBasic = false;
    
    // Subject cache as map with identity as key and subject as value.
    private Map<String,SecurityContext> securityContexts = 
    	new ConcurrentHashMap<String,SecurityContext>();
    
    // Cached parents
    private static final long TTL_CACHED_PARENTS = 2000L;
    // Entry contains DataproviderObject and expiration date
    private static final Map<Path,Object[]> cachedParents = 
        new ConcurrentHashMap<Path,Object[]>();
    
}

//--- End of File -----------------------------------------------------------

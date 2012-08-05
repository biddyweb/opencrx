/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: AccessControl_1.java,v 1.66 2008/02/15 15:27:04 wfro Exp $
 * Description: openCRX access control plugin
 * Revision:    $Revision: 1.66 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/15 15:27:04 $
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
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.application.configuration.Configuration;
import org.openmdx.compatibility.base.collection.SparseList;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderReply;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.Dataprovider_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.QualityOfService;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.model.Standard_1;
import org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0;
import org.openmdx.compatibility.base.dataprovider.transport.adapter.Switch_1;
import org.openmdx.compatibility.base.exception.StackedException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.model1.accessor.basic.cci.ModelElement_1_0;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.openmdx.uses.org.apache.commons.collections.map.LRUMap;

/**
 * openCRX access control plugin. Implements the openCRX access control logic.
 *
 * This plugin is implemented as openMDX compatibility plugin. It will be 
 * migrated to a JMI plugin in one of the next versions.
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
            System.err.println("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName + "; path=" + accessPath.toXri());
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
            System.err.println("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName + "; path=" + accessPath.toXri());
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
    protected DataproviderObject_1_0 retrieveObjectFromLocal(
        ServiceHeader header,
        Path identity
    ) throws ServiceException {
        return super.get(
            header,
            new DataproviderRequest(
                new DataproviderObject(identity),
                DataproviderOperations.OBJECT_RETRIEVAL,
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}
            )
        ).getObject();
    }
    
    //-------------------------------------------------------------------------
    protected void completeOwningUserAndGroup(
      ServiceHeader header,
      DataproviderObject_1_0 object
    ) throws ServiceException {
        object.clearValues("owningUser");
        object.clearValues("owningGroup");
        if(object.values("owner").size() > 0) {
            if((String)object.values("owner").get(0) == null) {
                AppLog.error("Values of attribute owner are corrupt. Element at index 0 (owning user) is missing. Fix the database", object);
            }
            else {
	            object.values("owningUser").add(
	                this.getUserIdentity(object.path(), (String)object.values("owner").get(0))
	            );
            }
        }
        for(int i = 1; i < object.values("owner").size(); i++) {
            object.values("owningGroup").add(
                this.getGroupIdentity(object.path(), (String)object.values("owner").get(i))
            );            
        }       
    }

    //-------------------------------------------------------------------------
    protected void completeAccessGrantedByParent(
      ServiceHeader header,
      DataproviderObject_1_0 object,
      DataproviderObject_1_0 accessGrantedByParent
    ) throws ServiceException {
        if(accessGrantedByParent != null) {
            object.clearValues("accessGrantedByParent").add(
                accessGrantedByParent.path()
            );
        }        
    }
    
    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      DataproviderObject_1_0 object,
      DataproviderObject_1_0 accessGrantedByParent
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
        DataproviderObject_1_0 object
    ) {
        if(
            (object.getValues(SystemAttributes.OBJECT_CLASS) != null) &&
            (object.values(SystemAttributes.OBJECT_CLASS).size() > 0)
        ) {
            this.cachedParents.put(
                object.path(),
                new DataproviderObject(object)
            );
        }
        else {
            AppLog.error("Missing object class. Object not added to cache", object.path());
        }
    }
    
    //-------------------------------------------------------------------------
    protected DataproviderReply completeReply(
      ServiceHeader header,
      DataproviderReply reply,
      DataproviderObject_1_0 accessGrantedByParent
    ) throws ServiceException {
      for(int i = 0; i < reply.getObjects().length; i++) {
          DataproviderObject object = reply.getObjects()[i];
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
      DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.model.isSubtypeOf(
            objectClass,
            "org:opencrx:security:realm1:PrincipalGroup"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
      DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        if(objectClass == null) {
            AppLog.error("Undefined object class", object.path());
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
    public RequestCollection getRunAsRootDelegation(
    ) {
        return new RequestCollection(
            new ServiceHeader(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + "Root", null, false, new QualityOfService()),
            this.router == null
                ? this.getDelegation()
                : this.router
        );
    }
    
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
    public List getInheritFromParentTypes(
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
    ) throws Exception {
        super.activate(id, configuration, delegation);

        // model
        if(configuration.values(SharedConfigurationEntries.MODEL).size() > 0) {
            this.model = (Model_1_0)configuration.values(SharedConfigurationEntries.MODEL).get(0);
        }
        else {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.INVALID_CONFIGURATION, 
                null,
                "A model must be configured with options 'modelPackage' and 'packageImpl'"
            );
        } 
        
        // realmIdentity
        if(configuration.values(ConfigurationKeys.REALM_IDENTITY).size() > 0) {
            this.realmIdentity = new Path((String)configuration.values(ConfigurationKeys.REALM_IDENTITY).get(0));
        }
        else {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.INVALID_CONFIGURATION, 
                null,
                "A realm identity must be configured with option 'realmIdentity'"
            );
        } 
        
        // configure router
        SparseList dataproviderSource = configuration.values(
            SharedConfigurationEntries.DATAPROVIDER_CONNECTION
        );
        SparseList delegationPathSource = configuration.values(
            SharedConfigurationEntries.DELEGATION_PATH
        );
        if(delegationPathSource.isEmpty()){
            if(dataproviderSource.isEmpty()) {
              this.router = this.getDelegation();
            } 
            else {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.INVALID_CONFIGURATION,
                    new BasicException.Parameter[]{},
                    "The org:openmdx:compatibility:runtime1 model allowing to explore other dataproviders is deprecated." +
                    " Specifying " + SharedConfigurationEntries.DATAPROVIDER_CONNECTION +
                    " entries without their corresponding " + SharedConfigurationEntries.DELEGATION_PATH +
                    " is not supported"
                );
            }
        } 
        else {
            List dataproviderTarget = new ArrayList();
            List delegationPathTarget = new ArrayList();
            for(
                ListIterator dpi = delegationPathSource.populationIterator();
                dpi.hasNext();
            ) {
                int i = dpi.nextIndex();
                Object dp = dataproviderSource.get(i);
                if(dp == null) {
                    throw new ServiceException(
                        BasicException.Code.DEFAULT_DOMAIN,
                        BasicException.Code.INVALID_CONFIGURATION,
                        new BasicException.Parameter[]{
                            new BasicException.Parameter(SharedConfigurationEntries.DELEGATION_PATH, delegationPathSource),
                            new BasicException.Parameter("index", i),
                            new BasicException.Parameter(SharedConfigurationEntries.DATAPROVIDER_CONNECTION, dataproviderSource)
                        },
                        "The delegation path at the given index has no corresponding dataprovider counterpart"
                    );
                }
                delegationPathTarget.add(
                    new Path((String)dpi.next())
                );
                dataproviderTarget.add(dp);
            }
            this.router = new Switch_1(
                (Dataprovider_1_0[]) dataproviderTarget.toArray(
                    new Dataprovider_1_0[dataproviderTarget.size()]
                ),
                Path.toPathArray(delegationPathTarget),
                this.getDelegation()
            );
        }
        // Init inheritFromParentTypes
        this.inheritFromParentTypes = this.getInheritFromParentTypes();
    }
    
    //-------------------------------------------------------------------------
    /**
     * Set the current security context to the requesting principal, i.e.
     * this.requestingPrincipal, this.currentSecurityContext, this.requestingUser.
     */
    private void assertSecurityContext(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        String principalName = header.getPrincipalChain().size() == 0
            ? null
            : (String)header.getPrincipalChain().get(0);    
        String realmName = SecurityKeys.ROOT_PRINCIPAL.equals(principalName)
            ? "Root"
            : request.path().get(4);
        if(this.securityContexts.get(realmName) == null) {
            this.securityContexts.put(
                realmName,
                new SecurityContext(
                    this, 
                    this.realmIdentity.getParent().getChild(realmName)
                )
            );
        }
        this.currentSecurityContext = (SecurityContext)this.securityContexts.get(realmName);
        this.requestingPrincipal = this.currentSecurityContext.getPrincipal(principalName);        
        if(this.requestingPrincipal == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
                new BasicException.Parameter[]{
                    new BasicException.Parameter("principal", header.getPrincipalChain()),
                    new BasicException.Parameter("param0", header.getPrincipalChain()),
                    new BasicException.Parameter("param1", this.realmIdentity)
                },
                "Requested principal not found."
            );            
        }
        AppLog.detail("requesting principal", this.requestingPrincipal.path());
        this.requestingUser = this.currentSecurityContext.getGroup(this.requestingPrincipal);
    }
    
    //-------------------------------------------------------------------------
    /**
     * Get the direct composite parent of the object with the given access path. 
     * If path matches a configured inheritFromParentTypes path then the composite
     * parent of the parent is returned, recursively. 
     */
    private DataproviderObject_1_0 getCachedParent(
        ServiceHeader header,
        Path path
    ) throws ServiceException {
        Path reference = path;
        if(reference.size() % 2 == 1) {
            reference = path.getParent();
        }
        // Determine the parent according to the access level inheritance configuration
        while(true) {
            boolean matched = false;
            for(
                Iterator i = this.inheritFromParentTypes.iterator();
                i.hasNext();
            ) {
                Path referencePattern = (Path)i.next();
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
        DataproviderObject_1_0 parent = (DataproviderObject_1_0)this.cachedParents.get(parentPath);
        if(parent == null) {        
            parent = this.retrieveObjectFromLocal(
                header, 
                parentPath
            );
            this.addToParentsCache(parent);
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
        this.delegation = new RequestCollection(
            header,
            this.router == null
                ? this.getDelegation()
                : this.router
          );        
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
        this.requestingPrincipal = null;
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#create(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {

        this.assertSecurityContext(
            header,
            request
        );

        // Check permission. Must have update permission for parent
        DataproviderObject_1_0 parent = null;
        if(request.path().size() >= 7) {
	        parent = this.getCachedParent(
                header, 
                request.path()
            );
	        if(this.isSecureObject(parent)) {
	            Set<String> memberships = new HashSet<String>();
	            if(parent.values("accessLevelUpdate").size() < 1) {
	                AppLog.error("missing attribute value for accessLevelUpdate", parent);
	            }
	            else {
	                memberships = this.currentSecurityContext.getMemberships(
		                this.requestingPrincipal,
                        this.requestingUser,
		                ((Number)parent.values("accessLevelUpdate").get(0)).shortValue()
	                );
	            }
	            if(memberships != null) {
	                memberships.retainAll(parent.values("owner"));
		            if(memberships.size() == 0) {
		                throw new ServiceException(
		                    OpenCrxException.DOMAIN,
		                    OpenCrxException.AUTHORIZATION_FAILURE_CREATE, 
		                    new BasicException.Parameter[]{
		                        new BasicException.Parameter("object", request.path()),
		                        new BasicException.Parameter("param0", request.path()),
                                new BasicException.Parameter("param1", this.requestingPrincipal.path())                                
		                    },
		                    "No permission to create object."
		                );
		            }
	            }
	        }
        }

        // Create object
        DataproviderObject newObject = request.object();
        
        // Set owner in case of secure objects
        if(this.isSecureObject(newObject)) {
            
            // Owning user
	        String owningUser = null;
            
            // If new object is composite to user home set owning user to owning user of user home
            if(
                (newObject.path().size() > USER_HOME_PATH_PATTERN.size()) &&
                newObject.path().getPrefix(USER_HOME_PATH_PATTERN.size()).isLike(USER_HOME_PATH_PATTERN) &&
                (parent.values("owner").size() > 0)
            ) {
               owningUser = (String)parent.values("owner").get(0);
            }   
            // owning user set on new object
            else if(newObject.values("owningUser").size() > 0) {
	            owningUser = this.getQualifiedPrincipalName(
                    (Path)newObject.values("owningUser").get(0)
                );
	        }
            // set requesting principal as default
	        else {
	            // if no user found set owner to segment administrator
	            owningUser = newObject.values("owner").isEmpty()
	                ? this.requestingUser == null 
	                  ? this.getQualifiedPrincipalName(newObject.path(), SecurityKeys.ADMIN_PRINCIPAL) 
	                  : this.getQualifiedPrincipalName(this.requestingUser.path())
	                : (String)newObject.values("owner").get(0);
	        }
            newObject.clearValues("owner").add(owningUser);

            // Owning group
	        Set owningGroup = new HashSet();
	        if(newObject.getValues("owningGroup") != null) {
	            for(Iterator i = newObject.values("owningGroup").iterator(); i.hasNext(); ) {
	                owningGroup.add(
	                    this.getQualifiedPrincipalName((Path)i.next())
	                );
	            }
	        }
	        else {
	            DataproviderObject_1_0 userGroup = this.requestingPrincipal == null
	                ? null
	                : this.currentSecurityContext.getPrimaryGroup(this.requestingPrincipal);
                owningGroup = new HashSet();
                if(parent != null) {
                    List ownersParent = new ArrayList(parent.values("owner"));
                    ownersParent.remove(
                        this.getQualifiedPrincipalName(newObject.path(), SecurityKeys.USER_GROUP_USERS)
                    );
                    if(!ownersParent.isEmpty()) {
                        owningGroup.addAll(
                            ownersParent.subList(1, ownersParent.size())
                        );
                    }
                }
	            owningGroup.add(
                    userGroup == null 
                         ? this.getQualifiedPrincipalName(newObject.path(), "Unassigned") 
                         : this.getQualifiedPrincipalName(userGroup.path())
	            );
	        }
            newObject.values("owner").addAll(owningGroup);
            
            newObject.clearValues("owningUser");
            newObject.clearValues("owningGroup");
            // accessLevels
            for(
                Iterator i = Arrays.asList(new String[]{"accessLevelBrowse", "accessLevelUpdate", "accessLevelDelete"}).iterator();
                i.hasNext();
            ) {
                String mode = (String)i.next();
                if(
                    (newObject.values(mode).size() != 1) ||
                    (((Number)newObject.values(mode).get(0)).shortValue() == SecurityKeys.ACCESS_LEVEL_NA)
                ) {
                    newObject.clearValues(mode).add(
                        new Short(
                            "accessLevelBrowse".equals(mode)
                                ? SecurityKeys.ACCESS_LEVEL_DEEP
                                : SecurityKeys.ACCESS_LEVEL_BASIC
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
                            StackedException.DEFAULT_DOMAIN,
                            StackedException.NOT_SUPPORTED, 
                            new BasicException.Parameter[]{
                                new BasicException.Parameter("filter", filter)
                            },
                            "at most one value allowed for filter property 'identity'"
                        );                        
                    }
                    isExtent = true;
                    accessPath = new Path(p.values().iterator().next().toString());
                }
            }
            if(!isExtent) {
                throw new ServiceException(
                    StackedException.DEFAULT_DOMAIN,
                    StackedException.NOT_SUPPORTED, 
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("filter", filter)
                    },
                    "extent lookups require at least a filter value for property 'identity'"
                );            
            }
        }
        return this.model.getTypes(accessPath)[2];        
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
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#find(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
     */
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.assertSecurityContext(
            header,
            request
        );
        DataproviderObject_1_0 parent = this.getCachedParent(
            header,
            request.path()
        );
        if(request.operation() != DataproviderOperations.ITERATION_CONTINUATION) {
            ModelElement_1_0 referencedType = this.getReferencedType(
                request.path(),
                request.attributeFilter()
            );
            if(this.isSecureObject(referencedType) && this.isSecureObject(parent)) {
                Set<String> memberships = new HashSet<String>();
                if(parent.values("accessLevelBrowse").isEmpty()) {
                    AppLog.error("missing attribute value for accessLevelBrowse", parent);
                }
                else {
                    memberships = this.currentSecurityContext.getMemberships(
    	                this.requestingPrincipal,
                        this.requestingUser,
    	                ((Number)parent.values("accessLevelBrowse").get(0)).shortValue()
                    );
                }
                // allowedPrincipals == null --> global access. Do not restrict to allowed subjects
                if(memberships != null) {
    	            request.addAttributeFilterProperty(
    	                new FilterProperty(
    	                    Quantors.THERE_EXISTS,
    	                    "owner",
    	                    FilterOperators.IS_IN,
    	                    memberships.toArray(new String[memberships.size()])
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
        this.assertSecurityContext(
            header,
            request
        );
        DataproviderReply reply = super.get(
            header, 
            request
        );
        DataproviderObject_1_0 parent = null;
        if(request.path().size() >= 7) {
	        parent = this.getCachedParent(
                header,
                request.path()
            );
	        ModelElement_1_0 referencedType = this.model.getTypes(request.path())[2];
	        if(this.isSecureObject(referencedType) && this.isSecureObject(parent)) {
	            Set<String> memberships = new HashSet<String>();
	            if(parent.values("accessLevelBrowse").isEmpty()) {
	                AppLog.error("missing attribute value for accessLevelBrowse", parent);
	            }
	            else {
	                memberships = this.currentSecurityContext.getMemberships(
		                this.requestingPrincipal,
                        this.requestingUser,
		                ((Number)parent.values("accessLevelBrowse").get(0)).shortValue()
	                );
	            }
	            if(memberships != null) {
	                memberships.retainAll(reply.getObject().values("owner"));
		            if(!memberships.isEmpty()) {
		                return this.completeReply(
		                    header,
		                    reply,
                            parent
		                );
		            }
		            else  {
		                throw new ServiceException(
		                    BasicException.Code.DEFAULT_DOMAIN,
		                    BasicException.Code.AUTHORIZATION_FAILURE, 
		                    new BasicException.Parameter[]{
		                        new BasicException.Parameter("path", request.path()),
                                new BasicException.Parameter("param0", request.path()),                                
                                new BasicException.Parameter("param1", this.requestingPrincipal.path()) 
		                    },
		                    "no permission to access requested object."
		                );              
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
        
        this.assertSecurityContext(
            header,
            request
        );     
        DataproviderObject_1_0 existingObject = this.retrieveObjectFromLocal(            
            header,
            request.path()
        );
        DataproviderObject_1_0 parent = null;
        if(this.isSecureObject(existingObject)) {            
            parent = this.getCachedParent(
                header,
                request.path()
            );            
            // assert that requesting user is allowed to update object
            Set<String> memberships = new HashSet<String>();
            if(existingObject.values("accessLevelDelete").isEmpty()) {
                AppLog.error("missing attribute value for accessLevelDelete", existingObject);
            }
            else {
                memberships = this.currentSecurityContext.getMemberships(
	                this.requestingPrincipal,
                    this.requestingUser,
	                ((Number)existingObject.values("accessLevelDelete").get(0)).shortValue()
                );
            }
            if(memberships != null) {
                memberships.retainAll(existingObject.values("owner"));
                // no permission
	            if(memberships.isEmpty()) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_DELETE, 
	                    new BasicException.Parameter[]{
	                        new BasicException.Parameter("object", request.path()),
	                        new BasicException.Parameter("param0", request.path()),
                            new BasicException.Parameter("param1", this.requestingPrincipal.path())                            
	                    },
	                    "No permission to delete requested object."
	                );
	            }
            }
        }
        this.cachedParents.remove(
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
        this.assertSecurityContext(
            header,
            request
        );        
        DataproviderObject_1_0 existingObject = this.retrieveObjectFromLocal(
            header,
            request.path()
        );
        if(this.isSecureObject(existingObject)) {
            // assert that requesting user is allowed to update object
            Set<String> memberships = new HashSet<String>();
            if(existingObject.values("accessLevelUpdate").isEmpty()) {
                AppLog.error("missing attribute value for accessLevelUpdate", existingObject);
            }
            else {
                memberships = this.currentSecurityContext.getMemberships(
	                this.requestingPrincipal,
                    this.requestingUser,
	                ((Number)existingObject.values("accessLevelUpdate").get(0)).shortValue()
                );
            }
            if(memberships != null) {
                memberships.retainAll(existingObject.values("owner"));
                // no permission
	            if(memberships.isEmpty()) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_UPDATE, 
	                    new BasicException.Parameter[]{
	                        new BasicException.Parameter("object", request.path()),
	                        new BasicException.Parameter("param0", request.path()),
                            new BasicException.Parameter("param1", this.requestingPrincipal.path()) 
	                    },
	                    "No permission to update requested object."
	                );
	            }
            }
            // derive attribute owner from owningUser and owningGroup
	        DataproviderObject replacement = request.object();
	        
	        // Sets the attribute owner. owner[0] is set to the name of the owning user,
	        // owner[i] is set to the name of the owning group i-1.
	        replacement.clearValues("owner");
	        
	        // set 'owner' in case of secure objects
	        // owning user
	        String owningUser = null;
	        if(replacement.values("owningUser").size() > 0) {
	            owningUser = this.getQualifiedPrincipalName((Path)replacement.values("owningUser").get(0));
	        }
	        else {
	            // if no user found set owner to segment administrator
	            owningUser = existingObject.values("owner").size() == 0
	                ? this.requestingUser == null ? this.getQualifiedPrincipalName(replacement.path(), SecurityKeys.ADMIN_PRINCIPAL) : this.getQualifiedPrincipalName(this.requestingUser.path())
	                : (String)existingObject.values("owner").get(0);
	        }
	        replacement.values("owner").add(owningUser);
	        
	        // owning group
	        Set owningGroup = new HashSet();
	        if(replacement.getValues("owningGroup") != null) {
                // replace owning group
	            for(Iterator i = replacement.values("owningGroup").iterator(); i.hasNext(); ) {
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
                if(existingObject.values("owner").size() > 1) {
                    owningGroup.addAll(
                        existingObject.values("owner").subList(1, existingObject.values("owner").size())
                    );
                }
	        }
	        replacement.values("owner").addAll(owningGroup);
	        replacement.clearValues("owningUser");
	        replacement.clearValues("owningGroup");
        }
        
        this.cachedParents.remove(request.path());
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
        String operationName = request.path().get(
            request.path().size() - 2
        );
        if("checkPermissions".equals(operationName)) {
            this.assertSecurityContext(
                header,
                request
            );    
            String principalName = (String)request.object().values("principalName").get(0);
            DataproviderObject_1_0 principal = null;
            if(principalName != null) {
                try {
                    principal = this.currentSecurityContext.getPrincipal(principalName);    
                } catch(Exception e) {}
            }
            if(principal == null) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("principal", principalName),
                        new BasicException.Parameter("param0", principalName),
                        new BasicException.Parameter("param1", this.realmIdentity)
                    },
                    "Requested principal not found."
                );            
            }
            Path objectIdentity = request.path().getParent().getParent();
            DataproviderObject_1_0 user = this.currentSecurityContext.getGroup(principal);
            DataproviderObject_1_0 parent = this.getCachedParent(
                header,
                objectIdentity
            );            
            DataproviderObject_1_0 object = this.retrieveObjectFromLocal(            
                header,
                objectIdentity
            );
            DataproviderObject reply = this.createResult(
                    request,
                    "org:opencrx:kernel:base:CheckPermissionsResult"
                );
            // Check read permission
            Set<String> memberships = new HashSet<String>();
            if(parent.values("accessLevelBrowse").isEmpty()) {
                AppLog.error("missing attribute value for accessLevelBrowse", parent);
            }
            else {
                memberships = this.currentSecurityContext.getMemberships(
                    principal,
                    user,
                    ((Number)parent.values("accessLevelBrowse").get(0)).shortValue()
                );
            }
            if(memberships != null) {
                reply.values("membershipForRead").addAll(memberships);
                memberships.retainAll(object.values("owner"));
            }
            else {
                reply.values("membershipForRead").add("*");                
            }
            reply.values("hasReadPermission").add(
                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
            );
            // Check delete permission
            memberships = new HashSet<String>();
            if(object.values("accessLevelDelete").isEmpty()) {
                AppLog.error("missing attribute value for accessLevelDelete", object);
            }
            else {
                memberships = this.currentSecurityContext.getMemberships(
                    principal,
                    user,
                    ((Number)object.values("accessLevelDelete").get(0)).shortValue()
                );
            }
            if(memberships != null) {
                reply.values("membershipForDelete").addAll(memberships);
                memberships.retainAll(object.values("owner"));
            }
            else {
                reply.values("membershipForDelete").add("*");
            }
            reply.values("hasDeletePermission").add(
                Boolean.valueOf((memberships == null) || !memberships.isEmpty())
            );
            // Check update permission
            memberships = new HashSet<String>();
            if(object.values("accessLevelUpdate").isEmpty()) {
                AppLog.error("missing attribute value for accessLevelUpdate", object);
            }
            else {
                memberships = this.currentSecurityContext.getMemberships(
                    principal,
                    user,
                    ((Number)object.values("accessLevelUpdate").get(0)).shortValue()
                );
            }
            if(memberships != null) {
                reply.values("membershipForUpdate").addAll(memberships);
                memberships.retainAll(object.values("owner"));
            }
            else {
                reply.values("membershipForUpdate").add("*");                
            }
            reply.values("hasUpdatePermission").add(
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
        
    private List inheritFromParentTypes;
    private Path realmIdentity = null;    
    protected RequestCollection delegation = null;
    private Dataprovider_1_0 router = null;
    private Model_1_0 model = null;

    // Subject cache as map with identity as key and subject as value.
    private Map securityContexts = new HashMap();
    
    // Current security context
    private SecurityContext currentSecurityContext = null;
    // Requesting principal
    private DataproviderObject_1_0 requestingPrincipal = null;
    // Group principal of requesting user
    private DataproviderObject_1_0 requestingUser = null;
    
    // Cached parents (cleared on epilog)
    private static final int MAX_CACHED_PARENTS = 1000;
    private Map cachedParents = new LRUMap(MAX_CACHED_PARENTS);
    
}

//--- End of File -----------------------------------------------------------

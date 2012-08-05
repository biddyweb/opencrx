/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSecurity_1.java,v 1.43 2009/10/23 11:13:32 wfro Exp $
 * Description: OpenCrxSecurity_1
 * Revision:    $Revision: 1.43 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/10/23 11:13:32 $
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
package org.opencrx.security.layer.application;

import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.AttributeSpecifier;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.rest.spi.ObjectHolder_2Facade;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;

/**
 * This plugin implements the models org:openmdx:security:realm1,
 * org:openmdx:security1:authorization1, org:openmdx:security:authentication1.
 * 
 * It must be able to manage the following classes:
 * - realm1: Group, Permission, Principal, Policy, Privilege, Realm, Role
 * - authorization1: Policy, Privilege
 * - authentication1: Password
 * 
 * This plugin delegates all object retrievals, updates and creations to the
 * persistence plugin which is typically the database plugin 
 * (e.g. super.get(header, request), super.replace(header, request)). An 
 * LDAP implementation of this plugin must delegate requests to an LDAP
 * service.
 * 
 */
public class OpenCrxSecurity_1
  extends org.openmdx.application.dataprovider.layer.application.Standard_1 {

    //-------------------------------------------------------------------------
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
      this.model = Model_1Factory.getModel();
    } 

    //-------------------------------------------------------------------------
    private String getPrincipalName(
        ServiceHeader header
    ) throws ServiceException {
        if(header.getPrincipalChain().size() == 0) {
            return null;
        }
        return header.getPrincipalChain().get(0);
    }
    
    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      Set<String> fetchSet,
      MappedRecord object
    ) throws ServiceException {
    }
    
    //-------------------------------------------------------------------------
    private DataproviderReply completeReply(
      ServiceHeader header,
      Set<String> fetchSet,
      DataproviderReply reply
    ) throws ServiceException {
      for(int i = 0; i < reply.getObjects().length; i++) {
          this.completeObject(
              header,
              fetchSet,
              reply.getObjects()[i]
          );
      }
      return reply;
    }
    
    //-------------------------------------------------------------------------
    private void setQualifier(
    	MappedRecord obj
    ) throws ServiceException {
    	try {
	    	ObjectHolder_2Facade objFacade = ObjectHolder_2Facade.newInstance(obj);
	        if(
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Principal") ||
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Realm") ||
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Permission") ||
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Role") ||
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Policy")
	        ) {
	        	objFacade.clearAttributeValues("name").add(objFacade.getPath().getBase());            
	        }
	        else if(
	            this.model.objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Credential")
	        ) {
	        	objFacade.clearAttributeValues("id").add(objFacade.getPath().getBase());            
	        }
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-------------------------------------------------------------------------
    public MappedRecord retrieveObject(
        Path identity
    ) throws ServiceException {
        return this.delegation.addGetRequest(
            identity,
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{}
        );        
    }

    //-------------------------------------------------------------------------
    private MappedRecord createResult(
      DataproviderRequest request,
      String structName
    ) throws ServiceException {
    	try {
	    	MappedRecord result = ObjectHolder_2Facade.newInstance(
		        request.path().getDescendant(
		          new String[]{ "reply", UUIDs.getGenerator().next().toString()}
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
    public void prolog(
      ServiceHeader header,
      DataproviderRequest[] requests
    ) throws ServiceException {
      super.prolog(
          header,
          requests
      );
      this.delegation = new RequestCollection(
          header,
          this.getDelegation()
      );
    }

    //-------------------------------------------------------------------------
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
    private void setAttributes(
      ServiceHeader header,
      MappedRecord obj,
      MappedRecord oldValues
    ) throws ServiceException {
      this.setQualifier(
        obj 
      );
    }

    //-------------------------------------------------------------------------
    private void changePassword(
    	MappedRecord passwordCredential,
    	MappedRecord changePasswordParams
    ) throws ServiceException {
    	try {
	    	ObjectHolder_2Facade changePasswordParamsFacade = ObjectHolder_2Facade.newInstance(changePasswordParams);
	    	ObjectHolder_2Facade passwordCredentialFacade = ObjectHolder_2Facade.newInstance(passwordCredential);
	        String oldPassword = (changePasswordParamsFacade.getAttributeValues("oldPassword") != null) && !changePasswordParamsFacade.attributeValues("oldPassword").isEmpty() ? 
	        	Base64.encode((byte[])changePasswordParamsFacade.attributeValue("oldPassword")) : 
	        	null;
	        if((oldPassword != null) && !oldPassword.equals(passwordCredentialFacade.attributeValue("password"))) {
	            throw new ServiceException(
	            	OpenCrxException.DOMAIN,
	                BasicException.Code.ASSERTION_FAILURE, 
	                "old password verification mismatch",
	                new BasicException.Parameter("credential", passwordCredential)
	            );
	        }
	        MappedRecord changedPasswordCredential;
            try {
	            changedPasswordCredential = ObjectHolder_2Facade.cloneObject(passwordCredential);
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
	        ObjectHolder_2Facade.newInstance(changedPasswordCredential).clearAttributeValues("password").add(
	            Base64.encode((byte[])changePasswordParamsFacade.attributeValue("password"))
	        );
	        this.delegation.addReplaceRequest(
	            changedPasswordCredential
	        );
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-------------------------------------------------------------------------
    protected void checkPermission(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        
        short operation = request.operation();
        
        // read allowed for everybody
        if(
            operation == DataproviderOperations.OBJECT_RETRIEVAL ||
            operation == DataproviderOperations.ITERATION_START ||
            operation == DataproviderOperations.ITERATION_CONTINUATION
        ) {
            return;
        }
        // 1) All other operations only allowed for admin principals
        // 2) Principal is allowed to update its principal objects
        String principalName = this.getPrincipalName(header);
        if((
            !principalName.startsWith("admin" + SecurityKeys.ID_SEPARATOR) &&
            !(request.path().getParent().isLike(PATH_PATTERN_PRINCIPALS) && request.path().getBase().equals(principalName)))
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.AUTHORIZATION_FAILURE_UPDATE, 
                "No permission for " + DataproviderOperations.toString(operation) + " on object",
                new BasicException.Parameter("object", request.path()),
                new BasicException.Parameter("param0", request.path())
            );
        }        
    }
    
    //-------------------------------------------------------------------------
    /**
     * Update the realm if any object contained in the realm was modified.
     */
    private void updateRealm(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        if(
            (request.path().size() >= PATH_PATTERN_REALM_COMPOSITE.size()) &&
            request.path().getPrefix(PATH_PATTERN_REALM_COMPOSITE.size()).isLike(PATH_PATTERN_REALM_COMPOSITE)
        ) {
            try {
                Path realmIdentity = request.path().getPrefix(7);
                RequestCollection delegation = new RequestCollection(
                    header,
                    this.getDelegation()
                );
                MappedRecord realm = delegation.addGetRequest( 
                    realmIdentity,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                );
                delegation.addReplaceRequest(
                    (MappedRecord)realm.clone()
                );
            }
            catch(ServiceException e) {
                // Ignore if realm does not exist
                if(BasicException.Code.NOT_FOUND != e.getExceptionCode()) {
                    throw e;
                }
            }
            catch (CloneNotSupportedException e) {
            	throw new ServiceException(e);
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
        this.updateRealm(
            header,
            request
        );
        return super.remove(
            header, 
            request
        );
    }
    
    //-------------------------------------------------------------------------
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
        this.setAttributes(
            header,
            request.object(),
            null
        );
        this.updateRealm(
            header,
            request
        );
        return super.create(header, request);
    }
    
    //-------------------------------------------------------------------------
    public DataproviderReply replace(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
        this.setAttributes(
            header,
            request.object(),
            this.delegation.addGetRequest(
              request.path(),
              AttributeSelectors.ALL_ATTRIBUTES,
              null
            )
        );
        // Only mark realm as dirty if group memberships are modified
        // E.g. modifying lastLoginAt does not require to refresh the realm
        try {
	        if(ObjectHolder_2Facade.newInstance(request.object()).getValue().keySet().contains("isMemberOf")) {
	            this.updateRealm(
	                header,
	                request
	            );
	        }
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        try {
            return super.replace(
                header,
                request
            );
        }
        catch(ServiceException e) {
            // Ignore CONCURRENT_ACCESS_FAILURE on realms (updateRealm could have
            // been called before)
            if(
                (e.getExceptionCode() == BasicException.Code.CONCURRENT_ACCESS_FAILURE) &&
                request.path().isLike(PATH_PATTERN_REALM)
            ) {
                return new DataproviderReply(request.object());                
            }
            throw e;            
        }
    }
    
    //-------------------------------------------------------------------------
    public DataproviderReply get(
      ServiceHeader header,
      DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
        return this.completeReply(
            header,
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES ? 
            	request.attributeSpecifierAsMap().keySet() : 
            	null,
            super.get(
	            header,
	            request
            )
        );
    }

    //-------------------------------------------------------------------------
    public DataproviderReply operation(
      ServiceHeader header,
      DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
                    
        String operationName = request.path().get(
            request.path().size() - 2
        );
        MappedRecord source = this.retrieveObject(
            request.path().getPrefix(request.path().size() - 2)
        );
        String sourceClass = ObjectHolder_2Facade.getObjectClass(source);
        MappedRecord param = request.object();
        // change password
        MappedRecord reply = null;
        if("org:openmdx:security:authentication1:Password".equals(sourceClass)) {
            if("change".equals(operationName)) {
                this.changePassword(
                    source,
                    param
                );
                reply = this.createResult(
                    request,
                    "org:openmdx:base:Void"
                );
            }
        }
        
        // reply
        if(reply != null) {
            return new DataproviderReply(
              reply
            );
        }
        else {
            return super.operation(
              header,
              request
            );
        }
    }

    //-------------------------------------------------------------------------
    public DataproviderReply find(
      ServiceHeader header,
      DataproviderRequest request
    ) throws ServiceException {
        this.checkPermission(
            header,
            request
        );
        String principalName = this.getPrincipalName(header);
        String realmName = principalName.startsWith("admin" + SecurityKeys.ID_SEPARATOR) ? 
        	principalName.substring(principalName.indexOf("-") + 1) : 
        		"";
        // Restrict browsing on principals
        if(request.path().isLike(PATH_PATTERN_PRINCIPALS)) {
            boolean containsSubjectFilter = false;
            for(int i = 0; i < request.attributeFilter().length; i++) {
                if("subject".equals(request.attributeFilter()[i].name())) {
                    containsSubjectFilter = true;
                    break;
                }
            }
            // Return users and groups only if requesting principal is not admin-Root or segment admin
            if(
                !containsSubjectFilter &&
                !"Root".equals(realmName) &&
                !realmName.equals(request.path().get(request.path().size()-2))
            ) {
                request.addAttributeFilterProperty(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN,
                        "org:opencrx:security:realm1:PrincipalGroup",   
                        "org:opencrx:security:realm1:User"   
                    )
                );                    
            }
        }
        // Restrict browsing on subjects
        else if(request.path().isLike(PATH_PATTERN_SUBJECTS)) {
            // Do not restrict Root            
            if(!"Root".equals(realmName)) {
                request.addAttributeFilterProperty(
                    new FilterProperty(
                        Quantors.FOR_ALL,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN
                    )
                );
            }
        }
        return this.completeReply(
            header,
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES ? 
            	request.attributeSpecifierAsMap().keySet() : 
            	null,
            super.find(
	            header,
	            new DataproviderRequest(
	            	request,
	            	request.object(),
	            	request.operation(),
	            	request.attributeFilter(),
	            	request.position(),
	            	request.size(),
	            	request.direction(),
	            	AttributeSelectors.ALL_ATTRIBUTES,
	            	request.attributeSpecifier()
	            )
            )
        );
    }

    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    private static final Path PATH_PATTERN_PRINCIPALS = 
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal");
    private static final Path PATH_PATTERN_REALM =
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*");        
    private static final Path PATH_PATTERN_REALM_COMPOSITE =
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/:*");        
    private static final Path PATH_PATTERN_SUBJECTS = 
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject");

    private RequestCollection delegation = null;
    private Model_1_0 model = null;
    
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSecurity_1.java,v 1.62 2012/01/12 21:16:42 wfro Exp $
 * Description: OpenCrxSecurity_1
 * Revision:    $Revision: 1.62 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/12 21:16:42 $
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

import java.util.Date;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.DataproviderRequestProcessor;
import org.openmdx.application.dataprovider.cci.FilterProperty;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.layer.application.Standard_1;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.ConditionType;
import org.openmdx.base.query.Quantifier;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.cci.MessageRecord;
import org.openmdx.base.rest.spi.Facades;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
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
public class OpenCrxSecurity_1 extends Standard_1 {

    //-------------------------------------------------------------------------
	public OpenCrxSecurity_1(
	) {
	}
	
    //--------------------------------------------------------------------------
    public Interaction getInteraction(
        Connection connection
    ) throws ResourceException {
        return new LayerInteraction(connection);
    }
 
    //-------------------------------------------------------------------------
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
    } 

    //-------------------------------------------------------------------------
    protected String getPrincipalName(
        ServiceHeader header
    ) throws ServiceException {
        if(header.getPrincipalChain().isEmpty()) {
            return null;
        }
        return header.getPrincipalChain().get(0);
    }
    
    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      MappedRecord object
    ) throws ServiceException {
    }
    
    //-------------------------------------------------------------------------
    protected DataproviderReply completeReply(
      ServiceHeader header,
      DataproviderReply reply
    ) throws ServiceException {
      for(int i = 0; i < reply.getObjects().length; i++) {
          this.completeObject(
              header,
              reply.getObjects()[i]
          );
      }
      return reply;
    }
    
    //-------------------------------------------------------------------------
    private void setQualifier(
    	MappedRecord obj
    ) throws ServiceException {
    	Object_2Facade objFacade = Facades.asObject(obj);
        if(
            this.getModel().objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Principal") ||
            this.getModel().objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Realm") ||
            this.getModel().objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Role") ||
            this.getModel().objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Policy")
        ) {
        	objFacade.attributeValuesAsList("name").clear();
        	objFacade.attributeValuesAsList("name").add(
        		objFacade.getPath().getBase()
        	);
        }
        else if(
        	this.getModel().objectIsSubtypeOf(obj, "org:openmdx:security:realm1:Credential")
        ) {
        	objFacade.attributeValuesAsList("id").clear();
        	objFacade.attributeValuesAsList("id").add(
        		objFacade.getPath().getBase()
        	);
        }
    }
    
    //-------------------------------------------------------------------------
    protected Object_2Facade createResult(
      DataproviderRequest request,
      String structName
    ) throws ServiceException {
    	try {
    		Object_2Facade result = Object_2Facade.newInstance(
		        request.path().getDescendant(
		          new String[]{ "reply", UUIDs.getGenerator().next().toString()}
		        ),
		        structName
	      );
	      return result;
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }

    //-------------------------------------------------------------------------
    protected void setAttributes(
      ServiceHeader header,
      MappedRecord obj,
      MappedRecord oldValues
    ) throws ServiceException {
      this.setQualifier(
        obj 
      );
    }

    //-------------------------------------------------------------------------
    protected void changePassword(
    	ServiceHeader header,
    	MappedRecord passwordCredential,
    	MappedRecord changePasswordParams
    ) throws ServiceException {
        DataproviderRequestProcessor delegation = new DataproviderRequestProcessor(
            header,
            this.getDelegation()
        );    	    	
    	Object_2Facade changePasswordParamsFacade = Facades.asObject(changePasswordParams);
    	Object_2Facade passwordCredentialFacade = Facades.asObject(passwordCredential);
        String oldPassword = (changePasswordParamsFacade.getAttributeValues("oldPassword") != null) && !changePasswordParamsFacade.attributeValuesAsList("oldPassword").isEmpty() ? 
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
        MappedRecord changedPasswordCredential = Object_2Facade.cloneObject(passwordCredential);
        Object_2Facade changedPasswordCredentialFacade = Facades.asObject(changedPasswordCredential);
        changedPasswordCredentialFacade.attributeValuesAsList("password").clear();
        changedPasswordCredentialFacade.attributeValuesAsList("password").add(
            Base64.encode((byte[])changePasswordParamsFacade.attributeValue("password"))
        );
        changedPasswordCredentialFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).clear();
        changedPasswordCredentialFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).add(new Date());            
        delegation.addReplaceRequest(
            changedPasswordCredential
        );
    }

    //-------------------------------------------------------------------------
    /**
     * Update the realm if any object contained in the realm was modified.
     */
    protected void updateRealm(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        if(
            (request.path().size() >= PATH_PATTERN_REALM_COMPOSITE.size()) &&
            request.path().getPrefix(PATH_PATTERN_REALM_COMPOSITE.size()).isLike(PATH_PATTERN_REALM_COMPOSITE)
        ) {
            try {
                Path realmIdentity = request.path().getPrefix(7);
                DataproviderRequestProcessor delegation = new DataproviderRequestProcessor(
                    header,
                    this.getDelegation()
                );
                MappedRecord realm = delegation.addGetRequest( 
                    realmIdentity,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                );
                MappedRecord updatedRealm = (MappedRecord)realm.clone();
                Object_2Facade updatedRealmFacade = Facades.asObject(updatedRealm);
                updatedRealmFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).clear();                	
                updatedRealmFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).add(new Date());                	
                delegation.addReplaceRequest(
                    updatedRealmFacade.getDelegate()
                );
            }
            catch(ServiceException e) {
                // Ignore if realm does not exist
                if(BasicException.Code.NOT_FOUND != e.getExceptionCode()) {
                    throw e;
                }
            }
            catch (Exception e) {
            	throw new ServiceException(e);
            }
        }
    }
    
    // --------------------------------------------------------------------------
    public class LayerInteraction extends Standard_1.LayerInteraction {
        
        //---------------------------------------------------------------------------
        public LayerInteraction(
            javax.resource.cci.Connection connection
        ) throws ResourceException {
            super(connection);
        }
                
        //-------------------------------------------------------------------------
        public MappedRecord retrieveObject(
        	ServiceHeader header,
            Path identity
        ) throws ServiceException {
        	try {
	        	DataproviderRequest getRequest = new DataproviderRequest(
	                Query_2Facade.newInstance(identity).getDelegate(),
	                DataproviderOperations.OBJECT_RETRIEVAL,
	                AttributeSelectors.ALL_ATTRIBUTES,
	                new AttributeSpecifier[]{}
	        	);
	        	DataproviderReply getReply = this.newDataproviderReply();
	        	this.getDelegatingInteraction().get(
	        		getRequest.getInteractionSpec(), 
	        		Query_2Facade.newInstance(getRequest.path()), 
	        		getReply.getResult()
	        	);
	        	return getReply.getObject();
        	} catch(ResourceException e) {
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
                operation == DataproviderOperations.ITERATION_START
            ) {
                return;
            }
            // 1) All other operations only allowed for admin principals
            // 2) Principal is allowed to update its principal objects
            String principalName = OpenCrxSecurity_1.this.getPrincipalName(header);
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
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	        this.checkPermission(
	            header,
	            request
	        );
	        OpenCrxSecurity_1.this.updateRealm(
	            header,
	            request
	        );
	        return super.delete(
	            ispec,
	            input,
	            output
	        );
	    }
	    
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean create(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            this.checkPermission(
	            header,
	            request
	        );
            OpenCrxSecurity_1.this.setAttributes(
	            header,
	            request.object(),
	            null
	        );
            OpenCrxSecurity_1.this.updateRealm(
	            header,
	            request
	        );
	        return super.create(
	        	ispec,
	        	input,
	        	output
	        );
	    }
	    
	    //-------------------------------------------------------------------------
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
            this.checkPermission(
	            header,
	            request
	        );
	        OpenCrxSecurity_1.this.setAttributes(
	            header,
	            request.object(),
	            this.retrieveObject(
	            	header,
	            	request.path()
	            )
	        );
	        // Only mark realm as dirty if group memberships are modified
	        // E.g. modifying lastLoginAt does not require to refresh the realm
	        try {
		        if(Object_2Facade.newInstance(request.object()).getValue().keySet().contains("isMemberOf")) {
		        	OpenCrxSecurity_1.this.updateRealm(
		                header,
		                request
		            );
		        }
	        }
	        catch (ResourceException e) {
	        	throw new ServiceException(e);
	        }
	        try {
	            return super.put(
	                ispec,
	                input,
	                output
	            );
	        }
	        catch(Exception e) {
	        	ServiceException e0 = new ServiceException(e);
	            // Ignore CONCURRENT_ACCESS_FAILURE on realms (updateRealm could have
	            // been called before)
	            if(
	                (e0.getExceptionCode() == BasicException.Code.CONCURRENT_ACCESS_FAILURE) &&
	                request.path().isLike(PATH_PATTERN_REALM)
	            ) {
	            	if(reply.getResult() != null) {
	            		reply.getResult().add(
	            			request.object()
	            		);
	            	}
	            	return true;
	            }
	            throw e0;            
	        }
	    }
	    
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
            this.checkPermission(
	            header,
	            request
	        );
            super.get(
	            ispec,
	            input,
	            output
            );
            OpenCrxSecurity_1.this.completeReply(
	            header,
            	reply
	        );
	        return true;
	    }
	
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean invoke(
            RestInteractionSpec ispec, 
            MessageRecord input, 
            MessageRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            this.checkPermission(
	            header,
	            request
	        );
	                    
	        String operationName = request.path().get(
	            request.path().size() - 2
	        );
	        MappedRecord source = this.retrieveObject(
	        	header,
	            request.path().getPrefix(request.path().size() - 2)
	        );
	        String sourceClass = Object_2Facade.getObjectClass(source);
	        MappedRecord param = request.object();
	        // change password
	        Object_2Facade result = null;
	        if("org:openmdx:security:authentication1:Password".equals(sourceClass)) {
	            if("change".equals(operationName)) {
	                OpenCrxSecurity_1.this.changePassword(
	                	header,
	                    source,
	                    param
	                );
	                result = OpenCrxSecurity_1.this.createResult(
	                    request,
	                    "org:openmdx:base:Void"
	                );
	            }
	        }
	        
	        // reply
	        if(result != null) {
	        	output.setPath(result.getPath());
	        	output.setBody(result.getValue());
	        	return true;
	        }
	        else {
	            return super.invoke(
	              ispec,
	              input,
	              output
	            );
	        }
	    }
	
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
            this.checkPermission(
	            header,
	            request
	        );
	        String principalName = OpenCrxSecurity_1.this.getPrincipalName(header);
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
	                        Quantifier.THERE_EXISTS.code(),
	                        SystemAttributes.OBJECT_CLASS,
	                        ConditionType.IS_IN.code(),
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
	                        Quantifier.FOR_ALL.code(),
	                        SystemAttributes.OBJECT_CLASS,
	                        ConditionType.IS_IN.code()
	                    )
	                );
	            }
	        }
	        DataproviderRequest findRequest =  new DataproviderRequest(
            	request.object(),
            	request.operation(),
            	request.attributeFilter(),
            	request.position(),
            	request.size(),
            	request.direction(),
            	AttributeSelectors.ALL_ATTRIBUTES,
            	request.attributeSpecifier()
            );
	        try {
	            super.find(
	            	findRequest.getInteractionSpec(), 
	            	Query_2Facade.newInstance(findRequest.object()), 
	            	reply.getResult()
	            );
            }
            catch (ResourceException e) {
            	throw new ServiceException(e);
            }
	        OpenCrxSecurity_1.this.completeReply(
	            header,
	            reply
	        );
	        return true;
	    }
	    
    }
    
    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    protected static final Path PATH_PATTERN_PRINCIPALS = 
        new Path("xri://@openmdx*org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal");
    protected static final Path PATH_PATTERN_REALM =
        new Path("xri://@openmdx*org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*");        
    protected static final Path PATH_PATTERN_REALM_COMPOSITE =
        new Path("xri://@openmdx*org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/:*");        
    protected static final Path PATH_PATTERN_SUBJECTS = 
        new Path("xri://@openmdx*org.opencrx.security.identity1/provider/:*/segment/:*/subject");
}

//--- End of File -----------------------------------------------------------

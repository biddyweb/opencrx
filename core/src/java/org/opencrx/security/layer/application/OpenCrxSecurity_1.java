/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: OpenCrxSecurity_1.java,v 1.21 2008/07/07 14:03:00 wfro Exp $
 * Description: OpenCrxSecurity_1
 * Revision:    $Revision: 1.21 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/07/07 14:03:00 $
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

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.compatibility.base.application.configuration.Configuration;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderReply;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.application.ProvidingUid_1;
import org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;

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
  extends ProvidingUid_1 {

    //-------------------------------------------------------------------------
    public void activate(
      short id, 
      Configuration configuration,
      Layer_1_0 delegation
    ) throws ServiceException, Exception {
      super.activate(
        id,
        configuration,
        delegation
      );
      
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
      if(configuration.values("realmIdentity").size() > 0) {
          this.realmIdentity = new Path((String)configuration.values("realmIdentity").get(0));
      }
      else {
          throw new ServiceException(
              BasicException.Code.DEFAULT_DOMAIN,
              BasicException.Code.INVALID_CONFIGURATION, 
              null,
              "A realm identity must be configured with option 'realmIdentity'"
          );
      } 
      
    } 

    //-------------------------------------------------------------------------
    private String getPrincipalName(
        ServiceHeader header
    ) throws ServiceException {
        if(header.getPrincipalChain().size() == 0) {
            return null;
        }
        return (String)header.getPrincipalChain().get(0);
    }
    
    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      Set fetchSet,
      DataproviderObject_1_0 object
    ) throws ServiceException {
    }
    
    //-------------------------------------------------------------------------
    private DataproviderReply completeReply(
      ServiceHeader header,
      Set fetchSet,
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
        DataproviderObject obj
    ) throws ServiceException {
        if(
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Principal") ||
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Realm") ||
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Permission") ||
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Role") ||
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Policy")
        ) {
            obj.clearValues("name").add(obj.path().getBase());            
        }
        else if(
            this.model.isSubtypeOf(obj, "org:openmdx:security:realm1:Credential")
        ) {
           obj.clearValues("id").add(obj.path().getBase());            
        }
    }
    
    //-------------------------------------------------------------------------
    public DataproviderObject_1_0 retrieveObject(
        Path identity
    ) throws ServiceException {
        return this.delegation.addGetRequest(
            identity,
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{}
        );        
    }

    //-------------------------------------------------------------------------
    private DataproviderObject createResult(
      DataproviderRequest request,
      String structName
    ) {
      DataproviderObject result = new DataproviderObject(
        request.path().getDescendant(
          new String[]{ "reply", UUIDs.getGenerator().next().toString()}
        )
      );
      result.clearValues(SystemAttributes.OBJECT_CLASS).add(
        structName
      );
      return result;
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
      DataproviderObject obj,
      DataproviderObject_1_0 oldValues
    ) throws ServiceException {
      this.setQualifier(
        obj 
      );
    }

    //-------------------------------------------------------------------------
    private void changePassword(
        DataproviderObject_1_0 passwordCredential,
        DataproviderObject_1_0 changePasswordParams
    ) throws ServiceException {
        String oldPassword = changePasswordParams.getValues("oldPassword") != null
            ? Base64.encode((byte[])changePasswordParams.values("oldPassword").get(0))
            : null;
        if((oldPassword != null) && !oldPassword.equals(passwordCredential.values("password").get(0))) {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.ASSERTION_FAILURE, 
                new BasicException.Parameter[]{
                    new BasicException.Parameter("credential", passwordCredential)
                },
                "old password verification mismatch"
            );
        }
        DataproviderObject changedPasswordCredential = new DataproviderObject(
            passwordCredential
        );
        changedPasswordCredential.clearValues("password").add(
            Base64.encode((byte[])changePasswordParams.values("password").get(0))
        );
        this.delegation.addReplaceRequest(
            changedPasswordCredential
        );
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
                new BasicException.Parameter[]{
                    new BasicException.Parameter("object", request.path()),
                    new BasicException.Parameter("param0", request.path())
                },
                "No permission for " + DataproviderOperations.toString(operation) + " on object"
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
                DataproviderObject_1_0 realm = delegation.addGetRequest( 
                    realmIdentity,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                );
                delegation.addReplaceRequest(
                    new DataproviderObject(realm)
                );
            }
            catch(ServiceException e) {
                // Ignore if realm does not exist
                if(BasicException.Code.NOT_FOUND != e.getExceptionCode()) {
                    throw e;
                }
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
        if(request.object().containsAttributeName("isMemberOf")) {
            this.updateRealm(
                header,
                request
            );
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
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES
	            ? request.attributeSpecifierAsMap().keySet()
	            : null,
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
        DataproviderObject_1_0 source = this.retrieveObject(
            request.path().getPrefix(request.path().size() - 2)
        );
        String sourceClass = (String)source.values(SystemAttributes.OBJECT_CLASS).get(0);
        DataproviderObject param = request.object();

        // change password
        DataproviderObject reply = null;
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
        String realmName = principalName.startsWith("admin" + SecurityKeys.ID_SEPARATOR)
            ? principalName.substring(principalName.indexOf("-") + 1)
            : "";
        // Restrict browsing on principals
        if(request.path().isLike(PATH_PATTERN_PRINCIPALS)) {
            boolean containsSubjectFilter = false;
            for(int i = 0; i < request.attributeFilter().length; i++) {
                if("subject".equals(request.attributeFilter()[i].name())) {
                    containsSubjectFilter = true;
                    break;
                }
            }
            // Return groups only if requesting principal is not admin-Root
            // or segment admin
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
                        new String[]{
                            "org:opencrx:security:realm1:PrincipalGroup"   
                        }
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
                        FilterOperators.IS_IN,
                        new String[]{}
                    )
                );
            }
        }
        return this.completeReply(
            header,
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES
                ? request.attributeSpecifierAsMap().keySet()
	            : null,
            super.find(
	            header,
	            request
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
    private Path realmIdentity = null;
    private Model_1_0 model = null;
    
}

//--- End of File -----------------------------------------------------------

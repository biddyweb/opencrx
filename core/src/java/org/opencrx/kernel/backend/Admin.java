/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Admin.java,v 1.13 2009/01/06 13:00:22 wfro Exp $
 * Description: Admin
 * Revision:    $Revision: 1.13 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/01/06 13:00:22 $
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.uses.org.apache.commons.collections.set.ListOrderedSet;

public class Admin {

    //-----------------------------------------------------------------------
    public Admin(
        Backend backend
    ) {
        this.backend = backend;
    }
            
    //-----------------------------------------------------------------------
    public DataproviderObject_1_0 createSegment(
        String qualifiedModelName,
        String providerName,
        String segmentName,
        boolean isSecureObject,
        Path groupAdminIdentity,
        Path groupAdmininstratorsIdentity,
        List errors
    ) {
	    Path segmentIdentity = new Path("xri:@openmdx:" + qualifiedModelName.replace(':', '.') + "/provider");
	    segmentIdentity = segmentIdentity.getDescendant(new String[]{providerName, "segment", segmentName});        
	    DataproviderObject_1_0 segment = null;
	    try {
	        segment = this.backend.getDelegatingRequests().addGetRequest(
	            segmentIdentity,
	            AttributeSelectors.ALL_ATTRIBUTES,
	            new AttributeSpecifier[]{}        
	        );            
	    }
	    catch(Exception e) {
	        DataproviderObject newSegment = new DataproviderObject(
	            segmentIdentity
	        );
	        newSegment.values(SystemAttributes.OBJECT_CLASS).add(qualifiedModelName + ":Segment");
	        if(isSecureObject) {
		        newSegment.values("owningUser").add(groupAdminIdentity);
		        newSegment.values("owningGroup").add(groupAdmininstratorsIdentity);
                newSegment.values("accessLevelBrowse").add(new Short(SecurityKeys.ACCESS_LEVEL_GLOBAL));
                newSegment.values("accessLevelUpdate").add(new Short(SecurityKeys.ACCESS_LEVEL_DEEP));
                newSegment.values("accessLevelDelete").add(new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE));                
	        }
	        try {
	            this.backend.getDelegatingRequests().addCreateRequest(newSegment);
	        }
	        catch(Exception e0) {
	            ServiceException e1 = new ServiceException(e0);
	            AppLog.warning(e1.getMessage(), e1.getCause());
	            errors.add("can not create segment for " + qualifiedModelName);
	            errors.add("reason is " + e0.getMessage());
	            return null;
	        }
	        segment = newSegment;
	    }
	    return segment;
    }
    
    //-----------------------------------------------------------------------
    public Path createContact(
        Path adminIdentity,
        String segmentName,
        String principalName,
        Path owningUser,
        Path[] owningGroup,
        List errors
    ) {
        Path contactIdentity = new Path("xri:@openmdx:org.opencrx.kernel.account1/provider");
        contactIdentity = contactIdentity.getDescendant(new String[]{adminIdentity.get(2), "segment", segmentName, "account", principalName});        
        try {
            this.backend.getDelegatingRequests().addGetRequest(
                contactIdentity,
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}        
            );            
        }
        catch(Exception e) {
            DataproviderObject newContact = new DataproviderObject(
                contactIdentity
            );
            newContact.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:Contact");
            newContact.values("lastName").add(principalName);
            newContact.values("owningUser").add(owningUser);
            newContact.values("owningGroup").addAll(
                Arrays.asList(owningGroup)
            );
            try {
                this.backend.getAccounts().updateAccount(newContact, null);
                this.backend.getDelegatingRequests().addCreateRequest(newContact);
            }
            catch(Exception e0) {
                ServiceException e1 = new ServiceException(e);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("can not create contact");
                errors.add("reason is " + e0.getMessage());
                return null;
            }
        }
        return contactIdentity;
    }

    //-----------------------------------------------------------------------
    public Path createSubject(
        String subjectName,
        Path identitySegment,
        List errors
    ) {
        Path subjectIdentity = identitySegment;
        subjectIdentity = subjectIdentity.getDescendant(new String[]{"subject", subjectName});        
        try {
            this.backend.getDelegatingRequests().addGetRequest(
                subjectIdentity,
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}
            );  
        }
        catch(Exception e) {
            DataproviderObject newSubject = new DataproviderObject(
                subjectIdentity
            );
            newSubject.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:security:identity1:Subject");
            newSubject.values("description").add(subjectName);
            try {
                this.backend.getDelegatingRequests().addCreateRequest(newSubject);
            }
            catch(Exception e0) {
                ServiceException e1 = new ServiceException(e);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("can not create subject " + subjectName + " in segment " + identitySegment);
                errors.add("reason is " + e0.getMessage());
                return null;
            }
        }
        return subjectIdentity;
    }
    
    //-----------------------------------------------------------------------
    public Path createPrincipal(
        String principalName,
        Path realmIdentity,
        String principalClass,
        Path[] isMemberOf,
        Path subjectIdentity,
        List errors
    ) {
        Path principalIdentity = realmIdentity;
        principalIdentity = principalIdentity.getDescendant(new String[]{"principal", principalName});        
        try {
            DataproviderObject principal = new DataproviderObject(
                this.backend.getDelegatingRequests().addGetRequest(
                    principalIdentity,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                )
            );  
            List isMemberOfAsList = Arrays.asList(isMemberOf);
            if(!principal.values("isMemberOf").containsAll(isMemberOfAsList)) {
                Set modifiedIsMemberOf = new ListOrderedSet();
                modifiedIsMemberOf.addAll(principal.values("isMemberOf"));
                modifiedIsMemberOf.addAll(isMemberOfAsList);
                principal.clearValues("isMemberOf").addAll(
                    modifiedIsMemberOf
                );
                try {
                    this.backend.getDelegatingRequests().addReplaceRequest(principal);
                }
                catch(ServiceException e) {
                    ServiceException e1 = new ServiceException(e);
                    AppLog.warning(e1.getMessage(), e1.getCause());
                    errors.add("can not update principal " + principalName + " in realm " + realmIdentity);
                    errors.add("reason is " + e1.getMessage());
                    return null;                    
                }
            }
        }
        catch(Exception e) {
            DataproviderObject newPrincipal = new DataproviderObject(
                principalIdentity
            );
            newPrincipal.values(SystemAttributes.OBJECT_CLASS).add(principalClass);
            newPrincipal.values("description").add(principalIdentity.get(6) + "\\\\" + principalName);
            newPrincipal.values("disabled").add(Boolean.FALSE);
            newPrincipal.values("isMemberOf").addAll(
                Arrays.asList(isMemberOf)
            );
            if(subjectIdentity != null) {
                newPrincipal.values("subject").add(subjectIdentity);
            }
            try {
                this.backend.getDelegatingRequests().addCreateRequest(newPrincipal);
            }
            catch(Exception e0) {
                ServiceException e1 = new ServiceException(e);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("can not create principal " + principalName + " in realm " + realmIdentity);
                errors.add("reason is " + e0.getMessage());
                return null;
            }
        }
        return principalIdentity;
    }
    
    //-----------------------------------------------------------------------
    /*
     * Creates a new segment and segment administrators. <segmentName>:ADMIN_PRINCIPAL 
     * is the owner of all created objects. 
     */
    public void createAdministrator(
        Path adminIdentity,
        String segmentName,
        String principalName,
        String initialPassword,
        String initialPasswordVerification,
        List errors
    ) {
        String providerName = adminIdentity.get(2);
        
        // Principal must exist in login realm
        String segmentAdminName = SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName;
        Path loginRealmIdentity = this.backend.getRealmIdentity();
        try {
            this.backend.getDelegatingRequests().addGetRequest(
                loginRealmIdentity.getDescendant(new String[]{"principal", principalName}),
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}        
            );            
        }
        catch(Exception e) {
            // Create segment administrator if it does not exist
            if(principalName.equals(segmentAdminName)) {
                // Create segment administrator's subject
                Path segmentAdminSubjectIdentity = this.createSubject(
                    segmentAdminName,
                    new Path("xri:@openmdx:org.opencrx.security.identity1/provider/" + loginRealmIdentity.get(2) + "/segment/" + loginRealmIdentity.get(4)),
                    errors
                );
                if(errors.size() > 0) return;            
                // Create segment administrator's principal
                this.createPrincipal(
                    principalName,
                    loginRealmIdentity,
                    SecurityKeys.PRINCIPAL_TYPE_PRINCIPAL,
                    new Path[]{
                        loginRealmIdentity.getDescendant(new String[]{"principal", SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS})
                    },
                    segmentAdminSubjectIdentity,
                    errors
                );
                if(errors.size() > 0) return;                
            }
            else {
                ServiceException e1 = new ServiceException(e);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("principal " + principalName + " not found in realm " + loginRealmIdentity);
                return;
            }
        }
        
        // Principal admin-<segment name> must exist before any other
        // segment administrator can be created
        try {
            this.backend.getDelegatingRequests().addGetRequest(
                loginRealmIdentity.getDescendant(new String[]{"principal", segmentAdminName}),
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}        
            );            
        }
        catch(Exception e) {
            // admin-<segment name> does not exist --> principalName must match segment administrator
            if(!principalName.equals(segmentAdminName)) {
                errors.add("primary principal name must match " + segmentAdminName);
                return;
            }
        }
        if(
            (principalName.startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR)) &&
            !principalName.equals(segmentAdminName)
        ) {
            errors.add("admin principal for segment " + segmentName + " must match " + segmentAdminName);
            return;            
        }
        
        // realm
        Path realmIdentity = new Path("xri:@openmdx:org.openmdx.security.realm1/provider");
        realmIdentity = realmIdentity.getDescendant(new String[]{providerName, "segment", adminIdentity.get(4), "realm", segmentName});
        
        // user        
        Path userIdentity = realmIdentity;
        userIdentity = userIdentity.getDescendant(new String[]{"principal", principalName + "." + SecurityKeys.USER_SUFFIX});        
        
        // group Unspecified
        Path groupUnspecifiedIdentity = realmIdentity;
        groupUnspecifiedIdentity = groupUnspecifiedIdentity.getDescendant(new String[]{"principal", SecurityKeys.USER_GROUP_UNSPECIFIED});        

        // group Administrators
        Path groupAdministratorsIdentity = realmIdentity;
        groupAdministratorsIdentity = groupAdministratorsIdentity.getDescendant(new String[]{"principal", SecurityKeys.USER_GROUP_ADMINISTRATORS});        

        // group Users
        Path groupUsersIdentity = realmIdentity;
        groupUsersIdentity = groupUsersIdentity.getDescendant(new String[]{"principal", SecurityKeys.USER_GROUP_USERS});        

        // Create realm
        try {
            this.backend.getDelegatingRequests().addGetRequest(
                realmIdentity,
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}        
            );            
        }
        catch(Exception e) {
            DataproviderObject realm = new DataproviderObject(
                realmIdentity
            );
            realm.values(SystemAttributes.OBJECT_CLASS).add("org:openmdx:security:realm1:Realm");
            realm.values("description").add(segmentName + " Realm");
            try {
                this.backend.getDelegatingRequests().addCreateRequest(realm);
            }
            catch(Exception e0) {
                ServiceException e1 = new ServiceException(e);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("can not create realm " + realmIdentity);
                errors.add("reason is " + e0.getMessage());
                return;
            }
        }
        
        // Create segments
        this.createSegment("org:opencrx:kernel:account1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:home1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:activity1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:contract1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:product1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:document1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:building1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:uom1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:forecast1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:workflow1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:depot1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:model1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        this.createSegment("org:opencrx:kernel:code1", providerName, segmentName, true, userIdentity, groupAdministratorsIdentity, errors);
        if(errors.size() > 0) return;
        
        // Create contact: adminPrincipalName
        Path contactIdentity = this.createContact(
            adminIdentity, 
            segmentName, 
            principalName, 
            userIdentity, 
            new Path[]{
                groupUsersIdentity,
                groupAdministratorsIdentity
            },
            errors
        );
        if(contactIdentity == null) return;

        this.createPrincipal(
            SecurityKeys.USER_GROUP_UNSPECIFIED,
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_GROUP,
            new Path[]{},
            null,
            errors
        );
        if(errors.size() > 0) return;
        
        this.createPrincipal(
            SecurityKeys.USER_GROUP_ADMINISTRATORS,
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_GROUP,
            new Path[]{
                groupUnspecifiedIdentity        
            },
            null,
            errors
        );
        if(errors.size() > 0) return;

        this.createPrincipal(
            SecurityKeys.USER_GROUP_USERS,
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_GROUP,
            new Path[]{
                groupUnspecifiedIdentity        
            },
            null,
            errors
        );
        if(errors.size() > 0) return;

        this.createPrincipal(
            SecurityKeys.USER_GROUP_UNASSIGNED,
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_GROUP,
            new Path[]{
                groupUsersIdentity        
            },
            null,
            errors
        );
        if(errors.size() > 0) return;
                
        // Create user home principalName
        this.backend.getUserHomes().createUserHome(
            realmIdentity,
            contactIdentity,
            groupAdministratorsIdentity,
            principalName,
            Collections.EMPTY_SET,
            true,
            initialPassword,
            initialPasswordVerification,
            errors
        );
        
    }
    
    //-----------------------------------------------------------------------
    private DataproviderObject_1_0 retrievePrincipal(
        Path realm,
        String principalName
    ) {
        try {
            return this.backend.retrieveObjectFromDelegation(
                realm.getDescendant(new String[]{"principal", principalName})
            );
        }
        catch(Exception e) {
            return null;
        }
    }
    
    //-----------------------------------------------------------------------
    private DataproviderObject retrievePrincipalForModification(
        Path realm,
        String principalName
    ) {
        try {
            return this.backend.retrieveObjectForModification(
                realm.getDescendant(new String[]{"principal", principalName})
            );
        }
        catch(Exception e) {
            return null;
        }
    }
    
    //-----------------------------------------------------------------------
    private DataproviderObject_1_0 retrieveSubject(
        Path identitySegment,
        String subjectName
    ) {
        try {
            return this.backend.retrieveObjectFromDelegation(
                identitySegment.getDescendant(new String[]{"subject", subjectName})
            );
        }
        catch(Exception e) {
            return null;
        }
    }
    
    //-------------------------------------------------------------------------
    public String importLoginPrincipals(
        Path adminSegmentIdentity,
        byte[] item,
        boolean runAsRoot
    ) throws ServiceException {
        RequestCollection delegation = runAsRoot
            ? this.backend.getRunAsRootDelegation()
            : this.backend.getDelegatingRequests();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(item))
        );
        DataproviderObject_1_0 loginRealm = this.backend.retrieveObjectFromDelegation(
            new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + adminSegmentIdentity.get(2) + "/segment/Root/realm/Default")
        );
        DataproviderObject_1_0 identitySegment = this.backend.retrieveObjectFromDelegation( 
            new Path("xri:@openmdx:org.opencrx.security.identity1/provider/" + adminSegmentIdentity.get(2) + "/segment/Root")
        );
        int nCreatedPrincipals = 0;
        int nExistingPrincipals = 0;
        int nFailedPrincipals = 0;
        int nCreatedSubjects = 0;
        int nExistingSubjects = 0;
        int nFailedSubjects = 0;
        try {
            while(reader.ready()) {
                String l = reader.readLine();
                if(l.startsWith("Principal")) {
                    StringTokenizer t = new StringTokenizer(l, ";");
                    t.nextToken();
                    String principalName = t.nextToken();
                    String principalDescription = t.nextToken();
                    String subjectName = t.nextToken();
                    String groups = t.nextToken();                       
                    DataproviderObject principal = null;
                    if((principal = this.retrievePrincipalForModification(loginRealm.path(), principalName)) == null) {
                        try {
                            DataproviderObject_1_0 subject = this.backend.retrieveObjectFromDelegation(
                                identitySegment.path().getDescendant(new String[]{"subject", subjectName})
                            );
                            principal = new DataproviderObject(
                                loginRealm.path().getDescendant(new String[]{"principal", principalName})
                            );
                            principal.values(SystemAttributes.OBJECT_CLASS).add(
                                "org:opencrx:security:realm1:Principal"
                            );
                            principal.values("description").add(principalDescription);
                            principal.values("disabled").add(Boolean.FALSE);
                            principal.values("subject").add(subject.path());
                            delegation.addCreateRequest(
                                principal
                            );
                            principal = this.retrievePrincipalForModification(
                                loginRealm.path(),
                                principalName
                            );
                            nCreatedPrincipals++;
                        }
                        catch(Exception e) {
                            new ServiceException(e).log();
                            nFailedPrincipals++;
                        }
                    }
                    else {
                        nExistingPrincipals++;
                    }
                    if(principal != null) {
                        StringTokenizer g = new StringTokenizer(groups, ",");
                        while(g.hasMoreTokens()) {
                            String groupPrincipalName = g.nextToken();
                            DataproviderObject_1_0 groupPrincipal = this.retrievePrincipal(
                                loginRealm.path(), 
                                groupPrincipalName
                            );
                            if(!principal.values("isMemberOf").contains(groupPrincipal.path())) {
                                principal.values("isMemberOf").add(groupPrincipal.path());
                            }
                        }
                    }
                }
                else if(l.startsWith("Subject")) {
                    StringTokenizer t = new StringTokenizer(l, ";");
                    t.nextToken();
                    String subjectName = t.nextToken();
                    String subjectDescription = t.nextToken();
                    if((this.retrieveSubject(identitySegment.path(), subjectName)) == null) {
                        try {
                            DataproviderObject subject = new DataproviderObject(
                                identitySegment.path().getDescendant(new String[]{"subject", subjectName})
                            );
                            subject.values(SystemAttributes.OBJECT_CLASS).add(
                                "org:opencrx:security:identity1:Subject"
                            );
                            subject.values("description").add(subjectDescription);
                            delegation.addCreateRequest(
                                subject
                            );
                            nCreatedSubjects++;
                        }
                        catch(Exception e) {
                            new ServiceException(e).log();
                            nFailedSubjects++;
                        }
                    }
                    else {
                        nExistingSubjects++;
                    }
                }
            }
        }
        catch(IOException e) {
            new ServiceException(e).log();
        }
        return 
            "Principals=(created:" + nCreatedPrincipals + ",existing:" + nExistingPrincipals + ",failed:" + nFailedPrincipals + "); " +
            "Subjects:(created:" + nCreatedSubjects + ",existing:" + nExistingSubjects + ",failed:" + nFailedSubjects + ")";
    }
        
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    protected final Backend backend;
        
}

//--- End of File -----------------------------------------------------------

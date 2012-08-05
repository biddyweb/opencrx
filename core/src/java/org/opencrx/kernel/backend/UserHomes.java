/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: UserHomes.java,v 1.22 2008/11/06 12:23:54 wfro Exp $
 * Description: UserHomes
 * Revision:    $Revision: 1.22 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/06 12:23:54 $
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.w3c.cci2.BinaryLargeObjects;

public class UserHomes {

    //-----------------------------------------------------------------------
    public UserHomes(
        Backend backend
    ) {
        this.backend = backend;
    }
    
    //-------------------------------------------------------------------------
    public static String getUidAsString(
    ) {
        return UUIDConversion.toUID(UserHomes.uuidGenerator.next());        
    }
    
    //-------------------------------------------------------------------------
    public static void markAsAccepted(
        org.opencrx.kernel.home1.jmi1.Alert alert,
        PersistenceManager pm
    ) throws ServiceException {
        alert.setAlertState(
            (short)3
        );        
    }
    
    //-------------------------------------------------------------------------
    public static void markAsRead(
        org.opencrx.kernel.home1.jmi1.Alert alert,
        PersistenceManager pm
    ) throws ServiceException {
        alert.setAlertState(
            (short)2
        );        
    }
    
    //-----------------------------------------------------------------------
    public static void refreshItems(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        PersistenceManager pm
    ) throws ServiceException {
        if(userHome.getContact() != null) {
            // Remove accepted alerts
            Query query = pm.newQuery(org.opencrx.kernel.home1.jmi1.Alert.class);
            org.opencrx.kernel.home1.cci2.AlertQuery alertQuery = (org.opencrx.kernel.home1.cci2.AlertQuery)query; 
            alertQuery.alertState().greaterThanOrEqualTo((short)3);
            query.setCandidates(userHome.getAlert());
            List<org.opencrx.kernel.home1.jmi1.Alert> alerts = (List<org.opencrx.kernel.home1.jmi1.Alert>)query.execute();
            for(org.opencrx.kernel.home1.jmi1.Alert alert: alerts) {
                alert.refDelete();
            }
            // Update charts
            Contracts.calculateUserHomeCharts(
                userHome,
                pm
            );
            Activities.calculateUserHomeCharts(
                userHome,
                pm
            );
        }
    }
       
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */
    public DataproviderObject_1_0 getUserHome(
      Path from
    ) throws ServiceException {
        ServiceHeader header = this.backend.getServiceHeader();
        return this.getUserHome(
            (String)header.getPrincipalChain().get(0),
            from
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */
    public DataproviderObject_1_0 getUserHome(
        String user,
        Path from
    ) throws ServiceException {
        if(user == null) return null;
        Path userHomePath = new Path(
            new String[]{
              "org:opencrx:kernel:home1",
              "provider",
              from.get(2),
              "segment",
              from.get(4),
              "userHome",
              user
            }
        );
        try {
            DataproviderObject_1_0 userHome = this.backend.retrieveObject(
                userHomePath
            );
            return userHome;
        }
        catch(ServiceException e) {
            AppLog.info("can not retrieve UserHome", userHomePath);
        }
        return null;
    }
      
    //-------------------------------------------------------------------------
    private byte[] getPasswordDigest(
        String password,
        String algorithm
    ) {
        try {
	        MessageDigest md = MessageDigest.getInstance(algorithm);
	        md.update(password.getBytes("UTF-8"));
	        return md.digest();
        }
	    catch(NoSuchAlgorithmException e) {
	    }
	    catch(UnsupportedEncodingException e) {
	    }
        return null;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Creates a password credential for the specified subject. The password
     * is not set by this method. This must be done with changePassword.
     * 
     * @deprecated
     */
    public DataproviderObject createPasswordCredential(
        Path subjectIdentity,
        List errors
    ) {
        String providerName = subjectIdentity.get(2);
        
        // create password credential
        Path passwordIdentity = new Path("xri:@openmdx:org.openmdx.security.authentication1/provider");
        passwordIdentity = passwordIdentity.getDescendant(
            new String[]{
                providerName, 
                "segment", 
                "Root", // credentials are stored in Root segment 
                "credential", 
                this.backend.getUidAsString()
            }
        );
        DataproviderObject passwordCredential = new DataproviderObject(
            passwordIdentity
        );
        passwordCredential.values(SystemAttributes.OBJECT_CLASS).add("org:openmdx:security:authentication1:Password");
        passwordCredential.values("subject").add(subjectIdentity);
        passwordCredential.values("locked").add(Boolean.FALSE);
        try {
            this.backend.getDelegatingRequests().addCreateRequest(passwordCredential);
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            AppLog.warning(e0.getMessage(), e0.getCause());
            errors.add("can not create password credential");
            errors.add("reason is " + e.getMessage());
            return null;
        }
        return passwordCredential;
    }
    
    //-------------------------------------------------------------------------
    public short changePassword(
        Path passwordCredentialIdentity,
        String oldPassword,
        String password
    ) {
        DataproviderObject changePasswordParams = new DataproviderObject(
            passwordCredentialIdentity.getChild("change")
        );
        changePasswordParams.values(SystemAttributes.OBJECT_CLASS).add(
            "org:openmdx:security:authentication1:PasswordChangeParams"
        );
        if(oldPassword != null) {
            changePasswordParams.values("oldPassword").add(this.getPasswordDigest(oldPassword, this.backend.context.passwordEncodingAlgorithm));
        }
        if(password == null) {
            return MISSING_NEW_PASSWORD;
        }
        changePasswordParams.values("password").add(this.getPasswordDigest(password, this.backend.context.passwordEncodingAlgorithm));
        try {
            RequestCollection runAsRootDelegation = this.backend.getRunAsRootDelegation();
            runAsRootDelegation.addOperationRequest(changePasswordParams);
        }
        catch(ServiceException e) {
            AppLog.warning(e.getMessage(), e.getCause());
            return CAN_NOT_CHANGE_PASSWORD;
        }
        return CHANGE_PASSWORD_OK;
    }
    
    //-------------------------------------------------------------------------
    public short changePassword(
        Path userHomeIdentity,
        boolean verifyOldPassword,
        String oldPassword,
        String newPassword,
        String newPasswordVerification
    ) {
        if(newPassword == null) {
            return MISSING_NEW_PASSWORD;
        }
        if(newPasswordVerification == null) {
            return MISSING_NEW_PASSWORD_VERIFICATION;
        }
        if(!newPassword.equals(newPasswordVerification)) {
            return PASSWORD_VERIFICATION_MISMATCH;
        }
        if(verifyOldPassword && oldPassword == null) {
            return MISSING_OLD_PASSWORD;
        }
        
        // qualifier of user home is the principal name
        String principalName = userHomeIdentity.getBase();
        
        // get principal
        DataproviderObject principal = null;
        try {
            principal = 
                new DataproviderObject(
	                this.backend.getDelegatingRequests().addGetRequest(
		                this.backend.getRealmIdentity().getDescendant(new String[]{"principal", principalName}),
		                AttributeSelectors.ALL_ATTRIBUTES,
		                new AttributeSpecifier[]{}
	                )
                );            
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            AppLog.warning(e0.getMessage(), e0.getCause());
            return CAN_NOT_RETRIEVE_REQUESTED_PRINCIPAL;
        }

        return this.changePassword(
            (Path)principal.values("credential").get(0),
            verifyOldPassword ? oldPassword : null,
            newPassword
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * Creates a new user. The owner of the created objects are set as follows:
     * <ul>
     *   <li>user subject: segment administrator
     *   <li>all other objects (user home, password): user
     * </ul>
     * 
     * @deprecated
     */
    public UserHome createUserHome(
        Path realmIdentity,
        Path contactIdentity,
        Path primaryGroup,
        String principalName,
        Set isMemberOf,
        boolean isAdministrator,
        String initialPassword,
        String initialPasswordVerification,
        List errors
    ) {
        if(principalName == null) {
            errors.add("missing principalName");
            return null;                        
        }
        if(contactIdentity == null) {
            errors.add("missing contact");
            return null;            
        }
        String providerName = contactIdentity.get(2);
        String segmentName = contactIdentity.get(4);
        Path loginRealmIdentity = this.backend.getRealmIdentity();
        
        // Get login principal. Get subject and set password credential
        Path subjectIdentity = null;
        try {
            // Create login principal and subject on-demand. Create principal
            // and subject using the importLoginPrincipals() operation. Prepare
            // an import stream containing the principal and subject to be imported
            DataproviderObject_1_0 contact = this.backend.retrieveObjectFromDelegation(
                contactIdentity
            );            
            ByteArrayOutputStream item = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(item);
            pw.println("Subject;" + principalName + ";" + contact.values("fullName").get(0));
            pw.println("Principal;" + principalName + ";" + segmentName + "\\\\" + principalName + ";" + principalName + ";Users");
            pw.close();
            this.backend.getAdmin().importLoginPrincipals(
                new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root"), 
                item.toByteArray(),
                // Invoking user is segment admin. Create login principal and subject as root
                true
            ); 
            // Get login principal and subject
            DataproviderObject_1_0 loginPrincipal = this.backend.retrieveObjectForModification(
                loginRealmIdentity.getDescendant(new String[]{"principal", principalName})
            );
            if(loginPrincipal.values("subject").isEmpty()) {
                errors.add("Undefined subject for principal " + loginPrincipal.path());
                return null;
            }
            subjectIdentity = (Path)loginPrincipal.values("subject").get(0);            
            // Create password credential
            if((initialPassword != null) && (initialPassword.length() > 0)) {
                DataproviderObject passwordCredential = this.createPasswordCredential(
                    subjectIdentity,
                    errors
                );
                if(passwordCredential == null) {
                    return null;
                }            
                // Set initial password
                this.changePassword(
                    passwordCredential.path(),
                    null,
                    initialPassword
                );            
                // Update principal's credential
                loginPrincipal.clearValues("credential").add(passwordCredential.path());
            }
            if((loginPrincipal.getValues("credential") == null) || (loginPrincipal.getValues("credential").size() == 0)) {
                errors.add("No credential specified for principal " + principalName);                
            }
        }
        catch(Exception e) {
            ServiceException e1 = new ServiceException(e);
            AppLog.warning(e1.getMessage(), e1.getCause());
            errors.add("can not retrieve principal " + principalName + " in realm " + loginRealmIdentity);
            errors.add("reason is " + e.getMessage());
            return null;
        }
        
        // Add user principal 
        Path userIdentity = this.backend.getAdmin().createPrincipal(
            principalName + "." + SecurityKeys.USER_SUFFIX,
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_USER,
            new Path[]{},
            subjectIdentity,
            errors
        );
        if(errors.size() > 0) return null;

        Path groupAdministratorsIdentity = userIdentity.getParent().getChild(SecurityKeys.USER_GROUP_ADMINISTRATORS);
        Path groupUsersIdentity = userIdentity.getParent().getChild(SecurityKeys.USER_GROUP_USERS);
        List isMemberOfGroupIdentities = new ArrayList();
        if(isAdministrator) {
            isMemberOfGroupIdentities.add(groupAdministratorsIdentity);
            isMemberOfGroupIdentities.add(userIdentity);
            isMemberOfGroupIdentities.add(groupUsersIdentity);
        }
        else {
            isMemberOfGroupIdentities.add(groupUsersIdentity);
            isMemberOfGroupIdentities.add(userIdentity);
        }
        for(Iterator i = isMemberOf.iterator(); i.hasNext(); ) {
            isMemberOfGroupIdentities.add(
                userIdentity.getParent().getChild((String)i.next())
            );
        }
        
        // Add principal
        this.backend.getAdmin().createPrincipal(
            principalName, 
            realmIdentity,
            SecurityKeys.PRINCIPAL_TYPE_PRINCIPAL,
            (Path[])isMemberOfGroupIdentities.toArray(new Path[isMemberOfGroupIdentities.size()]),
            subjectIdentity,
            errors
        );
        if(errors.size() > 0) return null;

        // Add principal to Root realm (each principal should be registered
        // in the Root realm because the Root segment provides data common
        // to all segments, e.g. code tables
        this.backend.getAdmin().createPrincipal(
            principalName, 
            realmIdentity.getParent().getChild("Root"),
            SecurityKeys.PRINCIPAL_TYPE_PRINCIPAL,
            new Path[]{},
            subjectIdentity,
            errors
        );
        if(errors.size() > 0) return null;

        // Add user principal to Root realm 
        this.backend.getAdmin().createPrincipal(
            principalName + "." + SecurityKeys.USER_SUFFIX,
            realmIdentity.getParent().getChild("Root"),
            SecurityKeys.PRINCIPAL_TYPE_USER,
            new Path[]{},
            subjectIdentity,
            errors
        );
        if(errors.size() > 0) return null;
        
        initialPassword = initialPassword == null ? "" : initialPassword;
        initialPasswordVerification = initialPasswordVerification == null ? "" : initialPasswordVerification;
        if(!initialPassword.equals(initialPasswordVerification)) {
            errors.add("the passwords you typed do not match");
            return null;
        }
        
        /**
         * User home
         */
        Path userHomeReference = new Path("xri:@openmdx:org.opencrx.kernel.home1");
        userHomeReference = userHomeReference.getDescendant(
            new String[]{"provider", providerName, "segment", segmentName, "userHome"}
        );
        DataproviderObject_1_0 userHome = null;
        try {
            userHome = this.backend.getDelegatingRequests().addGetRequest(
                userHomeReference.getChild(principalName),
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}        
            );
            if(primaryGroup != null) {
                DataproviderObject updatedUserHome = this.backend.retrieveObjectForModification(
                    userHome.path()
                );
                updatedUserHome.clearValues("primaryGroup").add(primaryGroup);
            }
        }
        catch(Exception e) {
            DataproviderObject newUserHome = new DataproviderObject(
                userHomeReference.getChild(principalName)
            );
            newUserHome.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:home1:UserHome");
            newUserHome.values("contact").add(contactIdentity);
            newUserHome.values("sendMailSubjectPrefix").add(
                "[" + providerName + ":" + segmentName + "]"
            );
            newUserHome.clearValues("primaryGroup").add(primaryGroup);            
            // owning user of home is user itself
            newUserHome.values("owningUser").add(userIdentity);
            // owning group of home is segment administrator
            newUserHome.values("owningGroup").addAll(
                Arrays.asList(
                    new Path[]{
                        groupAdministratorsIdentity                    
                    }        
                )
            );
            // access level delete
            newUserHome.values("accessLevelDelete").add(
                new Short((short)0)
            );
            newUserHome.values("accessLevelDelete").add(new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE));
            this.backend.getBase().initCharts(newUserHome, null);
            
            try {
                this.backend.getDelegatingRequests().addCreateRequest(newUserHome);
            }
            catch(Exception e0) {
                ServiceException e1 = new ServiceException(e0);
                AppLog.warning(e1.getMessage(), e1.getCause());
                errors.add("can not create user home");
                errors.add("reason is " + e.getMessage());
                return null;
            }
            userHome = newUserHome;
        }        
        return userHome == null
            ? null
            : (UserHome)this.backend.getDelegatingPkg().refObject(userHome.path().toXri());
    }

    //-----------------------------------------------------------------------
    /**
     * @deprecated
     */    
    private DataproviderObject_1_0 retrieveUserHome(
        Path userHomeSegment,
        String principalName
    ) {
        try {
            return this.backend.retrieveObjectFromDelegation(
                userHomeSegment.getDescendant(new String[]{"userHome", principalName})
            );
        }
        catch(Exception e) {
            return null;
        }
    }
    
    //-----------------------------------------------------------------------
    private DataproviderObject_1_0 retrieveContact(
        Path accountSegment,
        String aliasName,
        String fullName
    ) {
        try {
            FilterProperty[] filter = null;
            if(!"-".equals(aliasName)) {
                filter = new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "aliasName",
                        FilterOperators.IS_IN,
                        aliasName
                    )
                };
            }
            else if(!"-".equals(fullName)) {
                filter = new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "fullName",
                        FilterOperators.IS_IN,
                        fullName
                    )
                };
            }
            List accounts = this.backend.getDelegatingRequests().addFindRequest(
                accountSegment.getChild("account"),
                filter
            );
            if(accounts.size() > 0) {
                return (DataproviderObject_1_0)accounts.iterator().next();
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }
        
    //-----------------------------------------------------------------------
    /**
     * @deprecated
     */    
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
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */    
    public String importUsers(
        Path homeSegmentIdentity,
        byte[] item
    ) throws ServiceException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(item))
        );
        DataproviderObject_1_0 realm = this.backend.retrieveObjectFromDelegation(
            new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + homeSegmentIdentity.get(2) + "/segment/Root/realm/" + homeSegmentIdentity.get(4))
        );
        DataproviderObject_1_0 accountSegment = this.backend.retrieveObjectFromDelegation( 
            new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + homeSegmentIdentity.get(2) + "/segment/" + homeSegmentIdentity.get(4))
        );

        int nCreatedUsers = 0;
        int nFailedUsersNoPrimaryGroup = 0;
        int nFailedUsersNoContact = 0;
        int nFailedUsersOther = 0;
        int nExistingUsers = 0;
        try {
            while(reader.ready()) {
                String l = reader.readLine();
                if(l.startsWith("User")) {
                    StringTokenizer t = new StringTokenizer(l, ";");
                    t.nextToken();
                    String principalName = t.nextToken();
                    String accountAlias = t.nextToken();
                    String accountFullName = t.nextToken();
                    String primaryGroupName = t.nextToken();
                    String password = t.nextToken();
                    String groups = t.hasMoreTokens() ? t.nextToken() : "";
                    
                    if(this.retrieveUserHome(homeSegmentIdentity, principalName) == null) {
                        try {
                            DataproviderObject_1_0 contact = this.retrieveContact(accountSegment.path(), accountAlias, accountFullName);                            
                            if(contact != null) {
                                DataproviderObject_1_0 primaryGroup = this.retrievePrincipal(
                                    realm.path(), 
                                    primaryGroupName
                                );
                                if(primaryGroup != null) {
                                    List errors = new ArrayList();
                                    
                                    // Get groups
                                    Set isMemberOf = new HashSet();
                                    StringTokenizer g = new StringTokenizer(groups, ",");
                                    while(g.hasMoreTokens()) {
                                        isMemberOf.add(g.nextToken());
                                    }                                    
                                    this.createUserHome(
                                        realm.path(),
                                        contact.path(),
                                        primaryGroup.path(), 
                                        principalName,
                                        isMemberOf,
                                        false, 
                                        password, 
                                        password, 
                                        errors
                                    );
                                    if(errors.size() == 0) {
                                        nCreatedUsers++;
                                    }
                                    else {
                                        nFailedUsersOther++;
                                    }
                                }
                                else {
                                    AppLog.info("Group " + primaryGroup + " for user " + principalName + " not found");
                                    nFailedUsersNoPrimaryGroup++;
                                }
                            }
                            else {
                                AppLog.info("Contact " + accountAlias + "/" + accountFullName + " for user " + principalName + " not found");
                                nFailedUsersNoContact++;
                            }
                        }
                        catch(Exception e) {
                            new ServiceException(e).log();
                            nFailedUsersOther++;
                        }
                    }
                    else {
                        nExistingUsers++;
                    }
                }
            }       
        }
        catch(IOException e) {
            new ServiceException(e).log();
        }        
        return 
            "Users=(created:" + nCreatedUsers + ",existing:" + nExistingUsers + ",failed no primary group:" + nFailedUsersNoPrimaryGroup + ",failed no contact:" + nFailedUsersNoContact + ",failed other:" + nFailedUsersOther + ");";       
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */    
    public void encodeEMailAccountPassword(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        if(this.backend.getModel().isSubtypeOf(objectClass, "org:opencrx:kernel:home1:EMailAccount")) {
            if(object.getValues("incomingPassword") != null) {
                try {
                    String password = (String)object.values("incomingPassword").get(0);
                    if((password != null) && !password.startsWith(SecurityKeys.PASSWORD_ENCODING_SCHEME)) {
                        object.clearValues("incomingPassword").add(
                            SecurityKeys.PASSWORD_ENCODING_SCHEME + Base64.encode(password.getBytes("UTF-8"))                    
                        );
                    }
                } catch(UnsupportedEncodingException e) {}                
            }
            if(object.getValues("outgoingPassword") != null) {
                try {
                    String password = (String)object.values("outgoingPassword").get(0);
                    if((password != null) && !password.startsWith(SecurityKeys.PASSWORD_ENCODING_SCHEME)) {
                        object.clearValues("outgoingPassword").add(
                            SecurityKeys.PASSWORD_ENCODING_SCHEME + Base64.encode(password.getBytes("UTF-8"))                    
                        );
                    }
                } catch(UnsupportedEncodingException e) {}                
            }
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */    
    public List completeUserHome(
      DataproviderObject_1_0 userHome
    ) throws ServiceException {
        List additionalFetchedObjects = new ArrayList();
        try {
            List charts = this.backend.getDelegatingRequests().addFindRequest(
                userHome.path().getChild("chart"),  
                null,
                AttributeSelectors.ALL_ATTRIBUTES,
                null,
                0, Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            for(
                int i = 0; 
                i < 4; 
                i++
            ) {            
                // Touch so that attributes are in fetch set
                userHome.values("favoriteChart" + i);
                userHome.values("favoriteChart" + i + "MimeType");
                userHome.values("favoriteChart" + i + "Name");
                userHome.values("favoriteChart" + i + "Description");
                if(userHome.getValues("chart" + i).size() > 0) {
                    for(
                        Iterator j = charts.iterator(); 
                        j.hasNext(); 
                    ) {
                        DataproviderObject_1_0 chart = (DataproviderObject_1_0)j.next();
                        if(chart.path().equals(userHome.getValues("chart" + i).get(0))) {
                            ByteArrayOutputStream content = new ByteArrayOutputStream();
                            try {
                                BinaryLargeObjects.streamCopy(
                                    (InputStream)chart.values("content").get(0), 
                                    0L, 
                                    content
                                );
                            } catch(Exception e) {}
                            userHome.values("favoriteChart" + i).add(
                                content.toByteArray()
                            );
                            userHome.values("favoriteChart" + i + "MimeType").addAll(
                                chart.values("contentMimeType")
                            );
                            userHome.values("favoriteChart" + i + "Name").addAll(
                                chart.values("contentName")
                            );
                            userHome.values("favoriteChart" + i + "Description").addAll(
                                chart.values("description")
                            );
                            additionalFetchedObjects.add(chart);
                            break;
                        }
                    }
                }
            }
        }
        // ignore if charts can not be retrieved
        catch(ServiceException e) {}
        return additionalFetchedObjects;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */    
    public FilterProperty[] mapObjectFinderToFilter(
        DataproviderObject_1_0 objectFinder
    ) {
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        String allWords = (String)objectFinder.values("allWords").get(0);
        if((allWords != null) && (allWords.length() > 0)) {
            String words[] = allWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                filter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "keywords",
                        FilterOperators.IS_LIKE,
                        "%" + words[i] + "%"
                    )
                );
            }
        }
        String withoutWords = (String)objectFinder.values("withoutWords").get(0);
        if((withoutWords != null) && (withoutWords.length() > 0)) {
            String words[] = withoutWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                filter.add(
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "keywords",
                        FilterOperators.IS_UNLIKE,
                        "%" + words[i] + "%"
                    )
                );
            }
        }
        String atLeastOneOfTheWords = (String)objectFinder.values("atLeastOneOfTheWords").get(0);
        if((atLeastOneOfTheWords != null) && (atLeastOneOfTheWords.length() > 0)) {
            String words[] = atLeastOneOfTheWords.split("[\\s,]");
            for(int i = 0; i < words.length; i++) {
                words[i] = "%" + words[i] + "%";
            }
            filter.add(
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "keywords",
                    FilterOperators.IS_LIKE,
                    (Object[])words
                )
            );
        }
        return filter.toArray(new FilterProperty[filter.size()]);
    }
    
    //-------------------------------------------------------------------------
    public static ObjectFinder searchBasic(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        String searchExpression,
        PersistenceManager pm
    ) throws ServiceException {
        String words[] = searchExpression.split("[\\s,]");
        StringBuilder allWords = new StringBuilder();
        StringBuilder atLeastOneOfTheWords = new StringBuilder();
        StringBuilder withoutWords = new StringBuilder();
        for(int i = 0; i < words.length; i++) {
            if(
                ((i < words.length-1) && "OR".equals(words[i+1])) ||
                ((i > 0) && "OR".equals(words[i-1]))                
            ) {
                atLeastOneOfTheWords.append(" ").append(words[i]);
            }
            else if(!"OR".equals(words[i])) {
                if(words[i].startsWith("-")) {
                    withoutWords.append(" ").append(words[i].substring(1));                    
                }
                else {
                    allWords.append(" ").append(words[i]);
                }
            }
        }
        return UserHomes.searchAdvanced(
            userHome, 
            allWords.toString().trim(), 
            atLeastOneOfTheWords.toString().trim(), 
            withoutWords.toString().trim(),
            pm
        );
    }
    
    //-------------------------------------------------------------------------
    public static ObjectFinder searchAdvanced(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        String allWords,
        String atLeastOneOfTheWords,
        String withoutWords,
        PersistenceManager pm
    ) throws ServiceException {
        org.opencrx.kernel.home1.jmi1.ObjectFinder objectFinder = (org.opencrx.kernel.home1.jmi1.ObjectFinder)pm.newInstance(org.opencrx.kernel.home1.jmi1.ObjectFinder.class);
        objectFinder.refInitialize(false, false);
        objectFinder.setName(
            (allWords != null) && (allWords.length() > 0) ? 
                allWords : 
                (atLeastOneOfTheWords != null) && (atLeastOneOfTheWords.length() > 0) ? 
                    atLeastOneOfTheWords : 
                    withoutWords
        );
        if(allWords != null) {
            objectFinder.setAllWords(allWords.toLowerCase());
        }
        if(atLeastOneOfTheWords != null) {
            objectFinder.setAtLeastOneOfTheWords(atLeastOneOfTheWords.toLowerCase());
        }
        if(withoutWords != null) {
            objectFinder.setWithoutWords(withoutWords.toLowerCase());
        }
        userHome.addObjectFinder(
            false, 
            UserHomes.getUidAsString(), 
            objectFinder
        );
        return objectFinder;
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final short CHANGE_PASSWORD_OK = 0;
    public static final short MISSING_NEW_PASSWORD = 1;
    public static final short MISSING_NEW_PASSWORD_VERIFICATION = 2;
    public static final short PASSWORD_VERIFICATION_MISMATCH = 3;
    public static final short CAN_NOT_RETRIEVE_REQUESTED_PRINCIPAL = 4;
    public static final short CAN_NOT_CHANGE_PASSWORD = 5;
    public static final short MISSING_OLD_PASSWORD = 6;

    private static UUIDGenerator uuidGenerator = UUIDs.getGenerator();
    protected final Backend backend;
        
}

//--- End of File -----------------------------------------------------------

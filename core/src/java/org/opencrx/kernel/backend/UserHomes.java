/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: UserHomes.java,v 1.97 2010/12/24 15:07:40 wfro Exp $
 * Description: UserHomes
 * Revision:    $Revision: 1.97 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/24 15:07:40 $
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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.aop2.Configuration;
import org.opencrx.kernel.backend.Admin.PrincipalType;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.home1.cci2.EMailAccountQuery;
import org.opencrx.kernel.home1.cci2.SubscriptionQuery;
import org.opencrx.kernel.home1.jmi1.ActivityGroupCalendarFeed;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.opencrx.kernel.home1.jmi1.DocumentFeed;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.opencrx.kernel.home1.jmi1.Subscription;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow1.jmi1.Topic;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.io.QuotaByteArrayOutputStream;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.UserSettings;
import org.openmdx.portal.servlet.WebKeys;

public class UserHomes extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new UserHomes());
	}
	
    //-------------------------------------------------------------------------
	public static UserHomes getInstance(
	) throws ServiceException {
		return getInstance(UserHomes.class);
	}

	//-------------------------------------------------------------------------
	protected UserHomes(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public void markAsAccepted(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(
            ALERT_STATE_ACCEPTED
        );        
    }
    
    //-------------------------------------------------------------------------
    public void markAsRead(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(
            ALERT_STATE_READ
        );        
    }
    
    //-------------------------------------------------------------------------
    public void markAsNew(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(
            ALERT_STATE_NEW
        );        
    }
    
    //-----------------------------------------------------------------------
    public void refreshItems(
        org.opencrx.kernel.home1.jmi1.UserHome userHome
    ) throws ServiceException {
        if(userHome.getContact() != null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
        	// Collect non-expired alerts
        	final Date NOW_MINUS_1M = new Date(System.currentTimeMillis() - 30L * 86400L * 1000L);
        	final Date NOW_MINUS_3M = new Date(System.currentTimeMillis() - 90L * 86400L * 1000L);
            AlertQuery alertQuery = (AlertQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.Alert.class);
            alertQuery.alertState().lessThan(ALERT_STATE_EXPIRED);
            alertQuery.orderByCreatedAt().descending();
            List<Path> alertIdentities = new ArrayList<Path>();
            List<org.opencrx.kernel.home1.jmi1.Alert> alerts = userHome.getAlert(alertQuery);
            int n = 0;
            for(org.opencrx.kernel.home1.jmi1.Alert alert: alerts) {
        		alertIdentities.add(alert.refGetPath());
        		n++;            	
        		if(n > 25) break;
            }
            // Update state
            Set<ContextCapable> references = new HashSet<ContextCapable>();
            for(Path alertIdentity: alertIdentities) {
            	org.opencrx.kernel.home1.jmi1.Alert alert = (org.opencrx.kernel.home1.jmi1.Alert)pm.getObjectById(alertIdentity);
            	ContextCapable reference = null;
            	try {
            		reference = alert.getReference();
            	} catch(Exception e) {}
            	// Set state to expired if 
            	// * if it is accepted and older than three months
            	if(
            		(alert.getAlertState() == ALERT_STATE_ACCEPTED && alert.getCreatedAt().compareTo(NOW_MINUS_3M) < 0)
            	) {
                	alert.setAlertState(ALERT_STATE_EXPIRED);
            	}
            	// Set state to accepted if 
            	// * its reference is not valid or duplicate or
            	// * alert is older than one month
            	else if(
            		(reference == null) || 
            		references.contains(reference) || 
            		(alert.getAlertState() < ALERT_STATE_ACCEPTED && alert.getCreatedAt().compareTo(NOW_MINUS_1M) < 0)
            	) {
                	alert.setAlertState(ALERT_STATE_ACCEPTED);
            	}
            	if(reference != null) {
            		references.add(reference);
            	}
            }
        }
    }
       
    //-------------------------------------------------------------------------
    public UserHome getUserHome(
      Path from,
      PersistenceManager pm
    ) throws ServiceException {
    	List<String> principalChain = UserObjects.getPrincipalChain(pm);
    	return (UserHome)pm.getObjectById(
    		new Path(new String[]{
              "org:opencrx:kernel:home1",
              "provider",
              from.get(2),
              "segment",
              from.get(4),
              "userHome",
              principalChain.get(0)
            })    		
    	);
    }
    
    //-------------------------------------------------------------------------
    public UserHome getUserHome(
        String user,
        Path from,
        PersistenceManager pm
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
        return (UserHome)pm.getObjectById(userHomePath);
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
    public org.openmdx.security.authentication1.jmi1.Password createPasswordCredential(
    	org.openmdx.security.realm1.jmi1.Subject subject,
        List<String> errors
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(subject);
        String providerName = subject.refGetPath().get(2);        
        org.openmdx.security.authentication1.jmi1.Segment authenticationSegment =
        	(org.openmdx.security.authentication1.jmi1.Segment)pm.getObjectById(
        		new Path("xri://@openmdx*org.openmdx.security.authentication1/provider/" + providerName + "/segment/Root")
        	);
        org.openmdx.security.authentication1.jmi1.Password passwordCredential = pm.newInstance(org.openmdx.security.authentication1.jmi1.Password.class);
        passwordCredential.refInitialize(false, false);
        passwordCredential.setSubject(subject);
        passwordCredential.setLocked(Boolean.FALSE);
        authenticationSegment.addCredential(
        	false,
        	this.getUidAsString(),
        	passwordCredential
        );
        return passwordCredential;
    }
    
    //-------------------------------------------------------------------------
    public short changePassword(
    	org.openmdx.security.authentication1.jmi1.Password passwordCredential,
        String oldPassword,
        String password
    ) {
        if(password == null) {
            return MISSING_NEW_PASSWORD;
        }
    	PersistenceManager pm = JDOHelper.getPersistenceManager(passwordCredential);
    	PersistenceManager pmRoot = null;
    	org.openmdx.security.authentication1.jmi1.Password passwordCredentialByRoot =  null;
    	if(JDOHelper.isNew(passwordCredential)) {
    	    passwordCredentialByRoot = passwordCredential;
    	}
    	else {
        	pmRoot = pm.getPersistenceManagerFactory().getPersistenceManager(SecurityKeys.ROOT_PRINCIPAL, null);
        	passwordCredentialByRoot = 
        		(org.openmdx.security.authentication1.jmi1.Password)pmRoot.getObjectById(
        			passwordCredential.refGetPath()
        		);
    	}
    	try {
    		Configuration config = (Configuration)pm.getUserObject(Configuration.class.getSimpleName());
    		if(pmRoot != null) {
    			pmRoot.currentTransaction().begin();
    		}
	    	passwordCredentialByRoot.change(
	    		((org.openmdx.security.authentication1.jmi1.Authentication1Package)passwordCredentialByRoot.refImmediatePackage()).createPasswordChangeParams(
		    		oldPassword == null ? null : this.getPasswordDigest(oldPassword, config.getPasswordEncodingAlgorithm()),
		    		password == null ? null : this.getPasswordDigest(password, config.getPasswordEncodingAlgorithm())
		    	)
	    	);
	    	if(pmRoot != null) {
	    		pmRoot.currentTransaction().commit();
	    	}
    	}
        catch(Exception e) {
        	ServiceException e0 = new ServiceException(e);
        	if(e0.getCause(OpenCrxException.DOMAIN).getExceptionCode() == BasicException.Code.ASSERTION_FAILURE) {
        		return OLD_PASSWORD_VERIFICATION_MISMATCH;
        	}
        	else {
	        	SysLog.warning(e.getMessage(), e.getCause());
	            return CAN_NOT_CHANGE_PASSWORD;
        	}
        }
        return CHANGE_PASSWORD_OK;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Default implementation does not enforce a password policy.
     */
    public boolean testPasswordPolicy(
        String password
    ) {
    	return true;
    }
    
    //-------------------------------------------------------------------------
    public short changePassword(
        UserHome userHome,
        String oldPassword,
        String newPassword,
        String newPasswordVerification
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
        if(newPassword == null) {
            return MISSING_NEW_PASSWORD;
        }
        if(newPasswordVerification == null) {
            return MISSING_NEW_PASSWORD_VERIFICATION;
        }
        if(!newPassword.equals(newPasswordVerification)) {
            return PASSWORD_VERIFICATION_MISMATCH;
        }
        if(!this.testPasswordPolicy(newPassword)) {
        	return PASSWORD_POLICY_VIOLATION;
        }
        // Check permission
    	List<String> principalChain = UserObjects.getPrincipalChain(pm);
        String requestingPrincipalName = !principalChain.isEmpty() ? 
        	principalChain.get(0) : 
        		"guest";
        // make sure that the requesting principal changes the password of its
        // own user home (qualifier of user home matches the principal). If yes,
        // execute changePassword as segment administrator. If not, execute it as
        // requesting principal. In this case the principal must have enough permissions
        // to create a password credential and update the principal.
        String segmentName = userHome.refGetPath().get(4);
        boolean requestingPrincipalOwnsUserHome = userHome.refGetPath().getBase().equals(requestingPrincipalName);
        boolean requestingPrincipalIsAdmin = requestingPrincipalName.equals(
        	SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName
        );
        if(!requestingPrincipalOwnsUserHome && !requestingPrincipalIsAdmin) {
        	return CAN_NOT_CHANGE_PASSWORD;
        }
        boolean verifyOldPassword = !requestingPrincipalIsAdmin;
        if(verifyOldPassword && oldPassword == null) {
            return MISSING_OLD_PASSWORD;
        }        
        // qualifier of user home is the principal name
        String principalName = userHome.refGetPath().getBase();        
        // get principal
        org.opencrx.security.realm1.jmi1.Principal principal = null;
        try {
            principal = (org.opencrx.security.realm1.jmi1.Principal)pm.getObjectById(
            	SecureObject.getInstance().getLoginRealmIdentity(
            		userHome.refGetPath().get(2)
            	).getDescendant(new String[]{"principal", principalName})
            );
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            SysLog.warning(e0.getMessage(), e0.getCause());
            return CAN_NOT_RETRIEVE_REQUESTED_PRINCIPAL;
        }
        return this.changePassword(
            (org.openmdx.security.authentication1.jmi1.Password)principal.getCredential(),
            verifyOldPassword ? oldPassword : null,
            newPassword
        );
    }
    
    //-------------------------------------------------------------------------
    public UserHome createUserHome(
        org.openmdx.security.realm1.jmi1.Realm realm,
        Contact contact,
        org.opencrx.security.realm1.jmi1.PrincipalGroup primaryGroup,
        String principalName,
        List<org.openmdx.security.realm1.jmi1.Group> requiredGroups,
        boolean isAdministrator,
        String initialPassword,
        String initialPasswordVerification,
        List<String> errors,
        PersistenceManager pmRoot
    ) throws ServiceException {
        if(principalName == null) {
            errors.add("missing principal name");
            return null;                        
        }
        if(contact == null) {
            errors.add("missing contact");
            return null;            
        }
        if(!this.testPasswordPolicy(initialPassword)) {
        	errors.add("password policy violation");
        	return null;
        }
        PersistenceManager pm = JDOHelper.getPersistenceManager(realm); 
        String providerName = contact.refGetPath().get(2);
        String segmentName = contact.refGetPath().get(4);
        Path loginRealmIdentity = SecureObject.getInstance().getLoginRealmIdentity(providerName);   
        org.openmdx.security.realm1.jmi1.Realm loginRealm = 
        	(org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(
        		loginRealmIdentity
        	);
        // Get login principal. Get subject and set password credential
        
        // --- BEGIN pmRoot
        org.openmdx.security.realm1.jmi1.Subject subject = null;
        boolean rootUowIsActive = pmRoot.currentTransaction().isActive();
        if(!rootUowIsActive) {        	
        	pmRoot.currentTransaction().begin();
        }
        try {
            // Create login principal and subject on-demand. Create principal
            // and subject using the importLoginPrincipals() operation. Prepare
            // an import stream containing the principal and subject to be imported
            QuotaByteArrayOutputStream item = new QuotaByteArrayOutputStream(UserHomes.class.getName());
            PrintWriter pw = new PrintWriter(item);
            pw.println("Subject;" + principalName + ";" + contact.getFullName());
            pw.println("Principal;" + principalName + ";" + segmentName + "\\\\" + principalName + ";" + principalName + ";Users");
            pw.close();
            Admin.getInstance().importLoginPrincipals(
                (org.opencrx.kernel.admin1.jmi1.Segment)pmRoot.getObjectById(
                	new Path("xri://@openmdx*org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root")
                ), 
                item.toByteArray()
            ); 
            // Get login principal and subject
            org.openmdx.security.realm1.jmi1.Principal loginPrincipal = loginRealm.getPrincipal(principalName);
            if(loginPrincipal.getSubject() == null) {
                errors.add("Undefined subject for principal " + principalName);
                return null;
            }
            subject = loginPrincipal.getSubject();            
            // Create password credential
            if((initialPassword != null) && (initialPassword.length() > 0)) {
            	org.openmdx.security.authentication1.jmi1.Password passwordCredential = this.createPasswordCredential(
                    subject,
                    errors
                );
                if(passwordCredential == null) {
                    return null;
                }            
                // Set initial password
                this.changePassword(
                    passwordCredential,
                    null,
                    initialPassword
                );
                // Update principal's credential
                loginPrincipal.setCredential(passwordCredential);
            }
            if((loginPrincipal.getCredential() == null)) {
                errors.add("No credential specified for principal " + principalName);                
            }
        }
        catch(Exception e) {
            ServiceException e1 = new ServiceException(e);
            SysLog.warning(e1.getMessage(), e1.getCause());
            errors.add("can not retrieve principal " + principalName + " in realm " + loginRealmIdentity);
            errors.add("reason is " + e.getMessage());
            return null;
        }
        // Add User 
        org.openmdx.security.realm1.jmi1.Realm realmByRoot = 
        	(org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(realm.refGetPath());
        org.opencrx.security.realm1.jmi1.User user = (org.opencrx.security.realm1.jmi1.User)Admin.getInstance().createPrincipal(
            principalName + "." + SecurityKeys.USER_SUFFIX,
            null,
            realmByRoot,
            PrincipalType.USER,
            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
            subject
        );
        org.opencrx.security.realm1.jmi1.PrincipalGroup groupAdministrators = 
        	(org.opencrx.security.realm1.jmi1.PrincipalGroup)realmByRoot.getPrincipal(SecurityKeys.USER_GROUP_ADMINISTRATORS);
        org.opencrx.security.realm1.jmi1.PrincipalGroup groupUsers = 
        	(org.opencrx.security.realm1.jmi1.PrincipalGroup)realmByRoot.getPrincipal(SecurityKeys.USER_GROUP_USERS);
        List<org.openmdx.security.realm1.jmi1.Group> groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
        if(isAdministrator) {
        	groups.add(groupAdministrators);
        	groups.add(user);
        	groups.add(groupUsers);
        }
        else {
        	groups.add(groupUsers);
        	groups.add(user);
        }
        if(requiredGroups != null) {
        	groups.addAll(requiredGroups);
        }
        // Add principal
        Admin.getInstance().createPrincipal(
            principalName, 
            null,
            realmByRoot,
            PrincipalType.PRINCIPAL,
            groups,
            subject
        );
        // Add principal to Root realm (each principal should be registered
        // in the Root realm because the Root segment provides data common
        // to all segments, e.g. code tables
        Admin.getInstance().createPrincipal(
            principalName, 
            null,
            (org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(realm.refGetPath().getParent().getChild("Root")),
            PrincipalType.PRINCIPAL,
            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
            subject
        );
        // Add user principal to Root realm 
        Admin.getInstance().createPrincipal(
            principalName + "." + SecurityKeys.USER_SUFFIX,
            null,
            (org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(realm.refGetPath().getParent().getChild("Root")),
            PrincipalType.USER,
            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
            subject
        );
        initialPassword = initialPassword == null ? "" : initialPassword;
        initialPasswordVerification = initialPasswordVerification == null ? "" : initialPasswordVerification;
        if(!initialPassword.equals(initialPasswordVerification)) {
            errors.add("the passwords you typed do not match");
            return null;
        }
        if(!rootUowIsActive) {
        	pmRoot.currentTransaction().commit();
        }
        
        //--- END pmRoot
        
        /**
         * User home
         */
        user = (org.opencrx.security.realm1.jmi1.User)pm.getObjectById(user.refGetPath());
        groupAdministrators = (org.opencrx.security.realm1.jmi1.PrincipalGroup)pm.getObjectById(groupAdministrators.refGetPath());
        org.opencrx.kernel.home1.jmi1.Segment userHomeSegment = 
        	(org.opencrx.kernel.home1.jmi1.Segment)pm.getObjectById(
        		 new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName)
        	);        
        UserHome userHome = userHomeSegment.getUserHome(principalName);
        if(userHome != null) {
        	if(primaryGroup != null) {
        		userHome.setPrimaryGroup(primaryGroup);
        	}
        }
        else {
            userHome = pm.newInstance(UserHome.class);
            userHome.refInitialize(false, false);
            userHome.setContact(contact);
            // owning user of home is user itself
            userHome.setOwningUser(user);
            // owning group of home is segment administrator
            userHome.getOwningGroup().add(groupAdministrators);
            // access level delete
            userHome.setAccessLevelDelete(
                new Short((short)0)
            );
            userHome.setAccessLevelUpdate(
            	new Short(SecurityKeys.ACCESS_LEVEL_BASIC)
            );
            userHomeSegment.addUserHome(
            	false,
            	principalName,
            	userHome
            );
            Properties userSettings = this.getSettings(userHome);
            this.applyUserSettings(
            	userHome, 
            	0, // currentPerspective
            	userSettings, 
            	true, // createUserHome is always invoked as admin
            	true, // storeSettings 
            	primaryGroup,
            	null, // fTimezone, 
            	"1", // fStoreSettingsOnLogoff
            	null, // fDefaultEmailAccount 
            	"[" + providerName + ":" + segmentName + "]", 
            	null, // fWebAccessUrl 
            	"8", // fTopNavigationShowMax
            	true, // fShowTopNavigationSublevel
            	null, // fRootObjects 
            	null // fSubscriptions
            );
        }
        return userHome;
    }

    //-----------------------------------------------------------------------
    private Contact retrieveContact(
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        String aliasName,
        String fullName
    ) {
        try {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
        	AccountQuery accountQuery = (AccountQuery)pm.newQuery(Account.class);
            if(!"-".equals(aliasName)) {
            	accountQuery.thereExistsAliasName().equalTo(aliasName);
            }
            else if(!"-".equals(fullName)) {
            	accountQuery.thereExistsFullName().equalTo(fullName);
            }
            List<Account> accounts = accountSegment.getAccount(accountQuery);
            if(!accounts.isEmpty()) {
                return (Contact)accounts.iterator().next();
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }
        
    //-------------------------------------------------------------------------
    public String importUsers(
        org.opencrx.kernel.home1.jmi1.Segment homeSegment,
        byte[] item
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(homeSegment);
        BufferedReader reader;
        try {
	        reader = new BufferedReader(
	            new InputStreamReader(new ByteArrayInputStream(item), "UTF-8")
	        );
        }
        catch (UnsupportedEncodingException e) {
        	throw new ServiceException(e);
        }
        org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(
        	SecureObject.getRealmIdentity(homeSegment.refGetPath().get(2), homeSegment.refGetPath().get(4))
        );
        org.opencrx.kernel.account1.jmi1.Segment accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById( 
        	new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + homeSegment.refGetPath().get(2) + "/segment/" + homeSegment.refGetPath().get(4))
        );
        int nCreatedUsers = 0;
        int nFailedUsersNoPrimaryGroup = 0;
        int nFailedUsersNoContact = 0;
        int nFailedUsersOther = 0;
        int nExistingUsers = 0;
        try {
            while(reader.ready()) {
                String l = reader.readLine();
                if(l.indexOf("User;") >= 0) {
                    StringTokenizer t = new StringTokenizer(l, ";");
                    t.nextToken();
                    String principalName = t.nextToken();
                    String accountAlias = t.nextToken();
                    String accountFullName = t.nextToken();
                    String primaryGroupName = t.nextToken();
                    String password = t.nextToken();
                    String groups = t.hasMoreTokens() ? t.nextToken() : "";
                    UserHome userHome = null;
                    try {
                    	userHome = homeSegment.getUserHome(principalName);
                    }
                    catch(Exception e) {}
                    if(userHome == null) {
                        try {
                            Contact contact = this.retrieveContact(
                            	accountSegment, 
                            	accountAlias, 
                            	accountFullName
                            );                            
                            if(contact != null) {
                            	org.opencrx.security.realm1.jmi1.PrincipalGroup primaryGroup = null;
                                try {
                                	if("-".equals(primaryGroupName)) {
                                		primaryGroup = (org.opencrx.security.realm1.jmi1.PrincipalGroup)realm.getPrincipal(primaryGroupName);
                                	}
                                }
                                catch(Exception e) {}
                                List<String> errors = new ArrayList<String>();                                    
                                // Get groups
                                List<org.openmdx.security.realm1.jmi1.Group> isMemberOf = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
                                StringTokenizer g = new StringTokenizer(groups, ",");
                                while(g.hasMoreTokens()) {
                                	org.openmdx.security.realm1.jmi1.Group group = null;
                                	try {
                                		group = (org.openmdx.security.realm1.jmi1.Group)realm.getPrincipal(g.nextToken());
                                		isMemberOf.add(group);
                                	}
                                	catch(Exception e) {}
                                }                                    
                                this.createUserHome(
                                    realm,
                                    contact,
                                    primaryGroup, // principalName.Group will be created, if null 
                                    principalName,
                                    isMemberOf,
                                    false, 
                                    password, 
                                    password, 
                                    errors,
                                    pm
                                );
                                if(errors.isEmpty()) {
                                    nCreatedUsers++;
                                }
                                else {
                                    nFailedUsersOther++;
                                }
                            }
                            else {
                            	SysLog.info("Contact " + accountAlias + "/" + accountFullName + " for user " + principalName + " not found");
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
    public ObjectFinder searchBasic(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        String searchExpression
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
        return this.searchAdvanced(
            userHome, 
            allWords.toString().trim(), 
            atLeastOneOfTheWords.toString().trim(), 
            withoutWords.toString().trim()
        );
    }
    
    //-------------------------------------------------------------------------
    public ObjectFinder searchAdvanced(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        String allWords,
        String atLeastOneOfTheWords,
        String withoutWords
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);    	
        org.opencrx.kernel.home1.jmi1.ObjectFinder objectFinder = pm.newInstance(org.opencrx.kernel.home1.jmi1.ObjectFinder.class);
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
            this.getUidAsString(), 
            objectFinder
        );
        return objectFinder;
    }

    //-----------------------------------------------------------------------
    public String getWebAccessUrl(
        UserHome userHome
    ) {
        Path userHomeIdentity = userHome.refGetPath();
        return userHome.getWebAccessUrl() == null ? 
            "http://localhost/opencrx-core-" + userHomeIdentity.get(2) + "/" + WebKeys.SERVLET_NAME : 
            userHome.getWebAccessUrl() + "/" + WebKeys.SERVLET_NAME;        
    }

    //-------------------------------------------------------------------------
    /**
     * Initializes a user's home and settings.
     */
    public void applyUserSettings(
    	org.opencrx.kernel.home1.jmi1.UserHome userHome,
    	int currentPerspective,
    	Properties userSettings,
    	boolean runAsAdministrator,
    	boolean storeSettings,
    	org.opencrx.security.realm1.jmi1.PrincipalGroup primaryGroup,    	
    	String fTimezone,
    	String fStoreSettingsOnLogoff,
    	String fDefaultEmailAccount,
    	String fSendmailSubjectPrefix,
    	String fWebAccessUrl,
    	String fTopNavigationShowMax,
    	Boolean fShowTopNavigationSublevel,
    	List<String> fRootObjects,
    	Map<String,String> fSubscriptions
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome); 
    	String providerName = userHome.refGetPath().get(2);
    	String segmentName = userHome.refGetPath().get(4);
    	String principalName = userHome.refGetPath().getBase();
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
    		(org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName)
    		);
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
    		(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", providerName, "segment", segmentName)
    		);
    	org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = 
    		(org.opencrx.kernel.workflow1.jmi1.Segment)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.workflow1").getDescendant("provider", providerName, "segment", segmentName)
    		);    	
    	org.opencrx.kernel.document1.jmi1.Segment documentSegment = 
    		(org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.document1").getDescendant("provider", providerName, "segment", segmentName)
    		);
    	if(fTimezone != null) {
    		userSettings.setProperty(UserSettings.TIMEZONE_NAME, fTimezone);
    	}
		userHome.setStoreSettingsOnLogoff(
			Boolean.valueOf(fStoreSettingsOnLogoff == null ? "false" :"true")
		);
		if(fWebAccessUrl != null) {
			userHome.setWebAccessUrl(fWebAccessUrl);
		}
		if(fSendmailSubjectPrefix != null) {
			userHome.setSendMailSubjectPrefix(fSendmailSubjectPrefix);
		}
		// Email account
		EMailAccountQuery emailAccountQuery = (EMailAccountQuery)pm.newQuery(EMailAccount.class);
		emailAccountQuery.thereExistsIsActive().isTrue();
		emailAccountQuery.thereExistsIsDefault().isTrue();
		List<EMailAccount> emailAccounts = userHome.getEMailAccount(emailAccountQuery);
		EMailAccount defaultEmailAccount = emailAccounts.isEmpty() ?
			null :
				emailAccounts.iterator().next();
		if(
			(defaultEmailAccount == null) &&
			(fDefaultEmailAccount != null) &&
			(fDefaultEmailAccount.length() > 0)
		) {
			defaultEmailAccount = pm.newInstance(EMailAccount.class);
			defaultEmailAccount.refInitialize(false, false);
			defaultEmailAccount.setDefault(Boolean.TRUE);
			defaultEmailAccount.setActive(Boolean.TRUE);
			defaultEmailAccount.setName(fDefaultEmailAccount);
			userHome.addEMailAccount(
				false,
				Activities.getInstance().getUidAsString(),
				defaultEmailAccount
			);
		}
		else if(
			(defaultEmailAccount != null) &&
			((fDefaultEmailAccount == null) ||
			(fDefaultEmailAccount.length() == 0))
		) {
			defaultEmailAccount.refDelete();
		}
		else if(defaultEmailAccount != null) {
			defaultEmailAccount.setName(fDefaultEmailAccount);
		}
		// Root objects
		for(int i = 0; i < 20; i++) {
			String state = (fRootObjects != null) && (i < fRootObjects.size()) ? 
				fRootObjects.get(i) : 
				"1";
			userSettings.setProperty(
				UserSettings.ROOT_OBJECT_STATE + (currentPerspective == 0 ? "" : "[" + Integer.toString(currentPerspective) + "]") + "." + i + ".State",
				state == null ? "0" : state
			);
		}
		// Show max items in top navigation
		if(fTopNavigationShowMax != null) {
			userSettings.setProperty(
				UserSettings.TOP_NAVIGATION_SHOW_MAX,
				fTopNavigationShowMax
			);
		}
		// Show sublevels
		if(fShowTopNavigationSublevel != null) {
			userSettings.setProperty(
				UserSettings.TOP_NAVIGATION_SHOW_SUBLEVEL,
				Boolean.toString(fShowTopNavigationSublevel)
			);			
		}
		// If running as segment admin set ACLs of created objects
		org.opencrx.security.realm1.jmi1.PrincipalGroup privatePrincipalGroup = null;
		// Get principal group with name <principal>.Group. This is the private group of the owner of the user home page
		org.openmdx.security.realm1.jmi1.Realm realm = org.opencrx.kernel.backend.SecureObject.getInstance().getRealm(
			pm,
			providerName,
			segmentName
		);
		privatePrincipalGroup = (org.opencrx.security.realm1.jmi1.PrincipalGroup)org.opencrx.kernel.backend.SecureObject.getInstance().findPrincipal(
			principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
			realm
		);
		if(runAsAdministrator) {
			if(privatePrincipalGroup == null) {
				privatePrincipalGroup = pm.newInstance(PrincipalGroup.class);
				privatePrincipalGroup.refInitialize(false, false);
				privatePrincipalGroup.setDescription(segmentName + "\\\\" + principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX);
				realm.addPrincipal(
					false,
					principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
					privatePrincipalGroup
				);
			}
			// Set UserHome's primary group
			if(primaryGroup == null) {
				userHome.setPrimaryGroup(privatePrincipalGroup);
			} else {
				userHome.setPrimaryGroup(primaryGroup);				
			}
			org.openmdx.security.realm1.jmi1.Principal principal = null;
			try {
				principal = org.opencrx.kernel.backend.SecureObject.getInstance().findPrincipal(
					principalName,
					realm
				);
				// Validate that user is member of <principal>.Group
				if(!principal.getIsMemberOf().contains(privatePrincipalGroup)) {
					principal.getIsMemberOf().add(privatePrincipalGroup);
				}
				// Validate that user is member of group 'Public'
				org.opencrx.security.realm1.jmi1.PrincipalGroup publicGroup = (org.opencrx.security.realm1.jmi1.PrincipalGroup)org.opencrx.kernel.backend.SecureObject.getInstance().findPrincipal(
					"Public",
					realm
				);
				if(publicGroup != null && !principal.getIsMemberOf().contains(publicGroup)) {
					principal.getIsMemberOf().add(publicGroup);
				}
			} catch(Exception e) {}
			// Validate that subject of <principal>.Group is the same as of <principal>
			if((principal != null) && (privatePrincipalGroup != null)) {
				privatePrincipalGroup.setSubject(principal.getSubject());
			}
		}
		// Private activity tracker
		org.opencrx.kernel.activity1.jmi1.ActivityTracker privateTracker = null;
		try {
			privateTracker = (org.opencrx.kernel.activity1.jmi1.ActivityTracker)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityTracker/" + principalName)
			);
		}
		catch(Exception e) {}
		if(privateTracker == null) {
			privateTracker = pm.newInstance(ActivityTracker.class);
			privateTracker.refInitialize(false, false);
			privateTracker.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			if(privatePrincipalGroup != null) {
				privateTracker.getOwningGroup().clear();
				privateTracker.getOwningGroup().add(privatePrincipalGroup);
			}
			activitySegment.addActivityTracker(
				false,
				principalName,
				privateTracker
			);
		}
		// Private Incidents creator
		org.opencrx.kernel.activity1.jmi1.ActivityCreator privateIncidentsCreator = null;
		try {
			privateIncidentsCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + principalName)
			);
		}
		catch(Exception e) {}
		if(privateIncidentsCreator == null) {
			privateIncidentsCreator = pm.newInstance(ActivityCreator.class);
			privateIncidentsCreator.refInitialize(false, false);
			privateIncidentsCreator.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			privateIncidentsCreator.getActivityGroup().add(privateTracker);
			privateIncidentsCreator.setActivityType(
				org.opencrx.kernel.backend.Activities.getInstance().findActivityType(
					"Bugs + Features", 
					activitySegment, 
					pm
				)
			);
			if(privatePrincipalGroup != null) {
				privateIncidentsCreator.getOwningGroup().clear();
				privateIncidentsCreator.getOwningGroup().add(privatePrincipalGroup);
			}
			activitySegment.addActivityCreator(
				false,
				principalName,
				privateIncidentsCreator
			);
		}
		if(privateIncidentsCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
			privateIncidentsCreator.setIcalType(ICalendar.ICAL_TYPE_VEVENT);			
		}
		// Private E-Mails creator
		org.opencrx.kernel.activity1.jmi1.ActivityCreator privateEMailsCreator = null;
		try {
			privateEMailsCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + principalName + "~E-Mails")
			);
		}
		catch(Exception e) {}
		if(privateEMailsCreator == null) {
			privateEMailsCreator = pm.newInstance(ActivityCreator.class);
			privateEMailsCreator.refInitialize(false, false);
			privateEMailsCreator.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX + "~E-Mails");
			privateEMailsCreator.getActivityGroup().add(privateTracker);
			privateEMailsCreator.setActivityType(
				org.opencrx.kernel.backend.Activities.getInstance().findActivityType(
					"E-Mails", 
					activitySegment, 
					pm
				)
			);
			if(privatePrincipalGroup != null) {
				privateEMailsCreator.getOwningGroup().clear();
				privateEMailsCreator.getOwningGroup().add(privatePrincipalGroup);
			}
			activitySegment.addActivityCreator(
				false,
				principalName + "~E-Mails",
				privateEMailsCreator
			);
		}
		if(privateEMailsCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
			privateEMailsCreator.setIcalType(ICalendar.ICAL_TYPE_VEVENT);			
		}
		// Private Tasks creator
		org.opencrx.kernel.activity1.jmi1.ActivityCreator privateTasksCreator = null;
		try {
			privateTasksCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + principalName + "~Tasks")
			);
		}
		catch(Exception e) {}
		if(privateTasksCreator == null) {
			privateTasksCreator = pm.newInstance(ActivityCreator.class);
			privateTasksCreator.refInitialize(false, false);
			privateTasksCreator.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX + "~Tasks");
			privateTasksCreator.getActivityGroup().add(privateTracker);
			privateTasksCreator.setActivityType(
				org.opencrx.kernel.backend.Activities.getInstance().findActivityType(
					"Tasks", 
					activitySegment, 
					pm
				)
			);
			if(privatePrincipalGroup != null) {
				privateTasksCreator.getOwningGroup().clear();
				privateTasksCreator.getOwningGroup().add(privatePrincipalGroup);
			}
			activitySegment.addActivityCreator(
				false,
				principalName + "~Tasks",
				privateTasksCreator
			);
		}
		if(privateTasksCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
			privateTasksCreator.setIcalType(ICalendar.ICAL_TYPE_VTODO);			
		}
		// Private Meetings creator
		org.opencrx.kernel.activity1.jmi1.ActivityCreator privateMeetingsCreator = null;
		try {
			privateMeetingsCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + principalName + "~Meetings")
			);
		}
		catch(Exception e) {}
		if(privateMeetingsCreator == null) {
			privateMeetingsCreator = pm.newInstance(ActivityCreator.class);
			privateMeetingsCreator.refInitialize(false, false);
			privateMeetingsCreator.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX + "~Meetings");
			privateMeetingsCreator.getActivityGroup().add(privateTracker);
			privateMeetingsCreator.setActivityType(
				org.opencrx.kernel.backend.Activities.getInstance().findActivityType(
					"Meetings", 
					activitySegment, 
					pm
				)
			);
			if(privatePrincipalGroup != null) {
				privateMeetingsCreator.getOwningGroup().clear();
				privateMeetingsCreator.getOwningGroup().add(privatePrincipalGroup);
			}
			activitySegment.addActivityCreator(
				false,
				principalName + "~Meetings",
				privateMeetingsCreator
			);
		}
		if(privateMeetingsCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
			privateMeetingsCreator.setIcalType(ICalendar.ICAL_TYPE_VEVENT);			
		}
		// Set default creator on tracker
		privateTracker.setDefaultCreator(privateIncidentsCreator);
		// Resource
		org.opencrx.kernel.activity1.jmi1.Resource resource = Activities.getInstance().findResource(
			activitySegment,
			userHome
		);
		if(resource == null) {
			resource = pm.newInstance(Resource.class);
			resource.refInitialize(false, false);
			if(userHome.getContact() != null) {
				resource.setName(userHome.getContact().getFullName());
				resource.setContact(userHome.getContact());
			}
			else {
				resource.setName(principalName);
			}
			activitySegment.addResource(
				false,
				principalName,
				resource
			);
		}
		// Account group
		org.opencrx.kernel.account1.jmi1.Group privateAccountGroup = null;
		try {
			privateAccountGroup = (org.opencrx.kernel.account1.jmi1.Group)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName + "/account/" + principalName + Activities.PRIVATE_GROUP_SUFFIX)
			);
		} catch(Exception e) {}		
		if(privateAccountGroup == null) {
			privateAccountGroup = pm.newInstance(org.opencrx.kernel.account1.jmi1.Group.class);
			privateAccountGroup.refInitialize(false, false);
			privateAccountGroup.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			if(privatePrincipalGroup != null) {
				privateAccountGroup.getOwningGroup().clear();
				privateAccountGroup.getOwningGroup().add(privatePrincipalGroup);
			}			
			accountSegment.addAccount(
				principalName + Activities.PRIVATE_GROUP_SUFFIX,
				privateAccountGroup
			);
		}
		// Document folder
		org.opencrx.kernel.document1.jmi1.DocumentFolder privateDocumentFolder = null;
		try {
			privateDocumentFolder = (org.opencrx.kernel.document1.jmi1.DocumentFolder)pm.getObjectById(
				new Path("xri://@openmdx*org.opencrx.kernel.document1").getDescendant("provider", providerName, "segment", segmentName, "folder", principalName + Documents.PRIVATE_DOCUMENTS_FOLDER_SUFFIX)
			);
		} catch(Exception e) {}		
		if(privateDocumentFolder == null) {
			privateDocumentFolder = pm.newInstance(org.opencrx.kernel.document1.jmi1.DocumentFolder.class);
			privateDocumentFolder.refInitialize(false, false);
			privateDocumentFolder.setName(principalName + Documents.PRIVATE_DOCUMENTS_FOLDER_SUFFIX);
			if(privateDocumentFolder != null) {
				privateDocumentFolder.getOwningGroup().clear();
				privateDocumentFolder.getOwningGroup().add(privatePrincipalGroup);
			}
			documentSegment.addFolder(
				principalName + Documents.PRIVATE_DOCUMENTS_FOLDER_SUFFIX,
				privateDocumentFolder
			);
		}
		// AirSync profile
		org.opencrx.kernel.home1.jmi1.AirSyncProfile airSyncProfile = null;
		try {
			airSyncProfile = (org.opencrx.kernel.home1.jmi1.AirSyncProfile)pm.getObjectById(
				userHome.refGetPath().getDescendant("syncProfile", "AirSync")
			);
		} catch(Exception e) {}
		if(airSyncProfile == null) {
			airSyncProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.AirSyncProfile.class);
			airSyncProfile.refInitialize(false, false);
			airSyncProfile.setName("AirSync");
			userHome.addSyncProfile(
				false, 
				"AirSync",
				airSyncProfile
			);
			ActivityGroupCalendarFeed calendarFeed = pm.newInstance(ActivityGroupCalendarFeed.class);
			calendarFeed.refInitialize(false, false);
			calendarFeed.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			calendarFeed.setDescription("Calendar Feed");
			calendarFeed.setActivityGroup(privateTracker);
			calendarFeed.setActive(true);
			calendarFeed.setAllowAddDelete(true);
			calendarFeed.setAllowChange(true);
			airSyncProfile.addFeed(
				this.getUidAsString(), 
				calendarFeed
			);
			ContactsFeed contactsFeed = pm.newInstance(ContactsFeed.class);
			contactsFeed.refInitialize(false, false);
			contactsFeed.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			contactsFeed.setDescription("Contacts Feed");
			contactsFeed.setAccountGroup(privateAccountGroup);
			contactsFeed.setActive(true);
			contactsFeed.setAllowAddDelete(true);
			contactsFeed.setAllowChange(true);
			airSyncProfile.addFeed(
				this.getUidAsString(), 
				contactsFeed
			);
			DocumentFeed notesFeed = pm.newInstance(DocumentFeed.class);
			notesFeed.refInitialize(false, false);
			notesFeed.setName(principalName + Documents.PRIVATE_DOCUMENTS_FOLDER_SUFFIX);
			notesFeed.setDescription("Notes Feed");
			notesFeed.setDocumentFolder(privateDocumentFolder);
			notesFeed.setActive(true);
			notesFeed.setAllowAddDelete(true);
			notesFeed.setAllowChange(true);
			airSyncProfile.addFeed(
				this.getUidAsString(), 
				notesFeed
			);
		}
		// Card profile
		org.opencrx.kernel.home1.jmi1.CardProfile cardProfile = null;
		try {
			cardProfile = (org.opencrx.kernel.home1.jmi1.CardProfile)pm.getObjectById(
				userHome.refGetPath().getDescendant("syncProfile", "Card")
			);
		} catch(Exception e) {}
		if(cardProfile == null) {
			cardProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.CardProfile.class);
			cardProfile.refInitialize(false, false);
			cardProfile.setName(principalName + "~AddressBook");
			userHome.addSyncProfile(
				false, 
				"Card",
				cardProfile
			);
			ContactsFeed contactsFeed = pm.newInstance(ContactsFeed.class);
			contactsFeed.refInitialize(false, false);
			contactsFeed.setName(principalName + Activities.PRIVATE_GROUP_SUFFIX);
			contactsFeed.setDescription("Contacts Feed");
			contactsFeed.setAccountGroup(privateAccountGroup);
			contactsFeed.setActive(true);
			contactsFeed.setAllowAddDelete(true);
			contactsFeed.setAllowChange(true);
			cardProfile.addFeed(
				this.getUidAsString(), 
				contactsFeed
			);
		}
		// Subscriptions
		Collection<Topic> topics = workflowSegment.getTopic();
		for(Topic topic: topics) {
			SubscriptionQuery query = (SubscriptionQuery)pm.newQuery(Subscription.class);
			query.thereExistsTopic().equalTo(topic);
			List<Subscription> subscriptions = userHome.getSubscription(query);
			org.opencrx.kernel.home1.jmi1.Subscription subscription = null;
			if(subscriptions.isEmpty()) {
				subscription = pm.newInstance(Subscription.class);
				subscription.refInitialize(false, false);
				subscription.setName(topic.getName());
				subscription.setTopic(topic);
				userHome.addSubscription(
					false,
					org.opencrx.kernel.backend.Activities.getInstance().getUidAsString(),
					subscription
				);
			}
			else {
				subscription = subscriptions.iterator().next();
			}
			subscription.getEventType().clear();
			String topicId = topic.refGetPath().getBase();
			subscription.setActive(
				(fSubscriptions != null) && (fSubscriptions.get("topicIsActive-" + topicId) != null)
			);
			if((fSubscriptions != null) && (fSubscriptions.get("topicCreation-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)1));
			}
			if((fSubscriptions != null) && (fSubscriptions.get("topicReplacement-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)3));
			}
			if((fSubscriptions != null) && (fSubscriptions.get("topicRemoval-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)4));
			}
		}
		// Store settings
		if(storeSettings) {
			try {
				QuotaByteArrayOutputStream bsSettings = new QuotaByteArrayOutputStream(UserHomes.class.getName());
				userSettings.store(
					bsSettings,
					"settings of user " + userHome.refMofId()
				);
				bsSettings.close();
				userHome.setSettings(
					bsSettings.toString("UTF-8")
				);
			}
			catch(IOException e) {
				throw new ServiceException(e);
			}
		}
		// Assert owningUser of Resource
		if(runAsAdministrator) {
		    org.opencrx.kernel.base.jmi1.SetOwningUserParams setOwningUserParams = Utils.getBasePackage(pm).createSetOwningUserParams(
		        (short)SecureObject.MODE_RECURSIVE,
		        userHome.getOwningUser()
		    );
			resource.setOwningUser(
			    setOwningUserParams
			);
		}
    }
    
    //-------------------------------------------------------------------------
    public Properties getSettings(
    	UserHome userHome
    ) throws ServiceException {
        Properties settings = new Properties();
        if(userHome.getSettings() != null) {
            try {            	
				settings.load(
					new ByteArrayInputStream(
						userHome.getSettings().getBytes("UTF-8")
					)
				);
            }
            catch(IOException e) {
            	throw new ServiceException(e);
            }
        }
        return settings;
    }
    
    //-------------------------------------------------------------------------
    public String getUserTimezone(
    	Properties settings
    ) throws ServiceException {
    	return settings.getProperty(UserSettings.TIMEZONE_NAME);
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
    public static final short OLD_PASSWORD_VERIFICATION_MISMATCH = 7;
    public static final short PASSWORD_POLICY_VIOLATION = 8;

    public static final short ALERT_STATE_NA = 0;
    public static final short ALERT_STATE_NEW = 1;
    public static final short ALERT_STATE_READ = 2;
    public static final short ALERT_STATE_ACCEPTED = 3;
    public static final short ALERT_STATE_EXPIRED = 4;
    
}

//--- End of File -----------------------------------------------------------

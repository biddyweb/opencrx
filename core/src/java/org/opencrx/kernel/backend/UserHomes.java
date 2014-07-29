/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: UserHomes
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.aop2.Configuration;
import org.opencrx.kernel.backend.Admin.PrincipalType;
import org.opencrx.kernel.backend.ICalendar.ICalClass;
import org.opencrx.kernel.base.jmi1.SetOwningUserParams;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.home1.cci2.EMailAccountQuery;
import org.opencrx.kernel.home1.cci2.SubscriptionQuery;
import org.opencrx.kernel.home1.jmi1.ActivityGroupCalendarFeed;
import org.opencrx.kernel.home1.jmi1.Alert;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.opencrx.kernel.home1.jmi1.DocumentFeed;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.opencrx.kernel.home1.jmi1.Subscription;
import org.opencrx.kernel.home1.jmi1.Timer;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.workflow1.jmi1.Topic;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.io.QuotaByteArrayOutputStream;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.base.persistence.spi.PersistenceManagers;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.UserSettings;
import org.openmdx.portal.servlet.WebKeys;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * UserHomes
 *
 */
public class UserHomes extends AbstractImpl {

	/**
	 * Register this backend.
	 * 
	 */
	public static void register(
	) {
		registerImpl(new UserHomes());
	}
	
	/**
	 * Get registered UserHomes backend.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static UserHomes getInstance(
	) throws ServiceException {
		return getInstance(UserHomes.class);
	}

	/**
	 * Constructor.
	 */
	protected UserHomes(
	) {
		
	}
	
    /**
     * Set alert state to ACCEPTED.
     * 
     * @param alert
     * @throws ServiceException
     */
    public void markAsAccepted(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(AlertState.ACCEPTED.getValue());
    }
    
    /**
     * Set alert state to READ.
     * 
     * @param alert
     * @throws ServiceException
     */
    public void markAsRead(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(AlertState.READ.getValue());
    }
    
    /**
     * Set alert state to NEW.
     * 
     * @param alert
     * @throws ServiceException
     */
    public void markAsNew(
        org.opencrx.kernel.home1.jmi1.Alert alert
    ) throws ServiceException {
        alert.setAlertState(AlertState.NEW.getValue());
    }

    /**
     * Returns the user home segment.
     * 
     * @param pm
     * @param providerName
     * @param segmentName
     * @return
     */
    public org.opencrx.kernel.home1.jmi1.Segment getUserHomeSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.home1.jmi1.Segment) pm.getObjectById(
            new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", segmentName)
        );
    }
    
    /**
     * Refresh items on given user home. Currently these are:
     * <ul>
     *   <li>alerts
     * </ul> 
     * 
     * @param userHome
     * @throws ServiceException
     */
    public void refreshItems(
        org.opencrx.kernel.home1.jmi1.UserHome userHome
    ) throws ServiceException {
        if(userHome.getContact() != null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
        	// Collect non-expired alerts
        	final Date NOW_MINUS_1M = new Date(System.currentTimeMillis() - 30L * 86400L * 1000L);
        	final Date NOW_MINUS_3M = new Date(System.currentTimeMillis() - 90L * 86400L * 1000L);
            AlertQuery alertQuery = (AlertQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.Alert.class);
            alertQuery.alertState().lessThan(AlertState.EXPIRED.getValue());
            alertQuery.orderByCreatedAt().descending();
            List<Path> alertIdentities = new ArrayList<Path>();
            List<Alert> alerts = userHome.getAlert(alertQuery);
            int count = 0;
            for(Alert alert: alerts) {
        		alertIdentities.add(alert.refGetPath());
        		count++;
        		if(count > 200) break;
            }
            // Update state
            Set<Path> references = new HashSet<Path>();
            for(Path alertIdentity: alertIdentities) {
            	Alert alert = (Alert)pm.getObjectById(alertIdentity);
            	Path reference = null;
            	try {
            		reference = alert.getReference().refGetPath();
            	} catch(Exception ignore) {}
            	// Set state to expired if 
            	// * if it is read and older than three months
            	if(
            		alert.getAlertState() == AlertState.READ.getValue() && 
            		alert.getCreatedAt().compareTo(NOW_MINUS_3M) < 0
            	) {
                	alert.setAlertState(AlertState.EXPIRED.getValue());
            	}
            	// Set state to read if
            	// * its reference is not valid or duplicate or
            	// * alert is older than one month
            	else if(
            		(reference == null) || 
            		references.contains(reference) || 
            		(alert.getAlertState() < AlertState.READ.getValue() && alert.getCreatedAt().compareTo(NOW_MINUS_1M) < 0)
            	) {
                	alert.setAlertState(AlertState.READ.getValue());
            	}
            	if(reference != null) {
            		references.add(reference);
            	}
            }
        }
    }
       
    /**
     * Get user home.
     * 
     * @param from
     * @param pm
     * @return
     * @throws ServiceException
     */
    public UserHome getUserHome(
      Path from,
      PersistenceManager pm
    ) throws ServiceException {
    	return this.getUserHome(from, pm, false);
    }
    
    /**
     * Get user home.
     * 
     * @param from
     * @param pm
     * @param useRunAsPrincipal
     * @return
     * @throws ServiceException
     */
    public UserHome getUserHome(
      Path from,
      PersistenceManager pm,
      boolean useRunAsPrincipal
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
              useRunAsPrincipal && principalChain.size() > 1 ? 
            	  principalChain.get(1) : 
            		  principalChain.get(0)
            })
    	);
    }
    
    /**
     * Get user home.
     * 
     * @param user
     * @param from
     * @param pm
     * @return
     * @throws ServiceException
     */
    public UserHome getUserHome(
        String user,
        Path from,
        PersistenceManager pm
    ) throws ServiceException {
    	return this.getUserHome(user, from, pm, false);
    }

    /**
     * Get user home.
     * 
     * @param user
     * @param from
     * @param pm
     * @param useRunAsPrincipal
     * @return
     * @throws ServiceException
     */
    public UserHome getUserHome(
        String user,
        Path from,
        PersistenceManager pm,
        boolean useRunAsPrincipal
    ) throws ServiceException {
    	List<String> principalChain = PersistenceManagers.toPrincipalChain(user);
        if(principalChain.isEmpty()) return null;
        Path userHomePath = new Path(
            new String[]{
              "org:opencrx:kernel:home1",
              "provider",
              from.get(2),
              "segment",
              from.get(4),
              "userHome",
              useRunAsPrincipal && principalChain.size() > 1 ? 
            	  principalChain.get(1) : 
            		  principalChain.get(0)
            }
        );
        return (UserHome)pm.getObjectById(userHomePath);
    }

    /**
     * Get password digest.
     * 
     * @param password
     * @param algorithm
     * @return
     */
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
    
    /**
     * Create a password credential for given subject.
     *  
     * @param subject
     * @param errors
     * @return
     */
    public org.openmdx.security.authentication1.jmi1.Password createPasswordCredential(
    	org.openmdx.security.realm1.jmi1.Subject subject,
        List<String> errors
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(subject);
        String providerName = subject.refGetPath().get(2);        
        org.openmdx.security.authentication1.jmi1.Segment authenticationSegment =
        	(org.openmdx.security.authentication1.jmi1.Segment)pm.getObjectById(
        		new Path("xri://@openmdx*org.openmdx.security.authentication1").getDescendant("provider", providerName, "segment", "Root")
        	);
        org.openmdx.security.authentication1.jmi1.Password passwordCredential = pm.newInstance(org.openmdx.security.authentication1.jmi1.Password.class);
        passwordCredential.setSubject(subject);
        passwordCredential.setLocked(Boolean.FALSE);
        authenticationSegment.addCredential(
        	false,
        	this.getUidAsString(),
        	passwordCredential
        );
        return passwordCredential;
    }
    
    /**
     * Change password.
     * 
     * @param passwordCredential
     * @param oldPassword
     * @param password
     * @return
     */
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
    	} catch(Exception e) {
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
   
    /**
     * Default implementation does not enforce a password policy.
     * 
     * @param password
     * @return
     */
    public boolean testPasswordPolicy(
        String password
    ) {
    	return true;
    }

    /**
     * Change password for given user.
     * 
     * @param userHome
     * @param oldPassword
     * @param newPassword
     * @param newPasswordVerification
     * @return
     */
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
            principal = (org.opencrx.security.realm1.jmi1.Principal)SecureObject.getInstance().findPrincipal(
            	principalName,
        		SecureObject.getInstance().getLoginRealmIdentity(
            		userHome.refGetPath().get(2)
            	),	            	
            	pm
            );
        } catch(Exception e) {
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

    /**
     * Create a new user home.
     * 
     * @param realm
     * @param contact
     * @param primaryGroup
     * @param principalName
     * @param requiredGroups
     * @param isAdministrator
     * @param initialPassword
     * @param initialPasswordVerification
     * @param eMailAddress
     * @param timezone
     * @param errors
     * @return
     * @throws ServiceException
     */
    public UserHome createUserHome(
        org.openmdx.security.realm1.jmi1.Realm realm,
        Contact contact,
        PrincipalGroup primaryGroup,
        String principalName,
        List<org.openmdx.security.realm1.jmi1.Group> requiredGroups,
        boolean isAdministrator,
        String initialPassword,
        String initialPasswordVerification,
        String eMailAddress,
        String timezone,
        List<String> errors
    ) throws ServiceException {
        if(principalName == null) {
            errors.add("ERROR: Missing principal name");
            return null;
        }
        if(contact == null) {
            errors.add("ERROR: Missing contact");
            return null;
        }
        if(!this.testPasswordPolicy(initialPassword)) {
        	errors.add("ERROR: Password policy violation");
        	return null;
        }
        PersistenceManager pm = JDOHelper.getPersistenceManager(realm); 
        String providerName = contact.refGetPath().get(2);
        String segmentName = contact.refGetPath().get(4);
        org.opencrx.security.realm1.jmi1.User user = null;
        PrincipalGroup groupAdministrators = null;
        // --- BEGIN pmRoot
        {
            // Get login principal. Get subject and set password credential
	        org.openmdx.security.realm1.jmi1.Subject subject = null;
    		PersistenceManager pmRoot = pm.getPersistenceManagerFactory().getPersistenceManager(
				SecurityKeys.ROOT_PRINCIPAL,
				null
			);
            Path loginRealmIdentity = SecureObject.getInstance().getLoginRealmIdentity(providerName);
            org.openmdx.security.realm1.jmi1.Realm loginRealm =
            	(org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(
            		loginRealmIdentity
            	);
        	// Test whether principal exists in loginRealm but not in realm. In this case the user 
            // already exists in other realms. Get permission from the user to be added to realm.
            // Send a CreateUserRequest alert to the user in all realms and check for at least one 
            // acceptance.
            if(
            	loginRealm.getPrincipal(principalName) != null &&
            	realm.getPrincipal(principalName) == null
            ) {
            	boolean hasAcceptedCreateUserConfirmations = false;
            	// Check for accepted CreateUserRequests 
            	Set<String> realmNames = new TreeSet<String>();
                org.openmdx.security.realm1.jmi1.Segment realmSegment =  (org.openmdx.security.realm1.jmi1.Segment)pmRoot.getObjectById(loginRealm.refGetPath().getParent().getParent());
                for(org.openmdx.security.realm1.jmi1.Realm aRealm: realmSegment.<org.openmdx.security.realm1.jmi1.Realm>getRealm()) {
                	if(aRealm.getPrincipal(principalName) != null) {
                		Path userHomeIdentity = new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", aRealm.getName(), "userHome", principalName);
                		UserHome userHome = null;
                		try {
                			userHome = (UserHome)pmRoot.getObjectById(userHomeIdentity);
                		} catch(Exception ignore) {}
                		if(userHome != null) {
                			realmNames.add(aRealm.getName());
                			String alertName = "CreateUserConfirmationRequest " + principalName + "@" + realm.refGetPath().getBase();
                    		AlertQuery alertQuery = (AlertQuery)pm.newQuery(Alert.class);
                    		alertQuery.name().equalTo(alertName);
                    		alertQuery.alertState().equalTo(AlertState.ACCEPTED.getValue());
                    		alertQuery.createdAt().greaterThanOrEqualTo(new Date(System.currentTimeMillis() - 86400000L)); // max one day old
                    		List<Alert> alerts = userHome.getAlert(alertQuery);
                    		if(!alerts.isEmpty()) {
                    			hasAcceptedCreateUserConfirmations = true;
                    			break;
                    		}
                		}
            		}
                }
                // Send a CreateUserConfirmationRequest in case none is found 
                if(!hasAcceptedCreateUserConfirmations) {
                	String users = "";
                	for(String realmName: realmNames) {
                		Path userHomeIdentity = new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", realmName, "userHome", principalName);
               			UserHome userHome = (UserHome)pmRoot.getObjectById(userHomeIdentity);
    	            	// Send CreateUserConfirmationRequest to principal
            			Base.getInstance().sendAlert(
            				userHome,
            				principalName, 
            				"CreateUserConfirmationRequest " + principalName + "@" + realm.refGetPath().getBase(),
            				"[%\nMessage from '" + (SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + realm.refGetPath().getBase()) + "': Mark this alert as accepted to allow the creation of user '" + principalName + "' in segment '" + segmentName + "'.\n\nIMPORTANT: ONLY accept if you requested that '" + principalName + "' is to be added to segment '" + segmentName + "'!\n%]", 
            				(short)2, // importance 
            				0, // resendDelayInSeconds
            				null // reference
            			);
            			users +=
            				(users.isEmpty() ? "" : ", ") +
            				principalName + "@" + realmName;
            		}
	            	errors.add("ERROR: Principal already in use. CreateUserConfirmationRequests are sent to users {" + users + "}. Waiting for acceptance.");
	            	pmRoot.close();
	            	return null;
                }
            }
    		pmRoot.currentTransaction().begin();
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
	                	new Path("xri://@openmdx*org.opencrx.kernel.admin1").getDescendant("provider", providerName, "segment", "Root")
	                ),
	                item.toByteArray()
	            );
	            // Get login principal and subject
	            org.openmdx.security.realm1.jmi1.Principal loginPrincipal = loginRealm.getPrincipal(principalName);
	            if(loginPrincipal.getSubject() == null) {
	                errors.add("ERROR: Undefined subject for principal '" + principalName + "'");
	                return null;
	            }
	            subject = loginPrincipal.getSubject();            
	            // Create password credential
	            if((initialPassword != null) && !initialPassword.isEmpty()) {
	            	org.openmdx.security.authentication1.jmi1.Password passwordCredential = this.createPasswordCredential(
	                    subject,
	                    errors
	                );
	                if(passwordCredential == null) {
	                	errors.add("ERROR: Creation of password credential failed for principal '" + principalName + "'");
	                    return null;
	                }
	                // Set initial password
	                short changePasswordStatus = this.changePassword(
	                    passwordCredential,
	                    null,
	                    initialPassword
	                );
	                if(changePasswordStatus != CHANGE_PASSWORD_OK) {
	                	errors.add("ERROR: Changing of password failed with status " + changePasswordStatus);
	                	return null;
	                }
	                // Update principal's credential
	                loginPrincipal.setCredential(passwordCredential);
	            }
	            if((loginPrincipal.getCredential() == null)) {
	                errors.add("ERROR: No credential specified for principal '" + principalName + "'");                
	            }
	        } catch(Exception e) {
	            ServiceException e1 = new ServiceException(e);
	            SysLog.warning(e1.getMessage(), e1.getCause());
	            errors.add("ERROR: Can not retrieve principal '" + principalName + "' in realm '" + segmentName + "'");
	            errors.add("reason is " + e.getMessage());
	            return null;
	        }
	        // Add User 
	        org.openmdx.security.realm1.jmi1.Realm realmByRoot = 
	        	(org.openmdx.security.realm1.jmi1.Realm)pmRoot.getObjectById(realm.refGetPath());
	        user = (org.opencrx.security.realm1.jmi1.User)Admin.getInstance().createPrincipal(
	            principalName + "." + SecurityKeys.USER_SUFFIX,
	            null,
	            realmByRoot,
	            PrincipalType.USER,
	            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
	            subject
	        );
	        groupAdministrators = (PrincipalGroup)realmByRoot.getPrincipal(SecurityKeys.USER_GROUP_ADMINISTRATORS);
	        PrincipalGroup groupUsers = (PrincipalGroup)realmByRoot.getPrincipal(SecurityKeys.USER_GROUP_USERS);
	        List<org.openmdx.security.realm1.jmi1.Group> groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
	        if(isAdministrator) {
	        	groups.add(groupAdministrators);
	        	groups.add(user);
	        	groups.add(groupUsers);
	        } else {
	        	groups.add(groupUsers);
	        	groups.add(user);
	        }
	        if(requiredGroups != null) {
	        	for(org.openmdx.security.realm1.jmi1.Group requiredGroup: requiredGroups) {
	        		groups.add((org.openmdx.security.realm1.jmi1.Group)pmRoot.getObjectById(requiredGroup.refGetPath()));
	        	}
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
        	pmRoot.currentTransaction().commit();
        }
        //--- END pmRoot
        /**
         * User home
         */
        Path userHomeIdentity = null;
        {
    		PersistenceManager pmAdmin = pm.getPersistenceManagerFactory().getPersistenceManager(
    			UserObjects.getPrincipalChain(pm).toString(),
				null
			);
	        org.opencrx.kernel.home1.jmi1.Segment userHomeSegment = this.getUserHomeSegment(
	        	pmAdmin, 
	        	providerName, 
	        	segmentName
	        );
	        UserHome userHome = null;
    		// --- BEGIN pmAdmin
    		{
	    		pmAdmin.currentTransaction().begin();
		        user = (org.opencrx.security.realm1.jmi1.User)pmAdmin.getObjectById(user.refGetPath());
		        groupAdministrators = (PrincipalGroup)pmAdmin.getObjectById(groupAdministrators.refGetPath());
		        primaryGroup = primaryGroup == null ? null : (PrincipalGroup)pmAdmin.getObjectById(primaryGroup.refGetPath());
		        userHome = userHomeSegment.getUserHome(principalName);
		        if(userHome != null) {
		        	if(primaryGroup != null) {
		        		userHome.setPrimaryGroup(primaryGroup);
		        	}
		        	userHomeIdentity = userHome.refGetPath();
		        } else {
		            userHome = pmAdmin.newInstance(UserHome.class);
		            userHomeSegment.addUserHome(
		            	principalName,
		            	userHome
		            );
		            // owning user of home is user itself
		            userHome.setOwningUser(user);
		            // owning group of home is segment administrator
		            userHome.getOwningGroup().add(groupAdministrators);
		            userHome.setAccessLevelDelete(SecurityKeys.ACCESS_LEVEL_NA);
		            userHome.setAccessLevelUpdate(SecurityKeys.ACCESS_LEVEL_BASIC);
		            Properties userSettings = this.getDefaultUserSettings(userHome);
		            userSettings.putAll(this.getUserSettings(userHome));
		            this.applyUserSettings(
		            	userHome,
		            	0, // currentPerspective
		            	userSettings, 
		            	true, // Do not init user home at this stage. Let user do it interactively
		            	true, // storeSettings 
		            	primaryGroup,
		            	"-".equals(timezone) ? null : timezone, // settingTimezone, 
		            	"1", // settingStoreSettingsOnLogoff
		            	null, // settingDefaultEmailAccount 
		            	"[" + providerName + ":" + segmentName + "]", 
		            	null, // settingWebAccessUrl 
		            	"8", // settingTopNavigationShowMax
		            	false, // settingHideWorkspaceDashboard
		            	null, // settingRootObjects 
		            	null // settingSubscriptions
		            );
		            userHomeIdentity = userHome.refGetPath();
		        }
	            userHome.setContact(
	            	(Contact)pmAdmin.getObjectById(contact.refGetPath())
	            );
	            pmAdmin.currentTransaction().commit();
    		} // --- END pmAdmin
	        if(userHome != null && eMailAddress != null && !eMailAddress.isEmpty() && !"-".equals(eMailAddress)) {
	        	EMailAccountQuery emailAccountQuery = (EMailAccountQuery)pmAdmin.newQuery(EMailAccount.class);
	        	emailAccountQuery.name().equalTo(eMailAddress);
	        	List<EMailAccount> emailAccounts = userHome.getEMailAccount(emailAccountQuery);
	        	if(emailAccounts.isEmpty()) {
	        		pmAdmin.currentTransaction().begin();
	        		EMailAccount eMailAccount = (EMailAccount)pmAdmin.newInstance(EMailAccount.class);
	        		eMailAccount.setName(eMailAddress);
	        		eMailAccount.setDefault(Boolean.TRUE);
	        		eMailAccount.setActive(Boolean.TRUE);
	        		userHome.addEMailAccount(
	        			this.getUidAsString(),
	        			eMailAccount
	        		);
	        		pmAdmin.currentTransaction().commit();
	        	}
	        }
        }
        return userHomeIdentity == null ? null : (UserHome)pm.getObjectById(userHomeIdentity);
    }

    /**
     * Find contact matching given aliasName and / or fullName.
     * 
     * @param accountSegment
     * @param aliasName
     * @param fullName
     * @return
     */
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
            } else if(!"-".equals(fullName)) {
            	accountQuery.thereExistsFullName().equalTo(fullName);
            }
            List<Account> accounts = accountSegment.getAccount(accountQuery);
            if(!accounts.isEmpty()) {
                return (Contact)accounts.iterator().next();
            } else {
                return null;
            }
        } catch(Exception e) {
            return null;
        }
    }
        
    /**
     * Import users from given stream (as byte[]).
     * 
     * @param homeSegment
     * @param item
     * @return
     * @throws ServiceException
     */
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
        } catch (UnsupportedEncodingException e) {
        	throw new ServiceException(e);
        }
        String providerName = homeSegment.refGetPath().get(2);
        String segmentName = homeSegment.refGetPath().get(4);
        org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(
        	SecureObject.getRealmIdentity(providerName, segmentName)
        );
        org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
        	Accounts.getInstance().getAccountSegment(pm, providerName, segmentName); 
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
                    String emailAddress = t.hasMoreTokens() ? t.nextToken() : null;
                    String timezone = t.hasMoreTokens() ? t.nextToken() : null;
                    UserHome userHome = null;
                    try {
                    	userHome = homeSegment.getUserHome(principalName);
                    } catch(Exception e) {}
                    if(userHome == null) {
                        try {
                            Contact contact = this.retrieveContact(
                            	accountSegment, 
                            	accountAlias, 
                            	accountFullName
                            );
                            if(contact != null) {
                            	PrincipalGroup primaryGroup = null;
                                try {
                                	if("-".equals(primaryGroupName)) {
                                		primaryGroup = (PrincipalGroup)realm.getPrincipal(primaryGroupName);
                                	}
                                } catch(Exception e) {}
                                List<String> errors = new ArrayList<String>();                                    
                                // Get groups
                                List<org.openmdx.security.realm1.jmi1.Group> isMemberOf = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
                                if(!"-".equals(groups)) {
	                                StringTokenizer g = new StringTokenizer(groups, ",");
	                                while(g.hasMoreTokens()) {
	                                	org.openmdx.security.realm1.jmi1.Group group = null;
	                                	try {
	                                		group = (org.openmdx.security.realm1.jmi1.Group)realm.getPrincipal(g.nextToken());
	                                		isMemberOf.add(group);
	                                	} catch(Exception e) {}
	                                }
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
                                    emailAddress,
                                    timezone,
                                    errors
                                );
                                if(errors.isEmpty()) {
                                    nCreatedUsers++;
                                } else {
                                    nFailedUsersOther++;
                                }
                            } else {
                            	SysLog.info("Contact " + accountAlias + "/" + accountFullName + " for user " + principalName + " not found");
                                nFailedUsersNoContact++;
                            }
                        } catch(Exception e) {
                            new ServiceException(e).log();
                            nFailedUsersOther++;
                        }
                    } else {
                        nExistingUsers++;
                    }
                }
            }
        } catch(IOException e) {
            new ServiceException(e).log();
        }       
        return 
            "Users=(created:" + nCreatedUsers + ",existing:" + nExistingUsers + ",failed no primary group:" + nFailedUsersNoPrimaryGroup + ",failed no contact:" + nFailedUsersNoContact + ",failed other:" + nFailedUsersOther + ");";       
    }

    /**
     * Create object finder according to given basic search criteria.
     * 
     * @param userHome
     * @param searchExpression
     * @return
     * @throws ServiceException
     */
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
            } else if(!"OR".equals(words[i])) {
                if(words[i].startsWith("-")) {
                    withoutWords.append(" ").append(words[i].substring(1));                    
                } else {
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
    
    /**
     * Create object finder according to given advanced search criteria.
     * 
     * @param userHome
     * @param allWords
     * @param atLeastOneOfTheWords
     * @param withoutWords
     * @return
     * @throws ServiceException
     */
    public ObjectFinder searchAdvanced(
        org.opencrx.kernel.home1.jmi1.UserHome userHome,
        String allWords,
        String atLeastOneOfTheWords,
        String withoutWords
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);    	
        org.opencrx.kernel.home1.jmi1.ObjectFinder objectFinder = pm.newInstance(org.opencrx.kernel.home1.jmi1.ObjectFinder.class);
        objectFinder.setName(
            (allWords != null) && !allWords.isEmpty() 
            	? allWords 
            	: (atLeastOneOfTheWords != null) && (atLeastOneOfTheWords.length() > 0) 
            		? atLeastOneOfTheWords 
            		: withoutWords
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
            this.getUidAsString(), 
            objectFinder
        );
        return objectFinder;
    }

    /**
     * Get web access url.
     * 
     * @param userHome
     * @return
     */
    public String getWebAccessUrl(
        UserHome userHome
    ) {
        Path userHomeIdentity = userHome.refGetPath();
        return userHome.getWebAccessUrl() == null 
        	? "http://localhost/opencrx-core-" + userHomeIdentity.getSegment(2).toClassicRepresentation() + "/" + WebKeys.SERVLET_NAME 
        	: userHome.getWebAccessUrl() + "/" + WebKeys.SERVLET_NAME;        
    }

    /**
     * OpenCrxUserSettings
     *
     */
    public static class OpenCrxUserSettings {
    	
		private final String timezone; 
		private final String storeSettingsOnLogoff; 
		private final String defaultEmailAccount;
		private final String sendmailSubjectPrefix; 
		private final String webAccessUrl;
		private final String topNavigationShowMax; 		
		private final Boolean hideWorkspaceDashboard;

		private final List<String> rootObjects;
		private final Map<String,String> subscriptions;
		
		public OpenCrxUserSettings(
			String timezone,
			String storeSettingsOnLogoff, 
			String defaultEmailAccount,
			String sendmailSubjectPrefix, 
			String webAccessUrl,
			String topNavigationShowMax, 
			Boolean hideWorkspaceDashboard,
			List<String> rootObjects,
			Map<String,String> subscriptions
		) {
			this.timezone = timezone; 
			this.storeSettingsOnLogoff = storeSettingsOnLogoff; 
			this.defaultEmailAccount = defaultEmailAccount; 
			this.sendmailSubjectPrefix = sendmailSubjectPrefix;
			this.webAccessUrl = webAccessUrl;
			this.topNavigationShowMax = topNavigationShowMax; 
			this.hideWorkspaceDashboard = hideWorkspaceDashboard;
			this.rootObjects = rootObjects;
			this.subscriptions = subscriptions;
		}

		public String getTimezone() {
        	return timezone;
        }

		public String getStoreSettingsOnLogoff() {
        	return storeSettingsOnLogoff;
        }

		public String getDefaultEmailAccount() {
        	return defaultEmailAccount;
        }

		public String getSendmailSubjectPrefix() {
        	return sendmailSubjectPrefix;
        }

		public String getWebAccessUrl() {
        	return webAccessUrl;
        }

		public String getTopNavigationShowMax() {
        	return topNavigationShowMax;
        }

		public Boolean getHideWorkspaceDashboard() {
        	return hideWorkspaceDashboard;
        }
		
		public List<String> getRootObjects() {
        	return rootObjects;
        }

		public Map<String,String> getSubscriptions() {
        	return subscriptions;
        }
		
    }

    /**
     * Initializes a user's home and settings.
     * 
     * @param userHome
     * @param currentPerspective
     * @param settings
     * @param doNotInitUserHome
     * @param storeSettings
     * @param primaryGroup
     * @param settingTimezone
     * @param settingStoreSettingsOnLogoff
     * @param settingDefaultEmailAccount
     * @param settingSendmailSubjectPrefix
     * @param settingWebAccessUrl
     * @param settingTopNavigationShowMax
     * @param settingHideWorkspaceDashboard
     * @param settingRootObjects
     * @param settingSubscriptions
     * @throws ServiceException
     */
    public void applyUserSettings(
    	UserHome userHome,
    	int currentPerspective,
    	Properties settings,
    	boolean doNotInitUserHome,
    	boolean storeSettings,
    	PrincipalGroup primaryGroup,    	
    	String settingTimezone,
    	String settingStoreSettingsOnLogoff,
    	String settingDefaultEmailAccount,
    	String settingSendmailSubjectPrefix,
    	String settingWebAccessUrl,
    	String settingTopNavigationShowMax,
    	Boolean settingHideWorkspaceDashboard,
    	List<String> settingRootObjects,
    	Map<String,String> settingSubscriptions
    ) throws ServiceException {
    	this.applyUserSettings(
    		userHome, 
    		currentPerspective, 
    		settings, 
    		storeSettings, 
    		primaryGroup, 
    		new OpenCrxUserSettings(
    			settingTimezone,
	    		settingStoreSettingsOnLogoff, 
	    		settingDefaultEmailAccount, 
	    		settingSendmailSubjectPrefix, 
	    		settingWebAccessUrl, 
	    		settingTopNavigationShowMax, 
	    		settingHideWorkspaceDashboard,
	    		settingRootObjects, 
	    		settingSubscriptions
	    	),
	    	doNotInitUserHome
    	);
    }
    
    /**
     * Applies the newSettings to settings. If !noInitUserHome is set then
     * the user home first is initialized, i.e. method {@link #initUserHome(UserHome)}
     * is called. The settings are stored on userHome if storeSettings is true.
     * 
     * @param userHome
     * @param currentPerspective
     * @param settings
     * @param storeSettings
     * @param primaryGroup
     * @param newSettings
     * @param doNotInitUserHome
     * @throws ServiceException
     */
    public void applyUserSettings(
    	UserHome userHome,
    	int currentPerspective,
    	Properties settings,
    	boolean storeSettings,
    	PrincipalGroup primaryGroup,    
    	OpenCrxUserSettings newSettings,
    	boolean doNotInitUserHome
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
    	String providerName = userHome.refGetPath().getSegment(2).toClassicRepresentation();
    	String segmentName = userHome.refGetPath().getSegment(4).toClassicRepresentation();
    	String principalName = userHome.refGetPath().getLastSegment().toClassicRepresentation();
    	// Run realm-related operations in separate unit-of-work
    	{
    		PersistenceManager pmAdmin = pm.getPersistenceManagerFactory().getPersistenceManager(
				SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName,
				null
			);
   			pmAdmin.currentTransaction().begin();
			// Get principal group with name <principal>.Group. This is the private group of the owner of the user home page    	
			org.openmdx.security.realm1.jmi1.Realm realm = SecureObject.getInstance().getRealm(
				pmAdmin,
				providerName,
				segmentName
			);
			PrincipalGroup privatePrincipalGroup = (PrincipalGroup)SecureObject.getInstance().findPrincipal(
				principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
				realm
			);
			if(privatePrincipalGroup == null) {
				privatePrincipalGroup = pmAdmin.newInstance(PrincipalGroup.class);
				privatePrincipalGroup.setDescription(segmentName + "\\\\" + principalName + "." + SecurityKeys.GROUP_SUFFIX);
				realm.addPrincipal(
					principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
					privatePrincipalGroup
				);
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
				PrincipalGroup publicGroup = (PrincipalGroup)SecureObject.getInstance().findPrincipal(
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
			pmAdmin.currentTransaction().commit();
			pmAdmin.close();
    	}
    	if(!doNotInitUserHome) {
        	List<String> currentPrincipals = UserObjects.getPrincipalChain(pm);
        	if(!currentPrincipals.isEmpty() && !principalName.equals(currentPrincipals.get(0))) {
        		throw new ServiceException(
        			BasicException.Code.DEFAULT_DOMAIN,
        			BasicException.Code.ASSERTION_FAILURE,
        			"doNotInitUserHome=false requires that principal of invoking pm matches the user home's principal",
        			new BasicException.Parameter("pm.principalChain", currentPrincipals),
        			new BasicException.Parameter("userHome.xri", userHome.refGetPath())
        		);    			
        	}
	    	this.initUserHome(userHome);
    	}
		// Set UserHome's primary group
		if(primaryGroup == null) {
			org.openmdx.security.realm1.jmi1.Realm realm = SecureObject.getInstance().getRealm(
				pm,
				providerName,
				segmentName
			);
			PrincipalGroup privatePrincipalGroup = (PrincipalGroup)SecureObject.getInstance().findPrincipal(
				principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
				realm
			);	
			userHome.setPrimaryGroup(privatePrincipalGroup);
		} else {
			userHome.setPrimaryGroup(primaryGroup);				
		}
    	if(newSettings.getTimezone() != null) {
    		settings.setProperty(UserSettings.TIMEZONE_NAME.getName(), newSettings.getTimezone());
    	}
		userHome.setStoreSettingsOnLogoff(
			Boolean.valueOf(newSettings.getStoreSettingsOnLogoff() == null ? "false" :"true")
		);
		if(newSettings.getWebAccessUrl() != null) {
			userHome.setWebAccessUrl(newSettings.getWebAccessUrl());
		}
		if(newSettings.getSendmailSubjectPrefix() != null) {
			userHome.setSendMailSubjectPrefix(newSettings.getSendmailSubjectPrefix());
		}
		// Email account
		EMailAccountQuery emailAccountQuery = (EMailAccountQuery)pm.newQuery(EMailAccount.class);
		emailAccountQuery.thereExistsIsActive().isTrue();
		emailAccountQuery.thereExistsIsDefault().isTrue();
		List<EMailAccount> emailAccounts = userHome.getEMailAccount(emailAccountQuery);
		EMailAccount defaultEmailAccount = emailAccounts.isEmpty() ? null : emailAccounts.iterator().next();
		if(
			(defaultEmailAccount == null) &&
			(newSettings.getDefaultEmailAccount() != null) &&
			(newSettings.getDefaultEmailAccount().length() > 0)
		) {
			defaultEmailAccount = pm.newInstance(EMailAccount.class);
			defaultEmailAccount.setDefault(Boolean.TRUE);
			defaultEmailAccount.setActive(Boolean.TRUE);
			defaultEmailAccount.setName(newSettings.getDefaultEmailAccount());
			userHome.addEMailAccount(
				this.getUidAsString(),
				defaultEmailAccount
			);
		} else if(
			(defaultEmailAccount != null) &&
			((newSettings.getDefaultEmailAccount() == null) ||
			(newSettings.getDefaultEmailAccount().length() == 0))
		) {
			defaultEmailAccount.refDelete();
		} else if(defaultEmailAccount != null) {
			defaultEmailAccount.setName(newSettings.getDefaultEmailAccount());
		}
		// Root objects
		for(int i = 0; i < 20; i++) {
			String state = (newSettings.getRootObjects() != null) && (i < newSettings.getRootObjects().size()) ? 
				newSettings.getRootObjects().get(i) : 
				"1";
			settings.setProperty(
				UserSettings.ROOT_OBJECT_STATE.getName() + (currentPerspective == 0 ? "" : "[" + Integer.toString(currentPerspective) + "]") + "." + i + ".State",
				state == null ? "0" : state
			);
		}
		// Show max items in top navigation
		if(newSettings.getTopNavigationShowMax() != null) {
			settings.setProperty(
				UserSettings.TOP_NAVIGATION_SHOW_MAX.getName(),
				newSettings.getTopNavigationShowMax()
			);
		}
		// Show workspace dashboard
		if(newSettings.getHideWorkspaceDashboard() != null) {			
			settings.setProperty(
				UserSettings.HIDE_WORKSPACE_DASHBOARD.getName(),
				Boolean.toString(newSettings.getHideWorkspaceDashboard())
			);			
		}
		// Subscriptions
    	org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = Workflows.getInstance().getWorkflowSegment(pm, providerName, segmentName); 
		Collection<Topic> topics = workflowSegment.getTopic();
		for(Topic topic: topics) {
			SubscriptionQuery query = (SubscriptionQuery)pm.newQuery(Subscription.class);
			query.thereExistsTopic().equalTo(topic);
			List<Subscription> subscriptions = userHome.getSubscription(query);
			org.opencrx.kernel.home1.jmi1.Subscription subscription = null;
			if(subscriptions.isEmpty()) {
				subscription = pm.newInstance(Subscription.class);
				subscription.setName(topic.getName());
				subscription.setTopic(topic);
				userHome.addSubscription(
					this.getUidAsString(),
					subscription
				);
			} else {
				subscription = subscriptions.iterator().next();
			}
			subscription.getEventType().clear();
			String topicId = topic.refGetPath().getLastSegment().toClassicRepresentation();
			subscription.setActive(
				(newSettings.getSubscriptions() != null) && (newSettings.getSubscriptions().get("topicIsActive-" + topicId) != null)
			);
			if((newSettings.getSubscriptions() != null) && (newSettings.getSubscriptions().get("topicCreation-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)1));
			}
			if((newSettings.getSubscriptions() != null) && (newSettings.getSubscriptions().get("topicReplacement-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)3));
			}
			if((newSettings.getSubscriptions() != null) && (newSettings.getSubscriptions().get("topicRemoval-" + topicId) != null)) {
				subscription.getEventType().add(new Short((short)4));
			}
		}
		// Store settings
		if(storeSettings) {
			try {
				QuotaByteArrayOutputStream bsSettings = new QuotaByteArrayOutputStream(UserHomes.class.getName());
				settings.store(
					bsSettings,
					"settings of user " + userHome.refMofId()
				);
				bsSettings.close();
				userHome.setSettings(
					bsSettings.toString("UTF-8")
				);
			} catch(IOException e) {
				throw new ServiceException(e);
			}
		}
    }
    
    /**
     * Initializes user home. It creates
     * <ul> 
     *   <li>Private activity trackers, activity creators for incidents, meetings, tasks
     *   <li>Private document folders
     *   <li>Private account groups
     *   <li>Sync profiles
     *  </ul>
     */
    public void initUserHome(
    	UserHome userHome
    ) throws ServiceException {
    	Path userHomeIdentity = userHome.refGetPath();
    	String providerName = userHomeIdentity.getSegment(2).toClassicRepresentation();
    	String segmentName = userHomeIdentity.getSegment(4).toClassicRepresentation();
    	String principalName = userHomeIdentity.getLastSegment().toClassicRepresentation();
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
    	{
			// Get principal group with name <principal>.Group. This is the private group of the owner of the user home page    	
			org.openmdx.security.realm1.jmi1.Realm realm = org.opencrx.kernel.backend.SecureObject.getInstance().getRealm(
				pm,
				providerName,
				segmentName
			);
			PrincipalGroup privatePrincipalGroup = (PrincipalGroup)org.opencrx.kernel.backend.SecureObject.getInstance().findPrincipal(
				principalName + "." + org.opencrx.kernel.generic.SecurityKeys.GROUP_SUFFIX,
				realm
			);
	    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
	    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, providerName, segmentName); 
	    	org.opencrx.kernel.document1.jmi1.Segment documentSegment = Documents.getInstance().getDocumentSegment(pm, providerName, segmentName);
			// Private activity tracker
			ActivityTracker privateTracker = null;
			try {
				privateTracker = (ActivityTracker)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName, "activityTracker", principalName)
				);
			} catch(Exception e) {}
			if(privateTracker == null) {
				privateTracker = pm.newInstance(ActivityTracker.class);
				privateTracker.setName(principalName + Base.PRIVATE_SUFFIX);
				if(privatePrincipalGroup != null) {
					privateTracker.getOwningGroup().clear();
					privateTracker.getOwningGroup().add(privatePrincipalGroup);
				}
				activitySegment.addActivityTracker(
					principalName,
					privateTracker
				);
			}
			// Private Incident creator
			ActivityCreator privateIncidentsCreator = null;
			try {
				privateIncidentsCreator = (ActivityCreator)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName, "activityCreator", principalName)
				);
			} catch(Exception e) {}
			if(privateIncidentsCreator == null) {
				privateIncidentsCreator = pm.newInstance(ActivityCreator.class);				
				Activities.getInstance().initActivityCreator(
					privateIncidentsCreator,
					principalName + Base.PRIVATE_SUFFIX, 
					Activities.getInstance().findActivityType(
						"Bugs + Features", 
						activitySegment
					),
					ICalendar.ICAL_TYPE_VEVENT,
					ICalClass.PUBLIC,
					privateTracker == null ? null : Collections.<ActivityGroup>singletonList(privateTracker),
					privatePrincipalGroup == null ? null : Collections.singletonList(privatePrincipalGroup)
				);
				activitySegment.addActivityCreator(
					principalName,
					privateIncidentsCreator
				);
			}
			if(privateIncidentsCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
				privateIncidentsCreator.setIcalType(ICalendar.ICAL_TYPE_VEVENT);			
			}
			// Private E-Mail creator
			ActivityCreator privateEMailsCreator = null;
			try {
				privateEMailsCreator = (ActivityCreator)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName, "activityCreator", principalName + "~E-Mails")
				);
			} catch(Exception e) {}
			if(privateEMailsCreator == null) {
				privateEMailsCreator = pm.newInstance(ActivityCreator.class);
				Activities.getInstance().initActivityCreator(
					privateEMailsCreator,
					principalName + Base.PRIVATE_SUFFIX + "~E-Mails", 
					Activities.getInstance().findActivityType(
						"E-Mails", 
						activitySegment
					),
					ICalendar.ICAL_TYPE_VEVENT,
					ICalClass.PUBLIC,
					privateTracker == null ? null : Collections.<ActivityGroup>singletonList(privateTracker),
					privatePrincipalGroup == null ? null : Collections.singletonList(privatePrincipalGroup)
				);				
				activitySegment.addActivityCreator(
					principalName + "~E-Mails",
					privateEMailsCreator
				);
			}
			if(privateEMailsCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
				privateEMailsCreator.setIcalType(ICalendar.ICAL_TYPE_VEVENT);
			}
			// Private Task creator
			ActivityCreator privateTasksCreator = null;
			try {
				privateTasksCreator = (ActivityCreator)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName, "activityCreator", principalName + "~Tasks")
				);
			} catch(Exception e) {}
			if(privateTasksCreator == null) {
				privateTasksCreator = pm.newInstance(ActivityCreator.class);
				Activities.getInstance().initActivityCreator(
					privateTasksCreator,
					principalName + Base.PRIVATE_SUFFIX + "~Tasks", 
					Activities.getInstance().findActivityType(
						"Tasks", 
						activitySegment
					),
					ICalendar.ICAL_TYPE_VTODO,
					ICalClass.PUBLIC,
					privateTracker == null ? null : Collections.<ActivityGroup>singletonList(privateTracker),
					privatePrincipalGroup == null ? null : Collections.singletonList(privatePrincipalGroup)
				);				
				activitySegment.addActivityCreator(
					principalName + "~Tasks",
					privateTasksCreator
				);
			}
			if(privateTasksCreator.getIcalType() == ICalendar.ICAL_TYPE_NA) {
				privateTasksCreator.setIcalType(ICalendar.ICAL_TYPE_VTODO);
			}
			// Private Meeting creator
			ActivityCreator privateMeetingsCreator = null;
			try {
				privateMeetingsCreator = (ActivityCreator)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName, "activityCreator", principalName + "~Meetings")
				);
			} catch(Exception e) {}
			if(privateMeetingsCreator == null) {
				privateMeetingsCreator = pm.newInstance(ActivityCreator.class);
				Activities.getInstance().initActivityCreator(
					privateMeetingsCreator,
					principalName + Base.PRIVATE_SUFFIX + "~Meetings", 
					Activities.getInstance().findActivityType(
						"Meetings", 
						activitySegment
					),
					ICalendar.ICAL_TYPE_VEVENT,
					ICalClass.PUBLIC,
					privateTracker == null ? null : Collections.<ActivityGroup>singletonList(privateTracker),
					privatePrincipalGroup == null ? null : Collections.singletonList(privatePrincipalGroup)
				);				
				activitySegment.addActivityCreator(
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
			org.opencrx.kernel.activity1.jmi1.Resource resource = null;
			try {
				// Get resource specific for principal
				resource = activitySegment.getResource(principalName);
			} catch(Exception e) {}
			if(resource == null) {
				resource = pm.newInstance(Resource.class);
				if(userHome.getContact() != null) {
					resource.setName(userHome.getContact().getFullName());
					resource.setContact(userHome.getContact());
				} else {
					resource.setName(principalName);
				}
				activitySegment.addResource(
					false,
					principalName,
					resource
				);
			}
			// Private AccountGroup
			org.opencrx.kernel.account1.jmi1.Group privateAccountGroup = null;
			try {
				privateAccountGroup = (org.opencrx.kernel.account1.jmi1.Group)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", providerName, "segment", segmentName, "account", principalName + Base.PRIVATE_SUFFIX)
				);
			} catch(Exception e) {}
			if(privateAccountGroup == null) {
				privateAccountGroup = pm.newInstance(org.opencrx.kernel.account1.jmi1.Group.class);
				privateAccountGroup.setName(principalName + Base.PRIVATE_SUFFIX);
				if(privatePrincipalGroup != null) {
					privateAccountGroup.getOwningGroup().clear();
					privateAccountGroup.getOwningGroup().add(privatePrincipalGroup);
				}			
				accountSegment.addAccount(
					principalName + Base.PRIVATE_SUFFIX,
					privateAccountGroup
				);
			}
			// Private Document folder
			org.opencrx.kernel.document1.jmi1.DocumentFolder privateDocumentFolder = null;
			try {
				privateDocumentFolder = (org.opencrx.kernel.document1.jmi1.DocumentFolder)pm.getObjectById(
					new Path("xri://@openmdx*org.opencrx.kernel.document1").getDescendant("provider", providerName, "segment", segmentName, "folder", principalName + Documents.PRIVATE_DOCUMENTS_FOLDER_SUFFIX)
				);
			} catch(Exception e) {}		
			if(privateDocumentFolder == null) {
				privateDocumentFolder = pm.newInstance(org.opencrx.kernel.document1.jmi1.DocumentFolder.class);
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
			// Assert owningUser of Resource
		    SetOwningUserParams setOwningUserParams = Structures.create(
		    	SetOwningUserParams.class, 
		    	Datatypes.member(SetOwningUserParams.Member.mode, (short)SecureObject.MODE_RECURSIVE),
		    	Datatypes.member(SetOwningUserParams.Member.user, userHome.getOwningUser())	
		    ); 
			resource.setOwningUser(
			    setOwningUserParams
			);
			// AirSync profile
			org.opencrx.kernel.home1.jmi1.AirSyncProfile airSyncProfile = null;
			try {
				airSyncProfile = (org.opencrx.kernel.home1.jmi1.AirSyncProfile)pm.getObjectById(
					userHome.refGetPath().getDescendant("syncProfile", "AirSync")
				);
			} catch(Exception e) {}
			if(airSyncProfile == null) {
				airSyncProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.AirSyncProfile.class);
				airSyncProfile.setName("AirSync");
				userHome.addSyncProfile(
					"AirSync",
					airSyncProfile
				);
				ActivityGroupCalendarFeed calendarFeed = pm.newInstance(ActivityGroupCalendarFeed.class);
				calendarFeed.setName(principalName + Base.PRIVATE_SUFFIX);
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
				contactsFeed.setName(principalName + Base.PRIVATE_SUFFIX);
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
			// Contacts profile
			org.opencrx.kernel.home1.jmi1.CardProfile cardProfile = null;
			try {
				cardProfile = (org.opencrx.kernel.home1.jmi1.CardProfile)pm.getObjectById(
					userHome.refGetPath().getDescendant("syncProfile", "Card")
				);
			} catch(Exception e) {}
			if(cardProfile == null) {
				cardProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.CardProfile.class);
				cardProfile.setName("Contacts");
				cardProfile.setDescription("Contacts profile");
				userHome.addSyncProfile(
					"Card",
					cardProfile
				);
				ContactsFeed feed = pm.newInstance(ContactsFeed.class);
				feed.setName(principalName + Base.PRIVATE_SUFFIX);
				feed.setDescription("Contacts Feed");
				feed.setAccountGroup(privateAccountGroup);
				feed.setActive(true);
				feed.setAllowAddDelete(true);
				feed.setAllowChange(true);
				cardProfile.addFeed(
					this.getUidAsString(), 
					feed
				);
			}
			// Calendar profile
			org.opencrx.kernel.home1.jmi1.CalendarProfile calendarProfile = null;
			try {
				calendarProfile = (org.opencrx.kernel.home1.jmi1.CalendarProfile)pm.getObjectById(
					userHome.refGetPath().getDescendant("syncProfile", "Calendar")
				);
			} catch(Exception e) {}
			if(calendarProfile == null) {
				calendarProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.CalendarProfile.class);
				calendarProfile.setName("Calendars");
				calendarProfile.setDescription("Calendars profile");
				userHome.addSyncProfile(
					"Calendar",
					calendarProfile
				);
				ActivityGroupCalendarFeed feed = pm.newInstance(ActivityGroupCalendarFeed.class);
				feed.setName(principalName + Base.PRIVATE_SUFFIX);
				feed.setDescription("Calendars Feed");
				feed.setActivityGroup(privateTracker);
				feed.setActive(true);
				feed.setAllowAddDelete(true);
				feed.setAllowChange(true);
				calendarProfile.addFeed(
					this.getUidAsString(), 
					feed
				);
			}
			// Document profile
			org.opencrx.kernel.home1.jmi1.DocumentProfile documentProfile = null;
			try {
				documentProfile = (org.opencrx.kernel.home1.jmi1.DocumentProfile)pm.getObjectById(
					userHome.refGetPath().getDescendant("syncProfile", "Document")
				);
			} catch(Exception e) {}
			if(documentProfile == null) {
				documentProfile = pm.newInstance(org.opencrx.kernel.home1.jmi1.DocumentProfile.class);
				documentProfile.setName("Documents");
				documentProfile.setDescription("Documents profile");
				userHome.addSyncProfile(
					"Document",
					documentProfile
				);
				DocumentFeed documentFeed = pm.newInstance(DocumentFeed.class);
				documentFeed.setName(principalName + Base.PRIVATE_SUFFIX);
				documentFeed.setDescription("Documents Feed");
				documentFeed.setDocumentFolder(privateDocumentFolder);
				documentFeed.setActive(true);
				documentFeed.setAllowAddDelete(true);
				documentFeed.setAllowChange(true);
				documentProfile.addFeed(
					this.getUidAsString(), 
					documentFeed
				);
			}  
    	}
    }
    
    /**
     * Get settings of given user.
     * 
     * @param userHome
     * @return
     * @throws ServiceException
     */
    public Properties getUserSettings(
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
            } catch(IOException e) {
            	throw new ServiceException(e);
            }
        }
        return settings;
    }
    
    /**
     * Get default user settings for given user. Default settings are stored
     * in documents with name pattern UserSettings.{role.name}. The content of
     * the document must be text/plain and in the standard Java properties format.
     * The assigned roles of the user are iterated by the order returned by
     * principal).getGrantedRole() and override properties with the same key
     * (defaultSettings.putAll(settings))
     * 
     * @param userHome
     * @return
     * @throws ServiceException
     */
    public Properties getDefaultUserSettings(
    	UserHome userHome
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(userHome);
    	String providerName = userHome.refGetPath().getSegment(2).toClassicRepresentation();
    	String segmentName = userHome.refGetPath().getSegment(4).toClassicRepresentation();
    	org.openmdx.security.realm1.jmi1.Realm realm = SecureObject.getInstance().getRealm(pm, providerName, segmentName);
    	org.openmdx.security.realm1.jmi1.Principal principal = SecureObject.getInstance().findPrincipal(
    		userHome.refGetPath().getLastSegment().toClassicRepresentation(), 
    		realm
    	);
    	Properties defaultSettings = new Properties();
    	if(principal instanceof org.opencrx.security.realm1.jmi1.Principal) {
    		org.opencrx.kernel.document1.jmi1.Segment documentSegment = Documents.getInstance().getDocumentSegment(pm, providerName, segmentName);
    		List<org.openmdx.security.realm1.jmi1.Role> roles = ((org.opencrx.security.realm1.jmi1.Principal)principal).getGrantedRole();
    		for(org.openmdx.security.realm1.jmi1.Role role: roles) {
    			Document settingsTemplateDocument = Documents.getInstance().findDocument("UserSettings." + role.getName() + ".properties", documentSegment);
    			if(settingsTemplateDocument != null && settingsTemplateDocument.getHeadRevision() instanceof MediaContent) {
    				try {
    					MediaContent settingsTemplateContent = (MediaContent)settingsTemplateDocument.getHeadRevision();
    					// The settings template must be created/modified by the segment admin. Otherwise it will be ignored.
    					if(settingsTemplateContent.getModifiedBy().contains(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName)) {
		    				Properties roleSettings = new Properties();
		    				roleSettings.load(settingsTemplateContent.getContent().getContent());
		    				defaultSettings.putAll(roleSettings);
    					}
    				} catch(Exception e) {
    					new ServiceException(e).log();
    				}
    			}
    		}
    	}
    	return defaultSettings;
    }

    /**
     * Get timezone property.
     * 
     * @param settings
     * @return
     * @throws ServiceException
     */
    public String getUserTimezone(
    	Properties settings
    ) throws ServiceException {
    	return settings.getProperty(UserSettings.TIMEZONE_NAME.getName());
    }
    
    /**
     * Update derived attributes of given timer.
     * 
     * @param timer
     * @throws ServiceException
     */
    public void updateTimer(
    	Timer timer
    ) throws ServiceException {
    	Date now = new Date();
        if(JDOHelper.isNew(timer)) {
        	if(timer.getTriggerRepeat() == null) {
        		timer.setTriggerRepeat(1);
        	}
        	if(timer.getTriggerIntervalMinutes() == null) {
        		timer.setTriggerIntervalMinutes(15);
        	}
        	if(timer.getTimerStartAt() == null) {
        		ContextCapable target = null;
        		try {
        			target = timer.getTarget();
        		} catch(Exception e) {}
        		if(target instanceof Activity) {
        			timer.setTimerStartAt(((Activity)target).getScheduledStart());
        			timer.setTimerState(((Activity)target).getActivityState());
        		} else {
        			timer.setTimerStartAt(now);
        			timer.setTimerState((short)TimerState.OPEN.getValue());
        		}
        	}
        } else {
            // Update activity
            {
    			ContextCapable target = null;
    			try {
    				target = timer.getTarget();
    			} catch(Exception e) {}
    			if(target instanceof Activity) {
    				Activities.getInstance().markActivityAsDirty((Activity)target);
    			}
            }
        }
        if(timer.getTimerStartAt() != null) {
	    	if(timer.getLastTriggerAt() == null || timer.getTimerStartAt().compareTo(now) > 0) {
	    		// Set lastTriggerAt period before timerStartAt. This way the timer triggers the 
	    		// first time at timerStartAt. Update only if the timer starts in the future.
	    		timer.setLastTriggerAt(
					new Date(timer.getTimerStartAt().getTime() - timer.getTriggerIntervalMinutes() * 60000L) 
				);
	    	}
        }
    }

	/* (non-Javadoc)
	 * @see org.opencrx.kernel.backend.AbstractImpl#preDelete(org.opencrx.kernel.generic.jmi1.CrxObject, boolean)
	 */
	@Override
	public void preDelete(
		RefObject_1_0 object, 
		boolean preDelete
	) throws ServiceException {
		super.preDelete(object, preDelete);
	}

	/* (non-Javadoc)
	 * @see org.opencrx.kernel.backend.AbstractImpl#preStore(org.opencrx.kernel.generic.jmi1.CrxObject)
	 */
	@Override
	public void preStore(
		RefObject_1_0 object
	) throws ServiceException {
		super.preStore(object);
		if(object instanceof Timer) {
			this.updateTimer((Timer)object);
		}
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

    /**
     * TimerState
     *
     */
    public enum TimerState {
    	
    	NA(0),
    	OPEN(10),
    	CLOSED(20),
    	CANCELLED(30);
    	
    	private final int value;
    	    	
    	private TimerState(
    		int value
    	) {
    		this.value = value;
    	}
    	
    	public int getValue(
    	) {
    		return this.value;
    	}
    	
    }

	/**
	 * AlertState
	 *
	 */
	public enum AlertState {
		
	    NA((short)0),
	    NEW((short)1),
	    READ((short)2),
	    ACCEPTED((short)3),
	    EXPIRED((short)4);
		
		private short value;
		
		private AlertState(
			short value
		) {
			this.value = value;
		}
		
		public short getValue(
		) {
			return this.value;
		}
		
	}

}

//--- End of File -----------------------------------------------------------

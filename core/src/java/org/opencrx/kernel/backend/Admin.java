/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Admin.java,v 1.26 2009/04/21 11:21:59 wfro Exp $
 * Description: Admin
 * Revision:    $Revision: 1.26 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 11:21:59 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

public class Admin extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Admin());
	}
	
    //-------------------------------------------------------------------------
	public static Admin getInstance(		
	) throws ServiceException {
		return getInstance(Admin.class);
	}

	//-------------------------------------------------------------------------
	protected Admin(
	) {
		
	}
	
    //-----------------------------------------------------------------------
	public enum PrincipalType {
		PRINCIPAL,
		USER,
		GROUP
	}
	
    //-----------------------------------------------------------------------
    public void createSegment(
    	org.openmdx.base.jmi1.Provider provider,
        org.openmdx.base.jmi1.Segment segment,
        String segmentName,
        org.opencrx.security.realm1.jmi1.User owningUser,
        org.opencrx.security.realm1.jmi1.PrincipalGroup owningGroup,
        List<String> errors
    ) {
    	segment.refInitialize(false, false);
    	org.openmdx.base.jmi1.Segment test = null;
	    try {
	        test = provider.getSegment(segmentName);
	    }
	    catch(Exception e) {}
	    if(test == null) {
	        if(segment instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
		        ((org.opencrx.kernel.base.jmi1.SecureObject)segment).setOwningUser(owningUser);
		        ((org.opencrx.kernel.base.jmi1.SecureObject)segment).getOwningGroup().add(owningGroup);
		        ((org.opencrx.kernel.base.jmi1.SecureObject)segment).setAccessLevelBrowse(new Short(SecurityKeys.ACCESS_LEVEL_GLOBAL));
		        ((org.opencrx.kernel.base.jmi1.SecureObject)segment).setAccessLevelUpdate(new Short(SecurityKeys.ACCESS_LEVEL_DEEP));
		        ((org.opencrx.kernel.base.jmi1.SecureObject)segment).setAccessLevelDelete(new Short(SecurityKeys.ACCESS_LEVEL_PRIVATE));                
	        }
	        try {
	        	provider.addSegment(
	        		false,
	        		segmentName,
	        		segment
	        	);
	        }
	        catch(Exception e0) {
	            ServiceException e1 = new ServiceException(e0);
	            AppLog.warning(e1.getMessage(), e1.getCause());
	            errors.add("Unable to create segment " + segment);
	            errors.add("reason is " + e0.getMessage());
	        }
	    }
    }
    
    //-----------------------------------------------------------------------
    public Contact createContact(
        org.opencrx.kernel.admin1.jmi1.Segment adminSegment,
        String segmentName,
        String principalName,
        org.opencrx.security.realm1.jmi1.User owningUser,
        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> owningGroups,
        List<String> errors
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(adminSegment);
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
    		(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
    			new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + adminSegment.refGetPath().get(2) + "/segment/" + segmentName)
    		);
    	Contact contact = null;
        try {
        	contact = (Contact)accountSegment.getAccount(principalName);
        }
        catch(Exception e) {}
        if(contact == null) {
        	contact = pm.newInstance(Contact.class);
        	contact.refInitialize(false, false);
            contact.setLastName(principalName);
            contact.setOwningUser(owningUser);
            contact.getOwningGroup().addAll(owningGroups);
            accountSegment.addAccount(
            	false,
            	principalName,
            	contact
            );
        }
        return contact;
    }

    //-----------------------------------------------------------------------
    public org.opencrx.security.identity1.jmi1.Subject createSubject(
    	org.opencrx.security.identity1.jmi1.Segment identitySegment,
        String subjectName,
        String subjectDescription,
        List<String> errors
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(identitySegment);
    	org.opencrx.security.identity1.jmi1.Subject subject = identitySegment.getSubject(subjectName);
    	if(subject == null) {
        	subject = pm.newInstance(org.opencrx.security.identity1.jmi1.Subject.class);
        	subject.refInitialize(false, false);
        	subject.setDescription(
        		subjectDescription == null ? subjectName : subjectDescription
        	);
        	identitySegment.addSubject(
        		false,
        		subjectName,
        		subject
        	);
        }
        return subject;
    }
    
    //-----------------------------------------------------------------------
    public org.openmdx.security.realm1.jmi1.Principal createPrincipal(
        String principalName,
        String principalDescription,
        org.openmdx.security.realm1.jmi1.Realm realm,
        PrincipalType principalType,
        List<org.openmdx.security.realm1.jmi1.Group> memberOfGroups,
        org.openmdx.security.realm1.jmi1.Subject subject
    ) {
        PersistenceManager pm = JDOHelper.getPersistenceManager(realm);
        org.openmdx.security.realm1.jmi1.Principal principal = realm.getPrincipal(principalName);
        if(principal != null) {
            Set<org.openmdx.security.realm1.jmi1.Group> mergedGroups = new LinkedHashSet<org.openmdx.security.realm1.jmi1.Group>();
            List<org.openmdx.security.realm1.jmi1.Group> principalIsMemberOf = principal.getIsMemberOf();
            mergedGroups.addAll(principalIsMemberOf);
            mergedGroups.addAll(memberOfGroups);
            principal.getIsMemberOf().clear();
            principal.getIsMemberOf().addAll(
                mergedGroups
            );
        }
        else {
        	switch(principalType) {
	        	case USER: 
	        		principal = pm.newInstance(org.opencrx.security.realm1.jmi1.User.class);
	        		break;
	        	case PRINCIPAL:
	        		principal = pm.newInstance(org.opencrx.security.realm1.jmi1.Principal.class);
	        		break;
	        	case GROUP:
	        		principal = pm.newInstance(org.opencrx.security.realm1.jmi1.PrincipalGroup.class);
	        		break;
        	}
        	principal.refInitialize(false, false);
            principal.setDescription(
            	principalDescription == null ?
            		realm.refGetPath().getBase() + "\\\\" + principalName :
            	    principalDescription
            );
            principal.setDisabled(Boolean.FALSE);
            principal.getIsMemberOf().addAll(
                memberOfGroups
            );
            principal.setSubject(subject);
            realm.addPrincipal(
            	false,
            	principalName,
            	principal
            );
        }
        return principal;
    }
    
    //-----------------------------------------------------------------------
    /*
     * Creates a new segment and segment administrators. <segmentName>:ADMIN_PRINCIPAL 
     * is the owner of all created objects. 
     */
    public void createAdministrator(
        org.opencrx.kernel.admin1.jmi1.Segment adminSegment,
        String segmentName,
        String principalName,
        String initialPassword,
        String initialPasswordVerification,
        List<String> errors
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(adminSegment);
        String providerName = adminSegment.refGetPath().get(2);        
        // Principal must exist in login realm
        String adminPrincipalName = SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName;
        Path loginRealmIdentity = SecureObject.getInstance().getLoginRealmIdentity(providerName);
        org.openmdx.security.realm1.jmi1.Realm loginRealm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(
        	loginRealmIdentity
        );
        org.openmdx.security.realm1.jmi1.Subject segmentAdminSubject = null;
    	if(loginRealm.getPrincipal(principalName) != null) {
            try {
            	segmentAdminSubject = loginRealm.getPrincipal(principalName).getSubject();
            }
            catch(Exception e) {}
    	}
        if(segmentAdminSubject == null) {
            // Create segment administrator if it does not exist
        	org.opencrx.security.identity1.jmi1.Segment identitySegment = 
        		(org.opencrx.security.identity1.jmi1.Segment)pm.getObjectById(
        			new Path("xri:@openmdx:org.opencrx.security.identity1/provider/" + loginRealmIdentity.get(2) + "/segment/Root")
        		);
            // Create segment administrator's subject
        	segmentAdminSubject = this.createSubject(
            	identitySegment,
                adminPrincipalName,
                null,
                errors
            );
            if(!errors.isEmpty()) return;            
            // Create segment administrator's principal
            List<org.openmdx.security.realm1.jmi1.Group> groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
            groups.add(
            	(org.openmdx.security.realm1.jmi1.Group)loginRealm.getPrincipal(SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS)
            );
            this.createPrincipal(
                principalName,
                null,
                loginRealm,
                PrincipalType.PRINCIPAL,
                groups,
                segmentAdminSubject
            );
        }
        // Principal admin-<segment name> must exist before any other
        // segment administrator can be created
        if(loginRealm.getPrincipal(adminPrincipalName) == null) {
            // admin-<segment name> does not exist --> principalName must match segment administrator
            if(!principalName.equals(adminPrincipalName)) {
                errors.add("primary principal name must match " + adminPrincipalName);
                return;
            }
        }
        if(
            (principalName.startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR)) &&
            !principalName.equals(adminPrincipalName)
        ) {
            errors.add("admin principal for segment " + segmentName + " must match " + adminPrincipalName);
            return;            
        }        
        // Create realm for segment
        Path realmIdentity = SecureObject.getRealmIdentity(providerName, segmentName);
        org.openmdx.security.realm1.jmi1.Realm realm = null;
        try {
        	realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(realmIdentity);
        }
        catch(Exception e) {}
        if(realm == null) {
            org.openmdx.security.realm1.jmi1.Segment realmSegment = 
            	(org.openmdx.security.realm1.jmi1.Segment)pm.getObjectById(
            		realmIdentity.getParent().getParent()
            	);
            realm = pm.newInstance(org.openmdx.security.realm1.jmi1.Realm.class);
            realm.refInitialize(false, false);
            realm.setDescription(segmentName + " Realm");
            realmSegment.addRealm(
            	false,
            	segmentName,
            	realm
            );
        }        
        org.opencrx.security.realm1.jmi1.User adminUser = (org.opencrx.security.realm1.jmi1.User)this.createPrincipal(
        	principalName + SecurityKeys.ID_SEPARATOR + SecurityKeys.GROUP_SUFFIX, 
        	null, 
        	realm, 
        	PrincipalType.USER, 
        	new ArrayList<org.openmdx.security.realm1.jmi1.Group>(), 
        	segmentAdminSubject
        );
        List<org.openmdx.security.realm1.jmi1.Group> groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
        groups.add(adminUser);
        this.createPrincipal(
        	principalName, 
        	null, 
        	realm, 
        	PrincipalType.PRINCIPAL, 
        	groups, 
        	segmentAdminSubject
        );
        org.opencrx.security.realm1.jmi1.PrincipalGroup groupUnspecified = (org.opencrx.security.realm1.jmi1.PrincipalGroup)this.createPrincipal(
            SecurityKeys.USER_GROUP_UNSPECIFIED,
            null,
            realm,
            PrincipalType.GROUP,
            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
            segmentAdminSubject
        );
        groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
        groups.add(groupUnspecified);
        org.opencrx.security.realm1.jmi1.PrincipalGroup groupAdministrators = (org.opencrx.security.realm1.jmi1.PrincipalGroup)this.createPrincipal(
            SecurityKeys.USER_GROUP_ADMINISTRATORS,
            null,
            realm,
            PrincipalType.GROUP,
            groups,
            segmentAdminSubject
        );
        org.opencrx.security.realm1.jmi1.PrincipalGroup groupUsers = (org.opencrx.security.realm1.jmi1.PrincipalGroup)this.createPrincipal(
            SecurityKeys.USER_GROUP_USERS,
            null,
            realm,
            PrincipalType.GROUP,
            groups,
            segmentAdminSubject
        );
        groups = new ArrayList<org.openmdx.security.realm1.jmi1.Group>();
        groups.add(groupUsers);
        this.createPrincipal(
            SecurityKeys.USER_GROUP_UNASSIGNED,
            null,
            realm,
            PrincipalType.GROUP,
            groups,
            segmentAdminSubject
        );
        // Create segments        
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.account1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.home1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.activity1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.contract1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.product1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.document1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.building1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.uom1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.forecast1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.workflow1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.depot1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.model1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        this.createSegment(
        	(org.openmdx.base.jmi1.Provider)pm.getObjectById(new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/" + providerName)), 
        	pm.newInstance(org.opencrx.kernel.code1.jmi1.Segment.class), 
        	segmentName,
        	adminUser, 
        	groupAdministrators, 
        	errors
        );
        if(errors.size() > 0) return;        
        // Create contact: adminPrincipalName
        Contact contact = this.createContact(
            adminSegment, 
            segmentName, 
            principalName, 
            adminUser,
            Arrays.asList(groupUsers, groupAdministrators),
            errors
        );
        if(contact == null) return;
        // Create user home for segment admin
        UserHomes.getInstance().createUserHome(
            realm,
            contact,
            groupAdministrators,
            principalName,
            new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),            
            true,
            initialPassword,
            initialPasswordVerification,
            errors,
            pm
        );   
    }
    
    //-------------------------------------------------------------------------
    public String importLoginPrincipals(
        org.opencrx.kernel.admin1.jmi1.Segment adminSegment,
        byte[] item
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(adminSegment);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(item))
        );
        org.openmdx.security.realm1.jmi1.Realm loginRealm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(
        	SecureObject.getInstance().getLoginRealmIdentity(adminSegment.refGetPath().get(2))
        );
        org.opencrx.security.identity1.jmi1.Segment identitySegment = (org.opencrx.security.identity1.jmi1.Segment)pm.getObjectById( 
            new Path("xri:@openmdx:org.opencrx.security.identity1/provider/" + adminSegment.refGetPath().get(2) + "/segment/Root")
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
                    org.openmdx.security.realm1.jmi1.Principal principal = null;
                    try {
                    	principal = loginRealm.getPrincipal(principalName);
                    }
                    catch(Exception e) {}
                    if(principal == null) {
                        try {
                        	principal = this.createPrincipal(
                        		principalName, 
                        		principalDescription,
                        		loginRealm, 
                        		PrincipalType.PRINCIPAL, 
                        		new ArrayList<org.openmdx.security.realm1.jmi1.Group>(),
                        		identitySegment.getSubject(subjectName)
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
                            org.openmdx.security.realm1.jmi1.Group groupPrincipal = 
                            	(org.openmdx.security.realm1.jmi1.Group)loginRealm.getPrincipal(groupPrincipalName);
                            if(!principal.getIsMemberOf().contains(groupPrincipal)) {
                                principal.getIsMemberOf().add(groupPrincipal);
                            }
                        }
                    }
                }
                else if(l.startsWith("Subject")) {
                    StringTokenizer t = new StringTokenizer(l, ";");
                    t.nextToken();
                    String subjectName = t.nextToken();
                    String subjectDescription = t.nextToken();
                    org.opencrx.security.identity1.jmi1.Subject subject = null;
                    try {
                    	subject = identitySegment.getSubject(subjectName);
                    }
                    catch(Exception e) {}
                    if(subject == null) {
                        try {
                        	subject = this.createSubject(
                        		identitySegment,
                        		subjectName,
                        		subjectDescription,
                        		new ArrayList<String>()
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
                
}

//--- End of File -----------------------------------------------------------

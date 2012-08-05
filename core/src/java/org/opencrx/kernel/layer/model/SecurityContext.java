/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: SecurityContext.java,v 1.37 2009/09/04 23:42:34 wfro Exp $
 * Description: SecurityContext
 * Revision:    $Revision: 1.37 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/09/04 23:42:34 $
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
package org.opencrx.kernel.layer.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.security.realm1.cci2.PrincipalGroupQuery;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

/**
 * A security context manages the subjects of one subject segment.
 */
public class SecurityContext {
    
    //-------------------------------------------------------------------------
    public SecurityContext(
        AccessControl_1 plugin,
        Path realmIdentity
    ) throws ServiceException {
        this.plugin = plugin;
        this.realm = (org.openmdx.security.realm1.jmi1.Realm)plugin.getPmAsRoot().getObjectById(realmIdentity);
        this.isActive = true;
        if(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY) != null) {
            this.isActive = "true".equals(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY));
        }
        if(!this.isActive) {
            System.out.println("WARNING: AccessControl_1 is not active. Activate with system property " + SecurityKeys.ENABLE_SECURITY_PROPERTY + "=true. Default is true.");
        }
    }
    
    //-------------------------------------------------------------------------
    private Set<String> getAllSubgroups(
        PrincipalGroup groupOf
    ) throws ServiceException {
        Set<String> subgroups = new HashSet<String>();
        subgroups.add(
            this.plugin.getQualifiedPrincipalName(groupOf.refGetPath())
        );
        for(Iterator<PrincipalGroup> i = this.groups.values().iterator(); i.hasNext(); ) {
        	PrincipalGroup group = i.next();
            if(group.getIsMemberOf().contains(groupOf)) {
                subgroups.addAll(
                    this.getAllSubgroups(group)
                );
                subgroups.add(
                    this.plugin.getQualifiedPrincipalName(group.refGetPath())
                );
            }
        }
        return subgroups;
    }
    
    //-------------------------------------------------------------------------
    private Set<String> getAllSupergroups(
        PrincipalGroup groupOf
    ) throws ServiceException {
        Set<String> supergroups = new HashSet<String>();
        supergroups.add(
            this.plugin.getQualifiedPrincipalName(groupOf.refGetPath())
        );
        List<org.openmdx.security.realm1.jmi1.Group> groups = groupOf.getIsMemberOf();
        for(org.openmdx.security.realm1.jmi1.Group group: groups) {
        	if(group instanceof PrincipalGroup) {
                supergroups.addAll(
                    this.getAllSupergroups((PrincipalGroup)group)
                );
        	}
        }
        return supergroups;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Assert the existence of the specified principal in the principals cache. 
     */
    @SuppressWarnings("unchecked")
    private void assertPrincipals(
        String principalName,
        boolean checkGroups
    ) throws ServiceException {
        PersistenceManager pm = this.plugin.getPmAsRoot();
        org.openmdx.security.realm1.jmi1.Principal principal = null;
        try {
            // Already present in cache
            if(this.principals.get(principalName) == null) {
                principal = (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
                    this.realm.refGetPath().getDescendant(new String[]{"principal", principalName})
                );            
                this.principals.put(
                    principalName,
                    principal
                );
            }
        }
        catch(Exception e) {
            // Remove principal if not found
            this.principals.remove(principalName);
        }
        SysLog.detail("principal cache size", new Integer(this.principals.size()));        
        if(checkGroups) {   
        	this.plugin.getPmAsRoot().evictAll();
            // Cache principal groups
        	PrincipalGroupQuery principalGroupQuery = (PrincipalGroupQuery)pm.newQuery(PrincipalGroup.class);
            List<PrincipalGroup> principalGroups = this.realm.getPrincipal(principalGroupQuery);
            this.groups = new HashMap<Path,PrincipalGroup>();
            for(
                Iterator<PrincipalGroup> i = principalGroups.iterator(); 
                i.hasNext(); 
            ) {
            	PrincipalGroup principalGroup = i.next();
                this.groups.put(
                    principalGroup.refGetPath(),
                    principalGroup
                );
            }            
            // Update group hierarchy
            this.subgroups = new HashMap<String,Set<String>>();
            this.supergroups = new HashMap<String,Set<String>>();
            for(Iterator<Map.Entry<Path,PrincipalGroup>> i = this.groups.entrySet().iterator(); i.hasNext(); ) {
            	Map.Entry<Path,PrincipalGroup> e = i.next();
                Path groupIdentity = e.getKey();
                PrincipalGroup group = e.getValue();
                this.supergroups.put(
                    this.plugin.getQualifiedPrincipalName(groupIdentity),
                    this.getAllSupergroups(group)
                );
                // Do not calculate subgroups for final groups
            	if(Boolean.TRUE.equals(group.isFinal())) {
	                this.subgroups.put(
	                    this.plugin.getQualifiedPrincipalName(groupIdentity),
	                    Collections.EMPTY_SET
	                );
            	}
            	else {
	                this.subgroups.put(
	                    this.plugin.getQualifiedPrincipalName(groupIdentity),
	                    this.getAllSubgroups(group)
	                );            		
            	}
            }
        }
    }
    
    //-------------------------------------------------------------------------
    protected org.openmdx.security.realm1.jmi1.Principal getPrincipal(
        String principalName
    ) throws ServiceException {        
    	org.openmdx.security.realm1.jmi1.Principal principal = this.principals == null ? 
    		null : 
    		this.principals.get(principalName);
        // Refresh subjects
        if( 
            (principal == null) ||
            (System.currentTimeMillis() > this.securityRealmCheckAt)
        ) {         
            PersistenceManager pm = this.plugin.getPmAsRoot();
            pm.refresh(this.realm);
            Date securityRealmModifiedAt = this.realm.getModifiedAt();
            this.assertPrincipals(               
                principalName,
                this.securityRealmModifiedAt == null || securityRealmModifiedAt.compareTo(this.securityRealmModifiedAt) != 0
            );
            this.securityRealmModifiedAt = securityRealmModifiedAt; 
            this.securityRealmCheckAt = System.currentTimeMillis() + SECURITY_REALM_CHECK_RATE;            
        }
        
	    if(principal == null) {
            principal = this.principals.get(principalName);
            if(principal == null) {
            	SysLog.warning("principal not found", principalName);
    	        throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_FOUND, 
                    "principal not found",
                    new BasicException.Parameter("realm", this.realm),
                    new BasicException.Parameter("principal", principalName)
    	        );
            }
	    }
	    return principal;
    }

    //-------------------------------------------------------------------------
    protected UserHome getUserHome(
        Path principalIdentity
    ) throws ServiceException {
        String providerName = principalIdentity.get(2);
        String segmentName = principalIdentity.get(principalIdentity.size()-3);
        PersistenceManager pm = this.plugin.getPmAsRoot();            
        return (UserHome)pm.getObjectById(
            new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + principalIdentity.getBase())
        );        
    }
    
    //-------------------------------------------------------------------------
    protected org.openmdx.security.realm1.jmi1.Principal getPrimaryGroup(
    	org.openmdx.security.realm1.jmi1.Principal principal
    ) throws ServiceException {
        try {
        	UserHome userHome = this.getUserHome(principal.refGetPath());
            PrincipalGroup userGroup = userHome.getPrimaryGroup();
            return userGroup == null ? 
            	this.getPrincipal(principal.refGetPath().getBase() + "." + SecurityKeys.USER_SUFFIX) : 
            	this.getPrincipal(userGroup.refGetPath().getBase());
        }
        // In case the principal does not have a user home
        // which defines the primary group fallback to user principal
        catch(Exception e) {
            return this.getPrincipal(principal.refGetPath().getBase() + "." + SecurityKeys.USER_SUFFIX);
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * Get user principal for the given principal. The group principal is
     * the assigned to the same subject as the given principal and is of type
     * PrincipalGroup. The owningGroup of a openCRX object is a PrincipalGroup.
     */
    protected org.opencrx.security.realm1.jmi1.User getUser(
    	org.openmdx.security.realm1.jmi1.Principal principal
    ) throws ServiceException {
    	org.opencrx.security.realm1.jmi1.User user = this.groupMapping.get(principal.refGetPath());
        if(user == null) {
        	PersistenceManager pm = this.plugin.getPmAsRoot();
        	org.opencrx.security.realm1.cci2.UserQuery userQuery = (org.opencrx.security.realm1.cci2.UserQuery)pm.newQuery(org.opencrx.security.realm1.jmi1.User.class);
        	userQuery.thereExistsSubject().equalTo(
        		principal.getSubject()
        	);
            List<org.opencrx.security.realm1.jmi1.User> owningGroups = this.realm.getPrincipal(userQuery);
            if(owningGroups.isEmpty()) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_FOUND, 
                    "Undefined user for principal",
                    new BasicException.Parameter("principal", principal)
                );                    
            }
            user = owningGroups.iterator().next();
            this.groupMapping.put(
            	principal.refGetPath(),
                user
            );
        }
        return user;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Return set of qualified names of subgroups of specified group subject. 
     */
    protected Set<String> getSubgroups(
        String qualifiedPrincipalName
    ) throws ServiceException {
        Set<String> subgroups = this.subgroups.get(qualifiedPrincipalName);
        return subgroups == null ? 
        	new HashSet<String>() : 
        	subgroups;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Return set of qualified names of subgroups of specified group subject. 
     */
    protected Set<String> getSupergroups(
        String qualifiedPrincipalName
    ) throws ServiceException {
        Set<String> supergroups = this.supergroups.get(qualifiedPrincipalName);
        return supergroups == null ? 
        	new HashSet<String>() : 
        	supergroups;
    }
    
    //-------------------------------------------------------------------------
    protected Set<String> getMemberships(
    	org.openmdx.security.realm1.jmi1.Principal principal,
    	org.opencrx.security.realm1.jmi1.User user,
        short accessLevel,
        boolean useExtendedAccessLevelBasic
    ) throws ServiceException {
        // GLOBAL
        if(!this.isActive || (accessLevel == SecurityKeys.ACCESS_LEVEL_GLOBAL)) {
            return null;
        }
        Set<String> allowedPrincipals = new HashSet<String>();
        try {
            // PRIVATE --> grant requesting user access to all owned objects
            if(accessLevel >= SecurityKeys.ACCESS_LEVEL_PRIVATE) {
                allowedPrincipals.add(
                    this.plugin.getQualifiedPrincipalName(principal.refGetPath())
                );
	            allowedPrincipals.add(
	                this.plugin.getQualifiedPrincipalName(user.refGetPath())
	            );
            }
            // BASIC, DEEP --> all direct subgroups, supergroups 
            if((accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) || (accessLevel == SecurityKeys.ACCESS_LEVEL_BASIC)) {
            	List<org.openmdx.security.realm1.jmi1.Group> groups = principal.getIsMemberOf();
	            if(useExtendedAccessLevelBasic) {
	                for(org.openmdx.security.realm1.jmi1.Group group: groups) {
	                    allowedPrincipals.addAll(
	                        this.getSubgroups(this.plugin.getQualifiedPrincipalName(group.refGetPath()))
	                    );
	                }	                
	            }
                // Add all supergroups
	            for(org.openmdx.security.realm1.jmi1.Group group: groups) {
	                allowedPrincipals.addAll(
	                    this.getSupergroups(this.plugin.getQualifiedPrincipalName(group.refGetPath()))
	                );
	            }
            }
            // DEEP --> all subgroups of direct and supergroups
            if(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) {
	            // All subgroups of all supergroups
	            for(Iterator<String> i = new HashSet<String>(allowedPrincipals).iterator(); i.hasNext(); ) {
	                allowedPrincipals.addAll(
	                    this.getSubgroups(i.next())
	                );
	            }
                // ... and their supergroups
                for(Iterator<String> i = new HashSet<String>(allowedPrincipals).iterator(); i.hasNext(); ) {
                    allowedPrincipals.addAll(
                        this.getSupergroups(i.next())
                    );
                }
            }
        }
        catch(ServiceException e) {
        	SysLog.warning("requesting principal can not be determined. Set of allowed principals = {}");
            return allowedPrincipals;
        }
        // member of Root:Administrators --> access level global
        return allowedPrincipals.contains(SecurityKeys.ROOT_ADMINISTRATORS_GROUP) ? 
        	null : 
        	allowedPrincipals;
    }
        
    //-----------------------------------------------------------------------
    // Variables
    //-----------------------------------------------------------------------
    private static final int SECURITY_REALM_CHECK_RATE = 10000;
    
    private final AccessControl_1 plugin;
    private final org.openmdx.security.realm1.jmi1.Realm realm;
    
    private Map<String,org.openmdx.security.realm1.jmi1.Principal> principals = 
    	new HashMap<String,org.openmdx.security.realm1.jmi1.Principal>();
    private Map<Path,PrincipalGroup> groups = null;
    private boolean isActive = true;
    private long securityRealmCheckAt = 0;
    private Date securityRealmModifiedAt = null;
    
    // Cache for group membership with qualified group name as key
    // and list of subgroups as values.
    private Map<String,Set<String>> subgroups = null;
    private Map<String,Set<String>> supergroups = null;
    
    // principal -> principal group. Key is principal path,
    // value is principal group path
    private final Map<Path,org.opencrx.security.realm1.jmi1.User> groupMapping = 
    	new HashMap<Path,org.opencrx.security.realm1.jmi1.User>();

}

//--- End of File -----------------------------------------------------------

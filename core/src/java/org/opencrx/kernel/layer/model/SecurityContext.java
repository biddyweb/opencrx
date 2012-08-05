/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: SecurityContext.java,v 1.27 2009/02/20 21:44:49 wfro Exp $
 * Description: SecurityContext
 * Revision:    $Revision: 1.27 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/20 21:44:49 $
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.Orders;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.kernel.exception.BasicException;

/**
 * A security context manages the subjects of one subject segment.
 */
public class SecurityContext {
    
    //-------------------------------------------------------------------------
    public SecurityContext(
        AccessControl_1 plugin,
        Path realmIdentity
    ) {
        this.plugin = plugin;
        this.realmIdentity = realmIdentity;
        this.isActive = true;
        if(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY) != null) {
            this.isActive = "true".equals(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY));
        }
        if(!this.isActive) {
            System.out.println("WARNING: AccessControl_1 is not active. Activate with system property " + SecurityKeys.ENABLE_SECURITY_PROPERTY + "=true. Default is true.");
        }
    }
    
    //-------------------------------------------------------------------------
    private Set getAllSubgroups(
        Path groupIdentity
    ) throws ServiceException {
        Set subgroups = new HashSet();
        subgroups.add(
            this.plugin.getQualifiedPrincipalName(groupIdentity)
        );
        for(Iterator i = this.groups.values().iterator(); i.hasNext(); ) {
            DataproviderObject_1_0 group = (DataproviderObject_1_0)i.next();
            if(group.values("isMemberOf").contains(groupIdentity)) {
                subgroups.addAll(
                    this.getAllSubgroups(group.path())
                );
                subgroups.add(
                    this.plugin.getQualifiedPrincipalName(group.path())
                );
            }
        }
        return subgroups;
    }
    
    //-------------------------------------------------------------------------
    private Set getAllSupergroups(
        Path groupIdentity
    ) throws ServiceException {
        Set supergroups = new HashSet();
        DataproviderObject_1_0 group = (DataproviderObject_1_0)this.groups.get(groupIdentity);
        if(group == null) {
            AppLog.error("Can not find group in group list", "group=" + groupIdentity + "; groups=" + this.groups);
        }
        else {
            supergroups.add(
                this.plugin.getQualifiedPrincipalName(groupIdentity)
            );
            for(Iterator i = group.values("isMemberOf").iterator(); i.hasNext(); ) {
                supergroups.addAll(
                    this.getAllSupergroups((Path)i.next())
                );
            }
        }
        return supergroups;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Assert the existence of the specified principal in the principals cache. 
     */
    private void assertPrincipals(
        String principalName,
        boolean checkGroups
    ) throws ServiceException {
        RequestCollection delegation = this.plugin.getRunAsRootDelegation();
        DataproviderObject_1_0 principal = null;
        try {
            // Already present in cache
            if(this.principals.get(principalName) == null) {
                principal = delegation.addGetRequest(
                    this.realmIdentity.getDescendant(new String[]{"principal", principalName})
                );            
                this.principals.put(
                    principalName,
                    principal
                );
            }
        }
        catch(ServiceException e) {
            // Remove principal if not found
            this.principals.remove(principalName);
        }
        AppLog.info("principal cache size", new Integer(this.principals.size()));
        
        if(checkGroups) {
            // Cache principal groups
            List principalGroups = delegation.addFindRequest(
                this.realmIdentity.getChild("principal"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN,
                        new Object[]{"org:opencrx:security:realm1:PrincipalGroup"}                    
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Orders.ASCENDING
            );
            this.groups = new HashMap();
            for(
                Iterator i = principalGroups.iterator(); 
                i.hasNext(); 
            ) {
                DataproviderObject_1_0 principalGroup = (DataproviderObject_1_0)i.next();
                this.groups.put(
                    principalGroup.path(),
                    principalGroup
                );
            }            
            // Update group hierarchy
            this.subgroups = new HashMap();
            this.supergroups = new HashMap();
            for(Iterator i = this.groups.keySet().iterator(); i.hasNext(); ) {
                Path groupIdentity = (Path)i.next();
                this.subgroups.put(
                    this.plugin.getQualifiedPrincipalName(groupIdentity),
                    this.getAllSubgroups(groupIdentity)
                );
                this.supergroups.put(
                    this.plugin.getQualifiedPrincipalName(groupIdentity),
                    this.getAllSupergroups(groupIdentity)
                );
            }
        }
    }
    
    //-------------------------------------------------------------------------
    protected DataproviderObject_1_0 getPrincipal(
        String principalName
    ) throws ServiceException {
        
        DataproviderObject_1_0 principal = this.principals == null
            ? null
            : (DataproviderObject_1_0)this.principals.get(principalName);

        // Refresh subjects
        if( 
            (principal == null) ||
            (System.currentTimeMillis() > this.securityRealmCheckAt)
        ) { 
            RequestCollection delegation = this.plugin.getRunAsRootDelegation();            
            DataproviderObject_1_0 realm = delegation.addGetRequest(
                this.realmIdentity,                
                AttributeSelectors.ALL_ATTRIBUTES,
                new AttributeSpecifier[]{}                        
            );
            String securityRealmModifiedAt = (String)realm.values(SystemAttributes.MODIFIED_AT).get(0);
            this.assertPrincipals(               
                principalName,
                securityRealmModifiedAt.compareTo(this.securityRealmModifiedAt) != 0
            );
            this.securityRealmModifiedAt = securityRealmModifiedAt; 
            this.securityRealmCheckAt = System.currentTimeMillis() + SECURITY_REAL_CHECK_RATE;            
        }
        
	    if(principal == null) {
            principal = (DataproviderObject_1_0)this.principals.get(principalName);
            if(principal == null) {
    	        AppLog.warning("principal not found", principalName);
    	        throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_FOUND, 
                    "principal not found",
                    new BasicException.Parameter("realm", this.realmIdentity),
                    new BasicException.Parameter("principal", principalName)
    	        );
            }
	    }
	    return principal;
    }

    //-------------------------------------------------------------------------
    protected DataproviderObject_1_0 getUserHome(
        Path principalIdentity
    ) throws ServiceException {
        String providerName = principalIdentity.get(2);
        String segmentName = principalIdentity.get(principalIdentity.size()-3);
        RequestCollection delegation = this.plugin.getRunAsRootDelegation();            
        return delegation.addGetRequest(
            new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + principalIdentity.getBase()),
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{}                        
        );        
    }
    
    //-------------------------------------------------------------------------
    protected DataproviderObject_1_0 getPrimaryGroup(
        DataproviderObject_1_0 principal
    ) throws ServiceException {
        try {
            DataproviderObject_1_0 userHome = this.getUserHome(principal.path());
            Path userGroupIdentity = (Path)userHome.values("primaryGroup").get(0);
            return userGroupIdentity == null
                ? this.getPrincipal(principal.path().getBase() + "." + SecurityKeys.USER_SUFFIX)
                : this.getPrincipal(userGroupIdentity.getBase());
        }
        // In case the principal does not have a user home
        // which defines the primary group fallback to user principal
        catch(Exception e) {
            return this.getPrincipal(principal.path().getBase() + "." + SecurityKeys.USER_SUFFIX);
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * Get group principal for the given principal. The group principal is
     * the assigned to the same subject as the given principal and is of type
     * PrincipalGroup. The owningGroup of a openCRX object is a PrincipalGroup.
     */
    protected DataproviderObject_1_0 getGroup(
        DataproviderObject_1_0 principal
    ) throws ServiceException {
        DataproviderObject_1_0 group = (DataproviderObject_1_0)this.groupMapping.get(principal.path());
        if(group == null) {
            RequestCollection delegation = this.plugin.getRunAsRootDelegation();            
            List owningGroups = delegation.addFindRequest(
                this.realmIdentity.getChild("principal"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "subject",
                        FilterOperators.IS_IN,
                        new Object[]{
                            principal.values("subject").get(0)
                        }
                    ),
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN,
                        new Object[]{
                            "org:opencrx:security:realm1:User"
                        }
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                0,
                Integer.MAX_VALUE,
                Orders.ASCENDING
            );
            if(owningGroups.isEmpty()) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_FOUND, 
                    "Undefined user for principal",
                    new BasicException.Parameter("principal", principal.path())
                );                    
            }
            group = (DataproviderObject_1_0)owningGroups.iterator().next();
            this.groupMapping.put(
                principal.path(),
                group
            );
        }
        return group;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Return set of qualified names of subgroups of specified group subject. 
     */
    protected Set getSubgroups(
        String qualifiedPrincipalName
    ) throws ServiceException {
        Set subgroups = (Set)this.subgroups.get(qualifiedPrincipalName);
        return subgroups == null
            ? new HashSet()
            : subgroups;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Return set of qualified names of subgroups of specified group subject. 
     */
    protected Set getSupergroups(
        String qualifiedPrincipalName
    ) throws ServiceException {
        Set supergroups = (Set)this.supergroups.get(qualifiedPrincipalName);
        return supergroups == null
            ? new HashSet()
            : supergroups;
    }
    
    //-------------------------------------------------------------------------
    protected Set<String> getMemberships(
        DataproviderObject_1_0 principal,
        DataproviderObject_1_0 user,
        short accessLevel,
        boolean useExtendedAccessLevelBasic
    ) {
        // GLOBAL
        if(!this.isActive || (accessLevel == SecurityKeys.ACCESS_LEVEL_GLOBAL)) {
            return null;
        }
        Set<String> allowedPrincipals = new HashSet<String>();
        try {
            // PRIVATE --> grant requesting user access to all owned objects
            if(accessLevel >= SecurityKeys.ACCESS_LEVEL_PRIVATE) {
                allowedPrincipals.add(
                    this.plugin.getQualifiedPrincipalName(principal.path())
                );
	            allowedPrincipals.add(
	                this.plugin.getQualifiedPrincipalName(user.path())
	            );
            }
            // BASIC, DEEP --> all direct subgroups, supergroups 
            if((accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) || (accessLevel == SecurityKeys.ACCESS_LEVEL_BASIC)) {
	            if(useExtendedAccessLevelBasic) {
	                for(Iterator i = principal.values("isMemberOf").iterator(); i.hasNext(); ) {
	                    Path groupIdentity = (Path)i.next();
	                    allowedPrincipals.addAll(
	                        this.getSubgroups(this.plugin.getQualifiedPrincipalName(groupIdentity))
	                    );
	                }	                
	            }
                // Add all supergroups
	            for(Iterator<Object> i = principal.values("isMemberOf").iterator(); i.hasNext(); ) {
	                Path groupIdentity = (Path)i.next();
	                allowedPrincipals.addAll(
	                    this.getSupergroups(this.plugin.getQualifiedPrincipalName(groupIdentity))
	                );
	            }
            }
            // DEEP --> all subgroups of direct and supergroups
            if(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) {
	            // All subgroups of all supergroups
	            for(Iterator<String> i = new HashSet(allowedPrincipals).iterator(); i.hasNext(); ) {
	                allowedPrincipals.addAll(
	                    this.getSubgroups(i.next())
	                );
	            }
                // ... and their supergroups
                for(Iterator<String> i = new HashSet(allowedPrincipals).iterator(); i.hasNext(); ) {
                    allowedPrincipals.addAll(
                        this.getSupergroups(i.next())
                    );
                }
            }
        }
        catch(ServiceException e) {
            AppLog.warning("requesting principal can not be determined. Set of allowed principals = {}");
            return allowedPrincipals;
        }
        // member of Root:Administrators --> access level global
        return allowedPrincipals.contains(SecurityKeys.ROOT_ADMINISTRATORS_GROUP)
            ? null
            : allowedPrincipals;
    }
        
    //-----------------------------------------------------------------------
    // Variables
    //-----------------------------------------------------------------------
    private static final int SECURITY_REAL_CHECK_RATE = 10000;
    
    private final AccessControl_1 plugin;
    private final Path realmIdentity;
    
    private Map principals = new HashMap();
    private Map groups = null;
    private boolean isActive = true;
    private long securityRealmCheckAt = 0;
    private String securityRealmModifiedAt = "";
    
    // Cache for group membership with qualified group name as key
    // and list of subgroups as values.
    private Map subgroups = null;
    private Map supergroups = null;
    
    // principal -> principal group. Key is principal path,
    // value is principal group path
    private final Map groupMapping = new HashMap();

}

//--- End of File -----------------------------------------------------------

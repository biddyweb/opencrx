/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: SecurityContext.java,v 1.47 2010/06/14 14:57:04 wfro Exp $
 * Description: SecurityContext
 * Revision:    $Revision: 1.47 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/06/14 14:57:04 $
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
	public static class CachedPrincipal {
	
		public CachedPrincipal(
			org.openmdx.security.realm1.jmi1.Principal principal
		) {
			this.principalIdentity = principal.refGetPath();
			this.isMemberOf = new HashSet<Path>();
			List<org.openmdx.security.realm1.jmi1.Group> groups = principal.getIsMemberOf();
			for(org.openmdx.security.realm1.jmi1.Group group: groups) {
				this.isMemberOf.add(group.refGetPath());
			}
		}
		
		public Path getIdentity(
		) {
			return this.principalIdentity;
		}
		
		public void setPrimaryGroup(
			Path primaryGroup
		) {
			this.primaryGroup = primaryGroup;
		}
		
		public Path getPrimaryGroup(
		) {
			return this.primaryGroup;
		}
		
		public Set<Path> getIsMemberOf(
		) {
			return this.isMemberOf;
		}
		
		private final Path principalIdentity;
		private final Set<Path> isMemberOf;
		private Path primaryGroup;
	}

    //-------------------------------------------------------------------------
    public SecurityContext(
        AccessControl_1 plugin,
        Path realmIdentity
    ) throws ServiceException {
        this.plugin = plugin;
        this.realmIdentity = realmIdentity;
        this.isActive = true;
        if(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY) != null) {
            this.isActive = "true".equals(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY));
        }
        if(!this.isActive) {
            System.out.println("WARNING: AccessControl_1 is not active. Activate with system property " + SecurityKeys.ENABLE_SECURITY_PROPERTY + "=true. Default is true.");
        }
        if(System.getProperty(SecurityKeys.REALM_REFRESH_RATE_MILLIS) != null) {
        	this.realmRefreshRateMillis = Long.valueOf(System.getProperty(SecurityKeys.REALM_REFRESH_RATE_MILLIS));
        }
    }
    
    //-------------------------------------------------------------------------
    private Set<String> getAllSubgroups(
        PrincipalGroup group,
        Map<Path,Set<PrincipalGroup>> directSubgroups
    ) throws ServiceException {
    	Path groupIdentity = group.refGetPath();
        Set<String> subgroups = new HashSet<String>();
        subgroups.add(
            this.plugin.getQualifiedPrincipalName(groupIdentity)
        );
        if(directSubgroups.get(groupIdentity) != null) {
	        for(PrincipalGroup subgroup: directSubgroups.get(groupIdentity)) {
                subgroups.addAll(
           			this.getAllSubgroups(subgroup, directSubgroups)
                );
                subgroups.add(this.plugin.getQualifiedPrincipalName(subgroup.refGetPath()));
	        }
        }
        return subgroups;
    }
    
    //-------------------------------------------------------------------------
    private Set<String> getAllSupergroups(
        PrincipalGroup groupOf,
        Map<String,Set<String>> allSupergroups
    ) throws ServiceException {
        Set<String> supergroups = new HashSet<String>();
        supergroups.add(
            this.plugin.getQualifiedPrincipalName(groupOf.refGetPath())
        );
        List<org.openmdx.security.realm1.jmi1.Group> groups = groupOf.getIsMemberOf();
        for(org.openmdx.security.realm1.jmi1.Group group: groups) {
        	if(group instanceof PrincipalGroup) {
            	String groupId = this.plugin.getQualifiedPrincipalName(group.refGetPath());
                supergroups.addAll(
                	allSupergroups.containsKey(groupId) ?
                		allSupergroups.get(groupId) :
                			this.getAllSupergroups((PrincipalGroup)group, allSupergroups)
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
        PersistenceManager pm = this.plugin.getDelegatingPersistenceManager();
        org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(this.realmIdentity);
        org.openmdx.security.realm1.jmi1.Principal principal = null;
        try {
            // Already present in cache
            if(this.principals.get(principalName) == null) {
                principal = (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
                    this.realmIdentity.getDescendant(new String[]{"principal", principalName})
                );            
                this.principals.put(
                    principalName,
                    new CachedPrincipal(principal)
                );
            }
        }
        catch(Exception e) {
            // Remove principal if not found
            this.principals.remove(principalName);
        }
        SysLog.detail("principal cache size", new Integer(this.principals.size()));        
        if(checkGroups) {   
            // Cache principal groups
        	PrincipalGroupQuery principalGroupQuery = (PrincipalGroupQuery)pm.newQuery(PrincipalGroup.class);
            List<PrincipalGroup> principalGroups = realm.getPrincipal(principalGroupQuery);
            Map<Path,PrincipalGroup> allGroups = new HashMap<Path,PrincipalGroup>();
            for(
                Iterator<PrincipalGroup> i = principalGroups.iterator(); 
                i.hasNext(); 
            ) {
            	PrincipalGroup principalGroup = i.next();
                allGroups.put(
                    principalGroup.refGetPath(),
                    principalGroup
                );
            }            
            // Update group hierarchy            

            // Prepare groups membership
            Map<Path,Set<PrincipalGroup>> directSubgroups = new HashMap<Path,Set<PrincipalGroup>>();
            for(PrincipalGroup group: allGroups.values()) {
                List<PrincipalGroup> supergroups = group.getIsMemberOf();
                for(PrincipalGroup supergroup: supergroups) {
                	Path supergroupIdentity = supergroup.refGetPath();
                	Set<PrincipalGroup> subgroups = directSubgroups.get(supergroupIdentity);
                	if(subgroups == null) {
                		directSubgroups.put(
                			supergroupIdentity,
                			subgroups = new HashSet<PrincipalGroup>()
                		);
                	}
                	subgroups.add(group);
                }
            }
            Map<String,Set<String>> allSupergroups = new HashMap<String,Set<String>>();
            Map<String,Set<String>> allSubgroups = new HashMap<String,Set<String>>();
            for(Iterator<Map.Entry<Path,PrincipalGroup>> i = allGroups.entrySet().iterator(); i.hasNext(); ) {
            	Map.Entry<Path,PrincipalGroup> e = i.next();
                Path groupIdentity = e.getKey();
                PrincipalGroup group = e.getValue();
                allSupergroups.put(
                    this.plugin.getQualifiedPrincipalName(groupIdentity),
                    this.getAllSupergroups(group, allSupergroups)
                );
                // Do not calculate subgroups for final groups
            	if(Boolean.TRUE.equals(group.isFinal())) {
	                allSubgroups.put(
	                    this.plugin.getQualifiedPrincipalName(groupIdentity),
	                    Collections.EMPTY_SET
	                );
            	}
            	else {
	                allSubgroups.put(
	                    this.plugin.getQualifiedPrincipalName(groupIdentity),
	                    this.getAllSubgroups(group, directSubgroups)
	                );            		
            	}
            }
            this.supergroups = allSupergroups;
            this.subgroups = allSubgroups;
        }
        pm.close();
    }
    
    //-------------------------------------------------------------------------
    protected CachedPrincipal getPrincipal(
        String principalName
    ) throws ServiceException {        
    	CachedPrincipal principal = this.principals == null ? 
    		null : 
    		this.principals.get(principalName);
        // Refresh subjects
        if( 
            (principal == null) ||
            (System.currentTimeMillis() > this.realmRefreshAt)
        ) {   
        	if(!this.isRefreshing) {
        		try {
        			// Must not be 100% thread-safe. We only want to safe CPU in 
        			// case multiple threads refresh the cache at the same time.
        			this.isRefreshing = true;
		            PersistenceManager pm = this.plugin.getDelegatingPersistenceManager();
		            org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(this.realmIdentity);
		            Date securityRealmModifiedAt = realm.getModifiedAt();
		            this.assertPrincipals(               
		                principalName,
		                this.realmModifiedAt == null || securityRealmModifiedAt.compareTo(this.realmModifiedAt) != 0
		            );
		            this.realmModifiedAt = securityRealmModifiedAt; 
		            this.realmRefreshAt = System.currentTimeMillis() + this.realmRefreshRateMillis;
		            pm.close();
        		} finally {
        			this.isRefreshing = false;
        		}
        	}
        }
        // Principal not found in current state of cache. Either the principal does not 
        // exist or it exists but will only visible after refreshing the cache. 
        // Throw a NOT_FOUND now and retry at a later time.
	    if(principal == null) {
            principal = this.principals.get(principalName);
            if(principal == null) {
            	SysLog.warning("principal not found", principalName);
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
    protected Path getPrimaryGroup(
    	CachedPrincipal principal
    ) throws ServiceException {
        try {
        	if(principal.getPrimaryGroup() == null) {
	            String providerName = principal.getIdentity().get(2);
	            String segmentName = principal.getIdentity().get(principal.getIdentity().size()-3);
	            PersistenceManager pm = this.plugin.getDelegatingPersistenceManager();            
	            UserHome userHome = (UserHome)pm.getObjectById(
	                new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + principal.getIdentity().getBase())
	            );        
	            PrincipalGroup userGroup = userHome.getPrimaryGroup();
	            Path primaryGroup = userGroup == null ? 
	            	this.getPrincipal(principal.getIdentity().getBase() + "." + SecurityKeys.USER_SUFFIX).getIdentity() : 
	            		this.getPrincipal(userGroup.refGetPath().getBase()).getIdentity();
	            principal.setPrimaryGroup(primaryGroup);
	            pm.close();	            
        	}
        	return principal.getPrimaryGroup();
        }
        // In case the principal does not have a user home
        // which defines the primary group fallback to user principal
        catch(Exception e) {
        	Path primaryGroup = 
        		this.getPrincipal(principal.getIdentity().getBase() + "." + SecurityKeys.USER_SUFFIX).getIdentity();
        	principal.setPrimaryGroup(primaryGroup);
        	return primaryGroup;
        }
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
    	CachedPrincipal principal,
    	Path userIdentity,
        short accessLevel,
        boolean useExtendedAccessLevelBasic
    ) throws ServiceException {
        // GLOBAL
        if(!this.isActive || (accessLevel == SecurityKeys.ACCESS_LEVEL_GLOBAL)) {
            return null;
        }
        Set<String> memberships = new HashSet<String>();
        try {
            // PRIVATE --> grant requesting user access to all owned objects
            if(accessLevel >= SecurityKeys.ACCESS_LEVEL_PRIVATE) {
                memberships.add(
                    this.plugin.getQualifiedPrincipalName(principal.getIdentity())
                );
	            memberships.add(
	                this.plugin.getQualifiedPrincipalName(userIdentity)
	            );
            }
            // BASIC, DEEP --> all direct subgroups, supergroups 
            if((accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) || (accessLevel == SecurityKeys.ACCESS_LEVEL_BASIC)) {
            	Set<Path> groups = principal.getIsMemberOf();
	            if(useExtendedAccessLevelBasic) {
	                for(Path group: groups) {
	                    memberships.addAll(
	                        this.getSubgroups(this.plugin.getQualifiedPrincipalName(group))
	                    );
	                }	                
	            }
                // Add all supergroups
	            for(Path group: groups) {
	                memberships.addAll(
	                    this.getSupergroups(this.plugin.getQualifiedPrincipalName(group))
	                );
	            }
            }
            // DEEP --> all subgroups of direct and supergroups
            if(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) {
	            // All subgroups of all supergroups
	            for(Iterator<String> i = new HashSet<String>(memberships).iterator(); i.hasNext(); ) {
	                memberships.addAll(
	                    this.getSubgroups(i.next())
	                );
	            }
                // ... and their supergroups
                for(Iterator<String> i = new HashSet<String>(memberships).iterator(); i.hasNext(); ) {
                    memberships.addAll(
                        this.getSupergroups(i.next())
                    );
                }
            }
        }
        catch(ServiceException e) {
        	SysLog.warning("requesting principal can not be determined. Set of allowed principals = {}");
            return memberships;
        }
        // member of Root:Administrators --> access level global
        return memberships.contains(SecurityKeys.ROOT_ADMINISTRATORS_GROUP) ? 
        	null : 
        	memberships;
    }
        
    //-----------------------------------------------------------------------
    // Variables
    //-----------------------------------------------------------------------    
    private final AccessControl_1 plugin;
    private final Path realmIdentity;
    
    private Map<String,CachedPrincipal> principals = new HashMap<String,CachedPrincipal>();
    private boolean isActive = true;
    private long realmRefreshRateMillis = 10000;
    private long realmRefreshAt = 0;
    private Date realmModifiedAt = null;
    
    // Cache for group membership with qualified group name as key
    // and list of subgroups as values.
    private boolean isRefreshing = false;
    private Map<String,Set<String>> subgroups = null;
    private Map<String,Set<String>> supergroups = null;
    
}

//--- End of File -----------------------------------------------------------

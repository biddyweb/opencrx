/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: SecurityContext.java,v 1.58 2011/03/09 13:19:41 wfro Exp $
 * Description: SecurityContext
 * Revision:    $Revision: 1.58 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/03/09 13:19:41 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.security.realm1.cci2.PrincipalGroupQuery;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

/**
 * A security context manages the principals of one realm.
 */
public class SecurityContext {
    
    //-------------------------------------------------------------------------
	public class CachedPrincipal {
	
		public CachedPrincipal(
			org.openmdx.security.realm1.jmi1.Principal principal,
			Set<String> allSupergroups,
			long expiresAt
		) {
			this.principalIdentity = principal.refGetPath();
			this.isMemberOf = new HashSet<String>();
			List<org.openmdx.security.realm1.jmi1.Group> groups = principal.getIsMemberOf();			
			for(Iterator<org.openmdx.security.realm1.jmi1.Group> i = groups.iterator(); i.hasNext(); ) {
				try {
					org.openmdx.security.realm1.jmi1.Group group = i.next();
					this.isMemberOf.add(group.refGetPath().getBase());
				}
				catch(Exception e) {
					ServiceException e0 = new ServiceException(
						e,
						OpenCrxException.DOMAIN,
						BasicException.Code.GENERIC,
						"Unable to principal's group membership",
						new BasicException.Parameter("principal", principal.refGetPath())
					);
					e0.log();
				}
			}
			this.allSupergroups = allSupergroups;
			this.expiresAt = expiresAt;
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
		
		public Set<String> getIsMemberOf(
		) {
			return this.isMemberOf;
		}
		
		public long getExpiresAt(
		) {
			return this.expiresAt;
		}
		
	    private List<PrincipalGroup> getSubgroups(
	    	PersistenceManager pm
	    ) throws ServiceException {
	    	org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(SecurityContext.this.realmIdentity);
	    	org.openmdx.security.realm1.jmi1.Principal group = (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
	    		this.principalIdentity
	    	);
	    	// Do not calculate subgroups for final groups
	    	if(group instanceof PrincipalGroup && Boolean.TRUE.equals(((PrincipalGroup)group).isFinal())) {
	    		return Collections.emptyList();
	    	}
		    PrincipalGroupQuery query = (PrincipalGroupQuery)pm.newQuery(PrincipalGroup.class);
	    	query.thereExistsIsMemberOf().equalTo(group);
	    	return realm.getPrincipal(query);
	    }

		private Set<String> getAllSubgroups(
			PersistenceManager pm
		) throws ServiceException {
            Set<String> allSubgroups = new HashSet<String>();
            allSubgroups.add(SecurityContext.this.plugin.getQualifiedPrincipalName(this.principalIdentity));
            for(PrincipalGroup subgroup: this.getSubgroups(pm)) {
            	String qualifiedGroupName = SecurityContext.this.plugin.getQualifiedPrincipalName(subgroup.refGetPath());
            	if(!allSubgroups.contains(qualifiedGroupName)) {
                	allSubgroups.addAll(
                		SecurityContext.this.getPrincipal(qualifiedGroupName).getAllSubgroups()
                	);
            	}
            }
            return allSubgroups;
		}
		
		public Set<String> getAllSubgroups(
		) throws ServiceException {
			if(this.allSubgroups == null) {
				PersistenceManager pm = SecurityContext.this.plugin.newDelegatingPersistenceManager();
				this.allSubgroups = this.getAllSubgroups(pm);
                pm.close();
			}
			return this.allSubgroups;
		}
		
		public Set<String> getAllSupergroups(
		) {
			return this.allSupergroups;
		}
		
		private long expiresAt;
		private final Path principalIdentity;
		private final Set<String> isMemberOf;
		private Set<String> allSubgroups = null;
		private final Set<String> allSupergroups;
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
        	this.cachedObjectTTLMillis = Long.valueOf(System.getProperty(SecurityKeys.REALM_REFRESH_RATE_MILLIS));
        }
    }
    
    //-------------------------------------------------------------------------
    protected CachedPrincipal getPrincipal(
        String principalName
    ) throws ServiceException {
    	// In case name is qualified
    	if(principalName.indexOf(":") > 0) {
    		principalName = principalName.substring(principalName.indexOf(":") + 1);
    	}
    	CachedPrincipal cachedPrincipal = this.cachedPrincipals.get(principalName);
    	if(cachedPrincipal == null || System.currentTimeMillis() > cachedPrincipal.getExpiresAt()) {
            PersistenceManager pm = this.plugin.newDelegatingPersistenceManager();
            org.openmdx.security.realm1.jmi1.Principal principal = null;
            try {
                principal = (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
                    this.realmIdentity.getDescendant(new String[]{"principal", principalName})
                );
                Set<String> allSupergroups = new HashSet<String>();
                allSupergroups.add(this.plugin.getQualifiedPrincipalName(principal.refGetPath()));
                List<org.openmdx.security.realm1.jmi1.Group> supergroups = principal.getIsMemberOf();
                for(Iterator<org.openmdx.security.realm1.jmi1.Group> i = supergroups.iterator(); i.hasNext(); ) {
                	try {
                		org.openmdx.security.realm1.jmi1.Group supergroup = i.next();
	                	String qualifiedGroupName = this.plugin.getQualifiedPrincipalName(supergroup.refGetPath());
	                	if(!allSupergroups.contains(qualifiedGroupName)) {
		                	allSupergroups.addAll(
		                		this.getPrincipal(qualifiedGroupName).getAllSupergroups()
		                	);
	                	}
                	}
                	catch(Exception e) {
    					ServiceException e0 = new ServiceException(
    						e,
    						OpenCrxException.DOMAIN,
    						BasicException.Code.GENERIC,
    						"Unable to principal's group membership",
    						new BasicException.Parameter("principal", principal.refGetPath())
    					);
    					e0.log();
                	}
                }
                this.cachedPrincipals.put(
                    principalName,
                    cachedPrincipal = new CachedPrincipal(
                    	principal,
                    	allSupergroups,
                    	System.currentTimeMillis() + this.cachedObjectTTLMillis
                    )
                );
            }
            catch(Exception e) {
            	new ServiceException(e).log();
                this.cachedPrincipals.remove(principalName);
            }
            finally {
            	pm.close();
            }    		
    	}
	    if(cachedPrincipal == null) {
        	SysLog.warning("principal not found", principalName);
	        throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.NOT_FOUND, 
                "principal not found",
                new BasicException.Parameter("realm", this.realmIdentity),
                new BasicException.Parameter("principal", principalName)
	        );
	    }
	    return cachedPrincipal;
    }

    //-------------------------------------------------------------------------
    protected Path getPrimaryGroup(
    	CachedPrincipal principal
    ) throws ServiceException {
        try {
        	if(principal.getPrimaryGroup() == null) {
	            String providerName = principal.getIdentity().get(2);
	            String segmentName = principal.getIdentity().get(principal.getIdentity().size()-3);
	            PersistenceManager pm = this.plugin.newDelegatingPersistenceManager();            
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
    protected Set<String> getMemberships(
    	CachedPrincipal principal,
    	Path userIdentity,
        short accessLevel,
        boolean useExtendedAccessLevelBasic
    ) {
        // GLOBAL or Root
        if(
        	!this.isActive || 
        	(accessLevel == SecurityKeys.ACCESS_LEVEL_GLOBAL) || 
        	principal.getIdentity().getBase().equals(SecurityKeys.ROOT_PRINCIPAL)
        ) {
            return null;
        }
        Set<String> memberships = new HashSet<String>();
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
        if(
        	(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) || 
        	(accessLevel == SecurityKeys.ACCESS_LEVEL_BASIC)
        ) {
        	Set<String> groups = principal.getIsMemberOf();
            if(useExtendedAccessLevelBasic) {	            	
                for(String group: groups) {
                	try {
                		CachedPrincipal p = this.getPrincipal(group);
	                    memberships.addAll(
	                        p.getAllSubgroups()
	                    );
                	} catch(Exception e) {
                		new ServiceException(e).log();
                	}
                }
            }
            // Add all supergroups
            for(String group: groups) {
            	try {
            		CachedPrincipal p = this.getPrincipal(group);
	                memberships.addAll(
	                	p.getAllSupergroups()
	                );
            	} catch(Exception e) {
            		new ServiceException(e).log();
            	}
            }
        }
        // DEEP --> all subgroups of direct and supergroups
        if(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) {
            // All subgroups of all supergroups
        	Set<String> newMemberships = new HashSet<String>();
            for(String group: memberships) {
            	try {
            		CachedPrincipal p = this.getPrincipal(group);
	                newMemberships.addAll(
	                	p.getAllSubgroups()
	                );
            	} catch(Exception e) {
            		new ServiceException(e).log();
            	}
            }
            memberships.addAll(newMemberships);
            newMemberships.clear();
            // ... and their supergroups
            for(String group: memberships) {
            	try {
            		CachedPrincipal p = this.getPrincipal(group);
                    newMemberships.addAll(
                        p.getAllSupergroups()
                    );
            	} catch(Exception e) {
            		new ServiceException(e).log();
            	}
            }
            memberships.addAll(newMemberships);
        }
        // member of Root:Administrators --> access level global
        return memberships.contains(SecurityKeys.ROOT_ADMINISTRATORS_GROUP) ? 
        	null : 
        	memberships;
    }
        
    //-----------------------------------------------------------------------
    // Variables
    //-----------------------------------------------------------------------    
    protected final AccessControl_1 plugin;
    protected final Path realmIdentity;
    
    private Map<String,CachedPrincipal> cachedPrincipals = new ConcurrentHashMap<String,CachedPrincipal>();

    private boolean isActive = true;
    private long cachedObjectTTLMillis = 120000;
    
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Description: openCRX access control plugin
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2005, CRIXP Corp., Switzerland
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jdo.FetchGroup;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.SecurityKeys.Action;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.security.realm1.cci2.PrincipalGroupQuery;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.FilterProperty;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.application.dataprovider.layer.model.Standard_1;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.spi.EntityManagerFactory_1;
import org.openmdx.base.accessor.rest.DataManagerFactory_1;
import org.openmdx.base.collection.Sets;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.ConfigurableProperty;
import org.openmdx.base.query.ConditionType;
import org.openmdx.base.query.Quantifier;
import org.openmdx.base.query.SortOrder;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.cci.MessageRecord;
import org.openmdx.base.rest.spi.ConnectionFactoryAdapter;
import org.openmdx.base.rest.spi.Facades;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.security.realm1.jmi1.Group;
import org.openmdx.security.realm1.jmi1.Permission;
import org.openmdx.security.realm1.jmi1.Principal;
import org.openmdx.security.realm1.jmi1.Realm;
import org.openmdx.security.realm1.jmi1.Role;

/**
 * openCRX access control plugin. Implements the openCRX access control logic.
 */
public class AccessControl_1 extends Standard_1 {
	
    //-------------------------------------------------------------------------
	public AccessControl_1(
	) {
	}

    //--------------------------------------------------------------------------
    public Interaction getInteraction(
        Connection connection
    ) throws ResourceException {
        return new LayerInteraction(connection);
    }

    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	CachedPrincipal principal
    ) {
    	return this.getUserIdentity(
    		principal.getIdentity().get(6), 
    		principal.getIdentity().getBase()
    	);
    }

    //-------------------------------------------------------------------------
    interface GetRunAsPrincipalResult {
    	CachedPrincipal getPrincipal();
    	Path getUserIdentity();
    }
    
    //-------------------------------------------------------------------------
	public class CachedPrincipal {
	
		public CachedPrincipal(
			DefaultRealm realm,
			Principal principal,
			Set<String> allSupergroups,
			long expiresAt
		) {
			this.realm = realm;
			this.principalIdentity = principal.refGetPath();
			this.isMemberOf = new TreeSet<String>();
			List<Group> groups = principal.getIsMemberOf();			
			for(Iterator<Group> i = groups.iterator(); i.hasNext(); ) {
				try {
					Group group = i.next();
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
			this.permissions = new HashSet<String[]>();
			if(principal instanceof org.opencrx.security.realm1.jmi1.Principal) {
				List<Role> roles = ((org.opencrx.security.realm1.jmi1.Principal)principal).getGrantedRole();
				for(Role role: roles) {
					Collection<Permission> permissions = role.getPermission();
					for(Permission permission: permissions) {
						for(String action: permission.getAction()) {
							this.permissions.add(
								new String[]{permission.getName(), action}
							);
						}
					}
				}
			}
			this.allSupergroups = allSupergroups;
			this.expiresAt = expiresAt;
		}
		
		public Path getIdentity(
		) {
			return this.principalIdentity;
		}
		
		public DefaultRealm getRealm(
		) {
			return this.realm;
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
		
		public Set<String> getPermissions(
			String action
		) {
			Set<String> permissions = new HashSet<String>();
			for(String[] permission: this.permissions) {
				if(action.equals(permission[1])) {
					permissions.add(permission[0]);
				}
			}
			return permissions;
		}
		
		public long getExpiresAt(
		) {
			return this.expiresAt;
		}
		
	    private List<PrincipalGroup> getSubgroups(
	    	PersistenceManager pm
	    ) throws ServiceException {
	    	Realm realm = (Realm)pm.getObjectById(this.realm.getRealmIdentity());
	    	Principal group = (Principal)pm.getObjectById(
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
            allSubgroups.add(AccessControl_1.this.getQualifiedPrincipalName(this.principalIdentity));
            for(PrincipalGroup subgroup: this.getSubgroups(pm)) {
            	String qualifiedGroupName = AccessControl_1.this.getQualifiedPrincipalName(subgroup.refGetPath());
            	if(!allSubgroups.contains(qualifiedGroupName)) {
                	allSubgroups.addAll(
                		this.realm.getPrincipal(qualifiedGroupName).getAllSubgroups()
                	);
            	}
            }
            return allSubgroups;
		}
		
		public Set<String> getAllSubgroups(
		) throws ServiceException {
			if(this.allSubgroups == null) {
				PersistenceManager pm = AccessControl_1.this.newDelegatingPersistenceManager();
				this.allSubgroups = this.getAllSubgroups(pm);
                pm.close();
			}
			return this.allSubgroups;
		}
		
		public Set<String> getAllSupergroups(
		) {
			return this.allSupergroups;
		}
		
		public String toString(
		) {
			return this.getIdentity().toString();
		}
		
		private final DefaultRealm realm;
		private long expiresAt;
		private final Path principalIdentity;
		private final Set<String> isMemberOf;
		private final Set<String[]> permissions;
		private Set<String> allSubgroups = null;
		private final Set<String> allSupergroups;
		private Path primaryGroup;
	}

    /**
     * Default realm implementation. Overload for custom-specific policies.
     */
    public class DefaultRealm {
        
        /**
         * Constructor
         * @param realmIdentity
         * @throws ServiceException
         */
        public DefaultRealm(
            Path realmIdentity
        ) throws ServiceException {
            this.realmIdentity = realmIdentity;
            this.isActive = true;
            if(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY) != null) {
                this.isActive = "true".equals(System.getProperty(SecurityKeys.ENABLE_SECURITY_PROPERTY));
            }
            if(!this.isActive) {
                System.out.println("WARNING: AccessControl_1 is not active. Activate with system property " + SecurityKeys.ENABLE_SECURITY_PROPERTY + "=true. Default is true.");
            }
            if(System.getProperty(SecurityKeys.REALM_REFRESH_RATE_MILLIS) != null) {
            	this.cachedPrincipalsTTL = Long.valueOf(System.getProperty(SecurityKeys.REALM_REFRESH_RATE_MILLIS));
            }
        }
        
        /**
         * Retrieve principal for given principal name.
         * @param principalName
         * @return
         * @throws ServiceException
         */
        protected CachedPrincipal getPrincipal(
            String principalName
        ) throws ServiceException {
        	// In case name is qualified
        	if(principalName.indexOf(":") > 0) {
        		principalName = principalName.substring(principalName.indexOf(":") + 1);
        	}
        	CachedPrincipal cachedPrincipal = this.cachedPrincipals.get(principalName);
        	if(cachedPrincipal == null || System.currentTimeMillis() > cachedPrincipal.getExpiresAt()) {
                PersistenceManager pm = AccessControl_1.this.newDelegatingPersistenceManager();
                Principal principal = null;
                try {
                    principal = (Principal)pm.getObjectById(
                        this.realmIdentity.getDescendant(new String[]{"principal", principalName})
                    );
                    Set<String> allSupergroups = new HashSet<String>();
                    allSupergroups.add(AccessControl_1.this.getQualifiedPrincipalName(principal.refGetPath()));
                    List<Group> supergroups = principal.getIsMemberOf();
                    for(Iterator<Group> i = supergroups.iterator(); i.hasNext(); ) {
                    	try {
                    		Group supergroup = i.next();
    	                	String qualifiedGroupName = AccessControl_1.this.getQualifiedPrincipalName(supergroup.refGetPath());
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
                        	this,
                        	principal,
                        	allSupergroups,
                        	System.currentTimeMillis() + this.cachedPrincipalsTTL
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

	    /**
	     * Get runAs principal according to service header and available runAs permissions.
	     * @param header
	     * @param request
	     * @param interaction
	     * @return
	     * @throws ServiceException
	     */
	    public GetRunAsPrincipalResult getRunAsPrincipal(
	    	ServiceHeader header,
	    	DataproviderRequest request,
	    	LayerInteraction interaction
	    ) throws ServiceException {
	        CachedPrincipal principal = this.getPrincipal(
	        	AccessControl_1.this.getPrincipalName(header)
	        );
	        Path userIdentity = AccessControl_1.this.getUser(principal);
	        // Check for runAs permission
	        Set<String> runAsPermissions = new HashSet<String>();
	        if(
	        	(header.getPrincipalChain().size() >= 2) &&
	        	this.hasPermission(request, null, null, principal, userIdentity, Action.RUN_AS, runAsPermissions, interaction)
	        ) {
	        	boolean hasRunAsPermission = false;
	        	for(String runAsPermission: runAsPermissions) {
	        		if(runAsPermission.indexOf("@") > 0) {
	        			String runAsPrincipalName = runAsPermission.substring(runAsPermission.indexOf("@") + 1);
	        			if(runAsPrincipalName.equals(header.getPrincipalChain().get(1))) {
	        				if(hasRunAsPermission) {
	        	        		SysLog.warning("Multiple runAs permissions found. Accepting first only.", Arrays.asList(header.getPrincipalChain(), principal, runAsPermissions));        					
	        				}
	        				else {
			        			CachedPrincipal runAsPrincipal = this.getPrincipal(runAsPrincipalName);
			        	        Path runAsUserIdentity = AccessControl_1.this.getUser(runAsPrincipal);
			        	        SysLog.detail("Applying runAs permission", Arrays.asList(principal, userIdentity, runAsPrincipal, runAsUserIdentity));
			        	        principal = runAsPrincipal;
			        	        userIdentity = runAsUserIdentity;
			        	        hasRunAsPermission = true;
	        				}
	        			}
	        		} else if(!runAsPermission.equals(ALL_PERMISSION)){
		        		SysLog.warning("Invalid format for runAs permission. Accepted format is 'authority@principal'. Ignoring.", Arrays.asList(principal, runAsPermissions));	        			
	        		}
	        	}
	        }
	        final CachedPrincipal fPrincipal = principal;
	        final Path fUserIdentity = userIdentity;
	        
	        return new GetRunAsPrincipalResult(){
				@Override
                public CachedPrincipal getPrincipal() {
					return fPrincipal;
                }
				@Override
                public Path getUserIdentity() {
					return fUserIdentity;
                }	        	
	        };
	    }
        
        /**
         * Get primary group for given principal.
         * @param principal
         * @return
         * @throws ServiceException
         */
        protected Path getPrimaryGroup(
        	CachedPrincipal principal
        ) throws ServiceException {
            try {
            	if(principal.getPrimaryGroup() == null) {
    	            String providerName = principal.getIdentity().get(2);
    	            String segmentName = principal.getIdentity().get(principal.getIdentity().size()-3);
    	            PersistenceManager pm = AccessControl_1.this.newDelegatingPersistenceManager();            
    	            UserHome userHome = (UserHome)pm.getObjectById(
    	                new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", segmentName, "userHome", principal.getIdentity().getBase())
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
        
        /**
         * Get permissions for given principal and access level.
         * @param request
         * @param principal
         * @param userIdentity
         * @param accessLevel
         * @param action
         * @return
         */
        protected Set<String> getPermissions(
        	DataproviderRequest request,
        	CachedPrincipal principal,
        	Path userIdentity,
            short accessLevel,
            Action action
        ) {
            Set<String> permissions = new HashSet<String>();
        	//
        	// Map group memberships to permissions
        	//
        	{
	            // GLOBAL or Root
	            if(
	            	!this.isActive || 
	            	(accessLevel == SecurityKeys.ACCESS_LEVEL_GLOBAL) || 
	            	principal.getIdentity().getBase().equals(SecurityKeys.ROOT_PRINCIPAL)
	            ) {
	                return null;
	            }
	            // PRIVATE --> grant requesting user access to all owned objects
	            if(accessLevel >= SecurityKeys.ACCESS_LEVEL_PRIVATE) {
	                permissions.add(
	                    AccessControl_1.this.getQualifiedPrincipalName(principal.getIdentity())
	                );
	                permissions.add(
	                	AccessControl_1.this.getQualifiedPrincipalName(userIdentity)
	                );
	            }
	            // BASIC, DEEP --> all direct subgroups, supergroups 
	            if(
	            	(accessLevel == SecurityKeys.ACCESS_LEVEL_DEEP) || 
	            	(accessLevel == SecurityKeys.ACCESS_LEVEL_BASIC)
	            ) {
	            	Set<String> groups = principal.getIsMemberOf();
	                if(AccessControl_1.this.useExtendedAccessLevelBasic) {	            	
	                    for(String group: groups) {
	                    	try {
	                    		CachedPrincipal p = this.getPrincipal(group);
	    	                    permissions.addAll(
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
	    	                permissions.addAll(
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
	                for(String group: permissions) {
	                	try {
	                		CachedPrincipal p = this.getPrincipal(group);
	    	                newMemberships.addAll(
	    	                	p.getAllSubgroups()
	    	                );
	                	} catch(Exception e) {
	                		new ServiceException(e).log();
	                	}
	                }
	                permissions.addAll(newMemberships);
	                newMemberships.clear();
	                // ... and their supergroups
	                for(String group: permissions) {
	                	try {
	                		CachedPrincipal p = this.getPrincipal(group);
	                        newMemberships.addAll(
	                            p.getAllSupergroups()
	                        );
	                	} catch(Exception e) {
	                		new ServiceException(e).log();
	                	}
	                }
	                permissions.addAll(newMemberships);
	            }
        	}
        	// Assigned permissions
        	{
        		permissions.addAll(
        			principal.getPermissions(action.getName())
        		);
        	}
            // Member of Root:Administrators --> access level global
            return permissions.contains(SecurityKeys.ROOT_ADMINISTRATORS_GROUP) ? 
            	null : 
            	permissions;
        }

        /**
         * Get identity of realm.
         * @return
         */
        public Path getRealmIdentity(
        ) {
        	return this.realmIdentity;
        }
        
        /**
         * Return true if principal has permission to perform the request.
         * @param request
         * @param secureObject
         * @param parent
         * @param principal
         * @param userIdentity
         * @param action
         * @param grantedPermissions
         * @param interaction
         * @return
         * @throws ServiceException
         */
        public boolean hasPermission(
        	DataproviderRequest request,
        	Object_2Facade secureObject,
        	Object_2Facade parent,        	
        	CachedPrincipal principal,
        	Path userIdentity,
        	Action action,
        	Set<String> grantedPermissions,
        	LayerInteraction interaction
        ) throws ServiceException {
        	if(secureObject == null && Object_2Facade.isDelegate(request.object())) {
				secureObject = Facades.asObject(request.object());
			}
        	// DELETE
        	if(action == Action.DELETE) {
	            Set<String> permissions = new HashSet<String>();
	            if(secureObject.attributeValuesAsList("accessLevelDelete").isEmpty()) {
	            	SysLog.error("Missing value for attribute 'accessLevelDelete'", secureObject);
	            }
	            else {
	                permissions = this.getPermissions(
	                	request,
		                principal,
	                    userIdentity,
		                ((Number)secureObject.attributeValue("accessLevelDelete")).shortValue(),
		                action
	                );
	            }
	            if(permissions != null) {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.addAll(permissions);
	            	}
	                permissions.retainAll(secureObject.attributeValuesAsList("owner"));
		            return !permissions.isEmpty();
	            } else {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.add(ALL_PERMISSION);
	            	}
	            	return true;
	            }
        	}
        	// UPDATE
        	else if(action == Action.UPDATE) {
	            Set<String> permissions = new HashSet<String>();
	            if(secureObject.attributeValuesAsList("accessLevelUpdate").isEmpty()) {
	            	SysLog.error("Missing value for attribute 'accessLevelUpdate'", secureObject);
	            }
	            else {
	                permissions = this.getPermissions(
	                	request,
		                principal,
	                    userIdentity,
		                ((Number)secureObject.attributeValue("accessLevelUpdate")).shortValue(),
		                action
	                );
	            }
	            if(permissions != null) {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.addAll(permissions);
	            	}
	                permissions.retainAll(secureObject.attributeValuesAsList("owner"));
		            return !permissions.isEmpty();
	            } else {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.add(ALL_PERMISSION);
	            	}
	            	return true;
	            }
        	}
        	// READ
        	else if(action == Action.READ) {
                Set<String> permissions = new HashSet<String>();
                if(parent.attributeValuesAsList("accessLevelBrowse").isEmpty()) {
                	SysLog.error("Missing value for attribute 'accessLevelBrowse'", parent);
                }
                else {
                    permissions = this.getPermissions(
                    	request,
                        principal,
                        userIdentity,
                        parent == null ? 
	                    	SecurityKeys.ACCESS_LEVEL_GLOBAL : 
	                    	((Number)parent.attributeValue("accessLevelBrowse")).shortValue(),                        
                        action
                    );
                }
                if(permissions != null) {
                	if(grantedPermissions != null) {
                		grantedPermissions.addAll(permissions);
                	}
                    permissions.retainAll(secureObject.attributeValuesAsList("owner"));
                    return !permissions.isEmpty();
                }
                else {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.add(ALL_PERMISSION);
	            	}
                	return true;
                }
        	}
        	// RUN_AS
        	if(action == Action.RUN_AS) {
        		Set<String> permissions = this.getPermissions(
                	request,
                    principal,
                    userIdentity,
                   	SecurityKeys.ACCESS_LEVEL_NA, 
                    action
                );
                if(permissions != null) {
                	// runAs permissions are of the form
                	// * object:authority/object path@runAsPrincipal', e.g. object:org.opencrx.kernel.activity1/tracker/guest@admin-Standard
                	// * groupMembership:authority/group path@runAsPrincipal, e.g. groupMembership:org.opencrx.kernel.activity1/tracker/guest@admin-Standard@admin-Standard
                	for(Iterator<String> i = permissions.iterator(); i.hasNext(); ) {
                		String permission = i.next();
                		boolean matches = false;
                		// object: runAs permission
                		if(permission.startsWith("object:")) {
                			String[] components = permission.substring(7, permission.indexOf("@")).split("/");
                			if(components.length > 0) {
                				Path pattern = new Path("xri://@openmdx*" + components[0]).getDescendant("provider", request.path().get(2), "segment", request.path().get(4));
                				for(int j = 1; j < components.length; j++) {
                					pattern = pattern.getDescendant(components[j]);
                				}
                        		if(request.path().isLike(pattern)) {
                        			matches = true;
                        		}
                        		// runAs permission defined for activityCreator implies runAs permission for 
                        		// activities created with this creator and their composites
                        		else if(
                        			secureObject != null &&
                        			secureObject.getPath().startsWith(new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", request.path().get(2), "segment", request.path().get(4), "activity"))
                        		) {
                        			Object_2Facade activity = null;
                        			if(request.path().isLike(new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", request.path().get(2), "segment", request.path().get(4), "activity", ":*"))) {
                        				activity = secureObject;
                        			} else {
                        				try {
                        					activity = Facades.asObject(interaction.retrieveObject(secureObject.getPath().getPrefix(7)));
                        				} catch(Exception e) {}
                        			}
                        			if(
                        				activity != null &&
                        				activity.attributeValue("lastAppliedCreator") != null &&
                        				((Path)activity.attributeValue("lastAppliedCreator")).isLike(pattern)
	                        		) {
	                        			matches = true;
	                        		}
                        		}
                			}
                		}
                		// groupMembership: runAs permission
                		else if(permission.startsWith("groupMembership:")) {
	    					// Activity group membership
	    					if(
	    						request.path().startsWith(new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", request.path().get(2), "segment", request.path().get(4), "activity")) &&
	    						request.path().size() >= 7
	    					) {
	                			String[] groupIdentityComponents = permission.substring(16, permission.indexOf("@")).split("/");
	                			if(groupIdentityComponents.length > 0) {
	                				Path groupIdentity = new Path("xri://@openmdx*" + groupIdentityComponents[0]).getDescendant("provider", request.path().get(2), "segment", request.path().get(4));
	                				for(int j = 1; j < groupIdentityComponents.length; j++) {
	                					groupIdentity = groupIdentity.getDescendant(groupIdentityComponents[j]);
	                				}
	                				Object_2Facade group = null;
	                				try {
	                					group = Facades.asObject(interaction.retrieveObject(groupIdentity));
	                				} catch(Exception e) {}
	                				if(group != null) {
	            						Path activityIdentity = request.path().getPrefix(7);
	            						MappedRecord[] groupAssignments = interaction.findObjects(activityIdentity.getDescendant("assignedGroup"));
	            						for(int j = 0; j < groupAssignments.length; j++) {
	            							Object_2Facade groupAssignment = null;
	            							try {
	            								groupAssignment = Facades.asObject(groupAssignments[j]);
	            							} catch(Exception e) {}
	            							if(groupAssignment != null) {
	            								if(group.getPath().equals(groupAssignment.attributeValue("activityGroup"))) {
	            									matches = true;
	            									break;
	            								}
	            							}
	            						}
                					}
	                			}
	    					}
        					else {
        						// Test for other memberships TBD
        					}
                		}
                		if(!matches) {
                			i.remove();
                		}
                	}
                	if(grantedPermissions != null) {
                		grantedPermissions.addAll(permissions);
                	}
                    return !permissions.isEmpty();
                }
                else {
	            	if(grantedPermissions != null) {
	            		grantedPermissions.add(ALL_PERMISSION);
	            	}
                	return true;
                }
        	}
        	else {
            	SysLog.error("Unknown action", action.toString());
        		return false;
        	}
        }

        /**
         * Restrict query according to permissions of given principal.
         * @param request
         * @param object
         * @param principal
         * @param userIdentity
         * @throws ServiceException
         */
        public void restrictQuery(
        	DataproviderRequest request,
        	Object_2Facade object,
        	CachedPrincipal principal,
            Path userIdentity    	
        ) throws ServiceException {
        	Set<String> memberships = new HashSet<String>();    	
            if(object.attributeValuesAsList("accessLevelBrowse").isEmpty()) {
            	SysLog.error("Missing attribute value for accessLevelBrowse", object);
            }
            else {
                memberships = this.getPermissions(
                	request,
                    principal,
                    userIdentity,
                    ((Number)object.attributeValue("accessLevelBrowse")).shortValue(),
                    Action.READ
                );
            }
            // allowedPrincipals == null --> global access. Do not restrict to allowed subjects
            if(memberships != null) {
            	// Restrict access to objects for which user has read access
            	FilterProperty ownerFilterProperty = null;
            	for(FilterProperty p: request.attributeFilter()) {
            		if("owner".equals(p.name())) {
            			ownerFilterProperty = p;
            			break;
            		}
            	}
            	if(ownerFilterProperty != null) {
            		memberships.retainAll(
            			ownerFilterProperty.values()
            		);
            	}
        		// Add condition for owner
            	if(!memberships.isEmpty()) {
    	            request.addAttributeFilterProperty(
    	                new FilterProperty(
    	                    Quantifier.THERE_EXISTS.code(),
    	                    "owner",
    	                    ConditionType.IS_IN.code(),
    	                    memberships.toArray()
    	                )
    	            );
            	}
            }
        }

        //-----------------------------------------------------------------------
        // Variables
        //-----------------------------------------------------------------------    
        private final Path realmIdentity;        
        private Map<String,CachedPrincipal> cachedPrincipals = new ConcurrentHashMap<String,CachedPrincipal>();
        private boolean isActive = true;
        private long cachedPrincipalsTTL = 120000;        
    }
    
    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	String qualifiedPrincipalName
    ) {
        int pos = qualifiedPrincipalName.indexOf(":");
        String realmName = null;
        String principalName = null;
        if(pos < 0) {
            SysLog.error("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName);
            realmName = "Root";
            principalName = qualifiedPrincipalName;
        }
        else {
            realmName = qualifiedPrincipalName.substring(0, pos);
            principalName = qualifiedPrincipalName.substring(pos+1);
        }
        return this.getUserIdentity(
        	realmName, 
        	principalName
        );
    }

    //-------------------------------------------------------------------------
    protected Path getUserIdentity(
    	String realmName,
    	String principalName
    ) {
        // <= 1.7.1 compatibility. Principals with name 'admin'|'loader' must be
        // mapped to 'admin-<segment>'
        if(SecurityKeys.ADMIN_PRINCIPAL.equals(principalName) || SecurityKeys.LOADER_PRINCIPAL.equals(principalName)) {
            principalName = principalName + SecurityKeys.ID_SEPARATOR + realmName;
        }
        // <= 1.7.1 compatibility. Qualified principal name must have the
        // format of a user principal
        if(!principalName.endsWith(SecurityKeys.USER_SUFFIX)) {
            principalName += "." + SecurityKeys.USER_SUFFIX;
        }
        Path userIdentity = this.realmIdentity.getParent().getDescendant(
            new String[]{realmName, "principal", principalName}             
        );
        return userIdentity;
    }

    //-------------------------------------------------------------------------
    protected Path getUser(
    	CachedPrincipal principal
    ) throws ServiceException {
    	return this.getUserIdentity(principal);
    }
        
    //-------------------------------------------------------------------------
    protected Path getGroupIdentity(
        Path accessPath,
        String qualifiedPrincipalName
    ) {
        int pos = qualifiedPrincipalName.indexOf(":");
        String segmentName = null;
        String principalName = null;
        if(pos < 0) {
            System.err.println("FATAL: object has illegal formatted owner (<realm segment>:<subject name>): " + qualifiedPrincipalName + "; path=" + accessPath.toXRI());
            segmentName = "Root";
            principalName = qualifiedPrincipalName;
        }
        else {
            segmentName = qualifiedPrincipalName.substring(0, pos);
            principalName = qualifiedPrincipalName.substring(pos+1);
        }
        Path principalIdentity = this.realmIdentity.getParent().getDescendant(
            new String[]{segmentName, "principal", principalName}             
        );
        return principalIdentity;
    }

    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path accessPath,
        String principalName
    ) {
        return accessPath.get(4) + ":" + principalName;
    }
    
    //-------------------------------------------------------------------------
    protected String getQualifiedPrincipalName(
        Path principalIdentity
    ) {
        return principalIdentity.get(6) + ":" + principalIdentity.getBase();
    }
    
    // --------------------------------------------------------------------------
    public class LayerInteraction extends Standard_1.LayerInteraction {
        
        public LayerInteraction(
            Connection connection
        ) throws ResourceException {
            super(connection);
        }
            
	    //-------------------------------------------------------------------------
	    protected MappedRecord retrieveObject(
	        Path identity
	    ) throws ServiceException {
	        try {
	        	DataproviderRequest getRequest = new DataproviderRequest(
	                Query_2Facade.newInstance(identity).getDelegate(),
	                DataproviderOperations.OBJECT_RETRIEVAL,
	                AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES,
	                new AttributeSpecifier[]{
	                	new AttributeSpecifier("owner")
	                }
	            );
	        	DataproviderReply getReply = this.newDataproviderReply();
		        super.get(  
		            getRequest.getInteractionSpec(),
		            Query_2Facade.newInstance(getRequest.object()),
		            getReply.getResult()
		        );
		        return getReply.getObject();
	        }
	        catch (Exception e) {
	        	throw new ServiceException(e);
	        }
	    }

	    //-------------------------------------------------------------------------
	    protected MappedRecord[] findObjects(
	        Path reference
	    ) throws ServiceException {
	        try {
	        	DataproviderRequest findRequest = new DataproviderRequest(
		            Query_2Facade.newInstance(reference).getDelegate(),
                    DataproviderOperations.ITERATION_START,
                    new FilterProperty[]{},
                    0, 
                    Integer.MAX_VALUE,
                    SortOrder.ASCENDING.code(),
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
	            );
	        	DataproviderReply findReply = this.newDataproviderReply();
		        super.find(
		            findRequest.getInteractionSpec(),
		            Query_2Facade.newInstance(findRequest.object()),
		            findReply.getResult()
		        );
		        return findReply.getObjects();
	        }
	        catch (Exception e) {
	        	new ServiceException(e).log();
	        	throw new ServiceException(e);
	        }
	    }

	    //-------------------------------------------------------------------------
	    protected String getOwningUserForNewObject(
	    	Path requestingUser,
	    	Object_2Facade newObjectFacade,
	    	Object_2Facade parentFacade,
	    	DefaultRealm realm
	    ) throws ServiceException {
	        String owningUser = null;
	        // If new object is composite to user home set owning user to owning user of user home
	        if(
	            (newObjectFacade.getPath().size() > USER_HOME_PATH_PATTERN.size()) &&
	            newObjectFacade.getPath().getPrefix(USER_HOME_PATH_PATTERN.size()).isLike(USER_HOME_PATH_PATTERN) &&
	            !parentFacade.attributeValuesAsList("owner").isEmpty()
	        ) {
	           owningUser = (String)parentFacade.attributeValue("owner");
	        }   
	        // Owning user set on new object
	        else if(!newObjectFacade.attributeValuesAsList("owningUser").isEmpty()) {
	            owningUser = AccessControl_1.this.getQualifiedPrincipalName(
	                (Path)newObjectFacade.attributeValue("owningUser")
	            );
	        }
	        // Set requesting principal as default
	        else {
	            // If no user found set owner to segment administrator
	            owningUser = newObjectFacade.attributeValuesAsList("owner").isEmpty() ? 
	            	requestingUser == null ? 
	            		AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
	            			AccessControl_1.this.getQualifiedPrincipalName(requestingUser) : 
	            	(String)newObjectFacade.attributeValue("owner");
	        }
	        return owningUser;
	    }
	    
	    // --------------------------------------------------------------------------
	    protected Set<String> getOwningGroupsForNewObject(
	    	CachedPrincipal requestingPrincipal,
	    	Object_2Facade newObjectFacade,
	    	Object_2Facade parentFacade
	    ) throws ServiceException {
	    	DefaultRealm realm = requestingPrincipal.getRealm();
	        Set<String> owningGroup = new HashSet<String>();
	        if(
	        	(newObjectFacade.getAttributeValues("owningGroup") != null) && 
	        	!newObjectFacade.attributeValuesAsList("owningGroup").isEmpty()
	        ) {
	            for(Iterator<Object> i = newObjectFacade.attributeValuesAsList("owningGroup").iterator(); i.hasNext(); ) {
	                owningGroup.add(
	                	AccessControl_1.this.getQualifiedPrincipalName((Path)i.next())
	                );
	            }
	        }
	        else {
	        	Path userGroup = requestingPrincipal == null ? 
	        		null : 
	        		realm.getPrimaryGroup(requestingPrincipal);
	            owningGroup = new HashSet<String>();
	            if(parentFacade != null) {
                    @SuppressWarnings({
                        "rawtypes", "unchecked"
                    })
                    List<String> ownersParent = new ArrayList(parentFacade.attributeValuesAsList("owner"));
	                // Do not inherit group Users at segment-level
	                if(parentFacade.getPath().size() == 5) {
	                    ownersParent.remove(
	                    	AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), SecurityKeys.USER_GROUP_USERS)
	                    );
	                }
	                if(!ownersParent.isEmpty()) {
	                    owningGroup.addAll(
	                        ownersParent.subList(1, ownersParent.size())
	                    );
	                }
	            }
	            owningGroup.add(
	                userGroup == null ? 
	                	AccessControl_1.this.getQualifiedPrincipalName(newObjectFacade.getPath(), "Unassigned") : 
	                		AccessControl_1.this.getQualifiedPrincipalName(userGroup)
	            );
	        }  
	        return owningGroup;
	    }
	    
	    //-------------------------------------------------------------------------
	    private MappedRecord getCachedObject(
	        ServiceHeader header,
	        Path path
	    ) throws ServiceException {    	
	    	ConcurrentMap<Path,Object[]> objectCache = AccessControl_1.getObjectCache();
	        // Remove cached parents if expired
	        for(Iterator<Map.Entry<Path,Object[]>> i = objectCache.entrySet().iterator(); i.hasNext(); ) {
	            Map.Entry<Path,Object[]> entry = i.next();
	            try {
	                if(entry != null) {
	                    if(entry.getValue() == null) {
	                        i.remove();
	                    }
	                    else {
	                        Long expiresAt = (Long)entry.getValue()[1];
	                        if((expiresAt == null) || (expiresAt < System.currentTimeMillis())) {
	                            i.remove();
	                        }
	                    }
	                }
	            } 
	            catch(Exception e) {}
	        }
	        // Get cached parent
	        Path reference = path;
	        if(reference.size() % 2 == 1) {
	            reference = path.getParent();
	        }
	        // Get parent from cache or retrieve
	        Path parentPath = reference.getParent();
	        Object[] entry = objectCache.get(parentPath);
	        MappedRecord parent = null;
	        if(entry == null) {
	            parent = this.retrieveObject(
	                parentPath
	            );
	            this.addToObjectCache(parent);
	        }
	        else {
	            parent = (MappedRecord)entry[0];
	        }
	        return parent;
	    }
	    
	    //-------------------------------------------------------------------------
	    private void addToObjectCache(
	    	MappedRecord object
	    ) throws ServiceException {
	    	Object_2Facade facade = Facades.asObject(object);
	        if(facade.getObjectClass() != null) {
            	AccessControl_1.getObjectCache().put(
	                facade.getPath(),
	                new Object[]{
	                    Object_2Facade.cloneObject(object),
	                    new Long(System.currentTimeMillis() + TTL_CACHED_OBJECTS)
	                }
	            );
	        }
	        else {
	        	SysLog.error("Missing object class. Object not added to cache", facade.getPath());
	        }
	    }
	    	    
	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#create(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean create(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
	        GetRunAsPrincipalResult getRunAsPrincipalResult = realm.getRunAsPrincipal(
	        	header, 
	        	request,
	        	this
	        );
	        CachedPrincipal principal = getRunAsPrincipalResult.getPrincipal();
	        Path userIdentity = getRunAsPrincipalResult.getUserIdentity();
	        // Check permission. Must have update permission for parent
	        MappedRecord parent = null;
	        Object_2Facade parentFacade = null;
	        if(request.path().size() >= 7) {
		        parent = this.getCachedObject(
	                header, 
	                request.path()
	            );
		        parentFacade = Facades.asObject(parent);
		        if(AccessControl_1.this.isSecureObject(parent)) {
		        	boolean hasPermission = realm.hasPermission(
		        		request, 
		        		parentFacade, 
		        		null, // parent 
		        		principal, 
		        		userIdentity, 
		        		Action.UPDATE, 
		        		null, // grantedPermissions
		        		this
		        	);
		        	if(!hasPermission) {
		                throw new ServiceException(
		                    OpenCrxException.DOMAIN,
		                    OpenCrxException.AUTHORIZATION_FAILURE_CREATE, 
		                    "No permission to create object.",
                            new BasicException.Parameter("object", request.path()),
                            new BasicException.Parameter("param0", request.path()),
                            new BasicException.Parameter("param1", principal.getIdentity().get(6) + ":" +  principal.getIdentity().getBase())                                
		                );
		            }
		        }
	        }
	        // Create object
	        // Set owner in case of secure objects
	        MappedRecord newObject = request.object();
	        if(AccessControl_1.this.isSecureObject(newObject)) {
	            Object_2Facade newObjectFacade = Facades.asObject(newObject);
	            String owningUser = this.getOwningUserForNewObject(
	            	userIdentity, 
	            	newObjectFacade, 
	            	parentFacade, 
	            	realm
	            );
	            newObjectFacade.attributeValuesAsList("owner").clear();
	            newObjectFacade.attributeValuesAsList("owner").add(owningUser);		            
		        Set<String> owningGroup = this.getOwningGroupsForNewObject(
		        	principal, 
		        	newObjectFacade, 
		        	parentFacade 
		        );
		        newObjectFacade.attributeValuesAsList("owner").addAll(owningGroup);            
		        newObjectFacade.getValue().keySet().remove("owningUser");
		        newObjectFacade.getValue().keySet().remove("owningGroup");
	            // Access levels
	            for(
	                Iterator<String> i = Arrays.asList("accessLevelBrowse", "accessLevelUpdate", "accessLevelDelete").iterator();
	                i.hasNext();
	            ) {
	                String mode = i.next();
	                if(
	                    (newObjectFacade.attributeValuesAsList(mode).size() != 1) ||
	                    (((Number)newObjectFacade.attributeValue(mode)).shortValue() == SecurityKeys.ACCESS_LEVEL_NA)
	                ) {
	                	newObjectFacade.attributeValuesAsList(mode).clear();
	                	newObjectFacade.attributeValuesAsList(mode).add(
	                        new Short(
	                            "accessLevelBrowse".equals(mode) ? 
	                            	SecurityKeys.ACCESS_LEVEL_DEEP : 
	                            	SecurityKeys.ACCESS_LEVEL_BASIC
	                        )
	                    );
	                }
	            }
	        }
	        if(super.create(ispec, input, output)) {
	        	this.addToObjectCache(input.getDelegate());
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }
	    
	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#find(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
	        GetRunAsPrincipalResult getRunAsPrincipalResult = realm.getRunAsPrincipal(
	        	header, 
	        	request,
	        	this
	        );
	        CachedPrincipal principal = getRunAsPrincipalResult.getPrincipal();
	        Path userIdentity = getRunAsPrincipalResult.getUserIdentity();
	        MappedRecord parent = this.getCachedObject(
	            header,
	            request.path()
	        );
	        Object_2Facade parentFacade = Facades.asObject(parent);
	        ModelElement_1_0 referencedType = AccessControl_1.this.getReferencedType(
	            request.path(),
	            request.attributeFilter()
	        );
	        if(
	        	AccessControl_1.this.isSecureObject(referencedType) && 
	        	AccessControl_1.this.isSecureObject(parent)
	        ) {
	        	boolean containsSharedAssociation = AccessControl_1.this.model.containsSharedAssociation(
	        		request.path()
	        	);
	        	if(containsSharedAssociation) {
	        		Number originalSize = input.getSize();
	        		Number originalPosition = input.getPosition();
	        		// In case of shared association get the composite parent and 
	        		// restrict query to the parent's security settings
	        		input.setSize(1);
	        		input.setPosition(0);
			        super.find(
			        	ispec, 
			        	input, 
			        	output
			        );
	        		if(!output.isEmpty()) {
			        	Object_2Facade objectParentFacade = Facades.asObject(
			        		this.getCachedObject(
			        			header, 
			        			Object_2Facade.getPath(reply.getObject()).getParent()
			        		)
			        	);
			        	// Restrict query according to security settings of composite parent
			        	realm.restrictQuery(
			        		request, 
			        		objectParentFacade, 
			        		principal, 
			        		userIdentity
			        	);
	        		}
	        		input.setPosition(originalPosition);
	        		input.setSize(originalSize);
	        		output.clear();
	        	}
	        	// Restrict query according to security settings of parent
	        	realm.restrictQuery(
	        		request, 
	        		parentFacade, 
	        		principal, 
	        		userIdentity
	        	);
		        super.find(
		        	ispec, 
		        	input, 
		        	output
		        );
	        }
	        else {
		        super.find(
		        	ispec, 
		        	input, 
		        	output
		        );
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#get(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
	        GetRunAsPrincipalResult getRunAsPrincipalResult = realm.getRunAsPrincipal(
	        	header, 
	        	request,
	        	this
	        );
	        CachedPrincipal principal = getRunAsPrincipalResult.getPrincipal();
	        Path userIdentity = getRunAsPrincipalResult.getUserIdentity();
	        // Need multi-valued attribute 'owner' of retrieved object. This
	        // is required for permission check in next step --> realm.hasPermission()
	        try {
	        	input.setGroups(Sets.asSet(Arrays.asList(FetchGroup.ALL)));
	        } catch(Exception e) {}
	        super.get(
	            ispec,
	            input,
	            output
	        );
	        MappedRecord parent = null;
	        if(request.path().size() >= 7) {
		        parent = this.getCachedObject(
	                header,
	                request.path()
	            );
		        Object_2Facade parentFacade = Facades.asObject(parent);
		        ModelElement_1_0 referencedType = this.getModel().getTypes(request.path())[2];
		        if(AccessControl_1.this.isSecureObject(referencedType) && AccessControl_1.this.isSecureObject(parent)) {
		        	Object_2Facade objectFacade = Facades.asObject(reply.getObject());
		        	boolean hasPermission = realm.hasPermission(
		        		request,
		        		objectFacade, 
		        		parentFacade, 
		        		principal, 
		        		userIdentity,
		        		Action.READ,
		        		null, // grantedPermissions
		        		this
		        	);
		        	if(hasPermission) {
		            	AccessControl_1.this.completeReply(
		                    header,
		                    reply,
                            parent
		                );
		                return true;
		            }
		            else {
                        throw new ServiceException(
                            BasicException.Code.DEFAULT_DOMAIN,
                            BasicException.Code.AUTHORIZATION_FAILURE, 
                            "No permission to access requested object.",
                            new BasicException.Parameter("path", request.path()),
                            new BasicException.Parameter("param0", request.path().toXRI()),                                
                            new BasicException.Parameter("param1", principal.getIdentity().get(6) + ":" +  principal.getIdentity().getBase()), 
                            new BasicException.Parameter("param2", userIdentity.toXRI()) 
                        );
		            }
		        }
	        }
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#remove(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
	        GetRunAsPrincipalResult getRunAsPrincipalResult = realm.getRunAsPrincipal(
	        	header, 
	        	request,
	        	this
	        );
	        CachedPrincipal principal = getRunAsPrincipalResult.getPrincipal();
	        Path userIdentity = getRunAsPrincipalResult.getUserIdentity();
	        MappedRecord object = this.retrieveObject(            
	            request.path()
	        );
	        MappedRecord parent = null;
	        if(AccessControl_1.this.isSecureObject(object)) {   	        	
	            parent = this.getCachedObject(
	                header,
	                request.path()
	            );
	            Object_2Facade objectFacade = Facades.asObject(object);
	            boolean hasPermission = realm.hasPermission(
	            	request, 
	            	objectFacade,
	            	null, // parent
	            	principal, 
	            	userIdentity, 
	            	Action.DELETE,
	            	null, // grantedPermissions
	            	this
	            );
	            if(!hasPermission) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_DELETE, 
	                    "No permission to delete requested object.",
                        new BasicException.Parameter("object", request.path()),
                        new BasicException.Parameter("param0", request.path().toXRI()),
                        new BasicException.Parameter("param1", principal.getIdentity().get(6) + ":" +  principal.getIdentity().getBase()),                            
                        new BasicException.Parameter("param2", userIdentity.toXRI())                            
	                );
	            }
	        }
	        objectCache.remove(
	            request.path()
	        );
	        super.delete(
	        	ispec, 
	        	input, 
	        	output
	        );
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            parent
	        );
	        return true;
	    }

	    //-------------------------------------------------------------------------
	    /* (non-Javadoc)
	     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#replace(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest)
	     */
	    @Override
	    public boolean put(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
            MappedRecord replacement = request.object();
            Object_2Facade replacementFacade = Facades.asObject(replacement);
	        GetRunAsPrincipalResult getRunAsPrincipalResult = realm.getRunAsPrincipal(
	        	header, 
	        	request,
	        	this
	        );
	        CachedPrincipal principal = getRunAsPrincipalResult.getPrincipal();
	        Path userIdentity = getRunAsPrincipalResult.getUserIdentity();
	        MappedRecord object = this.retrieveObject(
	            request.path()
	        );
	        if(AccessControl_1.this.isSecureObject(object)) {
	            Object_2Facade objectFacade = Facades.asObject(object);
	            boolean hasPermission = 
	            	realm.hasPermission(
		            	request, 
		            	objectFacade, 
		            	null, // parent
		            	principal, 
		            	userIdentity, 
		            	Action.UPDATE,
		            	null, // grantedPermissions
		            	this
		            );
	            if(!hasPermission) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_UPDATE, 
	                    "No permission to update requested object.",
                        new BasicException.Parameter("object", request.path()),
                        new BasicException.Parameter("param0", request.path()),
                        new BasicException.Parameter("param1", principal.getIdentity().get(6) + ":" +  principal.getIdentity().getBase()), 
                        new BasicException.Parameter("param2", userIdentity.toXRI()) 
	                );
	            }
	            // Derive attribute owner from owningUser and owningGroup
		        // Derive newOwner from owningUser and owningGroup
		        List<Object> newOwner = new ArrayList<Object>();	        
		        // owning user
		        String owningUser = null;
		        if(!replacementFacade.attributeValuesAsList("owningUser").isEmpty()) {
		            owningUser = AccessControl_1.this.getQualifiedPrincipalName((Path)replacementFacade.attributeValue("owningUser"));
		        }
		        else {
		            // if no user found set owner to segment administrator
		            owningUser = objectFacade.attributeValuesAsList("owner").isEmpty() ? 
		            	userIdentity == null ? 
		            		AccessControl_1.this.getQualifiedPrincipalName(replacementFacade.getPath(), SecurityKeys.ADMIN_PRINCIPAL) : 
		            			AccessControl_1.this.getQualifiedPrincipalName(userIdentity) : 
		            	(String)objectFacade.attributeValue("owner");
		        }
		        newOwner.add(owningUser);
		        // owning group
		        Set<Object> owningGroup = new HashSet<Object>();
		        if(replacementFacade.getAttributeValues("owningGroup") != null) {
	                // replace owning group
		            for(Iterator<Object> i = replacementFacade.attributeValuesAsList("owningGroup").iterator(); i.hasNext(); ) {
	                    Path group = (Path)i.next();
	                    if(group != null) {
	    	                owningGroup.add(
	    	                	AccessControl_1.this.getQualifiedPrincipalName(group)
	    	                );
	                    }
		            }
		        }
		        else {
	                // keep existing owning group
	                if(objectFacade.attributeValuesAsList("owner").size() > 1) {
	                    owningGroup.addAll(
	                    	objectFacade.attributeValuesAsList("owner").subList(1, objectFacade.attributeValuesAsList("owner").size())
	                    );
	                }
		        }
		        newOwner.addAll(owningGroup);
		        // Only replace owner if modified
		        if(!objectFacade.attributeValuesAsList("owner").containsAll(newOwner) || !newOwner.containsAll(objectFacade.attributeValuesAsList("owner"))) {
		        	replacementFacade.attributeValuesAsList("owner").clear();
		        	replacementFacade.attributeValuesAsList("owner").addAll(newOwner);
		        }
		        replacementFacade.getValue().keySet().remove("owningUser");
		        replacementFacade.getValue().keySet().remove("owningGroup");
	        }
	        objectCache.remove(request.path());
	        super.put(
	        	ispec, 
	        	input, 
	        	output
	        );
	        AccessControl_1.this.completeReply(
	            header,
	            reply,
	            null
	        );
	        return true;
	    }

	    //-----------------------------------------------------------------------
	    @Override
	    public boolean invoke(
	        RestInteractionSpec ispec, 
	        MessageRecord input, 
	        MessageRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	        DefaultRealm realm = AccessControl_1.this.getRealm(
	            header,
	            request
	        );
	        String operationName = request.path().get(
	            request.path().size() - 2
	        );
	        if("checkPermissions".equals(operationName)) {
	        	AccessControl_1.this.getRealm(
	                header,
	                request
	            );
	            String principalName = (String)Facades.asObject(request.object()).attributeValue("principalName");
	            CachedPrincipal principal = realm.getPrincipal(principalName);    
	            if(principal == null) {
	                throw new ServiceException(
	                    OpenCrxException.DOMAIN,
	                    OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
	                    "Requested principal not found.",
	                    new BasicException.Parameter("principal", principalName),
	                    new BasicException.Parameter("param0", principalName),
	                    new BasicException.Parameter("param1", AccessControl_1.this.realmIdentity)
	                );            
	            }
	            Path objectIdentity = request.path().getParent().getParent();
	            Path userIdentity = AccessControl_1.this.getUser(principal);
	            MappedRecord parent = objectIdentity.size() <= 5 ?
	            	null:
	        		this.getCachedObject(
		                header,
		                objectIdentity
		            );            
	            Object_2Facade parentFacade = parent == null ? null : Facades.asObject(parent);
	            MappedRecord object = this.retrieveObject(            
	                objectIdentity
	            );
	            Object_2Facade objectFacade = Facades.asObject(object);
	            MappedRecord result = AccessControl_1.this.createResult(
	                request,
	                "org:opencrx:kernel:base:CheckPermissionsResult"
	            );
	            Object_2Facade replyFacade = Facades.asObject(result);
	            // Read permissions
	            Set<String> grantedPermissions = new HashSet<String>();
	            boolean hasPermission = realm.hasPermission(
	            	request, 
	            	objectFacade, 
	            	parentFacade, 
	            	principal, 
	            	userIdentity, 
	            	Action.READ, 
	            	grantedPermissions,
	            	this
	            );
                replyFacade.attributeValuesAsList("grantedPermissionsRead").addAll(grantedPermissions);
	            replyFacade.attributeValuesAsList("hasReadPermission").add(hasPermission);
	            // Delete permissions
	            grantedPermissions = new HashSet<String>();
	            hasPermission = realm.hasPermission(
	            	request, 
	            	objectFacade, 
	            	parentFacade, 
	            	principal, 
	            	userIdentity, 
	            	Action.DELETE, 
	            	grantedPermissions,
	            	this
	            );
                replyFacade.attributeValuesAsList("grantedPermissionsDelete").addAll(grantedPermissions);
	            replyFacade.attributeValuesAsList("hasDeletePermission").add(hasPermission);
	            // Update
	            grantedPermissions = new HashSet<String>();
	            hasPermission = realm.hasPermission(
	            	request, 
	            	objectFacade, 
	            	parentFacade, 
	            	principal, 
	            	userIdentity, 
	            	Action.UPDATE, 
	            	grantedPermissions,
	            	this
	            );
                replyFacade.attributeValuesAsList("grantedPermissionsUpdate").addAll(grantedPermissions);
	            replyFacade.attributeValuesAsList("hasUpdatePermission").add(hasPermission);
	            // Return result
	            output.setPath(replyFacade.getPath());
	            output.setBody(replyFacade.getValue());
	            return true;
	        }
	        super.invoke(
	            ispec,
	            input,
	            output
	        );
	        return true;
	    }
	    
    }

    //-------------------------------------------------------------------------
    protected void completeOwningUserAndGroup(
      ServiceHeader header,
      MappedRecord object
    ) throws ServiceException {
    	Object_2Facade facade = Facades.asObject(object);
    	facade.getValue().keySet().remove("owningUser");
    	facade.getValue().keySet().remove("owningGroup");
        if(!facade.attributeValuesAsList("owner").isEmpty()) {
            if((String)facade.attributeValue("owner") == null) {
            	SysLog.error("Values of attribute owner are corrupt. Element at index 0 (owning user) is missing. Fix the database", object);
            }
            else {
            	facade.attributeValuesAsList("owningUser").add(
	                this.getUserIdentity((String)facade.attributeValue("owner"))
	            );
            }
        }
        for(int i = 1; i < facade.attributeValuesAsList("owner").size(); i++) {
        	facade.attributeValuesAsList("owningGroup").add(
        		this.getGroupIdentity(facade.getPath(), (String)facade.attributeValuesAsList("owner").get(i))
            );            
        }       
    }

    //-------------------------------------------------------------------------
    protected void completeObject(
      ServiceHeader header,
      MappedRecord object,
      MappedRecord parent
    ) throws ServiceException {
        this.completeOwningUserAndGroup(
            header,
            object
        );
    }

    //-------------------------------------------------------------------------
    protected DataproviderReply completeReply(
      ServiceHeader header,
      DataproviderReply reply,
      MappedRecord parent
    ) throws ServiceException {
    	if(reply.getResult() != null) {
	      for(int i = 0; i < reply.getObjects().length; i++) {
	    	  MappedRecord object = reply.getObjects()[i];
	          this.completeObject(
	              header,
	              object,
	              parent
	          );
	      }
    	}
    	return reply;
    }

    //-------------------------------------------------------------------------
    protected boolean isPrincipalGroup(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        return this.model.isSubtypeOf(
            objectClass,
            "org:opencrx:security:realm1:PrincipalGroup"
        );
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        if(objectClass == null) {
        	SysLog.error("Undefined object class", Object_2Facade.getPath(object));
            return true;
        }
        else {
            return this.model.isSubtypeOf(
                objectClass,
                "org:opencrx:kernel:base:SecureObject"
            );
        }
    }

    //-------------------------------------------------------------------------
    protected boolean isSecureObject(
      ModelElement_1_0 type
    ) throws ServiceException {
        return this.model.isSubtypeOf(
            type,
            "org:opencrx:kernel:base:SecureObject"
        );
    }

    //-------------------------------------------------------------------------
    public PersistenceManager newDelegatingPersistenceManager(
    ) {
        if(!this.connectionFactories.isEmpty()) {
        	ConnectionFactoryAdapter connectionFactoryAdapter = (ConnectionFactoryAdapter)this.connectionFactories.get(0);
        	Map<String,Object> entityManagerConfiguration = new HashMap<String,Object>();
        	entityManagerConfiguration.put(
            	ConfigurableProperty.ConnectionFactory.qualifiedName(),
            	DataManagerFactory_1.getPersistenceManagerFactory(
        			Collections.singletonMap(
        				ConfigurableProperty.ConnectionFactory.qualifiedName(),
        				connectionFactoryAdapter
        			)            		
            	)
            );
            entityManagerConfiguration.put(
            	ConfigurableProperty.PersistenceManagerFactoryClass.qualifiedName(),
            	EntityManagerFactory_1.class.getName()
            );	            	
        	return JDOHelper.getPersistenceManagerFactory(
        		entityManagerConfiguration
        	).getPersistenceManager(
        		SecurityKeys.ROOT_PRINCIPAL,
        		null
        	);
        }    
        return null;
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0#activate(short, org.openmdx.compatibility.base.application.configuration.Configuration, org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0)
     */
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1 delegation
    ) throws ServiceException {
        super.activate(
        	id, 
        	configuration, 
        	delegation
        );
        this.model = Model_1Factory.getModel();
        // realmIdentity
        if(!configuration.values(ConfigurationKeys.REALM_IDENTITY).isEmpty()) {
            this.realmIdentity = new Path((String)configuration.values(ConfigurationKeys.REALM_IDENTITY).get(0));
        }
        else {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.INVALID_CONFIGURATION, 
                "A realm identity must be configured with option 'realmIdentity'"
            );
        }         
        // Get dataprovider connections
        this.connectionFactories = new ArrayList<Object>(
        	configuration.values(
        		SharedConfigurationEntries.DATAPROVIDER_CONNECTION_FACTORY
        	).values()
        );
        // useExtendedAccessLevelBasic
        this.useExtendedAccessLevelBasic = configuration.values("useExtendedAccessLevelBasic").isEmpty() ?
            false :
            ((Boolean)configuration.values("useExtendedAccessLevelBasic").get(0)).booleanValue();        
    }

    //-------------------------------------------------------------------------
    protected String getPrincipalName(
    	ServiceHeader header
    ) {
    	return header.getPrincipalChain().isEmpty() ? 
        	null : 
        	(String)header.getPrincipalChain().get(0);
    }

    //-------------------------------------------------------------------------
    /**
     * Allows to provide a custom-specific realm implementation.
     */
    protected DefaultRealm newRealm(
    	Path realmIdentity
    ) throws ServiceException {
        return new DefaultRealm(realmIdentity);
    }

    //-------------------------------------------------------------------------
    protected DefaultRealm getRealm(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        String principalName = this.getPrincipalName(header); 
        String realmName = SecurityKeys.ROOT_PRINCIPAL.equals(principalName) ? 
        	"Root" : 
        	request.path().get(4);
        if(this.cachedRealms.get(realmName) == null) {
            this.cachedRealms.put(
                realmName,
                this.newRealm(
                    this.realmIdentity.getParent().getChild(realmName)
                )
            );
        }
        DefaultRealm realm = this.cachedRealms.get(realmName);
        CachedPrincipal requestingPrincipal = realm.getPrincipal(principalName);        
        if(requestingPrincipal == null) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.AUTHORIZATION_FAILURE_MISSING_PRINCIPAL, 
                "Requested principal not found.",
                new BasicException.Parameter("principal", header.getPrincipalChain()),
                new BasicException.Parameter("param0", header.getPrincipalChain()),
                new BasicException.Parameter("param1", this.realmIdentity)
            );
        }
        SysLog.detail("Requesting principal", requestingPrincipal);
        return realm;
    }

    //-------------------------------------------------------------------------
    protected ModelElement_1_0 getReferencedType(
        Path accessPath,
        FilterProperty[] filter
    ) throws ServiceException {
        // Extent access requires special treatment
        // get the set of referenceIds specified by filter property 'identity'
        boolean isExtent = false;        
        if(filter != null && accessPath.isLike(EXTENT_PATTERN)) {
            for(
                int i = 0;
                i < filter.length;
                i++
            ) {
                FilterProperty p = filter[i];
                if(SystemAttributes.OBJECT_IDENTITY.equals(p.name())) {
                    if(p.values().size() > 1) {
                        throw new ServiceException(
                            BasicException.Code.DEFAULT_DOMAIN,
                            BasicException.Code.NOT_SUPPORTED, 
                            "at most one value allowed for filter property 'identity'",
                            new BasicException.Parameter("filter", (Object[])filter)
                        );                        
                    }
                    isExtent = true;
                    accessPath = new Path(p.values().iterator().next().toString());
                }
            }
            if(!isExtent) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_SUPPORTED, 
                    "extent lookups require at least a filter value for property 'identity'",
                    new BasicException.Parameter("filter", (Object[])filter)
                );            
            }
        }
        return this.model.getTypes(accessPath)[2];        
    }
    
    //-------------------------------------------------------------------------
    protected MappedRecord createResult(
        DataproviderRequest request,
        String structName
    ) throws ServiceException {
    	try {
	    	MappedRecord result = Object_2Facade.newInstance(
	            request.path().getDescendant(
	                new String[]{ "reply", UUIDs.newUUID().toString()}
	            ),
	            structName
	        ).getDelegate();
	        return result;
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }

    //-----------------------------------------------------------------------
    protected static ConcurrentMap<Path,Object[]> getObjectCache(
    ) {
    	return objectCache;
    }
    
    //-----------------------------------------------------------------------
    protected final static Path EXTENT_PATTERN = 
        new Path("xri:@openmdx:**/provider/**/segment/**/extent");
    
    protected static final Path USER_HOME_PATH_PATTERN =
        new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*");

    protected static final String ALL_PERMISSION = "*";
    
    protected List<Object> connectionFactories = null;    
    protected Path realmIdentity = null;
    protected Model_1_0 model = null;
    protected boolean useExtendedAccessLevelBasic = false;

    // Cached realms
    private Map<String,DefaultRealm> cachedRealms = 
    	new ConcurrentHashMap<String,DefaultRealm>();
    
    // TTL of cached objects
    private static final long TTL_CACHED_OBJECTS = 20000L;
    
    // Entry contains (path,[object,ttl])
    protected static final ConcurrentMap<Path,Object[]> objectCache = 
        new ConcurrentHashMap<Path,Object[]>();
    
}

//--- End of File -----------------------------------------------------------

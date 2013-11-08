/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: PortalExtension
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2011, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefStruct;

import org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.ResourceQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessState;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Addresses;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.Exporter;
import org.opencrx.kernel.backend.SecureObject;
import org.opencrx.kernel.code1.jmi1.AbstractEntry;
import org.opencrx.kernel.code1.jmi1.CodeValueContainer;
import org.opencrx.kernel.contract1.jmi1.ContractCreator;
import org.opencrx.kernel.contract1.jmi1.ContractType;
import org.opencrx.kernel.contract1.jmi1.SalesContract;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.ExportProfileQuery;
import org.opencrx.kernel.home1.jmi1.ExportProfile;
import org.opencrx.kernel.portal.AbstractPropertyDataBinding.PropertySetHolderType;
import org.opencrx.kernel.portal.action.GridExportAsXlsAction;
import org.opencrx.kernel.portal.action.GridExportAsXmlAction;
import org.opencrx.kernel.portal.action.GridExportIncludingCompositesAsXmlAction;
import org.opencrx.kernel.portal.action.GridExportWysiwygAllColumnsAsXlsAction;
import org.opencrx.kernel.portal.action.GridExportWysiwygAsXlsAction;
import org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension;
import org.opencrx.kernel.portal.wizard.CreateAccountWizardExtension;
import org.opencrx.kernel.portal.wizard.FileBrowserWizardExtension;
import org.opencrx.kernel.utils.QueryBuilderUtil;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.accessor.jmi.spi.RefMetaObject_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.spi.PersistenceManagers;
import org.openmdx.base.persistence.spi.QueryExtension;
import org.openmdx.base.query.Condition;
import org.openmdx.base.query.Extension;
import org.openmdx.base.query.IsInCondition;
import org.openmdx.base.query.IsLikeCondition;
import org.openmdx.base.query.Quantifier;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ActionFactory_1_0;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.Autocompleter_1_0;
import org.openmdx.portal.servlet.Codes;
import org.openmdx.portal.servlet.DataBinding;
import org.openmdx.portal.servlet.DefaultPortalExtension;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ValueListAutocompleter;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.WebKeys;
import org.openmdx.portal.servlet.control.Control;
import org.openmdx.portal.servlet.view.Grid;
import org.openmdx.portal.servlet.view.ObjectView;
import org.openmdx.portal.servlet.view.ShowObjectView;
import org.openmdx.security.realm1.jmi1.Principal;

/**
 * PortalExtension
 *
 */
public class PortalExtension extends DefaultPortalExtension implements Serializable {

	/**
	 * Constructor.
	 * 
	 */
	public PortalExtension(
	) {
		super();
	}

	/**
	 * CachedPermission
	 *
	 */
	static class CachedPermission implements Comparable<CachedPermission> {
		
		public CachedPermission(
			String permission,
			String action
		) {
			this.permission = permission;
			this.action = action;
		}
		
		@Override
        public int compareTo(
        	CachedPermission that
        ) {
        	int compare = this.permission.compareTo(that.permission);
        	return compare == 0 ?
        		this.action.compareTo(that.action) : 
        			compare;	        		
        }
		
		@Override
        public String toString(
        ) {
			return this.permission + "|" + this.action;
        }

		public String permission;
		public String action;

	}
	
	/**
	 * PermissionsCache
	 *
	 */
	static class PermissionsCache {
	
		public PermissionsCache(
			Collection<org.openmdx.security.realm1.jmi1.Permission> permissions			
		) {
			this.expiresAt = System.currentTimeMillis() + TTL;
			this.permissions = new TreeSet<CachedPermission>();
			for(org.openmdx.security.realm1.jmi1.Permission permission: permissions) {
				for(String action: permission.getAction()) {
					this.permissions.add(new CachedPermission(permission.getName(), action));
				}
			}
		}
		
		public boolean containsPermission(
			String permission,
			String action
		) {
			return this.permissions.contains(
				new CachedPermission(permission, action)
			);
		}
		
		public boolean isExpired(
		) {
			return System.currentTimeMillis() > this.expiresAt;
		}
		
		private final Set<CachedPermission> permissions;
		private final long expiresAt;
		private static final long TTL = 60000;
		
	}
	
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getFindObjectsBaseFilter(org.openmdx.portal.servlet.ApplicationContext, org.openmdx.base.accessor.jmi.cci.RefObject_1_0, java.lang.String)
     */
    @Override
    public List<Condition> getFindObjectsBaseFilter(
        ApplicationContext application,
        RefObject_1_0 context, 
        String referenceName
    ) {
        List<Condition> baseFilter = super.getFindObjectsBaseFilter(
            application, 
            context, 
            referenceName
        );
        // Add disabled filter for types with attribute 'disabled'
        boolean excludeDisabled = false;
        try {
            Model_1_0 model = application.getModel();
            ModelElement_1_0 parentDef = ((RefMetaObject_1)context.refMetaObject()).getElementDef();                
            ModelElement_1_0 referenceDef = (ModelElement_1_0)parentDef.objGetMap("reference").get(referenceName);
            if(referenceDef != null) {
                ModelElement_1_0 referencedType = model.getElement(referenceDef.objGetValue("type"));                
                excludeDisabled = model.getAttributeDefs(referencedType, true, false).containsKey("disabled");
            }
        } catch(Exception e) {}
        if(excludeDisabled) {
            baseFilter.add(
                new IsInCondition(
                    Quantifier.FOR_ALL,
                    "disabled",
                    true,
                    Boolean.FALSE
                )             
            );
        }
        return baseFilter;
    }

    /**
     * Get code mapper used for object title generation.
     * 
     * @param codes
     * @return
     */
    protected Base.CodeMapper getCodeMapper(
    	final Codes codes
    ) {
		return new Base.CodeMapper() {						
			@Override
			public String getLocaleText(short locale) {
				return (String)codes.getShortText("locale", (short)0, true, true).get(locale);
			}
			@Override
			public String getCurrencyText(short currency, short locale) {
				return (String)codes.getLongText("currency", locale, true, true).get(currency);
			}
			@Override
			public String getCountryText(short country, short locale) {
				return (String)codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(country);
			}
		};	
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getTitle(org.openmdx.base.accessor.jmi.cci.RefObject_1_0, short, java.lang.String, boolean, org.openmdx.portal.servlet.ApplicationContext)
     */
    @Override
    public String getTitle(
        RefObject_1_0 obj,
        short locale,
        String localeAsString,
        boolean asShortTitle,
        ApplicationContext app
    ) {
        if(obj == null) {
            return "#NULL";
        }
        if(JDOHelper.isNew(obj) || !JDOHelper.isPersistent(obj)) {
            return "Untitled";
        }
        try {
        	if(obj instanceof org.openmdx.base.jmi1.Segment) {
        		return app.getLabel(obj.refClass().refMofId());
        	}
        	else {
        		final Codes codes = app.getCodes();
        		String title = Base.getInstance().getTitle(
        			obj,
        			this.getCodeMapper(codes),
        			locale, 
        			asShortTitle
        		);
        		return title == null ?
        			super.getTitle(obj, locale, localeAsString, asShortTitle, app) :
        				title;
        	}
        } catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            SysLog.info(e0.getMessage(), e0.getCause());
            SysLog.info("can not evaluate. object", obj.refMofId());
            return "#ERR (" + e.getMessage() + ")";
        }
    }

    /**
     * Returns true if principal has permission for the given permission / action.
     * 
     * @param principal
     * @param permission
     * @param specificPermission
     * @param action
     * @return
     */
    protected boolean hasPermission(
    	org.openmdx.security.realm1.jmi1.Principal principal,
    	String permission,
    	String specificPermission,
    	String action
    ) {
    	String actionGrant = null;
    	String actionRevoke = null;
    	if(action.startsWith(WebKeys.GRANT_PREFIX)) {
    		actionGrant = action;
    		actionRevoke = WebKeys.REVOKE_PREFIX + action.substring(1);
    	} else if(action.startsWith(WebKeys.REVOKE_PREFIX)) {
    		actionGrant = WebKeys.GRANT_PREFIX + action.substring(1);
    		actionRevoke = action;
    	}
		Boolean allow = null;    	
    	if(
    		actionGrant != null &&
    		actionRevoke != null &&
    		principal instanceof org.opencrx.security.realm1.jmi1.Principal
    	) {
    		org.opencrx.security.realm1.jmi1.Principal requestingPrincipal = (org.opencrx.security.realm1.jmi1.Principal)principal;
    		List<org.openmdx.security.realm1.jmi1.Role> roles = requestingPrincipal.getGrantedRole();
    		List<org.openmdx.security.realm1.jmi1.Role> validatedRoles = new ArrayList<org.openmdx.security.realm1.jmi1.Role>();
    		for(Iterator<org.openmdx.security.realm1.jmi1.Role> i = roles.iterator(); i.hasNext(); ) {
    			try {
    				validatedRoles.add(i.next());
    			} catch(Exception e) {
    				SysLog.warning("Role can not be accessed. Ignoring.", e.getMessage());
    				new ServiceException(e).log();
    			}
    		}
    		for(org.openmdx.security.realm1.jmi1.Role role: validatedRoles) {
    			if(!Boolean.TRUE.equals(role.isDisabled())) {
	    			PermissionsCache cache = cachedPermissionsByRole.get(role.getName());
	    			if(cache == null || cache.isExpired()) {
		    			Collection<org.openmdx.security.realm1.jmi1.Permission> permissions = role.getPermission();
	    				cachedPermissionsByRole.put(
	    					role.getName(),
	    					cache = new PermissionsCache(permissions)
	    				);
	    			}
	    			if(cache.containsPermission(permission, actionGrant)) {
	    				allow = true;
	    			}
	    			if(cache.containsPermission(permission, actionRevoke)) {
	    				allow = false;
	    			}
	    			if(specificPermission != null) {
	    				if(cache.containsPermission(specificPermission, actionGrant)) {
	    					allow = true;
	    				}
	    				if(cache.containsPermission(specificPermission, actionRevoke)) {
	    					allow = false;
	    				}
	    			}
    			}
    		}
    	}
    	return allow == null ? 
    		false :
    			action.startsWith(WebKeys.GRANT_PREFIX) ?
    				allow :
    					!allow;    
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#hasPermission(java.lang.String, org.openmdx.base.accessor.jmi.cci.RefObject_1_0, org.openmdx.portal.servlet.ApplicationContext, java.lang.String)
     */
    @Override
    public boolean hasPermission(
        String elementName, 
        RefObject_1_0 refObj,
        ApplicationContext app,
        String action
    ) {
    	// Disable some wizards for root admin
    	if(
    		this.isRootPrincipal(app.getLoginPrincipal()) &&
    		elementName != null && 
    		(elementName.indexOf("ManageGUIPermissionsWizard") > 0 ||
    		elementName.indexOf("DashboardWizard") > 0 ||
    		elementName.indexOf("WorkspaceDashboardWizard") > 0)
    	) {
    		return action.startsWith(WebKeys.REVOKE_PREFIX);
    	}
    	if(refObj instanceof org.opencrx.kernel.depot1.jmi1.CompoundBooking) {
            try {
                org.opencrx.kernel.depot1.jmi1.CompoundBooking cb = (org.opencrx.kernel.depot1.jmi1.CompoundBooking)refObj;
                boolean isPending = cb.getBookingStatus() == 1;     
                boolean isProcessed = cb.getBookingStatus() == 2;
                boolean isReversal = cb.getBookingType() == 30;
                if("org:opencrx:kernel:depot1:CompoundBooking:cancelCb".equals(elementName)) {
                    return isProcessed && !isReversal;
                }
                else if("org:opencrx:kernel:depot1:CompoundBooking:acceptCb".equals(elementName)) {
                    return isPending;
                }
                else if("org:opencrx:kernel:depot1:CompoundBooking:finalizeCb".equals(elementName)) {
                    return isPending;
                }
            }
            catch(Exception e) {
                ServiceException e0 = new ServiceException(e);
                SysLog.warning(e0.getMessage(), e0.getCause());                
            }
        }
    	if(refObj != null) {
	    	PersistenceManager pm = JDOHelper.getPersistenceManager(refObj);
	    	String providerName = app.getUserHomeIdentityAsPath().get(2);
	    	String segmentName = app.getUserHomeIdentityAsPath().get(4);
	    	org.openmdx.security.realm1.jmi1.Principal principal = Utils.getRequestingPrincipal(pm, providerName, segmentName);
	    	String specificElementName = null;
	    	if(elementName.indexOf(":") > 0) {
	    		specificElementName = refObj.refClass().refMofId() + elementName.substring(elementName.lastIndexOf(":"));
	    	}
	    	boolean hasPermission = this.hasPermission(
	    		principal, 
	    		elementName, 
	    		specificElementName, 
	    		action
	    	);
	    	if(hasPermission) {
	    		return true;
	    	}
    	}
        return super.hasPermission(
            elementName, 
            refObj,
            app,
            action
        );
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#hasPermission(org.openmdx.portal.servlet.control.Control, org.openmdx.base.accessor.jmi.cci.RefObject_1_0, org.openmdx.portal.servlet.ApplicationContext, java.lang.String)
     */
    @Override
    public boolean hasPermission(
        Control control, 
        RefObject_1_0 refObj,
        ApplicationContext app,
        String action       
    ) {
    	if(refObj != null) {
	    	PersistenceManager pm = JDOHelper.getPersistenceManager(refObj);
	    	String providerName = app.getUserHomeIdentityAsPath().get(2);
	    	String segmentName = app.getUserHomeIdentityAsPath().get(4);
	    	org.openmdx.security.realm1.jmi1.Principal principal = Utils.getRequestingPrincipal(pm, providerName, segmentName);
	    	boolean hasPermission = this.hasPermission(
	    		principal, 
	    		control.getId(), 
	    		null, // specificPermission
	    		action
	    	);
	    	if(hasPermission) {
	    		return true;
	    	}
    	}
        return super.hasPermission(
            control, 
            refObj, 
            app,
            action
        );
    }
    
    /**
     * Return a filter with the given clause and parameters as query condition.
     * @param clause
     * @param stringParams
     * @param app
     * @return
     */
    protected org.openmdx.base.query.Filter getQueryConditions(
    	String clause,
    	List<String> stringParams,
    	ApplicationContext app
    ) {
    	org.openmdx.base.query.Filter filter = new org.openmdx.base.query.Filter();
    	Extension queryExtension = new QueryExtension();
    	queryExtension.setClause(clause);
    	queryExtension.setStringParam(stringParams.toArray(new String[stringParams.size()]));    	
    	filter.getExtension().add(queryExtension);
    	return filter;
    }
    
    /**
     * Get predicate for case-insensitive match of account's fullName.
     * 
     * @param qualifiedFeatureName
     * @param negate
     * @param s0
     * @param s1
     * @return
     */
    protected QueryBuilderUtil.Predicate getAccountFullNameMatchesPredicate(
    	String qualifiedFeatureName,
    	boolean negate,
    	String... params
    ) {
		return new QueryBuilderUtil.ReferencePredicate(
			Utils.getUidAsString(),
			null, // description
			qualifiedFeatureName,
			QueryBuilderUtil.ReferencePredicate.Condition.EXISTS,
			new QueryBuilderUtil.OrPredicate(
				Utils.getUidAsString(), 
				null, // description
				negate // negate
			).or(
				new QueryBuilderUtil.SingleValuedAttributePredicate(
					Utils.getUidAsString(), 
    				null, // description
					"UPPER(fullName)",
					"org:opencrx:kernel:account1:Account:fullName", 
					QueryBuilderUtil.SingleValuedAttributePredicate.Condition.IS_LIKE, 
					"UPPER(" + params[0] + ")"
				)
			).or(
				new QueryBuilderUtil.SingleValuedAttributePredicate(
					Utils.getUidAsString(), 
    				null, // description
					"UPPER(fullName)",
					"org:opencrx:kernel:account1:Account:fullName", 
					QueryBuilderUtil.SingleValuedAttributePredicate.Condition.IS_LIKE, 
					params[1]
				)
			)
	    );
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getQuery(org.openmdx.ui1.jmi1.ValuedField, java.lang.String, int, org.openmdx.portal.servlet.ApplicationContext)
     */
    @Override
    public org.openmdx.base.query.Filter getQuery(
    	String qualifiedFeatureName,
    	String filterValue,
    	int queryFilterStringParamCount,
    	ApplicationContext app
    ) throws ServiceException {
    	String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
    	QueryConditionParser conditionParser = this.getQueryConditionParser(
    		qualifiedFeatureName,
			new IsLikeCondition(
				Quantifier.THERE_EXISTS,
				featureName,
				true,
				(Object[])null
			)
    	);
    	Condition condition = conditionParser.parse(filterValue);
    	filterValue = filterValue.substring(conditionParser.getOffset());
    	if(condition instanceof IsLikeCondition) {
	    	String clause = null;
    		boolean negate = !((IsLikeCondition)condition).isFulfil();
	    	int paramCount = queryFilterStringParamCount;
	    	String s0 = "?s" + paramCount++;
	    	String s1 = "?s" + paramCount++;
	    	List<String> stringParams = new ArrayList<String>();
	        String stringParam = app.getWildcardFilterValue(filterValue);
	        String stringParam0 = stringParam.startsWith("(?i)") ? stringParam.substring(4) : stringParam;
	        String stringParam1 = stringParam.startsWith("(?i)") ? stringParam.substring(4).toUpperCase() : stringParam.toUpperCase();
	        if(
	        	"org:opencrx:kernel:contract1:SalesContract:salesRep".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:contract1:SalesContract:customer".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:contract1:SalesContract:supplier".equals(qualifiedFeatureName)	        	
	        ) {
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.contract1").getDescendant("provider", ":*", "segment", ":*", "salesOrder"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if(
	        	"org:opencrx:kernel:activity1:Activity:assignedTo".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:activity1:Activity:reportingContact".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:activity1:Activity:reportingAccount".equals(qualifiedFeatureName)	        	
	        ) {
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.activity1").getDescendant("provider", ":*", "segment", ":*", "activity"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);            	
	        } else if("org:opencrx:kernel:product1:PriceListEntry:product".equals(qualifiedFeatureName)) {
	        	clause = "EXISTS (SELECT 0 FROM OOCKE1_PRODUCT p WHERE v.product = p.object_id AND " + (negate ? "NOT" : "") + " (UPPER(p.name) LIKE UPPER(" + s0 + ") OR UPPER(p.name) LIKE " + s1 + "))";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if("org:opencrx:kernel:home1:UserHome:contact".equals(qualifiedFeatureName)) {
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.home1").getDescendant("provider", ":*", "segment", ":*", "userHome"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);            	
	        } else if("org:opencrx:kernel:account1:AccountAssignment:account".equals(qualifiedFeatureName)) {            
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.account1").getDescendant("provider", ":*", "segment", ":*", "account", ":*", "member"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if("org:opencrx:kernel:contract1:ContractRole:account".equals(qualifiedFeatureName)) {
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.contract1").getDescendant("provider", ":*", "segment", ":*", "contractRole"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if(
	        	"org:opencrx:kernel:account1:AccountMembership:accountFrom".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:account1:AccountMembership:accountTo".equals(qualifiedFeatureName) ||
	        	"org:opencrx:kernel:account1:AccountMembership:forUseBy".equals(qualifiedFeatureName)
	        ) {
        		clause = this.getAccountFullNameMatchesPredicate(
        			qualifiedFeatureName, 
        			negate, 
        			s0, s1
        		).toSql(
	        		"", 
	        		new Path("xri://@openmdx*org:opencrx.kernel.account1").getDescendant("provider", ":*", "segment", ":*", "account", ":*", "accountMembership"), 
	        		"v"
	        	);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if("org:opencrx:kernel:activity1:AddressGroupMember:address".equals(qualifiedFeatureName)) {   
	        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a WHERE v.address = a.object_id AND " + (negate ? "NOT" : "") + " (UPPER(a.postal_address_line_0) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_address_line_1) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_address_line_2) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_0) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_1) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_2) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.email_address) LIKE UPPER(" + s0 + ")))";  
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if("org:opencrx:kernel:contract1:AbstractContract:account".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNTASSIGNMENT aa INNER JOIN OOCKE1_ACCOUNT a ON aa.account = a.object_id WHERE v.object_id = aa.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	        } else if("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_MOBILE + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:account1:Account:address*Business!emailAddress".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.email_address) LIKE UPPER(" + s0 + ") OR UPPER(a.email_address) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:account1:Account:address*Business!postalCity".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.postal_city) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_city) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:account1:Account:address*Business!region1".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (UPPER(a.region1) LIKE UPPER(" + s0 + ") OR UPPER(a.region1) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:activity1:Activity:identity".equals(qualifiedFeatureName)) {
		    	String s2 = "?s" + paramCount++;
		    	String s3 = "?s" + paramCount++;
		    	String s4 = "?s" + paramCount++;
		    	String s5 = "?s" + paramCount++;		    	
		    	clause = (negate ? "NOT" : "") + " (UPPER(v.name) LIKE UPPER(" + s0 + ") OR UPPER(v.name) LIKE " + s1 + " OR UPPER(v.description) LIKE UPPER(" + s2 + ") OR UPPER(v.description) LIKE " + s3 + " OR UPPER(v.detailed_description) LIKE UPPER(" + s4 + ") OR UPPER(v.detailed_description) LIKE " + s5 + ")";
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
	            stringParams.add(stringParam0);
	            stringParams.add(stringParam1);
		    } else if("org:opencrx:kernel:account1:Account:address*Business!postalCountry".equals(qualifiedFeatureName)) {
		    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND " + (negate ? "NOT" : "") + " (a.postal_country IN (" + filterValue + ")) AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
		    } else {
	            return super.getQuery(
	            	qualifiedFeatureName, 
	            	filterValue, 
	            	queryFilterStringParamCount,
	            	app
	            );
		    }
	        return this.getQueryConditions(
	        	clause,
	        	stringParams,
	        	app
	        );
    	} else {
            return super.getQuery(
            	qualifiedFeatureName,
            	filterValue,
            	queryFilterStringParamCount,
            	app
            );
    	}
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getGridPageSize(java.lang.String)
     */
    @Override
    public int getGridPageSize(
        String referencedTypeName
    ) {
        if("org:opencrx:kernel:product1:SelectableItem".equals(referencedTypeName)) {
            return 500;
        }
        else if("org:opencrx:kernel:model1:EditableOperation".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableParameter".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableAttribute".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableStructureField".equals(referencedTypeName)) {
            return 200;
        }
        else {
            return super.getGridPageSize(referencedTypeName);
        }
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#isLookupType(org.openmdx.base.mof.cci.ModelElement_1_0)
     */
    @Override
    public boolean isLookupType(
        ModelElement_1_0 classDef
    ) throws ServiceException {
        String qualifiedName = (String)classDef.objGetValue("qualifiedName");
        return 
            !"org:opencrx:kernel:generic:CrxObject".equals(qualifiedName) &&
            super.isLookupType(classDef);
    }
        
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getAutocompleter(org.openmdx.portal.servlet.ApplicationContext, org.openmdx.base.accessor.jmi.cci.RefObject_1_0, java.lang.String)
     */
    @Override
    public Autocompleter_1_0 getAutocompleter(
        ApplicationContext app,
        RefObject_1_0 context,
        String qualifiedFeatureName,
        String restrictToType
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(context);
        if(
        	"org:opencrx:kernel:contract1:CreateContractParams:contractType".equals(qualifiedFeatureName) ||
        	"org:opencrx:kernel:contract1:CreateSalesContractParams:contractType".equals(qualifiedFeatureName)        	
        ) {
            List<ObjectReference> selectableValues = null;
            if(context instanceof ContractCreator) {
                selectableValues = new ArrayList<ObjectReference>();
            	ContractCreator contractCreator = (ContractCreator)context;
            	List<ContractType> contractTypes = contractCreator.getContractType();
            	for(ContractType contractType: contractTypes) {
                    selectableValues.add(
                        new ObjectReference(contractType, app)
                    );            		
            	}
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if(
        	"org:opencrx:kernel:activity1:ActivityDoFollowUpParams:transition".equals(qualifiedFeatureName) ||
        	"org:opencrx:kernel:activity1:LinkToAndFollowUpParams:transition".equals(qualifiedFeatureName)        	
        ) {
            List<ObjectReference> selectableValues = null;
            if(context instanceof Activity) {
                Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)context;
                ActivityType activityType = null;
                ActivityProcessState processState = null;
                try {
                    activityType = activity.getActivityType();
                    processState = activity.getProcessState();
                } catch(Exception ignore) {}
                if((activityType != null) && (processState != null)) {
                    selectableValues = new ArrayList<ObjectReference>();
                    org.opencrx.kernel.activity1.jmi1.ActivityProcess activityProcess = activityType.getControlledBy();
                    processState = activity.getProcessState();
                    ActivityProcessTransitionQuery transitionFilter = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
                    transitionFilter.orderByNewPercentComplete().ascending();
                    List<org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition> transitions = activityProcess.getTransition(transitionFilter);
                    for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition: transitions) {
                        if(transition.getPrevState().equals(processState)) {
                            selectableValues.add(
                                new ObjectReference(transition, app)
                            );
                        }
                    }
                }
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if("org:opencrx:kernel:activity1:ActivityAssignToParams:resource".equals(qualifiedFeatureName)) {
            List<ObjectReference> selectableValues = null;
            if(context instanceof Activity) {
                Activity activity = (Activity)context;
            	try {
	                org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(
	                	pm, 
	                	activity.refGetPath().get(2), 
	                	activity.refGetPath().get(4)
	                );
	                ResourceQuery query = (ResourceQuery)pm.newQuery(Resource.class);
	                query.forAllDisabled().isFalse();
	                query.contact().isNonNull();
	                query.orderByName().ascending();
	                selectableValues = new ArrayList<ObjectReference>();
	                int count = 0;
	                List<Resource> resources = activitySegment.getResource(query);
	                for(Resource resource: resources) {
	                    if(resource != null) {
	                        selectableValues.add(
	                            new ObjectReference(resource, app)
	                        );
	                    }
	                }
	                // Show at most 20 values in drop down. Otherwise show lookup
	                if(count >= 20) {
	                    selectableValues = null;
	                }
	            } catch(Exception ignore) {}
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if(
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByPeriodParams:activity".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByDurationParams:activity".equals(qualifiedFeatureName)
        ) {
            List<ObjectReference> selectableValues = null;
            if(context instanceof Resource) {
                Resource resource = (Resource)context;
                ActivityQuery filter = (ActivityQuery)pm.newQuery(Activity.class);
                filter.thereExistsPercentComplete().lessThan(new Short((short)100));
                filter.orderByActivityNumber().ascending();
                selectableValues = new ArrayList<ObjectReference>();
                int count = 0;
                List<Activity> activities = resource.getAssignedActivity(filter);
                for(
                    Iterator<Activity> i = activities.iterator(); 
                    i.hasNext() && count < 20; 
                    count++
                ) {
                    selectableValues.add(
                        new ObjectReference(i.next(), app)
                    );
                }
                // Show at most 20 values in drop down. Otherwise show lookup
                if(count >= 20) {
                    selectableValues = null;
                }
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if(
            "org:opencrx:kernel:base:StringProperty:stringValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:IntegerProperty:integerValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:BooleanProperty:booleanValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:UriProperty:uriValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:DecimalProperty:decimalValue".equals(qualifiedFeatureName)
        ) {
            // org:opencrx:kernel:base:Property:....
            List<String> selectableValues = null;
            if(context instanceof org.opencrx.kernel.base.jmi1.Property) {
                org.opencrx.kernel.base.jmi1.Property property = (org.opencrx.kernel.base.jmi1.Property)context;
                try {
                    if(property.getDomain() != null) {
                        selectableValues = new ArrayList<String>();
                        CodeValueContainer domain = property.getDomain();
                        Collection<AbstractEntry> entries = domain.getEntry();
                        for(AbstractEntry entry: entries) {
                            selectableValues.add(
                                entry.getEntryValue() != null 
                                	? entry.getEntryValue()
                                    // get qualifier as value if no entryValue is specified
                                    : new Path(entry.refMofId()).getBase()
                            );
                        }
                    }
                } catch(Exception ignore) {}
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if("org:opencrx:kernel:base:ExportItemAdvancedParams:itemMimeType".equals(qualifiedFeatureName)) {
            // org:opencrx:kernel:base:ExportItemParams:itemMimeType
            List<String> selectableValues = new ArrayList<String>();
            selectableValues.add("application/x-excel");
            selectableValues.add(Exporter.MIME_TYPE_XML);
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if("org:opencrx:kernel:base:ExportItemParams:exportProfile".equals(qualifiedFeatureName)) {
            // org:opencrx:kernel:base:ExportItemParams:exportProfile
            List<ObjectReference> selectableValues = null;
            if(context instanceof org.opencrx.kernel.base.jmi1.Exporter) {
                String providerName = context.refGetPath().get(2);
                String segmentName = context.refGetPath().get(4);
                String currentPrincipal = app.getUserHomeIdentityAsPath().getBase();
                String adminPrincipal = SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName;
                // Collect export profiles from current user
                try {
                    org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
                        new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", segmentName, "userHome", currentPrincipal)
                    );
                    ExportProfileQuery exportProfileQuery = (ExportProfileQuery)pm.newQuery(ExportProfile.class);
                    exportProfileQuery.orderByName().ascending();
                    Collection<org.opencrx.kernel.home1.jmi1.ExportProfile> exportProfiles = userHome.getExportProfile();
                    for(org.opencrx.kernel.home1.jmi1.ExportProfile exportProfile: exportProfiles) {
                        for(String forClass: exportProfile.getForClass()) {
                            if(app.getModel().isSubtypeOf(context.refClass().refMofId(), forClass)) {
                                if(selectableValues == null) {
                                    selectableValues = new ArrayList<ObjectReference>();
                                }
                                selectableValues.add(
                                    new ObjectReference(exportProfile, app)
                                );
                                break;
                            }
                        }
                    }
                } catch(Exception e) {}
                // Collect shared export profiles from segment admin
                try {
                    if(!currentPrincipal.equals(adminPrincipal)) {
                        org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
                            new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", segmentName, "userHome", adminPrincipal)
                        );
                        ExportProfileQuery exportProfileQuery = (ExportProfileQuery)pm.newQuery(ExportProfile.class);
                        exportProfileQuery.orderByName().ascending();
                        Collection<org.opencrx.kernel.home1.jmi1.ExportProfile> exportProfiles = userHome.getExportProfile();
                        for(org.opencrx.kernel.home1.jmi1.ExportProfile exportProfile: exportProfiles) {
                            for(String forClass: exportProfile.getForClass()) {
                                if(app.getModel().isSubtypeOf(context.refClass().refMofId(), forClass)) {
                                    if(selectableValues == null) {
                                        selectableValues = new ArrayList<ObjectReference>();
                                    }
                                    selectableValues.add(
                                        new ObjectReference(exportProfile, app)
                                    );
                                    break;
                                }
                            }
                        }
                    }
                } catch(Exception ignore) {}
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(selectableValues);
        } else if("org:opencrx:kernel:address1:Addressable:tz".equals(qualifiedFeatureName)) {
            // org:opencrx:kernel:address1:Addressable:tz
        	Set<String> selectableValues = new TreeSet<String>();
        	selectableValues.add("");
            String[] timezones = java.util.TimeZone.getAvailableIDs();
            for(int i = 0; i < timezones.length; i++) {
            	String timezoneID = timezones[i].trim();
            	selectableValues.add(timezoneID);
            }
            return selectableValues == null 
            	? null 
            	: new ValueListAutocompleter(new ArrayList<String>(selectableValues));
        } else {
            return super.getAutocompleter(
                app,
                context,
                qualifiedFeatureName,
                restrictToType
            );
        }
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getLookupView(java.lang.String, org.openmdx.base.mof.cci.ModelElement_1_0, org.openmdx.base.accessor.jmi.cci.RefObject_1_0, java.lang.String, org.openmdx.portal.servlet.ApplicationContext)
     */
    @Override
    public ObjectView getLookupView(
        String id, 
        ModelElement_1_0 lookupType, 
        RefObject_1_0 startFrom, 
        String filterValues,
        ApplicationContext app
    ) throws ServiceException {
        Model_1_0 model = app.getModel();
        // start from customer if the current object is a contract and the 
        // lookup type is AccountAddress
        if(startFrom instanceof SalesContract) {
            SalesContract contract = (SalesContract)startFrom;
            org.opencrx.kernel.account1.jmi1.Account customer = null;
            try {
                customer = contract.getCustomer();
            } catch(Exception e) {}
            if(
                (customer != null) &&
                model.isSubtypeOf("org:opencrx:kernel:account1:AccountAddress", lookupType)
            ) {
                startFrom = customer;
            }
            ShowObjectView view = (ShowObjectView)super.getLookupView(
                id, 
                lookupType, 
                startFrom, 
                filterValues,
                app
            );
            return view;
        } else {
            // Default lookup
            return super.getLookupView(
                id, 
                lookupType, 
                startFrom, 
                filterValues,
                app
            );
        }
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#hasUserDefineableQualifier(org.openmdx.ui1.jmi1.Inspector, org.openmdx.portal.servlet.ApplicationContext)
     */
    @Override
    public boolean hasUserDefineableQualifier(
        org.openmdx.ui1.jmi1.Inspector inspector,
        ApplicationContext application        
    ) {
        return 
            application.getCurrentUserRole().startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR) ||
            CLASSES_WITH_USER_DEFINABLE_QUALIFER.contains(inspector.getForClass());
    }
        
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#renderTextValue(org.openmdx.portal.servlet.ViewPort, java.lang.String, boolean)
     */
    @Override
    public void renderTextValue(
        ViewPort p,
        String value,
        boolean asWiki
    ) throws ServiceException {
        ApplicationContext app = p.getApplicationContext();
        // Process Tag activity:#
        if(
            (p.getView() instanceof ObjectView) && 
            (value.indexOf("activity:") >= 0)
        ) {
            RefObject_1_0 object = ((ObjectView)p.getView()).getRefObject();
            PersistenceManager pm = JDOHelper.getPersistenceManager(object);
            String providerName = object.refGetPath().get(2);
            String segmentName = object.refGetPath().get(4);
            Path activitySegmentIdentity = 
                new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", providerName, "segment", segmentName);
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(activitySegmentIdentity);
            int currentPos = 0;
            int newPos;
            while((newPos = value.indexOf("activity:", currentPos)) >= 0) {
                super.renderTextValue(
                    p,
                    value.substring(currentPos, newPos),
                    asWiki
                );
                // Get activity number
                int start = newPos + 9;
                int end = start;
                while(
                    (end < value.length()) &&
                    Character.isDigit(value.charAt(end))
                ) {
                    end++;
                }
                // activity: is followed by number. Try to find activity with the
                // corresponding activity number
                if(end > start) {
                    String activityNumber = value.substring(start, end);
                    ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
                    activityQuery.thereExistsActivityNumber().equalTo(
                        activityNumber
                    );
                    Collection<Activity> activities = activitySegment.getActivity(activityQuery);
                    if(activities.size() == 1) {
                        ObjectReference objRef = new ObjectReference(
                            activities.iterator().next(),
                            app
                        );
                        Action action = objRef.getSelectObjectAction();
                      	p.write("<a href=\"\" onclick=\"javascript:this.href=", p.getEvalHRef(action), ";\">", app.getHtmlEncoder().encode(action.getTitle(), false), "</a>");
                    } else {
                        super.renderTextValue(
                            p,
                            value.substring(newPos, end),
                            asWiki
                        );
                    }
                } else {
                    super.renderTextValue(
                        p,
                        value.substring(newPos, end),
                        asWiki
                    );
                }
                currentPos = end;
            }
            super.renderTextValue(
                p,
                value.substring(currentPos),
                asWiki
            );
        } else {
            super.renderTextValue(
                p, 
                value,
                asWiki
            );
        }
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getDataBinding(java.lang.String)
     */
    @Override
    public DataBinding getDataBinding(
        String dataBindingName
    ) {
        if((dataBindingName != null) && dataBindingName.startsWith(StringPropertyDataBinding.class.getName())) {
            return new StringPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(IntegerPropertyDataBinding.class.getName())) {
            return new IntegerPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(BooleanPropertyDataBinding.class.getName())) {
            return new BooleanPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(DecimalPropertyDataBinding.class.getName())) {
            return new DecimalPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(DatePropertyDataBinding.class.getName())) {
            return new DatePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(DateTimePropertyDataBinding.class.getName())) {
            return new DateTimePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(ReferencePropertyDataBinding.class.getName())) {
            return new ReferencePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(EmailAddressDataBinding.class.getName())) {
            return new EmailAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(PhoneNumberDataBinding.class.getName())) {
            return new PhoneNumberDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(PostalAddressDataBinding.class.getName())) {
            return new PostalAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(WebAddressDataBinding.class.getName())) {
            return new WebAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(AssignedActivityGroupsDataBinding.class.getName())) {
            return new AssignedActivityGroupsDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(AccountIsMemberOfDataBinding.class.getName())) {
            return new AccountIsMemberOfDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(AccountAssignmentDataBinding.class.getName())) {
            return new AccountAssignmentDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(DocumentFolderAssignmentsDataBinding.class.getName())) {
            return new DocumentFolderAssignmentsDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        } else if((dataBindingName != null) && dataBindingName.startsWith(EMailRecipientDataBinding.class.getName())) {
            return new EMailRecipientDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        } else if(FilteredActivitiesDataBinding.class.getName().equals(dataBindingName)) {
            return new FilteredActivitiesDataBinding();
        } else if(FormattedNoteDataBinding.class.getName().equals(dataBindingName)) {
            return new FormattedNoteDataBinding();
        } else if(FormattedFollowUpDataBinding.class.getName().equals(dataBindingName)) {
            return new FormattedFollowUpDataBinding();
        } else if(DocumentDataBinding.class.getName().equals(dataBindingName)) {
            return new DocumentDataBinding();
        } else if(ContractHasAssignedActivitiesDataBinding.class.getName().equals(dataBindingName)) {
        	return new ContractHasAssignedActivitiesDataBinding();
        } else {
            return super.getDataBinding(
                dataBindingName
            );
        }
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#handleOperationResult(org.openmdx.base.accessor.jmi.cci.RefObject_1_0, java.lang.String, javax.jmi.reflect.RefStruct, javax.jmi.reflect.RefStruct)
     */
    @Override
    public RefObject_1_0 handleOperationResult(
        RefObject_1_0 target,
        String operationName, 
        RefStruct params, 
        RefStruct result
    ) throws ServiceException {
        if(result instanceof org.opencrx.kernel.home1.jmi1.SearchResult) {
            org.opencrx.kernel.home1.jmi1.SearchResult searchResult = (org.opencrx.kernel.home1.jmi1.SearchResult)result;
            if(searchResult.getObjectFinder() != null) {
            	PersistenceManager pm = JDOHelper.getPersistenceManager(target);
                RefObject_1_0 objectFinder = (RefObject_1_0)pm.getObjectById(
                    searchResult.getObjectFinder().refGetPath()
                );
                return objectFinder;
            }
        }
        return super.handleOperationResult(
            target, 
            operationName, 
            params, 
            result
        );
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getNewUserRole(org.openmdx.portal.servlet.ApplicationContext, org.openmdx.base.naming.Path)
     */
    @Override
    public String getNewUserRole(
    	ApplicationContext app, 
    	Path requestedObjectIdentity
    ) {
    	// Do not change user role in case an object (Principals, Subjects, ...) in segment Root is requested
    	if(requestedObjectIdentity != null && "Root".equals(requestedObjectIdentity.get(4))) {
    		return app.getCurrentUserRole();
    	} else {
    		return super.getNewUserRole(app, requestedObjectIdentity);
    	}
    }
    
	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.DefaultPortalExtension#getGridActions(org.openmdx.portal.servlet.view.ObjectView, org.openmdx.portal.servlet.view.Grid)
	 */
	@Override
    public List<Action> getGridActions(
    	ObjectView view,
    	Grid grid
    ) throws ServiceException {
		final int MAX_SIZE = 500;
		ApplicationContext app = view.getApplicationContext();
	    List<Action> actions = new ArrayList<Action>(super.getGridActions(view, grid));
	    org.openmdx.ui1.jmi1.Element uiExport = app.getUiElement("org:opencrx:kernel:base:Exporter:Pane:Op:Tab:exportItem");
	    String lExport = app.getCurrentLocaleAsIndex() < uiExport.getToolTip().size() 
	    	? uiExport.getToolTip().get(app.getCurrentLocaleAsIndex()) 
	    	: uiExport.getToolTip().get(0);
	    Action exportWysiwygAsXlsAction = new Action(
            GridExportWysiwygAsXlsAction.EVENT_ID, 
            new Action.Parameter[]{
                new Action.Parameter(Action.PARAMETER_OBJECTXRI, view.getRefObject().refMofId()),                                                  
                new Action.Parameter(Action.PARAMETER_SIZE, Integer.toString(MAX_SIZE))                                               
            },
            lExport + " --> XLS (wysiwyg)", 
            true
        );
	    actions.add(exportWysiwygAsXlsAction);
	    Action exportWysiwygAllColumnsAsXlsAction = new Action(
            GridExportWysiwygAllColumnsAsXlsAction.EVENT_ID, 
            new Action.Parameter[]{
                new Action.Parameter(Action.PARAMETER_OBJECTXRI, view.getRefObject().refMofId()),                                                  
                new Action.Parameter(Action.PARAMETER_SIZE, Integer.toString(MAX_SIZE))                                               
            },
            lExport + " --> XLS (wysiwyg+)", 
            true
        );
	    actions.add(exportWysiwygAllColumnsAsXlsAction);
	    Action exportAsXmlAction = new Action(
            GridExportAsXmlAction.EVENT_ID, 
            new Action.Parameter[]{
                new Action.Parameter(Action.PARAMETER_OBJECTXRI, view.getRefObject().refMofId()),                                               
                new Action.Parameter(Action.PARAMETER_SIZE, Integer.toString(MAX_SIZE))                                               
            },
            lExport + " --> XML", 
            true
	    );
	    actions.add(exportAsXmlAction);
	    Action exportAsXlsAction = new Action(
            GridExportAsXlsAction.EVENT_ID, 
            new Action.Parameter[]{
                new Action.Parameter(Action.PARAMETER_OBJECTXRI, view.getRefObject().refMofId()),                                                  
                new Action.Parameter(Action.PARAMETER_SIZE, Integer.toString(MAX_SIZE))                                               
            },
            lExport + " --> XLS", 
            true
        );
	    actions.add(exportAsXlsAction);
	    Action exportIncludingCompositesAsXmlAction = new Action(
            GridExportIncludingCompositesAsXmlAction.EVENT_ID, 
            new Action.Parameter[]{
                new Action.Parameter(Action.PARAMETER_OBJECTXRI, view.getRefObject().refMofId()),                                                  
                new Action.Parameter(Action.PARAMETER_SIZE, Integer.toString(MAX_SIZE))                                               
            },
            lExport + " --> XML+", 
            true
        );
	    actions.add(exportIncludingCompositesAsXmlAction);
	    return actions;
    }
	
	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.DefaultPortalExtension#getActionFactory()
	 */
	@Override
    public ActionFactory_1_0 getActionFactory(
    ) {
		return this.actionFactory;
    }

    /**
     * Returns group memberships of given principal.
     * @param loginPrincipal
     * @param realmName
     * @param pm
     * @return
     */
    protected List<org.openmdx.security.realm1.jmi1.Group> getGroupMembership(
        org.openmdx.security.realm1.jmi1.Principal loginPrincipal,
        String realmName,
        PersistenceManager pm
    ) {
        try {
            String loginPrincipalName = new Path(loginPrincipal.refMofId()).getBase();            
            Path loginPrincipalIdentity = loginPrincipal.refGetPath();
            SysLog.detail("Group membership for segment", realmName);
            SysLog.detail("Group membership for principal", loginPrincipalIdentity);
            org.openmdx.security.realm1.jmi1.Principal principal = 
                (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
                    loginPrincipalIdentity.getPrefix(loginPrincipalIdentity.size()-3).getDescendant(
                        new String[]{realmName, "principal", loginPrincipalName}
                    )
                );
            return principal.getIsMemberOf();
        }
        catch(Exception e) {
        	SysLog.detail("Can not retrieve group membership", e);
            new ServiceException(e).log();
            return null;
        }
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#checkPrincipal(org.openmdx.base.naming.Path, java.lang.String, javax.jdo.PersistenceManager)
     */
    @Override
    public boolean checkPrincipal(
        Path realmIdentity,
        String principalName,
        PersistenceManager pm
    ) throws ServiceException {
        org.openmdx.security.realm1.jmi1.Principal principal = SecureObject.getInstance().findPrincipal(principalName, realmIdentity, pm);
        if(principal == null) {
        	SysLog.info("principal not found in realm", "realm=" + realmIdentity + ", principal=" + principalName);
            return false;
        }
        return !Boolean.TRUE.equals(principal.isDisabled());
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getUserRoles(org.openmdx.base.naming.Path, java.lang.String, javax.jdo.PersistenceManager)
     */
    @Override
    public List<String> getUserRoles(
        Path loginRealmIdentity,
        String loginPrincipalName,
        PersistenceManager pm
    ) throws ServiceException {
    	List<String> principalChain = PersistenceManagers.toPrincipalChain(loginPrincipalName);
    	org.openmdx.security.realm1.jmi1.Realm loginRealm = (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(loginRealmIdentity);
        List<String> roleNames = new ArrayList<String>();
        // Get principals owned by subject
        org.openmdx.security.realm1.jmi1.Principal primaryLoginPrincipal = loginRealm.getPrincipal(principalChain.get(0));
        org.openmdx.security.realm1.jmi1.Subject subject = primaryLoginPrincipal.getSubject();
        org.openmdx.security.realm1.cci2.PrincipalQuery principalQuery = (org.openmdx.security.realm1.cci2.PrincipalQuery)pm.newQuery(org.openmdx.security.realm1.jmi1.Principal.class);
        principalQuery.thereExistsSubject().equalTo(subject);
        List<org.openmdx.security.realm1.jmi1.Principal> allLoginPrincipals = loginRealm.getPrincipal(principalQuery);        
        // Reverse sort user roles by their last login date
        org.openmdx.security.realm1.jmi1.Segment realmSegment = 
            (org.openmdx.security.realm1.jmi1.Segment)pm.getObjectById(loginRealm.refGetPath().getParent().getParent());
        // Iterate all realms
        long leastRecentLoginAt = 0L;
        Collection<org.openmdx.security.realm1.jmi1.Realm> realms = realmSegment.getRealm();
        for(org.openmdx.security.realm1.jmi1.Realm realm: realms) {
        	SysLog.detail("Checking realm", realm);
            // Skip login realm
            if(!realm.equals(loginRealm)) {
                for(org.openmdx.security.realm1.jmi1.Principal loginPrincipal: allLoginPrincipals) {
                    String id = loginPrincipal.refGetPath().getBase();
                    List<String> principalIds = new ArrayList<String>();
                	principalIds.add(id);
                    if(id.equals(principalChain.get(0)) && !id.equals(loginPrincipalName)) {
                    	principalIds.add(loginPrincipalName);
                    }
                    for(String principalId: principalIds) {
	                    SysLog.detail("Checking principal", principalId);
	                    org.openmdx.security.realm1.jmi1.Principal principal = SecureObject.getInstance().findPrincipal(principalId, realm.refGetPath(), pm);
	                    String realmName = realm.getName();
	                    // Do not include root realm in roles except if principal is root
	                    if(principal != null && !Boolean.TRUE.equals(principal.isDisabled()) ) {
	                        try {
	                            List<org.openmdx.security.realm1.jmi1.Group> groups = this.getGroupMembership(
	                                principal,
	                                realmName,
	                                pm
	                            );
	                            SysLog.detail("Principal groups", groups);
	                            if(groups != null) {
	                                long lastLoginAt = 0L;
	                                try {
	                                    Date at = (Date)principal.refGetValue("lastLoginAt");
	                                    if(at != null) {
	                                        lastLoginAt = at.getTime();
	                                    }
	                                } catch(Exception e) {}
	                                String roleId = principalId + "@" + realmName;
	                                SysLog.detail("Checking role", roleId);
	                                if(
	                                    !roleNames.contains(roleId) &&
	                                    (!ROOT_REALM_NAME.equals(realmName) || ROOT_PRINCIPAL_NAME.equals(principalId))                                    
	                                ) {
	                                	SysLog.detail("Adding role", roleId);
	                                    roleNames.add(
	                                        lastLoginAt > leastRecentLoginAt ? 0 : roleNames.size(),
	                                        roleId
	                                    );
	                                }
	                                try {
	                                    for(org.openmdx.security.realm1.jmi1.Group userGroup: groups) {
	                                    	SysLog.detail("Checking group", userGroup);
	                                        String userGroupIdentity = userGroup.refGetPath().getBase();
	                                        if(SecurityKeys.PRINCIPAL_GROUP_ADMINISTRATORS.equals(userGroupIdentity)) {
	                                            roleId = ADMIN_PRINCIPAL_PREFIX + realmName + "@" + realmName;
	                                            if(!roleNames.contains(roleId)) {
	                                            	SysLog.detail("Adding role", roleId);
	                                                roleNames.add(
	                                                    lastLoginAt > leastRecentLoginAt ? 1 : roleNames.size(),
	                                                    roleId
	                                                );
	                                            }
	                                        }
	                                    }
	                                }
	                                // Ignore errors while inspecting groups
	                                catch(Exception e) {}
	                                leastRecentLoginAt = Math.max(lastLoginAt, leastRecentLoginAt);
	                            }
	                        }
	                        // Ignore errors while inspecting user roles (e.g. subject can not be found)
	                        catch(Exception e) {}
	                    }
                    }
                }
            }
        }
        return roleNames;
    }

    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#getAdminPrincipal(java.lang.String)
     */
    @Override
    public String getAdminPrincipal(
        String realmName
    ) {
        return ADMIN_PRINCIPAL_PREFIX + realmName;
    }
  
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#isRootPrincipal(java.lang.String)
     */
    @Override
    public boolean isRootPrincipal(
        String principalName
    ) {
        return principalName.startsWith(ROOT_PRINCIPAL_NAME);
    }
    
    /* (non-Javadoc)
     * @see org.openmdx.portal.servlet.DefaultPortalExtension#setLastLoginAt(org.openmdx.base.naming.Path, java.lang.String, java.lang.String, javax.jdo.PersistenceManager)
     */
    @Override
    public void setLastLoginAt(
    	Path realmIdentity,
    	String segmentName,
    	String principalName,
    	PersistenceManager pm    	
    ) throws ServiceException {
        try {
        	List<String> principalChain = PersistenceManagers.toPrincipalChain(principalName);
            Principal principal = (Principal)pm.getObjectById(
            	realmIdentity.getParent().getDescendant(
                    new String[]{segmentName, "principal", principalChain.get(0)}
                )
            );              
            // Don't care if feature 'lastLoginAt does not exist on principal
            pm.currentTransaction().begin();
            principal.refSetValue("lastLoginAt", new Date());
            pm.currentTransaction().commit();
        } 
        catch(Exception e) {
            try {
                pm.currentTransaction().rollback();
            } catch(Exception e0) {}
            SysLog.info("Unable to set last login date. For more info see detail log", e.getMessage());
            SysLog.detail(e.getMessage(), e.getCause());
        }
    }
    
	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.DefaultPortalExtension#getTimeZone(java.lang.String, org.openmdx.portal.servlet.ApplicationContext)
	 */
	@Override
    public TimeZone getTimeZone(
    	String qualifiedFeatureName, 
    	ApplicationContext app
    ) {
		if(
			"org:opencrx:kernel:account1:Contact:birthdate".equals(qualifiedFeatureName) ||
			"org:opencrx:kernel:account1:Contact:anniversary".equals(qualifiedFeatureName) ||
			"org:opencrx:kernel:account1:Contact:dateOfDeath".equals(qualifiedFeatureName)
		) {
			return TimeZone.getTimeZone("UTC");
		} else {
			return super.getTimeZone(qualifiedFeatureName, app);
		}
    }

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.DefaultPortalExtension#getExtension(java.lang.String)
	 */
	@Override
    public Object getExtension(
    	String name
    ) {
        if((name != null) && name.startsWith(CreateAccountWizardExtension.class.getName())) {
        	return new CreateAccountWizardExtension();
        } else if((name != null) && name.startsWith(ChangePasswordWizardExtension.class.getName())) {
        	return new ChangePasswordWizardExtension();
        } else if((name != null) && name.startsWith(FileBrowserWizardExtension.class.getName())) {
        	return new FileBrowserWizardExtension();
        } else {
        	return super.getExtension(name);
        }
    }

	//-------------------------------------------------------------------------
	// Members
	//-------------------------------------------------------------------------
    private static final long serialVersionUID = 3761691203816992816L;

    private final ActionFactory_1_0 actionFactory = new ActionFactory();
	
    private static Map<String,PermissionsCache> cachedPermissionsByRole = new ConcurrentHashMap<String,PermissionsCache>();
    
    private static final Set<String> CLASSES_WITH_USER_DEFINABLE_QUALIFER =
        new HashSet<String>(Arrays.asList(
            new String[]{
                "org:opencrx:security:realm1:PrincipalGroup",
                "org:opencrx:security:realm1:Principal",                
                "org:opencrx:security:realm1:User",
                "org:opencrx:security:identity:Subject",
                "org:opencrx:kernel:uom1:Uom"
            }
        ));

}

//--- End of File -----------------------------------------------------------

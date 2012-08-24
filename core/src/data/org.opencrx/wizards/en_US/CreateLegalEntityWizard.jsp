﻿<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Name:        $Id: CreateLegalEntityWizard.jsp,v 1.8 2012/07/08 13:30:31 wfro Exp $
 * Description: CreateLegalEntity wizard
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/07/08 13:30:31 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2005-2012, CRIXP Corp., Switzerland
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
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.opencrx.kernel.backend.*,
org.opencrx.kernel.portal.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.text.conversion.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.databinding.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.exception.*,
org.openmdx.base.naming.*
" %><%
	request.setCharacterEncoding("UTF-8");
	String servletPath = "." + request.getServletPath();
	String servletPathPrefix = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(objectXri == null || app == null || viewsCache.getView(requestId) == null) {
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	javax.jdo.PersistenceManager pm = app.getNewPmData();
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
	String formName = "LegalEntityForm";
	String memberFormName = "MemberForm";
	String wizardName = "CreateLegalEntityWizard";
    org.opencrx.kernel.portal.wizard.CreateContactWizardExtension wizardExtension = 
    	(org.opencrx.kernel.portal.wizard.CreateContactWizardExtension)app.getPortalExtension().getExtension(
    		org.opencrx.kernel.portal.wizard.CreateContactWizardExtension.class.getName()
    	);

	// Get Parameters
	String command = request.getParameter("Command");
	if(command == null) command = "";
	boolean actionSave = "OK".equals(command);
	boolean actionCancel = "Cancel".equals(command);
	boolean actionSearch = "Search".equals(command);
	boolean actionCreate = "Create".equals(command);
	String memberXri = request.getParameter("MEMBER_XRI") == null ? "" : request.getParameter("MEMBER_XRI");
	boolean isAddMemberMode = request.getParameter("isAddMemberMode") != null;

	if ("DeleteMember".equals(command)) {
			//System.out.println("delete member: " + request.getParameter("Para0"));
			try {
					pm.currentTransaction().begin();
					org.opencrx.kernel.account1.jmi1.Member member =
						(org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(request.getParameter("Para0")));
					//member.refDelete();
					// do NOT delete, just disable member
					member.setDisabled(new Boolean(true));
					pm.currentTransaction().commit();
			} catch (Exception e) {
					new ServiceException(e);
			}
	}
	List matchingAccounts = null;
	if(actionCancel) {
	  session.setAttribute(wizardName, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
		return;
	}
%>
<!--
	<meta name="UNUSEDlabel" content="Create Legal Entity">
	<meta name="UNUSEDtoolTip" content="Create Legal Entity">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:account1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
	<meta name="order" content="org:opencrx:kernel:account1:Segment:createLegalEntity">
	<meta name="order" content="org:opencrx:kernel:account1:LegalEntity:createLegalEntity">
-->
<%
	String errorMsg = "";
	String errorTitle = "";
	
	org.opencrx.kernel.account1.jmi1.Account account = null;
	org.opencrx.kernel.account1.jmi1.Segment accountSegment = null;
	if(obj instanceof org.opencrx.kernel.account1.jmi1.Account) {
	    account = (org.opencrx.kernel.account1.jmi1.Account)obj;
	    accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(account.refGetPath().getParent().getParent());
	}
	else if(obj instanceof org.opencrx.kernel.account1.jmi1.Segment) {
	    accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)obj;
	}
	org.openmdx.ui1.jmi1.FormDefinition formDefinition = app.getUiFormDefinition(formName);
	org.openmdx.portal.servlet.control.FormControl form = new org.openmdx.portal.servlet.control.FormControl(
		formDefinition.refGetPath().getBase(),
		app.getCurrentLocaleAsString(),
		app.getCurrentLocaleAsIndex(),
		app.getUiContext(),
		formDefinition
	);
	// Member form
	org.openmdx.ui1.jmi1.FormDefinition memberFormDefinition = app.getUiFormDefinition(memberFormName);
	org.openmdx.portal.servlet.control.FormControl memberForm = new org.openmdx.portal.servlet.control.FormControl(
		memberFormDefinition.refGetPath().getBase(),
		app.getCurrentLocaleAsString(),
		app.getCurrentLocaleAsIndex(),
		app.getUiContext(),
		memberFormDefinition
	);

	Map formValues = new HashMap();
	// Initialize formValues with account values
	if(account != null && account instanceof org.opencrx.kernel.account1.jmi1.LegalEntity) {
	    org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = (org.opencrx.kernel.account1.jmi1.LegalEntity)account;
	    formValues.put("org:opencrx:kernel:account1:AbstractGroup:name", legalEntity.getName());
	    formValues.put("org:opencrx:kernel:account1:Account:aliasName", legalEntity.getAliasName());
	    formValues.put("org:opencrx:kernel:account1:LegalEntity:industry", legalEntity.getIndustry());
	    formValues.put("org:opencrx:kernel:account1:Account:description", legalEntity.getDescription());
	    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(account);
	    if(addresses[Accounts.PHONE_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.MOBILE] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.PHONE_OTHER] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_OTHER]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.FAX_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.FAX_BUSINESS]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.MAIL_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Business!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress());
	    }
	    if(addresses[Accounts.MAIL_OTHER] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Other!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_OTHER]).getEmailAddress());
	    }
	    if(addresses[Accounts.WEB_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:LegalEntity:address!webUrl", ((org.opencrx.kernel.account1.jmi1.WebAddress)addresses[Accounts.WEB_BUSINESS]).getWebUrl());
	    }
	    if(addresses[Accounts.POSTAL_BUSINESS] != null) {
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine", new ArrayList(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine()));
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalStreet", new ArrayList(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet()));
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalCity", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCity());
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalState", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalState());
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCode());
		    formValues.put("org:opencrx:kernel:account1:Account:address*Business!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCountry());
	    }
	}
	// Allows rendering to determine the object type (e.g. required for code fields)
	formValues.put(
		org.openmdx.base.accessor.cci.SystemAttributes.OBJECT_CLASS, 
		"org:opencrx:kernel:account1:LegalEntity"
	);
	form.updateObject(
		request.getParameterMap(),
		formValues,
		app,
		pm
	);
	memberForm.updateObject(
		request.getParameterMap(),
		formValues,
		app,
		pm
	);
	// Initialize formValues with member values
	if(!memberXri.isEmpty()) {
		// an existing member is being edited
		try {
			org.opencrx.kernel.account1.jmi1.Member member =
				(org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(memberXri));
			if ("editMember".equals(command)) {
				formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", member.getAccount().refGetPath());
				formValues.put("org:opencrx:kernel:account1:Member:memberRole", new ArrayList<Short>(member.getMemberRole()));
				formValues.put("org:opencrx:kernel:account1:Member:name", member.getName());
				formValues.put("org:opencrx:kernel:account1:Member:description", member.getDescription());
			}
		} catch (Exception e) {
			memberXri = "";
		}
	}
	if ("AddMember".equals(command)) {
		//System.out.println("entering mode to add new member");
		memberXri = "";
		formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formValues.put("org:opencrx:kernel:account1:Member:memberRole", null);
		formValues.put("org:opencrx:kernel:account1:Member:name", null);
		formValues.put("org:opencrx:kernel:account1:Member:description", null);
		// default: set role for employee
		formValues.put("org:opencrx:kernel:account1:Member:memberRole", Arrays.asList(new Short[]{(short)11}));
	}

	// Search
	List matchingLegalEntities = null;
	if(actionSearch) {
	    boolean hasQueryProperty = false;
	    org.opencrx.kernel.account1.cci2.LegalEntityQuery legalEntityQuery =
	        (org.opencrx.kernel.account1.cci2.LegalEntityQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.LegalEntity.class);
	    legalEntityQuery.orderByName().ascending();
	    String name = (String)formValues.get("org:opencrx:kernel:account1:AbstractGroup:name");
	    String aliasName = (String)formValues.get("org:opencrx:kernel:account1:Account:aliasName");
	    String phoneNumberBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull");
	    String phoneNumberMobile = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull");
	    String postalCityBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCity");
	    List postalStreetBusiness = (List)formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet");
	    String emailBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
		final String wildcard = ".*";
	    if(name != null) {
	        hasQueryProperty = true;
	        legalEntityQuery.thereExistsFullName().like("(?i)" + wildcard + name + wildcard);
	    } else {
	    	// is required field, so use wildcard if no search string is provided
	        hasQueryProperty = true;
	        legalEntityQuery.thereExistsFullName().like(wildcard);
	    }
	    if(aliasName != null) {
	        hasQueryProperty = true;
	        legalEntityQuery.thereExistsAliasName().like("(?i)" + wildcard + aliasName + wildcard);
	    }
	    String queryFilterClause = null;
	    List<String> stringParams = new ArrayList<String>();
	    int stringParamIndex = 0;
	    if(phoneNumberBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.PhoneNumberQuery query = (org.opencrx.kernel.account1.cci2.PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
	    	query.thereExistsPhoneNumberFull().like(wildcard + phoneNumberBusiness + wildcard);
	    	legalEntityQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(phoneNumberMobile != null) {
	    	org.opencrx.kernel.account1.cci2.PhoneNumberQuery query = (org.opencrx.kernel.account1.cci2.PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
	    	query.thereExistsPhoneNumberFull().like(wildcard + phoneNumberMobile + wildcard);
	    	legalEntityQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(postalCityBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.PostalAddressQuery query = (org.opencrx.kernel.account1.cci2.PostalAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PostalAddress.class);
	    	query.thereExistsPostalCity().like(wildcard + postalCityBusiness + wildcard);
	    	legalEntityQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(postalStreetBusiness != null) {
	        for(int i = 0; i < postalStreetBusiness.size(); i++) {
		        hasQueryProperty = true;
		        if(stringParamIndex > 0) { queryFilterClause += " AND "; } else { queryFilterClause = ""; }
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE adr.postal_street_" + i + " LIKE ?s" + stringParamIndex++ + ")";
		        stringParams.add(wildcard + postalStreetBusiness.get(i) + wildcard);
	        }
	    }
	    if(emailBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like("(?i)" + wildcard + emailBusiness + wildcard);
	    	legalEntityQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(queryFilterClause != null) {
	    	org.openmdx.base.query.Extension queryFilter = org.openmdx.base.persistence.cci.PersistenceHelper.newQueryExtension(legalEntityQuery);
		    queryFilter.setClause(queryFilterClause);
		    queryFilter.getStringParam().addAll(stringParams);
	    }
	    matchingLegalEntities = hasQueryProperty ?
	        accountSegment.getAccount(legalEntityQuery) :
	        null;
	}
	else if(actionCreate || actionSave) {
	    // LegalEntity
	    try {
				org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = null;
				try {
					if(actionCreate) {
						legalEntity = pm.newInstance(org.opencrx.kernel.account1.jmi1.LegalEntity.class);
						legalEntity.refInitialize(false, false);
					}
					else {
						legalEntity = (org.opencrx.kernel.account1.jmi1.LegalEntity)account;
					}
					String aliasName = (String)formValues.get("org:opencrx:kernel:account1:Account:aliasName");
					if(aliasName == null) {
						org.opencrx.kernel.account1.cci2.AccountQuery accountQuery =
						    (org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Account.class);
						accountQuery.thereExistsAliasName().greaterThanOrEqualTo("A00000000");
						accountQuery.thereExistsAliasName().lessThanOrEqualTo("A99999999");
						accountQuery.orderByAliasName().descending();
						List accounts = accountSegment.getAccount(accountQuery);
						if(accounts.isEmpty()) {
						    aliasName = "A00000000";
						}
						else {
						    String lastAliasName = ((org.opencrx.kernel.account1.jmi1.Account)accounts.iterator().next()).getAliasName();
						    String nextAccountNumber = "00000000" + (Integer.valueOf(lastAliasName.substring(1)).intValue() + 1);
						    aliasName = "A" + nextAccountNumber.substring(nextAccountNumber.length() - 8);
						}
						formValues.put(
					        "org:opencrx:kernel:account1:Account:aliasName",
					        aliasName
						);
					}
					pm.currentTransaction().begin();
					legalEntity.setName((String)formValues.get("org:opencrx:kernel:account1:AbstractGroup:name"));
					legalEntity.setAliasName(aliasName);
					if (formValues.get("org:opencrx:kernel:account1:LegalEntity:industry") != null) {
							legalEntity.setIndustry(((Number)formValues.get("org:opencrx:kernel:account1:LegalEntity:industry")).shortValue());
		    		}
					legalEntity.setDescription((String)formValues.get("org:opencrx:kernel:account1:Account:description"));
					if(actionCreate) {
						accountSegment.addAccount(
							false,
							org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
							legalEntity
						);
					}
					pm.currentTransaction().commit();
				} catch (Exception e) {
					new ServiceException(e).log();
					errorMsg = "ERROR - cannot create/save legal entity";
					Throwable err = e;
					while (err.getCause() != null) {
						err = err.getCause();
					}
					errorTitle += "<pre>" + err + "</pre>";
					try {
						pm.currentTransaction().rollback();
					} catch (Exception er) {}
				}
				objectXri = legalEntity.refMofId();
				account = legalEntity;
				// Update Addresses
				try {
					pm.currentTransaction().begin();
					// Phone Business
					DataBinding phoneBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)500;automaticParsing=(boolean)true");
					phoneBusinessDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull")
					);
					// Phone Mobile
					DataBinding phoneMobileDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)200;automaticParsing=(boolean)true");
					phoneMobileDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull",
						formValues.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull")
					);
					// Phone Other
					DataBinding phoneOtherDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)1800;automaticParsing=(boolean)true");
					phoneOtherDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull",
						formValues.get("org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull")
					);
					// Fax Business
					DataBinding faxBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)530;automaticParsing=(boolean)true");
					faxBusinessDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull",
						formValues.get("org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull")
					);
					// Mail Business
					DataBinding mailBusinessDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)500;[emailType=(short)1]");
					mailBusinessDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!emailAddress",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress")
					);
				    // Mail Other
				    DataBinding mailOtherDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)1800;[emailType=(short)1]");
				    mailOtherDataBinding.setValue(
				    		legalEntity,
				        "org:opencrx:kernel:account1:Account:address*Other!emailAddress",
				        formValues.get("org:opencrx:kernel:account1:Account:address*Other!emailAddress")
				    );
				    // Web Business
				    DataBinding webBusinessDataBinding = new CompositeObjectDataBinding("type=org:opencrx:kernel:account1:WebAddress;disabled=(boolean)false;[isMain=(boolean)true];usage=(short)500");
				    webBusinessDataBinding.setValue(
				    		legalEntity,
				        "org:opencrx:kernel:account1:LegalEntity:address!webUrl",
				        formValues.get("org:opencrx:kernel:account1:LegalEntity:address!webUrl")
				    );
				
					// Postal Business
					DataBinding postalBusinesDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)500?zeroAsNull=true");
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalAddressLine",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine")
					);
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalStreet",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")
					);
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalCity",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCity")
					);
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalState",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalState")
					);
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalCode",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCode")
					);
					postalBusinesDataBinding.setValue(
							legalEntity,
						"org:opencrx:kernel:account1:Account:address*Business!postalCountry",
						formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCountry")
					);
					pm.currentTransaction().commit();
				} catch (Exception e) {
					new ServiceException(e).log();
					errorMsg = "ERROR - cannot create/save legal entity";
					Throwable err = e;
					while (err.getCause() != null) {
						err = err.getCause();
					}
					errorTitle += "<pre>" + err + "</pre>";
					try {
						pm.currentTransaction().rollback();
					} catch (Exception er) {}
				}
				if(isAddMemberMode || !memberXri.isEmpty()) {
						// create/update data for member
						try {
							org.opencrx.kernel.account1.jmi1.Member member = null;
							org.opencrx.kernel.account1.jmi1.Account accountTo = null;
							try {
								accountTo = formValues.get("org:opencrx:kernel:account1:AccountAssignment:account") != null ?
									(org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
										formValues.get("org:opencrx:kernel:account1:AccountAssignment:account")
									) : null;
							} catch (Exception e) {}
							if (accountTo != null) {
								if(!memberXri.isEmpty()) {
									// update existing member
									member = (org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(memberXri));
								} else {
									// create new member
									try {
										pm.currentTransaction().begin();
										member = pm.newInstance(org.opencrx.kernel.account1.jmi1.Member.class);
										member.setValidFrom(new java.util.Date());
										member.setQuality((short)5);
										member.setName((String)formValues.get("org:opencrx:kernel:account1:Member:name"));
										if (member.getName() == null || member.getName().length() == 0) {
											member.setName(accountTo.getFullName());
										}
										member.setAccount(accountTo);
										account.addMember(
											org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
											member
										);
										pm.currentTransaction().commit();
									} catch (Exception e) {
										member = null;
										new ServiceException(e).log();
										errorMsg = "ERROR - cannot create/save member";
										Throwable err = e;
										while (err.getCause() != null) {
											err = err.getCause();
										}
										errorTitle += "<pre>" + err + "</pre>";
										try {
											pm.currentTransaction().rollback();
										} catch (Exception er) {}
									}
								}
								try {
									pm.currentTransaction().begin();
									member.setAccount(accountTo);
									member.setName((String)formValues.get("org:opencrx:kernel:account1:Member:name"));
									if (member.getName() == null || member.getName().length() == 0) {
											member.setName(accountTo.getFullName());
									}
									member.setDescription((String)formValues.get("org:opencrx:kernel:account1:Member:description"));
									pm.currentTransaction().commit();
								} catch (Exception e) {
									member = null;
									new ServiceException(e).log();
									try {
										pm.currentTransaction().rollback();
									} catch (Exception er) {}
								}
								if (member != null && formValues.get("org:opencrx:kernel:account1:Member:memberRole") != null) {
									// update member roles
									try {
										List<Short> memberRole = (List<Short>)formValues.get("org:opencrx:kernel:account1:Member:memberRole");
										pm.currentTransaction().begin();
										member.setMemberRole(memberRole);
										pm.currentTransaction().commit();
									} catch (Exception e) {
										member = null;
										new ServiceException(e).log();
										errorMsg = "ERROR - cannot create/save member";
										Throwable err = e;
										while (err.getCause() != null) {
											err = err.getCause();
										}
										errorTitle += "<pre>" + err + "</pre>";
										try {
											pm.currentTransaction().rollback();
										} catch (Exception er) {}
									}
								}
							}
						} catch (Exception e) {
							new ServiceException(e).log();
						}
					}
				memberXri = "";
				isAddMemberMode = false;
				formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
				formValues.put("org:opencrx:kernel:account1:Member:memberRole", null);
				formValues.put("org:opencrx:kernel:account1:Member:name", null);
				formValues.put("org:opencrx:kernel:account1:Member:description", null);
	    } catch (Exception e) {
	    		new ServiceException(e).log();
	    }
	}
	
	TransientObjectView view = new TransientObjectView(
		formValues,
		app,
		obj,
		pm
	);
	ViewPort p = ViewPortFactory.openPage(
		view,
		request,
		out
	);
	int tabIndex = 100;
%>
<br />
<form id="<%= formName %>" name="<%= formName %>" accept-charset="UTF-8" action="<%= servletPath %>">
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
	<input type="Hidden" id="Command" name="Command" value="" />
	<input type="Hidden" id="Para0" name="Para0" value="" />
	<input type="hidden" name="MEMBER_XRI" id="MEMBER_XRI" value="<%= memberXri %>" />
	<input type="checkbox" style="display:none;" name="isAddMemberMode" id="isAddMemberMode" <%= isAddMemberMode ? "checked" : "" %>/>
	
	<table cellspacing="8" class="tableLayout">
		<tr>
			<td class="cellObject">
				<div class="panel" id="panel<%= formName %>" style="display: block">
<%
					form.paint(
						p,
						null, // frame
						true // forEditing
					);
					p.flush();

					if(account != null) {
						// list existing members

%>
						<div class="fieldGroupName">&nbsp;</div>
						<table class="fieldGroup">
							<tr>
								<td class="label">
									<%= app.getLabel("org:opencrx:kernel:account1:Member") %><br>
									<input type="submit" name="AddMember" id="AddMember.Button" tabindex="<%= tabIndex++ %>" title="<%= app.getTexts().getNewText() + " " + app.getLabel("org:opencrx:kernel:account1:Member") %>" value="+" onclick="javascript:$('Command').value=this.name;$('isAddMemberMode').checked=true;" />
								</td>
								<td>
									<table class="gridTableFull">
										<tr class="gridTableHeaderFull">
											<td/>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:Member", "account", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:Member", "name", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:Member", "description", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:Member", "memberRole", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td/>
										</tr>
<%
										try {
												org.opencrx.kernel.account1.cci2.MemberQuery memberFilter = org.opencrx.kernel.utils.Utils.getAccountPackage(pm).createMemberQuery();
												memberFilter.forAllDisabled().isFalse();
												for(
														Iterator am = account.getMember(memberFilter).iterator();
														am.hasNext();
												) {
														org.opencrx.kernel.account1.jmi1.Member member =
															(org.opencrx.kernel.account1.jmi1.Member)am.next();
														try {
																org.opencrx.kernel.account1.jmi1.Account accountTo = member.getAccount();
							                  String accountHref = "";
							                  Action action = new ObjectReference(
							                		  accountTo,
							                      app
							                  ).getSelectObjectAction();
							                  accountHref = action.getEncodedHRef();
																//memberOfList.add(accountTo.getFullName());
																String rolesText = "";
																for(Iterator roles = member.getMemberRole().iterator(); roles.hasNext(); ) {
																		if (rolesText.length() > 0) {
																				rolesText += ";";
																		}
																		rolesText += codes.getLongTextByCode("memberRole", app.getCurrentLocaleAsIndex(), true).get(new Short(((Short)roles.next()).shortValue()));
																}
																//memberRoleList.add(rolesText);
%>
																<tr class="gridTableRow" <%= memberXri.compareTo(member.refMofId()) == 0 ? "style='background-color:#E4FF79;'" : "" %>>
																	<td class="addon">
																		<button type="submit" name="editMember" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getEditTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('Command').value=this.name;$('MEMBER_XRI').value='<%=member.refMofId() %>';" ><img src="images/edit.gif" /></button>
																	</td>
																	<td><a href="<%= accountHref %>" target="_blank"><%= app.getHtmlEncoder().encode(new ObjectReference(member.getAccount(), app).getTitle(), false) %></a></td>
																	<td><%= member.getName() != null ? app.getHtmlEncoder().encode(member.getName(), false) : "" %></td>
																	<td><%= member.getDescription() != null ?  app.getHtmlEncoder().encode(member.getDescription(), false) : "" %></td>
																	<td style="overflow:hidden;text-overflow:ellipsis;"><%= rolesText %></td>
																	<td class="addon">
																		<button type="submit" name="DeleteMember" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getDeleteTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('MEMBER_XRI').value='';$('Command').value=this.name;$('Para0').value='<%= member.refMofId() %>';" ><img src="images/deletesmall.gif" /></button>
																	</td>
																</tr>
<%
														}	catch(Exception em) {
																new ServiceException(em).log();
														}
												}
										} catch(Exception e) {
												new ServiceException(e).log();
										}
%>
									</table>
								</td>
								<td class="addon"/>
							</tr>
						</table>
<%
						if (isAddMemberMode || !memberXri.isEmpty()) {
								memberForm.paint(
									p,
									null, // frame
									true // forEditing
								);
								p.flush();
						}
					}
%>
					<div class="fieldGroupName">&nbsp;</div>				
				</div>
				<div id="WaitIndicator" style="float:left;width:50px;height:24px;" class="wait">&nbsp;</div>
<%
				if (errorMsg.length() > 0) {
%>
					<div title="<%= errorTitle.replace("\"", "'") %>"  style="background-color:red;color:white;border:1px solid black;padding:10px;font-weight:bold;margin-top:10px;">
						<%= errorMsg %>
					</div>
<%
				}
%>
				<div id="SubmitArea" style="float:left;display:none;">
				<input type="submit" name="Search" id="Search.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getSearchText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;" />
				<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= servletPath + "?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(accountSegment.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= app.getTexts().getSearchText() %>" />
<%
				if(account != null) {
%>
					<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= servletPathPrefix + "CreateLeadWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:contract1:Lead") %>" />
					<input type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= servletPathPrefix + "CreateContractWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:contract1:ContractRole", "contract", app.getCurrentLocaleAsIndex()) %>" />
					<input style='display:none;' type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= servletPathPrefix + "CreateActivityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %>" />
<%
					// prepare href to open new tab with activity segment and then call inline wizard to create new activity
					String providerName = obj.refGetPath().get(2);
					String segmentName = obj.refGetPath().get(4);
					org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
					String createActivityScript = "$('UserDialogWait').className='loading udwait';new Ajax.Updater('UserDialog', '" + servletPathPrefix + "CreateActivityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId + "&reportingAccount=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "', {evalScripts: true});";
					
					QuickAccessor createActivityAccessor = new QuickAccessor(
					    activitySegment.refGetPath(), // target
					    "New Activity", // name
					    "New Activity", // description
					    "Task.gif", // iconKey
					    Action.MACRO_TYPE_JAVASCRIPT, // actionType
					    createActivityScript,
					    Collections.<String>emptyList() // actionParams
						);
					Action newActivityAction =	createActivityAccessor.getAction(
					    accountSegment.refGetPath()
					  );
					String newActivityHref = newActivityAction.getEncodedHRef(requestId);
%>
					<a href="<%= newActivityHref %>" target="_blank"><button type="button" name="newActivity" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %>"><%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %></button></a>
					<input type="submit" name="OK" id="OK.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				}
				else {
				    if(matchingLegalEntities != null) {
%>
						<input type="submit" name="Create" id="Create.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:account1:LegalEntity") %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				    }
				}
%>
				<input type="submit" name="Cancel" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
				</div>
<%
				if(matchingLegalEntities != null) {
%>
					<div>&nbsp;</div>
					<table class="gridTableFull">
						<tr class="gridTableHeaderFull">
							<td/>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:LegalEntity", "name", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:LegalEntity", "aliasName", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= app.getLabel("org:opencrx:kernel:account1:PostalAddress") %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Mobile!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!emailAddress", app.getCurrentLocaleAsIndex()) %></td>
							<td class="addon"/>
						</tr>
<%
						int count = 0;
						for(Iterator i = matchingLegalEntities.iterator(); i.hasNext(); ) {
						    org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = ( org.opencrx.kernel.account1.jmi1.LegalEntity)i.next();
						    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(legalEntity);
%>
							<tr class="gridTableRowFull">
								<td><img style="cursor: pointer;" src="images/LegalEntity.gif" onclick="javascript:new Ajax.Updater('UserDialog', '<%= servletPath + "?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(legalEntity.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});"/></td>
								<td><%= legalEntity.getFullName() == null ? "" : legalEntity.getFullName() %></td>
								<td><%= legalEntity.getAliasName() == null ? "" : legalEntity.getAliasName() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.POSTAL_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.PHONE_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MOBILE], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MAIL_BUSINESS], app).getTitle() %></td>
								<td class="addon"/>
							</tr>
<%
							count++;
							if(count > 50) break;
						}
					}
%>
				</table>
			</td>
		</tr>
	</table>
</form>
<br>
<script language="javascript" type="text/javascript">
	Event.observe('<%= formName %>', 'submit', function(event) {
		$('<%= formName %>').request({
			onFailure: function() { },
			onSuccess: function(t) {
				$('UserDialog').update(t.responseText);
			}
		});
		Event.stop(event);
	});
	$('WaitIndicator').style.display='none';
	$('SubmitArea').style.display='block';
</script>
<%
p.close(false);
if(pm != null) {
	pm.close();
}
%>

﻿<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.openmdx.org/
 * Name:        $Id: CreateContactWizard.jsp,v 1.49 2012/07/08 13:30:31 wfro Exp $
 * Description: CreateContact wizard
 * Revision:    $Revision: 1.49 $
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
	final String formName = "ContactForm";
	final String membershipFormName = "MembershipForm";
	final String wizardName = "CreateContactWizard";
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
	String accountMembershipXri = request.getParameter("ACCOUNTMEMBERSHIP_XRI") == null ? "" : request.getParameter("ACCOUNTMEMBERSHIP_XRI");
	boolean isAddMembershipMode = request.getParameter("isAddMembershipMode") != null;

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
	<meta name="UNUSEDlabel" content="Create Contact">
	<meta name="UNUSEDtoolTip" content="Create Contact">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:account1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:account1:Contact">
	<meta name="order" content="org:opencrx:kernel:account1:Segment:createContact">
	<meta name="order" content="org:opencrx:kernel:account1:Contact:createContact">
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
	String providerName = obj.refGetPath().get(2);
	String segmentName = obj.refGetPath().get(4);
	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
	org.openmdx.ui1.jmi1.FormDefinition formDefinition = app.getUiFormDefinition(formName);
	org.openmdx.portal.servlet.control.FormControl form = new org.openmdx.portal.servlet.control.FormControl(
		formDefinition.refGetPath().getBase(),
		app.getCurrentLocaleAsString(),
		app.getCurrentLocaleAsIndex(),
		app.getUiContext(),
		formDefinition
	);
	// Membership form
	org.openmdx.ui1.jmi1.FormDefinition membershipFormDefinition = app.getUiFormDefinition(membershipFormName);
	org.openmdx.portal.servlet.control.FormControl membershipForm = new org.openmdx.portal.servlet.control.FormControl(
		membershipFormDefinition.refGetPath().getBase(),
		app.getCurrentLocaleAsString(),
		app.getCurrentLocaleAsIndex(),
		app.getUiContext(),
		membershipFormDefinition
	);

	Map formValues = new HashMap();
	// Initialize formValues with account values
	if(account != null && account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
	    org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
	    formValues.put("org:opencrx:kernel:account1:Contact:salutationCode", contact.getSalutationCode());
	    formValues.put("org:opencrx:kernel:account1:Contact:salutation", contact.getSalutation());
	    formValues.put("org:opencrx:kernel:account1:Contact:firstName", contact.getFirstName());
	    formValues.put("org:opencrx:kernel:account1:Contact:middleName", contact.getMiddleName());
	    formValues.put("org:opencrx:kernel:account1:Contact:lastName", contact.getLastName());
	    formValues.put("org:opencrx:kernel:account1:Account:aliasName", contact.getAliasName());
	    formValues.put("org:opencrx:kernel:account1:Contact:jobTitle", contact.getJobTitle());
	    formValues.put("org:opencrx:kernel:account1:Contact:jobRole", contact.getJobRole());
	    formValues.put("org:opencrx:kernel:account1:Contact:organization", contact.getOrganization());
	    formValues.put("org:opencrx:kernel:account1:Contact:department", contact.getDepartment());
	    formValues.put("org:opencrx:kernel:account1:Contact:doNotPhone", contact.isDoNotPhone());
	    formValues.put("org:opencrx:kernel:account1:Contact:birthdate", contact.getBirthdate());
	    formValues.put("org:opencrx:kernel:account1:Account:description", contact.getDescription());
	    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(account);
	    if(addresses[Accounts.PHONE_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.MOBILE] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.PHONE_HOME] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Contact:address!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_HOME]).getPhoneNumberFull());
	    }
	    if(addresses[Accounts.MAIL_BUSINESS] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Account:address*Business!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress());
	    }
	    if(addresses[Accounts.MAIL_HOME] != null) {
	    	formValues.put("org:opencrx:kernel:account1:Contact:address!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_HOME]).getEmailAddress());
	    }
	    if(addresses[Accounts.POSTAL_HOME] != null) {
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalAddressLine", new ArrayList(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalAddressLine()));
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalStreet", new ArrayList(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalStreet()));
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalCity", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCity());
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalState", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalState());
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCode());
		    formValues.put("org:opencrx:kernel:account1:Contact:address!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCountry());
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
		"org:opencrx:kernel:account1:Contact"
	);
	form.updateObject(
		request.getParameterMap(),
		formValues,
		app,
		pm
	);
	membershipForm.updateObject(
		request.getParameterMap(),
		formValues,
		app,
		pm
	);
	// Initialize formValues with member values
	if(!accountMembershipXri.isEmpty()) {
		// an existing membership is being edited
		try {
			org.opencrx.kernel.account1.jmi1.AccountMembership accountMembership =
				(org.opencrx.kernel.account1.jmi1.AccountMembership)pm.getObjectById(new Path(accountMembershipXri));
			formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", accountMembership.getAccountFrom().refGetPath()); // must not change parent account!
			if ("editMembership".equals(command)) {
				formValues.put("org:opencrx:kernel:account1:Member:memberRole", new ArrayList<Short>(accountMembership.getMember().getMemberRole()));
			}
		} catch (Exception e) {
			accountMembershipXri = "";
		}
	}
	if ("AddMembership".equals(command)) {
		//System.out.println("entering mode to add new membership");
		accountMembershipXri = "";
		formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formValues.put("org:opencrx:kernel:account1:Member:memberRole", null);
		// default: set role for employee
		formValues.put("org:opencrx:kernel:account1:Member:memberRole", Arrays.asList(new Short[]{(short)11}));
	}

	// Search
	List matchingContacts = null;
	if(actionSearch) {
	    boolean hasQueryProperty = false;
	    org.opencrx.kernel.account1.cci2.ContactQuery contactQuery =
	        (org.opencrx.kernel.account1.cci2.ContactQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Contact.class);
		contactQuery.orderByFullName().ascending();
	    String firstName = (String)formValues.get("org:opencrx:kernel:account1:Contact:firstName");
	    String lastName = (String)formValues.get("org:opencrx:kernel:account1:Contact:lastName");
	    String aliasName = (String)formValues.get("org:opencrx:kernel:account1:Account:aliasName");
	    String phoneNumberMobile = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull");
	    String phoneNumberHome = (String)formValues.get("org:opencrx:kernel:account1:Contact:address!phoneNumberFull");
	    String phoneNumberBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull");
	    String postalCityHome = (String)formValues.get("org:opencrx:kernel:account1:Contact:address!postalCity");
	    String postalCityBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCity");
	    List postalStreetHome = (List)formValues.get("org:opencrx:kernel:account1:Contact:address!postalStreet");
	    List postalStreetBusiness = (List)formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet");
	    String emailHome = (String)formValues.get("org:opencrx:kernel:account1:Contact:address!emailAddress");
	    String emailBusiness = (String)formValues.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
      	final String wildcard = ".*";
	    if(firstName != null) {
	        hasQueryProperty = true;
	        contactQuery.thereExistsFirstName().like("(?i)" + wildcard + firstName + wildcard);
	    }
	    if(lastName != null) {
	        hasQueryProperty = true;
	        contactQuery.thereExistsLastName().like("(?i)" + wildcard + lastName + wildcard);
	    } else {
	    	// is required field, so use wildcard if no search string is provided
	        hasQueryProperty = true;
	        contactQuery.thereExistsLastName().like(wildcard);
	    }
	    if(aliasName != null) {
	        hasQueryProperty = true;
	        contactQuery.thereExistsAliasName().like("(?i)" + wildcard + aliasName + wildcard);
	    }
	    String queryFilterClause = null;
	    List<String> stringParams = new ArrayList<String>();
	    int stringParamIndex = 0;
	    if(phoneNumberMobile != null) {
	    	org.opencrx.kernel.account1.cci2.PhoneNumberQuery query = (org.opencrx.kernel.account1.cci2.PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
	    	query.thereExistsPhoneNumberFull().like(wildcard + phoneNumberMobile + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(phoneNumberHome != null) {
	    	org.opencrx.kernel.account1.cci2.PhoneNumberQuery query = (org.opencrx.kernel.account1.cci2.PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
	    	query.thereExistsPhoneNumberFull().like(wildcard + phoneNumberHome + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(phoneNumberBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.PhoneNumberQuery query = (org.opencrx.kernel.account1.cci2.PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
	    	query.thereExistsPhoneNumberFull().like(wildcard + phoneNumberBusiness + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(postalCityHome != null) {
	    	org.opencrx.kernel.account1.cci2.PostalAddressQuery query = (org.opencrx.kernel.account1.cci2.PostalAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PostalAddress.class);
	    	query.thereExistsPostalCity().like(wildcard + postalCityHome + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(postalCityBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.PostalAddressQuery query = (org.opencrx.kernel.account1.cci2.PostalAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PostalAddress.class);
	    	query.thereExistsPostalCity().like(wildcard + postalCityBusiness + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(postalStreetHome != null) {
	        for(int i = 0; i < postalStreetHome.size(); i++) {
		        hasQueryProperty = true;
		        if(stringParamIndex > 0) { queryFilterClause += " AND "; } else { queryFilterClause = ""; }
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE adr.postal_street_" + i + " LIKE ?s" + stringParamIndex++ + ")";
		        stringParams.add(wildcard + postalStreetHome.get(i) + wildcard);
	        }
	    }
	    if(postalStreetBusiness != null) {
	        for(int i = 0; i < postalStreetBusiness.size(); i++) {
		        hasQueryProperty = true;
		        if(stringParamIndex > 0) { queryFilterClause += " AND "; } else { queryFilterClause = ""; }
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE adr.postal_street_" + i + " LIKE ?s" + stringParamIndex++ + ")";
		        stringParams.add(wildcard + postalStreetBusiness.get(i) + wildcard);
	        }
	    }
	    if(emailHome != null) {
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like(wildcard + emailHome + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(emailBusiness != null) {
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like(wildcard + emailBusiness + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(queryFilterClause != null) {
	    	org.openmdx.base.query.Extension queryFilter = org.openmdx.base.persistence.cci.PersistenceHelper.newQueryExtension(contactQuery);
		    queryFilter.setClause(queryFilterClause);
		    queryFilter.getStringParam().addAll(stringParams);
	    }
	    matchingContacts = hasQueryProperty ?
	        accountSegment.getAccount(contactQuery) :
	        null;
	}
	else if(actionCreate || actionSave) {
	    // Contact
	    org.opencrx.kernel.account1.jmi1.Contact contact = null;
		try {
		    if(actionCreate) {
			    contact = pm.newInstance(org.opencrx.kernel.account1.jmi1.Contact.class);
		    	contact.refInitialize(false, false);
		    }
		    else {
		        contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
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
		    contact.setSalutationCode(((Number)formValues.get("org:opencrx:kernel:account1:Contact:salutationCode")).shortValue());
		    contact.setSalutation((String)formValues.get("org:opencrx:kernel:account1:Contact:salutation"));
		    contact.setFirstName((String)formValues.get("org:opencrx:kernel:account1:Contact:firstName"));
		    contact.setMiddleName((String)formValues.get("org:opencrx:kernel:account1:Contact:middleName"));
		    contact.setLastName((String)formValues.get("org:opencrx:kernel:account1:Contact:lastName"));
		    contact.setAliasName(aliasName);
		    contact.setJobTitle((String)formValues.get("org:opencrx:kernel:account1:Contact:jobTitle"));
		    contact.setJobRole((String)formValues.get("org:opencrx:kernel:account1:Contact:jobRole"));
		    contact.setOrganization((String)formValues.get("org:opencrx:kernel:account1:Contact:organization"));
		    contact.setDepartment((String)formValues.get("org:opencrx:kernel:account1:Contact:department"));
		    contact.setDoNotPhone((Boolean)formValues.get("org:opencrx:kernel:account1:Contact:doNotPhone"));
		    contact.setBirthdate((Date)formValues.get("org:opencrx:kernel:account1:Contact:birthdate"));
		    contact.setDescription((String)formValues.get("org:opencrx:kernel:account1:Account:description"));
		    if(actionCreate) {
			    accountSegment.addAccount(
			        false,
			        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
			        contact
			    );
		    }
		    pm.currentTransaction().commit();
		} catch (Exception e) {
			new ServiceException(e).log();
			errorMsg = "ERROR - cannot create/save contact";
			Throwable err = e;
			while (err.getCause() != null) {
				err = err.getCause();
			}
			errorTitle += "<pre>" + err + "</pre>";
			try {
				pm.currentTransaction().rollback();
			} catch (Exception er) {}
		}
		objectXri = contact.refMofId();
		account = contact;
	    // Update Addresses
	    try {
		    pm.currentTransaction().begin();
		    // Phone Business
		    DataBinding phoneBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)500;automaticParsing=(boolean)true");
		    phoneBusinessDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull")
		    );
		    // Phone Mobile
		    DataBinding phoneMobileDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)200;automaticParsing=(boolean)true");
		    phoneMobileDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull")
		    );
		    // Phone Home
		    DataBinding phoneHomeDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)400;automaticParsing=(boolean)true");
		    phoneHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!phoneNumberFull",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!phoneNumberFull")
		    );
		    // Mail Business
		    DataBinding mailBusinessDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)500;[emailType=(short)1]");
		    mailBusinessDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!emailAddress",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress")
		    );
		    // Mail Home
		    DataBinding mailHomeDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)400;[emailType=(short)1]");
		    mailHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!emailAddress",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!emailAddress")
		    );
		    // Postal Home
		    DataBinding postalHomeDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)400?zeroAsNull=true");
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalAddressLine",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!postalAddressLine")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalStreet",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!postalStreet")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCity",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!postalCity")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
			        "org:opencrx:kernel:account1:Contact:address!postalState",
			        formValues.get("org:opencrx:kernel:account1:Contact:address!postalState")
		    );
		    postalHomeDataBinding.setValue(
		    		contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCode",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!postalCode")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCountry",
		        formValues.get("org:opencrx:kernel:account1:Contact:address!postalCountry")
		    );
		    // Postal Business
		    DataBinding postalBusinesDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)500?zeroAsNull=true");
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalAddressLine",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalStreet",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCity",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCity")
		    );
		    postalBusinesDataBinding.setValue(
		    		contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalState",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalState")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCode",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCode")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCountry",
		        formValues.get("org:opencrx:kernel:account1:Account:address*Business!postalCountry")
		    );
		    pm.currentTransaction().commit();
		} catch (Exception e) {
			new ServiceException(e).log();
			errorMsg = "ERROR - cannot create/save contact";
			Throwable err = e;
			while (err.getCause() != null) {
				err = err.getCause();
			}
			errorTitle += "<pre>" + err + "</pre>";
			try {
				pm.currentTransaction().rollback();
			} catch (Exception er) {}
		}
	   	if(wizardExtension != null) {
	   		wizardExtension.setSecurityForNewContact(contact);		   		
	   	}
	    if(isAddMembershipMode || !accountMembershipXri.isEmpty()) {
			// create/update data for membership
			try {
				org.opencrx.kernel.account1.jmi1.Account parentAccount = null;
				org.opencrx.kernel.account1.jmi1.Member member = null;
				if(!accountMembershipXri.isEmpty()) {
					// update existing membership
					// get parent account
					org.opencrx.kernel.account1.jmi1.AccountMembership accountMembership =
						(org.opencrx.kernel.account1.jmi1.AccountMembership)pm.getObjectById(new Path(accountMembershipXri));
					parentAccount = accountMembership.getAccountFrom();
					member = accountMembership.getMember();
				} else {
					// create new membership
					// get parent account
					try {
						parentAccount = formValues.get("org:opencrx:kernel:account1:AccountAssignment:account") != null ?
							(org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
								formValues.get("org:opencrx:kernel:account1:AccountAssignment:account")
							) : null;
					} catch (Exception e) {}
					if (parentAccount != null) {
						// create new member
						try {
							pm.currentTransaction().begin();
							member = pm.newInstance(org.opencrx.kernel.account1.jmi1.Member.class);
							member.setValidFrom(new java.util.Date());
							member.setQuality((short)5);
							member.setName(account.getFullName());
							member.setAccount(account);
							parentAccount.addMember(
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
			} catch (Exception e) {
				new ServiceException(e).log();
			}
		}
	    accountMembershipXri = "";
	    isAddMembershipMode = false;
	    formValues.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
	    formValues.put("org:opencrx:kernel:account1:Member:memberRole", null);
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
<form id="<%= formName %>" name="<%= formName %>" accept-charset="UTF-8" action="<%= servletPath %>" style="padding-top:8px;">
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
	<input type="Hidden" id="Command" name="Command" value="" />
	<input type="Hidden" id="Para0" name="Para0" value="" />
	<input type="hidden" name="ACCOUNTMEMBERSHIP_XRI" id="ACCOUNTMEMBERSHIP_XRI" value="<%= accountMembershipXri %>" />
	<input type="checkbox" style="display:none;" name="isAddMembershipMode" id="isAddMembershipMode" <%= isAddMembershipMode ? "checked" : "" %>/>
	
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
						// list existing memberships

%>
						<div class="fieldGroupName">&nbsp;</div>
						<table class="fieldGroup">
							<tr>
								<td class="label">
									<%= app.getLabel("org:opencrx:kernel:account1:AccountMembership") %><br>
									<input type="submit" name="AddMembership" id="AddMembership.Button" tabindex="<%= tabIndex++ %>" title="<%= app.getTexts().getNewText() + " " + app.getLabel("org:opencrx:kernel:account1:AccountMembership") %>" value="+" onclick="javascript:$('Command').value=this.name;$('isAddMembershipMode').checked=true;" />
								</td>
								<td>
									<table class="gridTableFull">
										<tr class="gridTableHeaderFull">
											<td/>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:AccountMembership", "accountFrom", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:AccountMembership", "accountTo", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td><strong><%= view.getFieldLabel("org:opencrx:kernel:account1:AccountMembership", "memberRole", app.getCurrentLocaleAsIndex()) %></strong></td>
											<td/>
										</tr>
<%
										try {
												org.opencrx.kernel.account1.cci2.AccountMembershipQuery accountMembershipFilter = org.opencrx.kernel.utils.Utils.getAccountPackage(pm).createAccountMembershipQuery();
												accountMembershipFilter.distance().equalTo(
														new Short((short)-1) // only direct/immediate memberships are of interest
													);
												accountMembershipFilter.orderByCreatedAt().ascending();
												accountMembershipFilter.forAllDisabled().isFalse();
												for(
														Iterator am = account.getAccountMembership(accountMembershipFilter).iterator();
														am.hasNext();
												) {
														org.opencrx.kernel.account1.jmi1.AccountMembership accountMembership =
															(org.opencrx.kernel.account1.jmi1.AccountMembership)am.next();
														try {
																org.opencrx.kernel.account1.jmi1.Member member = accountMembership.getMember();
																org.opencrx.kernel.account1.jmi1.Account accountFrom = accountMembership.getAccountFrom();
							                  String accountHref = "";
							                  Action action = new ObjectReference(
							                		  accountFrom,
							                      app
							                  ).getSelectObjectAction();
							                  accountHref = action.getEncodedHRef();
																//memberOfList.add(accountFrom.getFullName());
																String rolesText = "";
																for(Iterator roles = accountMembership.getMemberRole().iterator(); roles.hasNext(); ) {
																		if (rolesText.length() > 0) {
																				rolesText += ";";
																		}
																		rolesText += codes.getLongTextByCode("memberRole", app.getCurrentLocaleAsIndex(), true).get(new Short(((Short)roles.next()).shortValue()));
																}
																//memberRoleList.add(rolesText);
%>
																<tr class="gridTableRow" <%= accountMembershipXri.compareTo(accountMembership.refMofId()) == 0 ? "style='background-color:#E4FF79;'" : "" %>>
																	<td class="addon">
																		<button type="submit" name="editMembership" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getEditTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('Command').value=this.name;$('ACCOUNTMEMBERSHIP_XRI').value='<%=accountMembership.refMofId() %>';" ><img src="images/edit.gif" /></button>
																	</td>
																	<td><a href="<%= accountHref %>" target="_blank"><%= app.getHtmlEncoder().encode(new ObjectReference(accountMembership.getAccountFrom(), app).getTitle(), false) %></a></td>
																	<td><%= app.getHtmlEncoder().encode(new ObjectReference(accountMembership.getAccountTo(), app).getTitle(), false) %></td>
																	<td style="overflow:hidden;text-overflow:ellipsis;"><%= rolesText %></td>
																	<td class="addon">
																		<button type="submit" name="DeleteMember" tabindex="<%= tabIndex++ %>" value="&mdash;" title="<%= app.getTexts().getDeleteTitle() %>" style="border:0;background:transparent;font-size:10px;font-weight:bold;cursor:pointer;" onclick="javascript:$('ACCOUNTMEMBERSHIP_XRI').value='';$('Command').value=this.name;$('Para0').value='<%= member.refMofId() %>';" ><img src="images/deletesmall.gif" /></button>
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
						if (isAddMembershipMode || !accountMembershipXri.isEmpty()) {
								membershipForm.paint(
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
					<input style='display:none;' type="button" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; new Ajax.Updater('UserDialog', '<%= servletPathPrefix + "CreateActivityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId + "&reportingAccount=" + java.net.URLEncoder.encode(account.refMofId(), "UTF-8") %>', {evalScripts: true});" value="<%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:activity1:ActivityFollowUp", "activity", app.getCurrentLocaleAsIndex()) %>" />
<%
					// prepare href to open new tab with activity segment and then call inline wizard to create new activity
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
<%					
					
					// prepare href to open new tab with account segment and then call inline wizard to create new legal entity
					String createLegalEntityScript = "$('UserDialogWait').className='loading udwait';new Ajax.Updater('UserDialog', '" + servletPathPrefix + "CreateLegalEntityWizard.jsp?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(accountSegment.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId + "', {evalScripts: true});";
					
					QuickAccessor createLegalEntityAccessor = new QuickAccessor(
					    accountSegment.refGetPath(), // target
					    "New LegalEntity", // name
					    "New LegalEntity", // description
					    "LegalEntity.gif", // iconKey
					    Action.MACRO_TYPE_JAVASCRIPT, // actionType
					    createLegalEntityScript,
					    Collections.<String>emptyList() // actionParams
						);
					Action newLegalEntityAction =	createLegalEntityAccessor.getAction(
					    accountSegment.refGetPath()
					  );
					String newLegalEntityHref = newLegalEntityAction.getEncodedHRef(requestId);
%>
					<a href="<%= newLegalEntityHref %>" target="_blank"><button type="button" name="newLegalEntity" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:account1:LegalEntity") %>"><%= app.getTexts().getNewText() %> <%= app.getLabel("org:opencrx:kernel:account1:LegalEntity") %></button></a>
					<input type="submit" name="OK" id="OK.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				}
				else {
				    if(matchingContacts != null) {
%>
						<input type="submit" name="Create" id="Create.Button" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getNewText() %> <%= view.getFieldLabel("org:opencrx:kernel:activity1:Resource", "contact", app.getCurrentLocaleAsIndex()) %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
<%
				    }
				}
%>
				<input type="submit" name="Cancel" tabindex="<%= tabIndex++ %>" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:$('WaitIndicator').style.display='block';$('SubmitArea').style.display='none'; $('Command').value=this.name;"/>
				</div>
<%
				if(matchingContacts != null) {
%>
					<div>&nbsp;</div>
					<table class="gridTableFull">
						<tr class="gridTableHeaderFull">
							<td/>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "fullName", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "aliasName", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= app.getLabel("org:opencrx:kernel:account1:PostalAddress") %></td>
							<td><%= app.getLabel("org:opencrx:kernel:account1:PostalAddress") %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Mobile!phoneNumberFull", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Account", "address*Business!emailAddress", app.getCurrentLocaleAsIndex()) %></td>
							<td><%= view.getFieldLabel("org:opencrx:kernel:account1:Contact", "organization", app.getCurrentLocaleAsIndex()) %></td>
							<td class="addon"/>
						</tr>
<%
						int count = 0;
						for(Iterator i = matchingContacts.iterator(); i.hasNext(); ) {
						    org.opencrx.kernel.account1.jmi1.Contact contact = ( org.opencrx.kernel.account1.jmi1.Contact)i.next();
						    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(contact);
%>
							<tr class="gridTableRowFull">
								<td><img style="cursor: pointer;" src="images/Contact.gif" onclick="javascript:new Ajax.Updater('UserDialog', '<%= servletPath + "?" + Action.PARAMETER_OBJECTXRI + "=" + java.net.URLEncoder.encode(contact.refMofId(), "UTF-8") + "&" + Action.PARAMETER_REQUEST_ID + "=" + requestId %>', {evalScripts: true});"/></td>
								<td><%= contact.getFullName() == null ? "" : contact.getFullName() %></td>
								<td><%= contact.getAliasName() == null ? "" : contact.getAliasName() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.POSTAL_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.POSTAL_HOME], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.PHONE_BUSINESS], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MOBILE], app).getTitle() %></td>
								<td><%= new org.openmdx.portal.servlet.ObjectReference(addresses[Accounts.MAIL_BUSINESS], app).getTitle() %></td>
								<td><%= contact.getOrganization() == null ? "" : contact.getOrganization() %></td>
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

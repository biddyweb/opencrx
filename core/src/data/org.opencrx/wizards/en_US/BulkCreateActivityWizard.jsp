<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: BulkCreateActivityWizard.jsp,v 1.23 2012/07/08 13:30:03 wfro Exp $
 * Description: BulkCreateActivityWizard.jsp
 * Revision:    $Revision: 1.23 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/07/08 13:30:03 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2011-2012, CRIXP Corp., Switzerland
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
%>
<%@ taglib uri="http://www.openmdx.org/tags/openmdx-portal" prefix="portal" %>
<%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.opencrx.kernel.portal.*,
org.opencrx.kernel.backend.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*
" %>
<%!

	public static List<String> splitEqually(String text, int size) {
	    // Give the list the right capacity to start with. You could use an array instead if you wanted.
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
	
	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}

	public static void updateEMailRecipient(
		org.opencrx.kernel.activity1.jmi1.EMail email,
		org.opencrx.kernel.account1.jmi1.EMailAddress emailAddress,
		Activities.PartyType partyType
	) throws ServiceException {
		javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(email);
		org.opencrx.kernel.activity1.cci2.EMailRecipientQuery query = 
			(org.opencrx.kernel.activity1.cci2.EMailRecipientQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.EMailRecipient.class);
		query.partyType().equalTo(partyType.getValue());
		query.orderByCreatedAt().descending();
		List<org.opencrx.kernel.activity1.jmi1.EMailRecipient> emailRecipients = email.getEmailRecipient(query);
		org.opencrx.kernel.activity1.jmi1.EMailRecipient emailRecipient = null;
		if(emailRecipients.isEmpty()) {
			emailRecipient = pm.newInstance(org.opencrx.kernel.activity1.jmi1.EMailRecipient.class);
			email.addEmailRecipient(
				Base.getInstance().getUidAsString(), 
				emailRecipient
			);
		} else {
			emailRecipient = emailRecipients.iterator().next();
		}		
		emailRecipient.setEmailHint(emailAddress.getEmailAddress());
		emailRecipient.setParty(emailAddress);
		emailRecipient.setPartyType(partyType.getValue());
		emailRecipient.setPartyStatus(Activities.PartyStatus.ACCEPTED.getValue());		
	}

%>
<%
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
	org.openmdx.base.persistence.cci.UserObjects.setBulkLoad(pm, true);
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
	org.opencrx.kernel.portal.wizard.BulkCreateActivityWizardExtension extension = 
		(org.opencrx.kernel.portal.wizard.BulkCreateActivityWizardExtension)app.getPortalExtension().getExtension(org.opencrx.kernel.portal.wizard.BulkCreateActivityWizardExtension.class.getName());
	final String FORM_NAME = "BulkCreateActivityForm";
	final String FORM_NAME_CREATOR = FORM_NAME + "Creator";
	final String FORM_NAME_PLACEHOLDERS = FORM_NAME + "PlaceHolders";
	final String FORM_NAME_ACTIVITY = FORM_NAME + "Activity";
	final String FORM_NAME_EMAIL = FORM_NAME + "EMail";
	final String FORM_NAME_EMAILTO = FORM_NAME + "EMailTo";
	final String FORM_NAME_RECIPIENT = FORM_NAME + "Recipient";
	final String wizardName = "BulkCreateActivityWizard.jsp";
	final int NUM_OF_TEST_ACTIVITIES = 3;

	try {
    	// Get Parameters
		String command = request.getParameter("Command");
		if(command == null) command = "";
		boolean actionSave = "Save".equals(command);
		boolean actionCreate = "Create".equals(command);
		boolean actionCreateTest = "CreateTest".equals(command);
		boolean actionCreateTestConfirmed = "CreateTestCONF".equals(command);
		boolean actionCancel = "Cancel".equals(command);
		boolean actionReload = "Reload".equals(command);
		boolean actionRefresh = "Refresh".equals(command);
		
		org.opencrx.kernel.account1.jmi1.AccountFilterGlobal accountFilter = null;
		org.opencrx.kernel.account1.jmi1.Group group = null;
		org.opencrx.kernel.account1.jmi1.AddressFilterGlobal addressFilter = null;
		org.opencrx.kernel.activity1.jmi1.AddressGroup addressGroup = null;

		boolean sourceOk = false;
		if (obj instanceof org.opencrx.kernel.account1.jmi1.AccountFilterGlobal) {
			accountFilter = (org.opencrx.kernel.account1.jmi1.AccountFilterGlobal)obj;
			sourceOk = true;
		} else if (obj instanceof org.opencrx.kernel.account1.jmi1.Group) {
			group = (org.opencrx.kernel.account1.jmi1.Group)obj;
			sourceOk = true;
		} else if (obj instanceof org.opencrx.kernel.account1.jmi1.AddressFilterGlobal) {
			addressFilter = (org.opencrx.kernel.account1.jmi1.AddressFilterGlobal)obj;
			sourceOk = true;
		} else if (obj instanceof org.opencrx.kernel.activity1.jmi1.AddressGroup) {
			addressGroup = (org.opencrx.kernel.activity1.jmi1.AddressGroup)obj;
			sourceOk = true;
		}
		
		if(actionCancel || !sourceOk) {
			session.setAttribute(wizardName, null);
			Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
			response.sendRedirect(
				request.getContextPath() + "/" + nextAction.getEncodedHRef()
			);
			return;
		}

		String providerName = obj.refGetPath().get(2);
		String segmentName = obj.refGetPath().get(4);
		org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
%>
      <!--
      	<meta name="label" content="Bulk - Create Activity">
      	<meta name="toolTip" content="Bulk - Create Activity">
      	<meta name="targetType" content="_inplace">
      	<meta name="forClass" content="org:opencrx:kernel:account1:AccountFilterGlobal">
      	<meta name="forClass" content="org:opencrx:kernel:account1:Group">
      	<meta name="forClass" content="org:opencrx:kernel:account1:AddressFilterGlobal">
      	<meta name="forClass" content="org:opencrx:kernel:activity1:AddressGroup">
      	<meta name="order" content="5555">
      -->
<%
		org.openmdx.ui1.jmi1.FormDefinition formDefCreator = app.getUiFormDefinition(FORM_NAME_CREATOR);
		org.openmdx.portal.servlet.control.FormControl formCreator = new org.openmdx.portal.servlet.control.FormControl(
			formDefCreator.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefCreator
		);
		org.openmdx.ui1.jmi1.FormDefinition formDefPlaceHolders = app.getUiFormDefinition(FORM_NAME_PLACEHOLDERS);
		org.openmdx.portal.servlet.control.FormControl formPlaceHolders = new org.openmdx.portal.servlet.control.FormControl(
			formDefPlaceHolders.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefPlaceHolders
		);
		org.openmdx.ui1.jmi1.FormDefinition formDefActivity = app.getUiFormDefinition(FORM_NAME_ACTIVITY);
		org.openmdx.portal.servlet.control.FormControl formActivity = new org.openmdx.portal.servlet.control.FormControl(
			formDefActivity.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefActivity
		);
		org.openmdx.ui1.jmi1.FormDefinition formDefEMail = app.getUiFormDefinition(FORM_NAME_EMAIL);
		org.openmdx.portal.servlet.control.FormControl formEMail = new org.openmdx.portal.servlet.control.FormControl(
			formDefEMail.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefEMail
		);
		org.openmdx.ui1.jmi1.FormDefinition formDefEMailTo = app.getUiFormDefinition(FORM_NAME_EMAILTO);
		org.openmdx.portal.servlet.control.FormControl formEMailTo = new org.openmdx.portal.servlet.control.FormControl(
			formDefEMailTo.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefEMailTo
		);
		org.openmdx.ui1.jmi1.FormDefinition formDefRecipient = app.getUiFormDefinition(FORM_NAME_RECIPIENT);
		org.openmdx.portal.servlet.control.FormControl formRecipient = new org.openmdx.portal.servlet.control.FormControl(
			formDefRecipient.refGetPath().getBase(),
			app.getCurrentLocaleAsString(),
			app.getCurrentLocaleAsIndex(),
			app.getUiContext(),
			formDefRecipient
		);
		Map formValues = new HashMap();
		formCreator.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);
		formPlaceHolders.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);
		formActivity.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);
		formEMail.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);		
		formEMailTo.updateObject(
    		request.getParameterMap(),
    		formValues,
    		app,
    		pm
    	);		
		formRecipient.updateObject(
	    		request.getParameterMap(),
	    		formValues,
	    		app,
	    		pm
	    	);		
		boolean isFirstCall = request.getParameter("isFirstCall") == null;
		// Initialize formValues on first call
		if(isFirstCall) {
			// Nothing todo
		}
		org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = null;
		if(formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") != null) {
		    activityCreator =
	           	(org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
	           		formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator")
	           	);
		}
		
	    Number locale = (Number)formValues.get("org:opencrx:kernel:generic:LocalizedField:locale");

		if(actionReload) {
			if(activityCreator != null) {
				StringPropertyDataBinding dataBinding = new StringPropertyDataBinding();
				formValues.put(
					"org:opencrx:kernel:activity1:Activity:name",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!name"
					)					
				);
				formValues.put(
					"org:opencrx:kernel:activity1:Activity:description",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!description"
					)					
				);
				formValues.put(
					"org:opencrx:kernel:activity1:Activity:detailedDescription",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!detailedDescription"
					)					
				);
				formValues.put(
					"org:opencrx:kernel:activity1:EMail:messageSubject",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!messageSubject"
					)					
				);
				// get messageBodyPieces
				String tempMessageBody = "";
				int idx = 0;
				try {
					while (dataBinding.getValue(activityCreator, ":BulkCreateActivityWizardSettings." + locale + "!messageBody" + idx) != null) {
						tempMessageBody += dataBinding.getValue(activityCreator, ":BulkCreateActivityWizardSettings." + locale + "!messageBody" + idx);
						idx++;
					}
				} catch (Exception e) {}
				formValues.put(
					"org:opencrx:kernel:activity1:EMail:messageBody",
					tempMessageBody					
				);
				formValues.put(
					"org:opencrx:kernel:base:Note:text",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!placeHolders"
					)
				);
				formValues.put(
					"org:opencrx:kernel:activity1:EMail:sender",
					dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!sender"
					)
				);
				List<Short> usagesList = new ArrayList<Short>();
				for(int i = 0; i < 10; i++) {
					Object usage = dataBinding.getValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!usage." + i
					);
					if(usage != null) {
						usagesList.add(Short.valueOf((String)usage));
					}
				}
				formValues.put(
					"org:opencrx:kernel:address1:Addressable:usage",
					usagesList
				);
			}
		}

	    Integer numOfAccounts = null;
	    Integer numOfAddresses = null;
	    int numOfAccountsWithoutSuitableEmailAddress = 0;
    	int numOfActivitiesCreated = 0;
    	int numOfActivitiesUpdated = 0;
	    
   	    String name = (String)formValues.get("org:opencrx:kernel:activity1:Activity:name");
   	    String description = (String)formValues.get("org:opencrx:kernel:activity1:Activity:description");
   	    String detailedDescription = (String)formValues.get("org:opencrx:kernel:activity1:Activity:detailedDescription");
   	    String messageSubject = (String)formValues.get("org:opencrx:kernel:activity1:EMail:messageSubject");
   	    String messageBody = (String)formValues.get("org:opencrx:kernel:activity1:EMail:messageBody");
   	    Date scheduledStart = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:scheduledStart");
   	    Date scheduledEnd = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:scheduledEnd");
   	    Short priority = (Short)formValues.get("org:opencrx:kernel:activity1:Activity:priority");
   	    Date dueBy = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:dueBy");
		org.opencrx.kernel.account1.jmi1.AccountAddress sender = null;
		try {
			Path senderPath = null;
			System.out.println("sender0 set?");
			if (formValues.get("org:opencrx:kernel:activity1:EMail:sender") != null) {
				System.out.println("yes sender0 set");
				if (formValues.get("org:opencrx:kernel:activity1:EMail:sender") instanceof String) {
					senderPath = new Path((String)formValues.get("org:opencrx:kernel:activity1:EMail:sender"));
				} else {
					senderPath = (Path)formValues.get("org:opencrx:kernel:activity1:EMail:sender");
				}
				sender = (org.opencrx.kernel.account1.jmi1.AccountAddress)pm.getObjectById(senderPath);
			}
		} catch(Exception e) {
			new ServiceException(e).log();
		}
		List<Short> usages = (List<Short>)formValues.get("org:opencrx:kernel:address1:Addressable:usage");
		org.opencrx.kernel.activity1.jmi1.ActivityType activityType = null;
		if (activityCreator != null) {
			try {
				activityType = activityCreator.getActivityType();
			} catch (Exception e) {
				new ServiceException(e).log();
			}
		}

    	boolean canCreate = activityCreator != null &&
    						 activityType != null &&
    						 (sender != null || activityType.getActivityClass() != Activities.ActivityClass.EMAIL.getValue());
		SortedSet accountXris = new TreeSet();
		SortedSet addressXris = new TreeSet();
		List<String> xris = new ArrayList();
		boolean xrisAreAccounts = true;

		if(actionCreate || actionCreateTest || actionCreateTestConfirmed) {
    	    // Create activities
    	    if(canCreate) {
    			String text = (String)formValues.get("org:opencrx:kernel:base:Note:text");
    			Properties defaultPlaceHolders = new Properties();
    			if(text != null) {
    				defaultPlaceHolders.load(new StringReader(text));
    			}

    	    	if (accountFilter != null) {
                    org.opencrx.kernel.account1.cci2.AccountQuery accountQuery = 
                    	(org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Account.class);
                    accountQuery.forAllDisabled().isFalse();
    	    	    for(Iterator i = accountFilter.getFilteredAccount(accountQuery).iterator(); i.hasNext(); ) {
    	    	    	try {
	    	    	        accountXris.add(((org.opencrx.kernel.account1.jmi1.Account)i.next()).refMofId());
    	    	    	} catch (Exception e) {
    	    	    		new ServiceException(e).log();
    	    	    	}
    	    	    }
    	    	    xris = new ArrayList<String>(accountXris);
    	    	} else if (group != null) {
                    org.opencrx.kernel.account1.cci2.MemberQuery memberQuery = 
                    	(org.opencrx.kernel.account1.cci2.MemberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Member.class);
                    memberQuery.forAllDisabled().isFalse();
    	    	    for(Iterator i = group.getMember(memberQuery).iterator(); i.hasNext(); ) {
    	    	    	try {
    	    	    		org.opencrx.kernel.account1.jmi1.Member m = (org.opencrx.kernel.account1.jmi1.Member)i.next();
    	    	    		if (m.getAccount() != null && (m.getAccount().isDisabled() == null || m.getAccount().isDisabled().booleanValue() == false)) {
	    	    	        	accountXris.add(m.getAccount().refMofId());
    	    	    		}
    	    	    	} catch (Exception e) {
    	    	    		new ServiceException(e).log();
    	    	    	}
    	    	    }
    	    	    xris = new ArrayList<String>(accountXris);
    	    	} else if (addressFilter != null) {
                    org.opencrx.kernel.account1.cci2.AccountAddressQuery accountAddressQuery = 
                    	(org.opencrx.kernel.account1.cci2.AccountAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.AccountAddress.class);
                    accountAddressQuery.forAllDisabled().isFalse();
    	    	    for(Iterator i = addressFilter.getFilteredAddress(accountAddressQuery).iterator(); i.hasNext(); ) {
    	    	    	try {
    	    	    		addressXris.add(((org.opencrx.kernel.account1.jmi1.AccountAddress)i.next()).refMofId());
    	    	    	} catch (Exception e) {
    	    	    		new ServiceException(e).log();
    	    	    	}
    	    	    }
    	    	    xrisAreAccounts = false;
    	    	    xris = new ArrayList<String>(addressXris);
    	    	} else if (addressGroup != null) {
                    org.opencrx.kernel.activity1.cci2.AddressGroupMemberQuery addressGroupMemberQuery = 
                    	(org.opencrx.kernel.activity1.cci2.AddressGroupMemberQuery)pm.newQuery(org.opencrx.kernel.activity1.cci2.AddressGroupMember.class);
                    addressGroupMemberQuery.forAllDisabled().isFalse();
    	    	    for(Iterator i = addressGroup.getMember(addressGroupMemberQuery).iterator(); i.hasNext(); ) {
    	    	    	try {
	    	    	        org.opencrx.kernel.activity1.jmi1.AddressGroupMember addressGroupMember = (org.opencrx.kernel.activity1.jmi1.AddressGroupMember)i.next();
	    	    	        if (addressGroupMember.getAddress() != null && (addressGroupMember.getAddress().isDisabled() == null || addressGroupMember.getAddress().isDisabled().booleanValue() == false)) {
		    	    	        addressXris.add(addressGroupMember.getAddress().refMofId());
	    	    	        }
    	    	    	} catch (Exception e) {
    	    	    		new ServiceException(e).log();
    	    	    	}
    	    	    }
    	    	    xrisAreAccounts = false;
    	    	    xris = new ArrayList<String>(addressXris);
    	    	}
    	    	
    	    	if (xrisAreAccounts) {
    	    		if (actionCreateTestConfirmed) {
    	    			// add optional test accounts if any
						if (formValues.get("org:opencrx:kernel:account1:AccountAssignment:account") != null) {
							try {
								xris.add(0, ((Path)formValues.get("org:opencrx:kernel:account1:AccountAssignment:account")).toXRI());
							} catch (Exception e) {
								new ServiceException(e).log();
							}
						}
    	    		}
    	    		numOfAccounts = xris.size();
    	    	} else {
    	    		if (actionCreateTestConfirmed) {
    	    			// add optional test addresses if any
						org.opencrx.kernel.activity1.jmi1.ActivityType actType = activityCreator.getActivityType(); 
						if(actType != null && actType.getActivityClass() == org.opencrx.kernel.backend.Activities.ActivityClass.EMAIL.getValue()) {
							if (formValues.get("org:opencrx:kernel:activity1:EMail:to0") != null) {
								try {
									xris.add(0, ((Path)formValues.get("org:opencrx:kernel:activity1:EMail:to0")).toXRI());
								} catch (Exception e) {
									new ServiceException(e).log();
								}
							}
							if (formValues.get("org:opencrx:kernel:activity1:EMail:to1") != null) {
								try {
									xris.add(0, ((Path)formValues.get("org:opencrx:kernel:activity1:EMail:to1")).toXRI());
								} catch (Exception e) {}
							}
							if (formValues.get("org:opencrx:kernel:activity1:EMail:to2") != null) {
								try {
									xris.add(0, ((Path)formValues.get("org:opencrx:kernel:activity1:EMail:to2")).toXRI());
								} catch (Exception e) {}
							}
						}
    	    		}
    	    		numOfAddresses = xris.size();
    	    	}
    	    	
    	    	if (actionCreate || actionCreateTestConfirmed) {
	    	    	int counter = 0;
	    	    	for(String id: xris) {
	    	    		counter++;
	    	    		org.opencrx.kernel.account1.jmi1.AccountAddress address = null;
	    	    		org.opencrx.kernel.account1.jmi1.Account account = null;
	    	    		if (xrisAreAccounts) {
	    	    			try {
	    	    				account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(new Path(id));
	    	    				//System.out.println("adding #" + counter + " account: " + id);
	    	    			} catch (Exception e) {
	    	    				new ServiceException(e).log();
	    	    			}
	    	    		} else {
	    	    			try {
	    	    				address = (org.opencrx.kernel.account1.jmi1.AccountAddress)pm.getObjectById(new Path(id));
	    	    				account = (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(address.refGetPath().getParent().getParent());
	    	    				//System.out.println("adding #" + counter + " address: " + id);
	    	    			} catch (Exception e) {
	    	    				new ServiceException(e).log();
	    	    			}    	    			
	    	    		}
		    			org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = (org.opencrx.kernel.activity1.cci2.ActivityQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Activity.class);
		    			org.opencrx.kernel.activity1.cci2.EMailQuery emailQuery = (org.opencrx.kernel.activity1.cci2.EMailQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.EMail.class);					
		    			List<org.opencrx.kernel.activity1.jmi1.Activity> activities = null;
	
		    			// match recipient address
		    			if(
		    				address != null &&
		    				address instanceof org.opencrx.kernel.account1.jmi1.EMailAddress &&
		    				activityType.getActivityClass() == Activities.ActivityClass.EMAIL.getValue()
		    			) {
							// match recipient e-mail address
							org.opencrx.kernel.activity1.cci2.EMailRecipientQuery emailRecipientQuery = 
								(org.opencrx.kernel.activity1.cci2.EMailRecipientQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.EMailRecipient.class);
							emailRecipientQuery.thereExistsParty().equalTo(address);
							emailQuery.thereExistsEmailRecipient().elementOf(
								org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(emailRecipientQuery)
							);
		    	    		if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
		    	    			emailQuery.thereExistsReportingContact().equalTo(account);    	    			
		    	    		} else {
		    	    			emailQuery.thereExistsReportingAccount().equalTo(account);
		    	    		}
			    			// match ActivityCreator
		    	    		emailQuery.thereExistsLastAppliedCreator().equalTo(activityCreator);
		    	    		emailQuery.orderByCreatedAt().descending();
			    			activities = activitySegment.getActivity(emailQuery);
						} else {
			    			// match Account with reportingContact/reportingAccount
		    	    		if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
								activityQuery.thereExistsReportingContact().equalTo(account);    	    			
		    	    		} else {
		    	    			activityQuery.thereExistsReportingAccount().equalTo(account);
		    	    		}
			    			// match ActivityCreator
		    	    		activityQuery.thereExistsLastAppliedCreator().equalTo(activityCreator);
		    	    		activityQuery.orderByCreatedAt().descending();
			    			activities = activitySegment.getActivity(activityQuery);		    			
						}
		    			org.opencrx.kernel.activity1.jmi1.Activity activity = null;
		    			// Create if activity does not exist
		    			if(activities.isEmpty()) {
		    	    		String activityName = name + " / " + account.getFullName() + (account.getAliasName() == null ? "" : " / " + account.getAliasName());		    			
		        			org.opencrx.kernel.activity1.jmi1.NewActivityParams params = org.opencrx.kernel.utils.Utils.getActivityPackage(pm).createNewActivityParams(
		        				null, // creationContext
		        				description,
		        				detailedDescription,
		        				dueBy,
		        				ICalendar.ICAL_TYPE_NA,
		        				activityName,
		        				priority,
		        				null, // reportingContact
		        				scheduledEnd,
		        				scheduledStart
							);
		        			try {
		        				pm.currentTransaction().begin();
		        				org.opencrx.kernel.activity1.jmi1.NewActivityResult result = activityCreator.newActivity(params);	        				
		        				pm.currentTransaction().commit();
		        				activity = result.getActivity();
		        				numOfActivitiesCreated++;
		        			} catch(Exception e) {
		        				try {
		        					pm.currentTransaction().rollback();
		        				} catch(Exception e0) {}
		        			}	        			
		    			} else {
		    				activity = activities.iterator().next();
	        				numOfActivitiesUpdated++;
		    			}
		    			if(activity != null) {
		    				try {
			    				String replacedDescription = null;
			    				try {
			    					replacedDescription = extension.replacePlaceHolders(
			    	    	    		account, 
			    	    	    		locale == null ? (short)0 : locale.shortValue(), 
			    	    	    		description, 
			    	    	    		defaultPlaceHolders, 
			    	    	    		codes
			    	    	    	);
			    				} catch (Exception e) {}
			    				String replacedDetailedDescription = null;
			    				try {
			    					replacedDetailedDescription = extension.replacePlaceHolders(
			    	    	    		account, 
			    	    	    		locale == null ? (short)0 : locale.shortValue(), 
			    	    	    		detailedDescription, 
			    	    	    		defaultPlaceHolders, 
			    	    	    		codes
			    	    	    	);
			    				} catch (Exception e) {}
			    				String replacedMessageSubject = null;
			    				try {
				    				replacedMessageSubject = extension.replacePlaceHolders(
			    	    	    		account, 
			    	    	    		locale == null ? (short)0 : locale.shortValue(), 
			    	    	    		messageSubject, 
			    	    	    		defaultPlaceHolders, 
			    	    	    		codes
			    	    	    	);
			    				} catch (Exception e) {}
			    				String replacedMessageBody = null;
			    				try {
			    					replacedMessageBody = extension.replacePlaceHolders(
			    	    	    		account, 
			    	    	    		locale == null ? (short)0 : locale.shortValue(), 
			    	    	    		messageBody, 
			    	    	    		defaultPlaceHolders, 
			    	    	    		codes
			    	    	    	);
			    				} catch (Exception e) {}
								pm.currentTransaction().begin();
			    				activity.setDescription(replacedDescription);
			    				activity.setDetailedDescription(replacedDetailedDescription);
								if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
									activity.setReportingContact((org.opencrx.kernel.account1.jmi1.Contact)account);
									//System.out.println("reporting Contact: " + activity.getReportingContact().getFullName());
								} else {
									activity.setReportingContact(null);
									activity.setReportingAccount(account);
									//System.out.println("reporting Account: " + activity.getReportingAccount().getFullName());
								}
								if(activity instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
									org.opencrx.kernel.activity1.jmi1.EMail email = (org.opencrx.kernel.activity1.jmi1.EMail)activity;
									email.setMessageSubject(replacedMessageSubject);
									email.setMessageBody(replacedMessageBody);
									// Sender / Recipient EMAIL_FROM
									email.setSender(sender);
									if(sender instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) {
										updateEMailRecipient(
											email,
											(org.opencrx.kernel.account1.jmi1.EMailAddress)sender,
											Activities.PartyType.EMAIL_FROM
										);
									}
									// Recipient EMAIL_TO
									org.opencrx.kernel.account1.jmi1.AccountAddress recipientAddress = null;
									if (address != null) {
										recipientAddress = address;
									} else {
										for(short usage: usages) {
											List<org.opencrx.kernel.account1.jmi1.AccountAddress> addresses = Accounts.getInstance().getAccountAddresses(
												account, 
												usage
											);
											if(!addresses.isEmpty()) {
												for(org.opencrx.kernel.account1.jmi1.AccountAddress acctAddress: addresses) {
													if(acctAddress instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) {
														recipientAddress = acctAddress;
														break;
													}
												}
												if(recipientAddress != null) {
													break;
												}
											}
										}
									}
									if(recipientAddress instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) {
										updateEMailRecipient(
											email,
											(org.opencrx.kernel.account1.jmi1.EMailAddress)recipientAddress,
											Activities.PartyType.EMAIL_TO
										);
									} else {
										// delete email as there is no recipient
										email.refDelete();
										counter--;
						    			if(activities.isEmpty()) {
						    				numOfActivitiesCreated--;
						    			} else {
						    				numOfActivitiesUpdated--;
						    			}
						    			numOfAccountsWithoutSuitableEmailAddress++;
									}
								}
								pm.currentTransaction().commit();
			       			} catch(Exception e) {
			       				try {
			       					pm.currentTransaction().rollback();
			       				} catch(Exception e0) {}
			       			}
		    			}
	    	    		if (actionCreateTestConfirmed && counter >= NUM_OF_TEST_ACTIVITIES) {break;} // create only 3 test activities
	    	    	}
    	    	}
				/*
				Action nextAction = new ObjectReference(
					obj,
					app
				).getSelectObjectAction();
				response.sendRedirect(
					request.getContextPath() + "/" + nextAction.getEncodedHRef()
				);
				return;
				*/
			}
		}
		else if(actionSave) {
			String text = (String)formValues.get("org:opencrx:kernel:base:Note:text");
			Properties placeHolders = new Properties();
			if(text != null) {
				placeHolders.load(new StringReader(text));
			}
			extension.updatePlaceHolders(
				placeHolders,
				(String)formValues.get("org:opencrx:kernel:activity1:Activity:description")
			);
			extension.updatePlaceHolders(
				placeHolders,
				(String)formValues.get("org:opencrx:kernel:activity1:Activity:detailedDescription")
			);
			extension.updatePlaceHolders(
				placeHolders,
				(String)formValues.get("org:opencrx:kernel:activity1:EMail:messageSubject")
			);
			extension.updatePlaceHolders(
				placeHolders,
				(String)formValues.get("org:opencrx:kernel:activity1:EMail:messageBody")
			);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();			
			placeHolders.store(new OutputStreamWriter(bos, "UTF-8"), "");
			// Update form values
			formValues.put(
				"org:opencrx:kernel:base:Note:text",
				bos.toString("UTF-8")
			);
			// Save as properties on activity creator
			if(activityCreator != null && locale != null) {
				try {
					pm.currentTransaction().begin();
					StringPropertyDataBinding dataBinding = new StringPropertyDataBinding();
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!name", 
						formValues.get("org:opencrx:kernel:activity1:Activity:name")
					);
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!description", 
						formValues.get("org:opencrx:kernel:activity1:Activity:description")
					);
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!detailedDescription", 
						formValues.get("org:opencrx:kernel:activity1:Activity:detailedDescription")
					);
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!messageSubject", 
						formValues.get("org:opencrx:kernel:activity1:EMail:messageSubject")
					);
					// split messageBody into pieces of 2048 chars
					List<String> messageBodyParts = splitEqually((String)formValues.get("org:opencrx:kernel:activity1:EMail:messageBody"), 2048);
					int idx = 0;
					for(int i = 0; i < messageBodyParts.size(); i++) {
						try {
							dataBinding.setValue(
								activityCreator, 
								":BulkCreateActivityWizardSettings." + locale + "!messageBody" + i, 
								messageBodyParts.get(i)
							);
							idx++;
						} catch (Exception e) {
							new ServiceException(e).log();
						}
					}
					// reset unused messageBody properties if they exist
					try {
						while (dataBinding.getValue(activityCreator, ":BulkCreateActivityWizardSettings." + locale + "!messageBody" + idx) != null) {
							/*
							dataBinding.setValue(
									activityCreator, 
									":BulkCreateActivityWizardSettings." + locale + "!messageBody" + idx, 
									null
								);
							*/
							org.opencrx.kernel.base.jmi1.Property property = dataBinding.findProperty(
									activityCreator, 
									":BulkCreateActivityWizardSettings." + locale + "!messageBody" + idx
								);
							property.refDelete();
							idx++;
						}
					} catch (Exception e) {
						new ServiceException(e).log();
					}
					
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!placeHolders", 
						formValues.get("org:opencrx:kernel:base:Note:text")
					);
					Path senderPath = (Path)formValues.get("org:opencrx:kernel:activity1:EMail:sender");
					dataBinding.setValue(
						activityCreator, 
						":BulkCreateActivityWizardSettings." + locale + "!sender", 
						(senderPath == null ? null : senderPath.toXRI())
					);
					int i = 0;
					for(Short usage: (List<Short>)formValues.get("org:opencrx:kernel:address1:Addressable:usage")) {
						dataBinding.setValue(
							activityCreator,
							":BulkCreateActivityWizardSettings." + locale + "!usage." + i,
							usage.toString()
						);
						i++;
					}
					pm.currentTransaction().commit();
				} catch(Exception e) {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e0) {}
				}
			}
		}
    	TransientObjectView view = new TransientObjectView(
    		formValues,
    		app,
    		Accounts.getInstance().getAccountSegment(pm, providerName, segmentName), // pass accountSegment instead of current obj to ensure proper e-mail address lookup
    		pm
    	);
    	ViewPort p = ViewPortFactory.openPage(
    		view,
    		request,
    		out
    	);
%>
		<br />
		<form id="<%= FORM_NAME %>" name="<%= FORM_NAME %>" accept-charset="UTF-8" method="POST" action="<%= servletPath %>">
			<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
			<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
			<input type="hidden" id="Command" name="Command" value="" />
			<input type="hidden" id="Command2" name="Command2" value="" />
			<input type="hidden" id="DeleteCommand" name="DeleteCommand" value="" />
			<input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked="true" />
			<table cellspacing="8" class="tableLayout">
				<tr>
					<td class="cellObject">
						<div class="panel" id="panel<%= FORM_NAME_CREATOR %>" style="display: block">
<%
							formCreator.paint(
								p,
								null, // frame
								true // forEditing
							);
							p.flush();
%>
						</div>
						<div id="submitButtons1">
							<input type="submit" name="Reload"  id="Reload.Button"  tabindex="8000" value="<%= texts.getReloadText() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('submitButtons1').style.visibility='hidden';" />
							<input type="submit" name="Refresh" id="Refresh.Button" tabindex="8001" value="<%= texts.getReloadText() %>" style="display:none;" onclick="javascript:$('Command').value=this.name;this.name='--';$('submitButtons1').style.visibility='hidden';" />
						</div>
<%
						if(activityCreator != null) {
%>						
							<div class="panel" id="panel<%= FORM_NAME_ACTIVITY %>" style="display: block">
<%
								formActivity.paint(
									p,
									null, // frame
									true // forEditing
								);
								p.flush();
%>
							</div>
<%
							try {
								org.opencrx.kernel.activity1.jmi1.ActivityType actType = activityCreator.getActivityType(); 
								if(actType != null && actType.getActivityClass() == org.opencrx.kernel.backend.Activities.ActivityClass.EMAIL.getValue()) {
%>							
									<div class="panel" id="panel<%= FORM_NAME_EMAIL %>" style="display: block">
<%
										formEMail.paint(
											p,
											null, // frame
											true // forEditing
										);
										p.flush();
%>
									</div>
<%
								}
							} catch (Exception e) {
								new ServiceException(e).log();
							}
%>								
							<div class="panel" id="panel<%= FORM_NAME_PLACEHOLDERS %>" style="display: block">
<%
								formPlaceHolders.paint(
									p,
									null, // frame
									true // forEditing
								);
								p.flush();
%>
							</div>
							<div id="waitMsg" style="display:none;float:left;">
								Processing request - please wait...<br>
								<img border="0" src='./images/progress_bar.gif' alt='please wait...' />
							</div>						
							<div id="submitButtons" style="padding-top:20px;float:left;">
								<input type="submit" name="Save" id="Save.Button" tabindex="9000" value="<%= app.getTexts().getSaveTitle() %>" onclick="javascript:$('Command').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';" />
<%
								if (canCreate) {
%>
									<input type="submit" name="CreateTest" id="CreateTest.Button" tabindex="9010" value="Create/Update <%= NUM_OF_TEST_ACTIVITIES %> Test Activities" <%= actionCreateTest ? "style='display:none;'" : "" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';" />
									<input type="submit" name="Create" id="Create.Button" tabindex="9030" value="Create/Update" onclick="javascript:$('Command').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';" />
<%
								} else {
%>
									<span style="border:1px solid grey;padding:3px;"> E-Mail Sender missing </span>
<%
								}
%>
								<input type="submit" name="Cancel" tabindex="9040" value="<%= app.getTexts().getCloseText() %>" onclick="javascript:$('Command').value=this.name;" />
							</div><span style="clear:both;"></span>
							
<%
							if (canCreate) {
								try {
									org.opencrx.kernel.activity1.jmi1.ActivityType actType = activityCreator.getActivityType(); 
									if(
										actType != null && actType.getActivityClass() == org.opencrx.kernel.backend.Activities.ActivityClass.EMAIL.getValue() &&
										!xrisAreAccounts
									) {
%>							
										<div class="panel" id="panel<%= FORM_NAME_EMAILTO %>" style="display:<%= actionCreateTest ? "block" : "none" %>;">
<%
											formEMailTo.paint(
												p,
												null, // frame
												true // forEditing
											);
											p.flush();
%>
										</div>
<%
									} else if (xrisAreAccounts) {
%>							
										<div class="panel" id="panel<%= FORM_NAME_RECIPIENT %>" style="display:<%= actionCreateTest ? "block" : "none" %>;">
<%
											formRecipient.paint(
												p,
												null, // frame
												true // forEditing
											);
											p.flush();
%>
										</div>
<%
									}
								} catch (Exception e) {
									new ServiceException(e).log();
								}
%>
								<input type="submit" name="CreateTestCONF" id="CreateTestCONF.Button" tabindex="9020" value="Confirm Create/Update <%= NUM_OF_TEST_ACTIVITIES %> Test Activities" <%= actionCreateTest ? "" : "style='display:none;'" %> onclick="javascript:$('Command').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';" />
<%
							}
						}
%>							
					</td>
				</tr>
			</table>
		</form>
		<br>&nbsp;
		
<%
		if(actionCreate || actionCreateTestConfirmed) {
%>
			<div class="fieldGroupName">Create/Update results</div>
            <div style="border:0px solid black;padding:10px;margin:2px;">
            	<table>
<%
					if (numOfAccounts != null) {
%>
	            		<tr>
	            			<td>#Accounts processed:</td>
	            			<td><%= numOfAccounts.intValue() %> <%= numOfAccountsWithoutSuitableEmailAddress > 0 ? "&nbsp;&nbsp;&nbsp;(WARNING: #Accounts w/out suitable e-mail address: " + numOfAccountsWithoutSuitableEmailAddress + ")" : "" %></td>
	            		</tr>
<%
					}
					if (numOfAddresses != null) {
%>
	            		<tr>
	            			<td>#Addresses processed:</td>
	            			<td><%= numOfAddresses.intValue() %></td>
	            		</tr>
<%
					}
%>
            		<tr>
            			<td>#Activities newly created:&nbsp;&nbsp;</td>
            			<td><%= numOfActivitiesCreated %></td>
            		</tr>
            		<tr>
            			<td>#Activities updated:</td>
            			<td><%= numOfActivitiesUpdated %></td>
            		</tr>
            		<tr>
            			<td>#Activities processed:</td>
            			<td><%= numOfActivitiesCreated + numOfActivitiesUpdated %> <%= actionCreateTestConfirmed ? " (note: test run - processing limit is " + NUM_OF_TEST_ACTIVITIES + " activities)" : "" %></td>
            		</tr>
            	</table>
            </div>
<%
		}

		if(activityCreator != null) {
			org.opencrx.kernel.activity1.cci2.ActivityQuery activityQuery = 
				(org.opencrx.kernel.activity1.cci2.ActivityQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Activity.class);
			activityQuery.thereExistsLastAppliedCreator().equalTo(activityCreator);
			int ii = 0;
			int tabIdx = 9050;
			for(org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup: activityCreator.<org.opencrx.kernel.activity1.jmi1.ActivityGroup>getActivityGroup()) {
				String deleteButtonName = "DeleteAct-" + activityGroup.refMofId();
				boolean deleteActivitiesRequest = request.getParameter("DeleteCommand") != null && request.getParameter("DeleteCommand").compareTo(deleteButtonName) == 0;
				boolean deleteActivitiesConfirmed = request.getParameter("DeleteCommand") != null && request.getParameter("DeleteCommand").compareTo(deleteButtonName + "CONF") == 0;

				String countButtonName = "CountAct-" + activityGroup.refMofId();
				boolean countActivitiesRequest = request.getParameter("Command2") != null && request.getParameter("Command2").compareTo(countButtonName) == 0;
%>
				<div class="fieldGroupName"><%= (new ObjectReference(activityGroup, app)).getLabel() + " " + activityGroup.getName() %></div>
				<div style='padding:4px;'>
<%
					Integer numberOfActivities = null;
					if(countActivitiesRequest) {
						numberOfActivities = activityGroup.getFilteredActivity().size();
					}
%>				
					<h2>						
						<input type="button" name="<%= countButtonName %>" id="<%= countButtonName %>.Button" tabindex="<%= tabIdx++ %>" value='Count Activities<%= numberOfActivities == null ? "" : " (=" + numberOfActivities + ")" %>' onclick="javascript:$('Command2').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';$('Refresh.Button').click();" />
						<input type="button" name="<%= deleteButtonName %>" id="<%= deleteButtonName %>.Button" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getDeleteTitle() %> Activities" onclick="javascript:$('DeleteCommand').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';$('Refresh.Button').click();" />
					</h2>
<%
					if (deleteActivitiesConfirmed) {
						Collection<org.opencrx.kernel.activity1.jmi1.Activity> activities = activityGroup.getFilteredActivity();
						for(org.opencrx.kernel.activity1.jmi1.Activity activity : activities) {
							try {
								pm.currentTransaction().begin();
								activity.refDelete();
								pm.currentTransaction().commit();
							} catch (Exception e) {
								new ServiceException(e).log();
							}
						}
					}
					if (deleteActivitiesRequest) {
%>
			            <div style="border:1px solid black;padding:10px;margin:2px;background-color:#FF9900;">
			              Do you really want to delete all activities assigned to this tracker?
			            </div>
			            <input type="button" name="<%= deleteButtonName %>CONF" id="<%= deleteButtonName %>CONF.Button" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getDeleteTitle() %>" onclick="javascript:$('DeleteCommand').value=this.name;this.name='--';$('waitMsg').style.display='block';$('submitButtons').style.visibility='hidden';$('Refresh.Button').click();" />
			            <INPUT type="button" name="CancelDelete.Button" tabindex="<%= tabIdx++ %>" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Refresh.Button').click();" />
<%
					}
%>
					<portal:showobject id='<%= "A" + ii %>' object="<%= activityGroup.refGetPath() %>" navigationTarget="_blank" resourcePathPrefix="./" showAttributes="true" grids="filteredActivity">
						<portal:query name="filteredActivity" query="<%= activityQuery %>" />
					</portal:showobject>
				</div>
<%
				ii++;
			}
		}
%>
		</div>		
		<script language="javascript" type="text/javascript">
	      	Event.observe('<%= FORM_NAME %>', 'submit', function(event) {
	      		$('<%= FORM_NAME %>').request({
	      			onFailure: function() { },
	      			onSuccess: function(t) {
	      				$('UserDialog').update(t.responseText);
	      			}
	      		});
	      		Event.stop(event);
	      	});
		</script>
<%
		p.close(false);
	}
	catch (Exception e) {
		new ServiceException(e).log();
	}
	finally {
		if(pm != null) {
			pm.close();
		}
	}
%>

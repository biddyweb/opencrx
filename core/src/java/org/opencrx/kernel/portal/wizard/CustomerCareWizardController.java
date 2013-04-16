/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CustomerCareWizardController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2013, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.wizard;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.ContactQuery;
import org.opencrx.kernel.account1.cci2.EMailAddressQuery;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.cci2.PhoneNumberQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpParams;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessState;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.portal.AbstractPropertyDataBinding;
import org.opencrx.kernel.portal.BooleanPropertyDataBinding;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.ViewPortFactory;
import org.openmdx.portal.servlet.WebKeys;
import org.openmdx.portal.servlet.view.TransientObjectView;
import org.openmdx.portal.servlet.view.View;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * CustomerCareWizardController
 *
 */
public class CustomerCareWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 */
	public CustomerCareWizardController(
	) {
		super();
	}
	
	/**
	 * Commands
	 *
	 */
	public enum Command {
		Cancel,
		BackToSearchContact,
		BackToContact,
		SearchContact,
		SelectContact,
		LockContact,
		NewActivityContact,
		NewActivity,
		SaveAsNewActivityContact,
		SaveAsNewActivity,
		SelectActivity,
		DoFollowUp
	}

	/**
	 * Form
	 *
	 */
	public enum Form {
		CustomerCareSearchContactForm,	
		ContactForm,
		CustomerCareCreateActivityForm,
		CustomerCareShowActivityForm,
		CustomerCareCreateFollowUpForm
	}

	static final String PROPERTY_NAME_IS_LOCKED = "org:opencrx:wizards:CustomerCareWizard!contactIsLocked";
	
	static final long LOCK_TIMEOUT = 300000L; // 5 minutes 
	
	/**
	 * WizardState
	 *
	 */
	public static class WizardState {
		
		/**
		 * Constructor.
		 * 
		 * @param activitySegment
		 * @param app
		 */
		public WizardState(
			org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
			ApplicationContext app
		) {
			javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(activitySegment);
			// Allowed activity creators
			List<Path> allowedActivityCreators = new ArrayList<Path>(); 
			List<Path> allowedActivityCreatorsContact = new ArrayList<Path>(); 
			ActivityCreatorQuery activityCreatorQuery = (ActivityCreatorQuery)pm.newQuery(ActivityCreator.class);
			activityCreatorQuery.forAllDisabled().isFalse();
			activityCreatorQuery.orderByName().ascending();
			List<ActivityCreator> activityCreators = activitySegment.getActivityCreator(activityCreatorQuery);
			for(ActivityCreator activityCreator: activityCreators) {
				if(app.getPortalExtension().hasPermission("CustomerCareWizard:SEARCH_CONTACT:" + activityCreator.refGetPath().get(5) + "/" + activityCreator.refGetPath().get(6), activityCreator, app, WebKeys.GRANT_PREFIX + "show")) {
					allowedActivityCreators.add(activityCreator.refGetPath());
				} else if(app.getPortalExtension().hasPermission("CustomerCareWizard:CONTACT:" + activityCreator.refGetPath().get(5) + "/" + activityCreator.refGetPath().get(6), activityCreator, app, WebKeys.GRANT_PREFIX + "show")) {
					allowedActivityCreatorsContact.add(activityCreator.refGetPath());
				}
			}
			this.allowedActivityCreators = allowedActivityCreators;
			this.allowedActivityCreatorsContact = allowedActivityCreatorsContact;
			// Allowed process states
			List<Path> allowedProcessStates = new ArrayList<Path>(); 
			Collection<ActivityProcess> activityProcesses = activitySegment.getActivityProcess();
			for(ActivityProcess activityProcess: activityProcesses) {
				Collection<ActivityProcessState> activityProcessStates = activityProcess.getState();
				for(ActivityProcessState processState: activityProcessStates) {
					if(
						app.getPortalExtension().hasPermission("CustomerCareWizard:" + processState.refGetPath().get(5) + "/" + processState.refGetPath().get(6) + "/" + processState.refGetPath().get(7) + "/:*", activitySegment, app, WebKeys.GRANT_PREFIX + "show") ||							
						app.getPortalExtension().hasPermission("CustomerCareWizard:" + processState.refGetPath().get(5) + "/" + processState.refGetPath().get(6) + "/" + processState.refGetPath().get(7) + "/" + processState.refGetPath().get(8), processState, app, WebKeys.GRANT_PREFIX + "show")
					) {
						allowedProcessStates.add(processState.refGetPath());
					}					
				}
			}
			this.allowedProcessStates = allowedProcessStates;
		}
		
		public void setSelectedContactIdentity(
			Path newValue
		) {
			this.selectedContactIdentity = newValue;
		}
		
		public Path getSelectedContactIdentity(
		) {
			return this.selectedContactIdentity;
		}
		
		public void setSelectedActivityIdentity(
			Path newValue
		) {
			this.selectedActivityIdentity = newValue;
		}
		
		public Path getSelectedActivityIdentity(
		) {
			return this.selectedActivityIdentity;
		}
		
		public List<Path> getAllowedActivityCreatorsContact(
		) {
			return this.allowedActivityCreatorsContact;
		}
		
		public List<Path> getAllowedActivityCreators(
		) {
			return this.allowedActivityCreators;
		}
		
		public List<Path> getAllowedProcessStates(
		) {
			return this.allowedProcessStates;
		}
		
		public Path getSelectedActivityCreatorIdentity(
		) {
			return this.selectedActivityCreatorIdentity;
		}
		
		public void setSelectedActivityCreatorIdentity(
			Path newValue
		) {
			this.selectedActivityCreatorIdentity = newValue;
		}
		
		private Path selectedContactIdentity = null;
		private Path selectedActivityIdentity = null;
		private Path selectedActivityCreatorIdentity = null;
		private final List<Path> allowedActivityCreatorsContact;
		private final List<Path> allowedActivityCreators;
		private final List<Path> allowedProcessStates;
	}
	
	/**
	 * Validates whether lock flag is set for contact.
	 * 
	 * @param contact
	 * @return
	 */
	public boolean contactIsLocked(
		org.opencrx.kernel.account1.jmi1.Account contact
	) {
		BooleanPropertyDataBinding binding = new BooleanPropertyDataBinding(AbstractPropertyDataBinding.PropertySetHolderType.CrxObject);
		org.opencrx.kernel.base.jmi1.Property p = binding.findProperty(contact, PROPERTY_NAME_IS_LOCKED);
		return 
			Boolean.TRUE.equals(binding.getValue(contact, PROPERTY_NAME_IS_LOCKED)) &&
			p != null &&
			p.getModifiedAt().getTime() > System.currentTimeMillis() - LOCK_TIMEOUT;
	}
	
	/**
	 * Set lock flag for contact.
	 * 
	 * @param contact
	 */
	public void lockContact(
		org.opencrx.kernel.account1.jmi1.Account contact
	) {
		BooleanPropertyDataBinding binding = new BooleanPropertyDataBinding(AbstractPropertyDataBinding.PropertySetHolderType.CrxObject);
		javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(contact);
		// Make sure that modifiedAt is updated
		pm.currentTransaction().begin();
		binding.setValue(
			contact, 
			PROPERTY_NAME_IS_LOCKED, 
			false
		);
		pm.currentTransaction().commit();
		pm.currentTransaction().begin();
		binding.setValue(
			contact, 
			PROPERTY_NAME_IS_LOCKED, 
			true
		);
		pm.currentTransaction().commit();
	}
	
	/**
	 * Create activity.
	 * 
	 * @param reportingContact
	 * @return
	 * @throws ServiceException
	 */
	public Activity createActivity(
		Contact reportingContact
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		String name = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:name");
		Short priority = (Short)this.formFields.get("org:opencrx:kernel:activity1:Activity:priority");
		Date dueBy = (Date)this.formFields.get("org:opencrx:kernel:activity1:Activity:dueBy");
		Date scheduledStart = (Date)this.formFields.get("org:opencrx:kernel:activity1:Activity:scheduledStart");
		Date scheduledEnd = (Date)this.formFields.get("org:opencrx:kernel:activity1:Activity:scheduledEnd");
		String misc1 = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:misc1");					
		String misc2 = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:misc2");					
		String misc3 = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:misc3");					
		String description = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:description");					
		String detailedDescription = (String)this.formFields.get("org:opencrx:kernel:activity1:Activity:detailedDescription");
		ActivityCreator activityCreator = (ActivityCreator)pm.getObjectById(this.wizardState.getSelectedActivityCreatorIdentity());					
		Activity newActivity = null;
		if(name != null) {
			try {
				pm.currentTransaction().begin();
				newActivity = Activities.getInstance().newActivity(
					activityCreator, 
					name,
					description == null ? null : description.replace("\r\n", "\n"), 
					detailedDescription == null ? null : detailedDescription.replace("\r\n", "\n"), 
					scheduledStart, 
					scheduledEnd, 
					dueBy, 
					priority, 
					ICalendar.ICAL_TYPE_NA,
					ICalendar.ICalClass.NA,
					reportingContact, 
					null // creationContext
				);
				newActivity.setMisc1(misc1);
				newActivity.setMisc2(misc2);
				newActivity.setMisc3(misc3);
				pm.currentTransaction().commit();
			} catch(Exception e)  {
				new ServiceException(e).log();
				try {
					pm.currentTransaction().rollback();
				} catch(Exception e0) {}
			}
		}
		return newActivity;
	}

	/**
	 * Cancel action.
	 */
	public void doCancel(
	) {
		this.getSession().setAttribute(
			this.getClass().getName(),
			null
		);
    	this.setExitAction(
    		new ObjectReference(this.getObject(), this.getApp()).getSelectObjectAction()
    	);		
	}

	/**
	 * Refresh action.
	 * 
	 * @param selectedObjectXri
	 * @throws ServiceException
	 */
	public void doRefresh(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,
		@FormParameter(forms = "CustomerCareSearchContactForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		ApplicationContext app = this.getApp();
		RefObject_1_0 obj = this.getObject();
		org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, this.getProviderName(), this.getSegmentName());
		this.wizardState = null;
		try {
			this.wizardState = (WizardState)this.getSession().getAttribute(this.getClass().getName());
		} catch(Exception e) {}
		if(this.wizardState == null) {
			this.getSession().setAttribute(
				this.getClass().getName(),
				this.wizardState = new WizardState(
					activitySegment,
					app
				)
			);
		}
		this.showMaxContacts = 200;
		if(obj instanceof ActivityGroup) {
			this.activityGroup = (ActivityGroup)obj;
		} else if(obj instanceof org.opencrx.kernel.account1.jmi1.Account) {
			this.account = (org.opencrx.kernel.account1.jmi1.Account)obj;
		}
		this.forEditing = true;
	}

	/**
	 * BackToSearchContact action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doBackToSearchContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,
		@FormParameter(forms = "CustomerCareSearchContactForm") Map<String,Object> formFields		
	) throws ServiceException {
		this.forward(
			Command.SearchContact.name(), 
			this.getRequest().getParameterMap()
		);
	}

	/**
	 * BackToContact action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doBackToContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "ContactForm") Map<String,Object> formFields		
	) throws ServiceException  {
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		Map<String,String[]> parameterMap = new HashMap<String,String[]>(this.getRequest().getParameterMap());
		parameterMap.put(
			PARAMETER_SELECTED_OBJECT_XRI, 
			new String[]{this.wizardState.getSelectedContactIdentity().toXRI()}
		);
		this.forward(
			Command.SelectContact.name(), 
			parameterMap
		);
	}

	/**
	 * SearchContact action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSearchContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareSearchContactForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		String firstName = (String)formFields.get("org:opencrx:kernel:account1:Contact:firstName");
		String lastName = (String)formFields.get("org:opencrx:kernel:account1:Contact:lastName");
		String aliasName = (String)formFields.get("org:opencrx:kernel:account1:Account:aliasName");
		String eMailBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
		String phoneBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull");
		if(
			(firstName != null && !firstName.isEmpty()) ||
			(lastName != null && !lastName.isEmpty()) ||
			(aliasName != null && !aliasName.isEmpty()) ||
			(eMailBusiness != null && !eMailBusiness.isEmpty()) ||				
			(phoneBusiness != null && !phoneBusiness.isEmpty())						
		) {
			if(this.account != null) {
				MemberQuery memberQuery = (MemberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Member.class);
				memberQuery.orderByName().ascending();
				if(
					(firstName != null && !firstName.isEmpty()) ||
					(lastName != null && !lastName.isEmpty())
				) {
					memberQuery.thereExistsAccount().thereExistsFullName().like(
						"(?i)" +
						(lastName !=  null && !lastName.isEmpty() ? lastName + WILCARD : "") +
						(firstName !=  null && !firstName.isEmpty() ? WILCARD + firstName + WILCARD : "")
					);						
				}
				if(aliasName != null && !aliasName.isEmpty()) {
					memberQuery.thereExistsAccount().thereExistsAliasName().like("(?i)" + WILCARD + aliasName + WILCARD);						
				}
				memberQuery.thereExistsAccount().thereExistsAssignedActivity().thereExistsProcessState().elementOf(this.wizardState.getAllowedProcessStates());
				{
					org.openmdx.base.persistence.cci.PersistenceHelper.setClasses(
						memberQuery.thereExistsAccount(), 
						org.opencrx.kernel.account1.jmi1.Contact.class
					);
				}
				if(phoneBusiness != null && !phoneBusiness.isEmpty()) {
					PhoneNumberQuery query = (PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
					query.thereExistsPhoneNumberFull().like(WILCARD + phoneBusiness + WILCARD);
					memberQuery.thereExistsAccount().thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
			    }
			    if(eMailBusiness != null && !eMailBusiness.isEmpty()) {
			    	EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
			    	query.thereExistsEmailAddress().like(WILCARD + eMailBusiness + WILCARD);
					memberQuery.thereExistsAccount().thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));						    	
			    }
				this.matchingContacts = this.account.getMember(memberQuery);
			} else if(this.activityGroup != null) {
				ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
				if(
					(firstName != null && !firstName.isEmpty()) ||
					(lastName != null && !lastName.isEmpty())
				) {
					activityQuery.thereExistsReportingContact().thereExistsFullName().like(
						"(?i)" +
						(lastName !=  null && !lastName.isEmpty() ? lastName + WILCARD : "") +
						(firstName !=  null && !firstName.isEmpty() ? WILCARD + firstName + WILCARD : "")
					);						
				}
				if(aliasName != null && !aliasName.isEmpty()) {
					activityQuery.thereExistsReportingContact().thereExistsAliasName().like("(?i)" + WILCARD + aliasName + WILCARD);						
				}
				activityQuery.thereExistsProcessState().elementOf(this.wizardState.getAllowedProcessStates());
			    if(phoneBusiness != null && !phoneBusiness.isEmpty()) {
					PhoneNumberQuery query = (PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
					query.thereExistsPhoneNumberFull().like(WILCARD + phoneBusiness + WILCARD);
					activityQuery.thereExistsReportingContact().thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
			    }
			    if(eMailBusiness != null && !eMailBusiness.isEmpty()) {
			    	EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
			    	query.thereExistsEmailAddress().like(WILCARD + eMailBusiness + WILCARD);
			    	activityQuery.thereExistsReportingContact().thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
			    }
				this.matchingContacts = this.activityGroup.getFilteredActivity(activityQuery);						
			} else {
				ContactQuery contactQuery = (ContactQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Contact.class);
				contactQuery.orderByFullName().ascending();
				if(
					(firstName != null && !firstName.isEmpty()) ||
					(lastName != null && !lastName.isEmpty())
				) {
					contactQuery.thereExistsFullName().like(
						"(?i)" +
						(lastName !=  null && !lastName.isEmpty() ? lastName + WILCARD : "") +
						(firstName !=  null && !firstName.isEmpty() ? WILCARD + firstName + WILCARD : "")
					);
				}
				if(aliasName != null && !aliasName.isEmpty()) {
					contactQuery.thereExistsAliasName().like("(?i)" + WILCARD + aliasName + WILCARD);						
				}
				contactQuery.thereExistsAssignedActivity().thereExistsProcessState().elementOf(this.wizardState.getAllowedProcessStates());
			    if(phoneBusiness != null && !phoneBusiness.isEmpty()) {
					PhoneNumberQuery query = (PhoneNumberQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.PhoneNumber.class);
					query.thereExistsPhoneNumberFull().like(WILCARD + phoneBusiness + WILCARD);
					contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
			    }
			    if(eMailBusiness != null && !eMailBusiness.isEmpty()) {
			    	EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
			    	query.thereExistsEmailAddress().like(WILCARD + eMailBusiness + WILCARD);
			    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
			    }
				this.matchingContacts = Accounts.getInstance().getAccountSegment(
					pm, 
					this.getProviderName(), 
					this.getSegmentName()
				).getAccount(contactQuery);
			}
		}
		// By default list all contacts of activity group.
		// Order by activity modification date descending.
		else if(this.activityGroup != null) {
			ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
			activityQuery.orderByModifiedAt().ascending();
			activityQuery.thereExistsProcessState().elementOf(this.wizardState.getAllowedProcessStates());
			activityQuery.activityState().equalTo(Activities.ActivityState.OPEN.getValue());
			this.matchingContacts = this.activityGroup.getFilteredActivity(activityQuery);						
			this.showMaxContacts = 20;
		}
		this.forEditing = true;
	}

	/**
	 * SelectContact action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSelectContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "ContactForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		org.opencrx.kernel.account1.jmi1.Contact selectedContact = null;
		try {
			this.wizardState.setSelectedContactIdentity(new Path(selectedObjectXri));
			selectedContact = (Contact)pm.getObjectById(this.wizardState.getSelectedContactIdentity());
		} catch(Exception e) {}
		if(selectedContact != null) {
			formFields.clear();
			formFields.put("org:opencrx:kernel:account1:Contact:salutationCode", selectedContact.getSalutationCode());
			formFields.put("org:opencrx:kernel:account1:Contact:salutation", selectedContact.getSalutation());
			formFields.put("org:opencrx:kernel:account1:Contact:firstName", selectedContact.getFirstName());
			formFields.put("org:opencrx:kernel:account1:Contact:middleName", selectedContact.getMiddleName());
			formFields.put("org:opencrx:kernel:account1:Contact:lastName", selectedContact.getLastName());
			formFields.put("org:opencrx:kernel:account1:Account:aliasName", selectedContact.getAliasName());
			formFields.put("org:opencrx:kernel:account1:Contact:jobTitle", selectedContact.getJobTitle());
			formFields.put("org:opencrx:kernel:account1:Contact:jobRole", selectedContact.getJobRole());
			formFields.put("org:opencrx:kernel:account1:Contact:organization", selectedContact.getOrganization());
			formFields.put("org:opencrx:kernel:account1:Contact:department", selectedContact.getDepartment());
			formFields.put("org:opencrx:kernel:account1:Contact:doNotPhone", selectedContact.isDoNotPhone());
			formFields.put("org:opencrx:kernel:account1:Contact:birthdate", selectedContact.getBirthdate());
			formFields.put("org:opencrx:kernel:account1:Account:description", selectedContact.getDescription());
		    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(selectedContact);
		    if(addresses[Accounts.PHONE_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.MOBILE] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.PHONE_HOME] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_HOME]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.MAIL_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress());
		    }
		    if(addresses[Accounts.MAIL_HOME] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_HOME]).getEmailAddress());
		    }
		    if(addresses[Accounts.POSTAL_HOME] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!postalAddressLine", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalAddressLine()));
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!postalStreet", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalStreet()));
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!postalCity", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCity());
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCode());
		    	formFields.put("org:opencrx:kernel:account1:Contact:address!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCountry());
		    }
		    if(addresses[Accounts.POSTAL_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine()));
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalStreet", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet()));
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCity", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCity());
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCode());
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCountry());
		    }
		}
		this.forEditing = false;
	}

	/**
	 * LockContact action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doLockContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "ContactForm") Map<String,Object> formFields		
	) throws ServiceException  {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		if(this.wizardState.getSelectedContactIdentity() != null) {
			Contact contact = (Contact)pm.getObjectById(this.wizardState.getSelectedContactIdentity());
			try {
				this.lockContact(contact);
			} catch(Exception e) {
				try {
					pm.currentTransaction().rollback();
				} catch(Exception e1) {}
				new ServiceException(e).log();
			}
			Map<String,String[]> parameterMap = new HashMap<String,String[]>(this.getRequest().getParameterMap());
			parameterMap.put(
				PARAMETER_SELECTED_OBJECT_XRI, 
				new String[]{this.wizardState.getSelectedContactIdentity().toXRI()}
			);
			this.forward(
				Command.SelectContact.name(), 
				parameterMap
			);			
		}		
	}

	/**
	 * NewActivityContact action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doNewActivityContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareCreateActivityForm") Map<String,Object> formFields		
	) throws ServiceException  {
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		if(selectedObjectXri != null) {
			this.wizardState.setSelectedActivityCreatorIdentity(
				new Path(selectedObjectXri)
			);
			this.forEditing = true;
		}
	}

	/**
	 * NewActivity action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doNewActivity(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareCreateActivityForm") Map<String,Object> formFields		
	) throws ServiceException  {
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		if(selectedObjectXri != null) {
			this.wizardState.setSelectedActivityCreatorIdentity(
				new Path(selectedObjectXri)
			);
			this.forEditing = true;
		}
	}

	/**
	 * SaveAsNewActivityContact action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSaveAsNewActivityContact(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareCreateActivityForm") Map<String,Object> formFields		
	) throws ServiceException  {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		Activity newActivity = this.createActivity(
			this.wizardState.getSelectedContactIdentity() == null
				? null
				: (Contact)pm.getObjectById(this.wizardState.getSelectedContactIdentity()
			)
		);
		Command command = null;
		Map<String,String[]> parameterMap = new HashMap<String,String[]>(this.getRequest().getParameterMap());
		if(newActivity != null) {
			this.wizardState.setSelectedActivityIdentity(newActivity.refGetPath());
			parameterMap.put(
				PARAMETER_SELECTED_OBJECT_XRI, 
				new String[]{this.wizardState.getSelectedActivityIdentity().toXRI()}
			);
			command = Command.SelectActivity;
		} else {
			parameterMap.put(
				PARAMETER_SELECTED_OBJECT_XRI, 
				new String[]{this.wizardState.getSelectedContactIdentity().toXRI()}
			);
			command = Command.SelectContact;
		}
		this.forward(
			command.name(), 
			parameterMap
		);
	}

	/**
	 * Save as new activity action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSaveAsNewActivity(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareCreateActivityForm") Map<String,Object> formFields		
	) throws ServiceException  {
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		Activity newActivity = this.createActivity(null);
		Command command = null;
		if(newActivity != null) {
			this.wizardState.setSelectedActivityIdentity(newActivity.refGetPath());
			command = Command.SearchContact;
		} else {
			command = Command.BackToSearchContact;
		}		
		Map<String,String[]> parameterMap = new HashMap<String,String[]>(this.getRequest().getParameterMap());
		parameterMap.put(
			PARAMETER_SELECTED_OBJECT_XRI, 
			new String[]{this.wizardState.getSelectedContactIdentity().toXRI()}
		);
		this.forward(
			command.name(), 
			parameterMap
		);
	}

	/**
	 * Select activity action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSelectActivity(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareShowActivityForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		Activity selectedActivity = null;
		try {
			this.wizardState.setSelectedActivityIdentity(new Path(selectedObjectXri));
			selectedActivity = (Activity)pm.getObjectById(this.wizardState.getSelectedActivityIdentity());
		} catch(Exception e) {}
		if(selectedActivity != null) {
			formFields.put("org:opencrx:kernel:activity1:Activity:activityNumber", selectedActivity.getActivityNumber());
			formFields.put("org:opencrx:kernel:activity1:Activity:name", selectedActivity.getName());
			formFields.put("org:opencrx:kernel:activity1:Activity:lastAppliedCreator", selectedActivity.getLastAppliedCreator());
			formFields.put("org:opencrx:kernel:activity1:Activity:reportingContact", selectedActivity.getReportingContact());
			formFields.put("org:opencrx:kernel:activity1:Activity:reportingAccount", selectedActivity.getReportingAccount());
			formFields.put("org:opencrx:kernel:activity1:Activity:processState", selectedActivity.getProcessState());
			formFields.put("org:opencrx:kernel:activity1:Activity:priority", selectedActivity.getPriority());
			formFields.put("org:opencrx:kernel:activity1:Activity:dueBy", selectedActivity.getDueBy());
			formFields.put("org:opencrx:kernel:activity1:Activity:scheduledStart", selectedActivity.getScheduledStart());
			formFields.put("org:opencrx:kernel:activity1:Activity:scheduledEnd", selectedActivity.getScheduledEnd());
			formFields.put("org:opencrx:kernel:activity1:Activity:misc1", selectedActivity.getMisc1());
			formFields.put("org:opencrx:kernel:activity1:Activity:misc2", selectedActivity.getMisc2());
			formFields.put("org:opencrx:kernel:activity1:Activity:misc3", selectedActivity.getMisc3());
			formFields.put("org:opencrx:kernel:activity1:Activity:description", selectedActivity.getDescription());
			formFields.put("org:opencrx:kernel:activity1:Activity:detailedDescription", selectedActivity.getDetailedDescription());						
		}
		this.forEditing = false;
	}

	/**
	 * FollowUp action.
	 * 
	 * @param selectedObjectXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doDoFollowUp(
		@RequestParameter(name = "selectedObjectXri") String selectedObjectXri,		
		@FormParameter(forms = "CustomerCareCreateFollowUpForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(selectedObjectXri, formFields);
		this.formFields = formFields;
		ActivityProcessTransition transition = (ActivityProcessTransition)pm.getObjectById(new Path(selectedObjectXri));
		String followUpTitle = (String)formFields.get("org:opencrx:kernel:base:Note:title");
		String followUpText = (String)formFields.get("org:opencrx:kernel:base:Note:text");
		if(
			this.wizardState.getSelectedActivityIdentity() != null &&
			transition != null
		) {
			ActivityDoFollowUpParams params = Structures.create(
				ActivityDoFollowUpParams.class,
				Datatypes.member(ActivityDoFollowUpParams.Member.followUpText, followUpText == null ? null : followUpText.replace("\r\n", "\n")),
				Datatypes.member(ActivityDoFollowUpParams.Member.followUpTitle, followUpTitle),
				Datatypes.member(ActivityDoFollowUpParams.Member.transition, transition)
			);
			try {
				pm.currentTransaction().begin();
				Activity activity = (Activity)pm.getObjectById(this.wizardState.getSelectedActivityIdentity());
				activity.doFollowUp(params);
				pm.currentTransaction().commit();
			} catch(Exception e) {
				try {
					new ServiceException(e).log();
					pm.currentTransaction().rollback();
				} catch(Exception e0) {}
			}
		}
		Map<String,String[]> parameterMap = new HashMap<String,String[]>(this.getRequest().getParameterMap());
		parameterMap.put(
			PARAMETER_SELECTED_OBJECT_XRI, 
			new String[]{wizardState.getSelectedActivityIdentity().toXRI()}
		);
		this.forward(
			Command.SelectActivity.name(), 
			parameterMap
		);
	}

	/**
	 * @return the forEditing
	 */
	public boolean isForEditing() {
		return forEditing;
	}

	/**
	 * @return the formFields
	 */
	public Map<String, Object> getFormFields() {
		return formFields;
	}

	/**
	 * @return the wizardState
	 */
	public WizardState getWizardState() {
		return wizardState;
	}

	/**
	 * @return the matchingContacts
	 */
	public List<?> getMatchingContacts() {
		return matchingContacts;
	}

	/**
	 * @return the showMaxContacts
	 */
	public int getShowMaxContacts() {
		return showMaxContacts;
	}

	/**
	 * Get viewPort.
	 * 
	 * @param out
	 * @return
	 */
	public ViewPort getViewPort(
		Writer out
	) {
		if(this.viewPort == null) {
			this.view = new TransientObjectView(
				this.getFormFields(),
				this.getApp(),
				this.getObject(),
				this.getPm()
			);
			this.viewPort = ViewPortFactory.openPage(
				view,
				this.getRequest(),
				out
			);
			this.viewPort.setResourcePathPrefix("../../");		
		}
		return this.viewPort;
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.AbstractWizardController#close()
	 */
    @Override
    public void close(
    ) throws ServiceException {
	    super.close();
		if(this.viewPort != null) {
			this.viewPort.close(false);		
		}	    
    }		

    /**
     * Get short label for given field.
     * 
     * @param forClass
     * @param featureName
     * @param locale
     * @return
     * @throws ServiceException
     */
    public String getShortLabel(
    	String forClass,
    	String featureName,
    	short locale
    ) throws ServiceException {
    	return this.view == null 
    		? "NA"
    		: this.view.getShortLabel(forClass, featureName, locale);
    }

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------		
	public static final String WILCARD = ".*";
	public static final String PARAMETER_SELECTED_OBJECT_XRI = "selectedObjectXri";

	private boolean forEditing = false;
	private Map<String,Object> formFields;	
	private WizardState wizardState;
	@SuppressWarnings("rawtypes")
    private List matchingContacts = null;	
	private Account account;
	private ActivityGroup activityGroup;
	private int showMaxContacts;
	private ViewPort viewPort;
	private TransientObjectView view;
}

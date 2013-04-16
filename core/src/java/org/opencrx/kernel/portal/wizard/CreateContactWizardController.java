/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CreateContractWizardController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.AccountMembership;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.portal.EmailAddressDataBinding;
import org.opencrx.kernel.portal.PhoneNumberDataBinding;
import org.opencrx.kernel.portal.PostalAddressDataBinding;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.DataBinding;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.ViewPortFactory;
import org.openmdx.portal.servlet.view.TransientObjectView;

/**
 * CreateContactWizardController
 *
 */
public class CreateContactWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public CreateContactWizardController(
   	) {
   		super();
   	}

	/**
	 * Update contact.
	 * 
	 * @param contact
	 * @param formFields
	 * @param isAddMembershipMode
	 * @param accountMembershipXri
	 * @throws ServiceException
	 */
	protected void updateContact(
		org.opencrx.kernel.account1.jmi1.Contact contact,
		Map<String,Object> formFields,
		Boolean isAddMembershipMode,
		String accountMembershipXri
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
	    org.opencrx.kernel.portal.wizard.CreateContactWizardExtension wizardExtension = 
   	     	(org.opencrx.kernel.portal.wizard.CreateContactWizardExtension)this.getApp().getPortalExtension().getExtension(
   	     		org.opencrx.kernel.portal.wizard.CreateContactWizardExtension.class.getName()
   	     	);
		try {
		    org.opencrx.kernel.account1.jmi1.Segment accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
		    	new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", this.getProviderName(), "segment", this.getSegmentName())
		    );
		    String aliasName = (String)formFields.get("org:opencrx:kernel:account1:Account:aliasName");
		    if(aliasName == null) {
		        org.opencrx.kernel.account1.cci2.AccountQuery accountQuery =
		            (org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(Account.class);
		        accountQuery.thereExistsAliasName().greaterThanOrEqualTo("A00000000");
		        accountQuery.thereExistsAliasName().lessThanOrEqualTo("A99999999");
		        accountQuery.orderByAliasName().descending();
		        List<Account> accounts = accountSegment.getAccount(accountQuery);
		        if(accounts.isEmpty()) {
		            aliasName = "A00000000";
		        } else {
		            String lastAliasName = ((Account)accounts.iterator().next()).getAliasName();
		            String nextAccountNumber = "00000000" + (Integer.valueOf(lastAliasName.substring(1)).intValue() + 1);
		            aliasName = "A" + nextAccountNumber.substring(nextAccountNumber.length() - 8);
		        }
		        formFields.put(
	                "org:opencrx:kernel:account1:Account:aliasName",
	                aliasName
		        );
		    }
		    pm.currentTransaction().begin();
		    contact.setSalutationCode(((Number)formFields.get("org:opencrx:kernel:account1:Contact:salutationCode")).shortValue());
		    contact.setSalutation((String)formFields.get("org:opencrx:kernel:account1:Contact:salutation"));
		    contact.setFirstName((String)formFields.get("org:opencrx:kernel:account1:Contact:firstName"));
		    contact.setMiddleName((String)formFields.get("org:opencrx:kernel:account1:Contact:middleName"));
		    contact.setLastName((String)formFields.get("org:opencrx:kernel:account1:Contact:lastName"));
		    contact.setAliasName(aliasName);
		    contact.setJobTitle((String)formFields.get("org:opencrx:kernel:account1:Contact:jobTitle"));
		    contact.setJobRole((String)formFields.get("org:opencrx:kernel:account1:Contact:jobRole"));
		    contact.setOrganization((String)formFields.get("org:opencrx:kernel:account1:Contact:organization"));
		    contact.setDepartment((String)formFields.get("org:opencrx:kernel:account1:Contact:department"));
		    contact.setDoNotPhone((Boolean)formFields.get("org:opencrx:kernel:account1:Contact:doNotPhone"));
		    contact.setBirthdate((Date)formFields.get("org:opencrx:kernel:account1:Contact:birthdate"));
		    contact.setDescription((String)formFields.get("org:opencrx:kernel:account1:Account:description"));
		    if(!javax.jdo.JDOHelper.isPersistent(contact)) {
			    accountSegment.addAccount(
			        Base.getInstance().getUidAsString(),
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
	    // Update Addresses
	    try {
		    pm.currentTransaction().begin();
		    // Phone Business
		    DataBinding phoneBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)500;automaticParsing=(boolean)true");
		    phoneBusinessDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull")
		    );
		    // Phone Mobile
		    DataBinding phoneMobileDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)200;automaticParsing=(boolean)true");
		    phoneMobileDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull")
		    );
		    // Phone Home
		    DataBinding phoneHomeDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)400;automaticParsing=(boolean)true");
		    phoneHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!phoneNumberFull",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!phoneNumberFull")
		    );
		    // Mail Business
		    String emailBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
		    if (emailBusiness != null) {emailBusiness = emailBusiness.trim();}
		    DataBinding mailBusinessDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)500;[emailType=(short)1]");
		    mailBusinessDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!emailAddress",
		        emailBusiness
		    );
		    // Mail Home
		    String emailHome = (String)formFields.get("org:opencrx:kernel:account1:Contact:address!emailAddress");
		    if (emailHome != null) {emailHome = emailHome.trim();}
		    DataBinding mailHomeDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)400;[emailType=(short)1]");
		    mailHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!emailAddress",
		        emailHome
		    );
		    // Postal Home
		    DataBinding postalHomeDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)400?zeroAsNull=true");
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalAddressLine",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!postalAddressLine")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalStreet",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!postalStreet")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCity",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!postalCity")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
				"org:opencrx:kernel:account1:Contact:address!postalState",
				formFields.get("org:opencrx:kernel:account1:Contact:address!postalState")
		    );
		    postalHomeDataBinding.setValue(
		    	contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCode",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!postalCode")
		    );
		    postalHomeDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Contact:address!postalCountry",
		        formFields.get("org:opencrx:kernel:account1:Contact:address!postalCountry")
		    );
		    // Postal Business
		    DataBinding postalBusinesDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)500?zeroAsNull=true");
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalAddressLine",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalStreet",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCity",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCity")
		    );
		    postalBusinesDataBinding.setValue(
		    		contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalState",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalState")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCode",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCode")
		    );
		    postalBusinesDataBinding.setValue(
		        contact,
		        "org:opencrx:kernel:account1:Account:address*Business!postalCountry",
		        formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCountry")
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
	    if(Boolean.TRUE.equals(isAddMembershipMode) || accountMembershipXri != null) {
			// create/update data for membership
			try {
				Account parentAccount = null;
				org.opencrx.kernel.account1.jmi1.Member member = null;
				if(accountMembershipXri != null && !accountMembershipXri.isEmpty()) {
					AccountMembership accountMembership = 
						(AccountMembership)pm.getObjectById(new Path(accountMembershipXri));
					parentAccount = accountMembership.getAccountFrom();
					member = accountMembership.getMember();
				} else {
					// create new membership
					// get parent account
					try {
						parentAccount = formFields.get("org:opencrx:kernel:account1:AccountAssignment:account") != null 
							? (Account)pm.getObjectById(
								formFields.get("org:opencrx:kernel:account1:AccountAssignment:account")
							  ) 
							: null;
					} catch (Exception e) {}
					if (parentAccount != null) {
						// create new member
						try {
							pm.currentTransaction().begin();
							member = pm.newInstance(org.opencrx.kernel.account1.jmi1.Member.class);
							member.setValidFrom(new java.util.Date());
							member.setQuality((short)5);
							member.setName(contact.getFullName());
							member.setAccount(contact);
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
				if(member != null && formFields.get("org:opencrx:kernel:account1:Member:memberRole") != null) {
					// update member roles
					try {
						@SuppressWarnings("unchecked")
                        List<Short> memberRole = (List<Short>)formFields.get("org:opencrx:kernel:account1:Member:memberRole");
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
	}

	/**
	 * OK action.
	 * 
	 * @param isAddMembershipMode
	 * @param accountMembershipXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doOK(
   		@RequestParameter(name = "isAddMembershipMode") Boolean isAddMembershipMode,
   	   	@RequestParameter(name = "accountMembershipXri") String accountMembershipXri,     				
   		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
	) throws ServiceException {
		this.doRefresh(formFields);
		RefObject_1_0 obj = getObject();
		if(obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
			this.updateContact(
				(org.opencrx.kernel.account1.jmi1.Contact)obj, 
				formFields,
				isAddMembershipMode,
				accountMembershipXri
			);
		}
	    formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
	    formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
	}
		
	/**
	 * Create action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doCreate(
   		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = getPm();
		this.doRefresh(formFields);
		this.contact = pm.newInstance(org.opencrx.kernel.account1.jmi1.Contact.class);
		this.updateContact(
			this.contact,
			formFields,
			null,
			null
		);
	    formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
	    formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.AbstractWizardController#initFormFields(java.util.Map)
	 */
	@Override 
	protected void initFormFields(
		Map<String,Object> formFields
	) throws ServiceException {
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
		    org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)obj;
		    formFields.put("org:opencrx:kernel:account1:Contact:salutationCode", contact.getSalutationCode());
		    formFields.put("org:opencrx:kernel:account1:Contact:salutation", contact.getSalutation());
		    formFields.put("org:opencrx:kernel:account1:Contact:firstName", contact.getFirstName());
		    formFields.put("org:opencrx:kernel:account1:Contact:middleName", contact.getMiddleName());
		    formFields.put("org:opencrx:kernel:account1:Contact:lastName", contact.getLastName());
		    formFields.put("org:opencrx:kernel:account1:Account:aliasName", contact.getAliasName());
		    formFields.put("org:opencrx:kernel:account1:Contact:jobTitle", contact.getJobTitle());
		    formFields.put("org:opencrx:kernel:account1:Contact:jobRole", contact.getJobRole());
		    formFields.put("org:opencrx:kernel:account1:Contact:organization", contact.getOrganization());
		    formFields.put("org:opencrx:kernel:account1:Contact:department", contact.getDepartment());
		    formFields.put("org:opencrx:kernel:account1:Contact:doNotPhone", contact.isDoNotPhone());
		    formFields.put("org:opencrx:kernel:account1:Contact:birthdate", contact.getBirthdate());
		    formFields.put("org:opencrx:kernel:account1:Account:description", contact.getDescription());
		    AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(contact);
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
			    formFields.put("org:opencrx:kernel:account1:Contact:address!postalState", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalState());
			    formFields.put("org:opencrx:kernel:account1:Contact:address!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCode());
			    formFields.put("org:opencrx:kernel:account1:Contact:address!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_HOME]).getPostalCountry());
		    }
		    if(addresses[Accounts.POSTAL_BUSINESS] != null) {
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine()));
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalStreet", new ArrayList<String>(((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet()));
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCity", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCity());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalState", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalState());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCode", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCode());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCountry", ((org.opencrx.kernel.account1.jmi1.PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCountry());
		    }
   			// Allows rendering to determine the object type (e.g. required for code fields)
   			formFields.put(
   				org.openmdx.base.accessor.cci.SystemAttributes.OBJECT_CLASS, 
   				"org:opencrx:kernel:account1:Contact"
   			);
		}
	}

	/**
	 * Refresh action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doRefresh(
   		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
   	) throws ServiceException {
		this.formFields = formFields;
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
			this.contact = (org.opencrx.kernel.account1.jmi1.Contact)obj;
		}
	}

	/**
	 * Cancel action.
	 * 
	 * @throws ServiceException
	 */
	public void doCancel(
	) throws ServiceException {
		this.setExitAction(
			new ObjectReference(getObject(), getApp()).getSelectObjectAction()
		);
	}

	/**
	 * Search action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doSearch(
   		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields   				
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(formFields);   			
	    boolean hasQueryProperty = false;
	    org.opencrx.kernel.account1.cci2.ContactQuery contactQuery =
	        (org.opencrx.kernel.account1.cci2.ContactQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Contact.class);
		contactQuery.orderByFullName().ascending();
	    String firstName = (String)formFields.get("org:opencrx:kernel:account1:Contact:firstName");
	    String lastName = (String)formFields.get("org:opencrx:kernel:account1:Contact:lastName");
	    String aliasName = (String)formFields.get("org:opencrx:kernel:account1:Account:aliasName");
	    String phoneNumberMobile = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull");
	    String phoneNumberHome = (String)formFields.get("org:opencrx:kernel:account1:Contact:address!phoneNumberFull");
	    String phoneNumberBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull");
	    String postalCityHome = (String)formFields.get("org:opencrx:kernel:account1:Contact:address!postalCity");
	    String postalCityBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCity");
	    List<String> postalStreetHome = null;
	    try {
	    	if (formFields.get("org:opencrx:kernel:account1:Contact:address!postalStreet") != null) {
				if (formFields.get("org:opencrx:kernel:account1:Contact:address!postalStreet") instanceof List) {
					@SuppressWarnings("unchecked")
                    List<String> values = (List<String>)formFields.get("org:opencrx:kernel:account1:Contact:address!postalStreet");
					postalStreetHome = values;
				} else {
					String postalStreet = ((String)formFields.get("org:opencrx:kernel:account1:Contact:address!postalStreet")).replace("\n\r", "\n");
					postalStreetHome = Arrays.asList(postalStreet.split("\n"));
				}
	    	}
	    } catch (Exception e) {}
	    List<String> postalStreetBusiness = null;
	    try {
	    	if (formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet") != null) {
				if (formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet") instanceof List) {
					@SuppressWarnings("unchecked")
                    List<String> values = (List<String>)formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet");
					postalStreetBusiness = values;
				} else {
					String postalStreet = ((String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")).replace("\n\r", "\n");
					postalStreetBusiness = Arrays.asList(postalStreet.split("\n"));
				}
	    	}
	    } catch (Exception e) {}
	    String emailHome = (String)formFields.get("org:opencrx:kernel:account1:Contact:address!emailAddress");
	    String emailBusiness = (String)formFields.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
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
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE ((adr.postal_street_0" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_1" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_2" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_3" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_4" + " LIKE ?s" + stringParamIndex + ")))";
		        stringParams.add(wildcard + postalStreetHome.get(i) + wildcard);
		        stringParamIndex++;
	        }
	    }
	    if(postalStreetBusiness != null) {
	        for(int i = 0; i < postalStreetBusiness.size(); i++) {
		        hasQueryProperty = true;
		        if(stringParamIndex > 0) { queryFilterClause += " AND "; } else { queryFilterClause = ""; }
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE ((adr.postal_street_0" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_1" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_2" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_3" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_4" + " LIKE ?s" + stringParamIndex + ")))";
		        stringParams.add(wildcard + postalStreetBusiness.get(i) + wildcard);
		        stringParamIndex++;
	        }
	    }
	    if(emailHome != null) {
	    	emailHome = emailHome.trim();
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like(wildcard + emailHome + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(emailBusiness != null) {
	    	emailBusiness = emailBusiness.trim();
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like(wildcard + emailBusiness + wildcard);
	    	contactQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(queryFilterClause != null) {
	    	org.openmdx.base.query.Extension queryFilter = org.openmdx.base.persistence.cci.PersistenceHelper.newQueryExtension(contactQuery);
		    queryFilter.setClause(queryFilterClause);
		    queryFilter.getStringParam().addAll(stringParams);
	    }
	    org.opencrx.kernel.account1.jmi1.Segment accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
	    	new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", this.getProviderName(), "segment", this.getSegmentName())
	    );
	   	this.matchingContacts = hasQueryProperty ?
	        accountSegment.getAccount(contactQuery) :
	        null;
	}

	/**
	 * DisableMembership action.
	 * 
	 * @param memberXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doDisableMembership( 				
		@RequestParameter(name = "Para0") String memberXri,
		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(formFields);
		try {
			pm.currentTransaction().begin();
			org.opencrx.kernel.account1.jmi1.Member member = (org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(memberXri));
			member.setDisabled(true);
			pm.currentTransaction().commit();
		} catch (Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new ServiceException(e);
		}   			
	}

	/**
	 * AddMembership action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doAddMembership(
		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
	) throws ServiceException {
		this.doRefresh(formFields);
		this.isAddMembershipMode = true;
		formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", Arrays.asList(new Short[]{(short)11}));   			
	}

	/**
	 * EditMembership action.
	 * 
	 * @param accountMembershipXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doEditMembership(
   		@RequestParameter(name = "accountMembershipXri") String accountMembershipXri,   				
		@FormParameter(forms = {"ContactForm", "MembershipForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();   			
		this.doRefresh(formFields);
		this.accountMembershipXri = accountMembershipXri;
		if(accountMembershipXri != null && !accountMembershipXri.isEmpty()) {
			AccountMembership accountMembership = null;
			try {
				accountMembership = (AccountMembership)pm.getObjectById(new Path(accountMembershipXri));
				this.formFields.put(
					"org:opencrx:kernel:account1:AccountAssignment:account", 
					accountMembership.getAccountFrom().refGetPath()
				);
			} catch (Exception ignore) {}
			if(accountMembership != null) {
				this.formFields.put(
					"org:opencrx:kernel:account1:Member:memberRole", 
					new ArrayList<Short>(accountMembership.getMember().getMemberRole())
				);
			}
		}
	}

	/**
	 * Get form values.
	 * 
	 * @return
	 */
	public Map<String,Object> getFormFields(
	) {
		return this.formFields;
	}
	
	/**
	 * Get error message.
	 * 
	 * @return
	 */
	public String getErrorMsg(
	) {
		return this.errorMsg;
	}
	
	/**
	 * Get error title.
	 * 
	 * @return
	 */
	public String getErrorTitle(
	) {
		return this.errorTitle;
	}
	
	/**
	 * Get XRI of selected account membership.
	 * 
	 * @return
	 */
	public String getAccountMembershipXri(
	) {
		return this.accountMembershipXri;
	}
	
	/**
	 * Get contacts selected by doSearch action.
	 * 
	 * @return
	 */
	public List<Account> getMachingContacts(
	) {
		return this.matchingContacts;
	}
	
	/**
	 * Return true if in add membership mode.
	 * 
	 * @return
	 */
	public Boolean getIsAddMembershipMode(
	) {
		return this.isAddMembershipMode;
	}
	
	/**
	 * Get current contact.
	 * 
	 * @return
	 */
	public Contact getContact(
	) {
		return this.contact;
	}
	
	/**
	 * Get view port.
	 * 
	 * @param out
	 * @return
	 */
	public ViewPort getViewPort(
		Writer out
	) {
		if(this.viewPort == null) {
			TransientObjectView view = new TransientObjectView(
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

	//-------------------------------------------------------------------
	// Members
	//-------------------------------------------------------------------
    private String errorMsg = "";
	private String errorTitle = "";
	private Map<String,Object> formFields;
	private Boolean isAddMembershipMode;
	private org.opencrx.kernel.account1.jmi1.Contact contact;
	private String accountMembershipXri;
	private List<Account> matchingContacts;
	private ViewPort viewPort;

}

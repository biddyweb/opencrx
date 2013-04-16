/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CreateLegalEntityWizardController
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
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.LegalEntity;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
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
import org.openmdx.portal.servlet.databinding.CompositeObjectDataBinding;
import org.openmdx.portal.servlet.view.TransientObjectView;

/**
 * CreateLegalEntityWizardController
 *
 */
public class CreateLegalEntityWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public CreateLegalEntityWizardController(
   	) {
   		super();
   	}

	/**
	 * Update legal entity.
	 * 
	 * @param legalEntity
	 * @param formFields
	 * @param isAddMemberMode
	 * @param memberXri
	 * @throws ServiceException
	 */
	protected void updateLegalEntity(
		LegalEntity legalEntity,
		Map<String,Object> formFields,
		Boolean isAddMemberMode,
		String memberXri
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		try {
			org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, this.getProviderName(), this.getSegmentName());
			String aliasName = (String)this.formFields.get("org:opencrx:kernel:account1:Account:aliasName");
			if(aliasName == null) {
				org.opencrx.kernel.account1.cci2.AccountQuery accountQuery =
				    (org.opencrx.kernel.account1.cci2.AccountQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.Account.class);
				accountQuery.thereExistsAliasName().greaterThanOrEqualTo("A00000000");
				accountQuery.thereExistsAliasName().lessThanOrEqualTo("A99999999");
				accountQuery.orderByAliasName().descending();
				List<Account> accounts = accountSegment.getAccount(accountQuery);
				if(accounts.isEmpty()) {
				    aliasName = "A00000000";
				} else {
				    String lastAliasName = ((org.opencrx.kernel.account1.jmi1.Account)accounts.iterator().next()).getAliasName();
				    String nextAccountNumber = "00000000" + (Integer.valueOf(lastAliasName.substring(1)).intValue() + 1);
				    aliasName = "A" + nextAccountNumber.substring(nextAccountNumber.length() - 8);
				}
				this.formFields.put(
			        "org:opencrx:kernel:account1:Account:aliasName",
			        aliasName
				);
			}
			pm.currentTransaction().begin();
			legalEntity.setName((String)this.formFields.get("org:opencrx:kernel:account1:AbstractGroup:name"));
			legalEntity.setAliasName(aliasName);
			if(this.formFields.get("org:opencrx:kernel:account1:LegalEntity:industry") != null) {
				legalEntity.setIndustry(((Number)this.formFields.get("org:opencrx:kernel:account1:LegalEntity:industry")).shortValue());
    		}
			legalEntity.setDescription((String)this.formFields.get("org:opencrx:kernel:account1:Account:description"));
			if(!JDOHelper.isPersistent(legalEntity)) {
				accountSegment.addAccount(
					Base.getInstance().getUidAsString(),
					legalEntity
				);
			}
			pm.currentTransaction().commit();
		} catch (Exception e) {
			new ServiceException(e).log();
			this.errorMsg = "ERROR - cannot create/save legal entity";
			Throwable err = e;
			while (err.getCause() != null) {
				err = err.getCause();
			}
			this.errorTitle += "<pre>" + err + "</pre>";
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
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull")
			);
			// Phone Mobile
			DataBinding phoneMobileDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)200;automaticParsing=(boolean)true");
			phoneMobileDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull")
			);
			// Phone Other
			DataBinding phoneOtherDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)1800;automaticParsing=(boolean)true");
			phoneOtherDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull")
			);
			// Fax Business
			DataBinding faxBusinessDataBinding = new PhoneNumberDataBinding("[isMain=(boolean)true];usage=(short)530;automaticParsing=(boolean)true");
			faxBusinessDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull")
			);
			// Mail Business
			String emailBusiness = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
		    if (emailBusiness != null) {emailBusiness = emailBusiness.trim();}
		    DataBinding mailBusinessDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)500;[emailType=(short)1]");
			mailBusinessDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!emailAddress",
				emailBusiness
			);
		    // Mail Other
		    String emailOther = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Other!emailAddress");
		    if (emailOther != null) {emailOther = emailOther.trim();}
		    DataBinding mailOtherDataBinding = new EmailAddressDataBinding("[isMain=(boolean)true];usage=(short)1800;[emailType=(short)1]");
		    mailOtherDataBinding.setValue(
		    	legalEntity,
		        "org:opencrx:kernel:account1:Account:address*Other!emailAddress",
		        emailOther
		    );
		    // Web Business
		    DataBinding webBusinessDataBinding = new CompositeObjectDataBinding("type=org:opencrx:kernel:account1:WebAddress;disabled=(boolean)false;[isMain=(boolean)true];usage=(short)500");
		    webBusinessDataBinding.setValue(
		    	legalEntity,
		        "org:opencrx:kernel:account1:LegalEntity:address!webUrl",
		        this.formFields.get("org:opencrx:kernel:account1:LegalEntity:address!webUrl")
		    );
			// Postal Business
			DataBinding postalBusinesDataBinding = new PostalAddressDataBinding("[isMain=(boolean)true];usage=(short)500?zeroAsNull=true");
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalAddressLine",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine")
			);
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalStreet",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")
			);
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalCity",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCity")
			);
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalState",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalState")
			);
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalCode",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCode")
			);
			postalBusinesDataBinding.setValue(
				legalEntity,
				"org:opencrx:kernel:account1:Account:address*Business!postalCountry",
				this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCountry")
			);
			pm.currentTransaction().commit();
		} catch (Exception e) {
			new ServiceException(e).log();
			this.errorMsg = "ERROR - cannot create/save legal entity";
			Throwable err = e;
			while (err.getCause() != null) {
				err = err.getCause();
			}
			this.errorTitle += "<pre>" + err + "</pre>";
			try {
				pm.currentTransaction().rollback();
			} catch (Exception er) {}
		}
	    if(Boolean.TRUE.equals(isAddMemberMode) || memberXri != null) {
			// create/update data for member
			try {
				org.opencrx.kernel.account1.jmi1.Member member = null;
				org.opencrx.kernel.account1.jmi1.Account accountTo = null;
				try {
					accountTo = this.formFields.get("org:opencrx:kernel:account1:AccountAssignment:account") != null ?
						(org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
							this.formFields.get("org:opencrx:kernel:account1:AccountAssignment:account")
						) : null;
				} catch (Exception e) {}
				if (accountTo != null) {
					if(memberXri != null && !memberXri.isEmpty()) {
						// update existing member
						member = (org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(memberXri));
					} else {
						// create new member
						try {
							pm.currentTransaction().begin();
							member = pm.newInstance(org.opencrx.kernel.account1.jmi1.Member.class);
							member.setValidFrom(new java.util.Date());
							member.setQuality((short)5);
							member.setName((String)this.formFields.get("org:opencrx:kernel:account1:Member:name"));
							if (member.getName() == null || member.getName().length() == 0) {
								member.setName(accountTo.getFullName());
							}
							member.setAccount(accountTo);
							legalEntity.addMember(
								Base.getInstance().getUidAsString(),
								member
							);
							pm.currentTransaction().commit();
						} catch (Exception e) {
							member = null;
							new ServiceException(e).log();
							this.errorMsg = "ERROR - cannot create/save member";
							Throwable err = e;
							while (err.getCause() != null) {
								err = err.getCause();
							}
							this.errorTitle += "<pre>" + err + "</pre>";
							try {
								pm.currentTransaction().rollback();
							} catch (Exception er) {}
						}
					}
					try {
						pm.currentTransaction().begin();
						member.setAccount(accountTo);
						member.setName((String)this.formFields.get("org:opencrx:kernel:account1:Member:name"));
						if (member.getName() == null || member.getName().isEmpty()) {
							member.setName(accountTo.getFullName());
						}
						member.setDescription((String)this.formFields.get("org:opencrx:kernel:account1:Member:description"));
						pm.currentTransaction().commit();
					} catch (Exception e) {
						member = null;
						new ServiceException(e).log();
						try {
							pm.currentTransaction().rollback();
						} catch (Exception er) {}
					}
					if(member != null && this.formFields.get("org:opencrx:kernel:account1:Member:memberRole") != null) {
						// update member roles
						try {
							@SuppressWarnings("unchecked")
                            List<Short> memberRole = (List<Short>)this.formFields.get("org:opencrx:kernel:account1:Member:memberRole");
							pm.currentTransaction().begin();
							member.setMemberRole(memberRole);
							pm.currentTransaction().commit();
						} catch (Exception e) {
							member = null;
							new ServiceException(e).log();
							this.errorMsg = "ERROR - cannot create/save member";
							Throwable err = e;
							while (err.getCause() != null) {
								err = err.getCause();
							}
							this.errorTitle += "<pre>" + err + "</pre>";
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
	}

	/**
	 * OK action.
	 * 
	 * @param isAddMemberMode
	 * @param memberXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doOK(
   		@RequestParameter(name = "isAddMemberMode") Boolean isAddMemberMode,
   	   	@RequestParameter(name = "memberXri") String memberXri,     				
   		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
	) throws ServiceException {
		this.doRefresh(formFields);
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof LegalEntity) {
			this.updateLegalEntity(
				(LegalEntity)obj, 
				formFields,
				isAddMemberMode,
				memberXri
			);
		}
		formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
		formFields.put("org:opencrx:kernel:account1:Member:name", null);
		formFields.put("org:opencrx:kernel:account1:Member:description", null);
	}

	/**
	 * Create action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doCreate(
   		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = getPm();
		this.doRefresh(formFields);
		this.legalEntity = pm.newInstance(LegalEntity.class);
		this.updateLegalEntity(
			this.legalEntity,
			formFields,
			null,
			null
		);
		formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
		formFields.put("org:opencrx:kernel:account1:Member:name", null);
		formFields.put("org:opencrx:kernel:account1:Member:description", null);
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.AbstractWizardController#initFormFields(java.util.Map)
	 */
	@Override 
	protected void initFormFields(
		Map<String,Object> formFields
	) throws ServiceException {
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof LegalEntity) {
		    LegalEntity legalEntity = (LegalEntity)obj;
		    formFields.put("org:opencrx:kernel:account1:AbstractGroup:name", legalEntity.getName());
		    formFields.put("org:opencrx:kernel:account1:Account:aliasName", legalEntity.getAliasName());
		    formFields.put("org:opencrx:kernel:account1:LegalEntity:industry", legalEntity.getIndustry());
		    formFields.put("org:opencrx:kernel:account1:Account:description", legalEntity.getDescription());
		    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getInstance().getMainAddresses(legalEntity);
		    if(addresses[Accounts.PHONE_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.MOBILE] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.PHONE_OTHER] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Other!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.PHONE_OTHER]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.FAX_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*BusinessFax!phoneNumberFull", ((org.opencrx.kernel.account1.jmi1.PhoneNumber)addresses[Accounts.FAX_BUSINESS]).getPhoneNumberFull());
		    }
		    if(addresses[Accounts.MAIL_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Business!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress());
		    }
		    if(addresses[Accounts.MAIL_OTHER] != null) {
		    	formFields.put("org:opencrx:kernel:account1:Account:address*Other!emailAddress", ((org.opencrx.kernel.account1.jmi1.EMailAddress)addresses[Accounts.MAIL_OTHER]).getEmailAddress());
		    }
		    if(addresses[Accounts.WEB_BUSINESS] != null) {
		    	formFields.put("org:opencrx:kernel:account1:LegalEntity:address!webUrl", ((org.opencrx.kernel.account1.jmi1.WebAddress)addresses[Accounts.WEB_BUSINESS]).getWebUrl());
		    }
		    if(addresses[Accounts.POSTAL_BUSINESS] != null) {
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalAddressLine", new ArrayList<String>(((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalAddressLine()));
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalStreet", new ArrayList<String>(((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalStreet()));
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCity", ((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCity());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalState", ((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalState());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCode", ((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCode());
			    formFields.put("org:opencrx:kernel:account1:Account:address*Business!postalCountry", ((PostalAddress)addresses[Accounts.POSTAL_BUSINESS]).getPostalCountry());
		    }
		}
		formFields.put(
			org.openmdx.base.accessor.cci.SystemAttributes.OBJECT_CLASS, 
			"org:opencrx:kernel:account1:LegalEntity"
		);		
	}

	/**
	 * Refresh action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doRefresh(
   		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
   	) throws ServiceException {
		this.formFields = formFields;
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof LegalEntity) {
			this.legalEntity = (LegalEntity)obj;
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
   		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields   				
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(formFields);   			
	    boolean hasQueryProperty = false;
	    org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, this.getProviderName(), this.getSegmentName());
	    org.opencrx.kernel.account1.cci2.LegalEntityQuery legalEntityQuery =
	        (org.opencrx.kernel.account1.cci2.LegalEntityQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.LegalEntity.class);
	    legalEntityQuery.orderByName().ascending();
	    String name = (String)this.formFields.get("org:opencrx:kernel:account1:AbstractGroup:name");
	    String aliasName = (String)this.formFields.get("org:opencrx:kernel:account1:Account:aliasName");
	    String phoneNumberBusiness = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull");
	    String phoneNumberMobile = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull");
	    String postalCityBusiness = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalCity");
	    List<String> postalStreetBusiness = null;
	    try {
	    	if (this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet") != null) {
				if (this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet") instanceof java.util.ArrayList) {
					@SuppressWarnings("unchecked")
                    List<String> values = (List<String>)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet");
					postalStreetBusiness = values;
				} else {
					String postalStreet = ((String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!postalStreet")).replace("\n\r", "\n");
					postalStreetBusiness = Arrays.asList(postalStreet.split("\n"));
				}
	    	}
	    } catch (Exception e) {}
	    String emailBusiness = (String)this.formFields.get("org:opencrx:kernel:account1:Account:address*Business!emailAddress");
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
		        queryFilterClause += "v.object_id IN (SELECT act.object_id FROM OOCKE1_ACCOUNT act INNER JOIN OOCKE1_ADDRESS adr ON adr.p$$parent = act.object_id WHERE ((adr.postal_street_0" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_1" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_2" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_3" + " LIKE ?s" + stringParamIndex + ") OR (adr.postal_street_4" + " LIKE ?s" + stringParamIndex + ")))";
		        stringParams.add(wildcard + postalStreetBusiness.get(i) + wildcard);
		        stringParamIndex++;
	        }
	    }
	    if(emailBusiness != null) {
	    	emailBusiness = emailBusiness.trim();
	    	org.opencrx.kernel.account1.cci2.EMailAddressQuery query = (org.opencrx.kernel.account1.cci2.EMailAddressQuery)pm.newQuery(org.opencrx.kernel.account1.jmi1.EMailAddress.class);
	    	query.thereExistsEmailAddress().like("(?i)" + wildcard + emailBusiness + wildcard);
	    	legalEntityQuery.thereExistsAddress().elementOf(org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(query));
	    }
	    if(queryFilterClause != null) {
	    	org.openmdx.base.query.Extension queryFilter = org.openmdx.base.persistence.cci.PersistenceHelper.newQueryExtension(legalEntityQuery);
		    queryFilter.setClause(queryFilterClause);
		    queryFilter.getStringParam().addAll(stringParams);
	    }
	   	this.matchingLegalEntities = hasQueryProperty ?
	        accountSegment.getAccount(legalEntityQuery) :
	        null;
	}

	/**
	 * DisableMember action.
	 * 
	 * @param memberXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doDisableMember( 				
		@RequestParameter(name = "Para0") String memberXri,
		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(formFields);
		try {
			pm.currentTransaction().begin();
			Member member = (Member)pm.getObjectById(new Path(memberXri));
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
	 * AddMember action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doAddMember(
		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
	) throws ServiceException {
		this.doRefresh(formFields);
		this.isAddMemberMode = true;
		this.memberXri = null;
		formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", null);
		formFields.put("org:opencrx:kernel:account1:Member:name", null);
		formFields.put("org:opencrx:kernel:account1:Member:description", null);
		formFields.put("org:opencrx:kernel:account1:Member:memberRole", Arrays.asList(new Short[]{(short)11}));
	}

	/**
	 * EditMember action.
	 * 
	 * @param memberXri
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doEditMember(
   		@RequestParameter(name = "memberXri") String memberXri,   				
		@FormParameter(forms = {"LegalEntityForm", "MemberForm"}) Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();   			
		this.doRefresh(formFields);
		this.memberXri = memberXri;
		if(memberXri != null && !memberXri.isEmpty()) {
			org.opencrx.kernel.account1.jmi1.Member member =
				(org.opencrx.kernel.account1.jmi1.Member)pm.getObjectById(new Path(memberXri));
			this.formFields.put("org:opencrx:kernel:account1:AccountAssignment:account", member.getAccount().refGetPath());
			this.formFields.put("org:opencrx:kernel:account1:Member:memberRole", new ArrayList<Short>(member.getMemberRole()));
			this.formFields.put("org:opencrx:kernel:account1:Member:name", member.getName());
			this.formFields.put("org:opencrx:kernel:account1:Member:description", member.getDescription());
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
	public String getMemberXri(
	) {
		return this.memberXri;
	}
	
	/**
	 * Get legal entities selected by doSearch action.
	 * 
	 * @return
	 */
	public List<Account> getMachingLegalEntities(
	) {
		return this.matchingLegalEntities;
	}

	/**
	 * Return true if in add membership mode.
	 * 
	 * @return
	 */
	public Boolean getIsAddMemberMode(
	) {
		return this.isAddMemberMode;
	}
	
	/**
	 * Get current legal entity.
	 * 
	 * @return
	 */
	public LegalEntity getLegalEntity(
	) {
		return this.legalEntity;
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
	private Boolean isAddMemberMode;
	private LegalEntity legalEntity;
	private String memberXri;
	private List<Account> matchingLegalEntities;
	private ViewPort viewPort;
}

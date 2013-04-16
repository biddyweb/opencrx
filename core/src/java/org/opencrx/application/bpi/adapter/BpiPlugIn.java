/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BpiAdapterExtension
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
package org.opencrx.application.bpi.adapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.application.bpi.datatype.BpiAccountMember;
import org.opencrx.application.bpi.datatype.BpiActivity;
import org.opencrx.application.bpi.datatype.BpiActivityCreator;
import org.opencrx.application.bpi.datatype.BpiActivityFollowUp;
import org.opencrx.application.bpi.datatype.BpiAddress;
import org.opencrx.application.bpi.datatype.BpiCodeTable;
import org.opencrx.application.bpi.datatype.BpiCodeTableEntry;
import org.opencrx.application.bpi.datatype.BpiContact;
import org.opencrx.application.bpi.datatype.BpiEMailAddress;
import org.opencrx.application.bpi.datatype.BpiLocalizedField;
import org.opencrx.application.bpi.datatype.BpiObject;
import org.opencrx.application.bpi.datatype.BpiOrganization;
import org.opencrx.application.bpi.datatype.BpiParticipant;
import org.opencrx.application.bpi.datatype.BpiPhoneNumber;
import org.opencrx.application.bpi.datatype.BpiPostalAddress;
import org.opencrx.kernel.account1.cci2.ContactQuery;
import org.opencrx.kernel.account1.cci2.LegalEntityQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.LegalEntity;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery;
import org.opencrx.kernel.activity1.jmi1.AbstractActivityParty;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityFollowUp;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.IncidentParty;
import org.opencrx.kernel.activity1.jmi1.MeetingParty;
import org.opencrx.kernel.activity1.jmi1.TaskParty;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.code1.cci2.CodeValueContainerQuery;
import org.opencrx.kernel.code1.cci2.CodeValueEntryQuery;
import org.opencrx.kernel.code1.jmi1.CodeValueContainer;
import org.opencrx.kernel.code1.jmi1.CodeValueEntry;
import org.opencrx.kernel.generic.jmi1.LocalizedField;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.naming.Path;

import com.google.gson.Gson;


/**
 * BpiPlugIn
 *
 */
public class BpiPlugIn {

    /**
     * Stringify value.
     * 
     * @param pw
     * @param value
     */
	public void printObject(
    	PrintWriter pw,
    	Object value
    ) {
    	Gson gson = new Gson();
    	pw.println(gson.toJson(value));
    }

    /**
     * Parse object from string.
     * 
     * @param reader
     * @param clazz
     * @return
     */
    public <T> T parseObject(
    	Reader r,
    	Class<T> clazz
    ) {
    	Gson gson = new Gson();
    	return gson.fromJson(r, clazz);
    }

    /**
     * Find code value containers matching the given name.
     * 
     * @param path
     * @param pm
     * @return
     * @throws ServiceException
     */
    public List<CodeValueContainer> findCodeValueContainers(
    	Path path,
    	PersistenceManager pm
    ) throws ServiceException {
    	org.opencrx.kernel.code1.jmi1.Segment codeSegment = (org.opencrx.kernel.code1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.code1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	CodeValueContainerQuery codeValueContainerQuery = (CodeValueContainerQuery)pm.newQuery(CodeValueContainer.class);
    	codeValueContainerQuery.thereExistsName().equalTo(path.getBase());
    	codeValueContainerQuery.orderByCreatedAt().ascending();
    	return codeSegment.getValueContainer(codeValueContainerQuery);
    }

    /**
     * Get new instance of of BpiCodeTable.
     * 
     * @return
     */
    public BpiCodeTable newBpiCodeTable(
    ) {
    	return new BpiCodeTable();
    }

    /**
     * Get new instance of of BpiCodeTableEntry.
     * 
     * @return
     */
    public BpiCodeTableEntry newBpiCodeTableEntry(
    ) {
    	return new BpiCodeTableEntry();
    }
    
    /**
     * Get new instance of BpiActivityCreator.
     * 
     * @return
     */
    public BpiActivityCreator newBpiActivityCreator(
    ) {
    	return new BpiActivityCreator();
    }

    /**
     * Get new instance of BpiActivity.
     * 
     * @return
     */
    public BpiActivity newBpiActivity(
    ) {
    	return new BpiActivity();
    }

    /**
     * Get new instance of BpiActivityFollowUp.
     * 
     * @return
     */
    public BpiActivityFollowUp newBpiActivityFollowUp(
    ) {
    	return new BpiActivityFollowUp();
    }

    /**
     * Get new instance of BpiEMailAddress.
     * 
     * @return
     */
    public BpiEMailAddress newBpiEMailAddress(
    ) {
    	return new BpiEMailAddress();
    }

    /**
     * Get new instance of BpiPhoneNumber.
     * 
     * @return
     */
    public BpiPhoneNumber newBpiPhoneNumber(
    ) {
    	return new BpiPhoneNumber();
    }

    /**
     * Get new instance of BpiAccountMember.
     * 
     * @return
     */
    public BpiAccountMember newBpiAccountMember(
    ) {
    	return new BpiAccountMember();
    }

    /**
     * Get new instance of BpiPostalAddress.
     * 
     * @return
     */
    public BpiPostalAddress newBpiPostalAddress(
    ) {
    	return new BpiPostalAddress();
    }

    /**
     * Get new instance of BpiParticipant.
     * 
     * @return
     */
    public BpiParticipant newBpiParticipant(
    ) {
    	return new BpiParticipant();
    }

    /**
     * Get new instance of BpiContact.
     * 
     * @return
     */
    public BpiContact newBpiContact(
    ) {
    	return new BpiContact();
    }

    /**
     * Get new instance of BpiOrganization.
     * 
     * @return
     */
    public BpiOrganization newBpiOrganization(
    ) {
    	return new BpiOrganization();
    }

    /**
     * Get new instance of BpiLocalizedField.
     * 
     * @return
     */
    public BpiLocalizedField newBpiLocalizedField(
    ) {
    	return new BpiLocalizedField();
    }

    /**
     * Map code value container to BpiCodeTable.
     * 
     * @param codeValueContainer
     * @param id
     * @return
     */
    public BpiCodeTable toBpiCodeTable(
    	CodeValueContainer codeValueContainer,
    	BpiCodeTable bpiCodeTable
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(codeValueContainer);
    	this.toBpiObject(codeValueContainer, bpiCodeTable);
    	bpiCodeTable.setName(codeValueContainer.getName());
    	List<BpiCodeTableEntry> bpiEntries = new ArrayList<BpiCodeTableEntry>();
		CodeValueEntryQuery codeValueEntryQuery = (CodeValueEntryQuery)pm.newQuery(CodeValueEntry.class);
		codeValueEntryQuery.orderByCreatedAt().ascending();    	
		for(CodeValueEntry entry: codeValueContainer.<CodeValueEntry>getEntry(codeValueEntryQuery)) {
			BpiCodeTableEntry bpiEntry = newBpiCodeTableEntry();
			this.toBpiObject(entry, bpiEntry);
			bpiEntry.setValidFrom(entry.getValidFrom());
			bpiEntry.setValidTo(entry.getValidTo());
			bpiEntry.setShortText(entry.getShortText());
			bpiEntry.setLongText(entry.getLongText());
			bpiEntries.add(bpiEntry);
		}
    	bpiCodeTable.setEntry(bpiEntries);
    	return bpiCodeTable;
    }

	/**
	 * Map activity creator to BpiActivityCreator.
	 * 
	 * @param activityCreator
	 * @return
	 * @throws ServiceException
	 */
    public BpiActivityCreator toBpiActivityCreator(
		ActivityCreator activityCreator,
		BpiActivityCreator bpiActivityCreator
	) throws ServiceException {
		this.toBpiObject(activityCreator, bpiActivityCreator);
		bpiActivityCreator.setName(activityCreator.getName());
		bpiActivityCreator.setDescription(activityCreator.getDescription());
		return bpiActivityCreator;
	}
	
    /**
     * Map business addresses of given account to BpiAddress.
     * 
     * @param account
     * @throws ServiceException
     * @throws IOException
     */
	public List<BpiAddress> toBpiBusinessAddress(
    	Account account
    ) throws ServiceException {
		AccountAddress[] mainAddresses = Accounts.getInstance().getMainAddresses(account);
		List<BpiAddress> bpiBusinessAddresses = new ArrayList<BpiAddress>();
		if(mainAddresses[Accounts.MAIL_BUSINESS] instanceof EMailAddress) {
			EMailAddress emailAddress = (EMailAddress)mainAddresses[Accounts.MAIL_BUSINESS];
			BpiEMailAddress bpiEMailAddress = newBpiEMailAddress();
			this.toBpiObject(emailAddress, bpiEMailAddress);
			bpiEMailAddress.setId(emailAddress.refGetPath().getBase());
			bpiEMailAddress.setXri(emailAddress.refGetPath().toXRI());
			bpiEMailAddress.setEmailAddress(emailAddress.getEmailAddress());
			bpiBusinessAddresses.add(bpiEMailAddress);
		}
		if(mainAddresses[Accounts.PHONE_BUSINESS] instanceof PhoneNumber) {
			PhoneNumber phoneNumber = (PhoneNumber)mainAddresses[Accounts.PHONE_BUSINESS];
			BpiPhoneNumber bpiPhoneNumber = newBpiPhoneNumber();
			this.toBpiObject(phoneNumber, bpiPhoneNumber);
			bpiPhoneNumber.setId(phoneNumber.refGetPath().getBase());
			bpiPhoneNumber.setXri(phoneNumber.refGetPath().toXRI());
			bpiPhoneNumber.setPhoneNumberFull(phoneNumber.getPhoneNumberFull());
			bpiBusinessAddresses.add(bpiPhoneNumber);
		}
		if(mainAddresses[Accounts.POSTAL_BUSINESS] instanceof PostalAddress) {
			PostalAddress postalAddress = (PostalAddress)mainAddresses[Accounts.POSTAL_BUSINESS];
			BpiPostalAddress bpiPostalAddress = newBpiPostalAddress();
			this.toBpiObject(postalAddress, bpiPostalAddress);
			bpiPostalAddress.setId(postalAddress.refGetPath().getBase());
			bpiPostalAddress.setXri(postalAddress.refGetPath().toXRI());
			bpiPostalAddress.setPostalAddressLine(postalAddress.getPostalAddressLine());
			bpiPostalAddress.setPostalStreet(postalAddress.getPostalStreet());
			bpiPostalAddress.setPostalCode(postalAddress.getPostalCode());
			bpiPostalAddress.setPostalCountry(postalAddress.getPostalCountry());
			bpiPostalAddress.setPostalCity(postalAddress.getPostalCity());
			bpiBusinessAddresses.add(bpiPostalAddress);
		}
		return bpiBusinessAddresses;
    }

    /**
     * Map BasicObject to BpiObject.
     * 
     * @param object
     * @param bpiObject
     * @throws ServiceException
     */
    public BpiObject toBpiObject(
    	BasicObject object,
    	BpiObject bpiObject
    ) throws ServiceException {
    	if(bpiObject.getId() == null) {
    		bpiObject.setId(object.refGetPath().getBase());
    	}
    	bpiObject.setXri(object.refGetPath().toXRI());
    	bpiObject.setCreatedAt(object.getCreatedAt());
    	bpiObject.setCreatedBy(object.getCreatedBy());
    	bpiObject.setModifiedAt(object.getModifiedAt());
    	bpiObject.setModifiedBy(object.getModifiedBy());
    	return bpiObject;
    }

    /**
     * Map contact to BpiContact.
     * 
     * @param contact
     * @throws ServiceException
     */
    public BpiContact toBpiContact(
       	Contact contact,
       	BpiContact bpiContact
    ) throws ServiceException {
    	this.toBpiObject(contact, bpiContact);
    	bpiContact.setFullName(contact.getFullName());
    	bpiContact.setVcard(contact.getVcard());
    	bpiContact.setFirstName(contact.getFirstName());
    	bpiContact.setLastName(contact.getLastName());
    	bpiContact.setJobTitle(contact.getJobTitle());
    	bpiContact.setBusinessAddress(this.toBpiBusinessAddress(contact));
		return bpiContact;
    }

    /**
     * Map organization to BpiOrganization.
     * 
     * @param organization
     * @throws ServiceException
     * @throws IOException
     */
    public BpiOrganization toBpiOrganization(
    	LegalEntity organization,
    	BpiOrganization bpiOrganization
    ) throws ServiceException {
    	this.toBpiObject(organization, bpiOrganization);
    	bpiOrganization.setFullName(organization.getFullName());
    	bpiOrganization.setVcard(organization.getVcard());
    	bpiOrganization.setBusinessAddress(this.toBpiBusinessAddress(organization));
		return bpiOrganization;
    }

    /**
     * Map activity to BpiActivity.
     * 
     * @param activity
     * @param bpiActivity
     */
    public BpiActivity toBpiActivity(
    	Activity activity,
    	BpiActivity bpiActivity
    ) throws ServiceException {
    	return this.toBpiActivity(
    		activity, 
    		bpiActivity, 
    		true // includeParticipants
    	);
    }

    /**
     * Map activity to BpiActivity.
     * 
     * @param activity
     * @param bpiActivity
     * @param includeParticipants
     */
    public BpiActivity toBpiActivity(
    	Activity activity,
    	BpiActivity bpiActivity,
    	boolean includeParticipants
    ) throws ServiceException {
    	this.toBpiObject(activity, bpiActivity);
    	bpiActivity.setActivityNumber(activity.getActivityNumber());
    	bpiActivity.setName(activity.getName());
    	bpiActivity.setDescription(activity.getDescription());
    	bpiActivity.setAdditionalInformation(activity.getDetailedDescription());
    	bpiActivity.setScheduledStart(activity.getScheduledStart());
    	bpiActivity.setScheduledEnd(activity.getScheduledEnd());
    	bpiActivity.setActivityState(activity.getActivityState());
    	bpiActivity.setLocation(activity.getLocation());
    	bpiActivity.setCategory(activity.getCategory());
    	// Participants
    	if(includeParticipants) {
        	if(activity.getReportingContact() != null) {
        		bpiActivity.setReportingContact(this.toBpiContact(activity.getReportingContact(), this.newBpiContact()));
        	}
        	if(activity.getAssignedTo() != null) {
        		bpiActivity.setAssignedTo(this.toBpiContact(activity.getAssignedTo(), this.newBpiContact()));
        	}
	    	List<BpiParticipant> bpiParticipants = new ArrayList<BpiParticipant>();
			for(AbstractActivityParty party: Activities.getInstance().getActivityParties(activity)) {
				Account partyAccount = null;
				if(party instanceof TaskParty) {
					partyAccount = (Account)((TaskParty)party).getParty();
				} else if(party instanceof MeetingParty) {
					partyAccount = (Account)((MeetingParty)party).getParty();
				} else if(party instanceof IncidentParty) {
					partyAccount = (Account)((IncidentParty)party).getParty();
				}
				if(partyAccount != null) {
					BpiParticipant bpiParticipant = newBpiParticipant();
					this.toBpiObject(party, bpiParticipant);
					bpiParticipant.setPartyStatus(party.getPartyStatus());
					bpiParticipant.setPartyType(party.getPartyType());
					if(partyAccount instanceof Contact) {
						bpiParticipant.setAccount(this.toBpiContact((Contact)partyAccount, this.newBpiContact()));
					} else if(partyAccount instanceof LegalEntity) {
						bpiParticipant.setAccount(this.toBpiOrganization((LegalEntity)partyAccount, this.newBpiOrganization()));
					}
					bpiParticipants.add(bpiParticipant);
				}
			}
			bpiActivity.setParticipant(bpiParticipants);
    	}
		// Localized fields
		List<BpiLocalizedField> bpiLocalizedFields = new ArrayList<BpiLocalizedField>();
		for(LocalizedField localizedField: activity.<LocalizedField>getLocalizedField()) {			
			bpiLocalizedFields.add(
				this.toBpiLocalizedField(localizedField, this.newBpiLocalizedField())
			);
		}
		bpiActivity.setLocalizedField(bpiLocalizedFields);
		return bpiActivity;
    }

    /**
     * Map activityFollowUp to BpiActivityFollowUp.
     * 
     * @param activityFollowUp
     * @param bpiActivityFollowUp
     */
    public BpiActivityFollowUp toBpiActivityFollowUp(
    	ActivityFollowUp activityFollowUp,
    	BpiActivityFollowUp bpiActivityFollowUp
    ) throws ServiceException {
    	this.toBpiObject(activityFollowUp, bpiActivityFollowUp);
    	bpiActivityFollowUp.setTitle(activityFollowUp.getTitle());
    	bpiActivityFollowUp.setText(activityFollowUp.getText());
    	bpiActivityFollowUp.setTransition(activityFollowUp.getTransition().getName());
		return bpiActivityFollowUp;
    }

    /**
     * Map member to BpiAccountMember.
     * 
     * @param member
     * @param bpiAccountMember
     * @return
     * @throws ServiceException
     */
    public BpiAccountMember toBpiAccountMember(
    	Member member,
    	BpiAccountMember bpiAccountMember
    ) throws ServiceException {
		bpiAccountMember.setId(member.refGetPath().getBase());
		bpiAccountMember.setXri(member.refGetPath().toXRI());
		bpiAccountMember.setName(member.getAccount().getFullName());
		bpiAccountMember.setMemberRole(member.getMemberRole());
		if(member.getAccount() instanceof Contact) {
			bpiAccountMember.setAccount(
				this.toBpiContact((Contact)member.getAccount(), this.newBpiContact())
			);
		} else if(member.getAccount() instanceof LegalEntity) {
			bpiAccountMember.setAccount(
				this.toBpiOrganization((LegalEntity)member.getAccount(), this.newBpiOrganization())
			);
		}
		return bpiAccountMember;
    }

    /**
     * Map localized field to BpiLocalizedField.
     * 
     * @param localizedField
     * @param bpiLocalizedField
     * @return
     * @throws ServiceException
     */
    public BpiLocalizedField toBpiLocalizedField(
    	LocalizedField localizedField,
    	BpiLocalizedField bpiLocalizedField
    ) throws ServiceException {
    	this.toBpiObject(
    		localizedField, 
    		bpiLocalizedField
    	);
    	bpiLocalizedField.setName(localizedField.getName());
    	bpiLocalizedField.setDescription(localizedField.getDescription());
    	bpiLocalizedField.setLocale(localizedField.getLocale());
    	bpiLocalizedField.setLocalizedValue(localizedField.getLocalizedValue());
    	return bpiLocalizedField;
    }

    /**
     * Find contacts with the given id.
     * 
     * @param path
     * @param pm
     * @return
     */
    public List<Contact> findContacts(
    	Path path,
    	PersistenceManager pm    	
    ) {
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	ContactQuery contactQuery = (ContactQuery)pm.newQuery(Contact.class);
    	contactQuery.thereExistsAliasName().equalTo(path.getBase());
    	contactQuery.orderByCreatedAt().ascending();
    	contactQuery.forAllDisabled().isFalse();
    	return accountSegment.getAccount(contactQuery);    	
    }

    /**
     * Find legal entities with the given id.
     * 
     * @param path
     * @param pm
     * @return
     */
    public List<LegalEntity> findLegalEntities(
    	Path path,
    	PersistenceManager pm    	
    ) {
    	org.opencrx.kernel.account1.jmi1.Segment accountSegment = (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	LegalEntityQuery legalEntityQuery = (LegalEntityQuery)pm.newQuery(LegalEntity.class);
    	legalEntityQuery.name().equalTo(path.getBase());
    	legalEntityQuery.orderByCreatedAt().ascending();
    	legalEntityQuery.forAllDisabled().isFalse();
    	return accountSegment.getAccount(legalEntityQuery);    	
    }
    
    /**
     * Find activity creators with given name.
     * 
     * @param path
     * @param pm
     * @return
     */
    public List<ActivityCreator> findActivityCreators(
    	Path path,
    	PersistenceManager pm    	
    ) {
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	ActivityCreatorQuery activityCreatorQuery = (ActivityCreatorQuery)pm.newQuery(ActivityCreator.class);
    	activityCreatorQuery.name().equalTo(path.getBase());
    	activityCreatorQuery.orderByCreatedAt().ascending();
    	activityCreatorQuery.forAllDisabled().isFalse();
    	return activitySegment.getActivityCreator(activityCreatorQuery);    	
    }
    
    /**
     * Find activity trackers with given name.
     * 
     * @param path
     * @param pm
     * @return
     */
    public List<ActivityTracker> findActivityTrackers(
    	Path path,
    	PersistenceManager pm    	
    ) {
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	ActivityTrackerQuery activityTrackerQuery = (ActivityTrackerQuery)pm.newQuery(ActivityTracker.class);
    	activityTrackerQuery.name().equalTo(path.getBase());
    	activityTrackerQuery.orderByCreatedAt().ascending();
    	activityTrackerQuery.forAllDisabled().isFalse();
    	return activitySegment.getActivityTracker(activityTrackerQuery);    	
    }

    /**
     * Find activities with given number.
     * 
     * @param path
     * @param pm
     * @return
     */
    public List<Activity> findActivities(
    	Path path,
    	PersistenceManager pm    	
    ) {
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
    		new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", path.get(2), "segment", path.get(4))
    	);
    	ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
    	activityQuery.thereExistsActivityNumber().equalTo(path.getBase());
    	activityQuery.orderByCreatedAt().ascending();
    	activityQuery.forAllDisabled().isFalse();
    	return activitySegment.getActivity(activityQuery);    	
    }
		
}

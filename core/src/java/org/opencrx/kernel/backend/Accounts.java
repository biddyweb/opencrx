/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Accounts.java,v 1.100 2012/01/13 17:15:42 wfro Exp $
 * Description: Accounts
 * Revision:    $Revision: 1.100 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/13 17:15:42 $
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
package org.opencrx.kernel.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.AccountAddressQuery;
import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.cci2.EMailAddressQuery;
import org.opencrx.kernel.account1.cci2.PhoneNumberQuery;
import org.opencrx.kernel.account1.cci2.PostalAddressQuery;
import org.opencrx.kernel.account1.cci2.RoomQuery;
import org.opencrx.kernel.account1.cci2.WebAddressQuery;
import org.opencrx.kernel.account1.jmi1.AbstractFilterAccount;
import org.opencrx.kernel.account1.jmi1.AbstractFilterAddress;
import org.opencrx.kernel.account1.jmi1.AbstractGroup;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.AddressAccountMembershipFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressCategoryFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressDisabledFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressMainFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressQueryFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressTypeFilterProperty;
import org.opencrx.kernel.account1.jmi1.AddressUsageFilterProperty;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.account1.jmi1.Room;
import org.opencrx.kernel.account1.jmi1.WebAddress;
import org.opencrx.kernel.activity1.cci2.AddressGroupMemberQuery;
import org.opencrx.kernel.activity1.cci2.EMailQuery;
import org.opencrx.kernel.activity1.cci2.EMailRecipientQuery;
import org.opencrx.kernel.activity1.cci2.IncidentPartyQuery;
import org.opencrx.kernel.activity1.cci2.MeetingPartyQuery;
import org.opencrx.kernel.activity1.cci2.TaskPartyQuery;
import org.opencrx.kernel.activity1.jmi1.AddressGroupMember;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.IncidentParty;
import org.opencrx.kernel.activity1.jmi1.MeetingParty;
import org.opencrx.kernel.activity1.jmi1.TaskParty;
import org.opencrx.kernel.base.jmi1.AttributeFilterProperty;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesContract;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.query.ConditionType;
import org.openmdx.base.query.Quantifier;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

public class Accounts extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Accounts());
	}
	
    //-------------------------------------------------------------------------
	public static Accounts getInstance(
	) throws ServiceException {
		return getInstance(Accounts.class);
	}

	//-------------------------------------------------------------------------
	protected Accounts(
	) {		
	}
	
    //-------------------------------------------------------------------------
    public void markAccountAsDirty(
        Account account
    ) throws ServiceException {
    	account.setAccountRating(account.getAccountRating());
    }
    	
    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead.
     */
    public void initContract(
        SalesContract contract,
        Account account,
        String name,
        String description
    ) throws ServiceException {
        if(name != null) {
            contract.setName(name);
        }
        if(description != null) {
            contract.setDescription(description);
        }
        contract.setCustomer(account);
        contract.setActiveOn(
        	new Date()
        );
        Base.getInstance().assignToMe(
            contract,
            true, // overwrite
            false // useRunAsPrincipal
        );
    }

    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead
     */
    public Lead createLead(
        Account account,
        String name,
        String description,
        String nextStep,
        Lead basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
    	String providerName = account.refGetPath().get(2);
    	String segmentName = account.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getInstance().getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
    	Lead contract = (Lead)Contracts.getInstance().createContract(
    		contractSegment, 
    		Contracts.CONTRACT_TYPE_LEAD, 
    		basedOn
    	);
        contract.setNextStep(nextStep);    	
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead
     */
    public Opportunity createOpportunity(
        Account account,
        String name,
        String description,
        String nextStep,
        Opportunity basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
    	String providerName = account.refGetPath().get(2);
    	String segmentName = account.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getInstance().getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
    	Opportunity contract = (Opportunity)Contracts.getInstance().createContract(
    		contractSegment, 
    		Contracts.CONTRACT_TYPE_OPPORTUNITY, 
    		basedOn
    	);
        contract.setNextStep(nextStep);    	
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead
     */
    public Quote createQuote(
        Account account,
        String name,
        String description,
        Quote basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
    	String providerName = account.refGetPath().get(2);
    	String segmentName = account.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getInstance().getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
    	Quote contract = (Quote)Contracts.getInstance().createContract(
    		contractSegment, 
    		Contracts.CONTRACT_TYPE_QUOTE, 
    		basedOn
    	);
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead
     */
    public SalesOrder createSalesOrder(
        Account account,
        String name,
        String description,
        SalesOrder basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
    	String providerName = account.refGetPath().get(2);
    	String segmentName = account.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getInstance().getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
    	SalesOrder contract = (SalesOrder)Contracts.getInstance().createContract(
    		contractSegment, 
    		Contracts.CONTRACT_TYPE_SALES_ORDER, 
    		basedOn
    	);
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    /**
     * @deprecated use contract creators instead
     */
    public Invoice createInvoice(
        Account account,
        String name,
        String description,
        Invoice basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
    	String providerName = account.refGetPath().get(2);
    	String segmentName = account.refGetPath().get(4);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = Contracts.getInstance().getContractSegment(
        	pm, 
        	providerName, 
        	segmentName
        );
    	Invoice contract = (Invoice)Contracts.getInstance().createContract(
    		contractSegment, 
    		Contracts.CONTRACT_TYPE_INVOICE, 
    		basedOn
    	);
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    public void updateFullName(
        Account account
    ) throws ServiceException {
        // org:opencrx:kernel:account1:Contact:fullName
        if(account instanceof Contact) {
        	Contact contact = (Contact)account;
            contact.setFullName(
                ( (contact.getLastName() == null ? "" : contact.getLastName()) + ", "
                  + (contact.getFirstName() == null ? "" : contact.getFirstName() + " ")
                  + (contact.getMiddleName() == null ? "" : contact.getMiddleName() + "") ).trim()
            );
        }
        // org:opencrx:kernel:account1:AbstractGroup
        else if(account instanceof AbstractGroup) {
        	AbstractGroup group = (AbstractGroup)account;
            group.setFullName(
                group.getName() == null ? "" : group.getName()
            );
        }
    }
    
    //-------------------------------------------------------------------------
    public void updateAccount(
        Account account
    ) throws ServiceException {
        this.updateFullName(
            account 
        );
        List<String> statusMessage = new ArrayList<String>();
        String vcard = VCard.getInstance().mergeVcard(
            account,
            account.getVcard(),
            statusMessage
        );
        account.setVcard(
            vcard == null ? "" : vcard
        );
        // Assertion externalLink().contains(vcard uid)
        String uid = VCard.getInstance().getVCardUid(vcard);
        boolean vcardLinkMatches = false;
        boolean hasVCardLink = false;
        for(String externalLink: account.getExternalLink()) {
        	if(externalLink.startsWith(VCard.VCARD_SCHEMA)) {
        		hasVCardLink = true;
        		if(externalLink.endsWith(uid)) {
	        		vcardLinkMatches = true;
	        		break;
        		}
        	}
        }
        if(!hasVCardLink) {
        	account.getExternalLink().add(
        		VCard.VCARD_SCHEMA + uid
        	);
        }
        else if(!vcardLinkMatches) {
        	ServiceException e = new ServiceException(
        		BasicException.Code.DEFAULT_DOMAIN,
        		BasicException.Code.ASSERTION_FAILURE,
        		"Accounts's external link does not contain vcard UID",
        		new BasicException.Parameter("activity", account),
        		new BasicException.Parameter("externalLink", account.getExternalLink()),
        		new BasicException.Parameter("vcard", vcard)
        	);
        	SysLog.warning("Accounts's external link does not contain vcard UID", account.refGetPath());
        	SysLog.detail(e.getMessage(), e.getCause());
        }
    }

    //-------------------------------------------------------------------------
    public void updateAddress(
    	AccountAddress address
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(address);
    	if(address instanceof PhoneNumber) {
    		Addresses.getInstance().updatePhoneNumber(
    			(PhoneNumber)address 
    		);    		
    	}
		// Mark account as dirty updates VCard, ...
		this.markAccountAsDirty(
			(Account)pm.getObjectById(
				address.refGetPath().getParent().getParent()
			)
		);
    }
    
    //-------------------------------------------------------------------------
    public void removeAddress(
    	AccountAddress address,
    	boolean preDelete
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(address);
		// Mark account as dirty updates VCard, ...
		this.markAccountAsDirty(
			(Account)pm.getObjectById(
				address.refGetPath().getParent().getParent()
			)
		);
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredAccount(
    	AbstractFilterAccount accountFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(accountFilter);
        AccountQuery query = (AccountQuery)pm.newQuery(Account.class);
    	org.openmdx.base.query.Extension queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);    	
    	List<Account> accounts = accountFilter.getFilteredAccount(query);
        return accounts.size();
    }

    //-----------------------------------------------------------------------
    /**
     * @return Returns the account segment.
     */
    public org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.account1.jmi1.Segment) pm.getObjectById(
            new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", providerName, "segment", segmentName)
        );
    }

    //-----------------------------------------------------------------------
    /**
     * Search accounts containing the given email address.
     */
    public List<EMailAddress> lookupEmailAddress(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String emailAddress
    ) {
        org.opencrx.kernel.account1.jmi1.Segment accountSegment =
            this.getAccountSegment(
                pm,
                providerName,
                segmentName
            );
        List<EMailAddress> addresses = Collections.emptyList();
        // Phase 1: exact match, case-sensitive
        {
            EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
            query.orderByModifiedAt().descending();
            query.thereExistsEmailAddress().equalTo(emailAddress);
           	query.forAllDisabled().isFalse();
           	addresses = accountSegment.getAddress(query);
        }        
        // Phase 2: exact match, case-insensitive
        if(addresses.isEmpty()) { 
            EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
            query.orderByModifiedAt().descending();
            query.thereExistsEmailAddress().like(
               "(?i)" + emailAddress.replace(".", "\\.") 
            );
           	query.forAllDisabled().isFalse();
           	addresses = accountSegment.getAddress(query);
        }
        // Phase 3: Search email address with wildcard pattern        
        if(addresses.isEmpty()) { 
	        EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
	        query.orderByModifiedAt().descending();
	        query.thereExistsEmailAddress().like(
	           "(?i).*" + emailAddress.replace(".", "\\.") + ".*" 
	        );
	       	query.forAllDisabled().isFalse();
	       	addresses = accountSegment.getAddress(query);
        }
        // Active addresses ordered by length. The best match should
        // be the first element in the returned list
   		List<EMailAddress> activeAddresses = new ArrayList<EMailAddress>();
   		for(EMailAddress address: addresses) {
   			Account account = address.getAccount();
   			if(account.isDisabled() == null || !account.isDisabled()) {
   				int index = 0;
   				while(index < activeAddresses.size()) {
   					if(address.getEmailAddress().length() < activeAddresses.get(index).getEmailAddress().length()) {
   						break;
   					}
   					index++;
   				}
   				activeAddresses.add(index, address);
   			}
   		}
   		return activeAddresses;
    }

    //-------------------------------------------------------------------------
    public List<EMailAddress> lookupEmailAddress(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String email,
        boolean forceCreate
    ) throws ServiceException {
    	List<EMailAddress> emailAddresses = Accounts.getInstance().lookupEmailAddress(
    		pm, 
    		providerName, 
    		segmentName, 
    		email
    	);
    	if(forceCreate && emailAddresses.isEmpty()) {
    		PersistenceManager pmAdmin = pm.getPersistenceManagerFactory().getPersistenceManager(
    			SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName,
    			null
    		);
        	Account segmentAdmin = (Account)pmAdmin.getObjectById(
        		new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", providerName, "segment", segmentName, "account", (SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName))
        	);
        	if(segmentAdmin != null) {
        		pmAdmin.currentTransaction().begin();
        		EMailAddress emailAddress = pmAdmin.newInstance(EMailAddress.class);
        		emailAddress.setEmailAddress(email);
        		emailAddress.setEmailType((short)1);
        		segmentAdmin.addAddress(
        			this.getUidAsString(),
        			emailAddress
        		);
        		pmAdmin.currentTransaction().commit();
        		emailAddresses = Collections.singletonList(
        			(EMailAddress)pm.getObjectById(emailAddress.refGetPath())
        		);
        	}
        	pmAdmin.close();
    	}
    	return emailAddresses;
    }

    //-------------------------------------------------------------------------
    private static List<org.opencrx.kernel.account1.jmi1.AccountAddress> getAccountAddresses(
    	PersistenceManager pm,
        org.opencrx.kernel.account1.jmi1.Account account,
        short usage
    ) {
        org.opencrx.kernel.account1.cci2.AccountAddressQuery query = 
        	(org.opencrx.kernel.account1.cci2.AccountAddressQuery)pm.newQuery( org.opencrx.kernel.account1.jmi1.AccountAddress.class); 
        query.thereExistsUsage().equalTo(usage);
        query.forAllDisabled().isFalse();
        return account.getAddress(query); 
    }
    
    //-------------------------------------------------------------------------
    public String getPrimaryBusinessEMail(
        Account account,
        String hint
    ) throws ServiceException {
        Collection<AccountAddress> addresses = account.getAddress();
        String emailAddress = null;
        for(AccountAddress address: addresses) {
            if(address instanceof EMailAddress) {
                String addr = ((EMailAddress)address).getEmailAddress();
                if(emailAddress == null || (hint != null && !hint.isEmpty())) {
                	if(emailAddress == null) {
                		emailAddress = addr;                		
                	} else {
                		if(hint.equals(addr)) {
                			emailAddress = addr;
                		}
                	}
                }
                if(addr != null && address.isMain() && address.getUsage().contains(Addresses.USAGE_BUSINESS)) {
                    emailAddress = addr;
                }
            }
        }
        return emailAddress;
    }

    //-------------------------------------------------------------------------
    public String getPrimaryBusinessPhone(
        Account account
    ) throws ServiceException {
        Collection<AccountAddress> addresses = account.getAddress();
        String phoneNumber = null;
        for(AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                String adr = ((PhoneNumber)address).getPhoneNumberFull();
                if((phoneNumber == null) && (adr != null)) {
                    phoneNumber = adr;
                }
                if(adr != null && address.isMain() && address.getUsage().contains(Addresses.USAGE_BUSINESS)) {
                    phoneNumber = adr;
                }
            }
        }
        return phoneNumber;
    }

    //-------------------------------------------------------------------------
    /**
     * Return main home and business addresses of given account.
     * @return array with elements {web home, web business, phone home, phone business, 
     *         fax home, fax, business, postal home, postal business,
     *         mail home, mail business, mobile, phone other} 
     */
    public org.opencrx.kernel.account1.jmi1.AccountAddress[] getMainAddresses(
        Account account
    ) {
        int currentOrderBusinessMail = 0;
        int currentOrderHomeMail = 0;
        int currentOrderBusinessPhone = 0;
        int currentOrderHomePhone = 0;
        int currentOrderBusinessFax = 0;
        int currentOrderHomeFax = 0;
        int currentOrderBusinessPostal = 0;
        int currentOrderBusinessWeb = 0;
        int currentOrderHomePostal = 0;
        int currentOrderHomeWeb = 0;
        int currentOrderMobile = 0;
        int currentOrderOtherPhone = 0;
        
        PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.account1.jmi1.AccountAddress[] mainAddresses = 
            new org.opencrx.kernel.account1.jmi1.AccountAddress[12];
        // Performance: retrieve and cache addresses
        account.getAddress().isEmpty(); 
        // HOME
        List<org.opencrx.kernel.account1.jmi1.AccountAddress> addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_HOME
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof WebAddress) {
                WebAddress webAddress = (WebAddress)address;
                boolean isMain = false;
                try {
                    isMain = webAddress.isMain();
                } catch(Exception e) {}
                int orderHomeWeb = isMain ? 3 : 1;
                if(orderHomeWeb > currentOrderHomeWeb) {
                    mainAddresses[WEB_HOME] = webAddress;
                    currentOrderHomeWeb = orderHomeWeb;
                }
            }
            else if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } catch(Exception e) {}
                int orderHomePhone = isMain ? 3 : 1;
                if(orderHomePhone > currentOrderHomePhone) {
                    mainAddresses[PHONE_HOME] = phoneNumber;
                    currentOrderHomePhone = orderHomePhone;
                }
            }
            else if (address instanceof PostalAddress) {
                PostalAddress postalAddress = (PostalAddress)address;
                boolean isMain = false;
                try {
                    isMain = postalAddress.isMain();
                } catch(Exception e) {}
                int orderHomePostal = isMain ? 3 : 1;
                if(orderHomePostal > currentOrderHomePostal) {       
                    mainAddresses[POSTAL_HOME] = postalAddress;
                    currentOrderHomePostal = orderHomePostal;
                }
            }
            else if(address instanceof EMailAddress) {
                EMailAddress mailAddress = (EMailAddress)address;
                boolean isMain = false;
                try {
                    isMain = mailAddress.isMain();
                } catch(Exception e) {}
                int orderHomeMail = isMain ? 3 : 1;
                if(orderHomeMail > currentOrderHomeMail) {
                    mainAddresses[MAIL_HOME] = mailAddress;
                    currentOrderHomeMail = orderHomeMail;
                }
            }
        }    
        // BUSINESS
        addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_BUSINESS
        );
        for(AccountAddress address: addresses) {
            if(address instanceof WebAddress) {
                WebAddress webAddress = (WebAddress)address;
                boolean isMain = false;
                try {
                    isMain = webAddress.isMain();
                } 
                catch(Exception e) {}
                int orderBusinessWeb = isMain ? 3 : 1;
                if(orderBusinessWeb > currentOrderBusinessWeb) {
                    mainAddresses[WEB_BUSINESS] = webAddress;
                    currentOrderBusinessWeb = orderBusinessWeb;
                }
            }
            else if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } 
                catch(Exception e) {}
                int orderBusinessPhone = isMain ? 3 : 1;
                if(orderBusinessPhone > currentOrderBusinessPhone) {
                    mainAddresses[PHONE_BUSINESS] = phoneNumber;
                    currentOrderBusinessPhone = orderBusinessPhone;
                }
            }
            else if (address instanceof PostalAddress) {
                PostalAddress postalAddress = (PostalAddress)address;
                boolean isMain = false;
                try {
                    isMain = postalAddress.isMain();
                } 
                catch(Exception e) {}
                int orderBusinessPostal = isMain ? 3 : 1;
                if(orderBusinessPostal > currentOrderBusinessPostal) {
                    mainAddresses[POSTAL_BUSINESS] = postalAddress;
                    currentOrderBusinessPostal = orderBusinessPostal;
                }
            }
            else if(address instanceof EMailAddress) {
                EMailAddress mailAddress = (EMailAddress)address;
                boolean isMain = false;
                try {
                    isMain = mailAddress.isMain();
                } 
                catch(Exception e) {}
                int orderBusinessMail = isMain ? 3 : 1;
                if(orderBusinessMail > currentOrderBusinessMail) {
                    mainAddresses[MAIL_BUSINESS] = mailAddress;
                    currentOrderBusinessMail = orderBusinessMail;
                }
            }
        }    
        // OTHER
        addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_OTHER
        );
        for(AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } catch(Exception e) {}
                int orderOtherPhone = isMain ? 3 : 1;
                if(orderOtherPhone > currentOrderOtherPhone) {
                    mainAddresses[PHONE_OTHER] = phoneNumber;
                    currentOrderOtherPhone = orderOtherPhone;
                }
            }
        }    
        // HOME_FAX
        addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_HOME_FAX
        );
        for(AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } catch(Exception e) {}
                int orderHomeFax = isMain ? 3 : 1;
                if(orderHomeFax > currentOrderHomeFax) {           
                    mainAddresses[FAX_HOME] = phoneNumber;
                    currentOrderHomeFax = orderHomeFax;
                }
            }
        }    
        // BUSINESS_FAX
        addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_BUSINESS_FAX
        );
        for(AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                                
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } catch(Exception e) {}
                int orderBusinessFax = isMain ? 3 : 1;
                if(orderBusinessFax > currentOrderBusinessFax) {
                    mainAddresses[FAX_BUSINESS] = phoneNumber;
                    currentOrderBusinessFax = orderBusinessFax;
                }
            }
        }    
        // MOBILE
        addresses = Accounts.getAccountAddresses(
        	pm,
            account,
            Addresses.USAGE_MOBILE
        );
        for(AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;     
                boolean isMain = false;
                try {
                    isMain = phoneNumber.isMain();
                } catch(Exception e) {}                
                int orderMobile = isMain ? 3 : 1;
                if(orderMobile > currentOrderMobile) { 
                    mainAddresses[MOBILE] = phoneNumber;
                    currentOrderMobile = orderMobile;
                }
            }
        }    
        return mainAddresses;
    }
    
    //-------------------------------------------------------------------------
    public void updateVcard(
        Account account
    ) throws ServiceException {
        List<String> messages = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();
        List<String> report = new ArrayList<String>();
        String vcard = VCard.getInstance().mergeVcard(
            account,
            account.getVcard(), 
            messages
        );        
        byte[] item = null;
        try {
            item = vcard.getBytes("UTF-8");
        } 
        catch(Exception e) {
            item = vcard.getBytes();    
        }
        VCard.getInstance().importItem(
            item, 
            account, 
            (short)0, 
            errors, 
            report
        );
    }
    
    //-------------------------------------------------------------------------
    public void updateMember(
    	Member member
    ) throws ServiceException {    	
    }
    
    //-------------------------------------------------------------------------
    public AccountAddressQuery getFilteredAddressQuery(
        AbstractFilterAddress addressFilter,
        boolean forCounting
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(addressFilter);
        Collection<AddressFilterProperty> filterProperties = addressFilter.getAddressFilterProperty();
        boolean hasQueryFilterClause = false;
        AccountAddressQuery query = (AccountAddressQuery)pm.newQuery(AccountAddress.class);
        for(AddressFilterProperty filterProperty: filterProperties) {
            Boolean isActive = filterProperty.isActive();            
            if((isActive != null) && isActive.booleanValue()) {
            	if(filterProperty instanceof AddressTypeFilterProperty) {
            		AddressTypeFilterProperty p = (AddressTypeFilterProperty)filterProperty;
            		if(!p.getAddressType().isEmpty()) {
            			switch(p.getAddressType().get(0)) {
            				case 0:
            					query = (PostalAddressQuery)pm.newQuery(PostalAddress.class);
            					break;
            				case 1:
            					query = (PhoneNumberQuery)pm.newQuery(PhoneNumber.class);
            					break;
            				case 2:
            					query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
            					break;
            				case 3:
            					query = (WebAddressQuery)pm.newQuery(WebAddress.class);
            					break;
            				case 4:
            					query = (RoomQuery)pm.newQuery(Room.class);
            					break;
            			}
            		}
            	}
            }
        }        
        for(AddressFilterProperty filterProperty: filterProperties) {
            Boolean isActive = filterProperty.isActive();            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if(filterProperty instanceof AddressQueryFilterProperty) {
                	AddressQueryFilterProperty p = (AddressQueryFilterProperty)filterProperty;
                	org.openmdx.base.query.Extension queryFilter = PersistenceHelper.newQueryExtension(query);
                	queryFilter.setClause(
                		(forCounting ? Database_1_Attributes.HINT_COUNT : "") + p.getClause()
                	);
                    queryFilter.getStringParam().addAll(
                    	p.getStringParam()
                    );
                    queryFilter.getIntegerParam().addAll(
                    	p.getIntegerParam()
                    );
                    queryFilter.getDecimalParam().addAll(
                    	p.getDecimalParam()
                    );
                    queryFilter.getBooleanParam().add(
                    	p.getBooleanParam().isEmpty() ? Boolean.FALSE : p.getBooleanParam().iterator().next()
                    );
                    queryFilter.getDateParam().addAll(
                    	p.getDateParam()
                    );
                    queryFilter.getDateTimeParam().addAll(
                    	p.getDateTimeParam()
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else if(filterProperty instanceof AttributeFilterProperty) {
                	AttributeFilterProperty attributeFilterProperty = (AttributeFilterProperty)filterProperty;
                    // Get filterOperator, filterQuantor
                    short operator = attributeFilterProperty.getFilterOperator();
                    operator = operator == 0 ? 
                    	ConditionType.IS_IN.code() : 
                    		operator;
                    short quantor = attributeFilterProperty.getFilterQuantor();
                    quantor = quantor == 0 ? 
                    	Quantifier.THERE_EXISTS.code() : 
                    	quantor;                    
                    if(filterProperty instanceof AddressCategoryFilterProperty) {
                    	AddressCategoryFilterProperty p = (AddressCategoryFilterProperty)filterProperty;
                    	switch(Quantifier.valueOf(quantor)) {
                    		case FOR_ALL:
                    			switch(ConditionType.valueOf(operator)) {
                    				case IS_IN: 
                    					query.forAllCategory().elementOf(p.getCategory()); 
                    					break;
                    				case IS_GREATER:
                    					query.forAllCategory().greaterThan(p.getCategory().get(0)); 
                    					break;
                    				case IS_GREATER_OR_EQUAL:
                    					query.forAllCategory().greaterThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case IS_LESS:
                    					query.forAllCategory().lessThan(p.getCategory().get(0)); 
                    					break;
                    				case IS_LESS_OR_EQUAL:
                    					query.forAllCategory().lessThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case IS_NOT_IN:
                    					query.forAllCategory().notAnElementOf(p.getCategory()); 
                    					break;
                    				default:
                    					query.forAllCategory().elementOf(p.getCategory()); 
                    					break;
                    			}
                    			break;
                    		default:
                    			switch(ConditionType.valueOf(operator)) {
                    				case IS_IN: 
                    					query.thereExistsCategory().elementOf(p.getCategory()); 
                    					break;
                    				case IS_GREATER:
                    					query.thereExistsCategory().greaterThan(p.getCategory().get(0)); 
                    					break;
                    				case IS_GREATER_OR_EQUAL:
                    					query.thereExistsCategory().greaterThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case IS_LESS:
                    					query.thereExistsCategory().lessThan(p.getCategory().get(0)); 
                    					break;
                    				case IS_LESS_OR_EQUAL:
                    					query.thereExistsCategory().lessThanOrEqualTo(p.getCategory().get(0)); 
                    					break;
                    				case IS_NOT_IN:
                    					query.thereExistsCategory().notAnElementOf(p.getCategory()); 
                    					break;
                    				default:
                    					query.thereExistsCategory().elementOf(p.getCategory()); 
                    					break;
                    			}
                    			break;
                    	}
                    }
                    else if(filterProperty instanceof AddressUsageFilterProperty) {
                    	AddressUsageFilterProperty p = (AddressUsageFilterProperty)filterProperty;
                    	switch(Quantifier.valueOf(quantor)) {
	                		case FOR_ALL:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN: 
	                					query.forAllUsage().elementOf(p.getUsage()); 
	                					break;
	                				case IS_GREATER:
	                					query.forAllUsage().greaterThan(p.getUsage().get(0)); 
	                					break;
	                				case IS_GREATER_OR_EQUAL:
	                					query.forAllUsage().greaterThanOrEqualTo(p.getUsage().get(0)); 
	                					break;
	                				case IS_LESS:
	                					query.forAllUsage().lessThan(p.getUsage().get(0)); 
	                					break;
	                				case IS_LESS_OR_EQUAL:
	                					query.forAllUsage().lessThanOrEqualTo(p.getUsage().get(0)); 
	                					break;
	                				case IS_NOT_IN:
	                					query.forAllUsage().notAnElementOf(p.getUsage()); 
	                					break;
	                				default:
	                					query.forAllUsage().elementOf(p.getUsage()); 
	                					break;
	                			}
	                			break;
                    		default:
                    			switch(ConditionType.valueOf(operator)) {
                    				case IS_IN: 
                    					query.thereExistsUsage().elementOf(p.getUsage()); 
                    					break;
                    				case IS_GREATER:
                    					query.thereExistsUsage().greaterThan(p.getUsage().get(0)); 
                    					break;
                    				case IS_GREATER_OR_EQUAL:
                    					query.thereExistsUsage().greaterThanOrEqualTo(p.getUsage().get(0)); 
                    					break;
                    				case IS_LESS:
                    					query.thereExistsUsage().lessThan(p.getUsage().get(0)); 
                    					break;
                    				case IS_LESS_OR_EQUAL:
                    					query.thereExistsUsage().lessThanOrEqualTo(p.getUsage().get(0)); 
                    					break;
                    				case IS_NOT_IN:
                    					query.thereExistsUsage().notAnElementOf(p.getUsage()); 
                    					break;
                    				default:
                    					query.thereExistsUsage().elementOf(p.getUsage()); 
                    					break;
                    			}
                    			break;
                    	}
                    }
                    else if(filterProperty instanceof AddressMainFilterProperty) {
                    	AddressMainFilterProperty p = (AddressMainFilterProperty)filterProperty;                    	
                    	switch(quantor) {
	                		default:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN:
	                					query.isMain().equalTo(p.isMain()); 
	                					break;
		            				case IS_NOT_IN:
		            					query.isMain().equalTo(!p.isMain()); 
		            					break;
	                			}
	                			break;
                    	}
                    }
                    else if(filterProperty instanceof AddressDisabledFilterProperty) {
                    	AddressDisabledFilterProperty p = (AddressDisabledFilterProperty)filterProperty;                    	
                    	switch(Quantifier.valueOf(quantor)) {
	                		case FOR_ALL:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN:
	                					query.forAllDisabled().equalTo(p.isDisabled()); 
	                					break;
	                				case IS_NOT_IN:
	                					query.forAllDisabled().equalTo(!p.isDisabled()); 
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN:
	                					query.thereExistsDisabled().equalTo(p.isDisabled()); 
	                					break;
	                				case IS_NOT_IN:
	                					query.thereExistsDisabled().equalTo(!p.isDisabled()); 
	                					break;
	                			}
	                			break;
                    	}
                	}
                    else if(filterProperty instanceof AddressAccountMembershipFilterProperty) {
                    	AddressAccountMembershipFilterProperty p = (AddressAccountMembershipFilterProperty)filterProperty;
                    	switch(Quantifier.valueOf(quantor)) {
	                		case FOR_ALL:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN:
	                                	query.account().thereExistsAccountMembership().forAllAccountFrom().elementOf(p.getAccount());
	                                	query.account().thereExistsAccountMembership().distance().equalTo(-1);
	                					break;
	                				case IS_NOT_IN:
	                                	query.account().thereExistsAccountMembership().forAllAccountFrom().notAnElementOf(p.getAccount());
	                                	query.account().thereExistsAccountMembership().distance().equalTo(-1);
	                					break;
	                			}
	                			break;
	                		default:
	                			switch(ConditionType.valueOf(operator)) {
	                				case IS_IN:
	                                	query.account().thereExistsAccountMembership().thereExistsAccountFrom().elementOf(p.getAccount());
	                                	query.account().thereExistsAccountMembership().distance().equalTo(-1);
	                					break;
	                				case IS_NOT_IN:
	                                	query.account().thereExistsAccountMembership().thereExistsAccountFrom().notAnElementOf(p.getAccount());
	                                	query.account().thereExistsAccountMembership().distance().equalTo(-1);
	                					break;
	                			}
	                			break;
	                    }
                    }
                }
            }
        }
        if(!hasQueryFilterClause && forCounting) {
        	org.openmdx.base.query.Extension queryFilter = PersistenceHelper.newQueryExtension(query);
        	queryFilter.setClause(
        		Database_1_Attributes.HINT_COUNT + "(1=1)"
        	);
        }
        return query;
    }
        
    //-------------------------------------------------------------------------
    public int countFilteredAddress(
    	AbstractFilterAddress addressFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(addressFilter);
    	AccountAddressQuery query = this.getFilteredAddressQuery(
    		addressFilter, 
    		true 
    	);
    	List<AccountAddress> addresses =
    		((org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(addressFilter.refGetPath().getPrefix(5))).getAddress(query);
        return addresses.size();
    }
        
    //-------------------------------------------------------------------------
    public int moveAddressToAccount(
    	AccountAddress source,
        Account targetAccount,
        Date updateRelationshipsSince,
        Date updateRelationshipsBefore        
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(source);
    	
    	// Clone address
    	AccountAddress target = null;
    	// Try to find address on target account. Clone it if it does not exist.
    	if(source instanceof EMailAddress) {
    		EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
    		query.thereExistsEmailAddress().equalTo(((EMailAddress)source).getEmailAddress());
    		List<EMailAddress> addresses = targetAccount.getAddress(query);
    		target = addresses.isEmpty() ? null : addresses.iterator().next();
    	} else if(source instanceof PhoneNumber) {
    		PhoneNumberQuery query = (PhoneNumberQuery)pm.newQuery(PhoneNumber.class);
    		query.thereExistsPhoneNumberFull().equalTo(((PhoneNumber)source).getPhoneNumberFull());
    		List<PhoneNumber> addresses = targetAccount.getAddress(query);
    		target = addresses.isEmpty() ? null : addresses.iterator().next();
    	} else if(source instanceof WebAddress) {
    		WebAddressQuery query = (WebAddressQuery)pm.newQuery(WebAddress.class);
    		query.thereExistsWebUrl().equalTo(((WebAddress)source).getWebUrl());
    		List<WebAddress> addresses = targetAccount.getAddress(query);
    		target = addresses.isEmpty() ? null : addresses.iterator().next();    	
    	} else if(source instanceof Room) {
    		RoomQuery query = (RoomQuery)pm.newQuery(Room.class);
    		query.thereExistsRoomNumber().equalTo(((Room)source).getRoomNumber());
    		List<Room> addresses = targetAccount.getAddress(query);
    		target = addresses.isEmpty() ? null : addresses.iterator().next();    		
    	}
    	if(target == null) {
	    	target = (AccountAddress)Cloneable.getInstance().cloneObject(
	    		source, 
	    		targetAccount, 
	    		source.refGetPath().getParent().getBase(), 
	    		null, // objectMarshallers
	    		"", // referenceFilterAsString
	    		targetAccount.getOwningUser(), 
	    		targetAccount.getOwningGroup()
	    	);
    	}
    	return this.moveAddress(
    		source, 
    		target, 
    		updateRelationshipsSince, 
    		updateRelationshipsBefore
    	);
    }
    
    //-------------------------------------------------------------------------
    public int moveAddress(
    	AccountAddress source,
        AccountAddress target,
        Date updateRelationshipsSince,
        Date updateRelationshipsBefore        
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(source);
    	String providerName = source.refGetPath().get(2);
    	String segmentName = source.refGetPath().get(4);
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getInstance().getActivitySegment(pm, providerName, segmentName);
    	Account sourceAccount = source.getAccount();
    	Account targetAccount = target.getAccount();
    	int count = 0;
    	// Update activity1::AddressGroupMember::address
    	{
	    	AddressGroupMemberQuery query = (AddressGroupMemberQuery)PersistenceHelper.newQuery(
	    		pm.getExtent(AddressGroupMember.class),
	    		activitySegment.refGetPath().getDescendant("addressGroup", ":*", "member", ":*")
	    	);
	    	query.thereExistsAddress().equalTo(source);
	    	if(updateRelationshipsSince != null) {
	    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
	    	}
	    	if(updateRelationshipsBefore != null) {
	    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
	    	}
	    	List<AddressGroupMember> members = new ArrayList<AddressGroupMember>();
	    	List<AddressGroupMember> addressGroupMembers = activitySegment.getExtent(query);
	    	members.addAll(addressGroupMembers);
	    	for(AddressGroupMember member: members) {
	    		member.setAddress(target);
	    		count++;
	    	}
    	}
    	// Update activity1::EMail::sender
    	{
	    	EMailQuery query = (EMailQuery)PersistenceHelper.newQuery(
	    		pm.getExtent(EMail.class),
	    		activitySegment.refGetPath().getDescendant("activity", ":*")
	    	);
	    	query.thereExistsSender().equalTo(source);
	    	if(updateRelationshipsSince != null) {
	    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
	    	}
	    	if(updateRelationshipsBefore != null) {
	    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
	    	}
	    	List<EMail> members = new ArrayList<EMail>();
	    	List<EMail> emails = activitySegment.getActivity(query);
	    	members.addAll(emails);
	    	for(EMail member: members) {
	    		member.setSender(target);
	    		count++;
	    	}
    	}
    	
    	// Update e-mail recipients
    	{
	    	EMailRecipientQuery query = (EMailRecipientQuery)PersistenceHelper.newQuery(
	    		pm.getExtent(EMailRecipient.class),
	    		activitySegment.refGetPath().getDescendant("activity", ":*", "emailRecipient", ":*")
	    	);
	    	query.thereExistsParty().equalTo(source);
	    	if(updateRelationshipsSince != null) {
	    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
	    	}
	    	if(updateRelationshipsBefore != null) {
	    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
	    	}
	    	List<EMailRecipient> members = new ArrayList<EMailRecipient>();
	    	List<EMailRecipient> recipients = activitySegment.getExtent(query);
	    	members.addAll(recipients);
	    	for(EMailRecipient member: members) {
	    		member.setParty(target);
	    		count++;
	    	}
    	}
    	
    	// Update incident parties
    	{
    		if(source instanceof EMailAddress) {
		    	IncidentPartyQuery query = (IncidentPartyQuery)PersistenceHelper.newQuery(
		    		pm.getExtent(IncidentParty.class),
		    		activitySegment.refGetPath().getDescendant("activity", ":*", "incidentParty", ":*")
		    	);
		    	query.thereExistsParty().equalTo(sourceAccount);
		    	query.thereExistsEmailHint().equalTo(((EMailAddress)source).getEmailAddress());
		    	if(updateRelationshipsSince != null) {
		    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
		    	}
		    	if(updateRelationshipsBefore != null) {
		    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
		    	}
		    	List<IncidentParty> members = new ArrayList<IncidentParty>();
		    	List<IncidentParty> parties = activitySegment.getExtent(query);
		    	members.addAll(parties);
		    	for(IncidentParty member: members) {
		    		member.setParty(targetAccount);		    		
		    		count++;
		    	}
    		}
    	}

    	// Update meeting parties
    	{
    		if(source instanceof EMailAddress) {
		    	MeetingPartyQuery query = (MeetingPartyQuery)PersistenceHelper.newQuery(
		    		pm.getExtent(MeetingParty.class),
		    		activitySegment.refGetPath().getDescendant("activity", ":*", "meetingParty", ":*")
		    	);
		    	query.thereExistsParty().equalTo(sourceAccount);
		    	query.thereExistsEmailHint().equalTo(((EMailAddress)source).getEmailAddress());
		    	if(updateRelationshipsSince != null) {
		    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
		    	}
		    	if(updateRelationshipsBefore != null) {
		    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
		    	}
		    	List<MeetingParty> members = new ArrayList<MeetingParty>();
		    	List<MeetingParty> parties = activitySegment.getExtent(query);
		    	members.addAll(parties);
		    	for(MeetingParty member: members) {
		    		member.setParty(targetAccount);		    		
		    		count++;
		    	}
    		}	    	
    	}
    	
    	// Update task parties
    	{
    		if(source instanceof EMailAddress) {
		    	TaskPartyQuery query = (TaskPartyQuery)PersistenceHelper.newQuery(
		    		pm.getExtent(TaskParty.class),
		    		activitySegment.refGetPath().getDescendant("activity", ":*", "taskParty", ":*")
		    	);
		    	query.thereExistsParty().equalTo(sourceAccount);
		    	query.thereExistsEmailHint().equalTo(((EMailAddress)source).getEmailAddress());
		    	if(updateRelationshipsSince != null) {
		    		query.createdAt().greaterThanOrEqualTo(updateRelationshipsSince);
		    	}
		    	if(updateRelationshipsBefore != null) {
		    		query.createdAt().lessThanOrEqualTo(updateRelationshipsBefore);
		    	}
		    	List<TaskParty> members = new ArrayList<TaskParty>();
		    	List<TaskParty> parties = activitySegment.getExtent(query);
		    	members.addAll(parties);
		    	for(TaskParty member: members) {
		    		member.setParty(targetAccount);		    		
		    		count++;
		    	}
    		}
    	}

    	// Disable source address
    	source.setDisabled(true);
    	
    	return count;
    }

    //-----------------------------------------------------------------------
    public void removeAccount(
        Account account,
        boolean preDelete
    ) throws ServiceException {
    	// Override for custom-specific implementation
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final int MAIL_BUSINESS = 0;
    public static final int MAIL_HOME = 1;
    public static final int PHONE_BUSINESS = 2;
    public static final int PHONE_HOME = 3;
    public static final int FAX_BUSINESS = 4;
    public static final int FAX_HOME = 5;
    public static final int POSTAL_BUSINESS = 6;
    public static final int WEB_BUSINESS = 7;
    public static final int POSTAL_HOME = 8;
    public static final int WEB_HOME = 9;
    public static final int MOBILE = 10;
    public static final int PHONE_OTHER = 11;

    public static final short MEMBER_ROLE_EMPLOYEE = 11;
    public static final short MEMBER_ROLE_ASSISTANT = 17;

    public static final short MEMBER_QUALITY_NORMAL = 5;
    
}

//--- End of File -----------------------------------------------------------

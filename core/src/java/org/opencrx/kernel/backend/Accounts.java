/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Accounts.java,v 1.84 2011/04/13 13:15:03 wfro Exp $
 * Description: Accounts
 * Revision:    $Revision: 1.84 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/04/13 13:15:03 $
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
import org.opencrx.kernel.base.jmi1.AttributeFilterProperty;
import org.opencrx.kernel.contract1.jmi1.AbstractContract;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
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
    	
    //-----------------------------------------------------------------------
    public String getVCardUid(
    	String vcard
    ) {
    	String uid = null;
    	if(vcard.indexOf("UID:") > 0) {
    		int start = vcard.indexOf("UID:");
    		int end = vcard.indexOf("\n", start);
    		if(end > start) {
    			uid = vcard.substring(start + 4, end).trim();
    		}
    	}    	
    	return uid;
    }

    //-------------------------------------------------------------------------
    public void initContract(
        AbstractContract contract,
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
            true,
            null
        );
    }
    
    //-------------------------------------------------------------------------
    public Lead createLead(
        Account account,
        String name,
        String description,
        String nextStep,
        Lead basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = 
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + account.refGetPath().get(2) + "/segment/" + account.refGetPath().get(4))
        	);
    	Lead contract = null;
    	if(basedOn != null) {
    		contract = (Lead)Cloneable.getInstance().cloneObject(
    			basedOn, 
    			contractSegment, 
    			"lead", 
    			null, // object marshallers
    			null, // reference filter
    			null, // owning user
    			null // owningGroup
    		);    		
    	}
    	else {
	        contract = pm.newInstance(Lead.class);
	        contract.refInitialize(false, false);
	        contractSegment.addLead(
	        	false,
	        	this.getUidAsString(),
	        	contract
	        );
    	}
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
    public Opportunity createOpportunity(
        Account account,
        String name,
        String description,
        String nextStep,
        Opportunity basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = 
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + account.refGetPath().get(2) + "/segment/" + account.refGetPath().get(4))
        	);
    	Opportunity contract = null;
    	if(basedOn != null) {
    		contract = (Opportunity)Cloneable.getInstance().cloneObject(
    			basedOn, 
    			contractSegment, 
    			"opportunity", 
    			null, // object marshallers
    			null, // reference filter
    			null, // owning user
    			null // owningGroup
    		);    		
    	}
    	else {
	        contract = pm.newInstance(Opportunity.class);
	        contract.refInitialize(false, false);
	        contractSegment.addOpportunity(
	        	false,
	        	this.getUidAsString(),
	        	contract
	        );
    	}
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
    public Quote createQuote(
        Account account,
        String name,
        String description,
        Quote basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = 
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + account.refGetPath().get(2) + "/segment/" + account.refGetPath().get(4))
        	);
    	Quote contract = null;
    	if(basedOn != null) {
    		contract = (Quote)Cloneable.getInstance().cloneObject(
    			basedOn, 
    			contractSegment, 
    			"quote", 
    			null, // object marshallers
    			null, // reference filter
    			null, // owning user
    			null // owningGroup
    		);    		
    	}
    	else {
	        contract = pm.newInstance(Quote.class);
	        contract.refInitialize(false, false);
	        contractSegment.addQuote(
	        	false,
	        	this.getUidAsString(),
	        	contract
	        );
    	}
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    public SalesOrder createSalesOrder(
        Account account,
        String name,
        String description,
        SalesOrder basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = 
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + account.refGetPath().get(2) + "/segment/" + account.refGetPath().get(4))
        	);
    	SalesOrder contract = null;
    	if(basedOn != null) {
    		contract = (SalesOrder)Cloneable.getInstance().cloneObject(
    			basedOn, 
    			contractSegment, 
    			"salesOrder", 
    			null, // object marshallers
    			null, // reference filter
    			null, // owning user
    			null // owningGroup
    		);    		
    	}
    	else {
	        contract = pm.newInstance(SalesOrder.class);
	        contract.refInitialize(false, false);
	        contractSegment.addSalesOrder(
	        	false,
	        	this.getUidAsString(),
	        	contract
	        );
    	}
        this.initContract(
            contract,
            account,
            name,
            description
        );
        return contract;
    }
    
    //-------------------------------------------------------------------------
    public Invoice createInvoice(
        Account account,
        String name,
        String description,
        Invoice basedOn
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        org.opencrx.kernel.contract1.jmi1.Segment contractSegment = 
        	(org.opencrx.kernel.contract1.jmi1.Segment)pm.getObjectById(
        		new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/" + account.refGetPath().get(2) + "/segment/" + account.refGetPath().get(4))
        	);
    	Invoice contract = null;
    	if(basedOn != null) {
    		contract = (Invoice)Cloneable.getInstance().cloneObject(
    			basedOn, 
    			contractSegment, 
    			"invoice", 
    			null, // object marshallers
    			null, // reference filter
    			null, // owning user
    			null // owningGroup
    		);    		
    	}
    	else {
	        contract = pm.newInstance(Invoice.class);
	        contract.refInitialize(false, false);
	        contractSegment.addInvoice(
	        	false,
	        	this.getUidAsString(),
	        	contract
	        );
    	}
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
        String uid = this.getVCardUid(vcard);
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
     * 
     * @param rootPkg          The root package to be used for this request
     * @param providerName     The name of the current provider
     * @param segmentName      The name of the current segment
     * @param emailAddress     The email address
     * @return                 A List of accounts containing the email address
     */
    public List<EMailAddress> lookupEmailAddress(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String emailAddress
    ) {
        EMailAddressQuery query = (EMailAddressQuery)pm.newQuery(EMailAddress.class);
        org.opencrx.kernel.account1.jmi1.Segment accountSegment =
            this.getAccountSegment(
                pm,
                providerName,
                segmentName
            );
        query.thereExistsEmailAddress().like(
           "(?i).*" + emailAddress.replace(".", "\\.") + ".*" 
        );
       	query.forAllDisabled().isFalse();
       	List<EMailAddress> addresses = accountSegment.getAddress(query);
       	if(addresses.isEmpty() || addresses.size() == 1) {
       		return addresses;
       	}
       	else {
       		List<EMailAddress> activeAddresses = new ArrayList<EMailAddress>();
       		for(EMailAddress address: addresses) {
       			Account account = (Account)pm.getObjectById(address.refGetPath().getParent().getParent());
       			if(account.isDisabled() == null || !account.isDisabled()) {
       				activeAddresses.add(address);
       			}
       		}
       		return activeAddresses;
       	}
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
        Account account
    ) throws ServiceException {
        Collection<AccountAddress> addresses = account.getAddress();
        String emailAddress = null;
        for(AccountAddress address: addresses) {
            if(address instanceof EMailAddress) {
                String adr = ((EMailAddress)address).getEmailAddress();
                if((emailAddress == null) && (adr != null)) {
                    emailAddress = adr;
                }
                if(adr != null && address.isMain() && address.getUsage().contains(Addresses.USAGE_BUSINESS)) {
                    emailAddress = adr;
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
        org.opencrx.kernel.account1.jmi1.Account account
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
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
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
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
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
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
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
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
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
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
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

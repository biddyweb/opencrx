/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Accounts.java,v 1.24 2008/10/06 21:08:46 wfro Exp $
 * Description: Accounts
 * Revision:    $Revision: 1.24 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/06 21:08:46 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opencrx.kernel.account1.jmi1.EmailAddress;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.account1.jmi1.WebAddress;
import org.opencrx.kernel.contract1.jmi1.AbstractContract;
import org.opencrx.kernel.contract1.jmi1.Invoice;
import org.opencrx.kernel.contract1.jmi1.Lead;
import org.opencrx.kernel.contract1.jmi1.Opportunity;
import org.opencrx.kernel.contract1.jmi1.Quote;
import org.opencrx.kernel.contract1.jmi1.SalesOrder;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.w3c.cci2.Datatypes;

public class Accounts {

    //-----------------------------------------------------------------------
    public Accounts(
        Backend backend
    ) {
        this.backend = backend;
        this.vcards = new VCard(
            this.backend
        );                
    }

    //-------------------------------------------------------------------------
    public void completeOuMembership(
        DataproviderObject_1_0 contact
    ) {
        try {
            // find on ContactMembership extent
            List ouMemberships = new ArrayList();
            
            // organization memberships
            ouMemberships.addAll(
                this.backend.getDelegatingRequests().addFindRequest(
	                contact.path().getPrefix(5).getChild("extent"),
	                new FilterProperty[]{
		                new FilterProperty(
				            Quantors.THERE_EXISTS,
				            "identity",
				            FilterOperators.IS_LIKE,
				            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/contactMembership/:*"
				        ),
		                new FilterProperty(
				            Quantors.THERE_EXISTS,
				            "contact",
				            FilterOperators.IS_IN,
				            contact.path()
				        )
	                }
	            )
            );

            // organizational unit memberships
            ouMemberships.addAll(
                this.backend.getDelegatingRequests().addFindRequest(
	                contact.path().getPrefix(5).getChild("extent"),
	                new FilterProperty[]{
		                new FilterProperty(
				            Quantors.THERE_EXISTS,
				            "identity",
				            FilterOperators.IS_LIKE,
				            "xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/contactMembership/:*"
				        ),
		                new FilterProperty(
				            Quantors.THERE_EXISTS,
				            "contact",
				            FilterOperators.IS_IN,
				            contact.path()
				        )
	                }
	            )
            );

            Set memberships = new HashSet();
            for(Iterator i = ouMemberships.iterator(); i.hasNext(); ) {
                memberships.add(
                    ((DataproviderObject_1_0)i.next()).path().getParent().getParent()
                );
            }
            contact.clearValues("ouMembership").addAll(memberships);
        }
        catch(ServiceException e) {
            AppLog.info(e.getMessage(), e.getCause());
        }
    }
    
    //-------------------------------------------------------------------------
    public AbstractContract createContract(
        Path accountIdentity,
        DataproviderObject newContract,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = null;
        try {
            // Create new contract based on existing contract
            if(basedOnIdentity != null) {
                DataproviderObject_1_0 contractBase = this.backend.retrieveObjectFromDelegation(
                    basedOnIdentity
                );
                Path contractIdentity = this.backend.getCloneable().cloneAndUpdateReferences(
                    contractBase,
                    contractBase.path().getParent(),
                    null,
                    DEFAULT_REFERENCE_FILTER,
                    false,
                    AttributeSelectors.ALL_ATTRIBUTES                    
                ).path();
                contract = this.backend.retrieveObjectForModification(
                    contractIdentity
                );
            }
            // Create new contract
            else {
                newContract.values("priority").add(new Short((short)0));
                newContract.values("contractCurrency").add(new Short((short)0));
                newContract.values("paymentTerms").add(new Short((short)0));
                newContract.values("contractLanguage").add(new Short((short)0));
                newContract.values("contractState").add(new Short((short)0));
                newContract.values("pricingState").add(new Short((short)0));
                newContract.values("shippingMethod").add(new Short((short)0));
                this.backend.getDelegatingRequests().addCreateRequest(
                    newContract
                );
                contract = this.backend.retrieveObjectForModification(
                    newContract.path()
                );
            }
            
            // Update supplied contract attributes
            if(name != null) {
                contract.clearValues("name").add(name);
            }
            if(description != null) {
                contract.clearValues("description").add(description);
            }
            if(nextStep != null) {
                contract.clearValues("nextStep").add(nextStep);
            }
            // customer
            contract.clearValues("customer").add(accountIdentity);
            // activeOn
            contract.clearValues("activeOn").add(
                DateFormat.getInstance().format(new Date())
            );
            
            // assign to current user
            this.backend.getBase().assignToMe(
                contract,
                null,
                true,
                null
            );
        }
        catch(ServiceException e) {
            AppLog.info(e.getMessage(), e.getCause());
        }
        return contract == null
            ? null
            : (AbstractContract)this.backend.getDelegatingPkg().refObject(contract.path().toXri());
    }
    
    //-------------------------------------------------------------------------
    public Lead createLead(
        Path accountIdentity,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = new DataproviderObject(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider").getDescendant(
                new String[]{accountIdentity.get(2), "segment", accountIdentity.get(4), "lead", this.backend.getUidAsString()}
            )
        );
        contract.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Lead");
        contract.values("leadSource").add(new Short((short)0));
        contract.values("leadRating").add(new Short((short)0));
        contract.values("closeProbability").add(new Short((short)0));
        return (Lead)this.createContract(
            accountIdentity,
            contract,
            name,
            description,
            nextStep,
            basedOnIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public Opportunity createOpportunity(
        Path accountIdentity,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = new DataproviderObject(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider").getDescendant(
                new String[]{accountIdentity.get(2), "segment", accountIdentity.get(4), "opportunity", this.backend.getUidAsString()}
            )
        );
        contract.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Opportunity");
        contract.values("opportunitySource").add(new Short((short)0));
        contract.values("opportunityRating").add(new Short((short)0));
        contract.values("closeProbability").add(new Short((short)0));
        return (Opportunity)this.createContract(
            accountIdentity,
            contract,
            name,
            description,
            nextStep,
            basedOnIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public Quote createQuote(
        Path accountIdentity,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = new DataproviderObject(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider").getDescendant(
                new String[]{accountIdentity.get(2), "segment", accountIdentity.get(4), "quote", this.backend.getUidAsString()}
            )
        );
        contract.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Quote");
        contract.values("freightTerms").add(new Short((short)0));
        contract.values("closeProbability").add(new Short((short)0));
        return (Quote)this.createContract(
            accountIdentity,
            contract,
            name,
            description,
            nextStep,
            basedOnIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public SalesOrder createSalesOrder(
        Path accountIdentity,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = new DataproviderObject(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider").getDescendant(
                new String[]{accountIdentity.get(2), "segment", accountIdentity.get(4), "salesOrder", this.backend.getUidAsString()}
            )
        );
        contract.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:SalesOrder");
        contract.values("submitStatus").add(new Short((short)0));
        contract.values("freightTerms").add(new Short((short)0));
        return (SalesOrder)this.createContract(
            accountIdentity,
            contract,
            name,
            description,
            nextStep,
            basedOnIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public Invoice createInvoice(
        Path accountIdentity,
        String name,
        String description,
        String nextStep,
        Path basedOnIdentity
    ) {
        DataproviderObject contract = new DataproviderObject(
            new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider").getDescendant(
                new String[]{accountIdentity.get(2), "segment", accountIdentity.get(4), "invoice", this.backend.getUidAsString()}
            )
        );
        contract.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:contract1:Invoice");
        return (Invoice)this.createContract(
            accountIdentity,
            contract,
            name,
            description,
            nextStep,
            basedOnIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    public void updateFullName(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        // org:opencrx:kernel:account1:Contact:fullName
        if(this.backend.isContact(object)) {
            List<Object> lastName = this.backend.getNewValue("lastName", object, oldValues);
            List<Object> firstName = this.backend.getNewValue("firstName", object, oldValues);
            List<Object> middleName = this.backend.getNewValue("middleName", object, oldValues);
            object.clearValues("fullName").add(
                ( (lastName.isEmpty() ? "" : lastName.get(0)) + ", "
                  + (firstName.isEmpty() ? "" : firstName.get(0) + " ")
                  + (middleName.isEmpty() ? "" : middleName.get(0) + "") ).trim()
            );
        }
        // org:opencrx:kernel:account1:AbstractGroup
        else if(this.backend.isAbstractGroup(object)) {
            List<Object> name = this.backend.getNewValue("name", object, oldValues);
            object.clearValues("fullName").add(
                name.isEmpty() ? "" : name.get(0)
            );
        }
    }
    
    //-------------------------------------------------------------------------
    public void updateAccount(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        if(this.backend.isAccount(object)) {
            this.updateFullName(
                object, 
                oldValues
            );
            List<String> statusMessage = new ArrayList<String>();
            String vcard = this.vcards.mergeVcard(
                object,
                (String)object.values("vcard").get(0),
                statusMessage
            );
            object.clearValues("vcard").add(
                vcard == null ? "" : vcard
            );
        }
    }
    
    //-------------------------------------------------------------------------
    public FilterProperty[] getAccountFilterProperties(
        Path accountFilterIdentity,
        boolean forCounting
    ) throws ServiceException {
        List<DataproviderObject_1_0> filterProperties = this.backend.getDelegatingRequests().addFindRequest(
            accountFilterIdentity.getChild("accountFilterProperty"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            null,
            0, 
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );
        List<FilterProperty> filter = new ArrayList<FilterProperty>();
        boolean hasQueryFilterClause = false;
        for(
            Iterator<DataproviderObject_1_0> i = filterProperties.iterator();
            i.hasNext();
        ) {
            DataproviderObject_1_0 filterProperty = i.next();
            String filterPropertyClass = (String)filterProperty.values(SystemAttributes.OBJECT_CLASS).get(0);

            Boolean isActive = (Boolean)filterProperty.values("isActive").get(0);            
            if((isActive != null) && isActive.booleanValue()) {
                // Query filter
                if("org:opencrx:kernel:account1:AccountQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            (forCounting ? Database_1_Attributes.HINT_COUNT : "") + filterProperty.values("clause").get(0)
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            Database_1_Attributes.QUERY_FILTER_CLASS
                        )
                    );
                    // stringParam
                    List<Object> values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // integerParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // decimalParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // booleanParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<String> j = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATE_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(XMLGregorianCalendar.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATE_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    // dateTimeParam
                    values = new ArrayList<Object>();
                    for(
                        Iterator<String> j = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM).iterator();
                        j.hasNext();
                    ) {
                        values.add(
                            Datatypes.create(Date.class, j.next())
                        );
                    }
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DATETIME_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray()
                        )
                    );
                    hasQueryFilterClause = true;
                }
                // Attribute filter
                else {
                    // Get filterOperator, filterQuantor
                    short filterOperator = filterProperty.values("filterOperator").size() == 0
                        ? FilterOperators.IS_IN
                        : ((Number)filterProperty.values("filterOperator").get(0)).shortValue();
                    filterOperator = filterOperator == 0
                        ? FilterOperators.IS_IN
                        : filterOperator;
                    short filterQuantor = filterProperty.values("filterQuantor").size() == 0
                        ? Quantors.THERE_EXISTS
                        : ((Number)filterProperty.values("filterQuantor").get(0)).shortValue();
                    filterQuantor = filterQuantor == 0
                        ? Quantors.THERE_EXISTS
                        : filterQuantor;
                    
                    if("org:opencrx:kernel:account1:AccountTypeFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "accountType",
                                filterOperator,
                                filterProperty.values("accountType").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AccountCategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "accountCategory",
                                filterOperator,
                                filterProperty.values("accountCategory").toArray()                    
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:CategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterProperty.values("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:DisabledFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "disabled",
                                filterOperator,
                                filterProperty.values("disabled").toArray()
                            )
                        );
                    }
                }
            }
        }        
        if(!hasQueryFilterClause && forCounting) {
            String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
            // Clause and class
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.HINT_COUNT + "(1=1)"
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    Database_1_Attributes.QUERY_FILTER_CLASS
                )
            );            
        }
        return filter.toArray(new FilterProperty[filter.size()]);
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredAccount(
        Path accountFilterIdentity
    ) throws ServiceException {
        List accounts = this.backend.getDelegatingRequests().addFindRequest(
            new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/" + accountFilterIdentity.get(2) + "/segment/" + accountFilterIdentity.get(4) + "/account"),
            this.getAccountFilterProperties(
                accountFilterIdentity, 
                true
            ),
            AttributeSelectors.NO_ATTRIBUTES,
            null,
            0, 
            1,
            Directions.ASCENDING
        );
        return accounts.size();
    }
        
    //-----------------------------------------------------------------------
    /**
     * @return Returns the accountSegment.
     */
    public static org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.account1.jmi1.Segment) pm.getObjectById(
            "xri:@openmdx:org.opencrx.kernel.account1/provider/"
            + providerName + "/segment/" + segmentName
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
    public static List<org.opencrx.kernel.account1.jmi1.EmailAddress> lookupEmailAddress(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String emailAddress,
        boolean caseInsensitiveAddressLookup
    ) {
        org.opencrx.kernel.account1.cci2.EmailAddressQuery query = 
            Utils.getAccountPackage(pm).createEmailAddressQuery();
        org.opencrx.kernel.account1.jmi1.Segment accountSegment =
            Accounts.getAccountSegment(
                pm,
                providerName,
                segmentName
            );
        query.identity().like(
            Utils.xriAsIdentityPattern(accountSegment.refMofId() + "/account/:*/address/:*")  
        );
        query.thereExistsEmailAddress().like(
           caseInsensitiveAddressLookup
               ? "\\(\\?i\\)" + emailAddress
               : emailAddress
        );
        return accountSegment.getExtent(query);        
    }
        
    //-------------------------------------------------------------------------
    private static List<org.opencrx.kernel.account1.jmi1.AccountAddress> getAccountAddresses(
        org.opencrx.kernel.account1.jmi1.Account account,
        short usage
    ) {
        org.opencrx.kernel.account1.cci2.AccountAddressQuery query = 
            ((org.opencrx.kernel.account1.jmi1.Account1Package)account.refImmediatePackage()).createAccountAddressQuery();
        query.thereExistsUsage().equalTo(usage);
        return account.getAddress(query); 
    }
    
    //-------------------------------------------------------------------------
    /**
     * Return main home and business addresses of given account.
     * @return array with elements {web home, web business, phone home, phone business, 
     *         fax home, fax, business, postal home, postal business,
     *         mail home, mail business, mobile, phone other} 
     */
    public static org.opencrx.kernel.account1.jmi1.AccountAddress[] getMainAddresses(
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
        
        org.opencrx.kernel.account1.jmi1.AccountAddress[] mainAddresses = 
            new org.opencrx.kernel.account1.jmi1.AccountAddress[12];
        // HOME
        List<org.opencrx.kernel.account1.jmi1.AccountAddress> addresses = getAccountAddresses(
            account,
            Addresses.USAGE_HOME
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof WebAddress) {
                int orderHomeWeb = 1;
                WebAddress webAddress = (WebAddress)address;
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
                int orderHomePostal = 1;
                PostalAddress postalAddress = (PostalAddress)address;
                if(orderHomePostal > currentOrderHomePostal) {       
                    mainAddresses[POSTAL_HOME] = postalAddress;
                    currentOrderHomePostal = orderHomePostal;
                }
            }
            else if(address instanceof EmailAddress) {
                int orderHomeMail = 1;
                EmailAddress mailAddress = (EmailAddress)address;
                if(orderHomeMail > currentOrderHomeMail) {
                    mainAddresses[MAIL_HOME] = mailAddress;
                    currentOrderHomeMail = orderHomeMail;
                }
            }
        }    
        // BUSINESS
        addresses = getAccountAddresses(
            account,
            Addresses.USAGE_BUSINESS
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof WebAddress) {
                WebAddress webAddress = (WebAddress)address;
                int orderBusinessWeb = 1;
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
                } catch(Exception e) {}
                int orderBusinessPhone = isMain ? 3 : 1;
                if(orderBusinessPhone > currentOrderBusinessPhone) {
                    mainAddresses[PHONE_BUSINESS] = phoneNumber;
                    currentOrderBusinessPhone = orderBusinessPhone;
                }
            }
            else if (address instanceof PostalAddress) {
                PostalAddress postalAddress = (PostalAddress)address;
                int orderBusinessPostal = 1;
                if(orderBusinessPostal > currentOrderBusinessPostal) {
                    mainAddresses[POSTAL_BUSINESS] = postalAddress;
                    currentOrderBusinessPostal = orderBusinessPostal;
                }
            }
            else if(address instanceof EmailAddress) {
                EmailAddress mailAddress = (EmailAddress)address;
                int orderBusinessMail = 1;
                if(orderBusinessMail > currentOrderBusinessMail) {
                    mainAddresses[MAIL_BUSINESS] = mailAddress;
                    currentOrderBusinessMail = orderBusinessMail;
                }
            }
        }    
        // OTHER
        addresses = getAccountAddresses(
            account,
            Addresses.USAGE_OTHER
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                
                int orderOtherPhone = 1;
                if(orderOtherPhone > currentOrderOtherPhone) {
                    mainAddresses[PHONE_OTHER] = phoneNumber;
                    currentOrderOtherPhone = orderOtherPhone;
                }
            }
        }    
        // HOME_FAX
        addresses = getAccountAddresses(
            account,
            Addresses.USAGE_HOME_FAX
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                
                int orderHomeFax = 1;
                if(orderHomeFax > currentOrderHomeFax) {           
                    mainAddresses[FAX_HOME] = phoneNumber;
                    currentOrderHomeFax = orderHomeFax;
                }
            }
        }    
        // BUSINESS_FAX
        addresses = getAccountAddresses(
            account,
            Addresses.USAGE_BUSINESS_FAX
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                                
                int orderBusinessFax = 1;
                if(orderBusinessFax > currentOrderBusinessFax) {
                    mainAddresses[FAX_BUSINESS] = phoneNumber;
                    currentOrderBusinessFax = orderBusinessFax;
                }
            }
        }    
        // MOBILE
        addresses = getAccountAddresses(
            account,
            Addresses.USAGE_MOBILE
        );
        for(org.opencrx.kernel.account1.jmi1.AccountAddress address: addresses) {
            if(address instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber)address;                                
                int orderMobile = 1;
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
        Path accountIdentity
    ) throws ServiceException {
        List<String> messages = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();
        List<String> report = new ArrayList<String>();
        String vcard = this.vcards.mergeVcard(
            this.backend.retrieveObject(accountIdentity),
            null, 
            messages
        );        
        byte[] item = null;
        try {
            item = vcard.getBytes("UTF-8");
        } catch(Exception e) {
            item = vcard.getBytes();    
        }
        this.vcards.importItem(
            item, 
            accountIdentity, 
            (short)0, 
            errors, 
            report
        );
    }
    
    //-------------------------------------------------------------------------
    public void markAccountAsDirty(
        Path accountIdentity
    ) throws ServiceException {
        this.backend.retrieveObjectForModification(
            accountIdentity
        );
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String DEFAULT_REFERENCE_FILTER = ":*, :*/:*/:*, :*/:*/:*/:*/:*";
    
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
    
    protected final Backend backend;
    protected final VCard vcards;
    
}

//--- End of File -----------------------------------------------------------

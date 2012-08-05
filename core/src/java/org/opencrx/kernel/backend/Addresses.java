/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Addresses.java,v 1.11 2008/06/06 09:53:27 wfro Exp $
 * Description: Addresses
 * Revision:    $Revision: 1.11 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/06/06 09:53:27 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openmdx.base.exception.ServiceException;
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

public class Addresses {

    //-----------------------------------------------------------------------
    public Addresses(
        Backend backend
    ) {
        this.backend = backend;
    }
        
    //-------------------------------------------------------------------------
    public FilterProperty[] getAddressFilterProperties(
        Path activityFilterIdentity,
        boolean forCounting
    ) throws ServiceException {
        List<DataproviderObject_1_0> filterProperties = this.backend.getDelegatingRequests().addFindRequest(
            activityFilterIdentity.getChild("addressFilterProperty"),
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
                if("org:opencrx:kernel:account1:AddressQueryFilterProperty".equals(filterPropertyClass)) {     
                    String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.backend.getUidAsString() + ":";
                    // Clause and class
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
                            FilterOperators.PIGGY_BACK,
                            new Object[]{
                                (forCounting ? Database_1_Attributes.HINT_COUNT : "") +
                                filterProperty.values("clause").get(0)
                            }
                        )
                    );
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + SystemAttributes.OBJECT_CLASS,
                            FilterOperators.PIGGY_BACK,
                            new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                        )
                    );
                    // stringParam
                    List<Object> values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_STRING_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new String[values.size()])
                        )
                    );
                    // integerParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_INTEGER_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new Integer[values.size()])
                        )
                    );
                    // decimalParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_DECIMAL_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new BigDecimal[values.size()])
                        )
                    );
                    // booleanParam
                    values = filterProperty.values(Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM);
                    filter.add(
                        new FilterProperty(
                            Quantors.PIGGY_BACK,
                            queryFilterContext + Database_1_Attributes.QUERY_FILTER_BOOLEAN_PARAM,
                            FilterOperators.PIGGY_BACK,
                            values.toArray(new Boolean[values.size()])
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
                            values.toArray(new XMLGregorianCalendar[values.size()])
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
                            values.toArray(new Date[values.size()])
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
                    
                    if("org:opencrx:kernel:account1:AddressCategoryFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "category",
                                filterOperator,
                                filterProperty.values("category").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressTypeFilterProperty".equals(filterPropertyClass)) {
                        List<String> addressTypes = new ArrayList<String>();
                        for(Iterator<Number> j = filterProperty.values("addressType").iterator(); j.hasNext(); ) {
                            int addressType = j.next().intValue();
                            addressTypes.add(
                                Addresses.ADDRESS_TYPES[addressType]
                            );
                        }
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                SystemAttributes.OBJECT_INSTANCE_OF,
                                filterOperator,
                                addressTypes.toArray()
                            )
                        );
                    }                    
                    else if("org:opencrx:kernel:account1:AddressMainFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "isMain",
                                filterOperator,
                                filterProperty.values("isMain").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressUsageFilterProperty".equals(filterPropertyClass)) {
                        filter.add(
                            new FilterProperty(
                                filterQuantor,
                                "usage",
                                filterOperator,
                                filterProperty.values("usage").toArray()
                            )
                        );
                    }
                    else if("org:opencrx:kernel:account1:AddressDisabledFilterProperty".equals(filterPropertyClass)) {
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
                    new Object[]{
                        Database_1_Attributes.HINT_COUNT + "(1=1)"
                    }
                )
            );
            filter.add(
                new FilterProperty(
                    Quantors.PIGGY_BACK,
                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
                    FilterOperators.PIGGY_BACK,
                    new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
                )
            );            
        }        
        return filter.toArray(new FilterProperty[filter.size()]);
    }
        
    //-------------------------------------------------------------------------
    public int countFilteredAddress(
        Path addressFilterIdentity
    ) throws ServiceException {
        List addresses = this.backend.getDelegatingRequests().addFindRequest(
            addressFilterIdentity.getPrefix(5).getChild("address"),
            this.getAddressFilterProperties(
        		addressFilterIdentity,
        		true
            ),
            AttributeSelectors.NO_ATTRIBUTES,
            null,
            0, 
            1,
            Directions.ASCENDING
        );
        return addresses.size();
    }
        
    //-------------------------------------------------------------------------
    public void updatePhoneNumber(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        if(this.backend.isPhoneNumberAddressable(object)) {
            List<String> phoneNumberFullValues = this.backend.getNewValue("phoneNumberFull", object, oldValues);
            List<Boolean> automaticParsingValues = this.backend.getNewValue("automaticParsing", object, oldValues);
            if(
                !automaticParsingValues.isEmpty() && ((Boolean)automaticParsingValues.get(0)).booleanValue() &&
                !phoneNumberFullValues.isEmpty()
            ) {
                // assuming the phone number format +nn (nnn) nnn-nnnn x nnn
                String phoneNumberFull = phoneNumberFullValues.get(0);
                List<String> parts = new ArrayList<String>();
                StringTokenizer tokenizer = new StringTokenizer(phoneNumberFull, "+()x");
                while(tokenizer.hasMoreTokens()) {
                    parts.add(tokenizer.nextToken());
                }
                if(parts.size() >= 3) {
                    String countryCode = parts.get(0).toString().trim();
                    if("1".equals(countryCode)) {
                        countryCode = "840";
                    }
                    else {
                        Map<String,Short> phoneCountryPrefix = this.backend.getCodes().getShortText(
                            "org:opencrx:kernel:address1:PhoneNumberAddressable:phoneCountryPrefix",
                            (short)0,
                            false
                        );
                        Short code = phoneCountryPrefix.get(countryCode);
                        countryCode = code == null
                            ? "0"
                            : code.toString();
                    }
                    String areaCode = parts.get(1).toString().trim();
                    String localNumber = parts.get(2).toString().trim();
                    String extension = parts.size() >= 4 ? parts.get(3).toString().trim() : "";
    
                    object.clearValues("phoneCountryPrefix").add(new Short(countryCode));
                    object.clearValues("phoneCityArea").add(areaCode);
                    object.clearValues("phoneLocalNumber").add(localNumber);
                    object.clearValues("phoneExtension").add(extension);
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void updateAddress(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        this.updatePhoneNumber(
            object, 
            oldValues
        );
        if(this.backend.isAccountAddress(object)) {
            this.backend.getAccounts().markAccountAsDirty(
                object.path().getParent().getParent()
            );
        }
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String[] ADDRESS_TYPES = 
        new String[]{
            "org:opencrx:kernel:address1:PostalAddressable",
            "org:opencrx:kernel:address1:PhoneNumberAdressable",
            "org:opencrx:kernel:address1:EMailAddressable",
            "org:opencrx:kernel:address1:WebAddressable",
            "org:opencrx:kernel:address1:RoomAddressable"
        };
    
    /**
     * @deprecated
     */
    public static final Short USAGE_BUSINESS_MOBILE = new Short((short)540);
    /**
     * @deprecated
     */
    public static final Short USAGE_HOME_MOBILE = new Short((short)440);
    public static final Short USAGE_MOBILE = new Short((short)200);
    public static final Short USAGE_BUSINESS_FAX = new Short((short)530);
    public static final Short USAGE_HOME_FAX = new Short((short)430);
    /**
     * @deprecated
     */
    public static final Short USAGE_BUSINESS_MAIN_PHONE = new Short((short)520);
    /**
     * @deprecated
     */
    public static final Short USAGE_HOME_MAIN_PHONE = new Short((short)420);
    public static final Short USAGE_OTHER = new Short((short)1800);
    public static final Short USAGE_BUSINESS = new Short((short)500);
    public static final Short USAGE_HOME = new Short((short)400);
    
    public static final Short USAGE_CONTRACT_INVOICE = 10000;
    public static final Short USAGE_CONTRACT_DELIVERY = 10200;
    
    protected final Backend backend;
    
}

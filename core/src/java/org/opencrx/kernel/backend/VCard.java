/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: VCard.java,v 1.10 2008/01/29 15:59:13 wfro Exp $
 * Description: VCard
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/01/29 15:59:13 $
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.EmailAddress;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.account1.jmi1.WebAddress;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.id.UUIDs;

public class VCard {

    //-------------------------------------------------------------------------
    public VCard(
        Backend backend
    ) {
        this.backend = backend;
    }

    //-------------------------------------------------------------------------
    private Short numberAsShort(
        Object number
    ) {
        return new Short(((Number)number).shortValue());
    }

    //-------------------------------------------------------------------------
    public byte[] exportItem(
        Path contactIdentity,
        short locale
    ) throws ServiceException {
        DataproviderObject_1_0 contact = this.backend.retrieveObject(
            contactIdentity
        );
        AppLog.trace("inspecting contact", contact);
        String lastName = contact.values("lastName").size() == 0 ? "" : (String)contact.values("lastName").get(0);
        String firstName = contact.values("firstName").size() == 0 ? "" : (String)contact.values("firstName").get(0);
        String middleName = contact.values("middleName").size() == 0 ? "" : (String)contact.values("middleName").get(0);
        String salutation = contact.values("salutation").size() == 0 ? "" : (String)contact.values("salutation").get(0);
        String suffix = contact.values("suffix").size() == 0 ? "" : (String)contact.values("suffix").get(0);
        String jobTitle = contact.values("jobTitle").size() == 0 ? "" : (String)contact.values("jobTitle").get(0);
        String organization = contact.values("organization").size() == 0 ? "" : (String)contact.values("organization").get(0);
        
        AccountAddress[] addresses = Accounts.getMainAddresses(
            (Account)this.backend.getDelegatingPkg().refObject(contactIdentity.toXri())
        );
        
        String telWorkVoice = addresses[Accounts.PHONE_BUSINESS] == null
            ? ""
            : ((PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull();
        String telHomeVoice = addresses[Accounts.PHONE_HOME] == null
            ? ""
            : ((PhoneNumber)addresses[Accounts.PHONE_HOME]).getPhoneNumberFull();
        String telCellVoice = addresses[Accounts.MOBILE] == null
            ? ""
            : ((PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull();
        String telWorkFax = addresses[Accounts.FAX_BUSINESS] == null
            ? ""
            : ((PhoneNumber)addresses[Accounts.FAX_BUSINESS]).getPhoneNumberFull();
        String telHomeFax = addresses[Accounts.FAX_HOME] == null
            ? ""
            : ((PhoneNumber)addresses[Accounts.FAX_HOME]).getPhoneNumberFull();
        String postalWork = "";
        if(addresses[Accounts.POSTAL_BUSINESS] != null) {
            PostalAddress postalAddress = (PostalAddress)addresses[Accounts.POSTAL_BUSINESS];
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<String> addressLines = postalAddress.getPostalAddressLine();
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(addressLines.get(j));
            }
            // postalStreet
            List<String> postalStreet = postalAddress.getPostalStreet();
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(postalStreet.get(j));
            }
            // postalCity
            adr.append(
                postalAddress.getPostalCity() == null
                    ? ";"
                    : ";" + postalAddress.getPostalCity()
            );
            // postalCode
            adr.append(";");
            adr.append(
                postalAddress.getPostalCode() == null
                    ? ";"
                    : ";" + postalAddress.getPostalCode()
            );
            // postalCountry
            Map postalCountries = this.backend.getCodes().getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true);
            adr.append(";");
            adr.append((String)postalCountries.get(this.numberAsShort(postalAddress.getPostalCountry())));   
            postalWork = adr.toString();
        }
        String postalHome = "";
        if(addresses[Accounts.POSTAL_HOME] != null) {
            PostalAddress postalAddress = (PostalAddress)addresses[Accounts.POSTAL_HOME];
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<String> addressLines = postalAddress.getPostalAddressLine();
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(addressLines.get(j));
            }
            // postalStreet
            List<String> postalStreet = postalAddress.getPostalStreet();
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(postalStreet.get(j));
            }
            // postalCity
            adr.append(
                postalAddress.getPostalCity() == null
                    ? ";"
                    : ";" + postalAddress.getPostalCity()
            );
            // postalCode
            adr.append(";");
            adr.append(
                postalAddress.getPostalCode() == null
                    ? ";"
                    : ";" + postalAddress.getPostalCode()
            );
            // postalCountry
            Map postalCountries = this.backend.getCodes().getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true);
            adr.append(";");
            adr.append((String)postalCountries.get(this.numberAsShort(postalAddress.getPostalCountry())));   
            postalHome = adr.toString();
        }
        String urlWork = addresses[Accounts.WEB_BUSINESS] == null
            ? ""
            : ((WebAddress)addresses[Accounts.WEB_BUSINESS]).getWebUrl();
        String urlHome = addresses[Accounts.WEB_HOME] == null
            ? ""
            : ((WebAddress)addresses[Accounts.WEB_HOME]).getWebUrl();
        String emailWork = addresses[Accounts.MAIL_BUSINESS] == null
            ? ""
            : ((EmailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress();
        String emailHome = addresses[Accounts.MAIL_HOME] == null
            ? ""
            : ((EmailAddress)addresses[Accounts.MAIL_HOME]).getEmailAddress();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.println("BEGIN:VCARD");
        pw.println("VERSION:2.1");
        pw.println("N:" + lastName + ";" + firstName + ";"+ middleName + ";" + salutation + ";" + suffix);
        pw.println("FN:" + firstName + (middleName.length() == 0 ? "" : " " + middleName) + (lastName.length() == 0 ? "" : " " + lastName) + (suffix.length() == 0 ? "" : " " + suffix));
        pw.println("ORG:" + organization);
        pw.println("TITLE:" + jobTitle);
        pw.println("TEL;WORK;VOICE:" + telWorkVoice);
        pw.println("TEL;HOME;VOICE:" + telHomeVoice);
        pw.println("TEL;CELL;VOICE:" + telCellVoice);
        pw.println("TEL;FAX:" + telWorkFax);
        pw.println("TEL;HOME;FAX:" + telHomeFax);
        pw.println("ADR;WORK;ENCODING=QUOTED-PRINTABLE:;;" + postalWork);
        pw.println("ADR;HOME;ENCODING=QUOTED-PRINTABLE:;;" + postalHome);
        pw.println("URL;HOME:" + urlHome);
        pw.println("URL;WORK:" + urlWork);
        pw.println("EMAIL;INTERNET:" + emailHome);
        pw.println("EMAIL;PREF;INTERNET:" + emailWork);
        pw.println("REV:" + DateFormat.getInstance().format(new Date()));
        pw.println("END:VCARD");
        try {
            pw.flush();
            os.close();
        } catch(Exception e) {}
        return os.toByteArray();
    }

    //-------------------------------------------------------------------------
    public boolean updatePostalAddress(
        DataproviderObject address,
        String newValue,
        short locale
    ) throws ServiceException {
        if((newValue != null) && (newValue.length() > 0)) {
            String[] tokens = new String[]{"", "", "", "", "", "", ""};
            StringTokenizer tokenizer = new StringTokenizer(newValue, ";", true);
            int ii = 0;
            boolean hasTokens = false;
            while(tokenizer.hasMoreTokens() && (ii < tokens.length)) {
                String t = tokenizer.nextToken();
                if(";".equals(t)) {
                    ii++;
                }
                else {
                    tokens[ii] = t;
                    hasTokens = true;
                }
            }
            if(!hasTokens) {
                return false;
            }
            // parse street (=0D=0A separated tokens)
            List street = new ArrayList();
            String temp = tokens[2];
            int pos = 0;
            while((pos = temp.indexOf("=0D=0A")) >= 0) {
                street.add(temp.substring(0, pos));
                temp = temp.substring(pos + 6);
            }
            street.add(temp);
            address.clearValues("postalAddressLine");
            for(int i = 0; i < street.size()-1; i++) {
                address.values("postalAddressLine").add(street.get(i));
            }
            address.clearValues("postalStreet").add(street.get(street.size()-1));
            address.clearValues("postalCity").add(tokens[3]);
            address.clearValues("postalState").add(tokens[4]);
            address.clearValues("postalCode").add(tokens[5]);
            address.clearValues("postalCountry").add(new Short((short)0));

            // lookup country
            AppLog.trace("lookup country", tokens[6]);
            SortedMap countries = this.backend.getCodes().getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true);
            if(countries != null) {
                for(Iterator i = countries.entrySet().iterator(); i.hasNext(); ) {
                    Entry entry = (Entry)i.next();
                    if(((String)entry.getValue()).indexOf(tokens[6]) >= 0) {
                        address.clearValues("postalCountry").add(
                            entry.getKey()
                        );
                        break;
                    }
                }
            }
            AppLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updatePhoneNumber(
        DataproviderObject address,
        String newValue
    ) throws ServiceException {
        if((newValue != null) && (newValue.length() > 0)) {
            address.clearValues("phoneNumberFull").add(newValue);
            address.clearValues("automaticParsing").add(Boolean.TRUE);
            this.backend.getAddresses().parsePhoneNumber(address, null);
            AppLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updateWebAddress(
        DataproviderObject address,
        String newValue
    ) {
        if((newValue != null) && (newValue.length() > 0)) {
            address.clearValues("webUrl").add(newValue);
            AppLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updateEMailAddress(
        DataproviderObject address,
        String newValue
    ) {
        if((newValue != null) && (newValue.length() > 0)) {
            address.clearValues("emailAddress").add(newValue);
            AppLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        Path segmentPath,
        short locale,
        List report
    ) throws ServiceException {
        try {
            // Parse vcard
            InputStream is = new ByteArrayInputStream(item);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Map data = new HashMap();
            String line = null;
            boolean lineCont = false;
            String currentName = null;
            while((line = reader.readLine()) != null) {
                int pos;
                if(lineCont) {
                    data.put(
                        currentName,
                        data.get(currentName) + line
                    );
                    lineCont = false;
                }
                else if((pos = line.indexOf(":")) >= 0) {
                    currentName = line.substring(0, pos).toUpperCase();
                    lineCont = (currentName.indexOf("ENCODING=QUOTED-PRINTABLE") >= 0) && line.endsWith("=");
                    data.put(
                        currentName,
                        line.substring(pos + 1, lineCont ? line.length() - 1 : line.length())
                    );
                }
            }
            AppLog.trace("parsed vcard", data);
            return this.importItem(
                data,
                segmentPath,
                locale,
                report,
                false
            );
        }
        catch(IOException e) {
            AppLog.warning("can not read item", e.getMessage());
        }
        return null;
    }

    //-------------------------------------------------------------------------
    /**
     * Imports the vcard. If a contact with email address EMAIL;PREF;INTERNET
     * is found then the contact and addresses are updated with the new vcard
     * info. If no contact is found then the contact and all its addresses are
     * created. If createOnly=true then no existing data is updated. However, 
     * if the contact does not exist it is created.
     */
    public BasicObject importItem(
        Map vcard,
        Path segmentPath,
        short locale,
        List report,
        boolean updateOnly
    ) throws ServiceException {
        String lookupEmail = (String)vcard.get("EMAIL;PREF;INTERNET");
        DataproviderObject contact = null;
        if((lookupEmail != null) && (lookupEmail.length() > 0)) {
            AppLog.trace("looking up", lookupEmail);
            List addresses = this.backend.getDelegatingRequests().addFindRequest(
                segmentPath.getChild("extent"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "identity",
                        FilterOperators.IS_LIKE,
                        new String[]{
                            segmentPath.getDescendant(new String[]{"account", ":*", "address", ":*"}).toXri()
                        } 
                    ),
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN,
                        new String[]{
                            "org:opencrx:kernel:account1:EMailAddress"
                        }
                    ),
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "emailAddress",
                        FilterOperators.IS_IN,
                        new String[]{
                            lookupEmail
                        }
                    )
                }
            );
            if(addresses.iterator().hasNext()) {
                AppLog.trace("address found");
                DataproviderObject_1_0 address = (DataproviderObject_1_0)addresses.iterator().next();
                contact = this.backend.retrieveObjectForModification(
                    address.path().getParent().getParent()
                );
            }
        }
        AppLog.trace("account", contact);

        boolean isNew = contact == null;
        if(isNew && updateOnly) {
            return null;
        }
        if(isNew) {
            contact = new DataproviderObject(
                segmentPath.getDescendant(new String[]{"account", UUIDs.getGenerator().next().toString()})
            );
            contact.values(SystemAttributes.OBJECT_CLASS).add(
                "org:opencrx:kernel:account1:Contact"
            );
        }
        if("org:opencrx:kernel:account1:Contact".equals(contact.values(SystemAttributes.OBJECT_CLASS).get(0))) {

            /**
             * Contact
             */
            // name
            String name = (String)vcard.get("N");
            if((name != null) && (name.indexOf(";") >= 0)) {
                String[] tokens = new String[]{"", "", "", "", ""};
                StringTokenizer tokenizer = new StringTokenizer(name, ";", true);
                int ii = 0;
                while(tokenizer.hasMoreTokens() && (ii < tokens.length)) {
                    String t = tokenizer.nextToken();
                    if(";".equals(t)) {
                        ii++;
                    }
                    else {
                        tokens[ii] = t;
                    }
                }
                // lastName
                if(tokens[0].length() > 0) {
                    contact.clearValues("lastName").add(tokens[0]);        
                }
                else if(contact.values("lastName").size() == 0) {
                    contact.values("lastName").add("N/A");
                }
                // firstName
                if(tokens[1].length() > 0) {
                    contact.clearValues("firstName").add(tokens[1]);
                }
                // middleName
                if(tokens[2].length() > 0) {
                    contact.clearValues("middleName").add(tokens[2]);
                }
                // saluation
                if(tokens[3].length() > 0) {
                    contact.clearValues("salutation").add(tokens[3]);
                }
                // suffix
                if(tokens[4].length() > 0) {
                    contact.clearValues("suffix").add(tokens[4]);
                }
            }
            // jobTitle
            String jobTitle = (String)vcard.get("TITLE");
            if((jobTitle != null) && (jobTitle.length() > 0)) {
                contact.clearValues("jobTitle").add(jobTitle);
            }
            // organization
            String organization = (String)vcard.get("ORG");
            if((organization != null) && (organization.length() > 0)) {
                contact.clearValues("organization").add(organization);
            }
            this.backend.getAccounts().setAccountFullName(contact, null);
            if(isNew) {
                AppLog.trace("adding", contact);
                this.backend.getDelegatingRequests().addCreateRequest(
                    contact
                );
                report.add("Create contact");
            }
            else {
                AppLog.trace("updating", contact);
                report.add("Update contact");
            }
        }
        // note
        String s = vcard.get("NOTE") != null ? (String)vcard.get("NOTE") : (String)vcard.get("NOTE;ENCODING=QUOTED-PRINTABLE");
        if(s != null) {
            DataproviderObject note = null;
            isNew = false;
            try {
                note = this.backend.retrieveObjectForModification(
                    contact.path().getDescendant(new String[]{"note", "VCARD"})
                );
            } 
            catch(Exception e) {}
            if(note == null) {
                note = new DataproviderObject(contact.path().getDescendant(new String[]{"note", "VCARD"}));
                note.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:generic:Note");
                isNew = true;
            }
            note.clearValues("title").add("vCard note");
            String text = "";
            int pos = 0;
            while((pos = s.indexOf("=0D=0A")) >= 0) {
                text += s.substring(0, pos) + "\n";
                s = s.substring(pos + 6);
            }
            note.clearValues("text").add(text);
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(
                    note
                );
                report.add("Create note");
            }
            else {
                report.add("Update note");
            }
        }

        // addresses
        List addresses = this.backend.getDelegatingRequests().addFindRequest(
            contact.path().getChild("address"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );

        DataproviderObject adrHome = null;
        DataproviderObject adrWork = null;
        DataproviderObject telHomeVoice = null;
        DataproviderObject telWorkVoice = null;
        DataproviderObject telHomeFax = null;
        DataproviderObject telFax = null;
        DataproviderObject urlHome = null;
        DataproviderObject urlWork = null;
        DataproviderObject telCellVoice = null;
        DataproviderObject emailInternet = null;
        DataproviderObject emailPrefInternet = null;

        // get addresses
        for(Iterator i = addresses.iterator(); i.hasNext(); ) {
            DataproviderObject address = new DataproviderObject((DataproviderObject_1_0)i.next());
            String addressClass = (String)address.values(SystemAttributes.OBJECT_CLASS).get(0);
            List usage = new ArrayList();
            for(Iterator j = address.values("usage").iterator(); j.hasNext(); ) {
                usage.add(
                    this.numberAsShort(j.next())
                );
            }
            if("org:opencrx:kernel:account1:PostalAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    adrHome = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    adrWork = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }          
            }
            else if("org:opencrx:kernel:account1:EMailAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    emailPrefInternet = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
                else if(usage.contains(Addresses.USAGE_HOME)) {
                    emailInternet = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
            }
            else if("org:opencrx:kernel:account1:PhoneNumber".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    telHomeVoice = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
                // work voice
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    telWorkVoice = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }              
                // home fax
                else if(usage.contains(Addresses.USAGE_HOME_FAX)) {
                    telHomeFax = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
                // work fax
                else if(usage.contains(Addresses.USAGE_BUSINESS_FAX)) {
                    telFax = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }              
                // cell voice
                else if(usage.contains(Addresses.USAGE_MOBILE)) {
                    telCellVoice = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }          
            }
            else if("org:opencrx:kernel:account1:WebAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    urlHome = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
                // work url
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    urlWork = this.backend.retrieveObjectForModification(
                        address.path()
                    );
                }
            }
        }
        // update adrHome
        isNew = false;
        if(adrHome == null) {
            adrHome = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            adrHome.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PostalAddress");
            adrHome.values("usage").add(Addresses.USAGE_HOME);
            isNew = true;
        }
        s = vcard.get("ADR;HOME") != null ? (String)vcard.get("ADR;HOME") : (String)vcard.get("ADR;HOME;ENCODING=QUOTED-PRINTABLE");
        if(this.updatePostalAddress(adrHome, s, locale)) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(adrHome);
                report.add("Create postal address");
            }
            else {
                report.add("Update postal address");
            }
        }
        // update adrWork
        isNew = false;
        if(adrWork == null) {
            adrWork = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            adrWork.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PostalAddress");
            adrWork.values("usage").add(Addresses.USAGE_BUSINESS);
            isNew = true;
        }
        s = vcard.get("ADR;WORK") != null ? (String)vcard.get("ADR;WORK") : (String)vcard.get("ADR;WORK;ENCODING=QUOTED-PRINTABLE");
        if(this.updatePostalAddress(adrWork, s, locale)) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(adrWork);
                report.add("Create postal address");
            }
            else {
                report.add("Update postal address");
            }
        }
        // update telHomeVoice
        isNew = false;
        if(telHomeVoice == null) {
            telHomeVoice = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            telHomeVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
            telHomeVoice.values("usage").add(Addresses.USAGE_HOME);
            isNew = true;
        }
        if(this.updatePhoneNumber(telHomeVoice, (String)vcard.get("TEL;HOME;VOICE"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(telHomeVoice);
                report.add("Create phone number");
            }
            else {
                report.add("Update phone number");
            }
        }
        // update telWorkVoice
        isNew = false;
        if(telWorkVoice == null) {
            telWorkVoice = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            telWorkVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
            telWorkVoice.values("usage").add(Addresses.USAGE_BUSINESS);
            isNew = true;
        }
        if(this.updatePhoneNumber(telWorkVoice, (String)vcard.get("TEL;WORK;VOICE"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(telWorkVoice);
                report.add("Create phone number");
            }
            else {
                report.add("Update phone number");
            }
        }
        // update telHomeFax
        isNew = false;
        if(telHomeFax == null) {
            telHomeFax = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            telHomeFax.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
            telHomeFax.values("usage").add(Addresses.USAGE_HOME_FAX);
            isNew = true;
        }
        if(this.updatePhoneNumber(telHomeFax, (String)vcard.get("TEL;HOME;FAX"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(telHomeFax);
                report.add("Create phone number");
            }
            else {
                report.add("Update phone number");
            }
        }
        // update telFax
        isNew = false;
        if(telFax == null) {
            telFax = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            telFax.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
            telFax.values("usage").add(Addresses.USAGE_BUSINESS_FAX);
            isNew = true;
        }
        if(this.updatePhoneNumber(telFax, (String)vcard.get("TEL;FAX"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(telFax);
                report.add("Create phone number");
            }
            else {
                report.add("Update phone number");
            }
        }
        // update telCellVoice
        isNew = false;
        if(telCellVoice == null) {
            telCellVoice = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            telCellVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
            telCellVoice.values("usage").add(Addresses.USAGE_MOBILE);
            isNew = true;
        }
        if(this.updatePhoneNumber(telCellVoice, (String)vcard.get("TEL;CELL;VOICE"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(telCellVoice);
                report.add("Create phone number");
            }
            else {
                report.add("Update phone number");
            }
        }
        // update urlHome
        isNew = false;
        if(urlHome == null) {
            urlHome = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            urlHome.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:WebAddress");
            urlHome.values("usage").add(Addresses.USAGE_HOME);
            isNew = true;
        }
        if(this.updateWebAddress(urlHome, (String)vcard.get("URL;HOME"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(urlHome);
                report.add("Create web address");
            }
            else {
                report.add("Update web address");
            }
        }
        // update urlWork
        isNew = false;
        if(urlWork == null) {
            urlWork = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            urlWork.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:WebAddress");
            urlWork.values("usage").add(Addresses.USAGE_BUSINESS);
            isNew = true;
        }
        if(this.updateWebAddress(urlWork, (String)vcard.get("URL;WORK"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(urlWork);
                report.add("Create web address");
            }
            else {
                report.add("Update web address");
            }
        }
        // update emailPrefInternet
        isNew = false;
        if(emailPrefInternet == null) {
            emailPrefInternet = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            emailPrefInternet.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:EMailAddress");
            emailPrefInternet.values("usage").add(Addresses.USAGE_BUSINESS);
            isNew = true;
        }
        if(this.updateEMailAddress(emailPrefInternet, (String)vcard.get("EMAIL;PREF;INTERNET"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(emailPrefInternet);
                report.add("Create email address");
            }
            else {
                report.add("Update email address");
            }
        }
        // update emailInternet
        isNew = false;
        if(emailInternet == null) {
            emailInternet = new DataproviderObject(contact.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
            emailInternet.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:EMailAddress");
            emailInternet.values("usage").add(Addresses.USAGE_HOME);
            isNew = true;
        }
        if(this.updateEMailAddress(emailInternet, (String)vcard.get("EMAIL;INTERNET"))) {
            if(isNew) {
                this.backend.getDelegatingRequests().addCreateRequest(emailInternet);
                report.add("Create email address");
            }
            else {
                report.add("Update email address");
            }
        }
        return contact == null
            ? null
            : (BasicObject)this.backend.getDelegatingPkg().refObject(contact.path().toXri());
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String MIME_TYPE = "text/x-vcard";
    public static final int MIME_TYPE_CODE = 3;

    protected final Backend backend;

}

//--- End of File -----------------------------------------------------------

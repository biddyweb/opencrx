/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: VCard.java,v 1.32 2009/03/08 17:04:49 wfro Exp $
 * Description: VCard
 * Revision:    $Revision: 1.32 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/08 17:04:49 $
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Map.Entry;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.Directions;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.collection.SparseList;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;
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
    protected String getUtcDateTime(
        String dateTime,
        SimpleDateFormat dateTimeFormatter
    ) throws ParseException {
        Date date = null;
        if(dateTime.endsWith("Z")) {
            if(dateTime.length() == 16) {
                date = DateFormat.getInstance().parse(dateTime.substring(0, 15) + ".000Z");
            }
            else {
                date = DateFormat.getInstance().parse(dateTime);
            }
        }
        else if(dateTime.length() == 8) {
            date = DateFormat.getInstance().parse(dateTime + "T000000.000Z");
        }
        else {
            date = dateTimeFormatter.parse(dateTime);
        }
        return DateFormat.getInstance().format(date);
    }
    
    //-------------------------------------------------------------------------
    protected String encodeString(
        String s
    ) {
        if(s == null) return s;
        s = s.replace(";", "\\;");
        return s;
    }
    
    //-------------------------------------------------------------------------
    private String mapToSalutationText(
        short salutationCode
    ) throws ServiceException {
        return (String)this.backend.getCodes().getLongText(
            "org:opencrx:kernel:account1:Contact:salutationCode", 
            (short)0, 
            true
        ).get(Short.valueOf(salutationCode));        
    }
    
    //-------------------------------------------------------------------------
    private short mapToSalutationCode(
        String salutation
    ) throws ServiceException {
        for(int locale = 0; locale < 32; locale++) {
            Number salutationCode = (Number)this.backend.getCodes().getLongText(
                "org:opencrx:kernel:account1:Contact:salutationCode", 
                (short)locale, 
                false
            ).get(salutation);
            if(salutationCode != null) {
                return salutationCode.shortValue();
            }
        }
        return (short)0;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Update sourceVcard with account values and return merged vcard. 
     */
    public String mergeVcard(
        DataproviderObject_1_0 account,
        String sourceVcard,
        List<String> statusMessage
    ) throws ServiceException {
        boolean isContact = this.backend.isContact(account);
        // N
        String n = null;        
        if(isContact) {
            if(!account.values("lastName").isEmpty()) {
                String lastName = (String)account.values("lastName").get(0);
                String firstName = account.values("firstName").isEmpty() ? "" : (String)account.values("firstName").get(0);
                String middleName = account.values("middleName").isEmpty() ? "" : (String)account.values("middleName").get(0);
                Short salutationCode = account.values("salutationCode").isEmpty() ? null : ((Number)account.values("salutationCode").get(0)).shortValue();
                String salutation = account.values("salutation").isEmpty() ?
                    (salutationCode == null) || (salutationCode.shortValue() == 0) ?
                        null :
                        this.mapToSalutationText(salutationCode) :
                    (String)account.values("salutation").get(0);
                String suffix = account.values("suffix").isEmpty() ? "" : (String)account.values("suffix").get(0);
                n = lastName + ";" + firstName + ";"+ middleName + ";" + (salutation == null ? "" : salutation) + ";" + suffix;
            }
        }
        else {
            if(!account.values("name").isEmpty()) {
                String name = (String)account.values("name").get(0);
                n = name;
            }
        }
        // FN
        String fn = null;
        if(isContact) {
            String firstName = account.values("firstName").isEmpty() ? "" : (String)account.values("firstName").get(0);
            String lastName = account.values("lastName").isEmpty() ? "" : (String)account.values("lastName").get(0);
            String middleName = account.values("middleName").isEmpty() ? "" : (String)account.values("middleName").get(0);
            String suffix = account.values("suffix").isEmpty() ? "" : (String)account.values("suffix").get(0);
            fn = firstName + (middleName.length() == 0 ? "" : " " + middleName) + (lastName.length() == 0 ? "" : " " + lastName) + (suffix.length() == 0 ? "" : " " + suffix);
        }
        else {
            String fullName = account.values("fullName").isEmpty() ? "" : (String)account.values("fullName").get(0);
            fn = fullName;            
        }
        // NICKNAME
        String nickName = null;
        if(isContact) {
            nickName = account.values("nickName").isEmpty() ? "" : (String)account.values("nickName").get(0);            
        }
        // REV
        String rev = DateFormat.getInstance().format(new Date());        
        // ORG
        String org = null;
        if(isContact) {
            if(!account.values("organization").isEmpty()) {
                org = (String)account.values("organization").get(0);
            }
        }
        // TITLE
        String title = null;
        if(isContact) {
            if(!account.values("jobTitle").isEmpty()) {
                title = (String)account.values("jobTitle").get(0);
            }
        }
        // BDAY
        String bday = null;
        if(isContact) {
            if(!account.values("birthdate").isEmpty()) {
                bday = (String)account.values("birthdate").get(0);
            }
        }
        AccountAddress[] addresses = new AccountAddress[11];
        try {
            addresses = Accounts.getMainAddresses(
                (Account)this.backend.getDelegatingPkg().refObject(account.path().toXri())
            );
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            if(e0.getExceptionCode() != BasicException.Code.NOT_FOUND) {
                throw e0;
            }
        }
        // TEL;WORK;VOICE
        String telWorkVoice = addresses[Accounts.PHONE_BUSINESS] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.PHONE_BUSINESS].refGetPath()).values("phoneNumberFull").get(0);
        // TEL;HOME;VOICE
        String telHomeVoice = addresses[Accounts.PHONE_HOME] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.PHONE_HOME].refGetPath()).values("phoneNumberFull").get(0);
        // TEL;CELL;VOICE
        String telCellVoice = addresses[Accounts.MOBILE] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.MOBILE].refGetPath()).values("phoneNumberFull").get(0);
        // TEL;FAX
        String telWorkFax = addresses[Accounts.FAX_BUSINESS] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.FAX_BUSINESS].refGetPath()).values("phoneNumberFull").get(0);
        // TEL;HOME;FAX
        String telHomeFax = addresses[Accounts.FAX_HOME] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.FAX_HOME].refGetPath()).values("phoneNumberFull").get(0);
        // ADR;WORK
        String adrWork = "";
        if(addresses[Accounts.POSTAL_BUSINESS] != null) {
            DataproviderObject_1_0 postalAddress = this.backend.retrieveObject(addresses[Accounts.POSTAL_BUSINESS].refGetPath());
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<Object> addressLines = postalAddress.values("postalAddressLine");
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString((String)addressLines.get(j)));
            }
            // postalStreet
            List<Object> postalStreet = postalAddress.values("postalStreet");
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString((String)postalStreet.get(j)));
            }
            // postalCity
            adr.append(
                (postalAddress.getValues("postalCity") == null) || postalAddress.getValues("postalCity").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalCity").get(0))
            );
            // postalState
            adr.append(
                (postalAddress.getValues("postalState") == null) || postalAddress.getValues("postalState").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalState").get(0))
            );            
            // postalCode
            adr.append(
                (postalAddress.getValues("postalCode") == null) || postalAddress.getValues("postalCode").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalCode").get(0))
            );
            // postalCountry
            Map<Short,String> postalCountries = this.backend.getCodes().getLongText(
                "org:opencrx:kernel:address1:PostalAddressable:postalCountry", 
                DEFAULT_LOCALE, 
                true
            );
            adr.append(";");
            adr.append(postalCountries.get(
                this.numberAsShort(postalAddress.values("postalCountry").get(0)))
            );   
            adrWork = adr.toString();
        }
        // ADR;HOME
        String adrHome = "";
        if(addresses[Accounts.POSTAL_HOME] != null) {
            DataproviderObject_1_0 postalAddress = this.backend.retrieveObject(addresses[Accounts.POSTAL_HOME].refGetPath());
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<Object> addressLines = postalAddress.values("postalAddressLine");
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString((String)addressLines.get(j)));
            }
            // postalStreet
            List<Object> postalStreet = postalAddress.values("postalStreet");
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString((String)postalStreet.get(j)));
            }
            // postalCity
            adr.append(
                (postalAddress.getValues("postalCity") == null) || postalAddress.getValues("postalCity").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalCity").get(0))
            );
            // postalState
            adr.append(
                (postalAddress.getValues("postalState") == null) || postalAddress.getValues("postalState").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalState").get(0))
            );
            // postalCode
            adr.append(
                (postalAddress.getValues("postalCode") == null) || postalAddress.getValues("postalCode").isEmpty() ? 
                    ";" : 
                    ";" + this.encodeString((String)postalAddress.getValues("postalCode").get(0))
            );
            // postalCountry
            Map<Short,String> postalCountries = this.backend.getCodes().getLongText(
                "org:opencrx:kernel:address1:PostalAddressable:postalCountry", 
                DEFAULT_LOCALE, 
                true
            );
            adr.append(";");
            adr.append(postalCountries.get(
                this.numberAsShort(postalAddress.values("postalCountry").get(0)))
            );   
            adrHome = adr.toString();
        }
        // URL;WORK
        String urlWork = addresses[Accounts.WEB_BUSINESS] == null
            ? ""
            : (String)this.backend.retrieveObject(addresses[Accounts.WEB_BUSINESS].refGetPath()).values("webUrl").get(0);
        // URL;HOME
        String urlHome = addresses[Accounts.WEB_HOME] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.WEB_HOME].refGetPath()).values("webUrl").get(0);
        // EMAIL;PREF;INTERNET
        String emailWork = addresses[Accounts.MAIL_BUSINESS] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.MAIL_BUSINESS].refGetPath()).values("emailAddress").get(0);
        // EMAIL;INTERNET
        String emailHome = addresses[Accounts.MAIL_HOME] == null ? 
            "" : 
            (String)this.backend.retrieveObject(addresses[Accounts.MAIL_HOME].refGetPath()).values("emailAddress").get(0);        
        // return if data is missing
        if(!statusMessage.isEmpty()) {
            return null;
        }        
        if((sourceVcard == null) || (sourceVcard.length() == 0)) {
            // Template
            UUID uid = null;
            try {
                uid = UUIDConversion.fromString(account.path().getBase());
            }
            catch(Exception e) {
                uid = UUIDs.getGenerator().next();
            }
            sourceVcard = 
                "BEGIN:VCARD\n" +
                "VERSION:2.1\n" +
                "UID:" + uid.toString() + "\n" +
                "REV:" + rev.substring(0, 15) + "Z\n" +
                "N:\n" +
                "FN:\n" + 
                "NICKNAME:\n" +
                "ORG:\n" +
                "TITLE:\n" +
                "TEL;WORK;VOICE:\n" +
                "TEL;HOME;VOICE:\n" +
                "TEL;CELL;VOICE:\n" +
                (isContact ? "BDAY:\n" : "") +
                "TEL;FAX:\n" +
                "TEL;HOME;FAX:\n" +
                "ADR;WORK;ENCODING=QUOTED-PRINTABLE:;;\n" +
                "ADR;HOME;ENCODING=QUOTED-PRINTABLE:;;\n" +
                "URL;HOME:\n" +
                "URL;WORK:\n" +
                "EMAIL;PREF;INTERNET:\n" +
                "EMAIL;INTERNET:\n" +
                "END:VCARD";
        }
        try {
            ByteArrayOutputStream targetVcardBos = new ByteArrayOutputStream();
            PrintWriter targetVcard = new PrintWriter(new OutputStreamWriter(targetVcardBos, "UTF-8"));
            String line = null;
            BufferedReader readerSourceVcard = new BufferedReader(new StringReader(sourceVcard));
            boolean isVcard = false;
            String tagStart = null;
            while((line = readerSourceVcard.readLine()) != null) {
                if(!line.startsWith(" ")) {
                    tagStart = line;
                }
                if(
                    line.toUpperCase().startsWith("BEGIN:VCARD")
                ) {
                    targetVcard.println("BEGIN:VCARD");                    
                    isVcard = true;
                }
                // Dump updated event fields only for first event
                else if(
                    line.toUpperCase().startsWith("END:VCARD")
                ) {                    
                    // REV
                    if(rev != null) {
                        targetVcard.println("REV:" + rev.substring(0, 15) + "Z");
                    }
                    // N
                    if((n != null) && (n.length() > 0)) {
                        targetVcard.println("N:" + n);
                    }
                    // FN
                    if((fn != null) && (fn.length() > 0)) {
                        targetVcard.println("FN:" + fn);
                    }
                    // NICKNAME
                    if((nickName != null) && (nickName.length() > 0)) {
                        targetVcard.println("NICKNAME:" + nickName);
                    }
                    // ORG
                    if((org != null) && (org.length() > 0)) {
                        targetVcard.println("ORG:" + org);
                    }
                    // TITLE
                    if((title != null) && (title.length() > 0)) {
                        targetVcard.println("TITLE:" + title);
                    }
                    // BDAY
                    if((bday != null) && (bday.length() > 0)) {
                        targetVcard.println("BDAY:" + bday.substring(0, 15) + "Z");
                    }
                    // TEL;WORK;VOICE
                    if((telWorkVoice != null) && (telWorkVoice.length() > 0)) {
                        targetVcard.println("TEL;WORK;VOICE:" + telWorkVoice);
                    }
                    // TEL;HOME;VOICE
                    if((telHomeVoice != null) && (telHomeVoice.length() > 0)) {
                        targetVcard.println("TEL;HOME;VOICE:" + telHomeVoice);
                    }
                    // TEL;CELL;VOICE
                    if((telCellVoice != null) && (telCellVoice.length() > 0)) {
                        targetVcard.println("TEL;CELL;VOICE:" + telCellVoice);
                    }
                    // TEL;FAX
                    if((telWorkFax != null) && (telWorkFax.length() > 0)) {
                        targetVcard.println("TEL;FAX:" + telWorkFax);
                    }
                    // TEL;HOME;FAX
                    if((telHomeFax != null) && (telHomeFax.length() > 0)) {
                        targetVcard.println("TEL;HOME;FAX:" + telHomeFax);
                    }
                    // ADR;WORK
                    if((adrWork != null) && (adrWork.length() > 0)) {
                        targetVcard.println("ADR;WORK;ENCODING=QUOTED-PRINTABLE:;;" + adrWork);
                    }
                    // ADR;HOME
                    if((adrHome != null) && (adrHome.length() > 0)) {
                        targetVcard.println("ADR;HOME;ENCODING=QUOTED-PRINTABLE:;;" + adrHome);
                    }
                    // URL;HOME
                    if((urlHome != null) && (urlHome.length() > 0)) {
                        targetVcard.println("URL;HOME:" + urlHome);
                    }
                    // URL;WORK
                    if((urlWork != null) && (urlWork.length() > 0)) {
                        targetVcard.println("URL;WORK:" + urlWork);
                    }
                    // EMAIL;PREF;INTERNET
                    if((emailWork != null) && (emailWork.length() > 0)) {
                        targetVcard.println("EMAIL;PREF;INTERNET:" + emailWork);
                    }
                    // EMAIL;INTERNET
                    if((emailHome != null) && (emailHome.length() > 0)) {
                        targetVcard.println("EMAIL;INTERNET:" + emailHome);
                    }
                    targetVcard.println("END:VCARD");                                            
                    isVcard = false;
                }
                else if(isVcard ) {
                    boolean isUpdatableTag = 
                        tagStart.toUpperCase().startsWith("REV");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("N");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("FN");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("NICKNAME");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("ORG");
                    isUpdatableTag |= 
                        tagStart.toUpperCase().startsWith("TITLE");
                    isUpdatableTag |= 
                        tagStart.toUpperCase().startsWith("BDAY");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("TEL;WORK;VOICE");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("TEL;HOME;VOICE");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("TEL;CELL;VOICE");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("TEL;FAX");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("TEL;HOME;FAX");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("ADR;WORK");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("ADR;HOME");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("URL;HOME");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("URL;WORK");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("EMAIL;INTERNET");
                    isUpdatableTag |=
                        tagStart.toUpperCase().startsWith("EMAIL;PREF;INTERNET");
                    if(!isUpdatableTag) {
                        targetVcard.println(line);
                    }
                }
                else {
                    targetVcard.println(line);                    
                }
            }
            targetVcard.flush();
            targetVcardBos.close();
            try {
                return targetVcardBos.toString("UTF-8");
            }
            catch(Exception e) {
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }
    
    //-------------------------------------------------------------------------
    public boolean updatePostalAddress(
        DataproviderObject address,
        String newValue,
        short locale
    ) throws ServiceException {
        if((newValue != null) && (newValue.length() > 0)) {
            String[] tokens = new String[]{"", "", "", "", "", "", ""};
            // Unescape semicolons
            // Replace semicolons by tabs
            newValue = newValue.replace("\\;", "\u0001");
            newValue = newValue.replace(";", "\t");
            newValue = newValue.replace("\u0001", ";");
            StringTokenizer tokenizer = new StringTokenizer(newValue, "\t", true);
            int ii = 0;
            boolean hasTokens = false;
            while(tokenizer.hasMoreTokens() && (ii < tokens.length)) {
                String t = tokenizer.nextToken();
                if("\t".equals(t)) {
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
            this.backend.getAddresses().updatePhoneNumber(address, null);
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
    public static Map<String,String> parseVCard(
        BufferedReader reader
    ) throws IOException {
        Map<String,String> vcard = new HashMap<String,String>();
        String line = null;
        boolean lineCont = false;
        String currentName = null;
        while((line = reader.readLine()) != null) {
            int pos;
            if(lineCont) {
                vcard.put(
                    currentName,
                    vcard.get(currentName) + line
                );
                lineCont = false;
            }
            else if((pos = line.indexOf(":")) >= 0) {
                currentName = line.substring(0, pos).toUpperCase();
                lineCont = (currentName.indexOf("ENCODING=QUOTED-PRINTABLE") >= 0) && line.endsWith("=");
                vcard.put(
                    currentName,
                    line.substring(pos + 1, lineCont ? line.length() - 1 : line.length())
                );
            }
        }    
        return vcard;
    }
    
    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        Path accountIdentity,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
        try {
            InputStream is = new ByteArrayInputStream(item);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, "UTF-8")
            );
            Map<String,String> vcard = VCard.parseVCard(reader);
            AppLog.trace("parsed vcard", vcard);
            return this.importItem(
                vcard,
                this.backend.retrieveObjectForModification(accountIdentity),
                locale,
                report
            );
        }
        catch(IOException e) {
            AppLog.warning("can not read item", e.getMessage());
        }
        return null;
    }

    //-------------------------------------------------------------------------
    private BasicObject importItem(
        Map<String,String> vcard,
        DataproviderObject account,
        short locale,
        List<String> report
    ) throws ServiceException {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
        dateTimeFormatter.setLenient(false);
        boolean isContact = this.backend.isContact(account);
        // name
        String name = vcard.get("N");
        if(isContact) {
            if((name != null) && (name.indexOf(";") >= 0)) {
                String[] nameTokens = new String[]{"", "", "", "", ""};
                StringTokenizer tokenizer = new StringTokenizer(name, ";", true);
                int ii = 0;
                while(tokenizer.hasMoreTokens() && (ii < nameTokens.length)) {
                    String t = tokenizer.nextToken();
                    if(";".equals(t)) {
                        ii++;
                    }
                    else {
                        nameTokens[ii] = t;
                    }
                }
                // lastName
                if(nameTokens[0].length() > 0) {
                    account.clearValues("lastName").add(nameTokens[0]);        
                }
                else if(account.values("lastName").isEmpty()) {
                    account.values("lastName").add("N/A");
                }
                // firstName
                if(nameTokens[1].length() > 0) {
                    account.clearValues("firstName").add(nameTokens[1]);
                }
                // middleName
                if(nameTokens[2].length() > 0) {
                    account.clearValues("middleName").add(nameTokens[2]);
                }
                // salutation
                if(nameTokens[3].length() > 0) {
                    String salutation = nameTokens[3];
                    short salutationCode = this.mapToSalutationCode(salutation);
                    if(
                        (account.getValues("salutation") != null) && 
                        !account.getValues("salutation").isEmpty() &&
                        (((String)account.getValues("salutation").get(0)).length() > 0)
                    ) {
                        account.clearValues("salutation").add(salutation);
                    }
                    else if(salutationCode != 0) {
                        account.clearValues("salutationCode").add(salutationCode);
                    }
                    else {
                        account.clearValues("salutation").add(salutation);                        
                    }                    
                }
                // suffix
                if(nameTokens[4].length() > 0) {
                    account.clearValues("suffix").add(nameTokens[4]);
                }
            }
            // nickName
            String nickName = vcard.get("NICKNAME");
            if((nickName != null) && (nickName.length() > 0)) {
                account.clearValues("nickName").add(nickName);
            }
            // jobTitle
            String jobTitle = vcard.get("TITLE");
            if((jobTitle != null) && (jobTitle.length() > 0)) {
                account.clearValues("jobTitle").add(jobTitle);
            }
            // organization
            String organization = vcard.get("ORG");
            if((organization != null) && (organization.length() > 0)) {
                account.clearValues("organization").add(organization);
            }
            // bday
            String bday = vcard.get("BDAY");
            if((bday != null) && (bday.length() > 0)) {
                try {
                    account.clearValues("birthdate").add(
                        this.getUtcDateTime(
                            bday, 
                            dateTimeFormatter
                        )
                    );
                } 
                catch(Exception e) {}
            }
            this.backend.getAccounts().updateFullName(
                account, 
                null
            );
            AppLog.trace("Update", account);
            report.add("Update account");
        }
        else if(this.backend.isAbstractGroup(account)){
            if((name != null) && (name.indexOf(";") >= 0)) {
                String[] nameTokens = new String[]{"", "", "", "", ""};
                StringTokenizer tokenizer = new StringTokenizer(name, ";", true);
                int ii = 0;
                while(tokenizer.hasMoreTokens() && (ii < nameTokens.length)) {
                    String t = tokenizer.nextToken();
                    if(";".equals(t)) {
                        ii++;
                    }
                    else {
                        nameTokens[ii] = t;
                    }
                }
                // name
                if(nameTokens[0].length() > 0) {
                    account.clearValues("name").add(nameTokens[0]);        
                }
                else if(account.values("name").isEmpty()) {
                    account.values("name").add("N/A");
                }
            }
        }
        // externalLink
        boolean hasVcardUid = false;
        SparseList<Object> externalLinks = account.values("externalLink");
        String vcardUid = vcard.get("UID") == null
            ? account.path().getBase()
            : vcard.get("UID");
        for(int i = 0; i < externalLinks.size(); i++) {
            if(((String)externalLinks.get(i)).startsWith(VCARD_SCHEMA)) {
                externalLinks.set(
                    i,
                    VCARD_SCHEMA + vcardUid
                );
                hasVcardUid = true;
                break;
            }
        }
        if(!hasVcardUid) {
            externalLinks.add(
                VCARD_SCHEMA + vcardUid    
            );
        }        
        // note
        String s = vcard.get("NOTE") != null ? vcard.get("NOTE") : vcard.get("NOTE;ENCODING=QUOTED-PRINTABLE");
        if(s != null) {
            DataproviderObject note = null;
            boolean isNew = false;
            try {
                note = this.backend.retrieveObjectForModification(
                    account.path().getDescendant(new String[]{"note", "VCARD"})
                );
            } 
            catch(Exception e) {}
            if(note == null) {
                note = new DataproviderObject(account.path().getDescendant(new String[]{"note", "VCARD"}));
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
        List<DataproviderObject_1_0> addresses = this.backend.getDelegatingRequests().addFindRequest(
            account.path().getChild("address"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );

        Path adrHomeIdentity = null;
        Path adrWorkIdentity = null;
        Path telHomeVoiceIdentity = null;
        Path telWorkVoiceIdentity = null;
        Path telHomeFaxIdentity = null;
        Path telFaxIdentity = null;
        Path urlHomeIdentity = null;
        Path urlWorkIdentity = null;
        Path telCellVoiceIdentity = null;
        Path emailInternetIdentity = null;
        Path emailPrefInternetIdentity = null;

        // get addresses
        for(Iterator<DataproviderObject_1_0> i = addresses.iterator(); i.hasNext(); ) {
            DataproviderObject address = new DataproviderObject(i.next());
            String addressClass = (String)address.values(SystemAttributes.OBJECT_CLASS).get(0);
            List<Short> usage = new ArrayList<Short>();
            for(Iterator<Object> j = address.values("usage").iterator(); j.hasNext(); ) {
                usage.add(
                    this.numberAsShort(j.next())
                );
            }
            if("org:opencrx:kernel:account1:PostalAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    adrHomeIdentity = address.path();
                }
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    adrWorkIdentity = address.path();
                }          
            }
            else if("org:opencrx:kernel:account1:EMailAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    emailPrefInternetIdentity = address.path();
                }
                else if(usage.contains(Addresses.USAGE_HOME)) {
                    emailInternetIdentity = address.path();
                }
            }
            else if("org:opencrx:kernel:account1:PhoneNumber".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    telHomeVoiceIdentity = address.path();
                }
                // work voice
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    telWorkVoiceIdentity = address.path();
                }              
                // home fax
                else if(usage.contains(Addresses.USAGE_HOME_FAX)) {
                    telHomeFaxIdentity = address.path();
                }
                // work fax
                else if(usage.contains(Addresses.USAGE_BUSINESS_FAX)) {
                    telFaxIdentity = address.path();
                }              
                // cell voice
                else if(usage.contains(Addresses.USAGE_MOBILE)) {
                    telCellVoiceIdentity = address.path();
                }          
            }
            else if("org:opencrx:kernel:account1:WebAddress".equals(addressClass)) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    urlHomeIdentity = address.path();
                }
                // work url
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    urlWorkIdentity = address.path();
                }
            }
        }
        // update adrHome
        s = vcard.get("ADR;HOME") != null ? vcard.get("ADR;HOME") :
            vcard.get("ADR;TYPE=HOME") != null ? vcard.get("ADR;TYPE=HOME") :
            vcard.get("ADR;HOME;ENCODING=QUOTED-PRINTABLE");
        if((s != null) && (s.length() > 0) && !s.startsWith(";;;")) {
            if(adrHomeIdentity == null) {
                DataproviderObject adrHome = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                adrHome.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PostalAddress");
                adrHome.values("usage").add(Addresses.USAGE_HOME);
                adrHome.values("isMain").add(Boolean.TRUE);
                this.updatePostalAddress(adrHome, s, locale);
                this.backend.getDelegatingRequests().addCreateRequest(adrHome);
                report.add("Create postal address");
            }
            else {
                DataproviderObject adrHome = this.backend.retrieveObjectForModification(adrHomeIdentity);
                this.updatePostalAddress(adrHome, s, locale);
                report.add("Update postal address");
            }
        }
        // update adrWork
        s = vcard.get("ADR;WORK") != null ? vcard.get("ADR;WORK") :
            vcard.get("ADR;TYPE=WORK") != null ? vcard.get("ADR;TYPE=WORK") :
            vcard.get("ADR;WORK;ENCODING=QUOTED-PRINTABLE");
        if((s != null) && (s.length() > 0) && !s.startsWith(";;;")) {
            if(adrWorkIdentity == null) {
                DataproviderObject adrWork = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                adrWork.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PostalAddress");
                adrWork.values("usage").add(Addresses.USAGE_BUSINESS);
                adrWork.values("isMain").add(Boolean.TRUE);
                this.updatePostalAddress(adrWork, s, locale);
                this.backend.getDelegatingRequests().addCreateRequest(adrWork);
                report.add("Create postal address");
            }
            else {
                DataproviderObject adrWork = this.backend.retrieveObjectForModification(adrWorkIdentity);
                this.updatePostalAddress(adrWork, s, locale);
                report.add("Update postal address");
            }
        }
        // update telHomeVoice
        s = vcard.get("TEL;HOME;VOICE") != null ? vcard.get("TEL;HOME;VOICE") :
            vcard.get("TEL;TYPE=HOME");
        if((s != null) && (s.length() > 0)) {
            if(telHomeVoiceIdentity == null) {
                DataproviderObject telHomeVoice = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                telHomeVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
                telHomeVoice.values("usage").add(Addresses.USAGE_HOME);
                telHomeVoice.values("isMain").add(Boolean.TRUE);
                this.updatePhoneNumber(telHomeVoice, s);
                this.backend.getDelegatingRequests().addCreateRequest(telHomeVoice);
                report.add("Create phone number");
            }
            else {
                DataproviderObject telHomeVoice = this.backend.retrieveObjectForModification(telHomeVoiceIdentity);
                this.updatePhoneNumber(telHomeVoice, s);
                report.add("Update phone number");
            }
        }
        // update telWorkVoice
        s = vcard.get("TEL;WORK;VOICE") != null ? vcard.get("TEL;WORK;VOICE") :
            vcard.get("TEL;TYPE=WORK");
        if((s != null) && (s.length() > 0)) {        
            if(telWorkVoiceIdentity == null) {
                DataproviderObject telWorkVoice = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                telWorkVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
                telWorkVoice.values("usage").add(Addresses.USAGE_BUSINESS);
                telWorkVoice.values("isMain").add(Boolean.TRUE);
                this.updatePhoneNumber(telWorkVoice, s);
                this.backend.getDelegatingRequests().addCreateRequest(telWorkVoice);
                report.add("Create phone number");
            }
            else {
                DataproviderObject telWorkVoice = this.backend.retrieveObjectForModification(telWorkVoiceIdentity);
                this.updatePhoneNumber(telWorkVoice, s);
                report.add("Update phone number");
            }
        }
        // update telHomeFax
        s = vcard.get("TEL;HOME;FAX");
        if((s != null) && (s.length() > 0)) {                
            if(telHomeFaxIdentity == null) {
                DataproviderObject telHomeFax = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                telHomeFax.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
                telHomeFax.values("usage").add(Addresses.USAGE_HOME_FAX);
                telHomeFax.values("isMain").add(Boolean.TRUE);
                this.updatePhoneNumber(telHomeFax, s);
                this.backend.getDelegatingRequests().addCreateRequest(telHomeFax);
                report.add("Create phone number");
            }
            else {
                DataproviderObject telHomeFax = this.backend.retrieveObjectForModification(telHomeFaxIdentity);
                this.updatePhoneNumber(telHomeFax, s);
                report.add("Update phone number");
            }
        }
        // update telFax
        s = vcard.get("TEL;FAX") != null ? vcard.get("TEL;FAX") :
            vcard.get("TEL;TYPE=FAX");
        if((s != null) && (s.length() > 0)) {                
            if(telFaxIdentity == null) {
                DataproviderObject telFax = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                telFax.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
                telFax.values("usage").add(Addresses.USAGE_BUSINESS_FAX);
                telFax.values("isMain").add(Boolean.TRUE);
                this.updatePhoneNumber(telFax, s);
                this.backend.getDelegatingRequests().addCreateRequest(telFax);
                report.add("Create phone number");
            }
            else {
                DataproviderObject telFax = this.backend.retrieveObjectForModification(telFaxIdentity);
                this.updatePhoneNumber(telFax, s);
                report.add("Update phone number");
            }
        }
        // update telCellVoice
        s = vcard.get("TEL;CELL;VOICE") != null ? vcard.get("TEL;CELL;VOICE") :
            vcard.get("TEL;TYPE=CELL");
        if((s != null) && (s.length() > 0)) {                
            if(telCellVoiceIdentity == null) {
                DataproviderObject telCellVoice = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                telCellVoice.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:PhoneNumber");
                telCellVoice.values("usage").add(Addresses.USAGE_MOBILE);
                telCellVoice.values("isMain").add(Boolean.TRUE);
                this.updatePhoneNumber(telCellVoice, s);
                this.backend.getDelegatingRequests().addCreateRequest(telCellVoice);
                report.add("Create phone number");
            }
            else {
                DataproviderObject telCellVoice = this.backend.retrieveObjectForModification(telCellVoiceIdentity);
                this.updatePhoneNumber(telCellVoice, s);
                report.add("Update phone number");
            }
        }
        // update urlHome
        s = vcard.get("URL;HOME");
        if((s != null) && (s.length() > 0)) {                
            if(urlHomeIdentity == null) {
                DataproviderObject urlHome = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                urlHome.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:WebAddress");
                urlHome.values("usage").add(Addresses.USAGE_HOME);
                urlHome.values("isMain").add(Boolean.TRUE);
                this.updateWebAddress(urlHome, s);
                this.backend.getDelegatingRequests().addCreateRequest(urlHome);
                report.add("Create web address");
            }
            else {
                DataproviderObject urlHome = this.backend.retrieveObjectForModification(urlHomeIdentity);
                this.updateWebAddress(urlHome, s);
                report.add("Update web address");
            }
        }
        // update urlWork
        s = vcard.get("URL;WORK");
        if((s != null) && (s.length() > 0)) {                
            if(urlWorkIdentity == null) {
                DataproviderObject urlWork = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                urlWork.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:WebAddress");
                urlWork.values("usage").add(Addresses.USAGE_BUSINESS);
                urlWork.values("isMain").add(Boolean.TRUE);
                this.updateWebAddress(urlWork, s);
                this.backend.getDelegatingRequests().addCreateRequest(urlWork);
                report.add("Create web address");
            }
            else {
                DataproviderObject urlWork = this.backend.retrieveObjectForModification(urlWorkIdentity);
                this.updateWebAddress(urlWork, s);
                report.add("Update web address");
            }
        }
        // update emailPrefInternet
        s = vcard.get("EMAIL;PREF;INTERNET") != null ? vcard.get("EMAIL;PREF;INTERNET") :
            vcard.get("EMAIL;TYPE=WORK");
        if((s != null) && (s.length() > 0)) {                
            if(emailPrefInternetIdentity == null) {
                DataproviderObject emailPrefInternet = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                emailPrefInternet.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:EMailAddress");
                emailPrefInternet.values("usage").add(Addresses.USAGE_BUSINESS);
                emailPrefInternet.values("isMain").add(Boolean.TRUE);
                this.updateEMailAddress(emailPrefInternet, s);
                this.backend.getDelegatingRequests().addCreateRequest(emailPrefInternet);
                report.add("Create email address");
            }
            else {
                DataproviderObject emailPrefInternet = this.backend.retrieveObjectForModification(emailPrefInternetIdentity);
                this.updateEMailAddress(emailPrefInternet, s);
                report.add("Update email address");
            }
        }
        // update emailInternet
        s = vcard.get("EMAIL;INTERNET") != null ? vcard.get("EMAIL;INTERNET") :
            vcard.get("EMAIL;TYPE=HOME");
        if((s != null) && (s.length() > 0)) {                
            if(emailInternetIdentity == null) {
                DataproviderObject emailInternet = new DataproviderObject(account.path().getDescendant(new String[]{"address", UUIDs.getGenerator().next().toString()}));
                emailInternet.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:account1:EMailAddress");
                emailInternet.values("usage").add(Addresses.USAGE_HOME);
                emailInternet.values("isMain").add(Boolean.TRUE);
                this.updateEMailAddress(emailInternet, s);
                this.backend.getDelegatingRequests().addCreateRequest(emailInternet);
                report.add("Create email address");
            }
            else {
                DataproviderObject emailInternet = this.backend.retrieveObjectForModification(emailInternetIdentity);
                this.updateEMailAddress(emailInternet, s);
                report.add("Update email address");
            }
        }
        return account == null
            ? null
            : (BasicObject)this.backend.getDelegatingPkg().refObject(account.path().toXri());       
    }
        
    //-------------------------------------------------------------------------
    /**
      * Updates account according to values of vcard. If a account with email 
      * address EMAIL;PREF;INTERNET is found then the account and its addresses 
      * are updated. If no contact is found then null is returned.
      */
    public BasicObject updateAccount(
        Map<String,String> vcard,
        Path accountSegmentIdentity,
        short locale,
        List<String> report
    ) throws ServiceException {
        String lookupEmail = vcard.get("EMAIL;PREF;INTERNET");
        DataproviderObject contact = null;
        if((lookupEmail != null) && (lookupEmail.length() > 0)) {
            AppLog.trace("looking up", lookupEmail);
            List<DataproviderObject_1_0> addresses = this.backend.getDelegatingRequests().addFindRequest(
                accountSegmentIdentity.getChild("extent"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "identity",
                        FilterOperators.IS_LIKE,
                        accountSegmentIdentity.getDescendant(new String[]{"account", ":*", "address", ":*"}).toXri()
                    ),
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        SystemAttributes.OBJECT_CLASS,
                        FilterOperators.IS_IN,
                        "org:opencrx:kernel:account1:EMailAddress"
                    ),
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "emailAddress",
                        FilterOperators.IS_IN,
                        lookupEmail
                    )
                }
            );
            if(addresses.iterator().hasNext()) {
                AppLog.trace("address found");
                DataproviderObject_1_0 address = addresses.iterator().next();
                contact = this.backend.retrieveObjectForModification(
                    address.path().getParent().getParent()
                );
            }
        }
        AppLog.trace("account", contact);

        boolean isNew = contact == null;
        if(isNew) {
            return null;
        }
        return this.importItem(
            vcard, 
            contact, 
            locale, 
            report
        );
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String DATETIME_FORMAT =  "yyyyMMdd'T'HHmmss";
    public static final String MIME_TYPE = "text/x-vcard";
    public static final String PROD_ID = "//OPENCRX//Groupware Version 2//EN";
    
    public static final int MIME_TYPE_CODE = 3;
    public static final short DEFAULT_LOCALE = 0;
    public final static String VCARD_SCHEMA = "VCARD:";    

    protected final Backend backend;

}

//--- End of File -----------------------------------------------------------

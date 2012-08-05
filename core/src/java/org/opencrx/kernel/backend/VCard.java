/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: VCard.java,v 1.49 2009/08/20 15:52:38 wfro Exp $
 * Description: VCard
 * Revision:    $Revision: 1.49 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/08/20 15:52:38 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.EMailAddressQuery;
import org.opencrx.kernel.account1.jmi1.AbstractGroup;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.account1.jmi1.WebAddress;
import org.opencrx.kernel.generic.jmi1.Note;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;

public class VCard extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new VCard());
	}
	
    //-------------------------------------------------------------------------
	public static VCard getInstance(
	) throws ServiceException {
		return getInstance(VCard.class);
	}

	//-------------------------------------------------------------------------
	protected VCard(
	) {
		
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
    protected Date getUtcDate(
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
        return date;
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
    	return salutations.get(Integer.valueOf(salutationCode));
    }
    
    //-------------------------------------------------------------------------
    private short mapToSalutationCode(
        String salutation
    ) throws ServiceException {
    	for(Entry<Integer,String> entry: salutations.entrySet()) {
    		if(entry.getValue().equals(salutation)) {
    			return entry.getKey().shortValue();
    		}
    	}
    	return 0;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Update sourceVcard with account values and return merged vcard. 
     */
    public String mergeVcard(
        Account account,
        String sourceVcard,
        List<String> statusMessage
    ) throws ServiceException {
    	boolean isContact = account instanceof Contact;
        // N
        String n = null;        
        if(isContact) {
        	Contact contact = (Contact)account;
            if(contact.getLastName() != null) {
                String lastName = contact.getLastName();
                String firstName = contact.getFirstName() == null ? "" : contact.getFirstName();
                String middleName = contact.getMiddleName() == null ? "" : contact.getMiddleName();
                Short salutationCode = contact.getSalutationCode();
                String salutation = contact.getSalutation() == null ?
                    (salutationCode == null) || (salutationCode.shortValue() == 0) ?
                        null :
                        this.mapToSalutationText(salutationCode) :
                    contact.getSalutation();
                String suffix = contact.getSuffix() == null ? "" : contact.getSuffix();
                n = lastName + ";" + firstName + ";"+ middleName + ";" + (salutation == null ? "" : salutation) + ";" + suffix;
            }
        }
        else if(account instanceof AbstractGroup) {
        	AbstractGroup group = (AbstractGroup)account;
            if(group.getName() != null) {
                String name = group.getName();
                n = name;
            }
        }
        // FN
        String fn = null;
        if(isContact) {
        	Contact contact = (Contact)account;
            String firstName = contact.getFirstName() == null ? "" : contact.getFirstName();
            String lastName = contact.getLastName() == null ? "" : contact.getLastName();
            String middleName = contact.getMiddleName() == null ? "" : contact.getMiddleName();
            String suffix = contact.getSuffix() == null ? "" : contact.getSuffix();
            fn = firstName + (middleName.length() == 0 ? "" : " " + middleName) + (lastName.length() == 0 ? "" : " " + lastName) + (suffix.length() == 0 ? "" : " " + suffix);
        }
        else {
            String fullName = account.getFullName() == null ? "" : account.getFullName();
            fn = fullName;            
        }
        // NICKNAME
        String nickName = null;
        if(isContact) {
        	Contact contact = (Contact)account;        	
            nickName = contact.getNickName() == null ? "" : contact.getNickName();            
        }
        // REV
        String rev = DateFormat.getInstance().format(new Date());        
        // ORG
        String org = null;
        if(isContact) {
        	Contact contact = (Contact)account;        	
            if(contact.getOrganization() != null) {
                org = contact.getOrganization();
            }
        }
        // TITLE
        String title = null;
        if(isContact) {
        	Contact contact = (Contact)account;        	
            if(contact.getJobTitle() != null) {
                title = contact.getJobTitle();
            }
        }
        // BDAY
        String bday = null;
        if(isContact) {
        	Contact contact = (Contact)account;        	
            if(contact.getBirthdate() != null) {
                bday = DateFormat.getInstance().format(contact.getBirthdate());
            }
        }
        AccountAddress[] addresses = new AccountAddress[11];
        try {
            addresses = Accounts.getInstance().getMainAddresses(
            	account
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
            ((PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull();
        // TEL;HOME;VOICE
        String telHomeVoice = addresses[Accounts.PHONE_HOME] == null ? 
            "" : 
            ((PhoneNumber)addresses[Accounts.PHONE_HOME]).getPhoneNumberFull();
        // TEL;CELL;VOICE
        String telCellVoice = addresses[Accounts.MOBILE] == null ? 
            "" : 
            ((PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull();
        // TEL;FAX
        String telWorkFax = addresses[Accounts.FAX_BUSINESS] == null ? 
            "" : 
            ((PhoneNumber)addresses[Accounts.FAX_BUSINESS]).getPhoneNumberFull();
        // TEL;HOME;FAX
        String telHomeFax = addresses[Accounts.FAX_HOME] == null ? 
            "" : 
            ((PhoneNumber)addresses[Accounts.FAX_HOME]).getPhoneNumberFull();
        // ADR;WORK
        String adrWork = "";
        if(addresses[Accounts.POSTAL_BUSINESS] != null) {
            PostalAddress postalAddress = (PostalAddress)addresses[Accounts.POSTAL_BUSINESS];
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<String> addressLines = postalAddress.getPostalAddressLine();
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString(addressLines.get(j)));
            }
            // postalStreet
            List<String> postalStreet = postalAddress.getPostalStreet();
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString(postalStreet.get(j)));
            }
            // postalCity
            adr.append(
                (postalAddress.getPostalCity() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalCity())
            );
            // postalState
            adr.append(
                (postalAddress.getPostalState() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalState())
            );            
            // postalCode
            adr.append(
                (postalAddress.getPostalCode() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalCode())
            );
            adr.append(";");
            adr.append(
            	Addresses.getInstance().mapToPostalCountryText(postalAddress.getPostalCountry())
            );   
            adrWork = adr.toString();
        }
        // ADR;HOME
        String adrHome = "";
        if(addresses[Accounts.POSTAL_HOME] != null) {
            PostalAddress postalAddress = (PostalAddress)addresses[Accounts.POSTAL_HOME];
            StringBuilder adr = new StringBuilder();
            // postalAddressLine
            List<String> addressLines = postalAddress.getPostalAddressLine();
            for(int j = 0; j < addressLines.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString(addressLines.get(j)));
            }
            // postalStreet
            List<String> postalStreet = postalAddress.getPostalStreet();
            for(int j = 0; j < postalStreet.size(); j++) {
                adr.append(adr.length() == 0 ? "" : "=0D=0A");
                adr.append(this.encodeString(postalStreet.get(j)));
            }
            // postalCity
            adr.append(
                (postalAddress.getPostalCity() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalCity())
            );
            // postalState
            adr.append(
                (postalAddress.getPostalState() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalState())
            );
            // postalCode
            adr.append(
                (postalAddress.getPostalCode() == null) ? 
                    ";" : 
                    ";" + this.encodeString(postalAddress.getPostalCode())
            );
            // postalCountry
            adr.append(";");
            adr.append(
            	Addresses.getInstance().mapToPostalCountryText(postalAddress.getPostalCountry())
            );   
            adrHome = adr.toString();
        }
        // URL;WORK
        String urlWork = addresses[Accounts.WEB_BUSINESS] == null ? 
        	"" : 
        	((WebAddress)addresses[Accounts.WEB_BUSINESS]).getWebUrl();
        // URL;HOME
        String urlHome = addresses[Accounts.WEB_HOME] == null ? 
            "" : 
            ((WebAddress)addresses[Accounts.WEB_HOME]).getWebUrl();
        // EMAIL;PREF;INTERNET
        String emailWork = addresses[Accounts.MAIL_BUSINESS] == null ? 
            "" : 
            ((EMailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress();
        // EMAIL;INTERNET
        String emailHome = addresses[Accounts.MAIL_HOME] == null ? 
            "" : 
            ((EMailAddress)addresses[Accounts.MAIL_HOME]).getEmailAddress();        
        // return if data is missing
        if(!statusMessage.isEmpty()) {
            return null;
        }        
        if((sourceVcard == null) || (sourceVcard.length() == 0)) {
            String uid = null;
        	// Get from externalLink
        	for(int i = 0; i < account.getExternalLink().size(); i++) {
        		if(account.getExternalLink().get(i).startsWith(VCARD_SCHEMA)) {
        			uid = account.getExternalLink().get(i).substring(VCARD_SCHEMA.length());
        			break;
        		}
        	}
        	if(uid == null) {
	            // Derive from qualifier
	            try {
	                uid = account.refGetPath().getBase();
	            }
	            catch(Exception e) {
	            	// Generate new
	                uid = UUIDConversion.toUID(UUIDs.getGenerator().next());
	            }
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
        PostalAddress address,
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
            List<String> street = new ArrayList<String>();
            String temp = tokens[2];
            int pos = 0;
            while((pos = temp.indexOf("=0D=0A")) >= 0) {
                street.add(temp.substring(0, pos));
                temp = temp.substring(pos + 6);
            }
            street.add(temp);
            address.getPostalAddressLine().clear();
            for(int i = 0; i < street.size()-1; i++) {
                address.getPostalAddressLine().add(street.get(i));
            }
            address.getPostalStreet().clear();
            address.getPostalStreet().add(street.get(street.size()-1));
            address.setPostalCity(tokens[3]);
            address.setPostalState(tokens[4]);
            address.setPostalCode(tokens[5]);
            address.setPostalCountry(new Short((short)0));
            // Lookup country
            SysLog.trace("lookup country", tokens[6]);
            address.setPostalCountry(
            	Addresses.getInstance().mapToPostalCountryCode(tokens[6])
            );
            SysLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updatePhoneNumber(
        PhoneNumber address,
        String newValue
    ) throws ServiceException {
        if((newValue != null) && (newValue.length() > 0)) {
            address.setPhoneNumberFull(newValue);
            address.setAutomaticParsing(Boolean.TRUE);
            Addresses.getInstance().updatePhoneNumber(address);
            SysLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updateWebAddress(
        WebAddress address,
        String newValue
    ) {
        if((newValue != null) && (newValue.length() > 0)) {
            address.setWebUrl(newValue);
            SysLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public boolean updateEMailAddress(
        EMailAddress address,
        String newValue
    ) {
        if((newValue != null) && (newValue.length() > 0)) {
            address.setEmailAddress(newValue);
            SysLog.trace("updated address", address);
            return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------
    public Map<String,String> parseVCard(
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
        Account account,
        short locale,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
        try {
            InputStream is = new ByteArrayInputStream(item);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, "UTF-8")
            );
            Map<String,String> vcard = this.parseVCard(reader);
            SysLog.trace("parsed vcard", vcard);
            return this.importItem(
                vcard,
                account,
                locale,
                report
            );
        }
        catch(IOException e) {
        	SysLog.warning("can not read item", e.getMessage());
        }
        return null;
    }

    //-------------------------------------------------------------------------
    private Account importItem(
        Map<String,String> vcard,
        Account account,
        short locale,
        List<String> report
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(account);
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
        dateTimeFormatter.setLenient(false);
        boolean isContact = account instanceof Contact;
        // name
        String name = vcard.get("N");
        if(isContact) {
        	Contact contact = (Contact)account;
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
                    contact.setLastName(nameTokens[0]);        
                }
                else if(contact.getLastName() == null) {
                    contact.setLastName("N/A");
                }
                // firstName
                if(nameTokens[1].length() > 0) {
                    contact.setFirstName(nameTokens[1]);
                }
                // middleName
                if(nameTokens[2].length() > 0) {
                    contact.setMiddleName(nameTokens[2]);
                }
                // salutation
                if(nameTokens[3].length() > 0) {
                    String salutation = nameTokens[3];
                    short salutationCode = this.mapToSalutationCode(salutation);
                    if(
                        (contact.getSalutation() != null) && 
                        (contact.getSalutation().length() > 0)
                    ) {
                        contact.setSalutation(salutation);
                    }
                    else if(salutationCode != 0) {
                        contact.setSalutationCode(salutationCode);
                    }
                    else {
                        contact.setSalutation(salutation);                        
                    }                    
                }
                // suffix
                if(nameTokens[4].length() > 0) {
                    contact.setSuffix(nameTokens[4]);
                }
            }
            // nickName
            String nickName = vcard.get("NICKNAME");
            if((nickName != null) && (nickName.length() > 0)) {
                contact.setNickName(nickName);
            }
            // jobTitle
            String jobTitle = vcard.get("TITLE");
            if((jobTitle != null) && (jobTitle.length() > 0)) {
                contact.setJobTitle(jobTitle);
            }
            // organization
            String organization = vcard.get("ORG");
            if((organization != null) && (organization.length() > 0)) {
                contact.setOrganization(organization);
            }
            // bday
            String bday = vcard.get("BDAY");
            if((bday != null) && (bday.length() > 0)) {
                try {
                    contact.setBirthdate(
                    	this.getUtcDate(
                            bday, 
                            dateTimeFormatter
                        )
                    );
                } 
                catch(Exception e) {}
            }
            report.add("Update account");
        }
        else if(account instanceof AbstractGroup){
        	AbstractGroup group = (AbstractGroup)account;
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
                	group.setName(nameTokens[0]);        
                }
                else if(group.getName() == null) {
                    group.setName("N/A");
                }
            }
        }
        // externalLink
        boolean hasVcardUid = false;
        List<String> externalLinks = account.getExternalLink();
        String vcardUid = vcard.get("UID") == null ? 
        	account.refGetPath().getBase() : 
        	vcard.get("UID");
        for(int i = 0; i < externalLinks.size(); i++) {
            if(externalLinks.get(i).startsWith(VCARD_SCHEMA)) {
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
        	Note note = null;
        	try {
	            note = (Note)pm.getObjectById(
	                account.refGetPath().getDescendant(new String[]{"note", "VCARD"})
	            );
        	} catch(Exception e) {}
            if(note == null) {
                note = pm.newInstance(Note.class);
                note.refInitialize(false, false);
                account.addNote(
                	false,
                	this.getUidAsString(),
                	note
                );
                report.add("Create note");
            }
            else {
                report.add("Update note");
            }
            note.setTitle("VCard note");
            String text = "";
            int pos = 0;
            while((pos = s.indexOf("=0D=0A")) >= 0) {
                text += s.substring(0, pos) + "\n";
                s = s.substring(pos + 6);
            }
            note.setText(text);
        }

        // addresses
        Collection<AccountAddress> addresses = account.getAddress();
        PostalAddress adrHome = null;
        PostalAddress adrWork = null;
        PhoneNumber telHomeVoice = null;
        PhoneNumber telWorkVoice = null;
        PhoneNumber telHomeFax = null;
        PhoneNumber telFax = null;
        WebAddress urlHome = null;
        WebAddress urlWork = null;
        PhoneNumber telCellVoice = null;
        EMailAddress emailInternet = null;
        EMailAddress emailPrefInternet = null;

        // get addresses
        for(AccountAddress address: addresses) {
            List<Short> usage = new ArrayList<Short>();
            for(Iterator<Short> j = address.getUsage().iterator(); j.hasNext(); ) {
                usage.add(j.next());
            }
            if(address instanceof PostalAddress) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    adrHome = (PostalAddress)address;
                }
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    adrWork = (PostalAddress)address;
                }          
            }
            else if(address instanceof EMailAddress) {
                if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    emailPrefInternet = (EMailAddress)address;
                }
                else if(usage.contains(Addresses.USAGE_HOME)) {
                    emailInternet = (EMailAddress)address;
                }
            }
            else if(address instanceof PhoneNumber) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    telHomeVoice = (PhoneNumber)address;
                }
                // work voice
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    telWorkVoice = (PhoneNumber)address;
                }              
                // home fax
                else if(usage.contains(Addresses.USAGE_HOME_FAX)) {
                    telHomeFax = (PhoneNumber)address;
                }
                // work fax
                else if(usage.contains(Addresses.USAGE_BUSINESS_FAX)) {
                    telFax = (PhoneNumber)address;
                }              
                // cell voice
                else if(usage.contains(Addresses.USAGE_MOBILE)) {
                    telCellVoice = (PhoneNumber)address;
                }          
            }
            else if(address instanceof WebAddress) {
                if(usage.contains(Addresses.USAGE_HOME)) {
                    urlHome = (WebAddress)address;
                }
                // work url
                else if(usage.contains(Addresses.USAGE_BUSINESS)) {
                    urlWork = (WebAddress)address;
                }
            }
        }
        // update adrHome
        s = vcard.get("ADR;HOME") != null ? vcard.get("ADR;HOME") :
            vcard.get("ADR;TYPE=HOME") != null ? vcard.get("ADR;TYPE=HOME") :
            vcard.get("ADR;HOME;ENCODING=QUOTED-PRINTABLE");
        if((s != null) && (s.length() > 0) && !s.startsWith(";;;")) {
            if(adrHome == null) {
                adrHome = pm.newInstance(PostalAddress.class);
                adrHome.refInitialize(false, false);
                adrHome.getUsage().add(Addresses.USAGE_HOME);
                adrHome.setMain(Boolean.TRUE);
                this.updatePostalAddress(adrHome, s, locale);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	adrHome
                );
                report.add("Create postal address");
            }
            else {
            	this.updatePostalAddress(adrHome, s, locale);
                report.add("Update postal address");
            }
        }
        // update adrWork
        s = vcard.get("ADR;WORK") != null ? vcard.get("ADR;WORK") :
            vcard.get("ADR;TYPE=WORK") != null ? vcard.get("ADR;TYPE=WORK") :
            vcard.get("ADR;WORK;ENCODING=QUOTED-PRINTABLE");
        if((s != null) && (s.length() > 0) && !s.startsWith(";;;")) {
            if(adrWork == null) {
            	adrWork = pm.newInstance(PostalAddress.class);
            	adrWork.refInitialize(false, false);
                adrWork.getUsage().add(Addresses.USAGE_BUSINESS);
                adrWork.setMain(Boolean.TRUE);
                this.updatePostalAddress(adrWork, s, locale);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	adrWork
                );
                report.add("Create postal address");
            }
            else {
            	this.updatePostalAddress(adrWork, s, locale);
                report.add("Update postal address");
            }
        }
        // update telHomeVoice
        s = vcard.get("TEL;HOME;VOICE") != null ? vcard.get("TEL;HOME;VOICE") :
            vcard.get("TEL;TYPE=HOME");
        if((s != null) && (s.length() > 0)) {
            if(telHomeVoice == null) {
                telHomeVoice = pm.newInstance(PhoneNumber.class);
                telHomeVoice.refInitialize(false, false);                
                telHomeVoice.getUsage().add(Addresses.USAGE_HOME);
                telHomeVoice.setMain(Boolean.TRUE);
                this.updatePhoneNumber(telHomeVoice, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	telHomeVoice
                );
                report.add("Create phone number");
            }
            else {
            	this.updatePhoneNumber(telHomeVoice, s);
                report.add("Update phone number");
            }
        }
        // update telWorkVoice
        s = vcard.get("TEL;WORK;VOICE") != null ? vcard.get("TEL;WORK;VOICE") :
            vcard.get("TEL;TYPE=WORK");
        if((s != null) && (s.length() > 0)) {        
            if(telWorkVoice == null) {
            	telWorkVoice = pm.newInstance(PhoneNumber.class);
            	telWorkVoice.refInitialize(false, false);            	
                telWorkVoice.getUsage().add(Addresses.USAGE_BUSINESS);
                telWorkVoice.setMain(Boolean.TRUE);
                this.updatePhoneNumber(telWorkVoice, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	telWorkVoice
                );
                report.add("Create phone number");
            }
            else {
            	this.updatePhoneNumber(telWorkVoice, s);
                report.add("Update phone number");
            }
        }
        // update telHomeFax
        s = vcard.get("TEL;HOME;FAX");
        if((s != null) && (s.length() > 0)) {                
            if(telHomeFax == null) {
            	telHomeFax = pm.newInstance(PhoneNumber.class);
            	telWorkVoice.refInitialize(false, false);            	
                telHomeFax.getUsage().add(Addresses.USAGE_HOME_FAX);
                telHomeFax.setMain(Boolean.TRUE);
                this.updatePhoneNumber(telHomeFax, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	telHomeFax
                );
                report.add("Create phone number");
            }
            else {
            	this.updatePhoneNumber(telHomeFax, s);
                report.add("Update phone number");
            }
        }
        // update telFax
        s = vcard.get("TEL;FAX") != null ? vcard.get("TEL;FAX") :
            vcard.get("TEL;TYPE=FAX");
        if((s != null) && (s.length() > 0)) {                
            if(telFax == null) {
            	telFax = pm.newInstance(PhoneNumber.class);
            	telFax.refInitialize(false, false);            	
                telFax.getUsage().add(Addresses.USAGE_BUSINESS_FAX);
                telFax.setMain(Boolean.TRUE);
                this.updatePhoneNumber(telFax, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	telFax
                );
                report.add("Create phone number");
            }
            else {
            	this.updatePhoneNumber(telFax, s);
                report.add("Update phone number");
            }
        }
        // update telCellVoice
        s = vcard.get("TEL;CELL;VOICE") != null ? vcard.get("TEL;CELL;VOICE") :
            vcard.get("TEL;TYPE=CELL");
        if((s != null) && (s.length() > 0)) {                
            if(telCellVoice == null) {
            	telCellVoice = pm.newInstance(PhoneNumber.class);
            	telCellVoice.refInitialize(false, false);            	
                telCellVoice.getUsage().add(Addresses.USAGE_MOBILE);
                telCellVoice.setMain(Boolean.TRUE);
                this.updatePhoneNumber(telCellVoice, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	telCellVoice
                );
                report.add("Create phone number");
            }
            else {
            	this.updatePhoneNumber(telCellVoice, s);
                report.add("Update phone number");
            }
        }
        // update urlHome
        s = vcard.get("URL;HOME");
        if((s != null) && (s.length() > 0)) {                
            if(urlHome == null) {
                urlHome = pm.newInstance(WebAddress.class);
                urlHome.refInitialize(false, false);                
                urlHome.getUsage().add(Addresses.USAGE_HOME);
                urlHome.setMain(Boolean.TRUE);
                this.updateWebAddress(urlHome, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	urlHome
                );
                report.add("Create web address");
            }
            else {
            	this.updateWebAddress(urlHome, s);
                report.add("Update web address");
            }
        }
        // update urlWork
        s = vcard.get("URL;WORK");
        if((s != null) && (s.length() > 0)) {                
            if(urlWork == null) {
            	urlWork = pm.newInstance(WebAddress.class);
            	urlWork.refInitialize(false, false);            	
                urlWork.getUsage().add(Addresses.USAGE_BUSINESS);
                urlWork.setMain(Boolean.TRUE);
                this.updateWebAddress(urlWork, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	urlWork
                );
                report.add("Create web address");
            }
            else {
            	this.updateWebAddress(urlWork, s);
                report.add("Update web address");
            }
        }
        // update emailPrefInternet
        s = vcard.get("EMAIL;PREF;INTERNET") != null ? vcard.get("EMAIL;PREF;INTERNET") :
            vcard.get("EMAIL;TYPE=WORK");
        if((s != null) && (s.length() > 0)) {                
            if(emailPrefInternet == null) {
                emailPrefInternet = pm.newInstance(EMailAddress.class);
                emailPrefInternet.refInitialize(false, false);                
                emailPrefInternet.getUsage().add(Addresses.USAGE_BUSINESS);
                emailPrefInternet.setMain(Boolean.TRUE);
                this.updateEMailAddress(emailPrefInternet, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	emailPrefInternet
                );
                report.add("Create email address");
            }
            else {
            	this.updateEMailAddress(emailPrefInternet, s);
                report.add("Update email address");
            }
        }
        // update emailInternet
        s = vcard.get("EMAIL;INTERNET") != null ? vcard.get("EMAIL;INTERNET") :
            vcard.get("EMAIL;TYPE=HOME");
        if((s != null) && (s.length() > 0)) {                
            if(emailInternet == null) {
                emailInternet = pm.newInstance(EMailAddress.class);
                emailInternet.refInitialize(false, false);                
                emailInternet.getUsage().add(Addresses.USAGE_HOME);
                emailInternet.setMain(Boolean.TRUE);
                this.updateEMailAddress(emailInternet, s);
                account.addAddress(
                	false,
                	this.getUidAsString(),
                	emailInternet
                );
                report.add("Create email address");
            }
            else {
            	this.updateEMailAddress(emailInternet, s);
                report.add("Update email address");
            }
        }
        return account;
    }
        
    //-------------------------------------------------------------------------
    public Account updateAccount(
        Map<String,String> vcard,
        org.opencrx.kernel.account1.jmi1.Segment accountSegment,
        short locale,
        List<String> report
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
        String lookupEmail = vcard.get("EMAIL;PREF;INTERNET");
        Account contact = null;
        if((lookupEmail != null) && (lookupEmail.length() > 0)) {
        	SysLog.trace("looking up", lookupEmail);
            EMailAddressQuery addressQuery = (EMailAddressQuery)pm.newQuery(AccountAddress.class);
            addressQuery.identity().like(
            	accountSegment.refGetPath().getDescendant(new String[]{"account", ":*", "address", ":*"}).toXRI()
            );
            addressQuery.thereExistsEmailAddress().equalTo(lookupEmail);
            List<EMailAddress> addresses = accountSegment.getExtent(addressQuery);
            if(addresses.iterator().hasNext()) {
            	SysLog.trace("address found");
                AccountAddress address = addresses.iterator().next();
                contact = (Account)pm.getObjectById(
                    address.refGetPath().getParent().getParent()
                );
            }
        }
        SysLog.trace("account", contact);
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
    public static final Map<Integer,String> salutations = new HashMap<Integer,String>();
    
    static {
    	salutations.put(0, "NA");
    	salutations.put(1, "Mr.");
    	salutations.put(2, "Mrs.");
    	salutations.put(3, "Miss");
    	salutations.put(4, "Mr. and Mrs.");
    	salutations.put(5, "Company");
    	salutations.put(6, "Brothers");
    	salutations.put(7, "Prof.");
    	salutations.put(8, "Dr.");
    	salutations.put(9, "Family");    	
    }
    
    public static final String DATETIME_FORMAT =  "yyyyMMdd'T'HHmmss";
    public static final String MIME_TYPE = "text/x-vcard";
    public static final String PROD_ID = "//OPENCRX//Core Version 2//EN";
    
    public static final int MIME_TYPE_CODE = 3;
    public static final short DEFAULT_LOCALE = 0;
    public final static String VCARD_SCHEMA = "VCARD:";    

}

//--- End of File -----------------------------------------------------------

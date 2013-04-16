/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: Addresses
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Codes;

public class Addresses extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Addresses());
	}
	
    //-------------------------------------------------------------------------
	public static Addresses getInstance(
	) throws ServiceException {
		return getInstance(Addresses.class);
	}

	//-------------------------------------------------------------------------
	protected Addresses(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public String mapToPostalCountryText(
        short countryCode,
        org.opencrx.kernel.code1.jmi1.Segment codeSegment        
    ) throws ServiceException {
    	String country = POSTAL_COUNTRIES_BY_CODE.get(Short.valueOf(countryCode));
    	if(country != null) {
    		return country;
    	}
    	if(codeSegment != null) {
    		try {
		    	Codes codes = new Codes(codeSegment);
		    	country = (String)codes.getLongText(
		    		"country", 
		    		(short)0, 
		    		true, // codeAsKey 
		    		true // includeAll
		    	).get(countryCode);
		    	if(country != null) {
		    		POSTAL_COUNTRIES_BY_CODE.put(
		    			countryCode,
		    			country
		    		);
		    	}
    		} catch(Exception e) {}    		
    	}
    	return null;
    }
    
    //-----------------------------------------------------------------------
    public short mapToPostalCountryCode(    	
        String country,
        org.opencrx.kernel.code1.jmi1.Segment codeSegment
    ) throws ServiceException {    	
    	{
	    	country = country.toUpperCase();
	    	Integer lastMatchLength = null;
	    	Short countryCode = null;
	    	for(Entry<String,Short> entry: POSTAL_COUNTRIES_BY_TEXT.entrySet()) {
	    		if(entry.getKey().toUpperCase().indexOf(country) >= 0) {
	    			if(lastMatchLength == null || entry.getKey().length() < lastMatchLength) {
	    				countryCode = entry.getValue();
	    				lastMatchLength = entry.getKey().length();
	    			}
	    		}
	    	}
	    	if(countryCode != null) {
	    		return countryCode;
	    	}
    	}
    	if(codeSegment != null) {
    		try {
		    	Codes codes = new Codes(codeSegment);
		    	short countryCode = codes.findCodeFromValue(
		    		country, 
		    		"country" 
		    	);
		    	POSTAL_COUNTRIES_BY_TEXT.put(
		    		country,
		    		countryCode
		    	);
		    	return countryCode;
    		} catch(Exception e) {
    			SysLog.warning("Unable to map country. Exception occured", e);
    		}
    	}
    	SysLog.warning("Unable to map country. Country not found", country);
    	return 0;
    }

    //-------------------------------------------------------------------------
    public Integer mapToPhoneCountryPrefix(
        int countryCode
    ) throws ServiceException {
    	return PHONE_COUNTRIES.get(countryCode);
    }

    //-----------------------------------------------------------------------
    public int mapToPhoneCountryCode(
        int prefix
    ) throws ServiceException {
    	for(Entry<Short,Integer> entry: PHONE_COUNTRIES.entrySet()) {
    		if(entry.getValue() == prefix) {
    			return entry.getKey().shortValue();
    		}
    	}
    	return 0;
    }

    //-------------------------------------------------------------------------
    public void updatePhoneNumber(
    	org.opencrx.kernel.address1.jmi1.PhoneNumberAddressable phoneNumber
    ) throws ServiceException {
        String phoneNumberFull = phoneNumber.getPhoneNumberFull();
        boolean automaticParsing = phoneNumber.isAutomaticParsing();
        if(
        	automaticParsing &&
            (phoneNumberFull != null)
        ) {
        	String countryCode = "0";
        	String areaCode = "";
        	String localNumber = "";
        	String extension= "";
        	// Assuming phone number has format +nn nnnnnnnnnnnnnn
        	String[] parts0 = phoneNumberFull.split(" ");
        	if(parts0.length == 2 && parts0[0].startsWith("+")) {
        		countryCode = parts0[0].substring(1).trim();
                if("1".equals(countryCode)) {
                    countryCode = "840";
                }
                else {
                    Integer code = null;
                    try {
                    	code = this.mapToPhoneCountryCode(Short.valueOf(countryCode));
                    } catch(Exception e) {}
                    countryCode = code == null ? 
                    	"0" : 
                    	code.toString();
                }
                if(parts0[1].length() > 9) {
	                areaCode = parts0[1].substring(0, 3);
	                localNumber = parts0[1].substring(3);
                } else if(parts0[1].length() > 2) {
	                areaCode = parts0[1].substring(0, 2);
	                localNumber = parts0[1].substring(2);                	
                } else {
	                areaCode = "";
	                localNumber = parts0[1];         	                	
                }
                extension = "";                
        	}
        	// Assuming phone number has format +nn nnn nnn nn nn
        	else if(parts0.length >= 3 && parts0[0].startsWith("+")) {
        		countryCode = parts0[0].substring(1).trim();
                if("1".equals(countryCode)) {
                    countryCode = "840";
                }
                else {
                    Integer code = null;
                    try {
                    	code = this.mapToPhoneCountryCode(Short.valueOf(countryCode));
                    } catch(Exception e) {}
                    countryCode = code == null ? 
                    	"0" : 
                    	code.toString();
                }
                areaCode = parts0[1];
                localNumber = "";
                String sep = "";
                for(int i = 2; i < parts0.length; i++) {
                	localNumber += sep;
                	localNumber += parts0[i];
                	sep = " ";
                }
                extension = "";                
        	}
            // Assuming phone number has format +nn (nnn) nnn-nnnn x nnn
        	else {
	            List<String> parts1 = new ArrayList<String>();
	            StringTokenizer tokenizer = new StringTokenizer(phoneNumberFull, "/+()x");
	            while(tokenizer.hasMoreTokens()) {
	                parts1.add(tokenizer.nextToken());
	            }
	            if(parts1.size() >= 3) {
	                countryCode = parts1.get(0).toString().trim();
	                if("1".equals(countryCode)) {
	                    countryCode = "840";
	                }
	                else {
	                    Integer code = null;
	                    try {
	                    	code = this.mapToPhoneCountryCode(Short.valueOf(countryCode));
	                    } catch(Exception e) {}
	                    countryCode = code == null ? 
	                    	"0" : 
	                    	code.toString();
	                }
	                areaCode = parts1.get(1).toString().trim();
	                localNumber = parts1.get(2).toString().trim();
	                extension = parts1.size() >= 4 ? parts1.get(3).toString().trim() : "";
	            }
        	}
        	// Only touch fields if modified
        	if(Short.valueOf(countryCode) != phoneNumber.getPhoneCountryPrefix()) {
        		phoneNumber.setPhoneCountryPrefix(Short.valueOf(countryCode));
        	}
        	if(!areaCode.equals(phoneNumber.getPhoneCityArea())) {
        		phoneNumber.setPhoneCityArea(areaCode);
        	}
        	if(!localNumber.equals(phoneNumber.getPhoneLocalNumber())) {
        		phoneNumber.setPhoneLocalNumber(localNumber);
        	}
        	if(!extension.equals(phoneNumber.getPhoneExtension())) {
        		phoneNumber.setPhoneExtension(extension);
        	}
        }
    }
    
	/**
	 * EMail types.
	 */
	public enum EMailType {
		
		NA((short)0),
		SMTP((short)1),
		X500((short)2);
		
		private short value;
		
		private EMailType(
			short value
		) {
			this.value = value;
		}
		
		public short getValue(
		) {
			return this.value;
		}
		
	}
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String[] ADDRESS_TYPES = 
        new String[]{
            "org:opencrx:kernel:address1:PostalAddressable",
            "org:opencrx:kernel:address1:PhoneNumberAddressable",
            "org:opencrx:kernel:address1:EMailAddressable",
            "org:opencrx:kernel:address1:WebAddressable",
            "org:opencrx:kernel:address1:RoomAddressable"
        };

    public static final Map<Short,String> POSTAL_COUNTRIES_BY_CODE = new ConcurrentHashMap<Short,String>();
    private static final Map<String,Short> POSTAL_COUNTRIES_BY_TEXT = new ConcurrentHashMap<String,Short>(); 
    public static final Map<Short,Integer> PHONE_COUNTRIES = new HashMap<Short,Integer>();
    
    static {
		POSTAL_COUNTRIES_BY_CODE.put((short)0, "N/A");
		POSTAL_COUNTRIES_BY_CODE.put((short)4, "Afghanistan [AF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)8, "Albania [AL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)12, "Algeria [DZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)16, "American Samoa [AS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)20, "Andorra [AD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)24, "Angola [AO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)660, "Anguilla [AI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)10, "Antarctica [AQ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)28, "Antigua and Barbuda [AG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)32, "Argentina [AR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)51, "Armenia [AM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)533, "Aruba [AW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)36, "Australia [AU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)40, "Austria [AT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)31, "Azerbaijan [AZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)44, "Bahamas [BS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)48, "Bahrain [BH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)50, "Bangladesh [BD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)52, "Barbados [BB]");
		POSTAL_COUNTRIES_BY_CODE.put((short)112, "Belarus [BY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)56, "Belgium [BE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)84, "Belize [BZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)204, "Benin [BJ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)60, "Bermuda [BM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)64, "Bhutan [BT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)68, "Bolivia [BO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)70, "Bosnia and Herzegovina [BA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)72, "Botswana [BW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)74, "Bouvet Island [BV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)76, "Brazil [BR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)86, "British Indian Ocean Territory [IO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)96, "Brunei Darussalam [BN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)100, "Bulgaria [BG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)854, "Burkina Faso [BF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)108, "Burundi [BI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)116, "Cambodia [KH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)120, "Cameroon [CM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)124, "Canada [CA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)132, "Cape Verde [CV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)136, "Cayman Islands [KY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)140, "Central African Republic [CF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)148, "Chad [TD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)152, "Chile [CL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)156, "China [CN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)162, "Christmas Island [CX]");
		POSTAL_COUNTRIES_BY_CODE.put((short)166, "Cocos (Keeling) Islands [CC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)170, "Colombia [CO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)174, "Comoros [KM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)178, "Congo [CG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)180, "Congo, The Democratic Replublic of the [CD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)184, "Cook Islands [CK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)188, "Costa Rica [CR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)384, "Côte d'Ivoire [CI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)191, "Croatia [HR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)192, "Cuba [CU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)196, "Cyprus [CY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)203, "Czech Republic [CZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)208, "Denmark [DK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)262, "Djibouti [DJ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)212, "Dominica [DM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)214, "Dominican Republic [DO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)626, "East Timor [TP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)218, "Ecuador [EC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)818, "Egypt [EG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)222, "El Salvador [SV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)226, "Equatorial Guinea [GQ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)232, "Eritrea [ER]");
		POSTAL_COUNTRIES_BY_CODE.put((short)233, "Estonia [EE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)231, "Ethiopia [ET]");
		POSTAL_COUNTRIES_BY_CODE.put((short)238, "Falkland Islands (Islas Malvinas) [FK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)234, "Faroe Islands [FO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)242, "Fiji [FJ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)246, "Finland [FI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)249, "France [FR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)254, "French Guiana [GF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)258, "French Polynesia [PF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)260, "French Southern Territories [TF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)266, "Gabon [GA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)270, "Gambia [GM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)268, "Georgia [GE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)276, "Germany [DE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)288, "Ghana [GH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)292, "Gibraltar [GI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)300, "Greece [GR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)304, "Greenland [GL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)308, "Grenada [GD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)312, "Guadeloupe [GP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)316, "Guam [GU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)320, "Guatemala [GT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)324, "Guinea [GN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)624, "Guinea-Bissau [GW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)328, "Guyana [GY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)332, "Haiti [HT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)334, "Heard Island and McDonalds Islands [HM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)336, "Holy See (Vatican City State) [VA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)340, "Honduras [HN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)344, "Hong Kong [HK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)348, "Hungary [HU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)352, "Iceland [IS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)356, "India [IN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)360, "Indonesia [ID]");
		POSTAL_COUNTRIES_BY_CODE.put((short)364, "Iran, Islamic Republic of [IR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)368, "Iraq [IQ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)372, "Ireland [IE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)376, "Israel [IL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)380, "Italy [IT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)388, "Jamaica [JM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)392, "Japan [JP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)400, "Jordan [JO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)398, "Kazakstan [KZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)404, "Kenya [KE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)296, "Kiribati [KI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)408, "Korea, Democratic People's Republic of [KP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)410, "Korea, Republic of [KR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)414, "Kuwait [KW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)417, "Kyrgyzstan [KG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)418, "Lao People's Democratic Republic [LA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)428, "Latvia [LV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)422, "Lebanon [LB]");
		POSTAL_COUNTRIES_BY_CODE.put((short)426, "Lesotho [LS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)430, "Liberia [LR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)434, "Libyan Arab Jamahiriya [LY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)438, "Liechtenstein [LI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)440, "Lithuania [LT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)442, "Luxembourg [LU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)446, "Macau [MO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)807, "Macedonia, The Former Yugoslav Republic of [MK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)450, "Madagascar [MG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)454, "Malawi [MW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)458, "Malaysia [MY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)462, "Maldives [MV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)466, "Mali [ML]");
		POSTAL_COUNTRIES_BY_CODE.put((short)470, "Malta [MT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)584, "Marshall Islands [MH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)474, "Martinique [MQ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)478, "Mauritania [MR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)480, "Mauritius [MU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)175, "Mayotte [YT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)484, "Mexico [MX]");
		POSTAL_COUNTRIES_BY_CODE.put((short)583, "Micronesia, Federated States of [FM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)498, "Moldova, Republic of [MD]");
		POSTAL_COUNTRIES_BY_CODE.put((short)492, "Monaco [MC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)496, "Mongolia [MN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)500, "Montserrat [MS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)504, "Morocco [MA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)508, "Mozambique [MZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)104, "Myanmar [MM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)516, "Namibia [NA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)520, "Nauru [NR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)524, "Nepal [NP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)528, "Netherlands [NL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)530, "Netherlands Antilles [AN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)540, "New Caledonia [NC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)554, "New Zealand [NZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)558, "Nicaragua [NI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)562, "Niger [NE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)566, "Nigeria [NG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)570, "Niue [NU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)574, "Norfolk Island [NF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)580, "Northern Mariana Islands [MP]");
		POSTAL_COUNTRIES_BY_CODE.put((short)578, "Norway [NO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)512, "Oman [OM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)586, "Pakistan [PK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)585, "Palau [PW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)275, "Palestinian Territory, Occupied [PS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)591, "Panama [PA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)598, "Papua New Guinea [PG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)600, "Paraguay [PY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)604, "Peru [PE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)608, "Philippines [PH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)612, "Pitcairn [PN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)616, "Poland [PL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)620, "Portugal [PT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)630, "Puerto Rico [PR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)634, "Qatar [QA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)638, "Reunion [RE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)642, "Romania [RO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)643, "Russian Federation [RU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)646, "Rwanda [RW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)654, "Saint Helena [SH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)659, "Saint Kitts and Nevis [KN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)662, "Saint Lucia [LC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)666, "Saint Pierre and Miquelon [PM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)670, "Saint Vincent and The Grenadines [VC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)882, "Samoa [WS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)674, "San Marino [SM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)678, "Sao Tome and Principe [ST]");
		POSTAL_COUNTRIES_BY_CODE.put((short)682, "Saudia Arabia [SA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)686, "Senegal [SN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)688, "Serbia [RS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)690, "Seychelles [SC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)694, "Sierra Leone [SL]");
		POSTAL_COUNTRIES_BY_CODE.put((short)702, "Singapore [SG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)703, "Slovakia [SK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)705, "Slovenia [SI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)90, "Solomon Islands [SB]");
		POSTAL_COUNTRIES_BY_CODE.put((short)706, "Somalia [SO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)710, "South Africa [ZA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)239, "South Georgia and South Sandwich Islands [GS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)728, "South Sudan [SS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)729, "Sudan [SS]");
		POSTAL_COUNTRIES_BY_CODE.put((short)724, "Spain [ES]");
		POSTAL_COUNTRIES_BY_CODE.put((short)144, "Sri Lanka [LK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)740, "Suriname [SR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)744, "Svalbard and Jan Mayen [SJ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)748, "Swaziland [SZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)752, "Sweden [SE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)756, "Switzerland [CH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)760, "Syrian Arab Republic [SY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)158, "Taiwan, Province of China [TW]");
		POSTAL_COUNTRIES_BY_CODE.put((short)762, "Tajikistan [TJ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)834, "Tanzania, United Republic of [TZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)764, "Thailand [TH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)768, "Togo [TG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)772, "Tokelau [TK]");
		POSTAL_COUNTRIES_BY_CODE.put((short)776, "Tonga [TO]");
		POSTAL_COUNTRIES_BY_CODE.put((short)780, "Trinidad and Tobago [TT]");
		POSTAL_COUNTRIES_BY_CODE.put((short)788, "Tunisia [TN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)792, "Turkey [TR]");
		POSTAL_COUNTRIES_BY_CODE.put((short)795, "Turkmenistan [TM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)796, "Turks and Caicos Islands [TC]");
		POSTAL_COUNTRIES_BY_CODE.put((short)798, "Tuvalu [TV]");
		POSTAL_COUNTRIES_BY_CODE.put((short)800, "Uganda [UG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)804, "Ukraine [UA]");
		POSTAL_COUNTRIES_BY_CODE.put((short)784, "United Arab Emirates [AE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)826, "United Kingdom [GB]");
		POSTAL_COUNTRIES_BY_CODE.put((short)840, "United States of America [US]");
		POSTAL_COUNTRIES_BY_CODE.put((short)581, "United States Minor Outlying Islands [UM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)858, "Uruguay [UY]");
		POSTAL_COUNTRIES_BY_CODE.put((short)860, "Uzbekistan [UZ]");
		POSTAL_COUNTRIES_BY_CODE.put((short)548, "Vanatu [VU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)862, "Venezuela [VE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)704, "Viet Nam [VN]");
		POSTAL_COUNTRIES_BY_CODE.put((short)850, "Virgin Islands, U.S. [VI]");
		POSTAL_COUNTRIES_BY_CODE.put((short)92, "Virgin Islands. British [VG]");
		POSTAL_COUNTRIES_BY_CODE.put((short)876, "Wallis and Futuna [WF]");
		POSTAL_COUNTRIES_BY_CODE.put((short)732, "Western Sahara [EH]");
		POSTAL_COUNTRIES_BY_CODE.put((short)887, "Yemen [YE]");
		POSTAL_COUNTRIES_BY_CODE.put((short)891, "Yugoslavia [YU]");
		POSTAL_COUNTRIES_BY_CODE.put((short)894, "Zambia [ZM]");
		POSTAL_COUNTRIES_BY_CODE.put((short)716, "Zimbabwe [ZW]");
		for(Map.Entry<Short,String> entry: POSTAL_COUNTRIES_BY_CODE.entrySet()) {
			POSTAL_COUNTRIES_BY_TEXT.put(
				entry.getValue(),
				entry.getKey()
			);
		}
		
		PHONE_COUNTRIES.put((short)0, 0);
		PHONE_COUNTRIES.put((short)4, 93);
		PHONE_COUNTRIES.put((short)8, 355);
		PHONE_COUNTRIES.put((short)12, 213);
		PHONE_COUNTRIES.put((short)16, 684);
		PHONE_COUNTRIES.put((short)20, 376);
		PHONE_COUNTRIES.put((short)24, 244);
		PHONE_COUNTRIES.put((short)660, 1);
		PHONE_COUNTRIES.put((short)28, 1);
		PHONE_COUNTRIES.put((short)32, 54);
		PHONE_COUNTRIES.put((short)51, 374);
		PHONE_COUNTRIES.put((short)533, 297);
		PHONE_COUNTRIES.put((short)10000, 247);
		PHONE_COUNTRIES.put((short)36, 61);
		PHONE_COUNTRIES.put((short)10001, 672);
		PHONE_COUNTRIES.put((short)40, 43);
		PHONE_COUNTRIES.put((short)31, 994);
		PHONE_COUNTRIES.put((short)44, 1);
		PHONE_COUNTRIES.put((short)48, 973);
		PHONE_COUNTRIES.put((short)50, 880);
		PHONE_COUNTRIES.put((short)52, 1);
		PHONE_COUNTRIES.put((short)112, 375);
		PHONE_COUNTRIES.put((short)56, 32);
		PHONE_COUNTRIES.put((short)84, 501);
		PHONE_COUNTRIES.put((short)204, 229);
		PHONE_COUNTRIES.put((short)60, 1);
		PHONE_COUNTRIES.put((short)64, 975);
		PHONE_COUNTRIES.put((short)68, 591);
		PHONE_COUNTRIES.put((short)70, 387);
		PHONE_COUNTRIES.put((short)72, 267);
		PHONE_COUNTRIES.put((short)76, 55);
		PHONE_COUNTRIES.put((short)96, 673);
		PHONE_COUNTRIES.put((short)100, 359);
		PHONE_COUNTRIES.put((short)854, 226);
		PHONE_COUNTRIES.put((short)108, 257);
		PHONE_COUNTRIES.put((short)116, 855);
		PHONE_COUNTRIES.put((short)120, 237);
		PHONE_COUNTRIES.put((short)124, 1);
		PHONE_COUNTRIES.put((short)132, 238);
		PHONE_COUNTRIES.put((short)136, 1);
		PHONE_COUNTRIES.put((short)140, 236);
		PHONE_COUNTRIES.put((short)148, 235);
		PHONE_COUNTRIES.put((short)152, 56);
		PHONE_COUNTRIES.put((short)156, 86);
		PHONE_COUNTRIES.put((short)170, 57);
		PHONE_COUNTRIES.put((short)174, 269);
		PHONE_COUNTRIES.put((short)178, 242);
		PHONE_COUNTRIES.put((short)180, 243);
		PHONE_COUNTRIES.put((short)184, 682);
		PHONE_COUNTRIES.put((short)188, 506);
		PHONE_COUNTRIES.put((short)384, 225);
		PHONE_COUNTRIES.put((short)191, 385);
		PHONE_COUNTRIES.put((short)192, 53);
		PHONE_COUNTRIES.put((short)196, 357);
		PHONE_COUNTRIES.put((short)203, 420);
		PHONE_COUNTRIES.put((short)208, 45);
		PHONE_COUNTRIES.put((short)10002, 246);
		PHONE_COUNTRIES.put((short)262, 253);
		PHONE_COUNTRIES.put((short)212, 1);
		PHONE_COUNTRIES.put((short)214, 1);
		PHONE_COUNTRIES.put((short)626, 670);
		PHONE_COUNTRIES.put((short)218, 593);
		PHONE_COUNTRIES.put((short)818, 20);
		PHONE_COUNTRIES.put((short)222, 503);
		PHONE_COUNTRIES.put((short)226, 240);
		PHONE_COUNTRIES.put((short)232, 291);
		PHONE_COUNTRIES.put((short)233, 372);
		PHONE_COUNTRIES.put((short)231, 251);
		PHONE_COUNTRIES.put((short)238, 500);
		PHONE_COUNTRIES.put((short)234, 298);
		PHONE_COUNTRIES.put((short)242, 679);
		PHONE_COUNTRIES.put((short)246, 358);
		PHONE_COUNTRIES.put((short)249, 33);
		PHONE_COUNTRIES.put((short)254, 594);
		PHONE_COUNTRIES.put((short)258, 689);
		PHONE_COUNTRIES.put((short)266, 241);
		PHONE_COUNTRIES.put((short)270, 220);
		PHONE_COUNTRIES.put((short)268, 995);
		PHONE_COUNTRIES.put((short)276, 49);
		PHONE_COUNTRIES.put((short)288, 233);
		PHONE_COUNTRIES.put((short)292, 350);
		PHONE_COUNTRIES.put((short)10003, 881);
		PHONE_COUNTRIES.put((short)300, 30);
		PHONE_COUNTRIES.put((short)304, 299);
		PHONE_COUNTRIES.put((short)308, 1);
		PHONE_COUNTRIES.put((short)10004, 388);
		PHONE_COUNTRIES.put((short)312, 590);
		PHONE_COUNTRIES.put((short)316, 1);
		PHONE_COUNTRIES.put((short)320, 502);
		PHONE_COUNTRIES.put((short)324, 224);
		PHONE_COUNTRIES.put((short)624, 245);
		PHONE_COUNTRIES.put((short)328, 592);
		PHONE_COUNTRIES.put((short)332, 509);
		PHONE_COUNTRIES.put((short)336, 396);
		PHONE_COUNTRIES.put((short)340, 504);
		PHONE_COUNTRIES.put((short)344, 852);
		PHONE_COUNTRIES.put((short)348, 36);
		PHONE_COUNTRIES.put((short)352, 354);
		PHONE_COUNTRIES.put((short)356, 91);
		PHONE_COUNTRIES.put((short)360, 62);
		PHONE_COUNTRIES.put((short)10005, 871);
		PHONE_COUNTRIES.put((short)10006, 874);
		PHONE_COUNTRIES.put((short)10007, 873);
		PHONE_COUNTRIES.put((short)10008, 872);
		PHONE_COUNTRIES.put((short)10009, 870);
		PHONE_COUNTRIES.put((short)10010, 800);
		PHONE_COUNTRIES.put((short)10011, 882);
		PHONE_COUNTRIES.put((short)364, 98);
		PHONE_COUNTRIES.put((short)368, 964);
		PHONE_COUNTRIES.put((short)372, 353);
		PHONE_COUNTRIES.put((short)376, 972);
		PHONE_COUNTRIES.put((short)380, 39);
		PHONE_COUNTRIES.put((short)388, 1);
		PHONE_COUNTRIES.put((short)392, 81);
		PHONE_COUNTRIES.put((short)400, 962);
		PHONE_COUNTRIES.put((short)398, 7);
		PHONE_COUNTRIES.put((short)404, 254);
		PHONE_COUNTRIES.put((short)296, 686);
		PHONE_COUNTRIES.put((short)408, 850);
		PHONE_COUNTRIES.put((short)410, 82);
		PHONE_COUNTRIES.put((short)414, 965);
		PHONE_COUNTRIES.put((short)417, 996);
		PHONE_COUNTRIES.put((short)418, 856);
		PHONE_COUNTRIES.put((short)428, 371);
		PHONE_COUNTRIES.put((short)422, 961);
		PHONE_COUNTRIES.put((short)426, 266);
		PHONE_COUNTRIES.put((short)430, 231);
		PHONE_COUNTRIES.put((short)434, 218);
		PHONE_COUNTRIES.put((short)438, 423);
		PHONE_COUNTRIES.put((short)440, 370);
		PHONE_COUNTRIES.put((short)442, 352);
		PHONE_COUNTRIES.put((short)446, 853);
		PHONE_COUNTRIES.put((short)807, 389);
		PHONE_COUNTRIES.put((short)454, 265);
		PHONE_COUNTRIES.put((short)458, 60);
		PHONE_COUNTRIES.put((short)462, 960);
		PHONE_COUNTRIES.put((short)466, 223);
		PHONE_COUNTRIES.put((short)470, 356);
		PHONE_COUNTRIES.put((short)584, 692);
		PHONE_COUNTRIES.put((short)474, 596);
		PHONE_COUNTRIES.put((short)478, 222);
		PHONE_COUNTRIES.put((short)480, 230);
		PHONE_COUNTRIES.put((short)175, 269);
		PHONE_COUNTRIES.put((short)484, 52);
		PHONE_COUNTRIES.put((short)583, 691);
		PHONE_COUNTRIES.put((short)498, 373);
		PHONE_COUNTRIES.put((short)492, 377);
		PHONE_COUNTRIES.put((short)496, 976);
		PHONE_COUNTRIES.put((short)500, 1);
		PHONE_COUNTRIES.put((short)504, 212);
		PHONE_COUNTRIES.put((short)508, 258);
		PHONE_COUNTRIES.put((short)104, 95);
		PHONE_COUNTRIES.put((short)516, 264);
		PHONE_COUNTRIES.put((short)520, 674);
		PHONE_COUNTRIES.put((short)524, 977);
		PHONE_COUNTRIES.put((short)528, 31);
		PHONE_COUNTRIES.put((short)530, 599);
		PHONE_COUNTRIES.put((short)540, 687);
		PHONE_COUNTRIES.put((short)554, 64);
		PHONE_COUNTRIES.put((short)558, 505);
		PHONE_COUNTRIES.put((short)562, 227);
		PHONE_COUNTRIES.put((short)566, 234);
		PHONE_COUNTRIES.put((short)570, 683);
		PHONE_COUNTRIES.put((short)580, 1);
		PHONE_COUNTRIES.put((short)578, 47);
		PHONE_COUNTRIES.put((short)512, 968);
		PHONE_COUNTRIES.put((short)586, 92);
		PHONE_COUNTRIES.put((short)585, 680);
		PHONE_COUNTRIES.put((short)275, 970);
		PHONE_COUNTRIES.put((short)591, 507);
		PHONE_COUNTRIES.put((short)598, 675);
		PHONE_COUNTRIES.put((short)600, 595);
		PHONE_COUNTRIES.put((short)604, 51);
		PHONE_COUNTRIES.put((short)608, 63);
		PHONE_COUNTRIES.put((short)616, 48);
		PHONE_COUNTRIES.put((short)620, 351);
		PHONE_COUNTRIES.put((short)630, 1);
		PHONE_COUNTRIES.put((short)634, 974);
		PHONE_COUNTRIES.put((short)638, 262);
		PHONE_COUNTRIES.put((short)10012, 0);
		PHONE_COUNTRIES.put((short)10013, 875);
		PHONE_COUNTRIES.put((short)10014, 876);
		PHONE_COUNTRIES.put((short)10015, 877);
		PHONE_COUNTRIES.put((short)10016, 969);
		PHONE_COUNTRIES.put((short)10017, 878);
		PHONE_COUNTRIES.put((short)10018, 888);
		PHONE_COUNTRIES.put((short)10019, 808);
		PHONE_COUNTRIES.put((short)10020, 79);
		PHONE_COUNTRIES.put((short)10021, 979);
		PHONE_COUNTRIES.put((short)642, 40);
		PHONE_COUNTRIES.put((short)643, 7);
		PHONE_COUNTRIES.put((short)646, 250);
		PHONE_COUNTRIES.put((short)654, 290);
		PHONE_COUNTRIES.put((short)659, 1);
		PHONE_COUNTRIES.put((short)662, 1);
		PHONE_COUNTRIES.put((short)666, 508);
		PHONE_COUNTRIES.put((short)670, 1);
		PHONE_COUNTRIES.put((short)882, 685);
		PHONE_COUNTRIES.put((short)674, 378);
		PHONE_COUNTRIES.put((short)678, 239);
		PHONE_COUNTRIES.put((short)682, 966);
		PHONE_COUNTRIES.put((short)686, 221);
		PHONE_COUNTRIES.put((short)690, 248);
		PHONE_COUNTRIES.put((short)694, 232);
		PHONE_COUNTRIES.put((short)702, 65);
		PHONE_COUNTRIES.put((short)703, 421);
		PHONE_COUNTRIES.put((short)705, 386);
		PHONE_COUNTRIES.put((short)90, 677);
		PHONE_COUNTRIES.put((short)706, 252);
		PHONE_COUNTRIES.put((short)710, 27);
		PHONE_COUNTRIES.put((short)239, 0);
		PHONE_COUNTRIES.put((short)724, 34);
		PHONE_COUNTRIES.put((short)144, 94);
		PHONE_COUNTRIES.put((short)736, 249);
		PHONE_COUNTRIES.put((short)740, 597);
		PHONE_COUNTRIES.put((short)748, 268);
		PHONE_COUNTRIES.put((short)752, 46);
		PHONE_COUNTRIES.put((short)756, 41);
		PHONE_COUNTRIES.put((short)760, 963);
		PHONE_COUNTRIES.put((short)158, 886);
		PHONE_COUNTRIES.put((short)762, 992);
		PHONE_COUNTRIES.put((short)834, 255);
		PHONE_COUNTRIES.put((short)764, 66);
		PHONE_COUNTRIES.put((short)768, 228);
		PHONE_COUNTRIES.put((short)772, 690);
		PHONE_COUNTRIES.put((short)776, 676);
		PHONE_COUNTRIES.put((short)10022, 991);
		PHONE_COUNTRIES.put((short)780, 1);
		PHONE_COUNTRIES.put((short)788, 216);
		PHONE_COUNTRIES.put((short)792, 90);
		PHONE_COUNTRIES.put((short)795, 993);
		PHONE_COUNTRIES.put((short)796, 1);
		PHONE_COUNTRIES.put((short)798, 688);
		PHONE_COUNTRIES.put((short)800, 256);
		PHONE_COUNTRIES.put((short)804, 380);
		PHONE_COUNTRIES.put((short)784, 971);
		PHONE_COUNTRIES.put((short)826, 44);
		PHONE_COUNTRIES.put((short)840, 1);
		PHONE_COUNTRIES.put((short)858, 598);
		PHONE_COUNTRIES.put((short)860, 998);
		PHONE_COUNTRIES.put((short)548, 678);
		PHONE_COUNTRIES.put((short)862, 58);
		PHONE_COUNTRIES.put((short)704, 84);
		PHONE_COUNTRIES.put((short)850, 1);
		PHONE_COUNTRIES.put((short)92, 1);
		PHONE_COUNTRIES.put((short)876, 681);
		PHONE_COUNTRIES.put((short)887, 967);
		PHONE_COUNTRIES.put((short)891, 381);
		PHONE_COUNTRIES.put((short)894, 260);
		PHONE_COUNTRIES.put((short)716, 263);
			
	}
    
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
    
    // UNASSIGNED
    public static final String UNASSIGNED_ADDRESS = "UNASSIGNED";
    
}

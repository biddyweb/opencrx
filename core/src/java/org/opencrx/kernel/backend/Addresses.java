/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Addresses.java,v 1.36 2010/03/30 11:56:14 wfro Exp $
 * Description: Addresses
 * Revision:    $Revision: 1.36 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/30 11:56:14 $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.openmdx.base.exception.ServiceException;
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
    	if(codeSegment != null) {
    		try {
		    	Codes codes = new Codes(codeSegment);
		    	return (String)codes.getLongText(
		    		"org:opencrx:kernel:address1:PostalAddressable:postalCountry", 
		    		(short)0, 
		    		true, // codeAsKey 
		    		true // includeAll
		    	).get(countryCode);
    		} catch(Exception e) {}    		
    	}
    	return postalCountries.get(Integer.valueOf(countryCode));
    }
    
    //-----------------------------------------------------------------------
    public short mapToPostalCountryCode(    	
        String country,
        org.opencrx.kernel.code1.jmi1.Segment codeSegment
    ) throws ServiceException {
    	if(codeSegment != null) {
    		try {
		    	Codes codes = new Codes(codeSegment);
		    	return codes.findCodeFromValue(
		    		country, 
		    		"org:opencrx:kernel:address1:PostalAddressable:postalCountry" 
		    	);
    		} catch(Exception e) {}
    	}
    	for(Entry<Short,String> entry: postalCountries.entrySet()) {
    		if(entry.getValue().indexOf(country) >= 0) {
    			return entry.getKey().shortValue();
    		}
    	}
    	return 0;
    }
    
    //-------------------------------------------------------------------------
    public Integer mapToPhoneCountryPrefix(
        int countryCode
    ) throws ServiceException {
    	return phoneCountries.get(countryCode);
    }
    
    //-----------------------------------------------------------------------
    public int mapToPhoneCountryCode(
        int prefix
    ) throws ServiceException {
    	for(Entry<Short,Integer> entry: phoneCountries.entrySet()) {
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
            phoneNumber.setPhoneCountryPrefix(Short.valueOf(countryCode));
            phoneNumber.setPhoneCityArea(areaCode);
            phoneNumber.setPhoneLocalNumber(localNumber);
            phoneNumber.setPhoneExtension(extension);
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

    public static final Map<Short,String> postalCountries = new HashMap<Short,String>();
    public static final Map<Short,Integer> phoneCountries = new HashMap<Short,Integer>();
    
    static {
		postalCountries.put((short)0, "N/A");
		postalCountries.put((short)4, "Afghanistan [AF]");
		postalCountries.put((short)8, "Albania [AL]");
		postalCountries.put((short)12, "Algeria [DZ]");
		postalCountries.put((short)16, "American Samoa [AS]");
		postalCountries.put((short)20, "Andorra [AD]");
		postalCountries.put((short)24, "Angola [AO]");
		postalCountries.put((short)660, "Anguilla [AI]");
		postalCountries.put((short)10, "Antarctica [AQ]");
		postalCountries.put((short)28, "Antigua and Barbuda [AG]");
		postalCountries.put((short)32, "Argentina [AR]");
		postalCountries.put((short)51, "Armenia [AM]");
		postalCountries.put((short)533, "Aruba [AW]");
		postalCountries.put((short)36, "Australia [AU]");
		postalCountries.put((short)40, "Austria [AT]");
		postalCountries.put((short)31, "Azerbaijan [AZ]");
		postalCountries.put((short)44, "Bahamas [BS]");
		postalCountries.put((short)48, "Bahrain [BH]");
		postalCountries.put((short)50, "Bangladesh [BD]");
		postalCountries.put((short)52, "Barbados [BB]");
		postalCountries.put((short)112, "Belarus [BY]");
		postalCountries.put((short)56, "Belgium [BE]");
		postalCountries.put((short)84, "Belize [BZ]");
		postalCountries.put((short)204, "Benin [BJ]");
		postalCountries.put((short)60, "Bermuda [BM]");
		postalCountries.put((short)64, "Bhutan [BT]");
		postalCountries.put((short)68, "Bolivia [BO]");
		postalCountries.put((short)70, "Bosnia and Herzegovina [BA]");
		postalCountries.put((short)72, "Botswana [BW]");
		postalCountries.put((short)74, "Bouvet Island [BV]");
		postalCountries.put((short)76, "Brazil [BR]");
		postalCountries.put((short)86, "British Indian Ocean Territory [IO]");
		postalCountries.put((short)96, "Brunei Darussalam [BN]");
		postalCountries.put((short)100, "Bulgaria [BG]");
		postalCountries.put((short)854, "Burkina Faso [BF]");
		postalCountries.put((short)108, "Burundi [BI]");
		postalCountries.put((short)116, "Cambodia [KH]");
		postalCountries.put((short)120, "Cameroon [CM]");
		postalCountries.put((short)124, "Canada [CA]");
		postalCountries.put((short)132, "Cape Verde [CV]");
		postalCountries.put((short)136, "Cayman Islands [KY]");
		postalCountries.put((short)140, "Central African Republic [CF]");
		postalCountries.put((short)148, "Chad [TD]");
		postalCountries.put((short)152, "Chile [CL]");
		postalCountries.put((short)156, "China [CN]");
		postalCountries.put((short)162, "Christmas Island [CX]");
		postalCountries.put((short)166, "Cocos (Keeling) Islands [CC]");
		postalCountries.put((short)170, "Colombia [CO]");
		postalCountries.put((short)174, "Comoros [KM]");
		postalCountries.put((short)178, "Congo [CG]");
		postalCountries.put((short)180, "Congo, The Democratic Replublic of the [CD]");
		postalCountries.put((short)184, "Cook Islands [CK]");
		postalCountries.put((short)188, "Costa Rica [CR]");
		postalCountries.put((short)384, "Côte d'Ivoire [CI]");
		postalCountries.put((short)191, "Croatia [HR]");
		postalCountries.put((short)192, "Cuba [CU]");
		postalCountries.put((short)196, "Cyprus [CY]");
		postalCountries.put((short)203, "Czech Republic [CZ]");
		postalCountries.put((short)208, "Denmark [DK]");
		postalCountries.put((short)262, "Djibouti [DJ]");
		postalCountries.put((short)212, "Dominica [DM]");
		postalCountries.put((short)214, "Dominican Republic [DO]");
		postalCountries.put((short)626, "East Timor [TP]");
		postalCountries.put((short)218, "Ecuador [EC]");
		postalCountries.put((short)818, "Egypt [EG]");
		postalCountries.put((short)222, "El Salvador [SV]");
		postalCountries.put((short)226, "Equatorial Guinea [GQ]");
		postalCountries.put((short)232, "Eritrea [ER]");
		postalCountries.put((short)233, "Estonia [EE]");
		postalCountries.put((short)231, "Ethiopia [ET]");
		postalCountries.put((short)238, "Falkland Islands (Islas Malvinas) [FK]");
		postalCountries.put((short)234, "Faroe Islands [FO]");
		postalCountries.put((short)242, "Fiji [FJ]");
		postalCountries.put((short)246, "Finland [FI]");
		postalCountries.put((short)249, "France [FR]");
		postalCountries.put((short)254, "French Guiana [GF]");
		postalCountries.put((short)258, "French Polynesia [PF]");
		postalCountries.put((short)260, "French Southern Territories [TF]");
		postalCountries.put((short)266, "Gabon [GA]");
		postalCountries.put((short)270, "Gambia [GM]");
		postalCountries.put((short)268, "Georgia [GE]");
		postalCountries.put((short)276, "Germany [DE]");
		postalCountries.put((short)288, "Ghana [GH]");
		postalCountries.put((short)292, "Gibraltar [GI]");
		postalCountries.put((short)300, "Greece [GR]");
		postalCountries.put((short)304, "Greenland [GL]");
		postalCountries.put((short)308, "Grenada [GD]");
		postalCountries.put((short)312, "Guadeloupe [GP]");
		postalCountries.put((short)316, "Guam [GU]");
		postalCountries.put((short)320, "Guatemala [GT]");
		postalCountries.put((short)324, "Guinea [GN]");
		postalCountries.put((short)624, "Guinea-Bissau [GW]");
		postalCountries.put((short)328, "Guyana [GY]");
		postalCountries.put((short)332, "Haiti [HT]");
		postalCountries.put((short)334, "Heard Island and McDonalds Islands [HM]");
		postalCountries.put((short)336, "Holy See (Vatican City State) [VA]");
		postalCountries.put((short)340, "Honduras [HN]");
		postalCountries.put((short)344, "Hong Kong [HK]");
		postalCountries.put((short)348, "Hungary [HU]");
		postalCountries.put((short)352, "Iceland [IS]");
		postalCountries.put((short)356, "India [IN]");
		postalCountries.put((short)360, "Indonesia [ID]");
		postalCountries.put((short)364, "Iran, Islamic Republic of [IR]");
		postalCountries.put((short)368, "Iraq [IQ]");
		postalCountries.put((short)372, "Ireland [IE]");
		postalCountries.put((short)376, "Israel [IL]");
		postalCountries.put((short)380, "Italy [IT]");
		postalCountries.put((short)388, "Jamaica [JM]");
		postalCountries.put((short)392, "Japan [JP]");
		postalCountries.put((short)400, "Jordan [JO]");
		postalCountries.put((short)398, "Kazakstan [KZ]");
		postalCountries.put((short)404, "Kenya [KE]");
		postalCountries.put((short)296, "Kiribati [KI]");
		postalCountries.put((short)408, "Korea, Democratic People's Republic of [KP]");
		postalCountries.put((short)410, "Korea, Republic of [KR]");
		postalCountries.put((short)414, "Kuwait [KW]");
		postalCountries.put((short)417, "Kyrgyzstan [KG]");
		postalCountries.put((short)418, "Lao People's Democratic Republic [LA]");
		postalCountries.put((short)428, "Latvia [LV]");
		postalCountries.put((short)422, "Lebanon [LB]");
		postalCountries.put((short)426, "Lesotho [LS]");
		postalCountries.put((short)430, "Liberia [LR]");
		postalCountries.put((short)434, "Libyan Arab Jamahiriya [LY]");
		postalCountries.put((short)438, "Liechtenstein [LI]");
		postalCountries.put((short)440, "Lithuania [LT]");
		postalCountries.put((short)442, "Luxembourg [LU]");
		postalCountries.put((short)446, "Macau [MO]");
		postalCountries.put((short)807, "Macedonia, The Former Yugoslav Republic of [MK]");
		postalCountries.put((short)450, "Madagascar [MG]");
		postalCountries.put((short)454, "Malawi [MW]");
		postalCountries.put((short)458, "Malaysia [MY]");
		postalCountries.put((short)462, "Maldives [MV]");
		postalCountries.put((short)466, "Mali [ML]");
		postalCountries.put((short)470, "Malta [MT]");
		postalCountries.put((short)584, "Marshall Islands [MH]");
		postalCountries.put((short)474, "Martinique [MQ]");
		postalCountries.put((short)478, "Mauritania [MR]");
		postalCountries.put((short)480, "Mauritius [MU]");
		postalCountries.put((short)175, "Mayotte [YT]");
		postalCountries.put((short)484, "Mexico [MX]");
		postalCountries.put((short)583, "Micronesia, Federated States of [FM]");
		postalCountries.put((short)498, "Moldova, Republic of [MD]");
		postalCountries.put((short)492, "Monaco [MC]");
		postalCountries.put((short)496, "Mongolia [MN]");
		postalCountries.put((short)500, "Montserrat [MS]");
		postalCountries.put((short)504, "Morocco [MA]");
		postalCountries.put((short)508, "Mozambique [MZ]");
		postalCountries.put((short)104, "Myanmar [MM]");
		postalCountries.put((short)516, "Namibia [NA]");
		postalCountries.put((short)520, "Nauru [NR]");
		postalCountries.put((short)524, "Nepal [NP]");
		postalCountries.put((short)528, "Netherlands [NL]");
		postalCountries.put((short)530, "Netherlands Antilles [AN]");
		postalCountries.put((short)540, "New Caledonia [NC]");
		postalCountries.put((short)554, "New Zealand [NZ]");
		postalCountries.put((short)558, "Nicaragua [NI]");
		postalCountries.put((short)562, "Niger [NE]");
		postalCountries.put((short)566, "Nigeria [NG]");
		postalCountries.put((short)570, "Niue [NU]");
		postalCountries.put((short)574, "Norfolk Island [NF]");
		postalCountries.put((short)580, "Northern Mariana Islands [MP]");
		postalCountries.put((short)578, "Norway [NO]");
		postalCountries.put((short)512, "Oman [OM]");
		postalCountries.put((short)586, "Pakistan [PK]");
		postalCountries.put((short)585, "Palau [PW]");
		postalCountries.put((short)275, "Palestinian Territory, Occupied [PS]");
		postalCountries.put((short)591, "Panama [PA]");
		postalCountries.put((short)598, "Papua New Guinea [PG]");
		postalCountries.put((short)600, "Paraguay [PY]");
		postalCountries.put((short)604, "Peru [PE]");
		postalCountries.put((short)608, "Philippines [PH]");
		postalCountries.put((short)612, "Pitcairn [PN]");
		postalCountries.put((short)616, "Poland [PL]");
		postalCountries.put((short)620, "Portugal [PT]");
		postalCountries.put((short)630, "Puerto Rico [PR]");
		postalCountries.put((short)634, "Qatar [QA]");
		postalCountries.put((short)638, "Reunion [RE]");
		postalCountries.put((short)642, "Romania [RO]");
		postalCountries.put((short)643, "Russian Federation [RU]");
		postalCountries.put((short)646, "Rwanda [RW]");
		postalCountries.put((short)654, "Saint Helena [SH]");
		postalCountries.put((short)659, "Saint Kitts and Nevis [KN]");
		postalCountries.put((short)662, "Saint Lucia [LC]");
		postalCountries.put((short)666, "Saint Pierre and Miquelon [PM]");
		postalCountries.put((short)670, "Saint Vincent and The Grenadines [VC]");
		postalCountries.put((short)882, "Samoa [WS]");
		postalCountries.put((short)674, "San Marino [SM]");
		postalCountries.put((short)678, "Sao Tome and Principe [ST]");
		postalCountries.put((short)682, "Saudia Arabia [SA]");
		postalCountries.put((short)686, "Senegal [SN]");
		postalCountries.put((short)688, "Serbia [RS]");
		postalCountries.put((short)690, "Seychelles [SC]");
		postalCountries.put((short)694, "Sierra Leone [SL]");
		postalCountries.put((short)702, "Singapore [SG]");
		postalCountries.put((short)703, "Slovakia [SK]");
		postalCountries.put((short)705, "Slovenia [SI]");
		postalCountries.put((short)90, "Solomon Islands [SB]");
		postalCountries.put((short)706, "Somalia [SO]");
		postalCountries.put((short)710, "South Africa [ZA]");
		postalCountries.put((short)239, "South Georgia and South Sandwich Islands [GS]");
		postalCountries.put((short)724, "Spain [ES]");
		postalCountries.put((short)144, "Sri Lanka [LK]");
		postalCountries.put((short)736, "Sudan [SD]");
		postalCountries.put((short)740, "Suriname [SR]");
		postalCountries.put((short)744, "Svalbard and Jan Mayen [SJ]");
		postalCountries.put((short)748, "Swaziland [SZ]");
		postalCountries.put((short)752, "Sweden [SE]");
		postalCountries.put((short)756, "Switzerland [CH]");
		postalCountries.put((short)760, "Syrian Arab Republic [SY]");
		postalCountries.put((short)158, "Taiwan, Province of China [TW]");
		postalCountries.put((short)762, "Tajikistan [TJ]");
		postalCountries.put((short)834, "Tanzania, United Republic of [TZ]");
		postalCountries.put((short)764, "Thailand [TH]");
		postalCountries.put((short)768, "Togo [TG]");
		postalCountries.put((short)772, "Tokelau [TK]");
		postalCountries.put((short)776, "Tonga [TO]");
		postalCountries.put((short)780, "Trinidad and Tobago [TT]");
		postalCountries.put((short)788, "Tunisia [TN]");
		postalCountries.put((short)792, "Turkey [TR]");
		postalCountries.put((short)795, "Turkmenistan [TM]");
		postalCountries.put((short)796, "Turks and Caicos Islands [TC]");
		postalCountries.put((short)798, "Tuvalu [TV]");
		postalCountries.put((short)800, "Uganda [UG]");
		postalCountries.put((short)804, "Ukraine [UA]");
		postalCountries.put((short)784, "United Arab Emirates [AE]");
		postalCountries.put((short)826, "United Kingdom [GB]");
		postalCountries.put((short)840, "United States of America [US]");
		postalCountries.put((short)581, "United States Minor Outlying Islands [UM]");
		postalCountries.put((short)858, "Uruguay [UY]");
		postalCountries.put((short)860, "Uzbekistan [UZ]");
		postalCountries.put((short)548, "Vanatu [VU]");
		postalCountries.put((short)862, "Venezuela [VE]");
		postalCountries.put((short)70, "Viet Nam [VN]");
		postalCountries.put((short)850, "Virgin Islands, U.S. [VI]");
		postalCountries.put((short)92, "Virgin Islands. British [VG]");
		postalCountries.put((short)876, "Wallis and Futuna [WF]");
		postalCountries.put((short)732, "Western Sahara [EH]");
		postalCountries.put((short)887, "Yemen [YE]");
		postalCountries.put((short)891, "Yugoslavia [YU]");
		postalCountries.put((short)894, "Zambia [ZM]");
		postalCountries.put((short)716, "Zimbabwe [ZW]");
		
		phoneCountries.put((short)0, 0);
		phoneCountries.put((short)4, 93);
		phoneCountries.put((short)8, 355);
		phoneCountries.put((short)12, 213);
		phoneCountries.put((short)16, 684);
		phoneCountries.put((short)20, 376);
		phoneCountries.put((short)24, 244);
		phoneCountries.put((short)660, 1);
		phoneCountries.put((short)28, 1);
		phoneCountries.put((short)32, 54);
		phoneCountries.put((short)51, 374);
		phoneCountries.put((short)533, 297);
		phoneCountries.put((short)10000, 247);
		phoneCountries.put((short)36, 61);
		phoneCountries.put((short)10001, 672);
		phoneCountries.put((short)40, 43);
		phoneCountries.put((short)31, 994);
		phoneCountries.put((short)44, 1);
		phoneCountries.put((short)48, 973);
		phoneCountries.put((short)50, 880);
		phoneCountries.put((short)52, 1);
		phoneCountries.put((short)112, 375);
		phoneCountries.put((short)56, 32);
		phoneCountries.put((short)84, 501);
		phoneCountries.put((short)204, 229);
		phoneCountries.put((short)60, 1);
		phoneCountries.put((short)64, 975);
		phoneCountries.put((short)68, 591);
		phoneCountries.put((short)70, 387);
		phoneCountries.put((short)72, 267);
		phoneCountries.put((short)76, 55);
		phoneCountries.put((short)96, 673);
		phoneCountries.put((short)100, 359);
		phoneCountries.put((short)854, 226);
		phoneCountries.put((short)108, 257);
		phoneCountries.put((short)116, 855);
		phoneCountries.put((short)120, 237);
		phoneCountries.put((short)124, 1);
		phoneCountries.put((short)132, 238);
		phoneCountries.put((short)136, 1);
		phoneCountries.put((short)140, 236);
		phoneCountries.put((short)148, 235);
		phoneCountries.put((short)152, 56);
		phoneCountries.put((short)156, 86);
		phoneCountries.put((short)170, 57);
		phoneCountries.put((short)174, 269);
		phoneCountries.put((short)178, 242);
		phoneCountries.put((short)180, 243);
		phoneCountries.put((short)184, 682);
		phoneCountries.put((short)188, 506);
		phoneCountries.put((short)384, 225);
		phoneCountries.put((short)191, 385);
		phoneCountries.put((short)192, 53);
		phoneCountries.put((short)196, 357);
		phoneCountries.put((short)203, 420);
		phoneCountries.put((short)208, 45);
		phoneCountries.put((short)10002, 246);
		phoneCountries.put((short)262, 253);
		phoneCountries.put((short)212, 1);
		phoneCountries.put((short)214, 1);
		phoneCountries.put((short)626, 670);
		phoneCountries.put((short)218, 593);
		phoneCountries.put((short)818, 20);
		phoneCountries.put((short)222, 503);
		phoneCountries.put((short)226, 240);
		phoneCountries.put((short)232, 291);
		phoneCountries.put((short)233, 372);
		phoneCountries.put((short)231, 251);
		phoneCountries.put((short)238, 500);
		phoneCountries.put((short)234, 298);
		phoneCountries.put((short)242, 679);
		phoneCountries.put((short)246, 358);
		phoneCountries.put((short)249, 33);
		phoneCountries.put((short)254, 594);
		phoneCountries.put((short)258, 689);
		phoneCountries.put((short)266, 241);
		phoneCountries.put((short)270, 220);
		phoneCountries.put((short)268, 995);
		phoneCountries.put((short)276, 49);
		phoneCountries.put((short)288, 233);
		phoneCountries.put((short)292, 350);
		phoneCountries.put((short)10003, 881);
		phoneCountries.put((short)300, 30);
		phoneCountries.put((short)304, 299);
		phoneCountries.put((short)308, 1);
		phoneCountries.put((short)10004, 388);
		phoneCountries.put((short)312, 590);
		phoneCountries.put((short)316, 1);
		phoneCountries.put((short)320, 502);
		phoneCountries.put((short)324, 224);
		phoneCountries.put((short)624, 245);
		phoneCountries.put((short)328, 592);
		phoneCountries.put((short)332, 509);
		phoneCountries.put((short)336, 396);
		phoneCountries.put((short)340, 504);
		phoneCountries.put((short)344, 852);
		phoneCountries.put((short)348, 36);
		phoneCountries.put((short)352, 354);
		phoneCountries.put((short)356, 91);
		phoneCountries.put((short)360, 62);
		phoneCountries.put((short)10005, 871);
		phoneCountries.put((short)10006, 874);
		phoneCountries.put((short)10007, 873);
		phoneCountries.put((short)10008, 872);
		phoneCountries.put((short)10009, 870);
		phoneCountries.put((short)10010, 800);
		phoneCountries.put((short)10011, 882);
		phoneCountries.put((short)364, 98);
		phoneCountries.put((short)368, 964);
		phoneCountries.put((short)372, 353);
		phoneCountries.put((short)376, 972);
		phoneCountries.put((short)380, 39);
		phoneCountries.put((short)388, 1);
		phoneCountries.put((short)392, 81);
		phoneCountries.put((short)400, 962);
		phoneCountries.put((short)398, 7);
		phoneCountries.put((short)404, 254);
		phoneCountries.put((short)296, 686);
		phoneCountries.put((short)408, 850);
		phoneCountries.put((short)410, 82);
		phoneCountries.put((short)414, 965);
		phoneCountries.put((short)417, 996);
		phoneCountries.put((short)418, 856);
		phoneCountries.put((short)428, 371);
		phoneCountries.put((short)422, 961);
		phoneCountries.put((short)426, 266);
		phoneCountries.put((short)430, 231);
		phoneCountries.put((short)434, 218);
		phoneCountries.put((short)438, 423);
		phoneCountries.put((short)440, 370);
		phoneCountries.put((short)442, 352);
		phoneCountries.put((short)446, 853);
		phoneCountries.put((short)807, 389);
		phoneCountries.put((short)454, 265);
		phoneCountries.put((short)458, 60);
		phoneCountries.put((short)462, 960);
		phoneCountries.put((short)466, 223);
		phoneCountries.put((short)470, 356);
		phoneCountries.put((short)584, 692);
		phoneCountries.put((short)474, 596);
		phoneCountries.put((short)478, 222);
		phoneCountries.put((short)480, 230);
		phoneCountries.put((short)175, 269);
		phoneCountries.put((short)484, 52);
		phoneCountries.put((short)583, 691);
		phoneCountries.put((short)498, 373);
		phoneCountries.put((short)492, 377);
		phoneCountries.put((short)496, 976);
		phoneCountries.put((short)500, 1);
		phoneCountries.put((short)504, 212);
		phoneCountries.put((short)508, 258);
		phoneCountries.put((short)104, 95);
		phoneCountries.put((short)516, 264);
		phoneCountries.put((short)520, 674);
		phoneCountries.put((short)524, 977);
		phoneCountries.put((short)528, 31);
		phoneCountries.put((short)530, 599);
		phoneCountries.put((short)540, 687);
		phoneCountries.put((short)554, 64);
		phoneCountries.put((short)558, 505);
		phoneCountries.put((short)562, 227);
		phoneCountries.put((short)566, 234);
		phoneCountries.put((short)570, 683);
		phoneCountries.put((short)580, 1);
		phoneCountries.put((short)578, 47);
		phoneCountries.put((short)512, 968);
		phoneCountries.put((short)586, 92);
		phoneCountries.put((short)585, 680);
		phoneCountries.put((short)275, 970);
		phoneCountries.put((short)591, 507);
		phoneCountries.put((short)598, 675);
		phoneCountries.put((short)600, 595);
		phoneCountries.put((short)604, 51);
		phoneCountries.put((short)608, 63);
		phoneCountries.put((short)616, 48);
		phoneCountries.put((short)620, 351);
		phoneCountries.put((short)630, 1);
		phoneCountries.put((short)634, 974);
		phoneCountries.put((short)638, 262);
		phoneCountries.put((short)10012, 0);
		phoneCountries.put((short)10013, 875);
		phoneCountries.put((short)10014, 876);
		phoneCountries.put((short)10015, 877);
		phoneCountries.put((short)10016, 969);
		phoneCountries.put((short)10017, 878);
		phoneCountries.put((short)10018, 888);
		phoneCountries.put((short)10019, 808);
		phoneCountries.put((short)10020, 79);
		phoneCountries.put((short)10021, 979);
		phoneCountries.put((short)642, 40);
		phoneCountries.put((short)643, 7);
		phoneCountries.put((short)646, 250);
		phoneCountries.put((short)654, 290);
		phoneCountries.put((short)659, 1);
		phoneCountries.put((short)662, 1);
		phoneCountries.put((short)666, 508);
		phoneCountries.put((short)670, 1);
		phoneCountries.put((short)882, 685);
		phoneCountries.put((short)674, 378);
		phoneCountries.put((short)678, 239);
		phoneCountries.put((short)682, 966);
		phoneCountries.put((short)686, 221);
		phoneCountries.put((short)690, 248);
		phoneCountries.put((short)694, 232);
		phoneCountries.put((short)702, 65);
		phoneCountries.put((short)703, 421);
		phoneCountries.put((short)705, 386);
		phoneCountries.put((short)90, 677);
		phoneCountries.put((short)706, 252);
		phoneCountries.put((short)710, 27);
		phoneCountries.put((short)239, 0);
		phoneCountries.put((short)724, 34);
		phoneCountries.put((short)144, 94);
		phoneCountries.put((short)736, 249);
		phoneCountries.put((short)740, 597);
		phoneCountries.put((short)748, 268);
		phoneCountries.put((short)752, 46);
		phoneCountries.put((short)756, 41);
		phoneCountries.put((short)760, 963);
		phoneCountries.put((short)158, 886);
		phoneCountries.put((short)762, 992);
		phoneCountries.put((short)834, 255);
		phoneCountries.put((short)764, 66);
		phoneCountries.put((short)768, 228);
		phoneCountries.put((short)772, 690);
		phoneCountries.put((short)776, 676);
		phoneCountries.put((short)10022, 991);
		phoneCountries.put((short)780, 1);
		phoneCountries.put((short)788, 216);
		phoneCountries.put((short)792, 90);
		phoneCountries.put((short)795, 993);
		phoneCountries.put((short)796, 1);
		phoneCountries.put((short)798, 688);
		phoneCountries.put((short)800, 256);
		phoneCountries.put((short)804, 380);
		phoneCountries.put((short)784, 971);
		phoneCountries.put((short)826, 44);
		phoneCountries.put((short)840, 1);
		phoneCountries.put((short)858, 598);
		phoneCountries.put((short)860, 998);
		phoneCountries.put((short)548, 678);
		phoneCountries.put((short)862, 58);
		phoneCountries.put((short)704, 84);
		phoneCountries.put((short)850, 1);
		phoneCountries.put((short)92, 1);
		phoneCountries.put((short)876, 681);
		phoneCountries.put((short)887, 967);
		phoneCountries.put((short)891, 381);
		phoneCountries.put((short)894, 260);
		phoneCountries.put((short)716, 263);
			
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

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Addresses.java,v 1.31 2009/04/21 14:29:06 wfro Exp $
 * Description: Addresses
 * Revision:    $Revision: 1.31 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 14:29:06 $
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
        short countryCode
    ) throws ServiceException {
    	return postalCountries.get(Integer.valueOf(countryCode));
    }
    
    //-----------------------------------------------------------------------
    public short mapToPostalCountryCode(
        String country
    ) throws ServiceException {
    	for(Entry<Integer,String> entry: postalCountries.entrySet()) {
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
    	for(Entry<Integer,Integer> entry: phoneCountries.entrySet()) {
    		if(entry.getValue() == prefix) {
    			return entry.getKey().shortValue();
    		}
    	}
    	return 0;
    }
    
    //-------------------------------------------------------------------------
    public void updatePhoneNumber(
    	org.opencrx.kernel.address1.jmi1.PhoneNumberAddressable object
    ) throws ServiceException {
        String phoneNumberFull = object.getPhoneNumberFull();
        boolean automaticParsing = object.isAutomaticParsing();
        if(
        	automaticParsing &&
            (phoneNumberFull != null)
        ) {
            // assuming the phone number format +nn (nnn) nnn-nnnn x nnn
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
                    Integer code = this.mapToPhoneCountryCode(Short.valueOf(countryCode));
                    countryCode = code == null ? 
                    	"0" : 
                    	code.toString();
                }
                String areaCode = parts.get(1).toString().trim();
                String localNumber = parts.get(2).toString().trim();
                String extension = parts.size() >= 4 ? parts.get(3).toString().trim() : "";
                object.setPhoneCountryPrefix(Short.valueOf(countryCode));
                object.setPhoneCityArea(areaCode);
                object.setPhoneLocalNumber(localNumber);
                object.setPhoneExtension(extension);
            }
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

    public static final Map<Integer,String> postalCountries = new HashMap<Integer,String>();
    public static final Map<Integer,Integer> phoneCountries = new HashMap<Integer,Integer>();
    
    static {
		postalCountries.put(0, "N/A");
		postalCountries.put(4, "Afghanistan [AF]");
		postalCountries.put(8, "Albania [AL]");
		postalCountries.put(12, "Algeria [DZ]");
		postalCountries.put(16, "American Samoa [AS]");
		postalCountries.put(20, "Andorra [AD]");
		postalCountries.put(24, "Angola [AO]");
		postalCountries.put(660, "Anguilla [AI]");
		postalCountries.put(10, "Antarctica [AQ]");
		postalCountries.put(28, "Antigua and Barbuda [AG]");
		postalCountries.put(32, "Argentina [AR]");
		postalCountries.put(51, "Armenia [AM]");
		postalCountries.put(533, "Aruba [AW]");
		postalCountries.put(36, "Australia [AU]");
		postalCountries.put(40, "Austria [AT]");
		postalCountries.put(31, "Azerbaijan [AZ]");
		postalCountries.put(44, "Bahamas [BS]");
		postalCountries.put(48, "Bahrain [BH]");
		postalCountries.put(50, "Bangladesh [BD]");
		postalCountries.put(52, "Barbados [BB]");
		postalCountries.put(112, "Belarus [BY]");
		postalCountries.put(56, "Belgium [BE]");
		postalCountries.put(84, "Belize [BZ]");
		postalCountries.put(204, "Benin [BJ]");
		postalCountries.put(60, "Bermuda [BM]");
		postalCountries.put(64, "Bhutan [BT]");
		postalCountries.put(68, "Bolivia [BO]");
		postalCountries.put(70, "Bosnia and Herzegovina [BA]");
		postalCountries.put(72, "Botswana [BW]");
		postalCountries.put(74, "Bouvet Island [BV]");
		postalCountries.put(76, "Brazil [BR]");
		postalCountries.put(86, "British Indian Ocean Territory [IO]");
		postalCountries.put(96, "Brunei Darussalam [BN]");
		postalCountries.put(100, "Bulgaria [BG]");
		postalCountries.put(854, "Burkina Faso [BF]");
		postalCountries.put(108, "Burundi [BI]");
		postalCountries.put(116, "Cambodia [KH]");
		postalCountries.put(120, "Cameroon [CM]");
		postalCountries.put(124, "Canada [CA]");
		postalCountries.put(132, "Cape Verde [CV]");
		postalCountries.put(136, "Cayman Islands [KY]");
		postalCountries.put(140, "Central African Republic [CF]");
		postalCountries.put(148, "Chad [TD]");
		postalCountries.put(152, "Chile [CL]");
		postalCountries.put(156, "China [CN]");
		postalCountries.put(162, "Christmas Island [CX]");
		postalCountries.put(166, "Cocos (Keeling) Islands [CC]");
		postalCountries.put(170, "Colombia [CO]");
		postalCountries.put(174, "Comoros [KM]");
		postalCountries.put(178, "Congo [CG]");
		postalCountries.put(180, "Congo, The Democratic Replublic of the [CD]");
		postalCountries.put(184, "Cook Islands [CK]");
		postalCountries.put(188, "Costa Rica [CR]");
		postalCountries.put(384, "Côte d'Ivoire [CI]");
		postalCountries.put(191, "Croatia [HR]");
		postalCountries.put(192, "Cuba [CU]");
		postalCountries.put(196, "Cyprus [CY]");
		postalCountries.put(203, "Czech Republic [CZ]");
		postalCountries.put(208, "Denmark [DK]");
		postalCountries.put(262, "Djibouti [DJ]");
		postalCountries.put(212, "Dominica [DM]");
		postalCountries.put(214, "Dominican Republic [DO]");
		postalCountries.put(626, "East Timor [TP]");
		postalCountries.put(218, "Ecuador [EC]");
		postalCountries.put(818, "Egypt [EG]");
		postalCountries.put(222, "El Salvador [SV]");
		postalCountries.put(226, "Equatorial Guinea [GQ]");
		postalCountries.put(232, "Eritrea [ER]");
		postalCountries.put(233, "Estonia [EE]");
		postalCountries.put(231, "Ethiopia [ET]");
		postalCountries.put(238, "Falkland Islands (Islas Malvinas) [FK]");
		postalCountries.put(234, "Faroe Islands [FO]");
		postalCountries.put(242, "Fiji [FJ]");
		postalCountries.put(246, "Finland [FI]");
		postalCountries.put(249, "France [FR]");
		postalCountries.put(254, "French Guiana [GF]");
		postalCountries.put(258, "French Polynesia [PF]");
		postalCountries.put(260, "French Southern Territories [TF]");
		postalCountries.put(266, "Gabon [GA]");
		postalCountries.put(270, "Gambia [GM]");
		postalCountries.put(268, "Georgia [GE]");
		postalCountries.put(276, "Germany [DE]");
		postalCountries.put(288, "Ghana [GH]");
		postalCountries.put(292, "Gibraltar [GI]");
		postalCountries.put(300, "Greece [GR]");
		postalCountries.put(304, "Greenland [GL]");
		postalCountries.put(308, "Grenada [GD]");
		postalCountries.put(312, "Guadeloupe [GP]");
		postalCountries.put(316, "Guam [GU]");
		postalCountries.put(320, "Guatemala [GT]");
		postalCountries.put(324, "Guinea [GN]");
		postalCountries.put(624, "Guinea-Bissau [GW]");
		postalCountries.put(328, "Guyana [GY]");
		postalCountries.put(332, "Haiti [HT]");
		postalCountries.put(334, "Heard Island and McDonalds Islands [HM]");
		postalCountries.put(336, "Holy See (Vatican City State) [VA]");
		postalCountries.put(340, "Honduras [HN]");
		postalCountries.put(344, "Hong Kong [HK]");
		postalCountries.put(348, "Hungary [HU]");
		postalCountries.put(352, "Iceland [IS]");
		postalCountries.put(356, "India [IN]");
		postalCountries.put(360, "Indonesia [ID]");
		postalCountries.put(364, "Iran, Islamic Republic of [IR]");
		postalCountries.put(368, "Iraq [IQ]");
		postalCountries.put(372, "Ireland [IE]");
		postalCountries.put(376, "Israel [IL]");
		postalCountries.put(380, "Italy [IT]");
		postalCountries.put(388, "Jamaica [JM]");
		postalCountries.put(392, "Japan [JP]");
		postalCountries.put(400, "Jordan [JO]");
		postalCountries.put(398, "Kazakstan [KZ]");
		postalCountries.put(404, "Kenya [KE]");
		postalCountries.put(296, "Kiribati [KI]");
		postalCountries.put(408, "Korea, Democratic People's Republic of [KP]");
		postalCountries.put(410, "Korea, Republic of [KR]");
		postalCountries.put(414, "Kuwait [KW]");
		postalCountries.put(417, "Kyrgyzstan [KG]");
		postalCountries.put(418, "Lao People's Democratic Republic [LA]");
		postalCountries.put(428, "Latvia [LV]");
		postalCountries.put(422, "Lebanon [LB]");
		postalCountries.put(426, "Lesotho [LS]");
		postalCountries.put(430, "Liberia [LR]");
		postalCountries.put(434, "Libyan Arab Jamahiriya [LY]");
		postalCountries.put(438, "Liechtenstein [LI]");
		postalCountries.put(440, "Lithuania [LT]");
		postalCountries.put(442, "Luxembourg [LU]");
		postalCountries.put(446, "Macau [MO]");
		postalCountries.put(807, "Macedonia, The Former Yugoslav Republic of [MK]");
		postalCountries.put(450, "Madagascar [MG]");
		postalCountries.put(454, "Malawi [MW]");
		postalCountries.put(458, "Malaysia [MY]");
		postalCountries.put(462, "Maldives [MV]");
		postalCountries.put(466, "Mali [ML]");
		postalCountries.put(470, "Malta [MT]");
		postalCountries.put(584, "Marshall Islands [MH]");
		postalCountries.put(474, "Martinique [MQ]");
		postalCountries.put(478, "Mauritania [MR]");
		postalCountries.put(480, "Mauritius [MU]");
		postalCountries.put(175, "Mayotte [YT]");
		postalCountries.put(484, "Mexico [MX]");
		postalCountries.put(583, "Micronesia, Federated States of [FM]");
		postalCountries.put(498, "Moldova, Republic of [MD]");
		postalCountries.put(492, "Monaco [MC]");
		postalCountries.put(496, "Mongolia [MN]");
		postalCountries.put(500, "Montserrat [MS]");
		postalCountries.put(504, "Morocco [MA]");
		postalCountries.put(508, "Mozambique [MZ]");
		postalCountries.put(104, "Myanmar [MM]");
		postalCountries.put(516, "Namibia [NA]");
		postalCountries.put(520, "Nauru [NR]");
		postalCountries.put(524, "Nepal [NP]");
		postalCountries.put(528, "Netherlands [NL]");
		postalCountries.put(530, "Netherlands Antilles [AN]");
		postalCountries.put(540, "New Caledonia [NC]");
		postalCountries.put(554, "New Zealand [NZ]");
		postalCountries.put(558, "Nicaragua [NI]");
		postalCountries.put(562, "Niger [NE]");
		postalCountries.put(566, "Nigeria [NG]");
		postalCountries.put(570, "Niue [NU]");
		postalCountries.put(574, "Norfolk Island [NF]");
		postalCountries.put(580, "Northern Mariana Islands [MP]");
		postalCountries.put(578, "Norway [NO]");
		postalCountries.put(512, "Oman [OM]");
		postalCountries.put(586, "Pakistan [PK]");
		postalCountries.put(585, "Palau [PW]");
		postalCountries.put(275, "Palestinian Territory, Occupied [PS]");
		postalCountries.put(591, "Panama [PA]");
		postalCountries.put(598, "Papua New Guinea [PG]");
		postalCountries.put(600, "Paraguay [PY]");
		postalCountries.put(604, "Peru [PE]");
		postalCountries.put(608, "Philippines [PH]");
		postalCountries.put(612, "Pitcairn [PN]");
		postalCountries.put(616, "Poland [PL]");
		postalCountries.put(620, "Portugal [PT]");
		postalCountries.put(630, "Puerto Rico [PR]");
		postalCountries.put(634, "Qatar [QA]");
		postalCountries.put(638, "Reunion [RE]");
		postalCountries.put(642, "Romania [RO]");
		postalCountries.put(643, "Russian Federation [RU]");
		postalCountries.put(646, "Rwanda [RW]");
		postalCountries.put(654, "Saint Helena [SH]");
		postalCountries.put(659, "Saint Kitts and Nevis [KN]");
		postalCountries.put(662, "Saint Lucia [LC]");
		postalCountries.put(666, "Saint Pierre and Miquelon [PM]");
		postalCountries.put(670, "Saint Vincent and The Grenadines [VC]");
		postalCountries.put(882, "Samoa [WS]");
		postalCountries.put(674, "San Marino [SM]");
		postalCountries.put(678, "Sao Tome and Principe [ST]");
		postalCountries.put(682, "Saudia Arabia [SA]");
		postalCountries.put(686, "Senegal [SN]");
		postalCountries.put(688, "Serbia [RS]");
		postalCountries.put(690, "Seychelles [SC]");
		postalCountries.put(694, "Sierra Leone [SL]");
		postalCountries.put(702, "Singapore [SG]");
		postalCountries.put(703, "Slovakia [SK]");
		postalCountries.put(705, "Slovenia [SI]");
		postalCountries.put(90, "Solomon Islands [SB]");
		postalCountries.put(706, "Somalia [SO]");
		postalCountries.put(710, "South Africa [ZA]");
		postalCountries.put(239, "South Georgia and South Sandwich Islands [GS]");
		postalCountries.put(724, "Spain [ES]");
		postalCountries.put(144, "Sri Lanka [LK]");
		postalCountries.put(736, "Sudan [SD]");
		postalCountries.put(740, "Suriname [SR]");
		postalCountries.put(744, "Svalbard and Jan Mayen [SJ]");
		postalCountries.put(748, "Swaziland [SZ]");
		postalCountries.put(752, "Sweden [SE]");
		postalCountries.put(756, "Switzerland [CH]");
		postalCountries.put(760, "Syrian Arab Republic [SY]");
		postalCountries.put(158, "Taiwan, Province of China [TW]");
		postalCountries.put(762, "Tajikistan [TJ]");
		postalCountries.put(834, "Tanzania, United Republic of [TZ]");
		postalCountries.put(764, "Thailand [TH]");
		postalCountries.put(768, "Togo [TG]");
		postalCountries.put(772, "Tokelau [TK]");
		postalCountries.put(776, "Tonga [TO]");
		postalCountries.put(780, "Trinidad and Tobago [TT]");
		postalCountries.put(788, "Tunisia [TN]");
		postalCountries.put(792, "Turkey [TR]");
		postalCountries.put(795, "Turkmenistan [TM]");
		postalCountries.put(796, "Turks and Caicos Islands [TC]");
		postalCountries.put(798, "Tuvalu [TV]");
		postalCountries.put(800, "Uganda [UG]");
		postalCountries.put(804, "Ukraine [UA]");
		postalCountries.put(784, "United Arab Emirates [AE]");
		postalCountries.put(826, "United Kingdom [GB]");
		postalCountries.put(840, "United States of America [US]");
		postalCountries.put(581, "United States Minor Outlying Islands [UM]");
		postalCountries.put(858, "Uruguay [UY]");
		postalCountries.put(860, "Uzbekistan [UZ]");
		postalCountries.put(548, "Vanatu [VU]");
		postalCountries.put(862, "Venezuela [VE]");
		postalCountries.put(70, "Viet Nam [VN]");
		postalCountries.put(850, "Virgin Islands, U.S. [VI]");
		postalCountries.put(92, "Virgin Islands. British [VG]");
		postalCountries.put(876, "Wallis and Futuna [WF]");
		postalCountries.put(732, "Western Sahara [EH]");
		postalCountries.put(887, "Yemen [YE]");
		postalCountries.put(891, "Yugoslavia [YU]");
		postalCountries.put(894, "Zambia [ZM]");
		postalCountries.put(716, "Zimbabwe [ZW]");
		
		phoneCountries.put(0, 0);
		phoneCountries.put(4, 93);
		phoneCountries.put(8, 355);
		phoneCountries.put(12, 213);
		phoneCountries.put(16, 684);
		phoneCountries.put(20, 376);
		phoneCountries.put(24, 244);
		phoneCountries.put(660, 1);
		phoneCountries.put(28, 1);
		phoneCountries.put(32, 54);
		phoneCountries.put(51, 374);
		phoneCountries.put(533, 297);
		phoneCountries.put(10000, 247);
		phoneCountries.put(36, 61);
		phoneCountries.put(10001, 672);
		phoneCountries.put(40, 43);
		phoneCountries.put(31, 994);
		phoneCountries.put(44, 1);
		phoneCountries.put(48, 973);
		phoneCountries.put(50, 880);
		phoneCountries.put(52, 1);
		phoneCountries.put(112, 375);
		phoneCountries.put(56, 32);
		phoneCountries.put(84, 501);
		phoneCountries.put(204, 229);
		phoneCountries.put(60, 1);
		phoneCountries.put(64, 975);
		phoneCountries.put(68, 591);
		phoneCountries.put(70, 387);
		phoneCountries.put(72, 267);
		phoneCountries.put(76, 55);
		phoneCountries.put(96, 673);
		phoneCountries.put(100, 359);
		phoneCountries.put(854, 226);
		phoneCountries.put(108, 257);
		phoneCountries.put(116, 855);
		phoneCountries.put(120, 237);
		phoneCountries.put(124, 1);
		phoneCountries.put(132, 238);
		phoneCountries.put(136, 1);
		phoneCountries.put(140, 236);
		phoneCountries.put(148, 235);
		phoneCountries.put(152, 56);
		phoneCountries.put(156, 86);
		phoneCountries.put(170, 57);
		phoneCountries.put(174, 269);
		phoneCountries.put(178, 242);
		phoneCountries.put(180, 243);
		phoneCountries.put(184, 682);
		phoneCountries.put(188, 506);
		phoneCountries.put(384, 225);
		phoneCountries.put(191, 385);
		phoneCountries.put(192, 53);
		phoneCountries.put(196, 357);
		phoneCountries.put(203, 420);
		phoneCountries.put(208, 45);
		phoneCountries.put(10002, 246);
		phoneCountries.put(262, 253);
		phoneCountries.put(212, 1);
		phoneCountries.put(214, 1);
		phoneCountries.put(626, 670);
		phoneCountries.put(218, 593);
		phoneCountries.put(818, 20);
		phoneCountries.put(222, 503);
		phoneCountries.put(226, 240);
		phoneCountries.put(232, 291);
		phoneCountries.put(233, 372);
		phoneCountries.put(231, 251);
		phoneCountries.put(238, 500);
		phoneCountries.put(234, 298);
		phoneCountries.put(242, 679);
		phoneCountries.put(246, 358);
		phoneCountries.put(249, 33);
		phoneCountries.put(254, 594);
		phoneCountries.put(258, 689);
		phoneCountries.put(266, 241);
		phoneCountries.put(270, 220);
		phoneCountries.put(268, 995);
		phoneCountries.put(276, 49);
		phoneCountries.put(288, 233);
		phoneCountries.put(292, 350);
		phoneCountries.put(10003, 881);
		phoneCountries.put(300, 30);
		phoneCountries.put(304, 299);
		phoneCountries.put(308, 1);
		phoneCountries.put(10004, 388);
		phoneCountries.put(312, 590);
		phoneCountries.put(316, 1);
		phoneCountries.put(320, 502);
		phoneCountries.put(324, 224);
		phoneCountries.put(624, 245);
		phoneCountries.put(328, 592);
		phoneCountries.put(332, 509);
		phoneCountries.put(336, 396);
		phoneCountries.put(340, 504);
		phoneCountries.put(344, 852);
		phoneCountries.put(348, 36);
		phoneCountries.put(352, 354);
		phoneCountries.put(356, 91);
		phoneCountries.put(360, 62);
		phoneCountries.put(10005, 871);
		phoneCountries.put(10006, 874);
		phoneCountries.put(10007, 873);
		phoneCountries.put(10008, 872);
		phoneCountries.put(10009, 870);
		phoneCountries.put(10010, 800);
		phoneCountries.put(10011, 882);
		phoneCountries.put(364, 98);
		phoneCountries.put(368, 964);
		phoneCountries.put(372, 353);
		phoneCountries.put(376, 972);
		phoneCountries.put(380, 39);
		phoneCountries.put(388, 1);
		phoneCountries.put(392, 81);
		phoneCountries.put(400, 962);
		phoneCountries.put(398, 7);
		phoneCountries.put(404, 254);
		phoneCountries.put(296, 686);
		phoneCountries.put(408, 850);
		phoneCountries.put(410, 82);
		phoneCountries.put(414, 965);
		phoneCountries.put(417, 996);
		phoneCountries.put(418, 856);
		phoneCountries.put(428, 371);
		phoneCountries.put(422, 961);
		phoneCountries.put(426, 266);
		phoneCountries.put(430, 231);
		phoneCountries.put(434, 218);
		phoneCountries.put(438, 423);
		phoneCountries.put(440, 370);
		phoneCountries.put(442, 352);
		phoneCountries.put(446, 853);
		phoneCountries.put(807, 389);
		phoneCountries.put(454, 265);
		phoneCountries.put(458, 60);
		phoneCountries.put(462, 960);
		phoneCountries.put(466, 223);
		phoneCountries.put(470, 356);
		phoneCountries.put(584, 692);
		phoneCountries.put(474, 596);
		phoneCountries.put(478, 222);
		phoneCountries.put(480, 230);
		phoneCountries.put(175, 269);
		phoneCountries.put(484, 52);
		phoneCountries.put(583, 691);
		phoneCountries.put(498, 373);
		phoneCountries.put(492, 377);
		phoneCountries.put(496, 976);
		phoneCountries.put(500, 1);
		phoneCountries.put(504, 212);
		phoneCountries.put(508, 258);
		phoneCountries.put(104, 95);
		phoneCountries.put(516, 264);
		phoneCountries.put(520, 674);
		phoneCountries.put(524, 977);
		phoneCountries.put(528, 31);
		phoneCountries.put(530, 599);
		phoneCountries.put(540, 687);
		phoneCountries.put(554, 64);
		phoneCountries.put(558, 505);
		phoneCountries.put(562, 227);
		phoneCountries.put(566, 234);
		phoneCountries.put(570, 683);
		phoneCountries.put(580, 1);
		phoneCountries.put(578, 47);
		phoneCountries.put(512, 968);
		phoneCountries.put(586, 92);
		phoneCountries.put(585, 680);
		phoneCountries.put(275, 970);
		phoneCountries.put(591, 507);
		phoneCountries.put(598, 675);
		phoneCountries.put(600, 595);
		phoneCountries.put(604, 51);
		phoneCountries.put(608, 63);
		phoneCountries.put(616, 48);
		phoneCountries.put(620, 351);
		phoneCountries.put(630, 1);
		phoneCountries.put(634, 974);
		phoneCountries.put(638, 262);
		phoneCountries.put(10012, 0);
		phoneCountries.put(10013, 875);
		phoneCountries.put(10014, 876);
		phoneCountries.put(10015, 877);
		phoneCountries.put(10016, 969);
		phoneCountries.put(10017, 878);
		phoneCountries.put(10018, 888);
		phoneCountries.put(10019, 808);
		phoneCountries.put(10020, 79);
		phoneCountries.put(10021, 979);
		phoneCountries.put(642, 40);
		phoneCountries.put(643, 7);
		phoneCountries.put(646, 250);
		phoneCountries.put(654, 290);
		phoneCountries.put(659, 1);
		phoneCountries.put(662, 1);
		phoneCountries.put(666, 508);
		phoneCountries.put(670, 1);
		phoneCountries.put(882, 685);
		phoneCountries.put(674, 378);
		phoneCountries.put(678, 239);
		phoneCountries.put(682, 966);
		phoneCountries.put(686, 221);
		phoneCountries.put(690, 248);
		phoneCountries.put(694, 232);
		phoneCountries.put(702, 65);
		phoneCountries.put(703, 421);
		phoneCountries.put(705, 386);
		phoneCountries.put(90, 677);
		phoneCountries.put(706, 252);
		phoneCountries.put(710, 27);
		phoneCountries.put(239, 0);
		phoneCountries.put(724, 34);
		phoneCountries.put(144, 94);
		phoneCountries.put(736, 249);
		phoneCountries.put(740, 597);
		phoneCountries.put(748, 268);
		phoneCountries.put(752, 46);
		phoneCountries.put(756, 41);
		phoneCountries.put(760, 963);
		phoneCountries.put(158, 886);
		phoneCountries.put(762, 992);
		phoneCountries.put(834, 255);
		phoneCountries.put(764, 66);
		phoneCountries.put(768, 228);
		phoneCountries.put(772, 690);
		phoneCountries.put(776, 676);
		phoneCountries.put(10022, 991);
		phoneCountries.put(780, 1);
		phoneCountries.put(788, 216);
		phoneCountries.put(792, 90);
		phoneCountries.put(795, 993);
		phoneCountries.put(796, 1);
		phoneCountries.put(798, 688);
		phoneCountries.put(800, 256);
		phoneCountries.put(804, 380);
		phoneCountries.put(784, 971);
		phoneCountries.put(826, 44);
		phoneCountries.put(840, 1);
		phoneCountries.put(858, 598);
		phoneCountries.put(860, 998);
		phoneCountries.put(548, 678);
		phoneCountries.put(862, 58);
		phoneCountries.put(704, 84);
		phoneCountries.put(850, 1);
		phoneCountries.put(92, 1);
		phoneCountries.put(876, 681);
		phoneCountries.put(887, 967);
		phoneCountries.put(891, 381);
		phoneCountries.put(894, 260);
		phoneCountries.put(716, 263);
			
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

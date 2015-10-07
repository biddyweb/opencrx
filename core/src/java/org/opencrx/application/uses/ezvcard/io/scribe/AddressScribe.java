package org.opencrx.application.uses.ezvcard.io.scribe;

import java.util.List;

import org.opencrx.application.uses.ezvcard.VCard;
import org.opencrx.application.uses.ezvcard.VCardDataType;
import org.opencrx.application.uses.ezvcard.VCardVersion;
import org.opencrx.application.uses.ezvcard.io.xml.XCardElement;
import org.opencrx.application.uses.ezvcard.parameter.VCardParameters;
import org.opencrx.application.uses.ezvcard.property.Address;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Marshals {@link Address} properties.
 * @author Michael Angstadt
 */
public class AddressScribe extends VCardPropertyScribe<Address> {
	public AddressScribe() {
		super(Address.class, "ADR");
	}

	@Override
	protected VCardDataType _defaultDataType(VCardVersion version) {
		return VCardDataType.TEXT;
	}

	@Override
	protected void _prepareParameters(Address property, VCardParameters copy, VCardVersion version, VCard vcard) {
		handlePrefParam(property, copy, version, vcard);

		if (version == VCardVersion.V2_1 || version == VCardVersion.V3_0) {
			//remove the LABEL parameter
			//by the time this line of code is reached, VCardWriter will have created a LABEL property from this property's LABEL parameter
			copy.removeAll("LABEL");
		}
	}

	@Override
	protected String _writeText(Address property, VCardVersion version) {
		//@formatter:off
		return structured(
			property.getPoBox(),
			property.getExtendedAddress(),
			property.getStreetAddress(),
			property.getLocality(),
			property.getRegion(),
			property.getPostalCode(),
			property.getCountry()
		);
		//@formatter:on
	}

	@Override
	protected Address _parseText(String value, VCardDataType dataType, VCardVersion version, VCardParameters parameters, List<String> warnings) {
		Address property = new Address();
		StructuredIterator it = structured(value);

		property.setPoBox(it.nextString());
		property.setExtendedAddress(it.nextString());
		property.setStreetAddress(it.nextString());
		property.setLocality(it.nextString());
		property.setRegion(it.nextString());
		property.setPostalCode(it.nextString());
		property.setCountry(it.nextString());

		return property;
	}

	@Override
	protected void _writeXml(Address property, XCardElement parent) {
		parent.append("pobox", property.getPoBox()); //Note: The XML element must always be added, even if the value is null
		parent.append("ext", property.getExtendedAddress());
		parent.append("street", property.getStreetAddress());
		parent.append("locality", property.getLocality());
		parent.append("region", property.getRegion());
		parent.append("code", property.getPostalCode());
		parent.append("country", property.getCountry());
	}

	@Override
	protected Address _parseXml(XCardElement element, VCardParameters parameters, List<String> warnings) {
		Address property = new Address();
		property.setPoBox(sanitizeXml(element, "pobox"));
		property.setExtendedAddress(sanitizeXml(element, "ext"));
		property.setStreetAddress(sanitizeXml(element, "street"));
		property.setLocality(sanitizeXml(element, "locality"));
		property.setRegion(sanitizeXml(element, "region"));
		property.setPostalCode(sanitizeXml(element, "code"));
		property.setCountry(sanitizeXml(element, "country"));
		return property;
	}

	private String sanitizeXml(XCardElement element, String name) {
		String value = element.first(name);
		return (value == null || value.length() == 0) ? null : value;
	}

}

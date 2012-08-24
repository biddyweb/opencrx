/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BulkCreateActivityWizardExtension
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.wizard;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.backend.Documents;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.generic.cci2.LocalizedFieldQuery;
import org.opencrx.kernel.generic.jmi1.LocalizedField;
import org.opencrx.kernel.generic.jmi1.LocalizedFieldContainer;
import org.opencrx.kernel.utils.ScriptUtils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.Codes;
import org.w3c.cci2.BinaryLargeObjects;

public class BulkCreateActivityWizardExtension {

	private Class<?> getScript(
		RefObject_1_0 context
	) throws ServiceException {
		if(
			this.lastRefreshAt == null ||
			System.currentTimeMillis()	> this.lastRefreshAt + REFRESH_PERIOD_MILLIS
		) {
			try {
				this.lastRefreshAt = System.currentTimeMillis();
				PersistenceManager pm = JDOHelper.getPersistenceManager(context);
				String providerName = context.refGetPath().get(2);
				String segmentName = context.refGetPath().get(4);		
				org.opencrx.kernel.document1.jmi1.Segment documentSegment =
					Documents.getInstance().getDocumentSegment(pm, providerName, segmentName);
				Document scriptDocument = Documents.getInstance().findDocument(
					BulkCreateActivityWizardExtension.class.getSimpleName() + ".script", 
					documentSegment
				);
				if(
					scriptDocument == null || 
					!(scriptDocument.getHeadRevision() instanceof MediaContent)
				) {
					return null;
				}
				MediaContent script = (MediaContent)scriptDocument.getHeadRevision();
				if(
					this.scriptLastModifiedAt == null || 
					script.getModifiedAt().compareTo(this.scriptLastModifiedAt) > 0
				) {
					this.scriptLastModifiedAt = script.getModifiedAt();
					ByteArrayOutputStream content = new ByteArrayOutputStream();
					BinaryLargeObjects.streamCopy(
						script.getContent().getContent(), 
						0L, 
						content
					);
					this.script = ScriptUtils.getClass(
						content.toString("UTF-8")
					);
				}
			} catch(Exception e) {
				throw new ServiceException(e);
			}
		}
		return this.script;
	}

	/**
	 * @param name feature name
	 * @param defaultValue
	 * @param locale
	 * @param context null or a LocalizedFieldContainer
	 * @param codes if null, no code translation is made in case the value is of type short and the feature is coded.
	 * @return place holder value
	 * @throws ServiceException
	 */
	public String getPlaceHolderValue(
		String name,
		String defaultValue,
		short locale,
		RefObject_1_0 context,
		Codes codes
	) throws ServiceException {
		// Localized field
		if(context instanceof LocalizedFieldContainer) {
			PersistenceManager pm = JDOHelper.getPersistenceManager(context);
			LocalizedFieldContainer container = (LocalizedFieldContainer)context;
			LocalizedFieldQuery query = (LocalizedFieldQuery)pm.newQuery(LocalizedField.class);
			query.name().equalTo(name);
			query.locale().equalTo(locale);
			query.orderByCreatedAt().descending();
			List<LocalizedField> localizedFields = container.getLocalizedField(query);
			if(!localizedFields.isEmpty()) {
				return localizedFields.iterator().next().getLocalizedValue();
			}
		}
		// Feature on object
		try {
			Object value = context.refGetValue(name);
			if(value != null) {
				if(codes != null && value instanceof Short && name.endsWith("Code")) {
					@SuppressWarnings("unchecked")
                    Map<Object,Object> longTexts = codes.getLongText(
						context.refClass().refMofId() + ":" + name, 
						locale, 
						true, // codeAsKey
						true // includeAll
					);
					if(longTexts != null) {
						return (String)longTexts.get(value);
					}					
				} else {
					return value.toString();
				}
			}
		} catch(Exception e) {}
		// Feature with suffix 'Code' on object
		try {
			if(codes != null) {
				String nameWithCodeSuffix = name + "Code";
				Short code = (Short)context.refGetValue(nameWithCodeSuffix);
				if(code != null) {
					@SuppressWarnings("unchecked")
	                Map<Object,Object> longTexts = codes.getLongText(
						context.refClass().refMofId() + ":" + nameWithCodeSuffix, 
						locale, 
						true, // codeAsKey
						true // includeAll
					);
					if(longTexts != null) {
						return (String)longTexts.get(code);
					}
				}
			}
		} catch(Exception e) {}
		return defaultValue;
	}

	/**
	 * @param context
	 * @param locale
	 * @param text
	 * @param defaultPlaceHolders
	 * @param codes
	 * @return
	 * @throws ServiceException
	 */
	public String replacePlaceHolders(
		RefObject_1_0 context,
		short locale,
		String text,
		Properties defaultPlaceHolders,
		Codes codes
	) throws ServiceException {
		if(text == null) return null;
		int pos = text.indexOf("${");
		while(pos >= 0) {
			int start = pos + 2;
			int end = start;
			while(text.charAt(end) != '}' && end < text.length()) {
				end++;
			}
			if(end < text.length()) {
				String placeHolderName = text.substring(start, end);
				Class<?> script = this.getScript(context);
				Method getPlaceHolderValueMethod = null;
				if(script != null) {
					try {
						getPlaceHolderValueMethod = script.getMethod(
							"get" + (Character.toUpperCase(placeHolderName.charAt(0)) + placeHolderName.substring(1)) + "Value",
							String.class, // defaultValue
							short.class, // locale
							RefObject_1_0.class, // context
							Codes.class // codes
						);
					} catch(Exception e) {}
				}
				String placeHolderValue = null;
				if(getPlaceHolderValueMethod != null) {
					try {
						placeHolderValue = (String)getPlaceHolderValueMethod.invoke(
							null, 
							defaultPlaceHolders.getProperty(placeHolderName),
							locale,
							context,
							codes
						);
					} catch(Exception e) {
						throw new ServiceException(e);
					}
				} else  {
					placeHolderValue = this.getPlaceHolderValue(
						placeHolderName,
						defaultPlaceHolders.getProperty(placeHolderName),
						locale,
						context,
						codes
					);
				}
				if(placeHolderValue == null) {
					placeHolderValue = "";
				}
				text = text.substring(0, pos) + placeHolderValue + text.substring(end + 1);
				end = pos + placeHolderValue.length();
			}
			pos = text.indexOf("${", end);
		}
		return text;
	}

	/**
	 * @param placeHolders
	 * @param text
	 */
	public void updatePlaceHolders(
		Properties placeHolders,
		String text
	) {
		if(text == null) return;
		int pos = text.indexOf("${");
		while(pos >= 0) {
			int start = pos + 2;
			int end = start;
			while(text.charAt(end) != '}' && end < text.length()) {
				end++;
			}
			if(end < text.length()) {
				String placeHolderName = text.substring(start, end);
				if(placeHolders.get(placeHolderName) == null) {
					placeHolders.put(placeHolderName, "N/A");
				}
			}
			pos = text.indexOf("${", end);
		}
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	private static final long REFRESH_PERIOD_MILLIS = 60000L;
	
	private Date scriptLastModifiedAt = null;
	private Long lastRefreshAt = null;
	private Class<?> script = null;
	
}

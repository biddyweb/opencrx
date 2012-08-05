/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SimpleMimeMessage.java,v 1.6 2010/01/22 17:51:52 wfro Exp $
 * Description: SimpleMimeMessage
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/01/22 17:51:52 $
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
package org.opencrx.application.mail.importer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.openmdx.base.exception.ServiceException;

/**
 * Helper class to provide a more simple access to the regularly used attributes
 * of a mime message for importing emails.
 */
public class SimpleMimeMessage {
  
    //-------------------------------------------------------------------------  
    /**
     * Constructor
     * 
     * @param theMessage
     *          the MimeMessage which has to be wrapped
     */
    public SimpleMimeMessage(
        MimeMessage theMessage
    ) throws MessagingException, IOException, ServiceException {
    	this.mimeMessage = theMessage;
    }

    //-------------------------------------------------------------------------  
    public MimeMessage getMimeMessage(
    ) {
    	return this.mimeMessage;
    }
    
    //-------------------------------------------------------------------------  
    /**
     * Extracts the subject of the message and cache it locally. If the message
     * contains several subject entries, these entries are concatenated using
     * the system's line separator.
     * 
     * @return the subject of the message
     */
    public String getSubject(
    ) throws MessagingException {
    	if (this.subject == null) {
    		// extract the subject
    		// if
    		// handle a mail with no subject or with more than one subject
    		// as if subject is an empty string.
    		String[] subjects = this.mimeMessage.getHeader("Subject");
    		if (subjects != null && subjects.length > 0) {
    			StringBuffer msgSubject = new StringBuffer();
    			for(int i = 0; i < subjects.length; i++) {
    				if(i > 0) msgSubject.append(" ");
    				try {
    					msgSubject.append(
    						MimeUtility.decodeText(subjects[i])
    					);
    				} 
    				catch(UnsupportedEncodingException e) {
    					msgSubject.append(
    						subjects[i]
    					);            
    				}
    			}
    			this.subject = msgSubject.toString();
    		}
    	}
    	return this.subject;
    }

    //-------------------------------------------------------------------------  
    /**
     * Checks whether the message contains binary attachments.
     * 
     * @return      true, if the message contains some binary attachments
     */
    public boolean containsNestedMessage(
    ) {
    	return this.containsNestedMessageAttachment;
    }

    //-------------------------------------------------------------------------  
    /**
     * Returns a collection of the binary attachments or null if the message
     * does not contain any.
     * 
     * @return      collection of the binary attachments or null otherwise
     */
    @SuppressWarnings("unchecked")
    public Collection getBinaryContents(
    ) {
    	ArrayList binaryContents = new ArrayList();
    	if(this.content.size() > 0) {
    		Iterator binContents = this.content.iterator();
    		while (binContents.hasNext()) {
    			MessageContent contentElem = (MessageContent) binContents.next();
    			if(!(contentElem.getContent() instanceof String)) {
    				binaryContents.add(contentElem);
    			}
    		}
    	}
    	return binaryContents.size() > 0 
    	? binaryContents 
    		: null;
    }

    //-------------------------------------------------------------------------
    public void markAsDeleted(
    ) {
    	try {
    		this.mimeMessage.setFlag(
    			Flags.Flag.DELETED,
    			true
    		);
    	}
    	catch(MessagingException e) {}
    }
  
    // -----------------------------------------------------------------------
    // Members
    // -----------------------------------------------------------------------
    private final MimeMessage mimeMessage;

    private String subject = null;
    private List<Object> content = new ArrayList<Object>();
    private boolean containsNestedMessageAttachment = false;
  
}

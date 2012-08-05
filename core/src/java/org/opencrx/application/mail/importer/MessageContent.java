/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: MessageContent.java,v 1.1 2008/08/28 15:04:23 wfro Exp $
 * Description: MessageContent
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/08/28 15:04:23 $
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.mail.internet.ContentType;

/**
 * Utility class to be able to handle non text content of email messages.
 */
public class MessageContent {

    //-------------------------------------------------------------------------
    /**
     * Constructor.
     * MessageContent instances can only be created by providing values for each
     * of it's members, no setter methods are provided.
     * 
     * @param id                The id, e.g. the filename of the message content
     * @param section           content section
     * @param mimeType          The mime type of the content
     * @param binaryContent     The content
     */
    public MessageContent (
        String id,
        String section,
        String mimeType,
        Object binaryContent
    ) {
        this.id = id == null
            ? "untitled-[" + section + "]"
            : id;
        int posCharset = mimeType.indexOf("charset=");
        this.charsetName = posCharset > 0
            ? mimeType.substring(posCharset+8)
            : null;
        this.contentType = mimeType.indexOf(";") > 0
            ? mimeType.substring(0, mimeType.indexOf(";"))
            : mimeType;
        this.content = binaryContent;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns an InputStream to be able to read the real content of the
     * MessageContent object from.
     * 
     * @return      The InputStream to read the message content from
     */
    public InputStream getInputStream(
    ) {
        if(this.content instanceof byte[]) {
            return new ByteArrayInputStream((byte[])this.content);
        }
        else if(this.content instanceof String) {
            try {
                return new ByteArrayInputStream(((String)this.content).getBytes(this.charsetName));
            }
            catch(Exception e) {}
            return new ByteArrayInputStream(((String)this.content).getBytes());                                
        }
        else if(this.content instanceof InputStream) {
            return (InputStream)this.content;
        }
        else {
            return null;
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Returns the message content as Object.
     * 
     * @return     The message content in object form
     */
    public Object getContent(
    ) {
        return this.content;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns the type of this instance of MessageContent.
     * @return     The message content's type
     */
    public String getContentType() {
        return contentType;
    }

    //-------------------------------------------------------------------------
    /**
     * Returns the id of this instance of MessageContent.
     * @return     The message content's id
     */
    public String getId() {
        return id;
    }

    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        ContentType mimeType = null;
        try {
            mimeType = new ContentType(this.contentType);
        }
        catch (Exception e) {
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("[content id '" + this.id + "', ");
        buffer.append("content type '" + this.contentType + "'");
        if(mimeType != null && (mimeType.match("text/plain") || mimeType.match("text/html"))) {
            buffer.append(", content '" + this.content + "'");
        }
        if(this.content instanceof byte[]) {
            buffer.append(", contentLength:" + ((byte[]) this.content).length);
        }
        buffer.append("]");
        return buffer.toString();
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------  
    private final String id;
    private final String contentType;
    private final String charsetName;
    private final Object content;
}

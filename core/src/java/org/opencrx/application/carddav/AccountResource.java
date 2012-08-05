/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AccountResource.java,v 1.2 2010/11/24 13:05:17 wfro Exp $
 * Description: AccountResource
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/11/24 13:05:17 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.carddav;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.VCard;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

class AccountResource extends CardDavResource {

	public AccountResource(
		RequestContext requestContext,
		Account account,
		AccountCollectionResource parent
	) {
		super(requestContext, account);
		this.parent = parent;
	}

	@Override
    public Account getObject(
    ) {
        return (Account)super.getObject();
    }

	@Override
    public boolean isCollection(
    ) {
		return false;
    }
	
	@Override 
	public String getMimeType(
	) {
		return VCard.MIME_TYPE;			
	}
	
    @Override
    public String getName(
    ) {
        return super.getName() + ".vcf";
    }

    public String getDisplayName(
    ) {
    	return this.getObject().getFullName();
    }
    
    public AccountCollectionResource getAccountCollectionResource(
    ) {
    	return this.parent;
    }
    
	@Override
    public BinaryLargeObject getContent(
    ) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter p = new PrintWriter(out);
		Account account = this.getObject();
		HttpServletRequest req = this.getRequestContext().getHttpServletRequest();
        String vcard = account.getVcard();
        if((vcard != null) && (vcard.indexOf("BEGIN:VCARD") >= 0)) {
            int start = vcard.indexOf("BEGIN:VCARD");
            int end = vcard.indexOf("END:VCARD");
            p.write(vcard.substring(start, end));
            if(vcard.indexOf("URL:") < 0) {            	
            	String url = null;
            	try {
            		url = Base.getInstance().getAccessUrl(req, "-vcard-", account);
                    p.write("URL:" + url + "\n");
            	} catch(Exception e) {}
            }                        
            p.write("END:VCARD\n");
        }
		p.close();
		return BinaryLargeObjects.valueOf(out.toByteArray());
    }

	private final AccountCollectionResource parent;
	
}
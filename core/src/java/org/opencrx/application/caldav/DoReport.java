/*
 * ====================================================================
 * Project:     openCRX/core, http://www.opencrx.org/
 * Name:        $Id: DoReport.java,v 1.14 2010/12/05 14:13:33 wfro Exp $
 * Description: DoReport
 * Revision:    $Revision: 1.14 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/05 14:13:33 $
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

package org.opencrx.application.caldav;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.WebDavStore;
import org.opencrx.application.uses.net.sf.webdav.exceptions.LockFailedException;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

public class DoReport extends org.opencrx.application.uses.net.sf.webdav.methods.DoReport {

    public DoReport(
    	WebDavStore store 
    ) {
        super(
        	store 
        );
    }

	@Override
    public void folderBody(
    	RequestContext requestContext, 
        Resource so
    ) throws IOException, LockFailedException {
		HttpServletRequest req = requestContext.getHttpServletRequest();
        BufferedReader reader = new BufferedReader(req.getReader());		
        StringBuilder request = new StringBuilder();
        String l = null;
        while((l = reader.readLine()) != null) {
        	request.append(l).append("\n");
        }		
    	Collection<Resource> resources = Collections.emptyList();
    	// Multi-Get
    	if(request.indexOf("<D:href>") > 0) {
    		resources = new ArrayList<Resource>();
    		int pos = request.indexOf("<D:href>");
    		while(pos > 0) {
    			int pos1 = request.indexOf("</D:href>", pos);
    			String href = request.substring(pos + 8, pos1);
    			if(href.startsWith(req.getContextPath())) {
    				href = href.substring(req.getContextPath().length());
    			}
    			href = URLDecoder.decode(href, "UTF-8");
    			Resource res = _store.getResourceByPath(requestContext, href);
    			if(res != null) {
    				resources.add(res);
    			}
        		pos = request.indexOf("<D:href>", pos1);
    		}
    	}
    	// Query
    	else if(so instanceof ActivityCollectionResource) {
    		resources = _store.getChildren(requestContext, so);
       	}
    	HttpServletResponse resp = requestContext.getHttpServletResponse();
        resp.setStatus(SC_MULTI_STATUS);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/xml");
        resp.setHeader("DAV", "1, 2, calendar-access");
    	PrintWriter p = resp.getWriter();
    	p.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
    	SysLog.detail("<D:multistatus>");
    	p.println("<D:multistatus xmlns:D=\"DAV:\" xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
        for(Resource resource: resources) {
        	ActivityResource activityResource = (ActivityResource)resource;
        	if(
        		(activityResource.getSyncFeedResource().getRunAs() == null) || 
        		activityResource.getObject().getIcal().indexOf("CLASS:PRIVATE") < 0
        	) {
            	String name = resource.getName();
	        	p.println("  <D:response>");
	        	p.println("    <D:href>" + this.encodeURL(resp, this.getHRef(req, req.getServletPath() + name, false)) + "</D:href>");
	        	p.println("    <D:propstat>");
	        	p.println("      <D:prop>");
	        	p.println("        <D:getetag>" + this.getETag(activityResource) + "</D:getetag>");
	        	p.print  ("        <C:calendar-data xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
	        	p.print("<![CDATA[");
	        	BinaryLargeObject content = _store.getResourceContent(requestContext, resource);
	        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        	BinaryLargeObjects.streamCopy(content.getContent(), 0L, bos);
	        	bos.close();
	        	p.print(bos.toString("UTF-8"));
	        	p.print("]]>");
                p.println("</C:calendar-data>");
	        	p.println("      </D:prop>");
	        	p.println("      <D:status>HTTP/1.1 200 OK</D:status>");
	        	p.println("    </D:propstat>");
	        	p.println("  </D:response>");
        	}
        }
    	p.println("</D:multistatus>");
    	SysLog.detail("</D:multistatus>");
    	p.flush();
    }

    protected static final int SC_MULTI_STATUS = 207;
	
}

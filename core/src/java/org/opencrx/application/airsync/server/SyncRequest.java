/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: SyncRequest.java,v 1.16 2010/04/01 09:10:21 wfro Exp $
 * Description: Sync for openCRX
 * Revision:    $Revision: 1.16 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/01 09:10:21 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.airsync.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.servlet.http.HttpServletRequest;

import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.resource.Records;

public class SyncRequest {

	private final HttpServletRequest httpServletRequest;	
	private double protocolVersion;
	private final String cmd;
	private final String attachmentName;
	private final String deviceId;
	
	public SyncRequest(
	) {
		this.httpServletRequest = null;
		this.cmd = null;
		this.attachmentName = null;
		this.deviceId = null;
	}
	
	public SyncRequest(
		HttpServletRequest httpServletRequest
	) {
		this.httpServletRequest = httpServletRequest;
		List<String> params = new ArrayList<String>();
		for(Object parameter: httpServletRequest.getParameterMap().keySet()) {
			if(parameter instanceof String) {
				params.add(parameter + "=" + httpServletRequest.getParameter((String)parameter));
			}
		}
		System.out.println("SyncRequest: " + params);
		this.cmd = httpServletRequest.getParameter("Cmd");
		this.attachmentName = httpServletRequest.getParameter("AttachmentName");
		this.deviceId = httpServletRequest.getParameter("DeviceId");
	}

	public Object getSessionAttribute(
		String name
	) {
		return this.httpServletRequest.getSession().getAttribute(name);
	}
	
	public void setSessionAttribute(
		String name,
		Object value
	) {
		this.httpServletRequest.getSession().setAttribute(name, value);
	}
	
	public File getTempDir(
	) throws ServiceException {
		if(System.getProperty("org.opencrx.airsyncdir") != null) {
			return new File(System.getProperty("org.opencrx.airsyncdir"));
		} else {
			return (File)this.httpServletRequest.getSession().getServletContext().getAttribute("javax.servlet.context.tempdir");
		}
	}
	
	public double getProtocolVersion(
	) {
		return this.protocolVersion;
	}
	
	public InputStream getInputStream(
	) throws IOException {
		return this.httpServletRequest.getInputStream();
	}

	public String getCmd() {
    	return cmd;
    }

	public String getAttachmentName() {
    	return attachmentName;
    }

	public String getDeviceId() {
    	return deviceId;
    }

	@Override
    public String toString(
    ) {
        try {
            return Records.getRecordFactory().asMappedRecord(
                DataproviderRequest.class.getName(),
                null,
                new String[]{
                	"cmd",
                	"user",
                	"deviceId",
                	"protocolVersion"
                },
                new Object[]{
                    this.cmd,
                    this.httpServletRequest == null || this.httpServletRequest.getUserPrincipal() == null ? "NA" : this.httpServletRequest.getUserPrincipal().getName(),
                    this.deviceId,
                    this.protocolVersion
                }
            ).toString();
        } 
        catch (ResourceException exception) {
            return super.toString();
        }
    }

}

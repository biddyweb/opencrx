/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: SyncServlet.java,v 1.9 2010/03/26 00:03:17 wfro Exp $
 * Description: Sync for openCRX
 * Revision:    $Revision: 1.9 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/26 00:03:17 $
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
package org.opencrx.application.airsync.server.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.airsync.server.SyncRequest;
import org.opencrx.application.airsync.server.SyncResponse;
import org.opencrx.application.airsync.server.handler.FolderSyncHandler;
import org.opencrx.application.airsync.server.handler.GetAttachmentHandler;
import org.opencrx.application.airsync.server.handler.GetItemEstimateHandler;
import org.opencrx.application.airsync.server.handler.MoveItemsHandler;
import org.opencrx.application.airsync.server.handler.PingHandler;
import org.opencrx.application.airsync.server.handler.SendMailHandler;
import org.opencrx.application.airsync.server.handler.SyncHandler;
import org.opencrx.application.airsync.server.spi.IRequestHandler;
import org.opencrx.application.airsync.server.spi.ISyncBackend;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.loading.Classes;
import org.openmdx.kernel.log.SysLog;

public class SyncServlet extends HttpServlet {

	//-----------------------------------------------------------------------
	@Override
	public void init(
	) throws ServletException {
		try {
			super.init();
			// Backend
			String backendClassName = this.getInitParameter("backendClassName") == null ?
				org.opencrx.application.airsync.server.impl.SyncBackend.class.getName() :
					this.getInitParameter("backendClassName");
			Class<ISyncBackend> backendClass = Classes.getApplicationClass(backendClassName);
			this.backend = backendClass.newInstance();
			// Provider name
			this.providerName = this.getInitParameter("provider") != null && this.getInitParameter("provider").startsWith("provider/") ? 
				this.getInitParameter("provider").substring(9) :
					DEFAULT_PROVIDER_NAME;
			// Handlers
			this.handlers = new HashMap<String, IRequestHandler>();
			this.handlers.put("FolderSync", new FolderSyncHandler(this.backend));
			this.handlers.put("Sync", new SyncHandler(this.backend));
			this.handlers.put("GetItemEstimate", new GetItemEstimateHandler(this.backend));
			this.handlers.put("Ping", new PingHandler(this.backend));
			this.handlers.put("MoveItems", new MoveItemsHandler(this.backend));
			this.handlers.put("GetAttachment", new GetAttachmentHandler(this.backend));
			this.handlers.put("SendMail", new SendMailHandler(this.backend));
			this.pmf = Utils.getPersistenceManagerFactory();
		} 
		catch(Exception e) {
			new ServiceException(e).log();
			throw new ServletException(e);
		}
		System.out.println("AirSyncServlet " + this.providerName + " is running");
	}

	//-----------------------------------------------------------------------
	protected SyncRequest getSyncRequest(
		HttpServletRequest req
	) {
		return new SyncRequest(req);
	}

	//-----------------------------------------------------------------------
	protected SyncResponse getSyncResponse(
		HttpServletResponse res
	) {
		return new SyncResponse(res);
	}
	
	//-----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	protected void logRequest(
		HttpServletRequest request, 
		String command
	) {
		SysLog.warning("Request URL", request.getRequestURL());
		SysLog.warning("Parameters", request.getParameterMap().keySet());
		for(Object parameter: request.getParameterMap().keySet()) {
			if(parameter instanceof String) {
				SysLog.warning((String)parameter, request.getParameter((String)parameter));
			}
		}
		Enumeration heads = request.getHeaderNames();
		while (heads.hasMoreElements()) {
			String h = (String) heads.nextElement();
			SysLog.warning(h + ": " + request.getHeader(h));
		}
	}

	//-----------------------------------------------------------------------
	protected void setActiveSyncHeader(
		HttpServletResponse response
	) {
		// HTTP/1.1 200 OK
		// Connection: Keep-Alive
		// Content-Length: 1069
		// Date: Mon, 01 May 2006 20:15:15 GMT
		// Content-Type: application/vnd.ms-sync.wbxml
		// Server: Microsoft-IIS/6.0
		// X-Powered-By: ASP.NET
		// X-AspNet-Version: 2.0.50727
		// MS-Server-ActiveSync: 8.0
		// Cache-Control: private
		response.setHeader("Server", "Microsoft-IIS/6.0");
		response.setHeader("MS-Server-ActiveSync", "8.1");
		response.setHeader("Cache-Control", "private");
	}

	//-----------------------------------------------------------------------
	protected IRequestHandler getHandler(
		String command
	) {
		return this.handlers.get(command);
	}

	//-----------------------------------------------------------------------
	protected String getUserId(
		HttpServletRequest request
	) {
		return request.getUserPrincipal() == null ? null : request.getUserPrincipal().getName();		
	}
		
	//-----------------------------------------------------------------------
	@Override
    protected void doOptions(
    	HttpServletRequest request, 
    	HttpServletResponse response
    ) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		this.setActiveSyncHeader(response);
		response.setHeader("MS-ASProtocolVersions", "1.0,2.0,2.1,2.5");
		response.setHeader(
			"MS-ASProtocolCommands",
			"Sync,GetAttachment,GetHierarchy,FolderSync,MoveItems,GetItemEstimate,Ping"
		);
		response.setHeader("Public", "OPTIONS,POST");
		response.setHeader("Allow", "OPTIONS,POST");
		response.setContentLength(0);
		return;
    }

	//-----------------------------------------------------------------------
	@Override
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response
	) throws ServletException, IOException {
		request.getSession(true);
		SyncRequest syncRequest = this.getSyncRequest(request);
		UserHome userHome = SyncRequestHelper.getUserHome(syncRequest);
		if(userHome == null) {
			String userId = this.getUserId(request);
			String segmentName = DEFAULT_SEGMENT_NAME;
			String userName = userId;
			int pos;
			if((pos = userId.indexOf(DOMAIN_SEPARATOR)) > 0) {
				segmentName = userId.substring(0, pos);
				userName = userId.substring(pos + DOMAIN_SEPARATOR.length());
			}
			PersistenceManager pm = this.pmf.getPersistenceManager(userName, null);
			Path homeSegmentIdentity = new Path("xri://@openmdx*org.opencrx.kernel.home1/provider/" + this.providerName + "/segment/" + segmentName);
			try {
				userHome = UserHomes.getInstance().getUserHome(userName, homeSegmentIdentity, pm);
				SyncRequestHelper.setUserHome(
					syncRequest, 
					userHome
				);
			} catch(Exception e) {
				new ServiceException(e).log();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		}
		SyncResponse syncResponse = this.getSyncResponse(response);
		String cmd = syncRequest.getCmd();
		IRequestHandler handler = this.getHandler(cmd);
		if (handler == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			SysLog.warning("No handler for command", cmd);
			this.logRequest(request, cmd);
			return;
		}
		this.setActiveSyncHeader(response);
		try {
			handler.handle(
				syncRequest,
				syncResponse,
				true
			);
		} catch(Exception e) {
			new ServiceException(e).log();
		}
	}

	@Override
    protected void doGet(
    	HttpServletRequest request, 
    	HttpServletResponse response
    ) throws ServletException, IOException {
	    super.doGet(request, response);
    }

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	private static final long serialVersionUID = -4136686109306545436L;
	
	public static final String DEFAULT_PROVIDER_NAME = "CRX";
	public static final String DEFAULT_SEGMENT_NAME = "Standard";
	public static final String DOMAIN_SEPARATOR = "\\";
	
	protected Map<String, IRequestHandler> handlers;
	protected ISyncBackend backend;
	protected PersistenceManagerFactory pmf;
	protected String providerName;
	
}

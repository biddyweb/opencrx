  /*
   * ====================================================================
   * Project:     openCRX/Core, http://www.opencrx.org/
   * Name:        $Id: MailImporterServlet.java,v 1.21 2010/09/17 13:06:04 wfro Exp $
   * Description: MailImporterServlet
   * Revision:    $Revision: 1.21 $
   * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
   * Date:        $Date: 2010/09/17 13:06:04 $
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
package org.opencrx.application.mail.importer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.SendAlertParams;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;

/**
 * The MailImporterServlet imports E-Mails from a configured Mail server
 * and folder as openCRX EMail activities. Activities are created either
 * with a default EMail activity creator or by the activity creator defined
 * by the EMail subject.
 * 
 * The subject line of the wrapper message must be of the form
 * > @<email creator name> [#<activity creator name or activity#>]  <subject>
 * The activity creator name / activity# is optional. If specified an activity is
 * created and the imported E-Mails are linked to this activity.
 */
public class MailImporterServlet 
    extends HttpServlet {

    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(
        ServletConfig config
    ) throws ServletException {

        super.init(config);        
        // initialize model repository
        try {
            Model_1Factory.getModel();
        }
        catch(Exception e) {
        	SysLog.warning("Can not initialize model repository", e);
        }
        // data connection
        try {
            this.persistenceManagerFactory = Utils.getPersistenceManagerFactory();
        }
        catch(Exception e) {
            throw new ServletException("can not get connection to data provider", e);
        }
    }

    //-----------------------------------------------------------------------
    private void notifyAdmin(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        short importance,
        String subject,
        String message,
        String[] params
    ) {
        try {
            Path adminHomeIdentity = new Path(
                "xri://@openmdx*org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName
            );
            UserHome userHome = (UserHome)pm.getObjectById(adminHomeIdentity);
            try {
                pm.currentTransaction().begin();
                message = (message == null || message.length() == 0 ? "" : message + ": ") + Arrays.asList(params);
                SendAlertParams sendAlertParams = Utils.getBasePackage(pm).createSendAlertParams(
                    message,
                    importance,
                    "Email Importer [" + providerName + "/" + segmentName + "] " + subject,
                    null,
                    null,
                    SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName
                );
                userHome.sendAlert(sendAlertParams);
                pm.currentTransaction().commit();
            }
            catch(Exception e) {
                try {
                    pm.currentTransaction().rollback();                    
                } 
                catch(Exception e0) {}
            }
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }        
    }
    
    //-----------------------------------------------------------------------
    private void importMessages(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        MimeMessage message,
        MailImporterConfig config
    ) {        
    	SysLog.info("Importing Message (" + providerName + "/" + segmentName + "): ", message);
        String messageId = "NA";
        try {
            messageId = message.getMessageID();
        } 
        catch(Exception e) {}
        boolean success = true;
        try {
    		if(!message.getSubject().startsWith("> ")) {
    			message.setSubject("> " + message.getSubject());
    		}
    		Activities.getInstance().importMimeMessage(
    			pm,
	     		providerName, 
	     		segmentName, 
	     		message, 
	     		null // derive E-Mail creator from subject line 
            );
        }
        catch(Exception e) {
        	SysLog.info(e.getMessage(), e.getCause());                        
            this.notifyAdmin(
                pm,
                providerName,
                segmentName,
                Activities.PRIORITY_NORMAL,
                "Exception occurred when importing message (" + e.getMessage() + ")",
                new ServiceException(e).toString(),
                new String[]{messageId}
            );
            success = false;
        }
        if(success && config.deleteImportedMessages()) {
    		try {
	            message.setFlag(
	            	Flags.Flag.DELETED,
	            	true
	            );
            }
            catch (MessagingException e) {}
        }
    }

    //-----------------------------------------------------------------------
    protected void importMessages(
        String providerName,
        String segmentName        
    ) throws IOException {
        PersistenceManager pm = null;
        try {
            pm = this.persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName,
                UUIDs.getGenerator().next().toString()
            );
            System.out.println(new Date().toString() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName);
            Workflows.getInstance().initWorkflows(
                pm,
                providerName,
                segmentName
            );
            ActivityProcess emailActivityProcess = Activities.getInstance().initEmailProcess(
                pm,
                providerName,
                segmentName
            );
            ActivityType emailActivityType = Activities.getInstance().initActivityType(
                org.opencrx.kernel.backend.Activities.ACTIVITY_TYPE_NAME_EMAILS,
                org.opencrx.kernel.backend.Activities.ACTIVITY_CLASS_EMAIL,
                emailActivityProcess,
                pm,
                providerName,
                segmentName
            );
            ActivityGroup emailActivityTracker = Activities.getInstance().initActivityTracker(
                org.opencrx.kernel.backend.Activities.ACTIVITY_TRACKER_NAME_EMAILS, 
                null,
                pm, 
                providerName, 
                segmentName
            );
            Activities.getInstance().initActivityCreator(
                org.opencrx.kernel.backend.Activities.ACTIVITY_CREATOR_NAME_EMAILS, 
                emailActivityType,
                Arrays.asList(emailActivityTracker),
                null,
                pm, 
                providerName, 
                segmentName
            );
            MailImporterConfig mailImporterConfig = new MailImporterConfig(
                pm,
                providerName,
                segmentName
            );
            MailStore mailStore = new MailStore(mailImporterConfig);
            mailStore.openStore();
            mailStore.openFolder(
                mailImporterConfig.getMailbox()
            );
            Message[] messages = mailStore.getMessages();
            for(int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                if(message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String messageId = "NA";
                    try {
                        messageId = mimeMessage.getMessageID();
                        this.importMessages(
                            pm,
                            providerName,
                            segmentName,
                            mimeMessage,
                            mailImporterConfig
                        );
                    }
                    catch(Exception e) {
                    	SysLog.info(e.getMessage(), e.getCause());
                        this.notifyAdmin(
                            pm,
                            providerName,
                            segmentName,
                            Activities.PRIORITY_HIGH,
                            "Import of message " + messageId + " failed (" + e.getMessage() + ")",
                            new ServiceException(e).toString(),
                            new String[]{messageId}
                        );
                    }
                }
            }
            mailStore.closeFolder();
            mailStore.closeStore();
        }
        catch (Exception e) {
            if(pm != null) {
                this.notifyAdmin(
                    pm,
                    providerName,
                    segmentName,
                    Activities.PRIORITY_HIGH,
                    "Import of messages failed (" + e.getMessage() + ")",
                    new ServiceException(e).toString(),
                    new String[]{}
                );
            }
            SysLog.warning(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": ");
            new ServiceException(e).log();
        } finally {
        	if(pm != null) {
        		pm.close();
        	}
    	}
    }

    //-----------------------------------------------------------------------
    /**
     * Process an email import request.
     * 
     * @param req the servlet request
     * @param res the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        String segmentName = req.getParameter("segment");
        String providerName = req.getParameter("provider");
        String id = providerName + "/" + segmentName;
        // run
        if(COMMAND_EXECUTE.equals(req.getPathInfo())) {
            if(!this.runningSegments.containsKey(id)) {
                try {
                    this.runningSegments.put(
                    	id,
                    	Thread.currentThread()
                    );
	                this.importMessages(
	                    providerName,
	                    segmentName
	                );
	            }
	            catch(Exception e) {
	                ServiceException e0 = new ServiceException(e);
	                SysLog.warning("Import messages failed", e0.getMessage());
	                SysLog.warning(e0.getMessage(), e0.getCause());
	            }
	            finally {
	                this.runningSegments.remove(id);
	            }
            }
        	else if(
        		!this.runningSegments.get(id).isAlive() || 
        		this.runningSegments.get(id).isInterrupted()
        	) {
            	Thread t = this.runningSegments.get(id);
        		System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": workflow " + t.getId() + " is alive=" + t.isAlive() + "; interrupted=" + t.isInterrupted() + ". Skipping execution.");
        	}	            
        }
    }

    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -7260829387268368633L;
    
    private static final String WORKFLOW_NAME = "MailImporter";
    private static final String COMMAND_EXECUTE = "/execute";
    
    private PersistenceManagerFactory persistenceManagerFactory = null;
    private final Map<String,Thread> runningSegments = new ConcurrentHashMap<String,Thread>();

}

//  --- End of File -----------------------------------------------------------

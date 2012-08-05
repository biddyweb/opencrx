  /*
   * ====================================================================
   * Project:     openCRX/Core, http://www.opencrx.org/
   * Name:        $Id: MailImporterServlet.java,v 1.8 2009/03/08 17:04:54 wfro Exp $
   * Description: MailImporterServlet
   * Revision:    $Revision: 1.8 $
   * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
   * Date:        $Date: 2009/03/08 17:04:54 $
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.SendAlertParams;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.jmi1.Media;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

/**
 * The EMailImporterServlet imports E-Mails from a configured Mail server
 * and folder as openCRX EMail activities. Activities are created either
 * with a default EMail activity creator or by the activity creator defined
 * by the EMail subject.
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
            AppLog.warning("Can not initialize model repository", e);
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
                "xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName
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
    /**
     * @param mimeMsg          The email to be imported as openCRX EMailActivity
     * @param rootPkg          The root package to be used for this request
     * @param emailActivity    The openCRX EMailActivity currently in process
     */
    private void addMedia(
        SimpleMimeMessage mimeMsg,
        PersistenceManager pm,
        EMail emailActivity
    ) throws MessagingException {
        // add attachments to email
        // if the email already contains attachments as media, only
        // those attachments not already contained in the email
        // are imported. This check is done by comparing the name
        // of the attachment with the contentNames of the media.
        if(mimeMsg.containsAttachments()) {
            Iterator contentsIter = mimeMsg.getContents().iterator();
            boolean emailContainsMedia = emailActivity.getMedia().size() > 0;
            while (contentsIter.hasNext()) {
                MessageContent content = (MessageContent)contentsIter.next();
                if (emailContainsMedia) {
                    Iterator attachments = emailActivity.getMedia().iterator();
                    while (attachments.hasNext()) {
                        Media attachment = (Media)attachments.next();
                        if(content.getId() != null && content.getId().equals(attachment.getContentName())) {
                            AppLog.trace("Attachment already linked, '"
                                + mimeMsg.getSubject() + "', "
                                + mimeMsg.getMessageID() + "', "
                                + content.getId());
                        }
                        else {                            
                            // add the attachment
                            try {
                                Activities.addMedia(
                                    pm,
                                    emailActivity,
                                    content.getContentType(),
                                    content.getId(),
                                    content.getInputStream()
                                );
                            }
                            catch(IOException e) {
                                AppLog.warning("Can not add attachment", e);
                                new ServiceException(e).log();
                                AppLog.detail(e.getMessage(), e.getCause());
                            }
                        }
                    }
                }
                else {
                    // add the attachment
                    try {
                        Activities.addMedia(
                            pm,
                            emailActivity,
                            content.getContentType(),
                            content.getId(),
                            content.getInputStream()
                        );
                    }
                    catch(IOException e) {
                        AppLog.warning("Can not add attachment", e);
                        new ServiceException(e).log();
                        AppLog.detail(e.getMessage(), e.getCause());
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Imports an inbound MimeMessage as an EMail activity.
     * 
     * @param providerName     The name of the current provider
     * @param segmentName      The name of the current segment
     * @param mimeMessage          The email to be imported as openCRX EMailActivity
     * @throws ServiceException
     */
    private void importSimpleMimeMessage(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String creatorCriteria,
        SimpleMimeMessage mimeMessage,
        boolean caseInsensitiveAddressLookup
    ) throws ServiceException {

        try {
            List activities = Activities.lookupEmailActivity(
                pm,
                providerName,
                segmentName,
                mimeMessage.getMessageID()
            );
            if (activities.isEmpty()) {
                AppLog.trace("create a new EMailActivity");
      
                ActivityCreator emailCreator = this.getEmailCreator(
                    pm,
                    providerName,
                    segmentName,
                    creatorCriteria
                );
                
                pm.currentTransaction().begin();
                
                Activity1Package activityPkg = Utils.getActivityPackage(pm);
                NewActivityParams newActParam = activityPkg.createNewActivityParams(
                    null, // description
                    null, // detailedDescription
                    null, // dueBy
                    ICalendar.ICAL_TYPE_NA, // icalType
                    mimeMessage.getSubject(),
                    mimeMessage.getPriority(),
                    null, // reportingContract
                    null, // scheduledEnd
                    null  // scheduledStart
                );
                NewActivityResult newActivityResult = emailCreator.newActivity(
                    newActParam
                );
                pm.currentTransaction().commit();
      
                // Update EMail activity
                EMail emailActivity = (EMail)pm.getObjectById(newActivityResult.getActivity().refGetPath());
                pm.currentTransaction().begin();
                String subject = mimeMessage.getSubject();                
                emailActivity.setMessageSubject(
                    subject == null ? "" : subject
                );
                String body = mimeMessage.getBody();
                emailActivity.setMessageBody(
                    body == null ? "" : body
                );
                emailActivity.getExternalLink().clear();
                emailActivity.getExternalLink().add(
                    mimeMessage.getMessageID()
                );
                emailActivity.setSendDate(
                    mimeMessage.getDate()
                );
                pm.currentTransaction().commit();
      
                // Add FROM as sender
                String fromAddress = mimeMessage.getFrom()[0];
                List addresses = Accounts.lookupEmailAddress(
                    pm,
                    providerName,
                    segmentName,
                    fromAddress,
                    caseInsensitiveAddressLookup
                );
                EMailAddress from = null;
                if (addresses.size() == 1) {
                    from = (EMailAddress) addresses.iterator().next();
                    pm.currentTransaction().begin();
                    emailActivity.setSender(from);
                    pm.currentTransaction().commit();
                } 
                else {
                    AppLog.trace("lookup " + fromAddress + " finds "
                        + addresses.size() + " addresses");
                }
                // Handle recipients
                Activities.addRecipientToEmailActivity(
                    pm,
                    providerName,
                    segmentName,
                    emailActivity,
                    mimeMessage.getRecipients(),
                    Message.RecipientType.TO,
                    caseInsensitiveAddressLookup
                );
                Activities.addRecipientToEmailActivity(
                    pm,
                    providerName,
                    segmentName,
                    emailActivity,
                    mimeMessage.getRecipients(Message.RecipientType.CC),
                    Message.RecipientType.CC,
                    caseInsensitiveAddressLookup
                );      
                // add originator and recipients to a note
                Activities.addNote(
                    pm,
                    emailActivity,
                    "Recipients",
                    Activities.getRecipientsAsNoteText(
                        pm,
                        providerName,
                        segmentName,
                        mimeMessage.getFrom(),
                        mimeMessage.getRecipients(Message.RecipientType.TO),
                        mimeMessage.getRecipients(Message.RecipientType.CC),
                        mimeMessage.getRecipients(Message.RecipientType.BCC),
                        caseInsensitiveAddressLookup
                    )
                );
      
                // add EMail headers as Note
                Activities.addNote(
                    pm,
                    emailActivity,
                    "Message-Header",
                    mimeMessage.getAllHeaderLinesAsString()
                );
                // Add attachments if some exist
                this.addMedia(
                    mimeMessage, 
                    pm, 
                    emailActivity
                );
            }
            else if (activities.size() == 1) {
              AppLog.info(
                  "Import of email message skipped, an email with this message id exists already, "
                  + mimeMessage.getMessageID() + ", " + mimeMessage.getSubject());
            }
            else {
                AppLog.info(
                    "Import of email message skipped, found "
                    + activities.size() + " email with this message id, "
                    + mimeMessage.getMessageID() + ", " + mimeMessage.getSubject());
            }
        }
        catch (Exception e) {
            this.notifyAdmin(
                pm,
                providerName,
                segmentName,
                Activities.PRIORITY_HIGH,
                "Can not create email activity",
                e.getMessage(),
                new String[]{}
            );
            AppLog.warning("Can not create email activity", e.getMessage());
            AppLog.info(e.getMessage(), e.getCause());
            throw new ServiceException(e);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Search the appropriate ActivityCreator according to the email message.
     * Currently only the default inbound email creator is active. If no
     * creator can be found a new default one is created.
     * 
     * @param rootPkg          The root package to be used for this request
     * @param providerName     The name of the current provider
     * @param segmentName      The name of the current segment
     * @param message          The email to be imported as openCRX EMailActivity
     */
    private ActivityCreator getEmailCreator(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String criteria
    ) {
        try {
            Activity1Package activityPkg = Utils.getActivityPackage(pm);
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment = Activities.getActivitySegment(
                pm, 
                providerName, 
                segmentName
            );            
            ActivityCreator emailCreator = null;
            if((criteria != null) && (criteria.length() > 0)) {
                // Try to get activity creator which's name matches the message subject
                ActivityCreatorQuery query = activityPkg.createActivityCreatorQuery();
                query.name().equalTo(
                    criteria
                );
                for(Iterator i = activitySegment.getActivityCreator(query).iterator(); i.hasNext(); ) {
                    ActivityCreator creator = (ActivityCreator)i.next();
                    if((creator.getActivityType() != null) && (creator.getActivityType().getActivityClass() == Activities.ACTIVITY_CLASS_EMAIL)) {
                        emailCreator = creator;
                        break;
                    }
                }
            }
            // If  not found get default creator for inbound email activities.
            if(emailCreator == null) {
                emailCreator = activitySegment.getActivityCreator(Activities.DEFAULT_EMAIL_CREATOR_ID);
            }
            return emailCreator;
        }
        catch(Exception e) {
            AppLog.info("Can not retrieve config from external task configuration", e.getMessage());
        }
        return null;
    }
    
    //-----------------------------------------------------------------------
    private void importNestedMessages(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        SimpleMimeMessage message,
        MailImporterConfig mailImporterConfig
    ) {        
        AppLog.info("Importing Message (" + providerName + "/" + segmentName + "): ", message);
        String messageId = "NA";
        try {
            messageId = message.getMessageID();
        } 
        catch(Exception e) {}
        if(message.containsNestedMessage()) {
            boolean successful = true;
            for(
                Iterator i = message.getBinaryContents().iterator(); 
                i.hasNext(); 
            ) {
                MessageContent content = (MessageContent)i.next();
                if(content.getContent() instanceof SimpleMimeMessage) {
                    try {
                        this.importSimpleMimeMessage(
                            pm,
                            providerName,
                            segmentName,
                            message.getSubject(),
                            (SimpleMimeMessage)content.getContent(),
                            mailImporterConfig.getCaseInsensitiveAddressLookup()
                        );
                    }
                    catch(Exception e) {
                        AppLog.info(e.getMessage(), e.getCause());                        
                        this.notifyAdmin(
                            pm,
                            providerName,
                            segmentName,
                            Activities.PRIORITY_NORMAL,
                            "Error importing message. Retrying (" + e.getMessage() + ")",
                            new ServiceException(e).toString(),
                            new String[]{messageId}
                        );
                        successful = false;
                    }
                }
                else {
                    this.notifyAdmin(
                        pm,
                        providerName,
                        segmentName,
                        Activities.PRIORITY_NORMAL,
                        "Content not of type SimpleMimeMessage. Ignoring",
                        "",
                        new String[]{
                            messageId, 
                            (content.getContent() == null ? "N/A" : content.getContent().getClass().getName())
                        }
                    );
                }
            }
            if(successful && mailImporterConfig.deleteImportedMessages()) {
                // Delete message after successful import
                message.markAsDeleted();
            }
        }
        else {
            if(mailImporterConfig.deleteImportedMessages()) {
                message.markAsDeleted();
            }
            this.notifyAdmin(
                pm,
                providerName,
                segmentName,
                Activities.PRIORITY_LOW,
                "Message does not contain nested messages",
                "",
                new String[]{messageId, "delete=" + mailImporterConfig.deleteImportedMessages()}
            );
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
            Workflows.initWorkflows(
                pm,
                providerName,
                segmentName
            );
            ActivityProcess emailActivityProcess = Activities.initEmailProcess(
                pm,
                providerName,
                segmentName
            );
            ActivityType emailActivityType = Activities.initActivityType(
                org.opencrx.kernel.backend.Activities.ACTIVITY_TYPE_NAME_EMAILS,
                org.opencrx.kernel.backend.Activities.ACTIVITY_CLASS_EMAIL,
                emailActivityProcess,
                pm,
                providerName,
                segmentName
            );
            ActivityGroup emailActivityTracker = Activities.initActivityTracker(
                org.opencrx.kernel.backend.Activities.ACTIVITY_TRACKER_NAME_EMAILS, 
                null,
                pm, 
                providerName, 
                segmentName
            );
            Activities.initActivityCreator(
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
                    MimeMessage mimeMsg = (MimeMessage) message;
                    String messageId = "NA";
                    try {
                        messageId = mimeMsg.getMessageID();
                        SimpleMimeMessage simpleMimeMessage = new SimpleMimeMessage(
                            mimeMsg, 
                            true
                        );
                        this.importNestedMessages(
                            pm,
                            providerName,
                            segmentName,
                            simpleMimeMessage,
                            mailImporterConfig
                        );
                    }
                    catch(Exception e) {
                        AppLog.info(e.getMessage(), e.getCause());
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
            AppLog.warning(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": ");
            new ServiceException(e).log();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Process an email import request.
     * 
     * @param req      The servlet request
     * @param res      The servlet response
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
        if(
            COMMAND_EXECUTE.equals(req.getPathInfo()) &&
            !this.runningSegments.contains(id)
        ) {
            try {
                this.runningSegments.add(id);
                this.importMessages(
                    providerName,
                    segmentName
                );
            }
            catch(Exception e) {
                ServiceException e0 = new ServiceException(e);
                AppLog.warning("Import messages failed", e0.getMessage());
                AppLog.warning(e0.getMessage(), e0.getCause());
            }
            finally {
                this.runningSegments.remove(id);
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
    
    private static final UUIDGenerator uuidGenerator = UUIDs.getGenerator();

    private PersistenceManagerFactory persistenceManagerFactory = null;

    private final List runningSegments = new ArrayList();

}

//  --- End of File -----------------------------------------------------------

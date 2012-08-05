/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ExportMailWorkflow.java,v 1.1 2008/08/28 15:13:31 wfro Exp $
 * Description: ExportMailWorkflow
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/08/28 15:13:31 $
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
package org.opencrx.application.mail.exporter;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefObject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.opencrx.application.mail.exporter.MailWorkflow;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.generic.jmi1.Media;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.layer.application.ByteArrayDataSource;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;

public class ExportMailWorkflow 
    extends MailWorkflow {

    //-----------------------------------------------------------------------
    @Override
    protected String setContent(
        Message message,
        Session session,
        PersistenceManager pm,
        Path targetIdentity,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map params
    ) throws ServiceException {
        String text = null;
        try {
            RefObject targetObject = null;
            try {
                targetObject = (RefObject)pm.getObjectById(targetIdentity);
            } catch(Exception e) {}
            text = this.getText(
                message,
                pm,
                targetIdentity,
                targetObject,
                wfProcessInstanceIdentity,
                userHome,
                params
            );
            // message text
            Multipart multipart = new MimeMultipart();
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(text);            
            multipart.addBodyPart(bodyPart);            

            // Add EMail activity as nested message
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.Email) {
                org.opencrx.kernel.activity1.jmi1.Email emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.Email)targetObject;
                bodyPart = new MimeBodyPart();
                bodyPart.setFileName(
                    emailActivity.getMessageSubject() + ".msg"
                );
                // nested message
                MimeMessage nestedMessage = new MimeMessage(session);
                nestedMessage.setHeader(
                    "X-Mailer", 
                    "openCRX SendMail"
                );
                // nested message subject
                nestedMessage.setSubject(
                    emailActivity.getMessageSubject()
                );
                // nested message body
                Multipart nestedMultipart = new MimeMultipart();
                BodyPart nestedBodyPart = new MimeBodyPart();
                nestedBodyPart.setText(
                    emailActivity.getMessageBody()
                );
                nestedMultipart.addBodyPart(nestedBodyPart);
                // nested message sent date
                nestedMessage.setSentDate(
                    new Date()
                );
                Activities.mapMessageRecipients(
                    emailActivity, 
                    nestedMessage
                );
                // nested message media attachments
                Collection<Media> medias = emailActivity.getMedia();
                for(Media media: medias) {
                    nestedBodyPart = new MimeBodyPart();
                    nestedBodyPart.setFileName(
                        media.getContentName()
                    );
                    nestedBodyPart.setDisposition(
                        Part.ATTACHMENT
                    );
                    DataSource dataSource = new ByteArrayDataSource(
                        media.getContent().getContent(),
                        media.getContentMimeType()
                    );
                    nestedBodyPart.setDataHandler(
                        new DataHandler(dataSource)
                    );
                    nestedMultipart.addBodyPart(nestedBodyPart);
                }
                nestedMessage.setContent(nestedMultipart);
                bodyPart.setDisposition(
                    Part.ATTACHMENT
                );
                bodyPart.setContent(
                    nestedMessage,
                    "message/rfc822"
                );
                multipart.addBodyPart(bodyPart);
            }
            message.setContent(multipart);
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }
        return text;
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    
}

//--- End of File -----------------------------------------------------------

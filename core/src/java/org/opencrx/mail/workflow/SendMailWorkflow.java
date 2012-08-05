/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SendMailWorkflow.java,v 1.7 2007/12/20 19:20:33 wfro Exp $
 * Description: SendMailWorkflow
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/20 19:20:33 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2006, CRIXP Corp., Switzerland
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
package org.opencrx.mail.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jmi.reflect.RefObject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;

import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.home1.jmi1.EmailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;

public class SendMailWorkflow 
    extends MailWorkflow {

    //-----------------------------------------------------------------------
    protected String getSubject(
        RefPackage_1_0 rootPkg,
        Path targetIdentity,
        UserHome userHome,  
        Map params
    ) throws ServiceException {
        String subject = null;
        try {
            RefObject targetObject = rootPkg.refObject(targetIdentity.toXri());
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.Email) {
                org.opencrx.kernel.activity1.jmi1.Email emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.Email)targetObject;
                subject = emailActivity.getMessageSubject();
            }
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }
        return subject == null
            ? ""
            : subject;
    }
    
    //-----------------------------------------------------------------------
    protected Address[] setRecipients(
        Message message,
        RefPackage_1_0 rootPkg,
        Path targetIdentity,
        EmailAccount eMailAccount
    ) throws ServiceException {
        List<Address> recipients = new ArrayList<Address>();
        try {
            RefObject targetObject = rootPkg.refObject(targetIdentity.toXri());
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.Email) {
                org.opencrx.kernel.activity1.jmi1.Email emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.Email)targetObject;
                recipients = Activities.mapMessageRecipients(
                    emailActivity, 
                    message
                );
            }
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }
        return recipients.toArray(new Address[recipients.size()]);
    }
    
    //-----------------------------------------------------------------------
    protected String setContent(
        Message message,
        Session session,
        RefPackage_1_0 rootPkg,
        Path targetIdentity,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map params
    ) throws ServiceException {
        String text = null;
        try {
            RefObject targetObject = rootPkg.refObject(targetIdentity.toXri());
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.Email) {
                org.opencrx.kernel.activity1.jmi1.Email emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.Email)targetObject;
                Activities.mapMessageContent(
                    emailActivity,
                    message
                );
            }
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }
        return text;
    }
    
}

//--- End of File -----------------------------------------------------------

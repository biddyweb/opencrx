/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SendMailWorkflow.java,v 1.6 2009/04/21 16:54:05 wfro Exp $
 * Description: SendMailWorkflow
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 16:54:05 $
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefObject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;

import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.home1.jmi1.EMailAccount;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

public class SendMailWorkflow 
    extends MailWorkflow {

    //-----------------------------------------------------------------------
    @Override
    protected Address[] setRecipients(
        Message message,
        PersistenceManager pm,
        Path targetIdentity,
        EMailAccount eMailAccount
    ) throws ServiceException {
        List<Address> recipients = new ArrayList<Address>();
        try {
            RefObject targetObject = (RefObject)pm.getObjectById(targetIdentity);
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
                org.opencrx.kernel.activity1.jmi1.EMail emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.EMail)targetObject;
                recipients = Activities.getInstance().mapMessageRecipients(
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
    @Override
    protected String setContent(
        Message message,
        Session session,
        PersistenceManager pm,
        Path targetIdentity,
        Path wfProcessInstanceIdentity,
        UserHome userHome,
        Map<String,Object> params
    ) throws ServiceException {
        String text = null;
        try {
            RefObject targetObject = (RefObject)pm.getObjectById(targetIdentity);
            if(targetObject instanceof org.opencrx.kernel.activity1.jmi1.EMail) {
                org.opencrx.kernel.activity1.jmi1.EMail emailActivity = 
                    (org.opencrx.kernel.activity1.jmi1.EMail)targetObject;
                Activities.getInstance().mapMessageContent(
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

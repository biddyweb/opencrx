/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: MailStore.java,v 1.1 2008/08/28 15:04:22 wfro Exp $
 * Description: MailStore
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/08/28 15:04:22 $
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

import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;

public class MailStore {

    //-------------------------------------------------------------------------
    public MailStore(
        MailImporterConfig config 
    ) {
        this.config = config;
    }

    //-------------------------------------------------------------------------
    public void openStore (
    ) throws ServiceException {
        AppLog.info("Fetching emails with configuration", this.config);
        try {
            Context initialContxt = new InitialContext();
            Session session = (Session)initialContxt.lookup("java:comp/env" + this.config.getMailServiceName());
            this.store = session.getStore();
            String protocol = store.getURLName().getProtocol();
            String port = session.getProperty("mail." + protocol + ".port");
            this.store.connect(
                session.getProperty("mail." + protocol + ".host"), 
                port == null ? -1 : Integer.valueOf(port).intValue(),                           
                session.getProperty("mail." + protocol + ".user"), 
                session.getProperty("mail." + protocol + ".password")
            );
        } 
        catch (Exception e) {
            AppLog.error("Could not get mail session", this.config.getMailServiceName());
            ServiceException e0 = new ServiceException(e);
            AppLog.error(e0.getMessage(), e0.getCause());
            throw e0;
        }
    }

    //-------------------------------------------------------------------------
    public Message[] getMessages(
    ) throws MessagingException {
        return this.folder.getMessages();
    }
    
    //-------------------------------------------------------------------------
    public void openFolder(
        String name
    ) throws ServiceException {
      try {
          this.folder = this.store.getFolder(
              name == null 
                  ? DEFAULT_FOLDERNAME 
                  : name
              );
          this.folder.open(Folder.READ_WRITE);
      }
      catch(FolderNotFoundException e) {
          AppLog.error("Could not open the specified folder '" + name + "'");
          ServiceException e0 = new ServiceException(e);
          AppLog.error(e0.getMessage(), e0.getCause());
          throw e0;
      } 
      catch(MessagingException e) {
          AppLog.error("Exception while opening folder '" + name + "'");
          ServiceException e0 = new ServiceException(e);
          AppLog.error(e0.getMessage(), e0.getCause());
          throw e0;          
      }
    }

    //-------------------------------------------------------------------------
    public void closeFolder(
    ) {
      if(this.folder != null) {
          try {
              this.folder.close(true);              
          } catch(Exception e) {}
      }
    }
  
    //-------------------------------------------------------------------------
    /**
     * Finally close the store currently in use.
     * 
     */
    public void closeStore (
    ) {
      try {
        store.close();
      } 
      catch (MessagingException e) {
          AppLog.warning("Could not clean up resources");
          System.err.println("Could not clean up after importing");
      }
    }

    // -----------------------------------------------------------------------
    // Members
    // -----------------------------------------------------------------------
    public static final String POP3_MODE = "pop3";
    public static final String IMAP_MODE = "imap";
    public static final String DEFAULT_FOLDERNAME = "INBOX";

    private Store store = null;
    private Folder folder = null;
    private MailImporterConfig config = null;

}

// --- End of File -----------------------------------------------------------
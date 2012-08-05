/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: TestEmailFetcher.java,v 1.4 2006/01/30 00:18:01 wfro Exp $
 * Description: openCRX EMailImporter, Test for EmailFetcher
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2006/01/30 00:18:01 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2005, CRIXP Corp., Switzerland
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
package org.opencrx.test.email;

import junit.framework.TestCase;

import org.opencrx.email.EmailFetcher;
import org.opencrx.email.ServerConfigHolder;
import org.opencrx.email.SimpleMimeMessage;
import org.openmdx.base.exception.ServiceException;

public class TestEmailFetcher extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
    srvConfig = new ServerConfigHolder(
      System.getProperty("mailServer", "mail.omex.ch"),
      System.getProperty("mailAccount", "user"),
      System.getProperty("mailPassword"),
      System.getProperty("mailPort"),
      System.getProperty("mailProtocol"),
      ("true".equalsIgnoreCase(System.getProperty("mailSslRequired")) ? true : false),
      ("true".equalsIgnoreCase(System.getProperty("mailDebug")) ? true : false)
    );
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testGetMessages() {
    MailFetcher emailFetcher = null;
    String mailFolderName = "INBOX";
    SimpleMimeMessage messages[] = null;
    try {
      emailFetcher = new MailFetcher(srvConfig,-1);
      emailFetcher.openStore();
      messages = emailFetcher.getNextMessages(mailFolderName, false);
      assertTrue("getMessages returned null", messages != null);
      assertTrue("getMessages found no messages", messages.length > 0);
      assertTrue("getMessages returned amount is not correct", messages.length == 4);
      System.out.println("Successfully got '" + messages.length + "' messages");
//      for (int i = 0; i < messages.length; i++) {
//        System.out.println(messages[i].toString());
//      }
    } catch (ServiceException e) {
      e.printStackTrace();
      assertTrue("Problems reading emails. Stopping.", false);
    }
  }

  /*
   * members
   */
  static private ServerConfigHolder srvConfig = null;
}

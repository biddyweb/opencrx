/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SimpleMimeMessage.java,v 1.1 2008/08/28 15:04:22 wfro Exp $
 * Description: SimpleMimeMessage
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.opencrx.kernel.backend.Activities;
import org.openmdx.application.log.AppLog;
import org.openmdx.kernel.id.UUIDs;

/**
 * Helper class to provide a more simple access to the regularly used attributes
 * of a mime message for importing emails.
 */
public class SimpleMimeMessage {
  
    //-------------------------------------------------------------------------  
    /**
     * Constructor
     * 
     * @param theMessage
     *          the MimeMessage which has to be wrapped
     */
    public SimpleMimeMessage(
        MimeMessage theMessage
    ) throws MessagingException, IOException {
        this(theMessage, false);
    }

    //-------------------------------------------------------------------------  
    /**
     * Constructor
     * If <code>initAttributes</code> is true, the attributes are initialized
     * immediately by parsing the underlying MimeMessage. Therefore although
     * the folder containing the MimeMessage may already be closed, this
     * instance already holds the derived values for the attributes.
     * 
     * @param theMessage
     *          the MimeMessage which has to be wrapped
     * @param initAttributes
     *          true if the attributes should be initialized immediately, false otherwise
     */
    public SimpleMimeMessage(
        MimeMessage theMessage,
        boolean initAttributes
    ) throws MessagingException, IOException {
        this.mimeMsg = theMessage;
        if(initAttributes) {
            this.getDate();
            this.getFrom();
            this.getMessageID();
            this.getPriority();
            this.getRecipients();
            this.getSubject();
            this.getAllHeaderLinesAsString();
            this.getMessageIdentity();
            this.getBody();
        }
    }

    //-------------------------------------------------------------------------  
    /**
     * Get all the headers for this header_name. Note that certain headers may
     * be encoded as per RFC 2047 if they contain non US-ASCII characters and
     * these should be decoded.
     * <p>
     * 
     * This implementation obtains the headers from the <code>headers</code>
     * InternetHeaders object.
     * 
     * @param name
     *          name of header
     * @return array of headers
     * @see javax.mail.internet.MimeMessage
     */
    public String[] getHeader(
        String name
    ) throws MessagingException {
        return this.mimeMsg.getHeader(name);
    }

    //-------------------------------------------------------------------------  
    /**
     * Extracts the complete content of all header elements of a MIME message
     * and returns them as concatenated string using the system's line separator
     * as separator between the elements.
     * 
     * @return the concatenated header elements of the message
     */
    public String getAllHeaderLinesAsString(
    ) throws MessagingException {
        if(this.headerLines == null) {
            StringBuffer text = new StringBuffer();
            Enumeration lines = this.mimeMsg.getAllHeaderLines();
            while (lines.hasMoreElements()) {
                text.append((String) lines.nextElement());
                text.append(System.getProperty("line.separator", "\n"));
            }
            this.headerLines = text.toString();
        }
        return this.headerLines;
    }

    //-------------------------------------------------------------------------  
    /**
     * Extracts the body part of a MIME message, currently only message bodies
     * of the following formats are supported:<BR>
     * <UL>
     * <LI>String</LI>
     * <LI>InputStream</LI>
     * <LI>MultiPart</LI>
     * </UL>
     * In case of MultiParts only parts of the mime types "text/plain" and
     * "text/html" are currently supported. 
     * The body is returned in string format.
     * 
     * @return
     */
    public String getBody(
    ) throws MessagingException, IOException {
        if (this.messageBody == null) {
            this.messageBody = Activities.getMessageBody(this.mimeMsg);
        }
        return this.messageBody;
    }

  //-------------------------------------------------------------------------  
  public String getMessageIdentity(
  ) throws MessagingException {
      if (this.messageIdentity == null) {
          // extract the message ID
          this.messageIdentity = this.mimeMsg.getHeader(OPENCRX_MESSAGE_IDENTITY, null);
          if(this.messageIdentity == null) {
              this.messageIdentity = "";
          }
      }
      return messageIdentity;
  }
  
  //-------------------------------------------------------------------------  
  /**
   * Extracts the message ID of the message and cache it locally
   * 
   * @return the message ID of the message
   */
  public String getMessageID(
  ) throws MessagingException {
      if (this.messageId == null) {
          // extract the message ID
          this.messageId = this.mimeMsg.getMessageID();
          if(this.messageId == null || this.messageId.length() == 0) {
              this.messageId = OPENCRX_MESSAGE_ID + UUIDs.getGenerator().next();
          }
      }
      return this.messageId;
  }

  //-------------------------------------------------------------------------  
  /**
   * Extracts the subject of the message and cache it locally. If the message
   * contains several subject entries, these entries are concatenated using
   * the system's line separator.
   * 
   * @return the subject of the message
   */
  public String getSubject(
  ) throws MessagingException {
      if (this.subject == null) {
          // extract the subject
          // if
          // handle a mail with no subject or with more than one subject
          // as if subject is an empty string.
          String[] subjects = this.mimeMsg.getHeader("Subject");
          if (subjects != null && subjects.length > 0) {
              StringBuffer msgSubject = new StringBuffer();
              for(int i = 0; i < subjects.length; i++) {
                  if(i > 0) msgSubject.append(" ");
                  try {
                      msgSubject.append(
                          MimeUtility.decodeText(subjects[i])
                      );
                  } catch(UnsupportedEncodingException e) {
                      msgSubject.append(
                          subjects[i]
                      );            
                  }
              }
              this.subject = msgSubject.toString();
          }
      }
      return this.subject;
  }

  //-------------------------------------------------------------------------  
  /**
   * Extracts date of the message it was sent and cache it locally. Returns null
   * if no date is contained in the message.
   * 
   * @return the subject of the message
   */
  public Date getDate(
  ) throws MessagingException {
      return this.mimeMsg.getSentDate();
  }

  //-------------------------------------------------------------------------  
  /**
   * Extracts the sender's email address from the message and cache it locally.
   * If no address is found <code>UNSPECIFIED_ADDRESS</code> is used.
   * 
   * @return the sender's email addresses
   */
  public String[] getFrom(
  ) throws AddressException, MessagingException {
      if(this.from == null) {
          // Get FROM EMailAddress
          Address[] headerFromAddresses = this.mimeMsg.getFrom();
          this.from = getAddresses(headerFromAddresses);
      }
      return this.from;
  }

  //-------------------------------------------------------------------------  
  /**
   * Extracts the recipients of type Message.RecipientType.TO ("To") of
   * the message and cache it locally.
   * 
   * @return the recipients' email addresses
   */
  public String[] getRecipients(
  ) throws MessagingException {  
      return this.getRecipients(Message.RecipientType.TO);
  }

  //-------------------------------------------------------------------------  
  /**
   * Extracts the recipients of the given type of the message and cache it locally.
   * 
   * @param type     the type for which the addresses are needed, i.e. either
   *                 "To", "Cc" or "Bcc"
   * @return the recipients' email addresses
   */
  public String[] getRecipients(
      Message.RecipientType type
  ) throws MessagingException {
      if(Message.RecipientType.TO.toString().equalsIgnoreCase(type.toString())) {
          if(this.to == null) {
              this.to = getRecipientsByType(type);
          }
          return this.to;
      }
      else if (Message.RecipientType.CC.toString().equalsIgnoreCase(type.toString())) {
          if (this.cc == null) {
              this.cc = getRecipientsByType(type);
          }
          return this.cc;
      }
      else if (Message.RecipientType.BCC.toString().equalsIgnoreCase(type.toString())) {
          if(this.bcc == null) {
              this.bcc = getRecipientsByType(type);
          }
          return this.bcc;
      }
      return null;
  }

  //-------------------------------------------------------------------------  
  /**
   * Extract the priority from the email message. Note that if no header
   * element is found this indicates a "normal" priority. Note that rfc822
   * does not define a standard header field for priority. The name of the
   * "priority" header field depends on your mail client used. "Importance"
   * with values high, normal and low "Priority" with values Urgent and
   * Non-Urgent "X-Priority" with values 1=high and 5=low These values are
   * mapped to:
   * <UL>
   * <LI>ACTIVITY_PRIORITY_LOW,
   * <LI>ACTIVITY_PRIORITY_NORMAL and
   * <LI>ACTIVITY_PRIORITY_HIGH
   * </UL>
   * respectively.
   * 
   * @return the subject of the message
   */
  public short getPriority(
  ) throws MessagingException {
      return Activities.getMessagePriority(this.mimeMsg);
  }

  //-------------------------------------------------------------------------  
  /**
   * Checks whether the message contains binary attachments.
   * 
   * @return      true, if the message contains some binary attachments
   */
  public boolean containsAttachments(
  ) {
      return !this.content.isEmpty();
  }

  //-------------------------------------------------------------------------  
  /**
   * Checks whether the message contains binary attachments.
   * 
   * @return      true, if the message contains some binary attachments
   */
  public boolean containsNestedMessage(
  ) {
      return this.containsNestedMessageAttachment;
  }

  //-------------------------------------------------------------------------  
  /**
   * Checks whether the message contains an openCRX identity.
   * 
   * @return    true if the message contains an openCRX identity, false otherwise
   */
  public boolean hasIdentity(
  ) {
      return !(this.messageIdentity == null || this.messageIdentity.length() == 0);
  }

  //-------------------------------------------------------------------------  
  /**
   * Returns a collection of the attachments or null if the message
   * does not contain any.
   * 
   * @return      collection of the attachments or null otherwise
   */
  public Collection getContents(
  ) {
      return this.content.isEmpty()
      ? null
              : this.content; 
  }

  //-------------------------------------------------------------------------  
  /**
   * Returns a collection of the text attachments or null if the message
   * does not contain any.
   * 
   * @return      collection of the text attachments or null otherwise
   */
  public Collection getTextContents(
  ) {
      ArrayList textContents = new ArrayList();
      if(!this.content.isEmpty()) {
          Iterator binContents = this.content.iterator();
          while (binContents.hasNext()) {
              MessageContent contentElem = (MessageContent) binContents.next();
              if(contentElem.getContent() instanceof String) {
                  textContents.add(contentElem);
              }
          }
      }
      return textContents.size() > 0 
      ? textContents 
              : null;
  }

  //-------------------------------------------------------------------------  
  /**
   * Returns a collection of the binary attachments or null if the message
   * does not contain any.
   * 
   * @return      collection of the binary attachments or null otherwise
   */
  public Collection getBinaryContents(
  ) {
      ArrayList binaryContents = new ArrayList();
      if(this.content.size() > 0) {
          Iterator binContents = this.content.iterator();
          while (binContents.hasNext()) {
              MessageContent contentElem = (MessageContent) binContents.next();
              if(!(contentElem.getContent() instanceof String)) {
                  binaryContents.add(contentElem);
              }
          }
      }
      return binaryContents.size() > 0 
      ? binaryContents 
              : null;
  }

  //-------------------------------------------------------------------------  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString(
  ) {
      StringBuffer buffer = new StringBuffer();
      try {
          buffer.append("send date='").append(this.getDate());
      } catch(Exception e) {}
      buffer.append("', from={'");
      try { 
          this.getFrom();
      } catch(Exception e) {}
      if(this.from != null) {
          for (int i = 0; i < this.from.length; i++) {
              buffer.append(this.from[i]).append("',");
          }
      }
      else {
          buffer.append("',");
      }
      buffer.append("}, to={'");
      try {
          this.getRecipients(Message.RecipientType.TO);
      } catch(Exception e) {}
      if(this.to != null) {
          for (int i = 0; i < this.to.length; i++) {
              buffer.append(this.to[i]).append("',");
          }
      }
      else {
          buffer.append("',");
      }
      buffer.append("}, cc={'");
      try {
          this.getRecipients(Message.RecipientType.CC);
      } catch(Exception e) {}
      if(this.cc != null) {
          for (int i = 0; i < this.cc.length; i++) {
              buffer.append(this.cc[i]).append("',");
          }
      }
      buffer.append("}, bcc={'");
      try {
          this.getRecipients(Message.RecipientType.BCC);
      } catch(Exception e) {}
      if(this.bcc != null) {
          for (int i = 0; i < this.bcc.length; i++) {
              buffer.append(this.bcc[i]).append("',");
          }
      }
      else {
          buffer.append("',");
      }
      try {
          buffer.append("}', messageId='").append(this.getMessageID());
      } catch(Exception e) {}
      try {
          buffer.append("', subject='").append(this.getSubject());
      } catch(Exception e) {}
      try {
          buffer.append("', priority='").append(this.getPriority());
      } catch(Exception e) {}
      try {
          buffer.append("', body='").append(this.getBody() == null ? "" : this.getBody()).append("'");
      } catch(Exception e) {}
      if(this.containsAttachments()) {
          Iterator contents = this.content.iterator();
          while(contents.hasNext()) {
              MessageContent contentElem = (MessageContent) contents.next();
              buffer.append(" [included content element '" + contentElem.toString() + "' included.] ");
          }
      }
      return buffer.toString();
  }

  //-------------------------------------------------------------------------
  /**
   * @param headerAddresses
   * @throws AddressException
   */
  private String[] getAddresses(
      Address[] headerAddresses
  ) throws AddressException {
      String addresses[] = null;
      if (headerAddresses != null && headerAddresses.length > 0) {
          addresses = new String[headerAddresses.length];
          for (int i = 0; i < headerAddresses.length; i++) {
              if (headerAddresses[0] instanceof InternetAddress) {
                  addresses[i] = ((InternetAddress)headerAddresses[i]).getAddress();
              } else {
                  InternetAddress temp = new InternetAddress(headerAddresses[i].toString());
                  addresses[i] = temp.getAddress();
              }
          }
      } else {
          addresses = new String[]{UNSPECIFIED_ADDRESS};
      }
      return addresses;
  }

  //-------------------------------------------------------------------------
  private void parsePart(
      MimeBodyPart part,
      String partSection
  ) throws MessagingException, IOException {
      Object partContent = part.getContent();
      String partContentType = part.getContentType();

      if(partContentType.startsWith("text/")) {      
          String disposition = part.getDisposition();
          if(
              ((disposition == null) || DISPOSITION_INLINE.equalsIgnoreCase(disposition)) && 
              (this.messageBody == null)
          ) {
              this.messageBody = (String)partContent;
          }
          else {
              this.content.add(
                  new MessageContent(
                      part.getContentID(), 
                      partSection,
                      partContentType, 
                      partContent            
                  )
              );
          }
      }
      else if(partContent instanceof MimeMessage) {
          SimpleMimeMessage nestedMsg = new SimpleMimeMessage((MimeMessage)partContent, true);
          this.content.add(
              new MessageContent(
                  part.getFileName(), 
                  partSection,
                  partContentType, 
                  nestedMsg
              )
          );
          this.containsNestedMessageAttachment = true;
          AppLog.trace("attached MimeMessage imported", part.getFileName());
      }
      else if (partContent instanceof MimeMultipart) {
          MimeMultipart multipart = (MimeMultipart)partContent;
          for(int i = 0; i < multipart.getCount(); i++ ) {
              this.parsePart(
                  (MimeBodyPart)multipart.getBodyPart(i), 
                  partSection + "." + (i + 1)
              );
          }
      }
      else {
          InputStream is = null;                
          if(partContent instanceof InputStream) {
              is = (InputStream) partContent;
          }
          else {
              is = part.getInputStream();
          }
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          int readByte = is.read();
          while (readByte != -1) {
              bos.write(readByte);
              readByte = is.read();
          }
          this.content.add(
              new MessageContent(
                  part.getFileName(), 
                  partSection,
                  partContentType, 
                  bos.toByteArray()
              )
          );
          AppLog.trace("attachment of type", partContentType);
          AppLog.trace("attachment file name", part.getFileName());
      }
  }

  //-------------------------------------------------------------------------
  /**
   * Extracts the recipients of the given type of the message and cache it locally.
   * 
   * @param type     the type for which the addresses are needed, i.e. either
   *                 "To", "Cc" or "Bcc"
   * @return the recipients' email addresses
   */
  private String[] getRecipientsByType(
      Message.RecipientType type
  ) throws MessagingException {
      Address headerToAddresses[] = this.mimeMsg.getRecipients(type);
      return this.getAddresses(headerToAddresses);
  }

  //-------------------------------------------------------------------------
  public void markAsDeleted(
  ) {
      try {
          this.mimeMsg.setFlag(
              Flags.Flag.DELETED,
              true
          );
      }
      catch(MessagingException e) {}
  }
  
  // -----------------------------------------------------------------------
  // Members
  // -----------------------------------------------------------------------
  public static final String OPENCRX_MESSAGE_ID = "openCRX:MsgID:";  
  public static final String OPENCRX_MESSAGE_IDENTITY = "X-openCrxMsgIdentity";  
  public static final String UNSPECIFIED_ADDRESS = "NO_ADDRESS_SPECIFIED";

  public static final String DISPOSITION_INLINE = "inline";
  
  public static final short PRIORITY_LOW = 1;
  public static final short PRIORITY_NORMAL = 2;
  public static final short PRIORITY_HIGH = 3;
  public static final short PRIORITY_URGENT = 4;
  public static final short PRIORITY_IMMEDIATE = 5;
  
  private final MimeMessage mimeMsg;

  /* the openCRX identity */
  private String messageIdentity = null;

  /* the messageId */
  private String messageId = null;

  /* the subject of the message */
  private String subject = null;

  /* message body as string */
  private String messageBody = null;
  
  /* parts of some binary type */
  private List content = new ArrayList();

  /* all header lines */
  private String headerLines = null;
  
  /* addresses of recipients */
  private String from[] = null;
  private String to[] = null;
  private String cc[] = null;
  private String bcc[] = null;

  /* indicates whether message contains nested message attachment */
  private boolean containsNestedMessageAttachment = false;
  
}

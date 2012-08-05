/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: AttachmentStoreImpl.java,v 1.24 2008/07/03 23:42:31 wfro Exp $
 * Description: XWiki AttachmentStoreImpl
 * Revision:    $Revision: 1.24 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/07/03 23:42:31 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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
package org.opencrx.groupware.xwiki.spi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.cci2.BinaryLargeObjects;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.store.XWikiAttachmentStoreInterface;
import com.xpn.xwiki.store.XWikiHibernateAttachmentStore;

public class AttachmentStoreImpl 
    extends AbstractStoreImpl 
    implements XWikiAttachmentStoreInterface {
    
    //-----------------------------------------------------------------------
    public AttachmentStoreImpl(         
        XWikiContext context
    ) {
        super(
            context
        );
    }

    //-----------------------------------------------------------------------
    public void saveAttachmentContent(
        XWikiAttachment attachment, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        this.saveAttachmentContent(
            attachment, 
            true, 
            context, 
            bTransaction
        );
    }

    //-----------------------------------------------------------------------
    public void saveAttachmentContent(
        XWikiAttachment xWikiAttachment, 
        boolean parentUpdate, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiAttachment.getDocId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                List<DocumentAttachment> attachments = this.findAttachments(
                    document, 
                    session, 
                    Long.toString(xWikiAttachment.getId())
                );
                DocumentAttachment attachment = null;
                if(attachments.isEmpty()) {
                    attachment = session.getDocumentPackage().getDocumentAttachment().createDocumentAttachment();
                    attachment.refInitialize(false, false);
                    document.addAttachment(
                        false,
                        this.uuidAsString(),
                        attachment
                    );
                    attachment.setName(Long.toString(xWikiAttachment.getId()));            
                    attachment.setContentName(xWikiAttachment.getFilename());
                    attachment.setAuthor(xWikiAttachment.getAuthor());
                    attachment.setVersion(xWikiAttachment.getVersion());
                    attachment.setDescription(xWikiAttachment.getComment());                    
                }
                else {
                    attachment = attachments.get(0);
                }
                attachment.setContent(
                    BinaryLargeObjects.valueOf(
                        xWikiAttachment.getAttachment_content().getContent()
                    )
                );
            }
            session.commitTransaction();
            if (parentUpdate) {
                context.getWiki().getStore().saveXWikiDoc(
                    xWikiAttachment.getDoc(), 
                    context, 
                    true
                );
            }
        }
        catch (Exception e) {
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_ATTACHMENT,
                "Exception while saving attachments", 
                e
            );
        } 
        finally {
        }        
    }

    //-----------------------------------------------------------------------
    public void saveAttachmentsContent(
        List xWikiAttachments, 
        XWikiDocument xWikiDocument, 
        boolean bParentUpdate,         
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        try{
            if (xWikiAttachments == null) return;
            for(XWikiAttachment xWikiAttachment: (List<XWikiAttachment>)xWikiAttachments) {
                this.saveAttachmentContent(
                    xWikiAttachment, 
                    false, 
                    context, 
                    false
                );
            }
        }
        catch (Exception e) {
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_ATTACHMENT,
                "Exception while saving attachments", 
                e
            );
        } 
        finally {
        }                
    }

    //-----------------------------------------------------------------------    
    public void loadAttachmentContent(
        XWikiAttachment xWikiAttachment, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiAttachment.getDocId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                List<DocumentAttachment> attachments = this.findAttachments(
                    document, 
                    session, 
                    Long.toString(xWikiAttachment.getId())
                );
                if(!attachments.isEmpty()) {
                    DocumentAttachment attachment = attachments.get(0);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    InputStream content = attachment.getContent().getContent();
                    int b;
                    while((b = content.read()) != -1) {
                        bos.write(b);
                    }
                    bos.close();
                    xWikiAttachment.setContent(bos.toByteArray());
                    xWikiAttachment.setFilesize(bos.size());
                }
            }
        }
        catch (Exception e) {
            Object[] args = { xWikiAttachment.getFilename(), xWikiAttachment.getDoc().getFullName() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_ATTACHMENT,
                "Exception while loading attachment {0} of document {1}", 
                e, 
                args
            );
        } 
        finally {
        }        
    }

    //-----------------------------------------------------------------------    
    public void loadAttachmentArchive(
        XWikiAttachment xWikiAttachment, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
    }

    //-----------------------------------------------------------------------    
    public void deleteXWikiAttachment(
        XWikiAttachment attachment,  
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        this.deleteXWikiAttachment(
            attachment, 
            true, 
            context, 
            bTransaction
         );
    }

    //-----------------------------------------------------------------------
    public void deleteXWikiAttachment(
        XWikiAttachment xWikiAttachment, 
        boolean parentUpdate, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiAttachment.getDocId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                List<DocumentAttachment> attachments = this.findAttachments(
                    document, 
                    session, 
                    Long.toString(xWikiAttachment.getId())
                );
                for(DocumentAttachment attachment: (Collection<DocumentAttachment>)attachments) {
                    attachment.refDelete();
                }                
            }
            session.commitTransaction();
            // Update parent
            if (parentUpdate) {
                try {
                    List list = xWikiAttachment.getDoc().getAttachmentList();
                    for (int i=0;i<list.size();i++) {
                        XWikiAttachment attach = (XWikiAttachment) list.get(i);
                        if (xWikiAttachment.getFilename().equals(attach.getFilename())) {
                            list.remove(i);
                            break;
                        }
                    }
                    context.getWiki().getStore().saveXWikiDoc(
                        xWikiAttachment.getDoc(), 
                        context, 
                        false
                    );
                }
                catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Error updating document when deleting attachment " + xWikiAttachment.getFilename() + " of doc " + xWikiAttachment.getDoc().getFullName());
                    }
                }
            }
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}                        
            Object[] args = { xWikiAttachment.getFilename(), xWikiAttachment.getDoc().getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_ATTACHMENT,
                "Exception while deleting attachment {0} of document {1}", 
                e, 
                args
            );
        } 
        finally {
        }
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(XWikiHibernateAttachmentStore.class);

}

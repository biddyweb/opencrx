/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: AbstractStoreImpl.java,v 1.17 2008/09/09 22:05:36 wfro Exp $
 * Description: XWiki AbstractStoreImpl
 * Revision:    $Revision: 1.17 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/09 22:05:36 $
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

import java.util.Date;
import java.util.List;

import org.opencrx.kernel.document1.cci2.DocumentAttachmentQuery;
import org.opencrx.kernel.document1.cci2.DocumentFolderQuery;
import org.opencrx.kernel.document1.cci2.DocumentLockQuery;
import org.opencrx.kernel.document1.cci2.DocumentQuery;
import org.opencrx.kernel.document1.cci2.PropertySetQuery;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentAttachment;
import org.opencrx.kernel.document1.jmi1.DocumentFolder;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.objects.BaseCollection;
import com.xpn.xwiki.store.XWikiHibernateStore;

public class AbstractStoreImpl 
    // Must extend XWikiHibernateSession, otherwise XWiki.getHibernateSession() returns false */
    extends XWikiHibernateStore {
    
    //-----------------------------------------------------------------------
    public AbstractStoreImpl(          
        XWikiContext context
    ) {   
        super(context);
    }

    //-----------------------------------------------------------------------
    protected OpenCrxSession getOpenCrxSession(
        XWikiContext context            
    ) throws XWikiException {
        return new OpenCrxSession(context);
    }
    
    //-----------------------------------------------------------------------
    public void cleanUp(
        XWikiContext context
    ) {   
    }
    
    //-----------------------------------------------------------------------
    protected List<Document> findDocumentsByDocumentNumber(
        OpenCrxSession session,
        long docId 
    ) {
        DocumentQuery query = session.getDocumentPackage().createDocumentQuery();
        query.thereExistsDocumentNumber().equalTo(Long.toString(docId));
        query.forAllActiveUntil().greaterThanOrEqualTo(new Date());
        return session.getDocumentSegment().getDocument(query);        
    }
        
    //-----------------------------------------------------------------------
    protected List<Document> findDocumentsByQualifiedName(
        OpenCrxSession session,
        String qualifiedName 
    ) {
        DocumentQuery query = session.getDocumentPackage().createDocumentQuery();
        query.thereExistsQualifiedName().equalTo(qualifiedName);
        query.forAllActiveUntil().greaterThanOrEqualTo(new Date());
        return session.getDocumentSegment().getDocument(query);        
    }
        
    //-----------------------------------------------------------------------
    protected List<DocumentAttachment> findAttachments(
        Document document,
        OpenCrxSession session,
        String name 
    ) {
        DocumentAttachmentQuery query = session.getDocumentPackage().createDocumentAttachmentQuery();
        query.name().equalTo(name);
        return document.getAttachment(query);        
    }
        
    //-----------------------------------------------------------------------
    protected String getPropertySetName(
        BaseCollection object
    ) {
        return object.getClassName() + "#" + object.getNumber();
    }

    //-----------------------------------------------------------------------
    protected List<org.opencrx.kernel.document1.jmi1.DocumentAttachment> findAttachment(
        Document document,
        XWikiAttachment attachment,
        OpenCrxSession session
    ) {
        DocumentAttachmentQuery query = session.getDocumentPackage().createDocumentAttachmentQuery();
        query.name().equalTo(Long.toString(attachment.getId()));
        return document.getAttachment(query);
    }
    
    //-----------------------------------------------------------------------
    protected List<org.opencrx.kernel.document1.jmi1.DocumentLock> findXWikiLock(
        Document document,
        OpenCrxSession session
    ) {
        DocumentLockQuery query = session.getDocumentPackage().createDocumentLockQuery();
        query.name().equalTo("XWiki");
        return document.getLock(query);
    }
        
    //-----------------------------------------------------------------------
    protected List<org.opencrx.kernel.document1.jmi1.PropertySet> findPropertySet(
        Document document,
        String propertySetName,
        OpenCrxSession session
    ) {
        PropertySetQuery query = session.getDocumentPackage().createPropertySetQuery();
        query.name().equalTo(propertySetName);
        return document.getPropertySet(query);        
    }
        
    //-----------------------------------------------------------------------
    protected DocumentFolder getRootFolder(
        OpenCrxSession session
    ) {
        DocumentFolderQuery query = session.getDocumentPackage().createDocumentFolderQuery();
        query.name().equalTo(ROOT_FOLDER_NAME);
        List<DocumentFolder> folders = session.getDocumentSegment().getFolder(query);
        return folders.isEmpty() ? null : folders.get(0);
    }
    
    //-----------------------------------------------------------------------
    protected List<DocumentFolder> findDocumentFolder(
        String folderName,
        OpenCrxSession session
    ) {
        DocumentFolderQuery query = session.getDocumentPackage().createDocumentFolderQuery();
        query.name().equalTo(folderName);
        query.thereExistsParent().equalTo(
            this.getRootFolder(session)
        );
        return session.getDocumentSegment().getFolder(query);
    }
    
    //-----------------------------------------------------------------------
    protected String uuidAsString(
    ) {
        return UUIDConversion.toUID(uuidGenerator.next());
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static final String MONITOR_ID = "opencrx";
    protected static final String ROOT_FOLDER_NAME = "XWikiSpaces";
    protected static final UUIDGenerator uuidGenerator = UUIDs.getGenerator();

}

/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: VersioningStoreImpl.java,v 1.14 2008/06/21 23:04:37 wfro Exp $
 * Description: XWiki StoreImpl
 * Revision:    $Revision: 1.14 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/06/21 23:04:37 $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencrx.kernel.document1.cci2.DocumentRevisionQuery;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.suigeneris.jrcs.rcs.Version;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiDocumentArchive;
import com.xpn.xwiki.doc.rcs.XWikiPatch;
import com.xpn.xwiki.doc.rcs.XWikiRCSNodeContent;
import com.xpn.xwiki.doc.rcs.XWikiRCSNodeId;
import com.xpn.xwiki.doc.rcs.XWikiRCSNodeInfo;
import com.xpn.xwiki.store.XWikiVersioningStoreInterface;

public class VersioningStoreImpl 
    extends AbstractStoreImpl 
    implements XWikiVersioningStoreInterface {

    //-----------------------------------------------------------------------
    /**
     * This allows to initialize our storage engine.
     * The hibernate config file path is taken from xwiki.cfg
     * or directly in the WEB-INF directory.
     * @param xwiki
     * @param context
     */
    public VersioningStoreImpl(
        XWikiContext context
    ) {
        super(context);
    }

    //-----------------------------------------------------------------------
    public Version[] getXWikiDocVersions(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        try {
            XWikiDocumentArchive archive = this.getXWikiDocumentArchive(
                xWikiDocument, 
                context
            );
            if (archive == null) return new Version[0];
            Collection nodes = archive.getNodes();
            Version[] versions = new Version[nodes.size()];
            Iterator it = nodes.iterator();
            for(int i = 0; i < versions.length; i++) {
                XWikiRCSNodeInfo node = (XWikiRCSNodeInfo)it.next();
                versions[versions.length-1-i] = node.getId().getVersion();
            }
            return versions;
        } 
        catch (Exception e) {
            Object[] args = { xWikiDocument.getFullName() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_READING_REVISIONS,
                "Exception while reading document {0} revisions", 
                e, 
                args
            );
        }
    }

    //-----------------------------------------------------------------------
    public XWikiDocumentArchive getXWikiDocumentArchive(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        XWikiDocumentArchive archiveDoc = xWikiDocument.getDocumentArchive();
        if (archiveDoc != null) return archiveDoc;
        String key = ((xWikiDocument.getDatabase()==null)
            ? "xwiki"
            : xWikiDocument.getDatabase()) + ":" + xWikiDocument.getFullName();
        if(!"".equals(xWikiDocument.getLanguage())) {
            key = key + ":" + xWikiDocument.getLanguage();
        }
        archiveDoc = (XWikiDocumentArchive) context.getDocumentArchive(key);
        if(archiveDoc == null) {
            String db = context.getDatabase();
            try {
                if (xWikiDocument.getDatabase() != null) context.setDatabase(xWikiDocument.getDatabase());
                archiveDoc = new XWikiDocumentArchive(xWikiDocument.getId());
                this.loadXWikiDocArchive(
                    archiveDoc, 
                    true, 
                    context
                );
                xWikiDocument.setDocumentArchive(archiveDoc);
            } 
            finally {
                context.setDatabase(db);
            }
            // This will also make sure that the Archive has a strong reference
            // and will not be discarded as long as the context exists.
            context.addDocumentArchive(key, archiveDoc);
        }
        return archiveDoc;
    }

    //-----------------------------------------------------------------------
    public void loadXWikiDocArchive(
        XWikiDocumentArchive archivedoc, 
        boolean bTransaction, 
        XWikiContext context
    ) throws XWikiException {
        try {
            List nodes = this.loadAllRCSNodeInfo(
                context, 
                archivedoc.getId(), 
                bTransaction
            );
            archivedoc.setNodes(nodes);
        } 
        catch (Exception e) {
            Object[] args = { new Long(archivedoc.getId()) };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while loading archive {0}", 
                e, 
                args
            );
        }
    }

    //-----------------------------------------------------------------------
    public void saveXWikiDocArchive(
        final XWikiDocumentArchive archivedoc, 
        boolean bTransaction, 
        XWikiContext context
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
        for (Iterator it = archivedoc.getDeletedNodeInfo().iterator(); it.hasNext(); ) {
            XWikiRCSNodeInfo nodeInfo = (XWikiRCSNodeInfo) it.next();
//            session.delete(nodeInfo);
            it.remove();
        }
        for (Iterator it = archivedoc.getUpdatedNodeInfos().iterator(); it.hasNext(); ) {
            XWikiRCSNodeInfo nodeInfo = (XWikiRCSNodeInfo) it.next();
//            session.saveOrUpdate(nodeInfo);
            it.remove();
        }
        for (Iterator it = archivedoc.getUpdatedNodeContents().iterator(); it.hasNext(); ) {
            XWikiRCSNodeContent nodeContent = (XWikiRCSNodeContent) it.next();
//            session.update(nodeContent);
            it.remove();
        }
    }

    //-----------------------------------------------------------------------
    public XWikiDocument loadXWikiDoc(
        XWikiDocument basedoc, 
        String sversion, 
        XWikiContext context
    ) throws XWikiException {
        XWikiDocumentArchive archive = this.getXWikiDocumentArchive(
            basedoc, 
            context
        );
        Version version = new Version(sversion);
        XWikiDocument doc = archive.loadDocument(
            version, 
            context
        );
        if(doc == null) {
            Object[] args = { basedoc.getFullName(), version.toString() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_UNEXISTANT_VERSION,
                "Version {1} does not exist while reading document {0}", 
                null, 
                args
            );
        }
        // Make sure the document has the same name
        // as the new document (in case there was a name change
        doc.setName(basedoc.getName());
        doc.setSpace(basedoc.getSpace());
        doc.setDatabase(basedoc.getDatabase());
        doc.setStore(basedoc.getStore());
        return doc;
    }

    //-----------------------------------------------------------------------
    public void resetRCSArchive(
        final XWikiDocument doc, 
        boolean bTransaction, 
        final XWikiContext context
    ) throws XWikiException {
        XWikiDocumentArchive archive = this.getXWikiDocumentArchive(
            doc, 
            context
        );
        archive.resetArchive();
        archive.getDeletedNodeInfo().clear();
        doc.setMinorEdit(false);
        this.deleteArchive(
            doc, 
            false, 
            context
        );
        this.updateXWikiDocArchive(
            doc, 
            false, 
            context
        );
    }

    //-----------------------------------------------------------------------
    public void updateXWikiDocArchive(
        XWikiDocument doc, 
        boolean bTransaction, 
        XWikiContext context
    ) throws XWikiException {
        try {
//            XWikiDocumentArchive archiveDoc = this.getXWikiDocumentArchive(doc, context);
//            if(archiveDoc != null) {
//                archiveDoc.updateArchive(
//                    doc, 
//                    doc.getAuthor(), 
//                    doc.getDate(), 
//                    doc.getComment(), 
//                    doc.getRCSVersion(), 
//                    context
//                );
//                doc.setRCSVersion( archiveDoc.getLatestVersion() );
//                this.saveXWikiDocArchive(archiveDoc, bTransaction, context);
//            }
        } 
        catch (Exception e) {
            Object[] args = { doc.getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_OBJECT,
                "Exception while updating archive {0}", 
                e, 
                args
            );
        }
    }
    
    //-----------------------------------------------------------------------
    protected List loadAllRCSNodeInfo(
        XWikiContext context, 
        final long docId, 
        boolean bTransaction
    ) throws XWikiException {
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Document> documents = this.findDocumentsByDocumentNumber(
                session, 
                docId
            );
            List<XWikiRCSNodeInfo> nodes = new ArrayList<XWikiRCSNodeInfo>();
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                // Only get head revision. Ignore older revisions. Versioning does
                // not seem to work with this version of XWiki
                DocumentRevision revision = document.getHeadRevision();
//                Collection<DocumentRevision> revisions = document.getRevision();
//                for(DocumentRevision revision: revisions) {
                    XWikiRCSNodeInfo node = new XWikiRCSNodeInfo();
                    node.setId(
                        new XWikiRCSNodeId(
                            docId, 
                            new Version(revision.getVersion())
                        )
                    );
                    node.setAuthor(revision.getAuthor());
                    node.setComment(revision.getName());
                    node.setDate(revision.getCreatedAt());
                    nodes.add(node);
//                }
            }
            return nodes;
        }
        catch (Exception e) {
            Object[] args = { docId };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while getting RCS node info for document {0}", 
                e, 
                args
            );
        }
    }
    
    //-----------------------------------------------------------------------
    public XWikiRCSNodeContent loadRCSNodeContent(
        final XWikiRCSNodeId id, 
        boolean bTransaction, 
        XWikiContext context
    ) throws XWikiException {
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Document> documents = this.findDocumentsByDocumentNumber(
                session, 
                id.getDocId()
            );
            XWikiRCSNodeContent nodeContent = new XWikiRCSNodeContent(id);
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                DocumentRevisionQuery query = session.getDocumentPackage().createDocumentRevisionQuery();
                query.thereExistsVersion().equalTo(id.getVersion().toString());
                List<DocumentRevision> revisions = document.getRevision(query);
                if(!revisions.isEmpty() && (revisions.get(0) instanceof MediaContent)) {
                    MediaContent revision = (MediaContent)revisions.get(0);
                    XWikiPatch patch = new XWikiPatch();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    InputStream content = revision.getContent().getContent();
                    int b;
                    while((b = content.read()) != -1) {
                        bos.write(b);
                    }
                    bos.close();
                    patch.setContent(bos.toString("UTF-8"));
                    nodeContent.setPatch(patch);
                }
            }
            return nodeContent;
        }
        catch(Exception e) {
            Object[] args = { id };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while getting RCS node info for document {0}", 
                e, 
                args
            );            
        }
    }
    
    //-----------------------------------------------------------------------
    public void deleteArchive(
        final XWikiDocument doc, 
        boolean bTransaction, 
        XWikiContext context
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
//        session.createQuery("delete from "+XWikiRCSNodeInfo.class.getName()+" where id.docId=?")
//            .setLong(0, doc.getId())
//            .executeUpdate();
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------        
    private static final Log log = LogFactory.getLog(VersioningStoreImpl.class);
    
}


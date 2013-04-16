/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: openCRX documents default backend implementation.
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.backend;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentFolder;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

/**
 * Default documents backend class.
 *
 */
public class Documents extends AbstractImpl {

	/**
	 * Register Documents backend class.
	 */
	public static void register(
	) {
		registerImpl(new Documents());
	}
	
	/**
	 * Get instance of registered document backend.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static Documents getInstance(
	) throws ServiceException {
		return getInstance(Documents.class);
	}

	/**
	 * Constructor.
	 * 
	 */
	protected Documents(
	) {
		
	}

	/**
	 * Get documents segment.
	 * 
	 * @param pm
	 * @param providerName
	 * @param segmentName
	 * @return
	 * @throws ServiceException
	 */
	public org.opencrx.kernel.document1.jmi1.Segment getDocumentSegment(
		PersistenceManager pm,
		String providerName,
		String segmentName
	) throws ServiceException {
		return (org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
			new Path("xri://@openmdx*org.opencrx.kernel.document1").getDescendant("provider", providerName, "segment", segmentName)
		);
	}
	
	/**
	 * Find document folder.
	 * 
	 * @param documentFolderName
	 * @param segment
	 * @return
	 */
	public DocumentFolder findDocumentFolder(
		String documentFolderName,
		org.opencrx.kernel.document1.jmi1.Segment segment
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
		org.opencrx.kernel.document1.cci2.DocumentFolderQuery query =
		    (org.opencrx.kernel.document1.cci2.DocumentFolderQuery)pm.newQuery(DocumentFolder.class);
		query.name().equalTo(documentFolderName);
		Collection<DocumentFolder> documentFolders = segment.getFolder(query);
		if(!documentFolders.isEmpty()) {
			return documentFolders.iterator().next();
		}
		return null;
	}	

	/**
	 * Create / update document folder.
	 * 
	 * @param documentFolderName
	 * @param segment
	 * @param allUsers
	 * @return
	 */
	public DocumentFolder initDocumentFolder(
		String documentFolderName,
		org.opencrx.kernel.document1.jmi1.Segment segment,
		List<PrincipalGroup> allUsers
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
		DocumentFolder documentFolder = findDocumentFolder(
			documentFolderName,
			segment
		);
		if(documentFolder != null) return documentFolder;
		try {
			pm.currentTransaction().begin();
	  		documentFolder = pm.newInstance(DocumentFolder.class);
	  		documentFolder.setName(documentFolderName);
	  		documentFolder.getOwningGroup().addAll(allUsers);
	  		segment.addFolder(
	  			this.getUidAsString(),
	  			documentFolder
	  		);
			pm.currentTransaction().commit();
		}
		catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
		return documentFolder;
	}

	/**
	 * Create / update document.
	 * 
	 * @param documentName
	 * @param documentTitle
	 * @param revisionURL
	 * @param revisionMimeType
	 * @param revisionName
	 * @param documentFolder
	 * @param segment
	 * @param allUsers
	 * @return
	 */
	public Document initDocument(
		String documentName,
		String documentTitle,
		URL revisionURL,
		String revisionMimeType,
		String revisionName,
		DocumentFolder documentFolder,
		org.opencrx.kernel.document1.jmi1.Segment segment,
		List<PrincipalGroup> allUsers
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
		Document document = findDocument(
			documentName,
			segment
		);
		if(document != null) return document;
		try {
			pm.currentTransaction().begin();
			document = pm.newInstance(Document.class);
			document.setName(documentName);
			document.setTitle(documentTitle);
			document.getOwningGroup().addAll(allUsers);
			segment.addDocument(
				false,
				org.opencrx.kernel.backend.Activities.getInstance().getUidAsString(),
				document
			);
			MediaContent documentRevision = pm.newInstance(MediaContent.class);
			documentRevision.setName(revisionName);
			documentRevision.setContentName(revisionName);
			documentRevision.setContentMimeType(revisionMimeType);
			documentRevision.setContent(
				org.w3c.cci2.BinaryLargeObjects.valueOf(revisionURL)
			);
			documentRevision.getOwningGroup().addAll(allUsers);
			document.addRevision(
				this.getUidAsString(),
				documentRevision
			);
			document.setHeadRevision(documentRevision);
			if(documentFolder != null) {
				document.getFolder().add(documentFolder);
			}
			pm.currentTransaction().commit();
		}
		catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
		return document;
	}
		
	/**
	 * Find document.
	 * 
	 * @param documentName
	 * @param segment
	 * @return
	 */
	public Document findDocument(
		String documentName,
		org.opencrx.kernel.document1.jmi1.Segment segment
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
		org.opencrx.kernel.document1.cci2.DocumentQuery query =
		    (org.opencrx.kernel.document1.cci2.DocumentQuery)pm.newQuery(Document.class);
		query.name().equalTo(documentName);
		Collection<Document> documents = segment.getDocument(query);
		if(!documents.isEmpty()) {
			return documents.iterator().next();
		}
		return null;
	}

	//-------------------------------------------------------------------------
	// Members
	//-------------------------------------------------------------------------
	public static final String PRIVATE_DOCUMENTS_FOLDER_SUFFIX = "~Private";
	
}

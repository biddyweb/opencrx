/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WebDavStore.java,v 1.11 2011/03/01 22:34:09 wfro Exp $
 * Description: CalDavStore
 * Revision:    $Revision: 1.11 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/03/01 22:34:09 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.webdav;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.Lock;
import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.exceptions.LockFailedException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.WebdavException;
import org.opencrx.kernel.document1.cci2.DocumentBasedFolderEntryQuery;
import org.opencrx.kernel.document1.cci2.DocumentFolderQuery;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentFolder;
import org.opencrx.kernel.document1.jmi1.DocumentFolderEntry;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.home1.cci2.DocumentFeedQuery;
import org.opencrx.kernel.home1.cci2.DocumentProfileQuery;
import org.opencrx.kernel.home1.jmi1.DocumentFeed;
import org.opencrx.kernel.home1.jmi1.DocumentProfile;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

public class WebDavStore implements org.opencrx.application.uses.net.sf.webdav.WebDavStore {

	//-----------------------------------------------------------------------
	public WebDavStore(
		PersistenceManagerFactory pmf
	) {
		this.pmf = pmf;
	}
	
	//-----------------------------------------------------------------------
    public static PersistenceManager getPersistenceManager(
        HttpServletRequest req,
        PersistenceManagerFactory pmf
    ) {
        return req.getUserPrincipal() == null ?
            null :
            pmf.getPersistenceManager(
                req.getUserPrincipal().getName(),
                null
            );
    }
	
	//-----------------------------------------------------------------------
	@Override
	public RequestContext begin(
    	HttpServletRequest req,
    	HttpServletResponse resp		
	) {
		WebDavRequestContext requestContext = new WebDavRequestContext(req, resp, this.pmf);
		return requestContext;
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void commit(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().commit();
				pm.close();
			}
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void createCollection(
		RequestContext requestContext, 
		String path
	) {
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		int pos = path.lastIndexOf("/");
		if(pos > 0) {
			String parentPath = path.substring(0, pos);
			DocumentCollectionResource parent = (DocumentCollectionResource)this.getResourceByPath(requestContext, parentPath);
			if(parent != null) {
				String name = path.substring(pos + 1);
				PersistenceManager pm = JDOHelper.getPersistenceManager(parent.getObject());
				DocumentFolder parentFolder = parent.getObject();
				// Do not create if folder with same name exists
				DocumentFolderQuery query = (DocumentFolderQuery)pm.newQuery(DocumentFolder.class);
				query.name().equalTo(name);
				List<DocumentFolder> folders = parentFolder.getFolder(query);
				if(folders.isEmpty()) {				
					org.opencrx.kernel.document1.jmi1.Segment documentSegment = 
						(org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
							parentFolder.refGetPath().getPrefix(5)
						);
					pm.currentTransaction().begin();
					DocumentFolder newFolder = pm.newInstance(DocumentFolder.class);
					newFolder.refInitialize(false, false);
					newFolder.setName(name);
					newFolder.setParent(parentFolder);
					newFolder.getOwningGroup().addAll(parentFolder.getOwningGroup());
					documentSegment.addFolder(
						UUIDConversion.toUID(uuidGenerator.next()),					
						newFolder
					);
					pm.currentTransaction().commit();
				}
			} else {
				throw new WebdavException("Unable to create folder. Parent folder >" + path + "< not found");				
			}
		}
		else {
			throw new WebdavException("Unable to create folder. Invalid path >" + path + "<");
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public Collection<Resource> getChildren(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof WebDavResource) {
			return ((WebDavResource)res).getChildren();
		} else {
			return Collections.emptyList();
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public BinaryLargeObject getResourceContent(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof WebDavResource) {
			return ((WebDavResource)res).getContent();
		} else {
			return BinaryLargeObjects.valueOf(new byte[]{});
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Path is of the form:
	 * {provider.id} "/" {segment.id} "/user/" {user.id} "/profile/" {profile.name} "/" {feed.name} ["/" {folder.name} ]* "/" {document.name}
	 */
	@Override
	public Resource getResourceByPath(
		RequestContext requestContext, 
		String path
	) {
		PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		String[] components = path.split("/");
		if(components.length >= 6 && "user".equals(components[2]) && "profile".equals(components[4])) {
    		UserHome userHome = (UserHome)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", components[3])
    		);
        	DocumentProfileQuery documentProfileQuery = (DocumentProfileQuery)pm.newQuery(DocumentProfile.class);
        	documentProfileQuery.name().equalTo(components[5]);
        	List<DocumentProfile> documentProfiles = userHome.getSyncProfile(documentProfileQuery);
        	if(documentProfiles.isEmpty()) return null;
    		DocumentProfileResource documentProfileResource = new DocumentProfileResource(
    			requestContext, 
    			documentProfiles.iterator().next()
    		);
    		// DocumentProfile
    		if(components.length == 6) {
    			return documentProfileResource;
        	}
    		// DocumentFeed
			DocumentFeedQuery feedQuery = (DocumentFeedQuery)pm.newQuery(DocumentFeed.class);
			feedQuery.thereExistsDocumentFolder().name().equalTo(components[6]);
			List<DocumentFeed> documentFeeds = documentProfileResource.getObject().getFeed(feedQuery);
			if(documentFeeds.isEmpty()) return null;
			DocumentCollectionResource documentCollectionResource = new DocumentFeedResource(
				requestContext,
				documentFeeds.iterator().next()
			);
			// Document and DocumentFolder
			for(int i = 7; i < components.length; i++) {
				String name = components[i];
				// Is it a folder?
				DocumentFolderQuery folderQuery = documentCollectionResource.getFolderQuery();
				folderQuery.name().equalTo(name);
				List<DocumentFolder> documentFolders = documentCollectionResource.getObject().getFolder(folderQuery);
				if(!documentFolders.isEmpty()) {
					documentCollectionResource = new DocumentFolderResource(
						requestContext,
						documentFolders.iterator().next()
					);
				}
				// It must be a document? 
				else {					
					DocumentBasedFolderEntryQuery folderEntryQuery = documentCollectionResource.getFolderEntryQuery();
					folderEntryQuery.name().equalTo(name);
					List<DocumentFolderEntry> entries = documentCollectionResource.getObject().getFolderEntry(folderEntryQuery);
					return entries.isEmpty() ?
						null :
							new DocumentResource(
								requestContext, 
								(Document)entries.iterator().next().getDocument(), 
								documentCollectionResource
							);										
				}
			}
			return documentCollectionResource;
		}
		return null;		
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void removeResource(
		RequestContext requestContext, 
		Resource res
	) {
		PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();
		if(res instanceof DocumentResource) {
			Document document = ((DocumentResource)res).getObject();
			DocumentCollectionResource documentCollectionResource = ((DocumentResource)res).getDocumentCollectionResource();
			pm.currentTransaction().begin();
			document.getFolder().remove(documentCollectionResource.getObject());
			if(document.getFolder().isEmpty()) {
				document.setDisabled(true);
			}
			pm.currentTransaction().commit();				
		} else if(res instanceof DocumentCollectionResource) {
			pm.currentTransaction().begin();
			((DocumentCollectionResource)res).getObject().setDisabled(true);
			pm.currentTransaction().commit();			
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public MoveResourceStatus moveResource(
    	RequestContext requestContext, 
    	Resource sourceRes, 
    	String sourcePath,
    	String destinationPath
    ) {
        Resource destRes = this.getResourceByPath(requestContext, destinationPath);
        // Destination does not exist
        if(destRes == null) {
			int posSource = sourcePath.lastIndexOf("/");
			int posDestination = destinationPath.lastIndexOf("/");
			String sourcePrefix = sourcePath.substring(0, posSource);
			String destinationPrefix = destinationPath.substring(0, posDestination);
			// Rename
			if(sourcePrefix.equals(destinationPrefix)) {
				PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();
				String newName = destinationPath.substring(posDestination + 1);
				if(sourceRes instanceof DocumentCollectionResource) {
					DocumentFolder folder = ((DocumentCollectionResource)sourceRes).getObject();
					pm.currentTransaction().begin();
					folder.setName(newName);
					pm.currentTransaction().commit();
				} else if(sourceRes instanceof DocumentResource) {
					Document document = ((DocumentResource)sourceRes).getObject();
					pm.currentTransaction().begin();
					document.setName(newName);
					document.setTitle(newName);
					Collection<DocumentRevision> revisions = document.getRevision();
					for(DocumentRevision revision: revisions) {
						revision.setName(newName);
						if(revision instanceof MediaContent) {
							((MediaContent)revision).setContentName(newName);
						}
					}
					pm.currentTransaction().commit();
				}
				return MoveResourceStatus.CREATED;
			} else {
				throw new WebdavException("Move only supportes rename of resources. source=>" + sourcePrefix + "< destination=>" + destinationPrefix + "<");
			}
        }
        // Destination exists. Overwrite destination with 
        // content of source and remove source
        else {
        	try {
	        	this.putResource(
	        		requestContext, 
	        		destinationPath, 
	        		// Put content of source
	        		this.getResourceContent(
	            		requestContext, 
	            		sourceRes
	            	).getContent(),
	            	// keep mimeType of destination
	        		this.getMimeType(
	        			destRes
	        		)
	        	);
        	} catch(Exception e) {
				throw new WebdavException("Unable to put source content of to destination. source=>" + sourcePath + "< destination=>" + destinationPath + "<");        		
        	}
        	if(sourceRes instanceof DocumentResource) {
        		PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();        		
    			Document document = ((DocumentResource)sourceRes).getObject();
    			pm.currentTransaction().begin();
    			pm.deletePersistent(document);
    			pm.currentTransaction().commit();
        	}
        	return MoveResourceStatus.MOVED;
        }
    }
	
	//-----------------------------------------------------------------------
	@Override
	public void rollback(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((WebDavRequestContext)requestContext).getPersistenceManager();		
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().rollback();
				pm.close();
			}
		} catch(Exception e) {}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public String getMimeType(
    	Resource res
    ) {
		if(res instanceof WebDavResource) {
			return ((WebDavResource)res).getMimeType();
		} else {
			return "text/xml";
		}
    }
	
	//-----------------------------------------------------------------------	
    protected String getParentPath(
    	String path
    ) {
        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            return path.substring(0, slash);
        }
        return null;
    }
	
	//-----------------------------------------------------------------------
	@Override
    public PutResourceStatus putResource(
    	RequestContext requestContext, 
    	String path, 
    	InputStream content, 
    	String contentType 
    ) {
		String parentPath = this.getParentPath(path); 
		Resource parent = this.getResourceByPath(
			requestContext, 
			parentPath
		);
		if(parent instanceof DocumentCollectionResource) {
	    	try {
	    		DocumentCollectionResource documentCollectionResource = (DocumentCollectionResource)parent;
	    		PutResourceStatus status = PutResourceStatus.UPDATED;
	    		DocumentFolder documentFolder = documentCollectionResource.getObject();
	    		PersistenceManager pm = JDOHelper.getPersistenceManager(documentFolder);
	    		org.opencrx.kernel.document1.jmi1.Segment documentSegment = 
	    			(org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
	    				documentFolder.refGetPath().getParent().getParent()
	    			);
	    		DocumentBasedFolderEntryQuery query = documentCollectionResource.getFolderEntryQuery();
	    		String name = path.substring(path.lastIndexOf("/") + 1);
	    		if(contentType == null) {
	    			contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(name);
	    		}
	    		query.name().equalTo(name);	    		
	    		List<DocumentFolderEntry> entries = documentFolder.getFolderEntry(query);
	    		Document document = null;
	    		pm.currentTransaction().begin();
	    		if(entries.isEmpty()) {
	    			document = pm.newInstance(Document.class);
	    			document.refInitialize(false, false);
	    			document.setName(name);
	    			document.setTitle(name);
	    			document.setContentType(contentType);
	    			document.getOwningGroup().addAll(
	    				documentFolder.getOwningGroup()
	    			);
	    			documentSegment.addDocument(
	    				UUIDConversion.toUID(uuidGenerator.next()),
	    				document
	    			);
	    			document.getFolder().add(
	    				documentFolder
	    			);
	    			status = PutResourceStatus.CREATED;
	    		} else {
	    			document = (Document)entries.iterator().next().getDocument();
	    		}
	    		MediaContent revision = pm.newInstance(MediaContent.class);
	    		revision.refInitialize(false, false);
	    		revision.setName(name);
	    		revision.setContentName(name);
	    		revision.setContentMimeType(contentType);
	    		ByteArrayOutputStream contentAsBytes = new ByteArrayOutputStream();
	    		BinaryLargeObjects.streamCopy(
	    			content, 
	    			0L, 
	    			contentAsBytes
	    		);
	    		contentAsBytes.close();
	    		revision.setContent(BinaryLargeObjects.valueOf(contentAsBytes.toByteArray()));
	    		revision.getOwningGroup().addAll(
	    			documentFolder.getOwningGroup()
	    		);
	    		Integer version = 0;
	    		if(document.getHeadRevision() != null) {
	    			try {
	    				version = Integer.valueOf(document.getHeadRevision().getVersion());
	    				version += 1;
	    			} catch(Exception e) {}
	    		}
    			revision.setVersion(Integer.toString(version));
	    		document.addRevision(
	    			UUIDConversion.toUID(uuidGenerator.next()),
	    			revision
	    		);
	    		document.setHeadRevision(revision);
	    		pm.currentTransaction().commit();
	    		return status;
	    	}
	    	catch(Exception e) {
	    		new ServiceException(e).log();
	    	}
		}
	    return null;
    }

	//-----------------------------------------------------------------------
	@Override
    public List<Lock> getLocksByPath(
    	RequestContext requestContext, 
    	String path
    ) {
		return Collections.emptyList();
    }

	//-----------------------------------------------------------------------
	private static class LockImpl implements Lock {

		public LockImpl(
	    	String path, 
	    	String owner, 
	    	String scope, 
	    	String type, 
	    	int depth, 
	    	int timeout
		) {
			this.id = path + UUIDs.getGenerator().next().toString();
			this.path = path;
			this.owner = owner;
			this.scope = scope;
			this.type = type;
			this.depth = depth;
			this.expiresAt = System.currentTimeMillis() + timeout;
		}
		
		@Override
        public long getExpiresAt() {
			return this.expiresAt;
        }

		@Override
        public String getID() {
			return this.id;
        }

		@Override
        public int getLockDepth() {
			return this.depth;
        }

		@Override
        public String getOwner() {
			return this.owner;
        }

		@Override
        public String getPath() {
			return this.path;
        }

		@Override
        public String getScope() {
			return this.scope;
        }

		@Override
        public String getType() {
			return this.type;
        }

		private final String id;
    	private final String path; 
    	private final String owner; 
    	private final String scope; 
    	private final String type; 
    	private final int depth; 
    	private final long expiresAt;
		
	}
	
	//-----------------------------------------------------------------------
	@Override
    public Lock lock(
    	RequestContext requestContext, 
    	String path, 
    	String owner, 
    	String scope, 
    	String type, 
    	int depth, 
    	int timeout
    ) throws LockFailedException {
		return new LockImpl(
			path,
			owner,
			scope,
			type, 
			depth,
			timeout
		);
    }

	//-----------------------------------------------------------------------
	@Override
    public void setLockTimeout(
    	RequestContext requestContext, 
    	String id, 
    	int timeout
    ) {	    
    }

	//-----------------------------------------------------------------------
	@Override
    public boolean unlock(
    	RequestContext requestContext, 
    	String id
    ) {
	    return true;
    }

	//-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
	private static final UUIDGenerator uuidGenerator = UUIDs.getGenerator();
	
	protected PersistenceManagerFactory pmf = null;
	
}
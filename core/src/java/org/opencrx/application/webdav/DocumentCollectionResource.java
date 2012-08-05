/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: DocumentCollectionResource.java,v 1.5 2011/07/08 22:46:27 wfro Exp $
 * Description: AccountCollectionResource
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/07/08 22:46:27 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.kernel.document1.cci2.DocumentBasedFolderEntryQuery;
import org.opencrx.kernel.document1.cci2.DocumentFolderQuery;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentBasedFolderEntry;
import org.opencrx.kernel.document1.jmi1.DocumentFolder;
import org.openmdx.base.collection.MarshallingCollection;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.marshalling.Marshaller;

public abstract class DocumentCollectionResource extends WebDavResource {
	
	//-----------------------------------------------------------------------
	static class DocumentResourceCollection<T> extends MarshallingCollection<T> {
		
		public DocumentResourceCollection(
			final RequestContext requestContext,
			Collection<DocumentBasedFolderEntry> entries,
			final DocumentCollectionResource parentCollection
		) {
			super(
				new Marshaller(){

					@Override
                    public Object marshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof DocumentBasedFolderEntry) {
							return new DocumentResource(
								requestContext,
								(Document)((DocumentBasedFolderEntry)source).getDocument(),
								parentCollection
							);
						} else {
							return source;
						}
                    }

					@Override
                    public Object unmarshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof WebDavResource) {
							return ((WebDavResource)source).getObject();
						}
						else {
							return source;
						}
                    }
					
				},
				entries
			);
		}
		
        private static final long serialVersionUID = 6257982279508324945L;

	}
	
	//-----------------------------------------------------------------------
	static class DocumentFolderCollection<T> extends MarshallingCollection<T> {

		public DocumentFolderCollection(
			final RequestContext requestContext,
			Collection<DocumentFolder> entries
		) {
			super(
				new Marshaller(){

					@Override
                    public Object marshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof DocumentFolder) {
							return new DocumentFolderResource(
								requestContext,
								(DocumentFolder)source
							);
						} else {
							return source;
						}
                    }

					@Override
                    public Object unmarshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof WebDavResource) {
							return ((WebDavResource)source).getObject();
						}
						else {
							return source;
						}
                    }
					
				},
				entries
			);
		}
		
        private static final long serialVersionUID = 6284983517339059654L;

	}
	
	//-----------------------------------------------------------------------
	static class ChainingCollection<T> extends AbstractCollection<T> {

		class ChainingIterator implements Iterator<T> {

			public ChainingIterator(
			) {
				this.index = 0;
				this.iterator = ChainingCollection.this.collections[0].iterator();
			}

			@Override
            public boolean hasNext(
            ) {
				boolean hasNext = this.iterator.hasNext();
				while(!hasNext && (this.index < ChainingCollection.this.collections.length - 1)) {
					this.index++;
					this.iterator = ChainingCollection.this.collections[this.index].iterator();
					hasNext = this.iterator.hasNext();
				}
				return hasNext;
            }

			@Override
            public T next(
            ) {
				return this.iterator.next();				
            }

			@Override
            public void remove(
            ) {
				throw new UnsupportedOperationException();
            }
			
			private Iterator<T> iterator;
			private int index;
		}
			
		public ChainingCollection(
			Collection<T>... collections
		) {
			this.collections = collections;
		}
		
		@Override
        public Iterator<T> iterator(
        ) {
			return new ChainingIterator();
        }

		@Override
        public int size(
        ) {
			int size = 0;
			for(int i = 0; i < this.collections.length; i++) {
				size += this.collections[i].size();
			}
	        return size;
        }

		protected final Collection<T>[] collections;
	}
	
	//-----------------------------------------------------------------------
	public DocumentCollectionResource(
		RequestContext requestContext,
		DocumentFolder documentFolder
	) {
		super(
			requestContext,
			documentFolder
		);
	}

	//-----------------------------------------------------------------------
    public String getDisplayName(
    ) {
    	Set<String> features = this.getObject().refDefaultFetchGroup();
    	String name = this.getName();
    	if(features.contains("name")) {
    		name = (String)this.getObject().refGetValue("name");
    	}
    	return name;
    }
    		
	//-----------------------------------------------------------------------
	@Override
    public DocumentFolder getObject(
    ) {
	    return (DocumentFolder)super.getObject();
    }

	//-----------------------------------------------------------------------
	@Override
    public boolean isCollection(
    ) {
		return true;
    }
	
	//-----------------------------------------------------------------------
    @Override
    public String getName(
    ) {
    	return this.getObject().getName();
    }

	//-----------------------------------------------------------------------
	public DocumentBasedFolderEntryQuery getFolderEntryQuery(
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(this.getObject());
		DocumentBasedFolderEntryQuery query = (DocumentBasedFolderEntryQuery)pm.newQuery(DocumentBasedFolderEntry.class);
        query.forAllDisabled().isFalse();                    
        query.orderByCreatedAt().ascending();
        return query;
	}

	//-----------------------------------------------------------------------
	public DocumentFolderQuery getFolderQuery(
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(this.getObject());
	    DocumentFolderQuery folderQuery = (DocumentFolderQuery)pm.newQuery(DocumentFolder.class);
	    folderQuery.forAllDisabled().isFalse();
	    return folderQuery;
	}
	
	//-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
	public Collection<Resource> getChildren(
	) {
		DocumentBasedFolderEntryQuery folderEntryQuery = this.getFolderEntryQuery();
        DocumentResourceCollection documentResourceCollection = new DocumentResourceCollection(
        	this.getRequestContext(),
        	this.getObject().getFolderEntry(folderEntryQuery),
        	this
        );
        DocumentFolderQuery folderQuery = this.getFolderQuery();
        DocumentFolderCollection documentFolderCollection = new DocumentFolderCollection(
        	this.getRequestContext(),
        	this.getObject().getSubFolder(folderQuery)
        );
        return new ChainingCollection(
        	documentFolderCollection,
        	documentResourceCollection
        );
	}
	
	//-----------------------------------------------------------------------
    // Members
	//-----------------------------------------------------------------------
	
}
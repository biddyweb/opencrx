/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Indexed_1.java,v 1.46 2010/03/06 01:32:04 wfro Exp $
 * Description: openCRX indexing plugin
 * Revision:    $Revision: 1.46 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/06 01:32:04 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.layer.persistence;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipInputStream;

import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.text.ExcelToText;
import org.opencrx.kernel.text.OpenOfficeToText;
import org.opencrx.kernel.text.PDFToText;
import org.opencrx.kernel.text.RTFToText;
import org.opencrx.kernel.text.WordToText;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.AbstractDatabase_1;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.AttributeSpecifier;
import org.openmdx.base.query.Directions;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObject;

/**
 * This plugin creates audit entries for modified objects.
 */
public class Indexed_1 extends Database_1 {

    // --------------------------------------------------------------------------
    public Interaction getInteraction(
        javax.resource.cci.Connection connection
    ) throws ResourceException {
        return new LayerInteraction(connection);
    }
        	
    //-------------------------------------------------------------------------
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1 delegation
    ) throws ServiceException {
        super.activate(
            id, 
            configuration, 
            delegation
        );
        // Types to be indexed
        this.indexableTypes = new TreeSet<Path>();
        Model_1_0 model = this.getModel();
        for(ModelElement_1_0 element: model.getContent()) {
        	if(
        		element.isClassType() &&
        		model.isSubtypeOf(element, "org:opencrx:kernel:base:Indexed") &&
        		!model.isSubtypeOf(element, "org:openmdx:base:Segment")
        	) {
	            Path type = model.getIdentityPattern(element);	    
	            if(type != null) {
	            	this.indexableTypes.add(type);
	            }
        	}
        }
        // Manually add some more
        this.indexableTypes.addAll(
            Arrays.asList(
                new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/followUp/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/followUp/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/followUp/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/address/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/note/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/media/:*"),
                new Path("xri://@openmdx*org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/media/:*")
            )
        );            
    }
    
    //-------------------------------------------------------------------------
    protected MappedRecord createResult(
      DataproviderRequest request,
      String structName
    ) throws ServiceException {
    	try {
	    	MappedRecord result = Object_2Facade.newInstance(
		        request.path().getDescendant(
		          new String[]{ "reply", super.uidAsString()}
		        ),
		        structName
	    	).getDelegate();
	    	return result;
    	}
    	catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }

    //-------------------------------------------------------------------------
    protected Set<String> getKeywords(
    	MappedRecord obj
    ) throws ServiceException {
    	Object_2Facade objFacade;
        try {
	        objFacade = Object_2Facade.newInstance(obj);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        Set<String> keywords = new HashSet<String>();
        for(String attribute: INDEXED_ATTRIBUTES) {
            if(objFacade.getValue().keySet().contains(attribute)) {
                for(
                    Iterator<Object> j = objFacade.attributeValuesAsList(attribute).iterator(); 
                    j.hasNext(); 
                ) {
                    Object value = j.next();
                    Reader text = null;
                    boolean isXml = false;
                    if(value instanceof String) {
                        text = new StringReader((String)value);
                    }
                    else if(value instanceof InputStream || value instanceof byte[] || value instanceof BinaryLargeObject) {
                    	if(value instanceof byte[]) {
                    		value = new ByteArrayInputStream((byte[])value);
                    	}
                    	else if(value instanceof BinaryLargeObject) {
                    		try {
                    			value = ((BinaryLargeObject)value).getContent();
                    		} catch(Exception e) {}
                    	}
                        String contentName = (String)objFacade.attributeValuesAsList(attribute + "Name").get(0);
                        String contentMimeType = (String)objFacade.attributeValuesAsList(attribute + "MimeType").get(0);
                        if(contentName != null) { 
                            // text/rtf
                            if(
                                "text/rtf".equals(contentMimeType) ||
                                contentName.endsWith(".rtf")
                            ) {
                                 try {
                                     text = new RTFToText().parse(
                                         (InputStream)value
                                     );
                                 }
                                 catch(Exception e) {
                                	 SysLog.warning("Cannot extract text from a RTF document", Arrays.asList(new String[]{contentName, e.getMessage()}));
                                 }
                            }
                            // application/pdf
                            else if(
                                "application/pdf".equals(contentMimeType) ||
                                contentName.endsWith(".pdf")
                            ) {
                                try {
                                    text = new PDFToText().parse(
                                        (InputStream)value
                                    );
                                }
                                catch(Exception e) {
                                	SysLog.warning("Can not extract text from PDF document", Arrays.asList(new String[]{contentName, e.getMessage()}));
                                }
                            }
                            // application/vnd.ms-excel
                            else if(
                               "application/vnd.ms-excel".equals(contentMimeType) ||
                               "application/ms-excel".equals(contentMimeType) ||
                                contentName.endsWith(".xls")
                            ) {
                                try {
                                    text = new ExcelToText().parse(
                                        (InputStream)value
                                    );
                                }
                                catch(Exception e) {
                                	SysLog.warning("Can not extract text from Excel document", Arrays.asList(new String[]{contentName, e.getMessage()}));
                                }
                            }
                            // application/vnd.ms-word
                            else if(
                               "application/vnd.ms-word".equals(contentMimeType) ||
                               "application/ms-word".equals(contentMimeType) ||
                                contentName.endsWith(".doc")
                            ) {
                                try {
                                    text = new WordToText().parse(
                                        (InputStream)value
                                    );
                                }
                                catch(Exception e) {
                                	SysLog.warning("Can not extract text from Word document", Arrays.asList(new String[]{contentName, e.getMessage()}));
                                }
                            }
                            // application/vnd.sun.xml.writer
                            // application/vnd.sun.xml.calc
                            // application/vnd.sun.xml.draw
                            // application/vnd.sun.xml.impress
                            // application/vnd.sun.xml.chart
                            // application/vnd.sun.xml.math
                            // application/vnd.sun.xml.writer.global
                            else if(
                                contentName.endsWith(".odt") ||
                                contentName.endsWith(".odp") ||
                                contentName.endsWith(".ods")
                            ) {
                                try {
                                    ZipInputStream document = new ZipInputStream((InputStream)value);
                                    text = new OpenOfficeToText().parse(
                                        document
                                    );
                                    isXml = true;
                                }
                                catch(Exception e) {
                                	SysLog.warning("Can not extract text from OpenOffice document", Arrays.asList(new String[]{contentName, e.getMessage()}));
                                }
                            }
                            // text/plain
                            else if(
                                "text/plain".equals(contentMimeType) ||
                                contentName.endsWith(".txt")
                            ) {
                                text = new InputStreamReader((InputStream)value);                                
                            }                            
                            // text/html, text/xml
                            else if(
                                "text/html".equals(contentMimeType) ||
                                "text/xml".equals(contentMimeType) ||
                                contentName.endsWith(".xml") || 
                                contentName.endsWith(".html") || 
                                contentName.endsWith(".htm")
                            ) {
                                text = new InputStreamReader((InputStream)value);           
                                isXml = true;
                            }                            
                        }
                    }
                    if(text != null) {
                        try {
                            int ch = text.read();
                            while(ch != -1) {
                                // Skip tags if xml
                                if(isXml && (ch == '<')) {
                                    while(
                                        (ch != -1) &&
                                        (ch != '>')
                                    ) {
                                        ch = text.read();
                                    }
                                    if(ch != -1) {
                                        ch = text.read();
                                    }
                                }
                                StringBuilder keyword = new StringBuilder();
                                boolean isKeyword = false;
                                while(
                                    (ch != -1) && 
                                    (!isXml || (isXml && ch != '<')) &&
                                    Character.isLetterOrDigit((char)ch) || (ch == '-') || (ch == '_')                                    
                                ) {
                                    keyword.append((char)ch);
                                    ch = text.read();        
                                    isKeyword = true;
                                }
                                if(!isKeyword && (!isXml || (ch != '<'))) {
                                    ch = text.read();
                                }
                                else if(
                                    (keyword.length() > MIN_KEYWORD_LENGTH) &&
                                    (keyword.length() < MAX_KEYWORD_LENGTH)
                                ) {
                                    keywords.add(keyword.toString().toLowerCase());
                                }
                            }
                        } 
                        catch(Exception e) {}
                    }
                }
            }
        }       
        return keywords;
    }

    //-------------------------------------------------------------------------
    public boolean isAccountAddress(
    	MappedRecord object
    ) throws ServiceException {
        String objectClass = Object_2Facade.getObjectClass(object);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:AccountAddress"
        );
    }

    // --------------------------------------------------------------------------
    public class LayerInteraction extends AbstractDatabase_1.LayerInteraction {
        
        //---------------------------------------------------------------------------
        public LayerInteraction(
            javax.resource.cci.Connection connection
        ) throws ResourceException {
            super(connection);
        }
            
	    //-----------------------------------------------------------------------
	    private void updateIndexEntry(
	        ServiceHeader header,
	        MappedRecord indexed
	    ) throws ServiceException {
	    	try {
		    	Path indexedPath = Object_2Facade.getPath(indexed);
		    	Object_2Facade indexedFacade = Object_2Facade.newInstance(indexed);
		    	MappedRecord indexEntry = Object_2Facade.newInstance(
		            Object_2Facade.getPath(indexed).getPrefix(5).getDescendant(new String[]{"indexEntry", super.uidAsString()}),
		            "org:opencrx:kernel:base:IndexEntry"
		        ).getDelegate();
		    	Object_2Facade indexEntryFacade = Object_2Facade.newInstance(indexEntry);
		        indexEntryFacade.attributeValuesAsList("indexedObject").add(
		            indexedPath
		        );
		        Set<String> keywords = Indexed_1.this.getKeywords(
		            indexed
		        );
		        // AccountAddress: add keywords of account
		        if(Indexed_1.this.isAccountAddress(indexed)) {
		        	DataproviderRequest getRequest = new DataproviderRequest(
	                    Object_2Facade.newInstance(indexedPath.getPrefix(indexedPath.size() - 2)).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
		        	DataproviderReply getReply = super.newDataproviderReply();
		        	super.get(
		        		getRequest.getInteractionSpec(), 
		        		Query_2Facade.newInstance(getRequest.path()), 
		        		getReply.getResult()
		        	);
		            keywords.addAll(	            	
		            	Indexed_1.this.getKeywords(
		                	getReply.getObject()
		                )
		            );
		        }
		        indexEntryFacade.attributeValuesAsList("keywords").add(
		            keywords.toString()
		        );
		        indexEntryFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT).addAll(
		            indexedFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT)
		        );
		        indexEntryFacade.attributeValuesAsList(SystemAttributes.MODIFIED_BY).addAll(
		            indexedFacade.attributeValuesAsList(SystemAttributes.MODIFIED_BY)
		        );
		        indexEntryFacade.attributeValuesAsList(SystemAttributes.CREATED_AT).addAll(
		        	indexedFacade.attributeValuesAsList(SystemAttributes.MODIFIED_AT)
		        );
		        indexEntryFacade.attributeValuesAsList(SystemAttributes.CREATED_BY).addAll(
		        	indexedFacade.attributeValuesAsList(SystemAttributes.MODIFIED_BY)
		        );
		        indexEntryFacade.attributeValuesAsList("owner").addAll(
		        	indexedFacade.attributeValuesAsList("owner")
		        );
		        indexEntryFacade.attributeValuesAsList("accessLevelBrowse").addAll(
		        	indexedFacade.attributeValuesAsList("accessLevelBrowse")
		        );
		        indexEntryFacade.attributeValuesAsList("accessLevelUpdate").add(
		            new Short(SecurityKeys.ACCESS_LEVEL_NA)
		        );
		        indexEntryFacade.attributeValuesAsList("accessLevelDelete").add(
		            new Short(SecurityKeys.ACCESS_LEVEL_NA)
		        );                
		        // Create entry
		        try {
		        	DataproviderRequest createRequest = new DataproviderRequest(
	                    indexEntry,
	                    DataproviderOperations.OBJECT_CREATION,
	                    AttributeSelectors.NO_ATTRIBUTES,
	                    null
	                );
		        	DataproviderReply createReply = super.newDataproviderReply();
		        	super.create(
		        		createRequest.getInteractionSpec(), 
		        		Object_2Facade.newInstance(createRequest.object()), 
		        		createReply.getResult()
		        	);
		        }
		        catch(ServiceException e) {
		            e.log();
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	try {
		        Path reference = request.path().getParent();
		        if("indexEntry".equals(reference.getBase())) {
		        	DataproviderRequest getRequest = new DataproviderRequest(
	                    Object_2Facade.newInstance(
	                        request.path().getPrefix(5).getDescendant(new String[]{"indexEntry", request.path().getBase()})
	                    ).getDelegate(),
	                    DataproviderOperations.OBJECT_RETRIEVAL,
	                    AttributeSelectors.ALL_ATTRIBUTES,
	                    null
	                );
		        	DataproviderReply getReply = super.newDataproviderReply();
		        	super.get(
		        		getRequest.getInteractionSpec(), 
		        		Query_2Facade.newInstance(getRequest.path()), 
		        		getReply.getResult()
		        	);
		        	MappedRecord indexEntry = getReply.getObject();            
		        	MappedRecord mappedIndexEntry = Object_2Facade.newInstance(
		                request.path()
		            ).getDelegate();
		            Object_2Facade.getValue(mappedIndexEntry).putAll(
		                Object_2Facade.getValue(indexEntry)
		            );
		            if(reply.getResult() != null) {
		            	reply.getResult().add(
		            		mappedIndexEntry
		            	);
		            }
		            return true;
		        }
		        else {
		            super.get(
		                ispec,
		                input,
		                output
		            );
		            return true;
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    protected MappedRecord[] findIndexEntries(
	        ServiceHeader header,
	        Path objectIdentity,
	        FilterProperty[] attributeFilter,
	        AttributeSpecifier[] attributeSpecifier
	    ) throws ServiceException {
	    	try {
		        // Find index entries assigned to requesting object,
		        // request.path().getParent() IS_IN indexedObject of index entry
		        List<FilterProperty> filterProperties = attributeFilter == null ? 
		        	new ArrayList<FilterProperty>() :
		        	new ArrayList<FilterProperty>(Arrays.asList(attributeFilter));
		        filterProperties.add(
		            new FilterProperty(
		                Quantors.THERE_EXISTS,
		                "indexedObject",
		                FilterOperators.IS_IN,
		                objectIdentity
		            )
		        );
		        DataproviderRequest findRequest = new DataproviderRequest(
	                Query_2Facade.newInstance(
	                    objectIdentity.getPrefix(5).getChild("indexEntry")
	               ).getDelegate(),
	               DataproviderOperations.ITERATION_START,
	               filterProperties.toArray(new FilterProperty[filterProperties.size()]),
	               0, 
	               Integer.MAX_VALUE,
	               Directions.ASCENDING,
	               AttributeSelectors.ALL_ATTRIBUTES,
	               attributeSpecifier
		        );
		        DataproviderReply findReply = super.newDataproviderReply();
		        super.find(
		        	findRequest.getInteractionSpec(), 
		        	Query_2Facade.newInstance(findRequest.object()), 
		        	findReply.getResult()
		        );
		        return findReply.getObjects();
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	try {
		        // If indexEntry is segment do not rewrite find request. Otherwise filter
		        // index entries with indexedObject = requested reference
		        if(
		            (request.path().size() > 6) &&
		            "indexEntry".equals(request.path().getBase())
		        ) {            
		        	MappedRecord[] indexEntries = this.findIndexEntries(
		                header, 
		                request.path().getParent(), 
		                request.attributeFilter(), 
		                request.attributeSpecifier()
		            );
		            // Remap index entries so that the parent of the mapped
		            // index entries is the requesting object, i.e. the indexed object
		            List<MappedRecord> mappedIndexEntries = new ArrayList<MappedRecord>();
		            for(
		                int i = 0; 
		                i < indexEntries.length; 
		                i++
		            ) {
		            	MappedRecord mappedIndexEntry = Object_2Facade.cloneObject(indexEntries[i]);
		            	Object_2Facade.newInstance(mappedIndexEntry).setPath(
		                    request.path().getChild(Object_2Facade.getPath(indexEntries[i]).getBase())
		                );
		                mappedIndexEntries.add(
		                    mappedIndexEntry
		                );
		            }
		            
		            // reply
		            if(reply.getResult() != null) {
		            	reply.getResult().addAll(
		            		mappedIndexEntries
		            	);
		            }
		            reply.setHasMore(Boolean.FALSE);
		            reply.setTotal(new Integer(mappedIndexEntries.size()));
		            return true;
		        }
		        else {
		            return super.find(
		                ispec,
		                input,
		                output
		            );
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	    	try {
		        // Remove index entries of object to be removed
		        if(request.path().size() > 5) {
		        	MappedRecord[] indexEntries = this.findIndexEntries(
		                 header, 
		                 request.path(), 
		                 null, 
		                 null
		            );
		            for(int i = 0; i < indexEntries.length; i++) {
		            	DataproviderRequest deleteRequest = new DataproviderRequest(
	                        Object_2Facade.newInstance(
	                            indexEntries[i]
	                       ).getDelegate(),
	                       DataproviderOperations.OBJECT_REMOVAL,
	                       AttributeSelectors.NO_ATTRIBUTES,
	                       null
	                    );
		            	DataproviderReply deleteReply = super.newDataproviderReply();
		            	super.delete(
		            		deleteRequest.getInteractionSpec(), 
		            		Object_2Facade.newInstance(deleteRequest.object()), 
		            		deleteReply.getResult()
		            	);
		            }
		        }
		        super.delete(
		            ispec,
		            input,
		            output
		        );
		        return true;
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
	    //-----------------------------------------------------------------------
	    protected MappedRecord otherOperation(
	        ServiceHeader header,
	        DataproviderRequest request,
	        String operation, 
	        Path replyPath
	    ) throws ServiceException {
	    	try {
		        if("updateIndex".equals(operation)) {
		            Path indexedIdentity = request.path().getPrefix(request.path().size() - 2);
		            int numberOfIndexedObjects = 0;
		            // At segment level update all objects to be indexed (up to a batch size)
		            if(indexedIdentity.size() == 5) {
		                for(Path type: Indexed_1.this.indexableTypes) {
		                    // Type must be composite to indexed segment
		                    if(
		                        ":*".equals(type.get(0)) ||
		                        type.get(0).equals(indexedIdentity.get(0))
		                    ) {
		                        Path concreteType = new Path("");
		                        for(int i = 0; i < type.size(); i++) {
		                            // authority
		                            if(i == 0) {
		                                concreteType.add(indexedIdentity.get(0));
		                            }
		                            // provider name
		                            else if(i == 2) {
		                                concreteType.add(indexedIdentity.get(2));
		                            }
		                            // segment name
		                            else if(i == 4) {
		                                concreteType.add(indexedIdentity.get(4));
		                            }
		                            else {
		                                concreteType.add(type.get(i));
		                            }
		                        }
		                        String queryFilterContext = SystemAttributes.CONTEXT_PREFIX + this.uidAsString() + ":";
		                        DataproviderRequest findRequest = new DataproviderRequest(
		                            Query_2Facade.newInstance(indexedIdentity.getChild("extent")).getDelegate(),
		                            DataproviderOperations.ITERATION_START,
		                            new FilterProperty[]{
		                                new FilterProperty(
		                                    Quantors.THERE_EXISTS,
		                                    SystemAttributes.OBJECT_IDENTITY,
		                                    FilterOperators.IS_LIKE,
		                                    new Object[]{
		                                        concreteType.toUri()
		                                    }
		                                ),
		                                // All objects which do not have an up-to-date index entry
		                                new FilterProperty(
		                                    Quantors.PIGGY_BACK,
		                                    queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE,
		                                    FilterOperators.PIGGY_BACK,
		                                    new Object[]{
		                                    	Database_1_Attributes.HINT_COLUMN_SELECTOR + " v.object_id, v.dtype */ NOT EXISTS (SELECT 0 FROM OOCKE1_INDEXENTRY e WHERE v.object_id = e.indexed_object AND v.modified_at <= e.created_at)"
		                                    }
		                                ),
		                                new FilterProperty(
		                                    Quantors.PIGGY_BACK,
		                                    queryFilterContext + SystemAttributes.OBJECT_CLASS,
		                                    FilterOperators.PIGGY_BACK,
		                                    new Object[]{Database_1_Attributes.QUERY_FILTER_CLASS}
		                                )
		                            },
		                            0,
		                            BATCH_SIZE,
		                            Directions.ASCENDING,
		                            AttributeSelectors.NO_ATTRIBUTES,
		                            null
		                        );
		                        DataproviderReply findReply = super.newDataproviderReply();
	                            SysLog.detail("> Finding objects to be indexed " + findRequest.object());		                        			                        
		                        super.find(
		                        	findRequest.getInteractionSpec(), 
		                        	Query_2Facade.newInstance(findRequest.object()), 
		                        	findReply.getResult()
		                        );
		                        MappedRecord[] objectsToBeIndexed = findReply.getObjects();
	                            SysLog.detail("Indexing #objects", objectsToBeIndexed.length);		                        	
		                        for(int i = 0; i < objectsToBeIndexed.length; i++) {
		                            try {
		                            	Path objectToBeIndexedPath = Object_2Facade.getPath(objectsToBeIndexed[i]);
		                            	DataproviderRequest getRequest = new DataproviderRequest(
		            	                    Query_2Facade.newInstance(objectToBeIndexedPath).getDelegate(),
		            	                    DataproviderOperations.OBJECT_RETRIEVAL,
		            	                    AttributeSelectors.ALL_ATTRIBUTES,
		            	                    null		                            		
		                            	);
		                            	DataproviderReply getReply = super.newDataproviderReply();
		                            	super.get(
		                            		getRequest.getInteractionSpec(), 
		                            		Query_2Facade.newInstance(getRequest.object()), 
		                            		getReply.getResult()
		                            	);
		                                this.updateIndexEntry(
		                                    header, 
		                                    getReply.getObject()
		                                );
		                                numberOfIndexedObjects++;
		                            }
		                            catch(Exception e) {
		                            	SysLog.warning("Can not index", objectsToBeIndexed[i]);
		                            	SysLog.info(e.getMessage(), e.getCause());
		                            }
		                        }
		                    }
		                }
		            }
		            // Index object
		            else {
		            	DataproviderRequest getRequest = new DataproviderRequest(
	                        Query_2Facade.newInstance(indexedIdentity).getDelegate(),
	                        DataproviderOperations.OBJECT_RETRIEVAL,
	                        AttributeSelectors.ALL_ATTRIBUTES,
	                        null
	                    );
		            	DataproviderReply getReply = super.newDataproviderReply();
		            	super.get(
		            		getRequest.getInteractionSpec(), 
		            		Query_2Facade.newInstance(getRequest.path()), 
		            		getReply.getResult()
		            	);
		            	MappedRecord indexed = getReply.getObject();
		                this.updateIndexEntry(
		                    header,
		                    indexed
		                );
		                numberOfIndexedObjects++;
		            }
		            MappedRecord reply = Indexed_1.this.createResult(
		                request,
		                "org:opencrx:kernel:base:UpdateIndexResult"
		            );
		            Object_2Facade.newInstance(reply).attributeValuesAsList("numberOfIndexedObjects").add(
		                new Integer(numberOfIndexedObjects)
		            );
		            return reply;
		        }
		        // Delegate
		        else {
		            return super.otherOperation(
		                header,
		                request,
		                operation,
		                replyPath
		            );
		        }
	    	}
	    	catch(ResourceException e) {
	    		throw new ServiceException(e);
	    	}
	    }
	    
    }
    
    //-----------------------------------------------------------------------
    protected static final int MIN_KEYWORD_LENGTH = 3;
    protected static final int MAX_KEYWORD_LENGTH = 40;
    protected static final int BATCH_SIZE = 50;
    
    protected Set<Path> indexableTypes;
    
    protected static final Set<String> INDEXED_ATTRIBUTES =
        new HashSet<String>(
            Arrays.asList(
                "name",
                "description",
                "detailedDescription",
                "aliasName",
                "fullName",
                "postalAddressLine",
                "postalStreet",
                "postalCity",
                "postalCode",
                "emailAddress",
                "webUrl",
                "phoneNumberFull",
                "text",
                "content",
                "contractNumber",
                "activityNumber",
                "externalLink"
            )
        );
    
}

//--- End of File -----------------------------------------------------------

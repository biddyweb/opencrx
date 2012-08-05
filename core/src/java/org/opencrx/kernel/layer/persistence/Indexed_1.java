/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Indexed_1.java,v 1.27 2009/02/10 16:34:26 wfro Exp $
 * Description: openCRX indexing plugin
 * Revision:    $Revision: 1.27 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/10 16:34:26 $
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

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.layer.persistence.jdo.ObjectIdBuilder;
import org.opencrx.kernel.text.ExcelToText;
import org.opencrx.kernel.text.OpenOfficeToText;
import org.opencrx.kernel.text.PDFToText;
import org.opencrx.kernel.text.RTFToText;
import org.opencrx.kernel.text.WordToText;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderReplyContexts;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.Directions;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1;
import org.openmdx.compatibility.base.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.kernel.text.StringBuilders;

/**
 * This plugin creates audit entries for modified objects.
 */
public class Indexed_1 extends Database_1 {

    //-------------------------------------------------------------------------
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws ServiceException {
        super.activate(
            id, 
            configuration, 
            delegation
        );
        // Types to be indexed
        this.indexableTypes = new TreeSet<Path>();
        Model_1_0 model = this.getModel();
        for(int i = 0; i < ObjectIdBuilder.TYPES.length; i++) {
            Path type = ObjectIdBuilder.TYPES[i];
            boolean hasWildcardReferences = false;
            for(int j = 1; !hasWildcardReferences && (j < type.size()); j+=2) {
                hasWildcardReferences = ":*".equals(type.get(j));
            }
            if(!hasWildcardReferences) {
                List<String> className = ObjectIdBuilder.CLASS_NAMES[i]; 
                if(
                    (className != null) && 
                    model.isSubtypeOf(className, "org:opencrx:kernel:base:Indexed") &&
                    !model.isSubtypeOf(className, "org:openmdx:base:Segment")
                ) {
                    this.indexableTypes.add(type);
                }
            }
        }
        // Manually add some more
        this.indexableTypes.addAll(
            Arrays.asList(
                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/followUp/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/followUp/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/followUp/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/address/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/note/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/media/:*"),
                new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/media/:*")
            )
        );            
    }
    
    //-------------------------------------------------------------------------
    private DataproviderObject createResult(
      DataproviderRequest request,
      String structName
    ) {
      DataproviderObject result = new DataproviderObject(
        request.path().getDescendant(
          new String[]{ "reply", super.uidAsString()}
        )
      );
      result.clearValues(SystemAttributes.OBJECT_CLASS).add(
        structName
      );
      return result;
    }

    //-------------------------------------------------------------------------
    private Set<String> getKeywords(
        DataproviderObject_1_0 obj
    ) {
        Set<String> keywords = new HashSet<String>();
        for(String attribute: INDEXED_ATTRIBUTES) {
            if(obj.attributeNames().contains(attribute)) {
                for(
                    Iterator j = obj.values(attribute).iterator(); 
                    j.hasNext(); 
                ) {
                    Object value = j.next();
                    Reader text = null;
                    boolean isXml = false;
                    if(value instanceof String) {
                        text = new StringReader((String)value);
                    }
                    else if(value instanceof InputStream) {
                        String contentName = (String)obj.values(attribute + "Name").get(0);
                        String contentMimeType = (String)obj.values(attribute + "MimeType").get(0);
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
                                     AppLog.warning("Cannot extract text from a RTF document", Arrays.asList(new String[]{contentName, e.getMessage()}));
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
                                    AppLog.warning("Can not extract text from PDF document", Arrays.asList(new String[]{contentName, e.getMessage()}));
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
                                    AppLog.warning("Can not extract text from Excel document", Arrays.asList(new String[]{contentName, e.getMessage()}));
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
                                    AppLog.warning("Can not extract text from Word document", Arrays.asList(new String[]{contentName, e.getMessage()}));
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
                                    AppLog.warning("Can not extract text from OpenOffice document", Arrays.asList(new String[]{contentName, e.getMessage()}));
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
                                CharSequence keyword = StringBuilders.newStringBuilder();
                                boolean isKeyword = false;
                                while(
                                    (ch != -1) && 
                                    (!isXml || (isXml && ch != '<')) &&
                                    Character.isLetterOrDigit((char)ch) || (ch == '-') || (ch == '_')                                    
                                ) {
                                    StringBuilders.asStringBuilder(keyword).append((char)ch);
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
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:AccountAddress"
        );
    }

    //-----------------------------------------------------------------------
    private void updateIndexEntry(
        ServiceHeader header,
        DataproviderObject_1_0 indexed
    ) throws ServiceException {
        DataproviderObject indexEntry = new DataproviderObject(
            indexed.path().getPrefix(5).getDescendant(new String[]{"indexEntry", super.uidAsString()})
        );
        indexEntry.values(SystemAttributes.OBJECT_CLASS).add(
            "org:opencrx:kernel:base:IndexEntry"
        );
        indexEntry.values("indexedObject").add(
            indexed.path()
        );
        Set keywords = this.getKeywords(
            indexed
        );
        // AccountAddress: add keywords of account
        if(this.isAccountAddress(indexed)) {
            keywords.addAll(
                this.getKeywords(
                    super.get(
                        header,
                        new DataproviderRequest(
                            new DataproviderObject(indexed.path().getPrefix(indexed.path().size() - 2)),
                            DataproviderOperations.OBJECT_RETRIEVAL,
                            AttributeSelectors.ALL_ATTRIBUTES,
                            null
                        )             
                    ).getObject()                    
                )
            );
        }
        indexEntry.values("keywords").add(
            keywords.toString()
        );
        indexEntry.values(SystemAttributes.MODIFIED_AT).addAll(
            indexed.values(SystemAttributes.MODIFIED_AT)
        );
        indexEntry.values(SystemAttributes.MODIFIED_BY).addAll(
            indexed.values(SystemAttributes.MODIFIED_BY)
        );
        indexEntry.values(SystemAttributes.CREATED_AT).addAll(
            indexed.values(SystemAttributes.MODIFIED_AT)
        );
        indexEntry.values(SystemAttributes.CREATED_BY).addAll(
            indexed.values(SystemAttributes.MODIFIED_BY)
        );
        indexEntry.values("owner").addAll(
            indexed.values("owner")
        );
        indexEntry.values("accessLevelBrowse").addAll(
            indexed.values("accessLevelBrowse")
        );
        indexEntry.values("accessLevelUpdate").add(
            new Short(SecurityKeys.ACCESS_LEVEL_NA)
        );
        indexEntry.values("accessLevelDelete").add(
            new Short(SecurityKeys.ACCESS_LEVEL_NA)
        );                
        // Create entry
        try {
            super.create(
                header,
                new DataproviderRequest(
                    indexEntry,
                    DataproviderOperations.OBJECT_CREATION,
                    AttributeSelectors.NO_ATTRIBUTES,
                    null
                )
            );
        }
        catch(ServiceException e) {
            e.log();
        }
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply get(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        Path reference = request.path().getParent();
        if("indexEntry".equals(reference.getBase())) {            
            DataproviderObject indexEntry = super.get(
                header,
                new DataproviderRequest(
                    new DataproviderObject(
                        request.path().getPrefix(5).getDescendant(new String[]{"indexEntry", request.path().getBase()})
                    ),
                    DataproviderOperations.OBJECT_RETRIEVAL,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    null
                )
            ).getObject();            
            DataproviderObject mappedIndexEntry = new DataproviderObject(
                request.path()
            );
            mappedIndexEntry.addClones(
                indexEntry,
                true
            );
            return new DataproviderReply(
                mappedIndexEntry
            );
        }
        else {
            return super.get(
                header, 
                request
            );
        }
    }
    
    //-----------------------------------------------------------------------
    protected DataproviderObject[] findIndexEntries(
        ServiceHeader header,
        Path objectIdentity,
        FilterProperty[] attributeFilter,
        AttributeSpecifier[] attributeSpecifier
    ) throws ServiceException {
        // Find index entries assigned to requesting object,
        // request.path().getParent() IS_IN indexedObject of index entry
        List filterProperties = attributeFilter == null
            ? new ArrayList()
            : new ArrayList(Arrays.asList(attributeFilter));
        filterProperties.add(
            new FilterProperty(
                Quantors.THERE_EXISTS,
                "indexedObject",
                FilterOperators.IS_IN,
                objectIdentity
            )
        );
        return super.find(
            header,
            new DataproviderRequest(
                new DataproviderObject(
                     objectIdentity.getPrefix(5).getChild("indexEntry")
                ),
                DataproviderOperations.ITERATION_START,
                (FilterProperty[])filterProperties.toArray(new FilterProperty[filterProperties.size()]),
                0, 
                Integer.MAX_VALUE,
                Directions.ASCENDING,
                AttributeSelectors.ALL_ATTRIBUTES,
                attributeSpecifier
            )
        ).getObjects();        
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        // If indexEntry is segment do not rewrite find request. Otherwise filter
        // index entries with indexedObject = requested reference
        if(
            (request.path().size() > 6) &&
            "indexEntry".equals(request.path().getBase())
        ) {            
            DataproviderObject[] indexEntries = this.findIndexEntries(
                header, 
                request.path().getParent(), 
                request.attributeFilter(), 
                request.attributeSpecifier()
            );
            // Remap index entries so that the parent of the mapped
            // index entries is the requesting object, i.e. the indexed object
            List mappedIndexEntries = new ArrayList();
            for(
                int i = 0; 
                i < indexEntries.length; 
                i++
            ) {
                DataproviderObject mappedIndexEntry = new DataproviderObject(
                    request.path().getChild(indexEntries[i].path().getBase())
                );
                mappedIndexEntry.addClones(
                    indexEntries[i],
                    true
                );
                mappedIndexEntries.add(
                    mappedIndexEntry
                );
            }
            
            // reply
            DataproviderReply reply = new DataproviderReply(
                mappedIndexEntries
            );
            reply.context(
                DataproviderReplyContexts.HAS_MORE
            ).set(0, Boolean.FALSE);
            reply.context(
                DataproviderReplyContexts.TOTAL
            ).set(
                0, 
                new Integer(mappedIndexEntries.size())
            );
            reply.context(DataproviderReplyContexts.ATTRIBUTE_SELECTOR).set(
                0,
                new Short(request.attributeSelector())
            );
            return reply;            
        }
        else {
            return super.find(
                header, 
                request
            );
        }
    }
    
    //-----------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        // Remove index entries of object to be removed
        if(request.path().size() > 5) {
            DataproviderObject[] indexEntries = this.findIndexEntries(
                 header, 
                 request.path(), 
                 null, 
                 null
            );
            for(int i = 0; i < indexEntries.length; i++) {
                super.remove(
                    header, 
                    new DataproviderRequest(
                        new DataproviderObject(
                            indexEntries[i]
                       ),
                       DataproviderOperations.OBJECT_REMOVAL,
                       AttributeSelectors.NO_ATTRIBUTES,
                       null
                    )
                );
            }
        }
        return super.remove(
            header, 
            request
        );
    }
    
    //-----------------------------------------------------------------------
    protected DataproviderObject otherOperation(
        ServiceHeader header,
        DataproviderRequest request,
        String operation, 
        Path replyPath
    ) throws ServiceException {
        if("updateIndex".equals(operation)) {
            Path indexedIdentity = request.path().getPrefix(request.path().size() - 2);
            int numberOfIndexedObjects = 0;
            // At segment level update all objects to be indexed (up to a batch size)
            if(indexedIdentity.size() == 5) {
                for(Path type: this.indexableTypes) {
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
                        DataproviderRequest queryIsNotIndexed = new DataproviderRequest(
                            new DataproviderObject(indexedIdentity.getChild("extent")),
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
                                        "NOT EXISTS (SELECT 0 FROM OOCKE1_INDEXENTRY e WHERE v.object_id = e.indexed_object AND v.modified_at <= e.created_at)"
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
                            AttributeSelectors.ALL_ATTRIBUTES,
                            null
                        );
                        DataproviderObject[] objectsToBeIndexed = super.find(
                            header, 
                            queryIsNotIndexed
                        ).getObjects();
                        for(int i = 0; i < objectsToBeIndexed.length; i++) {
                            try {
                                this.updateIndexEntry(
                                    header, 
                                    objectsToBeIndexed[i]
                                );
                                numberOfIndexedObjects++;
                            }
                            catch(Exception e) {
                                AppLog.warning("Can not index", objectsToBeIndexed[i]);
                                AppLog.info(e.getMessage(), e.getCause());
                            }
                        }
                    }
                }
            }
            // Index object
            else {
                DataproviderObject indexed = super.get(
                    header,
                    new DataproviderRequest(
                        new DataproviderObject(indexedIdentity),
                        DataproviderOperations.OBJECT_RETRIEVAL,
                        AttributeSelectors.ALL_ATTRIBUTES,
                        null
                    )             
                ).getObject();
                this.updateIndexEntry(
                    header,
                    indexed
                );
                numberOfIndexedObjects++;
            }
            DataproviderObject reply = this.createResult(
                request,
                "org:opencrx:kernel:base:UpdateIndexResult"
            );
            reply.values("numberOfIndexedObjects").add(
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
    
    //-----------------------------------------------------------------------
    private static final int MIN_KEYWORD_LENGTH = 3;
    private static final int MAX_KEYWORD_LENGTH = 40;
    private static final int BATCH_SIZE = 50;
    
    private Set<Path> indexableTypes;
    
    private static final Set<String> INDEXED_ATTRIBUTES =
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

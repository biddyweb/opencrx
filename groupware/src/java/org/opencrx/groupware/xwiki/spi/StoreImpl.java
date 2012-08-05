/*
 * ====================================================================
 * Project:     openCRX/Groupware, http://www.opencrx.org/
 * Name:        $Id: StoreImpl.java,v 1.51 2008/09/12 08:53:31 wfro Exp $
 * Description: XWiki StoreImpl
 * Revision:    $Revision: 1.51 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/12 08:53:31 $
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opencrx.kernel.base.cci2.IndexEntryQuery;
import org.opencrx.kernel.base.cci2.StringPropertyQuery;
import org.opencrx.kernel.base.jmi1.IndexEntry;
import org.opencrx.kernel.document1.cci2.DocumentFolderQuery;
import org.opencrx.kernel.document1.cci2.DocumentLinkQuery;
import org.opencrx.kernel.document1.cci2.DocumentQuery;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentAttachment;
import org.opencrx.kernel.document1.jmi1.DocumentFolder;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.cci2.BinaryLargeObjects;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiLink;
import com.xpn.xwiki.doc.XWikiLock;
import com.xpn.xwiki.monitor.api.MonitorPlugin;
import com.xpn.xwiki.objects.BaseCollection;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.DBStringListProperty;
import com.xpn.xwiki.objects.DateProperty;
import com.xpn.xwiki.objects.DoubleProperty;
import com.xpn.xwiki.objects.IntegerProperty;
import com.xpn.xwiki.objects.LargeStringProperty;
import com.xpn.xwiki.objects.LongProperty;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.StringListProperty;
import com.xpn.xwiki.objects.StringProperty;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.render.XWikiRenderer;
import com.xpn.xwiki.store.XWikiBatcherStats;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.store.XWikiStoreInterface;
import com.xpn.xwiki.util.Util;
import com.xpn.xwiki.web.Utils;

public class StoreImpl 
    extends AbstractStoreImpl 
    implements XWikiStoreInterface  {

    //-----------------------------------------------------------------------
    public StoreImpl(
        XWikiContext context
    ) throws XWikiException {
        super(
            context
        );
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            // Assert root folder. XWiki spaces are mapped to document folders which
            // all have the same root folder as parent. This allows to retrieve all
            // document folders which are XWiki spaces
            DocumentFolder folder = this.getRootFolder(session);
            if(folder == null) {
                session.beginTransaction();
                folder = session.getDocumentPackage().getDocumentFolder().createDocumentFolder();
                folder.refInitialize(false, false);
                folder.setName(ROOT_FOLDER_NAME);
                session.commitTransaction();
            }
        }
        catch(Exception e) {
            try {
                if(session != null) session.rollbackTransaction();
            } catch(Exception e0) {}
        }
    }

    //-----------------------------------------------------------------------
    protected void deleteXWikiBaseObject(
        Document document,
        BaseCollection xWikiObject, 
        XWikiContext context, 
        OpenCrxSession session
    ) throws XWikiException {
        try {
            String propertySetName = this.getPropertySetName(xWikiObject);
            for(org.opencrx.kernel.document1.jmi1.PropertySet ps: this.findPropertySet(document, propertySetName, session)) {
                ps.refDelete();
            }
        } 
        catch (Exception e) {
            Object[] args = { xWikiObject.getName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_OBJECT,
                "Exception while deleting object {0}", 
                e, 
                args
             );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void loadXWikiBaseObject(
        org.opencrx.kernel.base.jmi1.PropertySet ps,
        BaseCollection xWikiObject, 
        XWikiContext context,
        OpenCrxSession session
    ) throws XWikiException {   
        try {
            org.opencrx.kernel.base.cci2.PropertyQuery query = session.getBasePackage().createPropertyQuery();
            query.orderByName().ascending();
            // Collect indexed properties and map them to one XWikiProperty
            // Indexed properties are e.g. used for storing large strings
            List<org.opencrx.kernel.base.jmi1.Property> values = new ArrayList<org.opencrx.kernel.base.jmi1.Property>();
            List<org.opencrx.kernel.base.jmi1.Property> properties = (List<org.opencrx.kernel.base.jmi1.Property>)ps.getProperty(query);
            String prevPropertyName = null;
            for(int i = 0; i < properties.size(); i++) {
                org.opencrx.kernel.base.jmi1.Property p = properties.get(i);
                String propertyName = p.getName();
                if(propertyName.indexOf("#") > 0) {
                    propertyName = propertyName.substring(0, propertyName.indexOf("#"));
                }
                if(
                    (prevPropertyName != null) && 
                    !propertyName.equals(prevPropertyName) // next property
                ) {
                    BaseProperty xWikiProperty = this.loadXWikiBaseObjectProperty(
                        values, 
                        context, 
                        false
                    );
                    if(xWikiProperty != null) {
                        xWikiProperty.setObject(xWikiObject);
                        xWikiProperty.setName(prevPropertyName);
                        xWikiObject.addField(
                            prevPropertyName, 
                            xWikiProperty
                        );
                    }
                    values.clear();
                }
                values.add(p);
                prevPropertyName = propertyName;
            }
            if(!values.isEmpty()) {
                BaseProperty xWikiProperty = this.loadXWikiBaseObjectProperty(
                    values, 
                    context, 
                    false
                );
                if(xWikiProperty != null) {
                    xWikiProperty.setObject(xWikiObject);
                    xWikiProperty.setName(prevPropertyName);
                    xWikiObject.addField(
                        prevPropertyName, 
                        xWikiProperty
                    );
                }                
            }
        } 
        catch (Exception e) {
            Object[] args = { xWikiObject.getName(), xWikiObject.getClass(), Integer.valueOf(xWikiObject.getNumber() + "") };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while loading object '{0}' of class '{1}' and number '{2}'", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected String listToString(
        List values
    ) {
        StringBuilder s = new StringBuilder(ARRAY_PREFIX);
        for(Object value: values) {
            s.append(value.toString()).append(ARRAY_SEPARATOR);
        }
        return s.toString();
    }
    
    //-----------------------------------------------------------------------
    protected List stringToList(
        String s
    ) {
        List l = new ArrayList();
        if((s != null) && s.startsWith(ARRAY_PREFIX)) {
            String[] values = s.substring(ARRAY_PREFIX.length()).split(ARRAY_SEPARATOR);
            l.addAll(
                Arrays.asList(values)
            );
        }
        return l;
    }
    
    //-----------------------------------------------------------------------
    protected BaseProperty loadXWikiBaseObjectProperty(
        List<org.opencrx.kernel.base.jmi1.Property> values, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        org.opencrx.kernel.base.jmi1.Property p = values.get(0);
        try {
            BaseProperty xWikiProperty = null;
            if(p instanceof org.opencrx.kernel.base.jmi1.StringProperty) {
                String value = ((org.opencrx.kernel.base.jmi1.StringProperty)p).getStringValue();
                if((value != null) && value.startsWith(ARRAY_PREFIX)) {
                    xWikiProperty = new DBStringListProperty();
                    xWikiProperty.setValue(
                        this.stringToList(value)
                    );
                }
                else {
                    for(int i = 1; i < values.size(); i++) {
                        value += ((org.opencrx.kernel.base.jmi1.StringProperty)values.get(i)).getStringValue();
                    }
                    xWikiProperty = new StringProperty();
                    xWikiProperty.setValue(value);
                }
            }
            else if(p instanceof org.opencrx.kernel.base.jmi1.IntegerProperty) {
                xWikiProperty = new IntegerProperty();
                xWikiProperty.setValue(((org.opencrx.kernel.base.jmi1.IntegerProperty)p).getIntegerValue());
            }
            else if(p instanceof org.opencrx.kernel.base.jmi1.DecimalProperty) {
                xWikiProperty = new DoubleProperty();
                xWikiProperty.setValue(((org.opencrx.kernel.base.jmi1.DecimalProperty)p).getDecimalValue());
            }
            else if(p instanceof org.opencrx.kernel.base.jmi1.DateTimeProperty) {
                xWikiProperty = new DateProperty();
                xWikiProperty.setValue(((org.opencrx.kernel.base.jmi1.DateTimeProperty)p).getDateTimeValue());
            }
            if(xWikiProperty != null) {
                xWikiProperty.setName(p.getName());
            }
            return xWikiProperty;
        }
        catch (Exception e) {
            Object[] args = { (p != null) ? p.getName() : "unknown", p.getName() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while loading property {1} of object {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void saveXWikiBaseObject(
        Document document,
        BaseCollection xWikiObject, 
        XWikiContext context, 
        OpenCrxSession session
    ) throws XWikiException {
        try {
            if (xWikiObject==null) return;
            org.opencrx.kernel.document1.jmi1.PropertySet ps = null;
            String name = this.getPropertySetName(xWikiObject);            
            List<org.opencrx.kernel.document1.jmi1.PropertySet> pss = this.findPropertySet(
                document, 
                name, 
                session
            );
            if(!pss.isEmpty()) {
                ps = pss.get(0);
            }
            boolean isInternal = "internal".equals(xWikiObject.getClassName());
            if(ps == null) {
                ps = session.getDocumentPackage().getPropertySet().createPropertySet();
                ps.refInitialize(false, false);
                ps.setName(name);
                document.addPropertySet(
                    false,
                    this.uuidAsString(),
                    ps
                );
            }
            // Remove existing properties
            else if(!isInternal) {
                Collection<org.opencrx.kernel.base.jmi1.Property> properties = ps.getProperty();
                for(org.opencrx.kernel.base.jmi1.Property p: properties) {
                    p.refDelete();
                }
                xWikiObject.setFieldsToRemove(new ArrayList());
            }
            // Add new properties
            if(!isInternal) {
                Set<String> handledProps = new HashSet<String>();
                for(String key: (Set<String>)xWikiObject.getPropertyList()) {
                    BaseProperty xWikiProperty = (BaseProperty) xWikiObject.getField(key);
                    if (!xWikiProperty.getName().equals(key)) {
                        Object[] args = { key, xWikiObject.getName() };
                        throw new XWikiException(
                            XWikiException.MODULE_XWIKI_CLASSES, 
                            XWikiException.ERROR_XWIKI_CLASSES_FIELD_INVALID,
                            "Field {0} in object {1} has an invalid name", 
                            null, 
                            args
                        );
                    }
                    String pname = xWikiProperty.getName();
                    if(pname != null && !pname.trim().equals("") && !handledProps.contains(pname) ){
                        this.saveXWikiBaseObjectProperty(
                            ps,
                            xWikiProperty, 
                            context,
                            session
                        );
                    }
                }
            }
        } 
        catch (XWikiException xe) {
            throw xe;
        } 
        catch (Exception e) {
            Object[] args = { xWikiObject.getName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_OBJECT,
                "Exception while saving object {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void saveXWikiBaseObjectProperty(
        org.opencrx.kernel.base.jmi1.PropertySet ps,
        PropertyInterface xWikiProperty, 
        XWikiContext context, 
        OpenCrxSession session
    ) throws XWikiException {
        try {
            org.opencrx.kernel.base.jmi1.Property[] properties = new org.opencrx.kernel.base.jmi1.Property[1];
            if(xWikiProperty instanceof StringProperty) {
                org.opencrx.kernel.base.jmi1.StringProperty p = session.getBasePackage().getStringProperty().createStringProperty();
                p.setStringValue(
                    (String)((StringProperty)xWikiProperty).getValue()
                );
                properties[0] = p;
            }
            else if(xWikiProperty instanceof LargeStringProperty) {
                // Break up large strings into parts with size of LARGE_STRING_PART_SIZE
                String stringValue = (String)((LargeStringProperty)xWikiProperty).getValue();
                int nParts = (stringValue.length() / LARGE_STRING_PART_SIZE) + 1;
                properties = new org.opencrx.kernel.base.jmi1.StringProperty[nParts];
                for(int i = 0; i < nParts; i++) {
                    org.opencrx.kernel.base.jmi1.StringProperty p = session.getBasePackage().getStringProperty().createStringProperty();
                    p.setStringValue(
                        stringValue.substring(
                            LARGE_STRING_PART_SIZE * i, 
                            Math.min(stringValue.length(), LARGE_STRING_PART_SIZE * (i + 1))
                        )
                    );
                    properties[i] = p;
                }
            }
            else if(xWikiProperty instanceof StringListProperty) {
                org.opencrx.kernel.base.jmi1.StringProperty p = session.getBasePackage().getStringProperty().createStringProperty();
                List values = ((StringListProperty)xWikiProperty).getList();
                p.setStringValue(
                    this.listToString(values)
                );
                properties[0] = p;
            }
            else if(xWikiProperty instanceof DBStringListProperty) {
                org.opencrx.kernel.base.jmi1.StringProperty p = session.getBasePackage().getStringProperty().createStringProperty();
                List values = ((DBStringListProperty)xWikiProperty).getList();
                p.setStringValue(
                     this.listToString(values)
                );
                properties[0] = p;
            }
            else if(xWikiProperty instanceof IntegerProperty) {
                org.opencrx.kernel.base.jmi1.IntegerProperty p = session.getBasePackage().getIntegerProperty().createIntegerProperty();
                Number value = (Number)((IntegerProperty)xWikiProperty).getValue();
                p.setIntegerValue(
                    value == null ? 1 : value.intValue()
                );                
                properties[0] = p;
            }
            else if(xWikiProperty instanceof LongProperty) {
                org.opencrx.kernel.base.jmi1.IntegerProperty p = session.getBasePackage().getIntegerProperty().createIntegerProperty();
                Number value = (Number)((LongProperty)xWikiProperty).getValue();
                p.setIntegerValue(
                    value == null ? 1 : value.intValue()
                );                
                properties[0] = p;
            }
            else if(xWikiProperty instanceof DoubleProperty) {
                org.opencrx.kernel.base.jmi1.DecimalProperty p = session.getBasePackage().getDecimalProperty().createDecimalProperty();
                Number value = (Number)((DoubleProperty)xWikiProperty).getValue();
                p.setDecimalValue(
                    value == null ? new BigDecimal(0.0) : new BigDecimal(value.doubleValue())
                );                
                properties[0] = p;
            }
            else if(xWikiProperty instanceof DateProperty) {
                org.opencrx.kernel.base.jmi1.DateTimeProperty p = session.getBasePackage().getDateTimeProperty().createDateTimeProperty();
                p.setDateTimeValue(
                    (Date)((DateProperty)xWikiProperty).getValue()
                );                
                properties[0] = p;
            }
            else {
                throw new UnsupportedOperationException("Unsupported property type " + xWikiProperty.getClass().getName());
            }
            for(int i = 0; i < properties.length; i++) {
                if(properties[i] != null) {
                    properties[i].setName(
                        xWikiProperty.getName() + (properties.length > 1 ? "#" + i : "")
                    );
                    ps.addProperty(
                        false,
                        this.uuidAsString(),
                        properties[i]
                    );
                }
            }
        }
        catch (Exception e) {
            BaseCollection obj = xWikiProperty.getObject();
            Object[] args = { (obj!=null) ? obj.getName() : "unknown", xWikiProperty.getName() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_OBJECT,
                "Exception while saving property {1} of object {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void saveAttachment(
        Document document,
        XWikiAttachment xWikiAttachment, 
        XWikiContext context, 
        OpenCrxSession session
    ) throws XWikiException {
        try {
            List<org.opencrx.kernel.document1.jmi1.DocumentAttachment> attachments = this.findAttachment(
                document, 
                xWikiAttachment, 
                session
            );
            org.opencrx.kernel.document1.jmi1.DocumentAttachment attachment = null;
            if(attachments.isEmpty()) {
                attachment = session.getDocumentPackage().getDocumentAttachment().createDocumentAttachment();
                attachment.refInitialize(false, false);
                document.addAttachment(
                    false,
                    this.uuidAsString(),
                    attachment
                );
            }
            else {
                attachment = attachments.get(0);
            }
            // Update
            attachment.setName(Long.toString(xWikiAttachment.getId()));            
            attachment.setContentName(xWikiAttachment.getFilename());
            attachment.setAuthor(xWikiAttachment.getAuthor());
            attachment.setVersion(xWikiAttachment.getVersion());
            attachment.setDescription(xWikiAttachment.getComment());
            // Content is saved by AttachmentStore
        }
        catch (Exception e) {
            Object[] args = { xWikiAttachment.getFilename(), xWikiAttachment.getDoc().getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_ATTACHMENT,
                "Exception while saving attachments for attachment {0} of document {1}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void saveAttachmentList(
        Document document,
        XWikiDocument xWikiDocument, 
        XWikiContext context, 
        OpenCrxSession session
    ) throws XWikiException {
        try {
            for(XWikiAttachment xWikiAttachment: (List<XWikiAttachment>)xWikiDocument.getAttachmentList()) {
                xWikiAttachment.setDoc(xWikiDocument);
                this.saveAttachment(
                    document,
                    xWikiAttachment, 
                    context, 
                    session
                );
            }
        }
        catch (Exception e) {
            Object[] args = { xWikiDocument.getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_ATTACHMENT_LIST,
                "Exception while saving attachments attachment list of document {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected void loadAttachmentList(
        Document document,
        XWikiDocument xWikiDocument, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        try {
            List<XWikiAttachment> xWikiAttachments = new ArrayList<XWikiAttachment>();
            Collection<DocumentAttachment> attachments = document.getAttachment();
            for(DocumentAttachment attachment: attachments) {
                XWikiAttachment xWikiAttachment = new XWikiAttachment();
                xWikiAttachment.setAuthor(attachment.getAuthor());
                xWikiAttachment.setComment(attachment.getDescription());
                xWikiAttachment.setDate(attachment.getCreatedAt());
                xWikiAttachment.setFilename(attachment.getContentName());
                xWikiAttachment.setDoc(xWikiDocument);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                InputStream content = attachment.getContent().getContent();
                int b;
                while((b = content.read()) != -1) {
                    bos.write(b);
                }
                bos.close();
                xWikiAttachment.setContent(bos.toByteArray());
                xWikiAttachment.setFilesize(bos.size());
                xWikiAttachments.add(xWikiAttachment);
            }
            xWikiDocument.setAttachmentList(xWikiAttachments);
        }
        catch (Exception e) {
            e.printStackTrace();
            Object[] args = { xWikiDocument.getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SEARCHING_ATTACHMENT,
                "Exception while searching attachments for documents {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    protected List searchDocumentsNamesInternal(
        String queryString, 
        int nb, 
        int start, 
        List parameterValues,
        XWikiContext context
    ) throws XWikiException {
        MonitorPlugin monitor  = Util.getMonitorPlugin(context);
        try {
            if(monitor!=null) monitor.startTimer(MONITOR_ID);
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<String> searchResult = new ArrayList<String>();
            for(int i = 0; i < parameterValues.size(); i++) {
                queryString = queryString.replaceFirst("\\?", "'" + parameterValues.get(i) + "'");
            }
            DocumentQuery query = this.createDocumentQuery(
                queryString, 
                context,
                session
            );
            if(query != null) {
                ListIterator<Document> documents = session.getDocumentSegment().getDocument(query).listIterator(start);
                int n = 0;
                nb = nb == 0 ? Integer.MAX_VALUE : nb;
                while(documents.hasNext() && (n < nb)) {
                    Document document = documents.next();
                    if(document.getQualifiedName() != null) {
                        searchResult.add(document.getQualifiedName());
                        n++;
                    }
                }
            }
            return searchResult;
        }
        catch (Exception e) {
            AppLog.warning("Exception while searching documents", e.getMessage());
            AppLog.warning(e.getMessage(), e.getCause());
            Object[] args = { queryString };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SEARCH,
                "Exception while searching documents with sql {0}", 
                e, 
                args
            );
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    private String getWhereClauseValue(
        String clause,
        int start
    ) {
        String value = null;
        int posBegin = clause.indexOf("'", start);
        if(posBegin > 0) {
            int posEnd = clause.indexOf("'", posBegin + 1);
            value = clause.substring(posBegin + 1, posEnd);
        }
        return value;
    }
    
    //-----------------------------------------------------------------------
    private String getWhereClauseOperator(
        String clause,
        int start
    ) {
        if(clause.indexOf("!=", start) > 0) {
            return "!=";
        }
        else if(clause.indexOf("<>", start) > 0) {
            return "!=";
        }
        else if(clause.indexOf("=", start) > 0) {
            return "=";
        }
        else if(clause.indexOf("like", start) > 0) {
            return "like";
        }
        return "=";
    }
    
    //-----------------------------------------------------------------------
    protected DocumentQuery createDocumentQuery(
        String queryString,
        XWikiContext context,
        OpenCrxSession session
    ) {
        DocumentQuery query = session.getDocumentPackage().createDocumentQuery();
        // Documents with property class XWiki.XWikiGroups where the value of
        // the property member matches a set of strings
        if(queryString.matches(QUERY_PATTERN_GROUPS_OF_USER)) {
            Pattern p = Pattern.compile(QUERY_PATTERN_GROUPS_OF_USER);
            Matcher m = p.matcher(queryString);
            if(m.matches()) {
                StringBuilder groupNames = new StringBuilder();
                for(int i = 0; i < m.groupCount(); i++) {
                    if(i > 0) groupNames.append(",");
                    groupNames.append("'").append(m.group(i+1)).append("'");
                }
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter groupMembershipQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                groupMembershipQuery.setClause(
                    "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.name LIKE 'XWiki.XWikiGroups#%' AND ps.object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTY p WHERE p.name = 'member' AND p.string_value IN (" + groupNames + ")))"
                );
                query.thereExistsContext().equalTo(groupMembershipQuery);
            }
            return query;
        }
        // Documents with property class XWiki.XWikiServerClass where the value of
        // the property server matches a set of strings
        else if(queryString.matches(QUERY_PATTERN_VIRTUAL_SERVERS)) {
            Pattern p = Pattern.compile(QUERY_PATTERN_VIRTUAL_SERVERS);
            Matcher m = p.matcher(queryString);
            if(m.matches()) {
                StringBuilder serverNames = new StringBuilder();
                for(int i = 0; i < m.groupCount(); i++) {
                    if(i > 0) serverNames.append(",");
                    serverNames.append("'").append(m.group(i+1)).append("'");
                }
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter groupMembershipQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                groupMembershipQuery.setClause(
                    "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.name LIKE 'XWiki.XWikiServerClass#%' AND ps.object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTY p WHERE p.name = 'server' AND p.string_value IN (" + serverNames + ")))"
                );
                query.thereExistsContext().equalTo(groupMembershipQuery);
            }
            return query;
        }           
        // Documents where property value matches a pattern
        else if(queryString.matches(QUERY_PATTERN_PROPERTY_VALUE_IS_LIKE)) {
            Pattern p = Pattern.compile(QUERY_PATTERN_PROPERTY_VALUE_IS_LIKE);
            Matcher m = p.matcher(queryString);
            if(m.matches()) {
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter propertyValueIsLikeQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                propertyValueIsLikeQuery.setClause(
                    "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTY p WHERE p.string_value LIKE '" + m.group(1) + "'))"
                );
                query.thereExistsContext().equalTo(propertyValueIsLikeQuery);
            }
            return query;
        }
        // Documents with head revision matching version
        else if(queryString.matches(QUERY_PATTERN_DOCS_MATCHING_VERSION)) {
            Pattern p = Pattern.compile(QUERY_PATTERN_DOCS_MATCHING_VERSION);
            Matcher m = p.matcher(queryString);
            if(m.matches()) {
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter versionIsLikeQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                versionIsLikeQuery.setClause(
                    "head_revision IN (SELECT object_id FROM OOCKE1_MEDIA m WHERE m.version LIKE '" + m.group(1) + "%')"
                );
                query.thereExistsContext().equalTo(versionIsLikeQuery);
            }
            return query;
        }
        // Documents where folder matches the web and property value matches a pattern
        else if(queryString.matches(QUERY_PATTERN_WEB_AND_PROPERTY_VALUE_IS_LIKE)) {
            Pattern p = Pattern.compile(QUERY_PATTERN_WEB_AND_PROPERTY_VALUE_IS_LIKE);
            Matcher m = p.matcher(queryString);
            if(m.matches()) {
                List<DocumentFolder> folders = this.findDocumentFolder(
                    m.group(1), 
                    session
                );
                if(!folders.isEmpty() && (folders.get(0) != null)) {
                    query.thereExistsFolder().equalTo(folders.get(0));
                }
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter propertyValueIsLikeQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                propertyValueIsLikeQuery.setClause(
                    "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTY p WHERE p.string_value LIKE '" + m.group(2) + "'))"
                );
                query.thereExistsContext().equalTo(propertyValueIsLikeQuery);
            }
            return query;
        }
        // Articles
        else if(queryString.matches(QUERY_PATTERN_ARTICLES)) {
            org.openmdx.compatibility.datastore1.jmi1.QueryFilter articleClassMembership =
                session.getDatastorePackage().getQueryFilter().createQueryFilter();
            articleClassMembership.setClause(
                "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.name LIKE 'XWiki.ArticleClass#%')"
            );
            String currentDocumentName = context.getDoc().getFullName();
            if(currentDocumentName.indexOf(".") > 0) {
                query.thereExistsQualifiedName().like(currentDocumentName.substring(0, currentDocumentName.lastIndexOf(".")).replace(".", "\\.") + "\\..*");
            }
            query.thereExistsContext().equalTo(articleClassMembership);            
            query.thereExistsQualifiedName().notEqualTo("XWiki.ArticleClassTemplate");
            query.orderByCreatedAt().descending();
            return query;
        }                   
        // All documents
        else if(queryString.startsWith(QUERY_PATTERN_DISTINCT_DOCUMENT_NAMES)) {
            return query;
        }
        // Parse query
        else {
            int posWhere = queryString.indexOf("where");
            String whereClause = posWhere >= 0
                ? queryString.substring(posWhere + 5).trim()
                : queryString;
            String[] operands = whereClause.indexOf(" and ") > 0
                ? whereClause.split(" and ")
                : new String[]{whereClause};
            for(int i = 0; i < operands.length; i++) {
                String operand = operands[i];
                // doc.web
                int posWeb = operand.indexOf("doc.web");
                if(posWeb >= 0) {
                    String folderName = this.getWhereClauseValue(operand, posWeb);
                    if((folderName.startsWith("%") && folderName.endsWith("%"))) {
                        folderName = folderName.substring(1, folderName.length() - 1);
                    }
                    if((folderName != null) && (folderName.length() > 0)) {
                        List<DocumentFolder> folders = this.findDocumentFolder(folderName, session);
                        if(folders.isEmpty()) {
                            return null;
                        }
                        query.thereExistsFolder().equalTo(folders.get(0));
                    }
                }
                // doc.name
                int posName = operand.indexOf("doc.name");
                if(posName >= 0) {
                    String name = this.getWhereClauseValue(operand, posName);
                    if(name != null) {
                        query.name().equalTo(name);
                    }
                }
                // doc.language
                int posLanguage = operand.indexOf("doc.language");
                if(posLanguage >= 0) {
                    String language = this.getWhereClauseValue(operand, posLanguage);
                    if(language != null) {
                        String operator = this.getWhereClauseOperator(operand, posLanguage);
                        if("=".equals(operator)) {
                            query.thereExistsCmsLanguage().equalTo(language);
                        }
                        else if("!=".equals(operator)) {
                            query.thereExistsCmsLanguage().notEqualTo(language);
                        }
                    }
                }
                // doc.fullName
                int posFullName = operand.indexOf("doc.fullName");
                if(posFullName >= 0) {
                    String fullName = this.getWhereClauseValue(operand, posFullName);
                    if(fullName != null) {
                        String operator = this.getWhereClauseOperator(operand, posFullName);
                        if("=".equals(operator)) {
                            query.thereExistsQualifiedName().equalTo(fullName);
                        }
                        else if("!=".equals(operator)) {
                            query.thereExistsQualifiedName().notEqualTo(fullName);
                        }
                        else if("like".equals(operator)) {
                            query.thereExistsQualifiedName().like(fullName.replace("%", ".*"));
                        }
                    }
                }
                // doc.parent
                int posParent = operand.indexOf("doc.parent");
                if(posParent >= 0) {
                    String parentQualifiedName = this.getWhereClauseValue(operand, posParent);
                    if(parentQualifiedName != null) {
                        List<Document> parents = this.findDocumentsByQualifiedName(session, parentQualifiedName);
                        if(!parents.isEmpty()) {
                            Document parent = parents.get(0);
                            String operator = this.getWhereClauseOperator(operand, posParent);
                            if("=".equals(operator)) {
                                query.thereExistsParent().equalTo(parent);
                            }
                            else if("!=".equals(operator)) {
                                query.thereExistsParent().notEqualTo(parent);
                            }
                        }
                    }
                }                
                // doc.author
                int posAuthor = operand.indexOf("doc.author");
                if(posAuthor >= 0) {
                    String author = this.getWhereClauseValue(operand, posAuthor);
                    if(author != null) {
                        String operator = this.getWhereClauseOperator(operand, posAuthor);
                        if("=".equals(operator)) {
                            query.thereExistsAuthor().equalTo(author);
                        }
                        else if("!=".equals(operator)) {
                            query.thereExistsAuthor().notEqualTo(author);
                        }
                    }
                }                
                // obj.className
                int posObjClassName = operand.indexOf("obj.className");
                if(posObjClassName >= 0) {
                    String className = this.getWhereClauseValue(operand, posObjClassName);
                    if(className != null) {
                        String operator = this.getWhereClauseOperator(operand, posObjClassName);
                        if("=".equals(operator)) {
                            org.openmdx.compatibility.datastore1.jmi1.QueryFilter propertySetFilter =
                                session.getDatastorePackage().getQueryFilter().createQueryFilter();
                            propertySetFilter.setClause(
                                "object_id IN (SELECT p$$parent FROM OOCKE1_PROPERTYSET ps WHERE ps.name LIKE '" + className + "#%')"
                            );
                            query.thereExistsContext().equalTo(propertySetFilter);
                        }
                    }
                }          
                // doc.content
                int posContent = operand.indexOf("doc.content");
                if(posContent >= 0) {
                    String content = this.getWhereClauseValue(operand, posContent);
                    if(content != null) {
                        String operator = this.getWhereClauseOperator(operand, posContent);
                        if("like".equals(operator)) {                            
                            query.thereExistsKeywords().like(content.replace("%", ".*"));
                        }
                    }
                }                
            }
            return query;
        }
    }
    
    //-----------------------------------------------------------------------
    //@Override
    public void createWiki(
        String wikiName, 
        XWikiContext context
    ) throws XWikiException {
        try {
            // XWiki database is mapped to an openCRX segment. getSession()
            // looks up the document segment. createWiki is OK if a session
            // to openCRX can be established and the document segment can
            // be retrieved. On-demand creation of openCRX is not supported.
            // It must be done with the standard openCRX GUI
            this.getOpenCrxSession(context);
        }
        catch (Exception e) {
            Object[] args = { wikiName  };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE,
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_CREATE_DATABASE,
                "Exception while create wiki database {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    //@Override
    public boolean exists(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        MonitorPlugin monitor  = Util.getMonitorPlugin(context);
        try {
            if(monitor!=null) monitor.startTimer(MONITOR_ID);
            xWikiDocument.setStore(this);
            OpenCrxSession session = this.getOpenCrxSession(context);            
            DocumentQuery query = session.getDocumentPackage().createDocumentQuery();
            query.thereExistsQualifiedName().equalTo(xWikiDocument.getFullName());
            return !session.getDocumentSegment().getDocument(query).isEmpty();
        } 
        catch (Exception e) {
            Object[] args = { xWikiDocument.getFullName() };
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_CHECK_EXISTS_DOC,
                "Exception while reading document {0}", 
                e, 
                args
            );
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    //@Override
    public void saveXWikiDoc(
        XWikiDocument xWikiDocument, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        MonitorPlugin monitor = Util.getMonitorPlugin(context);
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            if(monitor != null) monitor.startTimer(MONITOR_ID);
            xWikiDocument.setStore(this);
            xWikiDocument.setDatabase(context.getDatabase());
            // Map Space to DocumentFolder            
            session.beginTransaction();            
            List<DocumentFolder> folders = this.findDocumentFolder(
                xWikiDocument.getSpace(), 
                session
            );
            DocumentFolder folder = null;
            if(folders.isEmpty()) {
                folder = session.getDocumentPackage().getDocumentFolder().createDocumentFolder();
                folder.refInitialize(false, false);
                folder.setName(xWikiDocument.getSpace());
                folder.setParent(this.getRootFolder(session));
                session.getDocumentSegment().addFolder(
                    false,
                    this.uuidAsString(),
                    folder
                );
            }
            else {
                folder = folders.get(0);
            }
            session.commitTransaction();            
            // These informations will allow to not look for attachments and objects on loading
            session.beginTransaction();
            xWikiDocument.setElement(
                XWikiDocument.HAS_ATTACHMENTS, 
                !xWikiDocument.getAttachmentList().isEmpty()
            );
            xWikiDocument.setElement(
                XWikiDocument.HAS_OBJECTS, 
                !xWikiDocument.getxWikiObjects().isEmpty()
            );
            // Let's update the class XML since this is the new way to store it
            BaseClass bclass = xWikiDocument.getxWikiClass();
            if ((bclass!=null)&&(bclass.getFieldList().size()>0)) {
               xWikiDocument.setxWikiClassXML(bclass.toXMLString());
            }
            // Handle the latest text file
            if (xWikiDocument.isContentDirty() || xWikiDocument.isMetaDataDirty()) {
                Date ndate = new Date();
                xWikiDocument.setDate(ndate);
                if (xWikiDocument.isContentDirty()) {
                    xWikiDocument.setContentUpdateDate(ndate);
                    xWikiDocument.setContentAuthor(xWikiDocument.getAuthor());
                }
                xWikiDocument.incrementVersion();
                if (context.getWiki().hasVersioning(xWikiDocument.getFullName(), context)) {
                    context.getWiki().getVersioningStore().updateXWikiDocArchive(xWikiDocument, false, context);
                }
                xWikiDocument.setContentDirty(false);
                xWikiDocument.setMetaDataDirty(false);
            } 
            else {
                if(xWikiDocument.getDocumentArchive() != null) {
                    // Let's make sure we save the archive if we have one
                    // This is especially needed if we load a document from XML
                    if (context.getWiki().hasVersioning(xWikiDocument.getFullName(), context))
                     context.getWiki().getVersioningStore().saveXWikiDocArchive(xWikiDocument.getDocumentArchive(),false, context);
                } 
                else {
                    // Make sure the getArchive call has been made once
                    // with a valid context
                    try {
                        if (context.getWiki().hasVersioning(xWikiDocument.getFullName(), context)) {
                            xWikiDocument.getDocumentArchive(context);
                        }
                    } 
                    catch (XWikiException e) {
                        // this is a non critical error
                    }
                }
            }
            // Create or update the document and create a new revision
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiDocument.getId());
            Document document = null;
            if(documents.isEmpty()) {
                document = session.getDocumentPackage().getDocument().createDocument();
                document.refInitialize(false, false);
                session.getDocumentSegment().addDocument(
                    false,
                    this.uuidAsString(),
                    document
                );
            }
            else {
                document = documents.get(0);
            }
            // Map XWikiDocument to openCRX document
            document.setName(xWikiDocument.getName());
            document.setQualifiedName(xWikiDocument.getFullName());
            document.setDocumentNumber(Long.toString(xWikiDocument.getId()));
            document.setTitle(xWikiDocument.getTitle());
            document.setContentType(xWikiDocument.getFormat());
            document.setAuthor(xWikiDocument.getAuthor());
            if(xWikiDocument.getParent() != null) {
                List<Document> parents = this.findDocumentsByQualifiedName(
                    session, 
                    xWikiDocument.getParent()
                );
                if(!parents.isEmpty()) {
                    document.setParent(parents.get(0));
                }
            }
            document.setCmsMeta(xWikiDocument.getMeta());
            document.setCmsTemplate(xWikiDocument.getTemplate());
            document.setCmsLanguage(xWikiDocument.getLanguage());            
            document.setCmsDefaultLanguage(xWikiDocument.getDefaultLanguage());
            document.setCmsClass(xWikiDocument.getxWikiClassXML());
            document.setCmsTranslation(xWikiDocument.getTranslation());
            document.getFolder().clear();
            document.getFolder().add(folder);
            // Create document revision and store content to revision
            MediaContent revision = session.getDocumentPackage().getMediaContent().createMediaContent();
            revision.refInitialize(false, false);
            revision.setName(xWikiDocument.getName());
            revision.setContentName(xWikiDocument.getName());
            revision.setContent(                
                BinaryLargeObjects.valueOf(
                    xWikiDocument.getContent().getBytes("UTF-8")
                )
            );
            revision.setContentMimeType("text/html");
            revision.setVersion(xWikiDocument.getVersion());
            revision.setAuthor(xWikiDocument.getContentAuthor());
            document.addRevision(
                false,
                this.uuidAsString(),
                revision
            );
            document.setHeadRevision(revision);
            session.commitTransaction();
            // Update index entry of created revision
            session.beginTransaction();
            revision.updateIndex();
            session.commitTransaction();            
            // Update document with keywords, properties, links, attachments
            session.beginTransaction();
            IndexEntryQuery latestIndexEntryQuery = session.getBasePackage().createIndexEntryQuery();
            latestIndexEntryQuery.orderByCreatedAt().descending();
            List<IndexEntry> indexEntries = revision.getIndexEntry(latestIndexEntryQuery);
            if(!indexEntries.isEmpty()) {
                IndexEntry indexEntry = indexEntries.get(0);
                String keywords = indexEntry.getKeywords();
                if(keywords != null && (keywords.length() > 3500)) {
                    keywords = keywords.substring(0, 3500);
                }
                document.setKeywords(keywords);
            }
            // Remove properties planned for removal
            if (xWikiDocument.getObjectsToRemove().size()>0) {
                for(BaseObject xWikiObject: (List<BaseObject>)xWikiDocument.getObjectsToRemove()) {
                    if(xWikiObject != null) {
                        this.deleteXWikiBaseObject(
                            document,
                            xWikiObject, 
                            context, 
                            session
                        );
                    }
                }
                xWikiDocument.setObjectsToRemove(new ArrayList());
            }
            if (xWikiDocument.hasElement(XWikiDocument.HAS_OBJECTS)) {
                // TODO: Delete all objects for which we don't have a name in the Map..
                Collection<Vector<BaseObject>> xWikiObjects = 
                    (Collection<Vector<BaseObject>>)xWikiDocument.getxWikiObjects().values();
                for(Vector<BaseObject> objects: xWikiObjects) {
                    for(BaseCollection xWikiObject: objects) {
                        if(xWikiObject != null){
                            xWikiObject.setName(xWikiDocument.getFullName());
                            this.saveXWikiBaseObject(
                                document,
                                xWikiObject, 
                                context, 
                                session
                            );
                        }
                    }
                }
            }
            // Links
            if (context.getWiki().hasBacklinks(context)){
                this.saveLinks(
                    xWikiDocument, 
                    context, 
                    true
                );
            }
            // Attachments
            if (xWikiDocument.hasElement(XWikiDocument.HAS_ATTACHMENTS)) {
                this.saveAttachmentList(
                    document,
                    xWikiDocument, 
                    context, 
                    session
                );
            }
            session.commitTransaction();
            xWikiDocument.setNew(false);
            // We need to ensure that the saved document becomes the original document
            xWikiDocument.setOriginalDocument((XWikiDocument) xWikiDocument.clone());
        } 
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}            
            new ServiceException(e).log();
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_DOC,
                "Exception while saving document " + xWikiDocument.getFullName() + ". See log for more details"
            );
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    //@Override
    public void saveXWikiDoc(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        this.saveXWikiDoc(
            xWikiDocument, 
            context, 
            true
        );
    }

    
    //-----------------------------------------------------------------------
    @Override
    public XWikiDocument loadXWikiDoc(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        MonitorPlugin monitor = Util.getMonitorPlugin(context);
        try {
            if (monitor!=null) monitor.startTimer(MONITOR_ID);
            xWikiDocument.setStore(this);
            OpenCrxSession session = this.getOpenCrxSession(context);
            // Find document
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiDocument.getId());
            Document document = null;
            if(documents.isEmpty()) {
                xWikiDocument.setNew(true);
                return xWikiDocument;
            }
            document = documents.get(0);
            // Map to XWikiDocument
            xWikiDocument.setName(document.getName());       
            xWikiDocument.setFullName(document.getQualifiedName());
            xWikiDocument.setTitle(document.getTitle());
            xWikiDocument.setFormat(
                document.getContentType() == null ? 
                    "" : 
                    document.getContentType()
            );
            xWikiDocument.setAuthor(document.getAuthor());
            if(document.getParent() != null) {
                xWikiDocument.setParent(document.getParent().getQualifiedName());
            }
            xWikiDocument.setMeta(document.getCmsMeta());
            xWikiDocument.setTemplate(document.getCmsTemplate());
            xWikiDocument.setLanguage(document.getCmsLanguage());            
            xWikiDocument.setDefaultLanguage(document.getCmsDefaultLanguage());
            xWikiDocument.setxWikiClassXML(document.getCmsClass());
            xWikiDocument.setTranslation(document.getCmsTranslation());
            // Space
            if(!document.getFolder().isEmpty()) {
                xWikiDocument.setSpace(document.getFolder().get(0).getName());
            }
            // Content
            if((document.getHeadRevision() != null) && (document.getHeadRevision() instanceof MediaContent)) {
                MediaContent revision = (MediaContent)document.getHeadRevision();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                InputStream content = revision.getContent().getContent();
                int b;
                while((b = content.read()) != -1) {
                    bos.write(b);
                }
                bos.close();
                xWikiDocument.setContent(bos.toString("UTF-8"));
                xWikiDocument.setVersion(revision.getVersion());
                xWikiDocument.setContentAuthor(revision.getAuthor());
            }
            xWikiDocument.setDatabase(context.getDatabase());
            xWikiDocument.setNew(false);
            xWikiDocument.setMostRecent(true);
            // Attachments
            if(xWikiDocument.hasElement(XWikiDocument.HAS_ATTACHMENTS)) {
                this.loadAttachmentList(
                    document, 
                    xWikiDocument, 
                    context, 
                    false
                );
            }
            // BaseClass
            BaseClass bclass = new BaseClass();
            String cxml = xWikiDocument.getxWikiClassXML();
            if (cxml!=null) {
                bclass.fromXML(cxml);
                bclass.setName(xWikiDocument.getFullName());
                xWikiDocument.setxWikiClass(bclass);
            }
            // Objects
            context.addBaseClass(bclass);
            if (xWikiDocument.hasElement(XWikiDocument.HAS_OBJECTS)) {
                Collection<org.opencrx.kernel.document1.jmi1.PropertySet> propertySets = document.getPropertySet();
                for(org.opencrx.kernel.document1.jmi1.PropertySet ps: propertySets) {
                    String className = ps.getName();
                    int id = 0;
                    int posIndex = className.indexOf("#");
                    if(posIndex > 0) {
                        id = Integer.valueOf(className.substring(posIndex + 1)).intValue();
                        className = className.substring(0, posIndex);
                    }
                    if(className.equals("internal")) {
                        // continue
                    }
                    else if(className.equals("XWiki.XWikiGroups")) {
                        int number = 0;
                        Collection<org.opencrx.kernel.base.jmi1.Property> properties = ps.getProperty();
                        for(org.opencrx.kernel.base.jmi1.Property p: properties) {                            
                            if(p instanceof org.opencrx.kernel.base.jmi1.StringProperty) {
                                String member = ((org.opencrx.kernel.base.jmi1.StringProperty)p).getStringValue();
                                BaseObject obj = BaseClass.newCustomClassInstance(className, context);
                                obj.setName(xWikiDocument.getFullName());
                                obj.setClassName(className);
                                obj.setNumber(number);
                                obj.setStringValue("member", member);
                                xWikiDocument.setObject(
                                    className, 
                                    obj.getNumber(), 
                                    obj
                                );
                                number++;
                            }
                        }
                    }
                    else if(!className.equals("")) {
                        BaseObject xWikiObject;
                        if(className.equals(xWikiDocument.getFullName())) {
                            xWikiObject = bclass.newCustomClassInstance(context);
                        }
                        else {
                            xWikiObject = BaseClass.newCustomClassInstance(className, context);
                        }
                        if(xWikiObject != null) {
                            xWikiObject.setName(xWikiDocument.getFullName());
                            xWikiObject.setNumber(id);
                            xWikiObject.setClassName(className);
                            this.loadXWikiBaseObject(
                                ps,
                                xWikiObject, 
                                context,
                                session
                            );
                            xWikiDocument.setObject(
                                className, 
                                xWikiObject.getNumber(), 
                                xWikiObject
                            );
                        }   
                    }
                }
            }
            // We need to ensure that the loaded document becomes the original document
            xWikiDocument.setOriginalDocument((XWikiDocument) xWikiDocument.clone());
        } 
        catch (Exception e) {
            ServiceException e0 = new ServiceException(e);
            if(e0.getExceptionCode() == BasicException.Code.NOT_FOUND) {
                xWikiDocument.setNew(false);
                return xWikiDocument;                
            }
            else {
                Object[] args = { xWikiDocument.getFullName() };
                throw new XWikiException(
                    XWikiException.MODULE_XWIKI_STORE, 
                    XWikiException.ERROR_XWIKI_STORE_HIBERNATE_READING_DOC,
                    "Exception while reading document {0}", 
                    e, 
                    args
                );
            }
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
        return xWikiDocument;
    }

    //-----------------------------------------------------------------------
    @Override
    public void deleteXWikiDoc(
        XWikiDocument xWikiDocument, 
        XWikiContext context
    ) throws XWikiException {
        MonitorPlugin monitor = Util.getMonitorPlugin(context);
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            if (monitor!=null) monitor.startTimer(MONITOR_ID);
            session.beginTransaction();
            if(xWikiDocument.getStore() == null) {
                Object[] args = { xWikiDocument.getFullName() };
                throw new XWikiException(
                    XWikiException.MODULE_XWIKI_STORE, 
                    XWikiException.ERROR_XWIKI_STORE_HIBERNATE_CANNOT_DELETE_UNLOADED_DOC,
                    "Impossible to delete document {0} if it is not loaded", 
                    null, 
                    args
                );
            }
            // Find document
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiDocument.getId());
            Document document = null;
            if(documents.isEmpty()) {
                return;
            }
            document = documents.get(0);
            document.refDelete();
            // We need to ensure that the deleted document becomes the original document
            xWikiDocument.setOriginalDocument((XWikiDocument) xWikiDocument.clone());
            session.commitTransaction();
        } 
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}
            Object[] args = { xWikiDocument.getFullName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_DOC,
                "Exception while deleting document {0}", 
                e, 
                args
            );
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public void saveXWikiObject(
        BaseObject xWikiObject, 
        XWikiContext context,
        boolean transaction
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            List<Document> documents = this.findDocumentsByDocumentNumber(
                session, 
                context.getDoc().getId()
            );
            session.beginTransaction();
            if(!documents.isEmpty()) {
                this.saveXWikiBaseObject(
                    documents.get(0), 
                    xWikiObject, 
                    context, 
                    session
                );
            }
            session.commitTransaction();
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}
            Object[] args = { xWikiObject.getName() };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_DOC,
                "Exception while saving object {0}", 
                e, 
                args
            );
        } 
        finally {
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    public XWikiLock loadLock(
        long docId, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        XWikiLock xWikiLock = null;
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Document> documents = this.findDocumentsByDocumentNumber(session, docId);
            if(!documents.isEmpty()) {
                List<org.opencrx.kernel.document1.jmi1.DocumentLock> locks = this.findXWikiLock(
                    documents.get(0), 
                    session
                );
                if(!locks.isEmpty()) {
                    org.opencrx.kernel.document1.jmi1.DocumentLock lock = locks.get(0);
                    xWikiLock = new XWikiLock();
                    xWikiLock.setDocId(docId);
                    if(!lock.getLockedBy().isEmpty()) {
                        xWikiLock.setUserName(lock.getLockedBy().get(0));
                    }
                    xWikiLock.setDate(lock.getLockedAt());
                }
            }
        }
        catch (Exception e) {
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_LOCK,
                "Exception while loading lock", 
                e
            );
        } 
        finally {
        }
        return xWikiLock;
    }

    //-----------------------------------------------------------------------
    @Override
    public void saveLock(
        XWikiLock xWikiLock, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        OpenCrxSession session = getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiLock.getDocId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                List<org.opencrx.kernel.document1.jmi1.DocumentLock> locks = this.findXWikiLock(
                    document, 
                    session
                );            
                org.opencrx.kernel.document1.jmi1.DocumentLock lock = null;
                if(locks.isEmpty()) {
                    lock = session.getDocumentPackage().getDocumentLock().createDocumentLock();
                    lock.refInitialize(false, false);
                    document.addLock(
                        false,
                        this.uuidAsString(),
                        lock
                    );
                }
                else {
                    lock = locks.get(0);
                }
                lock.setName("XWiki");
                lock.getLockedBy().clear();
                lock.getLockedBy().add(xWikiLock.getUserName());
                lock.setLockedAt(xWikiLock.getDate());
            }
            session.commitTransaction();
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}                        
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_LOCK,
                "Exception while locking document", 
                e
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public void deleteLock(
        XWikiLock xWikiLock, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        OpenCrxSession session = this.getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiLock.getDocId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                List<org.opencrx.kernel.document1.jmi1.DocumentLock> locks = this.findXWikiLock(
                    document, 
                    session
                );
                for(org.opencrx.kernel.document1.jmi1.DocumentLock lock: locks) {
                    lock.refDelete();
                }
            }
            session.commitTransaction();
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}                                    
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_LOCK,
                "Exception while deleting lock", 
                e
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public List loadLinks(
        long docId, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        List<XWikiLink> xWikiLinks = new ArrayList<XWikiLink>();
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Document> documents = this.findDocumentsByDocumentNumber(session, docId);
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                Collection<org.opencrx.kernel.document1.jmi1.DocumentLink> documentLinks = document.getLink();
                for(org.opencrx.kernel.document1.jmi1.DocumentLink link: documentLinks) {
                    XWikiLink xWikiLink = new XWikiLink();
                    xWikiLink.setDocId(docId);
                    xWikiLink.setFullName(link.getDescription());
                    xWikiLink.setLink(link.getLinkUri());
                    xWikiLinks.add(xWikiLink);
                }
            }
        }
        catch (Exception e) {
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_LINKS,
                "Exception while loading links", 
                e
            );
        }
        finally {
        }
        return xWikiLinks;
    }

    //-----------------------------------------------------------------------
    @Override
    public List loadBacklinks(
        String fullName, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        List<XWikiLink> backlinks = new ArrayList<XWikiLink>();
        try {
            OpenCrxSession session = this.getOpenCrxSession(context);
            DocumentLinkQuery query = session.getDocumentPackage().createDocumentLinkQuery();
            query.linkUri().equalTo(fullName);
            Path documentSegmentIdentity = session.getDocumentSegment().refGetPath();
            query.identity().like(
                "xri:@openmdx:org\\.opencrx\\.kernel\\.document1/provider/" + documentSegmentIdentity.get(2) + "/segment/" + documentSegmentIdentity.get(4) + "/document/.*/link/.*"
            );
            Collection<org.opencrx.kernel.document1.jmi1.DocumentLink> documentLinks = session.getDocumentSegment().getExtent(query);
            for(org.opencrx.kernel.document1.jmi1.DocumentLink link: documentLinks) {
                try {
                    XWikiLink xWikiLink = new XWikiLink();
                    xWikiLink.setDocId(Long.valueOf(link.getDocument().getDocumentNumber()));
                    xWikiLink.setFullName(link.getName());
                    xWikiLink.setLink(link.getLinkUri());
                    backlinks.add(xWikiLink);
                } 
                // Skip the link in case the document can not be retrieved
                catch(Exception e) {
                    new ServiceException(e).log();
                }
            }
        }
        catch (Exception e) {
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_LOADING_BACKLINKS,
                "Exception while loading backlinks", 
                e
            );
        }
        finally {
        }
        return backlinks;
    }

    //-----------------------------------------------------------------------
    @Override
    public void saveLinks(
        XWikiDocument xWikiDocument, 
        XWikiContext context, 
        boolean bTransaction
    ) throws XWikiException {
        OpenCrxSession session = getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, xWikiDocument.getId());
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                Collection<org.opencrx.kernel.document1.jmi1.DocumentLink> documentLinks = document.getLink();
                for(org.opencrx.kernel.document1.jmi1.DocumentLink link: documentLinks) {
                    link.refDelete();
                }
                context.remove("links");
                List<String> linkUris;
                XWikiDocument originalDocument = context.getDoc();
                context.setDoc(xWikiDocument);
                try {
                    XWikiContext renderContext = (XWikiContext)context.clone();
                    XWikiRenderer renderer = renderContext.getWiki().getRenderingEngine().getRenderer("wiki");
                    renderer.render(xWikiDocument.getContent(), xWikiDocument, xWikiDocument, renderContext);
                    linkUris = (List)renderContext.get("links");
                } 
                catch (Exception e) {
                    linkUris = Collections.EMPTY_LIST;
                } 
                finally {
                    if (originalDocument != null) {
                        context.setDoc(originalDocument);
                    }
                }
                if (linkUris != null) {
                    for(String linkUri: linkUris) {
                        org.opencrx.kernel.document1.jmi1.DocumentLink link = session.getDocumentPackage().getDocumentLink().createDocumentLink();
                        link.refInitialize(false, false);
                        link.setName(xWikiDocument.getFullName());
                        link.setLinkUri(linkUri);
                        document.addLink(
                            false,
                            this.uuidAsString(),
                            link
                        );
                    }
                }
                session.commitTransaction();
            }
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}                                                
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SAVING_LINKS,
                "Exception while saving links", 
                e
            );
        } 
        finally {
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public void deleteLinks(
        long docId, 
        XWikiContext context,
        boolean transaction
    ) throws XWikiException {
        OpenCrxSession session = getOpenCrxSession(context);
        try {
            session.beginTransaction();
            List<Document> documents = this.findDocumentsByDocumentNumber(session, docId);
            if(!documents.isEmpty()) {
                Document document = documents.get(0);
                Collection<org.opencrx.kernel.document1.jmi1.DocumentLink> documentLinks = document.getLink();
                for(org.opencrx.kernel.document1.jmi1.DocumentLink link: documentLinks) {
                    link.refDelete();
                }
                session.commitTransaction();
            }
        }
        catch (Exception e) {
            try {
                if (session != null) session.rollbackTransaction();
            } catch (Exception e0) {}                                                
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_DELETING_LINKS,
                "Exception while deleting links", 
                e
            );
        } finally {
        }        
    }
    
    //-----------------------------------------------------------------------
    @Override
    public List getClassList(
        XWikiContext context
    ) throws XWikiException {
        try {
            OpenCrxSession session = getOpenCrxSession(context);
            DocumentQuery query = session.getDocumentPackage().createDocumentQuery();
            query.cmsClass().isNonNull();
            List<String> list = new ArrayList<String>();
            for(Document document: (Collection<Document>)session.getDocumentSegment().getDocument(query)) {
                list.add(document.getQualifiedName());
            }
            return list;
        }
        catch (Exception e) {
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SEARCH,
                "Exception while searching class list", 
                e
            );
        } 
        finally {         
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocumentsNames(
        String parametrizedSqlClause, 
        List parameterValues,
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocumentsNames(
            parametrizedSqlClause, 
            0, 
            0, 
            parameterValues, 
            context
         );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocumentsNames(
        String queryString, 
        int nb, 
        int start,
        List parameterValues, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocumentsNamesInternal(
            queryString, 
            nb, 
            start, 
            parameterValues, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List search(
        String queryString, 
        int nb, 
        int start, 
        Object[][] whereParams, 
        XWikiContext context
    ) throws XWikiException {
        if(queryString == null) return null;
        MonitorPlugin monitor = Util.getMonitorPlugin(context);
        try {
            if(monitor!=null) monitor.startTimer(MONITOR_ID);
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<Object> searchResult = new ArrayList();
            
            // Search for spaces
            if(queryString.startsWith(QUERY_PATTERN_ALL_SPACES)) {
                DocumentFolderQuery query = session.getDocumentPackage().createDocumentFolderQuery();
                query.thereExistsParent().equalTo(this.getRootFolder(session));
                nb = nb == 0 ? Integer.MAX_VALUE : nb;            
                if(query != null) {
                    ListIterator<DocumentFolder> documentFolders = session.getDocumentSegment().getFolder(query).listIterator(start);
                    int n = 0;
                    while(documentFolders.hasNext() && (n < nb)) {
                        DocumentFolder documentFolder = documentFolders.next();
                        searchResult.add(documentFolder.getName());
                        n++;
                    }
                }
            }
            // Search for tags
            else if(queryString.matches(QUERY_PATTERN_TAGS)) {
                Path documentSegmentIdentity = session.getDocumentSegment().refGetPath();
                StringPropertyQuery query = session.getBasePackage().createStringPropertyQuery();
                query.identity().like(
                    "xri:@openmdx:org\\.opencrx\\.kernel\\.document1/provider/" + documentSegmentIdentity.get(2) + "/segment/" + documentSegmentIdentity.get(4) + "/document/:*/propertySet/:*/property/:*"
                );                
                query.name().equalTo("tags");
                org.openmdx.compatibility.datastore1.jmi1.QueryFilter propertyClassQuery =
                    session.getDatastorePackage().getQueryFilter().createQueryFilter();
                propertyClassQuery.setClause(
                    "p$$parent IN (SELECT object_id FROM OOCKE1_PROPERTYSET ps WHERE ps.name LIKE 'XWiki.TagClass#%')"
                );
                query.thereExistsContext().equalTo(propertyClassQuery);   
                Collection<org.opencrx.kernel.base.jmi1.StringProperty> properties = session.getDocumentSegment().getExtent(query);
                for(org.opencrx.kernel.base.jmi1.StringProperty p: properties) {
                    List<String> tags = this.stringToList(p.getStringValue());
                    for(int i = 0; i < tags.size(); i++) {
                        String tag = tags.get(i);
                        if((tag != null) && (tag.length() > 0) && !searchResult.contains(tag) && !"|".equals(tag)) {
                            searchResult.add(tags.get(i));
                        }
                    }
                }
            }
            // Search for documents
            else {                
                DocumentQuery query = this.createDocumentQuery(
                    queryString, 
                    context,
                    session
                );
                nb = nb == 0 ? Integer.MAX_VALUE : nb;            
                if(query != null) {
                    ListIterator<Document> documents = session.getDocumentSegment().getDocument(query).listIterator(start);
                    int n = 0;
                    while(documents.hasNext() && (n < nb)) {
                        Document document = documents.next();
                        if(queryString.startsWith(QUERY_PATTERN_ALL_LANGUAGES)) {
                            if(document.getCmsLanguage() != null) {
                                searchResult.add(document.getCmsLanguage());
                                n++;
                            }                            
                        }
                        else {
                            if(document.getQualifiedName() != null) {
                                searchResult.add(document.getQualifiedName());
                                n++;
                            }
                        }
                    }
                }
            }
            return searchResult;
        }
        catch (Exception e) {
            Object[] args = { queryString };
            throw new XWikiException( 
                XWikiException.MODULE_XWIKI_STORE, 
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SEARCH,
                "Exception while searching documents with sql {0}", 
                e, 
                args
            );
        } 
        finally {
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public List search(
        String queryString, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
        return this.search(
            queryString, 
            nb, 
            start, 
            (Object[][])null, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocumentsNames(
        String queryString, 
        int nb, 
        int start, 
        String selectColumns, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocumentsNamesInternal(
            queryString, 
            nb, 
            start, 
            Collections.EMPTY_LIST, 
            context
         );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String queryString, 
        boolean distinctByLanguage, 
        boolean customMapping, 
        boolean checkRight, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
        MonitorPlugin monitor  = Util.getMonitorPlugin(context);
        try {
            if(monitor!=null) monitor.startTimer(MONITOR_ID);
            OpenCrxSession session = this.getOpenCrxSession(context);
            List<XWikiDocument> searchResult = new ArrayList<XWikiDocument>();
            DocumentQuery query = this.createDocumentQuery(
                queryString,
                context,
                session                
            );
            if(query != null) {
                ListIterator<Document> documents = session.getDocumentSegment().getDocument(query).listIterator(start);
                int n = 0;
                while(documents.hasNext() && (n < nb)) {
                    Document document = documents.next();
                    XWikiDocument doc = new XWikiDocument(document.getFolder().get(0).getName(), document.getName());
                    if (checkRight) {
                        if(!context.getWiki().getRightService().checkAccess("view", doc, context)) continue;
                    }
                    String name = doc.getFullName();
                    if (distinctByLanguage) {
                        String language = document.getCmsLanguage();
                        if ((language==null)||(language.equals(""))) {
                            searchResult.add(context.getWiki().getDocument(name, context));
                        }
                        else {
                            XWikiDocument doc2 = context.getWiki().getDocument(name, context);
                            searchResult.add(doc2.getTranslatedDocument(language, context));
                        }
                    } 
                    else {
                        searchResult.add(context.getWiki().getDocument(name, context));
                    }
                }
            }
            return searchResult;
        }
        catch (Exception e) {
            throw new XWikiException(
                XWikiException.MODULE_XWIKI_STORE,
                XWikiException.ERROR_XWIKI_STORE_HIBERNATE_SEARCH,
                "Exception while searching documents with SQL [{0}]", 
                e, 
                new Object[]{ queryString }
            );
        } 
        finally {
            // End monitoring timer
            if (monitor!=null) monitor.endTimer(MONITOR_ID);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isCustomMappingValid(
        BaseClass bclass, 
        String custommapping1, 
        XWikiContext context
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public void injectCustomMappings(
        XWikiContext context
    ) throws XWikiException {
    }

    //-----------------------------------------------------------------------
    @Override
    public void injectUpdatedCustomMappings(
        XWikiContext context
    ) throws XWikiException {
    }

    // -----------------------------------------------------------------------
    @Override
    public boolean injectCustomMappings(
        XWikiDocument doc, 
        XWikiContext context
    ) throws XWikiException {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean injectCustomMapping(
        BaseClass doc1class, 
        XWikiContext context
    ) throws XWikiException {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public List getCustomMappingPropertyList(
        BaseClass bclass
    ) {
        return Collections.EMPTY_LIST;
    }

    //-----------------------------------------------------------------------
    @Override
    public XWikiBatcherStats getBatcherStats(
    ) {
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public void resetBatcherStats(
    ) {
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocumentsNames(
        String wheresql, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocumentsNames(
            wheresql, 
            0, 
            0, 
            "", 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocumentsNames(
        String wheresql, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocumentsNames(
            wheresql, 
            nb, 
            start, 
            "", 
            context
         );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocuments(
            wheresql, 
            true, 
            0, 
            0, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        boolean distinctbylanguage, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocuments(
            wheresql, 
            distinctbylanguage, 
            0, 
            0, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        boolean distinctbylanguage, 
        boolean customMapping, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocuments(
            wheresql, 
            distinctbylanguage, 
            customMapping, 
            0, 
            0, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocuments(
            wheresql, 
            true, 
            nb, 
            start, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        boolean distinctbyname, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
          return this.searchDocuments(
              wheresql, 
              distinctbyname, 
              false, 
              nb, 
              start, 
              context
          );
    }

    //-----------------------------------------------------------------------
    @Override
    public List searchDocuments(
        String wheresql, 
        boolean distinctbyname, 
        boolean customMapping, 
        int nb, 
        int start, 
        XWikiContext context
    ) throws XWikiException {
        return this.searchDocuments(
            wheresql, 
            distinctbyname, 
            customMapping, 
            true, 
            nb, 
            start, 
            context
        );
    }

    //-----------------------------------------------------------------------
    @Override
    public List getTranslationList(
        XWikiDocument doc, 
        XWikiContext context
    ) throws XWikiException {
        String queryString = "select doc.language from XWikiDocument as doc where doc.web = '"
            + Utils.SQLFilter(doc.getSpace()) + "' and doc.name = '"
            + Utils.SQLFilter(doc.getName()) + "' and doc.language <> ''";
        List list = context.getWiki().search(queryString, context);
        return (list == null) ? new ArrayList() : list;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------    
    private static final Logger log = LoggerFactory.getLogger(XWikiHibernateStore.class);

    protected static final String ARRAY_PREFIX = "#[";
    protected static final String ARRAY_SEPARATOR = "|";
    protected static final int LARGE_STRING_PART_SIZE = 2000;
    
    protected static final String QUERY_PATTERN_GROUPS_OF_USER =
        ".*where obj\\.name=doc\\.fullName and obj\\.className='XWiki\\.XWikiGroups' and obj\\.id = prop\\.id\\.id and prop\\.id\\.name='member' and \\(prop\\.value='(.*)' or prop\\.value='(.*)' or prop\\.value='(.*)'\\)";
    protected static final String QUERY_PATTERN_VIRTUAL_SERVERS =
        ".*where obj\\.name=doc\\.fullName and obj\\.className='XWiki\\.XWikiServerClass' and prop\\.id\\.id = obj\\.id and prop\\.id\\.name = 'server' and prop\\.value='(.*)'";    
    protected static final String QUERY_PATTERN_PROPERTY_VALUE_IS_LIKE =
        ".*where  obj\\.name=doc\\.fullName and prop\\.id\\.id = obj\\.id and upper\\(prop\\.value\\) like upper\\('(.*)'\\)";
    protected static final String QUERY_PATTERN_WEB_AND_PROPERTY_VALUE_IS_LIKE =
        ".*where doc\\.web='(.*)' and  obj\\.name=doc\\.fullName and prop\\.id\\.id = obj\\.id and upper\\(prop\\.value\\) like upper\\('(.*)'\\)";
    protected static final String QUERY_PATTERN_DOCS_MATCHING_VERSION =
        ".*where doc\\.id=ni\\.id\\.docId and ni\\.id\\.version2=(.*) group by doc.web, doc.name order by max\\(ni\\.date\\) desc";
    protected static final String QUERY_PATTERN_TAGS = 
        "select distinct elements\\(prop\\.list\\) from BaseObject as obj, DBStringListProperty as prop where obj\\.className='XWiki\\.TagClass' and obj\\.id=prop\\.id\\.id and prop\\.id\\.name='tags'";
    protected static final String QUERY_PATTERN_ARTICLES = 
        ".*where obj\\.name=doc\\.fullName and obj\\.className='XWiki.ArticleClass' and obj\\.name<>'XWiki\\.ArticleClassTemplate' order by doc\\.creationDate desc";
    protected static final String QUERY_PATTERN_ALL_SPACES =
        "select distinct doc.web";
    protected static final String QUERY_PATTERN_DISTINCT_DOCUMENT_NAMES =
        "select distinct doc.name";
    protected static final String QUERY_PATTERN_ALL_LANGUAGES =
        "select doc.language from XWikiDocument";

}

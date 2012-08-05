/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Exporter.java,v 1.17 2008/09/26 07:52:11 wfro Exp $
 * Description: Exporter
 * Revision:    $Revision: 1.17 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/26 07:52:11 $
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
package org.opencrx.kernel.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.opencrx.kernel.backend.Importer.NullOutputStream;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.query.Filter;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.exporter.ProviderTraverser;
import org.openmdx.compatibility.base.dataprovider.exporter.TraversalHandler;
import org.openmdx.compatibility.base.dataprovider.exporter.Traverser;
import org.openmdx.compatibility.base.dataprovider.exporter.XMLExportHandler;
import org.openmdx.compatibility.base.dataprovider.exporter.XmlContentHandler;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.w3c.cci2.BinaryLargeObject;

public class Exporter {

    //-------------------------------------------------------------------------
    public Exporter(
        Backend backend,
        RequestCollection target,
        RequestCollection reader
    ) {
        this.backend = backend;
        this.target = target;
        this.source = reader;
    }

    //-------------------------------------------------------------------------
    public Exporter(
        Backend backend,
        RequestCollection reader
    ) {
        this.backend = backend;
        this.target = null;
        this.source = reader;
    }
    
    //-------------------------------------------------------------------------
    public void exportItem(
        OutputStream out,
        PrintStream ps,
        List<Path> startPoints,
        Set<Path> allExportedObjects,
        Set<Path> allReferencedObjects,
        Map<String,Object> context,
        int level
    ) throws IOException, ServiceException {
        if(level >= 0) {
            List<Path> referencedObjects = new ArrayList<Path>();
            for(
                Iterator<Path> i = startPoints.iterator();
                i.hasNext();
            ) {
                Path startPoint = i.next();
                if(!allExportedObjects.contains(startPoint)) {
                    boolean isMultiFileExport = out instanceof ZipOutputStream;                        
                    if(isMultiFileExport) {
                        ((ZipOutputStream)out).putNextEntry(
                            new ZipEntry(
                                "data-" + 
                                (this.currentEntryId++) +
                                FILE_EXT_XML
                            )
                        );
                    }
                    
                    // derive schema name from identity
                    String qualifiedModelName = startPoint.get(0);
                    String namespace = qualifiedModelName.substring(0, qualifiedModelName.lastIndexOf(":"));
                    String modelName = qualifiedModelName.substring(qualifiedModelName.lastIndexOf(":") + 1);
                    ExportHandler exporter = new ExportHandler(
                        ps,
                        context
                    );
                    exporter.export(
                        Arrays.asList(new Path[]{startPoint}),
                        this.referenceFilter,
                        Collections.EMPTY_MAP,
                        "xri:+resource/" + namespace.replace(':', '/') + "/" + modelName + "/xmi/" + modelName + ".xsd",
                        level
                    );                    
                    // Update referenced and exported objects
                    referencedObjects.addAll(
                        exporter.getReferencedObjects()
                    );
                    allExportedObjects.addAll(
                        exporter.getExportedObjects()
                    );
                    allReferencedObjects.addAll(
                        exporter.getReferencedObjects()
                    );
                }
            }
            this.exportItem(
                out,
                ps,
                referencedObjects,
                allExportedObjects,
                allReferencedObjects,
                context,
                level-1
            );
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Export object with identity startFromIdentity. The export format is 
     * specified by exportProfile.
     * @return exported object as array of {name as String, mimeTypeas String, content as byte[]}
     */
    public Object[] exportItem(
        Path startFromIdentity,
        org.opencrx.kernel.base.jmi1.ExportProfile exportProfile,
        String referenceFilter,
        String itemMimeType
    ) throws ServiceException {
        
        try {            
            if(exportProfile != null) {
                referenceFilter = exportProfile.getReferenceFilter();
                itemMimeType = exportProfile.getMimeType();                
            }
            this.referenceFilter = new HashSet<String>();
            this.exportOptions = new HashMap<String,List<String>>();
            List<Path> startingIdentities = new ArrayList<Path>();
            startingIdentities.add(startFromIdentity);
            if(referenceFilter != null) {
                // Starting identities are separated by $
                if(referenceFilter.indexOf("$") > 0) {
                    StringTokenizer tokenizer = new StringTokenizer(referenceFilter.substring(0, referenceFilter.indexOf("$")), "\t\n ;,", false);
                    while(tokenizer.hasMoreTokens()) {
                        startingIdentities.add(
                            new Path(tokenizer.nextToken())
                        );
                    }
                    referenceFilter = referenceFilter.substring(referenceFilter.indexOf("$") + 1);
                }                
                // Options are separated by !
                String options = null;
                if(referenceFilter.indexOf("!") > 0) {
                    options = referenceFilter.substring(referenceFilter.indexOf("!") + 1);
                    referenceFilter = referenceFilter.substring(0, referenceFilter.indexOf("!"));
                }
                // Parse reference filter
                StringTokenizer referenceFilterTokenizer = new StringTokenizer(referenceFilter, "\t\n ;,", false);
                while(referenceFilterTokenizer.hasMoreTokens()) {
                    this.referenceFilter.add(
                        referenceFilterTokenizer.nextToken()
                    );
                }
                // Parse options. Options are of the format String[String;...], String[String;...]
                if(options != null) {
                    StringTokenizer optionsTokenizer = new StringTokenizer(options, "\t\n ,", false);
                    while(optionsTokenizer.hasMoreTokens()) {
                        String option = optionsTokenizer.nextToken();
                        String optionName = null;
                        List<String> optionParams = new ArrayList<String>();
                        if(option.indexOf("[") > 0) {
                            optionName = option.substring(0, option.indexOf("["));
                            StringTokenizer optionParamsTokenizer = new StringTokenizer(
                                option.substring(option.indexOf("[") + 1), 
                                ";]", 
                                false
                            );
                            while(optionParamsTokenizer.hasMoreTokens()) {
                                optionParams.add(
                                    optionParamsTokenizer.nextToken()
                                );
                            }
                        }
                        else {
                            optionName = option;
                        }
                        this.exportOptions.put(
                            optionName,
                            optionParams
                        );
                    }
                }
            }
            this.exportMimeType = itemMimeType == null ? 
                MIME_TYPE_XML :
                itemMimeType;
            Set<Path> allExportedObjects = new TreeSet<Path>();
            Set<Path> allReferencedObjects = new TreeSet<Path>();
            
            // 2-pass export
            // 1) determine transitive closure of objects to export
            // 2) export

            Map<String,Object> context = new HashMap<String,Object>();
            if((exportProfile != null) && (exportProfile.getTemplate() != null)) {
                context.put(
                    "template", 
                    exportProfile.getTemplate()
                );
            }            
            boolean isMultiFileExport = this.exportMimeType.equals(MIME_TYPE_XML);
            // Pass 1
            OutputStream out = isMultiFileExport 
                ? new ZipOutputStream(new NullOutputStream())
                : new NullOutputStream();
            PrintStream ps = new PrintStream(out);
            this.currentEntryId = 0;
            this.exportItem(
                out, 
                ps,
                startingIdentities,
                allExportedObjects,
                allReferencedObjects,
                context,
                MAX_LEVELS
            );
            // Prepare starting points. These are all referenced objects which
            // are not composite to either other referenced objects or exported objects
            List<Path> startingPoints = new ArrayList<Path>(allReferencedObjects);
            for(Iterator<Path> i = startingPoints.iterator(); i.hasNext(); ) {
                Path pi = i.next();
                for(Iterator<Path> j = allReferencedObjects.iterator(); j.hasNext(); ) {
                    Path pj = j.next();
                    if(pi.size() > pj.size() && pi.startsWith(pj)) {
                        i.remove();
                        break;
                    }
                }
            }
            for(Iterator<Path> i = startingPoints.iterator(); i.hasNext(); ) {
                Path pi = i.next();
                for(Iterator<Path> j = allExportedObjects.iterator(); j.hasNext(); ) {
                    Path pj = j.next();
                    if(pi.size() > pj.size() && pi.startsWith(pj)) {
                        i.remove();
                        break;
                    }
                }
            }
                        
            // Pass 2
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            out = isMultiFileExport 
                ? new ZipOutputStream(bs)
                : bs;
            ps = new PrintStream(out);
            context.keySet().retainAll(Arrays.asList("template"));
            // export startFrom with traverseContent = false
            this.currentEntryId = 0;
            this.exportItem(
                out, 
                ps,
                startingIdentities,
                new TreeSet<Path>(),
                new TreeSet<Path>(),
                context,
                0
            );            
            // export contained and referenced objects with traverseContent = true
            this.exportItem(
                out, 
                ps,
                startingPoints,
                new TreeSet<Path>(),
                new TreeSet<Path>(),
                context,
                0
            );
            // post-process user objects
            for(Object object: context.values()) {
                if(object instanceof HSSFWorkbook) {
                    try {
                        ((HSSFWorkbook)object).write(ps);
                    }
                    catch(IOException e) {
                        throw new ServiceException(e);
                    }
                }
            }
            ps.close();
            String contentMimeType = isMultiFileExport ? Base.MIME_TYPE_ZIP : this.exportMimeType;
            String contentName = "Export" + (contentMimeType.equals(Base.MIME_TYPE_ZIP)
                ? FILE_EXT_ZIP : contentMimeType.equals(MIME_TYPE_EXCEL) ? FILE_EXT_XLS : FILE_EXT_BIN);
            return new Object[]{
                contentName,
                contentMimeType,
                bs.toByteArray()
            };
        }
        catch(IOException e) {
            throw new ServiceException(e);
        }
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String MIME_TYPE_XML = "text/xml";
    public static final String MIME_TYPE_EXCEL = "application/x-excel";
    public static final String FILE_EXT_XLS = ".xls";
    public static final String FILE_EXT_XML = ".xml";
    public static final String FILE_EXT_BIN = ".bin";
    public static final String FILE_EXT_ZIP = ".zip";
    protected static final int MAX_LEVELS = 4;
    protected int currentEntryId = 0;
    protected final Backend backend;
    protected final RequestCollection target;
    protected final RequestCollection source;
    protected Set<String> referenceFilter = null;
    protected Map<String,List<String>> exportOptions = null;
    protected String exportMimeType = null;

    //-------------------------------------------------------------------------
    class ExportHandler {
        
        //---------------------------------------------------------------------
        public ExportHandler(
            PrintStream out, 
            Map<String,Object> context
        ) {
            this.out = out;
            this.context = context;
            this.referencedObjects = new HashSet<Path>();
            this.exportedObjects = new HashSet<Path>();
        }
        
        //---------------------------------------------------------------------
        public Set<Path> getReferencedObjects(
        ) {
            return this.referencedObjects;
        }
        
        //---------------------------------------------------------------------
        public Set<Path> getExportedObjects(
        ) {
            return this.exportedObjects;
        }
        
        //---------------------------------------------------------------------
        void updateReferencedObjects(
            DataproviderObject_1_0 object
        ) throws ServiceException {
            Exporter.this.backend.getCloneable().collectReferencedObjects(
                object,
                Exporter.this.referenceFilter,
                this.referencedObjects
            );
        }

        //---------------------------------------------------------------------
        protected TraversalHandler getTraversalHandler(
            String schemaString
        ) throws ServiceException {
            TraversalHandler traversalHandler = null;
            if(MIME_TYPE_XML.equals(Exporter.this.exportMimeType)) {
                XmlContentHandler contentHandler = new NonClosingXmlContentHandler(this.out);        
                contentHandler.setAutoCollation(true);
                contentHandler.setEncoding("UTF-8");
                contentHandler.setIndentation(true);
                contentHandler.setIndentationLength(4);        
                TaggingXMLTraversalHandler taggingXmlTraversalHandler = new TaggingXMLTraversalHandler(
                    Exporter.this.backend.getModel(),
                    "http://www.w3.org/2001/XMLSchema-instance",
                    schemaString
                );
                taggingXmlTraversalHandler.setContentHandler(
                    contentHandler
                );
                traversalHandler = taggingXmlTraversalHandler;
            }
            else if(MIME_TYPE_EXCEL.equals(Exporter.this.exportMimeType)) {
                traversalHandler = new ExcelTraversalHandler(
                    this.context
                );
            }
            else {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE, 
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("mimetype", Exporter.this.exportMimeType)
                    },
                    "Unsupported mime type. Unable to export item."
                );
            }
            return traversalHandler;
        }
        
        //---------------------------------------------------------------------
        public void export(
            List<Path> startPoints,
            Set<String> referenceFilters,
            Map<String,Filter> attributeFilters,
            String schemaString,
            int maxObjects
        ) throws ServiceException {
            Traverser traverser = new ProviderTraverser(
                Exporter.this.source, 
                Exporter.this.backend.getModel(), 
                startPoints, 
                referenceFilters,
                attributeFilters
            );
            traverser.setTraversalHandler(
                this.getTraversalHandler(
                    schemaString
                )
            );
            try {
                traverser.traverse();
            }
            catch(Exception e) {
                throw new ServiceException(e);
            }
        }

        //---------------------------------------------------------------------
        // ExcelTraversalHandler
        //---------------------------------------------------------------------
        class ExcelTraversalHandler implements TraversalHandler {
         
            public ExcelTraversalHandler(
                Map<String,Object> context
            ) {
                this.wb = (HSSFWorkbook)context.get("wb");
                if(this.wb == null) {
                    HSSFWorkbook wb = null;
                    // Create workbook from template
                    if(context.get("template") != null) {
                        Object template = context.get("template");
                        try {
                            if(template instanceof InputStream) {
                                wb = new HSSFWorkbook(
                                    (InputStream)template,
                                    true
                                );
                            }
                            else if(template instanceof org.opencrx.kernel.document1.jmi1.Document) {
                                org.opencrx.kernel.document1.jmi1.Document templateDocument = (org.opencrx.kernel.document1.jmi1.Document)template;
                                DocumentRevision revision = templateDocument.getHeadRevision();
                                if(revision instanceof MediaContent) {
                                    BinaryLargeObject content = ((MediaContent)revision).getContent();
                                    wb = new HSSFWorkbook(
                                        content.getContent(),
                                        true
                                    );
                                    // Replace template document by its stream representation
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    wb.write(bos);
                                    bos.close();
                                    context.put("template", new ByteArrayInputStream(bos.toByteArray()));
                                }
                            }
                        }
                        catch(Exception e) {
                            ServiceException e0 = new ServiceException(e);
                        }
                    }
                    if(wb == null) {
                        wb = new HSSFWorkbook();
                    }
                    context.put(
                        "wb",
                        this.wb = wb
                    );
                }
                this.cellStyleHidden = this.wb.createCellStyle();
                this.cellStyleHidden.setHidden(false);
                this.objectCount = 0;
                this.metainf = this.wb.getSheet("META-INF");
                HSSFCell mCell = (this.metainf == null) || (this.metainf.getRow(1) == null) 
                    ? null 
                    : this.metainf.getRow(1).getCell(this.getCellNum(this.metainf, "META-INF", "MaxObjects"));                
                this.objectLimit = mCell == null
                    ? MAX_OBJECTS
                    : new Double(mCell.getNumericCellValue()).intValue();                
            }

            private short getCellNum(
                HSSFSheet sheet,
                String sheetName,
                String attributeName
            ) {
                HSSFRow heading = sheet.getRow(0);
                short num = 0;
                for(Iterator<HSSFCell> i = heading.cellIterator(); i.hasNext(); ) {
                    HSSFCell cell = i.next();
                    if(attributeName.equals(cell.getStringCellValue())) {
                        return cell.getCellNum();
                    }
                    num++;
                }
                HSSFCell cell = heading.createCell(num);
                cell.setCellValue(new HSSFRichTextString(attributeName));
                // Set column width to 0 if attribute is not listed in export options 
                if(Exporter.this.exportOptions.get(sheetName) != null) {
                    List<String> attributeNames = Exporter.this.exportOptions.get(sheetName);                    
                    if(!attributeNames.isEmpty() && !attributeNames.contains(attributeName)) {
                        sheet.setColumnWidth(num, (short)0);                    
                    }
                }
                return num;
            }
                
            private int getMaxValueSize(
                DataproviderObject_1_0 object
            ) {
                int maxIndex = 0;
                for(String attributeName: (Set<String>)object.attributeNames()) {
                    if(!NOT_EXPORTED_ATTRIBUTES.contains(attributeName)) {
                        maxIndex = Math.max(maxIndex, object.values(attributeName).size());
                    }
                }
                return maxIndex;
            }
            
            public void contentComplete(
                Path objectPath,
                String objectClassName, 
                List containedReferences
            ) throws ServiceException {
            }

            public void endObject(
                String qualifiedName
            ) throws ServiceException {
            }

            public void endReference(
                String reference
            ) throws ServiceException {
                this.sheet = null;
            }

            public void endTraversal(
            ) throws ServiceException {
            }

            public boolean featureComplete(
                Path reference,
                DataproviderObject_1_0 object
            ) throws ServiceException {
                this.objectCount++;
                if(
                    (object.getValues(SystemAttributes.OBJECT_CLASS) != null) && 
                    (object.path().size() > 5)
                ) {
                    ExportHandler.this.updateReferencedObjects(object);
                    ExportHandler.this.exportedObjects.add(object.path());
                    String sheetName = reference.getBase();
                    if(sheetName.length() > 0) {
                        sheetName = Character.toUpperCase(sheetName.charAt(0)) + sheetName.substring(1);
                    }
                    this.sheet = this.wb.getSheet(sheetName);
                    if(this.sheet == null) {
                        this.sheet = this.wb.createSheet(sheetName);
                        // Create named groups for DATA
                        String cellName = sheetName + ".DATA";
                        try {
                            HSSFName namedCell = this.wb.createName();
                            namedCell.setNameName(cellName);
                            namedCell.setReference(sheetName + "!$A$2:$CY$65536");
                        } catch(Exception e) {}
                        // Create named groups for COLUMN
                        try {
                            HSSFName namedCell = this.wb.createName();
                            namedCell.setNameName(sheetName + ".COLUMN");
                            namedCell.setReference(sheetName + "!$A$1:$CY$1");
                        }
                        catch(Exception e) {}
                        // Heading row which contains the attribute names
                        this.sheet.createRow(0);
                        // Create columns specified by export options
                        if(Exporter.this.exportOptions.get(sheetName) != null) {
                            List<String> attributeNames = Exporter.this.exportOptions.get(sheetName);
                            for(String attributeName: attributeNames) {
                                this.getCellNum(
                                    this.sheet,
                                    sheetName, 
                                    attributeName
                                );
                            }                            
                        }
                    }                    
                    int maxIndex = this.getMaxValueSize(object);
                    String xri = object.path().toXri();
                    HSSFRichTextString parentXri = new HSSFRichTextString(reference.getParent().toXri());
                    HSSFRichTextString objectId = new HSSFRichTextString(object.path().getBase());
                    for(int i = 0; i < maxIndex; i++) {
                        HSSFRow row = this.sheet.createRow(this.sheet.getLastRowNum() + 1);
                        HSSFCell cell = row.createCell(this.getCellNum(this.sheet, sheetName, "XRI"));
                        cell.setCellValue(new HSSFRichTextString(xri + (i > 0 ? "*" + i : "")));
                        cell = row.createCell(this.getCellNum(this.sheet, sheetName, "XRI.PARENT"));
                        cell.setCellValue(parentXri);
                        cell = row.createCell(this.getCellNum(this.sheet, sheetName, "ID"));
                        cell.setCellValue(objectId);
                        cell = row.createCell(this.getCellNum(this.sheet, sheetName, "IDX"));
                        cell.setCellValue(i);
                        for(String attributeName: (Set<String>)object.attributeNames()) {
                            if(!NOT_EXPORTED_ATTRIBUTES.contains(attributeName)) {
                                Object value = object.values(attributeName).get(i);
                                if(value != null) {
                                    cell = row.createCell(this.getCellNum(this.sheet, sheetName, attributeName));
                                    if(value instanceof InputStream) {
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        int b;
                                        try {
                                            while((b = ((InputStream)value).read()) != -1) {
                                                bytes.write(b);
                                            }
                                        } catch(IOException e) {}
                                        String encodedBytes = Base64.encode(bytes.toByteArray());
                                        if(encodedBytes.length() < Short.MAX_VALUE) {
                                            cell.setCellValue(new HSSFRichTextString(encodedBytes));
                                        }                                        
                                    }
                                    else if(value instanceof byte[]) {
                                        String encodedBytes = Base64.encode((byte[])value);
                                        if(encodedBytes.length() < Short.MAX_VALUE) {
                                            cell.setCellValue(new HSSFRichTextString(encodedBytes));
                                        }
                                    }
                                    else if(value instanceof Number) {
                                        cell.setCellValue(((Number)value).doubleValue());
                                    }
                                    else if(value instanceof Boolean) {
                                        cell.setCellValue(((Boolean)value).booleanValue());
                                    }                                    
                                    else if(value instanceof Path) {
                                        cell.setCellValue(new HSSFRichTextString(((Path)value).toXri()));
                                    }                                    
                                    else {
                                        String stringifiedValue = value.toString();
                                        if(stringifiedValue.length() > Short.MAX_VALUE) {                                        
                                            cell.setCellValue(new HSSFRichTextString(stringifiedValue.substring(0, Short.MAX_VALUE-5) + "..."));
                                        }
                                        else {
                                            cell.setCellValue(new HSSFRichTextString(stringifiedValue));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HSSFCell mCell = (this.metainf == null) || (this.metainf.getRow(1) == null) 
                        ? null 
                        : this.metainf.getRow(1).getCell(this.getCellNum(this.metainf, "META-INF", sheetName));
                    int rowLimit = mCell == null 
                        ? this.objectLimit 
                        : new Double(mCell.getNumericCellValue()).intValue();
                    return this.sheet.getLastRowNum() < rowLimit;
                }
                else {
                    return this.objectCount < this.objectLimit;
                }
            }

            public Map<String,String> getAttributeTags(
                DataproviderObject_1_0 object
            ) throws ServiceException {
                return null;
            }

            public short getTransactionBehavior(
            ) {
                return 0;
            }

            public void referenceComplete(
                Path reference, 
                Collection objectIds
            ) throws ServiceException {
            }

            public void setTransactionBehavior(
                short transactionBehavior
            ) throws ServiceException {
            }

            public boolean startObject(
                Path reference,
                String qualifiedName,
                String qualifierName, 
                String id, 
                short operation
             ) throws ServiceException {
                return this.objectCount < this.objectLimit;
            }

            public boolean startReference(
                String qualifiedName
            ) throws ServiceException {
                return true;
            }

            public void startTraversal(
                List startPaths
            ) throws ServiceException {
                this.sheet = null;
            }
            
            private final Set<String> NOT_EXPORTED_ATTRIBUTES = new HashSet<String>(
                Arrays.asList(SystemAttributes.OBJECT_INSTANCE_OF)
            );
            
            private final static int MAX_OBJECTS = 2000;
            
            private HSSFWorkbook wb = null;
            private HSSFSheet sheet = null;
            private HSSFSheet metainf = null;
            private HSSFCellStyle cellStyleHidden = null;
            private int objectLimit;
            private int objectCount;
            
        }
        
        //---------------------------------------------------------------------
        // TaggingXMLTraversalHandler
        //---------------------------------------------------------------------
        class TaggingXMLTraversalHandler
            extends XMLExportHandler {
         
            public TaggingXMLTraversalHandler(
                Model_1_0 model,
                String schemaInstance, 
                String schemaLocation
            ) {
                super(
                    model, 
                    schemaInstance, 
                    schemaLocation
                );
            }

            public Map<String,String> getAttributeTags(
                DataproviderObject_1_0 object
            ) throws ServiceException {
                return null;
            }

            public boolean featureComplete(
                Path reference,
                DataproviderObject_1_0 object
            ) throws ServiceException {
                ExportHandler.this.updateReferencedObjects(object);
                // skip dummy and segment-level objects
                if((object.getValues(SystemAttributes.OBJECT_CLASS) != null) && (object.path().size() > 5)) {
                    ExportHandler.this.exportedObjects.add(object.path());
                }
                return super.featureComplete(
                    reference,
                    object
                );
            }            
        }
        
        //---------------------------------------------------------------------
        class NonClosingXmlContentHandler
            extends XmlContentHandler {

            public NonClosingXmlContentHandler(
                PrintStream stream
            ) {
                super(stream);
            }
                        
            public void endDocument(
            ) throws ServiceException {
                super.endDocument(false);                
            }
        }
        
        //---------------------------------------------------------------------
        int currentEntryId = 0;        
        final Set<Path> referencedObjects;
        final Set<Path> exportedObjects;
        final Map<String,Object> context;
        final PrintStream out;
    }
    
}

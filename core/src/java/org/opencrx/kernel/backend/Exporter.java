/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Exporter.java,v 1.40 2009/05/16 22:19:32 wfro Exp $
 * Description: Exporter
 * Revision:    $Revision: 1.40 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/16 22:19:32 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.oasisopen.jmi1.RefContainer;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.mof.cci.AggregationKind;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.cci.Multiplicities;
import org.openmdx.base.mof.cci.PrimitiveTypes;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.base.text.conversion.XMLEncoder;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.kernel.exception.BasicException;
import org.w3c.cci2.BinaryLargeObject;

public class Exporter extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Exporter());
	}
	
    //-------------------------------------------------------------------------
	public static Exporter getInstance(
	) throws ServiceException {
		return getInstance(Exporter.class);
	}

	//-------------------------------------------------------------------------
	protected Exporter(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    static class NullOutputStream
        extends OutputStream {

        public NullOutputStream() {            
        }        
        public void close() throws IOException {
        }
        public void flush() throws IOException {
        }
        public void write(byte[] b, int off, int len) throws IOException {
        }
        public void write(byte[] b) throws IOException {
        }
        public void write(int b) throws IOException {
        }
    }
    	
    //-----------------------------------------------------------------------
    static class ExportParams {
    
    	public ExportParams(
    	) {
    		this.referenceFilter = new HashSet<String>();
    		this.options = new HashMap<String,List<String>>();
    		this.context = new HashMap<String,Object>();
    		this.currentEntryId = 0;
    	}

    	public Set<String> getReferenceFilter(
    	) {
    		return this.referenceFilter;
    	}
    	
    	public Map<String,List<String>> getOptions(
    	) {
    		return this.options;
    	}
    	
    	public String getMimeType(
    	) {
    		return this.mimeType;
    	}
    	
    	public Map<String,Object> getContext(
    	) {
    		return this.context;
    	}
    	
    	public void setMimeType(
    		String mimeType
    	) {
    		this.mimeType = mimeType;
    	}
    	
    	public int getNextEntryId(
    	) {
    		return this.currentEntryId++;
    	}
    	
    	public void resetEntryId(
    	) {
    		this.currentEntryId = 0;
    	}
    	
    	private final Set<String> referenceFilter;
		private final Map<String,List<String>> options;
		private final Map<String,Object> context;
		private String mimeType;
		private int currentEntryId;
    	
    }
    
    static class XMLWriter {

        static private class Attribute{
            Attribute(String qName, String value) {
                _qName = qName;
                _value = value;
            }

            String _qName;
            String _value;
        }

        static public class Attributes{

            public Attributes() {
                _attributes = new ArrayList<Attribute>();
            }

            public void addCDATA(
                String qName, 
                String value
            ) {
                this._attributes.add(new Attribute(qName, value));
            }

            public String getQName(int index) {
                return this._attributes.get(index)._qName;

            }

            public String getValue(int index) {
                return this._attributes.get(index)._value;
            }

            public int getLength() {
                return this._attributes.size();
            }

            private List<Attribute> _attributes; 
        }

        public XMLWriter(
        	PrintStream out
        ) {
            this.out = out;
            this.qNameStack = new Stack<String>();
        }

        public void startElement(
            String namespaceURI,
            String localName,
            String qName,
            Attributes atts
        ) throws ServiceException {
            this.out.print('\n');
            for (int i = 0; i < this.qNameStack.size(); i++) {
                this.out.print(this.indentString);
            }
            this.out.print('<');
            this.out.print(qName);
            if (atts.getLength() > 0) {
                for (int i = 0; i < atts.getLength(); i++) {
                    this.out.print(" ");
                    this.out.print(XMLEncoder.encode(atts.getQName(i)));
                    this.out.print('=');
                    this.out.print('"');
                    this.out.print(XMLEncoder.encode(atts.getValue(i)));
                    this.out.print('"');
                }
            }
            this.out.write('>');
            this.qNameStack.push(qName);
        }

        public void endElement(
            String namespaceURI,
            String localName,
            String qName,
            boolean newLine
        ) throws ServiceException {
            String expectedQName = this.qNameStack.pop();
            if (!expectedQName.equals(qName)) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE,
                    "Non matching qName for XML tag.",
                    new BasicException.Parameter("qName", qName),
                    new BasicException.Parameter("expected qName", expectedQName)
                );
            }
            if(newLine) {
                this.out.print('\n');
                for (int i = 0; i < this.qNameStack.size(); i++) {
                    this.out.print(this.indentString);
                }            	
            }
            this.out.print("</");
            this.out.print(XMLEncoder.encode(qName));
            this.out.print(">");
        }

        public void characters(
            char[] ch,
            int start,
            int length
        ) throws ServiceException {
            String s = new String(ch, start, length);
            this.out.print(XMLEncoder.encode(s));
        }

        public void startDocument(
        ) throws ServiceException {
            this.out.print("<?xml version=\"1.0\" encoding=\"");
            this.out.print("UTF-8");
            this.out.print("\"?>");
        }

        public void comment(
            String comment
        ) throws ServiceException {
            this.out.print("<!-- " + comment + " -->");
        }

        public void endDocument(
        ) throws ServiceException {
            this.out.flush();
            if(!this.qNameStack.isEmpty()) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE,
                    "Open elements while endDocument().",
                    new BasicException.Parameter("elements", this.qNameStack.toString())
                );
            }
        }

        public void processingInstruction(
            String target,
            String data
        ) throws ServiceException {
            this.out.print("\n<?");
            this.out.print(target);
            this.out.print(' ');
            this.out.print(data);
            this.out.print("?>");
        }

        private final PrintStream out;
        private String indentString = "\t";
        private final Stack<String> qNameStack;
    }

    //-------------------------------------------------------------------------
    public void exportItem(
        OutputStream out,
        PrintStream ps,
        Set<RefObject_1_0> startPoints,
        Set<RefObject_1_0> allExportedObjects,
        Set<RefObject_1_0> allReferencedObjects,
        ExportParams params,
        int level
    ) throws IOException, ServiceException {
        if(level >= 0) {
            Set<RefObject_1_0> referencedObjects = new HashSet<RefObject_1_0>();
            for(RefObject_1_0 startPoint: startPoints) {
                if(!allExportedObjects.contains(startPoint)) {
                    boolean isMultiFileExport = out instanceof ZipOutputStream;                        
                    if(isMultiFileExport) {
                        ((ZipOutputStream)out).putNextEntry(
                            new ZipEntry(
                                "data-" + 
                                (params.getNextEntryId()) +
                                FILE_EXT_XML
                            )
                        );
                    }                    
                    // derive schema name from identity
                    String qualifiedModelName = startPoint.refGetPath().get(0);
                    String namespace = qualifiedModelName.substring(0, qualifiedModelName.lastIndexOf(":"));
                    String modelName = qualifiedModelName.substring(qualifiedModelName.lastIndexOf(":") + 1);
                    ObjectExporter objectExporter = new ObjectExporter(
                        ps,
                        params
                    );
                    objectExporter.export(
                        startPoint,
                        "xri:+resource/" + namespace.replace(':', '/') + "/" + modelName + "/xmi/" + modelName + ".xsd",
                        level
                    );                    
                    // Update referenced and exported objects
                    referencedObjects.addAll(
                        objectExporter.getReferencedObjects()
                    );
                    allExportedObjects.addAll(
                        objectExporter.getExportedObjects()
                    );
                    allReferencedObjects.addAll(
                        objectExporter.getReferencedObjects()
                    );
                }
            }
            this.exportItem(
                out,
                ps,
                referencedObjects,
                allExportedObjects,
                allReferencedObjects,
                params,
                level-1
            );
        }
    }

    //-----------------------------------------------------------------------
    public Object[] exportItem(
        BasicObject startFrom,
        org.opencrx.kernel.base.jmi1.ExportProfile exportProfile,
        String referenceFilter,
        String itemMimeType
    ) throws ServiceException {        
        try {            
        	PersistenceManager pm = JDOHelper.getPersistenceManager(exportProfile);
            if(exportProfile != null) {
                referenceFilter = exportProfile.getReferenceFilter();
                itemMimeType = exportProfile.getMimeType();                
            }
            ExportParams exportParams = new ExportParams();
            Set<RefObject_1_0> startPoints = new HashSet<RefObject_1_0>();
            startPoints.add(startFrom);
            if(referenceFilter != null) {
                // Starting identities are separated by $
                if(referenceFilter.indexOf("$") > 0) {
                    StringTokenizer tokenizer = new StringTokenizer(referenceFilter.substring(0, referenceFilter.indexOf("$")), "\t\n ;,", false);
                    while(tokenizer.hasMoreTokens()) {
                        startPoints.add(
                            (RefObject_1_0)pm.getObjectById(new Path(tokenizer.nextToken()))
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
                	exportParams.getReferenceFilter().add(
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
                        exportParams.getOptions().put(
                            optionName,
                            optionParams
                        );
                    }
                }
            }
            exportParams.setMimeType(
            	itemMimeType == null ? 
	                MIME_TYPE_XML :
	                itemMimeType
	        );
            Set<RefObject_1_0> allExportedObjects = new HashSet<RefObject_1_0>();
            Set<RefObject_1_0> allReferencedObjects = new HashSet<RefObject_1_0>();            
            // 2-pass export
            // 1) determine transitive closure of objects to export
            // 2) export
            if((exportProfile != null) && (exportProfile.getTemplate() != null)) {
                exportParams.getContext().put(
                    "template", 
                    exportProfile.getTemplate()
                );
            }            
            boolean isMultiFileExport = exportParams.getMimeType().equals(MIME_TYPE_XML);
            // Pass 1
            OutputStream out = isMultiFileExport ? 
            	new ZipOutputStream(new NullOutputStream()) : 
            	new NullOutputStream();
            PrintStream ps = new PrintStream(out);
            this.exportItem(
                out, 
                ps,
                startPoints,
                allExportedObjects,
                allReferencedObjects,
                exportParams,
                MAX_LEVELS
            );
            // Prepare starting points. These are all referenced objects which
            // are not composite to either other referenced objects or exported objects
            Set<RefObject_1_0> nonCompositeStartingPoints = new HashSet<RefObject_1_0>(allReferencedObjects);
            for(Iterator<RefObject_1_0> i = nonCompositeStartingPoints.iterator(); i.hasNext(); ) {
                RefObject_1_0 pi = i.next();
                for(Iterator<RefObject_1_0> j = allReferencedObjects.iterator(); j.hasNext(); ) {
                    RefObject_1_0 pj = j.next();
                    if(pi.refGetPath().size() > pj.refGetPath().size() && pi.refGetPath().startsWith(pj.refGetPath())) {
                        i.remove();
                        break;
                    }
                }
            }
            for(Iterator<RefObject_1_0> i = nonCompositeStartingPoints.iterator(); i.hasNext(); ) {
                RefObject_1_0 pi = i.next();
                for(Iterator<RefObject_1_0> j = allExportedObjects.iterator(); j.hasNext(); ) {
                    RefObject_1_0 pj = j.next();
                    if(pi.refGetPath().size() > pj.refGetPath().size() && pi.refGetPath().startsWith(pj.refGetPath())) {
                        i.remove();
                        break;
                    }
                }
            }
            // Pass 2
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            out = isMultiFileExport ? 
            	new ZipOutputStream(bs) : 
            	bs;
            ps = new PrintStream(out);
            exportParams.getContext().keySet().retainAll(
            	Arrays.asList("template")
            );
            // export startFrom with traverseContent = false
            exportParams.resetEntryId();
            this.exportItem(
                out, 
                ps,
                startPoints,
                new HashSet<RefObject_1_0>(),
                new HashSet<RefObject_1_0>(),
                exportParams,
                0
            );            
            // export contained and referenced objects with traverseContent = true
            this.exportItem(
                out, 
                ps,
                nonCompositeStartingPoints,
                new HashSet<RefObject_1_0>(),
                new HashSet<RefObject_1_0>(),
                exportParams,
                0
            );
            // post-process user objects
            for(Object object: exportParams.getContext().values()) {
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
            String contentMimeType = isMultiFileExport ? Base.MIME_TYPE_ZIP : exportParams.getMimeType();
            String contentName = "Export" + (contentMimeType.equals(Base.MIME_TYPE_ZIP) ? 
            	FILE_EXT_ZIP : 
            	contentMimeType.equals(MIME_TYPE_EXCEL) ? FILE_EXT_XLS : FILE_EXT_BIN);
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
    
    //-------------------------------------------------------------------------
    static class ObjectExporter {
        
        //---------------------------------------------------------------------
        interface ContentHandler {
        	
            public boolean startReference(
            	String name
            ) throws ServiceException;

            public void endReference(
            	String reference
            ) throws ServiceException;

            public boolean startObject(
    			RefObject_1_0 object,
    			String id,
    			String qualifierName,
    			String qualifiedName
            ) throws ServiceException;

            public void endObject(
    			RefObject_1_0 object,
    			String qualifiedName
            ) throws ServiceException;

            public boolean featureComplete(
                RefObject_1_0 object
            ) throws ServiceException;

            public void startTraversal(
            	RefObject_1_0 object
            ) throws ServiceException;

            public void endTraversal(
            	RefObject_1_0 object
            ) throws ServiceException;
        	
        }
        
        //---------------------------------------------------------------------
        public ObjectExporter(
            PrintStream out, 
            ExportParams params
        ) {
            this.out = out;
            this.params = params;
            this.referencedObjects = new HashSet<RefObject_1_0>();
            this.exportedObjects = new HashSet<RefObject_1_0>();
        }
        
        //---------------------------------------------------------------------
        public Set<RefObject_1_0> getReferencedObjects(
        ) {
            return this.referencedObjects;
        }
        
        //---------------------------------------------------------------------
        public Set<RefObject_1_0> getExportedObjects(
        ) {
            return this.exportedObjects;
        }
        
        //---------------------------------------------------------------------
        public String getQualifierName(
        	RefObject_1_0 object
        ) throws ServiceException {
        	Model_1_0 model = Model_1Factory.getModel();
        	ModelElement_1_0 objectClass = model.getElement(object.refClass().refMofId());
            String qualifierName = null;
            if(!objectClass.objGetList("compositeReference").isEmpty()) {
                ModelElement_1_0 compReference =
                    model.getElement(
                        ((Path) objectClass.objGetValue("compositeReference"))
                        .getBase());
                ModelElement_1_0 associationEnd =
                    model.getElement(
                        ((Path) compReference.objGetValue("referencedEnd"))
                        .getBase());
                qualifierName =
                    (String) associationEnd.objGetValue("qualifierName");
            }
            else if (
            	"org:openmdx:base:Authority".equals(objectClass.objGetValue("qualifiedName"))) {
                qualifierName = "name";
            }
            else {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE,
                    "no composite reference found for class.",
                    new BasicException.Parameter[] {
                        new BasicException.Parameter("class", objectClass)
                    });
            }
            return qualifierName;
        }
        
        //---------------------------------------------------------------------
        public void export(
            RefObject_1_0 object,
            String schemaString,
            int maxObjects
        ) throws ServiceException {
        	ContentHandler contentHandler = null;
            if(Exporter.MIME_TYPE_XML.equals(this.params.getMimeType())) {
            	contentHandler = new XMLContentHandler(
                    "http://www.w3.org/2001/XMLSchema-instance",
                    schemaString            		
            	);
            }
            else if(Exporter.MIME_TYPE_EXCEL.equals(this.params.getMimeType())) {
            	contentHandler = new ExcelContentHandler(
            		this.params.getContext()
            	);
            }
            else {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE, 
                    "Unsupported mime type. Unable to export item.",
                    new BasicException.Parameter("mimetype", this.params.getMimeType())
                );            	
            }
            contentHandler.startTraversal(
            	object
            );
        	this.exportObject(
        		object, 
        		contentHandler
        	);
            contentHandler.endTraversal(
            	object
            );
        }

        //---------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        private void exportObject(
        	RefObject_1_0 object,
        	ContentHandler contentHandler
        ) throws ServiceException {
        	Model_1_0 model = Model_1Factory.getModel();
        	String objectType = object.refClass().refMofId();
            String qualifierName = this.getQualifierName(object);
        	contentHandler.startObject(
        		object, 
        		object.refGetPath().getBase(), 
        		qualifierName, 
        		objectType
        	);
        	contentHandler.featureComplete(
        		object
        	);
            Map<String,ModelElement_1_0> references = (Map)model.getElement(
                objectType
            ).objGetValue("reference");
            for(ModelElement_1_0 featureDef: references.values()) {
                ModelElement_1_0 referencedEnd = model.getElement(
                    featureDef.objGetValue("referencedEnd")
                );
                boolean referenceIsComposite = 
                    model.isReferenceType(featureDef) &&
                    AggregationKind.COMPOSITE.equals(referencedEnd.objGetValue("aggregation"));
                boolean referenceIsShared = 
                    model.isReferenceType(featureDef) &&
                    AggregationKind.SHARED.equals(referencedEnd.objGetValue("aggregation"));            
                // Only navigate changeable references which are either 'composite' or 'shared'
                // Do not navigate references with aggregation 'none'.
                if(referenceIsComposite || referenceIsShared) {
                    String referenceName = (String)featureDef.objGetValue("name");
                    Set<String> referenceFilter = this.params.getReferenceFilter();
                    boolean matches = referenceFilter == null;
                    if(!matches) {
                        String qualifiedReferenceName = (String)featureDef.objGetValue("qualifiedName");
                        matches =
                            referenceFilter.contains(referenceName) ||
                            referenceFilter.contains(qualifiedReferenceName);
                    }
                    if(matches) {   
                    	List<?> content = ((RefContainer)object.refGetValue(referenceName)).refGetAll(null);
                    	contentHandler.startReference(referenceName);
                    	for(Object contained: content) {
                    		if(contained instanceof RefObject_1_0) {
	                    		this.exportObject(
	                    			(RefObject_1_0)contained, 
	                    			contentHandler
	                    		);
                    		}
                    	}
                    	contentHandler.endReference(referenceName);
                    }
                }
            }
        	contentHandler.endObject(
        		object, 
        		objectType
        	);      
        	this.exportedObjects.add(
        		object
        	);
        }
        
        //---------------------------------------------------------------------
        // ExcelTraversalHandler
        //---------------------------------------------------------------------
        class ExcelContentHandler implements ContentHandler {
         
            public ExcelContentHandler(
                Map<String,Object> context
            ) {
            	this.model = Model_1Factory.getModel();
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
                            e0.log();
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
                HSSFCell mCell = (this.metainf == null) || (this.metainf.getRow(1) == null) ? 
                	null : 
                	this.metainf.getRow(1).getCell(this.getCellNum(this.metainf, "META-INF", "MaxObjects"));                
                this.objectLimit = mCell == null ? 
                	MAX_OBJECTS : 
                	new Double(mCell.getNumericCellValue()).intValue();                
            }

            @SuppressWarnings({
                "unchecked", "deprecation"
            })
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
                if(ObjectExporter.this.params.getOptions().get(sheetName) != null) {
                    List<String> attributeNames = ObjectExporter.this.params.getOptions().get(sheetName);                    
                    if(!attributeNames.isEmpty() && !attributeNames.contains(attributeName)) {
                        sheet.setColumnWidth(num, (short)0);                    
                    }
                }
                return num;
            }
                
            @SuppressWarnings("unchecked")
            private Map<String,ModelElement_1_0> getAttributes(
            	RefObject_1_0 object
            ) throws ServiceException {
            	return ((Map<String,ModelElement_1_0>)this.model.getElement(
            		object.refClass().refMofId()).objGetValue("attribute")
            	);
            }
            
            private int getMaxValueSize(
                RefObject_1_0 object
            ) throws ServiceException {
                int maxIndex = 0;
                for(String attributeName: this.getAttributes(object).keySet()) {
                    if(!NOT_EXPORTED_ATTRIBUTES.contains(attributeName)) {
                    	Object value = null;
                    	try {
                    		value = object.refGetValue(attributeName);
                    	}
                    	catch(Exception e) {}
                    	if(value instanceof Collection) {
                    		maxIndex = Math.max(maxIndex, ((Collection<?>)value).size());
                    	}
                    	else if(value != null) {
                    		maxIndex = Math.max(maxIndex, 1);                    		
                    	}
                    }
                }
                return maxIndex;
            }
            
            public void endObject(
				RefObject_1_0 object,
				String qualifiedName
            ) throws ServiceException {
            }

            public void endReference(
                String reference
            ) throws ServiceException {
                this.sheet = null;
            }

            public void endTraversal(
            	RefObject_1_0 object
            ) throws ServiceException {
            }

            public boolean featureComplete(
                RefObject_1_0 object
            ) throws ServiceException {
                this.objectCount++;
                if(object.refGetPath().size() > 5) {
                    Exporter.ObjectExporter.this.exportedObjects.add(object);
                    String sheetName = object.refGetPath().getParent().getBase();
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
                        } 
                        catch(Exception e) {}
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
                        if(ObjectExporter.this.params.getOptions().get(sheetName) != null) {
                            List<String> attributeNames = ObjectExporter.this.params.getOptions().get(sheetName);
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
                    String xri = object.refGetPath().toXri();
                    HSSFRichTextString parentXri = new HSSFRichTextString(object.refGetPath().getParent().getParent().toXri());
                    HSSFRichTextString objectId = new HSSFRichTextString(object.refGetPath().getBase());
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
                        Map<String,ModelElement_1_0> attributes = this.getAttributes(object);
                        for(String attributeName: attributes.keySet()) {
                        	Object v = null;
                        	try {
                        		v = object.refGetValue(attributeName);
                        	}
                        	catch(Exception e) {}
                        	List<Object> values = new ArrayList<Object>();
                        	if(v instanceof Collection) {
                        		try {
                        			values.addAll((Collection<?>)v);
                        		}
                        		// Ignore in case some values can not be retrieved
                        		catch(Exception e) {}
                        	}
                        	else if(v != null) {
                        		values.add(v);
                        	}
                        	Object value = i < values.size() ? 
                        		values.get(i) : 
                        		null;
                            if(!NOT_EXPORTED_ATTRIBUTES.contains(attributeName)) {
                                if(value != null) {
                                    cell = row.createCell(this.getCellNum(this.sheet, sheetName, attributeName));
                                    if(value instanceof InputStream) {
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        int b;
                                        try {
                                            while((b = ((InputStream)value).read()) != -1) {
                                                bytes.write(b);
                                            }
                                        } 
                                        catch(IOException e) {}
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
                                    else if(value instanceof RefObject_1_0) {
                                        cell.setCellValue(new HSSFRichTextString(((RefObject_1_0)value).refGetPath().toXri()));
                                        Set<String> referenceFilter = ObjectExporter.this.params.getReferenceFilter();
                                        boolean matches = referenceFilter == null;
                                        if(!matches) {
                                            ModelElement_1_0 featureDef = attributes.get(attributeName);
                                            if(featureDef != null) {
                                                String qualifiedFeatureName = (String)featureDef.objGetValue("qualifiedName");
                                                matches = 
                                                    referenceFilter.contains(attributeName) ||
                                                    referenceFilter.contains(qualifiedFeatureName);
                                            }
                                        }
                                        if(matches) {
                                            ObjectExporter.this.referencedObjects.add((RefObject_1_0)value);
                                        }                                        
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
                    HSSFCell mCell = (this.metainf == null) || (this.metainf.getRow(1) == null) ? 
                    	null : 
                    	this.metainf.getRow(1).getCell(this.getCellNum(this.metainf, "META-INF", sheetName));
                    int rowLimit = mCell == null ? 
                    	this.objectLimit : 
                    	new Double(mCell.getNumericCellValue()).intValue();
                    return this.sheet.getLastRowNum() < rowLimit;
                }
                else {
                    return this.objectCount < this.objectLimit;
                }
            }

            public boolean startObject(
    			RefObject_1_0 object,
    			String id,
    			String qualifierName,
    			String qualifiedName
             ) throws ServiceException {
                return this.objectCount < this.objectLimit;
            }

            public boolean startReference(
                String qualifiedName
            ) throws ServiceException {
                return true;
            }

            public void startTraversal(
            	RefObject_1_0 object
            ) throws ServiceException {
                this.sheet = null;
            }
            
            private final static int MAX_OBJECTS = 2000;
            
            private final Model_1_0 model;
            private HSSFWorkbook wb = null;
            private HSSFSheet sheet = null;
            private HSSFSheet metainf = null;
            private HSSFCellStyle cellStyleHidden = null;
            private int objectLimit;
            private int objectCount;
            
        }
        
        //---------------------------------------------------------------------
        // XMLContentHandler
        //---------------------------------------------------------------------
        class XMLContentHandler implements ContentHandler {
        
        	public XMLContentHandler(
                String schemaInstance, 
                String schemaLocation
            ) {
        		this.model = Model_1Factory.getModel();
        		this.schemaInstance = schemaInstance;
        		this.schemaLocation = schemaLocation;
        		this.xmlWriter = new XMLWriter(ObjectExporter.this.out);
        	}

            private String toXML(
                Object elementName
            ) {
                return ((String) elementName).replace(':', '.');
            } 
        	
            private String toSimpleQualifiedName(
                String qualifiedName
            ) {
                return qualifiedName.substring(qualifiedName.lastIndexOf(':') + 1);
            }
                        
			public void endObject(
				RefObject_1_0 object,
				String qualifiedName
			) throws ServiceException {
		        this.xmlWriter.endElement(
		        	"", 
		        	"", 
		        	"_content",
		        	true		        	
		        );
		        String endElem = this.toXML(qualifiedName);
		        this.xmlWriter.endElement(
		        	"", 
		        	"", 
		        	endElem,
		        	true
		        );
            }

			public void endReference(
				String reference
			) throws ServiceException {
		        this.xmlWriter.endElement(
		            "",
		            "",
		            this.toSimpleQualifiedName(reference),
		            true
		        );				
            }

			@SuppressWarnings("unchecked")
            public boolean featureComplete(
				RefObject_1_0 object
			) throws ServiceException {
		        Map<String,String> tags = null;        
		        ModelElement_1_0 objectClass = this.model.getElement(
		        	object.refClass().refMofId()
		        );
		        this.xmlWriter.startElement(
		            "",
		            "",
		            "_object",
		            new XMLWriter.Attributes()
		        );
		        Map<String,ModelElement_1_0> modelAttributes = (Map)objectClass.objGetValue("attribute");
		        for(String attributeName: modelAttributes.keySet()) {
		        	if((objectClass != null) && !NOT_EXPORTED_ATTRIBUTES.contains(attributeName)) {
		                ModelElement_1_0 attributeDef = modelAttributes.get(attributeName);
		                Object attributeValue = null;
		                try {
		                	attributeValue = object.refGetValue(attributeName);
		                } catch(Exception e) {}
		                if(
		                	(attributeDef == null) ||
		                	(attributeValue == null) 
		                ) {
		                    continue;
		                }
		                String multiplicity = (String)attributeDef.objGetValue("multiplicity");
		                boolean isMultiValued = 
		                    multiplicity.equals(Multiplicities.MULTI_VALUE) || 
		                    multiplicity.equals(Multiplicities.SET) || 
		                    multiplicity.equals(Multiplicities.LIST) || 
		                    multiplicity.equals(Multiplicities.SPARSEARRAY);
		                boolean needsPosition = multiplicity.equals(Multiplicities.SPARSEARRAY);                
		                String elementTag = this.toSimpleQualifiedName(
		                    (String) attributeDef.objGetValue("qualifiedName")
		                );
		                List<Object> attributeValues = new ArrayList<Object>();
		                if(attributeValue instanceof Collection) {
		                	try {
		                		attributeValues.addAll((Collection<?>)attributeValue);
		                	}
		                	// In case attribute value is an object which is not accessible ignore it
		                	catch(Exception e) {}
		                }
		                else {
		                	attributeValues.add(attributeValue);
		                }
		                if(!attributeValues.isEmpty()) {
			                this.xmlWriter.startElement(
			                    "",
			                    "",
			                    elementTag,
			                    new XMLWriter.Attributes()
			                );
			                int valueIndex = 0;
			                for(Object value: attributeValues) {
			                    String stringValue = null;
			                    ModelElement_1_0 attributeType = this.model.getDereferencedType(
			                    	attributeDef.objGetValue("type")
			                    );
			                    String typeName = (String)attributeType.objGetValue("qualifiedName"); 
			                    if (PrimitiveTypes.DATETIME.equals(typeName)) {
			                        String v = DateFormat.getInstance().format((Date)value);
			                        String t =
			                            v.substring(0, 4) + 
			                            "-" + 
			                            v.substring(4, 6) + 
			                            "-" + 
			                            v.substring(6, 11) + 
			                            ":" + 
			                            v.substring(11, 13) + 
			                            ":" + 
			                            v.substring(13, 20);
			                        stringValue = t;
			                    }
			                    else if(PrimitiveTypes.DATE.equals(typeName)) {
			                    	XMLGregorianCalendar v = (XMLGregorianCalendar)value;		                    	
			                        String t =
			                            v.getYear() + 
			                            "-" + 
			                            (v.getMonth() < 10 ? "0" + v.getMonth() : v.getMonth()) + 
			                            "-" + 
			                            (v.getDay() < 10 ? "0" + v.getDay() : v.getDay());
			                        stringValue = t;
			                    }
			                    else if(
			                    	PrimitiveTypes.LONG.equals(typeName) || 
			                    	PrimitiveTypes.INTEGER.equals(typeName) || 
			                    	PrimitiveTypes.SHORT.equals(typeName)
			                    ) {
			                        value = new Long(((Number) value).longValue());
			                        stringValue = value.toString();
			                    }
			                    else if(PrimitiveTypes.BINARY.equals(typeName)) {
			                        if(value instanceof byte[]) {
			                            stringValue = Base64.encode((byte[])value);
			                        }
			                        else {
			                            stringValue = value.toString();
			                        }
			                    }
			                    else if(value instanceof RefObject_1_0){
			                    	RefObject_1_0 obj = (RefObject_1_0)value;
			                        stringValue = obj.refGetPath().toXri();
			                        ObjectExporter.this.referencedObjects.add(obj);
			                    }
			                    else {                       
			                        stringValue = value.toString();
			                    }		                    
			                    XMLWriter.Attributes atts = new XMLWriter.Attributes();
			                    if(needsPosition) {
			                        atts.addCDATA(
			                            "_position", 
			                            String.valueOf(valueIndex)
			                        );
			                    }
			                    if(isMultiValued) {
			                        this.xmlWriter.startElement(
			                            "",
			                            "",
			                            "_item",
			                            atts
			                        );
			                    }                    
			                    this.xmlWriter.characters(
			                        stringValue.toCharArray(),
			                        0,
			                        stringValue.length()
			                    );                        
			                    if(isMultiValued) {
			                        this.xmlWriter.endElement(
			                        	"", 
			                        	"", 
			                        	"_item",
			                        	false
			                        );
			                    }
			                    valueIndex++;
			                }
			                this.xmlWriter.endElement(
			                	"", 
			                	"", 
			                	elementTag,
			                	isMultiValued
			                );
			                // generate attribute tag as comment
			                if((tags != null) && (tags.get(attributeName) != null)) {
			                    this.xmlWriter.comment(tags.get(attributeName));
			                }
		                }
		            }
		        }
		        this.xmlWriter.endElement(
		        	"", 
		        	"", 
		        	"_object",
		        	true
		        );        
		        this.xmlWriter.startElement(
		            "",
		            "",
		            "_content",
		            new XMLWriter.Attributes()
		        );        
		        return true;
            }

			public boolean startObject(
				RefObject_1_0 object,
				String id,
				String qualifierName,
				String qualifiedName
			) throws ServiceException {
		        XMLWriter.Attributes atts = new XMLWriter.Attributes();
		        atts.addCDATA(
		        	qualifierName, 
		        	object.refGetPath().getBase()
		        );
		        this.xmlWriter.startElement(
		        	"", 
		        	"", 
		        	this.toXML(qualifiedName), 
		        	atts
		        );
		        return true;
            }

			public boolean startReference(
				String name
			) throws ServiceException {
		        this.xmlWriter.startElement(
		            "",
		            "",
		            this.toSimpleQualifiedName(name),
		            new XMLWriter.Attributes());

		        return true;
            }

			public void endTraversal(
				RefObject_1_0 object
			) throws ServiceException {
		        PersistenceManager pm = JDOHelper.getPersistenceManager(object);
		        for(int i = object.refGetPath().size() - 2; i > 0; i-=2) {
		        	RefObject_1_0 parent = (RefObject_1_0)pm.getObjectById(object.refGetPath().getPrefix(i));
		        	String qualifiedTypeName = parent.refClass().refMofId();
			        this.xmlWriter.endElement(
			        	"", 
			        	"", 
			        	object.refGetPath().get(i), 
			        	true
			        );
			        this.xmlWriter.endElement(
			        	"", 
			        	"", 
			        	"_content", 
			        	true
			        );
			        this.xmlWriter.endElement(
			        	"", 
			        	"", 
			        	this.toXML(qualifiedTypeName), 
			        	true
			        );
		        }		        				
		        this.xmlWriter.endDocument();
            }

			public void startTraversal(
				RefObject_1_0 object
			) throws ServiceException {
		        PersistenceManager pm = JDOHelper.getPersistenceManager(object);
		        this.xmlWriter.startDocument();
		        for(int i = 1; i < object.refGetPath().size(); i+=2) {
		        	RefObject_1_0 parent = (RefObject_1_0)pm.getObjectById(object.refGetPath().getPrefix(i));
		        	String qualifiedTypeName = parent.refClass().refMofId();
		        	String qualifierName = ObjectExporter.this.getQualifierName(parent);
		        	XMLWriter.Attributes atts = new XMLWriter.Attributes();
		        	atts.addCDATA(
		        		qualifierName, 
		        		parent.refGetPath().getBase()
		        	);
		        	atts.addCDATA(
		        		"_operation", 
		        		"null"
		        	);
			        if(qualifiedTypeName.equals("org:openmdx:base:Authority")) {
			            atts.addCDATA(
			                "xmlns:xsi",
			                this.schemaInstance
			            );
			            atts.addCDATA(
			                "xsi:noNamespaceSchemaLocation",
			                this.schemaLocation
			            );
			        }
			        this.xmlWriter.startElement(
			        	"", 
			        	"", 
			        	this.toXML(qualifiedTypeName), 
			        	atts
			        );
			        this.xmlWriter.startElement(
			        	"", 
			        	"", 
			        	"_object", 
			        	new XMLWriter.Attributes()
			        );
			        this.xmlWriter.endElement(
			        	"", 
			        	"", 
			        	"_object",
			        	true
			        );
			        this.xmlWriter.startElement(
			        	"", 
			        	"", 
			        	"_content", 
			        	new XMLWriter.Attributes()
			        );
			        this.xmlWriter.startElement(
			        	"", 
			        	"", 
			        	object.refGetPath().get(i), 
			        	new XMLWriter.Attributes()
			        );
		        }		        
            }
			
	        final Model_1_0 model;
	        final String schemaInstance;
	        final String schemaLocation;
			private final XMLWriter xmlWriter;
        		
        }
        
        //---------------------------------------------------------------------
        final Set<String> NOT_EXPORTED_ATTRIBUTES = new HashSet<String>(
            Arrays.asList(
            	SystemAttributes.OBJECT_INSTANCE_OF, 
            	SystemAttributes.CONTEXT_CAPABLE_CONTEXT
            )
        );
        
        final Set<RefObject_1_0> referencedObjects;
        final Set<RefObject_1_0> exportedObjects;
        final ExportParams params;
        final PrintStream out;
    }
    
}

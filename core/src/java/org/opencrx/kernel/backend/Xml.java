/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Xml.java,v 1.6 2007/12/25 17:19:16 wfro Exp $
 * Description: Xml
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/25 17:19:16 $
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
package org.opencrx.kernel.backend;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.query.Filter;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.exporter.TraversalHandler;
import org.openmdx.compatibility.base.dataprovider.exporter.XMLExportHandler;
import org.openmdx.compatibility.base.dataprovider.exporter.XmlContentHandler;
import org.openmdx.compatibility.base.dataprovider.exporter.XmlExporter;
import org.openmdx.compatibility.base.dataprovider.importer.xml.XmlImporter;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.model1.accessor.basic.cci.ModelElement_1_0;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.openmdx.model1.code.Multiplicities;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Xml {

    //-------------------------------------------------------------------------
    class Importer extends XmlImporter {
      
        public Importer(
            RequestCollection target,
            RequestCollection reader,
            String targetSegment,
            List errors,
            List report
        ) throws ServiceException {
            super(target, reader, false, true);
            this.targetSegment = targetSegment;
            this.errors = errors;
            this.report = report;
        }

        private void addError(
            String message
        ) {
            if(this.errors.size() < 5) {
                this.errors.add(message);
            }
        }
            
        private void addError(
            SAXParseException e
        ) {
            this.addError("[" + this.getLocationString(e) + "]: " + e.getMessage());
        }
            
        public void beginObject(
            DataproviderObject object, 
            String operation
        ) throws ServiceException {
            if(
                (object.path().size() > 5) &&
                ((this.mainObject == null) || (object.path().size() < this.mainObject.path().size()))
            ) {
                this.mainObject = object;
            }
        }
    
        public void endObject(
            DataproviderObject object, 
            String operation
        ) throws ServiceException {
            try {
                if(!"null".equals(operation)) {
                    // Remap all paths to target segment:
                    // <ul>
                    //   <li>data can only be imported to segment of invoking user
                    //   <li>XMLs for a specific segment can easily be imported into other segments without modification
                    // </ul>
                    if(
                        (object.path().size() > 4) &&
                        !this.targetSegment.equals(object.path().get(4))
                    ) {
                        object.path().setTo(
                          object.path().getPrefix(4).getChild(this.targetSegment).getDescendant(object.path().getSuffix(5))
                        );
                    }
                    // Remap segment of all path values except for Root segment (Root segment
                    // is used for shared objects such as Users, Uoms)
                    for(Iterator i = object.attributeNames().iterator(); i.hasNext(); ) {
                        List values = object.values((String)i.next());
                        for(Iterator j = values.iterator(); j.hasNext(); ) {
                            Object value = j.next();
                            if(value instanceof Path) {
                                Path p = (Path)value;
                                // Fix realm for owning groups and users
                                if(p.getPrefix(REALM_PATTERN.size()).isLike(REALM_PATTERN)) {
                                    p.setTo(
                                        p.getPrefix(6).getChild(this.targetSegment).getDescendant(p.getSuffix(7))
                                    );                                   
                                }
                                else if(!this.targetSegment.equals(((Path)value).get(4))) {
                                    if(p.size() > 4) {
                                        p.setTo(
                                            p.getPrefix(4).getChild(this.targetSegment).getDescendant(p.getSuffix(5))
                                        );
                                    }
                                }
                            }
                        }
                    }
	                try {
	                    // verify object to be replaced
	                    DataproviderObject test = new DataproviderObject(
	                        Xml.this.backend.retrieveObjectForModification(
                                object.path()
                            )
	                    );
	                    test.addClones(
	                        object,
	                        true
	                    );
		                Xml.this.backend.getModel().verifyObject(
		                    test,
		                    test.values(SystemAttributes.OBJECT_CLASS).get(0),
		                    Multiplicities.SINGLE_VALUE,
		                    "create".equals(operation)
		                );
		                
		                // replace
		                Xml.this.removeForeignAndDerivedAttributes(object);                
	                    Xml.this.backend.retrieveObjectForModification(
	                        object.path()
                        ).addClones(
	                        object,
	                        true
	                    );
	                } catch(Exception e) {
		                Xml.this.backend.getModel().verifyObject(
		                    object,
		                    object.values(SystemAttributes.OBJECT_CLASS).get(0),
		                    Multiplicities.SINGLE_VALUE,
		                    "create".equals(operation)
		                );
		                Xml.this.removeForeignAndDerivedAttributes(object);                
	                    Xml.this.target.addCreateRequest(object);
	                }
	                this.report.add(operation);
                }
            }
            catch(ServiceException e) {
                e.log(); // log for administrators
                this.addError(e.getMessage() + " at object " + object.path());
            }
        }
        
        public void error(
          SAXParseException e
        ) throws SAXException {
            this.addError(e);
            super.error(e);
        }

        public void fatalError(
            SAXParseException e
        ) throws SAXException {
            this.addError(e);
            super.fatalError(e);
        }

        public void warning(
            SAXParseException e
        ) {
            this.report.add(e.getMessage());
            super.warning(e);
        }
        
        public void process(
            InputStream[] is
        ) {
            try {
                super.process(is);
            }
            catch(ServiceException e) {
                BasicException be = e.getStackedException(0);
                this.errors.add(be.getDescription() + " (" + Arrays.asList(be.getParameters()) + ")");
            }
        }
        
        public DataproviderObject getMainObject(
        ) {
          return this.mainObject;
        }
        
        private DataproviderObject mainObject = null;
        private final String targetSegment;
        private final List errors;
        private final List report;
    }

    //-------------------------------------------------------------------------
    class Exporter extends XmlExporter {
        
        //---------------------------------------------------------------------
        public Exporter(
            ServiceHeader header,
            RequestCollection reader, 
            PrintStream exportStream, 
            Model_1_0 model,
            Map exportedCodes
        ) {
            super(
                header,
                reader, 
                exportStream,
                model,
                "UTF-8"
            );
            this.referencedObjects = new HashSet();
            this.exportedObjects = new HashSet();
            this.exportedCodes = exportedCodes;
        }
        
        public Set getReferencedObjects(
        ) {
            return this.referencedObjects;
        }
        
        public Set getExportedObjects(
        ) {
            return this.exportedObjects;
        }
        
        void updateReferencedObjects(
            DataproviderObject_1_0 object
        ) {
            Xml.this.backend.getCloneable().collectReferencedObjects(
                object,
                Xml.this.referenceFilter,
                this.referencedObjects
            );
        }
       
        protected TraversalHandler setupTraversalHandler(
        ) throws ServiceException {
            this.setupContentHandler(
                this.exportStream
            );
            TaggingXMLExportHandler exportHandler = new TaggingXMLExportHandler(
                this.model,
                "http://www.w3.org/2001/XMLSchema-instance",
                this.schemaString,
                this.exportedCodes
            );
            exportHandler.setContentHandler(
                this.setupContentHandler(this.exportStream)           
            );
            return exportHandler;
        }
        
        protected XmlContentHandler setupContentHandler(
            PrintStream target
        ) throws ServiceException {
            return new NonClosingXmlContentHandler(
                target
            );
        }

        //---------------------------------------------------------------------
        public void export(
            List startPoints,
            List referenceFilters,
            Map attributeFilters,
            String schemaString,
            int maxObjects
        ) throws ServiceException {
            super.export(
                startPoints,
                referenceFilters,
                attributeFilters,
                schemaString
            );            
        }

        //---------------------------------------------------------------------
        class TaggingXMLExportHandler
            extends XMLExportHandler {
         
            public TaggingXMLExportHandler(
                Model_1_0 model,
                String schemaInstance, 
                String schemaLocation,
                Map exportedCodes
            ) {
                super(model, schemaInstance, schemaLocation);
                this.exportedCodes = exportedCodes;
            }

            /**
             * Test all attributes of object to be a coded value, i.e. configured
             * as member of a code value container. If yes, add the attributes to
             * a map of tags which contains a hint of the corresponding code value
             * container. The hints are streamed as XML comments. 
             */
            public Map getAttributeTags(
                DataproviderObject_1_0 object
            ) throws ServiceException {
                Map attributeTags = new HashMap();
                String objectClassName = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
                if(objectClassName != null) {
                    ModelElement_1_0 objectClass = this.model.getElement(objectClassName);
                    if(objectClass != null) {
                        for(Iterator i = object.attributeNames().iterator(); i.hasNext(); ) {
                            String attributeName = (String)i.next();
                            // try to find a code value container for attribute
                            for(Iterator j = objectClass.values("allSupertype").iterator(); j.hasNext(); ) {
                                ModelElement_1_0 supertype = this.model.getElement(j.next());
                                String qualifiedClassName = (String)supertype.values("qualifiedName").get(0);
                                String qualifiedAttributeName = qualifiedClassName + ":" + attributeName;
                                DataproviderObject_1_0 codeValueContainer = Xml.this.backend.getCodes().getCodeValueContainerByName(qualifiedAttributeName);
                                // if found add hint with reference to corresponding code value container to tags.
                                if(codeValueContainer != null) {
                                    String codeValueContainerName = codeValueContainer.path().getBase();
                                    attributeTags.put(
                                        attributeName,
                                        "@see CodeValueContainer name=\"" + codeValueContainerName + "\""
                                    );
                                    // remember exported codes
                                    Set codes = null;
                                    if((codes = (Set)this.exportedCodes.get(codeValueContainerName)) == null) {
                                        this.exportedCodes.put(
                                            codeValueContainerName,
                                            codes = new HashSet()
                                        );
                                    }
                                    // add all codes as Integer
                                    try {
                                        for(Iterator k = object.values(attributeName).iterator(); k.hasNext(); ) {
                                            codes.add(new Integer(((Number)k.next()).intValue()));
                                        }
                                    }
                                    catch(Exception e) {
                                        AppLog.error("Exception occurred while retrieving codes for attribute", attributeName);
                                        new ServiceException(e).log();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                return attributeTags;
            }
       
            public boolean featureComplete(
                DataproviderObject_1_0 object
            ) throws ServiceException {
                Exporter.this.updateReferencedObjects(object);
                // skip dummy and egment-level objects
                if((object.getValues(SystemAttributes.OBJECT_CLASS) != null) && (object.path().size() > 5)) {
                    Exporter.this.exportedObjects.add(object.path());
                }
                return super.featureComplete(
                    object
                );
            }
            
            private final Map exportedCodes;
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
        
        int currentEntryId = 0;
        
        final Set referencedObjects;
        final Set exportedObjects;
        // map with CodeValueContainerName as key and List as value 
        // containing the exported codes
        final Map exportedCodes; 
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
    
    //-------------------------------------------------------------------------
    public Xml(
        Backend backend,
        RequestCollection target,
        RequestCollection reader
    ) {
        this.backend = backend;
        this.target = target;
        this.reader = reader;
    }

    //-------------------------------------------------------------------------
    public Xml(
        Backend backend,
        RequestCollection reader
    ) {
        this.backend = backend;
        this.target = null;
        this.reader = reader;
    }

    //---------------------------------------------------------------------------
    public void removeForeignAndDerivedAttributes(
      DataproviderObject object
    ) throws ServiceException {   

      // remove derived attributes but not SystemAttributes
      ModelElement_1_0 typeDef = this.backend.getModel().getElement(object.values(SystemAttributes.OBJECT_CLASS).get(0));
      if(this.backend.getModel().isClassType(typeDef)) {
        Map attributeDefs = this.backend.getModel().getAttributeDefs(
          typeDef, false, true
        );
        for(
          Iterator i = object.attributeNames().iterator();
          i.hasNext();
        ) {
          boolean isDerived = false;
          boolean isChangeable = true;
          boolean isForeign = true;
          
          String featureName = (String)i.next();
          
          // ignore namespaces
          if(featureName.indexOf(':') < 0) {
            ModelElement_1_0 featureDef = (ModelElement_1_0) attributeDefs.get(featureName);
            
            if (featureDef != null) {
                isDerived = 
                  (featureDef.values("isDerived").size() > 0) && 
                  ((Boolean)featureDef.values("isDerived").get(0)).booleanValue();
                isChangeable = 
                  (featureDef.values("isChangeable").size() > 0) && 
                  ((Boolean)featureDef.values("isChangeable").get(0)).booleanValue();          
                isForeign = false;
            }
            boolean isSystemAttribute = 
                 SystemAttributes.OBJECT_CLASS.equals(featureName)
              || SystemAttributes.CREATED_AT.equals(featureName)
              || SystemAttributes.MODIFIED_AT.equals(featureName)
              || SystemAttributes.CREATED_BY.equals(featureName)
              || SystemAttributes.MODIFIED_BY.equals(featureName);

            if((isDerived || !isChangeable || isForeign) && !isSystemAttribute) {
              i.remove();
            }
          }
        }
      }
    }
    
    //-------------------------------------------------------------------------
    public void exportItem(
        ZipOutputStream zip,
        PrintStream ps,
        List startPoints,
        Set exportedObjects,
        Map exportedCodes,
        int level
    ) throws IOException, ServiceException {
        if(level >= 0) {
            List referencedObjects = new ArrayList();
            for(
                Iterator i = startPoints.iterator();
                i.hasNext();
            ) {
                Path startPoint = (Path)i.next();
                if(!exportedObjects.contains(startPoint)) {
                    zip.putNextEntry(
                        new ZipEntry("data-" + (this.currentEntryId++) + ".xml")
                    );
                    
                    // derive schema name from identity
                    String qualifiedModelName = startPoint.get(0);
                    String namespace = qualifiedModelName.substring(0, qualifiedModelName.lastIndexOf(":"));
                    String modelName = qualifiedModelName.substring(qualifiedModelName.lastIndexOf(":") + 1);
                    Exporter exporter = new Exporter(
                        this.backend.getServiceHeader(),
                        this.reader,
                        ps,
                        this.backend.getModel(),
                        exportedCodes                       
                    );
                    exporter.export(
                        Arrays.asList(new Path[]{startPoint}),
                        this.referenceFilter,
                        this.attributeFilter,
                        "xri:+resource/" + namespace.replace(':', '/') + "/" + modelName + "/xmi/" + modelName + ".xsd"
                    );
                    
                    // update referenced and exported objects
                    referencedObjects.addAll(
                        exporter.getReferencedObjects()
                    );
                    exportedObjects.addAll(
                        exporter.getExportedObjects()
                    );
                }
            }
            this.exportItem(
                zip,
                ps,
                referencedObjects,
                exportedObjects,
                exportedCodes,
                level-1
            );
        }
    }

    //-------------------------------------------------------------------------
    public byte[] exportItem(
        Path startFromIdentity,
        String referenceFilter,
        String attributeFilter
    ) throws ServiceException {
        
        try {            
            // reference filter
            if(referenceFilter == null) {
                referenceFilter = ":*";
            }
            this.referenceFilter = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(referenceFilter, " ;,", false);
            while(tokenizer.hasMoreTokens()) {
                this.referenceFilter.add(
                    new Path(startFromIdentity.toXri() + "/" + tokenizer.nextToken())
                );
            }
            
            // attribute filter
            this.attributeFilter = new HashMap();
            if(attributeFilter != null) {
                tokenizer = new StringTokenizer(attributeFilter, " ;,", false);
                while(tokenizer.hasMoreTokens()) {
                    String filterDefinition = tokenizer.nextToken();
                    int pos = 0;
                    if((pos = filterDefinition.indexOf("=")) >= 0) {
                        Path reference = new Path(startFromIdentity.toXri() + "/" + filterDefinition.substring(0, pos));                        
                        Filter filter = null;
                        if(!"*".equals(filterDefinition.substring(pos+1))) {
                            try {
                                XMLDecoder decoder = new XMLDecoder(
                                    new ByteArrayInputStream(Base64.decode(filterDefinition.substring(pos+1)))
                                );
                                filter = (Filter)decoder.readObject();
                                decoder.close();
                            }
                            catch(Exception e) {}
                        }
                        if(filter != null) {
                            this.attributeFilter.put(
                                reference,
                                filter
                            );
                        }
                    }
                }
            }
            
            Set exportedObjects = new TreeSet();
            
            // 2-pass export
            // 1) determine transitive closure of objects to export
            // 2) export

            // pass 1
            ZipOutputStream zip = new ZipOutputStream(new NullOutputStream());
            PrintStream ps = new PrintStream(zip);
            Map exportedCodes = new HashMap();
            // export at least code table 'locale'
            exportedCodes.put(
                LOCALE_CODE_TABLE_ID,
                null
            );
            this.currentEntryId = 0;
            this.exportItem(
                zip, 
                ps,
                Arrays.asList(new Path[]{startFromIdentity}
                ),
                exportedObjects,
                exportedCodes,
                MAX_LEVELS
            );

            // Remove all objects which are composite in set of exported objects
            // NOTE: exported objects are ordered by identity
            Path last = null;
            for(
                Iterator i = exportedObjects.iterator();
                i.hasNext();
            ) {
                Path current = (Path)i.next();
                if((last != null) && (current.startsWith(last))) {
                    i.remove();
                }
                else {
                    last = current;
                }            
            }
            
            // Assert that the object to be exported is not contained
            // in the file export list. It will be exportered separately
            // with traverseContent = false
            List exportedObjectsAsList = new ArrayList(exportedObjects);
            exportedObjectsAsList.remove(startFromIdentity);
            
            // pass 2
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            zip = new ZipOutputStream(bs);
            ps = new PrintStream(zip);
            exportedCodes = new HashMap();
            exportedCodes.put(
                LOCALE_CODE_TABLE_ID,
                null
            );
            // export startFrom with traverseContent = false
            this.currentEntryId = 0;
            this.exportItem(
                zip, 
                ps,
                Arrays.asList(new Path[]{startFromIdentity}),
                new TreeSet(),
                exportedCodes,
                0
            );            
            // export contained and referenced objects with traverseContent = true
            this.exportItem(
                zip, 
                ps,
                exportedObjectsAsList,
                new TreeSet(),
                exportedCodes,
                0
            );

            // referenced codes in separate entry
            zip.putNextEntry(
                new ZipEntry("codes.xml")
            );
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(ps, "UTF-8"),
                true
            );                    
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<org.openmdx.base.Authority xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"org:opencrx:kernel:code1\" xsi:noNamespaceSchemaLocation=\"xri:+resource/org/opencrx/kernel/code1/xmi/code1.xsd\">");
            writer.println("  <_object/>");
            writer.println("  <_content>");
            writer.println("    <provider>");
            writer.println("      <org.openmdx.base.Provider qualifiedName=\"CRX\" _operation=\"null\">");
            writer.println("        <_object/>");
            writer.println("        <_content>");
            writer.println("          <segment>");
            writer.println("            <org.opencrx.kernel.code1.Segment qualifiedName=\"Root\" _operation=\"null\">");
            writer.println("              <_object/>");
            writer.println("              <_content>");
            writer.println("                <valueContainer>");
            for(Iterator j = exportedCodes.entrySet().iterator(); j.hasNext(); ) {
                Entry e = (Entry)j.next();
                String codeValueContainerId = (String)e.getKey();
                Set codes = (Set)e.getValue();
                List codeEntries = this.backend.getCodes().getCodeEntriesById(codeValueContainerId);
                if(((codes == null) || (codes.size() > 0)) && (codeEntries != null)) {
                    writer.println("                  <org.opencrx.kernel.code1.CodeValueContainer name=\"" + codeValueContainerId + "\" _operation=\"null\">");
                    writer.println("                    <_object/>");
                    writer.println("                    <_content>");
                    writer.println("                      <entry>");
                    for(Iterator k = codeEntries.iterator(); k.hasNext(); ) {
                        DataproviderObject_1_0 codeEntry = (DataproviderObject_1_0)k.next();
                        Number code = new Integer(codeEntry.path().getBase());
                        if((codes == null) || (codes.contains(code))) {
                            writer.println("                        <org.opencrx.kernel.code1.CodeValueEntry code=\"" + code + "\" _operation=\"null\">");
                            writer.println("                          <_object>");
                            
                            // shortText
                            writer.println("                            <shortText>");
                            int ll = 0;
                            for(
                                Iterator l = codeEntry.values("shortText").iterator(); 
                                l.hasNext();
                                ll++
                             ) {
                                String text = (String)l.next();
                                if(text == null) {
                                    writer.println("                              <_item />");
                                }
                                else { 
                                    writer.println("                              <_item>" + text + "</_item>");
                                }    
                            }
                            writer.println("                            </shortText>");

                            // longText
                            writer.println("                            <longText>");
                            ll = 0;
                            for(
                                Iterator l = codeEntry.values("longText").iterator(); 
                                l.hasNext();
                                ll++
                             ) {
                                String text = (String)l.next();
                                if(text == null) {
                                    writer.println("                              <_item />");
                                }
                                else {
                                    writer.println("                              <_item>" + text + "</_item>");                                    
                                }
                             }
                             writer.println("                            </longText>");

                             writer.println("                          </_object>");      
                             writer.println("                          <_content/>");      
                             writer.println("                        </org.opencrx.kernel.code1.CodeValueEntry>");
                        }
                    }
                    writer.println("                      </entry>");
                    writer.println("                    </_content>");
                    writer.println("                  </org.opencrx.kernel.code1.CodeValueContainer>");
                }
            }
            writer.println("                </valueContainer>");
            writer.println("              </_content>");
            writer.println("            </org.opencrx.kernel.code1.Segment>");
            writer.println("          </segment>");
            writer.println("        </_content>");
            writer.println("      </org.openmdx.base.Provider>");
            writer.println("    </provider>");
            writer.println("  </_content>");
            writer.println("</org.openmdx.base.Authority>");
            zip.closeEntry();
            
            ps.close();
            return bs.toByteArray();
        }
        catch(IOException e) {
            throw new ServiceException(e);
        }
    }

    //-------------------------------------------------------------------------
    public BasicObject importItem(
        byte[] item,
        short locale,
        String targetSegment,
        List errors,
        List report
    ) throws ServiceException {
        Importer importer = new Importer(
            this.target,
            this.reader,
            targetSegment,
            errors,
            report
        );
        importer.process(
            new InputStream[]{new ByteArrayInputStream(item)}
        );
        DataproviderObject importedObject = importer.getMainObject();
        return importedObject == null
            ? null
            : (BasicObject)this.backend.getDelegatingPkg().refObject(importedObject.path().toXri());
    }
  
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final String MIME_TYPE = "text/xml";
    public static final int MIME_TYPE_CODE = 2;
    public static final String LOCALE_CODE_TABLE_ID = "locale";
    protected static final int MAX_LEVELS = 4;
    protected static final Path REALM_PATTERN = new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/Root/realm/:*");
    protected int currentEntryId = 0;
    protected final Backend backend;
    protected final RequestCollection target;
    protected final RequestCollection reader;
    protected List referenceFilter = null;
    protected Map attributeFilter = null;
    
}

//--- End of File -----------------------------------------------------------

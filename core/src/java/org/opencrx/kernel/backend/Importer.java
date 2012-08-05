/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Importer.java,v 1.1 2008/05/09 18:14:36 wfro Exp $
 * Description: Importer
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/05/09 18:14:36 $
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.importer.xml.XmlImporter;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.model1.accessor.basic.cci.ModelElement_1_0;
import org.openmdx.model1.code.Multiplicities;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Importer {

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
    public Importer(
        Backend backend,
        RequestCollection target,
        RequestCollection reader
    ) {
        this.backend = backend;
        this.target = target;
        this.reader = reader;
    }

    //-------------------------------------------------------------------------
    public Importer(
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
        Map<String,ModelElement_1_0> attributeDefs = this.backend.getModel().getAttributeDefs(
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
            ModelElement_1_0 featureDef = attributeDefs.get(featureName);
            
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
    public BasicObject importItem(
        byte[] item,
        short locale,
        String targetSegment,
        List<String> errors,
        List<String> report
    ) throws ServiceException {
        ImportHandler importer = new ImportHandler(
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
    protected static final Path REALM_PATTERN = new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/Root/realm/:*");
    protected final Backend backend;
    protected final RequestCollection target;
    protected final RequestCollection reader;

    //-------------------------------------------------------------------------
    class ImportHandler extends XmlImporter {
      
        public ImportHandler(
            RequestCollection target,
            RequestCollection reader,
            String targetSegment,
            List<String> errors,
            List<String> report
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
                            Importer.this.backend.retrieveObjectForModification(
                                object.path()
                            )
                        );
                        test.addClones(
                            object,
                            true
                        );
                        Importer.this.backend.getModel().verifyObject(
                            test,
                            test.values(SystemAttributes.OBJECT_CLASS).get(0),
                            Multiplicities.SINGLE_VALUE,
                            "create".equals(operation)
                        );
                        
                        // replace
                        Importer.this.removeForeignAndDerivedAttributes(object);                
                        Importer.this.backend.retrieveObjectForModification(
                            object.path()
                        ).addClones(
                            object,
                            true
                        );
                    } catch(Exception e) {
                        Importer.this.backend.getModel().verifyObject(
                            object,
                            object.values(SystemAttributes.OBJECT_CLASS).get(0),
                            Multiplicities.SINGLE_VALUE,
                            "create".equals(operation)
                        );
                        Importer.this.removeForeignAndDerivedAttributes(object);                
                        Importer.this.target.addCreateRequest(object);
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
        private final List<String> errors;
        private final List<String> report;
    }
    
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ImporterImpl.java,v 1.8 2009/01/09 18:11:25 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/01/09 18:11:25 $
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
package org.opencrx.kernel.plugin.application.base;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.backend.Importer;
import org.opencrx.kernel.backend.VCard;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;

public class ImporterImpl {

    //-----------------------------------------------------------------------
    public ImporterImpl(
        org.opencrx.kernel.base.jmi1.Importer current,
        org.opencrx.kernel.base.cci2.Importer next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_0)this.current.refOutermostPackage()).refUserContext();
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ImportResult importItem(
        org.opencrx.kernel.base.jmi1.ImportParams params
    ) {
        try {
            short locale = params.getLocale();
            byte[] item = params.getItem();
            String itemMimeType = params.getItemMimeType();
            AppLog.trace("import MIME_TYPE", itemMimeType);
            List<String> report = new ArrayList<String>();
            List<String> errors = new ArrayList<String>();
            BasicObject importedObject = null; 
            if(VCard.MIME_TYPE.equals(itemMimeType)) {
                importedObject = new VCard(
                    this.getBackend()
                ).importItem(
                    item,
                    this.current.refGetPath(),
                    locale,
                    errors,
                    report
                );
            }
            else if(ICalendar.MIME_TYPE.equals(itemMimeType)) {            
                importedObject = new ICalendar(
                    this.getBackend()
                ).importItem(
                    item,
                    this.current.refGetPath(),
                    locale,
                    errors,
                    report
                );
            }
            else if(Importer.MIME_TYPE.equals(itemMimeType)) {
                importedObject = new Importer(
                    this.getBackend(),
                    // self-delegation asserts that processed objects are verified and completed
                    this.getBackend().getLocalRequests(),
                    // read objects from delegation
                    this.getBackend().getDelegatingRequests()
                ).importItem(
                    item,
                    locale,
                    // target segment
                    this.current.refGetPath().get(4),
                    errors,
                    report
                );
            }            
            else {
                return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createImportResult(
                    null,
                    Base.IMPORT_EXPORT_FORMAT_NOT_SUPPORTED,
                    null                        
                );
            }
            if(importedObject != null) {
                return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createImportResult(
                    importedObject,
                    Base.IMPORT_EXPORT_OK,
                    this.getBackend().analyseReport(report)                        
                );                            
            }
            else {
                return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createImportResult(
                    null,
                    Base.IMPORT_EXPORT_ITEM_NOT_VALID,
                    this.getBackend().analyseReport(errors)                        
                );                            
            }
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.base.jmi1.Importer current;
    protected final org.opencrx.kernel.base.cci2.Importer next;    
    
}

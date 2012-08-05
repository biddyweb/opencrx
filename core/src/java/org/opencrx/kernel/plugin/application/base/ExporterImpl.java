/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ExporterImpl.java,v 1.6 2009/01/09 18:11:25 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.6 $
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

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.Exporter;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;

public class ExporterImpl {

    //-----------------------------------------------------------------------
    public ExporterImpl(
        org.opencrx.kernel.base.jmi1.Exporter current,
        org.opencrx.kernel.base.cci2.Exporter next
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
    public org.opencrx.kernel.base.jmi1.ExportItemResult exportItem(
        org.opencrx.kernel.base.jmi1.ExportItemParams params
    ) {
        try {
            Object[] exportedItem = new Exporter(
                this.getBackend(),
                // delegate to myself. This completes the derived attributes
                // which is required for the export
                this.getBackend().getLocalRequests()
            ).exportItem(
                this.current.refGetPath(),
                params.getExportProfile(), 
                null,
                null
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createExportItemResult(
                (byte[])exportedItem[2],
                (String)exportedItem[1],
                (String)exportedItem[0],
                Base.IMPORT_EXPORT_OK,
                null
            );
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ExportItemResult exportItemAdvanced(
        org.opencrx.kernel.base.jmi1.ExportItemAdvancedParams params
    ) {
        try {
            Object[] exportedItem = new Exporter(
                this.getBackend(),
                // delegate to myself. This completes the derived attributes
                // which is required for the export
                this.getBackend().getLocalRequests()
            ).exportItem(
                this.current.refGetPath(),
                null, 
                params.getReferenceFilter(),
                params.getItemMimeType()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createExportItemResult(
                (byte[])exportedItem[2],
                (String)exportedItem[1],
                (String)exportedItem[0],
                Base.IMPORT_EXPORT_OK,
                null
            );
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.base.jmi1.Exporter current;
    protected final org.opencrx.kernel.base.cci2.Exporter next;    
    
}

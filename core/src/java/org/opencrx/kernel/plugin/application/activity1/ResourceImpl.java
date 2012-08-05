/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ResourceImpl.java,v 1.2 2007/12/25 17:15:54 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/25 17:15:54 $
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
package org.opencrx.kernel.plugin.application.activity1;

import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityWorkRecord;
import org.opencrx.kernel.backend.Backend;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.accessor.jmi.spi.RefException_1;
import org.openmdx.base.exception.ServiceException;

public class ResourceImpl {

    //-----------------------------------------------------------------------
    public ResourceImpl(
        org.opencrx.kernel.activity1.jmi1.Resource current,
        org.opencrx.kernel.activity1.cci2.Resource next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_3)this.current.refOutermostPackage()).refUserContext();
    }
            
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.AddWorkRecordResult addWorkRecordByDuration(
        org.opencrx.kernel.activity1.jmi1.ResourceAddWorkRecordByDurationParams params
    ) throws javax.jmi.reflect.RefException {
        try {
            ActivityWorkRecord workRecord = this.getBackend().getActivities().resourceAddWorkRecordByDuration(
                this.current.refGetPath(), 
                params.getActivity() == null ? null : params.getActivity().refGetPath(),
                params.getName(),
                params.getDescription(),
                params.getStartAt(),
                params.getEndAt(),
                params.getDurationHours(),
                params.getDurationMinutes(),
                params.getRateType(),
                params.getDepotSelector()
            );
            return ((Activity1Package)this.current.refOutermostPackage().refPackage(Activity1Package.class.getName())).createAddWorkRecordResult(
                workRecord
            );            
        }
        catch(ServiceException e) {
            throw new RefException_1(e);
        }                    
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.activity1.jmi1.Resource current;
    protected final org.opencrx.kernel.activity1.cci2.Resource next;    
    
}

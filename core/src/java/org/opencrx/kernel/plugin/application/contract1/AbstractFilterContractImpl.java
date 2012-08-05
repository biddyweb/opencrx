/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AbstractFilterContractImpl.java,v 1.2 2008/06/18 10:59:40 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/06/18 10:59:40 $
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
package org.opencrx.kernel.plugin.application.contract1;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;

public class AbstractFilterContractImpl {

    //-----------------------------------------------------------------------
    public AbstractFilterContractImpl(
        org.opencrx.kernel.contract1.jmi1.AbstractFilterContract current,
        org.opencrx.kernel.contract1.cci2.AbstractFilterContract next
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
    public org.opencrx.kernel.base.jmi1.CountFilteredObjectsResult countFilteredContract(
    ) {
        try {
            int count = this.getBackend().getContracts().countFilteredContract(
                this.current.refGetPath()
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createCountFilteredObjectsResult(
                count
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.contract1.jmi1.AbstractFilterContract current;
    protected final org.opencrx.kernel.contract1.cci2.AbstractFilterContract next;
    
}

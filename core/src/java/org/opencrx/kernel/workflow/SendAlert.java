/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SendAlert.java,v 1.20 2008/04/01 12:33:02 wfro Exp $
 * Description: PrintConsole workflow
 * Revision:    $Revision: 1.20 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/04/01 12:33:02 $
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
package org.opencrx.kernel.workflow;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.backend.SynchWorkflow_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.naming.Path;

public class SendAlert 
    implements SynchWorkflow_1_0 {

    /* (non-Javadoc)
     * @see org.opencrx.kernel.layer.application.Workflow_1_0#execute(org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader, org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0, org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0, org.opencrx.kernel.layer.application.OpenCrxKernel_1, org.openmdx.compatibility.base.dataprovider.cci.RequestCollection)
     */
    public void execute(
        DataproviderObject_1_0 userHome,
        Path targetObjectIdentity,
        Map params,
        Path wfProcessInstanceIdentity,
        Backend backend
    ) throws ServiceException {
        String name = null;
        // Get attribute name of target object if available and set as name of alert
        if(targetObjectIdentity != null) {
            try {
                DataproviderObject_1_0 targetObject = backend.getDelegatingRequests().addGetRequest(
                    targetObjectIdentity                    
                );
                if(targetObject.getValues("name") != null) {
                    name = (String)targetObject.values("name").get(0);
                }
            } catch(Exception e) {}
        }
        String description = "";
        for(
            Iterator i = params.entrySet().iterator(); 
            i.hasNext();
        ) {
            Entry e = (Entry)i.next();
            description += e.getKey() + "=" + e.getValue() + "\n";
        }
        try {
            backend.getBase().sendAlert(
                targetObjectIdentity, 
                userHome.path().getBase(), 
                name == null
                    ? "SendAlert notification"
                    : name, // toUsers
                description,
                5, // importance
                60, // resend delay is 1 minute
                targetObjectIdentity
            );
        }
        catch(ServiceException e) {
            System.out.println("can not execute workflow SendAlert. Reason " + e.getMessage());
        }
    }

}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SendAlert.java,v 1.29 2009/04/23 22:30:57 wfro Exp $
 * Description: PrintConsole workflow
 * Revision:    $Revision: 1.29 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/23 22:30:57 $
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

import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.SynchWorkflow_2_0;
import org.opencrx.kernel.base.jmi1.WorkflowTarget;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;

public class SendAlert 
    implements SynchWorkflow_2_0 {

    public void execute(
        WorkflowTarget wfTarget,
        ContextCapable targetObject,
        Map<String,Object> params,
        WfProcessInstance wfProcessInstance
    ) throws ServiceException {
        String name = null;
        // Get attribute name of target object if available and set as name of alert
        if(targetObject != null) {
            try {
                if(targetObject.refGetValue("name") != null) {
                    name = (String)targetObject.refGetValue("name");
                }
            } 
            catch(Exception e) {}
        }
        String description = "";
        for(
            Iterator<Entry<String,Object>> i = params.entrySet().iterator(); 
            i.hasNext();
        ) {
            Entry<String,Object> e = i.next();
            description += e.getKey() + "=" + e.getValue() + "\n";
        }
        try {
        	if(wfTarget instanceof UserHome) {
	            Base.getInstance().sendAlert(
	                targetObject, 
	                wfTarget.refGetPath().getBase(), // toUsers
	                name == null ? 
	                	"SendAlert notification" : 
	                	name,
	                description,
	                (short)5, // importance
	                60, // resend delay is 1 minute
	                targetObject
	            );
        	}
        }
        catch(ServiceException e) {
            System.out.println("can not execute workflow SendAlert. Reason " + e.getMessage());
        }
    }

}

//--- End of File -----------------------------------------------------------

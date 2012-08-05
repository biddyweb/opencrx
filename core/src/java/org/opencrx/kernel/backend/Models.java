/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Models.java,v 1.18 2009/04/21 11:22:00 wfro Exp $
 * Description: Models
 * Revision:    $Revision: 1.18 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 11:22:00 $
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

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.model1.cci2.ParameterQuery;
import org.openmdx.base.exception.ServiceException;

public class Models extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Models());
	}
	
    //-------------------------------------------------------------------------
	public static Models getInstance(		
	) throws ServiceException {
		return getInstance(Models.class);
	}

	//-------------------------------------------------------------------------
	protected Models(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public String getOperationSignature(
        org.opencrx.kernel.model1.jmi1.Operation operation
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(operation);
    	org.opencrx.kernel.model1.jmi1.Segment modelSegment =
    		(org.opencrx.kernel.model1.jmi1.Segment)pm.getObjectById(
    			operation.refGetPath().getPrefix(5)
    		);
    	ParameterQuery parameterQuery = (ParameterQuery)pm.newQuery(org.opencrx.kernel.model1.jmi1.Parameter.class);
    	parameterQuery.thereExistsContainer().equalTo(
    		operation
    	);
    	parameterQuery.orderByElementOrder().ascending();
    	List<org.opencrx.kernel.model1.jmi1.Parameter> parameters = modelSegment.getElement(parameterQuery); 
        String signature = operation.getName() + "(";
        int ii = 0;
        for(org.opencrx.kernel.model1.jmi1.Parameter parameter: parameters) {
        	org.opencrx.kernel.model1.jmi1.Classifier type = parameter.getType();
            if(ii > 0) signature += ",";
            signature += "\n  ";
            String typeName = type == null ? 
            	"void" : 
            	(String)type.getQualifiedName(); 
            signature += 
                PARAMETER_DIRECTIONS[parameter.getDirection()] + " " +
                typeName + " " +
                "[" + MULTIPLICITIES[parameter.getMultiplicity()] + "] " +                
                parameter.getName() + " ";
        }
        signature += "\n);";
        return signature;
    }
    
    //-------------------------------------------------------------------------
    public void updateModelElement(
        org.opencrx.kernel.model1.jmi1.Element element
    ) throws ServiceException {
        org.opencrx.kernel.model1.jmi1.Namespace container = element.getContainer();
        String name = element.getName(); 
        element.setQualifiedName(
            container == null ? 
            	name : 
            	container.getQualifiedName() + ":" + name           
        );
    }
            
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    protected static final String[] PARAMETER_DIRECTIONS = {"N/A", "in", "out", "inout", "return"};
    protected static final String[] MULTIPLICITIES = {"N/A", "0..*", "0..1", "1", "1..*", "set", "sparsearray"};
        
}

//--- End of File -----------------------------------------------------------

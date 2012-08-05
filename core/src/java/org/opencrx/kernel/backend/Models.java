/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Models.java,v 1.12 2009/03/08 17:04:48 wfro Exp $
 * Description: Models
 * Revision:    $Revision: 1.12 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/08 17:04:48 $
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.Directions;
import org.openmdx.application.dataprovider.cci.Orders;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;

public class Models {

    //-----------------------------------------------------------------------
    public Models(
        Backend backend
    ) {
        this.backend = backend;
    }

    //-------------------------------------------------------------------------
    private String getOperationSignature(
        DataproviderObject_1_0 operation
    ) throws ServiceException {
        // Calculate operation signature
        List parameters = this.backend.getDelegatingRequests().addFindRequest(
            operation.path().getPrefix(5).getChild("element"),
            new FilterProperty[]{
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    "container",
                    FilterOperators.IS_IN,
                    new Object[]{operation.path().getPrefix(7)}
                ),                        
                new FilterProperty(
                    Quantors.THERE_EXISTS,
                    SystemAttributes.OBJECT_CLASS,
                    FilterOperators.IS_IN,
                    new Object[]{"org:opencrx:kernel:model1:Parameter"}
                )                        
            },                
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{
                new AttributeSpecifier("elementOrder", 0, Orders.ASCENDING)
            },
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING
        );        
        String signature = operation.values("name").get(0) + "(";
        int ii = 0;
        for(
            Iterator i = parameters.iterator();
            i.hasNext();
            ii++
        ) {
            DataproviderObject_1_0 parameter = (DataproviderObject_1_0)i.next();
            DataproviderObject_1_0 type = null;
            try {
                type = this.backend.retrieveObjectFromDelegation(
                    (Path)parameter.values("type").get(0)
                );
            } 
            catch(Exception e) {}
            if(ii > 0) signature += ",";
            signature += "\n  ";
            String typeName = type == null
                ? "void"
                : (String)type.values("qualifiedName").get(0); 
            signature += 
                PARAMETER_DIRECTIONS[((Number)parameter.values("direction").get(0)).intValue()] + " " +
                typeName + " " +
                "[" + MULTIPLICITIES[((Number)parameter.values("multiplicity").get(0)).intValue()] + "] " +                
                parameter.values("name").get(0) + " ";
        }
        signature += "\n);";
        return signature;
    }
    
    //-------------------------------------------------------------------------
    public void updateModelElement(
        DataproviderObject object,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        if(this.backend.isModelElement(object)) {
            DataproviderObject_1_0 container = null;
            if((oldValues != null) && (oldValues.values("container").size() > 0)) {
                container = this.backend.retrieveObject((Path)oldValues.values("container").get(0));
            }
            else if((object.getValues("container") != null) && (object.values("container").size() > 0)) {
                container = this.backend.retrieveObject((Path)object.values("container").get(0));
            }
            String name = oldValues == null 
                ? (String)object.values("name").get(0) 
                : object.getValues("name") == null ? (String)oldValues.values("name").get(0) : (String)object.values("name").get(0);
            object.clearValues("qualifiedName").add(
                container == null
                    ? name
                    : container.values("qualifiedName").get(0) + ":" + name
            );
        }
    }
            
    //-------------------------------------------------------------------------
    public List completeElement(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        if("org:opencrx:kernel:model1:Operation".equals(objectClass)) {
            object.clearValues("signature").add(
                this.getOperationSignature(object)
            );
        }
        return Collections.EMPTY_LIST;
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    protected final Backend backend;
    
    protected static final String[] PARAMETER_DIRECTIONS = {"N/A", "in", "out", "inout", "return"};
    protected static final String[] MULTIPLICITIES = {"N/A", "0..*", "0..1", "1", "1..*", "set", "sparsearray"};
        
}

//--- End of File -----------------------------------------------------------

/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Models.java,v 1.7 2007/12/26 22:41:46 wfro Exp $
 * Description: Models
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/26 22:41:46 $
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

import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.Orders;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;

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
            } catch(Exception e) {}
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
    public void updateType(
        DataproviderObject typedElement,
        DataproviderObject editableTypedElement
    ) throws ServiceException {
        if((editableTypedElement.getValues("type") != null) && (editableTypedElement.values("type").size() > 0)) {
            typedElement.clearValues("type").addAll(editableTypedElement.values("type"));
        }
        else if((editableTypedElement.getValues("typeName") != null) && (editableTypedElement.values("typeName").size() > 0)) {
            List types = this.backend.getDelegatingRequests().addFindRequest(
                editableTypedElement.path().getPrefix(6),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "qualifiedName",
                        FilterOperators.IS_IN,
                        new Object[]{editableTypedElement.values("typeName").get(0)}
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
            if(types.size() > 0) {
                DataproviderObject_1_0 type = (DataproviderObject_1_0)types.iterator().next();
                typedElement.clearValues("type").add(type.path());
            }
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
    public void removeOperation(
        Path editableOperation
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableOperation.getPrefix(6).getChild(editableOperation.getBase())
        );        
    }
    
    //-------------------------------------------------------------------------
    public void removeParameter(
        Path editableParameter
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableParameter.getPrefix(6).getChild(editableParameter.getBase())
        );        
    }
    
    //-------------------------------------------------------------------------
    public void removeAttribute(
        Path editableAttribute
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableAttribute.getPrefix(6).getChild(editableAttribute.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    public void removeStructureField(
        Path editableStructureField
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableStructureField.getPrefix(6).getChild(editableStructureField.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    public void removePackage(
        Path editablePackage
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editablePackage.getPrefix(6).getChild(editablePackage.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    public void removeClass(
        Path editableClass
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableClass.getPrefix(6).getChild(editableClass.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    public void removeStructureType(
        Path editableStructureType
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableStructureType.getPrefix(6).getChild(editableStructureType.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    public void removeOperationTag(
        Path editableOperationTag
    ) throws ServiceException {
        // The editable element and the (basedOn) element have the same qualifier
        this.backend.removeObject(
            editableOperationTag.getPrefix(6).getChild(editableOperationTag.getBase())
        );                
    }
    
    //-------------------------------------------------------------------------
    private void replaceElementAnnotation(
        DataproviderObject element,
        String annotation
    ) throws ServiceException {
        String existingAnnotation = (String)element.values("annotation").get(0);
        if(existingAnnotation == null) {
            element.clearValues("annotation").add(annotation);
        }
        // Replace first sentence of element's annotation by new annotation
        else {
            int posNewLine = existingAnnotation.indexOf("\n");
            posNewLine = posNewLine < 0 ? existingAnnotation.length() : posNewLine;
            int posDot = existingAnnotation.indexOf(".");
            posDot = posDot < 0 ? existingAnnotation.length() : posDot;
            int pos = Math.min(posNewLine, posDot);
            element.clearValues("annotation").add(
                annotation + existingAnnotation.substring(pos)
            );
        }        
    }
    
    //-------------------------------------------------------------------------
    public void replaceOperation(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedOperation = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedOperation.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedOperation,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedOperation.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
    }
        
    //-------------------------------------------------------------------------
    public void replaceParameter(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedParameter = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedParameter.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedParameter,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedParameter.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
        if(modificationRequest.attributeNames().contains("direction")) {
            modifiedParameter.clearValues("direction").addAll(modificationRequest.values("direction"));            
        }
        if(modificationRequest.attributeNames().contains("multiplicity")) {
            modifiedParameter.clearValues("multiplicity").addAll(modificationRequest.values("multiplicity"));            
        }
        if(modificationRequest.attributeNames().contains("upperBound")) {
            modifiedParameter.clearValues("upperBound").addAll(modificationRequest.values("upperBound"));            
        }
        this.updateType(
            modifiedParameter,
            request.object()
        );
    }
        
    //-------------------------------------------------------------------------
    public void replaceAttribute(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedAttribute = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedAttribute.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedAttribute,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedAttribute.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
        if(modificationRequest.attributeNames().contains("maxLength")) {
            modifiedAttribute.clearValues("maxLength").addAll(modificationRequest.values("maxLength"));            
        }
        if(modificationRequest.attributeNames().contains("multiplicity")) {
            modifiedAttribute.clearValues("multiplicity").addAll(modificationRequest.values("multiplicity"));            
        }
        if(modificationRequest.attributeNames().contains("upperBound")) {
            modifiedAttribute.clearValues("upperBound").addAll(modificationRequest.values("upperBound"));            
        }
        this.updateType(
            modifiedAttribute,
            request.object()
        );
    }
        
    //-------------------------------------------------------------------------
    public void replaceStructureField(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedStructureField = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedStructureField.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedStructureField,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedStructureField.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
        if(modificationRequest.attributeNames().contains("maxLength")) {
            modifiedStructureField.clearValues("maxLength").addAll(modificationRequest.values("maxLength"));            
        }
        if(modificationRequest.attributeNames().contains("multiplicity")) {
            modifiedStructureField.clearValues("multiplicity").addAll(modificationRequest.values("multiplicity"));            
        }
        if(modificationRequest.attributeNames().contains("upperBound")) {
            modifiedStructureField.clearValues("upperBound").addAll(modificationRequest.values("upperBound"));            
        }
        this.updateType(
            modifiedStructureField,
            request.object()
        );
    }
        
    //-------------------------------------------------------------------------
    public void replacePackage(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedPackage = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedPackage.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedPackage,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedPackage.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
    }
        
    //-------------------------------------------------------------------------
    public void replaceClass(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedClass = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedClass.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedClass,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedClass.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
    }
        
    //-------------------------------------------------------------------------
    public void replaceStructureType(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedStructureType = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedStructureType.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedStructureType,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedStructureType.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
    }
        
    //-------------------------------------------------------------------------
    public void replaceOperationTag(
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderObject modificationRequest = request.object();
        // The editable element and the (basedOn) element have the same qualifier
        DataproviderObject modifiedOperationTag = this.backend.retrieveObjectForModification(
            request.path().getPrefix(6).getChild(request.path().getBase())
        );        
        if(modificationRequest.attributeNames().contains("name")) {
            modifiedOperationTag.clearValues("name").addAll(modificationRequest.values("name"));            
        }
        if(modificationRequest.attributeNames().contains("annotation")) {
            this.replaceElementAnnotation(
                modifiedOperationTag,
                (String)modificationRequest.values("annotation").get(0)
            );
        }
        if(modificationRequest.attributeNames().contains("elementOrder")) {
            modifiedOperationTag.clearValues("elementOrder").addAll(modificationRequest.values("elementOrder"));            
        }
        if(modificationRequest.attributeNames().contains("tagValue")) {
            modifiedOperationTag.clearValues("tagValue").addAll(modificationRequest.values("tagValue"));            
        }
    }
        
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    protected final Backend backend;
    
    protected static final String[] PARAMETER_DIRECTIONS = {"N/A", "in", "out", "inout", "return"};
    protected static final String[] MULTIPLICITIES = {"N/A", "0..*", "0..1", "1", "1..*", "set", "sparsearray"};
        
}

//--- End of File -----------------------------------------------------------

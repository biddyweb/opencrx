/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AbstractPropertyDataBinding.java,v 1.5 2008/10/01 00:28:30 wfro Exp $
 * Description: AbstractPropertyDataBinding
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/01 00:28:30 $
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
package org.opencrx.kernel.portal;

import java.util.Collection;

import javax.jmi.reflect.RefObject;

import org.opencrx.kernel.base.jmi1.Property;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.generic.jmi1.PropertySet;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.portal.servlet.DataBinding_1_0;

public abstract class AbstractPropertyDataBinding implements DataBinding_1_0 {

    //-----------------------------------------------------------------------
    public Property findProperty(
        RefObject object,
        String qualifiedFeatureName
    ) {
        if(
            (object instanceof CrxObject) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            CrxObject crxObject = (CrxObject)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            Collection<PropertySet> propertySets = crxObject.getPropertySet();
            for(PropertySet ps : propertySets) {
                if(ps.getName().equals(propertySetName)) {
                    Collection<Property> properties = ps.getProperty();
                    for(Property p: properties) {
                        if(p.getName().equals(propertyName)) {
                            return p;
                        }
                    }
                }
            }
        }  
        return null;
    }

    //-----------------------------------------------------------------------
    public void createProperty(
        RefObject object,
        String qualifiedFeatureName,
        Property property
    ) {
        if(
            (object instanceof CrxObject) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            CrxObject crxObject = (CrxObject)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            // Find / Create property set
            PropertySet propertySet = null;
            Collection<PropertySet> propertySets = crxObject.getPropertySet();
            for(PropertySet ps : propertySets) {
                if(ps.getName().equals(propertySetName)) {
                    propertySet = ps;
                    break;
                }
            }
            if(propertySet == null) {
                org.opencrx.kernel.generic.jmi1.GenericPackage genericPkg = 
                    (org.opencrx.kernel.generic.jmi1.GenericPackage)object.refOutermostPackage().refPackage(
                        org.opencrx.kernel.generic.jmi1.GenericPackage.class.getName()
                    );                
                propertySet = genericPkg.getPropertySet().createPropertySet();
                propertySet.setName(propertySetName);
                crxObject.addPropertySet(
                    false,
                    UUIDs.getGenerator().next().toString(),
                    propertySet
                );
            }
            // Add property to property set
            property.setName(propertyName);
            propertySet.addProperty(
                false,
                UUIDs.getGenerator().next().toString(),
                property
            );
        }          
    }
    
}

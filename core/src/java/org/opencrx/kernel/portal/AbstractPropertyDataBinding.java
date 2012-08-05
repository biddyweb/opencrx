/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AbstractPropertyDataBinding.java,v 1.8 2009/03/08 17:04:54 wfro Exp $
 * Description: AbstractPropertyDataBinding
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/08 17:04:54 $
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
import org.opencrx.kernel.product1.jmi1.ProductConfiguration;
import org.opencrx.kernel.product1.jmi1.ProductConfigurationSet;
import org.opencrx.kernel.product1.jmi1.ProductConfigurationType;
import org.opencrx.kernel.product1.jmi1.ProductConfigurationTypeSet;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.portal.servlet.DataBinding_1_0;

public abstract class AbstractPropertyDataBinding implements DataBinding_1_0 {

    //-----------------------------------------------------------------------
    public enum PropertySetHolderType {
        CrxObject,
        ProductConfigurationTypeSet,
        ProductConfigurationSet
    }

    //-----------------------------------------------------------------------
    public AbstractPropertyDataBinding(
        PropertySetHolderType type
    ) {
        this.type = type;
    }
    
    //-----------------------------------------------------------------------
    protected Property findProperty(
        RefObject object,
        String qualifiedFeatureName
    ) {
        // ProductConfigurationTypeSet
        if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.ProductConfigurationTypeSet) &&
            (object instanceof ProductConfigurationTypeSet) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            ProductConfigurationTypeSet productConfigurationTypeSet = (ProductConfigurationTypeSet)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            Collection<ProductConfigurationType> configurationTypes = productConfigurationTypeSet.getConfigurationType();
            for(ProductConfigurationType configurationType : configurationTypes) {
                if(configurationType.getName().equals(propertySetName)) {
                    Collection<Property> properties = configurationType.getProperty();
                    for(Property p: properties) {
                        if(p.getName().equals(propertyName)) {
                            return p;
                        }
                    }
                }
            }
        }  
        // ProductConfigurationTypeSet
        else if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.ProductConfigurationSet) &&
            (object instanceof ProductConfigurationSet) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            ProductConfigurationSet productConfigurationSet = (ProductConfigurationSet)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            Collection<ProductConfiguration> configurations = productConfigurationSet.getConfiguration();
            for(ProductConfiguration configuration : configurations) {
                if(configuration.getName().equals(propertySetName)) {
                    Collection<Property> properties = configuration.getProperty();
                    for(Property p: properties) {
                        if(p.getName().equals(propertyName)) {
                            return p;
                        }
                    }
                }
            }
        }  
        // CrxObject
        else if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.CrxObject) &&
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
    protected void createProperty(
        RefObject object,
        String qualifiedFeatureName,
        Property property
    ) {
        // ProductConfigurationTypeSet
        if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.ProductConfigurationTypeSet) &&
            (object instanceof ProductConfigurationTypeSet) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            ProductConfigurationTypeSet productConfigurationTypeSet = (ProductConfigurationTypeSet)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            // Find / Create property set
            ProductConfigurationType configurationType = null;
            Collection<ProductConfigurationType> configurationTypes = productConfigurationTypeSet.getConfigurationType();
            for(ProductConfigurationType ct : configurationTypes) {
                if(ct.getName().equals(propertySetName)) {
                    configurationType = ct;
                    break;
                }
            }
            if(configurationType == null) {
                org.opencrx.kernel.product1.jmi1.Product1Package productPkg = 
                    (org.opencrx.kernel.product1.jmi1.Product1Package)object.refOutermostPackage().refPackage(
                        org.opencrx.kernel.product1.jmi1.Product1Package.class.getName()
                    );                
                configurationType = productPkg.getProductConfigurationType().createProductConfigurationType();
                configurationType.setName(propertySetName);
                productConfigurationTypeSet.addConfigurationType(
                    UUIDConversion.toUID(uuids.next()),
                    configurationType
                );
            }
            // Add property to property set
            property.setName(propertyName);
            configurationType.addProperty(
                false,
                UUIDConversion.toUID(uuids.next()),
                property
            );
        }          
        // ProductConfigurationTypeSet
        else if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.ProductConfigurationSet) &&
            (object instanceof ProductConfigurationSet) &&
            (qualifiedFeatureName.indexOf("!") > 0)
        ) {
            ProductConfigurationSet productConfigurationSet = (ProductConfigurationSet)object;
            String featureName = qualifiedFeatureName.substring(qualifiedFeatureName.lastIndexOf(":") + 1);
            String propertySetName = featureName.substring(0, featureName.indexOf("!"));
            String propertyName = featureName.substring(featureName.indexOf("!") + 1);
            // Find / Create property set
            ProductConfiguration configuration = null;
            Collection<ProductConfiguration> configurations = productConfigurationSet.getConfiguration();
            for(ProductConfiguration c : configurations) {
                if(c.getName().equals(propertySetName)) {
                    configuration = c;
                    break;
                }
            }
            if(configuration == null) {
                org.opencrx.kernel.product1.jmi1.Product1Package productPkg = 
                    (org.opencrx.kernel.product1.jmi1.Product1Package)object.refOutermostPackage().refPackage(
                        org.opencrx.kernel.product1.jmi1.Product1Package.class.getName()
                    );                
                configuration = productPkg.getProductConfiguration().createProductConfiguration();
                configuration.setName(propertySetName);
                productConfigurationSet.addConfiguration(
                    UUIDConversion.toUID(uuids.next()),
                    configuration
                );
            }
            // Add property to property set
            property.setName(propertyName);
            configuration.addProperty(
                false,
                UUIDConversion.toUID(uuids.next()),
                property
            );
        }          
        // CrxObject
        else if(
            (this.type == AbstractPropertyDataBinding.PropertySetHolderType.CrxObject) &&
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
                    UUIDConversion.toUID(uuids.next()),
                    propertySet
                );
            }
            // Add property to property set
            property.setName(propertyName);
            propertySet.addProperty(
                false,
                UUIDConversion.toUID(uuids.next()),
                property
            );
        }          
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final UUIDGenerator uuids = UUIDs.getGenerator();
    private final PropertySetHolderType type;
}

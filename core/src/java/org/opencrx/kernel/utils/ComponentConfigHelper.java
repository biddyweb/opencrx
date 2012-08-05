/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ComponentConfigHelper.java,v 1.5 2009/06/09 14:10:35 wfro Exp $
 * Description: ComponentConfigHelper
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/09 14:10:35 $
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
package org.opencrx.kernel.utils;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.admin1.jmi1.Admin1Package;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

public class ComponentConfigHelper {

    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.admin1.jmi1.ComponentConfiguration getComponentConfiguration(
        String configurationId,
        String providerName,
        PersistenceManager pm,
        boolean autoCreate,
        String[][] initialProperties
    ) {
        org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;
        try {
            Admin1Package adminPackage = Utils.getAdminPackage(pm);            
            org.opencrx.kernel.base.jmi1.BasePackage basePackage = Utils.getBasePackage(pm); 
            org.opencrx.kernel.admin1.jmi1.Segment adminSegment = 
                (org.opencrx.kernel.admin1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root").toXri()
                );
            try {
                componentConfiguration = adminSegment.getConfiguration(configurationId);
            }
            catch(Exception e) {}
            if(autoCreate && (componentConfiguration == null)) {
                componentConfiguration = 
                    adminPackage.getComponentConfiguration().createComponentConfiguration();
                componentConfiguration.setName(configurationId);
                pm.currentTransaction().begin();
                adminSegment.addConfiguration(
                    false,
                    configurationId,
                    componentConfiguration
                );
                UUIDGenerator uuids = UUIDs.getGenerator();
                for(String[] e: initialProperties) {
                    org.opencrx.kernel.base.jmi1.StringProperty sp = basePackage.getStringProperty().createStringProperty();
                    sp.setName(e[0]);
                    sp.setStringValue(e[1]);
                    componentConfiguration.addProperty(
                        false,
                        uuids.next().toString(),
                        sp
                    );
                }
                pm.currentTransaction().commit();
                componentConfiguration = adminSegment.getConfiguration(
                    configurationId
                );
            }
        }
        catch(Exception e) {
            AppLog.warning("Can not get component configuration", e);
        }
        return componentConfiguration;
    }
    
    //-----------------------------------------------------------------------
    public static org.opencrx.kernel.base.jmi1.StringProperty getComponentConfigProperty(
        String name,
        org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(componentConfiguration);
        org.opencrx.kernel.base.jmi1.StringProperty value = null;
        for(int i = 0; i < 1; i++) {
        	Collection<org.opencrx.kernel.base.jmi1.Property> properties = componentConfiguration.getProperty();
            for(org.opencrx.kernel.base.jmi1.Property p: properties) {
                if(
                    p.getName().equals(name) &&
                    (p instanceof org.opencrx.kernel.base.jmi1.StringProperty)
                ) {
                    value = (org.opencrx.kernel.base.jmi1.StringProperty)p;
                    break;
                }
            }
            if(value == null) {
                pm.refresh(componentConfiguration);
            }
            else {
                break;
            }
        }
        return value;
    }
    
}

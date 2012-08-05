/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SecureObjectImpl.java,v 1.5 2008/08/29 14:11:44 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/08/29 14:11:44 $
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
package org.opencrx.kernel.plugin.application.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.backend.SecureObject;
import org.opencrx.kernel.base.jmi1.BasePackage;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.naming.Path;

public class SecureObjectImpl {

    //-----------------------------------------------------------------------
    public SecureObjectImpl(
        org.opencrx.kernel.base.jmi1.SecureObject current,
        org.opencrx.kernel.base.cci2.SecureObject next
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
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult setOwningUser(
        org.opencrx.kernel.base.jmi1.SetOwningUserParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            new SecureObject(
                backend, 
                params.getUser() == null ? null : params.getUser().refGetPath(),
                null,
                params.getMode(),
                null,
                null,
                null
            ).setOwningUser(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                 backend.analyseReport(report)
            );    
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult addOwningGroup(
        org.opencrx.kernel.base.jmi1.ModifyOwningGroupParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            new SecureObject(
                backend, 
                null,
                Arrays.asList(
                    params.getGroup() == null ? 
                        null : 
                        params.getGroup().refGetPath()
                ),
                params.getMode(),
                null,
                null,
                null
            ).addOwningGroup(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                backend.analyseReport(report)
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult removeOwningGroup(
        org.opencrx.kernel.base.jmi1.ModifyOwningGroupParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            new SecureObject(
                backend, 
                null,
                Arrays.asList(
                    params.getGroup() == null ? 
                        null : 
                        params.getGroup().refGetPath()
                ),
                params.getMode(),
                null,
                null,
                null
            ).removeOwningGroup(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                backend.analyseReport(report)
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult removeAllOwningGroup(
        org.opencrx.kernel.base.jmi1.RemoveAllOwningGroupParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            new SecureObject(
                backend, 
                null,
                null,
                params.getMode(),
                null,
                null,
                null
            ).removeAllOwningGroup(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                backend.analyseReport(report)
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }   
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult setAccessLevel(
        org.opencrx.kernel.base.jmi1.SetAccessLevelParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            new SecureObject(
                backend, 
                null,
                null,
                params.getMode(),
                params.getAccessLevelBrowse(),
                params.getAccessLevelUpdate(),
                params.getAccessLevelDelete()
            ).setAccessLevel(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                backend.analyseReport(report)
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }   
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.ModifySecureObjectResult replaceOwningGroup(
        org.opencrx.kernel.base.jmi1.ModifyOwningGroupsParams params
    ) {
        try {
            Backend backend = this.getBackend();
            List<String> report = new ArrayList<String>();
            List<Path> owningGroups = new ArrayList<Path>();
            List<PrincipalGroup> groups = params.getGroup();
            for(PrincipalGroup group: groups) {
                owningGroups.add(group.refGetPath());
            }
            new SecureObject(
                backend, 
                null,
                owningGroups,
                params.getMode(),
                null,
                null,
                null
            ).replaceOwningGroups(
                this.current.refGetPath(),
                report
            );
            return ((BasePackage)this.current.refOutermostPackage().refPackage(BasePackage.class.getName())).createModifySecureObjectResult(
                backend.analyseReport(report)
            );            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                    
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.base.jmi1.SecureObject current;
    protected final org.opencrx.kernel.base.cci2.SecureObject next;    
    
}

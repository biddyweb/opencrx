/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: BackendContext.java,v 1.13 2009/02/10 16:34:26 wfro Exp $
 * Description: BackendContext
 * Revision:    $Revision: 1.13 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/10 16:34:26 $
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.Dataprovider_1_0;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.mof.cci.Model_1_3;
import org.openmdx.base.naming.Path;

public class BackendContext {

    //-----------------------------------------------------------------------
    public BackendContext(
        Model_1_3 model,
        ServiceHeader header,
        List<DataproviderObject> pendingModifications,
        RequestCollection delegatingRequests,
        RefPackage_1_0 delegatingPkg,
        Layer_1_0 delegatingLayer,
        RequestCollection delegationAsRoot,
        RefPackage_1_0 localPkg,
        RequestCollection localRequests,
        Dataprovider_1_0 router,
        String passwordEncodingAlgorithm,
        Path codeSegmentIdentity,
        Path realmIdentity,
        List readOnlyTypes
    ) {
        this.model = model;
        this.header = header;
        this.pendingModifications = pendingModifications;
        this.delegatingRequests = delegatingRequests;
        this.delegatingPkg = delegatingPkg;
        this.delegatingLayer = delegatingLayer;
        this.delegationAsRoot = delegationAsRoot;
        this.localPkg = localPkg;
        this.localRequests = localRequests;
        this.router = router;
        this.passwordEncodingAlgorithm = passwordEncodingAlgorithm;
        this.codeSegmentIdentity = codeSegmentIdentity;
        this.realmIdentity = realmIdentity;
        this.readOnlyTypes = readOnlyTypes;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public final Model_1_3 model;
    public final ServiceHeader header;
    public final List<DataproviderObject> pendingModifications;
    public final Map<Path,DataproviderObject> modifiedObjects = new HashMap<Path,DataproviderObject>();    
    public final RequestCollection delegatingRequests;
    public final RefPackage_1_0 delegatingPkg;
    public final Layer_1_0 delegatingLayer;
    public final RequestCollection delegationAsRoot;
    public final RefPackage_1_0 localPkg;
    public final RequestCollection localRequests;
    public final Dataprovider_1_0 router;
    public final String passwordEncodingAlgorithm;
    public final Path codeSegmentIdentity;
    public final Path realmIdentity;
    public final List readOnlyTypes;
    
}

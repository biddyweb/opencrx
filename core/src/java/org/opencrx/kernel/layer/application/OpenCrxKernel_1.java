/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: OpenCrxKernel_1.java,v 1.314 2009/05/15 15:23:17 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.314 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/15 15:23:17 $
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

package org.opencrx.kernel.layer.application;

import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.opencrx.kernel.generic.OpenCrxException;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;

/**
 * openCRX application plugin. This plugin implements the openCRX 
 * application logic. It is implemented as openMDX compatibility plugin. It 
 * will be migrated to a JMI plugin in version 2.x of openCRX. This will make
 * the plugin more object-oriented and more extensible.
 * 
 * However, it can be extended as follows by user-defined application logic:
 * <ul>
 *   <li>Object validation: override the method validateObject().
 *   <li>Workflow: provide a class implementing the interface Workflow_1_0.
 *       For sample workflows @see org.opencrx.kernel.worklow. The workflow
 *       class must be registered as WfProcess object using the GUI.
 *   <li>Derived attributes: the computation of derived attributes can be
 *       added by extending completeObject().
 * </ul>
 */

public class OpenCrxKernel_1 extends Layer_1 {

    //-------------------------------------------------------------------------
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws ServiceException {        
        super.activate(
            id,
            configuration,
            delegation
        );
        try {
            // Readonly object types
            this.readOnlyTypes = configuration.values("readOnlyObjectType");
            this.datatypes = DatatypeFactory.newInstance();
            this.requestHelper = this.getRequestHelper();
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }        
    }

    //-------------------------------------------------------------------------
    protected RequestContext getRequestContext(
        ServiceHeader header
    ) throws ServiceException {
        return new RequestContext(
            this.getModel(),
            header,
            this.delegation,
            this.getDelegation(),
            new RequestCollection(
                header,
                this
            ),
            this.readOnlyTypes
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * Implement a user-defined backend as follows:
     * <ul>
     *   <li>Create a backend class which extends the default backend class <code>Backend</code>
     *   <li>Create a plugin class which extends this plugin and override the method <code>getBackend</code>
     * </ul>
     */
    protected RequestHelper getRequestHelper(
    ) throws ServiceException {
        return new RequestHelper();
    }
    
    //-------------------------------------------------------------------------
    public void testReferenceIsChangeable(
        Path referencePath
    ) throws ServiceException {
        // Reference must be changeable
        ModelElement_1_0 reference = null;
        try {
            reference = this.getModel().getReferenceType(referencePath);
        }
        catch(ServiceException e) {
            AppLog.warning("Reference not found in model", referencePath);
        }           
        if(
            (reference != null) &&
            !((Boolean)reference.objGetValue("isChangeable")).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.REFERENCE_IS_READONLY,
                "Reference is readonly. Can not add/remove objects.",
                new BasicException.Parameter("param0", referencePath)
            );                                                                
        }
    }

    //-------------------------------------------------------------------------
    public void prolog(
        ServiceHeader header,
        DataproviderRequest[] requests
    ) throws ServiceException {
        if(
            (this.localCorrelationId == null) ||
            !localCorrelationId.equals(header.getCorrelationId())
        ) {
            super.prolog(
                header,
                requests
            );
            this.header = header;
            this.delegation = new RequestCollection(
                header,
                this.getDelegation()
            );
            this.requestHelper.open(
                this.getRequestContext(
                    header
                )
            );
        }
    }

    //-------------------------------------------------------------------------
    public void epilog(
        ServiceHeader header,
        DataproviderRequest[] requests,
        DataproviderReply[] replies
    ) throws ServiceException {      
        if(
            (this.localCorrelationId == null) ||
            !localCorrelationId.equals(header.getCorrelationId())
        ) {
            AppLog.trace("Process pending modifications");
            AppLog.trace("Flush object modifications");
            super.epilog(
                header,
                requests,
                replies
            );
        }
    }

    //-------------------------------------------------------------------------
    public DataproviderReply get(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderReply reply = this.requestHelper.getDerivedReferences().getReply(header, request);
        if(reply == null) {
            reply = super.get(
                header,
                request
            );
        }
        return reply;
    }

    //-------------------------------------------------------------------------
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderReply reply = this.requestHelper.getDerivedReferences().getReply(header, request);
        if(reply == null) {
            reply = super.find(
                header,
                request
            );            
        }
        return reply;
    }

    //-------------------------------------------------------------------------
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.testReferenceIsChangeable(
            request.path().getParent()
        );
        DataproviderReply reply = super.create(
            header,
            request
        );
        return reply;
    }

    //-------------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        
        this.testReferenceIsChangeable(
            request.path().getParent()
        );
        return super.remove(
            header,
            request
        );
    }

    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    public static final String MAX_DATE = "99991231T000000.000Z"; 

    protected ServiceHeader header = null;
    protected RequestCollection delegation = null;    

    // Plugin-level
    protected List<String> readOnlyTypes = null;
    public DatatypeFactory datatypes = null;
    public String localCorrelationId = null;
    
    // Backend
    public RequestHelper requestHelper = null;

}

//--- End of File -----------------------------------------------------------

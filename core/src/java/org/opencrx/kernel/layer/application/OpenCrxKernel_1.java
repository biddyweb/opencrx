/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: OpenCrxKernel_1
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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

import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.xml.datatype.DatatypeFactory;

import org.opencrx.kernel.generic.OpenCrxException;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.layer.application.Standard_1;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.resource.spi.RestInteractionSpec;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

public class OpenCrxKernel_1 extends Standard_1 {

    //-------------------------------------------------------------------------
	public OpenCrxKernel_1(
	) {
	}
	
    //--------------------------------------------------------------------------
    public Interaction getInteraction(
        Connection connection
    ) throws ResourceException {
        return new StandardLayerInteraction(connection);
    }
 
    //-------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
    @Override
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1 delegation
    ) throws ServiceException {        
        super.activate(
            id,
            configuration,
            delegation
        );
        try {
            this.readOnlyTypes = new ArrayList(
            	configuration.values("readOnlyObjectType").values()
            );
            this.datatypes = DatatypeFactory.newInstance();
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }        
    }

    //--------------------------------------------------------------------------
    public class StandardLayerInteraction extends Standard_1.LayerInteraction {
      
        public StandardLayerInteraction(
            Connection connection
        ) throws ResourceException {
            super(connection);
        }
        
	    //-------------------------------------------------------------------------
	    protected RequestContext getRequestContext(
	        ServiceHeader header
	    ) throws ServiceException {
	        return new RequestContext(
	            this.getModel(),
	            header,
	            this.getDelegatingInteraction(),
	            OpenCrxKernel_1.this.readOnlyTypes
	        );
	    }
	    
	    //-------------------------------------------------------------------------
	    protected RequestHelper getRequestHelper(
	    	RequestContext context
	    ) throws ServiceException {
	        return new RequestHelper(context);
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
	        	SysLog.warning("Reference not found in model", referencePath);
	        }           
	        if(
	            (reference != null) &&
	            !((Boolean)reference.isChangeable()).booleanValue()
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
	    @Override
	    public boolean get(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	RequestHelper requestHelper = this.getRequestHelper(
	    		this.getRequestContext(
	                header
	            )
	    	);
	        boolean isDerived = requestHelper.getDerivedReferences().getReply(
	        	header, 
	        	request,
	        	reply
	        );
	        if(!isDerived) {
	            super.get(
	                ispec,
	                input,
	                output
	            );
	        }
	        return true;
	    }
	
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean find(
	        RestInteractionSpec ispec,
	        Query_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
        	ServiceHeader header = this.getServiceHeader();
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
            DataproviderReply reply = this.newDataproviderReply(output);
	    	RequestHelper requestHelper = this.getRequestHelper(
	    		this.getRequestContext(
	                header
	            )    		
	    	);
	        boolean isDerived = requestHelper.getDerivedReferences().getReply(
	        	header, 
	        	request,
	        	reply
	        );
	        if(!isDerived) {
	            super.find(
	                ispec,
	                input,
	                output
	            );            
	        }
	        return true;
	    }
	
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean create(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	        this.testReferenceIsChangeable(
	            request.path().getParent()
	        );
	        super.create(
	        	ispec,
	        	input,
	        	output
	        );
	        return true;
	    }
	
	    //-------------------------------------------------------------------------
	    @Override
	    public boolean delete(
	        RestInteractionSpec ispec,
	        Object_2Facade input,
	        IndexedRecord output
	    ) throws ServiceException {
            DataproviderRequest request = this.newDataproviderRequest(ispec, input);
	        this.testReferenceIsChangeable(
	            request.path().getParent()
	        );
	        super.delete(
	            ispec,
	            input,
	            output
	        );
	        return true;
	    }
	    
    }
    
    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    public static final String MAX_DATE = "99991231T000000.000Z"; 

    // Configuration
    protected List<String> readOnlyTypes = null;
    public DatatypeFactory datatypes = null;

}

//--- End of File -----------------------------------------------------------

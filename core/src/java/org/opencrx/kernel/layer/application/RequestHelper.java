/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: RequestHelper
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

import java.util.Iterator;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;

import org.opencrx.kernel.generic.OpenCrxException;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.AttributeSpecifier;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.spi.Layer_1;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.rest.spi.Object_2Facade;
import org.openmdx.base.rest.spi.Query_2Facade;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;

public class RequestHelper {

    //-----------------------------------------------------------------------
    public RequestHelper(
    	RequestContext context
    ) {
        this.context = context;
        this.derivedReferences = null;                
    }

    //-----------------------------------------------------------------------
    public DerivedReferences getDerivedReferences(
    ) {
        if(this.derivedReferences == null) {
            this.derivedReferences = new DerivedReferences(
                this
            );
        }
        return this.derivedReferences;
    }

    //-----------------------------------------------------------------------
    public Layer_1.LayerInteraction getDelegatingInteraction(
    ) {
        return this.context.delegatingInteraction;
    }
    
    //-----------------------------------------------------------------------
    public ServiceHeader getServiceHeader(
    ) {
        return this.context.header;
    }
    
    //-------------------------------------------------------------------------
    public Model_1_0 getModel(
    ) {
        return this.context.model;
    }
    
    //-------------------------------------------------------------------------
    public String getUidAsString(
    ) {
        return UUIDConversion.toUID(UUIDs.newUUID());        
    }
    
    //-------------------------------------------------------------------------
    public MappedRecord retrieveObject(
        Path identity
    ) throws ServiceException {
    	try {
	    	DataproviderRequest getRequest = new DataproviderRequest(
	            Query_2Facade.newInstance(identity).getDelegate(),
	            DataproviderOperations.OBJECT_RETRIEVAL,
	            AttributeSelectors.ALL_ATTRIBUTES,
	            new AttributeSpecifier[]{}
	    	);
	    	DataproviderReply getReply = new DataproviderReply();
	    	this.getDelegatingInteraction().get(
	    		getRequest.getInteractionSpec(), 
	    		Query_2Facade.newInstance(getRequest.object()), 
	    		getReply.getResult()
	    	);
	    	return getReply.getObject();
    	} catch(ResourceException e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testObjectIsChangeable(
        MappedRecord object
    ) throws ServiceException {
    	Object_2Facade facade;
        try {
	        facade = Object_2Facade.newInstance(object);
        }
        catch (ResourceException e) {
        	throw new ServiceException(e);
        }
        String objectClass = facade.getObjectClass();
        for(
            Iterator<String> i = this.context.readOnlyTypes.iterator();
            i.hasNext();
        ) {           
            String type = i.next();
            if(this.getModel().isSubtypeOf(objectClass, type)) {
                for(
                    Iterator<String> j = facade.getValue().keySet().iterator();
                    j.hasNext();
                ) {
                    String attributeName = j.next();
                    if(
                        !SystemAttributes.OBJECT_CLASS.equals(attributeName) &&
                        !SystemAttributes.MODIFIED_AT.equals(attributeName) &&
                        !SystemAttributes.MODIFIED_BY.equals(attributeName)                        
                    ) {
                        throw new ServiceException(
                            OpenCrxException.DOMAIN,
                            OpenCrxException.OBJECT_TYPE_IS_READONLY,
                            "Object type is readonly. Can not modify object.",
                            new BasicException.Parameter("param0", objectClass)
                        );
                    }
                }
            }
        }
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------    
    public final RequestContext context;
    protected DerivedReferences derivedReferences = null;
    
}

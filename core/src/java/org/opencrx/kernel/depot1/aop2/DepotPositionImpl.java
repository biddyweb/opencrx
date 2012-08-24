/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: DepotPositionImpl
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
package org.opencrx.kernel.depot1.aop2;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOUserException;
import javax.jdo.listener.DeleteCallback;

import org.opencrx.kernel.backend.Depots;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class DepotPositionImpl
	<S extends org.opencrx.kernel.depot1.jmi1.DepotPosition,N extends org.opencrx.kernel.depot1.cci2.DepotPosition,C extends Void>
	extends AbstractObject<S,N,C>
	implements DeleteCallback {
    
    //-----------------------------------------------------------------------
    public DepotPositionImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.depot1.jmi1.CloseDepotPositionResult closeDepotPosition(
        org.opencrx.kernel.depot1.jmi1.CloseDepotPositionParams params
    ) {
        try {
            List<String> errors = new ArrayList<String>();
            Depots.getInstance().closeDepotPosition(
                this.sameObject(),
                params.getClosingDate(),
                errors
            );
            return Utils.getDepotPackage(this.sameManager()).createCloseDepotPositionResult(
                (short)0, 
                null
            );
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }        
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void jdoPreDelete(
    ) {
    	try {
    		Depots.getInstance().removeDepotPosition(
    			this.sameObject(), 
    			true
    		);
    		super.jdoPreDelete();
    	}
    	catch(ServiceException e) {
    		throw new JDOUserException(
    			"jdoPreDelete failed",
    			e,
    			this.sameObject()
    		);
    	}
    }
                    
}

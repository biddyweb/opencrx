/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SalesVolumeBudgetImpl.java,v 1.1 2010/11/03 16:22:02 wfro Exp $
 * Description: AccountImpl
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/11/03 16:22:02 $
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
package org.opencrx.kernel.forecast1.aop2;

import javax.jdo.listener.StoreCallback;

import org.opencrx.kernel.backend.Forecasts;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.exception.ServiceException;

public class SalesVolumeBudgetImpl
	<S extends org.opencrx.kernel.forecast1.jmi1.SalesVolumeBudget,N extends org.opencrx.kernel.forecast1.cci2.SalesVolumeBudget,C extends Void>
	extends AbstractBudgetImpl<S,N,C>
	implements StoreCallback {

    //-----------------------------------------------------------------------
    public SalesVolumeBudgetImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void recalcBudget(
    ) {
    	try {
	        Forecasts.getInstance().recalcBudget(
	            this.sameObject()
	        );
	        return this.newVoid();                     
    	}
    	catch(ServiceException e) {
    		throw new JmiServiceException(e);
    	}
    }
        
}
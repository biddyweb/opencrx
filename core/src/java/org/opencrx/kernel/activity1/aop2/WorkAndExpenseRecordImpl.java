/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WorkAndExpenseRecordImpl.java,v 1.4 2010/09/29 14:03:51 wfro Exp $
 * Description: WorkAndExpenseRecordImpl
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/09/29 14:03:51 $
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
package org.opencrx.kernel.activity1.aop2;

import javax.jdo.JDOUserException;
import javax.jdo.listener.DeleteCallback;
import javax.jdo.listener.StoreCallback;

import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ResourceAssignment;
import org.opencrx.kernel.backend.Activities;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class WorkAndExpenseRecordImpl 
	<S extends org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord,N extends org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecord,C extends Void>
	extends AbstractObject<S,N,C>
	implements StoreCallback, DeleteCallback {

    //-----------------------------------------------------------------------
    public WorkAndExpenseRecordImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    /**
     * Derived attribute activity
     */
    public org.opencrx.kernel.activity1.jmi1.Activity getActivity(
    ) {
    	try {
	    	return (Activity)this.sameManager().getObjectById(
	    		this.sameObject().refGetPath().getPrefix(7)
	    	);
    	}
    	catch(Exception e) {
    		return null;
    	}
    }

    //-----------------------------------------------------------------------
    /**
     * Derived attribute resource
     */
    public org.opencrx.kernel.activity1.jmi1.Resource getResource(
    ) {
    	try {
	    	return ((ResourceAssignment)this.sameManager().getObjectById(
	    		this.sameObject().refGetPath().getPrefix(9)
	    	)).getResource();
    	}
    	catch(Exception e) {
    		return null;
    	}
    }
        
    //-----------------------------------------------------------------------
    @Override
	public void jdoPreStore(
	) {
		try {
			Activities.getInstance().updateWorkAndExpenseRecord(
				this.sameObject() 
			);
			super.jdoPreStore();
		}
		catch(ServiceException e) {
			throw new JDOUserException(
				"preStore failed",
				e,
				this.sameObject()
			);
		}
    }

    //-----------------------------------------------------------------------
    @Override
	public void jdoPreDelete(
	) {
		try {
			Activities.getInstance().removeWorkRecord(
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

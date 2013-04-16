/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: openCRX application plugin
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
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
package org.opencrx.kernel.base.aop2;

import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

public class CloneableImpl
	<S extends org.opencrx.kernel.base.jmi1.Cloneable,N extends org.opencrx.kernel.base.cci2.Cloneable,C extends Void>
	extends AbstractObject<S,N,C> {

    //-----------------------------------------------------------------------
    public CloneableImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.base.jmi1.CloneResult clone_(
        org.opencrx.kernel.base.jmi1.CloneParams params
    ) {
        try {
            BasicObject clonedObject = (BasicObject)org.opencrx.kernel.backend.Cloneable.getInstance().cloneObject(
            	this.sameObject(), 
            	(RefObject_1_0)this.sameManager().getObjectById(
            		this.sameObject().refGetPath().getParent().getParent()
            	), 
            	this.sameObject().refGetPath().getParent().getBase(), 
            	null, 
            	params.getReferenceFilter(),
            	this.sameObject() instanceof org.opencrx.kernel.base.jmi1.SecureObject ? 
            		((org.opencrx.kernel.base.jmi1.SecureObject)this.sameObject()).getOwningUser() : 
            		null,
               	this.sameObject() instanceof org.opencrx.kernel.base.jmi1.SecureObject ? 
               		((org.opencrx.kernel.base.jmi1.SecureObject)this.sameObject()).getOwningGroup() : 
               		null
            );
            return Structures.create(
            	org.opencrx.kernel.base.jmi1.CloneResult.class, 
            	Datatypes.member(org.opencrx.kernel.base.jmi1.CloneResult.Member.clonedObject, clonedObject)
            );
        } catch(ServiceException e) {
            throw new JmiServiceException(e);
        }
    }
    
}

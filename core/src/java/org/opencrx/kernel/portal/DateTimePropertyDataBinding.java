/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: DateTimePropertyDataBinding.java,v 1.7 2012/01/06 13:22:51 wfro Exp $
 * Description: DateTimePropertyDataBinding
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/06 13:22:51 $
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
package org.opencrx.kernel.portal;

import java.util.Date;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefObject;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opencrx.kernel.base.jmi1.DateTimeProperty;
import org.opencrx.kernel.base.jmi1.Property;

public class DateTimePropertyDataBinding extends AbstractPropertyDataBinding {

    public DateTimePropertyDataBinding(
    ) {
        super(PropertySetHolderType.CrxObject);
    }
    
    public DateTimePropertyDataBinding(
        PropertySetHolderType type
    ) {
        super(type);
    }
            
    public Object getValue(
        RefObject object, 
        String qualifiedFeatureName
    ) {
        Property p = this.findProperty(object, qualifiedFeatureName);
        if(p instanceof DateTimeProperty) {
            return ((DateTimeProperty)p).getDateTimeValue();
        }
        else {
            return null;
        }
    }

    public void setValue(
        RefObject object, 
        String qualifiedFeatureName, 
        Object newValue
    ) {
        Property p = this.findProperty(object, qualifiedFeatureName);
        if(p == null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(object);
        	p = pm.newInstance(DateTimeProperty.class);
        	p.refInitialize(false, false);
            this.createProperty(
                object,
                qualifiedFeatureName,
                p
            );                
        }
        if(p instanceof DateTimeProperty) {
            if(newValue instanceof XMLGregorianCalendar) {
                ((DateTimeProperty)p).setDateTimeValue(((XMLGregorianCalendar)newValue).toGregorianCalendar().getTime());
            }
            else {
                ((DateTimeProperty)p).setDateTimeValue((Date)newValue);                
            }
        }
    }

}

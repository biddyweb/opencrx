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
package org.opencrx.kernel.home1.aop2;

import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class UserHomeImpl
	<S extends org.opencrx.kernel.home1.jmi1.UserHome,N extends org.opencrx.kernel.home1.cci2.UserHome,C extends Void>
	extends AbstractObject<S,N,C> {

    //-----------------------------------------------------------------------
    public UserHomeImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void refreshItems(
    ) {
        try {
            UserHomes.getInstance().refreshItems(
                this.sameObject()
            );
            return super.newVoid();            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.SearchResult searchBasic(
        org.opencrx.kernel.home1.jmi1.SearchBasicParams params
    ) {
        try {
            ObjectFinder objectFinder = UserHomes.getInstance().searchBasic(
                this.sameObject(),
                params.getSearchExpression()
            );
            return Utils.getHomePackage(this.sameManager()).createSearchResult(
                objectFinder
            );   
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }              
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.SearchResult searchAdvanced(
        org.opencrx.kernel.home1.jmi1.SearchAdvancedParams params
    ) {
        try {
            ObjectFinder objectFinder = UserHomes.getInstance().searchAdvanced(
                this.sameObject(),
                params.getAllWords(),
                params.getAtLeastOneOfTheWords(),
                params.getWithoutWords()
            );
            return Utils.getHomePackage(this.sameManager()).createSearchResult(
                objectFinder
            );   
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }              
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.ChangePasswordResult changePassword(
        org.opencrx.kernel.home1.jmi1.ChangePasswordParams params
    ) {
    	try {
	        short status = UserHomes.getInstance().changePassword(
	            this.sameObject(),
	            params.getOldPassword(),
	            params.getNewPassword(),
	            params.getNewPasswordVerification()
	        );
	        return Utils.getHomePackage(this.sameManager()).createChangePasswordResult(
	            status
	        );
    	}
    	catch(Exception e) {
    		throw new JmiServiceException(e);
    	}
    }
            
}

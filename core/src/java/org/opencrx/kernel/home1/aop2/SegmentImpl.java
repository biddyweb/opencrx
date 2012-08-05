/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SegmentImpl.java,v 1.6 2009/04/21 00:10:37 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 00:10:37 $
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

import java.util.ArrayList;
import java.util.List;

import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.backend.SecureObject;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;

public class SegmentImpl
	<S extends org.opencrx.kernel.home1.jmi1.Segment,N extends org.opencrx.kernel.home1.cci2.Segment,C extends Void>
	extends AbstractObject<S,N,C> {

    //-----------------------------------------------------------------------
    public SegmentImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.CreateUserResult createUser(
        org.opencrx.kernel.home1.jmi1.CreateUserParams params
    ) {
    	try {
	        String principalName = params.getPrincipalName();
	        Contact contact = params.getContact();
	        org.opencrx.security.realm1.jmi1.PrincipalGroup primaryUserGroup = params.getPrimaryUserGroup();
	        String initialPassword = params.getInitialPassword();
	        String initialPasswordVerification = params.getInitialPasswordVerification();
	        org.openmdx.security.realm1.jmi1.Realm realm = (org.openmdx.security.realm1.jmi1.Realm)this.sameManager().getObjectById(
	        	SecureObject.getRealmIdentity(
	        		this.sameObject().refGetPath().get(2), 
	        		this.sameObject().refGetPath().get(4)
	        	)
	        );
	        List<String> errors = new ArrayList<String>();
	        UserHome userHome = UserHomes.getInstance().createUserHome(
	            realm,
	            contact,
	            primaryUserGroup,
	            principalName,
	            null,
	            false,
	            initialPassword,
	            initialPasswordVerification,
	            errors,
	            this.sameManager().getPersistenceManagerFactory().getPersistenceManager(SecurityKeys.ROOT_PRINCIPAL, null)            
	        );
	        if((userHome == null) || !errors.isEmpty()) {
	            return Utils.getHomePackage(this.sameManager()).createCreateUserResult(
	                null,
	                (short)1,
	                errors.toString()
	            );
	        }
	        else {
	            return Utils.getHomePackage(this.sameManager()).createCreateUserResult(
	                userHome,
	                (short)0,
	                null
	            );
	        }
    	}
    	catch(Exception e) {
    		throw new JmiServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.ImportUsersResult importUsers(
        org.opencrx.kernel.home1.jmi1.ImportUsersParams params
    ) {
        try {
            String statusMessage = UserHomes.getInstance().importUsers(
                this.sameObject(),
                params.getItem()
            );            
            return Utils.getHomePackage(this.sameManager()).createImportUsersResult(
                statusMessage
            );
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }                
    }
    
}

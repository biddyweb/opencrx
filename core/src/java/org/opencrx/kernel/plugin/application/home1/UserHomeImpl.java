/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: UserHomeImpl.java,v 1.7 2008/11/06 01:14:13 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/06 01:14:13 $
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
package org.opencrx.kernel.plugin.application.home1;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.home1.jmi1.Home1Package;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;


public class UserHomeImpl {

    //-----------------------------------------------------------------------
    public UserHomeImpl(
        org.opencrx.kernel.home1.jmi1.UserHome current,
        org.opencrx.kernel.home1.cci2.UserHome next
    ) {
        this.current = current;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public Backend getBackend(
    ) {
        return (Backend)((RefPackage_1_3)this.current.refOutermostPackage()).refUserContext();
    }
        
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.ChangePasswordResult changePassword(
        org.opencrx.kernel.home1.jmi1.ChangePasswordParams params
    ) {
        String requestingPrincipalName = this.getBackend().getServiceHeader().getPrincipalChain().size() > 0
            ? (String)this.getBackend().getServiceHeader().getPrincipalChain().get(0)
            : "guest";
        // make sure that the requesting principal changes the password of its
        // own user home (qualifier of user home matches the principal). If yes,
        // execute changePassword as segment administrator. If not, execute it as
        // requesting principal. In this case the principal must have enough permissions
        // to create a password credential and update the principal.
        boolean requestingPrincipalOwnsUserHome = this.current.refGetPath().getBase().equals(requestingPrincipalName);
        short status = this.getBackend().getUserHomes().changePassword(
            this.current.refGetPath(),
            requestingPrincipalOwnsUserHome,
            params.getOldPassword(),
            params.getNewPassword(),
            params.getNewPasswordVerification()
        );
        return ((Home1Package)this.current.refOutermostPackage().refPackage(Home1Package.class.getName())).createChangePasswordResult(
            status
        );            
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.home1.jmi1.UserHome current;
    protected final org.opencrx.kernel.home1.cci2.UserHome next;    
    
}

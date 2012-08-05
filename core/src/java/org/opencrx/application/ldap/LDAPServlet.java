/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: LDAPServlet.java,v 1.2 2010/02/10 16:36:34 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/02/10 16:36:34 $
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
package org.opencrx.application.ldap;

import javax.jdo.PersistenceManagerFactory;

import org.opencrx.application.adapter.AbstractServer;
import org.opencrx.application.adapter.AbstractServlet;

public class LDAPServlet extends AbstractServlet {

	@Override
    public String getConfigurationId(
    ) {
		return "LDAPServer";
    }

	@Override
    public int getPortNumber(
    	String configuredPortNumber
    ) {
		return configuredPortNumber == null ? 
			389 : 
				(configuredPortNumber.startsWith("ldap:") ? 
					Integer.valueOf(configuredPortNumber.substring(5)) : 
						Integer.valueOf(configuredPortNumber));		
    }

	@Override
    public AbstractServer newServer(
        PersistenceManagerFactory pmf,
        String providerName,
        String bindAddress,
        int portNumber,
        String sslKeystoreFile,
        String sslKeystoreType,
        String sslKeystorePass,
        String sslKeyPass,
        boolean isDebug,
        int delayOnStartup
    ) {
		return new LDAPServer(
			pmf,
			providerName,
		    bindAddress,
		    portNumber,
		    sslKeystoreFile,
		    sslKeystoreType,
		    sslKeystorePass,
		    sslKeyPass,
			isDebug,
			delayOnStartup
		);
    }

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
    private static final long serialVersionUID = -4748080363157122023L;
	
}

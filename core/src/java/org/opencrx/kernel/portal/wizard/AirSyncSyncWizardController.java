/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: AirSyncSyncWizardController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2013, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.wizard;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.jmi1.AirSyncClientProfile;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ObjectReference;

/**
 * AirSyncSyncWizardController
 *
 */
public class AirSyncSyncWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public AirSyncSyncWizardController(
	) {
		super();
	}
	
	/**
	 * Cancel action.
	 * 
	 */
	public void doCancel(
	) {
		this.setExitAction(
			new ObjectReference(this.getObject(), this.getApp()).getSelectObjectAction()
		);		
	}
	
	/**
	 * OK action.
	 * 
	 * @throws ServiceException
	 */
	public void doOK(
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		RefObject_1_0 obj = this.getObject();
		if(obj instanceof AirSyncClientProfile) {
			AirSyncClientProfile syncProfile = (AirSyncClientProfile)obj;
		    try {
		    	org.opencrx.application.airsync.backend.cci.SyncBackend syncBackend =
		    		new org.opencrx.application.airsync.backend.impl.OpenCrxSyncBackend(
		    			pm.getPersistenceManagerFactory(),
		    			syncProfile.refGetPath().get(2)
		    		);
		    	org.opencrx.application.airsync.client.ClientHandler syncHandler =
		    		new org.opencrx.application.airsync.client.SyncHandler(syncBackend);
		    	syncHandler.handle(
		   			org.opencrx.application.airsync.utils.AirSyncUtils.newRemoteSyncTarget(
						syncProfile.getServerUrl(),
						syncProfile.getUsername(),
						syncProfile.getDomain(),
						syncProfile.getPassword(),
						syncProfile.refGetPath().getBase()
					),
		   			syncProfile.refGetPath().get(4) + org.opencrx.application.airsync.backend.cci.SyncBackend.DOMAIN_SEPARATOR + syncProfile.refGetPath().get(6),
		   			syncProfile.getName(),
		   			UserHomes.getInstance().getUserHome(syncProfile.refGetPath(), pm)
		    	);
		    } catch (Exception e) {
		        new ServiceException(e).log();
		    } finally {
		    }			
		}
		this.forward(
			"Cancel",
			this.getRequest().getParameterMap()
		);
	}
	
}

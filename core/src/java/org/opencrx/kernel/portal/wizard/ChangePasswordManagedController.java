/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: ChangePasswordManagedController
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

import org.opencrx.kernel.home1.jmi1.ChangePasswordParams;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * ChangePasswordManagedController
 *
 */
public class ChangePasswordManagedController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public ChangePasswordManagedController(
	) {
		super();
	}
	
	/**
	 * Refresh action.
	 * 
	 * @param pwOld
	 * @param pwNew1
	 * @param pwNew2
	 * @param showPasswords
	 * @throws ServiceException
	 */
	public void doRefresh(
		@RequestParameter(name = "pw_old") String pwOld,
		@RequestParameter(name = "pw_new1") String pwNew1,
		@RequestParameter(name = "pw_new2") String pwNew2,
		@RequestParameter(name = "showPasswords") Boolean showPasswords
	) throws ServiceException {
		this.showPasswords = showPasswords;
		this.pwOld = pwOld;
		this.pwNew1 = pwNew1;
		this.pwNew2 = pwNew2;
		if(this.getObject() instanceof UserHome) {
			UserHome userHome =  (UserHome)this.getObject();
			this.principalName = userHome.refGetPath().getBase();
		}
	}

	/**
	 * OK action.
	 * 
	 * @param pwOld
	 * @param pwNew1
	 * @param pwNew2
	 * @param showPasswords
	 * @throws ServiceException
	 */
	public void doOK(
		@RequestParameter(name = "pw_old") String pwOld,
		@RequestParameter(name = "pw_new1") String pwNew1,
		@RequestParameter(name = "pw_new2") String pwNew2,
		@RequestParameter(name = "showPasswords") Boolean showPasswords
	) throws ServiceException {
		ApplicationContext app = this.getApp();
		PersistenceManager pm = this.getPm();
		this.doRefresh(
			pwOld,
			pwNew1,
			pwNew2,
			showPasswords
		);
		org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension wizardExtension = 
			(org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension)app.getPortalExtension().getExtension(org.opencrx.kernel.portal.wizard.ChangePasswordWizardExtension.class.getName());
		if(this.getObject() instanceof UserHome) {
			try {
				UserHome userHome =  (UserHome)this.getObject();				
				wizardExtension.validatePassword(pwNew1, this.principalName, app);
				try {
					ChangePasswordParams params = Structures.create(
						ChangePasswordParams.class,
						Datatypes.member(ChangePasswordParams.Member.newPassword, pwNew1),
						Datatypes.member(ChangePasswordParams.Member.newPasswordVerification, pwNew2),
						Datatypes.member(ChangePasswordParams.Member.oldPassword, pwOld)
					);
					pm.currentTransaction().begin();
					org.opencrx.kernel.home1.jmi1.ChangePasswordResult result = userHome.changePassword(params);
					pm.currentTransaction().commit();
					short resultCode = result.getStatus();
					try {
						this.resultText = "[" + resultCode + "]: " + (String)(this.getCodes().getLongText("org:opencrx:kernel:home1:ChangePasswordResult:status", app.getCurrentLocaleAsIndex(), true, true).get(new Short(resultCode)));
					} catch (Exception e) {
						this.errorMsg = "unknown error";
					}
					if(resultCode > 0) {
						this.errorMsg = resultText;
						this.resultText = null;
						if(this.errorMsg == null || this.errorMsg.isEmpty()) {
							this.errorMsg = "unknown error";
						}
					}
				} catch(Exception e) {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e0) {}
					new ServiceException(e).log();
				}
			} catch(Exception e) {
				this.errorMsg = e.getMessage();
			}
		}
	}

	/**
	 * Cancel action.
	 * 
	 * @throws ServiceException
	 */
	public void doCancel(
	) throws ServiceException {
		this.setExitAction(
			new ObjectReference(this.getObject(), this.getApp()).getSelectObjectAction()
		);
	}

	/**
	 * @return the showPasswords
	 */
	public Boolean getShowPasswords(
	) {
		return this.showPasswords;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg(
	) {
		return this.errorMsg;
	}

	/**
	 * @return the resultText
	 */
	public String getResultText(
	) {
		return this.resultText;
	}

	/**
	 * @return the pwOld
	 */
	public String getPwOld(
	) {
		return this.pwOld;
	}

	/**
	 * @return the pwNew1
	 */
	public String getPwNew1(
	) {
		return this.pwNew1;
	}

	/**
	 * @return the pwNew2
	 */
	public String getPwNew2(
	) {
		return this.pwNew2;
	}
	
	/**
	 * @return the principalName
	 */
	public String getPrincipalName(
	) {
		return this.principalName;
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	private Boolean showPasswords;
	private String errorMsg = "";
	private String resultText = null;
	private String pwOld;
	private String pwNew1;
	private String pwNew2;
	private String principalName;
	
}

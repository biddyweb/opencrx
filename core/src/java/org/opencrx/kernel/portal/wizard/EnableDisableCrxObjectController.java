/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: DisableCrxObjectController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2013, CRIXP Corp., Switzerland
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

import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;

/**
 * DisableCrxObjectController
 *
 */
public abstract class EnableDisableCrxObjectController extends AbstractWizardController {

	/**
	 * Counter
	 *
	 */
	public static class Counter {

		public Counter(
			int initialValue
		) {
			this.counter = initialValue;
		}
		
		public void increment(
		) {
			this.counter++;
		}
		
		public int getValue(
		) {
			return this.counter;
		}
		
		private int counter;

	}

	/**
	 * Constructor.
	 */
	public EnableDisableCrxObjectController(
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
	 * Refresh action.
	 * 
	 * @param disable
	 */
	public void doRefresh(
		@RequestParameter(name = "disable") Boolean disable		
	) {
		this.disable = Boolean.TRUE.equals(disable);
		this.errorMsg = "";
		this.errorTitle = "";
	}
	
	/**
	 * OK action.
	 * 
	 * @param disable
	 */
	public void doOK(
		@RequestParameter(name = "disable") Boolean disable		
	) {
		PersistenceManager pm = this.getPm();
		ApplicationContext app = this.getApp();
		this.doRefresh(disable);
		if(this.getObject() instanceof CrxObject) {
			try {
				final String disabledReason = app.getCurrentUserRole();
				CrxObject crxObject = (org.opencrx.kernel.generic.jmi1.CrxObject)this.getObject();
				pm.currentTransaction().begin();
				crxObject.setDisabled(this.disable);		
				crxObject.setDisabledReason(disabledReason);
				SysLog.detail((this.disable ? "Disabling " : "Enabling ") + crxObject.refGetPath());
				// Disable/enable composites			
				Utils.traverseObjectTree(
					crxObject,
					null, // referenceFilter
					new Utils.TraverseObjectTreeCallback() {
						// @Override
						public Object visit(
							RefObject_1_0 object,
							Object context
						) throws ServiceException {
							if(context instanceof Counter) {
								Counter counter = (Counter)context;
								counter.increment();
								// Avoid transaction timeout. Commit every 100 modified objects
								if(counter.getValue() % 100 == 0) {
									javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(object);
									pm.currentTransaction().commit();
									pm.currentTransaction().begin();
								}
							}
							if (object instanceof CrxObject) {
								((CrxObject)object).setDisabled(EnableDisableCrxObjectController.this.disable);
								((CrxObject)object).setDisabledReason(disabledReason);
								SysLog.detail((EnableDisableCrxObjectController.this.disable ? "Disabling: " : "Enabling: ") + object.refGetPath());
							}
							return context;
						}
					},
					new Counter(0) // context
				);
				pm.currentTransaction().commit();
			} catch(Exception e) {
				new ServiceException(e).log();
				this.errorMsg = "ERROR - cannot " + (this.disable ? "disable" : "enable") + " object";
				Throwable err = e;
				while (err.getCause() != null) {
					err = err.getCause();
				}
				this.errorTitle += "<pre>" + err + "</pre>";
				try {
					pm.currentTransaction().rollback();
				} catch (Exception ignore) {}
			}
		}
	}

	/**
	 * @return the disable
	 */
	public boolean isDisable() {
		return disable;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @return the errorTitle
	 */
	public String getErrorTitle() {
		return errorTitle;
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------		
	private boolean disable;
	private String errorMsg;
	private String errorTitle;
}

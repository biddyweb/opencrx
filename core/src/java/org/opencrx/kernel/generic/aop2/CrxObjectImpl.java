/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CrxObjectImpl
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
package org.opencrx.kernel.generic.aop2;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.generic.jmi1.EnableDisableCrxObjectParams;
import org.opencrx.kernel.generic.jmi1.EnableDisableCrxObjectResult;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.persistence.cci.UserObjects;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * CrxObjectImpl
 *
 * @param <S>
 * @param <N>
 * @param <C>
 */
public class CrxObjectImpl
	<S extends org.opencrx.kernel.generic.jmi1.CrxObject,N extends org.opencrx.kernel.generic.cci2.CrxObject,C extends Void>
	extends AbstractObject<S,N,C> {

    /**
     * Constructor.
     * 
     * @param same
     * @param next
     */
    public CrxObjectImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

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
	 * Traverse recursively composites and disable / enable.
	 * 
	 * @param rootObj
	 * @param disable
	 * @param reason
	 * @param counter
	 * @return
	 */
	protected short traverse(
		final CrxObject rootObj,
		final boolean disable,
		final String reason,
		final Counter counter
	) {
		short status = 0;
		try {
			Utils.traverseObjectTree(
				rootObj,
				null, // referenceFilter
				new Utils.TraverseObjectTreeCallback() {
					@Override
					public Object visit(
						RefObject_1_0 object,
						Object context
					) throws ServiceException {
						if(object instanceof CrxObject) {
							CrxObject crxObject = (CrxObject)object;
							if(disable) {
								if(!Boolean.TRUE.equals(crxObject.isDisabled())) {								
									crxObject.setDisabled(true);
									crxObject.setDisabledReason(reason);
									if(context instanceof Counter) {
										((Counter)context).increment();
									}
								}
							} else {
								if(Boolean.TRUE.equals(crxObject.isDisabled())) {
									crxObject.setDisabled(false);									
									if(context instanceof Counter) {
										((Counter)context).increment();
									}
								}
							}
						}
						if(counter.getValue() % 100 == 0) {
							PersistenceManager pm = JDOHelper.getPersistenceManager(object);
							pm.currentTransaction().commit();
							pm.currentTransaction().begin();
						}
						return context;
					}
				},
				counter
			);
		} catch(Exception e) {
			status = -1;
		}
		return status;
	}

    /**
     * Disable CrxObject.
     * 
     * @param in
     * @return
     */
    public EnableDisableCrxObjectResult disableCrxObject(
    	EnableDisableCrxObjectParams in
    ) {
    	// Separate pm allows batching
		PersistenceManager pm = this.sameManager().getPersistenceManagerFactory().getPersistenceManager(
			UserObjects.getPrincipalChain(this.sameManager()).toString(),
			null
		);
		Counter counter = new Counter(0);
		short status = 0;
		try {
			pm.currentTransaction().begin();
			CrxObject rootObj = (CrxObject)pm.getObjectById(this.sameObject().refGetPath());		
			if(in.getMode() == 0) {
				if(!Boolean.TRUE.equals(rootObj.isDisabled())) {
					rootObj.setDisabled(true);
					rootObj.setDisabledReason(in.getReason());
					counter.increment();
				}
			} else {
				status = this.traverse(
					(CrxObject)pm.getObjectById(this.sameObject().refGetPath()), 
					true, 
					in.getReason(),
					counter
				);
			}
			pm.currentTransaction().commit();
		} catch(Exception e) {
			status = -1;
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
		pm.close();
        return Structures.create(
        	EnableDisableCrxObjectResult.class, 
        	Datatypes.member(EnableDisableCrxObjectResult.Member.status, status),
        	Datatypes.member(EnableDisableCrxObjectResult.Member.count, counter.getValue())
        );
    }

    /**
     * Enable CrxObject.
     * 
     * @param in
     * @return
     */
    public EnableDisableCrxObjectResult enableCrxObject(
    	EnableDisableCrxObjectParams in
    ) {
    	// Separate pm allows batching
		PersistenceManager pm = this.sameManager().getPersistenceManagerFactory().getPersistenceManager(
			UserObjects.getPrincipalChain(this.sameManager()).toString(),
			null
		);
		Counter counter = new Counter(0);
		short status = 0;
		try {
			pm.currentTransaction().begin();
			CrxObject object = (CrxObject)pm.getObjectById(this.sameObject().refGetPath());		
			if(in.getMode() == 0) {
				if(Boolean.TRUE.equals(object.isDisabled())) {
					object.setDisabled(false);
					counter.increment();
				}
			} else {
				status = this.traverse(
					object, 
					false, 
					null, 
					counter
				);
			}
			pm.currentTransaction().commit();
		} catch(Exception e) {
			status = -1;
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}	
		pm.close();
        return Structures.create(
        	EnableDisableCrxObjectResult.class, 
        	Datatypes.member(EnableDisableCrxObjectResult.Member.status, status),
        	Datatypes.member(EnableDisableCrxObjectResult.Member.count, counter.getValue())
        );
    }

}

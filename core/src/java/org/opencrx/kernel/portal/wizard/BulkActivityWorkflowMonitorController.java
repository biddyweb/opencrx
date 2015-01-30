/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BulkMonitorController
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.dataprovider.layer.persistence.jdbc.spi.Database_1_Attributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.rest.cci.QueryExtensionRecord;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ObjectReference;

/**
 * BulkMonitorController
 *
 */
public class BulkActivityWorkflowMonitorController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public BulkActivityWorkflowMonitorController(
	) {
		super();
	}

	/**
	 * ProcessState
	 *
	 */
	public enum ProcessState {
	    NA,
		PENDING_NOTYETSTARTED,
		PENDING_STARTED,
		COMPLETED_SUCCESS,
		COMPLETED_FAILURE
	}

	/**
	 * Get last element of name.
	 * 
	 * @param name
	 * @return
	 */
	public String getLastElementOfName(
		String name
	) {
		String result = "";
		if (name != null) {
			String[] splittedName = name.split("\\.");
			if (splittedName.length > 0) {
				result = splittedName[splittedName.length-1];
			}
		}
		//System.out.println(name + "  -->  " + result);
		return result;
	}

	/**
	 * Get workflow process state.
	 * 
	 * @param wfProcessInstance
	 * @return
	 */
	public ProcessState getState(
    	org.opencrx.kernel.home1.jmi1.WfProcessInstance wfProcessInstance
    ) {
    	ProcessState pstate = ProcessState.NA;
    	if (wfProcessInstance != null) {
    		if (wfProcessInstance.getLastActivityOn() == null) {
    			pstate = ProcessState.PENDING_NOTYETSTARTED;
    		} else  if (wfProcessInstance.getStartedOn() == null){
    			pstate = ProcessState.PENDING_STARTED;
    		} else if (wfProcessInstance.isFailed() != null && wfProcessInstance.isFailed().booleanValue()) {
    			pstate = ProcessState.COMPLETED_FAILURE;
    		} else {
    			pstate = ProcessState.COMPLETED_SUCCESS;
    		}
    	}
    	return pstate;
    }

    /**
     * Get sort key for given workflow.
     * 
     * @param wfProcessInstance
     * @return
     */
    public String getWfProcessInstanceSortKey(
    	org.opencrx.kernel.home1.jmi1.WfProcessInstance wfProcessInstance
    ) {
    	// Sort Ordering:
    	// * lastActivity DESC
    	// * startedOn    DESC
    	// * createdAt    DESC
    	
    	NumberFormat formatter = new DecimalFormat("00000000000000000000");

    	String maxkey = formatter.format(Long.MAX_VALUE) + formatter.format(Long.MAX_VALUE) + formatter.format(Long.MAX_VALUE);
    	String minkey = formatter.format(0) + formatter.format(0) + formatter.format(0);
    	String key = maxkey + maxkey + maxkey;
		if (wfProcessInstance != null) {
			key = (wfProcessInstance.getLastActivityOn() == null ? minkey : formatter.format(Long.MAX_VALUE - wfProcessInstance.getLastActivityOn().getTime())) +
					(wfProcessInstance.getStartedOn()    == null ? minkey : formatter.format(Long.MAX_VALUE - wfProcessInstance.getStartedOn().getTime())) +
					(wfProcessInstance.getCreatedAt()    == null ? minkey : formatter.format(Long.MAX_VALUE - wfProcessInstance.getCreatedAt().getTime()));
		}
    	return key;
    }
	
    /**
     * Get number of failed child processes.
     * 
     * @param wfProcessInstance
     * @return
     */
    public int getCountChildrenFailed(
    	WfProcessInstance wfProcessInstance
    ) {
    	PersistenceManager pm = this.getPm();
		org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery query = (org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.WfProcessInstance.class);
		query.startedOn().isNonNull();
		query.forAllFailed().isTrue();
		QueryExtensionRecord queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);		
		return wfProcessInstance.getChildProcessInstance(query).size();    	
    }
    
    /**
     * Get number of pending child processes.
     * 
     * @param wfProcessInstance
     * @return
     */
    public int getCountChildrenPending(
    	WfProcessInstance wfProcessInstance
    ) {
    	PersistenceManager pm = this.getPm();
		org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery query = (org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.WfProcessInstance.class);
		query.startedOn().isNull();
		QueryExtensionRecord queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);		
		return wfProcessInstance.getChildProcessInstance(query).size();    	
    }

    /**
     * Get number of successful child processes.
     * 
     * @param wfProcessInstance
     * @return
     */
    public int getCountChildrenSuccess(
    	WfProcessInstance wfProcessInstance
    ) {
    	PersistenceManager pm = this.getPm();
		org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery query = (org.opencrx.kernel.home1.cci2.WfProcessInstanceQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.WfProcessInstance.class);
		query.startedOn().isNonNull();
		query.forAllFailed().isFalse();
		QueryExtensionRecord queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);		
		return wfProcessInstance.getChildProcessInstance(query).size();    	
    }

	/**
	 * Refresh action.
	 * 
	 * @throws ServiceException
	 */
	public void doRefresh(
	) throws ServiceException {
		RefObject_1_0 obj = this.getObject();
		PersistenceManager pm = this.getPm();
		String providerName = obj.refGetPath().get(2);
		String segmentName = obj.refGetPath().get(4);
		ActivityTracker activityTracker = (ActivityTracker)obj;
		org.opencrx.kernel.home1.jmi1.Segment homeSegment =
			(org.opencrx.kernel.home1.jmi1.Segment)pm.getObjectById(
				new Path("xri:@openmdx:org.opencrx.kernel.home1").getDescendant("provider", providerName, "segment", segmentName)
			);
		this.processInstances = new TreeMap<String,WfProcessInstance>();
    	try {
    		WfProcessInstanceQuery query = (WfProcessInstanceQuery)PersistenceHelper.newQuery(
	    		pm.getExtent(org.opencrx.kernel.home1.jmi1.WfProcessInstance.class),
	    		homeSegment.refGetPath().getDescendant("userHome", ":*", "wfProcessInstance", ":*")
	    	);
	    	query.thereExistsTargetObject().equalTo(activityTracker.getIdentity());
	    	for(WfProcessInstance wfProcessInstance: homeSegment.<WfProcessInstance>getExtent(query)) {
	    		this.processInstances.put(
	    			getWfProcessInstanceSortKey(wfProcessInstance), 
	    			wfProcessInstance
	    		);
	    	}
    	} catch(Exception e) {
    		new ServiceException(e).log();
    	}
		ActivityCreatorQuery activityCreatorQuery = (ActivityCreatorQuery)pm.newQuery(ActivityCreator.class);
		for(ActivityCreator activityCreator: activityTracker.getActivityCreator(activityCreatorQuery)) {
			try {
	    		WfProcessInstanceQuery query = (WfProcessInstanceQuery)PersistenceHelper.newQuery(
		    		pm.getExtent(org.opencrx.kernel.home1.jmi1.WfProcessInstance.class),
		    		homeSegment.refGetPath().getDescendant("userHome", ":*", "wfProcessInstance", ":*")
		    	);
		    	query.thereExistsTargetObject().equalTo(activityCreator.getIdentity());
		    	for(WfProcessInstance wfProcessInstance: homeSegment.<org.opencrx.kernel.home1.jmi1.WfProcessInstance>getExtent(query)) {
		    		this.processInstances.put(
		    			getWfProcessInstanceSortKey(wfProcessInstance), 
		    			wfProcessInstance
		    		);
		    	}
	    	} catch(Exception e) {
	    		new ServiceException(e).log();
	    	}
		}
	}

	/**
	 * Cancel action.
	 * 
	 * @throws ServiceException
	 */
	public void doClose(
	) throws ServiceException {
		this.setExitAction(
			new ObjectReference(this.getObject(), this.getApp()).getSelectObjectAction()
		);
	}
	
	/**
	 * @return the wfProcessInstances
	 */
	public Map<String, WfProcessInstance> getProcessInstances(
	) {
		return this.processInstances;
	}
	
	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	public static final String ACTIVITYTRACKER_CLASS = "org:opencrx:kernel:activity1:ActivityTracker";
	public static final String WFPROCESSINSTANCE_CLASS = "org:opencrx:kernel:home1:WfProcessInstance";
	
	private Map<String,WfProcessInstance> processInstances;

}

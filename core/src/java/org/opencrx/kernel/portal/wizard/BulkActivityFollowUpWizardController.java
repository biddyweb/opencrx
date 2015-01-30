/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BulkActivityFollowUpWizardController
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessState;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.base.jmi1.PropertySet;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.workflow.BulkActivityFollowUpWorkflow;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.dataprovider.layer.persistence.jdbc.spi.Database_1_Attributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.rest.cci.QueryExtensionRecord;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;

/**
 * BulkActivityFollowUpWizardController
 *
 */
public class BulkActivityFollowUpWizardController extends AbstractWizardController {

	/**
	 * FormFields
	 *
	 */
	public static class FormFields {
	
		/**
		 * @return the processTransitionXri0
		 */
		public String getProcessTransitionXri0() {
			return processTransitionXri0;
		}
		/**
		 * @param processTransitionXri0 the processTransitionXri0 to set
		 */
		public void setProcessTransitionXri0(String processTransitionXri0) {
			this.processTransitionXri0 = processTransitionXri0;
		}
		/**
		 * @return the followUpTitle1
		 */
		public String getFollowUpTitle0() {
			return followUpTitle0;
		}
		/**
		 * @param followUpTitle0 the followUpTitle0 to set
		 */
		public void setFollowUpTitle0(String followUpTitle0) {
			this.followUpTitle0 = followUpTitle0;
		}
		/**
		 * @return the followUpText0
		 */
		public String getFollowUpText0() {
			return followUpText0;
		}
		/**
		 * @param followUpText0 the followUpText0 to set
		 */
		public void setFollowUpText0(String followUpText0) {
			this.followUpText0 = followUpText0;
		}
		/**
		 * @return the processTransitionXri1
		 */
		public String getProcessTransitionXri1() {
			return processTransitionXri1;
		}
		/**
		 * @param processTransitionXri1 the processTransitionXri1 to set
		 */
		public void setProcessTransitionXri1(String processTransitionXri1) {
			this.processTransitionXri1 = processTransitionXri1;
		}
		/**
		 * @return the followUpTitle1
		 */
		public String getFollowUpTitle1() {
			return followUpTitle1;
		}
		/**
		 * @param followUpTitle1 the followUpTitle1 to set
		 */
		public void setFollowUpTitle1(String followUpTitle1) {
			this.followUpTitle1 = followUpTitle1;
		}
		/**
		 * @return the followUpText1
		 */
		public String getFollowUpText1() {
			return followUpText1;
		}
		/**
		 * @param followUpText1 the followUpText1 to set
		 */
		public void setFollowUpText1(String followUpText1) {
			this.followUpText1 = followUpText1;
		}
		/**
		 * @return the timerName
		 */
		public String getTimerName() {
			return timerName;
		}
		/**
		 * @param timerName the timerName to set
		 */
		public void setTimerName(String timerName) {
			this.timerName = timerName;
		}
		/**
		 * @return the useTimer
		 */
		public Boolean getUseTimer() {
			return useTimer;
		}
		/**
		 * @param useTimer the useTimer to set
		 */
		public void setUseTimer(Boolean useTimer) {
			this.useTimer = useTimer;
		}
		/**
		 * @return the activityGroupXri
		 */
		public String getActivityGroupXri() {
			return activityGroupXri;
		}
		/**
		 * @param activityGroupXri the activityGroupXri to set
		 */
		public void setActivityGroupXri(String activityGroupXri) {
			this.activityGroupXri = activityGroupXri;
		}
		/**
		 * @return the triggerAt
		 */
		public String getTriggerAt() {
			return triggerAt;
		}
		/**
		 * @param triggerAt the triggerAt to set
		 */
		public void setTriggerAt(String triggerAt) {
			this.triggerAt = triggerAt;
		}
		/**
		 * @return the assignToXri
		 */
		public String getAssignToXri() {
			return this.assignToXri;
		}
		/**
		 * @param assignToXri the assignToXri to set
		 */
		public void setAssignToXri(String assignToXri) {
			this.assignToXri = assignToXri;
		}

		private String processTransitionXri0;
		private String followUpTitle0;
		private String followUpText0;
		private String processTransitionXri1;
		private String followUpTitle1;
		private String followUpText1;
		private String timerName;		
		private Boolean useTimer;		
		private String activityGroupXri;
		private String triggerAt;
		private String assignToXri;
	}

	/**
	 * Constructor.
	 *  
	 */
	public BulkActivityFollowUpWizardController(
	) {
		super();
	}
	
	/**
	 * Get localized Calendar.
	 * 
	 * @param app
	 * @return
	 */
	public GregorianCalendar getLocalizedCalendar(
	) {
		GregorianCalendar cal = new GregorianCalendar(this.getApp().getCurrentLocale());
		cal.setTimeZone(TimeZone.getTimeZone(this.getApp().getCurrentTimeZone()));
		cal.setMinimalDaysInFirstWeek(4); // this conforms to DIN 1355/ISO 8601
		cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		return cal;
	}

	/**
	 * Get selectable process transitions for process state.
	 * 
	 * @param activityProcess
	 * @param processState
	 * @return
	 * @throws ServiceException
	 */
	public List<ActivityProcessTransition> getSelectableProcessTransitions(
		ActivityProcess activityProcess,
		ActivityProcessState processState
	) throws ServiceException {
		List<ActivityProcessTransition> selectableProcessTransitions = new ArrayList<ActivityProcessTransition>();
        if(processState != null && activityProcess != null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(activityProcess);
            ActivityProcessTransitionQuery transitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
            transitionQuery.orderByNewPercentComplete().ascending();
            List<ActivityProcessTransition> transitions = activityProcess.getTransition(transitionQuery);
            for(ActivityProcessTransition transition: transitions) {
                if(transition.getPrevState().equals(processState)) {
                	selectableProcessTransitions.add(transition);
                }
            }
        }
        return selectableProcessTransitions;
	}

	/**
	 * Get exit action after doOK was processed successfully.
	 * 
	 * @param targetObject
	 * @return
	 */
	protected Action getAfterOKExitAction(
		RefObject_1_0 targetObject
	) {
		return new ObjectReference(
			targetObject, 
			this.getApp()
		).getSelectObjectAction();
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.AbstractWizardController#init(javax.servlet.http.HttpServletRequest, java.lang.String, boolean, boolean)
	 */
    @Override
    public boolean init(
    	HttpServletRequest request, 
    	String encoding, 
    	boolean assertRequestId, 
    	boolean assertObjectXri
    ) {
	    if(!super.init(request, encoding, assertRequestId, assertObjectXri)) {
	    	return false;
	    } else {	    	
	    	org.openmdx.base.persistence.cci.UserObjects.setBulkLoad(this.getPm(), true);
	    	return true;
	    }
    }

	/**
	 * OK action.
	 * 
	 * @param timerName
	 * @param activityGroupXri
	 * @param triggerAt
	 * @param useTimer
	 * @throws ServiceException
	 */
	public void doOK(
		@RequestParameter(type = "Bean") FormFields formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		this.doRefresh(formFields);
		final GregorianCalendar NOW = this.getLocalizedCalendar();
		if(this.activity != null) {
			List<ActivityProcessTransition> processTransitions = new ArrayList<ActivityProcessTransition>();
			List<String> followUpTitles = new ArrayList<String>();
			List<String> followUpTexts = new ArrayList<String>();
			List<String> transitionNames = new ArrayList<String>();
			// Transition 1
			if(
				this.formFields.getProcessTransitionXri0() != null && 
				!this.formFields.getProcessTransitionXri0().isEmpty()
			) {
				ActivityProcessTransition processTransition = null;
				processTransitions.add(
					processTransition = (ActivityProcessTransition)pm.getObjectById(
						new Path(this.formFields.getProcessTransitionXri0())
					)
				);
				transitionNames.add(processTransition.getName());
				followUpTitles.add(this.formFields.getFollowUpTitle0());
				followUpTexts.add(this.formFields.getFollowUpText0());
				// Transition 2
				if(
					this.formFields.getProcessTransitionXri1() != null && 
					!this.formFields.getProcessTransitionXri1().isEmpty()
				) {
					processTransitions.add(
						processTransition = (ActivityProcessTransition)pm.getObjectById(
							new Path(this.formFields.getProcessTransitionXri1())
						)
					);
					transitionNames.add(processTransition.getName());
					followUpTitles.add(this.formFields.getFollowUpTitle1());
					followUpTexts.add(this.formFields.getFollowUpText1());
				}
			}
			if(
				this.activityGroup != null && 
				!processTransitions.isEmpty()
			) {
    	    	try {
			    	UserHome currentUserHome = UserHomes.getInstance().getUserHome(this.getObjectIdentity(), pm, true);		  
					org.opencrx.kernel.workflow1.jmi1.Segment workflowSegment = Workflows.getInstance().getWorkflowSegment(pm, this.getProviderName(), this.getSegmentName());
					WfProcess wfProcess = Workflows.getInstance().findWfProcess(org.opencrx.kernel.backend.Workflows.WORKFLOW_NAME_BULK_ACTIVITY_FOLLOWUP, workflowSegment);
	  				pm.currentTransaction().begin();
	  				PropertySet executionTarget = null;
	    	    	// Asynchronous execution, i.e. create timer
					if(
		    	    	Boolean.TRUE.equals(this.formFields.getUseTimer()) &&
						this.triggerAtDate != null &&
						this.triggerAtDate.compareTo(NOW) > 0
					) {
						org.opencrx.kernel.home1.jmi1.Timer timer = pm.newInstance(org.opencrx.kernel.home1.jmi1.Timer.class);
						timer.setName(
							(this.timerName == null || this.timerName.isEmpty() ? "" : this.timerName + ": ") +
							this.activityGroup.getName() + " / " + transitionNames + " / " + currentUserHome.refGetPath().getBase() 
						);
						timer.setTimerStartAt(this.triggerAtDate.getTime());
						try {
							if (wfProcess != null) {
								timer.getAction().add(wfProcess);
							}
						} catch (Exception e) {
							new ServiceException(e).log();
						}
						timer.setTriggerRepeat(1);
						timer.setTriggerIntervalMinutes(5); /* note that this value MUST be bigger than the ping interval of the subscription handler */
						timer.setDisabled(false);
						timer.setTimerState(new Short((short)10)); // open
						GregorianCalendar endAt = (GregorianCalendar)this.triggerAtDate.clone();
						endAt.add(Calendar.MINUTE, 60);
						timer.setTimerEndAt(endAt.getTime());
						timer.setTarget(this.activityGroup);
						currentUserHome.addTimer(
							Base.getInstance().getUidAsString(),
							timer
						);
						executionTarget = timer;
					} else {
						// Create workflow instance
						executionTarget = Workflows.getInstance().executeWorkflow(
							this.activityGroup.getName() + " / " + transitionNames + " / " + currentUserHome.refGetPath().getBase(),
							currentUserHome,
							wfProcess,
							this.activityGroup,
							null, // booleanParams
							null, // stringParams
							null, // integerParams 
							null, // decimalParams
							null, // dateTimeParams
							null, // uriParams
							null // parentProcessInstance
						);
					}
					// Set BulkActivityFollowUpWorkflow parameters
					{
						org.opencrx.kernel.base.jmi1.ReferenceProperty referenceProperty = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
						referenceProperty.setName(BulkActivityFollowUpWorkflow.OPTION_ACTIVITY);
						referenceProperty.setReferenceValue(this.activity);
						executionTarget.addProperty(
							Base.getInstance().getUidAsString(),
							referenceProperty
						);
					}
					Contact assignTo = this.formFields.getAssignToXri() != null && !this.formFields.getAssignToXri().isEmpty() 
						? (Contact)pm.getObjectById(new Path(this.formFields.getAssignToXri()))
						: null;
					if(assignTo != null) {
						org.opencrx.kernel.base.jmi1.ReferenceProperty referenceProperty = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
						referenceProperty.setName(BulkActivityFollowUpWorkflow.OPTION_ASSIGN_TO);
						referenceProperty.setReferenceValue(assignTo);
						executionTarget.addProperty(
							Base.getInstance().getUidAsString(),
							referenceProperty
						);
					}
					for(int i = 0; i < processTransitions.size(); i++) {
						{
							org.opencrx.kernel.base.jmi1.ReferenceProperty referenceProperty = pm.newInstance(org.opencrx.kernel.base.jmi1.ReferenceProperty.class);
							referenceProperty.setName(BulkActivityFollowUpWorkflow.OPTION_TRANSITION + i);
							referenceProperty.setReferenceValue(processTransitions.get(i));
							executionTarget.addProperty(
								Base.getInstance().getUidAsString(),
								referenceProperty
							);
						}
						{
							org.opencrx.kernel.base.jmi1.StringProperty stringProperty = pm.newInstance(org.opencrx.kernel.base.jmi1.StringProperty.class);
							stringProperty.setName(BulkActivityFollowUpWorkflow.OPTION_FOLLOWUP_TITLE + i);
							stringProperty.setStringValue(followUpTitles.get(i));
							executionTarget.addProperty(
								Base.getInstance().getUidAsString(),
								stringProperty
							);
						}
						{
							org.opencrx.kernel.base.jmi1.StringProperty stringProperty = pm.newInstance(org.opencrx.kernel.base.jmi1.StringProperty.class);
							stringProperty.setName(BulkActivityFollowUpWorkflow.OPTION_FOLLOWUP_TEXT + i);
							stringProperty.setStringValue(followUpTexts.get(i));
							executionTarget.addProperty(
								Base.getInstance().getUidAsString(),
								stringProperty
							);
						}
					}
					pm.currentTransaction().commit();
					Action exitAction = this.getAfterOKExitAction(this.activityGroup);
					if(exitAction != null) {
						this.setExitAction(exitAction);
					}
				} catch(Exception e) {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e1) {}
					new ServiceException(e).log();
				}
    	    }
		}
	}

	/**
	 * Refresh action.
	 * 
	 */
	public void doRefresh(
		@RequestParameter(type = "Bean") FormFields formFields
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		ApplicationContext app = this.getApp();
		this.formFields = formFields;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", app.getCurrentLocale());
		GregorianCalendar proposedTriggerAt = this.getLocalizedCalendar();
		proposedTriggerAt.add(Calendar.MINUTE, 15);
		this.formFields.setTriggerAt(
			this.formFields.getTriggerAt() == null 
				? dateFormat.format(proposedTriggerAt.getTime()) 
				: this.formFields.getTriggerAt()
		);
		this.timerName = this.formFields.getTimerName(); 
		this.triggerAtDate = null;
		try {
			if ((this.formFields.getTriggerAt() != null) && (this.formFields.getTriggerAt().length() == 16)) {
				this.triggerAtDate = this.getLocalizedCalendar();
				this.triggerAtDate.set(Calendar.YEAR, Integer.parseInt(this.formFields.getTriggerAt().substring(6,10)));
				this.triggerAtDate.set(Calendar.MONTH, Integer.parseInt(this.formFields.getTriggerAt().substring(3,5))-1);
				this.triggerAtDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(this.formFields.getTriggerAt().substring(0,2)));
				this.triggerAtDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(this.formFields.getTriggerAt().substring(11,13)));
				this.triggerAtDate.set(Calendar.MINUTE, Integer.parseInt(this.formFields.getTriggerAt().substring(14,16)));
			}
		} catch (Exception ignore) {}
		if(this.getObject() instanceof Activity) {
			this.activity = (Activity)this.getObject();
			// Get identity of activities to be updated
			this.activityCount = 0;
			if(this.formFields.getActivityGroupXri() == null || this.formFields.getActivityGroupXri().isEmpty()) {
				// Derive main tracker from activity group assignments
				List<ActivityGroup> activityGroups = new ArrayList<ActivityGroup>();
				for(ActivityGroupAssignment activityGroupAssignment: this.activity.<ActivityGroupAssignment>getAssignedGroup()) {
					try {
						if(activityGroupAssignment.getActivityGroup() != null) {
							activityGroups.add(activityGroupAssignment.getActivityGroup());
						}
					} catch(Exception ignore) {}
				}
				ActivityTracker mainActivityTracker = Activities.getInstance().getMainActivityTracker(activityGroups);
				if(mainActivityTracker != null) {
					this.formFields.setActivityGroupXri(
						mainActivityTracker.refGetPath().toXRI()
					);					
				}
			}
			this.activityGroup = null;
			if(this.formFields.getActivityGroupXri() != null) {
				try {
					this.activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)pm.getObjectById(
						new Path(this.formFields.getActivityGroupXri())
					);
					this.activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
					if(this.activity.getProcessState() != null) {
						this.activityQuery.thereExistsProcessState().equalTo(this.activity.getProcessState());
					}
					QueryExtensionRecord queryExtension = PersistenceHelper.newQueryExtension(this.activityQuery);
			    	queryExtension.setClause(
			    		Database_1_Attributes.HINT_COUNT + "(1=1)"
			    	);
					List<Activity> activities = this.activityGroup.getFilteredActivity(this.activityQuery);
					this.activityCount = activities.size();
				} catch (Exception e) {
					new ServiceException(e).log();
				}
			}
			if(this.activity != null) {
				if(this.activity.getActivityType() != null) {
					this.selectableProcessTransitions1 = this.getSelectableProcessTransitions(
						this.activity.getActivityType().getControlledBy(), 
						this.activity.getProcessState()
					);
					if(this.formFields.getProcessTransitionXri0() != null && !this.formFields.getProcessTransitionXri0().isEmpty()) {
						ActivityProcessTransition processTransition2 = (ActivityProcessTransition)pm.getObjectById(
							new Path(this.formFields.getProcessTransitionXri0())
						);
						this.selectableProcessTransitions2 = this.getSelectableProcessTransitions(
							this.activity.getActivityType().getControlledBy(),
							processTransition2.getNextState()
						);
					}
				}
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
	 * @return the formFields
	 */
	public FormFields getFormFields(
	) {
		return this.formFields;
	}

	/**
	 * @return the activityCount
	 */
	public Integer getActivityCount(
	) {
		return this.activityCount;
	}

	/**
	 * @return the activityGroup
	 */
	public ActivityGroup getActivityGroup(
	) {
		return this.activityGroup;
	}

	/**
	 * @return the activity
	 */
	public Activity getActivity(
	) {
		return this.activity;
	}
	
	/**
	 * @return the triggerAtDateOk
	 */
	public GregorianCalendar getTriggerAtDate(
	) {
		return this.triggerAtDate;
	}

	/**
	 * @return the timerName
	 */
	public String getTimerName(
	) {
		return this.timerName;
	}

	/**
	 * @return the activityQuery
	 */
	public ActivityQuery getActivityQuery() {
		return activityQuery;
	}

	/**
	 * @return the selectableProcessTransitions2
	 */
	public List<ActivityProcessTransition> getSelectableProcessTransitions2() {
		return selectableProcessTransitions2;
	}

	/**
	 * @return the selectableProcessTransitions1
	 */
	public List<ActivityProcessTransition> getSelectableProcessTransitions1() {
		return selectableProcessTransitions1;
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	public static final String TIMER_CLASS = "org:opencrx:kernel:home1:Timer";

	private String timerName;
	private Activity activity;
	private ActivityQuery activityQuery;
	private GregorianCalendar triggerAtDate;
	private Integer activityCount;
	private ActivityGroup activityGroup;
	private List<ActivityProcessTransition> selectableProcessTransitions1;
	private List<ActivityProcessTransition> selectableProcessTransitions2;
	private FormFields formFields;
	
}

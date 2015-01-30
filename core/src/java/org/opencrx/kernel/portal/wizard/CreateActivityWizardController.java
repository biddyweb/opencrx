/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CreateActivityWizardController
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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

import java.io.Writer;
import java.util.Date;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment;
import org.opencrx.kernel.activity1.jmi1.ActivityLinkTo;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.backend.ICalendar;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.ViewPortFactory;
import org.openmdx.portal.servlet.component.TransientObjectView;
import org.w3c.spi2.Datatypes;

/**
 * CreateActivityWizardController
 *
 */
public class CreateActivityWizardController extends org.openmdx.portal.servlet.AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 * @param formName
	 */
	public CreateActivityWizardController(
   	) {
   		super();
   	}

	/**
	 * Set exit action after successful creation of the activity.
	 * 
	 * @param activity
	 */
	public void setExitActionAfterCreation(
		Activity activity
	) throws ServiceException {
		this.setExitAction(
			new ObjectReference(activity, getApp()).getSelectObjectAction()
		);
	}

	/**
	 * OK action.
	 * 
	 * @param isInitialized
	 * @param linkToActivity
	 * @param memberContact
	 * @param fetchMemberContact
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doOK(
		@RequestParameter(name = "isInitialized") Boolean isInitialized,
		@RequestParameter(name = "linkToActivity") Boolean linkToActivity,
		@RequestParameter(name = "memberContact") String memberContact,
		@RequestParameter(name = "fetchMemberContact") Boolean fetchMemberContact,
		@FormParameter(forms = "CreateActivityForm") Map<String,Object> formFields   			
	) throws ServiceException {
		PersistenceManager pm = getPm();
		ApplicationContext app = getApp();
		RefObject_1_0 obj = getObject();
		this.doRefresh(
			isInitialized, 
			linkToActivity,
			memberContact, 
			fetchMemberContact,
			formFields
		);
		boolean allMandatoryFieldsSet = true;
		if(Boolean.TRUE.equals(isInitialized)) {
			org.openmdx.ui1.jmi1.ElementDefinition elDef = null;
			// Check name
			try {
				if((String)formFields.get("org:opencrx:kernel:activity1:Activity:name") == null) {
					allMandatoryFieldsSet = false;
					this.errorMessage += createErrorMessage(
						app.getTexts().getErrorTextMandatoryField(),
		            	new String[]{getFieldLabel(ACTIVITY_CLASS, "name", app.getCurrentLocaleAsIndex())}
		          	) + "<br>";
				}
			} catch(Exception ignore) {}    				
			// Check ActivityCreator
			try {
				if(formFields.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") == null) {
				allMandatoryFieldsSet = false;
				this.errorMessage += createErrorMessage(
					app.getTexts().getErrorTextMandatoryField(),
	            	new String[]{getFieldLabel(ACTIVITY_CLASS, "lastAppliedCreator", app.getCurrentLocaleAsIndex())}
	         	 ) + "<br>";
			}
		} catch(Exception ignore) {}    				
			// Check reportingAccount
			try {
				elDef = app.getUiElementDefinition("org:opencrx:kernel:activity1:Activity:reportingAccount");
				if (elDef != null && elDef.isMandatory() != null && elDef.isMandatory().booleanValue() && reportingAccount == null) {
					allMandatoryFieldsSet = false;
					this.errorMessage += createErrorMessage(
						app.getTexts().getErrorTextMandatoryField(),
		            	new String[]{getFieldLabel(ACTIVITY_CLASS, "reportingAccount", app.getCurrentLocaleAsIndex())}
		          	) + "<br>";
				}
			} catch(Exception ignore) {}
		}
		if(allMandatoryFieldsSet) {
			try{
				String name = (String)formFields.get("org:opencrx:kernel:activity1:Activity:name");
				Contact reportingContact = null;
				try {
					reportingContact = (Contact)pm.getObjectById(
						(Path)formFields.get("org:opencrx:kernel:activity1:Activity:reportingContact")
					);
				} catch (Exception ignore) {}
				Contact assignedTo = null;
				try {
					assignedTo = (Contact)pm.getObjectById(
						(Path)formFields.get("org:opencrx:kernel:activity1:Activity:assignedTo")
					);
				} catch (Exception ignore) {}
				Short priority = (Short)formFields.get("org:opencrx:kernel:activity1:Activity:priority");
				Date dueBy = (Date)formFields.get("org:opencrx:kernel:activity1:Activity:dueBy");
				Date scheduledStart = (Date)formFields.get("org:opencrx:kernel:activity1:Activity:scheduledStart");
				Date scheduledEnd = (Date)formFields.get("org:opencrx:kernel:activity1:Activity:scheduledEnd");
				String misc1 = (String)formFields.get("org:opencrx:kernel:activity1:Activity:misc1");
				String misc2 = (String)formFields.get("org:opencrx:kernel:activity1:Activity:misc2");
				String misc3 = (String)formFields.get("org:opencrx:kernel:activity1:Activity:misc3");
				String description = (String)formFields.get("org:opencrx:kernel:activity1:Activity:description");
				String detailedDescription = (String)formFields.get("org:opencrx:kernel:activity1:Activity:detailedDescription");
				if(
					(name != null) &&
					(name.trim().length() > 0) &&
					(this.lastAppliedActivityCreator != null)
				) {
					org.opencrx.kernel.activity1.jmi1.NewActivityParams params = org.w3c.spi2.Structures.create(
						NewActivityParams.class,
						Datatypes.member(NewActivityParams.Member.description, description),
						Datatypes.member(NewActivityParams.Member.detailedDescription, detailedDescription),
						Datatypes.member(NewActivityParams.Member.dueBy, dueBy),
						Datatypes.member(NewActivityParams.Member.name, name),
						Datatypes.member(NewActivityParams.Member.priority, priority),
						Datatypes.member(NewActivityParams.Member.reportingContact, reportingContact),
						Datatypes.member(NewActivityParams.Member.scheduledEnd, scheduledEnd),
						Datatypes.member(NewActivityParams.Member.scheduledStart, scheduledStart),
						Datatypes.member(NewActivityParams.Member.icalType, ICalendar.ICAL_TYPE_NA)   							
					);
					pm.currentTransaction().begin();
					org.opencrx.kernel.activity1.jmi1.NewActivityResult result = lastAppliedActivityCreator.newActivity(params);
					pm.currentTransaction().commit();
					org.opencrx.kernel.activity1.jmi1.Activity newActivity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(result.getActivity().refGetPath());
					pm.currentTransaction().begin();
					newActivity.setMisc1(misc1);
					newActivity.setMisc2(misc2);
					newActivity.setMisc3(misc3);
					newActivity.setReportingAccount(reportingAccount);
					if (assignedTo != null) {
						newActivity.setAssignedTo(assignedTo);
					}
					pm.currentTransaction().commit();
					// Create new ActivityLinkTo
	                if(obj instanceof org.opencrx.kernel.activity1.jmi1.Activity && Boolean.TRUE.equals(linkToActivity)) {
	                	try {
							pm.currentTransaction().begin();
							org.opencrx.kernel.activity1.jmi1.ActivityLinkTo activityLinkTo = pm.newInstance(ActivityLinkTo.class);
							activityLinkTo.setLinkTo(newActivity);
							activityLinkTo.setName(name);
							activityLinkTo.setActivityLinkType(CODE_ACTIVITYLINKTYPE_RELATESTO); // relates to
							((org.opencrx.kernel.activity1.jmi1.Activity)obj).addActivityLinkTo(
								org.opencrx.kernel.backend.Base.getInstance().getUidAsString(),
								activityLinkTo
							);
							pm.currentTransaction().commit();
						} catch (Exception e) {
							try {
								pm.currentTransaction().rollback();
							} catch (Exception er) {}
						}
	                }
	                org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup = null;
	                if (formFields.get("org:opencrx:kernel:activity1:ActivityGroupAssignment:activityGroup") != null) {
		    			try {
		    				activityGroup = (org.opencrx.kernel.activity1.jmi1.ActivityGroup)pm.getObjectById(
	    						(Path)formFields.get("org:opencrx:kernel:activity1:ActivityGroupAssignment:activityGroup")
	    					);
		    				if (activityGroup != null) {
		    					// Verify that this group has not been added already
		    					boolean alreadyAssigned = false;
								for(ActivityGroupAssignment assignment: newActivity.<ActivityGroupAssignment>getAssignedGroup()) {
									try {
										if (
											assignment.getActivityGroup() != null &&
											assignment.getActivityGroup().refGetPath().equals(activityGroup.refGetPath())
										) {
											alreadyAssigned = true;
											break;
										}
									} catch (Exception e) {
										new ServiceException(e).log();
									}
								}
								if(!alreadyAssigned) {
									try {
										pm.currentTransaction().begin();
										org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment agass = pm.newInstance(org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment.class);
										agass.refInitialize(false, false);
										agass.setActivityGroup(activityGroup);
										newActivity.addAssignedGroup(
									        false,
									        org.opencrx.kernel.backend.Accounts.getInstance().getUidAsString(),
									        agass
									    );
										pm.currentTransaction().commit();
									} catch (Exception e) {
										try {
											pm.currentTransaction().rollback();
										} catch (Exception er) {}
									}
								}
		    				}
		    			} catch (Exception e) {
		    				new ServiceException(e).log();
		    			}
	                }
	                this.setExitActionAfterCreation(result.getActivity());
					return;
				}
			} catch (Exception e) {
				new ServiceException(e).log();
				try {
					Throwable root = e;  
					while (root.getCause() != null) {  
					    root = root.getCause();  
					}
					this.errorMessage = (root.toString()).replaceAll("(\\r|\\n)","<br>");
				} catch (Exception e0) {}
				try {
					pm.currentTransaction().rollback();
				} catch (Exception er) {}
			}
		}
	}

	/**
	 * Refresh action.
	 * 
	 * @param isInitialized
	 * @param linkToActivity
	 * @param memberContact
	 * @param fetchMemberContact
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doRefresh(
		@RequestParameter(name = "isInitialized") Boolean isInitialized,
		@RequestParameter(name = "linkToActivity") Boolean linkToActivity,   			
		@RequestParameter(name = "memberContact") String memberContact,
		@RequestParameter(name = "fetchMemberContact") Boolean fetchMemberContact,
		@FormParameter(forms = "CreateActivityForm") Map<String,Object> formFields
	) throws ServiceException {
		PersistenceManager pm = getPm();
		this.linkToActivity = linkToActivity;
		this.formFields = formFields;
		RefObject_1_0 obj = getObject();
		if(
			formFields.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") == null &&
			obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityCreator
		) {
			formFields.put(
				"org:opencrx:kernel:activity1:Activity:lastAppliedCreator",
				obj.refGetPath()
			);
		}
		if(memberContact != null) {
			Contact resourceContact = null;
			try {
				resourceContact = (Contact)pm.getObjectById(new Path(memberContact));
		 	} catch (Exception ignore) {}
		 	if (resourceContact != null && Boolean.TRUE.equals(fetchMemberContact)) {
				formFields.put("org:opencrx:kernel:activity1:Activity:reportingContact", resourceContact.refGetPath());
		 	}
		}
		this.reportingAccount = formFields.get("org:opencrx:kernel:activity1:Activity:reportingAccount") != null 
			? (Account)pm.getObjectById(
	   			formFields.get("org:opencrx:kernel:activity1:Activity:reportingAccount")
	   		) 
	   		: null;
		this.lastAppliedActivityCreator = null;
		try {
			if (formFields.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") != null) {
				lastAppliedActivityCreator = (ActivityCreator)pm.getObjectById(
					formFields.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator")
				);
			}
		} catch (Exception ignore) {}
		if(obj instanceof Contact) {
		    formFields.put(
		    	"org:opencrx:kernel:activity1:Activity:reportingContact", 
		    	obj.refGetPath()
		    );
		}
		this.reportingContact = formFields.get("org:opencrx:kernel:activity1:Activity:reportingContact") != null 
			? (Contact)pm.getObjectById(
	   			formFields.get("org:opencrx:kernel:activity1:Activity:reportingContact")
	   		) 
	   		: null;
	}

	/**
	 * Cancel action.
	 * 
	 * @throws ServiceException
	 */
	public void doCancel(
	) throws ServiceException {
		this.setExitAction(
			new ObjectReference(getObject(), getApp()).getSelectObjectAction()
		);
	}

    /**
     * Create error message.
     * 
     * @param message
     * @param parameters
     * @return
     */
	public String createErrorMessage(
		String message,
		String[] parameters
	) {
		String preparedMessage = "";
		int i = 0;
		while(i < message.length()) {
			if((i <= message.length()-4) && "${".equals(message.substring(i,i+2))) {
				short index = new Short(message.substring(i+2, i+3)).shortValue();
				try {
					preparedMessage += parameters[index];
				} catch(Exception e) {}
				i += 4;
			} else {
				preparedMessage += message.charAt(i);
				i++;
			}
		}
		return preparedMessage;
	}

	/**
	 * Get form values.
	 * 
	 * @return
	 */
	public Map<String,Object> getFormFields(
	) {
		return this.formFields;
	}
	
	/**
	 * Get reporting account.
	 * 
	 * @return
	 */
	public Account getReportingAccount(
	) {
		return this.reportingAccount;
	}
	
	/**
	 * Get last applied creator.
	 * 
	 * @return
	 */
	public ActivityCreator getLastAppliedActivityCreator(
	) {
		return this.lastAppliedActivityCreator;
	}

	public Boolean getLinkToActivity(
	) {
		return this.linkToActivity;
	}

	/**
	 * @return the reportingContact
	 */
	public Contact getReportingContact() {
		return reportingContact;
	}
    
	/**
	 * Get view port.
	 * 
	 * @param out
	 * @return
	 */
	public ViewPort getViewPort(
		Writer out
	) {
		if(this.viewPort == null) {
			TransientObjectView view = new TransientObjectView(
				this.getFormFields(),
				this.getApp(),
				this.getObject(),
				this.getPm()
			);
			this.viewPort = ViewPortFactory.openPage(
				view,
				this.getRequest(),
				out
			);			
		}
		return this.viewPort;
	}

	/* (non-Javadoc)
	 * @see org.openmdx.portal.servlet.AbstractWizardController#close()
	 */
    @Override
    public void close(
    ) throws ServiceException {
	    super.close();
	    if(this.viewPort != null) {
	    	this.viewPort.close(false);
	    }
    }

	//-------------------------------------------------------------------
	// Members
	//-------------------------------------------------------------------    	
	public static final short CODE_ACTIVITYLINKTYPE_RELATESTO = (short)6;
	public static final String MEMBER_CLASS = "org:opencrx:kernel:account1:Member";
	public static final String ACTIVITY_CLASS = "org:opencrx:kernel:activity1:Activity";
	public static final String ACTIVITYLINKTO_CLASS = "org:opencrx:kernel:activity1:ActivityLinkTo";
	public static final String ACTIVITYCREATOR_CLASS = "org:opencrx:kernel:activity1:ActivityCreator";
	public static final String ACTIVITYGROUPASSIGNMENT_CLASS = "org:opencrx:kernel:activity1:ActivityGroupAssignment";
	public static final String ACTIVITYTYPE_CLASS = "org:opencrx:kernel:activity1:ActivityType";

	private Map<String,Object> formFields;
	private Account reportingAccount;
	private Contact reportingContact;
	private ActivityCreator lastAppliedActivityCreator;
	private Boolean linkToActivity;
	private ViewPort viewPort;

}

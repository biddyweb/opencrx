/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: TwitterSendMessageWizardController
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

import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;

import org.opencrx.application.twitter.TwitterUtils;
import org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityFollowUp;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.cci2.TwitterAccountQuery;
import org.opencrx.kernel.home1.jmi1.TwitterAccount;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.ViewPortFactory;
import org.openmdx.portal.servlet.component.TransientObjectView;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * TwitterSendMessageWizardController
 *
 */
public class TwitterSendMessageWizardController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public TwitterSendMessageWizardController(
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
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doRefresh(
		@FormParameter(forms = "TwitterSendMessageForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		ApplicationContext app = this.getApp();
		this.formFields = formFields;
		RefObject_1_0 obj = this.getObject();
		// Default value for field 'to'
		if(formFields.get("org:opencrx:kernel:base:SendAlertParams:toUsers") == null) {
	       	if(obj instanceof org.opencrx.kernel.generic.jmi1.CrxObject) {
	           	org.opencrx.kernel.generic.jmi1.CrxObject crxObject = (org.opencrx.kernel.generic.jmi1.CrxObject)obj;
				org.opencrx.kernel.generic.cci2.NoteQuery notesQuery = (org.opencrx.kernel.generic.cci2.NoteQuery)pm.newQuery(org.opencrx.kernel.generic.jmi1.Note.class);
				notesQuery.orderByCreatedAt().descending();
				notesQuery.thereExistsTitle().startsWith(NOTE_TITLE_PREFIX);
				List<org.opencrx.kernel.generic.jmi1.Note> notes = crxObject.getNote(notesQuery);
				if(!notes.isEmpty()) {
					org.opencrx.kernel.generic.jmi1.Note note = notes.iterator().next();
					int pos1 = note.getTitle().indexOf("[");
					int pos2 = note.getTitle().indexOf("]");
					if(pos2 > pos1) {
						String toUsers = note.getTitle().substring(pos1 + 1, pos2);
						formFields.put(
							"org:opencrx:kernel:base:SendAlertParams:toUsers",
							toUsers
						);
					}
				}
	       	}			
		}
		org.opencrx.kernel.home1.jmi1.UserHome userHome = UserHomes.getInstance().getUserHome(obj.refGetPath(), pm);
		if(formFields.get("org:opencrx:kernel:base:Note:text") == null) {
	    	Action nextAction = new ObjectReference(
		    	obj,
		    	app
		   	).getSelectObjectAction();
	    	if(userHome.getWebAccessUrl() != null) {
				String tinyUrl = org.opencrx.kernel.utils.TinyUrlUtils.getTinyUrl(
					userHome.getWebAccessUrl() + 
					(userHome.getWebAccessUrl().endsWith("/") ? "" : "/") + 
					nextAction.getEncodedHRef()
				);
				formFields.put(
					"org:opencrx:kernel:base:Note:text",
					"<your message> " + tinyUrl
				);
	    	}
		}		
	}

	/**
	 * OK action.
	 * 
	 * @param formFields
	 * @throws ServiceException
	 */
	public void doOK(
		@FormParameter(forms = "TwitterSendMessageForm") Map<String,Object> formFields		
	) throws ServiceException {
		PersistenceManager pm = this.getPm();
		RefObject_1_0 obj = this.getObject();
		this.doRefresh(formFields);
	    String toUsers = (String)formFields.get("org:opencrx:kernel:base:SendAlertParams:toUsers");
	    String text = (String)formFields.get("org:opencrx:kernel:base:Note:text");
	    if(
	        (toUsers != null) &&
	        !toUsers.isEmpty() &&
	        (text != null) &&
	        !text.isEmpty()
	    ) {
			javax.jdo.PersistenceManager rootPm = pm.getPersistenceManagerFactory().getPersistenceManager(
				org.opencrx.kernel.generic.SecurityKeys.ROOT_PRINCIPAL,
				null
			);
	    	org.opencrx.kernel.admin1.jmi1.ComponentConfiguration configuration = org.opencrx.application.twitter.TwitterUtils.getComponentConfiguration(
               	this.getProviderName(), 
               	this.getSegmentName(), 
               	rootPm
            );
            // Find default twitter account
			org.opencrx.kernel.home1.jmi1.UserHome userHome = UserHomes.getInstance().getUserHome(obj.refGetPath(), pm);	    	
            TwitterAccountQuery twitterAccountQuery = (TwitterAccountQuery)pm.newQuery(TwitterAccount.class);
            twitterAccountQuery.thereExistsIsDefault().isTrue();
            twitterAccountQuery.thereExistsIsActive().isTrue();
            List<TwitterAccount> twitterAccounts = userHome.getEMailAccount(twitterAccountQuery);            
            TwitterAccount twitterAccount = twitterAccounts.isEmpty() 
            	? null 
            	: twitterAccounts.iterator().next();            
	    	if(twitterAccount != null) {
            	TwitterFactory twitterFactory = new TwitterFactory();
            	AccessToken accessToken = new AccessToken(
            		twitterAccount.getAccessToken(),
            		twitterAccount.getAccessTokenSecret()
            	);
            	Twitter twitter = twitterFactory.getInstance();
            	twitter.setOAuthConsumer(
            		TwitterUtils.getConsumerKey(twitterAccount, configuration),
            		TwitterUtils.getConsumerSecret(twitterAccount, configuration)
            	);
            	twitter.setOAuthAccessToken(accessToken);
                StringTokenizer users = new StringTokenizer(toUsers, " ,", false);
                while(users.hasMoreTokens()) {
                	String user = users.nextToken();
                	try {
		            	@SuppressWarnings("unused")
                        DirectMessage message = twitter.sendDirectMessage(
		            		user,
		            		text
		            	);
                	} catch(Exception e) {
                		new ServiceException(e).log();
                	}
                }
               	// Send alert if invoked on user's home
	            String title = NOTE_TITLE_PREFIX + " [" + twitterAccount.getName() + "] @ " + new Date(); 
               	if(obj instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
               		org.opencrx.kernel.home1.jmi1.UserHome user = (org.opencrx.kernel.home1.jmi1.UserHome)obj;
               		Base.getInstance().sendAlert(
               			user, // target
               			user.refGetPath().getBase(),
               			title,
               			text,
               			(short)2, // importance
               			0, // resendDelayInSeconds
               			null // reference
               		);
               	}
                // Attach message as Note                
                else if(obj instanceof org.opencrx.kernel.generic.jmi1.CrxObject) {
                    try {
                    	pm.currentTransaction().begin();
	               		org.opencrx.kernel.generic.jmi1.CrxObject crxObject = (org.opencrx.kernel.generic.jmi1.CrxObject)obj;
	                	org.opencrx.kernel.generic.jmi1.Note note = pm.newInstance(org.opencrx.kernel.generic.jmi1.Note.class);
						note.setTitle(title);
						note.setText(text);
						crxObject.addNote(
							Base.getInstance().getUidAsString(),
							note
						);
						pm.currentTransaction().commit();
						// FollowUp if obj is an activity
						if(obj instanceof Activity) {
        					try {
        						Activity activity = (Activity)obj;
        						ActivityProcess activityProcess = activity.getActivityType().getControlledBy();
        						ActivityProcessTransitionQuery processTransitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(org.opencrx.kernel.activity1.cci2.ActivityProcessTransition.class);
        						processTransitionQuery.thereExistsPrevState().equalTo(activity.getProcessState());
        						processTransitionQuery.orderByNewPercentComplete().ascending();
        						List<ActivityProcessTransition> processTransitions = activityProcess.getTransition(processTransitionQuery);
        						if(!processTransitions.isEmpty()) {
        							ActivityProcessTransition processTransition = processTransitions.iterator().next();
        							pm.currentTransaction().begin();
        							@SuppressWarnings("unused")
                                    ActivityFollowUp followUp = Activities.getInstance().doFollowUp(
       					        		activity, 
       					        		title, 
       					        		text, 
       					        		processTransition, 
       					        		null, // reportingContact
       					        		null // parentProcessInstance
       					        	);
        							pm.currentTransaction().commit();
        						}
        					} catch(Exception e) {
        						try {
        							pm.currentTransaction().rollback();
        						} catch(Exception e0) {}
        					}							
						}
                    } catch(Exception e) {
                    	try {
                    		pm.currentTransaction().rollback();
                    	} catch(Exception e0) {}
                    }
                }
               	this.forward(
               		"Cancel",
               		this.getRequest().getParameterMap()
               	);
	    	}
	    }		
	}

	/**
	 * @return the formFields
	 */
	public Map<String, Object> getFormFields() {
		return formFields;
	}

	/**
	 * Get viewPort.
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

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	public final static String NOTE_TITLE_PREFIX = "Message sent to";

	private Map<String,Object> formFields;
	private ViewPort viewPort;
}

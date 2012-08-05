<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: CreateActivityWizard.jsp,v 1.25 2012/01/18 09:20:43 cmu Exp $
 * Description: CreateActivityWizard
 * Revision:    $Revision: 1.25 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/18 09:20:43 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2004-2011, CRIXP Corp., Switzerland
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
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.opencrx.kernel.portal.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.kernel.log.*,
org.openmdx.kernel.exception.*
" %>

<%!

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
                } 
                catch(Exception e) {}
                i += 4;
            }
            else {
                preparedMessage += message.charAt(i);
                i++;
            }
        }
        return preparedMessage;
    }

%>

<%
	request.setCharacterEncoding("UTF-8");
	String servletPath = "." + request.getServletPath();
	String servletPathPrefix = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
	String objectXri = request.getParameter(Action.PARAMETER_OBJECTXRI);
	if(objectXri == null || app == null || viewsCache.getView(requestId) == null) {
		response.sendRedirect(
			request.getContextPath() + "/" + WebKeys.SERVLET_NAME
		);
		return;
	}
	javax.jdo.PersistenceManager pm = app.getNewPmData();
	RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
	final String formName = "CreateActivityForm";
	final String wizardName = "CreateActivityWizard.jsp";
	final String ACTIVITY_CLASS = "org:opencrx:kernel:activity1:Activity";
	final String ACTIVITYCREATOR_CLASS = "org:opencrx:kernel:activity1:ActivityCreator";
	final String ACTIVITYTYPE_CLASS = "org:opencrx:kernel:activity1:ActivityType";
	final String MEMBER_CLASS = "org:opencrx:kernel:account1:Member";
	
	// Get Parameters
	String command = request.getParameter("Command");
	if(command == null) command = "";
	boolean actionCreate = "OK".equals(command);
	boolean actionCancel = "Cancel".equals(command);
	boolean isFirstCall = request.getParameter("isFirstCall") == null; // used to properly initialize various options
	
	if(actionCancel) {
		session.setAttribute(wizardName, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + nextAction.getEncodedHRef()
		);
		return;
	}
%>
<!--
	<meta name="UNUSEDlabel" content="Create Activity">
	<meta name="UNUSEDtoolTip" content="Create Activity">
	<meta name="targetType" content="_inplace">
	<meta name="forClass" content="org:opencrx:kernel:activity1:Segment">
	<meta name="forClass" content="org:opencrx:kernel:activity1:ActivityCreator">
	<meta name="order" content="org:opencrx:kernel:activity1:Segment:createActivity">
	<meta name="order" content="org:opencrx:kernel:activity1:ActivityCreator:createActivity">
-->
<%
	org.openmdx.ui1.jmi1.FormDefinition formDefinition = app.getUiFormDefinition(formName);
	org.openmdx.portal.servlet.control.FormControl form = new org.openmdx.portal.servlet.control.FormControl(
		formDefinition.refGetPath().getBase(),
		app.getCurrentLocaleAsString(),
		app.getCurrentLocaleAsIndex(),
		app.getUiContext(),
		formDefinition
	);
	Map formValues = new HashMap();
	form.updateObject(
		request.getParameterMap(),
		formValues,
		app,
		pm
	);
	
	/* if started from an activity creator - preselect this activity creator */
	if (
			 formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") == null &&
			 obj instanceof org.opencrx.kernel.activity1.jmi1.ActivityCreator
	) {
		formValues.put(
			"org:opencrx:kernel:activity1:Activity:lastAppliedCreator",
			obj.refGetPath()
		);
	}
	
	if(request.getParameter("memberContact") != null) {
		org.opencrx.kernel.account1.jmi1.Contact resourceContact = null;
		try {
			resourceContact = (org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(new Path(request.getParameter("memberContact")));
	 	} catch (Exception e) {}
	 	if (resourceContact != null && request.getParameter("fetchMemberContact") != null && request.getParameter("fetchMemberContact").length() > 0) {
			formValues.put("org:opencrx:kernel:activity1:Activity:reportingContact", resourceContact.refGetPath());
	 	}
	}
	
	String errorMsg = "";

	org.opencrx.kernel.account1.jmi1.Account reportingAccount = formValues.get("org:opencrx:kernel:activity1:Activity:reportingAccount") != null ?
   	(org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
   		formValues.get("org:opencrx:kernel:activity1:Activity:reportingAccount")
   	) : null;

	boolean allMandatoryFieldsSet = true;
	
	if (!isFirstCall) {

			org.openmdx.ui1.jmi1.ElementDefinition elDef = null;

			UserDefinedView userView = new UserDefinedView(
				pm.getObjectById(new Path(objectXri)),
				app,
				viewsCache.getView(requestId)
			);
		
			// check name
			try {
				if ((String)formValues.get("org:opencrx:kernel:activity1:Activity:name") == null) {
						allMandatoryFieldsSet = false;
						errorMsg += createErrorMessage(
								app.getTexts().getErrorTextMandatoryField(),
		            new String[]{userView.getFieldLabel(ACTIVITY_CLASS, "name", app.getCurrentLocaleAsIndex())}
		          ) + "<br>";
				}
			} catch(Exception e) {}
		
			// check ActivityCreator
			try {
				if (formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") == null) {
						allMandatoryFieldsSet = false;
						errorMsg += createErrorMessage(
								app.getTexts().getErrorTextMandatoryField(),
		            new String[]{userView.getFieldLabel(ACTIVITY_CLASS, "lastAppliedCreator", app.getCurrentLocaleAsIndex())}
		          ) + "<br>";
				}
			} catch(Exception e) {}
		
			// check reportingAccount
			try {
				elDef = app.getUiElementDefinition("org:opencrx:kernel:activity1:Activity:reportingAccount");
				if (elDef != null && elDef.isMandatory() != null && elDef.isMandatory().booleanValue() && reportingAccount == null) {
						allMandatoryFieldsSet = false;
						errorMsg += createErrorMessage(
								app.getTexts().getErrorTextMandatoryField(),
		            new String[]{userView.getFieldLabel(ACTIVITY_CLASS, "reportingAccount", app.getCurrentLocaleAsIndex())}
		          ) + "<br>";
				}
			} catch(Exception e) {}
			
	}
	
	if(actionCreate && allMandatoryFieldsSet) {
		try{
	    String name = (String)formValues.get("org:opencrx:kernel:activity1:Activity:name");

			org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = null;
			try {
				if (formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator") != null) {
						activityCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)pm.getObjectById(
									formValues.get("org:opencrx:kernel:activity1:Activity:lastAppliedCreator")
								);
				}
			} catch (Exception e) {
				new ServiceException(e).log();
			}
	    
	    org.opencrx.kernel.account1.jmi1.Contact reportingContact = formValues.get("org:opencrx:kernel:activity1:Activity:reportingContact") != null ?
	    	(org.opencrx.kernel.account1.jmi1.Contact)pm.getObjectById(
	    		formValues.get("org:opencrx:kernel:activity1:Activity:reportingContact")
	    	) : null;
	    Short priority = (Short)formValues.get("org:opencrx:kernel:activity1:Activity:priority");
	    Date dueBy = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:dueBy");
	    Date scheduledStart = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:scheduledStart");
	    Date scheduledEnd = (Date)formValues.get("org:opencrx:kernel:activity1:Activity:scheduledEnd");
	    String misc1 = (String)formValues.get("org:opencrx:kernel:activity1:Activity:misc1");
	    String misc2 = (String)formValues.get("org:opencrx:kernel:activity1:Activity:misc2");
	    String misc3 = (String)formValues.get("org:opencrx:kernel:activity1:Activity:misc3");
	    String description = (String)formValues.get("org:opencrx:kernel:activity1:Activity:description");
	    String detailedDescription = (String)formValues.get("org:opencrx:kernel:activity1:Activity:detailedDescription");
	    if(
	        (name != null) &&
	        (name.trim().length() > 0) &&
	        (activityCreator != null)
	    ) {
			org.opencrx.kernel.activity1.jmi1.NewActivityParams params = org.opencrx.kernel.utils.Utils.getActivityPackage(pm).createNewActivityParams(
				null, // creationContext
		 	    description,
		 	    detailedDescription,
		 	    dueBy,
		 	    (short)0,
		 	    name,
		 	    priority,
		 	    reportingContact,
		 	    scheduledEnd,
		 	    scheduledStart
			);
			pm.currentTransaction().begin();
			org.opencrx.kernel.activity1.jmi1.NewActivityResult result = activityCreator.newActivity(params);
			pm.currentTransaction().commit();
			org.opencrx.kernel.activity1.jmi1.Activity newActivity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(result.getActivity().refGetPath());
			pm.currentTransaction().begin();
			newActivity.setMisc1(misc1);
			newActivity.setMisc2(misc2);
			newActivity.setMisc3(misc3);
			newActivity.setReportingAccount(reportingAccount);
			pm.currentTransaction().commit();
			session.setAttribute(wizardName, null);
			Action nextAction = new ObjectReference(
		    	result.getActivity(),
		    	app
		   	).getSelectObjectAction();
			response.sendRedirect(
				request.getContextPath() + "/" + nextAction.getEncodedHRef()
			);
			return;
	    }
	  } catch (Exception e) {
	      new ServiceException(e).log();
	      try {
					Throwable root = e;  
			    while (root.getCause() != null) {  
			        root = root.getCause();  
			    }
					errorMsg = (root.toString()).replaceAll("(\\r|\\n)","<br>");
				} catch (Exception e0) {}
	      //System.out.println("error: " + errorMsg);
	  }
	}
	if(obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
	    formValues.put(
	    	"org:opencrx:kernel:activity1:Activity:reportingContact", 
	    	obj.refGetPath()
	    );
	}
	TransientObjectView view = new TransientObjectView(
		formValues,
		app,
		obj,
		pm
	);
	ViewPort p = ViewPortFactory.openPage(
		view,
		request,
		out
	);
%>
<br />
<form id="<%= formName %>" name="<%= formName %>" accept-charset="UTF-8" method="POST" action="<%= servletPath %>">
<%
	if (errorMsg != null && errorMsg.length() > 0) {
%>
			<div id="errorPane" style="padding:10px 10px 10px 10px;background-color:#FF0000;">
				<table>
					<tr style="vertical-align:top;">
						<td style="padding:5px;font-weight:bold;color:#FFFFFF;"><img src='images/Alert.gif'/></td>
						<td style="padding:5px;color:#FFFFFF;"><%= errorMsg %></td>
					</tr>
				</table>
			</div>
<%
	}
%>
	<input type="hidden" name="<%= Action.PARAMETER_REQUEST_ID %>" value="<%= requestId %>" />
	<input type="hidden" name="<%= Action.PARAMETER_OBJECTXRI %>" value="<%= objectXri %>" />
	<input type="checkbox" style="display:none;" id="isFirstCall" name="isFirstCall" checked="true" />
	<input type="hidden" id="Command" name="Command" value="" />
	<table cellspacing="8" class="tableLayout">
		<tr>
			<td class="cellObject">
				<div class="panel" id="panel<%= formName %>" style="display: block">
<%
					form.paint(
						p,
						null, // frame
						true // forEditing
					);
					p.flush();
%>					
					<script language="javascript" type="text/javascript">
						try {
				      var els = document.getElementsByTagName('INPUT');
				      for (i=0; i<els.length; i++) {
				        if (els[i].id && (els[i].id.indexOf('org:opencrx:kernel:activity1:Activity:reportingAccount') >= 0)) {
				          var INPUT = els[i];
				          <!-- workaround because onChange does not fire with {IE, Chrome, ...} -->
				          INPUT.setAttribute('onFocus', 'this.previousValue = this.value;');
				          INPUT.setAttribute('onBlur','if (this.previousValue != this.value) {this.previousValue=this.value;setTimeout("$(\'Refresh.Button\').click()", 500);}');
				        }
				      }
						} catch(e){}
					</script>
<%
					if (reportingAccount != null) {
							boolean hasContacts = false;
							// create dropdown with list of member contacts
%>
							<table style="display:none;">
								<tr><td/><td/><td/></tr>
								<tr id="toBeInserted">
									<td class="label">
									
										<span class="nw"><%= app.getLabel(MEMBER_CLASS) %>:</span>
									</td>
									<td>
										<input type="hidden" id="fetchMemberContact" name="fetchMemberContact" value="" /> 
										<select id="memberContact" name="memberContact" class="valueL" onchange="javascript:$('fetchMemberContact').value='override';$('Refresh.Button').click();" >
		                  <option value="NA">^</option>
<%
			                // get Members sorted by name(asc)
											String providerName = obj.refGetPath().get(2);
											String segmentName = obj.refGetPath().get(4);
											org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
													new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
												);
											org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);
			                org.opencrx.kernel.account1.cci2.MemberQuery memberQuery = accountPkg.createMemberQuery();
			                memberQuery.orderByName().ascending();
			                memberQuery.forAllDisabled().isFalse();
											int maxMemberToShow = 200;
			                for (
			                  Iterator k = reportingAccount.getMember(memberQuery).iterator();
			                  k.hasNext() && maxMemberToShow > 0;
			                  maxMemberToShow--
			                ) {
			                	try {
				                  // get member
				            	    org.opencrx.kernel.account1.jmi1.Member member =
				                    (org.opencrx.kernel.account1.jmi1.Member)k.next();
				            	    org.opencrx.kernel.account1.jmi1.Account account = member.getAccount();
				            	    org.opencrx.kernel.account1.jmi1.Contact contact = null;
				            	    if (account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
				            	    		contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
				            	    }
				            	    if (contact != null) {
				            	    	hasContacts = true;
					                  String selectedModifier = ""; //((contact != null ) && (assignedTo != null) && (assignedTo.refMofId().compareTo(contact.refMofId()) == 0)) ? "selected" : "";
%>
					                  <option <%= selectedModifier %> value="<%= contact.refMofId() %>"><%= member.getName() + (contact != null ? " (" + contact.getFirstName() + " " + contact.getLastName() + ")": "") %></option>
<%
				            	    }
			                	} catch (Exception e) {}
			                }
%>
			              </select>
				          </td>
									<td class="addon"/>
								</tr>
							</table>
							
							<script language="javascript" type="text/javascript">
								try {
						      var els = document.getElementsByTagName('DIV');
						      for (i=0; i<els.length && <%= hasContacts ? "true" : "false" %>; i++) {
						        if (els[i].className=="fieldGroupName" && els[i].innerHTML && (els[i].innerHTML.indexOf('Activity') >= 0)) {
						          var TABLE = els[i].nextSibling;
						          while (TABLE.nodeName != "TABLE" && TABLE.nextSibling) {
							          	TABLE = TABLE.nextSibling;
						          };
						          if (TABLE.nodeName == "TABLE") {
								          var TBODY = TABLE.firstChild;
								          while (TBODY.nodeName != "TBODY" && TBODY.nextSibling) {
								        	  	TBODY = TBODY.nextSibling;
								          };
								          if (TBODY.nodeName == "TBODY") {
									          	// append row with id "toBeInserted"
									          	var tr = document.getElementById('toBeInserted');
									          	TBODY.appendChild(tr);
								          }
						          }
						        }
						      }
								} catch(e){}
							</script>					
<%
					}
%>
				</div>
 				<input type="submit" name="Refresh" id="Refresh.Button" tabindex="9000" value="<%= app.getTexts().getReloadText() %>" style="display:none;" onclick="javascript:$('Command').value=this.name;" />
				<input type="submit" name="OK" id="OK.Button" tabindex="9000" value="<%= app.getTexts().getNewText() %>" onclick="javascript:$('Command').value=this.name;this.name='---';" />
				<input type="submit" name="Cancel" tabindex="9010" value="<%= app.getTexts().getCancelTitle() %>" onclick="javascript:$('Command').value=this.name;" />
			</td>
		</tr>
	</table>
</form>
<script language="javascript" type="text/javascript">
	Event.observe('<%= formName %>', 'submit', function(event) {
		$('<%= formName %>').request({
			onFailure: function() { },
			onSuccess: function(t) {
				$('UserDialog').update(t.responseText);
			}
		});
		Event.stop(event);
	});
</script>
<%
p.close(false);
if(pm != null) {
	pm.close();
}
%>

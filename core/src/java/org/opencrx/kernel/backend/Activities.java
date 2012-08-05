/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Activities.java,v 1.189 2010/12/25 11:54:04 wfro Exp $
 * Description: Activities
 * Revision:    $Revision: 1.189 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/25 11:54:04 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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

package org.opencrx.kernel.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.HeaderTokenizer;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;

import org.omg.mof.spi.Names;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.activity1.cci2.ActivityLinkToQuery;
import org.opencrx.kernel.activity1.cci2.ActivityProcessActionQuery;
import org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.AddressGroupMemberQuery;
import org.opencrx.kernel.activity1.cci2.EMailQuery;
import org.opencrx.kernel.activity1.cci2.ResourceAssignmentQuery;
import org.opencrx.kernel.activity1.cci2.ResourceQuery;
import org.opencrx.kernel.activity1.cci2.WorkAndExpenseRecordQuery;
import org.opencrx.kernel.activity1.jmi1.AbstractEMailRecipient;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityCreationAction;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityDoFollowUpParams;
import org.opencrx.kernel.activity1.jmi1.ActivityFollowUp;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment;
import org.opencrx.kernel.activity1.jmi1.ActivityLinkTo;
import org.opencrx.kernel.activity1.jmi1.ActivityProcess;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessAction;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessState;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.activity1.jmi1.ActivityVote;
import org.opencrx.kernel.activity1.jmi1.ActivityWorkRecord;
import org.opencrx.kernel.activity1.jmi1.AddressGroup;
import org.opencrx.kernel.activity1.jmi1.AddressGroupMember;
import org.opencrx.kernel.activity1.jmi1.Calendar;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.EMailRecipientGroup;
import org.opencrx.kernel.activity1.jmi1.EffortEstimate;
import org.opencrx.kernel.activity1.jmi1.LinkedActivityFollowUpAction;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.activity1.jmi1.ResourceAssignment;
import org.opencrx.kernel.activity1.jmi1.SetActualEndAction;
import org.opencrx.kernel.activity1.jmi1.SetActualStartAction;
import org.opencrx.kernel.activity1.jmi1.SetAssignedToAction;
import org.opencrx.kernel.activity1.jmi1.WeekDay;
import org.opencrx.kernel.activity1.jmi1.WfAction;
import org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord;
import org.opencrx.kernel.depot1.jmi1.CompoundBooking;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReference;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.jmi1.Media;
import org.opencrx.kernel.generic.jmi1.Note;
import org.opencrx.kernel.generic.jmi1.PropertySet;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.uom1.jmi1.Uom;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.kernel.workflow1.jmi1.WfProcess;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.io.QuotaByteArrayOutputStream;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.PersistenceHelper;
import org.openmdx.base.text.conversion.Base64;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.kernel.loading.Classes;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObjects;
import org.w3c.format.DateTimeFormat;

public class Activities extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Activities());
	}
	
    //-------------------------------------------------------------------------
	public static Activities getInstance(
	) throws ServiceException {
		return getInstance(Activities.class);
	}

	//-------------------------------------------------------------------------
	protected Activities(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public void refreshItems(
        ActivityTracker activityTracker
    ) throws ServiceException {
        this.refreshTracker(
        	activityTracker
        );
    }
    
    //-------------------------------------------------------------------------
    public void markActivityAsDirty(
        Activity activity
    ) throws ServiceException {
    	activity.setActivityState(activity.getActivityState());
    }
    	
    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityType findActivityType(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.activity1.cci2.ActivityTypeQuery activityTypeQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityTypeQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityType.class);
        activityTypeQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.ActivityType> activityTypes = segment.getActivityType(activityTypeQuery);
        return activityTypes.isEmpty() ? 
        	null : 
        	activityTypes.iterator().next();
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityProcess findActivityProcess(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
        org.opencrx.kernel.activity1.cci2.ActivityProcessQuery activityProcessQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityProcessQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityProcess.class);
        activityProcessQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.ActivityProcess> activityProcesses = segment.getActivityProcess(activityProcessQuery);
        return activityProcesses.isEmpty() ? 
        	null : 
        	activityProcesses.iterator().next();
    }

    //-----------------------------------------------------------------------
    public ActivityProcessTransition findActivityProcessTransition(
    	Activity activity,
    	String transitionName
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);
    	ActivityProcessState state = activity.getProcessState();
    	ActivityProcess process = (ActivityProcess)pm.getObjectById(state.refGetPath().getParent().getParent());    	
    	ActivityProcessTransitionQuery transitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
    	transitionQuery.name().equalTo(transitionName);
    	List<ActivityProcessTransition> transitions = process.getTransition(transitionQuery);
    	if(!transitions.isEmpty()) {
    		return transitions.iterator().next();
    	}
    	return null;
    }
        
    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityCreator findActivityCreator(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
        org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery activityCreatorQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.ActivityCreator.class);
        activityCreatorQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.ActivityCreator> activityCreators = segment.getActivityCreator(activityCreatorQuery);
        return activityCreators.isEmpty() ? 
        	null : 
        	activityCreators.iterator().next();
    }

    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */
    public org.opencrx.kernel.activity1.jmi1.ActivityTracker findActivityTracker(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
    	return this.findActivityTracker(name, segment);
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityTracker findActivityTracker(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);
        org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery activityTrackerQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery)pm.newQuery(ActivityTracker.class);
        activityTrackerQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.ActivityTracker> activityTrackers = segment.getActivityTracker(activityTrackerQuery);
        return activityTrackers.isEmpty() ? 
        	null : 
        	activityTrackers.iterator().next();
    }

    //-------------------------------------------------------------------------
    /**
     * @deprecated 
     */
    public org.opencrx.kernel.activity1.jmi1.ActivityCategory findActivityCategory(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
    	return this.findActivityCategory(name, segment);
    }

    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.ActivityCategory findActivityCategory(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);    	
        org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery activityCategoryQuery = 
        	(org.opencrx.kernel.activity1.cci2.ActivityCategoryQuery)pm.newQuery(ActivityCategory.class);
        activityCategoryQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.ActivityCategory> activityCategories = segment.getActivityCategory(activityCategoryQuery);
        return activityCategories.isEmpty() ? 
        	null : 
        	activityCategories.iterator().next();
    }

    //-------------------------------------------------------------------------
    /**
     * @deprecated
     */
    public org.opencrx.kernel.activity1.jmi1.Calendar findCalendar(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment,
        javax.jdo.PersistenceManager pm
    ) {
    	return this.findCalendar(name, segment);
    }
    
    //-------------------------------------------------------------------------
    public org.opencrx.kernel.activity1.jmi1.Calendar findCalendar(
        String name,
        org.opencrx.kernel.activity1.jmi1.Segment segment
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(segment);    	
        org.opencrx.kernel.activity1.cci2.CalendarQuery calendarQuery = 
        	(org.opencrx.kernel.activity1.cci2.CalendarQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Calendar.class);
        calendarQuery.name().equalTo(name);
        List<org.opencrx.kernel.activity1.jmi1.Calendar> calendars = segment.getCalendar(calendarQuery);
        return calendars.isEmpty() ? 
        	null : 
        	calendars.iterator().next();
    }
    
    //-----------------------------------------------------------------------
    public Calendar initCalendar(
        String calendarName,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        Calendar calendar = null;
        if((calendar = this.findCalendar(calendarName, activitySegment, pm)) != null) {
            return calendar;            
        }                        
        pm.currentTransaction().begin();                    
        calendar = pm.newInstance(Calendar.class);
        calendar.refInitialize(false, false);
        calendar.setName(calendarName);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        activitySegment.addCalendar(
            false,
            UUIDConversion.toUID(uuids.next()),
            calendar
        );
        // Sunday
        WeekDay weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)1);
        weekDay.setWorkingDay(false);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Monday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)2);
        weekDay.setWorkingDay(true);
        weekDay.setWorkDurationHours((short)8);
        weekDay.setWorkDurationMinutes((short)30);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Tuesday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)3);
        weekDay.setWorkingDay(true);
        weekDay.setWorkDurationHours((short)8);
        weekDay.setWorkDurationMinutes((short)30);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Wednesday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)4);
        weekDay.setWorkingDay(true);
        weekDay.setWorkDurationHours((short)8);
        weekDay.setWorkDurationMinutes((short)30);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Thursday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)5);
        weekDay.setWorkingDay(true);
        weekDay.setWorkDurationHours((short)8);
        weekDay.setWorkDurationMinutes((short)30);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Friday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)6);
        weekDay.setWorkingDay(true);
        weekDay.setWorkDurationHours((short)8);
        weekDay.setWorkDurationMinutes((short)30);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        // Saturday
        weekDay = pm.newInstance(WeekDay.class);
        weekDay.refInitialize(false, false);
        weekDay.setDayOfWeek((short)7);
        weekDay.setWorkingDay(false);
        calendar.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        calendar.addWeekDay(
            false,
            UUIDConversion.toUID(uuids.next()),
            weekDay
        );
        pm.currentTransaction().commit();
        return calendar;
    }
      
    //-----------------------------------------------------------------------
    public ActivityProcess initEmailProcess(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityProcess process = null;
        if((process = this.findActivityProcess(ACTIVITY_PROCESS_NAME_EMAILS, activitySegment, pm)) != null) {
            return process;            
        }                
        // Create email process
        pm.currentTransaction().begin();                    
        process = pm.newInstance(ActivityProcess.class);
        process.refInitialize(false, false);
        process.setName(ACTIVITY_PROCESS_NAME_EMAILS);
        process.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        activitySegment.addActivityProcess(
            false,
            UUIDConversion.toUID(uuids.next()),
            process
        );
        // State New
        ActivityProcessState newState = pm.newInstance(ActivityProcessState.class);
        newState.refInitialize(false, false);
        newState.setName("New");
        newState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            UUIDConversion.toUID(uuids.next()),
            newState
        );
        // State Open
        ActivityProcessState openState = pm.newInstance(ActivityProcessState.class);
        openState.refInitialize(false, false);
        openState.setName("Open");
        openState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            UUIDConversion.toUID(uuids.next()),
            openState
        );
        // State Closed
        ActivityProcessState closedState = pm.newInstance(ActivityProcessState.class);
        closedState.refInitialize(false, false);
        closedState.setName("Closed");
        closedState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            UUIDConversion.toUID(uuids.next()),
            closedState
        );
        pm.currentTransaction().commit();                    
        // Initial State
        pm.currentTransaction().begin();
        process.setStartState(newState);                    
        // Transition Assign: New->Open
        ActivityProcessTransition processTransition = pm.newInstance(ActivityProcessTransition.class);
        processTransition.refInitialize(false, false);
        processTransition.setName("Assign");
        processTransition.setPrevState(newState);
        processTransition.setNextState(openState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)20));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Create SetAssignedToAction
        SetAssignedToAction setAssignedToAction = pm.newInstance(SetAssignedToAction.class);
        setAssignedToAction.refInitialize(false, false);
        setAssignedToAction.setName("Set assignedTo");
        setAssignedToAction.setDescription("Set assignedTo to current user");
        setAssignedToAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setAssignedToAction
        );
        // Create SetActualStartAction
        SetActualStartAction setActualStartAction = pm.newInstance(SetActualStartAction.class);
        setActualStartAction.refInitialize(false, false);
        setActualStartAction.setName("Set actual start");
        setActualStartAction.setDescription("Set actual start on activity assignment");
        setActualStartAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setActualStartAction
        );
        // Transition Add Note: Open->Open
        processTransition = pm.newInstance(ActivityProcessTransition.class);
        processTransition.refInitialize(false, false);
        processTransition.setName("Add Note");
        processTransition.setPrevState(openState);
        processTransition.setNextState(openState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)50));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Transition Export: Open->Open
        processTransition = pm.newInstance(ActivityProcessTransition.class);
        processTransition.refInitialize(false, false);
        processTransition.setName("Export as mail attachment");
        processTransition.setPrevState(openState);
        processTransition.setNextState(openState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)50));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Create WorkflowAction for ExportMail
        WfAction wfAction = pm.newInstance(WfAction.class);
        wfAction.refInitialize(false, false);
        wfAction.setName("Export Mail");
        wfAction.setName("Export Mail as attachment to current user");
        wfAction.setWfProcess(
            (WfProcess)pm.getObjectById(
                new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/" + providerName + "/segment/" + segmentName + "/wfProcess/" + Workflows.WORKFLOW_EXPORT_MAIL)
            )
        );
        wfAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            wfAction
        );
        // Transition Send: Open->Open
        processTransition = pm.newInstance(ActivityProcessTransition.class);
        processTransition.refInitialize(false, false);
        processTransition.setName("Send as mail");
        processTransition.setPrevState(openState);
        processTransition.setNextState(openState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)50));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Create WorkflowAction for SendMail
        wfAction = pm.newInstance(WfAction.class);
        wfAction.refInitialize(false, false);
        wfAction.setName("Send Mail");
        wfAction.setName("Send as mail");
        wfAction.setWfProcess(
            (WfProcess)pm.getObjectById(
                new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/" + providerName + "/segment/" + segmentName + "/wfProcess/" + Workflows.WORKFLOW_SEND_MAIL)
            )
        );
        wfAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            wfAction
        );
        // Transition Close: Open->Closed
        processTransition = pm.newInstance(ActivityProcessTransition.class);
        processTransition.refInitialize(false, false);
        processTransition.setName("Close");
        processTransition.setPrevState(openState);
        processTransition.setNextState(closedState);
        processTransition.setNewActivityState((short)20);
        processTransition.setNewPercentComplete(new Short((short)100));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Create SetActualEndAction
        SetActualEndAction setActualEndAction = pm.newInstance(SetActualEndAction.class);
        setActualEndAction.refInitialize(false, false);
        setActualEndAction.setName("Set actual end");
        setActualEndAction.setName("Set actual end to current dateTime");
        setActualEndAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setActualEndAction
        );
        // Commit
        pm.currentTransaction().commit();
        
        return process;
    }
            
    //-----------------------------------------------------------------------
    public ActivityProcess initBugAndFeatureTrackingProcess(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        Activity1Package activityPkg = Utils.getActivityPackage(pm);
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityProcess process = null;
        if((process = this.findActivityProcess(ACTIVITY_PROCESS_NAME_BUG_AND_FEATURE_TRACKING, activitySegment, pm)) != null) {
            return process;            
        }                
        // Create process
        pm.currentTransaction().begin();                    
        process = activityPkg.getActivityProcess().createActivityProcess();
        process.refInitialize(false, false);
        process.setName(ACTIVITY_PROCESS_NAME_BUG_AND_FEATURE_TRACKING);
        process.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        activitySegment.addActivityProcess(
            false,
            UUIDConversion.toUID(uuids.next()),
            process
        );
        // State New
        ActivityProcessState newState = activityPkg.getActivityProcessState().createActivityProcessState();
        newState.refInitialize(false, false);
        newState.setName("New");
        newState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            "StateNew",
            newState
        );
        // State In Progress
        ActivityProcessState inProgressState = activityPkg.getActivityProcessState().createActivityProcessState();
        inProgressState.refInitialize(false, false);
        inProgressState.setName("In Progress");
        inProgressState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            "InProgress",
            inProgressState
        );
        // State Complete
        ActivityProcessState completeState = activityPkg.getActivityProcessState().createActivityProcessState();
        completeState.refInitialize(false, false);
        completeState.setName("Complete");
        completeState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            "StateComplete",
            completeState
        );
        // State Closed
        ActivityProcessState closedState = activityPkg.getActivityProcessState().createActivityProcessState();
        closedState.refInitialize(false, false);
        closedState.setName("Closed");
        closedState.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addState(
            false,
            "StateClosed",
            closedState
        );
        pm.currentTransaction().commit();                    
        // Initial State
        pm.currentTransaction().begin();
        process.setStartState(newState);                    
        // Transition Add Note: Open->Open
        ActivityProcessTransition processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Add Note");
        processTransition.setPrevState(inProgressState);
        processTransition.setNextState(inProgressState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)50));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            "TransitionAddNote",
            processTransition
        );
        // Transition Assign: New->In Progress
        processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Assign");
        processTransition.setPrevState(newState);
        processTransition.setNextState(inProgressState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)20));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            "TransitionAssign",
            processTransition
        );
        // Create SetAssignedToAction
        SetAssignedToAction setAssignedToAction = activityPkg.getSetAssignedToAction().createSetAssignedToAction();
        setAssignedToAction.refInitialize(false, false);
        setAssignedToAction.setName("Set assignedTo");
        setAssignedToAction.setDescription("Set assignedTo to current user");
        setAssignedToAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setAssignedToAction
        );
        // Create SetActualStartAction
        SetActualStartAction setActualStartAction = activityPkg.getSetActualStartAction().createSetActualStartAction();
        setActualStartAction.refInitialize(false, false);
        setActualStartAction.setName("Set actual start");
        setActualStartAction.setDescription("Set actual start on activity assignment");
        setActualStartAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setActualStartAction
        );
        // Transition Close: Complete->Closed
        processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Close");
        processTransition.setPrevState(completeState);
        processTransition.setNextState(closedState);
        processTransition.setNewActivityState((short)20);
        processTransition.setNewPercentComplete(new Short((short)100));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            "TransitionClose",
            processTransition
        );
        // Transition Complete: In Progress->Complete
        processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Complete");
        processTransition.setPrevState(inProgressState);
        processTransition.setNextState(completeState);
        processTransition.setNewActivityState((short)20);
        processTransition.setNewPercentComplete(new Short((short)100));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );
        // Create SetActualEndAction
        SetActualEndAction setActualEndAction = activityPkg.getSetActualEndAction().createSetActualEndAction();
        setActualEndAction.refInitialize(false, false);
        setActualEndAction.setName("Set actual end");
        setActualEndAction.setName("Set actual end to current dateTime");
        setActualEndAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setActualEndAction
        );
        // Create SetAssignedToAction
        setAssignedToAction = activityPkg.getSetAssignedToAction().createSetAssignedToAction();
        setAssignedToAction.refInitialize(false, false);
        setAssignedToAction.setName("Set assignedTo");
        setAssignedToAction.setDescription("Set assignedTo to reporting contact");
        setAssignedToAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setAssignedToAction
        );
        // Transition Create: New->New
        processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Create");
        processTransition.setPrevState(newState);
        processTransition.setNextState(newState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)0));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );        
        // Transition Reopen: Complete->In Progress
        processTransition = activityPkg.getActivityProcessTransition().createActivityProcessTransition();
        processTransition.refInitialize(false, false);
        processTransition.setName("Reopen");
        processTransition.setPrevState(completeState);
        processTransition.setNextState(inProgressState);
        processTransition.setNewActivityState((short)10);
        processTransition.setNewPercentComplete(new Short((short)50));
        processTransition.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        process.addTransition(
            false,
            UUIDConversion.toUID(uuids.next()),
            processTransition
        );        
        // Create SetAssignedToAction
        setAssignedToAction = activityPkg.getSetAssignedToAction().createSetAssignedToAction();
        setAssignedToAction.refInitialize(false, false);
        setAssignedToAction.setName("Set assignedTo");
        setAssignedToAction.setDescription("Set assignedTo to current user");
        setAssignedToAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setAssignedToAction
        );        
        // Create SetActualEndAction
        setActualEndAction = activityPkg.getSetActualEndAction().createSetActualEndAction();
        setActualEndAction.refInitialize(false, false);
        setActualEndAction.setName("Reset actualEnd");
        setActualEndAction.setDescription("Reset actualEnd");
        setActualEndAction.setResetToNull(true);
        setActualEndAction.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        processTransition.addAction(
            false,
            UUIDConversion.toUID(uuids.next()),
            setActualEndAction
        );        
        // Commit
        pm.currentTransaction().commit();
        
        return process;
    }
            
    //-----------------------------------------------------------------------
    public ActivityType initActivityType(
        String activityTypeName,
        short activityClass,
        ActivityProcess activityProcess,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityType activityType = null;
        if((activityType = this.findActivityType(activityTypeName, activitySegment, pm)) != null) {
            return activityType;            
        }                
        pm.currentTransaction().begin();
        activityType = pm.newInstance(ActivityType.class);
        activityType.refInitialize(false, false);
        activityType.setName(activityTypeName);
        activityType.setActivityClass(activityClass);
        activityType.setControlledBy(activityProcess);
        activityType.getOwningGroup().addAll(
            activitySegment.getOwningGroup()
        );
        activitySegment.addActivityType(
            false,
            UUIDConversion.toUID(uuids.next()),
            activityType
        );    
        pm.currentTransaction().commit();    
        return activityType;
    }
            
    //-----------------------------------------------------------------------
    public ActivityTracker initActivityTracker(
        String trackerName,
        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> owningGroups,        
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityTracker activityTracker = null;
        if((activityTracker = this.findActivityTracker(trackerName, activitySegment, pm)) != null) {
            return activityTracker;            
        }        
        pm.currentTransaction().begin();
        activityTracker = pm.newInstance(ActivityTracker.class);
        activityTracker.refInitialize(false, false);
        activityTracker.setName(trackerName);
        activityTracker.getOwningGroup().addAll(
            owningGroups == null
                ? activitySegment.getOwningGroup()
                : owningGroups
        );
        activitySegment.addActivityTracker(
            false,
            UUIDConversion.toUID(uuids.next()),
            activityTracker
        );                        
        pm.currentTransaction().commit();
        return activityTracker;
    }
            
    //-----------------------------------------------------------------------
    public ActivityCategory initActivityCategory(
        String categoryName,
        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> owningGroups,        
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityCategory activityCategory = null;
        if((activityCategory = this.findActivityCategory(categoryName, activitySegment, pm)) != null) {
            return activityCategory;            
        }        
        pm.currentTransaction().begin();
        activityCategory = pm.newInstance(ActivityCategory.class);
        activityCategory.refInitialize(false, false);
        activityCategory.setName(categoryName);
        activityCategory.getOwningGroup().addAll(
            owningGroups == null
                ? activitySegment.getOwningGroup()
                : owningGroups
        );
        activitySegment.addActivityCategory(
            false,
            UUIDConversion.toUID(uuids.next()),
            activityCategory
        );                        
        pm.currentTransaction().commit();
        return activityCategory;
    }
            
    //-----------------------------------------------------------------------
    public ActivityCreator initActivityCreator(
        String creatorName,
        org.opencrx.kernel.activity1.jmi1.ActivityType activityType,
        List<org.opencrx.kernel.activity1.jmi1.ActivityGroup> activityGroups,
        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> owningGroups,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        UUIDGenerator uuids = UUIDs.getGenerator();
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(
            pm, 
            providerName, 
            segmentName
        );
        ActivityCreator activityCreator = null;
        if((activityCreator = this.findActivityCreator(creatorName, activitySegment)) == null) {
            pm.currentTransaction().begin();
            activityCreator = pm.newInstance(ActivityCreator.class);
            activityCreator.refInitialize(false, false);
            activityCreator.setName(creatorName);
            activityCreator.setPriority((short)0);
            activityCreator.getOwningGroup().addAll(
                owningGroups == null ? 
                    activitySegment.getOwningGroup() : 
                    owningGroups
            );
            activityCreator.getActivityGroup().addAll(
                activityGroups
            );
            activityCreator.setActivityType(activityType);                        
            activitySegment.addActivityCreator(
                false,
                UUIDConversion.toUID(uuids.next()),
                activityCreator
            );
            pm.currentTransaction().commit();
        }
        // Set default creator for activity groups
        pm.currentTransaction().begin();
        for(org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup: activityGroups) {
            if(activityGroup.getDefaultCreator() == null) {
                activityGroup.setDefaultCreator(activityCreator);
            }
        }
        pm.currentTransaction().commit();
        return activityCreator;
    }
            
    //-------------------------------------------------------------------------
    public ActivityTracker refreshTracker(
      ActivityTracker activityTracker
    ) throws ServiceException {    	
        Collection<Activity> activities = activityTracker.getFilteredActivity();
        int estimateEffortHours = 0;
        int estimateEffortMinutes = 0;
        // Iterate all activities and sum up all main effort estimates. Don't care
        // if isMain=true if there is exactly one estimate for the activity
        for(Activity activity: activities) {
            Collection<EffortEstimate> effortEstimates = activity.getEffortEstimate();
            if(effortEstimates.size() == 1) {
                EffortEstimate effortEstimate = effortEstimates.iterator().next();
                if(effortEstimate.getEstimateEffortHours() != null) {
                    estimateEffortHours += effortEstimate.getEstimateEffortHours().intValue();
                }
                if(effortEstimate.getEstimateEffortMinutes() != null) {
                    estimateEffortMinutes += effortEstimate.getEstimateEffortMinutes().intValue();
                }
            }
            // Lookup main estimate
            else {
                for(EffortEstimate effortEstimate: effortEstimates) {
                    if(
                        (effortEstimate.isMain() != null) &&
                        effortEstimate.isMain().booleanValue()
                    ) {                    
                        if(effortEstimate.getEstimateEffortHours() != null) {
                            estimateEffortHours += effortEstimate.getEstimateEffortHours().intValue();
                        }
                        if(effortEstimate.getEstimateEffortMinutes() != null) {
                            estimateEffortMinutes += effortEstimate.getEstimateEffortMinutes();
                        }
                        // At most one main estimate is allowed. If the user entered more 
                        // than one include the first only
                        break;
                    }
                }
            }
        }
        // Update tracker
        activityTracker.setSumEstimateEffortHours(
        	new Integer(estimateEffortHours + estimateEffortMinutes / 60)
        );
        activityTracker.setSumEstimateEffortMinutes(
            new Integer(estimateEffortMinutes % 60)
        );
        return activityTracker;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Creates a new activity and links the tracker with this new activity.
     */
    public Activity newActivity(
        ActivityCreator activityCreator,
        String name,
        String description,
        String detailedDescription,
        Date suppliedScheduledStart,
        Date suppliedScheduledEnd,
        Date suppliedDueBy,
        Number suppliedPriority,
        Number suppliedIcalType,
        Contact reportingContact
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activityCreator);    	
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
    		(org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
    			activityCreator.refGetPath().getParent().getParent()
    		);
        if(activityCreator.getActivityType() != null) {
            ActivityType activityType = activityCreator.getActivityType();
            ActivityProcess activityProcess = activityType.getControlledBy();
            Date scheduledStart = null;
            scheduledStart = suppliedScheduledStart != null ? 
                suppliedScheduledStart : 
                (activityCreator.getBaseDate() != null) && (activityCreator.getScheduledStart() != null) ? 
                    new Date(System.currentTimeMillis() + activityCreator.getScheduledStart().getTime()) : 
                    new Date();
            Date scheduledEnd = null;
            scheduledEnd = suppliedScheduledEnd != null ? 
                suppliedScheduledEnd : 
                (activityCreator.getBaseDate() != null) && (activityCreator.getScheduledEnd() != null) ? 
                    new Date(System.currentTimeMillis() + activityCreator.getScheduledEnd().getTime() - activityCreator.getBaseDate().getTime()) : 
                    new Date(scheduledStart.getTime() + 3600000L);
            Date dueBy = null;
            dueBy = suppliedDueBy != null ? 
                suppliedDueBy : 
                (activityCreator.getBaseDate() != null) && (activityCreator.getDueBy() != null) ? 
                    new Date(System.currentTimeMillis() + activityCreator.getDueBy().getTime() - activityCreator.getBaseDate().getTime()) : 
                    null;
            short priority = (suppliedPriority == null) || (suppliedPriority.shortValue() == 0) ?
                activityCreator.getPriority() != 0 ? 
                    activityCreator.getPriority() : 
                    (short)2 :    
                suppliedPriority.shortValue();
            short icalType = (suppliedIcalType == null) || (suppliedIcalType.shortValue() == 0) ?
                activityCreator.getIcalType() :    
                suppliedIcalType.shortValue();
            String activityClass = activityType.getActivityClassName() != null ? 
                activityType.getActivityClassName() : 
                ACTIVITY_TYPES[((Number)activityType.getActivityClass()).intValue()];
            Activity newActivity = null;
			try {
				String packageName = activityClass.substring(0, activityClass.lastIndexOf(":"));
				String className = activityClass.substring(activityClass.lastIndexOf(":") + 1);
				newActivity = (Activity)pm.newInstance(
					Classes.getApplicationClass(
						packageName.replace(":", ".") + "." + Names.JMI1_PACKAGE_SUFFIX + "." + className
					)
				);
			}
			catch(ClassNotFoundException e) {
				throw new ServiceException(e);
			}
            newActivity.refInitialize(false, false);
            if(name != null) {
                newActivity.setName(name);
            }
            if(description != null) {
                newActivity.setDescription(description);
            }
            if(detailedDescription != null) {
                newActivity.setDetailedDescription(detailedDescription);
            }
            if(scheduledStart != null) {
                newActivity.setScheduledStart(scheduledStart);
            }
            if(scheduledEnd != null) {
                newActivity.setScheduledEnd(scheduledEnd);
            }
            if(reportingContact != null) {
                newActivity.setReportingContact(reportingContact);                    
            }
            else {
                org.opencrx.kernel.home1.jmi1.UserHome userHome = UserHomes.getInstance().getUserHome(
                    activityCreator.refGetPath(),
                    pm
                );
                newActivity.setReportingContact(userHome.getContact());
            }
            newActivity.setPriority(new Short(priority));
            newActivity.setIcalType(new Short(icalType));
            if(dueBy != null) {
                newActivity.setDueBy(dueBy);
            }
            newActivity.setActivityState(new Short((short)0));
            newActivity.setPercentComplete(new Short((short)0));
            newActivity.setActivityType(activityType);
            newActivity.setProcessState(activityProcess.getStartState());
            // Set code values to 0 (non-optional attributes)
            if(newActivity instanceof org.opencrx.kernel.activity1.jmi1.Incident) {
            	org.opencrx.kernel.activity1.jmi1.Incident incident = (org.opencrx.kernel.activity1.jmi1.Incident)newActivity;
            	incident.setCaseOrigin(new Short((short)0));
            	incident.setCaseType(new Short((short)0));
            	incident.setCustomerSatisfaction(new Short((short)0));
            	incident.setSeverity(new Short((short)0));
            	incident.setReproducibility(new Short((short)0));
            }
            try {                
                // Create activity
            	activitySegment.addActivity(
            		true,
            		this.getUidAsString(),
            		newActivity
            	);
                this.reapplyActivityCreator(
                    newActivity,
                    activityCreator
                );
                Base.getInstance().assignToMe(
                	newActivity, 
                	false, 
                	null 
                );                	
                this.updateIcal(
                    newActivity
                );
                return newActivity;
            }
            catch(ServiceException e) {
            	SysLog.warning("Creation of new activity failed", e.getMessage());
            	SysLog.warning(e.getMessage(), e.getCause());
            }
        }
        return null;
    }
    
    //-------------------------------------------------------------------------
    public ActivityVote voteForActivity(
        Activity activity,
        String name,
        String description
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
        ActivityVote vote = pm.newInstance(ActivityVote.class);
        vote.refInitialize(false, false);
        if(name != null) {
            vote.setName(name);
        }
        if(description != null) {
            vote.setDescription(description);
        }
        activity.addVote(
        	false,
        	this.getUidAsString(),
        	vote
        );
        Base.getInstance().assignToMe(
            vote,
            true,
            null
        );
        return vote;
    }

    //-------------------------------------------------------------------------
    public void markAsAllDayEvent(
        Activity activity,
        String timezoneID
    ) {
    	if(timezoneID == null) {
    		timezoneID = "GMT-0:00";
    	}
    	// scheduledStart
		java.util.Calendar localScheduledStart = GregorianCalendar.getInstance(java.util.TimeZone.getTimeZone(timezoneID));
		localScheduledStart.setTime(activity.getScheduledStart());
		java.util.Calendar scheduledStart = GregorianCalendar.getInstance(java.util.TimeZone.getTimeZone("GMT-0:00"));
		scheduledStart.set(java.util.Calendar.YEAR, localScheduledStart.get(java.util.Calendar.YEAR));
		scheduledStart.set(java.util.Calendar.MONTH, localScheduledStart.get(java.util.Calendar.MONTH));
		scheduledStart.set(java.util.Calendar.DAY_OF_MONTH, localScheduledStart.get(java.util.Calendar.DAY_OF_MONTH));		
		scheduledStart.set(java.util.Calendar.HOUR_OF_DAY, 0);
		scheduledStart.set(java.util.Calendar.MINUTE, 0);
		scheduledStart.set(java.util.Calendar.SECOND, 0);
		scheduledStart.set(java.util.Calendar.MILLISECOND, 0);
		// scheduledEnd
		java.util.Calendar localScheduledEnd = GregorianCalendar.getInstance(java.util.TimeZone.getTimeZone(timezoneID));		
		localScheduledEnd.setTime(activity.getScheduledEnd());
		java.util.Calendar scheduledEnd = GregorianCalendar.getInstance(java.util.TimeZone.getTimeZone("GMT-0:00"));
		scheduledEnd.set(java.util.Calendar.YEAR, localScheduledEnd.get(java.util.Calendar.YEAR));
		scheduledEnd.set(java.util.Calendar.MONTH, localScheduledEnd.get(java.util.Calendar.MONTH));
		scheduledEnd.set(java.util.Calendar.DAY_OF_MONTH, localScheduledEnd.get(java.util.Calendar.DAY_OF_MONTH));				
		scheduledEnd.set(java.util.Calendar.HOUR_OF_DAY, 0);
		scheduledEnd.set(java.util.Calendar.MINUTE, 0);
		scheduledEnd.set(java.util.Calendar.SECOND, 0);		
		scheduledEnd.set(java.util.Calendar.MILLISECOND, 0);		
		if(scheduledStart.get(java.util.Calendar.DAY_OF_MONTH) == scheduledEnd.get(java.util.Calendar.DAY_OF_MONTH)) {
			scheduledEnd.add(
				java.util.Calendar.DAY_OF_MONTH,
				1
			);
		}
		activity.setScheduledStart(scheduledStart.getTime());
		activity.setScheduledEnd(scheduledEnd.getTime());
    }
    
    //-------------------------------------------------------------------------
    public ActivityFollowUp linkToAndFollowUp(
        Activity activity,
        ActivityProcessTransition processTransition,
    	Activity linkTo
    ) throws ServiceException {
    	if(linkTo == null) return null;
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);
    	String followUpTitle = null;
    	String followUpText = null;
    	// Append activity number to linkTo.name if not already present
    	if(linkTo.getName() != null && ((linkTo.getName().indexOf(" [#") < 0) || !linkTo.getName().endsWith("]"))) {
    		linkTo.setName(linkTo.getName() + " [#" + activity.getActivityNumber() + "]");
    	}
    	if(linkTo instanceof EMail) {
    		EMail email = (EMail)linkTo;
    		// append activity number to linkTo.messageSubject if not already present
    		if(email.getMessageSubject() != null && ((email.getMessageSubject().indexOf(" [#") < 0) || !email.getMessageSubject().endsWith("]"))) {
    			email.setMessageSubject(email.getMessageSubject() + " [#" + activity.getActivityNumber() + "]");
    		}
    		followUpTitle = email.getMessageSubject();
    		followUpText = email.getMessageBody();
    	} else {
    		followUpTitle = linkTo.getName();
    		followUpText = linkTo.getDetailedDescription();
    	}
    	ActivityFollowUp followUp = this.doFollowUp(
    		activity, 
    		followUpTitle, 
    		followUpText, 
    		processTransition, 
    		linkTo.getReportingContact()
    	);
    	followUp.setActivity(linkTo);
    	ActivityLinkTo activityLink = pm.newInstance(ActivityLinkTo.class);
    	activityLink.refInitialize(false, false);
    	activityLink.setName("#" + linkTo.getActivityNumber() + ": " + activity.getName());
    	activityLink.setActivityLinkType(ACTIVITY_LINK_TYPE_RELATES_TO);
    	activityLink.setLinkTo(linkTo);
    	activity.addActivityLinkTo(
    		this.getUidAsString(),
    		activityLink
    	);
    	return followUp;
    }
    
    //-------------------------------------------------------------------------
    public ActivityFollowUp doFollowUp(
        Activity activity,
        String followUpTitle,
        String followUpText,
        ActivityProcessTransition processTransition,
        Contact assignTo
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
        ActivityProcessState processState = activity.getProcessState();
        if(processTransition != null) {
        	org.opencrx.kernel.activity1.jmi1.Segment activitySegment =
        		(org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
        			activity.refGetPath().getParent().getParent()
        		);
            if(processTransition.getNextState() == null) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.ACTIVITY_UNDEFINED_NEXT_STATE, 
                    "Undefined next state. Transition not possible.",
                    new BasicException.Parameter("param0", processTransition.refGetPath())
                );
            }
            // Check that previous state of transition matches the current activity's state
            if(
                ((processTransition.getPrevState() == null) && (processState == null)) ||
                ((processTransition.getPrevState() != null) && processTransition.getPrevState().equals(processState))
            ) {
                
            }
            else {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.ACTIVITY_TRANSITION_NOT_VALID_FOR_STATE, 
                    "Transition is not valid for current state",
                     new BasicException.Parameter("param0", processTransition.refGetPath()),
                     new BasicException.Parameter("param1", processState.refGetPath())
                );
            } 
            // Apply transition to activity
            activity.setLastTransition(
                processTransition         
            );    
            activity.setPercentComplete(
                processTransition.getNewPercentComplete()
            );
            activity.setActivityState(
                processTransition.getNewActivityState()
            );
            
            /**
             * Execute actions. If at least the execution of one action fails
             * the transition is considered as failed. In this case the activity
             * is set to set errState if defined.
             */
            ActivityProcessState nextState = processTransition.getNextState();
            ActivityProcessState errState = processTransition.getErrState();
            ActivityProcessActionQuery activityProcessActionQuery = (ActivityProcessActionQuery)pm.newQuery(ActivityProcessAction.class);
            activityProcessActionQuery.orderByName().ascending();
            List<ActivityProcessAction> actions = processTransition.getAction(activityProcessActionQuery);
            boolean failed = false;
            for(ActivityProcessAction action: actions) {
                // SetActualEndAction
                if(action instanceof SetActualEndAction) {
                	SetActualEndAction setActualEndAction = (SetActualEndAction)action;
                    if((setActualEndAction.isResetToNull() != null) && setActualEndAction.isResetToNull().booleanValue()) {
                        activity.setActualEnd(null);
                    }
                    else {
                        activity.setActualEnd(new Date());
                    }
                }
                // SetActualStartAction
                else if(action instanceof SetActualStartAction) {
                	SetActualStartAction setActualStartAction = (SetActualStartAction)action;
                    if((setActualStartAction.isResetToNull() != null) && setActualStartAction.isResetToNull().booleanValue()) {
                        activity.setActualStart(null);
                    }
                    else {
                        activity.setActualStart(new Date());
                    }
                }
                // SetAssignedToAction
                else if(action instanceof SetAssignedToAction) {
                	SetAssignedToAction setAssignedToAction = (SetAssignedToAction)action;
                    // Determine contact to which activity is assigned to
                    Contact contact = null;
                    try {
                        if(setAssignedToAction.getContactFeatureName() != null) {
                        	try {
                        		contact = (Contact)activity.refGetValue(setAssignedToAction.getContactFeatureName());
                        	} 
                        	catch(Exception e) {}
                        }
                        if(contact == null) {
                            UserHome userHome = UserHomes.getInstance().getUserHome(action.refGetPath(), pm);
                            contact = userHome.getContact();
                        }
                        if(contact != null) {
                            // Determine resource matching the contact 
                        	ResourceQuery resourceQuery = (ResourceQuery)pm.newQuery(Resource.class);
                        	resourceQuery.thereExistsContact().equalTo(contact);
                        	List<Resource> resources = activitySegment.getResource(resourceQuery);
                            if(!resources.isEmpty()) {
                                this.assignTo(
                                    activity,
                                    resources.iterator().next()
                                );
                            }
                        }
                    }
                    catch(Exception e) {
                    	SysLog.warning("Execution of action failed --> transition failed.", action);
                        new ServiceException(e).log();
                        failed = true;
                    }                            
                }
                // WfAction
                else if(action instanceof WfAction) {
                    UserHome userHome = UserHomes.getInstance().getUserHome(action.refGetPath(), pm);
                    WfAction wfAction = (WfAction)action;
                    if(wfAction.getWfProcess() != null) {
                        try {
                            WfProcessInstance wfProcessInstance = 
                                Workflows.getInstance().executeWorkflow(
                                    userHome,
                                    wfAction.getWfProcess(),
                                    activity,
                                    null,
                                    null,
                                    null
                                );
                            SysLog.info("Execution of workflow successful.", action);
                            Boolean wfExecutionFailed = wfProcessInstance.isFailed();
                            if((wfExecutionFailed != null) && wfExecutionFailed.booleanValue()) {
                                failed = true;
                            }
                        }
                        catch(Exception e) {
                        	SysLog.warning("Execution of action failed --> transition failed.", action);
                            new ServiceException(e).log();
                            failed = true;
                        }                            
                    }
                }
                // ActivityCreationAction
                else if(action instanceof ActivityCreationAction) {
                	ActivityCreationAction activityCreationAction = (ActivityCreationAction)action;
                    if(activityCreationAction.getActivityCreator() != null) {
                        try {
                            Activity newActivity = this.newActivity(
                            	activityCreationAction.getActivityCreator(),
                                activityCreationAction.getName(),
                                activityCreationAction.getActivityDescription(),
                                null, // detailedDescription
                                null, // suppliedScheduledStart
                                null, // suppliedScheduledEnd
                                null, // suppliedDueBy
                                null, // suppliedPriority
                                ICalendar.ICAL_TYPE_NA, // icalType
                                activity.getReportingContact()
                            );
                            // Link new activity with original
                            ActivityLinkTo activityLinkTo = pm.newInstance(ActivityLinkTo.class);
                            activityLinkTo.refInitialize(false, false);
                            activityLinkTo.setName(activity.getName());
                            activityLinkTo.setActivityLinkType(
                                new Short(ACTIVITY_LINK_TYPE_IS_DERIVED_FROM)
                            );
                            activityLinkTo.setLinkTo(activity);
                            newActivity.addActivityLinkTo(
                            	false,
                            	this.getUidAsString(),
                            	activityLinkTo
                            );
                        }
                        catch(Exception e) {
                        	SysLog.warning("Execution of action failed --> transition failed.", action);
                            new ServiceException(e).log();
                            failed = true;
                        }         
                    }
                }
                // LinkedActivityFollowUpAction
                else if(activity instanceof LinkedActivityFollowUpAction) {
                	LinkedActivityFollowUpAction linkedActivityFollowUpAction = (LinkedActivityFollowUpAction)action;
                    Collection<ActivityLinkTo> activityLinks = activity.getActivityLinkTo();
                    for(ActivityLinkTo activityLink: activityLinks) {
                        short activityLinkType = activityLink.getActivityLinkType();
                        short actionActivityLinkType = linkedActivityFollowUpAction.getActivityLinkType();  
                        if(
                            (activityLink.getLinkTo() != null) &&
                            (linkedActivityFollowUpAction.getTransition() != null) &&
                            (actionActivityLinkType == activityLinkType)
                        ) {
                            try {
                                this.doFollowUp(
                                    activityLink.getLinkTo(),
                                    linkedActivityFollowUpAction.getTransitionTitle(),
                                    linkedActivityFollowUpAction.getTransitionText(),
                                    linkedActivityFollowUpAction.getTransition(),
                                    null
                                );
                            }
                            catch(Exception e) {
                            	SysLog.warning("Execution of action failed --> transition failed.", action);
                                new ServiceException(e).log();
                                failed = true;
                            }
                        }
                    }
                }                
            }
            activity.setProcessState(
                failed && errState != null ? 
                	errState : 
                	nextState
            );
        }        
        // Create transition
        ActivityFollowUp transition = pm.newInstance(ActivityFollowUp.class);
        transition.refInitialize(false, false);
        transition.setTransition(processTransition);
        transition.setTitle(followUpTitle);
        transition.setText(followUpText);
        activity.addFollowUp(
        	false,
        	this.getUidAsString(),
        	transition
        );
        if(assignTo == null) {
            Base.getInstance().assignToMe(
                transition,
                true,
                null
            );
        }
        else {
            transition.setAssignedTo(assignTo);
        }
        return transition;
    }
        
    //-------------------------------------------------------------------------
    public void updateWorkAndExpenseRecord(
    	WorkAndExpenseRecord workRecord
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(workRecord);
        PersistenceManager pmRoot =  pm.getPersistenceManagerFactory().getPersistenceManager(
        	SecurityKeys.ROOT_PRINCIPAL,
        	null
        );
        BigDecimal amount = workRecord.getQuantity();
        BigDecimal rate = workRecord.getRate();
        workRecord.setBillableAmount(
        	amount == null || rate == null ?
        		null :
        		rate.multiply(workRecord.getQuantity())        	
        );
        BigDecimal oldAmount = null;
        try {
        	oldAmount = ((ActivityWorkRecord)pmRoot.getObjectById(workRecord.refGetPath())).getQuantity(); 
        }
        catch(Exception e) {}
        // Update booking
        if(!Utils.areEqual(amount, oldAmount)) {
	        // depotSelector
	        short depotSelector = workRecord.getDepotSelector() != 0 ? 
	        	workRecord.getDepotSelector() : 
	        	Depots.DEPOT_USAGE_WORK_EFFORT; 
	        if(depotSelector == 0) {
	            depotSelector = Depots.DEPOT_USAGE_WORK_EFFORT;
	        }
	        // Update work booking if duration has been changed                
            if(workRecord.getWorkCb() != null) {
                try {
                    Depots.getInstance().removeCompoundBooking(
                        workRecord.getWorkCb(),
                        false
                    );
                } 
                catch(Exception e) {}
            }
            workRecord.setWorkCb(null);
            // Depot credit
            // Get assigned depot of resource with usage DEPOT_USAGE_WORK_EFFORT
            Depot depotCredit = null;
            ResourceAssignment resourceAssignment = (ResourceAssignment)pm.getObjectById(
            	workRecord.refGetPath().getParent().getParent()
            );
            Resource resource = resourceAssignment.getResource();
            Collection<DepotReference> depotReferences = null;
            if(resource == null) {
                depotReferences = Collections.emptyList();
            }
            else {
                depotReferences = resource.getDepotReference();
            }
            // Depot selector
            for(DepotReference depotReference: depotReferences) {
                short depotUsage = depotReference.getDepotUsage();
                if(depotUsage == depotSelector) {
                    depotCredit = depotReference.getDepot();
                }
            }            
            // Depot debit
            // Get assigned depot of activity with usage DEPOT_USAGE_WORK_EFFORT
            Depot depotDebit = null;
            Activity activity = (Activity)pm.getObjectById(
                workRecord.refGetPath().getPrefix(workRecord.refGetPath().size() - 4)
            );
            depotReferences = activity.getDepotReference();
            for(DepotReference depotReference: depotReferences) {
                short depotUsage = depotReference.getDepotUsage();
                if(depotUsage == depotSelector) {
                    depotDebit = depotReference.getDepot();
               }
            }
            ActivityType activityType = activity.getActivityType();
            if(
                (depotCredit != null) &&
                (depotDebit != null) &&
                (activityType != null)
            ) {
                if(!depotCredit.refGetPath().getPrefix(7).equals(depotDebit.refGetPath().getPrefix(7))) {
                    throw new ServiceException(
                        OpenCrxException.DOMAIN,
                        OpenCrxException.DEPOT_POSITION_IS_LOCKED,
                        "Depot entity not equal",
                        new BasicException.Parameter("param0", depotDebit.refGetPath()),
                        new BasicException.Parameter("param1", depotCredit.refGetPath())
                    );
                }
                else {
                    Date valueDate = workRecord.getEndedAt();
                    DepotPosition positionCredit = Depots.getInstance().openDepotPosition(
                        depotCredit,
                        activityType.getName(),
                        activityType.getDescription(),
                        valueDate,
                        null, // if position does not exist open at value date
                        null,
                        Boolean.FALSE
                    );
                    DepotPosition positionDebit = Depots.getInstance().openDepotPosition(
                        depotDebit,
                        activityType.getName(),
                        activityType.getDescription(),
                        valueDate,
                        null, // if position does not exist open at value date
                        null,
                        Boolean.FALSE
                    );
                    CompoundBooking workCb = 
                        Depots.getInstance().createCreditDebitBooking(
                            (DepotEntity)pm.getObjectById(depotCredit.refGetPath().getPrefix(7)),
                            workRecord.getEndedAt(),
                            Depots.BOOKING_TYPE_STANDARD,
                            workRecord.getQuantity(),
                            BOOKING_TEXT_NAME_WORK_EFFORT,
                            activityType.getWorkBt(),
                            positionCredit,
                            positionDebit,
                            workRecord, // origin
                            null, // reversalOf
                            new ArrayList<String>()
                        );
                    workRecord.setWorkCb(workCb);
                }
	        }
    	}
    }
    
    //-------------------------------------------------------------------------
    public ActivityWorkRecord addWorkAndExpenseRecord(
        Activity activity,
        Resource resource,
        String name,
        String description,
        Date startedAt,
        Date endedAt,
        BigDecimal quantity,
        Uom quantityUom,
        short recordType,
        short paymentType,
        short depotSelector,
        BigDecimal rate,
        short rateCurrency,
        Boolean isBillable,
        Boolean isReimbursable,
        List<PrincipalGroup> owningGroups
    ) throws ServiceException {
        ActivityWorkRecord workRecord = null;
        if(resource == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.ACTIVITY_CAN_NOT_ADD_WORK_RECORD_MISSING_RESOURCE,
               "Can not add work record. Missing resource"
           );            
        }
        if(activity == null) {
           throw new ServiceException(
               OpenCrxException.DOMAIN,
               OpenCrxException.ACTIVITY_CAN_NOT_ADD_WORK_RECORD_MISSING_ACTIVITY,
               "Can not add work record. Missing activity"
           );            
        }        
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);
    	// Validate groups
    	List<PrincipalGroup> groups = new ArrayList<PrincipalGroup>();
    	for(PrincipalGroup group: owningGroups) {
    		if(group != null) {
    			groups.add(group);
    		}
    	}
        ResourceAssignmentQuery resourceAssignmentQuery = (ResourceAssignmentQuery)pm.newQuery(ResourceAssignment.class);
        resourceAssignmentQuery.thereExistsResource().equalTo(resource);
        Collection<ResourceAssignment> resourceAssignments = activity.getAssignedResource(resourceAssignmentQuery);
        ResourceAssignment resourceAssignment = null;
        if(resourceAssignments.isEmpty()) {
            resourceAssignment = this.createResourceAssignment(
                activity, 
                resource, 
                (short)0,
                groups
            );
        }
        else {
            resourceAssignment = resourceAssignments.iterator().next();
        }            
        workRecord = pm.newInstance(ActivityWorkRecord.class);
        workRecord.refInitialize(false, false);
        if(name != null) {
            workRecord.setName(name);
        }
        if(description != null) {
            workRecord.setDescription(description);
        }           
        try {
	        workRecord.setStartedAt(
	            startedAt == null ? 
	            	(startedAt = new Date()) : 
	            	startedAt
	        );
	        workRecord.setEndedAt(
	            endedAt == null ?
	            	quantity == null ?
	            		null :
	            	    (endedAt = DateTimeFormat.BASIC_UTC_FORMAT.parse(DateTimeFormat.BASIC_UTC_FORMAT.format(new Date(startedAt.getTime() + quantity.multiply(new BigDecimal(3600000L)).longValue())))) : 
	            	endedAt
	        );
        }
        catch(Exception e) {}
        // Default amount is endedAt - startedAt in hours/minutes
        if(quantity == null && endedAt != null && startedAt != null) {
        	workRecord.setQuantity(
        		new BigDecimal(Math.abs(endedAt.getTime() - startedAt.getTime()) / 60000L).divide(new BigDecimal("60.0"), RoundingMode.FLOOR)
        	);
        	Uom uomHour = null;
        	try {
        		uomHour = (Uom)pm.getObjectById(
        			new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/" + activity.refGetPath().get(2) + "/segment/Root/uom/hour")
        		);
        	}
        	catch(Exception e) {}
        	workRecord.setQuantityUom(uomHour);
        }
        else {
        	workRecord.setQuantity(quantity);        	
            workRecord.setQuantityUom(quantityUom);
        }
        // Get default rate from resource in case of work records
        if(
        	(rate == null) && 
        	(recordType == Activities.WORKRECORD_TYPE_WORK_OVERTIME || recordType == Activities.WORKRECORD_TYPE_WORK_STANDARD)
        ) {
            workRecord.setBillingCurrency(
                resource.getRateCurrency()
            );
        	switch(recordType) {
        		case Activities.WORKRECORD_TYPE_WORK_STANDARD:
        			workRecord.setRate(resource.getStandardRate());
        			break;
        		case Activities.WORKRECORD_TYPE_WORK_OVERTIME:
        			workRecord.setRate(resource.getOvertimeRate());
        			break;
        	}
        }
        else {
        	workRecord.setRate(rate);
            workRecord.setBillingCurrency(rateCurrency);
        }
        workRecord.setRecordType(recordType);
        workRecord.setPaymentType(paymentType);
        workRecord.setBillable(isBillable);
        workRecord.setReimbursable(isReimbursable);
        workRecord.setDepotSelector(depotSelector);
        if(groups != null && !groups.isEmpty()) {
        	workRecord.getOwningGroup().addAll(groups);
        }
        else {
        	workRecord.getOwningGroup().addAll(
        		resourceAssignment.getOwningGroup()
        	);
        }
        resourceAssignment.addWorkRecord(
        	false,
        	this.getUidAsString(),
        	workRecord
        );
        this.updateWorkAndExpenseRecord(
        	workRecord
        );
        return workRecord;
    }
    
    //-------------------------------------------------------------------------
    public void removeWorkRecord(
    	WorkAndExpenseRecord workRecord,
        boolean preDelete
    ) throws ServiceException {
        if(workRecord.getWorkCb() != null) {
            Depots.getInstance().removeCompoundBooking(
                workRecord.getWorkCb(),
                false
            );
        }
        if(!preDelete) {
        	workRecord.refDelete();
        }
    }
    
    //-------------------------------------------------------------------------
    public void removeActivityGroup(
        ActivityGroup activityGroup,
        boolean preDelete
    ) throws ServiceException {
        Collection<Activity> activities = activityGroup.getFilteredActivity();
        // Don't allow removal if activity group has assigned activities
        for (Activity activity: activities) {
        	if (!JDOHelper.isDeleted(activity)){        
	            throw new ServiceException(
	                OpenCrxException.DOMAIN,
	                OpenCrxException.ACTIVITY_GROUP_HAS_ASSIGNED_ACTIVITIES, 
	                "Activity group has assigned activities. Can not remove.",
	                new BasicException.Parameter("param0", activityGroup.refGetPath())
	            );
        	}
        }
        if(!preDelete) {
        	activityGroup.refDelete();
        }
    }
    
    //-----------------------------------------------------------------------
    protected String getICalUid(
    	String event
    ) {
    	String uid = null;
    	if(event.indexOf("UID:") > 0) {
    		int start = event.indexOf("UID:");
    		int end = event.indexOf("\n", start);
    		if(end > start) {
    			uid = event.substring(start + 4, end).trim();
    		}
    	}    	
    	return uid;
    }
        
    //-------------------------------------------------------------------------
    public void updateActivity(
        Activity activity
    ) throws ServiceException {
        if(!JDOHelper.isPersistent(activity) && JDOHelper.isNew(activity)) {
            if((activity.getDueBy() == null)) {
            	try {
            		activity.setDueBy(DateTimeFormat.BASIC_UTC_FORMAT.parse("99991231T000000.000Z"));
            	}
            	catch(Exception e) {}
            }
            if(activity.getPercentComplete() == null) {
                activity.setPercentComplete(new Short((short)0));
            }
        }
        List<String> statusMessage = new ArrayList<String>();
        String ical = ICalendar.getInstance().mergeIcal(
        	activity, 
        	activity.getIcal(), 
        	statusMessage 
        );
        activity.setIcal(
            ical == null ? "" : ical
        );
        // Assertion externalLink().contains(ical uid)
        String uid = this.getICalUid(ical);
        boolean icalLinkMatches = false;
        boolean hasICalLink = false;
        for(String externalLink: activity.getExternalLink()) {
        	if(externalLink.startsWith(ICalendar.ICAL_SCHEMA)) {
        		hasICalLink = true;
        		if(externalLink.endsWith(uid)) {
	        		icalLinkMatches = true;
	        		break;
        		}
        	}
        }
        if(!hasICalLink) {
        	activity.getExternalLink().add(
        		ICalendar.ICAL_SCHEMA + uid
        	);
        }
        else if(!icalLinkMatches) {
        	// Should not happen. Log warning.
        	// Activity must be fixed manually with updateICal() operation
        	SysLog.warning("Activity's external link does not contain ical UID", Arrays.asList(activity.refGetPath().toString(), activity.getActivityNumber()));
        	ServiceException e = new ServiceException(
        		BasicException.Code.DEFAULT_DOMAIN,
        		BasicException.Code.ASSERTION_FAILURE,
        		"Activity's external link does not contain ical UID",
        		new BasicException.Parameter("activity", activity),
        		new BasicException.Parameter("externalLink", activity.getExternalLink()),
        		new BasicException.Parameter("ical", ical)        		
        	);
        	SysLog.detail(e.getMessage(), e.getCause());
        }
    }
        
    //-------------------------------------------------------------------------
    public ResourceAssignment createResourceAssignment(
        Activity activity,
        Resource resource,
        short resourceOrder,
        List<PrincipalGroup> owningGroups
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
        ResourceAssignment resourceAssignment = pm.newInstance(ResourceAssignment.class);
        resourceAssignment.refInitialize(false, false);
        resourceAssignment.setName(
            resource.getName()
        );
        resourceAssignment.setDescription(
            (activity.getName() != null ? activity.getName() : "") +
            " (" + 
            (resource.getName() != null ? resource.getName() : "") +
            ")"
        );
        resourceAssignment.setResource(
            resource
        );
        resourceAssignment.setResourceRole(
            new Short((short)0)
        );
        resourceAssignment.setResourceOrder(
            new Short(resourceOrder)
        );                    
        resourceAssignment.setWorkDurationPercentage(
            new Short((short)100)
        );
        resourceAssignment.getOwningGroup().addAll(
        	activity.getOwningGroup()
        );
        if(owningGroups != null && !owningGroups.isEmpty()) {
        	resourceAssignment.getOwningGroup().addAll(owningGroups);
        }
        else {
        	resourceAssignment.getOwningGroup().addAll(
        		activity.getOwningGroup()
        	);
        }
        activity.addAssignedResource(
        	false,
        	this.getUidAsString(),
        	resourceAssignment
        );
        return resourceAssignment;
    }
    
    //-------------------------------------------------------------------------
    public void reapplyActivityCreator(
        Activity activity,
        ActivityCreator activityCreator
    ) throws ServiceException {
        if(activityCreator != null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);        	
        	org.opencrx.kernel.activity1.jmi1.Segment activitySegment =
        		(org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
        			activityCreator.refGetPath().getParent().getParent()
        		);
            ActivityType activityType = activityCreator.getActivityType();
            if(activityType != null) {
                // Type of activity must match defined activity class
                String activityClass = activityType.getActivityClassName() != null ? 
                	activityType.getActivityClassName() : 
                	ACTIVITY_TYPES[activityType.getActivityClass()];
                if(activityClass.equals(activity.refClass().refMofId())) {
                    // Replace owning groups. The owning groups of the activity is the
                    // the union of all owning groups of the assigned activity groups. 
                    // This way it is guaranteed that the activity can be viewed in all
                    // assigned activity groups. 
                	// In case of private folders (name ends with suffix PRIVATE_FOLDER_SUFFIX) 
                	// only add principal groups matching the activity groups' name. 
                	// This way - by default - only the creator of the activity and the
                	// owner of the activity group have access to the activity.
                    List<ActivityGroup> activityGroups = activityCreator.getActivityGroup();
                    Set<PrincipalGroup> owningGroups = new HashSet<PrincipalGroup>();
                    for(ActivityGroup activityGroup: activityGroups) {
                        List<PrincipalGroup> groups = activityGroup.getOwningGroup();
                        for(PrincipalGroup group: groups) {
                            if(activityGroup.getName().endsWith(PRIVATE_GROUP_SUFFIX)) {
                            	String activityGroupName = activityGroup.getName().substring(0, activityGroup.getName().indexOf("~"));
                            	String principalGroupName = group.getName().substring(0, group.getName().indexOf("."));
                            	if(activityGroupName.equals(principalGroupName)) {
                            		owningGroups.add(group);
                            	}
                            }
                            else {                         	
                            	owningGroups.add(group);
                            }
                        }
                    }
                    activity.getOwningGroup().clear();
                    activity.getOwningGroup().addAll(owningGroups);
                    activity.setLastAppliedCreator(activityCreator);
                    // Create GroupAssignments
                    // Remove already assigned activity groups from list to be added
                    Collection<ActivityGroupAssignment> existingGroupAssignments = activity.getAssignedGroup();
                    List<ActivityGroup> excludeActivityGroups = new ArrayList<ActivityGroup>();
                    for(ActivityGroupAssignment existingGroupAssignment: existingGroupAssignments) {
                        if(existingGroupAssignment.getActivityGroup() != null) {
                            excludeActivityGroups.add(
                                existingGroupAssignment.getActivityGroup()
                            );
                        }
                    }
                    // Add new group assignments
                    for(ActivityGroup activityGroup: activityGroups) {
                        if(!excludeActivityGroups.contains(activityGroup)) {
                            ActivityGroupAssignment activityGroupAssignment = pm.newInstance(ActivityGroupAssignment.class);
                            activityGroupAssignment.refInitialize(false, false);
                            activityGroupAssignment.setActivityGroup(activityGroup);
                            activityGroupAssignment.getOwningGroup().addAll(owningGroups);
                            activity.addAssignedGroup(
                            	false,
                            	this.getUidAsString(),
                            	activityGroupAssignment
                            );
                        }
                    }    
                    // Create ResourceAssignments
                    List<Resource> resources = new ArrayList<Resource>();
                    if(!activityCreator.getResource().isEmpty()) {
                        List<Resource> rs = activityCreator.getResource();
                        for(Resource r: rs) {
                            resources.add(r);
                        }
                    }
                    else {
                        // Try to find resource matching the current user
                    	ResourceQuery resourceQuery = (ResourceQuery)pm.newQuery(Resource.class);
                    	org.opencrx.kernel.account1.jmi1.Contact contact = 
                    		UserHomes.getInstance().getUserHome(activityCreator.refGetPath(), pm).getContact();
                    	if(contact != null) {
	                    	resourceQuery.thereExistsContact().equalTo(contact);
	                    	List<Resource> allResources = activitySegment.getResource(resourceQuery);
	                        if(!allResources.isEmpty()) {
	                            resources.add(
	                                allResources.iterator().next()
	                            );
	                        }
                    	}
                    }
                    // Remove already assigned resources from list to be added 
                    Collection<ResourceAssignment> existingResourceAssignments = activity.getAssignedResource();
                    List<Resource> excludeResources = new ArrayList<Resource>();
                    for(ResourceAssignment existingResourceAssignment: existingResourceAssignments) {
                        if(existingResourceAssignment.getResource() != null) {
                            excludeResources.add(
                                existingResourceAssignment.getResource()
                            );
                        }
                    }                    
                    int ii = 0;
                    for(Resource resource: resources) {
                        if(!excludeResources.contains(resource)) {
                            this.createResourceAssignment(
                                activity,
                                resource,
                                (short)ii,
                                null
                            );
                            ii++;
                        }
                    }
                    // Create depot references
                    Collection<DepotReference> existingDepotReferences = activity.getDepotReference();
                    List<Short> excludesDepotUsages = new ArrayList<Short>();
                    for(DepotReference existingDepotReference: existingDepotReferences) {
                        excludesDepotUsages.add(
                            new Short(existingDepotReference.getDepotUsage())
                        );
                    }                    
                    Collection<DepotReference> depotReferences = activityCreator.getDepotReference();
                    for(DepotReference depotReference: depotReferences) {
                        if(!excludesDepotUsages.contains(new Short(depotReference.getDepotUsage()))) {
                            Cloneable.getInstance().cloneObject(
                                depotReference,
                                activity,
                                "depotReference",
                                null,
                                "",
                                activity.getOwningUser(),
                                activity.getOwningGroup()
                            );
                        }
                    }                            
                    // Create PropertySet
                    Collection<PropertySet> existingPropertySets = activity.getPropertySet();
                    List<String> excludePropertySets = new ArrayList<String>();
                    for(PropertySet existingPropertySet: existingPropertySets) {
                        excludePropertySets.add(
                            existingPropertySet.getName()
                        );
                    }                    
                    Collection<PropertySet> propertySets = activityCreator.getPropertySet();
                    for(PropertySet propertySet: propertySets) {
                        if(!excludePropertySets.contains(propertySet.getName())) {
                            Cloneable.getInstance().cloneObject(
                                propertySet,
                                activity,
                                "propertySet",
                                null,
                                "property",
                                activity.getOwningUser(),
                                activity.getOwningGroup()
                            );
                        }
                    }                            
                    // Set processState, lastTransition
                    activity.setActivityType(activityType);
                    activity.setProcessState(null);
                    activity.setLastTransition(null);
                    if(activityType.getControlledBy() != null) {
                        ActivityProcess activityProcess = activityType.getControlledBy();
                        // Try to find transition which most closely matches the current activity
                        // completeness and state. If no transition can be found set to start transition.
                        ActivityProcessTransition lastTransition = null;
                        ActivityProcessState processState = null;
                        if(activity.getPercentComplete() != null) {
                        	ActivityProcessTransitionQuery transitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
                        	transitionQuery.thereExistsNewPercentComplete().equalTo(activity.getPercentComplete());
                            List<ActivityProcessTransition> transitions = activityProcess.getTransition(transitionQuery);
                            if(!transitions.isEmpty()) {
                                lastTransition = transitions.iterator().next();
                                processState = lastTransition.getNextState();
                            }
                        }
                        if(lastTransition == null) {
                        	ActivityProcessTransitionQuery transitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
                        	transitionQuery.newActivityState().equalTo(activity.getActivityState());
                            List<ActivityProcessTransition> transitions = activityProcess.getTransition(transitionQuery); 
                            if(!transitions.isEmpty()) {
                                lastTransition = transitions.iterator().next();
                                processState = lastTransition.getNextState();
                            }
                        }
                        if(lastTransition == null) {
                            lastTransition = null;
                            processState = activityProcess.getStartState();
                        }
                        if(processState != null) {
                            activity.setProcessState(processState);
                        }
                        if(lastTransition != null) {
                            activity.setLastTransition(lastTransition);
                            activity.setPercentComplete(lastTransition.getNewPercentComplete());
                            activity.setActivityState(lastTransition.getNewActivityState());
                        }
                    }
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void assignTo(
        Activity activity,
        Resource resource
    ) throws ServiceException {
        if(resource != null) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);        	
            Contact contact = resource.getContact();
            Collection<ResourceAssignment> resourceAssignments = activity.getAssignedResource(); 
            boolean hasAssignment = false;
            // Try to find resource which matches specified contact
            for(ResourceAssignment resourceAssignment: resourceAssignments) {
                if(resourceAssignment.getResource() != null) {
                    Resource assignedResource = resourceAssignment.getResource();
                    if(
                        (assignedResource.getContact() != null) &&
                        contact.equals(assignedResource.getContact())
                    ) {
                        hasAssignment = true;
                        break;
                    }
                }
            }
            // Create a resource assignment
            if(!hasAssignment) {
                ResourceAssignment resourceAssignment = pm.newInstance(ResourceAssignment.class);
                resourceAssignment.refInitialize(false, false);
                resourceAssignment.setName(resource.getName());
                resourceAssignment.setDescription(
                    "#" + (activity.getActivityNumber() == null ? "" : activity.getActivityNumber()) + ": " + (activity.getName() == null ? "" : activity.getName())
                );
                resourceAssignment.setResource(resource);
                resourceAssignment.setResourceRole((short)0);
                resourceAssignment.setResourceOrder((short)0);
                resourceAssignment.setWorkDurationPercentage((short)100);
                activity.addAssignedResource(
                	false,
                	this.getUidAsString(),
                	resourceAssignment
                );
            }
            activity.setAssignedTo(contact);
        }
    }

    //-------------------------------------------------------------------------
    public void updateIcal(
        Activity activity
    ) throws ServiceException {
        List<String> messages = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();
        List<String> report = new ArrayList<String>();
        String ical = ICalendar.getInstance().mergeIcal(
            activity,
            activity.getIcal(), 
            messages
        );        
        byte[] item = null;
        try {
            item = ical.getBytes("UTF-8");
        } 
        catch(Exception e) {
            item = ical.getBytes();    
        }
        ICalendar.getInstance().importItem(
            item, 
            activity, 
            (short)0, 
            errors, 
            report
        );
    }

    //-------------------------------------------------------------------------
    protected void calcTotalQuantity(
    	List<WorkAndExpenseRecord> workAndExpenseRecords,
        List<BigDecimal> totalQuantities,
        List<Uom> quantityUoms
    ) {
        for(WorkAndExpenseRecord workAndExpenseRecord: workAndExpenseRecords) {
        	boolean found = false;
        	int ii = 0;
        	for(Iterator<Uom> i = quantityUoms.iterator(); i.hasNext(); ii++) {
        		Uom quantityUom = i.next();
            	BigDecimal uomScaleFactor = Utils.getUomScaleFactor(
            		workAndExpenseRecord.getQuantityUom(),
            		quantityUom        		
            	);
            	if(uomScaleFactor.compareTo(BigDecimal.ONE) >= 0) {
                    totalQuantities.set(
                    	ii,
	                    totalQuantities.get(ii).add(
	                        workAndExpenseRecord.getQuantity().multiply(uomScaleFactor)
	                    )
	                );
                    found = true;
                    break;
            	}
            	else if(uomScaleFactor.compareTo(BigDecimal.ZERO) > 0) {
            		totalQuantities.set(
            			ii,
            			totalQuantities.get(ii).divide(uomScaleFactor, RoundingMode.FLOOR)
            		);
            		quantityUoms.set(
            			ii,
            			workAndExpenseRecord.getQuantityUom()
            		);
            		totalQuantities.set(
            			ii,
            			totalQuantities.get(ii).add(
            				workAndExpenseRecord.getQuantity()
            			)
            		);
            		found = true;
            		break;
            	}            	
        	}
        	if(!found && workAndExpenseRecord.getQuantityUom() != null) {
        		totalQuantities.add(
        			workAndExpenseRecord.getQuantity()
        		);
        		quantityUoms.add(
        			workAndExpenseRecord.getQuantityUom()
        		);        		
        	}
        }
    }

    //-------------------------------------------------------------------------
    public void calcTotalQuantity(
        Activity activity,
        short recordType,
        Date startAt,
        Date endAt,
        List<BigDecimal> totalQuantities,
        List<Uom> quantityUoms
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);    	
    	WorkAndExpenseRecordQuery workAndExpenseRecordQuery = (WorkAndExpenseRecordQuery)pm.newQuery(WorkAndExpenseRecord.class);
    	workAndExpenseRecordQuery.recordType().equalTo(recordType);
    	if(startAt != null) {
    		workAndExpenseRecordQuery.thereExistsEndedAt().greaterThanOrEqualTo(startAt);
    	}
    	if(endAt != null) {
    		workAndExpenseRecordQuery.thereExistsStartedAt().lessThanOrEqualTo(endAt);
    	}
    	List<WorkAndExpenseRecord> workAndExpenseRecords = activity.getWorkReportEntry(workAndExpenseRecordQuery);
        this.calcTotalQuantity(
        	workAndExpenseRecords, 
        	totalQuantities, 
        	quantityUoms
        );
    }
    
    //-------------------------------------------------------------------------
    public void calcTotalQuantity(
        Resource resource,
        short recordType,
        Date startAt,
        Date endAt,
        List<BigDecimal> totalQuantities,
        List<Uom> quantityUoms
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(resource);    	
    	WorkAndExpenseRecordQuery workReportQuery = (WorkAndExpenseRecordQuery)pm.newQuery(WorkAndExpenseRecord.class);
    	workReportQuery.recordType().equalTo(recordType);
    	if(startAt != null) {
    		workReportQuery.thereExistsEndedAt().greaterThanOrEqualTo(startAt);
    	}
    	if(endAt != null) {
    		workReportQuery.thereExistsStartedAt().lessThanOrEqualTo(endAt);
    	}
    	List<WorkAndExpenseRecord> workAndExpenseRecords = resource.getWorkReportEntry(workReportQuery);
        this.calcTotalQuantity(
        	workAndExpenseRecords, 
        	totalQuantities, 
        	quantityUoms
        );
    }
    
    //-------------------------------------------------------------------------
    public void calcTotalQuantity(
        org.opencrx.kernel.activity1.jmi1.ActivityGroup activityGroup,
        short recordType,
        Date startAt,
        Date endAt,
        List<BigDecimal> totalQuantities,
        List<Uom> quantityUoms
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activityGroup);    	    	
    	WorkAndExpenseRecordQuery workReportQuery = (WorkAndExpenseRecordQuery)pm.newQuery(WorkAndExpenseRecord.class);
    	workReportQuery.recordType().equalTo(recordType);
    	if(startAt != null) {
    		workReportQuery.thereExistsEndedAt().greaterThanOrEqualTo(startAt);
    	}
    	if(endAt != null) {
    		workReportQuery.thereExistsStartedAt().lessThanOrEqualTo(endAt);
    	}    	
        List<WorkAndExpenseRecord> workAndExpenseRecords = activityGroup.getWorkReportEntry(workReportQuery);
        this.calcTotalQuantity(
        	workAndExpenseRecords, 
        	totalQuantities, 
        	quantityUoms
        );
    }
    
    //-------------------------------------------------------------------------
    public void calcTotalQuantity(
        org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity activityFilter,
        short recordType,
        Date startAt,
        Date endAt,        
        List<BigDecimal> totalQuantities,
        List<Uom> quantityUoms
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activityFilter);    	
    	ActivityQuery query = (ActivityQuery)pm.newQuery(Activity.class);
    	List<Activity> activities = activityFilter.getFilteredActivity(query);
        for(Activity activity: activities) {
        	WorkAndExpenseRecordQuery workReportQuery = (WorkAndExpenseRecordQuery)pm.newQuery(WorkAndExpenseRecord.class);
        	workReportQuery.recordType().equalTo(recordType);
        	if(startAt != null) {
        		workReportQuery.thereExistsEndedAt().greaterThanOrEqualTo(startAt);
        	}
        	if(endAt != null) {
        		workReportQuery.thereExistsStartedAt().lessThanOrEqualTo(endAt);
        	}        	
        	List<WorkAndExpenseRecord> workReportEntries = activity.getWorkReportEntry(workReportQuery);
            this.calcTotalQuantity(
            	workReportEntries, 
            	totalQuantities, 
            	quantityUoms
            );
        }
    }
        
    //-------------------------------------------------------------------------
    public Object[] calcMainEffortEstimate(
        Activity activity
    ) throws ServiceException {
        Collection<EffortEstimate> estimates = activity.getEffortEstimate();
        BigDecimal estimateEffortHours = BigDecimal.ZERO;
        BigDecimal effortEstimateMinutes = BigDecimal.ZERO;
        for(EffortEstimate estimate: estimates) {
            if(
                (estimate.isMain() != null) &&
                estimate.isMain().booleanValue()
            ) {
                estimateEffortHours = new BigDecimal(estimate.getEstimateEffortHours());
                effortEstimateMinutes = new BigDecimal(estimate.getEstimateEffortMinutes());    
                break;
            }
        }
        int hours = Math.abs(estimateEffortHours.intValue() + (effortEstimateMinutes.intValue() / 60));
        int minutes = Math.abs(effortEstimateMinutes.intValue() % 60);
        boolean isNegative = 
            estimateEffortHours.intValue() < 0 || 
            effortEstimateMinutes.intValue() < 0;
        return new Object[]{
        	hours,
        	minutes,
        	(isNegative ? "-" : "") + hours + ":" + (minutes < 10 ? "0" + minutes : "" + minutes) + "'"
        };
    }
    
    //-------------------------------------------------------------------------
    public int countFilteredActivity(
        org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity activityFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activityFilter);
    	ActivityQuery query = (ActivityQuery)pm.newQuery(Activity.class);
    	org.openmdx.base.query.Extension queryExtension = PersistenceHelper.newQueryExtension(query);
    	queryExtension.setClause(
    		Database_1_Attributes.HINT_COUNT + "(1=1)"
    	);
    	List<Activity> activities = activityFilter.getFilteredActivity(query);
        return activities.size();
    }
    
    //-----------------------------------------------------------------------
    /**
     * Creates and adds an email recipient to the email activity.
     * 
     * @param emailActivity    The openCRX EMailActivity currently in process
     * @param address          The email address object
     * @param type             The address type (TO, CC, BCC)
     */
    public void addEmailRecipient(
        PersistenceManager pm,
        EMail emailActivity,
        EMailAddress address,
        Message.RecipientType type
    ) {
        boolean isTxLocal = !pm.currentTransaction().isActive();
        if(isTxLocal) {
        	pm.currentTransaction().begin();
        }
        EMailRecipient recipient = pm.newInstance(EMailRecipient.class);
        recipient.refInitialize(false, false);
        emailActivity.addEmailRecipient(
            false,
            UUIDs.getGenerator().next().toString(),
            recipient
        );
        recipient.setParty(address);
        if(Message.RecipientType.TO.toString().equalsIgnoreCase(type.toString())) {
            recipient.setPartyType(PARTY_TYPE_TO);
        }
        else if(Message.RecipientType.CC.toString().equalsIgnoreCase(type.toString())) {
            recipient.setPartyType(PARTY_TYPE_CC);
        }
        else if(Message.RecipientType.BCC.toString().equalsIgnoreCase(type.toString())) {
            recipient.setPartyType(PARTY_TYPE_BCC);
        }
        // 'copy' the email's owning groups
        recipient.getOwningGroup().addAll(
            emailActivity.getOwningGroup()
        );
        if(isTxLocal) {
        	pm.currentTransaction().commit();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Adds a Media object to the currently processed email activity with the
     * given content. This is how attachments of email messages can be imported.
     * 
     * @param rootPkg          The root package to be used for this request
     * @param emailActivity    The openCRX EMailActivity currently in process
     * @param content          The content of the media object, e.g. an
     *                          attachment
     */
    public void createOrUpdateMedia(
        EMail emailActivity,
        String contentType,
        String contentName,
        InputStream content
    ) throws IOException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(emailActivity);
        Collection<Media> medias = emailActivity.getMedia();
        Media media = null;
        for(Media attachment: medias) {
            if((contentName != null) && contentName.equals(attachment.getContentName())) {
                media = attachment;
                break;
            }
        }
        if(media == null) {    	    	
	        media = Utils.getGenericPackage(pm).getMedia().createMedia();
	        media.refInitialize(false, false);
	        emailActivity.addMedia(
	            false,
	            UUIDs.getGenerator().next().toString(),
	            media
	        );
	        media.setContentName(
	            contentName == null ?
	                Utils.toFilename(contentType) :
	                contentName
	        );
        }
        media.setContentMimeType(contentType);    
        media.setContent(
            BinaryLargeObjects.valueOf(content)
        );
        // 'copy' the email's owning groups
        media.getOwningGroup().addAll(
            emailActivity.getOwningGroup()
        );
    }
    
    //-----------------------------------------------------------------------
    /**
     * Adds an email recipient to the currently processed email activity if
     * the email message contains an email address which is contained in an
     * openCRX account. Email addresses for which no account can be found, are
     * recorded via a note attached to the email activity.
     * 
     * @param rootPkg                   The root package to be used for this request
     * @param emailActivity             The EMailActivity currently in process
     * @param addresses                 A list of addresses
     * @param type                      The address type (TO, CC, BCC)
     */
    public void mapAddressesToEMailRecipients(
        EMail emailActivity,
        String[] addresses,
        Message.RecipientType type
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(emailActivity);
    	String providerName = emailActivity.refGetPath().get(2);
    	String segmentName = emailActivity.refGetPath().get(4);
        if (addresses == null || addresses.length == 0) {
        	SysLog.trace("Message does not contain any recipient of type '" + type.toString() + "'");
        }
        Set<String> newAddresses = new HashSet<String>(Arrays.asList(addresses));
        newAddresses.remove("NO_ADDRESS_SPECIFIED");
        Collection<AbstractEMailRecipient> recipients = emailActivity.getEmailRecipient();
        for(AbstractEMailRecipient recipient: recipients) {
            if(recipient instanceof EMailRecipient) {
                EMailAddress address = (EMailAddress)((EMailRecipient)recipient).getParty();
                if((address != null) && (address.getEmailAddress() != null)) {
                    newAddresses.remove(address.getEmailAddress());
                }
            }
        }
        for(String address: newAddresses) {
            List<org.opencrx.kernel.account1.jmi1.EMailAddress> emailAddresses = 
                Accounts.getInstance().lookupEmailAddress(
                    pm,
                    providerName,
                    segmentName,
                    address
                );
            if(emailAddresses.isEmpty()) {
                emailAddresses = Accounts.getInstance().lookupEmailAddress(
                    pm,
                    providerName,
                    segmentName,
                    Addresses.UNASSIGNED_ADDRESS
                );
            }
            if(!emailAddresses.isEmpty()) {
                this.addEmailRecipient(
                    pm, 
                    emailActivity, 
                    emailAddresses.iterator().next(), 
                    type
                );
            }
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Search email activity with the given external link, i.e. the given
     * message id.
     * 
     * @param rootPkg          The root package to be used for this request
     * @param providerName     The name of the current provider
     * @param segmentName      The name of the current segment
     * @param externalLink     The message id
     * @return                 A List of activities containing the message id
     */
    public List<org.opencrx.kernel.activity1.jmi1.Activity> lookupEmailActivity(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String externalLink
    ) {
        if(externalLink == null) {
            return Collections.emptyList();
        }
        else {
            EMailQuery query = Utils.getActivityPackage(pm).createEMailQuery();
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment =
                this.getActivitySegment(
                    pm,
                    providerName,
                    segmentName
                );
            query.thereExistsExternalLink().equalTo(
                externalLink  
            );
            return activitySegment.getActivity(query);
        }
    }
        
    //-----------------------------------------------------------------------
    /**
     * @return Returns the activitySegment.
     */
    public org.opencrx.kernel.activity1.jmi1.Segment getActivitySegment(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.opencrx.kernel.activity1.jmi1.Segment) pm.getObjectById(
            new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
        );
    }

	//-----------------------------------------------------------------------
	public org.opencrx.kernel.activity1.jmi1.Resource findResource(
		org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
		UserHome userHome
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(activitySegment);
		String providerName = activitySegment.refGetPath().get(2);
		String segmentName = activitySegment.refGetPath().get(4);
		// Resource
		org.opencrx.kernel.activity1.jmi1.Resource resource = null;
		try {
			resource = (org.opencrx.kernel.activity1.jmi1.Resource)pm.getObjectById(
				new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/resource/" + userHome.refGetPath().getBase())
			);
		}
		catch(Exception e) {}
		if(resource == null) {
			try {
				ResourceQuery query = (ResourceQuery)pm.newQuery(Resource.class);
				query.thereExistsContact().equalTo(userHome.getContact());
				query.orderByName().ascending();
				Collection<Resource> resources = activitySegment.getResource(query);
				for(Resource candidate: resources) {
					if(
						candidate.getName().equals(userHome.refGetPath().getBase()) ||
						candidate.getName().equals(userHome.getContact().getFullName())
					) {
						resource = candidate;
						break;
					}
				}
				if(resource == null) {
					resource = resources.iterator().next();
				}
			}
			catch(Exception e) {}
		}
		return resource;
	}
    
    //-----------------------------------------------------------------------
    /**
     * Formats a text containing all the addresses of the different types
     * (i.e., TO, CC, BCC) to be attached to the email activity by a note
     * indicating whether an account containing the email address could be
     * found.
     * 
     * @param rootPkg          The root package to be used for this request
     * @param providerName     The name of the current provider
     * @param segmentName      The name of the current segment
     * @param mimeMsg          The email to be imported as openCRX EMailActivity
     * @return                 The formatted content for the 'Recipients' note
     */
    public String getRecipientsAsNoteText(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String[] from,
        String[] to,
        String[] cc,
        String[] bcc
    ) throws ServiceException {
        StringBuffer text = new StringBuffer();  

        // add 'FROM's to the note
        String addresses[] = from;
        for (int i = 0; i < addresses.length; i++) {
        	if(!"NO_ADDRESS_SPECIFIED".equalsIgnoreCase(addresses[i])) {
	            List<org.opencrx.kernel.account1.jmi1.EMailAddress> emailAddresses = 
	                Accounts.getInstance().lookupEmailAddress(
	                    pm,
	                    providerName,
	                    segmentName,
	                    addresses[i]
	                );
	            text.append(
	            	"FROM: " + 
	            	addresses[i] + 
	            	" [" + 
	            	((emailAddresses == null || emailAddresses.size() == 0) ? "UNMATCHED" : "MATCHED") + "]\n"
	            );
        	}
        }
  
        // add 'TO's to the note
        addresses = to;
        for (int i = 0; i < addresses.length; i++) {
        	if(!"NO_ADDRESS_SPECIFIED".equalsIgnoreCase(addresses[i])) {
	            List<org.opencrx.kernel.account1.jmi1.EMailAddress> emailAddresses = 
	                Accounts.getInstance().lookupEmailAddress(
	                    pm,
	                    providerName,
	                    segmentName,
	                    addresses[i]
	                );
	            text.append(
	            	"TO: " + 
	            	addresses[i] + 
	            	" [" + 
	            	((emailAddresses == null || emailAddresses.size() == 0) ? "UNMATCHED" : "MATCHED") + "]\n"
	            );
        	}
        }
  
        // add 'CC's to the note
        addresses = cc;
        for (int i = 0; i < addresses.length; i++) {
        	if(!"NO_ADDRESS_SPECIFIED".equalsIgnoreCase(addresses[i])) {
	            List<org.opencrx.kernel.account1.jmi1.EMailAddress> emailAddresses = 
	                Accounts.getInstance().lookupEmailAddress(
	                    pm,
	                    providerName,
	                    segmentName,
	                    addresses[i]
	                );
	            text.append(
	            	"CC: " + 
	            	addresses[i] + 
	            	" [" + 
	            	((emailAddresses == null || emailAddresses.size() == 0) ? "UNMATCHED" : "MATCHED") + "]\n"
	            );
        	}
        }
  
        // add 'BCC's to the note
        addresses = bcc;
        for (int i = 0; i < addresses.length; i++) {
        	if(!"NO_ADDRESS_SPECIFIED".equalsIgnoreCase(addresses[i])) {
	            List<org.opencrx.kernel.account1.jmi1.EMailAddress> emailAddresses = 
	                Accounts.getInstance().lookupEmailAddress(
	                    pm,
	                    providerName,
	                    segmentName,
	                    addresses[i]
	                );
	            text.append(
	            	"BCC: " + 
	            	addresses[i] + 
	            	" [" + 
	            	((emailAddresses == null || emailAddresses.size() == 0) ? "UNMATCHED" : "MATCHED") + "]\n"
	            );
        	}
        }
        return text.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a note to the currently processed email activity.
     * 
     * @param rootPkg          The root package to be used for this request
     * @param emailActivity    The openCRX EMailActivity currently in process
     * @param title            The note's title
     * @param content          The note's content
     */
    public void addNote(
        PersistenceManager pm,
        EMail emailActivity,
        String title,
        String content
    ) {
    	boolean isTxLocal = !pm.currentTransaction().isActive();
    	if(isTxLocal) {
    		pm.currentTransaction().begin();
    	}
        Note note = pm.newInstance(Note.class);
        note.refInitialize(false, false);
        emailActivity.addNote(
            false,
            UUIDs.getGenerator().next().toString(),
            note
        );
        note.setTitle(title);
        note.setText(content);
        // 'copy' the email's owning groups
        note.getOwningGroup().addAll(
            emailActivity.getOwningGroup()
        );
        if(isTxLocal) {
        	pm.currentTransaction().commit();
        }
    }
    
    //-------------------------------------------------------------------------  
    /**
     * Extract the priority from the email message. Note that if no header
     * element is found this indicates a "normal" priority. Note that rfc822
     * does not define a standard header field for priority. The name of the
     * "priority" header field depends on your mail client used. "Importance"
     * with values high, normal and low "Priority" with values Urgent and
     * Non-Urgent "X-Priority" with values 1=high and 5=low These values are
     * mapped to:
     * <UL>
     * <LI>ACTIVITY_PRIORITY_LOW,
     * <LI>ACTIVITY_PRIORITY_NORMAL and
     * <LI>ACTIVITY_PRIORITY_HIGH
     * </UL>
     * respectively.
     * 
     * @return the subject of the message
     */
    public short getMessagePriority(
        Message message
    ) throws MessagingException {
        String priority = "normal";
        short priorityAsShort = PRIORITY_NORMAL;
        String[] values = message.getHeader("Importance");
        if (values != null && values.length > 0) {
            priority = values[0];
        }
        values = message.getHeader("X-Priority");
        if (values != null && values.length > 0) {
            priority = values[0];
        }
        values = message.getHeader("Priority");
        if (values != null && values.length > 0) {
            priority = values[0];
        }
        if (priority.equalsIgnoreCase("normal") || priority.equalsIgnoreCase("3")) {
            priorityAsShort = PRIORITY_NORMAL;
        } 
        else if (priority.equalsIgnoreCase("high")
                || priority.equalsIgnoreCase("1")
                || priority.equalsIgnoreCase("Urgent")) {
            priorityAsShort = PRIORITY_HIGH;
        } 
        else if (priority.equalsIgnoreCase("low")
                || priority.equalsIgnoreCase("5")
                || priority.equalsIgnoreCase("Non-Urgent")) {
            priorityAsShort = PRIORITY_LOW;
        }
        return priorityAsShort;
    }
    
    //-------------------------------------------------------------------------      
    private static Part getFirstTextPart(
        Object content
    ) throws MessagingException, IOException {
        if(content instanceof MimeMultipart) {
            MimeMultipart multipartMessage = (MimeMultipart)content;
            // Try to find a part with mimeType text/plain
            for(int i = 0; i < multipartMessage.getCount(); i++) {
                Part part = multipartMessage.getBodyPart(i);
                if(part.isMimeType("text/plain")) {
                    return part;
                }
                else if(part.getContent() instanceof MimeMultipart) {
                    return Activities.getFirstTextPart(part.getContent());
                }
            }
            return multipartMessage.getCount() > 0 ?
                multipartMessage.getBodyPart(0) :
                null;
        }
        else if(content instanceof Part) {
            Object c = ((Part)content).getContent();
            return c instanceof MimeMultipart ?
            	Activities.getFirstTextPart(c) :
                (Part)content;
        }  
        else {
            return null;
        }
    }
    
    //-------------------------------------------------------------------------      
    public String getMessageBody(
        MimePart messagePart
    ) throws IOException, MessagingException {
        Part part = Activities.getFirstTextPart(messagePart);
        if(part == null) return null;
        Object content = part.getContent();
        if(content instanceof String) {
            return (String)content;
        }
        else if (content instanceof InputStream) {
            if(part.isMimeType("text/plain") || part.isMimeType("text/html")) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    part.getInputStream())
                );
                StringBuffer body = new StringBuffer();
                while (in.ready()) {
                    body.append(in.readLine());
                    if(in.ready()) {
                        body.append(System.getProperty("line.separator", "\n"));
                    }
                }
                return body.toString();
            }
            else {
                return "";
            }
        }
        return null;
    }

    //-------------------------------------------------------------------------
    public boolean isAllAscii(
        String s
    ) {
        int ascii = 0, nonAscii = 0;
        int l = s.length();
        for(int i = 0; i < l; i++) {
            char c = s.charAt(i);
            boolean isNonAscii = (c >= 0177) || (c < 040 && c != '\r' && c != '\n' && c != '\t');
            if(isNonAscii) {
                nonAscii++;
            }
            else {
                ascii++;
            }
        }        
        return nonAscii == 0;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Maps email activity to message. If email activity has a media attachment
     * which contains the original MimeMessage the stream of this message is
     * returned in addition.
     */
    public InputStream mapMessageContent(
        EMail email,
        Message message
    ) throws MessagingException {
        String originalMessageMediaName = email.getActivityNumber().trim() + ".eml.zip";
        InputStream originalMessageStream = null;
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String text = email.getMessageBody();
        text = text == null ? "" : text;
        if(text.startsWith("<!DOCTYPE html")) {
            String charset = null;
            if (!this.isAllAscii(text)) {
                charset = MimeUtility.getDefaultJavaCharset();
            }
            else {
                charset = "us-ascii";                
            }
            messageBodyPart.setContent(
                text, 
                "text/html; charset=" + MimeUtility.quote(charset, HeaderTokenizer.MIME)
            );        
        }
        else {
            messageBodyPart.setText(text);            
        }
        multipart.addBodyPart(messageBodyPart);    
        Collection<org.opencrx.kernel.generic.jmi1.Media> medias = email.getMedia();
        for(org.opencrx.kernel.generic.jmi1.Media media: medias) {
            if(media.getContentName() != null) {
                try {
                    QuotaByteArrayOutputStream mediaContent = new QuotaByteArrayOutputStream(Activities.class.getName());
                    try {
                        InputStream is = media.getContent().getContent();
                        BinaryLargeObjects.streamCopy(is, 0L, mediaContent);
                    }
                    catch(Exception e) {
                    	SysLog.warning("Unable to get media content (see detail for more info)", e.getMessage());
                    	SysLog.info(e.getMessage(), e.getCause());
                    }
                    mediaContent.close();
                    // Test whether media is zipped original mail. 
                    // If yes return as original message
                    if(originalMessageMediaName.equals(media.getContentName())) {
                        ZipInputStream zippedMessageStream = new ZipInputStream(mediaContent.toInputStream());
                        zippedMessageStream.getNextEntry();
                        originalMessageStream = zippedMessageStream;
                    }
                    InternetHeaders headers = new InternetHeaders();
                    headers.addHeader("Content-Type", media.getContentMimeType() + "; name=\"" + MimeUtility.encodeText(media.getContentName()) + "\"");
                    headers.addHeader("Content-Disposition", "attachment");
                    headers.addHeader("Content-Transfer-Encoding", "base64");
                    messageBodyPart = new MimeBodyPart(                        
                        headers,
                        Base64.encode(mediaContent.getBuffer(), 0, mediaContent.size()).getBytes("US-ASCII")
                    );
                    multipart.addBodyPart(messageBodyPart);
                }
                catch(Exception e) {
                    new ServiceException(e).log();
                }
            }
        }
        message.setContent(multipart);
        return originalMessageStream;
    }
    
    //-------------------------------------------------------------------------
    public String getInternetAddress(
        AccountAddress address,
        String gateway
    ) {
        if(address instanceof EMailAddress) {
            return ((EMailAddress)address).getEmailAddress();
        }
        else if(address instanceof PhoneNumber) {
            String phoneNumber = ((PhoneNumber)address).getPhoneNumberFull();
            StringBuilder inetAddress = new StringBuilder();
            for(int i = 0; i < phoneNumber.length(); i++) {
                char c = phoneNumber.charAt(i);
                if((c == '+') && (inetAddress.length() == 0)) {
                    inetAddress.append("_");
                }
                else if(Character.isLetterOrDigit(c)) {
                    inetAddress.append(Character.toUpperCase(c));
                }
            }
            if((gateway != null) && (gateway.indexOf("@") > 0)) {
                inetAddress.append(
                    gateway.substring(gateway.indexOf("@"))
                );
            }
            return inetAddress.toString();
        }
        else {
            return null;
        }
    }
    
    //-------------------------------------------------------------------------
    public List<Address> mapMessageRecipients(
        org.opencrx.kernel.activity1.jmi1.EMail emailActivity
    ) throws AddressException, MessagingException {
    	return this.mapMessageRecipients(emailActivity, null);
    }
    
    //-------------------------------------------------------------------------
    public List<Address> mapMessageRecipients(
        org.opencrx.kernel.activity1.jmi1.EMail email,
        Message message            
    ) throws AddressException, MessagingException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(email);
        String gateway = email.getGateway() == null ?
            null : 
            email.getGateway().getEmailAddress();
        List<Address> recipients = new ArrayList<Address>();        
        AccountAddress sender = null;
        try {
            sender = email.getSender();
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
        }
        if(sender != null) {
            String inetAddress = this.getInternetAddress(
                sender,
                gateway
            );
            if(inetAddress != null) {
            	if(message != null) {
	                message.setFrom(
	                    new InternetAddress(inetAddress)
	                );
            	}
            }
        }
        Collection<AbstractEMailRecipient> emailRecipients = email.getEmailRecipient();
        for(AbstractEMailRecipient recipient: emailRecipients) {
            RecipientType recipientType = null;
            if(recipient.getPartyType() == PARTY_TYPE_TO) {
                recipientType = RecipientType.TO;
            }
            else if(recipient.getPartyType() == PARTY_TYPE_CC) {
                recipientType = RecipientType.CC;
            }
            else if(recipient.getPartyType() == PARTY_TYPE_BCC) {
                recipientType = RecipientType.BCC;
            }
            if(recipientType != null) {
                if(recipient instanceof EMailRecipient) {
                	AccountAddress address = null;
                	try {
                		address = ((EMailRecipient)recipient).getParty();
                	}
                	catch(Exception e) {
                		new ServiceException(e).log();
                	}
                	if((address != null) && !Boolean.TRUE.equals(address.isDisabled())) {
	                    String inetAddress = null;
	                    try {
	                        inetAddress = this.getInternetAddress(
	                            address,
	                            gateway
	                        );
	                    } 
	                    catch(Exception e) {
	                        ServiceException e0 = new ServiceException(e);
	                        SysLog.detail(e0.getMessage(), e0.getCause());
	                    }
	                    if(inetAddress != null) {
	                        try {
	                            Address to = new InternetAddress(inetAddress);
	                            recipients.add(to);
	                            if(message != null) {
		                            message.addRecipient(
		                                recipientType,
		                                to
		                            );
	                            }
	                        }
	                        catch(Exception e) {
	                        	SysLog.warning("Invalid recipient", Arrays.asList(email, inetAddress));
	                        }
	                    }
                	}
                }
                else if(recipient instanceof EMailRecipientGroup) {
                    EMailRecipientGroup recipientGroup = (EMailRecipientGroup)recipient;
                	AddressGroup addressGroup = null;
                	try {
                		addressGroup = recipientGroup.getParty();
                	}
                	catch(Exception e) {
                		new ServiceException(e).log();
                	}                    
                    AddressGroupMemberQuery addressGroupMemberQuery = (AddressGroupMemberQuery)pm.newQuery(AddressGroupMember.class);
                    addressGroupMemberQuery.forAllDisabled().isFalse();
                    if((addressGroup != null) && !Boolean.TRUE.equals(addressGroup.isDisabled())) {
	                    Collection<AddressGroupMember> members = addressGroup.getMember(addressGroupMemberQuery);
	                    for(AddressGroupMember member: members) {
	                    	AccountAddress address = null;
	                    	try {
	                    		address = member.getAddress();
	                    	} catch(Exception e) {}                        		
	                        if(
	                        	(address != null) &&
	                        	!Boolean.TRUE.equals(address.isDisabled())
	                        ) {
	                            String inetAddress = this.getInternetAddress(
	                                address,
	                                gateway
	                            );
	                            if(inetAddress != null) {
	                                try {
	                                    Address to = new InternetAddress(inetAddress);
	                                    recipients.add(to);
	                                    if(message != null) {
		                                    message.addRecipient(
		                                        recipientType,
		                                        to
		                                    );
	                                    }
	                                }
	                                catch(Exception e) {
	                                	SysLog.warning("Invalid recipient", Arrays.asList(email, inetAddress));
	                                }
	                            }
	                        }
	                    }
                    }
                }
            }
        }        
        return recipients;        
    }
    
    //-------------------------------------------------------------------------
    /**
     * Maps email to mime message. Either returns message or an input stream
     * which contains a mime message.
     */
    public Object mapToMessage(
        EMail email,
        Message message
    ) throws MessagingException {
        try {
            InputStream messageStream = this.mapMessageContent(
                email, 
                message
            );
            if(messageStream != null) {
                return messageStream;
            }
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }
        try {
        	this.mapMessageRecipients(
                email, 
                message
            );
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }
        message.setSubject(
        	email.getMessageSubject() != null ?
        		email.getMessageSubject() :
        			email.getName()
        );
        message.setHeader(
            "Date", 
            DATEFORMAT_MESSAGE_HEADER.format(
                email.getSendDate() != null ? 
                    email.getSendDate() : 
                    	email.getCreatedAt()
            )
        );
        return message;
    }
        
    //-----------------------------------------------------------------------
    public ActivityCreator findActivityCreator(
        Collection<ActivityCreator> activityCreators,
        short activityClass
    ) {
        for(ActivityCreator creator: activityCreators) {
            if(
                (creator.getActivityType() != null) && 
                (creator.getActivityType().getActivityClass() == activityClass)
            ) {
                return creator;
            }
        }
        return null;
    }

    //-------------------------------------------------------------------------
    public void removeActivity(
        Activity activity,
        boolean preDelete
    ) throws ServiceException {
    }

    //-------------------------------------------------------------------------
    public String[] getInternetAddresses(
        javax.mail.Address[] addresses
    ) throws AddressException {
        String internetAddresses[] = null;
        if (addresses != null && addresses.length > 0) {
            internetAddresses = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                if (addresses[0] instanceof InternetAddress) {
                    internetAddresses[i] = ((InternetAddress)addresses[i]).getAddress();
                } 
                else {
                    InternetAddress temp = new InternetAddress(addresses[i].toString());
                    internetAddresses[i] = temp.getAddress();
                }
            }
        } 
        else {
            internetAddresses = new String[]{UNSPECIFIED_ADDRESS};
        }
        return internetAddresses;
    }
    
    //-----------------------------------------------------------------------
    public String[] parseContentType(
        String contentType
    ) {
        String[] result = new String[2];
        Pattern pattern = Pattern.compile("([0-9a-zA-Z/\\+\\-\\.]+)(?:;(?:[ \\r\\n\\t]*)name(?:[^\\=]*)\\=\"(.*)\")?");
        Matcher matcher = pattern.matcher(contentType);
        if(matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        }
        else {
            result[0] = contentType;
            result[1] = null;                            
        }
        return result;
    }
    
    //-----------------------------------------------------------------------
    public void addAttachments(
        MimeMessage mimeMessage,
        EMail email
    ) throws IOException, MessagingException, ServiceException {
        if(mimeMessage.getContent() instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart)mimeMessage.getContent();
            for(int i = 1; i < multipart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart)multipart.getBodyPart(i);
                String[] contentType = this.parseContentType(part.getContentType());
                if(contentType[1] == null) contentType[1] = part.getContentID();
            	PersistenceManager pm = JDOHelper.getPersistenceManager(email);
            	boolean isTxLocal = !pm.currentTransaction().isActive();
            	if(isTxLocal) {
            		pm.currentTransaction().begin();
            	}
                Activities.getInstance().createOrUpdateMedia(
                    email,
                    contentType[0],
                    contentType[1],
                    part.getInputStream()
                );
                if(isTxLocal) {
                	pm.currentTransaction().commit();
                }
            }
        }
    }
        
    //-------------------------------------------------------------------------
    public void importMimeMessage(
    	org.opencrx.kernel.activity1.jmi1.EMail email,
    	MimeMessage mimeMessage,
    	boolean isNew
    ) throws ServiceException, MessagingException, IOException, ParseException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(email);
    	String providerName = email.refGetPath().get(2);
    	String segmentName = email.refGetPath().get(4);
    	boolean isTxLocal = !pm.currentTransaction().isActive();
        MailDateFormat mailDateFormat = new MailDateFormat();
        javax.mail.Address[] addressesFrom = mimeMessage.getFrom();
        javax.mail.Address[] addressesTo = mimeMessage.getRecipients(Message.RecipientType.TO);
        javax.mail.Address[] addressesCc = mimeMessage.getRecipients(Message.RecipientType.CC);
        javax.mail.Address[] addressesBcc = mimeMessage.getRecipients(Message.RecipientType.BCC);        
    	if(isNew) {
            // Update activity step 1
    		if(isTxLocal) {
    			pm.currentTransaction().begin();
    		}
            String subject = mimeMessage.getSubject();
            email.setMessageSubject(
                subject == null ? "" : subject
            );
            email.getExternalLink().clear();
            if(mimeMessage.getMessageID() != null) {
            	String messageId = mimeMessage.getMessageID(); 
                email.getExternalLink().add(messageId);
                // Update ICALs UID
                email.getExternalLink().add(
                    ICalendar.ICAL_SCHEMA + messageId
                );
                if(email.getIcal() != null) {
                	String ical = email.getIcal();
                	int pos1 = ical.indexOf("UID:");
                	int pos2 = ical.indexOf("\n", pos1);
                	if(pos2 > pos1) {
                		ical = 
                			ical.substring(0, pos1) + 
                			"UID:" + messageId +
                			ical.substring(pos2);
                		email.setIcal(ical);
                	}
                }
            }
            if(isTxLocal) {
            	pm.currentTransaction().commit();
            }
            // Update activity step 2
            // Append original email as media attachment
            String mimeMessageName = email.getActivityNumber().trim() + ".eml";
            QuotaByteArrayOutputStream mimeMessageBytes = new QuotaByteArrayOutputStream(Activities.class.getName());
            ZipOutputStream mimeMessageZipped = new ZipOutputStream(mimeMessageBytes);
            mimeMessageZipped.putNextEntry(
                new ZipEntry(mimeMessageName)
            );
            mimeMessage.writeTo(mimeMessageZipped);
            mimeMessageZipped.close();
            if(isTxLocal) {
            	pm.currentTransaction().begin();
            }
            this.createOrUpdateMedia(
                email, 
                "application/zip", 
                mimeMessageName + ".zip", 
                mimeMessageBytes.toInputStream()
            );
            if(isTxLocal) {
            	pm.currentTransaction().commit();
            }
            // Update activity step 3
            if(isTxLocal) {
            	pm.currentTransaction().begin();
            }
            String body = this.getMessageBody(mimeMessage);
            email.setMessageBody(
                body == null ? "" : body
            );
            String[] date = mimeMessage.getHeader("Date");
            if(date.length > 0) {
                email.setSendDate(
                    mailDateFormat.parse(date[0])
                );
            }
            if(isTxLocal) {
            	pm.currentTransaction().commit();
            }
            // Add originator and recipients to a note
            this.addNote(
                pm,
                email,
                "Recipients",
                this.getRecipientsAsNoteText(
                    pm,
                    providerName,
                    segmentName,
                    this.getInternetAddresses(addressesFrom),
                    this.getInternetAddresses(addressesTo),
                    this.getInternetAddresses(addressesCc),
                    this.getInternetAddresses(addressesBcc)
                )
            );                  
            // Add headers as Note
            Activities.getInstance().addNote(
                pm,
                email,
                "Message-Header",
                MimeMessageImpl.getHeadersAsRFC822(
                     mimeMessage, 
                     null
                )
            );
            this.addAttachments(
                mimeMessage, 
                email
            );
    	}
        // linkToAndFollowUp()
    	String subject = email.getMessageSubject();
        if(subject.indexOf("#") >= 0) {
        	StringTokenizer tokenizer = new StringTokenizer(subject, " .,()[]", false);
        	while(tokenizer.hasMoreTokens()) {
        		String split = tokenizer.nextToken();
        		if(split.startsWith("#")) {
        			String activityNumber = split.substring(1);
        			// Try to match activity if we have a potential activity number
        			if(activityNumber != null && activityNumber.length() >= 6) {
        				org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(pm, providerName, segmentName);
        				ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
        				activityQuery.thereExistsActivityNumber().like(".*" + activityNumber + ".*");
        				List<Activity> activities = activitySegment.getActivity(activityQuery);
        				for(Activity activity: activities) {
        					ActivityLinkToQuery linkToQuery = (ActivityLinkToQuery)pm.newQuery(ActivityLinkTo.class);
        					linkToQuery.thereExistsLinkTo().equalTo(email);
        					List<ActivityLinkTo> links = activity.getActivityLinkTo(linkToQuery);
        					// Only add link if not already there
        					if(links.isEmpty()) {
	        					try {
	        						ActivityProcess activityProcess = activity.getActivityType().getControlledBy();
	        						ActivityProcessTransitionQuery processTransitionQuery = (ActivityProcessTransitionQuery)pm.newQuery(ActivityProcessTransition.class);
	        						processTransitionQuery.thereExistsPrevState().equalTo(activity.getProcessState());
	        						processTransitionQuery.orderByNewPercentComplete().ascending();
	        						List<ActivityProcessTransition> processTransitions = activityProcess.getTransition(processTransitionQuery);
	        						if(!processTransitions.isEmpty()) {
		            					this.linkToAndFollowUp(
		            						activity, 
		            						processTransitions.iterator().next(), 
		            						email
		            					);
	        						}
	        					} catch(Exception e) {
	        						// Log and ignore
	        						new ServiceException(e).log();
	        					}
        					}
        				}
        			}
        		}
        	}
        }
        // Add FROM as sender
        List<org.opencrx.kernel.account1.jmi1.EMailAddress> addresses = 
            Accounts.getInstance().lookupEmailAddress(
                pm,
                providerName,
                segmentName,
                this.getInternetAddresses(addressesFrom)[0]
            );
        if(addresses.isEmpty()) {
            addresses = Accounts.getInstance().lookupEmailAddress(
                pm,
                providerName,
                segmentName,
                Addresses.UNASSIGNED_ADDRESS
            );                            
        }
        EMailAddress from = null;
        if(!addresses.isEmpty()) {
            from = addresses.iterator().next();
            if(isTxLocal) {
            	pm.currentTransaction().begin();
                pm.refresh(email);
            }
            email.setSender(from);
            if(isTxLocal) {
            	pm.currentTransaction().commit();
            }
        } 
        Activities.getInstance().mapAddressesToEMailRecipients(
            email,
            this.getInternetAddresses(addressesTo),
            Message.RecipientType.TO
        );
        Activities.getInstance().mapAddressesToEMailRecipients(
            email,
            this.getInternetAddresses(addressesCc),
            Message.RecipientType.CC
        );    	
    }
    
    //-------------------------------------------------------------------------
    public List<EMail> importMimeMessage(
    	PersistenceManager pm,
    	String providerName,
    	String segmentName,
    	MimeMessage mimeMessage,
    	ActivityCreator emailCreator
    ) throws ServiceException, MessagingException, IOException, ParseException {
    	if(emailCreator != null) {
    		emailCreator = (ActivityCreator)pm.getObjectById(emailCreator.refGetPath());
    	}
    	List<MimeMessage> messages = new ArrayList<MimeMessage>();
    	// mimeMessage is a wrapper message which contains messages to be 
    	// imported. The wrapper message is mapped to an activity. Create or lookup. 
    	String activityNumber = null;
    	if(mimeMessage.getSubject().startsWith("> ")) {
    		String subject = mimeMessage.getSubject().substring(2);
    		String specifier = subject;
    		if(subject.indexOf("  ") > 0) {
    			int pos = subject.indexOf("  ");
    			specifier = subject.substring(0, pos);
    			subject = subject.substring(pos + 2);
    		} else {
    			subject = null;
    		}
    		org.opencrx.kernel.activity1.jmi1.Segment activitySegment = this.getActivitySegment(pm, providerName, segmentName);
    		// Subject line is of the form
    		// > @<email creator name> [#<activity creator name or activity#>]  <subject>
    		// The activity creator name / activity# is optional. If specified an activity is created
    		// and the imported E-Mails are linked to this activity
    		// Derive email creator from subject line
    		if(emailCreator == null) {
    			int pos = specifier.indexOf("@");
    			if(pos >= 0) {    				
    				String creatorName = specifier.substring(pos + 1);
    				pos = creatorName.indexOf("  #");
    				if(pos > 0) {
    					creatorName = creatorName.substring(0, pos);
    				}
    				creatorName = creatorName.trim();    				
    				emailCreator = this.findActivityCreator(creatorName, activitySegment);
    			} else {
    				SysLog.warning("E-Mail creator not specified", specifier);
    			}
    		}
    		// Default Email creator
    		if(emailCreator == null) {
    			SysLog.warning("E-Mail creator not found. Using default creator", Activities.ACTIVITY_CREATOR_NAME_EMAILS);
    			emailCreator = this.findActivityCreator(Activities.ACTIVITY_CREATOR_NAME_EMAILS, activitySegment);
    		}
    		Activity activity = null;
    		// Derive activity / activity creator from subject line
    		if(specifier.indexOf("#") >= 0) {
    			int pos = specifier.indexOf("#");
    			String activityQualifier = specifier.substring(pos + 1);
    			pos = activityQualifier.indexOf(" @");
    			if(pos > 0) {
    				activityQualifier = activityQualifier.substring(0, pos);
    			}
    			activityQualifier = activityQualifier.trim();
    			// Try to find activity where the activity number matches the activity qualifier
    			if(activityQualifier.length() >= 6) {
	    			ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
	    			activityQuery.thereExistsActivityNumber().like(".*" + activityQualifier + ".*");
	    			List<Activity> activities = activitySegment.getActivity(activityQuery);
	    			if(!activities.isEmpty()) {
	    				activity = activities.iterator().next();
	    			}
    			}
    			// Try to find activity creator based on the activity qualifier
    			if(activity == null) {
    				ActivityCreator creator = this.findActivityCreator(activityQualifier, activitySegment);
    				if(creator == null) {
    					SysLog.warning("Activity creator not found. No activity will be created", activityQualifier);
    				} 
    				else {
    					if(subject == null || subject.length() <= 5) {
        					SysLog.warning("Subject line must have at least five characters", subject);    						
    					} 
    					else {
				            pm.currentTransaction().begin();                            
				            Activity1Package activityPkg = Utils.getActivityPackage(pm);
				            NewActivityParams newActivityParams = activityPkg.createNewActivityParams(
				                null, // description
				                this.getMessageBody(mimeMessage), // detailedDescription
				                null, // dueBy
				                ICalendar.ICAL_TYPE_NA, // icalType
				                subject,
				                this.getMessagePriority(mimeMessage),
				                null, // reportingContract
				                null, // scheduledEnd
				                null  // scheduledStart
				            );
				            NewActivityResult newActivityResult = creator.newActivity(
				                newActivityParams
				            );
				            pm.currentTransaction().commit();                  
				            activity = (Activity)pm.getObjectById(newActivityResult.getActivity().refGetPath());
    					}
					}
    			}    			
    		}
    		else {
    			SysLog.warning("Activity creator not specified. No activity will be created", specifier);
    		}
    		if(activity != null) {
    			activityNumber = activity.getActivityNumber();
    		}
    		// Get attachments as mime messages
            if(mimeMessage.getContent() instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart)mimeMessage.getContent();
                for(int i = 1; i < multipart.getCount(); i++) {
                	// Ignore attachments which are not a mime message
                    try {
                        MimeBodyPart part = (MimeBodyPart)multipart.getBodyPart(i);
	                	MimeMessage message = new MimeMessageImpl(part.getInputStream());
	                	messages.add(message);
                    } catch(Exception e) {}
                }
            }    	
    	}
    	else {
    		messages.add(mimeMessage);
    	}
    	List<EMail> emails = new ArrayList<EMail>();
    	for(MimeMessage message: messages) {
	        List<Activity> activities = this.lookupEmailActivity(
	            pm,
	            providerName,
	            segmentName,
	            message.getMessageID()
	        );
	        EMail email = null;
	        String activityRefTag = activityNumber == null ? null : " [#" + activityNumber + "]";
	        // Create new E-Mail
	        if(activities.isEmpty()) {
	    		if(activityRefTag != null) {
	    			message.setSubject(
	    				message.getSubject() + activityRefTag
	    			);
	    		}
	            pm.currentTransaction().begin();                            
	            Activity1Package activityPkg = Utils.getActivityPackage(pm);
	            NewActivityParams newActivityParams = activityPkg.createNewActivityParams(
	                null, // description
	                null, // detailedDescription
	                null, // dueBy
	                ICalendar.ICAL_TYPE_NA, // icalType
	                message.getSubject(),
	                this.getMessagePriority(message),
	                null, // reportingContract
	                null, // scheduledEnd
	                null  // scheduledStart
	            );
	            NewActivityResult newActivityResult = emailCreator.newActivity(
	                newActivityParams
	            );
	            pm.currentTransaction().commit();                  
	            email = (EMail)pm.getObjectById(newActivityResult.getActivity().refGetPath());
		        pm.currentTransaction().begin();
		        this.importMimeMessage(
		        	email, 
		        	message, 
		        	true // isNew 
		        );
		        pm.currentTransaction().commit();
	        }
	        // Update existing E-Mail
	        else {
	            email = (EMail)activities.iterator().next();
		        pm.currentTransaction().begin();
	            if(activityRefTag != null && email.getName() != null && email.getName().indexOf(activityRefTag) < 0) {
            		email.setName(email.getName() + activityRefTag);
            		email.setMessageSubject(email.getMessageSubject() + activityRefTag);
	            }
		        pm.currentTransaction().commit();
		        pm.currentTransaction().begin();
		        this.importMimeMessage(
		        	email, 
		        	message, 
		        	false // isNew 
		        );
	            if(activityRefTag != null && email.getName() != null && email.getName().indexOf(activityRefTag) < 0) {
            		email.setName(email.getName() + activityRefTag);
            		email.setMessageSubject(email.getMessageSubject() + activityRefTag);
	            }
		        pm.currentTransaction().commit();
	        }
	        emails.add(email);
    	}
	    return emails;
    }

    //-------------------------------------------------------------------------
    public void addEMailRecipients(
    	EMail email,
    	String sender,
    	List<String> recipientTo,
    	List<String> recipientCc,
    	List<String> recipientBcc
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(email);
    	String providerName = email.refGetPath().get(2);
    	String segmentName = email.refGetPath().get(4);
		// Sender
		if(sender != null) {
			List<EMailAddress> emailAddresses = 
				Accounts.getInstance().lookupEmailAddress(
					pm, 
					providerName,
					segmentName,
					sender 
				);        	
			if(!emailAddresses.isEmpty()) {
				email.setSender(emailAddresses.iterator().next());
			}
		}
		// Recipients TO  
		for(String recipientAddress: recipientTo) {
			List<EMailAddress> emailAddresses = 
				Accounts.getInstance().lookupEmailAddress(
					pm, 
					providerName,
					segmentName,
					recipientAddress 
				);        	
			if(!emailAddresses.isEmpty()) {
				EMailRecipient recipient = pm.newInstance(EMailRecipient.class);
				recipient.refInitialize(false, false);
				recipient.setParty(emailAddresses.iterator().next());
				recipient.setPartyType(Activities.PARTY_TYPE_TO);
				email.addEmailRecipient(
					this.getUidAsString(),
					recipient
				);
			}
		}
		// Recipients CC
		for(String recipientAddress: recipientCc) {
			List<EMailAddress> emailAddresses = 
				Accounts.getInstance().lookupEmailAddress(
					pm, 
					providerName,
					segmentName,
					recipientAddress 
				);        	
			if(!emailAddresses.isEmpty()) {
				EMailRecipient recipient = pm.newInstance(EMailRecipient.class);
				recipient.refInitialize(false, false);
				recipient.setParty(emailAddresses.iterator().next());
				recipient.setPartyType(Activities.PARTY_TYPE_CC);
				email.addEmailRecipient(
					this.getUidAsString(),
					recipient
				);
			}        					
		}
		// Recipients BCC
		for(String recipientAddress: recipientBcc) {
			List<EMailAddress> emailAddresses = 
				Accounts.getInstance().lookupEmailAddress(
					pm, 
					providerName,
					segmentName,
					recipientAddress 
				);        	
			if(!emailAddresses.isEmpty()) {
				EMailRecipient recipient = pm.newInstance(EMailRecipient.class);
				recipient.refInitialize(false, false);
				recipient.setParty(emailAddresses.iterator().next());
				recipient.setPartyType(Activities.PARTY_TYPE_BCC);
				email.addEmailRecipient(
					this.getUidAsString(),
					recipient
				);
			}         					
		}    	
    }

    //-------------------------------------------------------------------------
    public void sendEMail(
    	EMail email
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(email);
		// Assign
		ActivityProcessTransition transition = this.findActivityProcessTransition(
			email, 
			"Assign"
		);
		pm.currentTransaction().begin();
		ActivityDoFollowUpParams doFollowUpParams = Utils.getActivityPackage(pm).createActivityDoFollowUpParams(
			null, // assignTo 
			null, // followUpText
			"Accepted", 
			transition
		);
		email.doFollowUp(doFollowUpParams);
		pm.currentTransaction().commit();
		// Send as mail
		transition = this.findActivityProcessTransition(
			email, 
			"Send as Mail"
		);
		if(transition == null) {
			transition = this.findActivityProcessTransition(
				email, 
				"Send as mail"
			);         					
		}
		pm.currentTransaction().begin();
		doFollowUpParams = Utils.getActivityPackage(pm).createActivityDoFollowUpParams(
			null, // assignTo 
			null, // followUpText 
			"Processing", 
			transition
		);
		email.doFollowUp(doFollowUpParams);
		pm.currentTransaction().commit();
		// Close
		transition = this.findActivityProcessTransition(
			email, 
			"Close"
		);
		pm.currentTransaction().begin();
		doFollowUpParams = Utils.getActivityPackage(pm).createActivityDoFollowUpParams(
			null, // assignTo 
			null, // followUpText 
			"Sent", 
			transition
		);
		email.doFollowUp(doFollowUpParams);
		pm.currentTransaction().commit();    	
    }
    
    //-------------------------------------------------------------------------
    public enum ActivityState {
    	
    	NA(0),
    	OPEN(10),
    	CLOSED(20),
    	CANCELLED(30);
    	
    	private final int value;
    	    	
    	private ActivityState(
    		int value
    	) {
    		this.value = value;
    	}
    	
    	public int getValue(
    	) {
    		return this.value;
    	}
    	
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    private static final String[] ACTIVITY_TYPES = 
        new String[]{
            "org:opencrx:kernel:activity1:EMail",
            "org:opencrx:kernel:activity1:EMail", // Fax is deprecated
            "org:opencrx:kernel:activity1:Incident",
            "org:opencrx:kernel:activity1:Mailing",
            "org:opencrx:kernel:activity1:Meeting",
            "org:opencrx:kernel:activity1:EMail", // Sms is deprecated
            "org:opencrx:kernel:activity1:PhoneCall",
            "org:opencrx:kernel:activity1:EMail", // Mms is deprecated
            "org:opencrx:kernel:activity1:Task",
            "org:opencrx:kernel:activity1:Absence",
            "org:opencrx:kernel:activity1:ExternalActivity",
            "org:opencrx:kernel:activity1:SalesVisit"  
        };
    
    public static final short ACTIVITY_CLASS_EMAIL = 0;
    public static final short ACTIVITY_CLASS_INCIDENT = 2;
    public static final short ACTIVITY_CLASS_MAILING = 3;
    public static final short ACTIVITY_CLASS_MEETING = 4;
    public static final short ACTIVITY_CLASS_PHONE_CALL = 6;
    public static final short ACTIVITY_CLASS_TASK = 8;
    public static final short ACTIVITY_CLASS_ABSENCE = 9;
    public static final short ACTIVITY_CLASS_SALES_VISIT = 11;
    
    // PARTY_TYPE
    public final static short PARTY_TYPE_FROM = 210;
    public final static short PARTY_TYPE_TO = 220;
    public final static short PARTY_TYPE_CC = 230;
    public final static short PARTY_TYPE_BCC = 240;
    
    // LINK_TYPE
    public static final short ACTIVITY_LINK_TYPE_RELATES_TO = 6;
    public static final short ACTIVITY_LINK_TYPE_IS_DERIVED_FROM = 97;
    
    // WORKRECORD_TYPE
    public static final short WORKRECORD_TYPE_NA = 0;
    public static final short WORKRECORD_TYPE_WORK_STANDARD = 1;
    public static final short WORKRECORD_TYPE_WORK_OVERTIME = 2;
    
    // PRIORITY
    public static final short PRIORITY_LOW = 1;
    public static final short PRIORITY_NORMAL = 2;
    public static final short PRIORITY_HIGH = 3;
    public static final short PRIORITY_URGENT = 4;
    public static final short PRIORITY_IMMEDIATE = 5;
        
    // Booking texts
    protected static final String BOOKING_TEXT_NAME_WORK_EFFORT = "work efforts";
    
    public static final String DEFAULT_EMAIL_CREATOR_ID = "EMailCreator";
    
    public static final String ACTIVITY_PROCESS_NAME_BUG_AND_FEATURE_TRACKING = "Bug + feature tracking process";
    public static final String ACTIVITY_PROCESS_NAME_EMAILS = "E-Mail Process";

    public static final String CALENDAR_NAME_DEFAULT_BUSINESS = "Default Business Calendar";

    public static final String ACTIVITY_TYPE_NAME_BUGS_AND_FEATURES = "Bugs + Features";
    public static final String ACTIVITY_TYPE_NAME_EMAILS = "E-Mails";
    public static final String ACTIVITY_TYPE_NAME_MEETINGS = "Meetings";
    public static final String ACTIVITY_TYPE_NAME_PHONE_CALLS = "Phone Calls";
    public static final String ACTIVITY_TYPE_NAME_TASKS = "Tasks";
    public static final String ACTIVITY_TYPE_NAME_MAILINGS = "Mailings";
    public static final String ACTIVITY_TYPE_NAME_SALES_VISITS = "Sales Visits";
    public static final String ACTIVITY_TYPE_NAME_ABSENCES = "Absences";
    public static final String ACTIVITY_TYPE_NAME_INCIDENTS = "Incidents";

    public static final String ACTIVITY_CREATOR_NAME_BUGS_AND_FEATURES = "Bugs + Features";
    public static final String ACTIVITY_CREATOR_NAME_EMAILS = "E-Mails";
    public static final String ACTIVITY_CREATOR_NAME_MEETINGS = "Meetings";
    public static final String ACTIVITY_CREATOR_NAME_PHONE_CALLS = "Phone Calls";
    public static final String ACTIVITY_CREATOR_NAME_TASKS = "Tasks";
    public static final String ACTIVITY_CREATOR_NAME_POLLS = "Polls";
    public static final String ACTIVITY_CREATOR_NAME_MEETING_ROOMS = "Meeting Rooms";
    public static final String ACTIVITY_CREATOR_NAME_MAILINGS = "Mailings";
    public static final String ACTIVITY_CREATOR_NAME_SALES_VISITS = "Sales Visits";
    public static final String ACTIVITY_CREATOR_NAME_ABSENCES = "Absences";
    public static final String ACTIVITY_CREATOR_NAME_INCIDENTS = "Incidents";
    public static final String ACTIVITY_CREATOR_NAME_PUBLIC_EMAILS = "Public E-Mails";
    public static final String ACTIVITY_CREATOR_NAME_PUBLIC_MEETINGS = "Public Meetings";
    public static final String ACTIVITY_CREATOR_NAME_PUBLIC_PHONE_CALLS = "Public Phone Calls";
    public static final String ACTIVITY_CREATOR_NAME_PUBLIC_TASKS = "Public Tasks";

    public static final String ACTIVITY_TRACKER_NAME_BUGS_AND_FEATURES = "Bugs + Features";
    public static final String ACTIVITY_TRACKER_NAME_EMAILS = "E-Mails";
    public static final String ACTIVITY_TRACKER_NAME_MEETINGS = "Meetings";
    public static final String ACTIVITY_TRACKER_NAME_PHONE_CALLS = "Phone Calls";
    public static final String ACTIVITY_TRACKER_NAME_TASKS = "Tasks";
    public static final String ACTIVITY_TRACKER_NAME_PUBLIC = "Public";
    public static final String ACTIVITY_TRACKER_NAME_TRASH = "Trash";
    public static final String ACTIVITY_TRACKER_NAME_POLLS = "Polls";
    public static final String ACTIVITY_TRACKER_NAME_MEETING_ROOMS = "Meeting Rooms";

    public static final String UNSPECIFIED_ADDRESS = "NO_ADDRESS_SPECIFIED";

	public static final String PRIVATE_GROUP_SUFFIX = "~Private";
    
	private static final SimpleDateFormat DATEFORMAT_MESSAGE_HEADER = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(
		java.text.DateFormat.SHORT, 
		java.text.DateFormat.SHORT, 
		new Locale("US")
	);
	static {
		DATEFORMAT_MESSAGE_HEADER.applyPattern("d-MMM-yyyy HH:mm:ss Z");
	}
	
}

//--- End of File -----------------------------------------------------------

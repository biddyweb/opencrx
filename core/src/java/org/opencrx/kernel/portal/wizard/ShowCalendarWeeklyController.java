/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: ShowCalendarWeeklyController
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

import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.portal.servlet.AbstractWizardController;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ObjectReference;

/**
 * ShowCalendarWeeklyController
 *
 */
public class ShowCalendarWeeklyController extends AbstractWizardController {

	/**
	 * Constructor.
	 * 
	 */
	public ShowCalendarWeeklyController(
	) {
		super();
	}
	
	/**
	 * Get date as string.
	 * 
	 * @param date
	 * @return
	 */
	public String getDateAsString(
		GregorianCalendar date
	) {
		return getDateAsString(
			date.get(GregorianCalendar.YEAR),
			date.get(GregorianCalendar.MONTH) + 1,
			date.get(GregorianCalendar.DAY_OF_MONTH)
		);
	}

	/**
	 * Get date as string.
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public String getDateAsString(
		int year,
		int month,
		int dayOfMonth
	) {
		return // YYYYMMDD
			Integer.toString(year) +
			((month < 10 ? "0" : "") + Integer.toString(month)) +
			((dayOfMonth < 10 ? "0" : "") + Integer.toString(dayOfMonth));
	}

	/**
	 * Get date as calendar.
	 * 
	 * @param dateAsString
	 * @param app
	 * @return
	 */
	public GregorianCalendar getDateAsCalendar(
		String dateAsString,
		ApplicationContext app
	) {

		GregorianCalendar date = new GregorianCalendar(app.getCurrentLocale());
		date.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));
		date.setMinimalDaysInFirstWeek(4); // this conforms to DIN 1355/ISO 8601
		date.set(GregorianCalendar.YEAR, Integer.valueOf(dateAsString.substring(0, 4)));
		date.set(GregorianCalendar.MONTH, Integer.valueOf(dateAsString.substring(4, 6)) - 1);
		date.set(GregorianCalendar.DAY_OF_MONTH, Integer.valueOf(dateAsString.substring(6, 8)));
		date.set(GregorianCalendar.HOUR_OF_DAY, 0);
		date.set(GregorianCalendar.MINUTE, 0);
		date.set(GregorianCalendar.SECOND, 0);
		date.set(GregorianCalendar.MILLISECOND, 0);
		return date;
	}
	
	/**
	 * Get activities for current week.
	 * 
	 * @return
	 */
	protected Iterator<Activity> getActivitiesByWeek(
	) {
		ApplicationContext app = this.getApp();
		PersistenceManager pm = this.getPm();
		GregorianCalendar calendarBeginOfWeek = getDateAsCalendar(this.selectedDateStr, app);
		while (calendarBeginOfWeek.get(GregorianCalendar.DAY_OF_WEEK) != 2) {
			calendarBeginOfWeek.add(GregorianCalendar.DAY_OF_MONTH, -1);
		}
		this.tzDstOffset = calendarBeginOfWeek.get(GregorianCalendar.ZONE_OFFSET)/(60*60*1000) + calendarBeginOfWeek.get(GregorianCalendar.DST_OFFSET)/(60*60*1000);
		this.selectedDateStr = getDateAsString(
			calendarBeginOfWeek.get(GregorianCalendar.YEAR),
			calendarBeginOfWeek.get(GregorianCalendar.MONTH) + 1,
			calendarBeginOfWeek.get(GregorianCalendar.DAY_OF_MONTH)
		);
		// get Activities within time window, i.e. ScheduledStart IN [beginOfPeriod..endOfPeriod]
		java.util.Date beginOfPeriod = calendarBeginOfWeek.getTime();
		calendarBeginOfWeek.add(GregorianCalendar.DAY_OF_MONTH, 8);
		java.util.Date endOfPeriod = calendarBeginOfWeek.getTime();
		ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
		activityQuery.forAllDisabled().isFalse();
		activityQuery.thereExistsScheduledStart().greaterThan(beginOfPeriod);
		activityQuery.thereExistsScheduledStart().lessThan(endOfPeriod);
		Iterator<Activity> activities = null;
		RefObject_1_0 obj = this.getObject();
		this.calendarName = null;
		// activity filter
		if(obj instanceof ActivityFilterGroup) {
			ActivityFilterGroup activityFilterGroup =
				(ActivityFilterGroup)obj;
			activities = activityFilterGroup.getFilteredActivity(activityQuery).iterator();
			if (activityFilterGroup.getName() != null) {
				this.calendarName = activityFilterGroup.getName();
			}
		} else if (obj instanceof ActivityFilterGlobal) {
			ActivityFilterGlobal activityFilterGlobal = (ActivityFilterGlobal)obj;
			activities = activityFilterGlobal.getFilteredActivity(activityQuery).iterator();
			if (activityFilterGlobal.getName() != null) {
				this.calendarName = activityFilterGlobal.getName();
			}
		}
		// activity group
		if(obj instanceof ActivityTracker) {
			ActivityTracker activityTracker = (ActivityTracker)obj;
			activities = activityTracker.getFilteredActivity(activityQuery).iterator();
			if (activityTracker.getName() != null) {
				this.calendarName = activityTracker.getName();
			}
		} else if(obj instanceof ActivityCategory) {
			ActivityCategory activityCategory = (ActivityCategory)obj;
			activities = activityCategory.getFilteredActivity(activityQuery).iterator();
			if (activityCategory.getName() != null) {
				this.calendarName = activityCategory.getName();
			}
		} else if(obj instanceof ActivityMilestone) {
			ActivityMilestone activityMilestone = (ActivityMilestone)obj;
			activities = activityMilestone.getFilteredActivity(activityQuery).iterator();
			if (activityMilestone.getName() != null) {
				this.calendarName = activityMilestone.getName();
			}
		} else if(obj instanceof Resource) {
			Resource resource = (Resource)obj;
			activities = resource.getAssignedActivity(activityQuery).iterator();
			if (resource.getName() != null) {
				this.calendarName = resource.getName();
			}
		} else if(obj instanceof UserHome) {
			UserHome userHome = (UserHome)obj;
			activities = userHome.getAssignedActivity(activityQuery).iterator();
			try {
				this.calendarName = URLEncoder.encode(obj.refGetPath().getBase(), "UTF-8");
			} catch(Exception ignore) {}
		}
		return activities;
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
	 * @param selectedDateStr
	 */
	public void doRefresh(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		if(selectedDateStr == null || selectedDateStr.length() != 8) {
			GregorianCalendar today = new GregorianCalendar(app.getCurrentLocale());
			today.setTimeZone(TimeZone.getTimeZone(app.getCurrentTimeZone()));
			this.selectedDateStr = this.getDateAsString(today);
		} else {
			this.selectedDateStr = selectedDateStr;			
		}
	}

	/**
	 * Today action.
	 * 
	 * @param selectedDateStr
	 */
	public void doToday(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		this.doRefresh(null);
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * NextWeek action.
	 * 
	 * @param selectedDateStr
	 */
	public void doNextWeek(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = this.getDateAsCalendar(this.selectedDateStr, app); //
		date.add(GregorianCalendar.DAY_OF_MONTH, 7);
		this.selectedDateStr = this.getDateAsString(date);
		this.activities = this.getActivitiesByWeek();
	}
	
	/**
	 * PrevWeek action.
	 * 
	 * @param selectedDateStr
	 */
	public void doPrevWeek(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = this.getDateAsCalendar(this.selectedDateStr, app);
		date.add(GregorianCalendar.DAY_OF_MONTH, -7);
		this.selectedDateStr = this.getDateAsString(date);		
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * NextMonth action.
	 * 
	 * @param selectedDateStr
	 */
	public void doNextMonth(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = this.getDateAsCalendar(this.selectedDateStr, app);
		date.add(GregorianCalendar.MONTH, 1);
		this.selectedDateStr = this.getDateAsString(date);
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * PrevMonth action.
	 * 
	 * @param selectedDateStr
	 */
	public void doPrevMonth(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = this.getDateAsCalendar(this.selectedDateStr, app);
		date.add(GregorianCalendar.MONTH, -1);
		this.selectedDateStr = this.getDateAsString(date);
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * NextYear action.
	 * 
	 * @param selectedDateStr
	 */
	public void doNextYear(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = getDateAsCalendar(this.selectedDateStr, app); //
		date.add(GregorianCalendar.YEAR, 1);
		this.selectedDateStr = this.getDateAsString(date);
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * PrevYear action.
	 * 
	 * @param selectedDateStr
	 */
	public void doPrevYear(
		@RequestParameter(name = "selectedDateStr") String selectedDateStr
	) {
		ApplicationContext app = this.getApp();
		this.doRefresh(selectedDateStr);
		GregorianCalendar date = this.getDateAsCalendar(this.selectedDateStr, app); //
		date.add(GregorianCalendar.YEAR, -1);
		this.selectedDateStr = getDateAsString(date);
		this.activities = this.getActivitiesByWeek();
	}

	/**
	 * @return the selectedDateStr
	 */
	public String getSelectedDateStr() {
		return selectedDateStr;
	}

	/**
	 * @return the activities
	 */
	public Iterator<Activity> getActivities() {
		return activities;
	}

	/**
	 * @return the tz_dst_offset
	 */
	public int getTzDstOffset() {
		return tzDstOffset;
	}

	/**
	 * @return the name
	 */
	public String getCalendarName() {
		return calendarName;
	}

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	private String selectedDateStr;
	private Iterator<Activity> activities;
	private int tzDstOffset = 0;
	private String calendarName;

}

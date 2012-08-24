/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: ConnectionHelper
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2012, CRIXP Corp., Switzerland
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
package org.opencrx.application.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGlobal;
import org.opencrx.kernel.activity1.jmi1.ActivityFilterGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.home1.jmi1.ActivityFilterCalendarFeed;
import org.opencrx.kernel.home1.jmi1.ActivityGroupCalendarFeed;
import org.opencrx.kernel.home1.jmi1.AirSyncProfile;
import org.opencrx.kernel.home1.jmi1.CalendarProfile;
import org.opencrx.kernel.home1.jmi1.CardProfile;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.opencrx.kernel.home1.jmi1.DocumentProfile;
import org.opencrx.kernel.home1.jmi1.SyncFeed;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

public class AdapterConnectionHelper {

	private static List<URL> mapToURLs(
		String baseUrl,
		RefObject_1_0 obj,
		String servletType,
		List<String> paths
	) throws ServiceException {
		try {
			String providerName = obj.refGetPath().get(2);	
			String segmentName = obj.refGetPath().get(4);			
			List<URL> urls = new ArrayList<URL>();
			for(String path: paths) {
				urls.add(
					new URL(baseUrl.replace("-core-", "-" + servletType + "-") + providerName + "/" + segmentName + path)
				);
			}
			return urls;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}
	
	private static List<String> getCalendarPaths(
		RefObject_1_0 obj,
		boolean isCollectionTypeTask,
		String suffix
	) throws ServiceException {
		try {
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);
			List<String> paths = new ArrayList<String>();
			if(obj instanceof ActivityFilterGroup) {
				ActivityFilterGroup activityFilterGroup =
					(ActivityFilterGroup)obj;
				if((activityFilterGroup.getName() != null) && !activityFilterGroup.getName().isEmpty()) {
					ActivityGroup group = (ActivityGroup)pm.getObjectById(activityFilterGroup.refGetPath().getParent().getParent());
					if(group instanceof ActivityTracker) {
						paths.add("/tracker/" + group.getName() + "/filter/" + activityFilterGroup.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
					} else if(group instanceof ActivityMilestone) {
						paths.add("/milestone/" +  group.getName() + "/filter/" + activityFilterGroup.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);						
					} else if(group instanceof ActivityCategory) {
						paths.add("/category/" +  group.getName() + "/filter/" + activityFilterGroup.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);						
					}
				}
			} else if(obj instanceof ActivityFilterGlobal) {
				ActivityFilterGlobal activityFilterGlobal = (ActivityFilterGlobal)obj;
				if((activityFilterGlobal.getName() != null) && !activityFilterGlobal.getName().isEmpty()) {
					paths.add("/globalfilter/" + activityFilterGlobal.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
				}
			} else if(obj instanceof ActivityTracker) {
				ActivityTracker activityTracker =
					(ActivityTracker)obj;
				if((activityTracker.getName() != null) && !activityTracker.getName().isEmpty()) {
					paths.add("/tracker/" + activityTracker.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
				}
			} else if(obj instanceof ActivityCategory) {
				ActivityCategory activityCategory =
					(ActivityCategory)obj;
				if((activityCategory.getName() != null) && !activityCategory.getName().isEmpty()) {
					paths.add("/category/" + activityCategory.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
				}
			} else if(obj instanceof ActivityMilestone) {
				ActivityMilestone activityMilestone = (ActivityMilestone)obj;
				if((activityMilestone.getName() != null) && !activityMilestone.getName().isEmpty()) {
					paths.add("/milestone/" + activityMilestone.getName() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
				}
			} else if(obj instanceof CalendarProfile) {
				CalendarProfile calendarProfile = (CalendarProfile)obj;
				for(SyncFeed feed: calendarProfile.<SyncFeed>getFeed()) {
					if(Boolean.TRUE.equals(feed.isActive())) {
						if(feed instanceof ActivityFilterCalendarFeed) {
							paths.addAll(
								getCalendarPaths(
									((ActivityFilterCalendarFeed)feed).getActivityFilter(),
									isCollectionTypeTask,
									suffix
								)
							);
						} else if(feed instanceof ActivityGroupCalendarFeed) {
							paths.addAll(
								getCalendarPaths(
									((ActivityGroupCalendarFeed)feed).getActivityGroup(),
									isCollectionTypeTask,
									suffix
								)
							);
						}
					}
				}
			} else if(obj instanceof UserHome) {
				paths.add("/home/" + obj.refGetPath().getBase() + (isCollectionTypeTask ? "/VTODO" : "") + suffix);
			}
			return paths;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	private static List<String> getCardPaths(
		RefObject_1_0 obj,
		String suffix
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
		    if(obj instanceof org.opencrx.kernel.account1.jmi1.AccountFilterGlobal) {
		    	org.opencrx.kernel.account1.jmi1.AccountFilterGlobal accountFilterGlobal =
		    		(org.opencrx.kernel.account1.jmi1.AccountFilterGlobal)obj;
		    	if((accountFilterGlobal.getName() != null) && !accountFilterGlobal.getName().isEmpty()) {
		    		paths.add("/filter/" + accountFilterGlobal.getName() + suffix);
		    	}
		    }
		    else if(obj instanceof org.opencrx.kernel.account1.jmi1.AbstractGroup) {
		    	org.opencrx.kernel.account1.jmi1.AbstractGroup accountGroup =
		    		(org.opencrx.kernel.account1.jmi1.AbstractGroup)obj;
		    	if((accountGroup.getName() != null) && !accountGroup.getName().isEmpty()) {
		    		paths.add("/group/" + accountGroup.getName() + suffix);
		    	}
		    } else if(obj instanceof CardProfile) {
		    	CardProfile cardProfile = (CardProfile)obj;
				for(SyncFeed feed: cardProfile.<SyncFeed>getFeed()) {
					if(Boolean.TRUE.equals(feed.isActive())) {					
						if(feed instanceof ContactsFeed) {
							paths.addAll(
								getCardPaths(
									((ContactsFeed)feed).getAccountGroup(),
									suffix
								)
							);
						}
					}
				}
			}
		    return paths;
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getCalDavCollectionSetURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		try {
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);
			List<String> paths = new ArrayList<String>();
			if(obj instanceof CalendarProfile) {
				CalendarProfile calendarProfile =
					(CalendarProfile)obj;			
		        if((calendarProfile.getName() != null) && !calendarProfile.getName().isEmpty()) {
		        	UserHome userHome = (UserHome)pm.getObjectById(new Path(obj.refMofId()).getParent().getParent());
		        	paths.add("/user/" + userHome.refGetPath().getBase() + "/profile/" + calendarProfile.getName());      	
		        }
			}
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
				obj,
				"caldav",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getCalDavEventCollectionURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		return mapToURLs(
			baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
			obj,
			"caldav",
			getCalendarPaths(obj, false, "")
		);
	}

	public static List<URL> getCalDavTaskCollectionURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		return mapToURLs(
			baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
			obj,
			"caldav",
			getCalendarPaths(obj, true, "")
		);
	}

	public static List<URL> getWebDavCollectionURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {		
		try {
			List<String> paths = new ArrayList<String>();
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);
			if(obj instanceof DocumentProfile) {
				DocumentProfile documentProfile =
					(DocumentProfile)obj;			
		        if((documentProfile.getName() != null) && !documentProfile.getName().isEmpty()) {
		        	UserHome userHome =  (UserHome)pm.getObjectById(obj.refGetPath().getParent().getParent());
		        	paths.add("/user/" + userHome.refGetPath().getBase() + "/profile/" + documentProfile.getName());	        	
		        }			
			}
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
				obj,
				"webdav",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getCardDavCollectionSetURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);			
			if(obj instanceof CardProfile) {
				CardProfile cardProfile = (CardProfile)obj;			
		        if((cardProfile.getName() != null) && !cardProfile.getName().isEmpty()) {
		        	UserHome userHome =  (UserHome)pm.getObjectById(obj.refGetPath().getParent().getParent());
		        	paths.add("/user/" + userHome.refGetPath().getBase() + "/profile/" + cardProfile.getName());	        	
		        }
			}
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
				obj,
				"carddav",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getCardDavCollectionURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);			
			if(obj instanceof CardProfile) {
				CardProfile cardProfile = (CardProfile)obj;
		        if((cardProfile.getName() != null) && !cardProfile.getName().isEmpty()) {
		        	UserHome userHome =  (UserHome)pm.getObjectById(obj.refGetPath().getParent().getParent());
					for(SyncFeed feed: cardProfile.<SyncFeed>getFeed()) {
						if(Boolean.TRUE.equals(feed.isActive())) {					
							if(feed instanceof ContactsFeed) {
								paths.add("/" + userHome.refGetPath().getBase() + "/" + cardProfile.refGetPath().getBase() + "/" + feed.refGetPath().getBase());
							}
						}
					}
				}
			}
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
				obj,
				"carddav",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getICalURLs(
		String baseUrl,
		RefObject_1_0 obj,
		String optionMax,
		String optionIsDisabled
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			String suffix = "&type=ics&max=" + (optionMax == null ? "" : optionMax) + "&disabled=" + (optionIsDisabled == null ? "" : optionIsDisabled);
			if(obj instanceof Resource) {
				Resource resource = (Resource)obj;
				if((resource.getName() != null) && !resource.getName().isEmpty()) {
					paths.add("/resource/" + resource.getName() + suffix);      	
			      }
			}			
		    paths.addAll(
		    	getCalendarPaths(
		    		obj,
		    		false,
		    		suffix
		    	)
		    );
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl + "activities?id=" : baseUrl + "/activities?id=",
				obj,
				"ical",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getTimelineURLs(
		String baseUrl,
		RefObject_1_0 obj,
		String optionMax,
		String optionIsDisabled,
		String optionTimelineHeight
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			String suffix = "&type=html&max=" + (optionMax == null ? "" : optionMax) + "&height=" + (optionTimelineHeight == null ? "" : optionTimelineHeight) + "&disabled=" + (optionIsDisabled == null ? "" : optionIsDisabled);
			if(obj instanceof Resource) {
				Resource resource = (Resource)obj;
				if((resource.getName() != null) && !resource.getName().isEmpty()) {
					paths.add("/resource/" + resource.getName() + suffix);      	
			      }
			}			
		    paths.addAll(
		    	getCalendarPaths(
		    		obj,
		    		false,
		    		suffix
		    	)
		    );
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl + "activities?id=" : baseUrl + "/activities?id=",
				obj,
				"ical",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getFreeBusyURLs(
		String baseUrl,
		RefObject_1_0 obj,
		String optionUser,
		String optionMax,
		String optionIsDisabled
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			String suffix1 = "&user=" + (optionUser == null ? "" : optionUser) + "&max=" + (optionMax == null ? "" : optionMax) + "&disabled=" + (optionIsDisabled == null ? "" : optionIsDisabled);
			String suffix2 = "&type=ics&user=" + (optionUser == null ? "" : optionUser) + "&max=" + (optionMax == null ? "" : optionMax) + "&disabled=" + (optionIsDisabled == null ? "" : optionIsDisabled);
			if(obj instanceof Resource) {
				Resource resource = (Resource)obj;
				if((resource.getName() != null) && !resource.getName().isEmpty()) {
					paths.add("/resource/" + resource.getName() + suffix1);      	
					paths.add("/resource/" +resource.getName() + suffix2);      	
				}
			}			
		    paths.addAll(
		    	getCalendarPaths(
		    		obj, 
		    		false,
		    		suffix1
		    	)
		    );
		    paths.addAll(
		    	getCalendarPaths(
		    		obj, 
		    		false,
		    		suffix2
		    	)
		    );
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl + "freebusy?id=" : baseUrl + "/freebusy?id=",
				obj,
				"ical",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getBirthdayCalendarURLs(
		String baseUrl,
		RefObject_1_0 obj,
		String optionMax,
		String optionSummaryPrefix,
		String optionCategories,
		String optionYear,
		String optionAlarm
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			paths.addAll(
				getCardPaths(
					obj,
					"&type=ics&max=" + optionMax + "&icalType=VEVENT&summaryPrefix=" + (optionSummaryPrefix == null ? "" : optionSummaryPrefix) + "&categories=" + (optionCategories == null ? "" : optionCategories) + "&year=" + (optionYear == null ? "" : optionYear) + "&alarm=" + (optionAlarm == null ? "" : optionAlarm)
				)
			);
			paths.addAll(
				getCardPaths(
					obj,
					"&type=ics&max=&icalType=VTODO&summaryPrefix=" + (optionSummaryPrefix == null ? "" : optionSummaryPrefix) + "&categories=" + (optionCategories == null ? "" : optionCategories) + "&year=" + (optionYear == null ? "" : optionYear) + "&alarm=" + (optionAlarm == null ? "" : optionAlarm)
				)
			);
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl + "bdays?id=" : baseUrl + "/bdays?id=",
				obj,
				"ical",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getVCardURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		try {
			List<String> paths = getCardPaths(
				obj,
				"&type=vcf"
			);
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl + "accounts?id=" : baseUrl + "/accounts?id=",
				obj,
				"vcard",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	public static List<URL> getAirSyncURLs(
		String baseUrl,
		RefObject_1_0 obj
	) throws ServiceException {
		try {
			List<String> paths = new ArrayList<String>();
			PersistenceManager pm = JDOHelper.getPersistenceManager(obj);
			if(obj instanceof AirSyncProfile) {
				AirSyncProfile syncProfile =
		            (AirSyncProfile)obj;
		        if((syncProfile.getName() != null) && !syncProfile.getName().isEmpty()) {
		        	UserHome userHome = (UserHome)pm.getObjectById(obj.refGetPath().getParent().getParent());
		        	paths.add("/user/" + userHome.refGetPath().getBase() + "/profile/" + syncProfile.getName());	        		        	
		        }
			}
			return mapToURLs(
				baseUrl.endsWith("/") ? baseUrl : baseUrl + "/",
				obj,
				"airsync",
				paths
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

}

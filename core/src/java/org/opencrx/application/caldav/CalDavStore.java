/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CalDavStore
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.caldav;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.Lock;
import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.WebDavStore;
import org.opencrx.application.uses.net.sf.webdav.exceptions.LockFailedException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.WebdavException;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.home1.cci2.CalendarProfileQuery;
import org.opencrx.kernel.home1.jmi1.CalendarProfile;
import org.opencrx.kernel.home1.jmi1.SyncFeed;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.ActivityQueryHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

public class CalDavStore implements WebDavStore {

	//-----------------------------------------------------------------------
	public CalDavStore(
		PersistenceManagerFactory pmf
	) {
		this.pmf = pmf;
	}
	
	//-----------------------------------------------------------------------
    public static PersistenceManager getPersistenceManager(
        HttpServletRequest req,
        PersistenceManagerFactory pmf
    ) {
        return req.getUserPrincipal() == null ?
            null :
            pmf.getPersistenceManager(
                req.getUserPrincipal().getName(),
                null
            );
    }
	
	//-----------------------------------------------------------------------
	public static ActivityQueryHelper getActivityQueryHelper(
        PersistenceManager pm,
        String filterId,
        String isDisabledFilter
    ) {
        ActivityQueryHelper activitiesHelper = new ActivityQueryHelper(pm);
        if(filterId != null) {
            try {
                activitiesHelper.parseQueryId(                        
                    (filterId.startsWith("/") ? "" : "/") + filterId
                );
                activitiesHelper.parseDisabledFilter(
                   isDisabledFilter
                );
            }
            catch(Exception  e) {}
        }        
        return activitiesHelper;
    }
    		
	//-----------------------------------------------------------------------
	/**
	 * Path is of the form:
	 * - Format 1: {provider.id} "/" {segment.id} "/user/" {user.id} "/profile/" {profile.name}
	 * - Format 2: {provider.id} "/" {segment.id} "/" {user.id} "/" {profile.id} "/" {feed.id} [":VTODO"] "/" {activity.id}
	 * - Format 3: {provider.id} "/" {segment.id} ["/user/" {user.id} ] "/" {tracker|milestone|category|home|resource|filter} "/" {calendar.name} ["/filter/" {filter.name}] ["/VTODO"] "/" {activity.id}	
	 */
	protected Resource getResourceByPath(
		RequestContext requestContext, 
		String path,
		boolean allowRunAs
	) {
		PersistenceManager pm = ((CalDavRequestContext)requestContext).getPersistenceManager();
		HttpServletRequest req = requestContext.getHttpServletRequest();
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] components = path.split("/");
		// Format 1
		if(
			components.length == 6 && 
			"user".equals(components[2]) && 
			"profile".equals(components[4])
		) {
			String userId = components[3];
    		UserHome userHome = (UserHome)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", userId)
    		);
        	CalendarProfile syncProfile = null;
        	CalendarProfileQuery calendarProfileQuery = (CalendarProfileQuery)pm.newQuery(CalendarProfile.class);
        	calendarProfileQuery.name().equalTo(components[5]);
        	List<CalendarProfile> calendarProfiles = userHome.getSyncProfile(calendarProfileQuery);
        	if(!calendarProfiles.isEmpty()) {
        		syncProfile = calendarProfiles.iterator().next();
	    		String runAs = null;
	    		if(!userId.equals(req.getUserPrincipal().getName())) {
	    			runAs = userId;
	    		}
	    		return new CalendarProfileResource(
	    			requestContext, 
	    			syncProfile,
	    			runAs
	    		);
        	}
		}
		// Format 3
		else if(
			components.length >= 3 && 
			(CALENDAR_TYPES.contains(components[2]) || ("user".equals(components[2]) && CALENDAR_TYPES.contains(components[4]))) 
		) {
			String runAs = null;
			if("user".equals(components[2])) {
				String userId = components[3];
				if(allowRunAs && !userId.equals(req.getUserPrincipal().getName())) {
					runAs = userId;
				}
				path = components[0] + "/" + components[1];
				for(int i = 4; i < components.length; i++) {
					path += "/" + components[i];
				}
			}
			String activityId = null;
			if(path.endsWith(".ics")) {
				int pos = path.lastIndexOf("/");
				if(pos > 0) {
					activityId = path.substring(pos + 1);
					path = path.substring(0, pos);
				}
			}
			ActivityCollectionResource.Type type = ActivityCollectionResource.Type.VEVENT; 
			if(path.indexOf("/VTODO") > 0) {
				path = path.substring(0, path.indexOf("/VTODO"));
				type = ActivityCollectionResource.Type.VTODO;
			}
			ActivityQueryHelper queryHelper = CalDavStore.getActivityQueryHelper(
				pm, 
				path, 
				"false"
			);
			if(queryHelper.getSource() == null) {
				SysLog.log(Level.FINE, "Unable to get query helper for user >{0}< and path >{1}<", req.getUserPrincipal().getName(), Arrays.asList(components));
				return null;
			}
			else {
				ActivityCollectionResource activityCollectionResource = new QueryBasedActivityCollectionResource(
					requestContext,
					queryHelper,
					type,
					runAs
				);
				if(activityId != null) {
					if(activityId.endsWith(".ics")) {
						activityId = activityId.substring(0, activityId.indexOf(".ics"));
					}
		    		if(this.uidMapping.containsKey(activityId)) {
		    			String newId = this.uidMapping.get(activityId);
		    			// old -> new mapping only available for one request
		    			this.uidMapping.remove(activityId);
		    			activityId = newId;
		    		}
		    		try {
			    		Activity activity = (Activity)activityCollectionResource.getQueryHelper().getPersistenceManager().getObjectById(
			    			new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", components[0], "segment", components[1], "activity", activityId)
			    		);	    		
						return new ActivityResource(
							requestContext,
							activity,
							activityCollectionResource
						);
		    		} catch(Exception e) {
		    			ServiceException e0 = new ServiceException(e);
		    			SysLog.detail(e0.getMessage(), e0.getCause());
		    		}
				} else {
					return activityCollectionResource;
				}
			}
		}
		// Format 2
		else if(components.length >= 3) {
			String userId = components[2];
    		String runAs = null;
    		if(allowRunAs && !userId.equals(req.getUserPrincipal().getName())) {
    			runAs = userId;
    		}
			// UserHome
			if(components.length == 3) {
				UserHome userHome = (UserHome)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", userId)
	    		);
	    		return new UserHomeResource(
	    			requestContext, 
	    			userHome,
	    			runAs
	    		);
			}	
			// SyncProfile
			else if(components.length == 4) {
	    		CalendarProfile calendarProfile = (CalendarProfile)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", userId, "syncProfile", components[3])
	    		);
	    		return new CalendarProfileResource(
	    			requestContext, 
	    			calendarProfile,
	    			runAs
	    		);
			}
			// SyncFeed
			else if(components.length == 5) {
				String id = components[4];
				ActivityCollectionResource.Type type = ActivityCollectionResource.Type.VEVENT; 
				if(id.endsWith(":VTODO")) {
					id = id.substring(0, id.indexOf(":VTODO"));
					type = ActivityCollectionResource.Type.VTODO;
				}
	    		SyncFeed syncFeed = (SyncFeed)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", userId, "syncProfile", components[3], "feed", id)
	    		);
	    		return new SyncFeedBasedActivityCollectionResource(
	    			requestContext, 
	    			syncFeed,
	    			type,
	    			runAs
	    		);
			}
			// Activity
			else if(components.length == 6) {
				ActivityCollectionResource parent = (ActivityCollectionResource)this.getResourceByPath(
		   			requestContext, 
		   			components[0] + "/" + components[1] + "/" + components[2] + "/" + components[3] + "/" + components[4]
		   		);
				String id = components[5];
				if(id.endsWith(".ics")) {
					id = id.substring(0, id.indexOf(".ics"));
				}
	    		if(this.uidMapping.containsKey(id)) {
	    			String newId = this.uidMapping.get(id);
	    			// old -> new mapping only available for one request
	    			this.uidMapping.remove(id);
	    			id = newId;
	    		}			
				// Get activity
	    		try {
		    		Activity activity = (Activity)pm.getObjectById(
		    			new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant("provider", components[0], "segment", components[1], "activity", id)
		    		);
		    		return new ActivityResource(
		    			requestContext, 
		    			activity,
		    			parent
		    		);
	    		} catch(Exception e) {
	    			ServiceException e0 = new ServiceException(e);
					SysLog.detail(e0.getMessage(), e0.getCause());
	    		}
			}
		}
		return null;		
	}
	
	//-----------------------------------------------------------------------
	@Override
	public RequestContext begin(
    	HttpServletRequest req,
    	HttpServletResponse resp		
	) {
		CalDavRequestContext requestContext = new CalDavRequestContext(req, resp, this.pmf);
		return requestContext;
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void commit(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((CalDavRequestContext)requestContext).getPersistenceManager();
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().commit();
				pm.close();
			}
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void createCollection(
		RequestContext requestContext, 
		String path
	) {
		throw new WebdavException("Not supported by CalDAV");
	}
	
	//-----------------------------------------------------------------------
	@Override
	public Collection<Resource> getChildren(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof CalDavResource) {
			return ((CalDavResource)res).getChildren();
		} else {
			return Collections.emptyList();
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public BinaryLargeObject getResourceContent(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof CalDavResource) {
			return ((CalDavResource)res).getContent();
		} else {
			return BinaryLargeObjects.valueOf(new byte[]{});
		}
	}

	//-----------------------------------------------------------------------
	@Override
	public Resource getResourceByPath(
		RequestContext requestContext, 
		String path
	) {
		return this.getResourceByPath(
			requestContext, 
			path, 
			true // allowRunAs
		);
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void removeResource(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof ActivityResource) {
			PersistenceManager pm = ((CalDavRequestContext)requestContext).getPersistenceManager();		
			pm.currentTransaction().begin();
			((ActivityResource)res).getObject().setDisabled(true);
			pm.currentTransaction().commit();
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public MoveResourceStatus moveResource(
    	RequestContext requestContext, 
    	Resource res, 
    	String sourcePath,
    	String destinationPath
    ) {
		throw new WebdavException("Not supported by CalDAV");	    
    }
	
	//-----------------------------------------------------------------------
	@Override
	public void rollback(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((CalDavRequestContext)requestContext).getPersistenceManager();		
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().rollback();
				pm.close();
			}
		} catch(Exception e) {}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public String getMimeType(
    	Resource res
    ) {
		if(res instanceof CalDavResource) {
			return ((CalDavResource)res).getMimeType();
		} else {
			return "text/xml";
		}
    }
	
	//-----------------------------------------------------------------------	
    protected String getParentPath(
    	String path
    ) {
        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            return path.substring(0, slash);
        }
        return null;
    }
	
	//-----------------------------------------------------------------------
	@Override
    public PutResourceStatus putResource(
    	RequestContext requestContext, 
    	String path, 
    	InputStream content, 
    	String contentType 
    ) {
		String parentPath = this.getParentPath(path); 
		Resource parent = this.getResourceByPath(
			requestContext, 
			parentPath,
			false // allowRunAs
		);
		if(parent instanceof ActivityCollectionResource) {
			ActivityCollectionResource activityCollection = (ActivityCollectionResource)parent;
			if(activityCollection.allowChange()) {
		    	try {
			    	BufferedReader reader = new BufferedReader(
			    		new InputStreamReader(content, "UTF-8")
			    	);
		        	ICalendar.PutICalResult result = ICalendar.getInstance().putICal(
		        		reader, 
		        		activityCollection.getQueryHelper(),
		        		true
		        	);
		            if(result.getOldUID() != null && result.getActivity() != null) {
		            	this.uidMapping.put(
		            		result.getOldUID(), 
		            		result.getActivity().refGetPath().getBase()
		            	);
		            }
		        	return result.getStatus() == ICalendar.PutICalResult.Status.CREATED ? 
		        		PutResourceStatus.CREATED : 
		        			PutResourceStatus.UPDATED;
		    	}
		    	catch(Exception e) {
		    		new ServiceException(e).log();
		    	}
			} else {
				return PutResourceStatus.FORBIDDEN;
			}
		}
	    return null;
    }

	//-----------------------------------------------------------------------
	@Override
    public List<Lock> getLocksByPath(
    	RequestContext requestContext, 
    	String path
    ) {
		return Collections.emptyList();
    }

	//-----------------------------------------------------------------------
	@Override
    public Lock lock(
    	RequestContext requestContext, 
    	String path, 
    	String owner, 
    	String scope, 
    	String type, 
    	int depth, 
    	int timeout
    ) throws LockFailedException {
	    return null;
    }

	//-----------------------------------------------------------------------
	@Override
    public void setLockTimeout(
    	RequestContext requestContext, 
    	String id, 
    	int timeout
    ) {	    
    }

	//-----------------------------------------------------------------------
	@Override
    public boolean unlock(
    	RequestContext requestContext, 
    	String id
    ) {
	    return false;
    }

	//-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
	protected static final Set<String> CALENDAR_TYPES =
		new HashSet<String>(Arrays.asList("tracker", "milestone", "category", "home", "resource", "filter", "globalfilter"));
			
	protected PersistenceManagerFactory pmf = null;
    protected final Map<String,String> uidMapping = new ConcurrentHashMap<String,String>();
	
}
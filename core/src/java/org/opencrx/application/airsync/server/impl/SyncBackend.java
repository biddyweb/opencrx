/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: SyncBackend.java,v 1.48 2010/04/16 13:51:22 wfro Exp $
 * Description: Sync for openCRX
 * Revision:    $Revision: 1.48 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/16 13:51:22 $
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
package org.opencrx.application.airsync.server.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.opencrx.application.airsync.datatypes.AttachmentDataT;
import org.opencrx.application.airsync.datatypes.ContactT;
import org.opencrx.application.airsync.datatypes.EventT;
import org.opencrx.application.airsync.datatypes.FolderType;
import org.opencrx.application.airsync.datatypes.IData;
import org.opencrx.application.airsync.datatypes.TaskT;
import org.opencrx.application.airsync.server.SyncCollection;
import org.opencrx.application.airsync.server.SyncDataItem;
import org.opencrx.application.airsync.server.SyncFolder;
import org.opencrx.application.airsync.server.SyncRequest;
import org.opencrx.application.airsync.server.SyncState;
import org.opencrx.application.airsync.server.spi.ISyncBackend;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.Group;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.Meeting;
import org.opencrx.kernel.activity1.jmi1.AbstractFilterActivity;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.Incident;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.activity1.jmi1.Task;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.home1.cci2.AirSyncProfileQuery;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.home1.cci2.SyncFeedQuery;
import org.opencrx.kernel.home1.jmi1.ActivityFilterCalendarFeed;
import org.opencrx.kernel.home1.jmi1.ActivityGroupCalendarFeed;
import org.opencrx.kernel.home1.jmi1.AirSyncProfile;
import org.opencrx.kernel.home1.jmi1.Alert;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.opencrx.kernel.home1.jmi1.SyncFeed;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.log.SysLog;

public class SyncBackend implements ISyncBackend {

	public SyncBackend(
	) {
	}

	protected PersistenceManager getPersistenceManager(
		SyncRequest request
	) throws ServiceException {
		try {
			PersistenceManager pm = JDOHelper.getPersistenceManager(this.getUserHome(request));
			if(pm == null || pm.getPersistenceManagerFactory() == null) {
				throw new ServiceException(
					BasicException.Code.DEFAULT_DOMAIN,
					BasicException.Code.ASSERTION_FAILURE,
					"Unable to aquire persistence manager from session",
					new BasicException.Parameter("request", request),					
					new BasicException.Parameter("userHome", this.getUserHome(request))
				);
			}
			return pm.getPersistenceManagerFactory().getPersistenceManager(
				UserObjects.getPrincipalChain(pm).toString(),
				null
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	protected UserHome getUserHome(
		SyncRequest request
	) {
		return SyncRequestHelper.getUserHome(request);		
	}
	
	@Override
	public SyncState getSyncState(
		SyncRequest request, 
		String syncKey
	) {
		SyncState syncState = new SyncState();
		syncState.setValue(
			Long.valueOf(syncKey)
		);
		return syncState;
	}
	
	@Override
    public String getNewSyncKey(
    	SyncRequest request, 
    	String syncKey
    ) {
		return "0".equals(syncKey) ?
			"1" : // lowest value after "0" :
			Long.toString(System.currentTimeMillis());
    }

	@Override
    public void deleteDataItem(
    	SyncRequest request, 
    	String folderId,
    	String itemId
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		if(pm != null) {
			Path folderIdentity = DatatypeMappers.toObjectIdentity(folderId);
			if(folderIdentity != null) {
				RefObject_1_0 folder = null;
				try {
					folder = (RefObject_1_0)pm.getObjectById(folderIdentity);
				} catch(Exception e) {}
				if(folder != null) {			
					Path objectIdentity = DatatypeMappers.toObjectIdentity(itemId);
					if(objectIdentity != null) {
						RefObject_1_0 refObj = (RefObject_1_0)pm.getObjectById(objectIdentity);
						try {
							// In case of Contact folders disable relationship (Member) 
							// instead of contact object itself
							if(folder instanceof Group && refObj instanceof Contact) {
								MemberQuery memberQuery = (MemberQuery)pm.newQuery(Member.class);
								memberQuery.thereExistsAccount().equalTo(refObj);
								List<Member> members = ((Group)folder).getMember(memberQuery);
								pm.currentTransaction().begin();
								for(Member member: members) {
									member.setDisabled(true);
								}
								pm.currentTransaction().commit();
							} 
							// In case of alert mark alert as accepted
							else if(refObj instanceof Alert) {
								pm.currentTransaction().begin();
								((Alert)refObj).setAlertState(UserHomes.ALERT_STATE_ACCEPTED);
								pm.currentTransaction().commit();
							} 
							// Mark object as disabled. Physical deletion is 
							// not supported by this backend
							else if(refObj instanceof CrxObject) {
								pm.currentTransaction().begin();
								((CrxObject)refObj).setDisabled(true);
								pm.currentTransaction().commit();
							}
						} catch(Exception e) {
							try {
								pm.currentTransaction().rollback();
							} catch(Exception e1) {}
						}
					}
				}
			}
		}
    }

	@Override
    public SyncDataItem fetchDataItem(
    	SyncRequest request,
    	String folderId, 
    	String itemId
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		if(pm != null) {
			Path objectIdentity = DatatypeMappers.toObjectIdentity(itemId);
			if(objectIdentity != null) {
				RefObject_1_0 object = (RefObject_1_0)pm.getObjectById(objectIdentity);	
				return DatatypeMappers.toDataItem(object, false);
			}
		}
		return null;
    }

	@Override
	public AttachmentDataT getAttachementData(
		SyncRequest request,
		String attachmentId
	) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		if(pm != null) {
			Path objectIdentity = DatatypeMappers.toObjectIdentity(attachmentId);
			if(objectIdentity != null) {
				RefObject_1_0 object = (RefObject_1_0)pm.getObjectById(objectIdentity);
				return DatatypeMappers.toAttachmentData(object);
			}
		}
		return null;
	}

	protected Activity createActivity(
		ActivityGroup group,
		short activityClass,
		String name,
		String detailedDescription,
		Date scheduledStart
	) throws ServiceException {
		PersistenceManager pm = JDOHelper.getPersistenceManager(group);
		ActivityCreator creator = null;
		Collection<ActivityCreator> activityCreators =  group.getActivityCreator();
		for(ActivityCreator activityCreator: activityCreators) {
			if(activityCreator.getActivityType() != null && activityCreator.getActivityType().getActivityClass() == activityClass) {
				creator = activityCreator;
				break;
			}										
		}
		Activity activity = null;
		if(creator != null) {
			NewActivityParams params = Utils.getActivityPackage(pm).createNewActivityParams(
				null, // description 
				detailedDescription, 
				null, // dueBy
				ICalendar.ICAL_TYPE_NA, 
				name, 
				Activities.PRIORITY_NORMAL, 
				null, // reportingContact 
				null, // scheduledEnd 
				scheduledStart
			);
			pm.currentTransaction().begin();
			NewActivityResult result = creator.newActivity(params);
			pm.currentTransaction().commit();
			if(result.getActivity() != null) {
				activity = (Activity)pm.getObjectById(result.getActivity().refGetPath());
			}
		}
		return activity;
	}
	
	@Override
	public String createOrUpdateDataItem(
		SyncRequest request, 
		String folderId,
		String itemId, 
		IData data
	) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		if(pm != null) {
			Path folderIdentity = DatatypeMappers.toObjectIdentity(folderId);
			if(folderIdentity != null) {
				RefObject_1_0 folder = null;
				try {
					folder = (RefObject_1_0)pm.getObjectById(folderIdentity);
				} catch(Exception e) {}
				if(folder != null) {
					RefObject_1_0 object = null;
					if(itemId == null) {
						switch(data.getType()) {

							case Calendar:
								if(folder instanceof ActivityGroup) {
									EventT eventT = (EventT)data;
									object = this.createActivity(
										(ActivityGroup)folder,
										Activities.ACTIVITY_CLASS_MEETING,
										eventT.getSubject(),
										eventT.getBody(),
										eventT.getStartTime()
									);									
								}
								break;
								
							case Contacts:
								if(folder instanceof Group) {
									pm.currentTransaction().begin();
									Group group = (Group)folder;
									String providerName = group.refGetPath().get(2);
									String segmentName = group.refGetPath().get(4);
									org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
										(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
											new Path("xri://@openmdx*org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName)
										);
									ContactT contactT = (ContactT)data;
									Contact contact = pm.newInstance(Contact.class);
									contact.refInitialize(false, false);
									contact.setLastName(contactT.getLastName());
									contact.setFirstName(contactT.getFirstName());
									contact.getOwningGroup().addAll(
										group.getOwningGroup()
									);
									Member member = pm.newInstance(Member.class);
									member.refInitialize(false, false);
									member.setName(contactT.getLastName() + ", " + contactT.getFirstName());
									member.setAccount(contact);
									group.addMember(
										Base.getInstance().getUidAsString(), 
										member
									);
									accountSegment.addAccount(
										Base.getInstance().getUidAsString(), 
										contact
									);
									pm.currentTransaction().commit();
									object = contact;
								}
								break;
								
							case Tasks:
								if(folder instanceof ActivityGroup) {
									TaskT taskT = (TaskT)data;
									object = this.createActivity(
										(ActivityGroup)folder,
										Activities.ACTIVITY_CLASS_TASK,
										taskT.getSubject(),
										taskT.getBody(),
										taskT.getStartdate()
									);									
								}
								break;
							
						}
					}
					else {
						Path objectIdentity = DatatypeMappers.toObjectIdentity(itemId);
						if(objectIdentity != null) {
							try {
								object = (RefObject_1_0)pm.getObjectById(objectIdentity);
							} catch(Exception e) {}
						}
					}
					if(object != null) {
						pm.currentTransaction().begin();
						DatatypeMappers.toObject(
							data,
							object							
						);
						pm.currentTransaction().commit();
						String objectId = DatatypeMappers.toObjectId(object);
						pm.close();
						return objectId;
					}
				}
			}
			pm.close();
		}
		return null;
	}

	@Override
	public String moveDataItem(
		SyncRequest request, 
		String srcFolderId,
		String dstFolderId,
		String itemId
	) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);		
		if(pm != null) {
			Path srcFolderIdentity = DatatypeMappers.toObjectIdentity(srcFolderId);
			Path dstFolderIdentity = DatatypeMappers.toObjectIdentity(dstFolderId);
			Path itemIdentity = DatatypeMappers.toObjectIdentity(itemId);
			if(srcFolderIdentity != null && dstFolderIdentity != null && itemIdentity != null) {
				RefObject_1_0 srcFolder = (RefObject_1_0)pm.getObjectById(srcFolderIdentity);
				RefObject_1_0 dstFolder = (RefObject_1_0)pm.getObjectById(dstFolderIdentity);
				RefObject_1_0 item = (RefObject_1_0)pm.getObjectById(itemIdentity);
				// Move activity
				if(srcFolder instanceof ActivityGroup && dstFolder instanceof ActivityGroup && item instanceof Activity) {
					Activity activity = (Activity)item;
					ActivityGroup srcGroup = (ActivityGroup)srcFolder;
					ActivityGroup dstGroup = (ActivityGroup)dstFolder;
					Collection<ActivityGroupAssignment> assignments = activity.getAssignedGroup();
					for(ActivityGroupAssignment assignment: assignments) {
						if(assignment.getActivityGroup().equals(srcGroup)) {
							pm.currentTransaction().begin();
							assignment.setActivityGroup(dstGroup);
							pm.currentTransaction().commit();		
							break;
						}
					}
				}
				// Move contact
				else if(srcFolder instanceof Group && dstFolder instanceof Group && item instanceof Contact) {
					Contact contact = (Contact)item;
					Group srcGroup = (Group)srcFolder;
					Group dstGroup = (Group)dstFolder;
					MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
					query.thereExistsAccount().equalTo(contact);
					List<Member> members = srcGroup.getMember(query);
					if(!members.isEmpty()) {
						Member srcMember = members.iterator().next();
						pm.currentTransaction().begin();
						srcMember.refDelete();
						Member dstMember = pm.newInstance(Member.class);
						dstMember.refInitialize(false, false);
						dstMember.setName(srcMember.getName());
						dstMember.setAccount(contact);
						dstGroup.addMember(
							Base.getInstance().getUidAsString(),
							dstMember
						);
						pm.currentTransaction().commit();
					}
				}
				pm.close();
				return itemId;
			}
			pm.close();
		}
		return null;
	}

	@Override
    public void setDataItemReadFlag(
    	SyncRequest request, 
    	String folderId, 
    	String itemId, 
    	boolean read
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);		
		if(pm != null) {
			Path itemIdentity = DatatypeMappers.toObjectIdentity(itemId);
			if(itemIdentity != null) {
				RefObject_1_0 item = (RefObject_1_0)pm.getObjectById(itemIdentity);
				if(item instanceof Alert) {
					Alert alert = (Alert)item;
					pm.currentTransaction().begin();
					if(read) {
						UserHomes.getInstance().markAsRead(alert);
					}
					else {
						UserHomes.getInstance().markAsNew(alert);
					}
					pm.currentTransaction().commit();
				}
			}
			pm.close();
		}
    }

	@Override
    public List<SyncDataItem> getChangedDataItems(
    	SyncRequest request, 
    	SyncCollection collection,
    	boolean noData,
    	int maxItems
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);			
		if(pm != null) {
			List<SyncDataItem> changedDataItems = new ArrayList<SyncDataItem>();
			Path folderIdentity = DatatypeMappers.toObjectIdentity(collection.getCollectionId());
			if(folderIdentity != null) {
				RefObject_1_0 folder = (RefObject_1_0)pm.getObjectById(folderIdentity);
				Date since = new Date(Long.valueOf(collection.getSyncKey()));
				// Activity groups
				if(folder instanceof ActivityGroup) {
					ActivityGroup activityGroup = (ActivityGroup)folder;
					List<ActivityQuery> queries = new ArrayList<ActivityQuery>(); 
					switch(collection.getDataType()) {
						case Calendar:  
							queries.add((ActivityQuery)pm.newQuery(Meeting.class));
							queries.add((ActivityQuery)pm.newQuery(Incident.class));
							break;
						case Tasks:  
							queries.add((ActivityQuery)pm.newQuery(Task.class));
							break;
						case Email:  
							queries.add((ActivityQuery)pm.newQuery(EMail.class));
							break;
					}
					int n = 0;
					for(ActivityQuery query: queries) {
						query.forAllDisabled().isFalse();
						query.modifiedAt().greaterThanOrEqualTo(since);
						query.orderByModifiedAt().ascending();
						List<Activity> activities = activityGroup.getFilteredActivity(query);
						for(Activity activity: activities) {
							changedDataItems.add(
								DatatypeMappers.toDataItem(activity, noData)
							);
							n++;
							if(n >= maxItems) break;
						}
					}
				}
				// Activity filters
				else if(folder instanceof AbstractFilterActivity) {
					AbstractFilterActivity activityFilter = (AbstractFilterActivity)folder;
					List<ActivityQuery> queries = new ArrayList<ActivityQuery>(); 
					switch(collection.getDataType()) {
						case Calendar:  
							queries.add((ActivityQuery)pm.newQuery(Meeting.class));
							queries.add((ActivityQuery)pm.newQuery(Incident.class));
							break;
						case Tasks:  
							queries.add((ActivityQuery)pm.newQuery(Task.class));
							break;
						case Email:  
							queries.add((ActivityQuery)pm.newQuery(EMail.class));
							break;
					}
					int n = 0;
					for(ActivityQuery query: queries) {
						query.forAllDisabled().isFalse();
						query.modifiedAt().greaterThanOrEqualTo(since);
						query.orderByModifiedAt().ascending();
						List<Activity> activities = activityFilter.getFilteredActivity(query);
						for(Activity activity: activities) {
							changedDataItems.add(
								DatatypeMappers.toDataItem(activity, noData)
							);
							n++;
							if(n >= maxItems) break;
						} 
					}
				}
				// Contacts
				else if(folder instanceof Group) {
					Group group = (Group)folder;
					Set<Path> changedContacts = new HashSet<Path>();
					// Get all changed contacts
					MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
					query.forAllDisabled().isFalse();
					query.thereExistsAccount().modifiedAt().greaterThanOrEqualTo(since);
					query.thereExistsAccount().forAllDisabled().isFalse();
					List<Member> members = group.getMember(query);
					int n = 0;
					for(Member member: members) {
						if(!changedContacts.contains(member.getAccount().refGetPath())) {
							changedDataItems.add(
								DatatypeMappers.toDataItem(member.getAccount(), noData)
							);
							changedContacts.add(member.getAccount().refGetPath());
							n++;
							if(n >= maxItems) break;
						}
					}
					// Get contacts where the membership changed but not the underlying contact
					query = (MemberQuery)pm.newQuery(Member.class);
					query.forAllDisabled().isFalse();
					query.modifiedAt().greaterThanOrEqualTo(since);
					members = group.getMember(query);				
					for(Member member: members) {
						if(!changedContacts.contains(member.getAccount().refGetPath())) {
							changedDataItems.add(
								DatatypeMappers.toDataItem(member.getAccount(), noData)
							);
							changedContacts.add(member.getAccount().refGetPath());
							n++;
							if(n >= maxItems) break;
						}
					}
				}
				else if(folder instanceof UserHome) {
					UserHome userHome = (UserHome)folder;
					AlertQuery query = (AlertQuery)pm.newQuery(Alert.class);
					query.modifiedAt().greaterThanOrEqualTo(since);
					query.orderByModifiedAt().ascending();
					query.alertState().lessThan(UserHomes.ALERT_STATE_ACCEPTED);
					List<Alert> alerts = userHome.getAlert(query);
					int n = 0;
					for(Alert alert: alerts) {
						changedDataItems.add(
							DatatypeMappers.toDataItem(alert, noData)
						);
						n++;
						if(n >= maxItems) break;
					}
				}
			}
			pm.close();
			return changedDataItems;
		}
	    return null;
    }

	@Override
    public List<String> getDeletedDataItems(
    	SyncRequest request, 
    	SyncCollection collection 
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);	
		List<String> deletedDataItems = new ArrayList<String>();
		if(pm != null) {
			Path folderIdentity = DatatypeMappers.toObjectIdentity(collection.getCollectionId());
			if(folderIdentity != null) {
				RefObject_1_0 folder = (RefObject_1_0)pm.getObjectById(folderIdentity);
				Date since = new Date(Long.valueOf(collection.getSyncKey()));
				// Activities
				if(folder instanceof ActivityGroup) {
					ActivityGroup activityGroup = (ActivityGroup)folder;
					List<ActivityQuery> queries = new ArrayList<ActivityQuery>(); 
					switch(collection.getDataType()) {
						case Calendar:  
							queries.add((ActivityQuery)pm.newQuery(Meeting.class));
							queries.add((ActivityQuery)pm.newQuery(Incident.class));
							break;
						case Tasks:  
							queries.add((ActivityQuery)pm.newQuery(Task.class));
							break;
						case Email:  
							queries.add((ActivityQuery)pm.newQuery(EMail.class));
							break;
					}
					for(ActivityQuery query: queries) {
						query.thereExistsDisabled().isTrue();
						query.modifiedAt().greaterThanOrEqualTo(since);
						query.orderByModifiedAt().ascending();
						List<Activity> activities = activityGroup.getFilteredActivity(query);
						for(Activity activity: activities) {
							deletedDataItems.add(
								DatatypeMappers.toObjectId(activity)
							);
						}
					}
				}
				// Contacts
				else if(folder instanceof Group) {
					Group group = (Group)folder;
					Set<Path> deletedContacts = new HashSet<Path>();
					// Get all deleted contacts
					MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
					query.thereExistsAccount().modifiedAt().greaterThanOrEqualTo(since);
					query.thereExistsAccount().thereExistsDisabled().isTrue();
					List<Member> members = group.getMember(query);
					for(Member member: members) {
						if(
							member.getAccount() instanceof Contact && 
							!deletedContacts.contains(member.getAccount().refGetPath())
						) {
							deletedDataItems.add(
								DatatypeMappers.toObjectId(member.getAccount())
							);
							deletedContacts.add(member.getAccount().refGetPath());
						}
					}
					// Get contacts where the membership changed but not the underlying contact
					query = (MemberQuery)pm.newQuery(Member.class);
					query.thereExistsDisabled().isTrue();
					query.modifiedAt().greaterThanOrEqualTo(since);
					members = group.getMember(query);
					for(Member member: members) {
						if(
							member.getAccount() instanceof Contact && 
							!deletedContacts.contains(member.getAccount().refGetPath())
						) {
							deletedDataItems.add(
								DatatypeMappers.toObjectId(member.getAccount())
							);
							deletedContacts.add(member.getAccount().refGetPath());
						}
					}
				}
				else if(folder instanceof UserHome) {
					UserHome userHome = (UserHome)folder;
					AlertQuery query = (AlertQuery)pm.newQuery(Alert.class);
					query.modifiedAt().greaterThanOrEqualTo(since);
					query.alertState().greaterThanOrEqualTo(UserHomes.ALERT_STATE_ACCEPTED);		
					query.orderByModifiedAt().ascending();
					List<Alert> alerts = userHome.getAlert(query);
					for(Alert alert: alerts) {
						deletedDataItems.add(
							DatatypeMappers.toObjectId(alert)
						);
					}
				}		
			}
			pm.close();
		}
	    return deletedDataItems;
    }

	@Override
    public String createOrUpdateFolder(
    	SyncRequest request, 
    	SyncFolder folder
    ) {
		// Creation or update of folders is not supported.
		// Folders must be managed on the user home's AirSync profile.
		return null;
    }

	@Override
    public String deleteFolder(
    	SyncRequest request, 
    	String folderId
    ) {
		// Deletion of folders is not supported.
		// Folders must be managed on the user home's AirSync profile.
	    return null;
    }

    public List<SyncFolder> getFolders(
    	SyncRequest request, 
    	SyncState syncState,
    	boolean activeFoldersOnly
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		List<SyncFolder> changedFolders = new ArrayList<SyncFolder>();
		if(pm != null) {
			Date since = new Date(syncState.getValue());
			UserHome userHome = this.getUserHome(request); 
			AirSyncProfileQuery profileQuery = (AirSyncProfileQuery)pm.newQuery(AirSyncProfile.class);
			profileQuery.name().equalTo("AirSync");
			List<AirSyncProfile> profiles = userHome.getSyncProfile(profileQuery);
			if(!profiles.isEmpty()) {				
				SyncFolder syncFolder = new SyncFolder();				
				AirSyncProfile profile = profiles.iterator().next();
				SyncFeedQuery feedQuery = (SyncFeedQuery)pm.newQuery(SyncFeed.class);
				feedQuery.modifiedAt().greaterThanOrEqualTo(since);
				if(activeFoldersOnly) {
					feedQuery.thereExistsIsActive().isTrue();
				} else {
					feedQuery.forAllIsActive().isFalse();					
				}
				List<SyncFeed> feeds = profile.getFeed(feedQuery);
				// Create sync folders for configured feeds
				for(SyncFeed feed: feeds) {
					// Activity filters
					if(feed instanceof ActivityFilterCalendarFeed) {
						ActivityFilterCalendarFeed activityFilterFeed = (ActivityFilterCalendarFeed)feed;
						AbstractFilterActivity activityFilter = activityFilterFeed.getActivityFilter();
						// Meeting
						syncFolder = new SyncFolder();
						syncFolder.setServerId(DatatypeMappers.toObjectId(activityFilter) + "?type=" + Activities.ACTIVITY_CLASS_MEETING);
						syncFolder.setFolderType(FolderType.USER_CREATED_CALENDAR_FOLDER);
						syncFolder.setDisplayName(feed.getName() + " - Calendar");
						syncFolder.setParentId("0");
						changedFolders.add(syncFolder);
						// EMail
						syncFolder = new SyncFolder();
						syncFolder.setServerId(DatatypeMappers.toObjectId(activityFilter) + "?type=" + Activities.ACTIVITY_CLASS_EMAIL);
						syncFolder.setFolderType(FolderType.USER_CREATED_EMAIL_FOLDER);
						syncFolder.setDisplayName(feed.getName() + " - Mails");
						syncFolder.setParentId("0");
						changedFolders.add(syncFolder);
						// Tasks
						syncFolder = new SyncFolder();
						syncFolder.setServerId(DatatypeMappers.toObjectId(activityFilter) + "?type=" + Activities.ACTIVITY_CLASS_TASK);
						syncFolder.setFolderType(FolderType.USER_CREATED_TASKS_FOLDER);
						syncFolder.setDisplayName(feed.getName() + " - Tasks");
						syncFolder.setParentId("0");
						changedFolders.add(syncFolder);						
					}
					// Activity groups
					else if(feed instanceof ActivityGroupCalendarFeed) {
						ActivityGroupCalendarFeed activityGroupFeed = (ActivityGroupCalendarFeed)feed;
						ActivityGroup activityGroup = activityGroupFeed.getActivityGroup();
						Collection<ActivityCreator> activityCreators = activityGroup.getActivityCreator();
						// Create a folder for each creator
						for(ActivityCreator activityCreator: activityCreators) {
							int activityClass = activityCreator.getActivityType().getActivityClass();
							if(activityClass == Activities.ACTIVITY_CLASS_MEETING) {
								syncFolder = new SyncFolder();
								syncFolder.setServerId(DatatypeMappers.toObjectId(activityGroup) + "?type=" + Activities.ACTIVITY_CLASS_MEETING);
								syncFolder.setFolderType(FolderType.USER_CREATED_CALENDAR_FOLDER);
								syncFolder.setDisplayName(feed.getName() + " - Calendar");
								syncFolder.setParentId("0");
								changedFolders.add(syncFolder);
							} else if(activityClass == Activities.ACTIVITY_CLASS_EMAIL) {
								syncFolder = new SyncFolder();
								syncFolder.setServerId(DatatypeMappers.toObjectId(activityGroup) + "?type=" + Activities.ACTIVITY_CLASS_EMAIL);
								syncFolder.setFolderType(FolderType.USER_CREATED_EMAIL_FOLDER);
								syncFolder.setDisplayName(feed.getName() + " - Mails");
								syncFolder.setParentId("0");
								changedFolders.add(syncFolder);
							} else if(activityClass == Activities.ACTIVITY_CLASS_TASK) {
								syncFolder = new SyncFolder();
								syncFolder.setServerId(DatatypeMappers.toObjectId(activityGroup) + "?type=" + Activities.ACTIVITY_CLASS_TASK);
								syncFolder.setFolderType(FolderType.USER_CREATED_TASKS_FOLDER);
								syncFolder.setDisplayName(feed.getName() + " - Tasks");
								syncFolder.setParentId("0");
								changedFolders.add(syncFolder);
							}
						}
					}
					// Contacts
					else if(feed instanceof ContactsFeed) {
						ContactsFeed contactsFeed = (ContactsFeed)feed;
						Group group = contactsFeed.getAccountGroup();
						syncFolder = new SyncFolder();
						syncFolder.setServerId(DatatypeMappers.toObjectId(group));
						syncFolder.setFolderType(FolderType.USER_CREATED_CONTACTS_FOLDER);
						syncFolder.setDisplayName(feed.getName() + " - Contacts");
						syncFolder.setParentId("0");
						changedFolders.add(syncFolder);						
					}
				}
				// User home as feed for alerts
				if(activeFoldersOnly && userHome.getModifiedAt().compareTo(since) > 0) {
					syncFolder = new SyncFolder();
					syncFolder.setServerId(DatatypeMappers.toObjectId(userHome));
					syncFolder.setFolderType(FolderType.USER_CREATED_EMAIL_FOLDER);
					syncFolder.setDisplayName(userHome.refGetPath().getBase() + " - Alerts");
					syncFolder.setParentId("0");
					changedFolders.add(syncFolder);
				}
			}
			pm.close();
		}
		return changedFolders;
    }
	
	@Override
    public List<SyncFolder> getChangedFolders(
    	SyncRequest request, 
    	SyncState syncState
    ) throws ServiceException {
		return this.getFolders(
			request, 
			syncState, 
			true // activeFoldersOnly
		);
    }

	@Override
    public List<SyncFolder> getDeletedFolders(
    	SyncRequest request, 
    	SyncState syncState
    ) throws ServiceException {
		return this.getFolders(
			request, 
			syncState, 
			false // activeFoldersOnly
		);
    }

	@Override
    public SyncDataItem.State getDataItemState(
    	SyncRequest request, 
    	SyncCollection collection, 
    	SyncDataItem dataItem
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
		if(pm != null) {
			Path objectIdentity = DatatypeMappers.toObjectIdentity(dataItem.getServerId());
			if(objectIdentity != null) {
				RefObject_1_0 object = (RefObject_1_0)pm.getObjectById(objectIdentity);
				if(object instanceof BasicObject) {
					BasicObject item = (BasicObject)object;
					SyncState syncState = this.getSyncState(request, collection.getSyncKey());
					if(item.getCreatedAt().getTime() >= syncState.getValue()) {
						pm.close();
						return SyncDataItem.State.NEW;
					} else if(item.getModifiedAt().getTime() >= syncState.getValue()) {
						pm.close();
						return SyncDataItem.State.MODIFIED;
					} else {
						pm.close();
						return SyncDataItem.State.CLEAN;
					}
				}
			}
			pm.close();
		}
	    return  SyncDataItem.State.UNKNOWN;
    }

	@Override
    public void sendMail(
    	SyncRequest request, 
    	InputStream mimeMessage
    ) throws ServiceException {
		PersistenceManager pm = this.getPersistenceManager(request);
        Transport transport = null;		
		if(pm != null) {
			UserHome userHome = this.getUserHome(request);
			if(userHome != null) {
				String providerName = userHome.refGetPath().get(2);
				String segmentName = userHome.refGetPath().get(4);
				MimeMessage message = null;
				// Send EMail if mail session is configured and available
				try {
	                Context initialContext = new InitialContext();				
			        Session session = null;
			        String mailServiceName = "/mail/provider/" + providerName + "/segment/" + segmentName;		        
			        try {
			            session = (Session)initialContext.lookup("java:comp/env" + mailServiceName);
			        } catch(Exception e) {    
			        	SysLog.detail("Mail service not found", mailServiceName);
			            // Fallback to mail/provider/<provider>
			            mailServiceName = "/mail/provider/" + providerName;
			            SysLog.detail("Fall back to mail service", mailServiceName);
			            session = (Session)initialContext.lookup("java:comp/env" + mailServiceName);
			        }			
					message = new MimeMessage(session, request.getInputStream());	
		            transport = session.getTransport();
		            String protocol = transport.getURLName().getProtocol();
		            String port = session.getProperty("mail." + protocol + ".port");
		            transport.connect(
		                session.getProperty("mail." + protocol + ".host"), 
		                port == null ? -1 : Integer.valueOf(port).intValue(),                           
		                session.getProperty("mail." + protocol + ".user"), 
		                session.getProperty("mail." + protocol + ".password")
		            );
		            transport.sendMessage(
		                message,
		                message.getAllRecipients()
		            );		            
				} catch(Exception e) {
					pm.close();
					throw new ServiceException(e);
				} finally {
		            if(transport != null) {
		                try {
		                    transport.close();
		                } catch(Exception e) {}
		            }
		        }
				// Import EMail
				try {
					ActivityCreator emailCreator = (ActivityCreator)pm.getObjectById(
						new Path("xri://@openmdx*org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName + "/activityCreator/" + userHome.refGetPath().getBase() + "~E-Mails")
					);
					if(emailCreator != null && message != null) {
						Activities.getInstance().importMimeMessage(
							providerName, 
							segmentName, 
							message, 
							emailCreator, 
                            message.getFrom(),
                            message.getRecipients(Message.RecipientType.TO),
                            message.getRecipients(Message.RecipientType.CC),
                            message.getRecipients(Message.RecipientType.BCC),
							false, // isEMailAddressLookupCaseInsensitive 
							true // isEMailAddressLookupIgnoreDisabled
						);
					}
				} catch(Exception e) {
					pm.close();
					throw new ServiceException(e);
				} finally {
					try {
						pm.currentTransaction().rollback();
					} catch(Exception e1) {}
		        }
			}
			pm.close();
		}
    }

	@Override
    public File getContextTempDir(
    	SyncRequest request,
    	File tempDir
    ) throws ServiceException {
		UserHome userHome = this.getUserHome(request);
		if(userHome != null) {
			String providerName = userHome.refGetPath().get(2);
			String segmentName = userHome.refGetPath().get(4);
			return new File(tempDir, providerName + File.separator + segmentName);
		} else {
			return tempDir;
		}
    }

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	
}

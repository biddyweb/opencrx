/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestHandlers.java,v 1.16 2010/03/29 10:42:44 wfro Exp $
 * Description: TestHandlers
 * Revision:    $Revision: 1.16 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/29 10:42:44 $
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
package test.org.opencrx.application.airsync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.xml.transform.stream.StreamResult;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.opencrx.application.airsync.datatypes.AddressT;
import org.opencrx.application.airsync.datatypes.AttachmentDataT;
import org.opencrx.application.airsync.datatypes.AttachmentT;
import org.opencrx.application.airsync.datatypes.AttendeeStatus;
import org.opencrx.application.airsync.datatypes.AttendeeT;
import org.opencrx.application.airsync.datatypes.AttendeeType;
import org.opencrx.application.airsync.datatypes.BusyStatus;
import org.opencrx.application.airsync.datatypes.ContactT;
import org.opencrx.application.airsync.datatypes.DataType;
import org.opencrx.application.airsync.datatypes.EmailBodyT;
import org.opencrx.application.airsync.datatypes.EmailT;
import org.opencrx.application.airsync.datatypes.EventT;
import org.opencrx.application.airsync.datatypes.FolderType;
import org.opencrx.application.airsync.datatypes.IData;
import org.opencrx.application.airsync.datatypes.Importance;
import org.opencrx.application.airsync.datatypes.MeetingStatus;
import org.opencrx.application.airsync.datatypes.MessageClass;
import org.opencrx.application.airsync.datatypes.MimeType;
import org.opencrx.application.airsync.datatypes.RecurrenceT;
import org.opencrx.application.airsync.datatypes.Sensitivity;
import org.opencrx.application.airsync.datatypes.TaskT;
import org.opencrx.application.airsync.server.SyncCollection;
import org.opencrx.application.airsync.server.SyncDataItem;
import org.opencrx.application.airsync.server.SyncFolder;
import org.opencrx.application.airsync.server.SyncRequest;
import org.opencrx.application.airsync.server.SyncState;
import org.opencrx.application.airsync.server.SyncDataItem.State;
import org.opencrx.application.airsync.server.handler.FolderSyncHandler;
import org.opencrx.application.airsync.server.handler.GetItemEstimateHandler;
import org.opencrx.application.airsync.server.handler.MoveItemsHandler;
import org.opencrx.application.airsync.server.handler.PingHandler;
import org.opencrx.application.airsync.server.handler.SyncHandler;
import org.opencrx.application.airsync.server.spi.ISyncBackend;
import org.opencrx.application.airsync.utils.DOMUtils;
import org.opencrx.application.airsync.utils.WbXMLTransformer;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.lightweight.naming.NonManagedInitialContextFactoryBuilder;
import org.openmdx.kernel.loading.Classes;
import org.w3c.dom.Document;

import test.org.opencrx.generic.AbstractTest;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestHandlers.TestAll.class
    }
)

public class TestHandlers {

	protected static class TestBackend implements ISyncBackend {

		@Override
		public String createOrUpdateDataItem(
			SyncRequest request,
			String folderId, 
			String itemId, 
			IData data
		) throws ServiceException {
			if(itemId == null) {
				return UUIDs.getGenerator().next().toString();
			} else {
				return itemId;
			}
		}

		@Override
		public String createOrUpdateFolder(
			SyncRequest request,
			SyncFolder folder
		) throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deleteDataItem(
			SyncRequest request, 
			String folderId,
			String itemId
		) throws ServiceException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String deleteFolder(
			SyncRequest request, 
			String folderId
		) throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SyncDataItem fetchDataItem(
			SyncRequest request, 
			String folderId,
			String itemId
		) throws ServiceException {
			SyncCollection collection = new SyncCollection();
			collection.setCollectionId(folderId);
			if(itemId.startsWith("Calendar://")) {
				collection.setDataType(DataType.Calendar);
			}
			else if(itemId.startsWith("Contacts://")) {
				collection.setDataType(DataType.Contacts);
			}
			else if(itemId.startsWith("Tasks://")) {
				collection.setDataType(DataType.Tasks);
			}
			else if(itemId.startsWith("Email://")) {
				collection.setDataType(DataType.Email);
			}
			return this.getChangedDataItems(
				request, 
				collection, 
				false, 
				1
			).iterator().next();
		}

		@Override
		public AttachmentDataT getAttachementData(
			SyncRequest request,
			String attachmentId
		) throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<SyncDataItem> getChangedDataItems(
			SyncRequest request,
			SyncCollection collection,
			boolean noData,
			int maxItems
		) throws ServiceException {
			List<SyncDataItem> changedDataItems = new ArrayList<SyncDataItem>();
			switch(collection.getDataType()) {
				case Contacts: {
					// Contact 1
					ContactT contactT = new ContactT();
					contactT.setBusinessPhoneNumber("(01) 71 19 13 01");
					contactT.setCompanyName("RMC");
					contactT.setFileAs("WEILL");
					contactT.setFirstName("WEILL");
					contactT.setLastName("Président");
					contactT.setLastName("-");
					SyncDataItem dataItem = new SyncDataItem();
					dataItem.setData(contactT);
					dataItem.setServerId("2147483712");
					changedDataItems.add(dataItem);
					// Contact 2
					contactT = new ContactT();
					contactT.setBusinessAddressCity("- St Cloud");
					contactT.setBusinessPostalCode("92210");
					contactT.setBusinessPhoneNumber("(01) 49 66 12 83");
					contactT.setCompanyName("RMC");
					contactT.setFileAs("Le chalet de l'Oasis");
					contactT.setJobTitle("Restaurant");
					contactT.setFirstName("WEILL");
					contactT.setLastName("Le chalet de l'Oasis");
					contactT.setMobilePhoneNumber("(06) 80 72 85 58");
					dataItem = new SyncDataItem();
					dataItem.setData(contactT);
					dataItem.setServerId("2147483714");
					changedDataItems.add(dataItem);
					// Contact 3
					contactT = new ContactT();
					contactT.setBusinessPhoneNumber("(01) 53 26 65 65");
					contactT.setCompanyName("20 MINUTES");
					contactT.setFileAs("ASSAYAG,William");
					contactT.setJobTitle("JOURNALISTE");
					contactT.setFirstName("William");
					contactT.setLastName("ASSAYAG");
					contactT.setMobilePhoneNumber("(06) 80 72 85 58");
					dataItem = new SyncDataItem();
					dataItem.setData(contactT);
					dataItem.setServerId("2147483714");
					changedDataItems.add(dataItem);
					// Contact 4
					contactT = new ContactT();
					contactT.setBusinessAddressCity("PARIS 15");
					contactT.setBusinessPostalCode("75015");
					contactT.setBusinessPhoneNumber("+33142503529");
					contactT.setCompanyName("20 MINUTES");
					contactT.setFileAs("Le Cristal De Sel");
					contactT.setJobTitle("JOURNALISTE");
					contactT.setLastName("Le Cristal De Sel");
					contactT.setMobilePhoneNumber("(06) 80 72 85 58");
					dataItem = new SyncDataItem();
					dataItem.setData(contactT);
					dataItem.setServerId("2147483716");
					changedDataItems.add(dataItem);
					// Contact 5
					contactT = new ContactT();
					contactT.setBusinessAddressCity("PARIS 15");
					contactT.setBusinessPostalCode("75015");
					contactT.setBusinessPhoneNumber("+33142503529");
					contactT.setCompanyName("20 MINUTES");
					contactT.setFileAs("FORGUES,Wilfrid");
					contactT.setFirstName("Wilfrid");
					contactT.setHomePhoneNumber("(05) 63 40 03 77");
					contactT.setJobTitle("JOURNALISTE");
					contactT.setLastName("FORGUES");
					contactT.setMobilePhoneNumber("(06) 80 72 85 58");
					dataItem = new SyncDataItem();
					dataItem.setData(contactT);
					dataItem.setServerId("2147483717");
					changedDataItems.add(dataItem);					
					break;
				}
				case Tasks: {
					// Task 1
					TaskT taskT = new TaskT();
					taskT.setBody("Task 1");
					taskT.setCategories(Arrays.asList("cat1", "cat2", "cat3"));
					taskT.setComplete(true);
					taskT.setDatecompleted(new Date());
					taskT.setDuedate(new Date());
					taskT.setUtcduedate(new Date());
					taskT.setImportance(Importance.HIGH);
					taskT.setRecurrence(new RecurrenceT());
					taskT.setReminderset("reminder set");
					taskT.setRemindertime(new Date());
					taskT.setSensitivity(Sensitivity.CONFIDENTIAL);
					taskT.setStartdate(new Date());
					taskT.setUtcstartdate(new Date());
					taskT.setSubject("Task 1");				
					SyncDataItem dataItem = new SyncDataItem();
					dataItem.setData(taskT);
					dataItem.setServerId("12345678");
					changedDataItems.add(dataItem);
					// Task 2
					taskT = new TaskT();
					taskT.setBody("Task 2");
					taskT.setCategories(Arrays.asList("cat1", "cat2", "cat3"));
					taskT.setComplete(true);
					taskT.setDatecompleted(new Date());
					taskT.setDuedate(new Date());
					taskT.setUtcduedate(new Date());
					taskT.setImportance(Importance.HIGH);
					taskT.setRecurrence(new RecurrenceT());
					taskT.setReminderset("reminder set");
					taskT.setRemindertime(new Date());
					taskT.setSensitivity(Sensitivity.CONFIDENTIAL);
					taskT.setStartdate(new Date());
					taskT.setUtcstartdate(new Date());
					taskT.setSubject("Task 1");				
					dataItem = new SyncDataItem();
					dataItem.setData(taskT);
					dataItem.setServerId("12345679");
					changedDataItems.add(dataItem);					
					break;
				}
				case Email: {
					AddressT from = new AddressT("from@opencrx.org");
					AddressT replyTo = new AddressT("replyto@opencrx.org");
					List<AddressT> to = new ArrayList<AddressT>();
					to.add(new AddressT("to1@opencrx.org"));
					to.add(new AddressT("to2@opencrx.org"));
					to.add(new AddressT("to3@opencrx.org"));
					List<AddressT> cc = new ArrayList<AddressT>();
					cc.add(new AddressT("cc1@opencrx.org"));
					cc.add(new AddressT("cc2@opencrx.org"));
					cc.add(new AddressT("cc3@opencrx.org"));
					List<AddressT> bcc = new ArrayList<AddressT>();
					bcc.add(new AddressT("bcc1@opencrx.org"));
					bcc.add(new AddressT("bcc2@opencrx.org"));
					bcc.add(new AddressT("bcc3@opencrx.org"));
					List<AttachmentT> attachments = new ArrayList<AttachmentT>();
					AttachmentT attachmentT = new AttachmentT();
					attachmentT.setAttachmentName("attachment 1 name");
					attachmentT.setDisplayName("attachment 1 display name");
					attachmentT.setContentLocation("file://tmp/attachment1.txt");
					attachments.add(attachmentT);
					attachmentT = new AttachmentT();
					attachmentT.setAttachmentName("attachment 2 name");
					attachmentT.setDisplayName("attachment 2 display name");
					attachmentT.setContentLocation("file://tmp/attachment2.txt");
					attachments.add(attachmentT);
					// Email 1
					EmailT emailT = new EmailT();
					emailT.setSubject("Email 1");
					EmailBodyT emailBodyT = new EmailBodyT();
					emailBodyT.setData("Body 1");
					emailBodyT.setType(MimeType.PlainText);
					emailT.setBody(emailBodyT);
					emailT.setDateReceived(new Date());
					emailT.setAttachements(attachments);
					emailT.setMessageClass(MessageClass.Note);
					emailT.setImportance(Importance.HIGH);
					emailT.setFrom(from);
					emailT.setTo(to);
					emailT.setCc(cc);
					emailT.setBcc(bcc);
					emailT.setReplyTo(replyTo);					
					SyncDataItem dataItem = new SyncDataItem();
					dataItem.setData(emailT);
					dataItem.setServerId("11111111");
					changedDataItems.add(dataItem);
					// Email 2
					emailT = new EmailT();
					emailT.setSubject("Email 2");
					emailBodyT = new EmailBodyT();
					emailBodyT.setData("Body 2");
					emailBodyT.setType(MimeType.PlainText);
					emailT.setBody(emailBodyT);
					emailT.setDateReceived(new Date());
					emailT.setAttachements(attachments);
					emailT.setMessageClass(MessageClass.Note);
					emailT.setImportance(Importance.HIGH);
					emailT.setFrom(from);
					emailT.setTo(to);
					emailT.setCc(cc);
					emailT.setBcc(bcc);
					emailT.setReplyTo(replyTo);					
					dataItem = new SyncDataItem();
					dataItem.setData(emailT);
					dataItem.setServerId("2222222222");
					changedDataItems.add(dataItem);
					break;
				}
				case Calendar: {
					List<AttendeeT> attendees = new ArrayList<AttendeeT>();
					AttendeeT attendeeT = new AttendeeT();
					attendeeT.setName("Attendee 1");
					attendeeT.setEmail("attendee1@opencrx.org");
					attendeeT.setAttendeeType(AttendeeType.OPTIONAL);
					attendeeT.setAttendeeStatus(AttendeeStatus.ACCEPT);
					attendees.add(attendeeT);
					attendeeT = new AttendeeT();
					attendeeT.setName("Attendee 2");
					attendeeT.setEmail("attendee2@opencrx.org");
					attendeeT.setAttendeeType(AttendeeType.OPTIONAL);
					attendeeT.setAttendeeStatus(AttendeeStatus.ACCEPT);
					attendees.add(attendeeT);
					attendeeT = new AttendeeT();
					attendeeT.setName("Attendee 3");
					attendeeT.setEmail("attendee3@opencrx.org");
					attendeeT.setAttendeeType(AttendeeType.OPTIONAL);
					attendeeT.setAttendeeStatus(AttendeeStatus.ACCEPT);
					attendees.add(attendeeT);
					// Event 1
					EventT eventT = new EventT();
					eventT.setOrganizerName("Test Organizer");
					eventT.setOrganizerEmail("event-organizer@opencrx.org");
					eventT.setLocation("Location Event 1");
					eventT.setSubject("Subject Event 1");
					eventT.setUID("C62B950B-0FC0-3A19-9D65-96E5F42F64E8");
					eventT.setBody("Event 1");
					eventT.setDtStamp(new Date());
					eventT.setEndTime(new Date());
					eventT.setStartTime(new Date());
					eventT.setAllDayEvent(true);
					eventT.setBusyStatus(BusyStatus.FREE);
					eventT.setSensitivity(Sensitivity.CONFIDENTIAL);
					eventT.setMeetingStatus(MeetingStatus.IS_A_MEETING);
					eventT.setReminder(3);
					eventT.setAttendees(attendees);
					eventT.setCategories(Arrays.asList("cat1", "cat2", "cat3"));
					eventT.setRecurrence(new RecurrenceT());
					eventT.setExceptions(new ArrayList<EventT>());
					eventT.setExceptionStartTime(new Date());					
					SyncDataItem dataItem = new SyncDataItem();
					dataItem.setData(eventT);
					dataItem.setServerId("111111111");
					changedDataItems.add(dataItem);
					// Event 2
					eventT = new EventT();
					eventT.setOrganizerName("Test Organizer");
					eventT.setOrganizerEmail("event-organizer@opencrx.org");
					eventT.setLocation("Location Event 2");
					eventT.setSubject("Subject Event 2");
					eventT.setUID("C62B950B-0FC0-3A19-9D65-96E5F42F64E8");
					eventT.setBody("Event 2");
					eventT.setDtStamp(new Date());
					eventT.setEndTime(new Date());
					eventT.setStartTime(new Date());
					eventT.setAllDayEvent(true);
					eventT.setBusyStatus(BusyStatus.FREE);
					eventT.setSensitivity(Sensitivity.CONFIDENTIAL);
					eventT.setMeetingStatus(MeetingStatus.IS_A_MEETING);
					eventT.setReminder(3);
					eventT.setAttendees(attendees);
					eventT.setCategories(Arrays.asList("cat1", "cat2", "cat3"));
					eventT.setRecurrence(new RecurrenceT());
					eventT.setExceptions(new ArrayList<EventT>());
					eventT.setExceptionStartTime(new Date());					
					dataItem = new SyncDataItem();
					dataItem.setData(eventT);
					dataItem.setServerId("22222222");
					changedDataItems.add(dataItem);
					break;
				}
			}
			return changedDataItems;
		}

		@Override
		public List<SyncFolder> getChangedFolders(
			SyncRequest request,
			SyncState syncState
		) throws ServiceException {
			List<SyncFolder> changedFolders = new ArrayList<SyncFolder>();

			// Folder 1
			SyncFolder folder = new SyncFolder();
			folder.setDisplayName("Deleted Items");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-ce6a");
			folder.setFolderType(FolderType.DEFAULT_DELETED_ITEMS_FOLDERS);
			folder.setParentId("0");
			changedFolders.add(folder);
			// Folder 2
			folder = new SyncFolder();
			folder.setDisplayName("Drafts");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-df2a");
			folder.setFolderType(FolderType.DEFAULT_DRAFTS_FOLDERS);
			folder.setParentId("0");
			changedFolders.add(folder);
			// Folder 3
			folder = new SyncFolder();
			folder.setDisplayName("Inbox");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-ce67");
			folder.setFolderType(FolderType.DEFAULT_INBOX_FOLDER);
			folder.setParentId("0");
			changedFolders.add(folder);
			// Folder 4
			folder = new SyncFolder();
			folder.setDisplayName("Junk E-mail");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-ce67");
			folder.setFolderType(FolderType.USER_FOLDER_GENERIC);
			folder.setParentId("0");
			changedFolders.add(folder);
			// Folder 5
			folder = new SyncFolder();
			folder.setDisplayName("Outbox");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-ce68");
			folder.setFolderType(FolderType.DEFAULT_OUTBOX_FOLDER);
			folder.setParentId("0");
			changedFolders.add(folder);
			// Folder 6
			folder = new SyncFolder();
			folder.setDisplayName("Sent Items");
			folder.setServerId("2b9c185f572fd511beaf00b0d079d711-ce69");
			folder.setFolderType(FolderType.DEFAULT_SENT_EMAIL_FOLDER);
			folder.setParentId("0");
			changedFolders.add(folder);
			
			return changedFolders;
		}

		@Override
		public List<String> getDeletedDataItems(
			SyncRequest request,
			SyncCollection collection
		) {
			return null;
		}

		@Override
		public List<SyncFolder> getDeletedFolders(
			SyncRequest request,
			SyncState syncState
		) throws ServiceException {
			List<SyncFolder> deletedFolders = new ArrayList<SyncFolder>();

			// Folder 1
			SyncFolder folder = new SyncFolder();
			folder.setDisplayName("Deleted Items");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-ce6a");
			folder.setFolderType(FolderType.DEFAULT_DELETED_ITEMS_FOLDERS);
			folder.setParentId("0");
			deletedFolders.add(folder);
			// Folder 2
			folder = new SyncFolder();
			folder.setDisplayName("Drafts");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-df2a");
			folder.setFolderType(FolderType.DEFAULT_DRAFTS_FOLDERS);
			folder.setParentId("0");
			deletedFolders.add(folder);
			// Folder 3
			folder = new SyncFolder();
			folder.setDisplayName("Inbox");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-ce67");
			folder.setFolderType(FolderType.DEFAULT_INBOX_FOLDER);
			folder.setParentId("0");
			deletedFolders.add(folder);
			// Folder 4
			folder = new SyncFolder();
			folder.setDisplayName("Junk E-mail");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-ce67");
			folder.setFolderType(FolderType.USER_FOLDER_GENERIC);
			folder.setParentId("0");
			deletedFolders.add(folder);
			// Folder 5
			folder = new SyncFolder();
			folder.setDisplayName("Outbox");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-ce68");
			folder.setFolderType(FolderType.DEFAULT_OUTBOX_FOLDER);
			folder.setParentId("0");
			deletedFolders.add(folder);
			// Folder 6
			folder = new SyncFolder();
			folder.setDisplayName("Sent Items");
			folder.setServerId("deleted-2b9c185f572fd511beaf00b0d079d711-ce69");
			folder.setFolderType(FolderType.DEFAULT_SENT_EMAIL_FOLDER);
			folder.setParentId("0");
			deletedFolders.add(folder);
			
			return deletedFolders;
		}

		@Override
		public String getNewSyncKey(
			SyncRequest request, 
			String syncKey
		) throws ServiceException {
			return Long.toString(
				System.currentTimeMillis()
			);
		}

		@Override
		public SyncState getSyncState(
			SyncRequest request, 
			String syncKey
		) throws ServiceException {
			SyncState syncState = new SyncState();
			syncState.setValue(
				Long.valueOf(syncKey)
			);
			return syncState;
		}

		@Override
		public String moveDataItem(
			SyncRequest request, 
			String srcFolderId,
			String dstFolderId, 
			String itemId
		) throws ServiceException {
			return itemId;
		}

		@Override
		public void setDataItemReadFlag(
			SyncRequest request, 
			String folderId,
			String itemId, 
			boolean read
		) throws ServiceException {
		}

		@Override
		public State getDataItemState(
			SyncRequest request,
			SyncCollection collection, 
			SyncDataItem dataItem
		) {
			return State.NEW;
		}

		@Override
		public void sendMail(
			SyncRequest request, 
			InputStream mimeMessage
		) throws ServiceException {
		}

		@Override
		public File getContextTempDir(
			SyncRequest request, 
			File tempDir
		) throws ServiceException {
			return tempDir;
		}

	}
	
    //-----------------------------------------------------------------------
	protected static class TestSyncRequest extends SyncRequest {

		public TestSyncRequest(
		) {
			super();
		}

		@Override
		public File getTempDir(
		) throws ServiceException {
			try {
				return File.createTempFile(TestHandlers.class.getName(), ".tmp").getParentFile();
			} catch(Exception e) {
				throw new ServiceException(e);
			}
		}

		@Override
		public String getDeviceId(
		) {
			return "mypda";
		}		
		
	}
	
    //-----------------------------------------------------------------------
    @BeforeClass
    public static void initialize(
    ) throws NamingException, ServiceException {
        if(!NamingManager.hasInitialContextFactoryBuilder()) {
            NonManagedInitialContextFactoryBuilder.install(null);
        }
        entityManagerFactory = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactory();
        syncBackend = new TestBackend();
    }
    
    //-----------------------------------------------------------------------
    public static class TestAll extends AbstractTest {
    	
		public TestAll(
		) {
			super(TestHandlers.entityManagerFactory);			
		}
	
        @Test
        public void run(
        ) throws ServiceException {
        	this.testFolderSyncHandler();
            this.testGetItemEstimateHandler();
            this.testMoveItemsHandler();
            this.testPingHandler();
            this.testSyncHandler();
        }
		
        protected void testFolderSyncHandler(
        ) throws ServiceException {
        	try {
	        	FolderSyncHandler handler = new FolderSyncHandler(syncBackend);
	        	SyncRequest request = new TestSyncRequest();
	    		URL url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/FolderHierarchyRequest.xml"
	    		);
	    		InputStream is = url.openStream();
	    		Document docRequest = DOMUtils.parse(is);
	        	Document docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testFolderSyncHandler =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testFolderSyncHandler =-=-=-=");
        	} catch(Exception e) {
        		throw new ServiceException(e);
        	}
        }
        
	    protected void testGetItemEstimateHandler(
	    ) throws ServiceException {
	    	try {
		    	GetItemEstimateHandler handler = new GetItemEstimateHandler(syncBackend);
	        	SyncRequest request = new TestSyncRequest();
	    		URL url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/GetItemEstimateRequest.xml"
	    		);
	    		InputStream is = url.openStream();
	    		Document docRequest = DOMUtils.parse(is);
	    		Document docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testGetItemEstimateHandler =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testGetItemEstimateHandler =-=-=-=");	    		
        	} catch(Exception e) {
        		throw new ServiceException(e);
        	}
	    }

	    protected void testMoveItemsHandler(
	    ) throws ServiceException {
	    	try {
		    	MoveItemsHandler handler = new MoveItemsHandler(syncBackend);
	        	SyncRequest request = new TestSyncRequest();
	    		URL url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/MoveItemsRequest.xml"
	    		);
	    		InputStream is = url.openStream();
	    		Document docRequest = DOMUtils.parse(is);
	    		Document docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testMoveItemsHandler =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testMoveItemsHandler =-=-=-=");	    		
        	} catch(Exception e) {
        		throw new ServiceException(e);
        	}
	    }

	    protected void testPingHandler(
	    ) throws ServiceException {
	    	try {
		    	PingHandler handler = new PingHandler(syncBackend);
	        	SyncRequest request = new TestSyncRequest();
	        	// PingRequest1
	    		URL url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/PingRequest1.xml"
	    		);
	    		InputStream is = url.openStream();
	    		Document docRequest = DOMUtils.parse(is);
	    		Document docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testPingHandler (1) =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testPingHandler (1) =-=-=-=");	    		
	        	// PingRequest2
	    		url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/PingRequest2.xml"
	    		);
	    		is = url.openStream();
	    		docRequest = DOMUtils.parse(is);
	    		docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testPingHandler (2) =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testPingHandler (2) =-=-=-=");	    		
        	} catch(Exception e) {
        		throw new ServiceException(e);
        	}
	    }

	    protected void testSyncHandler(
	    ) throws ServiceException {
	    	try {
		    	SyncHandler handler = new SyncHandler(syncBackend);
	        	SyncRequest request = new TestSyncRequest();
	    		URL url = Classes.getApplicationResource(
	    			"test/org/opencrx/application/airsync/SyncRequest.xml"
	    		);
	    		InputStream is = url.openStream();
	    		Document docRequest = DOMUtils.parse(is);
	    		Document docResponse = handler.handle(request, docRequest);
                System.out.println();
                System.out.println("=-=-=-= testSyncHandler =-=-=-=");
	        	WbXMLTransformer.transform(
	        		docResponse, 
	        		new StreamResult(System.out), 
	        		true
	        	);
                WbXMLTransformer.transformToWBXML(
                	docResponse, 
                	new ByteArrayOutputStream()
                );
                System.out.println();
                System.out.println("=-=-=-= testSyncHandler =-=-=-=");	    		
        	} catch(Exception e) {
        		throw new ServiceException(e);
        	}
	    }
	    
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static PersistenceManagerFactory entityManagerFactory = null;
    protected static ISyncBackend syncBackend = null;
    	
}

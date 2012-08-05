/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SyncHandler.java,v 1.40 2010/08/24 17:46:12 wfro Exp $
 * Description: AirSync Client SyncHandler
 * Revision:    $Revision: 1.40 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/08/24 17:46:12 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.airsync.client;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.transform.stream.StreamResult;

import org.opencrx.application.airsync.backend.cci.ClientProfile;
import org.opencrx.application.airsync.backend.cci.GetChangedDataItemsResult;
import org.opencrx.application.airsync.backend.cci.SyncBackend;
import org.opencrx.application.airsync.backend.cci.SyncBackend.RequestContext;
import org.opencrx.application.airsync.datatypes.AttendeeT;
import org.opencrx.application.airsync.datatypes.ContactT;
import org.opencrx.application.airsync.datatypes.DataFormatFactory;
import org.opencrx.application.airsync.datatypes.DataType;
import org.opencrx.application.airsync.datatypes.EventT;
import org.opencrx.application.airsync.datatypes.FolderType;
import org.opencrx.application.airsync.datatypes.IData;
import org.opencrx.application.airsync.datatypes.IDataFormat;
import org.opencrx.application.airsync.datatypes.SyncCollection;
import org.opencrx.application.airsync.datatypes.SyncDataItem;
import org.opencrx.application.airsync.utils.DOMUtils;
import org.opencrx.application.airsync.utils.WbXMLTransformer;
import org.openmdx.base.exception.ServiceException;

public class SyncHandler extends AbstractClientHandler {

	public SyncHandler(
		SyncBackend backend
	) {
		this(
			backend,
			DEFAULT_BATCH_SIZE
		);
	}
	
	public SyncHandler(
		SyncBackend backend,
		int batchSize
	) {
		super(backend);
		this.batchSize = batchSize;		
	}
	
	@Override
    public void handle(
    	SyncTarget target,
    	String userId,
    	String profileName,
    	Object context
    ) throws ServiceException {
		try {
			SyncBackend backend = this.getBackend();
			RequestContext requestContext = backend.newRequestContext(userId, context);
			ClientProfile clientProfile = backend.getClientProfile(
				requestContext,
				profileName
			);
			for(ClientProfile.Folder folder: clientProfile.getFolders()) {
				String folderId = folder.getClientId();
				String collectionId = folder.getServerId();
				FolderType folderType = folder.getType();
				String syncKeyServer = folder.getSyncKeyServer();
				String syncKeyClient = folder.getSyncKeyClient();
		    	ClientProfile.Folder.ItemIdMappings itemIdMappings = folder.getItemIdMappings();
				if(collectionId != null && collectionId.length() > 0 ) {
					DataType collectionType = null;
					switch(folderType) {
						case DEFAULT_TASKS_FOLDER:
						case USER_CREATED_TASKS_FOLDER:
							collectionType = DataType.Tasks;
							break;
						case DEFAULT_CALENDAR_FOLDER:
						case USER_CREATED_CALENDAR_FOLDER:
							collectionType = DataType.Calendar;
							break;
						case DEFAULT_CONTACTS_FOLDER:
						case USER_CREATED_CONTACTS_FOLDER:
							collectionType = DataType.Contacts;
							break;
					}
					if(collectionType != null) {
						org.w3c.dom.Document docRequest = DOMUtils.createDoc(
							"AirSync:", 
							"Sync",
							new String[]{"xmlns:Email", "POOMMAIL:"},
							new String[]{"xmlns:Email2", "POOMMAIL2:"},
							new String[]{"xmlns:Contacts", "POOMCONTACTS:"},
							new String[]{"xmlns:Contacts2", "POOMCONTACTS2:"},
							new String[]{"xmlns:Tasks", "POOMTASKS:"},
							new String[]{"xmlns:Calendar", "POOMCAL:"},
							new String[]{"xmlns:AirSyncBase", "AirSyncBase:"}
						);
						org.w3c.dom.Element eRoot = docRequest.getDocumentElement();
						org.w3c.dom.Element eCollections = DOMUtils.createElement(eRoot, null, "Collections");
						org.w3c.dom.Element eCollection = DOMUtils.createElement(eCollections, null, "Collection");
						DOMUtils.createElementAndText(eCollection, null, "Class", collectionType.toString());
						DOMUtils.createElementAndText(eCollection, null, "SyncKey", (syncKeyServer == null ? "0" : syncKeyServer));		
						DOMUtils.createElementAndText(eCollection, null, "CollectionId", collectionId);
						if(syncKeyServer != null) {
							DOMUtils.createElement(eCollection, null, "GetChanges");
							DOMUtils.createElementAndText(eCollection, null, "WindowSize", Integer.toString(this.batchSize));
							// Get changed and deleted items from backend
					    	org.opencrx.application.airsync.datatypes.SyncCollection clientCollection = 
					    		new org.opencrx.application.airsync.datatypes.SyncCollection();
					    	clientCollection.setCollectionId(folderId);
					    	// Increment folder generation in case of initial sync. This 
					    	// migrates the clientId -> serverId mappings to the new generation
					    	if(syncKeyClient == null || "0".equals(syncKeyClient)) {
					    		folder.setSyncKeyClient(
					    			syncKeyClient = "0"
					    		);
					    		folder.setGeneration(folder.getGeneration() + 1);
					    	}
					    	clientCollection.setSyncKey(syncKeyClient);					    	
					    	clientCollection.setDataType(collectionType);
					    	List<SyncDataItem> changedDataItems = new ArrayList<SyncDataItem>();
					    	Set<String> excludes = new HashSet<String>();
					    	GetChangedDataItemsResult getNewDataItemsResult = backend.getChangedDataItems(
					    		requestContext,
					    		profileName,
					    		clientCollection,
					    		false, // noData
					    		this.batchSize, // maxItems
					    		SyncDataItem.State.NEW,
					    		excludes
					    	);
					    	changedDataItems.addAll(getNewDataItemsResult.getDataItems());
					    	syncKeyClient = getNewDataItemsResult.getSyncKey();
					    	for(SyncDataItem dataItem: getNewDataItemsResult.getDataItems()) {
					    		excludes.add(dataItem.getServerId());
					    	}
					    	if(!getNewDataItemsResult.hasMore()) {
								// Set temporarily to syncKeyClient for querying modified items
								String tmpSyncKey = clientCollection.getSyncKey();					    		
					    		clientCollection.setSyncKey(syncKeyClient);
						    	GetChangedDataItemsResult getChangedDataItemsResult = backend.getChangedDataItems(
						    		requestContext,
						    		profileName,
						    		clientCollection,
						    		false, // noData
						    		this.batchSize, // maxItems
						    		SyncDataItem.State.MODIFIED,
						    		excludes
						    	);
						    	clientCollection.setSyncKey(tmpSyncKey);
						    	changedDataItems.addAll(getChangedDataItemsResult.getDataItems());
						    	syncKeyClient = getChangedDataItemsResult.getSyncKey();						    	
					    	}
					    	List<String> deletedItemIds = backend.getDeletedDataItems(
					    		requestContext,
					    		profileName,
					    		clientCollection,
					    		syncKeyClient
					    	);
					    	// If nothing to change then delete items of old generations
					    	if(changedDataItems.isEmpty()) {
					    		for(ClientProfile.Folder.ItemIdMapping mapping: itemIdMappings.getOldMappings()) {	
						    		deletedItemIds.add(
						    			mapping.getClientId()
						    		);
					    		}
					    	}
				    		// Generate commands for changed and deleted items
					    	if(
					    		!changedDataItems.isEmpty() || 
					    		!deletedItemIds.isEmpty()
					    	) {
					    		IDataFormat dataFormat = DataFormatFactory.getXmlFormat(collectionType);
								org.w3c.dom.Element eCommands = DOMUtils.createElement(eCollection, null, "Commands");
					    		for(SyncDataItem changedItem: changedDataItems) {					    				
					    			String clientId = changedItem.getServerId();
					    			String serverId = itemIdMappings.getServerId(clientId);					    			
					    			org.w3c.dom.Element eCommand = null;
					    			// Add item, if we do not have a ServerId
					    			if(serverId == null) {
					    				eCommand = DOMUtils.createElement(eCommands, null, "Add");
										DOMUtils.createElementAndText(eCommand, null, "ClientId", clientId);
					    			}
					    			// Change it
					    			else {
					    				// Update mapping. This moves mapping to current generation
					    				itemIdMappings.updateMappings(clientId, serverId);
					    				eCommand = DOMUtils.createElement(eCommands, null, "Change");
										DOMUtils.createElementAndText(eCommand, null, "ServerId", serverId);
										// Exchange does not allow to change organizerName and organizerEMail
										if(changedItem.getData() instanceof EventT) {
											EventT eventT = (EventT)changedItem.getData();
											eventT.setOrganizerName(null);
											eventT.setOrganizerEmail(null);
										}
					    			}
									org.w3c.dom.Element eApplicationData = DOMUtils.createElement(eCommand, null, "ApplicationData");
									if(changedItem.getData() instanceof EventT) {
										EventT eventT = (EventT)changedItem.getData();
										// Exchange requires a timezone although the spec says it is optional
										eventT.setTimezone("4AEAAFAAYQBjAGkAZgBpAGMAIABTAHQAYQBuAGQAYQByAGQAIABUAGkAbQBlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAsAAAABAAIAAAAAAAAAAAAAAFAAYQBjAGkAZgBpAGMAIABEAGEAeQBsAGkAZwBoAHQAIABUAGkAbQBlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAACAAIAAAAAAAAAxP///w==");
										// Exchange: Status and type of attendees are not modifiable
										for(AttendeeT attendeeT: eventT.getAttendees()) {
											attendeeT.setAttendeeStatus(null);											
											attendeeT.setAttendeeType(null);											
										}
									}
									else if(changedItem.getData() instanceof ContactT) {
										ContactT contactT = (ContactT)changedItem.getData();
										// Alias must not be sent to server. According to spec:
										// The <Alias> element is only returned in a recipient information cache request
										contactT.setAlias(null);
									}
					    			dataFormat.format(
					    				eApplicationData,
					    				changedItem.getData(),
					    				2.5 // protocolVersion
					    			);
					    		}
					    		for(String itemId: deletedItemIds) {
					    			String serverId = itemIdMappings.getServerId(itemId);
					    			if(serverId != null) {
					    				itemIdMappings.removeAllMappingsByClientId(itemId);
						    			org.w3c.dom.Element eCommand = DOMUtils.createElement(eCommands, null, "Delete");
					    				DOMUtils.createElementAndText(eCommand, null, "ServerId", serverId);
					    			}
					    		}
					    	}
				    	}
			    		// Invoke
				    	org.w3c.dom.Document docResponse = target.perform(
				    		"Sync", 
				    		clientProfile.getPolicyKey() == null ? "0" : clientProfile.getPolicyKey(),
				    		clientProfile.getUserAgent(),
				    		docRequest
				    	);
			    		// Response
				    	eRoot = docResponse.getDocumentElement();
						org.w3c.dom.NodeList lCollection = eRoot.getElementsByTagNameNS("AirSync:", "Collection");
						for(int i = 0; i < lCollection.getLength(); i++) {
							try {
								eCollection = (org.w3c.dom.Element)lCollection.item(i);
								SyncCollection responseCollection = SyncCollection.decode("AirSync:", eCollection);
								if(collectionId.equals(responseCollection.getCollectionId())) {
									// Commands
									org.w3c.dom.Element eCommands = DOMUtils.getUniqueElement(eCollection, "AirSync:", "Commands");
									if(eCommands != null) {										
										org.w3c.dom.NodeList lCommand = eCommands.getChildNodes();
										for(int j = 0; j < lCommand.getLength(); j++) {							
											org.w3c.dom.Node node = lCommand.item(j);
											if(node instanceof org.w3c.dom.Element) {
												org.w3c.dom.Element eCommand = (org.w3c.dom.Element)node;								
												String command = eCommand.getNodeName();
												String serverId = DOMUtils.getElementText(eCommand, "AirSync:", "ServerId");
												String clientId = DOMUtils.getElementText(eCommand, "AirSync:", "ClientId");
												// Should not happen. However, if clientId is not returned by 
												// server try to map serverId to clientId using id mapping
												if(clientId == null && serverId != null) {
													ClientProfile.Folder.ItemIdMapping mapping = itemIdMappings.getMappingByClientId(serverId);
													if(mapping != null) {
														clientId = mapping.getClientId();
													}
												}
												org.w3c.dom.Element eApplicationData = DOMUtils.getUniqueElement(eCommand, "AirSync:", "ApplicationData");
												IDataFormat dataDecoder = DataFormatFactory.getXmlFormat(responseCollection.getDataType());
												IData data = null;
												if(eApplicationData != null) {
													data = dataDecoder.parse(eApplicationData);
												}
												if(folderId != null) {
													SyncCollection collection = new SyncCollection();
													collection.setCollectionId(folderId);
													collection.setDataType(collectionType);
													if(command.endsWith("Modify")) {
														clientId = backend.createOrUpdateDataItem(
															requestContext,
															profileName,
															collection,
															clientId,
															data														
														);
													}
													else if(command.endsWith("Add")) {
														clientId = backend.createOrUpdateDataItem(
															requestContext,
															profileName,
															collection,
															clientId,
															data														
														);
													}
													else if(command.endsWith("Change")) {
														clientId = backend.createOrUpdateDataItem(
															requestContext,
															profileName,
															collection,
															clientId,
															data														
														);
													}
													else if(command.endsWith("Delete")) {
														if(clientId != null) {
															backend.deleteDataItem(
																requestContext,
																profileName,
																collection,
																clientId
															);
															itemIdMappings.removeAllMappingsByClientId(clientId);
															clientId = null;
														}
													}
													if(clientId != null && serverId != null && clientId.length() > 0 && serverId.length() > 0) {
														// Move mapping to current generation
														itemIdMappings.updateMappings(clientId, serverId);
													}
												}
											}
										}
									}
									// Responses
									org.w3c.dom.Element eResponses = DOMUtils.getUniqueElement(eCollection, "AirSync:", "Responses");
									if(eResponses != null) {										
										org.w3c.dom.NodeList lResponse = eResponses.getChildNodes();
										for(int j = 0; j < lResponse.getLength(); j++) {							
											org.w3c.dom.Node node = lResponse.item(j);
											if(node instanceof org.w3c.dom.Element) {
												org.w3c.dom.Element eCommand = (org.w3c.dom.Element)node;								
												String command = eCommand.getNodeName();
												String serverId = DOMUtils.getElementText(eCommand, "AirSync:", "ServerId");
												String clientId = DOMUtils.getElementText(eCommand, "AirSync:", "ClientId");
												if(folderId != null) {
													if(command.endsWith("Add")) {
														if(clientId != null && serverId != null && clientId.length() > 0 && serverId.length() > 0) {
															// Move mapping to current generation
															itemIdMappings.updateMappings(clientId, serverId);
														}
													}
													else if(command.endsWith("Delete")) {
														if(serverId.length() > 0) {
															itemIdMappings.removeAllMappingsByServerId(serverId);
														}
													}
												}
											}
										}
									}
									// Has somebody else touched items in this collection?
									// Only test if syncKeyServer != null --> syncKeyClient was updated
									if(syncKeyServer != null) {
								    	org.opencrx.application.airsync.datatypes.SyncCollection clientCollection = 
								    		new org.opencrx.application.airsync.datatypes.SyncCollection();
								    	clientCollection.setCollectionId(folderId);
								    	clientCollection.setSyncKey(syncKeyClient);					    	
								    	clientCollection.setDataType(collectionType);
								    	GetChangedDataItemsResult getChangedDataItemsResult = backend.getChangedDataItems(
								    		requestContext,
								    		profileName,
								    		clientCollection,
								    		true, // noData
								    		1, // maxItems
								    		SyncDataItem.State.MODIFIED,
								    		Collections.<String>emptySet()						    		
								    	);
								    	// There were no changes during [syncKeyClient, requestContext.getSyncKey()] if 
								    	// the syncKey of the first changed item matches the sync key of the request context
										if(getChangedDataItemsResult.getSyncKey().equals(requestContext.getSyncKey())) {
											syncKeyClient = requestContext.getSyncKey();
										}
									}
									// Update folder's sync keys									
									folder.setSyncKeyServer(responseCollection.getSyncKey());									
									folder.setSyncKeyClient(
										"0".equals(syncKeyClient) ? 
											backend.getNextSyncKey(requestContext, syncKeyClient) :
												syncKeyClient
									);
								}
							} catch(Exception e) {
								new ServiceException(e).log();
							}
						}
						if(logger.isLoggable(Level.FINE)) {
							ByteArrayOutputStream out = new ByteArrayOutputStream();
					    	WbXMLTransformer.transform(
					    		docResponse,
					    		new StreamResult(out),
					    		true
					    	);
					    	out.close();
					    	logger.log(Level.FINE, "+-+-+-+-+- Response +-+-+-+-+-");
					    	logger.log(Level.FINE, out.toString());
						}
					}
				}
			}
			// Save updated sync keys and mappings
			backend.updateClientProfile(
				requestContext,
				clientProfile,
				null, // all folders
				false, // noSyncKeys
				false // noMappings
			);
		} catch(Exception e) {
			throw new ServiceException(e);
		}	    
    }

	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
	private static final int DEFAULT_BATCH_SIZE = 50;
	
	private final int batchSize;
	
}

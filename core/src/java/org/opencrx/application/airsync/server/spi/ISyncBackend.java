/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: ISyncBackend.java,v 1.20 2010/04/01 09:10:21 wfro Exp $
 * Description: Sync for openCRX
 * Revision:    $Revision: 1.20 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/01 09:10:21 $
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
package org.opencrx.application.airsync.server.spi;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.opencrx.application.airsync.datatypes.AttachmentDataT;
import org.opencrx.application.airsync.datatypes.IData;
import org.opencrx.application.airsync.server.SyncCollection;
import org.opencrx.application.airsync.server.SyncDataItem;
import org.opencrx.application.airsync.server.SyncFolder;
import org.opencrx.application.airsync.server.SyncRequest;
import org.opencrx.application.airsync.server.SyncState;
import org.openmdx.base.exception.ServiceException;

public interface ISyncBackend {

	public String getNewSyncKey(
		SyncRequest request,
		String syncKey
	) throws ServiceException;
	
	public SyncState getSyncState(
		SyncRequest request,
		String syncKey
	) throws ServiceException;
	
	public SyncDataItem fetchDataItem(
		SyncRequest request,
		String folderId,
		String itemId
	) throws ServiceException;
	
	public String moveDataItem(
		SyncRequest request,
		String srcFolderId,
		String dstFolderId,
		String itemId
	) throws ServiceException;

	public SyncDataItem.State getDataItemState(
		SyncRequest request,
		SyncCollection collection,
		SyncDataItem dataItem
	) throws ServiceException;
	
	public AttachmentDataT getAttachementData(
		SyncRequest request,
		String attachmentId
	) throws ServiceException;

	public String createOrUpdateDataItem(
		SyncRequest request, 
		String folderId,
		String itemId, 
		IData data
	) throws ServiceException;
	
    public void deleteDataItem(
    	SyncRequest request, 
    	String folderId, 
    	String itemId
    ) throws ServiceException;

	public void setDataItemReadFlag(
		SyncRequest request,
		String folderId,
		String itemId, 
		boolean read
	) throws ServiceException;

	public List<SyncDataItem> getChangedDataItems(
		SyncRequest request,
		SyncCollection collection,
		boolean noData,
		int maxItems
	) throws ServiceException;
	
	public List<String> getDeletedDataItems(
		SyncRequest request,
		SyncCollection collection
	) throws ServiceException;
	
	public List<SyncFolder> getChangedFolders(
		SyncRequest request,
		SyncState syncState
	) throws ServiceException;
	
	public List<SyncFolder> getDeletedFolders(
		SyncRequest request,
		SyncState syncState
	) throws ServiceException;
	
	public String createOrUpdateFolder(
		SyncRequest request,
		SyncFolder folder
	) throws ServiceException;
	
	public String deleteFolder(
		SyncRequest request,
		String folderId
	) throws ServiceException;	

	public void sendMail(
		SyncRequest request,
		InputStream mimeMessage
	) throws ServiceException;
	
	public File getContextTempDir(
		SyncRequest request,
		File tempDir
	) throws ServiceException;
	
}

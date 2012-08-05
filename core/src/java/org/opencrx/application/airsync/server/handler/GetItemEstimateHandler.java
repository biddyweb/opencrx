/*
 * ====================================================================
 * Project:     openCRX/Application, http://www.opencrx.org/
 * Name:        $Id: GetItemEstimateHandler.java,v 1.25 2010/03/18 17:02:59 wfro Exp $
 * Description: AirSync for openCRX
 * Revision:    $Revision: 1.25 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/03/18 17:02:59 $
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
package org.opencrx.application.airsync.server.handler;

import java.util.ArrayList;
import java.util.List;

import org.opencrx.application.airsync.server.SyncCollection;
import org.opencrx.application.airsync.server.SyncRequest;
import org.opencrx.application.airsync.server.SyncStatus;
import org.opencrx.application.airsync.server.spi.ISyncBackend;
import org.opencrx.application.airsync.utils.DOMUtils;
import org.openmdx.base.exception.ServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GetItemEstimateHandler extends AbstractHandler {

	public GetItemEstimateHandler(ISyncBackend backend) {
		super(backend);
	}

	@Override
	public Document handle(
		SyncRequest request, 
		Document docRequest
	) {
		List<SyncCollection> collections = new ArrayList<SyncCollection>();
		NodeList lCollection = docRequest.getDocumentElement().getElementsByTagName("Collection");
		for(int i = 0; i < lCollection.getLength(); i++) {
			Element eCollection = (Element) lCollection.item(i);
			SyncCollection collection = SyncCollection.decode(eCollection);
			collections.add(collection);
		}
		Document docResponse = DOMUtils.createDoc("ItemEstimate:", "GetItemEstimate");
		Element eRoot = docResponse.getDocumentElement();
		for(SyncCollection collection: collections) {
			Element eResponse = DOMUtils.createElement(eRoot, null, "Response");
			SyncStatus syncStatus = SyncStatus.OK;
			int count = 0;
			try {
				count = this.backend.getChangedDataItems(
					request, 
					collection,
					true, // noData
					Integer.MAX_VALUE
				).size();
			} catch(Exception e) {
				new ServiceException(e).log();
				syncStatus = SyncStatus.SERVER_ERROR; 
			}
			DOMUtils.createElementAndText(eResponse, null, "Status", Integer.toString(syncStatus.getValue()));
			if(syncStatus == SyncStatus.OK) {
				Element eCollection = DOMUtils.createElement(eResponse, null, "Collection");
				if(collection.getDataType() != null) {
					DOMUtils.createElementAndText(eCollection, null, "Class", collection.getDataType().toString());
				}
				DOMUtils.createElementAndText(eCollection, null, "CollectionId", collection.getCollectionId());
				Element eEstimate = DOMUtils.createElement(eCollection, null, "Estimate");
				eEstimate.setTextContent(Integer.toString(count));
			}
		}
		return docResponse;
	}
	
}

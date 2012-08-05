/*
 * ====================================================================
 * Project:     openCRX/core, http://www.opencrx.org/
 * Name:        $Id: DoDelete.java,v 1.11 2010/12/08 18:57:07 wfro Exp $
 * Description: DoDelete
 * Revision:    $Revision: 1.11 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/08 18:57:07 $
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

/*
 * This source was originally published under net.sf.webdav.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.opencrx.application.uses.net.sf.webdav.methods;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.WebDavStore;
import org.opencrx.application.uses.net.sf.webdav.WebdavStatus;
import org.opencrx.application.uses.net.sf.webdav.exceptions.AccessDeniedException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.LockFailedException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.ObjectNotFoundException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.WebdavException;
import org.openmdx.base.exception.ServiceException;


public class DoDelete extends WebDavMethod {

    private static Logger LOG = Logger.getLogger(DoDelete.class.getPackage().getName());

    private final WebDavStore _store;
    private final boolean _readOnly;

    public DoDelete(
    	WebDavStore store,
        boolean readOnly
    ) {
        _store = store;
        _readOnly = readOnly;
    }

    @Override
    public void execute(
    	RequestContext requestContext 
    ) throws IOException, LockFailedException {
        LOG.finest("-- " + this.getClass().getName());
        HttpServletRequest req = requestContext.getHttpServletRequest();
        HttpServletResponse resp = requestContext.getHttpServletResponse();
        if (!_readOnly) {
            String path = getRelativePath(requestContext);
            String parentPath = getParentPath(getCleanPath(path));
            Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();
            if (!checkLocks(requestContext, _store, parentPath)) {
                errorList.put(parentPath, WebdavStatus.SC_LOCKED);
                sendReport(requestContext, errorList);
                return; // parent is locked
            }
            if (!checkLocks(requestContext, _store, path)) {
                errorList.put(path, WebdavStatus.SC_LOCKED);
                sendReport(requestContext, errorList);
                return; // resource is locked
            }
            try {
                errorList = new Hashtable<String, Integer>();
                Resource so = _store.getResourceByPath(requestContext, path);
                this.deleteResource(requestContext, so, path, errorList);
                if (!errorList.isEmpty()) {
                    sendReport(requestContext, errorList);
                }
            } catch (AccessDeniedException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (ObjectAlreadyExistsException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
            } catch (WebdavException e) {
            	new ServiceException(e).log();            	
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
            }
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * deletes the resources at "path"
     * 
     * @param requestContext
     *      indicates that the method is within the scope of a WebDAV
     *      transaction
     * @param path
     *      the folder to be deleted
     * @param errorList
     *      all errors that occurred
     * @param req
     *      HttpServletRequest
     * @param resp
     *      HttpServletResponse
     * @throws WebdavException
     *      if an error in the underlying store occurs
     * @throws IOException
     *      when an error occurs while sending the response
     */
    public void deleteResource(
    	RequestContext requestContext, 
    	Resource so,
    	String path,
        Hashtable<String, Integer> errorList 
    ) throws IOException, WebdavException {
    	HttpServletResponse resp = requestContext.getHttpServletResponse();
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        if (!_readOnly) {
            if (so.isCollection()) {
                deleteCollectionContent(requestContext, path, so, errorList);
                _store.removeResource(requestContext, so);
            } else {
                _store.removeResource(requestContext, so);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * 
     * helper method of deleteResource() deletes the folder and all of its
     * contents
     * 
     * @param requestContext
     *      indicates that the method is within the scope of a WebDAV
     *      transaction
     * @param path
     *      the folder to be deleted
     * @param errorList
     *      all errors that ocurred
     * @param req
     *      HttpServletRequest
     * @param resp
     *      HttpServletResponse
     * @throws WebdavException
     *      if an error in the underlying store occurs
     */
    private void deleteCollectionContent(
    	RequestContext requestContext, 
    	String path,
    	Resource parent,
        Hashtable<String, Integer> errorList 
    ) throws WebdavException {
        Collection<Resource> children = _store.getChildren(requestContext, parent);
        for(Resource child: children) {
            try {
                if(child.isCollection()) {
                    deleteCollectionContent(requestContext, path + "/" + child.getName(), child, errorList);
                    _store.removeResource(requestContext, child);
                } else {
                    _store.removeResource(requestContext,child);
                }
            } catch (AccessDeniedException e) {
                errorList.put(path + "/" + child.getName(), new Integer(HttpServletResponse.SC_FORBIDDEN));
            } catch (ObjectNotFoundException e) {
                errorList.put(path + "/" + child.getName(), new Integer(HttpServletResponse.SC_NOT_FOUND));
            } catch (WebdavException e) {
                errorList.put(path + "/" + child.getName(), new Integer(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            }
        }

    }

}

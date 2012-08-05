/*
 * ====================================================================
 * Project:     openCRX/core, http://www.opencrx.org/
 * Name:        $Id: DoGet.java,v 1.16 2010/12/15 16:52:11 wfro Exp $
 * Description: DoGet
 * Revision:    $Revision: 1.16 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/12/15 16:52:11 $
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.WebDavStore;
import org.w3c.cci2.BinaryLargeObjects;


public class DoGet extends DoHead {

    private static Logger LOG = Logger.getLogger(DoGet.class.getPackage().getName());

    public DoGet(
    	WebDavStore store 
    ) {
        super(
        	store 
        );
    }

    @Override
    protected void doBody(
    	RequestContext requestContext, 
        Resource res
    ) {
    	HttpServletResponse resp = requestContext.getHttpServletResponse();
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            OutputStream out = resp.getOutputStream();
            InputStream in = _store.getResourceContent(requestContext, res).getContent();
            String mimeType = _store.getMimeType(res);
            if(mimeType != null) {
	            resp.setContentType(mimeType);
	            if(mimeType.startsWith("text/")) {
	                resp.setCharacterEncoding("UTF-8");            	
	            }
            }
            long length = BinaryLargeObjects.streamCopy(
            	in, 
            	0L, 
            	out
            );
            out.flush();   
            resp.setContentLength((int)length);
        } catch (Exception e) {
            LOG.finest(e.toString());
        }
    }

    @Override
    protected void folderBody(
    	RequestContext requestContext, 
    	Resource so
    ) throws IOException {
    	HttpServletRequest req = requestContext.getHttpServletRequest();
    	HttpServletResponse resp = requestContext.getHttpServletResponse();    	
        if (so == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            if (so.isCollection()) {
                // TODO some folder response (for browsers, DAV tools
                // use propfind) in html?
                DateFormat shortDF = getDateTimeFormat(req.getLocale());
                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF-8");
                OutputStream out = resp.getOutputStream();
                Collection<Resource> children = _store.getChildren(requestContext, so);
                StringBuilder childrenTemp = new StringBuilder();
                childrenTemp.append("<html><head><title>Content of folder");
                childrenTemp.append(so.getName());
                childrenTemp.append("</title><style type=\"text/css\">");
                childrenTemp.append(getCSS());
                childrenTemp.append("</style></head>");
                childrenTemp.append("<body>");
                childrenTemp.append(getHeader(requestContext, so.getName()));
                childrenTemp.append("<table>");
                childrenTemp.append("<tr><th>Name</th><th>Size</th><th>Created</th><th>Modified</th></tr>");
                childrenTemp.append("<tr>");
                childrenTemp.append("<td colspan=\"4\"><a href=\"../\">Parent</a></td></tr>");
                boolean isEven= false;
                for (Resource child: children) {
                    isEven= !isEven;
                    childrenTemp.append("<tr class=\"");
                    childrenTemp.append(isEven ? "even" : "odd");
                    childrenTemp.append("\">");
                    childrenTemp.append("<td>");
                    childrenTemp.append("<a href=\"");
                    childrenTemp.append(child.getName());
                    if (child.isCollection()) {
                        childrenTemp.append("/");
                    }
                    childrenTemp.append("\">");
                    childrenTemp.append(child.getName());
                    childrenTemp.append("</a></td>");
                    if (child.isCollection()) {
                        childrenTemp.append("<td>Folder</td>");
                    }
                    else {
                        childrenTemp.append("<td>");
                        childrenTemp.append(_store.getResourceContent(requestContext, child).getLength());
                        childrenTemp.append(" Bytes</td>");
                    }
                    if (child.getCreationDate() != null) {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(child.getCreationDate()));
                        childrenTemp.append("</td>");
                    }
                    else {
                        childrenTemp.append("<td></td>");
                    }
                    if(child.getLastModified() != null) {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(child.getLastModified()));
                        childrenTemp.append("</td>");
                    }
                    else {
                        childrenTemp.append("<td></td>");
                    }
                    childrenTemp.append("</tr>");
                }
                childrenTemp.append("</table>");
                childrenTemp.append(getFooter(requestContext, so.getName()));
                childrenTemp.append("</body></html>");
                out.write(childrenTemp.toString().getBytes("UTF-8"));
            }
        }
    }

    /**
     * Return the CSS styles used to display the HTML representation
     * of the webdav content.
     * 
     * @return String returning the CSS style sheet used to display result in html format
     */
    protected String getCSS()
    {
        // The default styles to use
       String retVal= "body {\n"+
                "	font-family: Arial, Helvetica, sans-serif;\n"+
                "}\n"+
                "h1 {\n"+
                "	font-size: 1.5em;\n"+
                "}\n"+
                "th {\n"+
                "	background-color: #9DACBF;\n"+
                "}\n"+
                "table {\n"+
                "	border-top-style: solid;\n"+
                "	border-right-style: solid;\n"+
                "	border-bottom-style: solid;\n"+
                "	border-left-style: solid;\n"+
                "}\n"+
                "td {\n"+
                "	margin: 0px;\n"+
                "	padding-top: 2px;\n"+
                "	padding-right: 5px;\n"+
                "	padding-bottom: 2px;\n"+
                "	padding-left: 5px;\n"+
                "}\n"+
                "tr.even {\n"+
                "	background-color: #CCCCCC;\n"+
                "}\n"+
                "tr.odd {\n"+
                "	background-color: #FFFFFF;\n"+
                "}\n"+
                "";
        try
        {
            // Try loading one via class loader and use that one instead
            ClassLoader cl = getClass().getClassLoader();
            InputStream iStream = cl.getResourceAsStream("webdav.css");
            if(iStream != null)
            {
                // Found css via class loader, use that one
                StringBuilder out = new StringBuilder();
                byte[] b = new byte[4096];
                for (int n; (n = iStream.read(b)) != -1;)
                {
                    out.append(new String(b, 0, n));
                }
                retVal= out.toString();
            }
        }
        catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "Error in reading webdav.css", ex);
        }

        return retVal;
    }

    /**
     * Return this as the Date/Time format for displaying Creation + Modification dates
     * 
     * @param browserLocale
     * @return DateFormat used to display creation and modification dates
     */
    protected DateFormat getDateTimeFormat(Locale browserLocale)
    {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM, browserLocale);
    }

    /**
     * Return the header to be displayed in front of the folder content
     * 
     * @param requestContext
     * @param path
     * @return String representing the header to be display in front of the folder content
     */
    protected String getHeader(
    	RequestContext requestContext, 
    	String name
    ) {
        return "<h1>Content of folder " + name + "</h1>";
    }

    /**
     * Return the footer to be displayed after the folder content
     * 
     * @param requestContext
     * @param name
     * @return String representing the footer to be displayed after the folder content
     */
    protected String getFooter(
    	RequestContext requestContext, 
    	String name
    ) {
        return "";
    }
}

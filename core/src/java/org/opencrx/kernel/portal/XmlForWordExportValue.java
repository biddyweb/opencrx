/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: XmlForWordExportValue.java,v 1.1 2007/11/17 15:51:44 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/11/17 15:51:44 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.text.StringBuilders;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.HtmlPage;
import org.openmdx.portal.servlet.attribute.Attribute;
import org.openmdx.portal.servlet.attribute.BinaryValue;
import org.openmdx.portal.servlet.attribute.FieldDef;

public class XmlForWordExportValue 
    extends BinaryValue
    implements Serializable {

    //-------------------------------------------------------------------------
    public XmlForWordExportValue(
        Object object, 
        FieldDef fieldDef,
        ApplicationContext application
    ) {
        super(
            object, 
            fieldDef,
            application
        );
    }

    //-------------------------------------------------------------------------
    public void paint(
        Attribute attribute,
        HtmlPage p,
        String id,
        String label,
        RefObject_1_0 lookupObject,
        int nCols,
        int tabIndex,
        String gapModifier,
        String styleModifier,
        String widthModifier,
        String rowSpanModifier,
        String readonlyModifier,
        String disabledModifier,
        String lockedModifier,
        String stringifiedValue,
        boolean forEditing
    ) throws ServiceException { 
        int currentDocId = p.getProperty(XmlForWordExportValue.class.getName()) != null
            ? ((Integer)p.getProperty(XmlForWordExportValue.class.getName())).intValue()
            : 0;
        Map params = this.getMimeTypeParams();

        p.write("<script language=\"javascript\" type=\"text/javascript\">");
        p.write("<!--//");
        p.write("function openWordDoc" + currentDocId + "(boolVisible, strContextUrl, strDocPath, strMacroName, objectId) {");
        p.write("  var WordApp = new ActiveXObject(\"Word.Application\");");
        p.write("  if (WordApp != null) {");
        p.write("    WordApp.Visible = boolVisible;");
        p.write("    var WordDoc=WordApp.Documents.Add(strDocPath);");
        p.write("    if (WordDoc == null) {");
        p.write("      WordDoc = WordApp.Documents.Add(strContextUrl+strDocPath);");
        p.write("    }");
        p.write("    if (WordDoc != null) {");
        p.write("      var el = getElement(objectId);");
        p.write("      if (el) {");
        p.write("        if (el.innerText) {");
        p.write("          WordApp.Run(strMacroName, el.innerText);");
        p.write("        }");
        p.write("      }");
        p.write("    }");
        p.write("  }");
        p.write("}");
        p.write("");
        p.write("//      -->");
        p.write("</script>");

        // XmlForWordExportValue assumens that the binary value is a zip stream
        // which contains the exported XML files. Append all files contained
        // in the ZIP to one string
        CharSequence exportedXml = StringBuilders.newStringBuilder();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                this.getBinaryValue(os);
            }
            catch(ServiceException e) {
                AppLog.warning(e.getMessage(), e.getCause(), 1);
            }
            ZipInputStream zip = new ZipInputStream(
                new ByteArrayInputStream(os.toByteArray())
            );
            while((zip.getNextEntry()) != null) {
                ByteArrayOutputStream xmlFile = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = zip.read(buffer)) != -1) {
                    xmlFile.write(buffer, 0, len);
                }
                BufferedReader xmlReader = new BufferedReader(
                    new StringReader(xmlFile.toString("UTF-8"))
                );
                String line = null;
                while((line = xmlReader.readLine()) != null) {
                    StringBuilders.asStringBuilder(exportedXml).append(line + "\n");  
                }
            }
        }
        catch(IOException e) {
            AppLog.warning("can not read binary value. Is it a ZIP stream?");
        }

        HttpServletRequest request = p.getHttpServletRequest();
        String hostAndContextPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        p.write("<td class=\"label\"><span class=\"nw\">" + attribute.getLabel() + "</span></td>");
        p.write("<td class=\"valueL\" " + widthModifier + ">");
        p.write("<div class=\"field\"><input type=button onClick=\"openWordDoc" + currentDocId + "(" + params.get("visible") + ", '" + hostAndContextPath + "', '" + params.get("template") + "', '" + params.get("macro") + "', 'xmlExport" + currentDocId + "');\"value=\"" + attribute.getLabel() + "\"><img class=\"popUpButton\" src=\"images/help.gif\" width=\"16\" height=\"16\" border=\"0\" onclick=\"javascript:void(window.open('helpActiveX_" + this.application.getCurrentLocaleAsString() + ".html', 'Help', 'fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=400'));\" /></div>");
        p.write("<div id=\"showXML\" class=\"field\" style=\"display: block\"><a onclick=\"javascript:{document.getElementById('hideXML').style.display='block';document.getElementById('showXML').style.display='none';}\"><img src=\"images/expand_down.gif\" border=\"0\" align=\"middle\" alt=\"v\" title=\"\" /></a></div>");
        p.write("<div id=\"hideXML\" class=\"field\" style=\"display: none\"><a onclick=\"javascript:{document.getElementById('hideXML').style.display='none';document.getElementById('showXML').style.display='block';}\"><img src=\"images/shrink_up.gif\" border=\"0\" align=\"middle\" alt=\"^\" title=\"\" /></a><br /> <textarea id=\"xmlExport" + currentDocId + "\" rows=\"10\" cols=\"20\" style=\"overflow:auto;\" >" + exportedXml.toString() + "</textarea> </div>");
        p.write("</td>");
                
        p.setProperty(
            XmlForWordExportValue.class.getName(),
            new Integer(currentDocId+1)
        );
    }

    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258129167668425784L;

}

//  --- End of File -----------------------------------------------------------

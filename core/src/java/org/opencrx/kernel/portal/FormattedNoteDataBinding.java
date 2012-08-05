/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: FormattedNoteDataBinding.java,v 1.5 2008/11/13 09:55:15 wfro Exp $
 * Description: NoteDataBinding
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/13 09:55:15 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
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

import javax.jmi.reflect.RefObject;

import org.opencrx.kernel.base.jmi1.Note;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.DefaultDataBinding;

public class FormattedNoteDataBinding extends DefaultDataBinding {

    //-----------------------------------------------------------------------
    public FormattedNoteDataBinding(
        ApplicationContext applicationContext
    ) {
        this.applicationContext = applicationContext;
    }
    
    //-----------------------------------------------------------------------
    @Override
    public Object getValue(
        RefObject object, 
        String qualifiedFeatureName
    ) {
        if(object instanceof Note) {
            Note note = (Note)object;
            String title = note.getTitle();
            String text = note.getText();
            if((title == null) || (title.length() == 0)) {
                return text;
            }
            else {
                int titleLength = title.length();
                int indexBol = 0;
                int indexEol = 0;
                if(text != null) {
                    while((indexEol = text.indexOf('\n', indexBol)) > 0) {
                        titleLength = Math.max(titleLength, indexEol - indexBol);
                        indexBol = indexEol + 1;                    
                    }
                }
                titleLength = Math.min(140, titleLength);
                StringBuilder formattedNote = new StringBuilder("<b>");
                formattedNote.append(title);
                for(int i = title.length(); i < titleLength; i++) {
                    formattedNote.append("&nbsp;");
                }
                formattedNote
                    .append("</b><br />")
                    .append(text == null ? "" : text.startsWith("<") ? text : text.replace("\n", "<br />"));
                return formattedNote.toString();
            }
        }
        else {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final ApplicationContext applicationContext;
    
}

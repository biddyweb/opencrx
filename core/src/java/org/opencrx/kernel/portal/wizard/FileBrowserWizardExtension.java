/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: FileBrowserWizardExtension
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2012, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.wizard;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileBrowserWizardExtension {

	public FileBrowserWizardExtension() {		
	}
	
	/**
	 * If true, all operations (besides upload and native commands) 
	 * which change something on the file system are permitted
	 */
	public boolean isReadOnly(
	) {
		return true;
	}
	
	/**
	 * If true, uploads are allowed even if READ_ONLY = true
	 * @return
	 */
	public boolean allowUpload(
	) {
		return false;
	}
	
	public boolean allowNativeCommands(
	) {
		return false;
	}
	
	/**
	 * Allow browsing and file manipulation only in certain directories
	 * @return
	 */
	public boolean restrictBrowsing(
	) {
		return true;
	}
	
    /**
     * If true, the user is allowed to browse only in RESTRICT_PATH,
     * if false, the user is allowed to browse all directories besides RESTRICT_PATH
     * @return
     */
	public boolean restrictWhitelist(
	) {
		return true;
	}

	/**
	 * Restrict access to paths. Paths, separated by semicolon
	 * @return
	 */
	public List<String> getRestrictPaths(
	) {
    	try {
			return Arrays.asList(
				new File(".").getCanonicalFile().getAbsolutePath()
			);
    	} catch(Exception e) {}
    	return Collections.emptyList();
	}

	/**
	 * Return list of forbidden files.
	 * @return
	 */
	public List<String> getForbiddenFiles(
	) {
		return Arrays.asList(
			"openejb.xml", 
			"tomcat-users.xml",
			"users.properties"
		);				
	}
	
}

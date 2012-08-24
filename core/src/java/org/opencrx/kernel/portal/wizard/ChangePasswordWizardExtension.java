/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: ChangePasswordWizardExtension
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

import org.openmdx.portal.servlet.ApplicationContext;

public class ChangePasswordWizardExtension {

	public static class ValidationException extends Exception {

		public ValidationException(
			String s
		) {
			super(s);
		}
		
        private static final long serialVersionUID = -4280012013952018121L;
		
	}
	
	public ChangePasswordWizardExtension(
	) {
		
	}
	
	boolean containsOnlyCharsOf (
		String password,
		String choiceStr
	) {
		boolean isOk = true;
		for(int i = 0; i < password.length() && isOk; i++) {
			if (!choiceStr.contains(password.substring(i, i+1))) {
				isOk = false;
			}
		}
		return isOk;
	}

	public boolean containsCharOf (
		String password,
		String choiceStr
	) {
		boolean isOk = false;
		for(int i = 0; i < password.length() && !isOk; i++) {
			if(choiceStr.contains(password.substring(i, i+1))) {
				isOk = true;
			}
		}
		return isOk;
	}
	
	protected void validatePasswordLength(
		String password,
		ApplicationContext app		
	) throws ValidationException {
//		if(password.length() < 8) {
//			throw new ValidationException("Password too short - minimal length is 8 characters");
//		}
	}
	
	protected void validateContainsUsername(
		String password,
		String username,
		ApplicationContext app		
	) throws ValidationException {
//		if(password.contains(username)) {
//			throw new ValidationException("Password contains user name");
//		}
	}

	protected void validateAtLeastOneUpperChar(
		String password,
		ApplicationContext app		
	) throws ValidationException {
//		if(!containsCharOf(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
//			throw new ValidationException("Password must contain at least 1 upper case character from ABCDE...XYZ");
//		}
	}

	protected void validateAtLeastOneLowerChar(
		String password,
		ApplicationContext app		
	) throws ValidationException {
//		if(!containsCharOf(password, "abcdefghijklmnopqrstuvwxyz")) {
//			throw new ValidationException("Password must contain at least 1 lower case character from abcde...xyz");
//		}
	}

	protected void validateAtLeastOneDigit(
		String password,
		ApplicationContext app		
	) throws ValidationException {
//		if(!containsCharOf(password, "0123456789")) {
//			throw new ValidationException("Password must contain at least 1 digit from 0123456789");
//		}
	}

	protected void validateAtLeastOneSpecialChar(
		String password,
		ApplicationContext app		
	) throws ValidationException {
//		if(!containsCharOf(password, "!$#%.")) {
//			throw new ValidationException("Password must contain at least 1 special character ! $ # % or .");
//		}
	}

	protected void validateValidCharsOnly(
		String password,
		ApplicationContext app		
	) throws ValidationException {
		if(!this.containsOnlyCharsOf(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%&*()_+=-[]/<>\\|}{:;,.")) {
			throw new ValidationException("Password contains illegal characters - allowed are alpha characters ABC..XYZ, digits 0..9, and ~!@#$%&*()_+=-[]/<>\\|}{:;,.");
		}
	}
	
	public void validatePassword(
		String password,
		String username,
		ApplicationContext app
	) throws ValidationException {
		this.validatePasswordLength(password, app);
		this.validateContainsUsername(password, username, app);
		this.validateAtLeastOneUpperChar(password, app);
		this.validateAtLeastOneLowerChar(password, app);
		this.validateAtLeastOneDigit(password, app);
		this.validateAtLeastOneSpecialChar(password, app);
		this.validateValidCharsOnly(password, app);
	}

}

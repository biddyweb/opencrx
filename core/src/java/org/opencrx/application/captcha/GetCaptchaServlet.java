/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: GetCaptchaServlet.java,v 1.5 2009/11/23 16:51:15 wfro Exp $
 * Description: GetCaptchaServlet
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/11/23 16:51:15 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
package org.opencrx.application.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmdx.kernel.log.SysLog;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class GetCaptchaServlet extends HttpServlet {

	//-----------------------------------------------------------------------
    public void init(
    	ServletConfig servletConfig
    ) throws ServletException {
        super.init(servletConfig);
    }

    //-----------------------------------------------------------------------
    protected void doGet(
    	HttpServletRequest request, 
    	HttpServletResponse response
    ) throws ServletException, IOException {
		byte[] captchaChallengeAsJpeg = null;
		// the output stream to render the captcha image as jpeg into
	 	ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
	 	try {
	 		// get the session id that will identify the generated captcha.
	 		// the same id must be used to validate the response, the session id is a good candidate!
	 		String captchaId = request.getParameter("j_captcha_id") == null ?
	 			request.getSession().getId() :
	 				request.getParameter("j_captcha_id");
	 		// call the ImageCaptchaService getChallenge method
	 		ImageCaptchaService captchaService = org.opencrx.application.captcha.CaptchaService.getInstance();
	 		SysLog.detail("captchaService", captchaService);
	 		SysLog.detail("captchaId", captchaId);
			BufferedImage challenge =
				captchaService.getImageChallengeForID(
					captchaId,
	                request.getLocale()
				);
	     	// a jpeg encoder
	     	JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(jpegOutputStream);
	     	jpegEncoder.encode(challenge);
		} 
	 	catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
	 	} 
	 	catch (CaptchaServiceException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	     	return;
	 	}
	 	captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
	 	// flush it in the response
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
	 	response.setContentType("image/jpeg");
	 	ServletOutputStream os = response.getOutputStream();
	 	os.write(captchaChallengeAsJpeg);
	 	os.flush();
	 	os.close();
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -2632900250827951264L;
    
}

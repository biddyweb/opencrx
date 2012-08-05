/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: NavigationControl.java,v 1.3 2011/07/07 22:40:05 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/07/07 22:40:05 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.control;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefObject;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.action.SelectObjectAction;

public class NavigationControl extends org.openmdx.portal.servlet.control.NavigationControl {

	//-----------------------------------------------------------------------
	public NavigationControl(
		String id, 
		String locale, 
		int localeAsIndex
	) {
	    super(id, locale, localeAsIndex);
    }

	//-----------------------------------------------------------------------
	public static void paintAlertBox(
		ViewPort p,
		boolean forEditing
	) throws ServiceException {
		ApplicationContext app = p.getApplicationContext();
		RefObject[] rootObjects = app.getRootObject();
		for(RefObject rootObject: rootObjects) {
		    if(rootObject instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
		    	PersistenceManager pm = JDOHelper.getPersistenceManager(rootObject);
		        org.opencrx.kernel.home1.cci2.AlertQuery alertQuery = (org.opencrx.kernel.home1.cci2.AlertQuery)pm.newQuery(org.opencrx.kernel.home1.jmi1.Alert.class);
		        alertQuery.alertState().lessThanOrEqualTo(new Short((short)1));
		        Collection<org.opencrx.kernel.home1.jmi1.Alert> alerts = ((org.opencrx.kernel.home1.jmi1.UserHome)rootObject).getAlert(alertQuery);
		        int alertsSize = 0;
		        boolean hasMoreAlerts = false;
		        for(org.opencrx.kernel.home1.jmi1.Alert alert: alerts) {
		            alertsSize++;
		            if(alertsSize >= 9) {
		                hasMoreAlerts = true;
		                break;
		            }
		        }
		        if(alertsSize > 0) {
		            Action selectAlertsAction = new Action(
		                SelectObjectAction.EVENT_ID,
			            new Action.Parameter[]{
       		                new Action.Parameter(Action.PARAMETER_PANE, "0"),
       		                new Action.Parameter(Action.PARAMETER_REFERENCE, "0"),
       		                new Action.Parameter(Action.PARAMETER_OBJECTXRI, rootObject.refMofId())
		                },
		                "Alerts",
		                true
		            );
		            p.write("<div id=\"alertBox\" onclick=\"javascript:window.location.href=", p.getEvalHRef(selectAlertsAction), ";\"><div>", Integer.toString(alertsSize), (hasMoreAlerts ? "+" : ""), "</div></div>");
		        }
		        break;
			  }
		}		
	}
	
	//-----------------------------------------------------------------------
	public static void paintRssLinks(
		ViewPort p,
		boolean forEditing
	) throws ServiceException {
		if(p.getView().getObject() instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
			org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)p.getView().getObject();
			String providerName = userHome.refGetPath().get(2);
			String segmentName = userHome.refGetPath().get(4);
			String link = 
				"<link rel='alternate' type='application/rss+xml' " +
	            " href='" + p.getHttpServletRequest().getContextPath().replace("-core-", "-news-") + "/news?id=" + providerName + "/" + segmentName + "&type=rss' " + 
	            "title='" + userHome.refGetPath().getBase() + "@" + providerName + ":" + segmentName + "' />";
			p.write(link);
		}
	}
	
	//-----------------------------------------------------------------------
	// Members
	//-----------------------------------------------------------------------
    private static final long serialVersionUID = 7946560008514514040L;
	
}

/*
 * ====================================================================
 * Project:     openCRX/CalDAV, http://www.opencrx.org/
 * Name:        $Id: IMAPServer.java,v 1.21 2008/01/29 17:18:24 wfro Exp $
 * Description: IMAPServer
 * Revision:    $Revision: 1.21 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/01/29 17:18:24 $
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
package org.opencrx.groupware.imap;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.mail.MessagingException;

import org.opencrx.groupware.generic.ActivitiesHelper;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.openmdx.kernel.id.UUIDs;

public class IMAPServer implements Runnable {
    
	//-----------------------------------------------------------------------
	protected IMAPServer(
	    PersistenceManagerFactory persistenceManagerFactory,
	    String providerName,
	    int portNumber,
	    boolean isDebug,
	    int delayOnStartup
	) {
	    this.persistenceManagerFactory = persistenceManagerFactory;
		this.portNumber = portNumber;
		this.providerName = providerName;
		this.isDebug = isDebug;
		this.delayOnStartup = delayOnStartup;
	}
	
    //-----------------------------------------------------------------------
	public void run(
	) {
        try {           
            Thread.sleep(this.delayOnStartup * 1000L);
            ServerSocket serverSocket = new ServerSocket(this.portNumber);
            System.out.println("IMAPServer " + this.providerName + " is listening on port " + this.portNumber);            
            while(true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(600000);
                Thread clientHandler = new Thread(
                    new IMAPSessionImpl(socket, this)
                );
                clientHandler.start();
            }
        }
        catch(Exception err) {
            err.printStackTrace();
        }
	}

    //-----------------------------------------------------------------------
	public String getProviderName(
	) {
	    return this.providerName;
	}
	
    //-----------------------------------------------------------------------
    public boolean isDebug(
    ) {
        return this.isDebug;
    }
    
    //-----------------------------------------------------------------------
	public static void main(
	    String[] args
	) {
	    startServer(
	        null,
	        "CRX",
	        143,
	        true,
	        0
	    );
	}
	
    //-----------------------------------------------------------------------
	public static void startServer(
        PersistenceManagerFactory persistenceManagerFactory,
        String providerName,
        int portNumber,
        boolean isDebug,
        int delayOnStartup
	) {
        IMAPServer server = new IMAPServer(
            persistenceManagerFactory,
            providerName,
            portNumber,
            isDebug,
            delayOnStartup
        );
        Thread serverThread = new Thread(server);
        serverThread.start();    
	}

    //-----------------------------------------------------------------------
	private String encodeFolderName(
	    String name
	) {
	    // Slash is qualified name separator. Do not allow in folder names
	    name = name.replace("/", ".");
	    StringBuilder encodedName = new StringBuilder();
	    for(int i = 0; i < name.length(); i++) {
	        char c = name.charAt(i);
	        if((c <= 127) && (c != '&')) {
	            encodedName.append(c);
	        }
	        else {
	            String base64EncodedChar = org.openmdx.base.text.conversion.Base64.encode(new byte[]{(byte)(c / 256), (byte)(c % 256)});
	            encodedName.append("&" + base64EncodedChar.substring(0, base64EncodedChar.length()-1) + "-");
	        }
	    }
	    return encodedName.toString();	    
	}
	
    //-----------------------------------------------------------------------
    public List<IMAPFolderImpl> getFolders(
        String username
    ) throws MessagingException {     
        List<IMAPFolderImpl> folders = this.folders.get(username);
        if(folders == null) {
            String principalName = username.substring(0, username.indexOf("@"));
            String segmentName = username.substring(username.indexOf("@") + 1);
            PersistenceManager pm = this.getPersistenceManagerFactory().getPersistenceManager(
                principalName, 
                UUIDs.getGenerator().next().toString()
            );
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
                    "xri:@openmdx:org.opencrx.kernel.activity1/provider/" + this.getProviderName() + "/segment/" + segmentName
                );
            this.folders.put(
                username,
                folders = new ArrayList<IMAPFolderImpl>()
            );
            folders.add(
                new IMAPFolderImpl(
                    "INBOX",
                    null
                )
            );            
            Collection<ActivityTracker> trackers = activitySegment.getActivityTracker();
            for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: trackers) {
                ActivitiesHelper activitiesHelper = new ActivitiesHelper(pm);
                activitiesHelper.parseFilteredActivitiesUri(
                    "/" + this.getProviderName() + "/" + segmentName + "/tracker/" + group.getName()
                );
                folders.add(
                    new IMAPFolderImpl(
                        "INBOX/" + this.getProviderName() + "/" + segmentName + "/tracker/" + this.encodeFolderName(group.getName()),
                        activitiesHelper
                    )
                );
            }
            Collection<ActivityMilestone> milestones = activitySegment.getActivityMilestone();
            for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: milestones) {
                ActivitiesHelper activitiesHelper = new ActivitiesHelper(pm);
                activitiesHelper.parseFilteredActivitiesUri(
                    "/" + this.getProviderName() + "/" + segmentName + "/milestone/" + group.getName()
                );
                folders.add(
                    new IMAPFolderImpl(
                        "INBOX/" + this.getProviderName() + "/" + segmentName + "/milestone/" + this.encodeFolderName(group.getName()),
                        activitiesHelper
                    )
                );
            }
            Collection<ActivityCategory> categories = activitySegment.getActivityCategory();
            for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: categories) {
                ActivitiesHelper activitiesHelper = new ActivitiesHelper(pm);
                activitiesHelper.parseFilteredActivitiesUri(
                    "/" + this.getProviderName() + "/" + segmentName + "/category/" + group.getName()
                );
                folders.add(
                    new IMAPFolderImpl(
                        "INBOX/" + this.getProviderName() + "/" + segmentName + "/category/" + this.encodeFolderName(group.getName()),
                        activitiesHelper
                    )
                );
            }
        }
        return folders;
    }
            
    //-----------------------------------------------------------------------
	public PersistenceManagerFactory getPersistenceManagerFactory(
	) {
	    return this.persistenceManagerFactory;
	}
	
    //-----------------------------------------------------------------------
	// Members
    //-----------------------------------------------------------------------
    protected final int portNumber;
    protected final String providerName;
    protected final boolean isDebug;
    protected final int delayOnStartup;
    protected final PersistenceManagerFactory persistenceManagerFactory;
    protected Map<String,List<IMAPFolderImpl>> folders = new HashMap<String,List<IMAPFolderImpl>>();
    
}

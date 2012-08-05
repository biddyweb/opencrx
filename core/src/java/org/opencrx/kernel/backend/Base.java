/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Base.java,v 1.39 2010/06/28 17:27:38 wfro Exp $
 * Description: Base
 * Revision:    $Revision: 1.39 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/06/28 17:27:38 $
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
package org.opencrx.kernel.backend;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.home1.jmi1.Alert;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.persistence.cci.UserObjects;

public class Base extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Base());
	}
	
    //-------------------------------------------------------------------------
	public static Base getInstance(
	) throws ServiceException {
		return getInstance(Base.class);
	}

	//-------------------------------------------------------------------------
	protected Base(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public void sendAlert(
    	ContextCapable target,
        String toUsers,        
        String name,
        String description,
        short importance,
        Integer resendDelayInSeconds,
        ContextCapable reference
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(target);    	
        StringTokenizer tokenizer = new StringTokenizer(
            toUsers == null ? 
                "" : 
                toUsers, ";, "
        );
        while(tokenizer.hasMoreTokens()) {
            String toUser = tokenizer.nextToken();
            UserHome userHome = UserHomes.getInstance().getUserHome(
                toUser,
                target.refGetPath(),
                pm
            );
            if(userHome != null) {
            	ContextCapable alertReference = (reference == null) || !JDOHelper.isPersistent(reference) ? 
                	target : 
                	reference;                
                // Only create alert if there is not already an alert with the 
                // same alert reference and created within the delay period.
                PersistenceManager pmRoot = pm.getPersistenceManagerFactory().getPersistenceManager(
                	SecurityKeys.ROOT_PRINCIPAL, 
                	null
                );
                AlertQuery alertQuery = (AlertQuery)pmRoot.newQuery(Alert.class);
                alertQuery.thereExistsReference().equalTo(alertReference);
                alertQuery.createdAt().greaterThanOrEqualTo(
                	new Date(System.currentTimeMillis() - 1000 * (resendDelayInSeconds == null ? 0 : resendDelayInSeconds.intValue()))
                );
                UserHome userHomeByRoot = (UserHome)pmRoot.getObjectById(userHome.refGetPath());
                List<Alert> alertsByRoot = userHomeByRoot.getAlert(alertQuery);
                if(alertsByRoot.isEmpty()) {
                	Alert alertByRoot = pmRoot.newInstance(Alert.class);
                	alertByRoot.refInitialize(false, false);
                	alertByRoot.setAlertState(new Short((short)1));
                	alertByRoot.setName(
                        name == null || name.length() == 0 ?
                            "--" : // name is mandatory
                            name
                    );
                    if(description != null) {
                        alertByRoot.setDescription(description);
                    }
                    alertByRoot.setImportance(importance);
                    alertByRoot.setReference(alertReference);
                    alertByRoot.getOwningGroup().clear();
                    alertByRoot.getOwningGroup().addAll(
                        userHomeByRoot.getOwningGroup()
                    );
                    pmRoot.currentTransaction().begin();
                    userHomeByRoot.addAlert(
                    	false,
                    	this.getUidAsString(),
                    	alertByRoot
                    );
                    pmRoot.currentTransaction().commit();
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void assignToMe(
        RefObject_1_0 target,
        boolean overwrite,
        Set<String> attributeFilter
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(target);    	
        if(attributeFilter == null) {
            attributeFilter = new HashSet<String>();
            attributeFilter.add("assignedTo");
            attributeFilter.add("salesRep");
            attributeFilter.add("ratedBy");
        }
        String objectClass = target.refClass().refMofId();
        Model_1_0 model = Model_1Factory.getModel();
        ModelElement_1_0 classDef = model.getElement(objectClass);
        Map<String,ModelElement_1_0> attributeDefs = model.getAttributeDefs(classDef, false, true);
        // Test whether class has feature assignedTo. If yes and obj has
        // not already set assignedTo assign current user as default value
        List<String> principalChain = UserObjects.getPrincipalChain(pm);
        if(!principalChain.isEmpty()) {
            UserHome userHome = UserHomes.getInstance().getUserHome(
                target.refGetPath(),
                pm
            );
            if((userHome != null) && (userHome.getContact() != null)) {
                for(Iterator<String> i = attributeFilter.iterator(); i.hasNext(); ) {
                    String attribute = i.next();
                    if(
                        attributeDefs.keySet().contains(attribute) &&
                        (overwrite || (target.refGetValue(attribute) == null))
                    ) {
                        target.refSetValue(
                        	attribute,
                            userHome.getContact()
                        ); 
                    }
                }
            }
        }
    }
    
	//-------------------------------------------------------------------------
    /**
     * Counts the number of occurences of items in report and returns a string
     * of the form item0: n0, item1: n1, etc.
     */
    public String analyseReport(
        List<String> report
    ) {
        Map<Object,Short> reportAsMap = new HashMap<Object,Short>();
        for(int i = 0; i < report.size(); i++) {
            Object key = report.get(i);
            Short count = reportAsMap.get(key);
            if(count == null) {
                count = new Short((short)0);
            }
            reportAsMap.put(
                key,
                new Short((short)(count.shortValue() + 1))
            );
        }
        return reportAsMap.toString();
    }
        
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final short IMPORT_EXPORT_OK = 0;
    public static final short IMPORT_EXPORT_FORMAT_NOT_SUPPORTED = 1;
    public static final short IMPORT_EXPORT_ITEM_NOT_VALID = 2;
    public static final short IMPORT_EXPORT_MISSING_DATA = 3;
    
    public static final String MIME_TYPE_ZIP = "application/zip";

	public static final String COMMENT_SEPARATOR_BOT = "//"; // at beginning of text
	public static final String COMMENT_SEPARATOR_EOT = " //"; // at end of text
    
}

//--- End of File -----------------------------------------------------------

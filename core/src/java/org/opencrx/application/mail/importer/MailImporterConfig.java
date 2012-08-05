/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: MailImporterConfig.java,v 1.2 2009/01/06 13:00:22 wfro Exp $
 * Description: MailImporterConfig
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/01/06 13:00:22 $
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
package org.opencrx.application.mail.importer;

import java.util.Iterator;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.admin1.jmi1.Admin1Package;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

public class MailImporterConfig {

    //-----------------------------------------------------------------------
    /**
     * Get the MailImporter config from component configuration. Create default
     * properties on-the-fly
     */
    public MailImporterConfig(
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        String mailServiceName = null;
        String mailbox = null;
        Boolean deleteImportedMessages = null;
        Boolean caseInsensitiveAddressLookup = null;
        
        try {
            Admin1Package adminPkg = Utils.getAdminPackage(pm);
            org.opencrx.kernel.base.jmi1.BasePackage basePkg = Utils.getBasePackage(pm);
            org.opencrx.kernel.admin1.jmi1.Segment adminSegment = 
                (org.opencrx.kernel.admin1.jmi1.Segment)pm.getObjectById(
                    new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root")
                );
            org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = null;
            // Get component configuration
            try {
                componentConfiguration = adminSegment.getConfiguration(
                    COMPONENT_CONFIGURATION_ID
                );
            }
            catch(Exception e) {}
            String optionPrefix = providerName + "." + segmentName + ".";
            if(componentConfiguration == null) {
                componentConfiguration = adminPkg.getComponentConfiguration().createComponentConfiguration();
                componentConfiguration.setName(COMPONENT_CONFIGURATION_ID);
                pm.currentTransaction().begin();
                adminSegment.addConfiguration(
                    false,                     
                    COMPONENT_CONFIGURATION_ID,
                    componentConfiguration
                );
                UUIDGenerator uuids = UUIDs.getGenerator();
                // Default for mail service name
                org.opencrx.kernel.base.jmi1.StringProperty sp = basePkg.getStringProperty().createStringProperty();
                sp.setName(optionPrefix + MailImporterConfig.OPTION_MAIL_SERVICE_NAME + ".Default");
                sp.setDescription("Mail service name");
                sp.setStringValue("/mail/provider/" + providerName);
                componentConfiguration.addProperty(
                    false,
                    uuids.next().toString(),
                    sp
                );
                // Default for mailbox
                sp = basePkg.getStringProperty().createStringProperty();
                sp.setName(optionPrefix + MailImporterConfig.OPTION_MAIL_BOX + ".Default");
                sp.setDescription("Mailbox name");
                sp.setStringValue("INBOX");
                componentConfiguration.addProperty(
                    false,
                    uuids.next().toString(),
                    sp
                );
                // Default for deleteImportedMessages
                org.opencrx.kernel.base.jmi1.BooleanProperty bp = basePkg.getBooleanProperty().createBooleanProperty();
                bp.setName(optionPrefix + MailImporterConfig.OPTION_MAIL_DELETE_IMPORTED_MESSAGES + ".Default");
                bp.setDescription("Delete imported messages");
                bp.setBooleanValue(false);
                componentConfiguration.addProperty(
                    false,
                    uuids.next().toString(),
                    bp
                );
                // Default for caseInsenstiveAddressLookup
                bp = basePkg.getBooleanProperty().createBooleanProperty();
                bp.setName(optionPrefix + MailImporterConfig.OPTION_CASE_INSENSITIVE_ADDRESS_LOOKUP + ".Default");
                bp.setDescription("Case insensitive address lookup");
                bp.setBooleanValue(true);
                componentConfiguration.addProperty(
                    false,
                    uuids.next().toString(),
                    bp
                );
                pm.currentTransaction().commit();
                componentConfiguration = adminSegment.getConfiguration(
                    COMPONENT_CONFIGURATION_ID
                );
            }
            // Get configuration
            for(
                int i = 0; 
                i < 2;
                i++
            ) {
                String suffix = new String[]{".Default", ""}[i];
                for(
                    Iterator j = componentConfiguration.getProperty().iterator(); 
                    j.hasNext(); 
                ) {                    
                    org.opencrx.kernel.base.jmi1.Property property = (org.opencrx.kernel.base.jmi1.Property)j.next();
                    if((optionPrefix + MailImporterConfig.OPTION_MAIL_SERVICE_NAME + suffix).equals(property.getName())) {
                        mailServiceName = ((org.opencrx.kernel.base.jmi1.StringProperty)property).getStringValue();
                    }
                    else if((optionPrefix + MailImporterConfig.OPTION_MAIL_BOX + suffix).equals(property.getName())) {
                        mailbox = ((org.opencrx.kernel.base.jmi1.StringProperty)property).getStringValue();
                    }
                    else if((optionPrefix + MailImporterConfig.OPTION_MAIL_DELETE_IMPORTED_MESSAGES + suffix).equals(property.getName())) {
                        deleteImportedMessages = new Boolean(((org.opencrx.kernel.base.jmi1.BooleanProperty)property).isBooleanValue());
                    }
                    else if((optionPrefix + MailImporterConfig.OPTION_CASE_INSENSITIVE_ADDRESS_LOOKUP + suffix).equals(property.getName())) {
                        caseInsensitiveAddressLookup = new Boolean(((org.opencrx.kernel.base.jmi1.BooleanProperty)property).isBooleanValue());
                    }
                }
            }
            if(mailServiceName == null) {
                AppLog.warning("Could not get option " + MailImporterConfig.OPTION_MAIL_SERVICE_NAME + " from configuration. Either it does not exist or access level browse is not set to global. Fallback to system default. Used option prefix", optionPrefix);
                AppLog.warning("Checked configuration properties", componentConfiguration.getProperty());
            }
            if(mailbox == null) {
                AppLog.warning("Could not get option " + MailImporterConfig.OPTION_MAIL_BOX + " from configuration. Either it does not exist or access level browse is not set to global. Fallback to system default. Used option prefix", optionPrefix);
                AppLog.warning("Checked configuration properties", componentConfiguration.getProperty());                
            }
        }
        catch(JmiServiceException e0) {
            AppLog.info("Can not create default configuration", e0.getMessage());
            throw e0;
        }
        // Fallback to servlet config
        this.mailServiceName = mailServiceName == null 
            ? "/mail/provider/" + providerName 
            : mailServiceName;
        this.mailbox = mailbox == null 
            ? "INBOX" 
            : mailbox;
        this.deleteImportedMessages = deleteImportedMessages == null 
            ? false 
            : deleteImportedMessages.booleanValue();
        this.caseInsensitiveAddressLookup = caseInsensitiveAddressLookup == null 
            ? true 
            : caseInsensitiveAddressLookup.booleanValue();
      }
    
    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(
    ) {
        StringBuffer tmp = new StringBuffer();
        tmp.append("EMailServerConfiguration{");
        tmp.append(OPTION_MAIL_SERVICE_NAME + "='").append(this.mailServiceName).append("', ");
        tmp.append(OPTION_MAIL_BOX + "='").append(this.mailbox).append("', ");
        tmp.append(OPTION_MAIL_DELETE_IMPORTED_MESSAGES + "='").append(this.deleteImportedMessages).append("', ");
        return tmp.toString();
    }

    //-------------------------------------------------------------------------
    public String getMailServiceName(
    ) {
        return this.mailServiceName;
    }
  
    //-------------------------------------------------------------------------
    public String getMailbox(
    ) {
        return this.mailbox;
    }
  
    //-------------------------------------------------------------------------
    public boolean deleteImportedMessages(
    ) {
        return this.deleteImportedMessages;
    }
  
    //-------------------------------------------------------------------------
    public boolean getCaseInsensitiveAddressLookup(
    ) {
        return this.caseInsensitiveAddressLookup;
    }
  
    //-------------------------------------------------------------------------
    private static final String COMPONENT_CONFIGURATION_ID = "MailImporterServlet";
        
    private static final String OPTION_MAIL_BOX = "mailbox";
    private static final String OPTION_MAIL_SERVICE_NAME = "mailServiceName";
    private static final String OPTION_MAIL_DELETE_IMPORTED_MESSAGES = "deleteImportedMessages";
    private static final String OPTION_CASE_INSENSITIVE_ADDRESS_LOOKUP = "caseInsenstiveAddressLookup";
    
    protected final String mailServiceName;
    protected final String mailbox;
    protected final boolean deleteImportedMessages;
    protected final boolean caseInsensitiveAddressLookup;
  
}

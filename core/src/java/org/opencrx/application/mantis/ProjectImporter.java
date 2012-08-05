/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ProjectImporter.java,v 1.7 2008/02/12 19:49:06 wfro Exp $
 * Description: openCRX Mantis Importer
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/12 19:49:06 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2006, CRIXP Corp., Switzerland
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
package org.opencrx.application.mantis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.ResourceQuery;
import org.opencrx.kernel.activity1.jmi1.Incident;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.activity1.jmi1.ResourceAssignment;
import org.opencrx.kernel.generic.jmi1.Media;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.pattern.StringExpression;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.kernel.application.cci.Classes;

public class ProjectImporter {

    //-----------------------------------------------------------------------
    public ProjectImporter(
        RefPackage_1_1 rootPkg,
        String jdbcDriverClass,
        String jdbcConnectionUrl,
        String jdbcUser,
        String jdbcPassword
    ) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        this.rootPkg = rootPkg;
        Classes.getKernelClass(jdbcDriverClass).newInstance();
        this.conn = DriverManager.getConnection(
            jdbcConnectionUrl,
            jdbcUser,
            jdbcPassword
        );
    }
    
    //-----------------------------------------------------------------------
    private String decodeMantisText(
        String s
    ) {
        String t = StringExpression.compile("\\&quot;").matcher(s).replaceAll("\"");
        t = StringExpression.compile("\\'").matcher(t).replaceAll("\'");
        t = StringExpression.compile("\\\\").matcher(t).replaceAll("\\");
        t = StringExpression.compile("&lt;").matcher(t).replaceAll("<");
        t = StringExpression.compile("&gt;").matcher(t).replaceAll(">");
        t = StringExpression.compile("&amp;").matcher(t).replaceAll("&");
        return t;
    }
    
    //-----------------------------------------------------------------------
    private Contact getContact(
        int userId,
        org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg,
        org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
        List errors
    ) throws SQLException, ServiceException {
        if(userId == 0) {
            return null;
        }
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            s = conn.prepareStatement("SELECT * FROM mantis_user_table WHERE id = ?");
            s.setInt(1, userId);
            s.executeQuery();
            rs = s.getResultSet();
            while(rs.next()) {
                String email = rs.getString("email");
                List emails = new ArrayList();
                StringTokenizer tokenizer = new StringTokenizer(email, ";", false);
                while(tokenizer.hasMoreTokens()) {
                    emails.add(tokenizer.nextToken());
                }
                ResourceQuery resourceQuery = activityPkg.createResourceQuery();
                resourceQuery.thereExistsDescription().equalTo(
                    (String[])emails.toArray(new String[emails.size()])
                );
                List resources = activitySegment.getResource(resourceQuery);
                if(resources.size() == 1) {
                    Resource resource = (Resource)resources.iterator().next();
                    if(resource.getContact() == null) {
                        errors.add(
                            "Resource has no assigned contact: " + email
                        );
                        return null;
                    }
                    return resource.getContact();
                }
                else {
                    errors.add(
                        "Resource not found: " + email
                    );
                    return null;
                }
            }
            errors.add(
                "User not found in mantis_user_table: " + userId
            );
            return null;
        }
        finally {
            rs.close();
            s.close();            
        }
    }
    
    //-----------------------------------------------------------------------
    public void close(
    ) throws SQLException {
        if(this.conn != null) {
            this.conn.close();
        }
        this.conn = null;
    }
    
    //-----------------------------------------------------------------------
    public void importProjects(
        String providerName,
        String segmentName,
        String projectName,
        String activityCreatorName,
        String processTransitionName,
        String bugCategoryNames,
        String fileBaseDir,
        List errors,
        List report
    ) {
        try {
              
            // incident types
            if((bugCategoryNames == null) || (bugCategoryNames.length() == 0)) {
                bugCategoryNames = "documentation, bug, software bug, hardware bug, feature request, note, question, task, installation, application form";
            }
            List bugCategories = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(bugCategoryNames, ",;", false);
            while(tokenizer.hasMoreTokens()) {
                bugCategories.add(
                    tokenizer.nextToken()
                );
            }
            
            // severities
            Map severities = new HashMap();
            severities.put(new Integer(70), new Integer(5));
            severities.put(new Integer(60), new Integer(4));
            severities.put(new Integer(50), new Integer(4));
            severities.put(new Integer(40), new Integer(2));
            severities.put(new Integer(10), new Integer(1));

            // activityStates
            Map activityStates = new HashMap();
            activityStates.put(new Integer(90), new Integer(2110));
            activityStates.put(new Integer(80), new Integer(120));
        
            PersistenceManager pm = this.rootPkg.refPersistenceManager();
            
            // get activity segment
            org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = 
                (org.opencrx.kernel.activity1.jmi1.Activity1Package)rootPkg.refPackage(
                    org.opencrx.kernel.activity1.jmi1.Activity1Package.class.getName()
                );            
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                (org.opencrx.kernel.activity1.jmi1.Segment)this.rootPkg.refObject(
                    "xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName
                );
            // generic package
            org.opencrx.kernel.generic.jmi1.GenericPackage genericPkg = 
                (org.opencrx.kernel.generic.jmi1.GenericPackage)rootPkg.refPackage(
                    org.opencrx.kernel.generic.jmi1.GenericPackage.class.getName()
                );            
            // Get activity creator
            org.opencrx.kernel.activity1.cci2.ActivityCreatorQuery activityCreatorFilter = 
                activityPkg.createActivityCreatorQuery();
            activityCreatorFilter.name().equalTo(
                new String[]{activityCreatorName}
            );            
            List activityCreators = activitySegment.getActivityCreator(activityCreatorFilter);
            org.opencrx.kernel.activity1.jmi1.ActivityCreator activityCreator = null;
            if(activityCreators.size() == 1) {
                activityCreator = (org.opencrx.kernel.activity1.jmi1.ActivityCreator)activityCreators.iterator().next();
            }
            else {
                errors.add(
                    "Activity creator not found: " + activityCreatorName
                );
                return;
            }
                
            // Get transition
            org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery processTransitionFilter = 
                activityPkg.createActivityProcessTransitionQuery();
            processTransitionFilter.name().equalTo(
                new String[]{processTransitionName}
            );            
            List processTransitions = activityCreator.getActivityType().getControlledBy().getTransition(processTransitionFilter);
            org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition processTransition = null;
            if(processTransitions.size() == 1) {
                processTransition = (org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition)processTransitions.iterator().next();
            }
            else {
                errors.add(
                    "Process transition not found: " + processTransitionName
                );
                return;
            }
            
            // importing
            System.out.println("openCRX Mantis Importer (importing to provider=" + providerName + "; segment=" + segmentName + ")");

            PreparedStatement s = null;
            ResultSet rs = null;
            NumberFormat idFormatter = new DecimalFormat("0000000");
            
            // mantis_project_table
            List includeProjectIds = new ArrayList();
            System.out.println("mantis_project_table");
            s = this.conn.prepareStatement("SELECT * FROM mantis_project_table");
            s.executeQuery();
            rs = s.getResultSet();
            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                if(projectName.equals(name)) {
                    includeProjectIds.add(new Integer(id));
                }
            }
            System.out.println();
            rs.close();
            s.close();
            System.out.println("Project ids: " + includeProjectIds);
                        
            // mantis_bug_table
            System.out.println("mantis_bug_table");
            s = this.conn.prepareStatement("SELECT * FROM mantis_bug_table ORDER BY last_updated");
            s.executeQuery();
            rs = s.getResultSet();
            int nBugs = 0;
            while(rs.next()) {
                int bugId = rs.getInt("id");
                int projectId = rs.getInt("project_id");
                if((includeProjectIds == null) || includeProjectIds.contains(new Integer(projectId))) {
                    int bugReporterId = rs.getInt("reporter_id");
                    int bugHandlerId = rs.getInt("handler_id");
                    int bugPriority = rs.getInt("priority");
                    int bugSeverity = rs.getInt("severity");
                    int bugReproducibility = rs.getInt("reproducibility");
                    int bugStatus = rs.getInt("status");
//                    int bugResolution = rs.getInt("resolution");
                    String bugCategory = rs.getString("category");
                    Date bugDateSubmitted = rs.getDate("date_submitted");
                    Date bugLastUpdated = rs.getDate("last_updated");
                    int bugTextId = rs.getInt("bug_text_id");
                    String bugPlatform = rs.getString("platform");
                    String bugVersion = rs.getString("version");
//                    String bugBuild = rs.getString("build");
                    String bugSummary = decodeMantisText(new String(rs.getBytes("summary"), "8859_1"));
                    String bugDescription = "";
//                    String bugStepsToReproduce = "";
                    String bugAdditionalInformation = "";
                    
                    // mantis_bug_text_table
                    PreparedStatement psBugTexts = this.conn.prepareStatement("SELECT * FROM mantis_bug_text_table WHERE id = ?");
                    psBugTexts.setInt(1, bugTextId);
                    psBugTexts.executeQuery();
                    ResultSet rsBugTexts = psBugTexts.getResultSet();
                    if(rsBugTexts.next()) {
                        bugDescription = decodeMantisText(new String(rsBugTexts.getBytes("description"), "8859_1"));
                        bugDescription = bugDescription.replaceAll("\r\n", "<br />");
                        bugDescription = bugDescription.replaceAll("\n", "<br />");                        
                        bugAdditionalInformation = decodeMantisText(new String(rsBugTexts.getBytes("additional_information"), "8859_1"));
                        bugAdditionalInformation = bugAdditionalInformation.replaceAll("\r\n", "<br />");
                        bugAdditionalInformation = bugAdditionalInformation.replaceAll("\n", "<br />");                                                
                    }
                    rsBugTexts.close();
                    psBugTexts.close();
                    
                    String activityId = idFormatter.format(bugId);
                    ActivityQuery activityFilter = activityPkg.createActivityQuery();
                    activityFilter.thereExistsMisc1().equalTo(
                        "mantis:" + activityId
                    );
                    List activities = activitySegment.getActivity(activityFilter);
                    if(activities.isEmpty()) {
                        System.out.println("Creating incident " + activityId);
                        pm.currentTransaction().begin();
                        NewActivityResult res = activityCreator.newActivity(
                            null, 
                            null, 
                            null, 
                            bugSummary, 
                            (short)(bugPriority / 10 - 1), 
                            null, 
                            null, 
                            bugDateSubmitted
                        );
                        pm.currentTransaction().commit();
                        Incident incident = (Incident)rootPkg.refObject(res.getActivity().refMofId());
                        // Update incident
                        pm.currentTransaction().begin();
                        incident.setActualStart(bugDateSubmitted);
                        incident.refSetValue(
                            SystemAttributes.MODIFIED_AT, 
                            bugLastUpdated
                        );                    
                        incident.setName(bugSummary);
                        bugAdditionalInformation = bugAdditionalInformation.length() > 0
                            ? bugDescription + "<br />~~~<br />" + bugAdditionalInformation
                            : bugDescription + "<br />";
                        bugDescription = null;
                        incident.setDescription(bugDescription);
                        incident.setDetailedDescription(bugAdditionalInformation);
                        incident.setMisc1("mantis:" + activityId);
                        Contact contact =  this.getContact(
                            bugReporterId, 
                            activityPkg, 
                            activitySegment, 
                            errors
                        );
                        if(!errors.isEmpty()) {
                            pm.currentTransaction().rollback();
                            return;
                        }
                        
                        incident.setReportingContact(contact);
                        contact = this.getContact(
                            bugHandlerId, 
                            activityPkg, 
                            activitySegment, 
                            errors
                        );
                        if(!errors.isEmpty()) {
                            pm.currentTransaction().rollback();
                            return;
                        }
                        
                        incident.setAssignedTo(contact);
                        // priority
                        incident.setPriority(
                            (short)(bugPriority / 10 - 1)
                        );
                        // severity
                        if(severities.get(new Integer(bugSeverity)) != null) {
                            incident.setSeverity(
                                ((Number)severities.get(new Integer(bugSeverity))).shortValue()
                            );
                        }
                        else {
                            incident.setSeverity((short)0);
                        }
                        // Proper percent complete is set at the end
                        incident.setPercentComplete(
                            processTransition.getNewPercentComplete()
                        );
                        incident.setCaseType(
                            bugCategories.contains(bugCategory)
                                ? (short)(bugCategories.indexOf(bugCategory) + 1)
                                : (short)0
                        );
                        incident.setReproducibility((short)bugReproducibility);                                        
                        if((bugVersion != null) && (bugVersion.length() > 0)) {
                            incident.setMisc2(bugVersion);
                        }
                        if((bugPlatform != null) && (bugPlatform.length() > 0)) {
                            incident.setMisc3(bugPlatform);
                        }
                        pm.currentTransaction().commit();
                        
                        // Reapply creator (set current state according to percent complete)
                        pm.currentTransaction().begin();
                        incident.reapplyActivityCreator(activityCreator);
                        pm.currentTransaction().commit();
                        
                        // files
                        PreparedStatement psBugFile = conn.prepareStatement("SELECT * FROM mantis_bug_file_table WHERE bug_id = ?");
                        psBugFile.setInt(1, bugId);
                        psBugFile.executeQuery();
                        ResultSet rsBugFile = psBugFile.getResultSet();
                        while(rsBugFile.next()) {
                            int fileId = rsBugFile.getInt("id");
                            File fileDiskfile = new File(decodeMantisText(new String(rsBugFile.getBytes("diskfile"), "8859_1")));
                            String fileName = decodeMantisText(new String(rsBugFile.getBytes("filename"), "8859_1"));
                            String fileDescription = decodeMantisText(new String(rsBugFile.getBytes("description"), "8859_1"));
                            String fileType = rsBugFile.getString("file_type");                            
                            File file = new File(new File(fileBaseDir, fileDiskfile.getParentFile().getName()), fileDiskfile.getName());                            
                            try {
                                System.out.println("Uploading file " + file);
                                Media media = genericPkg.getMedia().createMedia();
                                pm.currentTransaction().begin();
                                incident.addMedia(
                                    false,
                                    idFormatter.format(fileId),
                                    media
                                );
                                media.setDescription(fileDescription);
                                media.setContentName(fileName);
                                media.setContentMimeType(fileType);                                
                                InputStream is = new FileInputStream(file);
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                int b = 0;
                                while((b = is.read()) != -1) {
                                    os.write(b);
                                }
                                is.close();
                                os.close();
                                media.refSetValue("content", os.toByteArray());                                
                                pm.currentTransaction().commit();                                
                            }
                            catch(FileNotFoundException e) {
                                try {
                                    pm.currentTransaction().rollback();
                                } catch(Exception e0) {}
                                System.out.println("can not find file " + file);
                            }
                            catch(IOException e) {
                                try {
                                    pm.currentTransaction().rollback();
                                } catch(Exception e0) {}
                                System.out.println("can not read file " + file);
                            }
                        }
                        rsBugFile.close();
                        psBugFile.close();
                        
                        // bug notes
                        PreparedStatement psBugNote = this.conn.prepareStatement("SELECT * FROM mantis_bugnote_table WHERE bug_id = ? ORDER BY date_submitted");
                        psBugNote.setInt(1, bugId);
                        psBugNote.executeQuery();
                        ResultSet rsBugNote = psBugNote.getResultSet();
                        while(rsBugNote.next()) {     
                            int bugNoteReporterId = rsBugNote.getInt("reporter_id");
                            int bugNoteBugNoteTextId = rsBugNote.getInt("bugnote_text_id");
                            String note = null;
                            
                            PreparedStatement psBugNoteText = conn.prepareStatement("SELECT * FROM mantis_bugnote_text_table WHERE id = ?");
                            psBugNoteText.setInt(1, bugNoteBugNoteTextId);
                            psBugNoteText.executeQuery();
                            ResultSet rsBugTextNote = psBugNoteText.getResultSet();
                            if(rsBugTextNote.next()) {
                                note = decodeMantisText(new String(rsBugTextNote.getBytes("note"), "8859_1"));
                                if(note != null) {
                                    note = note.replaceAll("\r\n", "<br />");
                                    note = note.replaceAll("\n", "<br />");     
                                    note += "<br />";
                                }
                            }                        
                            pm.currentTransaction().begin();
                            Contact bugNoteReporter = this.getContact(bugNoteReporterId, activityPkg, activitySegment, errors);
                            if(!errors.isEmpty()) {
                                pm.currentTransaction().rollback();
                                return;
                            }
                            incident.doFollowUp(
                                bugNoteReporter,
                                note,
                                processTransition.getName() + " by " + bugNoteReporter.getFullName(),
                                processTransition
                            );
                            pm.currentTransaction().commit();
                        }
                        System.out.println();
                        rsBugNote.close();
                        psBugNote.close();                        
                        // Set percent complete
                        pm.currentTransaction().begin();
                        incident.setPercentComplete(new Short((short)bugStatus));
                        if((bugStatus == 80) || (bugStatus == 90)) {
                            incident.setPercentComplete(new Short((short)100));
                        }
                        pm.currentTransaction().commit();                        
                        // Reapply creator (set current state according to percent complete)
                        pm.currentTransaction().begin();
                        incident.reapplyActivityCreator(activityCreator);
                        pm.currentTransaction().commit();
                        // Remove all assigned resources
                        pm.currentTransaction().begin();
                        for(Iterator i = incident.getAssignedResource().iterator(); i.hasNext(); ) {
                            ResourceAssignment assignment = (ResourceAssignment)i.next();
                            assignment.refDelete();
                        }
                        pm.currentTransaction().commit();                                                
                    }
                    nBugs++;
                }
            }
            System.out.println();
            rs.close();
            s.close();
                        
            // done
            System.out.println("Done");
            report.add(
                "#Bugs imported: " + nBugs
            );
        }        
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            AppLog.error(e0.getMessage(), e0.getCause(), 1);
            errors.add(
                "Exception while importing (see log for more details): " + e.getMessage()
            );
        }
    }    

    //-----------------------------------------------------------------------
    private final RefPackage_1_1 rootPkg;
    private java.sql.Connection conn;
}

//--- End of File -----------------------------------------------------------

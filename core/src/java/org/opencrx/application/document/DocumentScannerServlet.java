/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: DocumentScannerServlet.java,v 1.8 2008/09/03 16:53:36 wfro Exp $
 * Description: IndexerServlet
 * Revision:    $Revision: 1.8 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/03 16:53:36 $
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
package org.opencrx.application.document;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.document1.cci2.DocumentFolderQuery;
import org.opencrx.kernel.document1.cci2.DocumentQuery;
import org.opencrx.kernel.document1.cci2.FolderAssignmentQuery;
import org.opencrx.kernel.document1.jmi1.Document1Package;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.FolderAssignment;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.document1.jmi1.ResourceIdentifier;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.ComponentConfigHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.w3c.cci2.BinaryLargeObjects;

/**
 * The DocumentScannerServlet scans a directory and its sub-directories. File names
 * are of the form <code>name { "#" folder name } "." extension</code>. Files with
 * the same name are mapped to the same Document. Documents are assigned to the
 * document folders specified by the # separated folder list. 
 */  
public class DocumentScannerServlet 
    extends HttpServlet {

    //-----------------------------------------------------------------------
    static class ModifiedSinceFileFilter implements FileFilter {
        
        public ModifiedSinceFileFilter(
            Date modifiedSince
        ) {
            this.modifiedSince = modifiedSince;
        }
        
        public boolean accept(
            File file
        ) {
            if(file.isDirectory()) {
                return true;
            }
            else if(file.lastModified() > this.modifiedSince.getTime()) {
                return true;
            }
            else {
                return false;
            }
        }

        private final Date modifiedSince;
    }
    
    //-----------------------------------------------------------------------
    public void init(
        ServletConfig config
    ) throws ServletException {
        super.init(config);        
        try {
            this.persistenceManagerFactory = Utils.getPersistenceManagerFactory();
        }
        catch (Exception e) {
            throw new ServletException("Can not get connection to persistence manager", e);
        }        
    }

    //-----------------------------------------------------------------------
    private org.opencrx.kernel.base.jmi1.StringProperty getComponentConfigProperty(
        PersistenceManager pm,
        String providerName,
        String segmentName,
        String name
    ) {
        org.opencrx.kernel.admin1.jmi1.ComponentConfiguration componentConfiguration = 
            ComponentConfigHelper.getComponentConfiguration(
                COMPONENT_CONFIGURATION_ID,
                providerName,
                pm,
                true,
                new String[][]{
                    new String[]{providerName + ".Standard." + OPTION_SCAN_DIR, ""},
                    new String[]{providerName + ".Standard." + OPTION_URL_PREFIX, ""},                    
                    new String[]{providerName + ".Standard." + OPTION_UPLOAD, ""},                    
                    new String[]{providerName + ".Standard." + OPTION_GROUPS, ""}                    
                }
            );            
        if(componentConfiguration != null) {
            return ComponentConfigHelper.getComponentConfigProperty(
                providerName + "." + segmentName + "." + name, 
                componentConfiguration
            );
        }
        return null;
    }
        
    //-----------------------------------------------------------------------    
    private void scanDocuments(
        File currentDir,
        File rootDir,
        FileFilter filter,
        org.opencrx.kernel.document1.jmi1.Segment documentSegment,
        String uriPrefix,
        Boolean upload,
        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> principalGroups,
        PersistenceManager pm
    ) {
        File[] files = currentDir.listFiles(filter);
        Document1Package documentPkg = Utils.getDocumentPackage(pm);
        if(files != null) {
            for(File file: files) {
                if(file.isDirectory()) {
                    this.scanDocuments(
                        file,
                        rootDir,
                        filter,
                        documentSegment,
                        uriPrefix,
                        upload,
                        principalGroups,
                        pm
                    );
                }
                else {
                    String filename = file.getName();
                    String ext = null;
                    if(filename.indexOf(".") > 0) {
                        int pos = filename.lastIndexOf(".");
                        ext = filename.substring(pos + 1);
                        filename = filename.substring(0, pos);
                    }
                    String[] names = filename.split("#");
                    boolean hasVersion = 
                        (names.length > 0) && 
                        (names[names.length-1].length() > 1) &&
                        (names[names.length-1].charAt(0) == 'v') &&
                        Character.isDigit(names[names.length-1].charAt(1));
                    String version = hasVersion ? 
                        names[names.length-1].substring(1) : 
                        "1.0";                    
                    // Get/create document
                    DocumentQuery query = documentPkg.createDocumentQuery();
                    String documentName = names[0] + (ext == null ? "" : "." + ext);
                    query.name().equalTo(documentName);
                    List<org.opencrx.kernel.document1.jmi1.Document> documents = documentSegment.getDocument(query); 
                    org.opencrx.kernel.document1.jmi1.Document document = null;
                    if(documents.isEmpty()) {
                        document = documentPkg.getDocument().createDocument();
                        document.refInitialize(false, false);
                        document.setName(documentName);
                        document.getOwningGroup().clear();
                        document.getOwningGroup().addAll(principalGroups);
                        try {
                            pm.currentTransaction().begin();
                            documentSegment.addDocument(
                                false,
                                UUIDConversion.toUID(this.uuids.next()), 
                                document
                            );
                            pm.currentTransaction().commit();
                        }
                        catch(Exception e) {
                            try {
                                pm.currentTransaction().rollback();
                            } catch(Exception e0) {}
                        }                                                                        
                    }
                    else {
                        document = documents.iterator().next();                        
                    }                    
                    // Find revision with specified version
                    DocumentRevision revision = null;
                    Collection<DocumentRevision> revisions = document.getRevision();
                    for(DocumentRevision r: revisions) {
                        if(version.equals(r.getVersion())) {
                            revision = r;
                            break;
                        }
                    }
                    // Create revision with specified version
                    if(revision == null) {                    
                        // Upload file
                        if(upload) {
                            MediaContent mediaContent = documentPkg.getMediaContent().createMediaContent();
                            mediaContent.refInitialize(false, false);
                            mediaContent.setContent(BinaryLargeObjects.valueOf(file));
                            mediaContent.setContentMimeType(
                                MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file.getName())
                            );
                            mediaContent.setContentName(file.getName());
                            revision = mediaContent;
                        }
                        // Create a document link
                        else {
                            ResourceIdentifier resourceIdentifier = documentPkg.getResourceIdentifier().createResourceIdentifier();
                            resourceIdentifier.refInitialize(false, false);
                            resourceIdentifier.setUri(
                                uriPrefix + 
                                currentDir.getPath().substring(rootDir.getPath().length()).replace("\\", "/") + 
                                "/" + 
                                file.getName()
                            );
                            revision = resourceIdentifier;
                        }
                        revision.setName(file.getName());
                        revision.setVersion(version);
                        revision.getOwningGroup().clear();
                        revision.getOwningGroup().addAll(principalGroups);
                        try {
                            pm.currentTransaction().begin();
                            document.addRevision(
                                false, 
                                UUIDConversion.toUID(this.uuids.next()), 
                                revision
                            );
                            document.setHeadRevision(revision);
                            pm.currentTransaction().commit();
                        }
                        catch(Exception e) {
                            try {
                                pm.currentTransaction().rollback();
                            } catch(Exception e0) {}
                        }                                            
                    }
                    // Assign document to folders
                    List<String> folderNames = new ArrayList<String>();
                    for(
                        int i = 1; 
                        i < (hasVersion ? names.length-1 : names.length); 
                        i++
                    ) {
                        folderNames.add(names[i]);
                    }
                    folderNames.add(currentDir.getName());
                    for(String folderName: folderNames) {
                        DocumentFolderQuery folderQuery = documentPkg.createDocumentFolderQuery();
                        folderQuery.name().equalTo(folderName);
                        List<org.opencrx.kernel.document1.jmi1.DocumentFolder> folders = documentSegment.getFolder(folderQuery);
                        org.opencrx.kernel.document1.jmi1.DocumentFolder folder = null;
                        if(folders.isEmpty()) {
                            folder = documentPkg.getDocumentFolder().createDocumentFolder();
                            folder.refInitialize(false, false);
                            folder.setName(folderName);
                            folder.getOwningGroup().clear();
                            folder.getOwningGroup().addAll(principalGroups);
                            try {
                                pm.currentTransaction().begin();
                                documentSegment.addFolder(
                                    false,
                                    UUIDConversion.toUID(this.uuids.next()), 
                                    folder
                                );
                                pm.currentTransaction().commit();
                            }
                            catch(Exception e) {
                                try {
                                    pm.currentTransaction().rollback();
                                } catch(Exception e0) {}
                            }                        
                        }
                        else {
                            folder = folders.iterator().next();
                        }
                        // Assign document to folder
                        FolderAssignmentQuery assignmentQuery = documentPkg.createFolderAssignmentQuery();
                        assignmentQuery.thereExistsDocumentFolder().equalTo(folder);
                        List<FolderAssignment> assignments = document.getDocumentFolderAssignment(assignmentQuery);
                        // Add assignment
                        if(assignments.isEmpty()) {
                            FolderAssignment assignment = documentPkg.getFolderAssignment().createFolderAssignment();
                            assignment.refInitialize(false, false);
                            assignment.setName(folder.getName());
                            assignment.setDocumentFolder(folder);
                            assignment.getOwningGroup().clear();
                            assignment.getOwningGroup().addAll(principalGroups);
                            try {
                                pm.currentTransaction().begin();
                                document.addDocumentFolderAssignment(
                                    false,
                                    UUIDConversion.toUID(this.uuids.next()), 
                                    assignment
                                );
                                pm.currentTransaction().commit();
                            }
                            catch(Exception e) {
                                try {
                                    pm.currentTransaction().rollback();
                                } catch(Exception e0) {}
                            }
                        }
                    }                
                }
            }
        }
    }
    
    //-----------------------------------------------------------------------    
    public void scanDocuments(
        String id,
        String providerName,
        String segmentName,
        HttpServletRequest req, 
        HttpServletResponse res        
    ) throws IOException {
        System.out.println(new Date().toString() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName);
        try {
            PersistenceManager pm = this.persistenceManagerFactory.getPersistenceManager(
                "admin-" + segmentName,
                UUIDs.getGenerator().next().toString()
            );    
            PersistenceManager rootPm = Utils.getPersistenceManagerFactory().getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
            Workflows.initWorkflows(
                pm, 
                providerName, 
                segmentName
            );
            for(int i = -1; i < 10; i++) {
                String idxSuffix = i < 0 ? "" : "[" + i + "]";
                org.opencrx.kernel.base.jmi1.StringProperty scanDir = this.getComponentConfigProperty(
                    rootPm,
                    providerName, 
                    segmentName, 
                    OPTION_SCAN_DIR + idxSuffix
                );
                org.opencrx.kernel.base.jmi1.StringProperty urlPrefix = this.getComponentConfigProperty(
                    rootPm,
                    providerName, 
                    segmentName, 
                    OPTION_URL_PREFIX + idxSuffix
                );
                org.opencrx.kernel.base.jmi1.StringProperty upload = this.getComponentConfigProperty(
                    rootPm,
                    providerName, 
                    segmentName, 
                    OPTION_UPLOAD + idxSuffix
                );
                org.opencrx.kernel.base.jmi1.StringProperty groups = this.getComponentConfigProperty(
                    rootPm,
                    providerName, 
                    segmentName, 
                    OPTION_GROUPS + idxSuffix
                );
                if(scanDir != null) {
                    Date lastRunAt = scanDir.getModifiedAt();
                    rootPm.currentTransaction().begin();
                    scanDir.setDescription(
                        "Last scan at " + new Date()
                    );
                    rootPm.currentTransaction().commit();
                    scanDir = (org.opencrx.kernel.base.jmi1.StringProperty)rootPm.getObjectById(scanDir.refGetPath());
                    Date runAt = scanDir.getModifiedAt();
                    if(
                        (scanDir.getStringValue() != null) && 
                        (scanDir.getStringValue().length() > 0)                
                    ) {
                        File dir = new File(scanDir.getStringValue());
                        FileFilter modifiedSinceFileFilter = new ModifiedSinceFileFilter(lastRunAt);
                        org.opencrx.kernel.document1.jmi1.Segment documentSegment = 
                            (org.opencrx.kernel.document1.jmi1.Segment)pm.getObjectById(
                                new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/" + providerName + "/segment/" + segmentName)
                            );
                        List<org.opencrx.security.realm1.jmi1.PrincipalGroup> principalGroups = 
                            new ArrayList<org.opencrx.security.realm1.jmi1.PrincipalGroup>();
                        if((groups == null) || (groups.getStringValue().length() == 0)) {
                            org.opencrx.security.realm1.jmi1.PrincipalGroup group =
                                (org.opencrx.security.realm1.jmi1.PrincipalGroup)pm.getObjectById(
                                    new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + providerName + "/segment/Root/realm/" + segmentName + "/principal/" + SecurityKeys.USER_GROUP_USERS)
                                );                            
                            principalGroups.add(group);
                            group =
                                (org.opencrx.security.realm1.jmi1.PrincipalGroup)pm.getObjectById(
                                    new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + providerName + "/segment/Root/realm/" + segmentName + "/principal/" + SecurityKeys.USER_GROUP_ADMINISTRATORS)
                                );                            
                            principalGroups.add(group);
                        }
                        else {
                            StringTokenizer tokenizer = new StringTokenizer(groups.getStringValue(), ",; ", false);
                            while(tokenizer.hasMoreTokens()) {
                                String groupName = tokenizer.nextToken();
                                org.opencrx.security.realm1.jmi1.PrincipalGroup group = null;
                                try {
                                    group = (org.opencrx.security.realm1.jmi1.PrincipalGroup)pm.getObjectById(
                                        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + providerName + "/segment/Root/realm/" + segmentName + "/principal/" + groupName)
                                    );
                                } catch(Exception e) {}
                                if(group != null) {
                                    principalGroups.add(group);
                                }                                
                            }
                        }
                        this.scanDocuments(
                            dir,
                            dir, 
                            modifiedSinceFileFilter,
                            documentSegment,
                            urlPrefix == null ? "http://localhost" : urlPrefix.getStringValue(),
                            upload == null ? false : Boolean.valueOf(upload.getStringValue()),
                            principalGroups,
                            pm
                        );                    
                    }
                }
            }
            try {
                pm.close();
            } catch(Exception e) {}
        }
        catch(Exception e) {
            new ServiceException(e).log();
            System.out.println(new Date() + ": " + WORKFLOW_NAME + " " + providerName + "/" + segmentName + ": exception occured " + e.getMessage() + ". Continuing");
        }        
    }
    
    //-----------------------------------------------------------------------
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        if(System.currentTimeMillis() > this.startedAt + STARTUP_DELAY) {
            String segmentName = req.getParameter("segment");
            String providerName = req.getParameter("provider");
            String id = providerName + "/" + segmentName;
            if(
                COMMAND_EXECUTE.equals(req.getPathInfo()) &&
                !this.runningSegments.contains(id)
            ) {
                try {
                    this.runningSegments.add(id);
                    this.scanDocuments(
                        id,
                        providerName,
                        segmentName,
                        req,
                        res
                    );
                } catch(Exception e) {
                    new ServiceException(e).log();
                }
                finally {
                    this.runningSegments.remove(id);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    protected void doPost(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.flushBuffer();
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 4441731357561757549L;

    private static final String COMMAND_EXECUTE = "/execute";
    private static final String WORKFLOW_NAME = "DocumentScanner";
    private static final String COMPONENT_CONFIGURATION_ID = "DocumentScanner"; 
    private static final String OPTION_SCAN_DIR = "scanDir";
    private static final String OPTION_URL_PREFIX = "urlPrefix";
    private static final String OPTION_UPLOAD = "upload";
    private static final String OPTION_GROUPS = "groups";
    private static final long STARTUP_DELAY = 180000L;
    
    private final UUIDGenerator uuids = UUIDs.getGenerator();
    private PersistenceManagerFactory persistenceManagerFactory = null;
    private final List<String> runningSegments = new ArrayList<String>();
    private long startedAt = System.currentTimeMillis();
        
}

//--- End of File -----------------------------------------------------------
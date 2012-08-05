/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Base.java,v 1.22 2009/03/02 11:47:36 wfro Exp $
 * Description: Base
 * Revision:    $Revision: 1.22 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/02 11:47:36 $
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.Orders;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.Quantors;

public class Base {

    //-----------------------------------------------------------------------
    public Base(
        Backend backend
    ) {
        this.backend = backend;
    }
    
    //-------------------------------------------------------------------------
    public void sendAlert(
        Path userHomeSender,
        DataproviderObject params
    ) throws ServiceException {
        String toUsers = params.values("toUsers").size() == 0
            ? ""
            : (String)params.values("toUsers").get(0);
        String name = (String)params.values("name").get(0);
        String description = (String)params.values("description").get(0);
        int importance = params.values("importance").isEmpty()
            ? 0
            : ((Number)params.values("importance").get(0)).intValue();
        int resendDelayInSeconds = params.values("resendDelayInSeconds").isEmpty()
            ? 0
            : ((Number)params.values("resendDelayInSeconds").get(0)).intValue();
        Path reference = (Path)params.values("reference").get(0);
        this.sendAlert(
            userHomeSender, 
            toUsers, 
            name,
            description,
            importance,
            resendDelayInSeconds,
            reference
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * Get additional description of given object.
     */
    public DataproviderObject_1_0 getAdditionalDescription(
        Path forObject,
        int language
    ) {
        try {
            List additionalDescriptions = this.backend.getDelegatingRequests().addFindRequest(
                forObject.getChild("additionalDescription"),
                new FilterProperty[]{
                    new FilterProperty(
                        Quantors.THERE_EXISTS,
                        "language",
                        FilterOperators.IS_IN,
                        new Integer(language)
                    )
                },
                AttributeSelectors.ALL_ATTRIBUTES,
                null,
                0,
                Integer.MAX_VALUE,
                Orders.ASCENDING
            );
            DataproviderObject_1_0 additionalDescription = null;
            for(Iterator i = additionalDescriptions.iterator(); i.hasNext(); ) {
                additionalDescription = (DataproviderObject_1_0)i.next();
                AppLog.trace("additionalDescription", additionalDescription);
            }
            return additionalDescription;
        }
        catch(ServiceException e) {
            e.log();
            return null;
        }
    }

    //-------------------------------------------------------------------------
    public void sendAlert(
        Path userHomeSender,
        String toUsers,        
        String name,
        String description,
        int importance,
        Integer resendDelayInSeconds,
        Path reference
    ) throws ServiceException {
        StringTokenizer tokenizer = new StringTokenizer(
            toUsers == null ? 
                "" : 
                toUsers, ";, "
        );
        while(tokenizer.hasMoreTokens()) {
            String toUser = tokenizer.nextToken();
            DataproviderObject_1_0 userHome = this.backend.getUserHomes().getUserHome(
                toUser,
                userHomeSender
            );
            if(userHome != null) {
                Path alertReference = reference == null 
                    ? userHomeSender 
                    : reference;
                
                // Only create alert if there is not already an alert with the 
                // same alert reference and created within the delay period.
                RequestCollection delegation = this.backend.getRunAsRootDelegation();
                List<DataproviderObject_1_0> alerts = delegation.addFindRequest(
                    userHome.path().getChild("alert"), 
                    new FilterProperty[]{
                        new FilterProperty(
                            Quantors.THERE_EXISTS,                            
                            "reference", 
                            FilterOperators.IS_IN,
                            alertReference
                        ),
                        new FilterProperty(
                            Quantors.THERE_EXISTS,                            
                            SystemAttributes.CREATED_AT, 
                            FilterOperators.IS_GREATER_OR_EQUAL,
                            org.openmdx.base.text.format.DateFormat.getInstance().format(new Date(System.currentTimeMillis() - 1000*(resendDelayInSeconds == null ? 0 : resendDelayInSeconds.intValue())))
                        )
                    }
                );
                if(alerts.isEmpty()) {
                    DataproviderObject alert = new DataproviderObject(
                        userHome.path().getDescendant(
                            new String[]{
                                "alert", 
                                this.backend.getUidAsString()
                            }
                        )
                    );
                    alert.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:home1:Alert");
                    alert.values("alertState").add(new Short((short)1));                    
                    alert.values("name").add(
                        name == null || name.length() == 0 ?
                            "--" : // name is mandatory
                            name
                    );
                    if(description != null) {
                        alert.values("description").add(description);
                    }
                    alert.values("importance").add(
                        new Integer(importance)
                    );
                    alert.values("reference").add(
                        alertReference
                    );
                    alert.clearValues("owningGroup").addAll(
                        userHome.values("owningGroup")
                    );
                    try {
                        this.backend.getRunAsRootDelegation().addCreateRequest(
                            alert
                        );
                    }
                    catch(ServiceException e) {
                        AppLog.warning("can not create alert", alert);
                        e.log();
                    }
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void assignToMe(
        Path targetIdentity,
        boolean overwrite,
        Set attributeFilter
    ) throws ServiceException {
        this.assignToMe(
            this.backend.retrieveObjectForModification(
                targetIdentity
            ),
            this.backend.retrieveObject(
                targetIdentity
            ),
            overwrite,
            attributeFilter
        );
    }
    
    //-------------------------------------------------------------------------
    public List completePictured(
        DataproviderObject_1_0 object,
        Set fetchSet
    ) throws ServiceException {
        List additionalFetchedObjects = new ArrayList();
        if(
            this.backend.isAccount(object) ||
            (fetchSet == null) ||
            fetchSet.contains("pictureContent") ||
            fetchSet.contains("pictureContentName") ||
            fetchSet.contains("pictureContentMimeType")
        ) {
            object.values("pictureContent");
            object.values("pictureContentName");
            object.values("pictureContentMimeType");
            if(
                (object.getValues("picture") != null) &&
                !object.values("picture").isEmpty()
            ) {
                try {
                    DataproviderObject_1_0 media = this.backend.retrieveObject(
                        (Path)object.values("picture").get(0)
                    );
                    if(
                        (media.getValues("content") != null) && 
                        !media.values("content").isEmpty()
                    ) {
                        // map media type <<stream>> binary to binary
                        InputStream is = (InputStream)media.values("content").get(0);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        int b = 0;
                        int length = 0;
                        while((b = is.read()) != -1) {
                            os.write(b);
                            length++;
                        }
                        if(length > 0) {
                            object.values("pictureContent").add(
                                os.toByteArray()                  
                            );
                            object.values("pictureContentName").addAll(
                                media.values("contentName")
                            );
                            object.values("pictureContentMimeType").addAll(
                                media.values("contentMimeType")
                            );
                            additionalFetchedObjects.add(media);
                        }                
                    }
                }
                catch(Exception e) {
                    ServiceException e0 = new ServiceException(e);
                    AppLog.detail("can not get picture.", e.getMessage());
                    AppLog.info(e0.getMessage(), e0.getCause());
                }
            }
        }
        return additionalFetchedObjects;
    }

    //-------------------------------------------------------------------------
    public List completePropertySetEntry(
        DataproviderObject_1_0 object,
        Set fetchSet
    ) {
        List additionalFetchedObjects = Collections.EMPTY_LIST;
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        if("org:opencrx:kernel:generic:StringPropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("stringValue").isEmpty()
                    ? ""
                    : object.values("stringValue").get(0)
            );
        }
        else if("org:opencrx:kernel:generic:IntegerPropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("integerValue").isEmpty()
                    ? ""
                    : object.values("integerValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:BooleanPropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("booleanValue").isEmpty()
                    ? ""
                    : object.values("booleanValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:UriPropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("uriValue").isEmpty()
                    ? ""
                    : object.values("uriValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:DecimalPropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("decimalValue").isEmpty()
                    ? ""
                    : object.values("decimalValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:DatePropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("dateValue").isEmpty()
                    ? ""
                    : object.values("dateValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:DateTimePropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("dateTimeValue").isEmpty()
                    ? ""
                    : object.values("dateTimeValue").get(0).toString()
            );
        }
        else if("org:opencrx:kernel:generic:ReferencePropertySetEntry".equals(objectClass)) {
            object.clearValues("stringifiedValue").add(
                object.values("referenceValue").isEmpty()
                    ? ""
                    : ((Path)object.values("referenceValue").get(0)).toXri()
            );
        }
        return additionalFetchedObjects;
    }
    
    //-------------------------------------------------------------------------
    public void assignToMe(
        DataproviderObject target,
        DataproviderObject_1_0 targetOldValues,
        boolean overwrite,
        Set attributeFilter
    ) throws ServiceException {
        if(attributeFilter == null) {
            attributeFilter = new HashSet();
            attributeFilter.add("assignedTo");
            attributeFilter.add("salesRep");
            attributeFilter.add("ratedBy");
        }
        DataproviderObject_1_0 source = targetOldValues == null
        ? target
                : targetOldValues;
        String objectClass = (String)source.values(SystemAttributes.OBJECT_CLASS).get(0);
        ModelElement_1_0 classDef = this.backend.getModel().getElement(objectClass);
        Map attributeDefs = this.backend.getModel().getAttributeDefs(classDef, false, true);

        // Test whether class has feature assignedTo. If yes and obj has
        // not already set assignedTo assign current user as default value
        ServiceHeader header = this.backend.getServiceHeader();
        if(header.getPrincipalChain().size() > 0) {
            DataproviderObject_1_0 userHome = this.backend.getUserHomes().getUserHome(
                target.path()
            );
            if((userHome != null) && (userHome.values("contact").size() > 0)) {
                for(Iterator i = attributeFilter.iterator(); i.hasNext(); ) {
                    String attribute = (String)i.next();
                    if(
                        attributeDefs.keySet().contains(attribute) &&
                        (overwrite || (source.getValues(attribute) == null) || (source.getValues(attribute).size() == 0))
                    ) {
                        target.clearValues(attribute).add(
                            userHome.values("contact").get(0)
                        ); 
                    }
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void initCharts(
        DataproviderObject userHome,
        DataproviderObject_1_0 oldValues
    ) {
        String objectClass = (String)userHome.values(SystemAttributes.OBJECT_CLASS).get(0);
        // init favorite chart references in case when the user home is 
        // assigned to a contact.
        if(
            "org:opencrx:kernel:home1:UserHome".equals(objectClass) &&
            ((oldValues == null) || (oldValues.values("contact").size() == 0)) &&
            (userHome.values("contact").size() > 0)
        ) {
            // set chart default references
            userHome.clearValues("chart0").add(
                userHome.path().getDescendant(new String[]{"chart", "0"})
            );
            userHome.clearValues("chart1").add(
                userHome.path().getDescendant(new String[]{"chart", "2"})
            );
            userHome.clearValues("chart2").add(
                userHome.path().getDescendant(new String[]{"chart", "1"})
            );
            userHome.clearValues("chart3").add(
                userHome.path().getDescendant(new String[]{"chart", "3"})
            );
        }
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final short IMPORT_EXPORT_OK = 0;
    public static final short IMPORT_EXPORT_FORMAT_NOT_SUPPORTED = 1;
    public static final short IMPORT_EXPORT_ITEM_NOT_VALID = 2;
    public static final short IMPORT_EXPORT_MISSING_DATA = 3;
    
    public static final String MIME_TYPE_ZIP = "application/zip";
    
    protected final Backend backend;
        
}

//--- End of File -----------------------------------------------------------

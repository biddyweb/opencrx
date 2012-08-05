/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: Backend.java,v 1.23 2008/10/06 17:12:53 wfro Exp $
 * Description: Backend
 * Revision:    $Revision: 1.23 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/06 17:12:53 $
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencrx.kernel.generic.OpenCrxException;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_3;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.dataprovider.spi.Layer_1_0;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.model1.accessor.basic.cci.Model_1_3;

public class Backend {

    //-----------------------------------------------------------------------
    public Backend(
    ) {
        this.uuids = UUIDs.getGenerator();
    }

    //-----------------------------------------------------------------------
    public void open(
        BackendContext context
    ) {
        this.context = context;    
        this.models = null;
        this.admin = null;
        this.accounts = null;
        this.addresses = null;
        this.contracts = null;
        this.cloneable = null;
        this.depots = null;
        this.products = null;
        this.activities = null;
        this.userHomes = null;
        this.workflows = null;
        this.base = null;
        this.derivedReferences = null;        
    }

    //-----------------------------------------------------------------------
    public Codes getCodes(
    ) throws ServiceException {
        if(this.codes == null) {
            this.codes = new Codes(
                this.context.delegationAsRoot,
                this.context.codeSegmentIdentity
            );
        }
        return this.codes;
    }
        
    //-----------------------------------------------------------------------
    public Contracts getContracts(
    ) {
        if(this.contracts == null) {
            this.contracts = new Contracts(this);
        }
        return this.contracts;
    }
    
    //-----------------------------------------------------------------------
    public Cloneable getCloneable(
    ) {
        if(this.cloneable == null) {
            this.cloneable = new Cloneable(this);
        }
        return this.cloneable;
    }
    
    //-----------------------------------------------------------------------
    public Depots getDepots(
    ) {
        if(this.depots == null) {
            this.depots = new Depots(this);
        }
        return this.depots;
    }
    
    //-----------------------------------------------------------------------
    public Products getProducts(
    ) {
        if(this.products == null) {
            this.products = new Products(this);
        }
        return this.products;
    }
    
    //-----------------------------------------------------------------------
    public Activities getActivities(
    ) {
        if(this.activities == null) {
            this.activities = new Activities(this);
        }
        return this.activities;
    }
    
    //-----------------------------------------------------------------------
    public Admin getAdmin(
    ) {
        if(this.admin == null) {
            this.admin = new Admin(this);
        }
        return this.admin;
    }
    
    //-----------------------------------------------------------------------
    public Accounts getAccounts(
    ) {
        if(this.accounts == null) {            
            this.accounts = new Accounts(this);
        }
        return this.accounts;
    }
    
    //-----------------------------------------------------------------------
    public Addresses getAddresses(
    ) {
        if(this.addresses == null) {
            this.addresses = new Addresses(this);
        }
        return this.addresses;
    }
    
    //-----------------------------------------------------------------------
    public Models getModels(
    ) {
        if(this.models == null) {
            this.models = new Models(this);
        }
        return this.models;
    }
    
    //-----------------------------------------------------------------------
    public UserHomes getUserHomes(
    ) {
        if(this.userHomes == null) {
            this.userHomes = new UserHomes(this);
        }
        return this.userHomes;
    }
    
    //-----------------------------------------------------------------------
    public Workflows getWorkflows(
    ) throws ServiceException {
        if(this.workflows == null) {
            this.workflows = new Workflows(this);
        }
        return this.workflows;
    }

    //-----------------------------------------------------------------------
    public Base getBase(
    ) {
        if(this.base == null) {
            this.base = new Base(this);
        }
        return this.base;
    }

    //-----------------------------------------------------------------------
    public DerivedReferences getDerivedReferences(
    ) {
        if(this.derivedReferences == null) {
            this.derivedReferences = new DerivedReferences(
                this
            );
        }
        return this.derivedReferences;
    }

    //-------------------------------------------------------------------------
    /**
     * This method is invoked before removing an object. If this method throws
     * an exception the object will not be removed. 
     */
    public void notifyRemoveObject(
        Path objectIdentity
    ) throws ServiceException {
    }
    
    //-------------------------------------------------------------------------
    public boolean isContract(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:contract1:AbstractContract"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isLead(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:contract1:Lead"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isAccount(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:Account"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isUnspecifiedAccount(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:UnspecifiedAccount"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isContact(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:Contact"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isPhoneNumberAddressable(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:address1:PhoneNumberAddressable"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isAccountAddress(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:AccountAddress"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isAbstractGroup(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:account1:AbstractGroup"
        );
    }
    
    //-------------------------------------------------------------------------
    public boolean isPropertySetEntry(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:generic:PropertySetEntry"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isDepot(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:depot1:Depot"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isDepotHolder(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:depot1:DepotHolder"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isMedia(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:document1:Media"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isSecureObject(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:base:SecureObject"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isContractPosition(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:contract1:ContractPosition"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isPictured(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:generic:Pictured"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isSegment(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:openmdx:base:Segment"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isUserHome(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:home1:UserHome"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isActivityTracker(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:activity1:ActivityTracker"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isEffortEstimate(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:activity1:EffortEstimate"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isActivity(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:activity1:Activity"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isActivityGroup(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:activity1:ActivityGroup"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isModelElement(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:model1:Element"
        );
    }

    //-------------------------------------------------------------------------
    public boolean isActivityWorkRecord(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        return this.getModel().isSubtypeOf(
            objectClass,
            "org:opencrx:kernel:activity1:ActivityWorkRecord"
        );
    }
    
    //-----------------------------------------------------------------------
    public RequestCollection getDelegatingRequests(
    ) {
        return this.context.delegatingRequests;
    }
    
    //-----------------------------------------------------------------------
    public Layer_1_0 getDelegatingLayer(
    ) {
        return this.context.delegatingLayer;
    }
    
    //-----------------------------------------------------------------------
    public RequestCollection getLocalRequests(
    ) {
        return this.context.localRequests;
    }
    
    //-----------------------------------------------------------------------
    public RequestCollection getRunAsRootDelegation(
    ) {
        return this.context.delegationAsRoot;
    }
    
    //-----------------------------------------------------------------------
    public ServiceHeader getServiceHeader(
    ) {
        return this.context.header;
    }
    
    //-------------------------------------------------------------------------
    public Model_1_3 getModel(
    ) {
        return this.context.model;
    }
    
    //-------------------------------------------------------------------------
    public String getUidAsString(
    ) {
        return UUIDConversion.toUID(this.uuids.next());        
    }
    
    //-------------------------------------------------------------------------
    public RefPackage_1_3 getLocalPkg(
    ) {
        return this.context.localPkg;
    }
    
    //-------------------------------------------------------------------------
    public RefPackage_1_3 getDelegatingPkg(
    ) {
        return this.context.delegatingPkg;
    }
    
    //-------------------------------------------------------------------------
    public Path getRealmIdentity(
    ) {
        return this.context.realmIdentity;
    }
    
    //-------------------------------------------------------------------------
    public List getNewValue(
        String feature,
        DataproviderObject obj,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        if((obj != null) && (obj.getValues(feature) != null)) {
            return obj.values(feature);
        }
        else if(oldValues != null) {
            return oldValues.values(feature);
        }
        return new ArrayList();
    }
    
    //-------------------------------------------------------------------------
    public DataproviderObject_1_0 retrieveObjectFromDelegation(
        Path identity
    ) throws ServiceException {
        return this.getDelegatingRequests().addGetRequest(
            identity,
            AttributeSelectors.ALL_ATTRIBUTES,
            new AttributeSpecifier[]{}
        );
    }
    
    //-----------------------------------------------------------------------
    public Date parseDate(
        String date
    ) throws ServiceException {
        if(date == null) return null;
        try {
            return DateFormat.getInstance().parse(date);
        }
        catch(ParseException e) {
            throw new ServiceException(e);
        }
    }
        
    //-------------------------------------------------------------------------
    public DataproviderObject retrieveObjectForModification(
        Path identity
    ) throws ServiceException {
        DataproviderObject object = null;
        if(this.context.modifiedObjects.keySet().contains(identity)) {
            object = (DataproviderObject)this.context.modifiedObjects.get(identity);
        }
        else {            
            object =
                new DataproviderObject(
                    this.retrieveObjectFromDelegation(identity)
                );
            this.context.modifiedObjects.put(
                identity,
                object
            );
        }
        return object;
    }
    
    //-------------------------------------------------------------------------
    public DataproviderObject_1_0 retrieveObject(
        Path identity
    ) throws ServiceException {
        if(this.context.modifiedObjects.keySet().contains(identity)) {
            return (DataproviderObject)this.context.modifiedObjects.get(identity);
        }
        else if(!identity.get(0).startsWith("org:opencrx:kernel")) {
            return this.retrieveObjectFromDelegation(
                identity
            );
        }
        else {
            return this.context.delegatingLayer.get(
                this.getServiceHeader(),
                new DataproviderRequest(
                    new DataproviderObject(identity),
                    DataproviderOperations.OBJECT_RETRIEVAL,
                    AttributeSelectors.ALL_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                )
            ).getObject();
        }
    }

    //-------------------------------------------------------------------------
    public void replaceObject(
        ServiceHeader header,
        DataproviderObject object
    ) throws ServiceException {
        if(!USE_OPTIMIZED_REPLACE || !object.path().get(0).startsWith("org:opencrx:kernel")) {
            this.getDelegatingRequests().addReplaceRequest(
                object
            );
        }
        else {
            // WARNING: here we skip the prolog of the SystemAttributes_1 plugin 
            this.context.delegatingLayer.replace(
                header,
                new DataproviderRequest(
                    object,
                    DataproviderOperations.OBJECT_REPLACEMENT,
                    AttributeSelectors.NO_ATTRIBUTES,
                    new AttributeSpecifier[]{}
                )
            );
        }
    }

    //-------------------------------------------------------------------------
    public void createObject(
        DataproviderObject object
    ) throws ServiceException {
        try {
            this.getDelegatingRequests().addCreateRequest(
                object
            );
        }
        catch(ServiceException e) {
            if(e.getExceptionCode() != BasicException.Code.DUPLICATE) {
                throw e;
            }
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.DUPLICATE_OBJECT,
                new BasicException.Parameter[]{
                        new BasicException.Parameter("param0", object.path().getBase())
                },
                "Duplicate object. Object with qualifier already exists."
            );
        }
    }
    
    //-------------------------------------------------------------------------
    public void removeObject(
        Path identity
    ) throws ServiceException {
        this.notifyRemoveObject(
            identity
        );
        this.getDelegatingRequests().addRemoveRequest(
            identity
        );
        this.context.modifiedObjects.remove(identity);
        for(
            Iterator i = this.context.pendingModifications.iterator();
            i.hasNext();
        ) {
            DataproviderObject object = (DataproviderObject)i.next();
            if(object.path().equals(identity)) {
                i.remove();
                break;
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void removeAll(
        Path forReference,
        FilterProperty[] includeFilter,
        int excludeFirst,
        int limit,
        Set exclude
    ) {
        try {
            // Order descending by modification date. This way the 
            // excludeFirst least recently modified objects are not removed.
            List objects = this.getDelegatingRequests().addFindRequest(
                forReference,  
                includeFilter,
                AttributeSelectors.NO_ATTRIBUTES,
                new AttributeSpecifier[]{
                    new AttributeSpecifier(
                        SystemAttributes.MODIFIED_AT, 
                        0, 
                        Integer.MAX_VALUE, 
                        Directions.DESCENDING
                    )
                },
                0, Integer.MAX_VALUE,
                Directions.ASCENDING
            );
            // get list of objects to be removed
            List identities = new ArrayList();
            int ii = 0;
            for(
                Iterator i = objects.iterator(); 
                i.hasNext() && (identities.size() < limit);
                ii++
            ) {
                Path identity = ((DataproviderObject_1_0)i.next()).path();
                if((ii >= excludeFirst) && ((exclude == null) || !exclude.contains(identity))) {
                    identities.add(identity);
                }
            }
            // remove objects
            for(
                Iterator i = identities.iterator(); 
                i.hasNext(); 
            ) {
                this.getDelegatingRequests().addRemoveRequest((Path)i.next());
            }
        }
        catch(ServiceException e) {
            SysLog.info("can not remove objects");
            SysLog.info(e.getMessage(), e.getCause());
        }
    }

    //-------------------------------------------------------------------------
    public void flushObjectModifications(
        ServiceHeader header
    ) throws ServiceException {
        // loop until pre-store does not make dirty any new objects
        int count = 0;
        while(!this.context.modifiedObjects.isEmpty()) {
            Map<Path,DataproviderObject> modifiedObjects = new HashMap<Path,DataproviderObject>(this.context.modifiedObjects);
            for(
                Iterator<DataproviderObject> i = modifiedObjects.values().iterator(); 
                i.hasNext(); 
            ) {
                DataproviderObject object = i.next();
                DataproviderObject_1_0 existingObject = this.retrieveObject(
                    object.path()
                );
                AppLog.trace("Before preStore", object);
                this.preStore(
                    header,
                    object,
                    existingObject
                );
                this.verifyObject(
                    header,
                    object
                );
                try {
                    AppLog.trace("Replace object", object);
                    this.replaceObject(
                        header, 
                        object
                    );
                }
                catch(ServiceException e) {
                    // remap MEDIA_ACCESS_FAILURES as OpenCrx exception
                    if(e.getExceptionCode() == BasicException.Code.MEDIA_ACCESS_FAILURE) {
                        throw new ServiceException(
                            e,
                            OpenCrxException.DOMAIN,
                            OpenCrxException.MEDIA_ACCESS_FAILURE,
                            new BasicException.Parameter[]{
                                new BasicException.Parameter("param0", DateFormat.getInstance().format(e.getCause().getTimestamp()))        
                            },
                            e.getCause().getDescription()
                        );
                    }
                    else {
                        throw e;
                    }
                }
                // Remove modified object from list of modified objects
                // Remaining pre-stores may modify the same object again and therefore
                // it will be added to the list of modified objects again. It will be
                // processed by the next update iteration
                this.context.modifiedObjects.remove(
                    object.path()
                );
            }
            // Packages may contain cached objects. Init packages so 
            // that all stored objects are re-read
            this.context.delegatingPkg.clear();
            this.context.localPkg.clear();
            count++;
            if(count > 5) {
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.ASSERTION_FAILURE,
                    new BasicException.Parameter[]{},
                    "maximum limit of pre-store count exceeded. Possible reason: update recursion in preStore()"
                );                
            }
        }        
        this.context.modifiedObjects.clear();
    }
    
    //-------------------------------------------------------------------------
    public void processPendingModifications(
        ServiceHeader header
    ) throws ServiceException {
        for(
            Iterator<DataproviderObject> i = this.context.pendingModifications.iterator();
            i.hasNext();
        ) {
            DataproviderObject modification = i.next();
            this.testObjectIsChangeable(modification);            
            DataproviderObject modifiedObject = this.retrieveObjectForModification(
                modification.path()
            );
            modifiedObject.addClones(
                modification,
                true
            );
        }
        this.context.pendingModifications.clear();
    }
    
    //-------------------------------------------------------------------------
    public void testObjectIsChangeable(
        DataproviderObject_1_0 object
    ) throws ServiceException {
        String objectClass = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);
        for(
            Iterator i = this.context.readOnlyTypes.iterator();
            i.hasNext();
        ) {           
            String type = (String)i.next();
            if(this.getModel().isSubtypeOf(objectClass, type)) {
                for(
                    Iterator j = object.attributeNames().iterator();
                    j.hasNext();
                ) {
                    String attributeName = (String)j.next();
                    if(
                        !SystemAttributes.OBJECT_CLASS.equals(attributeName) &&
                        !SystemAttributes.MODIFIED_AT.equals(attributeName) &&
                        !SystemAttributes.MODIFIED_BY.equals(attributeName)                        
                    ) {
                        throw new ServiceException(
                            OpenCrxException.DOMAIN,
                            OpenCrxException.OBJECT_TYPE_IS_READONLY,
                            new BasicException.Parameter[]{
                                new BasicException.Parameter("param0", objectClass)
                            },
                            "Object type is readonly. Can not modify object."
                        );
                    }
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void preStore(
        ServiceHeader header,
        DataproviderObject obj,
        DataproviderObject_1_0 oldValues
    ) throws ServiceException {
        this.getAddresses().updateAddress(
            obj,
            oldValues
        );
        this.getAccounts().updateAccount(
            obj, 
            oldValues
        );
        this.getBase().initCharts(
            obj,
            oldValues
        );
        // updateContractPosition makes contract dirty which adds the
        // contract to the list of modified objects. updateContract()
        // will be called by flushObjectModifications() in a second pass
        this.getContracts().updateContractPosition(
            obj,
            oldValues
        );
        this.getContracts().updateContract(
            header,
            obj,
            oldValues
        );
        this.getUserHomes().encodeEMailAccountPassword(
            obj,
            oldValues
        );
        this.getActivities().updateWorkRecord(
            obj,
            oldValues
        );
        this.getActivities().updateActivity(
            obj,
            oldValues
        );
        this.getModels().updateModelElement(
            obj,
            oldValues
        );

        // create only
        if(oldValues == null) {
            this.getBase().assignToMe(
                obj,
                null,
                false,
                null
            );
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * This method is invoked before storing the object. The object can be verified 
     * for consistency. A user defined ServiceException must be thrown in case the 
     * object must not be stored.
     */
    public void verifyObject(
        ServiceHeader header,
        DataproviderObject object
    ) throws ServiceException {
        String objectClass  = (String)object.values(SystemAttributes.OBJECT_CLASS).get(0);

        // this is a sample validation. user-defined validations can be added
        // by overriding verifyObject()
        if("org:opencrx:kernel:account1:Contact".equals(objectClass)) {
            List lastName = object.getValues("lastName");
            if(
                ((lastName != null) && (lastName.size() > 0)) &&
                ("##demo for user defined error##".equals(lastName.get(0)))
            ) {
                // the text for exception code OpenCrxException.ASSERTION_FAILURE
                // must be defined in texts.properties
                throw new ServiceException(
                    OpenCrxException.DOMAIN,
                    OpenCrxException.ASSERTION_FAILURE, 
                    new BasicException.Parameter[]{
                            new BasicException.Parameter("contact", object),
                            new BasicException.Parameter("param0", "lastName"),
                            new BasicException.Parameter("param1", lastName)
                    },
                    "A contact's last name must not be '##demo for user defined error##'"
                );
            }
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * Counts the number of occurences of items in report and returns a string
     * of the form item0: n0, item1: n1, etc.
     */
    public String analyseReport(
        List report
    ) {
        Map reportAsMap = new HashMap();
        for(int i = 0; i < report.size(); i++) {
            Object key = report.get(i);
            Short count = (Short)reportAsMap.get(key);
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

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final static boolean USE_OPTIMIZED_REPLACE = false;
    
    private final UUIDGenerator uuids;
   
    private Codes codes = null;

    // Reset by open()
    public BackendContext context = null;
    protected Models models = null;
    protected Admin admin = null;
    protected Accounts accounts = null;
    protected Addresses addresses = null;
    protected Contracts contracts = null;
    protected Cloneable cloneable = null;
    protected Depots depots = null;
    protected Products products = null;
    protected Activities activities = null;
    protected UserHomes userHomes = null;
    protected Workflows workflows = null;
    protected Base base = null;
    protected DerivedReferences derivedReferences = null;
    
}

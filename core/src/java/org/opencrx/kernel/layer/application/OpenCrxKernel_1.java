/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: OpenCrxKernel_1.java,v 1.287 2009/03/04 19:17:30 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.287 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/03/04 19:17:30 $
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

package org.opencrx.kernel.layer.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;

import org.opencrx.kernel.backend.Backend;
import org.opencrx.kernel.backend.BackendContext;
import org.opencrx.kernel.generic.OpenCrxException;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.plugin.application.account1.ContactImpl;
import org.opencrx.kernel.plugin.application.activity1.ActivityImpl;
import org.opencrx.kernel.plugin.application.base.AlertSenderImpl;
import org.opencrx.kernel.plugin.application.contract1.AbstractContractImpl;
import org.opencrx.kernel.plugin.application.depot1.DepotImpl;
import org.opencrx.kernel.plugin.application.home1.UserHomeImpl;
import org.opencrx.kernel.plugin.application.product1.ProductImpl;
import org.openmdx.application.cci.SystemAttributes;
import org.openmdx.application.configuration.Configuration;
import org.openmdx.application.dataprovider.accessor.Connection_1;
import org.openmdx.application.dataprovider.cci.AttributeSelectors;
import org.openmdx.application.dataprovider.cci.DataproviderObject;
import org.openmdx.application.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.application.dataprovider.cci.DataproviderOperations;
import org.openmdx.application.dataprovider.cci.DataproviderReply;
import org.openmdx.application.dataprovider.cci.DataproviderRequest;
import org.openmdx.application.dataprovider.cci.QualityOfService;
import org.openmdx.application.dataprovider.cci.RequestCollection;
import org.openmdx.application.dataprovider.cci.ServiceHeader;
import org.openmdx.application.dataprovider.cci.SharedConfigurationEntries;
import org.openmdx.application.dataprovider.spi.Layer_1_0;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.cci.PersistenceManager_1_0;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_2;
import org.openmdx.base.accessor.jmi.spi.RefObjectFactory_1;
import org.openmdx.base.accessor.jmi.spi.RefRootPackage_1;
import org.openmdx.base.accessor.view.Manager_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.compatibility.base.dataprovider.transport.dispatching.OperationAwarePlugin_1;
import org.openmdx.kernel.exception.BasicException;

/**
 * openCRX application plugin. This plugin implements the openCRX 
 * application logic. It is implemented as openMDX compatibility plugin. It 
 * will be migrated to a JMI plugin in version 2.x of openCRX. This will make
 * the plugin more object-oriented and more extensible.
 * 
 * However, it can be extended as follows by user-defined application logic:
 * <ul>
 *   <li>Object validation: override the method validateObject().
 *   <li>Workflow: provide a class implementing the interface Workflow_1_0.
 *       For sample workflows @see org.opencrx.kernel.worklow. The workflow
 *       class must be registered as WfProcess object using the GUI.
 *   <li>Derived attributes: the computation of derived attributes can be
 *       added by extending completeObject().
 * </ul>
 */

public class OpenCrxKernel_1
    extends OperationAwarePlugin_1 {

    //-------------------------------------------------------------------------
    public void activate(
        short id, 
        Configuration configuration,
        Layer_1_0 delegation
    ) throws ServiceException {        
        super.activate(
            id,
            configuration,
            delegation
        );
        try {
            // Realm identity
            if(!configuration.values(ConfigurationKeys.REALM_IDENTITY).isEmpty()) {
                this.loginRealmIdentity = new Path((String)configuration.values(ConfigurationKeys.REALM_IDENTITY).get(0));
            }
            else {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.INVALID_CONFIGURATION, 
                    "A realm identity must be configured with option 'realmIdentity'"
                );
            } 
    
            // Password encoding algorithm
            this.passwordEncodingAlgorithm = "MD5";
            if(configuration.values(ConfigurationKeys.PASSWORD_ENCODING_ALGORITHM).size() > 0) {
                this.passwordEncodingAlgorithm = (String)configuration.values(ConfigurationKeys.PASSWORD_ENCODING_ALGORITHM).get(0);
            }
    
            // Lookup code segment
            List exposedPaths = configuration.values(SharedConfigurationEntries.EXPOSED_PATH);
            this.codeSegment = null;
            for(int i = 0; i < exposedPaths.size(); i++) {
                Path exposedPath = (Path)exposedPaths.get(i);
                if(
                    "org:opencrx:kernel:code1".equals(exposedPath.get(0)) &&
                    (exposedPath.size() == 5)
                ) {
                    this.codeSegment = exposedPath;
                    break;
                }
            }
            if(this.codeSegment == null) {
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.INVALID_CONFIGURATION,
                    "provider must expose a code segment, i.e. a path of the form xri:@openmdx:org.opencrx.kernel.code1/provider/<id>/segment/<id>",
                    new BasicException.Parameter("exposed paths",  exposedPaths)
                );
            }
    
            // Readonly object types
            this.readOnlyTypes = configuration.values("readOnlyObjectType");
            this.datatypes = DatatypeFactory.newInstance();
            this.backend = this.getBackend();
        }
        catch(Exception e) {
            throw new ServiceException(e);
        }        
    }

    //-------------------------------------------------------------------------
    @Override
    protected Set getDirectAccessPaths(
    ) throws ServiceException {
        Set paths = super.getDirectAccessPaths();
        return paths;
    }
    
    //---------------------------------------------------------------------------
    @Override
    protected Map getPackageImpls(
    ) {
        // Get configured package impls
        Map packageImpls = new HashMap();
        if(super.getPackageImpls() != null) {
            packageImpls.putAll(super.getPackageImpls());
        }
        packageImpls.put(
            "org:opencrx:kernel:base",             
            AlertSenderImpl.class.getPackage().getName() // any impl from base package
        );
        packageImpls.put(
            "org:opencrx:kernel:account1",             
            ContactImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:activity1",             
            ActivityImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:admin1",             
            org.opencrx.kernel.plugin.application.admin1.SegmentImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:contract1",             
            AbstractContractImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:depot1",             
            DepotImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:home1",             
            UserHomeImpl.class.getPackage().getName()
        );
        packageImpls.put(
            "org:opencrx:kernel:product1",             
            ProductImpl.class.getPackage().getName()
        );
        return packageImpls;
    }

    //---------------------------------------------------------------------------
    protected PersistenceManager_1_0 getObjectFactory(
    ) throws ServiceException {
        ServiceHeader header = this.header;
        // Local manager
        // Header with indicator for local delegations. Used in #prolog and #epilog
        ServiceHeader localHeader = new ServiceHeader(
            header.getPrincipalChain().toArray(new String[header.getPrincipalChain().size()]),
            this.localCorrelationId = this.uidAsString(),
            false,
            header.getQualityOfService(),
            header.getRequestedAt(),
            header.getRequestedFor()                
        );        
        Connection_1 localConnection = new Connection_1(
            new RequestCollection(
                localHeader,
                this
            ),
            false, // transactionPolicyIsNew
            true // containerManagedUnitOfWork
        );
        PersistenceManager_1_0 localObjectFactory = new Manager_1(localConnection);
        this.localPkg = new RefRootPackage_1(
            localObjectFactory,
            null, // impls
            this.backend // OpenCrxKernel_1 plugin as context
        );
        // Delegating manager
        this.delegatingPkg = new RefRootPackage_1(
            this.getManager(),
            this.getPackageImpls(),
            this.backend // OpenCrxKernel_1 plugin as context
        );
        return new RefObjectFactory_1(
            (RefRootPackage_1)this.delegatingPkg
        );
    }

    //-------------------------------------------------------------------------
    protected BackendContext getBackendContext(
        ServiceHeader header,
        List pendingModifications
    ) throws ServiceException {
        return new BackendContext(
            this.getModel(),
            header,
            pendingModifications,
            this.delegation,
            this.delegatingPkg,
            this.getDelegation(),
            this.getRunAsRootDelegation(),
            this.localPkg,
            new RequestCollection(
                header,
                this
            ),
            this.router,
            this.passwordEncodingAlgorithm,
            this.codeSegment,
            this.loginRealmIdentity,
            this.readOnlyTypes
        );
    }
    
    //-------------------------------------------------------------------------
    /**
     * Implement a user-defined backend as follows:
     * <ul>
     *   <li>Create a backend class which extends the default backend class <code>Backend</code>
     *   <li>Create a plugin class which extends this plugin and override the method <code>getBackend</code>
     * </ul>
     */
    protected Backend getBackend(
    ) throws ServiceException {
        return new Backend();
    }
    
    //-------------------------------------------------------------------------
    public String getUidAsString(
    ) {
        return super.uidAsString();
    }

    //-------------------------------------------------------------------------
    public void testReferenceIsChangeable(
        Path referencePath
    ) throws ServiceException {
        // Reference must be changeable
        ModelElement_1_0 reference = null;
        try {
            reference = this.getModel().getReferenceType(referencePath);
        }
        catch(ServiceException e) {
            AppLog.warning("Reference not found in model", referencePath);
        }           
        if(
            (reference != null) &&
            !((Boolean)reference.objGetValue("isChangeable")).booleanValue()
        ) {
            throw new ServiceException(
                OpenCrxException.DOMAIN,
                OpenCrxException.REFERENCE_IS_READONLY,
                "Reference is readonly. Can not add/remove objects.",
                new BasicException.Parameter("param0", referencePath)
            );                                                                
        }
    }

    //-------------------------------------------------------------------------
    protected DataproviderObject createResult(
        Path replyPath,
        String structName
    ) {
        DataproviderObject result = new DataproviderObject(replyPath);
        result.clearValues(SystemAttributes.OBJECT_CLASS).add(
            structName
        );
        return result;
    }

    //-------------------------------------------------------------------------
    protected List completeObject(
        ServiceHeader header,
        Set fetchSet,
        DataproviderObject_1_0 object
    ) throws ServiceException {
        List additionalFetchedObjects = new ArrayList();
        // Pictures
        if(this.backend.isPictured(object)) {
            additionalFetchedObjects.addAll(
                this.backend.getBase().completePictured(
                    object, 
                    fetchSet
                )
            );
        }
        // User home
        if(this.backend.isUserHome(object)) {
            additionalFetchedObjects.addAll(
                this.backend.getUserHomes().completeUserHome(
                    object
                )
            );
        }
        // Contract position
        else if(this.backend.isContractPosition(object)) {
            this.backend.getContracts().calculateContractPosition(
                object,
                fetchSet
            );
        }
        // Contract
        else if(this.backend.isContract(object)) {
            this.backend.getContracts().completeContract(
                object, 
                fetchSet
            );
            this.backend.getContracts().calculateForwardReferences(
                object,
                fetchSet
            );
        }
        // Activity
        else if(this.backend.isActivity(object)) {
            additionalFetchedObjects.addAll(
                this.backend.getActivities().completeActivity(
                    object, 
                    fetchSet
                )
            );
        }        
        // Contact
        else if(this.backend.isContact(object)) {
            // Only get ouMembership if required
            object.attributeNames().remove("ouMembership");
            if((fetchSet == null) || fetchSet.contains("ouMembership")) {
                this.backend.getAccounts().completeOuMembership(object);
            }
        }
        // Media
        else if(this.backend.isMedia(object)) {
            // TODO: complete contentLength
        }
        // model1:Operation
        else if(this.backend.isModelElement(object)) {
            additionalFetchedObjects.addAll(
                this.backend.getModels().completeElement(
                    object
                )
            );
        }
        else if(this.backend.isPropertySetEntry(object)) {
            this.backend.getBase().completePropertySetEntry(
                object, 
                fetchSet
            );
        }
        return additionalFetchedObjects;
    }

    //-------------------------------------------------------------------------
    /**
     * This method is invoked after successful validation and before 
     * storing the object. If this method throws an exception the object will
     * not be stored.
     */
    protected void notifyPreStoreObject(
        ServiceHeader header,
        DataproviderObject object
    ) throws ServiceException {
    }

    //-------------------------------------------------------------------------
    private DataproviderReply completeReply(        
        ServiceHeader header,
        DataproviderRequest request,
        Set fetchSet,
        boolean fetchAdditionalObjects,
        DataproviderReply reply
    ) throws ServiceException {

        // Tuning
        // Get contract position. Get contract then all positions. The positions
        // are cached in delegatingPkg. So when calculateContractPosition() is called
        // several times the positions are not re-read
        if(request.path().isLike(PATTERN_CONTRACT_POSITION.getParent())) {
            org.opencrx.kernel.contract1.jmi1.AbstractContract c =
                (org.opencrx.kernel.contract1.jmi1.AbstractContract)this.delegatingPkg.refObject(
                    request.path().getParent().toXri()
                );
            Collection positions = null;
            if(c instanceof org.opencrx.kernel.contract1.jmi1.Opportunity) {
                positions = ((org.opencrx.kernel.contract1.jmi1.Opportunity)c).getPosition();
            }
            else if(c instanceof org.opencrx.kernel.contract1.jmi1.Quote) {
                positions = ((org.opencrx.kernel.contract1.jmi1.Quote)c).getPosition();
            }
            else if(c instanceof org.opencrx.kernel.contract1.jmi1.SalesOrder) {
                positions = ((org.opencrx.kernel.contract1.jmi1.SalesOrder)c).getPosition();
            }
            else if(c instanceof org.opencrx.kernel.contract1.jmi1.Invoice) {
                positions = ((org.opencrx.kernel.contract1.jmi1.Invoice)c).getPosition();
            }
            // Get all positions in one roundtrip. Read up to the last element
            for(Iterator i = positions.iterator(); i.hasNext(); ) {
                i.next();
            }
        }

        List additionalFetchedObjects = new ArrayList();
        for(int i = 0; i < reply.getObjects().length; i++) {
            try {
                additionalFetchedObjects.addAll(
                    this.completeObject(
                        header,
                        fetchSet,
                        reply.getObjects()[i]
                    )
                );
            }
            // Ignore authorization failures. The incomplete object is returned.
            // A subsequent object retrieval will result in an AUTHORIZATION_FAILURE.
            // However, ignoring an AUTHORIZATION_FAILURE here does not break the whole 
            // (find) reply.
            catch(ServiceException e) {
                if(e.getExceptionCode() != BasicException.Code.AUTHORIZATION_FAILURE) {
                    throw e;
                }
            }
        }
        if(!fetchAdditionalObjects || additionalFetchedObjects.isEmpty()) {
            return reply;
        }
        else {
            List allObjects = new ArrayList(Arrays.asList(reply.getObjects()));
            allObjects.addAll(additionalFetchedObjects);
            return new DataproviderReply(allObjects);
        }
    }

    //-------------------------------------------------------------------------
    public RequestCollection getRunAsRootDelegation(
    ) {
        return new RequestCollection(
            new ServiceHeader(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + "Root", null, false, new QualityOfService()),
            this.router == null
                ? this.getDelegation()
                : this.router
        );
    }

    //-------------------------------------------------------------------------
    public void prolog(
        ServiceHeader header,
        DataproviderRequest[] requests
    ) throws ServiceException {
        if(
            (this.localCorrelationId == null) ||
            !localCorrelationId.equals(header.getCorrelationId())
        ) {
            super.prolog(
                header,
                requests
            );
            // Remember replacement requests as pending modifications
            // They are processed in epilog in processPendingModifications
            List pendingModifications = new ArrayList();
            for(
                int i = 0; 
                i < requests.length; 
                i++
            ) {
                if(requests[i].operation() == DataproviderOperations.OBJECT_REPLACEMENT) {
                    Path p = requests[i].path();
                    pendingModifications.add(
                        requests[i].object()                        
                    );
                }
            }
            this.backend.open(
                this.getBackendContext(
                    header,
                    pendingModifications
                )
            );
        }
    }

    //-------------------------------------------------------------------------
    public void epilog(
        ServiceHeader header,
        DataproviderRequest[] requests,
        DataproviderReply[] replies
    ) throws ServiceException {      
        if(
            (this.localCorrelationId == null) ||
            !localCorrelationId.equals(header.getCorrelationId())
        ) {
            AppLog.trace("Process pending modifications");
            this.backend.processPendingModifications(header);
            AppLog.trace("Flush object modifications");
            this.backend.flushObjectModifications(header);
            super.epilog(
                header,
                requests,
                replies
            );
        }
    }

    //-------------------------------------------------------------------------
    public DataproviderReply get(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderReply reply = this.backend.getDerivedReferences().getReply(header, request);
        if(reply == null) {
            reply = super.get(
                header,
                request
            );
        }
        return this.completeReply(
            header,
            request,
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES
                ? request.attributeSpecifierAsMap().keySet()
                : null,
            true,
            reply
        );
    }

    //-------------------------------------------------------------------------
    public DataproviderReply find(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        DataproviderReply reply = this.backend.getDerivedReferences().getReply(header, request);
        if(reply == null) {
            reply = super.find(
                header,
                request
            );            
        }
        return this.completeReply(
            header,
            request,
            request.attributeSelector() == AttributeSelectors.SPECIFIED_AND_TYPICAL_ATTRIBUTES
                ? request.attributeSpecifierAsMap().keySet()
                : null,
            false,
            reply
        );
    }

    //-------------------------------------------------------------------------
    public DataproviderReply create(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        this.testReferenceIsChangeable(
            request.path().getParent()
        );
        this.backend.preStore(
            header,
            request.object(),
            null
        );
        this.backend.verifyObject(
            header,
            request.object()
        );
        this.notifyPreStoreObject(
            header,
            request.object()
        );
        DataproviderReply reply = super.create(
            header,
            request
        );
        this.backend.retrieveObjectForModification(
            request.path()
        );
        return reply;
    }

    //-------------------------------------------------------------------------
    public DataproviderReply remove(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {        

        // remove PriceLevel
        if(request.path().isLike(PATTERN_PRODUCT_PRICE_LEVEL)) {
            this.backend.getProducts().removePriceLevel(
                request.path(),
                true
            );
            return new DataproviderReply(request.object());
        }
        // remove ActivityWorkRecord
        if(request.path().isLike(PATTERN_ACTIVITY_WORK_RECORD)) {
            this.backend.getActivities().removeWorkRecord(
                request.path()
            );
            return new DataproviderReply(request.object());
        }
        // remove ActivityGroup
        else if(
            request.path().isLike(PATTERN_ACTIVITY_TRACKER) ||
            request.path().isLike(PATTERN_ACTIVITY_CATEGORY) ||
            request.path().isLike(PATTERN_ACTIVITY_MILESTONE)
        ) {
            this.backend.getActivities().removeActivityGroup(
                request.path()
            );
            return new DataproviderReply(request.object());
        }
        // remove DepotEntity
        else if(request.path().isLike(PATTERN_DEPOT_ENTITY)) {
            this.backend.getDepots().removeDepotEntity(request.path());
            return new DataproviderReply(request.object());
        }
        // remove DepotContract
        else if(request.path().isLike(PATTERN_DEPOT_HOLDER)) {
            this.backend.getDepots().removeDepotContract(request.path());
            return new DataproviderReply(request.object());
        }
        // remove Depot
        else if(request.path().isLike(PATTERN_DEPOT_DEPOT)) {
            this.backend.getDepots().removeDepot(request.path());
            return new DataproviderReply(request.object());
        }
        // remove DepotPosition
        else if(request.path().isLike(PATTERN_DEPOT_POSITION)) {
            this.backend.getDepots().removeDepotPosition(request.path());
            return new DataproviderReply(request.object());
        }
        // SingleBooking
        else if(request.path().isLike(PATTERN_DEPOT_SINGLE_BOOKING)) {
            this.backend.getDepots().removeSingleBooking(request.path());
            return new DataproviderReply(request.object());
        }
        // SimpleBooking
        else if(request.path().isLike(PATTERN_DEPOT_SIMPLE_BOOKING)) {
            this.backend.getDepots().removeSimpleBooking(request.path());
            return new DataproviderReply(request.object());
        }
        // CompoundBooking
        else if(request.path().isLike(PATTERN_DEPOT_COMPOUND_BOOKING)) {
            this.backend.getDepots().removeCompoundBooking(request.path());
            return new DataproviderReply(request.object());
        }        
        // ContractPosition
        else if(request.path().isLike(PATTERN_CONTRACT_POSITION)) {
            this.backend.getContracts().removeContractPosition(
                header, 
                this.backend.retrieveObject(request.path()), 
                true
            );
            return new DataproviderReply(request.object());
        }    
        // other types
        else {
            this.testReferenceIsChangeable(
                request.path().getParent()
            );
            this.backend.notifyRemoveObject(
                request.path()
            );
            return super.remove(
                header,
                request
            );
        }
    }

    //-------------------------------------------------------------------------
    public DataproviderReply replace(
        ServiceHeader header,
        DataproviderRequest request
    ) throws ServiceException {
        Path p = request.path();
        if(p.isLike(PATTERN_CONTRACT_POSITION)) {
            this.backend.getContracts().replaceContractPosition(
                request
            );
        }
        else if(p.isLike(PATTERN_CODE_SEGMENT)) {
            this.backend.getCodes().markAsDirty();
        }
        return new DataproviderReply(
            new DataproviderObject(request.object())
        );
    }

    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    public static final String MAX_DATE = "99991231T000000.000Z"; 

    public static final Path PATTERN_ACTIVITY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*");
    public static final Path PATTERN_ACTIVITY_WORK_RECORD = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedResource/:*/workRecord/:*");

    // Patterns for derived remove/replace requests
    public static final Path PATTERN_DEPOT_ENTITY = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*");
    public static final Path PATTERN_DEPOT_HOLDER = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*");
    public static final Path PATTERN_DEPOT_DEPOT = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*");
    public static final Path PATTERN_DEPOT_POSITION = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*");
    public static final Path PATTERN_DEPOT_SINGLE_BOOKING = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*");
    public static final Path PATTERN_DEPOT_SIMPLE_BOOKING = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/simpleBooking/:*");
    public static final Path PATTERN_DEPOT_COMPOUND_BOOKING = new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*");
    public static final Path PATTERN_CONTRACT_POSITION = new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*");
    public static final Path PATTERN_CODE_SEGMENT = new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*");   
    public static final Path PATTERN_ACTIVITY_TRACKER = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*");
    public static final Path PATTERN_ACTIVITY_CATEGORY = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*");
    public static final Path PATTERN_ACTIVITY_MILESTONE = new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*");
    public static final Path PATTERN_PRODUCT_PRICE_LEVEL = new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*");

    // Plugin-level
    protected Path codeSegment = null;
    protected Path loginRealmIdentity = null;
    protected List readOnlyTypes = null;
    public DatatypeFactory datatypes = null;
    public String localCorrelationId = null;
    
    // Request-level
    public RefPackage_1_2 localPkg = null;
    public RefPackage_1_2 delegatingPkg = null; 

    // Backend
    public Backend backend = null;

    // Password encoding
    private String passwordEncodingAlgorithm = null;

}

//--- End of File -----------------------------------------------------------

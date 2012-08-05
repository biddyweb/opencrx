/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: TestJdo.java,v 1.10 2008/01/22 10:21:23 wfro Exp $
 * Description: TestJdo
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/01/22 10:21:23 $
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
package org.opencrx.kernel.layer.persistence.jdo;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.oasisopen.spi2.ObjectIdBuilder;

public class TestJdo
    extends TestCase {

    //-----------------------------------------------------------------------
    /**
     * Constructs a test case with the given name.
     */
    public TestJdo(String name) {
        super(name);
        if(pm == null) {
            Properties props = new Properties();
            props.put("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
            props.put("javax.jdo.option.Mapping","standard");
            props.put("org.jpox.autoCreateSchema", "false");
            props.put("org.jpox.autoCreateTables", "false");
            props.put("org.jpox.validateTables", "false");
            props.put("org.jpox.autoCreateColumns", "false");
            props.put("org.jpox.autoCreateConstraints", "false");
            props.put("org.jpox.validateConstraints", "false");
            props.put("org.jpox.identifier.case", "uppercase");
            props.put("org.jpox.autoStartMechanism", "Classes");
            StringBuilder classes = new StringBuilder();
            classes
            .append("org.opencrx.kernel.account1.jdo2.Account,")
            .append("org.opencrx.kernel.account1.jdo2.AccountAddress,")
            .append("org.opencrx.kernel.account1.jdo2.AccountCategoryFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AccountFilterGlobal,")
            .append("org.opencrx.kernel.account1.jdo2.AccountFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AccountQueryFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AccountTypeFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressCategoryFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressDisabledFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressFilterGlobal,")
            .append("org.opencrx.kernel.account1.jdo2.AddressFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressMainFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressQueryFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressTypeFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.AddressUsageFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.CategoryFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.Competitor,")
            .append("org.opencrx.kernel.account1.jdo2.Contact,")
            .append("org.opencrx.kernel.account1.jdo2.ContactMembership,")
            .append("org.opencrx.kernel.account1.jdo2.ContactRelationship,")
            .append("org.opencrx.kernel.account1.jdo2.CreditLimit,")
            .append("org.opencrx.kernel.account1.jdo2.DisabledFilterProperty,")
            .append("org.opencrx.kernel.account1.jdo2.EmailAddress,")
            .append("org.opencrx.kernel.account1.jdo2.EmploymentContract,")
            .append("org.opencrx.kernel.account1.jdo2.Group,")
            .append("org.opencrx.kernel.account1.jdo2.GroupMembership,")
            .append("org.opencrx.kernel.account1.jdo2.LegalEntity,")
            .append("org.opencrx.kernel.account1.jdo2.Member,")
            .append("org.opencrx.kernel.account1.jdo2.Organization,")
            .append("org.opencrx.kernel.account1.jdo2.OrganizationalUnit,")
            .append("org.opencrx.kernel.account1.jdo2.OrganizationalUnitRelationship,")
            .append("org.opencrx.kernel.account1.jdo2.PhoneNumber,")
            .append("org.opencrx.kernel.account1.jdo2.PostalAddress,")
            .append("org.opencrx.kernel.account1.jdo2.RevenueReport,")
            .append("org.opencrx.kernel.account1.jdo2.Room,")
            .append("org.opencrx.kernel.account1.jdo2.SearchIndexEntry,")
            .append("org.opencrx.kernel.account1.jdo2.Segment,")
            .append("org.opencrx.kernel.account1.jdo2.UnspecifiedAccount,")
            .append("org.opencrx.kernel.account1.jdo2.WebAddress,")
            .append("org.opencrx.kernel.activity1.jdo2.Absence,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractEmailRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractFaxRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractMailingRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractMmsRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractPhoneCallRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.AbstractSmsRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.Activity,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityCategory,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityCreationAction,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityCreator,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityFilterGlobal,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityFilterGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityFollowUp,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityGroupAssignment,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityGroupFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityLinkFrom,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityLinkTo,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityMilestone,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityNumberFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityProcess,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityProcessAction,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityProcessState,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityProcessStateFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityProcessTransition,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityQueryFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityStateFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityTracker,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityType,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityTypeFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityVote,")
            .append("org.opencrx.kernel.activity1.jdo2.ActivityWorkRecord,")
            .append("org.opencrx.kernel.activity1.jdo2.AddressGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.AddressGroupMember,")
            .append("org.opencrx.kernel.activity1.jdo2.Calendar,")
            .append("org.opencrx.kernel.activity1.jdo2.CalendarDay,")
            .append("org.opencrx.kernel.activity1.jdo2.DisabledFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.EffortEstimate,")
            .append("org.opencrx.kernel.activity1.jdo2.Email,")
            .append("org.opencrx.kernel.activity1.jdo2.EmailRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.EmailRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.ExternalActivity,")
            .append("org.opencrx.kernel.activity1.jdo2.Fax,")
            .append("org.opencrx.kernel.activity1.jdo2.FaxRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.FaxRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.Incident,")
            .append("org.opencrx.kernel.activity1.jdo2.IncidentParty,")
            .append("org.opencrx.kernel.activity1.jdo2.LinkedActivityFollowUpAction,")
            .append("org.opencrx.kernel.activity1.jdo2.Mailing,")
            .append("org.opencrx.kernel.activity1.jdo2.MailingRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.MailingRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.Meeting,")
            .append("org.opencrx.kernel.activity1.jdo2.MeetingParty,")
            .append("org.opencrx.kernel.activity1.jdo2.Mms,")
            .append("org.opencrx.kernel.activity1.jdo2.MmsRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.MmsRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.MmsSlide,")
            .append("org.opencrx.kernel.activity1.jdo2.PhoneCall,")
            .append("org.opencrx.kernel.activity1.jdo2.PhoneCallRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.PhoneCallRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.ProductReference,")
            .append("org.opencrx.kernel.activity1.jdo2.Resource,")
            .append("org.opencrx.kernel.activity1.jdo2.ResourceAssignment,")
            .append("org.opencrx.kernel.activity1.jdo2.ResourceFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ResourceNameFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ResourceRoleFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.SalesVisit,")
            .append("org.opencrx.kernel.activity1.jdo2.ScheduledEndFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.ScheduledStartFilterProperty,")
            .append("org.opencrx.kernel.activity1.jdo2.Segment,")
            .append("org.opencrx.kernel.activity1.jdo2.SetActualEndAction,")
            .append("org.opencrx.kernel.activity1.jdo2.SetActualStartAction,")
            .append("org.opencrx.kernel.activity1.jdo2.SetAssignedToAction,")
            .append("org.opencrx.kernel.activity1.jdo2.Sms,")
            .append("org.opencrx.kernel.activity1.jdo2.SmsRecipient,")
            .append("org.opencrx.kernel.activity1.jdo2.SmsRecipientGroup,")
            .append("org.opencrx.kernel.activity1.jdo2.Task,")
            .append("org.opencrx.kernel.activity1.jdo2.TaskParty,")
            .append("org.opencrx.kernel.activity1.jdo2.WeekDay,")
            .append("org.opencrx.kernel.activity1.jdo2.WfAction,")
            .append("org.opencrx.kernel.activity1.jdo2.WorkReportEntry,")
            .append("org.opencrx.kernel.admin1.jdo2.ComponentConfiguration,")
            .append("org.opencrx.kernel.admin1.jdo2.Segment,")
            .append("org.opencrx.kernel.base.jdo2.AuditEntry,")
            .append("org.opencrx.kernel.base.jdo2.BooleanProperty,")
            .append("org.opencrx.kernel.base.jdo2.BooleanReplacement,")
            .append("org.opencrx.kernel.base.jdo2.DateProperty,")
            .append("org.opencrx.kernel.base.jdo2.DateTimeProperty,")
            .append("org.opencrx.kernel.base.jdo2.DateTimeReplacement,")
            .append("org.opencrx.kernel.base.jdo2.DecimalProperty,")
            .append("org.opencrx.kernel.base.jdo2.IndexEntry,")
            .append("org.opencrx.kernel.base.jdo2.IntegerProperty,")
            .append("org.opencrx.kernel.base.jdo2.NumberReplacement,")
            .append("org.opencrx.kernel.base.jdo2.ObjectCreationAuditEntry,")
            .append("org.opencrx.kernel.base.jdo2.ObjectModificationAuditEntry,")
            .append("org.opencrx.kernel.base.jdo2.ObjectRemovalAuditEntry,")
            .append("org.opencrx.kernel.base.jdo2.Property,")
            .append("org.opencrx.kernel.base.jdo2.ReferenceProperty,")
            .append("org.opencrx.kernel.base.jdo2.ReferenceReplacement,")
            .append("org.opencrx.kernel.base.jdo2.StringProperty,")
            .append("org.opencrx.kernel.base.jdo2.StringReplacement,")
            .append("org.opencrx.kernel.base.jdo2.TemplateReplacement,")
            .append("org.opencrx.kernel.base.jdo2.UriProperty,")
            .append("org.opencrx.kernel.building1.jdo2.AccountAssignmentInventoryItem,")
            .append("org.opencrx.kernel.building1.jdo2.Building,")
            .append("org.opencrx.kernel.building1.jdo2.BuildingComplex,")
            .append("org.opencrx.kernel.building1.jdo2.BuildingUnit,")
            .append("org.opencrx.kernel.building1.jdo2.Facility,")
            .append("org.opencrx.kernel.building1.jdo2.InventoryItem,")
            .append("org.opencrx.kernel.building1.jdo2.LinkableItemLinkFrom,")
            .append("org.opencrx.kernel.building1.jdo2.LinkableItemLinkTo,")
            .append("org.opencrx.kernel.building1.jdo2.Segment,")
            .append("org.opencrx.kernel.code1.jdo2.AbstractEntry,")
            .append("org.opencrx.kernel.code1.jdo2.CodeValueContainer,")
            .append("org.opencrx.kernel.code1.jdo2.CodeValueEntry,")
            .append("org.opencrx.kernel.code1.jdo2.Segment,")
            .append("org.opencrx.kernel.code1.jdo2.SimpleEntry,")
            .append("org.opencrx.kernel.contract1.jdo2.AbstractInvoicePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.AbstractOpportunityPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.AbstractQuotePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.AbstractSalesOrderPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.AccountAddress,")
            .append("org.opencrx.kernel.contract1.jdo2.CalculationRule,")
            .append("org.opencrx.kernel.contract1.jdo2.ConfigurationModification,")
            .append("org.opencrx.kernel.contract1.jdo2.ContractAddress,")
            .append("org.opencrx.kernel.contract1.jdo2.ContractRole,")
            .append("org.opencrx.kernel.contract1.jdo2.CustomerContractRole,")
            .append("org.opencrx.kernel.contract1.jdo2.DeliveryInformation,")
            .append("org.opencrx.kernel.contract1.jdo2.DeliveryRequest,")
            .append("org.opencrx.kernel.contract1.jdo2.EmailAddress,")
            .append("org.opencrx.kernel.contract1.jdo2.Invoice,")
            .append("org.opencrx.kernel.contract1.jdo2.InvoiceBundledProductPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.InvoiceComplexProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.InvoicePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.InvoiceProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.InvoiceProductOfferingPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.Lead,")
            .append("org.opencrx.kernel.contract1.jdo2.Opportunity,")
            .append("org.opencrx.kernel.contract1.jdo2.OpportunityBundledProductPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.OpportunityComplexProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.OpportunityPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.OpportunityProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.OpportunityProductOfferingPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.PhoneNumber,")
            .append("org.opencrx.kernel.contract1.jdo2.PositionCreation,")
            .append("org.opencrx.kernel.contract1.jdo2.PositionModification,")
            .append("org.opencrx.kernel.contract1.jdo2.PositionRemoval,")
            .append("org.opencrx.kernel.contract1.jdo2.PostalAddress,")
            .append("org.opencrx.kernel.contract1.jdo2.ProductApplication,")
            .append("org.opencrx.kernel.contract1.jdo2.QuantityModification,")
            .append("org.opencrx.kernel.contract1.jdo2.Quote,")
            .append("org.opencrx.kernel.contract1.jdo2.QuoteBundledProductPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.QuoteComplexProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.QuotePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.QuoteProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.QuoteProductOfferingPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.RemovedBundledProductPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.RemovedComplexProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.RemovedPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.RemovedProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.RemovedProductOfferingPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrder,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrderBundledProductPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrderComplexProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrderPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrderProductBundlePosition,")
            .append("org.opencrx.kernel.contract1.jdo2.SalesOrderProductOfferingPosition,")
            .append("org.opencrx.kernel.contract1.jdo2.Segment,")
            .append("org.opencrx.kernel.contract1.jdo2.WebAddress,")
            .append("org.opencrx.kernel.depot1.jdo2.Booking,")
            .append("org.opencrx.kernel.depot1.jdo2.BookingPeriod,")
            .append("org.opencrx.kernel.depot1.jdo2.BookingText,")
            .append("org.opencrx.kernel.depot1.jdo2.BundledProductDepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.ComplexProductBundleDepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.CompoundBooking,")
            .append("org.opencrx.kernel.depot1.jdo2.CreditBooking,")
            .append("org.opencrx.kernel.depot1.jdo2.DebitBooking,")
            .append("org.opencrx.kernel.depot1.jdo2.Depot,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotAddress,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotContract,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotEntity,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotEntityRelationship,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotGroup,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotHolder,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReference,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReport,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReportItemBooking,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReportItemCredit,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReportItemDebit,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotReportItemPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.DepotType,")
            .append("org.opencrx.kernel.depot1.jdo2.EmailAddress,")
            .append("org.opencrx.kernel.depot1.jdo2.PhoneNumber,")
            .append("org.opencrx.kernel.depot1.jdo2.PostalAddress,")
            .append("org.opencrx.kernel.depot1.jdo2.ProductBundleDepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.ProductDepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.ProductOfferingDepotPosition,")
            .append("org.opencrx.kernel.depot1.jdo2.Room,")
            .append("org.opencrx.kernel.depot1.jdo2.Segment,")
            .append("org.opencrx.kernel.depot1.jdo2.Site,")
            .append("org.opencrx.kernel.depot1.jdo2.Warehouse,")
            .append("org.opencrx.kernel.depot1.jdo2.WebAddress,")
            .append("org.opencrx.kernel.document1.jdo2.Document,")
            .append("org.opencrx.kernel.document1.jdo2.DocumentFolder,")
            .append("org.opencrx.kernel.document1.jdo2.DocumentRevision,")
            .append("org.opencrx.kernel.document1.jdo2.MediaContent,")
            .append("org.opencrx.kernel.document1.jdo2.MediaReference,")
            .append("org.opencrx.kernel.document1.jdo2.ResourceIdentifier,")
            .append("org.opencrx.kernel.document1.jdo2.Segment,")
            .append("org.opencrx.kernel.forecast1.jdo2.Budget,")
            .append("org.opencrx.kernel.forecast1.jdo2.BudgetMilestone,")
            .append("org.opencrx.kernel.forecast1.jdo2.Forecast,")
            .append("org.opencrx.kernel.forecast1.jdo2.ForecastPeriod,")
            .append("org.opencrx.kernel.forecast1.jdo2.Segment,")
            .append("org.opencrx.kernel.generic.jdo2.AdditionalExternalLink,")
            .append("org.opencrx.kernel.generic.jdo2.BooleanPropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.DatePropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.DateTimePropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.DecimalPropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.Description,")
            .append("org.opencrx.kernel.generic.jdo2.DocumentAttachment,")
            .append("org.opencrx.kernel.generic.jdo2.IntegerPropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.Media,")
            .append("org.opencrx.kernel.generic.jdo2.Note,")
            .append("org.opencrx.kernel.generic.jdo2.PropertySet,")
            .append("org.opencrx.kernel.generic.jdo2.PropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.Rating,")
            .append("org.opencrx.kernel.generic.jdo2.ReferencePropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.StringPropertySetEntry,")
            .append("org.opencrx.kernel.generic.jdo2.UriPropertySetEntry,")
            .append("org.opencrx.kernel.home1.jdo2.AccessHistory,")
            .append("org.opencrx.kernel.home1.jdo2.Alert,")
            .append("org.opencrx.kernel.home1.jdo2.Chart,")
            .append("org.opencrx.kernel.home1.jdo2.EmailAccount,")
            .append("org.opencrx.kernel.home1.jdo2.QuickAccess,")
            .append("org.opencrx.kernel.home1.jdo2.Segment,")
            .append("org.opencrx.kernel.home1.jdo2.Subscription,")
            .append("org.opencrx.kernel.home1.jdo2.UserHome,")
            .append("org.opencrx.kernel.home1.jdo2.WfActionLogEntry,")
            .append("org.opencrx.kernel.home1.jdo2.WfProcessInstance,")
            .append("org.opencrx.kernel.jdo2.Segment,")
            .append("org.opencrx.kernel.model1.jdo2.AliasType,")
            .append("org.opencrx.kernel.model1.jdo2.Association,")
            .append("org.opencrx.kernel.model1.jdo2.AssociationEnd,")
            .append("org.opencrx.kernel.model1.jdo2.Attribute,")
            .append("org.opencrx.kernel.model1.jdo2.Class,")
            .append("org.opencrx.kernel.model1.jdo2.CollectionType,")
            .append("org.opencrx.kernel.model1.jdo2.Constant,")
            .append("org.opencrx.kernel.model1.jdo2.Constraint,")
            .append("org.opencrx.kernel.model1.jdo2.Element,")
            .append("org.opencrx.kernel.model1.jdo2.EnumerationType,")
            .append("org.opencrx.kernel.model1.jdo2.Exception,")
            .append("org.opencrx.kernel.model1.jdo2.Import,")
            .append("org.opencrx.kernel.model1.jdo2.Operation,")
            .append("org.opencrx.kernel.model1.jdo2.Package,")
            .append("org.opencrx.kernel.model1.jdo2.Parameter,")
            .append("org.opencrx.kernel.model1.jdo2.PrimitiveType,")
            .append("org.opencrx.kernel.model1.jdo2.Reference,")
            .append("org.opencrx.kernel.model1.jdo2.Segment,")
            .append("org.opencrx.kernel.model1.jdo2.StructureField,")
            .append("org.opencrx.kernel.model1.jdo2.StructureType,")
            .append("org.opencrx.kernel.model1.jdo2.Tag,")
            .append("org.opencrx.kernel.product1.jdo2.AbstractProductBundle,")
            .append("org.opencrx.kernel.product1.jdo2.AbstractProductBundleType,")
            .append("org.opencrx.kernel.product1.jdo2.AccountAssignment,")
            .append("org.opencrx.kernel.product1.jdo2.AccountAssignmentProduct,")
            .append("org.opencrx.kernel.product1.jdo2.BundledProduct,")
            .append("org.opencrx.kernel.product1.jdo2.BundledProductType,")
            .append("org.opencrx.kernel.product1.jdo2.CategoryFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.ComplexProductBundle,")
            .append("org.opencrx.kernel.product1.jdo2.ComplexProductBundleType,")
            .append("org.opencrx.kernel.product1.jdo2.DefaultSalesTaxTypeFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.DisabledFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.DiscountPriceModifier,")
            .append("org.opencrx.kernel.product1.jdo2.EmailAddress,")
            .append("org.opencrx.kernel.product1.jdo2.LinearPriceModifier,")
            .append("org.opencrx.kernel.product1.jdo2.PhoneNumber,")
            .append("org.opencrx.kernel.product1.jdo2.PostalAddress,")
            .append("org.opencrx.kernel.product1.jdo2.PriceLevel,")
            .append("org.opencrx.kernel.product1.jdo2.PriceListEntry,")
            .append("org.opencrx.kernel.product1.jdo2.PriceModifier,")
            .append("org.opencrx.kernel.product1.jdo2.PriceUomFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.PricingRule,")
            .append("org.opencrx.kernel.product1.jdo2.Product,")
            .append("org.opencrx.kernel.product1.jdo2.ProductAddress,")
            .append("org.opencrx.kernel.product1.jdo2.ProductBasePrice,")
            .append("org.opencrx.kernel.product1.jdo2.ProductBundle,")
            .append("org.opencrx.kernel.product1.jdo2.ProductBundleType,")
            .append("org.opencrx.kernel.product1.jdo2.ProductClassification,")
            .append("org.opencrx.kernel.product1.jdo2.ProductClassificationFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.ProductClassificationRelationship,")
            .append("org.opencrx.kernel.product1.jdo2.ProductConfiguration,")
            .append("org.opencrx.kernel.product1.jdo2.ProductConfigurationType,")
            .append("org.opencrx.kernel.product1.jdo2.ProductConfigurationTypeSet,")
            .append("org.opencrx.kernel.product1.jdo2.ProductFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.ProductOffering,")
            .append("org.opencrx.kernel.product1.jdo2.ProductQueryFilterProperty,")
            .append("org.opencrx.kernel.product1.jdo2.RelatedProduct,")
            .append("org.opencrx.kernel.product1.jdo2.Room,")
            .append("org.opencrx.kernel.product1.jdo2.SalesTaxType,")
            .append("org.opencrx.kernel.product1.jdo2.Segment,")
            .append("org.opencrx.kernel.product1.jdo2.SelectableBundledProduct,")
            .append("org.opencrx.kernel.product1.jdo2.SelectableComplexProductBundle,")
            .append("org.opencrx.kernel.product1.jdo2.SelectableItem,")
            .append("org.opencrx.kernel.product1.jdo2.SelectableProductBundle,")
            .append("org.opencrx.kernel.product1.jdo2.SelectableProductConfiguration,")
            .append("org.opencrx.kernel.product1.jdo2.WebAddress,")
            .append("org.opencrx.kernel.ras1.jdo2.Artifact,")
            .append("org.opencrx.kernel.ras1.jdo2.ArtifactContext,")
            .append("org.opencrx.kernel.ras1.jdo2.ArtifactDependency,")
            .append("org.opencrx.kernel.ras1.jdo2.Asset,")
            .append("org.opencrx.kernel.ras1.jdo2.AssetContext,")
            .append("org.opencrx.kernel.ras1.jdo2.ClassificationElement,")
            .append("org.opencrx.kernel.ras1.jdo2.Descriptor,")
            .append("org.opencrx.kernel.ras1.jdo2.DescriptorGroup,")
            .append("org.opencrx.kernel.ras1.jdo2.DesignDiagram,")
            .append("org.opencrx.kernel.ras1.jdo2.DesignModel,")
            .append("org.opencrx.kernel.ras1.jdo2.ImplementationPart,")
            .append("org.opencrx.kernel.ras1.jdo2.InterfaceSpec,")
            .append("org.opencrx.kernel.ras1.jdo2.Profile,")
            .append("org.opencrx.kernel.ras1.jdo2.RequirementDiagram,")
            .append("org.opencrx.kernel.ras1.jdo2.RequirementModel,")
            .append("org.opencrx.kernel.ras1.jdo2.SolutionPart,")
            .append("org.opencrx.kernel.ras1.jdo2.TestDiagram,")
            .append("org.opencrx.kernel.ras1.jdo2.TestModel,")
            .append("org.opencrx.kernel.ras1.jdo2.UseCase,")
            .append("org.opencrx.kernel.ras1.jdo2.VariabilityPoint,")
            .append("org.opencrx.kernel.reservation1.jdo2.ContactRole,")
            .append("org.opencrx.kernel.reservation1.jdo2.Event,")
            .append("org.opencrx.kernel.reservation1.jdo2.Part,")
            .append("org.opencrx.kernel.reservation1.jdo2.Segment,")
            .append("org.opencrx.kernel.reservation1.jdo2.Slot,")
            .append("org.opencrx.kernel.uom1.jdo2.Segment,")
            .append("org.opencrx.kernel.uom1.jdo2.Uom,")
            .append("org.opencrx.kernel.uom1.jdo2.UomSchedule,")
            .append("org.opencrx.kernel.workflow1.jdo2.AbstractTask,")
            .append("org.opencrx.kernel.workflow1.jdo2.ExternalTask,")
            .append("org.opencrx.kernel.workflow1.jdo2.Segment,")
            .append("org.opencrx.kernel.workflow1.jdo2.Topic,")
            .append("org.opencrx.kernel.workflow1.jdo2.WfProcess,")
            .append("org.opencrx.security.authentication1.jdo2.CrxPassword,")
            .append("org.opencrx.security.authorization1.jdo2.CrxPolicy,")
            .append("org.opencrx.security.identity1.jdo2.Segment,")
            .append("org.opencrx.security.identity1.jdo2.Subject,")
            .append("org.opencrx.security.jdo2.Segment,")
            .append("org.opencrx.security.realm1.jdo2.Principal,")
            .append("org.opencrx.security.realm1.jdo2.PrincipalGroup,")
            .append("org.opencrx.security.realm1.jdo2.User,")
            .append("org.openmdx.security.authentication1.jdo2.AuthenticationContext,")
            .append("org.openmdx.security.authentication1.jdo2.ChallengeResponse,")
            .append("org.openmdx.security.authentication1.jdo2.Credential,")
            .append("org.openmdx.security.authentication1.jdo2.Passcode,")
            .append("org.openmdx.security.authentication1.jdo2.Password,")
            .append("org.openmdx.security.authentication1.jdo2.Segment,")
            .append("org.openmdx.security.authentication1.jdo2.SendPasscode,")
            .append("org.openmdx.security.authorization1.jdo2.ModelClassPrivilege,")
            .append("org.openmdx.security.authorization1.jdo2.Policy,")
            .append("org.openmdx.security.authorization1.jdo2.Privilege,")
            .append("org.openmdx.security.authorization1.jdo2.ResourcePrivilege,")
            .append("org.openmdx.security.authorization1.jdo2.Segment,")
            .append("org.openmdx.security.realm1.jdo2.Group,")
            .append("org.openmdx.security.realm1.jdo2.Permission,")
            .append("org.openmdx.security.realm1.jdo2.Principal,")
            .append("org.openmdx.security.realm1.jdo2.Privilege,")
            .append("org.openmdx.security.realm1.jdo2.Realm,")
            .append("org.openmdx.security.realm1.jdo2.Role,")
            .append("org.openmdx.security.realm1.jdo2.Segment,")
            .append("org.openmdx.base.jdo2.Authority,")
            .append("org.openmdx.base.jdo2.Provider,")
            .append("org.openmdx.base.jdo2.Segment,");
            props.put("org.jpox.autoStartClassNames", classes.toString());
            props.put("javax.jdo.option.ConnectionDriverName","com.microsoft.sqlserver.jdbc.SQLServerDriver");
            props.put("javax.jdo.option.ConnectionURL","jdbc:sqlserver://localhost:1433;databaseName=CRX_1_11;EnableLogging=true");
            props.put("javax.jdo.option.ConnectionUserName","system");
            props.put("javax.jdo.option.ConnectionPassword","manager");
            PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
            
            pm = pmf.getPersistenceManager();        
            pm.putUserObject(
                ObjectIdBuilder.class.getName(),
                new org.opencrx.kernel.layer.persistence.jdo.ObjectIdBuilder()
            );
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * The batch TestRunner can be given a class to run directly.
     * To start the batch runner from your main you can write: 
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run (suite());
    }
    
    //-----------------------------------------------------------------------
    /**
     * A test runner either expects a static method suite as the
     * entry point to get a test to run or it will extract the 
     * suite automatically. 
     */
    public static Test suite() {
        return new TestSuite(TestJdo.class);
    }

    //-----------------------------------------------------------------------
    private void printSecureObject(
        org.opencrx.kernel.base.cci2.SecureObject secureObject
    ) {
        System.out.println("  owners: " + secureObject.getOwner());
        System.out.println("  access level browse: " + secureObject.getAccessLevelBrowse());
        System.out.println("  access level delete: " + secureObject.getAccessLevelDelete());
        System.out.println("  access level update: " + secureObject.getAccessLevelUpdate());        
    }
    
    //-----------------------------------------------------------------------
    private void printAccountAddress(
        org.opencrx.kernel.address1.cci2.Addressable address
    ) {
        System.out.println();
        if(address instanceof org.opencrx.kernel.address1.cci2.PostalAddressable) {
            org.opencrx.kernel.address1.cci2.PostalAddressable postalAddress = (org.opencrx.kernel.address1.cci2.PostalAddressable)address;
            System.out.println("postal address " + postalAddress.getPostalAddressLine());
            System.out.println("  usage: " + address.getUsage());
            System.out.println("  street: " + postalAddress.getPostalStreet());
            System.out.println("  code: " + postalAddress.getPostalCode());
            System.out.println("  city: " + postalAddress.getPostalCode());
        }
        else if(address instanceof org.opencrx.kernel.address1.cci2.PhoneNumberAddressable) {
            org.opencrx.kernel.address1.cci2.PhoneNumberAddressable phoneNumber = (org.opencrx.kernel.address1.cci2.PhoneNumberAddressable)address;
            System.out.println("phone number " + phoneNumber.getPhoneNumberFull());
            System.out.println("  usage: " + address.getUsage());
        }
        else if(address instanceof org.opencrx.kernel.address1.cci2.WebAddressable) {
            org.opencrx.kernel.address1.cci2.WebAddressable webAddress = (org.opencrx.kernel.address1.cci2.WebAddressable)address;
            System.out.println("web address " + webAddress.getWebUrl());
            System.out.println("  usage: " + address.getUsage());
        }
        else if(address instanceof org.opencrx.kernel.address1.cci2.EmailAddressable) {
            org.opencrx.kernel.address1.cci2.EmailAddressable emailAddress = (org.opencrx.kernel.address1.cci2.EmailAddressable)address;
            System.out.println("email address " + emailAddress.getEmailAddress());
            System.out.println("  usage: " + address.getUsage());
        }           
    }

    //-----------------------------------------------------------------------
    public void printCrxObject(
        org.opencrx.kernel.generic.cci2.CrxObject crxObject
    ) {
        this.printSecureObject(crxObject);
        for(org.opencrx.kernel.generic.cci2.Note note : crxObject.getNote()) {
            System.out.println();
            System.out.println("  note " + note.getTitle());
        }
        for(org.opencrx.kernel.generic.cci2.Rating rating : crxObject.getRating()) {
            System.out.println();
            System.out.println("  rating " + rating.getDescription());
        }
        for(org.opencrx.kernel.generic.cci2.PropertySetEntry property : crxObject.getPropertySetEntry()) {
            System.out.println();
            System.out.println("  property " + property.getPropertyName());
        }
        for(org.opencrx.kernel.base.cci2.AuditEntry auditEntry : crxObject.getAudit()) {
            System.out.println();
            System.out.println("  audit " + auditEntry.getCreatedAt());
        }
        for(org.opencrx.kernel.base.cci2.IndexEntry indexEntry : crxObject.getIndexEntry()) {
            System.out.println();
            System.out.println("  index " + indexEntry.getKeywords());
        }
    }

    //-----------------------------------------------------------------------
    public void testAccounts(
    ) throws Exception {
        try {
            pm.currentTransaction().begin();
            
            org.opencrx.kernel.account1.cci2.Segment segment = 
                (org.opencrx.kernel.account1.cci2.Segment)org.oasisopen.jdo2.Identifiable.openmdxjdoGetObjectById(
                    pm,
                    "accounts/CRX/Standard"
                );
            System.out.println("account1 segment");
            System.out.println("  description: " + segment.getDescription());
            this.printSecureObject(segment);
    
            // Accounts
            int ii = 0;
            for(org.opencrx.kernel.account1.cci2.Account account : segment.getAccount()) {
                System.out.println();
                System.out.println("account " + account.getFullName());
                System.out.println("  description: " + account.getDescription());
                this.printCrxObject(account);
                for(org.opencrx.kernel.account1.cci2.AccountAddress address : account.getAddress()) {
                    this.printAccountAddress(address);
                    this.printCrxObject(address);                        
                }
                ii++;
                if(ii > MAX_COUNT) break;
            }
    
            // Addresses
            ii = 0;
            for(org.opencrx.kernel.account1.cci2.AccountAddress address : segment.getAddress()) {
                this.printAccountAddress(address);
                this.printCrxObject(address);                        
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Competitors
            
            // Account filters
            
            // Address filters
            
        }
        finally {
            pm.currentTransaction().commit();            
        }        
    }

    //-----------------------------------------------------------------------
    private void printActivity(
        org.opencrx.kernel.activity1.cci2.Activity activity
    ) {
        System.out.println();
        System.out.println("activity " + activity.getActivityNumber());
        System.out.println("  name: " + activity.getName());
        System.out.println("  description: " + activity.getDescription());
        this.printCrxObject(activity);                      
        for(org.opencrx.kernel.activity1.cci2.ActivityFollowUp followUp : activity.getFollowUp()) {
            System.out.println();
            System.out.println("  follow up " + followUp.getTitle());
            System.out.println("    text: " + followUp.getText());
        }
    }
    
    //-----------------------------------------------------------------------
    public void testActivities(
    ) throws Exception {
        try {
            pm.currentTransaction().begin();
            
            org.opencrx.kernel.activity1.cci2.Segment segment = 
                (org.opencrx.kernel.activity1.cci2.Segment)org.oasisopen.jdo2.Identifiable.openmdxjdoGetObjectById(
                    pm,
                    "activities/CRX/Standard"
                );
            System.out.println("activity1 segment");
            System.out.println("  description: " + segment.getDescription());
            this.printSecureObject(segment);
            
            // Activities
            int ii = 0;
            for(org.opencrx.kernel.activity1.cci2.Activity activity : segment.getActivity()) {
                this.printActivity(activity);
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Creators
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityCreator activityCreator : segment.getActivityCreator()) {
                System.out.println();
                System.out.println("creator " + activityCreator.getName());
                System.out.println("  description: " + activityCreator.getDescription());
                this.printCrxObject(activityCreator);                
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Trackers
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityTracker activityTracker : segment.getActivityTracker()) {
                System.out.println();
                System.out.println("tracker " + activityTracker.getName());
                System.out.println("  description: " + activityTracker.getDescription());
                this.printCrxObject(activityTracker);                
                int jj = 0;
                for(org.opencrx.kernel.activity1.cci2.Activity activity : activityTracker.getFilteredActivity()) {
                    this.printActivity(activity);
                    jj++;
                    if(jj > MAX_COUNT) break;            
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Milestones
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityMilestone activityMilestone : segment.getActivityMilestone()) {
                System.out.println();
                System.out.println("milestone " + activityMilestone.getName());
                System.out.println("  description: " + activityMilestone.getDescription());
                this.printCrxObject(activityMilestone);                
                int jj = 0;
                for(org.opencrx.kernel.activity1.cci2.Activity activity : activityMilestone.getFilteredActivity()) {
                    this.printActivity(activity);
                    jj++;
                    if(jj > MAX_COUNT) break;            
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Category
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityCategory category : segment.getActivityCategory()) {
                System.out.println();
                System.out.println("category " + category.getName());
                System.out.println("  description: " + category.getDescription());
                this.printCrxObject(category);                
                int jj = 0;
                for(org.opencrx.kernel.activity1.cci2.Activity activity : category.getFilteredActivity()) {
                    this.printActivity(activity);
                    jj++;
                    if(jj > MAX_COUNT) break;            
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Resources
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.Resource resource : segment.getResource()) {
                System.out.println();
                System.out.println("resource " + resource.getName());
                System.out.println("  description: " + resource.getDescription());
                this.printCrxObject(resource);                
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Calendars
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.Calendar calendar : segment.getCalendar()) {
                System.out.println();
                System.out.println("calendar " + calendar.getName());
                System.out.println("  description: " + calendar.getDescription());
                this.printCrxObject(calendar);                
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Processes
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityProcess activityProcess : segment.getActivityProcess()) {
                System.out.println();
                System.out.println("process " + activityProcess.getName());
                System.out.println("  description: " + activityProcess.getDescription());
                this.printCrxObject(activityProcess);                
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Activity types
            ii = 0;
            for(org.opencrx.kernel.activity1.cci2.ActivityType activityType : segment.getActivityType()) {
                System.out.println();
                System.out.println("type " + activityType.getName());
                System.out.println("  description: " + activityType.getDescription());
                this.printCrxObject(activityType);                
                ii++;
                if(ii > MAX_COUNT) break;            
            }            
        }
        finally {
            pm.currentTransaction().commit();
        }
    }

    //-----------------------------------------------------------------------
    private void printContractPosition(
        org.opencrx.kernel.contract1.cci2.ContractPosition position
    ) {
        System.out.println();
        System.out.println("  position " + position.getPositionNumber());
        System.out.println("    name: " + position.getName());
        System.out.println("    description: " + position.getDescription());
        System.out.println("    base amount: " + position.getBaseAmount());
    }
    
    //-----------------------------------------------------------------------
    public void testContracts(
    ) throws Exception {
        try{
            pm.currentTransaction().begin();
            
            org.opencrx.kernel.contract1.cci2.Segment segment = 
                (org.opencrx.kernel.contract1.cci2.Segment)org.oasisopen.jdo2.Identifiable.openmdxjdoGetObjectById(
                    pm,
                    "contracts/CRX/Standard"
                );
            System.out.println("contract1 segment");
            System.out.println("  description: " + segment.getDescription());
            this.printSecureObject(segment);
            
            // Leads
            int ii = 0;
            for(org.opencrx.kernel.contract1.cci2.Lead lead : segment.getLead()) {
                System.out.println();
                System.out.println("lead " + lead.getContractNumber());
                System.out.println("  name: " + lead.getName());
                System.out.println("  description: " + lead.getDescription());
                this.printCrxObject(lead);
                ii++;
                if(ii > MAX_COUNT) break;            
            }

            // Opportunities
            ii = 0;
            for(org.opencrx.kernel.contract1.cci2.Opportunity opportunity : segment.getOpportunity()) {
                System.out.println();
                System.out.println("opportunity " + opportunity.getContractNumber());
                System.out.println("  name: " + opportunity.getName());
                System.out.println("  description: " + opportunity.getDescription());
                this.printCrxObject(opportunity);
                for(org.opencrx.kernel.contract1.cci2.AbstractOpportunityPosition position : opportunity.getPosition()) {
                    this.printContractPosition(position);                            
                    this.printCrxObject(position);                    
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Quotes
            ii = 0;
            for(org.opencrx.kernel.contract1.cci2.Quote quote : segment.getQuote()) {
                System.out.println();
                System.out.println("quote " + quote.getContractNumber());
                System.out.println("  name: " + quote.getName());
                System.out.println("  description: " + quote.getDescription());
                this.printCrxObject(quote);
                for(org.opencrx.kernel.contract1.cci2.AbstractQuotePosition position : quote.getPosition()) {
                    this.printContractPosition(position);                            
                    this.printCrxObject(position);                    
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Sales orders
            ii = 0;
            for(org.opencrx.kernel.contract1.cci2.SalesOrder salesOrder : segment.getSalesOrder()) {
                System.out.println();
                System.out.println("sales order " + salesOrder.getContractNumber());
                System.out.println("  name: " + salesOrder.getName());
                System.out.println("  description: " + salesOrder.getDescription());
                this.printCrxObject(salesOrder);
                for(org.opencrx.kernel.contract1.cci2.AbstractSalesOrderPosition position : salesOrder.getPosition()) {
                    this.printContractPosition(position);                            
                    this.printCrxObject(position);                    
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
            // Invoices
            ii = 0;
            for(org.opencrx.kernel.contract1.cci2.Invoice invoice : segment.getInvoice()) {
                System.out.println();
                System.out.println("invoice " + invoice.getContractNumber());
                System.out.println("  name: " + invoice.getName());
                System.out.println("  description: " + invoice.getDescription());
                this.printCrxObject(invoice);
                for(org.opencrx.kernel.contract1.cci2.AbstractInvoicePosition position : invoice.getPosition()) {
                    this.printContractPosition(position);                            
                    this.printCrxObject(position);                    
                }
                ii++;
                if(ii > MAX_COUNT) break;            
            }
            
        }
        finally {
            pm.currentTransaction().commit();
        }
    }

    //-----------------------------------------------------------------------
    private void printProduct(
        org.opencrx.kernel.product1.cci2.AbstractProduct product
    ) {
        System.out.println();
        System.out.println("product " + product.getProductNumber());
        System.out.println("  name: " + product.getName());
        System.out.println("  description: " + product.getDescription());
        this.printCrxObject(product);      
        for(org.opencrx.kernel.product1.cci2.ProductBasePrice price : product.getBasePrice()) {
            System.out.println();
            System.out.println("  base price " + price.getDescription());
            System.out.println("    price: " + price.getPrice());
            System.out.println("    currency: " + price.getPriceCurrency());
            this.printCrxObject(price);
        }
    }
    
    //-----------------------------------------------------------------------
    public void testProducts(
    ) throws Exception {
        try {
            pm.currentTransaction().begin();
            
            org.opencrx.kernel.product1.cci2.Segment segment = 
                (org.opencrx.kernel.product1.cci2.Segment)org.oasisopen.jdo2.Identifiable.openmdxjdoGetObjectById(
                    pm,
                    "products/CRX/Standard"
                );
            System.out.println("product1 segment");
            System.out.println("  description: " + segment.getDescription());
            this.printSecureObject(segment);
            
            // Products
            int ii = 0;
            for(org.opencrx.kernel.product1.cci2.Product product : segment.getProduct()) {
                this.printProduct(product);
                ii++;
                if(ii > MAX_COUNT) break;            
            }
        }
        finally {
            pm.currentTransaction().commit();
        }
    }

    //-----------------------------------------------------------------------
    private void printBooking(
        org.opencrx.kernel.depot1.cci2.SingleBooking booking
    ) {
        System.out.println();
        System.out.println("booking " + booking.getName());
        System.out.println("  description: " + booking.getDescription());
        System.out.println("  booking date: " + booking.getBookingDate());
        this.printCrxObject(booking);        
    }
    
    //-----------------------------------------------------------------------
    public void testDepots(
    ) throws Exception {
        try {
            pm.currentTransaction().begin();
            
            org.opencrx.kernel.depot1.cci2.Segment segment = 
                (org.opencrx.kernel.depot1.cci2.Segment)org.oasisopen.jdo2.Identifiable.openmdxjdoGetObjectById(
                    pm,
                    "depots/CRX/Standard"
                );
            System.out.println("depot1 segment");
            System.out.println("  description: " + segment.getDescription());
            this.printSecureObject(segment);
            
            // DepotEntity
            int ii = 0;
            for(org.opencrx.kernel.depot1.cci2.DepotEntity depotEntity : segment.getEntity()) {
                System.out.println();
                System.out.println("invoice " + depotEntity.getName());
                System.out.println("  description: " + depotEntity.getDescription());
                this.printCrxObject(depotEntity);
                
                // DepotGroup
                int jj = 0;
                for(org.opencrx.kernel.depot1.cci2.DepotGroup depotGroup : depotEntity.getDepotGroup()) {
                    System.out.println();
                    System.out.println("  depotGroup " + depotGroup.getName());
                    System.out.println("  description: " + depotGroup.getDescription());
                    this.printCrxObject(depotGroup);
                    jj++;
                    if(jj > MAX_COUNT) break;
                }
                
                // DepotHolder
                jj = 0;
                for(org.opencrx.kernel.depot1.cci2.DepotHolder depotHolder : depotEntity.getDepotHolder()) {
                    System.out.println();
                    System.out.println("  depotHolder " + depotHolder.getName());
                    System.out.println("  description: " + depotHolder.getDescription());
                    this.printCrxObject(depotHolder);
                    
                    int kk = 0;
                    for(org.opencrx.kernel.depot1.cci2.Depot depot : depotHolder.getDepot()) {
                        System.out.println();
                        System.out.println("  depot " + depot.getDepotNumber());
                        System.out.println("  name: " + depot.getName());
                        System.out.println("  description: " + depot.getDescription());
                        System.out.println("  opening date: " + depot.getOpeningDate());
                        System.out.println("  closing date: " + depot.getClosingDate());
                        this.printCrxObject(depot);
                        kk++;
                        if(kk > MAX_COUNT) break;
                    }
                    
                    jj++;
                    if(jj > MAX_COUNT) break;
                }
                
                ii++;
                if(ii > MAX_COUNT) break;
            }
                
            // ComboundBooking
            ii = 0;
            for(org.opencrx.kernel.depot1.cci2.CompoundBooking cb : segment.getCb()) {
                System.out.println();
                System.out.println("cb " + cb.getName());
                System.out.println("  description: " + cb.getDescription());
                System.out.println("  booking date: " + cb.getBookingDate());
                System.out.println("  accepted by: " + cb.getAcceptedBy());
                this.printCrxObject(cb);
                
                int jj = 0;
                for(org.opencrx.kernel.depot1.cci2.SingleBooking booking : cb.getBooking()) {
                    this.printBooking(booking);
                    jj++;
                    if(jj > MAX_COUNT) break;
                }
                
                ii++;
                if(ii > MAX_COUNT) break;
            }
            
            // Booking
            ii = 0;
            for(org.opencrx.kernel.depot1.cci2.SingleBooking booking : segment.getBooking()) {
                this.printBooking(booking);
                ii++;
                if(ii > MAX_COUNT) break;
            }
            
            // DepotType
            ii = 0;
            for(org.opencrx.kernel.depot1.cci2.DepotType depotType : segment.getDepotType()) {
                System.out.println();
                System.out.println("booking " + depotType.getName());
                System.out.println("  description: " + depotType.getDescription());
                this.printCrxObject(depotType);
                ii++;
                if(ii > MAX_COUNT) break;
            }            
            
        }
        finally {
            pm.currentTransaction().commit();
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final int MAX_COUNT = 50;
    
    private static PersistenceManager pm = null;
}

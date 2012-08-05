/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: PortalExtension.java,v 1.36 2008/08/29 15:46:14 wfro Exp $
 * Description: Evaluator
 * Revision:    $Revision: 1.36 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/08/29 15:46:14 $
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
package org.opencrx.kernel.portal;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefStruct;

import org.opencrx.kernel.activity1.jmi1.InvolvedObject;
import org.opencrx.kernel.address1.jmi1.Addressable;
import org.opencrx.kernel.address1.jmi1.RoomAddressable;
import org.opencrx.kernel.building1.jmi1.AbstractBuildingUnit;
import org.opencrx.kernel.code1.jmi1.SimpleEntry;
import org.opencrx.kernel.contract1.jmi1.ContractRole;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotContract;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReport;
import org.opencrx.kernel.depot1.jmi1.DepotReportItemPosition;
import org.opencrx.kernel.document1.jmi1.Media;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.accessor.jmi.spi.RefMetaObject_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.model1.accessor.basic.cci.ModelElement_1_0;
import org.openmdx.model1.accessor.basic.cci.Model_1_0;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.Autocompleter_1_0;
import org.openmdx.portal.servlet.Codes;
import org.openmdx.portal.servlet.DataBinding_1_0;
import org.openmdx.portal.servlet.DefaultPortalExtension;
import org.openmdx.portal.servlet.HtmlPage;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ValueListAutocompleter;
import org.openmdx.portal.servlet.control.ControlFactory;
import org.openmdx.portal.servlet.view.ObjectView;
import org.openmdx.portal.servlet.view.ShowObjectView;

public class PortalExtension 
    extends DefaultPortalExtension
    implements Serializable {

    //-------------------------------------------------------------------------
    private DateFormat getDateFormat(
        String language
    ) {
        Map dateFormatters = (Map)PortalExtension.cachedDateFormat.get();
        DateFormat dateFormat = (DateFormat)dateFormatters.get(language);
        if(dateFormat == null) {
            dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(language));
            dateFormatters.put(
                language,
                dateFormat
            );
        }
        return dateFormat;
    }
    
    //-------------------------------------------------------------------------
    @Override
    public List getFindObjectsBaseFilter(
        ApplicationContext application,
        RefObject_1_0 context, 
        String referenceName
    ) {
        List<FilterProperty> baseFilter = super.getFindObjectsBaseFilter(
            application, 
            context, 
            referenceName
        );
        // Add disabled filter for types with attribute 'disabled'
        boolean excludeDisabled = false;
        try {
            Model_1_0 model = application.getModel();
            ModelElement_1_0 parentDef = ((RefMetaObject_1)context.refMetaObject()).getElementDef();                
            ModelElement_1_0 referenceDef = 
                (ModelElement_1_0)((Map)parentDef.values("reference").get(0)).get(referenceName);
            if(referenceDef != null) {
                ModelElement_1_0 referencedType = model.getElement(referenceDef.values("type").get(0));                
                excludeDisabled = model.getAttributeDefs(referencedType, true, false).containsKey("disabled");
            }
        } catch(Exception e) {}
        if(excludeDisabled) {
            baseFilter.add(
                new FilterProperty(
                    Quantors.FOR_ALL,
                    "disabled",
                    FilterOperators.IS_IN,
                    new Boolean[]{Boolean.FALSE}
                )                    
            );
        }
        return baseFilter;
    }

    //-------------------------------------------------------------------------
    private DateFormat getTimeFormat(
        String language
    ) {
        Map timeFormatters = (Map)PortalExtension.cachedTimeFormat.get();
        DateFormat timeFormat = (DateFormat)timeFormatters.get(language);
        if(timeFormat == null) {
            timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, new Locale(language)); 
            timeFormatters.put(
                language,
                timeFormat
            );
        }
        return timeFormat; 
    }
    
    //-------------------------------------------------------------------------
    private DecimalFormat getDecimalFormat(
        String language
    ) {
        Map decimalFormatters = (Map)PortalExtension.cachedDecimalFormat.get();
        DecimalFormat decimalFormat = (DecimalFormat)decimalFormatters.get(language);
        if(decimalFormat == null) {
            decimalFormat = (DecimalFormat)DecimalFormat.getInstance(new Locale(language)); 
            decimalFormatters.put(
                language,
                decimalFormat
            );
        }
        return decimalFormat; 
    }

    //-------------------------------------------------------------------------
    @Override
    public String getTitle(
        RefObject_1_0 refObj,
        short locale,
        String localeAsString,
        ApplicationContext application
    ) {
        if(refObj == null) {
            return "#NULL";
        }
        if(refObj.refIsNew() || !refObj.refIsPersistent()) {
            return "Untitled";
        }
        try {
          Codes codes = application.getCodes();
          String language = localeAsString.substring(0, 2);
          DateFormat dateFormat = this.getDateFormat(language);
          DateFormat timeFormat =  this.getTimeFormat(language);
          DecimalFormat decimalFormat = this.getDecimalFormat(language);
          
          if(refObj instanceof org.openmdx.base.jmi1.Segment) {
              return application.getLabel(refObj.refClass().refMofId());
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.Account) {
              org.opencrx.kernel.account1.jmi1.Account obj = (org.opencrx.kernel.account1.jmi1.Account)refObj;
              return toS(obj.getFullName());
          }
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.ProductBasePrice) {
              org.opencrx.kernel.product1.jmi1.ProductBasePrice obj = (org.opencrx.kernel.product1.jmi1.ProductBasePrice)refObj;
              Map currencyTexts = codes.getLongText("org:opencrx:kernel:product1:AbstractProductPrice:priceCurrency", locale, true, true);
              try {
                  return toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " " + toS(currencyTexts.get(new Short(obj.getPriceCurrency())));
              }
              catch(Exception e) {
                  return toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " N/A";                  
              }
          }          
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.PriceListEntry) {
              org.opencrx.kernel.product1.jmi1.PriceListEntry obj = (org.opencrx.kernel.product1.jmi1.PriceListEntry)refObj;
              return toS(obj.getProductName());
          }          
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
              org.opencrx.kernel.activity1.jmi1.Activity obj = (org.opencrx.kernel.activity1.jmi1.Activity)refObj;
              return toS(obj.getActivityNumber()).trim() + ": " + toS(obj.getName());
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.ActivityFollowUp) {
              org.opencrx.kernel.activity1.jmi1.ActivityFollowUp followUp = (org.opencrx.kernel.activity1.jmi1.ActivityFollowUp)refObj;              
              RefObject_1_0 activity = (RefObject_1_0)application.getPmData().getObjectById(
                  followUp.refGetPath().getParent().getParent()
              );
              return this.getTitle(activity, locale, localeAsString, application) + ": " + toS(followUp.getTitle());
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.AddressGroupMember) {
              org.opencrx.kernel.activity1.jmi1.AddressGroupMember member = (org.opencrx.kernel.activity1.jmi1.AddressGroupMember)refObj;
              org.opencrx.kernel.account1.jmi1.AccountAddress address = member.getAddress();
              if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
                  org.opencrx.kernel.account1.jmi1.Account account = 
                      (org.opencrx.kernel.account1.jmi1.Account)application.getPmData().getObjectById(
                          address.refGetPath().getParent().getParent()
                      );
                  return this.getTitle(address, locale, localeAsString, application) + " / " + account.getFullName();
              }
              else {
                  return address  == null ? 
                      "Untitled" : 
                      this.getTitle(address, locale, localeAsString, application);                
              }
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.AbstractActivityParty) {
              RefObject_1_0 party = null;
              if(
                  (refObj instanceof org.opencrx.kernel.activity1.jmi1.EmailRecipient) ||
                  (refObj instanceof org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient)
              ) {
                  org.opencrx.kernel.account1.jmi1.AccountAddress address = null;
                  if(refObj instanceof org.opencrx.kernel.activity1.jmi1.EmailRecipient) {
                      org.opencrx.kernel.activity1.jmi1.EmailRecipient recipient = (org.opencrx.kernel.activity1.jmi1.EmailRecipient)refObj;
                      address = recipient.getParty();
                  }
                  else {
                      org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient recipient = (org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient)refObj;
                      address = recipient.getParty();                      
                  }
                  if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
                      org.opencrx.kernel.account1.jmi1.Account account = 
                          (org.opencrx.kernel.account1.jmi1.Account)application.getPmData().getObjectById(
                              address.refGetPath().getParent().getParent()
                          );
                      return this.getTitle(address, locale, localeAsString, application) + " / " + account.getFullName();
                  }
                  else {
                      return address  == null ? 
                          "Untitled" : 
                          this.getTitle(address, locale, localeAsString, application);                
                  }                  
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.EmailRecipientGroup) {
                  party = ((org.opencrx.kernel.activity1.jmi1.EmailRecipientGroup)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.IncidentParty) {
                  party = ((org.opencrx.kernel.activity1.jmi1.IncidentParty)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.MailingRecipient) {
                  party = ((org.opencrx.kernel.activity1.jmi1.MailingRecipient)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.MailingRecipientGroup) {
                  party = ((org.opencrx.kernel.activity1.jmi1.MailingRecipientGroup)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.MeetingParty) {
                  party = ((org.opencrx.kernel.activity1.jmi1.MeetingParty)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.TaskParty) {
                  party = ((org.opencrx.kernel.activity1.jmi1.TaskParty)refObj).getParty();
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.PhoneCallRecipientGroup) {
                  party = ((org.opencrx.kernel.activity1.jmi1.PhoneCallRecipientGroup)refObj).getParty();                  
              }
              return party == null ? 
                  "Untitled" :
                  this.getTitle(party, locale, localeAsString, application);
          }
          else if(refObj instanceof org.opencrx.kernel.contract1.jmi1.AccountAddress) {
             return refObj.refGetValue("address") == null ? 
                 "Untitled" : 
                 this.getTitle((RefObject_1_0)refObj.refGetValue("address"), locale, localeAsString, application);
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.EmailAddressable) {
              return "* " + toS(refObj.refGetValue("emailAddress"));
          }
          else if(refObj instanceof org.opencrx.kernel.contract1.jmi1.ContractPosition) {
              return toS(refObj.refGetValue("lineItemNumber"));
          }
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.AbstractProduct) {
              org.opencrx.kernel.product1.jmi1.AbstractProduct obj = (org.opencrx.kernel.product1.jmi1.AbstractProduct)refObj;
              return obj.getProductNumber() == null || obj.getProductNumber().length() == 0 ? 
                  toS(obj.getName()) : 
                  toS(obj.getName() + " / " + obj.getProductNumber()); 
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable) {
              String address = "";
              int nLines = 0;
              for(Iterator i = ((List)refObj.refGetValue("postalAddressLine")).iterator(); i.hasNext(); ) {
                  String line = toS(i.next());
                  if(line.length() > 0) {
                      if(nLines > 0) address += "<br />";
                      address += line;
                      nLines++;
                  }
              }
              for(Iterator i = ((List)refObj.refGetValue("postalStreet")).iterator(); i.hasNext(); ) {
                  String street = toS(i.next());
                  if(street.length() > 0) {
                      if(nLines > 0) address += "<br />";
                      address += street;
                      nLines++;
                  }
              }
              Object postalCountry = refObj.refGetValue("postalCountry");
              String postalCountryS = postalCountry == null
                  ? ""
                  : codes == null
                      ? "" + postalCountry
                      : toS(codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(postalCountry));
              return address + "<br />" + toS(refObj.refGetValue("postalCode")) + " " + toS(refObj.refGetValue("postalCity")) + "<br />" + postalCountryS;
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.PhoneNumberAddressable) {
              return toS(refObj.refGetValue("phoneNumberFull"));
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.RoomAddressable) {
              RoomAddressable obj = (RoomAddressable)refObj;
              if(refObj instanceof Addressable) {              
                  AbstractBuildingUnit building = ((Addressable)refObj).getBuilding();
                  if(building == null) {
                      return toS(obj.getRoomNumber());                  
                  }
                  else {
                      return this.getTitle(building, locale, localeAsString, application) + " " +  toS(obj.getRoomNumber());
                  }
              }
              else {
                  return toS(obj.getRoomNumber());
              }
          }
          else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Rating) {
              return toS(refObj.refGetValue("ratingLevel"));
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.RevenueReport) {
              return toS(refObj.refGetValue("reportNumber"));
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.WebAddressable) {
              return "* " + toS(refObj.refGetValue("webUrl"));
          }
          else if(refObj instanceof org.opencrx.kernel.code1.jmi1.CodeValueEntry) {
              return toS(refObj.refGetValue("shortText"));
          }
          else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Description) {
              return toS(refObj.refGetValue("language"));
          }
          else if(refObj instanceof org.opencrx.kernel.document1.jmi1.Document) {
              org.opencrx.kernel.document1.jmi1.Document document = (org.opencrx.kernel.document1.jmi1.Document)refObj;
              if(document.getQualifiedName() != null) {
                  return toS(document.getQualifiedName());
              }
              else if(document.getName() != null) {
                  return toS(document.getName());                  
              }
              else {
                  return toS(document.getDocumentNumber());
              }
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.Member) {
              return (refObj.refGetValue("account") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("account"), locale, localeAsString, application));
          }    
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.ContactRelationship) {
              return (refObj.refGetValue("toContact") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("toContact"), locale, localeAsString, application));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
              return (refObj.refGetValue("contact") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("contact"), locale, localeAsString, application));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.AccessHistory) {
              return (refObj.refGetValue("reference") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("reference"), locale, localeAsString, application));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.EmailAccount) {
              return toS(refObj.refGetValue("eMailAddress"));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.WfProcessInstance) {
              return (refObj.refGetValue("process") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("process"), locale, localeAsString, application) + " " + toS(refObj.refGetValue("startedOn")));
          }      
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.AuditEntry) {
              Object createdAt = refObj.refGetValue(SystemAttributes.CREATED_AT);
              return (refObj.refGetValue(SystemAttributes.CREATED_AT) == null) ? "Untitled" : dateFormat.format(createdAt) + " " + timeFormat.format(createdAt);
          }
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.Note) {
              return toS(refObj.refGetValue("title"));
          }
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.Chart) {
              return toS(refObj.refGetValue("description"));
          }
          else if(refObj instanceof org.opencrx.kernel.model1.jmi1.Element) {
              String title = toS(refObj.refGetValue("qualifiedName"));              
              if(title.indexOf(":") > 0) {
                  int pos = title.lastIndexOf(":");
                  title = title.substring(pos + 1) + " (" + title.substring(0, pos) + ")";
              }
              return title;
          }
          else if(refObj instanceof DepotEntity) {
              DepotEntity obj = (DepotEntity)refObj;
              return obj.getDepotEntityNumber() == null ? toS(obj.getName()) : toS(obj.getDepotEntityNumber());
          }
          else if(refObj instanceof DepotContract) {
              DepotContract obj = (DepotContract)refObj;
              return obj.getDepotHolderNumber() == null ? toS(obj.getName()) : toS(obj.getDepotHolderNumber());
          }
          else if(refObj instanceof Depot) {
              Depot obj = (Depot)refObj;
              return obj.getDepotNumber() == null ? toS(obj.getName()) : toS(obj.getDepotNumber());          
          }
          else if(refObj instanceof DepotPosition) {
              DepotPosition obj = (DepotPosition)refObj;
              String depotTitle = this.getTitle(obj.getDepot(), locale, localeAsString, application);
              return depotTitle + " / " + toS(obj.getName());                    
          }
          else if(refObj instanceof DepotReport) {
              DepotReport obj = (DepotReport)refObj;
              String depotTitle = this.getTitle(obj.getDepot(), locale, localeAsString, application);
              return depotTitle + " / " + toS(obj.getName());                    
          }
          else if(refObj instanceof DepotReportItemPosition) {
              DepotReportItemPosition obj = (DepotReportItemPosition)refObj;
              return obj.getPositionName();
          }
          else if(refObj instanceof SimpleEntry) {
              SimpleEntry obj = (SimpleEntry)refObj;
              return toS(obj.getEntryValue());
          }
          else if(refObj instanceof Media) {
              Media obj = (Media)refObj;
              return toS(obj.getContentName());
          }
          else if(refObj instanceof ContractRole) {
              ContractRole contractRole = (ContractRole)refObj;
              return 
                  this.getTitle(contractRole.getContract(), locale, localeAsString, application) + " / " + 
                  this.getTitle(contractRole.getAccount(), locale, localeAsString, application) + " / " + 
                  this.getTitle(contractRole.getContractReferenceHolder(), locale, localeAsString, application);
          }
          else if(refObj instanceof InvolvedObject) {
              InvolvedObject involved = (InvolvedObject)refObj;
              return this.getTitle(involved.getInvolved(), locale, localeAsString, application);
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.AccountAssignment) {
              org.opencrx.kernel.account1.jmi1.AccountAssignment obj = (org.opencrx.kernel.account1.jmi1.AccountAssignment)refObj;
              return this.getTitle(obj.getAccount(), locale, localeAsString, application);
          }          
          else {
              return super.getTitle(refObj, locale, localeAsString, application);
          }
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            AppLog.info(e0.getMessage(), e0.getCause());
            AppLog.info("can not evaluate. object", refObj.refMofId());
            return "#ERR (" + e.getMessage() + ")";
        }
    }

    //-------------------------------------------------------------------------
    public boolean isEnabled(
        String elementName, 
        RefObject_1_0 refObj,
        ApplicationContext context
    ) {
        if(refObj instanceof org.opencrx.kernel.depot1.jmi1.CompoundBooking) {
            try {
                org.opencrx.kernel.depot1.jmi1.CompoundBooking cb = (org.opencrx.kernel.depot1.jmi1.CompoundBooking)refObj;
                boolean isPending = cb.getBookingStatus() == 1;     
                boolean isProcessed = cb.getBookingStatus() == 2;
                boolean isReversal = cb.getBookingType() == 30;
                if("org:opencrx:kernel:depot1:CompoundBooking:cancelCb".equals(elementName)) {
                    return isProcessed && !isReversal;
                }
                else if("org:opencrx:kernel:depot1:CompoundBooking:acceptCb".equals(elementName)) {
                    return isPending;
                }
                else if("org:opencrx:kernel:depot1:CompoundBooking:finalizeCb".equals(elementName)) {
                    return isPending;
                }
            }
            catch(Exception e) {
                ServiceException e0 = new ServiceException(e);
                AppLog.warning(e0.getMessage(), e0.getCause());                
            }
        }
        return super.isEnabled(
            elementName, 
            refObj,
            context
        );
    }
    
    //-------------------------------------------------------------------------
    public String getIdentityQueryFilterClause(
        String qualifiedReferenceName
    ) {
        if("org:opencrx:kernel:contract1:AbstractContract:salesRep".equals(qualifiedReferenceName)) {
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.sales_rep = a.object_id AND UPPER(a.full_name) LIKE ?s0)"; 
        }
        else if("org:opencrx:kernel:contract1:AbstractContract:customer".equals(qualifiedReferenceName)) {
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.customer = a.object_id AND UPPER(a.full_name) LIKE ?s0)";             
        }
        else if("org:opencrx:kernel:contract1:AbstractContract:supplier".equals(qualifiedReferenceName)) {
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.supplier = a.object_id AND UPPER(a.full_name) LIKE ?s0)";             
        }
        else if("org:opencrx:kernel:activity1:Activity:assignedTo".equals(qualifiedReferenceName)) {
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.assigned_to = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                         
        }        
        else if("org:opencrx:kernel:product1:PriceListEntry:product".equals(qualifiedReferenceName)) {
            return "EXISTS (SELECT 0 FROM OOCKE1_PRODUCT p WHERE v.product = p.object_id AND UPPER(p.name) LIKE ?s0)";
        }
        else if("org:opencrx:kernel:home1:UserHome:contact".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.contact = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else if("org:opencrx:kernel:account1:AccountAssignment:account".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else if("org:opencrx:kernel:contract1:ContractRole:account".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else if("org:opencrx:kernel:account1:AccountMembership:accountFrom".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account_from = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else if("org:opencrx:kernel:account1:AccountMembership:accountTo".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account_to = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else if("org:opencrx:kernel:account1:AccountMembership:forUseBy".equals(qualifiedReferenceName)) {            
            return "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.for_use_by = a.object_id AND UPPER(a.full_name) LIKE ?s0)";                                     
        }
        else {
            return super.getIdentityQueryFilterClause(qualifiedReferenceName);
        }
    }
    
    //-------------------------------------------------------------------------
    @Override
    public int getGridPageSize(
        String referencedTypeName
    ) {
        if("org:opencrx:kernel:product1:SelectableItem".equals(referencedTypeName)) {
            return 500;
        }
        else if("org:opencrx:kernel:model1:EditableOperation".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableParameter".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableAttribute".equals(referencedTypeName)) {
            return 200;
        }
        else if("org:opencrx:kernel:model1:EditableStructureField".equals(referencedTypeName)) {
            return 200;
        }
        else {
            return super.getGridPageSize(referencedTypeName);
        }
    }
    
    //-------------------------------------------------------------------------
    @Override
    public boolean isLookupType(
        ModelElement_1_0 classDef
    ) {
        String qualifiedName = (String)classDef.values("qualifiedName").get(0);
        return 
            !"org:opencrx:kernel:generic:CrxObject".equals(qualifiedName) &&
            super.isLookupType(classDef);
    }
        
    //-------------------------------------------------------------------------
    @Override
    public Autocompleter_1_0 getAutocompleter(
        ApplicationContext application,
        RefObject_1_0 context,
        String qualifiedFeatureName
    ) {
        RefPackage_1_0 rootPkg = (RefPackage_1_0)context.refOutermostPackage();
        // Derive autocomplete lookup root from root object 0
        
        // org:opencrx:kernel:activity1:ActivityDoFollowUpParams:transition
        if("org:opencrx:kernel:activity1:ActivityDoFollowUpParams:transition".equals(qualifiedFeatureName)) {
            List selectableValues = null;
            if(context instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
                org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)context;
                org.opencrx.kernel.activity1.jmi1.ActivityType activityType = null;
                org.opencrx.kernel.activity1.jmi1.ActivityProcessState processState = null;
                org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = 
                    (org.opencrx.kernel.activity1.jmi1.Activity1Package)rootPkg.refPackage(
                        org.opencrx.kernel.activity1.jmi1.Activity1Package.class.getName()
                    );
                try {
                    activityType = activity.getActivityType();
                    processState = activity.getProcessState();
                } catch(Exception e) {}
                if((activityType != null) && (processState != null)) {
                    selectableValues = new ArrayList();
                    org.opencrx.kernel.activity1.jmi1.ActivityProcess activityProcess = activityType.getControlledBy();
                    processState = activity.getProcessState();
                    org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery transitionFilter = activityPkg.createActivityProcessTransitionQuery();
                    transitionFilter.orderByNewPercentComplete().ascending();
                    for(Iterator i = activityProcess.getTransition(transitionFilter).iterator(); i.hasNext(); ) {
                        org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition = 
                            (org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition)i.next();
                        if(transition.getPrevState().equals(processState)) {
                            selectableValues.add(
                                new ObjectReference(transition, application)
                            );
                        }
                    }
                }
            }
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:activity1:ActivityAssignToParams:resource
        else if("org:opencrx:kernel:activity1:ActivityAssignToParams:resource".equals(qualifiedFeatureName)) {
            List selectableValues = null;
            if(context instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
                org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)context;
                Path activityIdentity = new Path(activity.refMofId());
                org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                    (org.opencrx.kernel.activity1.jmi1.Segment)rootPkg.refObject(
                    activityIdentity.getPrefix(activityIdentity.size() - 2).toXri()
                );
                org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = 
                    (org.opencrx.kernel.activity1.jmi1.Activity1Package)rootPkg.refPackage(
                        org.opencrx.kernel.activity1.jmi1.Activity1Package.class.getName()
                    );
                org.opencrx.kernel.activity1.cci2.ResourceQuery filter = activityPkg.createResourceQuery();
                filter.orderByName().ascending();
                selectableValues = new ArrayList();
                int count = 0;
                for(
                    Iterator i = activitySegment.getResource(filter).iterator(); 
                    i.hasNext() && (count < 20);
                ) {
                    org.opencrx.kernel.activity1.jmi1.Resource resource = (org.opencrx.kernel.activity1.jmi1.Resource)i.next();
                    if(resource != null) {
                        selectableValues.add(
                            new ObjectReference(resource, application)
                        );
                    }
                }
                // Show at most 20 values in drop down. Otherwise show lookup
                if(count >= 20) {
                    selectableValues = null;
                }
            }
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:activity1:ResourceAddWorkRecordByPeriodParams:activity
        // org:opencrx:kernel:activity1:ResourceAddWorkRecordByDurationParams:activity
        else if(
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByPeriodParams:activity".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByDurationParams:activity".equals(qualifiedFeatureName)
        ) {
            List selectableValues = null;
            if(context instanceof org.opencrx.kernel.activity1.jmi1.Resource) {
                org.opencrx.kernel.activity1.jmi1.Resource resource = 
                    (org.opencrx.kernel.activity1.jmi1.Resource)context;
                org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = 
                    (org.opencrx.kernel.activity1.jmi1.Activity1Package)rootPkg.refPackage(
                        org.opencrx.kernel.activity1.jmi1.Activity1Package.class.getName()
                    );
                org.opencrx.kernel.activity1.cci2.ActivityQuery filter = activityPkg.createActivityQuery();
                filter.thereExistsPercentComplete().lessThan(new Short((short)100));
                filter.orderByActivityNumber().ascending();
                selectableValues = new ArrayList();
                int count = 0;
                for(
                    Iterator i = resource.getAssignedActivity(filter).iterator(); 
                    i.hasNext() && count < 20; 
                    count++
                ) {
                    selectableValues.add(
                        new ObjectReference((org.opencrx.kernel.activity1.jmi1.Activity)i.next(), application)
                    );
                }
                // Show at most 20 values in drop down. Otherwise show lookup
                if(count >= 20) {
                    selectableValues = null;
                }
            }
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:base:Property:....
        else if(
            "org:opencrx:kernel:base:StringProperty:stringValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:IntegerProperty:integerValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:BooleanProperty:booleanValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:UriProperty:uriValue".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:base:DecimalProperty:decimalValue".equals(qualifiedFeatureName)
        ) {
            List selectableValues = null;
            if(context instanceof org.opencrx.kernel.base.jmi1.Property) {
                org.opencrx.kernel.base.jmi1.Property property = 
                    (org.opencrx.kernel.base.jmi1.Property)context;
                try {
                    if(property.getDomain() != null) {
                        selectableValues = new ArrayList();
                        org.opencrx.kernel.code1.jmi1.CodeValueContainer domain = property.getDomain();
                        for(Iterator i = domain.getEntry().iterator(); i.hasNext(); ) {
                            org.opencrx.kernel.code1.jmi1.AbstractEntry entry = (org.opencrx.kernel.code1.jmi1.AbstractEntry)i.next();
                            selectableValues.add(
                                entry.getEntryValue() != null 
                                    ? entry.getEntryValue()
                                    // get qualifier as value if no entryValue is specified
                                    : new Path(entry.refMofId()).getBase()
                            );
                        }
                    }
                } catch(Exception e) {}
            }
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:base:ExportItemParams:itemMimeType
        else if(
            "org:opencrx:kernel:base:ExportItemAdvancedParams:itemMimeType".equals(qualifiedFeatureName)
        ) {
            List<String> selectableValues = new ArrayList<String>();
            selectableValues.add("application/x-excel");
            selectableValues.add("text/xml");
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:base:ExportItemParams:exportProfile
        if("org:opencrx:kernel:base:ExportItemParams:exportProfile".equals(qualifiedFeatureName)) {
            List<ObjectReference> selectableValues = null;
            if(context instanceof org.opencrx.kernel.base.jmi1.Exporter) {
                PersistenceManager pm = application.getPmData();
                String providerName = context.refGetPath().get(2);
                String segmentName = context.refGetPath().get(4);
                String currentPrincipal = application.getUserHomeIdentity().getBase();
                String adminPrincipal = SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName;
                // Collect export profiles from current user
                try {
                    org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)application.getPmData().getObjectById(
                        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + currentPrincipal)
                    );
                    org.opencrx.kernel.home1.cci2.ExportProfileQuery exportProfileQuery = Utils.getHomePackage(pm).createExportProfileQuery();
                    exportProfileQuery.orderByName().ascending();
                    Collection<org.opencrx.kernel.home1.jmi1.ExportProfile> exportProfiles = userHome.getExportProfile();
                    for(org.opencrx.kernel.home1.jmi1.ExportProfile exportProfile: exportProfiles) {
                        for(String forClass: exportProfile.getForClass()) {
                            if(application.getModel().isSubtypeOf(context.refClass().refMofId(), forClass)) {
                                if(selectableValues == null) {
                                    selectableValues = new ArrayList<ObjectReference>();
                                }
                                selectableValues.add(
                                    new ObjectReference(exportProfile, application)
                                );
                                break;
                            }
                        }
                    }
                } catch(Exception e) {}
                // Collect shared export profiles from segment admin
                try {
                    if(!currentPrincipal.equals(adminPrincipal)) {
                        org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)application.getPmData().getObjectById(
                            new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/" + providerName + "/segment/" + segmentName + "/userHome/" + adminPrincipal)
                        );
                        org.opencrx.kernel.home1.cci2.ExportProfileQuery exportProfileQuery = Utils.getHomePackage(pm).createExportProfileQuery();
                        exportProfileQuery.orderByName().ascending();
                        Collection<org.opencrx.kernel.home1.jmi1.ExportProfile> exportProfiles = userHome.getExportProfile();
                        for(org.opencrx.kernel.home1.jmi1.ExportProfile exportProfile: exportProfiles) {
                            for(String forClass: exportProfile.getForClass()) {
                                if(application.getModel().isSubtypeOf(context.refClass().refMofId(), forClass)) {
                                    if(selectableValues == null) {
                                        selectableValues = new ArrayList<ObjectReference>();
                                    }
                                    selectableValues.add(
                                        new ObjectReference(exportProfile, application)
                                    );
                                    break;
                                }
                            }
                        }
                    }
                }
                catch(Exception e) {}
            }
            return selectableValues == null
                ? null
                : new ValueListAutocompleter(selectableValues);
        }        
        else {
            return super.getAutocompleter(
                application,
                context,
                qualifiedFeatureName
            );
        }
    }

    
    //-------------------------------------------------------------------------
    @Override
    public ObjectView getLookupView(
        String id, 
        ModelElement_1_0 lookupType, 
        RefObject_1_0 startFrom, 
        String filterValues,
        ControlFactory controlFactory,
        ApplicationContext application
    ) throws ServiceException {
        Model_1_0 model = application.getModel();
        // start from customer if the current object is a contract and the 
        // lookup type is AccountAddress
        if(startFrom instanceof org.opencrx.kernel.contract1.jmi1.AbstractContract) {
            org.opencrx.kernel.contract1.jmi1.AbstractContract contract = 
                (org.opencrx.kernel.contract1.jmi1.AbstractContract)startFrom;
            org.opencrx.kernel.account1.jmi1.Account customer = null;
            try {
                customer = contract.getCustomer();
            } catch(Exception e) {}
            if(
                (customer != null) &&
                model.isSubtypeOf("org:opencrx:kernel:account1:AccountAddress", lookupType)
            ) {
                startFrom = customer;
            }
            ShowObjectView view = (ShowObjectView)super.getLookupView(
                id, 
                lookupType, 
                startFrom, 
                filterValues,
                controlFactory, 
                application
            );
            // The grid of the lookup object can be postprocessed, i.e.
            // select reference panes, set grid filters, set grid orders            
//            Grid grid = view.selectReferencePane("address");
//            Filter[] filters = grid.getFilters();
//            grid.selectFilter(
//                filters[5].getName(), // the filter position is defined by customization
//                null
//            );
            return view;
        }
        // Default lookup
        else {
            return super.getLookupView(
                id, 
                lookupType, 
                startFrom, 
                filterValues,
                controlFactory, 
                application
            );
        }
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean hasUserDefineableQualifier(
        org.openmdx.ui1.jmi1.Inspector inspector,
        ApplicationContext application        
    ) {
        return 
            application.getCurrentUserRole().startsWith(SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR) ||
            CLASSES_WITH_USER_DEFINABLE_QUALIFER.contains(inspector.getForClass());
    }
        
    //-------------------------------------------------------------------------
    /**
     * The default implementation handles the following tags:
     * <ul>
     *   <li>activity:&lt;activity number&gt;
     * </ul>
     */
    @Override
    public void renderTextValue(
        HtmlPage p,
        String value
    ) throws ServiceException {
        ApplicationContext application = p.getApplicationContext();        
        // Process Tag activity:#
        if(
            (p.getView() instanceof ObjectView) && 
            (value.indexOf("activity:") >= 0)
        ) {
            RefObject_1_0 object = ((ObjectView)p.getView()).getRefObject();
            String providerName = object.refGetPath().get(2);
            String segmentName = object.refGetPath().get(4);
            RefPackage_1_0 rootPkg = (RefPackage_1_0)object.refOutermostPackage();            
            org.opencrx.kernel.activity1.jmi1.Activity1Package activityPkg = 
                (org.opencrx.kernel.activity1.jmi1.Activity1Package)rootPkg.refPackage(
                    org.opencrx.kernel.activity1.jmi1.Activity1Package.class.getName()
                );
            Path activitySegmentIdentity = 
                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName);
            org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                (org.opencrx.kernel.activity1.jmi1.Segment)application.getPmData().getObjectById(activitySegmentIdentity);
            int currentPos = 0;
            int newPos;
            while((newPos = value.indexOf("activity:", currentPos)) >= 0) {
                super.renderTextValue(
                    p,
                    value.substring(currentPos, newPos)
                );
                // Get activity number
                int start = newPos + 9;
                int end = start;
                while(
                    (end < value.length()) &&
                    Character.isDigit(value.charAt(end))
                ) {
                    end++;
                }
                // activity: is followed by number. Try to find activity with the
                // corresponding activity number
                if(end > start) {
                    String activityNumber = value.substring(start, end);
                    org.opencrx.kernel.activity1.cci2.ActivityQuery filter = activityPkg.createActivityQuery();
                    filter.thereExistsActivityNumber().equalTo(
                        activityNumber
                    );
                    Collection activities = activitySegment.getActivity(filter);
                    if(activities.size() == 1) {
                        ObjectReference objRef = new ObjectReference(
                            (RefObject_1_0)activities.iterator().next(),
                            application
                        );
                        Action action = objRef.getSelectObjectAction();
                        p.write("<a href=\"\" onclick=\"javascript:this.href=", p.getEvalHRef(action), ";\">", application.getHtmlEncoder().encode(action.getTitle(), false), "</a>");                        
                    }
                    else {
                        super.renderTextValue(
                            p,
                            value.substring(newPos, end)
                        );
                    }
                }
                else {
                    super.renderTextValue(
                        p,
                        value.substring(newPos, end)
                    );
                }
                currentPos = end;
            }
            super.renderTextValue(
                p,
                value.substring(currentPos)
            );
        }
        else {
            super.renderTextValue(
                p, 
                value
            );
        }
    }
    
    //-------------------------------------------------------------------------
    @Override
    public DataBinding_1_0 getDataBinding(
        String dataBindingName,
        ApplicationContext application
    ) {
        if(StringPropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new StringPropertyDataBinding();
        }
        else if(IntegerPropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new IntegerPropertyDataBinding();
        }
        else if(BooleanPropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new BooleanPropertyDataBinding();
        }
        else if(DecimalPropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new DecimalPropertyDataBinding();
        }
        else if(DatePropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new DatePropertyDataBinding();
        }
        else if(DateTimePropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new DateTimePropertyDataBinding();
        }
        else if(ReferencePropertyDataBinding.class.getName().equals(dataBindingName)) {
            return new ReferencePropertyDataBinding();
        }
        else if(AssignedActivityGroupsDataBinding.class.getName().equals(dataBindingName)) {
            return new AssignedActivityGroupsDataBinding();
        }
        else if(FilteredActivitiesDataBinding.class.getName().equals(dataBindingName)) {
            return new FilteredActivitiesDataBinding(application.getCurrentUserRole());
        }
        else {
            return super.getDataBinding(
                dataBindingName,
                application
            );
        }
    }
    
    //-------------------------------------------------------------------------
    @Override
    public RefObject_1_0 handleOperationResult(
        RefObject_1_0 target,
        String operationName, 
        RefStruct params, 
        RefStruct result
    ) throws ServiceException {
        if(result instanceof org.opencrx.kernel.home1.jmi1.SearchResult) {
            org.opencrx.kernel.home1.jmi1.SearchResult searchResult = (org.opencrx.kernel.home1.jmi1.SearchResult)result;
            if(searchResult.getObjectFinder() != null) {
                RefObject_1_0 objectFinder = (RefObject_1_0)((RefPackage_1_0)target.refOutermostPackage()).refObject(
                    searchResult.getObjectFinder().refMofId()
                );
                return objectFinder;
            }
        }
        return super.handleOperationResult(
            target, 
            operationName, 
            params, 
            result
        );
    }

    //-------------------------------------------------------------------------
    private static final long serialVersionUID = 3761691203816992816L;

    private static final Set CLASSES_WITH_USER_DEFINABLE_QUALIFER =
        new HashSet(Arrays.asList(
            new String[]{
                "org:opencrx:security:realm1:PrincipalGroup",
                "org:opencrx:security:realm1:Principal",                
                "org:opencrx:security:realm1:User",
                "org:opencrx:security:identity:Subject",
                "org:opencrx:kernel:uom1:Uom"
            }
        ));
    
    private static ThreadLocal cachedDateFormat = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };
    private static ThreadLocal cachedTimeFormat = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };
    private static ThreadLocal cachedDecimalFormat = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };
    
}

//--- End of File -----------------------------------------------------------

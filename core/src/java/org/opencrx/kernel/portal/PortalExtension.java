/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: PortalExtension.java,v 1.97 2010/04/27 12:17:30 wfro Exp $
 * Description: PortalExtension
 * Revision:    $Revision: 1.97 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/27 12:17:30 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
import java.net.URLEncoder;
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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jmi.reflect.RefStruct;

import org.opencrx.kernel.activity1.jmi1.InvolvedObject;
import org.opencrx.kernel.address1.jmi1.Addressable;
import org.opencrx.kernel.address1.jmi1.RoomAddressable;
import org.opencrx.kernel.backend.Addresses;
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
import org.opencrx.kernel.portal.AbstractPropertyDataBinding.PropertySetHolderType;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.accessor.jmi.spi.RefMetaObject_1;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.naming.Path;
import org.openmdx.base.query.Condition;
import org.openmdx.base.query.FilterOperators;
import org.openmdx.base.query.FilterProperty;
import org.openmdx.base.query.PiggyBackCondition;
import org.openmdx.base.query.Quantors;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.Autocompleter_1_0;
import org.openmdx.portal.servlet.Codes;
import org.openmdx.portal.servlet.DataBinding;
import org.openmdx.portal.servlet.DefaultPortalExtension;
import org.openmdx.portal.servlet.ObjectReference;
import org.openmdx.portal.servlet.ValueListAutocompleter;
import org.openmdx.portal.servlet.ViewPort;
import org.openmdx.portal.servlet.control.Control;
import org.openmdx.portal.servlet.view.ObjectView;
import org.openmdx.portal.servlet.view.ShowObjectView;

public class PortalExtension 
    extends DefaultPortalExtension
    implements Serializable {

    //-------------------------------------------------------------------------
    private DateFormat getDateFormat(
        String language
    ) {
        Map<String,DateFormat> dateFormatters = PortalExtension.cachedDateFormat.get();
        DateFormat dateFormat = dateFormatters.get(language);
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
    @SuppressWarnings("unchecked")
    @Override
    public List<FilterProperty> getFindObjectsBaseFilter(
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
                (ModelElement_1_0)((Map)parentDef.objGetValue("reference")).get(referenceName);
            if(referenceDef != null) {
                ModelElement_1_0 referencedType = model.getElement(referenceDef.objGetValue("type"));                
                excludeDisabled = model.getAttributeDefs(referencedType, true, false).containsKey("disabled");
            }
        } 
        catch(Exception e) {}
        if(excludeDisabled) {
            baseFilter.add(
                new FilterProperty(
                    Quantors.FOR_ALL,
                    "disabled",
                    FilterOperators.IS_IN,
                    Boolean.FALSE
                )                    
            );
        }
        return baseFilter;
    }

    //-------------------------------------------------------------------------
    private DateFormat getTimeFormat(
        String language
    ) {
        Map<String,DateFormat> timeFormatters = PortalExtension.cachedTimeFormat.get();
        DateFormat timeFormat = timeFormatters.get(language);
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
        Map<String,DecimalFormat> decimalFormatters = PortalExtension.cachedDecimalFormat.get();
        DecimalFormat decimalFormat = decimalFormatters.get(language);
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
    @SuppressWarnings("unchecked")
    @Override
    public String getTitle(
        RefObject_1_0 refObj,
        short locale,
        String localeAsString,
        boolean asShortTitle,
        ApplicationContext application
    ) {
        if(refObj == null) {
            return "#NULL";
        }
        if(JDOHelper.isNew(refObj) || !JDOHelper.isPersistent(refObj)) {
            return "Untitled";
        }        
        try {
          PersistenceManager pm = JDOHelper.getPersistenceManager(refObj);
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
              return this.toNbspS(obj.getFullName());
          }
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.ProductBasePrice) {
              org.opencrx.kernel.product1.jmi1.ProductBasePrice obj = (org.opencrx.kernel.product1.jmi1.ProductBasePrice)refObj;
              Map<Object,Object> currencyTexts = codes.getLongText("org:opencrx:kernel:product1:AbstractProductPrice:priceCurrency", locale, true, true);
              try {
                  return this.toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " " + toS(currencyTexts.get(new Short(obj.getPriceCurrency())));
              }
              catch(Exception e) {
                  return this.toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " N/A";                  
              }
          }          
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.PriceListEntry) {
              org.opencrx.kernel.product1.jmi1.PriceListEntry obj = (org.opencrx.kernel.product1.jmi1.PriceListEntry)refObj;
              return this.toS(obj.getProductName());
          }          
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
              org.opencrx.kernel.activity1.jmi1.Activity obj = (org.opencrx.kernel.activity1.jmi1.Activity)refObj;
              return this.toS(obj.getActivityNumber()).trim() + ": " + this.toS(obj.getName());
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.ActivityProcessState) {
              org.opencrx.kernel.activity1.jmi1.ActivityProcessState obj = (org.opencrx.kernel.activity1.jmi1.ActivityProcessState)refObj;
              return this.toNbspS(obj.getName());
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.ActivityFollowUp) {
              org.opencrx.kernel.activity1.jmi1.ActivityFollowUp followUp = (org.opencrx.kernel.activity1.jmi1.ActivityFollowUp)refObj;
              org.opencrx.kernel.activity1.jmi1.Activity activity = null;
              // In case of NO_PERMISSION
              try {
                  activity = (org.opencrx.kernel.activity1.jmi1.Activity)pm.getObjectById(
                      followUp.refGetPath().getParent().getParent()
                  );
                  activity.getName();
              } 
              catch(Exception e) {}
              return 
                  (activity == null ? "" : this.getTitle(activity, locale, localeAsString, asShortTitle, application) + ": ") + 
                  this.toS(followUp.getTitle());
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment) {
              org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment obj = (org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment)refObj;
              return obj == null ? 
                  "Untitled" : 
                  this.getTitle(obj.getActivityGroup(), locale, localeAsString, asShortTitle, application);
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.AddressGroupMember) {
              org.opencrx.kernel.activity1.jmi1.AddressGroupMember member = (org.opencrx.kernel.activity1.jmi1.AddressGroupMember)refObj;
              org.opencrx.kernel.account1.jmi1.AccountAddress address = member.getAddress();
              if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
                  org.opencrx.kernel.account1.jmi1.Account account = 
                      (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
                          address.refGetPath().getParent().getParent()
                      );
                  return this.getTitle(address, locale, localeAsString, asShortTitle, application) + " / " + account.getFullName();
              }
              else {
                  return address  == null ? 
                      "Untitled" : 
                      this.getTitle(address, locale, localeAsString, asShortTitle, application);                
              }
          }
          else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.AbstractActivityParty) {
              RefObject_1_0 party = null;
              if(
                  (refObj instanceof org.opencrx.kernel.activity1.jmi1.EMailRecipient) ||
                  (refObj instanceof org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient)
              ) {
                  org.opencrx.kernel.account1.jmi1.AccountAddress address = null;
                  if(refObj instanceof org.opencrx.kernel.activity1.jmi1.EMailRecipient) {
                      org.opencrx.kernel.activity1.jmi1.EMailRecipient recipient = (org.opencrx.kernel.activity1.jmi1.EMailRecipient)refObj;
                      address = recipient.getParty();
                      if(address instanceof org.opencrx.kernel.account1.jmi1.EMailAddress) {
                          String title = this.getTitle(address, locale, localeAsString, asShortTitle, application);
                          if(asShortTitle) {
                        	  return title;
                          }
                          else {
	                          org.opencrx.kernel.activity1.jmi1.EMail email = (org.opencrx.kernel.activity1.jmi1.EMail)pm.getObjectById(
	                              recipient.refGetPath().getParent().getParent()
	                          );
	                          String messageSubject = email.getMessageSubject() == null ? 
	                        	  "" :
	                        	  URLEncoder.encode(email.getMessageSubject(), "UTF-8").replace("+", "%20");
	                          String messageBody = email.getMessageBody() == null ?
	                        	  "" :
	                        	  URLEncoder.encode(email.getMessageBody(), "UTF-8").replace("+", "%20");
	                          // Browser limit
	                          if(messageBody.length() > 1500) {
	                              messageBody = messageBody.substring(0, 1500);
	                          }
	                          return title + (title.indexOf("@") > 0 ? "?subject=" + messageSubject + "&body=" + messageBody : "");
                          }
                      }
                  }
                  else {
                      org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient recipient = (org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient)refObj;
                      address = recipient.getParty();                      
                  }
                  if(address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
                      org.opencrx.kernel.account1.jmi1.Account account = 
                          (org.opencrx.kernel.account1.jmi1.Account)pm.getObjectById(
                              address.refGetPath().getParent().getParent()
                          );
                      return this.getTitle(address, locale, localeAsString, asShortTitle, application) + " / " + account.getFullName();
                  }
                  else {
                      return address  == null ? 
                          "Untitled" : 
                          this.getTitle(address, locale, localeAsString, asShortTitle, application);                
                  }                  
              }
              else if(refObj instanceof org.opencrx.kernel.activity1.jmi1.EMailRecipientGroup) {
                  party = ((org.opencrx.kernel.activity1.jmi1.EMailRecipientGroup)refObj).getParty();
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
                  this.getTitle(party, locale, localeAsString, asShortTitle, application);
          }
          else if(refObj instanceof org.opencrx.kernel.contract1.jmi1.AccountAddress) {
             return refObj.refGetValue("address") == null ? 
                 "Untitled" : 
                 this.getTitle((RefObject_1_0)refObj.refGetValue("address"), locale, localeAsString, asShortTitle, application);
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.EMailAddressable) {
              return "* " + this.toS(refObj.refGetValue("emailAddress"));
          }
          else if(refObj instanceof org.opencrx.kernel.contract1.jmi1.AbstractContractPosition) {
              return this.toS(refObj.refGetValue("lineItemNumber"));
          }
          else if(refObj instanceof org.opencrx.kernel.product1.jmi1.AbstractProduct) {
              org.opencrx.kernel.product1.jmi1.AbstractProduct product = (org.opencrx.kernel.product1.jmi1.AbstractProduct)refObj;
              return product.getProductNumber() == null || product.getProductNumber().length() == 0 || product.getProductNumber().equals(product.getName()) ? 
            	  this.toS(product.getName()) : 
            		  this.toS(product.getName() + " / " + product.getProductNumber()); 
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.PostalAddressable) {
              String address = "";
              int nLines = 0;
              for(Iterator<String> i = ((List<String>)refObj.refGetValue("postalAddressLine")).iterator(); i.hasNext(); ) {
                  String line = this.toS(i.next());
                  if(line.length() > 0) {
                      if(nLines > 0) address += "<br />";
                      address += line;
                      nLines++;
                  }
              }
              for(Iterator<String> i = ((List<String>)refObj.refGetValue("postalStreet")).iterator(); i.hasNext(); ) {
                  String street = this.toS(i.next());
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
                      : this.toS(codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(postalCountry));
              return address + "<br />" + this.toS(refObj.refGetValue("postalCode")) + " " + this.toS(refObj.refGetValue("postalCity")) + "<br />" + postalCountryS;
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.PhoneNumberAddressable) {
              return "* " + this.toS(refObj.refGetValue("phoneNumberFull"));
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.RoomAddressable) {
              RoomAddressable obj = (RoomAddressable)refObj;
              if(refObj instanceof Addressable) {              
                  AbstractBuildingUnit building = ((Addressable)refObj).getBuilding();
                  if(building == null) {
                      return this.toS(obj.getRoomNumber());                  
                  }
                  else {
                      return this.getTitle(building, locale, localeAsString, asShortTitle, application) + " " +  this.toS(obj.getRoomNumber());
                  }
              }
              else {
                  return this.toS(obj.getRoomNumber());
              }
          }
          else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Rating) {
              return this.toS(refObj.refGetValue("ratingLevel"));
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.RevenueReport) {
              return this.toS(refObj.refGetValue("reportNumber"));
          }
          else if(refObj instanceof org.opencrx.kernel.address1.jmi1.WebAddressable) {
              return "* " + this.toS(refObj.refGetValue("webUrl"));
          }
          else if(refObj instanceof org.opencrx.kernel.code1.jmi1.CodeValueEntry) {
              return this.toS(refObj.refGetValue("shortText"));
          }
          else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Description) {
              return this.toS(refObj.refGetValue("language"));
          }
          else if(refObj instanceof org.opencrx.kernel.document1.jmi1.Document) {
              org.opencrx.kernel.document1.jmi1.Document document = (org.opencrx.kernel.document1.jmi1.Document)refObj;
              if(document.getQualifiedName() != null) {
                  return this.toS(document.getQualifiedName());
              }
              else if(document.getName() != null) {
                  return this.toS(document.getName());                  
              }
              else {
                  return this.toS(document.getDocumentNumber());
              }
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.Member) {
              return (refObj.refGetValue("account") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("account"), locale, localeAsString, asShortTitle, application));
          }    
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.ContactRelationship) {
              return (refObj.refGetValue("toContact") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("toContact"), locale, localeAsString, asShortTitle, application));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
              org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)refObj;
              if(userHome == null) {
                  return "Untitled"; 
              }
              else {
                  return this.getTitle(userHome.getContact(), locale, localeAsString, asShortTitle, application);
              }
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.AccessHistory) {
              return (refObj.refGetValue("reference") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("reference"), locale, localeAsString, asShortTitle, application));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.EMailAccount) {
              return this.toS(refObj.refGetValue("eMailAddress"));
          }
          else if(refObj instanceof org.opencrx.kernel.home1.jmi1.WfProcessInstance) {
              return (refObj.refGetValue("process") == null ? "Untitled" : this.getTitle((RefObject_1_0)refObj.refGetValue("process"), locale, localeAsString, asShortTitle, application) + " " + this.toS(refObj.refGetValue("startedOn")));
          }      
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.AuditEntry) {
              Object createdAt = refObj.refGetValue(SystemAttributes.CREATED_AT);
              return (refObj.refGetValue(SystemAttributes.CREATED_AT) == null) ? "Untitled" : dateFormat.format(createdAt) + " " + timeFormat.format(createdAt);
          }
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.Note) {
              return this.toS(refObj.refGetValue("title"));
          }
          else if(refObj instanceof org.opencrx.kernel.base.jmi1.Chart) {
              return this.toS(refObj.refGetValue("description"));
          }
          else if(refObj instanceof org.opencrx.kernel.model1.jmi1.Element) {
              String title = this.toS(refObj.refGetValue("qualifiedName"));              
              if(title.indexOf(":") > 0) {
                  int pos = title.lastIndexOf(":");
                  title = title.substring(pos + 1) + " (" + title.substring(0, pos) + ")";
              }
              return title;
          }
          else if(refObj instanceof DepotEntity) {
              DepotEntity obj = (DepotEntity)refObj;
              return obj.getDepotEntityNumber() == null ? this.toS(obj.getName()) : this.toS(obj.getDepotEntityNumber());
          }
          else if(refObj instanceof DepotContract) {
              DepotContract obj = (DepotContract)refObj;
              return obj.getDepotHolderNumber() == null ? this.toS(obj.getName()) : this.toS(obj.getDepotHolderNumber());
          }
          else if(refObj instanceof Depot) {
              Depot obj = (Depot)refObj;
              return obj.getDepotNumber() == null ? this.toS(obj.getName()) : this.toS(obj.getDepotNumber());          
          }
          else if(refObj instanceof DepotPosition) {
              DepotPosition obj = (DepotPosition)refObj;
              String depotTitle = this.getTitle(obj.getDepot(), locale, localeAsString, asShortTitle, application);
              return depotTitle + " / " + this.toS(obj.getName());                    
          }
          else if(refObj instanceof DepotReport) {
              DepotReport obj = (DepotReport)refObj;
              String depotTitle = this.getTitle(obj.getDepot(), locale, localeAsString, asShortTitle, application);
              return depotTitle + " / " + this.toS(obj.getName());                    
          }
          else if(refObj instanceof DepotReportItemPosition) {
              DepotReportItemPosition obj = (DepotReportItemPosition)refObj;
              return obj.getPositionName();
          }
          else if(refObj instanceof SimpleEntry) {
              SimpleEntry obj = (SimpleEntry)refObj;
              return this.toS(obj.getEntryValue());
          }
          else if(refObj instanceof Media) {
              Media obj = (Media)refObj;
              return this.toS(obj.getContentName());
          }
          else if(refObj instanceof ContractRole) {
              ContractRole contractRole = (ContractRole)refObj;
              return 
                  this.getTitle(contractRole.getContract(), locale, localeAsString, asShortTitle, application) + " / " + 
                  this.getTitle(contractRole.getAccount(), locale, localeAsString, asShortTitle, application) + " / " + 
                  this.getTitle(contractRole.getContractReferenceHolder(), locale, localeAsString, asShortTitle, application);
          }
          else if(refObj instanceof InvolvedObject) {
              InvolvedObject involved = (InvolvedObject)refObj;
              return this.getTitle(involved.getInvolved(), locale, localeAsString, asShortTitle, application);
          }
          else if(refObj instanceof org.opencrx.kernel.account1.jmi1.AccountAssignment) {
              org.opencrx.kernel.account1.jmi1.AccountAssignment obj = (org.opencrx.kernel.account1.jmi1.AccountAssignment)refObj;
              return this.getTitle(obj.getAccount(), locale, localeAsString, asShortTitle, application);
          }          
          else {
              return super.getTitle(refObj, locale, localeAsString, asShortTitle, application);
          }
        }
        catch(Exception e) {
            ServiceException e0 = new ServiceException(e);
            SysLog.info(e0.getMessage(), e0.getCause());
            SysLog.info("can not evaluate. object", refObj.refMofId());
            return "#ERR (" + e.getMessage() + ")";
        }
    }

    //-------------------------------------------------------------------------
    @Override
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
                SysLog.warning(e0.getMessage(), e0.getCause());                
            }
        }
        return super.isEnabled(
            elementName, 
            refObj,
            context
        );
    }
    
    //-------------------------------------------------------------------------
    @Override
    public boolean isEnabled(
        Control control, 
        RefObject_1_0 refObj,
        ApplicationContext application
    ) {
        return super.isEnabled(
            control, 
            refObj, 
            application
        );
    }
    
    //-------------------------------------------------------------------------
    protected  List<Condition> getQueryConditions(
    	String clause,
    	String queryFilterContext,
    	List<String> stringParams,
    	ApplicationContext app
    ) {
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(
            new PiggyBackCondition(
                queryFilterContext + SystemAttributes.OBJECT_CLASS, 
                Database_1_Attributes.QUERY_FILTER_CLASS
            )                          
        );
        conditions.add(
            new PiggyBackCondition(
                queryFilterContext + Database_1_Attributes.QUERY_FILTER_CLAUSE, 
                clause
            )                          
        );
        conditions.add(
            new PiggyBackCondition(
                queryFilterContext + Database_1_Attributes.QUERY_FILTER_STRING_PARAM, 
                (Object[])stringParams.toArray(new String[stringParams.size()])
            )                          
        );    	
        return conditions;
    }
    
    //-------------------------------------------------------------------------
    @Override
    public List<Condition> getQuery(
    	org.openmdx.ui1.jmi1.ValuedField field,
    	String filterValue,
    	String queryFilterContext,
    	int queryFilterStringParamCount,
    	ApplicationContext app
    ) {
    	String qualifiedReferenceName = field.getQualifiedFeatureName();
    	String clause = null;
    	int paramCount = queryFilterStringParamCount;
    	String s0 = "?s" + paramCount++;
    	String s1 = "?s" + paramCount++;
    	List<String> stringParams = new ArrayList<String>();
        String stringParam = app.getWildcardFilterValue(filterValue); 
        String stringParam0 = stringParam.startsWith("(?i)") ? stringParam.substring(4) : stringParam;
        String stringParam1 = stringParam.startsWith("(?i)") ? stringParam.substring(4).toUpperCase() : stringParam.toUpperCase();
        if("org:opencrx:kernel:contract1:AbstractContract:salesRep".equals(qualifiedReferenceName)) {
            clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.sales_rep = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:contract1:AbstractContract:customer".equals(qualifiedReferenceName)) {
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.customer = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";             
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:contract1:AbstractContract:supplier".equals(qualifiedReferenceName)) {
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.supplier = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";             
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:activity1:Activity:assignedTo".equals(qualifiedReferenceName)) {
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.assigned_to = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                         
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }        
        else if("org:opencrx:kernel:product1:PriceListEntry:product".equals(qualifiedReferenceName)) {
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_PRODUCT p WHERE v.product = p.object_id AND (UPPER(p.name) LIKE UPPER(" + s0 + ") OR UPPER(p.name) LIKE " + s1 + "))";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:home1:UserHome:contact".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.contact = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:account1:AccountAssignment:account".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:contract1:ContractRole:account".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:account1:AccountMembership:accountFrom".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account_from = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:account1:AccountMembership:accountTo".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.account_to = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:account1:AccountMembership:forUseBy".equals(qualifiedReferenceName)) {            
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ACCOUNT a WHERE v.for_use_by = a.object_id AND (UPPER(a.full_name) LIKE UPPER(" + s0 + ") OR UPPER(a.full_name) LIKE " + s1 + "))";                                     
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
        else if("org:opencrx:kernel:activity1:AddressGroupMember:address".equals(qualifiedReferenceName)) {       
        	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a WHERE v.address = a.object_id AND (UPPER(a.postal_address_line_0) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_address_line_1) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_address_line_2) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_0) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_1) LIKE UPPER(" + s0 + ") OR UPPER(a.postal_street_2) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.email_address) LIKE UPPER(" + s0 + ")))";  
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
	    else if(field instanceof org.openmdx.ui1.jmi1.ObjectReferenceField) {
            clause = "(" + qualifiedReferenceName.substring(qualifiedReferenceName.lastIndexOf(":") + 1) + " LIKE " + s0 + ")";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
        }
	    else if("org:opencrx:kernel:account1:Account:address*Business!phoneNumberFull".equals(field.getQualifiedFeatureName())) {
	    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND (UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
	    }
	    else if("org:opencrx:kernel:account1:Account:address*Mobile!phoneNumberFull".equals(field.getQualifiedFeatureName())) {
	    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND (UPPER(a.phone_number_full) LIKE UPPER(" + s0 + ") OR UPPER(a.phone_number_full) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_MOBILE + ")";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
	    }
	    else if("org:opencrx:kernel:account1:Account:address*Business!emailAddress".equals(field.getQualifiedFeatureName())) {
	    	clause = "EXISTS (SELECT 0 FROM OOCKE1_ADDRESS a INNER JOIN OOCKE1_ADDRESS_ a_ ON a.object_id = a_.object_id WHERE v.object_id = a.p$$parent AND (UPPER(a.email_address) LIKE UPPER(" + s0 + ") OR UPPER(a.email_address) LIKE " + s1 + ") AND a_.objusage = " + Addresses.USAGE_BUSINESS + ")";
            stringParams.add(stringParam0);
            stringParams.add(stringParam1);            	
	    }
	    else {
            return super.getQuery(
            	field, 
            	filterValue, 
            	queryFilterContext,
            	queryFilterStringParamCount,
            	app
            );
	    }
        return this.getQueryConditions(
        	clause, 
        	queryFilterContext, 
        	stringParams, 
        	app
        );
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
    ) throws ServiceException {
        String qualifiedName = (String)classDef.objGetValue("qualifiedName");
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
            List<ObjectReference> selectableValues = null;
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
                    selectableValues = new ArrayList<ObjectReference>();
                    org.opencrx.kernel.activity1.jmi1.ActivityProcess activityProcess = activityType.getControlledBy();
                    processState = activity.getProcessState();
                    org.opencrx.kernel.activity1.cci2.ActivityProcessTransitionQuery transitionFilter = activityPkg.createActivityProcessTransitionQuery();
                    transitionFilter.orderByNewPercentComplete().ascending();
                    List<org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition> transitions = activityProcess.getTransition(transitionFilter);
                    for(org.opencrx.kernel.activity1.jmi1.ActivityProcessTransition transition: transitions) {
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
            List<ObjectReference> selectableValues = null;
            if(context instanceof org.opencrx.kernel.activity1.jmi1.Activity) {
                org.opencrx.kernel.activity1.jmi1.Activity activity = (org.opencrx.kernel.activity1.jmi1.Activity)context;
            	PersistenceManager pm = JDOHelper.getPersistenceManager(activity);
                Path activityIdentity = activity.refGetPath();
                org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
                    (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(
                    activityIdentity.getPrefix(activityIdentity.size() - 2)
                );
                org.opencrx.kernel.activity1.cci2.ResourceQuery query = 
                	(org.opencrx.kernel.activity1.cci2.ResourceQuery)pm.newQuery(org.opencrx.kernel.activity1.jmi1.Resource.class);
                query.forAllDisabled().isFalse();
                query.contact().isNonNull();
                query.orderByName().ascending();
                selectableValues = new ArrayList<ObjectReference>();
                int count = 0;
                List<org.opencrx.kernel.activity1.jmi1.Resource> resources = activitySegment.getResource(query);
                for(org.opencrx.kernel.activity1.jmi1.Resource resource: resources) {
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
            return selectableValues == null ? 
            	null : 
            	new ValueListAutocompleter(selectableValues);
        }
        // org:opencrx:kernel:activity1:ResourceAddWorkRecordByPeriodParams:activity
        // org:opencrx:kernel:activity1:ResourceAddWorkRecordByDurationParams:activity
        else if(
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByPeriodParams:activity".equals(qualifiedFeatureName) ||
            "org:opencrx:kernel:activity1:ResourceAddWorkRecordByDurationParams:activity".equals(qualifiedFeatureName)
        ) {
            List<ObjectReference> selectableValues = null;
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
                selectableValues = new ArrayList<ObjectReference>();
                int count = 0;
                List<org.opencrx.kernel.activity1.jmi1.Activity> activities = resource.getAssignedActivity(filter);
                for(
                    Iterator<org.opencrx.kernel.activity1.jmi1.Activity> i = activities.iterator(); 
                    i.hasNext() && count < 20; 
                    count++
                ) {
                    selectableValues.add(
                        new ObjectReference(i.next(), application)
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
            List<String> selectableValues = null;
            if(context instanceof org.opencrx.kernel.base.jmi1.Property) {
                org.opencrx.kernel.base.jmi1.Property property = 
                    (org.opencrx.kernel.base.jmi1.Property)context;
                try {
                    if(property.getDomain() != null) {
                        selectableValues = new ArrayList<String>();
                        org.opencrx.kernel.code1.jmi1.CodeValueContainer domain = property.getDomain();
                        Collection<org.opencrx.kernel.code1.jmi1.AbstractEntry> entries = domain.getEntry();
                        for(org.opencrx.kernel.code1.jmi1.AbstractEntry entry: entries) {
                            selectableValues.add(
                                entry.getEntryValue() != null 
                                    ? entry.getEntryValue()
                                    // get qualifier as value if no entryValue is specified
                                    : new Path(entry.refMofId()).getBase()
                            );
                        }
                    }
                } 
                catch(Exception e) {}
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
                PersistenceManager pm = JDOHelper.getPersistenceManager(context);
                String providerName = context.refGetPath().get(2);
                String segmentName = context.refGetPath().get(4);
                String currentPrincipal = application.getUserHomeIdentity().getBase();
                String adminPrincipal = SecurityKeys.ADMIN_PRINCIPAL + SecurityKeys.ID_SEPARATOR + segmentName;
                // Collect export profiles from current user
                try {
                    org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
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
                } 
                catch(Exception e) {}
                // Collect shared export profiles from segment admin
                try {
                    if(!currentPrincipal.equals(adminPrincipal)) {
                        org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)pm.getObjectById(
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
            } 
            catch(Exception e) {}
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
                application
            );
            return view;
        }
        // Default lookup
        else {
            return super.getLookupView(
                id, 
                lookupType, 
                startFrom, 
                filterValues,
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
        ViewPort p,
        String value,
        boolean asWiki
    ) throws ServiceException {
        ApplicationContext application = p.getApplicationContext();
        // Process Tag activity:#
        if(
            (p.getView() instanceof ObjectView) && 
            (value.indexOf("activity:") >= 0)
        ) {
            RefObject_1_0 object = ((ObjectView)p.getView()).getRefObject();
            PersistenceManager pm = JDOHelper.getPersistenceManager(object);
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
                (org.opencrx.kernel.activity1.jmi1.Segment)pm.getObjectById(activitySegmentIdentity);
            int currentPos = 0;
            int newPos;
            while((newPos = value.indexOf("activity:", currentPos)) >= 0) {
                super.renderTextValue(
                    p,
                    value.substring(currentPos, newPos),
                    asWiki
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
                    Collection<org.opencrx.kernel.activity1.jmi1.Activity> activities = activitySegment.getActivity(filter);
                    if(activities.size() == 1) {
                        ObjectReference objRef = new ObjectReference(
                            activities.iterator().next(),
                            application
                        );
                        Action action = objRef.getSelectObjectAction();
                      	p.write("<a href=\"\" onclick=\"javascript:this.href=", p.getEvalHRef(action), ";\">", application.getHtmlEncoder().encode(action.getTitle(), false), "</a>");
                    }
                    else {
                        super.renderTextValue(
                            p,
                            value.substring(newPos, end),
                            asWiki
                        );
                    }
                }
                else {
                    super.renderTextValue(
                        p,
                        value.substring(newPos, end),
                        asWiki
                    );
                }
                currentPos = end;
            }
            super.renderTextValue(
                p,
                value.substring(currentPos),
                asWiki
            );
        }
        else {
            super.renderTextValue(
                p, 
                value,
                asWiki
            );
        }
    }
    
    //-------------------------------------------------------------------------
    @Override
    public DataBinding getDataBinding(
        String dataBindingName
    ) {
        if((dataBindingName != null) && dataBindingName.startsWith(StringPropertyDataBinding.class.getName())) {
            return new StringPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(IntegerPropertyDataBinding.class.getName())) {
            return new IntegerPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(BooleanPropertyDataBinding.class.getName())) {
            return new BooleanPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(DecimalPropertyDataBinding.class.getName())) {
            return new DecimalPropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(DatePropertyDataBinding.class.getName())) {
            return new DatePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(DateTimePropertyDataBinding.class.getName())) {
            return new DateTimePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(ReferencePropertyDataBinding.class.getName())) {
            return new ReferencePropertyDataBinding(
                dataBindingName.indexOf("?") < 0 ? PropertySetHolderType.CrxObject : PropertySetHolderType.valueOf(dataBindingName.substring(dataBindingName.indexOf("?") + 1))
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(EmailAddressDataBinding.class.getName())) {
            return new EmailAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(PhoneNumberDataBinding.class.getName())) {
            return new PhoneNumberDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(PostalAddressDataBinding.class.getName())) {
            return new PostalAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(WebAddressDataBinding.class.getName())) {
            return new WebAddressDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(AssignedActivityGroupsDataBinding.class.getName())) {
            return new AssignedActivityGroupsDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(DocumentFolderAssignmentsDataBinding.class.getName())) {
            return new DocumentFolderAssignmentsDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        }
        else if((dataBindingName != null) && dataBindingName.startsWith(EMailRecipientDataBinding.class.getName())) {
            return new EMailRecipientDataBinding(
                dataBindingName.indexOf("?") < 0 ? "" : dataBindingName.substring(dataBindingName.indexOf("?") + 1)            	
            );
        }
        else if(FilteredActivitiesDataBinding.class.getName().equals(dataBindingName)) {
            return new FilteredActivitiesDataBinding();
        }
        else if(FormattedNoteDataBinding.class.getName().equals(dataBindingName)) {
            return new FormattedNoteDataBinding();
        }
        else if(FormattedFollowUpDataBinding.class.getName().equals(dataBindingName)) {
            return new FormattedFollowUpDataBinding();
        }
        else if(DocumentDataBinding.class.getName().equals(dataBindingName)) {
            return new DocumentDataBinding();
        }
        else {
            return super.getDataBinding(
                dataBindingName
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
                    searchResult.getObjectFinder().refGetPath()
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

    private static final Set<String> CLASSES_WITH_USER_DEFINABLE_QUALIFER =
        new HashSet<String>(Arrays.asList(
            new String[]{
                "org:opencrx:security:realm1:PrincipalGroup",
                "org:opencrx:security:realm1:Principal",                
                "org:opencrx:security:realm1:User",
                "org:opencrx:security:identity:Subject",
                "org:opencrx:kernel:uom1:Uom"
            }
        ));
    
    private static ThreadLocal<Map<String,DateFormat>> cachedDateFormat = new ThreadLocal<Map<String,DateFormat>>() {
        protected synchronized Map<String,DateFormat> initialValue() {
            return new HashMap<String,DateFormat>();
        }
    };
    private static ThreadLocal<Map<String,DateFormat>> cachedTimeFormat = new ThreadLocal<Map<String,DateFormat>>() {
        protected synchronized Map<String,DateFormat> initialValue() {
            return new HashMap<String,DateFormat>();
        }
    };
    private static ThreadLocal<Map<String,DecimalFormat>> cachedDecimalFormat = new ThreadLocal<Map<String,DecimalFormat>>() {
        protected synchronized Map<String,DecimalFormat> initialValue() {
            return new HashMap<String,DecimalFormat>();
        }
    };
    
}

//--- End of File -----------------------------------------------------------

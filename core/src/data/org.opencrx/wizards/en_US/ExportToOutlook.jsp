<%/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: ExportToOutlook.jsp,v 1.10 2008/02/25 10:14:26 cmu Exp $
 * Description: openCRX outlook exporter - contact
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/25 10:14:26 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2005, CRIXP Corp., Switzerland
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
 */%>

<%@  page contentType= "text/html;charset=UTF-8" language="java" pageEncoding= "UTF-8" %>
<%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.compatibility.base.naming.*,
org.openmdx.compatibility.base.dataprovider.cci.*,
org.openmdx.application.log.*
" %><%
  request.setCharacterEncoding("UTF-8");
  ApplicationContext app = (ApplicationContext)session.getValue("ObjectInspectorServlet.ApplicationContext");
  ShowObjectView view = (ShowObjectView)session.getValue("ObjectInspectorServlet.View");
  Texts_1_0 texts = app.getTexts();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
  <title>openCRX - Export Contact to MS Outlook</title>
  <meta name="UNUSEDlabel" content="Export to MS Outlook">
  <meta name="UNUSEDtoolTip" content="Export to MS Outlook">
  <meta name="targetType" content="_blank">
  <meta name="forClass" content="org:opencrx:kernel:account1:Contact">
  <meta name="order" content="org:opencrx:kernel:account1:Contact:exportToOutlook">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link href="../../_style/n2default.css" rel="stylesheet" type="text/css">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">

  <script language="vbscript">
  Sub AddContact()
    Const olContactItem = 2
    Dim olkApp, olkNS, olkContact
    Set olkApp = CreateObject("Outlook.Application")

    if (olkApp.Explorers.Count >= 0) then
      Set olkNS = olkApp.GetNamespace("MAPI")
      olkNS.Logon "Outlook"
      Set olkContact = olkApp.CreateItem(olContactItem)
      With olkContact
<%
        try {
          Codes codes = app.getCodes();
          RefPackage_1_0 dataPkg = app.getDataPackage();
          org.opencrx.kernel.account1.jmi1.Account1Package accountPkg =
            (org.opencrx.kernel.account1.jmi1.Account1Package)dataPkg.refPackage(
          	  org.opencrx.kernel.account1.jmi1.Account1Package.class.getName()
            );
          String objectXri = request.getParameter("xri");
          if (objectXri == null) {
            objectXri = view.getObjectReference().refMofId();
          }

       	  try {
              org.opencrx.kernel.account1.jmi1.Contact contact =
            	  (org.opencrx.kernel.account1.jmi1.Contact)dataPkg.refObject(objectXri);
              int salutationCode = 0;
              try {
                salutationCode = contact.getSalutationCode();
              } catch (Exception e) {};
%>
              .Account = "<%= (contact.getIdentity() == null) ? "" : contact.getIdentity() %>"
              .Title = "<%= (contact.getSalutation() == null) ? ((salutationCode == 0) ? "" : (String)(codes.getLongText("org:opencrx:kernel:account1:Contact:salutationCode", app.getCurrentLocaleAsIndex(), true, true).get(new Short((short)salutationCode)))) : contact.getSalutation() %>"
              .FirstName = "<%= (contact.getFirstName() == null) ? "" : contact.getFirstName() %>"
              .MiddleName = "<%= (contact.getMiddleName() == null) ? "" : contact.getMiddleName() %>"
              .LastName = "<%= (contact.getLastName() == null) ? "" : contact.getLastName() %>"
              .Suffix = "<%= (contact.getSuffix() == null) ? "" : contact.getSuffix() %>"
              .NickName = "<%= (contact.getNickName() == null) ? "" : contact.getNickName() %>"
              .JobTitle = "<%= (contact.getJobTitle() == null) ? "" : contact.getJobTitle() %>"
              .CompanyName = "<%= (contact.getOrganization() == null) ? "" : contact.getOrganization() %>"
              .Department = "<%= (contact.getDepartment() == null) ? "" : contact.getDepartment() %>"
              .GovernmentIDNumber = "<%= (contact.getGovernmentId() == null) ? "" : contact.getGovernmentId() %>"
              .Children = "<%
  						for(
  						    Iterator k = contact.getChildrenNames().iterator();
  						    k.hasNext();
  						) {
  						  %><%= (String)k.next() %>, <%
  						}
   						%>"
<%
      				/**
      				 * Addresses
      				 */
    					for(
    					    Iterator i = contact.getAddress().iterator();
    					    i.hasNext();
    					) {
    						org.opencrx.kernel.account1.jmi1.AccountAddress address =
    							(org.opencrx.kernel.account1.jmi1.AccountAddress)i.next();
    					  if (address instanceof org.opencrx.kernel.account1.jmi1.PostalAddress) {
    					    //
    					    // Postal Address
    					    //
    					    org.opencrx.kernel.account1.jmi1.PostalAddress postalAddress = (org.opencrx.kernel.account1.jmi1.PostalAddress) address;
    					    if (address.getUsage().size() == 0) {
%>                  .OtherAddressStreet = "<%
        						for(
        						    Iterator k = postalAddress.getPostalAddressLine().iterator();
        						    k.hasNext();
        						) {
        						  %><%= (String)k.next() %>" & vbCrLf & "<%
        						}
        						for(
        						    Iterator k = postalAddress.getPostalStreet().iterator();
        						    k.hasNext();
        						) {
        						  %><%= (String)k.next() %>" & vbCrLf & "<%
        						}
        						%>"
                    .OtherAddressCity          = "<%= (postalAddress.getPostalCity() == null)  ? "" : postalAddress.getPostalCity() %>"
                    .OtherAddressCountry       = "<%= (postalAddress.getPostalCountry() == 0)  ? "" : (String)(codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", app.getCurrentLocaleAsIndex(), true, true).get(new Short(postalAddress.getPostalCountry()))) %>"
                    .OtherAddressPostalCode    = "<%= (postalAddress.getPostalCode() == null)  ? "" : postalAddress.getPostalCode() %>"
                    .OtherAddressState         = "<%= (postalAddress.getPostalState() == null) ? "" : postalAddress.getPostalState() %>"
<%
                  }
      						for(
      						    Iterator j = address.getUsage().iterator();
      						    j.hasNext();
      						) {
                    String prefix = "";
    						    Number usage = (Number)j.next();
                    switch (usage.intValue()) {
                      case  100: prefix = "Home";     break; // Private
                      case  300: prefix = "Business"; break; // Primary
                      case  310: prefix = "Home";     break; // Secondary
                      case  400: prefix = "Home";     break; // Home
                      case  450: prefix = "Other";    break; // Home Other
                      case  500: prefix = "Business"; break; // Business
                      case  550: prefix = "Other";    break; // Business Other
                      case  600: prefix = "Business"; break; // Company
                      case  650: prefix = "Other";    break; // Company Other
                      case 1800: prefix = "Other";    break; // Other
                      default  :                      break; // 700=Assistant, 800=Deputy, ...
                    }
                    if (prefix.length() > 0) {
%>                    .<%= prefix %>AddressStreet = "<%
          						for(
          						    Iterator k = postalAddress.getPostalAddressLine().iterator();
          						    k.hasNext();
          						) {
          						  %><%= (String)k.next() %>" & vbCrLf & "<%
          						}
          						for(
          						    Iterator k = postalAddress.getPostalStreet().iterator();
          						    k.hasNext();
          						) {
          						  %><%= (String)k.next() %>" & vbCrLf & "<%
          						}
          						%>"
                      .<%= prefix %>AddressCity          = "<%= (postalAddress.getPostalCity() == null)  ? "" : postalAddress.getPostalCity() %>"
                      .<%= prefix %>AddressCountry       = "<%= (postalAddress.getPostalCountry() == 0)  ? "" : (String)(codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", app.getCurrentLocaleAsIndex(), true, true).get(new Short(postalAddress.getPostalCountry()))) %>"
                      .<%= prefix %>AddressPostalCode    = "<%= (postalAddress.getPostalCode() == null)  ? "" : postalAddress.getPostalCode() %>"
                      .<%= prefix %>AddressState         = "<%= (postalAddress.getPostalState() == null) ? "" : postalAddress.getPostalState() %>"
<%
                    }
      						}
    					  }
    					  if (address instanceof org.opencrx.kernel.account1.jmi1.EmailAddress) {
    					    //
    					    // E-mail Address
    					    //
    					    org.opencrx.kernel.account1.jmi1.EmailAddress emailAddress = (org.opencrx.kernel.account1.jmi1.EmailAddress) address;
    					    if (address.getUsage().size() == 0) {
%>                  .Email2Address = "<%= (emailAddress.getEmailAddress() == null) ? "" : emailAddress.getEmailAddress() %>"
<%
                  }
      						for(
      						    Iterator j = address.getUsage().iterator();
      						    j.hasNext();
      						) {
    						    Number usage = (Number)j.next();
                    switch (usage.intValue()) {
                      case 300: %> .Email1Address <% break; // Primary
                      case 310: %> .Email2Address <% break; // Secondary
                      default : %> .Email3Address <% break; // Other
                    }
%>                  = "<%= (emailAddress.getEmailAddress() == null) ? "" : emailAddress.getEmailAddress() %>"
<%
      						}
    					  }
    					  if (address instanceof org.opencrx.kernel.account1.jmi1.WebAddress) {
    					    //
    					    // Web Address
    					    //
    					    org.opencrx.kernel.account1.jmi1.WebAddress webAddress = (org.opencrx.kernel.account1.jmi1.WebAddress) address;
%>                .WebPage = "<%= (webAddress.getWebUrl() == null) ? "" : webAddress.getWebUrl() %>"
<%
    					  }
    					  if (address instanceof org.opencrx.kernel.account1.jmi1.PhoneNumber) {
    					    // Phone Number
    					    org.opencrx.kernel.account1.jmi1.PhoneNumber phoneNumber = (org.opencrx.kernel.account1.jmi1.PhoneNumber) address;
    					    if (address.getUsage().size() == 0) {
%>                  .OtherTelephoneNumber = "<%= (phoneNumber.getPhoneNumberFull() == null) ? "" : phoneNumber.getPhoneNumberFull() %>"
<%
                  }
      						for(
      						    Iterator j = address.getUsage().iterator();
      						    j.hasNext();
      						) {
    						    Number usage = (Number)j.next();
                    switch (usage.intValue()) {
                      case  100: %> .HomeTelephoneNumber        <% break; // Private
                      case  150: %> .HomeFaxNumber              <% break; // Private Fax
                      case  200: %> .MobileTelephoneNumber      <% break; // Mobile
                      case  300: %> .PrimaryTelephoneNumber     <% break; // Primary
                      case  310: %> .OtherTelephoneNumber       <% break; // Secondary
                      case  400: %> .HomeTelephoneNumber        <% break; // Home
                      case  410: %> .HomeTelephoneNumber        <% break; // Home Direct
                      case  420: %> .HomeTelephoneNumber        <% break; // Home Main
                      case  430: %> .HomeFaxNumber              <% break; // Home Fax
                      case  440: %> .MobileTelephoneNumber      <% break; // Home Mobile
                      case  450: %> .Home2TelephoneNumber       <% break; // Home Other
                      case  500: %> .BusinessTelephoneNumber    <% break; // Business
                      case  510: %> .BusinessTelephoneNumber    <% break; // Business Direct
                      case  520: %> .CompanyMainTelephoneNumber <% break; // Business Main
                      case  530: %> .BusinessFaxNumber          <% break; // Business Fax
                      case  540: %> .MobileTelephoneNumber      <% break; // Business Mobile
                      case  550: %> .Business2TelephoneNumber   <% break; // Business Other
                      case  600: %> .BusinessTelephoneNumber    <% break; // Company
                      case  610: %> .BusinessTelephoneNumber    <% break; // Company Direct
                      case  620: %> .CompanyMainTelephoneNumber <% break; // Company Main
                      case  630: %> .BusinessFaxNumber          <% break; // Company Fax
                      case  640: %> .MobileTelephoneNumber      <% break; // Company Mobile
                      case  650: %> .Business2TelephoneNumber   <% break; // Company Other
                      case  700: %> .AssistantTelephoneNumber   <% break; // Assistant
                      case  900: %> .CarTelephoneNumber         <% break; // Car
                      case 1000: %> .RadioTelephoneNumber       <% break; // Radio
                      case 1200: %> .ISDNNumber                 <% break; // ISDN
                      case 1300: %> .PagerNumber                <% break; // Pager
                      case 1500: %> .CallbackTelephoneNumber    <% break; // Callback
                      case 1600: %> .TTYTDDTelephoneNumber      <% break; // TTY/TTD
                      case 1700: %> .TelexNumber                <% break; // Telex
                      case 2000: %> .OtherFaxNumber             <% break; // Fax
                      default  : %> .OtherTelephoneNumber       <% break; // 710=AssistantFax, 720=AssistantMobile, 800=Deputy, 810=DeputyFax, 820=DeputyMobile
                                                                          // 1100=Cable, 1400=Secure, 1420=Encrypted, 1800=Other, 1900=Emergency
                    }
%>                  = "<%= (phoneNumber.getPhoneNumberFull() == null) ? "" : phoneNumber.getPhoneNumberFull() %>"
<%
      						}
    					  }
              }

         	    org.opencrx.kernel.account1.jmi1.Account account = contact.getAssistant();
        	    if((account != null) && (account.getFullName().length() > 0)) {
%>
                .AssistantName = "<%= (account.getFullName() == null) ? "" : account.getFullName() %>"
<%
              }
         	    account = contact.getReportsTo();
        	    if((account != null) && (account.getFullName().length() > 0)) {
%>
                .ManagerName = "<%= (account.getFullName() == null) ? "" : account.getFullName() %>"
<%
              }
              try {
          	    if(contact.getPreferredSpokenLanguage() != 0) {
%>
                  .Language = "<%= (String)(codes.getLongText("org:opencrx:kernel:account1:Contact:preferredSpokenLanguage", app.getCurrentLocaleAsIndex(), true, true).get(new Short(contact.getPreferredSpokenLanguage()))) %>"
<%
                }
              } catch (Exception e) {}
              try {
          	    if(contact.getGender() != 0) {
%>
                  .Gender = "<%= (contact.getGender() == 1) ? "2" : "1" %>"
<%
                }
              } catch (Exception e) {}
              String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
              java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
              sdf.setTimeZone(TimeZone.getDefault());
              if ((contact.getBirthdate() != null) && (contact.getBirthdate() != null)) {
%>
                .Birthday = "<%= sdf.format(contact.getBirthdate()) %>"
<%
              }
              if ((contact.getAnniversary() != null) && (contact.getAnniversary() != null)) {
%>
                .Anniversary = "<%= sdf.format(contact.getAnniversary()) %>"
<%
              }
%>
              .Display()
              //.Save
<%
          }
          catch(Exception e) {
  		    	try {
          		//dataPkg.refRollback();
              } catch(Exception e0) {}
  		        throw e;
	        }
        }
      	catch (Exception ex) {
    	    out.println("<p><b>!! Failed !!<br><br>The following exception occur:</b><br><br>");
    	    ex.printStackTrace(new PrintWriter(out));
        }
%>
      End With
    end if
    if (olkApp.Explorers.Count = 0) then
      // alert("Outlook Not Open")
      olkApp.Quit()
    end if
  End Sub
  </script>
  <link rel='shortcut icon' href='../../images/favicon.ico' />
</head>

<body>
  <p>please note:
  <ul>
    <li>this wizard requires <b>MS Internet Explorer</b> and <b>MS Outlook</b></li>
    <li>the openCRX Server must be a trusted site</li>
  </ul>
  only if the above conditions are met the ActiveX control required to create MS Outlook Contacts can be started</p>
  <p>it is safe to close this page</p>
  <script language="vbscript">
    AddContact
    close
  </script>
</body>

</html>

<%
/*
// see http://msdn.microsoft.com/library/default.asp?url=/library/en-us/vbaol11/html/olobjContactItem_HV05247818.asp
X.Account
.Actions
X.Anniversary
.Application
X.AssistantName
X.AssistantTelephoneNumber
.Attachments
X.AutoResolvedWinner
.BillingInformation
X.Birthday
.Body
X.Business2TelephoneNumber
X.BusinessAddress
X.BusinessAddressCity
X.BusinessAddressCountry
X.BusinessAddressPostalCode
.BusinessAddressPostOfficeBox
X.BusinessAddressState
X.BusinessAddressStreet
X.BusinessFaxNumber
-.BusinessHomePage
X.BusinessTelephoneNumber
X.CallbackTelephoneNumber
X.CarTelephoneNumber
.Categories
X.Children
.Class
.Companies
-.CompanyAndFullName
.CompanyLastFirstNoSpace
.CompanyLastFirstSpaceOnly
X.CompanyMainTelephoneNumber
X.CompanyName
.ComputerNetworkName
.Conflicts
.ConversationIndex
.ConversationTopic
.CreationTime
.CustomerID
X.Department
.DownloadState
X.Email1Address
-.Email1AddressType
-.Email1DisplayName
-.Email1EntryID
X.Email2Address
-.Email2AddressType
-.Email2DisplayName
-.Email2EntryID
X.Email3Address
-.Email3AddressType
-.Email3DisplayName
-.Email3EntryID
-.EntryID
-.FileAs
X.FirstName
.FormDescription
.FTPSite
-.FullName
-.FullNameAndCompany
X.Gender
.GetInspector
.GovernmentIDNumber
.HasPicture
.Hobby
X.Home2TelephoneNumber
X.HomeAddress
X.HomeAddressCity
X.HomeAddressCountry
X.HomeAddressPostalCode
-.HomeAddressPostOfficeBox
X.HomeAddressState
X.HomeAddressStreet
X.HomeFaxNumber
X.HomeTelephoneNumber
.IMAddress
.Importance
.Initials
.InternetFreeBusyAddress
.IsConflict
X.ISDNNumber
.ItemProperties
X.JobTitle
.Journal
X.Language
.LastFirstAndSuffix
.LastFirstNoSpace
.LastFirstNoSpaceAndSuffix
.LastFirstNoSpaceCompany
.LastFirstSpaceOnly
.LastFirstSpaceOnlyCompany
.LastModificationTime
X.LastName
.LastNameAndFirstName
.Links
-.MailingAddress
-.MailingAddressCity
-.MailingAddressCountry
-.MailingAddressPostalCode
-.MailingAddressPostOfficeBox
-.MailingAddressState
-.MailingAddressStreet
X.ManagerName
.MarkForDownload
.MessageClass
X.MiddleName
.Mileage
X.MobileTelephoneNumber
.NetMeetingAlias
.NetMeetingServer
X.NickName
.NoAging
.OfficeLocation
.OrganizationalIDNumber
X.OtherAddress
X.OtherAddressCity
X.OtherAddressCountry
X.OtherAddressPostalCode
X.OtherAddressPostOfficeBox
X.OtherAddressState
X.OtherAddressStreet
X.OtherFaxNumber
X.OtherTelephoneNumber
.OutlookInternalVersion
.OutlookVersion
X.PagerNumber
.Parent
-.PersonalHomePage
X.PrimaryTelephoneNumber
.Profession
X.RadioTelephoneNumber
.ReferredBy
.Saved
.SelectedMailingAddress
.Sensitivity
.Session
.Size
.Spouse
.Subject
X.Suffix
X.TelexNumber
X.Title
X.TTYTDDTelephoneNumber
-.UnRead
-.User1
-.User2
-.User3
-.User4
-.UserCertificate
-.UserProperties
X.WebPage
-.YomiCompanyName
-.YomiFirstName
-.YomiLastName
*/
%>
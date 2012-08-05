<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     openmdx, http://www.openmdx.org/
 * Name:        $Id: Account_Quick.jsp,v 1.7 2008/02/25 10:14:26 cmu Exp $
 * Description: sample wizard: create account and some address objects
 * Revision:    $Revision: 1.7 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/02/25 10:14:26 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2005-2008, CRIXP Corp., Switzerland
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
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
org.openmdx.kernel.id.cci.*,
org.openmdx.kernel.id.*,
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
	Texts_1_0 texts = app.getTexts();
	Codes codes = app.getCodes();
	ShowObjectView showView = (ShowObjectView)session.getValue("ObjectInspectorServlet.View");
	UUIDGenerator uuids = UUIDs.getGenerator();

  String formName   = "Account_Quick";
	String wizardName = formName + ".jsp";

  // Classes
  String ACCOUNT_CLASS = "org:opencrx:kernel:account1:Account";
  String CONTACT_CLASS = "org:opencrx:kernel:account1:Contact";
  String GROUP_CLASS = "org:opencrx:kernel:account1:Group";
  String ABSTRACTGROUP_CLASS = "org:opencrx:kernel:account1:AbstractGroup";
  String LEGALENTITY_CLASS = "org:opencrx:kernel:account1:LegalEntity";
  String EMAIL_CLASS = "org:opencrx:kernel:account1:EMailAddress";
  String PHONENUMBER_CLASS = "org:opencrx:kernel:account1:PhoneNumber";
  String POSTALADDRESS_CLASS = "org:opencrx:kernel:account1:PostalAddress";
  String WEBADDRESS_CLASS = "org:opencrx:kernel:account1:WebAddress";
  String ROOM_CLASS = "org:opencrx:kernel:account1:Room";

  // Features
  String POSTALADDRESSLINE_FEATURE = "postalAddressLine";
  String POSTALADDRESSSTREET_FEATURE = "postalStreet";
  String POSTALADDRESSCITY_FEATURE = "postalCity";
  String POSTALADDRESSCODE_FEATURE = "postalCode";
  String POSTALADDRESSCOUNTRY_FEATURE = "postalCountry";

  // get Parameters
	boolean actionOk        = request.getParameter("OK.Button") != null;
	boolean actionCancel    = request.getParameter("Cancel.Button") != null;
	boolean initialCall     = (request.getParameter("OK.Button") == null) && (request.getParameter("Cancel.Button") == null);
	boolean editingExisting = request.getParameter("editingExisting") != null; // modified later if object of instance Contact
	String accountClass     = request.getParameter("accountClass");
	String errorMsg         = request.getParameter("errorMsg");

  String objectXri = request.getParameter("xri");
  if ((objectXri == null) || (objectXri != null && objectXri.length() == 0)) {
    objectXri = showView.getObjectReference().refMofId();
  }

  // Get data package. This is the JMI root package to handle
  // openCRX object requests
  RefPackage_1_0 dataPkg = app.getDataPackage();
  RefObject_1_0 obj = (RefObject_1_0)dataPkg.refObject(objectXri);

	if (initialCall) {
	  if (obj instanceof org.opencrx.kernel.account1.jmi1.Contact) {
      editingExisting = true;
      accountClass = CONTACT_CLASS;
    }
	  if (obj instanceof org.opencrx.kernel.account1.jmi1.LegalEntity) {
      editingExisting = true;
      accountClass = LEGALENTITY_CLASS;
    }
  }

  String attributeErrors = "<h1>Attribute Errors</h1><br><br>hint: adapt the placement of customized attributes of the wizard to your customizing<br><br>";
  boolean hasAttributeErrors = false;

	if(actionCancel) {
	  session.setAttribute(wizardName, null);
		Action nextAction = new ObjectReference(obj, app).getSelectObjectAction();
		response.sendRedirect(
			request.getContextPath() + "/" + showView.getEncodedHRef(nextAction, false)
  	);
  	return;
	}

%><!--[if IE]><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><![endif]-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="<%= texts.getDir() %>">
<head>
  <title><%= editingExisting ? app.getTexts().getEditTitle() : app.getTexts().getNewText() %> - <%= accountClass == null ? app.getLabel(ACCOUNT_CLASS) : app.getLabel(accountClass) %></title>
  <meta name="UNUSEDlabel" content="Account Quick">
  <meta name="UNUSEDtoolTip" content="Account Quick">
  <meta name="targetType" content="_self">
  <meta name="forClass" content="org:opencrx:kernel:account1:Segment">
  <meta name="forClass" content="org:opencrx:kernel:account1:Contact">
  <meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
  <meta name="order" content="org:opencrx:kernel:account1:Segment:accountQuick">
  <meta name="order" content="org:opencrx:kernel:account1:Contact:accountQuick">
  <meta name="order" content="org:opencrx:kernel:account1:LegalEntity:accountQuick">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link href="../../_style/colors.css" rel="stylesheet" type="text/css">
  <link href="../../_style/calendar-small.css" rel="stylesheet" type="text/css">
  <!--[if lt IE 7]><script type="text/javascript" src="../../javascript/iehover-fix.js"></script><![endif]-->
  <script language="javascript" type="text/javascript" src="../../javascript/portal-all.js"></script>
  <script language="javascript" type="text/javascript" src="../../javascript/calendar/lang/calendar-<%= app.getCurrentLocaleAsString() %>.js"></script>

  <script language="javascript" type="text/javascript">
    var OF = null;
    try {
      OF = self.opener.OF;
    }
    catch(e) {
      OF = null;
    }
    if(!OF) {
      OF = new ObjectFinder();
    }
  </script>
	<link rel="stylesheet" type="text/css" href="../../_style/ssf.css">
	<link rel="stylesheet" type="text/css" href="../../_style/n2default.css">
  <link rel="shortcut icon" href="../../images/favicon.ico" />
  <style type="text/css" media="all">
    .imglabel {
      text-align: left;
      text-decoration: none;
      white-space: nowrap;
      vertical-align: top;
      padding-top: 0.15em;
      width: 20px;
      overflow: hidden;
    }
    .addressHeader td {
      padding-top:5px;
      padding-bottom:5px;
    }
    .addressHeader SPAN {
      font-weight:bold;
    }
  </style>
</head>
<%

  // References
  String featureReportsTo = "org:opencrx:kernel:account1:Contact:reportsTo";
  String featureAssistant = "org:opencrx:kernel:account1:Contact:assistant";
  String featureDeputy    = "org:opencrx:kernel:account1:Contact:deputy";

  String featureUsageAddress = "org:opencrx:kernel:address1:Addressable:usage";
  String featurePostalCountryCode = "org:opencrx:kernel:address1:PostalAddressable:postalCountry";

  //SortedMap postalCountryCode_longTextsC = codes.getLongText(featurePostalCountryCode, app.getCurrentLocaleAsIndex(), true, false);
  SortedMap postalCountryCode_longTextsT = codes.getLongText(featurePostalCountryCode, app.getCurrentLocaleAsIndex(), false, true);

	// Prepare user defined view
	app.getUiContext().getAssertedInspector(CONTACT_CLASS);
	app.getUiContext().getAssertedInspector(LEGALENTITY_CLASS);
	app.getUiContext().getAssertedInspector(POSTALADDRESS_CLASS);
	//app.getUiContext().getAssertedInspector(PHONENUMBER_CLASS);

  // Attributes
  // -------------- C O N T A C T -------------------------

  String salutationCode_attributeName    = "salutationCode";
  int    salutationCode_tabIndex         = 100;
  String salutationCode_fieldId          = CONTACT_CLASS + ":" + salutationCode_attributeName + "[" + Integer.toString(salutationCode_tabIndex) + "]";
  String salutationCode_label            = null;

  String salutation_attributeName        = "salutation";
  int    salutation_tabIndex             = 200;
  String salutation_fieldId              = CONTACT_CLASS + ":" + salutation_attributeName + "[" + Integer.toString(salutation_tabIndex) + "]";
  String salutation_label                = null;

  String firstName_attributeName         = "firstName";
  int    firstName_tabIndex              = 300;
  String firstName_fieldId               = CONTACT_CLASS + ":" + firstName_attributeName + "[" + Integer.toString(firstName_tabIndex) + "]";
  String firstName_label                 = null;

  String middleName_attributeName        = "middleName";
  int    middleName_tabIndex             = 400;
  String middleName_fieldId              = CONTACT_CLASS + ":" + middleName_attributeName + "[" + Integer.toString(middleName_tabIndex) + "]";
  String middleName_label                = null;

  String lastName_attributeName          = "lastName";
  int    lastName_tabIndex               = 500;
  String lastName_fieldId                = CONTACT_CLASS + ":" + lastName_attributeName + "[" + Integer.toString(lastName_tabIndex) + "]";
  String lastName_label                  = null;

  String aliasName_attributeName         = "aliasName";
  int    aliasName_tabIndex              = 600;
  String aliasName_fieldId               = ACCOUNT_CLASS + ":" + aliasName_attributeName + "[" + Integer.toString(aliasName_tabIndex) + "]";
  String aliasName_label                 = null;

  String prefSLanguage_attributeName     = "preferredSpokenLanguage";
  int    prefSLanguage_tabIndex          = 700;
  String prefSLanguage_fieldId           = CONTACT_CLASS + ":" + prefSLanguage_attributeName + "[" + Integer.toString(prefSLanguage_tabIndex) + "]";
  String prefSLanguage_label             = null;

  String prefWLanguage_attributeName     = "preferredWrittenLanguage";
  int    prefWLanguage_tabIndex          = 800;
  String prefWLanguage_fieldId           = CONTACT_CLASS + ":" + prefWLanguage_attributeName + "[" + Integer.toString(prefWLanguage_tabIndex) + "]";
  String prefWLanguage_label             = null;

  String jobTitle_attributeName          = "jobTitle";
  int    jobTitle_tabIndex               = 1100;
  String jobTitle_fieldId                = CONTACT_CLASS + ":" + jobTitle_attributeName + "[" + Integer.toString(jobTitle_tabIndex) + "]";
  String jobTitle_label                  = null;

  String jobRole_attributeName           = "jobRole";
  int    jobRole_tabIndex                = 1200;
  String jobRole_fieldId                 = CONTACT_CLASS + ":" + jobRole_attributeName + "[" + Integer.toString(jobRole_tabIndex) + "]";
  String jobRole_label                   = null;

  String organization_attributeName      = "organization";
  int    organization_tabIndex           = 1300;
  String organization_fieldId            = CONTACT_CLASS + ":" + organization_attributeName + "[" + Integer.toString(organization_tabIndex) + "]";
  String organization_label              = null;

  String department_attributeName        = "department";
  int    department_tabIndex             = 1400;
  String department_fieldId              = CONTACT_CLASS + ":" + department_attributeName + "[" + Integer.toString(department_tabIndex) + "]";
  String department_label                = null;

  String reportsTo_attributeName         = "reportsTo";
  int    reportsTo_tabIndex              = 1500;
  String reportsTo_fieldId               = CONTACT_CLASS + ":" + reportsTo_attributeName + "[" + Integer.toString(reportsTo_tabIndex) + "]";
  String reportsTo_label                 = null;

  String assistant_attributeName         = "assistant";
  int    assistant_tabIndex              = 1600;
  String assistant_fieldId               = CONTACT_CLASS + ":" + assistant_attributeName + "[" + Integer.toString(assistant_tabIndex) + "]";
  String assistant_label                 = null;

  String deputy_attributeName            = "deputy";
  int    deputy_tabIndex                 = 1700;
  String deputy_fieldId                  = CONTACT_CLASS + ":" + deputy_attributeName + "[" + Integer.toString(deputy_tabIndex) + "]";
  String deputy_label                    = null;

  String birthdate_attributeName         = "birthdate";
  int    birthdate_tabIndex              = 1800;
  String birthdate_fieldId               = CONTACT_CLASS + ":" + birthdate_attributeName + "[" + Integer.toString(birthdate_tabIndex) + "]";
  String birthdate_label                 = null;

  // -------------- L E G A L   E N T I T Y --------------------

  String name_attributeName              = "name";
  int    name_tabIndex                   = 100;
  String name_fieldId                    = ABSTRACTGROUP_CLASS + ":" + name_attributeName + "[" + Integer.toString(name_tabIndex) + "]";
  String name_label                      = null;

/*
  String aliasName_attributeName         = "aliasName";
  String aliasName_customizedField       = ACCOUNT_CLASS + ":Pane:Attr:Tab:25:Group:10:Field:" + aliasName_attributeName;
  int    aliasName_tabIndex              = 600;
  String aliasName_fieldId               = ACCOUNT_CLASS + ":" + aliasName_attributeName + "[" + Integer.toString(aliasName_tabIndex) + "]";
  String aliasName_label                 = null;
*/

  String industry_attributeName          = "industry";
  int    industry_tabIndex               = 700;
  String industry_fieldId                = LEGALENTITY_CLASS + ":" + industry_attributeName + "[" + Integer.toString(industry_tabIndex) + "]";
  String industry_label                  = null;

  String tickerSymbol_attributeName      = "tickerSymbol";
  int    tickerSymbol_tabIndex           = 800;
  String tickerSymbol_fieldId            = LEGALENTITY_CLASS + ":" + tickerSymbol_attributeName + "[" + Integer.toString(tickerSymbol_tabIndex) + "]";
  String tickerSymbol_label              = null;

  String stockExchange_attributeName     = "stockExchange";
  int    stockExchange_tabIndex          = 900;
  String stockExchange_fieldId           = LEGALENTITY_CLASS + ":" + stockExchange_attributeName + "[" + Integer.toString(stockExchange_tabIndex) + "]";
  String stockExchange_label             = null;

  // -------------- A D D R E S S E S -------------------------

  short usageLeftDefault   = (short)500;
  short usage2LeftDefault  = (short)000;
  short usageRightDefault  = (short)400;
  short usage2RightDefault = (short)000;

  if ((accountClass != null) && (accountClass.compareTo(LEGALENTITY_CLASS) == 0)) {
    usageLeftDefault   = (short)0;
    usageRightDefault  = (short)0;
  }

  short[] usageLeft   = {usageLeftDefault,   (short)500};
  short[] usage2Left  = {usage2LeftDefault,  (short)0};
  short[] usageRight  = {usageRightDefault,  (short)400};
  short[] usage2Right = {usage2RightDefault, (short)0};

  short usageFax      = (short)30;
  short usageCell     = (short)200;

%><%@ include file="Account_Quick_overload.config" %><%

  try {usageLeft[0]   = Short.parseShort(request.getParameter("usageLeft"  ));} catch (Exception e) {};
  try {usage2Left[0]  = Short.parseShort(request.getParameter("usage2Left" ));} catch (Exception e) {};
  try {usageRight[0]  = Short.parseShort(request.getParameter("usageRight" ));} catch (Exception e) {};
  try {usage2Right[0] = Short.parseShort(request.getParameter("usage2Right"));} catch (Exception e) {};

  // left
  String addressCWeb_fieldId             = "addressCWeb";
  String addressCWeb_label               = "../../images/WebAddress.gif";
  int    addressCWeb_tabIndex            = 2000;

  String addressCEMail_fieldId           = "addressCEMail";
  String addressCEMail_label             = "../../images/EMailAddress.gif";
  int    addressCEMail_tabIndex          = 2100;

  String addressCPhone_fieldId           = "addressCPhone";
  String addressCPhone_label             = "../../images/PhoneNumber.gif";
  int    addressCPhone_tabIndex          = 2200;

  String addressCFax_fieldId             = "addressCFax";
  String addressCFax_label               = "../../images/Fax.gif";
  int    addressCFax_tabIndex            = 2300;

  String addressCCell_fieldId            = "addressCCell";
  String addressCCell_label              = "../../images/Cell.gif";
  int    addressCCell_tabIndex           = 2400;

  String addressCPLine_fieldId           = "addressCPLine";
  String addressCPLine_label             = "../../images/PostalAddress.gif";
  int    addressCPLine_tabIndex          = 2500;

  String addressCPStreet_fieldId         = "addressCPStreet";
  String addressCPStreet_label           = "../../images/PStreet.gif";
  int    addressCPStreet_tabIndex        = 2600;

  String addressCPCity_fieldId           = "addressCPCity";
  String addressCPCity_label             = "../../images/PCity.gif";
  int    addressCPCity_tabIndex          = 2700;

  String addressCPCode_fieldId           = "addressCPCode";
  String addressCPCode_label             = "../../images/PCode.gif";
  int    addressCPCode_tabIndex          = 2800;

  String addressCPCountry_fieldId        = "addressCPCountry";
  String addressCPCountry_label          = "../../images/PCountry.gif";
  int    addressCPCountry_tabIndex       = 2900;

  // right
  String addressPWeb_fieldId             = "addressPWeb";
  String addressPWeb_label               = "../../images/WebAddress.gif";
  int    addressPWeb_tabIndex            = 3000;

  String addressPEMail_fieldId           = "addressPEMail";
  String addressPEMail_label             = "../../images/EMailAddress.gif";
  int    addressPEMail_tabIndex          = 3100;

  String addressPPhone_fieldId           = "addressPPhone";
  String addressPPhone_label             = "../../images/PhoneNumber.gif";
  int    addressPPhone_tabIndex          = 3200;

  String addressPFax_fieldId             = "addressPFax";
  String addressPFax_label               = "../../images/Fax.gif";
  int    addressPFax_tabIndex            = 3300;

  String addressPCell_fieldId            = "addressPCell";
  String addressPCell_label              = "../../images/Cell.gif";
  int    addressPCell_tabIndex           = 3400;

  String addressPPLine_fieldId           = "addressPPLine";
  String addressPPLine_label             = "../../images/PostalAddress.gif";
  int    addressPPLine_tabIndex          = 3500;

  String addressPPStreet_fieldId         = "addressPPStreet";
  String addressPPStreet_label           = "../../images/PStreet.gif";
  int    addressPPStreet_tabIndex        = 3600;

  String addressPPCity_fieldId           = "addressPPCity";
  String addressPPCity_label             = "../../images/PCity.gif";
  int    addressPPCity_tabIndex          = 3700;

  String addressPPCode_fieldId           = "addressPPCode";
  String addressPPCode_label             = "../../images/PCode.gif";
  int    addressPPCode_tabIndex          = 3800;

  String addressPPCountry_fieldId        = "addressPPCountry";
  String addressPPCountry_label          = "../../images/PCountry.gif";
  int    addressPPCountry_tabIndex       = 3900;

	// Contact
	Attribute salutationCodeAttr = null;
	Attribute salutationAttr = null;
	Attribute firstNameAttr = null;
	Attribute middleNameAttr = null;
	Attribute lastNameAttr = null;
	Attribute aliasNameAttr = null;
	Attribute prefSLanguageAttr = null;
	Attribute prefWLanguageAttr = null;
	Attribute jobTitleAttr = null;
	Attribute jobRoleAttr = null;
	Attribute organizationAttr = null;
	Attribute departmentAttr = null;
	Attribute reportsToAttr = null;
	Attribute assistantAttr = null;
	Attribute deputyAttr = null;
	Attribute birthdateAttr = null;

 	// LegalEntity
	Attribute nameAttr          = null;
	//Attribute aliasNameAttr   = null;
	Attribute industryAttr      = null;
	Attribute tickerSymbolAttr  = null;
	Attribute stockExchangeAttr = null;

 	Map createAccountValues = null;
	UserDefinedView view = (UserDefinedView)session.getValue(wizardName);

	//if (true) {
	if (view == null) {
		createAccountValues = new HashMap();
		view = new UserDefinedView(
			createAccountValues,
			app,
			showView
		);

 		// get Contact attributes
  	try {
    	salutationCodeAttr = view.addAttribute(
			  salutationCode_fieldId,
			  CONTACT_CLASS,
    		salutationCode_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += salutationCode_attributeName + "<br>";
    };
  	try {
    	salutationAttr = view.addAttribute(
			  salutation_fieldId,
			  CONTACT_CLASS,
			  salutation_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += salutation_attributeName + "<br>";
    };
  	try {
    	firstNameAttr = view.addAttribute(
			  firstName_fieldId,
			  CONTACT_CLASS,
    		firstName_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += firstName_attributeName + "<br>";
    };
  	try {
    	middleNameAttr = view.addAttribute(
			  middleName_fieldId,
			  CONTACT_CLASS,
    		middleName_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += middleName_attributeName + "<br>";
    };
  	try {
    	lastNameAttr = view.addAttribute(
			  lastName_fieldId,
			  CONTACT_CLASS,
    		lastName_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += lastName_attributeName + "<br>";
    };
  	try {
    	aliasNameAttr = view.addAttribute(
			  aliasName_fieldId,
			  ACCOUNT_CLASS,
    		aliasName_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += aliasName_attributeName + "<br>";
    };
  	try {
    	prefSLanguageAttr = view.addAttribute(
			  prefSLanguage_fieldId,
			  CONTACT_CLASS,
    		prefSLanguage_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += prefSLanguage_attributeName + "<br>";
    };
  	try {
    	prefWLanguageAttr = view.addAttribute(
			  prefWLanguage_fieldId,
			  CONTACT_CLASS,
    		prefWLanguage_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += prefWLanguage_attributeName + "<br>";
    };
  	try {
    	jobTitleAttr = view.addAttribute(
			  jobTitle_fieldId,
			  CONTACT_CLASS,
    		jobTitle_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += jobTitle_attributeName + "<br>";
    };
  	try {
    	jobRoleAttr = view.addAttribute(
			  jobRole_fieldId,
			  CONTACT_CLASS,
    		jobRole_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += jobRole_attributeName + "<br>";
    };
  	try {
    	organizationAttr = view.addAttribute(
			  organization_fieldId,
			  CONTACT_CLASS,
    		organization_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += organization_attributeName + "<br>";
    };
  	try {
    	departmentAttr = view.addAttribute(
			  department_fieldId,
			  CONTACT_CLASS,
    		department_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += department_attributeName + "<br>";
    };
  	try {
    	reportsToAttr = view.addAttribute(
		    reportsTo_fieldId,
		    CONTACT_CLASS,
    		reportsTo_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += reportsTo_attributeName + "<br>";
    };
  	try {
    	assistantAttr = view.addAttribute(
			  assistant_fieldId,
			  CONTACT_CLASS,
    		assistant_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += assistant_attributeName + "<br>";
    };
  	try {
    	deputyAttr = view.addAttribute(
			  deputy_fieldId,
			  CONTACT_CLASS,
    		deputy_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += deputy_attributeName + "<br>";
    };
  	try {
    	birthdateAttr = view.addAttribute(
			  birthdate_fieldId,
			  CONTACT_CLASS,
    		birthdate_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += birthdate_attributeName + "<br>";
    };
 		// get LegalEntity attributes
  	try {
    	nameAttr = view.addAttribute(
			  name_fieldId,
			  ABSTRACTGROUP_CLASS,
    		name_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += name_attributeName + "<br>";
    };
  	try {
    	industryAttr = view.addAttribute(
			  industry_fieldId,
			  LEGALENTITY_CLASS,
    		industry_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += industry_attributeName + "<br>";
    };
  	try {
    	tickerSymbolAttr = view.addAttribute(
			  tickerSymbol_fieldId,
			  LEGALENTITY_CLASS,
    		tickerSymbol_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += tickerSymbol_attributeName + "<br>";
    };
  	try {
    	stockExchangeAttr = view.addAttribute(
        stockExchange_fieldId,
		    LEGALENTITY_CLASS,
    		stockExchange_attributeName,
    		createAccountValues
    	);
    } catch (Exception e) {
      hasAttributeErrors = true;
      attributeErrors += stockExchange_attributeName + "<br>";
    };
  }
  else {
	  createAccountValues = (Map)view.getObject();
	  // Contact
    salutationCodeAttr  = view.getAttribute(salutationCode_fieldId);
    salutationAttr      = view.getAttribute(salutation_fieldId);
    firstNameAttr       = view.getAttribute(firstName_fieldId);
    middleNameAttr      = view.getAttribute(middleName_fieldId);
    lastNameAttr        = view.getAttribute(lastName_fieldId);
    aliasNameAttr       = view.getAttribute(aliasName_fieldId);
    prefSLanguageAttr   = view.getAttribute(prefSLanguage_fieldId);
    prefWLanguageAttr   = view.getAttribute(prefWLanguage_fieldId);
    jobTitleAttr        = view.getAttribute(jobTitle_fieldId);
    jobRoleAttr         = view.getAttribute(jobRole_fieldId);
    organizationAttr    = view.getAttribute(organization_fieldId);
    departmentAttr      = view.getAttribute(department_fieldId);
    reportsToAttr       = view.getAttribute(reportsTo_fieldId);
    assistantAttr       = view.getAttribute(assistant_fieldId);
    deputyAttr          = view.getAttribute(deputy_fieldId);
    birthdateAttr       = view.getAttribute(birthdate_fieldId);
	  // LegalEntity
    nameAttr            = view.getAttribute(name_fieldId);
    //aliasNameAttr     = view.getAttribute(aliasName_fieldId);
    industryAttr        = view.getAttribute(industry_fieldId);
    tickerSymbolAttr    = view.getAttribute(tickerSymbol_fieldId);
    stockExchangeAttr   = view.getAttribute(stockExchange_fieldId);
  }

  if (hasAttributeErrors) {
%>
    <body>
      <table class="tableError">
        <tr>
          <td class="cellErrorLeft">Error</td>
          <td class="cellErrorRight">
            <%= attributeErrors %>
          </td>
        </tr>
      </table>
      <form name="<%= formName %>" accept-charset="UTF-8" method="POST" action="<%= wizardName %>">
        <br>
        <INPUT type="Submit" name="Cancel.Button" tabindex="9010" value="<%= app.getTexts().getCancelTitle() %>" />
      </form>
    </body>
    </html>
<%
	session.setAttribute(wizardName, null);
	return;
  }

	HtmlPage p = HtmlPageFactory.openPage(
		view,
		request,
		out
	);
	p.setProperty(HtmlPage.PROPERTY_FORM_ID, formName);
	p.setResourcePathPrefix("../../");

	app.getPortalExtension().updateObject(
		createAccountValues,
		view.getAsParameterMap(
			request.getParameterMap(),
			new String[]{
			  // Contact
			  salutationCode_fieldId,
			  salutation_fieldId,
			  firstName_fieldId,
			  middleName_fieldId,
				lastName_fieldId,
				aliasName_fieldId,
				prefSLanguage_fieldId,
				prefWLanguage_fieldId,
				jobTitle_fieldId,
				jobRole_fieldId,
				organization_fieldId,
				department_fieldId,
				reportsTo_fieldId,
				reportsTo_fieldId + ".Title",
				assistant_fieldId,
				assistant_fieldId + ".Title",
				deputy_fieldId,
				deputy_fieldId + ".Title",
				birthdate_fieldId,
				// LegalEntity
				name_fieldId,
				//aliasName_fieldId,
				industry_fieldId,
				tickerSymbol_fieldId,
				stockExchange_fieldId
			}
		),
		view.getAsFieldMap(
			new Attribute[]{
			  // Contact
			  salutationCodeAttr,
			  salutationAttr,
			  firstNameAttr,
			  middleNameAttr,
				lastNameAttr,
				aliasNameAttr,
				prefSLanguageAttr,
				prefWLanguageAttr,
				jobTitleAttr,
				jobRoleAttr,
				organizationAttr,
				departmentAttr,
				reportsToAttr,
				assistantAttr,
				deputyAttr,
				birthdateAttr,
				// LegalEntity
				nameAttr,
				//aliasNameAttr,
				industryAttr,
				tickerSymbolAttr,
				stockExchangeAttr
			}
		),
		app,
		app.getDataPackage()
	);

  // autocompleter attributes
	String reportsToTitle    = request.getParameter(reportsTo_fieldId + ".Title");
	String reportsToValue    = request.getParameter(reportsTo_fieldId);

	String assistantTitle    = request.getParameter(assistant_fieldId + ".Title");
	String assistantValue    = request.getParameter(assistant_fieldId);

	String deputyTitle       = request.getParameter(deputy_fieldId + ".Title");
	System.out.println("deputy.Title=" + deputyTitle);
	String deputyValue       = request.getParameter(deputy_fieldId);
	System.out.println("deputy.Xri=" + deputyValue);

  if (initialCall && editingExisting) {
    if ((accountClass != null) && (accountClass.compareTo(CONTACT_CLASS) == 0)) {
  	  // load Contact attributes from calling Contact
  	  org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)obj;
  	  short codeValue = (short)0; try {codeValue = contact.getSalutationCode();} catch (Exception e) {}
      createAccountValues.put(salutationCodeAttr.getName(), new Short((short)codeValue));
      createAccountValues.put(salutationAttr.getName(),     contact.getSalutation());
      createAccountValues.put(firstNameAttr.getName(),      contact.getFirstName());
      createAccountValues.put(middleNameAttr.getName(),     contact.getMiddleName());
      createAccountValues.put(lastNameAttr.getName(),       contact.getLastName());
      createAccountValues.put(aliasNameAttr.getName(),      contact.getAliasName());
  	  codeValue = (short)0; try {codeValue = contact.getPreferredSpokenLanguage();} catch (Exception e) {}
      createAccountValues.put(prefSLanguageAttr.getName(),  new Short((short)codeValue));
  	  codeValue = (short)0; try {codeValue = contact.getPreferredWrittenLanguage();} catch (Exception e) {}
      createAccountValues.put(prefWLanguageAttr.getName(),  new Short((short)codeValue));
      createAccountValues.put(jobTitleAttr.getName(),       contact.getJobTitle());
      createAccountValues.put(jobRoleAttr.getName(),        contact.getJobRole());
      createAccountValues.put(organizationAttr.getName(),   contact.getOrganization());
      createAccountValues.put(departmentAttr.getName(),     contact.getDepartment());
      createAccountValues.put(birthdateAttr.getName(),      contact.getBirthdate());
      createAccountValues.put(reportsToAttr.getName(),      contact.getReportsTo());
      createAccountValues.put(assistantAttr.getName(),      contact.getAssistant());
      createAccountValues.put(deputyAttr.getName(),         contact.getDeputy());
      // autocompleter attributes
/*
      try {
        if (contact.getReportsTo() != null) {
          ObjectReference ref  = new ObjectReference(contact.getReportsTo(), app);
        	reportsToTitle = ref.getTitle();
        	reportsToValue = contact.getReportsTo().refMofId();
        }
      } catch (Exception e) {}
      try {
        if (contact.getAssistant() != null) {
          ObjectReference ref  = new ObjectReference(contact.getAssistant(), app);
        	assistantTitle = ref.getTitle();
        	assistantValue = contact.getAssistant().refMofId();
        }
      } catch (Exception e) {}
      try {
        if (contact.getDeputy() != null) {
          ObjectReference ref  = new ObjectReference(contact.getDeputy(), app);
        	deputyTitle = ref.getTitle();
        	deputyValue = contact.getDeputy().refMofId();
        }
      } catch (Exception e) {}
*/
    }
    if ((accountClass != null) && (accountClass.compareTo(LEGALENTITY_CLASS) == 0)) {
  	  // load LegalEntity attributes from calling LegalEntity
  	  org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = (org.opencrx.kernel.account1.jmi1.LegalEntity)obj;
      createAccountValues.put(nameAttr.getName(),          legalEntity.getName());
      createAccountValues.put(aliasNameAttr.getName(),     legalEntity.getAliasName());
  	  short codeValue = (short)0; try {codeValue = legalEntity.getIndustry();} catch (Exception e) {}
      createAccountValues.put(industryAttr.getName(), new Short((short)codeValue));
      createAccountValues.put(tickerSymbolAttr.getName(),  legalEntity.getTickerSymbol());
      createAccountValues.put(stockExchangeAttr.getName(), legalEntity.getStockExchange());
    }
	}

  // Contact
  Short  salutationCode    = (Short)createAccountValues.get(salutationCodeAttr.getName());
  String salutation        = (String)createAccountValues.get(salutationAttr.getName());
  String firstName         = (String)createAccountValues.get(firstNameAttr.getName());
  String middleName        = (String)createAccountValues.get(middleNameAttr.getName());
  String lastName          = (String)createAccountValues.get(lastNameAttr.getName());
  String aliasName         = (String)createAccountValues.get(aliasNameAttr.getName());
  Short  prefSLanguage     = (Short)createAccountValues.get(prefSLanguageAttr.getName());
  Short  prefWLanguage     = (Short)createAccountValues.get(prefWLanguageAttr.getName());
  String jobTitle          = (String)createAccountValues.get(jobTitleAttr.getName());
  String jobRole           = (String)createAccountValues.get(jobRoleAttr.getName());
  String organization      = (String)createAccountValues.get(organizationAttr.getName());
  String department        = (String)createAccountValues.get(departmentAttr.getName());
  java.util.Date birthdate = (java.util.Date)createAccountValues.get(birthdateAttr.getName());

  // autocompleter attributes
  RefObject_1_0 reportsTo = (RefObject_1_0)createAccountValues.get(reportsToAttr.getName());
  RefObject_1_0 assistant = (RefObject_1_0)createAccountValues.get(assistantAttr.getName());
  RefObject_1_0 deputy    = (RefObject_1_0)createAccountValues.get(deputyAttr.getName());

  // LegalEntity
  String name              = (String)createAccountValues.get(nameAttr.getName());
  //String aliasName       = (String)createAccountValues.get(aliasNameAttr.getName());
  Short  industry          = (Short)createAccountValues.get(industryAttr.getName());
  String tickerSymbol      = (String)createAccountValues.get(tickerSymbolAttr.getName());
  String stockExchange     = (String)createAccountValues.get(stockExchangeAttr.getName());

  // Addresses
  String addressCWeb       = request.getParameter(addressCWeb_fieldId);
  String addressCEMail     = request.getParameter(addressCEMail_fieldId);
  String addressCPhone     = request.getParameter(addressCPhone_fieldId);
  String addressCFax       = request.getParameter(addressCFax_fieldId);
  String addressCCell      = request.getParameter(addressCCell_fieldId);
  String addressCPLine     = request.getParameter(addressCPLine_fieldId);
  String addressCPStreet   = request.getParameter(addressCPStreet_fieldId);
  String addressCPCity     = request.getParameter(addressCPCity_fieldId);
  String addressCPCode     = request.getParameter(addressCPCode_fieldId);
  String addressCPCountry  = request.getParameter(addressCPCountry_fieldId);

  String addressPWeb       = request.getParameter(addressPWeb_fieldId);
  String addressPEMail     = request.getParameter(addressPEMail_fieldId);
  String addressPPhone     = request.getParameter(addressPPhone_fieldId);
  String addressPFax       = request.getParameter(addressPFax_fieldId);
  String addressPCell      = request.getParameter(addressPCell_fieldId);
  String addressPPLine     = request.getParameter(addressPPLine_fieldId);
  String addressPPStreet   = request.getParameter(addressPPStreet_fieldId);
  String addressPPCity     = request.getParameter(addressPPCity_fieldId);
  String addressPPCode     = request.getParameter(addressPPCode_fieldId);
  String addressPPCountry  = request.getParameter(addressPPCountry_fieldId);

  session.setAttribute(wizardName, view);

%>
<body onload="initPage();">

<%
	try {
    Path objectPath = new Path(showView.getObjectReference().refMofId());
    String providerName = objectPath.get(2);
    String segmentName = objectPath.get(4);
		if(
        actionOk &&
        ((
            (accountClass != null) && (accountClass.compareTo(CONTACT_CLASS) == 0) &&
            (lastName != null) && (lastName.length() > 0)
          ) ||
          (
            (accountClass != null) && (accountClass.compareTo(LEGALENTITY_CLASS) == 0) &&
            (name != null) && (name.length() > 0)
        ))
		  ) {

        // Get account1 package
		    org.opencrx.kernel.account1.jmi1.Account1Package accountPkg =
		    	(org.opencrx.kernel.account1.jmi1.Account1Package)dataPkg.refPackage(
		    		org.opencrx.kernel.account1.jmi1.Account1Package.class.getName()
		    	);

        // Get account segment
        org.opencrx.kernel.account1.jmi1.Segment accountSegment =
          (org.opencrx.kernel.account1.jmi1.Segment)dataPkg.refObject(
            "xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName
           );

        org.opencrx.kernel.account1.jmi1.Account account = null;

		    try {
			    dataPkg.refBegin();

          if ((accountClass != null) && (accountClass.compareTo(CONTACT_CLASS) == 0)) {
            // Contact

            org.opencrx.kernel.account1.jmi1.Contact contact = null;
            if (editingExisting) {
              // fetch existing contact
              contact = (org.opencrx.kernel.account1.jmi1.Contact)obj;
            }
            else {
              // create new contact
              contact = accountPkg.getContact().createContact();
              contact.refInitialize(false, false);
            }
            contact.setSalutationCode(salutationCode != null ? salutationCode.shortValue() : (short)0);
            contact.setSalutation(salutation);
            contact.setFirstName(firstName);
            contact.setMiddleName(middleName);
            contact.setLastName(lastName);
            contact.setAliasName(aliasName);
            contact.setPreferredSpokenLanguage(prefSLanguage != null ? prefSLanguage.shortValue() : (short)0);
            contact.setPreferredWrittenLanguage(prefWLanguage != null ? prefWLanguage.shortValue() : (short)0);
            contact.setJobTitle(jobTitle);
            contact.setJobRole(jobRole);
            contact.setOrganization(organization);
            contact.setDepartment(department);
            try {
              if ((reportsToTitle != null) && (reportsToTitle.length() > 0)) {
                contact.setReportsTo((org.opencrx.kernel.account1.jmi1.Contact)reportsTo);
              }
              else {
                contact.setReportsTo(null);
              }
            } catch (Exception e) {};
            try {
              if ((assistantTitle != null) && (assistantTitle.length() > 0)) {
                contact.setAssistant((org.opencrx.kernel.account1.jmi1.Contact)assistant);
              }
              else {
                contact.setAssistant(null);
              }
            } catch (Exception e) {};
            try {
              if ((deputyTitle != null) && (deputyTitle.length() > 0)) {
                contact.setDeputy((org.opencrx.kernel.account1.jmi1.Contact)deputy);
              }
              else {
                contact.setDeputy(null);
              }
            } catch (Exception e) {};
            contact.setBirthdate(birthdate);
            if (!editingExisting) {
              // add new contact to account segment
              accountSegment.addAccount(
				false,
				uuids.next().toString(),
				contact
			  );
            }
            account = (org.opencrx.kernel.account1.jmi1.Account)contact;
          }

          if ((accountClass != null) && (accountClass.compareTo(LEGALENTITY_CLASS) == 0)) {
            // LegalEntity

            org.opencrx.kernel.account1.jmi1.LegalEntity legalEntity = null;
            if (editingExisting) {
              // fetch existing legalEntity
              legalEntity = (org.opencrx.kernel.account1.jmi1.LegalEntity)obj;
            }
            else {
              // create new legalEntity
              legalEntity = accountPkg.getLegalEntity().createLegalEntity();
              legalEntity.refInitialize(false, false);
            }
            legalEntity.setName(name);
            legalEntity.setAliasName(aliasName);
            legalEntity.setIndustry(industry != null ? industry.shortValue() : (short)0);
            legalEntity.setTickerSymbol(tickerSymbol);
            legalEntity.setStockExchange(stockExchange);
            if (!editingExisting) {
              // add new legalEntity to account segment
              accountSegment.addAccount(
				false,
				uuids.next().toString(),
				legalEntity
			  );
            }
            account = (org.opencrx.kernel.account1.jmi1.Account)legalEntity;
          }

			    // Add web address to account if supplied
			    if((addressCWeb != null) && (addressCWeb.length() > 0)) {
            org.opencrx.kernel.account1.jmi1.WebAddress webAddress = accountPkg.getWebAddress().createWebAddress();
            webAddress.refInitialize(false, false);
            webAddress.setMain(true);
            webAddress.setWebUrl(addressCWeb);
            if (usageLeft[0] != 0) {webAddress.getUsage().add(new Short(usageLeft[0]));}
            if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {webAddress.getUsage().add(new Short(usage2Left[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					webAddress
				);
		    }
			if((addressPWeb != null) && (addressPWeb.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.WebAddress webAddress = accountPkg.getWebAddress().createWebAddress();
				webAddress.refInitialize(false, false);
				webAddress.setMain(true);
				webAddress.setWebUrl(addressPWeb);
				if (usageRight[0] != 0) {webAddress.getUsage().add(new Short(usageRight[0]));}
				if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {webAddress.getUsage().add(new Short(usage2Right[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					webAddress
				);
			}

			// Add email address to account if supplied
			if((addressCEMail != null) && (addressCEMail.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.EmailAddress email = accountPkg.getEmailAddress().createEmailAddress();
				email.refInitialize(false, false);
				email.setMain(true);
				email.setEmailFormat((short)0);
				email.setEmailType((short)0);
				email.setEmailAddress(addressCEMail);
				if (usageLeft[0] != 0) {email.getUsage().add(new Short(usageLeft[0]));}
				if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {email.getUsage().add(new Short(usage2Left[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					email
				);
		    }
		    if((addressPEMail != null) && (addressPEMail.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.EmailAddress email = accountPkg.getEmailAddress().createEmailAddress();
				email.refInitialize(false, false);
				email.setMain(true);
				email.setEmailFormat((short)0);
				email.setEmailType((short)0);
				email.setEmailAddress(addressPEMail);
				if (usageRight[0] != 0) {email.getUsage().add(new Short(usageRight[0]));}
				if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {email.getUsage().add(new Short(usage2Right[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					email
				);
		    }

			// Add phone number to account if supplied
			if((addressCPhone != null) && (addressCPhone.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressCPhone);
				if (usageLeft[0] != 0) {number.getUsage().add(new Short(usageLeft[0]));}
				if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {number.getUsage().add(new Short(usage2Left[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
		    }
		    if((addressPPhone != null) && (addressPPhone.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressPPhone);
				if (usageRight[0] != 0) {number.getUsage().add(new Short(usageRight[0]));}
				if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {number.getUsage().add(new Short(usage2Right[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
			}

			// Add fax to account if supplied
			if((addressCFax != null) && (addressCFax.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressCFax);
				if (usageLeft[0] != 0) {number.getUsage().add(new Short((short)(usageLeft[0]+usageFax)));}
				if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {number.getUsage().add(new Short(usage2Left[0]));}
				//number.getUsage().add(new Short(usageFax));
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
			}
			if((addressPFax != null) && (addressPFax.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressPFax);
				if (usageRight[0] != 0) {number.getUsage().add(new Short((short)(usageRight[0]+usageFax)));}
				if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {number.getUsage().add(new Short(usage2Right[0]));}
				//number.getUsage().add(new Short(usageFax));
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
		    }

			// Add mobile phone to account if supplied
			if((addressCCell != null) && (addressCCell.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressCCell);
				if (usageLeft[0] != 0) {number.getUsage().add(new Short(usageCell));}
				if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {number.getUsage().add(new Short(usageCell));}
				//number.getUsage().add(new Short(usageCell));
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
		    }
		    if((addressPCell != null) && (addressPCell.length() > 0)) {
				org.opencrx.kernel.account1.jmi1.PhoneNumber number = accountPkg.getPhoneNumber().createPhoneNumber();
				number.refInitialize(false, false);
				number.setMain(true);
				number.setAutomaticParsing(true);
				number.setPhoneNumberFull(addressPCell);
				if (usageRight[0] != 0) {number.getUsage().add(new Short(usageCell));}
				if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {number.getUsage().add(new Short(usageCell));}
				//number.getUsage().add(new Short(usageCell));
				account.addAddress(
					false,
					uuids.next().toString(),
					number
				);
		    }

			    // Add postal address to account if supplied
			    if(addressCPLine    != null) {addressCPLine   = addressCPLine.trim();}
			    if(addressCPStreet  != null) {addressCPStreet = addressCPStreet.trim();}
			    if(addressCPCity    != null) {addressCPCity   = addressCPCity.trim();}
			    if(addressCPCode    != null) {addressCPCode   = addressCPCode.trim();}
			    boolean countrySelected = false;
			    try {
			      if (Short.parseShort(addressCPCountry) > 0) {countrySelected = true;}
			    } catch (Exception e) {}
			    if(
			      ((addressCPLine    != null) && (addressCPLine.length()    > 0)) ||
			      ((addressCPStreet  != null) && (addressCPStreet.length()  > 0)) ||
			      ((addressCPCity    != null) && (addressCPCity.length()    > 0)) ||
			      ((addressCPCode    != null) && (addressCPCode.length()    > 0)) ||
			      (countrySelected)
			    ) {
            org.opencrx.kernel.account1.jmi1.PostalAddress address = accountPkg.getPostalAddress().createPostalAddress();
            address.refInitialize(false, false);
            address.setMain(true);
            if ((addressCPLine    != null) && (addressCPLine.length()    > 0)) {address.getPostalAddressLine().add(addressCPLine);}
            if ((addressCPStreet  != null) && (addressCPStreet.length()  > 0)) {address.getPostalStreet().add(addressCPStreet);}
            if ((addressCPCity    != null) && (addressCPCity.length()    > 0)) {address.setPostalCity(addressCPCity);}
            if ((addressCPCode    != null) && (addressCPCode.length()    > 0)) {address.setPostalCode(addressCPCode);}
            if ((addressCPCity    != null) && (addressCPCity.length()    > 0)) {address.setPostalCity(addressCPCity);}
            address.setPostalCountry((short)(Short.parseShort(addressCPCountry)));
            if (usageLeft[0] != 0) {address.getUsage().add(new Short(usageLeft[0]));}
            if ((usage2Left[0] != 0) && (usage2Left[0] != usageLeft[0])) {address.getUsage().add(new Short(usage2Left[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					address
				);
			}
			    if(addressPPLine    != null) {addressPPLine   = addressPPLine.trim();}
			    if(addressPPStreet  != null) {addressPPStreet = addressPPStreet.trim();}
			    if(addressPPCity    != null) {addressPPCity   = addressPPCity.trim();}
			    if(addressPPCode    != null) {addressPPCode   = addressPPCode.trim();}
			    countrySelected = false;
			    try {
			      if (Short.parseShort(addressPPCountry) > 0) {countrySelected = true;}
			    } catch (Exception e) {}
			    if(
			      ((addressPPLine    != null) && (addressPPLine.length()    > 0)) ||
			      ((addressPPStreet  != null) && (addressPPStreet.length()  > 0)) ||
			      ((addressPPCity    != null) && (addressPPCity.length()    > 0)) ||
			      ((addressPPCode    != null) && (addressPPCode.length()    > 0)) ||
			      (countrySelected)
			    ) {
            org.opencrx.kernel.account1.jmi1.PostalAddress address = accountPkg.getPostalAddress().createPostalAddress();
            address.refInitialize(false, false);
            address.setMain(true);
            if ((addressPPLine    != null) && (addressPPLine.length()    > 0)) {address.getPostalAddressLine().add(addressPPLine);}
            if ((addressPPStreet  != null) && (addressPPStreet.length()  > 0)) {address.getPostalStreet().add(addressPPStreet);}
            if ((addressPPCity    != null) && (addressPPCity.length()    > 0)) {address.setPostalCity(addressPPCity);}
            if ((addressPPCode    != null) && (addressPPCode.length()    > 0)) {address.setPostalCode(addressPPCode);}
            if ((addressPPCity    != null) && (addressPPCity.length()    > 0)) {address.setPostalCity(addressPPCity);}
            address.setPostalCountry((short)(Short.parseShort(addressPPCountry)));
            if (usageRight[0] != 0) {address.getUsage().add(new Short(usageRight[0]));}
            if ((usage2Right[0] != 0) && (usage2Right[0] != usageRight[0])) {address.getUsage().add(new Short(usage2Right[0]));}
				account.addAddress(
					false,
					uuids.next().toString(),
					address
				);
			}

			    dataPkg.refCommit();
    		  session.setAttribute(wizardName, null);

			    // Go to created/edited account
          Action nextAction =
              new Action(
        			Action.EVENT_SELECT_OBJECT,
		        new Action.Parameter[]{
		            new Action.Parameter(Action.PARAMETER_OBJECTXRI, account.refMofId())
		        },
		        "", true
			    );
			    response.sendRedirect(
			        request.getContextPath() + "/" + showView.getEncodedHRef(nextAction)
			    );
			    return;
  			}
  			catch(Exception e) {
  				errorMsg = app.getTexts().getErrorTextCanNotCreateOrEditObject();
  				errorMsg = errorMsg.replaceAll("\\$\\{0\\}", "Security/Permission");
  				try {
  				    dataPkg.refRollback();
  				} catch(Exception e0) {}
  				//throw e;
        }
		}
%>

<%@ include file="../../show-header.html" %>
<div id="header" style="padding:10px 0px 10px 0px;">
  <table id="headerlayout" style="position:relative;">
    <tr id="headRow">
      <td id="head" colspan="2">
        <table id="info">
          <tr>
            <td id="headerCellLeft"><img id="logoLeft" src="../../images/logoLeft.gif" alt="openCRX - limitless relationship management" title="" /></td>
            <td id="headerCellMiddle"></td>
            <td id="headerCellRight"><img id="logoRight" src="../../images/logoRight.gif" alt="" title="" /></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</div>

<%
if ((errorMsg != null) && (errorMsg.length() > 0)) {
%>
  <table class="tableError">
    <tr>
      <td class="cellErrorLeft">Error</td>
      <td class="cellErrorRight">
        <%= errorMsg %>
      </td>
    </tr>
  </table>
<%
  errorMsg = null;
}
%>
<form name="<%= formName %>" accept-charset="UTF-8" method="POST" action="<%= wizardName %>">
  <input type="hidden" name="xri" value="<%= objectXri %>" />
  <input type="hidden" name="errorMsg" value="<%= errorMsg == null ? "" : errorMsg %>" />
  <table cellspacing="8" class="tableLayout">
    <tr>
      <td class="cellObject">
        <noscript>
          <div class="panelJSWarning" style="display: block;">
            <a href="../../helpJsCookie.html" target="_blank"><img class="popUpButton" src="../../images/help.gif" width="16" height="16" border="0" onclick="javascript:void(window.open('helpJsCookie.html', 'Help', 'fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=400'));" alt="" /></a> <%= texts.getPageRequiresScriptText() %>
          </div>
        </noscript>
        <table class="objectTitle">
          <tr>
            <td>
              <div style="padding-left:5px; padding-bottom: 3px;">
                <%= editingExisting ? app.getTexts().getEditTitle() : app.getTexts().getNewText() %> - <%= accountClass == null ? app.getLabel(ACCOUNT_CLASS) : app.getLabel(accountClass) %>
              </div>
            </td>
          </tr>
        </table>
<%
        if (accountClass == null) {
          // user must first determine class of object to be created
%>
          <br>
          <label>
            <input type="radio" name="accountClass" value="<%= CONTACT_CLASS %>" onchange="javascript:document.forms['<%= formName %>'].submit();">
            <%= app.getLabel(CONTACT_CLASS) %>
          </label>
          <br>
          <label>
            <input type="radio" name="accountClass" value="<%= LEGALENTITY_CLASS %>" onchange="javascript:document.forms['<%= formName %>'].submit();">
            <%= app.getLabel(LEGALENTITY_CLASS) %>
          </label>
          <br>
          <br>
          <!--
          <INPUT type="Submit" name="OK.Button" id="OK.Button" tabindex="9000" value="<%= app.getTexts().getOkTitle() %>" />
          -->
          <INPUT type="Submit" name="Cancel.Button" tabindex="9010" value="<%= app.getTexts().getCancelTitle() %>" />
<%
        }
        else {
          // show edit mask
%>
          <input type="hidden" name="accountClass" value="<%= accountClass %>" />
          <div class="panel" id="panelObj0" style="display: block">
            <div class="fieldGroupName">&nbsp;</div>
    	      <table class="fieldGroup">
<%
              if (accountClass.compareTo(CONTACT_CLASS) == 0) {
              // Contact
%>
      	        <tr>
<%
                  // salutationCode
                  salutationCodeAttr.paintForEdit(
                    p,
                    salutationCode_fieldId,   // field ID
                    salutationCode_label,     // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    salutationCode_tabIndex,  // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    salutationCodeAttr.getStringifiedValue(p, true, true)
                  );
                  // jobTitle
                  jobTitleAttr.paintForEdit(
                    p,
                    jobTitle_fieldId,       // field ID
                    jobTitle_label,         // field label
                    showView.getRefObject(),// lookup object
                    1,                      // #columns
                    jobTitle_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    jobTitleAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // salutation
                  salutationAttr.paintForEdit(
                    p,
                    salutation_fieldId,      // field ID
                    salutation_label,        // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    salutation_tabIndex,     // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    salutationAttr.getStringifiedValue(p, true, true)
                  );
                  // jobRole
                  jobRoleAttr.paintForEdit(
                    p,
                    jobRole_fieldId,        // field ID
                    jobRole_label,          // field label
                    showView.getRefObject(),// lookup object
                    1,                      // #columns
                    jobRole_tabIndex,       // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    jobRoleAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // firstName
                  firstNameAttr.paintForEdit(
                    p,
                    firstName_fieldId,       // field ID
                    firstName_label,         // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    firstName_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    firstNameAttr.getStringifiedValue(p, true, true)
                  );
                  // organization
                  organizationAttr.paintForEdit(
                    p,
                    organization_fieldId,    // field ID
                    organization_label,      // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    organization_tabIndex,   // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    organizationAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // middleName
                  middleNameAttr.paintForEdit(
                    p,
                    middleName_fieldId,      // field ID
                    middleName_label,        // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    middleName_tabIndex,     // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    middleNameAttr.getStringifiedValue(p, true, true)
                  );
                  // department
                  departmentAttr.paintForEdit(
                    p,
                    department_fieldId,       // field ID
                    department_label,         // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    department_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    departmentAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // lastName
                  lastNameAttr.paintForEdit(
                    p,
                    lastName_fieldId,         // field ID
                    lastName_label,           // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    lastName_tabIndex,        // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    lastNameAttr.getStringifiedValue(p, true, true)
                  );

                  // reportsTo
                  reportsToAttr.paintForEdit(
                    p,
                    reportsTo_fieldId,        // field ID
                    reportsTo_label,          // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    reportsTo_tabIndex,       // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    reportsToAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // aliasName
                  aliasNameAttr.paintForEdit(
                    p,
                    aliasName_fieldId,        // field ID
                    aliasName_label,          // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    aliasName_tabIndex,       // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    aliasNameAttr.getStringifiedValue(p, true, true)
                  );

                  // assistant
                  assistantAttr.paintForEdit(
                    p,
                    assistant_fieldId,       // field ID
                    assistant_label,         // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    assistant_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    assistantAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // prefSLanguage
                  prefSLanguageAttr.paintForEdit(
                    p,
                    prefSLanguage_fieldId,   // field ID
                    prefSLanguage_label,     // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    prefSLanguage_tabIndex,  // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    prefSLanguageAttr.getStringifiedValue(p, true, true)
                  );

                  // deputy
                  deputyAttr.paintForEdit(
                    p,
                    deputy_fieldId,         // field ID
                    deputy_label,           // field label
                    showView.getRefObject(),// lookup object
                    1,                      // #columns
                    deputy_tabIndex,        // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    deputyAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // prefWLanguage
                  prefWLanguageAttr.paintForEdit(
                    p,
                    prefWLanguage_fieldId,       // field ID
                    prefWLanguage_label,         // field label
                    showView.getRefObject(),     // lookup object
                    1,                           // #columns
                    prefWLanguage_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    prefWLanguageAttr.getStringifiedValue(p, true, true)
                  );

                  // birthdate
                  birthdateAttr.paintForEdit(
                    p,
                    birthdate_fieldId,       // field ID
                    birthdate_label,         // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    birthdate_tabIndex,      // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    birthdateAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>
<%
              }
              if (accountClass.compareTo(LEGALENTITY_CLASS) == 0) {
              // LegalEntity
%>
      	        <tr>
<%
                  // name
                  nameAttr.paintForEdit(
                    p,
                    name_fieldId,           // field ID
                    name_label,             // field label
                    showView.getRefObject(),// lookup object
                    1,                      // #columns
                    name_tabIndex,          // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    nameAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>
              </table>
              <table class="fieldGroup">
      	        <tr>
<%
                  // aliasName
                  aliasNameAttr.paintForEdit(
                    p,
                    aliasName_fieldId,        // field ID
                    aliasName_label,          // field label
                    showView.getRefObject(),  // lookup object
                    1,                        // #columns
                    aliasName_tabIndex,       // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    aliasNameAttr.getStringifiedValue(p, true, true)
                  );
                  // tickerSymbol
                  tickerSymbolAttr.paintForEdit(
                    p,
                    tickerSymbol_fieldId,      // field ID
                    tickerSymbol_label,        // field label
                    showView.getRefObject(),   // lookup object
                    1,                         // #columns
                    tickerSymbol_tabIndex,     // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    tickerSymbolAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>

      	        <tr>
<%
                  // industry
                  industryAttr.paintForEdit(
                    p,
                    industry_fieldId,        // field ID
                    industry_label,          // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    industry_tabIndex,       // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    industryAttr.getStringifiedValue(p, true, true)
                  );
                  // stockExchange
                  stockExchangeAttr.paintForEdit(
                    p,
                    stockExchange_fieldId,   // field ID
                    stockExchange_label,     // field label
                    showView.getRefObject(), // lookup object
                    1,                       // #columns
                    stockExchange_tabIndex,  // tabIndex
                    "", // rowSpanModifier
                    "", // readOnlyModifier
                    "", // disabledModifier
                    "", // lockedModifier
                    stockExchangeAttr.getStringifiedValue(p, true, true)
                  );
                  p.flush();
%>
                </tr>
<%
              }
%>
    	      </table>

            <div class="fieldGroupName">&nbsp;</div>
    	      <table class="fieldGroup">
    	        <tr>
                <td class="imglabel"></td>
                <td></td>
                <td class="addon"></td>
                <td class="imglabel"></td>
                <td></td>
                <td class="addon"></td>
              </tr>

    	        <tr class="addressHeader">
                <td colspan="2">
                  <table width="100%">
                    <tr>
                      <td width="50%">
                        <select class="valueL" id="<%= usageLeft %>" name="usageLeft" tabindex="1900">
<%
                          for(short i=1; i < usageLeft.length; i++) {
%>
                            <option <%= usageLeft[0] == usageLeft[i] ? "selected" : "" %> value="<%= usageLeft[i] %>"><%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usageLeft[i]))) %>
<%
                          }
%>
                        </select>
                      </td>
                      <td width="50%">
                        <select class="valueL" id="<%= usage2Left %>" name="usage2Left" tabindex="1910">
<%
                          for(short i=1; i < usage2Left.length; i++) {
%>
                            <option <%= usage2Left[0] == usage2Left[i] ? "selected" : "" %> value="<%= usage2Left[i] %>"><%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usage2Left[i]))) %>
<%
                          }
%>
                        </select>
                      </td>
                    </tr>
                  </table>
                </td>
                <td></td>
                <td colspan="2">
                  <table width="100%">
                    <tr>
                      <td width="50%">
                        <select class="valueL" id="<%= usageRight %>" name="usageRight" tabindex="1920">
<%
                          for(short i=1; i < usageRight.length; i++) {
%>
                            <option <%= usageRight[0] == usageRight[i] ? "selected" : "" %> value="<%= usageRight[i] %>"><%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usageRight[i]))) %>
<%
                          }
%>
                        </select>
                      </td>
                      <td width="50%">
                        <select class="valueL" id="<%= usage2Right %>" name="usage2Right" tabindex="1930">
<%
                          for(short i=1; i < usage2Right.length; i++) {
%>
                            <option <%= usage2Right[0] == usage2Right[i] ? "selected" : "" %> value="<%= usage2Right[i] %>"><%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usage2Right[i]))) %>
<%
                          }
%>
                        </select>
                      </td>
                    </tr>
                  </table>
                </td>
                <td></td>
              </tr>

              <!-- Web                                   -->
              <tr>
                <td><img src="<%= addressCWeb_label %>" alt="web" title="<%= app.getLabel(WEBADDRESS_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCWeb_fieldId %>" name="<%= addressCWeb_fieldId %>" tabindex="<%= addressCWeb_tabIndex %>" value="<%= addressCWeb != null ? addressCWeb : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPWeb_label %>" alt="web" title="<%= app.getLabel(WEBADDRESS_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPWeb_fieldId %>" name="<%= addressPWeb_fieldId %>" tabindex="<%= addressPWeb_tabIndex %>" value="<%= addressPWeb != null ? addressPWeb : "" %>" /></td>
                <td></td>
              </tr>
              <!-- E-Mail                                -->
              <tr>
                <td><img src="<%= addressCEMail_label %>" alt="@" title="<%= app.getLabel(EMAIL_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCEMail_fieldId %>" name="<%= addressCEMail_fieldId %>" tabindex="<%= addressCEMail_tabIndex %>" value="<%= addressCEMail != null ? addressCEMail : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPEMail_label %>" alt="@" title="<%= app.getLabel(EMAIL_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPEMail_fieldId %>" name="<%= addressPEMail_fieldId %>" tabindex="<%= addressPEMail_tabIndex %>" value="<%= addressPEMail != null ? addressPEMail : "" %>" /></td>
                <td></td>
              </tr>
              <!-- Phone                                 -->
              <tr>
                <td><img src="<%= addressCPhone_label %>" alt="fon" title="<%= app.getLabel(PHONENUMBER_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCPhone_fieldId %>" name="<%= addressCPhone_fieldId %>" tabindex="<%= addressCPhone_tabIndex %>" value="<%= addressCPhone != null ? addressCPhone : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPPhone_label %>" alt="fon" title="<%= app.getLabel(PHONENUMBER_CLASS) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPPhone_fieldId %>" name="<%= addressPPhone_fieldId %>" tabindex="<%= addressPPhone_tabIndex %>" value="<%= addressPPhone != null ? addressPPhone : "" %>" /></td>
                <td></td>
              </tr>
              <!-- Fax                                   -->
              <tr>
                <td><img src="<%= addressCFax_label %>" alt="fax" title="<%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short((short)(500+usageFax)))) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCFax_fieldId %>" name="<%= addressCFax_fieldId %>" tabindex="<%= addressCFax_tabIndex %>" value="<%= addressCFax != null ? addressCFax : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPFax_label %>" alt="fax" title="<%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short((short)(400+usageFax)))) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPFax_fieldId %>" name="<%= addressPFax_fieldId %>" tabindex="<%= addressPFax_tabIndex %>" value="<%= addressPFax != null ? addressPFax : "" %>" /></td>
                <td></td>
              </tr>
              <!-- Cell                                  -->
              <tr>
                <td><img src="<%= addressCCell_label %>" alt="cell" title="<%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usageCell))) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCCell_fieldId %>" name="<%= addressCCell_fieldId %>" tabindex="<%= addressCCell_tabIndex %>" value="<%= addressCCell != null ? addressCCell : "" %>" /></td>
                <td></td>
<!--
                <td><img src="<%= addressPCell_label %>" alt="cell" title="<%= (String)(codes.getLongText(featureUsageAddress, app.getCurrentLocaleAsIndex(), true, true).get(new Short(usageCell))) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPCell_fieldId %>" name="<%= addressPCell_fieldId %>" tabindex="<%= addressPCell_tabIndex %>" value="<%= addressPCell != null ? addressPCell : "" %>" /></td>
-->
                <td></td>
                <td></td>
                <td></td>
              </tr>

              <tr>
                <td colspan="6">&nbsp;</td>
              </tr>

              <!-- PostalAddress: postalAddressLine      -->
              <tr>
                <td><img src="<%= addressCPLine_label %>" alt="addr" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSLINE_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCPLine_fieldId %>" name="<%= addressCPLine_fieldId %>" tabindex="<%= addressCPLine_tabIndex %>" value="<%= addressCPLine != null ? addressCPLine : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPPLine_label %>" alt="addr" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSLINE_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPPLine_fieldId %>" name="<%= addressPPLine_fieldId %>" tabindex="<%= addressPPLine_tabIndex %>" value="<%= addressPPLine != null ? addressPPLine : "" %>" /></td>
                <td></td>
              </tr>
              <!-- PostalAddress: postalStreet           -->
              <tr>
                <td><img src="<%= addressCPStreet_label %>" alt="str" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSSTREET_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCPStreet_fieldId %>" name="<%= addressCPStreet_fieldId %>" tabindex="<%= addressCPStreet_tabIndex %>" value="<%= addressCPStreet != null ? addressCPStreet : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPPStreet_label %>" alt="str" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSSTREET_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPPStreet_fieldId %>" name="<%= addressPPStreet_fieldId %>" tabindex="<%= addressPPStreet_tabIndex %>" value="<%= addressPPStreet != null ? addressPPStreet : "" %>" /></td>
                <td></td>
              </tr>
              <!-- PostalAddress: postalCity             -->
              <tr>
                <td><img src="<%= addressCPCity_label %>" alt="city" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCITY_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCPCity_fieldId %>" name="<%= addressCPCity_fieldId %>" tabindex="<%= addressCPCity_tabIndex %>" value="<%= addressCPCity != null ? addressCPCity : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPPCity_label %>" alt="city" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCITY_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPPCity_fieldId %>" name="<%= addressPPCity_fieldId %>" tabindex="<%= addressPPCity_tabIndex %>" value="<%= addressPPCity != null ? addressPPCity : "" %>" /></td>
                <td></td>
              </tr>
              <!-- PostalAddress: postalCode             -->
              <tr>
                <td><img src="<%= addressCPCode_label %>" alt="zip" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCODE_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressCPCode_fieldId %>" name="<%= addressCPCode_fieldId %>" tabindex="<%= addressCPCode_tabIndex %>" value="<%= addressCPCode != null ? addressCPCode : "" %>" /></td>
                <td></td>
                <td><img src="<%= addressPPCode_label %>" alt="zip" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCODE_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td><input type="text" class="valueL" id="<%= addressPPCode_fieldId %>" name="<%= addressPPCode_fieldId %>" tabindex="<%= addressPPCode_tabIndex %>" value="<%= addressPPCode != null ? addressPPCode : "" %>" /></td>
                <td></td>
              </tr>
              <!-- PostalAddress: postalCountry          -->
              <tr>
                <td><img src="<%= addressCPCountry_label %>" alt="ctry" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCOUNTRY_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td>
                  <select class="valueL" id="<%= addressCPCountry_fieldId %>" name="<%= addressCPCountry_fieldId %>" tabindex="<%= addressCPCountry_tabIndex %>">
<%
                    if (postalCountryCode_longTextsT == null) {
%>
                      <option value="0">N/A
<%
                    }
                    else {
                      for(Iterator options = postalCountryCode_longTextsT.entrySet().iterator(); options.hasNext(); ) {
                        Map.Entry option = (Map.Entry)options.next();
                        short value = Short.parseShort((option.getValue()).toString());
                        if (addressCPCountry == null) {addressCPCountry = "0";}
                        String selectedModifier = Short.parseShort(addressCPCountry) == value ? "selected" : "";
%>
                        <option <%= selectedModifier %> value="<%= value %>"><%= option.getKey().toString() %>
<%
                      }
                    }
%>
                  </select>
                </td>
                <td></td>
                <td><img src="<%= addressPPCountry_label %>" alt="ctry" title="<%= app.getLabel(POSTALADDRESS_CLASS) + " " + view.getFieldLabel(POSTALADDRESS_CLASS, POSTALADDRESSCOUNTRY_FEATURE, app.getCurrentLocaleAsIndex()) %>" /></td>
                <td>
                  <select class="valueL" id="<%= addressPPCountry_fieldId %>" name="<%= addressPPCountry_fieldId %>" tabindex="<%= addressPPCountry_tabIndex %>">
<%
                    if (postalCountryCode_longTextsT == null) {
%>
                      <option value="0">N/A
<%
                    }
                    else {
                      for(Iterator options = postalCountryCode_longTextsT.entrySet().iterator(); options.hasNext(); ) {
                        Map.Entry option = (Map.Entry)options.next();
                        short value = Short.parseShort((option.getValue()).toString());
                        if (addressPPCountry == null) {addressPPCountry = "0";}
                        String selectedModifier = Short.parseShort(addressPPCountry) == value ? "selected" : "";
%>
                        <option <%= selectedModifier %> value="<%= value %>"><%= option.getKey().toString() %>
<%
                      }
                    }
%>
                  </select>
                </td>
                <td></td>
              </tr>
            </table>
          </div>
          <INPUT type="Submit" name="OK.Button" id="OK.Button" tabindex="9000" value="<%= app.getTexts().getSaveTitle() %>" />
          <INPUT type="Submit" name="Cancel.Button" tabindex="9010" value="<%= app.getTexts().getCancelTitle() %>" />
          <INPUT type="checkbox" name="editingExisting" <%= editingExisting ? "checked" : "" %> value="" style="visibility:hidden;" />
<%
        }
%>
    	</td>
    </tr>
  </table>
</form>
<%
  }
  catch (Exception ex) {
    out.println("<p><b>!! Failed !!<br><br>The following exception occur:</b><br><br><pre>");
    ex.printStackTrace(new PrintWriter(out));
    out.println("</pre></p>");
  }
	p.close(false);
%>
  <script language="javascript" type="text/javascript">
    requiredField = '#FFB287';

    function trim(str) {
      str = str.replace(/^[ ]+(.*)$/, '$1'); // Trims leading spaces
      str = str.replace(/^(.*)[ ]+$/, '$1'); // Trims trailing spaces
      return str;
    }

    function verifyNotEmpty() {
      if (trim(this.value).length == 0) {
        this.style.backgroundColor = requiredField;
      }
      else {
        this.style.backgroundColor = '';
      }
      return true;
    }

    function verifyRequiredFields() {
      /* is handled by JSP
      els = document.getElementsByTagName('INPUT');
      for (i=0; i<els.length; i++) {
        if (els[i].name.indexOf('<%= lastName_fieldId %>'    ) >= 0) {
          if (trim(els[i].value).length == 0) {alert('<%= lastNameAttr.getLabel() %>!'); return false;}
        }
      }
      new Effect.Highlight(this, {duration:0.3});
      document.forms.<%= formName %>.submit();
      return true;
      */
    }

    function initPage() {
      try {
<%
        if ((accountClass != null) && (accountClass.compareTo(CONTACT_CLASS) == 0)) {
%>
          elt = $('<%= lastName_fieldId %>');
          elt.style.backgroundColor = requiredField;
          elt.onblur=verifyNotEmpty;
          elt.onblur();
<%
        }
        if ((accountClass != null) && (accountClass.compareTo(LEGALENTITY_CLASS) == 0)) {
%>
          elt = $('<%= name_fieldId %>');
          elt.style.backgroundColor = requiredField;
          elt.onblur=verifyNotEmpty;
          elt.onblur();
<%
        }
%>
      } catch(e){};
      //$('OK.Button').onclick=verifyRequiredFields;
    }
	</script>
</body>
</html>

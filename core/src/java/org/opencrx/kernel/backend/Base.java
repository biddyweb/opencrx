/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Base.java,v 1.54 2012/01/13 17:15:42 wfro Exp $
 * Description: Base
 * Revision:    $Revision: 1.54 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2012/01/13 17:15:42 $
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAssignment;
import org.opencrx.kernel.account1.jmi1.ContactRelationship;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.RevenueReport;
import org.opencrx.kernel.activity1.jmi1.AbstractActivityParty;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityFollowUp;
import org.opencrx.kernel.activity1.jmi1.ActivityGroupAssignment;
import org.opencrx.kernel.activity1.jmi1.ActivityProcessState;
import org.opencrx.kernel.activity1.jmi1.AddressGroupMember;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.EMailRecipientGroup;
import org.opencrx.kernel.activity1.jmi1.IncidentParty;
import org.opencrx.kernel.activity1.jmi1.InvolvedObject;
import org.opencrx.kernel.activity1.jmi1.MailingRecipient;
import org.opencrx.kernel.activity1.jmi1.MailingRecipientGroup;
import org.opencrx.kernel.activity1.jmi1.MeetingParty;
import org.opencrx.kernel.activity1.jmi1.PhoneCallRecipient;
import org.opencrx.kernel.activity1.jmi1.PhoneCallRecipientGroup;
import org.opencrx.kernel.activity1.jmi1.TaskParty;
import org.opencrx.kernel.address1.jmi1.Addressable;
import org.opencrx.kernel.address1.jmi1.EMailAddressable;
import org.opencrx.kernel.address1.jmi1.PhoneNumberAddressable;
import org.opencrx.kernel.address1.jmi1.PostalAddressable;
import org.opencrx.kernel.address1.jmi1.RoomAddressable;
import org.opencrx.kernel.address1.jmi1.WebAddressable;
import org.opencrx.kernel.building1.jmi1.AbstractBuildingUnit;
import org.opencrx.kernel.building1.jmi1.InventoryItem;
import org.opencrx.kernel.code1.jmi1.CodeValueEntry;
import org.opencrx.kernel.code1.jmi1.SimpleEntry;
import org.opencrx.kernel.contract1.jmi1.ContractRole;
import org.opencrx.kernel.contract1.jmi1.SalesContractPosition;
import org.opencrx.kernel.depot1.jmi1.Depot;
import org.opencrx.kernel.depot1.jmi1.DepotContract;
import org.opencrx.kernel.depot1.jmi1.DepotEntity;
import org.opencrx.kernel.depot1.jmi1.DepotPosition;
import org.opencrx.kernel.depot1.jmi1.DepotReport;
import org.opencrx.kernel.depot1.jmi1.DepotReportItemPosition;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.DocumentRevision;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.generic.jmi1.CrxObject;
import org.opencrx.kernel.generic.jmi1.Media;
import org.opencrx.kernel.home1.cci2.AlertQuery;
import org.opencrx.kernel.home1.jmi1.Alert;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.opencrx.kernel.product1.jmi1.AbstractPriceLevel;
import org.opencrx.kernel.product1.jmi1.AbstractProduct;
import org.opencrx.kernel.product1.jmi1.PriceListEntry;
import org.opencrx.kernel.product1.jmi1.ProductBasePrice;
import org.opencrx.kernel.uom1.jmi1.Uom;
import org.opencrx.kernel.utils.TinyUrlUtils;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.cci.SystemAttributes;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.ContextCapable;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.Codes;
import org.openmdx.portal.servlet.WebKeys;
import org.openmdx.portal.servlet.action.SelectObjectAction;
import org.w3c.cci2.BinaryLargeObjects;

public class Base extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new Base());
	}
	
    //-------------------------------------------------------------------------
	public static Base getInstance(
	) throws ServiceException {
		return getInstance(Base.class);
	}

	//-------------------------------------------------------------------------
	protected Base(
	) {
		
	}
	
    //-------------------------------------------------------------------------
    public void sendAlert(
    	ContextCapable target,
        String toUsers,        
        String name,
        String description,
        short importance,
        Integer resendDelayInSeconds,
        ContextCapable reference
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(target);    	
        StringTokenizer tokenizer = new StringTokenizer(
            toUsers == null ? 
                "" : 
                toUsers, ";, "
        );
        while(tokenizer.hasMoreTokens()) {
            String toUser = tokenizer.nextToken();
            UserHome userHome = UserHomes.getInstance().getUserHome(
                toUser,
                target.refGetPath(),
                pm
            );
            if(userHome != null) {
            	Path referenceIdentity = (reference == null) || !JDOHelper.isPersistent(reference) ? 
                	target.refGetPath() : 
                	reference.refGetPath();                
                // Only create alert if there is not already an alert with the 
                // same alert reference and created within the delay period.
                PersistenceManager pmRoot = pm.getPersistenceManagerFactory().getPersistenceManager(
                	SecurityKeys.ROOT_PRINCIPAL, 
                	null
                );
                ContextCapable alertReference = (ContextCapable)pmRoot.getObjectById(referenceIdentity);
                AlertQuery alertQuery = (AlertQuery)pmRoot.newQuery(Alert.class);
                alertQuery.thereExistsReference().equalTo(alertReference);
                alertQuery.createdAt().greaterThanOrEqualTo(
                	new Date(System.currentTimeMillis() - 1000 * (resendDelayInSeconds == null ? 0 : resendDelayInSeconds.intValue()))
                );
                UserHome userHomeByRoot = (UserHome)pmRoot.getObjectById(userHome.refGetPath());
                List<Alert> existingAlerts = userHomeByRoot.getAlert(alertQuery);
                if(existingAlerts.isEmpty()) {
                	Alert newAlert = pmRoot.newInstance(Alert.class);
                	newAlert.setAlertState(new Short((short)1));
                	newAlert.setName(
                        name == null || name.length() == 0 ?
                            "--" : // name is mandatory
                            name
                    );
                    if(description != null) {
                        newAlert.setDescription(description);
                    }
                    newAlert.setImportance(importance);
                    newAlert.setReference(alertReference);
                    newAlert.getOwningGroup().clear();
                    newAlert.getOwningGroup().addAll(
                        userHomeByRoot.getOwningGroup()
                    );
                    pmRoot.currentTransaction().begin();
                    userHomeByRoot.addAlert(
                    	this.getUidAsString(),
                    	newAlert
                    );
                    pmRoot.currentTransaction().commit();
                    pmRoot.close();
                }
            }
        }
    }
    
    //-------------------------------------------------------------------------
    public void assignToMe(
        RefObject_1_0 target,
        boolean overwrite,
        boolean useRunAsPrincipal
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(target);
        String objectClass = target.refClass().refMofId();
        Model_1_0 model = Model_1Factory.getModel();
        ModelElement_1_0 classDef = model.getElement(objectClass);
        Map<String,ModelElement_1_0> attributeDefs = model.getAttributeDefs(classDef, false, true);
        List<String> principalChain = UserObjects.getPrincipalChain(pm);
        if(!principalChain.isEmpty()) {
            UserHome userHome = UserHomes.getInstance().getUserHome(
                target.refGetPath(),
                pm,
                useRunAsPrincipal
            );
            if((userHome != null) && (userHome.getContact() != null)) {
                for(String attribute: ASSIGN_TO_ME_ATTRIBUTES) {
                    if(
                        attributeDefs.keySet().contains(attribute) &&
                        (overwrite || (target.refGetValue(attribute) == null))
                    ) {
                        target.refSetValue(
                        	attribute,
                            userHome.getContact()
                        );
                    }
                }
            }
        }
    }

	//-------------------------------------------------------------------------
    /**
     * Counts the number of occurences of items in report and returns a string
     * of the form item0: n0, item1: n1, etc.
     */
    public String analyseReport(
        List<String> report
    ) {
        Map<Object,Short> reportAsMap = new HashMap<Object,Short>();
        for(int i = 0; i < report.size(); i++) {
            Object key = report.get(i);
            Short count = reportAsMap.get(key);
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
        
    //-------------------------------------------------------------------------
    public String getAccessUrl(
    	Object context,
    	String contextPattern,
    	RefObject_1_0 object
    ) {
    	return this.getAccessUrl(
    		context, 
    		contextPattern, 
    		object, 
    		false
    	);
    }
    
    //-------------------------------------------------------------------------
    public String getAccessUrl(
    	Object context,
    	String contextPattern,
    	RefObject_1_0 object,
    	boolean asTinyUrl
    ) {
    	if(context instanceof HttpServletRequest) {
    		HttpServletRequest req = (HttpServletRequest)context;
    		String urlPrefix = req.getScheme()+ "://" + req.getServerName() + ":" + req.getServerPort();		
            Action selectObjectAction = 
                new Action(
                    SelectObjectAction.EVENT_ID, 
                    new Action.Parameter[]{
                        new Action.Parameter(Action.PARAMETER_OBJECTXRI, object.refMofId())
                    },
                    "",
                    true
                );        
            String fullUrl =
                urlPrefix + 
                req.getContextPath().replace(contextPattern, "-core-") +  "/" + 
                WebKeys.SERVLET_NAME + 
                "?event=" + SelectObjectAction.EVENT_ID + 
                "&parameter=" + selectObjectAction.getParameter();
            String url = fullUrl;
            if(asTinyUrl) {
	            String tinyUrl = TinyUrlUtils.getTinyUrl(fullUrl);
	            url = tinyUrl != null ? tinyUrl : fullUrl;
            }
            return url;
    	}
    	else if(context instanceof UserHome) {
    		UserHome userHome = (UserHome)context;
    		String urlPrefix = userHome.getWebAccessUrl();
    		if(urlPrefix != null) {
    	        Action selectObjectAction = 
    	            new Action(
    	                SelectObjectAction.EVENT_ID, 
    	                new Action.Parameter[]{
    	                    new Action.Parameter(Action.PARAMETER_OBJECTXRI, object.refMofId())
    	                },
    	                "",
    	                true
    	            );
    	        String fullUrl =
    	            urlPrefix + (urlPrefix.endsWith("/") ? "" : "/") + 
    	            WebKeys.SERVLET_NAME + 
    	            "?event=" + SelectObjectAction.EVENT_ID + 
    	            "&parameter=" + selectObjectAction.getParameter();
    	        String url = fullUrl;
    	        if(asTinyUrl) {
	    	        String tinyUrl = TinyUrlUtils.getTinyUrl(fullUrl);
	    	        url = tinyUrl != null ? tinyUrl : fullUrl;
    	        }
    	        return url;
    		}
    		else {
    			return null;
    		}
    	}
    	else {
    		return null;
    	}
    }

	//-------------------------------------------------------------------------
    public boolean isAccessUrl(
    	String url
    ) {
    	return
        	url.indexOf(WebKeys.SERVLET_NAME) > 0 || 
        	url.startsWith(TinyUrlUtils.PREFIX);    	
    }
    
    //-----------------------------------------------------------------------
    public void createOrUpdateMedia(
        CrxObject object,
        String contentType,
        String contentName,
        InputStream content
    ) throws IOException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(object);
        Collection<Media> medias = object.getMedia();
        Media media = null;
        for(Media attachment: medias) {
            if((contentName != null) && contentName.equals(attachment.getContentName())) {
                media = attachment;
                break;
            }
        }
        if(media == null) {    	    	
	        media = pm.newInstance(Media.class);
	        object.addMedia(
	            false,
	            this.getUidAsString(),
	            media
	        );
	        media.setContentName(
	            contentName == null ?
	                Utils.toFilename(contentType) :
	                contentName
	        );
        }
        media.setContentMimeType(contentType);    
        media.setContent(
            BinaryLargeObjects.valueOf(content)
        );
        // inherit the object's owning groups
        media.getOwningGroup().addAll(
            object.getOwningGroup()
        );
    }
    
    //-------------------------------------------------------------------------
    public static String toS(
        Object obj
    ) {
        return obj == null
          ? ""
          : (obj instanceof Collection) && ((Collection)obj).size() > 0
            ? ((Collection)obj).iterator().next().toString()
            : obj.toString();
    }
    
    //-------------------------------------------------------------------------
    public String toNbspS(
        Object obj
    ) {
        return toS(obj).replace(" ", "&nbsp;");
    }
    
    //-------------------------------------------------------------------------
    public String getTitle(
    	RefObject_1_0 refObj,
    	Codes codes,
    	short locale,
    	boolean asShortTitle
    ) throws ServiceException {
    	if(refObj == null) {
    		return "#NULL";
    	}
    	if(JDOHelper.isNew(refObj) || !JDOHelper.isPersistent(refObj)) {
    		return "Untitled";
    	}        
    	try {
    		PersistenceManager pm = JDOHelper.getPersistenceManager(refObj);
    		String localeAsString = "en_US";
    		try {
    			localeAsString = (String)codes.getShortText("locale", (short)0, true, true).get(locale);
    		} catch(Exception e) {}
    		String language = "en";
    		if(localeAsString.indexOf("_") > 0) {
    			language = localeAsString.substring(0, 2);
    		}
    		DateFormat dateFormat = Utils.getDateFormat(language);
    		DateFormat timeFormat =  Utils.getTimeFormat(language);
    		DecimalFormat decimalFormat = Utils.getDecimalFormat(language);          
    		if(refObj instanceof Account) {
    			Account obj = (Account)refObj;
    			return toNbspS(obj.getFullName());
    		} else if(refObj instanceof ProductBasePrice) {
    			ProductBasePrice obj = (ProductBasePrice)refObj;
    			Map<Object,Object> currencyTexts = codes.getLongText("currency", locale, true, true);
    			try {
    				return toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " " + toS(currencyTexts.get(new Short(obj.getPriceCurrency())));
    			}
    			catch(Exception e) {
    				return toS(obj.getPrice() == null ? "N/A" : decimalFormat.format(obj.getPrice().doubleValue())) + " N/A";                  
    			}
    		} else if(refObj instanceof PriceListEntry) {
    			PriceListEntry obj = (PriceListEntry)refObj;
    			return toS(obj.getProductName());
    		} else if(refObj instanceof Activity) {
    			Activity obj = (Activity)refObj;
    			return toS(obj.getActivityNumber()).trim() + ": " + toS(obj.getName());
    		} else if(refObj instanceof ActivityProcessState) {
    			ActivityProcessState obj = (ActivityProcessState)refObj;
    			return toNbspS(obj.getName());
    		} else if(refObj instanceof ActivityFollowUp) {
    			ActivityFollowUp followUp = (ActivityFollowUp)refObj;
    			Activity activity = null;
    			// In case of NO_PERMISSION
    			try {
    				activity = (Activity)pm.getObjectById(
    					followUp.refGetPath().getParent().getParent()
    				);
    				activity.getName();
    			} 
    			catch(Exception e) {}
    			return 
    			(activity == null ? "" : getTitle(activity, codes, locale, asShortTitle) + ": ") + 
    			toS(followUp.getTitle());
    		} else if(refObj instanceof ActivityGroupAssignment) {
    			ActivityGroupAssignment obj = (ActivityGroupAssignment)refObj;
    			return obj == null ? 
    				"Untitled" : 
    					getTitle(obj.getActivityGroup(), codes, locale, asShortTitle);
    		} else if(refObj instanceof AddressGroupMember) {
    			AddressGroupMember member = (AddressGroupMember)refObj;
    			org.opencrx.kernel.account1.jmi1.AccountAddress address = member.getAddress();
    			if(address instanceof PhoneNumber) {
    				Account account = 
    					(Account)pm.getObjectById(
    						address.refGetPath().getParent().getParent()
    					);
    				return getTitle(address, codes, locale, asShortTitle) + " / " + account.getFullName();
    			}
    			else {
    				return address  == null ? 
    					"Untitled" : 
    						getTitle(address, codes, locale, asShortTitle);                
    			}
    		} else if(refObj instanceof AbstractActivityParty) {
    			RefObject_1_0 party = null;
    			if(
    				(refObj instanceof EMailRecipient) ||
    				(refObj instanceof PhoneCallRecipient)
    			) {
    				org.opencrx.kernel.account1.jmi1.AccountAddress address = null;
    				if(refObj instanceof EMailRecipient) {
    					EMailRecipient recipient = (EMailRecipient)refObj;
    					address = recipient.getParty();
    					if(address instanceof EMailAddress) {
    						String title = getTitle(address, codes, locale, asShortTitle);
    						if(asShortTitle) {
    							return title;
    						}
    						else {
    							EMail email = (EMail)pm.getObjectById(
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
    					PhoneCallRecipient recipient = (PhoneCallRecipient)refObj;
    					address = recipient.getParty();                      
    				}
    				if(address instanceof PhoneNumber) {
    					Account account = 
    						(Account)pm.getObjectById(
    							address.refGetPath().getParent().getParent()
    						);
    					return getTitle(address, codes, locale, asShortTitle) + " / " + account.getFullName();
    				}
    				else {
    					return address  == null ? 
    						"Untitled" : 
    							getTitle(address, codes, locale, asShortTitle);                
    				}                  
    			}
    			else if(refObj instanceof EMailRecipientGroup) {
    				party = ((EMailRecipientGroup)refObj).getParty();
    			}
    			else if(refObj instanceof IncidentParty) {
    				party = ((IncidentParty)refObj).getParty();
    			}
    			else if(refObj instanceof MailingRecipient) {
    				party = ((MailingRecipient)refObj).getParty();
    			}
    			else if(refObj instanceof MailingRecipientGroup) {
    				party = ((MailingRecipientGroup)refObj).getParty();
    			}
    			else if(refObj instanceof MeetingParty) {
    				party = ((MeetingParty)refObj).getParty();
    			}
    			else if(refObj instanceof TaskParty) {
    				party = ((TaskParty)refObj).getParty();
    			}
    			else if(refObj instanceof PhoneCallRecipientGroup) {
    				party = ((PhoneCallRecipientGroup)refObj).getParty();                  
    			}
    			String emailHint = ((AbstractActivityParty)refObj).getEmailHint();
    			return party == null ? 
    				"Untitled" :
    					getTitle(party, codes, locale, asShortTitle) + (emailHint == null ? "" : " (" + emailHint + ")");
    		} else if(refObj instanceof org.opencrx.kernel.contract1.jmi1.AccountAddress) {
    			return refObj.refGetValue("address") == null ? 
    				"Untitled" : 
    					getTitle((RefObject_1_0)refObj.refGetValue("address"), codes, locale, asShortTitle);
    		} else if(refObj instanceof EMailAddressable) {
    			return "* " + toS(refObj.refGetValue("emailAddress"));
    		} else if(refObj instanceof SalesContractPosition) {
    			return toS(refObj.refGetValue("lineItemNumber"));
    		} else if(refObj instanceof AbstractProduct) {
    			AbstractProduct product = (AbstractProduct)refObj;
    			return product.getProductNumber() == null || product.getProductNumber().length() == 0 || product.getProductNumber().equals(product.getName()) ? 
    				toS(product.getName()) : 
    					toS(product.getName() + " / " + product.getProductNumber()); 
    		} else if(refObj instanceof PostalAddressable) {
    			String address = "";
    			int nLines = 0;
    			for(String l: (List<String>)refObj.refGetValue("postalAddressLine")) {
    				String line = toS(l);
    				if(nLines > 0) address += "<br />";
    				address += line;
    				nLines++;
    			}
    			for(String l: (List<String>)refObj.refGetValue("postalStreet")) {
    				String street = toS(l);
    				if(nLines > 0) address += "<br />";
    				address += street;
    				nLines++;
    			}
    			Object postalCountry = refObj.refGetValue("postalCountry");
    			String postalCountryS = postalCountry == null ? 
    				"" : 
    					codes == null ? 
    						"" + postalCountry : 
    							toS(codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", locale, true, true).get(postalCountry));
    			return address + "<br />" + toS(refObj.refGetValue("postalCode")) + " " + toS(refObj.refGetValue("postalCity")) + "<br />" + postalCountryS;
    		} else if(refObj instanceof PhoneNumberAddressable) {
    			return "* " + toS(refObj.refGetValue("phoneNumberFull"));
    		} else if(refObj instanceof RoomAddressable) {
    			RoomAddressable obj = (RoomAddressable)refObj;
    			if(refObj instanceof Addressable) {              
    				AbstractBuildingUnit building = ((Addressable)refObj).getBuilding();
    				if(building == null) {
    					return toS(obj.getRoomNumber());
    				}
    				else {
    					return getTitle(building, codes, locale, asShortTitle) + " " +  toS(obj.getRoomNumber());
    				}
    			}
    			else {
    				return toS(obj.getRoomNumber());
    			}
    		} else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Rating) {
    			return toS(refObj.refGetValue("ratingLevel"));
    		} else if(refObj instanceof RevenueReport) {
    			return toS(refObj.refGetValue("reportNumber"));
    		} else if(refObj instanceof WebAddressable) {
    			return "* " + toS(refObj.refGetValue("webUrl"));
    		} else if(refObj instanceof CodeValueEntry) {
    			return toS(refObj.refGetValue("shortText"));
    		} else if(refObj instanceof org.opencrx.kernel.generic.jmi1.Description) {
    			return toS(refObj.refGetValue("language"));
    		} else if(refObj instanceof Document) {
    			Document document = (Document)refObj;
    			if(document.getQualifiedName() != null) {
    				return toS(document.getQualifiedName());
    			}
    			else if(document.getName() != null) {
    				return toS(document.getName());                  
    			}
    			else {
    				return toS(document.getDocumentNumber());
    			}
    		} else if(refObj instanceof Member) {
    			return (refObj.refGetValue("account") == null ? "Untitled" : getTitle((RefObject_1_0)refObj.refGetValue("account"), codes, locale, asShortTitle));
    		} else if(refObj instanceof ContactRelationship) {
    			return (refObj.refGetValue("toContact") == null ? "Untitled" : getTitle((RefObject_1_0)refObj.refGetValue("toContact"), codes, locale, asShortTitle));
    		} else if(refObj instanceof org.opencrx.kernel.home1.jmi1.UserHome) {
    			org.opencrx.kernel.home1.jmi1.UserHome userHome = (org.opencrx.kernel.home1.jmi1.UserHome)refObj;
    			return getTitle(userHome.getContact(), codes, locale, asShortTitle);
    		} else if(refObj instanceof org.opencrx.kernel.home1.jmi1.AccessHistory) {
    			return (refObj.refGetValue("reference") == null ? "Untitled" : getTitle((RefObject_1_0)refObj.refGetValue("reference"), codes, locale, asShortTitle));
    		} else if(refObj instanceof org.opencrx.kernel.home1.jmi1.WfProcessInstance) {
    			return (refObj.refGetValue("process") == null ? "Untitled" : getTitle((RefObject_1_0)refObj.refGetValue("process"), codes, locale, asShortTitle) + " " + toS(refObj.refGetValue("startedOn")));
    		} else if(refObj instanceof org.opencrx.kernel.base.jmi1.AuditEntry) {
    			Object createdAt = refObj.refGetValue(SystemAttributes.CREATED_AT);
    			return (refObj.refGetValue(SystemAttributes.CREATED_AT) == null) ? "Untitled" : dateFormat.format(createdAt) + " " + timeFormat.format(createdAt);
    		} else if(refObj instanceof org.opencrx.kernel.base.jmi1.Note) {
    			return toS(refObj.refGetValue("title"));
    		} else if(refObj instanceof org.opencrx.kernel.base.jmi1.Chart) {
    			return toS(refObj.refGetValue("description"));
    		} else if(refObj instanceof org.opencrx.kernel.model1.jmi1.Element) {
    			String title = toS(refObj.refGetValue("qualifiedName"));              
    			if(title.indexOf(":") > 0) {
    				int pos = title.lastIndexOf(":");
    				title = title.substring(pos + 1) + " (" + title.substring(0, pos) + ")";
    			}
    			return title;
    		} else if(refObj instanceof DepotEntity) {
    			DepotEntity obj = (DepotEntity)refObj;
    			return obj.getDepotEntityNumber() == null ? toS(obj.getName()) : toS(obj.getDepotEntityNumber());
    		} else if(refObj instanceof DepotContract) {
    			DepotContract obj = (DepotContract)refObj;
    			return obj.getDepotHolderNumber() == null ? toS(obj.getName()) : toS(obj.getDepotHolderNumber());
    		} else if(refObj instanceof Depot) {
    			Depot obj = (Depot)refObj;
    			return obj.getDepotNumber() == null ? toS(obj.getName()) : toS(obj.getDepotNumber());          
    		} else if(refObj instanceof DepotPosition) {
    			DepotPosition obj = (DepotPosition)refObj;
    			String depotTitle = getTitle(obj.getDepot(), codes, locale, asShortTitle);
    			return depotTitle + " / " + toS(obj.getName());                    
    		} else if(refObj instanceof DepotReport) {
    			DepotReport obj = (DepotReport)refObj;
    			String depotTitle = getTitle(obj.getDepot(), codes, locale, asShortTitle);
    			return depotTitle + " / " + toS(obj.getName());                    
    		} else if(refObj instanceof DepotReportItemPosition) {
    			DepotReportItemPosition obj = (DepotReportItemPosition)refObj;
    			return obj.getPositionName();
    		} else if(refObj instanceof SimpleEntry) {
    			SimpleEntry obj = (SimpleEntry)refObj;
    			return toS(obj.getEntryValue());
    		} else if(refObj instanceof Media) {
    			Media obj = (Media)refObj;
    			return toS(obj.getContentName());
    		} else if(refObj instanceof ContractRole) {
    			ContractRole contractRole = (ContractRole)refObj;
    			return 
	    			getTitle(contractRole.getContract(), codes, locale, asShortTitle) + " / " + 
	    			getTitle(contractRole.getAccount(), codes, locale, asShortTitle) + " / " + 
	    			getTitle(contractRole.getContractReferenceHolder(), codes, locale, asShortTitle);
    		} else if(refObj instanceof InvolvedObject) {
    			InvolvedObject involved = (InvolvedObject)refObj;
    			return getTitle(involved.getInvolved(), codes, locale, asShortTitle);
    		} else if(refObj instanceof AccountAssignment) {
    			AccountAssignment obj = (AccountAssignment)refObj;
    			return getTitle(obj.getAccount(), codes, locale, asShortTitle);
    		} else if(refObj instanceof Uom) {
    			return ((Uom)refObj).getName();
    		} else if(refObj instanceof AbstractPriceLevel) {
    			return toS(((AbstractPriceLevel)refObj).getName());
    		} else if(refObj instanceof DocumentRevision) {
    			if(refObj instanceof MediaContent) {
    				return toS(((MediaContent)refObj).getContentName());
    			} else {
    				return toS(((DocumentRevision)refObj).getName());
    			}
    		} else if(refObj instanceof InventoryItem) {
				return toS(((InventoryItem)refObj).getName());    			
    		} else {
    			return null;
    		}
    	}
    	catch(Exception e) {
    		throw new ServiceException(e);
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

	public static final String COMMENT_SEPARATOR_BOT = "//"; // at beginning of text
	public static final String COMMENT_SEPARATOR_EOT = " //"; // at end of text

	public static final List<String> ASSIGN_TO_ME_ATTRIBUTES = Arrays.asList("assignedTo", "salesRep", "ratedBy");

}

//--- End of File -----------------------------------------------------------

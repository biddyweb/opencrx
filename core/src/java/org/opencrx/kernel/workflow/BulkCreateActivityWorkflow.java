/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: BulkCreateActivityWorkflow
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2013, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.workflow;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.opencrx.kernel.account1.cci2.AccountAddressQuery;
import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.AccountAddress;
import org.opencrx.kernel.account1.jmi1.AccountFilterGlobal;
import org.opencrx.kernel.account1.jmi1.AddressFilterGlobal;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.account1.jmi1.Group;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.AddressGroupMemberQuery;
import org.opencrx.kernel.activity1.cci2.EMailQuery;
import org.opencrx.kernel.activity1.cci2.EMailRecipientQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.activity1.jmi1.AddressGroup;
import org.opencrx.kernel.activity1.jmi1.AddressGroupMember;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.EMailRecipient;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.Documents;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.backend.Workflows;
import org.opencrx.kernel.document1.jmi1.Document;
import org.opencrx.kernel.document1.jmi1.MediaContent;
import org.opencrx.kernel.generic.cci2.LocalizedFieldQuery;
import org.opencrx.kernel.generic.jmi1.LocalizedField;
import org.opencrx.kernel.generic.jmi1.LocalizedFieldContainer;
import org.opencrx.kernel.home1.jmi1.WfProcessInstance;
import org.opencrx.kernel.utils.ScriptUtils;
import org.opencrx.kernel.utils.WorkflowHelper;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.base.persistence.cci.UserObjects;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Codes;
import org.w3c.cci2.BinaryLargeObjects;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * BulkCreateActivityWorkflow
 *
 */
public class BulkCreateActivityWorkflow extends Workflows.AsynchronousWorkflow {
    
	/**
	 * CreationType
	 *
	 */
	public enum CreationType {
		CREATE,
		CREATE_CONFIRMED,
		CREATE_TEST,
		CREATE_TEST_CONFIRMED
	}

	/**
	 * ActivityCreatorThread
	 *
	 */
	public class ActivityCreatorThread extends Thread {
	
		/**
		 * Constructor.
		 * 
		 * @param wfProcessInstanceIdentity
		 * @param activityCreatorIdentity
		 * @param accountAndAddressXris
		 * @param selectedAccountsOnly
		 * @param excludeNoBulkEMail
		 * @param name
		 * @param description
		 * @param detailedDescription
		 * @param dueBy
		 * @param priority
		 * @param scheduledEnd
		 * @param scheduledStart
		 * @param emailSenderIdentity
		 * @param emailAddressUsages
		 * @param emailMessageSubject
		 * @param emailMessageBody
		 * @param defaultPlaceHolders
		 * @param locale
		 * @param codes
		 */
		public ActivityCreatorThread(
			PersistenceManagerFactory pmf,
			Path wfProcessInstanceIdentity,
			Path activityCreatorIdentity,
			List<Path> accountAndAddressXris,
			int beginIndex,
			int endIndex,			
			boolean selectedAccountsOnly,
			Boolean excludeNoBulkEMail,
			String name,
			String description,
			String detailedDescription,
			Date dueBy,
			short priority,
			Date scheduledEnd,
			Date scheduledStart,
			Path emailSenderIdentity,
			List<Short> emailAddressUsages,
			String emailMessageSubject,
			String emailMessageBody,
			Properties defaultPlaceHolders,
			short locale,
			Codes codes
		) throws ServiceException {
    	    PersistenceManager pm = pmf.getPersistenceManager(
    	    	wfProcessInstanceIdentity.get(6),
    	    	null
    	    );
    	    UserObjects.setBulkLoad(pm, true);
			this.activitySegment = Activities.getInstance().getActivitySegment(
				pm,
				wfProcessInstanceIdentity.get(2), 
				wfProcessInstanceIdentity.get(4)
			);
			this.wfProcessInstanceIdentity = wfProcessInstanceIdentity;
			this.activityCreator = (ActivityCreator)pm.getObjectById(activityCreatorIdentity);
			this.selectedAccountsOnly = selectedAccountsOnly;
			this.excludeNoBulkEMail = excludeNoBulkEMail;
			this.accountAndAddressXris = accountAndAddressXris;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.name = name;
			this.description = description;
			this.detailedDescription = detailedDescription;
			this.dueBy = dueBy;
			this.priority = priority;
			this.scheduledEnd = scheduledEnd;
			this.scheduledStart = scheduledStart;
			this.emailSender = emailSenderIdentity == null ? null : (AccountAddress)pm.getObjectById(emailSenderIdentity);
			this.emailAddressUsages = emailAddressUsages;
			this.emailMessageSubject = emailMessageSubject;
			this.emailMessageBody = emailMessageBody;
			this.defaultPlaceHolders = defaultPlaceHolders;
			this.locale = locale;
			this.codes = codes;
		}

		/**
		 * Update e-mail recipient.
		 * 
		 * @param email
		 * @param emailAddress
		 * @param partyType
		 * @throws ServiceException
		 */
		protected void updateEMailRecipient(
			EMail email,
			EMailAddress emailAddress,
			Activities.PartyType partyType
		) throws ServiceException {
			javax.jdo.PersistenceManager pm = javax.jdo.JDOHelper.getPersistenceManager(email);
			EMailRecipientQuery query = 
				(EMailRecipientQuery)pm.newQuery(EMailRecipient.class);
			query.partyType().equalTo(partyType.getValue());
			query.orderByCreatedAt().descending();
			List<EMailRecipient> emailRecipients = email.getEmailRecipient(query);
			EMailRecipient emailRecipient = null;
			if(emailRecipients.isEmpty()) {
				emailRecipient = pm.newInstance(EMailRecipient.class);
				email.addEmailRecipient(
					Base.getInstance().getUidAsString(), 
					emailRecipient
				);
			} else {
				emailRecipient = emailRecipients.iterator().next();
			}		
			emailRecipient.setEmailHint(emailAddress.getEmailAddress());
			emailRecipient.setParty(emailAddress);
			emailRecipient.setPartyType(partyType.getValue());
			emailRecipient.setPartyStatus(Activities.PartyStatus.ACCEPTED.getValue());		
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
        @Override
        public void run(
        ) {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(this.activitySegment);
        	try {
	        	for(int i = this.beginIndex; i < this.endIndex && i < this.accountAndAddressXris.size(); i++) {
	        		Path id = this.accountAndAddressXris.get(i);
					AccountAddress address = null;
					Account account = null;
					if(this.selectedAccountsOnly) {
						try {
							account = (Account)pm.getObjectById(id);
						} catch (Exception e) {
							new ServiceException(e).log();
						}
					} else {
						try {
							address = (AccountAddress)pm.getObjectById(id);
							account = (Account)pm.getObjectById(address.refGetPath().getParent().getParent());
						} catch (Exception e) {
							new ServiceException(e).log();
						}
					}
					ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
					EMailQuery emailQuery = (EMailQuery)pm.newQuery(EMail.class);					
					List<Activity> activities = null;
					// match recipient address
					if(
						account != null &&
						address instanceof EMailAddress &&
						this.activityCreator.getActivityType() != null &&
						this.activityCreator.getActivityType().getActivityClass() == Activities.ActivityClass.EMAIL.getValue()
					) {
						// match recipient e-mail address
						EMailRecipientQuery emailRecipientQuery = 
							(EMailRecipientQuery)pm.newQuery(EMailRecipient.class);
						emailRecipientQuery.thereExistsParty().equalTo(address);
						emailQuery.thereExistsEmailRecipient().elementOf(
							org.openmdx.base.persistence.cci.PersistenceHelper.asSubquery(emailRecipientQuery)
						);
						if(account instanceof Contact) {
							emailQuery.thereExistsReportingContact().equalTo(account);						
						} else if(account instanceof Account) {
							emailQuery.thereExistsReportingAccount().equalTo(account);
						}
						// match ActivityCreator
						emailQuery.thereExistsLastAppliedCreator().equalTo(this.activityCreator);
						emailQuery.orderByCreatedAt().descending();
						activities = this.activitySegment.getActivity(emailQuery);
					} else {
						// match Account with reportingContact/reportingAccount
						if(account instanceof Contact) {
							activityQuery.thereExistsReportingContact().equalTo(account);						
						} else if(account instanceof Account) {
							activityQuery.thereExistsReportingAccount().equalTo(account);
						}
						// match ActivityCreator
						activityQuery.thereExistsLastAppliedCreator().equalTo(this.activityCreator);
						activityQuery.orderByCreatedAt().descending();
						activities = this.activitySegment.getActivity(activityQuery);						
					}
					Activity activity = null;
					boolean doUpdateAddress = false;
					// Create if activity does not exist
					if(activities.isEmpty()) {
						String activityName = this.name + " / " + account.getFullName() + (account.getAliasName() == null ? "" : " / " + account.getAliasName());						
						NewActivityParams newActivityParams = Structures.create(
							NewActivityParams.class,
							Datatypes.member(NewActivityParams.Member.description, this.description),
							Datatypes.member(NewActivityParams.Member.detailedDescription, this.detailedDescription),
							Datatypes.member(NewActivityParams.Member.dueBy, this.dueBy),
							Datatypes.member(NewActivityParams.Member.icalType, ICalendar.ICAL_TYPE_NA),
							Datatypes.member(NewActivityParams.Member.name, activityName),
							Datatypes.member(NewActivityParams.Member.priority, this.priority),
							Datatypes.member(NewActivityParams.Member.scheduledEnd, this.scheduledEnd),
							Datatypes.member(NewActivityParams.Member.scheduledStart, this.scheduledStart)
						);
						try {
							pm.currentTransaction().begin();
							org.opencrx.kernel.activity1.jmi1.NewActivityResult result = this.activityCreator.newActivity(newActivityParams);							
							pm.currentTransaction().commit();
							activity = result.getActivity();
							this.numberOfActivitiesCreated++;
						} catch(Exception e) {
							try {
								pm.currentTransaction().rollback();
							} catch(Exception e0) {}
						}
						doUpdateAddress = true;
					} else {
						activity = activities.iterator().next();
						if(
							activity.getCreationContext() == null || 
							!this.wfProcessInstanceIdentity.equals(activity.getCreationContext().refGetPath())
						) {
							doUpdateAddress = true;
							this.numberOfActivitiesUpdated++;
						} else {
							this.numberOfActivitiesSkipped++;
						}
					}
					if(doUpdateAddress) {
						try {
							String replacedDescription = null;
							try {
								replacedDescription = BulkCreateActivityWorkflow.this.replacePlaceHolders(
									account, 
									this.locale, 
									this.description, 
									this.defaultPlaceHolders, 
									this.codes
								);
							} catch (Exception ignore) {}
							String replacedDetailedDescription = null;
							try {
								replacedDetailedDescription = BulkCreateActivityWorkflow.this.replacePlaceHolders(
									account, 
									this.locale, 
									this.detailedDescription, 
									this.defaultPlaceHolders, 
									this.codes
								);
							} catch (Exception ignore) {}
							String replacedMessageSubject = null;
							try {
								replacedMessageSubject = BulkCreateActivityWorkflow.this.replacePlaceHolders(
									account, 
									this.locale, 
									this.emailMessageSubject, 
									this.defaultPlaceHolders, 
									this.codes
								);
							} catch (Exception ignore) {}
							String replacedMessageBody = null;
							try {
								replacedMessageBody = BulkCreateActivityWorkflow.this.replacePlaceHolders(
									account, 
									this.locale, 
									this.emailMessageBody, 
									this.defaultPlaceHolders, 
									this.codes
								);
							} catch (Exception ignore) {}
							pm.currentTransaction().begin();
							activity.setDescription(replacedDescription);
							activity.setDetailedDescription(replacedDetailedDescription);
							activity.setCreationContext((WfProcessInstance)pm.getObjectById(this.wfProcessInstanceIdentity));
							boolean sendingEmailToContactAllowed = true;
							if(account instanceof Contact) {
								if(Boolean.TRUE.equals(((Contact)account).isDoNotEMail())) {
									sendingEmailToContactAllowed = false;
								}
								activity.setReportingContact((Contact)account);
							} else {
								activity.setReportingContact(null);
								activity.setReportingAccount(account);
							}
							if(activity instanceof EMail) {
								EMail email = (EMail)activity;
								email.setMessageSubject(replacedMessageSubject);
								email.setMessageBody(replacedMessageBody);
								// Sender / Recipient EMAIL_FROM
								email.setSender(this.emailSender);
								if(this.emailSender instanceof EMailAddress) {
									this.updateEMailRecipient(
										email,
										(EMailAddress)this.emailSender,
										Activities.PartyType.EMAIL_FROM
									);
								}
								// Recipient EMAIL_TO
								AccountAddress recipientAddress = null;
								if (address != null) {
									recipientAddress = address;
								} else {
									for(short usage: this.emailAddressUsages) {
										List<AccountAddress> addresses = Accounts.getInstance().getAccountAddresses(
											account, 
											usage
										);
										for(AccountAddress adr: addresses) {
											if(adr instanceof EMailAddress) {
												recipientAddress = recipientAddress == null
													? adr
													: Boolean.TRUE.equals(adr.isMain()) ? adr : recipientAddress;
											}
										}
										if(recipientAddress != null) {
											break;
										}
									}
								}
								if(
									recipientAddress instanceof EMailAddress &&
									(!Boolean.TRUE.equals(this.excludeNoBulkEMail) || sendingEmailToContactAllowed)
								) {
									this.updateEMailRecipient(
										email,
										(EMailAddress)recipientAddress,
										Activities.PartyType.EMAIL_TO
									);
								} else {
									// delete email as there is no recipient
									email.refDelete();
									this.counter--;
									if(activities.isEmpty()) {
										this.numberOfActivitiesCreated--;
									} else {
										this.numberOfActivitiesUpdated--;
									}
									this.numberOfAccountsWithoutEmailAddress++;
								}
							}
							pm.currentTransaction().commit();
			   			} catch(Exception e) {
			   				try {
			   					pm.currentTransaction().rollback();
			   				} catch(Exception e0) {}
			   			}
					}
	        	}
        	} catch(Exception e) {
        		new ServiceException(e).log();
        	} finally {
        		try {
        			pm.close();
        		} catch(Exception ignore) {}
        	}
        }

        /**
		 * @return the counter
		 */
		public int getCounter() {
			return counter;
		}

		/**
		 * @return the numberOfActivitiesCreated
		 */
		public int getNumberOfActivitiesCreated() {
			return numberOfActivitiesCreated;
		}

		/**
		 * @return the numberOfActivitiesUpdated
		 */
		public int getNumberOfActivitiesUpdated() {
			return numberOfActivitiesUpdated;
		}

		/**
		 * @return the numberOfAccountsWithoutEmailAddress
		 */
		public int getNumberOfAccountsWithoutEmailAddress() {
			return numberOfAccountsWithoutEmailAddress;
		}

		/**
		 * @return the numberOfActivitiesSkipped
		 */
		public int getNumberOfActivitiesSkipped() {
			return numberOfActivitiesSkipped;
		}
        
        //-------------------------------------------------------------------
        // Members
        //-------------------------------------------------------------------        
        private int counter = 0;
		private int numberOfActivitiesCreated = 0;
        private int numberOfActivitiesUpdated = 0;
        private int numberOfAccountsWithoutEmailAddress = 0;
        private int numberOfActivitiesSkipped = 0;

        private final org.opencrx.kernel.activity1.jmi1.Segment activitySegment;
        private final Path wfProcessInstanceIdentity;
        private final ActivityCreator activityCreator;
        private final List<Path> accountAndAddressXris;
        private final int beginIndex;
        private final int endIndex;
        private final boolean selectedAccountsOnly;
        private final Boolean excludeNoBulkEMail;
        private final String name;
        private final String description;
        private final String detailedDescription;
        private final Date dueBy;
        private final short priority;
        private final Date scheduledEnd;
        private final Date scheduledStart;
        private final AccountAddress emailSender;
        private final List<Short> emailAddressUsages;
        private final String emailMessageSubject;
        private final String emailMessageBody;
        private final Properties defaultPlaceHolders;
        private final short locale;
        private final Codes codes; 
	}

	/**
	 * Get workflow-specific script.
	 * 
	 * @param context
	 * @return
	 * @throws ServiceException
	 */
	private Class<?> getScript(
		RefObject_1_0 context
	) throws ServiceException {
		if(
			this.lastRefreshAt == null ||
			System.currentTimeMillis()	> this.lastRefreshAt + REFRESH_PERIOD_MILLIS
		) {
			try {
				this.lastRefreshAt = System.currentTimeMillis();
				PersistenceManager pm = JDOHelper.getPersistenceManager(context);
				String providerName = context.refGetPath().get(2);
				String segmentName = context.refGetPath().get(4);		
				org.opencrx.kernel.document1.jmi1.Segment documentSegment =
					Documents.getInstance().getDocumentSegment(pm, providerName, segmentName);
				Document scriptDocument = Documents.getInstance().findDocument(
					BulkCreateActivityWorkflow.class.getSimpleName() + ".script", 
					documentSegment
				);
				if(
					scriptDocument == null || 
					!(scriptDocument.getHeadRevision() instanceof MediaContent)
				) {
					return null;
				}
				MediaContent script = (MediaContent)scriptDocument.getHeadRevision();
				if(
					this.scriptLastModifiedAt == null || 
					script.getModifiedAt().compareTo(this.scriptLastModifiedAt) > 0
				) {
					this.scriptLastModifiedAt = script.getModifiedAt();
					ByteArrayOutputStream content = new ByteArrayOutputStream();
					BinaryLargeObjects.streamCopy(
						script.getContent().getContent(), 
						0L, 
						content
					);
					this.script = ScriptUtils.getClass(
						content.toString("UTF-8")
					);
				}
			} catch(Exception e) {
				throw new ServiceException(e);
			}
		}
		return this.script;
	}

	/**
	 * Get place holder value.
	 * 
	 * @param name feature name
	 * @param defaultValue
	 * @param locale
	 * @param context null or a LocalizedFieldContainer
	 * @param codes if null, no code translation is made in case the value is of type short and the feature is coded.
	 * @return place holder value
	 * @throws ServiceException
	 */
	public String getPlaceHolderValue(
		String name,
		String defaultValue,
		short locale,
		RefObject_1_0 context,
		Codes codes
	) throws ServiceException {
		// Localized field
		if(context instanceof LocalizedFieldContainer) {
			PersistenceManager pm = JDOHelper.getPersistenceManager(context);
			LocalizedFieldContainer container = (LocalizedFieldContainer)context;
			LocalizedFieldQuery query = (LocalizedFieldQuery)pm.newQuery(LocalizedField.class);
			query.name().equalTo(name);
			query.locale().equalTo(locale);
			query.orderByCreatedAt().descending();
			List<LocalizedField> localizedFields = container.getLocalizedField(query);
			if(!localizedFields.isEmpty()) {
				return localizedFields.iterator().next().getLocalizedValue();
			}
		}
		// Feature on object
		try {
			Object value = context.refGetValue(name);
			if(value != null) {
				if(codes != null && value instanceof Short && name.endsWith("Code")) {
					@SuppressWarnings("unchecked")
                    Map<Object,Object> longTexts = codes.getLongText(
						context.refClass().refMofId() + ":" + name, 
						locale, 
						true, // codeAsKey
						true // includeAll
					);
					if(longTexts != null) {
						return (String)longTexts.get(value);
					}					
				} else {
					return value.toString();
				}
			}
		} catch(Exception e) {}
		// Feature with suffix 'Code' on object
		try {
			if(codes != null) {
				String nameWithCodeSuffix = name + "Code";
				Short code = (Short)context.refGetValue(nameWithCodeSuffix);
				if(code != null) {
					@SuppressWarnings("unchecked")
	                Map<Object,Object> longTexts = codes.getLongText(
						context.refClass().refMofId() + ":" + nameWithCodeSuffix, 
						locale, 
						true, // codeAsKey
						true // includeAll
					);
					if(longTexts != null) {
						return (String)longTexts.get(code);
					}
				}
			}
		} catch(Exception e) {}
		return defaultValue;
	}

	/**
	 * Replace place holder with its value.
	 * 
	 * @param context
	 * @param locale
	 * @param text
	 * @param defaultPlaceHolders
	 * @param codes
	 * @return
	 * @throws ServiceException
	 */
	public String replacePlaceHolders(
		RefObject_1_0 context,
		short locale,
		String text,
		Properties defaultPlaceHolders,
		Codes codes
	) throws ServiceException {
		if(text == null) return null;
		int pos = text.indexOf("${");
		while(pos >= 0) {
			int start = pos + 2;
			int end = start;
			while(text.charAt(end) != '}' && end < text.length()) {
				end++;
			}
			if(end < text.length()) {
				String placeHolderName = text.substring(start, end);
				Class<?> script = getScript(context);
				Method getPlaceHolderValueMethod = null;
				if(script != null) {
					try {
						getPlaceHolderValueMethod = script.getMethod(
							"get" + (Character.toUpperCase(placeHolderName.charAt(0)) + placeHolderName.substring(1)) + "Value",
							String.class, // defaultValue
							short.class, // locale
							RefObject_1_0.class, // context
							Codes.class // codes
						);
					} catch(Exception e) {}
				}
				String placeHolderValue = null;
				if(getPlaceHolderValueMethod != null) {
					try {
						placeHolderValue = (String)getPlaceHolderValueMethod.invoke(
							null, 
							defaultPlaceHolders.getProperty(placeHolderName),
							locale,
							context,
							codes
						);
					} catch(Exception e) {
						throw new ServiceException(e);
					}
				} else  {
					placeHolderValue = this.getPlaceHolderValue(
						placeHolderName,
						defaultPlaceHolders.getProperty(placeHolderName),
						locale,
						context,
						codes
					);
				}
				if(placeHolderValue == null) {
					placeHolderValue = "";
				}
				text = text.substring(0, pos) + placeHolderValue + text.substring(end + 1);
				end = pos + placeHolderValue.length();
			}
			pos = text.indexOf("${", end);
		}
		return text;
	}

	/**
	 * Update place holders in text.
	 * 
	 * @param placeHolders
	 * @param text
	 */
	public void updatePlaceHolders(
		Properties placeHolders,
		String text
	) {
		if(text == null) return;
		int pos = text.indexOf("${");
		while(pos >= 0) {
			int start = pos + 2;
			int end = start;
			while(text.charAt(end) != '}' && end < text.length()) {
				end++;
			}
			if(end < text.length()) {
				String placeHolderName = text.substring(start, end);
				if(placeHolders.get(placeHolderName) == null) {
					placeHolders.put(placeHolderName, "N/A");
				}
			}
			pos = text.indexOf("${", end);
		}
	}
	
	/**
	 * Create log entry.
	 * 
	 * @param wfProcessInstance
	 * @param numberOfAccounts
	 * @param numberOfAddresses
	 * @param numberOfAccountsWithoutEmailAddress
	 * @param numberOfActivitiesCreated
	 * @param numberOfActivitiesUpdated
	 * @throws ServiceException
	 */
	protected void createLogEntry(
		String name,
		WfProcessInstance wfProcessInstance,
		int numberOfActivities,
		int numberOfAccountsWithoutEmailAddress,
		int numberOfActivitiesCreated,
		int numberOfActivitiesUpdated,
		int numberOfActivitiesSkipped
	) throws ServiceException {
	    Map<String,Object> report = new HashMap<String,Object>();
	    report.put(
	    	"Total",
	    	Integer.toString(numberOfActivities)
	    );
	    report.put(
	    	"Missing e-mail",
	    	Integer.toString(numberOfAccountsWithoutEmailAddress)
	    );
	    report.put(
	    	"Created",
	    	Integer.toString(numberOfActivitiesCreated)
	    );
	    report.put(
	    	"Updated",
	    	Integer.toString(numberOfActivitiesUpdated)
	    );
	    report.put(
	    	"Skipped",
	    	Integer.toString(numberOfActivitiesSkipped)
	    );
	    report.put(
	    	"Pending",
	    	Integer.toString(numberOfActivities - numberOfAccountsWithoutEmailAddress - numberOfActivitiesCreated - numberOfActivitiesUpdated - numberOfActivitiesSkipped)
	    );
        WorkflowHelper.createLogEntry(
            wfProcessInstance,
            name,
            report.toString()
        );
	}

	/**
	 * Create log entry.
	 * 
	 * @param name
	 * @param wfProcessInstance
	 * @param numberOfActivities
	 * @throws ServiceException
	 */
	protected void createLogEntry(
		String name,
		WfProcessInstance wfProcessInstance,
		int numberOfActivities
	) throws ServiceException {
	    Map<String,Object> report = new HashMap<String,Object>();
	    report.put(
	    	"Total",
	    	Integer.toString(numberOfActivities)
	    );
        WorkflowHelper.createLogEntry(
            wfProcessInstance,
            name,
            report.toString()
        );
	}

	/* (non-Javadoc)
     * @see org.opencrx.kernel.backend.Workflows.AsynchronousWorkflow#execute(org.opencrx.kernel.home1.jmi1.WfProcessInstance)
     */
    @Override
    public void execute(
        WfProcessInstance wfProcessInstance
    ) throws ServiceException {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(wfProcessInstance);
        try {
        	PersistenceManagerFactory pmf = pm.getPersistenceManagerFactory();
    	    PersistenceManager pmUser = pmf.getPersistenceManager(
    	    	wfProcessInstance.refGetPath().get(6),
    	    	null
    	    );
    	    UserObjects.setBulkLoad(pmUser, true);
            Map<String,Object> params = WorkflowHelper.getWorkflowParameters(
            	(WfProcessInstance)pmUser.getObjectById(wfProcessInstance.refGetPath())
            );
            String providerName = wfProcessInstance.refGetPath().get(2);
    		Codes codes = new Codes(
    			(RefObject_1_0)pmUser.getObjectById(
    				new Path("xri://@openmdx*org.opencrx.kernel.code1").getDescendant("provider", providerName, "segment", "Root")
    			)
    		);
			Properties defaultPlaceHolders = new Properties();
			if(params.get(OPTION_DEFAULT_PLACEHOLDERS) instanceof String) {
				try {
					defaultPlaceHolders.load(new StringReader((String)params.get(OPTION_DEFAULT_PLACEHOLDERS)));
				} catch(Exception ignore) {}
			}                     
            CreationType creationType = CreationType.CREATE_TEST;
            if(params.get(OPTION_CREATION_TYPE) instanceof String) {
            	try {
	            	creationType = CreationType.valueOf(
	           			(String)params.get(OPTION_CREATION_TYPE)
	           		);
            	} catch(Exception ignore) {}
            }
            RefObject_1_0 accounts = null;
            if(params.get(OPTION_ACCOUNTS_SELECTOR) instanceof RefObject_1_0) {
            	accounts = (RefObject_1_0)pmUser.getObjectById(
           			((RefObject_1_0)params.get(OPTION_ACCOUNTS_SELECTOR)).refGetPath()
           		);
            }
            String name = null;
            if(params.get(OPTION_ACTIVITY_NAME) instanceof String) {
            	name = (String)params.get(OPTION_ACTIVITY_NAME);
            }
            String description = null;
            if(params.get(OPTION_ACTIVITY_DESCRIPTION) instanceof String) {
            	description = (String)params.get(OPTION_ACTIVITY_DESCRIPTION);
            }
            String detailedDescription = null;
            if(params.get(OPTION_ACTIVITY_DETAILED_DESCRIPTION) instanceof String) {
            	detailedDescription = (String)params.get(OPTION_ACTIVITY_DETAILED_DESCRIPTION);
            }
            short priority = 0;
            if(params.get(OPTION_ACTIVITY_PRIORITY) instanceof Number) {
            	priority = ((Number)params.get(OPTION_ACTIVITY_PRIORITY)).shortValue();
            }
            Date scheduledStart = null;
            if(params.get(OPTION_ACTIVITY_SCHEDULED_START) instanceof Date) {
            	scheduledStart = (Date)params.get(OPTION_ACTIVITY_SCHEDULED_START);
            }
            Date scheduledEnd = null;
            if(params.get(OPTION_ACTIVITY_SCHEDULED_END) instanceof Date) {
            	scheduledEnd = (Date)params.get(OPTION_ACTIVITY_SCHEDULED_END);
            }
            Date dueBy = null;
            if(params.get(OPTION_ACTIVITY_DUE_BY) instanceof Date) {
            	dueBy = (Date)params.get(OPTION_ACTIVITY_DUE_BY);
            }
            short locale = 0;
            if(params.get(OPTION_LOCALE) instanceof Number) {
            	locale = ((Number)params.get(OPTION_LOCALE)).shortValue();
            }
            AccountAddress emailSender = null;
            if(params.get(OPTION_EMAIL_SENDER) instanceof AccountAddress) {
            	emailSender = (AccountAddress)pmUser.getObjectById(
           			((AccountAddress)params.get(OPTION_EMAIL_SENDER)).refGetPath()
           		);
            }
            String emailMessageSubject = null;
            if(params.get(OPTION_EMAIL_MESSAGE_SUBJECT) instanceof String) {
            	emailMessageSubject = (String)params.get(OPTION_EMAIL_MESSAGE_SUBJECT);
            }
			String emailMessageBody = "";
			int idx = 0;
			while(params.get(OPTION_EMAIL_MESSAGE_BODY + idx) instanceof String) {
				emailMessageBody += params.get(OPTION_EMAIL_MESSAGE_BODY + idx);
				idx++;
			}
			List<Short> emailAddressUsages = new ArrayList<Short>();
			idx = 0;
			while(params.get(OPTION_EMAIL_ADDRESS_USAGE + idx) instanceof Number) {
				emailAddressUsages.add(
					((Number)params.get(OPTION_EMAIL_ADDRESS_USAGE + idx)).shortValue()
				);
				idx++;
			}
			Account testAccount = null;
            if(params.get(OPTION_TEST_ACCOUNT) instanceof Account) {
            	testAccount = (Account)pmUser.getObjectById(
           			((Account)params.get(OPTION_TEST_ACCOUNT)).refGetPath()
           		);
            }
			List<EMailAddress> testEMails = new ArrayList<EMailAddress>();
			idx = 0;
            while(params.get(OPTION_TEST_EMAIL + idx) instanceof EMailAddress) {
            	testEMails.add(
            		(EMailAddress)pmUser.getObjectById(
            			((EMailAddress)params.get(OPTION_TEST_EMAIL + idx)).refGetPath()
            		)
           		);
            }
            Boolean excludeNoBulkEMail = null;
            if(params.get(OPTION_EXCLUDE_NO_BULK_EMAIL) instanceof Boolean) {
            	excludeNoBulkEMail = (Boolean)params.get(OPTION_EXCLUDE_NO_BULK_EMAIL);
            }
            RefObject_1_0 targetObject = null;
            if(wfProcessInstance.getTargetObject() != null) {
            	try {
            		targetObject = (RefObject_1_0)pmUser.getObjectById(
            			new Path(wfProcessInstance.getTargetObject())
            		);
            	} catch(Exception e) {}
            }
            ActivityCreator activityCreator = targetObject instanceof ActivityCreator
            	? (ActivityCreator)targetObject
            	: null;
    	    // Create activities
            {
            	boolean selectedAccountsOnly = true;
        		Set<Path> accountXris = new TreeSet<Path>();
        		Set<Path> addressXris = new TreeSet<Path>();
        		List<Path> accountAndAddressXris = new ArrayList<Path>();            	
    			if(accounts instanceof AccountFilterGlobal) {
    				AccountQuery accountQuery = (AccountQuery)pmUser.newQuery(Account.class);
    				accountQuery.forAllDisabled().isFalse();
    				for(Account account: ((AccountFilterGlobal)accounts).getFilteredAccount(accountQuery)) {
    					try {
    						accountXris.add(account.refGetPath());
    					} catch (Exception e) {
    						new ServiceException(e).log();
    					}
    				}
    				accountAndAddressXris = new ArrayList<Path>(accountXris);
    			} else if(accounts instanceof Group) {
    				MemberQuery memberQuery = (MemberQuery)pmUser.newQuery(Member.class);
    				memberQuery.forAllDisabled().isFalse();
    				for(Member m: ((Group)accounts).getMember(memberQuery)) {
    					try {
    						if (m.getAccount() != null && !Boolean.TRUE.equals(m.getAccount().isDisabled())) {
    							accountXris.add(m.getAccount().refGetPath());
    						}
    					} catch (Exception e) {
    						new ServiceException(e).log();
    					}
    				}
    				accountAndAddressXris = new ArrayList<Path>(accountXris);
    			} else if(accounts instanceof AddressFilterGlobal) {
    				AccountAddressQuery accountAddressQuery = (AccountAddressQuery)pmUser.newQuery(AccountAddress.class);
    				accountAddressQuery.forAllDisabled().isFalse();
    				for(AccountAddress accountAddress: ((AddressFilterGlobal)accounts).getFilteredAddress(accountAddressQuery)) {
    					try {
    						addressXris.add(accountAddress.refGetPath());
    					} catch (Exception ignore) {}
    				}
    				selectedAccountsOnly = false;
    				accountAndAddressXris = new ArrayList<Path>(addressXris);
    			} else if(accounts instanceof AddressGroup) {
    				AddressGroupMemberQuery addressGroupMemberQuery = (AddressGroupMemberQuery)pmUser.newQuery(AddressGroupMember.class);
    				addressGroupMemberQuery.forAllDisabled().isFalse();
    				for(AddressGroupMember addressGroupMember: ((AddressGroup)accounts).getMember(addressGroupMemberQuery)) {
    					try {
    						if (addressGroupMember.getAddress() != null && !Boolean.TRUE.equals(addressGroupMember.getAddress().isDisabled())) {
    							addressXris.add(addressGroupMember.getAddress().refGetPath());
    						}
    					} catch (Exception ignore) {}
    				}
    				selectedAccountsOnly = false;
    				accountAndAddressXris = new ArrayList<Path>(addressXris);
    			}
    			if(selectedAccountsOnly) {
    				if(creationType == CreationType.CREATE_TEST_CONFIRMED) {
    					if(testAccount != null) {
   							accountAndAddressXris.add(0, testAccount.refGetPath());
    					}
    				}
    			} else {
    				if(creationType == CreationType.CREATE_TEST_CONFIRMED) {
    					// add optional test addresses if any
    					ActivityType actType = activityCreator.getActivityType(); 
    					if(actType != null && actType.getActivityClass() == Activities.ActivityClass.EMAIL.getValue()) {
    						for(EMailAddress testEMail: testEMails) {
   								accountAndAddressXris.add(0, testEMail.refGetPath());
    						}
    					}
    				}
    			}
    			if(
    				activityCreator != null &&
    				(creationType == CreationType.CREATE_CONFIRMED || 
    				creationType == CreationType.CREATE_TEST_CONFIRMED)
    			) {
            		int numberOfAccountsWithoutEmailAddress = 0;
            		int numberOfActivitiesCreated = 0;
            		int numberOfActivitiesUpdated = 0;
            		int numberOfActivitiesSkipped = 0;
    				this.createLogEntry(
    					"Report @" + new Date(),
    					wfProcessInstance, 
    					accountAndAddressXris.size(), 
    					numberOfAccountsWithoutEmailAddress, 
    					numberOfActivitiesCreated, 
    					numberOfActivitiesUpdated,
    					numberOfActivitiesSkipped
    				);
    				int counter = 0;
    				while(counter < accountAndAddressXris.size()) {
    					// Create activities in parallel. Start MAX_THREADS threads where 
    					// each thread processes a batch of THREAD_BATCH_SIZE addresses
    					List<ActivityCreatorThread> activityCreatorThreads = new ArrayList<ActivityCreatorThread>();    					
    					for(int tIndex = 0; tIndex < MAX_THREADS; tIndex++) {
    						ActivityCreatorThread t = new ActivityCreatorThread(
	    						pmf, 
	    						wfProcessInstance.refGetPath(), 
	    						activityCreator.refGetPath(), 
	    						creationType == CreationType.CREATE_TEST_CONFIRMED 
	    							? accountAndAddressXris.subList(0, Math.min(NUM_OF_TEST_ACTIVITIES, accountAndAddressXris.size()))
	    							: accountAndAddressXris,
	    						counter + tIndex * THREAD_BATCH_SIZE,
	    						counter + (tIndex + 1) * THREAD_BATCH_SIZE,
	    						selectedAccountsOnly,
	    						excludeNoBulkEMail, 
	    						name, 
	    						description, 
	    						detailedDescription, 
	    						dueBy, 
	    						priority, 
	    						scheduledEnd, 
	    						scheduledStart, 
	    						emailSender == null ? null : emailSender.refGetPath(), 
	    						emailAddressUsages, 
	    						emailMessageSubject, 
	    						emailMessageBody, 
	    						defaultPlaceHolders, 
	    						locale, 
	    						codes
	    					);
	    					t.start();
	    					activityCreatorThreads.add(t);	    					
    					}
    					// Wait for termination
    					for(ActivityCreatorThread t: activityCreatorThreads) {
    						try {
    							t.join();
    						} catch(Exception ignore) {}
    						numberOfAccountsWithoutEmailAddress += t.getNumberOfAccountsWithoutEmailAddress();
    						numberOfActivitiesCreated += t.getNumberOfActivitiesCreated();
    						numberOfActivitiesUpdated += t.getNumberOfActivitiesUpdated();
    						numberOfActivitiesSkipped += t.getNumberOfActivitiesSkipped();
    					}
						this.createLogEntry(
							"Report @" + new Date(),
							wfProcessInstance, 
							accountAndAddressXris.size(),
							numberOfAccountsWithoutEmailAddress, 
							numberOfActivitiesCreated, 
							numberOfActivitiesUpdated,
							numberOfActivitiesSkipped
						);
						counter += MAX_THREADS * THREAD_BATCH_SIZE;
    				}
    				this.createLogEntry(
    					"Report - Complete",
    					wfProcessInstance, 
    					accountAndAddressXris.size(), 
    					numberOfAccountsWithoutEmailAddress, 
    					numberOfActivitiesCreated, 
    					numberOfActivitiesUpdated,
						numberOfActivitiesSkipped
    				);
    			} else if(
    				creationType == CreationType.CREATE ||
    				creationType == CreationType.CREATE_TEST
    			) {
    				this.createLogEntry(
    					"Report - Complete",
    					wfProcessInstance, 
    					accountAndAddressXris.size() 
    				);
    			}
            }
    	    pmUser.close();
        } catch(Exception e) {
        	SysLog.warning("Can not perform BulkCreateActivity (reason=Exception)", e.getMessage());
            ServiceException e0 = new ServiceException(e);
            SysLog.detail(e0.getMessage(), e0.getCause());
            WorkflowHelper.createLogEntry(
                wfProcessInstance,
                "Can not perform BulkCreateActivity: Exception",
                e.getMessage()
            );
            throw e0;
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
	public static final int NUM_OF_TEST_ACTIVITIES = 3;
	public static final int MAX_THREADS = 5;
	public static final int THREAD_BATCH_SIZE = 20;
	private static final long REFRESH_PERIOD_MILLIS = 60000L;

    public static final String OPTION_LOCALE = "locale";
    public static final String OPTION_DEFAULT_PLACEHOLDERS = "defaultPlaceHolders";
    public static final String OPTION_ACCOUNTS_SELECTOR = "accountsSelector";
    public static final String OPTION_CREATION_TYPE = "creationType";    
    public static final String OPTION_ACTIVITY_NAME = "activityName";
    public static final String OPTION_ACTIVITY_DESCRIPTION = "activityDescription";
    public static final String OPTION_ACTIVITY_DETAILED_DESCRIPTION = "activityDetailedDescription";
    public static final String OPTION_ACTIVITY_PRIORITY = "activityPriority";
    public static final String OPTION_ACTIVITY_SCHEDULED_START = "activityScheduledStart";
    public static final String OPTION_ACTIVITY_SCHEDULED_END = "activityScheduledEnd";
    public static final String OPTION_ACTIVITY_DUE_BY = "activityDueBy";
    public static final String OPTION_EMAIL_SENDER = "emailSender";
    public static final String OPTION_EMAIL_MESSAGE_SUBJECT = "emailMessageSubject";
    public static final String OPTION_EMAIL_MESSAGE_BODY = "emailMessageBody";
    public static final String OPTION_EMAIL_ADDRESS_USAGE = "emailAddressUsage";
    public static final String OPTION_TEST_ACCOUNT = "testAccount";
    public static final String OPTION_TEST_EMAIL = "testEMail";
    public static final String OPTION_EXCLUDE_NO_BULK_EMAIL = "excludeNoBulkEMail";

	private Date scriptLastModifiedAt = null;
	private Long lastRefreshAt = null;
	private Class<?> script = null;

}

//--- End of File -----------------------------------------------------------

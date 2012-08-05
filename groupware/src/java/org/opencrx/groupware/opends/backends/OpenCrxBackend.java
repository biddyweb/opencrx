/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Portions Copyright 2006-2007 Sun Microsystems, Inc.
 */
package org.opencrx.groupware.opends.backends;

import static org.opends.messages.BackendMessages.ERR_BACKEND_CANNOT_REGISTER_BASEDN;
import static org.opends.messages.BackendMessages.ERR_MEMORYBACKEND_REQUIRE_EXACTLY_ONE_BASE;
import static org.opends.server.loggers.debug.DebugLogger.debugEnabled;
import static org.opends.server.loggers.debug.DebugLogger.getTracer;
import static org.opends.server.util.ServerConstants.OID_SUBTREE_DELETE_CONTROL;
import static org.opends.server.util.StaticUtils.getExceptionMessage;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.jmi1.EmailAddress;
import org.opencrx.kernel.account1.jmi1.PhoneNumber;
import org.opencrx.kernel.account1.jmi1.PostalAddress;
import org.opencrx.kernel.account1.jmi1.WebAddress;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opends.messages.CoreMessages;
import org.opends.messages.Message;
import org.opends.server.admin.Configuration;
import org.opends.server.admin.std.server.MemoryBackendCfg;
import org.opends.server.api.Backend;
import org.opends.server.config.ConfigException;
import org.opends.server.core.AddOperation;
import org.opends.server.core.DeleteOperation;
import org.opends.server.core.DirectoryServer;
import org.opends.server.core.ModifyDNOperation;
import org.opends.server.core.ModifyOperation;
import org.opends.server.core.SearchOperation;
import org.opends.server.loggers.debug.DebugTracer;
import org.opends.server.types.AttributeType;
import org.opends.server.types.BackupConfig;
import org.opends.server.types.BackupDirectory;
import org.opends.server.types.ConditionResult;
import org.opends.server.types.Control;
import org.opends.server.types.DN;
import org.opends.server.types.DebugLogLevel;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.Entry;
import org.opends.server.types.IndexType;
import org.opends.server.types.InitializationException;
import org.opends.server.types.LDIFExportConfig;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.types.LDIFImportResult;
import org.opends.server.types.RestoreConfig;
import org.opends.server.types.ResultCode;
import org.opends.server.types.SearchFilter;
import org.opends.server.types.SearchScope;
import org.opends.server.util.LDIFReader;
import org.opends.server.util.Validator;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.Authority;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.portal.servlet.Codes;
import org.openmdx.security.authentication1.jmi1.Password;

public class OpenCrxBackend extends Backend {

    //-----------------------------------------------------------------------
    /**
     * Creates a new backend with the provided information.  All backend
     * implementations must implement a default constructor that use
     * <CODE>super()</CODE> to invoke this constructor.
     */
    public OpenCrxBackend(
    ) {
        super();
        // Perform all initialization in initializeBackend.
    }

    //-----------------------------------------------------------------------
    /**
     * Set the base DNs for this backend.  This is used by the unit tests
     * to set the base DNs without having to provide a configuration
     * object when initializing the backend.
     * @param baseDNs The set of base DNs to be served by this memory backend.
     */
    public void setBaseDNs(
        DN[] baseDNs
    ) {
        this.baseDNs = baseDNs;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public void configureBackend(
        Configuration config
    ) throws ConfigException {
        if (config != null) {
            Validator.ensureTrue(config instanceof MemoryBackendCfg);
            MemoryBackendCfg cfg = (MemoryBackendCfg)config;
            DN[] baseDNs = new DN[cfg.getBaseDN().size()];
            cfg.getBaseDN().toArray(baseDNs);
            setBaseDNs(baseDNs);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void initializeBackend(
    ) throws ConfigException, InitializationException {
        // We won't support anything other than exactly one base DN in this
        // implementation.  If we were to add such support in the future, we would
        // likely want to separate the data for each base DN into a separate entry
        // map.
        if ((baseDNs == null) || (baseDNs.length != 1)) {
            Message message = ERR_MEMORYBACKEND_REQUIRE_EXACTLY_ONE_BASE.get();
            throw new ConfigException(message);
        }
        baseDNSet = new HashSet<DN>();
        for (DN dn : baseDNs) {
            baseDNSet.add(dn);
        }    
        childDNs = new HashMap<DN,HashSet<DN>>();
        supportedControls = new HashSet<String>();
        supportedControls.add(OID_SUBTREE_DELETE_CONTROL);    
        supportedFeatures = new HashSet<String>();    
        for (DN dn : baseDNs) {
            try {
                DirectoryServer.registerBaseDN(dn, this, false);
            }
            catch (Exception e) {
                if (debugEnabled()) {
                    TRACER.debugCaught(DebugLogLevel.ERROR, e);
                }    
                Message message = ERR_BACKEND_CANNOT_REGISTER_BASEDN.get(
                    dn.toString(), getExceptionMessage(e));
                throw new InitializationException(message, e);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Removes any data that may have been stored in this backend.
     */
    public synchronized void clearMemoryBackend(
    ) {
        childDNs.clear();
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void finalizeBackend(
    ) {
        for (DN dn : baseDNs) {
          try {
              DirectoryServer.deregisterBaseDN(dn);
          }
          catch (Exception e) {
              if (debugEnabled()) {
                  TRACER.debugCaught(DebugLogLevel.ERROR, e);
              }
          }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public DN[] getBaseDNs(
    ) {
        return baseDNs;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized long getEntryCount(
    ) {
        return -1;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean isLocal(
    ) {
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean isIndexed(
        AttributeType attributeType, 
        IndexType indexType
    ) {
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ConditionResult hasSubordinates(
        DN entryDN
    ) throws DirectoryException {
        long ret = numSubordinates(entryDN);
        if(ret < 0) {
            return ConditionResult.UNDEFINED;
        }
        else if(ret == 0) {
            return ConditionResult.FALSE;
        }
        else {
            return ConditionResult.TRUE;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized long numSubordinates(
        DN entryDN
    ) throws DirectoryException {
        return -1;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized Entry getEntry(
        DN entryDN
    ) {
        Entry e = null;
        if("dc=users".equalsIgnoreCase(entryDN.getRDN(1).toString())) {
            // Get login realm
            String rdn = entryDN.getRDN(2).toString();
            String providerName = rdn.substring(rdn.indexOf("=") + 1);
            rdn = entryDN.getRDN(0).toString();
            String principalName = rdn.substring(rdn.indexOf("=") + 1);
            PersistenceManager pm = persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
            org.opencrx.security.realm1.jmi1.Principal principal = 
                (org.opencrx.security.realm1.jmi1.Principal)pm.getObjectById(
                    "xri:@openmdx:org.openmdx.security.realm1/provider/" + providerName + "/segment/Root/realm/Default/principal/" + principalName
                );
            if(principal != null) {
                org.openmdx.security.realm1.jmi1.Credential credential = principal.getCredential();
                String password = credential instanceof Password
                    ? (String)credential.refGetValue("password")
                    : null;
                String entry =
                    "dn: " + entryDN.toNormalizedString() + "\n" +
                    "objectClass: top\n" +
                    "objectClass: person\n" +
                    "cn: " + principal.getName() + "\n" +
                    (password == null ? "" : "userPassword: {MD5}" + password + "\n"); 
                try {
                    LDIFImportConfig config = new LDIFImportConfig(new StringReader(entry));
                    LDIFReader reader = new LDIFReader(config);
                    e = reader.readEntry();
                }
                catch(Exception e0) {
                    new ServiceException(e0).log();
                }
            }
        }
        return e;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized boolean entryExists(
        DN entryDN
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void addEntry(
        Entry entry, 
        AddOperation addOperation
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ADD_BACKEND_READONLY.get(String.valueOf(entry));
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void deleteEntry(
        DN entryDN,
        DeleteOperation deleteOperation
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_DELETE_BACKEND_READONLY.get(String.valueOf(entryDN));
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }
    
    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void replaceEntry(
        Entry entry,
        ModifyOperation modifyOperation
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_MODIFY_BACKEND_READONLY.get("Unsupported Operation");
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void renameEntry(
        DN currentDN, Entry entry,
        ModifyDNOperation modifyDNOperation
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_MODIFY_BACKEND_READONLY.get("Unsupported Operation");
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    protected String getRequestingPrincipal(
        DN authorizationDN
    ) {
        return authorizationDN.getRDN(0).getAttributeValue(0).getStringValue();
    }

    //-----------------------------------------------------------------------
    protected Codes getCodes(
        String providerName
    ) {
        if(codes == null) {
            PersistenceManager pm = persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
            try {
                codes = new Codes(
                    (RefObject_1_0)pm.getObjectById("xri:@openmdx:org.opencrx.kernel.code1/provider/" + providerName + "/segment/Root")
                );
            }
            catch(Throwable t) {
                AppLog.warning(t.getMessage(), t.getCause(), 1);
            }
        }
        return codes;
    }

    //-----------------------------------------------------------------------
    protected Map getPostalCountries(
        String providerName
    ) {
        if(postalCountries == null) {
            Codes codes = this.getCodes(providerName);
            postalCountries = codes.getLongText("org:opencrx:kernel:address1:PostalAddressable:postalCountry", (short)0, true, true);
        }
        return postalCountries;
    }
    
    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void search(
        SearchOperation searchOperation
    ) throws DirectoryException {
    
        // Search Accounts
        if("dc=accounts".equalsIgnoreCase(searchOperation.getBaseDN().getRDN(0).toString())) {
            // Get the base DN, scope, and filter for the search.
            DN baseDN = searchOperation.getBaseDN();
            SearchScope scope = searchOperation.getScope();
            SearchFilter filter = searchOperation.getFilter();
            // Search requires authorization
            if(searchOperation.getAuthorizationDN() == null) {
                throw new DirectoryException(
                    ResultCode.AUTHORIZATION_DENIED,
                    CoreMessages.INFO_RESULT_AUTHORIZATION_DENIED.get()
                );                
            }            
            PersistenceManager pm = persistenceManagerFactory.getPersistenceManager(
                this.getRequestingPrincipal(searchOperation.getAuthorizationDN()),
                UUIDs.getGenerator().next().toString()
            );
            // Get account segment
            String rdn = baseDN.getRDN(1).toString();
            String segmentName = rdn.substring(rdn.indexOf("=") + 1);
            rdn = baseDN.getRDN(2).toString();
            String providerName = rdn.substring(rdn.indexOf("=") + 1);
            org.opencrx.kernel.account1.jmi1.Account1Package accountPackage = 
                (org.opencrx.kernel.account1.jmi1.Account1Package)((Authority)pm.getObjectById(
                    Authority.class,
                    org.opencrx.kernel.account1.jmi1.Account1Package.AUTHORITY_XRI
                )).refImmediatePackage();            
            org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
                (org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
                    "xri:@openmdx:org.opencrx.kernel.account1/provider/" + providerName + "/segment/" + segmentName
                );
            AccountQuery accountQuery = accountPackage.createAccountQuery();
            accountQuery.orderByFullName();
            String stringifiedFilter = filter.toString();
            if(stringifiedFilter.indexOf("(cn=") > 0) {
                int posCn = stringifiedFilter.indexOf("(cn=");
                String pattern = stringifiedFilter.substring(
                    posCn + 4, 
                    stringifiedFilter.indexOf(")", posCn)
                ); 
                if(!pattern.startsWith("(?i)")) {
                    pattern = "(?i)" + pattern;
                }
                pattern = pattern.replace("*", ".*");
                accountQuery.thereExistsFullName().like(pattern);
            }            
            // If it's a base-level search, then just get that entry and return it if it
            // matches the filter.
            if (scope == SearchScope.BASE_OBJECT) {
                String entry =
                    "dn: " + baseDN.toNormalizedString() + "\n" +
                    "objectclass: top\n" +
                    "objectclass: dcObject\n" +
                    "objectclass: organization\n" +
                    "o: OPENCRX\n";
                try {
                    LDIFImportConfig config = new LDIFImportConfig(new StringReader(entry));
                    LDIFReader reader = new LDIFReader(config);
                    Entry baseEntry = reader.readEntry();
                    if (filter.matchesEntry(baseEntry)) {
                        searchOperation.returnEntry(baseEntry, new LinkedList<Control>());
                    }
                }
                catch(Exception e) {
                    new ServiceException(e).log();
                }
            }
            else {
                int n = 0;
                for(org.opencrx.kernel.account1.jmi1.Account account: (List<org.opencrx.kernel.account1.jmi1.Account>)accountSegment.getAccount(accountQuery)) {
                    String businessMail = null;
                    String homeMail = null;
                    String businessPhone = null;
                    String homePhone = null;
                    String businessFax = null;
                    String homeFax = null;
                    PostalAddress businessPostal = null;
                    String businessWeb = null;
                    PostalAddress homePostal = null;
                    String homeWeb = null;
                    String mobile = null;
                    String cn = null;
                    String givenName = null;
                    String sn = null;
                    String title = null;
                    String organization = null;
                    String department = null;
                    if(account instanceof org.opencrx.kernel.account1.jmi1.Contact) {
                        org.opencrx.kernel.account1.jmi1.Contact contact = (org.opencrx.kernel.account1.jmi1.Contact)account;
                        cn = contact.getLastName() + (contact.getFirstName() == null ? "" : " " + contact.getFirstName());
                        givenName = contact.getFirstName();
                        sn = contact.getLastName();
                        title = contact.getJobTitle();
                        organization = contact.getOrganization();
                        department = contact.getDepartment();
                    }
                    else {
                        cn = account.getFullName();
                    }
                    Map postalCountries = this.getPostalCountries(providerName);
                    org.opencrx.kernel.account1.jmi1.AccountAddress[] addresses = Accounts.getMainAddresses(account);
                    homeWeb = addresses[Accounts.WEB_HOME] == null ? null : ((WebAddress)addresses[Accounts.WEB_HOME]).getWebUrl();
                    businessWeb = addresses[Accounts.WEB_BUSINESS] == null ? null : ((WebAddress)addresses[Accounts.WEB_BUSINESS]).getWebUrl();
                    homePhone = addresses[Accounts.PHONE_HOME] == null ? null : ((PhoneNumber)addresses[Accounts.PHONE_HOME]).getPhoneNumberFull();
                    businessPhone = addresses[Accounts.PHONE_BUSINESS] == null ? null : ((PhoneNumber)addresses[Accounts.PHONE_BUSINESS]).getPhoneNumberFull();
                    homeFax = addresses[Accounts.FAX_HOME] == null ? null : ((PhoneNumber)addresses[Accounts.FAX_HOME]).getPhoneNumberFull();
                    businessFax = addresses[Accounts.FAX_BUSINESS] == null ? null : ((PhoneNumber)addresses[Accounts.FAX_BUSINESS]).getPhoneNumberFull();
                    homePostal = (PostalAddress)addresses[Accounts.POSTAL_HOME];
                    businessPostal = (PostalAddress)addresses[Accounts.POSTAL_BUSINESS];
                    homeMail = addresses[Accounts.MAIL_HOME] == null ? null : ((EmailAddress)addresses[Accounts.MAIL_HOME]).getEmailAddress();
                    businessMail = addresses[Accounts.MAIL_BUSINESS] == null ? null : ((EmailAddress)addresses[Accounts.MAIL_BUSINESS]).getEmailAddress();
                    mobile = addresses[Accounts.MOBILE] == null ? null : ((PhoneNumber)addresses[Accounts.MOBILE]).getPhoneNumberFull();
                    if(businessMail != null) {
                        cn += " (" + businessMail + ")";
                    }
                    else if(homeMail != null) {
                        cn += " (" + homeMail + ")";
                    }
                    String businessPostalCountryName = businessPostal == null
                        ? null
                        : (String)postalCountries.get(new Short(businessPostal.getPostalCountry()));
                    businessPostalCountryName = (businessPostalCountryName != null) && (businessPostalCountryName.indexOf("[") > 0)
                        ? businessPostalCountryName.substring(0, businessPostalCountryName.indexOf("["))
                        : businessPostalCountryName;
                    String homePostalCountryName = homePostal == null
                        ? null
                        : (String)postalCountries.get(new Short(homePostal.getPostalCountry()));
                    homePostalCountryName = (homePostalCountryName != null) && (homePostalCountryName.indexOf("[") > 0)
                        ? homePostalCountryName.substring(0, homePostalCountryName.indexOf("["))
                        : homePostalCountryName;                                
                    String entry = 
                        "dn: cn=" + cn + "," + baseDN.toNormalizedString() + "\n" +
                        "objectclass: top\n" +
                        "objectclass: person\n" +
                        "objectclass: organizationalPerson\n" +
                        "objectclass: inetOrgPerson\n" +
                        "objectclass: mozillaAddressBookEntry\n" +
                        (givenName == null ? "" : "givenName: " + givenName + "\n") +
                        (sn == null ? "" : "sn: " + sn + "\n") +
                        "cn: " + cn + "\n" +
                        (businessMail == null ? "" : "mail: " + businessMail + "\n") +
                        (businessPhone == null ? "" : "telephoneNumber: " + businessPhone + "\n") +
                        (businessFax == null ? "" : "facsimileTelephoneNumber: " + businessFax + "\n") +
                        (mobile == null ? "" : "mobile: " + mobile + "\n") +
                        (businessPostal == null || businessPostal.getPostalStreet().size() < 1 ? "" : "street: " + businessPostal.getPostalStreet().get(0) + "\n") +
                        (businessPostal == null || businessPostal.getPostalStreet().size() < 1 ? "" : "streetAddress: " + businessPostal.getPostalStreet().get(0) + "\n") +
                        (businessPostal == null || businessPostal.getPostalStreet().size() < 2 ? "" : "mozillaWorkStreet2: " + businessPostal.getPostalStreet().get(1) + "\n") +
                        (businessPostal == null || businessPostal.getPostalCity() == null ? "" : "l: " + businessPostal.getPostalCity() + "\n") +
                        (businessPostal == null || businessPostal.getPostalCode() == null ? "" : "postalCode: " + businessPostal.getPostalCode() + "\n") +
                        (businessPostal == null || businessPostal.getPostalState() == null ? "" : "st: " + businessPostal.getPostalState() + "\n") +
                        (businessPostal == null || businessPostal.getPostalCountry() == 0 ? "" : "c: " + businessPostalCountryName + "\n") +
                        (businessPostal == null || businessPostal.getPostalStreet().size() < 1 ? "" : "postalAddress: " + businessPostal.getPostalStreet().get(0) + "\n") +
                        (title == null ? "" : "title: " + title + "\n") +
                        (title == null ? "" : "personalTitle: " + title + "\n") +
                        (organization == null ? "" : "o: " + organization + "\n") +
                        (organization == null ? "" : "company: " + organization + "\n") +
                        (department == null ? "" : "ou: " + department + "\n") +
                        (department == null ? "" : "department: " + department + "\n") +
                        (businessWeb == null ? "" : "mozillaWorkUrl: " + businessWeb + "\n") +
                        (homePostal == null || homePostal.getPostalStreet().size() < 1 ? "" : "mozillaHomeStreet: " + homePostal.getPostalStreet().get(0) + "\n") +
                        (homePostal == null || homePostal.getPostalStreet().size() < 2 ? "" : "mozillaHomeStreet2: " + homePostal.getPostalStreet().get(1) + "\n") +
                        (homePostal == null || homePostal.getPostalCity() == null ? "" : "mozillaHomeLocalityName: " + homePostal.getPostalCity() + "\n") +
                        (homePostal == null || homePostal.getPostalCode() == null ? "" : "mozillaHomePostalCode: " + homePostal.getPostalCode() + "\n") +
                        (homePostal == null || homePostal.getPostalState() == null ? "" : "mozillaHomeState: " + homePostal.getPostalState() + "\n") +
                        (homePostal == null || homePostal.getPostalCountry() == 0 ? "" : "mozillaHomeCountryName: " + homePostalCountryName + "\n") +
                        (homePhone == null ? "" : "homePhone: " + homePhone + "\n") +
                        (homeWeb == null ? "" : "mozillaHomeUrl: " + homeWeb + "\n");                        
                    try {
                        LDIFImportConfig config = new LDIFImportConfig(new StringReader(entry));
                        LDIFReader reader = new LDIFReader(config);
                        Entry e = reader.readEntry();
                        if (e.matchesBaseAndScope(baseDN, scope) && filter.matchesEntry(e)) {
                            searchOperation.returnEntry(e, new LinkedList<Control>());
                            n++;
                            if(n > 100) break;
                        }
                    }
                    catch(Exception e) {
                        new ServiceException(e).log();
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public HashSet<String> getSupportedControls(
    ) {
        return this.supportedControls;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public HashSet<String> getSupportedFeatures(
    ) {
        return this.supportedFeatures;
   }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean supportsLDIFExport(
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized void exportLDIF(
        LDIFExportConfig exportConfig
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ABANDON_OP_NO_SUCH_OPERATION.get(0);
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean supportsLDIFImport(
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public synchronized LDIFImportResult importLDIF(
        LDIFImportConfig importConfig
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ABANDON_OP_NO_SUCH_OPERATION.get(0);
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean supportsBackup(
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean supportsBackup(
        BackupConfig backupConfig,
        StringBuilder unsupportedReason
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public void createBackup(
        BackupConfig backupConfig
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ABANDON_OP_NO_SUCH_OPERATION.get(0);
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public void removeBackup(
        BackupDirectory backupDirectory,
        String backupID
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ABANDON_OP_NO_SUCH_OPERATION.get(0);
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }
    
    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public boolean supportsRestore(
    ) {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override()
    public void restoreBackup(
        RestoreConfig restoreConfig
    ) throws DirectoryException {
        Message message = CoreMessages.ERR_ABANDON_OP_NO_SUCH_OPERATION.get(0);
        throw new DirectoryException(ResultCode.UNWILLING_TO_PERFORM, message);
    }
  
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    
    private static final DebugTracer TRACER = getTracer();

    // The base DNs for this backend.
    private DN[] baseDNs;

    // The mapping between parent DNs and their immediate children.
    private HashMap<DN,HashSet<DN>> childDNs;

    // The base DNs for this backend, in a hash set.
    private HashSet<DN> baseDNSet;

    // The set of supported controls for this backend.
    private HashSet<String> supportedControls;

    // The set of supported features for this backend.
    private HashSet<String> supportedFeatures;

    public static PersistenceManagerFactory persistenceManagerFactory = null;
    protected static Codes codes = null;
    protected static Map postalCountries = null; 
    
}

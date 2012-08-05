package org.opencrx.application.imap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.PersistenceManager;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.opencrx.kernel.account1.jmi1.EMailAddress;
import org.opencrx.kernel.activity1.cci2.EMailQuery;
import org.opencrx.kernel.activity1.jmi1.Activity1Package;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityGroup;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.Addresses;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.base.cci2.AuditEntryQuery;
import org.opencrx.kernel.base.jmi1.AuditEntry;
import org.opencrx.kernel.base.jmi1.ObjectRemovalAuditEntry;
import org.opencrx.kernel.generic.jmi1.Media;
import org.opencrx.kernel.utils.ActivitiesHelper;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.format.DateFormat;

public class IMAPFolderImpl extends Folder implements UIDFolder {

    //-----------------------------------------------------------------------
    public IMAPFolderImpl(
        String name,
        String username,
        ActivitiesHelper activitiesHelper
    ) {
        super(null);
        this.name = name;
        this.folderDir = new File(
        	IMAPFolderImpl.getMailDir(username),
            name.replace(":", "~")
        );
        if(System.getProperty(EMAIL_ADDRESS_LOOKUP_CASE_INSENSITIVE_PROPERTY_NAME) != null) {
        	this.isEMailAddressLookupCaseInsensitive = Boolean.valueOf(
        		System.getProperty(EMAIL_ADDRESS_LOOKUP_CASE_INSENSITIVE_PROPERTY_NAME)
        	);
        }
        if(System.getProperty(EMAIL_ADDRESS_LOOKUP_IGNORE_DISABLED_PROPERTY_NAME) != null) {
        	this.isEMailAddressLookupIgnoreDisabled = Boolean.valueOf(
        		System.getProperty(EMAIL_ADDRESS_LOOKUP_IGNORE_DISABLED_PROPERTY_NAME)
        	);
        }
        this.folderDir.mkdirs();
        this.activitiesHelper = activitiesHelper;        
    }

    //-------------------------------------------------------------------------
    public static File getMailDir(
        String username
    ) {
        return new File(
            (System.getProperty(MAILDIR_PROPERTY_NAME) == null
                ? "/temp"
                : System.getProperty(MAILDIR_PROPERTY_NAME)
            ) + "/" +
            username.replace(":", "~")
        );
    }
    
    //-------------------------------------------------------------------------
    protected String[] getInternetAddresses(
        Address[] addresses
    ) throws AddressException {
        String internetAddresses[] = null;
        if (addresses != null && addresses.length > 0) {
            internetAddresses = new String[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                if (addresses[0] instanceof InternetAddress) {
                    internetAddresses[i] = ((InternetAddress)addresses[i]).getAddress();
                } 
                else {
                    InternetAddress temp = new InternetAddress(addresses[i].toString());
                    internetAddresses[i] = temp.getAddress();
                }
            }
        } 
        else {
            internetAddresses = new String[]{UNSPECIFIED_ADDRESS};
        }
        return internetAddresses;
    }
    
    //-----------------------------------------------------------------------
    protected String[] parseContentType(
        String contentType
    ) {
        String[] result = new String[2];
        Pattern pattern = Pattern.compile("([0-9a-zA-Z/\\+\\-\\.]+)(?:;(?:[ \\r\\n\\t]*)name(?:[^\\=]*)\\=\"(.*)\")?");
        Matcher matcher = pattern.matcher(contentType);
        if(matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        }
        else {
            result[0] = contentType;
            result[1] = null;                            
        }
        return result;
    }
    
    //-----------------------------------------------------------------------
    protected void addMedia(
        MimeMessage mimeMessage,
        EMail emailActivity
    ) throws IOException, MessagingException, ServiceException {
        if(mimeMessage.getContent() instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart)mimeMessage.getContent();
            for(int i = 1; i < multipart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart)multipart.getBodyPart(i);
                String[] contentType = this.parseContentType(part.getContentType());
                if(contentType[1] == null) contentType[1] = part.getContentID();
                boolean found = false;
                Collection<Media> medias = emailActivity.getMedia();
                for(Media attachment: medias) {
                    if((contentType[1] != null) && contentType[1].equals(attachment.getContentName())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    Activities.getInstance().addMedia(
                        this.activitiesHelper.getPersistenceManager(),
                        emailActivity,
                        contentType[0],
                        contentType[1],
                        part.getInputStream()
                    );
                }
            }
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void appendMessages(
        Message[] newMessages
    ) throws MessagingException {
        if((this.activitiesHelper != null) && (this.activitiesHelper.getActivityGroup() != null)) {
            // Find a creator which creates Email activities
            ActivityCreator emailCreator = null;
            Collection<ActivityCreator> activityCreators = this.activitiesHelper.getActivityGroup().getActivityCreator();
            for(ActivityCreator creator: activityCreators) {
                if(
                    (creator.getActivityType() != null) && 
                    (creator.getActivityType().getActivityClass() == Activities.ACTIVITY_CLASS_EMAIL)
                ) {
                    emailCreator = creator;
                    break;
                }
            }
            if(emailCreator != null) {
                PersistenceManager pm = this.activitiesHelper.getPersistenceManager();
                String providerName = emailCreator.refGetPath().get(2);
                String segmentName = emailCreator.refGetPath().get(4);
                MailDateFormat mailDateFormat = new MailDateFormat();                
                for(Message message: newMessages) {
                    MimeMessage mimeMessage = (MimeMessage)message;
                    try {
                        List<org.opencrx.kernel.activity1.jmi1.Activity> activities = Activities.getInstance().lookupEmailActivity(
                            pm,
                            providerName,
                            segmentName,
                            mimeMessage.getMessageID()
                        );
                        EMail emailActivity = null;
                        if(activities.isEmpty()) {
                            AppLog.trace("Create a new EMailActivity");
                            pm.currentTransaction().begin();                            
                            Activity1Package activityPkg = Utils.getActivityPackage(pm);
                            NewActivityParams newActParam = activityPkg.createNewActivityParams(
                                null, // description
                                null, // detailedDescription
                                null, // dueBy
                                ICalendar.ICAL_TYPE_NA, // icalType
                                mimeMessage.getSubject(),
                                Activities.getInstance().getMessagePriority(message),
                                null, // reportingContract
                                null, // scheduledEnd
                                null  // scheduledStart
                            );
                            NewActivityResult newActivityResult = emailCreator.newActivity(
                                newActParam
                            );
                            pm.currentTransaction().commit();                  
                            emailActivity = (EMail)pm.getObjectById(newActivityResult.getActivity().refMofId());
                            // Update activity step 1
                            pm.currentTransaction().begin();
                            String subject = mimeMessage.getSubject();                
                            emailActivity.setMessageSubject(
                                subject == null ? "" : subject
                            );
                            emailActivity.getExternalLink().clear();
                            if(mimeMessage.getMessageID() != null) {
                                emailActivity.getExternalLink().add(
                                    mimeMessage.getMessageID()
                                );
                            }
                            pm.currentTransaction().commit();
                            // Update activity step 2
                            // Append original email as media attachment
                            String mimeMessageName = emailActivity.getActivityNumber().trim() + ".eml";
                            ByteArrayOutputStream mimeMessageBytes = new ByteArrayOutputStream();
                            ZipOutputStream mimeMessageZipped = new ZipOutputStream(mimeMessageBytes);
                            mimeMessageZipped.putNextEntry(
                                new ZipEntry(mimeMessageName)
                            );
                            mimeMessage.writeTo(mimeMessageZipped);
                            mimeMessageZipped.close();
                            Activities.getInstance().addMedia(
                                pm, 
                                emailActivity, 
                                "application/zip", 
                                mimeMessageName + ".zip", 
                                new ByteArrayInputStream(mimeMessageBytes.toByteArray())
                            );
                            // Update activity step 3
                            pm.currentTransaction().begin();
                            String body = Activities.getInstance().getMessageBody(mimeMessage);
                            emailActivity.setMessageBody(
                                body == null ? "" : body
                            );
                            String[] date = mimeMessage.getHeader("Date");
                            if(date.length > 0) {
                                emailActivity.setSendDate(
                                    mailDateFormat.parse(date[0])
                                );
                            }
                            pm.currentTransaction().commit();
                            // Add originator and recipients to a note
                            Activities.getInstance().addNote(
                                pm,
                                emailActivity,
                                "Recipients",
                                Activities.getInstance().getRecipientsAsNoteText(
                                    pm,
                                    providerName,
                                    segmentName,
                                    this.getInternetAddresses(mimeMessage.getFrom()),
                                    this.getInternetAddresses(mimeMessage.getRecipients(Message.RecipientType.TO)),
                                    this.getInternetAddresses(mimeMessage.getRecipients(Message.RecipientType.CC)),
                                    this.getInternetAddresses(mimeMessage.getRecipients(Message.RecipientType.BCC)),
                                    this.isEMailAddressLookupCaseInsensitive,
                                    this.isEMailAddressLookupIgnoreDisabled
                                )
                            );                  
                            // Add headers as Note
                            Activities.getInstance().addNote(
                                pm,
                                emailActivity,
                                "Message-Header",
                                MimeMessageImpl.getHeadersAsRFC822(
                                     mimeMessage, 
                                     null
                                )
                            );
                            // Add attachments
                            this.addMedia(
                                mimeMessage, 
                                emailActivity
                            );
                        }
                        else {
                            emailActivity = (EMail)activities.iterator().next();
                        }
                        // Add FROM as sender
                        List<org.opencrx.kernel.account1.jmi1.EMailAddress> addresses = 
                            Accounts.getInstance().lookupEmailAddress(
                                pm,
                                providerName,
                                segmentName,
                                this.getInternetAddresses(mimeMessage.getFrom())[0],
                                this.isEMailAddressLookupCaseInsensitive,
                                this.isEMailAddressLookupIgnoreDisabled                                
                            );
                        if(addresses.isEmpty()) {
                            addresses = Accounts.getInstance().lookupEmailAddress(
                                pm,
                                providerName,
                                segmentName,
                                Addresses.UNASSIGNED_ADDRESS,
                                this.isEMailAddressLookupCaseInsensitive,
                                this.isEMailAddressLookupIgnoreDisabled                                
                            );                            
                        }
                        EMailAddress from = null;
                        if(!addresses.isEmpty()) {
                            from = addresses.iterator().next();
                            pm.currentTransaction().begin();
                            emailActivity.setSender(from);
                            pm.currentTransaction().commit();
                        } 
                        Activities.getInstance().addRecipientToEmailActivity(
                            pm,
                            providerName,
                            segmentName,
                            emailActivity,
                            this.getInternetAddresses(mimeMessage.getRecipients(Message.RecipientType.TO)),
                            Message.RecipientType.TO,
                            this.isEMailAddressLookupCaseInsensitive,
                            this.isEMailAddressLookupIgnoreDisabled                            
                        );
                        Activities.getInstance().addRecipientToEmailActivity(
                            pm,
                            providerName,
                            segmentName,
                            emailActivity,
                            this.getInternetAddresses(mimeMessage.getRecipients(Message.RecipientType.CC)),
                            Message.RecipientType.CC,
                            this.isEMailAddressLookupCaseInsensitive,
                            this.isEMailAddressLookupIgnoreDisabled                            
                        );      
                    }
                    catch (Exception e) {
                        try {
                            pm.currentTransaction().rollback();
                        } 
                        catch(Exception e0) {}
                        AppLog.warning("Can not create email activity", e.getMessage());
                        new ServiceException(e).log();                        
                    }                                
                }
            }
            // Force synch
            this.synchronizeNextAt = 0;
        }
    }

    //-----------------------------------------------------------------------
    synchronized void synchronizeMailDir(
    ) {                
        // Synchronize
        if(this.synchronizeNextAt < System.currentTimeMillis()) {            
            // Get index
            File indexFile = new File(this.folderDir, INDEX_FILE_NAME);
            Date lastSynchronizedAt = null;
            if(indexFile.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            new FileInputStream(indexFile)
                        )
                    );
                    lastSynchronizedAt = DateFormat.getInstance().parse(reader.readLine());
                    this.messageUIDs.clear();
                    this.messageXRIs.clear();
                    while(reader.ready()) {
                        String l = reader.readLine();
                        String[] ids = l.split(" ");
                        // A line has format activity.number activity.xri
                        // In case of a syntax error reset the index
                        if(ids.length != 2) {
                            lastSynchronizedAt = null;
                            break;
                        }
                        Long uid = Long.parseLong(ids[0]);
                        this.messageUIDs.add(uid);
                        this.messageXRIs.put(
                            uid,
                            ids[1]
                        ); 
                    }
                    reader.close();
                }
                catch(Exception e) {
                    AppLog.error("Can not read index file " + indexFile, e.getMessage());
                }
            }
            // Get activities which are newer than synchronizedAt
            if(this.activitiesHelper != null) {
                PersistenceManager pm = this.activitiesHelper.getPersistenceManager();
                pm.evictAll();
                EMailQuery query = Utils.getActivityPackage(pm).createEMailQuery();
                query.activityNumber().isNonNull();            
                query.orderByActivityNumber().ascending();
                if(lastSynchronizedAt == null) {
                	this.messageUIDs.clear();
                	this.messageXRIs.clear();
                }
                else {
                    query.modifiedAt().greaterThan(lastSynchronizedAt);
                }
                Collection<org.opencrx.kernel.activity1.jmi1.Activity> activities = 
                    this.activitiesHelper.getFilteredActivities(query);
                for(org.opencrx.kernel.activity1.jmi1.Activity activity: activities) {
                    Long uid = Long.valueOf(activity.getActivityNumber().trim());                    
                    try {
                        MimeMessageImpl mimeMessage = new MimeMessageImpl();
                        InputStream originalMessageStream = Activities.getInstance().mapToMessage(
                            (org.opencrx.kernel.activity1.jmi1.EMail)activity, 
                            mimeMessage
                        );
                        File mimeMessageFile = new File(
                            this.folderDir, 
                            Long.toString(uid) + ".eml"
                        );
                        OutputStream out = new FileOutputStream(mimeMessageFile);
                        if(originalMessageStream != null) {
                            int b;
                            while((b = originalMessageStream.read()) != -1) {
                                out.write(b);
                            }
                            originalMessageStream.close();
                        }
                        else {
                            mimeMessage.setUid(uid);
                            mimeMessage.writeTo(out);
                        }
                        out.close();             
                        this.messageUIDs.add(uid);
                        this.messageXRIs.put(
                            uid,
                            activity.refMofId()
                        );                     
                    }
                    catch(Exception e) {
                        AppLog.warning("Unable to map activity to mime message", uid);
                        new ServiceException(e).log();
                    }
                }
                pm.evictAll();
            }
            // Update index for removed activities
            if(lastSynchronizedAt != null) {
                org.opencrx.kernel.base.jmi1.BasePackage basePackage = Utils.getBasePackage(
                    this.activitiesHelper.getPersistenceManager()
                );
                AuditEntryQuery query = basePackage.createObjectRemovalAuditEntryQuery();
                query.createdAt().greaterThan(lastSynchronizedAt);
                query.auditee().like(
                    this.activitiesHelper.getActivitySegment().refGetPath().getDescendant("activity", "**").toResourcePattern()
                );
                try {
                    List<AuditEntry> auditEntries = this.activitiesHelper.getActivitySegment().getAudit(query);
                    for(AuditEntry auditEntry: auditEntries) {
                        try {
                            if(auditEntry instanceof ObjectRemovalAuditEntry) {
                                // Lookup entry which matches the removed activity's xri.
                                // Update index and remove message file
                                for(Map.Entry<Long,String> entry: this.messageXRIs.entrySet()) {
                                    if(auditEntry.getAuditee().equals(entry.getValue())) {
                                        File mimeMessageFile = new File(
                                            this.folderDir, 
                                            entry.getKey() + ".eml"
                                        );                         
                                        try {
                                            mimeMessageFile.delete();
                                        } 
                                        catch(Exception e) {}
                                        this.messageUIDs.remove(entry.getKey());
                                        this.messageXRIs.remove(entry.getKey());
                                        break;
                                    }
                                }
                            }
                        } 
                        catch(Exception e) {}
                    }
                }
                catch(Exception e) {}
            }            
            // Write index
            try {
                PrintStream out = new PrintStream(indexFile);
                out.println(DateFormat.getInstance().format(new Date()));
                for(Map.Entry<Long,String> entry: this.messageXRIs.entrySet()) {
                    out.println(Long.toString(entry.getKey()) + " " + entry.getValue());
                }
                out.close();
            }
            catch(Exception e) {
                AppLog.error("Can not write index file " + indexFile, e.getMessage());            
            }
            this.synchronizeNextAt = System.currentTimeMillis() + SYNCHRONIZE_REFRESH_RATE;
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void close(boolean arg0) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean create(int arg0) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean delete(
        boolean arg0
    ) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean exists(
    ) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public Message[] expunge(
    ) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public Folder getFolder(
        String arg0
    ) throws MessagingException {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public Message getMessage(
        int messageNumber
    ) throws MessagingException {
        if(this.activitiesHelper != null) {
            this.synchronizeMailDir();
            Long uid = messageNumber <= this.messageUIDs.size() ? 
            	this.messageUIDs.get(messageNumber - 1) : 
            	null;
            if(uid != null) {
                File mimeMessageFile = new File(this.folderDir, uid + ".eml");
                try {
                    FileInputStream in = new FileInputStream(mimeMessageFile);
                    MimeMessageImpl mimeMessage = new MimeMessageImpl(in);
                    in.close();
                    mimeMessage.setUid(uid);
                    mimeMessage.setMessageNumber(messageNumber);
                    return mimeMessage;
                }
                catch(Exception e) {
                    AppLog.error("Can not read message " + mimeMessageFile, e.getMessage());
                }
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public int getMessageCount(
    ) throws MessagingException {
        if(this.activitiesHelper != null) {
            this.synchronizeMailDir();
            return this.messageUIDs.size();
        }
        else {
            return 0;
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String getFullName(
    ) {
        return this.name;        
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName(
    ) {
        return this.name;        
    }

    //-----------------------------------------------------------------------
    @Override
    public Folder getParent(
    ) throws MessagingException {
        return null;        
    }

    //-----------------------------------------------------------------------
    @Override
    public Flags getPermanentFlags(
    ) {
        throw new UnsupportedOperationException();        
    }

    //-----------------------------------------------------------------------
    @Override
    public char getSeparator(
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    @Override
    public int getType(
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean hasNewMessages(
    ) throws MessagingException {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isOpen(
    ) {
        return true;        
    }

    //-----------------------------------------------------------------------
    @Override
    public Folder[] list(
        String arg0
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    @Override
    public void open(
        int arg0
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean renameTo(
        Folder arg0
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    // UIDFolder
    //-----------------------------------------------------------------------
    public Message getMessageByUID(
        long uid
    ) throws MessagingException {
        if(this.activitiesHelper != null) {
            this.synchronizeMailDir();
            File mimeMessageFile = new File(this.folderDir, uid + ".eml");
            try {
                FileInputStream in = new FileInputStream(mimeMessageFile);
                MimeMessageImpl mimeMessage = new MimeMessageImpl(in);
                in.close();
                mimeMessage.setUid(uid);
                int messageIndex = this.messageUIDs.indexOf(uid);
                if(messageIndex != -1) {
                    mimeMessage.setMessageNumber(messageIndex + 1);
                }
                return mimeMessage;
            }
            catch(Exception e) {
                AppLog.error("Can not read message " + mimeMessageFile, e.getMessage());
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    public Message[] getMessagesByUID(
        long[] uids
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    public Message[] getMessagesByUID(
        long start, 
        long end
    ) throws MessagingException {
        List<Message> result = new ArrayList<Message>();
        if(this.activitiesHelper != null) {
            this.synchronizeMailDir();
            for(Long uid: this.messageXRIs.keySet()) {
                if(uid >= start && uid <= end) {
                    result.add(this.getMessageByUID(uid));
                }
            }
        }
        return result.toArray(new Message[result.size()]);
    }

    //-----------------------------------------------------------------------
    public long getUID(
        Message message
    ) throws MessagingException {
        return ((MimeMessageImpl)message).getUid();
    }

    //-----------------------------------------------------------------------
    public long getUIDValidity(
    ) throws MessagingException {
        if((this.activitiesHelper != null) && (this.activitiesHelper.getActivityGroup() != null)) {
            ActivityGroup group = this.activitiesHelper.getActivityGroup();
            Date createdAt = new Date();
            if(group instanceof ActivityTracker) {
                createdAt = ((ActivityTracker)group).getModifiedAt();
            }
            else if(group instanceof ActivityMilestone) {
                createdAt = ((ActivityMilestone)group).getModifiedAt();
            }
            else if(group instanceof ActivityCategory) {
                createdAt = ((ActivityCategory)group).getModifiedAt();
            }
            return createdAt.getTime() / 1000L;
        }
        return 123987L;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static final String UNSPECIFIED_ADDRESS = "NO_ADDRESS_SPECIFIED";
    protected static final String INDEX_FILE_NAME = ".INDEX";
    protected static final long SYNCHRONIZE_REFRESH_RATE = 60000;
    public static final String MAILDIR_PROPERTY_NAME = "org.opencrx.maildir";
    public static final String EMAIL_ADDRESS_LOOKUP_CASE_INSENSITIVE_PROPERTY_NAME = "org.opencrx.eMailAddressLookupCaseInsensitive";
    public static final String EMAIL_ADDRESS_LOOKUP_IGNORE_DISABLED_PROPERTY_NAME = "org.opencrx.eMailAddressLookupIgnoreDisabled";
    
    protected final String name;
    protected final ActivitiesHelper activitiesHelper;
    protected final List<Long> messageUIDs = new ArrayList<Long>();
    protected final Map<Long,String> messageXRIs = new TreeMap<Long,String>();
    protected File folderDir;
	protected boolean isEMailAddressLookupCaseInsensitive = true;
	protected boolean isEMailAddressLookupIgnoreDisabled = false;
    protected long synchronizeNextAt = 0;

}

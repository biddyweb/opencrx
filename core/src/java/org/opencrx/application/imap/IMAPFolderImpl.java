package org.opencrx.application.imap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

import org.opencrx.application.adapter.AbstractSession;
import org.opencrx.kernel.activity1.cci2.EMailQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.EMail;
import org.opencrx.kernel.backend.Activities;
import org.opencrx.kernel.backend.MimeMessageImpl;
import org.opencrx.kernel.utils.ActivityQueryHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.log.SysLog;
import org.w3c.format.DateTimeFormat;

public class IMAPFolderImpl extends Folder implements UIDFolder {

    //-----------------------------------------------------------------------
    public IMAPFolderImpl(
        String name,
        String folderId,
        String username,
        PersistenceManagerFactory pmf
    ) {
        super(null);
        this.name = name;
        this.folderDir = new File(
        	IMAPFolderImpl.getMailDir(username),
            name.toUpperCase().replace(":", "~")
        );
        this.folderDir.mkdirs();
        this.username = username;
        this.pmf = pmf;
        this.folderId = folderId;
    }

    //-------------------------------------------------------------------------
    static class MimeFileDescr {
    
    	public MimeFileDescr(
    		int messageNumber,
    		long uid,
    		File file
    	) {
    		this.messageNumber = messageNumber;
    		this.uid = uid;
    		this.file = file;
    	}
    	
    	public int getMessageNumber(
    	) {
    		return this.messageNumber;
    	}
    	
    	public long getUID(
    	) {
    		return this.uid;
    	}
    	
    	public File getFile(
    	) {
    		return this.file;
    	}
    	
    	private final int messageNumber;
    	private final long uid;
    	private final File file;
    }
    
    //-----------------------------------------------------------------------
	public Object[] loadMetaInf(
	) {
        File metainfFile = new File(this.folderDir, METAINF_FILE_NAME);
        Object[] metainf = null;
        if(metainfFile.exists()) {
        	BufferedReader reader = null;
        	try {
                reader = new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream(metainfFile)
                    )
                );
                Date createdAt = DateTimeFormat.BASIC_UTC_FORMAT.parse(reader.readLine());
                Date lastSynchronizedAt = DateTimeFormat.BASIC_UTC_FORMAT.parse(reader.readLine());
                Long nextUID = Long.valueOf(reader.readLine());
                metainf = new Object[]{createdAt, lastSynchronizedAt, nextUID}; 
        	} 
        	catch(Exception e) {}
        	finally {
        		if(reader != null) {
        			try {
        				reader.close();
        			} catch(Exception e) {}
        		}
        	}            	
        }
        return metainf;
	}

    //-----------------------------------------------------------------------
	public void storeMetaInf(
		Object[] metainf
	) {
        File metainfFile = new File(this.folderDir, METAINF_FILE_NAME);
        try {            
            PrintStream out = new PrintStream(metainfFile);
            for(int i = 0; i < metainf.length; i++) {
            	Object value = metainf[i];
            	if(value instanceof Date) {
            		out.println(DateTimeFormat.BASIC_UTC_FORMAT.format((Date)value));
            	}
            	else {
            		out.println(value.toString());            		
            	}
            }
            out.close();
        }
        catch(Exception e) {
        	SysLog.error("Can not read index file " + metainfFile, e.getMessage());
        	new ServiceException(e).log();
        }            
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
    
    //-----------------------------------------------------------------------
    private long getUID(
    	File mimeFile
    ) {
    	int pos = mimeFile.getName().indexOf("-");
    	if(pos > 0) {
    		return Long.valueOf(mimeFile.getName().substring(0, pos));
    	} else {
    		return -1;
    	}
    }
    
    //-----------------------------------------------------------------------
    private Map<Long,File> getMimeFiles(
    ) {
    	Map<Long,File> sortedFiles = new TreeMap<Long,File>();    	
    	File[] files = this.folderDir.listFiles();
    	if(files != null) {
	    	// Order files by UID
	    	for(int i = 0; i < files.length; i++) {
	    		if(files[i].getName().endsWith(".eml")) {
	    			sortedFiles.put(
	    				getUID(files[i]), 
	    				files[i]
	    			);
	    		}
	    	}
    	}
    	return sortedFiles;
    }
    
    //-----------------------------------------------------------------------
    private MimeFileDescr getMimeFileDescr(
    	Activity activity
    ) {
    	String activityNumber = activity.getActivityNumber().trim();
    	Map<Long,File> mimeFiles = this.getMimeFiles();
    	int messageNumber = 1;
    	for(Map.Entry<Long,File> entry: mimeFiles.entrySet()) {
    		if(entry.getValue().getName().indexOf("-" + activityNumber) > 0) {
    			return new MimeFileDescr(
    				messageNumber,
    				this.getUID(entry.getValue()),
    				entry.getValue()
    			);
    		}
    	}
    	return null;
    }
    
    //-----------------------------------------------------------------------
    private MimeFileDescr getMimeFileDescr(
    	long uid
    ) {
    	Map<Long,File> mimeFiles = this.getMimeFiles();
    	int messageNumber = 1;
    	for(Map.Entry<Long,File> entry: mimeFiles.entrySet()) {
    		if(this.getUID(entry.getValue()) == uid) {
    			return new MimeFileDescr(
    				messageNumber,
    				uid,
    				entry.getValue()
    			);
    		}
    		messageNumber++;
    	}
    	return null;
    }
    
    //-----------------------------------------------------------------------
    private MimeFileDescr getMimeFileDescr(
    	int messageNumber
    ) {
    	Map<Long,File> mimeFiles = this.getMimeFiles();
    	int index = 1;
    	for(Map.Entry<Long,File> entry: mimeFiles.entrySet()) {
    		if(index == messageNumber) {
    			return new MimeFileDescr(
    				messageNumber,
    				this.getUID(entry.getValue()),
    				entry.getValue()
    			);
    		}
    		index++;
    	}
    	return null;
    }
    
    //-----------------------------------------------------------------------
    protected ActivityQueryHelper getActivitiesHelper(
    ) {
    	ActivityQueryHelper activitiesHelper = new ActivityQueryHelper(
    		AbstractSession.newPersistenceManager(this.pmf, this.username)
    	);
    	try {
    		activitiesHelper.parseQueryId(this.folderId);
    	} catch(Exception e) {}
    	return activitiesHelper;
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void appendMessages(
        Message[] newMessages
    ) throws MessagingException {
    	ActivityQueryHelper activitiesHelper = this.getActivitiesHelper();
    	try {
	        if(activitiesHelper.getActivityGroup() != null) {
	            // Find a creator which creates Email activities
	            ActivityCreator emailCreator = null;
	            Collection<ActivityCreator> activityCreators = activitiesHelper.getActivityGroup().getActivityCreator();
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
	                PersistenceManager pm = activitiesHelper.getPersistenceManager();
	                String providerName = emailCreator.refGetPath().get(2);
	                String segmentName = emailCreator.refGetPath().get(4);
	                for(Message message: newMessages) {
	                    MimeMessage mimeMessage = (MimeMessage)message;
	                    try {
	                    	Activities.getInstance().importMimeMessage(
	                    		pm,
	                    		providerName, 
	                    		segmentName, 
	                    		mimeMessage, 
	                    		emailCreator 
	                    	);
	                    }
	                    catch (Exception e) {
	                        try {
	                            pm.currentTransaction().rollback();
	                        } 
	                        catch(Exception e0) {}
	                        SysLog.warning("Can not create email activity", e.getMessage());
	                        new ServiceException(e).log();                        
	                    }                                
	                }
	            }
	        }
    	}
    	finally {
    		activitiesHelper.close();
    	}
    }

    //-----------------------------------------------------------------------
    synchronized void synchronizeMailDir(
    ) {                
    	ActivityQueryHelper activitiesHelper = this.getActivitiesHelper();
    	try {
            if(activitiesHelper.getActivityGroup() != null) {
                PersistenceManager pm = activitiesHelper.getPersistenceManager();
                // Get .METAINF
                Date lastSynchronizedAt = null;
                Date createdAt = null;
                Long nextUID = null;
                Object[] metainf = this.loadMetaInf();
                if(metainf == null) {
                	File[] files = this.folderDir.listFiles();
                	for(int i = 0; i < files.length; i++) {
                		if(files[i].isFile()) {
                			try {
                				files[i].delete();
                			} catch(Exception e) {}
                		}
                	}
                	createdAt = new Date();
                	nextUID = 1L;
                } 
                else {
                	createdAt = (Date)metainf[META_INF_CREATED_AT];
                	lastSynchronizedAt = (Date)metainf[META_INF_LAST_SYNCHRONIZED_AT];
                	nextUID = (Long)metainf[META_INF_NEXT_UID];
                }
                // Create MIME files for new activities
                EMailQuery query = (EMailQuery)pm.newQuery(EMail.class);
                if(lastSynchronizedAt != null) {
                	query.modifiedAt().greaterThanOrEqualTo(lastSynchronizedAt);
                }
                query.activityNumber().isNonNull();
                query.forAllDisabled().isFalse();
                query.orderByActivityNumber().ascending();
                Collection<Activity> activities = activitiesHelper.getFilteredActivities(query);
                for(org.opencrx.kernel.activity1.jmi1.Activity activity: activities) {
                	MimeFileDescr mimeFileDescr = this.getMimeFileDescr(activity);
                	if(mimeFileDescr == null) {
	                    String activityNumber = activity.getActivityNumber().trim();
	                    long uid = nextUID++;
	                    File mimeMessageFile = new File(
	                        this.folderDir, 
	                        uid + "-" + activityNumber + ".eml"
	                    );
	                    try {
	                        MimeMessageImpl mimeMessage = new MimeMessageImpl();
	                        Object mappedMessage = Activities.getInstance().mapToMessage(
	                            (EMail)activity, 
	                            mimeMessage
	                        );
	                        if(mappedMessage instanceof InputStream) {
	                        	mimeMessage = new MimeMessageImpl((InputStream)mappedMessage);
	                        	// Update message subject
	                        	mimeMessage.setSubject(((EMail)activity).getMessageSubject());
	                        }
                            OutputStream out = new FileOutputStream(mimeMessageFile);
	                        mimeMessage.setUid(uid);
	                        mimeMessage.writeTo(out);
	                        out.close();
	                    }
	                    catch(Exception e) {
	                    	SysLog.warning("Unable to map activity to mime message", activityNumber);
	                        new ServiceException(e).log();
	                    }
                	}
                }
                if(lastSynchronizedAt != null) {
	                // Remove MIME files for disabled activities                
	                query = (EMailQuery)pm.newQuery(EMail.class);
	                query.activityNumber().isNonNull();
	                query.thereExistsDisabled().isTrue();
	                query.orderByActivityNumber().ascending();                
	                query.modifiedAt().greaterThan(lastSynchronizedAt);
	                activities = activitiesHelper.getFilteredActivities(query);
	                for(org.opencrx.kernel.activity1.jmi1.Activity activity: activities) {
	                    MimeFileDescr mimeFileDescr = this.getMimeFileDescr(activity);
	                    if(mimeFileDescr != null) {
	                    	try {
	                    		mimeFileDescr.getFile().delete();
	                    	} catch(Exception e) {}
	                    }
	                }
	                // Create MIME files for enabled activities	                
	                query = (EMailQuery)pm.newQuery(EMail.class);
	                query.activityNumber().isNonNull();
	                query.forAllDisabled().isFalse();
	                query.orderByActivityNumber().ascending();                
                    query.modifiedAt().greaterThan(lastSynchronizedAt);
	                activities = activitiesHelper.getFilteredActivities(query);
	                for(org.opencrx.kernel.activity1.jmi1.Activity activity: activities) {
	                    String activityNumber = activity.getActivityNumber().trim();
	                    MimeFileDescr mimeFileDescr = this.getMimeFileDescr(activity);
	                    if(mimeFileDescr == null) {
		                    long uid = nextUID++;
		                    File mimeMessageFile = new File(
		                        this.folderDir, 
		                        uid + "-" + activityNumber + ".eml"
		                    );
		                    try {
		                        MimeMessageImpl mimeMessage = new MimeMessageImpl();
		                        Object mappedMessage = Activities.getInstance().mapToMessage(
		                            (EMail)activity, 
		                            mimeMessage
		                        );
		                        if(mappedMessage instanceof InputStream) {
		                        	mimeMessage = new MimeMessageImpl((InputStream)mappedMessage);
		                        	// Update message subject
		                        	mimeMessage.setSubject(((EMail)activity).getMessageSubject());
		                        }
	                            OutputStream out = new FileOutputStream(mimeMessageFile);
		                        mimeMessage.setUid(uid);
		                        mimeMessage.writeTo(out);
		                        out.close();
		                    }
		                    catch(Exception e) {
		                    	SysLog.warning("Unable to map activity to mime message", activityNumber);
		                        new ServiceException(e).log();
		                    }
	                    }
	                }
                }
                // Update .METAINF
                lastSynchronizedAt = new Date();
                this.storeMetaInf(                	
                	new Object[]{createdAt, lastSynchronizedAt, nextUID}
                );
            }
    	}
    	finally {
    		activitiesHelper.close();
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
    	ActivityQueryHelper activitiesHelper = this.getActivitiesHelper();
    	try {
    		MimeFileDescr mimeFileDescr = this.getMimeFileDescr(messageNumber);
    		if(mimeFileDescr != null) {
	            try {
	                FileInputStream in = new FileInputStream(mimeFileDescr.getFile());
	                MimeMessageImpl mimeMessage = new MimeMessageImpl(in);
	                in.close();
	                mimeMessage.setUid(mimeFileDescr.getUID());
	                mimeMessage.setMessageNumber(messageNumber);
	                return mimeMessage;
	            }
	            catch(Exception e) {
	            	SysLog.error("Can not read message " + mimeFileDescr.getFile(), e.getMessage());
	            }
    		}
        }
    	finally {
    		activitiesHelper.close();
    	}
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public int getMessageCount(
    ) throws MessagingException {
        this.synchronizeMailDir();
        Map<Long,File> mimeFiles = this.getMimeFiles();
        return mimeFiles.size();
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
    @Override
    public Message getMessageByUID(
        long uid
    ) throws MessagingException {
    	MimeFileDescr mimeFileDescr = this.getMimeFileDescr(uid);
    	if(mimeFileDescr != null) {
	        try {
	            FileInputStream in = new FileInputStream(mimeFileDescr.getFile());
	            MimeMessageImpl mimeMessage = new MimeMessageImpl(in);
	            in.close();
	            mimeMessage.setUid(uid);
	            mimeMessage.setMessageNumber(mimeFileDescr.getMessageNumber());
	            return mimeMessage;
	        }
	        catch(Exception e) {
	        	SysLog.error("Can not read message " + mimeFileDescr.getFile(), e.getMessage());
	        }
    	}
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public Message[] getMessagesByUID(
        long[] uids
    ) throws MessagingException {
        throw new MessagingException("Unsupported Operation");
    }

    //-----------------------------------------------------------------------
    @Override
    public Message[] getMessagesByUID(
        long start, 
        long end
    ) throws MessagingException {
        List<Message> result = new ArrayList<Message>();
        Map<Long,File> mimeFiles = this.getMimeFiles();
        for(Map.Entry<Long,File> entry: mimeFiles.entrySet()) {
        	long uid = this.getUID(entry.getValue());
            if(uid >= start && uid <= end) {
                result.add(this.getMessageByUID(uid));
            }
        }
        return result.toArray(new Message[result.size()]);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getUID(
        Message message
    ) throws MessagingException {
        return ((MimeMessageImpl)message).getUid();
    }

    //-----------------------------------------------------------------------
    @Override
    public long getUIDValidity(
    ) throws MessagingException {
    	ActivityQueryHelper activitiesHelper = this.getActivitiesHelper();
    	try {
    		Object[] metainf = this.loadMetaInf();
    		if(metainf == null) {
    			this.synchronizeMailDir();
    		}
    		metainf = this.loadMetaInf();
    		return metainf == null ?
    			System.currentTimeMillis() / 1000L :
    				((Date)metainf[META_INF_CREATED_AT]).getTime() / 1000L;
    	}
    	finally {
    		activitiesHelper.close();
    	}
    }
    
	//-----------------------------------------------------------------------
    @Override
    public Message[] search(
    	SearchTerm searchTerm
    ) throws MessagingException {
    	List<Message> messages = new ArrayList<Message>();
    	Map<Long,File> mimeFiles = this.getMimeFiles();
    	int messageNumber = 1;
    	for(Map.Entry<Long,File> mimeFile: mimeFiles.entrySet()) {
    		try {
	            FileInputStream in = new FileInputStream(mimeFile.getValue());
	            MimeMessageImpl mimeMessage = new MimeMessageImpl(in);
	            in.close();
	            mimeMessage.setUid(this.getUID(mimeFile.getValue()));
	            mimeMessage.setMessageNumber(messageNumber);
	            mimeMessage.setFlag(Flags.Flag.SEEN, true);
	            mimeMessage.setFlag(Flags.Flag.DELETED, false);
	            mimeMessage.setFlag(Flags.Flag.ANSWERED, false);
	            mimeMessage.setFlag(Flags.Flag.RECENT, false);
	            mimeMessage.setFlag(Flags.Flag.DRAFT, false);
	    		if(searchTerm == null || searchTerm.match(mimeMessage)) {
	    			messages.add(mimeMessage);
	    		}
    		} catch(Exception e) {}
    		messageNumber++;
    	}
    	return messages.toArray(new Message[messages.size()]);
    }
    
	//-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected static final String METAINF_FILE_NAME = ".METAINF";
    protected static final long SYNCHRONIZE_REFRESH_RATE = 60000;
    public static final String MAILDIR_PROPERTY_NAME = "org.opencrx.maildir";
	public static final int META_INF_CREATED_AT = 0;
	public static final int META_INF_LAST_SYNCHRONIZED_AT = 1;
	public static final int META_INF_NEXT_UID = 2;
	    
    protected final String name;
    protected File folderDir;
    protected final String username;
    protected final String folderId;
    protected final PersistenceManagerFactory pmf;

}

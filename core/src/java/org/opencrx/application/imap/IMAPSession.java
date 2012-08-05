/**
 * The ClientHandler class is responsible for replying to client requests.
 */
package org.opencrx.application.imap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;

import org.opencrx.application.adapter.AbstractServer;
import org.opencrx.application.adapter.AbstractSession;
import org.opencrx.kernel.activity1.jmi1.ActivityCategory;
import org.opencrx.kernel.activity1.jmi1.ActivityMilestone;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.utils.ActivitiesFilterHelper;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.io.QuotaByteArrayOutputStream;
import org.openmdx.base.naming.Path;
import org.openmdx.base.text.conversion.UUIDConversion;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.kernel.text.format.HexadecimalFormatter;

public class IMAPSession extends AbstractSession {
    
    //-----------------------------------------------------------------------
    public IMAPSession(
        Socket client, 
        AbstractServer server
    ) {
    	super(
    		client,
    		server
    	);
    }

    //-----------------------------------------------------------------------
    private String readLine(
    ) throws IOException {
        StringBuilder line = new StringBuilder();
        while(true) {
            char c = (char)this.in.read();
            if(c == '\r') {
                c = (char)this.in.read();
            }
            if(c == '\n') {
                break;
            }
            if(c == -1) {
                return null;
            }
            line.append(c);
            if(line.length() > MAX_LINE_LENGTH) {
                throw new IOException("Error: line too long. Details: " + Arrays.asList(this.username, line.length(), line));
            }
        }
        return line.toString();
    }

    //-----------------------------------------------------------------------
    public void run(
    ) {
        if(this.socket == null || !this.socket.isConnected()) {
            System.out.println("Unable to start conversation, invalid connection passed to client handler");
        } 
        else {
        	SysLog.info("Session started for client", this.socket.getInetAddress().getHostAddress());
            try {
                this.out = new PrintStream(this.socket.getOutputStream());
                this.in = this.socket.getInputStream();
                this.println("* OK [CAPABILTY IMAP4rev1] OPENCRX");
                String line = this.readLine();
                Pattern pattern = Pattern.compile("([a-zA-Z0-9]+) ([a-zA-Z0-9]+)(.*)");
                while (line != null) {
                    if((line.indexOf("LOGIN") > 0) && (line.indexOf(" ") > 0)) {
                        System.out.println(">>> IMAPServer[" + this.socket.getInetAddress() + "]\n" + line.substring(0, line.lastIndexOf(" ")));
                    }
                    else if(line.indexOf("LOGOUT") > 0) {
                        System.out.println(">>> IMAPServer[" + this.socket.getInetAddress() + "]\n" + line + " " + this.username);
                    }
                    if(this.server.isDebug()) {
                        System.out.println(">>> IMAPServer[" + this.socket.getInetAddress() + "]\n" + line);
                    }
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.find()) {
                        String tag = matcher.group(1);
                        String command = matcher.group(2);
                        String params = matcher.group(3);
                        if(!this.handleCommand(tag, command, params)) {                            
                            this.socket.close();                       
                            if(this.pm != null) {
                                try {
                                    this.pm.close();
                                } 
                                catch(Exception e) {}
                            }
                            return;
                        }
                    }
                    line = this.readLine();
                }
                SysLog.info("connection closed", socket.getInetAddress().getHostAddress());
            } 
            catch (Exception e) {
                if(!(e instanceof SocketTimeoutException)) {
                    ServiceException e0 = new ServiceException(e);
                    SysLog.detail(e0.getMessage(), e0.getCause());
                }
            }
            finally {                
                try {
                    // Make sure to close connection when thread terminates. 
                    // Otherwise we may have open connections with no listening threads.
                    this.socket.close();
                } 
                catch(Exception e) {}                
            }
        }
    }

    //-----------------------------------------------------------------------
    private String encodeFolderName(
        String name
    ) {
        // Slash is qualified name separator. Do not allow in folder names
        name = name.replace("/", ".");
        StringBuilder encodedName = new StringBuilder();
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if((c <= 127) && (c != '&')) {
                encodedName.append(c);
            }
            else if(c == '&') {
            	encodedName.append("&-");
            }
            else {
                String base64EncodedChar = org.openmdx.base.text.conversion.Base64.encode(new byte[]{(byte)(c / 256), (byte)(c % 256)});
                encodedName.append("&" + base64EncodedChar.substring(0, base64EncodedChar.length()-1) + "-");
            }
        }
        return encodedName.toString();      
    }
    
    //-----------------------------------------------------------------------
    /**
     * Return all folders which the user is allowed to subscribe.
     */
    private Map<String,String> getAvailableFolders(
    ) throws MessagingException {     
    	if(System.currentTimeMillis() > this.refreshFoldersAt) {
    		this.pm.evictAll();
            Map<String,String> folders = new HashMap<String,String>();    		
	        String providerName = this.server.getProviderName();
	        String segmentName = username.substring(username.indexOf("@") + 1);
	        org.opencrx.kernel.activity1.jmi1.Segment activitySegment = 
	            (org.opencrx.kernel.activity1.jmi1.Segment)this.pm.getObjectById(
	                new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/" + providerName + "/segment/" + segmentName)
	            );
	        Collection<ActivityTracker> trackers = activitySegment.getActivityTracker();
	        for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: trackers) {
	            String groupUri = "/" + providerName + "/" + segmentName + "/tracker/";
	            folders.put(
	                "INBOX" + groupUri + this.encodeFolderName(group.getName()),
	                groupUri + group.getName()
	            );
	        }
	        Collection<ActivityMilestone> milestones = activitySegment.getActivityMilestone();
	        for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: milestones) {
	            String groupUri = "/" + providerName + "/" + segmentName + "/milestone/";
	            folders.put(
	                "INBOX" + groupUri + this.encodeFolderName(group.getName()),
	                groupUri + group.getName()
	            );
	        }
	        Collection<ActivityCategory> categories = activitySegment.getActivityCategory();
	        for(org.opencrx.kernel.activity1.jmi1.ActivityGroup group: categories) {
	            String groupUri = "/" + providerName + "/" + segmentName + "/category/";
	            folders.put(
	                "INBOX" + groupUri + this.encodeFolderName(group.getName()),
	                groupUri + group.getName()
	            );
	        }
	        this.availableFolders = folders;
	        this.refreshFoldersAt = System.currentTimeMillis() + FOLDER_REFRESH_PERIOD_MILLIS;
    	}
        return this.availableFolders;
    }
                
    //-----------------------------------------------------------------------
    /**
     * process the command and send a response if appropriate...
     * 
     * @param line
     */
    public boolean handleCommand(
        String tag, 
        String command, 
        String params
    ) throws MessagingException {
		command = command.toUpperCase();
		if(command.equals("CAPABILITY")) {
			this.println("* CAPABILITY IMAP4rev1");
			this.println(tag + " OK CAPABILITY complete");
		}
		else if(command.equals("NOOP")) {
			this.println(tag + " OK NOOP completed");
		}
		else if (command.equals("LOGOUT")) {
			this.println("* BYE IMAP4rev1 Server logging out");
			this.println(tag + " OK LOGOUT complete");
			this.logout();
			try  {
				return false;
			}
			catch (Exception e) {
				new ServiceException(e).log();
			}
		}
		else {
			if (this.state == NOT_AUTHENTICATED_STATE) {
				if(command.equals("LOGIN")) {
					String username = "";
					String password = "";
					try {
						String[] bits = params.split(" ");
						username = bits[1].replace("\"", "");
						password = bits[2].replace("\"", "");
					}
					catch (Exception e) {
						this.println(tag + " BAD parameters");
						return true;
					}
					if(this.login(username, password)) {
						this.state = AUTHENTICATED_STATE;
						this.println(tag + " OK User logged in");
					} 
					else {
						this.println(tag + " NO LOGIN failed.");
					}
				}
				else {
					this.unrecognizedCommand(tag, command);
				}
			}
			else if (this.state == AUTHENTICATED_STATE || this.state == SELECTED_STATE) {
				if (command.equals("SELECT")) {
					params = params.replace("\"", "");
					params = params.trim().toUpperCase();
					this.selectedFolder = null;
					IMAPFolderImpl folder = this.getFolder(params);
					if(folder != null) {
						this.selectedFolder = folder;
						this.println("* " + folder.getMessageCount() + " EXISTS");
						this.println("* 0 RECENT");
						this.println("* OK [UIDVALIDITY " + folder.getUIDValidity() + "] UID validity status");
						this.println(tag + " OK [" + (folder.getMode() == Folder.READ_ONLY ? "READ-ONLY" : "READ-WRITE") + "] complete");
						this.state = SELECTED_STATE;
					}
					if(this.selectedFolder == null) {
					    this.println(tag + " NO SELECT failed, no mailbox with that name");
					}
				}
				else if (command.equals("STATUS")) {
					params = params.replace("\"", "");
					params = params.replace("(UNSEEN)", "");
					params = params.replace("(MESSAGES UNSEEN)", "");
					params = params.trim().toUpperCase();
					IMAPFolderImpl folder = this.getFolder(params);
                    if(folder != null) {
                        this.println("* " + folder.getMessageCount() + " EXISTS");
						this.println("* 0 RECENT");
						this.println("* OK [UIDVALIDITY " + folder.getUIDValidity() + "] UID validity status");
						this.println(tag + " OK [" + (folder.getMode() == Folder.READ_ONLY ? "READ-ONLY" : "READ-WRITE") + "] complete");
						this.state = SELECTED_STATE;
					}
                    else {
						this.println(tag + " NO STATUS failed, no mailbox with that name");
					}
				}
				else if(command.equals("EXAMINE")) {
					params = params.replace("\"", "");
					params = params.trim().toUpperCase();
                    IMAPFolderImpl folder = this.getFolder(params);
                    if(folder != null) {
                        this.println("* " + folder.getMessageCount() + " EXISTS");
                        this.println("* 0 RECENT");
                        this.println("* OK [UIDVALIDITY " + folder.getUIDValidity() + "] UID validity status");
                        this.println(tag + " OK [" + (folder.getMode() == Folder.READ_ONLY ? "READ-ONLY" : "READ-WRITE") + "] complete");
                    }
                    else {
						this.println(tag + " NO EXAMINE failed, no mailbox with that name");
					}
				}
				else if(command.equals("CREATE")) {
					this.println(tag + " NO command not supported");
				}
				else if (command.equals("DELETE")) {
					this.println(tag + " NO command not supported");
				}
				else if (command.equals("RENAME")) {
					this.println(tag + " NO command not supported");
				}
				else if(command.equals("LIST")) {
                    Pattern pattern = Pattern.compile(" \"([a-zA-Z0-9]*)\" \"([a-zA-Z0-9*%]+)\"");
                    Matcher matcher = pattern.matcher(params);
                    if (matcher.find()) {
                        String folderName = matcher.group(1);
                        String query = matcher.group(2);
                        if("".equals(folderName)) {
                            for(String folder: this.getAvailableFolders().keySet()) {
                                if(
                                    folder.equals(query) ||
                                    ((query.endsWith("*") || query.endsWith("%")) && folder.startsWith(query.substring(0, query.length()-1)))
                                ) {
                                    this.println("* LIST () \"/\" \"" + folder + "\"");
                                }
    						}
                        }
					}
                    this.println(tag + " OK LIST complete");
				}
				else if(command.equals("LSUB")) {
                    for(Folder folder: this.getSubscribedFolders()) {
                    	println("* LSUB () \"/\" \"" + folder.getFullName() + "\"");
					}
					this.println(tag + " OK LSUB complete");
				}
				else if(command.equals("SUBSCRIBE")) {
                    params = params.replace("\"", "");
                    params = params.trim();
                    Map<String,String> availableFolders = this.getAvailableFolders();
                    if(availableFolders.containsKey(params)) {
                        this.subscribeFolder(
                            params, 
                            availableFolders
                        );                     
                        this.println(tag + " OK SUBSCRIBE complete");                    
                    }
                    else {
                        this.println(tag + " NO SUBSCRIBE invalid folder name");                                            
                    }
				}
				else if(command.equals("UNSUBSCRIBE")) {
                    params = params.replace("\"", "");
                    params = params.trim();
                    List<IMAPFolderImpl> subscribedFolders = this.getSubscribedFolders();
                    boolean found = false;
                    for(IMAPFolderImpl folder: subscribedFolders) {
                        if(folder.getFullName().equals(params)) {
                            this.unsubscribeFolder(
                                params
                            );
                            found = true;
                            break;
                        }
                    }
                    if(found) {
                        this.println(tag + " OK UNSUBSCRIBE complete");
                    }
                    else {
                        this.println(tag + " NO UNSUBSCRIBE invalid folder name");                        
                    }
				}
				else if(command.equals("APPEND")) {
				    Pattern pattern = Pattern.compile(" \"(.*)\"(?: \\((.*)\\))? \"(.*)\" \\{([0-9]+)\\}");
				    Matcher matcher = pattern.matcher(params);
				    if(matcher.find()) {
				        try {
    				        int size = Integer.valueOf(matcher.group(4));
    				        IMAPFolderImpl folder = this.getFolder(matcher.group(1));
    				        String date = matcher.group(3);
    				        if(folder != null) {
        	                    this.println("+ OK");
        	                    if(this.server.isDebug()) {
        	                        System.out.println("Reading " + size + " bytes");
        	                    }
        	                    byte[] msg = new byte[size];
        	                    boolean success = true;
        	                    int i = 0;
        	                    for(i = 0; i < size; i++) {
        	                        if(this.server.isDebug() && (i > 0) && (i % 1000 == 0)) {
        	                            System.out.println(i + " bytes");
        	                        }
        	                        int c = this.in.read();
        	                        if(c < 0) {
        	                        	success = false;
        	                        	break;
        	                        }
        	                        msg[i] = (byte)c;
        	                    }
        	                    if(this.server.isDebug()) {
        	                        System.out.println(new String(msg, "iso-8859-1"));
        	                        System.out.println();
        	                        System.out.println(new HexadecimalFormatter(msg, 0, size).toString());
                                    System.out.println();        	                        
        	                        System.out.flush();
        	                    }
        	                    // Import if upload is successful
        	                    if(success) {
                                    Message message = new MimeMessageImpl(
                                        new ByteArrayInputStream(msg)
                                    );
                                    message.setHeader("Date", date);
                                    folder.appendMessages(new Message[]{message});
                                    this.println(tag + " OK APPEND complete");        	                        
        	                    }
        	                    else {
                                    this.println(tag + " NO invalid message");                                  	                        
        	                    }
    				        }
    				        else {
                                this.println(tag + " NO folder not found");                                				            
    				        }
				        }
				        catch(Exception e) {
		                    this.println(tag + " NO invalid message");				            
				        }
				    }
				    else {
				        this.println(tag + " NO invalid parameter");
				    }
				}
				else if(this.state == SELECTED_STATE) {
					if(command.equals("CHECK")) {
						this.println(tag + " OK CHECK complete");
					}
					else if(command.equals("CLOSE")) {
						this.println(tag + " OK CLOSE complete");
						this.state = AUTHENTICATED_STATE;
					}
					else if(command.equals("EXPUNGE")) {
						this.println(tag + " NO EXPUNGE command not supported");
					}
					else if(command.equals("SEARCH")) {
						this.println(tag + " NO SEARCH command not supported");
					}
					else if(command.equals("FETCH")) {
                        // FETCH n1,n2, ...
                        int posCommands = params.indexOf("(");
					    StringTokenizer p = new StringTokenizer(params.substring(0, posCommands), " ", false);
					    if(p.countTokens() == 1) {
                            List<int[]> messageNumbers = new ArrayList<int[]>();
                            StringTokenizer g = new StringTokenizer(p.nextToken(), ",", false);
                            while(g.hasMoreTokens()) {
                                try {
                                    String number = g.nextToken();
                                    if(number != null) {
                                        int start = -1;
                                        int end = -1;
                                        if(number.indexOf(":") > 0) {
                                            String startAsString = number.substring(0, number.indexOf(":"));
                                            start = "*".equals(startAsString) ? 0 : Integer.valueOf(startAsString);
                                            String endAsString = number.substring(number.indexOf(":") + 1);        
                                            end = "*".equals(endAsString) ? 0 : Integer.valueOf(endAsString);
                                        }
                                        else {
                                            start = Integer.valueOf(number);
                                            end = start;
                                        }
                                        messageNumbers.add(new int[]{start,end});
                                    }
                                }
                                catch(Exception e) {}                                    
                            }                                
                            String fetchParams = params.substring(posCommands);   
                            for(int[] messageNumber: messageNumbers) {
                                for(Message message: this.selectedFolder.getMessages(messageNumber[0], messageNumber[1])) {
                                    if(message instanceof MimeMessage) {
                                        List<String> commands = this.getFetchCommands(fetchParams);
                                        this.print("* " + message.getMessageNumber() + " FETCH (");
                                        int n = 0;
                                        for(String cmd: commands) {
                                            boolean success = this.processFetchCommand(
                                                cmd, 
                                                (MimeMessage)message, 
                                                n
                                            );
                                            if(success) n++;
                                        }
                                        this.println(")");
                                    }
                                }
                            }
                            this.println(tag + " OK FETCH complete");
                        }
					}
					else if(command.equals("STORE")) {
						this.println(tag + " NO STORE command not supported");
					}
					else if(command.equals("COPY")) {
						this.println(tag + " NO COPY command not supported");
					}
					else if(command.equals("UID")) {
					    String uidCommand = params.trim().toUpperCase();
						if(uidCommand.startsWith("FETCH")) {
						    int posCommands = params.indexOf("(");
						    StringTokenizer p = new StringTokenizer(params.substring(0, posCommands), " ", false);
						    if(p.countTokens() == 2) {
                                List<long[]> messageUids = new ArrayList<long[]>();
                                p.nextToken();
                                StringTokenizer g = new StringTokenizer(p.nextToken(), ",", false);
                                while(g.hasMoreTokens()) {
                                    try {
                                        String uid = g.nextToken();
                                        if(uid != null) {
                                            long start = -1;
                                            long end = -1;
                                            if(uid.indexOf(":") > 0) {
                                                String startAsString = uid.substring(0, uid.indexOf(":"));
                                                start = "*".equals(startAsString) ? 0L : Long.valueOf(startAsString);
                                                String endAsString = uid.substring(uid.indexOf(":") + 1);                        
                                                end = "*".equals(endAsString) ? MAX_ACTIVITY_NUMBER : Long.valueOf(endAsString);
                                            }
                                            else {
                                                start = Long.valueOf(uid);
                                                end = start;
                                            }
                                            messageUids.add(new long[]{start,end});
                                        }
                                    }
                                    catch(Exception e) {}                                    
                                }                                
                                String fetchParams = params.substring(posCommands);
                                for(long[] messageUid: messageUids) {
                                    for(Message message: this.selectedFolder.getMessagesByUID(messageUid[0], messageUid[1])) {                                    
                                        if(message instanceof MimeMessage) {
                                            List<String> commands = this.getFetchCommands(fetchParams);
                                            if(!commands.contains("UID")) {
                                                commands.add(0, "UID");
                                            }
                                            this.print("* " + message.getMessageNumber() + " FETCH (");
                                            int n = 0;
                                            for(String cmd: commands) {
                                                boolean success = this.processFetchCommand(
                                                    cmd, 
                                                    (MimeMessage)message, 
                                                    n
                                                );
                                                if(success) n++;
                                            }
                                            this.println(")");
                                        }
                                    }
                                }
                                this.println(tag + " OK UID complete");
                            }
						}
	                    else if(uidCommand.startsWith("COPY")) {
	                        Pattern pattern = Pattern.compile(" (?:COPY|copy) ([0-9*\\:]+)(?:,([0-9*\\:]+))* \"(.*)\"");
	                        Matcher matcher = pattern.matcher(params);
	                        if(matcher.find()) {
	                            IMAPFolderImpl folder = this.getFolder(matcher.group(matcher.groupCount()));
                                if(folder != null) {
                                    List<long[]> messageUids = new ArrayList<long[]>();
                                    for(int i = 1; i < matcher.groupCount(); i++) {
                                        try {
                                            String uid = matcher.group(i);
                                            if(uid != null) {
                                                long start = -1;
                                                long end = -1;
                                                if(uid.indexOf(":") > 0) {
                                                    String startAsString = uid.substring(0, uid.indexOf(":"));
                                                    start = "*".equals(startAsString) ? 0L : Long.valueOf(startAsString);
                                                    String endAsString = uid.substring(uid.indexOf(":") + 1);                        
                                                    end = "*".equals(endAsString) ? MAX_ACTIVITY_NUMBER : Long.valueOf(endAsString);
                                                }
                                                else {
                                                    start = Long.valueOf(uid);
                                                    end = start;
                                                }
                                                messageUids.add(new long[]{start,end});
                                            }
                                        }
                                        catch(Exception e) {}                                    
                                    }                                
                                    for(long[] messageUid: messageUids) {
                                        List<MimeMessage> newMessages = new ArrayList<MimeMessage>(); 
                                        for(Message message: this.selectedFolder.getMessagesByUID(messageUid[0], messageUid[1])) {
                                            if(message instanceof MimeMessage) {
                                                newMessages.add(new MimeMessageImpl((MimeMessage)message));
                                            }
                                        }
                                        folder.appendMessages(
                                            newMessages.toArray(new MimeMessage[newMessages.size()])
                                        );
                                    }
                                }
	                        }
                            this.println(tag + " OK UID complete");
	                    }
	                    else {
	                        this.println(tag + " NO UID" + params + " command not supported");                     
	                    }
					}
				}
				else {
					this.unrecognizedCommand(tag, command);
				}
			}
		}
		return true;
	}

    //-----------------------------------------------------------------------
    public void unrecognizedCommand(
        String tag, 
        String command
    ) {
    	SysLog.detail("Could not", tag + " " + command);
        this.println(tag + " BAD " + command);
    }

    //-----------------------------------------------------------------------
    public List<String> getFetchCommands(
        String fetchParams
    ) {
        List<String> fetchCommands = new ArrayList<String>();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9\\.]+(\\[\\])?(\\[[a-zA-Z0-9\\.()\\- ]+\\])?");
        Matcher matcher = pattern.matcher(fetchParams);
        while(matcher.find()) {
            String newCommand = matcher.group(0);
            if(!newCommand.startsWith("BODY")) {
                fetchCommands.add(0, newCommand);
            }
            else {
                fetchCommands.add(newCommand);
            }
            fetchParams = fetchParams.substring(newCommand.length()).trim();
            matcher = pattern.matcher(fetchParams);
        }
        return fetchCommands;
    }

    //-----------------------------------------------------------------------
    protected void getBodyAsRFC822(
        MimePart part,
        boolean ignoreHeaders,
        QuotaByteArrayOutputStream out
    ) {
        try {
            out.reset();
            if(ignoreHeaders) {
                OutputStream os = MimeUtility.encode(out, part.getEncoding());
                part.getDataHandler().writeTo(os);
                os.flush();           
            }
            else {
                part.writeTo(out);
                out.close();
            }
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
    }
    
    //-----------------------------------------------------------------------
    protected int getBodyLength(
        MimePart part,
        boolean ignoreHeaders
    ) {
        try {
            QuotaByteArrayOutputStream bos = byteOutputStreams.get();
            bos.reset();
            if(ignoreHeaders) {
                OutputStream os = MimeUtility.encode(bos, part.getEncoding());
                part.getDataHandler().writeTo(os);
                os.flush();           
            }
            else {
                part.writeTo(bos);
                bos.close();
            }
            return bos.size();
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
        return 0;
    }
    
    //-----------------------------------------------------------------------
    protected String getMessageFlags(
        Message message
    ) throws MessagingException {
        return "\\Seen";
    }
    
    //-----------------------------------------------------------------------
    public boolean processFetchCommand(
        String params, 
        MimeMessage message,
        int count
    ) throws MessagingException {
        String command = params;
        if (params.indexOf(" ") != -1) {
            command = params.substring(0, params.indexOf(" "));
            params = params.substring(params.indexOf(" ")).trim();
        }
        command = command.toUpperCase();
        if (command.equals("FLAGS")) {
            if(count > 0) this.print(" ");
            this.print("FLAGS (" + this.getMessageFlags(message) + ")");
            return true;
        } 
        else if(command.equals("RFC822")) {
            if(count > 0) this.print(" ");
            QuotaByteArrayOutputStream messageRFC822 = byteOutputStreams.get(); 
            this.getBodyAsRFC822(message, false, messageRFC822);
            this.println("RFC822 {" + (messageRFC822.size() + 2) + "}");
            this.printBytes(messageRFC822);
            this.println("");
            return true;
        } 
        else if(command.equals("RFC822.HEADER")) {
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                MimeMessageImpl.STANDARD_HEADER_FIELDS
            );
            if(count > 0) this.print(" ");
            this.println("RFC822 {" + (temp.length() + 2) + "}");
            this.print(temp);
            return true;
        } 
        else if(command.equals("RFC822.SIZE")) {
            if(count > 0) this.print(" ");
            int length = this.getBodyLength(message, false);            
            this.print("RFC822.SIZE " + length);
            return true;
        } 
        else if(command.equals("UID")) {
            if(count > 0) this.print(" ");
            this.print("UID " + this.selectedFolder.getUID(message));
            return true;
        } 
        else if(command.equals("BODY[]") || command.equals("BODY.PEEK[]")) {
            if(count > 0) this.print(" ");
            QuotaByteArrayOutputStream messageRFC822 = byteOutputStreams.get(); 
            this.getBodyAsRFC822(message, false, messageRFC822);
            this.println("BODY[] {" + (messageRFC822.size() + 2) + "}");
            this.printBytes(messageRFC822);
            this.println("");
            return true;
        } 
        else if(command.equals("BODY[HEADER]")) {
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                MimeMessageImpl.STANDARD_HEADER_FIELDS
            );
            if(count > 0) this.print(" ");
            this.println("BODY[HEADER] {" + (temp.length() + 2) + "}");
            this.print(temp);
            return true;
        } 
        else if(command.matches("BODY\\.PEEK\\[([0-9]+)(?:\\.MIME)?\\]")) {
            try {
                if(message.getContent() instanceof MimeMultipart) {
                    Pattern pattern = Pattern.compile("BODY\\.PEEK\\[([0-9]+)(?:\\.MIME)?\\]");
                    Matcher matcher = pattern.matcher(command);
                    if(matcher.find()) {
                        int partId = Integer.valueOf(matcher.group(1)).intValue();
                        if(command.indexOf(".MIME") > 0) {
                            String temp = MimeMessageImpl.getHeadersAsRFC822(
                                ((MimeMultipart)message.getContent()).getBodyPart(partId - 1), 
                                new String[]{"Content-Type", "Content-Disposition", "Content-Transfer-Encoding"}
                            );                                                
                            if(count > 0) this.print(" ");
                            this.println("BODY[" + partId + ".MIME] {" + (temp.length() + 2) + "}");
                            this.println(temp);
                            return true;
                        }
                        else {
                            QuotaByteArrayOutputStream temp = byteOutputStreams.get();                         	
                            this.getBodyAsRFC822(
                                (MimePart)((MimeMultipart)message.getContent()).getBodyPart(partId - 1),
                                true,
                                temp
                            );
                            if(count > 0) this.print(" ");
                            this.println("BODY[" + partId + "] {" + (temp.size() + 2) + "}");
                            this.printBytes(temp);
                            this.println("");
                            return true;
                        }
                    }
                }
            }
            catch(IOException e) {
                throw new MessagingException(e.getMessage());
            }
        } 
        else if(command.equals("BODY[TEXT]")) {
            if(count > 0) this.print(" ");
            QuotaByteArrayOutputStream messageRFC822 = byteOutputStreams.get();             
            this.getBodyAsRFC822(message, false, messageRFC822);
            this.println("BODY[TEXT] {" + (messageRFC822.size() + 2) + "}");
            this.printBytes(messageRFC822);
            this.println("");
            return true;
        } 
        else if(command.equals("BODY[HEADER.FIELDS")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");
            if(count > 0) this.print(" ");
            this.print("BODY[HEADER.FIELDS (");
            String[] fields = params.split(" ");
            int n = 0;
            for (int i = 0; i < fields.length; i++) {
                if(n > 0) this.print(" ");
                this.print("\"" + fields[i].toUpperCase() + "\"");
                n++;
            }
            this.print(")] ");
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                fields
            );
            this.println("{" + (temp.length() + 2) + "}");
            this.println(temp);
            return true;
        } 
        else if(command.equals("BODY[HEADER.FIELDS.NOT")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");
            String[] fields = params.split(" ");
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                fields
            );
            if(count > 0) this.print(" ");
            this.println("BODY[HEADER.FIELDS.NOT" + params + " {" + (temp.length() + 2) + "}");
            this.println(temp);
            return true;
        } 
        else if(command.equals("BODY.PEEK[TEXT]")) {
            if(count > 0) this.print(" ");
            QuotaByteArrayOutputStream messageRFC822 = byteOutputStreams.get();             
            this.getBodyAsRFC822(message, false, messageRFC822);
            this.println("BODY[TEXT] {" + (messageRFC822.size() + 2) + "}");
            this.printBytes(messageRFC822);
            this.println("");
            return true;
        } 
        else if(command.equals("BODY.PEEK[HEADER]")) {
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                MimeMessageImpl.STANDARD_HEADER_FIELDS
            );
            if(count > 0) this.print(" ");
            this.println("BODY[HEADER] {" + (temp.length() + 2) + "}");
            this.println(temp);
            return true;
        } 
        else if(command.equals("BODY.PEEK[HEADER.FIELDS")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");
            if(count > 0) this.print(" ");
            this.print("BODY[HEADER.FIELDS (");
            String[] fields = params.split(" ");
            int n = 0;
            for(int i = 0; i < fields.length; i++) {
                if(n > 0) this.print(" ");
                this.print("\"" + fields[i].toUpperCase() + "\"");
                n++;
            }
            this.print(")] ");
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                fields
            );
            this.println("{" + (temp.length() + 2) + "}");
            this.println(temp);
            return true;
        } 
        else if(command.equals("BODY.PEEK[HEADER.FIELDS.NOT")) {
            params = params.replace("]", "");
            params = params.replace("(", "");
            params = params.replace(")", "");
            params = params.replace("\"", "");
            String[] fields = params.split(" ");
            String temp = MimeMessageImpl.getHeadersAsRFC822(
                message, 
                fields
            );
            if(count > 0) this.print(" ");
            this.println("BODY[HEADER.FIELDS.NOT" + params + " {" + (temp.length() + 2) + "}");
            this.println(temp);
            return true;
        } 
        else if(command.equals("INTERNALDATE")) {
            if(count > 0) this.print(" ");
            this.print("INTERNALDATE ");
            this.printList(message.getHeader("Date"), true);
            return true;
        } 
        else if(command.equals("ENVELOPE")) {
            if(count > 0) this.print(" ");
            this.print("ENVELOPE (");
            this.printList(message.getHeader("Date"), true);
            this.print(" ");
            String[] subjects = message.getHeader("Subject");
            if(subjects.length > 0) {
                String subject = subjects[0].replace("\r\n", " ");
                subject = subject.replace("\r", " ");
                subject = subject.replace("\n", " ");
                this.printList(new String[]{subject}, true);
            }
            try {
                this.printAddresses(message.getFrom(), 1);
            } 
            catch(Exception e) {
                this.printAddresses(null, 1);
            }
            try {
                this.printAddresses(message.getFrom(), 1);
            }
            catch(Exception e) {
                this.printAddresses(null, 1);
            }
            try {
                this.printAddresses(message.getFrom(), 1);
            }
            catch(Exception e) {
                this.printAddresses(null, 1);
            }
            try {
                this.printAddresses(message.getAllRecipients(), 1);                
            }
            catch(Exception e) {
                this.printAddresses(null, 1);
            }            
            this.print(" NIL NIL NIL ");
            this.printList(message.getHeader("Message-Id"), true);
            this.print(")");
            return true;
        } 
        else if(command.equals("BODYSTRUCTURE")) {
            if(count > 0) this.print(" ");
            this.print("BODYSTRUCTURE ");
            this.printMessageStructure(message);
            return true;
        } 
        return false;
    }

    //-----------------------------------------------------------------------
    private void updateSubscriptionFile(
        List<IMAPFolderImpl> folders            
    ) {
        try {
            File mailDir = IMAPFolderImpl.getMailDir(this.username);
            mailDir.mkdirs();        
            File subscriptionsFile = new File(mailDir, ".SUBSCRIPTIONS");
            if(!subscriptionsFile.exists()) {
                PrintStream ps = new PrintStream(subscriptionsFile);
                ps.println();
                ps.close();
            }
            PrintStream ps = new PrintStream(subscriptionsFile);
            for(IMAPFolderImpl f: folders) {
                ps.println(f.getFullName());
            }
            ps.close();                
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }       
    }
    
    //-----------------------------------------------------------------------
    private void unsubscribeFolder(
        String name
    ) throws MessagingException {
        List<IMAPFolderImpl> folders = this.getSubscribedFolders();
        for(Iterator<IMAPFolderImpl> i = folders.iterator(); i.hasNext(); ) {
            IMAPFolderImpl folder = i.next();
            if(folder.getFullName().equals(name)) {
                // Mark folder as unsubscribed (can be deleted)
                if(!"INBOX".equalsIgnoreCase(name)) {
                    try {
                        File dest = new File(
                            folder.folderDir.getParentFile(),
                            folder.folderDir.getName() + "-" + UUIDConversion.toUID(UUIDs.getGenerator().next()) + ".UNSUBSCRIBE"
                        );
                        folder.folderDir.renameTo(dest);
                    } 
                    catch(Exception e) {
                        new ServiceException(e).log();
                    }                    
                }
                i.remove();
                break;
            }
        }
        this.updateSubscriptionFile(folders);
    }
    
    //-----------------------------------------------------------------------
    private void subscribeFolder(
        String name,
        Map<String,String> availableFolders
    ) throws MessagingException {
        List<IMAPFolderImpl> folders = this.getSubscribedFolders();
        // Check whether already subscribed
        for(IMAPFolderImpl folder: folders) {
            if(name.equals(folder.getFullName())) {
                return;
            }
        }
        // Add folder
        ActivitiesFilterHelper activitiesHelper = new ActivitiesFilterHelper(this.pm);
        activitiesHelper.parseFilteredActivitiesUri(
            availableFolders.get(name)
        );
        IMAPFolderImpl folder = new IMAPFolderImpl(
            name,
            this.username,
            activitiesHelper
        );
        folders.add(folder);
        this.updateSubscriptionFile(folders);
        folder.synchronizeMailDir();
    }
    
    //-----------------------------------------------------------------------
    private List<IMAPFolderImpl> getSubscribedFolders(
    ) throws MessagingException {   
        List<IMAPFolderImpl> folders = new ArrayList<IMAPFolderImpl>();        
        try {
            File mailDir = IMAPFolderImpl.getMailDir(this.username);
            mailDir.mkdirs();
            File subscriptionsFile = new File(mailDir, ".SUBSCRIPTIONS");
            folders.add(
                new IMAPFolderImpl(
                    "INBOX",
                    this.username,
                    null
                )
            );            
            if(subscriptionsFile.exists()) {
                Map<String,String> availableFolders = this.getAvailableFolders();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream(subscriptionsFile)
                    )
                );
                while(reader.ready()) {
                    String name = reader.readLine();
                    if(availableFolders.containsKey(name)) {
                        ActivitiesFilterHelper activitiesHelper = new ActivitiesFilterHelper(this.pm);
                        try {
                            activitiesHelper.parseFilteredActivitiesUri(
                                availableFolders.get(name)
                            );
                            IMAPFolderImpl folder = new IMAPFolderImpl(
                                name,
                                this.username,
                                activitiesHelper
                            );                        
                            folders.add(folder);
                        }
                        catch(Exception e) {}
                    }
                }
            }
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }
        return folders;
    }
                
    //-----------------------------------------------------------------------
    private IMAPFolderImpl getFolder(
        String name
    ) throws MessagingException {
        for(IMAPFolderImpl folder: this.getSubscribedFolders()) {
            if(
                folder.getFullName().equalsIgnoreCase(name) || 
                // Required for some Outlook versions
                folder.getFullName().replace("/", "").equalsIgnoreCase(name)
            ) { 
                return folder;
            }
        }
        return null;
    }
    
    //-----------------------------------------------------------------------
    protected void println(
        String s
    ) {      
        try {
            if(this.server.isDebug()) {
                System.out.println(s);
                System.out.flush();
            }
            this.out.write(s.getBytes("US-ASCII"));
            this.out.write("\r\n".getBytes("US-ASCII"));
            this.out.flush();
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
    }

    //-----------------------------------------------------------------------
    protected void print(
        String s
    ) {
        try {        	
            if(this.server.isDebug()) {
                System.out.print(s);
                System.out.flush();
            }
            this.out.write(s.getBytes("US-ASCII"));
            this.out.flush();
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
    }

    //-----------------------------------------------------------------------
    protected void printBytes(
        QuotaByteArrayOutputStream bytes
    ) {
        try {
            if(this.server.isDebug()) {
                System.out.print(new String(bytes.getBuffer(), 0, bytes.size(), "UTF-8"));
                System.out.flush();
            }
            bytes.writeTo(this.out);
            this.out.flush();
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
    }

    //-----------------------------------------------------------------------
    protected void printList(
        String[] values,
        boolean nested
    ) {
        if(values == null) {
            this.print("NIL");
            return;
        }
        if(nested && values.length > 1) this.print("(");
        for(int i = 0; i < values.length; i++) {
            if(i > 0) this.print(" ");
            try {
                if(values[i].startsWith("\"")) {
                    this.print(MimeUtility.encodeText(values[i], "UTF-8", null));
                }
                else {
                    this.print("\"" + MimeUtility.encodeText(values[i].trim(), "UTF-8", null) + "\"");                
                }
            } 
            catch(UnsupportedEncodingException e) {}
        }
        if(nested && values.length > 1) this.print(")");
    }
    
    //-----------------------------------------------------------------------
    protected void printMessageStructure(
        Message message
    ) throws MessagingException {
        try {
            this.print("(");
            if(message.getContent() instanceof Multipart) {
                Multipart mp = (Multipart)message.getContent();
                for(int i = 0; i < mp.getCount(); i++) {
                    this.printBodyPartStructure(mp.getBodyPart(i));
                }
                if(message.getContentType() == null) {
                    this.print(" NIL");
                }
                else {
                    String[] contentType = new String[0];
                    try {
                        message.getHeader("Content-Type")[0].split(";");
                    } 
                    catch(Exception e) { /* don't care if something is wrong when fetching the content type */ }
                    this.print(" \"mixed\"");
                    if(contentType.length > 1) {
                        this.print(" ");
                        int pos = contentType[1].indexOf("=");
                        this.printList(
                            new String[]{
                                contentType[1].substring(0, pos),
                                contentType[1].substring(pos + 1)                                
                            },
                            true
                        );
                    }
                }
                this.print(" NIL NIL");
            }
            else if(message.getContent() instanceof BodyPart) {
                this.printBodyPartStructure(
                    (BodyPart)message.getContent()
                );
            }
            this.print(")");
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
    }

    //-----------------------------------------------------------------------
    protected void printBodyPartStructure(
        BodyPart part
    ) throws MessagingException {
        this.print("(");
        String[] contentType = part.getContentType().split(";");        
        if(contentType.length > 0) {
            String[] mimeType = contentType[0].split("/");
            this.printList(mimeType, false);
        }
        if(contentType.length > 1) {
            this.print(" ");
            int pos = contentType[1].indexOf("=");
            this.printList(
                new String[]{
                    contentType[1].substring(0, pos),
                    contentType[1].substring(pos + 1)
                },
                true
            ); 
        }
        this.print(" NIL NIL ");
        this.printList(part.getHeader("Content-Transfer-Encoding"), true);
        if(part.getSize() > 0) {
            this.print(" " + part.getSize());
        }
        else {
            this.print(" NIL");
        }
        this.print(" NIL");
        if(part.getDisposition() == null) {
            this.print(" NIL");
        }
        else {
            this.print(" (");
            String[] disposition = part.getHeader("Content-Disposition")[0].split(";");            
            for(int i = 0; i < disposition.length; i++) {
                if(i > 0) this.print(" ");
                int pos = disposition[i].indexOf("=");
                if(pos > 0) {
                    this.printList(
                        new String[]{
                            disposition[i].substring(0, pos),
                            disposition[i].substring(pos + 1)                     
                        }, 
                        true
                    );
                }
                else {
                    this.printList(new String[]{disposition[i]}, true);
                }
            }
            this.print(")");
        }                                           
        this.print(" NIL");
        this.print(")");
    }

    //-----------------------------------------------------------------------
    protected void printAddresses(
        Address[] addresses,
        int max
    ) {
        if(addresses == null) {
            this.print(" ((NIL NIL NIL NIL))");            
        }
        else {
            int n = 0;
            for(Address address: addresses) {
                if(address instanceof InternetAddress) {
                    InternetAddress inetAddress = (InternetAddress)address;
                    int pos = inetAddress.getAddress().indexOf("@");
                    String personalName = inetAddress.getPersonal();
                    if(personalName != null) {
                        if(personalName.startsWith("'")) {
                            personalName = personalName.substring(1);
                        }
                        if(personalName.endsWith("'")) {
                            personalName = personalName.substring(0, personalName.length() - 1);
                        }
                    }
                    if(pos > 0) {
                        this.print(" ((NIL NIL \"" + inetAddress.getAddress().substring(0, pos) + "\" \"" + inetAddress.getAddress().substring(pos + 1) + "\"))");
                    }
                    else {
                        this.print(" ((NIL NIL \"" + inetAddress.getAddress() + "\" NIL))");                        
                    }
                }
                n++;
                if(n >= max) break;
            }
        }
    }
    
    //----------------------------------------------------------------------
    public boolean isConnected(
    ) {
        return (this.socket.isConnected());
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final int MAX_LINE_LENGTH = 2048;
    private static final long FOLDER_REFRESH_PERIOD_MILLIS = 60000L;
    
    public static final int NOT_AUTHENTICATED_STATE = 0;
    public static final int AUTHENTICATED_STATE = 1;
    public static final int SELECTED_STATE = 2;
    public static final int LOGOUT_STATE = 3;
    public static final long MAX_ACTIVITY_NUMBER = 9999999999L;
    
    protected OutputStream out = null;
    protected InputStream in = null;
    protected int state = 0;
    protected IMAPFolderImpl selectedFolder = null;
    protected Map<String,String> availableFolders = new HashMap<String,String>();
    protected long refreshFoldersAt = System.currentTimeMillis();

    private static ThreadLocal<QuotaByteArrayOutputStream> byteOutputStreams = new ThreadLocal<QuotaByteArrayOutputStream>() {
        protected synchronized QuotaByteArrayOutputStream initialValue() {
            return new QuotaByteArrayOutputStream(IMAPSession.class.getName());
        }
    };
    
}

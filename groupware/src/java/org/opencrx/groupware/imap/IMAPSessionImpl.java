/**
 * The ClientHandler class is responsible for replying to client requests.
 */
package org.opencrx.groupware.imap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
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

import org.opencrx.groupware.generic.Util;
import org.opencrx.kernel.generic.SecurityKeys;
import org.openmdx.application.log.AppLog;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.text.format.HexadecimalFormatter;
import org.openmdx.security.authentication1.jmi1.Password;

public class IMAPSessionImpl implements Runnable {
    
    //-----------------------------------------------------------------------
    public IMAPSessionImpl(
        Socket client, 
        IMAPServer server
    ) throws MessagingException {
        this.client = client;
        this.server = server;
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
            line.append(c);
        }
        return line.toString();
    }
    
    //-----------------------------------------------------------------------
    public void run(
    ) {
        if(this.client == null || !this.client.isConnected()) {
            System.out.println("Unable to start conversation, invalid connection passed to client handler");
        } 
        else {
            AppLog.info("Session started for client", this.client.getInetAddress().getHostAddress());
            try {
                this.out = new PrintStream(this.client.getOutputStream());
                this.in = this.client.getInputStream();
                this.println("* OK [CAPABILTY IMAP4rev1] OPENCRX");
                String line = this.readLine();
                Pattern pattern = Pattern.compile("([a-zA-Z0-9]+) ([a-zA-Z0-9]+)(.*)");
                while (line != null) {
                    if((line.indexOf("LOGIN") > 0) && (line.indexOf(" ") > 0)) {
                        System.out.println(">>> IMAPServer[" + this.client.getInetAddress() + "]\n" + line.substring(0, line.lastIndexOf(" ")));
                    }
                    else if(line.indexOf("LOGOUT") > 0) {
                        System.out.println(">>> IMAPServer[" + this.client.getInetAddress() + "]\n" + line + " " + this.username);
                    }
                    if(this.server.isDebug()) {
                        System.out.println(">>> IMAPServer[" + this.client.getInetAddress() + "]\n" + line);
                    }
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.find()) {
                        String tag = matcher.group(1);
                        String command = matcher.group(2);
                        String params = matcher.group(3);
                        if(!this.handleCommand(tag, command, params)) {
                            this.client.close();                            
                            return;
                        }
                    }
                    line = this.readLine();
                }
                AppLog.info("connection closed", client.getInetAddress().getHostAddress());
            } 
            catch (Exception e) {
                if(!(e instanceof SocketTimeoutException)) {
                    new ServiceException(e).log();
                }
            }
            finally {                
                try {
                    // Make sure to close connection when thread terminates. 
                    // Otherwise we may have open connections with no listening threads.
                    this.client.close();
                } catch(Exception e) {}                
            }
        }
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
						println(tag + " NO STATUS failed, no mailbox with that name");
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
                            for(Folder folder: this.server.getFolders(this.username)) {
                                if(
                                    folder.getFullName().equals(query) ||
                                    ((query.endsWith("*") || query.endsWith("%")) && folder.getFullName().startsWith(query.substring(0, query.length()-1)))
                                ) {
                                    this.println("* LIST () \"/\" \"" + folder.getFullName() + "\"");
                                }
    						}
                        }
					}
                    this.println(tag + " OK LIST complete");
				}
				else if(command.equals("LSUB")) {
                    for(Folder folder: this.server.getFolders(this.username)) {
						println("* LSUB () \"/\" \"" + folder.getFullName() + "\"");
					}
					this.println(tag + " OK LSUB complete");
				}
				else if(command.equals("SUBSCRIBE")) {
					this.println(tag + " OK SUBSCRIBE complete");
				}
				else if(command.equals("UNSUBSCRIBE")) {
					this.println(tag + " OK UNSUBSCRIBE complete");
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
        	                        int timeout = 0;
        	                        while((this.in.available() == 0) && (timeout < 10)) {
        	                            if(this.server.isDebug()) {
        	                                System.out.println(i + " bytes. Waiting...");
        	                                Thread.sleep(500);
        	                                timeout++;
        	                            }
        	                        }
        	                        if(this.in.available() == 0) {
        	                            success = false;
        	                            break;
        	                        }
        	                        int c = this.in.read();
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
                        Pattern pattern = Pattern.compile(" ([0-9*\\:]+)(?:,([0-9*\\:]+))* \\((.+)\\)");
                        Matcher matcher = pattern.matcher(params);
                        if(params.equals(" 1,50,100,150")) {
                            boolean stop = true;
                        }
                        if(matcher.find()) {
                            List<int[]> messageNumbers = new ArrayList<int[]>();
                            for(int i = 1; i < matcher.groupCount(); i++) {
                                try {
                                    String number = matcher.group(i);
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
                            String fetchParams = matcher.group(matcher.groupCount());   
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
                            Pattern pattern = Pattern.compile(" ([0-9*\\:]+)(?:,([0-9*\\:]+))* \\((.+)\\)");
                            Matcher matcher = pattern.matcher(params);
                            if(matcher.find()) {
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
                                String fetchParams = matcher.group(matcher.groupCount());
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
        AppLog.detail("Could not", tag + " " + command);
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
    protected String getBodyAsRFC822(
        MimePart part,
        boolean ignoreHeaders
    ) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if(ignoreHeaders) {
                OutputStream os = MimeUtility.encode(bos, part.getEncoding());
                part.getDataHandler().writeTo(os);
                os.flush();           
            }
            else {
                part.writeTo(bos);
                bos.close();
            }
            return bos.toString("US-ASCII");
        } 
        catch (Exception e) {
            new ServiceException(e).log();
        }
        return "";
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
            String messageRFC822 = this.getBodyAsRFC822(message, false);
            this.println("RFC822 {" + (messageRFC822.length() + 2) + "}");
            this.println(messageRFC822);
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
            String messageRFC822 = this.getBodyAsRFC822(message, false);            
            this.print("RFC822.SIZE " + messageRFC822.length());
            return true;
        } 
        else if(command.equals("UID")) {
            if(count > 0) this.print(" ");
            this.print("UID " + this.selectedFolder.getUID(message));
            return true;
        } 
        else if(command.equals("BODY[]") || command.equals("BODY.PEEK[]")) {
            if(count > 0) this.print(" ");
            String messageRFC822 = this.getBodyAsRFC822(message, false);
            this.println("BODY[] {" + (messageRFC822.length() + 2) + "}");
            this.println(messageRFC822);
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
                            String temp = this.getBodyAsRFC822(
                                (MimePart)((MimeMultipart)message.getContent()).getBodyPart(partId - 1),
                                true
                            );
                            if(count > 0) this.print(" ");
                            this.println("BODY[" + partId + "] {" + (temp.length() + 2) + "}");
                            this.println(temp);
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
            String messageRFC822 = this.getBodyAsRFC822(message, false);
            this.println("BODY[TEXT] {" + (messageRFC822.length() + 2) + "}");
            this.println(messageRFC822);
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
            String messageRFC822 = this.getBodyAsRFC822(message, false);
            this.println("BODY[TEXT] {" + (messageRFC822.length() + 2) + "}");
            this.println(messageRFC822);
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
    private IMAPFolderImpl getFolder(
        String name
    ) throws MessagingException {
        for(IMAPFolderImpl folder: this.server.getFolders(this.username)) {
            if(folder.getFullName().equalsIgnoreCase(name)) { 
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
            this.out.print(s);
            this.out.print("\r\n");
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
            this.out.print(s);
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
            if(i > 0) print(" ");
            try {
                if(values[i].startsWith("\"")) {
                    this.print(MimeUtility.encodeText(values[i], "UTF-8", null));
                }
                else {
                    this.print("\"" + MimeUtility.encodeText(values[i].trim(), "UTF-8", null) + "\"");                
                }
            } catch(UnsupportedEncodingException e) {}
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
                    } catch(Exception e) { /* don't care if something is wrong when fetching the content type */ }
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
    
    //-----------------------------------------------------------------------
    private boolean login(
        String username,
        String password
    ) {
        try {
            if(username.indexOf("@") > 0) {
                this.segmentName = username.substring(username.indexOf("@") + 1);
                this.username = username;
                String principalName = username.substring(0, username.indexOf("@"));            
                PersistenceManager pm = this.server.getPersistenceManagerFactory().getPersistenceManager(
                    SecurityKeys.ROOT_PRINCIPAL,
                    UUIDs.getGenerator().next().toString()
                );                
                org.opencrx.security.realm1.jmi1.Principal principal = 
                    (org.opencrx.security.realm1.jmi1.Principal)pm.getObjectById(
                        "xri:@openmdx:org.openmdx.security.realm1/provider/" + this.server.getProviderName() + "/segment/Root/realm/Default/principal/" + principalName
                    );
                if(principal != null) {
                    org.openmdx.security.realm1.jmi1.Credential credential = principal.getCredential();                
                    if(credential instanceof Password) {
                        boolean success = Util.getPasswordDigest(password, PASSWORD_ENCODING_ALGORITHM).equals(
                            "{" + PASSWORD_ENCODING_ALGORITHM + "}" + credential.refGetValue("password")
                        );
                        return success;
                    }
                }
            }
        }
        catch(Exception e) {
            new ServiceException(e).log();
        }
        return false;
    }
    
    //----------------------------------------------------------------------
    public boolean isConnected(
    ) {
        return (this.client.isConnected());
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final int NOT_AUTHENTICATED_STATE = 0;
    public static final int AUTHENTICATED_STATE = 1;
    public static final int SELECTED_STATE = 2;
    public static final int LOGOUT_STATE = 3;
    public static final String PASSWORD_ENCODING_ALGORITHM = "MD5";
    public static final long MAX_ACTIVITY_NUMBER = 9999999999L;
    
    protected final Socket client;
    protected final IMAPServer server;
    protected PrintStream out = null;
    protected InputStream in = null;
    protected int state = 0;
    protected IMAPFolderImpl selectedFolder = null;
    protected String username = null;
    protected String segmentName = null;

}

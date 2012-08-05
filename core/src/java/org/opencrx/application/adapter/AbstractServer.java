/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AbstractServer.java,v 1.5 2010/04/22 09:38:09 wfro Exp $
 * Description: AbstractServer
 * Revision:    $Revision: 1.5 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/04/22 09:38:09 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.PersistenceManagerFactory;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.openmdx.base.exception.ServiceException;

public abstract class AbstractServer implements Runnable {
    
	//-----------------------------------------------------------------------
	protected AbstractServer(
		String serverName,
	    PersistenceManagerFactory pmf,
	    String providerName,
	    String bindAddress,
	    int portNumber,
	    String sslKeystoreFile,
	    String sslKeystoreType,
	    String sslKeystorePass,
	    String sslKeyPass,
	    boolean isDebug,
	    int delayOnStartup
	) {
		this.serverName = serverName;
	    this.pmf = pmf;
	    this.bindAddress = bindAddress;
		this.portNumber = portNumber;
		this.sslKeystoreFile = sslKeystoreFile;
		this.sslKeystoreType = sslKeystoreType;
		this.sslKeystorePass = sslKeystorePass;
		this.sslKeyPass = sslKeyPass;
		this.providerName = providerName;
		this.isDebug = isDebug;
		this.delayOnStartup = delayOnStartup;
	}

    //-----------------------------------------------------------------------
	public abstract AbstractSession newSession(
		Socket socket,
		AbstractServer server
	);
	
    //-----------------------------------------------------------------------
    public boolean bind(
    ) throws ServiceException {
    	boolean isSsl = false;
        ServerSocketFactory serverSocketFactory;
        if (this.sslKeystoreFile == null || this.sslKeystoreFile.length() == 0) {
            serverSocketFactory = ServerSocketFactory.getDefault();
        } 
        else {
        	isSsl = true;
            FileInputStream keyStoreInputStream = null;
            try {
                keyStoreInputStream = new FileInputStream(this.sslKeystoreFile);
                KeyStore keystore = KeyStore.getInstance(this.sslKeystoreType);
                keystore.load(
                	keyStoreInputStream,
                    this.sslKeystorePass.toCharArray()
                );
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                	KeyManagerFactory.getDefaultAlgorithm()
                );
                kmf.init(
                	keystore, 
                	this.sslKeyPass.toCharArray()
                );
                SSLContext sslContext = SSLContext.getInstance("SSLv3");
                sslContext.init(
                	kmf.getKeyManagers(), 
                	null, 
                	null
                );
                serverSocketFactory = sslContext.getServerSocketFactory();
            } 
            catch (IOException e) {
                throw new ServiceException(e);
            } 
            catch (GeneralSecurityException e) {
            	throw new ServiceException(e);
            } 
            finally {
                if (keyStoreInputStream != null) {
                    try {
                        keyStoreInputStream.close();
                    } 
                    catch (IOException exc) {}
                }
            }
        }
        try {
            // create the server socket
            if(this.bindAddress == null || this.bindAddress.length() == 0) {
                this.serverSocket = serverSocketFactory.createServerSocket(this.portNumber);
            } 
            else {
                this.serverSocket = serverSocketFactory.createServerSocket(
                	this.portNumber, 
                	0, 
                	Inet4Address.getByName(this.bindAddress)
                );
            }
        } 
        catch (IOException e) {
        	throw new ServiceException(e);
        }
        return isSsl;
    }
	
    //-----------------------------------------------------------------------
	public void run(
	) {
		try {
			Thread.sleep(this.delayOnStartup * 1000L);
		} catch(Exception e) {}		
        while(true) {
        	if(this.serverSocket == null) {
        		try {
        			Thread.sleep(5000L);
        		} catch(Exception e) {}
        	}
        	else {
        		try {
	                Socket socket = this.serverSocket.accept();
	                socket.setSoTimeout(30000);
	                AbstractSession session = newSession(socket, this);
	                Thread clientHandler = new Thread(session);
	                clientHandler.start();
	                for(Iterator<Entry<Thread,AbstractSession>> i = this.sessions.entrySet().iterator(); i.hasNext(); ) {
	                	Entry<Thread,AbstractSession> e = i.next();
	                	if(!e.getKey().isAlive()) {
	                		i.remove();
	                	}
	                }
	                this.sessions.put(
	                	clientHandler,
	                	session
	                );
        		} catch(Exception e) {
        			new ServiceException(e).log();
            		try {
            			Thread.sleep(5000L);
            		} catch(Exception e1) {}
        		}
        	}
        }
	}

    //-----------------------------------------------------------------------
	public void pause(
	) {
		// Close server socket
		try {
			ServerSocket serverSocket = this.serverSocket;
			this.serverSocket = null;
			if(serverSocket != null) {
				serverSocket.close();
			}
		}
		catch(IOException e) {}			
        System.out.println(this.serverName + " " + this.providerName + " stopped listening on port " + this.portNumber);            
		// Stop all sessions
		for(AbstractSession session: this.sessions.values()) {
			session.stop();
		}
		this.sessions.clear();
	}
	
    //-----------------------------------------------------------------------
	public void resume(
	) {
		boolean isSsl = false;
		try {
			
			if(this.serverSocket == null || this.serverSocket.isClosed()) {
				this.sessions.clear();
				isSsl = this.bind();		
				System.out.println(this.serverName + " " + this.providerName + " is listening on " + (isSsl ? "SSL " : "") + "port " + this.portNumber);
			}
		}
		catch(Exception e) {
			new ServiceException(e).log();
	        System.out.println(this.serverName + " " + this.providerName + " bind failed for " + (isSsl ? "SSL " : "") + "port " + this.portNumber + ". See log for more information.");            			
		}
	}
	
    //-----------------------------------------------------------------------
	public String getProviderName(
	) {
	    return this.providerName;
	}
	
    //-----------------------------------------------------------------------
    public boolean isDebug(
    ) {
        return this.isDebug;
    }
    
    //-----------------------------------------------------------------------
	public PersistenceManagerFactory getPersistenceManagerFactory(
	) {
	    return this.pmf;
	}
	
    //-----------------------------------------------------------------------
	// Members
    //-----------------------------------------------------------------------
	protected final String serverName;
    protected final String bindAddress;
	protected final String sslKeystoreFile;
	protected final String sslKeystoreType;
	protected final String sslKeystorePass;	
	protected final String sslKeyPass;
    protected final int portNumber;
    protected final String providerName;
    protected final boolean isDebug;
    protected final int delayOnStartup;
    protected final Map<Thread,AbstractSession> sessions = new ConcurrentHashMap<Thread,AbstractSession>();
    protected ServerSocket serverSocket = null;
    protected final PersistenceManagerFactory pmf;
    
}

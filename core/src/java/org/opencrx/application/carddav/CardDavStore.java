/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: CalDavStore
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2010, CRIXP Corp., Switzerland
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
package org.opencrx.application.carddav;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.application.uses.net.sf.webdav.Lock;
import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.application.uses.net.sf.webdav.WebDavStore;
import org.opencrx.application.uses.net.sf.webdav.exceptions.LockFailedException;
import org.opencrx.application.uses.net.sf.webdav.exceptions.WebdavException;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.jmi1.AbstractGroup;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.backend.Accounts;
import org.opencrx.kernel.backend.Base;
import org.opencrx.kernel.backend.VCard;
import org.opencrx.kernel.home1.cci2.CardProfileQuery;
import org.opencrx.kernel.home1.jmi1.CardProfile;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.opencrx.kernel.home1.jmi1.UserHome;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.log.SysLog;
import org.w3c.cci2.BinaryLargeObject;
import org.w3c.cci2.BinaryLargeObjects;

public class CardDavStore implements WebDavStore {

	//-----------------------------------------------------------------------
	public CardDavStore(
		PersistenceManagerFactory pmf
	) {
		this.pmf = pmf;
	}
	
	//-----------------------------------------------------------------------
    public static PersistenceManager getPersistenceManager(
        HttpServletRequest req,
        PersistenceManagerFactory pmf
    ) {
        return req.getUserPrincipal() == null ?
            null :
            pmf.getPersistenceManager(
                req.getUserPrincipal().getName(),
                null
            );
    }
	
	//-----------------------------------------------------------------------
	@Override
	public RequestContext begin(
    	HttpServletRequest req,
    	HttpServletResponse resp		
	) {
		CardDavRequestContext requestContext = new CardDavRequestContext(req, resp, this.pmf);
		return requestContext;
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void commit(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((CardDavRequestContext)requestContext).getPersistenceManager();
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().commit();
				pm.close();
			}
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception e0) {}
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void createCollection(
		RequestContext requestContext, 
		String path
	) {
		throw new WebdavException("Not supported by CalDAV");
	}
	
	//-----------------------------------------------------------------------
	@Override
	public Collection<Resource> getChildren(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof CardDavResource) {
			return ((CardDavResource)res).getChildren();
		} else {
			return Collections.emptyList();
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
	public BinaryLargeObject getResourceContent(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof CardDavResource) {
			return ((CardDavResource)res).getContent();
		} else {
			return BinaryLargeObjects.valueOf(new byte[]{});
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Path is of the form:
	 * - Format 1: {provider.id} "/" {segment.id} "/user/" {user.id} "/profile/" {profile.name}
	 * - Format 2: {provider.id} "/" {segment.id} "/" {user.id} "/" {profile.id} "/" {feed.id} "/" {account.id}
	 */
	@Override
	public Resource getResourceByPath(
		RequestContext requestContext, 
		String path
	) {
		PersistenceManager pm = ((CardDavRequestContext)requestContext).getPersistenceManager();
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] components = path.split("/");
		// Strip extra components (sent by iOS)
		{
			int posStripped = -1;
			int pos = 0;
			for(String component: components) {
				if(component.startsWith(":") || ".well-known".equals(component)) {
					posStripped = pos;
					break;
				}
				pos++;
			}
			if(posStripped >= 0) {
				List<String> strippedComponents = new ArrayList<String>(Arrays.asList(components));
				components = strippedComponents.subList(0, posStripped).toArray(new String[posStripped]);
			}
		}
		// Format 1
		if(components.length == 6 && "user".equals(components[2]) && "profile".equals(components[4])) {
    		UserHome userHome = (UserHome)pm.getObjectById(
    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", components[3])
    		);
        	CardProfile syncProfile = null;
        	CardProfileQuery cardProfileQuery = (CardProfileQuery)pm.newQuery(CardProfile.class);
        	cardProfileQuery.name().equalTo(components[5]);
        	List<CardProfile> cardProfiles = userHome.getSyncProfile(cardProfileQuery);
        	if(!cardProfiles.isEmpty()) {
        		syncProfile = cardProfiles.iterator().next();
	    		return new CardProfileResource(
	    			requestContext, 
	    			syncProfile
	    		);
        	}
		}
		// Format 2
		else if(components.length >= 3) {
			// UserHome
			if(components.length == 3) {
				UserHome userHome = (UserHome)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", components[2])
	    		);
	    		return new UserHomeResource(
	    			requestContext, 
	    			userHome
	    		);
			}	
			// CardProfile
			else if(components.length == 4) {
	    		CardProfile cardProfile = (CardProfile)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", components[2], "syncProfile", components[3])
	    		);
	    		return new CardProfileResource(
	    			requestContext, 
	    			cardProfile
	    		);
			}
			// SyncFeed
			else if(components.length == 5) {
				String id = components[4];
				ContactsFeed contactsFeed = (ContactsFeed)pm.getObjectById(
	    			new Path("xri://@openmdx*org.opencrx.kernel.home1").getDescendant("provider", components[0], "segment", components[1], "userHome", components[2], "syncProfile", components[3], "feed", id)
	    		);
	    		return new AccountCollectionResource(
	    			requestContext,
	    			contactsFeed
	    		);
			}
			// Account
			else if(components.length == 6) {
				AccountCollectionResource parent = (AccountCollectionResource)this.getResourceByPath(
		   			requestContext, 
		   			components[0] + "/" + components[1] + "/" + components[2] + "/" + components[3] + "/" + components[4]
		   		);
				String id = components[5];
				if(id.endsWith(".vcf")) {
					id = id.substring(0, id.indexOf(".vcf"));
				}
	    		if(this.uidMapping.containsKey(id)) {
	    			String newId = this.uidMapping.get(id);
	    			// old -> new mapping only available for one request
	    			this.uidMapping.remove(id);
	    			id = newId;
	    		}			
				// Get account
	    		try {
		    		Account account = (Account)pm.getObjectById(
		    			new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant("provider", components[0], "segment", components[1], "account", id)
		    		);
		    		return new AccountResource(
		    			requestContext, 
		    			account,
		    			parent
		    		);
	    		} catch(Exception e) {
	    			ServiceException e0 = new ServiceException(e);
					SysLog.detail(e0.getMessage(), e0.getCause());
	    		}
			}
		}
		return null;		
	}
	
	//-----------------------------------------------------------------------
	@Override
	public void removeResource(
		RequestContext requestContext, 
		Resource res
	) {
		if(res instanceof AccountResource) {
			PersistenceManager pm = ((CardDavRequestContext)requestContext).getPersistenceManager();
			Account account = ((AccountResource)res).getObject();
			MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
			query.thereExistsAccount().equalTo(account);
			List<Member> members = ((AccountResource)res).getAccountCollectionResource().getObject().getAccountGroup().getMember(query);
			pm.currentTransaction().begin();
			for(Member member: members) {
				member.setDisabled(true);
			}
			pm.currentTransaction().commit();
		}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public MoveResourceStatus moveResource(
    	RequestContext requestContext, 
    	Resource res, 
    	String sourcePath,
    	String destinationPath
    ) {
		throw new WebdavException("Not supported by CardDAV");	    
    }
		
	//-----------------------------------------------------------------------
	@Override
	public void rollback(
		RequestContext requestContext
	) {
		PersistenceManager pm = ((CardDavRequestContext)requestContext).getPersistenceManager();		
		try {
			if(!pm.isClosed()) {
				pm.currentTransaction().rollback();
				pm.close();
			}
		} catch(Exception e) {}
	}
	
	//-----------------------------------------------------------------------
	@Override
    public String getMimeType(
    	Resource res
    ) {
		if(res instanceof CardDavResource) {
			return ((CardDavResource)res).getMimeType();
		} else {
			return "text/xml";
		}
    }
	
	//-----------------------------------------------------------------------	
    protected String getParentPath(
    	String path
    ) {
        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            return path.substring(0, slash);
        }
        return null;
    }
	
	//-----------------------------------------------------------------------
	@Override
    public PutResourceStatus putResource(
    	RequestContext requestContext, 
    	String path, 
    	InputStream content, 
    	String contentType 
    ) {
		String parentPath = this.getParentPath(path); 
		Resource parent = this.getResourceByPath(
			requestContext, 
			parentPath
		);
		if(parent instanceof AccountCollectionResource) {			
	    	try {
		    	BufferedReader reader = new BufferedReader(
		    		new InputStreamReader(content, "UTF-8")
		    	);
		    	// Create/Update account
		    	ContactsFeed feed = ((AccountCollectionResource)parent).getObject();
		    	if(feed.isAllowChange()) {
			    	AbstractGroup group = feed.getAccountGroup();		    	
			    	PersistenceManager pm = JDOHelper.getPersistenceManager(group);
			    	org.opencrx.kernel.account1.jmi1.Segment accountSegment =
			    		(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
			    			group.refGetPath().getParent().getParent()
			    		);
		        	VCard.PutVCardResult result = VCard.getInstance().putVCard(
		        		reader, 
		        		accountSegment
		        	);
		        	// Create membership
		        	if(result.getAccount() != null) {
		        		MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
		        		query.thereExistsAccount().equalTo(result.getAccount());
		        		List<Member> members = group.getMember(query);
		        		if(members.isEmpty()) {
		        			boolean isTxLocal = !pm.currentTransaction().isActive();
		        			if(isTxLocal) {
		        				pm.currentTransaction().begin();
		        			}
		        			Member member = pm.newInstance(Member.class);
							member.setName(result.getAccount().getFullName());
							member.setAccount(result.getAccount());
							member.setQuality(Accounts.MEMBER_QUALITY_NORMAL);
							group.addMember(
								Base.getInstance().getUidAsString(), 
								member
							);
							if(isTxLocal) {
								pm.currentTransaction().commit();
							}
		        		}
	 	        	}
		            if(result.getOldUID() != null && result.getAccount() != null) {
		            	this.uidMapping.put(
		            		result.getOldUID(), 
		            		result.getAccount().refGetPath().getBase()
		            	);
		            }
		        	return result.getStatus() == VCard.PutVCardResult.Status.CREATED ? 
		        		PutResourceStatus.CREATED : 
		        			PutResourceStatus.UPDATED;
		    	} else {
		    		return PutResourceStatus.FORBIDDEN;
		    	}
	    	}
	    	catch(Exception e) {
	    		new ServiceException(e).log();
	    	}
		}
	    return null;
    }

	//-----------------------------------------------------------------------
	@Override
    public List<Lock> getLocksByPath(
    	RequestContext requestContext, 
    	String path
    ) {
		return Collections.emptyList();
    }

	//-----------------------------------------------------------------------
	@Override
    public Lock lock(
    	RequestContext requestContext, 
    	String path, 
    	String owner, 
    	String scope, 
    	String type, 
    	int depth, 
    	int timeout
    ) throws LockFailedException {
	    return null;
    }

	//-----------------------------------------------------------------------
	@Override
    public void setLockTimeout(
    	RequestContext requestContext, 
    	String id, 
    	int timeout
    ) {	    
    }

	//-----------------------------------------------------------------------
	@Override
    public boolean unlock(
    	RequestContext requestContext, 
    	String id
    ) {
	    return false;
    }

	//-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------			
	protected PersistenceManagerFactory pmf = null;
    protected final Map<String,String> uidMapping = new ConcurrentHashMap<String,String>();
	
}
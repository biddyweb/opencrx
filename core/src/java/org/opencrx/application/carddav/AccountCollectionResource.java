/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: AccountCollectionResource.java,v 1.2 2010/11/24 14:34:24 wfro Exp $
 * Description: AccountCollectionResource
 * Revision:    $Revision: 1.2 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2010/11/24 14:34:24 $
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
package org.opencrx.application.carddav;

import java.util.Collection;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.application.uses.net.sf.webdav.RequestContext;
import org.opencrx.application.uses.net.sf.webdav.Resource;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.backend.ICalendar;
import org.opencrx.kernel.home1.jmi1.ContactsFeed;
import org.openmdx.base.collection.MarshallingCollection;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.marshalling.Marshaller;

public class AccountCollectionResource extends CardDavResource {
	
	//-----------------------------------------------------------------------
	static class AccountResourceCollection<T> extends MarshallingCollection<T> {
		
		public AccountResourceCollection(
			final RequestContext requestContext,
			Collection<Member> members,
			final AccountCollectionResource accountCollectionResource
		) {
			super(
				new Marshaller(){

					@Override
                    public Object marshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof Member) {
							return new AccountResource(
								requestContext,
								((Member)source).getAccount(),
								accountCollectionResource
							);
						} else {
							return source;
						}
                    }

					@Override
                    public Object unmarshal(
                    	Object source
                    ) throws ServiceException {
						if(source instanceof CardDavResource) {
							return ((CardDavResource)source).getObject();
						}
						else {
							return source;
						}
                    }
					
				},
				members
			);
		}
		
        private static final long serialVersionUID = 6257982279508324945L;

	}
	
	//-----------------------------------------------------------------------
	public AccountCollectionResource(
		RequestContext requestContext,
		ContactsFeed contactsFeed
	) {
		super(
			requestContext,
			contactsFeed
		);
	}

	//-----------------------------------------------------------------------
	@Override
    public ContactsFeed getObject(
    ) {
        return (ContactsFeed)super.getObject();
    }

	//-----------------------------------------------------------------------
    public String getDisplayName(
    ) {
    	Set<String> features = this.getObject().refDefaultFetchGroup();
    	String name = this.getName();
    	if(features.contains("name")) {
    		name = (String)this.getObject().refGetValue("name");
    	}
    	return name;
    }
    		
	//-----------------------------------------------------------------------
	@Override
    public boolean isCollection(
    ) {
		return true;
    }
	
	//-----------------------------------------------------------------------
    @Override
    public String getName(
    ) {
    	return super.getName();
    }

	//-----------------------------------------------------------------------
	@Override
    public String getMimeType(
    ) {
		return ICalendar.MIME_TYPE;
    }

	//-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
	public Collection<Resource> getChildren(
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(this.getObject());
        MemberQuery query = (MemberQuery)pm.newQuery(Member.class);
        query.forAllDisabled().isFalse();                    
        query.thereExistsAccount().vcard().isNonNull();
        query.orderByCreatedAt().ascending();
        return new AccountResourceCollection(
        	this.getRequestContext(),
        	this.getObject().getAccountGroup().getMember(query),
        	this
        );
	}
	
	//-----------------------------------------------------------------------
    // Members
	//-----------------------------------------------------------------------
	
}
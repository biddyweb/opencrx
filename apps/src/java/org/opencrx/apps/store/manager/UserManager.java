/*
 * ====================================================================
 * Project:     openCRX/Apps, http://www.opencrx.org/
 * Name:        $Id: UserManager.java,v 1.4 2009/05/22 14:18:43 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/22 14:18:43 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2007-2009, CRIXP Corp., Switzerland
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
package org.opencrx.apps.store.manager;

import java.util.List;

import javax.jdo.Transaction;

import org.opencrx.apps.store.common.PrimaryKey;
import org.opencrx.apps.store.objects.User;
import org.opencrx.apps.utils.ApplicationContext;
import org.opencrx.kernel.account1.cci2.ContactQuery;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.openmdx.base.exception.ServiceException;

public final class UserManager
{
    //-----------------------------------------------------------------------
    public UserManager(
        ApplicationContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final boolean create(
        final User newValue
    ) {
    	Transaction tx = null;
        try {
            tx = this.context.getPersistenceManager().currentTransaction();       
            tx.begin();
            Contact contact = this.context.getPersistenceManager().newInstance(Contact.class);
            contact.refInitialize(false, false);
            newValue.update(
                contact, 
                this.context
            );
            this.context.getAccountSegment().addAccount(
                false,
                newValue.getKey().getUuid(),
                contact
            );
            tx.commit();
            return true;
        }
        catch(Exception e) {
        	if(tx != null) {
        		try {
        			tx.rollback();
        		}
        		catch(Exception e0) {}
        	}
            new ServiceException(e).log();
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final void delete(
        final PrimaryKey key
    ) {
    	Transaction tx = null;
    	try {
	        tx = this.context.getPersistenceManager().currentTransaction();       
	        tx.begin();
	        Contact contact = (Contact)this.context.getAccountSegment().getAccount(key.getUuid());
	        contact.refDelete();
	        tx.commit();
    	}
    	catch(Exception e) {
        	if(tx != null) {
        		try {
        			tx.rollback();
        		}
        		catch(Exception e0) {}
        	}
            new ServiceException(e).log();    		
    	}
    }

    //-----------------------------------------------------------------------
    public final User get(
        final PrimaryKey key
    ) {
        if(key.toString().length() > 0) {
            Contact contact = (Contact)this.context.getAccountSegment().getAccount(key.getUuid());
            return new User(contact);
        }
        else {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    public final User get(
        final String key
    ) {
        Contact contact = (Contact)this.context.getAccountSegment().getAccount(key);
        return new User(contact);
    }

    //-----------------------------------------------------------------------
    public final User update(
        final User newValue
    ) {
    	Transaction tx = null;
    	try {
	        tx = this.context.getPersistenceManager().currentTransaction();       
	        tx.begin();
	        Contact contact = (Contact)this.context.getAccountSegment().getAccount(newValue.getKey().getUuid());
	        newValue.update(
	            contact,
	            this.context
	        );
	        tx.commit();
	        return this.get(newValue.getKey());
    	}
    	catch(Exception e) {
        	if(tx != null) {
        		try {
        			tx.rollback();
        		}
        		catch(Exception e0) {}
        	}
            new ServiceException(e).log();
            return null;
    	}
    }

    //-----------------------------------------------------------------------
    public final boolean login(
        final String userName, 
        final String password
    ) {
        final User user = this.getUserByName(userName);
        // no user found by user name
        if (null == user)
            return false;

        // user found, match password
        if (user.getPassword().equals(password)) {
            return true;
        }
        else {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final User getUserByName(
        final String name
    ) {
        ContactQuery query = (ContactQuery)this.context.getPersistenceManager().newQuery(Contact.class);
        query.thereExistsAliasName().like(name);
        List<Contact> contacts = this.context.getAccountSegment().getAccount(query);
        if(!contacts.isEmpty()) {
            return new User(
                contacts.iterator().next()
            );
        }
        else {
            return null;
        }
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private final ApplicationContext context;
    
}

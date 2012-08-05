/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: UserManager.java,v 1.18 2008/11/08 00:21:54 wfro Exp $
 * Description: ProductManager
 * Revision:    $Revision: 1.18 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/08 00:21:54 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2007, CRIXP Corp., Switzerland
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
package org.opencrx.store.manager;

import java.util.Collection;

import javax.jdo.Transaction;

import org.opencrx.store.common.PrimaryKey;
import org.opencrx.store.common.util.OpenCrxContext;
import org.opencrx.store.objects.User;
import org.openmdx.base.exception.ServiceException;

/**
 * Manager for user subsystem
 * 
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class UserManager
{
    //-----------------------------------------------------------------------
    public UserManager(
        OpenCrxContext context
    ) {
        this.context = context;
    }
    
    //-----------------------------------------------------------------------
    public final boolean create(
        final User newValue
    ) {
        try {
            Transaction tx = this.context.getPersistenceManager().currentTransaction();       
            tx.begin();
            org.opencrx.kernel.account1.jmi1.Contact contact = 
                this.context.getAccountPackage().getContact().createContact();
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
            new ServiceException(e).log();
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final void delete(
        final PrimaryKey key
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.account1.jmi1.Contact contact = 
            (org.opencrx.kernel.account1.jmi1.Contact)this.context.getAccountSegment().getAccount(key.getUuid());
        contact.refDelete();
        tx.commit();
    }

    //-----------------------------------------------------------------------
    public final User get(
        final PrimaryKey key
    ) {
        if(key.toString().length() > 0) {
            org.opencrx.kernel.account1.jmi1.Contact contact = 
                (org.opencrx.kernel.account1.jmi1.Contact)this.context.getAccountSegment().getAccount(key.getUuid());
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
        org.opencrx.kernel.account1.jmi1.Contact contact = 
            (org.opencrx.kernel.account1.jmi1.Contact)this.context.getAccountSegment().getAccount(key);
        return new User(contact);
    }

    //-----------------------------------------------------------------------
    public final User update(
        final User newValue
    ) {
        Transaction tx = this.context.getPersistenceManager().currentTransaction();       
        tx.begin();
        org.opencrx.kernel.account1.jmi1.Contact contact = 
            (org.opencrx.kernel.account1.jmi1.Contact)this.context.getAccountSegment().getAccount(newValue.getKey().getUuid());
        newValue.update(
            contact,
            this.context
        );
        tx.commit();
        return this.get(newValue.getKey());
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
        if (user.getPassword().equals(password))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public final User getUserByName(
        final String name
    ) {
        org.opencrx.kernel.account1.cci2.ContactQuery query =
            this.context.getAccountPackage().createContactQuery();
        query.thereExistsAliasName().like(name);
        Collection contacts = this.context.getAccountSegment().getAccount(query);
        if(!contacts.isEmpty()) {
            return new User(
                (org.opencrx.kernel.account1.jmi1.Contact)contacts.iterator().next()
            );
        }
        else {
            return null;
        }
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final int STATUS_BUYING = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_CANCELLED = 3;

    private final OpenCrxContext context;
    
}

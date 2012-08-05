/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: UserHomeImpl.java,v 1.6 2008/11/26 09:40:42 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.6 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/11/26 09:40:42 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2007, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.home1.aop2;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.listener.StoreCallback;

import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.jmi1.Home1Package;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasePackage;

public class UserHomeImpl implements StoreCallback {

    //-----------------------------------------------------------------------
    public UserHomeImpl(
        org.opencrx.kernel.home1.jmi1.UserHome same,
        org.opencrx.kernel.home1.cci2.UserHome next
    ) {
        this.same = same;
        this.next = next;
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void refreshItems(
    ) {
        try {
            PersistenceManager pm = JDOHelper.getPersistenceManager(this.same);
            UserHomes.refreshItems(
                this.same,
                pm
            );
            return ((BasePackage)this.same.refOutermostPackage().refPackage(BasePackage.class.getName())).createVoid();            
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }            
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.SearchResult searchBasic(
        org.opencrx.kernel.home1.jmi1.SearchBasicParams params
    ) {
        try {
            PersistenceManager pm = JDOHelper.getPersistenceManager(this.same);
            ObjectFinder objectFinder = UserHomes.searchBasic(
                this.same,
                params.getSearchExpression(),
                pm
            );
            return ((Home1Package)this.same.refOutermostPackage().refPackage(Home1Package.class.getName())).createSearchResult(
                objectFinder
            );   
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }              
    }
    
    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.SearchResult searchAdvanced(
        org.opencrx.kernel.home1.jmi1.SearchAdvancedParams params
    ) {
        try {
            PersistenceManager pm = JDOHelper.getPersistenceManager(this.same);
            ObjectFinder objectFinder = UserHomes.searchAdvanced(
                this.same,
                params.getAllWords(),
                params.getAtLeastOneOfTheWords(),
                params.getWithoutWords(),
                pm
            );
            return ((Home1Package)this.same.refOutermostPackage().refPackage(Home1Package.class.getName())).createSearchResult(
                objectFinder
            );   
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }              
    }
    
    //-----------------------------------------------------------------------
    public void jdoPreStore(
    ) {
        boolean inCallback = true;
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    protected final org.opencrx.kernel.home1.jmi1.UserHome same;
    protected final org.opencrx.kernel.home1.cci2.UserHome next;
    
}

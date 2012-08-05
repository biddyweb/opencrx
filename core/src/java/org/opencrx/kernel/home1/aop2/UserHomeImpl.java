/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: UserHomeImpl.java,v 1.17 2009/04/21 00:10:36 wfro Exp $
 * Description: openCRX application plugin
 * Revision:    $Revision: 1.17 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/04/21 00:10:36 $
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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.opencrx.kernel.backend.UserHomes;
import org.opencrx.kernel.home1.jmi1.ObjectFinder;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.accessor.jmi.cci.JmiServiceException;
import org.openmdx.base.aop2.AbstractObject;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.persistence.cci.UserObjects;
import org.w3c.cci2.BinaryLargeObjects;

public class UserHomeImpl
	<S extends org.opencrx.kernel.home1.jmi1.UserHome,N extends org.opencrx.kernel.home1.cci2.UserHome,C extends UserHomeImpl.DerivedAttributes>
	extends AbstractObject<S,N,C> {

    //-----------------------------------------------------------------------
	public static class DerivedAttributes {
		
		public DerivedAttributes(
			byte[][] charts,
			String[] chartNames,
			String[] chartDescriptions,
			String[] chartMimeTypes
		) {
			this.charts = charts;
			this.chartNames = chartNames;
			this.chartDescriptions = chartDescriptions;
			this.chartMimeTypes = chartDescriptions;
		}
		
		public byte[][] charts;
		public String[] chartNames;
		public String[] chartDescriptions;
		public String[] chartMimeTypes;
		
	}
	
    //-----------------------------------------------------------------------
    public UserHomeImpl(
        S same,
        N next
    ) {
    	super(same, next);
    }

    //-----------------------------------------------------------------------
    public org.openmdx.base.jmi1.Void refreshItems(
    ) {
        try {
            UserHomes.getInstance().refreshItems(
                this.sameObject()
            );
            return super.newVoid();            
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
            ObjectFinder objectFinder = UserHomes.getInstance().searchBasic(
                this.sameObject(),
                params.getSearchExpression()
            );
            return Utils.getHomePackage(this.sameManager()).createSearchResult(
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
            ObjectFinder objectFinder = UserHomes.getInstance().searchAdvanced(
                this.sameObject(),
                params.getAllWords(),
                params.getAtLeastOneOfTheWords(),
                params.getWithoutWords()
            );
            return Utils.getHomePackage(this.sameManager()).createSearchResult(
                objectFinder
            );   
        }
        catch(ServiceException e) {
            throw new JmiServiceException(e);
        }              
    }

    //-----------------------------------------------------------------------
    public org.opencrx.kernel.home1.jmi1.ChangePasswordResult changePassword(
        org.opencrx.kernel.home1.jmi1.ChangePasswordParams params
    ) {
    	try {
	    	List<String> principalChain = UserObjects.getPrincipalChain(this.sameManager());
	        String requestingPrincipalName = !principalChain.isEmpty() ? 
	        	principalChain.get(0) : 
	        	"guest";
	        // make sure that the requesting principal changes the password of its
	        // own user home (qualifier of user home matches the principal). If yes,
	        // execute changePassword as segment administrator. If not, execute it as
	        // requesting principal. In this case the principal must have enough permissions
	        // to create a password credential and update the principal.
	        boolean requestingPrincipalOwnsUserHome = this.sameObject().refGetPath().getBase().equals(requestingPrincipalName);
	        short status = UserHomes.getInstance().changePassword(
	            this.sameObject(),
	            requestingPrincipalOwnsUserHome,
	            params.getOldPassword(),
	            params.getNewPassword(),
	            params.getNewPasswordVerification()
	        );
	        return Utils.getHomePackage(this.sameManager()).createChangePasswordResult(
	            status
	        );
    	}
    	catch(Exception e) {
    		throw new JmiServiceException(e);
    	}
    }
        
    //-----------------------------------------------------------------------
    public byte[] getFavoriteChart0(
    ) {
    	return this.thisContext().charts[0];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart0Description(
    ) {
    	return this.thisContext().chartDescriptions[0];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart0MimeType(
    ) {
    	return this.thisContext().chartMimeTypes[0];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart0Name(
    ) {
    	return this.thisContext().chartNames[0];
    }

    //-----------------------------------------------------------------------
    public byte[] getFavoriteChart1(
    ) {
    	return this.thisContext().charts[1];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart1Description(
    ) {
    	return this.thisContext().chartDescriptions[1];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart1MimeType(
    ) {
    	return this.thisContext().chartMimeTypes[1];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart1Name(
    ) {
    	return this.thisContext().chartNames[1];
    }

    //-----------------------------------------------------------------------
    public byte[] getFavoriteChart2(
    ) {
    	return this.thisContext().charts[2];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart2Description(
    ) {
    	return this.thisContext().chartDescriptions[2];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart2MimeType(
    ) {
    	return this.thisContext().chartMimeTypes[2];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart2Name(
    ) {
    	return this.thisContext().chartNames[2];
    }
    
    //-----------------------------------------------------------------------
    public byte[] getFavoriteChart3(
    ) {
    	return this.thisContext().charts[3];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart3Description(
    ) {
    	return this.thisContext().chartDescriptions[3];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart3MimeType(
    ) {
    	return this.thisContext().chartMimeTypes[3];
    }

    //-----------------------------------------------------------------------
    public java.lang.String getFavoriteChart3Name(
    ) {
    	return this.thisContext().chartNames[3];
    }
    
    //-----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
    @Override
    protected C newContext(
    ) {
		byte[][] charts = new byte[4][];
		String[] chartNames = new String[4];
		String[] chartMimeTypes = new String[4];
		String[] chartDescriptions = new String[4];
		// chart0
		if(this.sameObject().getChart0() != null) {
			org.opencrx.kernel.document1.jmi1.Media chart = this.sameObject().getChart0();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            try {
                BinaryLargeObjects.streamCopy(
                    chart.getContent().getContent(), 
                    0L, 
                    content
                );
            } 
            catch(Exception e) {}
			charts[0] = content.toByteArray();
			chartNames[0] = chart.getContentName();
			chartDescriptions[0] = chart.getDescription();
			chartMimeTypes[0] = chart.getContentMimeType();
		}
		// chart1
		if(this.sameObject().getChart1() != null) {
			org.opencrx.kernel.document1.jmi1.Media chart = this.sameObject().getChart1();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            try {
                BinaryLargeObjects.streamCopy(
                    chart.getContent().getContent(), 
                    0L, 
                    content
                );
            } 
            catch(Exception e) {}
			charts[1] = content.toByteArray();
			chartNames[1] = chart.getContentName();
			chartDescriptions[1] = chart.getDescription();
			chartMimeTypes[1] = chart.getContentMimeType();
		}
		// chart2
		if(this.sameObject().getChart2() != null) {
			org.opencrx.kernel.document1.jmi1.Media chart = this.sameObject().getChart2();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            try {
                BinaryLargeObjects.streamCopy(
                    chart.getContent().getContent(), 
                    0L, 
                    content
                );
            } 
            catch(Exception e) {}
			charts[2] = content.toByteArray();
			chartNames[2] = chart.getContentName();
			chartDescriptions[2] = chart.getDescription();
			chartMimeTypes[2] = chart.getContentMimeType();
		}
		// chart3
		if(this.sameObject().getChart3() != null) {
			org.opencrx.kernel.document1.jmi1.Media chart = this.sameObject().getChart3();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            try {
                BinaryLargeObjects.streamCopy(
                    chart.getContent().getContent(), 
                    0L, 
                    content
                );
            } 
            catch(Exception e) {}
			charts[3] = content.toByteArray();
			chartNames[3] = chart.getContentName();
			chartDescriptions[3] = chart.getDescription();
			chartMimeTypes[3] = chart.getContentMimeType();
		}
        return (C)new DerivedAttributes(
        	charts,
        	chartNames,
        	chartMimeTypes,
        	chartDescriptions
        );
    }
    
}

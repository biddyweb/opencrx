/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: SecureObject.java,v 1.30 2011/03/08 23:25:57 wfro Exp $
 * Description: SecureObject
 * Revision:    $Revision: 1.30 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2011/03/08 23:25:57 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.backend;

import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.oasisopen.jmi1.RefContainer;
import org.opencrx.kernel.utils.Utils;
import org.opencrx.security.realm1.jmi1.PrincipalGroup;
import org.opencrx.security.realm1.jmi1.Realm1Package;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.marshalling.Marshaller;
import org.openmdx.base.mof.cci.AggregationKind;
import org.openmdx.base.mof.cci.ModelElement_1_0;
import org.openmdx.base.mof.cci.Model_1_0;
import org.openmdx.base.mof.spi.Model_1Factory;
import org.openmdx.base.naming.Path;

public class SecureObject extends AbstractImpl {

    //-------------------------------------------------------------------------
	public static void register(
	) {
		registerImpl(new SecureObject());
	}
	
    //-------------------------------------------------------------------------
	public static SecureObject getInstance(
	) throws ServiceException {
		return getInstance(SecureObject.class);
	}

	//-------------------------------------------------------------------------
	protected SecureObject(
	) {
		
	}
	
    //-----------------------------------------------------------------------
    static class SetOwningUserMarshaller implements Marshaller {
    	
    	public SetOwningUserMarshaller(
    		org.opencrx.security.realm1.jmi1.User user    		
    	) {
    		this.user = user;
    	}
    	
        public Object marshal(Object s) throws ServiceException {
            if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
            	org.opencrx.kernel.base.jmi1.SecureObject obj = (org.opencrx.kernel.base.jmi1.SecureObject)s;
            	if(this.user != null) {
            		obj.setOwningUser(this.user);
            	}
            }
            return s;
        }

        public Object unmarshal(Object s) {
          throw new UnsupportedOperationException();
        }
        
        private final org.opencrx.security.realm1.jmi1.User user;
        
    }

    static class AddOwningGroupMarshaller implements Marshaller {

    	public AddOwningGroupMarshaller(
    		org.opencrx.security.realm1.jmi1.PrincipalGroup group
    	) {
    		this.group = group;
    	}
        public Object marshal(Object s) throws ServiceException {
            if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
            	org.opencrx.kernel.base.jmi1.SecureObject obj = (org.opencrx.kernel.base.jmi1.SecureObject)s;
            	if(!obj.getOwningGroup().contains(this.group)) {
            		obj.getOwningGroup().add(this.group);
            	}
            }
            return s;
        }
        
        public Object unmarshal(Object s) {
          throw new UnsupportedOperationException();
        }
    	
        private final org.opencrx.security.realm1.jmi1.PrincipalGroup group;
    }

    static class ReplaceOwningGroupMarshaller implements Marshaller {
    
    	public ReplaceOwningGroupMarshaller(
    		List<org.opencrx.security.realm1.jmi1.PrincipalGroup> groups
    	) {
    		this.groups = groups;
    	}
    	
        public Object marshal(Object s) throws ServiceException {
            if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
            	org.opencrx.kernel.base.jmi1.SecureObject obj = (org.opencrx.kernel.base.jmi1.SecureObject)s;
            	obj.getOwningGroup().clear();
            	obj.getOwningGroup().addAll(this.groups);
            }
            return s;
        }
        public Object unmarshal(Object s) {
          throw new UnsupportedOperationException();
        }

        private final List<org.opencrx.security.realm1.jmi1.PrincipalGroup> groups;
    	
    }

    static class RemoveOwningGroupMarshaller implements Marshaller {
    	
    	public RemoveOwningGroupMarshaller(
    		org.opencrx.security.realm1.jmi1.PrincipalGroup group    		
    	) {
    		this.group = group;
    	}
    	
        public Object marshal(Object s) throws ServiceException {
            if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
            	org.opencrx.kernel.base.jmi1.SecureObject obj = (org.opencrx.kernel.base.jmi1.SecureObject)s;
            	obj.getOwningGroup().remove(this.group);
            }
            return s;
        }
        public Object unmarshal(Object s) {
          throw new UnsupportedOperationException();
        }

        private final org.opencrx.security.realm1.jmi1.PrincipalGroup group;
        
    }

    static class SetAccessLevelMarshaller implements Marshaller {
    	
    	public SetAccessLevelMarshaller(
    		short accessLevelBrowse,
    		short accessLevelUpdate,
    		short accessLevelDelete
    	) {
    		this.accessLevelBrowse = accessLevelBrowse;
    		this.accessLevelUpdate = accessLevelUpdate;
    		this.accessLevelDelete = accessLevelDelete;
    		
    	}
    	
        public Object marshal(Object s) throws ServiceException {
            if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
            	org.opencrx.kernel.base.jmi1.SecureObject obj = (org.opencrx.kernel.base.jmi1.SecureObject)s;        
            	obj.setAccessLevelBrowse(this.accessLevelBrowse);
            	obj.setAccessLevelUpdate(this.accessLevelUpdate);
            	obj.setAccessLevelDelete(this.accessLevelDelete);
            }
	        return s;
        }
        public Object unmarshal(Object s) {
          throw new UnsupportedOperationException();
        }

		private final short accessLevelBrowse;
		private final short accessLevelUpdate;
		private final short accessLevelDelete;
        
    }
    
    //-------------------------------------------------------------------------
    public org.openmdx.security.realm1.jmi1.Principal findPrincipal(
        String name,
        org.openmdx.security.realm1.jmi1.Realm realm
    ) {
        try {
        	PersistenceManager pm = JDOHelper.getPersistenceManager(realm);
            return (org.openmdx.security.realm1.jmi1.Principal)pm.getObjectById(
                realm.refGetPath().getDescendant(new String[]{"principal", name})
            );
        }
        catch(Exception e) {
            return null;
        }
    }

    //-------------------------------------------------------------------------
    public org.openmdx.security.realm1.jmi1.Realm getRealm(
        javax.jdo.PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        return (org.openmdx.security.realm1.jmi1.Realm)pm.getObjectById(
            new Path("xri://@openmdx*org.openmdx.security.realm1").getDescendant("provider", providerName, "segment", "Root", "realm", segmentName)
        );
    }

    //-----------------------------------------------------------------------
    public PrincipalGroup initPrincipalGroup(
        String groupName,
        PersistenceManager pm,
        String providerName,
        String segmentName
    ) {
        Realm1Package realmPkg = Utils.getRealmPackage(pm);
        org.openmdx.security.realm1.jmi1.Realm realm = this.getRealm(
            pm, 
            providerName, 
            segmentName
        );
        PrincipalGroup principalGroup = null;
        if((principalGroup = (PrincipalGroup)this.findPrincipal(groupName, realm)) != null) {
            return principalGroup;            
        }        
        pm.currentTransaction().begin();
        principalGroup = realmPkg.getPrincipalGroup().createPrincipalGroup();
        principalGroup.refInitialize(false, false);
        principalGroup.setDescription(segmentName + "\\\\" + groupName);
        realm.addPrincipal(                
            false,
            groupName,
            principalGroup
        );                        
        pm.currentTransaction().commit();
        return principalGroup;
    }
                
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void applyAcls(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
        Marshaller marshaller,
        Short mode,
        String reportText,
        List<String> report
    ) {
        try {
            marshaller.marshal(obj);
            report.add(reportText);           
            if((mode != null) && (mode.intValue() == MODE_RECURSIVE)) {
            	Model_1_0 model = Model_1Factory.getModel();
                Map<String,ModelElement_1_0> references = (Map)model.getElement(
                    obj.refClass().refMofId()
                ).objGetValue("reference");
                for(ModelElement_1_0 featureDef: references.values()) {
                    ModelElement_1_0 referencedEnd = model.getElement(
                        featureDef.objGetValue("referencedEnd")
                    );
                    if(
                        model.isReferenceType(featureDef) &&
                        AggregationKind.COMPOSITE.equals(referencedEnd.objGetValue("aggregation")) &&
                        ((Boolean)referencedEnd.objGetValue("isChangeable")).booleanValue()
                    ) {
                        String referenceName = (String)featureDef.objGetValue("name");
                        RefContainer container = (RefContainer)obj.refGetValue(referenceName);
                        List<?> content = container.refGetAll(null);
                        for(Object contained: content) {
                        	if(contained instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
                        		this.applyAcls(
	                                (org.opencrx.kernel.base.jmi1.SecureObject)contained,
	                                marshaller,
	                                mode,
	                                reportText,
	                                report
	                            );
                        	}
                        }
                    }
                }
            }
        }
        catch(ServiceException e){
            e.log();
            report.add(e.getMessage());
        }
    }
    
    //-----------------------------------------------------------------------
    public void setOwningUser(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
        org.opencrx.security.realm1.jmi1.User user,
        short mode,
        List<String> report
    ) throws ServiceException {   
    	this.applyAcls(
            obj,
            new SetOwningUserMarshaller(
        		user
        	),
            mode,
            "setOwningUser",
            report
        );
    }
    
    //-----------------------------------------------------------------------
    public void addOwningGroup(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
    	org.opencrx.security.realm1.jmi1.PrincipalGroup group,
    	short mode,
        List<String> report
    ) throws ServiceException {        
    	this.applyAcls(
            obj,
            new AddOwningGroupMarshaller(
            	group
            ),
            mode,
            "addOwningGroup",
            report
        );
    }
            
    //-----------------------------------------------------------------------
    public void replaceOwningGroups(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
    	List<org.opencrx.security.realm1.jmi1.PrincipalGroup> groups,
    	short mode,
        List<String> report
    ) throws ServiceException {        
    	this.applyAcls(
            obj,
            new ReplaceOwningGroupMarshaller(
            	groups
            ),
            mode,
            "replaceOwningGroup",
            report
        );
    }
            
    //-----------------------------------------------------------------------
    public void removeOwningGroup(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
    	org.opencrx.security.realm1.jmi1.PrincipalGroup group,
    	short mode,
        List<String> report
    ) throws ServiceException {        
    	this.applyAcls(
            obj,
            new RemoveOwningGroupMarshaller(
            	group
            ),
            mode,
            "removeOwningGroup",
            report
        );
    }

    //-----------------------------------------------------------------------
    public void removeAllOwningGroup(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
    	short mode,
        List<String> report
    ) throws ServiceException {        
    	this.applyAcls(
            obj,
            new Marshaller() {
                public Object marshal(Object s) throws ServiceException {
                    if(s instanceof org.opencrx.kernel.base.jmi1.SecureObject) {
                        ((org.opencrx.kernel.base.jmi1.SecureObject)s).getOwningGroup().clear();
                    }
                    return s;
                }
                public Object unmarshal(Object s) {
                  throw new UnsupportedOperationException();
                }
            },
            mode,
            "removeAllOwningGroup",
            report
        );
    }
        
    //-----------------------------------------------------------------------
    public void setAccessLevel(
    	org.opencrx.kernel.base.jmi1.SecureObject obj,
    	short accessLevelBrowse,
    	short accessLevelUpdate,
    	short accessLevelDelete,
    	short mode,
        List<String> report
    ) throws ServiceException {        
    	this.applyAcls(
            obj,
            new SetAccessLevelMarshaller(
            	accessLevelBrowse,
            	accessLevelUpdate,
            	accessLevelDelete
            ),
            mode,
            "setAccessLevel",
            report
        );
    }

    //-------------------------------------------------------------------------
    public Path getLoginRealmIdentity(
    	String providerName
    ) {
    	return SecureObject.getRealmIdentity(providerName, "Default");
    }
    
    //-------------------------------------------------------------------------
    public static Path getRealmIdentity(
    	String providerName,
    	String realmName
    ) {
        return new Path("xri:@openmdx:org.openmdx.security.realm1/provider/" + providerName + "/segment/Root/realm/" + realmName);
    }

    //-------------------------------------------------------------------------
    public void updateSecureObject(
        org.opencrx.kernel.base.jmi1.SecureObject secureObject
    ) throws ServiceException {
    	
    }
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    public static final int MODE_LOCAL = 0;
    public static final int MODE_RECURSIVE = 1;
        
}

//--- End of File -----------------------------------------------------------

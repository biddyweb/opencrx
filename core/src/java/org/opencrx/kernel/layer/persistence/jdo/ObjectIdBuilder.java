/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ObjectIdBuilder.java,v 1.35 2009/05/06 16:48:55 wfro Exp $
 * Description: ObjectIdBuilder
 * Revision:    $Revision: 1.35 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/05/06 16:48:55 $
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
package org.opencrx.kernel.layer.persistence.jdo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.openmdx.base.exception.RuntimeServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;

@SuppressWarnings("unchecked")
public class ObjectIdBuilder {

    /**
     * Abstract Object Id Parser
     */
    public abstract static class AbstractObjectIdParser {

        /**
         * Constructor 
         *
         * @param path
         */
        protected AbstractObjectIdParser(
            final Path path
        ) {
            this.path = path;
            if(path.size() % 2 != 1) throw new RuntimeServiceException(           
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.BAD_PARAMETER,
                "An object id requires an odd number of path components",
                new BasicException.Parameter("ObjectIdParser", this.getClass().getName()),
                new BasicException.Parameter("path", (Object[])path.getSuffix(0))
            );
        }

        /**
         * 
         */
        protected final Path path;

        /* (non-Javadoc)
         * @see org.oasisopen.spi2.ObjectId#getQualifier(java.lang.Class, int)
         */
        public <E> E getQualifier(
            Class<E> qualifierClass, 
            int index
        ) {
            String message = qualifierClass != String.class ?
                "This object id builder expects the qualifier being of type String" :
                    index != 0 ?
                        "This object id builder expects a single qualifier" :
                            null;
            if(message != null) throw new RuntimeServiceException(           
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.BAD_PARAMETER,
                message,
                new BasicException.Parameter("ObjectIdParser", this.getClass().getName()),
                new BasicException.Parameter("qualifierClass", qualifierClass),
                new BasicException.Parameter("index", index)
            );
            String value = this.path.getBase();
            return (E) (
                    value.startsWith("!") ? value.substring(1) : value
            );
        }

        /* (non-Javadoc)
         * @see org.oasisopen.spi2.ObjectId#isQualifierPersistent(int)
         */
        public boolean isQualifierPersistent(int index) {
            return this.path.getBase().startsWith("!");
        }

    }
    
    public static class ObjectIdParser extends ObjectIdBuilder.AbstractObjectIdParser {
                
        protected ObjectIdParser(
            final Path path
        ) {
            super(path);
        }

        public List<String> getParentClass(
            String parentObjectId
        ) {
            List<String> components = new ArrayList<String>();
            StringTokenizer t = new StringTokenizer(parentObjectId, "/");
            while(t.hasMoreTokens()) {
                components.add(t.nextToken());          
            }
            if(components.isEmpty()) {
                throw new RuntimeServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.ASSERTION_FAILURE, 
                    "No components found for object id",
                    new BasicException.Parameter("objectId", parentObjectId)
                );          
            }
            for(int i = 0; i < TYPE_NAMES.length; i++) {
                if(TYPE_NAMES[i].equals(components.get(0))) {
                    return CLASS_NAMES[i];
                }
            }
            throw new RuntimeServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.ASSERTION_FAILURE, 
                "No class found for object id",
                new BasicException.Parameter("objectId", parentObjectId)
            );          
        }
                
    }
    
    //-----------------------------------------------------------------------
    public String fromPath(
        Path path
    ) {
        String objectId = null;
        for(int i = 0; i < TYPES.length; i++) {
            Path type = TYPES[i];
            if((type.size() >= 2) && path.isLike(type.getParent())) {
                objectId = TYPE_NAMES[i];
                for(
                    int l = 0;
                    l < path.size();
                    l++
                ) {
                    if(
                        ":*".equals(path.get(l)) ||
                        !path.get(l).equals(type.get(l))
                    ) {                  
                        objectId += "/" + path.get(l);
                    }
                }
            }
        }
        if(objectId == null) {
            throw new RuntimeServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.ASSERTION_FAILURE, 
                "No type found for path", 
                new BasicException.Parameter("path", path)
            );          
        }
        return objectId;
    }

    //-----------------------------------------------------------------------
    public Path toPath(
        String objectId
    ) {
      List<String> components = new ArrayList<String>();
      StringTokenizer t = new StringTokenizer(objectId, "/");
      while(t.hasMoreTokens()) {
          components.add(t.nextToken());          
      }
      if(components.isEmpty()) {
          throw new RuntimeServiceException(
              BasicException.Code.DEFAULT_DOMAIN,
              BasicException.Code.ASSERTION_FAILURE, 
              "No components found for object id",
              new BasicException.Parameter("objectId", objectId)
          );          
      }
      Path type = null;
      for(int i = 0; i < TYPE_NAMES.length; i++) {
          if(TYPE_NAMES[i].equals(components.get(0))) {
              type = TYPES[i];
              break;
          }
      }
      if(type == null) {
          throw new RuntimeServiceException(
              BasicException.Code.DEFAULT_DOMAIN,
              BasicException.Code.ASSERTION_FAILURE, 
              "No type found for object id", 
              new BasicException.Parameter("objectId", objectId)
          );                    
      }
      String[] objectIdComponents = new String[type.size()];
      int pos = 1;
      for(int i = 0; i < objectIdComponents.length; i++) {
          if(":*".equals(type.get(i))) {
              if(pos >= components.size()) {
                  throw new RuntimeServiceException(
                      BasicException.Code.DEFAULT_DOMAIN,
                      BasicException.Code.ASSERTION_FAILURE, 
                      "object id not valid for type", 
                      new BasicException.Parameter("objectId", objectId),
                      new BasicException.Parameter("type", type)
                  );                            
              }
              objectIdComponents[i] = components.get(pos);
              pos++;
          }
          else {
              objectIdComponents[i] = type.get(i);
          }
      }
      Path path = new Path(objectIdComponents);
      if(path.size() % 2 == 0) {
          throw new RuntimeServiceException(
              BasicException.Code.DEFAULT_DOMAIN,
              BasicException.Code.ASSERTION_FAILURE, 
              "path size must be odd number",
               new BasicException.Parameter("path", path)
          );                              
      }
      return path;
    }
    
    //-----------------------------------------------------------------------
    public ObjectIdParser parseObjectId(
        String objectId, 
        List<String> baseClass
    ) {
        return new ObjectIdParser(
            this.toPath(objectId)
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    public static final Path[] TYPES = {
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uomSchedule/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uom/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*/permission/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/wfProcess/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/privilege/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*/credential/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/revenueReport/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/subscription/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/topic/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/emailRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/mailingRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*/entry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/basePrice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/meetingParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/eMailAccount/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/contactRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/phoneCallRecipient/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/meetingActivityParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/assignedActivity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/salesTaxType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/assignedAccount/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*/member/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/taskParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/incidentParty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/priceModifier/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/assignedActivity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/ouRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/followUp/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedResource/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.region1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/searchIndexEntry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceListEntry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*/actionLog/:*"),
        new Path("xri:@openmdx:org:opencrx.kernel.reservation1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contact1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/effortEstimate/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/productApplication/:*"),
        new Path("xri:@openmdx:org.opencrx:kernel.account1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx:kernel.model1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedResource/:*/workRecord/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/vote/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/filteredActivity/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*/accountFilterProperty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/contactMembership/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/contactMembership/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/address/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityNote/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.addess1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/deliveryInformation/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCreator/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*/filterProperty/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/rating/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/note/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/deliveryRequest/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/deliveryRequest/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/state/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/transition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/transition/:*/action/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/creditLimit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/creditLimit/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/member/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/media/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/quickAccess/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/accessHistory/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/assignedContract/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/assignedContract/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*/assignedActivity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*/filterProperty/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/productReference/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/audit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/product/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedGroup/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/activityLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/activityLinkFrom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/followUp/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*/weekDay/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*/calendarDay/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/position/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingText/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/definingProfile/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/relatedProduct/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*/descriptor/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/dependency/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/artifactContext/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/vp/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entityRelationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/depotType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingPeriod/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/simpleBooking/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/accountFilterProperty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/productFilterProperty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/revision/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/configuration/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/:*/:*/basePrice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/property/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/pricingRule/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/calculationRule/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/depotReference/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/depotReference/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/propertySet/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/propertySet/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/propertySet/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/propertySet/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/productApplication/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/deliveryInformation/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*/configurationType/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/contractRole/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/contractRole/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/removedPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/removedPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/removedPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/removedPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/removedPosition/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/positionModification/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/positionModification/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/positionModification/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/positionModification/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/positionModification/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*"),
        new Path("xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*/permission/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/privilege/:*"),
        new Path("xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject/:*"),
        new Path("xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*/credential/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/assignedAddress/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.admin1/provider/:*/segment/:*/configuration/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productClassification/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productClassification/:*/relationship/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/accountMembership/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/indexEntry/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/propertySetEntry/:*"),
        new Path("xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/propertySetEntry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/address/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/facility/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingUnit/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*/itemLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*/itemLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*/itemLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/itemLinkTo/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*/itemLinkFrom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*/itemLinkFrom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*/itemLinkFrom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/itemLinkFrom/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/assignedAccount/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/booking/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*/addressFilterProperty/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/link/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/lock/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/attachment/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/involvedObject/:*")
    };

    @SuppressWarnings("cast")
    public static final List<String>[] CLASS_NAMES = new List[]{
        org.opencrx.kernel.account1.jpa3.Organization.CLASS,
        org.opencrx.security.identity1.jpa3.Segment.CLASS,
        org.openmdx.security.realm1.jpa3.Segment.CLASS,
        org.opencrx.kernel.uom1.jpa3.UomSchedule.CLASS,
        org.opencrx.kernel.uom1.jpa3.Uom.CLASS,
        org.openmdx.security.realm1.jpa3.Principal.CLASS,
        org.openmdx.security.authorization1.jpa3.Policy.CLASS,
        (List<String>)null,
        org.opencrx.kernel.contract1.jpa3.Lead.CLASS,
        org.openmdx.security.realm1.jpa3.Role.CLASS,
        org.openmdx.security.realm1.jpa3.Permission.CLASS,
        org.opencrx.kernel.workflow1.jpa3.AbstractTask.CLASS,
        org.opencrx.kernel.product1.jpa3.Product.CLASS,
        org.openmdx.security.authorization1.jpa3.Privilege.CLASS,
        org.opencrx.security.identity1.jpa3.Subject.CLASS,
        org.openmdx.security.authentication1.jpa3.Segment.CLASS,
        org.opencrx.kernel.document1.jpa3.Segment.CLASS,
        org.openmdx.security.authentication1.jpa3.Credential.CLASS,
        org.openmdx.security.authentication1.jpa3.Segment.CLASS,
        org.opencrx.kernel.account1.jpa3.RevenueReport.CLASS,
        org.opencrx.kernel.home1.jpa3.Subscription.CLASS,
        org.openmdx.security.realm1.jpa3.Segment.CLASS,
        org.openmdx.security.authorization1.jpa3.Segment.CLASS,
        org.opencrx.kernel.activity1.jpa3.Activity.CLASS,
        org.opencrx.kernel.workflow1.jpa3.Topic.CLASS,
        org.opencrx.kernel.activity1.jpa3.AbstractEMailRecipient.CLASS,
        org.opencrx.kernel.activity1.jpa3.AbstractMailingRecipient.CLASS,
        org.opencrx.kernel.code1.jpa3.AbstractEntry.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductBasePrice.CLASS,
        org.opencrx.kernel.code1.jpa3.CodeValueContainer.CLASS,
        org.opencrx.kernel.activity1.jpa3.MeetingParty.CLASS,
        org.opencrx.kernel.document1.jpa3.Document.CLASS,
        org.opencrx.kernel.home1.jpa3.EMailAccount.CLASS,
        org.opencrx.kernel.account1.jpa3.ContactRelationship.CLASS,
        org.opencrx.kernel.activity1.jpa3.AbstractPhoneCallRecipient.CLASS,
        org.opencrx.kernel.activity1.jpa3.MeetingParty.CLASS,
        org.opencrx.kernel.building1.jpa3.Building.CLASS,
        org.opencrx.kernel.activity1.jpa3.Activity.CLASS,
        org.opencrx.kernel.product1.jpa3.SalesTaxType.CLASS,
        org.opencrx.kernel.product1.jpa3.AccountAssignment.CLASS,
        org.opencrx.kernel.product1.jpa3.PriceLevel.CLASS,
        org.opencrx.kernel.account1.jpa3.Competitor.CLASS,
        org.opencrx.kernel.building1.jpa3.BuildingUnit.CLASS,
        org.opencrx.kernel.activity1.jpa3.AddressGroupMember.CLASS,
        org.opencrx.kernel.activity1.jpa3.TaskParty.CLASS,
        org.opencrx.kernel.activity1.jpa3.IncidentParty.CLASS,
        org.opencrx.kernel.product1.jpa3.PriceModifier.CLASS,
        org.opencrx.kernel.activity1.jpa3.Activity.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityTracker.CLASS,
        org.opencrx.kernel.building1.jpa3.Segment.CLASS,
        org.opencrx.kernel.account1.jpa3.OrganizationalUnitRelationship.CLASS,
        org.opencrx.kernel.forecast1.jpa3.Budget.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFollowUp.CLASS,
        org.opencrx.kernel.activity1.jpa3.AddressGroup.CLASS,
        org.opencrx.kernel.activity1.jpa3.ResourceAssignment.CLASS,
        org.opencrx.kernel.code1.jpa3.Segment.CLASS,
        org.opencrx.kernel.product1.jpa3.Segment.CLASS,
        org.opencrx.kernel.admin1.jpa3.Segment.CLASS,
        (List<String>)null,
        org.opencrx.kernel.account1.jpa3.SearchIndexEntry.CLASS,
        org.opencrx.kernel.product1.jpa3.PriceListEntry.CLASS,
        org.opencrx.kernel.home1.jpa3.WfActionLogEntry.CLASS,
        org.opencrx.kernel.reservation1.jpa3.Segment.CLASS,
        org.opencrx.kernel.building1.jpa3.BuildingComplex.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityCategory.CLASS,
        (List<String>)null,
        org.opencrx.kernel.account1.jpa3.Account.CLASS,
        org.opencrx.kernel.home1.jpa3.WfProcessInstance.CLASS,
        org.opencrx.kernel.account1.jpa3.OrganizationalUnit.CLASS,
        org.opencrx.kernel.activity1.jpa3.EffortEstimate.CLASS,
        org.opencrx.kernel.contract1.jpa3.ProductApplication.CLASS,
        org.opencrx.kernel.account1.jpa3.Segment.CLASS,
        org.opencrx.kernel.model1.jpa3.Segment.CLASS,
        org.opencrx.kernel.depot1.jpa3.Segment.CLASS,
        org.opencrx.kernel.activity1.jpa3.WorkAndExpenseRecord.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityVote.CLASS,
        org.opencrx.kernel.activity1.jpa3.Activity.CLASS,
        org.opencrx.kernel.generic.jpa3.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jpa3.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jpa3.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jpa3.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.account1.jpa3.AccountFilterGlobal.CLASS,
        org.opencrx.kernel.account1.jpa3.AccountFilterProperty.CLASS,
        org.opencrx.kernel.forecast1.jpa3.Segment.CLASS,
        org.opencrx.kernel.account1.jpa3.ContactMembership.CLASS,
        org.opencrx.kernel.account1.jpa3.ContactMembership.CLASS,
        Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable"),
        Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable"),
        Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable"),
        (List<String>)null,
        org.opencrx.kernel.workflow1.jpa3.Segment.CLASS,
        org.opencrx.kernel.uom1.jpa3.Segment.CLASS,
        org.opencrx.kernel.activity1.jpa3.Segment.CLASS,
        org.opencrx.kernel.home1.jpa3.Segment.CLASS,
        org.opencrx.kernel.contract1.jpa3.Segment.CLASS,
        (List<String>)null,
        org.openmdx.security.realm1.jpa3.Segment.CLASS,
        org.opencrx.security.identity1.jpa3.Segment.CLASS,
        org.openmdx.security.authorization1.jpa3.Segment.CLASS,
        org.opencrx.kernel.contract1.jpa3.DeliveryInformation.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityCreator.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFilterGlobal.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFilterProperty.CLASS,
        org.opencrx.kernel.generic.jpa3.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jpa3.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jpa3.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jpa3.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jpa3.Rating.CLASS,
        org.opencrx.kernel.generic.jpa3.Rating.CLASS,
        org.opencrx.kernel.generic.jpa3.Rating.CLASS,
        org.opencrx.kernel.generic.jpa3.Rating.CLASS,
        org.opencrx.kernel.generic.jpa3.Note.CLASS,
        org.opencrx.kernel.generic.jpa3.Note.CLASS,
        org.opencrx.kernel.generic.jpa3.Note.CLASS,
        org.opencrx.kernel.generic.jpa3.Note.CLASS,
        org.opencrx.kernel.contract1.jpa3.DeliveryRequest.CLASS,
        org.opencrx.kernel.contract1.jpa3.DeliveryRequest.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractOpportunityPosition.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityProcess.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityProcessState.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityProcessTransition.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityProcessAction.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityMilestone.CLASS,
        org.opencrx.kernel.account1.jpa3.CreditLimit.CLASS,
        org.opencrx.kernel.account1.jpa3.CreditLimit.CLASS,
        org.opencrx.kernel.generic.jpa3.Description.CLASS,
        org.opencrx.kernel.generic.jpa3.Description.CLASS,
        org.opencrx.kernel.generic.jpa3.Description.CLASS,
        org.opencrx.kernel.account1.jpa3.Member.CLASS,
        org.opencrx.kernel.generic.jpa3.Media.CLASS,
        org.opencrx.kernel.generic.jpa3.Media.CLASS,
        org.opencrx.kernel.generic.jpa3.Media.CLASS,
        org.opencrx.kernel.generic.jpa3.Media.CLASS,
        org.opencrx.kernel.home1.jpa3.UserHome.CLASS,
        org.opencrx.kernel.home1.jpa3.Alert.CLASS,
        org.opencrx.kernel.home1.jpa3.QuickAccess.CLASS,
        org.opencrx.kernel.home1.jpa3.AccessHistory.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityType.CLASS,
        org.opencrx.kernel.activity1.jpa3.Calendar.CLASS,
        org.opencrx.kernel.activity1.jpa3.Resource.CLASS,
        Arrays.asList("org", "opencrx", "kernel", "contract1", "AbstractContract"),
        Arrays.asList("org", "opencrx", "kernel", "contract1", "AbstractContract"),
        org.opencrx.kernel.activity1.jpa3.Activity.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractQuotePosition.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFilterGroup.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFilterProperty.CLASS,
        org.opencrx.kernel.activity1.jpa3.ProductReference.CLASS,
        org.opencrx.kernel.base.jpa3.Property.CLASS,
        org.opencrx.kernel.base.jpa3.AuditEntry.CLASS,
        org.opencrx.kernel.product1.jpa3.Product.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityGroupAssignment.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityLinkTo.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityLinkFrom.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractSalesOrderPosition.CLASS,
        org.opencrx.kernel.activity1.jpa3.ActivityFollowUp.CLASS,
        org.opencrx.kernel.activity1.jpa3.WeekDay.CLASS,
        org.opencrx.kernel.activity1.jpa3.CalendarDay.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractInvoicePosition.CLASS,
        org.opencrx.kernel.model1.jpa3.Element.CLASS,
        org.opencrx.kernel.depot1.jpa3.BookingText.CLASS,
        org.opencrx.kernel.ras1.jpa3.Profile.CLASS,
        org.opencrx.kernel.product1.jpa3.RelatedProduct.CLASS,
        org.opencrx.kernel.ras1.jpa3.ClassificationElement.CLASS,
        org.opencrx.kernel.ras1.jpa3.Descriptor.CLASS,
        org.opencrx.kernel.ras1.jpa3.SolutionPart.CLASS,
        org.opencrx.kernel.ras1.jpa3.ArtifactDependency.CLASS,
        org.opencrx.kernel.ras1.jpa3.ArtifactContext.CLASS,
        org.opencrx.kernel.ras1.jpa3.VariabilityPoint.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotEntity.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotEntityRelationship.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotType.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotGroup.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotHolder.CLASS,
        org.opencrx.kernel.depot1.jpa3.CompoundBooking.CLASS,
        org.opencrx.kernel.depot1.jpa3.BookingPeriod.CLASS,
        org.opencrx.kernel.depot1.jpa3.SingleBooking.CLASS,
        org.opencrx.kernel.depot1.jpa3.SimpleBooking.CLASS,
        org.opencrx.kernel.depot1.jpa3.Depot.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotPosition.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductConfiguration.CLASS,
        org.opencrx.kernel.base.jpa3.Property.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotReport.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotReportItemPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.Opportunity.CLASS,
        org.opencrx.kernel.generic.jpa3.Description.CLASS,
        org.opencrx.kernel.generic.jpa3.Description.CLASS,
        org.opencrx.kernel.account1.jpa3.AccountFilterProperty.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductFilterProperty.CLASS,
        org.opencrx.kernel.document1.jpa3.DocumentFolder.CLASS,
        org.opencrx.kernel.document1.jpa3.DocumentRevision.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductConfiguration.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductConfiguration.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductBasePrice.CLASS,
        org.opencrx.kernel.contract1.jpa3.Quote.CLASS,
        org.opencrx.kernel.contract1.jpa3.SalesOrder.CLASS,
        org.opencrx.kernel.base.jpa3.Property.CLASS,
        org.opencrx.kernel.contract1.jpa3.Invoice.CLASS,
        org.opencrx.kernel.product1.jpa3.PricingRule.CLASS,
        org.opencrx.kernel.contract1.jpa3.CalculationRule.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotReference.CLASS,
        org.opencrx.kernel.depot1.jpa3.DepotReference.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySet.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySet.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySet.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySet.CLASS,
        org.opencrx.kernel.contract1.jpa3.ProductApplication.CLASS,
        org.opencrx.kernel.contract1.jpa3.DeliveryInformation.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductConfigurationTypeSet.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductConfigurationType.CLASS,
        org.opencrx.kernel.contract1.jpa3.ContractRole.CLASS,
        org.opencrx.kernel.contract1.jpa3.ContractRole.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jpa3.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jpa3.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jpa3.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jpa3.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jpa3.PositionModification.CLASS,
        org.openmdx.security.realm1.jpa3.Realm.CLASS,
        org.openmdx.security.realm1.jpa3.Principal.CLASS,
        org.openmdx.security.authorization1.jpa3.Policy.CLASS,
        org.openmdx.security.realm1.jpa3.Role.CLASS,
        org.openmdx.security.realm1.jpa3.Permission.CLASS,
        org.openmdx.security.authorization1.jpa3.Privilege.CLASS,
        org.opencrx.security.identity1.jpa3.Subject.CLASS,
        org.openmdx.security.authentication1.jpa3.Credential.CLASS,
        Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable"),
        org.opencrx.kernel.admin1.jpa3.ComponentConfiguration.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductClassification.CLASS,
        org.opencrx.kernel.product1.jpa3.ProductClassificationRelationship.CLASS,
        org.opencrx.kernel.account1.jpa3.AccountMembership.CLASS,
        org.opencrx.kernel.base.jpa3.IndexEntry.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySetEntry.CLASS,
        org.opencrx.kernel.generic.jpa3.PropertySetEntry.CLASS,
        Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable"),
        org.opencrx.kernel.building1.jpa3.Facility.CLASS,
        org.opencrx.kernel.building1.jpa3.Facility.CLASS,
        org.opencrx.kernel.building1.jpa3.Facility.CLASS,
        org.opencrx.kernel.building1.jpa3.Facility.CLASS,
        Arrays.asList("org", "opencrx", "kernel", "building1", "AbstractBuildingUnit"),
        org.opencrx.kernel.building1.jpa3.InventoryItem.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jpa3.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.product1.jpa3.AccountAssignmentProduct.CLASS,
        org.opencrx.kernel.depot1.jpa3.SingleBooking.CLASS,
        org.opencrx.kernel.account1.jpa3.AddressFilterGlobal.CLASS,
        org.opencrx.kernel.account1.jpa3.AddressFilterProperty.CLASS,
        org.opencrx.kernel.document1.jpa3.DocumentLink.CLASS,
        org.opencrx.kernel.document1.jpa3.DocumentLock.CLASS,
        org.opencrx.kernel.document1.jpa3.DocumentAttachment.CLASS,
        org.opencrx.kernel.home1.jpa3.ObjectFinder.CLASS,
        org.opencrx.kernel.activity1.jpa3.InvolvedObject.CLASS        
    };
    
    public static final String[] TYPE_NAMES = {
        "organization",
        "identities",
        "realm",
        "uomSchedule",
        "uom",
        "principal",
        "policy",
        "leadPos",
        "lead",
        "role",
        "permission",
        "wfProcess",
        "product",
        "privilege",
        "subject",
        "authentications",
        "docs",
        "credential",
        "authentications",
        "revenueReport",
        "subscription",
        "realms",
        "authorizations",
        "activity",
        "topic",
        "emailRecipient",
        "mailingRecipient",
        "valueContainerEntry",
        "price1",
        "valueContainer",
        "meetingParty",
        "doc",
        "eMailAccount",
        "contactRelationship",
        "phoneCallRecipient",
        "meetingActivityParty",
        "building",
        "accountAssignedActivity",
        "salesTaxType",
        "assignedAccount",
        "priceLevel",
        "competitor",
        "buildingUnit",
        "addressGroupMember",
        "taskParty",
        "incidentParty",
        "priceModifier",
        "userHomeAssignedActivity",
        "activityTracker",
        "buildings",
        "ouRelationship",
        "budget",
        "followUp1",
        "addressGroup",
        "assignedResource",
        "codes",
        "products",
        "admins",
        "regions",
        "searchIndexEntry",
        "priceListEntry",
        "actionLog",
        "reservations",
        "buildingComplex",
        "activityCategory",
        "contacts",
        "account",
        "wfProcessInstance",
        "organizationalUnit",
        "effortEstimate",
        "productApplication",
        "accounts",
        "models",
        "depots",
        "workRecord",
        "vote",
        "filteredActivity1",
        "externalLink1",
        "externalLink2",
        "externalLink3",
        "externalLink4",
        "accountFilter",
        "accountFilterProperty2",
        "forecasts",
        "oContactMembership",
        "ouContactMembership",
        "address1",
        "address2",
        "address3",
        "activityNote1",
        "workflows",
        "uoms",
        "activities",
        "homes",
        "contracts",
        "addresses",
        "realms",
        "identities",
        "authorizations",
        "deliveryInfo",
        "activityCreator",
        "activityFilter1",
        "activityFilterProperty1",
        "doc1",
        "doc2",
        "doc3",
        "doc4",
        "rating1",
        "rating2",
        "rating3",
        "rating4",
        "note1",
        "note2",
        "note3",
        "note4",
        "deliveryRequest1",
        "deliveryRequest2",
        "opportunityPos",
        "activityProcess",
        "activityProcessState",
        "activityProcessTransition",
        "activityProcessAction",
        "activityMilestone",
        "organizationalUnitCreditLimit",
        "organizationCreditLimit",
        "descr1",
        "descr2",
        "descr3",
        "accountMember",
        "media1",
        "media2",
        "media3",
        "media4",
        "userHome",
        "alert",
        "quickAccess",
        "accessHistory",
        "activityType",
        "calendar",
        "resource",
        "userHomeAssignedContract",
        "accountAssignedContract",
        "assignedActivity",
        "quotePos",
        "activityFilter2",
        "activityFilterProperty2",
        "productRef1",
        "p1",
        "audit",
        "accountProduct",
        "assignedGroup",
        "activityLinkTo",
        "activityLinkFrom",
        "salesOrderPos",
        "followUp",
        "weekDay",
        "calendarDay",
        "invoicePos",
        "modelElement",
        "bookingText",
        "productDefiningProfile",
        "relatedProduct",
        "productCassificationElement",
        "classificationElementDescriptor",
        "productSolutionPart",
        "solutionPartDependency",
        "solutionPartArtifactContext",
        "solutionPartVp",
        "depotEntity",
        "depotEntityRelationship",
        "depotType",
        "depotGroup",
        "depotHolder",
        "cb",
        "bookingPeriod",
        "booking",
        "simpleBooking",
        "depot",
        "depotPos",
        "config1",
        "p2",
        "depotReport",
        "depotReportItemPosition",
        "opportunity",
        "additionalDescription4",
        "additionalDescription5",
        "accountFilterProperty1",
        "productFilterProperty1",
        "docFolder",
        "docRevision",
        "config2",
        "config3",
        "price2",
        "quote",
        "salesOrder",
        "p3",
        "invoice",
        "pricingRule",
        "calcRule",
        "depotRef1",
        "depotRef2",
        "ps1",
        "ps2",
        "ps3",
        "ps4",
        "productApplicationRem",
        "deliveryInfoRem",
        "configTypeSet",
        "configType",
        "depotHolderContractRole",
        "depotContractRole",
        "leadPosRem",
        "opportunityPosRem",
        "quotePosRem",
        "salesOrderPosRem",
        "invoicePosRem",
        "leadPosMod",
        "opportunityPosMod",
        "quotePosMod",
        "salesOrderPosMod",
        "invoicePosMod",
        "realm",
        "principal",
        "policy",
        "role",
        "permission",
        "privilege",
        "subject",
        "credential",
        "assignedAddress",
        "componentConfig",
        "productClass",
        "productClassRel",
        "accountMembership",
        "indexEntry",
        "propertySetEntry1",
        "propertySetEntry2",
        "address4",
        "facility",
        "facility1",
        "facility2",
        "facility3",
        "buildingUnit1",
        "inventoryItem",
        "itemLinkTo",
        "itemLinkTo1",
        "itemLinkTo2",
        "itemLinkTo3",
        "itemLinkFrom",
        "itemLinkFrom1",
        "itemLinkFrom2",
        "itemLinkFrom3",
        "assignedAccount9",
        "booking1",
        "addressFilter",
        "addressFilterProperty",
        "documentLink",
        "documentLock",
        "documentAttachment",
        "objectFinder",
        "involvedObject"
    };

}

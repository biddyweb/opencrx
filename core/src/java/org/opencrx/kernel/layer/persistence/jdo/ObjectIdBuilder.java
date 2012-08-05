/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ObjectIdBuilder.java,v 1.25 2008/10/06 17:04:53 wfro Exp $
 * Description: ObjectIdBuilder
 * Revision:    $Revision: 1.25 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/10/06 17:04:53 $
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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.openmdx.base.exception.RuntimeServiceException;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.naming.jdo2.AbstractObjectIdBuilder;
import org.openmdx.kernel.exception.BasicException;

public class ObjectIdBuilder
    extends AbstractObjectIdBuilder {

    public static class ObjectIdParser extends AbstractObjectIdBuilder.AbstractObjectIdParser {
                
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
                    new BasicException.Parameter[]{
                        new BasicException.Parameter("objectId", parentObjectId)
                    },
                    "No components found for object id" 
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
                new BasicException.Parameter[]{
                    new BasicException.Parameter("objectId", parentObjectId)
                },
                "No class found for object id" 
            );          
        }
                
    }
    
    //-----------------------------------------------------------------------
    @Override
    protected String fromPath(
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
                new BasicException.Parameter[]{
                    new BasicException.Parameter("path", path)
                },
                "No type found for path" 
            );          
        }
        return objectId;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Path toPath(
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
              new BasicException.Parameter[]{
                  new BasicException.Parameter("objectId", objectId)
              },
              "No components found for object id" 
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
              new BasicException.Parameter[]{
                  new BasicException.Parameter("objectId", objectId)
              },
              "No type found for object id" 
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
                      new BasicException.Parameter[]{
                          new BasicException.Parameter("objectId", objectId),
                          new BasicException.Parameter("type", type)
                      },
                      "object id not valid for type" 
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
              new BasicException.Parameter[]{
                  new BasicException.Parameter("path", path)
              },
              "path size must be odd number" 
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
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/workReportEntry/:*"),
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
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/workReportEntry/:*"),
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
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/workReportEntry/:*"),
        new Path("xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/workReportEntry/:*"),
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

    public static final List[] CLASS_NAMES = {
        org.opencrx.kernel.account1.jdo2.Organization.CLASS,
        org.opencrx.security.identity1.jdo2.Segment.CLASS,
        org.openmdx.security.realm1.jdo2.Segment.CLASS,
        org.opencrx.kernel.uom1.jdo2.UomSchedule.CLASS,
        org.opencrx.kernel.uom1.jdo2.Uom.CLASS,
        org.openmdx.security.realm1.jdo2.Principal.CLASS,
        org.openmdx.security.authorization1.jdo2.Policy.CLASS,
        (List<String>)null,
        org.opencrx.kernel.contract1.jdo2.Lead.CLASS,
        org.openmdx.security.realm1.jdo2.Role.CLASS,
        org.openmdx.security.realm1.jdo2.Permission.CLASS,
        org.opencrx.kernel.workflow1.jdo2.AbstractTask.CLASS,
        org.opencrx.kernel.product1.jdo2.Product.CLASS,
        org.openmdx.security.authorization1.jdo2.Privilege.CLASS,
        org.opencrx.security.identity1.jdo2.Subject.CLASS,
        org.openmdx.security.authentication1.jdo2.Segment.CLASS,
        org.opencrx.kernel.document1.jdo2.Segment.CLASS,
        org.openmdx.security.authentication1.jdo2.Credential.CLASS,
        org.openmdx.security.authentication1.jdo2.Segment.CLASS,
        org.opencrx.kernel.account1.jdo2.RevenueReport.CLASS,
        org.opencrx.kernel.home1.jdo2.Subscription.CLASS,
        org.openmdx.security.realm1.jdo2.Segment.CLASS,
        org.openmdx.security.authorization1.jdo2.Segment.CLASS,
        org.opencrx.kernel.activity1.jdo2.Activity.CLASS,
        org.opencrx.kernel.workflow1.jdo2.Topic.CLASS,
        org.opencrx.kernel.activity1.jdo2.AbstractEmailRecipient.CLASS,
        org.opencrx.kernel.activity1.jdo2.AbstractMailingRecipient.CLASS,
        org.opencrx.kernel.code1.jdo2.AbstractEntry.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductBasePrice.CLASS,
        org.opencrx.kernel.code1.jdo2.CodeValueContainer.CLASS,
        org.opencrx.kernel.activity1.jdo2.MeetingParty.CLASS,
        org.opencrx.kernel.document1.jdo2.Document.CLASS,
        org.opencrx.kernel.home1.jdo2.EmailAccount.CLASS,
        org.opencrx.kernel.account1.jdo2.ContactRelationship.CLASS,
        org.opencrx.kernel.activity1.jdo2.AbstractPhoneCallRecipient.CLASS,
        org.opencrx.kernel.activity1.jdo2.MeetingParty.CLASS,
        org.opencrx.kernel.building1.jdo2.Building.CLASS,
        org.opencrx.kernel.activity1.jdo2.Activity.CLASS,
        org.opencrx.kernel.product1.jdo2.SalesTaxType.CLASS,
        org.opencrx.kernel.product1.jdo2.AccountAssignment.CLASS,
        org.opencrx.kernel.product1.jdo2.PriceLevel.CLASS,
        org.opencrx.kernel.account1.jdo2.Competitor.CLASS,
        org.opencrx.kernel.building1.jdo2.BuildingUnit.CLASS,
        org.opencrx.kernel.activity1.jdo2.AddressGroupMember.CLASS,
        org.opencrx.kernel.activity1.jdo2.TaskParty.CLASS,
        org.opencrx.kernel.activity1.jdo2.IncidentParty.CLASS,
        org.opencrx.kernel.product1.jdo2.PriceModifier.CLASS,
        org.opencrx.kernel.activity1.jdo2.Activity.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityTracker.CLASS,
        org.opencrx.kernel.building1.jdo2.Segment.CLASS,
        org.opencrx.kernel.account1.jdo2.OrganizationalUnitRelationship.CLASS,
        org.opencrx.kernel.forecast1.jdo2.Budget.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFollowUp.CLASS,
        org.opencrx.kernel.activity1.jdo2.AddressGroup.CLASS,
        org.opencrx.kernel.activity1.jdo2.ResourceAssignment.CLASS,
        org.opencrx.kernel.code1.jdo2.Segment.CLASS,
        org.opencrx.kernel.product1.jdo2.Segment.CLASS,
        org.opencrx.kernel.admin1.jdo2.Segment.CLASS,
        (List<String>)null,
        org.opencrx.kernel.account1.jdo2.SearchIndexEntry.CLASS,
        org.opencrx.kernel.product1.jdo2.PriceListEntry.CLASS,
        org.opencrx.kernel.home1.jdo2.WfActionLogEntry.CLASS,
        org.opencrx.kernel.reservation1.jdo2.Segment.CLASS,
        org.opencrx.kernel.building1.jdo2.BuildingComplex.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityCategory.CLASS,
        (List<String>)null,
        org.opencrx.kernel.account1.jdo2.Account.CLASS,
        org.opencrx.kernel.home1.jdo2.WfProcessInstance.CLASS,
        org.opencrx.kernel.account1.jdo2.OrganizationalUnit.CLASS,
        org.opencrx.kernel.activity1.jdo2.EffortEstimate.CLASS,
        org.opencrx.kernel.contract1.jdo2.ProductApplication.CLASS,
        org.opencrx.kernel.account1.jdo2.Segment.CLASS,
        org.opencrx.kernel.model1.jdo2.Segment.CLASS,
        org.opencrx.kernel.depot1.jdo2.Segment.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityWorkRecord.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityVote.CLASS,
        org.opencrx.kernel.activity1.jdo2.Activity.CLASS,
        org.opencrx.kernel.generic.jdo2.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jdo2.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jdo2.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.generic.jdo2.AdditionalExternalLink.CLASS,
        org.opencrx.kernel.account1.jdo2.AccountFilterGlobal.CLASS,
        org.opencrx.kernel.account1.jdo2.AccountFilterProperty.CLASS,
        org.opencrx.kernel.forecast1.jdo2.Segment.CLASS,
        org.opencrx.kernel.account1.jdo2.ContactMembership.CLASS,
        org.opencrx.kernel.account1.jdo2.ContactMembership.CLASS,
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable")),
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable")),
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable")),
        (List<String>)null,
        org.opencrx.kernel.workflow1.jdo2.Segment.CLASS,
        org.opencrx.kernel.uom1.jdo2.Segment.CLASS,
        org.opencrx.kernel.activity1.jdo2.Segment.CLASS,
        org.opencrx.kernel.home1.jdo2.Segment.CLASS,
        org.opencrx.kernel.contract1.jdo2.Segment.CLASS,
        (List<String>)null,
        org.openmdx.security.realm1.jdo2.Segment.CLASS,
        org.opencrx.security.identity1.jdo2.Segment.CLASS,
        org.openmdx.security.authorization1.jdo2.Segment.CLASS,
        org.opencrx.kernel.contract1.jdo2.DeliveryInformation.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityCreator.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFilterGlobal.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFilterProperty.CLASS,
        org.opencrx.kernel.generic.jdo2.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jdo2.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jdo2.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jdo2.DocumentAttachment.CLASS,
        org.opencrx.kernel.generic.jdo2.Rating.CLASS,
        org.opencrx.kernel.generic.jdo2.Rating.CLASS,
        org.opencrx.kernel.generic.jdo2.Rating.CLASS,
        org.opencrx.kernel.generic.jdo2.Rating.CLASS,
        org.opencrx.kernel.generic.jdo2.Note.CLASS,
        org.opencrx.kernel.generic.jdo2.Note.CLASS,
        org.opencrx.kernel.generic.jdo2.Note.CLASS,
        org.opencrx.kernel.generic.jdo2.Note.CLASS,
        org.opencrx.kernel.contract1.jdo2.DeliveryRequest.CLASS,
        org.opencrx.kernel.contract1.jdo2.DeliveryRequest.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractOpportunityPosition.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityProcess.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityProcessState.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityProcessTransition.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityProcessAction.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityMilestone.CLASS,
        org.opencrx.kernel.account1.jdo2.CreditLimit.CLASS,
        org.opencrx.kernel.account1.jdo2.CreditLimit.CLASS,
        org.opencrx.kernel.generic.jdo2.Description.CLASS,
        org.opencrx.kernel.generic.jdo2.Description.CLASS,
        org.opencrx.kernel.generic.jdo2.Description.CLASS,
        org.opencrx.kernel.account1.jdo2.Member.CLASS,
        org.opencrx.kernel.generic.jdo2.Media.CLASS,
        org.opencrx.kernel.generic.jdo2.Media.CLASS,
        org.opencrx.kernel.generic.jdo2.Media.CLASS,
        org.opencrx.kernel.generic.jdo2.Media.CLASS,
        org.opencrx.kernel.home1.jdo2.UserHome.CLASS,
        org.opencrx.kernel.home1.jdo2.Alert.CLASS,
        org.opencrx.kernel.home1.jdo2.QuickAccess.CLASS,
        org.opencrx.kernel.home1.jdo2.AccessHistory.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityType.CLASS,
        org.opencrx.kernel.activity1.jdo2.Calendar.CLASS,
        org.opencrx.kernel.activity1.jdo2.Resource.CLASS,
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "contract1", "AbstractContract")),
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "contract1", "AbstractContract")),
        org.opencrx.kernel.activity1.jdo2.WorkReportEntry.CLASS,
        org.opencrx.kernel.activity1.jdo2.Activity.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractQuotePosition.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFilterGroup.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFilterProperty.CLASS,
        org.opencrx.kernel.activity1.jdo2.ProductReference.CLASS,
        org.opencrx.kernel.base.jdo2.Property.CLASS,
        org.opencrx.kernel.base.jdo2.AuditEntry.CLASS,
        org.opencrx.kernel.product1.jdo2.Product.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityGroupAssignment.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityLinkTo.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityLinkFrom.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractSalesOrderPosition.CLASS,
        org.opencrx.kernel.activity1.jdo2.ActivityFollowUp.CLASS,
        org.opencrx.kernel.activity1.jdo2.WeekDay.CLASS,
        org.opencrx.kernel.activity1.jdo2.CalendarDay.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractInvoicePosition.CLASS,
        org.opencrx.kernel.model1.jdo2.Element.CLASS,
        org.opencrx.kernel.depot1.jdo2.BookingText.CLASS,
        org.opencrx.kernel.ras1.jdo2.Profile.CLASS,
        org.opencrx.kernel.product1.jdo2.RelatedProduct.CLASS,
        org.opencrx.kernel.ras1.jdo2.ClassificationElement.CLASS,
        org.opencrx.kernel.ras1.jdo2.Descriptor.CLASS,
        org.opencrx.kernel.ras1.jdo2.SolutionPart.CLASS,
        org.opencrx.kernel.ras1.jdo2.ArtifactDependency.CLASS,
        org.opencrx.kernel.ras1.jdo2.ArtifactContext.CLASS,
        org.opencrx.kernel.ras1.jdo2.VariabilityPoint.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotEntity.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotEntityRelationship.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotType.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotGroup.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotHolder.CLASS,
        org.opencrx.kernel.depot1.jdo2.CompoundBooking.CLASS,
        org.opencrx.kernel.depot1.jdo2.BookingPeriod.CLASS,
        org.opencrx.kernel.depot1.jdo2.SingleBooking.CLASS,
        org.opencrx.kernel.depot1.jdo2.SimpleBooking.CLASS,
        org.opencrx.kernel.depot1.jdo2.Depot.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotPosition.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductConfiguration.CLASS,
        org.opencrx.kernel.base.jdo2.Property.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotReport.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotReportItemPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.Opportunity.CLASS,
        org.opencrx.kernel.generic.jdo2.Description.CLASS,
        org.opencrx.kernel.generic.jdo2.Description.CLASS,
        org.opencrx.kernel.account1.jdo2.AccountFilterProperty.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductFilterProperty.CLASS,
        org.opencrx.kernel.document1.jdo2.DocumentFolder.CLASS,
        org.opencrx.kernel.document1.jdo2.DocumentRevision.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductConfiguration.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductConfiguration.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductBasePrice.CLASS,
        org.opencrx.kernel.contract1.jdo2.Quote.CLASS,
        org.opencrx.kernel.contract1.jdo2.SalesOrder.CLASS,
        org.opencrx.kernel.base.jdo2.Property.CLASS,
        org.opencrx.kernel.contract1.jdo2.Invoice.CLASS,
        org.opencrx.kernel.product1.jdo2.PricingRule.CLASS,
        org.opencrx.kernel.contract1.jdo2.CalculationRule.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotReference.CLASS,
        org.opencrx.kernel.depot1.jdo2.DepotReference.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySet.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySet.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySet.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySet.CLASS,
        org.opencrx.kernel.contract1.jdo2.ProductApplication.CLASS,
        org.opencrx.kernel.contract1.jdo2.DeliveryInformation.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductConfigurationTypeSet.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductConfigurationType.CLASS,
        org.opencrx.kernel.activity1.jdo2.WorkReportEntry.CLASS,
        org.opencrx.kernel.contract1.jdo2.ContractRole.CLASS,
        org.opencrx.kernel.contract1.jdo2.ContractRole.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.AbstractRemovedPosition.CLASS,
        org.opencrx.kernel.contract1.jdo2.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jdo2.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jdo2.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jdo2.PositionModification.CLASS,
        org.opencrx.kernel.contract1.jdo2.PositionModification.CLASS,
        org.openmdx.security.realm1.jdo2.Realm.CLASS,
        org.openmdx.security.realm1.jdo2.Principal.CLASS,
        org.openmdx.security.authorization1.jdo2.Policy.CLASS,
        org.openmdx.security.realm1.jdo2.Role.CLASS,
        org.openmdx.security.realm1.jdo2.Permission.CLASS,
        org.openmdx.security.authorization1.jdo2.Privilege.CLASS,
        org.opencrx.security.identity1.jdo2.Subject.CLASS,
        org.openmdx.security.authentication1.jdo2.Credential.CLASS,
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable")),
        org.opencrx.kernel.admin1.jdo2.ComponentConfiguration.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductClassification.CLASS,
        org.opencrx.kernel.product1.jdo2.ProductClassificationRelationship.CLASS,
        org.opencrx.kernel.account1.jdo2.AccountMembership.CLASS,
        org.opencrx.kernel.activity1.jdo2.WorkReportEntry.CLASS,
        org.opencrx.kernel.activity1.jdo2.WorkReportEntry.CLASS,
        org.opencrx.kernel.base.jdo2.IndexEntry.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySetEntry.CLASS,
        org.opencrx.kernel.generic.jdo2.PropertySetEntry.CLASS,
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "address1", "Addressable")),
        org.opencrx.kernel.building1.jdo2.Facility.CLASS,
        org.opencrx.kernel.building1.jdo2.Facility.CLASS,
        org.opencrx.kernel.building1.jdo2.Facility.CLASS,
        org.opencrx.kernel.building1.jdo2.Facility.CLASS,
        Collections.unmodifiableList(Arrays.asList("org", "opencrx", "kernel", "building1", "AbstractBuildingUnit")),
        org.opencrx.kernel.building1.jdo2.InventoryItem.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkTo.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.building1.jdo2.LinkableItemLinkFrom.CLASS,
        org.opencrx.kernel.product1.jdo2.AccountAssignmentProduct.CLASS,
        org.opencrx.kernel.depot1.jdo2.SingleBooking.CLASS,
        org.opencrx.kernel.account1.jdo2.AddressFilterGlobal.CLASS,
        org.opencrx.kernel.account1.jdo2.AddressFilterProperty.CLASS,
        org.opencrx.kernel.document1.jdo2.DocumentLink.CLASS,
        org.opencrx.kernel.document1.jdo2.DocumentLock.CLASS,
        org.opencrx.kernel.document1.jdo2.DocumentAttachment.CLASS,
        org.opencrx.kernel.home1.jdo2.ObjectFinder.CLASS,
        org.opencrx.kernel.activity1.jdo2.InvolvedObject.CLASS        
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
        "workReportEntry1",
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
        "workReportEntry2",
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
        "workReportEntry3",
        "workReportEntry4",
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
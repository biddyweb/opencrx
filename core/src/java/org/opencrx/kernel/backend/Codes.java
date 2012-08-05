/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Codes.java,v 1.3 2007/12/21 12:49:51 wfro Exp $
 * Description: Codes
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/12/21 12:49:51 $
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
package org.opencrx.kernel.backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.log.SysLog;

public class Codes
  implements Serializable {
  
    //-------------------------------------------------------------------------
    public Codes(
        RequestCollection delegation,
        Path codeSegmentIdentity
    ) throws ServiceException {
        this.channel = delegation;
        this.codeSegmentIdentity = codeSegmentIdentity;
        this.codeValueContainers = new HashMap();
        this.codeValueContainerIds = new HashMap();
        this.cachedEntries = new HashMap();
        this.shortTexts = new HashMap();
        this.longTexts = new HashMap();
        this.refresh();
    }

    //-------------------------------------------------------------------------
    public void markAsDirty(
    ) {
        this.cacheIsDirty = true;
    }

    //-------------------------------------------------------------------------
    private void refresh(
    ) throws ServiceException {        
        DataproviderObject_1_0 codeSegment = null;
        try {
            codeSegment = this.channel.addGetRequest(
                this.codeSegmentIdentity,
                AttributeSelectors.ALL_ATTRIBUTES,
                null
            );
        } catch(ServiceException e) {}
        // Only refresh if the code segment exists and if it was modified since last refresh
        if(this.cacheIsDirty && (codeSegment != null)) {
            this.codeValueContainers.clear();
            this.codeValueContainerIds.clear();
            this.cachedEntries.clear();            
            List valueContainers = this.channel.addFindRequest(
              codeSegmentIdentity.getChild("valueContainer"),
              null,
              AttributeSelectors.ALL_ATTRIBUTES,
              0,
              Integer.MAX_VALUE,
              Directions.ASCENDING      
            );
            for(
              Iterator i = valueContainers.iterator();
              i.hasNext();
            ) {
              DataproviderObject_1_0 valueContainer = (DataproviderObject_1_0)i.next();
              SysLog.detail("preparing code value container", valueContainer.path());
              this.codeValueContainers.put(
                  valueContainer.path().getBase(),
                  valueContainer
              );
              List name = valueContainer.values("name");
              for(Iterator j = name.iterator(); j.hasNext(); ) {
                this.codeValueContainerIds.put(
                  j.next(),
                  valueContainer.path().getBase()
                );
              } 
            }
            this.shortTexts.clear();
            this.longTexts.clear();
            this.cacheIsDirty = false;
        }
    }
  
  //-------------------------------------------------------------------------
  public DataproviderObject_1_0 getCodeValueContainerByName(
      String name
  ) {
      return (DataproviderObject_1_0)this.codeValueContainers.get(
          this.codeValueContainerIds.get(name)
      );      
  }
  
  //-------------------------------------------------------------------------
  public DataproviderObject_1_0 getCodeValueContainerById(
      String id
  ) {
      return (DataproviderObject_1_0)this.codeValueContainers.get(id);      
  }
  
  //-------------------------------------------------------------------------
  public List getCodeEntriesById(
      String id
  ) throws ServiceException {
      if(System.currentTimeMillis() > this.cachedEntriesExpirationAt) {
          this.refresh();
          this.cachedEntriesExpirationAt = System.currentTimeMillis() + this.codeCacheRefreshRate;
      }
      List codeEntries = (List)this.cachedEntries.get(id);
      if(codeEntries == null) {
          DataproviderObject_1_0 valueContainer = this.getCodeValueContainerById(id);
          if(valueContainer == null) {
            return null;
          }
          codeEntries = this.channel.addFindRequest(
            valueContainer.path().getChild("entry"),
            null,
            AttributeSelectors.ALL_ATTRIBUTES,
            0,
            Integer.MAX_VALUE,
            Directions.ASCENDING        
          );
          this.cachedEntries.put(
              id,
              codeEntries
          );
      }
      return codeEntries;
  }
  
  //-------------------------------------------------------------------------
  public List getCodeEntriesByName(
      String name
  ) throws ServiceException {
      return this.getCodeEntriesById(
          (String)this.codeValueContainerIds.get(name)
      );
  }
  
  //-------------------------------------------------------------------------
  public SortedMap getShortText(
    String name,
    short locale,
    boolean codeAsKey
  ) throws ServiceException {
    SortedMap shortTexts = null;
    if((shortTexts = (SortedMap)this.shortTexts.get(name + ":" + locale + ":" + codeAsKey)) == null) {
      List entries = this.getCodeEntriesByName(name);
      shortTexts = new TreeMap();
      for(Iterator i = entries.iterator(); i.hasNext(); ) {
        DataproviderObject_1_0 entry = (DataproviderObject_1_0)i.next();
        Short code = new Short((short)0);
        try {
          code = new Short(entry.path().getBase());
        } catch(Exception e) {}
        Object text = ((List)entry.values("shortText")).size() > locale
          ? ((List)entry.values("shortText")).get(locale)
          : ((List)entry.values("shortText")).get(0);          
        if(codeAsKey) {
          shortTexts.put(code, text);
        }
        else {
          shortTexts.put(text, code);
        }
      }
      this.shortTexts.put(
        name + ":" + locale + ":" + codeAsKey,
        shortTexts
      );
    }
    return shortTexts;   
  }
  
  //-------------------------------------------------------------------------
  public SortedMap getLongText(
    String name,
    short locale,
    boolean codeAsKey
  ) throws ServiceException {
    SortedMap longTexts = null;
    if((longTexts = (SortedMap)this.longTexts.get(name + ":" + locale + ":" + codeAsKey)) == null) {
      List entries = this.getCodeEntriesByName(name);
      longTexts = new TreeMap();      
      for(Iterator i = entries.iterator(); i.hasNext(); ) {
        DataproviderObject_1_0 entry = (DataproviderObject_1_0)i.next();
        Short code = new Short((short)0);
        try {
          code = new Short(entry.path().getBase());
        } catch(Exception e) {}
        Object text = 
          ((List)entry.values("longText")).size() > locale
            ? ((List)entry.values("longText")).get(locale)
            : ((List)entry.values("longText")).get(0);
        if(codeAsKey) {
          longTexts.put(code, text);
        }
        else {
          longTexts.put(text, code);
        }
      }
      this.longTexts.put(
        name + ":" + locale + ":" + codeAsKey,
        longTexts
      );
    }
    return longTexts;   
  }
  
  //-------------------------------------------------------------------------
  // Variables
  //-------------------------------------------------------------------------
  /**
    * Comment for <code>serialVersionUID</code>
    */
  private static final long serialVersionUID = 3256719593744381497L;

  private final RequestCollection channel;
  
  private final Path codeSegmentIdentity;
  
  // map containing the code value containers key=id, value=CodeValueContainer
  private final Map codeValueContainers;
  
  // map containing the attribute name -> code value container id mapping
  private final Map codeValueContainerIds;
  
  private final Map shortTexts;
  private final Map longTexts;
  private final Map cachedEntries;
  
  // Refresh cache
  private boolean cacheIsDirty = true;
  private long cachedEntriesExpirationAt = 0;
  private long codeCacheRefreshRate = 60000;
  
}

//--- End of File -----------------------------------------------------------

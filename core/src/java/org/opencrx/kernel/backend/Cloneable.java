/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: Cloneable.java,v 1.16 2008/09/02 12:27:56 wfro Exp $
 * Description: Cloneable
 * Revision:    $Revision: 1.16 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2008/09/02 12:27:56 $
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.jmi1.BasicObject;
import org.openmdx.base.text.format.DateFormat;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject_1_0;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.SystemAttributes;
import org.openmdx.compatibility.base.marshalling.Marshaller;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.kernel.exception.BasicException;
import org.openmdx.model1.accessor.basic.cci.ModelElement_1_0;
import org.openmdx.model1.code.AggregationKind;

public class Cloneable {

    //-------------------------------------------------------------------------
    public Cloneable(
        Backend backend
    ) {
        this.backend = backend;
    }
    
    //-------------------------------------------------------------------------
    protected Set<String> getReferenceFilter(
        String referenceFilterAsString
    ) {
        Set<String> referenceFilter = referenceFilterAsString == null ? null : new HashSet<String>();
        if(referenceFilter != null) {
            if(referenceFilterAsString != null) {
                StringTokenizer tokenizer = new StringTokenizer(referenceFilterAsString, " ;,", false);
                while(tokenizer.hasMoreTokens()) {
                    referenceFilter.add(
                        tokenizer.nextToken()
                    );
                }
            }
        }
        return referenceFilter;
    }
    
    //-------------------------------------------------------------------------
    public BasicObject cloneAndUpdateReferences(
        Path originalIdentity,
        String referenceFilterAsString,
        boolean replaceExisting
    ) throws ServiceException {
        DataproviderObject clonedObject = this.cloneAndUpdateReferences(
            this.backend.retrieveObject(
                originalIdentity
            ), 
            originalIdentity.getParent(),
            null,
            referenceFilterAsString, 
            replaceExisting,
            AttributeSelectors.ALL_ATTRIBUTES            
        );
        return clonedObject == null
            ? null
            : (BasicObject)this.backend.getDelegatingPkg().refObject(clonedObject.path().toXri());
    }
    
    //-------------------------------------------------------------------------
    /**
     * Model-driven object cloning. Copies the original object to the target
     * toContainer. If a corresponding marshaller is found, the orginal object
     * is marshalled before it is written to the target. The target is either
     * replaced or newly created. If cloneContent=true the operation is recursive,
     * i.e. the composite objects of original are cloned recursively. 
     */
    public DataproviderObject cloneAndUpdateReferences(
        DataproviderObject_1_0 original,
        Path toContainer,
        Map objectMarshallers,
        String referenceFilterAsString,
        boolean replaceExisting,
        short attributeSelector
    ) throws ServiceException {
        // Clone object
        Set<String> referenceFilter = this.getReferenceFilter(referenceFilterAsString);
        List replacements = new ArrayList();
        DataproviderObject cloned = this.cloneObject(
            original,
            toContainer,
            CLONE_EXCLUDE_ATTRIBUTES,
            objectMarshallers,
            referenceFilter,
            replacements,
            replaceExisting,
            attributeSelector
        );
        // Update references
        this.applyReplacements(
            cloned.path(),
            true,
            replacements,
            referenceFilter
        );
        return cloned;
    }
    
    //-------------------------------------------------------------------------
    /**
     * Add the objects referenced by object to referencedObjectPaths if the
     * referenced object paths match the reference filter. Update the reference
     * filter for the referenced objects which match the reference filter.
     */
    public void collectReferencedObjects(
        DataproviderObject_1_0 object,
        Set<String> referenceFilter,
        Set<Path> referencedObjectPaths
    ) throws ServiceException {
        Map<String,ModelElement_1_0> references = (Map)this.backend.getModel().getElement(
            object.values(SystemAttributes.OBJECT_CLASS).get(0)
        ).values("reference").get(0);        
        for(
            Iterator<String> i = object.attributeNames().iterator(); 
            i.hasNext(); 
        ) {
            String featureName = i.next();
            List<Object> values = object.values(featureName);
            for(Iterator<Object> j = values.iterator(); j.hasNext(); ) {
                Object value = j.next();
                if(value instanceof Path) {
                    boolean matches = referenceFilter == null;
                    if(!matches) {
                        ModelElement_1_0 featureDef = references.get(featureName);
                        if(featureDef != null) {
                            String qualifiedFeatureName = (String)featureDef.values("qualifiedName").get(0);
                            matches = 
                                referenceFilter.contains(featureName) ||
                                referenceFilter.contains(qualifiedFeatureName);
                        }
                    }
                    if(matches) {
                        referencedObjectPaths.add((Path)value);
                    }
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Model-driven object cloning. Copies the original object to the target
     * toContainer. If a corresponding marshaller is found, the orginal object
     * is marshalled before it is written to the target. The target is either
     * replaced or newly created. If cloneContent=true the operation is recursive,
     * i.e. the composite objects of original are cloned recursively. 
     */
    private DataproviderObject cloneObject(
        DataproviderObject_1_0 original,
        Path toContainer,
        Set excludeAttributes,
        Map objectMarshallers,
        Set<String> referenceFilter,
        List<DataproviderObject_1_0> replacements,
        boolean replaceExisting,
        short attributeSelector
    ) throws ServiceException {
        
        // Clone original
        String originalType = (String)original.values(SystemAttributes.OBJECT_CLASS).get(0);
        DataproviderObject clone = null;
        if((objectMarshallers != null) && (objectMarshallers.get(originalType) != null)) {
            clone = (DataproviderObject)((Marshaller)objectMarshallers.get(originalType)).marshal(
                original
            );
        }
        else {
            clone = new DataproviderObject(new Path(""));
            clone.addClones(
              original,
              true
            );
            // By default remove security settings of original. 
            // They will be set automatically by access control for the clone
            clone.attributeNames().remove("owningUser");
            clone.attributeNames().remove("owningGroup");
        }
        
        // Cloned object has same qualifier as original in case of replacement. Otherwise
        // try to keep qualifier if length is < MANUAL_QUALIFIER_THRESHOLD and toContainer
        // is different from original path
        clone.path().setTo(
            toContainer.getChild(
                replaceExisting
                    ? original.path().getBase()
                    : !original.path().startsWith(toContainer) && (original.path().getBase().length() < MANUAL_QUALIFIER_THRESHOLD) 
                        ? original.path().getBase() 
                        : this.backend.getUidAsString()
            )
        );
        // Create ReferenceReplacement. References to cloned objects must be updated
        DataproviderObject replacement = new DataproviderObject(new Path("xri:@openmdx:*"));
        replacement.values(SystemAttributes.OBJECT_CLASS).add("org:opencrx:kernel:base:ReferenceReplacement");
        replacement.values("oldReference").add(
            new Path((String)original.values(SystemAttributes.OBJECT_IDENTITY).get(0))
        );
        replacement.values("newReference").add(clone.path());
        replacements.add(replacement);        
        // Exclude attributes
        if(excludeAttributes != null) {
            clone.attributeNames().removeAll(excludeAttributes);
        }
        // Remove unknown and readonly features
        try {
            ModelElement_1_0 classDef = this.backend.getModel().getElement(originalType);
            for(
                Iterator i = clone.attributeNames().iterator();
                i.hasNext();
            ) {
                String featureName = (String)i.next();
                ModelElement_1_0 featureDef = this.backend.getModel().getFeatureDef(classDef, featureName, false);
                String qualifiedFeatureName = featureDef == null
                    ? null
                    : (String)featureDef.values("qualifiedName").get(0);
                if(
                    !(SystemAttributes.OBJECT_CLASS.equals(featureName) || CLONEABLE_READONLY_FEATURES.contains(qualifiedFeatureName)) &&
                    ((featureDef == null) || 
                    !((Boolean)featureDef.values("isChangeable").get(0)).booleanValue())
                ) {
                    i.remove();                    
                }                
            }            
        } catch(Exception e) {}
        
        // Either replace existing object with clone or create clone as new object 
        if(replaceExisting) {
            try {
                DataproviderObject existing = this.backend.retrieveObjectForModification(
                    clone.path()
                );
                existing.attributeNames().clear();
                existing.addClones(clone, true);
            }
            catch(ServiceException e) {
                // create object if not found
                if(e.getExceptionCode() == BasicException.Code.NOT_FOUND) {
                    this.backend.getDelegatingRequests().addCreateRequest(
                        clone
                    );                  
                }
            }
        }
        else {
            this.backend.getDelegatingRequests().addCreateRequest(
                clone
            );
        }
        // Clone content (shared and composite)
        Map<String,ModelElement_1_0> references = (Map)this.backend.getModel().getElement(
            original.values(SystemAttributes.OBJECT_CLASS).get(0)
        ).values("reference").get(0);
        for(
            Iterator<ModelElement_1_0> i = references.values().iterator();
            i.hasNext();
        ) {
            ModelElement_1_0 featureDef = i.next();
            ModelElement_1_0 referencedEnd = this.backend.getModel().getElement(
                featureDef.values("referencedEnd").get(0)
            );
            boolean referenceIsCompositeAndChangeable = 
                this.backend.getModel().isReferenceType(featureDef) &&
                AggregationKind.COMPOSITE.equals(referencedEnd.values("aggregation").get(0)) &&
                ((Boolean)referencedEnd.values("isChangeable").get(0)).booleanValue();
            boolean referenceIsSharedAndChangeable = 
                this.backend.getModel().isReferenceType(featureDef) &&
                AggregationKind.SHARED.equals(referencedEnd.values("aggregation").get(0)) &&
                ((Boolean)referencedEnd.values("isChangeable").get(0)).booleanValue();
            
            // Only navigate changeable references which are either 'composite' or 'shared'
            // Do not navigate references with aggregation 'none'.
            if(referenceIsCompositeAndChangeable || referenceIsSharedAndChangeable) {
                String referenceName = (String)featureDef.values("name").get(0);
                boolean matches = referenceFilter == null;
                if(!matches) {
                    String qualifiedReferenceName = (String)featureDef.values("qualifiedReferenceName").get(0);
                    matches =
                        referenceFilter.contains(referenceName) ||
                        referenceFilter.contains(qualifiedReferenceName);
                }
                if(matches) {                                    
                    List content = this.backend.getDelegatingRequests().addFindRequest(
                        original.path().getChild(referenceName),
                        null,
                        attributeSelector,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING
                    );
                    for(
                        Iterator<DataproviderObject> j = content.iterator();
                        j.hasNext();
                    ) {
                        DataproviderObject contained = j.next();
                        Path containedIdentity = new Path((String)contained.values(SystemAttributes.OBJECT_IDENTITY).get(0));
                        this.cloneObject(
                            contained,
                            // Contained is a composite to original if its access path matches its identity
                            // In this case case add the clone of contained as child of clone. Otherwise
                            // add the clone of contained to its composite parent
                            contained.path().equals(containedIdentity)
                                ? clone.path().getChild(referenceName)
                                : containedIdentity.getParent(),
                            excludeAttributes,
                            objectMarshallers,
                            referenceFilter,
                            replacements,
                            replaceExisting,
                            attributeSelector
                        );
                    }
                }
            }
        }
        return clone;
    }

    //-------------------------------------------------------------------------
    /**
     * Applies the replacements to object and its content including the referenced
     * objects specified by the reference filter.
     * @param isChangeable object is changeable. Replacements are applied to changeble
     *        objects only.
     * @param replacements list of TemplateReplacement
     * @param baseValues map of 'basedOn' attribute names/values.
     * 
     * @return number of replacements
     */
    private int applyReplacements(
        Path objectIdentity,
        boolean isChangeable,
        List replacements,
        Set<String> referenceFilter
    ) throws ServiceException {

        int numberOfReplacements = 0;
        DataproviderObject_1_0 object = null;
        try {
            object = this.backend.retrieveObject(objectIdentity);
        } catch(Exception e) {}
        if(object == null) return 0;
        
        if(isChangeable) {
            DataproviderObject replacedObject = null;
            for(
                Iterator i = object.attributeNames().iterator();
                i.hasNext();
            ) {
                String name = (String)i.next();
                Object oldValue = object.values(name).get(0);
                Object newValue = null;
                for(
                    Iterator j = replacements.iterator();
                    j.hasNext();
                ) {
                    DataproviderObject_1_0 replacement = (DataproviderObject_1_0)j.next();
                    String replacementType = (String)replacement.values(SystemAttributes.OBJECT_CLASS).get(0);
                    // matches?
                    boolean matches = replacement.getValues("name") != null
                        ? replacement.values("name").contains(name)
                        : true;
                    // StringReplacement
                    if(
                        matches &&
                        (oldValue instanceof String) && 
                        "org:opencrx:kernel:base:StringReplacement".equals(replacementType)
                    ) {
                        matches &= (replacement.getValues("oldString") != null) && (replacement.getValues("oldString").size() > 0) 
                            ? oldValue.equals(replacement.values("oldString").get(0))
                            : true;
                        newValue = replacement.values("newString").get(0);
                    }
                    // number
                    else if(
                        matches &&
                        (oldValue instanceof Comparable) && 
                        "org:opencrx:kernel:base:NumberReplacement".equals(replacementType)
                    ) {                    
                        matches &= (replacement.getValues("oldNumber") != null) && (replacement.getValues("oldNumber").size() > 0)
                            ? ((Comparable)oldValue).compareTo(replacement.values("oldNumber").get(0)) == 0
                            : true;
                        newValue = replacement.values("newNumber").get(0);
                    }
                    // DateTimeReplacement
                    else if(
                        matches &&
                        "org:opencrx:kernel:base:DateTimeReplacement".equals(replacementType)
                    ) {                    
                        matches &= (replacement.getValues("oldDateTime") != null) && (replacement.getValues("oldDateTime").size() > 0)
                            ? oldValue.equals(replacement.values("oldDateTime").get(0))
                            : true;
                        if(
                            (replacement.getValues("baseDateTime") != null) && 
                            (replacement.values("baseDateTime").size() > 0)
                        ) {
                            try {
                                DateFormat dateFormat = DateFormat.getInstance();                            
                                Date baseDate = dateFormat.parse((String)replacement.values("baseDateTime").get(0));
                                Date oldDate = dateFormat.parse((String)oldValue);
                                Date newDate = dateFormat.parse((String)replacement.values("newDateTime").get(0)); 
                                newValue = dateFormat.format(new Date(newDate.getTime() + (oldDate.getTime() - baseDate.getTime())));
                            }
                            catch(ParseException e) {
                                newValue = replacement.values("newDateTime").get(0);                                
                            }
                        }
                        else {
                            newValue = replacement.values("newDateTime").get(0);
                        }
                    }
                    // BooleanReplacement
                    else if(
                        matches &&
                        (oldValue instanceof Boolean) &&
                        "org:opencrx:kernel:base:BooleanReplacement".equals(replacementType)
                    ) {                    
                        matches &= (replacement.getValues("oldBoolean") != null) && (replacement.getValues("oldBoolean").size() > 0)
                            ? oldValue.equals(replacement.values("oldBoolean").get(0))
                            : true;
                        newValue = replacement.values("newBoolean").get(0);
                    }
                    // ReferenceReplacement
                    else if(
                        matches &&
                        (oldValue instanceof Path) &&
                        "org:opencrx:kernel:base:ReferenceReplacement".equals(replacementType)
                    ) {                    
                        matches &= (replacement.getValues("oldReference") != null) && (replacement.getValues("oldReference").size() > 0)
                            ? oldValue.equals(replacement.values("oldReference").get(0))
                            : true;
                        newValue = replacement.values("newReference").get(0);
                    }
                    else {
                        matches = false;
                    }
                    // Replace
                    if(matches) {
                        if(replacedObject == null) {
                            replacedObject = this.backend.retrieveObjectForModification(
                                object.path()
                            );
                        }
                        replacedObject.clearValues(name);
                        if(newValue != null) {
                            replacedObject.values(name).add(newValue);
                            numberOfReplacements++;
                        }
                    }
                }
            }
        }
            
        // Apply replacements to composite objects
        Map references = (Map)this.backend.getModel().getElement(
            object.values(SystemAttributes.OBJECT_CLASS).get(0)
        ).values("reference").get(0);
        for(
            Iterator i = references.values().iterator();
            i.hasNext();
        ) {
            ModelElement_1_0 featureDef = (ModelElement_1_0)i.next();
            ModelElement_1_0 referencedEnd = this.backend.getModel().getElement(
                featureDef.values("referencedEnd").get(0)
            );            
            boolean referenceIsCompositeAndChangeable = 
                this.backend.getModel().isReferenceType(featureDef) &&
                AggregationKind.COMPOSITE.equals(referencedEnd.values("aggregation").get(0)) &&
                ((Boolean)referencedEnd.values("isChangeable").get(0)).booleanValue();
            boolean referenceIsSharedAndChangeable = 
                this.backend.getModel().isReferenceType(featureDef) &&
                AggregationKind.SHARED.equals(referencedEnd.values("aggregation").get(0)) &&
                ((Boolean)referencedEnd.values("isChangeable").get(0)).booleanValue();                
            // Only navigate changeable references which are either 'composite' or 'shared'
            // Do not navigate references with aggregation 'none'.
            if(referenceIsCompositeAndChangeable || referenceIsSharedAndChangeable) {
                String featureName = (String)featureDef.values("name").get(0);
                boolean matches = referenceFilter == null;
                if(!matches) {
                    String qualifiedFeatureName = (String)featureDef.values("qualifiedName").get(0);
                    matches =
                        referenceFilter.contains(featureName) ||
                        referenceFilter.contains(qualifiedFeatureName);
                }
                if(matches) {                
                    List content = this.backend.getDelegatingRequests().addFindRequest(
                        objectIdentity.getChild(featureName),
                        null,
                        AttributeSelectors.ALL_ATTRIBUTES,
                        0,
                        Integer.MAX_VALUE,
                        Directions.ASCENDING
                    );
                    for(
                        Iterator<DataproviderObject_1_0> j = content.iterator();
                        j.hasNext();
                    ) {
                        DataproviderObject_1_0 composite = j.next();
                        numberOfReplacements += this.applyReplacements(
                            composite.path(),
                            ((Boolean)referencedEnd.values("isChangeable").get(0)).booleanValue(),
                            replacements,
                            referenceFilter
                        );
                    }
                }
            }
        }        
        return numberOfReplacements;
    }
    
    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------    
    public static final Set<String> CLONE_EXCLUDE_ATTRIBUTES =
        new HashSet<String>(Arrays.asList("activityNumber"));
    
    public static final Set<String> CLONE_EXCLUDE_COMPOSITE_REFERENCES =
        new HashSet<String>(Arrays.asList("view"));
    
    public static final Set<String> CLONEABLE_READONLY_FEATURES =
        new HashSet<String>(Arrays.asList(
            "org:opencrx:kernel:contract1:ContractPosition:lineItemNumber",
            "org:opencrx:kernel:product1:ProductDescriptor:product",
            "org:opencrx:kernel:account1:AbstractAccount:fullName",
            "org:opencrx:kernel:product1:ProductConfigurationSet:configType",
            "org:opencrx:kernel:product1:ProductConfiguration:configType",
            "org:opencrx:kernel:activity1:Activity:ical"
        ));
    
    public static final int MANUAL_QUALIFIER_THRESHOLD = 10;
    
    protected final Backend backend;
}

//--- End of File -----------------------------------------------------------

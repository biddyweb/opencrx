/*
 * ====================================================================
 * Project:     openCRX/Store, http://www.opencrx.org/
 * Name:        $Id: ObjectCollection.java,v 1.1 2009/02/14 17:22:42 wfro Exp $
 * Description: ObjectCollection
 * Revision:    $Revision: 1.1 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/02/14 17:22:42 $
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
package org.opencrx.apps.store.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Collection of objects
 * 
 * @author OAZM (initial implementation)
 * @author WFRO (port to openCRX)
 */
public final class ObjectCollection implements IObject
{
    private static final long serialVersionUID = -299190041086447015L;
    
    private final ArrayList keys;
    private final ArrayList objects;

    public ObjectCollection()
    {
        keys = new ArrayList();
        objects = new ArrayList();
    }

    public ObjectCollection(final int size)
    {
        keys = new ArrayList(size);
        objects = new ArrayList(size);
    }

    private int getObjectIndex(final Object key)
    {
        return keys.indexOf(key);
    }

    public final int size()
    {
        return keys.size();
    }

    public final boolean isEmpty()
    {
        return keys.isEmpty();
    }

    public final boolean containsKey(final Object key)
    {
        return keys.contains(key);
    }

    public final boolean containsValue(final Object value)
    {
        return objects.contains(value);
    }

    public final Object get(final Object key)
    {
        if (false == keys.contains( key ))
            return null;
        return objects.get(this.getObjectIndex(key));
    }

    public final Object put(final Object key, final Object value)
    {
        final Object oldObject = this.get(key);

        keys.add(key);
        objects.add(value);

        return oldObject;
    }

    public final Object remove(final Object key)
    {
        if (!keys.contains(key))
            return null;

        final int intObjectIndex = this.getObjectIndex(key);
        keys.remove(intObjectIndex);
        objects.remove(intObjectIndex);

        return null;
    }

    public final void putAll(final ObjectCollection t)
    {
        this.keys.addAll(t.keys());
        this.objects.addAll(t.objects());
    }

    public final void clear()
    {
        keys.clear();
        objects.clear();
    }

    public final List keys()
    {
        return this.keys;
    }

    public final List objects()
    {
        return this.objects;
    }

    /**
     * Extracts items from the collection using start index and end index and returns a
     * new collection which contains items of specified range.
     *
     * @param startIndex Start position
     * @param endIndex   End position
     * @return Returns the new collection which contains items from specified range.
     */
    public final ObjectCollection extract(final int startIndex, final int endIndex)
    {
        final int count = (endIndex - startIndex);
        final ObjectCollection col = new ObjectCollection(count);

        col.keys().addAll(this.keys.subList(startIndex, endIndex));
        col.objects().addAll(this.objects.subList(startIndex, endIndex));

        return col;
    }

    public final String toString()
    {
        final StringBuffer buf = new StringBuffer(this.size() * 10);
        buf.append('[');
        buf.append(this.size());
        final Iterator i = this.keys().iterator();
        while (i.hasNext())
        {
            final Object key = i.next();
            buf.append('{');
            buf.append(key);
            buf.append('=');
            final Object value = this.get(key);
            buf.append(value);
            buf.append('}');
            buf.append('|');
        }

        buf.append(']');

        return buf.toString();
    }

    public final int compareTo(final Object o)
    {
        return 0;
    }

}
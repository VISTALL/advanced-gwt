/*
 * Copyright 2008 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwt.advanced.client.datamodel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a list that has an unique identifier.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class IdentifiedList extends ArrayList {
    /** list identifier */
    private String identifier;

    /**
     * Constructs a new IdentifiedList.
     *
     * @param initialCapacity is an initial capacity value.
     */
    public IdentifiedList (int initialCapacity) {
        super(initialCapacity);
        this.identifier = generateUniqueString();
    }

    /** Constructs a new IdentifiedList. */
    public IdentifiedList () {
        super();
        this.identifier = generateUniqueString();
    }

    /**
     * Constructs a new IdentifiedList.
     *
     * @param c is an initial collection.
     */
    public IdentifiedList (Collection c) {
        super(c);
        this.identifier = generateUniqueString();
    }

    /**
     * Getter for property 'identifier'.
     *
     * @return Value for property 'identifier'.
     */
    public String getIdentifier () {
        return identifier;
    }

    /**
     * Setter for property 'identifier'.
     *
     * @param identifier Value to set for property 'identifier'.
     */
    protected void setIdentifier (String identifier) {
        this.identifier = identifier;
    }

    /**
     * This method egnerates an unique string.
     *
     * @return is a nunique string.
     */
    protected String generateUniqueString() {
        StringBuffer result = new StringBuffer(String.valueOf(System.currentTimeMillis()));
        result.append(Math.random());
        return result.toString();
    }
}

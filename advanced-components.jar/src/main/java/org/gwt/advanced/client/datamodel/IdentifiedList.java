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

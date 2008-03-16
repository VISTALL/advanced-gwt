package org.gwt.advanced.client.datamodel;

/**
 * This interface describes lazily loadable data models.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface LazyLoadable {
    /**
     * This method sets a total row count for a lazy loadable model.
     *
     * @param totalRowCount is a total row count.
     */
    void setTotalRowCount (int totalRowCount);
}

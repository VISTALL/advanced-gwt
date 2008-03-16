package org.gwt.advanced.client.datamodel;

/**
 * This class is a subgrid key.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class SubgridKey {
    /**
     * row identifier
     */
    private String rowIdentifier;
    /**
     * column number
     */
    private int columnNumber;

    /**
     * Creates an instance of this class.
     *
     * @param rowIdentifier    is a row identifier.
     * @param columnNumber is a column number.
     */
    public SubgridKey (String rowIdentifier, int columnNumber) {
        this.rowIdentifier = rowIdentifier;
        this.columnNumber = columnNumber;
    }

    /**
     * Getter for property 'rowIdentifier'.
     *
     * @return Value for property 'rowIdentifier'.
     */
    public String getRowIdentifier () {
        return rowIdentifier;
    }

    /**
     * Getter for property 'columnNumber'.
     *
     * @return Value for property 'columnNumber'.
     */
    public int getColumnNumber () {
        return columnNumber;
    }

    /** {@inheritDoc} */
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null) return false;

        SubgridKey that = (SubgridKey) o;

        if (columnNumber != that.columnNumber) return false;
        if (rowIdentifier != null
            ? !rowIdentifier.equals(that.rowIdentifier)
            : that.rowIdentifier != null) return false;

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode () {
        int result;
        result = (rowIdentifier != null ? rowIdentifier.hashCode() : 0);
        result = 31 * result + columnNumber;
        return result;
    }
}
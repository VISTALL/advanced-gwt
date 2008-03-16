package org.gwt.advanced.client.datamodel;

/**
 * This is a simple not-editable data model.<p>
 * Use it for quick tests and for simple grids.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class SimpleGridDataModel implements GridDataModel {
    /** data set */
    private Object[][] data = new Object[0][0];
    /** sort column number */
    private int sortColumn;
    /** sort direction */
    private boolean ascending = true;
    /** page size */
    private int pageSize = 20;
    /** current page number */
    private int currentPageNumber = 0;
    /** displayed pages number */
    private int displayedPages = 10;

    /**
     * Creates an instance of this class and initializes it with the specified data set.
     *
     * @param data is a data set.
     */
    public SimpleGridDataModel (Object[][] data) {
        if (data != null)
            this.data = data;
    }

    /** {@inheritDoc} */
    public int getTotalRowCount () {
        return getData().length;
    }

    /** {@inheritDoc} */
    public int getStartRow () {
        return getCurrentPageNumber() * getPageSize();
    }

    /** {@inheritDoc} */
    public int getEndRow () {
        return Math.min(getStartRow() + getPageSize() - 1, getTotalRowCount() - 1);
    }

    /** {@inheritDoc} */
    public int getSortColumn () {
        return sortColumn;
    }

    /** {@inheritDoc} */
    public boolean isAscending () {
        return ascending;
    }

    /** {@inheritDoc} */
    public Object[] getRowData (int rowNumber) {
        Object[] row = getData()[rowNumber];
        if (row == null)
            row = new Object[0];

        return row;
    }

    /** {@inheritDoc} */
    public void setSortColumn (int sortColumn) {
        this.sortColumn = sortColumn;
    }


    /** {@inheritDoc} */
    public void setAscending (boolean ascending) {
        this.ascending = ascending;
    }

    /** {@inheritDoc} */
    public boolean isEmpty () {
        return getTotalRowCount() == 0;
    }

    /**
     * Getter for property 'data'.
     *
     * @return Value for property 'data'.
     */
    public Object[][] getData () {
        return data;
    }

    /** {@inheritDoc} */
    public void setPageSize (int pageSize) {
        if (pageSize < 0)
            throw new IllegalArgumentException("Page size must be greater then zero");

        this.pageSize = pageSize;
    }

    /** {@inheritDoc} */
    public void setCurrentPageNumber (int currentPageNumber) throws IllegalArgumentException {
        if (currentPageNumber < 0)
            throw new IllegalArgumentException("Page number must be greater then zero");
        if (!isEmpty() && getTotalPagesNumber() <= currentPageNumber)
            throw new IllegalArgumentException("Page number must be less then total pages number");

        this.currentPageNumber = currentPageNumber;
    }

    /** {@inheritDoc} */
    public int getDisplayedPages () {
        return displayedPages;
    }

    /** {@inheritDoc} */
    public void setDisplayedPages (int displayedPages) {
        this.displayedPages = displayedPages;
    }

    /** {@inheritDoc} */
    public int getTotalPagesNumber () {
        int rowCount = getTotalRowCount();
        int pageSize = getPageSize();
        int result = rowCount / pageSize;

        return result + (result * pageSize == rowCount ? 0 : 1);
    }

    /** {@inheritDoc} */
    public int getStartPage () {
        int startPage = 0;

        if (getCurrentPageNumber() >= getDisplayedPages())
            startPage = getCurrentPageNumber() / getDisplayedPages() * getDisplayedPages();

        return startPage;
    }

    /** {@inheritDoc} */
    public int getEndPage () {
        int endPage;

        if (getStartPage() + getDisplayedPages() > getTotalPagesNumber() )
            endPage = getTotalPagesNumber() - 1;
        else
            endPage = getStartPage() + getDisplayedPages() - 1;

        if (endPage < 0)
            endPage = 0;
        
        return endPage;
    }

    /** {@inheritDoc} */
    public int getPageSize () {
        return pageSize;
    }

    /** {@inheritDoc} */
    public int getCurrentPageNumber () {
        return currentPageNumber;
    }

    /** {@inheritDoc} */
    public String toString () {
        Object[][] data = getData();
        String result = "";
        for (int i = 0; i < data.length; i++) {
            Object[] row = data[i];
            for (int j = 0; j < row.length; j++) {
                Object cell = row[j];
                result += String.valueOf(cell) + " ";
            }
            result += "\n";
        }
        return result; 
    }
}

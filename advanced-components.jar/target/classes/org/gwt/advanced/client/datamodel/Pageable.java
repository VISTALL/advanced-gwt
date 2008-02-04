package org.gwt.advanced.client.datamodel;

/**
 * This interface describes pageable object (usually row set of the grid).<p>
 * Implementations of this inteface are usually used by pagers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface Pageable {
    /**
     * This method sets a page size.
     *
     * @param pageSize is a page size.
     */
    void setPageSize(int pageSize);

    /**
     * This method sets the current page number.
     *
     * @param currentPageNumber is a current page number.
     *
     * @throws IllegalArgumentException if current page number less then zero or greater then
     *                                  actual number of pages.
     */
    void setCurrentPageNumber(int currentPageNumber) throws IllegalArgumentException;

    /**
     * This method returns a number of existing pages.
     *
     * @return a total pages number.
     */
    int getTotalPagesNumber();

    /**
     * This method returns a start page number.
     *
     * @return is a start page number.
     */
    int getStartPage();

    /**
     * This method returns an end page number.
     *
     * @return is an end page number.
     */
    int getEndPage();

    /**
     * This method gets the number of pages links to be displayed.
     *
     * @return is a number of pages.
     */
    int getDisplayedPages ();

    /**
     * This method sets the number of pages links to be displayed.
     *
     * @param displayedPages is a number of pages.
     */
    void setDisplayedPages (int displayedPages);

    /**
     * This method returns a page size.
     *
     * @return a page size (number of displyable rows).
     */
    int getPageSize ();

    /**
     * This method gets a current page number.
     *
     * @return a current page number.
     */
    int getCurrentPageNumber ();
}

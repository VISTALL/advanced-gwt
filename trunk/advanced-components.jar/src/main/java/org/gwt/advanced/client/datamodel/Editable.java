package org.gwt.advanced.client.datamodel;

import java.util.Comparator;

/**
 * This is interface describing an editable table.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface Editable extends GridDataModel {
    /**
     * This method adds a row into the model.
     *
     * @param beforeRow is a row number.
     * @param row is row data.
     * @throws IllegalArgumentException if the row number is invalid.
     */
    void addRow(int beforeRow, Object[] row) throws IllegalArgumentException;

    /**
     * This method updates a row with the specified data set.
     *
     * @param rowNumber is a row number.
     * @param row is row data.
     * @throws IllegalArgumentException if the row number is invalid.
     */
    void updateRow(int rowNumber, Object[] row) throws IllegalArgumentException;

    /**
     * This method removes a row from the model.
     *
     * @param rowNumber is a row number.
     * @throws IllegalArgumentException if the row number is invalid.
     */
    void removeRow(int rowNumber) throws IllegalArgumentException;

    /**
     * This method adds a column into the model.
     *
     * @param beforeColumn a column number.
     * @param column a column data set.
     *
     * @throws IllegalArgumentException if the column number is invalid.
     */
    void addColumn(int beforeColumn, Object[] column) throws IllegalArgumentException;

    /**
     * This method updates a column with the specified data set.
     *
     * @param columnNumber a column number.
     * @param column a column data set.
     *
     * @throws IllegalArgumentException if the column number is invalid.
     */
    void updateColumn(int columnNumber, Object[] column) throws IllegalArgumentException;

    /**
     * This method removes a column from the model.
     *
     * @param columnNumber a column number.
     *
     * @throws IllegalArgumentException if the column number is invalid.
     */
    void removeColumn(int columnNumber) throws IllegalArgumentException;

    /**
     * This method removes all rows from the model.
     */
    void removeAll();

    /**
     * This method updates the specified cell with the value.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @param data is a data to be applied.
     *
     * @throws IllegalArgumentException if row and / or column number is invalid.
     */
    void update(int row, int column, Object data) throws IllegalArgumentException;

    /**
     * This method sets a sort column and uses the specified comparator.
     *
     * @param sortColumn is a sort column.
     * @param comparator is a column comparator.
     */
    void setSortColumn (int sortColumn, Comparator comparator);

    /**
     * This method returns a callback handler instance.
     *
     * @return a callback handler.
     */
    DataModelCallbackHandler getHandler ();

    /**
     * This method updates data in the model using the specified value.
     *
     * @param data is a data to be placed.
     */
    void update(Object[][] data);

    /**
     * This method sets a callback handler.
     *
     * @param handler a callback handler.
     */
    void setHandler (DataModelCallbackHandler handler);

    /**
     * This method returns a total row count.
     *
     * @return a total row count.
     */
    int getTotalColumnCount ();

    /**
     * This method returns removed rows.
     *
     * @return a list of removed rows.
     */
    Object[][] getRemovedRows();

    /**
     * This method clears 
     */
    void clearRemovedRows();
}

package org.gwt.advanced.client.datamodel;

import java.util.*;

/**
 * This is a model for editable grids.<p>
 * It allows dynamically add / update and remove rows and columns. It also resizes the model
 * if the specified row or column is too long.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class EditableGridDataModel extends SimpleGridDataModel implements Editable {
    /** encapsulated data */
    private List data;
    /** a list of removed rows */
    private List removedRows = new ArrayList();
    /** number of columns */
    private int totalColumnCount;
    /** data model callback handler */
    private DataModelCallbackHandler handler;

    /**
     * Creates an instance of this class and initializes it with the data.<p>
     * It automatically resizes the data set if it's too small.
     *
     * @param data is a data set.
     */
    public EditableGridDataModel (Object[][] data) {
        super(null);

        if (data == null)
            data = new Object[0][0];

        prepareData(data, data.length, data.length > 0 ? data[0].length : 0);
    }

    /**
     * Creates a new instnace of this class and defines the handler.
     *
     * @param handler is a callback handler to be invoked on changes.
     */
    protected EditableGridDataModel (DataModelCallbackHandler handler) {
        this(new Object[0][0]);

        this.handler = handler;
        this.handler.synchronize(this);
    }

    /**
     * This method adds the specified row in the data model.<p>
     * It normalizes the model if the row is too long.
     *
     * @param beforeRow is a number of the new row in the grid.
     * @param row is new row data.
     *
     * @throws IllegalArgumentException if the row number is in invalid range.
     */
    public void addRow(int beforeRow, Object[] row) throws IllegalArgumentException {
        checkRowNumber(beforeRow, data.size() + 1);

        if (row == null)
            row = new Object[getTotalColumnCount()];

        IdentifiedList resultRow = normalizeColumnsCount(row);

        data.add(beforeRow, resultRow);
    }

    /**
     * This method updates the specified row in the data model.<p>
     * It normalizes the model if the row is too long.
     *
     * @param rowNumber is a number of the row in the grid.
     * @param row is new data.
     *
     * @throws IllegalArgumentException if the row number is in invalid range. 
     */
    public void updateRow(int rowNumber, Object[] row) throws IllegalArgumentException {
        checkRowNumber(rowNumber, data.size());

        if (row == null)
            row = new Object[getTotalColumnCount()];

        IdentifiedList oldRow = (IdentifiedList) data.get(rowNumber);
        IdentifiedList resultRow = normalizeColumnsCount(row);
        resultRow.setIdentifier(oldRow.getIdentifier());
        
        data.set(rowNumber, resultRow);
    }

    /**
     * This method removes the specified row in the data model.
     *
     * @param rowNumber is a number of the row in the grid.
     * 
     * @throws IllegalArgumentException if the row number is in invalid range.
     */
    public void removeRow(int rowNumber) throws IllegalArgumentException {
        checkRowNumber(rowNumber, data.size());

        removedRows.add(data.remove(rowNumber));
    }

    /**
     * This method adds the specified column in the data model.<p>
     * It normalizes the model if the column is too long.
     *
     * @param beforeColumn is a number of the new column in the grid.
     * @param column is new column data.
     *
     * @throws IllegalArgumentException if the column number is in invalid range.
     */
    public void addColumn(int beforeColumn, Object[] column) throws IllegalArgumentException {
        checkColumnNumber(beforeColumn, getTotalColumnCount() + 1);

        if (column == null)
            column = new Object[getTotalRowCount()];

        ArrayList resultColumn = normalizeRowsCount(column);

        for (int i = 0; i < resultColumn.size(); i++) {
            ArrayList row = (ArrayList) data.get(i);
            Object data = resultColumn.get(i);
            row.add(beforeColumn, data);
        }

        totalColumnCount++;
    }

    /**
     * This method updates the specified column in the data model.<p>
     * It normalizes the model if the column is too long.
     *
     * @param columnNumber is a number of the column in the grid.
     * @param column is new data.
     *
     * @throws IllegalArgumentException if the column number is in invalid range.
     */
    public void updateColumn(int columnNumber, Object[] column) {
        checkColumnNumber(columnNumber, getTotalColumnCount());

        if (column == null)
            column = new Object[getTotalRowCount()];

        ArrayList resultColumn = normalizeRowsCount(column);

        for (int i = 0; i < resultColumn.size(); i++) {
            ArrayList row = (ArrayList) data.get(i);
            Object cellData = resultColumn.get(i);
            row.set(columnNumber, cellData);
        }
    }

    /**
     * This method deletes the specified column in the data model.
     *
     * @param columnNumber is a number of the column in the grid.
     *
     * @throws IllegalArgumentException if the column number is in invalid range.
     */
    public void removeColumn(int columnNumber) {
        checkColumnNumber(columnNumber, getTotalColumnCount());

        for (int i = 0; i < data.size(); i++) {
            ArrayList row = (ArrayList) data.get(i);
            row.remove(columnNumber);
        }
        
        totalColumnCount--;
    }

    /** {@inheritDoc} */
    public Object[] getRowData (int rowNumber) {
        return ((List)data.get(rowNumber)).toArray();
    }

    /** {@inheritDoc} */
    public void removeAll () {
        removedRows.addAll(data);
        data.clear();
    }

    /** {@inheritDoc} */
    public void update (int row, int column, Object data) {
        checkRowNumber(row, this.data.size());
        checkColumnNumber(column, getTotalColumnCount());

        ((List)this.data.get(row)).set(column, data);
    }

    /** {@inheritDoc} */
    public void setSortColumn (int sortColumn, Comparator comparator) {
        setSortColumn(sortColumn);
        Collections.sort(data, createRowComparator(sortColumn, comparator));
    }

    /**
     * This method creates a row comparator.
     *
     * @param sortColumn is a sort column.
     * @param comparator is a cell comparator.
     *
     * @return is a row comparator instance.
     */
    protected RowComparator createRowComparator (
        int sortColumn, Comparator comparator
    ) {
        return new RowComparator(sortColumn, comparator);
    }

    /**
     * Getter for property 'totalColumnCount'.
     *
     * @return Value for property 'totalColumnCount'.
     */
    public int getTotalColumnCount () {
        return totalColumnCount;
    }

    /** {@inheritDoc} */
    public Object[][] getRemovedRows() {
        Object[][] rows = new Object[removedRows.size()][getTotalColumnCount()];

        for (int i = 0; i < rows.length; i++) {
            rows[i] = ((List)removedRows.get(i)).toArray();
        }
        return rows;
    }

    /** {@inheritDoc} */
    public void clearRemovedRows () {
        removedRows.clear();
    }

    /**
     * This method is overriden to avoid possible recursions.
     *
     * @return a total row count.
     */
    public int getTotalRowCount () {
        return data.size();
    }

    /** {@inheritDoc} */
    public DataModelCallbackHandler getHandler () {
        return handler;
    }

    /** {@inheritDoc} */
    public void setHandler (DataModelCallbackHandler handler) {
        this.handler = handler;
    }

    /**
     * Use this method to update the model with a new value.
     *
     * @param data is a data set to be applied instead of the current set.
     */
    public void update(Object[][] data) {
        prepareData(data, data.length, data.length > 0 ? data[0].length : 0);
    }

    /**
     * This method checks whether the specified column is in valid range and throws the
     * <code>IllegalArgumentException</code> if it isn't.
     *
     * @param columnNumber is a column number to check.
     * @param max is a max limit of the range.
     *
     * @throws IllegalArgumentException if check failed.
     */
    protected void checkColumnNumber(int columnNumber, int max) throws IllegalArgumentException {
        if (columnNumber < 0 || columnNumber >= max)
            throw new IllegalArgumentException("Wrong column number. It must be in range [0, " + max + "). It is " + columnNumber);
    }

    /**
     * This method checks whether the specified row is in valid range and throws the
     * <code>IllegalArgumentException</code> if it isn't.
     *
     * @param rowNumber is a row number to check.
     * @param max is a max limit of the range.
     *
     * @throws IllegalArgumentException if check failed.
     */
    protected void checkRowNumber(int rowNumber, int max) {
        if (rowNumber < 0 || rowNumber >= max)
            throw new IllegalArgumentException("Wrong row number. It must be in range [0, " + max + "). It is " + rowNumber);
    }

    /**
     * This method returns the data set of the model.
     *
     * @return a data set.
     */
    public Object[][] getData () {
        Object[][] rows = new Object[data.size()][getTotalColumnCount()];
        
        for (int i = 0; i < rows.length; i++) {
            ArrayList row = (ArrayList) data.get(i);
            for (int j = 0; row != null && j < rows[i].length; j++) {
                rows[i][j] = row.get(j);
            }
        }

        return rows;
    }

    /**
     * This method normalizes a number of columns in all rows adding empty cells.<p>
     * If the specified row is shorter then the current columns count, it add empty cells to the row.
     *
     * @param row is a pattern row.
     *
     * @return a result row.
     */
    protected IdentifiedList normalizeColumnsCount (Object[] row) {
        IdentifiedList resultRow = new IdentifiedList(Arrays.asList(row));

        //normalization
        if (row.length > getTotalColumnCount()) {
            for (Iterator iterator = data.iterator(); iterator.hasNext();) {
                ArrayList otherRow = (ArrayList) iterator.next();
                for (int i = getTotalColumnCount(); i < row.length; i++) {
                    otherRow.add(null);
                }
            }
            totalColumnCount = row.length;
        } else {
            for (int i = row.length; i < getTotalColumnCount(); i++) {
                resultRow.add(null);
            }
        }

        return resultRow;
    }

    /**
     * This method normalizes a number of rows in all columns adding empty cells.<p>
     * If the specified column is shorter then the current rows count, it add empty cells to the column.
     *
     * @param column is a pattern column.
     * @return a result column.
     */
    protected ArrayList normalizeRowsCount (Object[] column) {
        ArrayList resultColumn = new ArrayList(Arrays.asList(column));
        //normalize
        if (column.length > data.size()) {
            for (int i = data.size(); i < column.length; i++) {
                ArrayList row = new IdentifiedList(getTotalColumnCount());
                for (int j = 0; j < getTotalColumnCount(); j++) {
                    row.add(null);
                }
                data.add(row);
            }
        } else {
            for (int i = column.length; i < getTotalRowCount(); i++) {
                resultColumn.add(null);
            }
        }
        return resultColumn;
    }

    /**
     * This method initializes the data set with the specified values.<p>
     * It tries to fill the data set and if the specified value is shorter it adds empty cells.
     * Otherwise it increases the size of the data set.
     *
     * @param data is a data to put into the data set.
     * @param rowCount is a row count of the data set.
     * @param columnCount is a column count of the data set.
     */
    protected void prepareData (Object[][] data, int rowCount, int columnCount) {
        rowCount = Math.max(rowCount, (data != null ? data.length : 0));
        columnCount = Math.max(columnCount, (data != null && data.length > 0 ? data[0].length : 0));

        this.data = new ArrayList(rowCount);
        for (int i = 0; i < rowCount; i++) {
            List row = new IdentifiedList(columnCount);
            for (int j = 0; j < columnCount; j++) {
                if (data != null && data.length > i && data[0].length > j)
                    row.add(data[i][j]);
                else
                    row.add(null);
            }
            this.data.add(i, row);
        }

        this.totalColumnCount = columnCount;
    }

    /**
     * This method returns an original list of rows.
     *
     * @return a list of rows.
     */
    protected List getRows() {
        return data;
    }

    /**
     * This method returns an internal row identifier.
     *
     * @param row is a row number.
     * @return an identifier value.
     */
    protected String getInternalRowIdentifier(int row) {
        return ((IdentifiedList)getRows().get(row)).getIdentifier();
    }

    /**
     * This is a row comparator implementation.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected static class RowComparator implements Comparator {
        /** column number */
        private int column;
        /** cell comparator */
        private Comparator comparator;

        /**
         * Creates a new instance of this class.
         *
         * @param column is a column number.
         * @param comparator is a cell comparator.
         */
        public RowComparator (int column, Comparator comparator) {
            this.column = column;
            this.comparator = comparator;
        }

        /**
         * Compares cell values using the cell comparator.
         *
         * @param o1 is the first value.
         * @param o2 is the second value.
         *
         * @return a result of comparison.
         */
        public int compare (Object o1, Object o2) {
            if (!(o1 instanceof List) || !(o2 instanceof List))
                return 0;
            return comparator.compare(((List)o1).get(column), ((List)o2).get(column));
        }

        /**
         * Getter for property 'column'.
         *
         * @return Value for property 'column'.
         */
        public int getColumn () {
            return column;
        }

        /**
         * Getter for property 'comparator'.
         *
         * @return Value for property 'comparator'.
         */
        public Comparator getComparator () {
            return comparator;
        }
    }
}

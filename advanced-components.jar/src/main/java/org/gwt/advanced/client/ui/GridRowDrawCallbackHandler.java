package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a callback handler to be invoked on one row draw event.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface GridRowDrawCallbackHandler {
    /**
     * This method is invoked before the row is drawn.
     *
     * @param row is a row number.
     * @param pageSize is a page size.
     * @param grid is a grid instance.
     * @param rowData an array of row data.
     *
     * @return <code>false</code> if the row must be skipped.
     */
    boolean beforeDraw(int row, int pageSize, EditableGrid grid, Object[] rowData);

    /**
     * This method is invoked after the row is drawn.
     *
     * @param row is a row number.
     * @param pageSize is a page size.
     * @param grid is a grid instance.
     * @param rowData an array of row data.
     *
     * @return <code>false</code> if drawing must be stopped when the row is drawn.
     */
    boolean afterDraw(int row, int pageSize, EditableGrid grid, Object[] rowData);
}
package org.gwt.advanced.client.ui.widget.cell;

/**
 * This is a factory to create grid cells.<p>
 * You must implement this interface or extend one of existing factories if you like to use your own custom cells.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface GridCellFactory {
    /**
     * This method creates a new data cell.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @param data is data to be placed into the cell.
     * @return a grid cell widget.
     */
    GridCell create(int row, int column, Object data);

    /**
     * This method creates a header cell.
     *
     * @param column is a column number.
     * @param header is a header label.
     * @return a header cell.
     */
    HeaderCell create(int column, String header);
}
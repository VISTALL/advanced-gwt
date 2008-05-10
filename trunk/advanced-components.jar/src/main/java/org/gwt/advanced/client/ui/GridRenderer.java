package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.datamodel.GridDataModel;

/**
 * This is an interface that describes cell renderers.<p/>
 * All renderers applied by the grids must implement it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public interface GridRenderer {
    /**
     * This method renders column headers.
     *
     * @param headers is a list of header values to be rendered.
     */
    void drawHeaders(Object[] headers);

    /**
     * This method renders grid content.
     *
     * @param model is a model to be applied.
     */
    void drawContent(GridDataModel model);

    /**
     * This method renders a particular row.
     *
     * @param data is a row data array.
     * @param row is a row number.
     */
    void drawRow(Object[] data, int row);

    /**
     * This method draws a particular cell.
     *
     * @param data is a cell data.
     * @param row is a row number.
     * @param column is a column number.
     * @param active is a falg that indicates that the cell must be active.
     */
    void drawCell(Object data, int row, int column, boolean active);

    /**
     * This method converts grid row number to model row number.
     *
     * @param row is a row number.
     * @return a row number.
     */
    int getModelRow(int row);
}

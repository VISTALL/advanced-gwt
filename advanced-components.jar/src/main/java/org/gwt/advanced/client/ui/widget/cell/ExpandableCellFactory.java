package org.gwt.advanced.client.ui.widget.cell;

import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a cell factory designed especially to construct expandable cells.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class ExpandableCellFactory extends DefaultGridCellFactory {
    /**
     * Creates an instance of the factory.
     *
     * @param grid is a grid instance.
     */
    public ExpandableCellFactory (EditableGrid grid) {
        super(grid);
    }

    /** {@inheritDoc} */
    public GridCell create(int row, int column, Object content) {
        if (content instanceof GridCell) {
            ExpandableCell cell = new ExpandableCellImpl();
            prepareCell(cell, row, column, content);
            return cell;
        } else
            return super.create(row, column, content);
    }
}

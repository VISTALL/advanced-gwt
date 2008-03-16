package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a default cell focus listener implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class CellFocusListener implements FocusListener {
    /**
     * Fires start edit event.
     *
     * @param sender is a sender cell.
     */
    public void onFocus (Widget sender) {
        GridCell cell = (GridCell) sender.getParent();
        if (cell == null)
            return;

        FlexTable grid = cell.getGrid();
        if (grid instanceof EditableGrid)
            ((EditableGrid)grid).fireStartEdit(cell);
    }

    /**
     * This method validates entered value and passivate the cell or keep it activated if check fails.
     *
     * @param sender is a sender cell.
     */
    public void onLostFocus (Widget sender) {
        GridCell cell = (GridCell) sender.getParent();
        if (cell == null)
            return;

        FlexTable grid = cell.getGrid();
        if (grid instanceof EditableGrid)
            ((EditableGrid)grid).fireFinishEdit(cell, cell.getNewValue());
    }
}

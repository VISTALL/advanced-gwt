package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.cell.GridCell;

/**
 * This interface describes edit cell listeners.<p>
 * Use it to validate entered values.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface EditCellListener {
    /**
     * This method is invoked on start edit (cell activation) event.
     * 
     * @param cell is a cell widget.
     *
     * @return <code>true</code> if edit operation can be started. Otherwise the cell
     *         won't be activated. 
     */
    boolean onStartEdit(GridCell cell);

    /**
     * This method is invoked on finish edit (cell passivation) event.<p>
     * Listeners can also show alert messages if the cell value is invalid.
     *
     * @param cell is a cell widget.
     * @param newValue is a new value to be checked.
     *
     * @return <code>true</code> if edit operation can be finished. Otherwise the cell
     *         won't be passivated.
     */
    boolean onFinishEdit(GridCell cell, Object newValue);
}

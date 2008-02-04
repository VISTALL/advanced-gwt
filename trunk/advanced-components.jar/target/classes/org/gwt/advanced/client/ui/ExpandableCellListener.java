package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;

/**
 * This interface describes expandable cell listener.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface ExpandableCellListener {
    /**
     * This method is invoked every time when a user expands or collapse the cell.
     *
     * @param cell is a cell instance.
     */
    void onCellClick(ExpandableCell cell);
}

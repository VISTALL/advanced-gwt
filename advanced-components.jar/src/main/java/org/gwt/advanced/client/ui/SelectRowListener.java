package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This interface describes a listeners that handles row selection events.<p/>
 * The instance of this interface implementation will be invoked every time when
 * row selection is changed. If you like just to do handling on cell selection / edit
 * you must use {@link org.gwt.advanced.client.ui.EditCellListener}.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface SelectRowListener {
    /**
     * This method is invoked every time when row selection is done.
     *
     * @param grid is a grid where the row has been selected.
     * @param row is a selected row number.
     */
    void onSelect(EditableGrid grid, int row);
}

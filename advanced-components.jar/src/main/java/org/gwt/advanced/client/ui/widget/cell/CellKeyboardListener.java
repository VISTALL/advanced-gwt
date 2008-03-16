package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a default keyboard listener for all cells.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class CellKeyboardListener extends KeyboardListenerAdapter {
    /**
     * This method passivates the cell on enter key press.
     *
     * @param sender is a sender cell.
     * @param keyCode is a pressed key code.
     * @param modifiers is a modifiers mask.
     */
    public void onKeyPress (Widget sender, char keyCode, int modifiers) {
        if (keyCode == KEY_ENTER && sender instanceof HasFocus)
            ((HasFocus)sender).setFocus(false);
    }
}

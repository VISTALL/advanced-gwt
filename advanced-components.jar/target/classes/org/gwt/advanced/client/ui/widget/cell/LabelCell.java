package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a cell implementation for textual value to be represnted as read only label.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class LabelCell extends AbstractCell {
    /** {@inheritDoc} */
    protected Widget createActive () {
        removeStyleName("text-cell");
        return createInactive();
    }

    /** {@inheritDoc} */
    protected Widget createInactive () {
        Label label = getLabel();
        label.setText(String.valueOf(getValue()));
        addStyleName("text-cell");
        return label;
    }

    /** {@inheritDoc} */
    public void setFocus (boolean focus) {
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getLabel().getText();
    }
}

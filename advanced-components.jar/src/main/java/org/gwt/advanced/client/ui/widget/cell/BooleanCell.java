package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a cell implementation for <code>Boolean</code> values.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class BooleanCell extends AbstractCell {
    /** a checkbox click listener */
    private ClickListener clickListener;
    /** a checkbox widget */
    private CheckBox checkBox;

    /** {@inheritDoc} */
    public void setValue(Object value) {
        value = Boolean.valueOf(String.valueOf(value));

        if (getValue() == null)
            getCheckBox().setChecked(((Boolean)value).booleanValue());

        super.setValue(value);
    }

    /** {@inheritDoc} */
    protected Widget createActive() {
        removeStyleName("active-cell");
        addStyleName("passive-cell");
        return createInactive();
    }

    /** {@inheritDoc} */
    protected Widget createInactive() {
        addStyleName("boolean-cell");
        CheckBox widget = getCheckBox();
        addListeners(widget);
        return widget;
    }

    /** {@inheritDoc} */
    protected void addListeners(Widget widget) {
        if (clickListener == null) {
            final GridCell cell = this;
            clickListener = new ClickListener() {
                public void onClick(Widget sender) {
                    FlexTable table = getGrid();

                    if (table instanceof EditableGrid && ((EditableGrid)table).fireStartEdit(cell))
                        ((EditableGrid)table).fireFinishEdit(cell, getNewValue());
                }
            };
        }
        CheckBox checkBox = getCheckBox();
        checkBox.removeClickListener(clickListener);
        checkBox.addClickListener(clickListener);
    }

    /**
     * This method does nothing.
     */
    protected void removeListeners(Widget widget) {
    }

    /**
     * Getter for property 'checkBox'.
     *
     * @return Value for property 'checkBox'.
     */
    protected CheckBox getCheckBox() {
        if (checkBox == null)
            checkBox = new CheckBox();
        
        return checkBox;
    }

    /** {@inheritDoc} */
    public void setFocus(boolean focus) {
        getCheckBox().setFocus(focus);
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return Boolean.valueOf(getCheckBox().isChecked());
    }
}

package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a text cell implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class TextBoxCell extends AbstractCell {
    /** a text box widget */
    private TextBox textBox;

    /** {@inheritDoc} */
    protected Widget createActive () {
        TextBox textBox = getTextBox();
        textBox.setText(String.valueOf(getValue()));
        removeStyleName("text-cell");
        return textBox;
    }

    /** {@inheritDoc} */
    protected Widget createInactive () {
        Label labelBox = getLabel();
        labelBox.setText(String.valueOf(getValue()));
        addStyleName("text-cell");
        return labelBox;
    }

    /** {@inheritDoc} */
    public void setValue(Object value) {
        if (value == null)
            value = "";
        super.setValue(value);
    }

    /**
     * Getter for property 'textBox'.
     *
     * @return Value for property 'textBox'.
     */
    protected TextBox getTextBox () {
        if (textBox == null)
            textBox = new TextBox();

        return textBox;
    }

    /** {@inheritDoc} */
    public void setFocus (boolean focused) {
        textBox.setFocus(focused);
        if (focused)
            textBox.setCursorPos(textBox.getText().length());
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getTextBox().getText();
    }
}

/*
 * Copyright 2008-2012 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an abstract cell implementation, common for all cells which display components extended from the
 * {@code TextBoxBase} on edit event.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 2.0.8
 */
public abstract class TextBaseCell extends AbstractCell {
    /** a text box base widget */
    protected TextBoxBase textBox;
    /** html text for inactive cell */
    private HTML html;

    /** {@inheritDoc} */
    protected Widget createActive () {
        TextBoxBase textBox = getTextBox();
        textBox.setText(String.valueOf(getValue()).replaceAll("<br/>", "\n"));
        removeStyleName("text-cell");
        return textBox;
    }

    /** {@inheritDoc} */
    protected Widget createInactive () {
        HTML labelBox = (HTML) getLabel();
        labelBox.setHTML(String.valueOf(getValue()).replaceAll("\n", "<br/>"));
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
     * Gets a text box widget associated with the cell.
     *
     * @return a text box widget.
     */
    protected abstract TextBoxBase getTextBox();

    /** {@inheritDoc} */
    public void setFocus (boolean focused) {
        getTextBox().setFocus(focused);
        if (focused)
            getTextBox().setCursorPos(getTextBox().getText().length());
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getTextBox().getText();
    }

    /**
     * Gets HTML widget containing cell text.
     *
     * @return a cell text container widget.
     */
    protected Label getLabel() {
        if (html == null)
            html = new HTML();
        return html;
    }
}

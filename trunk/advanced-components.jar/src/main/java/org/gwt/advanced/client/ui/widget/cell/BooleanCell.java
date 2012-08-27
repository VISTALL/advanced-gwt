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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a cell implementation for <code>Boolean</code> values.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class BooleanCell extends AbstractCell {
    /**
     * a checkbox click handler
     */
    private ClickHandler clickHandler;
    /**
     * a checkbox widget
     */
    private CheckBox checkBox;

    /**
     * {@inheritDoc}
     */
    public void setValue(Object value) {
        value = Boolean.valueOf(String.valueOf(value));

        if (getValue() == null)
            getCheckBox().setValue((Boolean) value);

        super.setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    protected Widget createActive() {
        removeStyleName("active-cell");
        addStyleName("passive-cell");
        return createInactive();
    }

    /**
     * {@inheritDoc}
     */
    protected Widget createInactive() {
        addStyleName("boolean-cell");
        CheckBox widget = getCheckBox();
        addListeners(widget);
        return widget;
    }

    /**
     * {@inheritDoc}
     */
    protected void addListeners(Widget widget) {
        if (clickHandler == null) {
            final GridCell cell = this;
            clickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    FlexTable table = getGrid();

                    if (table instanceof EditableGrid && ((EditableGrid) table).fireStartEdit(cell))
                        ((EditableGrid) table).fireFinishEdit(cell, getNewValue());
                }
            };
            CheckBox checkBox = getCheckBox();
            checkBox.addClickHandler(clickHandler);
        }
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

    /**
     * {@inheritDoc}
     */
    public void setFocus(boolean focus) {
        getCheckBox().setFocus(focus);
    }

    /**
     * {@inheritDoc}
     */
    public Object getNewValue() {
        return getCheckBox().getValue();
    }
}

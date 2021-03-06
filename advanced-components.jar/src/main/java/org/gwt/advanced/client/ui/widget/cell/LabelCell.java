/*
 * Copyright 2008-2013 Sergey Skladchikov
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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a cell implementation for textual value to be represnted as read only label.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
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

/*
 * Copyright 2011 Sergey Skladchikov
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

import com.google.gwt.user.client.ui.TextArea;

/**
 * This is a multiline text cell implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 2.0.8
 */
public class TextAreaCell extends TextBaseCell {
    /** {@inheritDoc} */
    @Override
    protected TextArea getTextBox() {
        if (textBox == null) {
            textBox = new TextArea();
            Object value = getValue();
            if (value != null) {
                String text = String.valueOf(value);
                int lines = text.split("\n").length + 1;
                ((TextArea)textBox).setVisibleLines(lines);
            }
        }
        return (TextArea) textBox;
    }
}

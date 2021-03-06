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

/**
 * This is a cell implementation for <code>Float</code> numbers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class FloatCell extends NumberCell {
    /** Constructs a new FloatCell. */
    public FloatCell() {
        super(REAL_PATTERN, Float.MAX_VALUE, -Float.MAX_VALUE);
    }

    /** {@inheritDoc} */
    protected Number convertToNumber(String text) {
        return Float.valueOf(text);
    }
}

/*
 * Copyright 2009 Sergey Skladchikov
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

package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;

/**
 * This interface describes a widget that can fire expand cell events.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.0
 */
public interface ExpandCellEventProducer {
    /**
     * This method adds an expandable cell listener.
     *
     * @param listener a listener instance.
     */
    void addExpandableCellListener (ExpandableCellListener listener);

    /**
     * Removes expandable cell listener.
     *
     * @param listener is a listener to remove.
     */
    void removeExpandableCellListener (ExpandableCellListener listener);

    /**
     * This method fires the expand cell event.
     *
     * @param cell is an expanded / collapsed cell.
     */
    void fireExpandCell (ExpandableCell cell);
}

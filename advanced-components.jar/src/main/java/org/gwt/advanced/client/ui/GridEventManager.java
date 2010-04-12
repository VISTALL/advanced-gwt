/*
 * Copyright 2010 Sergey Skladchikov
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

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Event;
import org.gwt.advanced.client.ui.widget.GridPanel;

/**
 * This is an interface of grid event managers.<p/>
 * All classes which handle events produced by the grid must implement it.<br/>
 * Usually you won't have to implement this interface directly. Extend
 * {@link org.gwt.advanced.client.ui.widget.DefaultGridEventManager} or
 * {@link org.gwt.advanced.client.ui.widget.HierarchicalGridEventManager} instead.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public interface GridEventManager extends FocusHandler, BlurHandler, ClickHandler, Event.NativePreviewHandler {
    /**
     * Ctrl key modifier code
     */
    int MODIFIER_CTRL = 1;
    /**
     * Alt key modifier code
     */
    int MODIFIER_ALT = 2;
    /**
     * Shift key modifier code
     */
    int MODIFIER_SHIFT = 4;
    /**
     * Meta key modifier code
     */
    int MODIFIER_META = 8;

    /**
     * This method dispatches events and performs actions related to a concrete combinations of
     * keys.
     *
     * @param panel     is a grid panel that invokes the manager.
     * @param keyCode   is a key code.
     * @param modifiers is a list of modifiers defined in <code>KeyboardListener</code>.
     * @return a value that enables event cancelling. If <code>true</code> the original event
     *         must be cancelled.
     */
    boolean dispatch(GridPanel panel, char keyCode, int modifiers);
}

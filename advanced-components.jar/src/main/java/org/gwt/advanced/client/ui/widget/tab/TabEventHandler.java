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

package org.gwt.advanced.client.ui.widget.tab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;

/**
 * This handler changes the currently selected tab when a user clicks on it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.6
 */
public class TabEventHandler implements ClickHandler {
    /** target tabs panel */
    private AdvancedTabPanel panel;

    /**
     * Creates an instance of this class and initializes internal variables.
     *
     * @param panel is a targer tabs panel.
     */
    public TabEventHandler(AdvancedTabPanel panel) {
        this.panel = panel;
    }

    /**
     * See class docs.
     */
    @Override
    public void onClick(ClickEvent event) {
        FlexTable tabs = (FlexTable) event.getSource();
        HTMLTable.Cell cell = tabs.getCellForEvent(event);
        String name = tabs.getCellFormatter().getStyleName(cell.getRowIndex(), cell.getCellIndex());

        if (name != null && name.indexOf("unselected") != -1)
            panel.setSelected(Math.max(cell.getRowIndex() / 2, cell.getCellIndex() / 2));
    }
}
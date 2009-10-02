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

package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.HierarchicalGrid;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.DemoModelFactory;
import org.gwt.advanced.client.GridPanelFactoryImpl;

/**
 * This is a show case for the {@link HierarchicalGrid} widget.
 *
 * @author Sergey Skladchikov
 */
public class HierarchicalGridShowcase extends AbstractShowcase {
    private EditableGrid grid;

    /** {@inheritDoc} */
    protected Widget getWidget() {
        GridPanel gridPanel = new GridPanel();

        grid = gridPanel.createEditableGrid(
            new String[] {
                "Department", "Human Resources", "Description", "ID"
            },
            new Class[] {
                TextBoxCell.class, TextBoxCell.class, TextBoxCell.class, LongCell.class
            },
            DemoModelFactory.createHierarchicalDepartmentsModel()
        );
        gridPanel.setReadonlyColumn(1, true);
        gridPanel.setTopToolbarVisible(false);
        ((HierarchicalGrid)gridPanel.getGrid()).addGridPanelFactory(1, new GridPanelFactoryImpl());
        gridPanel.setInvisibleColumn(3, true);

        return gridPanel;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.hierarchicalGridDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Hierarchical Grid";
    }

    /**
     * Overrides the super method to enable vertical scrolling.
     *
     * @param panel the main tab panel.
     */
    public void initShowcase(AdvancedTabPanel panel) {
        super.initShowcase(panel);
        grid.setBodyHeight("300px");
        grid.enableVerticalScrolling(true);
        grid.getGridPanel().display();
    }
}

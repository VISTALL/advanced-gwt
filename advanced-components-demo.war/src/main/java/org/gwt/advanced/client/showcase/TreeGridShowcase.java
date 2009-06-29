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
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;
import org.gwt.advanced.client.ui.widget.cell.DateCell;
import org.gwt.advanced.client.ui.widget.cell.ComboBoxCell;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.DemoModelFactory;

/**
 * This is a show case for the {@link org.gwt.advanced.client.ui.widget.TreeGrid} widget.
 *
 * @author Sergey Skladchikov
 */
public class TreeGridShowcase extends AbstractShowcase {
    /** {@inheritDoc} */
    protected Widget getWidget() {
        GridPanel gridPanel = new GridPanel();
        gridPanel.createEditableGrid(
            new String[]{
                    "Name", "Birth Date", "Position", "ID"
            },
            new Class[]{
                    TextBoxCell.class, DateCell.class, ComboBoxCell.class, LongCell.class
            },
            DemoModelFactory.createTreePeopleModel()
        );
        gridPanel.setInvisibleColumn(3, true);
        gridPanel.setInvisibleColumn(3, false);
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.setTotalCountDisplayed(true);
        gridPanel.getGrid().setMultiRowModeEnabled(true);
        gridPanel.display();
        return gridPanel;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.treeGridDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Tree Grid";
    }
}

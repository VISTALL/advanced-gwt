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

package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.DemoModelFactory;
import org.gwt.advanced.client.GridPanelFactoryImpl;
import org.gwt.advanced.client.ui.widget.GridPanel;

/**
 * This is a show case for the {@link org.gwt.advanced.client.ui.widget.EditableGrid} widget.
 *
 * @author Sergey Skladchikov
 */
public class EditableGridShowcase extends AbstractShowcase {
    /** {@inheritDoc} */
    protected Widget getWidget() {
        GridPanel gridPanel =
                new GridPanelFactoryImpl().create(DemoModelFactory.createEployeesModel(-1)); //-1 means "render all"
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.setTotalCountDisplayed(true);
        gridPanel.display();
        gridPanel.getGrid().setMultiRowModeEnabled(true);

        return gridPanel;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.editableGridDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Editable Grid";
    }
}

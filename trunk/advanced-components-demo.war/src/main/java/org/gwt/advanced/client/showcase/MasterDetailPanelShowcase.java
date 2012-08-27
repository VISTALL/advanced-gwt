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

package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.DemoModelFactory;
import org.gwt.advanced.client.GridPanelFactoryImpl;
import org.gwt.advanced.client.datamodel.LazyGridDataModel;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.MasterDetailPanel;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;

/**
 * This is a showcase for the {@link MasterDetailPanel} widget.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class MasterDetailPanelShowcase extends AbstractShowcase {
    /** {@inheritDoc} */
    protected Widget getWidget() {
        MasterDetailPanel masterDetailPanel = new MasterDetailPanel();

        //create master grid panel (departments)
        GridPanel master = new GridPanel();
        master.createEditableGrid(
            new String[] {
                "Department", "Human Resources", "Description", "ID"
            },
            new Class[] {
                TextBoxCell.class, TextBoxCell.class, TextBoxCell.class, LongCell.class
            },
            DemoModelFactory.createDepartmentsModel()
        );
        master.setReadonlyColumn(1, true);
        master.setTopToolbarVisible(false);
        master.setInvisibleColumn(3, true);
        masterDetailPanel.addGridPanel(master, null, "Departments");
        master.display();

        //create detail grid panel (employees)
        GridPanel detail = new GridPanelFactoryImpl().create(new LazyGridDataModel(new Object[0][0]));
        detail.getGrid().setModel(DemoModelFactory.createDepartmentDetailModel(detail, master));
        detail.setTopToolbarVisible(false);
        masterDetailPanel.addGridPanel(detail, master, "Employees");
        detail.display();

        masterDetailPanel.display();
        return masterDetailPanel;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.masterDetailDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Master-Detail Panel";
    }
}

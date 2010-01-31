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

package org.gwt.advanced.client;

import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.cell.ComboBoxCell;
import org.gwt.advanced.client.ui.widget.cell.DateCell;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;

/**
 * This is a grid panel factory implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class GridPanelFactoryImpl implements GridPanelFactory {
    /**
     * Creates a new grid panel containing simple editable grid of employees.
     *
     * @param model is a model of the new grid.
     * @return an isntance of the grid panel.
     */
    public GridPanel create(GridDataModel model) {
        final GridPanel gridPanel = new GridPanel();
        gridPanel.createEditableGrid(
                new String[]{
                        "Name", "Birth Date", "Position", "ID"
                },
                new Class[]{
                        TextBoxCell.class, DateCell.class, ComboBoxCell.class, LongCell.class
                },
                (Editable) model
        );
        gridPanel.setInvisibleColumn(3, true);
        return gridPanel;
    }

    /**
     * This method creates a new subgrid model using data of the parent model.
     *
     * @param row   is a row number.
     * @param model is a parent model.
     * @return a new grid data model.
     */
    public GridDataModel create(int row, GridDataModel model) {
        Object[] data = model.getRowData(row);
        Object id = data[data.length - 1];
        if (id == null)
            id = new Long(0);
        return DemoModelFactory.createEployeesModel(Long.parseLong(String.valueOf(id)));
    }
}
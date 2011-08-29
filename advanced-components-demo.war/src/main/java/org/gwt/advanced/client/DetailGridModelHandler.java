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

package org.gwt.advanced.client;

import org.gwt.advanced.client.datamodel.DataModelCallbackHandler;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;

/**
 * This class is a demo of data handler for detail grid.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class DetailGridModelHandler implements DataModelCallbackHandler {
    private GridPanel panel;
    private GridPanel parent;

    public DetailGridModelHandler(GridPanel panel, GridPanel parent) {
        this.panel = panel;
        this.parent = parent;
    }

    public void synchronize(GridDataModel model) {
        panel.lock();
        try {
            EditableGrid grid = parent.getGrid();
            Object[] data = grid.getModel().getRowData(grid.getCurrentRow());
            GridDataModel newModel = DemoModelFactory.createEployeesModel((Long) data[data.length - 1]);
            ((Editable)model).update(newModel.getData());
        } finally {
            panel.unlock();
        }
    }
}

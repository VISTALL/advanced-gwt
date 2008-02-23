package org.gwt.advanced.client;

import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.GridToolbarListenerAdapter;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.cell.*;

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
        gridPanel.addToolbarListener(new GridToolbarListenerAdapter() {
            public void onAddClick() {
                EditableGrid grid = gridPanel.getGrid();
                GridCell cell = (GridCell) grid.getWidget(grid.getRowCount() - 1, 2);
                grid.fireStartEdit(cell);
                grid.fireFinishEdit(cell, DemoModelFactory.createDepartmentListBox(0));
            }
        });
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
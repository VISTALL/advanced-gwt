/*
 * Copyright 2008 Sergey Skladchikov
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

package org.gwt.advanced.client.ui.widget;

import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;
import org.gwt.advanced.client.ui.GridPanelFactory;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an extension for hierarchical grid rendering.<p/>
 * It creates {@link org.gwt.advanced.client.ui.widget.cell.ExpandableCell}s if the grid model
 * has submodels.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public class HierarchicalGridRenderer extends DefaultGridRenderer {
    /** disabling focus listener */
    private FocusListener disablingFocusListener;

    /**
     * Creates an instance of this class and initializes the grid cell factory.
     *
     * @param grid is a target grid.
     */
    public HierarchicalGridRenderer(EditableGrid grid) {
        super(grid);
    }

    /** {@inheritDoc} */
    public void drawContent(GridDataModel model) {
        int count = 0;
        EditableGrid grid = getGrid();
        while (grid.getRowCount() > count) {
            if (HierarchicalGrid.SUBGRID_ROW_STYLE.equals(grid.getRowFormatter().getStyleName(count)))
                grid.removeRow(count);
            else
                count++;
        }

        super.drawContent(model);

        GridDataModel dataModel = grid.getModel();
        if (dataModel instanceof Hierarchical) {
            int rowCount = grid.getRowCount();
            int expandedCells = 0;
            for (int i = 0; i < rowCount; i++) {
                int modelRow = super.getModelRow(i);
                int expandedBefore = expandedCells;
                for (int j = grid.getCellCount(i + expandedBefore) - 1; j >= 0; j--) {
                    if (((Hierarchical)dataModel).isExpanded(modelRow, j)) {
                        ((HierarchicalGrid) grid).expandCell(i + expandedBefore, j);
                        expandedCells++;
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void drawCell(Object data, int row, int column, boolean active) {
        Editable dataModel = getGrid().getModel();
        if (dataModel instanceof Hierarchical && ((HierarchicalGrid)getGrid()).isExpandable(column)) {
            Hierarchical model = (Hierarchical) dataModel;
            int modelRow = getModelRow(row);

            GridCell gridCell = getCellFactory().create(row, column, data);
            gridCell.displayActive(false);

            ExpandableCell expandableCell = (ExpandableCell) getCellFactory().create(row, column, gridCell);
            if (model.isExpanded(modelRow, column))
                expandableCell.setExpanded(true);
            expandableCell.displayActive(false);
        } else
            super.drawCell(data, row, column, active);
    }

    /** {@inheritDoc} */
    public int getModelRow(int row) {
        return super.getModelRow(row) - getSubgridRowsBefore(row);
    }

     /**
     * This method calculates subgrid rows number before the specified row.
     *
     * @param row is a row number.
     * @return number of subgrid rows.
     */
    protected int getSubgridRowsBefore(int row) {
        int result = 0;
        for (int i = 0; i < row; i++) {
            if (HierarchicalGrid.SUBGRID_ROW_STYLE.equals(getGrid().getRowFormatter().getStyleName(i)))
                result++;
        }
        return result;
    }
    
    /**
     * This method expands the cell and adds a subgrid row below the current row.
     *
     * @param parentRow is a row number.
     * @param parentColumn is a column number.
     */
    protected void renderSubgrid (int parentRow, int parentColumn) {
        GridDataModel submodel = null;
        GridPanelFactory factory = null;
        HierarchicalGrid grid = (HierarchicalGrid) getGrid();
        GridDataModel model = grid.getModel();

        int modelRow = getModelRow(parentRow);
        if (model instanceof Hierarchical) {
            submodel = ((Hierarchical)model).getSubgridModel(modelRow, parentColumn);
            factory = (GridPanelFactory) grid.getGridPanelFactories().get(new Integer(parentColumn));

            if (submodel == null && factory != null) {
                submodel = factory.create(modelRow, model);
                ((Hierarchical)model).addSubgridModel(modelRow, parentColumn, submodel);
            }
        }

        if (factory != null) {
            ((Hierarchical)model).setExpanded(modelRow, parentColumn, true);
            int thisRow = grid.getGridRowNumber(parentRow, parentColumn);
            grid.increaseRowNumbers(thisRow, 1);
            grid.insertRow(thisRow);
            if (grid.getCurrentRow() > parentRow)
                grid.setCurrentRow(grid.getCurrentRow() + 1);

            for (int i = 0; i < parentColumn; i++)
                grid.setText(thisRow, i, "");
            GridPanel gridPanel = (GridPanel) grid.getGridPanelCache().get(submodel);
            if (gridPanel == null) {
                gridPanel = factory.create(submodel);
                gridPanel.getTopToolbar().setSaveButtonVisible(false);
                gridPanel.getBottomToolbar().setSaveButtonVisible(false);
                gridPanel.getFocusPanel().addFocusListener(getDisablingFocusListener());
                gridPanel.display();

                grid.getGridPanelCache().put(submodel, gridPanel);
            }

            grid.getFlexCellFormatter().setColSpan(thisRow, parentColumn, grid.getCellCount(parentRow) - parentColumn);
            grid.getRowFormatter().setStyleName(thisRow, HierarchicalGrid.SUBGRID_ROW_STYLE);
            grid.getCellFormatter().setStyleName(thisRow, parentColumn, "subgrid-cell");
            grid.setWidget(thisRow, parentColumn, gridPanel);
        }
    }

    /**
     * This method collapses the cell and removes an appropriate subgrid row.
     *
     * @param parentRow is a row number..
     * @param parentColumn is a column number.
     */
    protected void removeSubgrid (int parentRow, int parentColumn) {
        HierarchicalGrid grid = (HierarchicalGrid) getGrid();
        GridDataModel dataModel = grid.getModel();
        if (dataModel instanceof Hierarchical) {
            ((Hierarchical) dataModel).setExpanded(getModelRow(parentRow), parentColumn, false);
            int thisRow = grid.getGridRowNumber(parentRow, parentColumn);
            grid.increaseRowNumbers(thisRow, -1);
            grid.getGridPanel(parentRow, parentColumn).getFocusPanel().removeFocusListener(getDisablingFocusListener());
            grid.removeRow(thisRow);
            if (grid.getCurrentRow() > parentRow)
                grid.setCurrentRow(grid.getCurrentRow() - 1);
        }
    }

    /**
     * Getter for property 'disablingFocusListener'.
     *
     * @return Value for property 'disablingFocusListener'.
     */
    public FocusListener getDisablingFocusListener() {
        if (disablingFocusListener == null)
            disablingFocusListener = new DisablingEventManager();
        return disablingFocusListener;
    }

    /**
     * This listener enables / disables the event manager of this grid on subgrids activation.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     * @since 1.3.0
     */
    protected class DisablingEventManager implements FocusListener {
        /** See class docs */
        public void onFocus(Widget sender) {
            ((HierarchicalGrid)getGrid()).enableEvents(false);
        }

        /** See class docs */
        public void onLostFocus(Widget sender) {
            ((HierarchicalGrid)getGrid()).enableEvents(true);
        }
    }
}
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

package org.gwt.advanced.client.ui.widget;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.HeaderCell;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an extension for hierarchical grid rendering.<p/>
 * It creates {@link org.gwt.advanced.client.ui.widget.cell.ExpandableCell}s if the grid model
 * has submodels.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public class HierarchicalGridRenderer extends DefaultGridRenderer {
    /**
     * disabling focus / blur handler
     */
    private DisablingEventManager disablingFocusManager;
    /**
     * map of focus handler registrations related to the specified focus panels (subgrids)
     */
    private Map<FocusPanel, HandlerRegistration> focusHandlerRegistrations = new HashMap<FocusPanel, HandlerRegistration>();
    /**
     * map of blur handler registrations related to the specified focus panels (subgrids)
     */
    private Map<FocusPanel, HandlerRegistration> blurHandlerRegistrations = new HashMap<FocusPanel, HandlerRegistration>();

    /**
     * Creates an instance of this class and initializes the grid cell factory.
     *
     * @param grid is a target grid.
     */
    public HierarchicalGridRenderer(EditableGrid grid) {
        super(grid);
    }

    /**
     * {@inheritDoc}
     */
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
                    if (((Hierarchical) dataModel).isExpanded(modelRow, j)) {
                        ((HierarchicalGrid) grid).expandCell(i + expandedBefore, j);
                        expandedCells++;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void drawCell(Object data, int row, int column, boolean active) {
        Editable dataModel = getGrid().getModel();
        if (dataModel instanceof Hierarchical && ((HierarchicalGrid) getGrid()).isExpandable(column)) {
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

    /**
     * {@inheritDoc}
     */
    public int getModelRow(int row) {
        return super.getModelRow(row) - getSubgridRowsBefore(row);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowByModelRow(int modelRow) {
        for (int i = 0; i < getGrid().getRowCount(); i++) {
            if (!HierarchicalGrid.SUBGRID_ROW_STYLE.equals(getGrid().getRowFormatter().getStyleName(i))
                    && getModelRow(i) == modelRow)
                return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public void drawColumn(Object[] data, int column, boolean overwrite) {
        if (!overwrite && column < getGrid().getHeaderWidgets().size())
            getGrid().insertHeaderCell(getGrid().getColumnByModelColumn(column));
        HeaderCell cell = getCellFactory().create(column, getGrid().getHeaders()[column]);
        cell.displayActive(false);

        for (int i = 0; i < data.length && i < getGrid().getRowCount(); i++) {
            Object cellData = data[i];
            if (!HierarchicalGrid.SUBGRID_ROW_STYLE.equals(getGrid().getRowFormatter().getStyleName(i)))
                drawCell(cellData, i, column, false);
        }
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
     * @param parentRow    is a row number.
     * @param parentColumn is a column number.
     */
    protected void renderSubgrid(int parentRow, int parentColumn) {
        GridDataModel submodel = null;
        GridPanelFactory factory = null;
        HierarchicalGrid grid = (HierarchicalGrid) getGrid();
        GridDataModel model = grid.getModel();

        int modelRow = getModelRow(parentRow);
        if (model instanceof Hierarchical) {
            submodel = ((Hierarchical) model).getSubgridModel(modelRow, parentColumn);
            factory = grid.getGridPanelFactories().get(parentColumn);

            if (submodel == null && factory != null) {
                submodel = factory.create(modelRow, model);
                ((Hierarchical) model).addSubgridModel(modelRow, parentColumn, submodel);
            }
        }

        if (factory != null) {
            int thisRow = grid.getGridRowNumber(parentRow, parentColumn);
            grid.increaseRowNumbers(thisRow, 1);
            grid.insertRow(thisRow);

            dropWrongSelection(parentRow, 1);

            for (int i = 0; i < parentColumn; i++)
                grid.setText(thisRow, i, "");
            GridPanel gridPanel = grid.getGridPanelCache().get(submodel);
            if (gridPanel == null) {
                gridPanel = factory.create(submodel);
                gridPanel.getTopToolbar().setSaveButtonVisible(false);
                gridPanel.getBottomToolbar().setSaveButtonVisible(false);
                focusHandlerRegistrations.put(
                        gridPanel.getFocusPanel(),
                        gridPanel.getFocusPanel().addFocusHandler(getDisablingFocusManager())
                );
                blurHandlerRegistrations.put(
                        gridPanel.getFocusPanel(),
                        gridPanel.getFocusPanel().addBlurHandler(getDisablingFocusManager())
                );
                gridPanel.display();

                grid.getGridPanelCache().put(submodel, gridPanel);
            }

            int invisibleColumns = 0;
            for (Object o : grid.getInvisibleColumns()) {
                if ((Integer) o > parentColumn)
                    invisibleColumns++;
            }
            grid.getFlexCellFormatter().setColSpan(thisRow, parentColumn, grid.getCellCount(parentRow) - parentColumn - invisibleColumns);
            grid.getRowFormatter().setStyleName(thisRow, HierarchicalGrid.SUBGRID_ROW_STYLE);

            FlexTable.FlexCellFormatter formatter = (FlexTable.FlexCellFormatter) grid.getCellFormatter();
            formatter.setStyleName(thisRow, parentColumn, "subgrid-cell");
            formatter.setColSpan(thisRow, parentColumn, grid.getCellCount(parentRow) - parentColumn);
            grid.setWidget(thisRow, parentColumn, gridPanel);
        }
    }

    /**
     * This method collapses the cell and removes an appropriate subgrid row.
     *
     * @param parentRow    is a row number..
     * @param parentColumn is a column number.
     */
    protected void removeSubgrid(int parentRow, int parentColumn) {
        HierarchicalGrid grid = (HierarchicalGrid) getGrid();
        GridDataModel dataModel = grid.getModel();
        if (dataModel instanceof Hierarchical) {
            int thisRow = grid.getGridRowNumber(parentRow, parentColumn);
            grid.increaseRowNumbers(thisRow, -1);
            FocusPanel panel = grid.getGridPanel(parentRow, parentColumn).getFocusPanel();
            HandlerRegistration registration = focusHandlerRegistrations.get(panel);
            if (registration != null) {
                registration.removeHandler();
                focusHandlerRegistrations.remove(panel);
            }
            registration = blurHandlerRegistrations.get(panel);
            if (registration != null) {
                registration.removeHandler();
                blurHandlerRegistrations.remove(panel);
            }
            grid.removeRow(thisRow);

            dropWrongSelection(parentRow, -1);
        }
    }

    /**
     * This method drops selection if the specified row is less then one one currently selected rows.<p/>
     * Then it sets a new row to be a current.
     *
     * @param parentRow is a row number to check.
     * @param step      is a difference between the selected row and newly selected row. May be negative.
     */
    protected void dropWrongSelection(int parentRow, int step) {
        int[] indexes = getGrid().getCurrentRows();
        int selection = -1;
        for (int row : indexes) {
            if (row > parentRow) {
                selection = row;
                break;
            }
        }

        if (selection != -1)
            getGrid().setCurrentRow(selection + step);
    }

    /**
     * Getter for property 'disablingFocusManager'.
     *
     * @return Value for property 'disablingFocusManager'.
     */
    public DisablingEventManager getDisablingFocusManager() {
        if (disablingFocusManager == null)
            disablingFocusManager = new DisablingEventManager();
        return disablingFocusManager;
    }

    /**
     * This handler enables / disables the event manager of this grid on subgrids activation.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     * @since 1.3.0
     */
    protected class DisablingEventManager implements FocusHandler, BlurHandler {
        @Override
        public void onBlur(BlurEvent event) {
            ((HierarchicalGrid) getGrid()).enableEvents(true);
        }

        @Override
        public void onFocus(FocusEvent event) {
            ((HierarchicalGrid) getGrid()).enableEvents(false);
        }
    }
}

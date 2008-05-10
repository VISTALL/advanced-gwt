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

import com.google.gwt.user.client.ui.HTMLTable;
import org.gwt.advanced.client.datamodel.DataModelCallbackHandler;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.ui.GridListener;
import org.gwt.advanced.client.ui.GridToolbarListener;
import org.gwt.advanced.client.ui.PagerListener;
import org.gwt.advanced.client.ui.SelectRowListener;
import org.gwt.advanced.client.ui.widget.cell.HeaderCell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is an event mediator class developer for centralized grid panel subcomponents
 * interoperability.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class EventMediator implements PagerListener, GridListener, GridToolbarListener, SelectRowListener {
    /** a grid panel */
    private GridPanel panel;
    /** pager listeners set */
    private List pagerListeners;
    /** grid listeners set */
    private List gridListeners;
    /** toolbar listeners set */
    private List toolbarListeners;

    /**
     * Creates an instance of the mediator and initializes the internal fields.
     *
     * @param panel is a grid panel.
     */
    public EventMediator (GridPanel panel) {
        this.panel = panel;
    }

    /** {@inheritDoc} */
    public void onPageChange (Pager sender, int page) {
        GridPanel gridPanel = getPanel();

        if (
            gridPanel.isTopPagerVisible() && gridPanel.getTopPager() == sender
            && gridPanel.isBottomPagerVisible()
        ){
            gridPanel.getBottomPager().display();
        } else if (
            gridPanel.isBottomPagerVisible() && gridPanel.getBottomPager() == sender
            && gridPanel.isTopPagerVisible()
        ) {
            gridPanel.getTopPager().display();
        }

        EditableGrid grid = gridPanel.getGrid();
        if (grid instanceof HierarchicalGrid)
            ((HierarchicalGrid)grid).getGridPanelCache().clear();

        synchronizeDataModel(grid.getModel());
    }

    /** {@inheritDoc} */
    public void onSort (HeaderCell cell, GridDataModel dataModel) {
        GridPanel gridPanel = getPanel();

        if (gridPanel.isTopPagerVisible())
            gridPanel.getTopPager().display();
        if (gridPanel.isBottomPagerVisible())
            gridPanel.getBottomPager().display();

        EditableGrid grid = getPanel().getGrid();

        if (!grid.isSorted(cell.getColumn())) {
            dataModel.setAscending(true);
            dataModel.setSortColumn(cell.getColumn());
        } else {
            dataModel.setAscending(!dataModel.isAscending());
        }

        grid.drawHeaders();
        grid.sortOnClient();
        synchronizeDataModel((Editable)dataModel);
    }

    /** {@inheritDoc} */
    public void onSave (GridDataModel dataModel) {
        GridPanel gridPanel = getPanel();

        if (gridPanel.isTopPagerVisible())
            setCurrentPageNumber(dataModel, gridPanel.getTopPager());
        if (gridPanel.isBottomPagerVisible())
            setCurrentPageNumber(dataModel, gridPanel.getBottomPager());

        synchronizeDataModel((Editable)dataModel);
    }

    /** {@inheritDoc} */
    public void onClear (GridDataModel dataModel) {
        GridPanel gridPanel = getPanel();

        if (gridPanel.isTopPagerVisible())
            setCurrentPageNumber(dataModel, gridPanel.getTopPager());
        if (gridPanel.isBottomPagerVisible())
            setCurrentPageNumber(dataModel, gridPanel.getBottomPager());

        synchronizeDataModel((Editable)dataModel);
    }

    /** {@inheritDoc} */
    public void onAddClick () {
        GridPanel panel = getPanel();
        EditableGrid grid = panel.getGrid();

        Editable dataModel = grid.getModel();
        Object[] emptyCells = new Object[grid.getHeaders().length];

        int rowCount = grid.getRowCount();
        dataModel.addRow(grid.getModelRow(rowCount), emptyCells);

        grid.addRow(rowCount, emptyCells);
        grid.setCurrentRow(rowCount);
    }

    /** {@inheritDoc} */
    public void onRemoveClick () {
        GridPanel panel = getPanel();
        EditableGrid grid = panel.getGrid();
        Editable model = grid.getModel();

        if (grid instanceof HierarchicalGrid) {
            int currentRow = grid.getCurrentRow();

            if (currentRow >= 0 && currentRow < grid.getRowCount()) {
                int modelRow = grid.getModelRow(currentRow);

                for (int i = 0; model instanceof Hierarchical && i < grid.getCellCount(currentRow); i++) {
                    Hierarchical hierarchical = ((Hierarchical) model);
                    if (hierarchical.isExpanded(modelRow, i)) {
                        ((HierarchicalGrid)grid).collapseCell(currentRow, i);
                    }
                    ((HierarchicalGrid)grid).getGridPanelCache().remove(
                        hierarchical.getSubgridModel(modelRow, i)
                    );
                }
            }
        }

        int currentRow = grid.getCurrentRow();

        if (currentRow >= 0 && grid.getRowCount() > 0) {
            int modelRow = grid.getModelRow(currentRow);
            model.removeRow(modelRow);

            int rowCount = grid.getRowCount() - 1;
            int selectRow = currentRow;
            if (selectRow >= rowCount) {
                selectRow = rowCount - 1;
            }

            grid.removeRow(currentRow);
            grid.setCurrentRow(selectRow);
            grid.increaseRowNumbers(selectRow, -1);
        }

        if (grid instanceof HierarchicalGrid) {
            HTMLTable.RowFormatter rowFormatter = grid.getRowFormatter();
            while(
                rowFormatter.getStyleName(
                    grid.getCurrentRow()).indexOf(HierarchicalGrid.SUBGRID_ROW_STYLE
                ) != -1
                && grid.getCurrentRow() > 0
            ) {
                grid.setCurrentRow(grid.getCurrentRow() - 1);
            }
        }
    }

    /** {@inheritDoc} */
    public void onSaveClick () {
        EditableGrid grid = getPanel().getGrid();
        Editable model = grid.getModel();
        fireSaveEvent(model);

        if (grid instanceof HierarchicalGrid)
            ((HierarchicalGrid)grid).getGridPanelCache().clear();
    }

    /** {@inheritDoc} */
    public void onClearClick () {
        EditableGrid grid = getPanel().getGrid();
        Editable dataModel = grid.getModel();
        dataModel.removeAll();
        fireClearEvent(dataModel);

        if (grid instanceof HierarchicalGrid)
            ((HierarchicalGrid)grid).getGridPanelCache().clear();
    }

    /** {@inheritDoc} */
    public void onSelect(EditableGrid grid, int row) {
        for (Iterator iterator = getPanel().getChildGridPanels().iterator(); iterator.hasNext();) {
            GridPanel gridPanel = (GridPanel) iterator.next();
            gridPanel.getMediator().synchronizeDataModel(gridPanel.getGrid().getModel());
        }
    }

    /**
     * This method fires page change event.
     *
     * @param pager is a pager which is a source of the event.
     * @param page is a new page number.
     */
    public void firePageChangeEvent(Pager pager, int page) {
        for (Iterator iterator = getPagerListeners().iterator(); iterator.hasNext();) {
            PagerListener listener = (PagerListener) iterator.next();
            listener.onPageChange(pager, page);
        }
    }

    /**
     * This method fires grid changes saving event.
     *
     * @param model is a grid data model.
     */
    public void fireSaveEvent(GridDataModel model) {
        for (Iterator iterator = getGridListeners().iterator(); iterator.hasNext();) {
            GridListener listener = (GridListener) iterator.next();
            listener.onSave(model);
        }
    }

    /**
     * This method fires sorting event.
     *
     * @param cell is a header cell.
     * @param model is a grid data model.
     */
    public void fireSortEvent(HeaderCell cell, GridDataModel model) {
        for (Iterator iterator = getGridListeners().iterator(); iterator.hasNext();) {
            GridListener listener = (GridListener) iterator.next();
            listener.onSort(cell, model);
        }
    }

    /**
     * This method fires the cleaning event.
     *
     * @param model is a grid data model.
     */
    public void fireClearEvent(GridDataModel model) {
        for (Iterator iterator = getGridListeners().iterator(); iterator.hasNext();) {
            GridListener listener = (GridListener) iterator.next();
            listener.onClear(model);
        }
    }

    /**
     * This method fires the add item event.
     */
    protected void fireAddRowEvent() {
        for (Iterator iterator = getToolbarListeners().iterator(); iterator.hasNext();) {
            GridToolbarListener gridToolbarListener = (GridToolbarListener) iterator.next();
            gridToolbarListener.onAddClick();
        }
    }

    /**
     * This method fires the remove item event.
     */
    protected void fireRemoveRowEvent() {
        for (Iterator iterator = getToolbarListeners().iterator(); iterator.hasNext();) {
            GridToolbarListener gridToolbarListener = (GridToolbarListener) iterator.next();
            gridToolbarListener.onRemoveClick();
        }
    }

    /**
     * This method fires the save event.
     */
    protected void fireSaveEvent () {
        for (Iterator iterator = getToolbarListeners().iterator(); iterator.hasNext();) {
            GridToolbarListener gridToolbarListener = (GridToolbarListener) iterator.next();
            gridToolbarListener.onSaveClick();
        }
    }

    /**
     * This method fires the clear items event.
     */
    protected void fireClearEvent() {
        for (Iterator iterator = getToolbarListeners().iterator(); iterator.hasNext();) {
            GridToolbarListener gridToolbarListener = (GridToolbarListener) iterator.next();
            gridToolbarListener.onClearClick();
        }
    }

    /**
     * This method adds a pager listener.
     *
     * @param listener is a pager listener.
     */
    public void addPagerListener(PagerListener listener) {
        List list = getPagerListeners();
        if (!list.contains(listener))
            list.add(listener);
    }

    /**
     * This method removes a pager listener.
     *
     * @param listener is a pager listener.
     */
    public void removePagerListener(PagerListener listener) {
        getPagerListeners().remove(listener);
    }

    /**
     * This method adds a grid listener.
     *
     * @param listener is a pager listener.
     */
    public void addGridListener(GridListener listener) {
        List list = getGridListeners();
        if (!list.contains(listener))
            list.add(listener);
    }

    /**
     * This method removes a grid listener.
     *
     * @param listener is a grid listener.
     */
    public void removeGridListener(GridListener listener) {
        getGridListeners().remove(listener);
    }

    /**
     * This method adds a toolbar listener.
     *
     * @param listener is a toolbar listener.
     */
    public void addToolbarListener(GridToolbarListener listener) {
        List list = getToolbarListeners();
        if (!list.contains(listener))
            list.add(listener);
    }

    /**
     * This method removes a toolbar listener.
     *
     * @param listener is a toolbar listener.
     */
    public void removeToolbarListener(GridToolbarListener listener) {
        getToolbarListeners().remove(listener);
    }

    /**
     * Getter for property 'panel'.
     *
     * @return Value for property 'panel'.
     */
    protected GridPanel getPanel () {
        return panel;
    }

    /**
     * Getter for property 'pagerListeners'.
     *
     * @return Value for property 'pagerListeners'.
     */
    protected List getPagerListeners () {
        if (pagerListeners == null)
            pagerListeners = new ArrayList();
        return pagerListeners;
    }

    /**
     * Getter for property 'gridListeners'.
     *
     * @return Value for property 'gridListeners'.
     */
    protected List getGridListeners () {
        if (gridListeners == null)
            gridListeners = new ArrayList();
        return gridListeners;
    }

    /**
     * Getter for property 'toolbarListeners'.
     *
     * @return Value for property 'toolbarListeners'.
     */
    protected List getToolbarListeners () {
        if (toolbarListeners == null)
            toolbarListeners = new ArrayList();
        return toolbarListeners;
    }

    /**
     * This method sets a curent page number.
     *
     * @param model is a grid data model
     * @param pager is a pager instance.
     */
    protected void setCurrentPageNumber (GridDataModel model, Pager pager) {
        int pageNumber = model.getCurrentPageNumber();
        if (model.isEmpty())
            pageNumber = 0;
        pager.setCurrentPageNumber(pageNumber);
    }

    /**
     * This method invokes the data model handler to synchronize the model and persistence
     * storage.
     *
     * @param dataModel is a grid data model.
     */
    protected void synchronizeDataModel(Editable dataModel) {
        DataModelCallbackHandler callbackHandler = dataModel.getHandler();
        if (callbackHandler != null) {
            callbackHandler.synchronize(dataModel); //redisplay will be done automatically
        } else {
            getPanel().getGrid().drawContent(); //just redisplay the content if data synchronization is not required
        }
    }
}

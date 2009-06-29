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
package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.ExpandCellEventProducer;
import org.gwt.advanced.client.ui.ExpandableCellListener;
import org.gwt.advanced.client.ui.widget.cell.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is a tree grid widget implentation that is able to display composite data models
 * as a tree of rows.<p/>
 * Remarkable feature of this grid is that it can render pageable subtrees.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.0
 */
public class TreeGrid extends EditableGrid implements ExpandCellEventProducer {
    /**
     * expandable cell listeners
     */
    private List expandableCellListeners;

    /**
     * Creates a tree grid with resizable columns by default.
     *
     * @param headers             is a list of header names.
     * @param columnWidgetClasses is a list of appropriate cell classes.
     */
    public TreeGrid(String[] headers, Class[] columnWidgetClasses) {
        super(headers, columnWidgetClasses);
    }

    /**
     * Creates a tree grid with resizable columns.
     *
     * @param headers             is a list of header names.
     * @param columnWidgetClasses is a list of appropriate cell classes.
     * @param resizable           is a resizability flag. if it's <code>true</code> th grid will allow resize columns
     *                            by a mouse otherwise it won't.
     */
    public TreeGrid(String[] headers, Class[] columnWidgetClasses, boolean resizable) {
        super(headers, columnWidgetClasses, resizable);
        setGridRenderer(new TreeGridRenderer(this));
        setGridCellfactory(new TreeCellFactory(this));
    }

    /**
     * {@inheritDoc}
     */
    public void addExpandableCellListener(ExpandableCellListener listener) {
        getExpandableCellListeners().add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeExpandableCellListener(ExpandableCellListener listener) {
        getExpandableCellListeners().remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void fireExpandCell(ExpandableCell cell) {
        if (cell.isExpanded())
            expandCell((TreeCell) cell);
        else
            collapseCell((TreeCell) cell);

        for (Iterator iterator = getExpandableCellListeners().iterator(); iterator.hasNext();) {
            ExpandableCellListener expandableCellListener = (ExpandableCellListener) iterator.next();
            expandableCellListener.onCellClick(cell);
        }
    }

    /**
     * This method returns a widget placed in the cell.<p>
     * If the widget is an expandable cell it returns cell child widget.
     *
     * @param row    is a row number.
     * @param column is a column number.
     * @return is a cell widget.
     */
    public Widget getWidget(int row, int column) {
        Widget widget = super.getWidget(row, column);
        if (widget instanceof ExpandableCell)
            widget = (Widget) ((ExpandableCell) widget).getValue();
        return widget;
    }

    /**
     * Gets a tree grid row by parent and child row indexes.
     *
     * @param parentIndex is a parent row index.
     * @param rowIndex    is a row index.
     * @return a tree grid row instance.
     */
    public TreeGridRow getGridRow(int parentIndex, int rowIndex) {
        if (getModel() instanceof Composite) {
            TreeGridRow parent = (TreeGridRow) getGridRow(parentIndex);
            if (parent != null && getGridRows(parentIndex).length > rowIndex)
                return ((Composite) getModel()).getRow(parent, rowIndex);
        }
        return null;
    }

    /**
     * Gets a list of tree grid rows by the parent index.
     *
     * @param parentIndex is a parent row index.
     * @return a list of tree grid rows.
     */
    public TreeGridRow[] getGridRows(int parentIndex) {
        if (getModel() instanceof Composite) {
            TreeGridRow parent = (TreeGridRow) getGridRow(parentIndex);
            if (parent != null)
                return ((Composite) getModel()).getRows(parent);
        }
        return new TreeGridRow[0];
    }

    /**
     * {@inheritDoc}
     */
    public GridRow getGridRowByRowNumber(int row) {
        Map mapping = ((TreeGridRenderer) getGridRenderer()).getRowMapping();
        return (GridRow) mapping.get(new Integer(row));
    }

    /**
     * {@inheritDoc}
     */
    public void addRow() {
        Editable dataModel = getModel();
        if (dataModel instanceof Composite) {
            Object[] emptyCells = new Object[getHeaders().length];

            TreeCell cell = getTreeCell(getCurrentRow());
            TreeGridRow parent = cell != null ? cell.getGridRow().getParent() : null;

            Composite model = (Composite) dataModel;
            model.addRow(parent, emptyCells);
        } else
            super.addRow();
    }

    /**
     * {@inheritDoc}
     */
    protected void drawColumn(int column) {
        if (getModel() != null) {
            Object[] data = getModel().getColumns()[column].getData();
            List resultData = new ArrayList(getRowCount());

            int end = getModel().getEndRow();
            int start = getModel().getStartRow();
            int count = 0;
            for (int i = start; i <= end; i++) {
                resultData.add(data[i]);
                Composite composite = (Composite) getModel();
                resultData.addAll(getSubtreeColumnData((TreeGridRow) composite.getRow(i), column, count));
                count+=resultData.size();
            }

            getGridRenderer().drawColumn(resultData.toArray(), column, false);
        }
    }

    /**
     * This method gets a list of column data values of the specified subtree recursively.<p/>
     * Note that it returns only those subtrees which currently expanded if you use lazily loadable models.
     * So if you don't want to expand them on the view simply synchronize the model subtree. 
     *
     * @param parent is a parent row of the subtree.
     * @param column is a column number.
     * @param count is a current count of column cell displayed above.
     *
     * @return a list of data values.
     */
    protected List getSubtreeColumnData(TreeGridRow parent, int column, int count) {
        List resultData = new ArrayList();
        if (getRowCount() > count && getTreeCell(count).isExpanded()) {
            Composite composite = (Composite) getModel();
            TreeGridRow[] treeGridRows = composite.getRows(parent);
            for (int j = 0; j < treeGridRows.length; j++) {
                TreeGridRow treeGridRow = treeGridRows[j];
                resultData.add(treeGridRow.getData()[column]);
                resultData.addAll(getSubtreeColumnData(treeGridRow, column, count + resultData.size()));
            }
        }
        return resultData;
    }

    /**
     * Draws a grid row on add row event.
     *
     * @param event is a model event containing a row number value.
     */
    protected void drawRow(EditableModelEvent event) {
        Editable source = event.getSource();
        if (source != getModel())
            return;
        CompositeModelEvent compositeEvent = (CompositeModelEvent) event;
        Composite model = (Composite) source;
        TreeCell cell = getTreeCell(getCurrentRow());
        TreeGridRow row = model.getRow(compositeEvent.getParent(), compositeEvent.getRow());

        int position = getCurrentRow() + 1;
        TreeCell nextCell = getTreeCell(position);
        while (nextCell != null && nextCell.getGridRow().getLevel() > cell.getGridRow().getLevel()) {
            position++;
            nextCell = getTreeCell(position);
        }

        TreeGridRenderer renderer = (TreeGridRenderer) getGridRenderer();
        renderer.getCurrentRows().add(row.getParent());
        try {
            renderer.drawRow(row, position);
            dropSelection();
        } finally {
            renderer.getCurrentRows().remove();
        }
        setCurrentRow(position);
    }

    /**
     * {@inheritDoc}
     */
    protected void removeRow() {
        Editable dataModel = getModel();
        if (dataModel instanceof Composite) {
            int[] indexes = getCurrentRows();
            int last = -1;
            if (indexes.length > 0)
                last = indexes[indexes.length - 1];

            TreeGridRenderer gridRenderer = (TreeGridRenderer) getGridRenderer();
            Map mapping = gridRenderer.getRowMapping();
            for (int i = 0; i < indexes.length; i++) {
                int currentRow = indexes[i];
                int modelRow = getModelRow(currentRow);
                TreeGridRow row = (TreeGridRow) mapping.get(new Integer(currentRow));
                ((Composite) dataModel).removeRow(row.getParent(), modelRow);
            }

            if (last >= getRowCount())
                last = getRowCount() - 1;
            if (last >= 0)
                setCurrentRow(last);
        } else
            super.removeRow();
    }

    /**
     * This method removes the row from the subgrid by it's model row number.
     *
     * @param event is a model event containing a row number value and parent row.
     */
    protected void deleteRow(EditableModelEvent event) {
        CompositeModelEvent compositeEvent = (CompositeModelEvent) event;
        int[] indexes = getCurrentRows();

        TreeGridRenderer gridRenderer = (TreeGridRenderer) getGridRenderer();
        int currentRow = gridRenderer.getRowByModelRow(compositeEvent.getParent(), compositeEvent.getRow());
        Map mapping = gridRenderer.getRowMapping();
        TreeCell cell = getTreeCell(currentRow);
        int count = gridRenderer.removeSubtree(cell);

        mapping.remove(new Integer(currentRow));
        gridRenderer.remapIndexes(currentRow + 1, -1);

        removeRow(currentRow);
        increaseRowNumbers(currentRow, -1);

        for (int j = currentRow; j < indexes.length; j++) {
            int index = indexes[j];
            if (index
                    > currentRow)
                indexes[j] = index - 1 - count;
        }
    }

    /** {@inheritDoc} */
    public int getRowByModelRow(EditableModelEvent event) {
        return ((TreeGridRenderer) getGridRenderer()).getRowByModelRow(
                ((CompositeModelEvent) event).getParent(), event.getRow());
    }

    /**
     * This method moves the selected row to the left position decreasing its level and changing the row parent
     * to the super parent row.
     */
    protected void moveLeft() {
        if (!(getModel() instanceof Composite))
            return;

        int indexes[] = getCurrentRows();
        for (int i = 0; i < indexes.length; i++) {
            int currentRow = indexes[i];
            Composite model = (Composite) getModel();
            int column = model.getExpandableColumn();
            TreeCell cell =
                    (TreeCell) getOriginalWidget(currentRow, column);
            TreeGridRow treeGridRow = cell.getGridRow();
            TreeGridRow parent = treeGridRow.getParent();

            if (parent != null)
                model.setParent(parent.getParent(), treeGridRow);
        }

        synchronizeDataModel();
    }

    /**
     * This method moves the selected row to the right position increasing its level and changing the row parent
     * to the nearest parent row having this new level.
     */
    protected void moveRight() {
        if (!(getModel() instanceof Composite))
            return;

        int indexes[] = getCurrentRows();
        for (int i = 0; i < indexes.length; i++) {
            int currentRow = indexes[i];
            Composite model = (Composite) getModel();
            int column = model.getExpandableColumn();
            TreeCell cell = (TreeCell) getOriginalWidget(currentRow, column);
            TreeGridRow treeGridRow = cell.getGridRow();

            int count = currentRow - 1;
            TreeCell parentCell = (TreeCell) getOriginalWidget(count, column);
            while (parentCell != null && parentCell.getGridRow().getLevel() > treeGridRow.getLevel()) {
                count--;
                if (count >= 0)
                    parentCell = (TreeCell) getOriginalWidget(count, column);
                else
                    break;
            }

            if (parentCell != null) {
                TreeGridRow parentRow = parentCell.getGridRow();
                model.setParent(parentRow, treeGridRow);
                parentRow.setExpanded(true);
            }
        }

        synchronizeDataModel();
    }

    /** {@inheritDoc} */
    protected void synchronizeView(EditableModelEvent event) {
        if (getModel() instanceof Composite && ((CompositeModelEvent) event).getParent() == null) {
            //clear content since rows insertion works slower than rendering
            while (getRowCount() > 0)
                removeRow(getRowCount() - 1);
            super.synchronizeView(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void synchronizeDataModel() {
        if (getModel() instanceof Composite) {
            //clear content since rows insertion works slower than rendering
            while (getRowCount() > 0)
                removeRow(getRowCount() - 1);
        }
        super.synchronizeDataModel();
    }

    /**
     * This renderer returns a tree cell for the specified row.
     *
     * @param row is a row number.
     * @return a tree cell link.
     */
    protected TreeCell getTreeCell(int row) {
        if (row < getRowCount()) {
            return (TreeCell) getOriginalWidget(row, ((TreeGridDataModel) getModel()).getExpandableColumn());
        } else
            return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void increaseRowNumbers(int startRow, int step) {
        for (int i = startRow; i >= 0 && i < getRowCount(); i++) {
            for (int j = 0; j < getCellCount(i); j++) {
                if (getOriginalWidget(i, j) != null) {
                    Widget widget = super.getWidget(i, j);
                    if (widget instanceof GridCell) {
                        GridCell gridCell = (GridCell) widget;
                        gridCell.setPosition(gridCell.getRow() + step, j);

                        if (widget instanceof ExpandableCell)
                            ((GridCell) ((ExpandableCell) widget).getValue()).setPosition(
                                    gridCell.getRow(), gridCell.getColumn()
                            );
                    }
                }
            }
        }
    }

    /**
     * This method performs client sorting of the subtree.
     *
     * @param parent is a parent row.
     */
    protected void sortOnClient(TreeGridRow parent) {
        if (!isClientSortEnabled())
            return;

        HeaderCell sortColumn = getCurrentSortColumn();
        if (sortColumn != null)
            ((Composite) getModel()).setSortColumn(parent, sortColumn.getColumn(), getCellComparator());
    }

    /**
     * {@inheritDoc}
     */
    protected void updateModel(GridCell cell, Object newValue) {
        if (!(getModel() instanceof Composite))
            super.updateModel(cell, newValue);
        else {
            Composite model = (Composite) getModel();
            TreeCell parent = (TreeCell) super.getWidget(cell.getRow(), model.getExpandableColumn());
            model.update(parent.getGridRow().getParent(), getModelRow(cell.getRow()), cell.getColumn(), newValue);
        }
    }

    /**
     * This method safely draws the subtree specified by its parent row.<p/>
     * If the parent is expanded and contains rendered subrows, they will be removed.
     *
     * @param parent is a parent row of the subtree.
     */
    protected void drawContent(TreeGridRow parent) {
        if (parent == null) {
            drawContent();
            return;
        }

        TreeGridRenderer renderer = (TreeGridRenderer) getGridRenderer();
        for (int i = 0; i < getRowCount(); i++) {
            TreeCell cell = getTreeCell(i);
            if (parent.equals(cell.getGridRow())) {
                renderer.removeSubtree(cell);
                renderer.drawSubtree(cell);
                return;
            }
        }
    }

    /**
     * This method expands the specified cell.
     *
     * @param cell is a cell to expand.
     */
    protected void expandCell(TreeCell cell) {
        if (getModel() instanceof Composite) {
            cell.getGridRow().setExpanded(true);
            if (getModel() instanceof LazyLoadableComposite) {
                ((TreeDataModelCallbackHandler) getModel().getHandler()).synchronize(
                        (LazyTreeGridRow) cell.getGridRow(), (Composite) getModel()
                );
            } else
                ((TreeGridRenderer) getGridRenderer()).drawSubtree(cell);
        }
    }

    /**
     * This method collapses the specified cell.
     *
     * @param cell is a cell to collapse.
     */
    protected void collapseCell(TreeCell cell) {
        if (getModel() instanceof Composite) {
            cell.getGridRow().setExpanded(false);
            ((TreeGridRenderer) getGridRenderer()).removeSubtree(cell);
        }
    }

    /**
     * This method cleans the subtree in the grid on remove subtree event received from the data model.
     *
     * @param event is an event to be handled.
     */
    protected void removeSubtree(EditableModelEvent event) {
        if (event instanceof CompositeModelEvent) {
            GridCell cell = (GridCell) getOriginalWidget(getRowByModelRow(event), event.getColumn());
            if (cell instanceof TreeCell && ((TreeCell)cell).isExpanded())
                collapseCell((TreeCell) cell);
        }
    }

    /**
     * Getter for property 'expandableCellListeners'.
     *
     * @return Value for property 'expandableCellListeners'.
     */
    protected List getExpandableCellListeners() {
        if (expandableCellListeners == null)
            expandableCellListeners = new ArrayList();
        return expandableCellListeners;
    }
}

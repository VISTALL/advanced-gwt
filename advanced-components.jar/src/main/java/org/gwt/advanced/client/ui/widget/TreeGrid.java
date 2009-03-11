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
import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.HeaderCell;
import org.gwt.advanced.client.ui.widget.cell.TreeCell;
import org.gwt.advanced.client.ui.widget.cell.TreeCellFactory;

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
    /** expandable cell listeners */
    private List expandableCellListeners;

    /**
     * Creates a tree grid with resizable columns by default.
     *
     * @param headers is a list of header names.
     * @param columnWidgetClasses is a list of appropriate cell classes.
     */
    public TreeGrid(String[] headers, Class[] columnWidgetClasses) {
        super(headers, columnWidgetClasses);
    }

    /**
     * Creates a tree grid with resizable columns.
     *
     * @param headers is a list of header names.
     * @param columnWidgetClasses is a list of appropriate cell classes.
     * @param resizable is a resizability flag. if it's <code>true</code> th grid will allow resize columns
     *        by a mouse otherwise it won't.
     */
    public TreeGrid(String[] headers, Class[] columnWidgetClasses, boolean resizable) {
        super(headers, columnWidgetClasses, resizable);
        setGridRenderer(new TreeGridRenderer(this));
        setGridCellfactory(new TreeCellFactory(this));
    }

    /** {@inheritDoc} */
    public void addExpandableCellListener(ExpandableCellListener listener) {
        getExpandableCellListeners().add(listener);
    }

    /** {@inheritDoc} */
    public void removeExpandableCellListener(ExpandableCellListener listener) {
        getExpandableCellListeners().remove(listener);
    }

    /** {@inheritDoc} */
    public void fireExpandCell(ExpandableCell cell) {
        if (cell.isExpanded())
            expandCell((TreeCell) cell);
        else
            collapseCell((TreeCell)cell);

        for (Iterator iterator = getExpandableCellListeners().iterator(); iterator.hasNext();) {
            ExpandableCellListener expandableCellListener = (ExpandableCellListener) iterator.next();
            expandableCellListener.onCellClick(cell);
        }
    }

    /** {@inheritDoc} */
    public int getModelRow(int gridRow) {
        return getGridRenderer().getModelRow(gridRow);
    }

    /**
     * This method returns a widget placed in the cell.<p>
     * If the widget is an expandable cell it returns cell child widget.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @return is a cell widget.
     */
    public Widget getWidget (int row, int column) {
        Widget widget = super.getWidget(row, column);
        if (widget instanceof ExpandableCell)
            widget = (Widget) ((ExpandableCell) widget).getValue();
        return widget;
    }

    /**
     * Gets a tree grid row by parent and child row indexes.
     *
     * @param parentIndex is a parent row index.
     * @param rowIndex is a row index.
     * @return a tree grid row instance.
     */
    public TreeGridRow getGridRow(int parentIndex, int rowIndex) {
        if (getModel() instanceof Composite) {
            TreeGridRow parent = (TreeGridRow) getGridRow(parentIndex);
            if (parent != null && getGridRows(parentIndex).length > rowIndex)
                return ((Composite)getModel()).getRow(parent, rowIndex);
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
                return ((Composite)getModel()).getRows(parent);
        }
        return new TreeGridRow[0];
    }

    /** {@inheritDoc} */
    protected void addRow() {
        Editable dataModel = getModel();
        if (dataModel instanceof Composite) {
            Object[] emptyCells = new Object[getHeaders().length];

            TreeCell cell = getTreeCell(getCurrentRow());
            TreeGridRow parent = cell.getGridRow().getParent();

            Composite model = (Composite) dataModel;
            int index = model.addRow(parent, emptyCells);
            TreeGridRow row = model.getRow(parent, index);

            int position = getCurrentRow() + 1;
            TreeCell nextCell = getTreeCell(position);
            while(nextCell != null && nextCell.getGridRow().getLevel() > cell.getGridRow().getLevel()) {
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
        } else
            super.addRow();
    }

    /** {@inheritDoc} */
    protected void removeRow() {
        Editable dataModel = getModel();
        if (dataModel instanceof Composite) {
            int currentRow = getCurrentRow();

            if (currentRow >= 0 && getRowCount() > 0) {
                int modelRow = getModelRow(currentRow);
                TreeGridRenderer gridRenderer = (TreeGridRenderer) getGridRenderer();
                Map mapping = gridRenderer.getRowMapping();
                TreeGridRow row = (TreeGridRow) mapping.get(new Integer(currentRow));
                TreeCell cell = getTreeCell(currentRow);
                gridRenderer.removeSubtree(cell);

                ((Composite) dataModel).removeRow(row.getParent(), modelRow);
                mapping.remove(new Integer(currentRow));
                gridRenderer.remapIndexes(currentRow + 1, -1);

                int rowCount = getRowCount() - 1;
                int selectRow = currentRow;
                if (selectRow >= rowCount) {
                    selectRow = rowCount - 1;
                }

                removeRow(currentRow);
                setCurrentRow(selectRow);
                increaseRowNumbers(currentRow, -1);
            }
        } else
            super.removeRow();
    }

    /**
     * This method moves the selected row to the left position decreasing its level and changing the row parent
     * to the super parent row.
     */
    protected void moveLeft() {
        if (!(getModel() instanceof Composite))
            return;

        int currentRow = getCurrentRow();

        if (currentRow > 0) {
            Composite model = (Composite) getModel();
            int column = model.getExpandableColumn();
            TreeCell cell =
                    (TreeCell) getOriginalWidget(currentRow, column);
            TreeGridRow treeGridRow = cell.getGridRow();
            TreeGridRow parent = treeGridRow.getParent();

            if (parent != null) {
                model.setParent(parent.getParent(), treeGridRow);
                synchronizeDataModel();
            }
        }
    }

    /**
     * This method moves the selected row to the right position increasing its level and changing the row parent
     * to the nearest parent row having this new level.
     */
    protected void moveRight() {
        if (!(getModel() instanceof Composite))
            return;

        int currentRow = getCurrentRow();

        if (currentRow > 0) {
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
                synchronizeDataModel();
            }
        }
    }

    /** {@inheritDoc} */
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
            return (TreeCell) getOriginalWidget(row, ((TreeGridDataModel)getModel()).getExpandableColumn());
        } else
            return null;
    }

    /** {@inheritDoc} */
    protected void increaseRowNumbers(int startRow, int step) {
        for (int i = startRow; i >= 0 && i < getRowCount(); i++) {
            for (int j = 0; j < getCellCount(i); j++) {
                if (getOriginalWidget(i, j) != null) {
                    Widget widget = super.getWidget(i, j);
                    if (widget instanceof GridCell) {
                        GridCell gridCell = (GridCell) widget;
                        gridCell.setPosition(gridCell.getRow() + step, j);

                        if (widget instanceof ExpandableCell)
                            ((GridCell)((ExpandableCell)widget).getValue()).setPosition (
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
    protected void sortOnClient (TreeGridRow parent) {
        if (!isClientSortEnabled())
            return;

        HeaderCell sortColumn = getCurrentSortColumn();
        if (sortColumn != null)
            ((Composite)getModel()).setSortColumn(parent, sortColumn.getColumn(), getCellComparator());
    }

    /** {@inheritDoc} */
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
                ((TreeDataModelCallbackHandler)getModel().getHandler()).synchronize(
                    (LazyTreeGridRow)cell.getGridRow(), (Composite) getModel()
                );
            } else
                ((TreeGridRenderer)getGridRenderer()).drawSubtree(cell);
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
            ((TreeGridRenderer)getGridRenderer()).removeSubtree(cell);
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

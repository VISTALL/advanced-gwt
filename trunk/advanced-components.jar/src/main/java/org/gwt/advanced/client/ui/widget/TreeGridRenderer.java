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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import org.gwt.advanced.client.datamodel.Composite;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.TreeGridRow;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.widget.cell.TreeCell;
import org.gwt.advanced.client.util.Stack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class renders tree grid content.<p/>
 * The {@link TreeGrid} uses this implementation by default.<br/>
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.0
 */
public class TreeGridRenderer extends DefaultGridRenderer {
    /** this field contains a stack of rows which have already been drawn */
    private Stack currentRows;
    /** grid to model row mapping */
    private Map rowMapping = new HashMap();

    /**
     * Creates an instance of this class and initializes the grid cell factory.
     *
     * @param grid is a target grid.
     */
    public TreeGridRenderer(EditableGrid grid) {
        super(grid);
    }

    /** {@inheritDoc} */
    public void drawContent(GridDataModel gridModel) {
        if (!(gridModel instanceof Composite)) {
            super.drawContent(gridModel);
            return;
        }

        Composite model = (Composite) gridModel;
        int start = model.getStartRow();
        int end = model.getEndRow();
        boolean empty = model.isEmpty();

        if (!empty && getGrid().getRowDrawHandler() != null) {
            DeferredCommand.addCommand(new DrawTreeRowCommand(start, end));
        } else {
            int rowNumber = 0;
            for (int i = start; !empty && i <= end; i++)
                rowNumber = drawRow((TreeGridRow) model.getRow(i - start), rowNumber);
            for (int i = getGrid().getRowCount() - 1; i >= rowNumber; i--)
                getGrid().removeRow(i);
        }
    }

    /** {@inheritDoc} */
    public void drawCell(Object data, int row, int column, boolean active) {
        Editable gridModel = getGrid().getModel();
        if (gridModel instanceof Composite && ((Composite)gridModel).getExpandableColumn() == column) {
            Composite model = (Composite) gridModel;
            int modelRow = getModelRow(row);

            GridCell gridCell = getCellFactory().create(row, column, data);
            gridCell.displayActive(false);

            TreeGridRow parentRow = (TreeGridRow) getCurrentRows().get();
            TreeGridRow currentRow = model.getRow(parentRow, modelRow);

            TreeCell cell = (TreeCell) getCellFactory().create(row, column, gridCell);
            cell.setGridRow(currentRow);
            cell.setExpanded(currentRow.isExpanded());
            cell.displayActive(false);
        } else
            super.drawCell(data, row, column, active);
    }

    /** {@inheritDoc} */
    public int getModelRow(int row) {
        Editable editable = getGrid().getModel();
        if (editable instanceof Composite) {
            return ((TreeGridRow)getRowMapping().get(new Integer(row))).getIndex();
        } else
            return super.getModelRow(row);
    }

    /**
     * This method draws tree grid rows recusrsively.<p/>
     * If the specified row has children and it is expanded subrows will be rendered as well.
     *
     * @param row is a row to be rendered.
     * @param rowNumber is a row number (index) in the grid.
     * @return the next row number.
     */
    protected int drawRow(TreeGridRow row, int rowNumber) {
        getRowMapping().put(new Integer(rowNumber), row);
        if (rowNumber < getGrid().getRowCount())
            getGrid().insertRow(rowNumber);
        drawRow(row.getData(), rowNumber);
        if (row.getParent() != null)
            getGrid().getRowFormatter().addStyleName(rowNumber, "grid-subrow");
        drawPager(((TreeGrid)getGrid()).getTreeCell(rowNumber));
        rowNumber++;

        Composite model = (Composite) getGrid().getModel();
        if (row.isExpanded()) {
            getCurrentRows().add(row);

            try {
                ((TreeGrid)getGrid()).sortOnClient(row);

                int start = model.getStartRow(row);
                int end = model.getEndRow(row);

                for (int i = start; i <= end; i++)
                    rowNumber = drawRow(model.getRow(row, i), rowNumber);
            } finally {
                getCurrentRows().remove();
            }
        }

        return rowNumber;
    }

    /**
     * This method renders a pager assotiated with the parent row.
     *
     * @param cell is a parent expandable cell.
     *
     * @return <code>true</code> if the pager has been displayed.
     */
    protected boolean drawPager(TreeCell cell) {
        TreeGridRow gridRow = cell.getGridRow();
        boolean draw = gridRow.isExpanded() && gridRow.isPagerEnabled();
        if (draw) {
            SubtreePager pager = new SubtreePager((TreeGrid) getGrid(), cell.getGridRow(), "subtree-pager");
            cell.putPager(pager);
        }
        return draw;
    }

    /**
     * Draws a subtree that belongs to the specified parent.
     *
     * @param parent is a parent cell.
     */
    protected void drawSubtree(TreeCell parent) {
        TreeGridRow gridRow = parent.getGridRow();
        Composite composite = (Composite) getGrid().getModel();
        ((TreeGrid)getGrid()).sortOnClient(gridRow);
        int start = composite.getStartRow(gridRow);
        int end = composite.getEndRow(gridRow);

        getGrid().dropSelection();
        getGrid().setCurrentRow(parent.getRow());

        parent.setLeaf(start > end);
        parent.setExpanded(!parent.isLeaf());
        parent.getGridRow().setExpanded(parent.isExpanded());

        drawPager(parent);

        int nextRow = parent.getRow() + 1;
        
        getCurrentRows().add(gridRow);
        try {
            for (int i = start; i <= end; i++) {
                remapIndexes(nextRow, 1);
                nextRow = drawRow(composite.getRow(gridRow, i), nextRow);
            }

            getGrid().increaseRowNumbers(nextRow, nextRow - parent.getRow() - 1);
        } finally {
            getCurrentRows().remove();
        }
    }

    /**
     * This method remaps indexes in the rows mapping.
     *
     * @param startRow is a grid row number to start remaping from.
     * @param step is a row number increasing / decreasing step.
     */
    protected void remapIndexes(int startRow, int step) {
        Set keys = getRowMapping().keySet();
        Map mapping = new HashMap();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Integer key = (Integer) iterator.next();
            Object value = getRowMapping().get(key);

            if (key.intValue() >= startRow)
                mapping.put(new Integer(key.intValue() + step), value);
            else
                mapping.put(key, value);
        }
        
        getRowMapping().clear();
        getRowMapping().putAll(mapping);
    }

    /**
     * Removes a subtree that belongs to the specified parent.
     *
     * @param parent is a parent cell.
     */
    protected void removeSubtree(TreeCell parent) {
        getGrid().dropSelection();
        getGrid().setCurrentRow(parent.getRow());
        int level = parent.getGridRow().getLevel();
        int row = parent.getRow() + 1;
        parent.removePager();

        if (row < getGrid().getRowCount()) {
            TreeGrid grid = (TreeGrid) getGrid();
            TreeCell treeCell = grid.getTreeCell(row);
            int count = 0;
            while (treeCell != null && treeCell.getGridRow().getLevel() > level) {
                if (getGrid().getCurrentRow() == treeCell.getRow())
                    getGrid().setCurrentRow(parent.getRow());
                getGrid().removeRow(row);
                getRowMapping().remove(new Integer(row));
                remapIndexes(row + 1, -1);
                count++;
                treeCell = grid.getTreeCell(row);
            }

            getGrid().increaseRowNumbers(row, -count);
        }
    }

    /**
     * Getter for property 'currentRows'.
     *
     * @return Value for property 'currentRows'.
     */
    protected Stack getCurrentRows() {
        if (currentRows == null)
            currentRows = new Stack();
        return currentRows;
    }

    /**
     * Getter for property 'rowMapping'.
     *
     * @return Value for property 'rowMapping'.
     */
    protected Map getRowMapping() {
        return rowMapping;
    }

    /**
     * This is a draw command that is used to draw one or more tree rows.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class DrawTreeRowCommand extends DrawRowCommand {
        /** current grid row number */
        private int rowNumber;

        /**
         * Creates an instance of this class and initilizes internal variables.
         *
         * @param start is a start row number.
         * @param end   is an end row number.
         */
        public DrawTreeRowCommand(int start, int end) {
            super(start, end);
        }

        /** {@inheritDoc} */
        public boolean execute() {
            if (!(getGrid().getModel() instanceof Composite))
                return super.execute();
            
            Composite model = (Composite) getGrid().getModel();
            int index = getCurrent() - getStart();
            int pageSize = getEnd() - getStart() + 1;

            boolean cont = false;
            for (int i = 0; getCurrent() <= getEnd() && i < 1; i++) {
                TreeGridRow row = (TreeGridRow) model.getRow(index);
                Object[] data = row.getData();
                cont = getGrid().fireBeforeDrawEvent(index, pageSize, data);

                if (cont) {
                    setRowNumber(drawRow(row, getRowNumber()));
                    cont = getGrid().fireAfterDrawEvent(index, pageSize, data);
                }

                if (!cont)
                    break;
                setCurrent(getCurrent() + 1);
            }

            if (!cont || getCurrent() > getEnd()) {
                for (int i = getGrid().getRowCount() - 1; i >= getRowNumber(); i--)
                    getGrid().removeRow(i);
                DOM.setStyleAttribute(getGrid().getBodyElement(), "display", "");
            }
            return cont && getCurrent() <= getEnd();
        }

        /**
         * Getter for property 'rowNumber'.
         *
         * @return Value for property 'rowNumber'.
         */
        protected int getRowNumber() {
            return rowNumber;
        }

        /**
         * Setter for property 'rowNumber'.
         *
         * @param rowNumber Value to set for property 'rowNumber'.
         */
        protected void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }
    }
}

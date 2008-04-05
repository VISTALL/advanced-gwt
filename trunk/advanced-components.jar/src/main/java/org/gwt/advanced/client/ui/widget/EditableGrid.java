package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.LazyLoadable;
import org.gwt.advanced.client.ui.*;
import org.gwt.advanced.client.ui.widget.cell.*;
import org.gwt.advanced.client.util.GWTUtil;

import java.util.*;

/**
 * This is an editable grid widget.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class EditableGrid extends AdvancedFlexTable implements AdvancedWidget {
    /**
     * this is a grid data model
     */
    private Editable model;
    /**
     * this is a list column widget classes
     */
    private Class[] columnWidgetClasses;
    /**
     * this is a map of flags containing read only flag values
     */
    private Map readOnlyColumns = new HashMap();
    /**
     * data-to-cell cellFactory instance
     */
    private GridCellFactory cellFactory = new DefaultGridCellFactory(this);
    /**
     * a list of edit cell listeners
     */
    private List editCellListeners;
    /**
     * a list of header labels
     */
    private String[] headers;
    /**
     * a map of sortable flag values for the headers
     */
    private Map sortableHeaders = new HashMap();
    /**
     * a list of invisible columns
     */
    private List invisibleColumns;
    /**
     * a list of grid decorators
     */
    private List decorators;
    /**
     * a current selected row number
     */
    private int currentRow = -1;
    /**
     * a cell comparator instance
     */
    private Comparator cellComparator;
    /**
     * a flag meaning whether the grid is locked
     */
    private boolean locked;
    /**
     * a grid panel
     */
    private GridPanel gridPanel;
    /**
     * a list of select row listeners
     */
    private List selectRowListeners;
    /**
     * a callback handler for the row drawing events
     */
    private GridRowDrawCallbackHandler rowDrawHandler;
    /**
     * a timer for automated grid resizing
     */
    private Timer resizeTimer = new ResizeTimer();
    /**
     * a flag meaning whether the grid has resizable columns
     */
    private boolean columnResizingAllowed = true;
    /**
     * a falg that means whether the grid columns are resizable
     */
    private boolean resizable;

    /**
     * Creates a new instance of this class.
     *
     * @param headers             is a list of header labels (including invisible).
     * @param columnWidgetClasses is a list of column widget classes.
     */
    public EditableGrid (String[] headers, Class[] columnWidgetClasses) {
        this(headers, columnWidgetClasses, true);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param headers             is a list of header labels (including invisible).
     * @param columnWidgetClasses is a list of column widget classes.
     * @param resizable           is a flag that means columns resizability (by default is <code>true</code>).
     */
    public EditableGrid (String[] headers, Class[] columnWidgetClasses, boolean resizable) {
        super();
        this.columnWidgetClasses = columnWidgetClasses;
        this.headers = headers;
        this.resizable = resizable;

        for (int i = 0; headers != null && i < headers.length; i++) {
            sortableHeaders.put(new Integer(i), Boolean.TRUE);
        }

        addListeners();
        makeResizable(resizable);
    }

    /**
     * Getter for property 'cellFactory'.
     *
     * @return Value for property 'cellFactory'.
     */
    public GridCellFactory getGridCellFactory () {
        return cellFactory;
    }

    /**
     * Getter for property 'columnWidgetClasses'.
     *
     * @return Value for property 'columnWidgetClasses'.
     */
    public Class[] getColumnWidgetClasses () {
        return columnWidgetClasses;
    }

    /**
     * Getter for property 'headers'.
     *
     * @return Value for property 'headers'.
     */
    public String[] getHeaders () {
        return headers;
    }

    /**
     * Setter for property 'cellFactory'.
     *
     * @param converter Value to set for property 'cellFactory'.
     */
    public void setGridCellfactory (GridCellFactory converter) {
        this.cellFactory = converter;
    }

    /**
     * Setter for property 'model'.
     *
     * @param model Value to set for property 'model'.
     */
    public void setModel (Editable model) {
        this.model = model;
    }

    /**
     * This method sets custom cell comparator.
     *
     * @param cellComparator is a cell comparator.
     */
    public void setCellComparator (Comparator cellComparator) {
        this.cellComparator = cellComparator;
    }

    /**
     * Adds a new edit cell listener.<p> Use this method to add validators.
     *
     * @param listener is a listener instance.
     */
    public void addEditCellListener (EditCellListener listener) {
        removeEditCellListener(listener);
        getEditCellListeners().add(listener);
    }

    /**
     * This method removes the edit cell listener from the internal list.
     *
     * @param listener is a listener to be removed.
     */
    public void removeEditCellListener (EditCellListener listener) {
        getEditCellListeners().remove(listener);
    }

    /**
     * This method adds an invisible column in the internal list.
     *
     * @param column is a column number.
     */
    public void addInvisibleColumn (int column) {
        removeInvisibleColumn(column);
        getInvisibleColumns().add(new Integer(column));
    }

    /**
     * This method removes the specified column form the list of invisible columns.
     *
     * @param column is an iinvisible column number.
     */
    public void removeInvisibleColumn (int column) {
        getInvisibleColumns().remove(new Integer(column));
    }

    /**
     * This method adds a new grid decorator to the grid.
     *
     * @param decorator is a grid decorator instance.
     */
    public void addGridDecorator(GridDecorator decorator) {
        removeGridDecorator(decorator);
        getDecorators().add(decorator);
    }

    /**
     * This method removes the grid decorator from the grid.
     *
     * @param decorator is a grid decorator instance.
     */
    public void removeGridDecorator(GridDecorator decorator) {
        getDecorators().remove(decorator);
    }

    /**
     * This method checks whether the specified column is read only.
     *
     * @param column is a column to check.
     *
     * @return <code>true</code> if the column is read only.
     */
    public boolean isReadOnly (int column) {
        return Boolean.valueOf(
            String.valueOf(getReadOnlyColumns().get(new Integer(column)))
        ).booleanValue();
    }

    /**
     * This method checks whether the specified column is sortable.
     *
     * @param column is a column to check.
     *
     * @return <code>true</code> if the column is sortable.
     */
    public boolean isSortable (int column) {
        return Boolean.valueOf(
            String.valueOf(getSortableHeaders().get(new Integer(column)))
        ).booleanValue();
    }

    /**
     * This method checks whether the specified column is sorted asceding.
     *
     * @param column is a column to check.
     *
     * @return <code>true</code> if the column is sorted ascending.
     */
    public boolean isAscending (int column) {
        return model == null || model.isAscending();
    }

    /**
     * This method checks whether the specified column is sorted.
     *
     * @param column is a column to check.
     *
     * @return <code>true</code> if the column is sorted.
     */
    public boolean isSorted (int column) {
        return getModel().getSortColumn() == column;
    }

    /**
     * This method makes a column to be read only.
     *
     * @param column   is a column to be read only.
     * @param readOnly is a read only flag value.
     */
    public void setReadOnly (int column, boolean readOnly) {
        getReadOnlyColumns().put(new Integer(column), Boolean.valueOf(readOnly));
    }

    /**
     * This method makes a column to be sortable.
     *
     * @param column   is a column to be sortable.
     * @param sortable is a sortable flag value.
     */
    public void setSortable (int column, boolean sortable) {
        getSortableHeaders().put(new Integer(column), Boolean.valueOf(sortable));
    }

    /**
     * This method detects whether the specified column is visible.
     *
     * @param column is a column number.
     *
     * @return <code>true</code> if the column is visible.
     */
    public boolean isVisible (int column) {
        return !getInvisibleColumns().contains(new Integer(column));
    }

    /**
     * Getter for property 'currentRow'.
     *
     * @return Value for property 'currentRow'.
     */
    public int getCurrentRow () {
        return currentRow;
    }

    /**
     * This method returns column resizability flag.
     *
     * @return a flag value.
     */
    public boolean isColumnResizingAllowed() {
        return columnResizingAllowed;
    }

    /**
     * This method switches on / off column resizability.
     *
     * @param columnResizingAllowed is a flag value.
     */
    public void setColumnResizingAllowed(boolean columnResizingAllowed) {
        this.columnResizingAllowed = columnResizingAllowed;
    }

    /**
     * Setter for property 'currentRow'.
     *
     * @param currentRow Value to set for property 'currentRow'.
     */
    public void setCurrentRow (int currentRow) {
        HTMLTable.RowFormatter rowFormatter = getRowFormatter();

        if (getCurrentRow() >= 0 && getCurrentRow() < getRowCount())
            rowFormatter.removeStyleName(getCurrentRow(), "selected-row");

        if (currentRow >= 0 && currentRow < getRowCount())
            rowFormatter.addStyleName(currentRow, "selected-row");

        this.currentRow = currentRow;
        for (Iterator iterator = getSelectRowListeners().iterator(); iterator.hasNext();) {
            SelectRowListener selectRowListener = (SelectRowListener) iterator.next();
            selectRowListener.onSelect(this, currentRow);
        }
    }

    /**
     * Getter for property 'gridPanel'.
     *
     * @return Value for property 'gridPanel'.
     */
    public GridPanel getGridPanel () {
        if (gridPanel == null)
            gridPanel = new GridPanel();
        return gridPanel;
    }

    /**
     * This method adds a selected row listener into the list.
     *
     * @param selectRowListener is a select row listener to be added.
     */
    public void addSelectRowListener(SelectRowListener selectRowListener) {
        removeSelectRowListener(selectRowListener);
        getSelectRowListeners().add(selectRowListener);
    }

    /**
     * This method removes a selected row listener from the list.
     *
     * @param selectRowListener a select row listener to be removed.
     */
    public void removeSelectRowListener(SelectRowListener selectRowListener) {
        getSelectRowListeners().remove(selectRowListener);
    }

    /**
     * This method returns a row draw handler instance.
     *
     * @return a link to the handler.
     */
    public GridRowDrawCallbackHandler getRowDrawHandler() {
        return rowDrawHandler;
    }

    /**
     * This method sets a row draw handler for this grid.
     *
     * @param rowDrawHandler a row draw handler instance.
     */
    public void setRowDrawHandler(GridRowDrawCallbackHandler rowDrawHandler) {
        this.rowDrawHandler = rowDrawHandler;
    }

    /**
     * Use this method to displayActive the grid.
     */
    public void display() {
        setStyleName("advanced-Grid");
        drawHeaders();
        sortOnClient();
        drawContent();
        runDecorators();
    }

    /**
     * This method fires the start edit event.
     *
     * @param cell is a cell to be edited.
     *
     * @return <code>true</code> if all listeners allow the edit operation.
     */
    public boolean fireStartEdit (GridCell cell) {
        boolean result = true;
        for (Iterator iterator = getEditCellListeners().iterator(); iterator.hasNext();) {
            EditCellListener editCellListener = (EditCellListener) iterator.next();
            result = result && editCellListener.onStartEdit(cell);
        }

        return result;
    }

    /**
     * This method fires the end edit event.
     *
     * @param cell     is a cell to be edited.
     * @param newValue is a new value to be applied.
     *
     * @return <code>true</code> if all listeners allow finishing edit.
     */
    public boolean fireFinishEdit (GridCell cell, Object newValue) {
        boolean result = true;
        for (Iterator iterator = getEditCellListeners().iterator(); iterator.hasNext();) {
            EditCellListener editCellListener = (EditCellListener) iterator.next();
            result = result && editCellListener.onFinishEdit(cell, newValue);
        }

        if (result) {
            Editable dataModel = getModel();

            dataModel.update(getModelRow(cell.getRow()), cell.getColumn(), newValue);

            Object oldValue = cell.getValue();
            cell.setValue(newValue);
            cell.displayActive(false);

            if (
                isClientSortEnabled()
                && isSortable(cell.getColumn())
                && isSorted(cell.getColumn())
                && !cell.valueEqual(oldValue)
            ) {
                dataModel.setAscending(!dataModel.isAscending()); // to keep original sort order
                fireSort(getCurrentSortColumn());
            }
        } else {
            cell.setFocus(true);
        }

        return result;
    }

    /**
     * This method returns a sort column number.
     *
     * @return is a sort column number.
     */
    public int getSortColumn() {
        return getModel().getSortColumn();
    }

    /**
     * This method sets the sort column and redisplays the grid.<p/>
     * Don't use this method if you are going to do other actions before grid redisplaying.
     * Use model methods instead.
     *
     * @param column is a column to be sorted.
     */
    public void setSortColumn(int column) {
        getModel().setSortColumn(column);
        display();
    }

    /**
     * This method sets the order of sorting and redisplays the grid.<p/>
     * Don't use this method if you are going to do other actions before grid redisplaying.
     * Use model methods instead.
     *
     * @param ascending is the order of sorting. <code>true</code> means ascending.
     */
    public void setAscending(boolean ascending) {
        getModel().setAscending(ascending);
        display();
    }

    /**
     * Getter for property 'resizable'.
     *
     * @return Value for property 'resizable'.
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Sets the resizability of column flag.<p/>
     * This method also sets table-layout style.
     *
     * @param columns resizability flag value.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        makeResizable(resizable);
    }

    /**
     * This method fires the sort event
     *
     * @param header is a header cell of the sortable column.
     */
    public void fireSort (HeaderCell header) {
        getGridPanel().getMediator().fireSortEvent(header, getModel());
    }

    /**
     * Getter for property 'model'.
     *
     * @return Value for property 'model'.
     */
    public Editable getModel () {
        return model;
    }

    /**
     * This method resizes the grid making it to fit as much space as possible.
     */
    protected void resize() {
        Element parent = DOM.getParent(getElement());
        int parentWidth = 0;
        if (parent != null) {
            if (isScrollable() && GWTUtil.isIE()) {
                Element superParent = DOM.getParent(parent);
                if (superParent != null)
                    DOM.setStyleAttribute(parent, "width", DOM.getElementPropertyInt(superParent, "offsetWidth") + "px");
                else
                    DOM.setStyleAttribute(parent, "width", (Window.getClientWidth() - 20) + "px");
            }

            if (GWTUtil.isIE())
                parentWidth = DOM.getElementPropertyInt(parent, "offsetWidth");
            else
                parentWidth = DOM.getElementPropertyInt(parent, "offsetWidth") - 2; 
        }
        if (parentWidth == 0)
            parentWidth = Window.getClientWidth() - 20;
        DOM.setStyleAttribute(getElement(), "width", parentWidth + "px");
    }

    /**
     * This method runs all attached decorators in the same order they have been added.
     */
    protected void runDecorators() {
        for(Iterator iterator = getDecorators().iterator(); iterator.hasNext();) {
            GridDecorator gridDecorator = (GridDecorator)iterator.next();
            gridDecorator.decorate(this);
        }
    }

    /**
     * This method adds the row into the grid.
     *
     * @param row  is a row number.
     * @param data is a row data set.
     */
    protected void addRow (int row, Object[] data) {
        for (int i = 0; data != null && i < data.length; i++) {
            if (isVisible(i)) {
                getGridCellFactory().create(row, i, data[i]).displayActive(false);
                getCellFormatter().addStyleName(row, i, "grid-column");
            }
        }
    }

    /**
     * Getter for property 'locked'.
     *
     * @return Value for property 'locked'.
     */
    protected boolean isLocked () {
        return locked && getModel() instanceof LazyLoadable;
    }

    /**
     * Setter for property 'locked'.
     *
     * @param locked Value to set for property 'locked'.
     */
    protected void setLocked (boolean locked) {
        this.locked = locked;
    }

    /**
     * Getter for property 'editCellListeners'.
     *
     * @return Value for property 'editCellListeners'.
     */
    protected List getEditCellListeners () {
        if (editCellListeners == null)
            editCellListeners = new ArrayList();
        return editCellListeners;
    }

    /**
     * Getter for property 'readOnlyColumns'.
     *
     * @return Value for property 'readOnlyColumns'.
     */
    protected Map getReadOnlyColumns () {
        return readOnlyColumns;
    }

    /**
     * Getter for property 'sortableHeaders'.
     *
     * @return Value for property 'sortableHeaders'.
     */
    protected Map getSortableHeaders () {
        return sortableHeaders;
    }

    /**
     * This method checks whether client sorting is enabled.
     *
     * @return <code>true</code> if sorting is enabled.
     */
    protected boolean isClientSortEnabled () {
        return !(getModel() instanceof LazyLoadable);
    }

    /**
     * Getter for property 'currentSortColumn'.
     *
     * @return Value for property 'currentSortColumn'.
     */
    public HeaderCell getCurrentSortColumn () {
        return getHeaderCell(getModel().getSortColumn());
    }

    /**
     * This method removes all content from the grid.
     */
    protected void removeContent () {
        while (getRowCount() > 0) {
            removeRow(getRowCount() - 1);
        }
    }

    /**
     * This meto calculates a row number in the model.
     *
     * @param gridRow is a grid row.
     *
     * @return a model row.
     */
    public int getModelRow (int gridRow) {
        Editable dataModel = getModel();
        return gridRow + dataModel.getPageSize() * dataModel.getCurrentPageNumber();
    }

    /**
     * This method returns a cell comparator.
     *
     * @return a cell comparator.
     */
    protected Comparator getCellComparator () {
        if (cellComparator == null)
            setCellComparator(new DefaultCellComparator(this));
        return cellComparator;
    }

    /**
     * This method draws a content of the grid.
     */
    protected void drawContent () {
        removeContent();
        GridDataModel model = getModel();
        if (model != null) {
            int start = model.getStartRow();
            int end = model.getEndRow();
            boolean empty = model.isEmpty();

            if (!empty && getRowDrawHandler() != null) {
                DeferredCommand.addCommand(new DrawRowCommand(start, end));
            } else {
                for (int i = start; !empty && i <= end; i++)
                    addRow(i - start, model.getRowData(i));
            }

            int count = end - start;
            if (empty) {
                setCurrentRow(-1);
            } else if (count < getCurrentRow()) {
                setCurrentRow(count);
            } else if (getCurrentRow() != -1) {
                setCurrentRow(getCurrentRow());
            } else {
                setCurrentRow(0);
            }
        }
    }

    /**
     * This method fires row drawing event before the row is drawn.
     *
     * @param row is a row number.
     * @param pageSize is a page size.
     * @param data is row data.
     *
     * @return <code>true</code> if the row must be drawn.
     */
    protected boolean fireBeforeDrawEvent(int row, int pageSize, Object[] data) {
        GridRowDrawCallbackHandler handler = getRowDrawHandler();
        return handler == null || handler.beforeDraw(row, pageSize, this, data);
    }

    /**
     * This method fires row drawing event after the row is drawn.
     *
     * @param row is a row number.
     * @param pageSize is a page size.
     * @param data is row data.
     * @return <code>true</code> if drawing must be continued.
     */
    protected boolean fireAfterDrawEvent(int row, int pageSize, Object[] data) {
        GridRowDrawCallbackHandler handler = getRowDrawHandler();
        return handler == null || handler.afterDraw(row, pageSize, this, data);
    }

    /**
     * This method draws header cells.
     */
    protected void drawHeaders () {
        String[] headers = getHeaders();
        for (int i = 0; headers != null && i < getHeaders().length; i++) {
            if (isVisible(i)) {
                HeaderCell cell = getGridCellFactory().create(i, headers[i]);
                cell.displayActive(false);
            }
        }

        detectCurrentSortColumn();
    }

    /**
     * This method returns a header cell widget.
     *
     * @param column is a column number.
     * @return a header cell widget associated with this column.
     */
    public HeaderCell getHeaderCell(int column) {
        return (HeaderCell) getHeaderWidgets().get(column);
    }

    /**
     * This method performs client sorting.
     */
    protected void sortOnClient () {
        if (!isClientSortEnabled())
            return;

        HeaderCell sortColumn = getCurrentSortColumn();
        if (sortColumn != null)
            getModel().setSortColumn(sortColumn.getColumn(), getCellComparator());
    }

    /**
     * This method looks for the sort column in the header list.<p> The method is invoked by the
     * {@link #drawHeaders()}.
     */
    protected void detectCurrentSortColumn () {
        if (getCurrentSortColumn() == null) {
            GridDataModel model = getModel();
            Map sortableHeaders = getSortableHeaders();
            for (Iterator iterator = sortableHeaders.keySet().iterator(); iterator.hasNext();) {
                Integer column = (Integer) iterator.next();
                if (((Boolean) sortableHeaders.get(column)).booleanValue()) {
                    model.setSortColumn(column.intValue());
                }
            }
        }
    }

    /**
     * This method adds grid specific methods into the current widget.<p> You can override the
     * method in your extensions.
     */
    protected void addListeners () {
        addTableListener(
            new TableListener() {
                public void onCellClicked (SourcesTableEvents sender, int row, int cell) {
                    Widget widget = getWidget(row, cell);

                    setCurrentRow(row);

                    if (
                        !isReadOnly(cell)
                        && widget instanceof GridCell
                        ) {
                        GridCell gridCell = (GridCell) widget;
                        gridCell.displayActive(true);
                    }
                }
            }
        );
    }

    /**
     * This method increases ro numbers in the cells.
     *
     * @param startRow is a start row number.
     * @param step     is an increase step.
     */
    protected void increaseRowNumbers (int startRow, int step) {
        for (int i = startRow; i >=0 && i < getRowCount(); i++) {
            for (int j = 0; j < getCellCount(i); j++) {
                Widget widget = getWidget(i, j);
                if (widget instanceof GridCell) {
                    GridCell gridCell = (GridCell) widget;
                    gridCell.setPosition(gridCell.getRow() + step, j);
                }
            }
        }
    }

    /**
     * Getter for property 'invisibleColumns'.
     *
     * @return Value for property 'invisibleColumns'.
     */
    protected List getInvisibleColumns () {
        if (invisibleColumns == null)
            invisibleColumns = new ArrayList();
        return invisibleColumns;
    }

    /**
     * Getter for property 'decorators'.
     *
     * @return Value for property 'decorators'.
     */
    protected List getDecorators() {
        if (decorators == null)
            decorators = new ArrayList();
        return decorators;
    }

    /**
     * Setter for property 'gridPanel'.
     *
     * @param gridPanel Value to set for property 'gridPanel'.
     */
    protected void setGridPanel (GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    /**
     * This method retuns a list of select row listeners.
     *
     * @return a list of the listeners.
     */
    protected List getSelectRowListeners() {
        if (selectRowListeners == null)
            selectRowListeners = new ArrayList();
        return selectRowListeners;
    }

    /**
     * Enables or disables columns resizability.
     *
     * @param resizable a flag to enable or disable resizable columns.
     */
    protected void makeResizable(boolean resizable) {
        if (resizable){
            DOM.setStyleAttribute(getElement(), "table-layout", "fixed");
            resizeTimer.scheduleRepeating(10);
        } else {
            DOM.setStyleAttribute(getElement(), "table-layout", "");
            resizeTimer.cancel();
        }
    }

    /**
     * This command displays one row.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class DrawRowCommand implements IncrementalCommand {
        /** start row number */
        private int start;
        /** end row number */
        private int end;
        /** current row number */
        private int current;

        /**
         * Creates an instance of this class and initilizes internal variables.
         *
         * @param start is a start row number.
         * @param end is an end row number.
         */
        public DrawRowCommand(int start, int end) {
            this.start = start;
            this.end = end;
            this.current = this.start;
            DOM.setStyleAttribute(getBodyElement(), "display", "none");
        }

        /**
         * This method stops when the end row reached.
         *
         * @return <code>true</code> if drawing must be continued.
         */
        public boolean execute() {
            int row = current - start;
            int pageSize = end - start + 1;

            boolean cont = false;
            for (int i = 0; current <= end && i < 1; i++) {
                Object[] data = model.getRowData(current);
                if (fireBeforeDrawEvent(row, pageSize, data))
                    addRow(row, data);
                cont = fireAfterDrawEvent(row, pageSize, data);
                if (!cont)
                    break;
                current++;
            }

            if (!cont || current > end)
                DOM.setStyleAttribute(getBodyElement(), "display", "");
            return cont && current <= end;
        }
    }

    /**
     * This timer automatically resizes the grid.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class ResizeTimer extends Timer {
        /** {@inheritDoc} */
        public void run() {
            resize();
        }
    }
}



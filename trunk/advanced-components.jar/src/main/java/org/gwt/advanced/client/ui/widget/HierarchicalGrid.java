package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.ui.ExpandableCellListener;
import org.gwt.advanced.client.ui.GridEventManager;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.widget.cell.ExpandableCell;
import org.gwt.advanced.client.ui.widget.cell.ExpandableCellFactory;
import org.gwt.advanced.client.ui.widget.cell.GridCell;

import java.util.*;

/**
 * This is a hierarchical grid implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class HierarchicalGrid extends EditableGrid {
    /** subgrid row style name */
    public static final String SUBGRID_ROW_STYLE = "subgrid-row";
    /** a list of expandable cell listeners */
    private Set expandableCellListeners = new HashSet();
    /** a grid panel cache */
    private Map gridPanelCache = new HashMap();
    /** grid panel factories */
    private Map gridPanelFactories = new HashMap();
    /** original instance of the event manager */
    private GridEventManager originalManager;

    /**
     * Creates a new instance of this class.
     *
     * @param headers             is a list of header labels (including invisible).
     * @param columnWidgetClasses is a list of column widget classes.
     */
    public HierarchicalGrid (String[] headers, Class[] columnWidgetClasses) {
        this(headers, columnWidgetClasses, true);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param headers             is a list of header labels (including invisible).
     * @param columnWidgetClasses is a list of column widget classes.
     * @param resizable           is a column resizability flag value.
     */
    public HierarchicalGrid (String[] headers, Class[] columnWidgetClasses, boolean resizable) {
        super(headers, columnWidgetClasses, resizable);
        setGridCellfactory(new ExpandableCellFactory(this));
        setGridRenderer(new HierarchicalGridRenderer(this));
    }

    /**
     * This method adds an expandable cell listener.
     *
     * @param listener a listener instance.
     */
    public void addExpandableCellListener (ExpandableCellListener listener) {
        expandableCellListeners.add(listener);
    }

    /**
     * This method adds a grid panel factory for subgrids.
     *
     * @param column is a column number.
     * @param factory is a factory instance.
     */
    public void addGridPanelFactory(int column, GridPanelFactory factory) {
        getGridPanelFactories().put(new Integer(column), factory);
    }

    /**
     * This method fires the expand cell event.
     *
     * @param cell is an expanded / collapsed cell.
     */
    public void fireExpandCell (ExpandableCell cell) {
        setCurrentRow(cell.getRow());
        if (cell.isExpanded()) {
            expandCell(cell.getRow(), cell.getColumn());
        } else {
            collapseCell(cell.getRow(), cell.getColumn());
        }

        for (Iterator iterator = getExpandableCellListeners().iterator(); iterator.hasNext();) {
            ExpandableCellListener listener = (ExpandableCellListener) iterator.next();
            listener.onCellClick(cell);
        }
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
     * This method sets a widget for the cell<p>
     * If the cell already contains an expandable cell widget, it adds it into this widget.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @param widget is a widget to be placed in the cell.
     */
    public void setWidget (int row, int column, Widget widget) {
        prepareCell(row, column);
        Widget cell = super.getWidget(row, column);
        if (!(cell instanceof ExpandableCell) || widget instanceof ExpandableCell)
            super.setWidget(row, column, widget);
    }

    /**
     * This method looks for a subgrid panel and returns it if the specified cell
     * has been a leats one time expanded.
     *
     * @param parentRow is a parent cell parentRow.
     * @param parentColumn is a parent cell parentColumn.
     * @return a subgrid panel.
     */
    public GridPanel getGridPanel(int parentRow, int parentColumn) {
        Hierarchical model = (Hierarchical)getModel();
        int modelRow = getModelRow(parentRow);
        return (GridPanel)getGridPanelCache().get(model.getSubgridModel(modelRow, parentColumn));
    }

    /**
     * This method expands the cell and adds a subgrid row below the current row.
     *
     * @param parentRow is a row number.
     * @param parentColumn is a column number.
     */
    protected void expandCell (int parentRow, int parentColumn) {
        ((HierarchicalGridRenderer)getGridRenderer()).renderSubgrid(parentRow, parentColumn);
    }

    /**
     * This method collapses the cell and removes an appropriate subgrid row.
     *
     * @param parentRow is a row number..
     * @param parentColumn is a column number.
     */
    protected void collapseCell (int parentRow, int parentColumn) {
        ((HierarchicalGridRenderer)getGridRenderer()).removeSubgrid(parentRow, parentColumn);
    }

    /**
     * This method calculates a correct row number of the subgrid row.
     *
     * @param parentRow is a row number of the expanded / collapsed cell.
     * @param parentColumn is a column number of the expanded / collapsed cell.
     *
     * @return a row number.
     */
    protected int getGridRowNumber(int parentRow, int parentColumn) {
        int result = parentRow + 1;
        for (int i = getCellCount(parentRow) - 1; i > parentColumn; i--) {
            Widget widget = super.getWidget(parentRow, i);
            if (widget instanceof ExpandableCell && ((ExpandableCell)widget).isExpanded()) {
                result++;
            }
        }

        return result;
    }

    /**
     * This method checks the column for expandability.
     *
     * @param column is a column number.
     * @return <code>true</code> if the column is expandable.
     */
    public boolean isExpandable(int column) {
        return getGridPanelFactories().get(new Integer(column)) != null;
    }

    /**
     * Getter for property 'expandableCellListeners'.
     *
     * @return Value for property 'expandableCellListeners'.
     */
    protected Set getExpandableCellListeners () {
        return expandableCellListeners;
    }

    /** {@inheritDoc} */
    protected void increaseRowNumbers (int startRow, int step) {
        for (int i = startRow; i >= 0 && i < getRowCount(); i++) {
            for (int j = 0; j < getCellCount(i); j++) {
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

    /**
     * Getter for property 'gridPanelCache'.
     *
     * @return Value for property 'gridPanelCache'.
     */
    protected Map getGridPanelCache () {
        return gridPanelCache;
    }

    /**
     * Getter for property 'gridPanelFactories'.
     *
     * @return Value for property 'gridPanelFactories'.
     */
    protected Map getGridPanelFactories () {
        return gridPanelFactories;
    }

    /**
     * Enables / disables the event manager.
     *
     * @param enabled is a flag that means whether the manager must be enabled.
     */
    protected void enableEvents(boolean enabled) {
        if (enabled)
            getGridPanel().setGridEventManager(originalManager);
        else {
            this.originalManager = getGridPanel().getGridEventManager();
            getGridPanel().setGridEventManager(null);
        }
    }
}

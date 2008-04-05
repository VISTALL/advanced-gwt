package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.ui.ExpandableCellListener;
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
        if (!(cell instanceof ExpandableCell))
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
        GridDataModel submodel = null;
        GridPanelFactory factory = null;
        GridDataModel model = getModel();

        int modelRow = getModelRow(parentRow);
        if (model instanceof Hierarchical) {
            submodel = ((Hierarchical)model).getSubgridModel(modelRow, parentColumn);
            factory = (GridPanelFactory) getGridPanelFactories().get(new Integer(parentColumn));

            if (submodel == null && factory != null) {
                submodel = factory.create(modelRow, model);
                ((Hierarchical)model).addSubgridModel(modelRow, parentColumn, submodel);
            }
        }

        if (factory != null) {
            ((Hierarchical)model).setExpanded(modelRow, parentColumn, true);
            int thisRow = getGridRowNumber(parentRow, parentColumn);
            increaseRowNumbers(thisRow, 1);
            insertRow(thisRow);
            if (getCurrentRow() > parentRow)
                setCurrentRow(getCurrentRow() + 1);

            for (int i = 0; i < parentColumn; i++)
                setText(thisRow, i, "");
            GridPanel gridPanel = (GridPanel) getGridPanelCache().get(submodel);
            if (gridPanel == null) {
                gridPanel = factory.create(submodel);
                gridPanel.getTopToolbar().setSaveButtonVisible(false);
                gridPanel.getBottomToolbar().setSaveButtonVisible(false);
                gridPanel.display();

                getGridPanelCache().put(submodel, gridPanel);
            }

            getFlexCellFormatter().setColSpan(thisRow, parentColumn, getCellCount(parentRow) - parentColumn);
            getRowFormatter().setStyleName(thisRow, SUBGRID_ROW_STYLE);
            getCellFormatter().setStyleName(thisRow, parentColumn, "subgrid-cell");
            setWidget(thisRow, parentColumn, gridPanel);
        }
    }

    /**
     * This method collapses the cell and removes an appropriate subgrid row.
     *
     * @param parentRow is a row number..
     * @param parentColumn is a column number.
     */
    protected void collapseCell (int parentRow, int parentColumn) {
        GridDataModel dataModel = getModel();
        if (dataModel instanceof Hierarchical) {
            ((Hierarchical) dataModel).setExpanded(getModelRow(parentRow), parentColumn, false);
            int thisRow = getGridRowNumber(parentRow, parentColumn);
            increaseRowNumbers(thisRow, -1);
            removeRow(thisRow);
            if (getCurrentRow() > parentRow)
                setCurrentRow(getCurrentRow() - 1);
        }
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

    /** {@inheritDoc} */
    protected void drawContent () {
        super.drawContent();

        GridDataModel dataModel = getModel();
        if (dataModel instanceof Hierarchical) {
            int rowCount = getRowCount();
            int expandedCells = 0;
            for (int i = 0; i < rowCount; i++) {
                int modelRow = super.getModelRow(i);
                int expandedBefore = expandedCells;
                for (int j = getCellCount(i + expandedBefore) - 1; j >= 0; j--) {
                    if (((Hierarchical)dataModel).isExpanded(modelRow, j)) {
                        expandCell(i + expandedBefore, j);
                        expandedCells++;
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public int getModelRow(int gridRow) {
        return super.getModelRow(gridRow) - getSubgridRowsBefore(gridRow);
    }

    /** {@inheritDoc} */
    protected void addRow (int row, Object[] data) {
        GridDataModel dataModel = getModel();
        if (dataModel instanceof Hierarchical) {
            Hierarchical model = (Hierarchical) dataModel;
            int modelRow = getModelRow(row);

            for (int i = 0; i < data.length; i++) {
                if (isVisible(i)) {
                    GridCell gridCell = getGridCellFactory().create(row, i, data[i]);
                    gridCell.displayActive(false);

                    if (getGridPanelFactories().get(new Integer(i)) != null) {
                        ExpandableCell expandableCell =
                            (ExpandableCell) getGridCellFactory().create(row, i, gridCell);

                        if (model.isExpanded(modelRow, i))
                            expandableCell.setExpanded(true);
                        expandableCell.displayActive(false);
                    }
                }
            }
        } else {
            super.addRow(row, data);
        }
    }

    /**
     * Getter for property 'expandableCellListeners'.
     *
     * @return Value for property 'expandableCellListeners'.
     */
    protected Set getExpandableCellListeners () {
        return expandableCellListeners;
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
            if (SUBGRID_ROW_STYLE.equals(getRowFormatter().getStyleName(i)))
                result++;
        }
        return result;
    }

    /**
     * This method adds grid specific methods into the current widget.<p>
     * You can override the method in your extensions.
     */
    protected void addListeners() {
        addTableListener(
            new TableListener() {
                public void onCellClicked (SourcesTableEvents sender, int row, int cell) {
                    Widget widget = getWidget(row, cell);

                    if (!SUBGRID_ROW_STYLE.equals(getRowFormatter().getStyleName(row)))
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
}

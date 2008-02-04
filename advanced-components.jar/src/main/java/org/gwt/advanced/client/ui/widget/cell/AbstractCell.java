package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is an sbtract cell implementation.<p>
 * All existing cells extend this class.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public abstract class AbstractCell extends SimplePanel implements GridCell {
    /** cell value */
    private Object value;
    /** row number */
    private int row;
    /** column number */
    private int column;
    /** grid instance which the cell belongs to */
    private FlexTable grid;
    /** active status flag */
    private boolean active;
    /** default cell focus listener */
    private FocusListener focusListener;
    /** default cell keybord listner */
    private KeyboardListener keyboardListener;
    /** label instance to be used in most cell extensions */
    private Label label;

    /** {@inheritDoc} */
    public Object getValue () {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue (Object value) {
        this.value = value;
    }

    /** {@inheritDoc} */
    public void setPosition (int row, int column) {
        this.row = row;
        this.column = column;
    }

    /** {@inheritDoc} */
    public int getRow () {
        return row;
    }

    /** {@inheritDoc} */
    public int getColumn () {
        return column;
    }

    /** {@inheritDoc} */
    public FlexTable getGrid () {
        return grid;
    }

    /** {@inheritDoc} */
    public void setGrid (FlexTable grid) {
        this.grid = grid;
    }

    /** {@inheritDoc} */
    public boolean isActive () {
        return active;
    }

    /** {@inheritDoc} */
    public void displayActive(boolean active) {
        if (getStyleName() == null || "".equals(getStyleName()))
            setStyleName("data-cell");
        
        if (active && !isActive()) {
            setActive(true);
            removeStyleName("passive-cell");
            addStyleName("active-cell");

            Widget widget = createActive();
            prepare(widget);
            addListeners(widget);
            setFocus(true);
        } else if (!active) {
            if (isActive())
                removeListeners(getWidget());

            setActive(false);
            removeStyleName("active-cell");
            addStyleName("passive-cell");
            
            prepare(createInactive());
        }

        FlexTable grid = getGrid();
        if (grid instanceof EditableGrid && !((EditableGrid) grid).isReadOnly(getColumn())) {
            removeStyleName("readonly-cell");
            addStyleName("editable-cell");
        } else {
            removeStyleName("editable-cell");
            addStyleName("readonly-cell");
        }
    }

    /** {@inheritDoc} */
    public boolean valueEqual (Object value) {
        Object thisValue = getValue();
        return thisValue == value || thisValue != null && thisValue.equals(value);
    }

    /**
     * This method creates an active cell element (content).
     *
     * @return a content element.
     */
    protected abstract Widget createActive ();

    /**
     * This method creates an inactive cell element (content).
     *
     * @return a content element.
     */
    protected abstract Widget createInactive ();

    /**
     * Setter for property 'active'.
     *
     * @param active Value to set for property 'active'.
     */
    protected void setActive (boolean active) {
        this.active = active;
    }

    /**
     * This method prepares a cell before displaying it.
     *
     * @param widget is a content widget.
     */
    protected void prepare(Widget widget) {
        FlexTable grid = getGrid();
        int row = getRow();
        int column = getColumn();

        if (getWidget() != null)
            remove(getWidget());

        add(widget);

        Widget current = null;
        if (row < grid.getRowCount() && column < grid.getCellCount(row))
            current = grid.getWidget(row, column);

        if (current != this)
            grid.setWidget(row, column, this);
    }

    /**
     * This method adds default listeners to the content widget.
     *
     * @param widget is a content widget.
     */
    protected void addListeners(Widget widget) {
        if (focusListener == null)
            focusListener = new CellFocusListener();
        if (keyboardListener == null)
            keyboardListener = new CellKeyboardListener();

        if (widget instanceof SourcesFocusEvents)
            ((SourcesFocusEvents)widget).addFocusListener(focusListener);
        if (widget instanceof SourcesKeyboardEvents)
            ((SourcesKeyboardEvents)widget).addKeyboardListener(keyboardListener);
    }

    /**
     * This method removes default listeners from the content widget.
     *
     * @param widget is a content widget.
     */
    protected void removeListeners(Widget widget) {
        if (widget instanceof SourcesFocusEvents)
            ((SourcesFocusEvents)widget).removeFocusListener(focusListener);
        if (widget instanceof SourcesKeyboardEvents)
            ((SourcesKeyboardEvents)widget).removeKeyboardListener(keyboardListener);
    }

    /**
     * Getter for property 'label'.
     *
     * @return Value for property 'label'.
     */
    protected Label getLabel () {
        if (label == null)
            label = new Label();
        
        return label;
    }

    /**
     * Setter for property 'label'.
     *
     * @param label Value to set for property 'label'.
     */
    protected void setLabel (Label label) {
        this.label = label;
    }
}
package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.ListBox;
import org.gwt.advanced.client.ui.widget.EditableGrid;

import java.util.Comparator;
import java.util.Date;

/**
 * This is a default implementation of grid cell comparator.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class DefaultCellComparator implements Comparator {
    /** grid instance */
    private EditableGrid grid;

    /**
     * Creates an instance of this class.
     *
     * @param grid is a grid.
     */
    public DefaultCellComparator (EditableGrid grid) {
        this.grid = grid;
    }

    /**
     * Compares cell values.
     *
     * @param o1 is a cell value.
     * @param o2 is a cell value.
     *
     * @return a result of comparison.
     */
    public int compare (Object o1, Object o2) {
        Object value1 = prepareValue(o1);
        Object value2 = prepareValue(o2);

        EditableGrid grdi = getGrid();
        if (grdi.isAscending(grdi.getCurrentSortColumn().getColumn()))
            return ((Comparable) value1).compareTo(value2);
        else
            return -((Comparable) value1).compareTo(value2);
    }

    /**
     * This method prepares the specified value and replace it with the default one
     * if it's <code>null</code>.
     *
     * @param value is a value to be prepared.
     * @return a prepared value.
     */
    protected Object prepareValue(Object value) {
        Class columnType =
            getGrid().getColumnWidgetClasses()[getGrid().getCurrentSortColumn().getColumn()];

        Object result;
        if (TextBoxCell.class.equals(columnType)) {
            result = value == null ? "" : String.valueOf(value);
        } else if (ListCell.class.equals(columnType)) {
            result = getLabelText((ListBox) value);
        } else if (BooleanCell.class.equals(columnType)) {
            //Boolean is not comparable in GWT?
            result = Boolean.valueOf(String.valueOf(value)).toString();
        } else if (ShortCell.class.equals(columnType)) {
            result = value == null ? new Short((short)0) : (Short)value;
        } else if (IntegerCell.class.equals(columnType)) {
            result = value == null ? new Integer((short)0) : (Integer)value;
        } else if (LongCell.class.equals(columnType)) {
            result = value == null ? new Long((short)0) : (Long)value;
        } else if (FloatCell.class.equals(columnType)) {
            result = value == null ? new Float((short)0) : (Float)value;
        } else if (DoubleCell.class.equals(columnType)) {
            result = value == null ? new Double((short)0) : (Double)value;
        } else if (NumberCell.class.equals(columnType)) {
            result = value == null ? new Double((short)0) : (Double) value;
        } else if (DateCell.class.equals(columnType)) {
            result = value == null ? new Date() : (Date) value;
        } else {
            result = value == null ? "" : value;
        }

        return result;
    }

    /**
     * This method returns a text for the inactive label.
     *
     * @param listBox is a list box.
     *
     * @return a label text.
     */
    protected String getLabelText (ListBox listBox) {
        String labelText = "";
        if (listBox != null) {
            int index = -1;

            if (listBox.getItemCount() > 0)
                index = listBox.getSelectedIndex();

            if (index != -1)
                labelText = listBox.getItemText(index);
            else if (listBox.getItemCount() > 0)
                labelText = listBox.getItemText(0);
        }
        return labelText;
    }

    /**
     * Getter for property 'grid'.
     *
     * @return Value for property 'grid'.
     */
    protected EditableGrid getGrid () {
        return grid;
    }
}

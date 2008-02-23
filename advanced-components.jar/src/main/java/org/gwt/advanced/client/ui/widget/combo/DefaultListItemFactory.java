package org.gwt.advanced.client.ui.widget.combo;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

import java.util.Date;

import org.gwt.advanced.client.ui.widget.DatePicker;
import org.gwt.advanced.client.datamodel.IconItem;

/**
 * This factory tries to detect what should be returned by value type.<p/>
 * If the value is instance of <code>String</code>, <code>Number</code> or <code>Date</code> it returns this value
 * wrapped in appropriate widget. Otherwise it returns <code>null</code>.<p/>
 * If you want to use more complex objects you should develop your own factory.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class DefaultListItemFactory implements ListItemFactory {
    /**
     * See class docs.
     *
     * @param value is a value to be adopted.
     * @return a widget to be inserted into the list.
     */
    public Widget createWidget(Object value) {
        if (value == null)
            return new Label();
        else if (value instanceof String || value instanceof Number)
            return new Label(String.valueOf(value));
        else if (value instanceof Date) {
            DatePicker datePicker = new DatePicker((Date) value);
            datePicker.setChoiceButtonVisible(false);
            return datePicker;
        } else if (value instanceof IconItem) {
            IconItem item = (IconItem) value;
            FlexTable table = new FlexTable();
            table.setStyleName("icon-item");
            table.setWidget(0, 0, new Image(item.getImageName()));
            table.setWidget(0, 1, new Label(item.getLabel()));
            table.getCellFormatter().setWidth(0, 0, "1%");
            table.getCellFormatter().setWidth(0, 1, "99%");
            return table;
        } else
            return null;
    }

    /** {@inheritDoc} */
    public String convert(Object value) {
        if (value == null)
            return "";
        else if (value instanceof String || value instanceof Number)
            return String.valueOf(value);
        else if (value instanceof Date)
            return new DatePicker((Date)value).getTextualDate();
        else if (value instanceof IconItem)
            return ((IconItem)value).getLabel();
        else
            return "";
    }
}

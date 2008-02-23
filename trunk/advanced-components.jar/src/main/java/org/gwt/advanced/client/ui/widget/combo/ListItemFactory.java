package org.gwt.advanced.client.ui.widget.combo;

import com.google.gwt.user.client.ui.Widget;

/**
 * This is a list item factory that helps to produce items in the
 * {@link org.gwt.advanced.client.ui.widget.ListPopupPanel}.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface ListItemFactory {
    /**
     * This method creates a new widget that should be inserted into the list.
     *
     * @param value is a value to be used to construct the widget.
     * @return a widget instance (can be equal to <code>null</code>).
     */
    Widget createWidget(Object value);

    /**
     * This method should convert the value to the text to be displayed in the selection text box.
     *
     * @param value is a value to be converted.
     * @return textual representation of the value.
     */
    String convert(Object value);
}

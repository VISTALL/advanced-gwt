package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import org.gwt.advanced.client.ui.CalendarListener;
import org.gwt.advanced.client.ui.widget.Calendar;
import org.gwt.advanced.client.ui.widget.EditableGrid;

import java.util.Date;

/**
 * This is a cell implementation for <code>Date</code> values.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class DateCell extends AbstractCell {
    /** a calendart change listener */
    private CalendarListener changeListener;
    /** a calendar widget */
    private Calendar calendar;
    /** a date picker popup */
    private PopupPanel popup;

    /** {@inheritDoc} */
    protected Widget createActive() {
        PopupPanel panel = getPopup();
        Calendar calendar = getCalendar();
        calendar.setSelectedDate((Date) getValue());
        calendar.display();

        if (panel.getWidget() != calendar)
            panel.add(calendar);

        panel.show();
        
        int left = getAbsoluteLeft();
        if (left + calendar.getOffsetWidth() > Window.getClientWidth())
            left -= (left + calendar.getOffsetWidth() - Window.getClientWidth());
        if (left < 0)
            left = 0;
        panel.setPopupPosition(left, getAbsoluteTop());

        FlexTable table = getGrid();
        if (table instanceof EditableGrid)
            ((EditableGrid)table).fireStartEdit(this);

        return getLabel();
    }

    /** {@inheritDoc} */
    protected Widget createInactive() {
        Label dateText = getLabel();
        dateText.setText(formatDate((Date) getValue()));

        removeStyleName("active-cell");
        addStyleName("passive-cell");
        addStyleName("date-cell");
        return dateText;
    }

    /** {@inheritDoc} */
    public void setFocus(boolean focus) {
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getCalendar().getSelectedDate();
    }

    /** {@inheritDoc} */
    protected void addListeners(Widget widget) {
        if (changeListener == null) {
            final GridCell cell = this;
            changeListener = new CalendarListener() {
                public void onChange(Widget sender, Date date) {
                    getPopup().hide();
                    ((AbstractCell)cell).setActive(false);

                    FlexTable table = getGrid();
                    boolean valid = true;
                    if (table instanceof EditableGrid)
                        valid = ((EditableGrid)table).fireFinishEdit(cell, getNewValue());

                    if (valid) {
                        createInactive();
                    }
                }

                public void onCancel(Widget sender) {
                    getPopup().hide();
                    ((AbstractCell)cell).setActive(false);
                    createInactive();
                }
            };
        }
        getCalendar().addCalendarListener(changeListener);
    }

    /** {@inheritDoc} */
    protected void removeListeners(Widget widget) {
        getCalendar().removeCalendarListener(changeListener);
    }

    /** {@inheritDoc} */
    public void setValue(Object value) {
        if (value == null)
            value = new Date();
        super.setValue(value);
    }

    /**
     * This method formates the specified date.
     *
     * @param date is a date to be formatted.
     * @return a date string.
     */
    protected String formatDate (Date date) {
        return DateTimeFormat.getLongDateFormat().format(date);
    }
    
    /**
     * Getter for property 'calendar'.
     *
     * @return Value for property 'calendar'.
     */
    protected Calendar getCalendar () {
        if (calendar == null) {
            calendar = new Calendar();
            calendar.setShowTime(false);
        }

        return calendar;
    }

    /**
     * Getter for property 'popup'.
     *
     * @return Value for property 'popup'.
     */
    protected PopupPanel getPopup () {
        if (popup == null)
            popup = new PopupPanel(false, true);

        return popup;
    }
}

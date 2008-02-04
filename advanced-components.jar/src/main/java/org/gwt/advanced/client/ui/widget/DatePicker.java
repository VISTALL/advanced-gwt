package org.gwt.advanced.client.ui.widget;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.AdvancedWidget;
import org.gwt.advanced.client.ui.CalendarListener;

import java.util.Date;

/**
 * This is a date picker widget.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class DatePicker extends DockPanel implements AdvancedWidget, CalendarListener {
    /** date text box */
    private TextBox dateBox;
    /** open calendar button */
    private ToggleButton openCalendarButton;
    /** calendar popup panel */
    private PopupPanel calendarPanel;
    /** calendar widget */
    private Calendar calendar;
    /** open calendar event listener */
    private ClickListener openCalendarClickListener;
    /** the date */
    private Date date;
    /** time visibility flag */
    private boolean timeVisible = false;
    /** open calendar button visibility flag */
    private boolean openCalendarButtonVisible = true;

    /**
     * Creates an instance of this class.
     *
     * @param initialDate is ana initial date.
     */
    public DatePicker(Date initialDate) {
        this.date = initialDate;
    }

    /**
     * Getter for property 'timeVisible'.
     *
     * @return Value for property 'timeVisible'.
     */
    public boolean isTimeVisible() {
        return timeVisible;
    }

    /**
     * Setter for property 'timeVisible'.
     *
     * @param timeVisible Value to set for property 'timeVisible'.
     */
    public void setTimeVisible(boolean timeVisible) {
        this.timeVisible = timeVisible;
    }

    /**
     * Getter for property 'openCalendarButtonVisible'.
     *
     * @return Value for property 'openCalendarButtonVisible'.
     */
    public boolean isOpenCalendarButtonVisible() {
        return openCalendarButtonVisible;
    }

    /**
     * Setter for property 'openCalendarButtonVisible'.
     *
     * @param openCalendarButtonVisible Value to set for property 'openCalendarButtonVisible'.
     */
    public void setOpenCalendarButtonVisible(boolean openCalendarButtonVisible) {
        this.openCalendarButtonVisible = openCalendarButtonVisible;
    }

    /** {@inheritDoc} */
    public void onChange(Widget sender, Date oldValue) {
        getCalendarPanel().hide();
        Date date = getCalendar().getDate();
        getDateBox().setText(getFormat().format(date));
        this.date = date; 
    }

    /** {@inheritDoc} */
    public void onCancel(Widget sender) {
        getCalendarPanel().hide();
    }

    /**
     * Getter for property 'date'.
     *
     * @return Value for property 'date'.
     */
    public Date getDate() {
        return date;
    }
    
    /** {@inheritDoc} */
    public void display() {
        setStyleName("advanced-DatePicker");

        removeWidgets();

        Date date = getDate();
        if (date != null)
            getDateBox().setText(getFormat().format(date));
        add(getDateBox(), DockPanel.CENTER);
        getDateBox().setStyleName("date-box");
        setCellWidth(getDateBox(), "100%");

        if (isOpenCalendarButtonVisible()) {
            add(getOpenCalendarButton(), DockPanel.EAST);
            getOpenCalendarButton().setStyleName("open-calendar-button");
            setCellWidth(getOpenCalendarButton(), "1%");
        }

        addComponentListeners();

        getCalendar().setShowTime(isTimeVisible());
    }

    /**
     * This method adds different listeners to elements of the widget.
     */
    protected void addComponentListeners() {
        if (openCalendarClickListener == null)
            openCalendarClickListener = new OpenCalendarClickListener();

        if (isOpenCalendarButtonVisible()) {
            ToggleButton calendarButton = getOpenCalendarButton();
            calendarButton.removeClickListener(openCalendarClickListener);
            calendarButton.addClickListener(openCalendarClickListener);
        } else {
            TextBox box = getDateBox();
            box.removeClickListener(openCalendarClickListener);
            box.addClickListener(openCalendarClickListener);
        }

        getCalendar().addCalendarListener(this);
    }

    /**
     * This method cleans the main panel.
     */
    protected void removeWidgets() {
        for (int i = 0; i < getWidgetCount(); i++)
            remove(i);
    }

    /**
     * Getter for property 'dateBox'.
     *
     * @return Value for property 'dateBox'.
     */
    protected TextBox getDateBox () {
        if (dateBox == null) {
            dateBox = new TextBox();
            dateBox.setReadOnly(true);
        }
        
        return dateBox;
    }

    /**
     * Getter for property 'openCalendarButton'.
     *
     * @return Value for property 'openCalendarButton'.
     */
    protected ToggleButton getOpenCalendarButton () {
        if (openCalendarButton == null)
            openCalendarButton = new ToggleButton("..");

        return openCalendarButton;
    }

    /**
     * Getter for property 'calendar'.
     *
     * @return Value for property 'calendar'.
     */
    protected Calendar getCalendar () {
        if (calendar == null) {
            calendar = new Calendar();
            calendar.setSelectedDate(getDate());
        }

        return calendar;
    }

    /**
     * Getter for property 'calendarPanel'.
     *
     * @return Value for property 'calendarPanel'.
     */
    protected PopupPanel getCalendarPanel() {
        if (calendarPanel == null) {
            calendarPanel = new PopupPanel(false, true);
            calendarPanel.add(getCalendar());
        }
        return calendarPanel;
    }

    /**
     * Getter for property 'format'.
     *
     * @return Value for property 'format'.
     */
    protected DateTimeFormat getFormat() {
        DateTimeFormat format;
        if (isTimeVisible())
            format = DateTimeFormat.getLongDateTimeFormat();
        else
            format = DateTimeFormat.getLongDateFormat();
        return format;
    }

    /**
     * This is an open calendar evbent listener implementation.
     */
    protected class OpenCalendarClickListener implements ClickListener {
        /** {@inheritDoc} */
        public void onClick (Widget sender) {
            Calendar calendar = getCalendar();

            if (getDate() != null)
                calendar.setSelectedDate(getDate());
            else
                calendar.setSelectedDate(new Date());

            calendar.display();
            getCalendarPanel().show();

            int left = getAbsoluteLeft() + getDateBox().getOffsetWidth();
            if (left + getCalendar().getOffsetWidth() > Window.getClientWidth())
                left -= getCalendar().getOffsetWidth();

            getCalendarPanel().setPopupPosition(left, getAbsoluteTop());
        }
    }
}

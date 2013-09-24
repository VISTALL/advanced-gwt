/*
 * Copyright 2008-2013 Sergey Skladchikov
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

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import org.gwt.advanced.client.ui.CalendarListener;

import java.util.Date;

/**
 * This is a date picker widget.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class DatePicker extends TextButtonPanel<Date> implements CalendarListener<Calendar>, HasChangeHandlers {
    /** calendar popup panel */
    private PopupPanel calendarPanel;
    /** calendar widget */
    private Calendar calendar;
    /** open calendar event handler */
    private ClickHandler openCalendarClickHandler;
    /** the date */
    private Date date;
    /** time visibility flag */
    private boolean timeVisible = false;
    /** date and time format for the widget */
    private String format;

    /**
     * Creates an instance of this class and does nothing else.
     */
    public DatePicker() {
        this(null);
    }

    /**
     * Creates an instance of this class.
     *
     * @param initialDate is an initial date.
     */
    public DatePicker(Date initialDate) {
        super();
        this.date = initialDate;
        getCalendar().setShowTime(isTimeVisible());
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
        if (getValue() != null)
            getSelectedValue().setText(getFormat().format(date));
        getCalendar().setShowTime(timeVisible);
    }

    /** {@inheritDoc} */
    @Override
    public void onChange(Calendar sender, Date oldValue) {
        getCalendarPanel().hide();
        Date date = getCalendar().getDate();
        getSelectedValue().setText(getFormat().format(date));
        this.date = date;
        fireEvent(new ChangeEvent(){});
    }

    /** {@inheritDoc} */
    @Override
    public void onCancel(Calendar sender) {
        getCalendarPanel().hide();
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addHandler(handler, ChangeEvent.getType());
    }

    /**
     * Getter for property 'date'.
     *
     * @return Value for property 'date'.
     * @deprecated use {@link #getValue()} instead.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets a date for this date picker.
     *
     * @param date is a date to set.
     * @deprecated use {@link #setValue(Date)} instead.
     */
    public void setDate(Date date) {
        this.date = date;
        if (date != null)
            getSelectedValue().setText(getFormat().format(date));
        else
            getSelectedValue().setText("");
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated you don't have to invoke this method any more 
     */
    @SuppressWarnings({"deprecation"})
    @Override
    public void display() {
    }

    /**
     * Gets a textual representation of the date using format properties.
     *
     * @return textual representation.
     */
    public String getTextualDate() {
        if (getValue() != null)
            return getFormat().format(getValue());
        else
            return "";
    }

    /**
     * Sets a format string for the displaying date and time.<p/>
     * It overrides the format specified in the resource file.
     * For more details see docs for the <code>DateTimeFormat</code> class.
     *
     * @param format is a format string.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /** Similar to {@link #getDate()} */
    @Override
    public Date getValue() {
        return date;
    }

    /** Similar to {@link #setDate(Date)} and doesn't send any event */
    @Override
    public void setValue(Date value) {
        setValue(value, false);
    }

    /** Similar to {@link #setDate(Date)} and sends {@code ValueChangeEvent} if {@code fireEvents = true} */
    @Override
    public void setValue(Date value, boolean fireEvents) {
        this.date = value;
        if (date != null)
            getSelectedValue().setText(getFormat().format(date));
        else
            getSelectedValue().setText("");
        if (fireEvents) {
            fireEvent(new ValueChangeEvent<Date>(value){});
        }
    }

    /**
     * Adds a value change handler to the component that will be invoked only if
     * {@link #setValue(Object, boolean)} has the second parameter = {@code true}.<p/>
     *
     * Note that the widget doesn't fire the event if you don't use the method specified above.
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /** {@inheritDoc} */
    protected void prepareSelectedValue() {
        super.prepareSelectedValue();
        getSelectedValue().setReadOnly(true);
        Date date = getValue();
        if (date != null)
            getSelectedValue().setText(getFormat().format(date));
    }

    /** {@inheritDoc} */
    protected String getDefaultImageName() {
        return "calendar.gif";
    }

    /**
     * This method adds different listeners to elements of the widget.
     */
    @SuppressWarnings({"unchecked"})
    protected void addComponentListeners() {
        if (openCalendarClickHandler == null) {
            openCalendarClickHandler = new OpenCalendarClickHandler();

            ToggleButton calendarButton = getChoiceButton();
            calendarButton.addClickHandler(openCalendarClickHandler);
            TextBox box = getSelectedValue();
            box.addClickHandler(openCalendarClickHandler);
        }

        getCalendar().addCalendarListener(this);
    }

    /**
     * Getter for property 'calendar'.
     *
     * @return Value for property 'calendar'.
     */
    protected Calendar getCalendar () {
        if (calendar == null) {
            calendar = new Calendar();
            if (getValue() != null)
                calendar.setSelectedDate(getValue());
            else
                calendar.setSelectedDate(new Date());
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
        if (this.format != null)
            return DateTimeFormat.getFormat(this.format);

        DateTimeFormat format;
        if (isTimeVisible())
            format = DateTimeFormat.getFormat(Calendar.constants.dateTimeFormat());
        else
            format = DateTimeFormat.getFormat(Calendar.constants.dateFormat());
        return format;
    }

    /**
     * This is an open calendar evbent handler implementation.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class OpenCalendarClickHandler implements ClickHandler {
        /** {@inheritDoc} */
        @Override
        public void onClick(ClickEvent event) {
            if (event.getSource() == getSelectedValue() && isChoiceButtonVisible())
                return;
            
            Calendar calendar = getCalendar();

            if (getValue() != null)
                calendar.setSelectedDate(getValue());
            else
                calendar.setSelectedDate(new Date());

            calendar.display();
            getCalendarPanel().show();

            int left = getAbsoluteLeft() + getSelectedValue().getOffsetWidth();
            if (left + getCalendar().getOffsetWidth() > Window.getClientWidth())
                left -= getCalendar().getOffsetWidth();

            getCalendarPanel().setPopupPosition(left, getAbsoluteTop());
            getChoiceButton().setDown(false);
        }
    }
}

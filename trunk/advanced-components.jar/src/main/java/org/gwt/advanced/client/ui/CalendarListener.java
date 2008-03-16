package org.gwt.advanced.client.ui;

import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * This is a calendar listener.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface CalendarListener {
    /**
     * This method is invoked when a user chooses a date.
     *
     * @param sender is a calendar which sent the event.
     * @param oldValue is an old date value.
     */
    void onChange(Widget sender, Date oldValue);

    /**
     * This method is invoked on cancel.
     *
     * @param sender is a calendar which sent the event.
     */
    void onCancel(Widget sender);
}

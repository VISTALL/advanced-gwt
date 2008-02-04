package org.gwt.advanced.client.ui;

import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

public interface CalendarListener {
    void onChange(Widget sender, Date oldValue);
    void onCancel(Widget sender);
}

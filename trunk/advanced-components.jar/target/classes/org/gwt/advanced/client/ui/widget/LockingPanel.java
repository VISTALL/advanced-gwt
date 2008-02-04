package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This is a panel that appears when one of components like to lock the screen.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class LockingPanel extends PopupPanel {
    /**
     * Creates an instance of this class.
     */
    public LockingPanel() {
        super(false, false);
    }

    /**
     * Shows the panel.
     */
    public void lock() {
        setStyleName("advanced-LockingPanel");
        setPopupPosition(0, 0);
        setWidth("100%");
        setHeight("100%");
        setPixelSize(Window.getClientWidth(), Window.getClientHeight());

        show();
    }

    /**
     * Hides the panel.
     */
    public void unlock() {
        hide();
    }
}

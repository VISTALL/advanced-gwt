package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.GridPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * This is an interface of grid event managers.<p/>
 * All classes which handle events produced by the grid must implement it.<br/>
 * Usually you won't have to implement this interface directly. Extend
 * {@link org.gwt.advanced.client.ui.widget.DefaultGridEventManager} instead. 
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public interface GridEventManager extends FocusListener, TableListener, KeyboardListener {
    /**
     * This method dispatches events and performs actions related to a concrete combinations of
     * keys.
     *
     * @param panel is a grid panel that invokes the manager.
     * @param keyCode is a key code.
     * @param modifiers is a list of modifiers defined in <code>KeyboardListener</code>.
     */
    void dispatch(GridPanel panel, char keyCode, int modifiers);
}

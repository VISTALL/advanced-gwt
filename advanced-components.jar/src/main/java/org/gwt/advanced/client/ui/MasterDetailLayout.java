package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.GridPanel;

/**
 * This interface describes an abstract master-detail layout where dependent grid panels
 * must be placed in.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface MasterDetailLayout {
    /**
     * This method adds a grid panel into the master-detail panel automatically choosing a cell to put it in.
     *
     * @param panel is a grid panel to be placed in.
     * @param parent is a parent grid panel.
     * @param caption is a grid panel caption (optional parameter).
     * @return <code>true</code> if the panel has been placed into the master-detail panel.
     */
    boolean addGridPanel(GridPanel panel, GridPanel parent, String caption);

    /**
     * This method removes a grid panel from this master-detail panel automatically finding it in cells.<p/>
     * Child grid panels will be removed as well.
     *
     * @param panel is a panel to be removed.
     * @return <code>true</code> is the widget has been found and removed.
     */
    boolean removeGridPanel(GridPanel panel);
}

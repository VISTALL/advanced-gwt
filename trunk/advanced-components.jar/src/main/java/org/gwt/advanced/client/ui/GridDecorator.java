package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.EditableGrid;

/**
 * This is a grid decorator interface.<p/>
 * It describes objects applicable for the grid look & feel change on content drawing.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.1.0
 */
public interface GridDecorator {
    /**
     * This method is invoked when content drawing finished.
     *
     * @param grid is a grid to decorate.
     */
    void decorate(EditableGrid grid);
}

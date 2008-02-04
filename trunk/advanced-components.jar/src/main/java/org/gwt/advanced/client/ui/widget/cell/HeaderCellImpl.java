package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is a header cell implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class HeaderCellImpl extends AbstractCell implements HeaderCell {
    /** one pixel image name */
    public static final String SINGLE_IMAGE = "advanced/images/single.gif";

    /** this is a dock panel for the cell elemnts */
    private DockPanel panel;
    /** order of sorting image */
    private Image image;
    /** sortable flag */
    private boolean sortable = true;
    /** ascending order flag */
    private boolean ascending = true;
    /** sorted flag */
    private boolean sorted = false;
    /** initialized status flag */
    private boolean initialized;

    /** {@inheritDoc} */
    public void displayActive(boolean active) {
        if (!isInitialized()) {
            prepare(createInactive());
            addListeners(null);
            initialized = true;

            addStyleName("header-cell");

            if (isSortable()) {
                addStyleName("sortable-header");
            } else {
                addStyleName("non-sortable-header");
            }
        }
    }

    /** {@inheritDoc} */
    protected Widget createActive() {
        return createInactive();
    }

    /** {@inheritDoc} */
    protected Widget createInactive() {
        DockPanel panel = getDockPanel();
        panel.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        Label label = getLabel();
        Image image = getImage();

        label.setText(String.valueOf(getValue()));

        if (panel.getWidgetIndex(label) == -1) {
            panel.add(label, DockPanel.CENTER);
            panel.setCellWidth(label, "100%");
        }

        if (isSortable() && panel.getWidgetIndex(image) == -1)
            panel.add(image, DockPanel.EAST);

        return panel;
    }

    /** {@inheritDoc} */
    protected void addListeners(Widget widget) {
        if (isSortable()) {
            DOM.setEventListener(getLabel().getElement(), new EventListener() {
                public void onBrowserEvent(Event event) {
                    if (DOM.eventGetType(event) == Event.ONCLICK)
                        sort();
                }
            });
        }
    }

    /** {@inheritDoc} */
    protected void prepare (Widget widget) {
        FlexTable grid = getGrid();

        if (grid instanceof EditableGrid) {
            int column = getColumn();

            if (getWidget() != null)
                remove(getWidget());

            add(widget);

            ((EditableGrid)grid).setHeaderWidget(column, this);
        } else {
            super.prepare(widget);
        }
    }

    /** {@inheritDoc} */
    public void setFocus(boolean focus) {
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getValue();
    }

    /** {@inheritDoc} */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /** {@inheritDoc} */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    /** {@inheritDoc} */
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    /** {@inheritDoc} */
    public boolean isAscending() {
        return ascending;
    }

    /** {@inheritDoc} */
    public boolean isSortable() {
        return sortable;
    }

    /** {@inheritDoc} */
    public boolean isSorted() {
        return sorted;
    }

    /** {@inheritDoc} */
    public void sort() {
        if (!isSortable())
            return;
        
        FlexTable table = getGrid();
        if (table instanceof EditableGrid)
            ((EditableGrid)table).fireSort(this);

        getImage();
    }

    /**
     * Getter for property 'dockPanel'.
     *
     * @return Value for property 'dockPanel'.
     */
    protected DockPanel getDockPanel() {
        if (panel == null)
            panel = new DockPanel();

        return panel;
    }

    /**
     * Getter for property 'image'.
     *
     * @return Value for property 'image'.
     */
    protected Image getImage() {
        if (image == null) {
            image = new Image();
        }

        if (isSorted()) {
            if (isAscending())
                DOM.setElementAttribute(image.getElement(), "src", ThemeHelper.getInstance().getFullImageName("bullet-up.gif"));
            else
                DOM.setElementAttribute(image.getElement(), "src", ThemeHelper.getInstance().getFullImageName("bullet-down.gif"));
        } else
            DOM.setElementAttribute(image.getElement(), "src", SINGLE_IMAGE);

        return image;
    }

    /**
     * Getter for property 'initialized'.
     *
     * @return Value for property 'initialized'.
     */
    protected boolean isInitialized() {
        return initialized;
    }
}


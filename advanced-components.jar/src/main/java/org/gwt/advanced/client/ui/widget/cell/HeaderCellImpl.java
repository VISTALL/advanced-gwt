package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.AdvancedFlexTable;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is a header cell implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class HeaderCellImpl extends AbstractCell implements HeaderCell {
    /** one pixel image name */
    public static final String SINGLE_IMAGE = "advanced/images/single.gif";

    /** this is a dock panel for the cell elemnts */
    private FlexTable panel;
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
        FlexTable panel = getDockPanel();

        Label label = getLabel();
        Image image = getImage();

        String header = String.valueOf(getValue());
        label.setText(header.length() == 0 ? " " : header);

        if (panel.getRowCount() < 1) {
            panel.setWidget(0, 0, label);
            panel.getFlexCellFormatter().setWidth(0, 0, "100%");
            panel.getFlexCellFormatter().setVerticalAlignment(0, 0, DockPanel.ALIGN_MIDDLE);
        }

        if (isSortable() && panel.getCellCount(0) < 2)
            panel.setWidget(0, 1, image);

        return panel;
    }

    /** {@inheritDoc} */
    protected void addListeners(Widget widget) {
        if (isSortable())
            DOM.setEventListener(getLabel().getElement(), new SortListener());

        Element thead = ((AdvancedFlexTable) getGrid()).getTHeadElement();
        DOM.setEventListener(thead, new ResizeListener());
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
    protected FlexTable getDockPanel() {
        if (panel == null)
            panel = new FlexTable();

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

    /**
     * This listener is invoked on sort event.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class SortListener implements EventListener {
        /**
         * Starts column sorting.
         *
         * @param event an event.
         */
        public void onBrowserEvent(Event event) {
            if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
                sort();
            }
            DOM.sinkEvents(((AdvancedFlexTable)getGrid()).getTHeadElement(), Event.MOUSEEVENTS);
        }
    }

    /**
     * This listener is invoked on different column resizing events.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class ResizeListener implements EventListener {
        /** currently selected TH element */
        private Element th;
        /** start mouse position */
        private int startX;

        /**
         * This method handles all mouse events related to resizing.
         *
         * @param event is an event.
         */
        public void onBrowserEvent(Event event) {
            EditableGrid grid = (EditableGrid) getGrid();

            if (grid.isColumnResizingAllowed()) {
                if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
                    startResizing(event);
                } else if (DOM.eventGetType(event) == Event.ONMOUSEUP && th != null) {
                    stopResizing(event);
                } else if (DOM.eventGetType(event) == Event.ONMOUSEOUT && th != null) {
                    interruptResizing(event);
                }
            }
            if (DOM.eventGetType(event) == Event.ONMOUSEMOVE) {
                setCursor(event);
            }
        }

        /**
         * Sets a cursor style.
         *
         * @param event is an event.
         */
        protected void setCursor(Event event) {
            EditableGrid grid = (EditableGrid) getGrid();
            Element th = getTh(event);
            if (this.th != null || grid.isColumnResizingAllowed() && isOverBorder(event, th))
                DOM.setStyleAttribute(DOM.eventGetTarget(event), "cursor", "e-resize");
            else if (isSortable())
                DOM.setStyleAttribute(DOM.eventGetTarget(event), "cursor", "pointer");
            else
                DOM.setStyleAttribute(DOM.eventGetTarget(event), "cursor", "default");
        }

        /**
         * This method interrupts resiszing.
         *
         * @param event is an event.
         */
        protected void interruptResizing(Event event) {
            EditableGrid grid = (EditableGrid) getGrid();
            int positionX = getPositionX(event);
            int positionY = getPositionY(event);
            Element thead = grid.getTHeadElement();
            int left = DOM.getAbsoluteLeft(thead);
            int top = DOM.getAbsoluteTop(thead);
            int width = getElementWidth(thead);
            int height = getElementHeight(thead);

            if (positionX < left || positionX > left + width || positionY < top || positionY > top + height)
                th = null;
        }

        /**
         * This method normally stops resisng and changes column width.
         *
         * @param event is an event.
         */
        protected void stopResizing(Event event) {
            int position = getPositionX(event);
            int delta = position - startX;
            Element tr = DOM.getParent(th);
            int left = DOM.getAbsoluteLeft(th);
            int width = getElementWidth(th);

            Element sibling = null;
            int sign = 0;
            if (startX <= left + 1) {
                sign = 1;
                sibling = DOM.getChild(tr, DOM.getChildIndex(tr, th) - 1);
            } else if (startX >= left + width - 1) {
                sign = -1;
                sibling = DOM.getChild(tr, DOM.getChildIndex(tr, th) + 1);
            }

            if (sibling != null) {
                DOM.setStyleAttribute(th, "width", (width - sign * delta) + "px");
                DOM.setStyleAttribute(sibling, "width", (getElementWidth(sibling) + sign * delta) + "px");
            }
            th = null;
        }

        /**
         * This method starts column resizing.
         *
         * @param event is an event.
         */
        protected void startResizing(Event event) {
            if ("e-resize".equalsIgnoreCase(DOM.getStyleAttribute(DOM.eventGetTarget(event), "cursor"))) {
                th = getTh(event);
                startX = getPositionX(event);
                if (!isOverBorder(event, th))
                    th = null;
            }
        }

        /**
         * Gets element width.
         *
         * @param element is an element
         * @return width in pixels
         */
        protected int getElementWidth(Element element) {
            return DOM.getElementPropertyInt(element, "offsetWidth");
        }

        /**
         * Gets element height.
         *
         * @param element is an element
         * @return height in pizels.
         */
        protected int getElementHeight(Element element) {
            return DOM.getElementPropertyInt(element, "offsetHeight");
        }

        /**
         * Gets mouse X position.
         *
         * @param event is an event.
         * @return X position in pixels.
         */
        protected int getPositionY(Event event) {
            return DOM.eventGetClientY(event);
        }

        /**
         * Gets mouse Y position.
         *
         * @param event is an event.
         * @return Y position in pixels.
         */
        protected int getPositionX(Event event) {
            return DOM.eventGetClientX(event);
        }

        /**
         * This method looks for the TH element starting from the element which produced the event.
         *
         * @param event is an event.
         * @return a TH element or <code>null</code> if there is no such element.
         */
        protected Element getTh(Event event) {
            Element element = DOM.eventGetTarget(event);
            while (element != null && !DOM.getElementProperty(element, "tagName").equalsIgnoreCase("th"))
                element = DOM.getParent(element);
            return element;
        }

        /**
         * This method detects whether the cursor is over the border between columns.
         *
         * @param event is an event.
         * @param th is TH element.
         * @return a result of check.
         */
        protected boolean isOverBorder(Event event, Element th) {
            int position = getPositionX(event);
            int left = DOM.getAbsoluteLeft(th);
            int width = getElementWidth(th);
            int index = DOM.getChildIndex(DOM.getParent(th), th);

            return position <= left + 1 && index > 0
                   || position >= left + width - 1 && index < DOM.getChildCount(DOM.getParent(th)) - 1;
        }
    }
}


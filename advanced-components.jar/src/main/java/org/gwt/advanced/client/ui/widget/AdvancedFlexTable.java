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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.ui.SourcesTableDoubleClickEvents;
import org.gwt.advanced.client.ui.TableDoubleClickListener;
import org.gwt.advanced.client.ui.TableDoubleClickListenerCollection;
import org.gwt.advanced.client.util.GWTUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is an advanced flex table.<p/>
 * The difference with the original GWT flex table is that this component allows using &lt;thead&gt;
 * and &lt;th&gt; HTML elements. It also allows enable vetical scrolling with fixed headers.<br/>
 * For horizontal scrolling use traditional tools like <code>ScrollPanel</code>.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class AdvancedFlexTable extends FlexTable implements SourcesTableDoubleClickEvents {
    /**
     * maximal timeout between two clicks in double click
     */
    private static final int CLICK_TIMEOUT = 300;
    /**
     * the thead element
     */
    private Element tHeadElement;
    /**
     * the tfoot element
     */
    private Element tFootElement;
    /**
     * header widgets list
     */
    private List<Widget> headerWidgets;
    /**
     * footer widgets list
     */
    private List<Widget> footerWidgets;
    /**
     * a scroll panel widget (supported by IE only)
     */
    private Panel scrollPanel;
    /**
     * a scrollable flag value
     */
    private boolean scrollable;
    /**
     * list of table click handlers
     */
    @SuppressWarnings({"deprecation"})
    private HandlerManager handlerManager = new HandlerManager(this);
    /**
     * list of double click listeners registered in this widget
     */
    private TableDoubleClickListenerCollection doubleClikcListeners = new TableDoubleClickListenerCollection();
    /**
     * the timer that is activated if the second click hasn't been done
     */
    private Timer clickTimer = new ClickTimer();
    /**
     * count of clicks
     */
    private int clickCount;
    /**
     * latest cell clicked
     */
    private Element cell;
    /**
     * flag meaning that ONCLICK event is already sank
     */
    private boolean clickEnabled;
    /**
     * enables double clicks, must be switched to <code>false</code> if there are no double click listeners
     */
    private boolean doubleClickEnabled;

    /**
     * Creates an instance of this class.
     */
    public AdvancedFlexTable() {
    }

    /**
     * Creates an instance of this class and initializes the THEAD element if the flag is <code>true</code>.<p/>
     * Otheriwse initialization happens only if the first header widget is added.
     *
     * @param initializeThead is an initilization flag.
     */
    public AdvancedFlexTable(boolean initializeThead) {
        if (initializeThead) {
            tHeadElement = DOM.createTHead();
            tHeadElement = DOM.createElement("thead");
            DOM.insertChild(getElement(), getTHeadElement(), 0);
            Element tr = DOM.createTR();
            DOM.insertChild(getTHeadElement(), tr, 0);
        }
    }

    /**
     * This method sets a widget for the specified header cell.
     *
     * @param column is a column number.
     * @param widget is a widget to be added to the cell.
     */
    public void setHeaderWidget(int column, Widget widget) {
        prepareHeaderCell(column);

        if (widget != null) {
            widget.removeFromParent();

            Element th = DOM.getChild(DOM.getFirstChild(getTHeadElement()), column);
            internalClearCell(th, true);

            // Physical attach.
            DOM.appendChild(th, widget.getElement());

            List<Widget> headerWidgets = getHeaderWidgets();
            if (headerWidgets.size() > column && headerWidgets.get(column) != null)
                headerWidgets.set(column, widget);
            else
                headerWidgets.add(column, widget);

            adopt(widget);
        }
    }

    /**
     * This method removes the header widget.
     *
     * @param column is a column number.
     */
    public void removeHeaderWidget(int column) {
        if (column < 0)
            throw new IndexOutOfBoundsException("Column number mustn't be negative");

        Element tr = DOM.getFirstChild(getTHeadElement());
        Element th = DOM.getChild(tr, column);
        DOM.removeChild(tr, th);

        getHeaderWidgets().remove(column);
    }

    /**
     * This method sets a widget for the specified footer cell.
     *
     * @param column is a column number.
     * @param widget is a widget to be added to the cell.
     */
    public void setFooterWidget(int column, Widget widget) {
        prepareFooterCell(column);

        if (widget != null) {
            widget.removeFromParent();

            Element td = DOM.getChild(DOM.getFirstChild(getTFootElement()), column);
            internalClearCell(td, true);

            // Physical attach.
            DOM.appendChild(td, widget.getElement());

            List<Widget> footerWidgets = getFooterWidgets();
            if (footerWidgets.size() > column && footerWidgets.get(column) != null)
                footerWidgets.set(column, widget);
            else
                footerWidgets.add(column, widget);

            adopt(widget);
        }
    }

    /**
     * This method removes the header widget.
     *
     * @param column is a column number.
     */
    public void removeFooterWidget(int column) {
        if (column < 0)
            throw new IndexOutOfBoundsException("Column number mustn't be negative");

        Element tr = DOM.getFirstChild(getTFootElement());
        Element th = DOM.getChild(tr, column);
        DOM.removeChild(tr, th);

        getFooterWidgets().remove(column);
    }

    /**
     * This method enables vertical scrolling ability<p/>
     * Note that in different browsers this feature can work in absolutely different ways.
     * Remember this fact every time when you make CSS for your site.
     *
     * @param enabled if <code>true</code> then the scrolling feature should be enabled,
     */
    public void enableVerticalScrolling(boolean enabled) {
        prepareScrolling(enabled);
        setScrollable(enabled);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Widget> iterator() {
        List<Widget> notAttachedWidgets = new ArrayList<Widget>();
        for (Widget widget : getHeaderWidgets()) {
            if (!widget.isAttached())
                notAttachedWidgets.add(widget);
        }
        return new AdvancedWidgetIterator(super.iterator(), notAttachedWidgets.iterator());
    }

    /**
     * Inserts a header cell element.
     *
     * @param column is a column number that the element will have.
     */
    public void insertHeaderCell(int column) {
        Element tr;
        if (tHeadElement == null) {
            tHeadElement = DOM.createElement("thead");
            DOM.insertChild(getElement(), getTHeadElement(), 0);
            tr = DOM.createTR();
            DOM.insertChild(getTHeadElement(), tr, 0);
        } else {
            tr = DOM.getChild(tHeadElement, 0);
        }

        Element th = DOM.createTH();
        DOM.insertBefore(tr, th, DOM.getChild(tr, column));
    }

    public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONCLICK) {
            setCellClicked(DOM.eventGetTarget(event));
            if (getClickCount() % 2 == 0 && doubleClickEnabled) {
                setClickCount(getClickCount() + 1);
                getClickTimer().schedule(CLICK_TIMEOUT);
            } else if (getClickCount() % 2 == 0 && !doubleClickEnabled) {
                fireClickEvent();
            } else if (doubleClickEnabled) {
                setClickCount(0);
                fireDoubleClickEvent();
                getClickTimer().cancel();
            }
        }
        super.onBrowserEvent(event);
    }

    /**
     * Adds a table handler and sinks the ONCLICK event if it's not sank.
     *
     * @param handler is a handler to add.
     */
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        HandlerRegistration registration = getHandlerManager().addHandler(ClickEvent.getType(), handler);
        if (!clickEnabled) {
            DOM.sinkEvents(getElement(), Event.ONCLICK);
            clickEnabled = true;
        }
        return registration;
    }

    /**
     * Adds a double click listener and sinks the ONCLICK event if it's not sank.
     *
     * @param listener is a listener to register.
     */
    public void addDoubleClickListener(TableDoubleClickListener listener) {
        removeDoubleClickListener(listener);
        getDoubleClickListeners().add(listener);
        if (!clickEnabled) {
            DOM.sinkEvents(getElement(), Event.ONCLICK);
            clickEnabled = true;
        }
        doubleClickEnabled = true;
    }

    /**
     * Removes the double click listener.
     *
     * @param listener is a listener to remove.
     */
    public void removeDoubleClickListener(TableDoubleClickListener listener) {
        getDoubleClickListeners().remove(listener);
        if (getDoubleClickListeners().isEmpty()) {
            doubleClickEnabled = false;
        }
    }

    /**
     * Fires click events.
     */
    protected void fireClickEvent() {
        getHandlerManager().fireEvent(new ClickEvent(){});
        setCellClicked(null);
    }

    @Override
    public Cell getCellForEvent(ClickEvent event) {
        Element td = getCellElement(getCellClicked());
        if (td == null)
            return null;

        Element tr = DOM.getParent(td);
        Element tbody = DOM.getParent(tr);
        return new Cell(DOM.getChildIndex(tbody, tr), DOM.getChildIndex(tr, td)){}; 
    }

    /**
     * Fires double click events.
     */
    protected void fireDoubleClickEvent() {
        Element td = getCellElement(getCellClicked());
        if (td == null)
            return;

        Element tr = DOM.getParent(td);
        Element tbody = DOM.getParent(tr);

        getDoubleClickListeners().fireCellDoubleClicked(this, DOM.getChildIndex(tbody, tr), DOM.getChildIndex(tr, td));
        setCellClicked(null);
    }

    /**
     * Searches for the td element strting from the clicked element to upper levels of the DOM tree.
     *
     * @param clickElement is an element that is clicked.
     * @return a found element or <code>null</code> if the clicked element is not the td tag and not nested
     *         into any td.
     */
    protected Element getCellElement(Element clickElement) {
        while (clickElement != null && !"td".equalsIgnoreCase(clickElement.getTagName()))
            clickElement = DOM.getParent(clickElement);

        if (clickElement == null)
            return null;

        Element tr = DOM.getParent(clickElement);
        Element tbody = DOM.getParent(tr);
        Element table = DOM.getParent(tbody);

        if (getElement().equals(table))
            return clickElement;
        else
            return getCellElement(table);
    }

    @SuppressWarnings({"deprecation"})
    protected HandlerManager getHandlerManager() {
        return handlerManager;
    }

    @SuppressWarnings({"deprecation"})
    protected void setHandlerManager(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    protected TableDoubleClickListenerCollection getDoubleClickListeners() {
        return doubleClikcListeners;
    }

    protected void setDoubleClikcListeners(TableDoubleClickListenerCollection doubleClikcListeners) {
        this.doubleClikcListeners = doubleClikcListeners;
    }

    protected Timer getClickTimer() {
        return clickTimer;
    }

    protected void setClickTimer(Timer clickTimer) {
        this.clickTimer = clickTimer;
    }

    protected int getClickCount() {
        return clickCount;
    }

    protected void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    protected Element getCellClicked() {
        return cell;
    }

    protected void setCellClicked(Element cell) {
        this.cell = cell;
    }

    /**
     * Prepares the flex table for scrolling.<p/>
     * Currently this method supports IE6+ and Firefox 2.x
     *
     * @param enabled if <code>true</code> then scrolling must be enabled.
     */
    protected void prepareScrolling(boolean enabled) {
        if (GWTUtil.isIE()) {
            Panel scrollPanel = getScrollPanel();

            if (enabled && !isScrollable()) {
                Element parent = DOM.getParent(getElement());
                String height = String.valueOf(getTableHeight());
                String width = String.valueOf(getTableWidth());

                if (parent != null) {
                    DOM.removeChild(parent, getElement());
                    DOM.appendChild(parent, scrollPanel.getElement());
                }
                DOM.appendChild(scrollPanel.getElement(), getElement());

                scrollPanel.setHeight(height);
                scrollPanel.setWidth(width);
            } else if (!enabled && isScrollable()) {
                Element parent = DOM.getParent(scrollPanel.getElement());
                DOM.removeChild(scrollPanel.getElement(), getElement());

                if (parent != null) {
                    DOM.removeChild(parent, scrollPanel.getElement());
                    DOM.appendChild(parent, getElement());
                }
            }
        } else {
            int tableHeight = getTableHeight();

            int bodyHeight;
            if (getTHeadElement() != null) {
                bodyHeight = tableHeight - (DOM.getAbsoluteTop(getBodyElement()) - DOM.getAbsoluteTop(getTHeadElement()));
            } else
                bodyHeight = tableHeight;

            if (enabled) {
                DOM.setStyleAttribute(getBodyElement(), "height", String.valueOf(bodyHeight));
                DOM.setStyleAttribute(getBodyElement(), "overflowY", "auto");
                DOM.setStyleAttribute(getBodyElement(), "overflowX", "hidden");
            } else {
                DOM.setStyleAttribute(getBodyElement(), "overflowY", "visible");
                DOM.setStyleAttribute(getBodyElement(), "overflowX", "visible");
            }
        }
    }

    /**
     * This method returns an actual table height.<p/>
     * If the value is not specified in the element styles, it returns the offset height.
     *
     * @return an actual table height.
     */
    protected int getTableHeight() {
        String height = DOM.getStyleAttribute(getElement(), "height");
        if (height != null && height.endsWith("px")) {
            return Integer.parseInt(height.substring(0, height.indexOf("px")));
        } else {
            return getOffsetHeight();
        }
    }

    /**
     * This method returns an actual table width.<p/>
     * If the value is not specified in the element styles, it returns the offset width.
     *
     * @return an actual table width.
     */
    protected int getTableWidth() {
        String width = DOM.getStyleAttribute(getElement(), "width");
        if (width != null && width.endsWith("px")) {
            return Integer.parseInt(width.substring(0, width.indexOf("px"))) + 20;
        } else {
            return getOffsetWidth() + 20;
        }
    }

    /**
     * This method prepares the header cell to be used.
     *
     * @param column is a column number.
     */
    protected void prepareHeaderCell(int column) {
        if (column < 0) {
            throw new IndexOutOfBoundsException(
                    "Cannot create a column with a negative index: " + column
            );
        }

        if (tHeadElement == null) {
            tHeadElement = DOM.createElement("thead");
            DOM.insertChild(getElement(), getTHeadElement(), 0);
            Element tr = DOM.createTR();
            DOM.insertChild(getTHeadElement(), tr, 0);
        }

        List<Widget> headerWidgets = getHeaderWidgets();
        if (headerWidgets.size() <= column || headerWidgets.get(column) == null) {
            int required = column + 1 - DOM.getChildCount(DOM.getChild(getTHeadElement(), 0));
            if (required > 0)
                addHeaderCells(getTHeadElement(), required);
        }
    }

    /**
     * This method prepares the footer cell to be used.
     *
     * @param column is a column number.
     */
    protected void prepareFooterCell(int column) {
        if (column < 0) {
            throw new IndexOutOfBoundsException(
                    "Cannot create a column with a negative index: " + column
            );
        }

        if (tFootElement == null) {
            tFootElement = DOM.createElement("tfoot");
            DOM.insertChild(getElement(), getTFootElement(), 1);
            Element tr = DOM.createTR();
            DOM.insertChild(getTFootElement(), tr, 0);
        }

        List<Widget> footerWidgets = getFooterWidgets();
        if (footerWidgets.size() <= column || footerWidgets.get(column) == null) {
            int required = column + 1 - DOM.getChildCount(DOM.getChild(getTFootElement(), 0));
            if (required > 0)
                addFooterCells(getTFootElement(), required);
        }
    }

    /**
     * This native method is used to create TH tags instead of TD tags.
     *
     * @param tHead is a grid thead element.
     * @param num   is a number of columns to create.
     */
    protected native void addHeaderCells(Element tHead, int num)/*-{
        var rowElem = tHead.rows[0];
        for(var i = 0; i < num; i++){
          var cell = $doc.createElement("th");
          rowElem.appendChild(cell);
        }
    }-*/;

    /**
     * This native method is used to create TD tags.
     *
     * @param tFoot is a grid tfoot element.
     * @param num   is a number of columns to create.
     */
    protected native void addFooterCells(Element tFoot, int num)/*-{
        var rowElem = tFoot.rows[0];
        for(var i = 0; i < num; i++){
          var cell = $doc.createElement("td");
          rowElem.appendChild(cell);
        }
    }-*/;

    /**
     * Getter for property 'tHeadElement'.
     *
     * @return Value for property 'tHeadElement'.
     */
    public Element getTHeadElement() {
        return tHeadElement;
    }

    /**
     * Getter for property 'tFootElement'.
     *
     * @return Value for property 'tFootElement'.
     */
    public Element getTFootElement() {
        return tFootElement;
    }

    /**
     * Getter for property 'headerWidgets'.
     *
     * @return Value for property 'headerWidgets'.
     */
    protected List<Widget> getHeaderWidgets() {
        if (headerWidgets == null)
            headerWidgets = new ArrayList<Widget>();
        return headerWidgets;
    }

    /**
     * Getter for property 'footerWidgets'.
     *
     * @return Value for property 'footerWidgets'.
     */
    protected List<Widget> getFooterWidgets() {
        if (footerWidgets == null)
            footerWidgets = new ArrayList<Widget>();
        return footerWidgets;
    }

    /**
     * Getter for property 'scrollPanel'.
     *
     * @return Value for property 'scrollPanel'.
     */
    protected Panel getScrollPanel() {
        if (scrollPanel == null) {
            scrollPanel = new RowsScrollPanel();
            scrollPanel.setHeight(getOffsetHeight() + "px");
            ((ScrollPanel) scrollPanel).setAlwaysShowScrollBars(false);
        }
        return scrollPanel;
    }

    /**
     * Setter for property 'scrollable'.
     *
     * @param scrollable Value to set for property 'scrollable'.
     */
    protected void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    /**
     * Getter for property 'scrollable'.
     *
     * @return Value for property 'scrollable'.
     */
    protected boolean isScrollable() {
        return scrollable;
    }

    /**
     * Overrides this method to make it accessible for this package and server side renderers.
     *
     * @return a body element.
     */
    protected Element getBodyElement() {
        return super.getBodyElement();
    }

    /**
     * {@inheritDoc}
     */
    protected void prepareCell(int row, int column) {
        super.prepareCell(row, column);
    }

    /**
     * {@inheritDoc}
     */
    protected void prepareRow(int row) {
        super.prepareRow(row);
    }

    /**
     * {@inheritDoc}
     */
    protected void checkCellBounds(int row, int column) {
        super.checkCellBounds(row, column);
    }

    /**
     * {@inheritDoc}
     */
    protected void checkRowBounds(int row) {
        super.checkRowBounds(row);
    }

    /**
     * {@inheritDoc}
     */
    protected Element createCell() {
        return super.createCell();
    }

    /**
     * {@inheritDoc}
     */
    protected int getDOMCellCount(Element tableBody, int row) {
        return super.getDOMCellCount(tableBody, row);
    }

    /**
     * {@inheritDoc}
     */
    protected int getDOMCellCount(int row) {
        return super.getDOMCellCount(row);
    }

    /**
     * {@inheritDoc}
     */
    protected int getDOMRowCount() {
        return super.getDOMRowCount();
    }

    /**
     * {@inheritDoc}
     */
    protected int getDOMRowCount(Element elem) {
        return super.getDOMRowCount(elem);
    }

    /**
     * {@inheritDoc}
     */
    protected Element getEventTargetCell(Event event) {
        return super.getEventTargetCell(event);
    }

    /**
     * {@inheritDoc}
     */
    protected void insertCells(int row, int column, int count) {
        super.insertCells(row, column, count);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean internalClearCell(Element td, boolean clearInnerHTML) {
        return super.internalClearCell(td, clearInnerHTML);
    }

    /**
     * {@inheritDoc}
     */
    protected void prepareColumn(int column) {
        super.prepareColumn(column);
    }

    /**
     * {@inheritDoc}
     */
    protected void setCellFormatter(CellFormatter cellFormatter) {
        super.setCellFormatter(cellFormatter);
    }

    /**
     * {@inheritDoc}
     */
    protected void setColumnFormatter(ColumnFormatter formatter) {
        super.setColumnFormatter(formatter);
    }

    /**
     * {@inheritDoc}
     */
    protected void setRowFormatter(RowFormatter rowFormatter) {
        super.setRowFormatter(rowFormatter);
    }

    /**
     * This is a scroll panel extension designed especially for rows scrolling.
     */
    protected class RowsScrollPanel extends ScrollPanel {
        /**
         * Constructs a new RowsScrollPanel.
         */
        public RowsScrollPanel() {
            setStyleAttribute("position", "relative");
            new Timer() {
                public void run() {
                    if (getTHeadElement() == null)
                        return;

                    String top = DOM.getStyleAttribute(DOM.getChild(DOM.getChild(getTHeadElement(), 0), 0), "top");
                    if (!(getScrollPosition() + "px").equals(top))
                        setStyleAttribute("top", String.valueOf(getScrollPosition()));
                }
            }.scheduleRepeating(100); //this timer ensures that the header will always be on top
            //whereas events don't
        }

        /**
         * This method sets the specified style attribute to all the header rows.
         *
         * @param name  is a name of style attribute.
         * @param value is a value of style attribute.
         */
        protected void setStyleAttribute(String name, String value) {
            if (getTHeadElement() == null)
                return;

            int rowCount = DOM.getChildCount(getTHeadElement());
            for (int i = 0; i < rowCount; i++)
                DOM.setStyleAttribute(DOM.getChild(getTHeadElement(), i), name, value);
        }
    }

    /**
     * This is an implementation of widget iterator for the advanced table.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class AdvancedWidgetIterator implements Iterator<Widget> {
        /**
         * parent flex table iterator
         */
        private Iterator<Widget> parentIterator;
        /**
         * header widget collection iterator
         */
        private Iterator<Widget> headersIterator;
        /**
         * end of headers collection reached flag
         */
        private boolean endOfHeadersReached;

        /**
         * Creates a new instance of this class.
         *
         * @param parentIterator is a parent flex table iterator.
         * @param headerIterator is a header iterator.
         */
        public AdvancedWidgetIterator(Iterator<Widget> parentIterator, Iterator<Widget> headerIterator) {
            this.parentIterator = parentIterator;
            this.headersIterator = headerIterator;
        }

        /**
         * Returns <code>true</code> if a least one of nested iterators returns <code>true</code>.
         *
         * @return a value meaning that there is a least one widget exists.
         */
        public boolean hasNext() {
            return parentIterator.hasNext() || headersIterator.hasNext();
        }

        /**
         * Returns the next widget attached to the table.<p/>
         * Header widgets are followed by cell widgets.
         *
         * @return a next widget link.
         */
        public Widget next() {
            if (!headersIterator.hasNext()) {
                endOfHeadersReached = true;
                return parentIterator.next();
            } else
                return headersIterator.next();
        }

        /**
         * Removes a currently selected widget.
         */
        public void remove() {
            if (!endOfHeadersReached)
                headersIterator.remove();
            else
                parentIterator.remove();
        }
    }

    /**
     * This timer is invoked if the first click is received bu t the second one isn't till the
     * {@link AdvancedFlexTable#CLICK_TIMEOUT} exceded.<p/>
     * It drops clicks count and fires onclick event.
     */
    protected class ClickTimer extends Timer {
        /**
         * See class docs
         */
        public void run() {
            setClickCount(0);
            fireClickEvent();
        }
    }
}

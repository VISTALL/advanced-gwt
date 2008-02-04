package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.util.GWTUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an advanced flex table.<p/>
 * The difference with the original GWT flex table is that this component allows using &lt;thead&gt;
 * and &lt;th&gt; HTML elements. It also allows enable vetical scrolling with fixed headers.<br/>
 * For horizontal scrolling use traditional tools like <code>ScrollPanel</code>.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class AdvancedFlexTable extends FlexTable {
    /**
     * the thead element
     */
    private Element tHeadElement;
    /**
     * header widgets list
     */
    private List headerWidgets;
    /** a scroll panel widget (supported by IE only) */
    private Panel scrollPanel;
    /** a scrollable flag value */
    private boolean scrollable;


    /**
     * This method sets a widget for the specified header cell.
     *
     * @param column is a column number.
     * @param widget is a widget to be added to the cell.
     */
    public void setHeaderWidget (int column, Widget widget) {
        prepareHeaderCell(column);

        if (widget != null) {
            widget.removeFromParent();

            Element th = DOM.getChild(DOM.getFirstChild(getTHeadElement()), column);
            internalClearCell(th, true);

            // Physical attach.
            DOM.appendChild(th, widget.getElement());

            List headerWidgets = getHeaderWidgets();
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
    public void removeHeaderWidget (int column) {
        if (column < 0)
            throw new IndexOutOfBoundsException("Column number mustn't be negative");

        Element tr = DOM.getFirstChild(getTHeadElement());
        Element th = DOM.getChild(tr, column);
        DOM.removeChild(tr, th);

        getHeaderWidgets().remove(column);
    }

    /**
     * This method enables verticall scrolling ability<p/>
     * Note that in different browsers theis feature can work in absolutely diffirent ways.
     * Rememeber this fact every time when you make CSS for your site.
     *
     * @param enabled if <code>true</code> then the scrolling feature should be enabled,
     */
    public void enableVerticalScrolling(boolean enabled) {
        prepareScrolling(enabled);
        setScrollable(enabled);
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

                DOM.appendChild(parent, scrollPanel.getElement());
                DOM.removeChild(parent, getElement());
                DOM.appendChild(scrollPanel.getElement(), getElement());

                scrollPanel.setHeight(height);
            } else if (!enabled && isScrollable()) {
                Element parent = DOM.getParent(scrollPanel.getElement());
                DOM.removeChild(scrollPanel.getElement(), getElement());
                DOM.appendChild(parent, getElement());
                DOM.removeChild(parent, scrollPanel.getElement());
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
     * This method prepares the header cell to be used.
     *
     * @param column is a column number.
     */
    protected void prepareHeaderCell (int column) {
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

        List headerWidgets = getHeaderWidgets();
        if (headerWidgets.size() <= column || headerWidgets.get(column) == null) {
            int required = column + 1 - DOM.getChildCount(DOM.getChild(getTHeadElement(), 0));
            if (required > 0)
                addHeaderCells(getTHeadElement(), required);
        }
    }

    /**
     * This native method is used to create TH tags instead of TD tags.
     *
     * @param tHead is a grid thead element.
     * @param num   is a number of columns to create.
     */
    protected native void addHeaderCells (Element tHead, int num)/*-{
        var rowElem = tHead.rows[0];
        for(var i = 0; i < num; i++){
          var cell = $doc.createElement("th");
          rowElem.appendChild(cell);
        }
    }-*/;

    /**
     * Getter for property 'tHeadElement'.
     *
     * @return Value for property 'tHeadElement'.
     */
    protected Element getTHeadElement () {
        return tHeadElement;
    }

    /**
     * Getter for property 'headerWidgets'.
     *
     * @return Value for property 'headerWidgets'.
     */
    protected List getHeaderWidgets () {
        if (headerWidgets == null)
            headerWidgets = new ArrayList();
        return headerWidgets;
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
     * This is a scroll panel extension designed especially for rows scrolling.
     */
    protected class RowsScrollPanel extends ScrollPanel {
        /** Constructs a new RowsScrollPanel. */
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
         * @param name is a name of style attribute.
         * @param value is a value of style attribute.
         */
        protected void setStyleAttribute(String name, String value) {
            if (getTHeadElement() == null)
                return;

            int rowCount = DOM.getChildCount(getTHeadElement());
            for(int i = 0; i < rowCount; i++)
                DOM.setStyleAttribute(DOM.getChild(getTHeadElement(), i), name, value);
        }
    }
}

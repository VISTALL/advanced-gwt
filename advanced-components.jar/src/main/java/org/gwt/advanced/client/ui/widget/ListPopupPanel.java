/*
 * Copyright 2009 Sergey Skladchikov
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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.datamodel.ComboBoxDataModel;
import org.gwt.advanced.client.ui.AdvancedWidget;
import org.gwt.advanced.client.ui.widget.combo.ListItemFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This widget displays a scrollable list of items.<p/>
 * Don't try to use it directly. It's just for the combo box widget.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public class ListPopupPanel extends PopupPanel implements AdvancedWidget {
    /**
     * a list of items
     */
    private VerticalPanel list;
    /**
     * items scrolling widget
     */
    private ScrollPanel scrollPanel;
    /**
     * a flag meaning whether this widget is hidden
     */
    private boolean hidden = true;
    /**
     * a parent selection box
     */
    private ComboBox comboBox;
    /**
     * a list of change listeners
     */
    private List changeListeners;
    /**
     * item click  listener
     */
    private ClickListener itemClickListener;
    /**
     * mouse event listener
     */
    private MouseListener mouseEventsListener;
    /**
     * the row that is currently hightlight in the list but my be not selected in the model
     */
    private int highlightRow = -1;
    /**
     * number of visible rows in the scrollable area of the popup list. Limited by 30% of screen height by default
     */
    private int visibleRows = -1;
    /**
     * the top item index to be displayed in the visible area of the list
     */
    private int startItemIndex = 0;

    /**
     * Creates an instance of this class and sets the parent combo box value.
     *
     * @param selectionTextBox is a selection box value.
     */
    protected ListPopupPanel(ComboBox selectionTextBox) {
        super(true, false);
        this.comboBox = selectionTextBox;

        setStyleName("advanced-ListPopupPanel");

        prepareList();
        setWidget(getScrollPanel());

        getList().setWidth("100%");
        getList().setStyleName("list");
        
        addPopupListener(new AutoPopupListener());
    }

    /**
     * This method adds a listener that will be invoked on choice.
     *
     * @param listener is a listener to be added.
     */
    public void addChangeListener(ChangeListener listener) {
        removeChangeListener(listener);
        getChangeListeners().add(listener);
    }

    /**
     * This method removes the change listener.
     *
     * @param listener a change listener.
     */
    public void removeChangeListener(ChangeListener listener) {
        getChangeListeners().remove(listener);
    }

    /**
     * Getter for property 'hidden'.
     *
     * @return Value for property 'hidden'.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Gets a currently hightlight row.<p/>
     * Note that it may not be equl to the selected row index in the model.
     *
     * @return a row number that is currently highlight.
     */
    public int getHighlightRow() {
        return highlightRow;
    }

    /**
     * Sets the hight light row number.
     *
     * @param row is a row number to become highlight.
     */
    protected void setHightlightRow(int row) {
        if (row >= 0 && row < getList().getWidgetCount()) {
            Widget widget = null;
            if (this.highlightRow >= 0 && getList().getWidgetCount() > this.highlightRow)
                widget = getList().getWidget(this.highlightRow);

            if (widget != null)
                widget.removeStyleName("selected-row");
            this.highlightRow = row;
            widget = getList().getWidget(this.highlightRow);
            widget.addStyleName("selected-row");
        }
    }

    /**
     * Checks whether the specified item is visible in the scroll area.<p/>
     * The result is <code>true</code> if whole item is visible.
     *
     * @param index is an index of the item.
     * @return a result of check.
     */
    public boolean isItemVisible(int index) {
        Widget item = getList().getWidget(index);
        int itemTop = item.getAbsoluteTop();
        int top = getScrollPanel().getAbsoluteTop();
        return itemTop >= top && itemTop + item.getOffsetHeight() <= top + getScrollPanel().getOffsetHeight();
    }

    /**
     * Makes the item visible in the list according to the check done by the {@link #isItemVisible(int)} method.
     *
     * @param item is an item to check.
     */
    public void ensureVisible(Widget item) {
        if (!isItemVisible(getList().getWidgetIndex(item))) {
            getScrollPanel().ensureVisible(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void hide() {
        try {
            getComboBox().getDelegateListener().onLostFocus(this);
        } finally {
            super.hide();
            setHidden(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void show() {
        setHidden(false);
        super.show();

        setPopupPosition(getComboBox().getAbsoluteLeft(),
                getComboBox().getAbsoluteTop() + getComboBox().getOffsetHeight());

        adjustSize();
        
        setHightlightRow(getComboBox().getModel().getSelectedIndex());
        getComboBox().getDelegateListener().onFocus(this);
    }

    /**
     * Gets a number of visible rows.<p/>
     * Values <= 0 interpreted as undefined.
     *
     * @return a visible rows to be displayed without scrolling.
     */
    public int getVisibleRows() {
        return visibleRows;
    }

    /**
     * Sets visible rows number.<p/>
     * You can pass a value <= 0. It will mean that this parameter in undefined.
     *
     * @param visibleRows is a number of rows to be displayed without scrolling.
     */
    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
        if (isShowing())
            adjustSize();
    }

    /**
     * Sets an item index that must be displayed on top.<p/>
     * If the item is outside the currently visible area the list will be scrolled down
     * to this item.
     *
     * @param index is an index of the element to display.
     */
    public void setStartItemIndex(int index) {
        if (index < 0)
            index = 0;
        this.startItemIndex = index;
        if (isShowing())
            adjustSize();
    }

    public int getStartItemIndex() {
        return startItemIndex;
    }

    /**
     * Adjusts drop down list sizes to make it take optimal area on the screen.
     */
    protected void adjustSize() {
        ScrollPanel table = getScrollPanel();
        int visibleRows = getVisibleRows();
        
        if (visibleRows <= 0) {
            if (table.getOffsetHeight() > Window.getClientHeight() * 0.3) {
                table.setHeight((int) (Window.getClientHeight() * 0.3) + "px");
            }
            setHightlightRow(0);
        } else if (getComboBox().getModel().getCount() > visibleRows){
            int index = getStartItemIndex();
            int count = getList().getWidgetCount();

            if (index + visibleRows > count) {
                index = count - visibleRows + 1;
                if (index < 0)
                    index = 0;
            }

            int listHeight = 0;
            int scrollPosition = 0;
            for (int i = 0; i < index + visibleRows && i < count; i++) {
                int height = getList().getWidget(i).getOffsetHeight();
                if (i < index)
                    scrollPosition += height;
                else
                    listHeight += height;
            }
            table.setSize(table.getOffsetWidth() + "px", listHeight + "px");
            table.setScrollPosition(scrollPosition);
            setHightlightRow(index);
        }

        int absoluteBottom = getAbsoluteTop() + getOffsetHeight();
        if (absoluteBottom > Window.getClientHeight()
            && getOffsetHeight() <= getComboBox().getAbsoluteTop()) {
            setPopupPosition(getAbsoluteLeft(), getComboBox().getAbsoluteTop() - getOffsetHeight());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated you don't have to invoke this method to display the widget any more
     */
    public void display() {
    }

    /**
     * This method prepares the list of items for displaying.
     */
    protected void prepareList() {
        VerticalPanel panel = getList();
        panel.clear();

        ComboBoxDataModel model = getComboBox().getModel();
        ListItemFactory itemFactory = getComboBox().getListItemFactory();

        for (int i = 0; i < model.getCount(); i++)
            panel.add(adoptItemWidget(itemFactory.createWidget(model.get(i))));

        selectRow(getComboBox().getModel().getSelectedIndex());
        getScrollPanel().setWidth(getComboBox().getOffsetWidth() + "px");
    }

    /**
     * This method higlights a selected row.
     *
     * @param newRow a row for selection.
     */
    protected void selectRow(int newRow) {
        ComboBoxDataModel model = getComboBox().getModel();
        model.setSelectedIndex(newRow);
    }

    /**
     * This method wraps the specified widget into the focus panel and adds necessary listeners.
     *
     * @param widget is an item widget to be wraped.
     * @return a focus panel adopted for displaying.
     */
    protected FocusPanel adoptItemWidget(Widget widget) {
        FocusPanel panel = new FocusPanel(widget);
        panel.setWidth("100%");
        widget.setWidth("100%");
        panel.addClickListener(getItemClickListener());
        panel.addMouseListener(getMouseEventsListener());
        panel.setStyleName("item");
        return panel;
    }

    /**
     * Setter for property 'hidden'.
     *
     * @param hidden Value to set for property 'hidden'.
     */
    protected void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Getter for property 'comboBox'.
     *
     * @return Value for property 'comboBox'.
     */
    protected ComboBox getComboBox() {
        return comboBox;
    }

    /**
     * Getter for property 'changeListeners'.
     *
     * @return Value for property 'changeListeners'.
     */
    protected List getChangeListeners() {
        if (changeListeners == null)
            changeListeners = new ArrayList();
        return changeListeners;
    }

    /**
     * Getter for property 'list'.
     *
     * @return Value for property 'list'.
     */
    protected VerticalPanel getList() {
        if (list == null)
            list = new VerticalPanel();
        return list;
    }

    /**
     * Getter for property 'scrollPanel'.
     *
     * @return Value for property 'scrollPanel'.
     */
    public ScrollPanel getScrollPanel() {
        if (scrollPanel == null) {
            scrollPanel = new ScrollPanel();
            scrollPanel.setAlwaysShowScrollBars(false);
            scrollPanel.setWidget(getList());
            DOM.setStyleAttribute(scrollPanel.getElement(), "overflowX", "hidden");

            scrollPanel.addScrollListener(new ScrollListener(){
                public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
                    getComboBox().setFocus(true);
                }
            });
        }
        return scrollPanel;
    }

    /**
     * Getter for property 'itemClickListener'.
     *
     * @return Value for property 'itemClickListener'.
     */
    public ClickListener getItemClickListener() {
        if (itemClickListener == null)
            itemClickListener = new ItemClickListener(this);
        return itemClickListener;
    }

    /**
     * Getter for property 'mouseEventsListener'.
     *
     * @return Value for property 'mouseEventsListener'.
     */
    public MouseListener getMouseEventsListener() {
        if (mouseEventsListener == null)
            mouseEventsListener = new ListMouseListener();
        return mouseEventsListener;
    }

    /**
     * This is a click listener required to dispatch click events.
     */
    protected class ItemClickListener implements ClickListener {
        /**
         * a list panel instance
         */
        private ListPopupPanel panel;

        /**
         * Creates an instance of this class and initializes internal fields.
         *
         * @param panel is a list panel.
         */
        public ItemClickListener(ListPopupPanel panel) {
            this.panel = panel;
        }

        /**
         * {@inheritDoc}
         */
        public void onClick(Widget sender) {
            selectRow(getList().getWidgetIndex(sender));

            for (Iterator iterator = getChangeListeners().iterator(); iterator.hasNext();) {
                ChangeListener changeListener = (ChangeListener) iterator.next();
                changeListener.onChange(getPanel());
            }
        }

        /**
         * Getter for property 'panel'.
         *
         * @return Value for property 'panel'.
         */
        public ListPopupPanel getPanel() {
            return panel;
        }
    }

    /**
     * This listener is required to handle mouse moving events over the list.
     */                                                                             
    protected class ListMouseListener extends MouseListenerAdapter {
        /**
         * {@inheritDoc}
         */
        public void onMouseEnter(Widget sender) {
            if (getComboBox().isKeyPressed())
                return;
            int index = getComboBox().getModel().getSelectedIndex();
            if (index >= 0)
                getList().getWidget(index).removeStyleName("selected-row");
            sender.addStyleName("selected-row");
            setHightlightRow(getList().getWidgetIndex(sender));
        }

        /**
         * {@inheritDoc}
         */
        public void onMouseLeave(Widget sender) {
            if (getComboBox().isKeyPressed())
                return;
            sender.removeStyleName("selected-row");
        }
    }

    /**
     * This is listener that sets the choice button in up state, if the panel has been closed automatically.
     */
    protected class AutoPopupListener implements PopupListener {
        /**
         * {@inheritDoc}
         */
        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            if (autoClosed) {
                hide();
                getComboBox().getChoiceButton().setDown(false);
            }
        }
    }
}

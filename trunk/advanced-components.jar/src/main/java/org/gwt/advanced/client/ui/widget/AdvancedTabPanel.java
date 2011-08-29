/*
 * Copyright 2011 Sergey Skladchikov
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

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.ui.Resizable;
import org.gwt.advanced.client.ui.widget.border.Border;
import org.gwt.advanced.client.ui.widget.border.BorderFactory;
import org.gwt.advanced.client.ui.widget.tab.ContentBorderFactory;
import org.gwt.advanced.client.ui.widget.tab.TabBorderFactory;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * This is ana dvanced tab panel implementation that allows placing tabs around the content.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.6
 */
public class AdvancedTabPanel extends SimplePanel implements HasSelectionHandlers<Integer>, HasBeforeSelectionHandlers<Integer>, Resizable {
    /** tabs position for displaying */
    private TabPosition position;
    /** index of the currently selected tab */
    private int selected = -1;
    /** tabs and contents list */
    private List<TabHolder> tabs = new ArrayList<TabHolder>();
    /** a factory for rendering tab borders */
    private BorderFactory tabBorderFactory;
    /** a factory for rendering the content border */
    private BorderFactory contentBorderFactory;
    /** tab panel layout */
    private FlexTable layout = new FlexTable();
    /** content border instance */
    private Border contentBorder;
    /** a widget of the tabs band */
    private Widget tabsWidget;
    /** tab states list */
    private List<TabState> tabStates = new ArrayList<TabState>();

    /** Creates an instance of this class and displays top tabs band. */
    public AdvancedTabPanel() {
        this(TabPosition.TOP);
    }

    /**
     * Creates an instance of this class and displays the tabs band in the specified position.
     *
     * @param position is a tabs position.
     */
    public AdvancedTabPanel(TabPosition position) {
        this(position, new TabBorderFactory(), new ContentBorderFactory());
    }

    /**
     * Creates an instance of this class and displays the panel using the specified border factories.
     *
     * @param position             is a position of tabs.
     * @param tabBorderFactory     is a factory for tabs border.
     * @param contentBorderFactory is a factory for the content border.
     */
    public AdvancedTabPanel(TabPosition position, BorderFactory tabBorderFactory, BorderFactory contentBorderFactory) {
        this.tabBorderFactory = tabBorderFactory;
        this.contentBorderFactory = contentBorderFactory;
        this.position = position;

        this.layout.setStyleName("layout");
        setStyleName("advanced-TabPanel");
        render();

        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                for (Object tabState1 : tabStates) {
                    TabState tabState = (TabState) tabState1;
                    if (event.getItem() == tabState.getIndex() && !tabState.isEnabled()) {
                        event.cancel();
                        return;
                    }
                }
            }
        });
    }

    /**
     * Do the same things like the
     * {@link #addTab(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget)}
     * method and included into the class for changing from GWT TabPanel to this one.
     *
     * @param content is a tab content.
     * @param tab     is a tab header.
     */
    public void add(Widget content, Widget tab) {
        addTab(tab, content);
    }

    /**
     * Do the same things like the
     * {@link #setSelected(int)} method and included into the class for changing from GWT TabPanel
     * to this one.
     *
     * @param index is a selected tab index.
     */
    public void selectTab(int index) {
        setSelected(index);
    }

    /**
     * Adds a new tab into the panel.
     *
     * @param tab     is a tab widget.
     * @param content is a content widget.
     */
    public void addTab(Widget tab, Widget content) {
        TabHolder holder = new TabHolder(tab, content);
        tabs.remove(holder); // to avoid duplicated tabs
        tabs.add(holder);
        if (getSelected() == -1)
            setSelected(0);

        renderTabs();

        TabState tabState = new TabState();
        tabState.setIndex(tabs.indexOf(holder));
        tabState.setTab(tab);
        tabState.setEnabled(true);
        tabStates.add(tabState);
    }

    /**
     * Removes the tab from the panel.
     *
     * @param tab is a tab widget to identify the tab.
     */
    public void removeTab(Widget tab) {
        int index = indexOf(tab);
        tabs.remove(new TabHolder(tab, null));

        if (getSelected() == index) {
            index = index + 1;
            if (index >= tabs.size())
                index = tabs.size() - 1;
            setSelected(index);
        }
        renderTabs();

        TabState selState = null;
        for (Object tabState1 : tabStates) {
            TabState tabState = (TabState) tabState1;
            if (tabState.getTab().equals(tab))
                selState = tabState;
        }

        if (selState != null) {
            int selIndex = selState.getIndex();
            tabStates.remove(selState);

            for (Object tabState1 : tabStates) {
                TabState tabState = (TabState) tabState1;
                if (tabState.getIndex() > selIndex)
                    tabState.setIndex(tabState.getIndex() - 1);
            }
        }
    }

    /**
     * Inserts the tab before the specified tab.
     *
     * @param tab     is a tab widget.
     * @param content is a tab widget.
     * @param before  is an index of existent tab.
     */
    public void insertTab(Widget tab, Widget content, int before) {
        if (tabs.size() >= before)
            throw new IllegalArgumentException("Insertion index can't be greater the number of existing tabs");

        TabHolder holder = new TabHolder(tab, content);
        tabs.remove(holder); // to avoid duplicated tabs
        tabs.add(before, holder);

        if (getSelected() == before)
            setSelected(before + 1);
        renderTabs();

        TabState tabState = new TabState();
        tabState.setIndex(tabs.indexOf(holder));
        tabState.setTab(tab);
        tabState.setEnabled(true);
        tabStates.add(tabState);
    }

    /**
     * Sets the specified tab selected.
     *
     * @param index is an index of the tab for selection.
     */
    public void setSelected(int index) {
        if (index != selected) {
            if (index >= tabs.size())
                return;

            BeforeSelectionEvent<Integer> event = BeforeSelectionEvent.fire(this, index);
            if (event.isCanceled())
                return;

            selected = index;
            renderTabs();

            SelectionEvent.fire(this, index);
            resize();
        }
    }

    /**
     * Sets the tab selected.
     *
     * @param tab is a widget of the tab to select.
     */
    public void setSelected(Widget tab) {
        setSelected(indexOf(tab));
    }

    /**
     * Gets an index of the selected tab.
     *
     * @return an index of the selected tab.
     */
    public int getSelected() {
        return selected;
    }

    /**
     * Gets the tab widget by index.
     *
     * @param index is an index of the tab.
     * @return a tab widget.
     */
    public Widget getTab(int index) {
        if (index < 0 || this.tabs.size() <= index)
            return null;
        TabHolder tabHolder = this.tabs.get(index);
        return tabHolder != null ? tabHolder.getTab() : null;
    }

    /**
     * Gets the content widget by the tab.
     *
     * @param tab is a tab widget.
     * @return a content widget.
     */
    public Widget getContent(Widget tab) {
        int index = indexOf(tab);
        if (index == -1)
            return null;
        TabHolder tabHolder = this.tabs.get(index);
        return tabHolder != null ? tabHolder.getContent() : null;
    }

    /**
     * Gets an index of the specified tab.<p/>
     * If there is no such widget, returns <code>-1</code>.
     *
     * @param tab is a widget of the tab.
     * @return an index of the tab.
     */
    public int indexOf(Widget tab) {
        return this.tabs.indexOf(new TabHolder(tab, null));
    }

    /**
     * Gets a total count of tabs.
     *
     * @return a total count.
     */
    public int count() {
        return this.tabs.size();
    }

    /**
     * Sets the specified tab enabled or disabled.
     *
     * @param tabIndex is a tab index.
     * @param enable   is a tab state value.
     */
    public void setTabEnabled(int tabIndex, boolean enable) {
        Widget w = getTab(tabIndex);
        if (enable) {
            w.removeStyleName("disabled-tab");
        } else {
            w.addStyleName("disabled-tab");
        }
        for (Object tabState1 : tabStates) {
            TabState tabState = (TabState) tabState1;
            if (tabState.getIndex() == tabIndex)
                tabState.setEnabled(enable);
        }
    }

    /**
     * Gets the tabs border factory.
     *
     * @return is the tabs border factory that is currently used.
     */
    public BorderFactory getTabBorderFactory() {
        return tabBorderFactory;
    }

    /**
     * Gets the content border factory.
     *
     * @return is the content border factory that is currently used.
     */
    public BorderFactory getContentBorderFactory() {
        return contentBorderFactory;
    }

    /**
     * Gets a content border that is already rendered.
     *
     * @return a content border.
     */
    public Border getContentBorder() {
        return contentBorder;
    }

    /**
     * Gets tabs position.
     *
     * @return tabs position.
     */
    public TabPosition getPosition() {
        return position;
    }

    /** Renders the widget. */
    protected void render() {
        remove(this.layout);
        setWidget(this.layout);

        if (getContentBorder() != null)
            this.layout.remove((Widget) getContentBorder());
        this.contentBorder = getContentBorderFactory().create();
        ((Widget) getContentBorder()).addStyleName("content-" + getPosition().getName());
        this.layout.setWidget(1, 1, (Widget) getContentBorder());

        resize();
    }

    /** This method renders tabs band widget and puts it in the current position. */
    protected void renderTabs() {
        if (this.tabsWidget != null)
            this.tabsWidget.removeFromParent();
        this.tabsWidget = getPosition().getRenderer().render(this);

        TabPosition.LayoutPosition layoutPosition = getPosition().getLayoutPosition();
        switch (layoutPosition) {
            case LEFT:
                this.layout.setWidget(1, 0, this.tabsWidget);
                break;
            case RIGHT:
                this.layout.setWidget(1, 2, this.tabsWidget);
                break;
            case BOTTOM:
                this.layout.setWidget(2, 1, this.tabsWidget);
                break;
            case TOP:
            default:
                this.layout.setWidget(0, 1, this.tabsWidget);
        }

        Element parent = DOM.getParent(((Widget) getContentBorder()).getElement());
        DOM.setStyleAttribute(parent, "height", "100%");
        DOM.setStyleAttribute(parent, "width", "100%");

        Widget content = getContent(getTab(getSelected()));
        if (getSelected() != -1 && (content != this.contentBorder.getWidget() || this.contentBorder.getWidget() == null)) {
            getContentBorder().setWidget(getContent(getTab(getSelected())));
        }
    }

    /** {@inheritDoc} */
    public void resize() {
        Widget content = getContent(getTab(getSelected()));
        if (content != null && content instanceof Resizable)
            ((Resizable) content).resize();
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> selectionHandler) {
        return addHandler(selectionHandler, SelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> beforeSelectionHandler) {
        return addHandler(beforeSelectionHandler, BeforeSelectionEvent.getType());
    }

    /**
     * The class implements a pair of widgets related to each other.<p/>
     * One of them is a tab widget and another is a content widget.<p/>
     * Tab widget is a key field.
     *
     * @author Sergey Skladchikov
     */
    protected static class TabHolder {
        /** tab widget */
        private Widget tab;
        /** content widget */
        private Widget content;

        /**
         * Creates an instance of this class and initializes internal fields.
         *
         * @param tab     is a tab widget (key).
         * @param content is a content widget (value).
         */
        public TabHolder(Widget tab, Widget content) {
            this.tab = tab;
            this.content = content;
        }

        /**
         * Gets a tab widget.
         *
         * @return a tab widget.
         */
        public Widget getTab() {
            return tab;
        }

        /**
         * Gets a content widget.
         *
         * @return a content widget.
         */
        public Widget getContent() {
            return content;
        }

        /** {@inheritDoc} */
        public boolean equals(Object o) {
            if (o instanceof TabHolder) {
                if (this == o) return true;
                TabHolder tabHolder = (TabHolder) o;
                return !(tab != null ? !tab.equals(tabHolder.tab) : tabHolder.tab != null);
            } else
                return false;
        }

        /** {@inheritDoc} */
        public int hashCode() {
            return tab != null ? tab.hashCode() : 0;
        }
    }

    /** Describes the tab state (enabled or disabled. */
    protected class TabState {
        /** tab index */
        int index;
        /** tab header */
        public Widget tab;
        /** enabled or disabled */
        public boolean enabled;

        public int getIndex() {
            return index;
        }

        public Widget getTab() {
            return tab;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setTab(Widget tab) {
            this.tab = tab;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

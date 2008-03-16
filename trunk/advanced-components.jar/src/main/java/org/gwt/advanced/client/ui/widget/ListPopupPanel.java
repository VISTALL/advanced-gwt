package org.gwt.advanced.client.ui.widget;

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
    /** a list of items */
    private VerticalPanel list;
    /** items scrolling widget */
    private ScrollPanel scrollPanel;
    /** a flag meaning whether this widget is hidden */
    private boolean hidden = true;
    /** a parent selection box */
    private ComboBox comboBox;
    /** a list of change listeners */
    private List changeListeners;
    /** item click  listener */
    private ClickListener itemClickListener;
    /** mouse event listener */
    private MouseListener mouseEventsListener;

    /**
     * Creates an instance of this class and sets the parent combo box value.
     *
     * @param selectionTextBox is a selection box value.
     */
    protected ListPopupPanel(ComboBox selectionTextBox) {
        super(true, false);
        this.comboBox = selectionTextBox;
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

    /** {@inheritDoc} */
    public void hide() {
        try {
            getComboBox().getDelegateListener().onLostFocus(this);
        } finally {
            super.hide();
            setHidden(true);
        }
    }

    /** {@inheritDoc} */
    public void show() {
        setHidden(false);
        super.show();
        getComboBox().getDelegateListener().onFocus(this);
    }
    
    /** {@inheritDoc} */
    public void display() {
        setStyleName("advanced-ListPopupPanel");
        
        if (!isHidden())
            hide();
                       
        prepareList();
        setWidget(getScrollPanel());
        
        show();
        setPopupPosition(getComboBox().getAbsoluteLeft(),
                getComboBox().getAbsoluteTop() + getComboBox().getOffsetHeight());

        ScrollPanel table = getScrollPanel();
        if (table.getOffsetHeight() > Window.getClientHeight() * 0.3) {
            table.setHeight((int)(Window.getClientHeight() * 0.3) + "px");
        } 
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
        panel.setWidth("100%");

        panel.setStyleName("list");
    }

    /**
     * This method higlights a selected row.
     *
     * @param newRow a row for selection.
     */
    protected void selectRow(int newRow) {
        ComboBoxDataModel model = getComboBox().getModel();
        VerticalPanel panel = getList();

        int row = model.getSelectedIndex();

        if (row >= 0 && row < panel.getWidgetCount())
            panel.getWidget(row).removeStyleName("selected-row");

        model.setSelectedIndex(newRow);
        newRow = model.getSelectedIndex();

        if (newRow >= 0 && newRow < panel.getWidgetCount())
            panel.getWidget(newRow).addStyleName("selected-row");
        
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
        if(scrollPanel == null) {
            scrollPanel = new ScrollPanel();
            scrollPanel.setAlwaysShowScrollBars(false);
            scrollPanel.setWidget(getList());
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
        /** a list panel instance */
        private ListPopupPanel panel;

        /**
         * Creates an instance of this class and initializes internal fields.
         *
         * @param panel is a list panel.
         */
        public ItemClickListener(ListPopupPanel panel) {
            this.panel = panel;
        }

        /** {@inheritDoc} */
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
        /** {@inheritDoc} */
        public void onMouseEnter(Widget sender) {
            int index = getComboBox().getModel().getSelectedIndex();
            if (index >= 0)
                getList().getWidget(index).removeStyleName("selected-row");
            sender.addStyleName("selected-row");
        }

        /** {@inheritDoc} */
        public void onMouseLeave(Widget sender) {
            sender.removeStyleName("selected-row");
        }
    }

    /**
     * This is listener that sets the choice button in up state, if the panel has been closed automatically.
     */
    protected class AutoPopupListener implements PopupListener {
        /** {@inheritDoc} */
        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            if (autoClosed)
                getComboBox().getChoiceButton().setDown(false);
        }
    }
}

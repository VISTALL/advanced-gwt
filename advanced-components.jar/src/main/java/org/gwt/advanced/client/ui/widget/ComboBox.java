package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.datamodel.ComboBoxDataModel;
import org.gwt.advanced.client.ui.widget.combo.DefaultListItemFactory;
import org.gwt.advanced.client.ui.widget.combo.ListItemFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a combo box widget implementation.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */                                                       
public class ComboBox extends TextButtonPanel
        implements SourcesFocusEvents, SourcesChangeEvents, SourcesKeyboardEvents, SourcesClickEvents {
    /** a combo box data model */
    private ComboBoxDataModel model;
    /** a list item factory */
    private ListItemFactory listItemFactory;
    /** a list popup panel */
    private ListPopupPanel listPanel;
    /** a combo box delegate listener */
    private DelegateListener delegateListener;

    /**
     * Setter for property 'model'.
     *
     * @param model Value to set for property 'model'.
     */
    public void setModel(ComboBoxDataModel model) {
        this.model = model;
    }

    /**
     * Setter for property 'listItemFactory'.
     *
     * @param listItemFactory Value to set for property 'listItemFactory'.
     */
    public void setListItemFactory(ListItemFactory listItemFactory) {
        this.listItemFactory = listItemFactory;
    }

    /**
     * This method adds a change listener that will be invoked on value change.
     *
     * @param listener is a listener to be added.
     */
    public void addChangeListener(ChangeListener listener) {
        removeChangeListener(listener);
        getDelegateListener().getChangeListeners().add(listener);
    }

    /**
     * This method removes the change listener.
     *
     * @param listener a change listener to be removed.
     */
    public void removeChangeListener(ChangeListener listener) {
        getDelegateListener().getChangeListeners().remove(listener);
    }

    /**
     * This method adds a click listener that will be invoked on widget click.
     *
     * @param listener is a listener to be added.
     */
    public void addClickListener(ClickListener listener) {
        removeClickListener(listener);
        getDelegateListener().getClickListeners().add(listener);
    }

    /**
     * This method removes the click listener.
     *
     * @param listener is a listener to be removed.
     */
    public void removeClickListener(ClickListener listener) {
        getDelegateListener().getClickListeners().remove(listener);
    }

    /**
     * This method adds a focus lister that will be invoked on focus capture.
     *
     * @param listener is a listener to be added.
     */
    public void addFocusListener(FocusListener listener) {
        removeFocusListener(listener);
        getDelegateListener().getFocusListeners().add(listener);
    }

    /**
     * This method removes the focus listener.
     *
     * @param listener is a focus listener to be removed.
     */
    public void removeFocusListener(FocusListener listener) {
        getDelegateListener().getFocusListeners().remove(listener);
    }

    /**
     * This method adds a keyboard listener that will be invoked on keyboard events.
     *
     * @param listener is a listener to be added.
     */
    public void addKeyboardListener(KeyboardListener listener) {
        removeKeyboardListener(listener);
        getDelegateListener().getKeyboardListeners().remove(listener);
    }

    /**
     * This method removes the keyboard listener.
     *
     * @param listener is a keyboard listener to be removed.
     */
    public void removeKeyboardListener(KeyboardListener listener) {
        getDelegateListener().getKeyboardListeners().remove(listener);
    }

    /**
     * Getter for property 'model'.
     *
     * @return Value for property 'model'.
     */
    public ComboBoxDataModel getModel() {
        if (model == null)
            model = new ComboBoxDataModel();
        return model;
    }

    /**
     * Getter for property 'listItemFactory'.
     *
     * @return Value for property 'listItemFactory'.
     */
    public ListItemFactory getListItemFactory() {
        if (listItemFactory == null)
            listItemFactory = new DefaultListItemFactory();
        return listItemFactory;
    }

    /**
     * This method sets focus on this widget.<p/>
     * But note that the combo box is not a focus event sourcer. It siply delegtes this functionality
     * to the text box.
     *
     * @param focus is a falg of focus.
     */
    public void setFocus(boolean focus) {
        if (isCustomTextAllowed())
            getSelectedValue().setFocus(focus);
        else
            getChoiceButton().setFocus(focus);
    }

    /**
     * This method check the list panel status.
     *
     * @return <code>true</code> if it's opened.
     */
    public boolean isListPanelOpened() {
        return !getListPanel().isHidden();
    }

    /**
     * This method returns a value currently displayed in the text box.
     *
     * @return a text value.
     */
    public String getText() {
        return getSelectedValue().getText();
    }

    /**
     * This method returns a selected item.
     *
     * @return is a selected item.
     */
    public Object getSelected() {
        return getModel().getSelected();
    }

    /**
     * This method returns a selected item index.
     *
     * @return is a selected item index.
     */
    public int getSelectedIndex() {
        return getModel().getSelectedIndex();
    }

    /**
     * This method returns a selected item ID.
     *
     * @return is a selected item ID.
     */
    public String getSelectedId() {
        return getModel().getSelectedId();
    }

    /**
     * This method sets the selected item ID.
     *
     * @param id is an item ID to select.
     */
    public void setSelectedId(String id) {
        getModel().setSelectedId(id);
    }

    /**
     * This method sets the selected item index.
     *
     * @param index a selected item index.
     */
    public void setSelectedIndex(int index) {
        getModel().setSelectedIndex(index);
    }

    /** {@inheritDoc} */
    protected String getDefaultImageName() {
        return "drop-down.gif";
    }

    /** {@inheritDoc} */
    protected void prepareSelectedValue() {
        super.prepareSelectedValue();
        getSelectedValue().setText(getListItemFactory().convert(getModel().getSelected()));
    }

    /** {@inheritDoc} */
    protected void addComponentListeners() {
        TextBox value = getSelectedValue();
        ToggleButton button = getChoiceButton();
        
        getListPanel().addChangeListener(getDelegateListener());
        value.removeChangeListener(getDelegateListener());
        value.addChangeListener(getDelegateListener());
        button.removeFocusListener(getDelegateListener());
        button.addFocusListener(getDelegateListener());
        value.removeFocusListener(getDelegateListener());
        value.addFocusListener(getDelegateListener());
        button.removeFocusListener(getDelegateListener());
        button.addFocusListener(getDelegateListener());
        value.removeClickListener(getDelegateListener());
        value.addClickListener(getDelegateListener());
        button.removeClickListener(getDelegateListener());
        button.addClickListener(getDelegateListener());
        value.removeKeyboardListener(getDelegateListener());
        value.addKeyboardListener(getDelegateListener());
    }

    /**
     * Getter for property 'listPanel'.
     *
     * @return Value for property 'listPanel'.
     */
    protected ListPopupPanel getListPanel() {
        if (listPanel == null)
            listPanel = new ListPopupPanel(this);
        return listPanel;
    }

    /**
     * Getter for property 'delegateListener'.
     *
     * @return Value for property 'delegateListener'.
     */
    protected DelegateListener getDelegateListener() {
        if (delegateListener == null)
            delegateListener = new DelegateListener(this);
        return delegateListener;
    }

    /**
     * Universal listener that delegates all events handling to custom listeners.
     */
    protected class DelegateListener implements FocusListener, ClickListener, ChangeListener, KeyboardListener {
        /** a list of focused controls */
        private Set focuses;
        /** a combo box widget */
        private Widget widget;
        /** a list of custom focus listeners */
        private FocusListenerCollection focusListeners;
        /** a list of custom click listeners */
        private ClickListenerCollection clickListeners;
        /** a list of custom change listeners */
        private ChangeListenerCollection changeListeners;
        /** s list of keyboard listeners */
        private KeyboardListenerCollection keyboardListeners;

        /**
         * Creates an instance of this class passing a widget that will be an event sender.
         *
         * @param widget is a widget to be used.
         */
        public DelegateListener(Widget widget) {
            this.widget = widget;
        }

        /** {@inheritDoc} */
        public void onFocus(Widget sender) {
            getFocuses().add(sender);

            TextBox value = getSelectedValue();
            if (sender == value) {
                if (!isCustomTextAllowed())
                    value.addStyleName("selected-row");
                else
                    value.setSelectionRange(0, value.getText().length());
            }

            if (focuses.size() == 1)
                getFocusListeners().fireFocus(widget);
        }

        /** {@inheritDoc} */
        public void onLostFocus(Widget sender) {
            getFocuses().remove(sender);

            TextBox value = getSelectedValue();
            if (sender == value && !isCustomTextAllowed())
                    value.removeStyleName("selected-row");
            
            if (!isFocus())
                getFocusListeners().fireLostFocus(widget);
        }

        /** {@inheritDoc} */
        public void onClick(Widget sender) {
            int count = getModel().getCount();
            if (sender instanceof ToggleButton || !isCustomTextAllowed()) {
                if (count > 0)
                    getListPanel().display();
                else
                    getChoiceButton().setDown(false);
            }
            getClickListeners().fireClick(widget);
        }

        /** {@inheritDoc} */
        public void onChange(Widget sender) {
            if (sender == getListPanel()) {
                getSelectedValue().setText(getListItemFactory().convert(getModel().getSelected()));
                getListPanel().hide();
                getSelectedValue().removeStyleName("selected-row");
                getChoiceButton().setDown(false);
            }
            getChangeListeners().fireChange(widget);
        }

        /** {@inheritDoc} */
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
            getKeyboardListeners().fireKeyDown(widget, keyCode, modifiers);
        }

        /** {@inheritDoc} */
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
            getKeyboardListeners().fireKeyPress(widget, keyCode, modifiers);
        }

        /** {@inheritDoc} */
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
            getKeyboardListeners().fireKeyUp(widget, keyCode, modifiers);
        }
        
        /**
         * Getter for property 'focusListeners'.
         *
         * @return Value for property 'focusListeners'.
         */
        protected FocusListenerCollection getFocusListeners() {
            if (focusListeners == null)
                focusListeners = new FocusListenerCollection();
            return focusListeners;
        }

        /**
         * Getter for property 'clickListeners'.
         *
         * @return Value for property 'clickListeners'.
         */
        protected ClickListenerCollection getClickListeners() {
            if (clickListeners == null)
                clickListeners = new ClickListenerCollection();
            return clickListeners;
        }

        /**
         * Getter for property 'changeListeners'.
         *
         * @return Value for property 'changeListeners'.
         */
        protected ChangeListenerCollection getChangeListeners() {
            if (changeListeners == null)
                changeListeners = new ChangeListenerCollection();
            return changeListeners;
        }

        /**
         * Getter for property 'keyboardListeners'.
         *
         * @return Value for property 'keyboardListeners'.
         */
        protected KeyboardListenerCollection getKeyboardListeners() {
            if (keyboardListeners == null)
                keyboardListeners = new KeyboardListenerCollection();
            return keyboardListeners;
        }

        /**
         * Getter for property 'focuses'.
         *
         * @return Value for property 'focuses'.
         */
        protected Set getFocuses() {
            if (focuses == null)
                focuses = new HashSet();
            return focuses;
        }

        /**
         * Getter for property 'focus'.
         *
         * @return Value for property 'focus'.
         */
        protected boolean isFocus() {
            return getFocuses().size() > 0;
        }
    }
}

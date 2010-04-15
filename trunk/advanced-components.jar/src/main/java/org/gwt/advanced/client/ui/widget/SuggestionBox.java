/*
 * Copyright 2010 Sergey Skladchikov
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

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.SuggestionBoxListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements suggestion box logic.<p/>
 * To use it properly you should specify the data model that is instance of
 * the {@link org.gwt.advanced.client.datamodel.SuggestionBoxDataModel} class. Otherwise it will work as
 * a simple combo box.<br/>
 * See also {@link #setModel(org.gwt.advanced.client.datamodel.SuggestionBoxDataModel)}.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public class SuggestionBox extends ComboBox<SuggestionBoxDataModel> {
    /**
     * default request timeout between last expression change and getting data
     */
    public static final int DEFAULT_REQUEST_TIMEOUT = 500;
    /**
     * suggestion expression length
     */
    private int expressionLength;
    /**
     * a list of suggestion box listeners
     */
    private List<SuggestionBoxListener> suggestionBoxListeners;
    /**
     * a timer that is activated when the text box catches focus
     */
    private Timer timer;
    /**
     * a focus handler that is invoked when the text box catches focus
     */
    private ExpressionFocusHandler focusHandler;
    /**
     * a keyboard listener to set focus when a user types any text
     */
    private ExpressionKeyboardHandler keyboardHandler;
    /**
     * request timeout value
     */
    private int requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    /** flag that stores model initialization status */
    private boolean modelInitialized;

    /**
     * Constructs a new SuggestionBox.</p>
     * By default the minimal expression length is <code>3</code>.
     */
    public SuggestionBox() {
        this(3);
    }

    /**
     * Constructs an instance of this class and allows specifying the minimal length of the expression.
     *
     * @param expressionLength is an expression length.
     */
    public SuggestionBox(int expressionLength) {
        this.expressionLength = expressionLength;
        setCustomTextAllowed(true);
        setChoiceButtonVisible(false);
    }

    /**
     * Getter for property 'expressionLength'.
     *
     * @return Value for property 'expressionLength'.
     */
    public int getExpressionLength() {
        return expressionLength;
    }

    /**
     * Setter for property 'expressionLength'.
     *
     * @param expressionLength Value to set for property 'expressionLength'.
     */
    public void setExpressionLength(int expressionLength) {
        this.expressionLength = expressionLength;
    }

    /**
     * Gets a timeout value between last expression change and getting data.
     *
     * @return a request timeout value.
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Sets the request timeout value.<p/>
     * Set it to control how often the widget will request a server for data.
     *
     * @param requestTimeout is a request timeout value in msc.
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    /**
     * Setter for property 'model'.
     *
     * @param model Value to set for property 'model'.
     */
    public void setModel(SuggestionBoxDataModel model) {
        if (model != null) {
            this.modelInitialized = true;
            super.setModel(model);
            addSuggestionBoxListener(model);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public SuggestionBoxDataModel getModel() {
        if (!modelInitialized) {
            setModel(new SuggestionBoxDataModel(null));
        }
        return (SuggestionBoxDataModel) super.getModel();
    }

    /**
     * Adds a new suggestion box listener that will be invoked on expression change.
     *
     * @param listener is a listener to be added.
     */
    public void addSuggestionBoxListener(SuggestionBoxListener listener) {
        removeSuggestionBoxListener(listener);
        getSuggestionBoxListeners().add(listener);
    }

    /**
     * This method removes the suggestion box listener.
     *
     * @param listener a suggestion box listener instance to remove.
     */
    public void removeSuggestionBoxListener(SuggestionBoxListener listener) {
        getSuggestionBoxListeners().remove(listener);
    }

    /**
     * This method returns an expression entered by a user for drop down list selection.<p/>
     * If the associated model is not {@link org.gwt.advanced.client.datamodel.SuggestionBoxDataModel} it retuns
     * <code>null</code>.
     *
     * @return an expression value.
     */
    public String getExpression() {
        ListDataModel comboBoxDataModel = getModel();
        if (comboBoxDataModel instanceof SuggestionBoxDataModel)
            return ((SuggestionBoxDataModel) comboBoxDataModel).getExpression();
        else
            return null;
    }

    /**
     * Sets the expression value and displays it in the text box.
     *
     * @param expression is an expression to be applied.
     */
    public void setExpression(String expression) {
        ListDataModel comboBoxDataModel = getModel();
        if (comboBoxDataModel instanceof SuggestionBoxDataModel) {
            ((SuggestionBoxDataModel) comboBoxDataModel).setExpression(expression);
            getSelectedValue().setText(expression);
        }
    }

    /**
     * Immediately refreshes the list of values which may match the expression.<p/>
     * Use this method if whant to open the popup list from the
     * {@link org.gwt.advanced.client.datamodel.ListCallbackHandler} implementation and values are NOT cached on a
     * client side.
     *
     * @deprecated you don't have to invoke this method since the list is updated on data model changes.
     */
    public void refreshList() {
    }

    /**
     * {@inheritDoc}
     */
    public void cleanSelection() {
        super.cleanSelection();
        getModel().setExpression(null);
    }

    /**
     * Additionally listens for events produced by the
     * {@link org.gwt.advanced.client.datamodel.SuggestionBoxDataModel}.
     * <p/>
     * {@inheritDoc}
     */
    public void onModelEvent(ListModelEvent event) {
        if (event.getType() == SuggestionModelEvent.EXPRESSION_CHANGED
                && !getText().equals(((SuggestionModelEvent) event).getExpression())) {
            setText(((SuggestionModelEvent) event).getExpression());
            getListPanel().adjustSize();
        } else if (event.getType() != SuggestionModelEvent.EXPRESSION_CHANGED) {
            super.onModelEvent(event);
        }
    }

    /**
     * Additionally cleans the list every time when the first item is added.<p/>
     * In other cases works like the same method in the super class.
     *
     * @param event is an event containing data about the added item.
     */
    protected void add(ListModelEvent event) {
        if (event.getItemIndex() > getItemCount())
            return;

        getTimer().cancel();
        if (!isListPanelOpened()) {
            getListPanel().getList().clear();
            getListPanel().getScrollPanel().setWidth(getOffsetWidth() + "px");
            getListPanel().show();
        }
        super.add(event);
        getListPanel().selectRow(getModel().getSelectedIndex());
    }

    /**
     * {@inheritDoc}
     */
    protected void addComponentListeners() {
        getSelectedValue().addFocusHandler(getExpressionFocusHandler());
        getSelectedValue().addBlurHandler(getExpressionFocusHandler());
        getSelectedValue().addKeyUpHandler(getExpressionKeyboardHandler());
        getSelectedValue().addChangeHandler(getExpressionKeyboardHandler());
        getSelectedValue().addClickHandler(getDelegateHandler());
        getSelectedValue().addKeyDownHandler(getDelegateHandler());
        getSelectedValue().addKeyPressHandler(getDelegateHandler());

        getChoiceButton().addFocusHandler(getDelegateHandler());
        getChoiceButton().addBlurHandler(getDelegateHandler());
        getChoiceButton().addClickHandler(getDelegateHandler());
		
		getListPanel().addChangeHandler(getDelegateHandler());
    }

    /**
     * Getter for property 'suggestionBoxListeners'.
     *
     * @return Value for property 'suggestionBoxListeners'.
     */
    protected List<SuggestionBoxListener> getSuggestionBoxListeners() {
        if (suggestionBoxListeners == null)
            suggestionBoxListeners = new ArrayList<SuggestionBoxListener>();
        return suggestionBoxListeners;
    }

    /**
     * Getter for property 'timer'.
     *
     * @return Value for property 'timer'.
     */
    protected Timer getTimer() {
        if (timer == null)
            timer = new ExpressionTimer();
        return timer;
    }

    /**
     * Getter for property 'focusHandler'.
     *
     * @return Value for property 'focusHandler'.
     */
    public ExpressionFocusHandler getExpressionFocusHandler() {
        if (focusHandler == null)
            focusHandler = new ExpressionFocusHandler();
        return focusHandler;
    }

    /** {@inheritDoc} */
    public void showList(boolean prepareList) {
        getTimer().schedule(getRequestTimeout());
    }

    /**
     * Getter for property 'keyboardHandler'.
     *
     * @return Value for property 'keyboardHandler'.
     */
    protected ExpressionKeyboardHandler getExpressionKeyboardHandler() {
        if (keyboardHandler == null)
            keyboardHandler = new ExpressionKeyboardHandler();
        return keyboardHandler;
    }

    /**
     * Immediately cancels the {@link #timer} if it's run.
     */
    protected void cancelTimer() {
        getTimer().cancel();
    }

    /**
     * This is a focus listener that starts / cancels the timer.
     */
    protected class ExpressionFocusHandler implements FocusHandler, BlurHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onFocus(FocusEvent event) {
            getTimer().schedule(getRequestTimeout());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onBlur(BlurEvent event) {
            getTimer().cancel();
        }
    }

    /**
     * This handler is invoked when a user types any text and sets focus
     */
    protected class ExpressionKeyboardHandler implements KeyUpHandler, ChangeHandler {
        /** last expression value before an event */
        private String lastExpression;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChange(ChangeEvent event) {
            getTimer().cancel();
            setSelectedId(null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (!getText().equals(lastExpression) && event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
                lastExpression = getText();
                setSelectedId(null);
                getTimer().schedule(getRequestTimeout());
            }
        }
    }

    /**
     * This is a timer that displays the list of items.
     */
    protected class ExpressionTimer extends Timer {
        /**
         * {@inheritDoc}
         */
        public void run() {
            String text = getSelectedValue().getText();
            if (text.length() < getExpressionLength())
                return;

            for (SuggestionBoxListener listener : getSuggestionBoxListeners())
                listener.onChange(text);
        }
    }
}

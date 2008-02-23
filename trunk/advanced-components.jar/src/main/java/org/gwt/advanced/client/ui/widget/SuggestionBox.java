package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.datamodel.ComboBoxDataModel;
import org.gwt.advanced.client.datamodel.SuggestionBoxDataModel;
import org.gwt.advanced.client.ui.SuggestionBoxListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements suggestion box logic.<p/>
 * To use it properly you should specify the data model that is instance of
 * the {@link org.gwt.advanced.client.datamodel.SuggestionBoxDataModel} class. Otheriwse it will work as
 * a simple combo box.<br/>
 * See also {@link #setModel(org.gwt.advanced.client.datamodel.SuggestionBoxDataModel)}.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class SuggestionBox extends ComboBox {
    /**
     * suggestion expression length
     */
    private int expressionLength;
    /**
     * a list of suggestion box listeners
     */
    private List suggestionBoxListeners;
    /**
     * a timer that is activated when the text box catches focus
     */
    private Timer timer;
    /**
     * a focus listener that is invoked when the text box catches focus
     */
    private FocusListener focusListener;
    /**
     * a keyboard listener to set focus when a user types any text
     */
    private KeyboardListener keyboardListener;


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
     * Setter for property 'model'.
     *
     * @param model Value to set for property 'model'.
     */
    public void setModel(SuggestionBoxDataModel model) {
        setModel((ComboBoxDataModel) model);
        addSuggestionBoxListener(model);
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
     * {@inheritDoc}
     */
    protected void prepareSelectedValue() {
        setCustomTextAllowed(true);
        super.prepareSelectedValue();
        setChoiceButtonVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    protected void addComponentListeners() {
        super.addComponentListeners();
        getSelectedValue().removeFocusListener(getFocusListener());
        getSelectedValue().addFocusListener(getFocusListener());
        getSelectedValue().removeKeyboardListener(getKeyboardListener());
        getSelectedValue().addKeyboardListener(getKeyboardListener());
        getSelectedValue().removeFocusListener(getDelegateListener());
    }

    /**
     * Getter for property 'suggestionBoxListeners'.
     *
     * @return Value for property 'suggestionBoxListeners'.
     */
    protected List getSuggestionBoxListeners() {
        if (suggestionBoxListeners == null)
            suggestionBoxListeners = new ArrayList();
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
     * Getter for property 'focusListener'.
     *
     * @return Value for property 'focusListener'.
     */
    public FocusListener getFocusListener() {
        if (focusListener == null)
            focusListener = new ExpressionFocusListener();
        return focusListener;
    }

    /**
     * Getter for property 'keyboardListener'.
     *
     * @return Value for property 'keyboardListener'.
     */
    public KeyboardListener getKeyboardListener() {
        if (keyboardListener == null)
            keyboardListener = new ExpressionKeyboardListener();
        return keyboardListener;
    }

    /**
     * This is a focus listener that starts / cancels the timer.
     */
    protected class ExpressionFocusListener implements FocusListener {
        /** {@inheritDoc} */
        public void onFocus(Widget sender) {
            getTimer().scheduleRepeating(500);
        }

        /** {@inheritDoc} */
        public void onLostFocus(Widget sender) {
            getTimer().cancel();
            ((ExpressionTimer)getTimer()).setLastExpression(null);
        }
    }

    /**
     * This listener is invoked when a user types any text and sets focus
     */
    protected class ExpressionKeyboardListener implements KeyboardListener {
        /** {@inheritDoc} */
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }

        /** {@inheritDoc} */
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
            getSelectedValue().setFocus(false);
            getSelectedValue().setFocus(true);
        }

        /** {@inheritDoc} */
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        }
    }

    /**
     * This is a timer that displays the list of items.
     */
    protected class ExpressionTimer extends Timer {
        /** a last displayed expression filter */
        private String lastExpression;

        /** {@inheritDoc} */
        public void run() {
            String text = getSelectedValue().getText();
            if (text == null || text.length() < getExpressionLength() || text.equals(getLastExpression()))
                return;

            for (Iterator iterator = getSuggestionBoxListeners().iterator(); iterator.hasNext();) {
                SuggestionBoxListener listener = (SuggestionBoxListener) iterator.next();
                listener.onChange(text);
            }

            if (getModel().getCount() > 0) {
                getTimer().cancel();
                getListPanel().display();
            }
            setLastExpression(text);
        }

        /**
         * Getter for property 'lastExpression'.
         *
         * @return Value for property 'lastExpression'.
         */
        protected String getLastExpression() {
            return lastExpression;
        }

        /**
         * Setter for property 'lastExpression'.
         *
         * @param lastExpression Value to set for property 'lastExpression'.
         */
        protected void setLastExpression(String lastExpression) {
            this.lastExpression = lastExpression;
        }
    }
}

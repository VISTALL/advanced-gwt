package org.gwt.advanced.client.datamodel;

import org.gwt.advanced.client.ui.SuggestionBoxListener;

/**
 * This is an implementation designed especially for the suggestion box widget.<p/>
 * The content of this widget depends on the expression value. Expression interpretation may be different
 * and must be defined in the {@link org.gwt.advanced.client.datamodel.ListCallbackHandler} implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public class SuggestionBoxDataModel extends ComboBoxDataModel implements SuggestionBoxListener {
    /** an expression to suggest items for the list */
    private String expression;
    /** a callback handler to fill the list */
    private ListCallbackHandler handler;

    /**
     * Constructs the class and sets the expression value to <code>null</code>.
     * 
     * @param handler is a handler to fill data.
     */
    public SuggestionBoxDataModel(ListCallbackHandler handler) {
        this(null, handler);
    }

    /**
     * Constructs the class and performs filling via the handler.
     *
     * @param expression an expression to be used for filtering.
     * @param handler a callback handler to fill the list.
     */
    public SuggestionBoxDataModel(String expression, ListCallbackHandler handler) {
        this.expression = expression;
        this.handler = handler;

        fill();
    }

    /**
     * Getter for property 'expression'.
     *
     * @return Value for property 'expression'.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Setter for property 'expression'.
     *
     * @param expression Value to set for property 'expression'.
     */
    public void setExpression(String expression) {
        this.expression = expression; 
    }

    /** {@inheritDoc} */
    public void onChange(String expression) {
        setExpression(expression);
        fill();
    }
    
    /**
     * Getter for property 'handler'.
     *
     * @return Value for property 'handler'.
     */
    protected ListCallbackHandler getHandler() {
        return handler;
    }

    /**
     * This method fills the list with the values.
     */
    protected void fill() {
        getHandler().fill(this);
    }
}
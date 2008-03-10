package org.gwt.advanced.client.ui;

/**
 * This is a suggestion box events listener.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public interface SuggestionBoxListener {
    /**
     * This method is invoked every time a user change the value in the
     * suggestion box.
     *
     * @param expression is an expression.
     */
    void onChange(String expression);
}
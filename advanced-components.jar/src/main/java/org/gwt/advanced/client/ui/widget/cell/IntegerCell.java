package org.gwt.advanced.client.ui.widget.cell;

/**
 * This is a cell implementation for <code>Integer</code> numbers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class IntegerCell extends NumberCell {
    /** Constructs a new IntegerCell. */
    public IntegerCell () {
        super(INTEGER_PATTERN, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    /** {@inheritDoc} */
    protected Number convertToNumber(String text) {
        return Integer.valueOf(text);
    }
}

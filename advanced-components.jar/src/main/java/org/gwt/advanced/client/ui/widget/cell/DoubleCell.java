package org.gwt.advanced.client.ui.widget.cell;

/**
 * This is a cell implementation for <code>Double</code> numbers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class DoubleCell extends NumberCell {
    /** Constructs a new DoubleCell. */
    public DoubleCell() {
        super(REAL_PATTERN, Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    /** {@inheritDoc} */
    protected Number convertToNumber(String text) {
        return Double.valueOf(text);
    }
}

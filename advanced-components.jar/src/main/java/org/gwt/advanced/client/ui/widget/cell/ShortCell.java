package org.gwt.advanced.client.ui.widget.cell;

/**
 * This is a cell implementation for <code>Short</code> numbers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class ShortCell extends NumberCell {
    /** Constructs a new ShortCell. */
    public ShortCell() {
        super(INTEGER_PATTERN, Short.MAX_VALUE, Short.MIN_VALUE);
    }

    /** {@inheritDoc} */
    protected Number convertToNumber(String text) {
        return Short.valueOf(text);
    }
}

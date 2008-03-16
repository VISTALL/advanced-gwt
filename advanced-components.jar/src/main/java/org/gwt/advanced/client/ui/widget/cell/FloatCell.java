package org.gwt.advanced.client.ui.widget.cell;

/**
 * This is a cell implementation for <code>Float</code> numbers.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class FloatCell extends NumberCell {
    /** Constructs a new FloatCell. */
    public FloatCell() {
        super(REAL_PATTERN, Float.MAX_VALUE, -Float.MAX_VALUE);
    }

    /** {@inheritDoc} */
    protected Number convertToNumber(String text) {
        return Float.valueOf(text);
    }
}

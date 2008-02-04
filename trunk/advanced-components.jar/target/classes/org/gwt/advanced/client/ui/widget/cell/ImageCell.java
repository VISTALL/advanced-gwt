package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a cell implementation forr <code>Image</code> widgets.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class ImageCell extends AbstractCell {
    /** one pixel image name */
    public static final String SINGLE_IMAGE = "advanced/images/single.gif";
    /** image value */
    private Image image;

    /** {@inheritDoc} */
    public boolean valuesEqual (Object value) {
        return false; //because there is no way to compare selected values
    }

    /** {@inheritDoc} */
    protected Widget createActive() {
        return createInactive();
    }

    /** {@inheritDoc} */
    protected Widget createInactive() {
        removeStyleName("active-cell");
        addStyleName("passive-cell");
        removeStyleName("image-cell");
        addStyleName("image-cell");
        return getImage();
    }

    /**
     * Getter for property 'image'.
     *
     * @return Value for property 'image'.
     */
    protected Image getImage() {
        if (image == null)
            image = (Image) getValue();
        
        if (image == null) {
            image = new Image();
            image.setUrl(SINGLE_IMAGE);
        }

        return image;
    }

    /** {@inheritDoc} */
    public void setFocus(boolean focus) {
    }

    /** {@inheritDoc} */
    public Object getNewValue () {
        return getValue();
    }
}

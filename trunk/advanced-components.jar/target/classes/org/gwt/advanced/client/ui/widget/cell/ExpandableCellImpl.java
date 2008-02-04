package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.widget.HierarchicalGrid;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is a basic expandable cell implementation.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class ExpandableCellImpl extends AbstractCell implements ExpandableCell {
    /** a dock panel to place an original cell and a node image */
    private DockPanel panel;
    /** a node image */
    private Image image;
    /** image click listener */
    private ClickListener imageClickListener;
    /** expanded flag */
    private boolean expanded;
    /** leaf flag */
    private boolean leaf;

    /**
     * Creates an instance of this class.
     */
    public ExpandableCellImpl() {
        setStyleName("expandable-cell");
    }

    /** {@inheritDoc} */
    public void displayActive(boolean active) {
        Widget widget = createInactive();
        prepare(widget);
    }

    /** {@inheritDoc} */
    protected Widget createActive() {
        return createInactive();
    }

    /** {@inheritDoc} */
    protected Widget createInactive() {
        DockPanel panel = getPanel();
        Image image = getImage();
        Widget child = (Widget) getValue();

        if (panel.getWidgetIndex(image) == -1)
            panel.add(image, DockPanel.WEST);
        panel.setCellWidth(image, "1%");

        if (panel.getWidgetIndex(child) == -1)
            panel.add(child, DockPanel.CENTER);
        panel.setCellWidth(child, "99%");

        return panel;
    }

    /** {@inheritDoc} */
    public void setFocus(boolean focus) {
    }

    /** {@inheritDoc} */
    protected void addListeners(Widget widget) {
        if (imageClickListener == null) {
            final ExpandableCell cell = this;
            imageClickListener = new ClickListener() {
                public void onClick(Widget sender) {
                    setExpanded(!isExpanded());

                    DockPanel panel = getPanel();
                    panel.remove(getImage());
                    createImage();
                    panel.add(getImage(), DockPanel.WEST);


                    if (!isLeaf())
                        ((HierarchicalGrid)getGrid()).fireExpandCell(cell);
                }
            };
        }

        getImage().removeClickListener(imageClickListener);
        getImage().addClickListener(imageClickListener);
    }

    /** {@inheritDoc} */
    public Object getNewValue() {
        return getValue();
    }

    /** {@inheritDoc} */
    public boolean isExpanded() {
        return expanded;
    }

    /** {@inheritDoc} */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /** {@inheritDoc} */
    public boolean isLeaf () {
        return leaf;
    }

    /** {@inheritDoc} */
    public void setLeaf (boolean leaf) {
        this.leaf = leaf;
        if (leaf)
            setExpanded(false);
    }

    /**
     * Getter for property 'image'.
     *
     * @return Value for property 'image'.
     */
    protected Image getImage() {
        if (image == null)
            createImage();
        return image;
    }

    /**
     * This method creates the node image and adds the listener.
     */
    protected void createImage() {
        if (isLeaf())
            setImage(new Image("advnaced/images/single.gif"));
        if (isExpanded())
            setImage(new Image(ThemeHelper.getInstance().getFullImageName("expanded.gif")));
        else if (!isLeaf())
            setImage(new Image(ThemeHelper.getInstance().getFullImageName("collapsed.gif")));
        addListeners(getImage());
    }

    /**
     * Setter for property 'image'.
     *
     * @param image Value to set for property 'image'.
     */
    protected void setImage(Image image) {
        this.image = image;
    }

    /**
     * Getter for property 'panel'.
     *
     * @return Value for property 'panel'.
     */
    protected DockPanel getPanel() {
        if (panel == null) {
            panel = new DockPanel();
            panel.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        }
        return panel;
    }
}

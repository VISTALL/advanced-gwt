package org.gwt.advanced.client.ui.widget.cell;

/**
 * This interface describes expandable cell methods.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface ExpandableCell extends GridCell {
    /**
     * This method checks whether the cell is expanded.
     *
     * @return a result of check.
     */
    boolean isExpanded();

    /**
     * This method sets an expanded flag value for the cell.
     *
     * @param expanded is an expanded falg value.
     */
    void setExpanded(boolean expanded);

    /**
     * This method checks whether the expanded cell is a leaf.
     *
     * @return a result of check.
     */
    boolean isLeaf();

    /**
     * This method sets a leaf flag value.
     *
     * @param leaf a leaf flag value.
     */
    void setLeaf(boolean leaf);
}

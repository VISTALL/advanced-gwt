/*
 * Copyright 2008 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwt.advanced.client.ui.widget.cell;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.gwt.advanced.client.datamodel.TreeGridRow;

/**
 * This is a tree cell implementation that renders an expand / collapse image and makes indents.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.0
 */
public class TreeCell extends ExpandableCellImpl implements ExpandableCell {
    /** indent value in pixels */
    public static final int INDENT = 10;
    /** gridRow gridRow */
    private TreeGridRow gridRow;

    /** {@inheritDoc} */
    protected void createImage() {
        super.createImage();
        Element image = getImage().getElement();
        DOM.setElementAttribute(image, "class", "indent-cell");
        DOM.setStyleAttribute(image, "marginLeft", getGridRow().getLevel() * INDENT + "px");
    }

    /** {@inheritDoc} */
    public void setLeaf(boolean leaf) {
        super.setLeaf(leaf);
        getPanel().remove(getImage());
        createImage();
        displayActive(false);
        getImage().removeClickListener(getImageClickListener());
    }

    /** {@inheritDoc} */
    public TreeGridRow getGridRow() {
        return gridRow;
    }

    /**
     * Setter for property 'gridRow'.
     *
     * @param gridRow Value to set for property 'gridRow'.
     */
    public void setGridRow(TreeGridRow gridRow) {
        this.gridRow = gridRow;
    }
}

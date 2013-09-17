package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.SimpleGrid;

/**
 * This sample demonstrates basic grids functionality: rendering, columns resizing and scrolling.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.6
 */
public class SimpleGridShowcase extends AbstractShowcase {
    /** sample widget */
    private SimpleGrid grid;

    /** {@inheritDoc} */
    protected Widget getWidget() {
        grid = new SimpleGrid();

        for (int i = 0; i < 3; i++) {
            grid.setHeaderWidget(i, new Label("Header " + i));
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 3; j++) {
                grid.setText(i, j, "Cell " + i + " " + j);
            }
        }

        for (int i = 0; i < 3; i++) {
            grid.setFooterWidget(i, new Label("Footer " + i));
        }

        return grid;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.simpleGridDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Simple Grid";
    }

    /**
     * Overrides the super method to enable vertical scrolling.
     *
     * @param panel the main tab panel.
     */
    public void initShowcase(AdvancedTabPanel panel) {
        super.initShowcase(panel);
//        grid.setBodyHeight("200px");
        grid.enableVerticalScrolling(true);
    }
}

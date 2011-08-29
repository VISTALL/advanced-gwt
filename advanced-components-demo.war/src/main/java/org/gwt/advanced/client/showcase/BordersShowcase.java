/*
 * Copyright 2011 Sergey Skladchikov
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

package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.ui.widget.border.RoundCornerBorder;
import org.gwt.advanced.client.ui.widget.border.SingleBorder;

/**
 * This is a showcase for the {@link org.gwt.advanced.client.ui.widget.border.RoundCornerBorder} and
 * {@link org.gwt.advanced.client.ui.widget.border.SingleBorder} widgets.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class BordersShowcase extends AbstractShowcase {
    /** {@inheritDoc} */
    protected Widget getWidget() {
        FlexTable layout = new FlexTable();
        layout.setStyleName("demo-Layout");

        RoundCornerBorder border1 = new RoundCornerBorder();
        border1.addStyleName("border");
        border1.setWidget(new Label("Rounded Corners"));
        layout.setWidget(0, 0, border1);

        RoundCornerBorder border2 = new RoundCornerBorder(true, true, true, false);
        border2.addStyleName("border");
        border2.setWidget(new Label("Bottom line is hidden"));
        layout.setWidget(1, 0, border2);

        RoundCornerBorder border3 = new RoundCornerBorder(true, true, false, false);
        border3.addStyleName("border");
        border3.setWidget(new Label("Right & bottom lines are hidden"));
        layout.setWidget(2, 0, border3);

        RoundCornerBorder border4 = new RoundCornerBorder();
        border4.addStyleName("border");
        border4.setWidget(new Label("Shadow under the border"));
        layout.setWidget(3, 0, border4);
        border4.setShadowVisibile(true);

        RoundCornerBorder border5 = new RoundCornerBorder();
        border5.setStyleName("demo-Border");
        border5.addStyleName("border");
        border5.setWidget(new Label("Theme independent border"));
        layout.setWidget(4, 0, border5);

        SingleBorder border = new SingleBorder();
        border.addStyleName("border");
        RoundCornerBorder border6 = new RoundCornerBorder();
        border6.addStyleName("border");
        border6.setWidget(new Label("Complex border"));
        border.setWidget(border6);
        layout.setWidget(5, 0, border);
        border.setShadowVisibile(true);

        return layout;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.bordersDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Borders";
    }
}

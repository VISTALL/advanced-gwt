/*
 * Copyright 2009 Sergey Skladchikov
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

package org.gwt.advanced.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.DOM;
import org.gwt.advanced.client.ui.Resizable;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.border.RoundCornerBorder;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;

/**
 * This is an abstract show case panel.
 *
 * @author Sergey Skladchikov
 */
public abstract class AbstractShowcase extends VerticalPanel implements Resizable {
    /** localized messages */
    protected ResourceBundle RESOURCES = (ResourceBundle) GWT.create(ResourceBundle.class);

    protected AbstractShowcase() {
        setStyleName("demo-Showcase");
    }

    /**
     * It's invoked on adding into the main tab panel.
     */
    public void resize() {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (getWidget(i) instanceof Resizable)
                ((Resizable) getWidget(i)).resize();
        }
    }

    /**
     * Gets a widget that represents a show case.
     *
     * @return a show case widget.
     */
    protected abstract Widget getWidget();

    /**
     * Gets the hint that must be displayed below the show case.
     *
     * @return a hint value.
     */
    protected abstract String getHint();

    /**
     * Gets a name of the tab in the main tab panel.
     *
     * @return a naem of the tab.
     */
    protected abstract String getName();
    
    /**
     * Initilizes the show case panel and places it into the main tab panel.
     *
     * @param panel the main tab panel.
     */
    protected void initShowcase(AdvancedTabPanel panel) {
        AdvancedTabPanel showCasePanel = new AdvancedTabPanel(TabPosition.BOTTOM);
        showCasePanel.addStyleName("tab-panel");
        add(showCasePanel);
        RoundCornerBorder border = new RoundCornerBorder();
        border.addStyleName("demo-HintLabel");
        border.setWidget(new HTML(getHint()));
        add(border);

        showCasePanel.addTab(new Label("Live Demo"), getWidget());
        HTML iframe = new HTML();
        iframe.setHTML("<iframe class='demo-Source' src='xref/" + GWT.getTypeName(this).replaceAll("\\.", "/") + ".html'/>");
        showCasePanel.addTab(new Label("Source Code"), iframe);

        panel.addTab(new Label(getName()), this);
        DOM.setStyleAttribute(DOM.getParent(showCasePanel.getElement()), "height", "100%");
    }
}

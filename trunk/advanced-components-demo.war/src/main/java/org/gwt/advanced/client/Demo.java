/*
 * Copyright 2008-2013 Sergey Skladchikov
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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwt.advanced.client.showcase.*;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is a demo entry point.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class Demo implements EntryPoint {
    /** Draws the sample screen. */
    public void onModuleLoad() {
        AdvancedTabPanel advancedTabPanel = new AdvancedTabPanel(TabPosition.LEFT);
        RootPanel.get("theme").add(createThemeSwitcher());
        RootPanel.get("container").add(advancedTabPanel);

        new EditableGridShowcase().initShowcase(advancedTabPanel);
        new HierarchicalGridShowcase().initShowcase(advancedTabPanel);
        new TreeGridShowcase().initShowcase(advancedTabPanel);
        new SimpleGridShowcase().initShowcase(advancedTabPanel);
        new MVPShowcase().initShowcase(advancedTabPanel);
        new MasterDetailPanelShowcase().initShowcase(advancedTabPanel);
        new TextAndButtonShowcase().initShowcase(advancedTabPanel);
        new BordersShowcase().initShowcase(advancedTabPanel);
    }

    /**
     * This method creates the theme switcher.
     *
     * @return a list box widget.
     */
    private ListBox createThemeSwitcher() {
        ThemeHelper.getInstance().setThemeName("classic");

        ListBox themes = new ListBox();
        themes.addItem("classic", "classic");
        themes.addItem("default", "default");
        themes.addItem("gray", "gray");
        themes.addItem("ascetic", "ascetic");
        themes.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ListBox box = (ListBox) event.getSource();
                ThemeHelper themeHelper = ThemeHelper.getInstance();
                themeHelper.setThemeName(box.getValue(box.getSelectedIndex()));
            }
        });
        return themes;
    }
}

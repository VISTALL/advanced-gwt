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

package org.gwt.advanced.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.widget.*;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;
import org.gwt.advanced.client.ui.widget.cell.DateCell;
import org.gwt.advanced.client.ui.widget.cell.ComboBoxCell;
import org.gwt.advanced.client.util.ThemeHelper;

import java.util.Date;

/**
 * This is a demo entry point.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class Demo implements EntryPoint {
    /** a list of demo hints */
    private static final String[] HINTS;

    static {
        ResourceBundle bundle = (ResourceBundle) GWT.create(ResourceBundle.class);
        HINTS = new String[] {
            bundle.editableGridDemo(),
            bundle.hierarchicalGridDemo(),
            bundle.lazyGridDemo(),
            bundle.treeGridDemo(),
            bundle.masterDetailDemo(),
            bundle.otherControlsDemo()
        };
    }

    /**
     * Draws the sample screen.
     */
    public void onModuleLoad() {
        DockPanel panel = new DockPanel();
        panel.setStyleName("demo-Table");
        final HTML hintLabel = new HTML(HINTS[0]);

        TabPanel tabPanel = new TabPanel();
        panel.add(tabPanel, DockPanel.CENTER);

        FlexTable switcherTable = new FlexTable();
        switcherTable.setStyleName("demo-Theme");
        panel.add(switcherTable, DockPanel.NORTH);
        ListBox switcher = createThemeSwitcher();
        switcherTable.setWidget(0, 0, new Label("Change theme:"));
        switcherTable.setWidget(0, 1, switcher);

        createEditableGridDemo(tabPanel);
        final AdvancedFlexTable scrollableGrid = createHierarchicalGridDemo(tabPanel).getGrid();
        createLazyLoadableGridDemo(tabPanel);
        createTreeGridDemo(tabPanel);
        createMasterDetailGridDemo(tabPanel);
        createOtherControlsDemo(tabPanel);

        //add this listener to demostrate scrolling feature demo
        tabPanel.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected (SourcesTabEvents sender, int tabIndex) {
                hintLabel.setHTML(HINTS[tabIndex]);
                return true;
            }

            public void onTabSelected (SourcesTabEvents sender, int tabIndex) {
                Widget widget = ((TabPanel) sender).getWidget(tabIndex);
                if (widget instanceof GridPanel)
                  ((GridPanel)widget).resize();
                else if (widget instanceof MasterDetailPanel)
                  ((MasterDetailPanel)widget).resize();

                if (tabIndex == 1) {
                    scrollableGrid.setHeight(((int)(Window.getClientHeight() * 0.5)) + "px");
                    scrollableGrid.enableVerticalScrolling(true);
                }
            }
        });

        tabPanel.selectTab(0);
        
        SimplePanel hint = new SimplePanel();
        hintLabel.setStyleName("demo-HintLabel");
        hint.add(hintLabel);
        panel.add(hint, DockPanel.SOUTH);

        RootPanel.get().add(panel);
    }

    /**
     * This method creates the theme switcher.
     *
     * @return a list box widget.
     */
    private ListBox createThemeSwitcher() {
        ListBox themes = new ListBox();
        themes.addItem("default", "default");
        themes.addItem("gray", "gray");
        themes.addItem("ascetic", "ascetic");
        themes.addChangeListener(new ChangeListener() {
            public void onChange (Widget sender) {
                ListBox box = (ListBox) sender;
                ThemeHelper themeHelper = ThemeHelper.getInstance();
                themeHelper.setThemeName(box.getValue(box.getSelectedIndex()));
            }
        });
        return themes;
    }

    /**
     * This method creates a sample of the editable grid.
     *
     * @param panel is a tab panel instnace where the grid must be placed.
     * @return is a created grid panel.
     */
    private GridPanel createEditableGridDemo(TabPanel panel) {
        GridPanel gridPanel =
                new GridPanelFactoryImpl().create(DemoModelFactory.createEployeesModel(-1)); //-1 means "display all"
        panel.add(gridPanel, "Editable Grid");
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.display();
        return gridPanel;
    }

    /**
     * This method creates a sample of the hierarchical grid.
     *
     * @param panel is a tab panel instnace where the grid must be placed.
     * @return is a created grid panel.
     */
    private GridPanel createHierarchicalGridDemo (TabPanel panel) {
        GridPanel gridPanel = new GridPanel();

        gridPanel.createEditableGrid(
            new String[] {
                "Department", "Human Resources", "Description", "ID"
            },
            new Class[] {
                TextBoxCell.class, TextBoxCell.class, TextBoxCell.class, LongCell.class
            },
            DemoModelFactory.createHierarchicalDepartmentsModel()
        );
        gridPanel.setReadonlyColumn(1, true);
        gridPanel.setTopToolbarVisible(false);
        ((HierarchicalGrid)gridPanel.getGrid()).addGridPanelFactory(1, new GridPanelFactoryImpl());
        panel.add(gridPanel, "Hierarchical Grid");
        gridPanel.setInvisibleColumn(3, true);
        gridPanel.display();

        return gridPanel;
    }

    /**
     * This method creates a sample of the lazy loadable editable grid.
     *
     * @param panel is a tab panel instnace where the grid must be placed.
     * @return is a created grid panel.
     */
    private GridPanel createLazyLoadableGridDemo(TabPanel panel) {
        Editable editable = DemoModelFactory.createLazyEmployeesModel();
        GridPanel gridPanel = new GridPanelFactoryImpl().create(editable);
        ((ServiceEmulationModelHandler)editable.getHandler()).setPanel(gridPanel);
        panel.add(gridPanel, "Lazy Loadable Grid");
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.display();
        return gridPanel;
    }

    /**
     * This method creates a sample of the tree grid.
     *
     * @param panel is a tab panel instnace where the grid must be placed.
     * @return is a created grid panel.
     */
    private GridPanel createTreeGridDemo(TabPanel panel) {
        GridPanel gridPanel = new GridPanel();
        gridPanel.createEditableGrid(
            new String[]{
                    "Name", "Birth Date", "Position", "ID"
            },
            new Class[]{
                    TextBoxCell.class, DateCell.class, ComboBoxCell.class, LongCell.class
            },
            DemoModelFactory.createTreePeopleModel()
        );
        gridPanel.setInvisibleColumn(3, true);
        panel.add(gridPanel, "Tree Grid");
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.display();
        return gridPanel;
    }

    /**
     * Creates a tab containing Master-Detail panel demo.
     *
     * @param panel is a tab panel.
     * @return a result of creation.
     */
    private MasterDetailPanel createMasterDetailGridDemo(TabPanel panel) {
        MasterDetailPanel masterDetailPanel = new MasterDetailPanel();

        //create master grid panel (departments)
        GridPanel master = new GridPanel();
        master.createEditableGrid(
            new String[] {
                "Department", "Human Resources", "Description", "ID"
            },
            new Class[] {
                TextBoxCell.class, TextBoxCell.class, TextBoxCell.class, LongCell.class
            },
            DemoModelFactory.createDepartmentsModel()
        );
        master.setReadonlyColumn(1, true);
        master.setTopToolbarVisible(false);
        master.setInvisibleColumn(3, true);
        masterDetailPanel.addGridPanel(master, null, "Departments");
        master.display();

        //create detail grid panel (employees)
        GridPanel detail = new GridPanelFactoryImpl().create(new LazyGridDataModel(new Object[0][0]));
        detail.getGrid().setModel(DemoModelFactory.createDepartmentDetailModel(detail, master));
        detail.setTopToolbarVisible(false);
        masterDetailPanel.addGridPanel(detail, master, "Employees");
        detail.display();

        panel.add(masterDetailPanel, "Master-Detail Grids");
        masterDetailPanel.display();
        return masterDetailPanel;
    }

    /**
     * Creates a tab containing other controls demo.
     *
     * @param tab is a tab to be inserted.
     */
    private void createOtherControlsDemo(TabPanel tab) {
        FlexTable panel = new FlexTable();

        panel.setWidget(0, 0, createHintLabel("Select a country:"));
        panel.setWidget(1, 0, createHintLabel("Select a date:"));
        panel.setWidget(2, 0, createHintLabel("Type any European country name:"));
        panel.setWidget(3, 0, createHintLabel("Type any European country name to see the flag:"));

        panel.getColumnFormatter().setWidth(0, "50%");

        ComboBox comboBox = new ComboBox();
        comboBox.setWidth("100%");
        ComboBoxDataModel model = DemoModelFactory.createsCountriesModel();
        comboBox.setModel(model);
        comboBox.setCustomTextAllowed(true);

        DatePicker picker = new DatePicker(new Date());
        picker.setWidth("100%");
        picker.setTimeVisible(true);

        SuggestionBox suggestionBox = new SuggestionBox();
        suggestionBox.setExpressionLength(1);
        suggestionBox.setModel(new SuggestionBoxDataModel(new SuggestionBoxHandler()));
        suggestionBox.setWidth("100%");

        SuggestionBox complexSuggestionBox = new SuggestionBox();
        complexSuggestionBox.setExpressionLength(1);
        complexSuggestionBox.setModel(new SuggestionBoxDataModel(new SuggestionBoxFlagsHandler()));
        complexSuggestionBox.setWidth("100%");

        panel.setWidget(0, 1, comboBox);
        panel.setWidget(1, 1, picker);
        panel.setWidget(2, 1, suggestionBox);
        panel.setWidget(3, 1, complexSuggestionBox);

        panel.getColumnFormatter().setWidth(1, "50%");

        tab.add(panel, "Other Controls");

        comboBox.display();
        picker.display();
        suggestionBox.display();
        complexSuggestionBox.display();
    }

    /**
     * Creates a hint label for the other controls demo.
     *
     * @param text is a hint text.
     * @return a new label.
     */
    private Label createHintLabel(String text) {
        Label label = new Label(text);
        label.setStyleName("demo-ControlLabel");
        return label;
    }

    /**
     * Sample suggestion box handler.
     */
    private class SuggestionBoxHandler implements ListCallbackHandler {
        /** {@inheritDoc} */
        public void fill(ListDataModel model) {
            String expression = ((SuggestionBoxDataModel) model).getExpression();
            DemoModelFactory.fillCountriesModel(expression, (SuggestionBoxDataModel) model);
        }
    }

    /**
     * Sample suggestion box handler.
     */
    private class SuggestionBoxFlagsHandler implements ListCallbackHandler {
        /** {@inheritDoc} */
        public void fill(ListDataModel model) {
            String expression = ((SuggestionBoxDataModel) model).getExpression();
            DemoModelFactory.fillCountriesWithFlagsModel(expression, (SuggestionBoxDataModel) model);
        }
    }
}

package org.gwt.advanced.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.LazyGridDataModel;
import org.gwt.advanced.client.ui.widget.AdvancedFlexTable;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.HierarchicalGrid;
import org.gwt.advanced.client.ui.widget.MasterDetailPanel;
import org.gwt.advanced.client.ui.widget.cell.LongCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;
import org.gwt.advanced.client.util.ThemeHelper;

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
            bundle.masterDetailDemo()
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
        final AdvancedFlexTable scorllableGrid = createHierarchicalGridDemo(tabPanel).getGrid();
        createLazyLoadableGridDemo(tabPanel);
        createMasterDetailGridDemo(tabPanel);

        //add this listener to demostrate scrolling feature demo
        tabPanel.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected (SourcesTabEvents sender, int tabIndex) {
                hintLabel.setHTML(HINTS[tabIndex]);
                return true;
            }

            public void onTabSelected (SourcesTabEvents sender, int tabIndex) {
                if (tabIndex == 1) {
                    scorllableGrid.enableVerticalScrolling(true);
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
        gridPanel.display();
        return gridPanel;
    }

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
}

package org.gwt.advanced.client;

import org.gwt.advanced.client.ui.widget.*;
import org.gwt.advanced.client.ui.widget.cell.LabelCell;
import org.gwt.advanced.client.ui.widget.cell.IntegerCell;
import org.gwt.advanced.client.ui.widget.cell.AbstractCell;
import org.gwt.advanced.client.ui.widget.cell.GridCell;
import org.gwt.advanced.client.ui.GridListenerAdapter;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.EditCellListener;
import org.gwt.advanced.client.ui.SelectRowListener;
import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.util.ThemeHelper;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Window;

import java.util.Date;

public class Sample {
    public static void sample1() {
        //create a new grid
        EditableGrid grid = new EditableGrid(
            new String[]{"First Name", "Surname"},
            new Class[]{LabelCell.class, LabelCell.class}
        );

        //create a new model
        Editable model = new EditableGridDataModel(
            new Object[][] {
                new String[]{"John", "Doe"},
                new String[]{"Piter", "Walkman"},
                new String[]{"Rupert", "Brown"}
            }
        );

        //apply the model
        grid.setModel(model);

        //display the grid
        grid.display();

        GridPanel panel = new GridPanel();
        // initialize the panel here

        // add custom listener
        panel.getMediator().addGridListener(new GridListenerAdapter() {
            public void onSave(GridDataModel dataModel) {
                //do synchronization
            }
        });
    }

    public static void sample2() {
        //create a new model containing employees
        Editable model = new EditableGridDataModel(
            new Object[][] {
                new String[]{"John", "Doe"},
                new String[]{"Piter", "Walkman"},
                new String[]{"Rupert", "Brown"}
            }
        );

        //create a new model containing departments
        HierarchicalGridDataModel hierarchicalModel = new HierarchicalGridDataModel(
            new Object[][] {
                new Object[]{"Accountants", new Integer(3)},
                new Object[]{"Management", new Integer(10)},
                new Object[]{"Development", new Integer(100)}
            }
        );

        //make a relationship between Accountants department and a list of employees
        hierarchicalModel.addSubgridModel(0, 0, model);
    }

    public static void sample3() {
        LazyLoadable model = new LazyGridDataModel(
            new DataModelCallbackHandler() {
                public void synchronize(GridDataModel model) {
                    // get data here

                    // set a total row count
                    ((LazyLoadable)model).setTotalRowCount(1000);
                }
            }
        );
    }

    public static void sample4() {
        LazyLoadable model = new LazyGridDataModel(
            new DataModelCallbackHandler() {
                public void synchronize(GridDataModel model) {
                    // get removed rows
                    Object[][] removedRows = ((Editable) model).getRemovedRows();

                    // delete data from the storage

                    // clear old information 
                    ((Editable) model).clearRemovedRows();
                }
            }
        );
    }

    public static void sample5() {
        AdvancedFlexTable table = new AdvancedFlexTable();

        // create headers and put them in the thead tag
        table.setHeaderWidget(0, new Label("First Name"));
        table.setHeaderWidget(1, new Label("Surname"));

        // enable verticall scrolling 
        table.enableVerticalScrolling(true);
    }

    public static void sample6() {
        Editable model = new EditableGridDataModel(null);

        // create a new grid panel
        GridPanel panel = new GridPanel();

        // create a new editable grid and put it into the panel
        EditableGrid grid = panel.createEditableGrid (
            new String[]{"First Name", "Surname"},
            new Class[]{LabelCell.class, LabelCell.class},
            model
        );

        // display all
        panel.display();
    }

    public static void sample7() {
        Editable model = new HierarchicalGridDataModel(null);

        // create a new grid panel
        GridPanel panel = new GridPanel();

        // create a new editable grid and put it into the panel
        HierarchicalGrid grid = (HierarchicalGrid) panel.createEditableGrid (
            new String[]{"Department", "Number of Employees"},
            new Class[]{LabelCell.class, IntegerCell.class},
            model
        );

        // add a grid panel factory to the second column
        grid.addGridPanelFactory(
            1, new GridPanelFactory() {
                public GridPanel create(GridDataModel model) {
                    GridPanel panel = new GridPanel();
                    // create a new grid here
                    return panel;
                }

                public GridDataModel create(int parentRow, GridDataModel parentModel) {
                     return new EditableGridDataModel(new Object[0][0]);
                }
            }
        );

        // display all
        panel.display();
    }

    public static void sample8() {
        GridPanel panel = new GridPanel();

        panel.setTopPagerVisible(false); // switch off the top pager
        panel.setBottomPagerVisible(true); // switch on the bottom pager
        panel.setTopToolbarVisible(false); // switch off the top toolbar
        panel.setBottomToolbarVisible(true); // switch on the bottom toolbar

        panel.setInvisibleColumn(0, true); // set the first column invisible
        panel.setSortableColumn(1, false); // make the second column non-soirtable
        panel.setReadonlyColumn(2, true); // make the third column read only
    }

    public static void sample9() {
        MasterDetailPanel panel = new MasterDetailPanel();

        GridPanel masterPanel = new GridPanel();
        panel.addGridPanel(masterPanel, null, "Departments");
        // initialize the master grid here
        masterPanel.display();

        GridPanel detailPanel = new GridPanel();
        panel.addGridPanel(detailPanel, masterPanel, "Employees");
        // initialize the detail grid here
        detailPanel.display();

        // apply styles
        panel.display();
    }

    public static void sample10() {
        DatePicker picker = new DatePicker(new Date());
        // swicth off time entering
        picker.setTimeVisible(false);
        // display the widget
        picker.display();
    }

    public static void sample11() {
        ThemeHelper helper = ThemeHelper.getInstance();

        // diaplay a name of the current theme
        Window.alert("Current theme is " + helper.getThemeName());

        // change theme to "gray"
        helper.setThemeName("gray");

        // diaplay a name of the current theme again
        Window.alert("Current theme is " + helper.getThemeName());
    }

    public static void quickstart() {
        //create a new model containing employees
        Editable model = new EditableGridDataModel(
            new Object[][] {
                new String[]{"John", "Doe"},
                new String[]{"Piter", "Walkman"},
                new String[]{"Rupert", "Brown"}
            }
        );

        // create a new grid of employees
        GridPanel panel = new GridPanel();
        panel.createEditableGrid(
            new String[]{"First Name", "Surname"},
            new Class[]{LabelCell.class, LabelCell.class},
            model
        );

        // display all
        panel.display();
        RootPanel.get().add(panel);
    }

    public class MyHandler implements DataModelCallbackHandler {
        private GridPanel panel;

        public MyHandler(GridPanel panel) {
            this.panel = panel;
        }

        public void synchronize(GridDataModel model) {
            //lock the grid panel
            panel.lock();
            try {
                // synchronize data here
            } finally {
                // unlock the panel and refresh grid content
                panel.unlock();
            }
        }
    }

    public class MyCell extends AbstractCell {
        private Button button = new Button("Sample");

        protected Widget createActive() {
            button.setEnabled(true); // enabled and avaliable when the cell is active 
            return button;
        }

        protected Widget createInactive() {
            button.setEnabled(false); // disabled before a user clicks the cell
            return button;
        }

        public void setFocus(boolean focus) {
            // do nothing
        }

        public Object getNewValue() {
            return button;
        }
    }

    public class MyValidator implements EditCellListener {
        public boolean onStartEdit(GridCell cell) {
            return true; // if this method returns false, the cell will never be activated
        }

        public boolean onFinishEdit(GridCell cell, Object newValue) {
            // if the first column value is equal to "wrong", return false
            return cell.getColumn() != 0 || !"wrong".equals(newValue);
        }
    }

    public class MyGridPanelFactory implements GridPanelFactory {
        public GridPanel create(GridDataModel model) {
            GridPanel panel = new GridPanel();

            // initialize the subgrid
            panel.createEditableGrid (
                new String[]{"First Name", "Surname"},
                new Class[]{LabelCell.class, LabelCell.class},
                (Editable) model
            );

            return panel;
        }

        public GridDataModel create(int parentRow, GridDataModel parentModel) {
            // default initialization
            return new EditableGridDataModel(new Object[0][0]);
        }
    }

    public class MyRowSelectionListener implements SelectRowListener {
        public void onSelect(EditableGrid grid, int row) {
            Window.alert("Row number " + row);
        }
    }
}

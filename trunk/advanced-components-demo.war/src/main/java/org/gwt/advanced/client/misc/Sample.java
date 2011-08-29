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

package org.gwt.advanced.client.misc;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.EditCellListener;
import org.gwt.advanced.client.ui.GridListenerAdapter;
import org.gwt.advanced.client.ui.GridPanelFactory;
import org.gwt.advanced.client.ui.SelectRowListener;
import org.gwt.advanced.client.ui.widget.*;
import org.gwt.advanced.client.ui.widget.border.Border;
import org.gwt.advanced.client.ui.widget.border.BorderFactory;
import org.gwt.advanced.client.ui.widget.border.RoundCornerBorder;
import org.gwt.advanced.client.ui.widget.border.SingleBorder;
import org.gwt.advanced.client.ui.widget.cell.*;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;
import org.gwt.advanced.client.ui.widget.tab.TopBandRenderer;
import org.gwt.advanced.client.util.ThemeHelper;

import java.util.Date;

public class Sample {
    public static void sample1() {
        //create a new grid
        EditableGrid<Editable> grid = new EditableGrid<Editable>(
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

        //apply the model and render the grid
        grid.setModel(model);

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
                new Object[]{"Accountants", 3},
                new Object[]{"Management", 10},
                new Object[]{"Development", 100}
            }
        );

        //make a relationship between Accountants department and a list of employees
        hierarchicalModel.addSubgridModel(0, 0, model);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void sample3() {
        LazyLoadable model = new LazyGridDataModel(
            new DataModelCallbackHandler<Editable>() {
                public void synchronize(Editable model) {
                    // get data here

                    // set a total row count
                    ((LazyLoadable)model).setTotalRowCount(1000);
                }
            }
        );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void sample4() {
        LazyLoadable model = new LazyGridDataModel(
            new DataModelCallbackHandler<Editable>() {
                public void synchronize(Editable model) {
                    // get removed rows
                    Object[][] removedRows = model.getRemovedRows();

                    // delete data from the storage

                    // clear old information 
                    model.clearRemovedRows();
                }
            }
        );
    }

    public static void sample5() {
        AdvancedFlexTable table = new AdvancedFlexTable();

        // create headers and put them in the thead tag
        table.setHeaderWidget(0, new Label("First Name"));
        table.setHeaderWidget(1, new Label("Surname"));

        // enabled verticall scrolling
        table.enableVerticalScrolling(true);
    }

    @SuppressWarnings({"UnusedDeclaration"})
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

        // render all
        panel.display();
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    public static void sample7() {
        Editable hierarchicalModel = new HierarchicalGridDataModel(null);

        // create a new grid panel
        GridPanel panel = new GridPanel();

        // create a new editable grid and put it into the panel
        HierarchicalGrid grid = (HierarchicalGrid) panel.createEditableGrid (
            new String[]{"Department", "Number of Employees"},
            new Class[]{LabelCell.class, IntegerCell.class},
            null
        );
        grid.setModel(hierarchicalModel);

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

        // render all
        panel.display();

        RootPanel.get().add(panel);
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

    @SuppressWarnings({"UnusedDeclaration"})
    public static void sample12() {
        Editable model = new EditableGridDataModel(null);

        // create a new grid panel
        GridPanel panel = new GridPanel();

        // create a new editable grid and put it into the panel
        EditableGrid grid = panel.createEditableGrid (
            new String[]{"First Name", "Surname"},
            new Class[]{LabelCell.class, LabelCell.class},
            model
        );

        // render all
        panel.display();

        RootPanel.get().add(panel);
    }

    @SuppressWarnings({"UnusedDeclaration", "unchecked"})
    public static void sample13() {
        //create a new model and add one root item
        TreeGridDataModel model = new TreeGridDataModel(new Object[][]{new String[]{"President"}});
        model.setAscending(false);
        model.setPageSize(3);

        //create second level items
        TreeGridRow president = (TreeGridRow) model.getRow(0);
        president.setExpanded(true);
        president.setPageSize(3);
        president.setPagerEnabled(true);
        model.addRow(president, new String[]{"Financial Department Director"});
        model.addRow(president, new String[]{"Marketing Department Director"});
        model.addRow(president, new String[]{"Chief Security Officer"});
        model.addRow(president, new String[]{"Development Department Director"});

        //create third level items for the first department
        TreeGridRow financialDirector = model.getRow(president, 0);
        model.addRow(financialDirector, new String[]{"Accountant"});
        model.addRow(financialDirector, new String[]{"Financial Manager"});

        //create third level items for the second department
        TreeGridRow marketingDirector = model.getRow(president, 0);
        model.addRow(marketingDirector, new String[]{"Brand Manager"});
        model.addRow(marketingDirector, new String[]{"Sales manager"});
        model.addRow(marketingDirector, new String[]{"Promouter"});

        //create third level items for the last department and configure paging
        TreeGridRow developmentDirector = model.getRow(president, 0);
        president.setPageSize(3);
        president.setPagerEnabled(true);
        model.addRow(marketingDirector, new String[]{"Database Developer"});
        model.addRow(marketingDirector, new String[]{"UI Developer"});
        model.addRow(marketingDirector, new String[]{"Support Engeneer"});
        model.addRow(marketingDirector, new String[]{"Tester"});

        //create a grid panel
        GridPanel panel = new GridPanel();

        //create and put the tree grid in the panel
        panel.createEditableGrid(
                new String[]{"Posts"}, new Class[]{TextBoxCell.class}, null
        ).setModel(model);

        RootPanel.get().add(panel);
    }

    public static void sample14() {
        SimpleGrid grid = new SimpleGrid();

        // create headers and put them in the thead tag
        grid.setHeaderWidget(0, new Label("First Name"));
        grid.setHeaderWidget(1, new Label("Surname"));

        // enabled verticall scrolling
        grid.enableVerticalScrolling(true);

        //set column widths in pixels
        grid.setColumnWidth(0, 200);
        grid.setColumnWidth(0, 100);
    }

    public static void sample15() {
        //create the model and callback handler
        SuggestionBoxDataModel model = new SuggestionBoxDataModel(new ListCallbackHandler(){
            public void fill(ListDataModel model) {
                if ("John".equals(((SuggestionBoxDataModel)model).getExpression())) {
                    //remove old values
                    model.clear();

                    //add Johns
                    model.add("john1", "John Doe");
                    model.add("john2", "John Parkinson");
                    model.add("john3", "John Todd");  
                } else {
                    //remove old values
                    model.clear();

                    //add default item
                    model.add("", "Nobody");
                }
            }
        });

        //initilize the suggestion box
        SuggestionBox box = new SuggestionBox();
        box.setModel(model);
        box.setMaxLength(3);
    }

    public static void sample16() {
        AdvancedTabPanel panel1 = new AdvancedTabPanel(TabPosition.LEFT);
        AdvancedTabPanel panel2 = new AdvancedTabPanel(TabPosition.BOTTOM);

        panel1.addTab(new Label("Nested Tabs"), panel2);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void sample17() {
        BorderFactory contentbBorderFactory = new BorderFactory() {
            public Border create() {
                return new SingleBorder();
            }
        };
        
        BorderFactory tabBorderFactory = new BorderFactory() {
            public Border create() {
                return new RoundCornerBorder();
            }
        };

        AdvancedTabPanel panel = new AdvancedTabPanel(TabPosition.TOP, tabBorderFactory, contentbBorderFactory);
    }

    @SuppressWarnings({"unchecked"})
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
            null
        ).setModel(model);

        RootPanel.get().add(panel);
    }

    @SuppressWarnings({"EmptyTryBlock"})
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

    public class MyGridEventManager extends DefaultGridEventManager {
        public MyGridEventManager(GridPanel panel) {
            super(panel);
        }

        public boolean dispatch(GridPanel panel, char keyCode, int modifiers) {
            if (keyCode == KeyCodes.KEY_TAB) //move the cursor to the next cell on TAB pressing
                moveToNextCell();
            else
                return super.dispatch(panel, keyCode, modifiers);
            return false;
        }
    }

    public class MyGridRenderer extends DefaultGridRenderer {
        public MyGridRenderer(EditableGrid grid) {
            super(grid);
        }

        public void drawHeaders(Object[] headers) {
            //draw simple labels
            for (int i = 0; i < headers.length; i++) {
                Object header = headers[i];
                getGrid().setHeaderWidget(i, new Label(String.valueOf(header)));
            }
        }
    }

    public class ServerSideContentRenderer extends DefaultGridRenderer {
        private String html; // HTML tbody content rendered on server side

        public ServerSideContentRenderer(EditableGrid grid, String html) {
            super(grid);
            this.html = html;
        }

        public void drawContent(GridDataModel model) {
            DOM.setInnerHTML(getTBodyElement(), html);
        }
    }

    public static class MyTabPosition extends TabPosition {
        public static final TabPosition CUSTOM = new MyTabPosition("custom");

        protected MyTabPosition(String name) {
            super(name, new MyTabBandRenderer(), LayoutPosition.TOP);
        }
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    public static class MyTabBandRenderer extends TopBandRenderer {
        public Widget render(AdvancedTabPanel panel) {
            Widget tab = super.render(panel);

            //do something specific

            return tab;
        }
    }
}

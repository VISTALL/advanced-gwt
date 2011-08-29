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

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.datamodel.EditableModelEvent;
import org.gwt.advanced.client.datamodel.EditableModelListener;
import org.gwt.advanced.client.ui.SelectRowListener;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.border.RoundCornerBorder;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;

import java.util.ArrayList;
import java.util.List;

/**
 * This is sample that shows how MVP pattern is implemented in the Advanced GWT Components
 * library<p/>
 * Entering different values in the input fields users can change data in the grid. All
 * operations affect the data model only and it informs the grid about changes to make it refresh
 * a row.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.7
 */
public class MVPShowcase extends AbstractShowcase {
    /** column display names */
    private static final String[] COLUMNS = new String[]{"Column 1", "Column 2", "Column 3"};
    /** a widget to be returned as a result of the {@link #getWidget()} method */
    private VerticalPanel widget = new VerticalPanel();
    /** a table where the form is placed */
    private FlexTable formTable = new FlexTable();
    /** the grid panel that contains a resulting grid */
    private GridPanel gridPanel;
    /** displayed text boxes (need it for text cleaning) */
    private List<TextBox> textBoxes = new ArrayList<TextBox>();

    /** {@inheritDoc} */
    protected Widget getWidget() {
        //setup the widget
        widget.setWidth("100%");

        // prepare the form
        EditableGridDataModel model = createModel();
        createForm(model);
        RoundCornerBorder border = new RoundCornerBorder();
        border.setWidget(formTable);
        widget.add(border);

        // prepare the grid panel
        createGridPanel(model);
        widget.add(gridPanel);
        gridPanel.display();

        return widget;
    }

    /**
     * This method creates a grid panel and initlizes the grid with the specified model.
     *
     * @param model is a model to be applied to the grid.
     */
    protected void createGridPanel(EditableGridDataModel model) {
        gridPanel = new GridPanel();
        gridPanel.createEditableGrid(
                COLUMNS,
                new Class[]{TextBoxCell.class, TextBoxCell.class, TextBoxCell.class},
                model
        );

        gridPanel.setTopPagerVisible(false);
        gridPanel.setBottomPagerVisible(false);
        gridPanel.setTopToolbarVisible(false);
        for (int i = 0; i < COLUMNS.length; i++)
            gridPanel.setReadonlyColumn(i, true);
        gridPanel.setMultiRowModeEnabled(false);

        SelectListener rowListener = new SelectListener();
        gridPanel.addSelectRowListener(rowListener);
        model.addListener(rowListener);

        // intialize the text boxes with the values extracted from the first row
        setTextBoxValues(gridPanel.getGrid().getModel().getRowData(gridPanel.getGrid().getModelRow(0)));
    }

    /**
     * Creates the form that contains column values in the text boxes
     *
     * @param model is a grid model to associate it with the text boxes.
     */
    protected void createForm(EditableGridDataModel model) {
        for (int i = 0; i < COLUMNS.length; i++) {
            String column = COLUMNS[i];
            Label label = new Label(column);
            label.setStyleName("demo-FormLabel");
            formTable.setWidget(i, 0, label);
            TextBox box = new TextBox();
            box.setStyleName("demo-TextBox");
            formTable.setWidget(i, 1, box);
            box.addKeyUpHandler(new FormKeyboardHandler(i, model));
            textBoxes.add(box);
        }
    }

    /**
     * Creates a grid data model that is used for events firing.
     *
     * @return a result data model.
     */
    protected EditableGridDataModel createModel() {
        String[][] values = new String[10][COLUMNS.length];
        for (int j = 0; j < 10; j++)
            for (int i = 0; i < COLUMNS.length; i++)
                values[j][i] = "Cell " + j + i;
        return new EditableGridDataModel(values);
    }

    /**
     * This method sets the specified values to the text boxes converting them to strings.
     *
     * @param values is a list of values to set in order of columns.
     */
    protected void setTextBoxValues(Object[] values) {
        int count = 0;
        for (Object textBoxe : textBoxes) {
            TextBox textBox = (TextBox) textBoxe;
            textBox.setText(String.valueOf(values[count++]));
        }
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.mvpDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "MVP Demo";
    }

    /** {@inheritDoc} */
    public void resize() {
        if (gridPanel != null)
            gridPanel.resize();
    }

    /**
     * This handler is added to the text boxes and being invoked updates the data model
     * getting row number by the selected row in the grid.
     */
    protected class FormKeyboardHandler implements KeyUpHandler {
        /** column to update */
        private int column;
        /** the model to be updated */
        private EditableGridDataModel model;

        /**
         * Creates an instance of this handler and initializes internal variables.
         *
         * @param column is a column to be associated with the text box.
         * @param model is a model to be updated.
         */
        public FormKeyboardHandler(int column, EditableGridDataModel model) {
            this.column = column;
            this.model = model;
        }
        
        /** See class docs. */
        @Override
        public void onKeyUp(KeyUpEvent event) {
            model.update(gridPanel.getGrid().getCurrentRow(), column, ((TextBox) event.getSource()).getText());
        }
    }

    /**
     * This is a listener that listens for row selectin events and data model events and
     * refreshes the text boxes with column values currently selected.
     */
    protected class SelectListener implements SelectRowListener, EditableModelListener {
        /** See class docs */
        public void onSelect(EditableGrid grid, int row) {
            setTextBoxValues(grid.getModel().getRowData(grid.getModelRow(row)));
        }

        /** See class docs */
        public void onModelEvent(EditableModelEvent event) {
            if (event.getEventType() == EditableModelEvent.SORT_ALL) {
                EditableGrid grid = gridPanel.getGrid();
                setTextBoxValues(grid.getModel().getRowData(grid.getModelRow(grid.getCurrentRow())));
            }
        }
    }
}

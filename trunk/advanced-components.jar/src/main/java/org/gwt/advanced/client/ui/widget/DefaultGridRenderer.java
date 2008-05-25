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

package org.gwt.advanced.client.ui.widget;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.CheckBox;
import org.gwt.advanced.client.ui.GridRenderer;
import org.gwt.advanced.client.ui.widget.cell.*;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.Editable;

import java.util.Date;

/**
 * This is default grid renderer implementation that is applied by grid if there is
 * no custom one specified.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public class DefaultGridRenderer implements GridRenderer {
    /** target grid instance */
    private EditableGrid grid;

    /**
     * Creates an instance of this class and initializes the grid cell factory.
     *
     * @param grid is a target grid.
     */
    public DefaultGridRenderer(EditableGrid grid) {
        this.grid = grid;
    }

    /** {@inheritDoc} */
    public void drawHeaders(Object[] headers) {
        for (int i = 0; i < headers.length; i++) {
            if (getGrid().isVisible(i)) {
                String header = (String) headers[i];
                HeaderCell cell = getCellFactory().create(i, header);
                cell.displayActive(false);
            }
        }
    }

    /** {@inheritDoc} */
    public void drawContent(GridDataModel model) {
        int start = model.getStartRow();
        int end = model.getEndRow();
        boolean empty = model.isEmpty();

        if (!empty && getGrid().getRowDrawHandler() != null) {
            DeferredCommand.addCommand(new DrawRowCommand(start, end));
        } else {
            for (int i = start; !empty && i <= end; i++)
                drawRow(model.getRowData(i), i - start);
        }

        for (int i = getGrid().getRowCount() - 1; i >= end - start + 1; i--)
            getGrid().removeRow(i);
    }

    /** {@inheritDoc} */
    public void drawRow(Object[] data, int row) {
        EditableGrid grid = getGrid();
        for (int i = 0; i < data.length; i++) {
            if (grid.isVisible(i)) {
                drawCell(data[i], row, i, false);
                grid.getCellFormatter().addStyleName(row, i, "grid-column");
            }
        }
    }

    /** {@inheritDoc} */
    public void drawCell(Object data, int row, int column, boolean active) {
        if (active)
            getCellFactory().create(row, column, data).displayActive(true);
        else {
            Class columnType = grid.getColumnWidgetClasses()[column];

            if (
                IntegerCell.class.equals(columnType) || LongCell.class.equals(columnType)
                || DoubleCell.class.equals(columnType) || FloatCell.class.equals(columnType)
                || ShortCell.class.equals(columnType)
            )
                setCellText(formatString(data), row, column, "numeric-cell");
            else if (ListCell.class.equals(columnType))
                setCellText(getListBoxText((ListBox) data), row, column, "list-cell");
            else if (DateCell.class.equals(columnType))
                setCellText(formatDate((Date) data), row, column, "date-cell");
            else if (LabelCell.class.equals(columnType) || TextBoxCell.class.equals(columnType))
                setCellText(formatString(data), row, column, "text-cell");
            else if (ImageCell.class.equals(columnType))
                setCellWidget((Widget) data, row, column, "image-cell");
            else if (BooleanCell.class.equals(columnType)) {
                CheckBox checkBox = new CheckBox();
                checkBox.setChecked(Boolean.valueOf(String.valueOf(data)).booleanValue());
                setCellWidget(checkBox, row, column, "boolean-cell");
            } else
                getCellFactory().create(row, column, data).displayActive(false);
        }
    }

    /** {@inheritDoc} */
    public int getModelRow(int row) {
        Editable dataModel = getGrid().getModel();
        return row + dataModel.getPageSize() * dataModel.getCurrentPageNumber();
    }

    /**
     * This method sets the cell value in textual format.<p/>
     * It also decorates the cell with the specified style.
     *
     * @param text is a text of the cell value.
     * @param row is a row number.
     * @param column is a column number.
     * @param style is a style name.
     */
    protected void setCellText(String text, int row, int column, String style) {
        getGrid().setText(row, column, text);
        decorateCell(row, column, style);
    }

    /**
     * Sets the widget into the specified cell and decorates this cell with the specified style.
     *
     * @param widget is a widget to set.
     * @param row is a row number.
     * @param column is a column number.
     * @param style is a cell style.
     */
    protected void setCellWidget(Widget widget, int row, int column, String style) {
        getGrid().setWidget(row, column, widget);
        decorateCell(row, column, style);
    }

    /**
     * This method decorates the cell with styles.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @param style is a style name.
     */
    protected void decorateCell(int row, int column, String style) {
        HTMLTable.CellFormatter formatter = getGrid().getCellFormatter();
        formatter.setStyleName(row, column, "data-cell");
        formatter.addStyleName(row, column, style);

        if (getGrid().isReadOnly(column))
            formatter.addStyleName(row, column, "readonly-cell");
        else
            formatter.addStyleName(row, column, "editable-cell");
    }

    /**
     * This method formates the specified date.
     *
     * @param date is a date to be formatted.
     * @return a date string.
     */
    protected String formatDate (Date date) {
        if (date == null)
            return "";
        return DateTimeFormat.getLongDateFormat().format(date);
    }

    /**
     * This method formats the object and returns its textual representation<p/>
     * If the object is null the method gets an empty string.
     *
     * @param data is an object to be formatted.
     * @return a textual representation of the object.
     */
    protected String formatString(Object data) {
        if (data == null)
            return "";

        return String.valueOf(data);
    }

    /**
     * This method returns a text for the inactive label.
     *
     * @param listBox is a list box.
     *
     * @return a label text.
     */
    protected String getListBoxText(ListBox listBox) {
        String labelText = "";
        if (listBox != null) {
            int index = -1;

            if (listBox.getItemCount() > 0)
                index = listBox.getSelectedIndex();

            if (index != -1)
                labelText = listBox.getItemText(index);
            else if (listBox.getItemCount() > 0)
                labelText = listBox.getItemText(0);
        }
        return labelText;
    }
    
    /**
     * Getter for property 'cellFactory'.
     *
     * @return Value for property 'cellFactory'.
     */
    protected GridCellFactory getCellFactory() {
        return getGrid().getGridCellFactory();
    }

    /**
     * This method retuns the thead element of the grid.
     *
     * @return a thead element.
     */
    protected Element getTHeadElement() {
        return getGrid().getTHeadElement();
    }

    /**
     * This method returns a tbody element of the grid.
     *
     * @return a tbody element.
     */
    protected Element getTBodyElement() {
        return getGrid().getBodyElement();
    }

    /**
     * Getter for property 'grid'.
     *
     * @return Value for property 'grid'.
     */
    public EditableGrid getGrid() {
        return grid;
    }

    /**
     * This command displays one row.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class DrawRowCommand implements IncrementalCommand {
        /** start row number */
        private int start;
        /** end row number */
        private int end;
        /** current row number */
        private int current;

        /**
         * Creates an instance of this class and initilizes internal variables.
         *
         * @param start is a start row number.
         * @param end is an end row number.
         */
        public DrawRowCommand(int start, int end) {
            this.start = start;
            this.end = end;
            this.current = this.start;
            DOM.setStyleAttribute(getGrid().getBodyElement(), "display", "none");
        }

        /**
         * This method stops when the end row reached.
         *
         * @return <code>true</code> if drawing must be continued.
         */
        public boolean execute() {
            int row = current - start;
            int pageSize = end - start + 1;

            boolean cont = false;
            for (int i = 0; current <= end && i < 1; i++) {
                Object[] data = getGrid().getModel().getRowData(current);
                if (getGrid().fireBeforeDrawEvent(row, pageSize, data))
                    drawRow(data, row);
                cont = getGrid().fireAfterDrawEvent(row, pageSize, data);
                if (!cont)
                    break;
                current++;
            }

            if (!cont || current > end)
                DOM.setStyleAttribute(getGrid().getBodyElement(), "display", "");
            return cont && current <= end;
        }
    }
}

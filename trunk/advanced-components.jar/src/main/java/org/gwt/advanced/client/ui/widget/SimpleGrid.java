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

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import org.gwt.advanced.client.util.GWTUtil;

/**
 * This is a super class for all advanced grids.<p/>
 * It represents a grid as a pair of flex tables: header and body. And delegates method calls to these parts.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.5
 */
public class SimpleGrid extends AdvancedFlexTable {
    /** header container */
    private AdvancedFlexTable headerTable;
    /** body container */
    private AdvancedFlexTable bodyTable;
    /** body scroll panel */
    private ScrollPanel scrollPanel;
    /** flag meaning that the grid has already benen initialized */
    private boolean initialized;
    /**
     * a falg that means whether the grid columns are resizable
     */
    protected boolean resizable;

    /** Constructs a new SimpleGrid. */
    public SimpleGrid() {
        this(true);
    }

    /**
     * Creates an instance of this class and does nothing else.
     *  
     * @param resizable is a resizable option flag.
     */
    public SimpleGrid(boolean resizable) {
        super.setWidget(0, 0, getHeaderTable());
        Element tr = getRowFormatter().getElement(0);
        DOM.setStyleAttribute(tr, "border", "0");
        DOM.setStyleAttribute(tr, "padding", "0");
        DOM.setStyleAttribute(tr, "margin", "0");
        Element td = getCellFormatter().getElement(0, 0);
        DOM.setStyleAttribute(td, "border", "0");
        DOM.setStyleAttribute(td, "padding", "0");
        DOM.setStyleAttribute(td, "margin", "0");
        getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        getHeaderTable().setStyleName("advanced-Grid");

        super.setWidget(1, 0, getBodyTable());
        tr = getRowFormatter().getElement(1);
        DOM.setStyleAttribute(tr, "border", "0");
        DOM.setStyleAttribute(tr, "padding", "0");
        DOM.setStyleAttribute(tr, "margin", "0");
        td = getCellFormatter().getElement(1, 0);
        DOM.setStyleAttribute(td, "border", "0");
        DOM.setStyleAttribute(td, "padding", "0");
        DOM.setStyleAttribute(td, "margin", "0");
        getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        getBodyTable().setStyleName("advanced-Grid");

        DOM.setStyleAttribute(getElement(), "border", "0");
        DOM.setStyleAttribute(getElement(), "padding", "0");
        DOM.setStyleAttribute(getElement(), "margin", "0");
        DOM.setStyleAttribute(getElement(), "borderCollapse", "collapse");
        
        setResizable(resizable);
        this.initialized  = true;
    }

    /**
     * This method resizes the grid making it to fit as much space as possible.
     */
    public void resize() {
        Element parent = DOM.getParent(getElement());
        if (parent == null || !isResizable())
            return;

        if (isScrollable()) {
            GWTUtil.adjustWidgetSize(this, parent, true);
            GWTUtil.adjustWidgetSize(getScrollPanel(), getBodyElement(), true);
            parent = getScrollPanel().getElement();
        } 

        if (getBodyTable().getRowCount() > 0) {
            int parentWidth = DOM.getElementPropertyInt(parent, "offsetWidth");
            int count = getBodyTable().getCellCount(0);
            int size = parentWidth / count;
            for (int i = 0; i < count; i++)
                setColumnWidth(i, size);
            if (!GWTUtil.isIE()) {
                int widthNow = Math.max(
                    DOM.getElementPropertyInt(getBodyTable().getElement(), "offsetWidth"),
                    DOM.getElementPropertyInt(getHeaderTable().getElement(), "offsetWidth")
                );
                if (count > 0 && size * count < widthNow)
                    setColumnWidth(count - 1, size - widthNow + size * count);
            }
        }
    }

    /** {@inheritDoc} */
    public void setHeaderWidget(int column, Widget widget) {
        getHeaderTable().setHeaderWidget(column, widget);
        Element tr = DOM.getChild(getTHeadElement(), 0);
        Element th = DOM.getChild(tr, column);
        if (isResizable()) {
            DOM.setStyleAttribute(th, "overflow", "hidden");
            DOM.setStyleAttribute(th, "whiteSpace", "nowrap");
        }
    }

    /** {@inheritDoc} */
    public void removeHeaderWidget(int column) {
        getHeaderTable().removeHeaderWidget(column);
    }

    /** {@inheritDoc} */
    public void enableVerticalScrolling(boolean enabled) {
        if (isScrollable() != enabled) {
            super.enableVerticalScrolling(enabled);
            resize();
        }
    }

    /** {@inheritDoc} */
    protected void prepareScrolling(boolean enabled) {
        this.initialized = false;
        removeCell(1, 0);
        if (enabled) {
            setWidget(1, 0, getScrollPanel());
        } else {
            setWidget(1, 0, getBodyTable());
        }
        Element td = getCellFormatter().getElement(1, 0);
        DOM.setStyleAttribute(td, "border", "0");
        DOM.setStyleAttribute(td, "padding", "0");
        DOM.setStyleAttribute(td, "margin", "0");
        getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        this.initialized = true;
    }

    /** {@inheritDoc} */
    protected int getTableHeight() {
        return DOM.getElementPropertyInt(getBodyElement(), "offsetHeight")
                + DOM.getElementPropertyInt(getTHeadElement(), "offsetHeight");
    }

    /** {@inheritDoc} */
    protected int getTableWidth() {
        return getBodyTable().getTableWidth();
    }

    /** {@inheritDoc} */
    protected void prepareHeaderCell(int column) {
        getHeaderTable().prepareHeaderCell(column);
    }

    /** {@inheritDoc} */
    protected void addHeaderCells(Element tHead, int num) {
        getHeaderTable().addHeaderCells(tHead, num);
    }

    /** {@inheritDoc} */
    public Element getTHeadElement() {
        return getHeaderTable().getTHeadElement();
    }

    /** {@inheritDoc} */
    protected List getHeaderWidgets() {
        return getHeaderTable().getHeaderWidgets();
    }

    /** {@inheritDoc} */
    protected Panel getScrollPanel() {
        if (scrollPanel == null) {
            scrollPanel = new ScrollPanel(getBodyTable());
            DOM.setStyleAttribute(scrollPanel.getElement(), "overflow", "");
            DOM.setStyleAttribute(scrollPanel.getElement(), "overflowX", "hidden");
            DOM.setStyleAttribute(scrollPanel.getElement(), "overflowY", "auto");
        }
        return scrollPanel;
    }

    /** {@inheritDoc} */
    public void addCell(int row) {
        if (initialized)
            getBodyTable().addCell(row);
        else
            super.addCell(row);
    }

    /** {@inheritDoc} */
    public int getCellCount(int row) {
        if (initialized)
            return getBodyTable().getCellCount(row);
        else
            return super.getCellCount(row);
    }

    /** {@inheritDoc} */
    public FlexCellFormatter getFlexCellFormatter() {
        if (initialized)
            return getBodyTable().getFlexCellFormatter();
        else
            return super.getFlexCellFormatter();
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        if (initialized)
            return getBodyTable().getRowCount();
        else
            return super.getRowCount();
    }

    /** {@inheritDoc} */
    public void insertCell(int beforeRow, int beforeColumn) {
        if (initialized)
            getBodyTable().insertCell(beforeRow, beforeColumn);
        else
            super.insertCell(beforeRow, beforeColumn);
    }

    /** {@inheritDoc} */
    public int insertRow(int beforeRow) {
        if (initialized)
            return getBodyTable().insertRow(beforeRow);
        else
            return super.insertRow(beforeRow);
    }

    /** {@inheritDoc} */
    public void removeCell(int row, int col) {
        if (initialized)
            getBodyTable().removeCell(row, col);
        else
            super.removeCell(row, col);
    }

    /** {@inheritDoc} */
    public void removeCells(int row, int column, int num) {
        if (initialized)
            getBodyTable().removeCells(row, column, num);
        else
            super.removeCells(row, column, num);
    }

    /** {@inheritDoc} */
    public void removeRow(int row) {
        if (initialized)
            getBodyTable().removeRow(row);
        else
            super.removeRow(row);
    }

    /** {@inheritDoc} */
    protected void prepareCell(int row, int column) {
        if (initialized)
            getBodyTable().prepareCell(row, column);
        else
            super.prepareCell(row, column);
    }

    /** {@inheritDoc} */
    protected void prepareRow(int row) {
        if (initialized)
            getBodyTable().prepareRow(row);
        else
            super.prepareRow(row);
    }

    /** {@inheritDoc} */
    public void addTableListener(TableListener listener) {
        if (initialized)
            getBodyTable().addTableListener(listener);
        else
            super.addTableListener(listener);
    }

    /** {@inheritDoc} */
    public void clear() {
        if (initialized) {
            getBodyTable().clear();
            getHeaderTable().clear();
        } else
            super.clear();
    }

    /** {@inheritDoc} */
    public boolean clearCell(int row, int column) {
        if (initialized)
            return getBodyTable().clearCell(row, column);
        else
            return super.clearCell(row, column);
    }

    /** {@inheritDoc} */
    public CellFormatter getCellFormatter() {
        if (initialized)
            return getBodyTable().getCellFormatter();
        else
            return super.getCellFormatter();
    }

    /** {@inheritDoc} */
    public int getCellPadding() {
        if (initialized)
            return getBodyTable().getCellPadding();
        else
            return super.getCellPadding();
    }

    /** {@inheritDoc} */
    public int getCellSpacing() {
        if (initialized)
            return getBodyTable().getCellSpacing();
        else
            return super.getCellSpacing();
    }

    /** {@inheritDoc} */
    public ColumnFormatter getColumnFormatter() {
        if (initialized)
            return getBodyTable().getColumnFormatter();
        else
            return super.getColumnFormatter();
    }

    /** {@inheritDoc} */
    public RowFormatter getRowFormatter() {
        if (initialized)
            return getBodyTable().getRowFormatter();
        else
            return super.getRowFormatter();
    }

    /** {@inheritDoc} */
    public String getText(int row, int column) {
        if (initialized)
            return getBodyTable().getText(row, column);
        else
            return super.getText(row, column);
    }

    /** {@inheritDoc} */
    public boolean isCellPresent(int row, int column) {
        if (initialized)
            return getBodyTable().isCellPresent(row, column);
        else
            return super.isCellPresent(row, column);
    }

    /** {@inheritDoc} */
    public boolean remove(Widget widget) {
        if (initialized)
            return getBodyTable().remove(widget) || getHeaderTable().remove(widget);
        else
            return super.remove(widget);
    }

    /** {@inheritDoc} */
    public void onBrowserEvent(Event event) {
        if (initialized)
            getBodyTable().onBrowserEvent(event);
        else
            super.onBrowserEvent(event);
    }

    /** {@inheritDoc} */
    public void removeTableListener(TableListener listener) {
        if (initialized)
            getBodyTable().removeTableListener(listener);
        else
            super.removeTableListener(listener);
    }

    /** {@inheritDoc} */
    public void setBorderWidth(int width) {
        if (initialized)
            getBodyTable().setBorderWidth(width);
        else
            super.setBorderWidth(width);
    }

    /** {@inheritDoc} */
    public void setCellSpacing(int spacing) {
        if (initialized)
            getBodyTable().setCellSpacing(spacing);
        else
            super.setCellSpacing(spacing);
    }

    /** {@inheritDoc} */
    public void setCellPadding(int padding) {
        if (initialized)
            getBodyTable().setCellPadding(padding);
        else
            super.setCellPadding(padding);
    }

    /** {@inheritDoc} */
    public void setText(int row, int column, String text) {
        if (initialized) {
            getBodyTable().setText(row, column, text);
            wrapContent(row, column);
        } else
            super.setText(row, column, text);
    }

    /** {@inheritDoc} */
    public void setWidget(int row, int column, Widget widget) {
        if (initialized) {
            getBodyTable().setWidget(row, column, widget);
            wrapContent(row, column);
        } else
            super.setWidget(row, column, widget);
    }

    /**
     * This method sets column size in pixels.
     *
     * @param column is a column number.
     * @param size is a size value in pixels.
     */
    public void setColumnWidth(int column, int size) {
        if (isResizable()) {
            Element tr = DOM.getChild(getHeaderTable().getTHeadElement(), 0);
            Element th = DOM.getChild(tr, column);
            DOM.setStyleAttribute(th, "width", size + "px");
            if (getRowCount() > 0) {
                Element td = getBodyTable().getCellFormatter().getElement(0, column);
                DOM.setStyleAttribute(td, "width", size + "px");
            }
        }
    }

    /**
     * This method wraps cell content into the special styles which are responsible for cell clipping.
     *
     * @param row is a row number.
     * @param column is a column number.
     * @return a wrapping td element.
     */
    protected Element wrapContent(int row, int column) {
        if (!isResizable())
            return null;
      
        Element td = getBodyTable().getCellFormatter().getElement(row, column);
        DOM.setStyleAttribute(td, "overflow", "hidden");
        DOM.setStyleAttribute(td, "whiteSpace", "nowrap");
        return td;
    }

    /** {@inheritDoc} */
    protected void checkCellBounds(int row, int column) {
        if (initialized)
            getBodyTable().checkCellBounds(row, column);
        else
            super.checkCellBounds(row, column);    
    }

    /** {@inheritDoc} */
    protected void checkRowBounds(int row) {
        if (initialized)
            getBodyTable().checkRowBounds(row);
        else
            super.checkRowBounds(row);
    }

    /** {@inheritDoc} */
    protected Element createCell() {
        if (initialized)
            return getBodyTable().createCell();
        else
            return super.createCell();
    }

    /** {@inheritDoc} */
    protected int getDOMCellCount(Element tableBody, int row) {
        if (initialized)
            return getBodyTable().getDOMCellCount(tableBody, row);
        else
            return super.getDOMCellCount(tableBody, row);
    }

    /** {@inheritDoc} */
    protected int getDOMCellCount(int row) {
        if (initialized)
            return getBodyTable().getDOMCellCount(row);
        else
            return super.getDOMCellCount(row);
    }

    /** {@inheritDoc} */
    protected int getDOMRowCount() {
        if (initialized)
            return getBodyTable().getDOMRowCount();
        else
            return super.getDOMRowCount();
    }

    /** {@inheritDoc} */
    protected int getDOMRowCount(Element elem) {
        if (initialized)
            return getBodyTable().getDOMRowCount(elem);
        else
            return super.getDOMRowCount(elem);
    }

    /** {@inheritDoc} */
    protected Element getEventTargetCell(Event event) {
        if (initialized)
            return getBodyTable().getEventTargetCell(event);
        else
            return super.getEventTargetCell(event);
    }

    /** {@inheritDoc} */
    protected void insertCells(int row, int column, int count) {
        if (initialized)
            getBodyTable().insertCells(row, column, count);
        else
            super.insertCells(row, column, count);
    }

    /** {@inheritDoc} */
    protected boolean internalClearCell(Element td, boolean clearInnerHTML) {
        if (initialized)
            return getBodyTable().internalClearCell(td, clearInnerHTML);
        else
            return super.internalClearCell(td, clearInnerHTML);
    }

    /** {@inheritDoc} */
    protected void prepareColumn(int column) {
        if (initialized)
            getBodyTable().prepareColumn(column);
        else
            super.prepareColumn(column);
    }

    /** {@inheritDoc} */
    public String getHTML(int row, int column) {
        if (initialized)
            return getBodyTable().getHTML(row, column);
        else
            return super.getHTML(row, column);
    }

    /** {@inheritDoc} */
    public void setHTML(int row, int column, String html) {
        if (initialized)
            getBodyTable().setHTML(row, column, html);
        else
            super.setHTML(row, column, html);
    }

    /** {@inheritDoc} */
    public Widget getWidget(int row, int column) {
        if (initialized)
            return getBodyTable().getWidget(row, column);
        else
            return super.getWidget(row, column);
    }

    /**
     * Getter for property 'headerTable'.
     *
     * @return Value for property 'headerTable'.
     */
    protected AdvancedFlexTable getHeaderTable() {
        if (headerTable == null)
            headerTable = new AdvancedFlexTable();
        return headerTable;
    }

    /**
     * Getter for property 'bodyTable'.
     *
     * @return Value for property 'bodyTable'.
     */
    protected AdvancedFlexTable getBodyTable() {
        if (bodyTable == null)
            bodyTable = new AdvancedFlexTable();
        return bodyTable;
    }

    /**
     * Getter for property 'resizable'.
     *
     * @return Value for property 'resizable'.
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Sets the resizability of column flag.<p/>
     * This method also sets table-layout style.
     *
     * @param resizable resizability flag value.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        makeResizable(resizable);
    }

    /**
     * Enables or disables columns resizability.
     *
     * @param resizable a flag to enable or disable resizable columns.
     */
    protected void makeResizable(boolean resizable) {
        if (resizable){
            DOM.setStyleAttribute(getBodyTable().getElement(), "tableLayout", "fixed");
            DOM.setStyleAttribute(getBodyTable().getElement(), "width", "0");
            DOM.setStyleAttribute(getHeaderTable().getElement(), "tableLayout", "fixed");
            DOM.setStyleAttribute(getHeaderTable().getElement(), "width", "0");
        } else {
            DOM.setStyleAttribute(getBodyTable().getElement(), "tableLayout", "");
            DOM.setStyleAttribute(getHeaderTable().getElement(), "tableLayout", "");
        }
    }
}
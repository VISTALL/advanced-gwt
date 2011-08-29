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

package org.gwt.advanced.client.ui.widget;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLTable;
import org.gwt.advanced.client.ui.GridEventManager;
import org.gwt.advanced.client.ui.widget.cell.GridCell;

/**
 * This is a default implementation of the grid event manager.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.3.0
 */
public class DefaultGridEventManager implements GridEventManager {
    /** a grid panel */
    private GridPanel panel;
    /** selection modifier key code (Shift or Ctrl) */
    private int selectionModifier;
    /** registration of the handler responsible for keyboard events */
    private HandlerRegistration keyHandlerRegistration;

    /**
     * Creates an instance of the class and adds itself to the listeners list of the grid.
     *
     * @param panel is a grid panel.
     */
    public DefaultGridEventManager(GridPanel panel) {
        this.panel = panel;
    }

    /** {@inheritDoc} */
    public boolean dispatch(GridPanel panel, char keyCode, int modifiers) {
        int mainModifier = MODIFIER_ALT | MODIFIER_CTRL;

        if (modifiers == MODIFIER_CTRL || modifiers == MODIFIER_SHIFT)
            selectionModifier = modifiers;
        else
            selectionModifier = 0;

        if (KeyCodes.KEY_DOWN == keyCode) {
            moveCursorDown();
            return true;
        } else if (KeyCodes.KEY_RIGHT == keyCode) {
            moveCursorRight();
            return true;
        } else if (KeyCodes.KEY_UP == keyCode) {
            moveCursorUp();
            return true;
        } else if (KeyCodes.KEY_LEFT == keyCode) {
            moveCursorLeft();
            return true;
        } else if (KeyCodes.KEY_HOME == keyCode && modifiers == MODIFIER_SHIFT) {
            moveToFirstCell();
            return true;
        } else if (KeyCodes.KEY_END == keyCode && modifiers == MODIFIER_SHIFT) {
            moveToLastCell();
            return true;
        } else if (KeyCodes.KEY_HOME == keyCode) {
            moveToStartPage();
            return true;
        } else if (KeyCodes.KEY_END == keyCode) {
            moveToEndPage();
            return true;
        } else if (KeyCodes.KEY_PAGEUP == keyCode) {
            moveToPrevPage();
            return true;
        } else if (KeyCodes.KEY_PAGEDOWN == keyCode) {
            moveToNextPage();
            return true;
        } else if (keyCode == ' ' && modifiers == (MODIFIER_SHIFT | mainModifier)) {
            moveToPreviousCell();
            return true;
        } else if (keyCode == ' ' && modifiers == mainModifier) {
            moveToNextCell();
            return true;
        } else if (KeyCodes.KEY_ENTER == keyCode && !isReadOnly()) {
            activateCell();
            return true;
        } else if (KeyCodes.KEY_TAB == keyCode && modifiers == MODIFIER_SHIFT) {
            moveByShiftTab();
            return true;
        } else if (KeyCodes.KEY_TAB == keyCode) {
            moveByTab();
            return true;
        }
        return false;
    }

    /** Sets a position of the cursor */
    public void onFocus(FocusEvent focusEvent) {
        EditableGrid grid = getPanel().getGrid();
        int row = grid.getCurrentRow();
        int column = grid.getCurrentColumn();

        if (row != -1 && column != -1 && (!grid.isMultiRowModeEnabled() || getSelectionModifier() == 0))
            setCursor(row, column, false);

        if (keyHandlerRegistration == null)
            keyHandlerRegistration = Event.addNativePreviewHandler(this);
    }

    /**
     * Registers this class instance as a handler for native preview events.
     *
     * @param event is an original event that is never used.
     */
    @Override
    public void onBlur(BlurEvent event) {
    }

    /** Sets the current position of the cursor or activates the selected cell. */
    public void onClick(ClickEvent event) {
        EditableGrid grid = getPanel().getGrid();
        HTMLTable.Cell cellForEvent = grid.getCellForEvent(event);
        if (cellForEvent != null) {
            int row = cellForEvent.getRowIndex();
            int cell = cellForEvent.getCellIndex();
            if (row == grid.getCurrentRow() && cell == grid.getCurrentColumn()
                    && !grid.hasActiveCell() && getSelectionModifier() == 0)
                activateCell();
            else if (!grid.hasActiveCell())
                grid.setFocus(true);
            else if ((row != grid.getCurrentRow() || cell != grid.getCurrentColumn())
                    && grid.hasActiveCell() && getSelectionModifier() == 0)
                activateCell();

            setCursor(row, cell, false);
        }
    }

    /**
     * Handles key down and key up events to dispatch key combination.
     *
     * @param event is an event that happens somewhere in the grid.
     */
    @Override
    public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        NativeEvent nativeEvent = event.getNativeEvent();
        if (nativeEvent.getEventTarget() == null)
            return;
        Element target = (Element) Element.as(nativeEvent.getEventTarget());
        if (!DOM.isOrHasChild(getPanel().getElement(), target))
            return;

        if (event.getTypeInt() == Event.ONKEYDOWN) {
            int modifiers = 0;
            if (nativeEvent.getAltKey())
                modifiers |= MODIFIER_ALT;
            if (nativeEvent.getShiftKey())
                modifiers |= MODIFIER_SHIFT;
            if (nativeEvent.getCtrlKey())
                modifiers |= MODIFIER_CTRL;
            if (nativeEvent.getMetaKey())
                modifiers |= MODIFIER_META;

            if (dispatch(panel, (char) nativeEvent.getKeyCode(), modifiers)) {
                nativeEvent.preventDefault();
                nativeEvent.stopPropagation();
                event.cancel();
            }
        } else if (event.getTypeInt() == Event.ONKEYUP) {
            selectionModifier = 0;
        }

        if (!event.isCanceled())
            event.consume();
    }

    /** Moves the cursor to the next cell */
    protected void moveToNextCell() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;

        if (grid.getCurrentColumn() < grid.getCellCount(grid.getCurrentRow()) - 1)
            moveCursorRight();
        else
            setCursor(grid.getCurrentRow() + 1, 0, false);
    }

    /** Moves the cursor to the previous cell */
    protected void moveToPreviousCell() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;

        if (grid.getCurrentColumn() > 0)
            moveCursorLeft();
        else
            setCursor(grid.getCurrentRow() - 1, grid.getCellCount(grid.getCurrentRow()) - 1, false);
    }

    /** Moves the cursor right */
    protected void moveCursorRight() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        setCursor(grid.getCurrentRow(), grid.getCurrentColumn() + 1, false);
    }

    /** Moves the cursor down */
    protected void moveCursorDown() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        setCursor(grid.getCurrentRow() + 1, grid.getCurrentColumn(), false);
    }

    /** Moves the cursor left */
    protected void moveCursorLeft() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        setCursor(grid.getCurrentRow(), grid.getCurrentColumn() - 1, false);
    }

    /** Moves the cursor up */
    protected void moveCursorUp() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        setCursor(grid.getCurrentRow() - 1, grid.getCurrentColumn(), false);
    }

    /** Opens the first page of the grid */
    protected void moveToStartPage() {
        if(!deactivateActiveCell())
            return;
        setPage(0);
    }

    /** Opens the last page of the grid */
    protected void moveToEndPage() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        int page = grid.getModel().getTotalPagesNumber() - 1;
        setPage(page);
    }

    /** Moves the cursor to the first cell on this page */
    protected void moveToFirstCell() {
        if(!deactivateActiveCell())
            return;
        setCursor(0, 0, false);
    }

    /** Moves the cursor to the last cell on this page */
    protected void moveToLastCell() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        int row = grid.getRowCount() - 1;
        if (row < 0)
            return;
        int column = grid.getCellCount(row) - 1;
        if (column < 0)
            return;
        setCursor(row, column, false);
    }

    /** Open the next page */
    protected void moveToNextPage() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        int page = grid.getModel().getCurrentPageNumber();
        setPage(page + 1);
    }

    /** Opens the previous page */
    protected void moveToPrevPage() {
        EditableGrid grid = getPanel().getGrid();
        if(!deactivateActiveCell())
            return;
        int page = grid.getModel().getCurrentPageNumber();
        setPage(page - 1);
    }

    /**
     * This method activates an active cell if there is one and if this cell allows
     * centralized deactivation by keyboard.<p/>
     * Invoke this method only if you handle keyboard events. Otherwise invoke {@link #activateCell()}.
     *
     * @return <code>true</code> if cell is deactivated.
     */
    protected boolean deactivateActiveCell() {
        EditableGrid grid = getPanel().getGrid();
        if (grid.hasActiveCell() && grid.getActiveCell().canBeDeactivated()) {
            activateCell();
            return true;
        }
        return !grid.hasActiveCell();
    }

    /**
     * Moves a cursor to the next right cell or to the next line if the current cell is last in the row.<p/>
     * If there are no cells on this page switches to the next page.
     */
    protected void moveByTab() {
        EditableGrid grid = getPanel().getGrid();
        if (grid.hasActiveCell())
            activateCell();

        if (grid.getCurrentColumn() < grid.getCellCount(grid.getCurrentRow()) - 1) {
            setCursor(grid.getCurrentRow(), grid.getCurrentColumn() + 1, false);
        } else if (grid.getCurrentRow() < grid.getRowCount() - 1) {
            setCursor(grid.getCurrentRow() + 1, 0, false);
        } else {
            int pageNumber = grid.getModel().getCurrentPageNumber();
            setPage(pageNumber + 1);
            if (pageNumber != grid.getModel().getCurrentPageNumber())
                setCursor(0, 0, false);
        }
    }

    /**
     * Moves a cursor to the next left cell or to the previous line if the current cell is the first in the row.<p/>
     * If there are no cells on this page switches to the previous page.
     */
    protected void moveByShiftTab() {
        EditableGrid grid = getPanel().getGrid();
        if (grid.hasActiveCell())
            activateCell();

        if (grid.getCurrentColumn() > 0) {
            setCursor(grid.getCurrentRow(), grid.getCurrentColumn() - 1, true);
        } else if (grid.getCurrentRow() > 0) {
            setCursor(grid.getCurrentRow() - 1, grid.getCellCount(grid.getCurrentRow() - 1) - 1, true);
        } else {
            int pageNumber = grid.getModel().getCurrentPageNumber();
            setPage(pageNumber - 1);
            if (pageNumber != grid.getModel().getCurrentPageNumber())
                setCursor(grid.getRowCount() - 1, grid.getCellCount(grid.getRowCount() - 1) - 1, true);
        }
    }

    /** Activates the currently selected cell */
    protected void activateCell() {
        EditableGrid grid = getPanel().getGrid();
        boolean active = !grid.hasActiveCell();
        int row = grid.getCurrentRow();
        int column = grid.getCurrentColumn();

        GridCell gridCell = (GridCell) grid.getWidget(row, column);
        boolean doAction;
        if (active)
            doAction = grid.fireStartEdit(gridCell);
        else
            doAction = grid.fireFinishEdit(gridCell, gridCell.getNewValue());

        if (doAction) {
            grid.activateCell(row, column, active);
            if (!active)
                grid.setFocus(true);
        } else if (!active) {
            gridCell.setFocus(true);
        }
    }

    /**
     * Checks whether the current cell is read only.
     *
     * @return <code>true</code> if it's read only.
     */
    protected boolean isReadOnly() {
        EditableGrid grid = getPanel().getGrid();
        int column = grid.getCurrentColumn();
        return grid.isReadOnly(column);
    }

    /**
     * Setter for property 'page'.
     *
     * @param page Value to set for property 'page'.
     */
    protected void setPage(int page) {
        if (page >= 0) {
            int row = getPanel().getGrid().getCurrentRow();
            int column = getPanel().getGrid().getCurrentColumn();
            Pager pager = getPanel().getTopPager();
            pager.setCurrentPageNumber(page);
            getPanel().getMediator().firePageChangeEvent(pager, page);
            setCursor(row, column, false);
        }
    }

    /**
     * This method sets the current cell value.<p/>
     * It takes into account whether the Shift or Ctrl modifier keys are pressed.
     *
     * @param row           is a row number.
     * @param cell          is a column number.
     * @param skipSelection is a flag value that forces selection skipping. Otherwise rows might be selected due to other
     *                      conditions.
     */
    protected void setCursor(int row, int cell, boolean skipSelection) {
        EditableGrid grid = getPanel().getGrid();
        if (getSelectionModifier() == 0 || !grid.isMultiRowModeEnabled() || skipSelection)
            grid.setCurrentCell(row, cell);
        else if (getSelectionModifier() == MODIFIER_CTRL) {
            if (!grid.isSelected(row))
                grid.selectRow(row);
            else
                grid.deselectCell(row, cell);
        } else if (getSelectionModifier() == MODIFIER_SHIFT) {
            grid.selectRows(row);
        }
    }

    /**
     * Gets a grid panel instance.
     *
     * @return a grid panel.
     */
    protected GridPanel getPanel() {
        return panel;
    }

    /**
     * Gets a key code of selection modifier pressed by a user.<p/>
     * Usually Shift or Ctrl.
     *
     * @return a selection modifier key code.
     */
    public int getSelectionModifier() {
        return selectionModifier;
    }
}

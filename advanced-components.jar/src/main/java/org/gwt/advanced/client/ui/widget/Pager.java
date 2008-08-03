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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.datamodel.Pageable;
import org.gwt.advanced.client.ui.AdvancedWidget;
import org.gwt.advanced.client.ui.PagerListener;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is pager widget implementation.<p>
 * It can be used not only by the grid, but also by other widgets where paging feature is required.
 * These widgets must implement the {@link PagerListener} interface.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class Pager extends SimplePanel implements AdvancedWidget {
    /** integer number pattern */
    protected static final String INTEGER_PATTERN = "([+-]?[0-9]+)|([0-9]*)";
    /** this flag defines whether the arrows must be displayed */
    private boolean arrowsVisible = true;
    /** pager table */
    private FlexTable table = new FlexTable();
    /** pageable data model */
    private Pageable model;
    /** left arrow click listener */
    private ClickListener leftClickListener;
    /** right arrow click listener */
    private ClickListener rightClickListener;
    /** left arrow image */
    private Image left = new Image();
    /** right arrow image */
    private Image right = new Image();
    /** a grid panel */
    private GridPanel gridPanel;
    /** a page number box */
    private TextBox pageNumber;
    /** submit button image */
    private Image submit;
    /** a flag that means whether the page number box must be displayed */
    private boolean pageNumberBoxDisplayed;

    /**
     * Creates an instance of this class and initialie core elements.
     */
    public Pager () {
        add(table);
    }

    /**
     * Getter for property 'model'.
     *
     * @return Value for property 'model'.
     */
    public Pageable getModel () {
        return model;
    }

    /**
     * Setter for property 'model'.
     *
     * @param model Value to set for property 'model'.
     */
    public void setModel (Pageable model) {
        this.model = model;
    }

    /**
     * Getter for property 'arrowsVisible'.
     *
     * @return Value for property 'arrowsVisible'.
     */
    public boolean isArrowsVisible () {
        return arrowsVisible;
    }

    /**
     * Setter for property 'arrowsVisible'.
     *
     * @param arrowsVisible Value to set for property 'arrowsVisible'.
     */
    public void setArrowsVisible (boolean arrowsVisible) {
        this.arrowsVisible = arrowsVisible;
    }

    /**                        
     * Invoke this method to displayActive the pager.
     */
    public void display() {
        setStyleName("advanced-Pager");

        if (getModel() != null) {
            if (table.getRowCount() > 0) {
                while(table.getCellCount(0) > 0) {
                    table.getColumnFormatter().setWidth(table.getCellCount(0) - 1, "1%");
                    table.removeCell(0, table.getCellCount(0) - 1);
                }
            }

            addArrows();
            addLinks();

            if (isPageNumberBoxDisplayed()) {
                int column = table.getCellCount(0);
                table.setWidget(0, column, getPageNumber());
                getPageNumber().setText(String.valueOf(getModel().getCurrentPageNumber() + 1));
                column = table.getCellCount(0);
                table.setWidget(0, column, getSubmit());
            }

            int column = table.getCellCount(0);
            table.setText(0, column, "");
            table.getColumnFormatter().setWidth(column, "100%");
        }
    }

    /**
     * Getter for property 'gridPanel'.
     *
     * @return Value for property 'gridPanel'.
     */
    public GridPanel getGridPanel () {
        if (gridPanel == null)
            gridPanel = new GridPanel();
        return gridPanel;
    }

    /**
     * Setter for property 'gridPanel'.
     *
     * @param gridPanel Value to set for property 'gridPanel'.
     */
    public void setGridPanel (GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    /**
     * Getter for property 'pageNumberBoxDisplayed'.
     *
     * @return Value for property 'pageNumberBoxDisplayed'.
     */
    public boolean isPageNumberBoxDisplayed() {
        return pageNumberBoxDisplayed;
    }

    /**
     * Setter for property 'pageNumberBoxDisplayed'.
     *
     * @param pageNumberBoxDisplayed Value to set for property 'pageNumberBoxDisplayed'.
     */
    public void setPageNumberBoxDisplayed(boolean pageNumberBoxDisplayed) {
        this.pageNumberBoxDisplayed = pageNumberBoxDisplayed;
    }

    /**
     * Getter for property 'pageNumber'.
     *
     * @return Value for property 'pageNumber'.
     */
    protected TextBox getPageNumber() {
        if (pageNumber == null) {
            pageNumber = new TextBox();
            pageNumber.addFocusListener(new PageBoxListener());
        }
        return pageNumber;
    }

    /**
     * Getter for property 'submit'.
     *
     * @return Value for property 'submit'.
     */
    public Image getSubmit() {
        if (submit == null) {
            submit = new Image();
            DOM.setElementAttribute(
                submit.getElement(), "src",
                ThemeHelper.getInstance().getFullImageName("arrow_right_16.gif")
            );
            final Pager pager = this;
            submit.addClickListener(new ClickListener(){
                public void onClick(Widget sender) {
                    setCurrentPageNumber(Integer.valueOf(getPageNumber().getText()).intValue() - 1);
                    getGridPanel().getMediator().firePageChangeEvent(pager, getModel().getCurrentPageNumber());
                }
            });
        }
        return submit;
    }

    /**
     * Setter for property 'currentPageNumber'.
     *
     * @param page Value to set for property 'currentPageNumber'.
     */
    protected void setCurrentPageNumber(int page) {
        Pageable pageable = getModel();
        if (page < pageable.getTotalPagesNumber())
            pageable.setCurrentPageNumber(page);
        if (isPageNumberBoxDisplayed())
            getPageNumber().setText(String.valueOf(page + 1));
        display();
    }

    /**
     * Getter for property 'leftClickListener'.
     *
     * @return Value for property 'leftClickListener'.
     */
    protected ClickListener getLeftClickListener() {
        if (leftClickListener == null) {
            final Pager pager = this;
            leftClickListener = new ClickListener() {
                public void onClick (Widget sender) {
                    Pageable pageable = getModel();

                    int startPage = pageable.getStartPage() - pageable.getDisplayedPages();
                    if (startPage < 0) {
                        startPage = 0;
                    }

                    setCurrentPageNumber(startPage);
                    getGridPanel().getMediator().firePageChangeEvent(pager, startPage);
                }
            };
        }

        return leftClickListener;
    }

    /**
     * Getter for property 'rightClickListener'.
     *
     * @return Value for property 'rightClickListener'.
     */
    protected ClickListener getRightClickListener() {
        if (rightClickListener == null) {
            final Pager pager = this;
            rightClickListener = new ClickListener() {
                public void onClick (Widget sender) {
                    Pageable pageable = getModel();

                    int startPage = pageable.getStartPage() + pageable.getDisplayedPages();
                    if (startPage >= pageable.getTotalPagesNumber()) {
                        startPage = (pageable.getStartPage() - 1) * pageable.getPageSize() + 1;
                        if (startPage < 0)
                            startPage = 0;
                    }

                    setCurrentPageNumber(startPage);
                    getGridPanel().getMediator().firePageChangeEvent(pager, startPage);
                }
            };
        }

        return rightClickListener;
    }

    /**
     * This method adds page links into the pager panel.
     */
    protected void addLinks () {
        final Pageable pageable = getModel();
        int count = 1;
        final Pager pager = this;
        for (int i = pageable.getStartPage(); i <= pageable.getEndPage(); i++) {
            if (pageable.getCurrentPageNumber() != i) {
                Hyperlink hyperlink = new Hyperlink(String.valueOf(i + 1), "");

                final int page = i;
                hyperlink.addClickListener(new ClickListener() {
                    public void onClick (Widget sender) {
                        setCurrentPageNumber(page);
                        getGridPanel().getMediator().firePageChangeEvent(pager, page);
                    }
                });

                table.setWidget(0, count++, hyperlink);
            } else {
                table.setText(0, count++, String.valueOf(i + 1));
            }
        }
    }

    /**
     * This method adds arrows into the pager panel.
     */
    protected void addArrows() {
        Pageable pageable = getModel();
        int pages = pageable.getEndPage() - pageable.getStartPage() + 2;

        table.setText(0, 0, "");
        table.setText(0, pages, "");

        if (isArrowsVisible()) {
            table.setWidget(0, 0, left);
            table.setWidget(0, pages, right);

            left.removeClickListener(getLeftClickListener());
            if (pageable.getCurrentPageNumber() >= pageable.getDisplayedPages()) {
                DOM.setElementAttribute(
                    left.getElement(), "src",
                    ThemeHelper.getInstance().getFullImageName("arrow_left_16.gif")
                );
                left.addClickListener(getLeftClickListener());
            } else {
                DOM.setElementAttribute(
                    left.getElement(), "src",
                    ThemeHelper.getInstance().getFullImageName("arrow_left_16_d.gif")
                );
            }

            right.removeClickListener(getRightClickListener());
            if (pageable.getStartPage() + pageable.getDisplayedPages() <= pageable.getTotalPagesNumber()) {
                DOM.setElementAttribute(
                    right.getElement(), "src",
                    ThemeHelper.getInstance().getFullImageName("arrow_right_16.gif")
                );
                right.addClickListener(getRightClickListener());
            } else {
                DOM.setElementAttribute(
                    right.getElement(), "src",
                    ThemeHelper.getInstance().getFullImageName("arrow_right_16_d.gif")
                );
            }
        }
    }

    /**
     * This listener restores the previous value in the text box if the new one doesn't match restrictions required
     * for the page number values.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class PageBoxListener implements FocusListener {
        /** previous page number */
        private int previousValue;

        /** {@inheritDoc} */
        public void onFocus(Widget sender) {
            TextBox box = (TextBox) sender;
            String text = box.getText();

            if (text != null && text.length() > 0)
                this.previousValue = Integer.valueOf(text).intValue();
            else
                this.previousValue = 1;    
        }

        /** {@inheritDoc} */
        public void onLostFocus(Widget sender) {
            TextBox box = (TextBox) sender;
            String text = box.getText();

            if (
                text != null
                && (!text.matches(INTEGER_PATTERN)
                   || Integer.valueOf(text).intValue() < 1
                   || Integer.valueOf(text).intValue() > getModel().getTotalPagesNumber())
            ) {
                box.setText(String.valueOf(this.previousValue));
            }
        }
    }
}

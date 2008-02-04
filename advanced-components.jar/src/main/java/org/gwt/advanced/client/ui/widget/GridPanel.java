package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.Hierarchical;
import org.gwt.advanced.client.ui.AdvancedWidget;
import org.gwt.advanced.client.ui.GridToolbarListener;
import org.gwt.advanced.client.ui.PagerListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a grid panel widget.<p>
 * It also helps to displayActive an editable grid with pagers and toolbars. Use it to construct the grid
 * and displayActive it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class GridPanel extends DockPanel implements AdvancedWidget {
    /** an editable grid */
    private EditableGrid grid;
    /** a top pager */
    private Pager topPager;
    /** a bottom pager */
    private Pager bottomPager;
    /** a bottom toolbar */
    private GridToolbar bottomToolbar;
    /** a top toolbar */
    private GridToolbar topToolbar;
    /** a top panel */
    private Panel topPanel;
    /** a bottom panel */
    private Panel bottomPanel;
    /** top pager visibility flag */
    private boolean topPagerVisible = true;
    /** bottom pager visibility flag */
    private boolean bottomPagerVisible = true;
    /** top toolbar visibility flag */
    private boolean topToolbarVisible = true;
    /** bottom toolbar visibility flag */
    private boolean bottomToolbarVisible = false;
    /** pager arrows visibility flag */
    private boolean arrowsVisible = true;
    /** toolbar listeners */
    private List toolbarListeners = new ArrayList();
    /** pager listeners */
    private List pagerListeners = new ArrayList();
    /** locking panel widget */
    private LockingPanel lockingPanel;
    /** a mediator to handle all component events inside the panel */
    private EventMediator mediator;
    /** a parent grid panel */
    private GridPanel parentGridPanel;
    /** a list of child grid panels */
    private List children;

    /** Constructs a new GridPanel. */
    public GridPanel () {
    }

    /**
     * Creates and adds a new editable grid into the panel.<p>
     * Use this method before {@link #display()}.
     *
     * @param headers is a list of heder labels.
     * @param columnWidgetClasses is a list of column widget classes.
     * @param model is a data model to be applied in the grid.
     *
     * @return a newly created grid.
     */
    public EditableGrid createEditableGrid(String[] headers, Class[] columnWidgetClasses, Editable model) {
        EditableGrid grid;
        if (model instanceof Hierarchical)
            grid = new HierarchicalGrid(headers, columnWidgetClasses);
        else
            grid = new EditableGrid(headers, columnWidgetClasses);
        grid.setGridPanel(this);
        grid.setModel(model);
        setGrid(grid);
        return grid;
    }

    /**
     * Makes the specified column to be readonly.<p>
     * Use this method before {@link #display()}.
     *
     * @param column is a column number.
     * @param readonly is a readonly attribute value.
     */
    public void setReadonlyColumn(int column, boolean readonly) {
        EditableGrid grid = getGrid();
        if (grid != null) {
            grid.setReadOnly(column, readonly);
        }
    }

    /**
     * Makes the specified column to be sortable.<p>
     * Use this method before {@link #display()}.
     *
     * @param column is a column number.
     * @param sortable is a sortable attribute value.
     */
    public void setSortableColumn(int column, boolean sortable) {
        EditableGrid grid = getGrid();
        if (grid != null) {
            grid.setSortable(column, sortable);
        }
    }

    /**
     * This method sets the specified column
     *
     * @param column is a column number.
     * @param invisible is a flag to switch the column to invisible state.
     */
    public void setInvisibleColumn(int column, boolean invisible) {
        EditableGrid grid = getGrid();
        if (grid == null)
            return;

        if (invisible)
            grid.addInvisibleColumn(column);
        else
            grid.removeInvisibleColumn(column);
    }

    /**
     * Setter for property 'arrowsVisible'.
     *
     * @param visible Value to set for property 'arrowsVisible'.
     */
    public void setArrowsVisible(boolean visible) {
        this.arrowsVisible = visible;
    }

    /**
     * This method displays the panel and adds nexted widgets.
     */
    public void display() {
        if (getGrid() == null)
            throw new IllegalStateException("Grid hasn't been initialized");

        setStyleName("advanced-GridPanel");
        remove(getGrid());
        remove(getTopPanel());
        remove(getBottomPanel());

        getMediator().addPagerListener(getMediator());
        getMediator().addToolbarListener(getMediator());
        getMediator().addGridListener(getMediator());

        for (Iterator iterator = getPagerListeners().iterator(); iterator.hasNext();) {
            PagerListener listener = (PagerListener) iterator.next();
            getMediator().addPagerListener(listener);
        }

        for (Iterator iterator = getToolbarListeners().iterator(); iterator.hasNext();) {
            GridToolbarListener toolbarListener = (GridToolbarListener) iterator.next();
            getMediator().addToolbarListener(toolbarListener);
        }
        
        packTopPanel();
        packGrid();
        packBottomPanel();
    }

    /**
     * This method locks the widget and refuses any user activities.
     */
    public void lock() {
        getGrid().setLocked(true);
        getLockingPanel().lock();
    }

    /**
     * This method unlocks the widget.
     */
    public void unlock() {
        getGrid().getModel().clearRemovedRows();
        display();
        getLockingPanel().unlock();
        getGrid().setLocked(false);
    }

    /**
     * Getter for property 'mediator'.
     *
     * @return Value for property 'mediator'.
     */
    public EventMediator getMediator () {
        if (mediator == null)
            mediator = new EventMediator(this);
        return mediator;
    }
    
    /**
     * Getter for property 'topPagerVisible'.
     *
     * @return Value for property 'topPagerVisible'.
     */
    public boolean isTopPagerVisible() {
        return topPagerVisible;
    }

    /**
     * Setter for property 'topPagerVisible'.
     *
     * @param topPagerVisible Value to set for property 'topPagerVisible'.
     */
    public void setTopPagerVisible(boolean topPagerVisible) {
        this.topPagerVisible = topPagerVisible;
    }

    /**
     * Getter for property 'bottomPagerVisible'.
     *
     * @return Value for property 'bottomPagerVisible'.
     */
    public boolean isBottomPagerVisible() {
        return bottomPagerVisible;
    }

    /**
     * Setter for property 'bottomPagerVisible'.
     *
     * @param bottomPagerVisible Value to set for property 'bottomPagerVisible'.
     */
    public void setBottomPagerVisible(boolean bottomPagerVisible) {
        this.bottomPagerVisible = bottomPagerVisible;
    }

    /**
     * Getter for property 'topToolbarVisible'.
     *
     * @return Value for property 'topToolbarVisible'.
     */
    public boolean isTopToolbarVisible() {
        return topToolbarVisible;
    }

    /**
     * Setter for property 'topToolbarVisible'.
     *
     * @param topToolbarVisible Value to set for property 'topToolbarVisible'.
     */
    public void setTopToolbarVisible(boolean topToolbarVisible) {
        this.topToolbarVisible = topToolbarVisible;
    }

    /**
     * Getter for property 'bottomToolbarVisible'.
     *
     * @return Value for property 'bottomToolbarVisible'.
     */
    public boolean isBottomToolbarVisible() {
        return bottomToolbarVisible;
    }

    /**
     * Setter for property 'bottomToolbarVisible'.
     *
     * @param bottomToolbarVisible Value to set for property 'bottomToolbarVisible'.
     */
    public void setBottomToolbarVisible(boolean bottomToolbarVisible) {
        this.bottomToolbarVisible = bottomToolbarVisible;
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
     * Getter for property 'grid'.
     *
     * @return Value for property 'grid'.
     */
    public EditableGrid getGrid() {
        return grid;
    }

    /**
     * Getter for property 'topPager'.
     *
     * @return Value for property 'topPager'.
     */
    public Pager getTopPager () {
        if (topPager == null)
            setTopPager(new Pager());
        return topPager;
    }

    /**
     * Getter for property 'topToolbar'.
     *
     * @return Value for property 'topToolbar'.
     */
    public GridToolbar getTopToolbar () {
        if (topToolbar == null)
           setTopToolbar(new GridToolbar());
        return topToolbar;
    }

    /**
     * Getter for property 'bottomPager'.
     *
     * @return Value for property 'bottomPager'.
     */
    public Pager getBottomPager () {
        if (bottomPager == null)
            setBottomPager(new Pager());
        return bottomPager;
    }

    /**
     * Getter for property 'bottomToolbar'.
     *
     * @return Value for property 'bottomToolbar'.
     */
    public GridToolbar getBottomToolbar () {
        if (bottomToolbar == null)
            setBottomToolbar(new GridToolbar());
        return bottomToolbar;
    }

    /**
     * Getter for property 'topPanel'.
     *
     * @return Value for property 'topPanel'.
     */
    public Panel getTopPanel() {
        if (topPanel == null) {
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.setStyleName("top-panel");
            setTopPanel(verticalPanel);
        }

        return topPanel;
    }

    /**
     * Getter for property 'bottomPanel'.
     *
     * @return Value for property 'bottomPanel'.
     */
    public Panel getBottomPanel() {
        if (bottomPanel == null) {
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.setStyleName("bottom-panel");
            setBottomPanel(verticalPanel);
        }

        return bottomPanel;
    }

    /**
     * Setter for property 'parent'.
     *
     * @param panel Value to set for property 'parent'.
     */
    public void setParent(GridPanel panel) {
        if (this.parentGridPanel != null)
            this.parentGridPanel.removeChildGridPanel(this);

        this.parentGridPanel = panel;
        if (panel != null)
            panel.addChildGridPanel(this);
    }

    /**
     * This method displays a top panel and adds top pager and toolbar into it.
     */
    protected void packTopPanel() {
        if (isTopPagerVisible() || isTopToolbarVisible()) {
            Panel panel = getTopPanel();
            add(panel, NORTH);

            if (isTopPagerVisible())
                preparePager(getTopPager(), panel).display();
            if (isTopToolbarVisible())
                prepareToolbar(getTopToolbar(), panel).display();
        }
    }

    /**
     * This method adds a grid into the panel.
     */
    protected void packGrid() {
        EditableGrid grid = getGrid();
        add(grid, CENTER);
        grid.display();
    }

    /**
     * This method displays a bottom panel and adds a bottom pager and toolbar into it.
     */
    protected void packBottomPanel() {
        if (isBottomPagerVisible() || isBottomToolbarVisible()) {
            Panel panel = getBottomPanel();
            add(panel, SOUTH);

            if (isBottomPagerVisible())
                preparePager(getBottomPager(), panel).display();
            if (isBottomToolbarVisible())
                prepareToolbar(getBottomToolbar(), panel).display();
        }
    }

    /**
     * This method initializes a toolbar with common values.
     *
     * @param toolbar is a toolbar to be initialized.
     * @param panel is a top or bottom panel.
     *
     * @return an initialized toolbar.
     */
    protected GridToolbar prepareToolbar (GridToolbar toolbar, Panel panel) {
        toolbar.setGridPanel(this);
        panel.add(toolbar);
        return toolbar;
    }

    /**
     * This method initializes a pager with common values.
     *
     * @param pager is a pager to be initialized.
     * @param panel is a top or bottom panel.
     *
     * @return an initialized toolbar.
     */
    protected Pager preparePager (Pager pager, Panel panel) {
        EditableGrid grid = getGrid();

        Editable model = grid.getModel();
        pager.setGridPanel(this);
        pager.setModel(model);
        pager.setArrowsVisible(isArrowsVisible());

        panel.add(pager);
        return pager;
    }

    /**
     * Setter for property 'bottomPanel'.
     *
     * @param bottomPanel Value to set for property 'bottomPanel'.
     */
    protected void setBottomPanel(Panel bottomPanel) {
        this.bottomPanel = bottomPanel;
    }

    /**
     * Setter for property 'topPanel'.
     *
     * @param topPanel Value to set for property 'topPanel'.
     */
    protected void setTopPanel(Panel topPanel) {
        this.topPanel = topPanel;
    }

    /**
     * Setter for property 'topPager'.
     *
     * @param topPager Value to set for property 'topPager'.
     */
    protected void setTopPager (Pager topPager) {
        this.topPager = topPager;
    }

    /**
     * Setter for property 'grid'.
     *
     * @param grid Value to set for property 'grid'.
     */
    protected void setGrid(EditableGrid grid) {
        this.grid = grid;
    }

    /**
     * Setter for property 'topToolbar'.
     *
     * @param topToolbar Value to set for property 'topToolbar'.
     */
    protected void setTopToolbar (GridToolbar topToolbar) {
        this.topToolbar = topToolbar;
    }

    /**
     * Setter for property 'bottomPager'.
     *
     * @param bottomPager Value to set for property 'bottomPager'.
     */
    protected void setBottomPager (Pager bottomPager) {
        this.bottomPager = bottomPager;
    }

    /**
     * Setter for property 'bottomToolbar'.
     *
     * @param bottomToolbar Value to set for property 'bottomToolbar'.
     */
    protected void setBottomToolbar (GridToolbar bottomToolbar) {
        this.bottomToolbar = bottomToolbar;
    }

    /**
     * Getter for property 'toolbarListeners'.
     *
     * @return Value for property 'toolbarListeners'.
     */
    protected List getToolbarListeners () {
        return toolbarListeners;
    }

    /**
     * Getter for property 'pagerListeners'.
     *
     * @return Value for property 'pagerListeners'.
     */
    protected List getPagerListeners () {
        return pagerListeners;
    }

    /**
     * This method adds a toolbar listener to the toolbars.
     *
     * @param gridToolbarListener is a toolbar listener.
     */
    public void addToolbarListener (GridToolbarListener gridToolbarListener) {
        getToolbarListeners().add(gridToolbarListener);
    }

    /**
     * This method adds a new pager listener.
     *
     * @param pagerListener is a pager listener.
     */
    public void addPagerListener(PagerListener pagerListener) {
        getPagerListeners().add(pagerListener);
    }

    /**
     * Getter for property 'lockingPanel'.
     *
     * @return Value for property 'lockingPanel'.
     */
    protected LockingPanel getLockingPanel() {
        if (lockingPanel == null)
            lockingPanel = new LockingPanel();
        return lockingPanel;
    }

    /**
     * Getter for property 'parentGridPanel'.
     *
     * @return Value for property 'parentGridPanel'.
     */
    protected GridPanel getParentGridPanel() {
        return parentGridPanel;
    }


    /**
     * This method returns a list of child grid panels.
     *
     * @return a list of grid panels.
     */
    public List getChildGridPanels() {
        if (children == null)
            children = new ArrayList();
        return children;
    }

    /**
     * This method adds a child grid panel.
     *
     * @param child is a child panel.
     */
    protected void addChildGridPanel(GridPanel child) {
        removeChildGridPanel(child);
        getGrid().addSelectRowListener(getMediator());
        getChildGridPanels().add(child);
    }

    /**
     * This method removes a child grid panel.
     *
     * @param child a child grid panel.
     */
    protected void removeChildGridPanel(GridPanel child) {
        getGrid().removeSelectRowListener(getMediator());
        getChildGridPanels().remove(child);
    }
}

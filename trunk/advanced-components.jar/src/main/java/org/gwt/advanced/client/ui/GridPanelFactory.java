package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.widget.GridPanel;

/**
 * This interface defines methods of the grid panel factory.<p>
 * Implementations of this interface are used in hierarchical grids to create expandable
 * subgrids.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface GridPanelFactory {
    /**
     * Creates an instance of the grid panel and initializes it.
     *
     * @param model is a model instance.
     * @return a newly created grid panel.
     */
    GridPanel create(GridDataModel model);

    /**
     * This method creates a data model.<p>
     * It's used by the hiearachical grid if there is no data model defined for the cell 
     * in the parent data model.
     *
     * @param parentRow is a parent model row.
     * @param parentModel is a parent model instance.
     *  
     * @return a data model.
     */
    GridDataModel create(int parentRow, GridDataModel parentModel);
}

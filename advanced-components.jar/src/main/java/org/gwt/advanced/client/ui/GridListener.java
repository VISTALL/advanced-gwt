package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.ui.widget.cell.HeaderCell;

/**
 * This interface describes grid events listeners.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface GridListener {
    /**
     * This method is invoked on column sort event.
     *
     * @param cell is a header cell widget.
     * @param dataModel is a data model to be updated.
     */
    void onSort (HeaderCell cell, GridDataModel dataModel);

    /**
     * This method in invoked on save opeartion.<p>
     * Listeners should persist changes because as soon as this method in invoked the
     * change hsitory will be destroyed.
     *
     * @param dataModel is a data model.
     */
    void onSave (GridDataModel dataModel);

    /**
     * This method is invoked on grid cler operation.<p>
     * Listeners don't have to persist changes since the history will be keeped.
     * In case if you decide persist changes, don't forget to clear the history manually.
     *
     * @param dataModel is a data model.
     */
    void onClear (GridDataModel dataModel);
}

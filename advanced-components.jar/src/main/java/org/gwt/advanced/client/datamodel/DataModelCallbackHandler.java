package org.gwt.advanced.client.datamodel;

/**
 * This is a callback handler interface.<p>
 * It describes the method to be invoked by the data model during different user activities.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface DataModelCallbackHandler {
    /**
     * This method should synchronize the data saved in the model with the persistent storage
     * repository.<p>
     * NOTE: you must take into account the fact that other grid controls can rise other evets during
     * synchronization. It's better to lock them and unlock when synchronization is finished. Otherwise
     * viewed content may be incorrect.<br/>
     * To do lock / unlock operations, use {@link org.gwt.advanced.client.ui.widget.GridPanel#lock()} and
     * {@link org.gwt.advanced.client.ui.widget.GridPanel#unlock()} methods accordingly. Note, the unlock
     * method also redraws the panel.
     *
     * @param model is a model to be used for additional parameters.
     */
    void synchronize(GridDataModel model);
}

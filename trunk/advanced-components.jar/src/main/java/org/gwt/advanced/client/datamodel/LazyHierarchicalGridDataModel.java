package org.gwt.advanced.client.datamodel;

/**
 * This is a lazy loadable hierarchical data model.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class LazyHierarchicalGridDataModel extends HierarchicalGridDataModel implements LazyLoadable {
    /**
     * Initializes the model with the preloaded piece of data set.
     *
     * @param data is a data set piece.
     */
    public LazyHierarchicalGridDataModel (Object[][] data) {
        super((Object[][])null);
        setDelegate(new LazyGridDataModel(data));
    }

    /**
     * Creates a new instnace of this class and defines the handler.
     *
     * @param handler is a callback handler to be invoked on changes.
     */
    public LazyHierarchicalGridDataModel (DataModelCallbackHandler handler) {
        super((Object[][])null);
        setDelegate(new LazyGridDataModel(handler));
    }

    /** {@inheritDoc} */
    public DataModelCallbackHandler getHandler () {
        return getDelegate().getHandler();
    }

    /** {@inheritDoc} */
    public void update (Object[][] data) {
        getDelegate().update(data);
    }

    /** {@inheritDoc} */
    public void setTotalRowCount (int totalRowCount) {
        ((LazyLoadable)getDelegate()).setTotalRowCount(totalRowCount);
    }

    /** {@inheritDoc} */
    public void setHandler (DataModelCallbackHandler handler) {
        getDelegate().setHandler(handler);
    }
}

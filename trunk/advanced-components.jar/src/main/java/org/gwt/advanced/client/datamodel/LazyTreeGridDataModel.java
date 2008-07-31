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

package org.gwt.advanced.client.datamodel;

/**
 * This class implements lazy loadable tree grid data model.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.0
 */
public class LazyTreeGridDataModel extends TreeGridDataModel implements LazyLoadableComposite {
    /**
     * Creates an instance of this class and initializes data putting into lazy loadable delegate.
     *
     * @param data is a data set to put.
     */
    public LazyTreeGridDataModel(Object[][] data) {
        super(data);
        setDelegate(new DelegateLazyGridDataModel(data));
    }

    /**
     * Creates an inistanec of this class and synchronizes data using the handler.
     *
     * @param handler is a handler to be used for synchronization.
     */
    public LazyTreeGridDataModel(TreeDataModelCallbackHandler handler) {
        super(handler);
        setDelegate(new DelegateLazyGridDataModel(handler));
    }

    /** {@inheritDoc} */
    public int getTotalRowCount(TreeGridRow parent) {
        if (parent == null)
            return getDelegate().getTotalRowCount();
        return ((LazyTreeGridRow) parent).getTotalRowCount();
    }

    /** {@inheritDoc} */
    public void setTotalRowCount(int totalRowCount) {
        ((LazyLoadable)getDelegate()).setTotalRowCount(totalRowCount);
    }

    /** {@inheritDoc} */
    public void setTotalRowCount(TreeGridRow gridRow, int totalRowCount) {
        if (gridRow == null) {
            setTotalRowCount(totalRowCount);
            return;
        }

        if (gridRow instanceof LazyLoadable)
            ((LazyLoadable) gridRow).setTotalRowCount(totalRowCount);
    }

    /**
     * This is a delegate class for lazy tree rows creation.
     *
     * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
     */
    protected class DelegateLazyGridDataModel extends DelegateEditableGridDataModel {
        /**
         * Creates a new instance of the delegate.
         *
         * @param data is a data to store in the delegate.
         */
        public DelegateLazyGridDataModel(Object[][] data) {
            super(data);
        }

        /**
         * Creates a new instance of the class.
         *
         * @param handler is a handler required for synchronization.
         */
        protected DelegateLazyGridDataModel(DataModelCallbackHandler handler) {
            super(handler);
        }

        /**
         * Overriden to create lazy rows.
         *
         * @param columnCount is a column count.
         * @return a lazy tree row instance.
         */
        protected GridRow createGridRow(int columnCount) {
            return new LazyTreeGridRow(getThisModel());
        }
    }
}
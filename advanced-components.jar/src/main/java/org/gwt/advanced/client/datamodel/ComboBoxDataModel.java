package org.gwt.advanced.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an implementation of the data model interface for the ComboBox widget.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public class ComboBoxDataModel implements ListDataModel {
    /** a list of item IDs where each item is instance of <code>String</code> */
    private List itemIds = new ArrayList();
    /** a map of items where each item is pair of <code>String</code> ID and <code>Object</code> value */
    private Map items = new HashMap();
    /** a selected item ID */
    private String selectedId;

    /** {@inheritDoc} */
    public void add(String id, Object item) {
        List ids = getItemIds();
        if (!ids.contains(id))
            ids.add(id);
        getItems().put(id, item);
    }

    /** {@inheritDoc} */
    public void add(int index, String id, Object item) {
        List ids = getItemIds();
        index = getValidIndex(index);

        if (!ids.contains(id))
            ids.add(index, id);

        add(id, item);
    }

    /** {@inheritDoc} */
    public Object get(String id) {
        return getItems().get(id);
    }

    /** {@inheritDoc} */
    public Object get(int index) {
        if (isIndexValid(index))
            return get((String) getItemIds().get(index));
        else
            return null;
    }

    /** {@inheritDoc} */
    public void remove(String id) {
        getItemIds().remove(id);
        getItems().remove(id);
    }

    /** {@inheritDoc} */
    public void remove(int index) {
        if (isIndexValid(index))
            remove((String) getItemIds().get(index));
    }

    /** {@inheritDoc} */
    public String getSelectedId() {
        if (selectedId == null && itemIds.size() > 0)
            selectedId = (String) itemIds.get(0);
        return selectedId;
    }

    /** {@inheritDoc} */
    public int getSelectedIndex() {
        return getItemIds().indexOf(getSelectedId());
    }

    /** {@inheritDoc} */
    public Object getSelected() {
        return getItems().get(getSelectedId());
    }

    /** {@inheritDoc} */
    public void setSelectedId(String id) {
        this.selectedId = id;
    }

    /** {@inheritDoc} */
    public void setSelectedIndex(int index) {
        index = getValidIndex(index);
        List ids = getItemIds();
        if (ids.size() > 0)
            setSelectedId((String) ids.get(index));
    }

    /** {@inheritDoc} */
    public void clear() {
        itemIds.clear();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return itemIds.isEmpty();
    }

    /** {@inheritDoc} */
    public int getCount() {
        return itemIds.size();
    }

    /**
     * Getter for property 'itemIds'.
     *
     * @return Value for property 'itemIds'.
     */
    protected List getItemIds() {
        return itemIds;
    }

    /**
     * Getter for property 'items'.
     *
     * @return Value for property 'items'.
     */
    protected Map getItems() {
        return items;
    }

    /**
     * This method checks whether the specified index is valid.
     *
     * @param index is an index value to check.
     * @return <code>true</code> if the index is valid.
     */
    protected boolean isIndexValid(int index) {
        return getItemIds().size() >= index;
    }

    /**
     * This method calculates a valid index value taking into account the following rule:
     * if the index < 0, it returns 0;
     * if the index > then {@link #getItemIds()} size, it returns {@link #getItemIds()} size.
     *
     * @param invalidIndex is an index.
     * @return a valid index value.
     */
    protected int getValidIndex(int invalidIndex) {
        List ids = getItemIds();

        if (invalidIndex < 0)
            invalidIndex = 0;
        if (invalidIndex > ids.size())
            invalidIndex = ids.size();
        return invalidIndex;
    }
}

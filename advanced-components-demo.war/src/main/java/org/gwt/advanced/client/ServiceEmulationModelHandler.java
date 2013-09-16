/*
 * Copyright 2008-2013 Sergey Skladchikov
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

package org.gwt.advanced.client;

import com.google.gwt.user.client.ui.ListBox;
import org.gwt.advanced.client.datamodel.DataModelCallbackHandler;
import org.gwt.advanced.client.datamodel.Editable;
import org.gwt.advanced.client.datamodel.GridDataModel;
import org.gwt.advanced.client.datamodel.LazyLoadable;
import org.gwt.advanced.client.ui.widget.GridPanel;

import java.util.*;

/**
 * This is a demo model handler.<p>
 * In fact it must be remote service, but this sample just emulates remoting.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class ServiceEmulationModelHandler implements DataModelCallbackHandler<Editable> {
    /** persistent data */
    private Object[][] data;
    /** a grid panel */
    private GridPanel panel;

    /**
     * Creates an instance of this class and initializes the internal field.
     *
     * @param data is an initial data.
     */
    public ServiceEmulationModelHandler(Object[][] data) {
        this.data = data;
    }

    /** {@inheritDoc} */
    public void synchronize(Editable model) {
        if (panel != null)
            panel.lock();
        saveData(model);
        List<Object[]> rows = Arrays.asList(data);
        Collections.sort(rows, new DataComparator(model.getSortColumn(), model.isAscending()));

        ((LazyLoadable)model).setTotalRowCount(data.length);
        List<Object[]> result = new ArrayList<Object[]>();
        for (int i = model.getStartRow(); i < rows.size() && i < model.getStartRow() + model.getPageSize(); i++) {
            result.add(rows.get(i));
        }

        model.update(result.toArray(new Object[result.size()][]));
        if (panel != null)
            panel.unlock();
    }

    /**
     * This method emulates data saving to the persistence storage.
     *
     * @param model is a grid data model to be saved.
     */
    private void saveData (GridDataModel model) {
        Object[][] modelData = model.getData();

        List<Object[]> dataList = new ArrayList<Object[]>(Arrays.asList(data));
        for (int j = 0; j < dataList.size(); j++) {
            Object[] persistentRow = dataList.get(j);
            Long persistentId = (Long) persistentRow[persistentRow.length - 1];
            for (Object[] row : modelData) {
                Long id = (Long) row[row.length - 1];
                if (persistentId.equals(id)) {
                    dataList.set(j, row);
                    break;
                } else if (id == null) {
                    row[row.length - 1] = System.currentTimeMillis();
                    dataList.add(row);
                    break;
                }
            }
        }
        
        Object[][] removedRows = ((Editable) model).getRemovedRows();
        for (Object[] row : removedRows) {
            Long id = (Long) row[row.length - 1];
            for (int j = 0; j < dataList.size(); j++) {
                Object[] persistentRow = dataList.get(j);
                Long persistentId = (Long) persistentRow[persistentRow.length - 1];
                if (persistentId.equals(id)) {
                    dataList.remove(j);
                    break;
                }
            }
        }

        data = dataList.toArray(new Object[dataList.size()][]);
    }

    /**
     * Getter for property 'panel'.
     *
     * @return Value for property 'panel'.
     */
    public GridPanel getPanel() {
        return panel;
    }

    /**
     * Setter for property 'panel'.
     *
     * @param panel Value to set for property 'panel'.
     */
    public void setPanel(GridPanel panel) {
        this.panel = panel;
    }

    /**
     * This is a data comparator to emulate server-side sorting.<p/>
     * In your applications you will use database sorting.
     */
    private static class DataComparator implements Comparator<Object> {
        /** sort column number */
        private int sortColumn;
        /** sort order */
        private boolean ascending;

        /**
         * This constructor initializes internal fields.
         *
         * @param sortRow is a sort column.
         * @param ascending is a sort order.
         */
        public DataComparator (int sortRow, boolean ascending) {
            this.sortColumn = sortRow;
            this.ascending = ascending;
        }

        /** {@inheritDoc} */
        @SuppressWarnings({"unchecked"})
        public int compare (Object o1, Object o2) {
            Object[] row1 = (Object[]) o1;
            Object[] row2 = (Object[]) o2;

            int sign = ascending ? 1 : -1;

            if (row1[sortColumn] == null && row2[sortColumn] == null)
                return 0;
            else if (row2[sortColumn] != null && row1[sortColumn] == null)
                return sign;
            else if (row1[sortColumn] != null && row2[sortColumn] == null)
                return -sign;

            if (row1[sortColumn] instanceof Comparable)
                return sign * ((Comparable)row1[sortColumn]).compareTo(row2[sortColumn]);
            else if (row1[sortColumn] instanceof ListBox) {
                ListBox list1 = (ListBox) row1[sortColumn];
                ListBox list2 = (ListBox) row2[sortColumn];
                return sign * list1.getValue(list1.getSelectedIndex()).compareTo(list2.getValue(list2.getSelectedIndex()));
            } else
                return 0;
        }
    }
}

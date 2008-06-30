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

package org.gwt.advanced.client;

import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.widget.ComboBox;
import org.gwt.advanced.client.ui.widget.GridPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a model factory simulating persistence storage.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a> 
 */
public class DemoModelFactory {
    /** employee list */
    private static Object[][] employees = new Object[][] {
        new Object[]{"John Doe", createRandomDate(), createDepartmentListBox(0), new Long(0)},
        new Object[]{"Peter Masters", createRandomDate(), createDepartmentListBox(0), new Long(1)},
        new Object[]{"Bill Walles", createRandomDate(), createDepartmentListBox(1), new Long(2)},
        new Object[]{"John Robinson", createRandomDate(), createDepartmentListBox(1), new Long(3)},
        new Object[]{"Roger Hooker", createRandomDate(), createDepartmentListBox(2), new Long(4)},
        new Object[]{"Tim Gilbert", createRandomDate(), createDepartmentListBox(2), new Long(5)},
        new Object[]{"Martin Connery", createRandomDate(), createDepartmentListBox(3), new Long(6)},
        new Object[]{"Robert Hendrikson", createRandomDate(), createDepartmentListBox(3), new Long(7)},
        new Object[]{"Sean York", createRandomDate(), createDepartmentListBox(3), new Long(8)},
        new Object[]{"John Gates", createRandomDate(), createDepartmentListBox(4), new Long(9)},
        new Object[]{"Ringo Bates", createRandomDate(), createDepartmentListBox(5), new Long(10)},
        new Object[]{"Tom Bakster", createRandomDate(), createDepartmentListBox(6), new Long(11)},
        new Object[]{"Donald Simpson", createRandomDate(), createDepartmentListBox(7), new Long(12)},
        new Object[]{"Harry McCormic", createRandomDate(), createDepartmentListBox(7), new Long(13)},
        new Object[]{"Rupert Wheel", createRandomDate(), createDepartmentListBox(8), new Long(14)}
    };

    /**
     * This method creates people tree model.<p>
     * Each boss has 0 or more employees.
     *
     * @return editable data model instance.
     */
    public static Editable createTreePeopleModel() {
        TreeGridDataModel model = new TreeGridDataModel(new Object[][]{employees[14]});
        model.setAscending(false);
        model.setPageSize(10);

        TreeGridRow president = (TreeGridRow) model.getRow(0);
        president.setExpanded(true);
        model.addRow(president, employees[10]);
        model.addRow(president, employees[11]);
        model.addRow(president, employees[2]);
        model.addRow(president, employees[3]);
        model.addRow(president, employees[0]);
        model.addRow(president, employees[1]);
        model.setSubtreePagingEnabled(president, true);

        //developers
        TreeGridRow projectManager = model.getRow(president, 0);
        model.addRow(projectManager, employees[9]);
        model.addRow(projectManager, employees[6]);
        model.addRow(projectManager, employees[7]);
        model.addRow(projectManager, employees[4]);
        model.addRow(projectManager, employees[8]);
        model.addRow(projectManager, employees[5]);
        model.setSubtreePagingEnabled(projectManager, true);

        //testers
        TreeGridRow qaManager = model.getRow(president, 1);
        model.addRow(qaManager, employees[12]);
        model.addRow(qaManager, employees[13]);
        model.setSubtreePagingEnabled(qaManager, true);

        return model;
    }

    /** departments list */
    private static Object[][] departments = new Object[][] {
        new Object[] {"HR", "Recruiters", "They hired everyone", new Long(0)},
        new Object[] {"Bookkeeping", "Accountants", "They count salary", new Long(1)},
        new Object[] {"Development", "Developers", "They make applications", new Long(2)},
        new Object[] {"Management", "Managers", "They manage everyone", new Long(3)},
        new Object[] {"Testing", "Testers", "They test applications", new Long(4)},
        new Object[] {"President", "President", "Very important person", new Long(5)},
    };

    /**
     * The list of Europe countries sorted by GDP.
     */
    private static String[][] countries = new String[][] {
        new String[] {"1", "Germany"},
        new String[] {"2", "United Kingdom"},
        new String[] {"3", "France"},
        new String[] {"4", "Italy"},
        new String[] {"5", "Spain"},
        new String[] {"6", "Russia"},
        new String[] {"7", "Netherlands"},
        new String[] {"8", "Turkey"},
        new String[] {"9", "Sweden"},
        new String[] {"10", "Poland"},
        new String[] {"11", "Switzerland"},
        new String[] {"12", "Norway"},
        new String[] {"13", "Austria"},
        new String[] {"14", "Greece"},
        new String[] {"15", "Denmark"},
        new String[] {"16", "Ireland"},
        new String[] {"17", "Finland"},
        new String[] {"18", "Portugal"},
        new String[] {"19", "Romania"},
        new String[] {"20", "Ukraine"},
        new String[] {"21", "Hungary"},
        new String[] {"22", "Slovakia"},
        new String[] {"23", "Croatia"},
        new String[] {"24", "Luxembourg"},
        new String[] {"25", "Slovenia"},
        new String[] {"26", "Serbia"},
        new String[] {"27", "Bulgaria"},
        new String[] {"28", "Azerbaijan"},
        new String[] {"29", "Lithuania"},
        new String[] {"30", "Belarus"},
        new String[] {"31", "Latvia"},
        new String[] {"32", "Estonia"},
        new String[] {"33", "Cyprus"},
        new String[] {"34", "Iceland"},
        new String[] {"35", "Bosnia and Herzegovina"},
        new String[] {"36", "Albania"},
        new String[] {"37", "Georgia"},
        new String[] {"38", "Armenia"},
        new String[] {"39", "Macedonia"},
        new String[] {"40", "Malta"},
        new String[] {"41", "Moldova"},
        new String[] {"42", "Andorra"},
        new String[] {"43", "Liechtenstein"},
        new String[] {"44", "Monaco"},
        new String[] {"45", "Montenegro"},
        new String[] {"46", "San Marino"},
        new String[] {"47", "Vatican City"}
    };

    /**
     * This method creates an employy model mapping the specified department to
     * appropriate employees.
     *
     * @param departmentId is a department ID.
     * @return editable data model instance.
     */
    public static Editable createEployeesModel(long departmentId) {
        EditableGridDataModel model;
        if (departmentId == 0)
            model = new EditableGridDataModel(getEmployees(new long[]{0, 1}));
        else if (departmentId == 1)
            model = new EditableGridDataModel(getEmployees(new long[]{2, 3}));
        else if (departmentId == 2)
            model = new EditableGridDataModel(getEmployees(new long[]{4, 5, 6, 7, 8, 9}));
        else if (departmentId == 3)
            model = new EditableGridDataModel(getEmployees(new long[]{10, 11}));
        else if (departmentId == 4)
            model = new EditableGridDataModel(getEmployees(new long[]{12, 13}));
        else if (departmentId == 5)
            model = new EditableGridDataModel(getEmployees(new long[]{14}));
        else
            model = new EditableGridDataModel(employees);
        model.setPageSize(10);
        return model;
    }

    /**
     * This method creates employees model.<p/>
     * This model is lazy loadable and uses {@link ServiceEmulationModelHandler} to obtain data.
     *
     * @return editable data model instance.
     */
    public static Editable createLazyEmployeesModel() {
        LazyGridDataModel lazyGridDataModel = new LazyGridDataModel(
            new ServiceEmulationModelHandler(employees)
        );
        lazyGridDataModel.setPageSize(10);
        return lazyGridDataModel;
    }

    /**
     * This method creates employees model.<p/>
     * It's used for master-detail demo.
     *
     * @param panel is a grid panel.
     * @param parent is a parent grid panel.
     * @return editable data model instance.
     */
    public static Editable createDepartmentDetailModel(GridPanel panel, GridPanel parent) {
        LazyGridDataModel lazyGridDataModel = new LazyGridDataModel(
            new DetailGridModelHandler(panel, parent)
        );
        lazyGridDataModel.setPageSize(10);
        return lazyGridDataModel;
    }

    /**
     * This method creates departments model.<p>
     * Each department consists of a least one employee.
     *
     * @return editable data model instance.
     */
    public static Editable createDepartmentsModel() {
        EditableGridDataModel model = new EditableGridDataModel(departments);
        model.setPageSize(10);
        return model;
    }

    /**
     * This method creates departments hierachical model.<p>
     * Each department consists of a least one employee.
     *
     * @return editable data model instance.
     */
    public static Editable createHierarchicalDepartmentsModel() {
        HierarchicalGridDataModel model = new HierarchicalGridDataModel(departments);
        model.setPageSize(10);
        return model;
    }

    /**
     * Creates a combo box model of countries.
     *
     * @return is a combo box model instance.
     */
    public static ComboBoxDataModel createsCountriesModel() {
        ComboBoxDataModel model = new ComboBoxDataModel();
        for (int i = 0; i < countries.length; i++) {
            String[] country = countries[i];
            model.add(country[0], country[1]);
        }
        return model;
    }

    /**
     * Fills the model with a list of countries filted by expression.
     *
     * @param expression is an expression to fill the list.
     * @param model is a model to fill.
     */
    public static void fillCountriesModel(String expression, SuggestionBoxDataModel model) {
        model.clear();
        for (int i = 0; i < countries.length; i++) {
            String[] country = countries[i];
            if (expression != null && country[1].toLowerCase().startsWith(expression.toLowerCase()))
                model.add(country[0], country[1]);
        }
    }

    /**
     * Fills the model with a list of countries (with flags) filted by expression.
     *
     * @param expression is an expression to fill the list.
     * @param model is a model to fill.
     */
    public static void fillCountriesWithFlagsModel(String expression, SuggestionBoxDataModel model) {
        model.clear();
        for (int i = 0; i < countries.length; i++) {
            String[] country = countries[i];
            if (expression != null && country[1].toLowerCase().startsWith(expression.toLowerCase())) {
                model.add(country[0], new IconItem("images/22px-Flag_of_" + country[1].replaceAll(" ", "_") + ".svg.png", country[1]));
            }
        }
    }

    /**
     * This method generates random date.
     *
     * @return a date.
     */
    private static Date createRandomDate() {
        return new Date((long)(Math.random() * 24*365*60*1000000));
    }

    /**
     * This method creates a list of employees by their IDs.
     *
     * @param ids is a list of IDs.
     * @return a list of employees.
     */
    private static Object[][] getEmployees(long[] ids) {
        List result = new ArrayList();
        for (int i = 0; i < employees.length; i++) {
            Object[] employee = employees[i];
            for (int j = 0; j < ids.length; j++) {
                long id = ids[j];
                if (id == ((Long)employee[employee.length - 1]).longValue())
                    result.add(employee);
            }
        }

        Object[][] resultArray = new Object[result.size()][];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = (Object[]) result.get(i);
        }
        return resultArray;
    }

    /**
     * This method generates departments combo box.
     *
     * @param selectedIndex is a selected item index.
     * @return a list box of departments.
     */
    public static ComboBox createDepartmentListBox(int selectedIndex) {
        ComboBox comboBox = new ComboBox();
        ComboBoxDataModel model = new ComboBoxDataModel();
        comboBox.setModel(model);

        model.add("Recruiter", "Recruiter");
        model.add("Accountant", "Accountant");
        model.add("Jr. Developer", "Jr. Developer");
        model.add("Developer", "Developer");
        model.add("Senior Developer", "Senior Developer");
        model.add("Project Manager", "Project Manager");
        model.add("QA Manager", "QA Manager");
        model.add("Tester", "Tester");
        model.add("President", "President");
        model.setSelectedIndex(selectedIndex);

        return comboBox;
    }
}

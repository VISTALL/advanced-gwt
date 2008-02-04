package org.gwt.advanced.client;

import com.google.gwt.user.client.ui.ListBox;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.gwt.advanced.client.datamodel.*;
import org.gwt.advanced.client.ui.widget.GridPanel;

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
     * This method generates departments list box.
     *
     * @param selectedIndex is a selected item index.
     * @return a list box of departments.
     */
    public static ListBox createDepartmentListBox(int selectedIndex) {
        ListBox listBox = new ListBox();
        listBox.addItem("Recruiter", "Recruiter");
        listBox.addItem("Accountant", "Accountant");
        listBox.addItem("Jr. Developer", "Jr. Developer");
        listBox.addItem("Developer", "Developer");
        listBox.addItem("Senior Developer", "Senior Developer");
        listBox.addItem("Project Manager", "Project Manager");
        listBox.addItem("QA Manager", "QA Manager");
        listBox.addItem("Tester", "Tester");
        listBox.addItem("President", "President");
        listBox.setSelectedIndex(selectedIndex);
        return listBox;
    }
}

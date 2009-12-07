/*
 * Copyright 2009 Sergey Skladchikov
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

package org.gwt.advanced.client.showcase;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.advanced.client.AbstractShowcase;
import org.gwt.advanced.client.DemoModelFactory;
import org.gwt.advanced.client.datamodel.ComboBoxDataModel;
import org.gwt.advanced.client.datamodel.ListCallbackHandler;
import org.gwt.advanced.client.datamodel.ListDataModel;
import org.gwt.advanced.client.datamodel.SuggestionBoxDataModel;
import org.gwt.advanced.client.ui.widget.ComboBox;
import org.gwt.advanced.client.ui.widget.DatePicker;
import org.gwt.advanced.client.ui.widget.SuggestionBox;

import java.util.Date;

/**
 * This is a showcase for the {@link DatePicker}, {@link ComboBox} and {@link SuggestionBox} widgets.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class TextAndButtonShowcase extends AbstractShowcase {
    /** {@inheritDoc} */
    protected Widget getWidget() {
        FlexTable panel = new FlexTable();

        panel.setWidget(0, 0, createHintLabel("Select a country or enter a new one:"));
        panel.setWidget(1, 0, createHintLabel("Select a date:"));
        panel.setWidget(2, 0, createHintLabel("Type any European country name:"));
        panel.setWidget(3, 0, createHintLabel("Type any European country name to see the flag:"));

        panel.getColumnFormatter().setWidth(0, "50%");

        ComboBox comboBox = new ComboBox();
        comboBox.setWidth("100%");
        ComboBoxDataModel model = DemoModelFactory.createsCountriesModel();
        model.setSelectedIndex(0);
        comboBox.setModel(model);
        comboBox.setCustomTextAllowed(true);

        DatePicker picker = new DatePicker(new Date());
        picker.setWidth("100%");
        picker.setTimeVisible(true);

        final SuggestionBox suggestionBox = new SuggestionBox();
        suggestionBox.setExpressionLength(1);
        suggestionBox.setModel(new SuggestionBoxDataModel(new SuggestionBoxHandler()));
        suggestionBox.setWidth("100%");


        SuggestionBox complexSuggestionBox = new SuggestionBox();
        complexSuggestionBox.setExpressionLength(1);
        complexSuggestionBox.setModel(new SuggestionBoxDataModel(new SuggestionBoxFlagsHandler()));
        complexSuggestionBox.setWidth("100%");

        panel.setWidget(0, 1, comboBox);
        panel.setWidget(1, 1, picker);
        panel.setWidget(2, 1, suggestionBox);
        panel.setWidget(3, 1, complexSuggestionBox);

        panel.getColumnFormatter().setWidth(1, "50%");

        comboBox.display();
        picker.display();
        suggestionBox.display();
        complexSuggestionBox.display();

        return panel;
    }

    /** {@inheritDoc} */
    protected String getHint() {
        return RESOURCES.otherControlsDemo();
    }

    /** {@inheritDoc} */
    protected String getName() {
        return "Text & Button Widgets";
    }

    /**
     * Creates a hint label for the other controls demo.
     *
     * @param text is a hint text.
     * @return a new label.
     */
    private Label createHintLabel(String text) {
        Label label = new Label(text);
        label.setStyleName("demo-ControlLabel");
        return label;
    }
    
    /**
     * Sample suggestion box handler.
     */
    private class SuggestionBoxHandler implements ListCallbackHandler {
        /** {@inheritDoc} */
        public void fill(ListDataModel model) {
            String expression = ((SuggestionBoxDataModel) model).getExpression();
            DemoModelFactory.fillCountriesModel(expression, (SuggestionBoxDataModel) model);
        }
    }

    /**
     * Sample suggestion box handler.
     */
    private class SuggestionBoxFlagsHandler implements ListCallbackHandler {
        /** {@inheritDoc} */
        public void fill(ListDataModel model) {
            String expression = ((SuggestionBoxDataModel) model).getExpression();
            DemoModelFactory.fillCountriesWithFlagsModel(expression, (SuggestionBoxDataModel) model);
        }
    }
}

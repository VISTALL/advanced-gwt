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

package org.gwt.advanced.client.ui.widget;

import com.google.gwt.user.client.ui.*;
import org.gwt.advanced.client.ui.AdvancedWidget;
import org.gwt.advanced.client.util.ThemeHelper;

/**
 * This is a basic class for all text boxs with a button.
 * 
 * @see org.gwt.advanced.client.ui.widget.ComboBox
 * @see org.gwt.advanced.client.ui.widget.DatePicker
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public abstract class TextButtonPanel extends SimplePanel implements AdvancedWidget {
    /** widget layout */
    private FlexTable layout;
    /** a selected value box */
    private TextBox selectedValue;
    /** a choice button */
    private ToggleButton choiceButton;
    /** a choice button image */
    private Image choiceButtonImage;
    /** this flag means whether it's possible to enter a custom text */
    private boolean customTextAllowed;
    /** a falg meaning whether the widget locked */
    private boolean locked;
    /** a locking panel to lock the screen */
    private LockingPanel lockingPanel;
    /** choice button visibility flag */
    private boolean choiceButtonVisible = true;
    /** widget width */
    private String width;
    /** widget height */
    private String height;

    /**
     * Getter for property 'customTextAllowed'.
     *
     * @return Value for property 'customTextAllowed'.
     */
    public boolean isCustomTextAllowed() {
        return customTextAllowed;
    }

    /**
     * Setter for property 'customTextAllowed'.
     *
     * @param customTextAllowed Value to set for property 'customTextAllowed'.
     */
    public void setCustomTextAllowed(boolean customTextAllowed) {
        this.customTextAllowed = customTextAllowed;
    }

    /**
     * Setter for property 'choiceButtonImage'.
     *
     * @param choiceButtonImage Value to set for property 'choiceButtonImage'.
     */
    public void setChoiceButtonImage(Image choiceButtonImage) {
        this.choiceButtonImage = choiceButtonImage;
    }

    /**
     * Getter for property 'choiceButtonVisible'.
     *
     * @return Value for property 'choiceButtonVisible'.
     */
    public boolean isChoiceButtonVisible() {
        return choiceButtonVisible;
    }

    /**
     * Setter for property 'choiceButtonVisible'.
     *
     * @param choiceButtonVisible Value to set for property 'choiceButtonVisible'.
     */
    public void setChoiceButtonVisible(boolean choiceButtonVisible) {
        this.choiceButtonVisible = choiceButtonVisible;
    }
    
    /** {@inheritDoc} */
    public void display() {
        FlexTable layout = getLayout();
        while (layout.getRowCount() > 0)
            layout.removeRow(layout.getRowCount() - 1);

        addComponentListeners();

        layout.setWidget(0, 0, getSelectedValue());
        layout.setWidget(0, 1, getChoiceButton());

        prepareSelectedValue();
        if(isChoiceButtonVisible())
            prepareChoiceButton();

        setStyleName("advanced-TextButtonPanel");
        setWidget(layout);
    }

    /** {@inheritDoc} */
    public void setWidth(String width) {
        super.setWidth(width);
        this.width = width;
        prepareSelectedValue();
    }


    /** {@inheritDoc} */
    public void setHeight(String height) {
        super.setHeight(height);
        this.height = height;
        prepareSelectedValue();
    }

    /**
     * This method gets a maximum length of the text box.<p/>
     * It makes sence if you allow custom values entering.<p/>
     * See also {@link #isCustomTextAllowed()} and {@link #setCustomTextAllowed(boolean)}.
     *
     * @return a maximum length of the text box.
     */
    public int getMaxLength() {
        return getSelectedValue().getMaxLength();
    }

    /**
     * This method sets a maximum length of the text box.<p/>
     * It makes sence if you allow custom values entering.<p/>
     * See also {@link #isCustomTextAllowed()} and {@link #setCustomTextAllowed(boolean)}.
     *
     * @param length is a maximum length of the text box.
     */
    public void setMaxLength(int length) {
        getSelectedValue().setMaxLength(length);
    }

    /**
     * This method should returns a default button image name.
     *
     * @return an image name.
     */
    protected abstract String getDefaultImageName();

    /**
     * This method adds component listeners.
     */
    protected abstract  void addComponentListeners();

    /**
     * Prepares the selected value box for displaying.
     */
    protected void prepareSelectedValue() {
        TextBox selectedValue = getSelectedValue();
        selectedValue.setReadOnly(!isCustomTextAllowed());
        selectedValue.setStyleName("selected-value");

        if(getHeight() != null) {
            getLayout().setHeight("100%");
            getLayout().getCellFormatter().setHeight(0, 0, "100%");
            getSelectedValue().setHeight("100%");
        }

        if (getWidth() != null) {
            getLayout().setWidth("100%");
            getLayout().getCellFormatter().setWidth(0, 0, "100%");
            getSelectedValue().setWidth("100%");
        }
    }

    /**
     * Prepares the drop down button for displaying.
     */
    protected void prepareChoiceButton() {
        ToggleButton dropDownButton = getChoiceButton();
        dropDownButton.getUpFace().setImage(getChoiceButtonImage());
        dropDownButton.getDownFace().setImage(getChoiceButtonImage());
        dropDownButton.setStyleName("choice-button");
    }

    /**
     * Getter for property 'layout'.
     *
     * @return Value for property 'layout'.
     */
    protected FlexTable getLayout() {
        if (layout == null)
            layout = new FlexTable();
        return layout;
    }

    /**
     * Getter for property 'selectedValue'.
     *
     * @return Value for property 'selectedValue'.
     */
    protected TextBox getSelectedValue() {
        if (selectedValue == null)
            selectedValue = new TextBox();
        return selectedValue;
    }

    /**
     * Getter for property 'choiceButton'.
     *
     * @return Value for property 'choiceButton'.
     */
    protected ToggleButton getChoiceButton() {
        if (choiceButton == null)
            choiceButton = new ToggleButton();
        return choiceButton;
    }

    /**
     * Getter for property 'choiceButtonImage'.
     *
     * @return Value for property 'choiceButtonImage'.
     */
    protected Image getChoiceButtonImage() {
        if (choiceButtonImage == null)
            choiceButtonImage = new Image(ThemeHelper.getInstance().getFullImageName(getDefaultImageName()));
        return choiceButtonImage;
    }

    /**
     * Getter for property 'locked'.
     *
     * @return Value for property 'locked'.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * This method locks the screen.
     */
    public void lock() {
        setLocked(true);
        getLockingPanel().lock();
    }

    /**
     * This method unlocks the screen and redisplays the widget.
     */
    public void unlock() {
        display();
        getLockingPanel().unlock();
        setLocked(false);
    }

    /**
     * Setter for property 'locked'.
     *
     * @param locked Value to set for property 'locked'.
     */
    protected void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Getter for property 'lockingPanel'.
     *
     * @return Value for property 'lockingPanel'.
     */
    protected LockingPanel getLockingPanel() {
        if (lockingPanel == null)
            lockingPanel = new LockingPanel();
        return lockingPanel;
    }

    /**
     * Getter for property 'width'.
     *
     * @return Value for property 'width'.
     */
    protected String getWidth() {
        return width;
    }

    /**
     * Getter for property 'height'.
     *
     * @return Value for property 'height'.
     */
    protected String getHeight() {
        return height;
    }
}

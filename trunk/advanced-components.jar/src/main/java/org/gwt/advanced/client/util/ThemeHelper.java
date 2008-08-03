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

package org.gwt.advanced.client.util;

/**
 * This sigleton is used to centrilize and simplify theme management.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class ThemeHelper {
    /** default name of the CSS link element */
    public static final String LINK_ELEMENT_ID = "advancedTheme";
    /** an instance of this class */
    private static final ThemeHelper instance = new ThemeHelper();
    /** theme name */
    private String themeName = "default";

    /**
     * Creates an instance of this class.
     */
    private ThemeHelper() {}

    /**
     * This method returns an instance of this class.
     *
     * @return an instance.
     */
    public static ThemeHelper getInstance() {
        return instance;
    }

    /** {@inheritDoc} */
    public void setThemeName (String name) {
        themeName = name;
        StyleUtil.setLinkHref(LINK_ELEMENT_ID, "advanced/themes/" + name + "/theme.css");
    }

    /** {@inheritDoc} */
    public String getThemeName () {
        return themeName;
    }

    /**
     * This method gets a full name of the specified image using the theme name.
     *
     * @param shortName is a short name of the image.
     * @return is a full name.
     */
    public String getFullImageName(String shortName) {
        return "advanced/themes/" + getThemeName() + "/images/" + shortName;
    }
}

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

import com.google.gwt.i18n.client.Constants;

/**
 * This is a resource bundle containing hints of the demo screen.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface ResourceBundle extends Constants {
    /**
     * Editable grid demo hint.
     *
     * @return the hint text.
     */
    String editableGridDemo();

    /**
     * Hierarchical grid demo hint.
     *
     * @return the hint text.
     */
    String hierarchicalGridDemo();

    /**
     * Lazy grid demo hint.
     *
     * @return the hint text.
     */
    String lazyGridDemo();

    /**
     * Master-detail demo hint.
     *
     * @return the hint text.
     */
    String masterDetailDemo();

    /**
     * Other controls demo hint.
     *
     * @return the hint text.
     */
    String otherControlsDemo();
}

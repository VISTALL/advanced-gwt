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

package org.gwt.advanced.client.ui;

/**
 * This interface describes grid toolbar listeners.<p>
 * Normally you don't have to implement it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface GridToolbarListener {
    /**
     * This is an operation to be done on Add button click.
     */
    void onAddClick();

    /**
     * This is an operation to be done on Remove button click.
     */
    void onRemoveClick();

    /**
     * This is an operation to be done on Save button click.
     */
    void onSaveClick ();

    /**
     * This is an operation to be done on Clear button click.
     */
    void onClearClick ();
}

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

package org.gwt.advanced.client.ui.widget.tab;

import com.google.gwt.user.client.ui.FlexTable;
import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.border.Border;
import org.gwt.advanced.client.ui.widget.border.BorderLine;

/**
 * This is implementation of the tabs band for bottom position.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.4.6
 */
public class BottomBandRenderer extends AbstractBandRenderer {
    /** delegate implemetation injected for reusing */
    private TopBandRenderer renderer = new TopBandRenderer();

    /** {@inheritDoc} */
    protected int createTab(AdvancedTabPanel panel, FlexTable result, int count, int index, Border border) {
        return renderer.createTab(panel, result, count, index, border);
    }

    /** {@inheritDoc} */
    protected BorderLine getHiddenLine() {
        return BorderLine.TOP;
    }

    /** {@inheritDoc} */
    protected int createEmpty(FlexTable result, int count, String styleName) {
        return renderer.createEmpty(result, count, styleName);
    }
}

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

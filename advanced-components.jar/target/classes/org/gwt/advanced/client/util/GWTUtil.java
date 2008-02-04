package org.gwt.advanced.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.impl.DOMImpl;

/**
 * This class contains helper methods for GWT framework.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public class GWTUtil {
    /**
     * This method checks whether the current browser is IE.
     *
     * @return <code>true</code> if the browser is IE.
     */
    public static boolean isIE() {
        return GWT.getTypeName(GWT.create(DOMImpl.class)).equals(
            "com.google.gwt.user.client.impl.DOMImplIE6"
        );
    }
}

package org.gwt.advanced.client.util;

/**
 * This is a CSS manipulation util.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class StyleUtil {
    /**
     * This method sets a href attribute of the specified link element.
     *
     * @param linkElementId is a link element.
     * @param url is a URL to be applied for the href.
     */
    public static native void setLinkHref(String linkElementId, String url) /*-{
        var link = $doc.getElementById(linkElementId);
        if (link != null && link != undefined) {
            link.href = url;
        }
    }-*/;
}

package org.gwt.advanced.client.ui;

import org.gwt.advanced.client.ui.widget.Pager;

/**
 * This interface describes pager listeners.<p>
 * Normally you don't have to implement it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public interface PagerListener {
    /**
     * This method is invoked on page change event.
     *
     * @param sender is a pager that sent the event.
     * @param page is a new page number.
     */
    void onPageChange(Pager sender, int page);
}

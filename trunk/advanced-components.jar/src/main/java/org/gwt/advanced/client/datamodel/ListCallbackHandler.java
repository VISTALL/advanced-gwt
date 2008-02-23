package org.gwt.advanced.client.datamodel;

/**
 * This handler is invoked on the suggestion box expression change.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 */
public interface ListCallbackHandler {
    /**
     * See class docs.
     *
     * @param model is a model to be refreshed.
     */
    void fill(ListDataModel model);
}

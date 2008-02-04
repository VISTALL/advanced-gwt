package org.gwt.advanced.client.ui;

/**
 * This interface describes grid toolbar listeners.<p>
 * Normally you don't have to implement it.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
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

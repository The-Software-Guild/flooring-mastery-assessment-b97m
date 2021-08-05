package com.bm.flooringmastery.dao.exceptions;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryFailedExportException extends Exception {
    public FlooringMasteryFailedExportException(String message) {
        super(message);
    }

    public FlooringMasteryFailedExportException(String message, Throwable cause) {
        super(message, cause);
    }    
}

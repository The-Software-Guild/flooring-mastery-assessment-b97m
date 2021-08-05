package com.bm.flooringmastery.dao.exceptions;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryFailedLoadException extends Exception {
    public FlooringMasteryFailedLoadException(String message) {
        super(message);
    }

    public FlooringMasteryFailedLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

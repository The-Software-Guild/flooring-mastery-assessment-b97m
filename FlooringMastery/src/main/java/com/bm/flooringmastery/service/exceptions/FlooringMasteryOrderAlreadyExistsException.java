package com.bm.flooringmastery.service.exceptions;
/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryOrderAlreadyExistsException extends Exception {
    public FlooringMasteryOrderAlreadyExistsException(String message) {
        super(message);
    }

    public FlooringMasteryOrderAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.bm.flooringmastery.service.exceptions;
/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryOrderNotPresentForReplacementException extends Exception {
    public FlooringMasteryOrderNotPresentForReplacementException(String message) {
        super(message);
    }

    public FlooringMasteryOrderNotPresentForReplacementException(String message, Throwable cause) {
        super(message, cause);
    }
}

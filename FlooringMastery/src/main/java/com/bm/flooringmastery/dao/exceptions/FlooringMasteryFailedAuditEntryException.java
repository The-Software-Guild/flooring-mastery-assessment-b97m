package com.bm.flooringmastery.dao.exceptions;
/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryFailedAuditEntryException extends Exception {
    public FlooringMasteryFailedAuditEntryException(String message) {
        super(message);
    }

    public FlooringMasteryFailedAuditEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}

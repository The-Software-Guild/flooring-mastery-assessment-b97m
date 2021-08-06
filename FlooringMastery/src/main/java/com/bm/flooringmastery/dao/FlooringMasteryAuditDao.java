package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException;

/**
 * A DAO for providing Service Layer auditing
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 6, 2021
 */
public interface FlooringMasteryAuditDao {
    /**
     * Appends a new line for auditing purposes
     * 
     * If this process fails, the below exception will be thrown.
     * 
     * @param line 
     * @throws com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException 
     */
    public void appendLine(String line) throws FlooringMasteryFailedAuditEntryException;
}

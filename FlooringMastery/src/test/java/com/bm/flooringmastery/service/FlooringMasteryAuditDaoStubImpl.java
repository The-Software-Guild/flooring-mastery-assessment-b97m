package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryAuditDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException;
import org.springframework.stereotype.Component;

/**
 * A stub implementation of the Audit Dao for testing purposes
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 7, 2021
 */
public class FlooringMasteryAuditDaoStubImpl implements FlooringMasteryAuditDao {

    @Override
    public void appendLine(String line) throws FlooringMasteryFailedAuditEntryException {
        // do nothing
    }
}

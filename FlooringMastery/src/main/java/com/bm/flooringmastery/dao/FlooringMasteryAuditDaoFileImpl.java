package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An implementation of the AuditDao interface
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 6, 2021
 */
public class FlooringMasteryAuditDaoFileImpl implements FlooringMasteryAuditDao {
    private final String AUDIT_FILE;

    public FlooringMasteryAuditDaoFileImpl() {
        this("audit.txt");
    }   
    
    public FlooringMasteryAuditDaoFileImpl(String AUDIT_FILE) {
        this.AUDIT_FILE = AUDIT_FILE;
    }
    
    @Override
    public void appendLine(String line) throws FlooringMasteryFailedAuditEntryException {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(AUDIT_FILE, true));
        } catch (IOException ex) {
            throw new FlooringMasteryFailedAuditEntryException(
                "Could not append audit entry"
            );
        }
        
        writer.println(String.format(
            "%s: %s",
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss:nnnnnnnnn"
                )
            ),
            line
        ));
        
        writer.close();
    }
}

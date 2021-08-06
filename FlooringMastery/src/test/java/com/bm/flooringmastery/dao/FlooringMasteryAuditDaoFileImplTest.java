package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Timeout;

/**
 * Tests for the AuditDao file implementation
 * 
 * @author Benjamin Munoz
 */
public class FlooringMasteryAuditDaoFileImplTest {    
    public FlooringMasteryAuditDaoFileImplTest() {
    }

    @Test
    @Timeout(30) // this shouldn't take very long
    public void testAppendLine() throws IOException {
         // set up an empty file
        new PrintWriter(new FileWriter("Testing/audit.txt"));
        
        
        FlooringMasteryAuditDaoFileImpl instance = new FlooringMasteryAuditDaoFileImpl("Testing/audit.txt");
        
        String[] testStrings = new String[] {
            "",
            "nonblank entry",
            "loooooooooooooooooooooooooong entry",
            "some other entry"
        };
        
        Arrays.stream(testStrings).forEach(line -> {
            try {
                instance.appendLine(line);
            } catch (FlooringMasteryFailedAuditEntryException ex) {
                fail("An exception should not be thrown here");
            }
        });
        
        Scanner reader = new Scanner(new FileReader("Testing/audit.txt"));
        List<String> lineList = new LinkedList<>();
        while (reader.hasNextLine()) {
            // strip time data
            String trueLine = reader.nextLine();
            trueLine = trueLine.substring("yyyy-MM-dd HH:mm:ss:nnnnnnnnn: ".length());            
            lineList.add(trueLine);
        }
        
        String[] resStrings = lineList.toArray(new String[0]);
        assertNotNull(resStrings, "This element should not be null");
        assertEquals(
            testStrings.length,
            resStrings.length,
            "These arrays should be of the same length"
        );
        
        for (int i = 0; i < testStrings.length; i++) {
            assertEquals(
                testStrings[i],
                resStrings[i],
                "These strings should be equal"
            );
        }
    }
    
}

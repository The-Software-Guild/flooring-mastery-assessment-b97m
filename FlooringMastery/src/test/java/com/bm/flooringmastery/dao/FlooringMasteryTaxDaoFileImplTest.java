package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryTaxDaoFileImplTest {
    private FlooringMasteryTaxDao subject;

    @Test
    public void testAllMethodsOnEmpty() throws IOException, FlooringMasteryFailedLoadException {
        // ensure the given file is empty
        PrintWriter setupWriter = new PrintWriter(
            new FileWriter("Testing/Taxes_empty.txt")
        );
        setupWriter.println("State::StateName::TaxRate");
        setupWriter.close();
        
        subject = new FlooringMasteryTaxDaoFileImpl("Testing/Taxes_empty.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            assertTrue(
                subject.stateAbbrSet().isEmpty(), 
                "There should be no elements"
            );

            assertTrue(
                subject.getPercentTaxRateForStateAbbr(null).isEmpty(),
                "It should not be possible to get information for null input"
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("").isEmpty(),
                "It should not be possible to get information for empty input"
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("WA").isEmpty(),
                "It should not be possible to get information for nonempty input"
            );
        }
    }    

    @Test
    public void testAllMethodsOnSingle() throws IOException, FlooringMasteryFailedLoadException {
        PrintWriter setupWriter = new PrintWriter(
            new FileWriter("Testing/Taxes_single.txt")
        );
        setupWriter.println("State::StateName::TaxRate");
        setupWriter.println("ND::North Dakota::5.00");
        setupWriter.close();
        
        subject = new FlooringMasteryTaxDaoFileImpl("Testing/Taxes_single.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            Set<String> abbrSet = subject.stateAbbrSet();
            assertTrue(
                abbrSet.contains("ND"),
                "The set should contain this abbreviation"
            );
            assertTrue(
                abbrSet.size() == 1,
                "There should only be one element in this set"
            );
            
            subject.getPercentTaxRateForStateAbbr("ND").ifPresentOrElse(
                val -> {
                    assertTrue(
                        val.equals(new BigDecimal("5.00")),
                        "The percent tax should be 5.00"
                    );
                }, 
                () -> {
                    fail("There should be tax information for North Dakota");
                }
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr(null).isEmpty(),
                "It should not be possible to get tax information for null"
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("").isEmpty(),
                "It should not be possible to get tax information for \"\""
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("WA").isEmpty(),
                "It should not be possible to get tax information for states "
                + "other than ND"
            );
        }
    }

    @Test
    public void testAllMethodsOnMultiple() throws IOException, FlooringMasteryFailedLoadException {
        PrintWriter setupWriter = new PrintWriter(
            new FileWriter("Testing/Taxes_multi.txt")
        );
        setupWriter.println("State::StateName::TaxRate");
        setupWriter.println("ND::North Dakota::5.00");
        setupWriter.println("WA::Washington::3.12");
        setupWriter.close();
        
        subject = new FlooringMasteryTaxDaoFileImpl("Testing/Taxes_multi.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            Set<String> abbrSet = subject.stateAbbrSet();
            assertTrue(
                abbrSet.contains("ND"),
                "The set should contain ND"
            );
            assertTrue(
                abbrSet.contains("WA"),
                "The set should contain WA"
            );
            assertTrue(
                abbrSet.size() == 2,
                "The set should have two elements"
            );
            
            subject.getPercentTaxRateForStateAbbr("ND").ifPresentOrElse(
                val -> {
                    assertTrue(
                        val.equals(new BigDecimal("5.00")),
                        "The tax rate for ND should be 5.00"
                    );
                }, 
                () -> fail("There should be tax data for ND")
            );
            
            subject.getPercentTaxRateForStateAbbr("WA").ifPresentOrElse(
                val -> {
                    assertTrue(
                        val.equals(new BigDecimal("3.12")),
                        "The tax rate for WA should be 3.12"
                    );
                }, 
                () -> fail("There should be tax data for WA")
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr(null).isEmpty(),
                "It should not be possible to get tax information for null"
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("").isEmpty(),
                "It should not be possible to get tax information for \"\""
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("PA").isEmpty(),
                "It should not be possible to get tax information for states "
                + "other than ND or WA"
            );
        }
    }
}

package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * An implementation of the TaxDao interface
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryTaxDaoFileImpl implements FlooringMasteryTaxDao {
    private final String SRC_FILE;
    private final Map<String, BigDecimal> TAX_MAP;
    
    public FlooringMasteryTaxDaoFileImpl() {
        this("Data/Taxes.txt");
    }
    
    public FlooringMasteryTaxDaoFileImpl(String SRC_FILE) {
        this.SRC_FILE = SRC_FILE;
        this.TAX_MAP = new HashMap<>();
    }
    
    @Override
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException {
        Scanner reader;
        try {
            reader = new Scanner(new BufferedReader(new FileReader(SRC_FILE)));
        } catch (FileNotFoundException ex) {
            throw new FlooringMasteryFailedLoadException(
                "Unable to load tax information",
                ex
            );
        }
        
        // ignore header
        reader.nextLine();
        while (reader.hasNextLine()) {
            String[] tokens = reader.nextLine().split(",");
            String state = tokens[0];
            BigDecimal rate = new BigDecimal(tokens[2]);
            
            TAX_MAP.put(state, rate);
        }
        
        reader.close();
    }

    @Override
    public boolean hasInfoForStateAbbr(String state) {
        return percentTaxRateForStateAbbr(state).isPresent();
    }

    @Override
    public Optional<BigDecimal> percentTaxRateForStateAbbr(String state) {
        BigDecimal rate = TAX_MAP.get(state);
        if (rate == null) {
            return Optional.empty();
        }
        return Optional.of(rate);
    }

}

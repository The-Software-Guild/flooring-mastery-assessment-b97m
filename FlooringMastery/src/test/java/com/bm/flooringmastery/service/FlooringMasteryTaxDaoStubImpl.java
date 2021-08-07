package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryTaxDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Stub implementation of the Tax Dao for testing purposes
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 7, 2021
 */
public class FlooringMasteryTaxDaoStubImpl implements FlooringMasteryTaxDao {
    private final Map<String, BigDecimal> TAX_MAP;

    public FlooringMasteryTaxDaoStubImpl() {
        TAX_MAP = new HashMap<>();
        TAX_MAP.put("WA", new BigDecimal("9.77"));
        TAX_MAP.put("FL", new BigDecimal("4.98"));
    }
    
    @Override
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException {
        // do nothing
    }

    @Override
    public Optional<BigDecimal> getPercentTaxRateForStateAbbr(String state) {
        Optional<BigDecimal> receivedVal;
        BigDecimal testVal = TAX_MAP.get(state);
        if (testVal == null) {
            receivedVal = Optional.empty();
        } else {
            receivedVal = Optional.of(testVal);
        }
        return receivedVal;
    }

    @Override
    public Set<String> stateAbbrSet() {
        Set<String> receivedSet = new HashSet<>();
        TAX_MAP.keySet().forEach(key -> receivedSet.add(key));
        return receivedSet;
    }
}

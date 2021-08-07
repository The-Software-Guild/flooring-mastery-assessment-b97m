package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryProductDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Stub implementation of the Product Dao for testing purposes
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 7, 2021
 */
public class FlooringMasteryProductDaoStubImpl implements FlooringMasteryProductDao  {
    private final Map<String, FlooringMasteryProduct> PROD_MAP;

    public FlooringMasteryProductDaoStubImpl() {
        this.PROD_MAP = new HashMap<>();
        PROD_MAP.put(
            "Concrete",
            new FlooringMasteryProduct(
                "Concrete",
                new BigDecimal("1.23"),
                new BigDecimal("4.56")
            )
        );
        PROD_MAP.put(
            "Construction Fungus",
            new FlooringMasteryProduct(
                "Construction Fungus",
                new BigDecimal("10.11"),
                new BigDecimal("12.13")
            )
        );
    }
    
    @Override
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException {
        // do nothing
    }

    @Override
    public Set<FlooringMasteryProduct> productsSet() {
        Set<FlooringMasteryProduct> receivedSet = new HashSet<>();
        PROD_MAP.values().forEach(prod -> receivedSet.add(prod));
        return receivedSet;
    }

    @Override
    public Optional<FlooringMasteryProduct> getProductByType(String type) {
        Optional<FlooringMasteryProduct> receivedVal;
        FlooringMasteryProduct testVal = PROD_MAP.get(type);
        if (testVal == null) {
            receivedVal = Optional.empty();
        } else {
            receivedVal = Optional.of(testVal);
        }
        return receivedVal;
    }
}

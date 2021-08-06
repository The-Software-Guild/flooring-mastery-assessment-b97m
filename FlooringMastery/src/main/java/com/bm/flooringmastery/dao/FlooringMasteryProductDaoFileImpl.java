package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * An implementation of the ProductDao interface
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryProductDaoFileImpl implements FlooringMasteryProductDao {
    private final String SRC_FILE;
    private final Map<String, FlooringMasteryProduct> PRODUCTS_MAP;
    
    public FlooringMasteryProductDaoFileImpl() {
        this("Data/Products.txt");
    }
    
    public FlooringMasteryProductDaoFileImpl(String SRC_FILE) {
        this.SRC_FILE = SRC_FILE;
        PRODUCTS_MAP = new HashMap<>();
    }
    
    @Override
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException {
        Scanner reader;
        try {
            reader = new Scanner(new BufferedReader(new FileReader(SRC_FILE)));
        } catch (FileNotFoundException ex) {
            throw new FlooringMasteryFailedLoadException(
                "Unable to load product information"
            );
        }
        
        // ignore header
        reader.nextLine();
        
        while(reader.hasNextLine()) {
            String[] tokens = reader.nextLine().split(",");
            String type = tokens[0];
            BigDecimal costPerSqFt = new BigDecimal(tokens[1]);
            BigDecimal laborCostPerSqFt = new BigDecimal(tokens[2]);
            
            PRODUCTS_MAP.put(
                type,
                new FlooringMasteryProduct(type, costPerSqFt, laborCostPerSqFt)
            );
        }
        
        reader.close();
    }

    @Override
    public Set<FlooringMasteryProduct> productsSet() {
        return Set.copyOf(PRODUCTS_MAP.values());
    }

    @Override
    public boolean hasProductWithType(String type) {
        return PRODUCTS_MAP.containsKey(type);
    }

    @Override
    public Optional<FlooringMasteryProduct> getProductByType(String type) {
        FlooringMasteryProduct product = PRODUCTS_MAP.get(type);
        if (product == null) {
            return Optional.empty();
        }
        return Optional.of(product);
    }
}

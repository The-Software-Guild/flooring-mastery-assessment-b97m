package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.util.Optional;
import java.util.Set;

/**
 * A Dao for gathering and storing product information
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public interface FlooringMasteryProductDao {
    /**
     * Loads product data from an external source into memory
     * 
     * If the loading is unsuccessful, the below exception will be thrown
     * 
     * @throws com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException
     */
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException;
    
    /**
     * @return A Set of all of the products
     */
    public Set<FlooringMasteryProduct> productsSet();
    
    /**
     * @param type
     * @return Whether or not there is a product with this type
     */
    public boolean hasProductWithType(String type);
    
    /**
     * Attempts to obtain a product with the associated type
     * 
     * If there is such a product, an instance containing it is returned
     * 
     * Otherwise, an empty instance is returned
     * 
     * @param type
     * @return The aforementioned instances
     */
    public Optional<FlooringMasteryProduct> getProductByType(String type);
}

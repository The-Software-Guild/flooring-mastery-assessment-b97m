package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * A Dao for gathering and storing Tax information
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public interface FlooringMasteryTaxDao {
    /**
     * Loads tax data from an external source into memory
     * 
     * If the loading is unsuccessful, the below exception will be thrown
     * 
     * @throws com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException
     */
    public void loadDataFromExternals() throws FlooringMasteryFailedLoadException;
    
    /**
     * @param state
     * @return Whether or not the indicated state has tax information
     */
    public boolean hasInfoForStateAbbr(String state);
    
    /**
     * Attempts to obtain the percentage tax rate for the indicated state
     * 
     * If no information exists for such a state, an empty instance will be
     * returned.
     * 
     * Otherwise, an instance containing the percent tax rate for that state is
     * returned.
     * 
     * @param state
     * @return The aformentioned instances
     */
    public Optional<BigDecimal> percentTaxRateForStateAbbr(String state);
}

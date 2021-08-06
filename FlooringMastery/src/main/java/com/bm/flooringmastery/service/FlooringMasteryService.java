package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryOrderDao;
import com.bm.flooringmastery.dao.FlooringMasteryProductDao;
import com.bm.flooringmastery.dao.FlooringMasteryTaxDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Acts as the Service Layer for this application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryService {
    private FlooringMasteryTaxDao taxDao;
    private FlooringMasteryProductDao prodDao;
    private FlooringMasteryOrderDao orderDao;

    public FlooringMasteryService(FlooringMasteryTaxDao taxDao, FlooringMasteryProductDao prodDao, FlooringMasteryOrderDao orderDao) {
        this.taxDao = taxDao;
        this.prodDao = prodDao;
        this.orderDao = orderDao;
    }
    
    /**
     * Loads each of its Daos
     * 
     * If any of the Daos fail to be loaded properly, the below exception will
     * be thrown
     * 
     * @throws FlooringMasteryFailedLoadException 
     */
    public void loadDaos() throws FlooringMasteryFailedLoadException {
        List<String> loadErrs = new LinkedList<>();
        
        try {
            taxDao.loadDataFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            loadErrs.add(ex.getMessage());
        }
        
        try {
            prodDao.loadDataFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            loadErrs.add(ex.getMessage());
        }
        
        try {
            orderDao.loadFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            loadErrs.add(ex.getMessage());
        }
        
        if (!loadErrs.isEmpty()) {
            throw new FlooringMasteryFailedLoadException(
                loadErrs.stream().collect(Collectors.joining(", "))
            );
        }
    }

    /**
     * Tests if the state abbreviation has tax data
     * 
     * @param abbr
     * @return true if there is, false otherwise
     */
    public boolean hasTaxDataForStateAbbr(String abbr) {
        return taxDao.hasInfoForStateAbbr(abbr);
    }
    
    /**
     * Attempts to obtain the tax information corresponding to this
     * state abbreviation
     * 
     * If such tax information exists, then an instance containing
     * the percent tax rate is returned.
     * 
     * Otherwise, an empty instance is returned
     * 
     * @param abbr
     * @return The aforementioned instances
     */
    public Optional<BigDecimal> percentTaxRateForStateAbbr(String abbr) {
        return taxDao.percentTaxRateForStateAbbr(abbr);
    }
    
    /**
     * @return The set of all products in inventory
     */
    public Set<FlooringMasteryProduct> productsSet() {
        return prodDao.productsSet();
    }
    
    /**
     * Whether or not there is a product with this type in the inventory
     * 
     * @param type
     * @return true if so, false otherwise
     */
    public boolean hasProductWithType(String type) {
        return prodDao.hasProductWithType(type);
    }

    /**
     * Attempts to get the product corresponding to this type
     * 
     * If such a product exists, an instance containing it is returned
     * 
     * Otherwise, an empty instance is returned
     * 
     * @param type
     * @return The aforementioned instances
     */
    public Optional<FlooringMasteryProduct> getProductByType(String type) {
        return prodDao.getProductByType(type);
    }

    public Set<FlooringMasteryOrder> ordersSet() {
        return orderDao.ordersSet();
    }
    
    /**
     * Returns all orders that whose date matches the given date
     * 
     * @param date
     * @return The aforementioned orders
     */
    public Set<FlooringMasteryOrder> getOrdersByDate(LocalDate date) {
        return orderDao.ordersSet().stream()
                .filter((FlooringMasteryOrder order) -> {
                    return order.getOrderDate().equals(date);
                })
                .collect(Collectors.toSet());
    }

    /**
     * Pushes the indicated order into the collection
     * @param order 
     */
    public void pushOrder(FlooringMasteryOrder order) {
        orderDao.pushOrder(order);
    }
}

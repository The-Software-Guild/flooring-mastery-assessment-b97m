package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryOrderDao;
import com.bm.flooringmastery.dao.FlooringMasteryProductDao;
import com.bm.flooringmastery.dao.FlooringMasteryTaxDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
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
}

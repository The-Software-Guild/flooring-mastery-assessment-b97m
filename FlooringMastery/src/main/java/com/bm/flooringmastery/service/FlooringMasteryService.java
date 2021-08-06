package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryAuditDao;
import com.bm.flooringmastery.dao.FlooringMasteryOrderDao;
import com.bm.flooringmastery.dao.FlooringMasteryProductDao;
import com.bm.flooringmastery.dao.FlooringMasteryTaxDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedAuditEntryException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedExportException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderAlreadyExistsException;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderNotPresentForReplacementException;
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
    private FlooringMasteryAuditDao auditDao;

    public FlooringMasteryService(
        FlooringMasteryTaxDao taxDao,
        FlooringMasteryProductDao prodDao,
        FlooringMasteryOrderDao orderDao,
        FlooringMasteryAuditDao auditDao) {
        
        this.taxDao = taxDao;
        this.prodDao = prodDao;
        this.orderDao = orderDao;
        this.auditDao = auditDao;
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
        
        recordEvent("Loading data...");
        
        try {
            taxDao.loadDataFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            recordEvent("Loading of tax data unsuccessful");
            loadErrs.add(ex.getMessage());
        }
        
        try {
            prodDao.loadDataFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            recordEvent("Loading of product data unsuccessful");
            loadErrs.add(ex.getMessage());
        }
        
        try {
            orderDao.loadFromExternals();
        } catch (FlooringMasteryFailedLoadException ex) {
            recordEvent("Loading of order data unsuccessful");
            loadErrs.add(ex.getMessage());
        }
        
        if (!loadErrs.isEmpty()) {
            throw new FlooringMasteryFailedLoadException(
                loadErrs.stream().collect(Collectors.joining(", "))
            );
        }
        
        recordEvent("Loading data successful");
    }

    public Set<String> stateAbbrSet() {
        recordEvent("State abbreviations queried");
        return taxDao.stateAbbrSet();
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
    public Optional<BigDecimal> getPercentTaxRateForStateAbbr(String abbr) {
        recordEvent("% Tax rate for " + abbr + " queried");
        return taxDao.getPercentTaxRateForStateAbbr(abbr);
    }
    
    /**
     * @return The set of all products in inventory
     */
    public Set<FlooringMasteryProduct> productsSet() {
        recordEvent("Products queried");
        return prodDao.productsSet();
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
        recordEvent("Product type " + type + " queried");
        return prodDao.getProductByType(type);
    }

    /**
     * @return The latest available order number
     */
    public int latestOrderNum() {
        recordEvent("Computed latest order number");
        int currMaxNum = orderDao.ordersSet().stream()
                            .map(x -> x.getOrderNum())
                            .reduce(1, (a, b) -> Math.max(a, b));
        return currMaxNum + 1;
    }
    
    /**
     * @return The set of all Orders in the collection
     */
    public Set<FlooringMasteryOrder> ordersSet() {
        recordEvent("Orders queried");
        return orderDao.ordersSet();
    }
    
    /**
     * Returns all orders that whose date matches the given date
     * 
     * @param date
     * @return The aforementioned orders
     */
    public Set<FlooringMasteryOrder> getOrdersByDate(LocalDate date) {
        recordEvent("Orders for " + date.toString() + " queried");
        return orderDao.ordersSet().stream()
                .filter((FlooringMasteryOrder order) -> {
                    return order.getOrderDate().equals(date);
                })
                .collect(Collectors.toSet());
    }

    /**
     * Attempts to push a new order into the collection
     * 
     * If there already exists an order with the same number and date
     * as the new one, the below exception will be thrown
     * 
     * @param order 
     * @throws com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderAlreadyExistsException
     */
    public void pushOrder(FlooringMasteryOrder order) throws FlooringMasteryOrderAlreadyExistsException {
        recordEvent("Attempting push of order");
        if (orderDao.pushOrder(order).isEmpty()) {
            recordEvent("Push failed");
            throw new FlooringMasteryOrderAlreadyExistsException(
                "It appears there is already an order with the same date and "
                        + "number in the collection"
            );
        }
        recordEvent("Push succeeded");
    }
    
    /**
     * Attempts to replace an order, A, in the collection with the same date 
     * and number as the passed in order with the passed in order
     * 
     * If there is no such order A, the below exception will be thrown
     * 
     * @param order
     * @throws com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderNotPresentForReplacementException
     */
    public void replaceOrder(FlooringMasteryOrder order) throws FlooringMasteryOrderNotPresentForReplacementException {
        recordEvent("Attempting replacement of order");
        if (orderDao.replaceOrder(order).isEmpty()) {
            recordEvent("Replacement failed");
            throw new FlooringMasteryOrderNotPresentForReplacementException(
                "It appears there is no order with the same date and number in "
                + "the collection that can be replaced by the new order"
            );
        }
        recordEvent("Replacement succeeded");
    }

    /**
     * Attempts to retrieve an order in the collection with the matching 
     * date and number
     * 
     * If no such order can be found, an empty instance is returned
     * 
     * Otherwise, an instance with the matching order is returned
     * 
     * @param date
     * @param num
     * @return The aforementioned instances
     */
    public Optional<FlooringMasteryOrder> getOrderByDateAndNumber(LocalDate date, int num) {
        recordEvent("Order for " + num + " on " + date.toString() + " queried");
        return orderDao.getOrderByDateAndNumber(date, num);
    }
    
    /**
     * Attempts to remove the order in the collection with the matching
     * date and number
     * 
     * If no such order can be found, an empty instance is returned.
     * 
     * Otherwise, the matching order is removed from the collection, and an 
     * instance containing the removed order if returned.
     * 
     * @param date
     * @param num
     * @return The aforementioned instances 
     */
    public Optional<FlooringMasteryOrder> removeOrderByDateAndNumber(LocalDate date, int num) {
        recordEvent("Removal for order " + num + " on " + date.toString() + " attempted");
        return orderDao.removeOrderByDateAndNumber(date, num);
    }
    
    /**
     * Saves the orders to an external source
     * 
     * If this saving fails, the below exception will be thrown
     * 
     * @throws FlooringMasteryFailedSaveException 
     */
    public void saveOrdersToExternals() throws FlooringMasteryFailedSaveException {
        recordEvent("Saving of orders attempted");
        orderDao.saveToExternals();
        recordEvent("Saving succeeded");
    }
    
    /**
     * Exports the orders to some external source
     * 
     * If this exporting fails, the below exception will be thrown
     * 
     * @throws FlooringMasteryFailedExportException 
     */
    public void exportOrders() throws FlooringMasteryFailedExportException {
        recordEvent("Exporting of orders attempted");
        orderDao.export();
        recordEvent("Exporting succeeded");
    }

    private void recordEvent(String event) {
        try {
            auditDao.appendLine(event);
        } catch (FlooringMasteryFailedAuditEntryException ex) {
        }
    }
}

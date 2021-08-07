package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedExportException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * A Dao for gathering and storing Order information
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public interface FlooringMasteryOrderDao {
    /**
     * Loads all Orders from external sources into memory
     * 
     * If the loading is unsuccessful, the below exception will be thrown.
     * 
     * @throws com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException
     */
    public void loadFromExternals() throws FlooringMasteryFailedLoadException;
    
    /**
     * Attempts to add a new Order to the collection.
     * 
     * If the input is null, an empty instance is returned
     * 
     * If there already exists an Order with the same date and number
     * in the collection, an empty instance will be returned
     * 
     * Otherwise, an instance containing the passed in Order will be 
     * returned
     * 
     * @param order 
     * @return The aforementioned instance
     */
    public Optional<FlooringMasteryOrder> pushOrder(FlooringMasteryOrder order);
    
    /**
     * Attempts to replace an order with the same date and number in the
     * collection as the new order with the new order.
     * 
     * If the input is null, an empty instance is returned
     * 
     * If there is no such order in the collection, an empty instance will
     * be returned.
     * 
     * Otherwise, an instance containing the passed in order will be 
     * returned
     * 
     * @param order
     * @return The aforementioned instance
     */
    public Optional<FlooringMasteryOrder> replaceOrder(FlooringMasteryOrder order);
    
    /**
     * @return The set of all Orders in this collection
     */
    public Set<FlooringMasteryOrder> ordersSet();
    
    /**
     * @param date
     * @return All the Orders associated with this date
     */
    public Set<FlooringMasteryOrder> ordersByDate(LocalDate date);
    
    /**
     * Attempts to obtain an Order matching the date and number
     * 
     * If no such Order can be found, an empty instance will be returned
     * 
     * Otherwise, an instance containing the matching Order will be returned
     * 
     * @param date
     * @param num
     * @return The aforementioned instances
     */
    public Optional<FlooringMasteryOrder> getOrderByDateAndNumber(LocalDate date, int num);
    
    /**
     * Attempts to remove an Order matching the date and number
     * 
     * If no such Order can be found, an empty instance is returned
     * 
     * Otherwise, the matching Order is removed and then returned
     * 
     * @param date
     * @param num
     * @return 
     */
    public Optional<FlooringMasteryOrder> removeOrderByDateAndNumber(LocalDate date, int num);
    
    /**
     * Saves all Orders to external sources
     * 
     * If the saving is unsuccessful, the below exception will be thrown
     * 
     * @throws com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException
     */
    public void saveToExternals() throws FlooringMasteryFailedSaveException;
    
    /**
     * Exports all Orders
     * 
     * If this exporting fails, the below exception will be thrown
     * @throws FlooringMasteryFailedExportException 
     */
    public void export() throws FlooringMasteryFailedExportException;
}

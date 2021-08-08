package com.bm.flooringmastery.controller;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedExportException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import com.bm.flooringmastery.service.FlooringMasteryService;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderAlreadyExistsException;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderNotPresentForReplacementException;
import com.bm.flooringmastery.view.FlooringMasteryView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Acts as the controller for this application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
@Component
public class FlooringMasteryController {
    private final FlooringMasteryView VIEW;
    private final FlooringMasteryService SERVICE;

    private final RoundingMode COMMON_ROUNDING_MODE = RoundingMode.HALF_UP;
    
    @Autowired
    public FlooringMasteryController(FlooringMasteryView VIEW, FlooringMasteryService SERVICE) {
        this.VIEW = VIEW;
        this.SERVICE = SERVICE;
    }
    
    public void run() {
        
        try {
            SERVICE.loadDaos();
        } catch (FlooringMasteryFailedLoadException ex) {
            VIEW.displayErrorLine(ex.getMessage());
        }
        
        boolean active = true;
        while (active) {
            displayMainMenu();
            int choice = VIEW.getInt(
                "Select an option", 
                val -> (1 <= val && val <= 6),
                "The input must be one of the numbers above"
            );
            switch (choice) {
                case 1:
                    displayOrders();
                    break;
                case 2:
                    addOrder();
                    break;
                case 3:
                    editOrder();
                    break;
                case 4:
                    removeOrder();
                    break;
                case 5:
                    exportOrders();
                    break;
                case 6:
                    VIEW.displayInformationalLine("Exiting application...");
                    active = false;
                    break;
                default:
                    VIEW.displayErrorLine("UNKNOWN COMMAND");
            }
        }
        
        try {
            SERVICE.saveOrdersToExternals();
        } catch (FlooringMasteryFailedSaveException ex) {
            VIEW.displayErrorLine("Unable to save orders");
        }
        
        VIEW.close();
    }

    private void displayOrders() {
        LocalDate filterDate = VIEW.getLocalDate(
            "Enter a valid date (yyyy-mm-dd) to select orders from",
            val -> true,
            "The entered input was invalid"
        );
        Set<FlooringMasteryOrder> orders = SERVICE.getOrdersByDate(filterDate);
        if (orders.isEmpty()) {
            VIEW.displayErrorLine("There are no orders with this date");
        } else {
            orders.forEach(order -> displayOrder(order));
        }
        pauseBeforeContinuation();
    }
    
    private void displayOrder(FlooringMasteryOrder order) {
        VIEW.displayAroundContents(
            "Order " + order.getOrderNum(),
            "Date: " + order.getOrderDate(),
            "Customer: " + order.getCustomerName(),
            "Product Type: " + order.getProductType(),
            "Cost per sq. ft.: $" + order.getCostPerSqFt(),
            "Labor cost per sq. ft.: $" + order.getLaborCostPerSqFt(),
            "Area ordered: " + order.getArea(),
            "State: " + order.getState(),
            "State's % Tax Rate: " + order.getPercentTaxRate(),
            "----------------------",
            "Material cost: $" + order.getMaterialCost().setScale(2, COMMON_ROUNDING_MODE).toString(),
            "Labor cost: $" + order.getLaborCost().setScale(2, COMMON_ROUNDING_MODE).toString(),
            "Tax: $" + order.getTax().setScale(2, COMMON_ROUNDING_MODE).toString(),
            "Total cost: $" + order.getTotal().setScale(2, COMMON_ROUNDING_MODE)
        );
    }
    
    private void addOrder() {
        LocalDate orderDate = VIEW.getLocalDate(
            "Enter a future date (yyyy-mm-dd) for the order",
            date -> LocalDate.now().isBefore(date),
            "The entered date was invalid or not in in the future"
        );

        int orderNum = SERVICE.latestOrderNum();
        
        // customer name
        String customerName = VIEW.getString(
            "Enter the customer's name for the order", 
            str -> {
                String trimmed = str.trim();
                return !(trimmed.isEmpty() || trimmed.contains("::"));
            },
            "The name must be nonempty and must not contain the sequence \"::\""
        );
        customerName = customerName.trim();
        
        // state abbr
        VIEW.displayInformationalLine("States Available for Service");
        SERVICE.stateAbbrSet().forEach(abbr -> VIEW.displayLine(abbr));
        String abbr = VIEW.getString(
            "Enter a State abbreviation for the order",
            str -> SERVICE.getPercentTaxRateForStateAbbr(str).isPresent(),
            "Either the input was not a state abbreviation, or there is "
            + "insufficient tax data for that state"
        );
        
        // % tax rate
        BigDecimal percentTaxRate = SERVICE.getPercentTaxRateForStateAbbr(abbr).get();
        
        // product
        VIEW.displayInformationalLine("Available Products");
        SERVICE.productsSet().forEach(product -> {
            VIEW.displayAroundContents(
                product.getType(),
                "Cost per sq. ft.: $" + product.getCostPerSqFt().toString(),
                "Labor cost per sq. ft.: $" + product.getLaborCostPerSqFt().toString()
            );
        });
        String prodType = VIEW.getString(
            "Choose the floor type for this order",
            str -> SERVICE.getProductByType(str).isPresent(),
            "That floor type is not available"
        );
        FlooringMasteryProduct orderedProd = SERVICE.getProductByType(prodType).get();
        
        // area
        BigDecimal area = VIEW.getBigDecimal(
            "Enter total area (in sq. ft.) demanded for this floor type in this"
            + " order (Min Allowed: 100)", 
            val -> val.compareTo(new BigDecimal("100")) >= 0,
            "The input must be some area no less than 100 sq. ft."
        );
         
        FlooringMasteryOrder order = new FlooringMasteryOrder(
            orderDate,
            orderNum,
            customerName,
            abbr,
            percentTaxRate,
            orderedProd,
            area
        );
        
        VIEW.displayInformationalLine("Order Review");
        displayOrder(order);
        String yesNo = VIEW.getString(
            "Submit order? (Y/n)",
            str -> str.equals("Y") || str.equals("n"),
            "Please enter \"Y\" or \"n\""
        );
        if (yesNo.equals("Y")) {
            try {
                SERVICE.pushOrder(order);
                VIEW.displayInformationalLine("Order submitted");
            } catch (FlooringMasteryOrderAlreadyExistsException ex) {
                VIEW.displayErrorLine(ex.getMessage());
                VIEW.displayInformationalLine("Order not submitted");
            }
        } else {
            VIEW.displayInformationalLine("Order not submitted");
        }
        VIEW.displayInformationalLine("Returning to Main Menu");
        pauseBeforeContinuation();
    }
    
    private void editOrder() {
        LocalDate filterDate = VIEW.getLocalDate(
            "Enter a valid date (yyyy-mm-dd) of the order to edit", 
            val -> true, 
            "The entered input was invalid"
        );
        
        int orderNum = VIEW.getInt(
            "Enter the order number of the order to edit",
            val -> true, 
            "The entered input was invalid"
        );
        
        SERVICE.getOrderByDateAndNumber(filterDate, orderNum).ifPresentOrElse(
            order -> {
                VIEW.displayInformationalLine("Current Order Data");
                displayOrder(order);
                
                // customer name replacement
                String customerName = VIEW.getStringReplacement(
                    "Enter the customer's name for the order, or enter nothing"
                    + " to maintain current value", 
                    str -> {
                        String trimmed = str.trim();
                        return !(trimmed.isEmpty() || trimmed.contains("::"));
                    },
                    "The new name must be nonempty and must not contain the "
                    + "sequence \"::\"",
                    order.getCustomerName()
                );
                customerName = customerName.trim();
                
                 // state abbr
                VIEW.displayInformationalLine("States Available for Service");
                SERVICE.stateAbbrSet().forEach(abbr -> VIEW.displayLine(abbr));
                String abbr = VIEW.getStringReplacement(
                    "Enter a State abbreviation for the order, or enter nothing"
                    + " to maintain current value",
                    str -> SERVICE.getPercentTaxRateForStateAbbr(str).isPresent(),
                    "Either the input was not a state abbreviation, or there is "
                    + "insufficient tax data for that state",
                    order.getState()
                );
                
                // % tax rate recomputed
                BigDecimal percentTaxRate = SERVICE.getPercentTaxRateForStateAbbr(abbr).get();
                
                // product replacement
                VIEW.displayInformationalLine("Available Products");
                SERVICE.productsSet().forEach(product -> {
                    VIEW.displayAroundContents(
                        product.getType(),
                        "Cost per sq. ft.: $" + product.getCostPerSqFt().toString(),
                        "Labor cost per sq. ft.: $" + product.getLaborCostPerSqFt().toString()
                    );
                });        
                String prodType = VIEW.getStringReplacement(
                    "Choose the floor type for this order, or enter nothing to"
                    + " maintain current value", 
                    str -> SERVICE.getProductByType(str).isPresent(),
                    "That floor type is not available",
                    order.getProductType()
                );
                FlooringMasteryProduct orderedProd = SERVICE.getProductByType(prodType).get();
                
                // area replacement
                BigDecimal area = VIEW.getBigDecimalReplacement(
                    "Enter total area (in sq. ft.) demanded for this floor type in this"
                    + " order (Min Allowed: 100), or enter nothing to maintain"
                    + " current value", 
                    val -> val.compareTo(new BigDecimal("100")) >= 0,
                    "The input must be some area no less than 100 sq. ft.",
                    order.getArea()
                );
                
                FlooringMasteryOrder newOrder = new FlooringMasteryOrder(
                    order.getOrderDate(),
                    order.getOrderNum(),
                    customerName,
                    abbr,
                    percentTaxRate,
                    orderedProd,
                    area
                );
                
                VIEW.displayInformationalLine("Order Edit Review");
                VIEW.displayInformationalLine("Order was");
                displayOrder(order);
                VIEW.displayInformationalLine("Order will now be");
                displayOrder(newOrder);
                
                String yesNo = VIEW.getString(
                    "Submit changes? (Y/n)",
                    str -> str.equals("Y") || str.equals("n"),
                    "Please enter \"Y\" or \"n\""
                );
                if (yesNo.equals("Y")) {
                    try {
                        SERVICE.replaceOrder(newOrder);
                        VIEW.displayInformationalLine("Order changes submitted");
                    } catch (FlooringMasteryOrderNotPresentForReplacementException ex) {
                        VIEW.displayErrorLine(ex.getMessage());
                        VIEW.displayInformationalLine("Order changes not submitted");
                    }
                } else {
                    VIEW.displayInformationalLine("Order changes not submitted");
                }
            },
            () -> {
                VIEW.displayErrorLine(
                    "It appears that the indicated order with that date and "
                    + "number does not exist"
                );
            }
        );
        pauseBeforeContinuation();
    }
    
    private void removeOrder() {
        LocalDate filterDate = VIEW.getLocalDate(
            "Enter a valid date (yyyy-mm-dd) of the order to remove", 
            val -> true, 
            "The entered input was invalid"
        );
        
        int orderNum = VIEW.getInt(
            "Enter the order number of the order to remove",
            val -> true, 
            "The entered input was invalid"
        );
        
        SERVICE.getOrderByDateAndNumber(filterDate, orderNum).ifPresentOrElse(
            order -> {
                VIEW.displayInformationalLine("Preparing to delete order");
                displayOrder(order);
                String yesNo = VIEW.getString(
                    "Are you sure you want to delete this order? (Y/n)", 
                    str -> str.equals("Y") || str.equals("n"), 
                    "Please enter \"Y\" or \"n\""
                );
                if (yesNo.equals("Y")) {
                    SERVICE.removeOrderByDateAndNumber(filterDate, orderNum).ifPresentOrElse(
                        removedOrder -> VIEW.displayErrorLine("Order Successfully deleted"),
                        () -> VIEW.displayErrorLine("Order deletion failed")
                    );
                } else {
                    VIEW.displayInformationalLine("Order deletion cancelled");
                }
            }, 
            () -> VIEW.displayErrorLine("That order does not exist")
        );

        pauseBeforeContinuation();
    }
    
    private void exportOrders() {
        try {
            SERVICE.exportOrders();
            VIEW.displayInformationalLine("Exporting Successful");
        } catch (FlooringMasteryFailedExportException ex) {
            VIEW.displayErrorLine("Exporting failed");
        }
        pauseBeforeContinuation();
    }
    
    private void pauseBeforeContinuation() {
        VIEW.getString("Press ENTER to Continue", val -> true, "");
    }
    
    private void displayMainMenu() {
        VIEW.displayAroundContents(
            "Flooring Program",
            "1. Display Orders",
            "2. Add an Order",
            "3. Edit an Order",
            "4. Remove an Order",
            "5. Export All Data",
            "6. Quit"
        );
    }    
}

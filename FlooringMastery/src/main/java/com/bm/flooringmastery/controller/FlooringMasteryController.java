/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bm.flooringmastery.controller;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import com.bm.flooringmastery.service.FlooringMasteryService;
import com.bm.flooringmastery.view.FlooringMasteryView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;

/**
 * Acts as the controller for this application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryController {
    private final FlooringMasteryView VIEW;
    private final FlooringMasteryService SERVICE;

    private final RoundingMode COMMON_ROUNDING_MODE = RoundingMode.CEILING;
    
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
                    VIEW.displayLine("EDIT");
                    break;
                case 4:
                    VIEW.displayLine("REMOVE");
                    break;
                case 5:
                    VIEW.displayLine("EXPORT");
                    break;
                case 6:
                    VIEW.displayLine("QUIT");
                    active = false;
                    break;
                default:
                    VIEW.displayErrorLine("UNKNOWN COMMAND");
            }
        }
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
        
        int orderNum = SERVICE.ordersSet().stream()
            .map(order -> order.getOrderNum())
            .reduce(1, (a, b) -> Math.max(a, b));
        orderNum++;
        
        String customerName = VIEW.getString(
            "Enter the customer's name for the order", 
            str -> !str.isEmpty(),
            "The name must be nonempty"
        );
        
        String abbr = VIEW.getString(
            "Enter a State abbreviation for the order",
            str -> SERVICE.hasTaxDataForStateAbbr(str),
            "Either the input was not a state abbreviation, or there is "
            + "insufficient tax data for that state"
        );
        BigDecimal percentTaxRate = SERVICE.percentTaxRateForStateAbbr(abbr).get();
        
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
            str -> SERVICE.hasProductWithType(str),
            "That floor type is not available"
        );
        FlooringMasteryProduct orderedProd = SERVICE.getProductByType(prodType).get();
        
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
            SERVICE.pushOrder(order);
            VIEW.displayInformationalLine("Order submitted");
        } else {
            VIEW.displayLine("Order not submitted");
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

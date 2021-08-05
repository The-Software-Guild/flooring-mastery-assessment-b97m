/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bm.flooringmastery.controller;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.service.FlooringMasteryService;
import com.bm.flooringmastery.view.FlooringMasteryView;
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
                    VIEW.displayLine("ADD");
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

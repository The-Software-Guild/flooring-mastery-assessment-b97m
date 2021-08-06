package com.bm.flooringmastery;

import com.bm.flooringmastery.controller.FlooringMasteryController;
import com.bm.flooringmastery.dao.FlooringMasteryAuditDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryOrderDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryProductDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryTaxDaoFileImpl;
import com.bm.flooringmastery.service.FlooringMasteryService;
import com.bm.flooringmastery.view.FlooringMasteryView;
import com.bm.flooringmastery.view.UserIoConsoleImpl;

/**
 * Acts as the entry point of the whole application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class App {
    public static void main(String[] args) {
        FlooringMasteryController controller = new FlooringMasteryController(
            new FlooringMasteryView(
                new UserIoConsoleImpl()
            ),
            new FlooringMasteryService(
                new FlooringMasteryTaxDaoFileImpl(),
                new FlooringMasteryProductDaoFileImpl(),
                new FlooringMasteryOrderDaoFileImpl(),
                new FlooringMasteryAuditDaoFileImpl()
            )
        );
        
        controller.run();
    }
}

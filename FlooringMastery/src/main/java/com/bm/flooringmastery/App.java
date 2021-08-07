package com.bm.flooringmastery;

import com.bm.flooringmastery.controller.FlooringMasteryController;
import com.bm.flooringmastery.dao.FlooringMasteryAuditDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryOrderDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryProductDaoFileImpl;
import com.bm.flooringmastery.dao.FlooringMasteryTaxDaoFileImpl;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import com.bm.flooringmastery.service.FlooringMasteryService;
import com.bm.flooringmastery.view.FlooringMasteryView;
import com.bm.flooringmastery.view.UserIoConsoleImpl;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Acts as the entry point of the whole application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class App {
    public static void main(String[] args) throws FlooringMasteryFailedLoadException {
        /*
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
        */
        
        var ttt = new FlooringMasteryOrderDaoFileImpl(
            "Testing/Orders",
            "Testing/DataExport.txt"
        );
        
        ttt.loadFromExternals();
        
        FlooringMasteryOrder shiftOrder = new FlooringMasteryOrder(
            LocalDate.parse("2022-10-14"),
            21,
            "Shifting Sands Training Center",
            "OH",
            new BigDecimal("5.75"),
            new FlooringMasteryProduct(
                "Tile",
                new BigDecimal("3.50"),
                new BigDecimal("4.15")
            ),
            new BigDecimal("234")
        );
        
        System.out.println(ttt.pushOrder(shiftOrder).isPresent());
        System.out.println("FIN");
    }
}

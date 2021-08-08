package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedExportException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryOrderDaoFileImplTest {
    private FlooringMasteryOrderDao subject;
    
    private static final FlooringMasteryOrder NORMAN_BATES_ORDER = new FlooringMasteryOrder(
        LocalDate.parse("2019-02-23"),
        17,
        "Norman Bates",
        "WA",
        new BigDecimal("9.25"),
        new FlooringMasteryProduct(
            "Carpet",
            new BigDecimal("2.25"),
            new BigDecimal("2.10")
        ),
        new BigDecimal("100")
    );
    
    private static final FlooringMasteryOrder SAKHILA_ORDER = new FlooringMasteryOrder(
        LocalDate.parse("2021-02-23"),
        18,
        "Sakhila's Fabrics, Inc.",
        "KY",
        new BigDecimal("6.00"),
        new FlooringMasteryProduct(
            "Laminate",
            new BigDecimal("1.75"),
            new BigDecimal("2.10")
        ),
        new BigDecimal("200")
    );
    
    private static final FlooringMasteryOrder LUMBER_ORDER = new FlooringMasteryOrder(
        LocalDate.parse("2021-02-23"),
        20,
        "Lumber-X",
        "CA",
        new BigDecimal("25.00"),
        new FlooringMasteryProduct(
            "Tile",
            new BigDecimal("3.50"),
            new BigDecimal("4.15")
        ),
        new BigDecimal("175")
    );
    
    @BeforeEach
    public void setUp() throws IOException {
        // an empty orders file
        PrintWriter setupWriter = new PrintWriter(
            new FileWriter(
                "Testing/Orders/Orders_01012021.txt"
            )
        );
        setupWriter.println(
            "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::"
            + "CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::"
            + "LaborCost::Tax::Total"
        );
        setupWriter.close();
        
        // an order file with only one entry
        setupWriter = new PrintWriter(
            new FileWriter(
                "Testing/Orders/Orders_02232019.txt"
            )
        );
        setupWriter.println(
            "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::"
            + "CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::"
            + "LaborCost::Tax::Total"
        );
        setupWriter.println(
            "17::Norman Bates::WA::9.25::Carpet::100::"
            + "2.25::2.10::225::"
            + "210::40.24::475.24"
        );
        setupWriter.close();
        
        // an order file with multiple entries
        setupWriter = new PrintWriter(
            new FileWriter(
                "Testing/Orders/Orders_02232021.txt"
            )
        );
        setupWriter.println(
            "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::"
            + "CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::"
            + "LaborCost::Tax::Total"
        );
        setupWriter.println(
            "18::Sakhila's Fabrics, Inc.::KY::6.00::Laminate::200::"
            + "1.75::2.10::350::"
            + "420::46.20::816.20"
        );
        setupWriter.println(
            "20::Lumber-X::CA::25.00::Tile::175::"
            + "3.50::4.15::612.50::"
            + "726.25::334.69::1673.44"
        );
        setupWriter.close();
        
        subject = new FlooringMasteryOrderDaoFileImpl(
            "Testing/Orders",
            "Testing/DataExport.txt"
        );
    }
    
    @Test
    public void testLoadAndOrdersSet() throws FlooringMasteryFailedLoadException {
        for (int i = 0; i < 20; i++) {
            subject.loadFromExternals();
            Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();

            // verify the contents of the set
            assertEquals(ordersSet.size(), 3, "There should be three elements");
            assertTrue(
                ordersSet.contains(NORMAN_BATES_ORDER),
                "The set should contain Norman Bates' Carpet Order"
            );

            assertTrue(
                ordersSet.contains(SAKHILA_ORDER),
                "The set should contain Sakhila's Laminate Order"
            );

            assertTrue(
                ordersSet.contains(LUMBER_ORDER),
                "The set should contain Lumber-X''s Tile Order"
            );
        }
    }

    @Test
    public void testLoadAndOrdersByDate() throws FlooringMasteryFailedLoadException {
        for (int i = 0; i < 20; i++) {
            subject.loadFromExternals();
            assertTrue(
                subject.ordersByDate(null).isEmpty(),
                "There should be no orders for a null date"
            );

            assertTrue(
                subject.ordersByDate(LocalDate.parse("1967-01-01")).isEmpty(),
                "There should be no orders for this date"
            );

            // check the empty orders file
            Set<FlooringMasteryOrder> ordersSet = subject.ordersByDate(
                LocalDate.parse("2021-01-01")
            );
            assertTrue(
                ordersSet.isEmpty(),
                "There should be no orders for this date since the corresponding "
                + "file is empty"
            );

            // check the orders file with one order
            ordersSet = subject.ordersByDate(LocalDate.parse("2019-02-23"));
            assertEquals(ordersSet.size(), 1, "This set should have one element");
            assertTrue(
                ordersSet.contains(NORMAN_BATES_ORDER),
                "This set should contain Norman Bates' Carpet order"
            );

            // check the orders file with multiple orders
            ordersSet = subject.ordersByDate(LocalDate.parse("2021-02-23"));
            assertEquals(ordersSet.size(), 2, "This set should have two elements");
            assertTrue(
                ordersSet.contains(SAKHILA_ORDER),
                "This set should contain Sakhila's Laminate order"
            );
            assertTrue(
                ordersSet.contains(LUMBER_ORDER),
                "This set should contain Lumber-X's Tile order"
            );
        }
    }

    @Test
    public void testLoadAndOrdersByDateAndNum() throws FlooringMasteryFailedLoadException {
        for (int i = 0; i < 20; i++) {
            subject.loadFromExternals();

            assertTrue(
                subject.getOrderByDateAndNumber(null, 0).isEmpty(),
                "There should be no Orders with a null date"
            );

            assertTrue(
                subject.getOrderByDateAndNumber(
                    LocalDate.parse("1967-01-01"),
                    0
                ).isEmpty(),
                "There should be no Orders with a date of 1967-01-01"
            );

            assertTrue(
                subject.getOrderByDateAndNumber(
                    LocalDate.parse("2021-01-01"), 
                    0
                ).isEmpty(),
                "There should be no Orders with a date of 2021-01-01 even though "
                + "a file for 2021-01-01 exists"
            );

            assertTrue(
                subject.getOrderByDateAndNumber(
                    LocalDate.parse("2019-02-23"), 
                    16
                ).isEmpty(),
                "There should be no Orders with this date and number"
            );

            subject.getOrderByDateAndNumber(LocalDate.parse("2019-02-23"), 17).ifPresentOrElse(
                order -> assertEquals(
                    order, 
                    NORMAN_BATES_ORDER, 
                    "Norman Bates' Order should be given"
                ), 
                () -> fail("There should be an Order with this date and number")
            );

            subject.getOrderByDateAndNumber(LocalDate.parse("2021-02-23"), 18).ifPresentOrElse(
                order -> assertEquals(
                    order, 
                    SAKHILA_ORDER, 
                    "Sakhila's Order should be given"
                ), 
                () -> fail("There should be an Order with this date and number")
            );

            subject.getOrderByDateAndNumber(LocalDate.parse("2021-02-23"), 20).ifPresentOrElse(
                order -> assertEquals(
                    order, 
                    LUMBER_ORDER, 
                    "Lumber-X's Order should be given"
                ), 
                () -> fail("There should be an Order with this date and number")
            );
        }
    }
    
    @Test
    public void testLoadAndPush() throws FlooringMasteryFailedLoadException {
        subject.loadFromExternals();

        assertTrue(
            subject.pushOrder(null).isEmpty(),
            "null should not be pushed"
        );

        assertTrue(
            subject.pushOrder(LUMBER_ORDER).isEmpty(),
            "An order with the same date and number as an existing order should"
            + " not be pushed"
        );

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
        assertTrue(
            subject.pushOrder(shiftOrder).isPresent(),
            "This order should be pushed"
        );

        assertTrue(
            subject.pushOrder(shiftOrder).isEmpty(),
            "Pushing the same order should fail"
        );
        
        FlooringMasteryOrder drossOrder = new FlooringMasteryOrder(
            LocalDate.parse("2022-10-14"),
            23,
            "Dross Shipping",
            "Wood",
            new BigDecimal("4.45"),
            new FlooringMasteryProduct(
                "Tile",
                new BigDecimal("5.15"),
                new BigDecimal("4.75")
            ),
            new BigDecimal("567.5")
        );
        
        assertTrue(
            subject.pushOrder(drossOrder).isPresent(),
            "This order should be pushed also"
        );

        assertTrue(
            subject.pushOrder(drossOrder).isEmpty(),
            "Pushing the same order should fail"
        );
        
        Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();
        // verify the contents of the set
        assertEquals(ordersSet.size(), 5, "There should be four elements");
        assertTrue(
            ordersSet.contains(NORMAN_BATES_ORDER),
            "The set should contain Norman Bates' Carpet Order"
        );

        assertTrue(
            ordersSet.contains(SAKHILA_ORDER),
            "The set should contain Sakhila's Laminate Order"
        );

        assertTrue(
            ordersSet.contains(LUMBER_ORDER),
            "The set should contain Lumber-X''s Tile Order"
        );

        assertTrue(
            ordersSet.contains(shiftOrder),
            "The set should contain the pushed in order"
        );
        
        assertTrue(
            ordersSet.contains(drossOrder),
            "The set should contain this order"
        );
    }

    @Test
    public void testLoadAndReplace() throws FlooringMasteryFailedLoadException {
        subject.loadFromExternals();
        
        assertTrue(
            subject.replaceOrder(null).isEmpty(),
            "There should be no null elements, so replacing with null should fail"
        );
        
        assertTrue(
            subject.replaceOrder(
                new FlooringMasteryOrder(
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
                )
            ).isEmpty(),
            "No order has this date, so no replacement should occur"
        );
        
        assertTrue(
            subject.replaceOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-23"),
                    40,
                    "Lumber-Z",
                    "CA",
                    new BigDecimal("25.00"),
                    new FlooringMasteryProduct(
                        "Tile",
                        new BigDecimal("3.50"),
                        new BigDecimal("4.15")
                    ),
                    new BigDecimal("175")
                )
            ).isEmpty(),
            "There are no orders that match this date and id, so no replacement"
            + " should occur"
        );
        
        assertTrue(
            subject.replaceOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-23"),
                    20,
                    "Xander's Lumber",
                    "CA",
                    new BigDecimal("25.00"),
                    new FlooringMasteryProduct(
                        "Tile",
                        new BigDecimal("3.50"),
                        new BigDecimal("4.15")
                    ),
                    new BigDecimal("160")
                )
            ).isPresent(),
            "There is an order that matches this date and id, a replacement "
            + "should occur"
        );
        
        Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();
        assertEquals(ordersSet.size(), 3, "There should be three elements");
        assertTrue(
            ordersSet.contains(NORMAN_BATES_ORDER),
            "The set should contain Norman Bates' Carpet Order"
        );

        assertTrue(
            ordersSet.contains(SAKHILA_ORDER),
            "The set should contain Sakhila's Laminate Order"
        );

        assertTrue(
            ordersSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-23"),
                    20,
                    "Xander's Lumber",
                    "CA",
                    new BigDecimal("25.00"),
                    new FlooringMasteryProduct(
                        "Tile",
                        new BigDecimal("3.50"),
                        new BigDecimal("4.15")
                    ),
                    new BigDecimal("160")
                )
            ),
            "The set should contain the replacement order"
        );
    }

    @Test
    public void testLoadAndRemove() throws FlooringMasteryFailedLoadException {
        subject.loadFromExternals();
        
        assertTrue(
            subject.removeOrderByDateAndNumber(null, 0).isEmpty(),
            "There are no orders with a null date, so no removal should occur"
        );
        
        assertTrue(
            subject.removeOrderByDateAndNumber(
                LocalDate.parse("2021-01-01"),
                0
            ).isEmpty(),
            "No order has both this date and number, so no removal should occur"
        );
        
        assertTrue(
            subject.removeOrderByDateAndNumber(
                LUMBER_ORDER.getOrderDate(),
                LUMBER_ORDER.getOrderNum()
            ).isPresent(),
            "Lumber-X's order should be removed"
        );
        
        Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();
        
        assertEquals(
            ordersSet.size(), 
            2, 
            "There should be two elements in this set"
        );
        
        assertTrue(
            ordersSet.contains(NORMAN_BATES_ORDER),
            "Norman Bates' order should still be contained"
        );
        
        assertTrue(
            ordersSet.contains(SAKHILA_ORDER),
            "Sakhila's order should still be contained"
        );
    }

    @Test
    public void testLoadAndSave() throws FlooringMasteryFailedLoadException, FlooringMasteryFailedSaveException, IOException {
        subject.loadFromExternals();
        
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
        
        FlooringMasteryOrder drossOrder = new FlooringMasteryOrder(
            LocalDate.parse("2022-10-14"),
            23,
            "Dross Shipping",
            "Wood",
            new BigDecimal("4.45"),
            new FlooringMasteryProduct(
                "Tile",
                new BigDecimal("5.15"),
                new BigDecimal("4.75")
            ),
            new BigDecimal("567.5")
        );
        
        subject.pushOrder(drossOrder);
        subject.pushOrder(shiftOrder);
        
        Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();    
        subject.saveToExternals();
            
        ordersSet.forEach(
            order -> subject.removeOrderByDateAndNumber(
                order.getOrderDate(),
                order.getOrderNum()
            )
        );
        
        assertTrue(subject.ordersSet().isEmpty());
        
        subject.loadFromExternals();
        
        ordersSet = subject.ordersSet();
        
        assertEquals(
            ordersSet.size(), 
            5, 
            "There should be four elements in this set"
        );
        
        assertTrue(
            ordersSet.contains(NORMAN_BATES_ORDER),
            "The set should contain Norman Bates' Order"
        );
        
        assertTrue(
            ordersSet.contains(SAKHILA_ORDER),
            "The set should contain Sakhila's Order"
        );
        
        assertTrue(
            ordersSet.contains(LUMBER_ORDER),
            "The set should contain Lumber-X's Order"
        );
        
        assertTrue(
            ordersSet.contains(drossOrder),
            "The set should contain the pushed in order"
        );
        
        assertTrue(
            ordersSet.contains(shiftOrder),
            "The set should contain the other pushed in order"
        );
        
        // delete the file created for the new orders
        Files.delete(
            FileSystems.getDefault().getPath(
                "Testing/Orders/Orders_10142022.txt"
            )
        );
    }

    @Test
    public void testLoadAndExport() throws FlooringMasteryFailedLoadException, FlooringMasteryFailedExportException, FileNotFoundException {
        subject.loadFromExternals();
        subject.export();
        
        Scanner reader = new Scanner(
            new BufferedReader(
                new FileReader("Testing/DataExport.txt")
            )
        );
        
        Set<String> lines = new HashSet<>();
        
        while (reader.hasNextLine()) {
            lines.add(reader.nextLine());
        }
        reader.close();
        
        assertEquals(lines.size(), 4, "There should be four lines");
        assertTrue(
            lines.contains(
                "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::"
                + "CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::"
                + "LaborCost::Tax::Total::OrderDate"
            ),
            "There should be a header"
        );
        
        assertTrue(
            lines.contains(
                "17::Norman Bates::WA::9.25::Carpet::100::"
                + "2.25::2.10::225.00::"
                + "210.00::40.24::475.24::02-23-2019"
            ),
            "There should be information for Norman Bates' Order"
        );

        assertTrue(
            lines.contains(
                "18::Sakhila's Fabrics, Inc.::KY::6.00::Laminate::200::"
                + "1.75::2.10::350.00::"
                + "420.00::46.20::816.20::02-23-2021"
            ),
            "There shuld be information for Sakhila's Order"
        );
        
        assertTrue(
            lines.contains(
                "20::Lumber-X::CA::25.00::Tile::175::"
                + "3.50::4.15::612.50::"
                + "726.25::334.69::1673.44::02-23-2021"
            ),
            "There should be information for Lumber-X's Order"
        );
    }
}

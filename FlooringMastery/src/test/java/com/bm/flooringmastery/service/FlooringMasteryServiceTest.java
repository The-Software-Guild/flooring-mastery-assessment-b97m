package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderAlreadyExistsException;
import com.bm.flooringmastery.service.exceptions.FlooringMasteryOrderNotPresentForReplacementException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryServiceTest {
    
    private FlooringMasteryService subject;
    
    @BeforeEach
    public void setUp() {
        subject = new FlooringMasteryService(
            new FlooringMasteryTaxDaoStubImpl(),
            new FlooringMasteryProductDaoStubImpl(),
            new FlooringMasteryOrderDaoStubImpl(),
            new FlooringMasteryAuditDaoStubImpl()
        );
    }

    @Test
    public void testLoadAndTaxInfo() throws FlooringMasteryFailedLoadException {
        for (int i = 0; i < 10; i++) {
            subject.loadDaos();
            Set<String> stateAbbrSet = subject.stateAbbrSet();

            // check consistency with stub implementation
            assertEquals(stateAbbrSet.size(), 2, "There should be two elements");

            assertTrue(
                stateAbbrSet.contains("WA"),
                "WA should be a member of the set"
            );

            assertTrue(
                stateAbbrSet.contains("FL"),
                "WA should be a member of the set"
            );

            subject.getPercentTaxRateForStateAbbr("WA").ifPresentOrElse(
                val -> assertEquals(
                    val,
                    new BigDecimal("9.77"),
                    "The % Tax rate should be 9.77"
                ), () -> fail("There should be tax info for WA")
            );

            subject.getPercentTaxRateForStateAbbr("FL").ifPresentOrElse(
                val -> assertEquals(
                    val,
                    new BigDecimal("4.98"),
                    "The % Tax rate should be 4.98"
                ), () -> fail("There should be tax info for FL")
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr(null).isEmpty(),
                "There should be no tax info for null"
            );
            
            assertTrue(
                subject.getPercentTaxRateForStateAbbr("MT").isEmpty(),
                "There should be no tax info for MT"
            );
        }
    }
    
    @Test
    public void testLoadAndProdInfo() throws FlooringMasteryFailedLoadException {
        for (int i = 0; i < 20; i++) {
            subject.loadDaos();

            Set<FlooringMasteryProduct> prodSet = subject.productsSet();
            assertEquals(prodSet.size(), 2, "There should be two elements");
            assertTrue(
                prodSet.contains(
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    )
                ),
                "A Concrete product should be contained"
            );
            assertTrue(
                prodSet.contains(
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    )
                ),
                "A Construction Fungus product should be contained"
            );

            subject.getProductByType("Concrete").ifPresentOrElse(
                val -> assertEquals(
                    val, 
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    )                
                ), () -> fail("There should be information for this product type")
            );

            subject.getProductByType("Construction Fungus").ifPresentOrElse(
                val -> assertEquals(
                    val, 
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    )                
                ), () -> fail("There should be information for this product type")
            );

            assertTrue(
                subject.getProductByType(null).isEmpty(),
                "There should be no information for null"
            );

            assertTrue(
                subject.getProductByType("Emerald").isEmpty(),
                "There should be no information for this item"
            );
        }
    }
    
    @Test
    public void testLoadAndOrderInfo() throws FlooringMasteryFailedLoadException {
        subject.loadDaos();

        FlooringMasteryOrder solidyneOrder =  new FlooringMasteryOrder(
            LocalDate.parse("2021-02-02"),
            1,
            "Solidyne Areodynamics",
            "FL",
            new BigDecimal("4.98"),
            new FlooringMasteryProduct(
                "Concrete",
                new BigDecimal("1.23"),
                new BigDecimal("4.56")
            ),
            new BigDecimal("400")
        );
        
        FlooringMasteryOrder tablatOrder = new FlooringMasteryOrder(
            LocalDate.parse("2022-03-03"),
            2,
            "Tablat Acquisitions, Inc.",
            "WA",
            new BigDecimal("9.77"),
            new FlooringMasteryProduct(
                "Concrete",
                new BigDecimal("1.23"),
                new BigDecimal("4.56")
            ),
            new BigDecimal("234")
        );
        
        FlooringMasteryOrder plutonianOrder = new FlooringMasteryOrder(
            LocalDate.parse("2022-03-03"),
            3,
            "Plutonian Ice",
            "FL",
            new BigDecimal("4.98"),
            new FlooringMasteryProduct(
                "Construction Fungus",
                new BigDecimal("10.11"),
                new BigDecimal("12.13")
            ),
            new BigDecimal("101")
        );
        
        // check consistency of set
        Set<FlooringMasteryOrder> ordersSet = subject.ordersSet();
        assertEquals(ordersSet.size(), 3, "There should be three elements");
        assertTrue(
            ordersSet.contains(solidyneOrder), 
            "The set should contain Solidyne's order"
        );
        assertTrue(
            ordersSet.contains(tablatOrder),
            "The set should contain Tablat's order"
        );
        assertTrue(
            ordersSet.contains(plutonianOrder),
            "The set should contain Plutonian's order"
        );
        
        // check consistency of retrieval by dates
        assertTrue(
            subject.getOrdersByDate(null).isEmpty(),
            "There should be no orders with a null date"
        );        
        assertTrue(
            subject.getOrdersByDate(LocalDate.parse("1998-06-07")).isEmpty(),
            "There should be no orders for this date"
        );
        
        assertTrue(
            subject.getOrdersByDate(LocalDate.parse("2020-01-01")).isEmpty(),
            "There should be no orders for this date"
        );
        
        ordersSet = subject.getOrdersByDate(LocalDate.parse("2021-02-02"));
        assertEquals(ordersSet.size(), 1, "There should be one element");
        assertTrue(ordersSet.contains(solidyneOrder));
        
        ordersSet = subject.getOrdersByDate(LocalDate.parse("2022-03-03"));
        assertEquals(ordersSet.size(), 2, "There should be two elements");
        assertTrue(ordersSet.contains(tablatOrder));
        assertTrue(ordersSet.contains(plutonianOrder));
        
        // check consistency with retrieval by date and number
        assertTrue(
            subject.getOrderByDateAndNumber(null, 0).isEmpty(),
            "There should be no order with a null date"
        );
        
        assertTrue(
            subject.getOrderByDateAndNumber(LocalDate.MAX, 0).isEmpty(),
            "There should be no orders for this date"
        );
        
        assertTrue(
            subject.getOrderByDateAndNumber(LocalDate.parse("2020-01-01"), 0).isEmpty(),
            "There should be no orders for this date"
        );
        
        assertTrue(
            subject.getOrderByDateAndNumber(LocalDate.parse("2021-02-02"), 12).isEmpty(),
            "There should be no order with both this date and number"
        );
        
        subject.getOrderByDateAndNumber(
            solidyneOrder.getOrderDate(),
            solidyneOrder.getOrderNum()
        ).ifPresentOrElse(
            val -> assertEquals(val, solidyneOrder), 
            () -> fail("This order should exist")
        );
        
        subject.getOrderByDateAndNumber(
            tablatOrder.getOrderDate(),
            tablatOrder.getOrderNum()
        ).ifPresentOrElse(
            val -> assertEquals(val, tablatOrder), 
            () -> fail("This order should exist")
        );
        
        subject.getOrderByDateAndNumber(
            plutonianOrder.getOrderDate(),
            plutonianOrder.getOrderNum()
        ).ifPresentOrElse(
            val -> assertEquals(val, plutonianOrder),
            () -> fail("This order should exist")
        );
    }

    @Test
    public void testLoadAndPushOrder() throws FlooringMasteryFailedLoadException {
        subject.loadDaos();
        
        assertEquals(subject.latestOrderNum(), 4, "4 should be the next order number at this point");
        
        // try bad inputs for pushing
        try {
            subject.pushOrder(null);
            fail("Null should not be pushed");
        } catch (FlooringMasteryOrderAlreadyExistsException ex) {
        }
        
        try {
            subject.pushOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    3,
                    "Plutonian Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                            "Construction Fungus",
                            new BigDecimal("10.11"),
                            new BigDecimal("12.13")
                    ),
                    new BigDecimal("101")
                )
            );
            fail("This order already exists, so an exception should've been thrown");
        } catch (FlooringMasteryOrderAlreadyExistsException ex) {
        }
        
        // attempt a valid push
        try {
            subject.pushOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    4,
                    "Kaiper Belt Creamery",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                            "Concrete",
                            new BigDecimal("1.23"),
                            new BigDecimal("4.56")
                    ),
                    new BigDecimal("678")
                )
            );
        } catch (FlooringMasteryOrderAlreadyExistsException ex) {
            fail("An exception should not be thrown");
        }
        
        // verify that pushing the new order has updated the order set appropriately
        assertEquals(
            5, 
            subject.latestOrderNum(), 
            "5 should now be the latest order number"
        );
        
        Set<FlooringMasteryOrder> orderSet = subject.ordersSet();
        assertEquals(orderSet.size(), 4, "There should be four elements");
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-02"),
                    1,
                    "Solidyne Areodynamics",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("400")
                )
            )
        );
        
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    2,
                    "Tablat Acquisitions, Inc.",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("234")
                )
            )
        );
        
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    3,
                    "Plutonian Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("101")
                )
            )
        );
        
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    4,
                    "Kaiper Belt Creamery",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                            "Concrete",
                            new BigDecimal("1.23"),
                            new BigDecimal("4.56")
                    ),
                    new BigDecimal("678")
                )
            )
        );
    }

    @Test
    public void testLoadAndReplaceOrder() throws FlooringMasteryFailedLoadException {
        subject.loadDaos();
        
        assertEquals(4, subject.latestOrderNum());
        
        // attempt bad inputs
        try {
            subject.replaceOrder(null);
            fail("Null should not replace anything");
        } catch (FlooringMasteryOrderNotPresentForReplacementException ex) {
        }
        
        // date with no corresponding order
        try {
            subject.replaceOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2027-01-03"),
                    3,
                    "Plutonian Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("101")
                )
            );
            fail("A replacement should not occur");
        } catch (FlooringMasteryOrderNotPresentForReplacementException ex) {
        }
        
        try {
            // id with no corresponding order
            subject.replaceOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    4,
                    "Plutonian Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("101")
                )
            );
            fail("A replacement should not occur");
        } catch (FlooringMasteryOrderNotPresentForReplacementException ex) {
        }

        try {
            subject.replaceOrder(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    3,
                    "Kaiper Belt Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("102")
                )
            );
        } catch (FlooringMasteryOrderNotPresentForReplacementException ex) {
            fail("A replacement should occur");
        }
        
        assertEquals(subject.latestOrderNum(), 4);
        
        // verify proper changes in order set
        Set<FlooringMasteryOrder> orderSet = subject.ordersSet();
        assertEquals(orderSet.size(), 3);
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-02"),
                    1,
                    "Solidyne Areodynamics",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("400")
                )
            )
        );
        
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    2,
                    "Tablat Acquisitions, Inc.",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("234")
                )
            )
        );
        
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    3,
                    "Kaiper Belt Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("102")
                )
            )
        );
    }

    @Test
    public void testLoadAndRemoveOrder() throws FlooringMasteryFailedLoadException {
        subject.loadDaos();
        
        assertEquals(4, subject.latestOrderNum());
        
        // attempt bad inputs
        assertTrue(
            subject.removeOrderByDateAndNumber(null, 0).isEmpty(),
            "There should be no removals for an order with a null date"
        );
        
        assertTrue(
            subject.removeOrderByDateAndNumber(
                LocalDate.parse("2020-01-01"), 
                0
            ).isEmpty(),
            "There should be no removals since no order has this date"
        );
        
        assertTrue(
            subject.removeOrderByDateAndNumber(
                LocalDate.parse("2022-03-03"),
                4
            ).isEmpty(),
            "There should be no removals since no order has this number"
        );
        
        // attempt removals
        subject.removeOrderByDateAndNumber(
            LocalDate.parse("2021-02-02"),
            1
        ).ifPresentOrElse(
            val -> assertEquals(
                val,
                new FlooringMasteryOrder(
                    LocalDate.parse("2021-02-02"),
                    1,
                    "Solidyne Areodynamics",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("400")
                ),
                "The wrong order may have been removed"
            ), () -> fail("An order should have been removed")
        );
        
        assertEquals(4, subject.latestOrderNum());
        
        subject.removeOrderByDateAndNumber(
            LocalDate.parse("2022-03-03"),
            3
        ).ifPresentOrElse(
            val -> assertEquals(
                val,
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    3,
                    "Plutonian Ice",
                    "FL",
                    new BigDecimal("4.98"),
                    new FlooringMasteryProduct(
                        "Construction Fungus",
                        new BigDecimal("10.11"),
                        new BigDecimal("12.13")
                    ),
                    new BigDecimal("101")
                )
            ), () -> fail("An order should have been removed")
        );
        
        // verify changes to orders set
        assertEquals(3, subject.latestOrderNum());
        
        Set<FlooringMasteryOrder> orderSet = subject.ordersSet();
        assertEquals(1, orderSet.size());
        assertTrue(
            orderSet.contains(
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    2,
                    "Tablat Acquisitions, Inc.",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("234")
                )
            ),
            "Only Tablat's order should remain"
        );
        
        // examine what happens if all elements are removed
        subject.removeOrderByDateAndNumber(
            LocalDate.parse("2022-03-03"),
            2
        ).ifPresentOrElse(
            val -> assertEquals(
                val, 
                new FlooringMasteryOrder(
                    LocalDate.parse("2022-03-03"),
                    2,
                    "Tablat Acquisitions, Inc.",
                    "WA",
                    new BigDecimal("9.77"),
                    new FlooringMasteryProduct(
                        "Concrete",
                        new BigDecimal("1.23"),
                        new BigDecimal("4.56")
                    ),
                    new BigDecimal("234")
                ),
                "Tablat's order should have been removed"
            ),() -> fail("A removal should have occured")
        );
        
        assertEquals(1, subject.latestOrderNum());
        
        orderSet = subject.ordersSet();
        assertTrue(orderSet.isEmpty());
    }
}

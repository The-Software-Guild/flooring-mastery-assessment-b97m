package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.model.FlooringMasteryOrder;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryOrderDaoFileImplTest {
    private FlooringMasteryOrderDao subject;

    @Test
    public void testLoadFromExternals() throws Exception {
        System.out.println("loadFromExternals");
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        instance.loadFromExternals();
        fail("The test case is a prototype.");
    }

    @Test
    public void testPushOrder() {
        System.out.println("pushOrder");
        FlooringMasteryOrder order = null;
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Optional<FlooringMasteryOrder> expResult = null;
        Optional<FlooringMasteryOrder> result = instance.pushOrder(order);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testReplaceOrder() {
        System.out.println("replaceOrder");
        FlooringMasteryOrder order = null;
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Optional<FlooringMasteryOrder> expResult = null;
        Optional<FlooringMasteryOrder> result = instance.replaceOrder(order);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testOrdersSet() {
        System.out.println("ordersSet");
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Set<FlooringMasteryOrder> expResult = null;
        Set<FlooringMasteryOrder> result = instance.ordersSet();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testOrdersByDate() {
        System.out.println("ordersByDate");
        LocalDate date = null;
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Set<FlooringMasteryOrder> expResult = null;
        Set<FlooringMasteryOrder> result = instance.ordersByDate(date);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetOrderByDateAndNumber() {
        System.out.println("getOrderByDateAndNumber");
        LocalDate date = null;
        int num = 0;
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Optional<FlooringMasteryOrder> expResult = null;
        Optional<FlooringMasteryOrder> result = instance.getOrderByDateAndNumber(date, num);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRemoveOrderByDateAndNumber() {
        System.out.println("removeOrderByDateAndNumber");
        LocalDate date = null;
        int num = 0;
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        Optional<FlooringMasteryOrder> expResult = null;
        Optional<FlooringMasteryOrder> result = instance.removeOrderByDateAndNumber(date, num);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSaveToExternals() throws Exception {
        System.out.println("saveToExternals");
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        instance.saveToExternals();
        fail("The test case is a prototype.");
    }

    @Test
    public void testExport() throws Exception {
        System.out.println("export");
        FlooringMasteryOrderDaoFileImpl instance = new FlooringMasteryOrderDaoFileImpl();
        instance.export();
        fail("The test case is a prototype.");
    }
    
    private FlooringMasteryOrderDao setupDao(
        String orderDirectory,
        String exportFile,
        String... contents) {
    
        PrintWriter setupWriter = new PrintWriter(new FileWriter(orderDirectory))
    }
}

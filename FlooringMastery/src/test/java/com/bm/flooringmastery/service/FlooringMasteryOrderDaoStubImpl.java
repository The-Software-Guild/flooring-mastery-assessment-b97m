package com.bm.flooringmastery.service;

import com.bm.flooringmastery.dao.FlooringMasteryOrderDao;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedExportException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedSaveException;
import com.bm.flooringmastery.model.FlooringMasteryOrder;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Stub implementation of the Order dao for testing purposes
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 7, 2021
 */
public class FlooringMasteryOrderDaoStubImpl implements FlooringMasteryOrderDao {
    private final Map<LocalDate, Map<Integer, FlooringMasteryOrder>> ORDERS_MAP;

    public FlooringMasteryOrderDaoStubImpl() {
        ORDERS_MAP = new HashMap<>();
        ORDERS_MAP.put(LocalDate.parse("2020-01-01"), new HashMap<>());
        
        ORDERS_MAP.put(LocalDate.parse("2021-02-02"), new HashMap<>());
        ORDERS_MAP.get(LocalDate.parse("2021-02-02")).put(
            1, 
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
        );
        
        ORDERS_MAP.put(LocalDate.parse("2022-03-03"), new HashMap<>());
        ORDERS_MAP.get(LocalDate.parse("2022-03-03")).put(
            2, 
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
        );
        ORDERS_MAP.get(LocalDate.parse("2022-03-03")).put(
            3, 
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
    }
    
    @Override
    public void loadFromExternals() throws FlooringMasteryFailedLoadException {
        // do nothing
    }

    @Override
    public Optional<FlooringMasteryOrder> pushOrder(FlooringMasteryOrder order) {
        Optional<FlooringMasteryOrder> receivedInstance;
        if (order == null) {
            receivedInstance = Optional.empty();
        } else if (ORDERS_MAP.containsKey(order.getOrderDate())) {
            Map<Integer, FlooringMasteryOrder> subMap = ORDERS_MAP.get(order.getOrderDate());
            if (subMap.containsKey(order.getOrderNum())) {
                receivedInstance = Optional.empty();
            } else {
                subMap.put(order.getOrderNum(), order);
                receivedInstance = Optional.of(order);
            }
        } else {
            ORDERS_MAP.put(order.getOrderDate(), new HashMap<>());
            ORDERS_MAP.get(order.getOrderDate()).put(order.getOrderNum(), order);
            receivedInstance = Optional.of(order);
        }
        return receivedInstance;
    }

    @Override
    public Optional<FlooringMasteryOrder> replaceOrder(FlooringMasteryOrder order) {
        Optional<FlooringMasteryOrder> receivedInstance;
        if (order == null) {
            receivedInstance = Optional.empty();
        } else if (ORDERS_MAP.containsKey(order.getOrderDate())) {
            Map<Integer, FlooringMasteryOrder> subMap = ORDERS_MAP.get(order.getOrderDate());
            if (subMap.containsKey(order.getOrderNum())) {
                subMap.put(order.getOrderNum(), order);
                receivedInstance = Optional.of(order);
            } else {
                receivedInstance = Optional.empty();
            }
        } else {
            receivedInstance = Optional.empty();
        }
        return receivedInstance;
    }

    @Override
    public Set<FlooringMasteryOrder> ordersSet() {
        Set<FlooringMasteryOrder> receivedOrders = new HashSet<>();
        ORDERS_MAP.values().forEach(subMap -> {
            receivedOrders.addAll(subMap.values());
        });
        
        return receivedOrders;
    }

    @Override
    public Set<FlooringMasteryOrder> ordersByDate(LocalDate date) {
        Map<Integer, FlooringMasteryOrder> subMap = ORDERS_MAP.get(date);
        if (subMap == null) {
            return new HashSet<>();
        }

        return subMap.values().stream().collect(Collectors.toSet());
    }

    @Override
    public Optional<FlooringMasteryOrder> getOrderByDateAndNumber(LocalDate date, int num) {
        Optional<FlooringMasteryOrder> receivedInstance;
        Map<Integer, FlooringMasteryOrder> subMap = ORDERS_MAP.get(date);
        if (subMap == null) {
            receivedInstance = Optional.empty();
        } else {        
            FlooringMasteryOrder order = subMap.get(num);
            if (order == null) {
                receivedInstance = Optional.empty();
            } else {
                receivedInstance = Optional.of(order);
            }
        }
        return receivedInstance;
    }

    @Override
    public Optional<FlooringMasteryOrder> removeOrderByDateAndNumber(LocalDate date, int num) {
        Optional<FlooringMasteryOrder> receivedInstance;
        Map<Integer, FlooringMasteryOrder> subMap = ORDERS_MAP.get(date);
        if (subMap == null) {
            receivedInstance = Optional.empty();
        } else {
            FlooringMasteryOrder order = subMap.remove(num);
            if (order == null) {
                receivedInstance = Optional.empty();
            } else {
                receivedInstance = Optional.of(order);
            }
        }
        return receivedInstance;
    }

    @Override
    public void saveToExternals() throws FlooringMasteryFailedSaveException {
        // do nothing
    }

    @Override
    public void export() throws FlooringMasteryFailedExportException {
        // do nothing
    }

}

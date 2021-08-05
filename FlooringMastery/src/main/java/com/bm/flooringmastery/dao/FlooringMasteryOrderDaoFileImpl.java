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
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * An implementation of the OrderDao interface
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryOrderDaoFileImpl implements FlooringMasteryOrderDao {
    private final String SRC_DIRECTORY;
    private final String EXP_FILE;
    
    private final Map<LocalDate, Map<Integer, FlooringMasteryOrder>> ORDERS_MAP;
    
    public FlooringMasteryOrderDaoFileImpl() {
        this("Orders", "Backup/DataExport.txt");
    }

    public FlooringMasteryOrderDaoFileImpl(String SRC_DIRECTORY, String EXP_FILE) {
        this.SRC_DIRECTORY = SRC_DIRECTORY;
        this.EXP_FILE = EXP_FILE;
        this.ORDERS_MAP = new TreeMap<>();
    }
        
    
    @Override
    public void loadFromExternals() throws FlooringMasteryFailedLoadException {
        List<String> filenames;
        try {
            Path ordersDirectoryPath = FileSystems.getDefault().getPath(SRC_DIRECTORY);
            // gather all the filenames in this directory
            filenames = Files.walk(ordersDirectoryPath, 1).skip(1)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new FlooringMasteryFailedLoadException("Unable to load orders", ex);
        }
        
        Scanner reader;
        for (String filename : filenames) {
            // Parse date from file name, which is assumed to have the form
            // Orders_MMDDYYYY.txt
            String monthStr = filename.substring(7, 9);
            String dayStr = filename.substring(9, 11);
            String yearStr = filename.substring(11, 15);
            LocalDate orderDate = LocalDate.parse(
                String.format(
                    "%s-%s-%s", 
                    yearStr, 
                    monthStr, 
                    dayStr
                )
            );

            try {
                reader = new Scanner(
                    new BufferedReader(
                        new FileReader(
                            SRC_DIRECTORY + "/" + filename
                        )
                    )
                );
            } catch (FileNotFoundException ex) {
                throw new FlooringMasteryFailedLoadException("Unable to load orders", ex);
            }
            
            reader.nextLine(); // ignore header
            while (reader.hasNextLine()) {
                String[] tokens = reader.nextLine().split(",");
                int orderNum = Integer.parseInt(tokens[0]);
                String customerName = tokens[1];
                String state = tokens[2];
                BigDecimal percentTaxRate = new BigDecimal(tokens[3]);

                String type = tokens[4];
                BigDecimal area = new BigDecimal(tokens[5]);
                BigDecimal costPerSqFt = new BigDecimal(tokens[6]);
                BigDecimal laborCostPerSqFt = new BigDecimal(tokens[7]);

                FlooringMasteryOrder order = new FlooringMasteryOrder(
                    orderDate,
                    orderNum,
                    customerName,
                    state,
                    percentTaxRate,
                    new FlooringMasteryProduct(
                        type,
                        costPerSqFt,
                        laborCostPerSqFt
                    ),
                    area
                );
                if (!ORDERS_MAP.containsKey(orderDate)) {
                    ORDERS_MAP.put(orderDate, new TreeMap<>());
                }
                ORDERS_MAP.get(orderDate).put(order.getOrderNum(), order);
            }
            reader.close();
        }
    }

    @Override
    public void pushOrder(FlooringMasteryOrder order) {
        if (!ORDERS_MAP.containsKey(order.getOrderDate())) {
            ORDERS_MAP.put(order.getOrderDate(), new TreeMap<>());
        }
        ORDERS_MAP.get(order.getOrderDate()).put(order.getOrderNum(), order);
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
                if (subMap.isEmpty()) {
                    ORDERS_MAP.remove(date);
                }
            }
        }
        return receivedInstance;
    }

    @Override
    public void saveToExternals() throws FlooringMasteryFailedSaveException {
        for (Entry<LocalDate, Map<Integer, FlooringMasteryOrder>> entry : ORDERS_MAP.entrySet()) {
            LocalDate date = entry.getKey();
            String filename = String.format(
                "Orders_%2d%2d%4d.txt",
                date.getDayOfMonth(),
                date.getDayOfMonth(),
                date.getYear()
            );
            
            PrintWriter writer;
            try {
                writer = new PrintWriter(new FileWriter("Orders/" + filename));
            } catch (IOException ex) {
                throw new FlooringMasteryFailedSaveException("Unable to save orders", ex);
            }
            
            writer.println(
                "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
                + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,"
                + "LaborCost,Tax,Total"
            );
            
            entry.getValue().values().forEach(order -> {
                String line = String.format(
                    "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    order.getOrderNum(),
                    order.getCustomerName(),
                    order.getState(),
                    order.getPercentTaxRate().toString(),
                    order.getProductType(),
                    order.getArea().toString(),
                    order.getCostPerSqFt().toString(),
                    order.getLaborCostPerSqFt().toString(),
                    order.getMaterialCost().toString(),
                    order.getLaborCost().toString(),
                    order.getTax().toString(),
                    order.getTotal().toString()
                );
                writer.println(line);
            });
            writer.close();
        }
    }

    @Override
    public void export() throws FlooringMasteryFailedExportException {
        PrintWriter writer;
        
        try {
            writer = new PrintWriter(new FileWriter(EXP_FILE));
        } catch (IOException ex) {
            throw new FlooringMasteryFailedExportException("Unable to export orders");
        }
        
        writer.println(
            "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
            + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,"
            + "Tax,Total,OrderDate"
        );
        
        for (Map<Integer, FlooringMasteryOrder> subMap : ORDERS_MAP.values()) {
            for (FlooringMasteryOrder order : subMap.values()) {
                String line = String.format(
                    "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    order.getOrderNum(),
                    order.getCustomerName(),
                    order.getState(),
                    order.getPercentTaxRate().toString(),
                    order.getProductType(),
                    order.getArea(),
                    order.getCostPerSqFt().toString(),
                    order.getLaborCostPerSqFt().toString(),
                    order.getMaterialCost().toString(),
                    order.getLaborCost().toString(),
                    order.getTax().toString(),
                    order.getTotal().toString(),
                    String.format(
                        "%2d-%2d-%4d",
                        order.getOrderDate().getMonthValue(),
                        order.getOrderDate().getDayOfYear(),
                        order.getOrderDate().getYear()
                    )
                );
                writer.println(line);
            }
        }
        writer.close();
    }
}

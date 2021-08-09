package com.bm.flooringmastery.dao;

import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import com.bm.flooringmastery.model.FlooringMasteryProduct;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Munoz
 */
public class FlooringMasteryProductDaoFileImplTest {
    private FlooringMasteryProductDao subject;
    
    @Test
    public void testAllOnEmpty() throws IOException, FlooringMasteryFailedLoadException {
        PrintWriter setupWriter = new PrintWriter(new FileWriter("Testing/Products_empty.txt"));
        setupWriter.println("ProductType::CostPerSquareFoot::LaborCostPerSquareFoot");
        setupWriter.close();
        
        subject = new FlooringMasteryProductDaoFileImpl("Testing/Products_empty.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            Set<FlooringMasteryProduct> prodSet = subject.productsSet();
            assertTrue(
                prodSet.isEmpty(),
                "This set should be empty"
            );
            
            assertTrue(
                subject.getProductByType(null).isEmpty(),
                "It should not be able to get products with null type"
            );
            
            assertTrue(
                subject.getProductByType("").isEmpty(),
                "It should not be able to get products with type \"\""
            );
            
            assertTrue(
                subject.getProductByType("Granite").isEmpty(),
                "It should not be able to get products with Granite type"
            );
        }
    }

    @Test
    public void testAllOnSingle() throws IOException, FlooringMasteryFailedLoadException {
        PrintWriter setupWriter = new PrintWriter(new FileWriter("Testing/Products_single.txt"));
        setupWriter.println("ProductType::CostPerSquareFoot::LaborCostPerSquareFoot");
        setupWriter.println("Marble::7.77::4.78");
        setupWriter.close();
        
        subject = new FlooringMasteryProductDaoFileImpl("Testing/Products_single.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            Set<FlooringMasteryProduct> prodSet = subject.productsSet();
            assertTrue(
                prodSet.contains(
                    new FlooringMasteryProduct(
                        "Marble",
                        new BigDecimal("7.77"),
                        new BigDecimal("4.78")
                    )
                ),
                "The set should contain the aformentioned product"
            );
            assertTrue(
                prodSet.size() == 1,
                "The set should only have one element"
            );
            
            subject.getProductByType("Marble").ifPresentOrElse(
                prod -> {
                    assertEquals(
                        prod, 
                        new FlooringMasteryProduct(
                            "Marble",
                            new BigDecimal("7.77"),
                            new BigDecimal("4.78")
                        ),
                        "The received product should Marble with cost and labor"
                        + " costs of $7.77 and $4.78 per square foot"
                    );
                },
                () -> {
                    fail(
                        "It should be possible to receive information about"
                        + " Marble"
                    );
                }
            );
            
            assertTrue(
                subject.getProductByType(null).isEmpty(),
                "It should not be able to get products with null type"
            );
            
            assertTrue(
                subject.getProductByType("").isEmpty(),
                "It should not be able to get products with type \"\""
            );
            
            assertTrue(
                subject.getProductByType("Granite").isEmpty(),
                "It should not be able to get products with Granite type"
            );
        }
    }

    @Test
    public void testAllOnMulti() throws IOException, FlooringMasteryFailedLoadException {
        PrintWriter setupWriter = new PrintWriter(new FileWriter("Testing/Products_multi.txt"));
        setupWriter.println("ProductType::CostPerSquareFoot::LaborCostPerSquareFoot");
        setupWriter.println("Marble::7.77::4.78");
        setupWriter.println("Vinyl::3.91::2.43");
        setupWriter.close();
        
        subject = new FlooringMasteryProductDaoFileImpl("Testing/Products_multi.txt");
        
        for (int i = 0; i < 10; i++) {
            subject.loadDataFromExternals();
            Set<FlooringMasteryProduct> prodSet = subject.productsSet();
            assertTrue(
                prodSet.contains(
                    new FlooringMasteryProduct(
                        "Marble",
                        new BigDecimal("7.77"),
                        new BigDecimal("4.78")
                    )
                ),
                "The set should contain the aformentioned product"
            );
            assertTrue(
                prodSet.contains(
                    new FlooringMasteryProduct(
                        "Vinyl",
                        new BigDecimal("3.91"),
                        new BigDecimal("2.43")
                    )
                ),
                "The set should contain the aformentioned product"
            );
            assertTrue(
                prodSet.size() == 2,
                "The set should have two elements"
            );
            
            subject.getProductByType("Marble").ifPresentOrElse(
                prod -> {
                    assertEquals(
                        prod, 
                        new FlooringMasteryProduct(
                            "Marble",
                            new BigDecimal("7.77"),
                            new BigDecimal("4.78")
                        ),
                        "The received product should Marble with cost and labor"
                        + " costs of $7.77 and $4.78 per square foot"
                    );
                },
                () -> {
                    fail(
                        "It should be possible to receive information about"
                        + " Marble"
                    );
                }
            );
            
            subject.getProductByType("Vinyl").ifPresentOrElse(
                prod -> {
                    assertEquals(
                        prod, 
                        new FlooringMasteryProduct(
                            "Vinyl",
                            new BigDecimal("3.91"),
                            new BigDecimal("2.43")
                        ),
                        "The received product should be Vinyl with cost and labor"
                        + " costs of $3.91 and $2.43 per square foot"
                    );
                },
                () -> {
                    fail(
                        "It should be possible to receive information about"
                        + " Vinyl"
                    );
                }
            );
            
            assertTrue(
                subject.getProductByType(null).isEmpty(),
                "It should not be able to get products with null type"
            );
            
            assertTrue(
                subject.getProductByType("").isEmpty(),
                "It should not be able to get products with type \"\""
            );
            
            assertTrue(
                subject.getProductByType("Granite").isEmpty(),
                "It should not be able to get products with Granite type"
            );
        }
    }
}

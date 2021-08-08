package com.bm.flooringmastery;

import com.bm.flooringmastery.controller.FlooringMasteryController;
import com.bm.flooringmastery.dao.exceptions.FlooringMasteryFailedLoadException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Acts as the entry point of the whole application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class App {
    public static void main(String[] args) throws FlooringMasteryFailedLoadException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
            "com.bm.flooringmastery"
        );
        
        FlooringMasteryController controller = ctx.getBean(FlooringMasteryController.class);
        controller.run();
    }
}

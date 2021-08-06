package com.bm.flooringmastery.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Acts as the View component of this application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class FlooringMasteryView {
    private final UserIO USER_IO;

    public FlooringMasteryView(UserIO USER_IO) {
        this.USER_IO = USER_IO;
    }
    
    /**
     * Displays a line of text to the user
     * 
     * @param line 
     */
    public void displayLine(String line) {
        this.USER_IO.displayLine(line);
    }
    
    /**
     * Displays informational text to the user
     * 
     * @param line 
     */
    public void displayInformationalLine(String line) {
        displayLine(String.format("|| %s ||", line));
    }

    /**
     * Displays a line of error text to the user
     * 
     * @param line 
     */
    public void displayErrorLine(String line) {
        displayLine(String.format("!! %s !!", line));
    }
    
    /**
     * Displays a line of text for soliciting the user for something
     * @param line 
     */
    private void displaySolicitationLine(String line) {
        displayLine(String.format("-- %s --", line));
    }
    
    /**
     * Displays text surrounding a given set of lines
     * 
     * @param header
     * @param contents 
     */
    public void displayAroundContents(String header, String... contents) {
        displayLine("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        displayLine(String.format(" * <<%s>>", header));
        Arrays.stream(contents).forEach(line -> displayLine(" * " + line));
        displayLine(" *");
        displayLine(" * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
    }
    
    /**
     * Prompts the user for an int that matches some constraint.
     * 
     * Each time the entered int either cannot be converted to the 
     * proper form or cannot meet the constraint, an error will be
     * displayed to the user.
     * 
     * @param prompt
     * @param constraint
     * @param errorText 
     * 
     * @return The aforementioned int
     */
    public int getInt(String prompt, Predicate<Integer> constraint, String errorText) {
        int receivedInt = -1;
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt);
            try {
                receivedInt = Integer.parseInt(USER_IO.getLine());
                invalid = !constraint.test(receivedInt);
            } catch (NumberFormatException ex) {
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        
        return receivedInt;
    }

    /**
     * Prompts the user for a String that matches some constraint.
     * 
     * Each time the entered String cannot meet the constraint, an error will be
     * displayed to the user.
     * 
     * @param prompt
     * @param constraint
     * @param errorText 
     * 
     * @return The aforementioned String
     */
    public String getString(String prompt, Predicate<String> constraint, String errorText) {
        String receivedString = "";
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt);
            receivedString = USER_IO.getLine();
            invalid = !constraint.test(receivedString);
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        
        return receivedString;
    }

    /**
     * Prompts the user to enter one of the following
     * 
     * If the user submits an empty input, the replacement value will be returned
     * 
     * If the user submits a nonempty input that meets the constraints, this new
     * input will be returned.
     * 
     * Otherwise, an error message will be displayed each time invalid input is
     * entered
     * 
     * @param prompt
     * @param constraint
     * @param errorText 
     * @param repVal
     * 
     * @return The aforementioned String
     */
    public String getStringReplacement(String prompt, Predicate<String> constraint, String errorText, String repVal) {
        String receivedString = "";
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt + " (currently " + repVal + ")");
            receivedString = USER_IO.getLine();
            if (receivedString.isEmpty()) {
                receivedString = repVal;
                invalid = false;
            } else {
               invalid = !constraint.test(receivedString);                
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        
        return receivedString;
    }
    
    /**
     * Prompts the user for a BigDecimal that matches some constraint
     * 
     * Each time the input cannot be converted to a BigDecimal or fails
     * to meet the constraint, an error will be displayed to the user
     * 
     * Otherwise,
     * @param prompt
     * @param constraint
     * @param errorText
     * @return The aforementioned BigDecimal
     */
    public BigDecimal getBigDecimal(String prompt, Predicate<BigDecimal> constraint, String errorText) {
        BigDecimal receivedInput = BigDecimal.ZERO;
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt);
            try {
                receivedInput = new BigDecimal(USER_IO.getLine());
                invalid = !constraint.test(receivedInput);
            } catch (Exception ex) {
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        return receivedInput;
    }

    /**
     * Prompts the user to enter one of the following
     * 
     * If the user provides empty input, the replacement value will be returned.
     * 
     * If the user provides a nonempty input that meets the constraints, the new
     * input will be returned
     * 
     * Otherwise, an error message will be displayed each time the user enters
     * invalid input.
     * 
     * @param prompt
     * @param constraint
     * @param errorText
     * @param repVal
     * @return The aforementioned user input
     */
    public BigDecimal getBigDecimalReplacement(String prompt, Predicate<BigDecimal> constraint, String errorText, BigDecimal repVal) {
        BigDecimal receivedValue = BigDecimal.ZERO;
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt + " (currently " + repVal.toString() + ")");
            String receivedString = USER_IO.getLine();
            if (receivedString.isEmpty()) {
                receivedValue = repVal;
                invalid = false;
            } else {
                try {
                    receivedValue = new BigDecimal(receivedString);
                    invalid = !constraint.test(receivedValue);
                } catch (Exception ex) {
                }
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        
        return receivedValue;
    }
    
    /**
     * Prompts the user for a LocalDate that matches some constraint
     * 
     * Each time the input cannot be converted to a LocalDate or fails to meet
     * the constraint, an error message will be displayed to the user
     * 
     * @param prompt
     * @param constraint
     * @param errorText
     * @return The aforementioned LocalDate
     */
    public LocalDate getLocalDate(String prompt, Predicate<LocalDate> constraint, String errorText) {
        LocalDate receivedInput = LocalDate.EPOCH;
        boolean invalid = true;
        while (invalid) {
            displaySolicitationLine(prompt);
            try {
                receivedInput = LocalDate.parse(USER_IO.getLine());
                invalid = !constraint.test(receivedInput);
            } catch (Exception ex) {
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        return receivedInput;
    }

    /**
     * Frees all resources associated with this component
     */
    public void close() {
        USER_IO.close();
    }
}

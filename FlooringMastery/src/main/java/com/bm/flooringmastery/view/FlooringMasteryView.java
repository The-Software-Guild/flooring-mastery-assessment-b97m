package com.bm.flooringmastery.view;

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
        displayLine(String.format("/| %s |\\", header));
        Arrays.stream(contents).forEach(line -> displayLine(line));
        displayLine(String.format("\\| %s /|", "-".repeat(header.length())));
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
                invalid = true;
            }
            if (invalid) {
                displayErrorLine(errorText);
            }
        }
        
        return receivedInt;
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

}

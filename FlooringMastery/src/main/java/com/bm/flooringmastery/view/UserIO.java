package com.bm.flooringmastery.view;

/**
 * Provides basic user i/o interactions
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public interface UserIO {
    /**
     * Display a line of text to the user
     * 
     * @param line 
     */
    public void displayLine(String line);
    
    /**
     * @return A String line of text from the user
     */
    public String getLine();
    
    /**
     * Frees any resources associated with this component
     */
    public void close();
}

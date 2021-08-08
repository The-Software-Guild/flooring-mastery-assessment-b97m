package com.bm.flooringmastery.view;

import java.util.Scanner;
import org.springframework.stereotype.Component;

/**
 * An implementation of the UserIo interface based on interactions with the 
 * console
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
@Component
public class UserIoConsoleImpl implements UserIO {
    private final Scanner USER_IO = new Scanner(System.in);
    
    @Override
    public void displayLine(String line) {
        System.out.println(line);
    }

    @Override
    public String getLine() {
        return USER_IO.nextLine();
    }

    @Override
    public void close() {
        USER_IO.close();
    }
}

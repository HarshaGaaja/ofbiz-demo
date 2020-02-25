package com.ofbiz.automation.exceptions;

public class SauceConnectionException extends Exception{

    public SauceConnectionException(String message, Throwable cause){
        super("Unable to connect to Sauce\n" + message,cause);
    }

    public SauceConnectionException(String message) {
        super("Unable to connect to Sauce\n" + message);
    }

}

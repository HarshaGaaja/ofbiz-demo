
package com.ofbiz.automation.exceptions;

public class TooManySessionsException extends Exception{

    public TooManySessionsException(String message, Throwable cause){
        super(message,cause);
    }

    public TooManySessionsException(String message) {
        super(message);
    }

}
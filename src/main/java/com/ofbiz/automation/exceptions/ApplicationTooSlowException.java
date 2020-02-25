package com.ofbiz.automation.exceptions;

import org.testng.Reporter;

public class ApplicationTooSlowException extends Exception {

    private static String getEnv(){
        return Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest()
                .getParameter("environment");
    }

    public ApplicationTooSlowException(String message, Throwable cause){
        super("The application is too slow on the '" + getEnv() + "' environment.\n" + message,cause);
    }
    public ApplicationTooSlowException(String message) {
        super("The application is too slow on the '" + getEnv() + "' environment.\n" + message);
    }
}

package com.ofbiz.automation.exceptions;

public class EnvironmentOutOfSyncException extends Exception {
    public EnvironmentOutOfSyncException(String message, Throwable cause){
        super("This exception occurred because this functionality differs in both the environments (UAT and QA3). This step will fail until both the environments are in sync.\n"
                + message,cause);
    }
    public EnvironmentOutOfSyncException(String message) {
        super("This exception occurred because this functionality differs in both the environments (UAT and QA3). This step will fail until both the environments are in sync.\n"
                + message);
    }
}
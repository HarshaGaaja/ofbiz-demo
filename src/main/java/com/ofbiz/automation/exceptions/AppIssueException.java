package com.ofbiz.automation.exceptions;

public class AppIssueException extends Exception{
	
	public AppIssueException(String message, Throwable cause){
		super(message,cause);
	}

	public AppIssueException(String message) {
		super(message);
	}

}

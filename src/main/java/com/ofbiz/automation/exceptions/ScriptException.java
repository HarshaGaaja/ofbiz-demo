package com.ofbiz.automation.exceptions;

public class ScriptException extends Exception{

	public ScriptException(String message, Throwable cause){
		super(message,cause);
	}

	public ScriptException(String message) {
		super(message);
	}
}

package com.ofbiz.automation.common;

public class Constants {
    public static World world;
    //Enum for the types of Drivers
    public enum DRIVERTYPE {
        SAUCE, LOCAL, GRID;
    }
    //Enum for types of Browsers
    public enum BROWSER {
        CHROME, FIREFOX, SAFARI, IEXPLORER, EDGE;
    }
    //Get the browser
    public enum BrowserName {
		FIREFOX("firefox"), CHROME("chrome"), SAFARI("safari"), EDGE("edge");
		private String browser;
		private BrowserName(String browser) {
			this.browser = browser;
		}
		
		public String getBrowserName() {
			return browser;
		}
	}
}

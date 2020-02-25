package com.ofbiz.automation.libraries;

import com.ofbiz.automation.common.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {
	private Properties properties;
	private Properties orProperties;
	public static Properties dbProperties;
	private final String propertyFilePath = System.getProperty("user.dir") + "/config/config.properties";
	private final String objectrepositoryFilePath = System.getProperty("user.dir")
			+ "/configs/objectrepository.properties";
	Logger logger = LogManager.getLogger(ConfigFileReader.class);

	// *************************************************
	private static ConfigFileReader configFileReader, objectRepositoryFileReader;
	private World world;
	public ConfigFileReader(World world){
		this.world = world;
	}

	public static ConfigFileReader getConfigFileReader() {
		String config = "CONFIG";
		if (configFileReader != null) {
			return configFileReader;
		} else {
			configFileReader = new ConfigFileReader(config);
			return configFileReader;
		}

	}

	public static ConfigFileReader getObjectRepositoryFileReader() {
		String repo = "OR";
		if (objectRepositoryFileReader != null) {
			return objectRepositoryFileReader;
		} else {
			objectRepositoryFileReader = new ConfigFileReader(repo);
			return objectRepositoryFileReader;
		}

	}
	// *************************************************

	protected ConfigFileReader(String configType) {
		logger.info("Into Config file reader");

		BufferedReader reader = null;
		try {
			if (configType.equalsIgnoreCase("CONFIG")) {
				logger.info("Into Config ");
				properties = new Properties();
				logger.info("The property file path is as :: "+propertyFilePath);
				reader = new BufferedReader(new FileReader(propertyFilePath));
				try {
					properties.load(reader);
					reader.close();
				} catch (IOException e) {
					logger.info("Here is the exception in config");
					e.printStackTrace();
				}

			} else if (configType.equalsIgnoreCase("OR")) {
				logger.info("Into OR Config ");
				orProperties = new Properties();
				reader = new BufferedReader(new FileReader(objectrepositoryFilePath));
				try {
					orProperties.load(reader);
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// properties = new Properties();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
		}
	}

	public int getdataSyncTime(){
		return Integer.parseInt(properties.getProperty("dataSyncWait"));
	}

    public int getMinDataSyncTime() {
        return Integer.parseInt(properties.getProperty("minDataSyncWait"));
    }


	public String getOFBIZUrl(String env) {
		String url = properties.getProperty("OFBIZ_URL");
		if (url != null)
			return url;
		else
			throw new RuntimeException(url + " url not specified in the Configuration.properties file.");
	}

	public String getOFBIZUserName(String env) {
		String userName = properties.getProperty("OFBIZ_Username");
		if (userName != null && !userName.isEmpty())
			return userName;
		else
			throw new RuntimeException("OFBIZ_Username not specified in the config.properties file.");
	}

	public String getOFBIZPassword(String env) {
		String password = properties.getProperty("OFBIZ_Password");
		if (password != null && !password.isEmpty())
			return password;
		else
			throw new RuntimeException("OFBIZ_Password not specified in the config.properties file.");
	}

	public String getElement(String elemntName) {
		String tempElement = orProperties.getProperty(elemntName);
		if (tempElement != null)
			return tempElement;
		else
			throw new RuntimeException(tempElement + " not specified in the ObjectRepository.properties file.");
	}

	public String getDriverPath() {
		String driverPath = System.getProperty("user.dir") + properties.getProperty("driverpath");
		if (driverPath != null)
			return driverPath;
		else
			throw new RuntimeException("driverPath not specified in the Configuration.properties file.");
	}

    public long getExplicitlyWait() {
        String explicitlyWait = properties.getProperty("explicitlyWait");
        if (explicitlyWait != null)
            return Long.parseLong(explicitlyWait);
        else
            throw new RuntimeException("explicitlyWait not specified in the Configuration.properties file.");
    }
	public long getImplicitlyWait() {
        String implicitlyWait = properties.getProperty("implicitlyWait");
		if (implicitlyWait != null)
			return Long.parseLong(implicitlyWait);
		else
			throw new RuntimeException("implicitlyWait not specified in the Configuration.properties file.");
	}
	public String getApplicationUrl() {

		String url = null;
		try {
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info(System.getenv("APP_URL"));
			url = System.getenv("APP_URL");
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

		} catch (Exception e) {
			url = null;
		}
		if (url == null) {
			url = properties.getProperty("url");
		}

		if (url != null)
			return url;
		else
			throw new RuntimeException("url not specified in the Configuration.properties file.");
	}

	public String getApiGatewayUrl() {
		String url = properties.getProperty("apigateway");
		if (url != null)
			return url;
		else
			throw new RuntimeException("url not specified in the Configuration.properties file.");
	}

	public String getKeyStatus() {
		String url = properties.getProperty("Jsonkeys");
		if (url != null)
			return url;
		else
			throw new RuntimeException("url not specified in the Configuration.properties file.");
	}

	public int getWebDriverWait() {
		String webdriverwait = properties.getProperty("webdriverwait");
		if (webdriverwait != null)
			return Integer.parseInt(webdriverwait);
		else
			throw new RuntimeException("webdriverwait not specified in the Configuration.properties file.");
	}

	public long getPause() {
		String pause = properties.getProperty("pause");
		if (pause != null)
			return Long.parseLong(pause);
		else
			throw new RuntimeException("pause not specified in the Configuration.properties file.");
	}

	public long getPageLoadPause() {
		String pageloadpause = properties.getProperty("pageloadpause");
		if (pageloadpause != null)
			return Long.parseLong(pageloadpause);
		else
			throw new RuntimeException("pageloadpause not specified in the Configuration.properties file.");
	}

	public String getTemplatePath() {
		String templatePath = System.getProperty("user.dir") + properties.getProperty("templateloc");
		if (templatePath != null)
			return templatePath;
		else
			throw new RuntimeException("templatePath not specified in the Configuration.properties file.");
	}

	public String getBrowser() {
		String browserName = properties.getProperty("browser");
		if (browserName != null)
			return browserName;
		else
			throw new RuntimeException(
					"Browser Name Key value in Configuration.properties is not matched : " + browserName);
	}

	public Boolean getBrowserWindowSize() {
		String windowSize = properties.getProperty("windowMaximize");
		if (windowSize != null)
			return Boolean.valueOf(windowSize);
		return true;
	}

	public String getReportConfigPath() {
		String reportConfigPath = properties.getProperty("reportConfigPath");
		if (reportConfigPath != null)
			return reportConfigPath;
		else
			throw new RuntimeException(
					"Report Config Path not specified in the Configuration.properties file for the Key:reportConfigPath");
	}

	public String getObjectRepoPath() {
		String objectConfigPath = properties.getProperty("ObjectRepoPath");
		if (objectConfigPath != null)
			return objectConfigPath;
		else
			throw new RuntimeException(
					"Object Repo Path not specified in the Configuration.properties file for the Key:reportConfigPath");
	}

	public String getDocumentType() {
		String template = System.getProperty("user.dir") + properties.getProperty("template");
		if (template != null)
			return template;
		else
			throw new RuntimeException("template is not specified in the Configuration.properties file.");
	}

//    public String getLogoutUrl(String env) {
//        String url = properties.getProperty("COO_" + env + "_LOGOUT_URL");
//        if (url != null)
//            return url;
//        else
//            throw new RuntimeException(url + " url not specified in the Configuration.properties file.");
//    }
	
	/*
	 * Mongo DB Host Name
	 * */
	public String getMongoDBHostName() {
	   String mongoHostName = properties.getProperty("MONGO_DB_HOSTNAME");
	   if(mongoHostName != null && !mongoHostName.isEmpty()) return mongoHostName;
	   else throw new RuntimeException("MONGO_DB_HOSTNAME not specified in the Configuration.properties file.");
	}

	/*
	 * Mongo DB port
	 * */
	public int getMongoDBPort() {
	   int mongoPort = Integer.parseInt(properties.getProperty("MODGO_DB_PORT"));
	   if(mongoPort != 0) return mongoPort;
	   else throw new RuntimeException("MODGO_DB_PORT not specified in the Configuration.properties file.");
	}

	/*
	 * Mongo DB User Name
	 * */
	public String getMongoDBUserName() {
	   String mongoUserName = properties.getProperty("MONGO_DB_USER_NAME");
	   if(mongoUserName != null && !mongoUserName.isEmpty()) return mongoUserName;
	   else throw new RuntimeException("MONGO_DB_USER_NAME not specified in the Configuration.properties file.");
	}

	/*
	 * Mongo DB Password
	 * */
	public String getMongoDBPassword() {
	   String mongoPassword = properties.getProperty("MONOG_DB_PASSWORD");
	   if(mongoPassword != null && !mongoPassword.isEmpty()) return mongoPassword;
	   else throw new RuntimeException("MONOG_DB_PASSWORD not specified in the Configuration.properties file.");
	}

	/*
	 * Mongo DB Collection
	 * */
	public String getMongoDBName() {
	   String mongoCollection = properties.getProperty("MONGO_DB_NAME");
	   if(mongoCollection != null && !mongoCollection.isEmpty()) return mongoCollection;
	   else throw new RuntimeException("MONGO_DB_NAME not specified in the Configuration.properties file.");
	}

	/*
	 * Debug Mode
	 * */
	public boolean isDebugModeOn() {
	   if(properties.getProperty("DEBUG_MODE")!=null && !properties.getProperty("DEBUG_MODE").isEmpty()) {
	      return Boolean.parseBoolean(properties.getProperty("DEBUG_MODE"));
	   }else {
	      return true;
	   }
	}

	/*
	 * Mongo DB Collection
	 * */
	public String getMongoDBCollectionName() {
	   String mongoCollection = properties.getProperty("MONGO_DB_COLLECTION");
	   if(mongoCollection != null && !mongoCollection.isEmpty()) return mongoCollection;
	   else throw new RuntimeException("MONGO_DB_COLLECTION not specified in the Configuration.properties file.");
	}

	/*
	 * Mongo DB Debug Mode Collection
	 * */
	public String getMongoDBDebugModeCollectionName() {
	   String mongoDebugCollection = properties.getProperty("DEBUG_MODE_MONGO_DB_COLLECTION");
	   if(mongoDebugCollection != null && !mongoDebugCollection.isEmpty()) return mongoDebugCollection;
	   else throw new RuntimeException("DEBUG_MODE_MONGO_DB_COLLECTION not specified in the Configuration.properties file.");
	}

	public long getWaitTime(String wait) {
		String waitTime = properties.getProperty(wait);
		if (waitTime != null)
			return Long.parseLong(waitTime);
		else
			throw new RuntimeException("Wait not specified in the Configuration.properties file.");
	}
}

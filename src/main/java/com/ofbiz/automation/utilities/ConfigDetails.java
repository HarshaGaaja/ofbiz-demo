package com.ofbiz.automation.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigDetails {
	private Properties properties;
	private Properties orProperties;
	public static Properties dbProperties;
	private final String propertyFilePath = System.getProperty("user.dir") + "/config/config.properties";
	private final String dbPropertyFilePath = System.getProperty("user.dir") + "/config/dbconfig.properties";
	private final String objectrepositoryFilePath = System.getProperty("user.dir")
			+ "/configs/objectrepository.properties";
	Logger logger = LogManager.getLogger(ConfigDetails.class);

	// *************************************************
	private static ConfigDetails configFileReader, objectRepositoryFileReader;

	public static ConfigDetails getConfigFileReader() {
		String config = "CONFIG";
		if (configFileReader != null) {
			return configFileReader;
		} else {
			configFileReader = new ConfigDetails(config);
			return configFileReader;
		}

	}

	/**
	 * Getting OMI service url from config file
	 * 
	 * @return
	 */
	public String getTBBOMIServiceOneUrl() {
		String url = properties.getProperty("OMI_SERVICE_URL_1");
		if (url == null)
			logger.error(url + " url not specified in the Configuration.properties file.");
		return url;
	}

	public static ConfigDetails getObjectRepositoryFileReader() {
		String repo = "OR";
		if (objectRepositoryFileReader != null) {
			return objectRepositoryFileReader;
		} else {
			objectRepositoryFileReader = new ConfigDetails(repo);
			return objectRepositoryFileReader;
		}

	}
	// *************************************************

	public ConfigDetails(String configType) {
		BufferedReader reader = null;
		try {
			if (configType.equalsIgnoreCase("CONFIG")) {
				logger.info("Into Config ");
				properties = new Properties();
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
			} else if (configType.equalsIgnoreCase("DB")) {
				dbProperties = new Properties();
				reader = new BufferedReader(new FileReader(dbPropertyFilePath));
				try {
					dbProperties.load(reader);
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// properties = new Properties();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Configuration.properties not found at " + propertyFilePath);
		}
	}
	public String getElement(String elemntName) {
		String tempElement = orProperties.getProperty(elemntName);
		if (tempElement == null)
			logger.error(tempElement + " not specified in the ObjectRepository.properties file.");
		return tempElement;
	}

	public String getDriverPath() {
		String driverPath = System.getProperty("user.dir") + properties.getProperty("driverpath");

		return driverPath;

	}

	public long getExplicitlyWait() {
		String explicitlyWait = properties.getProperty("explicitlyWait");
		if (explicitlyWait == null)
			logger.error("explicitlyWait not specified in the Configuration.properties file.");
		return Long.parseLong(explicitlyWait);
	}

	public String getApplicationUrl() {

		String url = null;
		try {
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			logger.info(" System ENV for APP_URL : " + System.getenv("APP_URL"));
			url = System.getenv("APP_URL");
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

		} catch (Exception e) {
			url = null;
		}
		if (url == null) {
			url = properties.getProperty("url");
		}

		if (url == null)
			logger.error("url not specified in the Configuration.properties file.");
		return url;
	}

	public String getApiGatewayUrl() {
		String url = properties.getProperty("apigateway");
		if (url == null)
			logger.error("url not specified in the Configuration.properties file.");
		return url;
	}

	public String getBeachBodyOIMApplicationUrl(String env) {
		logger.info("#########################################" + properties.getProperty("OIM_QA3_URL"));
		String url = properties.getProperty("OIM_" + env + "_URL");
		if (url == null)
			logger.error(url + " url not specified in the Configuration.properties file.");
		return url;

	}

	public String getKeyStatus() {
		String url = properties.getProperty("Jsonkeys");
		if (url == null)
			logger.error("url not specified in the Configuration.properties file.");
		return url;
	}

	public int getWebDriverWait() {
		String webdriverwait = properties.getProperty("webdriverwait");
		if (webdriverwait == null)
			logger.error("webdriverwait not specified in the Configuration.properties file.");
		return Integer.parseInt(webdriverwait);
	}

	public long getPause() {
		String pause = properties.getProperty("pause");
		if (pause == null)
			logger.error("pause not specified in the Configuration.properties file.");
		return Long.parseLong(pause);

	}

	public long getPageLoadPause() {
		String pageloadpause = properties.getProperty("pageloadpause");
		if (pageloadpause == null)
			logger.error("pageloadpause not specified in the Configuration.properties file.");

		return Long.parseLong(pageloadpause);

	}

	public String getTemplatePath() {
		String templatePath = System.getProperty("user.dir") + properties.getProperty("templateloc");
		return templatePath;
	}

	public String getBrowser() {
		String browserName = properties.getProperty("browser");
		if (browserName == null)
			logger.error("Browser Name Key value in Configuration.properties is not matched : " + browserName);
		return browserName;
	}

	public Boolean getBrowserWindowSize() {
		String windowSize = properties.getProperty("windowMaximize");
		if (windowSize != null)
			return Boolean.valueOf(windowSize);
		return true;
	}

	public String getReportConfigPath() {
		String reportConfigPath = properties.getProperty("reportConfigPath");
		if (reportConfigPath == null)
			logger.error(
					"Report Config Path not specified in the Configuration.properties file for the Key:reportConfigPath");
		return reportConfigPath;
	}

	public String getObjectRepoPath() {
		String objectConfigPath = properties.getProperty("ObjectRepoPath");
		if (objectConfigPath == null)
			logger.error(
					"Object Repo Path not specified in the Configuration.properties file for the Key:reportConfigPath");
		return objectConfigPath;
	}

	public String getDocumentType() {
		String template = System.getProperty("user.dir") + properties.getProperty("template");

		return template;
	}


	/*
	 * Mongo DB Host Name
	 */
	public String getMongoDBHostName() {
		String mongoHostName = properties.getProperty("MONGO_DB_HOSTNAME");
		if (mongoHostName == null || mongoHostName.isEmpty())
			logger.error("MONGO_DB_HOSTNAME not specified in the Configuration.properties file.");

		return mongoHostName;
	}

	/*
	 * Mongo DB port
	 */
	public int getMongoDBPort() {
		int mongoPort = Integer.parseInt(properties.getProperty("MODGO_DB_PORT"));
		if (mongoPort == 0)
			logger.error("MODGO_DB_PORT not specified in the Configuration.properties file.");

		return mongoPort;
	}

	/*
	 * Mongo DB User Name
	 */
	public String getMongoDBUserName() {
		String mongoUserName = properties.getProperty("MONGO_DB_USER_NAME");
		if (mongoUserName == null || mongoUserName.isEmpty())
			logger.error("MONGO_DB_USER_NAME not specified in the Configuration.properties file.");
		return mongoUserName;

	}

	/*
	 * Mongo DB Password
	 */
	public String getMongoDBPassword() {
		String mongoPassword = properties.getProperty("MONOG_DB_PASSWORD");
		if (mongoPassword == null || mongoPassword.isEmpty())
			logger.error("MONOG_DB_PASSWORD not specified in the Configuration.properties file.");
		return mongoPassword;

	}

	/*
	 * Mongo DB Collection
	 */
	public String getMongoDBName() {
		String mongoCollection = properties.getProperty("MONGO_DB_NAME");
		if (mongoCollection == null || mongoCollection.isEmpty())
			logger.error("MONGO_DB_NAME not specified in the Configuration.properties file.");

		return mongoCollection;
	}

	/*
	 * Debug Mode
	 */
	public boolean isDebugModeOn() {
		if (properties.getProperty("DEBUG_MODE") != null && !properties.getProperty("DEBUG_MODE").isEmpty()) {
			return Boolean.parseBoolean(properties.getProperty("DEBUG_MODE"));
		} else {
			return true;
		}
	}

	/*
	 * Mongo DB Collection
	 */
	public String getMongoDBCollectionName() {
		String mongoCollection = properties.getProperty("MONGO_DB_COLLECTION");
		if (mongoCollection == null || mongoCollection.isEmpty())
			logger.error("MONGO_DB_COLLECTION not specified in the Configuration.properties file.");
		return mongoCollection;
	}

	/*
	 * Mongo DB Debug Mode Collection
	 */
	public String getMongoDBDebugModeCollectionName() {
		String mongoDebugCollection = properties.getProperty("DEBUG_MODE_MONGO_DB_COLLECTION");
		if (mongoDebugCollection == null || mongoDebugCollection.isEmpty())
			logger.error("DEBUG_MODE_MONGO_DB_COLLECTION not specified in the Configuration.properties file.");
		return mongoDebugCollection;
	}
}

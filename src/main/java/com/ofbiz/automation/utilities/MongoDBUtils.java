package com.ofbiz.automation.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ofbiz.automation.common.Constants;
import com.ofbiz.automation.common.World;
import com.ofbiz.automation.libraries.ConfigFileReader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.bson.Document;

public class MongoDBUtils {
    static Logger logger = LogManager.getLogger(MongoDBUtils.class);

    /*
    * Store Results in Mongo DB Collection
    * */
    private World world;


    public MongoDBUtils(World world) {
        this.world = world;
    }

    public static List<Map<String, String>> testDataList = new ArrayList<>();
    public static List<Map<String, String>> generatedTestDataList = new ArrayList<>();
    public static List<Map<String, String>> apiTestDataList = new ArrayList<>();
    public static List<Map<String, String>> dataBaseInputList = new ArrayList<>();
    public static List<Map<String, String>> dataBaseOutputList = new ArrayList<>();

    public static boolean storeResultsIntoMongoDB() throws Exception {
        try {
            World world = Constants.world;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("driverType", world.getDriverType().toString());
            jsonObject.addProperty("locale", world.getLocale());
            if (world.getDriverType() == Constants.DRIVERTYPE.SAUCE && world.isMobile()) {
                jsonObject.addProperty("isMobile", true);
                jsonObject.addProperty("mobileDeviceName", world.getMobileDeviceName());
                jsonObject.addProperty("mobileDeviceOrientation", world.getMobileDeviceOrientation());
                jsonObject.addProperty("mobilePlatformVersion", world.getMobilePlatformVersion());
                jsonObject.addProperty("mobilePlatformName", world.getMobilePlatformName());
                jsonObject.addProperty("mobileBrowser", world.getMobileBrowser());
                jsonObject.addProperty("Environment",world.getTestEnvironment());

            } else if (world.getDriverType() == Constants.DRIVERTYPE.LOCAL && world.isMobile()) {
                jsonObject.addProperty("isMobile", true);
                jsonObject.addProperty("Browser", world.getBrowser());
                jsonObject.addProperty("browserVersion", world.getBrowserVersion());
                jsonObject.addProperty("browserVersion", world.getBrowserPlatform());

            } else {
                jsonObject.addProperty("Browser", world.getBrowser());
                jsonObject.addProperty("browserVersion", world.getBrowserVersion());
                jsonObject.addProperty("browserPlatform", world.getBrowserPlatform());
                jsonObject.addProperty("Environment",world.getTestEnvironment());
            }


            JsonArray testDataArray = new JsonArray();
            for(int i = 0; i <testDataList.size(); i++){
                JsonObject tdIndividualSce = new JsonObject();
                for (Map.Entry<String,String> entry : testDataList.get(i).entrySet()){
                    tdIndividualSce.addProperty(entry.getKey(),entry.getValue());
                }
                testDataArray.add(tdIndividualSce);
            }


            JsonArray generatedTestDataArray = new JsonArray();
            for(int i = 0; i <generatedTestDataList.size(); i++){
                JsonObject tdIndividualSce = new JsonObject();
                for (Map.Entry<String,String> entry : generatedTestDataList.get(i).entrySet()){
                    tdIndividualSce.addProperty(entry.getKey(),entry.getValue());
                }
                generatedTestDataArray.add(tdIndividualSce);
            }

            JsonArray apiTestDataArray = new JsonArray();
            for(int i = 0; i <apiTestDataList.size(); i++){
                JsonObject tdIndividualSce = new JsonObject();
                for (Map.Entry<String,String> entry : apiTestDataList.get(i).entrySet()){
                    tdIndividualSce.addProperty(entry.getKey(),entry.getValue());
                }
                apiTestDataArray.add(tdIndividualSce);
            }

            JsonArray dataBaseInputArray = new JsonArray();
            for(int i = 0; i <dataBaseInputList.size(); i++){
                JsonObject tdIndividualSce = new JsonObject();
                for (Map.Entry<String,String> entry : dataBaseInputList.get(i).entrySet()){
                    tdIndividualSce.addProperty(entry.getKey(),entry.getValue());
                }
                dataBaseInputArray.add(tdIndividualSce);
            }

            JsonArray dataBaseOutputArray = new JsonArray();
            for(int i = 0; i <dataBaseOutputList.size(); i++){
                JsonObject tdIndividualSce = new JsonObject();
                for (Map.Entry<String,String> entry : dataBaseOutputList.get(i).entrySet()){
                    tdIndividualSce.addProperty(entry.getKey(),entry.getValue());
                }
                dataBaseOutputArray.add(tdIndividualSce);
            }

            String jsonString = FileUtils.readFileToString(new File("target/json-cucumber-reports/cukejson.json"), "UTF-8");
            JsonArray resultArray = new Gson().fromJson(jsonString, JsonArray.class);
            JsonObject resultObject = new JsonObject();
            resultObject.add("result", resultArray);
            resultObject.add("metadata", jsonObject);
            resultObject.add("testData", testDataArray);
            resultObject.add("generatedData",generatedTestDataArray);
            resultObject.add("apiTestData",apiTestDataArray);
            resultObject.add("dataBaseInput",dataBaseInputArray);
            resultObject.add("dataBaseOutput",dataBaseOutputArray);

            Document doc = Document.parse(resultObject.toString());
            //Create the MONGO DB object to write the file into mongo
            String host = ConfigFileReader.getConfigFileReader().getMongoDBHostName();
            String databaseName = ConfigFileReader.getConfigFileReader().getMongoDBName();
            String cname = "";
            if (ConfigFileReader.getConfigFileReader().isDebugModeOn()) {
                cname = ConfigFileReader.getConfigFileReader().getMongoDBDebugModeCollectionName();
            } else {
                cname = ConfigFileReader.getConfigFileReader().getMongoDBCollectionName();
            }
            MongoClient client = new MongoClient(host, 27017);
            MongoDatabase database = client.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(cname);
            collection.insertOne(doc);
            logger.info("Sucessfully inserted into mongodb");
            //System.out.println("Sucessfully inserted into mongodb");
            client.close();
            return true;
        } catch (Exception e) {
            logger.info("Not Sucessfully inserted into mongodb");
           // System.out.println("Not Sucessfully inserted into mongodb");
            throw new Exception("Not able to store results into Mongo due to " + e.getMessage());
        }
    }
}

//package com.ofbiz.automation.utilities;
//
//import com.ofbiz.automation.common.World;
//import com.ofbiz.automation.libraries.ConfigFileReader;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.sql.*;
//
//public class DBConnectionUtilities {
//    public enum DBNames {EBS, IDM}
//
//    private Connection connection;
//    private Statement statement;
//    private World world;
//
//    Logger logger = LogManager.getLogger(DBConnectionUtilities.class);
//
//    public DBConnectionUtilities(World world) {
//        this.world = world;
//    }
//
//    public DBConnectionUtilities(DBNames dbNames, World world) throws Exception {
//        this.world = world;
//        Class.forName("oracle.jdbc.driver.OracleDriver");
//        connection = CreateConnection(dbNames,world.getTestEnvironment());
//        statement = connection.createStatement();
//    }
//
//    private Connection CreateConnection(DBNames dbNames,String env) throws SQLException {
//        String url = null;
//        ConfigDetails configFileReader = new ConfigDetails("db");
//        String user = configFileReader.dbProperties.getProperty(dbNames + "_"+env+"_USER_ID");
//        String password = ConfigDetails.dbProperties.getProperty(dbNames + "_"+env+"_PWD");
//        switch ((dbNames + "_"+env).toString().toLowerCase()) {
//            case "idm_uat":
//                url = String.format("@%s:%s/%s",
//                        ConfigDetails.dbProperties.getProperty("IDM_"+env+"_HOST_NAME"),
//                        ConfigDetails.dbProperties.getProperty("IDM_PORT"),
//                        ConfigDetails.dbProperties.getProperty("IDM_"+env+"_SERVICE_NAME"));
//                connection = DriverManager.getConnection("jdbc:oracle:thin:" + url, user, password);
//                break;
//            case "ebs_qa3":
//                url = String.format("@%s:%s/%s",
//                        ConfigDetails.dbProperties.getProperty("EBS_"+env+"_HOST_NAME"),
//                        ConfigDetails.dbProperties.getProperty("EBS_PORT"),
//                        ConfigDetails.dbProperties.getProperty("EBS_"+env+"_SERVICE_NAME"));
//                connection = DriverManager.getConnection("jdbc:oracle:thin:" + url, user, password);
//                break;
//            case "ebs_uat":
//                url = String.format("@%s:%s/%s",
//                        ConfigDetails.dbProperties.getProperty("EBS_"+env+"_HOST_NAME"),
//                        ConfigDetails.dbProperties.getProperty("EBS_PORT"),
//                        ConfigDetails.dbProperties.getProperty("EBS_"+env+"_SERVICE_NAME"));
//                connection = DriverManager.getConnection("jdbc:oracle:thin:" + url, user, password);
//                break;
//            default:
//                url = String.format("@%s:%s/%s",
//                        ConfigDetails.dbProperties.getProperty("IDM_"+env+"_HOST_NAME"),
//                        ConfigDetails.dbProperties.getProperty("IDM_PORT"),
//                        ConfigDetails.dbProperties.getProperty("IDM_"+env+"_SERVICE_NAME"));
//                connection = DriverManager.getConnection("jdbc:oracle:thin:" + url, user, password);
//                break;
//        }
//        logger.info("Db connection got created on " + dbNames);
//        return connection;
//    }
//
//    public ResultSet queryByDesignTable(String orderNumber) throws SQLException,InterruptedException {
//        String query = "select header_id, error_message, order_number, gnc_process_flag, process_type, by_design_order_status, bonus_date, error_message, cust_createupgrade_flag, order_type, zero_out_Shakeology from XXPP.XXPP_BYDESIGN_ORDER_headers"
//                + " WHERE order_number = '" + orderNumber + "'";
//        logger.info("Executing the query " + query);
//        return waitForResult(query,"GNC_PROCESS_FLAG","P");
//    }
//
//    public ResultSet queryICentrisTable(String orderNumber) throws SQLException,InterruptedException {
//        String query = "select *from apps.XXPP_ICENTRIS_EXTRACT_HEADERS where order_number = '" + orderNumber + "'";
//        world.getDataBaseInputTestDataJson().put("queryICentrisTable_"+orderNumber ,"select *from apps.XXPP_ICENTRIS_EXTRACT_HEADERS where order_number = '" + orderNumber + "'");
//        logger.info("Executing the query " + query);
//        waitForResult(query,"process_Flag","S");
//        return waitForResult(query,"ORDER_STATUS","POSTED");
//    }
//
//    public ResultSet queryUsersAreProvisoned(String email) throws SQLException, InterruptedException {
//        String schema = world.getTestEnvironment().equalsIgnoreCase("uat") ? "prodoim_oim" : "qa3oim_oim";
//        String query = "select ost.ost_status USER_STATUS, obj.obj_name APPLICATION, obj.obj_key\n" +
//                "from "+schema+".oiu\n" +
//                "inner join "+schema+".ost on oiu.ost_key = ost.ost_key\n" +
//                "inner join "+schema+".obi on oiu.obi_key = obi.obi_key\n" +
//                "inner join "+schema+".obj on obi.obj_key = obj.obj_key\n" +
//                "where oiu.usr_key=(select usr_key from "+schema+".usr where USR_EMAIL ='" + email + "')";
//        logger.info("Exeucting the query " + query);
//        return waitForResult(query,"USER_STATUS","provisioned");
//    }
//
//    public ResultSet ebsOrderUpdate(String email) throws SQLException {
//        String query = "select hp.attribute4 byd_sponsor_id,hca.attribute14 Customer_Role,hp.attribute3 byd_cust_id, hp.attribute2 byd_coach_id " +
//                " from apps.hz_parties hp, apps.hz_cust_accounts hca " +
//                " where hp.party_id = hca.party_id and UPPER(hp.EMAIL_ADDRESS) ='" + email + "'";
//        logger.info("Exeucting the query " + query);
//        return statement.executeQuery(query);
//    }
//
//    public ResultSet executeQuery(String query) throws SQLException {
//        logger.info("Exeucting the query " + query);
//        return statement.executeQuery(query);
//    }
//
//    public  ResultSet executeQueryUsingScrollableResultSet(String query) throws SQLException {
//        Statement scrollableStatement = connection.createStatement(
//                ResultSet.TYPE_SCROLL_INSENSITIVE,
//                ResultSet.CONCUR_READ_ONLY);
//        logger.info("Exeucting the query " + query);
//        return scrollableStatement.executeQuery(query);
//    }
//
//    public ResultSet waitForResult(String query) throws SQLException, InterruptedException {
//        ResultSet resultSet = null;
//        int count = 0;
//        do {
//            resultSet = executeQueryUsingScrollableResultSet(query);
//            if (resultSet.next()) {
//                resultSet.beforeFirst();
//                break;
//            }else{
//                Thread.sleep(60 * 1000);
//                count++;
//            }
//        } while (count < ConfigFileReader.getConfigFileReader().getdataSyncTime());
//        return resultSet;
//    }
//
//    public ResultSet waitForResult(String query, String columnName, String columnValue) throws SQLException, InterruptedException {
//        ResultSet resultSet = null;
//        int count = 0;
//        do {
//            resultSet = executeQueryUsingScrollableResultSet(query);
//            if (resultSet.next()) {
//                if(resultSet.getString(columnName).toLowerCase().equalsIgnoreCase(columnValue.toLowerCase())){
//                    resultSet.beforeFirst();
//                    break;
//                }else {
//                    resultSet.beforeFirst();
//                    Thread.sleep(60 * 1000);
//                    count++;
//                }
//            }else{
//                Thread.sleep(60 * 1000);
//                count++;
//            }
//        } while (count < ConfigFileReader.getConfigFileReader().getdataSyncTime());
//        return resultSet;
//    }
//}

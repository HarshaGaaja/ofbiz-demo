package com.ofbiz.automation.utilities;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RestAPI {
    public String endpoint;

    public RestAPI(String endpoint) {
        this.endpoint = endpoint;
    }

    public Object APIRequest(String methodType, String serviceurl, String token){
        String responseTxt = "";
        HttpClient httpClient = null;
        HttpResponse response = null;
        Object json = null;

        try {
            httpClient = HttpClients.createDefault();
            if (methodType.equalsIgnoreCase("get")) {
                HttpGet getRequest = new HttpGet(endpoint + "/" + serviceurl);
                getRequest.addHeader("accept", "application/json");
                getRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                response = httpClient.execute(getRequest);
            } else if (methodType.equalsIgnoreCase("post")) {
                HttpPost postRequest = new HttpPost(endpoint + "/" + serviceurl);
                response = httpClient.execute(postRequest);
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            //System.out.println("Output from Server .... \n Response Code: " + response.getStatusLine().getStatusCode());
            while ((output = br.readLine()) != null) {
                responseTxt += output;
                //System.out.println(output);
            }

            httpClient.getConnectionManager().shutdown();
            JSONParser parser = new JSONParser();
            json = parser.parse(responseTxt);
        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json;
    }
}

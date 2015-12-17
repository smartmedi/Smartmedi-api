package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.smartmedi.app.api.util.AuthenticationCredentials;
import com.smartmedi.app.api.util.AuthenticationUtil;
import com.smartmedi.app.api.util.NotificationUtil;
import com.smartmedi.dbconnector.CassandraConnector;
import com.smartmedi.plivoconnector.PlivoConnector;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by vignesh on 1/11/15.
 */
public class NotificationController {
    CassandraConnector cassandraConnector;
    public static JSONParser parser = new JSONParser();
    public NotificationController(CassandraConnector cassandraConnector) {
        this.cassandraConnector = cassandraConnector;

    }

    public static void sendNotification(Request request,Response response){
        String line;
        StringBuffer jsonString = new StringBuffer();
        try {

            URL url = new URL("https://api.quickblox.com/session.json");

            //escape the double quotes in json string
            String payload="{\"application_id\":\"21815\",\"auth_key\":\"jbEvVa4E3A5mtyF\",\"user\":{\"login\":ganeshs.srinivasan@gmail.com,\"password\":viki1991}}";

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }



    }

}

package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.plivo.helper.api.client.RestAPI;
import com.smartmedi.dbconnector.CassandraConnector;
import com.smartmedi.plivoconnector.PlivoConnector;
import org.apache.avro.util.Utf8;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vignesh on 16/4/15.
 */
public class RegisterController {
    public static CassandraConnector cassandraConnector;
    public static PlivoConnector plivoConnector;
    public static JSONParser parser = new JSONParser();

    public RegisterController(CassandraConnector cassandraConnector, PlivoConnector plivoConnector) {
        this.cassandraConnector = cassandraConnector;
        this.plivoConnector = plivoConnector;
    }

    public static void verifyRegisteredUser(Request request,Response response){

        try {

            Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
            JSONObject json = (JSONObject) obj;
            String phone=json.get("phone").toString();
            List<Row> row=cassandraConnector.getRowListWhere("registered_users","phone_number",phone);
            Boolean isDataExist=false;
            if(row!=null && row.size()>0){
                isDataExist=true;
            }

            if (isDataExist) {
                cassandraConnector.updateRow("registered_users", "1", "status", "phone_number", phone);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void sendVerificationCode(Request request, Response response) {

        String phone = request.getHeader("phone").toString();

        String resp = plivoConnector.sendSms(phone, "MAMDY2YZCWYJJIMJVMMZ", "YjExMTRhMDYyZTZhOGI4NmViYTkzY2Q5YmE5ZjE1");

        List<Row> rowList = cassandraConnector.getID("smartmedi", "users");
        Row row = rowList.get(0);

        Long user_id = row.getLong(0);

        List<Row> rows=cassandraConnector.getRowListWhere("registered_users","phone_number",phone);
        Boolean isDataExist=false;
        if(rows!=null && rows.size()>0){
            isDataExist=true;
        }

        if (!isDataExist) {
            Map<String, Object> registeredUser = new HashMap<String, Object>();
            registeredUser.put("user_id", user_id);
            registeredUser.put("phone_number", phone);
            registeredUser.put("status", "0");
            cassandraConnector.insertDetails("registered_users", registeredUser);
        }
        else
        {
            cassandraConnector.updateRow("registered_users", "0", "status", "phone_number", phone);
        }

    }
}

package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.plivo.helper.api.client.RestAPI;
import com.smartmedi.app.api.authentication.Encrypt;
import com.smartmedi.app.api.jsonparser.*;
import com.smartmedi.dbconnector.*;
import com.smartmedi.plivoconnector.*;
import org.apache.avro.util.Utf8;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static JSONObject sendVerificationCode(Request request, Response response) throws IOException, ParseException {

        Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
        JSONObject json = (JSONObject) obj;
        String phone=json.get("phone").toString();

        String resp = plivoConnector.sendSms(phone, "MAMDY2YZCWYJJIMJVMMZ", "YjExMTRhMDYyZTZhOGI4NmViYTkzY2Q5YmE5ZjE1");

        if(!resp.equals("error")) {
            List<Row> rowList = cassandraConnector.getID("smartmedi", "users");
            Row row = rowList.get(0);

            Long user_id = row.getLong(0);

            List<Row> rows = cassandraConnector.getRowListWhere("registered_users", "phone_number", phone);
            Boolean isDataExist = false;
            if (rows != null && rows.size() > 0) {
                isDataExist = true;
            }

            if (!isDataExist) {
                Map<String, Object> registeredUser = new HashMap<String, Object>();
                registeredUser.put("user_id", user_id);
                registeredUser.put("phone_number", phone);
                registeredUser.put("status", "0");
                System.out.println(phone);
                cassandraConnector.insertDetails("registered_users", registeredUser);

            } else {
                cassandraConnector.updateRow("registered_users", "0", "status", "phone_number", phone);

            }
            return JsonParser.getInstance().successResponse(resp);
        }
        else
        {
            return JsonParser.getInstance().errorResponse();
        }

    }

    public static void saveOrders(Request request,Response response){

        try{
            Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
            JSONObject json = (JSONObject) obj;
            String items=((JSONArray)json.get("items")).toJSONString();
            double amount=Double.parseDouble(json.get("amount").toString());
            String pharmacy_name=json.get("pharmacy_name").toString();
            String status="pending";
            String country=json.get("country").toString();
            String pincode=json.get("pincode").toString();
            String city=json.get("city").toString();
            String zone=json.get("zone").toString();
            String houseNumber=json.get("house_number").toString();
            String streetName=json.get("streetName").toString();
            String landmark=json.get("landmark").toString();
            Long user=Long.parseLong(json.get("user_id").toString());

            Row row=cassandraConnector.getRowWhere("registered_pharmacy","pharmacy_name",pharmacy_name);
            Long ph_id=row.getLong("ph_id");

            List<Row> rows=cassandraConnector.getID("smartmedi","id");
            Row id = rows.get(0);
            Long order_id = id.getLong(0);

            Map<String,Object> data=new HashMap<String, Object>();
            data.put("order_id",order_id);
            data.put("amount",amount);
            data.put("ph_id",ph_id);
            data.put("user_id",user);
            data.put("ordered_items",items);
            data.put("status",status);
            cassandraConnector.insertDetails("orders",data);
            
            Map<String,Object> order_address=new HashMap<String, Object>();
            order_address.put("order_id",order_id);
            order_address.put("city",city);
            order_address.put("country",country);
            order_address.put("house_number",houseNumber);
            order_address.put("landmark",landmark);
            order_address.put("street",streetName);
            order_address.put("zone",zone);
            order_address.put("pincode",pincode);
            cassandraConnector.insertDetails("order_address",order_address);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void saveProfile(Request request,Response response){
        try{
            Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
            JSONObject json = (JSONObject) obj;
            Map<String,Object> map=new HashMap<String,Object>();
            String profile_name=json.get("profile_name").toString();
            String profile_image=json.get("profile_image").toString();
            String email=json.get("email").toString();
            String phone=json.get("phone").toString();

            map.put("profile_name",profile_name);
            map.put("phone",phone);
            map.put("email",email);
            List<Row> rows=cassandraConnector.getRowListWhere("registered_users","phone_number",phone);
            Integer user_id=0;
            for(Row row:rows){
                user_id=row.getInt("user_id");
                map.put("user_id",user_id);
            }

            byte[] bytes=Base64.decodeBase64(profile_image);
            try{
                File file=new File("/home/vignesh/usersprofile");
                if(!file.exists()){
                    boolean result = false;
                    try{
                         file.mkdir();
                    }
                    catch(SecurityException se){
                         se.printStackTrace();
                    }
                }

                OutputStream outputStream=new FileOutputStream("/home/vignesh/usersprofile/"+user_id);
                outputStream.write(bytes);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }

            cassandraConnector.insertDetails("user_profile",map);
        }
        catch(Exception ex){
           ex.printStackTrace();
        }
    }

    public static JSONObject registerPharmacy(Request request,Response response){
        try {
            Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
            JSONObject json = (JSONObject) obj;
            String pharmacy_name=json.get("pharmacy_name").toString();
            String email=json.get("email").toString();
            String phone=json.get("phone").toString();
            String password=json.get("password").toString();
            Encrypt.setKey();
            Encrypt.encrypt(password);
            String enc_pass=Encrypt.getEncryptedString();
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("pharmacy_name",pharmacy_name);
            map.put("email",email);
            map.put("phone",phone);
            map.put("password",enc_pass);
            List<Row> rows=cassandraConnector.getRowListWhere("registered_pharmacy", "email",email);
            if(rows.size()>=1){
                return JsonParser.getInstance().errorResponse("Username already exist");
            }
            else {
                List<Row> rowList = cassandraConnector.getID("smartmedi", "users");
                Row row = rowList.get(0);
                Long ph_id = row.getLong(0);
                map.put("ph_id", ph_id);
                cassandraConnector.insertDetails("registered_pharmacy", map);
                return JsonParser.getInstance().successResponse(String.valueOf(ph_id));
            }
        }
        catch(Exception exception){
         exception.printStackTrace();
        }
        return JsonParser.getInstance().errorResponse();
    }

    public static void registerUser(Request request,Response response) throws IOException, ParseException {
        Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
        JSONObject json = (JSONObject) obj;
        String user_name=json.get("user_name").toString();
        String phone_number=json.get("contact_no").toString();
        String password=json.get("password").toString();
        String email=json.get("email").toString();
        Encrypt.setKey();
        Encrypt.encrypt(password);
        String enc_pass=Encrypt.getEncryptedString();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("phone_number",phone_number);
        map.put("customer_name",user_name);
        map.put("password",enc_pass);
        map.put("email",email);
        List<Row> rowList = cassandraConnector.getID("smartmedi", "users");
        Row row = rowList.get(0);
        Long user_id = row.getLong(0);
        map.put("status","1");
        map.put("user_id",user_id);
        cassandraConnector.insertDetails("registered_users",map);
    }

    public static JSONObject signIn(Request request,Response response){
            try {
                Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
                JSONObject json = (JSONObject) obj;
                String username=json.get("email").toString();
                String password=json.get("password").toString();

                List<Row> rows=cassandraConnector.getRowListWhere("registered_pharmacy", "email", username);
                for(Row row:rows){
                    String pass=row.getString("password");
                    Encrypt.setKey();
                    Encrypt.decrypt(pass);
                }
                String encrypt_pass=Encrypt.getDecryptedString();
                if(encrypt_pass.equals(password)){
                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();
                    Encrypt.setKey();
                    Encrypt.encrypt(username);
                    String enc_username=Encrypt.getEncryptedString();
                    return JsonParser.getInstance().successResponse(randomUUIDString+enc_username);
                }
                else
                {
                    return JsonParser.getInstance().errorResponse();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        return JsonParser.getInstance().errorResponse();
    }

}

package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.smartmedi.dbconnector.Neo4jConnector;
import com.smartmedi.reader.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;
import com.smartmedi.app.api.jsonparser.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Exception;
import java.lang.System;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4jController{
    public static Neo4jConnector neo4jConnector;
    public static CsvReader csvReader;
    public static JSONParser parser = new JSONParser();
    public Neo4jController(Neo4jConnector connector,CsvReader csvReader) {
        this.neo4jConnector=connector;
        this.csvReader=csvReader;
    }

    public static JSONObject registerPharmacyLocation(Request request, Response response){
        JsonParser jsonParser=JsonParser.getInstance();
        System.out.println("done");
        try{
            Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
            JSONArray jsonArray = (JSONArray) obj;
            Map<Integer,List<String>> map=new HashMap<Integer,List<String>>();

            for(int i=0;i<jsonArray.size();i++) {
                List<String> list=new ArrayList<String>();
                JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                String pharmacy_name = jsonObject.get("pharmacy_name").toString();
                String latitude = jsonObject.get("latitude").toString();
                String longitude = jsonObject.get("longitude").toString();
                String ph_id = jsonObject.get("phID").toString();
                list.add(pharmacy_name);
                list.add(latitude);
                list.add(longitude);
                list.add(ph_id);
                map.put(i,list);
            }
            neo4jConnector.insertData(map);

            return jsonParser.successResponse("success");

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return jsonParser.errorResponse();
    }



}
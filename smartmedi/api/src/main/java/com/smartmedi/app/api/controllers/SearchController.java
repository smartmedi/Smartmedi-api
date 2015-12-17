package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.google.gson.Gson;
import com.smartmedi.dbconnector.CassandraConnector;
import com.smartmedi.dbconnector.Neo4jConnector;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by vignesh on 19/7/15.
 */
public class SearchController {

    CassandraConnector connector;
    Neo4jConnector neo4jConnector;
    public static JSONParser parser = new JSONParser();

    public SearchController(CassandraConnector cassandraConnector,Neo4jConnector neo4jConnector){
        this.connector=cassandraConnector;
        this.neo4jConnector=neo4jConnector;
    }

    public JSONArray search(Request request,Response response) throws IOException, ParseException {

        String latitude= request.getHeader("latitude").toString();
        String longitude=request.getHeader("longitude").toString();
        String distance=request.getHeader("distance").toString();
        List<String> pharmacy=neo4jConnector.getNode(latitude,longitude,distance);
        String pharmacies = new Gson().toJson(pharmacy);
        JSONArray jsonObject=(JSONArray)parser.parse(pharmacies);
        return jsonObject;
    }

    public JSONArray getOrders(Request request,Response response) throws IOException, ParseException{

          List<Map<String, Object>> orders_list = new ArrayList<Map<String, Object>>();

          String pharmacy_name=request.getHeader("pharmacy_name").toString();
          System.out.println(pharmacy_name);
          Row row = this.connector.getRowWhere("registered_pharmacy", "pharmacy_name", pharmacy_name);
          Long ph_id = row.getLong("ph_id");
          List<Row> rows = this.connector.getRowListUsingInt("orders", "ph_id", ph_id);

          for (Row order : rows) {
              HashMap<String, Object> orders = new HashMap<String, Object>();
              String items=order.getString("ordered_items");
              double amount = order.getDouble("amount");
              Long order_id=order.getLong("order_id");
              String order_status = order.getString("status");
              orders.put("order_id",order_id);
              orders.put("items", items);
              orders.put("amount", amount);
              orders.put("status", order_status);
              orders_list.add(orders);
          }

          String orders = new Gson().toJson(orders_list);
          JSONArray jsonObject = (JSONArray) parser.parse(orders);
          return jsonObject;
    }

    public JSONArray getTotalUsers(Request request,Response response) throws IOException, ParseException{
        Set<Long> users =new TreeSet<Long>();
        String pharmacy_name=request.getHeader("pharmacy_name").toString();
        Row row = this.connector.getRowWhere("registered_pharmacy", "pharmacy_name", pharmacy_name);
        Long ph_id = row.getLong("ph_id");
        List<Row> rows = this.connector.getRowListUsingInt("orders", "ph_id", ph_id);
        for (Row order : rows) {
             Long user_id=order.getLong("user_id");
             users.add(user_id);
        }
        String users_list = new Gson().toJson(users);
        JSONArray jsonObject = (JSONArray) parser.parse(users_list);
        return jsonObject;
    }

    public JSONObject getMedicineslist(Request request,Response response){
            int token = Integer.parseInt(request.getHeader("token").toString());
            int limit = token + 100;
            String values="";
            for (int i = token; i < limit; i++) {
                values+=i+",";
            }
            List<Row> rows=connector.getRowLists("medicines_stock","med_id",values.substring(0, values.length()-1));
            JSONArray jsonArray=new JSONArray();
            JSONObject jsonObject=new JSONObject();
            Long med_id=null;
            for (Row row : rows){
                String med_name=row.getString("med_name");
                med_id=row.getLong("med_id");
                jsonArray.add(med_name);

            }
            jsonObject.put("med_name",jsonArray);
            jsonObject.put("nextToken",med_id);
            return jsonObject;
    }

}
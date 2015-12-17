package com.smartmedi.app.api.controllers;

import com.datastax.driver.core.Row;
import com.smartmedi.dbconnector.CassandraConnector;
import com.smartmedi.dbconnector.Neo4jConnector;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restexpress.Request;
import org.restexpress.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vignesh on 7/12/15.
 */
public class DatamigrationController {
    CassandraConnector connector;
    public static JSONParser parser = new JSONParser();

    public DatamigrationController(CassandraConnector cassandraConnector){
        this.connector=cassandraConnector;
    }

    public void saveMedicineData(Request request,Response response) throws IOException, ParseException {
       try {
           List<String> lines = new ArrayList<String>();
           BufferedReader br = new BufferedReader(new FileReader("/home/vignesh/Desktop/smartmedi-data/new-data/alldata.csv"));
           br.readLine();

           String line = br.readLine();
           while (line != null) {
               //lines.add(line);
               HashMap<String, Object> medicines = new HashMap<String, Object>();
               List<Row> rows = connector.getMedicineID("smartmedi", "medicines");
               Row id = rows.get(0);
               Long med_id = id.getLong(0);
               medicines.put("med_id", med_id);

               line = br.readLine();
               System.out.println(line);

               if(line!=null) {
                   if (line.length() > 0) {
                          if(line.split(",").length>=1) {
                              System.out.println(line.split(",")[0]);
                              medicines.put("med_name", line.split(",")[0]);
                          }if(line.split(",").length>2) {
                           System.out.println(line.split(",")[1]);
                              medicines.put("manufacturer_name", line.split(",")[1]);
                          }if(line.split(",").length>4) {
                           System.out.println(line.split(",")[3]);
                              medicines.put("form", line.split(",")[3]);
                          }if(line.split(",").length>6) {
                              medicines.put("unit_price", Double.parseDouble(line.split(",")[5]));
                          }if(line.split(",").length>7) {
                              medicines.put("package_price", Double.parseDouble(line.split(",")[6]));
                          }if(line.split(",").length>8) {
                              medicines.put("package_qty", Double.parseDouble(line.split(",")[7]));
                          }if(line.split(",").length>9) {
                              medicines.put("constituent", line.split(",")[8]);
                          }if(line.split(",").length==11) {
                              medicines.put("constituent_strength", line.split(",")[10]);
                          }

                         connector.insertDetails("medicines_stock", medicines);
                   }
               }
           }
           System.out.println("completed");
       }
       catch (Exception ex){
           ex.printStackTrace();
       }
    }
}

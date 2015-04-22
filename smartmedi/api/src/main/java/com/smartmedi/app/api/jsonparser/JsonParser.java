package com.smartmedi.app.api.jsonparser;

import org.codehaus.jettison.json.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by vignesh on 23/4/15.
 */
public class JsonParser {
    private static JsonParser jsonParser=null;



    private JsonParser(){

    }
    public static JsonParser getInstance(){
        if(jsonParser==null){
            jsonParser=new JsonParser();
        }
        return jsonParser;
    }
    public JSONObject jsonObject;
    public JSONArray jsonArray;

    public JSONObject successResponse(String msg){
        jsonObject=new JSONObject();
        jsonObject.put("rsp",200);
        jsonObject.put("msg",msg);
        return jsonObject;
    }

    public JSONObject errorResponse(){
        jsonObject=new JSONObject();
        jsonObject.put("rsp",400);
        jsonObject.put("msg","error");
        return jsonObject;
    }
}

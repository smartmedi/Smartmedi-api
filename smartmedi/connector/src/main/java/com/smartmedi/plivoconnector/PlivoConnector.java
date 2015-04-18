package com.smartmedi.plivoconnector;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.LinkedHashMap;

/**
 * Created by vignesh on 17/4/15.
 */
public class PlivoConnector {



    public String sendSms(String receiver,String auth_key,String auth_token){

        RestAPI restAPI=new RestAPI(auth_key,auth_token,"v1");
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("src","+919962609454");
        parameters.put("dst",receiver);
        int randomPIN = (int)(Math.random()*9000)+1000;
        parameters.put("text",String.valueOf(randomPIN));
        try {

            MessageResponse msgResponse = restAPI.sendMessage(parameters);
            System.out.print(getFields(msgResponse));
            if (msgResponse.serverCode == 202) {
                System.out.println(msgResponse);
              return "success";
            } else {
                System.out.println(msgResponse.error);
                return "error";
            }

        } catch (PlivoException e) {
            e.printStackTrace();
            return "error";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "error";
        }
    }
    public static String getFields(Object obj) throws IllegalAccessException {
        StringBuffer buffer = new StringBuffer();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                Object value = f.get(obj);
                buffer.append(f.getName());
                buffer.append("=");
                buffer.append("" + value);
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
}

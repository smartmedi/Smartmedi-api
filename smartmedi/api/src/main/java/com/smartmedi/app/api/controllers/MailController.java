package com.smartmedi.app.api.controllers;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.restexpress.Request;
import org.restexpress.Response;

import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


/**
 * Created by vignesh on 16/12/15.
 */
public class MailController {
    public static JSONParser parser = new JSONParser();
    public void sendMail(Request request,Response response)  {
      try{
          // Recipient's email ID needs to be mentioned.
          Object obj = parser.parse(IOUtils.toString(request.getBodyAsStream()));
          JSONObject json = (JSONObject) obj;
          String to=json.get("to").toString();

          // Sender's email ID needs to be mentioned
          String from = "ganeshs.srinivasan@gmail.com";

          // Assuming you are sending email from localhost
          String host = "vignesh-Ideapad-Z570.localhost";

          // Get system properties
          Properties properties = System.getProperties();

          // Setup mail server
          properties.setProperty("mail.smtp.gmail", host);

          // Get the default Session object.
          Session session = Session.getDefaultInstance(properties);


            // Create a default MimeMessage object.
          MimeMessage message = new MimeMessage(session);

          message.setFrom(new InternetAddress(from));
          // Set To: header field of the header.
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          // Set Subject: header field
          message.setSubject("");
          // Now set the actual message
          message.setText("This is actual message");
          // Send message
          Transport.send(message);
          System.out.println("Sent message successfully....");

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

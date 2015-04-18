package com.smartmedi.app.api.Routes;

import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;
import org.springframework.context.ApplicationContext;

/**
 * Created by vignesh on 17/4/15.
 */
public class RoutesBuilder {

    public static void buildRoutes(RestExpress server,ApplicationContext context){
        server.uri("/api/send/sms/{phone}", context.getBean("registerController")).action("sendVerificationCode", HttpMethod.GET);
        server.uri("/api/verify",context.getBean("registerController")).action("verifyRegisteredUser",HttpMethod.POST);
    }
}

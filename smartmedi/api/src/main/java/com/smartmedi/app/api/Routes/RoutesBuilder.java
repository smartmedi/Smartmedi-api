package com.smartmedi.app.api.Routes;

import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;
import org.springframework.context.ApplicationContext;

/**
 * Created by vignesh on 17/4/15.
 */
public class RoutesBuilder {

    public static void buildRoutes(RestExpress server,ApplicationContext context){
        server.uri("/api/send/sms", context.getBean("registerController")).action("sendVerificationCode", HttpMethod.POST);
        server.uri("/api/verify",context.getBean("registerController")).action("verifyRegisteredUser",HttpMethod.POST);
        server.uri("/api/register/pharmacy",context.getBean("neo4jController")).action("registerPharmacyLocation",HttpMethod.POST);
        server.uri("/api/search",context.getBean("searchController")).action("search",HttpMethod.GET);
        server.uri("/api/save/profile",context.getBean("registerController")).action("saveProfile",HttpMethod.POST);
        server.uri("/api/signup/dashboard",context.getBean("registerController")).action("registerPharmacy",HttpMethod.POST);
        server.uri("/api/signup/users",context.getBean("registerController")).action("registerUser",HttpMethod.POST);
        server.uri("/api/signin",context.getBean("registerController")).action("signIn",HttpMethod.POST);
        server.uri("/api/send/notification/message",context.getBean("notificationController")).action("sendNotification", HttpMethod.POST);
        server.uri("/api/orders",context.getBean("searchController")).action("getOrders",HttpMethod.GET);
        server.uri("/api/users",context.getBean("searchController")).action("getTotalUsers",HttpMethod.GET);
        server.uri("/api/savemedicines",context.getBean("datamigrationController")).action("saveMedicineData",HttpMethod.GET);
        server.uri("/api/getMedicinesList",context.getBean("searchController")).action("getMedicineslist",HttpMethod.GET);
        server.uri("/api/send/mail",context.getBean("mailController")).action("sendMail",HttpMethod.POST);
    }
}

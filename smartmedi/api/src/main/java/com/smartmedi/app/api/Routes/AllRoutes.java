package com.smartmedi.app.api.Routes;

import org.restexpress.RestExpress;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vignesh on 17/4/15.
 */
public class AllRoutes {

    public static void defineRoutes(RestExpress restExpress,AnnotationConfigApplicationContext appContext){
        RoutesBuilder.buildRoutes(restExpress,appContext);
    }
}

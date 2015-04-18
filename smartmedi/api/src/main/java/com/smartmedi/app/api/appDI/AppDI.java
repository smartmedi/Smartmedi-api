package com.smartmedi.app.api.appDI;

import com.smartmedi.app.api.controllers.RegisterController;
import com.smartmedi.dbconnector.CassandraConnector;
import com.smartmedi.plivoconnector.PlivoConnector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by vignesh on 16/4/15.
 */
public class AppDI {

    @Inject
    org.apache.commons.configuration.Configuration config;

    @Inject
    BeanFactory beanFactory;

    @Bean
    @Singleton
    public RegisterController registerController(){

     return new RegisterController(beanFactory.getBean("cassandraConnector", CassandraConnector.class),beanFactory.getBean("plivoConnector",PlivoConnector.class));
    }

    @Bean
    @Singleton
    public CassandraConnector cassandraConnector() {
        String cassandra_ip=config.getString("cassandra.ip");
        String keyspace=config.getString("cassandra.keyspace");
        return new CassandraConnector(cassandra_ip,keyspace);
    }

    @Bean
    @Singleton
    public PlivoConnector plivoConnector() {
        return new PlivoConnector();
    }
}

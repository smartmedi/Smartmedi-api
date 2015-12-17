package com.smartmedi.app.api.appDI;

import com.smartmedi.app.api.controllers.*;
import com.smartmedi.dbconnector.*;
import com.smartmedi.dbconnector.Neo4jConnector;
import com.smartmedi.plivoconnector.*;
import com.smartmedi.reader.CsvReader;
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
    public Neo4jController neo4jController(){
        return new Neo4jController(beanFactory.getBean("neo4jConnector", Neo4jConnector.class),beanFactory.getBean("csvReader",CsvReader.class));
    }

    @Bean
    @Singleton
    public SearchController searchController(){
        return new SearchController(beanFactory.getBean("cassandraConnector",CassandraConnector.class),beanFactory.getBean("neo4jConnector",Neo4jConnector.class));
    }

    @Bean
    @Singleton
    public MailController mailController(){
        return new MailController();
    }


    @Bean
    @Singleton
    public NotificationController notificationController(){
        return new NotificationController(beanFactory.getBean("cassandraConnector",CassandraConnector.class));
    }
    @Bean
    @Singleton
    public DatamigrationController datamigrationController(){
        return new DatamigrationController(beanFactory.getBean("cassandraConnector",CassandraConnector.class));
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
    public Neo4jConnector neo4jConnector() {
        String neo4jpath=config.getString("neo4j.path");
        return new Neo4jConnector(neo4jpath);
    }

    @Bean
    @Singleton
    public CsvReader csvReader() {
      return new CsvReader();
    }

    @Bean
    @Singleton
    public PlivoConnector plivoConnector() {
        return new PlivoConnector();
    }
}

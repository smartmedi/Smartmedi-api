package com.smartmedi.dbconnector;
/**
 * Created by vignesh on 17/4/15.
 */

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CassandraConnector {
    private static Cluster cluster;
    private static Session session;
    final private Logger log = LoggerFactory.getLogger(this.getClass());

    public CassandraConnector(String cassandraIP, String keyspace) {
        cluster = Cluster.builder().addContactPoint(cassandraIP).build();
        session = cluster.connect(keyspace);
    }

    public void insertDetails(String column_family, Map<String, Object> details) {
        Insert insert = QueryBuilder.insertInto(column_family).values(Arrays.copyOf(details.keySet().toArray(), details.keySet().toArray().length, String[].class), details.values().toArray());
        session.execute(insert);
    }

    public  Row getRowWhere(String column_family, String indexed_column_name, String value) {
        ResultSet result = session.execute("select * from " + column_family + " where " + indexed_column_name + "=\'" + value + "\'" + " ALLOW FILTERING");
        return  result.one();
    }

    public List<Row> getRowListWhere(String column_family, String indexed_column_name, String value) {
        ResultSet result = session.execute("select * from " + column_family + " where " + indexed_column_name + "=\'" + value + "\'" + " ALLOW FILTERING");
        return result.all();
    }

    public List<Row> getRowListUsingInt(String column_family, String indexed_column_name, Long value) {
        ResultSet result = session.execute("select * from " + column_family + " where " + indexed_column_name + "=" + value + " ALLOW FILTERING");
        return result.all();
    }


    public void updateRow(String column_family, String value,String update_row,String indexed_column_name,String indexed_value) {
        PreparedStatement incHitsQ = session.prepare(
                "UPDATE "+column_family+" SET "+update_row+"= ? WHERE "+ indexed_column_name +" = ?");
        BoundStatement bs = incHitsQ.bind(value,indexed_value);
        session.execute(bs);
    }

    public List<Row> getID(String keyspace, String table) {
        PreparedStatement incHitsQ = session.prepare(
                "UPDATE "+table+" SET value = value + ? WHERE KEY = 'row-key' and column1 = 'id' ");
        BoundStatement bs = incHitsQ.bind(100L);
        Statement select = QueryBuilder.select("value").from(keyspace, table);
        session.execute(bs);
        ResultSet results = session.execute(select);
        return results.all();
    }

    public List<Row> getMedicineID(String keyspace, String table) {
        //PreparedStatement incHitsQ = session.prepare(
          //      "UPDATE "+table+" SET value = value + ? WHERE KEY = 'med-id' and medicine_id = 'id' ");
        //BoundStatement bs = incHitsQ.bind(100L);
        Statement select = QueryBuilder.select("value").from(keyspace, table);
        session.execute("UPDATE "+table+" SET value = value + 1 WHERE KEY = 'med-id' and medicine_id = 'id' ");
        ResultSet results = session.execute(select);
        return results.all();
    }

    public List<Row>  getRowLists(String table,String index,String values) {
        ResultSet result=session.execute("select * FROM " + table + " where " + index + " in (" + values + ")");
        return result.all();
    }
}
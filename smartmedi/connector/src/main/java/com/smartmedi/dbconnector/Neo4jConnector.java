package com.smartmedi.dbconnector;

import org.neo4j.gis.spatial.indexprovider.SpatialIndexProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vignesh on 16/6/15.
 */
public class Neo4jConnector {
    public static GraphDatabaseService graphDatabaseService;

    public Neo4jConnector(String path) {
        this.graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(path);
    }

    public void insertData(Map<Integer,List<String>> pharmacy_data) {
        Transaction transaction = this.graphDatabaseService.beginTx();

        Index<Node> index = createSpatialIndex(graphDatabaseService, "pharmacyLocation");
        for(int i=0;i<pharmacy_data.size();i++){
            List<String> list=pharmacy_data.get(i);
            String name=list.get(0);
            String latitude=list.get(1);
            String longitude=list.get(2);
            Long ph_id=Long.parseLong(list.get(4));
            Node pharmacyNode = graphDatabaseService.createNode();
            pharmacyNode.setProperty("wkt", String.format("POINT(%s %s)",longitude,latitude));
            pharmacyNode.setProperty("name", name);
            pharmacyNode.setProperty("id",ph_id);
            index.add(pharmacyNode, "location", "value");
        }
        transaction.success();
        transaction.close();
        graphDatabaseService.shutdown();
    }

    private static Index<Node> createSpatialIndex(GraphDatabaseService db, String indexName) {
        return db.index().forNodes(indexName, SpatialIndexProvider.SIMPLE_WKT_CONFIG);
    }

    public List<String> getNode(String latitude,String logitude,String distance){
        Transaction tx = this.graphDatabaseService.beginTx();
        Result result = this.graphDatabaseService.execute( "START pharmacy=node:pharmacyLocation('withinDistance:["+ latitude+","+logitude+","+distance +"]') "+"\n"+" RETURN pharmacy.name, pharmacy.wkt;" );
        String rows="";
        List<String> pharmacy_name=new ArrayList<String>();
        while ( result.hasNext() )
        {
            Map<String,Object> row = result.next();
            pharmacy_name.add((String)row.get("pharmacy.name"));

        }
        tx.success();
        tx.close();
        return pharmacy_name;
    }

}

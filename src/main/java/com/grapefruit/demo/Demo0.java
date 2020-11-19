package com.grapefruit.demo;

import com.datastax.driver.core.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class Demo0 {

    @RequestMapping("/")
    public String index(){
        return "Grapefruit";
    }

    public static void main(String[] args) {

        AuthProvider authProvider = new PlainTextAuthProvider("cassandra","cassandra");
        Cluster cluster = Cluster.builder().addContactPoint("47.115.42.52").withAuthProvider(authProvider).withPort(9042).build();
        //Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9041).withCredentials("cassandra","cassandra").build();

        //cluster.connect("system");

        Session session = cluster.connect();

        String cql = "select * from testKeyspace.testTable";
        Metadata metadata = cluster.getMetadata();
        for (Host h: metadata.getAllHosts()) {
            System.out.println("address:" + h.getAddress());
        }
        KeyspaceMetadata keyspace = metadata.getKeyspace("mySpace");
        for (KeyspaceMetadata k: metadata.getKeyspaces()) {
            System.out.println("name:" + k.getName());
        }
        ResultSet set = session.execute(cql);
        set.one();
        System.out.println(set);
        cluster.close();
    }
}

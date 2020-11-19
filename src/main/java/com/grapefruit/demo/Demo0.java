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

    //$ git config --global user.name "zzh-1992"
    //$ git config --global user.email "haiyan.xu.vip@gmail.com"

    //git config --global user.name "zzh-1992"
    //git config --global user.eamil "ezzh1992@icloud.com"

    // ssh-keygen -t rsa -C "ezzh1992@icloud.com"

    public static void main(String[] args) {

        AuthProvider authProvider = new PlainTextAuthProvider("cassandra","cassandra");
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withAuthProvider(authProvider).withPort(9042).build();
        //Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9041).withCredentials("cassandra","cassandra").build();

        cluster.connect("system");

        Session session = cluster.connect();

        String cql = "select * from table_name";
        //Metadata metadata = cluster.getMetadata();

//        for (Host h: metadata.getAllHosts()) {
//            System.out.println("address:" + h.getAddress());
//        }
//
//        KeyspaceMetadata keyspace = metadata.getKeyspace("mySpace");
//        for (KeyspaceMetadata k: metadata.getKeyspaces()) {
//            System.out.println("name:" + k.getName());
//        }


        ResultSet set = session.execute(cql);
        set.one();

        System.out.println(set);

        //System.out.println(execute);
        cluster.close();

    }


}

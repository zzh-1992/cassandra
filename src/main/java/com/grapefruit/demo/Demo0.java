package com.grapefruit.demo;

import com.datastax.driver.core.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class Demo0 {

    @RequestMapping("/")
    public String index(){
        return "Grapefruit";
    }

    public static void main(String[] args) {

        String localhost = "127.0.0.1";
        //String aliyunHost = "47.115.42.52";

        AuthProvider authProvider = new PlainTextAuthProvider("cassandra","cassandra");
        Cluster cluster = Cluster.builder().addContactPoint(localhost).withAuthProvider(authProvider).withPort(9041).build();
        //Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9041).withCredentials("cassandra","cassandra").build();

        //cluster.connect("system");

        Session session = cluster.connect();

        // 坑 ==》cassandra的keyspace在命名不能使用大写,不然会报错,找不到对应的空间
        String table = "testKeyspace.table_name";
        String t2 = "testKeyspace.testTable";

        String cql = "select * from " + table;
        Metadata metadata = cluster.getMetadata();
        for (Host h: metadata.getAllHosts()) {
            System.out.println("address:" + h.getAddress());
        }
        for (KeyspaceMetadata k: metadata.getKeyspaces()) {
            System.out.println("name:" + k.getName());
        }
        ResultSet set = session.execute(cql);
        ColumnDefinitions columnDefinitions = set.getColumnDefinitions();

        System.out.println("set" + set);
        System.out.println("columnDefinitions:" + columnDefinitions);

        System.out.println("size:" + columnDefinitions.size());
        List<ColumnDefinitions.Definition> columnList = columnDefinitions.asList();

        // 创建map集合存储字段的名字和类型
        Map<String,DataType> columnMap = new HashMap();
        columnList.forEach(definition -> {
                    String name = definition.getName();
                    DataType type = definition.getType();
                    columnMap.put(name,type);}
        );

        // 存储数据的集合
        List<Row> rowList = set.all();

        List<Map> list = new ArrayList<>();
        for (Row row: rowList) {
            Map map = new HashMap<>();
            for(int i = 0;i < columnList.size();i++){
                // 获取第一个列对象
                ColumnDefinitions.Definition definition = columnList.get(i);
                // 获取第一列的"列名"
                String columnName = definition.getName();
                // 获取第一列的数据"类型"
                DataType columnType = definition.getType();

                if(columnType.getName().equals(DataType.Name.VARCHAR)){
                    String value = row.getString(columnName);
                    map.put(columnName,value);
                }
                if(columnType.getName().equals(DataType.Name.INT)){
                    int value = row.getInt(columnName);
                    map.put(columnName,value);
                }
            }
            list.add(map);
        }
        System.out.println(list);

        cluster.close();
    }
}

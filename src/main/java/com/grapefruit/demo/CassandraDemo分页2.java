package com.grapefruit.demo;

import com.datastax.driver.core.*;
import com.grapefruit.reactive.Person;

import java.util.ArrayList;
import java.util.List;

public class CassandraDemo分页2 {
    static int fetchSize = 4;
  public static void main(String[] args) {
    String[] contactPoints = new String[]{
            "172.16.163.2",
            "172.16.163.3",
            "172.16.163.4"
    };
    Cluster cluster = Cluster.builder()
            .addContactPoints(contactPoints).withPort(9042)
            .withCredentials("cassandra","cassandra")
      .build();
    cluster.init();

    Session session = cluster.connect();
      // =========尝试分页查询=========
      String cql = "SELECT * FROM grapefruit.person;";
      Statement statement = new SimpleStatement(cql);
      statement.setFetchSize(fetchSize);
      List<Person> list = new ArrayList<>();

      executeStatement(session,statement,list);

      System.out.println("集合个数:" + list.size());

        session.close();
        cluster.close();
      System.out.println("=====================================");
  }

  // 网页链接:https://ahappyknockoutmouse.wordpress.com/2014/11/12/246/
  // 分页处理
  public static void executeStatement(Session session,Statement statement,List<Person> list){
      ResultSet rs = session.execute(statement);
      for (Row r : rs) {
          if (rs.getAvailableWithoutFetching() == fetchSize && !rs.isFullyFetched()){
              rs.fetchMoreResults();
          }
          rowToList(r, list);
      }
  }
  public static void rowToList(Row row, List<Person> list){
      if(row == null){
          return;
      }
      int id = row.getInt("id");
      String name = row.getString("name");
      int age = row.getInt("age");
      String address = row.getString("address");
      Person person = new Person(id,address,name,age);
      list.add(person);
  }
}
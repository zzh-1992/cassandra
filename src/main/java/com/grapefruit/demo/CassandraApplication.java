package com.grapefruit.demo;

import com.alibaba.fastjson.JSON;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint;
import com.grapefruit.reactive.Person;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultSessionFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhihuangzhang
 */
public class CassandraApplication {

  // springboot cassandra官网链接:https://docs.spring.io/spring-data/cassandra/docs/3.2.0/reference/html/#cassandra.getting-started
  public static void main(String[] args) {
    // 套接字地址
    InetSocketAddress c1 = new InetSocketAddress("172.16.163.2",9042);
    InetSocketAddress c2 = new InetSocketAddress("172.16.163.3",9042);
    InetSocketAddress c3 = new InetSocketAddress("172.16.163.4",9042);

    EndPoint e1 = new DefaultEndPoint(c1);
    EndPoint e2 = new DefaultEndPoint(c2);
    EndPoint e3 = new DefaultEndPoint(c3);

    // 创建集群地址集合
    List<EndPoint> endPointList = Arrays.asList(e1, e2, e3);

    // 暂时只能填写"datacenter1"
    String dc = "datacenter1";
    //dc = "DC1";
    CqlSession cqlSession = CqlSession.builder()
            .withLocalDatacenter(dc)
            .withKeyspace("grapefruit")
            .withAuthCredentials("cassandra","cassandra")

            // 单节点设置
            //.addContactPoint(c1)

            //  集群设置
            .addContactEndPoints(new ArrayList(endPointList))
            .build();

    SessionFactory sessionFactory = new DefaultSessionFactory(cqlSession);

    CqlTemplate cqlTemplate = new CqlTemplate(sessionFactory);

    List<Map<String, Object>> list = cqlTemplate.queryForList("SELECT * FROM person");

    List<Person> personList = new ArrayList<>();
    System.out.println("map:" + list);
    list.forEach(row->{
      String jsonString = JSON.toJSONString(row);
      Person person = JSON.parseObject(jsonString, Person.class);
      personList.add(person);
    });
    cqlSession.close();
    System.out.println(personList);
  }
}
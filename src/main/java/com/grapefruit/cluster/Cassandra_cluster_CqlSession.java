package com.grapefruit.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint;
import com.grapefruit.entity.Person;
import com.grapefruit.utils.CassandraUtils;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultSessionFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * cassandra-集群操作-cqlsh
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-07 19:35
 */
public class Cassandra_cluster_CqlSession {

    // springboot cassandra官网链接:https://docs.spring.io/spring-data/cassandra/docs/3.2.0/reference/html/#cassandra.getting-started
    public static void main(String[] args) {
        // 套接字地址
        InetSocketAddress c1 = new InetSocketAddress("172.16.163.2", 9042);
        InetSocketAddress c2 = new InetSocketAddress("172.16.163.3", 9042);
        InetSocketAddress c3 = new InetSocketAddress("172.16.163.4", 9042);

        EndPoint e1 = new DefaultEndPoint(c1);
        EndPoint e2 = new DefaultEndPoint(c2);
        EndPoint e3 = new DefaultEndPoint(c3);

        // 创建集群地址集合
        List<EndPoint> endPointList = Arrays.asList(e1, e2, e3);

        // 暂时只能填写"datacenter1"
        String dc = "datacenter1";
        CqlSession cqlSession = CqlSession.builder()
                .withLocalDatacenter(dc)
                .withKeyspace("grapefruit")
                .withAuthCredentials("cassandra", "cassandra")

                // 单节点设置
                //.addContactPoint(c1)

                //  集群设置
                .addContactEndPoints(Arrays.asList(e1, e2, e3))
                .build();

        SessionFactory sessionFactory = new DefaultSessionFactory(cqlSession);

        CqlTemplate cqlTemplate = new CqlTemplate(sessionFactory);

        // 执行查询
        List<Map<String, Object>> list = cqlTemplate.queryForList("SELECT * FROM person");

        // 关闭资源
        cqlSession.close();

        List<Person> personList = CassandraUtils.toJavaBean(list, Person.class);

        System.out.println("");
    }
}

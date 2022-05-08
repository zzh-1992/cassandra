/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.cluster.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.internal.core.metadata.DefaultEndPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * cassandra config
 * spring-cassandra官网文档 https://docs.spring.io/spring-data/cassandra/docs/current/reference/html/#cassandra.connections
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-08 12:13
 */
@Configuration
@EnableCassandraRepositories(basePackages = {"com.grapefruit.cluster"})
public class Config {

    @Bean
    public CqlSession cqlSession() {
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
        return CqlSession.builder()
                .withLocalDatacenter(dc)
                .withKeyspace("grapefruit")
                .withAuthCredentials("cassandra", "cassandra")

                // 单节点设置
                //.addContactPoint(c1)

                //  集群设置
                .addContactEndPoints(endPointList)

                .build();
    }
}

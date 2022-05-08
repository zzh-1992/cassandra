/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.cluster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * cassandra config
 * spring-cassandra官网文档 https://docs.spring.io/spring-data/cassandra/docs/current/reference/html/#cassandra.connections
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-08 12:13
 */
@Table(value = "person")
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Person {
    @PrimaryKey
    @Id
    private int id;
    private String address;
    private String name;
    private int age;
}

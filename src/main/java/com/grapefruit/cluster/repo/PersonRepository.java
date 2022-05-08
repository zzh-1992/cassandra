/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.cluster.repo;

import com.grapefruit.cluster.entity.Person;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * https://docs.spring.io/spring-data/cassandra/docs/current/reference/html/#repositories.query-methods
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-07 19:35
 */
public interface PersonRepository extends CrudRepository<Person, UUID> {

    /**
     * 动态投影
     * Dynamic Projections https://docs.spring.io/spring-data/cassandra/docs/current/reference/html/#projection.dynamic
     *
     * @param address address
     * @param type    type
     * @param <T>     T
     * @return List<T>
     */
    @AllowFiltering
    <T> List<T> findByAddress(String address, Class<T> type);

    /*@Query(value = "select * from person",allowFiltering = true)
    <T> List<T> findAll(Class<T> type);*/
}

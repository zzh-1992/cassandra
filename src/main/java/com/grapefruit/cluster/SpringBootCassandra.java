/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.cluster;

import com.google.common.collect.Lists;
import com.grapefruit.cluster.entity.NamesOnly;
import com.grapefruit.cluster.entity.Person;
import com.grapefruit.cluster.repo.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 主启动类
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-08 12:46
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringBootCassandra {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCassandra.class, args);
    }

    @Bean
    public CommandLineRunner clr(PersonRepository people) {
        return args -> {
            // 查询地址是HZ当实体
            List<NamesOnly> address = people.findByAddress("HZ", NamesOnly.class);
            NamesOnly namesOnly = address.get(0);
            System.out.println(namesOnly.getFullName());

            // 全查询
            Iterable<Person> all = people.findAll();
            Lists.newArrayList(all).forEach(person -> System.out.println(person.toString()));
        };
    }
}

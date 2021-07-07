package com.grapefruit.operation;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.util.UUID;

/**
 * 批量插入数据
 * @author zhihuangzhang
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2021-07-07 5:19 下午
 */
public class BatchInsert {
    public static void main(String[] args) {
        String[] contactPoints = new String[]{
                "47.115.42.52"
        };
        Cluster cluster = Cluster.builder()
                .addContactPoints(contactPoints).withPort(9042)
                .withCredentials("cassandra", "cassandra")
                .build();
        cluster.init();

        Session session = cluster.connect();

        // =========批量插入数据=========
        BatchStatement batch = new BatchStatement();

        PreparedStatement ps = session
                .prepare("insert into grapefruit.grape(id, address, age,name) values(?,?,?,?)");
        for (int i = 0; i < 10; i++) {
            Grape grape = new Grape();
            grape.setId(i);
            grape.setAddress("test" + i);
            grape.setAge(i * 10 + 2);
            grape.setName(UUID.randomUUID().toString().replace("_", ""));

            BoundStatement bs = ps.bind(grape.getId(), grape.getAddress(), grape.getAge(), grape.getName());
            batch.add(bs);
        }
        // 执行查询
        session.execute(batch);

        batch.clear();

        session.close();
        cluster.close();
        System.out.println("==================end==================");
    }
}
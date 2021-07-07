package com.grapefruit.operation;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.grapefruit.reactive.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * 全查询
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2021-07-07 5:19 下午
 */
public class SelectAll {
    static int fetchSize = 4;

    public static void main(String[] args) {
        String[] contactPoints = new String[]{
                "172.16.163.2",
                "172.16.163.3",
                "172.16.163.4"
        };
        Cluster cluster = Cluster.builder()
                .addContactPoints(contactPoints).withPort(9042)
                .withCredentials("cassandra", "cassandra")
                .build();
        cluster.init();

        Session session = cluster.connect();
        // =========尝试全查询=========
        String cql = "SELECT * FROM grapefruit.person;";
        Statement statement = new SimpleStatement(cql);
        statement.setFetchSize(fetchSize);
        List<Person> list = new ArrayList<>();

        executeStatement(session, statement, list);

        System.out.println("集合个数:" + list.size());

        session.close();
        cluster.close();
        System.out.println("=====================================");
    }

    // 网页链接:https://ahappyknockoutmouse.wordpress.com/2014/11/12/246/
    // 全查询
    public static void executeStatement(Session session, Statement statement, List<Person> list) {
        ResultSet rs = session.execute(statement);
        for (Row r : rs) {
            if (rs.getAvailableWithoutFetching() == fetchSize && !rs.isFullyFetched()) {
                rs.fetchMoreResults();
            }
            rowToList(r, list);
        }
    }

    public static void rowToList(Row row, List<Person> list) {
        if (row == null) {
            return;
        }
        int id = row.getInt("id");
        String name = row.getString("name");
        int age = row.getInt("age");
        String address = row.getString("address");
        Person person = new Person(id, address, name, age);
        list.add(person);
    }
}
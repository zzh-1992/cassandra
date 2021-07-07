package com.grapefruit.operation;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 分页查询
 *
 * @author zhihuangzhang
 */
public class Paging {
    /**
     * 定义每次拉取的条数
     */
    static int fetchSize = 5;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] contactPoints = new String[]{
                "47.115.42.52"
        };
        Cluster cluster = Cluster.builder()
                .addContactPoints(contactPoints).withPort(9042)
                .withCredentials("cassandra", "cassandra")
                .withQueryOptions(new QueryOptions().setFetchSize(fetchSize))
                .build();
        cluster.init();
        Session session = cluster.connect();

        List<Grape> list = new ArrayList<>();
        // 定义查询语句
        String cql = "SELECT * FROM grapefruit.grape;";
        // =========尝试分页查询=========
        int limit = 0;
        String p0 = paging(session, null, cql, list, limit);

        String p1 = paging(session, p0, cql, list, limit);

        String p2 = paging(session, p1, cql, list, limit);

        session.close();
        cluster.close();
        System.out.println("=====================================");
    }

    /**
     * 分页查询
     *
     * @param session   session
     * @param pageState 上一次的分页末尾状态
     * @param cql       查询语句
     * @param list      拼装的结果集
     * @param limit     查询条数(若limit不传的话使用默认值)
     * @return pageState
     */
    public static String paging(Session session, String pageState, String cql, List<Grape> list, int limit) {
        Statement statement = new SimpleStatement(cql);
        statement.setFetchSize(limit == 0 ? fetchSize : limit);
        if (pageState != null) {
            statement.setPagingState(PagingState.fromString(pageState));
        }
        ResultSet rs = session.execute(statement);
        int remaining = rs.getAvailableWithoutFetching();
        System.out.println("remaining " + remaining);
        for (Row row : rs) {
            System.out.println("first" + row);
            // 把row转换成java对象并存入集合
            rowToList(row, list, Grape.class);

            if (--remaining == 0) {
                break;
            }
        }
        // 这个地方的pagingState需要和方法参数中的"pageState"做区别,"pageState"上一次的,pagingState是这次查询后的
        PagingState pagingState = rs.getExecutionInfo().getPagingState();
        return pagingState != null ? pagingState.toString() : "";
    }

    // 手写转换
    public static <T> void rowToList(Row row, List<T> list, Class<T> tClass) {
        // 获取列的定义
        List<ColumnDefinitions.Definition> definitions = row.getColumnDefinitions().asList();

        Map<String, Object> map = new HashMap<>();
        for (ColumnDefinitions.Definition definition : definitions) {
            // 获取该列的名字
            String name = definition.getName();
            // 获取该列的类型
            DataType type = definition.getType();
            if (type.getName().isCompatibleWith(DataType.Name.INT)) {
                int i = row.getInt(name);
                // 判断类型并获取值
                map.put(name, i);
                continue;
            }
            if (type.getName().isCompatibleWith(DataType.Name.VARCHAR)) {
                String s = row.getString(name);
                map.put(name, s);
            }
        }
        T t = JSON.parseObject(JSON.toJSONString(map), tClass);
        
        /*int id = row.getInt("id");
        String name = row.getString("name");
        int age = row.getInt("age");
        String address = row.getString("address");
        Grape grape = new Grape(id, address, age, name);*/
        list.add(t);
    }
}
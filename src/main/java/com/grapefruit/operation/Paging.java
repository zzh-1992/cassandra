package com.grapefruit.operation;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.grapefruit.utils.CassandraUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 分页查询
 * <p>
 * 参考链接
 * https://docs.datastax.com/en/developer/java-driver/3.6/manual/paging/
 * https://stackoverflow.com/questions/26757287/results-pagination-in-cassandra-cql
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2021-07-07 5:19 下午
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
            CassandraUtils.rowToList(row, list, Grape.class);

            if (--remaining == 0) {
                break;
            }
        }
        // 这个地方的pagingState需要和方法参数中的"pageState"做区别,"pageState"上一次的,pagingState是这次查询后的
        PagingState pagingState = rs.getExecutionInfo().getPagingState();
        return pagingState != null ? pagingState.toString() : "";
    }
}
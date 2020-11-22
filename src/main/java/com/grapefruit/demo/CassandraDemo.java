package com.grapefruit.demo;

import com.datastax.driver.core.*;

public class CassandraDemo {
  public static void main(String[] args) {
    // 此处填写数据库连接点地址（公网或者内网的），控制台有几个就填几个。
    // 实际上SDK最终只会连上第一个可连接的连接点并建立控制连接，填写多个是为了防止单个节点挂掉导致无法连接数据库。
    // 此处无需关心连接点的顺序，因为SDK内部会先打乱连接点顺序避免不同客户端的控制连接总是连一个点。
    // 千万不要把公网和内网的地址一起填入。
      int port = 9041;
    String[] contactPoints = new String[]{
      //"cds-xxxxxxxx-core-003.cassandra.rds.aliyuncs.com",
      //"cds-xxxxxxxx-core-002.cassandra.rds.aliyuncs.com"
            "127.0.0.1",
            "47.115.42.52"
    };
    String localhost = "127.0.0.1";
    //String aliyunHost = "47.115.42.52";
    Cluster cluster = Cluster.builder()
      .addContactPoints(localhost).withPort(port)
      // 填写账户名密码（如果忘记可以在 帐号管理 处重置）
      .withAuthProvider(new PlainTextAuthProvider("cassandra", "cassandra"))
      // 如果进行的是公网访问，需要在帐号名后面带上 @public 以切换至完全的公网链路。
      // 否则无法在公网连上所有内部节点，会看到异常或者卡顿，影响本地开发调试。
      // 后续会支持网络链路自动识别（即无需手动添加@public）具体可以关注官网Changelog。
      //.withAuthProvider(new PlainTextAuthProvider("cassandra@public", "123456"))
      .build();
    // 初始化集群，此时会建立控制连接（这步可忽略，建立Session时候会自动调用）
    cluster.init();
    // 连接集群，会对每个Cassandra节点建立长连接池。
    // 所以这个操作非常重，不能每个请求创建一个Session。合理的应该是每个进程预先创建若干个。
    // 通常来说一个够用了，你也可以根据自己业务测试情况适当调整，比如把读写的Session分开管理等。
    Session session = cluster.connect();
    // 创建keyspace，指定对应strategy， replication factor。
    session.execute(
                "CREATE KEYSPACE IF NOT EXISTS testKeyspace WITH replication "
                        + "= {'class':'SimpleStrategy', 'replication_factor':1};");
    // 创建table，给table指定对应的primary key 以及cluster key 和regular key
    session.execute(
                "CREATE TABLE IF NOT EXISTS testKeyspace.testTable ("
                        + "id int PRIMARY KEY,"
                        + "name text,"
                        + "age int,"
                        + "address text"
                        + ");");    
    // 执行insert 操作
     session.execute(
                "INSERT INTO testKeyspace.testTable (id, name, age, address) "
                        + "VALUES ("
                        + "1,"
                        + "'testname',"
                        + "11,"
                        + "'hangzhou');");
    // 执行select 操作，这里select * 表示获取所有列，也可以指定需要select 的列名获取对应列数据
    ResultSet res = session.execute(
                "SELECT * FROM testKeyspace.testTable ;");
    // 如果想要获取每一列对应的数据，可以如下操作
    for (Row row : res)
    {
        int id = row.getInt("id");
        String name = row.getString("name");
        int age = row.getInt("age");
        String address = row.getString("address");
        System.out.println("name:" + name + " age:" + age + " address:" + address);
    }
    // ResultSet 实现了 Iterable 接口，我们直接将每行信息打印到控制台
    res.forEach(System.out::println);
    // 关闭Session
    session.close();
    // 关闭Cluster
    cluster.close();
      System.out.println("=====================================");
  }
}
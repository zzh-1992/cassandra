##Cassandra config
### 方式1 在application.properties添加cassandra配置
```properties
spring.data.cassandra.schema-action=CREATE_IF_NOT_EXISTS
spring.data.cassandra.request.timeout=10s
spring.data.cassandra.connection.connect-timeout=10s
spring.data.cassandra.connection.init-query-timeout=10s

#cassandra 配置
spring.data.cassandra.session-name=MYDC

spring.data.cassandra.local-datacenter=datacenter1
spring.data.cassandra.contact-points=172.16.163.2,172.16.163.3,172.16.163.4
spring.data.cassandra.port=9042
spring.data.cassandra.username=cassandra
spring.data.cassandra.password=cassandra
spring.data.cassandra.keyspace-name=grapefruit
```
### 方式2 往容器注入CqlSession
```java
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
```

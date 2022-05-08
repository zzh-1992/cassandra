

云数据库 Cassandra > 快速入门 > 多语言SDK访问（公网&内网）

https://help.aliyun.com/document_detail/134892.html?spm=a2c4g.11186623.6.558.2e722ece8Ftsuj

###切到安装目录
```shell
cd /usr/local/apache-cassandra-3.11.9
```

###启动cassandra
```shell
bin/cassandra -R
```

###登陆客户端
```shell
bin/cqlsh -ucassandra -pcassandra 172.16.163.2

cqlsh> use grapefruit;

cqlsh:grape> select * from person;

 id | address | name
----+---------+------
  1 |      SZ |  GGG
  2 |      MZ |  ggg
```

### 关闭cassandra
```shell
pgrep -u root -f cassandra | xargs kill -9
```

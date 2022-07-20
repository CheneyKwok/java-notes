# Docker 部署中间件

## Mysql

- 启动 Mysql 服务

```java
docker run -p 3306:3306 --name mysql --restart=always \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7
```

- 配置 Mysql 编码

vi /mydata/mysql/conf/my.cnf

```java
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=uf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve
```

- 重启 Nysql 服务

## Elasticsearch

- 修改虚拟内存区域大小，否则会因为过小而无法启动:

```java
sysctl -w vm.max_map_count=262144
```

启动Elasticsearch服务：

```java
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch --restart=always \
-e "discovery.type=single-node" \
-e "cluster.name=elasticsearch" \
-e ES_JAVA_OPTS="-Xms128m -Xmx256m" \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-d elasticsearch:7.6.2
```

- 启动时会发现/usr/share/elasticsearch/data目录没有访问权限，只需要修改/mydata/elasticsearch/data目录的权限

```java
chmod 777 /mydata/elasticsearch/data/
```

- 安装中文分词器IKAnalyzer

`https://github.com/medcl/elasticsearch-analysis-ik`

从这里下载预构建包：https ://github.com/medcl/elasticsearch-analysis-ik/releases

创建插件文件夹 cd your-es-root/plugins/ && mkdir ik

解压插件到文件夹 your-es-root/plugins/ik

## Kibana

```java
docker run --name kibana -p 5601:5601 --restart=always \
-e "elasticsearch.hosts=http://192.168.56.10:9200" \
-d kibana:7.6.2
```

## Nacos

- 首先以不挂载的方式启动一个 nacos 容器，目的是拷贝挂载文件

```java
docker run --name nacos \
-e MODE=standalone \
-e JVM_XMS=128m \
-e JVM_XMX=128m \
-p 8848:8848 -d nacos/nacos-server:1.3.2
```

- 拷贝挂载文件

```java

mkdir -p /mydata/nacos/init.d

docker cp nacos:/home/nacos/init.d/custom.properties /mydata/nacos/init.d

docker stop nacos

docker rm nacos

```

- 重新启动 nacos

```java
docker run --name nacos --restart=always \
-e MODE=standalone \
-e JVM_XMS=128m \
-e JVM_XMX=128m \
-v /mydata/nacos/logs:/home/nacos/logs \
-v /mydata/nacos/init.d/custom.properties:/home/nacos/init.d/custom.properties \
-p 8848:8848 -d nacos/nacos-server:1.3.2
```

## RabbitMQ

- 部署启动

```java
docker run -p 5672:5672 -p 15672:15672 --name rabbitmq \
-d rabbitmq:3.9-management
```

- 访问地址查看是否安装成功：http://192.168.56.10:15672 默认账号：guest guest

## Seata

- 首先启动一个用于将 resources 目录文件拷出的临时容器

```java
docker run --name seata-server -p 8091:8091 -p 7091:7091 -d seataio/seata-server:1.3.0
```

- 拷贝 resources 目录

```java
mkdir -p /mydata/seata

docker cp seata-server:/seata-server/resources /mydata/seata

docker stop seata-server

docker rm seata-server
```

- 重新部署启动并挂载配置文件

```java
docker run --name seata-server --restart=always \
        -p 8091:8091 \
        -p 7091:7091 \
        -v /mydata/seata/resources:/seata-server/resources  \
        -d seataio/seata-server:1.3.0
```

## Zipkin

```java
docker run --name zipkin -p 9411:9411 -d openzipkin/zipkin
```

# Docker 部署中间件

## Elasticsearch

- 修改虚拟内存区域大小，否则会因为过小而无法启动:

```java
sysctl -w vm.max_map_count=262144
```

启动Elasticsearch服务：

```java
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \
-e "discovery.type=single-node" \
-e "cluster.name=elasticsearch" \
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
docker run --name kibana -p 5601:5601 \
-e "elasticsearch.hosts=http://192.168.56.10:9200" \
-d kibana:7.6.2
```

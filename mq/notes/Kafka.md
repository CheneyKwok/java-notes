# Kafka

## 安装与配置

### ZooKeeper 安装脚本

```shell
cd /opt

curl https://dlcdn.apache.org/zookeeper/zookeeper-3.8.0/apache-zookeeper-3.8.0-bin.tar.gz -O

tar zxvf apache-zookeeper-3.8.0-bin.tar.gz

mv apache-zookeeper-3.8.0-bin apache-zookeeper-3.8.0

cat >> /etc/profile <<EOF
export ZOO_HOME=/opt/apache-zookeeper-3.8.0
export PATH=$PATH:$ZOO_HOME/bin
EOF

source /etc/profile

mkdir -p /usr/local/share/applications/zookeeper/data

cat > /usr/local/share/applications/zookeeper/data/myid <<EOF
1
EOF

cd $ZOO_HOME/conf/
cp zoo_sample.cfg zoo.cfg
sed -i "s/dataDir=\/tmp\/zookeeper/dataDir=\/usr\/local\/share\/applications\/zookeeper\/data/g" zoo.cfg
cat >> zoo.cfg <<EOF
server.1=kafka1:2888:3888
server.2=kafka2:2888:3888
server.3=kafka3:2888:3888
EOF

cd ..
bin/zkServer.sh start
sleep 2s
bin/zkServer.sh status

```

### Broker 安装脚本

```shell
cd /opt

curl https://downloads.apache.org/kafka/3.4.0/kafka_2.12-3.4.0.tgz -O

tar zxvf kafka_2.12-3.4.0.tgz

cat >> /etc/profile <<EOF
export KAFKA_HOME=/opt/kafka_2.12-3.4.0
export PATH=$PATH:$KAFKA_HOME/bin
EOF

source /etc/profile

cd $KAFKA_HOME/config

sed -i 's/broker.id=0/broker.id=0/g' server.properties
sed -i 's/#listeners=PLAINTEXT:\/\/:9092/listeners=PLAINTEXT:\/\/:9092/g' server.properties
sed -i 's/#advertised.listeners=PLAINTEXT:\/\/your.host.name:9092/advertised.listeners=PLAINTEXT:\/\/your.host.name:9092/g' server.properties
sed -i 's/zookeeper.connect=localhost:2181/zookeeper.connect=kafka1:2181,kafka2:2181,kafka3:2181\/kafka/g' server.properties

../bin/kafka-server-start.sh server.properties &

```

### Broker 参数配置

- broker.id
  
  kafka 集群中 broker 的唯一标识

- listeners
  
  指明 kafka 当前节点监听本机的哪个网卡

- advertised.listeners
  
  指明客户端通过哪个 ip 可以访问到当前节点

- log.dir
  
  kafka 日志目录

- zookeeper.connect
  
  该参数指明 broker 要连接的 ZooKeeper 集群的服务地址（包含端口号）
  
  一般为 kafka1:2181,kafka2:2181,kafka3:2181/kafka，如果不指定 chroot， 那么默认使用 ZooKeeper 的根路径

## 生产者（Producer）


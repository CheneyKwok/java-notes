package github.cheneykwok.analysis.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义分区器
 *
 * @author gzc
 * @date 2023-05-26
 */
public class DemoPartitioner implements Partitioner {

    private final AtomicInteger counter = new AtomicInteger(1);
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int size = partitionInfos.size();
        if (null == keyBytes) {
            return counter.getAndIncrement() % size;
        } else {
            return Utils.toPositive(Utils.murmur2(keyBytes)) % size;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

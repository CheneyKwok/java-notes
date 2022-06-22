package util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;


@Slf4j
public class ElasticSearchFactory {

    public static RestHighLevelClient client() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"));
        return new RestHighLevelClient(builder);
    }

    public static void main(String[] args) {
        log.info(client().toString());
    }
}

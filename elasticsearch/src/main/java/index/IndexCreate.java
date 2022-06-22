package index;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import util.ElasticSearchFactory;

import java.io.IOException;

@Slf4j
public class IndexCreate {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = ElasticSearchFactory.client();
        CreateIndexRequest request = new CreateIndexRequest("user");
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged();
        log.info("创建索引 {}", acknowledged);
        client.close();
    }
}

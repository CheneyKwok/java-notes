package index;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import util.ElasticSearchFactory;

import java.io.IOException;

@Slf4j
public class IndexSearch {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = ElasticSearchFactory.client();
        GetIndexRequest request = new GetIndexRequest("user");
        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
        log.info("{}", response.getAliases());
        log.info("{}", response.getMappings());
        log.info("{}", response.getSettings());
        client.close();
    }
}

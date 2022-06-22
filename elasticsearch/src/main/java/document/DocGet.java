package document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import util.ElasticSearchFactory;

@Slf4j
public class DocGet {

    public static void main(String[] args) throws Exception {
        GetRequest request = new GetRequest();
        request.index("user").id("1");
        RestHighLevelClient client = ElasticSearchFactory.client();
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        log.info("index {}", response.getIndex());
        log.info("id {}", response.getId());
        log.info("source {}", response.getSourceAsString());
        client.close();
    }
}

package document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import util.ElasticSearchFactory;

@Slf4j
public class DocDelete {

    public static void main(String[] args) throws Exception {
        DeleteRequest request = new DeleteRequest();
        request.index("user").id("1");
        RestHighLevelClient client = ElasticSearchFactory.client();
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        log.info("index {}", response.getIndex());
        log.info("id {}", response.getId());
        log.info("result {}", response.getResult());
        client.close();
    }
}

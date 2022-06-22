package document;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.ElasticSearchFactory;

@Slf4j
public class DocUpdate {

    public static void main(String[] args) throws Exception {
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("1");
        request.doc(XContentType.JSON, "sex", "女");
        RestHighLevelClient client = ElasticSearchFactory.client();
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        log.info("index {}", response.getIndex());
        log.info("id {}", response.getId());
        log.info("result {}", response.getResult());
        client.close();
    }
}

package index;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import util.ElasticSearchFactory;

import java.io.IOException;

@Slf4j
public class IndexDelete {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = ElasticSearchFactory.client();
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        log.info("删除索引 {}", response.isAcknowledged());
        client.close();
    }
}

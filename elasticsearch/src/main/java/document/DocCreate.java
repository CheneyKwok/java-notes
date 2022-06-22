package document;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.ElasticSearchFactory;

@Slf4j
public class DocCreate {

    public static void main(String[] args) throws Exception {
        IndexRequest indexRequest = new IndexRequest("user").id("1");

        User user = User.builder()
                .name("张三")
                .age(20)
                .sex("男")
                .build();
        // 向 ES 插入数据，必须将数据转换为 json 格式
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        indexRequest.source(userJson, XContentType.JSON);
        RestHighLevelClient client = ElasticSearchFactory.client();
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        log.info("index {}", indexResponse.getIndex());
        log.info("id {}", indexResponse.getId());
        log.info("result {}", indexResponse.getResult());
        client.close();

    }
}

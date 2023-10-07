package com.relengxing.rebase.es.util;

import com.relengxing.rebase.es.domain.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RestESClientUtils <T,R> {

    @Autowired
    RestHighLevelClient esClient;

    /**
     * 查看索引是否存在
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return esClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建索引
     *
     * @param indexName
     * @param numberOfShards
     * @param numberOfReplicas
     * @throws IOException
     */
    public void createIndex(String indexName, int numberOfShards, int numberOfReplicas) throws IOException {
        if (!existIndex(indexName)) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            // settings部分
            request.settings(Settings.builder()
                    // 创建索引时，分配的主分片的数量
                    .put("index.number_of_shards", numberOfReplicas)
                    // 创建索引时，为每一个主分片分配的副本分片的数量
                    .put("index.number_of_replicas", numberOfReplicas)
            );
            // mapping部分 除了用json字符串来定义外，还可以使用Map或者XContentBuilder
            request.mapping("{\n" +
                    "  \"properties\": {\n" +
                    "    \"message\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}", XContentType.JSON);
            // 创建索引(同步的方式)
            // CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);

            // 创建索引(异步的方式)
            esClient.indices().createAsync(request, RequestOptions.DEFAULT, new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                    log.debug("执行情况:" + createIndexResponse);
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("执行失败的原因:" + e.getMessage());
                }
            });
        }
    }

    /**
     * 更新索引的settings配置
     *
     * @param indexName
     * @throws IOException
     */
    public void updateIndexSettings(String indexName) throws IOException {
        UpdateSettingsRequest request = new UpdateSettingsRequest(indexName);
        String settingKey = "index.number_of_replicas";
        int settingValue = 2;
        Settings.Builder settingsBuilder = Settings.builder().put(settingKey, settingValue);
        request.settings(settingsBuilder);
        // 是否更新已经存在的settings配置 默认false
        request.setPreserveExisting(true);

        // 更新settings配置(同步)
        //esClient.indices().putSettings(request, RequestOptions.DEFAULT);

        // 更新settings配置(异步)
        esClient.indices().putSettingsAsync(request, RequestOptions.DEFAULT, new ActionListener<AcknowledgedResponse>() {
            @Override
            public void onResponse(AcknowledgedResponse acknowledgedResponse) {
                log.debug("执行情况:" + acknowledgedResponse);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("执行失败的原因:" + e.getMessage());
            }
        });
    }

    /**
     * 更新索引的mapping配置
     *
     * @param indexName
     * @throws IOException
     */
    public void putIndexMapping(String indexName) throws IOException {
        PutMappingRequest request = new PutMappingRequest(indexName);
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("new_parameter");
                {
                    builder.field("type", "text");
                    builder.field("analyzer", "ik_max_word");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.source(builder);

        // 新增mapping配置(同步)
        //AcknowledgedResponse putMappingResponse = esClient.indices().putMapping(request, RequestOptions.DEFAULT);
        // 新增mapping配置(异步)
        esClient.indices().putMappingAsync(request, RequestOptions.DEFAULT, new ActionListener<AcknowledgedResponse>() {
            @Override
            public void onResponse(AcknowledgedResponse acknowledgedResponse) {
                log.debug("执行情况:" + acknowledgedResponse);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("执行失败的原因:" + e.getMessage());
            }
        });
    }

    /**
     * 新增Document   使用json字符串
     *
     * @param indexName
     * @throws IOException
     */
    public void addDocument1(String indexName) throws IOException {
        IndexRequest request = new IndexRequest(indexName);
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2020-03-28\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        request.routing("routing");

        esClient.index(request, RequestOptions.DEFAULT);
    }

    //使用map
    public void addDocument2(String indexName) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest(indexName).id("1").source(jsonMap);

        indexRequest.routing("routing");

        esClient.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                log.debug("执行情况: " + indexResponse);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("执行失败的原因");
            }
        });
    }

    /**
     * 根据枚举的维度来编写代码
     *
     * @param queryParam
     * @throws IOException
     */
    public List<Map<String, Object>> searchDocument(QueryParam queryParam) throws IOException {
        SearchRequest searchRequest = new SearchRequest(queryParam.getIndexName());
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();

        for(Map.Entry<String, String> entry : queryParam.getQueryCondition().entrySet()){
            booleanQueryBuilder.filter(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(booleanQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            result.add(hit.getSourceAsMap());
        }
        return result;
    }


    /**
     * SearchRequest 搜索请求
     * SearchSourceBuilder 条件构造
     * HighlightBuilder 构建高亮
     * TermQueryBuilder 精确查询
     * MatchAllQueryBuilder
     * 其他 build xxx QueryBuilder ，可以参考官方文档
     *
     * @throws IOException
     */
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("ak_mail_info-2022.05.28");
        //构建搜索的条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件，我们可以使用QueryBuilders工具类来实现
        //QueryBuilders.termQuery 精确
        //QueryBuilders.matchAllQuery() 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("business", "mail");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.from(1);
        sourceBuilder.size(2);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(1);
        }
//        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsMap());
        }
    }

}

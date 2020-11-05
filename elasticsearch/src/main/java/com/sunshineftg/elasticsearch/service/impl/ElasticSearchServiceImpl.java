package com.sunshineftg.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunshineftg.elasticsearch.entity.Article;
import com.sunshineftg.elasticsearch.entity.Keyword;
import com.sunshineftg.elasticsearch.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: zhangtietuo
 * @Description:
 * @Date: 2020/10/29 15:44
 */
@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Resource
    RestHighLevelClient restHighLevelClient;

    @Override
    public String createIndex(String index) throws IOException {
        //创建索引请求
        CreateIndexRequest index1 = new CreateIndexRequest(index); //hcode_index为索引名
        //执行请求，获得响应
        CreateIndexResponse response = restHighLevelClient.indices().create(index1, RequestOptions.DEFAULT);
        log.info("返回结果:{}", response.toString());
        return response.toString();
    }

    @Override
    public String createIndexArticle() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(Article.INDEX_NAME);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1)
        );

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
                builder.startObject("title");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_smart");
                }
                builder.endObject();
                builder.startObject("tags");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();
                builder.startObject("desc");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_smart");
                }
                builder.endObject();
                builder.startObject("content");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_smart");
                }
                builder.endObject();
                builder.startObject("fileId");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();
                builder.startObject("num");
                {
                    builder.field("type","long");
                }
                builder.endObject();
                builder.startObject("createDate");
                {
                    builder.field("type","date");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        return createIndexResponse.index();
    }

    @Override
    public String createIndexKeyword() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(Keyword.INDEX_NAME);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1)
        );

        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("type");
                {
                    builder.field("type","short");
                }
                builder.endObject();
                builder.startObject("text");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_max_word");
                }
                builder.endObject();
                builder.startObject("termText");
                {
                    builder.field("type","keyword");
                }
                builder.endObject();
                builder.startObject("num");
                {
                    builder.field("type","long");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        return createIndexResponse.index();
    }

    @Override
    public Boolean existIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        log.info("是否存在:{}", exists);
        return exists;
    }

    @Override
    public String deleteIndex(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        log.info("是否删除成功:{}", delete.isAcknowledged());
        return delete.toString();
    }

    @Override
    public String addDocument(Article article) throws IOException {
        //创建索引请求
        IndexRequest request = new IndexRequest(Article.INDEX_NAME);
        //设置规则 例如相当于 PUT /hcode_index/_doc/1 命令
        request.id(article.getId()).timeout(TimeValue.timeValueSeconds(1));//设置id，1秒超时
        request.timeout("90s");
        // 数据转换成json 放入请求
        request.source(JSON.toJSONString(article), XContentType.JSON);
        //将请求发出去,获取响应结果
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("返回当前操作的类型:{},内容:{}", indexResponse.status(), indexResponse.status());
        return indexResponse.toString();

    }

    @Override
    public Boolean isExists(Article article) throws IOException {
        GetRequest getRequest = new GetRequest(Article.INDEX_NAME, article.getId());
        // 不获取返回的_source 的上下文，会提高速度
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        log.info("是否存在:{}", exists);
        return exists;
    }

    @Override
    public String getDocument(Article article) throws IOException {
        GetRequest getRequest = new GetRequest(Article.INDEX_NAME, article.getId());
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        log.info("获取文档的内容:{}", response.getSourceAsString());
        return response.toString();
    }

    @Override
    public String updateDocument(Article article) throws IOException {
        UpdateRequest request = new UpdateRequest(Article.INDEX_NAME, article.getId());
        request.timeout("100s");
        request.doc(JSON.toJSONString(article), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        log.info("返回状态码(成功返回 OK):{},内容:{}", response.status(), response.toString());
        return response.toString();
    }

    @Override
    public String deleteDocument(Article article) throws IOException {
        DeleteRequest request = new DeleteRequest(Article.INDEX_NAME, article.getId());
        request.timeout("1s");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        log.info("返回状态码(成功返回 OK):{},内容:{}", response.status(), response.toString());
        return response.toString();
    }

    @Override
    public String bulkRequest(List<Article> list) throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        //批量请求，批量更新，删除都差不多！！！不设置id就会自动生成随机id，演示为批量插入
        for (int i = 0; i < list.size(); i++) {
            request.add(new IndexRequest(Article.INDEX_NAME)
                    .id(list.get(i).getId())
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        log.info("是否失败(false表示成功，true表示失败):{}", response.hasFailures());
        return response.toString();
    }

    @Override
    public SearchResponse query(Article article) throws IOException {
        SearchRequest request = new SearchRequest();
        //创建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 使用QueryBuilders工具，精确查询term
        //QueryBuilders.matchAllQuery() 匹配所有
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(article.getTitle())) {
            queryBuilder.should(QueryBuilders.matchQuery("title", article.getTitle()));
        }
        if(StringUtils.isNotBlank(article.getDesc())) {
            queryBuilder.should(QueryBuilders.matchQuery("desc", article.getDesc()));
        }
        if(StringUtils.isNotBlank(article.getTag())) {
            queryBuilder.should(QueryBuilders.matchQuery("tags", article.getTag()));
        }
        //配置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title"); //绑定属性
        highlightBuilder.field("desc"); //绑定属性
        highlightBuilder.field("tags"); //绑定属性
        highlightBuilder.requireFieldMatch(true); //关闭多个高亮，只显示一个高亮
        highlightBuilder.preTags("<p style='color:red'>"); //设置前缀
        highlightBuilder.postTags("</p>"); //设置后缀
        sourceBuilder.highlighter(highlightBuilder);
        //分页
        sourceBuilder.from((article.getPage()-1)*article.getSize());
        sourceBuilder.size(article.getSize());
        log.info("检索语句为:{}", queryBuilder.toString());
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //获取结果对象
        SearchHits hits = response.getHits();

        log.info(JSON.toJSONString(hits));

        for (SearchHit searchHit : hits) {
            //获取高亮的html
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            highlightFields.forEach((key, value) -> {
                if(!StringUtils.equals("tags", key)) {
                    HighlightField name = highlightFields.get(key);
                    //替换原有的字段
                    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                    if (name != null) {
                        Text[] fragments = name.fragments();
                        String light_name = "";
                        for (Text fragment : fragments) {
                            light_name += fragment;
                        }
                        sourceAsMap.put(key, light_name); //进行替换
                    }
                }

            });
            log.info(searchHit.getSourceAsMap().toString());
        }
        return response;
    }

    @Override
    public List<String> queryKeyword(Keyword keyword) throws IOException {
        List<String> keys = new ArrayList<>();
        SearchRequest request = new SearchRequest();
        //创建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 使用QueryBuilders工具，精确查询term
        //QueryBuilders.matchAllQuery() 匹配所有
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(keyword.getText())) {
            queryBuilder.should(QueryBuilders.matchQuery("text", keyword.getText()));
        }
        log.info("检索语句为:{}", queryBuilder.toString());
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //获取结果对象
        SearchHits hits = response.getHits();

        log.info(JSON.toJSONString(hits));

        for (SearchHit searchHit : hits) {
            //获取高亮的html
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            highlightFields.forEach((key, value) -> {

            });
            keys.add(searchHit.getSourceAsMap().get("text").toString());
            log.info(searchHit.getSourceAsMap().toString());
        }
        return keys;
    }

    public Set<String> analyze(String text) throws IOException {
        // 调用 IK 分词分词
//        JestClient client = new JestClient();
//        Analyze ikAnalyze = new Analyze.Builder()
//                .index(TEST_INDEX)
//                .analyzer(IK_TYPE)
//                .text(searchContent)
//                .build();
//
//        JestResult result = null;
        Set<String> keySet = new HashSet<String>();
//        try {
//            result = client.execute(ikAnalyze);
//            JsonArray jsonArray = result.getJsonObject().getAsJsonArray("tokens");
//            int arraySize = jsonArray.size();
//            for (int i = 0; i < arraySize; ++i) {
//                JsonElement curKeyword = jsonArray.get(i).getAsJsonObject().get("token");
//                //Logger.info("rst = " + curKeyword.getAsString());
//                keySet.add(curKeyword.getAsString());
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        return keySet;



    }

}

package com.github.dreamroute.me.server.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchApiTest {

    @Autowired
    private RestHighLevelClient client;

    private void printHits(SearchResponse resp) {
        SearchHit[] hitArr = resp.getHits().getHits();
        if (hitArr != null && hitArr.length > 0) {
            for (SearchHit hit : hitArr) {
                String data = hit.getSourceAsString();
                System.err.println(data);
            }
        }
    }

    @Test
    public void matchAllTest() throws Exception {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        SearchRequest request = new SearchRequest("user1");
        request.source(builder);
        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = resp.getHits();
        System.err.println(hits);
    }

    @Test
    public void termTest() throws Exception {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("name", "apple"));
        SearchRequest request = new SearchRequest("user").source(builder);
        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);
        printHits(resp);
    }

    @Test
    public void matchTest() throws Exception {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MatchQueryBuilder query = QueryBuilders.matchQuery("name", "w.dehai apple");
        query.operator(Operator.AND); // 默认为or
        query.analyzer("ik"); // 引入ik分词器
        query.lenient(true);
        query.fuzziness(Fuzziness.AUTO);
        query.zeroTermsQuery(ZeroTermsQuery.ALL);

        builder.query(query);
        SearchRequest request = new SearchRequest().source(builder);
        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);
        printHits(resp);
    }

    @Test
    public void highlightTest() throws Exception {
        HighlightBuilder.Field name = new HighlightBuilder.Field("name");
        name.highlighterType("unified");
        HighlightBuilder hb = new HighlightBuilder();
        hb.field(name);
        SearchSourceBuilder builder = new SearchSourceBuilder().highlighter(hb).query(QueryBuilders.matchQuery("name", "w.dehai"));
        SearchRequest request = new SearchRequest().source(builder);
        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = resp.getHits();
        for (SearchHit hit : hits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField field = highlightFields.get("name");
            Text[] texts = field.getFragments();
            if (texts != null && texts.length > 0) {
                for (Text text : texts) {
                    System.err.println(text.toString());
                }
            }
        }
        
    }

    @Test
    public void mappingTest() throws Exception {
        CreateIndexRequest request = new CreateIndexRequest("user");
        Map<String, String> id = new HashMap<>();
        id.put("type", "long");
        Map<String, String> name = new HashMap<>();
        name.put("type", "text");
        Map<String, String> password = new HashMap<>();
        password.put("type", "keyword");
        Map<String, String> email = new HashMap<>();
        email.put("type", "text");
        Map<String, String> age = new HashMap<>();
        age.put("type", "integer");
        Map<String, Object> props = new HashMap<>();
        props.put("id", id);
        props.put("name", name);
        props.put("password", password);
        props.put("email", email);
        props.put("age", age);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", props);
        request.mapping(mapping);
        
        request.settings(Settings.builder() 
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
            );
        
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    // 官方文档上面的外层分组的.field(password.keyword)行不通，而去掉keyword就可以，不知是为何
    @Test
    public void aggregationTest() throws Exception {
        TermsAggregationBuilder tab = AggregationBuilders.terms("by_password").field("password").subAggregation(AggregationBuilders.avg("average_age").field("age"));
        SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(tab);
        SearchRequest request = new SearchRequest("user").source(builder);
        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);
        Aggregations aggs = resp.getAggregations();
        Terms agg = aggs.get("by_password");
        List<? extends Bucket> bucketList = agg.getBuckets();
        if (bucketList != null && !bucketList.isEmpty()) {
            for (Bucket bucket : bucketList) {
                Avg avg = bucket.getAggregations().get("average_age");
                System.err.println(avg.getValue());
            }
        }
    }
    
    @Test
    public void suggestTest() throws Exception {
        SuggestBuilders.termSuggestion("name").text("w.dehai");
    }
    
    

}







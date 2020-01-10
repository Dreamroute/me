package com.github.dreamroute.me.server.service.impl;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.me.server.entity.BaseEntity;
import com.github.dreamroute.me.server.entity.BaseResp;
import com.github.dreamroute.me.server.entity.DeleteResp;
import com.github.dreamroute.me.server.entity.InsertResp;
import com.github.dreamroute.me.server.entity.UpdateResp;
import com.github.dreamroute.me.server.exception.MeException;
import com.github.dreamroute.me.server.service.CrudService;

/**
 * @author w.dehai
 */
@Component
public class CrudServiceImpl implements CrudService {

    @Autowired
    private RestHighLevelClient client;

    private void processResp(DocWriteResponse resp, BaseResp baseResp) {
        ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
        int total = shardInfo.getTotal();
        int successNum = shardInfo.getSuccessful();
        boolean suc = total == successNum;
        baseResp.setTotal(successNum);
        baseResp.setAllshardSuccess(suc);
        baseResp.setSuccess(successNum);

        if (shardInfo.getFailed() > 0) {
            baseResp.setFaild(shardInfo.getFailed());
            baseResp.setFailures(shardInfo.getFailures());
        }
    }

    @Override
    public InsertResp insert(BaseEntity entity, String index) {
        String json = JSON.toJSONString(entity);
        return insert(json, index);
    }

    @Override
    public InsertResp insert(String entity, String index) {

        Object id = JSON.parseObject(entity, BaseEntity.class).getId();
        IndexRequest request = new IndexRequest(index).id(String.valueOf(id)).source(entity, XContentType.JSON);
        try {
            IndexResponse resp = client.index(request, RequestOptions.DEFAULT);
            InsertResp insertResp = InsertResp.builder().resp(resp).build();
            processResp(resp, insertResp);
            return insertResp;
        } catch (IOException e) {
            throw new MeException("新增失败" + e, e);
        }
    }

    @Override
    public UpdateResp update(String entity, String index) {
        Object id = JSON.parseObject(entity, BaseEntity.class).getId();
        UpdateRequest request = new UpdateRequest(index, String.valueOf(id)).doc(entity, XContentType.JSON);
        try {
            UpdateResponse resp = client.update(request, RequestOptions.DEFAULT);
            UpdateResp updateResp = UpdateResp.builder().resp(resp).build();
            processResp(resp, updateResp);
            return updateResp;
        } catch (IOException e) {
            throw new MeException("" + e, e);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                throw new MeException("你修改的数据不存在" + e, e);
            }
            throw new MeException("修改失败" + e, e);
        }
    }

    @Override
    public UpdateResp update(BaseEntity entity, String index) {
        String json = JSON.toJSONString(entity);
        return update(json, index);
    }

    @Override
    public DeleteResp delete(Object id, String index) {
        DeleteRequest request = new DeleteRequest(index, String.valueOf(id));
        try {
            DeleteResponse resp = client.delete(request, RequestOptions.DEFAULT);
            DeleteResp deleteResp = DeleteResp.builder().resp(resp).build();
            processResp(resp, deleteResp);
            return deleteResp;
        } catch (IOException e) {
            throw new MeException("删除失败" + e, e);
        }
    }

    @Override
    public void putMapping(String index, Map<String, Object> mapping) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder().put("index.number_of_shards", 5).put("index.number_of_replicas", 2));
        request.mapping(mapping);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new MeException("创建索引失败，索引名: " + index + ", mapping: " + JSON.toJSONString(mapping), e);
        }
    }

    @Override
    public boolean exist(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new MeException("查询索引是否存在报错" + e, e);
        }
    }

    @Override
    public void deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new MeException("删除索引报错, 索引：" + index, e);
        }
    }

}

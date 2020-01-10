package com.github.dreamroute.me.server.service;

import java.util.Map;

import com.github.dreamroute.me.server.entity.BaseEntity;
import com.github.dreamroute.me.server.entity.DeleteResp;
import com.github.dreamroute.me.server.entity.InsertResp;
import com.github.dreamroute.me.server.entity.UpdateResp;
/**
 * @author w.dehai
 */
public interface CrudService {
    
    /**
     * 新增
     * 
     * @param entity
     * @param index
     * @return
     */
    InsertResp insert(BaseEntity entity, String index);
    
    /**
     * 新增
     * 
     * @param entity json字符串，必须要带有id属性
     * @param index
     * @return
     */
    InsertResp insert(String entity, String index);
    
    /**
     * ES的修改类似于updateSelective，空值不进行修改，而insert也能修改，但是空值也会进行修改
     * 
     * @param entity
     * @param index
     * @return
     */
    UpdateResp update(BaseEntity entity, String index);
    
    /**
     * 修改
     * 
     * @param entity json字符串，必须要带有id属性
     * @param index
     * @return
     */
    UpdateResp update(String entity, String index);
    
    /**
     * 根据id删除
     * 
     * @param id
     * @param index
     * @return
     */
    DeleteResp delete(Object id, String index);
    
    /**
     * 创建索引，需要Mapping
     * 
     * @param index
     * @param mapping
     */
    void putMapping(String index, Map<String, Object> mapping);
    
    /**
     * 查询索引是否存在
     * 
     * @param index
     * @return
     */
    boolean exist(String index);

    /**
     * 删除索引
     * @param index
     */
    void deleteIndex(String index);

}

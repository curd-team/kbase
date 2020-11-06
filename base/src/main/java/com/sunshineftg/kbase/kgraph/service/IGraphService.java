package com.sunshineftg.kbase.kgraph.service;

import com.sunshineftg.kbase.kgraph.domain.query.GraphQuery;

import java.util.HashMap;

public interface IGraphService {

    /**
     * 查询图谱节点和关系
     * @param query 查询条件
     * @return node relationship
     */
    HashMap<String, Object> getDomainGraph(GraphQuery query);

    HashMap<String, Object> getMoreRelationNode(String domain, String nodeId,String count);
}

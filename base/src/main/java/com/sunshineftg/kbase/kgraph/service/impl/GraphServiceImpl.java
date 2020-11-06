package com.sunshineftg.kbase.kgraph.service.impl;

import com.sunshineftg.kbase.kgraph.domain.query.GraphQuery;
import com.sunshineftg.kbase.kgraph.repository.GraphRepository;
import com.sunshineftg.kbase.kgraph.service.IGraphService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class GraphServiceImpl implements IGraphService {
    @Resource
    private GraphRepository graphRepository;


    @Override
    public HashMap<String, Object> getDomainGraph(GraphQuery query) {
        return graphRepository.getDomainGraph(query);
    }

    @Override
    public HashMap<String, Object> getMoreRelationNode(String domain, String nodeId,String count) {
        return graphRepository.getMoreRelationNode(domain,nodeId,count);
    }
}

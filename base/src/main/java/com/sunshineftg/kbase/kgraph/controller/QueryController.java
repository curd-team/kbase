package com.sunshineftg.kbase.kgraph.controller;

import com.alibaba.fastjson.JSON;
import com.sunshineftg.kbase.common.BaseResponse;
import com.sunshineftg.kbase.kgraph.domain.query.GraphQuery;
import com.sunshineftg.kbase.kgraph.service.IGraphService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

@RequestMapping("/graph/query")
@RestController
@Slf4j
public class QueryController {

    @Resource
    private IGraphService graphService;

    @GetMapping(value = "/getDomainPageList")
    public BaseResponse<Object> getDomainPageList(GraphQuery queryItem) {
        return BaseResponse.success(null);
    }

    // http://localhost:8080/graph/query/getDomainGraph?domain=&domainType=03
    @GetMapping(value = "/getDomainGraph")
    public BaseResponse<HashMap<String, Object>> getDomainGraph(GraphQuery query) {
        log.info("getDomainGraph query:{}", JSON.toJSONString(query));
        if ("基金".equals(query.getDomain()) && StringUtils.isNotEmpty(query.getDomainType()) && StringUtils.isEmpty(query.getNodeName())) {
            // 针对基金领域特殊处理
            query.setMatchType(1);
            if ("01".equals(query.getDomainType()) ) {
                query.setNodeName("【基金】");
            }
            if ("02".equals(query.getDomainType()) ) {
                query.setNodeName("【基金经理】");
            }
            if ("03".equals(query.getDomainType()) ) {
                query.setNodeName("【基金公司】");
            }
        }
        return BaseResponse.success(graphService.getDomainGraph(query));
    }


    // http://localhost:8080/graph/query/getMoreRelationNode?domain=%E5%9F%BA%E9%87%91&nodeId=12883
    @GetMapping(value = "/getMoreRelationNode")
    public BaseResponse<HashMap<String, Object>> getMoreRelationNode(String domain, String nodeId,String count) {
        if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(nodeId)){
            return BaseResponse.fail("参数不符合");
        }
        if (StringUtils.isEmpty(count)) {
            count = "50";
        }
        return BaseResponse.success(graphService.getMoreRelationNode(domain, nodeId,count));

    }



}

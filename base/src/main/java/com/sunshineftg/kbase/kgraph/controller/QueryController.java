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

    @GetMapping(value = "/getDomainGraph")
    public BaseResponse<HashMap<String, Object>> getDomainGraph(GraphQuery query) {
        log.info("getDomainGraph query:{}", JSON.toJSONString(query));
        return BaseResponse.success(graphService.getDomainGraph(query));
    }

    @GetMapping(value = "/getMoreRelationNode")
    public BaseResponse<HashMap<String, Object>> getMoreRelationNode(String domain, String nodeId) {
        if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(nodeId)){
            return BaseResponse.fail("参数不符合");
        }
        return BaseResponse.success(graphService.getMoreRelationNode(domain, nodeId));

    }



}

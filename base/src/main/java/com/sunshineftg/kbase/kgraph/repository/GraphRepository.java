package com.sunshineftg.kbase.kgraph.repository;

import com.sunshineftg.kbase.kgraph.domain.query.GraphQuery;
import com.sunshineftg.kbase.kgraph.util.Neo4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class GraphRepository {

    @Resource
    private Neo4jUtil neo4jUtil;


    /**
     * 查询图谱节点和关系
     * @return node relationship
     */
    public HashMap<String, Object> getDomainGraph(GraphQuery query) {
        HashMap<String, Object> nr = new HashMap<String, Object>();
        try {
            String domain = query.getDomain();
            // MATCH (n:`症状`) -[r]-(m:症状) where r.name='治疗' or r.name='危险因素' return n,m
            if (!StringUtils.isBlank(domain)) {
                String cqr = "";
                List<String> lis = new ArrayList<String>();
                if (query.getRelation() != null && query.getRelation().length > 0) {
                    for (String r : query.getRelation()) {
                        String it = String.format("r.name='%s'", r);
                        lis.add(it);
                    }
                    cqr = String.join(" or ", lis);
                }
                String cqWhere = "";
                if (StringUtils.isNotEmpty(query.getDomainType()) || !StringUtils.isBlank(query.getNodeName()) || !StringUtils.isBlank(cqr)) {
                    if (!StringUtils.isBlank(query.getNodeName())) {
                        if (query.getMatchType() == 1) {
                            cqWhere = String.format("where n.name ='%s' ", query.getNodeName());
                        } else {
                            cqWhere = String.format("where n.name contains('%s')", query.getNodeName());
                        }
                    }
                    String nodeOnly = cqWhere;
                    if (!StringUtils.isBlank(cqr)) {
                        if (StringUtils.isBlank(cqWhere)) {
                            cqWhere = String.format(" where ( %s )", cqr);
                        } else {
                            cqWhere += String.format(" and ( %s )", cqr);
                        }
                    }

                    if (StringUtils.isNotEmpty(query.getDomainType())) {
                        cqWhere+= String.format(" and n._domainType = '%s'",query.getDomainType());
                    }
                    // 下边的查询查不到单个没有关系的节点,考虑要不要左箭头
                    String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m) %s return * limit %s", domain, cqWhere,
                            query.getPageSize());
                    HashMap<String, Object> graphNode = neo4jUtil.GetGraphNodeAndShip(nodeSql);
                    Object node = graphNode.get("node");
                    // 没有关系显示则显示节点
                    if (node != null) {
                        nr.put("node", graphNode.get("node"));
                        nr.put("relationship", graphNode.get("relationship"));
                    } else {
                        String nodecql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain,
                                nodeOnly, query.getPageSize());
                        List<HashMap<String, Object>> nodeItem = neo4jUtil.GetGraphNode(nodecql);
                        nr.put("node", nodeItem);
                        nr.put("relationship", new ArrayList<HashMap<String, Object>>());
                    }
                } else {
                    String nodeSql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain, cqWhere,
                            query.getPageSize());
                    List<HashMap<String, Object>> graphNode = neo4jUtil.GetGraphNode(nodeSql);
                    nr.put("node", graphNode);
                    String domainSql = String.format("MATCH (n:`%s`)<-[r]-> (m) %s RETURN distinct(r) limit %s", domain,
                            cqWhere, query.getPageSize());// m是否加领域
                    List<HashMap<String, Object>> graphRelation = neo4jUtil.GetGraphRelationShip(domainSql);
                    nr.put("relationship", graphRelation);
                }
            }
        } catch (Exception e) {
            log.error("query domain graph ex",e);
        }
        return nr;
    }

    public HashMap<String, Object> getMoreRelationNode(String domain, String nodeId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s  return * limit 100", domain, nodeId);
            result = neo4jUtil.GetGraphNodeAndShip(cypherSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

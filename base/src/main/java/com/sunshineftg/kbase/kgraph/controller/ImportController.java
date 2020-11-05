package com.sunshineftg.kbase.kgraph.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.csvreader.CsvReader;
import com.sunshineftg.kbase.kgraph.util.Neo4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * 1: 创建领域
 * CREATE INDEX ON:测试(name);
 *
 * 2:查询 节点 关系 节点
 * Match (a:`测试`)-[r:RE]->(b:`测试`) return a,r,b;
 *
 * 3:这个查询适用于删除少量的数据，不适用于删除巨量的数据
 * match (n) detach delete n
 *
 * 4:删除一个节点及其所有的关系
 * match (n : {name : "Andres"}) detach delete n
 */

@RestController
@RequestMapping("graph")
@Slf4j
public class ImportController {

    @Autowired
    private Neo4jUtil neo4jUtil;

    /**
     *  {"index":{"_index":"keyword","_id":"%s"}}
     *  {"type":%s,"text":"ztt","termText":"ztt","num":0}
     */
    public String  generateEsJsonFile() throws IOException {
        List<String> fundCodes = FileUtils.readLines(new File("db/fundCodes.dic"), StandardCharsets.UTF_8);
        int j = 1;
        String oneP = "{\"index\":{\"_index\":\"keyword\",\"_id\":\"%s\"}}";
        String twoP = "{\"type\":%s,\"text\":\"%s\",\"termText\":\"%s\",\"num\":0}";
        for (String fundCode : fundCodes) {
            String one = String.format(oneP, j++);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(one), true);
            String two = String.format(twoP, "1", fundCode,fundCode);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(two), true);
        }
        List<String> fundNames = FileUtils.readLines(new File("db/fundNames.dic"), StandardCharsets.UTF_8);
        for (String fundName : fundNames) {
            String one = String.format(oneP, j++);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(one), true);
            String two = String.format(twoP, "2", fundName,fundName);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(two), true);
        }
        List<String> managerNames = FileUtils.readLines(new File("db/managerNames.dic"), StandardCharsets.UTF_8);
        for (String managerName : managerNames) {
            String one = String.format(oneP, j++);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(one), true);
            String two = String.format(twoP, "3", managerName,managerName);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(two), true);
        }
        List<String> fundComs = FileUtils.readLines(new File("db/fundComs.dic"), StandardCharsets.UTF_8);
        for (String fundCom : fundComs) {
            String one = String.format(oneP, j++);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(one), true);
            String two = String.format(twoP, "4", fundCom,fundCom);
            FileUtils.writeLines(new File("db/esIndex.json"), Collections.singleton(two), true);
        }
        return "ok";

    }

    public static void main(String[] args) throws IOException {
        String s = new ImportController().generateEsJsonFile();
        System.out.println("s = " + s);
    }




    /**
     * 节点属性解释：
     *
     * _domainType: 01:基金信息 / 02:基金经理 / 03:基金公司 （一个领域内进行再次抽象分类）
     * color: 节点颜色
     * r: 节点半径
     * name: 显示名称 nodeName
     * & otherProperties
     * http://localhost:8080/graph/loadCsv?domain=%E5%9F%BA%E9%87%91
     */
    @GetMapping("loadCsv")
    public String loadCsv(String csvLocation ,String domain) throws IOException {
        if (StringUtils.isEmpty(domain)) {
            domain = "domain";
        }
        // 基金经理
        HashMap<String,String> fundManagersCommonNodeProperties = new HashMap<>();
        fundManagersCommonNodeProperties.put("_domainType","02");
        fundManagersCommonNodeProperties.put("color","#6699ff");
        fundManagersCommonNodeProperties.put("r","60");

        // 基金公司
        HashMap<String, String> fundComsCommonNodeProperties = new HashMap<>();
        fundComsCommonNodeProperties.put("_domainType","03");
        fundComsCommonNodeProperties.put("color","#ff6600");
        fundComsCommonNodeProperties.put("r","60");

        // 基金
        HashMap<String, String> fundNodeProperties = new HashMap<>();
        fundNodeProperties.put("_domainType","01");
        fundNodeProperties.put("color","#669900");
        fundNodeProperties.put("r","60");

        // 基金类型
        HashMap<String, String> fundTypeNodeProperties = new HashMap<>();
        fundTypeNodeProperties.put("_domainType","01");
        fundTypeNodeProperties.put("color","#99ffff");
        fundTypeNodeProperties.put("r","60");

        // 创建领域
        String addIndexCypher = " CREATE INDEX ON :" + domain + "(name);";
        neo4jUtil.excuteCypherSql(addIndexCypher);
        csvLocation = "db/fundsData.csv";
        int i = 0;
        HashSet<String> fundCodes = new HashSet<>(),fundNames =new HashSet<>(), managerNames =new HashSet<>(),fundComs = new HashSet<>();
        try {
            CsvReader csvReader = new CsvReader(csvLocation, ',', StandardCharsets.UTF_8);
            // csvReader.readHeaders(); 过滤表头
            while ( /*i<100 && */ csvReader.readRecord()) {
                i++;
                String fundStr = csvReader.get(0);
                Map<String,String> fundMap = JSON.parseObject(fundStr, Map.class); // 基金信息
                fundMap.put("_domainType","01"); // 抽象分类
                fundNames.add(fundMap.get("name"));
                fundCodes.add(fundMap.get("fundCode"));


                fundMap.put("color","#04B404");//基金换个颜色
                List<Map> fundManagers = JSON.parseArray(csvReader.get(1),Map.class);  // 基金经理


                String fundComStr = csvReader.get(2);
                Map<String,String> fundCom = JSON.parseObject(fundComStr, Map.class); // 基金公司
                fundCom.put("_domainType","03");
                fundComs.add(fundCom.get("name"));

                String finalDomain = domain;
                fundManagers.forEach(item -> {
                    //  基金经理 -管理-> 基金
                    Map<String,String> map = new HashMap<>();
                    map.put("source",item.get("name").toString());
                    item.put("color","#0080FF");//基金经理换个颜色
                    item.put("_domainType","02"); // 抽象分类
                    map.put("sourceProperties",JSON.toJSONString(item));
                    map.put("target",fundMap.get("name"));
                    map.put("targetProperties",JSON.toJSONString(fundMap));
                    map.put("relation","管理");
                    createNodeAndRelation(map, finalDomain);
                    //  基金经理-员工->基金公司
                    map.put("target",fundCom.get("name"));
                    map.put("targetProperties",JSON.toJSONString(fundCom));
                    map.put("relation","员工");
                    createNodeAndRelation(map, finalDomain);
                    //  基金经理 -是-> 固定关联 根节点（基金经理）
                    map.put("target","【基金经理】");
                    map.put("targetProperties",JSON.toJSONString(fundManagersCommonNodeProperties));
                    map.put("relation","是");
                    createNodeAndRelation(map, finalDomain);

                    managerNames.add(item.get("name").toString());
                });
                //  基金公司-发行->基金
                Map<String,String> map2 = new HashMap<>();
                map2.put("source",fundCom.get("name"));
                map2.put("sourceProperties",JSON.toJSONString(fundCom));
                map2.put("target",fundMap.get("name"));
                map2.put("targetProperties",JSON.toJSONString(fundMap));
                map2.put("relation","发行");
                createNodeAndRelation(map2, finalDomain);

                //  基金公司-是->固定关联 根节点（基金公司）
                map2.put("target","【基金公司】");
                map2.put("targetProperties",JSON.toJSONString(fundComsCommonNodeProperties));
                map2.put("relation","是");
                createNodeAndRelation(map2, finalDomain);

                // 基金-类型->基金类型
                Map<String,String> map3 = new HashMap<>();
                map3.put("source","【基金】");
                map3.put("sourceProperties",JSON.toJSONString(fundNodeProperties));
                map3.put("target",fundMap.get("fundType"));
                map3.put("targetProperties",JSON.toJSONString(fundTypeNodeProperties));
                map3.put("relation","类型");
                createNodeAndRelation(map3, finalDomain);

                // 基金类型 -含有->基金
                Map<String,String> map4 = new HashMap<>();
                map4.put("source",fundMap.get("fundType"));
                map4.put("sourceProperties",JSON.toJSONString(fundTypeNodeProperties));
                map4.put("target",fundMap.get("name"));
                map4.put("targetProperties",JSON.toJSONString(fundMap));
                map4.put("relation","含有");
                createNodeAndRelation(map4, finalDomain);
            }
        } catch (IOException e) {
           log.error("ex",e);
        }
        log.info("========= import success! count:{}============",i);
        // 生成es分词词典
        // generateEsDicFile(fundCodes,fundNames,managerNames,fundComs);
        return "ok";
    }


    /**
     *  创建源节点 关系 目标节点
     * @param map source sourceProperties target targetProperties relation
     *                   source: 源节点Name
     *                   sourceProperties: 源节点属性 （color and r）color-颜色  r-展示的节点半径 & otherProperties JSON格式
     *                   target: 目标节点Name
     *                   targetProperties: 目标节点属性同 sourceProperties
     *                   relation: 关系
     * @param domain 领域
     */
    private void createNodeAndRelation(Map<String,String> map,String domain){
        // 创建源节点 step1
        JSONObject jsonObject = JSON.parseObject(map.get("sourceProperties"));
        jsonObject.put("name",map.get("source"));
        jsonObject.putIfAbsent("color","#ff4500");
        jsonObject.putIfAbsent("r",30);
        List<HashMap<String, Object>> sResult = createAndUpdateNode(jsonObject,domain);
        String sourceUuid = String.valueOf(sResult.get(0).get("uuid"));

        // 创建目标节点 step2
        JSONObject tarJsonObject = JSON.parseObject(map.get("targetProperties"));
        tarJsonObject.put("name",map.get("target"));
        tarJsonObject.putIfAbsent("color","#ff4500");
        tarJsonObject.putIfAbsent("r",30);
        List<HashMap<String, Object>> tResult = createAndUpdateNode(tarJsonObject,domain);
        String targetUuid = String.valueOf(tResult.get(0).get("uuid"));

        // 创建关系
        String rSql = String.format(
                "match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s MERGE (n)-[r:RE {name:'%s'}]->(m) return r",
                domain, domain, sourceUuid, targetUuid, map.get("relation"));
        List<HashMap<String, Object>> relationList = neo4jUtil.GetGraphRelationShip(rSql);
        System.out.println("relationList = " + JSON.toJSONString(relationList));
    }

    /**
     *  根据节点 领域 名字 _domainType进行创建及更新
     * @param nodeProperties 节点属性
     * @param domain 领域
     * @return 节点信息
     */
    private List<HashMap<String, Object>> createAndUpdateNode(JSONObject nodeProperties,String domain){
        String getByNameCypherSql = String.format("match (n:`%s`) where n.name='%s' and n._domainType='%s' return n;",domain,nodeProperties.get("name"),nodeProperties.get("_domainType"));
        List<HashMap<String, Object>> nodeResult = neo4jUtil.GetGraphNode(getByNameCypherSql);
        String propertiesString = neo4jUtil.getFilterPropertiesJson(nodeProperties.toJSONString());
        if (CollectionUtils.isNotEmpty(nodeResult)) {
            // 更新修改节点信息,覆盖节点属性
            String updateCypher = String.format("MATCH (n:`%s`) where id(n)='%s' SET n=%s return n;", domain, nodeResult.get(0).get("uuid"), propertiesString);
            neo4jUtil.GetGraphNode(updateCypher);
            return nodeResult;
        }
        String cypherSql = String.format("create (n:`%s` %s) return n", domain, propertiesString);
        nodeResult = neo4jUtil.GetGraphNode(cypherSql);
        System.out.println("nodeResult = " + JSON.toJSONString(nodeResult));
        return nodeResult;
    }


    /**
     * 根据节点名字创建节点-如果存在相同名字的进行更新
     * @param nodeProperties 节点属性
     * @param domain 领域
     * @return 该节点信息
     */
    private List<HashMap<String, Object>> mergeCreateNode(JSONObject nodeProperties,String domain){
        String propertiesString = neo4jUtil.getFilterPropertiesJson(nodeProperties.toJSONString());
        String cypherSql = String.format("MERGE (n:`%s` %s) return n", domain, propertiesString);
        List<HashMap<String, Object>>  nodeResult = neo4jUtil.GetGraphNode(cypherSql);
        System.out.println("nodeResult = " + JSON.toJSONString(nodeResult));
        return nodeResult;
    }


    private void generateEsDicFile(HashSet<String> fundCodes,HashSet<String> fundNames,HashSet<String> managerNames,HashSet<String> fundComs) throws IOException {
        log.info("fundCodes size:{}",fundCodes.size());
        log.info("fundNames size:{}",fundNames.size());
        log.info("managerNames size:{}",managerNames.size());
        log.info("fundComs size:{}",fundComs.size());
        for (String fundCode : fundCodes) {
             FileUtils.writeLines(new File("db/fundCodes.dic"), Collections.singleton(fundCode), true);
        }
        for (String fundName : fundNames) {
            FileUtils.writeLines(new File("db/fundNames.dic"), Collections.singleton(fundName), true);
        }
        for (String managerName : managerNames) {
            FileUtils.writeLines(new File("db/managerNames.dic"), Collections.singleton(managerName), true);
        }
        for (String item : fundComs) {
            FileUtils.writeLines(new File("db/fundComs.dic"), Collections.singleton(item), true);
        }
    }







}

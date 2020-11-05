package com.sunshineftg.kbase.es.controller;

import com.sunshineftg.elasticsearch.entity.Article;
import com.sunshineftg.elasticsearch.entity.Keyword;
import com.sunshineftg.elasticsearch.service.ElasticSearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: zhangtietuo
 * @Description:
 * @Date: 2020/10/29 15:39
 */
@RestController
@RequestMapping("/es")
public class EsController {

    @Resource
    ElasticSearchService elasticSearchService;

    @PostMapping("/createIndex")
    public String createIndex(@RequestBody String index) throws IOException {
        return elasticSearchService.createIndex(index);
    }

    @PostMapping("/createIndexArticle")
    public String createIndexArticle() throws IOException {
        return elasticSearchService.createIndexArticle();
    }

    @PostMapping("/createIndexKeyword")
    public String createIndexKeyword() throws IOException {
        return elasticSearchService.createIndexKeyword();
    }


    @PostMapping("/existIndex")
    public Boolean existIndex(@RequestBody String index) throws IOException {
        return elasticSearchService.existIndex(index);
    }

    @PostMapping("/deleteIndex")
    public String deleteIndex(@RequestBody String index) throws IOException {
        return elasticSearchService.deleteIndex(index);
    }

    @PostMapping("/addDocument")
    public String addDocument(@RequestBody Article article) throws IOException {
        return elasticSearchService.addDocument(article);
    }

    @PostMapping("/isExists")
    public Boolean isExists(@RequestBody Article article) throws IOException {
        return elasticSearchService.isExists(article);
    }

    @PostMapping("/getDocument")
    public String getDocument(@RequestBody Article article) throws IOException {
        return elasticSearchService.getDocument(article);
    }

    @PostMapping("/updateDocument")
    public String updateDocument(@RequestBody Article article) throws IOException {
        return elasticSearchService.updateDocument(article);
    }

    @PostMapping("/bulkRequest")
    public String bulkRequest(@RequestBody List<Article> articleList) throws IOException {
        return elasticSearchService.bulkRequest(articleList);
    }

    @PostMapping("/deleteDocument")
    public String deleteDocument(@RequestBody Article article) throws IOException {
        return elasticSearchService.deleteDocument(article);
    }


    @PostMapping("/queryArticleList")
    public Map<String, Object> queryArticleList(@RequestBody Article article) throws IOException {
        return elasticSearchService.queryArticleList(article);
    }

    @PostMapping("/queryKeyword")
    public List<String> queryKeyword(@RequestBody Keyword keyword) throws IOException {
        return elasticSearchService.queryKeyword(keyword);
    }

}

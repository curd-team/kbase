package com.sunshineftg.elasticsearch.service;

import com.sunshineftg.elasticsearch.entity.Article;
import com.sunshineftg.elasticsearch.entity.Keyword;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Auther: zhangtietuo
 * @Description:
 * @Date: 2020/10/29 15:44
 */
public interface ElasticSearchService {

    /**
     * 创建索引
     * @param index
     * @throws IOException
     */
    String createIndex(String index) throws IOException;

    String createIndexArticle() throws IOException;

    String createIndexKeyword() throws IOException;

    /**
     * 判断索引是否存在
     * @param index
     * @throws IOException
     */
    Boolean existIndex(String index) throws IOException;

    /**
     * 删除索引
     * @param index
     * @throws IOException
     */
    String deleteIndex(String index) throws IOException;

    /**
     * 添加文档
     * @throws IOException
     */
    String addDocument(Article article) throws IOException;

    /**
     * 判断文档是否存在
     * @throws IOException
     */
    Boolean isExists(Article article) throws IOException;

    /**
     * 获取文档的信息 GET /hcode_index/_doc/1
     * @throws IOException
     */
    String getDocument(Article article) throws IOException;

    /**
     * 更新文档信息
     * @throws IOException
     */
    String updateDocument(Article article) throws IOException;

    /**
     * 删除文档信息
     * @throws IOException
     */
    String deleteDocument(Article article) throws IOException;

    /**
     * 批量操作
     * @throws IOException
     */
    String bulkRequest(List<Article> article) throws IOException;

    /**
     * 搜索查询+高亮
     * @throws IOException
     */
    List<Article> queryArticleList(Article article) throws IOException;

    Set<String> analyze(String text) throws IOException;

    List<String> queryKeyword(Keyword keyword) throws IOException;
}

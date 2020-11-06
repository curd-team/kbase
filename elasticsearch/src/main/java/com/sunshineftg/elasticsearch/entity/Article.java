package com.sunshineftg.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Auther: zhangtietuo
 * @Description:
 * @Date: 2020/10/29 16:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    public static final String INDEX_NAME = "article";

    private String id;

    private String title;

    private String[] tags;

    private String desc;

    private String content;

    private String[] fileId;

    /**
     * 领
     */
    private String domain;

    /**
     * 上传者id
     */
    private String uid;

    private Long num;

    private LocalDateTime createDate;

    private String tagsStr;

    private String fileIdStr;

    /**
     * 查询用 不入索引库
     */
    private String tag;

    private Integer page;

    private Integer size;




}

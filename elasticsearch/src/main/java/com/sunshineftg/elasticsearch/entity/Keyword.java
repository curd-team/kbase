package com.sunshineftg.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: zhangtietuo
 * @Description: 关键词索引
 * @Date: 2020/11/5 13:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Keyword {

    public static final String INDEX_NAME = "keyword";

    private String id;

    /**
     * 关键词类型
     */
    private Integer type;

    private String text;

    private String termText;

    /**
     * 被搜索次数
     */
    private Long num;
}

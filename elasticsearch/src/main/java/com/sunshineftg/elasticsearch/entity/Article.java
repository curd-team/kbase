package com.sunshineftg.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private Long num;

    private LocalDate createDate;

    private String tag;

    private Integer page;

    private Integer size;


}

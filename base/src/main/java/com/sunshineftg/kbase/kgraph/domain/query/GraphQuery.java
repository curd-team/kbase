package com.sunshineftg.kbase.kgraph.domain.query;

import lombok.Data;

@Data
public class GraphQuery {
    private int domainId;
    private String domain;
    private String domainType;
    private String nodeName;
    private String[] relation;
    private int matchType ;
    private int pageSize = 10;
    private int pageIndex = 1;
}

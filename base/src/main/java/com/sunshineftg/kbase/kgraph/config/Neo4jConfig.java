package com.sunshineftg.kbase.kgraph.config;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

    @Value("${spring.neo4j.url:bolt://localhost:7687}")
    private String url;

    @Value("${spring.neo4j.username:neo4j}")
    private String username;

    @Value("${spring.neo4j.password:zhangjp}")
    private String password;

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }

}

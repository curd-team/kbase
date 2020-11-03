package com.sunshineftg.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: zhangtietuo
 * @Description:
 * @Date: 2020/10/29 15:33
 */
@Configuration
public class ElasticSearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        //有几个集群写几个！！
                        //new HttpHost("127.0.0.1",9200,"http"),
                        new HttpHost("39.107.105.125",9200,"http")
                )
        );
    }
}

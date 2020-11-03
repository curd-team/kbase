package com.sunshineftg.kbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages="com.sunshineftg")
@EnableZuulProxy
@EnableFeignClients
public class KbaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbaseApplication.class, args);
    }

}

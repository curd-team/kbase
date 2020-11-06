package com.sunshineftg.kbase.kgraph.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @program: kbase
 * @description: 开启跨域支持
 */
//@Profile({"test", "dev"})
@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter(corsConfigurationSource));
        registrationBean.setOrder(0);
        return registrationBean;
    }
}

package com.shu.hbase.config.commons;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("classpath:fdfs_client.conf")
    private Resource resource;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(1800)
                .allowCredentials(true);
    }


    @Bean
    public TrackerClient initClient() {
        try {
            ClientGlobal.init(resource.getFilename());
            log.info("FastDFS创建客户端成功");
            return new TrackerClient();
        } catch (Exception e) {
            log.info("FastDFS创建客户端失败");
            return null;
        }
    }
}
package com.soft.mongo;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(value=MongoConfig.class)
public class MongodbClient {

    @Autowired
    private MongoConfig mongoConfig;

    /**
     * 创建Mongo 连接驱动
     *
     * @return
     */
    @Bean
    public MongoClient newClient() {
        MongoClient mongo = new MongoClient(mongoConfig.getHost(), mongoConfig.getPort());
        return mongo;
    }

    @Bean
    public MongoTemplate newTemplate() {
        MongoTemplate template = new MongoTemplate(newClient(), mongoConfig.getDatabase());
        return template;
    }
}

package com.soft.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="spring.data.mongodb")
public class MongoConfig {

    private String host;

    private int port;

    private String database;

    public String getHost() {
        return host;
    }

    public MongoConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public MongoConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public MongoConfig setDatabase(String database) {
        this.database = database;
        return this;
    }
}

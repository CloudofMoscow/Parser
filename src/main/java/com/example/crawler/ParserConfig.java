package com.example.crawler;

public class ParserConfig {
    private final String rssUrl = "https://habr.com/ru/rss/articles/";
    private final String rabbitHost = "rabbitmq";
    private final String taskQueue = "task_queue";
    private final String resultQueue = "result_queue";
    private final String elasticHost = "elasticsearch";
    private final int elasticPort = 9200;
    private final String elasticIndex = "news";

    public String getRssUrl() { return rssUrl; }
    public String getRabbitHost() { return rabbitHost; }
    public String getTaskQueue() { return taskQueue; }
    public String getResultQueue() { return resultQueue; }
    public String getElasticHost() { return elasticHost; }
    public int getElasticPort() { return elasticPort; }
    public String getElasticIndex() { return elasticIndex; }
}

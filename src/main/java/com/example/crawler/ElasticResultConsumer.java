package com.example.crawler;

public class ElasticResultConsumer implements IResultConsumer {
    private final ResultConsumer consumer;

    public ElasticResultConsumer(String rabbitHost, String resultQueue, String elasticHost, int elasticPort, String elasticIndex) throws Exception {
        this.consumer = new ResultConsumer(rabbitHost, resultQueue);
        // elasticHost, elasticPort, elasticIndex можно использовать для расширения
    }

    @Override
    public void consume() throws Exception {
        consumer.consume();
    }
}

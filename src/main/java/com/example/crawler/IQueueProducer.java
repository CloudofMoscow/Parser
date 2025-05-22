package com.example.crawler;

public interface IQueueProducer extends AutoCloseable {
    void publish(String message) throws Exception;
    @Override
    void close() throws Exception;
}

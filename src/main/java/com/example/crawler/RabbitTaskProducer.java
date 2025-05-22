package com.example.crawler;

public class RabbitTaskProducer implements IQueueProducer {
    private final TaskProducer producer;

    public RabbitTaskProducer(String host, String queue) throws Exception {
        this.producer = new TaskProducer(host, queue);
    }

    @Override
    public void publish(String message) throws Exception {
        producer.sendTask(message);
    }

    @Override
    public void close() throws Exception {
        // Если TaskProducer реализует AutoCloseable, закрыть соединение
        // иначе оставить пустым
    }
}
package com.example.crawler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsCrawlerApp {

    private static final String RSS_FEED_URL = "https://habr.com/ru/rss/articles/";
    private static final String RABBITMQ_HOST = "rabbitmq";
    private static final String TASK_QUEUE_NAME = "task_queue";
    private static final String RESULT_QUEUE_NAME = "result_queue";
    private static final String ELASTICSEARCH_HOST = "elasticsearch";
    private static final int ELASTICSEARCH_PORT = 9200;
    private static final String ELASTICSEARCH_INDEX = "news";

    public static void main(String[] args) {
        System.out.println("=== News Crawler Started ===");
        NewsCrawlerApp app = new NewsCrawlerApp();
        app.run();
    }

    private void run() {
        List<Article> articles = retrieveArticles();
        if (articles != null && !articles.isEmpty()) {
            enqueueTasks(articles);
            initializeResultQueue();
            startWorkerAsync();
            startResultConsumer();
        } else {
            System.err.println("Не удалось получить статьи из RSS.");
        }
    }

    private List<Article> retrieveArticles() {
        try {
            RssCrawler rssCrawler = new RssCrawler();
            List<Article> articles = rssCrawler.fetchArticles(RSS_FEED_URL);
            System.out.printf("Получено статей: %d%n", articles.size());
            return articles;
        } catch (Exception ex) {
            System.err.println("Ошибка при получении статей: " + ex.getMessage());
            return null;
        }
    }

    private void enqueueTasks(List<Article> articles) {
        try (TaskProducer producer = new TaskProducer(RABBITMQ_HOST, TASK_QUEUE_NAME)) {
            for (Article article : articles) {
                producer.sendTask(article.link);
            }
        } catch (Exception ex) {
            System.err.println("Ошибка при отправке задач в RabbitMQ: " + ex.getMessage());
        }
    }

    private void initializeResultQueue() {
        try (TaskProducer resultProducer = new TaskProducer(RABBITMQ_HOST, RESULT_QUEUE_NAME)) {
            resultProducer.sendTask("init");
        } catch (Exception ex) {
            System.err.println("Ошибка при инициализации result_queue: " + ex.getMessage());
        }
    }

    private void startWorkerAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Worker worker = new Worker(RABBITMQ_HOST, TASK_QUEUE_NAME, RABBITMQ_HOST, RESULT_QUEUE_NAME);
                worker.start();
            } catch (Exception ex) {
                System.err.println("Ошибка воркера: " + ex.getMessage());
            }
        });
        executor.shutdown();
    }

    private void startResultConsumer() {
        try {
            ResultConsumer consumer = new ResultConsumer(RABBITMQ_HOST, RESULT_QUEUE_NAME);
            consumer.consume();
        } catch (Exception ex) {
            System.err.println("Ошибка ResultConsumer: " + ex.getMessage());
        }
    }
}

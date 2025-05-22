package com.example.crawler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParserStarter {

    public static void main(String[] args) {
        System.out.println("=== Parser System Boot ===");
        ParserConfig config = new ParserConfig();
        ParserStarter starter = new ParserStarter();
        starter.startPipeline(config);
    }

    private void startPipeline(ParserConfig config) {
        List<Article> articles = fetchRssArticles(config);
        if (articles == null || articles.isEmpty()) {
            System.err.println("No articles found in RSS feed.");
            return;
        }
        sendTasksToQueue(articles, config);
        ensureResultQueueExists(config);
        runWorkerAsync(config);
        runResultConsumer(config);
    }

    private List<Article> fetchRssArticles(ParserConfig config) {
        try {
            IRssFetcher fetcher = new HabrRssFetcher();
            List<Article> articles = fetcher.getArticles(config.getRssUrl());
            System.out.println("Articles fetched: " + articles.size());
            return articles;
        } catch (Exception e) {
            System.err.println("RSS fetch error: " + e.getMessage());
            return null;
        }
    }

    private void sendTasksToQueue(List<Article> articles, ParserConfig config) {
        try (IQueueProducer producer = new RabbitTaskProducer(config.getRabbitHost(), config.getTaskQueue())) {
            for (Article article : articles) {
                producer.publish(article.link);
            }
        } catch (Exception e) {
            System.err.println("Task queue error: " + e.getMessage());
        }
    }

    private void ensureResultQueueExists(ParserConfig config) {
        try (IQueueProducer producer = new RabbitTaskProducer(config.getRabbitHost(), config.getResultQueue())) {
            producer.publish("init");
        } catch (Exception e) {
            System.err.println("Result queue init error: " + e.getMessage());
        }
    }

    private void runWorkerAsync(ParserConfig config) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                ITaskWorker worker = new ArticleWorker(
                    config.getRabbitHost(),
                    config.getTaskQueue(),
                    config.getRabbitHost(),
                    config.getResultQueue()
                );
                worker.process();
            } catch (Exception e) {
                System.err.println("Worker error: " + e.getMessage());
            }
        });
        executor.shutdown();
    }

    private void runResultConsumer(ParserConfig config) {
        try {
            IResultConsumer consumer = new ElasticResultConsumer(
                config.getRabbitHost(),
                config.getResultQueue(),
                config.getElasticHost(),
                config.getElasticPort(),
                config.getElasticIndex()
            );
            consumer.consume();
        } catch (Exception e) {
            System.err.println("Result consumer error: " + e.getMessage());
        }
    }
}

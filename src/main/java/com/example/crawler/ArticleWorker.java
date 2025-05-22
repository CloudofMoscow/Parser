package com.example.crawler;

public class ArticleWorker implements ITaskWorker {
    private final Worker worker;

    public ArticleWorker(String taskHost, String taskQueue, String resultHost, String resultQueue) throws Exception {
        this.worker = new Worker(taskHost, taskQueue, resultHost, resultQueue);
    }

    @Override
    public void process() throws Exception {
        worker.start();
    }
}

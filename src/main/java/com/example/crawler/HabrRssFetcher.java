package com.example.crawler;

import java.util.List;

public class HabrRssFetcher implements IRssFetcher {
    @Override
    public List<Article> getArticles(String rssUrl) throws Exception {
        RssCrawler crawler = new RssCrawler();
        return crawler.fetchArticles(rssUrl);
    }
}

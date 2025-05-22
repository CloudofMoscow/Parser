package com.example.crawler;

import java.util.List;

public interface IRssFetcher {
    List<Article> getArticles(String rssUrl) throws Exception;
}

package com.example.crawler;

public class Article {
    public String link;
    public String title;
    public String pubDate;
    public String author;
    public String description;

    public Article() {
        // пустой конструктор для удобства
    }

    public Article(String link) {
        this.link = link;
    }

    public Article(String link, String title, String pubDate, String author, String description) {
        this.link = link;
        this.title = title;
        this.pubDate = pubDate;
        this.author = author;
        this.description = description;
    }
}

package com.example.android.newsfeed;

import java.util.Date;

public class Publication {
    private String author;
    private String articleName;
    private String datePosted;
    private String timePosted;

    Publication(String author, String articleName, String datePosted, String timePosted){
        this.author = author;
        this.articleName = articleName;
        this.datePosted = datePosted;
        this.timePosted = timePosted;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getArticleName() {
        return articleName;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimePosted() {
        return timePosted;
    }
}

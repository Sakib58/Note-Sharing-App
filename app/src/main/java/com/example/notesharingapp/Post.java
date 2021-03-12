package com.example.notesharingapp;

import com.google.firebase.Timestamp;

public class Post {
    private String author,body,key;
    private Timestamp time;

    public Post(String author, String body, String key, Timestamp time) {
        this.author = author;
        this.body = body;
        this.key = key;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}

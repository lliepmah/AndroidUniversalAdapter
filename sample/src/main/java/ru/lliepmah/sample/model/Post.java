package ru.lliepmah.sample.model;

/**
 * Created by Arthur Korchagin on 26.10.16
 */

public class Post {

    private int id;
    private String title;
    private String text;

    public Post(int id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}

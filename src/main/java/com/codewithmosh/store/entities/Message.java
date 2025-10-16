package com.codewithmosh.store.entities;

public class Message {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message(String text) {
        this.text = text;
    }
}

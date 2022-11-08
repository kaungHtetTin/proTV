package com.protv.mm.models;

public class MenuModel {
    String title;
    int src;

    public MenuModel(String title, int src) {
        this.title = title;
        this.src = src;
    }

    public String getTitle() {
        return title;
    }

    public int getSrc() {
        return src;
    }
}

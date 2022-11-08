package com.protv.mm.models;

public class CategoryModel {
    public String title="";
    public String id="";
    boolean vip;

    public CategoryModel(String title, String id,boolean vip) {
        this.title = title;
        this.id = id;
        this.vip=vip;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public boolean isVip() {
        return vip;
    }
}

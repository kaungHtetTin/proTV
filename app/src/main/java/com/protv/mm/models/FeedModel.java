package com.protv.mm.models;

public class FeedModel {
    public String thumbnail="";
    public String title="";
    public String des="";
    public String d_url="";
    String id;
    boolean vip;

    public FeedModel(String id,String thumbnail, String title, String des, String d_url,boolean vip) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.des = des;
        this.d_url = d_url;
        this.id=id;
        this.vip=vip;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDes() {
        return des;
    }

    public String getD_url() {
        return d_url;
    }

    public String getId() {
        return id;
    }

    public boolean isVip() {
        return vip;
    }
}

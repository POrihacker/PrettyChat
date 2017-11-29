package com.example.sumeet.prettychat;

/**
 * Created by sumeet on 2017-10-10.
 */

public class FriendsModel {
    String name;
    String status;
    String thumbnail;

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    String  online;

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public FriendsModel() {
    }

    public void setName(String name) {
        this.name = name;
    }



}

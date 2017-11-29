package com.example.sumeet.prettychat;

/**
 * Created by sumeet on 2017-10-14.
 */

public class Messages {
    private String message, type,from,user;
    private Long time;
    private boolean seen;

    public Messages() {
    }

    public Messages(String message, String type, Long time, boolean seen,String from,String user) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from=from;
        this.user=user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}


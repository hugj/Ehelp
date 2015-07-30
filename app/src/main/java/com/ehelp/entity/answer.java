package com.ehelp.entity;

/**
 * Created by gj on 2015/7/30.
 */
public class answer {
    private int id;
    private int event_id;
    private int author_id;
    private String content;
    private String time;
    private int is_adopted;
    private int liking_num;
    private int status;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIs_adopted(int is_adopted) {
        this.is_adopted = is_adopted;
    }

    public void setLiking_num(int liking_num) {
        this.liking_num = liking_num;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    public void setAuthorId(int author_id) {
        this.author_id = author_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getIs_adopted() {
        return is_adopted;
    }

    public int getLiking_num() {
        return liking_num;
    }

    public String getContent() {
        return content;
    }

    public int getEventId() {
        return event_id;
    }

    public int getAuthoridId() { return author_id; }

    public String getTime() {
        return time;
    }

    public int getStatus() { return status; }
}

package com.ehelp.entity;

import java.io.Serializable;

/**
 * Created by gj on 2015/8/5.
 */
public class comment implements Serializable {
    /**
     * parent_author : 回复的人的昵称
     * event_id : -1
     * author : 评论者的昵称
     * time : 评论的时间
     * comment_id : -1
     * author_id : -1
     * content : 评论的内容
     */
    private String parent_author;
    private int event_id;
    private String author;
    private String time;
    private int comment_id;
    private int author_id;
    private String content;

    public void setParent_author(String parent_author) {
        this.parent_author = parent_author;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent_author() {
        return parent_author;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public String getContent() {
        return content;
    }
}

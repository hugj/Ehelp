package com.ehelp.entity;

/**
 * Created by thetruthmyg on 2015/7/27.
 */
public class Event {

    private int follow_number;
    private double latitude;
    private String title;
    private int type;
    private String content;
    private double group_pts;
    private int support_number;
    private String last_time;
    private int id;
    private String time;
    private int state;
    private int launcher;
    private String launcher_id;
    private double longitude;

    public void setFollow_number(int follow_number) {
        this.follow_number = follow_number;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGroup_pts(double group_pts) {
        this.group_pts = group_pts;
    }

    public void setSupport_number(int support_number) {
        this.support_number = support_number;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setLauncher(int launcher) {
        this.launcher = launcher;
    }

    public void setLauncher_id(String launcher_id) {
        this.launcher_id = launcher_id;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getFollow_number() {
        return follow_number;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public double getGroup_pts() {
        return group_pts;
    }

    public int getSupport_number() {
        return support_number;
    }

    public String getLast_time() {
        return last_time;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public int getLauncher() {
        return launcher;
    }

    public String getLauncher_id() {
        return launcher_id;
    }

    public double getLongitude() {
        return longitude;
    }
}

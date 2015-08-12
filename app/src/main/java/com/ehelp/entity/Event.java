package com.ehelp.entity;

import java.io.Serializable;

/**
 * Created by thetruthmyg on 2015/7/27.
 */
public class Event implements Serializable {

    private int follow_number;
    private double latitude;
    private String title;
    private int type;
    private String content;
    private double group_pts;
    private int support_number;
    private String last_time;
    private int event_id;
    private int launcher_id;
    private String time;
    private int state;
    private String launcher;
    private double longitude;
    private int demand_number;
    private int love_coin;

    public void setDemand_number(int demand_number) {
        this.demand_number = demand_number;
    }
    public void setLove_coin(int love_coin) {
        this.love_coin = love_coin;
    }

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

    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    public void setLauncherId(int launcher_id) {
        this.launcher_id = launcher_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setLauncher(String launcher) {
        this.launcher = launcher;
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

    public int getEventId() {
        return event_id;
    }

    public int getLauncherId() { return launcher_id; }

    public String getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public String getLauncher() {
        return launcher;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDemand_number() {
        return demand_number;
    }

    public int getLove_coin() {
        return love_coin;
    }
}

package com.ehelp.utils;

/**
 * 此类用于管理创建的Activity对象，用于集中销毁多个Activity对象
 * 创建此类时使用了单例模式，需要集中销毁的activity只需在其onCreate方法内调用addActivity()方法将其
 * 加入ActivityCollector的单例，然后在需要集中销毁时使用exit()方法即可。
 * Created by thetruthmyg on 2015/7/28.
 */

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ActivityCollector {
    private List<Activity> activities = null;
    private static ActivityCollector instance;

    private ActivityCollector() {
        activities = new LinkedList<Activity>();
    }

    public static ActivityCollector getInstance() {
        if (null == instance) {
            instance = new ActivityCollector();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activities != null && activities.size() > 0) {
            if (!activities.contains(activity)) {
                activities.add(activity);
            }
        } else {
            activities.add(activity);
        }
    }

    public void exit() {
        if (activities != null && activities.size() > 0) {
            for (Activity at : activities) {
                if (!at.isFinishing())
                    at.finish();
            }
        }
    }
}

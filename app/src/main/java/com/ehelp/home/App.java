package com.ehelp.home;

import android.app.Application;
import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化融云
         */
        RongIM.init(this);

        String Token = "2wrXo3KmBO5cFjO4UrUWEQxDSlzJO+c4eiOET2yX7vru3wUdo85mCscV4aqdIw2uLzz521H25MruJnKDUAa9Dg==";
        /**
         * IMKit SDK调用第二步
         *
         * 建立与服务器的连接
         *
         */
        RongIM.connect(Token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.v("MainActivity", "------onSuccess----" + userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.v("MainActivity", "------onError----" + errorCode);
            }

            @Override
            public void onTokenIncorrect() {
                Log.v("MainActivity", "------onTokenIncorrect----");
            }
        });
    }
}
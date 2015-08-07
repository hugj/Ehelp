package com.ehelp.home;

import android.app.Application;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;
import android.widget.Toast;

import com.ehelp.utils.RequestHandler;
import com.sea_monster.common.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class App extends Application {
    private String TAG = "myowndeveloptest";

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化融云
         */
        RongIM.init(this);

        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider。
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 来缓存用户信息。
         * @see UserInfoProvider
         */
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {
                String name = "用户";
                Uri uri = Uri.parse("http://120.24.208.130:1501/avatar/touxiang.jpg");

                String jsonString = "{" + "\"id\":"+ userId + "}";
                String msg = RequestHandler.sendPostRequest("http://120.24.208.130:1501/user/get_information", jsonString);
                Log.v(TAG, msg);
                if (msg.equals("false")) {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
                try{
                    JSONObject jO = new JSONObject(msg);
                    if (jO.getInt("status") == 500) {
                        Toast.makeText(getApplicationContext(), "获取失败",
                                Toast.LENGTH_SHORT).show();
                    } else if (jO.getInt("status") == 200){
                        name = jO.getString("nickname");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Parcel in = new Parcel();
//                ParcelUtils.writeToParcel(in, userId);
//                ParcelUtils.writeToParcel(in, name);
//                ParcelUtils.writeToParcel(in, "http://120.24.208.130:1501/avatar/touxiang.jpg");
//
//                UserInfo ui = new UserInfo(in);

                UserInfo ui = new UserInfo(userId, name, uri);

                return ui;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);

//        String Token = "2wrXo3KmBO5cFjO4UrUWEQxDSlzJO+c4eiOET2yX7vru3wUdo85mCscV4aqdIw2uLzz521H25MruJnKDUAa9Dg==";
//        /**
//         * IMKit SDK调用第二步
//         *
//         * 建立与服务器的连接
//         *
//         */
//        RongIM.connect(Token, new RongIMClient.ConnectCallback() {
//            @Override
//            public void onSuccess(String userId) {
//                Log.v("MainActivity", "------onSuccess----" + userId);
//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//                Log.v("MainActivity", "------onError----" + errorCode);
//            }
//
//            @Override
//            public void onTokenIncorrect() {
//                Log.v("MainActivity", "------onTokenIncorrect----");
//            }
//        });
    }
}
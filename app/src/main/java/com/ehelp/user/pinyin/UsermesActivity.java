package com.ehelp.user.pinyin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ehelp.R;

/**
 * Created by Administrator on 2015/7/20.
 */
public class UsermesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermes);
    }
    //点击跳转到个人主页
    public void user_page_click(View v) {
        Intent intent = new Intent(this, homepageActivity.class);
        startActivity(intent);
    }
    //点击跳转到设置页面
    public void Setting_page_click(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}

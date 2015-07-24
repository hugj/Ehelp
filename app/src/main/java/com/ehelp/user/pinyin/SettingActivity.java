package com.ehelp.user.pinyin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.ehelp.R;

public class SettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
    //点击跳转到账户与安全界面
    public void Accountsafe_page_click (View v) {
        Intent intent = new Intent(this, AccountsafeActivity.class);
        startActivity(intent);
    }
}

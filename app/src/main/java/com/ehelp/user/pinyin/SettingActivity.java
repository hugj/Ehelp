package com.ehelp.user.pinyin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.account.login;
import com.ehelp.home.Home;

public class SettingActivity extends ActionBarActivity implements OnChangedListener{
    //TOOLbar
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);

        SlipButton myBtn = (SlipButton)findViewById(R.id.accept_push_or_not);
        myBtn.SetOnChangedListener(this);

    }
    @Override
    public void OnChanged(boolean CheckState) {//当按钮状态被改变时
        // TODO Auto-generated method stub
        if(CheckState)
            Toast.makeText(this, "打开推送消息.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"关闭推送消息" , Toast.LENGTH_SHORT).show();
    }

    //点击跳转到账户与安全界面
    public void Accountsafe_page_click (View v) {
        Intent intent = new Intent(this, AccountsafeActivity.class);
        startActivity(intent);
    }
    public void exit_click (View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", -1);
        editor.commit();

        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        this.finish();
    }
}

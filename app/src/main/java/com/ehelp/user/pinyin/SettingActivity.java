package com.ehelp.user.pinyin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.account.login;

public class SettingActivity extends Activity implements OnChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //为按钮设置监听
        SlipButton myBtn =(SlipButton) findViewById(R.id.accept_push_or_not);//获得指定控件
        myBtn.SetOnChangedListener(this);//为控件设置监听器
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
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}

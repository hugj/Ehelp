package com.ehelp.user.pinyin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ehelp.R;

public class AccountsafeActivity extends ActionBarActivity {
    //TOOLbar
    private Toolbar mToolbar;
    private TextView Account;
    String account;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsafe);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("账户与安全");
        //显示当前用户的手机号
        Account = (TextView)findViewById(R.id.phone_number1);
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        account = sharedPref.getString("account", "");
        Account.setText(account);

    }
    public void change_password_page_click(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ChangepasswordActivity.class);
        startActivity(intent);
    }
    public void change_phone_page_click(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ChangephoneActivity.class);
        startActivity(intent);
    }
}

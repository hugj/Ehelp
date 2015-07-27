package com.ehelp.user.pinyin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.ehelp.R;

public class AccountsafeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsafe);
    }
    public void change_password_page_click(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ChangepasswordActivity.class);
        startActivity(intent);
    }
}

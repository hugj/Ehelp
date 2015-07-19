package com.ehelp.account;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.ehelp.R;

public class FindPassword extends TabActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.activity_find_password,
                tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("通过验证码找回")
                .setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("通过邮箱找回")
                .setContent(R.id.view2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("回答问题找回")
                .setContent(R.id.view3));


        //��ǩ�л��¼�����setOnTabChangedListener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {   //��һ����ǩ
                }
                if (tabId.equals("tab2")) {   //�ڶ�����ǩ
                }
                if (tabId.equals("tab3")) {   //��������ǩ
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void resetPassword(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }
}

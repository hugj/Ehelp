package com.ehelp.user.pinyin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

//import com.ehelp.R;

import com.ehelp.R;
import com.wangjie.androidinject.annotation.present.AIActionBarActivity;

import java.util.ArrayList;
import java.util.List;


public class ContactlistActivity extends ActionBarActivity {
    /** Called when the activity is first created. */

    private PinyinAdapter adapter;
    private ExpandableListView eListView;
    private AssortView assortView;
    private List<String> names;

    //TOOLbar
    private Toolbar mToolbar;

    public void getMessagePage(View view) {
        Intent intent = new Intent(this, messageActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("                          通讯录");
        //Button btn  = new Button(this);

        //mToolbar.addView(btn);
        setSupportActionBar(mToolbar);

        eListView = (ExpandableListView) findViewById(R.id.elist);
        assortView = (AssortView) findViewById(R.id.assort);
        names=new ArrayList<String>();
        names.add("lxz");
        names.add("ming");
        names.add("Ada");
        names.add("nini");
        names.add("dada");
        names.add("jingjing");
        names.add("lucy");
        names.add("er");
        names.add("L");
        names.add("xdsfsdggs");
        names.add("diao");
        names.add("nih");
        names.add("Java");
        names.add("gen");
        names.add("小米");
        names.add("qianqian");
        names.add("有道");
        names.add("明明");
        names.add("yes");
        names.add("jack");
        names.add("jackson");
        names.add("0小明");
        names.add(" ");
        adapter = new PinyinAdapter(this,names);
        eListView.setAdapter(adapter);

        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
            eListView.expandGroup(i);
        }
        assortView.setOnTouchAssortListener(new AssortView.OnTouchAssortListener() {
            View layoutView = LayoutInflater.from(ContactlistActivity.this)
                    .inflate(R.layout.alert_dialog_menu_layout, null);
            TextView text = (TextView) layoutView.findViewById(R.id.content);
            PopupWindow popupWindow;

            public void onTouchAssortListener(String str) {
                int index = adapter.getAssort().getHashList().indexOfKey(str);
                if (index != -1) {
                    eListView.setSelectedGroup(index);
                }
                if (popupWindow != null) {
                    text.setText(str);
                } else {
                    popupWindow = new PopupWindow(layoutView,
                            120, 120, false);
                    popupWindow.showAtLocation(getWindow().getDecorView(),
                            Gravity.CENTER, 0, 0);
                }
                text.setText(str);
            }

            public void onTouchAssortUP() {
                if (popupWindow != null)
                    popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
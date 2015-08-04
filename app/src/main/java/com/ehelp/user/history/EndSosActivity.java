package com.ehelp.user.history;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;

import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

public class EndSosActivity extends ActionBarActivity {
    //TOOLbar
    private Toolbar mToolbar;

    //上一个页面传入的参数
    private int event_id;//该事件ID

    //与后台联系的变量
    private String message;
    private String jsonStrng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_sos);
        //toolbar
        toolbar();
        //跟后台交互获取详情
        Intent intent = getIntent();
        event_id = intent.getIntExtra("event_id",-1);//intent时传入的事件id
        showdetail();
        showhelper(1);
        showhelper(2);

    }
    /*
    toolbar设置
     */
    private void toolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("求救信息详情");
    }
    /*
    显示事件详情
     */
    private void showdetail(){
        if(event_id != -1){
            jsonStrng = "{" +
                    "\"event_id\":" + event_id + "\"type\":"+2+"}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/get_supporter", jsonStrng);



        }else {
            Toast.makeText(getApplicationContext(), "无此事件",
                    Toast.LENGTH_SHORT).show();
        }

    }
    /*
    显示帮客和关注者
     */
    private void showhelper(int ty){
        if(event_id != -1){
            jsonStrng = "{" +
                    "\"event_id\":" + event_id + "\"type\":"+ty+"}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/get_supporter", jsonStrng);



        }else {
            Toast.makeText(getApplicationContext(), "无此事件",
                    Toast.LENGTH_SHORT).show();
        }
    }




}

package com.ehelp.user.pinyin;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;


public class AddFriendActivity extends ActionBarActivity {

    //toolbar
    private Toolbar mToolbar;
    String phone="";//手机号码
    private String message;
    private String jsonStrng;
    private int idd;
    private SharedPreferences SharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("添加好友");

        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);


        //return str1;
    }
    /*
    查询按钮监听事件：从后台获取用户信息放至该页面，
     */
//public void chaxun(View v){}
    public void chaxun(View v){
        EditText editText1 =(EditText)findViewById(R.id.editText_comment);
        phone=editText1.getText().toString();
        if(!phone.isEmpty()) {
            jsonStrng = "{" +
                    "\"phone\":\"" + phone + "\"}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/get_information", jsonStrng);
            Log.v("addfatest", message);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            try{
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "该用户未注册",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Toast.makeText(getApplicationContext(), "萌萌哒",
                            Toast.LENGTH_SHORT).show();
                    //LinearLayout tvv =(LinearLayout) findViewById(R.id.parently);
                    RelativeLayout rlll = (RelativeLayout) findViewById(R.id.rll);
                    rlll.setVisibility(View.VISIBLE);
                    //tvv.addView(rlll);
                    //修改显示的名字
                    TextView tv =(TextView) findViewById(R.id.name);
                    if(jO.getString("nickname") !="") {
                        tv.setText(jO.getString("nickname"));
                    }else {
                        tv.setText(jO.getString("name"));
                    }
                    //修改显示的年龄
                    TextView tv1 =(TextView) findViewById(R.id.age);
                    tv1.setText("年龄："+jO.getInt("age"));
                    //修改显示的性别
                    TextView tv2 =(TextView) findViewById(R.id.gender);
                    if(jO.getInt("gender")==1) {
                        tv2.setText("性别：男");
                    }
                    idd = jO.getInt("id");

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "手机号不能为空",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void addfriend(View v){
        //int id =0;//让这个id等于当前用户的id就好了。
        int id = SharedPref.getInt("user_id", -1);

        jsonStrng = "{" +
                "\"id\":" + id + "," +
                "\"user_id\":" + idd + "," +
                "\"operation\":" + 1 + "," +
                "\"type\":" + 2 + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonStrng);
        if (message == "false") {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //未获取到
                        Toast.makeText(getApplicationContext(), "添加失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                return;
            }else {
                //当获取到
                Toast.makeText(getApplicationContext(), "添加成功",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void detail(View v){
        Intent intent = new Intent(this, messageActivity.class);
        intent.putExtra("type",0);//1表示非好友
        intent.putExtra("id",idd);
        startActivity(intent);
    }
}

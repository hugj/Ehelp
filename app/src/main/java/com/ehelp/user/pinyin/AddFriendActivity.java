package com.ehelp.user.pinyin;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    //用于后台数据交互
    private String message;
    private String jsonStrng;
    //用于获取当前登录id
    private SharedPreferences SharedPref;

    private int idd;//查询的用户ID
    String phone="";//要查询的手机号码


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
        //toolbar设置结束

        //获取当前登录用户id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
    }

    /*
    查询按钮监听事件：从后台获取用户信息放至该页面，
     */
    public void chaxun(View v){
        EditText editText1 =(EditText)findViewById(R.id.editText_comment);
        phone=editText1.getText().toString();

        if(!phone.isEmpty()) {//当输入的手机号不为空
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
                    return;
                }else {
                    Toast.makeText(getApplicationContext(), "查询成功",
                            Toast.LENGTH_SHORT).show();
                    //使隐藏的页卡显示
                    RelativeLayout rlll = (RelativeLayout) findViewById(R.id.rll);
                    rlll.setVisibility(View.VISIBLE);

                    //修改页卡中显示的名字
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
                    //获取查询的用户的id
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
        //这个id等于当前登录用户的id
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
                        //返回500
                        Toast.makeText(getApplicationContext(), "添加失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else {
                //返回200
                Toast.makeText(getApplicationContext(), "添加成功",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void detail(View v){
        Intent intent = new Intent(this, messageActivity.class);
        intent.putExtra("type",0);//0表示非好友1表示好友2表示紧急联系人
        intent.putExtra("id",idd);
        startActivity(intent);
    }
}

package com.ehelp.user.pinyin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendActivity extends ActionBarActivity {

    //toolbar
    private Toolbar mToolbar;
    String phone="";//手机号码
    private String message;
    private String jsonStrng;


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
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "手机号不能为空",
                    Toast.LENGTH_SHORT).show();
        }


    }

}

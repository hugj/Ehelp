package com.ehelp.user.lovebank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class BankActivity extends ActionBarActivity {
    private TextView name, phone, integral, coin;
    private ImageView level;
    private int id, integral_, coin_;
    private String jsonString, message, name_, phone_;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        setmes();
    }
    public void setmes(){
        //爱心银行首页的信息
        name = (TextView)findViewById(R.id.bank_name);
        phone = (TextView)findViewById(R.id.bank_phone);
        integral = (TextView)findViewById(R.id.bank_integral1);
        coin = (TextView)findViewById(R.id.bank_coin1);
        level = (ImageView)findViewById(R.id.bank_level);
        //后台线程
        new Thread(runnable).start();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setmes_help();
        }
    };
    //获取用户爱心银行信息
    private void setmes_help() {
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);
        jsonString = "{" +
                "\"id\":" + id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonString);
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
        try {
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取用户信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            name_ = jO.getString("name");
            phone_ = jO.getString("phone");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        name.setText(name_);
                        phone.setText(phone_);
                }
            });
            jsonString = "{" +
                    "\"user_id\":" + id + "}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/lovingbank", jsonString);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取爱心银行信息失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取爱心银行信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                integral_ = jO.getInt("score");
                coin_ = jO.getInt("love_coin");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            coin.setText(String.valueOf(coin_));
                            integral.setText(String.valueOf(integral_));
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void my_integral(View view){
        Intent intent = new Intent(this, IntegralActivity.class);
        startActivity(intent);
    }
    public void my_coin(View view){
        Intent intent = new Intent(this, CoinActivity.class);
        startActivity(intent);
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}

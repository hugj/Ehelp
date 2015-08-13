package com.ehelp.user.lovebank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

/*
 *爱心银行积分管理-兑换爱心币
 */
public class IntegralActivity extends ActionBarActivity {
    private TextView integral_num, num_;
    private Button btn;
    private EditText coin;
    private String str;
    private int n, edit_n, inte_n;//兑换数量,积分数
    private String message, jsonStrng;
    private String edit_string;
    private int id;
    //用于获取当前登录id
    private SharedPreferences SharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral);
        Intent intent = getIntent();
        str = intent.getStringExtra("integral");
        integral_num = (TextView)findViewById(R.id.integral_num);
        integral_num.setText(str); //积分数
        //获取当前登录用户id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        id = SharedPref.getInt("user_id", -1);
    }
    //点击显示兑换输入框
   public void change_integral(View view) {
       LinearLayout change_inte = (LinearLayout)findViewById(R.id.change1);
       change_inte.setVisibility(View.VISIBLE);
       num_ = (TextView)findViewById(R.id.num);
       //可兑换爱心币的数量
       n = (Integer.parseInt(str)) / 100;
       String s = String.valueOf(n);
       num_.setText(s);
   }
    //兑换爱心币
    public void change_coin(View view) {
        coin = (EditText) findViewById(R.id.change3);
        edit_string = coin.getText().toString();
        if (edit_string == "") {
            Toast.makeText(getApplicationContext(), "输入数量不能为空，请重新输入"
                    , Toast.LENGTH_LONG).show();
            return;
        } else {
            edit_n = Integer.parseInt(edit_string);//获取输入爱心币个数
            btn = (Button) findViewById(R.id.btn);
            inte_n = edit_n * 100; //兑换所需的积分数
            if (edit_n > n) {
                Toast.makeText(getApplicationContext(), "输入数量大于可兑换数量，请重新输入"
                        , Toast.LENGTH_LONG).show();
            } else {
                jsonStrng = "{" +
                        "\"id\":" + id + "," +
                        "\"operation\":" + 2 + "," +
                        "\"score\":" + inte_n + "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/user/bank_manage", jsonStrng); //修改数据
                if (message == "false") {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "连接失败，请检查网络是否连接并重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                try {
                    JSONObject j = new JSONObject(message);
                    if (j.getInt("status") == 500) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "兑换失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "兑换成功", Toast.LENGTH_SHORT).show();
                        String s = Integer.toString(j.getInt("score"));
                        integral_num.setText(s);//更改兑换后的积分数
                        String emp = String.valueOf(n - edit_n);
                        num_.setText(emp);
                        Intent intent = new Intent(this, BankActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

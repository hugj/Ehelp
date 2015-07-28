package com.ehelp.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class SignUp extends ActionBarActivity implements OnClickListener {

    private Toolbar mToolbar;
    private EditText Eaccount;
    private EditText inputCodeEt;
    private Button requestCodeBtn;
    private Button commitBtn;
    private String phoneNums;

    public final static String EXTRA_MESSAGE = "com.ehelp.MESSAGE";

    int i = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Intent intent = getIntent();
        setContentView(R.layout.activity_sign_up);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("注册新用户");
        setSupportActionBar(mToolbar);
        init();
    }

    private void init() {
        // 使用后台线程运行网络连接功能
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //set toolbar

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);//开启up button这个默认值是true
        //getSupportActionBar().setHomeButtonEnabled(true);//
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        inputCodeEt = (EditText) findViewById(R.id.edit_identify);//验证码
        requestCodeBtn = (Button) findViewById(R.id.request_code_btn);
        commitBtn = (Button) findViewById(R.id.commit_btn);
        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        requestCodeBtn.setBackgroundColor(Color.argb(102, 51, 181, 229));
        commitBtn.setClickable(false);
        commitBtn.setTextColor(Color.GRAY);

        // 启动短信验证sdk
        SMSSDK.initSDK(this, "8e8e104c2348", "a5a3c12216e6df81397844b0a73fb2db");
        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        Eaccount = (EditText)findViewById(R.id.edit_phoneNum);
        int a = 1;
        phoneNums = Eaccount.getText().toString();
        a = 2;
        switch (v.getId()) {
            case R.id.request_code_btn:
                String jsonStrng = "{" +
                        "\"account\":\"" + phoneNums + "\"," +
                        "\"password\":\"\"" +  "}";
                //String jsonStrng = "";
                String message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/account/login", jsonStrng);
                if (message == "false") {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jO = new JSONObject(message);
                    if (jO.getInt("status") == 500) {
                        // 1. 通过规则判断手机号
                        if (!judgePhoneNums(phoneNums)) {
                            return;
                        } // 2. 通过sdk发送短信验证
                        SMSSDK.getVerificationCode("86", phoneNums);

                        // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                        requestCodeBtn.setClickable(false);
                        requestCodeBtn.setBackgroundColor(Color.WHITE);
                        requestCodeBtn.setTextColor(Color.GRAY);
                        requestCodeBtn.setText("重新发送(" + i + ")");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (; i > 0; i--) {
                                    handler.sendEmptyMessage(-9);
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(-8);
                            }
                        }).start();
                    } else {
                        Toast.makeText(getApplicationContext(), "该用户已注册",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.commit_btn:
                SMSSDK.submitVerificationCode("86", phoneNums, inputCodeEt
                        .getText().toString());
                break;
        }
    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                requestCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                requestCodeBtn.setBackgroundColor(Color.argb(102, 51, 181, 229));
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, RegisterPassword.class);
                        intent.putExtra(EXTRA_MESSAGE, phoneNums);
                        startActivity(intent);
                        SignUp.this.finish();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        commitBtn.setClickable(true);
                        commitBtn.setBackgroundColor(Color.argb(102, 51, 181, 229));
                        commitBtn.setTextColor(Color.BLACK);

                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else {
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");
                        if (!TextUtils.isEmpty(des)) {
                            Toast.makeText(getApplicationContext(), des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        SMSLog.getInstance().w(e);
                    }
                }
            }
        }
    };


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
/*
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
    */
}

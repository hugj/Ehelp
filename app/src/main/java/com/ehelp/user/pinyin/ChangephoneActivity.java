package com.ehelp.user.pinyin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class ChangephoneActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText Input_verification_code;
    private Button Sent_verification_code;
    private Button btn_verification;
    private String PhoneNumber;
    private EditText Input_phone_number;


    public final static String EXTRA_MESSAGE = "com.ehelp.MESSAGE";

    int i = 60;
    //TOOLbar
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changephone);
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("更改绑定账号");
        
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
        //输入验证码&发送验证码&验证按钮
        Input_verification_code = (EditText)findViewById(R.id.accountsafe_change_phone8);
        Sent_verification_code = (Button)findViewById(R.id.accountsafe_change_phone6);
        btn_verification = (Button) findViewById(R.id.accountsafe_change_phone9);
        Sent_verification_code.setOnClickListener(this);
        btn_verification.setOnClickListener(this);

        // 启动短信验证sdk
        SMSSDK.initSDK(this, "8e8e104c2348", "a5a3c12216e6df81397844b0a73fb2db");
        EventHandler eventHandler = new EventHandler() {
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
        Input_phone_number = (EditText)findViewById(R.id.accountsafe_change_phone5);
        int a = 1;
        PhoneNumber = Input_phone_number.getText().toString();
        a = 2;
        switch (v.getId()) {
            case R.id.accountsafe_change_phone6:
                String jsonStrng = "{" +
                        "\"account\":\"" + PhoneNumber + "\"," +
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
                        if (!judgePhoneNums(PhoneNumber)) {
                            return;
                        } // 2. 通过sdk发送短信验证
                        SMSSDK.getVerificationCode("86", PhoneNumber);

                        // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                        Sent_verification_code.setClickable(false);
                        Sent_verification_code.setBackgroundColor(Color.WHITE);
                        Sent_verification_code.setTextColor(Color.GRAY);
                        Sent_verification_code.setText("重新发送(" + i + ")");
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
                        Toast.makeText(getApplicationContext(), "该号码已被绑定",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.accountsafe_change_phone9:
                SMSSDK.submitVerificationCode("86", PhoneNumber, Input_verification_code
                        .getText().toString());
                break;
        }
    }
    //handler
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                Sent_verification_code.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                Sent_verification_code.setClickable(true);
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
//                        //更改手机号
//                        TextView phonenum = (TextView)findViewById(R.id.phone_number);
//                        phonenum.setText(PhoneNumber);
                        //跳转到原页面
                        Intent intent = new Intent(ChangephoneActivity.this, AccountsafeActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, PhoneNumber);
                        startActivity(intent);
                        ChangephoneActivity.this.finish();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        btn_verification.setClickable(true);
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
    //判断手机号码是否合理
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }
    //判断字符串位数
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }
    //验证手机格式
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }

}

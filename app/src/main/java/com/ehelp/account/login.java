package com.ehelp.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.home.Home;
import com.ehelp.utils.MD5;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class login extends ActionBarActivity {

    private Toolbar mToolbar;
    private EditText Epassword;
    private EditText Eaccount;
    private String password;
    private String account;
    private String jsonStrng;
    private String message;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 查询user_id信息，如已登陆，则user_id存在，跳过登陆直接进入主页
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        int default_ = -1;
        int id;
        id = sharedPref.getInt("user_id", default_);
        if (id != -1) {
            Intent it = new Intent(this, Home.class);
            startActivity(it);
            login.this.finish();
        }
        init();
    }

    // 完成初始化
    private void init() {
        //set toolbar
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle("登录");
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
/*登录页右上角无需按钮
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
    public void signUp(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void findPassword(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, FindPassword.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        // Do something in response to button
        Eaccount = (EditText)findViewById(R.id.edit_phoneNum);
        Epassword = (EditText)findViewById(R.id.edit_password);
        account = Eaccount.getText().toString();
        password = Epassword.getText().toString();
        // 在后台线程操作用户登录验证
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            login_help();
        }
    };

    // 用户验证过程
    private void login_help() {
        if ((!account.isEmpty()) &&(!password.isEmpty())) {
            jsonStrng = "{" +
                    "\"account\":\"" + account + "\"," +
                    "\"password\":\"\"" +  "}";

            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/account/login", jsonStrng);
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
            String salt;
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "用户未注册",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }
                salt = jO.getString("salt");
                String password2 = MD5.MD5_encode(MD5.MD5_encode(password, ""), salt);
                jsonStrng = "{" +
                        "\"account\":\"" + account + "\"," +
                        "\"password\":\"" + password2 + "\"," +
                        "\"salt\":\"" + salt + "\" " +  "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/account/login", jsonStrng);
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
                jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "密码错误，登录失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    final int user_id = jO.getInt("id");
                    String jsonStrng = "{" +
                            "\"id\":" + user_id + "}";
                    final String message = RequestHandler.sendPostRequest(
                            "http://120.24.208.130:1501/user/get_information", jsonStrng);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "登录成功, 用户id:" + user_id,
                                    Toast.LENGTH_SHORT).show();
                            // 将用户信息存储，以便之后无需登录直接进入主页

                            if (message == "false") {
                                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                        Toast.LENGTH_SHORT).show();
                            }   else {
                                try {
                                    JSONObject jO = new JSONObject(message);
                                    String nickname = jO.getString("nickname");
                                    if (nickname.isEmpty()) {
                                        nickname = account;
                                    }
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt("user_id", user_id);
                                    editor.putString("account", account);
                                    editor.putString("nickname", nickname);
                                    editor.commit();

                                    Intent it = new Intent(login.this, Home.class);
                                    startActivity(it);
                                    login.this.finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

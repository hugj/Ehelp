package com.ehelp.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.server.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

//import android.widget.Toolbar;


public class login extends ActionBarActivity {

    private Toolbar mToolbar;
    private EditText Epassword;
    private EditText Eaccount;
    private String password;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_login);
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("登录");
        setSupportActionBar(mToolbar);
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
        if ((!account.isEmpty()) &&(!password.isEmpty())) {
            String jsonStrng = "{" +
                    "\"account\":\"" + account + "\"," +
                    "\"password\":\"\"" +  "}";
            //String jsonStrng = "";
            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/account/login", jsonStrng);
            if (message == "false") {
                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String salt;
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(), "用户未注册",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                salt = jO.getString("salt");
                String password2 = MD5.MD5_encode(password, salt);
                jsonStrng = "{" +
                        "\"account\":\"" + account + "\"," +
                        "\"password\":\"" + password2 + "\"," +
                        "\"salt\":\"" + salt + "\" " +  "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/account/login", jsonStrng);
                if (message == "false") {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(), "密码错误，登录失败",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    String user_id = jO.getString("id");
                    Toast.makeText(getApplicationContext(), "登录成功, 用户id:" + user_id,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "用户名或密码不能为空",
                    Toast.LENGTH_SHORT).show();
        }
    }

}

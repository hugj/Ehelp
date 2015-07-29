package com.ehelp.account;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.MD5;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterPassword extends ActionBarActivity {
    private Toolbar mToolbar;
    private EditText Epassword;
    private EditText Epassword2;
    private String password;
    private String password2;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
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

        // Get the message from the intent
        Intent intent = getIntent();
        account = intent.getStringExtra(FindPassword.EXTRA_MESSAGE);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("注册新用户");
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_password, menu);
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
        Epassword = (EditText)findViewById(R.id.edit_password);
        Epassword2 = (EditText)findViewById(R.id.edit_password2);
        password = Epassword.getText().toString();
        password2 = Epassword2.getText().toString();
        if ((!account.isEmpty()) && (!password.isEmpty()) && (!password2.isEmpty())) {
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "密码应不少于6位",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(password2)) {
                Toast.makeText(getApplicationContext(), "密码不一致",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String password2 = MD5.MD5_encode(password, "");
            String jsonStrng = "{" +
                    "\"account\":\"" + account + "\"," +
                    "\"password\":\"" + password2 + "\"" + "}";
            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/account/regist", jsonStrng);
            String status;
            try {
                JSONObject jO = new JSONObject(message);
                status = jO.getString("status");
                if (status.equals("200")) {
                    Toast.makeText(getApplicationContext(), "注册成功",
                            Toast.LENGTH_SHORT).show();
                    // 由于之前的页面除了login之外全都被销毁，所以这里只需要销毁该页面就可跳到login
                    RegisterPassword.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "注册失败",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "密码不能为空",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

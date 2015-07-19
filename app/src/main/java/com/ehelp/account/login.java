package com.ehelp.account;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.server.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;


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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
// toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("登录");// �������������setSupportActionBar֮ǰ����Ȼ����Ч
// toolbar.setSubtitle("������");
        setSupportActionBar(mToolbar);
/* ��Щͨ��ActionBar������Ҳ��һ���ģ�ע��Ҫ��setSupportActionBar(toolbar);֮�󣬲�Ȼ�ͱ����� */
// getSupportActionBar().setTitle("����");
// getSupportActionBar().setSubtitle("������");
// getSupportActionBar().setLogo(R.drawable.ic_launcher);

/* �˵��ļ���������toolbar�����ã�Ҳ������ActionBar������ͨ��Activity��onOptionsItemSelected
�ص�����������
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(activity_login.this, "action_settings", 0).show();
                        break;
                    case R.id.action_share:
                        Toast.makeText(MainActivity.this, "action_share", 0).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });*/
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
                    "\"account\": \" " + account + "\", " +
                    "\"password\": \"\", " +  "}";
            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/account/login", jsonStrng);
            String salt;
            try {
                JSONObject jO = new JSONObject(message);
                salt = jO.getString("salt");
                jsonStrng = "{" +
                        "\"account\": \" " + account + "\", " +
                        "\"password\": \" " + password + "\", " +
                        "\"salt\": \" " + salt + "\", " +  "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:8888/account/login", jsonStrng);
                if (message == "false") {
                    Toast.makeText(getApplicationContext(), "登录失败",
                            Toast.LENGTH_SHORT).show();
                    System.out.println("登录失败");
                }
                else {
                    Toast.makeText(getApplicationContext(), "登录成功",
                            Toast.LENGTH_SHORT).show();
                    System.out.println("登陆成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "用户名或密码不能为空",
                    Toast.LENGTH_SHORT).show();
            System.out.println("用户名或密码不能为空");
        }
    }

}

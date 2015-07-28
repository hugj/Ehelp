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
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.MD5;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPassword extends ActionBarActivity {

    private Toolbar mToolbar;
    private EditText Epassword;
    private EditText Epassword2;
    private String password;
    private String password2;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
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
       // mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("重置密码");
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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
    public void resetPassword(View view) {
        // Do something in response to button
        Epassword = (EditText)findViewById(R.id.edit_password);
        password = Epassword.getText().toString();
        Epassword2 = (EditText)findViewById(R.id.edit_password2);
        password2 = Epassword2.getText().toString();
        // 在后台线程上进行联网验证
        new Thread(runnable).start();
    }

    private void resetPassword_help() {
        if ((!password2.isEmpty()) &&(!password.isEmpty())) {
            if (password.length() < 6) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "密码应不少于6位",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            if (!password.equals(password2)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "密码不一致",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            String jsonStrng = "{" +
                    "\"account\":\"" + account + "\"," +
                    "\"password\":\"\"" +  "}";
            String message = RequestHandler.sendPostRequest(
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
                            Toast.makeText(getApplicationContext(), "用户名未注册",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                salt = jO.getString("salt");
                String password2 = MD5.MD5_encode(MD5.MD5_encode(password, ""), salt);
                jsonStrng = "{" +
                        "\"account\":\"" + account + "\"," +
                        "\"password\":\"" + password2 + "\"," +
                        "\"salt\":\"" + salt + "\" " +  "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/account/modify_password", jsonStrng);
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
                            Toast.makeText(getApplicationContext(), "提交密码与当前密码相同，修改失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "修改成功,请重新登录",
                                    Toast.LENGTH_SHORT).show();
                            // 销毁该页面，即可返回到login界面
                            ResetPassword.this.finish();
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
                    Toast.makeText(getApplicationContext(), "新密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            resetPassword_help();
        }
    };

}

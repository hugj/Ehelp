package com.ehelp.user.pinyin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.MD5;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangepasswordActivity extends ActionBarActivity {
    //TOOLbar
    private Toolbar mToolbar;
    private TextView Account;
    private EditText Password;
    private EditText New_password1;
    private EditText New_password2;
    private String jsonString;
    private String message;
    private String account, password, new_password1, new_password2;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("修改密码");
        //显示用户的手机号
        Account = (TextView)findViewById(R.id.accountsafe_change_password3);
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        account = sharedPref.getString("account", "");
        Account.setText(account);
    }
    public void check_password(View v) {
        //把文本框和编辑框的字符串传到变量里
        Password = (EditText)findViewById(R.id.accountsafe_change_password5);
        New_password1 = (EditText)findViewById(R.id.accountsafe_change_password7);
        New_password2 = (EditText)findViewById(R.id.accountsafe_change_password9);
        password = Password.getText().toString();
        new_password1 = New_password1.getText().toString();
        new_password2 = New_password2.getText().toString();
        // 创建新的线程并开启它，在后台线程操作用户验证，验证密码是否正确,并修改密码
        new Thread(runnable).start();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //执行验证
            verify();
        }
    };
   //验证过程,利用登陆接口获取盐值
   private void verify() {
       if ((!account.isEmpty()) &&(!password.isEmpty())) {
           jsonString = "{" +
                   "\"account\":\"" + account + "\"," +
                   "\"password\":\"\"" +  "}";
           message = RequestHandler.sendPostRequest(
                   "http://120.24.208.130:1501/account/login", jsonString);
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
                   return;
               }
               salt = jO.getString("salt"); //密码加密的盐值
               //MD5加密
               String password2 = MD5.MD5_encode(MD5.MD5_encode(password, ""), salt);
               jsonString = "{" +
                       "\"account\":\"" + account + "\"," +
                       "\"password\":\"" + password2 + "\"," +
                       "\"salt\":\"" + salt + "\" " +  "}";
               message = RequestHandler.sendPostRequest(
                       "http://120.24.208.130:1501/account/login", jsonString);
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
                           Toast.makeText(getApplicationContext(), "密码错误，请重新输入密码",
                                   Toast.LENGTH_SHORT).show();
                       }
                   });
               }
               else {

                   if ((new_password1.isEmpty()) || (new_password2.isEmpty())) {
                       Toast.makeText(getApplicationContext(), "新输入密码不能为空",
                               Toast.LENGTH_SHORT).show();
                       return;
                   }
                   //判断两次密码是否一致
                   if (new_password1.compareTo(new_password2)!= 0) {
                       Toast.makeText(getApplicationContext(), "两次密码输入不一致，请重新输入",
                               Toast.LENGTH_SHORT).show();
                       return;
                   } else {
                       //密码长度要求
                       if ((new_password1.length()>40)||(new_password1.length()<6)) {
                           Toast.makeText(getApplicationContext(), "密码长度应为6-40位",
                                   Toast.LENGTH_SHORT).show();
                           return;
                       }
                       //修改用户的密码
                       String salt1;
                       salt1 = jO.getString("salt"); //密码加密的盐值
                       //MD5加密
                       String new_password = MD5.MD5_encode(MD5.MD5_encode(new_password1, ""), salt1);
                       jsonString = "{" +
                               "\"account\":\"" + account + "\"," +
                               "\"password\":\"" + new_password + "\"," +
                               "\"salt\":\"" + salt + "\" " +  "}";
                       message = RequestHandler.sendPostRequest(
                               "http://120.24.208.130:1501/account/modify_password", jsonString);
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
                                   return;
                               }
                           });

                       }
                       else {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "修改成功,请重新登录",
                                           Toast.LENGTH_SHORT).show();
                                   // 销毁该页面
                                   ChangepasswordActivity.this.finish();
                               }
                           });
                       }
                   }
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       } else {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(getApplicationContext(), "密码不能为空",
                           Toast.LENGTH_SHORT).show();
                   return;
               }
           });
       }
   }

}

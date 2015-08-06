package com.ehelp.user.contactlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class messageActivity extends ActionBarActivity {

    private RelativeLayout myLay = null;
    private AlertDialog myDialog = null;

    //TOOLbar
    private Toolbar mToolbar;
    private TextView Setname;

    private int type = 0;//传进来的参数，用户的类型，0非好友，1好友，2紧急联系人
    private int idd;//要查看的用户的id
    private int id;//当前用户id

//与后台联系的变量
    private String message;
    private String jsonStrng;
    private SharedPreferences SharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("详细资料");

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);//默认0非好友。1表示好友，2表示紧急联系人
        idd = intent.getIntExtra("id",-1);//intent时传入的id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        id = SharedPref.getInt("user_id", -1);

        //根据用户类型初始化页面
        init();

        //根据用户ID从后台获取数据显示信息详情
        show123();

//        //click on set name设置备注名
//        myLay = (RelativeLayout) findViewById(R.id.detail_setname);
//        Setname = (TextView)findViewById(R.id.detail_setname2);
//        myLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                myDialog = new AlertDialog.Builder(messageActivity.this).create();
//                myDialog.show();
//                myDialog.getWindow().setContentView(R.layout.activity_messetname);
//                //click on cancel
//                myDialog.getWindow()
//                        .findViewById(R.id.setname5)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                myDialog.dismiss();
//                            }
//                        });
//                //click on ensure
//                myDialog.getWindow()
//                        .findViewById(R.id.setname6)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                EditText edit = (EditText)myDialog.getWindow()
//                                        .findViewById(R.id.setname4);
//                                Setname.setText(edit.getText().toString());
//                                Toast.makeText(getApplicationContext(), "备注名设置成功",
//                                        Toast.LENGTH_SHORT).show();
//                                myDialog.dismiss();
//                            }
//                        });
//                myDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//                myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
//                        .SOFT_INPUT_STATE_VISIBLE);
//            }
//        });
        //add contact添加紧急联系人
        Button add_excontact = (Button)findViewById(R.id.addexcontact);
        add_excontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageActivity.this.add_dialog();
            }
        });
        //delete contact 删除好友
        Button Delete_contact = (Button)findViewById(R.id.delete_contact);
        Delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageActivity.this.delete_dialog();
            }
        });
    }

    protected void init(){
        if(type == 0){
            Button btn = (Button)findViewById(R.id.addexcontact);
            btn.setVisibility(View.INVISIBLE);
            Button btn2 = (Button)findViewById(R.id.delete_contact);
            btn2.setText("添加为好友");
        }else if(type == 1){

        }else if(type == 2){
            Button btn = (Button)findViewById(R.id.addexcontact);
            btn.setText("删除紧急联系人");
            Button btn2 = (Button)findViewById(R.id.delete_contact);
            btn2.setVisibility(View.INVISIBLE);
        }else {
            Toast.makeText(getApplicationContext(), "参数传入错误",
                    Toast.LENGTH_SHORT).show();
        }

    }

    protected void show123(){
        if(idd != -1){
            jsonStrng = "{" +
                    "\"id\":" + idd + "}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/get_information", jsonStrng);
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
            try{
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "查询数据库错误",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Toast.makeText(getApplicationContext(), "查询数据库成功",
                            Toast.LENGTH_SHORT).show();
                    //修改显示的用户名
                    TextView tv =(TextView) findViewById(R.id.detail_name2);
                    String emp,emp_;
                    emp = jO.getString("nickname");
                    emp_ = jO.getString("name");
                    if(emp.equals("")) {
                        tv.setText(emp_);
                    } else {
                        tv.setText(emp);
                    }
                    //修改显示的手机号
                    TextView tv1 =(TextView) findViewById(R.id.detail_phone2);
                    tv1.setText(jO.getString("phone"));
                    //修改显示的备注姓名
//                    TextView tv2 =(TextView) findViewById(R.id.detail_setname2);
//                    tv2.setText("");
                    //显示个人职业
                    TextView tv2 = (TextView)findViewById(R.id.detail_job2);
                    int i = jO.getInt("occupation");
                    switch (i) {
                        case 0:
                            tv2.setText(R.string.mes_job1);
                            break;
                        case 1:
                            tv2.setText(R.string.mes_job2);
                            break;
                        case 2:
                            tv2.setText(R.string.mes_job3);
                            break;
                        case 3:
                            tv2.setText(R.string.mes_job4);
                            break;
                        case 4:
                            tv2.setText(R.string.mes_job5);
                            break;
                        case 5:
                            tv2.setText(R.string.mes_job6);
                            break;
                    }
                    //修改显示的性别
                    TextView tv3 =(TextView) findViewById(R.id.detail_file2);
                    if(jO.getInt("gender")==1) {
                        tv3.setText("男");
                    }
                    //修改显示的所在地
                    TextView tv4 =(TextView) findViewById(R.id.detail_loacl2);
                    tv4.setText(jO.getString("location"));
                    //修改显示的年龄
                    TextView tv5 =(TextView) findViewById(R.id.detail_age2);
                    tv5.setText(String.valueOf(jO.getInt("age")));
                    return;
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "无此用户",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //add ex contact
    protected void add_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(type == 2){
            builder.setMessage("是否删除紧急联系人（保留好友）？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(0,0);
                    manage_relation(1,2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",1);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();


                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else {
            builder.setMessage("是否添加为紧急联系人？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(1, 0);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",2);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }
    //删除好友
    protected void delete_dialog() {
        AlertDialog.Builder delete = new AlertDialog.Builder(this);
        if(type == 0){
            delete.setMessage("确定添加为好友？");
            delete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(1, 2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",1);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            delete.create().show();
        }else {
            delete.setMessage("确定删除此好友？");
            delete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(0, 2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",0);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            delete.create().show();
        }

    }


    public void manage_relation(int a,int b){
        //a代表0删除关系或1添加关系
        //b代表0紧急联系人操作2好友操作
        int id = SharedPref.getInt("user_id", -1);

        jsonStrng = "{" +
                "\"id\":" + id + "," +
                "\"user_id\":" + idd + "," +
                "\"operation\":" + a + "," +
                "\"type\":" + b + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonStrng);
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
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //返回500
                        Toast.makeText(getApplicationContext(), "操作失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else {
                //返回200
                Toast.makeText(getApplicationContext(), "操作成功",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

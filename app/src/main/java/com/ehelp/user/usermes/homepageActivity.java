package com.ehelp.user.usermes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.RequestHandler;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.androidinject.annotation.present.AIActionBarActivity;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@AILayout(R.layout.activity_homepage)
public class homepageActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    private AlertDialog EditnameDialog = null;
    private AlertDialog EditnicknameDialog = null;
    private AlertDialog EditlocationDialog = null;
    private AlertDialog EditageDialog = null;
    private final static int DATE_DIALOG = 0;
    private TextView edt = null;
    private RelativeLayout brithday_edit = null;
    private Calendar c = null;

    //TOOLbar
    private Toolbar mToolbar;
    //用户名&&昵称&&手机号&&性别&&年龄的获取
    private TextView Username, Nickname;
    private TextView Phonenum, Location;
    private TextView Gender, Job;
    private TextView Age;
    private String emp,jsonString, message;
    private int user_id;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Username = (TextView)findViewById(R.id.single_name2);
        Nickname = (TextView)findViewById(R.id.nickname2);
        Phonenum = (TextView)findViewById(R.id.single_phone2);
        Gender = (TextView)findViewById(R.id.single_file2);
        Job = (TextView)findViewById(R.id.single_job2);
        Location = (TextView)findViewById(R.id.single_loacl2);
        Age = (TextView)findViewById(R.id.single_age2);
        //获取本地的user_id通过其来获得用户信息
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
        jsonString = "{" +
                "\"id\":" + user_id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonString);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            JSONObject j = new JSONObject(message);
            if (j.getInt("status") == 500){
                Toast.makeText(getApplicationContext(), "用户未登陆",
                        Toast.LENGTH_SHORT).show();
            } else {
                //显示当前用户名
                Username.setText(j.getString("name"));
                //显示当前昵称
                Nickname.setText(j.getString("nickname"));
                //显示当前用户的手机号
                Phonenum.setText(j.getString("phone"));
                //显示当前用户性别
                int i = j.getInt("gender");
                if (i == 1) {
                    Gender.setText("男");
                } else {
                    Gender.setText("女");
                }
                int z = j.getInt("occupation");
                switch (z) {
                    case 0:
                        Job.setText(R.string.mes_job1);
                        break;
                    case 1:
                        Job.setText(R.string.mes_job2);
                        break;
                    case 2:
                        Job.setText(R.string.mes_job3);
                        break;
                    case 3:
                        Job.setText(R.string.mes_job4);
                        break;
                    case 4:
                        Job.setText(R.string.mes_job5);
                        break;
                    case 5:
                        Job.setText(R.string.mes_job6);
                        break;
                }
                //显示当前所在地
                Location.setText(j.getString("location"));
                //显示年龄
                Age.setText(String.valueOf(j.getInt("age")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        init();
    }
    private void init() {
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("个人信息");
       //用户信息的编辑
        editorInfo();
        //set FAB
        fab();
    }
    private void fab(){
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("求救")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xffd84315)
                        .setIconPressedColor(0xffbf360c)
                        .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("求助")
//                        .setResId(R.mipmap.ico_test_c)
                        .setDrawable(getResources().getDrawable(R.mipmap.ic_launcher))
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(context, 4)))
                        .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("提问")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xff056f00)
                        .setIconPressedColor(0xff0d5302)
                        .setLabelColor(0xff056f00)
                        .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(context, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(context, 5))
        ;

        rfabHelper = new RapidFloatingActionHelper(
                context,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            Intent intent = new Intent(this, sendhelp_map.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            Intent intent = new Intent(this, sendhelp_map.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    protected void editorInfo() {
        //click on edit username编辑用户名
        RelativeLayout myLay = (RelativeLayout) findViewById(R.id.single_name);
        myLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditnameDialog = new AlertDialog.Builder(homepageActivity.this).create();
                EditnameDialog.show();
                EditnameDialog.getWindow().setContentView(R.layout.edit_user_name);
                //click on cancel点击取消
                EditnameDialog.getWindow().findViewById(R.id.edit_username5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditnameDialog.dismiss();
                            }
                        });
                //click ensure点击确定
                EditnameDialog.getWindow().findViewById(R.id.edit_username6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                      .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);
                                Toast.makeText(getApplicationContext(), "用户名设置成功",
                                        Toast.LENGTH_SHORT).show();
                                EditnameDialog.dismiss();
                            }
                        });
                //使edittext能输入东西
                EditnameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditnameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
        //click on edit nickname编辑昵称
        RelativeLayout click_nickname = (RelativeLayout) findViewById(R.id.nickname);
        click_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditnicknameDialog = new AlertDialog.Builder(homepageActivity.this).create();
                EditnicknameDialog.show();
                EditnicknameDialog.getWindow().setContentView(R.layout.edit_nickname);
                //click on cancel点击取消
                EditnicknameDialog.getWindow().findViewById(R.id.edit_nickname5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditnicknameDialog.dismiss();
                            }
                        });
                //click ensure点击确定
                EditnicknameDialog.getWindow().findViewById(R.id.edit_nickname6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //修改用户资料里面的用户名
                                EditText Get_edittext = (EditText) EditnicknameDialog.getWindow()
                                        .findViewById(R.id.edit_nickname4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"nickname\":\"" + emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Nickname.setText(emp);
                                Toast.makeText(getApplicationContext(), "用户昵称修改成功",
                                        Toast.LENGTH_SHORT).show();
                                EditnicknameDialog.dismiss();
                            }
                        });
                //使edittext能输入
                EditnicknameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditnicknameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
        //编辑所在地名
        RelativeLayout location_choose = (RelativeLayout)findViewById(R.id.single_loacl);
        location_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditlocationDialog = new AlertDialog.Builder(homepageActivity.this).create();
                EditlocationDialog.show();
                EditlocationDialog.getWindow().setContentView(R.layout.edit_user_location);
                //click on cancel点击取消
                EditlocationDialog.getWindow().findViewById(R.id.edit_location5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditlocationDialog.dismiss();
                            }
                        });
                //click ensure点击确定
                EditlocationDialog.getWindow().findViewById(R.id.edit_location6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText Get_edittext = (EditText)EditlocationDialog.getWindow()
                                        .findViewById(R.id.edit_location4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"location\":\"" + emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Location.setText(emp);
                                Toast.makeText(getApplicationContext(), "所在地设置成功",
                                        Toast.LENGTH_SHORT).show();
                                EditlocationDialog.dismiss();
                            }
                        });
                EditlocationDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditlocationDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
        //选择性别
        RelativeLayout Sex_choose = (RelativeLayout)findViewById(R.id.single_file);
        Sex_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[]{"女", "男"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"gender\":" + which + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                if (which == 1) {
                                    Gender.setText(R.string.mes_user1);
                                } else {
                                    Gender.setText(R.string.mes_user2);
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //click age 编辑年龄
        RelativeLayout click_age = (RelativeLayout)findViewById(R.id.single_age);
        click_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditageDialog = new AlertDialog.Builder(homepageActivity.this).create();
                EditageDialog.show();
                EditageDialog.getWindow().setContentView(R.layout.edit_age);
                //click on cancel点击取消
                EditageDialog.getWindow().findViewById(R.id.edit_age5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditageDialog.dismiss();
                            }
                        });
                //click ensure点击确定
                EditageDialog.getWindow().findViewById(R.id.edit_age6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText Get_edittext = (EditText) EditageDialog.getWindow()
                                        .findViewById(R.id.edit_age4);
                                //获取输入的字符串,通过user_id来修改信息
                                int emp1 = Integer.parseInt( Get_edittext.getText().toString());
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"age\":" + emp1 + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Age.setText(String.valueOf(emp1));
                                Toast.makeText(getApplicationContext(), "用户年龄修改成功",
                                        Toast.LENGTH_SHORT).show();
                                EditageDialog.dismiss();
                            }
                        });
                EditageDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
        //选择职业
        RelativeLayout Job_choose = (RelativeLayout)findViewById(R.id.single_job);
        Job_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[]{"学生", "教师", "工人", "警察", "消防员", "其他"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"occupation\":" + which + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                switch (which) {
                                    case 0:
                                        Job.setText(R.string.mes_job1);
                                        break;
                                    case 1:
                                        Job.setText(R.string.mes_job2);
                                        break;
                                    case 2:
                                        Job.setText(R.string.mes_job3);
                                        break;
                                    case 3:
                                        Job.setText(R.string.mes_job4);
                                        break;
                                    case 4:
                                        Job.setText(R.string.mes_job5);
                                        break;
                                    case 5:
                                        Job.setText(R.string.mes_job6);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

}
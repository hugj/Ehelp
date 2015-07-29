package com.ehelp.user.pinyin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendHelp;
import com.ehelp.send.SendQuestion;
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
    private AlertDialog EditlocationDialog = null;
    private final static int DATE_DIALOG = 0;
    private TextView edt = null;
    private RelativeLayout brithday_edit = null;
    private Calendar c = null;

    //TOOLbar
    private Toolbar mToolbar;
    private TextView Set_name;
    private TextView Set_local;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init() {
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("个人信息");

        Set_name = (TextView)findViewById(R.id.single_name2);
        Set_local = (TextView)findViewById(R.id.single_loacl2);
        editorInfo();

        //set FAB
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
            Intent intent = new Intent(this, SendHelp.class);
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
            Intent intent = new Intent(this, SendHelp.class);
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
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                      .findViewById(R.id.edit_username4);
                                Set_name.setText(Get_edittext.getText().toString());
                                Toast.makeText(getApplicationContext(), "用户名设置成功",
                                        Toast.LENGTH_SHORT).show();
                                EditnameDialog.dismiss();
                            }
                        });
                EditnameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditnameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
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
                                Set_local.setText(Get_edittext.getText().toString());
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
        final TextView Gender_choose = (TextView)findViewById(R.id.single_file2);
        Sex_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[]{"男", "女"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    Gender_choose.setText(R.string.mes_user2);
                                } else {
                                    Gender_choose.setText(R.string.mes_user1);
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //编辑生日
        brithday_edit = (RelativeLayout)findViewById(R.id.single_birth);
        edt = (TextView)findViewById(R.id.single_birth2);
        brithday_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
    }
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DATE_DIALOG:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                                edt.setText(year + "-" + (month+1) + "-" + dayOfMonth );
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
        }
        return dialog;
    }

}
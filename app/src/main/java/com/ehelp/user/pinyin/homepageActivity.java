package com.ehelp.user.pinyin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;

import java.util.Calendar;

public class homepageActivity extends Activity {

    private AlertDialog EditnameDialog = null;
    private AlertDialog EditlocationDialog = null;
    //private AlertDialog EditbrithdayDialog = null;
    private final static int DATE_DIALOG = 0;
    private TextView edt = null;
    private RelativeLayout brithday_edit = null;
    private Calendar c = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //click on edit username编辑用户名
        RelativeLayout myLay = (RelativeLayout) findViewById(R.id.single_name);
        myLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditnameDialog = new AlertDialog.Builder(homepageActivity.this).create();
                EditnameDialog.show();
                EditnameDialog.getWindow().setContentView(R.layout.edit_user_name);
                //click on cancel
                EditnameDialog.getWindow().findViewById(R.id.edit_username5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditnameDialog.dismiss();
                            }
                        });
                //click ensure
                EditnameDialog.getWindow().findViewById(R.id.edit_username6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "set name sucessfully",
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
        //选择性别
        RelativeLayout Sex_choose = (RelativeLayout)findViewById(R.id.single_file);
        Sex_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[]{"男", "女"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
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
                //click on cancel
                EditlocationDialog.getWindow().findViewById(R.id.edit_location5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditlocationDialog.dismiss();
                            }
                        });
                //click ensure
                EditlocationDialog.getWindow().findViewById(R.id.edit_location6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "set name sucessfully",
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
package com.ehelp.user.pinyin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;

import java.util.Calendar;

public class Health extends ActionBarActivity {

    private AlertDialog EditnameDialog = null;
    private AlertDialog EditlocationDialog = null;
    //private AlertDialog EditbrithdayDialog = null;
    private final static int DATE_DIALOG = 0;
    private TextView edt = null;
    private RelativeLayout brithday_edit = null;
    private Calendar c = null;

    //TOOLbar
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("健康卡");
        //click on edit
        RelativeLayout myLay = (RelativeLayout) findViewById(R.id.allergy);
        myLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditnameDialog = new AlertDialog.Builder(Health.this).create();
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

    }

}
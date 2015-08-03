package com.ehelp.user.pinyin;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import java.util.Calendar;

public class Health extends ActionBarActivity {

    private AlertDialog EditanaphylaxisDialog = null;
    private AlertDialog EditmedicineDialog = null;
    private AlertDialog EditbloodtypeDialog = null;
    private AlertDialog EditillnessDialog = null;
    private AlertDialog EditheightDialog = null;
    private AlertDialog EditweightDialog = null;

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

        init();
    }

    private void init() {
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("健康卡");

        editorInfo();
    }

    protected void editorInfo() {
        //click on edit
        RelativeLayout edit_allergy = (RelativeLayout) findViewById(R.id.allergy);
        edit_allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditanaphylaxisDialog = new AlertDialog.Builder(Health.this).create();
                EditanaphylaxisDialog.show();
                EditanaphylaxisDialog.getWindow().setContentView(R.layout.edit_healthcard_anaphylaxis);
                //click on cancel
                EditanaphylaxisDialog.getWindow().findViewById(R.id.edit_healthcard_anaphylaxis5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditanaphylaxisDialog.dismiss();
                            }
                        });
                //click ensure
                EditanaphylaxisDialog.getWindow().findViewById(R.id.edit_healthcard_anaphylaxis6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改过敏反应成功",
                                        Toast.LENGTH_SHORT).show();
                                EditanaphylaxisDialog.dismiss();
                            }
                        });
                EditanaphylaxisDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditanaphylaxisDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });

        RelativeLayout edit_medicine = (RelativeLayout) findViewById(R.id.medicine_taken);
        edit_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditmedicineDialog = new AlertDialog.Builder(Health.this).create();
                EditmedicineDialog.show();
                EditmedicineDialog.getWindow().setContentView(R.layout.edit_healthcard_medicine);
                //click on cancel
                EditmedicineDialog.getWindow().findViewById(R.id.edit_healthcard_medicine5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditmedicineDialog.dismiss();
                            }
                        });
                //click ensure
                EditmedicineDialog.getWindow().findViewById(R.id.edit_healthcard_medicine6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改药物使用成功",
                                        Toast.LENGTH_SHORT).show();
                                EditmedicineDialog.dismiss();
                            }
                        });
                EditmedicineDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditmedicineDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });

        RelativeLayout edit_bloodtype = (RelativeLayout) findViewById(R.id.blood_type);
        edit_bloodtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditbloodtypeDialog = new AlertDialog.Builder(Health.this).create();
                EditbloodtypeDialog.show();
                EditbloodtypeDialog.getWindow().setContentView(R.layout.edit_healthcard_bloodtype);
                //click on cancel
                EditbloodtypeDialog.getWindow().findViewById(R.id.edit_healthcard_bloodtype5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditbloodtypeDialog.dismiss();
                            }
                        });
                //click ensure
                EditbloodtypeDialog.getWindow().findViewById(R.id.edit_healthcard_bloodtype6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改血型成功",
                                        Toast.LENGTH_SHORT).show();
                                EditbloodtypeDialog.dismiss();
                            }
                        });
                EditbloodtypeDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditbloodtypeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });

        RelativeLayout edit_illness = (RelativeLayout) findViewById(R.id.medihistory1);
        edit_illness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditillnessDialog = new AlertDialog.Builder(Health.this).create();
                EditillnessDialog.show();
                EditillnessDialog.getWindow().setContentView(R.layout.edit_healthcard_medihistory);
                //click on cancel
                EditillnessDialog.getWindow().findViewById(R.id.edit_healthcard_medihistory5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditillnessDialog.dismiss();
                            }
                        });
                //click ensure
                EditillnessDialog.getWindow().findViewById(R.id.edit_healthcard_medihistory6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改病史成功",
                                        Toast.LENGTH_SHORT).show();
                                EditillnessDialog.dismiss();
                            }
                        });
                EditillnessDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditillnessDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });

        RelativeLayout edit_height = (RelativeLayout) findViewById(R.id.height1);
        edit_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditheightDialog = new AlertDialog.Builder(Health.this).create();
                EditheightDialog.show();
                EditheightDialog.getWindow().setContentView(R.layout.edit_healthcard_height);
                //click on cancel
                EditheightDialog.getWindow().findViewById(R.id.edit_healthcard_height5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditheightDialog.dismiss();
                            }
                        });
                //click ensure
                EditheightDialog.getWindow().findViewById(R.id.edit_healthcard_height6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改身高成功",
                                        Toast.LENGTH_SHORT).show();
                                EditheightDialog.dismiss();
                            }
                        });
                EditheightDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditheightDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });

        RelativeLayout edit_weight = (RelativeLayout) findViewById(R.id.weight1);
        edit_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditweightDialog = new AlertDialog.Builder(Health.this).create();
                EditweightDialog.show();
                EditweightDialog.getWindow().setContentView(R.layout.edit_healthcard_weight);
                //click on cancel
                EditweightDialog.getWindow().findViewById(R.id.edit_healthcard_weight5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditweightDialog.dismiss();
                            }
                        });
                //click ensure
                EditweightDialog.getWindow().findViewById(R.id.edit_healthcard_weight6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*//修改用户资料里面的用户名
                                EditText Get_edittext = (EditText)EditnameDialog.getWindow()
                                        .findViewById(R.id.edit_username4);
                                //获取输入的字符串,通过user_id来修改信息
                                emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"name\":\"" +  emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Username.setText(emp);*/

                                Toast.makeText(getApplicationContext(), "修改体重成功",
                                        Toast.LENGTH_SHORT).show();
                                EditweightDialog.dismiss();
                            }
                        });
                EditweightDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditweightDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

}
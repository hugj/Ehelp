package com.ehelp.user.healthcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Health extends ActionBarActivity {
    private String TAG = "myowndeveloptest";
    //Dialog定义
    private AlertDialog EditanaphylaxisDialog = null;
    private AlertDialog EditmedicineDialog = null;
    private AlertDialog EditbloodtypeDialog = null;
    private AlertDialog EditillnessDialog = null;
    private AlertDialog EditheightDialog = null;
    private AlertDialog EditweightDialog = null;

    private TextView anaphylaxis;
    private TextView medicine_taken;
    private TextView bloodtype;
    private TextView medihistory;
    private TextView height;
    private TextView weight;

    private int user_id;
    private SharedPreferences SharedPref;

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

        anaphylaxis = (TextView)findViewById(R.id.allergy2);
        medicine_taken = (TextView)findViewById(R.id.medicine2);
        bloodtype = (TextView)findViewById(R.id.bloodType2);
        medihistory = (TextView)findViewById(R.id.mediHistory2);
        height = (TextView)findViewById(R.id.height2);
        weight = (TextView)findViewById(R.id.weight2);

        SharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = SharedPref.getInt("user_id", -1);
        String jsonString = "{" +
                "\"id\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/health/query", jsonString);
        if (message.equals("false")) {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject j = new JSONObject(message);
                if (j.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(), "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (j.getJSONObject("health_list") == null) {
                        //显示过敏反应
                        anaphylaxis.setText("无");
                        //显示药物使用
                        medicine_taken.setText("无");
                        //显示血型
                        bloodtype.setText("无");
                        //显示病史
                        medihistory.setText("无");
                        //显示身高
                        height.setText("无");
                        //显示体重
                        weight.setText("无");
                    } else {
                        JSONObject jo = j.getJSONObject("health_list");
                        //显示过敏反应
                        anaphylaxis.setText(jo.getString("anaphylaxis"));
                        //显示药物使用
                        medicine_taken.setText(jo.getString("medicine_taken"));
                        //显示血型
                        bloodtype.setText(jo.getString("blood_type"));
                        //显示病史
                        medihistory.setText(jo.getString("medical_history"));
                        //显示身高
                        height.setText(String.valueOf(jo.getInt("height")));
                        //显示体重
                        weight.setText(String.valueOf(jo.getInt("weight")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
                                //修改过敏反应
                                EditText Get_edittext = (EditText)EditanaphylaxisDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_anaphylaxis4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"anaphylaxis\":\"" +  emp + "\"" + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return anaphylaxis" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    anaphylaxis.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改过敏反应成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
                                //修改药物使用
                                EditText Get_edittext = (EditText)EditmedicineDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_medicine4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"medicine_taken\":\"" +  emp + "\"" + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return medicine_taken" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    medicine_taken.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改药物使用成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
                                //修改血型
                                EditText Get_edittext = (EditText)EditbloodtypeDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_bloodtype4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"blood_type\":\"" +  emp + "\"" + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return bloodtype" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    bloodtype.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改血型成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
                                //修改病史
                                EditText Get_edittext = (EditText)EditillnessDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_medihistory4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"medical_history\":\"" +  emp + "\"" + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return medihistory" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    medihistory.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改病史成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
                                //修改身高
                                EditText Get_edittext = (EditText)EditheightDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_height4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"height\":" +  emp + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return height" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    height.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改身高成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
                                //修改体重
                                EditText Get_edittext = (EditText)EditweightDialog.getWindow()
                                        .findViewById(R.id.edit_healthcard_weight4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"weight\":" +  emp + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/health/upload", jsonString);
                                Log.v(TAG, "return weight" + message);
                                if (message.equals("false")) {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    weight.setText(emp);
                                    Toast.makeText(getApplicationContext(), "修改体重成功",
                                            Toast.LENGTH_SHORT).show();
                                }
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
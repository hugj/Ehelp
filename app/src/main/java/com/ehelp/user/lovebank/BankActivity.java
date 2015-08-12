package com.ehelp.user.lovebank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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
import java.util.List;

@AILayout(R.layout.activity_bank)
public class BankActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private TextView name, phone, integral, coin;
    private ImageView level;
    private int id, integral_, coin_;
    private String jsonString, message, name_, phone_;
    private SharedPreferences sharedPref;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();//fab
        setmes();
    }
    public void setmes(){
        //爱心银行首页的信息
        name = (TextView)findViewById(R.id.bank_name);
        phone = (TextView)findViewById(R.id.bank_phone);
        integral = (TextView)findViewById(R.id.bank_integral1);
        coin = (TextView)findViewById(R.id.bank_coin1);
        level = (ImageView)findViewById(R.id.bank_level);
        //后台线程
        new Thread(runnable).start();
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setmes_help();
        }
    };
    //获取用户爱心银行信息
    private void setmes_help() {
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);
        jsonString = "{" +
                "\"id\":" + id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonString);
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
        try {
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取用户信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            name_ = jO.getString("name");
            phone_ = jO.getString("phone");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        name.setText(name_);
                        phone.setText(phone_);
                }
            });
            jsonString = "{" +
                    "\"user_id\":" + id + "}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/lovingbank", jsonString);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取爱心银行信息失败，请检查网络是否连接并重试",
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
                        Toast.makeText(getApplicationContext(), "获取爱心银行信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                integral_ = jO.getInt("score");
                coin_ = jO.getInt("love_coin");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            coin.setText(String.valueOf(coin_));
                            integral.setText(String.valueOf(integral_));
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void my_integral(View view){
        Intent intent = new Intent(this, IntegralActivity.class);
        String str1 = integral.getText().toString();
        intent.putExtra("integral", str1);
        startActivity(intent);
    }
    public void my_coin(View view){
        Intent intent = new Intent(this, CoinActivity.class);
        String  i = coin.getText().toString();
        intent.putExtra("coin", i);
        startActivity(intent);
    }
    private  void init() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("  ");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("爱心银行");

        //下面的圆型按钮
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
}

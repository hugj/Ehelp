package com.ehelp.user.lovebank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

@AILayout(R.layout.activity_transfer)
public class TransferActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private int receiver_id, sender_id, num, sender_coin;
    private String receiver_name;
    private Button btn;
    private TextView user_name;
    private EditText edit_num;
    private ImageView imag;
    private String message, jsonStrng;
    //用于获取当前登录id
    private SharedPreferences SharedPref;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        receiver_id = intent.getIntExtra("id", -1);
        receiver_name = intent.getStringExtra("name"); //传入用户id和name
        sender_coin = intent.getIntExtra("coin", 0); //传入的爱心币数
        user_name = (TextView)findViewById(R.id.transfer_name);
        user_name.setText(receiver_name);//设置用户名
        imag = (ImageView)findViewById(R.id.transfer_icon);//设置头像
        init();
    }
    public void transfer_btn(View view) { //点击兑换按钮
        btn = (Button)findViewById(R.id.transfer_btn1);
        edit_num = (EditText)findViewById(R.id.transfer_edit);
        //获取当前登录用户id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        sender_id = SharedPref.getInt("user_id", -1);
        num = Integer.parseInt(edit_num.getText().toString()); //获取输入数字
        if (sender_id == receiver_id) { //用户不能给自己转账
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "不能给自己转账，请重新选择",
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        } else if (edit_num.getText().toString() == "") { //输入为空时
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "输入数量不能为空，请重新输入",
                             Toast.LENGTH_LONG).show();
                }
            });
            return;
        } else if(num > sender_coin) {  //转账数额大余额
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "输入数量多于您的余额，请重新输入",
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        } else {
            jsonStrng = "{" + "\"sender\":" + sender_id + "," +
                    "\"receiver\":" + receiver_id + "," +
                    "\"love_coin\":" + num + "}";
            message =  RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/bank_transfer", jsonStrng);
            if(message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
            try {
                JSONObject j = new JSONObject(message);
                if (j.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "转账失败，请重试",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                } else {
                    Toast.makeText(getApplicationContext(),
                            "转账成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, CoinActivity.class);
                    String s = String.valueOf(sender_coin - num);
                    intent.putExtra("coin", s);
                    startActivity(intent);
                    TransferActivity.this.finish();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
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
        tvv.setText("转账");

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

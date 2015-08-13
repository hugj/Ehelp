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
import android.widget.LinearLayout;
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

/*
 *爱心银行积分管理-兑换爱心币
 */
@AILayout(R.layout.activity_integral)
public class IntegralActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private TextView integral_num, num_;
    private Button btn;
    private EditText coin;
    private String str;
    private int n, edit_n, inte_n;//兑换数量,积分数
    private String message, jsonStrng;
    private String edit_string;
    private int id;
    private Toolbar mToolbar;
    //用于获取当前登录id
    private SharedPreferences SharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        str = intent.getStringExtra("integral");
        integral_num = (TextView)findViewById(R.id.integral_num);
        integral_num.setText(str); //积分数
        //获取当前登录用户id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        id = SharedPref.getInt("user_id", -1);
        init();
    }
    //点击显示兑换输入框
   public void change_integral(View view) {
       LinearLayout change_inte = (LinearLayout)findViewById(R.id.change1);
       change_inte.setVisibility(View.VISIBLE);
       num_ = (TextView)findViewById(R.id.num);
       //可兑换爱心币的数量
       n = (Integer.parseInt(str)) / 100;
       String s = String.valueOf(n);
       num_.setText(s);
   }
    //兑换爱心币
    public void change_coin(View view) {
        coin = (EditText) findViewById(R.id.change3);
        edit_string = coin.getText().toString();
        if (edit_string == "") {
            Toast.makeText(getApplicationContext(), "输入数量不能为空，请重新输入"
                    , Toast.LENGTH_LONG).show();
            return;
        } else {
            edit_n = Integer.parseInt(edit_string);//获取输入爱心币个数
            btn = (Button) findViewById(R.id.btn);
            inte_n = edit_n * 100; //兑换所需的积分数
            if (edit_n > n) {
                Toast.makeText(getApplicationContext(), "输入数量大于可兑换数量，请重新输入"
                        , Toast.LENGTH_LONG).show();
            } else {
                jsonStrng = "{" +
                        "\"id\":" + id + "," +
                        "\"operation\":" + 2 + "," +
                        "\"score\":" + inte_n + "," +
                        "\"love_coin\":" + 0 + "}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/user/bank_manage", jsonStrng); //修改数据
                if (message == "false") {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "连接失败，请检查网络是否连接并重试", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(),
                                        "兑换失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "兑换成功", Toast.LENGTH_SHORT).show();
//                        String s = Integer.toString(j.getInt("score"));
//                        integral_num.setText(s);//更改兑换后的积分数
//                        String emp = String.valueOf(n - edit_n);
//                        num_.setText(emp);
                        Intent intent = new Intent(this, BankActivity.class);
                        startActivity(intent);
                        IntegralActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        tvv.setText("积分管理");

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

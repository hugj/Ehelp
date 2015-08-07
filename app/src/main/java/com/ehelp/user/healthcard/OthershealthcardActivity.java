package com.ehelp.user.healthcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

@AILayout(R.layout.activity_othershealthcard)
public class OthershealthcardActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private SharedPreferences SharedPref;
    private TextView allergy, medicine, bloodType, mediHistory, height, weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set FAB
        fab();
        int id = getIntent().getIntExtra("user_id", -1);
        set_healthcard(id);
    }
    private void set_healthcard(int id) {
        allergy = (TextView)findViewById(R.id.others_allergy2);
        medicine = (TextView)findViewById(R.id.others_medicine2);
        bloodType = (TextView)findViewById(R.id.others_bloodType2);
        mediHistory = (TextView)findViewById(R.id.others_mediHistory2);
        height = (TextView)findViewById(R.id.others_height2);
        weight = (TextView)findViewById(R.id.others_weight2);

        SharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        String jsonString = "{" +
                "\"id\":" + id + "}";
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
                        allergy.setText("无");
                        //显示药物使用
                        medicine.setText("无");
                        //显示血型
                        bloodType.setText("无");
                        //显示病史
                        mediHistory.setText("无");
                        //显示身高
                        height.setText("无");
                        //显示体重
                        weight.setText("无");
                    } else {
                        JSONObject jo = j.getJSONObject("health_list");
                        //显示过敏反应
                        allergy.setText(jo.getString("anaphylaxis"));
                        //显示药物使用
                        medicine.setText(jo.getString("medicine_taken"));
                        //显示血型
                        bloodType.setText(jo.getString("blood_type"));
                        //显示病史
                        mediHistory.setText(jo.getString("medical_history"));
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

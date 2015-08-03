package com.ehelp.user.contactlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

@AILayout(R.layout.activity_excontactlist)
public class ExcontactlistActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private Toolbar mToolbar;

    private PinyinAdapter adapter;
    private ExpandableListView eListView;
    private AssortView assortView;
    private List<String> names;
    private List<User> userList;
    private SharedPreferences sharedPref;
    private String jsonStrng, message;
    private int id;
    Gson gson = new Gson();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        DrawContactlist();
    }
    //好友列
    public void DrawContactlist() {
        eListView = (ExpandableListView) findViewById(R.id.elist1);
        assortView = (AssortView) findViewById(R.id.assort1);
        names=new ArrayList<String>();
        //获取user id
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);
        //获取紧急联系人
        jsonStrng = "{" +
                "\"id\":" + id + "," +
                "\"operation\":" + 2 + "," +
                "\"type\":" + 0 +"}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                Toast.makeText(getApplicationContext(), "没有紧急联系人",
                        Toast.LENGTH_SHORT).show();
            }else {
                String user_list = jO.getString("user_list");
                userList = gson.fromJson(user_list, new TypeToken<List<User>>(){}.getType());
                for (int i = 0; i < userList.size(); i++) {
                    String emp;
                    emp = userList.get(i).getNickname();
                    names.add(emp);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //紧急联系人列表的信息添加
        adapter = new PinyinAdapter(this,names);
        eListView.setAdapter(adapter);
        //展开紧急联系人
        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
            eListView.expandGroup(i);
        }
        assortView.setOnTouchAssortListener(new AssortView.OnTouchAssortListener() {
            View layoutView = LayoutInflater.from(ExcontactlistActivity.this)
                    .inflate(R.layout.alert_dialog_menu_layout, null);
            TextView text = (TextView) layoutView.findViewById(R.id.content);
            PopupWindow popupWindow;

            public void onTouchAssortListener(String str) {
                int index = adapter.getAssort().getHashList().indexOfKey(str);
                if (index != -1) {
                    eListView.setSelectedGroup(index);
                }
                if (popupWindow != null) {
                    text.setText(str);
                } else {
                    popupWindow = new PopupWindow(layoutView,
                            120, 120, false);
                    popupWindow.showAtLocation(getWindow().getDecorView(),
                            Gravity.CENTER, 0, 0);
                }
                text.setText(str);
            }
            public void onTouchAssortUP() {
                if (popupWindow != null)
                    popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }
    private void init() {
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
        tvv.setText("紧急联系人列表");

        //FAB
        fab();
    }
    //下面的按钮
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


    //点击跳转到联系人的详细信息上
    public void getMessagePage(View view) {
        Intent intent = new Intent(this, messageActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    //toolbar设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contactlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //添加好友。跳转至添加好友页面
            Intent intent = new Intent(this, AddFriendActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //toolbar设置结束
}

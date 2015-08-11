package com.ehelp.user.usermes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.user.contactlist.Child;
import com.ehelp.user.contactlist.ConstactAdapter;
import com.ehelp.user.contactlist.Group;
import com.ehelp.user.contactlist.IphoneTreeView;
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

@AILayout(R.layout.activity_fans_list)
public class FansListActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private IphoneTreeView mIphoneTreeView;
    private ConstactAdapter mExpAdapter;
    private List<Group> listGroup;
    private List<User> fanslist;
    private String message;
    private int id;
    private String Fans_list;
    private Gson gson = new Gson();
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_list);
        initView();
        init();//fab
        Drawfans(); //粉丝列表
    }
    public void Drawfans(){
        listGroup=new ArrayList<Group>();
        //获取本地的user_id通过其来获得用户信息
        SharedPreferences sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);
        //后台线程
        String jsonString = "{" +
                "\"id\":" + id +
                ",\"operation\":2" +
                ",\"state\":1" + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonString);
        //粉丝列表
        Group group=new Group();
        List<Child> listChild=new ArrayList<Child>();
        group.setGroupName("我的粉丝");
        if (message == "false") {
            Toast.makeText(getApplication(),"连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }
        try {
            JSONObject j = new JSONObject(message);
            if (j.getInt("status") == 500){
                        Toast.makeText(getApplication(), "连接失败",
                                Toast.LENGTH_LONG).show();
            } else {
                Fans_list = j.getString("user_list");
                fanslist = gson.fromJson(Fans_list, new TypeToken<List<User>>() {
                        }.getType());
                Child child = new Child();
                for (int i = 0; i < fanslist.size(); i++) {
                    String emp = fanslist.get(i).getNickname();
                    String emp_ = fanslist.get(i).getName();
                    if (emp == "") {
                        child.setUsername(emp_);
                    } else {
                        child.setUsername(emp);
                    }
                    String phone = fanslist.get(i).getPhone();
                    child.setPhone(phone);
                    child.setOnline_status("1");//1在线，0不在线
                    listChild.add(child);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        group.setChildList(listChild);
        listGroup.add(group);
        mExpAdapter = new ConstactAdapter(this, listGroup, mIphoneTreeView);
        mIphoneTreeView.setAdapter(mExpAdapter);
    }
    private void initView() {
        //画出通讯录
        mIphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
        mIphoneTreeView.setHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.fragment_constact_head_view, mIphoneTreeView, false));
        mIphoneTreeView.setGroupIndicator(null);
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
        tvv.setText("我的粉丝");

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

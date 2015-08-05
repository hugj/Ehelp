package com.ehelp.user.history;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.user.contactlist.Child;
import com.ehelp.user.contactlist.ConstactAdapter;
import com.ehelp.user.contactlist.Group;
import com.ehelp.user.contactlist.IphoneTreeView;
import com.ehelp.user.contactlist.messageActivity;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class EndSosActivity extends ActionBarActivity {

    private IphoneTreeView mIphoneTreeView;
    private ConstactAdapter mExpAdapter;
    private List<Group> listGroup;
    private List<User> userList;
    private SharedPreferences sharedPref;
    private int id;
    Gson gson = new Gson();

    //TOOLbar
    private Toolbar mToolbar;

    //上一个页面传入的参数
    private int event_id;//该事件ID

    //与后台联系的变量
    private String message;
    private String jsonStrng;

    private int idd;//求救发起者ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_sos);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //toolbar
        toolbar();
        //跟后台交互获取详情
        Intent intent = getIntent();
        event_id = intent.getIntExtra("event_id",-1);//intent时传入的事件id
        //event_id =292;
        showdetail();
        initView();
        listGroup=new ArrayList<Group>();
        showhelper(2);
        showhelper(1);
        mExpAdapter = new ConstactAdapter(this, listGroup, mIphoneTreeView);
        mIphoneTreeView.setAdapter(mExpAdapter);
        click_on_message();

    }
    /*
    toolbar设置
     */
    private void toolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("求救信息详情");
    }
    /*
    显示事件详情
     */
    private void showdetail(){
        jsonStrng = "{" +
                "\"event_id\":" + event_id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/get_information", jsonStrng);
        if (message == "false") {

                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
        }
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                        Toast.makeText(getApplicationContext(), "无此事件",
                                Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "查询成功",
                        Toast.LENGTH_SHORT).show();
                //修改显示的时间
                TextView tv1 =(TextView) findViewById(R.id.SOStime);
                tv1.setText(jO.getString("time"));
                //健康问题or安全问题
                TextView tv3 =(TextView) findViewById(R.id.problem);
                if(jO.getInt("demand_number") == 1){
                    tv3.setText("健康问题");
                }
                if(jO.getInt("demand_number") == 2){
                    tv3.setText("安全问题");
                }

                //通过发起者id寻找发起者用户名并显示
                idd = jO.getInt("launcher_id");
                findforusername();
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void findforusername(){
        if( idd != -1){
            jsonStrng = "{" +
                    "\"id\":" + idd + "}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/get_information", jsonStrng);
            if (message == "false") {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
            }
            try{
                JSONObject jO1 = new JSONObject(message);
                if (jO1.getInt("status") == 500) {
                            Toast.makeText(getApplicationContext(), "查询用户名错误",
                                    Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "查询用户名成功",
                            Toast.LENGTH_SHORT).show();
                    //修改显示的用户名
                    TextView tv3 =(TextView) findViewById(R.id.SOSusername);
                    if(jO1.getString("nickname") !="") {
                        tv3.setText(jO1.getString("nickname"));
                    }else if(jO1.getString("name")!=""){
                        tv3.setText(jO1.getString("name"));
                    }else{
                        tv3.setText(jO1.getString("phone"));
                    }
                    return;
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "无此用户",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /*
    显示帮客和关注者
     */
    private void showhelper(int ty){
                    if (event_id != -1) {
                        jsonStrng = "{" +
                                "\"event_id\":" + event_id + ",\"type\":" + ty + "}";
                        message = RequestHandler.sendPostRequest(
                                "http://120.24.208.130:1501/event/get_supporter", jsonStrng);
                        Group group = new Group();
                        List<Child> listChild = new ArrayList<Child>();
                        if (ty == 1) {
                            group.setGroupName("关注的人");
                        }
                        if (ty == 2) {
                            group.setGroupName("回应的人");
                        }
                        if (message == "false") {
                                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                            Toast.LENGTH_SHORT).show();
                        }
                        try {
                            JSONObject jO = new JSONObject(message);
                            if (jO.getInt("status") == 500) {
                                Toast.makeText(getApplicationContext(), "查询不到用户",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String user_list = jO.getString("user_list");
                                userList = gson.fromJson(user_list, new TypeToken<List<User>>() {
                                }.getType());
                                for (int i = 0; i < userList.size(); i++) {
                                    Child child = new Child();
                                    String emp, emp_, phone;
                                    emp = userList.get(i).getNickname();
                                    emp_ = userList.get(i).getName();
                                    //假如昵称为空则设置为用户名
                                    if (emp == "") {
                                        child.setUsername(emp_);
                                    } else {
                                        child.setUsername(emp);
                                    }
                                    phone = userList.get(i).getPhone();
                                    child.setHeadphoto("http://d.hiphotos.baidu.com/zhidao/pic/item/562c11dfa9ec8a13e028c4c0f603918fa0ecc0e4.jpg");
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
                    } else {
                        Toast.makeText(getApplicationContext(), "无此事件",
                                Toast.LENGTH_SHORT).show();
                    }
                }

    private void initView() {
        mIphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
        mIphoneTreeView.setHeaderView(LayoutInflater.from(this)
                .inflate(R.layout.fragment_constact_head_view, mIphoneTreeView, false));
        mIphoneTreeView.setGroupIndicator(null);
    }
    /*
    点击帮客进入帮客详情页
     */
    private void click_on_message() {
        mIphoneTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4) {
				/*
				 *arg0发生点击动作的ExpandableListView,arg1视图view，arg2 group在ExpandableListView
				 *的位置索引 arg3 child在group的位置，arg4child的行id
				 */
                String Phone = mExpAdapter.getChild(arg2, arg3).getPhone();
                jsonStrng = "{" +
                        "\"phone\":\"" + Phone + "\"}";
                message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/user/get_information", jsonStrng);
                if (message == "false") {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONObject jO = new JSONObject(message);
                    if (jO.getInt("status") == 500) {
                        Toast.makeText(getApplicationContext(), "没有此用户",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        int id = jO.getInt("id");
                        Intent intent = new Intent(EndSosActivity.this, messageActivity.class);
                        intent.putExtra("type", 0);//默认非好友
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//				Toast.makeText(getApplicationContext(), "点击"+arg2, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}

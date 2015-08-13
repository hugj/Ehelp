package com.ehelp.map;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class RespondPeopleActivity extends ActionBarActivity {

    private IphoneTreeView mIphoneTreeView;
    private ConstactAdapter mExpAdapter;
    private List<Group> listGroup;
    private List<User> userList;
    Gson gson = new Gson();

    //TOOLbar
    private Toolbar mToolbar;

    //上一个页面传入的参数
    private int event_id;//该事件ID

    //与后台联系的变量
    private String message;
    private String jsonStrng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_people);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //toolbar
        toolbar();
        //跟后台交互获取详情
        Intent intent = getIntent();
        event_id = intent.getIntExtra("event_id",-1);//intent时传入的事件id

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
        tvv.setText("帮客");
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
                return;
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

                } else {
                    try {
                        JSONObject jO = new JSONObject(message);
                        if (jO.getInt("status") == 500) {
                            Toast.makeText(getApplicationContext(), "没有此用户",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            int id = jO.getInt("id");
                            Intent intent = new Intent(RespondPeopleActivity.this, messageActivity.class);
                            intent.putExtra("type", 0);//默认非好友
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//				Toast.makeText(getApplicationContext(), "点击"+arg2, Toast.LENGTH_SHORT).show();
                return true;
            }

        });
    }
}
